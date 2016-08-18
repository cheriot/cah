'use strict';

const router = require('express').Router(),
  db = require('../models/firebase');

router.post('/init', function(req, res) {
  // Is the database empty? Initialize it.
  db.ref('/foo').once('value', function(snap) {
    res.json({
      message: 'Database initialized.',
      data: snap.val()
    });
  });
});

module.exports = router;
