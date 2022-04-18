/**
 * Created on 2021/1/27.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import tw.com.leadtek.nhiwidget.model.rdb.IP_P;
import tw.com.leadtek.nhiwidget.model.rdb.OP_P;

public interface IP_PDao extends JpaRepository<IP_P, Long> {

  public List<IP_P> findByIpdId(Long ipdId);
  
  public List<IP_P> findByMrId(Long mrId);
  
  public List<IP_P> findByIpdIdOrderByOrderSeqNo(Long ipdId);
  
  @Query(value = "SELECT * FROM IP_P WHERE IPD_ID IN (SELECT D_ID FROM MR WHERE DATA_FORMAT = ?1 AND MR_DATE BETWEEN ?2 AND ?3) ", nativeQuery = true)
  public List<IP_P> findByIpdIDFromMR(String dataFormat, Date sDate, Date eDate);
  
  @Query(value = "SELECT ID, IPD_ID, ORDER_SEQ_NO FROM IP_P WHERE IPD_ID IN "
      + "(SELECT ID FROM IP_D WHERE IPT_ID = ?1) ORDER BY IPD_ID, ORDER_SEQ_NO", nativeQuery = true)
  public List<Object[]> findByIptId(Long iptId);
  
  @Query(value = "SELECT DISTINCT (PRSN_ID) , CON_FUNC_TYPE "
      + "FROM IP_P WHERE PRSN_ID IS NOT NULL AND CON_FUNC_TYPE IS NOT NULL", nativeQuery = true)
  public List<Object[]> findDepartmentAndDoctor();
  
  /**
   * 找出虛擬醫令 G00001, J00001, H000開頭的點數值
   * @param mrId
   * @return
   */
  @Query(value = "SELECT ORDER_CODE, TW_DRGS_CALCU, TOTAL_DOT FROM IP_P WHERE " + 
      "MR_ID=?1 AND ((TW_DRGS_CALCU > 0 AND (ORDER_CODE = 'G00001' OR ORDER_CODE LIKE 'H000%' or ORDER_CODE = 'J00001')) "
      + "OR ORDER_TYPE = 'X')", nativeQuery = true)
  public List<Object[]> findVirtualCodeByMrId(Long mrId);
  
  /**
   * 住院各醫令類別點數
   * @param sdate
   * @param edate
   * @return [醫令代碼, 加總點數, 件數]
   */
  @Query(value = "SELECT IP_P.PAY_CODE_TYPE , SUM(TOTAL_DOT), COUNT(MR.ID) FROM MR, IP_P " + 
      "WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND IP_P.MR_ID = MR.ID AND TOTAL_DOT > 0 GROUP BY IP_P.PAY_CODE_TYPE", nativeQuery = true)
  public List<Object[]> findPointGroupByPayCodeType(Date sdate, Date edate);
  
  /**
   * 住院各醫令類別自費點數
   * @param sdate
   * @param edate
   * @return [醫令代碼, 加總點數, 件數]
   */
  @Query(value = "SELECT IP_P.PAY_CODE_TYPE , SUM(IP_P.TOTAL_DOT), COUNT(MR.ID) FROM MR, IP_P " + 
      "WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND IP_P.MR_ID = MR.ID AND IP_P.TOTAL_DOT > 0 "
      + "AND IP_P.ORDER_TYPE='E' GROUP BY IP_P.PAY_CODE_TYPE", nativeQuery = true)
  public List<Object[]> findOwnExpenseGroupByPayCodeType(Date sdate, Date edate);
  
  /**
   *  取得醫令碼與支付標準代碼相同的所有醫令
   * @return [醫令代碼]
   */
  @Query(value = "SELECT DISTINCT(ORDER_CODE) FROM IP_P " + 
      "WHERE ORDER_CODE IN (SELECT CODE FROM PAY_CODE) ", nativeQuery = true)
  public List<Object[]> findDistinctOrderCode();
  
  @Transactional
  @Modifying
  @Query(value = "UPDATE IP_P SET PAY_CODE_TYPE=?1 WHERE ORDER_CODE=?2", nativeQuery = true)
  public void updatePayCodeType(String payCodeType, String orderCode);
  
  /**
   * 應用比例偏高：取得單月申報總數量
   * @param applYm
   * @param drugNo
   * @return
   */
  @Query(value = "SELECT COUNT(1) FROM IP_P WHERE MR_ID IN ("
      + "SELECT id FROM mr WHERE APPL_YM =?1) AND ORDER_CODE =?2", nativeQuery = true)
  public Long countOrderByDrugNoAndApplYm(String applYm, String orderCode);
  
  /**
   * 應用比例偏高：取得六個月申報總數量
   * 
   * @param applYm
   * @param orderCode
   * @return
   */
  @Query(
      value = "SELECT COUNT(1) FROM IP_P WHERE MR_ID IN ("
          + "SELECT id FROM mr WHERE APPL_YM = ?1 OR APPL_YM = ?2 OR APPL_YM = ?3 "
          + "OR APPL_YM = ?4 OR APPL_YM = ?5 OR APPL_YM = ?6 ) AND ORDER_CODE =?7",
      nativeQuery = true)
  public Long countOrderByDrugNoAnd6ApplYm(String applYm1, String applYm2, String applYm3,
      String applYm4, String applYm5, String applYm6, String orderCode);
  
  /**
   * 取得指定申報年月的所有OPP
   * @param applYm
   * @param drugNo
   * @return
   */
  @Query(value = "SELECT * FROM IP_P WHERE MR_ID IN ("
      + "SELECT id FROM mr WHERE APPL_YM =?1) ORDER BY IPD_ID , ORDER_SEQ_NO DESC", nativeQuery = true)
  public List<IP_P> findByApplYM(String applYm);
  
  @Transactional
  @Modifying
  @Query(value = "UPDATE IP_P SET PAY_CODE_TYPE = '20' WHERE PAY_CODE_TYPE IS NULL ", nativeQuery = true)
  public void updatePayCodeType20();
  
  
  /**
 	 * 計算住院近一年的所有主診斷碼及相關醫令出現次數，並算出平均數與標準差值，將結果寫入 ICDCM_ORDER table。若主診斷碼與醫令出現在近一年病歷次數小於30次，則上下限值存0，不予計算
 	 * @param dataFormat
 	 * @return
 	 */
 	@Query(value = "SELECT ORDER_CODE,ICDCM1,AVG, AVG + 2 * STD as UP, AVG -2 * STD AS DOWN, MR_COUNT FROM (  "
 			+ "SELECT ORDER_CODE,icdcm1, COUNT(ICDCM1) AS MR_COUNT , AVG(ORDER_CODE_count) AS AVG, STDDEV(ORDER_CODE_count) STD FROM (  "
 			+ "SELECT ip.ORDER_CODE, count(ip.ORDER_CODE) AS ORDER_CODE_count , ip.MR_ID, mr.ICDCM1 icdcm1 FROM ip_p IP, MR  "
 			+ "WHERE ip.MR_ID = MR.ID  and MR.MR_DATE BETWEEN ?1 AND ?2 "
 			+ "GROUP BY ip.ORDER_CODE, ip.MR_ID, MR.ICDCM1 ORDER BY ORDER_CODE  "
 			+ ") TEMP  "
 			+ "GROUP BY ORDER_CODE , icdcm1 ORDER BY ORDER_CODE , icdcm1 ) temp2  "
 			+ "WHERE avg > 1 AND MR_COUNT >= 30  "
 			+ "GROUP BY ICDCM1, ORDER_CODE, AVG , STD, MR_COUNT ORDER BY AVG DESC", nativeQuery = true)
 	public List<Map<String, Object>> calculate(String sDate, String eDate);
 	
 	/**
 	 * 由支付準則代碼和MRid查詢
 	 * @param code
 	 * @param mrid
 	 * @return
 	 */
 	@Query(value = "select  ORDER_CODE, MR_ID ,  sum(TOTAL_Q) as TOTAL from ip_p where ORDER_CODE = ?1 and mr_id in (?2) group by ORDER_CODE, MR_ID", nativeQuery = true)
 	public List<Map<String, Object>> getListByOrderCodeAndMrid(String code, List<String> mrid);
 	
 	/**
 	 * 由支付準則代碼和MRid查詢，查詢使用次數
 	 * @param code
 	 * @param mrid
 	 * @return
 	 */
 	@Query(value = "select ipp.ORDER_CODE, ipd.ROC_ID, count(ipd.ROC_ID) as COUNT, ipd.MR_ID from ip_d ipd, ip_p ipp "
 			+ "where ipp.iPD_ID = ipd.id  "
 			+ "and ipp.ORDER_CODE = ?1 and ipp.MR_ID in (?2) "
 			+ "group by ipp.ORDER_CODE, ipd.ROC_ID, ipd.MR_ID", nativeQuery = true)
 	public List<Map<String, Object>> getListCountByOrderCodeAndMrid(String code, List<String> mrid);
 	/**
 	 * 取得民國年起迄日
 	 * @param mrid
 	 * @return
 	 */
 	@Query(value = "select temp.MR_ID, ipp.START_TIME / 10000 as START_TIME , ipp.END_TIME / 10000 as END_TIME from ( "
 			+ "select MR_ID , MAX(END_TIME) as END_TIME "
 			+ "from ip_p "
 			+ "group by MR_ID) temp, ip_p ipp "
 			+ "where ipp.MR_ID = temp.MR_ID and ipp.END_TIME = temp.END_TIME and ipp.MR_ID in (?1) "
 			+ "group by temp.MR_ID", nativeQuery = true)
 	public List<Map<String, Object>> getTimeListByMrid(List<String> mrid);
 	/**
 	 * 由支付準則代碼和MRid查詢，查詢一天之內次數
 	 * @param code
 	 * @param mrid
 	 * @return
 	 */
 	@Query(value = "select  ORDER_CODE, MR_ID ,  sum(TOTAL_Q) as TOTAL from ip_p where (END_TIME - START_TIME) <= 10000  and ORDER_CODE = ?1 and MR_ID in (?2) group by ORDER_CODE, MR_ID", nativeQuery = true)
 	public List<Map<String, Object>> getListOneDayByOrderCodeAndMrid(String code, List<String> mrid);
 	
 	/**
 	 * 由支付準則代碼和MRid和天數查詢，查詢特定天之內次數
 	 * @param days
 	 * @param code
 	 * @param mrid
 	 * @return
 	 */
 	@Query(value = "select  ORDER_CODE, MR_ID ,  sum(TOTAL_Q) as TOTAL from ip_p where (END_TIME - START_TIME) <= ?1 * 10000  and ORDER_CODE = ?2 and MR_ID in (?3) group by ORDER_CODE, MR_ID", nativeQuery = true)
 	public List<Map<String, Object>> getListByOrderCodeAndMridAndDays(int days, String code, List<String> mrid);
 	
 	/**
 	 * 由支付準則代碼和MRid和天數查詢，查詢特定天之內次數
 	 * @param days
 	 * @param code
 	 * @param mrid
 	 * @return
 	 */
 	@Query(value = "select ipp.ORDER_CODE, ipd.ROC_ID ,  sum(ipp.TOTAL_Q) as TOTAL from ip_p ipp, ip_d ipd  "
 			+ "where ipp.IPD_ID = ipd.id and  (ipp.END_TIME - ipp.START_TIME) <= ?1 * 10000 and ipp.ORDER_CODE = ?2 and ipp.MR_ID in (?3) "
 			+ "group by ipp.ORDER_CODE, ipd.ROC_ID", nativeQuery = true)
 	public List<Map<String, Object>> getListRocIdByOrderCodeAndMridAndDays(int days, String code, List<String> mrid);
 	
 	/**
 	 * 由支付準則代碼和MRid和天數查詢，查詢超過特定天之內次數
 	 * @param days
 	 * @param code
 	 * @param mrid
 	 * @return
 	 */
 	@Query(value = "select  ORDER_CODE, MR_ID ,  sum(TOTAL_Q) as TOTAL from ip_p where (END_TIME - START_TIME) > ?1 * 10000  and ORDER_CODE = ?2 and MR_ID in (?3) group by ORDER_CODE, MR_ID", nativeQuery = true)
 	public List<Map<String, Object>> getListOverByOrderCodeAndMridAndDays(int days, String code, List<String> mrid);
 	/**
 	 * 藥用計算超過天數
 	 * @param days
 	 * @param code
 	 * @param mrid
 	 * @return
 	 */
 	@Query(value = "select ORDER_CODE, TOTAL_Q, MR_ID, START_TIME, END_TIME, (END_TIME - START_TIME) / 10000 as DIFF "
 			+ "from ip_p where (END_TIME - START_TIME) > ?1 * 10000 and order_code = ?2 and MR_ID in (?3) order by MR_ID", nativeQuery = true)
 	public List<Map<String, Object>> getListByDaysAndCodeAndMrid(int days, String code, List<String> mrid);
 	
 	/**
 	 * 取得同drungNo的rocid筆數資料
 	 * @param durgNo
 	 * @param mrid
 	 * @return
 	 */
 	@Query(value = "select ipd.ROC_ID, ipp.START_TIME, ipp.END_TIME, ipp.MR_ID, (ipp.END_TIME- ipp.START_TIME) as DIFF, ipp.TOTAL_Q from ip_p ipp, ip_d ipd "
 			+ "where ipp.ipd_ID = ipd.id and ipd.ROC_ID in ( "
 			+ "select ROC_ID from ( "
 			+ "select  ipd.ROC_ID, count( ipd.ROC_ID) as COUNT from ip_p ipp, ip_d ipd "
 			+ "where ipp.ipd_ID = ipd.id  "
 			+ "and ipp.ORDER_CODE = ?1  and ipp.mr_id in (?2) "
 			+ "group by ipd.ROC_ID) temp "
 			+ "where COUNT > 1)  "
 			+ "and ipp.ORDER_CODE = ?1 order by ROC_ID , END_TIME desc", nativeQuery = true)
 	public List<Map<String, Object>> getRocIdCount(String durgNo, List<String> mrid);
 	
 	/**
 	 * 取得該orderCode的各病例總數
 	 * @param durgNo
 	 * @param mrid
 	 * @return
 	 */
 	@Query(value = "select * from ( "
 			+ "select ipd.ROC_ID, count(ipd.ROC_ID), sum( ipp.TOTAL_Q) as TOTAL from ip_p ipp, ip_d ipd "
 			+ "where ipp.ipd_ID = ipd.ID "
 			+ "and ipp.ORDER_CODE = ?1 "
 			+ "and ipp.MR_ID in (?2) "
 			+ "group by ipd.ROC_ID) temp", nativeQuery = true)
 	public List<Map<String, Object>> getRocidTotalByDrugNoandMrid(String durgNo, List<String> mrid);
 	/**
 	 * 取得相差分鐘資料
 	 * @param mrid
 	 * @return
 	 */
 	@Query(value = "select MR_ID,TOTAL_Q, START_TIME, END_TIME, "
 			+ "case when (END_TIME - START_TIME) <= 40 then (END_TIME - START_TIME) "
 			+ "when (END_TIME - START_TIME) > 41 and (END_TIME - START_TIME) <= 120 then (END_TIME - START_TIME) - 40 "
 			+ "when (END_TIME - START_TIME) > 121 and (END_TIME - START_TIME) <= 240 then (END_TIME - START_TIME) - 80 "
 			+ "when (END_TIME - START_TIME) > 241 and (END_TIME - START_TIME) <= 360 then (END_TIME - START_TIME) - 120 "
 			+ "else (END_TIME - START_TIME) "
 			+ "end  DIFF "
 			+ "from ip_p where MR_ID in (?1)", nativeQuery = true)
 	public List<Map<String, Object>> getAllListByMrid(List<String> mrid);
}
