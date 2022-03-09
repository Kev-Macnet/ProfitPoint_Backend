/**
 * Created on 2022/3/2.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import tw.com.leadtek.nhiwidget.payload.MO;

public interface MODao extends JpaRepository<MO, Long> {

  @Transactional
  @Modifying
  @Query(value = "DELETE FROM MO_DIFF WHERE MR_ID=?1", nativeQuery = true)
  public void deleteByMrId(Long mrId);
  
  public List<MO> findByMrId(Long mrId);
}
