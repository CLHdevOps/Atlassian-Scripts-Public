//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Created: 13th Mar 2023, 05:35 pm
//Author: Rinaldi Michael
//Last Modified: 21st Mar 2023, 01:21 pm
//References:
//https://community.atlassian.com/t5/Marketplace-Apps-Integrations/Scriptrunner-Check-if-user-group-has-permissions-in-Confluence/qaq-p/1548034
//https://community.atlassian.com/t5/Confluence-questions/Confluence-page-restrictions-scriptrunner/qaq-p/1333267
//https://www.theserverside.com/blog/Coffee-Talk-Java-News-Stories-and-Opinions/How-to-make-multiple-values-per-key-in-a-Java-map-possible
//https://www.geeksforgeeks.org/java-util-hashmap-in-java-with-examples/
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
 
//HashMap<String, String> groupAndPage1 = new HashMap<>();
//groupAndPage1.put("Group 1",new ArrayList<String>());
//groupAndPage1.get("Group 1").add("Page 1");
//groupAndPage1.get("Group 1").add("Page 2");
//groupAndPage1.put("Group 2",new ArrayList<String>());
//groupAndPage1.get("Group 2").add("Page 3")
//return groupAndPage1.entrySet()[1]
    
def allGroupsInConfluence = groupManager.getGroups().asList()
Set<String> allGroupsInConfluenceSet = new HashSet<String>();
for(int g=0;g<allGroupsInConfluence.size();g++)
{
    allGroupsInConfluenceSet.add(allGroupsInConfluence[g].getName())
}
   
def allSpacesInConfluence = spaceManager.getAllSpaces()
   
String printtext = ""
Set<String> groupsWithPagePermissions = new HashSet<String>();  //variable that will store groups having page permisisons
  
//Traverse through all pages in all spaces and fetch Group Names from each page's permissions
Set<String> pagesInConfluenceSet = new HashSet<String>();
HashMap<String, String> groupAndPage = new HashMap<>();
  
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
                groupsWithPagePermissions.add(pagePermission[pe].getGroupName())
                pagesInConfluenceSet.add(pagesInTheSpace[pa].toString())
 
                if(groupAndPage.containsKey(pagePermission[pe].getGroupName())==false)
                {
                    groupAndPage.put(pagePermission[pe].getGroupName(),new ArrayList<String>());
                    groupAndPage.get(pagePermission[pe].getGroupName()).add(pagesInTheSpace[pa]);
                }
                else
                {
                    groupAndPage.get(pagePermission[pe].getGroupName()).add(pagesInTheSpace[pa]);
                }
 
            }
        }
        catch (Exception ex)
        {
            log.info("Ignore")
        }
    }
       
}
  
   
//summary of groups with page permissions
printtext+="<h2><b>Groups that have Page Permissions.</h2></b><br>"
for(int i=1;i<=groupsWithPagePermissions.asList().size();i++)
{
    printtext+="${i}. "+groupsWithPagePermissions[i]+"<br>"
}
   
//summary of groups without page permissions
allGroupsInConfluenceSet.removeAll(groupsWithPagePermissions)
printtext+="<br><br>*********************************************<br><h3><b>Groups without Page Permissions.</h3></b><br>"
for(int i=1;i<=allGroupsInConfluenceSet.asList().size();i++)
{
    printtext+="${i}. "+allGroupsInConfluenceSet[i]+"<br>"
}
printtext += "<br><br>********************************<br>"
 
  
//summary of groups with page permissions
printtext+="<br>"
for(int i=1;i<groupAndPage.size();i++)
{
    //if(groupAndPage.entrySet()[i].getKey()=="null")
    //    continue
    printtext+="<b><h3>${groupAndPage.entrySet()[i].getKey()}: </b></h3>"
    printtext+=groupAndPage.entrySet()[i].getValue()+"<br>"
}
printtext += "<br><br>********************************<br>"
 
 
//printtext += "<br><h2>Pages that have permissions </h2></b><br>"
//for(int p=0;p<pagesInConfluenceSet.size();p++)
//{
//    printtext+= "<br>"+pagesInConfluenceSet[p]
//}
//printtext += "<br><br>********************************<br>"
 
 
return printtext
