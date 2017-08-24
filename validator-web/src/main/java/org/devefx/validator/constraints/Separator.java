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

package org.devefx.validator.constraints;

import org.devefx.validator.ConstraintValidator;
import org.devefx.validator.constraints.annotation.InitParam;
import org.devefx.validator.script.annotation.Script;
import org.devefx.validator.util.Assert;
import org.devefx.validator.util.StringUtils;

@Script
public class Separator implements ConstraintValidator {

    @InitParam
    private ConstraintValidator validator;
    @InitParam
    private String separator;
    @InitParam
    private boolean ignoreLastBlank;
    
    public Separator(ConstraintValidator validator, String separator) {
        this(validator, separator, true);
    }
    
    public Separator(ConstraintValidator validator, String separator, boolean ignoreLastBlank) {
        Assert.notNull(validator, "validator cannot be null.");
        Assert.hasLength(separator, "separator cannot be empty.");
        this.validator = validator;
        this.separator = separator;
        this.ignoreLastBlank = ignoreLastBlank;
    }
    
    @Override
    public boolean isValid(Object value) {
        if (value == null) {
            return true;
        }
        String stringValue = value.toString();
        if (ignoreLastBlank) {
            int pos = stringValue.lastIndexOf(separator);
            String lastString = stringValue.substring(pos + 1);
            if (!StringUtils.hasText(lastString)) {
                stringValue = stringValue.substring(0, pos);
            }
        }
        String[] subTexts = stringValue.split(separator);
        for (String subText : subTexts) {
            if (!validator.isValid(subText)) {
                return false;
            }
        }
        return true;
    }
}
