/**
 * Created on 2022/1/11.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import tw.com.leadtek.nhiwidget.model.rdb.DRG_CAL;
import tw.com.leadtek.nhiwidget.model.rdb.DRG_CALId;

public interface DRG_CALDao extends JpaRepository<DRG_CAL, DRG_CALId> {

  public List<DRG_CAL> findByMrId(Long mrId);
  
  @Transactional
  @Modifying
  @Query(value = "DELETE FROM drg_cal WHERE MR_ID IN ?1", nativeQuery = true)
  public void deleteByMrId(List<Long> mrIdList);
}
