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

package org.devefx.validator.http.reader.multipart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devefx.validator.http.MediaType;
import org.devefx.validator.http.reader.FormHttpMessageReader;
import org.devefx.validator.http.reader.HttpMessageNotReadableException;
import org.devefx.validator.util.LinkedMultiValueMap;
import org.devefx.validator.util.MultiValueMap;

public class MultipartFormHttpMessageReader extends FormHttpMessageReader {
	
	private static final Log logger = LogFactory.getLog(MultipartFormHttpMessageReader.class);
	
	private static List<MediaType> imageMediaTypes;
	
	static {
		imageMediaTypes = new ArrayList<MediaType>(3);
		imageMediaTypes.add(MediaType.IMAGE_GIF);
		imageMediaTypes.add(MediaType.IMAGE_JPEG);
		imageMediaTypes.add(MediaType.IMAGE_PNG);
	}
	
	public MultipartFormHttpMessageReader() {
		this.supportedMediaTypes.add(MediaType.MULTIPART_FORM_DATA);
	}
	
	@Override
	public MultiValueMap<String, ?> read(Class<? extends MultiValueMap<String, ?>> clazz,
			HttpServletRequest request) throws IOException, HttpMessageNotReadableException {
		
		MultiValueMap<String, Object> result = new LinkedMultiValueMap<>();
		extractUrlParams(result, request);
		
		if (ServletFileUpload.isMultipartContent(request)) {
			DiskFileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload fileUpload = new ServletFileUpload(factory);
			try {
				List<FileItem> fileItems = fileUpload.parseRequest(request);
				for (FileItem fileItem : fileItems) {
					if (fileItem.isFormField()) {
						result.add(fileItem.getFieldName(), fileItem.getString(this.charset.name()));
					} else {
						if (isImageMediaType(fileItem.getContentType())) {
							result.add(fileItem.getFieldName(), new CommonsImageMultipartFile(fileItem));
						} else {
							result.add(fileItem.getFieldName(), new CommonsMultipartFile(fileItem));
						}
					}
				}
			} catch (FileUploadException e) {
				logger.error("File upload failed", e);
			}
		}
		return result;
	}
	
	private boolean isImageMediaType(String contentType) {
		if (contentType == null) {
			return false;
		}
		MediaType type = MediaType.parseMediaType(contentType);
		for (MediaType mediaType : imageMediaTypes) {
			if (mediaType.includes(type)) {
				return true;
			}
		}
		return false;
	}
}
