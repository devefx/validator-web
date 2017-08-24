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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class DefaultValidationOrder implements ValidationOrder {
    
    /**
     * The list of single groups to be used this validation.
     */
    private List<Group> groupList = new ArrayList<>();

    @Override
    public Iterator<Group> getGroupIterator() {
        return groupList.iterator();
    }
    
    public void insertGroup(Group group) {
        if (!groupList.contains(group)) {
            groupList.add(group);
        }
    }
}
