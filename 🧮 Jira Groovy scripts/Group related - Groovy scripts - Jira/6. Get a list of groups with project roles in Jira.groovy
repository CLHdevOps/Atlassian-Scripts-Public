////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Author: Rinaldi Michael                                                                                                     //
//Created: 17th Mar 2023, 04:44 pm                                                                                            //
//Last Modified: 17th Mar 2023, 06:12 pm                                                                                      //
//Reference                                                                                                                   //
//https://community.atlassian.com/t5/Jira-questions/Get-List-of-users-added-in-a-Project-Role-using-Script-Runner/qaq-p/1759339/
//https://community.atlassian.com/t5/Jira-questions/Groovy-how-to-get-all-projects/qaq-p/666183                               //
//https://community.atlassian.com/t5/Jira-questions/How-to-get-groups-which-are-assigned-to-a-specific-role-in-a/qaq-p/708894 //
//Code results yet to be verified!
//Functionalities
//1. Get a list of groups that have and do not have project roles in Jira
//2. Get a list of projects that have and do not have project roles set for groups.
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
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
import com.atlassian.jira.security.roles.ProjectRoleActor
import com.atlassian.jira.security.roles.ProjectRoleManager
import com.atlassian.jira.bc.projectroles.ProjectRoleService
import com.atlassian.crowd.model.group.Groups
import com.atlassian.crowd.embedded.api.Group
  
  
def projectManager = ComponentAccessor.projectManager
def user = ComponentAccessor.jiraAuthenticationContext.loggedInUser
def userManager = ComponentAccessor.userManager
def projectRoleService = ComponentAccessor.getComponent(ProjectRoleService)
def projectRoleManager = ComponentAccessor.getComponent(ProjectRoleManager)
def groupManager = ComponentAccessor.GroupManager
String printtext=""
  
def allGroupsInJira = groupManager.getAllGroupNames()
//creating a set of all groups in Jira
Set<String> allGroupsInJiraSet = new HashSet<String>();
for(int g=0;g<allGroupsInJira.size();g++)  //loop through all groups in Jira
{
    allGroupsInJiraSet.add(allGroupsInJira[g])   
}
 
//Creating a set of all Projects in Jira
def allProjectsInJira = projectManager.getProjectObjects()
Set<String> allProjectsInJiraSet = new HashSet<String>();
for(int p=0;p<allProjectsInJira.size();p++)
{
    String temp = "Project Key: ${allProjectsInJira[p].getKey()}, Project Name: ${allProjectsInJira[p].getName()}"
    allProjectsInJiraSet.add(temp)
}
 
Set<String> projectsWithGroupProjectRoles = new HashSet<String>();
 
 
def projectRolesInJira = projectRoleManager.getProjectRoles()
Set<String> groupWithProjectRoles = new HashSet<String>();
  
for(int p=0;p<allProjectsInJira.size();p++)   //loop through all projects in Jira
{
    for(int pr=0;pr<projectRolesInJira.size();pr++)   //loop through all project roles in Jira
    {
        def projectRoleActors = projectRoleManager.getProjectRoleActors(projectRolesInJira[pr],allProjectsInJira[p])
        def actorTypes = projectRoleActors.getRoleActorsByType(ProjectRoleActor.GROUP_ROLE_ACTOR_TYPE)   //GROUP_ROLE_ACTOR_TYPE will get only Groups added to project roles
        if(actorTypes.size()>0)
            {
                String temp = "Project Key: ${allProjectsInJira[p].getKey()}, Project Name: ${allProjectsInJira[p].getName()}"
                projectsWithGroupProjectRoles.add(temp)
            }
        for(int at=0;at<actorTypes.size();at++)  //loop through all actors in Jira
        {
            groupWithProjectRoles.add(projectRoleActors.getRoleActorsByType(ProjectRoleActor.GROUP_ROLE_ACTOR_TYPE)[at].parameter)    //adding the group name to the set
        }
    }
}
 
printtext+="<h2><b>All groups with project roles in Jira</b></h2><br><br>"
for(int gwpr=1;gwpr<=groupWithProjectRoles.size();gwpr++)
{
    printtext+="${gwpr}. ${groupWithProjectRoles[gwpr]}<br>"
}
  
allGroupsInJiraSet.removeAll(groupWithProjectRoles)
printtext+="<br>****************************************************<br><h2><b>All groups without project roles in Jira</b></h2><br><br>"
for(int ags=1;ags<=allGroupsInJiraSet.size();ags++)
{
    printtext+="${ags}. ${allGroupsInJiraSet[ags]}<br>"
}
 
printtext+="<br>****************************************************<br>"
printtext+="****************************************************<br>"
 
printtext+="<h2><b>All projects which have Groups with project roles</h2></b>"
for(int prs=1;prs<=projectsWithGroupProjectRoles.size();prs++)
{
    printtext+="${prs}. ${projectsWithGroupProjectRoles[prs]}<br>"
}
 
allProjectsInJiraSet.removeAll(projectsWithGroupProjectRoles)
printtext+="<br>****************************************************<br><h2><b>All projects which do not have Groups with project roles</b></h2><br><br>"
for(int aps=1;aps<=allProjectsInJiraSet.size();aps++)
{
    printtext+="${aps}. ${allProjectsInJiraSet[aps]}<br>"
}
 
return printtext
