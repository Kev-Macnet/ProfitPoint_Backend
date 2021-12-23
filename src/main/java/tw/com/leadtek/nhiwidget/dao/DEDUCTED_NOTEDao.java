/**
 * Created on 2021/12/14.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tw.com.leadtek.nhiwidget.model.rdb.DEDUCTED_NOTE;

public interface DEDUCTED_NOTEDao extends JpaRepository<DEDUCTED_NOTE, Long>, JpaSpecificationExecutor<DEDUCTED_NOTE> {

  public List<DEDUCTED_NOTE> findByMrIdOrderById(Long mrId);
  
}
