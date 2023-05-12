////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Created - 30th Mar 2023, 11:22 am
//Last Modified - 30th Mar 2023, 02:44 pm
//Author - Rinaldi Michael
//References -
//https://library.adaptavist.com/entity/display-sql-macro
/*
Scheme, Permission Scheme granted to group, Comment visibility, Issue security level, Work log visibility



Since workflows are stored differently. Follow the below:
Workflows (Run 2. Write SQL results into a file - Groovy Scriptrunner) or (Run 1. View and copy results of an SQL query - Groovy ScriptRunner) from https://github.com/Rinaldi-James-Michael/Atlassian-Scripts-Public/tree/main/%F0%9F%93%BB%20Database%20Connection%20-%20Using%20ScriptRunner%20-%20Groovy%20Script
(info) Since workflows are stored in XML on Jira's database, it's necessary to export the values from the "Descriptor" column and search for the group's name.

SELECT
  jw.workflowname AS "Workflow",
  jw.descriptor AS "Descriptor"
FROM
  jiraworkflows jw;

Algorithm 
1. Retrieve inputs: Resource Name, Group name, Delimiter, Mode of output, Script Editor File name
2. Retrieve all groups using GroupManager function
3. Loop through every SQL query and run steps 4. to 6. 
4. Append the group name with (' or '% on the SQL query input
5. Try to get the results and assign the values into the output variable. Catch and log the groups that did not have any values.
6. Print or Write the results into a script editor file.

Introduction (One group, all settings)
With the below groovy script. Admins can provide the Resource Name, Group, Delimiter, Mode of output and/or Script Editor File name. Since the original query can only work with one setting at a time. This script will loop through all the SQL queries stored as a dictionary in code and compile all the results. Users will also get a two line summary of the group's usage.
All SQL queries were provided by Atlassian: https://confluence.atlassian.com/jirakb/how-to-identify-group-usage-in-jira-441221524.html

Output includes: Settings used and not used by the group, all PSQL tables for each setting (only values relevant to the group)
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
import com.atlassian.crowd.embedded.api.Group
import com.onresolve.scriptrunner.parameters.annotation.GroupPicker
       
def groupManager = ComponentAccessor.GroupManager
     
@ShortTextInput(label = "Resource Name", description = "Type in the name of the Local Database resource set up in ScriptRunner's Reources")
String resourceName
     
@GroupPicker(label = 'Group', description = 'Pick a group', placeholder = 'Pick a group')
Group group
      
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
  
String printtext="",settingsNotUsedIn,settingsUsedIn
  
if(outputOption=="cp")
{
    settingsNotUsedIn="<b>Settings that the group: <u>${group.getName()}</u> does not use:</b> "
    settingsUsedIn="<b>Settings that the group: <u>${group.getName()}</u> uses:</b> "
}
else
{
    settingsNotUsedIn="Settings that the group: ${group.getName()} does not use: "
    settingsUsedIn="Settings that the group: ${group.getName()} uses: "  
}
  
Dictionary sqlQueryInput = new Hashtable();
   sqlQueryInput.put("Project Roles","SELECT   pra.roletypeparameter AS \"Group\",   pr.name AS \"Project Role\",   p.pname AS \"Project\" FROM   projectroleactor pra   LEFT JOIN projectrole pr ON pra.projectroleid = pr.id   LEFT JOIN project p ON pra.pid = p.id WHERE   pra.roletype = 'atlassian-group-role-actor'   AND pra.roletypeparameter in")
   sqlQueryInput.put("Global Permissions","SELECT   gp.group_id AS \"Group\",   gp.permission AS \"Permission\" FROM   globalpermissionentry gp WHERE   gp.group_id in")
   sqlQueryInput.put("Custom Fields","SELECT   cfv.stringvalue AS \"Group(s)\",   cf.cfname AS \"Custom Field\",   CONCAT(p.pkey, '-', ji.issuenum) AS \"Issue\" FROM   customfieldvalue cfv   LEFT JOIN customfield cf ON cf.id = cfv.customfield   LEFT JOIN jiraissue ji ON cfv.issue = ji.id   LEFT JOIN project p ON ji.project = p.id WHERE   cf.customfieldtypekey IN (     'com.atlassian.jira.plugin.system.customfieldtypes:grouppicker',     'com.atlassian.jira.plugin.system.customfieldtypes:multigrouppicker'   )   AND cfv.stringvalue in")
   sqlQueryInput.put("Shared Dashboards","SELECT   shp.param1 AS \"Group\",   pp.pagename AS \"Dashboard\" FROM   sharepermissions shp   LEFT JOIN portalpage pp ON shp.entityid = pp.id WHERE   shp.entitytype = 'PortalPage'   AND shp.sharetype = 'group'   AND shp.param1 IN")
   sqlQueryInput.put("Shared Filters","SELECT   shp.param1 AS \"Group\",   sr.filtername AS \"Filter\" FROM   sharepermissions shp   LEFT JOIN searchrequest sr ON shp.entityid = sr.id WHERE   shp.entitytype = 'SearchRequest'   AND shp.sharetype = 'group'   AND shp.param1 IN")
   sqlQueryInput.put("Filter Subscriptions","SELECT  fs.groupname AS \"Group\", sr.filtername AS \"Filter Name\" FROM   filtersubscription fs   LEFT JOIN searchrequest sr ON fs.filter_i_d = sr.id WHERE   fs.groupname IN")
   sqlQueryInput.put("Application Access","SELECT     license_role_name AS \"Application\",     group_id AS \"Group\" FROM     licenserolesgroup WHERE     group_id in")
   sqlQueryInput.put("Saved Filters (JQL)","SELECT     id AS \"Filter ID\",     filtername AS \"Filter Name\",     reqcontent AS \"JQL\" FROM     searchrequest WHERE     LOWER(reqcontent) like")
   sqlQueryInput.put("Notification Schemes","select n.* from notification n where n.notif_type = 'Group_Dropdown' and n.notif_parameter in")
   sqlQueryInput.put("Permissions Scheme","SELECT   SP.id,SP.perm_parameter AS GroupName FROM   schemepermissions SP INNER JOIN   permissionscheme PS ON SP.scheme = PS.id WHERE   SP.perm_type = 'group'   AND SP.perm_parameter in")
   sqlQueryInput.put("Permission Scheme granted to group","SELECT   SP.perm_parameter AS GroupName, PS.name AS PermissionSchemeName, SP.permission_key AS Permission FROM   schemepermissions SP INNER JOIN   permissionscheme PS ON SP.scheme = PS.id WHERE   SP.perm_type = 'group'   AND SP.perm_parameter in")
   sqlQueryInput.put("Comment visibility","select ja.id,ja.issueid,ja.actiontype,ja.actionlevel from jiraaction ja where ja.actionlevel in")
   sqlQueryInput.put("Issue security level","select sis.* from schemeissuesecurities sis where sis.sec_type = 'group' and sis.sec_parameter in")
   sqlQueryInput.put("Work log visibility","select wl.id,wl.issueid,wl.grouplevel from worklog wl where wl.grouplevel in")
  
Dictionary sqlQueryCount = new Hashtable();
    sqlQueryCount.put(0,"Project Roles")
    sqlQueryCount.put(1,"Global Permissions")
    sqlQueryCount.put(2,"Custom Fields")
    sqlQueryCount.put(3,"Shared Dashboards")
    sqlQueryCount.put(4,"Shared Filters")
    sqlQueryCount.put(5,"Filter Subscriptions")
    sqlQueryCount.put(6,"Application Access")
    sqlQueryCount.put(7,"Saved Filters (JQL)")
    sqlQueryCount.put(8,"Notification Schemes")
    sqlQueryCount.put(9,"Permissions Scheme")
    sqlQueryCount.put(10,"Permission Scheme granted to group")
    sqlQueryCount.put(11,"Comment visibility")
    sqlQueryCount.put(12,"Issue security level")
    sqlQueryCount.put(13,"Work log visibility")
  
//return sqlQueryInput.get(sqlQueryCount.get(4))
  
  
//loop through sql results for each group
for(int s=0;s<sqlQueryInput.size();s++)
{
  
    Boolean headerPrintedOnce = true
    String sqlQuery = sqlQueryInput.get(sqlQueryCount.get(s))
  
    if(sqlQuery[sqlQuery.size()-4..sqlQuery.size()-1]=="like")
        sqlQuery+=" '%"+group.getName()+"%'"
    else
        sqlQuery+=" ('"+group.getName()+"')"
  
    
    try
    {
        def results
        DatabaseUtil.withSql(resourceName)
        { sql ->
        results = sql.rows(sqlQuery)
        }
     
        def columnsHeader = results[0].keySet()
  
        if(outputOption=="se")
        {
            printtext+="${sqlQueryCount.get(s)} Table"
            printtext+="\n"
        }
        else
        {
            printtext+="<h2><b>${sqlQueryCount.get(s)} Table</b></h2>"
            printtext+="<br>"  
        } 
    
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
                printtext+="<br>"
        }
        settingsUsedIn+=sqlQueryCount.get(s)+", "
  
    }//end of try block
     
    catch(Exception ex)
    {
        log.warn("Group - ${group.getName()} has no values in the query for ${sqlQueryCount.get(s)}")
        settingsNotUsedIn+=sqlQueryCount.get(s)+", "
    }
      
                if(outputOption=="se")
                printtext+="\n\n"
            else
                printtext+="<br><br>"
}
      
/*******************************************************************************************************************************************/
//for Board administrators
 
 
 
String sqlQueryforBoard = "SELECT   ba.\"KEY\" AS \"Group\",   rv.\"NAME\" AS \"Board\" FROM   \"AO_60DB71_BOARDADMINS\" ba   LEFT JOIN \"AO_60DB71_RAPIDVIEW\" rv ON ba.\"RAPID_VIEW_ID\" = rv.\"ID\" WHERE   ba.\"TYPE\" = 'GROUP'"
def results1
 
 
    DatabaseUtil.withSql(resourceName)
    { sql ->
        results1 = sql.rows(sqlQueryforBoard)
    }
 
    def columnsHeader = results1[0].keySet()
    boolean usedAtLeastOnce = false
 
    //values
    for(int r=0;r<results1.size();r++)
    {
 
 
        if(results1[r].values()[0].toString()==group.getName().toString())
        {
            if(outputOption=="se" && r==0)
            {
                printtext+="Board Administrators Table"
                printtext+="\n"
            }
            else if(r==0)
            {
                printtext+="<h2><b>Board Administrators Table</b></h2>"
                printtext+="<br>"  
            }
 
 
            for(int v=0;v<columnsHeader.size();v++)
            {
                printtext+=results1[r].values()[v].toString()+delimiter
            }
 
            if(outputOption=="se")
                printtext+="\n"
            else
                printtext+="<br>"
 
            settingsUsedIn+="Board Administrators, "
            usedAtLeastOnce=true
        }
    }
 
    if(usedAtLeastOnce!=true)
        settingsNotUsedIn+="Board Administrators, "
 
/*******************************************************************************************************************************************/
  
  
  
////////////////////////////////////////////////////////////////////////////////////////////////////////
//Start printing results
//remove trailing commas
settingsNotUsedIn=settingsNotUsedIn[0..settingsNotUsedIn.size()-3]
settingsUsedIn=settingsUsedIn[0..settingsUsedIn.size()-3]
  
if(outputOption=="se")
{
    @ShortTextInput(description = 'Enter any file name. This will create a new file in Script Editor.', label = 'Enter the FileName (if Script Editor is chosen)')
    String fileName
      
    new File("/home/jira/shared_home/scripts/${fileName}.groovy").withWriter('utf-8')
    {
        writer -> writer.writeLine (settingsNotUsedIn+"\n\n"+settingsUsedIn+"\n\n"+printtext)
    }
    return "The contents are written into <b>${fileName}.groovy</b> in the Script Editor."
}
else
{
    return (settingsNotUsedIn+"<br><br>"+settingsUsedIn+"<br><br>"+printtext)
}
