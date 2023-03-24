/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Author: Rinaldi Michael
//Created: 19th Mar 2023, 04:40 pm
//Last Modified: 20th Mar 2023, 01:02 pm
//Reference:
//https://community.atlassian.com/t5/Jira-questions/I-am-trying-to-update-Component-Lead-using-Groovy-but-its-not/qaq-p/1996485#U2303978
//https://docs.adaptavist.com/sr4js/latest/best-practices/write-code/dynamic-forms#:~:text=Use%20the%20Dynamic%20Forms%20feature,used%20for%20various%20use%20cases.
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 
 
import com.atlassian.jira.bc.project.component.ProjectComponent
import com.onresolve.scriptrunner.parameters.annotation.ComponentPicker
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.component.ComponentAccessor
import com.resolve.scriptrunner.*
import java.lang.String
import java.io.*
import com.atlassian.jira.component.ComponentAccessor
import com.onresolve.scriptrunner.parameters.annotation.*
import com.onresolve.scriptrunner.parameters.annotation.meta.*
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.project.Project
import com.atlassian.jira.component.ComponentAccessor
import com.resolve.scriptrunner.*
import com.onresolve.scriptrunner.parameters.annotation.*
import com.onresolve.scriptrunner.parameters.annotation.meta.*
import com.atlassian.jira.bc.project.component.MutableProjectComponent
import com.atlassian.jira.bc.EntityNotFoundException
 
  
 
def userManager = ComponentAccessor.userManager
def projectComponentManager = ComponentAccessor.projectComponentManager
 
@ComponentPicker(
    label = 'Components', description = 'Pick components',
    projectPlaceholder = 'Pick a project', placeholder = 'Pick components', multiple = true
)
List<ProjectComponent> components
 
@UserPicker(label = 'New project component lead', description = 'Enter a project lead to set for this project component')
ApplicationUser user
def username = user.getUsername()
 
@ShortTextInput(label = "User's Directory ID", description = "Enter the Directory that the user is from. <br>Internal: 1")
String userDir
 
String printtext=""
for(int c=0;c<components.size();c++)
{
  def tempuser = userManager.findUserInDirectory(username,userDir.toLong())
  MutableProjectComponent newProjectComponent = MutableProjectComponent.copy(components[c])
  newProjectComponent.setLead(tempuser.getKey())
  
      try
    {
      projectComponentManager.update(newProjectComponent);
      printtext+="<br>Component- <b>${components[c]}'s</b> lead is set to <b>${tempuser.getName()},(${tempuser.getEmailAddress()})</b>"
    }
    catch (EntityNotFoundException e)
    {
      return "did not work"
    }
}
 
return printtext
