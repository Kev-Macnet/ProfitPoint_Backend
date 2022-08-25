/**
 * Created on 2021/2/19.
 */
package tw.com.leadtek.nhiwidget.db;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import tw.com.leadtek.tools.GenClassFieldXMLTag;

public class GenerateDocumentFromDB {

  public final static int HANA = 1;

  public final static int MYSQL = 2;

  public final static int SQL_SERVER = 3;

  private Connection connection;

  private int dbType;

  private String schema;

  private BufferedWriter bw;

  public GenerateDocumentFromDB(int dbType, String schema) {
    this.dbType = dbType;
    this.schema = schema;
  }

  public void connect(String IP, int port, String username, String password) {
    if (dbType == HANA) {
      try { // encrypt and validateCertificate should be true for HANA Cloud connections
        connection = DriverManager.getConnection(String.format("jdbc:sap://%s:%d", IP, port),
            username, password);
        System.out.println("connected !");
      } catch (SQLException e) {
        System.err.println("Connection Failed:");
        System.err.println(e);
        return;
      }
    } else if (dbType == MYSQL) {
      try { // encrypt and validateCertificate should be true for HANA Cloud connections
        connection = DriverManager.getConnection(String.format("jdbc:mariadb://%s:%d/%s", IP, port, schema),
            username, password);
        System.out.println("connected !");
      } catch (SQLException e) {
        System.err.println("Connection Failed:");
        System.err.println(e);
        return;
      }
    }
  }

  public void gen(String outputFile) {
    try {
      bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "BIG5"));
      HashMap<String, String> tables = getAllTable(schema);
      for (String table : tables.keySet()) {
        writeTableName(table, tables.get(table), bw);
        String ddl = getTableDDL(table);
        convertDDLToDocument(table, ddl, bw);
        bw.newLine();
      }
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * 
   * @param schema
   * @return <Table_Name, Comment>
   */
  public HashMap<String, String> getAllTable(String schema) {
    HashMap<String, String> result = new HashMap<String, String>();
    try {
      PreparedStatement ps =
          connection.prepareStatement("select * from tables WHERE schema_name=?");
      ps.setString(1, schema);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        if (rs.getString("COMMENTS") != null && rs.getString("COMMENTS").length() > 0) {
          result.put(rs.getString("TABLE_NAME"), rs.getString("COMMENTS"));
        } else {
          result.put(rs.getString("TABLE_NAME"), "");
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return result;
  }

  public String getTableDDL(String tableName) {
    String ddl = null;
    try {
      PreparedStatement ps = connection.prepareStatement("CALL get_object_definition (?, ?)");
      ps.setString(1, schema);
      ps.setString(2, tableName);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        ddl = rs.getString("OBJECT_CREATION_STATEMENT");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return ddl;
  }

  public List<GenClassFieldXMLTag> convertDDLToDocument(String tableName, String ddl,
      BufferedWriter bw) throws IOException {
    System.out.println("ddl=" + ddl);
    String primaryKey = getPrimaryKeyFromDDL(ddl);
    List<GenClassFieldXMLTag> result = new ArrayList<GenClassFieldXMLTag>();
    String s = ddl.substring(ddl.indexOf(tableName.toUpperCase()) + tableName.length() + 1);
    s = s.substring(s.indexOf('(') + 1);
    String[] ss = s.split(",");
    List<String> list = combineComment(ss);
    for (int i = 0; i < list.size(); i++) {
      // System.out.println("list[" + i + "]="+ list.get(i));
      GenClassFieldXMLTag field = GenClassFieldXMLTag.sqlToClass(list.get(i));
      if (field == null) {
        continue;
      }
      if (primaryKey != null && primaryKey.equals(field.getName())) {
        field.setPrimaryKey(true);
      }
      result.add(field);
      writeField(i + 1, field, bw);
    }
    return result;
    // System.out.println(s);
  }
  
  private String getPrimaryKeyFromDDL(String ddl) {
    if (ddl.indexOf("PRIMARY KEY") >0) {
      String key = "PRIMARY KEY (\"";
      int index = ddl.indexOf(key);
      String s = ddl.substring(index + key.length());
      s = s.substring(0, s.indexOf('"'));
      return s;
    }
    return null;
  }

  private List<String> combineComment(String[] source) {
    List<String> result = new ArrayList<String>();
    StringBuffer last = null;
    for (String string : source) {
      if (last != null) {
        last.append(",").append(string);
        if (!string.endsWith("'")) {
          // 處理多個 ,
          continue;
        }
        result.add(last.toString());
        last = null;
      } else if (string.toUpperCase().indexOf("COMMENT") < 0) {
        result.add(string);
      } else {
        String comment =
            string.substring(string.toUpperCase().indexOf("COMMENT") + "COMMENT".length() + 2);
        if (!comment.endsWith("'")) {
          last = new StringBuffer(string);
        } else {
          result.add(string);
        }
      }
    }

    return result;
  }

  private void writeTableName(String tableName, String tableComment, BufferedWriter bw)
      throws IOException {
    bw.write("=== ");
    bw.write(tableName);
    bw.write(" (");
    bw.write(tableComment);
    bw.write(") ===");
    bw.newLine();
    bw.write("#\tColumn Name\tType\tLen\tComment");
    bw.newLine();
  }

  private void writeField(int index, GenClassFieldXMLTag field, BufferedWriter bw)
      throws IOException {
    if (bw == null) {
      return;
    }
    bw.write(String.valueOf(index));
    bw.write("\t");
    bw.write(field.getName());
    bw.write("\t");
    bw.write(field.getType());
    bw.write("\t");
    if (field.getLength() > 0) {
      bw.write(String.valueOf(field.getLength()));
    }
    bw.write("\t");
    if (field.getXmlTag() != null && field.getXmlTag().length() > 0) {
      writeString(field.getXmlTag(), bw, true);
      bw.write(" ");
    }
    writeString(field.getComment(), bw, false);
    bw.newLine();
  }

  private void writeString(String s, BufferedWriter bw, boolean addTag) throws IOException {
    if (s != null && s.length() > 0) {
      if (addTag) {
        bw.write("<");
      }
      bw.write(s);
      if (addTag) {
        bw.write(">");
      }
    }
  }

  public static void main(String[] args) {
    // HANA DB IP : 10.10.5.55:30041
    // username : NWUSER
    // password : Leadtek2021

    //GenerateDocumentFromDB gen = new GenerateDocumentFromDB(HANA, "NWUSER");
    GenerateDocumentFromDB gen = new GenerateDocumentFromDB(MYSQL, "nwuser");
    //gen.connect("10.10.5.31", 30041, "NWUSER", "Leadtek2021");
    gen.connect("10.10.5.23", 3306, "leadtek", "leadtek");
    gen.gen("NWUSER-maria.csv");
//    String date = "1421217";  
//    int funcEndYear = Integer.parseInt(date.substring(0, 3));
//    int funcEndMonth = Integer.parseInt(date.substring(3, 5));
//    System.out.println("year=" + funcEndYear);
//    System.out.println("month=" + funcEndMonth);
  }

}
