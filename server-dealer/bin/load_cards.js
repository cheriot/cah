'use strict';

process.on('unhandledRejection', function(error, promise) {
  console.error("UNHANDLED REJECTION", error.stack);
});

const _ = require('lodash');
const cards = require('../models/cards');

cards.loadCards()
  .then(() => {
    console.error('cards loaded');
    process.exit(0);
  });
