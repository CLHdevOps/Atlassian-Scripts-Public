//Author: Rinaldi Michael
//Created: 29th May 2023, 04:50pm
//Last Modified: 31st May 2023, 03:10pm
//References:
//https://community.atlassian.com/t5/Marketplace-Apps-Integrations/ScriptRunner-Groovy-Retrieve-all-saved-filters-and-edit-them/qaq-p/619544
//https://community.atlassian.com/t5/Marketplace-Apps-Integrations/I-am-having-trouble-trying-to-update-filters-using-ScriptRunner/qaq-p/703200
    
import java.lang.String
import java.io.*
import com.atlassian.jira.issue.search.*
import com.atlassian.jira.issue.search.SearchRequest
import com.atlassian.jira.bc.filter.SearchRequestService
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.bc.user.UserService
import com.atlassian.jira.component.ComponentAccessor
import com.onresolve.scriptrunner.parameters.annotation.*
import com.atlassian.jira.bc.user.UserService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.search.SearchRequest.*
import com.atlassian.jira.bc.issue.search.SearchService
import com.onresolve.scriptrunner.parameters.annotation.*
import com.onresolve.scriptrunner.parameters.annotation.meta.*
       
//////////////////////////////////////////////////////////////////////////////////////////
//Enter file name
@ShortTextInput(description = 'Enter any file name (without the extension .groovy). This will create a new file in Script Editor containing the results in csv format.', label = 'Enter the FileName')
String fileName
  
@ShortTextInput(description = 'Enter the character that you will use to split the output text into columns.', label = 'Delimiter')
String delimiter
       
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
     
String printtext = "Filter Name${delimiter}Filter Description${delimiter}Owner${delimiter}Status of the Filter${delimiter}JQL query${delimiter}Permissions (if applicable)${delimiter}Error/Warning (If applicable)\n"
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
 
    //fill in null values to not break the output
    if(allFilters[f].getName()==null)
    {
        allFilters[f].name=" "
    }
    if(allFilters[f].getDescription()==null)
    {
        allFilters[f].description=" "
    }
 
 
 
        
    try
    {
        def parseResult = searchService.parseQuery(user, jqlSearch)
        allFilters[f].getDescription()
        
        //check if the JQL query is invalid
        if (!parseResult.valid)
        {
            //replaceAll is being used to prevent results from being thrown into multiple rows or columns when pasted into a spreadsheet
            printtext+="\n${allFilters[f].getName().replaceAll("[\r\n]+", " ")}${delimiter}${allFilters[f].getDescription().replaceAll("[\r\n]+", " ")}${delimiter}${allFilters[f].getOwner().toString().replaceAll("[\r\n]+", " ")}${delimiter}Invalid${delimiter}${allFilters[f].getQuery().getQueryString().replaceAll("[\r\n]+", " ").replaceAll("[\t]+"," ")}${delimiter}${allFilters[f].getPermissions().getPermissionSet().toString().replaceAll("[\r\n]+", " ")}${delimiter}${parseResult.getErrors()} and errors - ${searchService.validateQuery(user,allFilters[f].getQuery()).getErrorMessages()} and warnings - ${searchService.validateQuery(user,allFilters[f].getQuery()).getWarningMessages()}"
        }
        //else check if the query has 0 results
        else if(searchService.searchCount(user, parseResult.query) == 0)
        {
            //replaceAll is being used to prevent results from being thrown into multiple rows or columns when pasted into a spreadsheet
            printtext+="\n${allFilters[f].getName().replaceAll("[\r\n]+", " ")}${delimiter}${allFilters[f].getDescription().replaceAll("[\r\n]+", " ")}${delimiter}${allFilters[f].getOwner().toString().replaceAll("[\r\n]+", " ")}${delimiter}No Results${delimiter}${allFilters[f].getQuery().getQueryString().replaceAll("[\r\n]+", " ").replaceAll("[\t]+"," ")}${delimiter}${allFilters[f].getPermissions().getPermissionSet().toString().replaceAll("[\r\n]+", " ")}${delimiter}${searchService.validateQuery(user,allFilters[f].getQuery()).getWarningMessages()}${searchService.validateQuery(user,allFilters[f].getQuery()).getErrorMessages()}"
        }
        else
        {
            printtext+="\n${allFilters[f].getName().replaceAll("[\r\n]+", " ")}${delimiter}${allFilters[f].getDescription().replaceAll("[\r\n]+", " ")}${delimiter}${allFilters[f].getOwner().toString().replaceAll("[\r\n]+", " ")}${delimiter}Valid${delimiter}${allFilters[f].getQuery().getQueryString().replaceAll("[\r\n]+", " ").replaceAll("[\t]+"," ")}${delimiter}${allFilters[f].getPermissions().getPermissionSet().toString().replaceAll("[\r\n]+", " ")}${delimiter}"
        }
    }
    catch(Exception ex)
    {
        printtext+="\n${allFilters[f].getName().replaceAll("[\r\n]+", " ")}${delimiter}"
        log.error("${allFilters[f].getName()} did not work ${allFilters[f].getQuery().getQueryString()} due to ${ex.getMessage()}")
    }
        
}
    
  
//////////////////////////////////////////////////////////////////////////////////////////
//Write results into a script editor file
new File("/home/jira/shared_home/scripts/${fileName}.groovy").withWriter('utf-8')
{
    writer -> writer.writeLine printtext
}
   
return "The contents are written into <b>${fileName}.groovy</b> in the Script Editor."
