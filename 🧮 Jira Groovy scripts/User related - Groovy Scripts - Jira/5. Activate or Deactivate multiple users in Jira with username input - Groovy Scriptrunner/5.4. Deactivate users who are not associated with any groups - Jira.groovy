////////////////////////////////////////
//Author: Rinaldi Michael
//Created: 26th Oct 2022 - 11:12 am
//Last Modified: 7th April 2023 - 04:56 pm
/*
With choosing directory
Has a fail safe to reactivate users if accidentally run.
Note: Fail safe does not reactivate user accounts without an email address
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
        
//variable declaration for adding groups and projects
def userManager = ComponentAccessor.userManager
def groupManager = ComponentAccessor.groupManager      
        
//user picker to verify if the user already exists
@UserPicker(description = 'Field not Mandatory. This cell does not play a role in this script. It is only used to verify if the account already exists', label = 'User Checker. Enter the username or Full Name', multiple = true)
def user
        
//variable delcaration for adding groups to the user
def userService = ComponentAccessor.getComponent(UserService)
def userUtil = ComponentAccessor.userUtil
 
def allUsers = ComponentAccessor.getOfBizDelegator().findAll("User");
 
 
@ShortTextInput(description = 'Internal Directory: 1<br>External Directory: 12345', label = 'Enter Directory ID')
String directoryId
     
@Select(
    label = "Choose to Activate or Deactivate",
    description = "Select between the two options to activate or deactivate a user.",
    options = [
        @Option(label = "Activate", value = "true"),
        @Option(label = "Deactivate", value = "false"),
    ]
)
String ActivateUserInput
     
Boolean ActivateUser
if(ActivateUserInput=="true")
  ActivateUser = true
else
  ActivateUser = false
        
     
     
     
String printtext
        
if(ActivateUser==true)
  printtext="<H2><b>Users with no groups will now be activated</H2></b>"+"<br>"
else
  printtext="<H2><b>Users with no groups will now be de-activated</H2></b>"+"<br>"
      
      
//for loop
for(int i=0;i<50;)
{
  String userName = allUsers[i].userName
  if(groupManager.getGroupNamesForUser(userName).size()>0)
  {
    i++
    continue
  }
 
 
  try
  {
    if (userManager.getUserByName(userName)&&(userManager.getUserByName(userName).getDirectoryId()==directoryId.toLong()))
    {
       
      if((ActivateUser==true)&&(userManager.getUserByName(userName).isActive()==true))
      {
        printtext+="<b>${userName}</b> is already active!<br>"
        i++
        continue;
      }
      if((ActivateUser==false)&&(userManager.getUserByName(userName).isActive()==false))
      {
        printtext+="<b>${userName}</b> is already inactive!<br>"
        i++
        continue;
      }
       
           
      def updatedUser = userService.newUserBuilder(userManager.getUserByName(userName)).active(ActivateUser).build() //true - activate user, false - deactivate user
      def updateUserValidationResult = userService.validateUpdateUser(updatedUser)
        
      if (!updateUserValidationResult.valid)
      {
        log.error "Update of ${userName} failed. ${updateUserValidationResult.errorCollection}"
        throw new Exception("${updateUserValidationResult.getErrorCollection()}");
      }
        
      if((ActivateUser==true)&&(userManager.getUserByName(userName).isActive()==false))
      {
        log.info "Activated"     
        printtext+="<b>"+userName+"</b><br>"
    
      }
      else if((ActivateUser==false)&&(userManager.getUserByName(userName).isActive()==true))
      {
        log.info "Deactivated"
        printtext+="<b>"+userName+"</b><br>"   
      }
       
      userService.updateUser(updateUserValidationResult)
    }
  } //end of try block
      
    catch(Exception ex)
    {
      log.error "Update of ${userName} failed."
      printtext=printtext+"<b>${userName}</b> activation/deactivation not performed due to errors -> ${ex}<br>"
    }
      
i=i+1
}//end of for loop
        
return printtext
