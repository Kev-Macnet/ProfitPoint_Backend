/**
 * Created on 2021/11/17.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import tw.com.leadtek.nhiwidget.model.rdb.MY_MR;

public interface MY_MRDao extends JpaRepository<MY_MR, Long>, JpaSpecificationExecutor<MY_MR> {

  public MY_MR findByMrId(long mrId);
  
  /**
   * 取得比對警示的病歷中ICD碼異動數、院內碼異動數、支付標準代碼異動數、其他資訊異動數、SO異動數
   * @return
   */
  @Query(value ="SELECT SUM(CHANGE_ICD), SUM(CHANGE_INH), SUM(CHANGE_ORDER)," + 
      "SUM(CHANGE_OTHER), SUM(CHANGE_SO) FROM MY_MR WHERE STATUS = -2", nativeQuery = true)
  public List<Object[]> getWarningOrderCount();
  
  /**
   * 取得比對警示的病歷中ICD碼異動數、院內碼異動數、支付標準代碼異動數、其他資訊異動數、SO異動數
   * @return
   */
  @Query(value ="SELECT SUM(CHANGE_ICD), SUM(CHANGE_INH), SUM(CHANGE_ORDER)," + 
      "SUM(CHANGE_OTHER), SUM(CHANGE_SO) FROM MY_MR WHERE STATUS = -2 AND APPL_USER_ID = ?1", nativeQuery = true)
  public List<Object[]> getWarningOrderCount(long applUserId);
  
  /**
   * 取得指定病歷狀態的已通知次數及已讀取次數
   * @return
   */
  @Query(value ="SELECT * FROM " + 
      "(SELECT COUNT(1) AS NOTICE_TIMES FROM MY_MR WHERE STATUS=?1 AND NOTICE_DATE IS NOT NULL ) a," + 
      "(SELECT COUNT(1) AS READED_TIMES FROM MY_MR WHERE STATUS=?2 AND READED_PPL > 0 ) b", nativeQuery = true)
  public List<Object[]> getNoticeAndReadedTimes(int status, int status2);
  
  /**
   * 取得指定病歷狀態的已通知次數及已讀取次數
   * @return
   */
  @Query(value ="SELECT * FROM " + 
      "(SELECT COUNT(1) AS NOTICE_TIMES FROM MY_MR WHERE STATUS=?1 AND NOTICE_DATE IS NOT NULL AND APPL_USER_ID = ?2) a," + 
      "(SELECT COUNT(1) AS READED_TIMES FROM MY_MR WHERE STATUS=?3 AND READED_PPL > 0 AND APPL_USER_ID = ?4) b", nativeQuery = true)
  public List<Object[]> getNoticeAndReadedTimes(int status, long applUserId, int status2, long applUserId2);
  
  /**
   * 取得指定病歷狀態的病歷數
   * @return
   */
  @Query(value ="SELECT COUNT(1) FROM MY_MR WHERE STATUS=?1", nativeQuery = true)
  public Long getCountByStatus(int status);
  
  /**
   * 取得指定病歷狀態的病歷數
   * @return
   */
  @Query(value ="SELECT COUNT(1) FROM MY_MR WHERE STATUS=?1 AND APPL_USER_ID=?2", nativeQuery = true)
  public Long getCountByStatusAndApplUserId(int status, long userId);
  
}
