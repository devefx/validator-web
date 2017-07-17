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

package org.devefx.validator.util;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

public class UriUtils {

	private static final String ALLOWED_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-_.!~*'()";
	
	/**
	 * 
	 * @param source the source string
	 * @param encoding the encoding of the source string
	 * @return the encoded URI
	 * @throws IllegalArgumentException when the given uri parameter is not a valid URI
	 */
	public static String encodeUriComponent(String source, String encoding)
			throws UnsupportedEncodingException {
		if (source == null) {
			return null;
		}
		Assert.hasLength(encoding, "Encoding must not be empty");
		byte[] bytes = encodeBytes(source.getBytes(encoding));
		return new String(bytes, "US-ASCII");
	}
	
	private static byte[] encodeBytes(byte[] source) {
		Assert.notNull(source, "Source must not be null");
		ByteArrayOutputStream bos = new ByteArrayOutputStream(source.length);
		for (byte b : source) {
			if (b < 0) {
				b += 256;
			}
			if (ALLOWED_CHARS.indexOf(b) != -1) {
				bos.write(b);
			}
			else {
				bos.write('%');
				char hex1 = Character.toUpperCase(Character.forDigit((b >> 4) & 0xF, 16));
				char hex2 = Character.toUpperCase(Character.forDigit(b & 0xF, 16));
				bos.write(hex1);
				bos.write(hex2);
			}
		}
		return bos.toByteArray();
	}
	
}
