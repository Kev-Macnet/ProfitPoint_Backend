/**
 * Created on 2021/5/3.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import tw.com.leadtek.nhiwidget.model.rdb.USER;

public interface USERDao extends JpaRepository<USER, Long> {

  public List<USER> findByUsername(String username);

}
