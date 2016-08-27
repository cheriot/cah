'use strict';

const firebase = require('./firebase'),
  assert = require('assert'),
  id_conceal = require('./id_conceal');

function fetchGameId() {
  function incrementSequence (current) {
    if(current) return current+1;
    else return 10000;
  }

  return firebase.ref('gameSequence')
    .transaction(incrementSequence, (error, committed, snapshot) => {
      if (error) {
        console.error('gameSequence: Transaction failed abnormally!', error);
      } else if (!committed) {
        console.error('gameSequence: Aborted transaction.');
      }
      return snapshot.val();
    })
    .then((transactionResult) => transactionResult.snapshot.val());
}

function concealCode(gameId) {
  return id_conceal.encode(gameId);
}

function createGame(uid, gameCode) {
  assert(gameCode, 'Valid gameCode required.');

  return firebase.ref('games').push({
    gameCode: gameCode,
    players: {[uid]: true},
    created: {
      // Time since the unix epock in ms.
      at: firebase.timestamp,
      uid: uid
    }
  });
}

module.exports.create = (uid) => {
  assert(uid, 'Valid uid required.');

  return fetchGameId()
    .then(concealCode)
    .then((gameCode) => createGame(uid, gameCode))
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
