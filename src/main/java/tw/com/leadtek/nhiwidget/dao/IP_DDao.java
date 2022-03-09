/**
 * Created on 2021/1/27.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.sql.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import tw.com.leadtek.nhiwidget.model.rdb.IP_D;

public interface IP_DDao extends JpaRepository<IP_D, Long>, JpaSpecificationExecutor<IP_D> {

  public List<IP_D> findByIptId(Long iptId);
  
  public List<IP_D> findByMrId(Long mrid);
  
  @Query(value =  "SELECT SEQ_NO ,ID ,ROC_ID, IN_DATE, MR_ID, ID_BIRTH_YMD FROM IP_D WHERE IPT_ID=?1 ", nativeQuery = true)
  public List<Object[]> findByIptIdSimple(Long iptId);
  
  @Query(value = "SELECT * FROM IP_D WHERE ID IN (SELECT D_ID FROM MR WHERE DATA_FORMAT = ?1 AND MR_DATE BETWEEN ?2 AND ?3) ", nativeQuery = true)
  public List<IP_D> findByIDFromMR(String dataFormat, Date sDate, Date eDate);
  
  // for Test
  @Query(value = "SELECT DISTINCT (PRSN_ID) , FUNC_TYPE "
      + "FROM IP_D WHERE PRSN_ID LIKE '%***%' AND FUNC_TYPE IS NOT NULL ORDER BY FUNC_TYPE", nativeQuery = true)
  public List<Object[]> findDepartmentAndDoctor();
  
  @Query(value =  "SELECT TW_DRG_CODE , MR_ID FROM IP_D WHERE TW_DRG_CODE IS NOT NULL ", nativeQuery = true)
  public List<Object[]> findDRGCodeNotNull();
  
  // fot Test
  @Query(value = "SELECT * FROM IP_D WHERE TW_DRG_CODE IS NOT NULL AND TW_DRGS_SUIT_MARK='0' AND MR_ID > ?1 AND IN_DATE >= '1100101' ORDER BY ID", nativeQuery = true)
  public List<IP_D> findAllWithDRG(long maxId);
  
  /**
   * 取得該年月小於20件核實申報的DRG件數
   * @param inDate
   * @param inDate2
   * @param ym
   * @return
   */
  @Query(value = "SELECT TW_DRG_CODE, d.ID FROM IP_D d, MR mr WHERE TW_DRG_CODE IN (" + 
      "SELECT CODE FROM DRG_CODE WHERE STARTED = 1 AND CASE20 = 1 AND START_DATE <= ?1 AND END_DATE >= ?2" + 
      ") AND mr.ID = d.MR_ID AND mr.APPL_YM = ?3", nativeQuery = true)
  public List<Object[]> getDRGCase20Id(Date inDate, Date inDate2, String ym);
  
  /**
   *  住院各科申報總點數
   * @param sdate 啟始日
   * @param edate 結束日
   * @return [科別代碼, 申報金額, 件數]
   */
  @Query(value = "SELECT IP_D.FUNC_TYPE, SUM(IP_D.APPL_DOT), COUNT(1) FROM MR, IP_D " + 
      "WHERE MR_DATE >= ?1 AND MR_DATE <= ?2 AND IP_D.MR_ID = MR.ID GROUP BY IP_D.FUNC_TYPE", nativeQuery = true) 
  public List<Object[]> findApplPointGroupByFuncType(Date sdate, Date edate);
  
  /**
   * 住院各科部分負擔總金額
   * @param sdate
   * @param edate
   * @return [科別代碼, 部分負擔金額, 件數]
   */
  @Query(value = "SELECT IP_D.FUNC_TYPE, SUM(PART_DOT), COUNT(1) FROM MR, IP_D " + 
      "WHERE MR_DATE >= ?1 AND MR_DATE <= ?2 AND IP_D.MR_ID = MR.ID GROUP BY IP_D.FUNC_TYPE", nativeQuery = true) 
  public List<Object[]> findPartPointGroupByFuncType(Date sdate, Date edate);
  
  /**
   * 取得指定區間的DRG件數及點數
   * @param sdate1
   * @param edate1
   * @param sdate2
   * @param edate2
   * @return [門急診點數, 住院點數]
   */
  @Query(value ="SELECT * FROM " + 
      "(SELECT COUNT(1) AS DRG_QUANTITY FROM MR WHERE MR_DATE >= ?1 AND MR_DATE <= ?2 " + 
      "AND DRG_SECTION IS NOT NULL)," + 
      "(SELECT COUNT(1) AS IP_QUANTITY FROM MR WHERE MR_DATE >= ?1 AND MR_DATE <= ?2 "
      + "AND DRG_SECTION IS NULL AND DATA_FORMAT ='20')," +
      "(SELECT (SUM(IP_D.MED_DOT) - SUM(IP_D.PART_DOT)) AS IP_POINT FROM MR, IP_D " + 
      "WHERE MR_DATE >= ?3 AND MR_DATE <= ?4 AND IP_D.MR_ID = MR.ID)", nativeQuery = true)
  public List<Object[]> findDRGAllPoint(Date sdate1, Date edate1, Date sdate2, Date edate2);
  
  /**
   * 取得指定申報年月的所有OPD
   * @param applYm
   * @return
   */
  @Query(value = "SELECT * FROM IP_D WHERE MR_ID IN ("
      + "SELECT id FROM mr WHERE APPL_YM =?1) ORDER BY CASE_TYPE , SEQ_NO", nativeQuery = true)
  public List<IP_D> findByApplYM(String applYm);
}
