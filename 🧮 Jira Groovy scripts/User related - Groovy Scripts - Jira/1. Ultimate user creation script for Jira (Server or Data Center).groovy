////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Created - 6th Oct 2022 09:30 am
//Last Modified - 9th May 2023, 11:00 pm
//Authors - Rinaldi Michael, Sumit Pal, Adaptavist
//References -
//https://library.adaptavist.com/
/*
Functionalities
1. Create users in Jira using Dynamic forms.

2. Provide multiple email addresses, project roles and names in a comma separated list. 

3. Change the separation character from a comma to any other character of choice by modifying (',') to ('\') for example in the below 3 lines accordingly:

            emailAddresses=emailAddressinput.split(',')

            displayNames=displayNameinput.split(',')

            roleNames=roleNameInput.split(',') 

4. Select multiple groups 

5. Select multiple projects

6. Auto fill Full Name by entering N/A. Details in it's usage is mentioned in the forms description

7. Reactivate an account if it already exists and is deactivated.

8. Fail safe to add existing accounts to groups or projects chosen.

9. User picker to help verify account's existence.

10. Prints out details of actions performed.

11. Catch errors in code.

12. Provide password or create random password

13. Convert email address/es input to lower case
*/
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 
 
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
import com.atlassian.jira.security.roles.ProjectRole
     
              
//variable declaration for adding groups and projects
def userManager = ComponentAccessor.userManager
def groupManager = ComponentAccessor.groupManager
              
//variable delcaration for adding groups to the user
def userService = ComponentAccessor.getComponent(UserService)
def userUtil = ComponentAccessor.userUtil
              
//variable declaration for adding projects
def projectRoleManager = ComponentAccessor.getComponent(ProjectRoleManager)
def projectRoleService = ComponentAccessor.getComponent(ProjectRoleService)
              
//user picker to verify if the user already exists
@UserPicker(description = 'Field not Mandatory. This cell does not play a role in this script. It is only used to verify if the account already exists', label = 'User Checker. Enter the username or Full Name', multiple = true)
def user
           
//variable that will store text to return in the result tab
String printtext=""
           
try
{
// The email address for the new user - required
@ShortTextInput(description = "Enter a comma seperated string of email addresses for multiple users. Existing users's will not be overwritten", label = "Enter the user's email ID (Required🌟)")
String emailAddressinput
              
String[] emailAddresses
emailAddresses=emailAddressinput.toLowerCase().split(',')
             
for(int e=0;e<emailAddresses.size();)
{
    emailAddresses[e]= emailAddresses[e].trim() //trim emailAddressinput for any whitespaces
    log.info("emailAddressinput: ${emailAddresses[e]}")
    e++
}
              
//Enter the users Full Names
@ShortTextInput(description = "Enter Full name as N/A if not known. Enter a comma seperated string of names for multiple users. Will accept combination of names and N/A like Rinaldi Michael,Mohit Mohapatra,N/A. If only one N/A is input in this field for mutliple user creation, the name present in the email address will be implemented for all users", label = "Enter the user's Full Name (Required🌟)")
String displayNameinput
              
String[] displayNames
displayNames=displayNameinput.split(',')
              
if(displayNames.size()==1 && displayNames[0]=='N/A')
{
    for(int i=1;i<emailAddresses.size();)
    {
        displayNameinput=displayNameinput.concat(",N/A")
        i++
    }
}
displayNames=displayNameinput.split(',')
              
//User group input
@GroupPicker(description = 'Select the group/s from the dropdown. jira-software-users is added by default', label = 'Enter the User Groups (Required🌟)', multiple = true, placeholder = 'Select group')
List<Group> groupone
          
//Enter password
@ShortTextInput(description = 'Enter a password or type in N/A for random password generation. For multiple users enter a comma separated list of passwords. Combinations of passwords and N/A in the comma separated list are also allowed similar to the Display Name input. Entering N/A once will generate random passwords for all accounts.', label = 'Enter Password (TEXT VISIBLE)(Required🌟)')
String passwordInput
      
String[] passwords
passwords=passwordInput.split(',')
              
if(passwords.size()==1 && passwords[0]=='N/A')
{
    for(int i=1;i<emailAddresses.size();)
    {
        passwordInput=passwordInput.concat(",N/A")
        i++
    }
}
passwords=passwordInput.split(',')
      
              
//Specify project and role
@ProjectPicker(description = 'Select the project(s)', label = 'Project(s)', includeArchived = false, multiple = true, placeholder = 'Select project(s)')
List<Project> projects
String[] projectString = new String[projects.size()]
              
if(projects!=[])
{
    for(int p=0;p<projects.size();)
    {
        projectString[p] = projects[p].toString()
        p++
    }
}
     
     
@ProjectRolePicker(label = 'View Project roles', description = 'This field does not play any role in the script. It is placed only to view the roles available in the jira instance.', multiple = true)
List<ProjectRole> projectRoles
     
              
@ShortTextInput(description = 'Enter one of the/many of the listed project roles - Admin, Manager, User, Viewer (comma list for mulitple users). Entering only one role will add the role to all users which are input', label = 'Project Role')
String roleNameInput
              
String[] roleNames
            
             
if(projects!=[])
{
roleNames=roleNameInput.split(',')
            
//make all project roles the same if only one input
    if(roleNames.size()==1)
    {
        for(int i=1;i<emailAddresses.size();)
        {
        roleNameInput=roleNameInput.concat(",${roleNameInput}")
        i++
        }
    roleNames=roleNameInput.split(',')
    }
             
    roleNames=roleNameInput.split(',')
    //trim emailAddressinput for any whitespaces
    for(int r=0;r<roleNames.size();)
    {
        roleNames[r]= roleNames[r].trim()
        log.info("Role Names input: ${roleNames[r]}")
        r++
    }
}
             
             
//Specify user/group role actor (ex./ UserRoleActor.TYPE, GroupRoleActor.TYPE)
//value found using the return statement - return com.atlassian.jira.security.roles.ProjectRoleActor.GROUP_ROLE_ACTOR_TYPE or USER_ROLE_ACTOR_TYPE
final def actorType = 'atlassian-user-role-actor'
              
//additional variables for the following loop
int i,j
def userName
           
              
def emailAddressparameter
def displayNameparameter
              
for(i=0;i<emailAddresses.size();)
{
    printtext=printtext+"<br><h1><b>**************************User ${i+1}**************************</h1></b><br>"
    // The username of the new user - needs to be lowercase and unique - required
    // same as email address. Assigning variables to the each user detail
    userName = emailAddresses[i].trim()
    emailAddressparameter = emailAddresses[i].trim()
    displayNameparameter=displayNames[i]
              
              
        def localPart
                  
        def name
              
              
        //activate the user if exists
        if (userManager.getUserByName(userName))
        {
        def updatedUser = userService.newUserBuilder(userManager.getUserByName(userName)).active(true).build()
        def updateUserValidationResult = userService.validateUpdateUser(updatedUser)
                      
                      
            //if the above does not return an error. Add user/s to the new group and display the below message and log
            if (updateUserValidationResult.valid)
            {
              
               //add text to the printtext variable so all messages can be displayed
               printtext=printtext.concat(" User - <b>${userManager.getUserByName(userName).displayName}</b> (").concat(emailAddresses[i]).concat(") already exists. <br>")
              
               log.warn "User ${userName} already exists"
               userService.updateUser(updateUserValidationResult)
              
                    printtext=printtext.concat("User - <b>${userManager.getUserByName(userName).displayName}</b> (").concat(emailAddresses[i]).concat(") has been added to/is probably already in the group/s - ")
                    for(int g=0;g<groupone.size();)
                    {
                        Group group1 = groupone[g]
                        assert group1 : "Could not find group with name $groupone"
              
                        def userToAddone = userManager.getUserByName(userName)
                        userUtil.addUserToGroups(groupone, userToAddone)
                        printtext=printtext.concat("<b>${group1.getName()}</b>").concat(", ")
                        g++
                    }
                    printtext=printtext[0..printtext.size()-3] //remove the last comma returned
                    printtext=printtext+"<br>"
                              
                              
                if(projects!=[])
                {
                    printtext=printtext.concat(" User - <b>${userManager.getUserByName(userName).displayName}</b> (").concat(emailAddresses[i]).concat(") has been added to the role - <b>${roleNames[i]}</b> for the project/s- ")
                    for(int p=0;p<projects.size();)
                    {
                        def projectRole
                        def userNamesforProject
                        //add user to the project and the role specified
                        if(roleNames.size()==1)
                        {
                            projectRole = projectRoleManager.getProjectRole(roleNames[0])
                            userNamesforProject = [userManager.getUserByName(userName).key.toString()]
                        }
                        else
                        {
                        projectRole = projectRoleManager.getProjectRole(roleNames[i])
                        userNamesforProject = [userManager.getUserByName(userName).key.toString()]
                        }
                        String errorCaught
                        try
                        {
                            projectRoleService.addActorsToProjectRole(userNamesforProject, projectRole, projects[p], actorType, null)
                        }
                        catch(Exception ex)
                        {
                            printtext=printtext.concat("<br><b>Exception</b><br>User ").concat(emailAddresses[i]).concat(" is probably already assigned to the role - <b>${roleNames[i]}</b> and project <b> ${projects[p].getName()}, Key - ${projectString[p]}</b>")
                            errorCaught="yes"
                        }
                        if(errorCaught!="yes")
                            printtext=printtext+"<b> ${projects[p].getName()}, Key - ${projectString[p]}</b>,"
                            printtext=printtext[0..printtext.size()-1] //remove the last comma returned
                        p++
                    }
                    printtext=printtext+"<br>"
                }
                              
                i=i+1
                continue
            }
            //log the error
            else
            {
              log.warn "Update of ${userManager.getUserByName(userName).name} failed: ${updateUserValidationResult.errorCollection.errors.entrySet().join(',')}"
                break
            }
        }
        //end of user activation snippet
              
                      
        //convert username to Full Name
        if(displayNames[i]!='N/A')
        {
            displayNames[i]=displayNames[i]
        }
        else
        {
        j=0
        displayNames[j]=emailAddresses[i]
              
        // Assign the bit before the @-sign and remove everything past @
        localPart = emailAddresses[i].replaceAll("@.+\$", "")
              
        // Split into two parts if contains '.'
        name =  localPart.split('\\.')
              
        // Extract first name and last name from email address and use them to make up display name
            if (name.size() == 1)
                displayNames[j] = name[0].capitalize()
            else if (name.size() == 2)
                displayNames[j] = name[0].capitalize() + " " + name[1].capitalize()
            else if (name.size() == 3)
                displayNames[j] = name[0].capitalize() + " " + name[1].capitalize() + " " + name[2].capitalize()
            else
                displayNames[j] = localPart
              
            displayNameparameter=displayNames[j]
        }
              
    // Notifications are sent by default, set to false to not send a notification
    final boolean sendNotification = false
              
    //Create the user with the recorded details
    def loggedInUser = ComponentAccessor.jiraAuthenticationContext.loggedInUser
              
            if(passwords[i]=="N/A")
            {
            //Random Password generator
            Random rnd = new Random()
            int randomNumber = rnd.nextInt(100)
            if(randomNumber==0 || randomNumber<6)
                randomNumber+=6
            passwords[i]=""
            String[] passwordCharacters=["a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"
                      ,"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"
                      ,"!","@","%","^","&","*","(",")","-","+","=",".","/",">","<",";",":","|"]
           
                for(int p=0;p<randomNumber;)
                {
                    try
                    {
                    passwords[i] = passwords[i]+passwordCharacters[rnd.nextInt(passwordCharacters.size())]
                    p++
                    }
                    catch(Exception ex)
                    {
                    p++
                    }
                }
            }//end of random password generator snippet
         
         
    def newCreateRequest = UserService.CreateUserRequest.withUserDetails(loggedInUser, userName, passwords[i], emailAddressparameter, displayNameparameter)
        .sendNotification(sendNotification)
              
    //Validate the result
    def createValidationResult = userService.validateCreateUser(newCreateRequest)
    assert createValidationResult.valid : createValidationResult.errorCollection
              
    userService.createUser(createValidationResult)
              
    //Add every username to the return message
    printtext=printtext.concat("<br>").concat("<b>${displayNameparameter}</b>").concat(" (${emailAddresses[i]})").concat(" has been created. <br>")
              
    printtext=printtext+"<br>The password is - <b>${passwords[i]}</b>  </b><br>Request the user to reset the password once logged in. Ignore if SSO is enabled.<br>"
         
              
                  
    //add the groups to the newly created user
    if(groupone!=null)
    {
        printtext=printtext.concat("<br>").concat("<b>${displayNameparameter}</b>").concat(" (${emailAddresses[i]})").concat(" has been added to the group/s - ")
        //add every group input
        for(int g=0;g<groupone.size();)
        {
            Group group1 = groupone[g]
            assert group1 : "Could not find group with name $groupone"
              
            def userToAddone = userManager.getUserByName(userName)
            userUtil.addUserToGroups(groupone, userToAddone)
            printtext=printtext.concat("<b>${group1.getName()}</b>").concat(", ")
            g++
        }
        printtext=printtext[0..printtext.size()-3] //remove the last comma returned
        printtext=printtext+"<br>"
    }
              
        if(projects!=[])
        {
            printtext=printtext.concat(" User ").concat("<b>${displayNameparameter}</b>").concat(" (${emailAddresses[i]})").concat(" has been added to the role - <b>${roleNames[i]}</b> for the project/s- ")
            for(int p=0;p<projects.size();)
            {
                def projectRole
                def userNamesforProject
                //add user to the project and the role specified
                if(roleNames.size()==1)
                {
                    projectRole = projectRoleManager.getProjectRole(roleNames[0])
                    userNamesforProject = [userManager.getUserByName(userName).key.toString()]
                }
                else
                {
                    projectRole = projectRoleManager.getProjectRole(roleNames[i])
                    userNamesforProject = [userManager.getUserByName(userName).key.toString()]
                }
                String errorCaught
                try
                {
                    projectRoleService.addActorsToProjectRole(userNamesforProject, projectRole, projects[p], actorType, null)
                }
                catch(Exception ex)
                {
                    printtext=printtext.concat(" User ").concat("<b>${displayNameparameter}</b>").concat(" (${emailAddresses[i]})").concat(" is probably already assigned to the role and project <b> ${projects[p].getName()}, Key - ${projectString[p]},</b>")
                    printtext=printtext[0..printtext.size()-2] //remove the last comma returned
                    errorCaught="yes"
                }
                if(errorCaught!="yes")
                    printtext=printtext+"<b> ${projects[p].getName()}, Key - ${projectString[p]}</b>,"
                    printtext=printtext[0..printtext.size()-2] //remove the last comma returned
                p++
            }
            printtext=printtext+"<br>"
        }
              
                  
                  
    i=i+1
}
}//End of Try block
           
catch(Exception ex)
{    
    printtext=printtext+"<h1><b><center>Oops...Something went wrong!</h1></b></center>"
    printtext+="<br>${ex}"
}
              
return printtext
