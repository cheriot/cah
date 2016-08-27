'use strict';

process.on('unhandledRejection', function(error, promise) {
  console.error("UNHANDLED REJECTION", error.stack);
});

const _ = require('lodash'),
  fb = require('../models/firebase');

const transform = (current) => {
  if(current) return current+1;
  else return 1;
};

fb.ref('/gameCodeCounter').transaction(transform, (error, committed, snapshot) => {
  if (error) {
    console.log('Transaction failed abnormally!', error);
  } else if (!committed) {
    console.log('Aborted transaction.');
  } else {
    console.log('Counter incremented.');
  }
  console.log("Value: ", snapshot.val());
})
.then((what) => {
  console.error(what.snapshot.val());
});
