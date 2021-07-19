/**
 * Created on 2021/2/1.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_TABLE;

public interface CODE_TABLEDao extends JpaRepository<CODE_TABLE, Long> {

  public CODE_TABLE findByCodeAndCat(String code, String cat);
  
  public CODE_TABLE findByDescChiAndCat(String desc, String cat);
  
  public List<CODE_TABLE> findByCat(String cat);
}
