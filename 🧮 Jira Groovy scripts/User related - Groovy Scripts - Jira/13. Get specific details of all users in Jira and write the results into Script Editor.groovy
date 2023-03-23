///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////Rinaldi Michael///////////////////
/////////Created in 27th Feb 2023, 06:38 pm////////
/////Modified in 16th Mar 2023, 11:37 am//////////
//References:
//https://community.atlassian.com/t5/Jira-Software-questions/To-extract-all-JIRA-users-and-their-last-access-date/qaq-p/1613347
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Details that will be returned:
//Display Name, Username, Email Address, Is active, Directory ID, Last Login Time, Groups
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      
import java.lang.String
import java.io.*
import com.atlassian.crowd.model.group.Groups
import com.atlassian.crowd.embedded.api.Group
import com.atlassian.jira.bc.user.UserService
import com.atlassian.jira.component.ComponentAccessor
import com.onresolve.scriptrunner.parameters.annotation.*
import com.atlassian.jira.bc.user.UserService
import com.atlassian.jira.user.*
import com.atlassian.jira.security.login.*
  
      
           
//variable declaration for adding groups and projects
def userManager = ComponentAccessor.userManager
def groupManager = ComponentAccessor.groupManager
def loginManager = ComponentAccessor.getComponent(LoginManager)
           
//variable delcaration for adding groups to the user
def userService = ComponentAccessor.getComponent(UserService)
def userUtil = ComponentAccessor.userUtil 
            
def allUsers = userManager.getAllApplicationUsers()
String printtext="<br>"
printtext="Display Name*Username*Email Address*Active*Directory ID*Last Login Date*Groups\n\n"
  
@ShortTextInput(description = 'Enter any file name. This will create a new file in Script Editor.', label = 'Enter the FileName')
String fileName
  
 
  
for(int i=0;i<allUsers.size();i++)
{
    try
    {
        printtext=printtext+"${allUsers[i].getDisplayName()}*${allUsers[i].getUsername()}*${allUsers[i].getEmailAddress()}*${userManager.getUserByKey(allUsers[i].getKey()).isActive()}*${allUsers[i].getDirectoryId()}*${new Date(loginManager.getLoginInfo(allUsers[i].getUsername()).getLastLoginTime())}*${groupManager.getGroupNamesForUser(allUsers[i].getUsername())}\n"
    }
    catch(Exception ex)
    {
        printtext=printtext+"${allUsers[i].getDisplayName()}*${allUsers[i].getUsername()}*${allUsers[i].getEmailAddress()}*${userManager.getUserByKey(allUsers[i].getKey()).isActive()}*${allUsers[i].getDirectoryId()}**${groupManager.getGroupNamesForUser(allUsers[i].getUsername())}\n"
    }
}
  
new File("/home/jira/shared_home/scripts/${fileName}.groovy").withWriter('utf-8')
{
    writer -> writer.writeLine printtext
} 
   
return "The contents are written into <b>${fileName}.groovy</b> in the Script Editor."
