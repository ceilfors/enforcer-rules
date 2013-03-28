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
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * TODO: too tedious to test many scenarios without mocking library
 */
public class MatchGroupIdAndPackageTest {
    MatchGroupIdAndPackage rule;

    @Test
    public void emptyGroupId_ok() throws EnforcerRuleException {
        // for now, will test with groupId == ""
        List<String> roots = new ArrayList<String>();
        roots.add(getClass().getResource("").getFile());
        EnforcerRuleHelper helper = new MavenProjectEnforcerRuleHelper(new MyMavenProject("", roots));
        rule = new MatchGroupIdAndPackage();
        rule.execute(helper);
    }

    @Test(expected = EnforcerRuleException.class)
    public void noMatchingDirectory_throws() throws EnforcerRuleException {
        List<String> roots = new ArrayList<String>();
        roots.add(getClass().getResource("").getFile());
        EnforcerRuleHelper helper = new MavenProjectEnforcerRuleHelper(new MyMavenProject("my.group", roots));
        rule = new MatchGroupIdAndPackage();
        rule.execute(helper);
    }
}

class MyMavenProject extends MavenProject {
    MyMavenProject(String groupId, List<String> compileSourceRoots) {
        setCompileSourceRoots(compileSourceRoots);
        setGroupId(groupId);
    }
}

// TODO: add mocking library & mock this
class MavenProjectEnforcerRuleHelper implements EnforcerRuleHelper {
    MavenProject mavenProject;

    MavenProjectEnforcerRuleHelper(MavenProject mavenProject) {
        this.mavenProject = mavenProject;
    }

    @Override
    public Log getLog() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getComponent(Class clazz) throws ComponentLookupException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getComponent(String componentKey) throws ComponentLookupException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getComponent(String role, String roleHint) throws ComponentLookupException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map getComponentMap(String role) throws ComponentLookupException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List getComponentList(String role) throws ComponentLookupException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PlexusContainer getContainer() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object evaluate(String s) throws ExpressionEvaluationException {
        // always return maven project! (not good - should be mocked)
        return mavenProject;
    }

    @Override
    public File alignToBaseDirectory(File file) {
        throw new UnsupportedOperationException();
    }
}
