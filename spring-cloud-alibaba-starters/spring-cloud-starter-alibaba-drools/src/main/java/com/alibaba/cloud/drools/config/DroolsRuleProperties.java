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

package com.alibaba.cloud.drools.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Drools rule building.
 *
 * @author Cursor
 */
@ConfigurationProperties(prefix = "spring.cloud.alibaba.drools")
public class DroolsRuleProperties {

	/**
	 * Whether Drools rule services are enabled.
	 */
	private boolean enabled = true;

	/**
	 * Package name used when a rule set does not declare its own package.
	 */
	private String defaultPackageName = "com.alibaba.cloud.drools.rules";

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getDefaultPackageName() {
		return defaultPackageName;
	}

	public void setDefaultPackageName(String defaultPackageName) {
		this.defaultPackageName = defaultPackageName;
	}

}
