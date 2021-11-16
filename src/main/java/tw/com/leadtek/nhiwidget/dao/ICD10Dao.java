/**
 * Created on 2021/11/1.
 */
package tw.com.leadtek.nhiwidget.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tw.com.leadtek.nhiwidget.model.rdb.ICD10;

public interface ICD10Dao extends JpaRepository<ICD10, Long>, JpaSpecificationExecutor<ICD10>  {

  public ICD10 findByCode(String code);
  
}
