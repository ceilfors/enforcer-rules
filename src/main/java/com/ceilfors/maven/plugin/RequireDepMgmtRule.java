package com.ceilfors.maven.plugin;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.execution.RuntimeInformation;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import java.util.List;
import java.util.Set;

/**
 * @author ceilfors
 */
public class RequireDepMgmtRule implements EnforcerRule {

    /**
     * Fail flag. Rule will fail when the flag is true.
     */
    private boolean fail;

    @Override
    public void execute(final EnforcerRuleHelper helper) throws EnforcerRuleException {
        Log log = helper.getLog();

        try {
            // get the various expressions out of the helper.
            MavenProject project = (MavenProject) helper.evaluate("${project}");
            MavenSession session = (MavenSession) helper.evaluate("${session}");
            String target = (String) helper.evaluate("${project.build.directory}");
            String artifactId = (String) helper.evaluate("${project.artifactId}");

            // retrieve any component out of the session directly
            ArtifactResolver resolver = (ArtifactResolver) helper.getComponent(ArtifactResolver.class);
            RuntimeInformation rti = (RuntimeInformation) helper.getComponent(RuntimeInformation.class);

            log.info("Retrieved Target Folder: " + target);
            log.info("Retrieved ArtifactId: " + artifactId);
            log.info("Retrieved Project: " + project);
            log.info("Retrieved RuntimeInfo: " + rti);
            log.info("Retrieved Session: " + session);
            log.info("Retrieved Resolver: " + resolver);

            Set<Artifact> artifacts = project.getDependencyArtifacts();
            for (Artifact artifact : artifacts) {
                log.info(artifact.toString());
            }

            List<Dependency> dependencies = project.getDependencies();
            for (Dependency dependency : dependencies) {
                log.info(dependency.toString());
            }

            if (this.fail) {
                throw new EnforcerRuleException("Failing because my param said so.");
            }
        } catch (ComponentLookupException e) {
            throw new EnforcerRuleException("Unable to lookup a component " + e.getLocalizedMessage(), e);
        } catch (ExpressionEvaluationException e) {
            throw new EnforcerRuleException("Unable to lookup an expression " + e.getLocalizedMessage(), e);
        }
    }

    @Override
    public boolean isCacheable() {
        return true;
    }

    @Override
    public boolean isResultValid(EnforcerRule enforcerRule) {
        return true;
    }

    @Override
    public String getCacheId() {
        return String.valueOf(fail ? 0 : 1);
    }
}
