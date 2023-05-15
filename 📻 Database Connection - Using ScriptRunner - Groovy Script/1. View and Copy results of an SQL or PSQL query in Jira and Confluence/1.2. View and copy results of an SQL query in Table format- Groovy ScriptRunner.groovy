////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Created - Dec 09, 2022 14:06
//Last Modified - Apr 24, 2023 21:22
//Author - Rinaldi Michael
//References -
//https://library.adaptavist.com/entity/display-sql-macro
//https://www.w3schools.com/tags/tag_table.asp
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
 
 
     
/////////////////////////////////////////////////////////////////////////////
  
def results
DatabaseUtil.withSql(resourceName)
{ sql ->
    results = sql.rows(sqlQuery)
}
  
/////////////////////////////////////////////////////////////////////////////
     
String printtextHeader="<html> <head> <style> table,th,td{border: 1px solid black;border-collapse: collapse;}     table.center {margin-left: auto;margin-right: auto;}    th,td{padding:10px;} </style> </head>"
printtextHeader+="<body><table>"
def columnsHeader = results[0].keySet()
     
//header
printtextHeader+="<tr style=\"background-color:#ff0800;color:#ffee00\">"
for(int h=0;h<columnsHeader.size();h++)
{
    printtextHeader+="<th>${columnsHeader[h]}</th>"
}
printtextHeader+="</tr>"
     
/////////////////////////////////////////////////////////////////////////////
   
//values array
int sizeOfValues = results.size()
String[] printtextValues = new String[sizeOfValues];
int valueCount=0
    
for(int r=0;r<results.size();r++)
{
    printtextValues[valueCount]="<tr>"
    for(int v=0;v<columnsHeader.size();v++)
    {
        printtextValues[valueCount]+="<td>${results[r].values()[v].toString()}</td>"
    }
printtextValues[valueCount]+="</tr>"
valueCount++
}
   
//values output
String outputValues=""
for(int o=0;o<valueCount;o++)
{
    outputValues+=printtextValues[o]
}
outputValues+="</body></html>"
/////////////////////////////////////////////////////////////////////////////
   
//display the output
return printtextHeader+outputValues
