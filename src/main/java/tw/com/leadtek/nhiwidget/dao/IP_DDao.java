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
  
  @Query(value = "SELECT * FROM IP_D WHERE ID IN (SELECT D_ID FROM MR WHERE DATA_FORMAT = ?1 AND MR_DATE BETWEEN ?2 AND ?3) ", nativeQuery = true)
  public List<IP_D> findByIDFromMR(String dataFormat, Date sDate, Date eDate);
}
