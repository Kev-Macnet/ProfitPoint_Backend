/**
 * Created on 2021/1/21.
 */
package tw.com.leadtek.nhiwidget.db;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import tw.com.leadtek.nhiwidget.payload.my.MyOrderPayload;
import tw.com.leadtek.tools.GenClassFieldXMLTag;

/**
 * 讀取有註解的 Java DB Model class，轉成 CREATE TABLE 的 SQL.
 * 
 * @author 2268
 *
 */
public class GenerateSqlByClass {

  private final static char SEPARATOR = '\t';

  private final static String SQL_TYPE = "MARIA";
  // private final static String SQL_TYPE = "HANA";

  private String schema;

  /**
   * SQL 暫存區
   */
  private StringBuffer sb;

  /**
   * table schema 文件暫存區
   */
  private StringBuffer sbDoc;

  private String remark;

  private String tableRemark;

  private String fieldName;

  private String fieldNullable;

  private int varcharLength = -1;

  private int fieldCount = 0;

  private HashMap<String, String> fieldToXMLTag;

  public GenerateSqlByClass(String schema) {
    this.schema = schema;
    sb = new StringBuffer();
    sbDoc = new StringBuffer();
  }

  public String getSchema() {
    return schema;
  }

  public void setSchema(String schema) {
    this.schema = schema;
  }

  public void generateSQL(String classFileName, String tableName, String primaryKey) {
    fieldCount = 0;
    fieldToXMLTag = getJsonAnnotation(classFileName);
    initialTable(tableName, primaryKey);
    try {
      BufferedReader br = new BufferedReader(
          new InputStreamReader(new FileInputStream(new File(classFileName)), "UTF-8"));
      String line = null;
      boolean readRemark = false;
      while ((line = br.readLine()) != null) {
        if (line.indexOf("/**") > -1) {
          readRemark = true;
          continue;
        }
        if (readRemark) {
          readRemark = processRemark(line);
          if (tableRemark == null && remark != null) {
            tableRemark = remark;
            sbDoc.append(" (");
            sbDoc.append(tableRemark);
            sbDoc.append(") =========================================\n");
            sbDoc.append("#").append(SEPARATOR).append("Column Name").append(SEPARATOR)
                .append("Type").append(SEPARATOR).append("Len").append(SEPARATOR)
                .append("Comment\n");
          }
          continue;
        }
        if (processAnnotationColumn(line)) {
          continue;
        }

        if (line.indexOf("private ") < 0) {
          continue;
        }
        String[] ss = line.substring(0, line.length() - 1).split(" ");
        addFieldSql(ss);
      }
      if (primaryKey != null) {
        // sb.append(" PRIMARY KEY (");
        // sb.append(primaryKey);
        // sb.append(")\n");
      } else {
        if (sb.charAt(sb.length() - 2) == ',') {
          sb.deleteCharAt(sb.length() - 2);
        }
      }
      if (SQL_TYPE.equals("MARIA")) {
        sb.append(")ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT = '");
      } else {
        sb.append(") COMMENT '");
      }

      sb.append(tableRemark);
      sb.append("';\n\n");

      sbDoc.append("\n");
      br.close();
      tableRemark = null;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private boolean processRemark(String line) {
    if (line.indexOf("*/") > -1) {
      return false;
    } else {
      int index = line.indexOf('*');
      remark = line;
      if (index > -1) {
        remark = remark.substring(index + 1).trim();
      }
      if (remark.indexOf("Created") > -1) {
        remark = null;
      }
    }
    return true;
  }

  private void initialTable(String tableName, String primaryKey) {
    sb.append("CREATE TABLE ");
    sb.append(schema);
    sb.append(".");
    sb.append(tableName);
    sb.append(" (");
    sb.append("\n");
    if (primaryKey != null) {
      sb.append("  ");
      sb.append(primaryKey);
      if (SQL_TYPE.equals("HANA")) {
        sb.append(" BIGINT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,\n");
      } else if (SQL_TYPE.equals("MARIA")) {
        sb.append(" BIGINT NOT NULL AUTO_INCREMENT COMMENT '序號',\n");
      }
    }

    sbDoc.append("====================================== ").append(tableName);
  }

  private void addFieldSql(String[] ss) {
    if (ss.length != 5) {
      return;
    }
    String type = getType(ss[3]);
    if (type.length() == 0) {
      remark = null;
      return;
    }

    // indent
    sb.append("  ");
    // field name
    if (fieldName != null) {
      fieldName = fieldName.toUpperCase();
      sb.append(fieldName.toUpperCase());
    } else {
      fieldName = ss[4].toUpperCase().trim();
      sb.append(ss[4].toUpperCase().trim());
    }

    sb.append(" ");
    sb.append(type);

    if (ss[4].toUpperCase().equals("ID")) {
      remark = "序號";
      if (SQL_TYPE.equals("HANA")) {
        sb.append(" PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY");
      } else if (SQL_TYPE.equals("MARIA")) {
        sb.append(" NOT NULL PRIMARY KEY AUTO_INCREMENT");
      }
    }

    if (fieldNullable != null && "false".equals(fieldNullable)) {
      sb.append(" NOT NULL");
    }

    if (remark != null) {
      sb.append(" COMMENT '");
      if (fieldToXMLTag.containsKey(ss[4])) {
        sb.append(fieldToXMLTag.get(ss[4]));
        sb.append(" ");
      }
      sb.append(remark);
      sb.append("'");
    }
    sb.append(",\n");

    addDocument(type, fieldName);

    fieldCount++;
    fieldName = null;
    fieldNullable = null;
  }

  private void addDocument(String type, String field) {
    sbDoc.append(fieldCount + 1).append(SEPARATOR);
    sbDoc.append(field).append(SEPARATOR);

    if (type.indexOf('(') > -1) {
      sbDoc.append(type.substring(0, type.indexOf('('))).append(SEPARATOR);
    } else {
      sbDoc.append(type).append(SEPARATOR);
    }

    if (varcharLength > 0) {
      sbDoc.append(varcharLength);
      varcharLength = -1;
    }
    sbDoc.append(SEPARATOR);
    if (remark != null) {
      if (fieldToXMLTag.containsKey(field)) {
        sbDoc.append(fieldToXMLTag.get(field));
        sbDoc.append(" ");
      }
      sbDoc.append(remark);
    }
    sbDoc.append("\n");
  }

  private String getType(String javaType) {
    if ("String".equals(javaType)) {
      String result = (SQL_TYPE.equals("MARIA")) ? "VARCHAR(" : "NVARCHAR(";
      if (varcharLength > 0) {
        result = result + varcharLength + ")";
      } else {
        result = result + "50)";
      }
      return result;
    }
    if ("Integer".equals(javaType)) {
      if (SQL_TYPE.equals("MARIA")) {
        return "INT(11)";
      } else if (SQL_TYPE.equals("HANA")) {
        return "INTEGER";
      }
    }
    if ("Long".equals(javaType)) {
      return "BIGINT";
    }
    if ("Date".equals(javaType)) {
      return "DATETIME";
    }
    if ("Float".equals(javaType)) {
      if (SQL_TYPE.equals("MARIA")) {
        return "FLOAT(8.3)";
      } else if (SQL_TYPE.equals("HANA")) {
        return "FLOAT";
      }
    }

    if (javaType.indexOf("List") >= 0) {
      return "";
    }
    return "";
  }

  private boolean processAnnotationColumn(String s) {
    String columnPrefix = "@Column";
    int index = s.indexOf(columnPrefix);
    if (index < 0) {
      return false;
    }
    // + 1 是加 '('
    String column = s.substring(index + columnPrefix.length() + 1);
    index = column.indexOf(',');
    if (index > 0) {
      String[] ss = column.split(",");
      for (String string : ss) {
        processAnnotationColumnParameter(string);
      }
    } else {
      processAnnotationColumnParameter(column);
    }
    return true;
  }

  private void processAnnotationColumnParameter(String s) {
    String[] ss = s.split("=");
    if (ss.length == 2) {
      String name = ss[0].trim().toLowerCase();
      int index = ss[1].indexOf(')');
      if ("length".equals(name)) {
        String value = (index > 0) ? ss[1].substring(0, index).trim() : ss[1].trim();
        varcharLength = Integer.parseInt(value);
      } else if ("name".equals(name)) {
        String value = (index > 0) ? ss[1].substring(0, index).trim() : ss[1].trim();
        fieldName = value.substring(1, value.length() - 1);
      } else if ("nullable".equals(name)) {
        String value = (index > 0) ? ss[1].substring(0, index).trim() : ss[1].trim();
        if ("false".equals(value.toLowerCase())) {
          // 不可為空值
          fieldNullable = "false";
        }
      }
    }
  }

  public void output(String filename) {
    try {
      BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
          new FileOutputStream(filename + "-" + SQL_TYPE + ".sql"), "UTF-8"));
      bw.write(sb.toString());
      bw.close();

      bw = new BufferedWriter(
          new OutputStreamWriter(new FileOutputStream(filename + ".csv"), "UTF-8"));
      bw.write(sbDoc.toString());
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static HashMap<String, String> getJsonAnnotation(String filename) {
    HashMap<String, String> result = new HashMap<String, String>();
    try {
      BufferedReader br = new BufferedReader(
          new InputStreamReader(new FileInputStream(new File(filename)), "UTF-8"));
      String line = null;
      String annotation1 = "@JsonProperty(\"";
      String annotation2 = "@JacksonXmlProperty(localName = \"";
      String SET = " set";
      String xmlTag = null;
      while ((line = br.readLine()) != null) {
        if (line.indexOf(annotation1) > -1) {
          String s = line.substring(line.indexOf(annotation1) + annotation1.length());
          int index = s.indexOf('"');
          xmlTag = s.substring(0, index);
          continue;
        }
        if (line.indexOf(annotation2) > -1) {
          String s = line.substring(line.indexOf(annotation2) + annotation2.length());
          int index = s.indexOf('"');
          xmlTag = s.substring(0, index);
          continue;
        }
        if (xmlTag != null && line.indexOf(SET) > 0) {
          String s = line.substring(line.indexOf(SET) + SET.length());
          int index = s.indexOf('(');
          String field = s.substring(0, index);
          result.put(field, "<" + xmlTag + ">");
          xmlTag = null;
          continue;
        }
      }
      br.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }

  public void generateClassByDB(int dbType, String schema, String IP, int port, String username,
      String password, String packageName, String folder) {
    GenerateDocumentFromDB genDB = new GenerateDocumentFromDB(dbType, schema);
    genDB.connect(IP, port, username, password);
    HashMap<String, String> tables = genDB.getAllTable(schema);
    for (String table : tables.keySet()) {
      String ddl = genDB.getTableDDL(table);
      generateClassBySQL(new StringReader(ddl), packageName, folder);
    }
  }

  public void generateClassBySQL(String filename, String packageName, String folder) {
    try {
      generateClassBySQL(new InputStreamReader(new FileInputStream(new File(filename)), "UTF-8"),
          packageName, folder);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  public void generateClassBySQL(Reader reader, String packageName, String folder) {
    try {
      BufferedReader br = new BufferedReader(reader);
      String line = null;
      int count = 0;
      String tableName = null;
      String tableRemark = null;
      List<GenClassFieldXMLTag> fields = null;
      boolean isHANA = false;
      String createTable = "CREATE TABLE ";
      while ((line = br.readLine()) != null) {
        System.out.println("line=" + line);
        if (line.indexOf("ENGINE") > -1 || line.startsWith("ALTER TABLE") || line.startsWith("CREATE INDEX")) {
          continue;
        }
        int index = line.toUpperCase().indexOf(createTable);
        if (index < 0) {
          index = line.toUpperCase().indexOf("CREATE COLUMN TABLE ");
          if (index > -1) {
            isHANA = true;
          }
        }
        if (index > -1) {
          if (tableName != null) {
            generateClass(packageName, folder, tableName, tableRemark, fields);
            tableRemark = null;
          }
          tableName = getTableName(line, isHANA);
          // System.out.println("tableName =" + tableName);
          fields = new ArrayList<GenClassFieldXMLTag>();
          if (isHANA) {
            // 所有欄位寫在一行
            GenerateDocumentFromDB gen = new GenerateDocumentFromDB(0, "");
            fields = gen.convertDDLToDocument(tableName, line, null);
          }
          continue;
        }
        GenClassFieldXMLTag xml = GenClassFieldXMLTag.sqlToClass(line.trim());
        if (xml != null && fields != null) {
          fields.add(xml);
        }
      }
      if (tableName != null) {
        generateClass(packageName, folder, tableName, tableRemark, fields);
      }
      br.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void generateClass(String packageName, String folder, String tableName, String tableRemark,
      List<GenClassFieldXMLTag> fields) {
    try {
      BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
          new FileOutputStream(folder + "//" + tableName + ".java"), "UTF-8"));
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
      writeRemark("Created on " + sdf.format(new Date()) + " by GenerateSqlByClass().", bw);
      bw.write("package ");
      bw.write(packageName);
      bw.write(";");
      bw.newLine();
      bw.newLine();
      for (GenClassFieldXMLTag genClassFieldXMLTag : fields) {
        if (genClassFieldXMLTag.getType().toUpperCase().equals("DATE")) {
          bw.write("import java.util.Date;\n");
          break;
        }
      }
      bw.write("import javax.persistence.Basic;\n" + "import javax.persistence.Column;\n"
          + "import javax.persistence.Entity;\n" + "import javax.persistence.GeneratedValue;\n"
          + "import javax.persistence.GenerationType;\n" + "import javax.persistence.Id;\n"
          + "import javax.persistence.Table;\n"
          + "import com.fasterxml.jackson.annotation.JsonIgnore;\n"
          + "import com.fasterxml.jackson.annotation.JsonProperty;\n\n");

      if (tableRemark != null) {
        writeRemark(tableRemark, bw);
      }

      bw.write("@Table(name = \"" + tableName + "\")");
      bw.newLine();
      bw.write("@Entity");;
      bw.newLine();
      bw.write("public class " + tableName.toUpperCase() + " {");
      bw.newLine();
      bw.newLine();

      for (GenClassFieldXMLTag genClassFieldXMLTag : fields) {
        bw.write(genClassFieldXMLTag.toJavaDeclareCode());
      }
      for (GenClassFieldXMLTag genClassFieldXMLTag : fields) {
        bw.write(genClassFieldXMLTag.toJavaGetSetCode());
      }
      bw.write("}");
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void writeRemark(String remark, BufferedWriter bw) throws IOException {
    bw.write("/**");
    bw.newLine();
    bw.write(" * ");
    bw.write(remark);
    bw.newLine();
    bw.write(" */");
    bw.newLine();
  }

  public void generateSQL() {
    generateSQL(
        "D:\\Users\\2268\\2020\\健保點數申報\\src\\NHIWidget\\src\\main\\java\\tw\\com\\leadtek\\nhiwidget\\model\\xml\\OutPatientD.java",
        "OP_D", null);
    generateSQL(
        "D:\\Users\\2268\\2020\\健保點數申報\\src\\NHIWidget\\src\\main\\java\\tw\\com\\leadtek\\nhiwidget\\model\\xml\\OutPatientP.java",
        "OP_P", null);
    generateSQL(
        "D:\\Users\\2268\\2020\\健保點數申報\\src\\NHIWidget\\src\\main\\java\\tw\\com\\leadtek\\nhiwidget\\model\\xml\\OutPatientT.java",
        "OP_T", null);

    generateSQL(
        "D:\\Users\\2268\\2020\\健保點數申報\\src\\NHIWidget\\src\\main\\java\\tw\\com\\leadtek\\nhiwidget\\model\\xml\\InPatientD.java",
        "IP_D", null);
    generateSQL(
        "D:\\Users\\2268\\2020\\健保點數申報\\src\\NHIWidget\\src\\main\\java\\tw\\com\\leadtek\\nhiwidget\\model\\xml\\InPatientP.java",
        "IP_P", null);
    generateSQL(
        "D:\\Users\\2268\\2020\\健保點數申報\\src\\NHIWidget\\src\\main\\java\\tw\\com\\leadtek\\nhiwidget\\model\\xml\\InPatientT.java",
        "IP_T", null);
    output("D:\\Users\\2268\\2020\\健保點數申報\\src\\" + getSchema());
  }

  public static String removeDoubleQuote(String s) {
    StringBuffer sb = new StringBuffer(s);
    if (s.startsWith("\"")) {
      sb.deleteCharAt(0);
    }
    if (s.endsWith("\"")) {
      sb.deleteCharAt(sb.length() - 1);
    }
    return sb.toString();
  }

  public static String getTableName(String s, boolean isHANA) {
    String result = (isHANA) ? s.substring("CREATE COLUMN TABLE ".length()).split(" ")[0]
        : s.substring("CREATE TABLE ".length()).split(" ")[0];
    int index = result.indexOf('.');
    if (index > -1) {
      result = result.split("\\.")[1];
    }
    return removeDoubleQuote(result);
  }

  public static void main(String[] args) {
    GenerateSqlByClass gen = new GenerateSqlByClass("NWUSER");
    // gen.generateSQL();

    // gen.generateClassBySQL("D:\\Users\\2268\\2020\\健保點數申報\\src\\NWUSER-ALL-HANA.sql",
    // "tw.com.leadtek.nhiwidget.model.rdb", "D:\\Users\\2268\\2020\\健保點數申報\\src\\generateClass");

//    gen.generateClassByDB(GenerateDocumentFromDB.HANA, "NWUSER", "10.10.5.31", 30041, "NWUSER",
//        "Leadtek2021", "tw.com.leadtek.nhiwidget.model.rdb",
//        "D:\\Users\\2268\\2020\\健保點數申報\\src\\generateClass");
    findDeclaredMethod("tw.com.leadtek.nhiwidget.payload.my.MyOrderPayload", "applId");
  }

  public static boolean findDeclaredMethod(String className, String field) {
    try {
      Class<?> c = Class.forName(className);
      String functionName = field.substring(0, 1).toUpperCase() + field.substring(1);
      c.getDeclaredMethod("get" + functionName, null);
      return true;
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (SecurityException e) {
      e.printStackTrace();
    }
    return false;
  }
}
