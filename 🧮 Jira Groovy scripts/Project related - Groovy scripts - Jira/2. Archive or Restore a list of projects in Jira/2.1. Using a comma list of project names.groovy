////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Created - 27th April 2023, 08:58 pm
//Last Modified - 27th April 2023, 09:11 pm
//Authors - Rinaldi Michael, Adaptavist
//References -
//https://library.adaptavist.com/entity/archive-not-recently-updated-projects
/*
1. Use ScriptRunner's dynamic form to select one or many projects
2. Choose to Archive or Restore a project
*/
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   
import com.onresolve.scriptrunner.parameters.annotation.ShortTextInput
import java.lang.String
import com.atlassian.jira.bc.issue.search.SearchService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.project.archiving.ArchivedProjectService
import org.apache.log4j.Level
import org.apache.log4j.Logger
import com.atlassian.jira.project.Project
import com.onresolve.scriptrunner.parameters.annotation.ProjectPicker
import com.onresolve.scriptrunner.parameters.annotation.*
import com.onresolve.scriptrunner.parameters.annotation.meta.*
   
 
def projectManager = ComponentAccessor.projectManager
def user = ComponentAccessor.jiraAuthenticationContext.loggedInUser
def searchService = ComponentAccessor.getComponent(SearchService)
def archivedProjectService = ComponentAccessor.getComponent(ArchivedProjectService)
 
 
@ShortTextInput(description = 'Enter a comma list of project names', label = 'Project Name/s')
String projectnamesinput
String[] projectnames = projectnamesinput.split(',')
  
  
@Select(
    label = "Choose to Archive or Restore project",
    description = "Archiving a project will hide data related to this project from users. Emergency Restore will restore projects if the .",
    options = [
        @Option(label = "Archive", value = "Archive"),
        @Option(label = "Restore", value = "Restore"),
    ]
)
String value
 
 
  
String printtext=""
int count=1
  
for(int p=0;p<projectnames.size();p++)
{
    def project = projectManager.getProjectObjByName(projectnames[p])
    boolean projectIsArchivedAlready = project.isArchived()
    boolean projectIsActiveAlready = !projectIsArchivedAlready
    if(value=="Archive")
    {
        def validationResult = archivedProjectService.validateArchiveProject(user, project.key)
        if (validationResult.valid)
        {
            archivedProjectService.archiveProject(validationResult)
            if(projectIsArchivedAlready)
                printtext+="<br>${count}. Project <b>${project.getName()}</b> with key <b>(${project.key})</b> is already archived<br>"
            else
                printtext+="<br>${count}. Project <b>${project.getName()}</b> with key <b>(${project.key})</b> has been archived<br>"
        }
        else
        {
            printtext+="<br>${count}. Project <b>${project.getName()}</b> with key <b>(${project.key})</b> could not be archived<br>"
        }
        count++
    }
    else
    {
        def validationResult = archivedProjectService.validateArchiveProject(user, project.key)
        if (validationResult.valid)
        {
            archivedProjectService.restoreProject(validationResult)
            if(projectIsActiveAlready)
                printtext+="<br>${count}. Project <b>${project.getName()}</b> with key <b>(${project.key})</b> is already active.<br>"
            else
                printtext+="<br>${count}. Project <b>${project.getName()}</b> with key <b>(${project.key})</b> has been restored.<br>"
        }
        else
        {
        printtext+="<br>${count}. Project <b>${project.getName()}</b> with key <b>(${project.key})</b> could not be restored.<br>"
        }
    count++
    }
}
  
  
return printtext
