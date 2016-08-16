'use strict';

const router = require('express').Router();

router.get('/', function(req, res) {
  res.json({ message: 'hooray! welcome to api v1!' });
});

module.exports = router;
