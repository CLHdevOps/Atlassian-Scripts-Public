//Author: Rinaldi Michael
//Created: 11th December 2022, 01:34 PM
//Last Modified: 6th June 2023, 01:21 PM
//Reference: N/A
  
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
@CustomFieldPicker(
    label = 'Custom Fields', description = 'Pick custom fields', placeholder='Select custom fields',
    multiple = true
)
List<CustomField> customFields
 
 
@ShortTextInput(description = "One custom field may have one or many contexts. Check the order in the custom fields configuration page and type in which context it is. If you want to modify the third context. Type in 3. Or for a comma list of different context numbers in order of custom field type in 2,1,3,5,6", label="Enter context number (comma list in order of custom fields selected)")
String contextNumbersInput
String[] contextNumbersArray = contextNumbersInput.split(',')
Integer[] contextNumbersArrayInteger = new Integer[contextNumbersArray.size()]
for(int i=0;i<contextNumbersArray.size();i++)
{
    contextNumbersArrayInteger[i]=contextNumbersArray[i].toInteger()-1
}
 
 
@ShortTextInput(description = 'Enter a comma separated list of project names', label = 'Provide a comma list of project names (Case sensitive, applies to all custom fields input)')
String projectsInput
String[] projects = projectsInput.split(',')
 
//Output variable
String printtext=""
 
//////////////////////////////////////////////////////////////////////////////////////////////////////
//Loop through the custom fields
for(int c=0;c<customFields.size();c++)
{
    printtext+="<h2>*******************${customFields[c]}*******************</h2><br>"
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    //Fetch the custom field context and configuration scheme
    FieldConfigScheme scheme = customFields[c].getConfigurationSchemes()[contextNumbersArrayInteger[c]]
 
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    printtext+= "Custom Field - <b>${customFields[c]}</b>"
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
            customFieldConfigSchemeManagerImpl.addAssociatedContexts(scheme,setOfContexts,customFields[c])
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
        printtext+="<br>${p+1}. Project Name: <b>${allProjectsInTheContext[p].getName()}</b>, Project Key: <b>${allProjectsInTheContext[p].getKey()}</b>"
    }
    printtext+="<br>"
}
  
return printtext
