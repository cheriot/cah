const firebase = require("firebase"),
      FirebaseServer = require('firebase-server');

var firebaseServer;
function startServer(initialData) {
  firebaseServer = new FirebaseServer(5000, 'localhost.firebaseio.com', initialData || {});
}

function stopServer() {
  firebaseServer.close();
}

var dbClient;
function initClient() {
  if(!dbClient) {
    firebase.initializeApp({
      apiKey: 'foo',
      databaseURL: 'ws://localhost.firebaseio.com:5000'
    });
    dbClient = firebase.database();
  }
  return dbClient;
}

module.exports = {
  startServer: startServer,
  stopServer: stopServer,
  initClient: initClient
};
