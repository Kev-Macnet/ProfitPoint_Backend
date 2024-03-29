/**
 * Created on 2021/3/12.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.sql.Date;
import java.util.ArrayList;
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
  @Query(value = "UPDATE MR SET DRG_CODE=?1, DRG_FIXED=?2, DRG_SECTION=?3 WHERE ID=?4", nativeQuery = true)
  public void updateDRG(String drg, int drgFix, String drgSection, Long mrId);
  
  /**
   * 取得DRG各科, 非DRG在指定日期區間的件數及點數.
   * 2022/7/28 因DRG和NODRG的科別可能會不一樣，因此拿掉WHERE條件。
   */
  @Query(value ="SELECT * FROM " + 
      "(SELECT MR.FUNC_TYPE , COUNT(1) AS DRG_QUANTITY, SUM(IP_D.APPL_DOT + IP_D.PART_DOT) AS DRG_ACTUAL_POINT "
      + "FROM MR, IP_D WHERE DRG_SECTION IS NOT NULL AND MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND "
      + "IP_D.MR_ID = MR.ID AND IP_D.TW_DRGS_SUIT_MARK = '0' GROUP BY MR.FUNC_TYPE) DRG," + 
      "(SELECT MR.FUNC_TYPE AS NONDRG_FUNC_TYPE, COUNT(1) AS NONDRG_QUANTITY, SUM(IP_D.APPL_DOT + IP_D.PART_DOT) "
      + "AS NONDRG_POINT FROM MR, IP_D WHERE IP_D.TW_DRGS_SUIT_MARK <> '0' AND DATA_FORMAT = '20' AND MR_END_DATE >= ?1 AND "
      + "MR_END_DATE <= ?2 AND MR.FUNC_TYPE IN (SELECT DISTINCT(FUNC_TYPE) FROM MR WHERE DRG_SECTION IS NOT NULL " +
      " AND MR_END_DATE >= ?1 AND MR_END_DATE <= ?2) AND IP_D.MR_ID = MR.ID GROUP BY MR.FUNC_TYPE) NODRG", nativeQuery = true)
  public List<Object[]> countAllDRGPointByStartDateAndEndDate(Date startDate1, Date endDate1);
  
  /**
   * 取得DRG各科在指定日期區間的件數及點數
   */
  @Query(value ="SELECT MR.FUNC_TYPE, COUNT(1) AS DRG_QUANTITY, SUM(T_DOT) AS DRG_POINT FROM MR, IP_D " + 
      " WHERE IP_D.TW_DRGS_SUIT_MARK = '0' AND MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 "
      + "AND MR.ID = IP_D.MR_ID GROUP BY MR.FUNC_TYPE", nativeQuery = true)
  public List<Object[]> countDRGPointByStartDateAndEndDate(Date startDate1, Date endDate1);
  
  /**
   * 取得非DRG各科在指定日期區間的件數及點數
   */
  @Query(value ="SELECT MR.FUNC_TYPE , COUNT(1) AS NONDRG_QUANTITY, SUM(T_DOT) AS NONDRG_POINT FROM MR, IP_D " + 
      " WHERE IP_D.TW_DRGS_SUIT_MARK <> '0' AND DATA_FORMAT = '20' AND MR_END_DATE >= ?1 "
      + "AND MR_END_DATE <= ?2 AND MR.ID = IP_D.MR_ID GROUP BY MR.FUNC_TYPE", nativeQuery = true)
  public List<Object[]> countNonDRGPointByStartDateAndEndDate(Date startDate1, Date endDate1);
  
  /**
   * 取得DRG指定科別在指定日期區間的不同區的件數及點數
   */
  @Query(value="SELECT DRG_SECTION, COUNT(1) AS QUANTITY, SUM(T_DOT) AS POINT FROM MR, IP_D " + 
      "WHERE DRG_SECTION IS NOT NULL AND IP_D.TW_DRGS_SUIT_MARK = '0' AND MR_END_DATE >= ?1 "
      + "AND MR_END_DATE <= ?2 AND MR.FUNC_TYPE =?3 AND MR.ID = IP_D.MR_ID GROUP BY DRG_SECTION", nativeQuery = true)
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
      "MR_END_DATE >= ?1 AND MR_END_DATE <= ?2", nativeQuery = true)
  public List<Object[]> findDRGDistinctFuncTypeByDate(Date startDate, Date endDate);
  
  /**
   * 取得某一年月落入DRG的指定科別的各區件數及點數
   * @param ym
   * @param funcType
   * @return
   */
  @Query(value="SELECT DRG_SECTION , COUNT(1) AS DRG_COUNT, SUM(IP_D.APPL_DOT + IP_D.PART_DOT) AS APPLY, "
      + "SUM(IP_D.MED_DOT + IP_D.NON_APPL_DOT) AS ACTUAL FROM MR, IP_D WHERE MR.MR_END_DATE LIKE CONCAT(?1,'%') AND "
      + "DRG_SECTION IS NOT NULL AND MR.ID = IP_D.MR_ID AND MR.FUNC_TYPE = ?2 AND IP_D.TW_DRGS_SUIT_MARK = '0' "
      + "GROUP BY DRG_SECTION", nativeQuery = true)
  public List<Object[]> findDRGCountAndDotByApplYmGroupByDrgSection(String ym, String funcType);
  
  @Query(value = "SELECT MIN(APPL_YM) FROM MR", nativeQuery = true)
  public String getMinYm();
  
  @Query(value = "SELECT MAX(APPL_YM) FROM MR", nativeQuery = true)
  public String getMaxYm();
  
  @Transactional
  @Modifying
  @Query(value = "UPDATE MR SET STATUS=?1 WHERE ID=?2", nativeQuery = true)
  public void updateMrStauts(Integer status, Long id);
  
  @Transactional
  @Modifying
  @Query(value = "UPDATE MR SET STATUS=?1 WHERE ID IN ?2", nativeQuery = true)
  public void updateMultiMrStauts(Integer status, List<Long> idList);
  
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
  
  @Query(value = "SELECT * FROM  "
  		+ "(SELECT OP_DOT + IP_DOT AS ALL_DOT, OP_DOT, EM_DOT, IP_DOT, OP_APPL + PART_IP + IP_APPLDOT AS ALL_APPL, OP_APPL, EM_APPL, PART_IP + IP_APPLDOT AS IP_APPL FROM   "
  		+ "(SELECT SUM(OP_D.T_APPL_DOT) + SUM(OP_D.PART_DOT) + SUM(OP_D.OWN_EXPENSE) AS OP_DOT FROM MR,OP_D WHERE MR.ID = OP_D.MR_ID AND MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND DATA_FORMAT='10') b,  "
  		+ "(SELECT SUM(OP_D.T_APPL_DOT) + SUM(OP_D.PART_DOT) + SUM(OP_D.OWN_EXPENSE) AS EM_DOT FROM MR,OP_D WHERE MR.ID = OP_D.MR_ID AND MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND MR.FUNC_TYPE='22') c, "
  		+ "(SELECT SUM(IP_D.MED_DOT) + SUM(IP_D.NON_APPL_DOT) + SUM(IP_D.OWN_EXPENSE) AS IP_DOT FROM MR, IP_D WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND MR.DATA_FORMAT='20' AND IP_D.MR_ID = MR.ID) d,   "
  		+ "(SELECT SUM(OP_D.T_APPL_DOT) + SUM(OP_D.PART_DOT) AS OP_APPL FROM MR,OP_D WHERE MR.ID = OP_D.MR_ID AND MR_END_DATE >=?1 AND MR_END_DATE <= ?2 AND DATA_FORMAT='10') e,  "
  		+ "(SELECT SUM(OP_D.T_APPL_DOT) + SUM(OP_D.PART_DOT) AS EM_APPL FROM MR,OP_D WHERE MR.ID = OP_D.MR_ID AND MR_END_DATE >=?1 AND MR_END_DATE <= ?2 AND DATA_FORMAT='10' AND MR.FUNC_TYPE='22') f,  "
  		+ "(SELECT SUM(IP_D.MED_DOT) + SUM(IP_D.NON_APPL_DOT) AS IP_APPL  FROM MR, IP_D WHERE MR.ID = IP_D.MR_ID AND MR_END_DATE >= ?1 AND MR_END_DATE <= ?2) g, "
  		+ "(SELECT SUM(IP_D.PART_DOT) AS PART_IP, SUM(MR.APPL_DOT) AS IP_APPLDOT FROM MR, IP_D WHERE MR_END_DATE >=?1 AND MR_END_DATE <= ?2  AND IP_D.MR_ID = MR.ID) i)temp"
      , nativeQuery = true)
  public List<Object[]> getPointPeriod(Date s1, Date e1);
  
  @Query(value = "SELECT * FROM " + 
      "(SELECT COUNT(1) AS VISITS_ALL FROM MR WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 ) VISIT_ALL," + 
      "(SELECT COUNT(1) AS VISITS_OP FROM MR WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND DATA_FORMAT='10') VISIT_OP," + 
      "(SELECT COUNT(1) AS VISITS_EM FROM MR WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND FUNC_TYPE='22') VISIT_EM," + 
      "(SELECT COUNT(1) AS VISITS_IP FROM MR WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND DATA_FORMAT='20') VISIT_IP," + 
      "(SELECT COUNT(1) AS VISITS_LEAVE FROM IP_D WHERE LEAVE_DATE >= ?1 AND LEAVE_DATE <= ?2) VISIT_LEAVE," + 
      "(SELECT COUNT(DISTINCT(mr.ID)) AS SURGERY_OPEM FROM mr, op_p WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 " + 
      "AND mr.ID = op_p.MR_ID AND op_p.DRUG_NO like '6%' and length (op_p.DRUG_NO) = 6) VISIT_SURGERY_OPEM," + 
      "(SELECT COUNT(DISTINCT(mr.ID)) AS SURGERY_EM FROM mr, op_p WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 " + 
      "AND mr.ID = op_p.MR_ID AND MR.FUNC_TYPE='22' AND op_p.DRUG_NO like '6%' and length (op_p.DRUG_NO) = 6) VISIT_SURGERY_EM," + 
      "(SELECT COUNT(DISTINCT(mr.ID)) AS SURGERY_IP FROM mr, ip_p WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 " + 
      "AND mr.ID = ip_p.MR_ID AND ip_p.ORDER_CODE  like '6%' and length (ip_p.ORDER_CODE) = 6) VISIT_SURGERY_IP," + 
      "(SELECT  COUNT(DISTINCT (IP_D.ID)) AS SURGERY_LEAVE FROM ip_d, ip_p WHERE LEAVE_DATE>= ?1 AND LEAVE_DATE <= ?2 " + 
      "AND ip_d.ID = ip_p.IPD_ID AND ip_p.ORDER_CODE  like '6%' and length (ip_p.ORDER_CODE) = 6) VISIT_SURGERY_LEAVE," +
      "(SELECT COUNT(1) AS VISITS_LAST_ALL FROM MR WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 ) VISIT_LAST_ALL," + 
      "(SELECT COUNT(1) AS VISITS_LAST_OP FROM MR WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND DATA_FORMAT='10') VISIT_LAST_OP," + 
      "(SELECT COUNT(1) AS VISITS_LAST_EM FROM MR WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND FUNC_TYPE='22') VISIT_LAST_EM," + 
      "(SELECT COUNT(1) AS VISITS_LAST_IP FROM MR WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND DATA_FORMAT='20') VISIT_LAST_IP," + 
      "(SELECT COUNT(1) AS VISITS_LAST_LEAVE FROM IP_D WHERE LEAVE_DATE >= ?1 AND LEAVE_DATE <= ?2) VISIT_LAST_LEAVE", nativeQuery = true)
  public List<Object[]> getVisitsPeriod(Date s1, Date e1);
  
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
   * @param date
   * @return
   */
  @Query(value = "SELECT * FROM( "
  		+ "SELECT MR.ID, MR.T_DOT , MR.ICDCM1, AI.AVG + 2 * AI.STDDEV AS UP, AI.AVG -2 * AI.STDDEV AS DOWN "
  		+ "FROM ( "
  		+ "SELECT ICDCM1, AVG(T_DOT) AS AVG, STDDEV(T_DOT) AS STDDEV "
  		+ "FROM MR "
  		+ "WHERE MR_DATE BETWEEN ?1 AND ?2 AND DATA_FORMAT ='10' GROUP BY ICDCM1) AI, MR "
  		+ "WHERE AI.STDDEV > 0 AND MR_DATE BETWEEN ?3 AND ?4 AND MR.ICDCM1 = AI.ICDCM1 AND mr.DATA_FORMAT ='10' "
  		+ ") TEMP WHERE T_DOT > (UP * ?5) OR T_DOT < (DOWN * ?6)", nativeQuery = true)
  public List<Map<String, Object>> clinic(String sDate1, String eDate1, String sDate2, String eDate2, float up, float down);
  
  /**
   * 費用差異--住院
   * @param date
   * @return
   */
  @Query(value = "select * from ( "
  		+ "SELECT MR.ID, MR.T_DOT , MR.ICDCM1, AI.AVG + 2 * AI.STDDEV AS UP, AI.AVG -2 * AI.STDDEV AS DOWN  "
  		+ "FROM( "
  		+ "SELECT ICD_CM_1, AVG(APPL_DOT - DIAG_DOT - ROOM_DOT) AS AVG,  "
  		+ "STDDEV(APPL_DOT - DIAG_DOT - ROOM_DOT) * 2 AS STDDEV "
  		+ "FROM IP_D WHERE MR_ID IN (SELECT ID FROM MR WHERE MR_DATE  between ?1 and ?2)   "
  		+ "GROUP BY ICD_CM_1) AI, MR "
  		+ "WHERE AI.STDDEV > 0 AND MR.MR_DATE BETWEEN ?3 AND ?4 AND DATA_FORMAT ='20' AND AI.ICD_CM_1 = MR.ICDCM1 "
  		+ ")TEMP WHERE T_DOT > (UP * ?5) OR T_DOT < (DOWN * ?6)", nativeQuery = true)
	  public List<Map<String, Object>> hospitalized(String sDate1, String eDate1, String sDate2, String eDate2, float up, float down);
  /**
   * 醫療行為差異--門診
   * @param date
   * @return
   */
  @Query(value = "select MR_ID,COUNT,ORDER_CODE,ICDCM,UP,DOWN from ( "
  		+ "select mr_id as MR_ID, COUNT, DRUG_NO as ORDER_CODE, io.icdcm as ICDCM, io.ulimit as UP, io.llimit as DOWN from ( "
  		+ "select op_d.mr_id, count(op_d.mr_id) as COUNT, op_p.drug_no, op_d.icd_cm_1 from op_d , op_p "
  		+ "where op_d.id = op_p.opd_id and op_d.id in (select id from mr where MR_DATE  BETWEEN ?1 AND ?2 and DATA_FORMAT ='10' ) "
  		+ "group by op_d.mr_id, op_p.drug_no, op_d.icd_cm_1) temp , ICDCM_ORDER io "
  		+ "where temp.drug_no = io.order_code and temp.icd_cm_1 = io.icdcm and io.data_format = '10' "
  		+ "order by mr_id) temp2 "
  		+ "where COUNT > UP   or COUNT < DOWN " , nativeQuery = true)
  	  public List<Map<String, Object>> clinicMedBeh(String sDate, String eDate);
  /**
   * 醫療行為差異--住院
   * @param date
   * @return
   */
  @Query(value = "select MR_ID,COUNT,ORDER_CODE,ICDCM,UP,DOWN from ( "
  		+ "select  temp.MR_ID, temp.COUNT, temp.ORDER_CODE, io.ICDCM, io.ulimit as UP, io.llimit as DOWN from ( "
  		+ "select ip_d.mr_id, count(ip_d.mr_id) as COUNT, ip_p.order_code, ip_d.icd_cm_1 from ip_d , ip_p "
  		+ "where ip_d.id = ip_p.ipd_id and ip_d.mr_id in (select id from mr where MR_DATE  BETWEEN ?1 AND ?2 and DATA_FORMAT ='20' ) "
  		+ "group by ip_d.mr_id, ip_p.order_code, ip_d.icd_cm_1) temp , ICDCM_ORDER io "
  		+ "where temp.order_code = io.order_code and temp.icd_cm_1 = io.icdcm and io.data_format = '20' "
  		+ "order by mr_id) temp2 "
  		+ "where COUNT > UP   or COUNT < DOWN " , nativeQuery = true)
  	  public List<Map<String, Object>> hospitalMedBeh(String sDate, String eDate);
  /**
   * 手術--門診
   * @param date
   * @return
   */
  @Query(value = "select m.ID, m.INH_MR_ID, opd.ICD_CM_1, ii.ICDOP, ii.TOTAL, ii.PERCENT from mr m "
  		+ "join  op_d opd on  opd.roc_id = m.roc_id and m.ICDCM1 = opd.icd_cm_1 "
  		+ "join ICDCM_ICDOP ii on ii.ICDCM = opd.icd_cm_1 and ii.ICDOP = opd.ICD_OP_CODE1 "
  		+ "where m.MR_DATE BETWEEN ?1 AND ?2 and m.DATA_FORMAT = '10' "
  		+ "group by opd.roc_id",  nativeQuery = true)
  public List<Map<String, Object>> clinicOpepration(String sDate, String eDate);
  
  /**
   * 手術--住院
   * @param date
   * @return
   */
  @Query(value = "select m.ID, m.INH_MR_ID, ipd.ICD_CM_1, ii.ICDOP, ii.TOTAL, ii.PERCENT from mr m "
  		+ "join  ip_d ipd on  ipd.roc_id = m.roc_id and m.ICDCM1 = ipd.icd_cm_1 "
  		+ "join ICDCM_ICDOP ii on ii.ICDCM = ipd.icd_cm_1 and ii.ICDOP = ipd.ICD_OP_CODE1 "
  		+ "where m.MR_DATE BETWEEN ?1 AND ?2 and m.DATA_FORMAT = '20' "
  		+ "group by ipd.roc_id",  nativeQuery = true)
  public List<Map<String, Object>> hospitalOpepration(String sDate, String eDate);
  /**
   * 依照日期與資料格式
   * @param date
   * @param dfmt
   * @return
   */
  @Query(value ="select * from mr where mr_date > ?1 and data_format = ?2 order by id", nativeQuery = true)
  public List<MR> getMrDataByDate(String date,String dfmt);
  
  /**
   * 依照日期與資料格式
   * @param date
   * @param dfmt
   * @return
   */
  @Query(value ="select * from mr where mr_date > ?1 and data_format = ?2 group by ICDCM1 order by id", nativeQuery = true)
  public List<MR> getMrDataGroupByIcdcm(String date,String dfmt);
  
  /**
   * 住院天數差異，住院病歷的住院天數 減去 各主診斷碼算出的上限值，若超過設定檔中的天數上限
   * @param sDate(起日)
   * @param eDate(迄日)
   * @param isDays(設定值)
   * @return
   */
  @Query(value ="select MR_ID, ICD_CM_1, UP, COUNT , (COUNT - UP) as VCOUNT from ( "
  		+ "select CO.MR_ID,AI.ICD_CM_1, avg +2 * stddev as UP , STDDEV, AI.COUNT as COUNT  from "
  		+ "(SELECT ICD_CM_1, AVG(S_BED_DAY + E_BED_DAY) AS AVG, STDDEV(S_BED_DAY + E_BED_DAY) AS STDDEV,(S_BED_DAY + E_BED_DAY) as COUNT  FROM IP_D  "
  		+ "WHERE MR_ID IN (SELECT ID FROM MR WHERE MR_DATE  between ?1 and ?2) GROUP BY ICD_CM_1) AI, (select ICD_CM_1, (S_BED_DAY + E_BED_DAY) as COUNT, MR_ID from IP_D where MR_ID IN (SELECT ID FROM MR WHERE MR_DATE  between ?1 and ?2)) CO "
  		+ "where AI.stddev > 0  AND AI.ICD_CM_1 = CO.ICD_CM_1) TEMP  "
  		+ "where  (COUNT - UP) > ?3",  nativeQuery = true)
  public List<Map<String,Object>> hospitalDays(String sDate, String eDate, int isDays);
  
  // temp1 : 取得各別診斷碼使用的藥品衛材的出現次數 
  // select  mr.ICDCM1, count(mr.ICDCM1) ICCOUNT, op.DRUG_NO  from op_p op , mr  
  // where op.mr_id = mr.id and mr.mr_date between '2021-01-01' and '2021-01-31' and length(op.drug_no) = 10  and mr.code_all like concat(concat('%,', op.drug_no), ',%') 
  // group by mr.icdcm1, op.DRUG_NO ORDER BY mr.ICDCM1
  // temp2 : 主診斷碼出現的病歷數
  /**
   * 取得主診斷碼出現次數 -門診
   * @param sDate
   * @param eDate
   * @return
   */
  @Query(value ="select temp1.icdcm1 as ICDCM, temp1.ICCOUNT, temp1.DRUG_NO as DRUGNO, temp2.count as ICOUNT , (temp1.iccount / temp2.count) * 100 as PERCENT from  "
  		+ "(select  mr.ICDCM1, count(mr.ICDCM1) ICCOUNT, op.DRUG_NO  from op_p op , mr  "
  		+ "where op.mr_id = mr.id and mr.mr_date between ?1 and ?2 and length(op.drug_no) = 10  and mr.code_all like concat(concat('%,', op.drug_no), ',%') "
  		+ "group by mr.icdcm1, op.DRUG_NO) temp1, (select ICDCM1, count(ICDCM1) as COUNT from mr where  mr_date between ?1 and ?2 and id in (select mr_id from op_p where length(drug_no) = 10)  "
  		+ "group by  ICDCM1) temp2 "
  		+ "where temp1.icdcm1 = temp2.icdcm1 "
  		+ "order by temp1.icdcm1", nativeQuery = true)
  public List<Map<String,Object>> getIcdcmCountOPByDate(String sDate, String eDate);
  /**
   * 取得主診斷碼出現次數 -門診
   * @param sDate
   * @param eDate
   * @return
   */
  @Query(value ="select * from ( "
  		+ "select temp1.ID, temp1.icdcm1 as ICDCM, temp1.ICCOUNT, temp1.DRUG_NO as DRUGNO, temp2.count as ICOUNT , (temp1.iccount / temp2.count) * 100 as PERCENT from  "
  		+ "(select mr.id,  mr.ICDCM1, count(mr.ICDCM1) ICCOUNT, op.DRUG_NO  from op_p op , mr  "
  		+ "where op.mr_id = mr.id and mr.mr_date between ?1 and ?2 and length(op.drug_no) = 10  and mr.code_all like concat('%', op.drug_no, '%') "
  		+ "group by mr.icdcm1, op.DRUG_NO, mr.id) temp1, (select ICDCM1, count(ICDCM1) as COUNT from mr where  mr_date between ?1 and ?2 and id in (select mr_id from op_p where length(drug_no) = 10)  "
  		+ "group by  ICDCM1) temp2 "
  		+ "where temp1.icdcm1 = temp2.icdcm1 "
  		+ "order by temp1.icdcm1) temp3", nativeQuery = true)
  public List<Map<String,Object>> getIcdcmCountOPByDate2(String sDate, String eDate);
  
  /**
   * 取得主診斷碼出現次數 -住院
   * @param sDate
   * @param eDate
   * @return
   */
  @Query(value ="select temp1.icdcm1 as ICDCM, temp1.ICCOUNT, temp1.ORDER_CODE as DRUGNO, temp2.count as ICOUNT , (temp1.iccount / temp2.count) * 100 as PERCENT from   "
  		+ "(select  mr.ICDCM1, count(mr.ICDCM1) ICCOUNT, ip.ORDER_CODE  from ip_p ip , mr   "
  		+ "where ip.mr_id = mr.id and mr.mr_date between ?1 and ?2 and length(ip.ORDER_CODE) = 10  and mr.code_all like concat('%', ip.ORDER_CODE, '%')  "
  		+ "group by mr.icdcm1, ip.ORDER_CODE) temp1, (select ICDCM1, count(ICDCM1) as COUNT from mr where  mr_date between ?1 and ?2 and id in (select mr_id from ip_p where length(ORDER_CODE) = 10)   "
  		+ "group by  ICDCM1) temp2  "
  		+ "where temp1.icdcm1 = temp2.icdcm1  "
  		+ "order by temp1.icdcm1", nativeQuery = true)
  public List<Map<String,Object>> getIcdcmCountIPByDate(String sDate, String eDate);
  /**
   * 取得主診斷碼出現次數 -住院
   * @param sDate
   * @param eDate
   * @return
   */
  @Query(value ="select temp1.ID, temp1.icdcm1 as ICDCM, temp1.ICCOUNT, temp1.ORDER_CODE as DRUGNO, temp2.count as ICOUNT , (temp1.iccount / temp2.count) * 100 as PERCENT from   "
  		+ "(select mr.ID, mr.ICDCM1, count(mr.ICDCM1) ICCOUNT, ip.ORDER_CODE  from ip_p ip , mr   "
  		+ "where ip.mr_id = mr.id and mr.mr_date between ?1 and ?2 and length(ip.ORDER_CODE) = 10  and mr.code_all like concat('%', ip.ORDER_CODE, '%')  "
  		+ "group by mr.icdcm1, ip.ORDER_CODE, mr.ID) temp1, (select ICDCM1, count(ICDCM1) as COUNT from mr where  mr_date between ?1 and ?2 and id in (select mr_id from ip_p where length(ORDER_CODE) = 10)   "
  		+ "group by  ICDCM1) temp2  "
  		+ "where temp1.icdcm1 = temp2.icdcm1  "
  		+ "order by temp1.icdcm1", nativeQuery = true)
  public List<Map<String,Object>> getIcdcmCountIPByDate2(String sDate, String eDate);
  
  public List<MR> findByApplYmAndDataFormatOrderById(String applYm, String dataFormat);
  
  public List<MR> findByApplYm(String applYm);
  
  @Query(value = "SELECT * FROM MR WHERE DATA_FORMAT = ?1 AND MR_END_DATE >= ?2 AND MR_END_DATE <= ?3", nativeQuery = true)
  public List<MR> findByMrEndDateAndDataFormatOrderById(String dataFormat, java.util.Date sDate, java.util.Date eDate);
  
  public List<MR> findByInhClinicId(String inhClinicId);
  
  @Query(value = "SELECT * FROM MR WHERE INH_CLINIC_ID IN ?1", nativeQuery = true)
  public List<MR> findByInhClinicId(List<String> inhClinicId); 
  
  @Query(value = "SELECT ID FROM MR WHERE INH_CLINIC_ID = ?1", nativeQuery = true)
  public List<Long> getIdByInhClinicId(String inhClinicId); 
  
  /**
   * 取得藥用碼出現次數 -門診
   * @param sDate
   * @param eDate
   * @return
   */
  @Query(value ="select  mr.ICDCM1, count(mr.ICDCM1) COUNT, op.DRUG_NO from op_p op , mr  "
  		+ "where op.mr_id = mr.id and mr.mr_date between ?1 and ?2 and length(drug_no) = 10    "
  		+ "group by mr.icdcm1", nativeQuery = true)
  public List<Map<String,Object>> getDrugNoCount(String sDate, String eDate);
  
  /**
   * 取得藥用碼出現次數 -住院
   * @param sDate
   * @param eDate
   * @return
   */
  @Query(value ="select  mr.ICDCM1, count(mr.ICDCM1) COUNT, ip.ORDER_CODE from ip_p ip , mr  "
  		+ "where ip.mr_id = mr.id and mr.mr_date between ?1 and ?2  and length(ip.order_code) = 10    "
  		+ "group by mr.icdcm1", nativeQuery = true)
  public List<Map<String,Object>> getOderCodeCount(String sDate, String eDate);
  
  /**
   * 取得藥用碼出現次數-門診
   * @param sDate
   * @param eDate
   * @return
   */
  @Query(value ="select mr.id, count(mr.id) from op_p op , mr  "
  		+ "where op.mr_id = mr.id and mr.mr_date  between ?1 and ?2  and length(op.drug_no) = 10 "
  		+ "group by mr.id ", nativeQuery = true)
  public List<Map<String,Object>> getIdByDrugNoCount(String sDate, String eDate);
  
  /**
   * 取得藥用碼出現次數-住院
   * @param sDate
   * @param eDate
   * @return
   */
  @Query(value ="select mr.id, count(mr.id) from ip_p ip , mr  "
  		+ "where ip.mr_id = mr.id and mr.mr_date  between ?1 and ?2  and length(ip.order_code) = 10 "
  		+ "group by mr.id ", nativeQuery = true)
  public List<Map<String,Object>> getIdByOderCodeCount(String sDate, String eDate);
  
  /**
   * 取得列在智能提示中的病歷，近一年違規且狀態為待確認
   */
  @Query(value = "select * from mr where mr_date between ?1 and ?2 and code_all like ?3 ", nativeQuery = true)
 public List<MR> getIntelligentMR(String sDate, String eDate, String code);
  
  /**
   * 取得列在智能提示中的病歷，近一年違規且狀態為待確認，門診
   */
   @Query(value = "select * from mr where mr_date between ?1 and ?2 and code_all like ?3 and data_format = '10' ", nativeQuery = true)
  public List<MR> getIntelligentMRO(String sDate, String eDate, String code);
   
   /**
    * 取得列在智能提示中的病歷，近一年違規且狀態為待確認，住院
    */
   @Query(value = "select * from mr where mr_date between ?1 and ?2 and code_all like ?3 and data_format = '20' ", nativeQuery = true)
  public List<MR> getIntelligentMRH(String sDate, String eDate, String code);
  
  /**
   * 取得列在智能提示中的病歷，近一年違規且狀態為待確認and FUNC_TYPEC not in
   */
  @Query(value = "select * from mr where id in (?1) and func_type not in (?2) ", nativeQuery = true)
  public List<MR> getIntelligentMrByFuncName(List<String> mrid, List<String> funcName);
  
  /**
   * 依照rocid & code 取得該筆病歷表
   * @param rocid
   * @param code
   * @param mrid
   * @return
   */
  @Query(value = "select * from mr where ROC_ID = ?1 and CODE_ALL like concat('%', ?2, '%') and id in(?3) limit 1", nativeQuery = true)
  public MR getMrByRocIdAndCode(String rocid, String code, List<String> mrid);
  /**
   * 依照id & code 取得該筆病歷表
   * @param mrid
   * @param code
   * @return
   */
  @Query(value = "select ROC_ID, count(ROC_ID) as COUNT from mr where ID in (?1) and CODE_ALL like concat('%',?2,'%') group by ROC_ID ", nativeQuery = true)
  public List<Map<String,Object>> getRocListByIdAndCode(List<String> mrid, String code);
  /**
   * 取得最新病例的身分號和日期
   * @param code
   * @return
   */
  @Query(value = "select ROC_ID, max(MR_DATE) as MR_DATE from mr where CODE_ALL like concat('%',?1,'%') group by ROC_ID", nativeQuery = true)
  public List<Map<String,Object>> getRocLastDayListByIdAndCode(String code);
  /**
   * 取得日期間的該準則資料
   * @param code
   * @param sdate
   * @param edate
   * @param rocid
   * @return
   */
  @Query(value = "select ROC_ID, count(ROC_ID) as COUNT from mr where  CODE_ALL like concat('%',?1,'%') and ROC_ID = ?2 and  MR_DATE between ?3 and ?4  group by ROC_ID", nativeQuery = true)
  public Map<String,Object> getRocCountListByCodeAndDate(String code, String rocid, String sdate, String edate);
  /**
   * 取得所有資料，以準則和身分號為條件
   * @param code
   * @param rocid
   * @return
   */
  @Query(value = "select * from mr where CODE_ALL like concat('%',?1,'%') and ROC_ID = ?2 order by MR_DATE desc", nativeQuery = true)
  public List<MR> getAllByCodeAndRocid(String code, String rocid);
  /**
   * 依照以下條件取得MR資料
   * @param icdcm
   * @param dataformat
   * @param sDate
   * @param eDate
   * @param code
   * @return
   */
  @Query(value = "select * from mr where ICDCM1 = ?1 and DATA_FORMAT = ?2 and mr_date between  ?3 and  ?4  and code_all like concat('%', ?5, '%')", nativeQuery = true)
  public List<MR> getDataByParams(String icdcm, String dataformat, String sDate, String eDate, String code);
  
  /**
   * 門診
   * 該支付準則意思是若病歷有A此醫令(在 MR.CODE_ALL或 OP_P.DRUG_NO)，其他醫令的醫令類別不可以是調劑費。
   * @param mrid
   * @return
   */
  @Query(value = "select distinct temp.id from "
  		+ "(select mr.* from mr, op_p "
  		+ "where mr.id = op_p.mr_id and mr.code_all like concat(concat('%',op_p.drug_no), '%') and mr.id in (?1))temp, pay_code "
  		+ "where temp.code_all like concat(concat('%',pay_code.code), '%') and pay_code.code_type = ?2 ", nativeQuery = true)
  public List<Map<String,Object>> getIdByOPandPaycode(List<String> mrid, String codeType);
  /**
   * 住院
   * 該支付準則意思是若病歷有A此醫令(在 MR.CODE_ALL或 OP_P.DRUG_NO)，其他醫令的醫令類別不可以是調劑費。
   * @param mrid
   * @return
   */
  @Query(value = "select distinct temp.id from "
  		+ "(select mr.* from mr, ip_p "
  		+ "where mr.id = ip_p.mr_id and mr.code_all like concat('%',ip_p.order_code, '%') and mr.id in (?1))temp, pay_code "
  		+ "where temp.code_all like concat('%',pay_code.code, '%') and pay_code.code_type = ?2 ", nativeQuery = true)
  
  public List<Map<String,Object>> getIdByIPandPaycode(List<String> mrid, String codeType);

  @Query(value = "SELECT ID, ICDCM1, ICDCM_OTHERS, ICDPCS FROM MR ORDER BY ID", nativeQuery = true)
  public List<Map<String,Object>> getICDALL();
  
  @Transactional
  @Modifying
  @Query(value = "UPDATE MR SET ICD_ALL=?1 WHERE ID=?2", nativeQuery = true)
  public void updateICDALL(String icdall, Long id);

  /**
   * 依照rocid & code 取得該筆病歷表
   * @param rocid
   * @param code
   * @param mrid
   * @return
   */
  @Query(value = "SELECT * FROM mr WHERE ICDCM1 in (?1) AND DATA_FORMAT = ?2 AND MR_END_DATE BETWEEN ?3 AND ?4", nativeQuery = true)
  public List<MR> getMrByIcdcm(List<String> icdcm, String dataFormat, Date startDate, Date endDate);
  
  @Query(value = "SELECT DISTINCT(APPL_YM) AS APPL_YM FROM mr ORDER BY APPL_YM", nativeQuery = true)
  public List<Map<String, Object>> getAllApplYm();
  
  @Query(value = "SELECT * FROM mr WHERE DATA_FORMAT = ?1 AND MR_END_DATE >= ?2 AND MR_END_DATE <= ?3", nativeQuery = true)
  public List<MR> getByDataFormatAndMrDateBetween(String dataFormat, java.util.Date startDate, java.util.Date endDate);
  
  /**
   * 取得所有使用該醫令的病歷數
   * @param code
   * @return
   */
  @Query(value = "SELECT COUNT(1) FROM mr WHERE CODE_ALL LIKE ?1", nativeQuery = true)
  public List<Long> getCountByCodeLike(String code);
  
  /**
   * 取得指定時間內所有使用該醫令的病歷
   * @param code
   * @return
   */
  public List<MR> findByCodeAllContaining(String code);

  @Query(value = "SELECT * FROM mr WHERE CODE_ALL LIKE ?1 AND MR_END_DATE >= ?2 "
      + " AND MR_END_DATE <= ?3", nativeQuery = true)
  public List<MR> getMRByCodeLikeAndMrEndDate(String code, java.util.Date startDate, java.util.Date endDate);

  @Query(value = "SELECT * FROM mr WHERE UPDATE_AT > ?1", nativeQuery = true)
  public List<MR> getTodayUpdatedMR(Date date);
  
  /**
   * 找出該病患使用該組醫令次數
   * @param orderCode
   * @return
   */
  @Query(value = "SELECT ROC_ID, COUNT(ROC_ID) FROM MR WHERE CODE_ALL LIKE ?1 "
      + " AND ROC_ID IN ?2 GROUP BY ROC_ID", nativeQuery = true)
  public List<Object[]> getRocIdByCodeTimes(String orderCode, List<String> rocIds);
  
  /**
   * 找出在該院使用該組醫令次數超過max次的病患證號
   * @param code
   * @param max
   * @return
   */
  @Query(value = "SELECT a.ROC_ID FROM (SELECT ROC_ID, COUNT(ROC_ID) AS total "
      + "FROM MR WHERE CODE_ALL LIKE ?1 GROUP BY ROC_ID) A WHERE total > ?2", nativeQuery = true)
  public List<String> getRocIdByCodeTimes(String code, int max);
  
  /**
   * 取得該科含指定醫令的病歷數及該科的所有病歷數
   * @param mrIdList
   * @param funcType
   * @param orderCode
   * @return
   */
  @Query(value = "SELECT a.use_order_code, b.total_mr FROM " + 
      "(SELECT count(id) AS use_order_code FROM mr WHERE APPL_YM = ?1 " + 
      "AND FUNC_TYPE = ?2 AND CODE_ALL LIKE ?3) a," + 
      "(SELECT count(id) AS total_mr FROM mr WHERE APPL_YM = ?1 " + 
      "AND FUNC_TYPE = ?2) b", nativeQuery = true)
  public List<Object[]> getMrCountByFuncTypeAndOrderCode(String applYm, String funcType, String orderCode);
  
  /**
   * 取得同一病患使用該支付代碼(注射)的間隔日期
   * @param orderCode
   * @return
   */
  @Query(value = "SELECT ID, ROC_ID, MR_END_DATE FROM mr WHERE CODE_ALL LIKE ?1 ORDER BY ROC_ID , MR_END_DATE ", nativeQuery = true)
  public List<Object[]> getMrEndDateByOrderCode(String orderCode);
  
  /**
   * 取得同一病患使用該支付代碼(注射)的間隔日期
   * @param orderCode
   * @return
   */
  @Query(value = "SELECT ID, ROC_ID, MR_END_DATE FROM mr WHERE CODE_ALL LIKE ?1 AND "
      + "MR_END_DATE >= ?2 AND MR_END_DATE <= ?3 ORDER BY ROC_ID, MR_END_DATE ", nativeQuery = true)
  public List<Object[]> getMrEndDateByOrderCode(String orderCode, Date startDate, Date endDate);
  
  /**
   * 取得同一病患使用該支付代碼年月。
   * sample:SELECT ROC_ID, APPL_YM FROM mr WHERE CODE_ALL LIKE '%,P1409C,%' AND ROC_ID IS NOT NULL ORDER BY ROC_ID
   * @param orderCode
   * @param rocIdList
   * @return
   */
  @Query(value = "SELECT ROC_ID, MR_END_DATE, APPL_YM, ID FROM mr WHERE CODE_ALL "
      + "LIKE ?1 AND ROC_ID IN ?2 ORDER BY ROC_ID, MR_END_DATE ", nativeQuery = true)
  public List<Object[]> getRocIdAndUseCountAndApplYm(String orderCode, List<String> rocIdList);

  @Query(value = "SELECT * FROM mr WHERE UPDATE_AT  >= CURRENT_DATE", nativeQuery = true)
  public List<MR> getTodayUpdatedMR();

  @Query(value = "SELECT * FROM mr WHERE INH_CLINIC_ID IN ?1", nativeQuery = true)
  public List<MR> getMrByInhClinicId(List<String> inhClinicId);

  @Query(value = "SELECT * FROM mr WHERE ID IN ?1", nativeQuery = true)
  public List<MR> getMrByIdList(List<Long> idList);
  
  public List<MR> findByDataFormat(String dataFormat);
  
  @Query(value = "SELECT MR.* FROM  MR INNER JOIN OP_D ON MR.ID = OP_D.MR_ID WHERE MR.DATA_FORMAT = '10' AND MR.APPL_YM = ?1 AND OP_D.CASE_TYPE = ?2 AND OP_D.SEQ_NO = ?3", nativeQuery = true)
  public List<MR> findByMrDataFormat10AndMrApplYmAndOpdCaseTypeAndOpdSeqNo(String applYm, String caseType, String seqNo);
  
  @Query(value = "SELECT MR.* FROM  MR INNER JOIN IP_D ON MR.ID = IP_D.MR_ID WHERE MR.DATA_FORMAT = '20' AND MR.APPL_YM = ?1 AND IP_D.CASE_TYPE = ?2 AND IP_D.SEQ_NO = ?3", nativeQuery = true)
  public List<MR> findByMrDataFormat20AndMrApplYmAndIpdCaseTypeAndIpdSeqNo(String applYm, String caseType, String seqNo);
  
}
