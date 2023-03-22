/////////////////////////////////////////////////
//////////Author: Rinaldi Michael////////////////
////Last Modified: 16/01/2023, 02:18 pm//////////
/////////////////////////////////////////////////
//Functionalities
//1. Delete one or multiple groups in Jira
//2. Code will ignore inputs if group does not exist.
/////////////////////////////////////////////////
 
import java.lang.String
import java.io.*
import com.atlassian.crowd.embedded.api.Group
import com.atlassian.jira.component.ComponentAccessor
import com.onresolve.scriptrunner.parameters.annotation.*
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.crowd.embedded.core.util.StaticCrowdServiceFactory
 
 
//Select the group
@GroupPicker(description = 'Select the group/s', label = 'Select the group/s', multiple = true, placeholder = 'Select group/s')
List<Group> group
 
//Printing workaround
String printtext
 
//variable declaration for adding groups and projects
def userManager = ComponentAccessor.userManager
def groupManager = ComponentAccessor.groupManager
def crowdService = StaticCrowdServiceFactory.crowdService
 
//remove the group/s
for(int g=0;g<group.size();g++)
{
  def removeGroupStatus
  try //Try to delete the group. A try block is used in case the group does not exist
  {
    removeGroupStatus = crowdService.removeGroup(group[g])
  }
  catch(Exception ex)
  {
    //Print out that the group does not exist
    printtext+="<br>Group does not exist!"
    continue
  }
   
  //confirm the group's deletion
  if(removeGroupStatus)
    printtext+="<br>Group - <b>${group[g].getName()}</b> has been removed successfully!"
}
 
return printtext //print out the results
