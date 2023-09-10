//Author: Rinaldi Michael
//Created: 11th December 2022, 01:07 PM
//Last Modified: 2nd June 2023, 01:15 PM
//Reference: N/A
/*
This script will allow you to provide a comma separated list of projects to add to a Custom field's context.i.e. Add projects to one custom field's context
Inputs: The Custom Field, Context number, Project names
*/
  
import java.io.*
import java.lang.String
import java.lang.*
import java.sql.Timestamp
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.*
import com.onresolve.scriptrunner.parameters.annotation.*
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.*
import com.atlassian.jira.issue.fields.config.manager.FieldConfigManager
import com.atlassian.jira.issue.fields.config.manager.FieldConfigSchemeManagerImpl
import com.atlassian.jira.project.Project
import com.onresolve.scriptrunner.parameters.annotation.ProjectPicker
import com.atlassian.jira.issue.fields.config.FieldConfigScheme
import com.atlassian.jira.issue.customfields.*
import com.atlassian.jira.issue.context.*
import com.atlassian.jira.issue.context.JiraContextNode
  
  
//////////////////////////////////////////////////////////////////////////////////////////////////////
//Variable declarations
def issueManager = ComponentAccessor.issueManager
def customFieldManager = ComponentAccessor.customFieldManager
def customFieldConfigManager = ComponentAccessor.getComponent(FieldConfigManager)
def customFieldConfigSchemeManagerImpl = ComponentAccessor.getComponent(FieldConfigSchemeManagerImpl)
def projectManagerVariable = ComponentAccessor.projectManager
def issueService = ComponentAccessor.issueService
def loggedInUser = ComponentAccessor.jiraAuthenticationContext.loggedInUser
    
//////////////////////////////////////////////////////////////////////////////////////////////////////
//for script console input
@CustomFieldPicker(label = 'Custom Field', description = 'Pick a custom field', placeholder='Select custom field')
CustomField customField1
  
@NumberInput(label = 'Enter context number', description = 'One custom field may have one or many contexts. Check the order in the custom fields configuration page and type in which context it is. If you want to modify the third context. Type in 3.')
Integer contextNumberInt
contextNumberInt-=1
 
@ShortTextInput(description = 'Enter a comma separated list of project names (case sensitive)', label = 'Provide a comma list of project names')
String string
String[] projects = string.split(',')
 
 
//////////////////////////////////////////////////////////////////////////////////////////////////////
//Fetch the custom field context and configuration scheme
FieldConfigScheme scheme = customField1.getConfigurationSchemes()[contextNumberInt]
 
//////////////////////////////////////////////////////////////////////////////////////////////////////
//Output variable
String printtext=""
printtext+= "Custom Field - <b>${customField1}</b>"
printtext+= "<br>Context is <b>${scheme.name}</b><br><br>"
 
//////////////////////////////////////////////////////////////////////////////////////////////////////
//add the projects provided to the custom field context
for(int i=0;i<projects.size();)
{
    try
    {
    scheme.contexts[i].projectId = projectManagerVariable.getProjectObjByName(projects[i]).getId()
 
    Set<JiraContextNode> setOfContexts = new HashSet<JiraContextNode>();
    for(int s=0;s<scheme.contexts.size();s++)
    {
        setOfContexts.add(scheme.contexts[s])
    }
    customFieldConfigSchemeManagerImpl.addAssociatedContexts(scheme,setOfContexts,customField1)
    }
    catch(Exception ex)
    {
        printtext+="<br><b>Please confirm the input provied. Errors are</b><br>${ex.getMessage()}<br><br>"
    }
i++
}
 
 
//////////////////////////////////////////////////////////////////////////////////////////////////////
//Print out the final list of projects in the custom field's context
printtext+="<b>Project List</b>"
printtext+="<br>The list tends to be inaccurate. Which is why Jira always asks admins to reindex the instance after modifying a custom field. Run the script again with the same inputs to view the accurate list.<br>"
def allProjectsInTheContext = scheme.associatedProjectObjects
for(int p=0;p<allProjectsInTheContext.size();p++)
{
    printtext+="<br>${p+1}. Project Name: <b>${allProjectsInTheContext[p].getName()}</b>, Project Key, <b>${allProjectsInTheContext[p].getKey()}</b>"
}
 
  
return printtext
