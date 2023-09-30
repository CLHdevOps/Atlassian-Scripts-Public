/*
//Compilation Query by: Rinaldi Michael
//Date: 29th September 2023
This query compiles every query provided by Atlassian to quickly summarize where groups in Jira are used.
Reference: https://confluence.atlassian.com/jirakb/how-to-identify-group-usage-in-jira-441221524.html
*/


SELECT
DISTINCT(group_name) as "Jira Group Name",
CASE
    WHEN TABLE1."Group" = group_name  THEN 'Used'
    ELSE '-'
END AS "Projects Roles",
CASE
    WHEN TABLE2."Group" = group_name  THEN 'Used'
    ELSE '-'
END AS "Global Permissions",
CASE
    WHEN TABLE3."Group(s)" = group_name  THEN 'Used'
    ELSE '-'
END AS "Custom Fields",
CASE
    WHEN TABLE4."Group" = group_name  THEN 'Used'
    ELSE '-'
END AS "Shared Dashboards",
CASE
    WHEN TABLE5."Group" = group_name  THEN 'Used'
    ELSE '-'
END AS "Shared Filters",
CASE
    WHEN TABLE6."Group" = group_name  THEN 'Used'
    ELSE '-'
END AS "Filter Subscriptions",
CASE
    WHEN TABLE7."Group" = group_name  THEN 'Used'
    ELSE '-'
END AS "Board Administrators",
CASE
    WHEN TABLE8."Group" = group_name  THEN 'Used'
    ELSE '-'
END AS "Application Access",
CASE
    WHEN TABLE9.groupname = group_name  THEN 'Used'
    ELSE '-'
END AS "Permission Scheme",
CASE
    WHEN TABLE10.groupname = group_name  THEN 'Used'
    ELSE '-'
END AS "Permission Scheme Granted to Group",
CASE
    WHEN TABLE11.actionlevel = group_name  THEN 'Used'
    ELSE '-'
END AS "Comment Visibility",
CASE
    WHEN TABLE12.sec_parameter = group_name  THEN 'Used'
    ELSE '-'
END AS "Issue security level",
CASE
    WHEN TABLE13.grouplevel = group_name  THEN 'Used'
    ELSE '-'
END AS "Work Log visibility",
CASE
    WHEN TABLE14."JQL" LIKE CONCAT('%',group_name,'%')  THEN 'Used'
    ELSE '-'
END AS "Saved Filters (JQL)",
CASE
    WHEN TABLE15."VALUE" LIKE CONCAT('%',group_name,'%')  THEN 'Used'
    ELSE '-'
END AS "Automation Rules",
CASE
    WHEN TABLE16."Descriptor" LIKE CONCAT('%',group_name,'%')  THEN 'Used'
    ELSE '-'
END AS "Workflows"
   
   
FROM cwd_group cg
   
   
   
   
   
LEFT JOIN
(
SELECT
  DISTINCT(pra.roletypeparameter) AS "Group"
FROM
  projectroleactor pra
  LEFT JOIN projectrole pr ON pra.projectroleid = pr.id
  LEFT JOIN project p ON pra.pid = p.id
WHERE
  pra.roletype = 'atlassian-group-role-actor'
)
as TABLE1 ON TABLE1."Group" = cg.group_name
   
   
   
   
   
   
LEFT JOIN
(
SELECT
  DISTINCT(gp.group_id) AS "Group"
FROM
  globalpermissionentry gp
)
as TABLE2 ON TABLE2."Group" = cg.group_name
   
   
   
   
   
   
   
LEFT JOIN
(
SELECT
  DISTINCT(cfv.stringvalue) AS "Group(s)"
FROM
  customfieldvalue cfv
  LEFT JOIN customfield cf ON cf.id = cfv.customfield
  LEFT JOIN jiraissue ji ON cfv.issue = ji.id
  LEFT JOIN project p ON ji.project = p.id
WHERE
  cf.customfieldtypekey IN (
    'com.atlassian.jira.plugin.system.customfieldtypes:grouppicker',
    'com.atlassian.jira.plugin.system.customfieldtypes:multigrouppicker'
  )
)
as TABLE3 ON TABLE3."Group(s)" = cg.group_name
   
   
   
   
   
   
   
   
   
LEFT JOIN
(
SELECT
  DISTINCT(shp.param1) AS "Group"
FROM
  sharepermissions shp
  LEFT JOIN portalpage pp ON shp.entityid = pp.id
WHERE
  shp.entitytype = 'PortalPage'
  AND shp.sharetype = 'group'
)
as TABLE4 ON TABLE4."Group" = cg.group_name
   
   
   
   
   
   
LEFT JOIN
(
SELECT
  DISTINCT(shp.param1) AS "Group"
FROM
  sharepermissions shp
  LEFT JOIN searchrequest sr ON shp.entityid = sr.id
WHERE
  shp.entitytype = 'SearchRequest'
  AND shp.sharetype = 'group'
)
as TABLE5 ON TABLE5."Group" = cg.group_name
   
   
   
   
   
   
   
   
   
   
LEFT JOIN
(
SELECT
DISTINCT(fs.groupname) AS "Group"
FROM
  filtersubscription fs
  LEFT JOIN searchrequest sr ON fs.filter_i_d = sr.id
)
AS TABLE6 ON TABLE6."Group" = cg.group_name
   
   
   
   
   
   
LEFT JOIN
(
SELECT
  DISTINCT(ba."KEY") AS "Group"
FROM
  "AO_60DB71_BOARDADMINS" ba
  LEFT JOIN "AO_60DB71_RAPIDVIEW" rv ON ba."RAPID_VIEW_ID" = rv."ID"
WHERE
  ba."TYPE" = 'GROUP'
)
AS TABLE7 ON TABLE7."Group" = cg.group_name
   
   
   
   
   
LEFT JOIN
(
SELECT
    DISTINCT(group_id) AS "Group"
FROM
    licenserolesgroup
)
AS TABLE8 ON TABLE8."Group" = cg.group_name
   
   
   
   
   
LEFT JOIN
(
SELECT
  DISTINCT(SP.perm_parameter) AS GroupName
FROM
  schemepermissions SP
INNER JOIN
  permissionscheme PS ON SP.scheme = PS.id
WHERE
  SP.perm_type = 'group'
)
AS TABLE9 ON TABLE9.groupname = cg.group_name
   
   
   
   
   
LEFT JOIN
(
SELECT
  DISTINCT(SP.perm_parameter) AS GroupName
FROM
  schemepermissions SP
INNER JOIN
  permissionscheme PS ON SP.scheme = PS.id
WHERE
  SP.perm_type = 'group'
)
AS TABLE10 ON TABLE10.groupname = cg.group_name
   
   
   
   
   
   
LEFT JOIN
(
select DISTINCT(ja.actionlevel)
from jiraaction ja
)
AS TABLE11 ON TABLE11.actionlevel = cg.group_name
   
   
   
   
   
   
   
LEFT JOIN
(
select DISTINCT(sec_parameter)
from schemeissuesecurities sis
where sis.sec_type = 'group'
)
AS TABLE12 ON TABLE12.sec_parameter = cg.group_name
   
   
   
   
LEFT JOIN
(
select DISTINCT(wl.grouplevel)
from worklog wl
) AS TABLE13 ON TABLE13.grouplevel = cg.group_name
  
  
  
  
  
LEFT JOIN
(
SELECT
    DISTINCT(reqcontent) AS "JQL"
FROM
    searchrequest
) AS TABLE14 ON TABLE14."JQL" LIKE CONCAT('%',cg.group_name,'%')
  
  
  
  
  
LEFT JOIN
(
select "VALUE" from "AO_589059_RULE_CFG_COMPONENT"
) AS TABLE15 ON TABLE15."VALUE" LIKE CONCAT('%',cg.group_name,'%')
  
  
  
  
LEFT JOIN
(
SELECT
  jw.descriptor AS "Descriptor"
FROM
  jiraworkflows jw
) AS TABLE16 ON TABLE16."Descriptor" LIKE CONCAT('%',cg.group_name,'%')
