/**
 * Created on 2021/3/12.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import tw.com.leadtek.nhiwidget.model.rdb.MR;

public interface MRDao extends JpaRepository<MR, Long>, JpaSpecificationExecutor<MR> {

  public List<MR> findByMrDateBetween(Date startDate, Date endDate);
  
  public List<MR> findByDataFormatAndMrDateBetweenOrderById(String dataFormat, Date startDate, Date endDate);
  
  public List<MR> findByDataFormatAndMrDateBetweenAndIcdAllIsNullOrderById(String dataFormat, Date startDate, Date endDate);
  
  public List<MR> findByStatusAndMrDateBetween(Integer status, Date startDate, Date endDate);
  
  /**
   * 取得指定日期區間的病歷數, 申請病歷數及申請總點數.
   * @return
   */
  @Query(value = "SELECT b.OP_TOTAL_MR AS OP_MR, c.IP_TOTAL_MR AS IP_MR, b.OP_DOT AS OP_DOT, "
      + "c.IP_DOT AS IP_DOT FROM " + 
      // 1. 門診申請件數 , 申請總點數  
      "(SELECT count(mr.ID) AS OP_TOTAL_MR, COALESCE(SUM(opd.T_APPL_DOT), 0) AS OP_DOT " + 
      "  FROM MR mr, OP_D opd WHERE mr.MR_DATE BETWEEN ?1 AND ?2 " + 
      "  AND mr.DATA_FORMAT = '10' AND mr.D_ID = opd.id) b," + 
      // 2. 住院申請件數 , 申請總點數" + 
      "(SELECT count(mr.ID) AS IP_TOTAL_MR, COALESCE(SUM(ipd.APPL_DOT), 0) AS IP_DOT " + 
      "  FROM MR mr, IP_D ipd WHERE mr.MR_DATE BETWEEN ?3 AND ?4 " + 
      "  AND mr.DATA_FORMAT = '20' AND mr.D_ID = ipd.id ) c", nativeQuery = true)
  public List<Map<String, Object>> queryMRRecordCount(Date sDate, Date eDate, Date sDate2, Date eDate2);
  
  /**
   * 取得指定日期區間及科別的病歷數, 申請病歷數及申請總點數.
   * @return
   */
  @Query(value = "SELECT b.OP_TOTAL_MR AS OP_MR, c.IP_TOTAL_MR AS IP_MR, b.OP_DOT AS OP_DOT, "
      + "c.IP_DOT AS IP_DOT FROM " + 
      "(SELECT count(DISTINCT(mr.ID)) AS OP_TOTAL_MR, COALESCE(sum(opp.TOTAL_DOT), 0) AS OP_DOT " + 
      "FROM MR mr, OP_D opd, OP_P opp WHERE mr.MR_DATE BETWEEN ?1 AND ?2 AND mr.FUNC_TYPE =?3 " + 
      "AND mr.DATA_FORMAT  = '10' AND mr.D_ID = opd.id AND opd.id = opp.OPD_ID AND opp.APPL_STATUS = 1) b," + 
      "(SELECT count(DISTINCT(mr.ID)) AS IP_TOTAL_MR, COALESCE(sum(ipp.TOTAL_DOT), 0) AS IP_DOT FROM " + 
      "MR mr, IP_D ipd, IP_P ipp WHERE mr.MR_DATE BETWEEN ?4 AND ?5 AND mr.FUNC_TYPE =?6 " + 
      "AND mr.DATA_FORMAT = '20' AND mr.D_ID = ipd.id AND ipd.id = ipp.IPD_ID AND ipp.APPL_STATUS = 1) c", nativeQuery = true)
  public List<Map<String, Object>> queryMRRecordCountByFuncType(Date sDate, Date eDate, String funcType, 
      Date sDate2, Date eDate2, String funcType2);
  
  /**
   * 取得指定日期區間的各個病歷確認狀態總數
   * @param sDate
   * @param eDate
   * @return
   */
  @Query(value = "SELECT STATUS, COUNT(STATUS) AS STATUS_SUM FROM MR "
      + "WHERE DATA_FORMAT = :dataFormat AND MR_DATE BETWEEN :sDate AND :eDate GROUP BY STATUS", nativeQuery = true)
  public List<Map<String, Object>> queryMRStatusCount(@Param("dataFormat") String dataFormat, 
      @Param("sDate") Date sDate,  @Param("eDate") Date eDate);
//  public List<Map<String, Object>> queryMRStatusCount(@Param("dataFormat") List<String> dataFormat, 
//      @Param("sDate") Date sDate,  @Param("eDate") Date eDate);
  
  /**
   * 依科別取得指定日期區間科別的各個病歷確認狀態總數
   * @param sDate
   * @param eDate
   * @return
   */
  @Query(value = "SELECT STATUS, COUNT(STATUS) AS STATUS_SUM FROM MR "
      + "WHERE DATA_FORMAT = ?1 AND MR_DATE BETWEEN "
      + "?2 AND ?3 AND FUNC_TYPE = ?4 GROUP BY STATUS ", nativeQuery = true)
  public List<Map<String, Object>> queryMRStatusCount(String dataFormat, Date sDate, Date eDate, String funcType);
  
  /**
   * 依申報人員id取得指定日期區間的各個病歷確認狀態總數
   * @param sDate
   * @param eDate
   * @return
   */
  @Query(value = "SELECT STATUS, COUNT(STATUS) AS STATUS_SUM FROM MR WHERE MR_DATE BETWEEN "
      + "?1 AND ?2 AND APPL_ID IN (?3) GROUP BY STATUS ", nativeQuery = true)
  public List<Map<String, Object>> queryMRStatusCountByApplId(Date sDate, Date eDate, List<String> applId);
  
  @Transactional
  @Modifying
  @Query(value = "UPDATE MR SET D_ID=?1 WHERE ID=?2", nativeQuery = true)
  public void updateDid(Long did, Long mrId);
  
  @Transactional
  @Modifying
  @Query(value = "UPDATE MR SET DRG_CODE=?1 WHERE ID=?2", nativeQuery = true)
  public void updateDRG(String drg, Long mrId);
  
  /**
   * 取得DRG各科, 非DRG在指定日期區間的件數及點數
   */
  @Query(value ="SELECT * FROM " + 
      "(SELECT FUNC_TYPE , COUNT(1) AS DRG_QUANTITY, SUM(T_DOT) AS DRG_POINT FROM MR " + 
      " WHERE DRG_SECTION IS NOT NULL AND MR_DATE >= ?1 AND MR_DATE <= ?2 GROUP BY FUNC_TYPE) DRG," + 
      "(SELECT FUNC_TYPE AS NONDRG_FUNC_TYPE, COUNT(1) AS NONDRG_QUANTITY, SUM(T_DOT) AS NONDRG_POINT FROM MR " + 
      " WHERE DRG_SECTION IS NULL AND DATA_FORMAT = '20' AND MR_DATE >= ?3 AND MR_DATE <= ?4 " + 
      " AND FUNC_TYPE IN (SELECT DISTINCT(FUNC_TYPE) FROM MR WHERE DRG_SECTION IS NOT NULL " +
      " AND MR_DATE >= ?5 AND MR_DATE <= ?6) GROUP BY FUNC_TYPE) NODRG " +
      "WHERE DRG.FUNC_TYPE = NODRG.NONDRG_FUNC_TYPE", nativeQuery = true)
  public List<Object[]> countDRGPointByStartDateAndEndDate(Date startDate1, Date endDate1, Date startDate2, Date endDate2, 
      Date startDate3, Date endDate3);
  
  /**
   * 取得DRG各科在指定日期區間的件數及點數
   */
  @Query(value ="SELECT FUNC_TYPE , COUNT(1) AS DRG_QUANTITY, SUM(T_DOT) AS DRG_POINT FROM MR " + 
      " WHERE DRG_SECTION IS NOT NULL AND MR_DATE >= ?1 AND MR_DATE <= ?2 GROUP BY FUNC_TYPE", nativeQuery = true)
  public List<Object[]> countDRGPointByStartDateAndEndDate(Date startDate1, Date endDate1);
  
  /**
   * 取得非DRG各科在指定日期區間的件數及點數
   */
  @Query(value ="SELECT FUNC_TYPE , COUNT(1) AS NONDRG_QUANTITY, SUM(T_DOT) AS NONDRG_POINT FROM MR " + 
      " WHERE DRG_SECTION IS NULL AND DATA_FORMAT = '20' AND MR_DATE >= ?1 AND MR_DATE <= ?2 GROUP BY FUNC_TYPE", nativeQuery = true)
  public List<Object[]> countNonDRGPointByStartDateAndEndDate(Date startDate1, Date endDate1);
  
  /**
   * 取得DRG指定科別在指定日期區間的不同區的件數及點數
   */
  @Query(value="SELECT DRG_SECTION, COUNT(1) AS QUANTITY, SUM(T_DOT) AS POINT FROM MR " + 
      "WHERE DRG_SECTION IS NOT NULL AND MR_DATE >= ?1 AND MR_DATE <= ?2 AND FUNC_TYPE =?3 GROUP BY DRG_SECTION", nativeQuery = true)
  public List<Object[]> countDRGPointByFuncTypeGroupByDRGSection(Date startDate1, Date endDate1, String funcType);
  
  /**
   * 取得某一年月落入DRG的所有科別
   * @param ym 民國年
   * @return
   */
  @Query(value="SELECT DISTINCT (FUNC_TYPE) FROM MR WHERE APPL_YM =?1 AND DRG_SECTION IS NOT NULL",
      nativeQuery = true)
  public List<Object[]> findDRGDistinctFuncTypeByApplYm(String ym);
  
  /**
   * 取得曾落入DRG的所有科別
   * @return
   */
  @Query(value="SELECT DISTINCT (FUNC_TYPE) FROM MR WHERE DRG_SECTION IS NOT NULL",
      nativeQuery = true)
  public List<Object[]> findDRGAllFuncType();
  
  /**
   * 取得所有科別
   * @return
   */
  @Query(value="SELECT DISTINCT (FUNC_TYPE) FROM MR",
      nativeQuery = true)
  public List<Object[]> findAllFuncType();
  
  /**
   * 取得指定時間落入DRG的所有科別
   * @param startDate
   * @param endDate
   * @return
   */
  @Query(value="SELECT DISTINCT (FUNC_TYPE) FROM MR WHERE DRG_SECTION IS NOT NULL AND" + 
      "MR_DATE >= ?1 AND MR_DATE <= ?2", nativeQuery = true)
  public List<Object[]> findDRGDistinctFuncTypeByDate(Date startDate, Date endDate);
  
  /**
   * 取得某一年月落入DRG的指定科別的各區件數及點數
   * @param ym
   * @param funcType
   * @return
   */
  @Query(value="SELECT DRG_SECTION , COUNT(1) AS DRG_COUNT, SUM(T_DOT), SUM(IP_D.MED_DOT) FROM MR, IP_D " + 
      "WHERE APPL_YM = ?1 AND DRG_SECTION IS NOT NULL AND MR.ID = IP_D.MR_ID " + 
      "AND MR.FUNC_TYPE = ?2 GROUP BY DRG_SECTION ", nativeQuery = true)
  public List<Object[]> findDRGCountAndDotByApplYmGroupByDrgSection(String ym, String funcType);
  
  @Query(value = "SELECT MIN(APPL_YM) FROM MR", nativeQuery = true)
  public String getMinYm();
  
  @Query(value = "SELECT MAX(APPL_YM) FROM MR", nativeQuery = true)
  public String getMaxYm();
  
  @Transactional
  @Modifying
  @Query(value = "UPDATE MR SET STATUS=?1 WHERE ID=?2", nativeQuery = true)
  public void updateMrStauts(Integer status, Long id);
  
  /**
   * 取得被智能提示助理標示需確認的病歷
   * @param conditionCode
   * @param reasonCode
   * @return
   */
  @Query(value = "SELECT * FROM MR WHERE ID IN (SELECT MR_ID FROM INTELLIGENT WHERE CONDITION_CODE=?1 "
      + " AND REASON_CODE LIKE ?2)", nativeQuery = true)
  public List<MR> getIntelligentMR(Integer conditionCode, String reasonCode);
  
  /**
   * 取得相同核刪條件的病歷
   * @param order
   * @param icdcm
   * @param dataFormat
   * @param mrId
   * @return
   */
  @Query(value = "SELECT MR.* FROM MR,  DEDUCTED_NOTE dn WHERE MR.ID = dn.ID AND DEDUCTED_ORDER=?1 "
      + "AND MR.ICDCM1 =?2 AND dn.STATUS = 1 AND MR.DATA_FORMAT =?3 AND MR.ID <> ?4", nativeQuery = true)
  public List<MR> getSameDeductedOrderMR(String order, String icdcm, String dataFormat, Long mrId);
  
  /**
   * 應用比例偏高：取得單月某院內碼使用總數量
   * @param applYm
   * @param inhCode
   * @return
   */
  @Query(value = "SELECT COUNT(1) FROM MR WHERE APPL_YM =?1 AND DATA_FORMAT = ?2 AND INH_CODE LIKE ?3", nativeQuery = true)
  public Long countByApplYmAndDataFormatAndInhCode(String applYm, String dataFormat, String inhCode);
  
  /**
   * 應用比例偏高：取得六個月院內碼使用總數量
   * 
   * @param applYm
   * @param inhCode
   * @return
   */
  @Query(
      value = "SELECT COUNT(1) FROM MR WHERE (APPL_YM = ?1 OR APPL_YM = ?2 OR APPL_YM = ?3 "
          + "OR APPL_YM = ?4 OR APPL_YM = ?5 OR APPL_YM = ?6 ) AND DATA_FORMAT = ?7 AND INH_CODE LIKE ?8",
      nativeQuery = true)
  public Long countBy6ApplYmAndDataFormatAndInhCode(String applYm1, String applYm2, String applYm3,
      String applYm4, String applYm5, String applYm6, String dataFormat, String inhCode);
  
  @Transactional
  @Modifying
  @Query(value = "UPDATE MR SET STATUS=?1 WHERE STATUS=-2 AND ID IN ("
      + "SELECT MR_ID FROM INTELLIGENT WHERE CONDITION_CODE=?2) ", nativeQuery = true)
  public void updateMrStautsForIntelligent(int newStatus, int conditionCode);
  
  @Transactional
  @Modifying
  @Query(value = "UPDATE MR SET STATUS=?1 WHERE STATUS=-2 AND ID IN "
      + "(SELECT MR_ID FROM INTELLIGENT WHERE CONDITION_CODE=?2 AND REASON_CODE=?3)", nativeQuery = true)
  public void updateMrStatusForIntelligent(int newStatus, int conditionCode, String reasonCode);
  
  @Transactional
  @Modifying
  @Query(value = "UPDATE MR SET STATUS=?1 WHERE STATUS=-2 AND ID IN "
      + "(SELECT MR_ID FROM INTELLIGENT WHERE CONDITION_CODE=?2 AND REASON_CODE=?3 AND REASON=?4)", nativeQuery = true)
  public void updateMrStatusForIntelligent(int newStatus, int conditionCode, String reasonCode, String reason);
  
  /**
   * 取得列在智能提示中的病歷
   */
  @Query(value = "SELECT * FROM MR WHERE MR.ID IN (SELECT MR_ID FROM INTELLIGENT WHERE "
      + "CONDITION_CODE =?1)", nativeQuery = true)
  public List<MR> getIntelligentMR(int conditionCode);
  
  /**
   * 修正無MR_END_DATE的 MR
   */
  @Transactional
  @Modifying
  @Query(value = "UPDATE MR SET MR_END_DATE=?1 WHERE ID=?2 ", nativeQuery = true)
  public void updateMrEndDate(Date mrEndDate, long id);
  
  @Query(value = "SELECT * FROM " + 
      "(SELECT SUM(T_DOT) + SUM(OWN_EXPENSE) AS ALL_DOT FROM MR WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2) MR_ALL," + 
      "(SELECT SUM(T_DOT) + SUM(OWN_EXPENSE) AS OP_DOT FROM MR WHERE MR_END_DATE >= ?3 AND MR_END_DATE <= ?4 AND DATA_FORMAT='10') MR_OP," + 
      "(SELECT SUM(T_DOT) + SUM(OWN_EXPENSE) AS EM_DOT FROM MR WHERE MR_END_DATE >= ?5 AND MR_END_DATE <= ?6 AND FUNC_TYPE='22') MR_EM," + 
      "(SELECT SUM(T_DOT) + SUM(OWN_EXPENSE) AS IP_DOT FROM MR WHERE MR_END_DATE >= ?7 AND MR_END_DATE <= ?8 AND DATA_FORMAT='20') MR_IP," + 
      "(SELECT SUM(T_DOT) AS ALL_APPL FROM MR WHERE MR_END_DATE >= ?9 AND MR_END_DATE <= ?10) ALL_APPL," + 
      "(SELECT SUM(T_DOT) AS OP_APPL FROM MR WHERE MR_END_DATE >= ?11 AND MR_END_DATE <= ?12 AND DATA_FORMAT='10') OP_APPL," + 
      "(SELECT SUM(T_DOT) AS EM_APPL FROM MR WHERE MR_END_DATE >= ?13 AND MR_END_DATE <= ?14 AND DATA_FORMAT='10' AND FUNC_TYPE='22') EM_APPL," + 
      "(SELECT SUM(T_DOT) AS IP_APPL FROM MR WHERE MR_END_DATE >= ?15 AND MR_END_DATE <= ?16 AND DATA_FORMAT='20') IP_APPL", nativeQuery = true)
  public List<Object[]> getPointPeriod(Date s1, Date e1, Date s2, Date e2, Date s3, Date e3,
      Date s4, Date e4, Date s5, Date e5, Date s6, Date e6, Date s7, Date e7, Date s8, Date e8);
  
  @Query(value = "SELECT * FROM " + 
      "(SELECT COUNT(1) AS VISITS_ALL FROM MR WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 ) VISIT_ALL," + 
      "(SELECT COUNT(1) AS VISITS_OP FROM MR WHERE MR_END_DATE >= ?3 AND MR_END_DATE <= ?4 AND DATA_FORMAT='10') VISIT_OP," + 
      "(SELECT COUNT(1) AS VISITS_EM FROM MR WHERE MR_END_DATE >= ?5 AND MR_END_DATE <= ?6 AND FUNC_TYPE='22') VISIT_EM," + 
      "(SELECT COUNT(1) AS VISITS_IP FROM MR WHERE MR_END_DATE >= ?7 AND MR_END_DATE <= ?8 AND DATA_FORMAT='20') VISIT_IP," + 
      "(SELECT COUNT(1) AS VISITS_LEAVE FROM IP_D WHERE LEAVE_DATE >= ?9 AND LEAVE_DATE <= ?10) VISIT_LEAVE," + 
      "(SELECT COUNT(1) AS SURGERY_OPEM FROM OP_D, MR WHERE MR_END_DATE >= ?11 AND MR_END_DATE <= ?12 " + 
      "AND OP_D.MR_ID = MR.ID AND OP_D.ICD_OP_CODE1 IS NOT NULL) VISIT_SURGERY_OPEM," + 
      "(SELECT COUNT(1) AS SURGERY_EM FROM OP_D, MR WHERE MR_END_DATE >= ?13 AND MR_END_DATE <= ?14 " + 
      "AND OP_D.MR_ID = MR.ID AND OP_D.ICD_OP_CODE1 IS NOT NULL AND OP_D.FUNC_TYPE='22') VISIT_SURGERY_EM," + 
      "(SELECT COUNT(1) AS SURGERY_IP FROM IP_D, MR WHERE MR_END_DATE >= ?15 AND MR_END_DATE <= ?16 " + 
      "AND IP_D.MR_ID = MR.ID AND IP_D.ICD_OP_CODE1 IS NOT NULL) VISIT_SURGERY_IP," + 
      "(SELECT COUNT(1) AS SURGERY_LEAVE FROM IP_D WHERE LEAVE_DATE>= ?17 AND LEAVE_DATE <= ?18 " + 
      "AND IP_D.ICD_OP_CODE1 IS NOT NULL) VISIT_SURGERY_LEAVE," +
      "(SELECT COUNT(1) AS VISITS_LAST_ALL FROM MR WHERE MR_END_DATE >= ?19 AND MR_END_DATE <= ?20 ) VISIT_LAST_ALL," + 
      "(SELECT COUNT(1) AS VISITS_LAST_OP FROM MR WHERE MR_END_DATE >= ?21 AND MR_END_DATE <= ?22 AND DATA_FORMAT='10') VISIT_LAST_OP," + 
      "(SELECT COUNT(1) AS VISITS_LAST_EM FROM MR WHERE MR_END_DATE >= ?23 AND MR_END_DATE <= ?24 AND FUNC_TYPE='22') VISIT_LAST_EM," + 
      "(SELECT COUNT(1) AS VISITS_LAST_IP FROM MR WHERE MR_END_DATE >= ?25 AND MR_END_DATE <= ?26 AND DATA_FORMAT='20') VISIT_LAST_IP," + 
      "(SELECT COUNT(1) AS VISITS_LAST_LEAVE FROM IP_D WHERE LEAVE_DATE >= ?27 AND LEAVE_DATE <= ?28) VISIT_LAST_LEAVE", nativeQuery = true)
  public List<Object[]> getVisitsPeriod(Date s1, Date e1, Date s2, Date e2, Date s3, Date e3,
      Date s4, Date e4, Date s5, Date e5, Date s6, Date e6, Date s7, Date e7, Date s8, Date e8,
      Date s9, Date e9, Date s10, Date e10, Date s11, Date e11, Date s12, Date e12, Date s13, Date e13, Date s14, Date e14);
  
  /**
   * 依照病歷碼取得
   * @return
   */
  @Query(value = "select INH_MR_ID, ICDCM1, T_DOT from mr where INH_MR_ID = ?1", nativeQuery = true)
  public Map<String, Object> queryByInhMrID(String inhMrID);
  /**
   * 依照ID取得該筆病歷表
   * @param MRID
   * @return
   */
  @Query(value = "select * from mr where ID = ?1", nativeQuery = true)
  public MR getMrByID(String MRID);
  /**
   * 依照診斷馬撈出該病歷
   * @param icdcm1
   * @return
   */
  @Query(value = "select * from mr where ICDCM1 = ?1", nativeQuery = true)
  public MR getMrByIcdcm1(String icdcm1);
  
  /**
   * 費用差異--門診
   * @return
   */
  @Query(value = "select * from ( "
  		+ "select ID, ICDCM1, avg +2 * stddev as up, avg -2 * stddev as down from ( "
  		+ "SELECT ID, ICDCM1, AVG(T_DOT) AS AVG, STDDEV(T_DOT) AS STDDEV FROM MR "
  		+ "WHERE MR_DATE > date_format(date_add(now(), interval -1 year), '%Y-%m-%d') "
  		+ "AND DATA_FORMAT ='10' GROUP BY ICDCM1) temp where stddev > 0) temp2 "
//  		+ "where temp2.icdcm1 = ?1", nativeQuery = true)
        + "", nativeQuery = true)
  public List<Map<String, Object>> clinic();
  
  /**
   * 費用差異--住院
   * @return
   */
  @Query(value = "select * from ( "
	  		+ "select MR_ID, ICD_CM_1, avg +2 * stddev as up, avg -2 * stddev as down from ( "
	  		+ "SELECT MR_ID, ICD_CM_1, AVG(APPL_DOT - DIAG_DOT - ROOM_DOT) AS AVG, STDDEV(APPL_DOT - DIAG_DOT - ROOM_DOT)*2 AS STDDEV FROM IP_D "
	  		+ "WHERE MR_ID IN(SELECT ID FROM MR WHERE MR_DATE > date_format(date_add(now(), interval -1 year), '%Y-%m-%d') AND DATA_FORMAT ='20' GROUP BY ICD_CM_1 )) "
//	  		+ "WHERE MR_DATE > date_format(date_add(now(), interval -1 year), '%Y-%m-%d') "
//	  		+ "AND DATA_FORMAT ='20' GROUP BY ICDCM1) temp where stddev > 0) temp2 "
//	  		+ "where temp2.icdcm1 = ?1", nativeQuery = true)
            + "temp where stddev > 0) temp2"
            + "", nativeQuery = true)
	  public List<Map<String, Object>> hospitalized();
}
