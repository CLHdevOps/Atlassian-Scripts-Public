//Rinaldi Michael
//Modified in 27th January 2023, 08:59 am
   
import java.lang.String
import java.io.*
import com.atlassian.crowd.model.group.Groups
import com.atlassian.crowd.embedded.api.Group
import com.atlassian.jira.bc.user.UserService
import com.atlassian.jira.component.ComponentAccessor
import com.onresolve.scriptrunner.parameters.annotation.*
import com.atlassian.jira.bc.user.UserService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.project.Project
import com.atlassian.jira.security.roles.ProjectRoleManager
import com.atlassian.jira.bc.projectroles.ProjectRoleService
import java.lang.String
import com.atlassian.jira.project.Project
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.security.roles.ProjectRoleManager
import com.atlassian.jira.bc.projectroles.ProjectRoleService
import com.onresolve.scriptrunner.parameters.annotation.*
import com.atlassian.jira.user.*
   
        
//variable declaration for adding groups and projects
def userManager = ComponentAccessor.userManager
def groupManager = ComponentAccessor.groupManager
        
//variable delcaration for adding groups to the user
def userService = ComponentAccessor.getComponent(UserService)
def userUtil = ComponentAccessor.userUtil
        
   
//User group input
@GroupPicker(description = 'Select the group/s from the dropdown.', label = 'Enter the User Groups', multiple = true, placeholder = 'Select group')
List<Group> groupone
   
@ShortTextInput(description = 'Enter a domain like gmail.com,yahoo.com,bing.com...etc. This will include users not added to the group but has the domain and is active. Number of groups input here should be the same as the number of groups set in the first field.', label = 'Enter the domain (Required*)')
String domainInput
String[] domains
if(domainInput!=null)
    domains = domainInput.split(",")
    
def loggedInUser = ComponentAccessor.jiraAuthenticationContext.loggedInUser
    
def usersFromGroup = userUtil.getAllUsersInGroups(groupone)
def allUsers = userManager.getAllApplicationUsers()
   
String printtext
String[] groupName = new String[groupone.size()]
   
for(int g=0;g<groupone.size();)
{
    groupName[g] = groupone[g].getName()
    g++
}
printtext="<h1><b>Active users from the group/s -> ${groupName}</h1></b><br><br>"
   
int userCount = 1
   
//Users in specified group
int inGroup=0
for(int i=0;i<usersFromGroup.size();)
{
    if(userManager.getUserByKey(usersFromGroup[i].getKey()).isActive()==true)
    {
        printtext=printtext+"${userCount}. "+usersFromGroup[i].getDisplayName()+"*"+usersFromGroup[i].getEmailAddress()+"<br>"
        userCount++
        inGroup++
    }
    i++
}
  
  
//Users not added to the group but has the domain and is active
int notInGroup=0
printtext+="<h1><b>Active users from domain/s who are not part of the group despite having the domain-> ${domainInput}</h1></b><br><br>"
for(int j=0;j<allUsers.size();)
{
        String[] domain = allUsers[j].getEmailAddress().split('@')
        for(int d=0;d<domains.size();)
        {
            try
            {
            if (domain[1]==domains[d] && userManager.getUserByName(allUsers[j].getName()).isActive()==true && groupManager.isUserInGroup(userManager.getUserByName(allUsers[j].getName()),groupone[d])==false)
            {
            printtext=printtext+"${userCount}. "+allUsers[j].getDisplayName()+"*"+allUsers[j].getEmailAddress()+"<br>"
            userCount++
            notInGroup++
            }
            }
            catch(Exception ex)
            {
                //ignore
            }
        d++
        }
j++
}//end of last for loop
  
   
   
return "Number of users - ${userCount-1}. <br>${inGroup} users are assigned to the group. ${notInGroup} users are not in the group."+printtext
