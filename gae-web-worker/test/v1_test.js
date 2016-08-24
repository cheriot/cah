const chai = require('chai'),
  expect = chai.expect,
  chaiHttp = require('chai-http'),
  firebaseMock = require('./firebase_mock');

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

  var app;
  before(() => {
    firebaseMock.installMockClient(require('../models/firebase'));
    app = require('../app');
  })

  // TODO extract admin tests into another file.
  describe('/admin/init', function() {
    it('initializes the database if empty.', function() {
      return chai.request(app)
        .post('/admin/init')
        .then(expect200)
        .then(expectJson({message: 'Database initialized.'}));
    });
  });

  describe('/api/v1', function() {

    describe('/games', function() {
      // Handle shitty hostel wifi (uses real firebase).
      this.timeout(5000);

      it('creates a game', function() {
        const userId = 'user-id-foo';
        app = require('../app');
        return chai.request(app)
          .post('/api/v1/games')
          .send({userId: userId})
          .then(expect200)
          .then(expectNoCache)
          .then(expectJson())
          .then((res) => {
            expect(res.body.gameKey).to.be.a('String')
          });
      });

      it('requires a userId', function() {
        return chai.request(app)
          .post('/api/v1/games')
          .send()
          .then(() => 'never called, but needed to get a promise')
          .catch((err) => {
            expect(err.status).to.eq(401);
            return err.response;
          })
          .then(expectJson({error: 'Invalid userId.'}))
      });
    });

    it('/ is reachable', function() {
      return chai.request(app)
        .get('/api/v1/')
        .then(expect200)
        .then(expectNoCache)
        .then(expectJson({message: "hooray! welcome to api v1!"}));
    });

    it('/message', function() {
      // Use a real firebase connection until firebase-server supports recent clients.
      // firebaseMock.startServer({message: 'original message'});
      const m = "New Message Value " + new Date();
      return chai.request(app)
        .post('/api/v1/message')
        .send({message: m})
        .then(expect200)
        .then(expectNoCache)
        .then(() => chai.request(app).get('/api/v1/message') )
        .then(expect200)
        .then(expectNoCache)
        .then(expectJson({message: m}))
        // .then(firebaseMock.stopServer);
    });
  });
});
