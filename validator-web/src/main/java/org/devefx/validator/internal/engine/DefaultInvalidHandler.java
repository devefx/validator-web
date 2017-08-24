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

package org.devefx.validator.internal.engine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.devefx.validator.ConstraintViolation;
import org.devefx.validator.InvalidHandler;
import org.devefx.validator.internal.util.ResultResponse;
import org.devefx.validator.web.View;
import org.devefx.validator.web.view.AbstractNestedView;
import org.devefx.validator.web.view.json.NestedJsonView;
import org.devefx.validator.web.view.xml.NestedXmlView;

public class DefaultInvalidHandler implements InvalidHandler {

    /** output style */
    public enum OutputStyle {
        JSON, XML
    }
    
    private OutputStyle outputStyle = OutputStyle.JSON;
    
    public void setOutputStyle(OutputStyle outputStyle) {
        this.outputStyle = outputStyle;
    }
    
    @Override
    public View renderInvalid(List<ConstraintViolation> violations) {
        if (violations == null || violations.isEmpty()) {
            return null;
        }
        AbstractNestedView view = null;
        if (this.outputStyle == OutputStyle.JSON) {
            view = new NestedJsonView();
        } else if (this.outputStyle == OutputStyle.XML) {
            view = new NestedXmlView();
        }
        
        Map<String, String> errorMap = new HashMap<>(violations.size());
        for (ConstraintViolation violation: violations) {
            String name = violation.getName();
            if (!errorMap.containsKey(name)) {
                errorMap.put(violation.getName(), violation.getMessage());
            }
        }
        
        ResultResponse<?> response = new ResultResponse<>(NOT_MATCH_CONSTRAINTS, errorMap);
        view.addStaticAttribute("response", response);
        view.setExtractValueFromSingleKeyModel(true);
        return view;
    }
}
