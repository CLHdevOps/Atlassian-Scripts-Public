////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Created - Mar 29, 2023 10:45 am
//Last Modified - Mar 29, 2023 11:56 am
//Author - Rinaldi Michael
//References -
//https://library.adaptavist.com/entity/display-sql-macro
/*
Algorithm 
1. Retrieve inputs: Resource Name, SQL Query, Delimiter, Mode of output, Script Editor File name
2. Retrieve all groups using GroupManager function
3. Loop through every group and run steps 4. to 6. 
4. Append the group name with (' or '% on the SQL query input
5. Try to get the results and assign the values into the output variable. Catch and log the groups that did not have any values.
6. Print or Write the results into a script editor file.

Introduction (One setting, all groups)
With the below groovy script. Admins can provide the Resource Name, SQL Query, Delimiter, Mode of output and/or Script Editor File name. Since the original query can only work with one group at a time. This script will loop through all groups present in Jira and compile all the results. Users will also get a one line summary of the groups usage.

All SQL queries were provided by Atlassian: https://confluence.atlassian.com/jirakb/how-to-identify-group-usage-in-jira-441221524.html
Only input the sql queries without ; and upto in or like. Basically remove the part with the group names.
eg. SELECT pra.roletypeparameter AS "Group", pr.name AS "Project Role", p.pname AS "Project" FROM projectroleactor pra LEFT JOIN projectrole pr ON pra.projectroleid = pr.id LEFT JOIN project p ON pra.pid = p.id WHERE pra.roletype = 'atlassian-group-role-actor' AND pra.roletypeparameter in

Output includes: list of groups that does not use the setting (each sql query is for each setting), the same PSQL table but it is a compilation of results for every group.
*/
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
     
import java.io.*
import java.util.*
import java.lang.*
import com.onresolve.scriptrunner.db.DatabaseUtil
import com.onresolve.scriptrunner.parameters.annotation.*
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.crowd.model.group.Groups
import com.atlassian.crowd.embedded.api.Group
import com.onresolve.scriptrunner.parameters.annotation.*
import com.onresolve.scriptrunner.parameters.annotation.meta.*
      
def groupManager = ComponentAccessor.GroupManager
def allGroupsInJira = groupManager.getAllGroupNames()
String printtext="",groupsNotUsed
Boolean headerPrintedOnce = true
    
@ShortTextInput(label = "Resource Name", description = "Type in the name of the Local Database resource set up in ScriptRunner's Reources")
String resourceName
    
@ShortTextInput(label = "SQL Query", description = "Type in the SQL query without the semicolon ; at the end")
String sqlQueryInput
     
@ShortTextInput(label = "Delimiter", description = "Enter the character that you would like to separate each value with")
String delimiter
  
@Select(
    label = "Output option",
    description = "Select how you would like to view the output",
    options = [
        @Option(label = "Current page", value = "cp"),
        @Option(label = "Script Editor", value = "se"),
    ]
)
String outputOption
 
if (outputOption=="cp")
    groupsNotUsed="<b>Groups that are not used:</b> "
else
    groupsNotUsed="Groups that are not used: "
    
//loop through sql results for each group
for(int g=0;g<allGroupsInJira.size();g++)
{
    String sqlQuery = sqlQueryInput
    if(sqlQuery[sqlQuery.size()-4..sqlQuery.size()-1]=="like")
        sqlQuery+=" '%"+allGroupsInJira[g]+"%'"
    else
        sqlQuery+=" ('"+allGroupsInJira[g]+"')"
   
    try
    {
        def results
        DatabaseUtil.withSql(resourceName)
        { sql ->
        results = sql.rows(sqlQuery)
        }
    
        def columnsHeader = results[0].keySet()
   
        //header
        if(headerPrintedOnce)
        {
            for(int h=0;h<columnsHeader.size();h++)
            {
                if(outputOption=="cp")
                    printtext+="<b>${columnsHeader[h]}${delimiter}</b>"
                else
                    printtext+="${columnsHeader[h]}${delimiter}"
            }
          
        if(outputOption=="se")
            printtext+="\n"
        else
            printtext+="<br>"
        headerPrintedOnce=false
        }
           
   
        //values
        for(int r=0;r<results.size();r++)
        {
            for(int v=0;v<columnsHeader.size();v++)
            {
                printtext+=results[r].values()[v].toString()+delimiter
            }        
  
            if(outputOption=="se")
                printtext+="\n"
            else
                printtext+="<br>"          }
      
    }//end of try block
    
    catch(Exception ex)
    {
        log.warn("Group - ${allGroupsInJira[g]} has no values in this query")
        groupsNotUsed+=allGroupsInJira[g]+", "
    }
   
}
     
//remove trailing commas
groupsNotUsed=groupsNotUsed[0..groupsNotUsed.size()-3]
  
if(outputOption=="se")
{
    @ShortTextInput(description = 'Enter any file name. This will create a new file in Script Editor.', label = 'Enter the FileName (if Script Editor is chosen)')
    String fileName
     
    new File("/home/jira/shared_home/scripts/${fileName}.groovy").withWriter('utf-8')
    {
        writer -> writer.writeLine (groupsNotUsed+"\n\n"+printtext)
    } 
    return "The contents are written into <b>${fileName}.groovy</b> in the Script Editor."
}
else
{
    return (groupsNotUsed+"<br><br>"+printtext)
}
