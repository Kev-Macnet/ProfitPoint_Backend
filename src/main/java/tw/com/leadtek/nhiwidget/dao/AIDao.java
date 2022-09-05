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
  
  /**
   * 撈出病歷最新日期及ICDCM_DRUG_ATC的最新日期
   * @param dataFormat
   * @return
   */
  @Query(value = "SELECT mr.MAX_MR_DATE, ida.MAX_DATE FROM " + 
      "(SELECT max(MR_END_DATE) AS MAX_MR_DATE FROM mr WHERE DATA_FORMAT = ?1) mr," + 
      "(SELECT max(LATEST_DATE) AS MAX_DATE FROM icdcm_drug_atc WHERE DATA_FORMAT= ?1) ida", 
      nativeQuery = true)
  public List<Map<String, Object>> getMaxMrEndDateAndIcdcmDrugAtcDate(String dataFormat);
  
  // temp1 取得各診斷碼搭配的藥物的出現次數及藥物的ATC碼
  // temp2 取得各診斷碼在的出現病歷數
  @Query(value = "SELECT temp1.ICDCM1, temp2.COUNT as ICDCM_COUNT, temp1.DRUG_NO, temp1.DRUG_COUNT,temp1.ATC FROM ( " + 
      "        SELECT mr.ICDCM1, count(mr.ICDCM1) DRUG_COUNT, op.DRUG_NO , pc.ATC FROM op_p op, mr, PAY_CODE pc " + 
      "          WHERE op.mr_id = mr.id and length(op.drug_no) = 10 AND op.DRUG_NO = pc.CODE AND mr.MR_END_DATE <= ?1 " + 
      "          group by mr.icdcm1, op.drug_no, pc.atc ORDER BY icdcm1, pc.atc " + 
      "        ) temp1," + 
      "        (" + 
      "         SELECT ICDCM1, count(ICDCM1) as COUNT FROM mr WHERE DATA_FORMAT ='10' AND mr.MR_END_DATE <= ?1" + 
      "          group by ICDCM1 " + 
      "        ) temp2 " + 
      "      WHERE temp1.ICDCM1 = temp2.ICDCM1 AND temp2.COUNT > 30 ORDER BY temp1.ICDCM1, temp1.atc", nativeQuery = true)
  public List<Map<String,Object>> icdcmDrugCountOP(Date endDate);
 
  // temp1 取得各診斷碼搭配的藥物的出現次數及藥物的ATC碼
  // temp2 取得各診斷碼在的出現病歷數
  @Query(value = "SELECT temp1.ICDCM1, temp2.COUNT as ICDCM_COUNT, temp1.DRUG_NO, temp1.DRUG_COUNT,temp1.ATC FROM ( " + 
      "        SELECT mr.ICDCM1, count(mr.ICDCM1) DRUG_COUNT, ip.ORDER_CODE AS DRUG_NO , pc.ATC FROM ip_p ip, mr, PAY_CODE pc " + 
      "          WHERE ip.mr_id = mr.id and length(ip.ORDER_CODE) = 10 AND ip.ORDER_CODE = pc.CODE AND mr.MR_END_DATE <= ?1 " + 
      "          group by mr.icdcm1, ip.ORDER_CODE, pc.atc ORDER BY icdcm1, pc.atc " + 
      "        ) temp1," + 
      "        (" + 
      "         SELECT ICDCM1, count(ICDCM1) as COUNT FROM mr WHERE DATA_FORMAT ='20' AND mr.MR_END_DATE <= ?1" + 
      "          group by ICDCM1 " + 
      "        ) temp2 " + 
      "      WHERE temp1.ICDCM1 = temp2.ICDCM1 AND temp2.COUNT > 30 ORDER BY temp1.ICDCM1, temp1.atc", nativeQuery = true)
  public List<Map<String,Object>> icdcmDrugCountIP(Date endDate);
  
  // temp1 取得各診斷碼搭配的衛品的出現次數
  // temp2 取得各診斷碼在的出現病歷數
  @Query(value = "SELECT temp1.ICDCM1, temp2.COUNT as ICDCM_COUNT, temp1.DRUG_NO, temp1.DRUG_COUNT, temp1.ATC FROM ( " + 
      "           SELECT mr.ICDCM1, count(mr.ICDCM1) DRUG_COUNT, op.DRUG_NO, SUBSTR(op.DRUG_NO, 1, 5) AS ATC FROM op_p op, mr " + 
      "           WHERE op.mr_id = mr.id and length(op.drug_no) = 12 AND mr.MR_END_DATE <= ?1 " + 
      "           GROUP BY mr.icdcm1, op.DRUG_NO ORDER BY mr.icdcm1, op.DRUG_NO " + 
      "        ) temp1," + 
      "        (" + 
      "         SELECT ICDCM1, count(ICDCM1) as COUNT FROM mr WHERE DATA_FORMAT ='10' AND mr.MR_END_DATE <= ?1" + 
      "          group by ICDCM1 " + 
      "        ) temp2 " + 
      "      WHERE temp1.ICDCM1 = temp2.ICDCM1 AND temp2.COUNT > 30 ORDER BY temp1.ICDCM1, temp1.ATC", nativeQuery = true)
  public List<Map<String,Object>> icdcmMaterialCountOP(Date endDate);
  
  // temp1 取得各診斷碼搭配的衛品的出現次數
  // temp2 取得各診斷碼在的出現病歷數
  @Query(value = "SELECT temp1.ICDCM1, temp2.COUNT as ICDCM_COUNT, temp1.DRUG_NO, temp1.DRUG_COUNT, temp1.ATC FROM ( " + 
      "           SELECT mr.ICDCM1, count(mr.ICDCM1) DRUG_COUNT, ip.ORDER_CODE AS DRUG_NO, SUBSTR(ip.ORDER_CODE, 1, 5) AS ATC FROM ip_p ip, mr " + 
      "           WHERE ip.mr_id = mr.id and length(ip.ORDER_CODE) = 12 AND mr.MR_END_DATE <= ?1 " + 
      "           GROUP BY mr.icdcm1, ip.ORDER_CODE ORDER BY icdcm1, ip.ORDER_CODE " + 
      "           ) temp1," + 
      "          (" + 
      "             SELECT ICDCM1, count(ICDCM1) as COUNT FROM mr WHERE DATA_FORMAT ='20' AND mr.MR_END_DATE <= ?1" + 
      "             group by ICDCM1 " + 
      "           ) temp2 " + 
      "      WHERE temp1.ICDCM1 = temp2.ICDCM1 AND temp2.COUNT > 30 ORDER BY temp1.ICDCM1, temp1.ATC", nativeQuery = true)
  public List<Map<String,Object>> icdcmMaterialCountIP(Date endDate);
}
