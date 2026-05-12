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

package com.alibaba.cloud.drools;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.alibaba.cloud.drools.compile.DroolsRuleCompiler;
import com.alibaba.cloud.drools.model.CompiledRuleSet;
import com.alibaba.cloud.drools.model.DroolsRuleDeploymentResult;
import com.alibaba.cloud.drools.model.RuleSetDefinition;
import com.alibaba.cloud.drools.publish.DroolsRulePublisher;
import com.alibaba.cloud.drools.publish.RulePublishResult;
import com.alibaba.cloud.drools.test.DroolsRuleTester;
import com.alibaba.cloud.drools.test.RuleTestResult;

/**
 * Facade for building, compiling, testing, and publishing Drools rule sets.
 *
 * @author Cursor
 */
public class DroolsRuleService {

	private final DroolsRuleCompiler ruleCompiler;

	private final DroolsRuleTester ruleTester;

	private final DroolsRulePublisher rulePublisher;

	public DroolsRuleService(DroolsRuleCompiler ruleCompiler,
			DroolsRuleTester ruleTester, DroolsRulePublisher rulePublisher) {
		this.ruleCompiler = ruleCompiler;
		this.ruleTester = ruleTester;
		this.rulePublisher = rulePublisher;
	}

	public CompiledRuleSet compile(RuleSetDefinition ruleSetDefinition) {
		return this.ruleCompiler.compile(ruleSetDefinition);
	}

	public RuleTestResult test(CompiledRuleSet compiledRuleSet, Collection<?> facts) {
		return this.ruleTester.test(compiledRuleSet, facts);
	}

	public RuleTestResult test(CompiledRuleSet compiledRuleSet, Collection<?> facts,
			Map<String, Object> globals) {
		return this.ruleTester.test(compiledRuleSet, facts, globals);
	}

	public RulePublishResult publish(CompiledRuleSet compiledRuleSet) {
		return this.rulePublisher.publish(compiledRuleSet);
	}

	public DroolsRuleDeploymentResult compileTestAndPublish(
			RuleSetDefinition ruleSetDefinition, Collection<?> testFacts) {
		return compileTestAndPublish(ruleSetDefinition, testFacts, Collections.emptyMap());
	}

	public DroolsRuleDeploymentResult compileTestAndPublish(
			RuleSetDefinition ruleSetDefinition, Collection<?> testFacts,
			Map<String, Object> globals) {
		CompiledRuleSet compiledRuleSet = compile(ruleSetDefinition);
		RuleTestResult testResult = test(compiledRuleSet, testFacts, globals);
		RulePublishResult publishResult = publish(compiledRuleSet);
		return new DroolsRuleDeploymentResult(compiledRuleSet, testResult,
				publishResult);
	}

}
