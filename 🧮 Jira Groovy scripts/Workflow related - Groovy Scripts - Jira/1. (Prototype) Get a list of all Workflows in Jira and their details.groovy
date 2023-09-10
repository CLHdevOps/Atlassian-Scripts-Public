////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Created - 11th May 2023, 11:30 am
//Last Modified - 17th May 2023, 06:53pm
//Authors - Rinaldi Michael
//References -
//https://global-confluence.randstadservices.com/pages/viewpage.action?pageId=6344051247
//https://docs.atlassian.com/software/jira/docs/api/7.1.9/com/atlassian/jira/workflow/WorkflowSchemeManager.html
//Details
//Name, Active/Inactive, Description, Schemes, Statuses
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
import java.io.*
import java.util.*
import java.lang.*
import com.atlassian.jira.workflow.*
import com.atlassian.jira.component.ComponentAccessor
    
def workflowManager = ComponentAccessor.getComponent(WorkflowManager)
def allWorkflowsInJira = workflowManager.getWorkflows()
def workflowSchemeManager = ComponentAccessor.getComponent(WorkflowSchemeManager)
    
String printtext="<html> <head>"
//Style and Header end
printtext+= "<style> table,th,td{border: 1px solid black;border-collapse: collapse;}     table.center {margin-left: auto;margin-right: auto;}    th,td{padding:10px;} </style> </head>"
     
//body start
printtext+="<body>"
    
//table start and header
printtext+="<table>"
printtext+="<tr style=\"background-color:#ff0800;color:#ffee00\"><th>S.No.</th><th>Workflow Name</th>"
printtext+="<th>Active/Inactive</th><th>Workflow Description</th><th>Workflow diagram</th><th>Last Updated</th><th>Workflow Schemes</th>"
printtext+="<th>Workflow statuses</th><th>Projects using the Workflow</th></tr>"
    
for(int w=0;w<allWorkflowsInJira.size();w++)
{
    printtext+="<tr><td>${w+1}</td><td>${allWorkflowsInJira[w].getName()}</td><td>${allWorkflowsInJira[w].isActive()}</td>"
    printtext+="<td>${allWorkflowsInJira[w].getDescription()}</td><td></td>"
        
    printtext+="<td>${allWorkflowsInJira[w].getUpdatedDate()}</td>"
 
    //workflow Schemes
    def workflowSchemes = workflowSchemeManager.getSchemesForWorkflow(allWorkflowsInJira[w])
    printtext+="<td>"
    for(int ws=0;ws<workflowSchemes.size();ws++)
    {
        printtext+="${workflowSchemes[ws].name}<br><br>"
    }
    printtext+="</td>"
    
    //workflow statuses
    def workflowStatuses = allWorkflowsInJira[w].getLinkedStatusObjects()
    printtext+="<td>"
    for(int s=0;s<workflowStatuses.size();s++)
    {
        printtext+="${workflowStatuses[s].getName()}<br>"
    }
    printtext+="</td>"
    
    //projects used in the workflow
        
    printtext+="<td>"
    for(int ws=0;ws<workflowSchemes.size();ws++)
    {
        def workflowSchemeName = workflowSchemes[ws].name
        def assignableWorkflowScheme = workflowSchemeManager.getWorkflowSchemeObj(workflowSchemeName.toString())
        def workflowProjects = workflowSchemeManager.getProjectsUsing(assignableWorkflowScheme)
        for(int p=0;p<workflowProjects.size();p++)
        {
            printtext+="${workflowProjects[p].getName()}, ${workflowProjects[p].getKey()}<br><br>"
        }
    }
    printtext+="</td>"
    
    /*workflow post functions (TO WORK ON)
    def workflowTransitions = allWorkflowsInJira[w].getAllActions()
    printtext+="<td>"
    for(int t=0;t<workflowTransitions.size();t++)
    {
        def transitionPostFunctions = workflowTransitions[t].getPostFunctions()
        def transitionConditions = workflowTransitions[t].getConditionalResults()
        def transitionValidators = workflowTransitions[t].getValidators()
        def transitionPreFunctions = workflowTransitions[t].getPreFunctions()
        //printtext+=transitionPostFunctions+"<br>"+transitionConditions+"<br>"
        //printtext+=transitionValidators+"<br>"+transitionPreFunctions
    }
    printtext+="</td>"
    */
    
    printtext+="</tr>"
}
    
//end table
printtext+="</table>"
    
//body end
printtext+="</body>"
     
//html end
printtext+="</html>"
    
return printtext
