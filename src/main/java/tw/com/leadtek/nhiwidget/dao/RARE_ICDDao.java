/**
 * Created on 2021/9/29.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tw.com.leadtek.nhiwidget.model.rdb.RARE_ICD;

public interface RARE_ICDDao extends JpaRepository<RARE_ICD, Long>, JpaSpecificationExecutor<RARE_ICD> {

  public List<RARE_ICD> findAllByOrderByStartDateDesc();
  
  public List<RARE_ICD> findByCodeOrderByStartDateDesc(String code);
}
