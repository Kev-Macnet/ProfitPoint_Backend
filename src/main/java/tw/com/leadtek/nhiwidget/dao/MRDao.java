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
      // 2. 住院申請件數 , 申請總點數\r\n" + 
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
      "(SELECT FUNC_TYPE , COUNT(1) AS DRG_QUANTITY, SUM(APPL_DOT) AS DRG_POINT FROM MR " + 
      " WHERE DRG_SECTION IS NOT NULL AND MR_DATE >= ?1 AND MR_DATE <= ?2 GROUP BY FUNC_TYPE) DRG," + 
      "(SELECT FUNC_TYPE AS NONDRG_FUNC_TYPE, COUNT(1) AS NONDRG_QUANTITY, SUM(APPL_DOT) AS NONDRG_POINT FROM MR " + 
      " WHERE DRG_SECTION IS NULL AND DATA_FORMAT = '20' AND MR_DATE >= ?3 AND MR_DATE <= ?4 " + 
      " AND FUNC_TYPE IN (SELECT DISTINCT(FUNC_TYPE) FROM MR WHERE DRG_SECTION IS NOT NULL " +
      " AND MR_DATE >= ?5 AND MR_DATE <= ?6) GROUP BY FUNC_TYPE) NODRG " +
      "WHERE DRG.FUNC_TYPE = NODRG.NONDRG_FUNC_TYPE", nativeQuery = true)
  public List<Object[]> countDRGPointByStartDateAndEndDate(Date startDate1, Date endDate1, Date startDate2, Date endDate2, 
      Date startDate3, Date endDate3);
  
  /**
   * 取得DRG各科在指定日期區間的件數及點數
   */
  @Query(value ="SELECT FUNC_TYPE , COUNT(1) AS DRG_QUANTITY, SUM(APPL_DOT) AS DRG_POINT FROM MR " + 
      " WHERE DRG_SECTION IS NOT NULL AND MR_DATE >= ?1 AND MR_DATE <= ?2 GROUP BY FUNC_TYPE", nativeQuery = true)
  public List<Object[]> countDRGPointByStartDateAndEndDate(Date startDate1, Date endDate1);
  
  /**
   * 取得非DRG各科在指定日期區間的件數及點數
   */
  @Query(value ="SELECT FUNC_TYPE , COUNT(1) AS NONDRG_QUANTITY, SUM(APPL_DOT) AS NONDRG_POINT FROM MR " + 
      " WHERE DRG_SECTION IS NULL AND DATA_FORMAT = '20' AND MR_DATE >= ?1 AND MR_DATE <= ?2 GROUP BY FUNC_TYPE", nativeQuery = true)
  public List<Object[]> countNonDRGPointByStartDateAndEndDate(Date startDate1, Date endDate1);
  
  /**
   * 取得DRG指定科別在指定日期區間的不同區的件數及點數
   */
  @Query(value="SELECT DRG_SECTION, COUNT(1) AS QUANTITY, SUM(APPL_DOT) AS POINT FROM MR " + 
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
   * 取得指定時間落入DRG的所有科別
   * @param startDate
   * @param endDate
   * @return
   */
  @Query(value="SELECT DISTINCT (FUNC_TYPE) FROM MR WHERE DRG_SECTION IS NOT NULL AND\r\n" + 
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
  @Query(value = "UPDATE MR SET STATUS=?1 WHERE STATUS=?2 AND ID IN "
      + "(SELECT MR_ID FROM INTELLIGENT WHERE CONDITION_CODE=?3 AND REASON_CODE=?4 AND REASON=?5)", nativeQuery = true)
  public void updateMrStatusByIntelligent(int newStatus, int oldStatus, int conditionCode, String reasonCode, String reason);
  
}
