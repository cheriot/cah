'use strict';

const router = require('express').Router(),
  requireAuth = require('../models/auth').requireAuth,
  games = require('../models/games'),
  _ = require('lodash');

// All routes require authentication.
router.use(requireAuth);

function requireBody(req, field) {
  const value = req.body[field];
  if(value) return value;
  else {
    const err = new Error('Required field ' + field + ' is empty.');
    err.status = 400;
    throw err;
  }
}

router.get('/', function foo(req, res) {
  res.json({ message: 'hooray! welcome to api v1!' });
});

router.route('/games')
  .post(function(req, res) {
    games
      .create(res.locals.currentUser)
      .then((game) => res.json(game) );
  });

router.route('/games/join')
  .post(function(req, res) {
    const gameCode = requireBody(req, 'gameCode');
    games
      .join(res.locals.currentUser, gameCode)
      .then((result) => {
        if(result.error) {
          res.status(404).json(result);
        } else {
          res.json(result);
        }
      });
  });

// Error handler. Must come after other routes and middleware.
router.use(function(err, req, res, next) {
  if(err.status == 500 || !err.status) {
    console.error('Server Error:', err);
  } else {
    console.error('Client Error:', err);
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
