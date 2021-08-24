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
}
