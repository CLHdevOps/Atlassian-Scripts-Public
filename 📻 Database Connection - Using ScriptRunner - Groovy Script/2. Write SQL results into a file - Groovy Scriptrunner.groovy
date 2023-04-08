////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Created - 24th Jan 2023, 12:03 pm
//Last Modified - 6th April 2023, 06:08 pm
//Author - Rinaldi Michael
//References -
//https://library.adaptavist.com/entity/display-sql-macro
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   
import java.io.*
import java.util.*
import com.onresolve.scriptrunner.db.DatabaseUtil
import com.onresolve.scriptrunner.parameters.annotation.*
import com.onresolve.scriptrunner.parameters.annotation.meta.*
    
@ShortTextInput(label = "Resource Name", description = "Type in the name of the Local Database resource set up in ScriptRunner's Reources")
String resourceName
   
@ShortTextInput(label = "SQL Query", description = "Type in the SQL query without the semicolon ; at the end")
String sqlQuery
   
@ShortTextInput(label = "Delimiter", description = "Enter the character that you would like to separate each value with")
String delimiter
   
@ShortTextInput(label = "Script Editor File name", description = "Enter the name of the file. This will be stored in ScriptRunner's Script Editor")
String fileName
  
@Select(
    label = "Application",
    description = "Select application as the server's path name is different for both",
    options = [
        @Option(label = "Jira", value = "jira"),
        @Option(label = "Confluence", value = "confluence"),
    ]
)
String application
 
/////////////////////////////////////////////////////////////////////////////
   
def results
DatabaseUtil.withSql(resourceName)
{ sql ->
    results = sql.rows(sqlQuery)
}
   
/////////////////////////////////////////////////////////////////////////////
 
String printtextHeader=""
def columnsHeader = results[0].keySet()
   
//header
for(int h=0;h<columnsHeader.size();h++)
{
    printtextHeader+=columnsHeader[h]+delimiter
}
printtextHeader+="\n"
   
/////////////////////////////////////////////////////////////////////////////
 
//values array
int sizeOfValues = results.size()
String[] printtextValues = new String[sizeOfValues];
int valueCount=0
   
for(int r=0;r<results.size();r++)
{
    printtextValues[valueCount]=""
    for(int v=0;v<columnsHeader.size();v++)
    {
        printtextValues[valueCount]+=results[r].values()[v].toString()+delimiter
    }
printtextValues[valueCount]+="\n"
valueCount++
}
  
  
  
//values output
String outputValues=""
for(int o=0;o<valueCount;o++)
{
    outputValues+=printtextValues[o]
}
 
 
/////////////////////////////////////////////////////////////////////////////
 
   
//Write into file
FileWriter resultFile = new FileWriter("/home/${application}/shared_home/scripts/${fileName}.groovy")
resultFile.write(printtextHeader+outputValues)
resultFile.close()
   
return "The results of the SQL query -><b> ${sqlQuery}</b> has been written into <b>/home/jira/shared_home/scripts/${fileName}.groovy</b>"
