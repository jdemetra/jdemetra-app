' cscript /nologo search.vbs
                
On Error Resume Next

If Wscript.Arguments.Count <> 1 Then
  Wscript.Echo "usage ..."
  Wscript.quit(0)
End If 

query = Wscript.Arguments.Item(0)

Set conn = CreateObject("ADODB.Connection")
conn.Open "Provider=Search.CollatorDSO;Extended Properties='Application=Windows';"

If Err.Number <> 0 Then
  Wscript.Echo "Connection failed: [" &  Err.Number & "] " & Err.Description
  Wscript.quit(1)
End If

Set rs = CreateObject("ADODB.Recordset")
rs.Open "SELECT System.ItemUrl FROM SYSTEMINDEX WHERE System.FileName like '%" & query & "%'", conn

If Err.Number <> 0 Then
  Wscript.Echo "Query failed: [" &  Err.Number & "] " & Err.Description
  conn.Close
  Wscript.quit(2)
End If

If Not (rs.BOF and rs.EOF) Then
  rs.MoveFirst
  Do Until rs.EOF
    Wscript.Echo Replace(rs.Fields.Item(0), "file:", "")
    rs.MoveNext
  Loop 
End If

rs.Close
conn.Close
