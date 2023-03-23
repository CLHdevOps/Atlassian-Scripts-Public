/////////////////Rinaldi Michael///////////////////
/////////Created in 18th Mar 2023, 6:46pm//////////
/////Modified in 19th Mar 2023, 11:53 am///////////
///////////////////////////////////////////////////
//Details returned
//Username and Email Address
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
  
@ShortTextInput(description = 'Internal Directory: 1<br>Check user Directory settings for External Directory ID', label = 'Enter the first Directory ID which has most users')
String directoryId1
 
@ShortTextInput(description = 'Internal Directory: 1<br>Check user Directory settings for External Directory ID', label = 'Enter the second Directory ID which has least users')
String directoryId2
  
int count=0
Set<String> usersFromDir1 = new HashSet<String>();
for(int u=0;u<allUsers.size();u++)
{
    def userIsNotNull = userManager.findUserInDirectory(allUsers[u].getUsername(),directoryId1.toLong())
    if(userIsNotNull!=null)
    {
        String data = "${allUsers[u].getUsername()}, <b>${allUsers[u].getEmailAddress()}</b>"
        usersFromDir1.add(data)
        count++
    }
}
printtext+="<h2><b>Number of Users from Directory Number: ${directoryId1} is ${count}</h2></b><br>"
 
 
count=0
Set<String> usersFromDir2 = new HashSet<String>();
for(int u=0;u<allUsers.size();u++)
{
    def userIsNotNull = userManager.findUserInDirectory(allUsers[u].getUsername(),directoryId2.toLong())
    if(userIsNotNull!=null)
    {
        String data = "${allUsers[u].getUsername()}, <b>${allUsers[u].getEmailAddress()}</b>"
        usersFromDir2.add(data)
        count++
    }
}
printtext+="<h2><b>Number of Users from Directory Number: ${directoryId2} is ${count}</h2></b><br>"
 
usersFromDir1.retainAll(usersFromDir2)
 
printtext+="<h2><b>Users present in both Directories: ${usersFromDir1.size()} users</h2></b><br><br>"
for(int f=0;f<usersFromDir1.size();f++)
{
  printtext+="${f+1}. ${usersFromDir1[f]}<br>"
}
  
return printtext
