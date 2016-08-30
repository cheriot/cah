const chai = require('chai'),
  expect = chai.expect,
  chaiHttp = require('chai-http'),
  firebaseMock = require('./firebase_mock'),
  _ = require('lodash');

chai.use(chaiHttp);

/*
 * Expect helpers.
 */

function expect200(res) {
  expect(res).to.have.status(200);
  return res;
}

function expectNoCache(res) {
  expect(res.headers['cache-control'])
    .to.equal('no-store, no-cache, must-revalidate, proxy-revalidate');
  expect(res.headers['pragma']).to.equal('no-cache');
  expect(res.headers['expires']).to.equal('0');
  return res;
}

function expectJson(obj) {
  return function(res) {
    expect(res.charset).to.equal('utf-8');
    expect(res.type).to.equal('application/json');
    if(obj) expect(res.body).to.deep.equal(obj);
    return res;
  }
}

function error(err) {
  console.log('Error is', err);
  throw err;
}

/*
 * Tests
 */

describe('http resources', function() {

  var app, uid;
  before(() => {
    const fb = require('../models/firebase');
    firebaseMock.installMockClient(fb);
    fb.setAuth({
      verifyIdToken: function(token) {
        // If the test set an empty uid, reject.
        if(_.isEmpty(uid)) return Promise.reject(new Error('Fake rejection.'));
        // If the test set a uid, resolve.
        else return Promise.resolve({name: 'Fake Name', uid: uid});
      }
    });
    require('../models/auth').currentUser = function mockCurrentUser() {
      if(uid) {
        return {displayName: 'Fake Name', uid: uid};
      } else {
        throw new Error('Fake authentication required error.');
      }
    }
    app = require('../app');
  })

  // TODO extract admin tests into another file.
  describe('/admin/init', function() {
    it('initializes the database if empty.', function() {
      return chai.request(app)
        .post('/admin/init')
        .set('Authorization', 'fake-token')
        .then(expect200)
        .then(expectJson({message: 'Database initialized.'}));
    });
  });

  describe('/api/v1', function() {

    describe('/games', function() {

      it('creates a game', function() {
        this.timeout(5000); // Hitting a live firebase.
        uid = 'fake-user-id';
        return chai.request(app)
          .post('/api/v1/games')
          .set('Authorization', 'fake-token')
          .then(expect200)
          .then(expectNoCache)
          .then(expectJson())
          .then((res) => {
            expect(res.body.gameKey).to.be.a('String')
          });
      });

      it('requires authentication', function() {
        uid = null;
        return chai.request(app)
          .post('/api/v1/games')
          .send()
          .then(() => 'never called, but needed to get a promise')
          .catch((err) => {
            expect(err.status).to.eq(401);
            return err.response;
          })
          .then(expectJson({error: 'No credentials found.'}))
      });

      it('fails when the token cannot be verified.', function() {
        uid = null;
        return chai.request(app)
          .post('/api/v1/games')
          .set('Authorization', 'fake-token')
          .send()
          .then(() => 'never called, but needed to get a promise')
          .catch((err) => {
            expect(err.status).to.eq(401);
            return err.response;
          })
          .then(expectJson({
            error: 'Error authenticating: Error: Fake rejection.'
          }));
      });
    });

    it('/ is reachable', function() {
      uid = 'fake-user-id';
      return chai.request(app)
        .get('/api/v1/')
         .set('Authorization', 'fake-token')
        .then(expect200)
        .then(expectNoCache)
        .then(expectJson({message: "hooray! welcome to api v1!"}));
    });

    describe('/games/join', function() {
      it('requires authentication', function() {
        uid = null;
        return chai.request(app)
          .post('/api/v1/games')
          .send()
          .then(() => 'never called, but needed to get a promise')
          .catch((err) => {
            expect(err.status).to.eq(401);
            return err.response;
          })
          .then(expectJson({error: 'No credentials found.'}))
      });

      it('accepts the gameCode and responds with the gameKey', function() {
        this.timeout(5000); // Hitting a live firebase.

        // Create a game and then join it.
        uid = 'fake-user-id';
        var gameKey, gameCode;
        return chai.request(app)
          .post('/api/v1/games')
          .set('Authorization', 'fake-token')
          .then(expect200)
          .then((res) => {
            expect(res.body.gameKey).to.be.a('String')
            gameKey = res.body.gameKey;
            gameCode = res.body.gameCode;
          })
          .then(() => {
            uid = 'second-fake-user-id';
            return chai.request(app)
              .post('/api/v1/games/join')
              .set('Authorization', 'fake-token')
              .send({gameCode: gameCode});
          })
          .then(expect200)
          .then((res) => {
            expect(res.body.gameKey).to.eq(gameKey);
            expect(res.body.gameCode).to.eq(gameCode);
          });

      });
    });

  });
});
