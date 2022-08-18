/**
 * Created on 2021/11/10.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.sql.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import tw.com.leadtek.nhiwidget.model.rdb.DRG_WEEKLY;

public interface DRG_WEEKLYDao extends JpaRepository<DRG_WEEKLY, Long> {

  public DRG_WEEKLY findByPyearAndPweek(int year, int week);
  
  public List<DRG_WEEKLY> findByFuncTypeAndPyearAndPweek(String funcType, int year, int week);
  
  public DRG_WEEKLY findByFuncTypeAndStartDateAndEndDate(String funcType, Date sdate, Date edate);
  
  public List<DRG_WEEKLY> findByFuncTypeAndEndDateLessThanEqualOrderByEndDateDesc(String funcType, Date edate);
  
}
