/**
 * Created on 2021/8/13.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tw.com.leadtek.nhiwidget.model.rdb.DRG_CODE;

public interface DRG_CODEDao extends JpaRepository<DRG_CODE, Long> {

  public List<DRG_CODE> findByCodeAndStartDayLessThanEqualAndEndDayGreaterThanEqual(String code, Date d1, Date d2);
  
  public List<DRG_CODE> findByCodeAndStartDayAndEndDay(String code, Date d1, Date d2);
  
}
