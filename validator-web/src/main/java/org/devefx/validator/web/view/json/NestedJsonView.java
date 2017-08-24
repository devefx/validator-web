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

package org.devefx.validator.web.view.json;

import java.io.IOException;
import java.io.OutputStream;

import org.devefx.validator.util.JsonWriter;
import org.devefx.validator.util.StringUtils;
import org.devefx.validator.web.view.AbstractNestedView;

public class NestedJsonView extends AbstractNestedView {
    
    public NestedJsonView() {
        setContentType("application/json;charset=UTF-8");
    }
    
    /**
     * Write the actual JSON content to the stream.
     * @param stream the output stream to use
     * @param value the value to be rendered, as returned from {@link #filterModel}
     * @throws IOException if writing failed
     */
    protected void writeContent(OutputStream stream, Object value) throws IOException {
        String jsonStr = JsonWriter.toJson(value);
        if (StringUtils.hasText(jsonStr)) {
            stream.write(jsonStr.getBytes(getCharset()));
        }
    }
}
