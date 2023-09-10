////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Created - 9th June 2023, 11:25am
//Last Modified -
//Authors - Rinaldi Michael
//References -
//https://community.atlassian.com/t5/Jira-questions/how-to-use-script-runner-to-switch-project-workflow-scheme/qaq-p/667906
//https://global-confluence.randstadservices.com/pages/viewpage.action?pageId=6344051247
//https://docs.atlassian.com/software/jira/docs/api/7.1.9/com/atlassian/jira/workflow/WorkflowSchemeManager.html
//Script not yet tested. Method not ideal as Workflows are connected to too many moving parts.
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
import java.io.*
import java.util.*
import java.lang.*
import com.atlassian.jira.workflow.*
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.project.Project
import com.onresolve.scriptrunner.parameters.annotation.ProjectPicker
    
 
/////////////////////////////////////////////////////////////////////////////
//declare managers
def workflowManager = ComponentAccessor.getComponent(WorkflowManager)
def allWorkflowsInJira = workflowManager.getWorkflows()
def workflowSchemeManager = ComponentAccessor.getComponent(WorkflowSchemeManager)
def projectManager = ComponentAccessor.projectManager
String printtext=""
 
/////////////////////////////////////////////////////////////////////////////
//get inputs
@ProjectPicker(description = 'Select the project/s which you would like to modify the workflow scheme of.', label = 'Destination Project (Active or Archived)', includeArchived = true, multiple = true, placeholder = 'Select project')
List<Project> destProjectsInput
 
@ProjectPicker(description = 'This project contains the workflow scheme you require', label = 'Reference Project (Active or Archived)', includeArchived = true, multiple = false, placeholder = 'Select project')
Project sourceProjectInput
 
 
/////////////////////////////////////////////////////////////////////////////
//loop through all projects and modify the workflow scheme
for(int w=0;w<destProjectsInput.size();w++)
{
    def sourceWorkflowScheme = workflowSchemeManager.getSchemeFor(sourceProjectInput)
    def oldWorkflowScheme = workflowSchemeManager.getSchemeFor(destProjectsInput[w])
    workflowSchemeManager.addSchemeToProject(destProjectsInput[w],sourceWorkflowScheme)
    printtext+="<br>${w+1}. Workflow scheme for <b>${destProjectsInput[w].getName()}</b> has been modified from"
    printtext+="<b>${sourceWorkflowScheme.getName()}</b> to <b>${oldWorkflowScheme.getName()}</b>"
}
 
/////////////////////////////////////////////////////////////////////////////
//Output
return printtext
