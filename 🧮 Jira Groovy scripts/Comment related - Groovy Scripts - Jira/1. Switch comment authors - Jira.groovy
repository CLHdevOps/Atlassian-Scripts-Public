////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Created - 15th June 2023, 06:45 pm
//Last Modified -
//Author - Rinaldi Michael
//References -
//https://library.adaptavist.com/entity/display-sql-macro
//https://www.w3schools.com/tags/tag_table.asp
//This script will switch the authors of comments (first set of user/s) to the second set of user/s input in the script. 
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      
import java.io.*
import java.util.*
import java.lang.*
import com.atlassian.jira.user.util.*
import com.atlassian.jira.component.ComponentAccessor
import com.onresolve.scriptrunner.db.DatabaseUtil
import com.onresolve.scriptrunner.parameters.annotation.*
 
/////////////////////////////////////////////////////////////////////////////
//declare managers
def userManager = ComponentAccessor.userManager
 
/////////////////////////////////////////////////////////////////////////////
//Get input
       
@ShortTextInput(label = "Resource Name", description = "Type in the name of the Local Database resource set up in ScriptRunner's Reources")
String resourceName
      
@ShortTextInput(description = 'Enter the old username', label = 'Old username')
String oldUsernameInput
String[] oldUsername = oldUsernameInput.split(',')
 
@ShortTextInput(description = 'Enter the new username', label = 'New username')
String newUsernameInput
String[] newUsername = newUsernameInput.split(',')
 
/////////////////////////////////////////////////////////////////////////////
//loop through all users
String printtext=""
for(int c=0;c<oldUsername.size();c++)
{
     
    //Get user keys
    String oldUserKey = userManager.getUserByName(oldUsername[c]).getKey()
    String newUserKey = userManager.getUserByName(newUsername[c]).getKey()
 
    /////////////////////////////////////////////////////////////////////////////
    //Update comments authors
    String sqlQuery = "update jiraaction set author = '${newUserKey}' where author = '${oldUserKey}' and actiontype = 'comment'"
 
    def results
    DatabaseUtil.withSql(resourceName)
    { sql ->
        results = sql.executeUpdate(sqlQuery)
    }
 
    printtext+="The update query to switch comment author from <u>${oldUsername[c]}</u> to <u>${newUsername[c]}</u>:<br> <b>${sqlQuery}</b> has returned the below string--<br><br> ${results.toString()}"
    printtext+="<br><h2><b>*********************************************************************</h2></b><br>"
}
 
return printtext
