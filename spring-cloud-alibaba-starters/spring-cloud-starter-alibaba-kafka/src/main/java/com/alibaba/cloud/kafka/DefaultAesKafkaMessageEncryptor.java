/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.cloud.kafka;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Default AES/GCM implementation for Kafka message encryption.
 *
 * @author Cursor
 */
public class DefaultAesKafkaMessageEncryptor implements AesKafkaMessageEncryptor {

	private static final String AES = "AES";

	private static final String TRANSFORMATION = "AES/GCM/NoPadding";

	private final SecureRandom secureRandom = new SecureRandom();

	private final SecretKeySpec secretKey;

	private final AesKafkaProperties properties;

	public DefaultAesKafkaMessageEncryptor(AesKafkaProperties properties) {
		Assert.notNull(properties, "AesKafkaProperties must not be null");
		this.properties = properties;
		this.secretKey = createSecretKey(properties);
	}

	@Override
	public String encrypt(String plainText) {
		if (plainText == null) {
			return null;
		}
		byte[] encrypted = encrypt(plainText.getBytes(properties.getCharset()));
		return Base64.getEncoder().encodeToString(encrypted);
	}

	@Override
	public String decrypt(String cipherText) {
		if (cipherText == null) {
			return null;
		}
		byte[] decoded = Base64.getDecoder().decode(cipherText);
		return new String(decrypt(decoded), properties.getCharset());
	}

	@Override
	public byte[] encrypt(byte[] plainText) {
		if (plainText == null) {
			return null;
		}
		try {
			byte[] iv = new byte[properties.getIvLength()];
			secureRandom.nextBytes(iv);
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey,
					new GCMParameterSpec(properties.getAuthenticationTagLength(), iv));
			byte[] encrypted = cipher.doFinal(plainText);
			byte[] result = new byte[iv.length + encrypted.length];
			System.arraycopy(iv, 0, result, 0, iv.length);
			System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);
			return result;
		}
		catch (GeneralSecurityException ex) {
			throw new IllegalStateException("Failed to encrypt Kafka message", ex);
		}
	}

	@Override
	public byte[] decrypt(byte[] cipherText) {
		if (cipherText == null) {
			return null;
		}
		try {
			int ivLength = properties.getIvLength();
			Assert.isTrue(cipherText.length > ivLength,
					"Cipher text must contain an initialization vector and payload");
			byte[] iv = Arrays.copyOfRange(cipherText, 0, ivLength);
			byte[] encrypted = Arrays.copyOfRange(cipherText, ivLength, cipherText.length);
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(Cipher.DECRYPT_MODE, secretKey,
					new GCMParameterSpec(properties.getAuthenticationTagLength(), iv));
			return cipher.doFinal(encrypted);
		}
		catch (GeneralSecurityException ex) {
			throw new IllegalStateException("Failed to decrypt Kafka message", ex);
		}
	}

	private SecretKeySpec createSecretKey(AesKafkaProperties properties) {
		Assert.hasText(properties.getSecretKey(),
				"spring.cloud.alibaba.kafka.aes.secret-key must not be empty");
		byte[] key = getKeyBytes(properties);
		Assert.isTrue(key.length == 16 || key.length == 24 || key.length == 32,
				"AES secret key must be 16, 24 or 32 bytes");
		return new SecretKeySpec(key, AES);
	}

	private byte[] getKeyBytes(AesKafkaProperties properties) {
		if (properties.getKeyEncoding() == AesKafkaProperties.KeyEncoding.BASE64) {
			return Base64.getDecoder().decode(properties.getSecretKey());
		}
		if (StringUtils.isEmpty(properties.getSecretKey())) {
			return new byte[0];
		}
		return properties.getSecretKey().getBytes(properties.getCharset());
	}

}
