/**
 * Created on 2021/9/10.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import tw.com.leadtek.nhiwidget.model.rdb.PAY_CODE;

public interface PAY_CODEDao extends JpaRepository<PAY_CODE, Long>, JpaSpecificationExecutor<PAY_CODE>{

  public List<PAY_CODE> findByCode(String code);
  
  public List<PAY_CODE> findByInhCode(String code);
  
  /**
   *  取得所有支付代碼的類別代碼
   * @return [支付代碼, 類別代碼 ]
   */
  @Query(value ="SELECT pc.CODE, ct.CODE AS PAY_CODE_TYPE FROM PAY_CODE pc, CODE_TABLE ct WHERE pc.CODE IN"
      + "(SELECT DISTINCT (DRUG_NO) FROM OP_P WHERE DRUG_NO IN (SELECT CODE FROM PAY_CODE))" + 
      "AND ct.CAT ='PAY_CODE_TYPE' AND ct.DESC_CHI = pc.CODE_TYPE", nativeQuery = true)
  public List<Object[]> findOPAllPayCodeAndTypeCode();
  
  /**
   *  取得所有支付代碼的類別代碼
   * @return [支付代碼, 類別代碼 ]
   */
  @Query(value ="SELECT pc.CODE, ct.CODE AS PAY_CODE_TYPE FROM PAY_CODE pc, CODE_TABLE ct WHERE pc.CODE IN"
      + "(SELECT DISTINCT (ORDER_CODE) FROM IP_P WHERE ORDER_CODE IN (SELECT CODE FROM PAY_CODE))" + 
      "AND ct.CAT ='PAY_CODE_TYPE' AND ct.DESC_CHI = pc.CODE_TYPE", nativeQuery = true)
  public List<Object[]> findIPAllPayCodeAndTypeCode();
}
