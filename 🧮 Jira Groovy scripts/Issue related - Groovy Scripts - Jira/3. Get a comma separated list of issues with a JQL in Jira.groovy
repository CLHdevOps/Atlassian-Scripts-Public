/////////////////////////////////////////////////
//////////Author: Rinaldi Michael////////////////
/////Created: 22/02/2023, 09:44 pm///////////////
////Last Modified: 22/02/2023, 09:44 pm///////////
//reference 1: https://docs.atlassian.com/software/jira/docs/api/8.1.0/com/atlassian/jira/bc/issue/search/SearchService.html#search-com.atlassian.jira.user.ApplicationUser-com.atlassian.query.Query-com.atlassian.jira.web.bean.PagerFilter-
//reference 2: https://community.atlassian.com/t5/Jira-questions/Using-JQL-queries-in-ScriptRunner/qaq-p/825481
/////////////////////////////////////////////////
 
import groovy.lang.*
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.bc.issue.search.SearchService
import com.onresolve.scriptrunner.parameters.annotation.*
import com.atlassian.jira.web.bean.*
import com.atlassian.jira.issue.search.*
  
//get dependencies
def issueManager = ComponentAccessor.getComponent(IssueManager)
def searchService = ComponentAccessor.getComponent(SearchService)
def user = ComponentAccessor.jiraAuthenticationContext.loggedInUser
def pagerFilter = ComponentAccessor.getComponent(PagerFilter)
  
 
//fetch JQL and process
@ShortTextInput(description = 'Provide the JQL. This will be appended to <b>issue in (${issueKeys[s]}) and</b>. View the last section of the script to see how this works. ', label = 'Enter the JQL with ${issues} instead of the actual issue key')
String jqlSearch
def JQLquery = searchService.parseQuery(user,jqlSearch).query
def results = searchService.search(user,JQLquery,PagerFilter.getUnlimitedFilter())
 
//get results of the JQL
def resultsList = results.getResults()
String printtext = ""
 
//get issue keys from the JQL
for(int r=0;r<resultsList.size();r++)
{
    String issue = issueManager.getIssueObject(resultsList[r].id)
    printtext+=issue+","
}
 
//print results
return "<b>The number of issues the JQL has returned is: ${resultsList.size()}</b><br><br><b>JQL:</b> ${jqlSearch}<br><br>"+printtext
