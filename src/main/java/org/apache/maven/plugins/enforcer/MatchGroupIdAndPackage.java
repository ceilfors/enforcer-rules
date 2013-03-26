package org.apache.maven.plugins.enforcer;

import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;

import java.io.File;

/**
 * Rule that fails a build if module does not contain a Java package (i.e. directory) that matches the module's groupId.
 */
public class MatchGroupIdAndPackage extends AbstractStandardEnforcerRule {
    public static final String RULE_NAME = "matchGroupIdAndPackage";


    @Override
    public void execute(EnforcerRuleHelper enforcerRuleHelper) throws EnforcerRuleException {
        try {
            MavenProject project = (MavenProject) enforcerRuleHelper.evaluate("${project}");
            String groupId = project.getGroupId();

            for (Object src : project.getCompileSourceRoots()) {
                File dir = new File(src + File.separator + groupId.replace('.', File.separatorChar));
                if (dir.exists()) {
                    // PASSED
                    return;
                }
            }

            // FAILED
            throw new EnforcerRuleException("No matching Java package for groupId [" + groupId + "]");

        } catch (ExpressionEvaluationException e) {
            throw new EnforcerRuleException("Error processing enforcer rule [" + RULE_NAME + "]", e);
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
        return "not-cached";
    }
}
