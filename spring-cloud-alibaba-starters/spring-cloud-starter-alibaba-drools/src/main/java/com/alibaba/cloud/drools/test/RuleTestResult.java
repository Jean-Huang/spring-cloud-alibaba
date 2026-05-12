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

package com.alibaba.cloud.drools.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Result returned after test facts are executed against compiled rules.
 *
 * @author Cursor
 */
public class RuleTestResult {

	private final int firedRules;

	private final List<String> firedRuleNames;

	private final List<Object> facts;

	public RuleTestResult(int firedRules, List<String> firedRuleNames,
			List<Object> facts) {
		this.firedRules = firedRules;
		this.firedRuleNames = Collections
				.unmodifiableList(new ArrayList<>(firedRuleNames));
		this.facts = Collections.unmodifiableList(new ArrayList<>(facts));
	}

	public int getFiredRules() {
		return firedRules;
	}

	public List<String> getFiredRuleNames() {
		return firedRuleNames;
	}

	public List<Object> getFacts() {
		return facts;
	}

}
