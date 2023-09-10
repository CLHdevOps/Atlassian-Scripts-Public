//Author: Rinaldi Michael
//last Modified: 17th Nov 2022, 9:16 PM
//Reference: https://community.atlassian.com/t5/Jira-Software-questions/Scriptrunner-Set-Clear-Resolutions-Globally/qaq-p/2068841
//This script will help remove any resolution in an issue which is currently not possible in Jira.
//This is useful when tickets are reopened but the resolution does not automatically set to unresolved. The below script can be run in the console or used in an automation rule.
 
import java.sql.Timestamp
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.*
   
def issueManager = ComponentAccessor.issueManager
 
//for specific issues
def issueKeys = issueManager.getIssueByCurrentKey("<type in issue key>")
 
//for use in automation rules
//def issueKeys = issue as MutableIssue
 
def issueService = ComponentAccessor.issueService
def loggedInUser = ComponentAccessor.jiraAuthenticationContext.loggedInUser
 
issueKeys.setResolution(null)
issueKeys.setResolutionDate(null)
   
issueKeys.store()
