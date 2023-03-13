////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Author: Rinaldi Michael
//Created: 6th March 2023, 11:32 am
//Last Modified: 6th March 2023, 05:32 pm
//Reference:
//https://community.developer.atlassian.com/t/search-for-space-by-title-using-cql/47112/2
//https://docs.atlassian.com/atlassian-confluence/6.3.0/com/atlassian/confluence/api/service/search/CQLSearchService.html
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Functionalities
//Provide the CQL query in the input field
//Provide the number of results (this information can be fetched in the CQL query page or a random big integer like 100000 can be input)
//Ignore duplicate values using the set data type
//Provide the group/s
//Provide the permissions required for the Spaces
//CQL must contain "type = space" for the script to work
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

 
import java.io.*
import java.lang.*
import java.util.*
import com.onresolve.scriptrunner.canned.confluence.utils.CQLSearchUtils
import com.onresolve.scriptrunner.canned.confluence.utils.CQLSearch
import com.onresolve.scriptrunner.runner.ScriptRunnerImpl
import com.atlassian.confluence.api.service.search.*
import com.atlassian.sal.api.component.ComponentLocator
import com.atlassian.confluence.api.model.pagination.SimplePageRequest
import com.atlassian.confluence.api.model.search.SearchOptions
import com.atlassian.confluence.api.model.Expansion
import com.onresolve.scriptrunner.parameters.annotation.*
import com.atlassian.user.GroupManager
import com.atlassian.confluence.security.PermissionManager
import com.atlassian.confluence.security.login.LoginManager
import com.atlassian.confluence.user.UserAccessor
import com.atlassian.user.UserManager
import com.atlassian.confluence.rpc.soap.services.SpacesSoapService
import com.atlassian.crowd.embedded.api.Group
import com.onresolve.scriptrunner.parameters.annotation.meta.*
import com.atlassian.confluence.spaces.*
  
@ShortTextInput(label = "Enter the CQL", description = "Type in the CQL query to fetch all the spaces that need their permissions to be added.")
String cqlQuery
 
@NumberInput(label = 'Enter max results', description = 'Type in the maximum number of results expected from the CQL query. For testing, it helps to try this out with any value less than 5 <br><br>----------------------------------')
Integer maxResults
 
@GroupPicker(label = 'Groups', description = 'Select the group/s', placeholder = 'Pick group/s', multiple = true)
List<Group> groups
 
@Select(
    label = "Space Permissions (Multi Picker)",
    description = "<br>Default Permissions - EDITSPACE,VIEWSPACE,REMOVEOWNCONTENT,CREATEATTACHMENT,COMMENT <br><br>VIEWSPACE - View pages in the Space<br>REMOVEOWNCONTENT - Delete users own content in the Space<br><br>EDITSPACE - Add and/or Edit pages in the Space<br>REMOVEPAGE - Delete pages in the Space <br><br>EDITBLOG - Add and/or Edit blogs in the Space<br>REMOVEBLOG - Delete Blogs in the Space<br><br>CREATEATTACHMENT - Add attachments to pages in the Space<br>REMOVEATTACHMENT - Delete attachments from pages in the Space<br><br>COMMENT - Add comments to pages in the Space<br>REMOVECOMMENT - Remove comments from pages in the Space<br><br>SETPAGEPERMISSIONS - Add or delete restrictions in pages in the Space<br><br>REMOVEMAIL - Mail is a legacy feature in Confluence Cloud that is in the process of being removed. Changes to Mail permissions won’t have any effect on product functionality or access.<br><br>EXPORTSPACE - Have permission to export the whole Space in Space Tools<br>SETSPACEPERMISSIONS - Have Admin permissions to the whole Space",
    options = [
        @Option(label = "Default Permissions", value = "default"),
        @Option(label = "View pages in the Space", value = "VIEWSPACE"),
        @Option(label = "Delete users own content in the Space", value = "REMOVEOWNCONTENT"),
        @Option(label = "Add and/or Edit pages in the Space", value ="EDITSPACE"),
        @Option(label = "Delete pages in the Space", value ="REMOVEPAGE"),
        @Option(label = "Add and/or Edit blogs in the Space", value ="EDITBLOG"),
        @Option(label = "Delete Blogs in the Space", value ="REMOVEBLOG"),
        @Option(label = "Add attachments to pages in the Space", value ="CREATEATTACHMENT"),
        @Option(label = "Delete attachments from pages in the Space", value ="REMOVEATTACHMENT"),
        @Option(label = "Add comments to pages in the Space", value ="COMMENT"),
        @Option(label = "Remove comments from pages in the Space", value ="REMOVECOMMENT"),
        @Option(label = "Add or delete restrictions in pages in the Space", value ="SETPAGEPERMISSIONS"),
        @Option(label = "Mail is a legacy feature in Confluence Cloud that is in the process of being removed. Changes to Mail permissions won’t have any effect on product functionality or access.", value ="REMOVEMAIL"),
        @Option(label = "Have permission to export the whole Space in Space Tools", value ="EXPORTSPACE"),
        @Option(label = "Have Admin permissions to the whole Space", value ="SETSPACEPERMISSIONS")
    ],
    multiple = true
)
List<String> permissionsInput
   
if(permissionsInput!=null)
{
    for(int p=0;p<permissionsInput.size();)
    {
        if(permissionsInput[p]=="default")
        {
            permissionsInput.remove(p)
            permissionsInput.addAll(["EDITSPACE","VIEWSPACE","REMOVEOWNCONTENT","CREATEATTACHMENT","COMMENT"])
        }
        p++
    }
}
      
String[] permissionsArray = new String[permissionsInput.size()]
      
if(permissionsInput!=null)
{
    for(int p=0;p<permissionsInput.size();)
    {
        permissionsArray[p]=permissionsInput[p]
        p++
    }
}
 
def cqlSearchUtils = ScriptRunnerImpl.scriptRunner.getBean(CQLSearchUtils)
def cqlSearchService = ScriptRunnerImpl.getOsgiService(CQLSearchService)
def searchOptions = ComponentLocator.getComponent(SearchOptions)
def cqlSearch = new CQLSearch()
def pageRequest = new SimplePageRequest(0, maxResults)
def userAccessor = ComponentLocator.getComponent(UserAccessor)
def groupManager = ComponentLocator.getComponent(GroupManager)
def loginManager = ComponentLocator.getComponent(LoginManager)
def userManager = ComponentLocator.getComponent(UserManager)
def permissionManager = ComponentLocator.getComponent(PermissionManager)
def spacesSoapService = ComponentLocator.getComponent(SpacesSoapService)
def spaceManager = ComponentLocator.getComponent(SpaceManager)
String printtext = ""
 
  
def cqlResultvariable = cqlSearchService.search(cqlQuery,SearchOptions.builder().build(),pageRequest,Expansion.combine("space")).getResults()
Set<String> setOfresults = new HashSet<String>();
 
//return cqlResultvariable[0].getEntity().name
  
//a set does not allow duplicate values. Hence the following loop
for(int c=0;c<cqlResultvariable.size();c++)
{
    setOfresults.add(cqlResultvariable[c].getEntity())
}
 
 
//add group/s to Space permissions
for(int g=0;g<groups.size();g++)
{
    printtext =  printtext + "<h2><b>Group ${g+1} -> ${groups[g].getName()}</h2></b>"
    for(int s=0;s<setOfresults.size();s++)
    {
        def spaceKey
        try
        {
            spaceKey = setOfresults[s].key
            spacesSoapService.addPermissionsToSpace(permissionsArray, groups[g].getName(), spaceKey)
            printtext = printtext + "<br>${s+1}. Group - <b>${groups[g].getName()}</b> is added to the Space: Name- <b>${setOfresults[s].name}</b>, Key- <b>${setOfresults[s].key}</b> with <b>${permissionsInput}</b> permissions"
        }
        catch(Exception ex)
        {
            printtext=printtext+"<br>${s+1}. Group - <b>${groups[g].getName()}</b> is <b>NOT</b> added to the Space: Name- <b>${setOfresults[s].name}</b>, Key- <b>${setOfresults[s].key}</b> with <b>${permissionsInput}</b> permissions. Reasons could be no permission to edit space."
        }
    }
    printtext+="<h1><b>************************************************************************************</b><br></h1>"
}
 
  
return printtext
