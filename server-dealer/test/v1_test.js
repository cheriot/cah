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

describe('http resources', function() {

  var app, uid;
  before(function() {
    this.timeout(20000);

    // Override database root and authentication

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

    // Instantiate the server
    app = require('../app');

    // Load card data
    return require('../models/cards').loadCards();
  })

  /*
   * Request types
   */

  function createGame(playerUid) {
    uid = playerUid;
    return chai.request(app)
      .post('/api/v1/games')
      .set('Authorization', 'fake-token')
      .then(expect200)
      .then(expectNoCache)
      .then(expectJson())
      .then((res) => {
        expect(res.body.gameKey).to.be.a('String')
        return [res.body.gameKey, res.body.inviteCode];
      });
  }

  function joinGame(playerUid, gameKey, inviteCode) {
    uid = playerUid;
    return chai.request(app)
      .patch('/api/v1/games/join')
      .set('Authorization', 'fake-token')
      .send({inviteCode: inviteCode})
      .then(expect200)
      .then((res) => {
        expect(res.body.gameKey).to.eq(gameKey);
        expect(res.body.inviteCode).to.eq(inviteCode);
        return gameKey;
      });
  }

  function startGame(playerUid, gameKey) {
    uid = playerUid;
    return chai.request(app)
      .patch('/api/v1/games/'+gameKey+'/start')
      .set('Authorization', 'fake-token')
      .then(expect200)
      .then(expectJson({gameKey: gameKey, success: true}))
      .then(() => gameKey);
  }

  function submitCard(playerUid, gameKey, roundNumber, cardKey) {
    uid = playerUid;
    return chai.request(app)
      .patch('/api/v1/games/'+gameKey+'/rounds/'+roundNumber+'/submit/'+cardKey)
      .set('Authorization', 'fake-token')
      .then(expect200)
      .then((res) => {
        expect(res.body.success).to.eq(true);
        return res.body;
      });
  }

  function judgeRound(playerUid, gameKey, roundNumber) {
    uid = playerUid;
    return chai.request(app)
      .patch('/api/v1/games/'+gameKey+'/rounds/'+roundNumber+'/judge')
      .set('Authorization', 'fake-token')
      .then(expect200)
      .then((res) => {
        expect(res.body.success).to.eq(true);
        return res.body;
      });
  }

  function completeRound(playerUid, gameKey, roundNumber, cardKey) {
    uid = playerUid;
    console.error('complete round', gameKey, roundNumber, cardKey);
    return chai.request(app)
      .patch('/api/v1/games/'+gameKey+'/rounds/'+roundNumber+'/winner/'+cardKey)
      .set('Authorization', 'fake-token')
      .then(expect200)
      .then((res) => {
        expect(res.body.success).to.eq(true);
        return res.body;
      });
  }

  function getGame(gameKey) {
    return require('../models/games').findGameByKey(gameKey);
  }

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
        return createGame('fake-user-id');
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
          .patch('/api/v1/games/join')
          .send()
          .then(() => 'never called, but needed to get a promise')
          .catch((err) => {
            expect(err.status).to.eq(401);
            return err.response;
          })
          .then(expectJson({error: 'No credentials found.'}))
      });

      it('accepts the inviteCode and responds with the gameKey', function() {
        this.timeout(5000);

        return createGame('fake-user-id')
          .then(([gameKey, inviteCode]) => {
            return joinGame('second-fake-user-id', gameKey, inviteCode);
          });

      });
    });

    describe('/games/:gameKey/start', function() {
      this.timeout(5000);

      it('starts', function() {
        return createGame('fake-user-id')
          .then(([gameKey, inviteCode]) => {
            return startGame('fake-user-id', gameKey);
          });
      });

    });

    describe('/games/:gameKey/round/:roundNumber/submit/:cardKey', function() {
      this.timeout(10000);

      it('records the card submitted', function() {
        return createGame('user-1')
          .then(([gameKey, inviteCode]) => joinGame('user-2', gameKey, inviteCode))
          .then((gameKey) => startGame('user-1', gameKey))
          .then((gameKey) => getGame(gameKey))
          .then((game) => {
            const cardKey = game.hands['user-2'][0].cardKey;
            return submitCard('user-2', game.gameKey, game.currentRound, cardKey);
          });
      });
    });

    describe('/games/:gameKey/rounds/:roundNumber/judge', function() {
      this.timeout(10000);

      it('changes the round\'s state', function() {
        return createGame('user-1')
          .then(([gameKey, inviteCode]) => joinGame('user-2', gameKey, inviteCode))
          .then((gameKey) => startGame('user-1', gameKey))
          .then((gameKey) => getGame(gameKey))
          .then((game) => {
            const cardKey = game.hands['user-2'][0].cardKey;
            return submitCard('user-2', game.gameKey, game.currentRound, cardKey);
          })
          .then((resBody) => {
            return judgeRound('user-1', resBody.gameKey, resBody.roundNumber);
          })
          .then((resBody) => getGame(resBody.gameKey))
          .then((game) => {
            expect(game.rounds[1].state).to.eq('JUDGING');
          });
      });

    });

    describe('/games/:gameKey/rounds/:roundNumber/winner', function() {
      this.timeout(10000);

      it('completes the round', function() {
        let cardKey;
        return createGame('user-1')
          .then(([gameKey, inviteCode]) => joinGame('user-2', gameKey, inviteCode))
          .then((gameKey) => startGame('user-1', gameKey))
          .then((gameKey) => getGame(gameKey))
          .then((game) => {
            cardKey = game.hands['user-2'][0].cardKey;
            return submitCard('user-2', game.gameKey, game.currentRound, cardKey);
          })
          .then((resBody) => {
            return judgeRound('user-1', resBody.gameKey, resBody.roundNumber);
          })
          .then((resBody) => {
            return completeRound('user-1', resBody.gameKey, resBody.roundNumber, cardKey);
          })
          .then((resBody) => getGame(resBody.gameKey))
          .then((game) => {
            expect(game.rounds[1].state).to.eq('COMPLETE');
            expect(game.currentRound).to.eq(2);
            expect(_.values(game.hands['user-2']).length).to.eq(7);
            expect(game.rounds['2'].state).to.eq('SUBMITTING');
          });
      });
    });

  });
});
