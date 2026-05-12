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

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link DefaultAesKafkaMessageEncryptor}.
 *
 * @author Cursor
 */
public class DefaultAesKafkaMessageEncryptorTests {

	@Test
	public void shouldEncryptAndDecryptStringPayload() {
		DefaultAesKafkaMessageEncryptor encryptor = new DefaultAesKafkaMessageEncryptor(
				properties("1234567890123456"));

		String encrypted = encryptor.encrypt("hello kafka");

		assertThat(encrypted).isNotEqualTo("hello kafka");
		assertThat(encryptor.decrypt(encrypted)).isEqualTo("hello kafka");
	}

	@Test
	public void shouldUseRandomInitializationVector() {
		DefaultAesKafkaMessageEncryptor encryptor = new DefaultAesKafkaMessageEncryptor(
				properties("1234567890123456"));

		String first = encryptor.encrypt("hello kafka");
		String second = encryptor.encrypt("hello kafka");

		assertThat(first).isNotEqualTo(second);
		assertThat(encryptor.decrypt(first)).isEqualTo("hello kafka");
		assertThat(encryptor.decrypt(second)).isEqualTo("hello kafka");
	}

	@Test
	public void shouldEncryptAndDecryptBytes() {
		DefaultAesKafkaMessageEncryptor encryptor = new DefaultAesKafkaMessageEncryptor(
				properties("1234567890123456"));
		byte[] payload = "binary kafka".getBytes(StandardCharsets.UTF_8);

		byte[] encrypted = encryptor.encrypt(payload);

		assertThat(encrypted).isNotEqualTo(payload);
		assertThat(encryptor.decrypt(encrypted)).isEqualTo(payload);
	}

	@Test
	public void shouldSupportBase64EncodedKey() {
		String secretKey = Base64.getEncoder()
				.encodeToString("1234567890123456".getBytes(StandardCharsets.UTF_8));
		AesKafkaProperties properties = properties(secretKey);
		properties.setKeyEncoding(AesKafkaProperties.KeyEncoding.BASE64);
		DefaultAesKafkaMessageEncryptor encryptor = new DefaultAesKafkaMessageEncryptor(
				properties);

		String encrypted = encryptor.encrypt("hello kafka");

		assertThat(encryptor.decrypt(encrypted)).isEqualTo("hello kafka");
	}

	@Test
	public void shouldRejectInvalidKeyLength() {
		assertThatThrownBy(() -> new DefaultAesKafkaMessageEncryptor(properties("short")))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("AES secret key");
	}

	private AesKafkaProperties properties(String secretKey) {
		AesKafkaProperties properties = new AesKafkaProperties();
		properties.setSecretKey(secretKey);
		return properties;
	}

}
