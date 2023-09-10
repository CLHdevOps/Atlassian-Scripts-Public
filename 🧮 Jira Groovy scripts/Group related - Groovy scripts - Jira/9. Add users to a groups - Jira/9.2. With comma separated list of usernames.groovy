/////////////////Rinaldi Michael///////////////////
/////////Created in 31st March 2023, 2:40 pm///////
/////Modified in 3rd April 2023, 04:05 pm//////////
///////////////////////////////////////////////////
         
import java.lang.String
import java.io.*
import com.atlassian.crowd.model.group.Groups
import com.atlassian.crowd.embedded.api.Group
import com.atlassian.jira.bc.user.UserService
import com.atlassian.jira.component.ComponentAccessor
import com.onresolve.scriptrunner.parameters.annotation.*
import com.atlassian.jira.bc.user.UserService
import com.atlassian.jira.user.*
     
//user picker to verify if the user already exists
//@UserPicker(description = '', label = 'Select the user/s (Required🌟)', multiple = true)
//List<ApplicationUser> user
 
@ShortTextInput(label = "Usernames (Required🌟)", description = "Provide a comma separated list of usernames you want to add to the group/s")
String usernames
String[] users = usernames.split(',')
 
//User group input
@GroupPicker(description = 'Select the group/s from the dropdown.', label = 'Enter the User Groups (Required🌟)', multiple = true, placeholder = 'Select group')
List<Group> groupone 
              
//variable declaration for adding groups and projects
def userManager = ComponentAccessor.userManager
def groupManager = ComponentAccessor.groupManager
def userService = ComponentAccessor.getComponent(UserService)
def userUtil = ComponentAccessor.userUtil
String printtext=""
  
for(int u=0;u<users.size();u++)
{
    def user=userManager.getUserByName(users[u])
    def groupsThatTheUserHas = groupManager.getGroupNamesForUser(user)
    String alreadyInGroup = "no"
    printtext=printtext+"${u+1}. User - <b>${user.displayName}</b> (${user.emailAddress})  has been added to - "
    for(int g=0;g<groupone.size();g++)
    { 
        for(int check=0;check<groupsThatTheUserHas.size();check++)
        {
            if(groupone[g].getName()==groupsThatTheUserHas[check].toString())
                alreadyInGroup="yes"
        }
  
        if(alreadyInGroup=="yes")
            continue
  
        Group group1 = groupone[g]
        assert group1 : "Could not find group with name $groupone"
                
        //def userToAddone = userManager.getUserByName(user[u].username)
        userUtil.addUserToGroups(groupone, user)
        printtext=printtext+"<b>${group1.getName()}</b>, "
    }
    printtext=printtext[0..printtext.size()-3] //remove the last comma returned
    printtext+=". This user was already in <b>${groupsThatTheUserHas}</b>"
    printtext=printtext+"<br><br>"
}
  
return printtext 
