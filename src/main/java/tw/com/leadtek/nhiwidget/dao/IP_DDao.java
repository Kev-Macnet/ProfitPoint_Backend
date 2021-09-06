/**
 * Created on 2021/1/27.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.sql.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import tw.com.leadtek.nhiwidget.model.rdb.IP_D;

public interface IP_DDao extends JpaRepository<IP_D, Long>, JpaSpecificationExecutor<IP_D> {

  public List<IP_D> findByIptId(Long iptId);
  
  @Query(value =  "SELECT SEQ_NO ,ID ,ROC_ID, IN_DATE, MR_ID FROM IP_D WHERE IPT_ID=?1 ", nativeQuery = true)
  public List<Object[]> findByIptIdSimple(Long iptId);
  
  @Query(value = "SELECT * FROM IP_D WHERE ID IN (SELECT D_ID FROM MR WHERE DATA_FORMAT = ?1 AND MR_DATE BETWEEN ?2 AND ?3) ", nativeQuery = true)
  public List<IP_D> findByIDFromMR(String dataFormat, Date sDate, Date eDate);
  
  // for Test
  @Query(value = "SELECT DISTINCT (PRSN_ID) , FUNC_TYPE "
      + "FROM IP_D WHERE PRSN_ID LIKE '%***%' AND FUNC_TYPE IS NOT NULL ORDER BY FUNC_TYPE", nativeQuery = true)
  public List<Object[]> findDepartmentAndDoctor();
  
  @Query(value =  "SELECT TW_DRG_CODE , MR_ID FROM IP_D WHERE TW_DRG_CODE IS NOT NULL ", nativeQuery = true)
  public List<Object[]> findDRGCodeNotNull();
  
  // fot Test
  @Query(value = "SELECT * FROM IP_D WHERE TW_DRG_CODE IS NOT NULL AND TW_DRGS_SUIT_MARK='0' AND ID > ?1 AND IN_DATE > '1090630' ORDER BY ID", nativeQuery = true)
  public List<IP_D> findAllWithDRG(long maxId);
  
  /**
   * 取得該年月小於20件核實申報的DRG件數
   * @param inDate
   * @param inDate2
   * @param ym
   * @return
   */
  @Query(value = "SELECT TW_DRG_CODE, d.ID FROM IP_D d, MR mr WHERE TW_DRG_CODE IN (" + 
      "SELECT CODE FROM DRG_CODE WHERE STARTED = 1 AND CASE20 = 1 AND START_DAY  <= ?1 AND END_DAY  >= ?2" + 
      ") AND mr.ID = d.MR_ID AND mr.APPL_YM = ?3", nativeQuery = true)
  public List<Object[]> getDRGCase20Id(Date inDate, Date inDate2, String ym);
}
