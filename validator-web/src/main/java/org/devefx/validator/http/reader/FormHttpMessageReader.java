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

package org.devefx.validator.http.reader;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.devefx.validator.http.MediaType;
import org.devefx.validator.util.LinkedMultiValueMap;
import org.devefx.validator.util.MultiValueMap;
import org.devefx.validator.util.ServletUtils;
import org.devefx.validator.util.StreamUtils;
import org.devefx.validator.util.StringUtils;

public class FormHttpMessageReader implements HttpMessageReader<MultiValueMap<String, ?>> {

    protected Charset charset = Charset.forName("UTF-8");
    
    protected List<MediaType> supportedMediaTypes = new ArrayList<MediaType>();
    
    public FormHttpMessageReader() {
        this.supportedMediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
    }
    
    /**
     * Sets the character set used for reading form data.
     */
    public void setCharset(Charset charset) {
        this.charset = charset;
    }
    
    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        if (!MultiValueMap.class.isAssignableFrom(clazz)) {
            return false;
        }
        if (mediaType == null) {
            return true;
        }
        for (MediaType supportedMediaType : getSupportedMediaTypes()) {
            if (supportedMediaType.includes(mediaType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Set the list of {@link MediaType} objects supported by this converter.
     */
    public void setSupportedMediaTypes(List<MediaType> supportedMediaTypes) {
        this.supportedMediaTypes = supportedMediaTypes;
    }
    
    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return Collections.unmodifiableList(this.supportedMediaTypes);
    }

    @Override
    public MultiValueMap<String, ?> read(Class<? extends MultiValueMap<String, ?>> clazz,
            HttpServletRequest request) throws IOException, HttpMessageNotReadableException {
        
        MediaType contentType = MediaType.parseMediaType(request.getContentType());
        Charset charset = contentType.getCharSet() != null ? contentType.getCharSet() : this.charset;
        String body = StreamUtils.copyToString(request.getInputStream(), charset);
        
        String[] pairs = StringUtils.tokenizeToStringArray(body, "&");
        
        MultiValueMap<String, String> result = new LinkedMultiValueMap<>(pairs.length);
        ServletUtils.extractUrlParams(result, request);
        
        for (String pair : pairs) {
            int idx = pair.indexOf('=');
            if (idx == -1) {
                result.add(URLDecoder.decode(pair, charset.name()), null);
            }
            else {
                String name = URLDecoder.decode(pair.substring(0, idx), charset.name());
                String value = URLDecoder.decode(pair.substring(idx + 1), charset.name());
                result.add(name, value);
            }
        }
        return result;
    }
}
