A) Selecting workspace template
bndtools.core:
 	
^
|  uses 
|
  org.bndtools.core.ui.wizards.shared.TemplateSelectionWizardPage
^
|
| looks-up and calls TemplateLoader service(s) on findTemplates()
|
org.bndtools.templating.gitrepo:
 org.bndtools.templating.jgit.GitHubWorkspaceTemplateLoader implements TemplateLoader
^
|
| findTemplates()
|
org.bndtools.templating.jgit.GitCloneTemplate
^
|
|  ResourceMap generateOutputs() calls CloneCommand cloneCmd = Git.cloneRepository()
| 
bndtools.wizards.workspace.WorkspacePreviewPage extends WizardPage 
^
|
|  Calls 
