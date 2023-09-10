//Author: Rinaldi Michael
//last Modified: 14th Nov 2022, 9:16 PM
//Reference: https://community.atlassian.com/t5/Jira-Software-questions/Scriptrunner-Set-Clear-Resolutions-Globally/qaq-p/2068841
 
import java.sql.Timestamp
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.*

//issue will be fetched from automation rule trigger
def issueKeys = issue as MutableIssue

def issueManager = ComponentAccessor.issueManager
def issueService = ComponentAccessor.issueService
def loggedInUser = ComponentAccessor.jiraAuthenticationContext.loggedInUser

//set resolution date to current time
issueKeys.setResolutionDate(new Timestamp(System.currentTimeMillis()))

//set resolution date to issue's last updated time
//issueKeys.setResolutionDate(issueKeys.getUpdated())

issueKeys.store()
