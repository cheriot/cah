// This is not a secure hash.
// Goal: display a short code to users that
//   1. is unique
//   2. as short as possible
//   3. is not easy to guess another recently allocated code
// Users will use this code to identify the game they want to join/invite.
// Based on the algorithm from http://alchemise.net/wordpress/?p=40

// Exclude (1, I) and (O, 0) to improve readability.
const unorderedReadableWheel = 'MEXU4DQZH925LJ7BTSGP6WRFA3V8YKNC';
const readableWheel          = '23456789ABCDEFGHJKLMNPQRSTUVWXYZ';
const base32Wheel            = '0123456789ABCDEFGHIJKLMNOPQRSTUV';

function intToReadableBase32(n, wheel) {
  function charBase32ToReadible(c) {
    const i = base32Wheel.indexOf(c);
    if(i == -1) throw new Error('Unexpected character: '+c);
    return wheel[i];
  }
  const base = base32Wheel.length;
  const base32str = n.toString(base).toUpperCase();
  return base32str.split('').map(charBase32ToReadible).join('');
}

function conceal(str, wheel) {
  // Using the same wheel to translate each character will make it apparant
  // that values are incrementing by one. Then it's easy to guess and join
  // a stranger's game. And looks silly. Solution: change the wheel based
  // on the least significan digit.

  function concealChar(value, offset) {
    const wheelIndex = wheel.indexOf(value);
    if(wheelIndex == -1) throw new Error('Unexpected character: '+value);
    return wheel[(wheelIndex + offset) % wheel.length];
  }

  const leastSignificantWheelIndex = wheel.indexOf(str[str.length-1]);

  function modifiedConcealChar(value, strIndex) {
    if(strIndex == 0) {
      // First digit, regular conceal
      return concealChar(value, 0);
    } else {
      return concealChar(value, strIndex + leastSignificantWheelIndex);
    }
  }

  // reverse to operate on the least significant digit first, then reverse
  // back to the original order.
  return str.split('').reverse().map(modifiedConcealChar).reverse().join('');
}

function encode(n) {
  const str = intToReadableBase32(n, unorderedReadableWheel);
  const concealed = conceal(str, unorderedReadableWheel);
  return concealed;
}

module.exports = {
  _base32Wheel: base32Wheel,
  _readableWheel: readableWheel,
  _unorderedReadableWheel: unorderedReadableWheel,
  _intToReadableBase32: intToReadableBase32,
  _conceal: conceal,
  encode: encode
}
