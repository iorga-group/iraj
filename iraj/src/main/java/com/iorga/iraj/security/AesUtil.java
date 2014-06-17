package com.iorga.iraj.security;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

// based on https://github.com/mpetersen/aes-example/blob/master/src/main/java/org/cloudme/sample/aes/AesUtil.java
public class AesUtil {
	private final int keySize;
	private final int iterationCount;
	private final Cipher cipher;

	public AesUtil(final int keySize, final int iterationCount) {
		this.keySize = keySize;
		this.iterationCount = iterationCount;
		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		} catch (final NoSuchAlgorithmException e) {
			throw fail(e);
		} catch (final NoSuchPaddingException e) {
			throw fail(e);
		}
	}

	public String encrypt(final String salt, final String iv, final String passphrase, final String plaintext) {
		try {
			final SecretKey key = generateKey(salt, passphrase);
			final byte[] encrypted = doFinal(Cipher.ENCRYPT_MODE, key, iv, plaintext.getBytes("UTF-8"));
			return base64(encrypted);
		} catch (final UnsupportedEncodingException e) {
			throw fail(e);
		}
	}

	public String decrypt(final String salt, final String iv, final String passphrase, final String ciphertext) {
		try {
			final SecretKey key = generateKey(salt, passphrase);
			final byte[] decrypted = doFinal(Cipher.DECRYPT_MODE, key, iv, base64(ciphertext));
			return new String(decrypted, "UTF-8");
		} catch (final UnsupportedEncodingException e) {
			throw fail(e);
		}
	}

	private byte[] doFinal(final int encryptMode, final SecretKey key, final String iv, final byte[] bytes) {
		try {
			cipher.init(encryptMode, key, new IvParameterSpec(hex(iv)));
			return cipher.doFinal(bytes);
		} catch (final InvalidKeyException e) {
			throw fail(e);
		} catch (final InvalidAlgorithmParameterException e) {
			throw fail(e);
		} catch (final IllegalBlockSizeException e) {
			throw fail(e);
		} catch (final BadPaddingException e) {
			throw fail(e);
		}
	}

	private SecretKey generateKey(final String salt, final String passphrase) {
		try {
			final SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			final KeySpec spec = new PBEKeySpec(passphrase.toCharArray(), hex(salt), iterationCount, keySize);
			final SecretKey key = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
			return key;
		} catch (final NoSuchAlgorithmException e) {
			throw fail(e);
		} catch (final InvalidKeySpecException e) {
			throw fail(e);
		}
	}

	public static String generateIV() {
		return random(16);
	}

	public static String generateSalt() {
		return random(32);
	}

	public static String random(final int length) {
		final byte[] salt = new byte[length];
		new SecureRandom().nextBytes(salt);
		return hex(salt);
	}

	public static String base64(final byte[] bytes) {
		return DatatypeConverter.printBase64Binary(bytes);
	}

	public static byte[] base64(final String str) {
		return DatatypeConverter.parseBase64Binary(str);
	}

	public static String hex(final byte[] bytes) {
		return DatatypeConverter.printHexBinary(bytes);
	}

	public static byte[] hex(final String str) {
		return DatatypeConverter.parseHexBinary(str);
	}

	private IllegalStateException fail(final Exception e) {
		return new IllegalStateException(e);
	}
}
