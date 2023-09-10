//Reference: https://library.adaptavist.com/entity/archive-not-recently-updated-projects
//Modified by: Rinaldi Michael
//Created: 13th Feb 2023, 01:15 pm
//Last Modified: 13th Feb 2023, 02:25pm
/*
1. Use ScriptRunner's dynamic form to select one or many projects
2. A select list drop down to immediately restore the archived projects if done by accident. Please note, this only works if the projects were not deselected from the first drop down.
*/
  
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
  
@ProjectPicker(
    label = 'Projects', description = 'Pick projects', placeholder = 'Pick projects', includeArchived = false,
    multiple = true
)
List<Project> projects
 
 
@Select(
    label = "Choose to Archive or Restore project",
    description = "Archiving a project will hide data related to this project from users. Emergency Restore will restore projects if the .",
    options = [
        @Option(label = "Archive", value = "Archive"),
        @Option(label = "Emergency Restore", value = "Restore"),
    ]
)
String value
 
def projectManager = ComponentAccessor.projectManager
def user = ComponentAccessor.jiraAuthenticationContext.loggedInUser
def searchService = ComponentAccessor.getComponent(SearchService)
def archivedProjectService = ComponentAccessor.getComponent(ArchivedProjectService)
 
String printtext
int count=1
 
 
if(value=="Archive")
{
    projects.each
    { project ->
    def validationResult = archivedProjectService.validateArchiveProject(user, project.key)
    if (validationResult.valid) {
        archivedProjectService.archiveProject(validationResult)
        log.debug("Project ${project.key} has been archived")
        printtext+="<br>${count}. Project <b>${project.getName()}</b> with key <b>(${project.key})</b> has been archived<br>"
    } else {
        log.warn("Project ${project.key} could not be archived: ${validationResult.errorCollection}")
        printtext+="<br>${count}. Project <b>${project.getName()}</b> with key <b>(${project.key})</b> could not be archived<br>"
    }
    count++
    }
}
else
{
    projects.each
    { project ->
    def validationResult = archivedProjectService.validateArchiveProject(user, project.key)
    if (validationResult.valid) {
        archivedProjectService.restoreProject(validationResult)
        log.debug("Project ${project.key} has been archived")
        printtext+="<br>${count}. Project <b>${project.getName()}</b> with key <b>(${project.key})</b> has been restored.<br>"
    } else {
        log.warn("Project ${project.key} could not be archived: ${validationResult.errorCollection}")
        printtext+="<br>${count}. Project <b>${project.getName()}</b> with key <b>(${project.key})</b> could not be restored.<br>"
    }
    count++
    }
}
 
 
return printtext
