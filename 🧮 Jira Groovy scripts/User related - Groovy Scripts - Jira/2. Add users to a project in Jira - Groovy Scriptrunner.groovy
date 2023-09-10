////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Created - 12th Oct 2022
//Last Modified - 5th May 2023, 09:21 pm
//Authors - Rinaldi Michael, Adaptavist
//References -
//https://library.adaptavist.com/
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
import com.atlassian.jira.user.ApplicationUser
import java.lang.String
import com.atlassian.jira.project.Project
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.security.roles.ProjectRoleManager
import com.atlassian.jira.bc.projectroles.ProjectRoleService
import com.onresolve.scriptrunner.parameters.annotation.*
   
def userManager = ComponentAccessor.userManager
def projectRoleManager = ComponentAccessor.getComponent(ProjectRoleManager)
def projectRoleService = ComponentAccessor.getComponent(ProjectRoleService)
  
String printtext=""
   
//return com.atlassian.jira.security.roles.ProjectRoleActor.USER_ROLE_ACTOR_TYPE
   
//Specify user
@UserPicker(description = 'Search for the user', label = 'User', multiple = true)
List<ApplicationUser> userInput
 
   
//Specify project and role
@ProjectPicker(description = 'Enter the project name', label = 'Project name(s)', includeArchived = false, multiple = true, placeholder = 'Select project')
List<Project> project
   
@ShortTextInput(description = 'Enter the project role', label = 'Project Role')
String roleName
   
//Specify user/group role actor (ex./ UserRoleActor.TYPE, GroupRoleActor.TYPE)
//value found using the line - return com.atlassian.jira.security.roles.ProjectRoleActor.GROUP_ROLE_ACTOR_TYPE
final def actorType = 'atlassian-user-role-actor'
   
//def project = ComponentAccessor.projectManager.getProjectByCurrentKey(projectKey)
def projectRole = projectRoleManager.getProjectRole(roleName)
   
 
 
for(int u=0;u<userInput.size();u++)
{
    def user1name = userInput[u].getUsername()
    def exampleUser = userManager.getUserByName(user1name)
    def users = [exampleUser.key.toString()]
    printtext+="<h2><b>***********************User ${u+1}***********************</b></h2><br>"
 
    for(int p=0;p<project.size();)
    {
        try
        {
            projectRoleService.addActorsToProjectRole(users, projectRole, project[p], actorType, null)
            printtext=printtext+"User <b>${user1name}</b> has been added to the project(s)"
            printtext=printtext+" <b>${project[p]}</b> with key <b>${project[p].getKey()}</b><br>"
        }
  
        catch(Exception ex)
        {
            printtext=printtext+"Error caught for adding <b>${user1name}</b> to <b>${project[p]}</b> with key <b>${project[p].getKey()}</b>. Can only add user once.<br>"
        }
        p++
    }
    printtext+="<br>"
}
  
return printtext
