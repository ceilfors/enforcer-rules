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

import org.apache.commons.lang.StringUtils;
import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.project.MavenProject;

import static com.ceilfors.enforcer.rules.EnforcerRuleUtils.getMavenProject;

/**
 * This rule verifies that a pom's artifact id is the same with it's base directory name.
 * @author ceilfors
 */
@SuppressWarnings("UnusedDeclaration") // Plugin
public class MatchArtifactIdAndBaseDir extends AbstractStandardEnforcerRule {

    @Override
    public void execute(final EnforcerRuleHelper helper) throws EnforcerRuleException {
        MavenProject mavenProject = getMavenProject(helper);
        String baseDir = mavenProject.getBasedir().getName();
        String artifactId = mavenProject.getArtifactId();
        String difference = StringUtils.difference(baseDir, artifactId);
        if (!difference.isEmpty()) {
            String template = "Artifact id: [%s] is not the same with base dir: [%s]. Difference is started at: [%s]";
            throw new EnforcerRuleException(String.format(template, artifactId, baseDir, difference));
        }
    }

    @Override
    public boolean isCacheable() {
        return false;
    }

    @Override
    public boolean isResultValid(EnforcerRule enforcerRule) {
        return false;
    }

    @Override
    public String getCacheId() {
        return "0";
    }
}
