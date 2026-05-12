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

package com.alibaba.cloud.drools.publish;

import java.time.Instant;

/**
 * Publication result returned by a rule runtime platform.
 *
 * @author Cursor
 */
public class RulePublishResult {

	private final String ruleSetId;

	private final String version;

	private final boolean success;

	private final boolean skipped;

	private final String message;

	private final Instant publishedAt;

	public RulePublishResult(String ruleSetId, String version, boolean success,
			boolean skipped, String message, Instant publishedAt) {
		this.ruleSetId = ruleSetId;
		this.version = version;
		this.success = success;
		this.skipped = skipped;
		this.message = message;
		this.publishedAt = publishedAt;
	}

	public static RulePublishResult success(String ruleSetId, String version,
			String message) {
		return new RulePublishResult(ruleSetId, version, true, false, message,
				Instant.now());
	}

	public static RulePublishResult skipped(String ruleSetId, String version,
			String message) {
		return new RulePublishResult(ruleSetId, version, true, true, message,
				Instant.now());
	}

	public static RulePublishResult failure(String ruleSetId, String version,
			String message) {
		return new RulePublishResult(ruleSetId, version, false, false, message,
				Instant.now());
	}

	public String getRuleSetId() {
		return ruleSetId;
	}

	public String getVersion() {
		return version;
	}

	public boolean isSuccess() {
		return success;
	}

	public boolean isSkipped() {
		return skipped;
	}

	public String getMessage() {
		return message;
	}

	public Instant getPublishedAt() {
		return publishedAt;
	}

}
