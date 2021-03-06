/**
 * Created on 2021/1/26.
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
import tw.com.leadtek.nhiwidget.model.rdb.OP_D;

public interface OP_DDao extends JpaRepository<OP_D, Long>, JpaSpecificationExecutor<OP_D> {

  public List<OP_D> findByOptId(Long optId);
  
  public List<OP_D> findByMrId(Long mrId);
  
  //門急診/住院病例總點數
  @Query(value="SELECT OP.OP_DOT + IP.IP_DOT FROM "
  		+ "(SELECT SUM(T_DOT) AS OP_DOT FROM OP_D WHERE OPT_ID IN ?1)OP,"
  		+ "(SELECT (SUM(MED_DOT)+SUM(NON_APPL_DOT)) AS IP_DOT FROM IP_D WHERE IPT_ID IN ?2)IP", nativeQuery=true)
  public String findTDot(List<Integer>ids,List<Integer>ids2);
  
  //門急診/住院案件數
  @Query(value="SELECT OP.OP_DOT + IP.IP_DOT FROM "
  		+ "(SELECT COUNT(T_DOT) AS OP_DOT FROM OP_D WHERE OPT_ID IN ?1)OP,"
  		+ "(SELECT (COUNT(MED_DOT)+COUNT(NON_APPL_DOT)) AS IP_DOT FROM IP_D WHERE IPT_ID IN ?2)IP", nativeQuery=true)
  public String findTCount(List<Integer>ids,List<Integer>ids2);
  
  //門急診/住院總藥費
  @Query(value="SELECT OP.OP_DOT + IP.IP_DOT FROM "
  		+ "(SELECT SUM(DRUG_DOT) AS OP_DOT FROM OP_D WHERE OPT_ID IN ?1)OP,"
  		+ "(SELECT SUM(DRUG_DOT) AS IP_DOT FROM IP_D WHERE IPT_ID IN ?2)IP",nativeQuery=true)
  public String findTDrugFee(List<Integer>ids,List<Integer>ids2);
  
  //門急診病例總點數
  @Query(value="SELECT OP.OP_DOT FROM "
	  		+ "(SELECT SUM(T_DOT) AS OP_DOT FROM OP_D WHERE OPT_ID IN ?1)OP", nativeQuery=true)
  public String findOPDot(List<Integer>ids);
  
  //門急診案件數
  @Query(value="SELECT OP.OP_DOT FROM "
  		+ "(SELECT COUNT(T_DOT) AS OP_DOT FROM OP_D WHERE OPT_ID IN ?1)OP", nativeQuery=true)
  public String findOPCount(List<Integer>ids);
  
  //門急診總藥費
  @Query(value="SELECT OP.OP_DOT FROM "
	  		+ "(SELECT SUM(DRUG_DOT) AS OP_DOT FROM OP_D WHERE OPT_ID IN ?1)OP",nativeQuery=true)
  public String findOPDrugFee(List<Integer>ids);
  
  //各科別門急診病例總點數
  @Query(value="SELECT FUNC_TYPE ,SUM(T_DOT) FROM OP_D WHERE OPT_ID IN ?1 GROUP BY FUNC_TYPE", nativeQuery=true)
  public List<Object[]> findClassOP_TDot(List<Integer>ids);
  
  //各科別門急診案件數
  @Query(value="SELECT FUNC_TYPE, COUNT(T_DOT) FROM OP_D WHERE OPT_ID IN ?1 GROUP BY FUNC_TYPE", nativeQuery=true)
  public List<Object[]> findClassOPCount(List<Integer>ids);
  
  //各科別門急診總藥品點數or總藥費
  @Query(value="SELECT FUNC_TYPE,SUM(DRUG_DOT) AS OP_DOT FROM "
  		+ "OP_D WHERE OPT_ID IN ?1 GROUP BY FUNC_TYPE",nativeQuery=true)
  public List<Object[]> findByOptIdAndGroupByFuncType(List<Integer>ids);
  
  //各科別各醫師門急診案件數、病歷實際總點數、總藥品點數(總藥費)
  @Query(value="SELECT FUNC_TYPE, PRSN_ID ,COUNT(T_DOT),SUM(T_DOT),"
  		+ "SUM(DRUG_DOT) FROM OP_D WHERE OPT_ID IN ?1 GROUP BY FUNC_TYPE ,PRSN_ID ",nativeQuery=true)
  public List<Object[]>findOPClassDoctor(List<Integer>ids);
  
  //各科別各醫師每週門急診案件數、病歷實際總點數、總藥品點數(總藥費)
  @Query(value="SELECT FUNC_TYPE, PRSN_ID,COUNT(T_DOT) ,SUM(T_DOT), SUM(DRUG_DOT) "
  		+ "FROM OP_D WHERE FUNC_END_DATE BETWEEN ?1 AND ?2 GROUP BY FUNC_TYPE ,PRSN_ID ORDER BY PRSN_ID",nativeQuery=true)
  public List<Object[]>findOPClassDoctorWeekly(String sdate,String edate);
  
  @Query(value = "SELECT SEQ_NO, ID, ROC_ID, FUNC_DATE, MR_ID, ID_BIRTH_YMD FROM OP_D WHERE OPT_ID= ?1 ", nativeQuery = true)
  public List<Object[]> findByOptIdSimple(Long optId);
  
  @Query(value = "SELECT * FROM OP_D WHERE MR_ID IN (SELECT ID FROM MR WHERE DATA_FORMAT = '10' "
      + "AND MR_END_DATE >= ?1 AND MR_END_DATE <= ?2) ", nativeQuery = true)
  public List<OP_D> findByIDFromMR(java.util.Date sDate, java.util.Date eDate);
  
  @Query(value = "SELECT DISTINCT(PRSN_ID) , FUNC_TYPE "
      + "FROM OP_D WHERE PRSN_ID LIKE '%***%' AND FUNC_TYPE IS NOT NULL ORDER BY FUNC_TYPE", nativeQuery = true)
  public List<Object[]> findDepartmentAndDoctor();
  
  /**
   * 取得單月門診、急診、住院部份負擔點數
   */
  @Query(value = "SELECT * FROM " + 
      "(SELECT SUM(PART_DOT) AS PART_OP FROM OP_D, MR WHERE OP_D.MR_ID = MR.ID AND MR.APPL_YM=?1 AND MR.FUNC_TYPE <> '22') a,"
      + "(SELECT SUM(PART_DOT) AS PART_EM FROM OP_D, MR WHERE OP_D.MR_ID = MR.ID AND MR.APPL_YM=?1 AND MR.FUNC_TYPE = '22') b,"
      + "(SELECT SUM(PART_DOT) AS PART_IP FROM IP_D, MR WHERE IP_D.MR_ID = MR.ID AND MR.APPL_YM=?1 ) c,"
      + "(SELECT SUM(T_APPL_DOT) AS APPL_OP FROM OP_D, MR WHERE OP_D.MR_ID = MR.ID AND MR.APPL_YM=?1 AND MR.FUNC_TYPE <> '22') d,"
      + "(SELECT SUM(T_APPL_DOT) AS APPL_EM FROM OP_D, MR WHERE OP_D.MR_ID = MR.ID AND MR.APPL_YM=?1 AND MR.FUNC_TYPE = '22') e,"
      + "(SELECT SUM(APPL_DOT) AS APPL_IP FROM MR WHERE DATA_FORMAT ='20' AND APPL_YM =?1) f,"
      + "(SELECT COUNT(1) AS PATIENT_OP FROM OP_D, MR WHERE OP_D.MR_ID = MR.ID AND MR.APPL_YM=?1 AND MR.FUNC_TYPE <> '22') g,"
      + "(SELECT COUNT(1) AS PATIENT_EM FROM OP_D, MR WHERE OP_D.MR_ID = MR.ID AND MR.APPL_YM=?1 AND MR.FUNC_TYPE = '22') h ,"
      + "(SELECT COUNT(1) AS PATIENT_IP FROM IP_D, MR WHERE IP_D.MR_ID = MR.ID AND MR.APPL_YM=?1 AND IP_D.OUT_DATE IS NOT NULL) i,"
      + "(SELECT 0 AS CHRONIC FROM OP_D LIMIT 1) j,"
      + "(SELECT COUNT(1) AS IP_QUANTITY FROM MR WHERE APPL_YM =?1 AND DATA_FORMAT ='20') k,"
      + "(SELECT COUNT(1) AS DRG_QUANTITY FROM MR WHERE DRG_SECTION IS NOT NULL AND APPL_YM=?1) l,"
      + "(SELECT SUM(MR.T_DOT) AS DRG_APPLDOT, SUM(IP_D.MED_DOT) AS DRG_ACTUAL_POINT FROM MR, IP_D WHERE "
      + "MR.DRG_SECTION IS NOT NULL AND MR.APPL_YM=?1 AND MR.ID = IP_D.MR_ID) m,"
      + "(SELECT SUM(IP_D.OWN_EXPENSE) AS OWN_EXPENSE_IP FROM IP_D, MR WHERE IP_D.MR_ID = MR.ID AND MR.APPL_YM =?1 AND IP_D.OWN_EXPENSE > 0) n,"
      + "(SELECT SUM(OP_D.OWN_EXPENSE) AS OWN_EXPENSE_OP FROM OP_D, MR WHERE OP_D.MR_ID = MR.ID AND MR.APPL_YM =?1 AND OP_D.OWN_EXPENSE > 0) o", nativeQuery = true)
  public List<Object[]> findMonthlyPoint(String applYm);
  
  /**
   * 取得指定區間的病歷數、申報點數及部份負擔點數
   */
  @Query(value = "SELECT * FROM "
      + "(SELECT COUNT(1) AS ALL_COUNT FROM MR WHERE MR_END_DATE >= ?1 AND MR_END_DATE <=?2) a,"
      + "(SELECT COUNT(1) AS OP_ALL_COUNT FROM MR WHERE DATA_FORMAT ='10' AND MR_END_DATE >= ?1 AND MR_END_DATE <= ?2) b,"
      + "(SELECT COUNT(1) AS OP_COUNT FROM MR WHERE DATA_FORMAT ='10' AND FUNC_TYPE <> '22' AND MR_END_DATE >= ?1 AND MR_END_DATE <=?2) c ,"
      + "(SELECT COUNT(1) AS OP_EM_COUNT FROM MR WHERE DATA_FORMAT ='10' AND FUNC_TYPE = '22' AND MR_END_DATE >= ?1 AND MR_END_DATE <=?2) d,"
      + "(SELECT COUNT(1) AS IP_COUNT FROM MR WHERE DATA_FORMAT ='20' AND MR_END_DATE >= ?1 AND MR_END_DATE <= ?2) e,"
      + "(SELECT SUM(T_APPL_DOT) AS APPL_OP_ALL FROM MR, OP_D WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND OP_D.MR_ID = MR.ID) f,"
      + "(SELECT SUM(T_APPL_DOT) AS APPL_OP FROM MR, OP_D WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND OP_D.MR_ID = MR.ID AND OP_D.FUNC_TYPE <> '22') g,"
      + "(SELECT SUM(T_APPL_DOT) AS APPL_EM FROM MR, OP_D WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND OP_D.MR_ID = MR.ID AND OP_D.FUNC_TYPE = '22') h,"
      + "(SELECT SUM(APPL_DOT) AS APPL_IP FROM MR WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND DATA_FORMAT = '20') i,"
      + "(SELECT SUM(PART_DOT) AS PART_OP_ALL FROM MR, OP_D WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND OP_D.MR_ID = MR.ID) j,"
      + "(SELECT SUM(PART_DOT) AS PART_OP FROM MR, OP_D WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND OP_D.MR_ID = MR.ID AND OP_D.FUNC_TYPE <> '22') k,"
      + "(SELECT SUM(PART_DOT) AS PART_EM FROM MR, OP_D WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND OP_D.MR_ID = MR.ID AND OP_D.FUNC_TYPE = '22') l,"
      + "(SELECT SUM(PART_DOT) AS PART_IP FROM MR, IP_D WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND IP_D.MR_ID = MR.ID) m,"
      + "(SELECT SUM(OP_D.OWN_EXPENSE) AS OWN_EXPENSE_OP_ALL FROM MR, OP_D WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND OP_D.MR_ID = MR.ID) n,"
      + "(SELECT SUM(OP_D.OWN_EXPENSE) AS OWN_EXPENSE_OP FROM MR, OP_D WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND OP_D.MR_ID = MR.ID AND OP_D.FUNC_TYPE <> '22') o,"
      + "(SELECT SUM(OP_D.OWN_EXPENSE) AS OWN_EXPENSE_EM FROM MR, OP_D WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND OP_D.MR_ID = MR.ID AND OP_D.FUNC_TYPE = '22') p,"
      + "(SELECT SUM(IP_D.OWN_EXPENSE) AS OWN_EXPENSE_IP FROM MR, IP_D WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND IP_D.MR_ID = MR.ID) q,"
      + "(SELECT SUM(NO_APPL) AS NO_APPL_OP_ALL FROM MR, OP_D WHERE MR_END_DATE >= ?1 AND MR_END_DATE <=?2 AND OP_D.MR_ID = MR.ID) r,"
      + "(SELECT SUM(NO_APPL) AS NO_APPL_OP FROM MR, OP_D WHERE MR_END_DATE >= ?1 AND MR_END_DATE <=?2 AND OP_D.MR_ID = MR.ID AND OP_D.FUNC_TYPE <> '22') s,"
      + "(SELECT SUM(NO_APPL) AS NO_APPL_EM FROM MR, OP_D WHERE MR_END_DATE >= ?1 AND MR_END_DATE <=?2 AND OP_D.MR_ID = MR.ID AND OP_D.FUNC_TYPE = '22') t,"
      + "(SELECT SUM(NON_APPL_DOT) AS NO_APPL_IP, SUM(MED_DOT) AS MED_DOT FROM MR, IP_D WHERE MR_END_DATE >= ?1 AND MR_END_DATE <=?2 AND IP_D.MR_ID = MR.ID) u",
      nativeQuery = true)
  public List<Object[]> findPeriodPoint(Date sdate1, Date edate1);
  
  /**
   * 門診各科申報總點數
   * @param sdate
   * @param edate
   * @return  [科別代碼, 申報金額, 件數]
   */
  @Query(value = "SELECT OP_D.FUNC_TYPE, SUM(T_APPL_DOT), COUNT(1) FROM MR, OP_D " + 
      "WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND OP_D.MR_ID = MR.ID GROUP BY OP_D.FUNC_TYPE", nativeQuery = true) 
  public List<Object[]> findApplPointGroupByFuncType(Date sdate, Date edate);
  
  /**
   * 門診各科部分負擔總金額
   * @param sdate
   * @param edate
   * @return  [科別代碼, 部分負擔金額, 件數]
   */
  @Query(value = "SELECT OP_D.FUNC_TYPE, SUM(PART_DOT), COUNT(1) FROM MR, OP_D " + 
      "WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND OP_D.MR_ID = MR.ID GROUP BY OP_D.FUNC_TYPE", nativeQuery = true) 
  public List<Object[]> findPartPointGroupByFuncType(Date sdate, Date edate);
  
  /**
   * 門診各科自費總金額
   * @param sdate
   * @param edate
   * @return  [科別代碼, 部分負擔金額, 件數]
   */
  @Query(value = "SELECT OP_D.FUNC_TYPE, SUM(OP_D.OWN_EXPENSE), COUNT(1) FROM MR, OP_D " + 
      "WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND OP_D.MR_ID = MR.ID AND "
      + "OP_D.OWN_EXPENSE > 0 GROUP BY OP_D.FUNC_TYPE", nativeQuery = true) 
  public List<Object[]> findOwnExpenseGroupByFuncType(Date sdate, Date edate);
  
  /**
   * 取得指定區間的(1)門急診點數,(2)住院點數(申報+部分負擔),(3)急診點數,(4)門診人次,(5)住院人次(6)出院人次
   */
  @Query(value ="SELECT * FROM " + 
      "(SELECT SUM(T_DOT) AS OP_POINT FROM MR WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND DATA_FORMAT ='10') OP," + 
      "(SELECT SUM(T_DOT) AS IP_POINT FROM MR WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND DATA_FORMAT ='20') IP," + 
      "(SELECT SUM(MR.T_DOT) AS EM_POINT FROM MR, OP_D WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 " + 
      "AND OP_D.MR_ID = MR.ID AND OP_D.FUNC_TYPE='22') EM," +
      "(SELECT SUM(OP_D.OWN_EXPENSE) AS OP_OWN_EXPENSE FROM MR, OP_D WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND OP_D.MR_ID = MR.ID) OP_OWN," +
      "(SELECT SUM(IP_D.OWN_EXPENSE) AS IP_OWN_EXPENSE FROM MR, IP_D WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND IP_D.MR_ID = MR.ID) IP_OWN," +
      "(SELECT COUNT(1) AS OP_VISITS FROM MR WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND DATA_FORMAT ='10') OP_VISITS," + 
      "(SELECT COUNT(1) AS IP_VISITS FROM MR WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND DATA_FORMAT ='20') IP_VISITS," + 
      "(SELECT COUNT(1) AS IP_LEAVE FROM IP_D WHERE LEAVE_DATE >= ?1 AND LEAVE_DATE <= ?2) IP_LEAVE", nativeQuery = true)
  public List<Object[]> findAllPoint(Date sdate1, Date edate1);
  
  /**
   * 取得指定區間的(1)門急診點數,(2)住院點數(申報+部分負擔),(3)急診點數,(4)門診人次,(5)住院人次(6)出院人次
   */
  @Query(value ="SELECT * FROM " + 
      "(SELECT SUM(MR.T_DOT) AS OP_POINT FROM MR WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 "
      + "AND MR.FUNC_TYPE=?3 AND DATA_FORMAT='10') OP," + 
      "(SELECT  SUM(MR.T_DOT) AS IP_POINT FROM MR WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 " 
      + "AND MR.FUNC_TYPE=?3 AND DATA_FORMAT='20') IP," + 
      "(SELECT SUM(MR.T_DOT) AS EM_POINT FROM MR WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 " 
      + "AND MR.FUNC_TYPE='22' AND DATA_FORMAT='10') EM," + 
      "(SELECT SUM(OP_D.OWN_EXPENSE) AS OP_OWN_EXPENSE FROM MR, OP_D WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 "
      + "AND MR.FUNC_TYPE=?3 AND OP_D.MR_ID = MR.ID) OP_OWN, " +
      "(SELECT SUM(IP_D.OWN_EXPENSE) AS IP_OWN_EXPENSE FROM MR, IP_D WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 "
      + "AND MR.FUNC_TYPE=?3 AND IP_D.MR_ID = MR.ID) IP_OWN, " +
      "(SELECT COUNT(1) AS OP_VISITS FROM MR WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND MR.FUNC_TYPE=?3 AND DATA_FORMAT ='10') OP_VISITS," + 
      "(SELECT COUNT(1) AS IP_VISITS FROM MR WHERE MR_END_DATE >= ?1 AND MR_END_DATE <= ?2 AND MR.FUNC_TYPE=?3 AND DATA_FORMAT ='20') IP_VISITS," + 
      "(SELECT COUNT(1) AS IP_LEAVE FROM IP_D WHERE LEAVE_DATE >= ?1 AND LEAVE_DATE <= ?2 AND FUNC_TYPE=?3) IP_LEAVE", nativeQuery = true)
  public List<Object[]> findAllPointByFuncType(Date sdate1, Date edate1, String funcType1);
  
  /**
   * 取得指定申報年月的所有OPD
   * @param applYm
   * @return
   */
  @Query(value = "SELECT * FROM OP_D WHERE MR_ID IN ("
      + "SELECT id FROM mr WHERE APPL_YM =?1) ORDER BY CASE_TYPE , SEQ_NO", nativeQuery = true)
  public List<OP_D> findByApplYM(String applYm);
  
  /**
   * 修正無MR_END_DATE的 MR
   */
  @Query(value = "  SELECT * FROM op_d WHERE MR_ID IN ("+ 
      "SELECT id FROM mr WHERE MR_END_DATE IS NULL AND DATA_FORMAT ='10')" + 
      "AND FUNC_END_DATE IS NOT NULL AND FUNC_DATE <> FUNC_END_DATE", nativeQuery = true)
  public List<OP_D> findNoMrEndDateByOpd();
  /**
   * 取得(診斷碼搭配手術碼的出現次數)
   * @param date
   * @return
   */
  @Query(value = "select * from (   "
  		+ "select mr.ICDCM1, op.ICD_OP_CODE1, count(op.ICD_OP_CODE1) as IOC1COUNT , op.ICD_OP_CODE2, count(op.ICD_OP_CODE2) as IOC2COUNT  from op_d op , mr   "
  		+ "where op.roc_id = mr.roc_id and mr.MR_DATE between ?1 and ?2 "
  		+ "group by op.ICD_OP_CODE1, mr.ICDCM1, op.ICD_OP_CODE2) temp  "
  		+ "where IOC1COUNT > 0 or IOC2COUNT > 0  "
  		+ "order by IOC1COUNT desc ", nativeQuery = true)
  public List<Map<String, Object>> getClinicOperation(String sDate, String eDate);
  /**
   * 取得(診斷碼搭配手術碼的出現次數)
   * @param date
   * @return
   */
  @Query(value = "select * from (   "
  		+ "select mr.ICDCM1, op.ICD_OP_CODE1, count(op.ICD_OP_CODE1) as IOC1COUNT , op.ICD_OP_CODE2, count(op.ICD_OP_CODE2) as IOC2COUNT  from op_d op , mr   "
  		+ "where op.roc_id = mr.roc_id and mr.MR_DATE between ?1 and ?2 "
  		+ "group by op.ICD_OP_CODE1, mr.ICDCM1, op.ICD_OP_CODE2) temp  "
  		+ "where IOC1COUNT > 0 or IOC2COUNT > 0  "
  		+ "order by IOC2COUNT desc ", nativeQuery = true)
  public List<Map<String, Object>> getClinicOperation2(String sDate, String eDate);
  
  /**
   * 如果包含牙科且case_type有其條件資料
   * @param mrId
   * @return
   */
  @Query(value = "select opp.DRUG_NO, opp.mr_id from  op_d opd  "
  		+ "join op_p opp on opd.id = opp.opd_id  "
  		+ "join pt_payment_terms ppt on  opp.drug_no = ppt.nhi_no  "
  		+ "join pt_outpatient_fee pof on ppt.id = pof.pt_id  "
  		+ "where pof.no_dentisit = 1   and opd.case_type in  ('09','11','12','13','14','16','17','19','21','22','23','24','25','28') "
  		+ "and opd.mr_id in (?1) ", nativeQuery = true)
  public List<Map<String, Object>> getValidByNoDentisit(List<String> mrId);
  
  /**
   * 如果包含中醫且case_type有其條件資料
   * @param mrId
   * @return
   */
  @Query(value = " select opp.drug_no, opp.mr_id from  op_d opd  "
  		+ "join op_p opp on opd.id = opp.opd_id  "
  		+ "join pt_payment_terms ppt on  opp.drug_no = ppt.nhi_no  "
  		+ "join pt_outpatient_fee pof on ppt.id = pof.pt_id "
  		+ "where pof.no_chi_medicine = 1   and opd.case_type in  ('09','11','12','13','14','16','17','19','21','22','23','24','25','28') "
	  	+ "and opd.mr_id in (?1) ", nativeQuery = true)
	  public List<Map<String, Object>> getValidByNoChiMedicine(List<String> mrId);
  
  /**
   * 查詢離島資料
   * @param drugNo
   * @param mridStr
   * @return
   */
  @Query(value = "SELECT MR_ID FROM op_D WHERE id IN ( "
  		+ "SELECT OPD_ID FROM op_p WHERE DRUG_NO = ?1 and MR_ID in (?2) ) "
  		+ "AND part_no <> '007'", nativeQuery = true)
  public List<Map<String, Object>> getPartNoByOutisLand(String drugNo,List<String> mridStr);
  
  /**
   * 由mrid取得該門診病例
   * @param mrid
   * @return
   */
  @Query(value = "select * from op_d where MR_ID in(?1)", nativeQuery = true)
  public List<OP_D> getDataListByMrId(List<String> mridStr);
  
  /**
   * 由func & mrid取得醫療人員人數
   * @param mrid
   * @param func
   * @return
   */
  @Query(value = "select count(PRSN_ID) prsnCount, count(PHAR_ID) pharCount from op_d where func_type = ?1 and mr_id in (?2)  group by PRSN_ID, PHAR_ID ", nativeQuery = true)
  public List<Map<String, Object>> getPersonCountByFuncAndMrId(String func, String mrid);
  /**
   * 由mrid取得op_d 列表
   * @param mrid
   * @return
   */
  @Query(value = "select * from op_d where mr_id in(?1) ", nativeQuery = true)
  public List<OP_D> getListByMrId(List<String> mrid);
  /**
   * 每月醫療人員上限筆數比較
   * @param mrid
   * @param limit
   * @return
   */
  @Query(value = "select FUNC_DATE , PRSN_ID, PRCOUNT, PHAR_ID, PHCOUNT from "
  		+ "(select substr(func_date, 1, 5) func_date, prsn_id,count(prsn_id) prcount, phar_id,count(phar_id) phcount from op_d where mr_id in (?1) "
  		+ "group by prsn_id,phar_id)temp "
  		+ "where prcount > ?2 or phcount > ?2 ", nativeQuery = true)
  public List<Map<String, Object>> getPerMonthPrmanCount(List<String> mrid, int limit);

  @Transactional
  @Modifying
  @Query(value = "UPDATE OP_D SET FUNC_TYPE=?1, DRUG_DOT=?2, TREAT_DOT=?3, METR_DOT=?4, DIAG_DOT=?5, DSVC_DOT=?6 WHERE ID=?7", nativeQuery = true)
  public void updateFuncTypeById(String funcType, int drugDot, int treatDot, int metrDot, int diagDot, int dsvcDot, Long id);
  /**
   * 門急診圓餅圖人數-人
   * @param date
   * @return
   */
  @Query(value = "SELECT COUNT, ROUND(COUNT / (SELECT sum(COUNT) FROM "
  		+ "(SELECT COUNT(opd.FUNC_TYPE) AS COUNT, opd.FUNC_TYPE, ct.DESC_CHI  FROM OP_D opd, CODE_TABLE ct WHERE opd.FUNC_TYPE  = ct.CODE "
  		+ "AND  opd.OPT_ID in (SELECT ID FROM OP_T WHERE  FEE_YM  LIKE CONCAT(?1,'%'))  AND ct.CAT ='FUNC_TYPE' "
  		+ "GROUP BY opd.FUNC_TYPE, ct.DESC_CHI) temp) * 100,2) AS PERCENT, FUNC_TYPE, DESC_CHI FROM  "
  		+ "(SELECT COUNT(opd.FUNC_TYPE) AS COUNT, opd.FUNC_TYPE, ct.DESC_CHI  FROM OP_D opd, CODE_TABLE ct WHERE opd.FUNC_TYPE  = ct.CODE "
  		+ "AND  opd.OPT_ID in (SELECT ID FROM OP_T WHERE  FEE_YM  LIKE CONCAT(?1,'%')) AND ct.CAT ='FUNC_TYPE' "
  		+ "GROUP BY opd.FUNC_TYPE, ct.DESC_CHI) temp", nativeQuery = true)
  public List<Map<String,Object>> getOPPieCountData(String date);
  
  /**
   * 門急診圓餅圖人數-人total
   * @param date
   * @return
   */
  @Query(value = "SELECT sum(COUNT) as TOTAL FROM "
  		+ "(SELECT COUNT(opd.FUNC_TYPE) AS COUNT, opd.FUNC_TYPE, ct.DESC_CHI  FROM OP_D opd, CODE_TABLE ct WHERE opd.FUNC_TYPE  = ct.CODE "
  		+ "AND  opd.OPT_ID in (SELECT ID FROM OP_T WHERE  FEE_YM  LIKE CONCAT(?1,'%')) AND ct.CAT ='FUNC_TYPE'  "
  		+ "GROUP BY opd.FUNC_TYPE, ct.DESC_CHI) temp", nativeQuery = true)
  public int getOPPieCountTotal(String date);
  
  /**
   * 門急診圓餅圖人數-點數
   * @param date
   * @return
   */
  @Query(value = "SELECT SUM, ROUND(SUM / (SELECT sum(SUM) FROM "
  		+ "(SELECT (SUM(opd.T_APPL_DOT) + SUM(opd.PART_DOT)) AS SUM, opd.FUNC_TYPE, ct.DESC_CHI  FROM OP_D opd, CODE_TABLE ct WHERE opd.FUNC_TYPE  = ct.CODE AND  opd.OPT_ID IN (SELECT ID FROM OP_T WHERE  FEE_YM  LIKE CONCAT(?1,'%')) AND ct.CAT ='FUNC_TYPE' GROUP BY opd.FUNC_TYPE, ct.DESC_CHI) temp) * 100,2) AS PERCENT, FUNC_TYPE, DESC_CHI FROM  "
  		+ "(SELECT (SUM(opd.T_APPL_DOT) + SUM(opd.PART_DOT))  AS SUM,  opd.FUNC_TYPE, ct.DESC_CHI  FROM OP_D opd, CODE_TABLE ct WHERE opd.FUNC_TYPE  = ct.CODE AND  opd.OPT_ID IN (SELECT ID FROM OP_T WHERE  FEE_YM  LIKE CONCAT(?1,'%')) AND ct.CAT ='FUNC_TYPE' GROUP BY opd.FUNC_TYPE, ct.DESC_CHI) temp  "
  		+ "", nativeQuery = true)
  public List<Map<String,Object>> getOPPieDotData(String date);
  
  /**
   * 門急診圓餅圖人數-點數 total
   * @param date
   * @return
   */
  @Query(value = "SELECT sum(SUM) FROM "
  		+ "(SELECT (SUM(opd.T_APPL_DOT) + SUM(opd.PART_DOT))  AS SUM, opd.FUNC_TYPE, ct.DESC_CHI  FROM OP_D opd, CODE_TABLE ct WHERE opd.FUNC_TYPE  = ct.CODE AND  opd.OPT_ID IN (SELECT ID FROM OP_T WHERE  FEE_YM  LIKE CONCAT(?1,'%')) AND ct.CAT ='FUNC_TYPE' GROUP BY opd.FUNC_TYPE, ct.DESC_CHI) temp"
  		, nativeQuery = true)
  public int getOPPieDotTotal(String date);
  
  @Transactional
  @Modifying
  @Query(value = "UPDATE OP_D SET FUNC_DATE=?1, FUNC_END_DATE=?2 WHERE ID=?3", nativeQuery = true)
  public void updateFuncTypeById(String startDate, String endDate, Long id);
  
  //找出醫令使用次數超過的病歷id
  @Query(value="SELECT mr_id FROM OP_P WHERE DRUG_NO =?1 AND TOTAL_Q > ?2 AND MR_ID IN ?3 " + 
      "UNION (" + 
      "SELECT mr_id FROM IP_P WHERE ORDER_CODE =?1 AND TOTAL_Q > ?2 AND MR_ID IN ?3)", nativeQuery=true)
  public List<Long> getMrIdByOrderCodeCount(String orderCode, int quantity, List<Long>ids);
  
  //找出院內碼使用次數超過的病歷id
  @Query(value="SELECT mr_id FROM OP_P WHERE INH_CODE =?1 AND TOTAL_Q > ?2 AND MR_ID IN ?3 " + 
      "UNION (" + 
      "SELECT mr_id FROM IP_P WHERE INH_CODE =?1 AND TOTAL_Q > ?2 AND MR_ID IN ?3)", nativeQuery=true)
  public List<Long> getMrIdByInhCodeCount(String orderCode, int quantity, List<Long>ids);
}
