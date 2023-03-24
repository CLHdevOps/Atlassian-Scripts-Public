////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Created - 24th Jan 2023, 12:03 pm
//Last Modified - 21st Mar 2023, 04:14 pm
//Author - Rinaldi Michael
//References -
//https://library.adaptavist.com/entity/display-sql-macro
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Results appear the same way a CSV file does. Copy the results into an Excel/Google Sheet and Split Text into Columns using the delimiter provided.
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


import java.io.*
import java.util.*
import com.onresolve.scriptrunner.db.DatabaseUtil
import com.onresolve.scriptrunner.parameters.annotation.*
  
@ShortTextInput(label = "Resource Name", description = "Type in the name of the Local Database resource set up in ScriptRunner's Reources")
String resourceName
 
@ShortTextInput(label = "SQL Query", description = "Type in the SQL query without the semicolon ; at the end")
String sqlQuery
 
@ShortTextInput(label = "Delimiter", description = "Enter the character that you would like to separate each value with")
String delimiter
 
@ShortTextInput(label = "Script Editor File name", description = "Enter the name of the file. This will be stored in ScriptRunner's Script Editor")
String fileName
 
def results
DatabaseUtil.withSql(resourceName)
{ sql ->
    results = sql.rows(sqlQuery)
}
 
String printtext=""
def columnsHeader = results[0].keySet()
 
//header
for(int h=0;h<columnsHeader.size();h++)
{
    printtext+=columnsHeader[h]+delimiter
}
printtext+="\n"
 
 
//values
for(int r=0;r<results.size();r++)
{
    for(int v=0;v<columnsHeader.size();v++)
    {
        printtext+=results[r].values()[v].toString()+delimiter
    }
printtext+="\n"
}
 
//Write into file
FileWriter resultFile = new FileWriter("/home/jira/shared_home/scripts/${fileName}.groovy")
resultFile.write(printtext)
resultFile.close()
 
return "The results of the SQL query -><b> ${sqlQuery}</b> has been written into <b>/home/jira/shared_home/scripts/${fileName}.groovy</b>"
