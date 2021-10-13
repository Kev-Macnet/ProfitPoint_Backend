/**
 * Created on 2021/9/8.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tw.com.leadtek.nhiwidget.model.rdb.ATC;

public interface ATCDao extends JpaRepository<ATC, String>, JpaSpecificationExecutor<ATC> {

  public List<ATC> findByCode(String code);
}
