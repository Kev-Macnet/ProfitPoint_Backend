/**
 * Created on 2021/4/29.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import tw.com.leadtek.nhiwidget.model.rdb.MR_CHECKED;

public interface MR_CHECKEDDao extends JpaRepository<MR_CHECKED, Long>, JpaSpecificationExecutor<MR_CHECKED> {
  
  //deprecated
  @Query(value = "SELECT MR.ID AS MRID, oc.REASON AS REASON, oc.DETAIL AS DETAIL "
      + "FROM MR, MR_CHECKED mrc, ORDER_CHECK oc WHERE mrc.MR_ID = MR.ID AND "
      + "mrc.MR_ID IN (?1) AND mrc.OC_ID = oc.ID", nativeQuery = true)
  public List<Map<String, Object>> queryMRChecked(List<Long> mrid);
  
  public MR_CHECKED findByMrId(long mrId);
  
  @Query(value = "SELECT * FROM MR_CHECKED WHERE APPL_ID=?1 AND STATUS IN (0, -1)", nativeQuery = true)
  public List<MR_CHECKED> findMyTodoList(long applId);
  
  @Query(value = "SELECT * FROM MR_CHECKED WHERE APPL_ID=?1 AND STATUS=-2", nativeQuery = true)
  public List<MR_CHECKED> findMyWarningList(long applId);
}
