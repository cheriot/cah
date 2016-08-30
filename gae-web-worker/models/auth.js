const firebase = require('./firebase'),
  _ = require('lodash');

function authError(reason) {
  const err = new Error(reason || 'Invalid credentials.');
  err.status = 401;
  return err;
}

// res.locals.uid || 401.
function requireAuth(req, res, next) {
  const token = req.header('Authorization');
  if(_.isEmpty(token)) {
    next(authError('No credentials found.'));
    return;
  }

  firebase.auth()
    .verifyIdToken(token)
    .then(function(decodedToken) {
      res.locals.currentUser = decodedToken;
      next();
    }).catch(function(err) {
      const msg = 'Error authenticating: ' + err;
      console.error(msg + ' Token of length ' + token.length + ' ' + token);
      next(authError(msg));
    });
}

function currentUser() {
  const user = firebase.auth().currentUser;
  if(!user) throw authError('Authentication required.');
  return user;
}

module.exports = {
  // Express middleware.
  requireAuth: requireAuth,
  // Function to access the current user.
  currentUser: currentUser
}
