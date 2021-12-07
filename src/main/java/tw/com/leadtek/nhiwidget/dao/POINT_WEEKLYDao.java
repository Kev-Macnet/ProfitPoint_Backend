/**
 * Created on 2021/11/5.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.sql.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import tw.com.leadtek.nhiwidget.model.rdb.POINT_WEEKLY;

public interface POINT_WEEKLYDao extends JpaRepository<POINT_WEEKLY, Long> {

  public POINT_WEEKLY findByPyearAndPweek(int year, int week);
  
  public POINT_WEEKLY findByStartDateAndEndDate(Date sdate, Date edate);
  
  public List<POINT_WEEKLY> findByEndDateLessThanEqualOrderByEndDateDesc(Date edate);
}
