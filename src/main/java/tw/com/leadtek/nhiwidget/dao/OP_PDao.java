/**
 * Created on 2021/1/25.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import tw.com.leadtek.nhiwidget.model.rdb.OP_P;

public interface OP_PDao extends JpaRepository<OP_P, Long> {

  public List<OP_P> findByOpdIdOrderByOrderSeqNo(Long opdId);
  
  public List<OP_P> findByMrId(Long mrId);
  
  @Query(value = "SELECT * FROM OP_P WHERE OPD_ID IN (SELECT D_ID FROM MR WHERE DATA_FORMAT = ?1 AND MR_END_DATE BETWEEN ?2 AND ?3) ", nativeQuery = true)
  public List<OP_P> findByOpdIDFromMR(String dataFormat, java.util.Date sDate, java.util.Date eDate);
  
  @Query(value = "SELECT * FROM OP_P WHERE MR_ID IN (SELECT ID FROM MR "
      + "WHERE DATA_FORMAT = '10' AND MR_END_DATE >= ?1 AND MR_END_DATE <= ?2) ", nativeQuery = true)
  public List<OP_P> getByMrIdFromMR(java.util.Date sDate, java.util.Date eDate);
  
  @Query(value = "SELECT ID, OPD_ID, ORDER_SEQ_NO FROM OP_P WHERE OPD_ID IN "
      + "(SELECT ID FROM OP_D WHERE OPT_ID = ?1) ORDER BY OPD_ID, ORDER_SEQ_NO", nativeQuery = true)
  public List<Object[]> findByOptId(Long optId);
  
  @Query(value = "SELECT DISTINCT (PRSN_ID) , FUNC_TYPE "
      + "FROM OP_P WHERE PRSN_ID IS NOT NULL AND FUNC_TYPE IS NOT NULL", nativeQuery = true)
  public List<Object[]> findDepartmentAndDoctor();
  
  /**
   * 門診各醫令類別點數
   * @return [醫令代碼, 加總點數, 件數]
   */
  @Query(value = "SELECT OP_P.PAY_CODE_TYPE, SUM(TOTAL_DOT), COUNT(MR.ID) FROM MR, OP_P " + 
      "WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND OP_P.MR_ID = MR.ID AND TOTAL_DOT > 0 GROUP BY OP_P.PAY_CODE_TYPE", nativeQuery = true)
  public List<Object[]> findPointGroupByPayCodeType(Date startDate, Date endDate);
  
  /**
   * 門診各醫令類別點數列表
   * @return [醫令代碼, 加總點數, 件數]
   */
  @Query(value = "SELECT OP_P.PAY_CODE_TYPE, SUM(TOTAL_DOT), COUNT(MR.ID) FROM MR, OP_P " + 
      "WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND OP_P.MR_ID = MR.ID AND TOTAL_DOT > 0 AND MR.FUNC_TYPE = ?3 GROUP BY OP_P.PAY_CODE_TYPE", nativeQuery = true)
  public List<Object[]> findPointAndFuncTypeGroupByPayCodeType(Date startDate, Date endDate, String funcType);
  
  /**
   * 門診各醫令類別自費點數
   * @return [醫令代碼, 加總點數, 件數]
   */
  @Query(value = "SELECT OP_P.PAY_CODE_TYPE, SUM(OWN_EXPENSE), COUNT(1) FROM MR, OP_P " + 
      "WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND OP_P.MR_ID = MR.ID AND TOTAL_DOT > 0 "
      + "AND MR.OWN_EXPENSE > 0 GROUP BY OP_P.PAY_CODE_TYPE", nativeQuery = true)
  public List<Object[]> findOwnExpensePointGroupByPayCodeType(Date startDate, Date endDate);
  
  /**
   * 門診各醫令類別自費點數
   * @return [醫令代碼, 加總點數, 件數]
   */
  @Query(value = "SELECT OP_P.PAY_CODE_TYPE, SUM(OWN_EXPENSE), COUNT(1) FROM MR, OP_P " + 
      "WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND OP_P.MR_ID = MR.ID AND TOTAL_DOT > 0 "
      + "AND MR.OWN_EXPENSE > 0 AND MR.FUNC_TYPE = ?3 GROUP BY OP_P.PAY_CODE_TYPE", nativeQuery = true)
  public List<Object[]> findOwnExpensePointAndFuncTypeGroupByPayCodeType(Date startDate, Date endDate,String funcType);
  
  /**
   *  取得醫令碼與支付標準代碼相同的所有醫令
   * @return [醫令代碼]
   */
  @Query(value = "SELECT DISTINCT(DRUG_NO) FROM OP_P " + 
      "WHERE DRUG_NO IN (SELECT CODE FROM PAY_CODE) AND PAY_CODE_TYPE IS NULL", nativeQuery = true)
  public List<Object[]> findDistinctDrugNo();
  
  @Transactional
  @Modifying
  @Query(value = "UPDATE OP_P SET PAY_CODE_TYPE=?1 WHERE DRUG_NO=?2", nativeQuery = true)
  public void updatePayCodeType(String payCodeType, String drugNo);
  
  /**
   * 應用比例偏高：取得單月申報總數量
   * @param applYm
   * @param drugNo
   * @return
   */
  @Query(value = "SELECT COUNT(1) FROM OP_P WHERE MR_ID IN ("
      + "SELECT id FROM mr WHERE APPL_YM =?1) AND DRUG_NO =?2", nativeQuery = true)
  public Long countOrderByDrugNoAndApplYm(String applYm, String drugNo);
  
  /**
   * 應用比例偏高：取得六個月申報總數量
   * 
   * @param applYm
   * @param drugNo
   * @return
   */
  @Query(
      value = "SELECT COUNT(1) FROM OP_P WHERE MR_ID IN ("
          + "SELECT id FROM mr WHERE APPL_YM = ?1 OR APPL_YM = ?2 OR APPL_YM = ?3 "
          + "OR APPL_YM = ?4 OR APPL_YM = ?5 OR APPL_YM = ?6 ) AND DRUG_NO =?7",
      nativeQuery = true)
  public Long countOrderByDrugNoAnd6ApplYm(String applYm1, String applYm2, String applYm3,
      String applYm4, String applYm5, String applYm6, String drugNo);
  
  /**
   * 取得指定申報年月的所有OPP
   * @param applYm
   * @param drugNo
   * @return
   */
  @Query(value = "SELECT * FROM OP_P WHERE MR_ID IN ("
      + "SELECT id FROM mr WHERE APPL_YM =?1) ORDER BY OPD_ID , ORDER_SEQ_NO DESC", nativeQuery = true)
  public List<OP_P> findByApplYM(String applYm);
  
  @Transactional
  @Modifying
  @Query(value = "UPDATE OP_P SET PAY_CODE_TYPE = '20' WHERE PAY_CODE_TYPE IS NULL", nativeQuery = true)
  public void updatePayCodeType20();
  
  /**
   * 取得門診需排除計算的醫令 (排除診察費、病房費)
   * @return
   */
  @Query(value = "SELECT DISTINCT(DRUG_NO) FROM op_p WHERE PAY_CODE_TYPE  in ('1','2', '3', '4', '5')", nativeQuery = true)
  public List<String> getOPPs();
  
  /**
	 * 計算門診近一年的所有主診斷碼及相關醫令出現次數，並算出平均數與標準差值，將結果寫入 ICDCM_ORDER table。若主診斷碼與醫令出現在近一年病歷次數小於30次，則上下限值存0，不予計算
	 * @param dataFormat
	 * @return
	 */
	@Query(value = "SELECT DRUG_NO,ICDCM1,AVG, AVG + 2 * STD as UP, AVG -2 * STD AS DOWN, MR_COUNT FROM ( "
			+ "SELECT drug_no,icdcm1, COUNT(ICDCM1) AS MR_COUNT , AVG(drug_no_count) AS AVG, STDDEV(drug_no_count) STD FROM ( "
			+ "SELECT op.DRUG_NO, count(op.DRUG_NO) AS drug_no_count , op.MR_ID, mr.ICDCM1 icdcm1 FROM op_p OP, MR "
			+ "WHERE op.MR_ID = MR.ID  and MR.MR_DATE BETWEEN ?1 AND ?2 " 
			+ "GROUP BY op.DRUG_NO, op.MR_ID, MR.ICDCM1 ORDER BY DRUG_NO "
			+ ") TEMP "
			+ "GROUP BY DRUG_NO , icdcm1 ORDER BY DRUG_NO , icdcm1 ) temp2 "
			+ "WHERE avg > 1 AND MR_COUNT >= 30 "
			+ "GROUP BY ICDCM1, DRUG_NO, AVG , STD, MR_COUNT ORDER BY AVG DESC "
			+ "", nativeQuery = true)
	public List<Map<String, Object>> calculate(String sDate, String eDate);
	
	/**
 	 * 由支付準則代碼和MRid查詢
 	 * @param code
 	 * @param mrid
 	 * @return
 	 */
 	@Query(value = "select opp.DRUG_NO, opp.MR_ID ,  sum(opp.TOTAL_Q) as TOTAL from op_p opp, op_d opd "
 			+ "where opp.OPD_ID = opd.id and opd.CASE_TYPE = '02' and DRUG_NO = ?1 and opp.MR_ID in (?2) "
 			+ "group by opp.DRUG_NO, opp.MR_ID", nativeQuery = true)
 	public List<Map<String, Object>> getListByDrugNoAndMrid(String code, List<String> mrid);
 	
 	/**
 	 * 由支付準則代碼和MRid查詢
 	 * @param code
 	 * @param mrid
 	 * @return
 	 */
 	@Query(value = "select  DRUG_NO, MR_ID ,  sum(TOTAL_Q) as TOTAL from op_p where DRUG_NO = ?1 and mr_id in (?2) group by DRUG_NO, MR_ID", nativeQuery = true)
 	public List<Map<String, Object>> getListByDrugNoAndMrid2(String code, List<String> mrid);
 	
 	/**
 	 * 由支付準則代碼和MRid查詢，查詢使用次數
 	 * @param code
 	 * @param mrid
 	 * @return
 	 */
 	@Query(value = "select opp.DRUG_NO, opd.ROC_ID, count(opd.ROC_ID) as COUNT, opd.MR_ID from op_d opd, op_p opp "
 			+ "where opp.OPD_ID = opd.id and opp.DRUG_NO = ?1 and opp.MR_ID in (?2) "
 			+ "group by opp.DRUG_NO, opd.ROC_ID, opd.MR_ID", nativeQuery = true)
 	public List<Map<String, Object>> getListCountByDrugNoAndMrid(String code, List<String> mrid);
 	/**
 	 * 由支付準則代碼和MRid查詢，查詢一天之內次數
 	 * @param code
 	 * @param mrid
 	 * @return
 	 */
 	@Query(value = "select  DRUG_NO, MR_ID ,  sum(TOTAL_Q) as TOTAL from op_p where (END_TIME - START_TIME) <= 10000  and DRUG_NO = ?1 and MR_ID in (?2) group by DRUG_NO, MR_ID", nativeQuery = true)
 	public List<Map<String, Object>> getListOneDayByDrugNoAndMrid(String code, List<String> mrid); 
 	
 	/**
 	 * 由支付準則代碼和MRid和天數查詢，查詢特定天之內次數
 	 * @param days
 	 * @param code
 	 * @param mrid
 	 * @return
 	 */
 	@Query(value = "select  DRUG_NO, MR_ID ,  sum(TOTAL_Q) as TOTAL from op_p where (END_TIME - START_TIME) <= ?1 * 10000  and DRUG_NO = ?2 and MR_ID in (?3) group by DRUG_NO, MR_ID", nativeQuery = true)
 	public List<Map<String, Object>> getListByDrugNoAndMridAndDays(int days, String code, List<String> mrid);
 	
 	/**
 	 * 由支付準則代碼和MRid和天數查詢，查詢特定天之內次數
 	 * @param days
 	 * @param code
 	 * @param mrid
 	 * @return
 	 */
 	@Query(value = "select opp.DRUG_NO, opd.ROC_ID ,  sum(opp.TOTAL_Q) as TOTAL from op_p opp, op_d opd  "
 			+ "where opp.OPD_ID = opd.id and  (opp.END_TIME - opp.START_TIME) <= ?1 * 10000 and DRUG_NO = ?2 and opp.MR_ID in (?3) "
 			+ "group by opp.DRUG_NO, opd.ROC_ID", nativeQuery = true)
 	public List<Map<String, Object>> getListRocIdByDrugNoAndMridAndDays(int days, String code, List<String> mrid);
 	/**
 	 * 取得準則區間門診數
 	 * @param mrid
 	 * @return
 	 */
 	@Query(value = "select  mr.roc_id as ROC_ID, sum(op_p.total_q) TOTAL  from op_p,mr "
 			+ "where op_p.mr_id = mr.id and mr.mr_date  between ?1 and ?2 and op_p.drug_no = ?3 "
 			+ "group by mr.roc_id", nativeQuery = true)
 	public List<Map<String,Object>> getPerMonthByDrugNoAndTotal(String sDate, String eDate, String code);
 	/**
 	 * 該準則門診總人數
 	 * @param sDate
 	 * @param eDate
 	 * @param code
 	 * @return
 	 */
 	@Query(value = "select  count(*) COUNT from op_p,mr "
 			+ "where op_p.mr_id = mr.id and mr.mr_date  between ?1 and ?2 and op_p.drug_no = ?3 ", nativeQuery = true)
 	public Map<String,Object> getTotalDrugByNo(String sDate, String eDate, String code);
 	
 	/**
 	 * 藥用計算超過天數
 	 * @param days
 	 * @param code
 	 * @param mrid
 	 * @return
 	 */
 	@Query(value = "select DRUG_NO , TOTAL_Q, MR_ID, START_TIME, END_TIME, (END_TIME - START_TIME) / 10000 as DIFF "
 			+ "from op_p where (END_TIME - START_TIME) > ?1 * 10000 and DRUG_NO = ?2 and MR_ID in (?3) order by MR_ID", nativeQuery = true)
 	public List<Map<String, Object>> getListByDaysAndCodeAndMrid(int days, String code, List<String> mrid);
 	
 	/**
 	 * 取得同drungNo的rocid筆數資料
 	 * @param durgNo
 	 * @param mrid
 	 * @return
 	 */
 	@Query(value = "select opd.ROC_ID, opp.START_TIME, opp.END_TIME, opp.MR_ID, (opp.END_TIME- opp.START_TIME) as DIFF, opp.TOTAL_Q from op_p opp, op_d opd "
 			+ "where opp.OPD_ID = opd.id and opd.ROC_ID in ( "
 			+ "select ROC_ID from ( "
 			+ "select  opd.ROC_ID, count( opd.ROC_ID) as COUNT from op_p opp, op_d opd "
 			+ "where opp.OPD_ID = opd.id  "
 			+ "and opp.DRUG_NO = ?1 and opp.MR_ID in(?2) "
 			+ "group by opd.ROC_ID) temp "
 			+ "where COUNT > 1)  "
 			+ "and opp.DRUG_NO = ?1  order by ROC_ID , END_TIME desc", nativeQuery = true)
 	public List<Map<String, Object>> getRocIdCount(String durgNo, List<String> mrid);
 	
 	/**
 	 * 取得該drugNo的各病例總數
 	 * @param durgNo
 	 * @param mrid
 	 * @return
 	 */
 	@Query(value = "select * from ( "
 			+ "select opd.ROC_ID, count(opd.ROC_ID), sum( opp.TOTAL_Q) as TOTAL from op_p opp, op_d opd "
 			+ "where opp.OPD_ID = opd.ID "
 			+ "and opp.DRUG_NO = ?1 "
 			+ "and opp.MR_ID in (?2) "
 			+ "group by opd.ROC_ID) temp", nativeQuery = true)
 	public List<Map<String, Object>> getRocidTotalByDrugNoandMrid(String durgNo, List<String> mrid);
 	
 	/**
 	 *  取得最新一筆資料
 	 * @param drugNo
 	 * @return
 	 */
 	@Query(value = "select temp.ROC_ID, temp2.START_TIME / 10000 as START_TIME, temp2.END_TIME / 10000 as END_TIME from "
			+ "(select ipd.ROC_ID, max(ipp.END_TIME) as END_TIME from ip_p ipp, ip_d ipd "
			+ "where ipp.ipd_ID = ipd.ID "
			+ "and ipp.ORDER_CODE = ?1 "
			+ "group by ipd.ROC_ID) temp, (select ipd.ROC_ID, ipp.END_TIME, ipp.START_TIME from ip_p ipp, ip_d ipd "
			+ "where ipp.ipd_ID = ipd.ID "
			+ "and ipp.ORDER_CODE = ?1) temp2 "
			+ "where temp.ROC_ID = temp2.ROC_ID and temp.END_TIME = temp2.END_TIME "
			+ "group by temp.ROC_ID ", nativeQuery = true)
 	public List<Map<String, Object>> getLastRocidByOrderCode(String orderCode);
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
 			+ "end as DIFF "
 			+ "from op_p where MR_ID in (?1)", nativeQuery = true)
 	public List<Map<String, Object>> getAllListByMrid(List<String> mrid);
 	/**
 	 * 門急診-取得診療結束人次
 	 * @param date
 	 * @return
 	 */
 	@Query(value="SELECT count(1) FROM OP_D od "
 			+ "WHERE FUNC_END_DATE LIKE CONCAT(?1,'%') ", nativeQuery = true)
 	public int getFuncEndDateCount(String date);
	
 	/**
 	 * 取得目前所有醫令的代碼及醫令類別代碼，排除無類別代碼或4(不得另計價)或代碼長度>=10(藥品、衛材)的醫令。
 	 * 長度10碼為藥品，類別代碼為1，長度12碼為衛材，類別代碼為3。
 	 * @return
 	 */
    @Query(value = "SELECT DISTINCT (DRUG_NO), ORDER_TYPE FROM op_p WHERE ORDER_TYPE IS NOT NULL " + 
        "and ORDER_TYPE <> '4' and length (DRUG_NO) < 10 GROUP BY DRUG_NO , ORDER_TYPE", nativeQuery = true)
    public List<Map<String,Object>> getDrugNoAndOrderType();
    
    @Query(value = "SELECT MR_ID FROM OP_P WHERE ORDER_TYPE=?1 AND MR_ID IN ?2", nativeQuery = true)
    public List<Long> getMrIdByOrderTypeAndMrId(String orderType, List<Long> mrIdList);
    
    /**
     * 取得單一門診就醫紀錄應用數量,超過 max 次數的病歷id
     * @param orderCode
     * @param mrIdList
     * @param max
     * @return
     */
    @Query(value = "SELECT a.MR_ID FROM (" + 
        "SELECT MR_ID, SUM(TOTAL_Q) AS total FROM op_p WHERE DRUG_NO =?1 AND mr_id IN ?2 " + 
        "GROUP BY mr_id) A WHERE total > ?3", nativeQuery = true)
    public List<Object[]> getMrIdByOrderCodeCount(String orderCode, List<Long> mrIdList, int max);
    
    /**
     * 取得醫令的起始與結束時間，計算該筆醫令是否符合需滿n小時或超過n小時不能使用
     * @param orderCode
     * @param mrIdList
     * @return
     */
    @Query(value = "SELECT MR_ID, START_TIME, END_TIME, TOTAL_Q FROM op_p "
        + "WHERE DRUG_NO = ?1 AND MR_ID IN ?2 ORDER BY MR_ID, START_TIME", nativeQuery = true)
    public List<Object[]> getMrIdAndStartTimeAndEndTimeByOrderCodeAndMrIdList(String orderCode, List<Long> mrIdList);
    
    /**
     * 取得相差分鐘資料
     * @param mrid
     * @return
     */
    @Query(value = "SELECT MR_ID, (END_TIME  - START_TIME ) AS DIFF FROM op_p "
        + "WHERE DRUG_NO = ?1 AND MR_ID in ?2 ORDER BY MR_ID", nativeQuery = true)
    public List<Object[]> getOrderCodeTimeDiffByMrid(String drugNo, List<Long> mrid);
    
    /**
     * 取得drugNo1或drugNo2醫令的個數
     * @param drugNo
     * @param inhCode
     * @param mrIdList
     * @return
     */
    @Query(value = "SELECT MR_ID, DRUG_NO, INH_CODE, TOTAL_Q FROM op_p "
        + "WHERE (DRUG_NO = ?1 OR INH_CODE = ?2 ) AND MR_ID IN ?3 ORDER BY MR_ID", nativeQuery = true)
    public List<Object[]> getMrIdAndDrugNoAndTotalQByMrIdList(String drugNo, String inhCode, List<Long> mrIdList);
    
    @Query(value = "SELECT * FROM op_p WHERE MR_ID IN ?1 ORDER BY MR_ID", nativeQuery = true)
    public List<OP_P> getOppListByMrIdList(List<Long> mrIdList);
    
    /**
     * 取得醫令的起始與結束時間
     * @param orderCode
     * @param mrIdList
     * @return
     */
    @Query(value = "SELECT MR_ID, DRUG_NO, START_TIME FROM op_p "
        + "WHERE DRUG_NO IN ?1 AND MR_ID IN ?2 AND ORDER_TYPE <> '4' ORDER BY MR_ID, DRUG_NO", nativeQuery = true)
    public List<Object[]> getMrIdAndOrderCodeAndStartTimeByMrIdAndOrderCode(List<String> orderCodes, List<Long> mrId);

}
