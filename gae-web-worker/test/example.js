'use strict';

// Playground for using the firebase api away from app and test setups.

process.on('unhandledRejection', function(error, promise) {
  console.error("UNHANDLED REJECTION", error.stack);
});

const _ = require('lodash');
const firebase = require('firebase');
firebase.database.enableLogging(true);

const serviceAccount = require('./config/cah-key-production.json');
console.log('connect to', serviceAccount.project_id);
firebase.initializeApp({
  serviceAccount: serviceAccount,
  databaseURL: 'https://cards-against-humanity-14b7e.firebaseio.com/'
});
const database = firebase.database();
const gamesRef = database.ref('games');

const gameRef = gamesRef.push(
  {
    message: new Date().toString(),
    createdAt: firebase.database.ServerValue.TIMESTAMP
  },
  (err) => console.log('error pushing', err)
);

gameRef.then((a) => console.log('push then', a.key));

gameRef.once('value', (snap) => {
  console.log('snapshot', snap.val(), _.keys(snap));
});
