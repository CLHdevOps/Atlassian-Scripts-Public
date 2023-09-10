//Author: Rinaldi Michael
//Last Modified: 7th December 2022, 10:49 am
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
import com.onresolve.scriptrunner.parameters.annotation.*
  
@UserPicker(description = '', label = 'User actor to perform below operations', multiple = false)
ApplicationUser applicationUsersAdmin
 
 
 
String printtext
  
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
  
//return allFilters.getProperties()
  
for(int k=0;k<allUsers.size();)
{
    def filterToAdd = searchRequestService.getOwnedFilters(allUsers[k])
    allFilters = allFilters + filterToAdd
    k++
}
  
//return allFilters
//return allUsers[608].getDisplayName()
 
/*
for(int f=0;f<=allFilters.size();)
{
    try
    {
    def ctx = new JiraServiceContextImpl(applicationUsersAdmin)
    def filter1 = allFilters[f]
    def jqlQuery = searchService.getJqlString(filter1.query)
    def newQueryString = jqlQuery.replace("ltd.harness.jira.hawkbudget@connect.atlassian.com",'h')
     
     
    def newQuery = searchService.parseQuery(applicationUsersAdmin,newQueryString).getQuery()
    filter1.setQuery(newQuery)
    searchRequestService.updateFilter(ctx,filter1)
    //return filter1
    }
        catch(Exception ex)
        {
            log.info("Some error at ${f}")
            //return "Some error at ${u} and ${f}"
        }
    f++
}
 
return "done"
*/
  
/*
for(int u=0;u<allUsers.size();)
{
    printtext = printtext + " ${u}. ${allUsers[u].getEmailAddress()}<br>"
    //if(allUsers[u].getUsername()=="rtummala")
    //return u.toString()+allUsers[u]
    u++
}
 
return printtext*/
  
for(int u=0;u<allUsers.size();)//u<=608;)//
{
 
try
{
def applicationUser = allUsers[u]
//String[] userBeforeAtList = applicationUser.getEmailAddress().split('@')
//String userBeforeAt = userBeforeAtList[0].toString()
  
//return applicationUser
  
def ctx = new JiraServiceContextImpl(applicationUsersAdmin)
//def filters = searchRequestService.getOwnedFilters(applicationUser)
  
if(applicationUser == null)
{
    u++
    continue;
}
 
  
    for(int f=0;f<allFilters.size();)
    {
    def filter1 = allFilters[f]
    def jqlQuery = searchService.getJqlString(filter1.query)
    def newQueryString
  
    if(jqlQuery.contains(applicationUser.getUsername()))
        newQueryString = jqlQuery.replace(applicationUser.getUsername(),'"'+applicationUser.getEmailAddress()+'"')
    else if(jqlQuery.contains(applicationUser.getUsername().toLowerCase()))
        newQueryString = jqlQuery.replace(applicationUser.getUsername().toLowerCase(),'"'+applicationUser.getEmailAddress()+'"')
    else if(jqlQuery.contains(applicationUser.getDisplayName()) && (applicationUser.getDisplayName().size()!=1))
        newQueryString = jqlQuery.replace(applicationUser.getDisplayName(),applicationUser.getEmailAddress())
    else if(jqlQuery.contains(applicationUser.getDisplayName().toLowerCase()) && (applicationUser.getDisplayName().size()!=1))
        newQueryString = jqlQuery.replace(applicationUser.getDisplayName().toLowerCase(),applicationUser.getEmailAddress())
    else
    {
        f++
        continue
    }
 
    def newQuery = searchService.parseQuery(applicationUser,newQueryString).getQuery()
 
    //return newQueryString
    printtext = printtext + newQueryString +"\n"
     
    //printtext = printtext + f.toString() + "for user - " + allUsers[u].getUsername() + newQuery.toString() + "<br><br>"
        try
        {
            filter1.setQuery(newQuery)
            searchRequestService.updateFilter(ctx,filter1)
            //return filter1
        }
        catch(Exception ex)
        {
            log.info("Some error at ${u} and ${f}")
            //return "Some error at ${u} and ${f}"
        } 
    f++
    }
  
u++
}
catch (Exception ex)
{
    log.info("Some error at ${u} for full loop")
    u++
}
}//end of first for loop
 
return "yes" + printtext
