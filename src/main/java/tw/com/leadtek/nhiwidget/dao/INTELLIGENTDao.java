/**
 * Created on 2021/11/19.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import tw.com.leadtek.nhiwidget.model.rdb.INTELLIGENT;

public interface INTELLIGENTDao extends JpaRepository<INTELLIGENT, Long>, JpaSpecificationExecutor<INTELLIGENT> {

  @Query(value ="SELECT CONDITION_CODE, COUNT(1) FROM INTELLIGENT GROUP BY CONDITION_CODE", nativeQuery = true)
  public List<Object[]> countGroupByConditionCode();
}
