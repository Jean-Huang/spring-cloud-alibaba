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

import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

/**
 * Rule set compiled by Drools and ready for test or publication.
 *
 * @author Cursor
 */
public class CompiledRuleSet {

	private final String id;

	private final String version;

	private final String packageName;

	private final String drl;

	private final KieBase kieBase;

	public CompiledRuleSet(String id, String version, String packageName, String drl,
			KieBase kieBase) {
		this.id = id;
		this.version = version;
		this.packageName = packageName;
		this.drl = drl;
		this.kieBase = kieBase;
	}

	public KieSession newKieSession() {
		return this.kieBase.newKieSession();
	}

	public String getId() {
		return id;
	}

	public String getVersion() {
		return version;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getDrl() {
		return drl;
	}

	public KieBase getKieBase() {
		return kieBase;
	}

}
