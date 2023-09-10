//Author: Rinaldi Michael
//last Modified: 26th May 2023, 06:33pm
//Reference: https://community.atlassian.com/t5/Jira-Software-questions/Scriptrunner-Set-Clear-Resolutions-Globally/qaq-p/2068841
//This is not a thoroughly thought out script. Working with the Built In Script's version will be ideal
   
import java.sql.Timestamp
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.*
import com.onresolve.scriptrunner.parameters.annotation.*
import com.atlassian.jira.issue.fields.CustomField
  
def issueManager = ComponentAccessor.issueManager
   
//for specific issues
@ShortTextInput(label = "Enter the Issue Key", description = "Enter the issue Key you would want to copy the custom field value from.")
String issueKey
def issueKeys = issueManager.getIssueByCurrentKey("issueKey")
   
//for use in automation rules
//def issueKeys = issue as MutableIssue
   
def issueService = ComponentAccessor.issueService
def loggedInUser = ComponentAccessor.jiraAuthenticationContext.loggedInUser
   
  
//for script console input
@CustomFieldPicker(label = 'Old Custom Field 1', description = 'Pick a custom field', placeholder='Select custom field')
CustomField customField1
     
@CustomFieldPicker(label = 'New Custom Field', description = 'Pick a custom field', placeholder='Select custom field')
CustomField customField2
  
//for pre-defined custom field input
//CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
//def customField1 = customFieldManager.getCustomFieldObjectsByNameIgnoreCase("CustomFieldName1")
//def customField2 = customFieldManager.getCustomFieldObjectsByNameIgnoreCase("CustomFieldName2")
  
issueKeys.setCustomFieldValue(customField2,customField1)
  
issueKeys.store()
