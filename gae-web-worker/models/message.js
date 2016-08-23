const db = require('./firebase');

function getMessage() {
  return db.ref('message').once('value').then((snap) => {
    return snap.val();
  });
}

function setMessage(str) {
  return db.ref('message').set(str).then(getMessage);
}

module.exports = {
  get: getMessage,
  set: setMessage
};
