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

package org.devefx.validator.constraints.multipart;

import org.devefx.validator.ConstraintValidator;
import org.devefx.validator.constraints.annotation.InitParam;
import org.devefx.validator.script.annotation.Script;
import org.devefx.validator.web.multipart.ImageMultipartFile;

@Script
public class ImageRatio implements ConstraintValidator {

    @InitParam
    private float ratio;
    
    public ImageRatio(float ratio) {
        this.ratio = ratio;
    }
    
    @Override
    public boolean isValid(Object value) {
        // null values are valid
        if (value == null) {
            return true;
        }
        // converter type
        ImageMultipartFile image;
        if (value instanceof ImageMultipartFile) {
            image = (ImageMultipartFile) value;
        } else {
            throw new IllegalArgumentException("Unsupported of type [" + value.getClass().getName() + "]");
        }
        float width = image.getWidth();
        float height = image.getHeight();
        return this.ratio == (width / height);
    }
}
