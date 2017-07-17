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

package org.devefx.validator.internal.engine.groups;

import org.devefx.validator.groups.Default;

public class Group {
	public static final Group DEFAULT_GROUP = new Group(Default.class);
	
	/**
	 * The actual group.
	 */
	private Class<?> group;
	
	public Group(Class<?> group) {
		this.group = group;
	}

	public Class<?> getDefiningClass() {
		return group;
	}
	
	public boolean isDefaultGroup() {
		return getDefiningClass().getName().equals(Default.class.getName());
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Group group1 = (Group) o;
		if (group != null ? !group.equals(group1.group) : group1.group != null) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return group != null ? group.hashCode() : 0;
	}
	
	@Override
	public String toString() {
		return "Group{" + "group=" + group.getName() + '}';
	}
}
