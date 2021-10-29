/**
 * Created on 2021/10/27.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tw.com.leadtek.nhiwidget.model.rdb.DEDUCTED;

public interface DEDUCTEDDao extends JpaRepository<DEDUCTED, Long>, JpaSpecificationExecutor<DEDUCTED> {

  public List<DEDUCTED> findByCode(String code);
}
