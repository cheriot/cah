const chai = require('chai'),
  expect = chai.expect,
  chaiHttp = require('chai-http'),
  app = require('../app');

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

describe('/api/v1', function() {
  it('/ is reachable', function() {
    return chai.request(app)
      .get('/api/v1/')
      .then(expect200)
      .then(expectNoCache)
      .then(expectJson({message: "hooray! welcome to api v1!"}))
      .catch(error);
  });
});
