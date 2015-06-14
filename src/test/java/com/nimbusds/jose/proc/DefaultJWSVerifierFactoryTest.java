package com.nimbusds.jose.proc;


import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import junit.framework.TestCase;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.util.ByteUtils;


/**
 * Tests the default JWS verifier factory.
 *
 * @version 2015-06-14
 */
public class DefaultJWSVerifierFactoryTest extends TestCase {


	private static SecretKey generateSharedKey(final int bitLength) {

		byte[] keyBytes = new byte[ByteUtils.byteLength(bitLength)];
		new SecureRandom().nextBytes(keyBytes);
		return new SecretKeySpec(keyBytes, "AES");
	}


	public void testCreateHS256Verifier()
		throws Exception {

		JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
		Key key = generateSharedKey(256);

		JWSVerifierFactory factory = new DefaultJWSVerifierFactory();

		JWSVerifier verifier = factory.createJWSVerifier(header, key);
		assertTrue(verifier.supportedJWSAlgorithms().contains(header.getAlgorithm()));

		MACVerifier macVerifier = (MACVerifier)verifier;
		assertTrue(Arrays.equals(key.getEncoded(), macVerifier.getSecret()));
	}


	public void testCreateHS384Verifier()
		throws Exception {

		JWSHeader header = new JWSHeader(JWSAlgorithm.HS384);
		Key key = generateSharedKey(384);

		JWSVerifierFactory factory = new DefaultJWSVerifierFactory();

		JWSVerifier verifier = factory.createJWSVerifier(header, key);
		assertTrue(verifier.supportedJWSAlgorithms().contains(header.getAlgorithm()));

		MACVerifier macVerifier = (MACVerifier)verifier;
		assertTrue(Arrays.equals(key.getEncoded(), macVerifier.getSecret()));
	}


	public void testCreateHS512Verifier()
		throws Exception {

		JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
		Key key = generateSharedKey(512);

		JWSVerifierFactory factory = new DefaultJWSVerifierFactory();

		JWSVerifier verifier = factory.createJWSVerifier(header, key);
		assertTrue(verifier.supportedJWSAlgorithms().contains(header.getAlgorithm()));

		MACVerifier macVerifier = (MACVerifier)verifier;
		assertTrue(Arrays.equals(key.getEncoded(), macVerifier.getSecret()));
	}


	public void testCreateHSVerifierWithInvalidKey()
		throws Exception {

		JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
		Key key = generateRSAPublicKey();

		JWSVerifierFactory factory = new DefaultJWSVerifierFactory();

		try {
			factory.createJWSVerifier(header, key);
			fail();
		} catch (JOSEException e) {
			assertTrue(e instanceof KeyTypeException);
		}
	}


	private static RSAPublicKey generateRSAPublicKey()
		throws Exception {

		KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
		return (RSAPublicKey)keyPair.getPublic();
	}


	public void testCreateRS256Verifier()
		throws Exception {

		JWSHeader header = new JWSHeader(JWSAlgorithm.RS256);
		Key key = generateRSAPublicKey();

		JWSVerifierFactory factory = new DefaultJWSVerifierFactory();

		JWSVerifier verifier = factory.createJWSVerifier(header, key);
		assertTrue(verifier.supportedJWSAlgorithms().contains(header.getAlgorithm()));

		RSASSAVerifier rsassaVerifier = (RSASSAVerifier)verifier;
		assertEquals(key, rsassaVerifier.getPublicKey());
	}


	public void testCreateRS384Verifier()
		throws Exception {

		JWSHeader header = new JWSHeader(JWSAlgorithm.RS384);
		Key key = generateRSAPublicKey();

		JWSVerifierFactory factory = new DefaultJWSVerifierFactory();

		JWSVerifier verifier = factory.createJWSVerifier(header, key);
		assertTrue(verifier.supportedJWSAlgorithms().contains(header.getAlgorithm()));

		RSASSAVerifier rsassaVerifier = (RSASSAVerifier)verifier;
		assertEquals(key, rsassaVerifier.getPublicKey());
	}


	public void testCreateRS512Verifier()
		throws Exception {

		JWSHeader header = new JWSHeader(JWSAlgorithm.RS512);
		Key key = generateRSAPublicKey();

		JWSVerifierFactory factory = new DefaultJWSVerifierFactory();

		JWSVerifier verifier = factory.createJWSVerifier(header, key);
		assertTrue(verifier.supportedJWSAlgorithms().contains(header.getAlgorithm()));

		RSASSAVerifier rsassaVerifier = (RSASSAVerifier)verifier;
		assertEquals(key, rsassaVerifier.getPublicKey());
	}


	public void testCreatePS256Verifier()
		throws Exception {

		JWSHeader header = new JWSHeader(JWSAlgorithm.PS256);
		Key key = generateRSAPublicKey();

		JWSVerifierFactory factory = new DefaultJWSVerifierFactory();

		JWSVerifier verifier = factory.createJWSVerifier(header, key);
		assertTrue(verifier.supportedJWSAlgorithms().contains(header.getAlgorithm()));

		RSASSAVerifier rsassaVerifier = (RSASSAVerifier)verifier;
		assertEquals(key, rsassaVerifier.getPublicKey());
	}


	public void testCreatePS384Verifier()
		throws Exception {

		JWSHeader header = new JWSHeader(JWSAlgorithm.PS384);
		Key key = generateRSAPublicKey();

		JWSVerifierFactory factory = new DefaultJWSVerifierFactory();

		JWSVerifier verifier = factory.createJWSVerifier(header, key);
		assertTrue(verifier.supportedJWSAlgorithms().contains(header.getAlgorithm()));

		RSASSAVerifier rsassaVerifier = (RSASSAVerifier)verifier;
		assertEquals(key, rsassaVerifier.getPublicKey());
	}


	public void testCreatePS512Verifier()
		throws Exception {

		JWSHeader header = new JWSHeader(JWSAlgorithm.PS512);
		Key key = generateRSAPublicKey();

		JWSVerifierFactory factory = new DefaultJWSVerifierFactory();

		JWSVerifier verifier = factory.createJWSVerifier(header, key);
		assertTrue(verifier.supportedJWSAlgorithms().contains(header.getAlgorithm()));

		RSASSAVerifier rsassaVerifier = (RSASSAVerifier)verifier;
		assertEquals(key, rsassaVerifier.getPublicKey());
	}


	public void testCreateRSVerifierWithInvalidKey()
		throws Exception {

		JWSHeader header = new JWSHeader(JWSAlgorithm.RS256);
		Key key = generateSharedKey(512);

		JWSVerifierFactory factory = new DefaultJWSVerifierFactory();

		try {
			factory.createJWSVerifier(header, key);
			fail();
		} catch (JOSEException e) {
			assertTrue(e instanceof KeyTypeException);
		}
	}


	private static ECPublicKey generateECPublicKey(final ECKey.Curve curve)
		throws Exception {

		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
		keyGen.initialize(curve.toECParameterSpec());
		return (ECPublicKey)keyGen.generateKeyPair().getPublic();
	}


	public void testCreateES256Verifier()
		throws Exception {

		JWSHeader header = new JWSHeader(JWSAlgorithm.ES256);
		Key key = generateECPublicKey(ECKey.Curve.P_256);

		JWSVerifierFactory factory = new DefaultJWSVerifierFactory();

		JWSVerifier verifier = factory.createJWSVerifier(header, key);
		assertTrue(verifier.supportedJWSAlgorithms().contains(header.getAlgorithm()));

		ECDSAVerifier ecdsaVerifier = (ECDSAVerifier)verifier;
		assertEquals(key, ecdsaVerifier.getPublicKey());
	}


	public void testCreateES384Verifier()
		throws Exception {

		JWSHeader header = new JWSHeader(JWSAlgorithm.ES384);
		Key key = generateECPublicKey(ECKey.Curve.P_384);

		JWSVerifierFactory factory = new DefaultJWSVerifierFactory();

		JWSVerifier verifier = factory.createJWSVerifier(header, key);
		assertTrue(verifier.supportedJWSAlgorithms().contains(header.getAlgorithm()));

		ECDSAVerifier ecdsaVerifier = (ECDSAVerifier)verifier;
		assertEquals(key, ecdsaVerifier.getPublicKey());
	}


	public void testCreateES512Verifier()
		throws Exception {

		JWSHeader header = new JWSHeader(JWSAlgorithm.ES512);
		Key key = generateECPublicKey(ECKey.Curve.P_521);

		JWSVerifierFactory factory = new DefaultJWSVerifierFactory();

		JWSVerifier verifier = factory.createJWSVerifier(header, key);
		assertTrue(verifier.supportedJWSAlgorithms().contains(header.getAlgorithm()));

		ECDSAVerifier ecdsaVerifier = (ECDSAVerifier)verifier;
		assertEquals(key, ecdsaVerifier.getPublicKey());
	}


	public void testCreateESVerifierWithInvalidKey()
		throws Exception {

		JWSHeader header = new JWSHeader(JWSAlgorithm.ES256);
		Key key = generateRSAPublicKey();

		JWSVerifierFactory factory = new DefaultJWSVerifierFactory();

		try {
			factory.createJWSVerifier(header, key);
			fail();
		} catch (JOSEException e) {
			assertTrue(e instanceof KeyTypeException);
		}
	}


	public void testWithUnsupportedAlgorithm()
		throws Exception {

		JWSHeader header = new JWSHeader(new JWSAlgorithm("xxx"));
		Key key = generateSharedKey(256);

		JWSVerifierFactory factory = new DefaultJWSVerifierFactory();

		try {
			factory.createJWSVerifier(header, key);
			fail();
		} catch (JOSEException e) {

			assertEquals("Unsupported JWS algorithm: xxx", e.getMessage());
		}
	}
}
