//Author: Rinaldi Michael
//Created: 29th May 2023, 04:50pm
//Last Modified: 30th May 2023, 07:37pm
//References:
//https://community.atlassian.com/t5/Marketplace-Apps-Integrations/ScriptRunner-Groovy-Retrieve-all-saved-filters-and-edit-them/qaq-p/619544
//https://community.atlassian.com/t5/Marketplace-Apps-Integrations/I-am-having-trouble-trying-to-update-filters-using-ScriptRunner/qaq-p/703200
//Always choose between options 1,2 or 3 first before proceeding to delete all invalid/no results filters.
  
import com.atlassian.jira.issue.search.*
import com.atlassian.jira.issue.search.SearchRequest
import com.atlassian.jira.bc.filter.SearchRequestService
import com.atlassian.jira.user.ApplicationUser
import java.lang.String
import java.io.*
import com.atlassian.crowd.model.group.Groups
import com.atlassian.crowd.embedded.api.Group
import com.atlassian.jira.bc.user.UserService
import com.atlassian.jira.component.ComponentAccessor
import com.onresolve.scriptrunner.parameters.annotation.*
import com.atlassian.jira.bc.user.UserService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.project.Project
import com.atlassian.jira.security.roles.ProjectRoleManager
import com.atlassian.jira.bc.projectroles.ProjectRoleService
import com.atlassian.jira.issue.search.SearchRequest.*
import com.atlassian.jira.bc.JiraServiceContextImpl
import com.atlassian.jira.bc.issue.search.SearchService
import com.onresolve.scriptrunner.parameters.annotation.*
import com.onresolve.scriptrunner.parameters.annotation.meta.*
     
//////////////////////////////////////////////////////////////////////////////////////////
//Get options input
@Select(
    label = "Choose action",
    description = "Choose between viewing all filters in three scenarios, viewing all filters and deleting the invalid ones, viewing all filters and deleting the ones with no results.",
    options = [
        @Option(label = "View all filters and details only", value = "view"),
        @Option(label = "View all invalid filters and details only", value = "viewinvalid"),
        @Option(label = "View all filters with no results and details only", value = "viewnoresults"),
        @Option(label = "!DELETE! View all filters, details and delete invalid filters", value = "viewanddeleteinvalid"),
        @Option(label = "!DELETE! View all filters, details and delete the ones with no results", value = "viewanddeletenoresults")
    ]
)
String option
     
//////////////////////////////////////////////////////////////////////////////////////////
//variable declaration
def userManager = ComponentAccessor.userManager
def groupManager = ComponentAccessor.groupManager
def userService = ComponentAccessor.getComponent(UserService)
def userUtil = ComponentAccessor.userUtil
def user = ComponentAccessor.jiraAuthenticationContext.loggedInUser
def searchRequestService = ComponentAccessor.getComponent(SearchRequestService)
def searchService = ComponentAccessor.getComponent(SearchService)
def allUsers = userManager.allApplicationUsers
def searchRequestManager = ComponentAccessor.getComponent(SearchRequestManager)
     
//////////////////////////////////////////////////////////////////////////////////////////
//Get all filters in Jira
def allFilters = searchRequestService.getOwnedFilters(allUsers[0])
allFilters.empty
   
String printtext = "<br>"
for(int k=0;k<allUsers.size();)
{
    def filterToAdd = searchRequestService.getOwnedFilters(allUsers[k])
    allFilters = allFilters + filterToAdd
    k++
}
  
//////////////////////////////////////////////////////////////////////////////////////////
//Parse each query
for(int f=0;f<allFilters.size();f++)
{
    def jqlSearch = allFilters[f].getQuery().getQueryString()
      
    try
    {
        def parseResult = searchService.parseQuery(user, jqlSearch)
      
        //check if the JQL query is invalid
        if ((!parseResult.valid) && (option!="viewnoresults"))
        {
            printtext+="<br>${f+1}. <b>Invalid Query</b> -> Filter name: ${allFilters[f].getName()}<br>"
            printtext+="<b>Query:</b> ${allFilters[f].getQuery().getQueryString()}<br>"
            printtext+="<b>Error:</b>${parseResult.getErrors()} and errors - ${searchService.validateQuery(user,allFilters[f].getQuery()).getErrorMessages()} and warnings - ${searchService.validateQuery(user,allFilters[f].getQuery()).getWarningMessages()}<br>"
            //delete the filter if option chosen
            if(option=="viewanddeleteinvalid")
            {
                searchRequestManager.delete(allFilters[f].getId())
                printtext+="<b>Saved Filter - ${allFilters[f].getName()} is now deleted!</b><br><br>"
            }
        }
        //else check if the query has 0 results
        else if((searchService.searchCount(user, parseResult.query) == 0) && (option!= "viewinvalid"))
        {
            printtext+="<br>${f+1}. <b>No results</b> -> Filter name: ${allFilters[f].getName()}<br>"
            printtext+="<b>Query:</b> ${allFilters[f].getQuery().getQueryString()}<br>"
            printtext+="<b>Reason for no results:</b>${searchService.validateQuery(user,allFilters[f].getQuery()).getWarningMessages()}${searchService.validateQuery(user,allFilters[f].getQuery()).getErrorMessages()}<br><br>"
            //delete the filter if option chosen
            if(option=="viewanddeletenoresults")
            {
                searchRequestManager.delete(allFilters[f].getId())
                printtext+="<b>Saved Filter - ${allFilters[f].getName()} is now deleted!</b><br><br>"
            }      
        }
        else if ((option== "view") || (option=="viewanddeleteinvalid") ||(option=="viewanddeletenoresults"))
        {
            printtext+="<br>${f+1}. <b>Valid query</b> -> Filter name: ${allFilters[f].getName()}<br>"
            printtext+="<b>Query:</b> ${allFilters[f].getQuery().getQueryString()}<br>"
        }
    }
    catch(Exception ex)
    {
        log.info("${ex.getMessage()}")
    }
      
}
  
return printtext
