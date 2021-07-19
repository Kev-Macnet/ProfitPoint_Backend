/**
 * Created on 2021/1/25.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.sql.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tw.com.leadtek.nhiwidget.model.rdb.OP_P;

public interface OP_PDao extends JpaRepository<OP_P, Long> {

  public List<OP_P> findByOpdId(Long opdId);
  
  @Query(value = "SELECT * FROM OP_P WHERE OPD_ID IN (SELECT D_ID FROM MR WHERE DATA_FORMAT = ?1 AND MR_DATE BETWEEN ?2 AND ?3) ", nativeQuery = true)
  public List<OP_P> findByOpdIDFromMR(String dataFormat, Date sDate, Date eDate);
}
