/////////////////Rinaldi Michael///////////////////
/////////Created in 27th Feb 2023, 06:38 pm////////
/////Modified in 8th Mar 2023, 04:27 pm////////////
///////////////////////////////////////////////////
//Results in Script Editor:
//The results will be written into a new file that will be created by the script in ScriptRunner's Script Editor.
//The delimiter set is *
//These results can be copied using CTRL+A into a Google sheet and Split into columns (in-built tool) with *
//1. Details that will be returned:
//Display Name, Username, Email Address, Is active, Groups
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
   
       
            
//variable declaration for adding groups and projects
def userManager = ComponentAccessor.userManager
def groupManager = ComponentAccessor.groupManager
            
//variable delcaration for adding groups to the user
def userService = ComponentAccessor.getComponent(UserService)
def userUtil = ComponentAccessor.userUtil
 
@ShortTextInput(description = 'Enter any file name. This will create a new file in Script Editor.', label = 'Enter the FileName')
String fileName
 
@GroupPicker(label = 'Select the group/s', description = 'Select the group/s you want the user list of', placeholder = 'Pick groups', multiple = true)
List<Group> groups           
 
 
String printtext="<br>"
printtext="Display Name*Username*Email Address*Active*Groups\n\n"
 
   
//return "${allUsers[0].getDisplayName()}*${allUsers[0].getEmailAddress()}*${userManager.getUserByKey(allUsers[0].getKey()).isActive()}*${groupManager.getGroupNamesForUser(allUsers[0].getUsername())}"
 
for(int g=0;g<groups.size();g++)
{
    def allUsersInGroup = groupManager.getUsersInGroup(groups[g])
    for(int i=0;i<allUsersInGroup.size();)
    {
    printtext=printtext+"${allUsersInGroup[i].getDisplayName()}*${allUsersInGroup[i].getUsername()}*${allUsersInGroup[i].getEmailAddress()}*${userManager.getUserByKey(allUsersInGroup[i].getKey()).isActive()}*${groupManager.getGroupNamesForUser(allUsersInGroup[i].getUsername())}\n"
    i++
    }
}
   
new File("/home/jira/shared_home/scripts/${fileName}.groovy").withWriter('utf-8')
{
    writer -> writer.writeLine printtext
}
    
return "The contents are written into <b>${fileName}.groovy</b> in the Script Editor."
