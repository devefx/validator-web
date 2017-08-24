package org.devefx.validator.http.reader.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;

import org.devefx.validator.http.MediaType;
import org.devefx.validator.http.reader.AbstractHttpMessageReader;
import org.devefx.validator.http.reader.HttpMessageNotReadableException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonHttpMessageReader extends AbstractHttpMessageReader<Object> {

    protected Gson gson;
    
    public GsonHttpMessageReader() {
        this(new GsonBuilder());
    }
    
    public GsonHttpMessageReader(GsonBuilder builder) {
        super(MediaType.ALL);
        this.gson = builder.create();
    }
    
    @Override
    protected boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    protected Object readInternal(Class<? extends Object> clazz, HttpServletRequest request)
            throws IOException, HttpMessageNotReadableException {
        
        InputStream in = request.getInputStream();
        return gson.fromJson(new InputStreamReader(in), clazz);
    }
}
