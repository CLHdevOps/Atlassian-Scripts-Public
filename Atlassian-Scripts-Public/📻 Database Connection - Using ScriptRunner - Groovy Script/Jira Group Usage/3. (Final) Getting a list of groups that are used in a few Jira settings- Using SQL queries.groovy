////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Created - 27th March 2023, 02:47 pm
//Last Modified - 31st March 2023, 11:16 am
//Author - Rinaldi Michael
//References -
//https://library.adaptavist.com/entity/display-sql-macro
/*
Results take a few minutes to load.
*The output for Workflows tends to show more groups than the actual result. This is because the only way to find it's usage is to character match the group name to all the data stored in the Workflows table, this means any random text that is coincidentally the same as the group name will be listed. Rest assured, the output will always include the group/s that are used in the instance's Workflows.

Introduction
The scripts present in this page will give you an overview of all the groups that use and do not use all settings listed in this page. The overview includes: Names of the groups that use and do not use at least one listed setting, the settings it uses (if used).

All SQL queries were provided by Atlassian: https://confluence.atlassian.com/jirakb/how-to-identify-group-usage-in-jira-441221524.html
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
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.crowd.model.group.Groups
import com.atlassian.crowd.embedded.api.Group
      
def groupManager = ComponentAccessor.GroupManager
def allGroupsInJira = groupManager.getAllGroupNames()
  
  
HashMap<String, String> groupHashMap = new HashMap<>();
//create a HashMap set for all Groups
for(int g=0;g<allGroupsInJira.size();g++)
{
    groupHashMap.put(allGroupsInJira[g],new HashSet<String>());
}
     
@ShortTextInput(label = "Database Resource Name", description = "Type in the name of the Local Database resource set up in ScriptRunner's Reources")
String resourceName
  
//String[] sqlQueryInput = [
//]
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
         
       
String printtext=""
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
  
  
Set<String> groupUsed =  new HashSet<String>();
Set<String> groupNotUsed =  new HashSet<String>();
     
//loop through all SQl queries in SQL Query Input
for(int s=0;s<sqlQueryInput.size();s++)
{
    String sqlQuery
     
    //loop through sql results for each group
    for(int g=0;g<allGroupsInJira.size();g++)
    {
        sqlQuery = sqlQueryInput.get(sqlQueryCount.get(s))
        def results
        if(s!=7)
            sqlQuery+=" ('"+allGroupsInJira[g]+"')"
        else
            sqlQuery+=" '%"+allGroupsInJira[g]+"%'"
     
        DatabaseUtil.withSql(resourceName)
        { sql ->
            results = sql.rows(sqlQuery)
        }
       
        def columnsHeader
        try
        {
            columnsHeader = results[0].keySet()  //if a value is returned. This means the group is used
            groupUsed.add(("${allGroupsInJira[g]}").toString())
            groupHashMap.get(allGroupsInJira[g]).add(sqlQueryCount.get(s))
        }
        catch(Exception ex)
        {
            groupNotUsed.add(("${allGroupsInJira[g]}").toString())  //if the try block gave an error. This means the group had no values
        }
    }
}
//end of results from sql queries with group input
     
     
     
/*******************************************************************************************************************************************/
     
//for Board administrators
String sqlQueryforBoard = "SELECT   ba.\"KEY\" AS \"Group\",   rv.\"NAME\" AS \"Board\" FROM   \"AO_60DB71_BOARDADMINS\" ba   LEFT JOIN \"AO_60DB71_RAPIDVIEW\" rv ON ba.\"RAPID_VIEW_ID\" = rv.\"ID\" WHERE   ba.\"TYPE\" = 'GROUP'"
        def results1
        DatabaseUtil.withSql(resourceName)
        { sql ->
            results1 = sql.rows(sqlQueryforBoard)
        }
  
//values
for(int r=0;r<results1.size();r++)
{
    for(int v=0;v<1;v++)
    {
        groupUsed.add(results1[r].values()[v].toString())
        groupHashMap.get(results1[r].values()[v].toString()).add("Board administrators")
    }
}
     
     
     
/*******************************************************************************************************************************************/
     
//for Workflows
String sqlQueryforWorkflow = "SELECT   jw.workflowname AS \"Workflow\",   jw.descriptor AS \"Descriptor\" FROM   jiraworkflows jw"
        def results2
        DatabaseUtil.withSql(resourceName)
        { sql ->
        results2 = sql.rows(sqlQueryforWorkflow)
        }
    
//workflow table values
for(int r=0;r<results2.size();r++)
{
    for(int v=0;v<2;v++)
    {
        for(int c=0;c<allGroupsInJira.size();c++)
        {
            def group = allGroupsInJira[c].toString()
            //workflowValues+=results2[r].values()[v].toString()+";"
            String workflowLine = results2[r].values()[v].toString()
            if(workflowLine.contains(group))
            {
                groupUsed.add(group)
                groupHashMap.get(group).add("Workflows")
            }
        }
    }
}
/*******************************************************************************************************************************************/
    
     
printtext+="<br><h2><b>Groups that are linked to:</b></h2> Project Roles, Global Permissions, Custom Fields, Shared Dashboards, Shared Filters, Workflows, Filter Subscriptions, Board Administrators (Jira Agile), Application Access (Jira 8.x), Saved Filters content, Notification Schemes, Permission Schemes, Comment visibility, Issue security level, Work log visibility</h3><br><br><b>Number of groups (Ignores 'null'):</b> ${groupUsed.size()-1}<br><br>"
for(int b=1;b<=groupUsed.size();b++)
{
        printtext+="<b>${groupUsed[b]}</b> -- ${groupHashMap.get(groupUsed[b])}<br>"
}
printtext+="<br><br>****************************************<br>"
     
groupNotUsed.removeAll(groupUsed)
printtext+="<br><h2><b>Groups that are NOT linked to:</b></h2> Project Roles, Global Permissions, Custom Fields, Shared Dashboards, Shared Filters, Workflows, Filter Subscriptions, Board Administrators (Jira Agile), Application Access (Jira 8.x), Saved Filters content, Notification Schemes, Permission Schemes, Comment visibility, Issue security level, Work log visibility</h3><br><br><b>Number of groups (Ignores 'null'):</b> ${groupNotUsed.size()-1}<br><br>"
for(int b=1;b<=groupNotUsed.size();b++)
{
    printtext+="<b>${groupNotUsed[b]}</b><br>"
}
    
  
return printtext
