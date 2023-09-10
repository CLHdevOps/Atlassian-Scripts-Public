//deactivate users from a group in Jira
//Created on: 20th February 2023, 5:50 pm
//Last Modified: 20th February 2023, 5:50 pm
//Rinaldi Michael
  
 
import com.atlassian.jira.bc.user.UserService
import com.atlassian.jira.component.ComponentAccessor
import com.resolve.scriptrunner.*
import java.lang.String
import java.io.*
import com.atlassian.jira.bc.user.UserService
import com.atlassian.jira.component.ComponentAccessor
import com.onresolve.scriptrunner.parameters.annotation.*
import com.atlassian.jira.bc.user.UserService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.crowd.embedded.api.Group
import com.onresolve.scriptrunner.parameters.annotation.GroupPicker
  
//variable declaration for adding groups and projects
def userManager = ComponentAccessor.userManager
def groupManager = ComponentAccessor.groupManager
 
 
//variable delcaration for adding groups to the user
def userService = ComponentAccessor.getComponent(UserService)
def userUtil = ComponentAccessor.userUtil
      
@GroupPicker(label = 'Group', description = 'Pick a group', placeholder = 'Pick a group')
Group group
def userNameStrings = groupManager.getUserNamesInGroup(group)
String[] userNameStringsarray = userNameStrings.toArray()  
 
 
 
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
  
for(int i=0;i<userNameStringsarray.size();)
{
try
{
 if (userManager.getUserByName(userNameStringsarray[i]))
 {
    def updatedUser = userService.newUserBuilder(userManager.getUserByName(userNameStringsarray[i])).active(ActivateUser).build() //true - activate user, false - deactivate user
    def updateUserValidationResult = userService.validateUpdateUser(updatedUser)
  
    if (!updateUserValidationResult.valid)
      {
        log.error "Update of ${userNameStringsarray[i]} failed. ${updateUserValidationResult.errorCollection}"
        throw new Exception("Some error happened");
    }
  
     
  
    if((ActivateUser==true)&&(userManager.getUserByName(userNameStringsarray[i]).isActive()==false))
      log.info "Activated"
    else if((ActivateUser==false)&&(userManager.getUserByName(userNameStringsarray[i]).isActive()==true))
      log.info "Deactivated"
    else
      throw new Exception("Some error happened");
  
    userService.updateUser(updateUserValidationResult)
    printtext=printtext+userNameStringsarray[i]+"<br>"
      
  }
}
catch(Exception e)
    {
        log.error "Update of ${userNameStringsarray[i]} failed."
        printtext=printtext+"<nobr>${userNameStringsarray[i]} activation/deactivation not performed.<br>"
    }
  i=i+1
}
  
return printtext
