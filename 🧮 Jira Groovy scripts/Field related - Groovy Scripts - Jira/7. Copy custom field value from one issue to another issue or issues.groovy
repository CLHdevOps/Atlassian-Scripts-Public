//Author: Rinaldi Michael
//Created: 16th Feb 2023, 08:44 PM
//last Modified: 20th Feb 2022, 06:53 PM
//Reference: https://community.atlassian.com/t5/Jira-Software-questions/Scriptrunner-Set-Clear-Resolutions-Globally/qaq-p/2068841
//This activity will not send notifications. Line that accomplishes this is EventDispatchOption.DO_NOT_DISPATCH
    
import java.lang.String
import java.sql.Timestamp
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.*
import com.onresolve.scriptrunner.parameters.annotation.*
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.user.ApplicationUser
  
   
@CustomFieldPicker(description = '', label = '', multiple = false, placeholder = 'Select custom field')
CustomField customField
   
def issueManager = ComponentAccessor.issueManager
def issueLinkManager = ComponentAccessor.getIssueLinkManager()
    
@ShortTextInput(description = 'Provide issue key/s in a comma separated list', label = 'Issue Keys')
String issue
String[] issues = issue.split(',')
   
@ShortTextInput(description = 'Provide the issue key of the issue that has the value you want to use', label = 'Reference issue')
String referenceIssue
def referenceIssueCustomFieldValue = issueManager.getIssueByCurrentKey(referenceIssue).getCustomFieldValue(customField)
  
String printtext=""
def issueService = ComponentAccessor.issueService
  
@UserPicker(label = "Select the Actor", description = "Select an admin account. This user account will be displayed in the issue's activity")
ApplicationUser loggedInUser
   
 
printtext+="<b>Issues -> </b><br>"
for(int s=0;s<issues.size();s++)
{
    try
    {
    def issueKey = issueManager.getIssueByCurrentKey(issues[s])
    issueKey.setCustomFieldValue(customField,referenceIssueCustomFieldValue)
    issueManager.updateIssue(loggedInUser,issueKey,EventDispatchOption.DO_NOT_DISPATCH, false)
    issueKey.store()
    printtext+="<br>${issues[s]} <b>has been updated with the value</b> ${referenceIssueCustomFieldValue}."
    }
    catch(Exception ex)
    {
      printtext+="<br>Issue ${issues[s]} does not exist."
    }
}
  
return printtext
