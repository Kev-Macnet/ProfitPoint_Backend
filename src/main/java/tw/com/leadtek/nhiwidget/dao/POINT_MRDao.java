/**
 * Created on 2021/11/3.
 */
package tw.com.leadtek.nhiwidget.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import tw.com.leadtek.nhiwidget.model.rdb.POINT_MR;

public interface POINT_MRDao extends JpaRepository<POINT_MR, Long> {

  public POINT_MR findByYm(int ym);
}
