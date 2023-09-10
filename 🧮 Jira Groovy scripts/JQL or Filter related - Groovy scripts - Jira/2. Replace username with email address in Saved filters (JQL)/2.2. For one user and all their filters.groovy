//Author: Rinaldi Michael
//Last Modified: 29th November 2022, 08:14 PM
//References:
//https://community.atlassian.com/t5/Marketplace-Apps-Integrations/ScriptRunner-Groovy-Retrieve-all-saved-filters-and-edit-them/qaq-p/619544
//https://community.atlassian.com/t5/Marketplace-Apps-Integrations/I-am-having-trouble-trying-to-update-filters-using-ScriptRunner/qaq-p/703200
 
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
@UserPicker(description = '', label = 'Enter User', multiple = false)
ApplicationUser applicationUser
 
 
String[] userBeforeAtList = applicationUser.getEmailAddress().split('@')
String userBeforeAt = userBeforeAtList[0].toString()
 
//return userBeforeAt
 
def ctx = new JiraServiceContextImpl(applicationUser)
def filters = searchRequestService.getOwnedFilters(applicationUser)
 
return filters
 
for(int f=0;f<filters.size();)
{
def filter1 = filters[f]
def jqlQuery = searchService.getJqlString(filter1.query)
 
//return jqlQuery
 
def newQueryString = jqlQuery.replace(userBeforeAt,'"'+applicationUser.getEmailAddress()+'"')
def newQuery = searchService.parseQuery(applicationUser,newQueryString).query
 
//return newQuery
try
{
    filter1.setQuery(newQuery)
    searchRequestService.updateFilter(ctx,filter1)
}
catch(Exception ex)
{
    log.info("ignore filter")
}
f++
}
