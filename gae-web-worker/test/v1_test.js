const chai = require('chai'),
  expect = chai.expect,
  chaiHttp = require('chai-http'),
  app = require('../app');

chai.use(chaiHttp);

describe('/api/v1', () => {
  it('/ is reachable', (done) => {
    chai.request(app)
      .get('/api/v1/')
      .end((err, res) => {
        expect(res).to.have.status(200);
        done();
      });
  });
});
