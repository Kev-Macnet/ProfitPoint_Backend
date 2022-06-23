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

import tw.com.leadtek.nhiwidget.dto.ClassDrugDotDto;
import tw.com.leadtek.nhiwidget.model.rdb.IP_D;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.model.rdb.OP_D;

public interface IP_DDao extends JpaRepository<IP_D, Long>, JpaSpecificationExecutor<IP_D> {

  public List<IP_D> findByIptId(Long iptId);
  
  public List<IP_D> findByMrId(Long mrid);
  
  //住院病例總點數
  @Query(value="SELECT IP.IP_DOT FROM "
  		+ "(SELECT (SUM(MED_DOT)+SUM(NON_APPL_DOT)) AS IP_DOT FROM IP_D WHERE IPT_ID IN ?1)IP", nativeQuery=true)
  public String findIPDot(List<Integer>ids);
  
  //住院案件數
  @Query(value="SELECT IP.IP_DOT FROM "
  		+ "(SELECT (COUNT(MED_DOT)+COUNT(NON_APPL_DOT)) AS IP_DOT FROM IP_D WHERE IPT_ID IN ?1)IP", nativeQuery=true)
  public String findIPCount(List<Integer>ids);
  
  //住院總藥費
  @Query(value="SELECT IP.IP_DOT FROM "
  		+ "(SELECT SUM(DRUG_DOT) AS IP_DOT FROM IP_D WHERE IPT_ID IN ?1)IP",nativeQuery=true)
  public String findIPDrugFee(List<Integer>ids);
  
  //各科別住院病歷總點數
  @Query(value="SELECT FUNC_TYPE,(SUM(MED_DOT)+SUM(NON_APPL_DOT)) AS IP_DOT FROM IP_D WHERE IPT_ID IN ?1 GROUP BY FUNC_TYPE", nativeQuery=true)
  public List<Object[]> findClassIP_TDot(List<Integer>ids);
  
  //各科別住院案件數
  @Query(value="SELECT FUNC_TYPE ,(COUNT(MED_DOT)+COUNT(NON_APPL_DOT)) FROM IP_D WHERE IPT_ID IN ?1 GROUP BY FUNC_TYPE", nativeQuery=true)
  public List<Object[]> findClassIPCount(List<Integer>ids);
  
  //各科別住院總藥品點數or總藥費
  @Query(value="SELECT FUNC_TYPE,SUM(DRUG_DOT) AS IP_DOT FROM "
  		+ "IP_D WHERE IPT_ID IN ?1 GROUP BY FUNC_TYPE",nativeQuery=true)
  public List<Object[]> findByIptIdAndGroupByFuncType(List<Integer>ids);
  
  //各科別各醫師住院案件數、病歷實際總點數、總藥品點數(總藥費)
  @Query(value="SELECT FUNC_TYPE,PRSN_ID ,(COUNT(MED_DOT)+COUNT(NON_APPL_DOT)),(SUM(MED_DOT)+SUM(NON_APPL_DOT))"
  		+ ",SUM(DRUG_DOT) FROM IP_D WHERE IPT_ID IN ?1 GROUP BY FUNC_TYPE ,PRSN_ID ",nativeQuery=true)
  public List<Object[]>findIPClassDoctor(List<Integer>ids);
  
  //各科別各醫師每週住院案件數、病歷實際總點數、總藥品點數(總藥費)
  @Query(value="SELECT FUNC_TYPE,PRSN_ID ,(COUNT(MED_DOT)+COUNT(NON_APPL_DOT)),(SUM(MED_DOT)+SUM(NON_APPL_DOT)),SUM(DRUG_DOT)"
  		+ "FROM IP_D WHERE OUT_DATE BETWEEN ?1 AND ?2 GROUP BY FUNC_TYPE ,PRSN_ID ORDER BY PRSN_ID",nativeQuery=true)
  public List<Object[]>findIPClassDoctorWeekly(String sdate,String edate);
  
  @Query(value =  "SELECT SEQ_NO ,ID ,ROC_ID, IN_DATE, MR_ID, ID_BIRTH_YMD FROM IP_D WHERE IPT_ID=?1 ", nativeQuery = true)
  public List<Object[]> findByIptIdSimple(Long iptId);
  
  @Query(value = "SELECT * FROM IP_D WHERE ID IN (SELECT D_ID FROM MR WHERE DATA_FORMAT = ?1 AND MR_DATE BETWEEN ?2 AND ?3) ", nativeQuery = true)
  public List<IP_D> findByIDFromMR(String dataFormat, Date sDate, Date eDate);
  
  @Query(value = "SELECT * FROM IP_D WHERE MR_ID IN (SELECT ID FROM MR WHERE DATA_FORMAT = '20' "
      + "AND MR_END_DATE >= ?1 AND MR_END_DATE <= ?2) ", nativeQuery = true)
  public List<IP_D> findByIDFromMR(java.util.Date sDate, java.util.Date eDate);
  
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
      "WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND IP_D.MR_ID = MR.ID GROUP BY IP_D.FUNC_TYPE", nativeQuery = true) 
  public List<Object[]> findApplPointGroupByFuncType(Date sdate, Date edate);
  
  /**
   * 住院各科部分負擔總金額
   * @param sdate
   * @param edate
   * @return [科別代碼, 部分負擔金額, 件數]
   */
  @Query(value = "SELECT IP_D.FUNC_TYPE, SUM(PART_DOT), COUNT(1) FROM MR, IP_D " + 
      "WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND IP_D.MR_ID = MR.ID GROUP BY IP_D.FUNC_TYPE", nativeQuery = true) 
  public List<Object[]> findPartPointGroupByFuncType(Date sdate, Date edate);
  
  /**
   * 住院各科自費總金額
   * @param sdate
   * @param edate
   * @return [科別代碼, 部分負擔金額, 件數]
   */
  @Query(value = "SELECT IP_D.FUNC_TYPE, SUM(IP_D.OWN_EXPENSE), COUNT(1) FROM MR, IP_D " + 
      "WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND IP_D.MR_ID = MR.ID AND IP_D.OWN_EXPENSE > 0 "
      + "GROUP BY IP_D.FUNC_TYPE", nativeQuery = true) 
  public List<Object[]> findOwnExpenseGroupByFuncType(Date sdate, Date edate);
  
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
  		+ "where pof.no_dentisit = 1   and ipd.case_type in  ('09','11','12','13','14','16','17','19','21','22','23','24','25','28') "
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
	  		+ "where pof.no_chi_medicine = 1   and ipd.case_type in  ('09','11','12','13','14','16','17','19','21','22','23','24','25','28') "
	  		+ "and ipd.mr_id in (?1) ", nativeQuery = true)
	  public List<Map<String, Object>> getValidByNoChiMedicine(List<String> mrId);
  /**
   * 查詢離島資料
   * @param ordercode
   * @param mridStr
   * @return
   */
  @Query(value = "SELECT MR_ID FROM ip_d WHERE id IN ( "
	  		+ "SELECT IPD_ID FROM ip_p WHERE ORDER_CODE = ?1 and MR_ID in (?2) ) "
	  		+ "AND part_no <> '007'", nativeQuery = true)
  public List<Map<String, Object>> getPartNoByOutisLand(String drugNo,List<String> mridStr);
  
  /**
   * 由mrid取得該住院病例
   * @param mrid
   * @return
   */
  @Query(value = "select * from ip_d where MR_ID in(?1)", nativeQuery = true)
  public List<IP_D> getDataListByMrId(List<String> mridStr);
  
  /**
   * 由mrid取得ip_d 列表
   * @param mrid
   * @return
   */
  @Query(value = "select * from ip_d where mr_id in(?1) ", nativeQuery = true)
  public List<IP_D> getListByMrId(List<String> mrid);
  
  /**
   * 每月醫療人員上限筆數比較
   * @param mrid
   * @param limit
   * @return
   */
  @Query(value = "select IN_DATE, IPDPRID, IPDCOUNT, IPPPRID, IPPCOUNT from ( "
  		+ "select substr(ipd.in_date, 1, 5) in_date, ipd.prsn_id ipdprid, count(ipd.prsn_id)ipdcount, ipp.prsn_id ippprid,count(ipp.prsn_id) ippcount from "
  		+ "(select * from ip_p where mr_id in (?1)) ipp,  "
  		+ "(select * from ip_d where  mr_id in (?1)) ipd "
  		+ "where ipp.ipd_id = ipp.id  "
  		+ "group by ipd.prsn_id, ipp.prsn_id)temp "
  		+ "where IPDCOUNT > ?2 or IPPCOUNT > ?2", nativeQuery = true)
  public List<Map<String, Object>> getPerMonthPrmanCount(List<String> mrid, int limit);
  
  @Transactional
  @Modifying
  @Query(value = "UPDATE IP_D SET FUNC_TYPE=?1, APPL_START_DATE=?2, APPL_END_DATE=?3, "
      + "ANE_DOT=?4, DSVC_DOT=?5, DIAG_DOT=?6, DRUG_DOT=?7, INJT_DOT=?8, ROOM_DOT=?9, "
      + "THRP_DOT=?10, AMIN_DOT=?11, OWN_EXPENSE=?12 WHERE ID=?13", nativeQuery = true)
  public void updateFuncTypeById(String funcType, String startDate, String endDate, 
      int aneDot, int dsvcDot, int diagDot, int drugDot, int injtDot, int roomDot, 
      int thrpDot, int aminDot, int ownExpense, Long id);  
  
  /**
   * 取得申報日期總人數
   * @param date
   * @return
   */
  @Query(value = "SELECT  ipd.* FROM MR mr, IP_D ipd "
  		+ "WHERE mr.id = ipd.MR_ID   AND mr.APPL_YM  = ?1 ", nativeQuery = true)
  public List<IP_D> getApplCountByApplYM(String date);
  
  /**
   * 取得未申報病歷資料
   * @return
   */
  @Query(value = "SELECT mr.APPL_YM , ipd.OUT_DATE , ipd.IN_DATE  FROM MR mr, IP_D ipd "
  		+ "WHERE mr.id = ipd.MR_ID   AND mr.APPL_YM  IS null", nativeQuery = true)
  public List<Map<String, Object>> getMrDataByApplYMNull();
  
  /**
   * 取得出院圓餅圖資料
   * @param sDate
   * @param eDate
   * @return
   */
  @Query(value = "SELECT  COUNT, ROUND(COUNT/ (SELECT SUM(COUNT) FROM (	 "
  		+ "SELECT COUNT(ipd.FUNC_TYPE) as COUNT, ipd.FUNC_TYPE, ct.DESC_CHI FROM IP_D ipd, CODE_TABLE ct WHERE ipd.FUNC_TYPE = ct.CODE AND  LEAVE_DATE BETWEEN ?1 AND ?2 AND ct.CAT ='FUNC_TYPE' GROUP BY ipd.FUNC_TYPE , ct.DESC_CHI) temp) * 100,2 ) AS PERCENT,FUNC_TYPE,DESC_CHI FROM "
  		+ "(SELECT COUNT(ipd.FUNC_TYPE) as COUNT, ipd.FUNC_TYPE, ct.DESC_CHI FROM IP_D ipd, CODE_TABLE ct WHERE ipd.FUNC_TYPE = ct.CODE AND  LEAVE_DATE BETWEEN ?1 AND ?2 AND ct.CAT ='FUNC_TYPE' GROUP BY ipd.FUNC_TYPE , ct.DESC_CHI) temp "
  		+ "", nativeQuery = true)
  public List<Map<String,Object>> getIPPieOutCountData(String sDate, String eDate);
  
  /**
   * 取得出院圓餅圖資料 total
   * @param sDate
   * @param eDate
   * @return
   */
  @Query(value = "SELECT SUM(COUNT) FROM (	 "
  		+ "SELECT COUNT(ipd.FUNC_TYPE) as COUNT, ipd.FUNC_TYPE, ct.DESC_CHI FROM IP_D ipd, CODE_TABLE ct WHERE ipd.FUNC_TYPE = ct.CODE AND  LEAVE_DATE BETWEEN ?1 AND ?2 AND ct.CAT ='FUNC_TYPE' GROUP BY ipd.FUNC_TYPE , ct.DESC_CHI) temp", nativeQuery = true)
  public int getIPPieOutCountToal(String sDate, String eDate);
  
  /**
   * 取得住院圓餅圖資料-人
   * @param date
   * @return
   */
  @Query(value = "SELECT COUNT, ROUND(COUNT / (SELECT SUM(COUNT) FROM "
  		+ "(SELECT COUNT(ipd.FUNC_TYPE) AS COUNT, ipd.FUNC_TYPE, ct.DESC_CHI FROM IP_D ipd, CODE_TABLE ct  WHERE ipd.FUNC_TYPE = ct.CODE  AND  ipd.IPT_ID in (SELECT ID FROM IP_T WHERE  FEE_YM  LIKE CONCAT(?1,'%')) AND ct.CAT ='FUNC_TYPE' GROUP BY ipd.FUNC_TYPE , ct.DESC_CHI)temp) * 100,2 ) AS PERCENT,FUNC_TYPE,DESC_CHI FROM"
  		+ "(SELECT COUNT(ipd.FUNC_TYPE) AS COUNT, ipd.FUNC_TYPE, ct.DESC_CHI FROM IP_D ipd, CODE_TABLE ct  WHERE ipd.FUNC_TYPE = ct.CODE  AND  ipd.IPT_ID in (SELECT ID FROM IP_T WHERE  FEE_YM  LIKE CONCAT(?1,'%')) AND ct.CAT ='FUNC_TYPE' GROUP BY ipd.FUNC_TYPE , ct.DESC_CHI) temp "
  		, nativeQuery = true)
  public List<Map<String,Object>> getIPPieCountData(String date);
  
  /**
   * 取得住院圓餅圖資料-人 total
   * @param date
   * @return
   */
  @Query(value = "SELECT SUM(COUNT) FROM "
  		+ "(SELECT COUNT(ipd.FUNC_TYPE) AS COUNT, ipd.FUNC_TYPE, ct.DESC_CHI FROM IP_D ipd, CODE_TABLE ct  WHERE ipd.FUNC_TYPE = ct.CODE  AND  ipd.IPT_ID in (SELECT ID FROM IP_T WHERE  FEE_YM  LIKE CONCAT(?1,'%')) AND ct.CAT ='FUNC_TYPE' GROUP BY ipd.FUNC_TYPE , ct.DESC_CHI)temp", nativeQuery = true)
  public int getIPPieCountTotal(String date);
  
  /**
   * 取得住院圓餅圖資料-點數
   * @param date
   * @return
   */
  @Query(value = "SELECT  SUM, ROUND(SUM / (SELECT SUM(SUM) AS SUM FROM "
  		+ "(SELECT (SUM(ipd.PART_DOT) + SUM(ipd.APPL_DOT)) AS SUM, ipd.FUNC_TYPE, ct.DESC_CHI FROM  IP_D ipd, CODE_TABLE ct WHERE ipd.FUNC_TYPE  = ct.CODE AND  ipd.IPT_ID IN (SELECT ID FROM IP_T WHERE  FEE_YM  LIKE CONCAT(?1,'%')) AND ct.CAT ='FUNC_TYPE' GROUP BY ipd.FUNC_TYPE, ct.DESC_CHI) temp "
  		+ ") * 100, 2) AS PERCENT, FUNC_TYPE,DESC_CHI FROM  "
  		+ "(SELECT (SUM(ipd.PART_DOT) + SUM(ipd.APPL_DOT)) AS SUM, ipd.FUNC_TYPE, ct.DESC_CHI FROM  IP_D ipd, CODE_TABLE ct WHERE ipd.FUNC_TYPE  = ct.CODE AND  ipd.IPT_ID IN (SELECT ID FROM IP_T WHERE  FEE_YM  LIKE CONCAT(?1,'%')) AND ct.CAT ='FUNC_TYPE' GROUP BY ipd.FUNC_TYPE, ct.DESC_CHI) temp "
  		+ " ", nativeQuery = true)
  public List<Map<String,Object>> getIPPieDotData(String date);
  
  /**
   * 取得住院圓餅圖資料-點數 total
   * @param date
   * @return
   */
  @Query(value = "SELECT SUM(SUM) AS SUM FROM "
  		+ "(SELECT (SUM(ipd.PART_DOT) + SUM(ipd.APPL_DOT)) AS SUM, ipd.FUNC_TYPE, ct.DESC_CHI FROM  IP_D ipd, CODE_TABLE ct WHERE ipd.FUNC_TYPE  = ct.CODE AND  ipd.IPT_ID IN (SELECT ID FROM IP_T WHERE  FEE_YM  LIKE CONCAT(?1,'%')) AND ct.CAT ='FUNC_TYPE' GROUP BY ipd.FUNC_TYPE, ct.DESC_CHI) temp "
  		, nativeQuery = true)
  public int getIPPieDotTotal(String date);

  @Query(value = "SELECT DISTINCT (ORDER_CODE), ORDER_TYPE FROM ip_p where ORDER_TYPE IS NOT NULL \r\n" + 
      "and ORDER_TYPE <> '4' and length (ORDER_CODE) <10 GROUP BY ORDER_CODE , ORDER_TYPE", nativeQuery = true)
  public List<Map<String,Object>> getOrderCodeAndOrderType();
}
