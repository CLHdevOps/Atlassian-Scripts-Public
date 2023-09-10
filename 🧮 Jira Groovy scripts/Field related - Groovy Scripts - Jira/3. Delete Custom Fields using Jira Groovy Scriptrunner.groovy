//Author: Rinaldi Michael
//last Modified: 21st Nov 2022, 07:42 PM
//Reference: https://mraddon.blog/2018/11/12/how-to-remove-jira-customfields-in-bulk-with-a-groovy-script/
   
import java.sql.Timestamp
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.*
import com.onresolve.scriptrunner.parameters.annotation.*
import com.atlassian.jira.issue.fields.CustomField
 
def loggedInUser = ComponentAccessor.jiraAuthenticationContext.loggedInUser
 
@CustomFieldPicker(description = 'Select the custom fields by providing the name/s or ID/s', label = 'Select Custom Fields', multiple = true, placeholder = 'Select custom field')
List<CustomField> customFields
  
//for pre-defined custom field input
//def customField1 = customFieldManager.getCustomFieldObjectsByNameIgnoreCase("CustomFieldName1")
//def customField2 = customFieldManager.getCustomFieldObjectsByNameIgnoreCase("CustomFieldName2")
  
CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
 
for(int c=0;c<customFields.size();)
{
    customFieldManager.removeCustomField(customFields[c])
    c++
}
