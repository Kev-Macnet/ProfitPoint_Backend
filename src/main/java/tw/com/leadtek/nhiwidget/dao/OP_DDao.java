/**
 * Created on 2021/1/26.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.sql.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tw.com.leadtek.nhiwidget.model.rdb.OP_D;

public interface OP_DDao extends JpaRepository<OP_D, Long> {

  public List<OP_D> findByOptId(Long optId);
  
  @Query(value = "SELECT SEQ_NO, ID, ROC_ID, FUNC_DATE, MR_ID FROM OP_D WHERE OPT_ID= ?1 ", nativeQuery = true)
  public List<Object[]> findByOptIdSimple(Long optId);
  
  @Query(value = "SELECT * FROM OP_D WHERE ID IN (SELECT D_ID FROM MR WHERE DATA_FORMAT = ?1 AND MR_DATE BETWEEN ?2 AND ?3) ", nativeQuery = true)
  public List<OP_D> findByIDFromMR(String dataFormat, Date sDate, Date eDate);
  
  @Query(value = "SELECT DISTINCT(PRSN_ID) , FUNC_TYPE "
      + "FROM OP_D WHERE PRSN_ID LIKE '%***%' AND FUNC_TYPE IS NOT NULL ORDER BY FUNC_TYPE", nativeQuery = true)
  public List<Object[]> findDepartmentAndDoctor();
  
  /**
   * 取得單月門診、急診、住院部份負擔點數
   */
  @Query(value = "SELECT * FROM " + 
      "(SELECT SUM(PART_DOT) AS PART_OP FROM OP_D WHERE OPT_ID=?1 AND CASE_TYPE<>'02'),"
      + "(SELECT SUM(PART_DOT) AS PART_EM FROM OP_D WHERE OPT_ID=?2 AND CASE_TYPE='02'),"
      + "(SELECT SUM(PART_DOT) AS PART_IP FROM IP_D WHERE IPT_ID=?3),"
      + "(SELECT SUM(T_APPL_DOT) AS APPL_OP FROM OP_D WHERE OPT_ID=?4 AND CASE_TYPE<>'02'),"
      + "(SELECT SUM(T_APPL_DOT) AS APPL_EM FROM OP_D WHERE OPT_ID=?5 AND CASE_TYPE='02'),"
      + "(SELECT SUM(APPL_DOT) AS APPL_IP FROM IP_D WHERE IPT_ID=?6),"
      + "(SELECT COUNT(1) AS PATIENT_OP FROM OP_D WHERE OPT_ID=?7 AND CASE_TYPE<>'02'),"
      + "(SELECT COUNT(1) AS PATIENT_EM FROM OP_D WHERE OPT_ID=?8 AND CASE_TYPE='02'),"
      + "(SELECT COUNT(1) AS PATIENT_IP FROM IP_D WHERE IPT_ID=?9 AND OUT_DATE IS NOT NULL),"
      + "(SELECT SUM(T_APPL_DOT) AS CHRONIC FROM OP_D WHERE OPT_ID=?10 AND CASE_TYPE = '08'),"
      + "(SELECT COUNT(1) AS IP_QUANTITY FROM MR WHERE APPL_YM =?11 AND DATA_FORMAT ='20'),"
      + "(SELECT COUNT(1) AS DRG_QUANTITY FROM MR WHERE DRG_SECTION IS NOT NULL AND APPL_YM=?12),"
      + "(SELECT SUM(MR.T_DOT) AS DRG_APPLDOT, SUM(IP_D.MED_DOT) AS DRG_ACTUAL_POINT FROM MR, IP_D WHERE "
      + "MR.DRG_SECTION IS NOT NULL AND MR.APPL_YM=?13 AND MR.ID = IP_D.MR_ID)", nativeQuery = true)
  public List<Object[]> findMonthlyPoint(long idOP1, long idOP2, long idIP1, long idOP3, long idOP4, long idIP2,
      long idOP5, long idOP6, long idIP3, long idOP7, String ym1, String ym2, String ym3);
  
  /**
   * 取得指定區間的病歷數、申報點數及部份負擔點數
   */
  @Query(value = "SELECT * FROM " + 
      "(SELECT COUNT(1) AS ALL_COUNT FROM MR WHERE MR_DATE >= ?1 AND MR_DATE <=?2)," + 
      "(SELECT COUNT(1) AS OP_ALL_COUNT FROM MR WHERE DATA_FORMAT ='10' AND MR_DATE >= ?3 AND MR_DATE <=?4)," + 
      "(SELECT COUNT(1) AS OP_COUNT FROM MR WHERE DATA_FORMAT ='10' AND FUNC_TYPE <> '22' AND MR_DATE >= ?5 AND MR_DATE <=?6)," + 
      "(SELECT COUNT(1) AS OP_EM_COUNT FROM MR WHERE DATA_FORMAT ='10' AND FUNC_TYPE = '22' AND MR_DATE >= ?7 AND MR_DATE <=?8)," + 
      "(SELECT COUNT(1) AS IP_COUNT FROM MR WHERE DATA_FORMAT ='20' AND MR_DATE >= ?9 AND MR_DATE <=?10)," + 
      "(SELECT SUM(T_APPL_DOT) AS APPL_OP_ALL FROM MR, OP_D WHERE MR_DATE >= ?11 AND MR_DATE <=?12 AND OP_D.MR_ID = MR.ID)," + 
      "(SELECT SUM(T_APPL_DOT) AS APPL_OP FROM MR, OP_D WHERE MR_DATE >= ?13 AND MR_DATE <=?14 AND OP_D.MR_ID = MR.ID AND OP_D.FUNC_TYPE <> '22')," + 
      "(SELECT SUM(T_APPL_DOT) AS APPL_EM FROM MR, OP_D WHERE MR_DATE >= ?15 AND MR_DATE <=?16 AND OP_D.MR_ID = MR.ID AND OP_D.FUNC_TYPE = '22')," + 
      "(SELECT SUM(IP_D.APPL_DOT) AS APPL_IP FROM MR, IP_D WHERE MR_DATE >= ?17 AND MR_DATE <=?18 AND IP_D.MR_ID = MR.ID)," + 
      "(SELECT SUM(PART_DOT) AS PART_OP_ALL FROM MR, OP_D WHERE MR_DATE >= ?19 AND MR_DATE <=?20 AND OP_D.MR_ID = MR.ID)," + 
      "(SELECT SUM(PART_DOT) AS PART_OP FROM MR, OP_D WHERE MR_DATE >= ?21 AND MR_DATE <=?22 AND OP_D.MR_ID = MR.ID AND OP_D.FUNC_TYPE <> '22')," + 
      "(SELECT SUM(PART_DOT) AS PART_EM FROM MR, OP_D WHERE MR_DATE >= ?23 AND MR_DATE <=?24 AND OP_D.MR_ID = MR.ID AND OP_D.FUNC_TYPE = '22')," + 
      "(SELECT SUM(PART_DOT) AS PART_IP FROM MR, IP_D WHERE MR_DATE >= ?25 AND MR_DATE <=?26 AND IP_D.MR_ID = MR.ID)", nativeQuery = true) 
  public List<Object[]> findPeriodPoint(Date sdate1, Date edate1,Date sdate2, Date edate2,Date sdate3, Date edate3,
      Date sdate4, Date edate4, Date sdate5, Date edate5,Date sdate6, Date edate6,Date sdate7, Date edate7,
      Date sdate8, Date edate8,Date sdate9, Date edate9, Date sdate10, Date edate10,Date sdate11, Date edate11,
      Date sdate12, Date edate12,Date sdate13, Date edate13);
  
  /**
   * 門診各科申報總點數
   * @param sdate
   * @param edate
   * @return  [科別代碼, 申報金額, 件數]
   */
  @Query(value = "SELECT OP_D.FUNC_TYPE, SUM(T_APPL_DOT), COUNT(1) FROM MR, OP_D " + 
      "WHERE MR_DATE >= ?1 AND MR_DATE <= ?2 AND OP_D.MR_ID = MR.ID GROUP BY OP_D.FUNC_TYPE", nativeQuery = true) 
  public List<Object[]> findApplPointGroupByFuncType(Date sdate, Date edate);
  
  /**
   * 門診各科部分負擔總金額
   * @param sdate
   * @param edate
   * @return  [科別代碼, 部分負擔金額, 件數]
   */
  @Query(value = "SELECT OP_D.FUNC_TYPE, SUM(PART_DOT), COUNT(1) FROM MR, OP_D " + 
      "WHERE MR_DATE >= ?1 AND MR_DATE <= ?2 AND OP_D.MR_ID = MR.ID GROUP BY OP_D.FUNC_TYPE", nativeQuery = true) 
  public List<Object[]> findPartPointGroupByFuncType(Date sdate, Date edate);
  
  /**
   * 取得指定區間的門急診點數及住院點數(申報+部分負擔)
   * @param sdate1
   * @param edate1
   * @param sdate2
   * @param edate2
   * @return [門急診點數, 住院點數]
   */
  @Query(value ="SELECT * FROM " + 
      "(SELECT SUM(OP_D.T_DOT) AS OP_POINT FROM MR, OP_D WHERE MR_DATE >= ?1 AND MR_DATE <= ?2 AND OP_D.MR_ID = MR.ID)," + 
      "(SELECT (SUM(IP_D.APPL_DOT) + SUM(IP_D.PART_DOT)) AS IP_POINT FROM MR, IP_D " + 
      "WHERE MR_DATE >= ?3 AND MR_DATE <= ?4 AND IP_D.MR_ID = MR.ID)", nativeQuery = true)
  public List<Object[]> findAllPoint(Date sdate1, Date edate1, Date sdate2, Date edate2);
}
