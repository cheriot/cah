const firebase = require('./firebase'),
  _ = require('lodash');

function authError(res, reason) {
  const err = new Error(reason || 'Invalid credentials.');
  err.status = 401;
  return err;
}

// res.locals.uid || 401.
function requireAuth(req, res, next) {
  const token = req.header('Authorization');
  if(_.isEmpty(token)) {
    next(authError(res, 'No credentials found.'));
    return;
  }

  firebase.auth().verifyIdToken(token).then(function(decodedToken) {
    res.locals.uid = decodedToken.uid;
    next();
  }).catch(function(err) {
    const msg = 'Error authenticating: ' + err.message;
    console.error(msg + ' Token of length ' + token.length + ' ' + token);
    next(authError(res, msg));
  });
}

module.exports = {
  // Express middleware.
  requireAuth: requireAuth,
}
