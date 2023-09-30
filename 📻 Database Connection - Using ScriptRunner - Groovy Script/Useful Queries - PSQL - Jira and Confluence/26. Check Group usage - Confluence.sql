/*
Created by: Rinaldi Michael
Date: 29th September 2023
Reference:
1. https://confluence.atlassian.com/confkb/how-to-check-permissions-for-a-space-via-sql-queries-717062216.html
2. https://community.atlassian.com/t5/Confluence-questions/List-of-page-restrictions-from-DB/qaq-p/810586
*/

SELECT
DISTINCT(group_name) as "Confluence Group Name",
CASE
    WHEN TABLE1.permgroupname = group_name  THEN 'Used'
    ELSE '-'
END AS "Space Permissions",
CASE
    WHEN TABLE2.groupname = group_name  THEN 'Used'
    ELSE '-'
END AS "Page/Content Permissions"
 
 
FROM cwd_group cg
 
 
 
 
 
LEFT JOIN
(
SELECT DISTINCT(sp.permgroupname)
FROM SPACEPERMISSIONS sp
JOIN SPACES s ON sp.spaceid = s.spaceid
LEFT JOIN user_mapping um ON sp.permusername = um.user_key
)
as TABLE1 ON TABLE1.permgroupname = cg.group_name
 
 
 
 
LEFT JOIN
(
select DISTINCT(groupname) from content_perm
)
as TABLE2 ON TABLE2.groupname = cg.group_name
