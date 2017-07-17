/*
 * Copyright 2016-2017, Youqian Yue (devefx@163.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.devefx.validator.script.mapping;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.devefx.validator.Validation;
import org.devefx.validator.beans.Container;
import org.devefx.validator.beans.factory.InitializingBean;
import org.devefx.validator.script.annotation.ScriptMapping;
import org.devefx.validator.util.ScriptUtil;
import org.devefx.validator.util.StringUtils;

public class DefaultMappingManager implements MappingManager, InitializingBean {
	
	protected Map<String, Mapping> mappings = new HashMap<String, Mapping>();

	@Override
	public Collection<String> getMappingNames() {
		return mappings.keySet();
	}
	
	@Override
	public Mapping getMapping(String name) {
		return mappings.get(name);
	}
	
	@Override
	public void afterSetup(Container container) {
		Collection<Validation> validations = container.getBeansOfType(Validation.class).values();
		for (Validation validation : validations) {
			Class<? extends Validation> validationClass = validation.getClass();
			ScriptMapping mapping = validationClass.getAnnotation(ScriptMapping.class);
			if (mapping != null) {
				String name = StringUtils.clean(mapping.value());
				if (name == null) {
					name = validationClass.getSimpleName();
				}
				this.addMapping(name, validationClass);
			}
		}
	}
	
	public void addMapping(String name, Class<? extends Validation> validationClass) {
		if (!ScriptUtil.isValidScriptName(name)) {
			throw new IllegalArgumentException("Illegal mapping name.");
		}
		Mapping other = getMapping(name);
		if (other != null) {
			throw new IllegalArgumentException("mapping name " + name + " is used by 2 classes ("
					+ other.getValidationClass() + " and " + validationClass + ")");
		}
		mappings.put(name, new Mapping(validationClass));
	}
}
