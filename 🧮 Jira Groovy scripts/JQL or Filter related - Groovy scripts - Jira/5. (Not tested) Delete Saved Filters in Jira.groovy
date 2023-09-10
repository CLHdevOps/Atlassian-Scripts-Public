//Author: Rinaldi Michael
//Last Modified: 26th May 2023, 1:40pm
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
import com.onresolve.scriptrunner.parameters.annotation.*
import com.onresolve.scriptrunner.parameters.annotation.SavedFilterPicker
 
@UserPicker(description = '', label = 'User actor to perform below operations', multiple = false)
ApplicationUser applicationUsersAdmin
 
@SavedFilterPicker(
    label = "Saved Filters", description = "Pick saved filters", placeholder = "Pick saved filters",
    multiple = true
)
List<SearchRequest> searchRequests
 
//variable declaration
def userManager = ComponentAccessor.userManager
def groupManager = ComponentAccessor.groupManager
def userService = ComponentAccessor.getComponent(UserService)
def userUtil = ComponentAccessor.userUtil
def searchRequestService = ComponentAccessor.getComponent(SearchRequestService)
def searchService = ComponentAccessor.getComponent(SearchService)
def ctx = new JiraServiceContextImpl(applicationUsersAdmin)
String printtext=""
 
//delete the filter
for(int f=0;f<searchRequests.size();f++)
{
    printtext+="<h2>JQL Filter - ${searchRequests[f].getName()}</h2><br>"
    printtext+="<h2>JQL Query: ${searchRequests[f].getQuery()}<br>"
    def filterName=searchRequests[f].getName()
    try
    {
        searchRequestService.deleteFilter(ctx,searchRequests[f].getId())
        printtext+="The saved fitler <b>${filterName}</b> is now deleted!<br>********<br>"
    }
    catch (Exception ex)
    {
        printtext+="<br>Error deleting the filter. Stack Trace -> ${ex.getStackTrace()}<br>********<br>"
    }
}
 
 
return printtext
