////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Created - 25th April 2023, 01:18 pm
//Last Modified - 25th April 2023, 02:45 pm
//Author - Rinaldi Michael
//References -
//https://community.atlassian.com/t5/Answers-Developer-Questions/How-to-change-issue-type-scheme-in-JIRA-7-x-programatically/qaq-p/489936
//https://docs.atlassian.com/software/jira/docs/api/7.6.1/index.html?com/atlassian/jira/issue/fields/FieldManager.html
//https://searchcode.com/file/133460664/
//https://docs.atlassian.com/software/jira/docs/api/7.0.5/com/atlassian/jira/issue/customfields/CustomFieldUtils.html
/*
Note: The below script will change the issue type scheme of archived projects as well.
If the project is active and the issue type of issues need to be mapped. Use the Jira automation to bulk change the issue type/s of issues.

If the project is archived and the issue type of issues need to be mapped. Use script 'Archive/Restore a list of projects in Jira' to quickly Restore the project/s. 
And then use the Jira automation to bulk change the issue type/s of issues. 
*/
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      
import java.io.*
import java.util.*
import java.lang.*
import com.onresolve.scriptrunner.db.DatabaseUtil
import com.onresolve.scriptrunner.parameters.annotation.*
import com.atlassian.jira.project.Project
import com.atlassian.jira.issue.fields.config.manager.*
import com.atlassian.jira.issue.fields.config.*
import com.atlassian.jira.issue.fields.*
import com.atlassian.jira.issue.customfields.option.*
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.web.action.admin.issuetypes.*
import com.atlassian.jira.web.action.admin.*
import com.atlassian.jira.issue.customfields.*
import com.atlassian.jira.issue.IssueFieldConstants
import com.atlassian.jira.issue.fields.config.manager.*
import com.onresolve.scriptrunner.parameters.annotation.IssueTypeSchemePicker.*
       
 
//////////////////////////////////////////////////////////////////////////////////////////////////////
//Declare managers    
FieldConfigSchemeManager fieldConfigSchemeManager = ComponentAccessor.getFieldConfigSchemeManager();
def projectManager = ComponentAccessor.getProjectManager();
def issueTypeSchemeManager = ComponentAccessor.getComponent(IssueTypeSchemeManager)
FieldManager fieldManager = ComponentAccessor.getFieldManager();
  
 
//////////////////////////////////////////////////////////////////////////////////////////////////////
//Get inputs
@ShortTextInput(description = 'Enter a comma separated list of Project names. Since project names are what is visible in the Issue Type Schemes page', label = 'List of Project Names')
String projectNamesInput
String[] projectNames = projectNamesInput.split(',')
  
@ShortTextInput(description = 'Enter the Issue Type Scheme\'s ID to which you want the project to switch to. This can be found in the URL of the Issue Type Scheme\'s Edit screen', label = 'Issue Type Scheme ID')
String fieldConfigSchemeID
Long fieldConfigSchemeIDlong = fieldConfigSchemeID.toLong()
  
String printtext=""
FieldConfigScheme fieldConfigScheme = fieldConfigSchemeManager.getFieldConfigScheme(fieldConfigSchemeIDlong);
def newIssueTypeName = fieldConfigScheme.getName()
 
 
 
//////////////////////////////////////////////////////////////////////////////////////////////////////
//Get a list of project ID's
def existingProjectsWithIssueTypeScheme = fieldConfigScheme.getAssociatedProjectObjects()
Long[] projectIds = new Long[projectNames.size()+existingProjectsWithIssueTypeScheme.size()];
int i
for(i=0;i<projectNames.size();i++)  //project list provided by user
{
    Project project = projectManager.getProjectObjByName(projectNames[i])
    projectIds[i]=project.getId();
}
for(int j=0;j<existingProjectsWithIssueTypeScheme.size();j++) //current project list from the issue type scheme
{
    projectIds[j+i]=existingProjectsWithIssueTypeScheme[j].getId();
}
 
//////////////////////////////////////////////////////////////////////////////////////////////////////
//Format output of the old list of projects in the issue type scheme
printtext+="<h2>Old list of projects from the issue type scheme: <b>${newIssueTypeName}</b></h2><br>"
for(int p=0;p<existingProjectsWithIssueTypeScheme.size();p++)
{
    printtext+="<br>${p+1}. <b>${existingProjectsWithIssueTypeScheme[p].getName()}</b> with Key ${existingProjectsWithIssueTypeScheme[p].getKey()}"
}
printtext+="<br><br><br>/////////////////////////////////////////////////////////////////////////////////////////////////////////////"
 
  
//////////////////////////////////////////////////////////////////////////////////////////////////////
//Format output of the project's old issue type schemes
printtext+="<br><h2>******************** Old issue type schemes of each project ********************</h2>"
for(int is=0;is<projectNames.size();is++)
{
    Project project = projectManager.getProjectObjByName(projectNames[is])
    String oldIssueTypeScheme = issueTypeSchemeManager.getConfigScheme(project).getName()
    printtext+="<br>${is+1}. <b>${project.getName()}</b> with Key <b>${project.getKey()}</b> had the issue type scheme <b>${oldIssueTypeScheme}</b>"
}
 
//////////////////////////////////////////////////////////////////////////////////////////////////////
//Add projects to the issue type scheme
List contexts = CustomFieldUtils.buildJiraIssueContexts(false, projectIds, projectManager);
ConfigurableField configurableField = fieldManager.getConfigurableField(IssueFieldConstants.ISSUE_TYPE);
def results = fieldConfigSchemeManager.updateFieldConfigScheme(fieldConfigScheme, contexts, configurableField);
 
 
 
//////////////////////////////////////////////////////////////////////////////////////////////////////
//Format output of the project's new issue type schemes
printtext+="<br><br><h2>******************** New issue type schemes of each project ********************</h2>"
for(int is=0;is<projectNames.size();is++)
{
    Project project = projectManager.getProjectObjByName(projectNames[is])
    String newIssueTypeScheme = issueTypeSchemeManager.getConfigScheme(project).getName()
    printtext+="<br>${is+1}. <b>${project.getName()}</b> with Key <b>${project.getKey()}</b> now has the issue type scheme <b>${newIssueTypeScheme}</b>"
}
 
//////////////////////////////////////////////////////////////////////////////////////////////////////
//Format output of the new list of projects in the issue type scheme
fieldConfigScheme = fieldConfigSchemeManager.getFieldConfigScheme(fieldConfigSchemeIDlong)
def newProjectsWithIssueTypeScheme = fieldConfigScheme.getAssociatedProjectObjects()
printtext+="<br><br><br>/////////////////////////////////////////////////////////////////////////////////////////////////////////////"
printtext+="<h2>New list of projects from the issue type scheme: <b>${newIssueTypeName}</b></h2><br>"
for(int p=0;p<newProjectsWithIssueTypeScheme.size();p++)
{
    printtext+="<br>${p+1}. <b>${newProjectsWithIssueTypeScheme[p].getName()}</b> with Key ${newProjectsWithIssueTypeScheme[p].getKey()}"
}
  
return printtext
