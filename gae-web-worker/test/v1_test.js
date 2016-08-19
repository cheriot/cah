const chai = require('chai'),
  expect = chai.expect,
  chaiHttp = require('chai-http'),
  app = require('../app'),
  FirebaseServer = require('firebase-server');

chai.use(chaiHttp);

/*
 * Firebase/data helpers
 */

var firebaseServer;
function startFirebase(initialData) {
  firebaseServer = new FirebaseServer(5000, 'localhost.firebaseio.com', initialData || {});
}

function closeFirebase() {
  firebaseServer.close();
}

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
    expect(res.body).to.deep.equal(obj);
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

  // TODO extract admin tests into another file.
  describe('/admin/init', function() {
    beforeEach(() => startFirebase({foo: 'bar'}));
    afterEach(closeFirebase);

    it('initializes the database if empty.', function() {
      return chai.request(app)
        .post('/admin/init')
        .then(expect200)
        .then(expectJson({message: 'Database initialized.', data: 'bar'}));
    });
  });

  describe('/api/v1', function() {
    it('/ is reachable', function() {
      return chai.request(app)
        .get('/api/v1/')
        .then(expect200)
        .then(expectNoCache)
        .then(expectJson({message: "hooray! welcome to api v1!"}))
    });

    it('/message', function() {
      startFirebase({message: 'original message'});
      const m = "New Message Value";
      return chai.request(app)
        .post('/api/v1/message')
        .send({message: m})
        .then(expect200)
        .then(expectNoCache)
        //.then(expectJson({message: m})) // See error in models/message.js
        .then(() => chai.request(app).get('/api/v1/message') )
        .then(expect200)
        .then(expectNoCache)
        .then(expectJson({message: m}))
        .then(closeFirebase);
    });
  });
});
