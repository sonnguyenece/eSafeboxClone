package com.ecpay.esafebox.algorithm;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Created by Joe on Saturday.
 */
@Component
public class EllipticCurveImpl implements EllipticCurve {
	// prime field
	private BigInteger p;
	// a factor
	private BigInteger a;
	// b factor
	private BigInteger b;
	// G base point
	private BigInteger g;
	// N order of G
	private BigInteger n;

	private ECCurve.Fp curve;
	private ECDomainParameters ecDomain;

	public EllipticCurveImpl(BigInteger p, BigInteger a, BigInteger b, BigInteger g, BigInteger n) {
		this.p = p;
		this.a = a;
		this.b = b;
		this.g = g;
		this.n = n;
		curve = new ECCurve.Fp(p, a, b, n, ECConstants.ONE);
		ecDomain = new ECDomainParameters(curve, curve.decodePoint(g.toByteArray()), n);
	}

	private EllipticCurveImpl() {
	}

	@Override
	public ECPrivateKeyParameters generatePrivateKeyParameters(BigInteger secretNumber) {
		return new ECPrivateKeyParameters(secretNumber, ecDomain);
	}

	@Override
	public ECPrivateKeyParameters generatePrivateKeyParameters() {
		return generatePrivateKeyParameters(randomD());
	}

	@Override
	public ECPublicKeyParameters getPublicKeyParameters(BigInteger secretNumber) {
		ECPoint ecPoint = ecDomain.getG().multiply(secretNumber);
		return new ECPublicKeyParameters(ecPoint, ecDomain);
	}

	@Override
	public ECPublicKeyParameters getPublicKeyParameters(ECPrivateKeyParameters privateKeyParameters) {
		ECPoint ecPoint = ecDomain.getG().multiply(privateKeyParameters.getD());
		return new ECPublicKeyParameters(ecPoint, ecDomain);
	}

	@Override
	public ECPublicKeyParameters getPublicKeyParameters(byte[] QPoint) {
		return new ECPublicKeyParameters(decodePoint(QPoint), ecDomain);
	}

	@Override
	public ECPoint decodePoint(byte[] data) {
		return curve.decodePoint(data);
	}

	@Override
	public ECPoint encodeToECPoint(byte[] data) {
		int lBits = n.bitLength() / 2;
		if (data.length * 8 > lBits) {
			throw new IllegalArgumentException("Message too large to be encoded(more than " + lBits / 8 + " bytes)");
		}

		BigInteger mask = BigInteger.ZERO.flipBit(lBits).subtract(BigInteger.ONE);
		BigInteger m = new BigInteger(1, data);
		ECFieldElement a = ecDomain.getCurve().getA();
		ECFieldElement b = ecDomain.getCurve().getB();

		BigInteger r;
		ECFieldElement x = null, y = null;
		do {
			r = randomD();
			r = r.andNot(mask).or(m);
			if (!ecDomain.getCurve().isValidFieldElement(r)) {
				continue;
			}
			x = ecDomain.getCurve().fromBigInteger(r);

			// y^2 = x^3 + ax + b = (x^2+a)x +b
			ECFieldElement y2 = x.square().add(a).multiply(x).add(b);
			y = y2.sqrt();
		} while (y == null);

		return ecDomain.getCurve().createPoint(x.toBigInteger(), y.toBigInteger());
	}

	@Override
	public byte[] decodeFromECPoint(ECPoint point) {
		int lBits = n.bitLength() / 2;
		byte[] bs = new byte[lBits / 8];
		byte[] xbytes = point.normalize().getAffineXCoord().toBigInteger().toByteArray();
		System.arraycopy(xbytes, xbytes.length - bs.length, bs, 0, bs.length);
		return bs;
	}

	@Override
	public ECPoint randomQ() {
		return ecDomain.getG().multiply(randomD());
	}

	@Override
	public BigInteger getN() {
		return n;
	}

	public BigInteger randomD() {
		int nBitLength = ecDomain.getN().bitLength();
		BigInteger k = new BigInteger(nBitLength, secureRandom);

		while (k.compareTo(ECConstants.ZERO) <= 0 || (k.compareTo(ecDomain.getN()) >= 0)) {
			k = new BigInteger(nBitLength, secureRandom);
		}

		return k;
	}

	@Override
	public ECPoint getGPoint() {
		return ecDomain.getG();
	}

	
	@Override
	public boolean verify(byte[] dataSign, byte[] signature, ECPublicKeyParameters publicKeyParameters) {
		try (ASN1InputStream asn1 = new ASN1InputStream(signature)) {
			ECDSASigner signer = new ECDSASigner();
			signer.init(false, publicKeyParameters);
			DLSequence seq = (DLSequence) asn1.readObject();
			BigInteger r = ((ASN1Integer) seq.getObjectAt(0)).getPositiveValue();
			BigInteger s = ((ASN1Integer) seq.getObjectAt(1)).getPositiveValue();
			return signer.verifySignature(dataSign, r, s);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}


	static SecureRandom secureRandom;

	static {
		try {
			secureRandom = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			secureRandom = new SecureRandom();
		}
	}
}
