/////////////////Rinaldi Michael///////////////////
/////////Created in 31st March 2023, 3:50 pm///////
/////Modified in                        ///////////
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
 
//User group input
@GroupPicker(description = 'Select the group/s from the dropdown.', label = 'Enter the User Group/s you want to fetch the users from (Required🌟)', multiple = true, placeholder = 'Select group')
List<Group> groupone 
  
//User group input
@GroupPicker(label = 'Enter the User Group you want to add the users from the first group to (Required🌟)', description = 'Select one group from the dropdown.', placeholder = 'Select group')
Group grouptwo
             
//variable declaration for adding groups and projects
def userManager = ComponentAccessor.userManager
def groupManager = ComponentAccessor.groupManager
def userService = ComponentAccessor.getComponent(UserService)
def userUtil = ComponentAccessor.userUtil
String printtext=""
 
def users = groupManager.getUserNamesInGroups(groupone)
 
for(int u=0;u<users.size();u++)
{
    def user = userManager.getUserByName(users[u])
    groupManager.addUserToGroup(user,grouptwo)
}
printtext+="<h2>Users from "
for(int g=0;g<groupone.size();g++)
{
    printtext+="<b> ${groupone[g].getName()}</b>,"
}
printtext=printtext[0..printtext.size()-2]
printtext+=" have been added to <b>${grouptwo.getName()}</b>!</h2><br><br>"
 
for(int u=0;u<users.size();u++)
{
    printtext+="${u+1}. ${users[u]}<br>"
}
 
return printtext
