const chai = require('chai'),
  expect = chai.expect,
  _ = require('lodash');

describe('id_conceal.js', function() {
  const id_conceal = require('../../models/id_conceal');

  before(() => {
  })

  it('uses 32 characters for base32', function() {
    // If the base or readableWheel change, there needs to
    // be a migration to prevent duplicate codes.
    // Maybe just reset the sequence to 1.
    expect(id_conceal._base32Wheel.length).to.eq(32);
  });

  it('uses the same number of characters for the base and readable wheels', function() {
    expect(id_conceal._base32Wheel.length).to.eq(id_conceal._readableWheel.length);
  });

  describe('_charToReadableBase32', function() {
    function test(number, readable) {
      return function() {
        expect(
          id_conceal._intToReadableBase32(number, id_conceal._readableWheel)
        ).to.eq(readable);
      }
    }
    it('converts 1', test(1, '3'));
    it('converts 1304', test(1304, '3AS'));
    it('converts 983023', test(983023, 'XZZH'));
  });

  describe('_conceal', function() {
    function test(number, result) {
      return function() {
        expect(
          id_conceal._conceal(number, id_conceal._readableWheel)
        ).to.eq(result);
      }
    }
    it('The first digit does not change.', test('2', '2'));
    it('Higher digits do change.', test('AZZ2', 'D322'));
    it('Higher digits do change.', test('AZZ3', 'E433'));
    it('Higher digits do change.', test('AZZ4', 'F544'));
  });

  describe('encode', function() {
    function test(number, result) {
      return function() {
        expect(id_conceal.encode(number)).to.eq(result);
      }
    }
    it('Works with a single digit.', test(1, 'E'));
    it('Higher digits do change.', test(10000, '89T'));
    it('Higher digits do change.', test(10001, 'Y2S'));
    it('Higher digits do change.', test(10002, 'K5G'));
  });
})
