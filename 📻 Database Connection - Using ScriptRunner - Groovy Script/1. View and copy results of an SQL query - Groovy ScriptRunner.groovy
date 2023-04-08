////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Created - Dec 09, 2022 14:06
//Last Modified - Apr 06, 2023 18:00
//Author - Rinaldi Michael
//References -
//https://library.adaptavist.com/entity/display-sql-macro
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
import java.io.*
import java.util.*
import java.lang.*
import com.onresolve.scriptrunner.db.DatabaseUtil
import com.onresolve.scriptrunner.parameters.annotation.*
     
@ShortTextInput(label = "Resource Name", description = "Type in the name of the Local Database resource set up in ScriptRunner's Reources")
String resourceName
    
@ShortTextInput(label = "SQL Query", description = "Type in the SQL query without the semicolon ; at the end")
String sqlQuery
    
@ShortTextInput(label = "Delimiter", description = "Enter the character that you would like to separate each value with")
String delimiter
    
/////////////////////////////////////////////////////////////////////////////
 
def results
DatabaseUtil.withSql(resourceName)
{ sql ->
    results = sql.rows(sqlQuery)
}
 
/////////////////////////////////////////////////////////////////////////////
    
String printtextHeader=""
def columnsHeader = results[0].keySet()
//return columns[0]
//return results[0].values()[1]
    
//header
for(int h=0;h<columnsHeader.size();h++)
{
    printtextHeader+=columnsHeader[h]+delimiter
}
printtextHeader+="<br>"
    
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
printtextValues[valueCount]+="<br>"
valueCount++
}
  
//values output
String outputValues=""
for(int o=0;o<valueCount;o++)
{
    outputValues+=printtextValues[o]
}
 
/////////////////////////////////////////////////////////////////////////////
  
//display the output
return printtextHeader+outputValues
