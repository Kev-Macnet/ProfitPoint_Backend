/**
 * Created on 2021/2/1.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_TABLE;

public interface CODE_TABLEDao extends JpaRepository<CODE_TABLE, Long>, JpaSpecificationExecutor<CODE_TABLE>  {

  public List<CODE_TABLE> findByCode(String code);
  
  public List<CODE_TABLE> findByCat(String cat);
  
  public CODE_TABLE findByCodeAndCat(String code, String cat);
  
  public CODE_TABLE findByDescChiAndCat(String desc, String cat);
  
  public List<CODE_TABLE> findByCatOrderByCode(String cat);
  
  public List<CODE_TABLE> findByCatOrderByParentCode(String cat);
}
