/**
 * Created on 2021/5/14.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tw.com.leadtek.nhiwidget.model.rdb.PARAMETERS;

public interface PARAMETERSDao extends JpaRepository<PARAMETERS, Long>, JpaSpecificationExecutor<PARAMETERS> {

  public List<PARAMETERS> findByName(String name);
  
  public List<PARAMETERS> findByCatAndStartDate(String cat, Date startDate);
  
  public List<PARAMETERS> findByNameAndStartDate(String name, Date startDate);
  
  public List<PARAMETERS> findByCatAndStartDateLessThanAndEndDateGreaterThan(String cat, Date sDate, Date eDate);
  
  public List<PARAMETERS> findByNameAndStartDateLessThanAndEndDateGreaterThan(String name, Date sDate, Date eDate);
  
  public List<PARAMETERS> findByNameInAndStartDateOrderByStartDateDesc(Collection<String> names, Date startDate);
  
}
