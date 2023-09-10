//Source: https://confluence.atlassian.com/jirakb/unlock-a-locked-jira-custom-field-using-scriptrunner-1167828785.html
//Modified on: 1st July 2023, 03:27pm
 
import com.atlassian.jira.issue.fields.CustomField
import com.onresolve.scriptrunner.parameters.annotation.CustomFieldPicker
import com.onresolve.scriptrunner.parameters.annotation.meta.*
import com.onresolve.scriptrunner.parameters.annotation.*
     
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.config.managedconfiguration.ManagedConfigurationItemService
import com.atlassian.jira.issue.CustomFieldManager
 
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.config.managedconfiguration.ConfigurationItemAccessLevel
import com.atlassian.jira.config.managedconfiguration.ManagedConfigurationItemService
import com.atlassian.jira.issue.CustomFieldManager
 
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Get input
  
@CustomFieldPicker(label = 'Custom Field', description = 'Pick a custom field', placeholder='Select custom field')
CustomField customField
 
String customFieldName = customField.getName()
 
 
@Select(
    label = "Choose to lock or unlock",
    description = "Choose",
    options = [
        @Option(label = "Lock", value = "lock"),
        @Option(label = "Unlock", value = "unlock"),
    ]
)
String choice
 
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Choice one to unlock- Script used with minimal modifications to the original
 
if(choice=="unlock")
{
 
    ManagedConfigurationItemService managedConfigurationItemService = ComponentAccessor.getComponent(ManagedConfigurationItemService)
    CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()
 
    def cf = customFieldManager.getCustomFieldObjectByName(customFieldName)
 
    if (cf)
    {
        def mci = managedConfigurationItemService.getManagedCustomField(cf)
        if (mci)
        {
            managedConfigurationItemService.removeManagedConfigurationItem(mci)
        }
    }
}
 
 
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Choice one to lock- Script used with minimal modifications to the original
 
else if(choice=="lock")
{
    ManagedConfigurationItemService managedConfigurationItemService = ComponentAccessor.getComponent(ManagedConfigurationItemService)
    CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()
 
    def cf = customFieldManager.getCustomFieldObjectByName(customFieldName)
 
    if (cf)
    {
        def mci = managedConfigurationItemService.getManagedCustomField(cf)
        if (mci)
        {
            def managedConfigurationItemBuilder = mci.newBuilder();
            def updatedMci = managedConfigurationItemBuilder.setManaged(true).setConfigurationItemAccessLevel(ConfigurationItemAccessLevel.LOCKED).build();
            managedConfigurationItemService.updateManagedConfigurationItem(updatedMci);
        }
    }
}
