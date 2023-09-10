///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////Rinaldi Michael///////////////////
/////////Created in 27th Feb 2023, 06:38 pm////////
/////Modified in 27th Mar 2023, 05:10 pm///////////
//References:
//https://community.atlassian.com/t5/Jira-Software-questions/To-extract-all-JIRA-users-and-their-last-access-date/qaq-p/1613347
//https://community.atlassian.com/t5/Jira-Service-Management/get-the-creation-date-of-a-user/qaq-p/802290
/*
Results in Script Editor:
The results will be written into a new file that will be created by the script in ScriptRunner's Script Editor. 
The delimiter set is *
These results can be copied using CTRL+A into a Google sheet and Split into columns (in-built tool) with *

3. Details that will be returned (New method of fetching all users!)
Display Name, Username, Email Address, Is active, Directory ID, Last Login Time, Created Date, Groups
This new method returns more results. Additional data is unclear as of now. But the number of active users in all methods remain the same.
*/
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
   
/*
def Testusers = ComponentAccessor.getOfBizDelegator().findAll("User");
return Testusers[0]
def usernames=new ArrayList<String>();
Testusers.each
{user ->
usernames.add(user.getString("createdDate"))
}
return Testusers[0].createdDate
*/    
            
//variable declaration for adding groups and projects
def userManager = ComponentAccessor.userManager
def groupManager = ComponentAccessor.groupManager
def loginManager = ComponentAccessor.getComponent(LoginManager)
            
//variable delcaration for adding groups to the user
def userService = ComponentAccessor.getComponent(UserService)
def userUtil = ComponentAccessor.userUtil
             
def allUsers = ComponentAccessor.getOfBizDelegator().findAll("User");
String printtext="<br>"
printtext="Display Name*Username*Email Address*Active*Directory ID*Last Login Date*Creation Date*Groups\n\n"
   
@ShortTextInput(description = 'Enter any file name. This will create a new file in Script Editor.', label = 'Enter the FileName')
String fileName
   
 
 
   
for(int i=0;i<allUsers.size();i++)
{
    try
    {
        printtext=printtext+"${allUsers[i].displayName}*${allUsers[i].userName}*${allUsers[i].emailAddress}*${allUsers[i].active}*${allUsers[i].directoryId}*${new Date(loginManager.getLoginInfo(allUsers[i].userName.toString()).getLastLoginTime())}*${allUsers[i].createdDate}*${groupManager.getGroupNamesForUser(allUsers[i].userName.toString())}\n"
    }
    catch(Exception ex)
    {
        printtext=printtext+"${allUsers[i].displayName}*${allUsers[i].userName}*${allUsers[i].emailAddress}*${allUsers[i].active}*${allUsers[i].directoryId}**${allUsers[i].createdDate}*${groupManager.getGroupNamesForUser(allUsers[i].userName.toString())}\n"
    }
}
 
printtext=printtext.replace("\t","")
   
new File("/home/jira/shared_home/scripts/${fileName}.groovy").withWriter('utf-8')
{
    writer -> writer.writeLine printtext
}
    
return "The contents are written into <b>${fileName}.groovy</b> in the Script Editor."
