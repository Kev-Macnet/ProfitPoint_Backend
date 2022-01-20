/**
 * Created on 2021/10/12.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_CONFLICT;

public interface CODE_CONFLICTDao extends JpaRepository<CODE_CONFLICT, Long>, JpaSpecificationExecutor<CODE_CONFLICT> {

  public List<CODE_CONFLICT> findByCodeAndOwnExpCodeAndCodeType(String code, String ownExpCode, Integer codeType);
}
