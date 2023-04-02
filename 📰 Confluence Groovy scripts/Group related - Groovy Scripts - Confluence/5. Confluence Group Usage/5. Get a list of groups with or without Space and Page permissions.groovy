//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Created: 14th Mar 2023, 09:13 am
//Author: Rinaldi Michael
//Last Modified:
//References:
//https://community.atlassian.com/t5/Marketplace-Apps-Integrations/Scriptrunner-Check-if-user-group-has-permissions-in-Confluence/qaq-p/1548034
//https://community.atlassian.com/t5/Confluence-questions/Confluence-page-restrictions-scriptrunner/qaq-p/1333267
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
import java.io.*
import java.lang.*
import java.util.*
import com.atlassian.sal.api.component.ComponentLocator
import com.atlassian.confluence.pages.*
import com.onresolve.scriptrunner.parameters.annotation.*
import com.onresolve.scriptrunner.parameters.annotation.meta.*
import com.atlassian.confluence.spaces.Space
import com.atlassian.confluence.spaces.SpaceManager
import com.onresolve.scriptrunner.parameters.annotation.*
import com.atlassian.user.GroupManager
import com.atlassian.confluence.security.*
import com.atlassian.confluence.core.ContentPermissionManager
import com.atlassian.confluence.security.ContentPermission
import com.atlassian.confluence.core.*
 
  
def pageManager = ComponentLocator.getComponent(PageManager)
def spaceManager = ComponentLocator.getComponent(SpaceManager)
def groupManager = ComponentLocator.getComponent(GroupManager)
def spacePermissionManager = ComponentLocator.getComponent(SpacePermissionManager)
def permissionManager = ComponentLocator.getComponent(PermissionManager)
def contentPermissionManager = ComponentLocator.getComponent(ContentPermissionManager)
def contentEntityObject = ComponentLocator.getComponent(ContentEntityObject)
  
def allGroupsInConfluence = groupManager.getGroups().asList()
Set<String> allGroupsInConfluenceSet = new HashSet<String>();
for(int g=0;g<allGroupsInConfluence.size();g++)
{
    allGroupsInConfluenceSet.add(allGroupsInConfluence[g].getName())
}
 
def allSpacesInConfluence = spaceManager.getAllSpaces()
 
String printtext = ""
Set<String> groupsWithSpacePagePermissions = new HashSet<String>();  //variable that will store groups having page permisisons
 
//get the groups that have space permissions
for(int s=0;s<allGroupsInConfluence.size();s++)
{
    if(spacePermissionManager.getAllPermissionsForGroup(allGroupsInConfluence[s].getName()).size()>0)
    {
        groupsWithSpacePagePermissions.add(allGroupsInConfluence[s].getName())
    }
}
 
 
//get the groups that are in page permissions
for(int s=0;s<allSpacesInConfluence.size();s++)
{
    def pagesInTheSpace = pageManager.getPages(allSpacesInConfluence[s],true).asList()
 
    for(int pa=0;pa<pagesInTheSpace.size();pa++)
    {
        try
        {
            def pageEntity = pageManager.getPage(pagesInTheSpace[pa].getId()).getEntity()
            def pagePermission = pageEntity.getPermissions()
 
            for(int pe=0;pe<pagePermission.size();pe++)
            {
                groupsWithSpacePagePermissions.add(pagePermission[pe].getGroupName())    
            }
        }
        catch (Exception ex)
        {
            log.info("Ignore")
        }
    }
     
}
 
//summary of groups with page permissions
printtext+="<h3><b>Groups that have Space and Page Permissions.</h3></b><br>"
for(int i=1;i<=groupsWithSpacePagePermissions.asList().size();i++)
{
    printtext+="${i}. "+groupsWithSpacePagePermissions[i]+"<br>"
}
 
//summary of groups without page permissions
allGroupsInConfluenceSet.removeAll(groupsWithSpacePagePermissions)
printtext+="<br><br>*********************************************<br><h3><b>Groups without Space and Page Permissions.</h3></b><br>"
for(int i=1;i<=allGroupsInConfluenceSet.asList().size();i++)
{
    printtext+="${i}. "+allGroupsInConfluenceSet[i]+"<br>"
}
  
 
return printtext
