////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Created - 6th March 2023, 8:00 pm
//Last Modified - 6th March 2023, 8:00 pm
//Authors - Rinaldi Michael=
//References -
//https://docs.atlassian.com/software/jira/docs/api/7.2.0/com/atlassian/jira/config/StatusManager.html
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 
import com.atlassian.jira.config.*
import com.atlassian.jira.component.ComponentAccessor
 
def statusManager = ComponentAccessor.getComponent(StatusManager)
 
def statusesList = statusManager.getStatuses()
 
String printtext =""
for(int s=0;s<statusesList.size();s++)
{
    printtext+="${s+1}. <b>${statusesList[s].name} </b>"+statusesList[s]+"<br>"
}
 
return printtext
