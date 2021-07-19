/**
 * Created on 2021/3/12.
 */
package tw.com.leadtek.nhiwidget.importdata;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ConvertOPD2MR {

  private Connection conn;

  public ConvertOPD2MR() {
    connectDB();
  }

  public void convert() {
    int count = 0;

    try {
      PreparedStatement ps = conn.prepareStatement(
          "SELECT FUNC_TYPE, ROC_ID, PRSN_ID , ID, FUNC_DATE, T_DOT FROM OP_D ORDER BY FUNC_DATE");
      PreparedStatement psUpdate =
          conn.prepareStatement("INSERT INTO MR(FUNC_TYPE, ROC_ID, PRSN_ID, "
              + "DATA_FORMAT, D_ID, MR_DATE, STATUS, UPDATE_AT, NOTIFY, READED, T_DOT,"
              + "CHANGE_ICD, CHANGE_ORDER, CHANGE_OTHER) VALUES "
              + "(?, ?, ?, '10', ?, ?, 1,  NOW(), 0, 0, ?, 0, 0, 0)");
      ResultSet rs = ps.executeQuery();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
      while (rs.next()) {
        count++;
        System.out.println(rs.getString("ROC_ID") +"," + rs.getString("PRSN_ID"));
        psUpdate.setString(1, rs.getString("FUNC_TYPE"));
        psUpdate.setString(2, rs.getString("ROC_ID"));
        psUpdate.setString(3, rs.getString("PRSN_ID"));
        psUpdate.setLong(4, rs.getLong("ID"));
        int chineseDate = 19110000 + Integer.parseInt(rs.getString("FUNC_DATE"));
        try {
          psUpdate.setDate(5, new Date(sdf.parse(String.valueOf(chineseDate)).getTime()));
        } catch (ParseException e) {
          e.printStackTrace();
        }
        psUpdate.setInt(6, rs.getInt("T_DOT"));
        psUpdate.addBatch();
        if (count % 1000 == 0) {
          psUpdate.executeBatch();
          conn.commit();
        }
      }
      rs.close();
      ps.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }


  public void connectDB() {
    conn = null;
    try { // encrypt and validateCertificate should be true for HANA Cloud connections
      conn = DriverManager.getConnection("jdbc:sap://10.10.5.55:30041", "NWUSER", "Leadtek2021");
      conn.setAutoCommit(false);
    } catch (SQLException e) {
      System.err.println("Connection Failed:");
      System.err.println(e);
      return;
    }
    if (conn != null) {
      System.out.println("Connection to HANA successful!");
    }
  }

  public static void main(String[] args) {
    new ConvertOPD2MR().convert();
  }

}
