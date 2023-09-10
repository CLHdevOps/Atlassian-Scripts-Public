//Author: Rinaldi Michael
//Last Modified: 6:39 pm
//Reference: https://community.atlassian.com/t5/Jira-Software-questions/Scriptrunner-groovy-trying-to-use-SwimlaneService-and-Swimlane/qaq-p/2086660#U2208439
//Experimental - Does not do anything much yet.
 
 
import com.onresolve.scriptrunner.runner.customisers.JiraAgileBean
import com.atlassian.jira.user.ApplicationUser
import com.onresolve.scriptrunner.parameters.annotation.*
import com.atlassian.greenhopper.service.rapid.view.RapidViewService
import com.atlassian.greenhopper.service.rapid.view.QuickFilterService
import com.onresolve.scriptrunner.runner.customisers.WithPlugin
import com.atlassian.jira.component.ComponentAccessor
@WithPlugin("com.pyxis.greenhopper.jira")
 
def authenticationContext = ComponentAccessor.jiraAuthenticationContext
def applicationUserInput = authenticationContext.loggedInUser
 
@JiraAgileBean
RapidViewService rapidViewService
 
@JiraAgileBean
QuickFilterService quickFilterService
 
def rapidViews = rapidViewService.getRapidView(applicationUserInput,66).get()
def rapidViewsIn66 = quickFilterService.loadQuickFilters(rapidViews)
def quickFiltersIn66 = rapidViewsIn66.query
 
return quickFiltersIn66
 
 
def rapidViewsAll = rapidViewService.getRapidViews(applicationUserInput).get()
def rapidViewsInJira = quickFilterService.loadQuickFilters(rapidViewsAll[3])
def quickFiltersInJira = rapidViewsInJira.query
