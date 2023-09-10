///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////Rinaldi Michael///////////////////
/////////Created in 5th April 2023, 06:49 pm///////
/////Modified in                        ///////////
//References:                           ///////////
//https://docs.atlassian.com/software/jira/docs/api/7.1.9/com/atlassian/jira/application/DefaultApplicationRoleManager.html
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
    
 
def userManager = ComponentAccessor.userManager
def groupManager = ComponentAccessor.groupManager
def loginManager = ComponentAccessor.getComponent(LoginManager)
def defaultApplicationRoleManager = ComponentAccessor.getComponent(DefaultApplicationRoleManager)
             
String printtext="<b>Jira groups that count towards the license are:</b><br><br>"
 
def allLicensedGroupRoles = defaultApplicationRoleManager.getRoles()[0].groups
for(int l=0;l<allLicensedGroupRoles.size();l++)
{
    printtext+="${l+1}. ${allLicensedGroupRoles[l].getName()}<br>"
}
    
return printtext
