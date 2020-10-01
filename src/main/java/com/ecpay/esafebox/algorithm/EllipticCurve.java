package com.ecpay.esafebox.algorithm;

import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.security.NoSuchProviderException;

/**
 * Created by Joe on Saturday.
 */
public interface EllipticCurve {
  ECPrivateKeyParameters generatePrivateKeyParameters(BigInteger secretNumber);

  ECPrivateKeyParameters generatePrivateKeyParameters();

  ECPublicKeyParameters getPublicKeyParameters(BigInteger secretNumber);

  ECPublicKeyParameters getPublicKeyParameters(ECPrivateKeyParameters privateKeyParameters);

  ECPublicKeyParameters getPublicKeyParameters(byte[] QPoint);

  ECPoint decodePoint(byte[] data);

  ECPoint encodeToECPoint(byte[] data);

  byte[] decodeFromECPoint(ECPoint point);

  BigInteger randomD();

  ECPoint randomQ();

  ECPoint getGPoint();

  BigInteger getN();
  
  boolean verify(byte[] dataSign, byte[] signature, ECPublicKeyParameters publicKeyParameters);

  static CurveType getByCurveName(String name) throws NoSuchProviderException {
    if (CurveType.EC_SECP256K1.matched(name)) {
      return CurveType.EC_SECP256K1;
    } else if (CurveType.EC_SECP256R1.matched(name)) {
      return CurveType.EC_SECP256R1;
    } else if (CurveType.EC_SECP384R1.matched(name)) {
      return CurveType.EC_SECP384R1;
    } else if (CurveType.EC_SECP512R1.matched(name)) {
      return CurveType.EC_SECP512R1;
    }
    throw new NoSuchProviderException();
  }
}
