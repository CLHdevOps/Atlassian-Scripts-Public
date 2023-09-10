/////////////////////////////////////////////////
//////////Author: Rinaldi Michael////////////////
/////Created: 20/02/2023, 08:36 pm///////////////
////Last Modified: 16/01/2023, 02:18 pm//////////
/////////////////////////////////////////////////
 
 
import org.ofbiz.core.entity.GenericValue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.project.Project
import com.atlassian.jira.project.ProjectManager
import com.atlassian.jira.project.*
import com.atlassian.jira.project.Project
import com.onresolve.scriptrunner.parameters.annotation.ProjectPicker
import java.lang.*
import java.io.*
import groovy.lang.*
  
 
def ret=[]
 
def projectManager = ComponentAccessor.getComponent(ProjectManager)
def issueManager = ComponentAccessor.getComponent(IssueManager)
 
 
def allProjects = projectManager.projects
def issuesInAProject = issueManager.getIssueIdsForProject(allProjects[0].getId())
 
for(int p=1;p<allProjects.size();p++)
{
    def tempIssuesInAProject = issueManager.getIssueIdsForProject(allProjects[p].getId())
    issuesInAProject += tempIssuesInAProject
}
 
def allIssueIdsInJira = issuesInAProject
String[] issueKeys = new String[allIssueIdsInJira.size()]
 
for(int i=0;i<allIssueIdsInJira.size();i++)
{
    issueKeys[i] = issueManager.getIssueObject(allIssueIdsInJira[i]).toString()
}
 
return issueKeys
