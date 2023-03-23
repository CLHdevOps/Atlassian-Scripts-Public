/////////////////Rinaldi Michael///////////////////
/////////Created in 27th Feb 2023, 06:38 pm////////
/////Modified in 17th Mar 2023, 12:10 pm///////////
///////////////////////////////////////////////////
//Functionalities
//1. Lists out every group.
//2. Users are listed under the group name.
//3. Output to Script Editor.
//4. Copy the result into Google sheet and split text to columns with *


       
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
             
def allGroupsInJira = groupManager.getAllGroups()
String printtext="<br>"
printtext="Group Name/Display Name*Username*Email Address\n\n"
   
@ShortTextInput(description = 'Enter any file name. This will create a new file in Script Editor.', label = 'Enter the FileName')
String fileName
 
   
for(int g=0;g<allGroupsInJira.size();g++)
{
    def allUsersInGroup = groupManager.getUsersInGroup(allGroupsInJira[g])
    printtext+="\n////////////////////////////\n"
    printtext+="\n${allGroupsInJira[g].getName()}\n"
    for(int i=0;i<allUsersInGroup.size();)
    {
        printtext=printtext+"${allUsersInGroup[i].getDisplayName()}*${allUsersInGroup[i].getUsername()}*${allUsersInGroup[i].getEmailAddress()}\n"
        i++
    }
}
 
 
new File("/home/jira/shared_home/scripts/${fileName}.groovy").withWriter('utf-8')
{
    writer -> writer.writeLine printtext
}
    
return "The contents are written into <b>${fileName}.groovy</b> in the Script Editor."
