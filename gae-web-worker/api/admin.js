'use strict';

const router = require('express').Router(),
  db = require('../models/firebase');

router.post('/init', (req, res) => {
  // Is the database empty? Initialize it.

  res.json({
    message: 'Database initialized.',
  });
});

module.exports = router;
