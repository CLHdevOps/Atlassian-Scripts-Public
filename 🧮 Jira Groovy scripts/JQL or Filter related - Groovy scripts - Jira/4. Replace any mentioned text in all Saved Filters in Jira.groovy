//Author: Rinaldi Michael
//Last Modified: 29th May 2023, 06:30pm
//References:
//https://community.atlassian.com/t5/Marketplace-Apps-Integrations/ScriptRunner-Groovy-Retrieve-all-saved-filters-and-edit-them/qaq-p/619544
//https://community.atlassian.com/t5/Marketplace-Apps-Integrations/I-am-having-trouble-trying-to-update-filters-using-ScriptRunner/qaq-p/703200
//Does not work if one saved filter/JQL has many errors. In case many errors exist in one filter. Input the filter's correct JQL and replace it with the old one. The script will replace as much as possible.
     
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
import com.onresolve.scriptrunner.parameters.annotation.*
     
@UserPicker(description = '', label = 'User actor to perform below operations', multiple = false)
ApplicationUser applicationUsersAdmin
   
@Checkbox(label = "View all filters (Does not perform any operation if selected)", description = "Select the checkbox to view all Filters in Jira before running the script")
Boolean ViewFiltersInJira
    
@ShortTextInput(description = 'Type in the text to replace in all filters in Jira', label = 'Old/Current Text (Case sensitive)')
String ToReplace
   
@ShortTextInput(description = 'Type in the text to replace old text with', label = 'New Text (Case sensitive)')
String NewText
  
@Checkbox(label = "Preview results before performing the changes", description = "Preview Changes")
Boolean Preview
    
String printtext=""
     
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
   
   
if(ViewFiltersInJira==true)
{
    for(int f=0;f<allFilters.size();f++)
    {
        printtext+="<h2>Filter Name: <b>${allFilters[f].getName()}</b></h2><br>${allFilters[f].getQuery().getQueryString()}<br>**********<br>"
    }
    return printtext
//return allUsers[608].getDisplayName()
}  
  
  
String PreviewText=""
if(Preview==true)
{
    for(int f=0;f<=allFilters.size();)
    { 
    try
    {
    def ctx = new JiraServiceContextImpl(applicationUsersAdmin)
    def filter1 = allFilters[f]
    def jqlQuery = searchService.getJqlString(filter1.query)
        if(jqlQuery.contains(ToReplace))
        {
        def newQueryString = jqlQuery.replace(ToReplace,NewText)
        
        def newQuery = searchService.parseQuery(applicationUsersAdmin,newQueryString).getQuery()
        PreviewText = PreviewText + "<h2>Filter Name: <b>${filter1.getName()}</b></h2>" + "<br>" + newQuery.toString() +"<br><br>*********<br>"
        }
        else
        {
            f++
            continue;
        }
    }
        catch(Exception ex)
        {
            log.info("Some error at ${f}")
            //return "Some error at ${u} and ${f}"
        }
    f++
    }
    return PreviewText
}
  
  
   
for(int f=0;f<=allFilters.size();)
{
    try
    {
    def ctx = new JiraServiceContextImpl(applicationUsersAdmin)
    def filter1 = allFilters[f]
    def jqlQuery = searchService.getJqlString(filter1.query)
        if(jqlQuery.contains(ToReplace))
        {
        def newQueryString = jqlQuery.replace(ToReplace,NewText)
        
        def newQuery = searchService.parseQuery(applicationUsersAdmin,newQueryString).getQuery()
        filter1.setQuery(newQuery)
        searchRequestService.updateFilter(ctx,filter1)
        //return filter1
        }
        else
        {
            f++
            continue;
        }
    }
        catch(Exception ex)
        {
            log.info("Some error at ${f}")
            //return "Some error at ${u} and ${f}"
        }
    f++
}
 
printtext+="<h1>All Filters after modification!</h1><br>"  
for(int f=0;f<allFilters.size();f++)
{
     
    printtext+="<h2>Filter Name: <b>${allFilters[f].getName()}</b></h2><br>${allFilters[f].getQuery()}<br>**********<br>"
}
return printtext
//return allFilters
