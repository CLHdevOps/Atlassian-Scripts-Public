//Author: Rinaldi Michael
//Created: 15th June 2023, 07:21 pm
//Last Modified: 15th June 2023, 08:47 pm
//References:
//https://library.adaptavist.com/entity/jql-search
/*
In simple terms, you can add an account as a watcher to an issue/s that another account is a watcher of. 
This would, natively, have to be done one pair at a time. Using this script you can input multiple pairs.

For example:

user1@email.com is a watcher in 200 issues. You would like user1@yahoo.com to be a watcher in the same 200 issues.
Also, user2@atlassian.com is a watcher in 400 issues. You would like user2@jira.com to be a watcher in the same 400 issues.

So in the script, you must input this in order:
Field 1: user1@email.com,user2@atlassian.com 
Field 2: user1@yahoo.com,user2@jira.com
*/
 
import java.lang.String
import com.onresolve.scriptrunner.parameters.annotation.*
import com.onresolve.scriptrunner.parameters.annotation.meta.*
import com.atlassian.jira.bc.user.UserService
import com.atlassian.jira.component.ComponentAccessor
 
 
/////////////////////////////////////////////////////////////////////////////////////////////////////////
//Declare managers
def userManager = ComponentAccessor.userManager
 
/////////////////////////////////////////////////////////////////////////////////////////////////////////
//Get input
 
@ShortTextInput(description = 'Enter a comma separated list of usernames', label = 'Old Usernames list')
String oldusernamesInput
String[] oldUsernames = oldusernamesInput.split(',')
 
@ShortTextInput(description = 'Enter a comma separated list of usernames that you want to add to issues where the old users were watchers in. The order should match the first input.', label = 'New Usernames list')
String newusernamesInput
String[] newUsernames = newusernamesInput.split(',')
 
 
/////////////////////////////////////////////////////////////////////////////////////////////////////////
//Add new watchers
String printtext = ""
 
for(int u=0;u<oldUsernames.size();u++)
{
    String JQL = "watcher = \"${oldUsernames[u]}\""
    def issues = Issues.search(JQL).toSet()
    String errors
    HashSet<String> projectErrors = new HashSet<String>();
 
    for(int i=0;i<issues.size();i++)
    {
        try
        {
            def user = userManager.getUserByName(newUsernames[u])
            issues[i].addWatcher(user)
        }
        catch(Exception ex)
        {
            errors="Make sure <b>${newUsernames[u]}</b> has access to the projects that these issues are a part of. User account not added as a watcher to all issues."
            String temp = " ${issues[i].getProjectObject().getName()} ${issues[i].getProjectObject().getKey()}  ,  "
            projectErrors.add(temp)
        }
    }
     
    printtext+="User - <b>${newUsernames[u]}</b> is now a watcher in the <b>${issues.size()}</b> issues that <b>${oldUsernames[u]}</b> was a watcher of."
    printtext+="<br><b>Errors (if any):</b> ${errors} ${projectErrors}"
    printtext+="<br><h2><b>********************************************************************************</h2></b><br>"
}
 
return printtext
