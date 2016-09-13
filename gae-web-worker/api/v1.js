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

function jsonResult(res, errHttpCode) {
  return (result) => {
    if(result.error) {
      res.status(errHttpCode || 500).json(result);
    } else {
      res.json(result);
    }
  }
}

router.get('/', function foo(req, res) {
  res.json({ message: 'hooray! welcome to api v1!' });
});

router.route('/games')
  .post(function(req, res) {
    games
      .create(res.locals.currentUser)
      .then(jsonResult(res));
  });

router.route('/games/join')
  .patch(function(req, res) {
    // TODO Also accept gameKey
    const inviteCode = requireBody(req, 'inviteCode');
    games
      .join(res.locals.currentUser, inviteCode)
      .then(jsonResult(res, 404));
  });

router.route('/games/:gameKey/start')
  // Deal the cards and start round one.
  .patch(function(req, res) {
    games
      .start(res.locals.currentUser, req.params.gameKey)
      .then(jsonResult(res));
  });

router.route('/games/:gameKey/rounds/:roundNumber/submit/:cardKey')
  .patch(function(req, res) {
    games
      .submitCard(
        res.locals.currentUser,
        req.params.gameKey,
        req.params.roundNumber,
        req.params.cardKey
      )
      .then(jsonResult(res));
  });

router.route('/games/:gameKey/rounds/:roundNumber/judge')
  .patch(function(req, res) {
    games
      .judgeRound(
        res.locals.currentUser,
        req.params.gameKey,
        req.params.roundNumber
      )
      .then(jsonResult(res));
  });

router.route('/games/:gameKey/rounds/:roundNumber/winner/:cardKey')
  .patch(function(req, res) {
    games
      .completeRound(
        res.locals.currentUser,
        req.params.gameKey,
        req.params.roundNumber,
        req.params.cardKey
      )
      .then(jsonResult(res));
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

module.exports = router;
