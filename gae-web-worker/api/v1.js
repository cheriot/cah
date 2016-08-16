'use strict';

const router = require('express').Router();

router.get('/', function(req, res) {
  res.json({ message: 'hooray! welcome to api v1!' });
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

module.exports = router;
