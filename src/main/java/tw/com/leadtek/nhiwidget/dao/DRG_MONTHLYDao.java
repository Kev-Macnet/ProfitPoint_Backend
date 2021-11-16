/**
 * Created on 2021/11/10.
 */
package tw.com.leadtek.nhiwidget.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import tw.com.leadtek.nhiwidget.model.rdb.DRG_MONTHLY;

public interface DRG_MONTHLYDao extends JpaRepository<DRG_MONTHLY, Long> {

  public DRG_MONTHLY findByYmAndFuncType(int ym, String funcType);
}
