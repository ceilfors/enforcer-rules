/*
 * Copyright (c) 2012 Wisen Tanasa
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.apache.maven.plugins.enforcer;

import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.plugin.logging.Log;

import java.io.*;

/**
 * This rule verifies if the files listed contains the specified content.
 *
 * @author ceilfors
 */
@SuppressWarnings("UnusedDeclaration") // Plugin
public class RequireFilesContent extends AbstractRequireFiles {

    String content;

    private String errorMsg;

    private Log log;

    @Override
    public void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {
        if (content == null) {
            throw new EnforcerRuleException("content is mandatory");
        }
        this.log = helper.getLog();
        super.execute(helper);
    }

    @Override
    boolean checkFile(File file) {
        if (file == null) {
            return true; // if we get here and it's null, treat it as a success; follow other implementation
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while((line = reader.readLine()) != null) {
                if (line.contains(content)) {
                    return true;
                }
            }
            reader.close();

        } catch (FileNotFoundException e) {
            this.errorMsg = file + " does not exist!";
            return false;
        } catch (IOException e) {
            this.log.error(e);
            this.errorMsg = file + " throws IOException!";
            return false;
        }

        this.errorMsg = file + " doesn't contain: " + content;
        return false;
    }


    @Override
    String getErrorMsg() {
        return this.errorMsg;
    }

    @Override
    public boolean isCacheable() {
        return false;
    }

    @Override
    public boolean isResultValid(EnforcerRule cachedRule) {
        return false;
    }
}
