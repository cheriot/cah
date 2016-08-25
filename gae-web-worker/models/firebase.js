const firebase = require('firebase');

// firebase.database.enableLogging(true);

var auth, database, dbPrefix = '';
function initClient() {
  if(process.env.NODE_ENV != 'production')
    throw new Error('Attempting to connect to production.');

  const serviceAccount = require('../config/cah-key-production.json');
  firebase.initializeApp({
    serviceAccount: serviceAccount,
    databaseURL: 'https://cards-against-humanity-14b7e.firebaseio.com/'
  });
  database = firebase.database();
  auth = firebase.auth();
}

function setAuth(a) {
  auth = a;
}
function getAuth() {
  if(!auth) initClient();
  return auth;
}

function ref(path) {
  if(!database) initClient();
  return database.ref(dbPrefix + path);
}

function setDatabase(mock, prefix) {
  dbPrefix = prefix;
  database = mock;
}

module.exports = {
  // database.ref
  ref: ref,
  timestamp: firebase.database.ServerValue.TIMESTAMP,
  // Set database to mock or spy
  setDatabase: setDatabase,
  // Set auth to a mock or spy.
  setAuth: setAuth,
  // firebase.auth
  auth: getAuth
}
