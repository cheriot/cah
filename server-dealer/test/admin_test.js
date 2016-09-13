const chai = require('chai'),
  expect = chai.expect,
  chaiHttp = require('chai-http'),
  app = require('../app');

chai.use(chaiHttp);
