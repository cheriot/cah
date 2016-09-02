'use strict';

// games: {
//   inviteCode
//   createdAt
//   createdBy
//   players: {
//     [playerKey]: {
//       displayName
//       connected
//     }
//   }
//
//   currentRound
//   rounds: {
//     [roundNumber]: {
//       number
//     }
//   }

const firebase = require('./firebase'),
  auth = require('./auth'),
  assert = require('assert'),
  id_conceal = require('./id_conceal'),
  _ = require('lodash');

function allocateGameId() {
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

function playersValue(currentUser) {
  return {
    displayName: currentUser.name,
    // The client will set to true after registering
    // an onDisconnect callback.
    connected: false
  };
}

function gameRef(gameKey) {
  if(!gameKey) throw 'Invalid gameKey '+gameKey;
  return firebase.ref('games/'+gameKey);
}

function createGame(currentUser, inviteCode) {
  assert(inviteCode, 'Valid inviteCode required.');

  return firebase.ref('games').push({
    version: 1,
    inviteCode: inviteCode,
    players: {
      [currentUser.uid]: playersValue(currentUser)
    },
    // Time since the unix epock in ms.
    createdAt: firebase.timestamp,
    createdBy: currentUser.uid
  });
}

function findGameByKey(gameKey) {
  return firebase.ref('games')
    .orderByKey()
    .equalTo(gameKey)
    .once('value')
    .then((snapshot) => {
      if(!snapshot.exists()) throw new Error('No game found '+gameKey);
      const game = snapshot.val()[gameKey];
      return game;
    });
}

function findGameByCode(inviteCode) {
  return firebase.ref('games')
    .orderByChild('inviteCode')
    .equalTo(inviteCode)
    .limitToLast(1)
    .once('value')
    .then((snapshot) => {
      const val = snapshot.val();
      // val is { gameKey: {inviteCode, createdAt, createdBy, ...}}
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

module.exports.create = (currentUser) => {
  return allocateGameId()
    .then((gameId) => id_conceal.encode(gameId))
    .then((inviteCode) => createGame(currentUser, inviteCode))
    // #then will make sure the write finishes before sending a response.
    .then((gameRef) => gameRef.once('value'))
    .then((snap) => {
      return { gameKey: snap.key, inviteCode: snap.val().inviteCode };
    })
    .catch((err) => {throw err});
}

module.exports.join = (currentUser, inviteCode) => {
  return findGameByCode(inviteCode)
    .then((game) => {
      return gameRef(game.gameKey)
        .child('players/'+currentUser.uid)
        .set(playersValue(currentUser))
        .then(() => _.pick(game, ['gameKey', 'inviteCode']));
    })
    .catch((err) => {throw err});
}

module.exports.start = (currentUser, gameKey) => {
  return findGameByKey(gameKey)
    .then((game) => {
      // Only the person that created the game can start it. This gives people
      // time to download or open the app and join.
      if(game.createdBy !== currentUser.uid) throw 'Only the creater can start.';
      const roundNumber = 1;
      // The round must be persisted before the currentRound changes.
      return gameRef(gameKey)
        .child('rounds/'+roundNumber)
        .set({number: roundNumber})
        .then(() => {
          return gameRef(gameKey).child('currentRound').set(roundNumber);
        })
        .then(() => {
          // Do not return any information. The requester will get data the same
          // as all other players.
          return {success: true};
        });
    });
}
