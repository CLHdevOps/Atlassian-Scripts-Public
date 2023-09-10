//Author: Rinaldi Michael
//Created: 31st May 2023, 01:26pm
//Last Modified: 1st June 2023, 10:55am
//References:
//https://docs.atlassian.com/software/jira/docs/api/7.2.2/com/atlassian/jira/board/BoardManager.html
//https://docs.atlassian.com/software/jira/docs/api/7.6.1/com/atlassian/jira/project/ProjectManager.html
//https://community.atlassian.com/t5/Jira-Software-questions/ScriptRunner-Getting-list-of-Boards-in-a-Project/qaq-p/2236114
//This script will provide you the list of every board in Jira. 
//Details: Board Name, Project Key, Saved Filter Name, Filter Query
  
  
import java.lang.String
import java.io.*
import java.util.*
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
import com.atlassian.jira.board.*
import com.atlassian.jira.project.*
import com.atlassian.greenhopper.service.rapid.ProjectRapidViewService
import com.atlassian.greenhopper.model.rapid.RapidView
import com.onresolve.scriptrunner.runner.customisers.JiraAgileBean
  
        
//////////////////////////////////////////////////////////////////////////////////////////
//Choose output type
  
@Select(
    label = "Output mode",
    description = "Choose between displaying results in this page or storing it in the Script Editor.",
    options = [
        @Option(label = "Current Page", value = "page"),
        @Option(label = "Script Editor", value = "scripteditor"),
    ]
)
String output
  
@ShortTextInput(description = 'Enter any file name (without the extension .groovy). This will create a new file in Script Editor containing the results in csv format.', label = 'Enter the FileName (if Script Editor is chosen)')
String fileName
   
@ShortTextInput(description = 'Enter the character that you will use to split the output text into columns. Refrain from using one special character delimiters given the nature of the output.', label = 'Delimiter (*required)')
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
def boardManager = ComponentAccessor.getComponent(BoardManager)
def boardService = ComponentAccessor.getComponent(BoardService)
def projectManager = ComponentAccessor.getComponent(ProjectManager)
@JiraAgileBean ProjectRapidViewService projectRapidViewService
Dictionary dict = new Hashtable();
  
String newLine
if(output=="page")
    newLine="<br>"
else
    newLine="\n"
  
String printtext="Board Name${delimiter}Project Name and Key${delimiter}Saved Filter Name${delimiter}Filter Query${newLine}"
  
//////////////////////////////////////////////////////////////////////////////////////////
//Get all projects in Jira
def allProjects = projectManager.getProjectObjects()
  
  
//////////////////////////////////////////////////////////////////////////////////////////
//Get all boards in Jira
List<RapidView> allBoards = projectRapidViewService.findRapidViewsByProject(user, allProjects[39]).value
allBoards.empty
  
for(int p=0;p<allProjects.size();p++)
{
    def boardsInProject = projectRapidViewService.findRapidViewsByProject(user, allProjects[p]).value
    for(int b=0;b<boardsInProject.size();b++)
    {
        allBoards.add(boardsInProject[b])
        dict.put(boardsInProject[b],allProjects[p])
    }
}
  
//////////////////////////////////////////////////////////////////////////////////////////
//fetch details of all boards and store it in the variable 'printtext'
for(int b=0;b<allBoards.size();b++)
{
    try
    {
        printtext+="${newLine}${allBoards[b].name}${delimiter}Project Name - ${dict.get(allBoards[b]).getName()}, Project Key - ${dict.get(allBoards[b]).getKey()}${delimiter}${searchRequestManager.getSearchRequestById(allBoards[b].getSavedFilterId()).getName()}${delimiter}${searchRequestManager.getSearchRequestById(allBoards[b].getSavedFilterId()).getQuery().getQueryString()}"
    }
    catch(Exception ex)
    {
        printtext+="${newLine}${allBoards[b].name}${delimiter}Project Name - ${dict.get(allBoards[b]).getName()}, Project Key - ${dict.get(allBoards[b]).getKey()}${delimiter}${searchRequestManager.getSearchRequestById(allBoards[b].getSavedFilterId()).getName()}${delimiter}!No Query!"
    }
}
  
  
//////////////////////////////////////////////////////////////////////////////////////////
//Print results in this page if chosen to do so
if(output=="page")
    return printtext
     
   
//////////////////////////////////////////////////////////////////////////////////////////
//Write results into a script editor file
new File("/home/jira/shared_home/scripts/${fileName}.groovy").withWriter('utf-8')
{
    writer -> writer.writeLine printtext
}
    
return "The contents are written into <b>${fileName}.groovy</b> in the Script Editor."
