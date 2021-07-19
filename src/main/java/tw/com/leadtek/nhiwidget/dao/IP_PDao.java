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
  
  @Query(value = "SELECT * FROM IP_P WHERE IPD_ID IN (SELECT D_ID FROM MR WHERE DATA_FORMAT = ?1 AND MR_DATE BETWEEN ?2 AND ?3) ", nativeQuery = true)
  public List<IP_P> findByIpdIDFromMR(String dataFormat, Date sDate, Date eDate);
}
