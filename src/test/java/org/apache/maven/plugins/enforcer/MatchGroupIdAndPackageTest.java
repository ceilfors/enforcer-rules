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
