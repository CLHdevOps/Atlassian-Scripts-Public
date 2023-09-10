//Reference: https://library.adaptavist.com/entity/archive-not-recently-updated-projects
//Modified by: Rinaldi Michael
//Last Modified:30th Jan 2023, 02:55pm
 
 
import com.onresolve.scriptrunner.parameters.annotation.ShortTextInput
import java.lang.String
import com.atlassian.jira.bc.issue.search.SearchService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.project.archiving.ArchivedProjectService
import org.apache.log4j.Level
import org.apache.log4j.Logger
 
 
@ShortTextInput(description = 'Enter the number of days in which a ticket was not created in the project', label = 'Number of days')
String numberOfDays
 
def projectManager = ComponentAccessor.projectManager
def user = ComponentAccessor.jiraAuthenticationContext.loggedInUser
def searchService = ComponentAccessor.getComponent(SearchService)
def archivedProjectService = ComponentAccessor.getComponent(ArchivedProjectService)
 
def log = Logger.getLogger(getClass())
log.setLevel(Level.DEBUG)
 
def projectsToArchive = projectManager.projects.findAll { project ->
    // JQL criteria to search within projects. If it returns anything, the project DOESN'T get archived
    def jqlSearch = "project in (${project.key})  AND (updated > -${numberOfDays}d OR created > -${numberOfDays}d)"
    def parseResult = searchService.parseQuery(user, jqlSearch)
 
    if (!parseResult.valid) {
        log.warn("The JQL '${jqlSearch}' is not valid. Parse result: ${parseResult.errors}")
        return false
    }
 
    searchService.searchCount(user, parseResult.query) == 0
}
 
String printtext=""
for(int p=0;p<projectsToArchive.size();p++)
{
  printtext+="<br><br>${p+1}. Name: <b>"+projectsToArchive[p].getName()+"</b><br>Key: "+projectsToArchive[p].getKey()
}
 
return printtext
 
 
/*
projectsToArchive.each { project ->
    def validationResult = archivedProjectService.validateArchiveProject(user, project.key)
    if (validationResult.valid) {
        archivedProjectService.archiveProject(validationResult)
        log.debug("Project ${project.key} has been archived")
    } else {
        log.warn("Project ${project.key} could not be archived: ${validationResult.errorCollection}")
    }
}
*/
