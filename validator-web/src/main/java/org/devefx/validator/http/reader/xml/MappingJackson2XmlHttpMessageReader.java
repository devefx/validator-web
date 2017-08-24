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

package org.devefx.validator.http.reader.xml;

import org.devefx.validator.http.MediaType;
import org.devefx.validator.http.reader.json.AbstractJackson2HttpMessageReader;
import org.devefx.validator.util.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class MappingJackson2XmlHttpMessageReader extends AbstractJackson2HttpMessageReader {

    public MappingJackson2XmlHttpMessageReader() {
        this(new XmlMapper());
    }
    
    public MappingJackson2XmlHttpMessageReader(ObjectMapper objectMapper) {
        super(objectMapper, new MediaType("application", "xml", DEFAULT_CHARSET),
                new MediaType("text", "xml", DEFAULT_CHARSET),
                new MediaType("application", "*+xml", DEFAULT_CHARSET));
        Assert.isInstanceOf(XmlMapper.class, objectMapper, "XmlMapper required");
    }
}
