/**
 * Created on 2021/10/14.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tw.com.leadtek.nhiwidget.model.rdb.MR_NOTE;

public interface MR_NOTEDao extends JpaRepository<MR_NOTE, Long>, JpaSpecificationExecutor<MR_NOTE> {
  
  public List<MR_NOTE> findByMrIdOrderById(Long mrId);
}