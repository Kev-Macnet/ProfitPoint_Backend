/**
 * Created on 2022/3/9.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import tw.com.leadtek.nhiwidget.model.rdb.PLAN_CONDITION;

public interface PLAN_CONDITIONDao extends JpaRepository<PLAN_CONDITION, Long> {

  public List<PLAN_CONDITION> findByActive(int active); 
  
}
