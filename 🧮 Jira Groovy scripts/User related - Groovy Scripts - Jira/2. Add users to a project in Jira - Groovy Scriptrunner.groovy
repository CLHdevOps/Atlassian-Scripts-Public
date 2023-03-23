//Author: Rinaldi Michael
//Modified in 14th Nov 2022

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
@UserPicker(description = 'Search for the user', label = 'User', multiple = false)
ApplicationUser userInput
 
def user1name = userInput.getUsername()
  
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
  
def exampleUser = userManager.getUserByName(user1name)
def users = [exampleUser.key.toString()]
  
 
printtext=printtext+"<b>Added to the project(s)</b>"
 
 
for(int p=0;p<project.size();)
{
    try
    {
    projectRoleService.addActorsToProjectRole(users, projectRole, project[p], actorType, null)
    printtext=printtext+"<br>${project[p]}<br>"
    //projectRoleService.removeActorsFromProjectRole(users, projectRole, project, actorType, null)
    }
 
    catch(Exception ex)
    {
        printtext=printtext+"<br>Error caught for ${project[p]}. Can only add user once."
    }
    p++
}
 
return printtext
