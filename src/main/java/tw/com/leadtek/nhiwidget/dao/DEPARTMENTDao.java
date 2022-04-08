/**
 * Created on 2021/5/3.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import tw.com.leadtek.nhiwidget.model.rdb.DEPARTMENT;

public interface DEPARTMENTDao extends JpaRepository<DEPARTMENT, Long> {

  public List<DEPARTMENT> findByName(String name);
}
