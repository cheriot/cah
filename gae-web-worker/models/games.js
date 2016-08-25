'use strict';

const firebase = require('./firebase'),
  assert = require('assert');

module.exports.create = (uid) => {
  assert(uid, 'Valid uid required.');

  return firebase.ref('games').push({
    players: {[uid]: true},
    created: {
      // Time since the unix epock in ms.
      at: firebase.timestamp,
      uid: uid
    }
  })
  // Make sure the write finishes before sending a response.
  .then((r) => r.key);
}

/*
 * Brainstorming data formats

 *  games: {
 *    .read: server-only
 *    .write: server-only
 *    [gameId]: {
 *      identifiers: {
 *        .read: games[gameId]players[auth.uid] exists
 *        shortCode: <value>
 *      }
 *      created: {
 *        .read: games[gameId]players[auth.uid] exists
 *        at: firebase.timestamp,
 *        uid: uid
 *      }
 *      players: {
 *        .read: games[gameId]players[auth.uid] exists
 *        [uid]: true
 *      }
 *      hands: {
 *        [uid]: {
 *          .read games[gameId]hands[auth.uid] is this one
 *          [cardId]: { copy of card }
 *        }
 *      }
 *    }

 *    OR

 *    [gameId]: {
 *      table: {
 *        .read players
 *        players:
 *        created:
 *        identifiers: {
 *          shortCode
 *        }
 *      }
 *      hand: {
 *        [uid]: {
 *          .read this player
 *        }
 *      }

*/
