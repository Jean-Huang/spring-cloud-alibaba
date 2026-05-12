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

package com.alibaba.cloud.drools.model;

import com.alibaba.cloud.drools.publish.RulePublishResult;
import com.alibaba.cloud.drools.test.RuleTestResult;

/**
 * End-to-end result of compiling, testing, and publishing a rule set.
 *
 * @author Cursor
 */
public class DroolsRuleDeploymentResult {

	private final CompiledRuleSet compiledRuleSet;

	private final RuleTestResult testResult;

	private final RulePublishResult publishResult;

	public DroolsRuleDeploymentResult(CompiledRuleSet compiledRuleSet,
			RuleTestResult testResult, RulePublishResult publishResult) {
		this.compiledRuleSet = compiledRuleSet;
		this.testResult = testResult;
		this.publishResult = publishResult;
	}

	public CompiledRuleSet getCompiledRuleSet() {
		return compiledRuleSet;
	}

	public RuleTestResult getTestResult() {
		return testResult;
	}

	public RulePublishResult getPublishResult() {
		return publishResult;
	}

}
