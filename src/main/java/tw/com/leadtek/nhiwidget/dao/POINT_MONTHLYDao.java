/**
 * Created on 2021/11/10.
 */
package tw.com.leadtek.nhiwidget.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import tw.com.leadtek.nhiwidget.model.rdb.POINT_MONTHLY;

public interface POINT_MONTHLYDao  extends JpaRepository<POINT_MONTHLY, Long> {

  public POINT_MONTHLY findByYm(int ym);
}

