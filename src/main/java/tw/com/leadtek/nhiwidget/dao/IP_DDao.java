/**
 * Created on 2021/1/27.
 */
package tw.com.leadtek.nhiwidget.dao;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

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
  
  /**
   * 修正無MR_END_DATE的 MR
   */
  @Query(value = "  SELECT * FROM ip_d WHERE MR_ID IN ("+ 
      "SELECT id FROM mr WHERE MR_END_DATE IS NULL AND DATA_FORMAT ='20')", nativeQuery = true)
  public List<IP_D> findNoMrEndDateByIpd();
  
  /**
   * 修正新增的 LEAVE_DATE 欄位
   */
  @Query(value = "  SELECT * FROM ip_d WHERE OUT_DATE IS NOT NULL", nativeQuery = true)
  public List<IP_D> findOutDateIsNotNull();
  
  /**
   * 修正新增的 LEAVE_DATE 欄位
   */
  @Transactional
  @Modifying
  @Query(value = "UPDATE ip_d SET LEAVE_DATE=?1 WHERE ID=?2 ", nativeQuery = true)
  public void updateLeaveDate(Date leaveDate, long id);
  
  /**
   * 取得(診斷碼搭配手術碼的出現次數)
   * @param date
   * @return
   */
  @Query(value = "select * from (   "
  		+ "select mr.ICDCM1, ip.ICD_OP_CODE1, count(ip.ICD_OP_CODE1) as IOC1COUNT , ip.ICD_OP_CODE2, count(ip.ICD_OP_CODE2) as IOC2COUNT  from ip_d ip , mr   "
  		+ "where ip.roc_id = mr.roc_id and mr.MR_DATE  between ?1 and ?2 "
  		+ "group by ip.ICD_OP_CODE1, mr.ICDCM1, ip.ICD_OP_CODE2) temp  "
  		+ "where IOC1COUNT > 0 or IOC2COUNT > 0  "
  		+ "order by IOC1COUNT desc", nativeQuery = true)
  public List<Map<String, Object>> getHospitalOperation(String sDate, String eDate);
  /**
   * 取得(診斷碼搭配手術碼的出現次數)
   * @param date
   * @return
   */
  @Query(value = "select * from (   "
  		+ "select mr.ICDCM1, ip.ICD_OP_CODE1, count(ip.ICD_OP_CODE1) as IOC1COUNT , ip.ICD_OP_CODE2, count(ip.ICD_OP_CODE2) as IOC2COUNT  from ip_d ip , mr   "
  		+ "where ip.roc_id = mr.roc_id and mr.MR_DATE  between ?1 and ?2 "
  		+ "group by ip.ICD_OP_CODE1, mr.ICDCM1, ip.ICD_OP_CODE2) temp  "
  		+ "where IOC1COUNT > 0 or IOC2COUNT > 0  "
  		+ "order by IOC2COUNT desc", nativeQuery = true)
  public List<Map<String, Object>> getHospitalOperation2(String sDate, String eDate);
  /**
   * 如果包含牙科且case_type有其條件資料
   * @param mrId
   * @return
   */
  @Query(value = "select ipp.order_code, ipp.mr_id from  ip_d ipd "
  		+ "join ip_p ipp on ipd.id = ipp.ipd_id "
  		+ "join pt_payment_terms ppt on  ipp.order_code = ppt.nhi_no "
  		+ "join pt_outpatient_fee pof on ppt.id = pof.pt_id "
  		+ "where pof.no_dentisit = 0   and ipd.case_type in  ('09','11','12','13','14','16','17','19','21','22','23','24','25','28') "
  		+ "and ipd.mr_id in (?1) ", nativeQuery = true)
  public List<Map<String, Object>> getValidByNoDentisit(List<String> mrId);
  
  /**
   * 如果包含中醫且case_type有其條件資料
   * @param mrId
   * @return
   */
  @Query(value = "select ipp.order_code, ipp.mr_id from  ip_d ipd "
	  		+ "join ip_p ipp on ipd.id = ipp.ipd_id "
	  		+ "join pt_payment_terms ppt on  ipp.order_code = ppt.nhi_no "
	  		+ "join pt_outpatient_fee pof on ppt.id = pof.pt_id "
	  		+ "where pof.no_chi_medicine = 0   and ipd.case_type in  ('09','11','12','13','14','16','17','19','21','22','23','24','25','28') "
	  		+ "and ipd.mr_id in (?1) ", nativeQuery = true)
	  public List<Map<String, Object>> getValidByNoChiMedicine(List<String> mrId);
  /**
   * 查詢離島資料
   * @return
   */
  @Query(value = "select * from  ip_d ipd "
  		+ "join ip_p ipp on ipd.id = ipp.ipd_id where ipd.PART_NO = '007' ", nativeQuery = true)
  public List<Map<String, Object>> getPartNoByOutisLand();
  
  /**
   * 由mrid取得該病例生日
   * @param mrid
   * @return
   */
  @Query(value = "select ipd.MR_ID, ipd.ID_BIRTH_YMD from ip_d ipd "
  		+ "where ipd.mr_id  in(?1) group by ipd.MR_ID,ipd.ID_BIRTH_YMD", nativeQuery = true)
  public List<Map<String, Object>> getBirthByMrId(List<String> mridStr);
  
  /**
   * 由mrid取得ip_d 列表
   * @param mrid
   * @return
   */
  @Query(value = "select * from ip_d where mr_id in(?1) ", nativeQuery = true)
  public List<IP_D> getListByMrId(List<String> mrid);
  
  
}
