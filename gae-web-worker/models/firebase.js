const firebase = require("firebase");

// Test setup
firebase.initializeApp({
  apiKey: 'foo',
  databaseURL: 'ws://localhost.firebaseio.com:5000'
});

// Real setup
// firebase.initializeApp({
//   serviceAccount: "path/to/serviceAccountCredentials.json",
//   databaseURL: "https://databaseName.firebaseio.com"
// });

const db = firebase.database();

module.exports = db;
