//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Created: 7th March 2023, 03:00 pm
//Author: Rinaldi Michael
//Last Modified: 7th March 2023, 04:56 pm
//References:
//https://community.atlassian.com/t5/Marketplace-Apps-Integrations/Scriptrunner-Check-if-user-group-has-permissions-in-Confluence/qaq-p/1548034
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
import org.jsoup.Jsoup
import com.onresolve.scriptrunner.parameters.annotation.*
import com.atlassian.user.GroupManager
import com.atlassian.confluence.security.*
 
def pageManager = ComponentLocator.getComponent(PageManager)
def spaceManager = ComponentLocator.getComponent(SpaceManager)
def groupManager = ComponentLocator.getComponent(GroupManager)
def spacePermissionManager = ComponentLocator.getComponent(SpacePermissionManager)
 
def allGroupsInConfluence = groupManager.getGroups().asList()
 
String printtext = ""
 
//summary snippet
printtext+= "Total number of Groups in Confluence: <b>${allGroupsInConfluence.size()}</b>"
int permissionsCount=0,nopermissionsCount=0;
String groupwithpermissions="",groupwithoutpermissions=""
for(int s=0;s<allGroupsInConfluence.size();s++)
{
    if(spacePermissionManager.getAllPermissionsForGroup(allGroupsInConfluence[s].getName()).size()==0)
    {
        groupwithpermissions+=allGroupsInConfluence[s].getName()+","
        nopermissionsCount++
    }
    else
    {
        groupwithoutpermissions+=allGroupsInConfluence[s].getName()+","
        permissionsCount++
    }
}
 
//remove trailing commas
groupwithpermissions=groupwithpermissions[0..groupwithpermissions.size()-2]
groupwithoutpermissions=groupwithoutpermissions[0..groupwithpermissions.size()-2]
 
printtext+="<br><br>Number of Groups without permissions: <b>${nopermissionsCount}</b>"
printtext+="<br><b>Groups are:</b> ${groupwithoutpermissions}<br><br>****************************<br>"
 
printtext+="<br>Number of Groups with permissions: <b>${permissionsCount}</b>"
printtext+="<br><b>Groups are: </b>${groupwithpermissions}<br><br>****************************<br>"
 
 
 
//permissions snippet
for(int g=0;g<allGroupsInConfluence.size();g++)
{
    def groupName = allGroupsInConfluence[g].getName()
    def spacePermissions = spacePermissionManager.getAllPermissionsForGroup(groupName)
    //return spacePermissions[0].getSpace().name
    printtext += "<br><h2>${g+1}. Spaces that have permissions for <b>${groupName}</h2></b><br>"
    Set<String> setOfspaces = new HashSet<String>();
        for(int p=0;p<spacePermissions.size();p++)
        {
            try
            {
                setOfspaces.add("Name: "+spacePermissions[p].getSpace().name+", Key: "+spacePermissions[p].getSpace().key)
            }
            catch(Exception ex)
            {
                log.info("Probably empty")
            }       
        }
 
        for(int k=0;k<setOfspaces.size();k++)
        {
            printtext+=setOfspaces[k]+"<br>"
        }
    printtext += "<br><br>***************************************************************************************<br>"
}
 
return printtext
