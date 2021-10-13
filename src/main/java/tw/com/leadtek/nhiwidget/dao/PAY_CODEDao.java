/**
 * Created on 2021/9/10.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tw.com.leadtek.nhiwidget.model.rdb.PAY_CODE;

public interface PAY_CODEDao extends JpaRepository<PAY_CODE, Long>, JpaSpecificationExecutor<PAY_CODE>{

  public List<PAY_CODE> findByCode(String code);
  
  public List<PAY_CODE> findByInhCode(String code);
}
