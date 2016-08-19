const firebase = require("firebase");

var database;
function initClient() {
  if(process.env.NODE_ENV != 'production')
    throw new Error('Attempting to connect to production.');

  const serviceAccount = require('../config/cah-key-production.json');
  firebase.initializeApp({
    serviceAccount: serviceAccount,
    databaseURL: 'https://cards-against-humanity-14b7e.firebaseio.com/'
  });
  database = firebase.database();
}

function ref(path) {
  if(!database) initClient();
  return database.ref(path);
}

function setDatabase(mock) {
  database = mock;
}

module.exports = {
  ref: ref,
  setDatabase: setDatabase
}
