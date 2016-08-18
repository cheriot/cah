'use strict';

const express = require('express'),
  bodyParser = require('body-parser'),
  nocache = require('nocache');

// Configure the server.
const app = express();
// Fancy query string params with qs!
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());
// Apps will access data through firebase. If that changes, reexamine.
app.use(nocache());

// TODO: Go through this agian:
// https://expressjs.com/en/advanced/best-practice-security.html
// TODO: use helmet
// Will GAE automatically gzip base on Accept headers?
// console.(error|log) can be synchronous. Use a logging lib:
// https://strongloop.com/strongblog/compare-node-js-logging-winston-bunyan/?_ga=1.63652591.1943477917.1471357444
// https://www.npmjs.com/package/debug
// TODO: Go through this again:
// https://expressjs.com/en/advanced/best-practice-performance.html

// Test route to verify the server is up and responding.
app.get('/', function (req, res) {
  res.status(200).send("You're horrible.");
});

// Mount api versions.
app.use('/api/v1', require('./api/v1'));
app.use('/admin', require('./api/admin'));

// Start the server
var server = app.listen(process.env.PORT || '8080', function () {
  console.log('App listening on port %s', server.address().port);
  console.log('Press Ctrl+C to quit.');
});

// For testing
module.exports = app;
