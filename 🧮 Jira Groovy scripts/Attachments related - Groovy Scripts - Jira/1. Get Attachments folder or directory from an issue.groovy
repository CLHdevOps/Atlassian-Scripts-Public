//Author: Rinaldi Michael
//last Modified: 1st Dec 2022, 09:33 pm
//Reference: https://community.atlassian.com/t5/Jira-Software-questions/Scriptrunner-Set-Clear-Resolutions-Globally/qaq-p/2068841
 
import java.lang.String  
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.*
import com.onresolve.scriptrunner.parameters.annotation.*
import com.onresolve.scriptrunner.parameters.annotation.ShortTextInput
import com.atlassian.jira.issue.AttachmentManager
import com.atlassian.jira.issue.attachment.*
import com.atlassian.jira.issue.attachment.FileSystemAttachmentDirectoryAccessor
 
 
 
def issueManager = ComponentAccessor.issueManager
def projectManager = ComponentAccessor.getProjectManager()
def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
def attachmentManager = ComponentAccessor.getAttachmentManager();
def attachmentDirectoryAccessor = ComponentAccessor.getComponent(FileSystemAttachmentDirectoryAccessor)
def temporaryAttachmentDirectory = attachmentDirectoryAccessor.getTemporaryAttachmentDirectory()
//def sas = ComponentAccessor.getComponent(FileSystemAttachmentStore)
  
def issueService = ComponentAccessor.issueService
def loggedInUser = ComponentAccessor.jiraAuthenticationContext.loggedInUser
     
//for specific issues
@ShortTextInput(description = 'Issue you want to move', label = 'Provide Issue Key')
String issueKeyInput
  
//@ShortTextInput(description = 'Project you want to move it to', label = 'Provide the Project Key')
//String projectKeyInput
 
//@ShortTextInput(description = 'Issue which contains the attachment.', label = 'Provide Issue Key')
//String NewissueKeyInput
  
def issueKeys = issueManager.getIssueByCurrentKey(issueKeyInput)
//def newissueKeys = issueManager.getIssueByCurrentKey(NewissueKeyInput)
 
//def projectKey = projectManager.getProjectObjByKey(projectKeyInput)
def oldIssueKey = issueKeys
def oldAttachments = attachmentManager.getAttachments(issueKeys)
File oldAttachmentFile = attachmentDirectoryAccessor.getAttachmentDirectory(issueKeys)
 
 
 
return oldAttachmentFile
