//Author: Rinaldi Michael
//last Modified: 12th Dec 2022, 12:56 pm
//Reference: https://community.atlassian.com/t5/Jira-Software-questions/Scriptrunner-Set-Clear-Resolutions-Globally/qaq-p/2068841
   
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.*
import java.lang.*
import java.io.*
 
def issueManager = ComponentAccessor.issueManager
   
//for specific issues
//def subTaskissueKeys = issueManager.getIssueByCurrentKey("<type in issue key here>")
//def parentTaskIssueKey = subTaskissueKeys.getParentObject()
  
   
//for use in automation rules
def subTaskissueKeys = issue as MutableIssue
def parentTaskIssueKey = subTaskissueKeys.getParentObject()
   
def issueService = ComponentAccessor.issueService
def loggedInUser = ComponentAccessor.jiraAuthenticationContext.loggedInUser
   
Long timeSpentHolder = 0
 
if (parentTaskIssueKey.getTimeSpent()==null)
    parentTaskIssueKey.setTimeSpent(timeSpentHolder)
 
def TimeSpent = parentTaskIssueKey.getTimeSpent()+subTaskissueKeys.getTimeSpent()
parentTaskIssueKey.setTimeSpent(TimeSpent)
 
     
parentTaskIssueKey.store()
