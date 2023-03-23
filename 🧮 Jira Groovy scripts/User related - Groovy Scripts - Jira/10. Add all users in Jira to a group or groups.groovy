//Rinaldi Michael
//Modified in 12th December 2022, 09:23 pm
    
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
import java.lang.String
import com.atlassian.jira.project.Project
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.security.roles.ProjectRoleManager
import com.atlassian.jira.bc.projectroles.ProjectRoleService
import com.onresolve.scriptrunner.parameters.annotation.*
import com.atlassian.jira.user.*
    
         
//variable declaration for adding groups and projects
def userManager = ComponentAccessor.userManager
def groupManager = ComponentAccessor.groupManager
         
//variable delcaration for adding groups to the user
def userService = ComponentAccessor.getComponent(UserService)
def userUtil = ComponentAccessor.userUtil
         
//variable declaration for adding projects
def projectRoleManager = ComponentAccessor.getComponent(ProjectRoleManager)
def projectRoleService = ComponentAccessor.getComponent(ProjectRoleService)
         
 
@GroupPicker(description = 'Select the group/s from the dropdown', label = 'Enter the User Groups (*Required)', multiple = true, placeholder = 'Select group')
List<Group> groupone       
     
def loggedInUser = ComponentAccessor.jiraAuthenticationContext.loggedInUser
     
def allUsers = userManager.getAllApplicationUsers()
  
String printtext
    
for(int i=0;i<allUsers.size();)
{
printtext=printtext+allUsers[i].getDisplayName()+"*"+allUsers[i].getEmailAddress()+"<br>"
 
                    for(int g=0;g<groupone.size();)
                    {
                        Group group1 = groupone[g]
                        assert group1 : "Could not find group with name $groupone"
           
                        def userToAddone = userManager.getUserByName(allUsers[i].getUsername())
                        userUtil.addUserToGroups(groupone, userToAddone)
                        printtext=printtext.concat("<b>${group1.getName()}</b>").concat(", ")
                        g++
                    }
 
 
 
i++
}
    
return printtext
