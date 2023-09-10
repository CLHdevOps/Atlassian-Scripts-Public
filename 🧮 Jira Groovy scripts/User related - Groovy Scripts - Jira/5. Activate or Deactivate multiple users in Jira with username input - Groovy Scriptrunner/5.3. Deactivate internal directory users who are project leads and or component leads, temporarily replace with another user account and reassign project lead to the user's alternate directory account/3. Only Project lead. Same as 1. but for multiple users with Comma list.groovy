////////////////////////////////////////
//Author: Rinaldi Michael
//Created: 19th Mar 2023, 04:40 pm
//Last Modified: 19th Mar 2023, 8:00 pm
//https://library.adaptavist.com/entity/change-project-lead
/*
Algorithm (for Project Lead scripts 2 and 3)
Works when Internal Directory order is higher than External Directory
1. Fetch user (internal directory is chosen by default)
2. Fetch projects which this user is a lead of
3. Set project lead/s of the project/s to <type in username of temporary user account>
4. Deactivate the user's internal directory account
5. Set project lead/s of the project/s to the user's account. The External Directory account is chosen by default since the internal directory account is deactivated.
*/
////////////////////////////////////////
        
import com.onresolve.scriptrunner.parameters.annotation.UserPicker
import com.atlassian.jira.bc.user.UserService
import com.atlassian.jira.component.ComponentAccessor
import com.resolve.scriptrunner.*
import java.lang.String
import java.io.*
import com.atlassian.jira.bc.user.UserService
import com.atlassian.jira.component.ComponentAccessor
import com.onresolve.scriptrunner.parameters.annotation.*
import com.onresolve.scriptrunner.parameters.annotation.meta.*
import com.atlassian.jira.bc.user.UserService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.project.Project
import com.atlassian.jira.project.UpdateProjectParameters
import com.atlassian.jira.user.ApplicationUser
import com.onresolve.scriptrunner.canned.util.OutputFormatter
import com.onresolve.scriptrunner.parameters.annotation.ProjectPicker
import com.onresolve.scriptrunner.parameters.annotation.UserPicker
import com.atlassian.jira.bc.user.UserService
import com.atlassian.jira.component.ComponentAccessor
import com.resolve.scriptrunner.*
import java.lang.String
import java.io.*
import com.atlassian.jira.bc.user.UserService
import com.atlassian.jira.component.ComponentAccessor
import com.onresolve.scriptrunner.parameters.annotation.*
import com.onresolve.scriptrunner.parameters.annotation.meta.*
import com.atlassian.jira.bc.user.UserService
import com.atlassian.jira.component.ComponentAccessor
        
//variable declaration for adding groups and projects
def userManager = ComponentAccessor.userManager
def projectManager = ComponentAccessor.projectManager
def userService = ComponentAccessor.getComponent(UserService)
String printtext=""
   
@ShortTextInput(label = "User List", description = "Enter user comma list")
String userInput
String[] usersArray = userInput.split(',')
  
for(int u=0;u<usersArray.size();u++)
{
    def user = userManager.findUserInDirectory(usersArray[u],1)
    def projects = projectManager.getProjectsLeadBy(user)
    def newProjectLead = userManager.getUserByName("<type in username of temporary user account>")
   
   
  projects.each
  {
    def params = UpdateProjectParameters.forProject(it.id).leadUserKey(newProjectLead.key)
    projectManager.updateProject(params)
  }
   
  try
  {
    if (user&&(user.getDirectoryId()==1))
    {
         
      def updatedUser = userService.newUserBuilder(userManager.getUserByName(user.getUsername())).active(false).build() //true - activate user, false - deactivate user
      def updateUserValidationResult = userService.validateUpdateUser(updatedUser)
          
      if (!updateUserValidationResult.valid)
      {
        log.error "Update of ${user.getUsername()} failed. ${updateUserValidationResult.errorCollection}"
        throw new Exception("${updateUserValidationResult.getErrorCollection()}");
      }
         
      userService.updateUser(updateUserValidationResult)
    }
  } //end of try block
        
  catch(Exception ex)
  {
    log.error "Update of ${user.getUsername()} failed."
    printtext=printtext+"<br><br><b>${user.getUsername()}</b> activation/deactivation not performed due to errors -> ${ex}<br>"
  }
   
  newProjectLead = userManager.getUserByName(user.getUsername())
  projects.each
  {
    def params = UpdateProjectParameters.forProject(it.id).leadUserKey(newProjectLead.key)
    projectManager.updateProject(params)
  }
   
  
  projects.each
  { project ->
    printtext=printtext.concat("<br>Updated ${project.key} project lead to ${newProjectLead.name}")
  }
  
}
  
return printtext
