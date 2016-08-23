'use strict';

const firebase = require('./firebase');

module.exports.create = (userId) => {
  return firebase.ref('games').push({
    players: {[userId]: true},
    // Time since the unix epock in ms.
    createdAt: firebase.timestamp
  })
  // Make sure the write finishes before sending the key to the client.
  .then((r) => r.key);
}
