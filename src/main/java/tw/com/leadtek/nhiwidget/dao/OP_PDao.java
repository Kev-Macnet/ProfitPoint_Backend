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
  
  @Query(value = "SELECT * FROM OP_P WHERE OPD_ID IN (SELECT D_ID FROM MR WHERE DATA_FORMAT = ?1 AND MR_DATE BETWEEN ?2 AND ?3) ", nativeQuery = true)
  public List<OP_P> findByOpdIDFromMR(String dataFormat, Date sDate, Date eDate);
  
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
  @Query(value = "SELECT OP_P.PAY_CODE_TYPE, SUM(TOTAL_DOT), COUNT(1) FROM MR, OP_P " + 
      "WHERE MR_DATE >= ?1 AND MR_DATE <= ?2 AND OP_P.MR_ID = MR.ID AND TOTAL_DOT > 0 GROUP BY OP_P.PAY_CODE_TYPE", nativeQuery = true)
  public List<Object[]> findPointGroupByPayCodeType(Date startDate, Date endDate);
  
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
	@Query(value = "select ICDCM1 as ICDCM, DRUG_NO as ORDER_CODE, '10' as DATA_FORMAT, CAST(mrCount AS DECIMAL(10,6)) as AVERAGE, CAST(if(mrCount < 30, 0, up) AS DECIMAL(10,6)) as ULIMIT, CAST(if(mrCount < 30, 0, down) AS DECIMAL(10,6)) as LLIMIT from ( "
			+ "select ICDCM1,DRUG_NO, avg +2*STDDEV as up, avg -2*STDDEV as down, mrCount from ( "
			+ "select m.ICDCM1, DRUG_NO,AVG(op.DRUG_NO) as AVG, STDDEV(op.DRUG_NO) as STDDEV, count(m.ID) as mrCount from op_p op "
			+ "join mr m on m.id = op.MR_ID and m.MR_DATE > ?1 "
			+ "where op.PAY_CODE_TYPE  in ('1','2', '3', '4', '5') "
			+ "group by m.icdcm1, op.DRUG_NO "
			+ ") temp "
			+ ") report where down >=0", nativeQuery = true)
	public List<Map<String, Object>> calculate(String date);
}
