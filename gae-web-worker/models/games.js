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
//   hands: {
//     [playerKey]: {
//       [position]: card
//     }
//   }
//
//   currentRound
//   rounds: {
//     [roundNumber]: {
//       number
//       question
//       judgeUid
//     }
//   }

const
  firebase = require('./firebase'),
  auth = require('./auth'),
  cards = require('./cards'),
  id_conceal = require('./id_conceal');

const
  assert = require('assert'),
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

function roundRef(gameKey, roundNumber) {
  return gameRef(gameKey).child('rounds').child(roundNumber);
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
  // TODO accept a playerUid and verify the requester is in the game.
  return firebase.ref('games')
    .orderByKey()
    .equalTo(gameKey)
    .once('value')
    .then((snapshot) => {
      if(!snapshot.exists()) throw new Error('No game found '+gameKey);
      const game = snapshot.val()[gameKey];
      game.gameKey = gameKey;
      return game;
    });
}
// Expose this method to verify test expectations.
module.exports.findGameByKey = findGameByKey;

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

function chooseJudge(game) {
  return Promise.resolve(game.createdBy);
}

function roundValue(game, number) {
  const round = {number: number};
  // deal black card, choose judge
  return Promise.all([
    cards.pickQuestion(game.gameKey),
    chooseJudge(game)
  ])
  .then(([question, judgeUid]) => {
    return {
      state: 'SUBMITTING',
      number: number,
      question: question,
      judgeUid: judgeUid
    };
  });
}

function initialHandsValue(gameKey, playerUids) {
  const cardsPerHand = 7;
  return cards.pickAnswer(gameKey, playerUids.length*cardsPerHand)
    .then((cards) => {
      return playerUids.reduce((hands, uid, index) => {
          const start = index*cardsPerHand;
          hands[uid] = cards.slice(start, start+cardsPerHand)
          return hands;
        }, {});
    });
}

function requireCard(hand, cardKey) {
  const card = _.values(hand).find((card) => card.cardKey == cardKey);
  if(!card) throw new Error('Unable to find card '+cardKey+' in player\'s hand.');
  return card;
}

function requireRound(game, roundNumber) {
  if(game.currentRound != roundNumber)
    throw new Error('Round '+roundNumber+' is no longer current. Try '+game.currentRound+'.');
  const round = game.rounds[roundNumber];
  if(!round) throw new Error('Unable to find round '+roundNumber);
  return round;
}

function requireJudge(game, roundNumber, currentUser) {
  const round = game.rounds[roundNumber];
  if(!round)
    throw new Error('Invalid roundNumber '+roundNumber);
  if(round.judgeUid != currentUser.uid)
    throw new Error('Only the judge can commence judging.');
  if(round.state != 'SUBMITTING')
    throw new Error('Round is not ready for judging.');
}

module.exports.judgeRound = (currentUser, gameKey, roundNumber) => {

  return findGameByKey(gameKey)
    .then((game) => {
      requireJudge(game, roundNumber, currentUser);
      requireRound(game, roundNumber);
      return roundRef(gameKey, roundNumber)
        .child('state')
        .set('JUDGING');
    })
    .then(() => {
      return {gameKey: gameKey, roundNumber: roundNumber, success: true};
    })
    .catch((err) => {throw err});
}

module.exports.submitCard = (currentUser, gameKey, roundNumber, cardKey) => {
  return findGameByKey(gameKey)
    .then((game) => {
      const hand = game.hands[currentUser.uid];
      const card = requireCard(hand, cardKey);
      const round = requireRound(game, roundNumber);
      return roundRef(gameKey, roundNumber)
        .child('cardsPlayed')
        .child(currentUser.uid)
        .set(card)
        .then(() => {
          return {gameKey: gameKey, roundNumber: roundNumber, cardKey: cardKey, success: true};
        });
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

      // Shuffle cards for the game. Future requests will draw from positions established here.
      const roundNumber = 1;
      const playerUids = _.keys(game.players);
      return cards.shuffleGame(gameKey, playerUids)
        .then(() => {
          // Deal answer cards.
          const handsPromise = initialHandsValue(gameKey, playerUids)
            .then((hands) => {
              return gameRef(gameKey)
                .child('hands')
                .set(hands);
            });

          // Set up the round.
          return roundValue(game, roundNumber)
            .then((round) => {
              return gameRef(gameKey)
                .child('rounds/'+roundNumber)
                .set(round);
            });
        })
        .then(() => {
          // Now that the round is persisted, making it current will trigger
          // clients to start playing it.
          return gameRef(gameKey).child('currentRound').set(roundNumber);
        })
        .then(() => {
          // Do not return any information. The requester will get data from
          // firebase the same as all other players.
          return {gameKey: gameKey, success: true};
        });
    });
}
