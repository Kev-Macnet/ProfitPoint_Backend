/**
 * Created on 2021/5/3.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import tw.com.leadtek.nhiwidget.model.rdb.USER_DEPARTMENT;

public interface USER_DEPARTMENTDao extends JpaRepository<USER_DEPARTMENT, Long> {

  public List<USER_DEPARTMENT> findByUserIdOrderByDepartmentId(Long userId);
  
  public long removeByUserId(Long userId);
}
