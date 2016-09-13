const firebase = require("firebase"),
      FirebaseServer = require('firebase-server');

var firebaseServer;
function startServer(initialData) {
  firebaseServer = new FirebaseServer(5000, 'localhost.firebaseio.com', initialData || {});
}

function stopServer() {
  firebaseServer.close();
}

var dbClient, refPrefix = '';
function initLocalClient() {
  console.error('init fake firebase');
  firebase.initializeApp({
    apiKey: 'fake-api-key',
    databaseURL: 'ws://localhost.firebaseio.com:5000'
  });
  dbClient = firebase.database();
}

function initRemoteClient() {
  console.error('init real firebase');
  const serviceAccount = require('../config/cah-key-production.json');
  firebase.initializeApp({
    serviceAccount: serviceAccount,
    databaseURL: 'https://cards-against-humanity-14b7e.firebaseio.com/'
  });
  dbClient = firebase.database();
  refPrefix = 'integration/';
}

function installMockClient(modelsFirebase) {
  if(!dbClient) initRemoteClient();
  modelsFirebase.setDatabase(dbClient, refPrefix);
}

module.exports = {
  startServer: startServer,
  stopServer: stopServer,
  installMockClient: installMockClient
};
