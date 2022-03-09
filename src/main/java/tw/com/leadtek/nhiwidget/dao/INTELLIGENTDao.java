/**
 * Created on 2021/11/19.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import tw.com.leadtek.nhiwidget.model.rdb.INTELLIGENT;

public interface INTELLIGENTDao extends JpaRepository<INTELLIGENT, Long>, JpaSpecificationExecutor<INTELLIGENT> {

  @Query(value ="SELECT CONDITION_CODE, COUNT(1) FROM INTELLIGENT WHERE STATUS = -2 GROUP BY CONDITION_CODE", nativeQuery = true)
  public List<Object[]> countGroupByConditionCode();
  
  /**
   * 找出相同智能提示條件下的清單，避免重複寫入
   * @param conditionCode
   * @param reasonCode
   * @return
   */
  public List<INTELLIGENT> findByConditionCodeAndReasonCode(int conditionCode, String reasonCode);
  
  public List<INTELLIGENT> findByMrIdAndConditionCodeAndReasonCode(Long mrId, int conditionCode, String reasonCode);
  
  public List<INTELLIGENT> findByRocIdAndConditionCodeAndReasonCode(String rocId, int conditionCode, String reasonCode);
  
  public List<INTELLIGENT> findByMrId(Long mrId);
  
  @Transactional
  @Modifying
  @Query(value = "DELETE FROM intelligent WHERE CONDITION_CODE=?1 AND REASON_CODE=?2 AND REASON=?3", nativeQuery = true)
  public void deleteIntelligent(int conditionCode, String reasonCode, String reason);
}
