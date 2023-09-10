//Author: Rinaldi Michael
//Created: 29th November 2022, 08:27 PM
//Last Modified: 29th May 2023, 06:07 pm
//References:
//https://community.atlassian.com/t5/Marketplace-Apps-Integrations/ScriptRunner-Groovy-Retrieve-all-saved-filters-and-edit-them/qaq-p/619544
//https://community.atlassian.com/t5/Marketplace-Apps-Integrations/I-am-having-trouble-trying-to-update-filters-using-ScriptRunner/qaq-p/703200
    
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
    
    
//variable declaration for adding groups and projects
def userManager = ComponentAccessor.userManager
def groupManager = ComponentAccessor.groupManager
             
//variable delcaration for adding groups to the user
def userService = ComponentAccessor.getComponent(UserService)
def userUtil = ComponentAccessor.userUtil
def searchRequestService = ComponentAccessor.getComponent(SearchRequestService)
def searchService = ComponentAccessor.getComponent(SearchService)
    
//user picker to verify if the user already exists
//@UserPicker(description = '', label = 'Enter User', multiple = false)
//ApplicationUser applicationUser
    
    
def allUsers = userManager.allApplicationUsers
    
def allFilters = searchRequestService.getOwnedFilters(allUsers[0])
allFilters.empty
  
String printtext = "<br>"
    
//return allFilters.getProperties()
    
for(int k=0;k<allUsers.size();)
{
    def filterToAdd = searchRequestService.getOwnedFilters(allUsers[k])
    allFilters = allFilters + filterToAdd
    k++
}
 
for(int f=0;f<allFilters.size();f++)
{
    def jqlSearch = allFilters[f].getQuery().getQueryString()
    printtext+="${f+1}. ${jqlSearch}<br><br>"
}
    
return printtext
