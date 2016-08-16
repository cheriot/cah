const chai = require('chai'),
  expect = chai.expect,
  chaiHttp = require('chai-http'),
  app = require('../app');

chai.use(chaiHttp);

function expect200(res) {
  expect(res).to.have.status(200);
  return res;
}

function expectJson(obj) {
  return function(res) {
    expect(res.body).to.deep.equal(obj);
    return res;
  }
}

function error(err) {
  console.log('Error is', err);
  throw err;
}

describe('/api/v1', () => {
  it('/ is reachable', () => {
    return chai.request(app)
      .get('/api/v1/')
      .then(expect200)
      .then(expectJson({message: "hooray! welcome to api v1!"}))
      .catch(error);
  });
});
