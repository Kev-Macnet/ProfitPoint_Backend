/**
 * Created on 2021/10/28.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tw.com.leadtek.nhiwidget.model.rdb.ASSIGNED_POINT;

public interface ASSIGNED_POINTDao extends JpaRepository<ASSIGNED_POINT, Long>, JpaSpecificationExecutor<ASSIGNED_POINT> {

  public List<ASSIGNED_POINT> findAllByStartDateLessThanEqualAndEndDateGreaterThanEqual(Date d1, Date d2);
  
  public List<ASSIGNED_POINT> findAllByOrderByEndDateDesc();
}
