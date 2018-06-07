package org.springframework.roo.bnd.workspace;

import org.springframework.roo.model.JavaPackage;

public interface BndOperations extends ProjectOperations {

	void createWorkspace();

	void createWorkspaceFromGitTemplate(String workspaceName, String template, Integer majorJavaVersion);

	void createProject(JavaPackage topLevelPackage, String projectName);

	void createProjectFromGitTemplate(JavaPackage topLevelPackage, String projectName);

	String getWorkspaceRoot();


	boolean isCreateWorkspaceAvailable();

	boolean isCreateProjectAvailable();
}
