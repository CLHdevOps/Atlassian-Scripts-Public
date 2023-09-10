////////////////////////////////////////
//Author: Rinaldi Michael
//Created: 19th Mar 2023, 04:40 pm
//Last Modified: 21st Mar 2023, 10:50 am
//https://library.adaptavist.com/entity/change-project-lead
/*
Algorithm (for script 1)
Mostly works when Internal Directory order is higher than External Directory. Can try if it works otherwise
1. Fetch the first user account (based on the first directory input)
2. Fetch projects and components which this user is a lead of
3. Set project lead/s and/or component lead/s to any other user account (if they are a lead of either)
4. Deactivate the user's second account (based on the first directory input)
5. Set project lead/s and/or component lead/s to the user's account (from the second directory) (if they were a lead of either)
*/
////////////////////////////////////////
 
import com.resolve.scriptrunner.*
import java.lang.String
import java.io.*  
import com.onresolve.scriptrunner.parameters.annotation.UserPicker
import com.atlassian.jira.bc.user.UserService
import com.atlassian.jira.component.ComponentAccessor
import com.onresolve.scriptrunner.parameters.annotation.*
import com.onresolve.scriptrunner.parameters.annotation.meta.*
import com.atlassian.jira.project.Project
import com.atlassian.jira.project.UpdateProjectParameters
import com.atlassian.jira.user.ApplicationUser
import com.onresolve.scriptrunner.canned.util.OutputFormatter
import com.onresolve.scriptrunner.parameters.annotation.ProjectPicker
import com.atlassian.jira.bc.project.component.MutableProjectComponent
import com.atlassian.jira.bc.EntityNotFoundException
        
//variable declaration for adding groups and projects
def userManager = ComponentAccessor.userManager
def projectManager = ComponentAccessor.projectManager
def userService = ComponentAccessor.getComponent(UserService)
def projectComponentManager = ComponentAccessor.projectComponentManager
String printtext=""
   
@ShortTextInput(label = "User List", description = "Enter user comma list")
String userInput
String[] usersArray = userInput.split(',')
 
@ShortTextInput(label = "Directory to deactivate from", description = "Type in the Directory number for which you want to deactivate the user from.<br>Internal: 1<br>External directory: 12345")
String Dir1
 
@ShortTextInput(label = "Directory to replace from", description = "Type in the Directory number for which you want the same lead to be from.<br>Internal: 1<br>External directory: 12345")
String Dir2
  
for(int u=0;u<usersArray.size();u++)
{
  try // try block for this user
  {
    def user = userManager.findUserInDirectory(usersArray[u],Dir1.toLong())
    def projects = projectManager.getProjectsLeadBy(user)
    def components = projectComponentManager.findComponentsByLead(user.getUsername())
    def newLead = userManager.getUserByName("<type in username of the temporary account>")
   
  //replace project lead with temporary user
  if(projects.size()>0)
  {
    projects.each
    {
      def params = UpdateProjectParameters.forProject(it.id).leadUserKey(newLead.key)
      projectManager.updateProject(params)
    }
  }
   
  //replace component lead with temporary user
  if(components.size()>0)
  {
    for(int c=0;c<components.size();c++)
    {
      MutableProjectComponent newProjectComponent = MutableProjectComponent.copy(components[c])
      newProjectComponent.setLead(newLead.getKey())
   
      try
      {
        projectComponentManager.update(newProjectComponent);
        printtext+="<br><br>Component- <b>${components[c]}'s</b> lead is set from <b>${user.getUsername()},(${user.getEmailAddress()})</b> to <b>${newLead.getName()},(${newLead.getEmailAddress()})</b>"
      }
      catch (EntityNotFoundException e)
      {     
       log.info("Setting to temporary user did not work for Component- <b>${components[c]}")
      }
    }
  }
 
 
  //deactivate the user account from the internal directory
  try
  {
    if (user&&(user.getDirectoryId()==1))
    {
         
      def updatedUser = userService.newUserBuilder(userManager.getUserByKey(user.getKey())).active(false).build() //true - activate user, false - deactivate user
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
 
   
  //fetch the user from the second directory
  newLead = userManager.findUserInDirectory(user.getUsername(),Dir2.toLong())
 
  //set the project lead to the user from the second directory
  if(projects.size()>0)
  {
    projects.each
    {
      def params = UpdateProjectParameters.forProject(it.id).leadUserKey(newLead.getKey())
      projectManager.updateProject(params)
      printtext=printtext.concat("<br>Updated ${it.key} project lead to ${newLead.name}")
    }
  }
 
  //replace component lead with the user from the second directory
  if(components.size()>0)
  {
    for(int c=0;c<components.size();c++)
    {
      MutableProjectComponent newProjectComponent = MutableProjectComponent.copy(components[c])
      newProjectComponent.setLead(newLead.getKey())
   
      try
      {
        projectComponentManager.update(newProjectComponent);
        printtext+="<br><br>Component- <b>${components[c]}'s</b> lead is set from <b><type in Display Name of the temporary account>,(<type in username of the temporary account>)</b> to <b>${newLead.getName()},(${newLead.getEmailAddress()})</b>"
      }
      catch (EntityNotFoundException e)
      {     
        log.info("Setting to <type in username of the temporary account> did not work for Component- <b>${components[c]}")
      }
    }
  }
 
  printtext+="<br>Ther user account of <b>${usersArray[u]} from ${Dir1}>/b> is deactivated and it's own project leads/component leads are replaced with <b>${usersArray[u]} from ${Dir2}>/b>"
  }//End of try block for this user
   
  catch(Exception ex) //catch any errors returned for this user
  {
    printtext+="<br>An error was thrown for the user - ${usersArray[u]}. Likely that the user does not exist in both directories. View error logs below.<br>Error -> ${ex}"
  }
  
}
  
return printtext
