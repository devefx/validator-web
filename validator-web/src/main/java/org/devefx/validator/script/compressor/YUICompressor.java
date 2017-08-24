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

package org.devefx.validator.script.compressor;

import java.io.StringReader;
import java.io.StringWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devefx.validator.script.Compressor;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

public class YUICompressor implements Compressor {
    
    private static final Log log = LogFactory.getLog(YUICompressor.class);
    
    private static final Integer DEFAULT_LINEBREAK = 20000;

    private int linebreak = DEFAULT_LINEBREAK;
    
    private boolean munge;
    
    private boolean verbose;
    
    private boolean preserveAllSemiColons;
    
    private boolean disableOptimizations;
    
    public YUICompressor() {
        this(DEFAULT_LINEBREAK, false, false, false, false);
    }
    
    public YUICompressor(int linebreak, boolean munge, boolean verbose, boolean preserveAllSemiColons, boolean disableOptimizations) {
        this.linebreak = linebreak;
        this.munge = munge;
        this.verbose = verbose;
        this.preserveAllSemiColons = preserveAllSemiColons;
        this.disableOptimizations = disableOptimizations;
    }
    
    public void setLinebreak(int linebreak) {
        this.linebreak = linebreak;
    }
    
    public void setMunge(boolean munge) {
        this.munge = munge;
    }
    
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
    
    public void setPreserveAllSemiColons(boolean preserveAllSemiColons) {
        this.preserveAllSemiColons = preserveAllSemiColons;
    }
    
    public void setDisableOptimizations(boolean disableOptimizations) {
        this.disableOptimizations = disableOptimizations;
    }
    
    @Override
    public String compressJavaScript(String script) throws Exception {
        StringReader stringReader = new StringReader(script);
        JavaScriptCompressor javaScriptCompressor = new JavaScriptCompressor(stringReader, new YUIErrorReporter());
        StringWriter stringWriter = new StringWriter();
        javaScriptCompressor.compress(stringWriter, linebreak, munge, verbose, preserveAllSemiColons, disableOptimizations);
        String compressedScript = stringWriter.toString();
        return compressedScript;
    }
    
    protected static class YUIErrorReporter implements ErrorReporter {
        @Override
        public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {
            if (line < 0) {
                log.warn(message);
            } else {
                log.error("\n" + line + ':' + lineOffset + ':' + message);
            }
        }
        
        @Override
        public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {
            if (line < 0) {
                log.error(message);
            } else {
                log.error(line + ':' + lineOffset + ':' + message);
            }
        }

        @Override
        public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource, int lineOffset) {
            error(message, sourceName, line, lineSource, lineOffset);
            return new EvaluatorException(message);
        }
    }
}
