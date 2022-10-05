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
  
  public List<INTELLIGENT> findByConditionCode(int conditionCode);
  
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
  
  public List<INTELLIGENT> findByMrIdAndFuncEnable(Long mrId, int funcEnable);
  
  @Transactional
  @Modifying
  @Query(value = "DELETE FROM intelligent WHERE CONDITION_CODE=?1", nativeQuery = true)
  public void deleteIntelligent(int conditionCode);
  
  @Transactional
  @Modifying
  @Query(value = "UPDATE intelligent SET FUNC_ENABLE = 0, UPDATE_AT=CURRENT_TIMESTAMP WHERE CONDITION_CODE=?1 AND MR_ID=?2", nativeQuery = true)
  public void disableIntelligentByMrId(int conditionCode, Long mrId);
  
  @Transactional
  @Modifying
  @Query(value = "UPDATE intelligent SET FUNC_ENABLE = 0, UPDATE_AT=CURRENT_TIMESTAMP WHERE CONDITION_CODE=?1", nativeQuery = true)
  public void disableIntelligent(int conditionCode);
  
  @Transactional
  @Modifying
  @Query(value = "UPDATE intelligent SET FUNC_ENABLE = 0, UPDATE_AT=CURRENT_TIMESTAMP WHERE ID=?1", nativeQuery = true)
  public void disableIntelligentById(Long id);
  
  @Transactional
  @Modifying
  @Query(value = "DELETE FROM intelligent WHERE CONDITION_CODE=?1 AND REASON_CODE=?2", nativeQuery = true)
  public void deleteIntelligent(int conditionCode, String reasonCode);

  @Transactional
  @Modifying
  @Query(
      value =
          "UPDATE intelligent SET FUNC_ENABLE = 0, UPDATE_AT=CURRENT_TIMESTAMP WHERE CONDITION_CODE=?1 AND REASON_CODE=?2",
      nativeQuery = true)
  public void disableIntelligent(int conditionCode, String reasonCode);

  @Transactional
  @Modifying
  @Query(value = "DELETE FROM intelligent WHERE CONDITION_CODE=?1 AND REASON_CODE=?2 AND REASON=?3", nativeQuery = true)
  public void deleteIntelligent(int conditionCode, String reasonCode, String reason);
  
  @Transactional
  @Modifying
  @Query(value = "UPDATE intelligent SET FUNC_ENABLE = 0, UPDATE_AT=CURRENT_TIMESTAMP WHERE CONDITION_CODE=?1 AND REASON_CODE=?2 AND REASON=?3", nativeQuery = true)
  public void disableIntelligent(int conditionCode, String reasonCode, String reason);
  
  @Transactional
  @Modifying
  @Query(value = "UPDATE intelligent SET STATUS = ?1, UPDATE_AT=CURRENT_TIMESTAMP WHERE MR_ID = ?2", nativeQuery = true)
  public void updateIntelligentStatus(Long id, int status);
}
