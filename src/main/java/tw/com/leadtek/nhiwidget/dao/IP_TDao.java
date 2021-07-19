/**
 * Created on 2021/1/27.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import tw.com.leadtek.nhiwidget.model.rdb.IP_T;

public interface IP_TDao extends JpaRepository<IP_T, Long> {

  public List<IP_T> findByFeeYmOrderById(String feeYM);
}
