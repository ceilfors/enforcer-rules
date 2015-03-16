/*
 * Copyright (c) 2013 Wisen Tanasa
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

import java.io.*;

/**
 * This rule verifies if the files listed contains the specified content. It doesn't extends
 * <tt>AbstractRequireFiles</tt> because it doesn't handle multiple file's error messages as intended.
 *
 * @author ceilfors
 */
@SuppressWarnings("UnusedDeclaration") // Plugin
public class RequireFilesContent extends AbstractStandardEnforcerRule {

    /**
     * The content that must be inside the files.
     */
    public String content;

    /**
     * Files to be checked.
     */
    public File[] files;

    /**
     * If null file handles should be allowed. If they are allowed, it means treat it as a success.
     */
    public boolean allowNulls = false; // Adopted from other RequireFiles implementation

    @Override
    public void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {
        checkNotNull(files, "file is mandatory");
        checkNotNull(content, "content is mandatory");
        checkArgument(files.length > 0, "at least 1 file must be specified");

        String newLine = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder();

        for (File file : files) {
            Result result = checkFile(helper, file);
            if (!result.successful) {
                sb.append(file.getAbsolutePath()).append(" : ").append(result.errorMessage);
                sb.append(newLine);
            }
        }

        if (sb.length() != 0) {
            throw new EnforcerRuleException(sb.toString() +
                    (getMessage() != null ? getMessage() :
                            "Some files produce errors, please check the error message for the individual file above."));
        }
    }

    protected static void checkNotNull(final Object reference, String errorMessage) {
        if (reference == null) {
            throw new NullPointerException(errorMessage);
        }
    }

    protected static void checkArgument(final boolean condition, String errorMessage) throws EnforcerRuleException {
        if (!condition) {
            throw new EnforcerRuleException(errorMessage);
        }
    }

    protected Result checkFile(EnforcerRuleHelper helper, File file) {
        if (file == null) {
            if (allowNulls) {
                return Result.success();
            } else {
                return Result.fail("Empty file name was given and allowNulls is set to false");
            }
        }

        if (file.isFile()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains(content)) {
                        return Result.success();
                    }
                }

                return Result.fail(String.format("Doesn't contain: \"%s\"", content));
            } catch (FileNotFoundException e) {
                return Result.fail("File doesn't exist");
            } catch (IOException e) {
                helper.getLog().error(e);
                return Result.fail("IOException was thrown, please check the log.");
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        helper.getLog().error(e);
                    }
                }
            }
        } else {
            return Result.fail("Not a file");
        }
    }

    /**
     * The checkFile() result object.
     */
    protected static class Result {

        /**
         * True if the check result is successful; otherwise false.
         */
        public boolean successful;
        /**
         * The error message if the result is NOT successful; otherwise empty string.
         */
        public String errorMessage = "";

        /**
         * Creates successful result object.
         *
         * @return the newly created result object
         */
        public static Result success() {
            return new Result(true, "");
        }

        /**
         * Creates successful result object.
         *
         * @return the newly created result object
         */
        public static Result fail(String errorMessage) {
            return new Result(false, errorMessage);
        }

        /**
         * @param successful   true if the check result is successful; otherwise false
         * @param errorMessage the error message if the result is NOT successful; otherwise empty string
         */
        private Result(final boolean successful, final String errorMessage) {
            this.successful = successful;
            this.errorMessage = errorMessage;
        }
    }

    @Override
    public boolean isCacheable() {
        return false;
    }

    @Override
    public boolean isResultValid(EnforcerRule cachedRule) {
        return false;
    }

    @Override
    public String getCacheId() {
        return "0";
    }
}
