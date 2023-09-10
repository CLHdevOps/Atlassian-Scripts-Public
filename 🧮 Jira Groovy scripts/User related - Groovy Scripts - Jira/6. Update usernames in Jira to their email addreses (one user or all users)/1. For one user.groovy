//Rinaldi Michael
//Modified in 18th Nov 2022 09:47 pm
//code from https://community.atlassian.com/t5/Marketplace-Apps-Integrations/Groovy-Update-an-application-User/qaq-p/785531
 
import com.atlassian.jira.event.user.UserRenamedEvent
import com.atlassian.jira.user.ApplicationUser
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
 
import com.atlassian.jira.event.user.UserProfileUpdatedEvent
 
import com.atlassian.jira.user.*
import com.atlassian.jira.bc.user.ApplicationUserBuilder;
     
//variable declaration for adding groups and projects
def userManager = ComponentAccessor.userManager
def groupManager = ComponentAccessor.groupManager
     
//variable delcaration for adding groups to the user
//def userService = ComponentAccessor.getComponent(UserService)
def userUtil = ComponentAccessor.userUtil
     
//variable declaration for adding projects
def projectRoleManager = ComponentAccessor.getComponent(ProjectRoleManager)
def projectRoleService = ComponentAccessor.getComponent(ProjectRoleService)
     
//user picker to verify if the user already exists
@UserPicker(description = 'User', label = 'User', multiple = false)
ApplicationUser applicationUser
 
def userName = applicationUser.username
def emailAddress = applicationUser.emailAddress
 
def loggedInUser = ComponentAccessor.jiraAuthenticationContext.loggedInUser
 
UserService userService = ComponentAccessor.getComponent(UserService.class);
ApplicationUserBuilder applicationUserBuilder = userService.newUserBuilder(applicationUser);
applicationUserBuilder.name(applicationUser.emailAddress);
ApplicationUser userForValidation = applicationUserBuilder.build();
 
UserService.UpdateUserValidationResult updateUserValidationResult = userService.validateUpdateUser(userForValidation);
if (updateUserValidationResult.isValid()) {
 userService.updateUser(updateUserValidationResult);
}
