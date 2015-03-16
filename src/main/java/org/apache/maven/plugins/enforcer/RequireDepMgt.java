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
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.project.MavenProject;

import java.util.*;

import static com.ceilfors.enforcer.rules.EnforcerRuleUtils.getMavenProject;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

/**
 * This rule verifies if dependency management is used.
 *
 * @author ceilfors
 */
@SuppressWarnings("UnusedDeclaration") // Plugin
public class RequireDepMgt extends AbstractStandardEnforcerRule {

    /**
     * If true, this rule will fail if the version managed by dependency management is being overridden.
     */
    public boolean checkVersion;
    /**
     * The list of artifact to be ignored in <tt>groupId:artifactId:type</tt> format.
     */
    public List<String> ignoreArtifacts = newArrayList();
    /**
     * Specify the scope to be ignored. By default ignoring test scope.
     */
    public List<String> ignoreScopes = newArrayList("test");
    
    /**
     * Ignore dependencies not managed by parent.
     */
    public boolean ignoreNonManaged; 
    

    @Override
    @SuppressWarnings("unchecked")
    public void execute(final EnforcerRuleHelper helper) throws EnforcerRuleException {
        final String newLine = System.getProperty("line.separator");

        MavenProject project = getMavenProject(helper);
        DependencyManagement depMgt = project.getDependencyManagement();
        Map<String, Dependency> depMgtMap =
                depMgt == null ? new HashMap<String, Dependency>() : getDependencyMap(depMgt.getDependencies());

        StringBuilder sb = new StringBuilder();
        List<Dependency> dependencies = project.getDependencies();
        for (Dependency dependency : dependencies) {
            if (ignoreScopes.contains(dependency.getScope())
                    || ignoreArtifacts.contains(dependency.getManagementKey())) {
                continue;
            }

            Dependency depMgtDependency = depMgtMap.get(dependency.getManagementKey());
            if (depMgtDependency == null && ignoreNonManaged == false) {
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
                    (getMessage() == null ? "Please update the dependency management." : getMessage()));
        }
    }

    /**
     * Converts a list of dependencies to a map.
     *
     * @param dependencies list of dependencies
     * @return dependency map with the dependency's <tt>management key</tt> as its key
     */
    private Map<String, Dependency> getDependencyMap(final List<Dependency> dependencies) {
        Map<String, Dependency> dependencyMap = newHashMap();
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
