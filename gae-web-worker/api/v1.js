'use strict';

const router = require('express').Router(),
  message = require('../models/message');

router.get('/', function(req, res) {
  res.json({ message: 'hooray! welcome to api v1!' });
});

router.route('/message')
  .get(function(req, res) {
    message.get().then((val) => res.json({message: val}));
  })
  .post(function(req, res) {
    message.set(req.body.message);
    res.json({success: true});
  });

// POST /games
// creates a new game
// - initialize game state
// - calculate join code

// POST /games/:game_id/players
// join a game
// - what player state is needed?
// - deals initial hand if game has already started

// DELETE /games/:game_id/players
// leave a game
// - leave everything in the hand

// PATCH /games/:game_id/start
// start a game
// - set the initial judge
// - deal hands

// POST /games/:game_id/round/winner/:card_id
// select the winning card
// - update score board
// - assign a new judge
// - creates a new round

console.error('Registered routes');
const routes = router.stack.forEach((r) => {
  console.error(r.route.methods, r.route.path);
});

module.exports = router;
