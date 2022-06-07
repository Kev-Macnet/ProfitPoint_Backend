/**
 * Created on 2021/11/5.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import tw.com.leadtek.nhiwidget.model.rdb.POINT_WEEKLY;

public interface POINT_WEEKLYDao extends JpaRepository<POINT_WEEKLY, Long> {

  public POINT_WEEKLY findByPyearAndPweekAndFuncType(int year, int week, String funcType);
  
  public POINT_WEEKLY findByStartDateAndEndDateAndFuncType(Date sdate, Date edate, String funcType);
  
  public List<POINT_WEEKLY> findByEndDateLessThanEqualAndFuncTypeOrderByEndDateDesc(Date edate, String funcType);
  
  public List<POINT_WEEKLY> findByEndDateLessThanEqualOrderByEndDateDesc(Date edate);
  
  /**
   * 條件funcType取得趨勢圖資料
   * @param funcType
   * @return
   */
  @Query(value = "SELECT pw.P_YEAR, pw.P_WEEK, pw.START_DATE, pw.END_DATE, pw.OP, pw.IP, pw.EM, pw.VISITS_OP, pw.VISITS_IP, pw.VISITS_LEAVE, pw.FUNC_TYPE, ct.DESC_CHI  FROM POINT_WEEKLY pw ,CODE_TABLE ct WHERE pw.FUNC_TYPE  = ct.CODE AND ct.CAT ='FUNC_TYPE' AND ct.DESC_CHI = ?1 ", nativeQuery = true)
  public List<Map<String,Object>> getTredDataByFuncType(String funcType);
  /**
   * 取得趨勢圖資料
   * @return
   */
  @Query(value = "SELECT * FROM POINT_WEEKLY  WHERE  END_DATE  BETWEEN  ?1 AND  ?2 ORDER BY END_DATE DESC", nativeQuery = true)
  public List<POINT_WEEKLY> getTredAllData(String sDate, String eDate);
  
  
}
