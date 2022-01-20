/**
 * Created on 2022/1/11.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import tw.com.leadtek.nhiwidget.model.rdb.DRG_CAL;
import tw.com.leadtek.nhiwidget.model.rdb.DRG_CALId;

public interface DRG_CALDao extends JpaRepository<DRG_CAL, DRG_CALId> {

  public List<DRG_CAL> findByMrIdAndErrorIsNull(Long mrId);
}
