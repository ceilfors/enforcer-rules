package org.apache.maven.plugins.enforcer;

import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;

import java.util.*;

/**
 * @author ceilfors
 */
public class RequireDepMgt extends AbstractStandardEnforcerRule {

    /**
     * If true, this rule will fail if the version managed by dependency management is being overridden.
     */
    public boolean checkVersion;
    /**
     * The list of artifact to be ignored in <tt>groupId:artifactId:type</tt> format.
     */
    public List<String> ignoreArtifacts = new ArrayList<String>();
    /**
     * Specify the scope to be ignored. By default ignoring test scope.
     */
    public List<String> ignoreScopes = Arrays.asList("test");

    @Override
    @SuppressWarnings("unchecked")
    public void execute(final EnforcerRuleHelper helper) throws EnforcerRuleException {
        final String newLine = System.getProperty("line.separator");

        try {
            MavenProject project = (MavenProject) helper.evaluate("${project}");
            DependencyManagement depMgt = project.getDependencyManagement();
            Map<String, Dependency> depMgtMap = getDependencyMap(depMgt.getDependencies());

            StringBuilder sb = new StringBuilder();
            List<Dependency> dependencies = project.getDependencies();
            for (Dependency dependency : dependencies) {
                if (ignoreScopes.contains(dependency.getScope())
                        || ignoreArtifacts.contains(dependency.getManagementKey())) {
                    continue;
                }

                Dependency depMgtDependency = depMgtMap.get(dependency.getManagementKey());
                if (depMgtDependency == null) {
                    sb.append(String.format("%s is not managed by dependency management",
                                dependency.getManagementKey()));
                    sb.append(newLine);
                } else {
                    if (checkVersion && depMgtDependency.getVersion() != null &&
                            !depMgtDependency.getVersion().equals(dependency.getVersion())) {
                        sb.append(String.format("%s version is with %s. Managed version is %s",
                                dependency.getManagementKey(), dependency.getVersion(), depMgtDependency.getVersion()));
                        sb.append(newLine);
                    }
                }
            }

            if (sb.length() != 0) {
                throw new EnforcerRuleException(sb.toString() +
                        (message == null ? "Please update the dependency management." : message));
            }

        } catch (ExpressionEvaluationException e) {
            throw new EnforcerRuleException("Unable to lookup an expression " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * Converts a list of dependencies to a map.
     *
     * @param dependencies list of dependencies
     * @return dependency map with the dependency's <tt>management key</tt> as its key
     */
    private Map<String, Dependency> getDependencyMap(final List<Dependency> dependencies) {
        Map<String, Dependency> dependencyMap = new HashMap<String, Dependency>();
        for (Dependency dependency : dependencies) {
            dependencyMap.put(dependency.getManagementKey(), dependency);
        }
        return dependencyMap;
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
        return String.valueOf(checkVersion ? 0 : 1);
    }
}
