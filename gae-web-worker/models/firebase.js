const firebase = require("firebase");

var database;
function initClient() {
  if(process.env.NODE_ENV != 'production')
    throw new Error('Attempting to connect to production.');

  firebase.initializeApp({
    serviceAccount: "path/to/serviceAccountCredentials.json",
    databaseURL: "https://databaseName.firebaseio.com"
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
