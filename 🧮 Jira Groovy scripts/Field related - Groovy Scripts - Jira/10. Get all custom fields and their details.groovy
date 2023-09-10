////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Created - 21st July 2023, 02:45 pm
//Last Modified - 21st July 2023, 04:24 pm
//Authors - Rinaldi Michael
//References:
//https://coderanch.com/t/426877/java/remove-carriage-return-string
/*
Results are written into Script Editor:
The results will be written into a new file that will be created by the script in ScriptRunner's Script Editor.  The delimiter can be set by you.

These results can be copied using CTRL+A into a Google sheet and Split into columns (in-built tool) with 
*/
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  
import java.lang.String
import com.atlassian.jira.issue.fields.CustomField
import com.onresolve.scriptrunner.parameters.annotation.meta.*
import com.onresolve.scriptrunner.parameters.annotation.*
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.config.managedconfiguration.ManagedConfigurationItemService
import com.atlassian.jira.issue.CustomFieldManager
  
  
//Declare managers
ManagedConfigurationItemService managedConfigurationItemService = ComponentAccessor.getComponent(ManagedConfigurationItemService)
CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()
  
//Get input for file to write results into
@ShortTextInput(description = 'Enter the Script Editor file you want to read. Exclude .groovy', label = 'Enter File name')
String fileName
  
@ShortTextInput(description = 'Enter the character you would like to separate your values with like in a CSV file', label = 'Enter the delimiter')
String delimiter
  
//Get all custom fields
def allCustomFields = customFieldManager.getCustomFieldObjects()
String printtext = "Name${delimiter}Description${delimiter}Type${delimiter}Field Name${delimiter}Last Updated${delimiter}Associate Project Objects${delimiter}Associated Issue Types${delimiter}All Projects${delimiter}All Issue Types\n"
 
 
//Loop through all custom fields and get details
for(int c=0;c<allCustomFields.size();c++)
{
    printtext+=allCustomFields[c].name+delimiter
    printtext+=allCustomFields[c].description.toString().replaceAll('[\\r\\n]',' ')+delimiter
    printtext+=allCustomFields[c].customFieldType.getName()+delimiter
    printtext+=allCustomFields[c].fieldName+delimiter
    printtext+=allCustomFields[c].getLastValueUpdate().toString()+delimiter
    printtext+=allCustomFields[c].associatedProjectObjects.toString()+delimiter
    printtext+=allCustomFields[c].associatedIssueTypes.toString()+delimiter
    printtext+=allCustomFields[c].allProjects.toString()+delimiter
    printtext+=allCustomFields[c].allIssueTypes.toString()+"\n"
}
  
  
new File("/home/jira/shared_home/scripts/${fileName}.groovy").withWriter('utf-8')
{
    writer -> writer.writeLine printtext
}
   
return "The contents are written into <b>/home/jira/shared_home/scripts/${fileName}</b> in the Script Editor."
