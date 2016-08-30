'use strict';

const firebase = require('./firebase'),
  auth = require('./auth'),
  assert = require('assert'),
  id_conceal = require('./id_conceal'),
  _ = require('lodash');

function fetchGameId() {
  function incrementSequence (current) {
    if(current) return current+1;
    else return 1024; // 32*32 so we'll start with at least three digits.
  }

  // Right now, if a code is reissued, we can't query in a way that
  // will find the most recent one. Change to use the following structure
  // so that the sequence can be reset without issue.
  // inviteCodes: {
  //   sequence: number,
  //   codeGames: {
  //     [code]: [gameKey]
  //   }
  // }

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

function createGame(uid, gameCode) {
  assert(gameCode, 'Valid gameCode required.');

  return firebase.ref('games').push({
    version: 1,
    gameCode: gameCode,
    players: {
      [uid]: {
        displayName: auth.currentUser().displayName,
        // The client will set to true after registering
        // an onDisconnect callback.
        connected: false
      }
    },
    // Time since the unix epock in ms.
    createdAt: firebase.timestamp,
    createdBy: uid
  });
}

function findGameByCode(gameCode) {
  return firebase.ref('games')
    .orderByChild('gameCode')
    .equalTo(gameCode)
    .limitToLast(1)
    .once('value')
    .then((snapshot) => {
      const val = snapshot.val();
      // val is { gameKey: {gameCode, createdAt, createdBy, ...}}
      const gameKey = _.keys(val)[0];
      const game = val[gameKey];
      game.gameKey = gameKey;
      if(snapshot.exists()) {
        return game;
      } else {
        return {error: 'Game not found'};
      }
    });
}

module.exports.create = (uid) => {
  assert(uid, 'Valid uid required.');

  return fetchGameId()
    .then((gameKey) => id_conceal.encode(gameKey))
    .then((gameCode) => createGame(uid, gameCode))
    // #then will make sure the write finishes before sending a response.
    .then((gameRef) => gameRef.once('value'))
    .then((snap) => {
      return { gameKey: snap.key, gameCode: snap.val().gameCode };
    })
    .catch((err) => throw err);
}

module.exports.join = (uid, gameCode) => {
  return findGameByCode(gameCode)
    .then((game) => {
      console.error('now add to players', game);
      return firebase
        .ref('games/'+game.gameKey+'/players/'+uid)
        .set(true)
        .then(() => _.pick(game, ['gameKey', 'gameCode']));
    })
    .catch((err) => throw err);
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
