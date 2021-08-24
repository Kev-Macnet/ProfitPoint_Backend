/**
 * Created on 2021/1/27.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.sql.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tw.com.leadtek.nhiwidget.model.rdb.IP_P;

public interface IP_PDao extends JpaRepository<IP_P, Long> {

  public List<IP_P> findByIpdId(Long ipdId);
  
  public List<IP_P> findByIpdIdOrderByOrderSeqNo(Long ipdId);
  
  @Query(value = "SELECT * FROM IP_P WHERE IPD_ID IN (SELECT D_ID FROM MR WHERE DATA_FORMAT = ?1 AND MR_DATE BETWEEN ?2 AND ?3) ", nativeQuery = true)
  public List<IP_P> findByIpdIDFromMR(String dataFormat, Date sDate, Date eDate);
  
  @Query(value = "SELECT ID, IPD_ID, ORDER_SEQ_NO FROM IP_P WHERE IPD_ID IN "
      + "(SELECT ID FROM IP_D WHERE IPT_ID = ?1) ORDER BY IPD_ID, ORDER_SEQ_NO", nativeQuery = true)
  public List<Object[]> findByIptId(Long iptId);
  
  @Query(value = "SELECT DISTINCT (PRSN_ID) , CON_FUNC_TYPE "
      + "FROM IP_P WHERE PRSN_ID IS NOT NULL AND CON_FUNC_TYPE IS NOT NULL", nativeQuery = true)
  public List<Object[]> findDepartmentAndDoctor();
}
