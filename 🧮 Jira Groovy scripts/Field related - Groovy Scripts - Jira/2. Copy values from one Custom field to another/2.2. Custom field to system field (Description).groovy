//Author: Rinaldi Michael
//last Modified: 25th Nov 2022, 08:20 PM
//Reference: https://community.atlassian.com/t5/Jira-Software-questions/Scriptrunner-Set-Clear-Resolutions-Globally/qaq-p/2068841
    
import java.sql.Timestamp
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.*
import com.onresolve.scriptrunner.parameters.annotation.*
import com.atlassian.jira.issue.fields.CustomField
   
def issueManager = ComponentAccessor.issueManager
    
//for specific issues
//def issueKeys = issueManager.getIssueByCurrentKey("DF172-566")
    
//for use in automation rules
def issueKeys = issue as MutableIssue
    
def issueService = ComponentAccessor.issueService
def loggedInUser = ComponentAccessor.jiraAuthenticationContext.loggedInUser
    
   
//for script console input
@CustomFieldPicker(label = 'Old Custom Field 1', description = 'Pick a custom field you want to copy the value to a description from.', placeholder='Select custom field')
CustomField customField1
      
//@CustomFieldPicker(label = 'New Custom Field', description = 'Pick a custom field', placeholder='Select custom field')
//CustomField customField2
   
//for pre-defined custom field input
//CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
//def customField1 = customFieldManager.getCustomFieldObjectsByNameIgnoreCase("CustomFieldName1")
//def customField2 = customFieldManager.getCustomFieldObjectsByNameIgnoreCase("CustomFieldName2")
   
//issueKeys.setCustomFieldValue(customField2,customField1)
  
String newDescription = issueKeys.getDescription() + issueKeys.getCustomFieldValue(customField1)
  
issueKeys.setDescription(newDescription)
   
issueKeys.store()
