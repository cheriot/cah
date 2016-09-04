const firebase = require('./firebase');
const _ = require('lodash');

function shuffleGame(gameKey, playerUids) {
  // Shuffle for this game and copy into a location that dealt cards can
  // be deleted from.
  return Promise.resolve();
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

function pickQuestion(gameKey) {
  // This does not remove the previously picked cards from the deck.

  return firebase.ref('cards/questions').once('value')
    .then((snapshot) => {
      const val = snapshot.val()
      console.error(val);

      const drawnCardKey = shuffle(_.keys(val))[0];
      return val[drawnCardKey];
    });
}

function pickAnswer(gameKey) {
  throw 'Not implemented yet.';
}

function loadCards() {
  function setCard(type, card) {
    if(!card.externalId) throw 'card.externalId is required';
    const cardsRef = firebase.ref('cards');
    const deckRef = cardsRef.child(type);

    return deckRef
      .orderByChild('externalId')
      .equalTo(card.externalId)
      .once('value')
      .then((snapshot) => {
        let write, promise;
        if(snapshot.exists()) {
          write = 'update';
          const val = snapshot.val();
          const existingKey = _.keys(val)[0];
          promise = deckRef.child(existingKey).set(card);
        } else {
          write = 'insert';
          promise = deckRef.push(card);
        }
        console.error(write, type, 'card', card.text);
        return promise;
      });
  }

  const questions = require('../data/questions').questions;
  const answers = require('../data/answers').answers;
  // cards/{questions,answers} will have the cononical key and data
  const qPromises = questions.map((q) => setCard('questions', q))
  const aPromises = answers.map((a) => setCard('answers', a));

  return Promise.all(qPromises.concat(aPromises))
}

module.exports = {
  loadCards: loadCards,
  shuffleGame: shuffleGame,
  pickQuestion: pickQuestion,
  pickAnswer: pickAnswer
};
