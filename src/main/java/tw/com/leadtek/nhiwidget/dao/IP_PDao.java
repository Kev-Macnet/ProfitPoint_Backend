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
  @Query(value = "SELECT IP_P.PAY_CODE_TYPE , SUM(TOTAL_DOT), COUNT(1) FROM MR, IP_P " + 
      "WHERE MR_DATE >= ?1 AND MR_DATE <= ?2 AND IP_P.MR_ID = MR.ID AND TOTAL_DOT > 0 GROUP BY IP_P.PAY_CODE_TYPE", nativeQuery = true)
  public List<Object[]> findPointGroupByPayCodeType(Date sdate, Date edate);
  
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
 	@Query(value = "select ICDCM1 as ICDCM, ORDER_CODE, '20' as DATA_FORMAT, CAST(mrCount AS DECIMAL(10,6)) as AVERAGE, CAST(if(mrCount < 30, 0, up) AS DECIMAL(10,6)) as ULIMIT, CAST(if(mrCount < 30, 0, down) AS DECIMAL(10,6)) as LLIMIT from ( "
 			+ "	select  ICDCM1,ORDER_CODE, avg +2*STDDEV as up, avg -2*STDDEV as down, mrCount from (  "
 			+ "	select m.ICDCM1, ORDER_CODE,AVG(ip.ORDER_CODE) as AVG, STDDEV(ip.ORDER_CODE) as STDDEV, count(m.ID) as mrCount from ip_p ip "
 			+ "	join mr m on m.id = ip.MR_ID and m.MR_DATE > '2020-03-30' "
 			+ "	where ip.PAY_CODE_TYPE  in ('1','2', '3', '4', '5') "
 			+ "	group by m.icdcm1, ip.ORDER_CODE "
 			+ "	) temp "
 			+ "	) report where down >=0", nativeQuery = true)
 	public List<Map<String, Object>> calculate(String date);
 	
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
}
