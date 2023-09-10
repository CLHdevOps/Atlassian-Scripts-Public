/////////////////Rinaldi Michael///////////////////
/////////Created in 18th Mar 2023, 6:46pm//////////
/////Modified in                      /////////////
///////////////////////////////////////////////////
 
 
import com.onresolve.scriptrunner.parameters.annotation.ShortTextInput
import java.lang.String
import com.atlassian.jira.bc.user.UserService
import com.atlassian.jira.component.ComponentAccessor
import org.apache.log4j.Logger
import org.apache.log4j.Level
import com.atlassian.jira.user.util.UserUtil
import com.onresolve.scriptrunner.annotation.*
 
def loggedInUser = ComponentAccessor.jiraAuthenticationContext.loggedInUser
def userService = ComponentAccessor.getComponent(UserService)
def userUtil = ComponentAccessor.userUtil
def userManager = ComponentAccessor.userManager
def grpMgr = ComponentAccessor.getGroupManager()
def userAccessor = ComponentAccessor.getComponent(UserUtil)
 
def allUsers = userManager.getAllApplicationUsers()
String printtext=""
 
@ShortTextInput(description = 'Internal Directory: 1<br>External Directory: 12345', label = 'Enter Directory ID')
String directoryId
 
printtext+="<h2><b>List of Users from Directory Number: ${directoryId}</h2></b><br>"
int count=0
 
for(int u=0;u<allUsers.size();u++)
{
    def userIsNotNull = userManager.findUserInDirectory(allUsers[u].getUsername(),directoryId.toLong())
    if(userIsNotNull!=null)
    {
        count++
    }
}
printtext+="<br> Number of users: ${count}<br><br>"
 
count=1
 
for(int u=0;u<allUsers.size();u++)
{
    def userIsNotNull = userManager.findUserInDirectory(allUsers[u].getUsername(),directoryId.toLong())
    if(userIsNotNull!=null)
    {
        printtext=printtext+(count).toString()+". "+allUsers[u].getUsername()+"<br>"
        count++
    }
}
 
return printtext
