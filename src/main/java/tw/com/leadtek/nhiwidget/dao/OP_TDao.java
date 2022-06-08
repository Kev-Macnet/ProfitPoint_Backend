/**
 * Created on 2021/1/26.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import tw.com.leadtek.nhiwidget.model.rdb.OP_T;

public interface OP_TDao extends JpaRepository<OP_T, Long> {

  /**
   * 
   * @param feeYM 民國年月
   * @return
   */
  public List<OP_T> findByFeeYmOrderById(String feeYM);
  
  public List<OP_T> findByFeeYmAndHospIdOrderById(String feeYM, String hospId);
  
  @Query(value="SELECT ID FROM OP_T WHERE FEE_YM IN ?1",nativeQuery = true)
  public List<Integer> findByFeeYmListOrderById(List<String>FeeYmList);
}
