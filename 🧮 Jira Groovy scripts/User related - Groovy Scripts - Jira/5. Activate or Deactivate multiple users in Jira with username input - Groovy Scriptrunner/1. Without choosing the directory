////////////////////////////////////////
//Author: Rinaldi Michael
//Created: 26th Oct 2022 - 11:12 am
//Last Modified: 15th Mar 2023 - 11:28 am
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
     
     
//user picker to verify if the user already exists
@UserPicker(description = 'Field not Mandatory. This cell does not play a role in this script. It is only used to verify if the account already exists', label = 'User Checker. Enter the username or Full Name', multiple = true)
def user
     
//variable delcaration for adding groups to the user
def userService = ComponentAccessor.getComponent(UserService)
def userUtil = ComponentAccessor.userUtil
     
     
@ShortTextInput(description = 'Enter the usernames list', label = 'Enter a comma separated list of users')
String userNameStrings
String[] userNameStringsarray
userNameStringsarray=userNameStrings.split(',')
  
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
  printtext="<H2><b>Users will now be activated</H2></b>"+"<br>"
else
  printtext="<H2><b>Users will now be de-activated</H2></b>"+"<br>"
   
   
//for loop
for(int i=0;i<userNameStringsarray.size();)
{
  try
  {
    if (userManager.getUserByName(userNameStringsarray[i]))
    {
    
      if((ActivateUser==true)&&(userManager.getUserByName(userNameStringsarray[i]).isActive()==true))
      {
        printtext+="${i+1}. <b>${userNameStringsarray[i]}</b> is already active!<br>"
        i++
        continue;
      }
      if((ActivateUser==false)&&(userManager.getUserByName(userNameStringsarray[i]).isActive()==false))
      {
        printtext+="${i+1}. <b>${userNameStringsarray[i]}</b> is already inactive!<br>"
        i++
        continue;
      }
    
        
      def updatedUser = userService.newUserBuilder(userManager.getUserByName(userNameStringsarray[i])).active(ActivateUser).build() //true - activate user, false - deactivate user
      def updateUserValidationResult = userService.validateUpdateUser(updatedUser)
     
      if (!updateUserValidationResult.valid)
      {
        log.error "Update of ${userNameStringsarray[i]} failed. ${updateUserValidationResult.errorCollection}"
        throw new Exception("${updateUserValidationResult.getErrorCollection()}");
      }
     
      if((ActivateUser==true)&&(userManager.getUserByName(userNameStringsarray[i]).isActive()==false))
      {
        log.info "Activated"        
        printtext+="${i+1}. <b>"+userNameStringsarray[i]+"</b><br>"
 
      }
      else if((ActivateUser==false)&&(userManager.getUserByName(userNameStringsarray[i]).isActive()==true))
      {
        log.info "Deactivated"
        printtext+="${i+1}. <b>"+userNameStringsarray[i]+"</b><br>"      
      }
    
      userService.updateUser(updateUserValidationResult)
    }
  } //end of try block
   
    catch(Exception ex)
    {
      log.error "Update of ${userNameStringsarray[i]} failed."
      printtext=printtext+"${i+1}. <b>${userNameStringsarray[i]}</b> activation/deactivation not performed due to errors -> ${ex}<br>"
    }
   
i=i+1
}//end of for loop
     
return printtext
