////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Created - 27th March 2023, 02:47 pm
//Last Modified - 27th March 2023, 04:22 pm
//Author - Rinaldi Michael
//References -
//https://library.adaptavist.com/entity/display-sql-macro
/*
2. Provide SQL query input and output the results of each
In some SQL queries. You will have to input the group names. Providing all group names together in the query does not work and throws a null pointer exception as some groups do not use that setting. The below script will take this into account and provide a more meaningful output. The output will list out the groups that use and do not use that setting. 

If the query is:   SELECT pra.roletypeparameter AS "Group", pr.name AS "Project Role", p.pname AS "Project" FROM projectroleactor pra LEFT JOIN projectrole pr ON pra.projectroleid = pr.id LEFT JOIN project p ON pra.pid = p.id WHERE pra.roletype = 'atlassian-group-role-actor' AND pra.roletypeparameter in ('jira-software-users', 'abc-jira-software-users');
You should input: SELECT   pra.roletypeparameter AS "Group",   pr.name AS "Project Role",   p.pname AS "Project" FROM   projectroleactor pra   LEFT JOIN projectrole pr ON pra.projectroleid = pr.id   LEFT JOIN project p ON pra.pid = p.id WHERE   pra.roletype = 'atlassian-group-role-actor'   AND pra.roletypeparameter in

*Comment one of these two lines according to the SQL query you will use:
sqlQuery+=" ('"+allGroupsInJira[g]+"')"
sqlQuery+=" '%"+allGroupsInJira[g]+"%'"
*/
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
import java.io.*
import java.util.*
import com.onresolve.scriptrunner.db.DatabaseUtil
import com.onresolve.scriptrunner.parameters.annotation.*
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.crowd.model.group.Groups
import com.atlassian.crowd.embedded.api.Group
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.crowd.model.group.Groups
import com.atlassian.crowd.embedded.api.Group
 
def groupManager = ComponentAccessor.GroupManager
def allGroupsInJira = groupManager.getAllGroupNames()
 
 
@ShortTextInput(label = "Resource Name", description = "Type in the name of the Local Database resource set up in ScriptRunner's Reources")
String resourceName
  
@ShortTextInput(label = "SQL Query", description = "Type in the SQL query without the group names part and without the semicolon ; at the end")
String sqlQueryInput
 
  
String printtext="",groupUsed="<br><h2><b>Groups that are linked to the setting<br><br></h2></b>",groupNotUsed="<br><h2><b>Groups that are NOT linked to the setting<br><br></h2></b>"
 
//loop through sql results for each group
for(int g=0;g<allGroupsInJira.size();g++)
{
    String sqlQuery = sqlQueryInput
    if(sqlQuery.contains("like"))
        sqlQuery+=" '%"+allGroupsInJira[g]+"%'"
    else
        sqlQuery+=" ('"+allGroupsInJira[g]+"')"
    //return sqlQuery
 
    def results
    DatabaseUtil.withSql(resourceName)
    { sql ->
        results = sql.rows(sqlQuery)
    }
  
    def columnsHeader
    try
    {
        columnsHeader = results[0].keySet()
        groupUsed+="${allGroupsInJira[g]}<br>"
    }
    catch(Exception ex)
    {
        groupNotUsed+="${allGroupsInJira[g]}<br>"
    }
}
 
printtext+=groupUsed+"<br><br>****************************************<br>"+groupNotUsed
  
return printtext
