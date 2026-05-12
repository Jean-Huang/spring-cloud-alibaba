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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.alibaba.cloud.drools.model.CompiledRuleSet;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.runtime.KieSession;

/**
 * Runs immediate test data through compiled Drools rules.
 *
 * @author Cursor
 */
public class DroolsRuleTester {

	public RuleTestResult test(CompiledRuleSet compiledRuleSet, Collection<?> facts) {
		return test(compiledRuleSet, facts, Collections.emptyMap());
	}

	public RuleTestResult test(CompiledRuleSet compiledRuleSet, Collection<?> facts,
			Map<String, Object> globals) {
		if (compiledRuleSet == null) {
			throw new IllegalArgumentException("Compiled rule set must not be null");
		}
		Collection<?> safeFacts = facts != null ? facts : Collections.emptyList();
		Map<String, Object> safeGlobals = globals != null ? globals
				: Collections.emptyMap();
		KieSession session = compiledRuleSet.newKieSession();
		List<String> firedRuleNames = new ArrayList<>();
		try {
			session.addEventListener(new DefaultAgendaEventListener() {
				@Override
				public void afterMatchFired(AfterMatchFiredEvent event) {
					firedRuleNames.add(event.getMatch().getRule().getName());
				}
			});
			for (Map.Entry<String, Object> entry : safeGlobals.entrySet()) {
				session.setGlobal(entry.getKey(), entry.getValue());
			}
			List<Object> insertedFacts = new ArrayList<>();
			for (Object fact : safeFacts) {
				session.insert(fact);
				insertedFacts.add(fact);
			}
			int firedRules = session.fireAllRules();
			return new RuleTestResult(firedRules, firedRuleNames, insertedFacts);
		}
		finally {
			session.dispose();
		}
	}

}
