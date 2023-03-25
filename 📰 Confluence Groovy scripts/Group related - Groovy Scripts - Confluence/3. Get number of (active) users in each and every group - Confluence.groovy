//Rinaldi Michael
//Modified in 27th January 2023, 11:21am
       
import java.lang.String
import java.io.*
import com.atlassian.confluence.security.login.LoginManager
import com.atlassian.confluence.user.UserAccessor
import com.atlassian.confluence.user.*
import com.atlassian.sal.api.component.ComponentLocator
import com.atlassian.user.GroupManager
import com.onresolve.scriptrunner.parameters.annotation.*
import com.atlassian.crowd.embedded.api.Group
import com.atlassian.user.GroupManager
import com.atlassian.user.UserManager
       
            
//variable declaration for users
def groupManager = ComponentLocator.getComponent(GroupManager)
def loginManager = ComponentLocator.getComponent(LoginManager)
def userAccessor = ComponentLocator.getComponent(UserAccessor)
def userManager = ComponentLocator.getComponent(UserManager)
 
//for printing values
String printtext
  
//get all groups list
def allGroupsInConfluence = groupManager.groups.asList()
 
//return groupManager.getGroup(allGroupsInConfluence[0].getName())
//return groupManager.getExternalMemberNames(allGroupsInConfluence[0]).size()
 
//for getting users in a group 
for(int g=0;g<allGroupsInConfluence.size();)
{
    int userCount=0
    def group1 = groupManager.getGroup(allGroupsInConfluence[g].getName())
    def usersFromGroup = groupManager.getMemberNames(groupManager.getGroup(group1.name)).asList()
    printtext+="<br>${g+1}. <b>${allGroupsInConfluence[g].getName()}</b>"
    for(int i=0;i<usersFromGroup.size();)
    {
        if(userAccessor.isDeactivated(userAccessor.getUserByName(usersFromGroup[i]))==false)
        {
        userCount++
        }
    i++
    }
    printtext+="*${userCount}"
g++
}//end of first for loop
  
return "<br>S.No. || Name of Group || Number of active users in the group<br>"+printtext
