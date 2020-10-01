package com.ecpay.esafebox.algorithm;


import java.math.BigInteger;

/**
 * Created by Joe on Thursday.
 */
public enum CurveType {
	EC_SECP256K1("EC-secp256k1", 256,
      new EllipticCurveImpl(
          new BigInteger("fffffffffffffffffffffffffffffffffffffffffffffffffffffffefffffc2f", 16),
          new BigInteger("0000000000000000000000000000000000000000000000000000000000000000", 16),
          new BigInteger("0000000000000000000000000000000000000000000000000000000000000007", 16),
          new BigInteger("0279be667ef9dcbbac55a06295ce870b07029bfcdb2dce28d959f2815b16f81798", 16),
          new BigInteger("fffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd0364141", 16)
      )),
	EC_SECP256R1("EC-secp256r1", 256,
      new EllipticCurveImpl(
          new BigInteger("ffffffff00000001000000000000000000000000ffffffffffffffffffffffff", 16),
          new BigInteger("ffffffff00000001000000000000000000000000fffffffffffffffffffffffc", 16),
          new BigInteger("5ac635d8aa3a93e7b3ebbd55769886bc651d06b0cc53b0f63bce3c3e27d2604b", 16),
          new BigInteger("036b17d1f2e12c4247f8bce6e563a440f277037d812deb33a0f4a13945d898c296", 16),
          new BigInteger("ffffffff00000000ffffffffffffffffbce6faada7179e84f3b9cac2fc632551", 16)
      )),
	EC_SECP384R1("EC-secp384r1", 384,
      new EllipticCurveImpl(
          new BigInteger("fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffeffffffff0000000000000000ffffffff", 16),
          new BigInteger("fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffeffffffff0000000000000000fffffffc", 16),
          new BigInteger("b3312fa7e23ee7e4988e056be3f82d19181d9c6efe8141120314088f5013875ac656398d8a2ed19d2a85c8edd3ec2aef", 16),
          new BigInteger("03aa87ca22be8b05378eb1c71ef320ad746e1d3b628ba79b9859f741e082542a385502f25dbf55296c3a545e3872760ab7", 16),
          new BigInteger("ffffffffffffffffffffffffffffffffffffffffffffffffc7634d81f4372ddf581a0db248b0a77aecec196accc52973", 16)
      )),
	EC_SECP512R1("EC-secp512r1", 512,
      new EllipticCurveImpl(
          new BigInteger("01ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", 16),
          new BigInteger("01fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffc", 16),
          new BigInteger("0051953eb9618e1c9a1f929a21a0b68540eea2da725b99b315f3b8b489918ef109e156193951ec7e937b1652c0bd3bb1bf073573df883d2c34f1ef451fd46b503f00", 16),
          new BigInteger("0200c6858e06b70404e9cd9e3ecb662395b4429c648139053fb521f828af606b4d3dbaa14b5e77efe75928fe1dc127a2ffa8de3348b3c1856a429bf97e7e31c2e5bd66", 16),
          new BigInteger("01fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffa51868783bf2f966b7fcc0148f709a5d03bb5c9b8899c47aebb6fb71e91386409", 16)
      ));

  public int getKeySize() {
    return this.keySize;
  }

  public EllipticCurve getEllipticCurve() {
    return this.ellipticCurve;
  }

  public boolean matched(String name) {
    return this.name.equals(name);
  }

  int keySize;
  String name;
  EllipticCurve ellipticCurve;

  CurveType(String name, int keySize, EllipticCurve ellipticCurve) {
    this.name = name;
    this.keySize = keySize;
    this.ellipticCurve = ellipticCurve;
  }
}
