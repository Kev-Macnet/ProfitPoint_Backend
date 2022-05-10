/**
 * Created on 2021/11/16.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tw.com.leadtek.nhiwidget.model.rdb.MR_NOTICE;

public interface MR_NOTICEDao extends JpaRepository<MR_NOTICE, Long>, JpaSpecificationExecutor<MR_NOTICE> {

  public List<MR_NOTICE> findByMrIdAndStatusAndReceiveUserIdContainingOrderByNoticeDateDesc(
      long mrId, int status, String userId);
  
  public List<MR_NOTICE> findByMrIdAndReceiveUserIdContainingOrderByNoticeDateDesc(
      long mrId, String userId);
  
  public List<MR_NOTICE> findByMrId(long mrId);
}
