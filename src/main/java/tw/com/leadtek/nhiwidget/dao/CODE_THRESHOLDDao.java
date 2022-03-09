/**
 * Created on 2021/10/6.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_THRESHOLD;

public interface CODE_THRESHOLDDao extends JpaRepository<CODE_THRESHOLD, Long>, JpaSpecificationExecutor<CODE_THRESHOLD> {

  public List<CODE_THRESHOLD> findByCodeTypeOrderByStartDateDesc(Integer codeType);
  
  public List<CODE_THRESHOLD> findByCodeTypeAndCodeOrderByStartDateDesc(Integer codeType, String code);
  
  public List<CODE_THRESHOLD> findByCodeTypeAndInhCodeOrderByStartDateDesc(Integer codeType, String code);
}
