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

package org.devefx.validator.http.extract;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devefx.validator.http.HttpServletRequestWrapper;
import org.devefx.validator.http.MediaType;
import org.devefx.validator.http.reader.HttpMessageReader;
import org.devefx.validator.util.LinkedMultiValueMap;

public class HttpMessageReaderExtractor implements RequestExtractor {
	
	private static final Log logger = LogFactory.getLog(HttpMessageReaderExtractor.class);

	private final List<HttpMessageReader<?>> messageReaders;
	
	private final Map<Class<?>, Object> defaultData = new HashMap<Class<?>, Object>(4);
	
	public HttpMessageReaderExtractor(List<HttpMessageReader<?>> messageReaders) {
		this.messageReaders = messageReaders;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T> T extractData(Class<T> requiredClass, HttpServletRequest request)
			throws IOException {
		HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(request);
		if (!requestWrapper.hasMessageBody() || requestWrapper.hasEmptyMessageBody()) {
			Object data = defaultData.get(requiredClass);
			if (data == null) {
				data = newInstance(requiredClass);
				defaultData.put(requiredClass, data);
			}
			return (T) data;
		}
		MediaType contentType = getContentType(requestWrapper);
		for (HttpMessageReader<?> messageReader : messageReaders) {
			if (requiredClass != null) {
				if (messageReader.canRead(requiredClass, contentType)) {
					if (logger.isDebugEnabled()) {
                        logger.debug("Reading [" + requiredClass.getName() + "] as \"" +
                                contentType + "\" using [" + messageReader + "]");
                    }
					return (T) messageReader.read((Class) requiredClass, requestWrapper);
				}
			}
		}
		throw new RuntimeException(
                "Could not extract response: no suitable HttpMessageReader found for response type [" +
                        requiredClass + "] and content type [" + contentType + "]");
	}

	private MediaType getContentType(HttpServletRequest request) {
		String contentType = request.getContentType();
		if (contentType == null) {
			if (logger.isTraceEnabled()) {
                logger.trace("No Content-Type header found, defaulting to application/octet-stream");
            }
			return MediaType.APPLICATION_OCTET_STREAM;
		}
		return MediaType.parseMediaType(contentType);
	}
	
	@SuppressWarnings("unchecked")
	private <T> T newInstance(Class<T> clazz){
		if (clazz.isAssignableFrom(LinkedMultiValueMap.class)) {
			return (T) new LinkedMultiValueMap<>();
		}
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			throw new IllegalArgumentException(clazz.getName() + " cannot be instantiated");
		}
	}
}
