/**
 * Created on 2021/1/26.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import tw.com.leadtek.nhiwidget.model.rdb.OP_T;

public interface OP_TDao extends JpaRepository<OP_T, Long> {

  public List<OP_T> findByFeeYmOrderById(String feeYM);
}
