'use strict';

const router = require('express').Router(),
  requireAuth = require('../models/auth').requireAuth,
  games = require('../models/games'),
  _ = require('lodash');

// All routes require authentication.
router.use(requireAuth);

router.get('/', function foo(req, res) {
  res.json({ message: 'hooray! welcome to api v1!' });
});

router.route('/games')
  .post(function(req, res) {
    games
      .create(res.locals.uid)
      .then((gameKey) => res.json({gameKey: gameKey}) );
  });

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

// Error handler. Must come after other routes and middleware.
router.use(function(err, req, res, next) {
  if(err.status == 500 || !err.status) {
    console.error('Server Error:', err);
  }
  res.status(err.status || 500)
     .json({error: err.message});
});

// DEBUG
// console.error('Registered routes');
// const routes = router.stack.forEach((r) => {
//   if(r.route) console.error(r.route.methods, r.route.path);
//   else console.error('middleware');
// });

module.exports = router;
