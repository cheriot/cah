const firebase = require('firebase'),
  _ = require('lodash');

var auth;
function setAuth(a) {
  auth = a;
}
setAuth(firebase.auth());

function authError(res, reason) {
  const err = new Error(reason || 'Invalid credentials.');
  err.status = 401;
  return err;
}

// res.locals.uid || 401.
function requireAuth(req, res, next) {
  const token = req.header('Authorization');
  if(_.isEmpty(token)) next(authError(res, 'No credentials found.'));

  auth.verifyIdToken(token).then(function(decodedToken) {
    res.locals.uid = decodedToken.uid;
    next();
  }).catch(function(err) {
    next(authError(res, 'Error authenticating: ' + err.message));
  });
}

module.exports = {
  // Express middleware.
  requireAuth: requireAuth,
  // Set auth to a mock or spy.
  setAuth: setAuth
}
