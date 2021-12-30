/**
 * Created on 2021/1/27.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.sql.Date;
import java.util.List;
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
}
