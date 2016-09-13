const firebase = require('./firebase');
const _ = require('lodash');

const
  questionsType = 'questions',
  answersType = 'answers';
  cardTypes = [questionsType, answersType];

function cardsRef(cardType) {
  return firebase.ref('cards').child(cardType);
}

function shuffledDecksRef(gameKey, cardType) {
  return firebase.ref('shuffledCards/'+gameKey+'/'+cardType);
}

function shuffleGame(gameKey, playerUids) {
  // Shuffle for this game and copy into a location that allows drawing cards
  // in the shuffled order.
  const promises = ['questions', 'answers'].map((cardType) => {
    return firebase
      .ref('cards/'+cardType)
      .once('value')
      .then((snapshot) => {
        const allCards = snapshot.val()

        const shuffled = shuffle(_.keys(allCards))
          .map((cardKey, index) => {
            return Object.assign(
              allCards[cardKey],
              {cardKey: cardKey, position: index}
            );
        });

        return shuffledDecksRef(gameKey, cardType)
          .set({
            nextPosition: 0,
            orderedCards: shuffled
          });
      });
  });
  return Promise.all(_.flatten(promises));
}

function shuffle(array) {
  let counter = array.length;
  // While there are elements in the array
  while (counter > 0) {
    // Pick a random index
    let index = Math.floor(Math.random() * counter);
    // Decrease counter by 1
    counter--;
    // And swap the last element with it
    let temp = array[counter];
    array[counter] = array[index];
    array[index] = temp;
  }
  return array;
}

function drawCard(deckRef, count) {
  const increment = count || 1;
  function transform(currentValue) {
    return currentValue+increment;
  }

  // increment the shuffled decks position and grab that card
  return deckRef
    .child('nextPosition')
    .transaction(transform, (error, committed, snapshot) => {
      if (error) {
        throw 'Transaction failed abnormally!'+error;
      } else if (!committed) {
        throw 'Aborted transaction.';
      }
    })
    .then((transactionResult) => {
      const drawnPosition = transactionResult.snapshot.val();
      const startAt = drawnPosition - increment;

      return deckRef
        .child('orderedCards')
        .orderByChild('position')
        .startAt(startAt)
        .endAt(drawnPosition)
        .once('value')
        .then((snapshot) => {
          const val = snapshot.val();
          return val;
        });
    });
}

function pickQuestion(gameKey) {
  return drawCard(shuffledDecksRef(gameKey, questionsType));
}

function pickAnswer(gameKey, numCards) {
  return drawCard(shuffledDecksRef(gameKey, answersType), numCards);
}

function loadCards() {
  function setCard(cardType, card) {
    if(!card.externalId) throw 'card.externalId is required';

    const cr = cardsRef(cardType);
    return cr
      .orderByChild('externalId')
      .equalTo(card.externalId)
      .once('value')
      .then((snapshot) => {
        let write, promise;
        if(snapshot.exists()) {
          write = 'update';
          const val = snapshot.val();
          const existingKey = _.keys(val)[0];
          promise = cr.child(existingKey).set(card);
        } else {
          write = 'insert';
          promise = cr.push(card);
        }
        console.error(write, cardType, 'card', card.text);
        return promise;
      });
  }

  function dataForCardType(cardType) {
    return require('../data/'+cardType+'.json')[cardType];
  }

  // cards/{questions,answers} will have the cononical key and data
  const promises = cardTypes.map((cardType) => {
    return dataForCardType(cardType)
      .map((card) => setCard(cardType, card));
  });
  return Promise.all(_.flatten(promises))
}

module.exports = {
  loadCards: loadCards,
  shuffleGame: shuffleGame,
  pickQuestion: pickQuestion,
  pickAnswer: pickAnswer
};
