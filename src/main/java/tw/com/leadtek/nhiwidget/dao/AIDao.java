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
  
  /**
   * 住院天數差異
   * @param lastYearSDate
   * @param lastYearEDate
   * @param sDate
   * @param eDate
   * @param days
   * @return
   */
  @Query(value = "SELECT MR_ID, ICD_CM_1, UP, COUNT, (COUNT - UP) as VCOUNT FROM (" + 
      "  SELECT CO.MR_ID,AI.ICD_CM_1, avg + " + VARIATION_TIMES + " * stddev as UP , STDDEV, CO.COUNT as COUNT FROM " + 
      "    (" + 
      "       SELECT ICD_CM_1, AVG(S_BED_DAY + E_BED_DAY) AS AVG, STDDEV(S_BED_DAY + E_BED_DAY) AS STDDEV FROM IP_D " + 
      "       WHERE MR_ID IN (SELECT ID FROM MR WHERE MR_END_DATE BETWEEN ?1 and ?2) GROUP BY ICD_CM_1" + 
      "    ) AI, " + 
      "    ( "
      + "     SELECT ICD_CM_1, (S_BED_DAY + E_BED_DAY) as COUNT, MR_ID from IP_D where MR_ID IN (SELECT ID FROM MR WHERE MR_END_DATE BETWEEN ?3 and ?4)" + 
      "    ) CO " + 
      "   WHERE AI.STDDEV > 0  AND AI.ICD_CM_1 = CO.ICD_CM_1" + 
      ") TEMP WHERE (COUNT - UP) > ?5", nativeQuery = true)
  public List<Map<String, Object>> ipDays(Date lastYearSDate, Date lastYearEDate, Date sDate, Date eDate, int days);
  
  // temp1 取得各診斷碼搭配藥物的出現次數
  // temp2 取得各診斷碼在指定時間內的出現次數
  @Query(value = "SELECT temp1.icdcm1 as ICDCM, temp1.ICCOUNT, temp1.DRUG_NO as DRUGNO, temp2.count as ICOUNT FROM (" + 
      "  SELECT mr.ICDCM1, count(mr.ICDCM1) ICCOUNT, op.DRUG_NO FROM op_p op , mr " + 
      "    WHERE op.mr_id = mr.id and mr.mr_date between ?1 and ?2 and length(op.drug_no) = 10 " + 
      "    group by mr.icdcm1, op.DRUG_NO " + 
      "  ) temp1, " + 
      " (SELECT ICDCM1, count(ICDCM1) as COUNT FROM mr WHERE mr_end_date between '2021-01-01' and '2021-01-31' and id in (select mr_id from op_p where length(drug_no) = 10) \r\n" + 
      "    group by  ICDCM1" + 
      "  ) temp2 " + 
      "WHERE temp1.icdcm1 = temp2.icdcm1 order by temp1.icdcm1", nativeQuery = true)
  public List<Map<String,Object>> icdcmDrugCountOP(Date sDate, Date eDate);
  
}
