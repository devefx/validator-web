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
public class ImageSize implements ConstraintValidator {

    @InitParam
    private int minWidth;
    @InitParam
    private int maxWidth;
    @InitParam
    private int minHeight;
    @InitParam
    private int maxHeight;
    
    public ImageSize(int maxWidth, int maxHeight) {
        this(1, maxWidth, 1, maxHeight);
    }
    
    public ImageSize(int minWidth, int maxWidth, int minHeight, int maxHeight) {
        this.minWidth = minWidth;
        this.maxWidth = maxWidth;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        validateParameters();
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
        int width = image.getWidth();
        int height = image.getHeight();
        return width >= minWidth && width <= maxWidth &&
                height >= minHeight && height <= maxHeight;
    }
    
    private void validateParameters() {
        if (minWidth < 1) {
            throw new IllegalArgumentException("The minWidth parameter cannot be less than 1.");
        }
        if (maxWidth < 1) {
            throw new IllegalArgumentException("The maxWidth parameter cannot be less than 1.");
        }
        if (minHeight < 1) {
            throw new IllegalArgumentException("The minHeight parameter cannot be less than 1.");
        }
        if (maxHeight < 1) {
            throw new IllegalArgumentException("The maxHeight parameter cannot be less than 1.");
        }
        if (maxWidth < minWidth) {
            throw new IllegalArgumentException("The width cannot be negative.");
        }
        if (maxHeight < minHeight) {
            throw new IllegalArgumentException("The height cannot be negative.");
        }
    }
}
