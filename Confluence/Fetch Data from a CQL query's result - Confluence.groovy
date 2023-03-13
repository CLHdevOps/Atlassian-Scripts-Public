////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Author: Rinaldi Michael
//Created: 5th March 2023, 8:30 pm
//Last Modified: 6th March 2023, 11:32 am
//Reference:
//https://community.developer.atlassian.com/t/search-for-space-by-title-using-cql/47112/2
//https://docs.atlassian.com/atlassian-confluence/6.3.0/com/atlassian/confluence/api/service/search/CQLSearchService.html
//https://www.geeksforgeeks.org/set-in-java/
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Functionalities
//Provide the CQL query in the input field
//Provide the number of results (this information can be fetched in the CQL query page or a random big integer like 100000 can be input)
//Ignore duplicate values using the set data type
//*the below example fetches the title column of a CQL query's results at setOfresults.add(cqlResultvariable[c].getTitle())
//** getTitle() can be modified to fetch other relevant data
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


  
import com.onresolve.scriptrunner.canned.confluence.utils.CQLSearchUtils
import com.onresolve.scriptrunner.canned.confluence.utils.CQLSearch
import com.onresolve.scriptrunner.runner.ScriptRunnerImpl
import java.io.*
import java.lang.*
import java.util.*
import com.atlassian.confluence.api.service.search.*
import com.atlassian.sal.api.component.ComponentLocator
import com.atlassian.confluence.api.model.pagination.SimplePageRequest
import com.atlassian.confluence.api.model.search.SearchOptions
import com.atlassian.confluence.api.model.Expansion
import com.onresolve.scriptrunner.parameters.annotation.*
  
@ShortTextInput(label = "Enter the CQL", description = "Type in the CQL query ")
String cqlQuery
 
@NumberInput(label = 'Enter max results', description = 'Type in the maximum number of results expected from the CQL query')
Integer maxResults
 
def cqlSearchUtils = ScriptRunnerImpl.scriptRunner.getBean(CQLSearchUtils)
def cqlSearchService = ScriptRunnerImpl.getOsgiService(CQLSearchService)
def searchOptions = ComponentLocator.getComponent(SearchOptions)
def cqlSearch = new CQLSearch()
def pageRequest = new SimplePageRequest(0, maxResults)
  
def cqlResultvariable = cqlSearchService.search(cqlQuery,SearchOptions.builder().build(),pageRequest,Expansion.combine("space")).getResults()
Set<String> setOfresults = new HashSet<String>();
  
//a set does not allow duplicate values. Hence the following loop
for(int c=0;c<cqlResultvariable.size();c++)
{
    setOfresults.add(cqlResultvariable[c].getTitle())
}
 
//format the output
String printtext = ""
for(int s=0;s<setOfresults.size();s++)
{
    printtext+="${s+1}. ${setOfresults[s]}<br>"
}
 
  
return printtext
