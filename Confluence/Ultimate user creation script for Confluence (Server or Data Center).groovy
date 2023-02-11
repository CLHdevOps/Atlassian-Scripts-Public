/*
Created - 19th Oct 2022 11:15 am 
Last Modified - 20th Jan 2023 08:30 am
Authors - Rinaldi Michael, Sumit Pal, Adaptavist, Kenneth Mcclean
References -
https://community.atlassian.com/t5/Confluence-questions/Confluence-Scriptrunner-Copy-users-from-space-permissions-to-the/qaq-p/1597679
https://www.kennethmcclean.com/blog/confluence-space-permissions-management-with-groovy-and-scriptrunner/
https://community.atlassian.com/t5/Confluence-questions/How-to-fetch-list-of-space-permissions-using-groovy-in/qaq-p/1759107
*/

/*
Functionalities
1. Create users in Confluence using Dynamic forms.

2. Provide multiple email addresses and names in a comma separated list. 

3. Change the separation character from a comma to any other character of choice by modifying (',') to ('\') for example in the below 3 lines accordingly:

            emailAddresses=emailAddressinput.split(',')

            displayNames=displayNameinput.split(',')

            roleNames=roleNameInput.split(',') 

4. Select multiple groups 

5. Select multiple spaces

6. Auto fill Full Name by entering N/A. Details in it's usage is mentioned in the forms description

7. Reactivate an account if it already exists and is deactivated.

8. Fail safe to add existing accounts to groups or spaces chosen.

9. User picker to help verify account's existence.

10. Prints out details of actions performed.

11. Specify Space permissions required.

12. Catch errors in code

13. Create random passwords from length 6 to 100

14. Convert email address/es input to lower case
*/
           
import com.onresolve.scriptrunner.parameters.annotation.GroupPicker
import com.atlassian.crowd.embedded.api.Group
import com.onresolve.scriptrunner.parameters.annotation.*
import com.onresolve.scriptrunner.parameters.annotation.meta.*
import java.lang.String
import com.atlassian.confluence.security.login.LoginManager
import com.atlassian.confluence.user.ConfluenceUserImpl
import com.atlassian.confluence.user.UserAccessor
import com.atlassian.sal.api.component.ComponentLocator
import com.atlassian.user.GroupManager
import com.atlassian.confluence.security.PermissionManager
import com.atlassian.confluence.security.login.LoginManager
import com.atlassian.confluence.user.UserAccessor
import com.atlassian.confluence.user.*
import com.atlassian.sal.api.component.ComponentLocator
import com.atlassian.user.UserManager
import com.atlassian.user.security.password.Credential
import com.atlassian.confluence.spaces.Space
import com.atlassian.confluence.rpc.soap.services.SpacesSoapService
import java.io.*
import groovy.lang.*
           
           
def userAccessor = ComponentLocator.getComponent(UserAccessor)
def groupManager = ComponentLocator.getComponent(GroupManager)
def loginManager = ComponentLocator.getComponent(LoginManager)
def userManager = ComponentLocator.getComponent(UserManager)
def permissionManager = ComponentLocator.getComponent(PermissionManager)
           
//User Checker
@UserPicker(description = 'User Checker. This does not perform any operation. It is only to search for any existing users.', label = 'User Checker', multiple = true)
def userchecker
           
String printtext
        
//start of try block
try
{
//Email Address input
@ShortTextInput(description = 'Enter an email address/multiple email addresses in a comma separated list', label = 'Email Address/es (Required🌟)')
String emailAddressinput
String[] email = emailAddressinput.toLowerCase().split(',')
for(int e=0;e<email.size();)
{
    email[e]= email[e].trim() //trim emailAddressinput for any whitespaces
    log.info("emailAddressinput: ${email[e]}")
    e++
}
          
//Full Name input
@ShortTextInput(description = 'Enter Full name as N/A if not known. Enter a comma seperated string of names for multiple users. Will accept combination of names and N/A like Rinaldi Michael,Mohit Mohapatra,N/A. If only one N/A is input in this field for mutliple user creation, the name present in the email address will be implemented for all users', label = 'Full name (Required🌟)')
String displayNameinput
           
String[] displayNames
displayNames=displayNameinput.split(',')
           
if(displayNames.size()==1 && displayNames[0]=='N/A')
{
    for(int i=1;i<email.size();)
    {
        displayNameinput=displayNameinput.concat(",N/A")
        i++
    }
}
displayNames=displayNameinput.split(',')
           
           
//Group Picker
@GroupPicker(description = 'Select the group/s the user needs to be a part of. confluence-users is added by default', label = 'Group/s (Required🌟)', multiple = true, placeholder = 'Select groups')
List<Group> GroupToUse
      
//Enter password
@ShortTextInput(description = 'Enter a password or type in N/A for random password generation. For multiple users enter a comma separated list of passwords. Combinations of passwords and N/A in the comma separated list are also allowed similar to the Display Name input.', label = 'Enter Password (TEXT VISIBLE)(Required🌟)')
String passwordInput
      
String[] passwords
passwords=passwordInput.split(',')
              
if(passwords.size()==1 && passwords[0]=='N/A')
{
    for(int i=1;i<email.size();)
    {
        passwordInput=passwordInput.concat(",N/A")
        i++
    }
}
passwords=passwordInput.split(',')
     
           
//Space Picker
@SpacePicker(description = 'Select the space/s the user needs to be a part of', label = 'Space', multiple = true)
List<Space> spaces
String[] spaceKey = new String[spaces.size()]
           
if(spaces!=[])
{
    for(int s=0;s<spaces.size();)
    {
        spaceKey[s] = spaces[s].getKey()
        s++
    }
}
           
           
/*
//Space Permissions input
@ShortTextInput(description = 'Default Permissions (copy paste this if default) - EDITSPACE,VIEWSPACE,REMOVEOWNCONTENT,CREATEATTACHMENT,COMMENT <br><br>Input values that can be used in a comma separated list - EDITSPACE,VIEWSPACE,SETPAGEPERMISSIONS,EXPORTSPACE,SETSPACEPERMISSIONS,COMMENT,\nREMOVECOMMENT,EDITBLOG,REMOVEBLOG,REMOVEPAGE,REMOVEOWNCONTENT,REMOVEATTACHMENT,REMOVEMAIL<br><br><br>VIEWSPACE - View pages in the Space<br>REMOVEOWNCONTENT - Delete users own content in the Space<br><br>EDITSPACE - Add and/or Edit pages in the Space<br>REMOVEPAGE - Delete pages in the Space <br><br>EDITBLOG - Add and/or Edit blogs in the Space<br>REMOVEBLOG - Delete Blogs in the Space<br><br>CREATEATTACHMENT - Add attachments to pages in the Space<br>REMOVEATTACHMENT - Delete attachments from pages in the Space<br><br>COMMENT - Add comments to pages in the Space<br>REMOVECOMMENT - Remove comments from pages in the Space<br><br>SETPAGEPERMISSIONS - Add or delete restrictions in pages in the Space<br><br>REMOVEMAIL - Mail is a legacy feature in Confluence Cloud that is in the process of being removed. Changes to Mail permissions won’t have any effect on product functionality or access.<br><br>EXPORTSPACE - Have permission to export the whole Space in Space Tools<br>SETSPACEPERMISSIONS - Have Admin permissions to the whole Space', label = 'Permissions')
String permissions
*/
     
@Select(
    label = "Space Permissions (Multi Picker)",
    description = "<br>Default Permissions - EDITSPACE,VIEWSPACE,REMOVEOWNCONTENT,CREATEATTACHMENT,COMMENT <br><br>VIEWSPACE - View pages in the Space<br>REMOVEOWNCONTENT - Delete users own content in the Space<br><br>EDITSPACE - Add and/or Edit pages in the Space<br>REMOVEPAGE - Delete pages in the Space <br><br>EDITBLOG - Add and/or Edit blogs in the Space<br>REMOVEBLOG - Delete Blogs in the Space<br><br>CREATEATTACHMENT - Add attachments to pages in the Space<br>REMOVEATTACHMENT - Delete attachments from pages in the Space<br><br>COMMENT - Add comments to pages in the Space<br>REMOVECOMMENT - Remove comments from pages in the Space<br><br>SETPAGEPERMISSIONS - Add or delete restrictions in pages in the Space<br><br>REMOVEMAIL - Mail is a legacy feature in Confluence Cloud that is in the process of being removed. Changes to Mail permissions won’t have any effect on product functionality or access.<br><br>EXPORTSPACE - Have permission to export the whole Space in Space Tools<br>SETSPACEPERMISSIONS - Have Admin permissions to the whole Space",
    options = [
        @Option(label = "Default Permissions", value = "default"),
        @Option(label = "View pages in the Space", value = "VIEWSPACE"),
        @Option(label = "Delete users own content in the Space", value = "REMOVEOWNCONTENT"),
        @Option(label = "Add and/or Edit pages in the Space", value ="EDITSPACE"),
        @Option(label = "Delete pages in the Space", value ="REMOVEPAGE"),
        @Option(label = "Add and/or Edit blogs in the Space", value ="EDITBLOG"),
        @Option(label = "Delete Blogs in the Space", value ="REMOVEBLOG"),
        @Option(label = "Add attachments to pages in the Space", value ="CREATEATTACHMENT"),
        @Option(label = "Delete attachments from pages in the Space", value ="REMOVEATTACHMENT"),
        @Option(label = "Add comments to pages in the Space", value ="COMMENT"),
        @Option(label = "Remove comments from pages in the Space", value ="REMOVECOMMENT"),
        @Option(label = "Add or delete restrictions in pages in the Space", value ="SETPAGEPERMISSIONS"),
        @Option(label = "Mail is a legacy feature in Confluence Cloud that is in the process of being removed. Changes to Mail permissions won’t have any effect on product functionality or access.", value ="REMOVEMAIL"),
        @Option(label = "Have permission to export the whole Space in Space Tools", value ="EXPORTSPACE"),
        @Option(label = "Have Admin permissions to the whole Space", value ="SETSPACEPERMISSIONS")
    ],
    multiple = true
)
List<String> permissionsInput
     
     
if(permissionsInput!=null)
{
    for(int p=0;p<permissionsInput.size();)
    {
        if(permissionsInput[p]=="default")
        {
            permissionsInput.remove(p)
            permissionsInput.addAll(["EDITSPACE","VIEWSPACE","REMOVEOWNCONTENT","CREATEATTACHMENT","COMMENT"])
        }
        p++
    }
}
     
String[] permissionsArray = new String[permissionsInput.size()]
     
if(permissionsInput!=null)
{
    for(int p=0;p<permissionsInput.size();)
    {
        permissionsArray[p]=permissionsInput[p]
        p++
    }
}
                       
//variable declarations 
def user
def localPart
def displayNameinputFromemailAddressinput
String defaultgroup = "confluence-users"
           
def addSpacePermission = ComponentLocator.getComponent(SpacesSoapService)
def String remoteEntity
           
           
           
for(int i=0;i<email.size();)
{
    printtext=printtext+"<br><h1><b>**************************User ${i+1}**************************</h1></b><br>"
        //Check if user is deactivated and reactivate him/her
        if(userAccessor.exists(email[i])==true)
        {
          //variables to use for space permissions
            user = userAccessor.getUserByName(email[i])
            remoteEntity = user.getLowerName()
           
        if(userAccessor.isDeactivated(email[i])==true)
        {
            userAccessor.reactivateUser(userAccessor.getUserByName(email[i]))
           
            //add group
            ConfluenceUserImpl existingreactivatedUser = new ConfluenceUserImpl(email[i], null, email[i])
           
            printtext=printtext.concat("User - <b>${user.getFullName()}</b> (${email[i]}) - has been reactivated and added to the group/s -")
            for(int g=0;g<GroupToUse.size();)
            {
            def group = groupManager.getGroup(GroupToUse[g].name)
            groupManager.addMembership(group, existingreactivatedUser)
            groupManager.addMembership(groupManager.getGroup(defaultgroup), existingreactivatedUser)
            printtext=printtext.concat("<b>${group.getName()}</b>").concat(", ")
            g++
            }
            printtext=printtext+"<br>"
           
            log.info("User - ${email[i]} - has been reactivated and added to the group/s")
           
            //add user to space with mentioned permissions
            if(spaces!=[])
            {
                printtext=printtext+"User - <b>${user.getFullName()}</b> (${email[i]}) - has been added to/probably already is in the space -"
                for(int s=0;s<spaces.size();)
                {
                    addSpacePermission.addPermissionsToSpace(permissionsArray, remoteEntity, spaceKey[s])
                    printtext=printtext+" <b>${spaces[s].getName()} </b>"+","
                    s++
                }
                printtext=printtext+" with <b>${permissionsInput}</b> permissions <br>"
            }
        }
        else
        {
         //add group to existing user
            ConfluenceUserImpl existingUser = new ConfluenceUserImpl(email[i], null, email[i])
           
            printtext=printtext.concat("User - <b>${user.getFullName()}</b> (${email[i]}) - already exists and is added to the group/s -")
            for(int g=0;g<GroupToUse.size();)
            {
            def group = groupManager.getGroup(GroupToUse[g].name)
            groupManager.addMembership(group, existingUser)
            groupManager.addMembership(groupManager.getGroup(defaultgroup), existingUser)
            printtext=printtext.concat("<b>${group.getName()}</b>").concat(", ")
            g++
            }
            printtext=printtext+"<br>"
           
            //add existing user to space with mentioned permissions
            if(spaces!=[])
            {
                printtext=printtext+"User - <b>${user.getFullName()}</b> (${email[i]}) - has been added to/probably already is in the space -"
                for(int s=0;s<spaces.size();)
                {
                addSpacePermission.addPermissionsToSpace(permissionsArray, remoteEntity, spaceKey[s])
                printtext=printtext+" <b>${spaces[s].getName()}</b>"+","
                s++
                }
                printtext=printtext+" with <b>${permissionsInput}</b> permissions <br>"
            }
            log.info("User - ${emailAddressinput} has been added to the group/s and space/s")
        }
    }
    //End of User reactivation snippet
           
    //beginning of user creation snippet
           if((displayNames[i]!=null)&&(userAccessor.exists(email[i])==false))
           {
           
               /*---------AUTO FILL FULL NAME-----------------*/
               if(displayNames[i]!='N/A')
                {
                    displayNames[i] = displayNames[i]
                }
                else
                {
                displayNames[i] = email[i]
           
                // Assign the bit before the @-sign and remove everything past @
                localPart = email[i].replaceAll("@.+\$", "")
           
                // Split into two parts if contains '.'
                displayNameinputFromemailAddressinput =  localPart.split('\\.')
           
                // Extract first name and last name from emailAddressinput address and use them to make up display name
                    if (displayNameinputFromemailAddressinput.size() == 1)
                        displayNames[i] = displayNameinputFromemailAddressinput[0].capitalize()
                    else if (displayNameinputFromemailAddressinput.size() == 2)
                        displayNames[i] = displayNameinputFromemailAddressinput[0].capitalize() + " " + displayNameinputFromemailAddressinput[1].capitalize()
                    else if (displayNameinputFromemailAddressinput.size() == 3)
                        displayNames[i] = displayNameinputFromemailAddressinput[0].capitalize() + " " + displayNameinputFromemailAddressinput[1].capitalize() + " " + displayNameinputFromemailAddressinput[2].capitalize()
                    else
                        displayNames[i] = localPart
            }
               /*-----------AUTO FILL FULL NAME---------------*/
           
      
            if(passwords[i]=="N/A")
            {
            //Random Password generator
            passwords[i]=""
            Random rnd = new Random()
            int randomNumber = rnd.nextInt(100)         
            if(randomNumber==0 || randomNumber<6)             
                randomNumber+=6
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
            }
            //end of random password generator snippet
        
               Credential credential = new Credential(false, passwords[i])
               log.info("displayNameinput: " + displayNameinput)
                          
                          
                if(email[i]!=null)
                {
                                   
                    ConfluenceUserImpl newUser = new ConfluenceUserImpl(email[i], displayNames[i], email[i])
                    userAccessor.createUser(newUser, credential)
           
                    //add user to group
                    printtext=printtext.concat("User - <b>${displayNames[i]}</b> (${email[i]}) - has been created and is added to the group/s -")
                    for(int g=0;g<GroupToUse.size();)
                    {
                    def group = groupManager.getGroup(GroupToUse[g].name)
                    groupManager.addMembership(group, newUser)
                    groupManager.addMembership(groupManager.getGroup(defaultgroup),newUser)
                    printtext=printtext.concat("<b>${group.getName()}</b>").concat(", ")
                    g++
                    }
                    printtext=printtext+"<br>"
           
                    printtext=printtext+"<br>The password is - <b>${passwords[i]}</b> <br></b>Request the user to reset the password once logged in. Ignore if SSO is enabled.<br>"
       
                    log.info("User - <b>${displayNames[i]}</b> (${email[i]}) has been created and added to the group/s")
           
                    //add user to space with mentioned permissions
                    //variables to use for space permissions
                    user = userAccessor.getUserByName(email[i])
                    remoteEntity = user.getLowerName()
                    if(spaces!=[])
                    {
                        printtext=printtext+"User - <b>${displayNames[i]}</b> (${email[i]}) - has been added to/probably already is in the space -"
                        for(int s=0;s<spaces.size();)
                        {
                            addSpacePermission.addPermissionsToSpace(permissionsArray, remoteEntity, spaceKey[s])
                            printtext=printtext+" <b>${spaces[s].getName()} </b>"+","
                            s++
                        }
                        printtext=printtext+" with <b>${permissionsInput}</b> permissions <br>"
                    }
                }
                else
                {
                log.info("Code error / Need to investigate")
                }
    }
    i++ //iterate to the next user
}
}//end of try block
        
catch (Exception ex)
{
    printtext=printtext+"<h1><b><center>Oops...Something went wrong. Make sure the inputs are correct and please try again</h1></b></center>"
}
           
return printtext
