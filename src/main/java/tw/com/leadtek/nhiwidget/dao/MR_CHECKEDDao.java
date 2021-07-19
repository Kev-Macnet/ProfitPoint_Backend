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
  
  @Query(value = "SELECT MR.ID AS MRID, oc.REASON AS REASON, oc.DETAIL AS DETAIL "
      + "FROM MR, MR_CHECKED mrc, ORDER_CHECK oc WHERE mrc.MR_ID = MR.ID AND "
      + "mrc.MR_ID IN (?1) AND mrc.OC_ID = oc.ID", nativeQuery = true)
  public List<Map<String, Object>> queryMRChecked(List<Long> mrid);
}
