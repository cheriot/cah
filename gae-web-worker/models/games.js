'use strict';

const firebase = require('./firebase'),
  assert = require('assert');

function fetchGameId() {
  function incrementSequence (current) {
    if(current) return current+1;
    else return 1;
  }

  return firebase.ref('gameSequence')
    .transaction(incrementSequence, (error, committed, snapshot) => {
      if (error) {
        console.error('gameSequence: Transaction failed abnormally!', error);
      } else if (!committed) {
        console.error('gameSequence: Aborted transaction.');
      }
      console.error('gameSequence', snapshot.val());
      return snapshot.val();
    })
    .then((transactionResult) => transactionResult.snapshot.val());
}

function concealCode(gameId) {
  console.error('concealCode', gameId);
  // TODO conceal
  return gameId;
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

// public static string Conceal(string value, string wheel)
// {
//     var alphabet = sortedAlphabet(wheel);
//     var distinctChars = wheel.Distinct().ToArray();
//     if (distinctChars.Length != wheel.Length)
//         throw (new ArgumentException("Error: Wheel contains duplicate characters."));
//     string result = "";
//     for (int i = 0; i < value.Length; i++)
//     {
//         int letterPosition = Array.IndexOf(alphabet, value[i]);
//         if (letterPosition == -1)
//             throw (new ArgumentException("Error: supplied character '"
//                 + value[i] + "' does not appear in code wheel.", value));
//         char encodedLetter = wheel[(letterPosition + i) % wheel.Length];
//         result += encodedLetter;
//     }
//     return result;
// }
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
