//Rinaldi Michael
//Modified in 27th January 2023, 11:46am
     
import java.lang.String
import java.io.*
import com.atlassian.crowd.model.group.Groups
import com.atlassian.crowd.embedded.api.Group
import com.atlassian.jira.bc.user.UserService
import com.atlassian.jira.component.ComponentAccessor
import com.onresolve.scriptrunner.parameters.annotation.*
import com.atlassian.jira.bc.user.UserService
import com.atlassian.jira.component.ComponentAccessor
import java.lang.String
import com.atlassian.jira.component.ComponentAccessor
import com.onresolve.scriptrunner.parameters.annotation.*
import com.atlassian.jira.user.*
  
//variable declaration for adding groups and projects
def userManager = ComponentAccessor.userManager
def groupManager = ComponentAccessor.groupManager
          
//variable delcaration for adding groups to the user
def userService = ComponentAccessor.getComponent(UserService)
def userUtil = ComponentAccessor.userUtil
  
//for printing values
String printtext
  
//get all Groups in Jira
def allGroupsInJira = groupManager.allGroups
    
for(int g=0;g<allGroupsInJira.size();g++)
{
    int userCount = 0
    def usersFromGroup = groupManager.getUserNamesInGroup(allGroupsInJira[g])
    int inGroup=0
    printtext+="<br>${g+1}. <b>${allGroupsInJira[g].getName()}</b>"
        for(int i=0;i<usersFromGroup.size();)
        {
            if(userManager.getUserByName(usersFromGroup[i]).isActive()==true)
            {
                userCount++
            }
        i++
        }
    printtext+="*${userCount}"
}
 
  
return "<br>S.No. || Name of Group || Number of Users in the group<br>"+printtext
