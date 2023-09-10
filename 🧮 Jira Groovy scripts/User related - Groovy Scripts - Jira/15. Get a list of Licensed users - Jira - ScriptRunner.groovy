///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////Rinaldi Michael///////////////////
/////////Created in 7th July 2023, 02:35pm ////////
/////Modified in 10th September 2023, 12:26pm///////////
//References:                           ///////////
//https://community.atlassian.com/t5/Jira-Software-questions/To-extract-all-JIRA-users-and-their-last-access-date/qaq-p/1613347
//https://community.atlassian.com/t5/Jira-Service-Management/get-the-creation-date-of-a-user/qaq-p/802290
//This script was created for a rest endpoint. Hence, the map output instead of a tabular format.
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
import com.atlassian.jira.application.*
 
                
//variable declaration for adding groups and projects
def userManager = ComponentAccessor.userManager
def groupManager = ComponentAccessor.groupManager
def loginManager = ComponentAccessor.getComponent(LoginManager)
                
//variable delcaration for adding groups to the user
def userService = ComponentAccessor.getComponent(UserService)
def userUtil = ComponentAccessor.userUtil               
 
//get all users in Jira and set up the output
def allUsers = ComponentAccessor.getOfBizDelegator().findAll("User");
 
//Get licensed groups and users
def defaultApplicationRoleManager = ComponentAccessor.getComponent(DefaultApplicationRoleManager)
def allLicensedGroupRoles = defaultApplicationRoleManager.getRoles()[0].groups
Set<String> licensedGroups = new LinkedHashSet<String>();
for(int l=0;l<allLicensedGroupRoles.size();l++)
{
    licensedGroups.add(allLicensedGroupRoles[l].getName())
}
def licensedUsers = groupManager.getNamesOfDirectMembersOfGroups(licensedGroups,allUsers.size())
   
//Variable declaration of Array List for output
List<Map<String , String>> allUsersMap  = new ArrayList<Map<String,String>>();
int activeUserCounter = 0
       
//store results in output variable
for(int i=0;i<allUsers.size();i++)
{
    if((allUsers[i].active==0))
    {
        continue;
    }
   
    if(groupManager.isUserInGroups(userManager.getUserByName(allUsers[i].userName.toString()),licensedGroups)==false)
        continue;
   
    Map<String,String> user = new HashMap<String, String>();
    try
    {
        user.put("Display Name",allUsers[i].displayName.toString())
        user.put("Username",allUsers[i].userName.toString())
        user.put("Email Address",allUsers[i].emailAddress.toString())
        user.put("Active",allUsers[i].active.toString())
        user.put("Directory",allUsers[i].directoryId.toString())
        user.put("Login Date",new Date(loginManager.getLoginInfo(allUsers[i].userName.toString()).getLastLoginTime()).toString())
        user.put("Creation Date",allUsers[i].createdDate.toString())
        user.put("Groups",groupManager.getGroupNamesForUser(allUsers[i].userName.toString()).toString())
    }
    catch(Exception ex)
    {
        user.put("Display Name",allUsers[i].displayName.toString())
        user.put("Username",allUsers[i].userName.toString())
        user.put("Email Address",allUsers[i].emailAddress.toString())
        user.put("Active",allUsers[i].active.toString())
        user.put("Directory",allUsers[i].directoryId.toString())
        user.put("Login Date",null)
        user.put("Creation Date",allUsers[i].createdDate.toString())
        user.put("Groups",groupManager.getGroupNamesForUser(allUsers[i].userName.toString()).toString())
    }
   
       
    allUsersMap.add(activeUserCounter,user)
    activeUserCounter++
}
  
return allUsersMap
