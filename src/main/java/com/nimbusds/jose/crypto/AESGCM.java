package com.nimbusds.jose.crypto;


import javax.crypto.SecretKey;

import net.jcip.annotations.Immutable;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;

import com.nimbusds.jose.JOSEException;


/**
 * Constants and static methods for AES/GSM/NoPadding encryption and 
 * decryption. Uses the BouncyCastle.org provider.
 *
 * <p>See draft-ietf-jose-json-web-algorithms-08, section 4.9.
 *
 * @author Vladimir Dzhuvinov
 * @author Axel Nennker
 * @version $version$ (2013-03-22)
 */
class AESGCM {


	/**
	 * The standard Initialisation Vector (IV) length (96 bits).
	 */
	public static final int IV_BIT_LENGTH = 96;


	/**
	 * The standard authentication tag length (128 bits).
	 */
	public static final int AUTH_TAG_BIT_LENGTH = 128;


	/**
	 * Encapsulates the result of an AES GCM encryption. This class is 
	 * immutable.
	 */
	@Immutable
	public static class Result {


		/**
		 * The cipher text.
		 */
		private final byte[] cipherText;


		/**
		 * The authentication tag.
		 */
		private final byte[] authTag;


		/**
		 * Creates a new AES GCM encryption result.
		 *
		 * @param cipherText The cipher text. Must not be {@code null}.
		 * @param authTag    The authentication tag. Must not be
		 *                   {@code null}.
		 */
		public Result(final byte[] cipherText, final byte[] authTag) {

			this.cipherText = cipherText;

			this.authTag = authTag;
		}


		/**
		 * Gets the cipher text.
		 *
		 * @return The cipher text.
		 */
		public byte[] getCipherText() {

			return cipherText;
		}


		/**
		 * Gets the authentication tag.
		 *
		 * @return The authentication tag.
		 */
		public byte[] getAuthenticationTag() {

			return authTag;
		}
	}


	/**
	 * Creates a new AES / GCM cipher.
	 *
	 * @param secretKey     The AES key. Must not be {@code null}.
	 * @param forEncryption If {@code true} creates an AES / GCM encryption
	 *                      cipher, else creates an AES / GCM decryption 
	 *                      cipher.
	 * @param iv            The initialisation vector (IV). Must not be
	 *                      {@code null}.
	 * @param authData      The authenticated data. Must not be 
	 *                      {@code null}.
	 *
	 * @return The AES / GCM cipher.
	 */
	private static GCMBlockCipher createAESGCMCipher(final SecretKey secretKey,
		                                         final boolean forEncryption,
		                                         final byte[] iv,
		                                         final byte[] authData) {

		// Initialise AES cipher
		BlockCipher cipher = AES.createAESCipher(secretKey, forEncryption);

		// Create GCM cipher with AES
		GCMBlockCipher gcm = new GCMBlockCipher(cipher);

		AEADParameters aeadParams = new AEADParameters(new KeyParameter(secretKey.getEncoded()), 
			                                       AUTH_TAG_BIT_LENGTH, 
			                                       iv, 
			                                       authData);
		gcm.init(forEncryption, aeadParams);

		return gcm;
	}


	/**
	 * Encrypts the specified plain text using AES / GCM.
	 *
	 * @param secretKey The AES key. Must not be {@code null}.
	 * @param plainText The plain text. Must not be {@code null}.
	 * @param authData  The authenticated data. Must not be {@code null}.
	 * @param iv        The initialisation vector (IV). Must not be
	 *                  {@code null}.
	 *
	 * @return The encryption result, consisting of the cipher text and the
	 *         authentication tag.
	 *
	 * @throws JOSEException If encryption failed.
	 */
	public static Result encrypt(final SecretKey secretKey, 
		                     final byte[] plainText, 
		                     final byte[] authData,
		                     final byte[] iv)
		throws JOSEException {

		// Initialise AES/GCM cipher for encryption
		GCMBlockCipher cipher = createAESGCMCipher(secretKey, true, iv, authData);


		// Prepare output buffer
		int outputLength = cipher.getOutputSize(plainText.length);
		byte[] output = new byte[outputLength];


		// Produce cipher text
		int outputOffset = cipher.processBytes(plainText, 0, plainText.length, output, 0);


		// Produce authentication tag
		try {
			outputOffset += cipher.doFinal(output, outputOffset);

		} catch (InvalidCipherTextException e) {

			throw new JOSEException("Couldn't generate GCM authentication tag: " + e.getMessage(), e);
		}

		// Split output into cipher text and authentication tag
		int authTagLength = AUTH_TAG_BIT_LENGTH / 8;

		byte[] cipherText = new byte[outputOffset - authTagLength];
		byte[] authTag = new byte[authTagLength];

		System.arraycopy(output, 0, cipherText, 0, cipherText.length);
		System.arraycopy(output, outputOffset - authTagLength, authTag, 0, authTag.length);

		return new Result(cipherText, authTag);
	}


	/**
	 * Decrypts the specified cipher text using AES / GCM.
	 *
	 * @param secretKey  The AES key. Must not be {@code null}.
	 * @param cipherText The cipher text. Must not be {@code null}.
	 * @param authData   The authenticated data. Must not be {@code null}.
	 * @param authTag    The authentication tag. Must not be {@code null}.
	 * @param iv         The initialisation vector (IV). Must not be
	 *                   {@code null}.
	 *
	 * @return The decrypted plain text.
	 *
	 * @throws JOSEException If decryption failed.
	 */
	public static byte[] decrypt(final SecretKey secretKey, 
		                     final byte[] cipherText,
		                     final byte[] authData,
		                     final byte[] authTag,
		                     final byte[] iv)
		throws JOSEException {

		// Initialise AES/GCM cipher for decryption
		GCMBlockCipher cipher = createAESGCMCipher(secretKey, false, iv, authData);


		// Join cipher text and authentication tag to produce cipher input
		byte[] input = new byte[cipherText.length + authTag.length];

		System.arraycopy(cipherText, 0, input, 0, cipherText.length);
		System.arraycopy(authTag, 0, input, cipherText.length, authTag.length);

		int outputLength = cipher.getOutputSize(input.length);

		byte[] output = new byte[outputLength];


		// Decrypt
		int outputOffset = cipher.processBytes(input, 0, input.length, output, 0);

		// Validate authentication tag
		try {
			outputOffset += cipher.doFinal(output, outputOffset);
				
		} catch (InvalidCipherTextException e) {

			throw new JOSEException("Couldn't validate GCM authentication tag: " + e.getMessage(), e);
		}

		return output;
	}


	/**
	 * Prevents public instantiation.
	 */
	private AESGCM() { }
}