/**
 * Created on 2021/10/12.
 */
package tw.com.leadtek.nhiwidget.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tw.com.leadtek.nhiwidget.model.rdb.SAME_ATC;

public interface SAME_ATCDao extends JpaRepository<SAME_ATC, Long>, JpaSpecificationExecutor<SAME_ATC> {

}
