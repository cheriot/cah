const firebase = require("firebase");

// Test setup
firebase.initializeApp({
  apiKey: 'foo',
  databaseURL: 'ws://localhost.firebaseio.com:5000'
});

const db = firebase.database();

// Real setup
// firebase.initializeApp({
//   serviceAccount: "path/to/serviceAccountCredentials.json",
//   databaseURL: "https://databaseName.firebaseio.com"
// });

module.exports = db;
