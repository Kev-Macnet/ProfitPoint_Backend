/**
 * Created on 2022/4/14.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tw.com.leadtek.nhiwidget.model.rdb.MR;

public interface AIDao extends JpaRepository<MR, Long> {

  /**
   * 變異數倍數
   */
  public final static String VARIATION_TIMES = "3";
  
  /**
   * 費用差異--門診和住院
   * @param date
   * @return
   */
  @Query(value = "SELECT * FROM( "
        + "SELECT MR.ID, MR.T_DOT , MR.ICDCM1, AI.AVG + " + VARIATION_TIMES + " * AI.STDDEV AS UP, "
            + "AI.AVG - " + VARIATION_TIMES + " * AI.STDDEV AS DOWN "
        + "FROM ( "
        + "SELECT ICDCM1, AVG(T_DOT) AS AVG, STDDEV(T_DOT) AS STDDEV "
        + "FROM MR "
        + "WHERE MR_DATE BETWEEN ?1 AND ?2 AND DATA_FORMAT =?3 GROUP BY ICDCM1) AI, MR "
        + "WHERE AI.STDDEV > 0 AND MR.APPL_YM = ?4 AND MR.ICDCM1 = AI.ICDCM1 AND MR.DATA_FORMAT =?3 "
        + ") TEMP WHERE T_DOT > (UP * ?5) OR T_DOT < (DOWN * ?6)", nativeQuery = true)
  public List<Map<String, Object>> costDiffOP(Date sdate, Date edate, String dataFormat,
      String applYm, float up, float down);
}
