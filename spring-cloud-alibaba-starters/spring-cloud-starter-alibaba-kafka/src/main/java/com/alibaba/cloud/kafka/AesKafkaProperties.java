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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for AES encrypted Kafka messages.
 *
 * @author Cursor
 */
@ConfigurationProperties(prefix = AesKafkaProperties.PREFIX)
public class AesKafkaProperties {

	public static final String PREFIX = "spring.cloud.alibaba.kafka.aes";

	private boolean enabled = true;

	private String secretKey;

	private KeyEncoding keyEncoding = KeyEncoding.RAW;

	private Charset charset = StandardCharsets.UTF_8;

	private int ivLength = 12;

	private int authenticationTagLength = 128;

	private String encryptedHeader = "x-kafka-aes-encrypted";

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public KeyEncoding getKeyEncoding() {
		return keyEncoding;
	}

	public void setKeyEncoding(KeyEncoding keyEncoding) {
		this.keyEncoding = keyEncoding;
	}

	public Charset getCharset() {
		return charset;
	}

	public void setCharset(Charset charset) {
		this.charset = charset;
	}

	public int getIvLength() {
		return ivLength;
	}

	public void setIvLength(int ivLength) {
		this.ivLength = ivLength;
	}

	public int getAuthenticationTagLength() {
		return authenticationTagLength;
	}

	public void setAuthenticationTagLength(int authenticationTagLength) {
		this.authenticationTagLength = authenticationTagLength;
	}

	public String getEncryptedHeader() {
		return encryptedHeader;
	}

	public void setEncryptedHeader(String encryptedHeader) {
		this.encryptedHeader = encryptedHeader;
	}

	public enum KeyEncoding {

		RAW, BASE64

	}

}
