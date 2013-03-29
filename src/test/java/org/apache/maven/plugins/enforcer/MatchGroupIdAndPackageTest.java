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

import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MatchGroupIdAndPackageTest {
    MatchGroupIdAndPackage rule;

    @Test
    public void emptyGroupId_ok() throws EnforcerRuleException {
        // for now, will test with groupId == ""
        List<String> roots = new ArrayList<String>();
        roots.add(getClass().getResource("").getFile());
        EnforcerRuleHelper helper = createMockEnforcerRuleHelper(createMockMavenProject("", roots));
        rule = new MatchGroupIdAndPackage();
        rule.execute(helper);
    }

    @Test(expected = EnforcerRuleException.class)
    public void noMatchingDirectory_throws() throws EnforcerRuleException {
        List<String> roots = new ArrayList<String>();
        roots.add(getClass().getResource("").getFile());
        EnforcerRuleHelper helper = createMockEnforcerRuleHelper(createMockMavenProject("my.group", roots));
        rule = new MatchGroupIdAndPackage();
        rule.execute(helper);
    }

    private MavenProject createMockMavenProject(String groupId, List<String> roots) {
        MavenProject mavenProject = mock(MavenProject.class);
        when(mavenProject.getCompileSourceRoots()).thenReturn(roots);
        when(mavenProject.getGroupId()).thenReturn(groupId);
        return mavenProject;
    }

    private EnforcerRuleHelper createMockEnforcerRuleHelper(MavenProject mavenProject) {
        EnforcerRuleHelper enforcerRuleHelper = mock(EnforcerRuleHelper.class);
        try {
            when(enforcerRuleHelper.evaluate("${project}")).thenReturn(mavenProject);
        } catch (ExpressionEvaluationException e) {
            throw new RuntimeException(e);
        }
        return enforcerRuleHelper;
    }
}
