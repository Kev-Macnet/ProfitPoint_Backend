/**
 * Created on 2021/11/17.
 */
package tw.com.leadtek.nhiwidget.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tw.com.leadtek.nhiwidget.model.rdb.MY_MR;

public interface MY_MRDao extends JpaRepository<MY_MR, Long>, JpaSpecificationExecutor<MY_MR> {

  public MY_MR findByMrId(long mrId);
}
