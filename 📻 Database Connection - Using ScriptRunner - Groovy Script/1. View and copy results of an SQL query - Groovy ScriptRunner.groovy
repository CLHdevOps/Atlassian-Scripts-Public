///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Created - Dec 09, 2022 14:06
//Last Modified - Mar 21, 2023 15:51
//Author - Rinaldi Michael
//References -
//https://library.adaptavist.com/entity/display-sql-macro
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Results appear the same way a CSV file does. Copy the results into an Excel/Google Sheet and Split Text into Columns using the delimiter provided
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 
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
 
def results
DatabaseUtil.withSql(resourceName)
{ sql ->
    results = sql.rows(sqlQuery)
}
 
String printtext=""
def columnsHeader = results[0].keySet()
//return columns[0]
//return results[0].values()[1]
 
//header
for(int h=0;h<columnsHeader.size();h++)
{
    printtext+=columnsHeader[h]+delimiter
}
printtext+="<br>"
 
//values
for(int r=0;r<results.size();r++)
{
    for(int v=0;v<columnsHeader.size();v++)
    {
        printtext+=results[r].values()[v].toString()+delimiter
    }
printtext+="<br>"
}
 
return printtext
