package tw.com.leadtek.nhiwidget.sql; 

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import tw.com.leadtek.tools.DateTool;

@Repository
public class ExportCSVDao {

	private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

	@Autowired
	protected JdbcTemplate jdbcTemplate;
	
	@Autowired
	private EntityManager entityManager;
	
	/**
	 * 取得opd 
	 * @param applYM
	 * @param fnSdate
	 * @param fnEdate
	 * @param inhCodes
	 * @return
	 */
	public List<LinkedHashMap<String, Object>> opdData(String applYM, String fnSdate, String fnEdate, String[] inhCodes, String limitPage, String offsetPage){
		
		List<String> inhCodeList = new ArrayList<String>();
		 String sql;
		 sql = "SELECT  "
				+ "MR.ID AS INH_MR, "
		 		+ "IFNULL(MR.INH_CLINIC_ID,'') AS INH_NO, "
		 		+ "MR_SO.SUBJECT_TEXT  AS SUBJECTIVE, "
		 		+ "MR_SO.OBJECT_TEXT  AS OBJECTIVE, "
		 		+ "MR.OWN_EXPENSE AS INH_OWN_EXP, "
		 		+ "IFNULL(MR.CLINIC,'') AS CLINIC, "
		 		+ "OP_D.CASE_TYPE, "
		 		+ "OP_D.SEQ_NO, "
		 		+ "MR.ROC_ID, "
		 		+ "IFNULL(OP_D.CURE_ITEM_NO1,'') AS CURE_ITEM_NO1, "
		 		+ "IFNULL(OP_D.CURE_ITEM_NO2,'') AS CURE_ITEM_NO2, "
		 		+ "IFNULL(OP_D.CURE_ITEM_NO3,'') AS CURE_ITEM_NO3, "
		 		+ "IFNULL(OP_D.CURE_ITEM_NO4,'') AS CURE_ITEM_NO4, "
		 		+ "MR.FUNC_TYPE, "
		 		+ "OP_D.FUNC_DATE, "
		 		+ "IFNULL(OP_D.FUNC_END_DATE,'') AS FUNC_END_DATE, "
		 		+ "OP_D.ID_BIRTH_YMD, "
		 		+ "IFNULL(OP_D.APPL_CAUSE_MARK,'') AS APPL_CAUSE_MARK, "
		 		+ "IFNULL(OP_D.CARE_MARK,'') AS CARE_MARK, "
		 		+ "OP_D.PAY_TYPE, "
		 		+ "OP_D.PART_NO, "
		 		+ "IFNULL(OP_D.SHARE_MARK,'') AS SHARE_MARK, "
		 		+ "OP_D.SHARE_HOSP_ID, "
		 		+ "OP_D.PAT_TRAN_OUT, "
		 		+ "IFNULL(OP_D.ICD_CM_1,'') AS ICD_CM_1, "
		 		+ "IFNULL(OP_D.ICD_CM_2,'') AS ICD_CM_2, "
		 		+ "IFNULL(OP_D.ICD_CM_3,'') AS ICD_CM_3, "
		 		+ "IFNULL(OP_D.ICD_CM_4,'') AS ICD_CM_4, "
		 		+ "IFNULL(OP_D.ICD_CM_5,'') AS ICD_CM_5, "
		 		+ "IFNULL(OP_D.ICD_OP_CODE1,'') AS ICD_OP_CODE1, "
		 		+ "IFNULL(OP_D.ICD_OP_CODE2,'') AS ICD_OP_CODE2, "
		 		+ "IFNULL(OP_D.ICD_OP_CODE3,'') AS ICD_OP_CODE3, "
		 		+ "OP_D.DRUG_DAY, "
		 		+ "OP_D.MED_TYPE, "
		 		+ "OP_D.CARD_SEQ_NO, "
		 		+ "OP_D.PRSN_ID, "
		 		+ "IFNULL(OP_D.PHAR_ID,'') AS PHAR_ID, "
		 		+ "OP_D.DRUG_DOT, "
		 		+ "OP_D.TREAT_DOT, "
		 		+ "OP_D.METR_DOT, "
		 		+ "IFNULL(OP_D.TREAT_CODE,'') AS TREAT_CODE, "
		 		+ "OP_D.DIAG_DOT, "
		 		+ "OP_D.DSVC_NO, "
		 		+ "OP_D.DSVC_DOT, "
		 		+ "OP_D.T_DOT, "
		 		+ "OP_D.PART_DOT, "
		 		+ "OP_D.T_APPL_DOT, "
		 		+ "IFNULL(OP_D.CASE_PAY_CODE,'') AS CASE_PAY_CODE, "
		 		+ "OP_D.ASSIST_PART_DOT, "
		 		+ "OP_D.CHR_DAYS, "
		 		+ "IFNULL(OP_D.NB_BIRTHDAY,'') AS NB_BIRTHDAY, "
		 		+ "OP_D.OUT_SVC_PLAN_CODE, "
		 		+ "MR.NAME, "
		 		+ "OP_D.AGENCY_ID, "
		 		+ "IFNULL(OP_D.CHILD_MARK,'') AS CHILD_MARK, "
		 		+ "IFNULL(OP_D.SPE_AREA_SVC,'') AS SPE_AREA_SVC, "
		 		+ "IFNULL(OP_D.SUPPORT_AREA,'') AS SUPPORT_AREA, "
		 		+ "IFNULL(OP_D.HOSP_ID,'') AS HOSP_ID, "
		 		+ "IFNULL(OP_D.TRAN_IN_HOSP_ID,'') AS TRAN_IN_HOSP_ID, "
		 		+ "IFNULL(OP_D.ORI_CARD_SEQ_NO,'') AS ORI_CARD_SEQ_NO, "
		 		+ "MR.ID AS MRID "
		 		+ "FROM MR, OP_D, MR_SO WHERE MR.ID = OP_D.MR_ID  AND MR.ID = MR_SO.MR_ID "
		 		;
		 if(!applYM.isEmpty()) {
			 sql += " AND MR.APPL_YM = '"+applYM+"' ";
		 }
		 if(!fnSdate.isEmpty()) {
			 sql += " AND OP_D.FUNC_END_DATE BETWEEN '"+fnSdate+"' AND '"+fnEdate+"' ";
		 }
		 if(inhCodes.length > 0) {
			 String inhCodeSql = "";
			 for (String str : inhCodes) {
				 
				 inhCodeList.add(str);
			     inhCodeSql += "'" + str + "',";
			 }
			 
			 inhCodeSql = inhCodeSql.substring(0,inhCodeSql.length() - 1);
			 sql += " AND MR.INH_CLINIC_ID IN ("+inhCodeSql+") ";   
		 }
		 if(limitPage != null) {
			 sql += " LIMIT " + Integer.parseInt(limitPage) + " OFFSET " + Integer.parseInt(offsetPage);
		 }
	     Query sqlQuery = entityManager.createNativeQuery(sql.toString());
		 sqlQuery.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		 @SuppressWarnings("unchecked")
		 List<Map<String, Object>> result = sqlQuery.getResultList();
		 entityManager.close(); 
		return opdMapto(result);
	}
	/**
	 * 取得opp
	 * @param applYM
	 * @param fnSdate
	 * @param fnEdate
	 * @param inhCodes
	 * @return
	 */
	public List<LinkedHashMap<String, Object>> oppData(String applYM, String fnSdate, String fnEdate, String[] inhCodes, String limitPage, String offsetPage){
		
		List<String> inhCodeList = new ArrayList<String>();
		 String sql;
		 sql = "SELECT  "
		 		+ "IFNULL(MR.INH_CLINIC_ID,'') AS INH_NO, "
		 		+ "IFNULL(MR.INH_CODE,'') AS INH_CODE, "
		 		+ "IFNULL(MR.APPL_STATUS,0) AS APPL_STATUS, "
		 		+ "IFNULL(OP_P.PAY_BY ,'') AS PAY_STATUS, "
		 		+ "IFNULL(OP_P.DRUG_DAY,0) AS DRUG_DAY, "
		 		+ "IFNULL(OP_P.MED_TYPE,'') AS MED_TYPE, "
		 		+ "OP_P.ORDER_TYPE, "
		 		+ "OP_P.DRUG_NO , "
		 		+ "IFNULL(OP_P.DRUG_USE,0) AS DRUG_USE, "
		 		+ "IFNULL(OP_P.DRUG_PATH ,'') AS CURE_PATH, "
		 		+ "IFNULL(OP_P.DRUG_FRE,'') AS DRUG_FRE, "
		 		+ "IFNULL(OP_P.PAY_RATE ,'') AS PAY_RATE, "
		 		+ "IFNULL(OP_P.DRUG_PATH,'') AS DRUG_PATH, "
		 		+ "IFNULL(OP_P.TOTAL_Q ,0) AS TOTAL_Q, "
		 		+ "IFNULL(OP_P.UNIT_P ,0) AS UNIT_P, "
		 		+ "IFNULL(OP_P.TOTAL_DOT ,0) AS TOTAL_DOT, "
		 		+ "OP_P.ORDER_SEQ_NO, "
		 		+ "IFNULL(OP_P.START_TIME,'') AS START_TIME, "
		 		+ "IFNULL(OP_P.END_TIME,'') AS END_TIME, "
		 		+ "IFNULL(OP_P.PRSN_ID,'') AS PRSN_ID, "
		 		+ "IFNULL(OP_P.CHR_MARK,'') AS CHR_MARK, "
		 		+ "IFNULL(OP_P.IMG_SOURCE,'') AS IMG_SOURCE, "
		 		+ "IFNULL(OP_P.PRE_NO,'') AS PRE_NO, "
		 		+ "IFNULL(OP_P.FUNC_TYPE,'') AS FUNC_TYPE, "
		 		+ "IFNULL(OP_P.OWN_EXP_MTR_NO,'') AS OWN_EXP_MTR_NO, "
		 		+ "IFNULL(OP_P.NON_LIST_MARK,'') AS NON_LIST_MARK, "
		 		+ "IFNULL(OP_P.NON_LIST_NAME,'') AS NON_LIST_NAME, "
		 		+ "IFNULL(OP_P.COMM_HOSP_ID,'') AS COMM_HOSP_ID, "
		 		+ "IFNULL(OP_P.DRUG_SERIAL_NO,'') AS DRUG_SERIAL_NO, "
		 		+ "MR.ID AS MRID "
		 		;
		 if(!applYM.isEmpty()) {
			 sql += " FROM MR, OP_P WHERE MR.ID = OP_P.MR_ID "
			 		+ " AND MR.APPL_YM = '"+applYM+"'";
		 }
		 if(!fnSdate.isEmpty()) {
			 sql += " FROM MR, OP_P, OP_D WHERE MR.ID = OP_P.MR_ID AND OP_P.OPD_ID = OP_D.ID "
			 		+ "AND OP_D.FUNC_END_DATE BETWEEN '"+fnSdate+"' AND '"+fnEdate+"' ";
		 }
		 if(inhCodes.length > 0) {
			 String inhCodeSql = "";
			 for (String str : inhCodes) {
				 
				 inhCodeList.add(str);
			     inhCodeSql += "'" + str + "',";
			 }
			 
			 inhCodeSql = inhCodeSql.substring(0,inhCodeSql.length() - 1);
			 sql += " FROM MR, OP_P WHERE MR.ID = OP_P.MR_ID "
			 		+ "AND MR.INH_CLINIC_ID IN ("+inhCodeSql+") ";   
		 }
		 if(limitPage != null) {
			 sql += " LIMIT " + Integer.parseInt(limitPage) + " OFFSET " + Integer.parseInt(offsetPage);
		 }
		 Query sqlQuery = entityManager.createNativeQuery(sql.toString());
		 sqlQuery.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		 @SuppressWarnings("unchecked")
		 List<Map<String, Object>> result = sqlQuery.getResultList();
		 entityManager.close(); 
		return oppMapto(result);
	}
	/**
	 * 取得opso
	 * @param applYM
	 * @param fnSdate
	 * @param fnEdate
	 * @param inhCodes
	 * @return
	 */
	public List<LinkedHashMap<String, Object>> opsoData(String applYM, String fnSdate, String fnEdate, String[] inhCodes, String limitPage, String offsetPage){
		
		List<String> inhCodeList = new ArrayList<String>();
		 String sql;
		 sql = "SELECT  "
		 		+ "IFNULL(MR.INH_CLINIC_ID,'') AS INH_NO, "
		 		+ "MR_SO.SUBJECT_TEXT, "
		 		+ "MR_SO.OBJECT_TEXT, "
		 		+ "MR.ID AS MR_ID, "
		 		+ "MR.ID AS MRID "
		 		;
		 if(!applYM.isEmpty()) {
			 sql += " FROM MR, MR_SO WHERE MR.ID = MR_SO.MR_ID AND MR.DATA_FORMAT = '10' "
			 		+ " AND MR.APPL_YM = '"+applYM+"'";
		 }
		 if(!fnSdate.isEmpty()) {
			 sql += " FROM MR, MR_SO, OP_D WHERE MR.ID = MR_SO.MR_ID AND MR.ID = OP_D.MR_ID AND MR.DATA_FORMAT = '10'  "
			 		+ "AND OP_D.FUNC_END_DATE BETWEEN '"+fnSdate+"' AND '"+fnEdate+"' ";
		 }
		 if(inhCodes.length > 0) {
			 String inhCodeSql = "";
			 for (String str : inhCodes) {
				 
				 inhCodeList.add(str);
			     inhCodeSql += "'" + str + "',";
			 }
			 
			 inhCodeSql = inhCodeSql.substring(0,inhCodeSql.length() - 1);
			 sql += " FROM MR, MR_SO WHERE MR.ID = MR_SO.MR_ID AND MR.DATA_FORMAT = '10' "
			 		+ "AND MR.INH_CLINIC_ID IN ("+inhCodeSql+") ";   
		 }
		 if(limitPage != null) {
			 sql += " LIMIT " + Integer.parseInt(limitPage) + " OFFSET " + Integer.parseInt(offsetPage);
		 }
		 Query sqlQuery = entityManager.createNativeQuery(sql.toString());
		 sqlQuery.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		 @SuppressWarnings("unchecked")
		 List<Map<String, Object>> result = sqlQuery.getResultList();
		 entityManager.close(); 
		return opdsoMapto(result);
	}
	/**
	 * 取得ipd
	 * @param applYM
	 * @param fnSdate
	 * @param fnEdate
	 * @param outSdate
	 * @param outEdate
	 * @param inhCodes
	 * @return
	 */
	public List<LinkedHashMap<String, Object>> ipdData(String applYM, String fnSdate, String fnEdate, String outSdate, String outEdate, String[] inhCodes, String limitPage, String offsetPage){
		List<String> inhCodeList = new ArrayList<String>();
		String sql;
		sql = "SELECT    "
				+ "MR.ID AS INH_MR, "
				+ "IFNULL(MR.INH_CLINIC_ID,'') AS INH_NO, "
				+ "MR_SO.SUBJECT_TEXT  AS SUBJECTIVE, "
				+ "MR_SO.OBJECT_TEXT  AS OBJECTIVE, "
				+ "MR.OWN_EXPENSE AS INH_OWN_EXP, "
				+ "IFNULL(MR.CLINIC,'') AS CLINIC, "
				+ "IP_D.CASE_TYPE, "
				+ "IP_D.SEQ_NO, "
				+ "MR.ROC_ID, "
				+ "IP_D.PART_NO, "
				+ "IP_D.APPL_CAUSE_MARK, "
				+ "IP_D.ID_BIRTH_YMD, "
				+ "IP_D.PAY_TYPE, "
				+ "IP_D.FUNC_TYPE, "
				+ "IP_D.IN_DATE, "
				+ "IFNULL(IP_D.OUT_DATE,'') AS OUT_DATE, "
				+ "IP_D.APPL_START_DATE, "
				+ "IP_D.APPL_END_DATE, "
				+ "IP_D.E_BED_DAY, "
				+ "IP_D.S_BED_DAY, "
				+ "IP_D.PATIENT_SOURCE, "
				+ "IP_D.CARD_SEQ_NO, "
				+ "IFNULL(IP_D.TW_DRG_CODE,'') AS TW_DRG_CODE, "
				+ "IFNULL(IP_D.TW_DRG_PAY_TYPE,'') AS TW_DRG_PAY_TYPE, "
				+ "MR.PRSN_ID, "
				+ "IFNULL(IP_D.CASE_DRG_CODE,'') AS CASE_DRG_CODE, "
				+ "IP_D.TRAN_CODE, "
				+ "IFNULL(IP_D.ICD_CM_1, '') AS ICD_CM_1, "
				+ "IFNULL(IP_D.ICD_CM_2, '') AS ICD_CM_2, "
				+ "IFNULL(IP_D.ICD_CM_3, '') AS ICD_CM_3, "
				+ "IFNULL(IP_D.ICD_CM_4, '') AS ICD_CM_4, "
				+ "IFNULL(IP_D.ICD_CM_5, '') AS ICD_CM_5, "
				+ "IFNULL(IP_D.ICD_CM_6, '') AS ICD_CM_6, "
				+ "IFNULL(IP_D.ICD_CM_7, '') AS ICD_CM_7, "
				+ "IFNULL(IP_D.ICD_CM_8, '') AS ICD_CM_8, "
				+ "IFNULL(IP_D.ICD_CM_9, '') AS ICD_CM_9, "
				+ "IFNULL(IP_D.ICD_CM_10, '') AS ICD_CM_10, "
				+ "IFNULL(IP_D.ICD_CM_11, '') AS ICD_CM_11, "
				+ "IFNULL(IP_D.ICD_CM_12, '') AS ICD_CM_12, "
				+ "IFNULL(IP_D.ICD_CM_13, '') AS ICD_CM_13, "
				+ "IFNULL(IP_D.ICD_CM_14, '') AS ICD_CM_14, "
				+ "IFNULL(IP_D.ICD_CM_15, '') AS ICD_CM_15, "
				+ "IFNULL(IP_D.ICD_CM_16, '') AS ICD_CM_16, "
				+ "IFNULL(IP_D.ICD_CM_17, '') AS ICD_CM_17, "
				+ "IFNULL(IP_D.ICD_CM_18, '') AS ICD_CM_18, "
				+ "IFNULL(IP_D.ICD_CM_19, '') AS ICD_CM_19, "
				+ "IFNULL(IP_D.ICD_CM_20, '') AS ICD_CM_20, "
				+ "IFNULL(IP_D.ICD_OP_CODE1, '') AS ICD_OP_CODE1, "
				+ "IFNULL(IP_D.ICD_OP_CODE2, '') AS ICD_OP_CODE2, "
				+ "IFNULL(IP_D.ICD_OP_CODE3, '') AS ICD_OP_CODE3, "
				+ "IFNULL(IP_D.ICD_OP_CODE4, '') AS ICD_OP_CODE4, "
				+ "IFNULL(IP_D.ICD_OP_CODE5, '') AS ICD_OP_CODE5, "
				+ "IFNULL(IP_D.ICD_OP_CODE6, '') AS ICD_OP_CODE6, "
				+ "IFNULL(IP_D.ICD_OP_CODE7, '') AS ICD_OP_CODE7, "
				+ "IFNULL(IP_D.ICD_OP_CODE8, '') AS ICD_OP_CODE8, "
				+ "IFNULL(IP_D.ICD_OP_CODE9, '') AS ICD_OP_CODE9, "
				+ "IFNULL(IP_D.ICD_OP_CODE10, '') AS ICD_OP_CODE10, "
				+ "IFNULL(IP_D.ICD_OP_CODE11, '') AS ICD_OP_CODE11, "
				+ "IFNULL(IP_D.ICD_OP_CODE12, '') AS ICD_OP_CODE12, "
				+ "IFNULL(IP_D.ICD_OP_CODE13, '') AS ICD_OP_CODE13, "
				+ "IFNULL(IP_D.ICD_OP_CODE14, '') AS ICD_OP_CODE14, "
				+ "IFNULL(IP_D.ICD_OP_CODE15, '') AS ICD_OP_CODE15, "
				+ "IFNULL(IP_D.ICD_OP_CODE16, '') AS ICD_OP_CODE16, "
				+ "IFNULL(IP_D.ICD_OP_CODE17, '') AS ICD_OP_CODE17, "
				+ "IFNULL(IP_D.ICD_OP_CODE18, '') AS ICD_OP_CODE18, "
				+ "IFNULL(IP_D.ICD_OP_CODE19, '') AS ICD_OP_CODE19, "
				+ "IFNULL(IP_D.ICD_OP_CODE20, '') AS ICD_OP_CODE20, "
				+ "IP_D.ORDER_QTY, "
				+ "IP_D.DIAG_DOT, "
				+ "IP_D.ROOM_DOT, "
				+ "IP_D.MEAL_DOT, "
				+ "IP_D.AMIN_DOT, "
				+ "IP_D.RADO_DOT, "
				+ "IP_D.THRP_DOT, "
				+ "IP_D.SGRY_DOT, "
				+ "IP_D.PHSC_DOT, "
				+ "IP_D.BLOD_DOT, "
				+ "IP_D.HD_DOT, "
				+ "IP_D.ANE_DOT, "
				+ "IP_D.METR_DOT, "
				+ "IP_D.DRUG_DOT, "
				+ "IP_D.DSVC_DOT, "
				+ "IP_D.NRTP_DOT, "
				+ "IP_D.INJT_DOT, "
				+ "IP_D.BABY_DOT, "
				+ "IP_D.MED_DOT, "
				+ "IP_D.PART_DOT, "
				+ "IFNULL(IP_D.APPL_DOT,0) AS APPL_DOT, "
				+ "IP_D.EB_APPL30_DOT, "
				+ "IP_D.EB_PART30_DOT, "
				+ "IP_D.EB_APPL60_DOT, "
				+ "IP_D.EB_PART60_DOT, "
				+ "IP_D.EB_APPL61_DOT, "
				+ "IP_D.EB_PART61_DOT, "
				+ "IP_D.SB_APPL30_DOT, "
				+ "IP_D.SB_PART30_DOT, "
				+ "IP_D.SB_APPL90_DOT, "
				+ "IP_D.SB_PART90_DOT, "
				+ "IP_D.SB_APPL180_DOT, "
				+ "IP_D.SB_PART180_DOT, "
				+ "IP_D.SB_APPL181_DOT, "
				+ "IP_D.SB_PART181_DOT, "
				+ "IP_D.NB_BIRTHDAY, "
				+ "IFNULL(IP_D.CHILD_MARK,'') AS CHILD_MARK, "
				+ "IP_D.TW_DRGS_SUIT_MARK, "
				+ "MR.NAME, "
				+ "IFNULL(IP_D.AGENCY_ID,'') AS AGENCY_ID, "
				+ "IFNULL(IP_D.TRAN_IN_HOSP_ID,'') AS TRAN_IN_HOSP_ID, "
				+ "IFNULL(IP_D.TRAN_OUT_HOSP_ID,'') AS TRAN_OUT_HOSP_ID, "
				+ "IFNULL(IP_D.HOSP_ID,'') AS HOSP_ID, "
				+ "IFNULL(IP_D.SVC_PLAN,'') AS SVC_PLAN, "
				+ "IFNULL(IP_D.PILOT_PROJECT,'') AS PILOT_PROJECT, "
				+ "IP_D.NON_APPL_DOT, "
				+ "MR.ID AS MRID "
				+ "FROM MR, IP_D, MR_SO WHERE MR.ID = IP_D.MR_ID AND MR.ID = MR_SO.MR_ID "
		 		;
		 if(!applYM.isEmpty()) {
			 sql += " AND MR.APPL_YM = '"+applYM+"'";
		 }
		 if(!fnSdate.isEmpty()) {
			 String fsdate = DateTool.convertChineseToADWithSlash(fnSdate).replaceAll("/", "-");
			 String fedate = DateTool.convertChineseToADWithSlash(fnEdate).replaceAll("/", "-");
			 sql += " AND MR.MR_END_DATE BETWEEN '"+fsdate+"' AND '"+fedate+"' ";
		 }
		 if(!outSdate.isEmpty()) {
			 sql += " AND IP_D.OUT_DATE BETWEEN '"+outSdate+"' AND '"+outEdate+"' ";
		 }
		 if(inhCodes.length > 0) {
			 String inhCodeSql = "";
			 for (String str : inhCodes) {
				 
				 inhCodeList.add(str);
			     inhCodeSql += "'" + str + "',";
			 }
			 
			 inhCodeSql = inhCodeSql.substring(0,inhCodeSql.length() - 1);
			 sql += "AND MR.INH_CLINIC_ID IN ("+inhCodeSql+") ";   
		 }
		 if(limitPage != null) {
			 sql += " LIMIT " + Integer.parseInt(limitPage) + " OFFSET " + Integer.parseInt(offsetPage);
		 }
		 Query sqlQuery = entityManager.createNativeQuery(sql.toString());
		 sqlQuery.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		 @SuppressWarnings("unchecked")
		 List<Map<String, Object>> result = sqlQuery.getResultList();
		 entityManager.close(); 
		 
		return ipdMapto(result);
	}
	/**
	 * 取得ipp
	 * @param applYM
	 * @param fnSdate
	 * @param fnEdate
	 * @param outSdate
	 * @param outEdate
	 * @param inhCodes
	 * @return
	 */
	public List<LinkedHashMap<String, Object>> ippData(String applYM, String fnSdate, String fnEdate, String outSdate, String outEdate, String[] inhCodes, String limitPage, String offsetPage){
		List<String> inhCodeList = new ArrayList<String>();
		String sql;
		sql = "SELECT  "
				+ "IFNULL(MR.INH_CLINIC_ID,'') AS INH_NO, "
				+ "IFNULL(MR.INH_CODE,'') AS INH_CODE, "
				+ "IFNULL(MR.APPL_STATUS,0) AS APPL_STATUS, "
				+ "IFNULL(IP_P.PAY_BY ,'') AS PAY_STATUS, "
				+ "IP_P.ORDER_SEQ_NO, "
				+ "IP_P.ORDER_TYPE, "
				+ "IP_P.ORDER_CODE, "
				+ "IP_P.PAY_RATE, "
				+ "IFNULL(IP_P.DRUG_USE,0) AS DRUG_USE, "
				+ "IFNULL(IP_P.DRUG_FRE,'') AS DRUG_FRE, "
				+ "IFNULL(IP_P.DRUG_PATH,'') AS DRUG_PATH, "
				+ "IFNULL(IP_P.CON_FUNC_TYPE,'') AS CON_FUNC_TYPE, "
				+ "IFNULL(IP_P.BED_NO,'') AS BED_NO, "
				+ "IFNULL(IP_P.CURE_PATH,'') AS CURE_PATH, "
				+ "IFNULL(IP_P.TW_DRGS_CALCU,0) AS TW_DRGS_CALCU, "
				+ "IFNULL(IP_P.PART_ACCO_DATA,'') AS PART_ACCO_DATA, "
				+ "IFNULL(IP_P.DONATER,'') AS DONATER, "
				+ "IP_P.START_TIME, "
				+ "IP_P.END_TIME, "
				+ "IP_P.TOTAL_Q, "
				+ "IP_P.UNIT_P, "
				+ "IP_P.TOTAL_DOT, "
				+ "IFNULL(IP_P.PRE_NO,'') AS PRE_NO, "
				+ "IFNULL(IP_P.PRSN_ID,'') AS PRSN_ID, "
				+ "IFNULL(IP_P.IMG_SOURCE,'') AS IMG_SOURCE, "
				+ "IP_P.FUNC_TYPE, "
				+ "IFNULL(IP_P.OWN_EXP_MTR_NO,'') AS OWN_EXP_MTR_NO, "
				+ "IFNULL(IP_P.NON_LIST_MARK,'') AS NON_LIST_MARK, "
				+ "IFNULL(IP_P.NON_LIST_NAME,'') AS NON_LIST_NAME, "
				+ "IFNULL(IP_P.COMM_HOSP_ID,'') AS COMM_HOSP_ID, "
				+ "IFNULL(IP_P.DRUG_SERIAL_NO,'') AS DRUG_SERIAL_NO, "
				+ "MR.ID AS MRID "
		 		;
		 if(!applYM.isEmpty()) {
			 sql += " FROM MR, IP_P WHERE MR.ID = IP_P.MR_ID "
			 		+ "AND MR.APPL_YM = '"+applYM+"'";
		 }
		 if(!fnSdate.isEmpty()) {
			 String fsdate = DateTool.convertChineseToADWithSlash(fnSdate).replaceAll("/", "-");
			 String fedate = DateTool.convertChineseToADWithSlash(fnEdate).replaceAll("/", "-");
			 sql += " FROM MR, IP_P, IP_D WHERE MR.ID = IP_P.MR_ID AND IP_D.ID = IP_P.IPD_ID "
			 		+ "AND MR.MR_END_DATE BETWEEN '"+fsdate+"' AND '"+fedate+"' ";
		 }
		 if(!outSdate.isEmpty()) {
			 sql += " FROM MR, IP_P, IP_D WHERE MR.ID = IP_P.MR_ID AND IP_D.ID = IP_P.IPD_ID "
			 		+ "AND IP_D.OUT_DATE BETWEEN '"+outSdate+"' AND '"+outEdate+"' ";
		 }
		 if(inhCodes.length > 0) {
			 String inhCodeSql = "";
			 for (String str : inhCodes) {
				 
				 inhCodeList.add(str);
			     inhCodeSql += "'" + str + "',";
			 }
			 
			 inhCodeSql = inhCodeSql.substring(0,inhCodeSql.length() - 1);
			 sql += " "
			 		+ "AND MR.INH_CLINIC_ID IN ("+inhCodeSql+") ";   
		 }
		 if(limitPage != null) {
			 sql += " LIMIT " + Integer.parseInt(limitPage) + " OFFSET " + Integer.parseInt(offsetPage);
		 }
		 Query sqlQuery = entityManager.createNativeQuery(sql.toString());
		 sqlQuery.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		 @SuppressWarnings("unchecked")
		 List<Map<String, Object>> result = sqlQuery.getResultList();
		 entityManager.close(); 
		 
		return ippMapto(result);
	}
	
	/**
	 * 取得ipso
	 * @param applYM
	 * @param fnSdate
	 * @param fnEdate
	 * @param outSdate
	 * @param outEdate
	 * @param inhCodes
	 * @return
	 */
	public List<LinkedHashMap<String, Object>> ipsoData(String applYM, String fnSdate, String fnEdate, String outSdate, String outEdate, String[] inhCodes, String limitPage, String offsetPage){
		
		List<String> inhCodeList = new ArrayList<String>();
		 String sql;
		 sql = "SELECT  "
		 		+ "IFNULL(MR.INH_CLINIC_ID,'') AS INH_NO, "
		 		+ "MR_SO.SUBJECT_TEXT, "
		 		+ "MR_SO.OBJECT_TEXT, "
		 		+ "MR_SO.DISCHARGE_TEXT, "
		 		+ "MR.ID AS MR_ID, "
		 		+ "MR.ID AS MRID "
		 		;
		 if(!applYM.isEmpty()) {
			 sql += " FROM MR, MR_SO WHERE MR.ID = MR_SO.MR_ID AND MR.DATA_FORMAT = '20' "
			 		+ " AND MR.APPL_YM = '"+applYM+"'";
		 }
		 if(!fnSdate.isEmpty()) {
			 String fsdate = DateTool.convertChineseToADWithSlash(fnSdate).replaceAll("/", "-");
			 String fedate = DateTool.convertChineseToADWithSlash(fnEdate).replaceAll("/", "-");
			 sql += " FROM MR, MR_SO, IP_D WHERE MR.ID = MR_SO.MR_ID AND MR.ID = IP_D.MR_ID AND MR.DATA_FORMAT = '20'  "
			 		+ "AND MR.MR_END_DATE BETWEEN '"+fsdate+"' AND '"+fedate+"' ";
		 }
		 if(!outSdate.isEmpty()) {
			 sql += " FROM MR, MR_SO, IP_D WHERE MR.ID = MR_SO.MR_ID AND MR.ID = IP_D.MR_ID AND MR.DATA_FORMAT = '20'  "
				 		+ "AND IP_D.OUT_DATE BETWEEN '"+outSdate+"' AND '"+outEdate+"' ";
		 }
		 if(inhCodes.length > 0) {
			 String inhCodeSql = "";
			 for (String str : inhCodes) {
				 
				 inhCodeList.add(str);
			     inhCodeSql += "'" + str + "',";
			 }
			 
			 inhCodeSql = inhCodeSql.substring(0,inhCodeSql.length() - 1);
			 sql += " FROM MR, MR_SO WHERE MR.ID = MR_SO.MR_ID AND MR.DATA_FORMAT = '20' "
			 		+ "AND MR.INH_CLINIC_ID IN ("+inhCodeSql+") ";   
		 }
		 if(limitPage != null) {
			 sql += " LIMIT " + Integer.parseInt(limitPage) + " OFFSET " + Integer.parseInt(offsetPage);
		 }
		 Query sqlQuery = entityManager.createNativeQuery(sql.toString());
		 sqlQuery.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		 @SuppressWarnings("unchecked")
		 List<Map<String, Object>> result = sqlQuery.getResultList();
		 entityManager.close(); 
		return ipdsoMapto(result);
	}
	
	public List<LinkedHashMap<String, Object>> deductedNoteData(String applYM, String fnSdate, String fnEdate, String outSdate, String outEdate,String[] dataFormats, String[] inhCodes, String limitPage, String offsetPage){
		
		List<String> inhCodeList = new ArrayList<String>();
		List<String> dataFormatList = new ArrayList<String>();
		String sql;
		sql = "SELECT  "
				+ "IFNULL(MR.INH_CLINIC_ID,'') AS INH_NO, "
				+ "DEDUCTED_NOTE.CAT, "
				+ "DEDUCTED_NOTE.ITEM, "
				+ "DEDUCTED_NOTE.CODE, "
				+ "DEDUCTED_NOTE.DEDUCTED_ORDER, "
				+ "DEDUCTED_NOTE.DEDUCTED_QUANTITY, "
				+ "DEDUCTED_NOTE.DEDUCTED_AMOUNT, "
				+ "IFNULL(DEDUCTED_NOTE.REASON,'') AS REASON, "
				+ "IFNULL(DEDUCTED_NOTE.NOTE,'') AS NOTE, "
				+ "IFNULL(DEDUCTED_NOTE.ROLLBACK_M,0) AS ROLLBACK_M, "
				+ "IFNULL(DEDUCTED_NOTE.ROLLBACK_Q,0) AS ROLLBACK_Q, "
				+ "IFNULL(DEDUCTED_NOTE.AFR_QUANTITY,0) AS AFR_QUANTITY, "
				+ "IFNULL(DEDUCTED_NOTE.AFR_AMOUNT,0) AS AFR_AMOUNT, "
				+ "IFNULL(DEDUCTED_NOTE.AFR_PAY_QUANTITY,0) AS AFR_PAY_QUANTITY, "
				+ "IFNULL(DEDUCTED_NOTE.AFR_PAY_AMOUNT,0) AS AFR_PAY_AMOUNT, "
				+ "IFNULL(DEDUCTED_NOTE.AFR_NO_PAY_CODE,'') AS AFR_NO_PAY_CODE, "
				+ "IFNULL(DEDUCTED_NOTE.AFR_NO_PAY_DESC,'') AS AFR_NO_PAY_DESC, "
				+ "IFNULL(DEDUCTED_NOTE.AFR_NOTE,'') AS AFR_NOTE, "
				+ "IFNULL(DEDUCTED_NOTE.DISPUTE_QUANTITY,0) AS DISPUTE_QUANTITY, "
				+ "IFNULL(DEDUCTED_NOTE.DISPUTE_AMOUNT,0) AS DISPUTE_AMOUNT, "
				+ "IFNULL(DEDUCTED_NOTE.DISPUTE_PAY_QUANTITY,0) AS DISPUTE_PAY_QUANTITY, "
				+ "IFNULL(DEDUCTED_NOTE.DISPUTE_PAY_AMOUNT,0) AS DISPUTE_PAY_AMOUNT, "
				+ "IFNULL(DEDUCTED_NOTE.DISPUTE_NO_PAY_CODE,'') AS DISPUTE_NO_PAY_CODE, "
				+ "IFNULL(DEDUCTED_NOTE.DISPUTE_NO_PAY_DESC,'') AS DISPUTE_NO_PAY_DESC, "
				+ "IFNULL(DEDUCTED_NOTE.DISPUTE_NOTE,'') AS DISPUTE_NOTE, "
				+ "MR.ID AS MRID "
				;
		
		if(dataFormats.length > 0) {
			///如果門急診/住院
			if(dataFormats.length > 1) {
				if(!applYM.isEmpty()) {
					 sql += " FROM MR, DEDUCTED_NOTE WHERE MR.ID = DEDUCTED_NOTE.MR_ID  "
					 		+ " AND MR.APPL_YM = '"+applYM+"'";
				 }
				 if(!fnSdate.isEmpty()) {
					 String fsdate = DateTool.convertChineseToADWithSlash(fnSdate).replaceAll("/", "-");
					 String fedate = DateTool.convertChineseToADWithSlash(fnEdate).replaceAll("/", "-");
					 sql += " FROM MR, DEDUCTED_NOTE WHERE MR.ID = DEDUCTED_NOTE.MR_ID  "
					 		+ "AND MR.MR_END_DATE BETWEEN '"+fsdate+"' AND '"+fedate+"' ";
				 }
				 if(!outSdate.isEmpty()) {
					 sql += " FROM MR, DEDUCTED_NOTE, IP_D WHERE MR.ID = DEDUCTED_NOTE.MR_ID AND MR.ID = IP_D.MR_ID "
						 		+ "AND IP_D.OUT_DATE BETWEEN '"+outSdate+"' AND '"+outEdate+"' ";
				 }
				 if(inhCodes.length > 0) {
					 String inhCodeSql = "";
					 for (String str : inhCodes) {
						 
						 inhCodeList.add(str);
					     inhCodeSql += "'" + str + "',";
					 }
					 
					 inhCodeSql = inhCodeSql.substring(0,inhCodeSql.length() - 1);
					 sql += " FROM MR, DEDUCTED_NOTE WHERE MR.ID = DEDUCTED_NOTE.MR_ID  "
					 		+ "AND MR.INH_CLINIC_ID IN ("+inhCodeSql+") ";   
				 }
			}
			else {
				String dataformat = dataFormats[0];
				///門急診
				if(dataformat.equals("op")) {
					if(!applYM.isEmpty()) {
						 sql += " FROM MR, DEDUCTED_NOTE WHERE MR.ID = DEDUCTED_NOTE.MR_ID AND MR.DATA_FORMAT = '10'  "
						 		+ " AND MR.APPL_YM = '"+applYM+"'";
					 }
					 if(!fnSdate.isEmpty()) {
						 String fsdate = DateTool.convertChineseToADWithSlash(fnSdate).replaceAll("/", "-");
						 String fedate = DateTool.convertChineseToADWithSlash(fnEdate).replaceAll("/", "-");
						 sql += " FROM MR, DEDUCTED_NOTE WHERE MR.ID = DEDUCTED_NOTE.MR_ID AND MR.DATA_FORMAT = '10' "
						 		+ "AND MR.MR_END_DATE BETWEEN '"+fsdate+"' AND '"+fedate+"' ";
					 }
					 if(inhCodes.length > 0) {
						 String inhCodeSql = "";
						 for (String str : inhCodes) {
							 
							 inhCodeList.add(str);
						     inhCodeSql += "'" + str + "',";
						 }
						 
						 inhCodeSql = inhCodeSql.substring(0,inhCodeSql.length() - 1);
						 sql += " FROM MR, DEDUCTED_NOTE WHERE MR.ID = DEDUCTED_NOTE.MR_ID AND MR.DATA_FORMAT = '10' "
						 		+ "AND MR.INH_CLINIC_ID IN ("+inhCodeSql+") ";   
					 }
				}
				else {///住院
					if(!applYM.isEmpty()) {
						 sql += " FROM MR, DEDUCTED_NOTE WHERE MR.ID = DEDUCTED_NOTE.MR_ID AND MR.DATA_FORMAT = '20' "
						 		+ " AND MR.APPL_YM = '"+applYM+"'";
					 }
					 if(!fnSdate.isEmpty()) {
						 String fsdate = DateTool.convertChineseToADWithSlash(fnSdate).replaceAll("/", "-");
						 String fedate = DateTool.convertChineseToADWithSlash(fnEdate).replaceAll("/", "-");
						 sql += " FROM MR, DEDUCTED_NOTE WHERE MR.ID = DEDUCTED_NOTE.MR_ID AND MR.DATA_FORMAT = '20' "
						 		+ "AND MR.MR_END_DATE BETWEEN '"+fsdate+"' AND '"+fedate+"' ";
					 }
					 if(!outSdate.isEmpty()) {
						 sql += " FROM MR, DEDUCTED_NOTE, IP_D WHERE MR.ID = DEDUCTED_NOTE.MR_ID AND MR.ID = IP_D.MR_ID "
							 		+ "AND IP_D.OUT_DATE BETWEEN '"+outSdate+"' AND '"+outEdate+"' ";
					 }
					 if(inhCodes.length > 0) {
						 String inhCodeSql = "";
						 for (String str : inhCodes) {
							 
							 inhCodeList.add(str);
						     inhCodeSql += "'" + str + "',";
						 }
						 
						 inhCodeSql = inhCodeSql.substring(0,inhCodeSql.length() - 1);
						 sql += " FROM MR, DEDUCTED_NOTE WHERE MR.ID = DEDUCTED_NOTE.MR_ID AND MR.DATA_FORMAT = '20' "
						 		+ "AND MR.INH_CLINIC_ID IN ("+inhCodeSql+") ";   
					 }
				}
			}
			
		}
		if(limitPage != null) {
			 sql += " LIMIT " + Integer.parseInt(limitPage) + " OFFSET " + Integer.parseInt(offsetPage);
		 }
		 Query sqlQuery = entityManager.createNativeQuery(sql.toString());
		 sqlQuery.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		 @SuppressWarnings("unchecked")
		 List<Map<String, Object>> result = sqlQuery.getResultList();
		 entityManager.close(); 
		
		
		return deductedMapto(result);
	}
	
	
	/**
	 * 取得opd 
	 * @param applYM
	 * @param fnSdate
	 * @param fnEdate
	 * @param inhCodes
	 * @return
	 */
	public int opdCount(String applYM, String fnSdate, String fnEdate, String[] inhCodes){
		
		List<String> inhCodeList = new ArrayList<String>();
		 String sql;
		 sql = "SELECT COUNT(*) AS COUNT FROM ( "
		 		+ "SELECT  "
		 		+ "MR.ID AS INH_MR, "
		 		+ "IFNULL(MR.INH_CLINIC_ID,'') AS INH_NO, "
		 		+ "MR_SO.SUBJECT_TEXT  AS SUBJECTIVE, "
		 		+ "MR_SO.OBJECT_TEXT  AS OBJECTIVE, "
		 		+ "MR.OWN_EXPENSE AS INH_OWN_EXP, "
		 		+ "IFNULL(MR.CLINIC,'') AS CLINIC, "
		 		+ "OP_D.CASE_TYPE, "
		 		+ "OP_D.SEQ_NO, "
		 		+ "MR.ROC_ID, "
		 		+ "IFNULL(OP_D.CURE_ITEM_NO1,'') AS CURE_ITEM_NO1, "
		 		+ "IFNULL(OP_D.CURE_ITEM_NO2,'') AS CURE_ITEM_NO2, "
		 		+ "IFNULL(OP_D.CURE_ITEM_NO3,'') AS CURE_ITEM_NO3, "
		 		+ "IFNULL(OP_D.CURE_ITEM_NO4,'') AS CURE_ITEM_NO4, "
		 		+ "MR.FUNC_TYPE, "
		 		+ "OP_D.FUNC_DATE, "
		 		+ "IFNULL(OP_D.FUNC_END_DATE,'') AS FUNC_END_DATE, "
		 		+ "OP_D.ID_BIRTH_YMD, "
		 		+ "IFNULL(OP_D.APPL_CAUSE_MARK,'') AS APPL_CAUSE_MARK, "
		 		+ "IFNULL(OP_D.CARE_MARK,'') AS CARE_MARK, "
		 		+ "OP_D.PAY_TYPE, "
		 		+ "OP_D.PART_NO, "
		 		+ "IFNULL(OP_D.SHARE_MARK,'') AS SHARE_MARK, "
		 		+ "OP_D.SHARE_HOSP_ID, "
		 		+ "OP_D.PAT_TRAN_OUT, "
		 		+ "IFNULL(OP_D.ICD_CM_1,'') AS ICD_CM_1, "
		 		+ "IFNULL(OP_D.ICD_CM_2,'') AS ICD_CM_2, "
		 		+ "IFNULL(OP_D.ICD_CM_3,'') AS ICD_CM_3, "
		 		+ "IFNULL(OP_D.ICD_CM_4,'') AS ICD_CM_4, "
		 		+ "IFNULL(OP_D.ICD_CM_5,'') AS ICD_CM_5, "
		 		+ "IFNULL(OP_D.ICD_OP_CODE1,'') AS ICD_OP_CODE1, "
		 		+ "IFNULL(OP_D.ICD_OP_CODE2,'') AS ICD_OP_CODE2, "
		 		+ "IFNULL(OP_D.ICD_OP_CODE3,'') AS ICD_OP_CODE3, "
		 		+ "OP_D.DRUG_DAY, "
		 		+ "OP_D.MED_TYPE, "
		 		+ "OP_D.CARD_SEQ_NO, "
		 		+ "OP_D.PRSN_ID, "
		 		+ "IFNULL(OP_D.PHAR_ID,'') AS PHAR_ID, "
		 		+ "OP_D.DRUG_DOT, "
		 		+ "OP_D.TREAT_DOT, "
		 		+ "OP_D.METR_DOT, "
		 		+ "IFNULL(OP_D.TREAT_CODE,'') AS TREAT_CODE, "
		 		+ "OP_D.DIAG_DOT, "
		 		+ "OP_D.DSVC_NO, "
		 		+ "OP_D.DSVC_DOT, "
		 		+ "OP_D.T_DOT, "
		 		+ "OP_D.PART_DOT, "
		 		+ "OP_D.T_APPL_DOT, "
		 		+ "IFNULL(OP_D.CASE_PAY_CODE,'') AS CASE_PAY_CODE, "
		 		+ "OP_D.ASSIST_PART_DOT, "
		 		+ "OP_D.CHR_DAYS, "
		 		+ "IFNULL(OP_D.NB_BIRTHDAY,'') AS NB_BIRTHDAY, "
		 		+ "OP_D.OUT_SVC_PLAN_CODE, "
		 		+ "MR.NAME, "
		 		+ "OP_D.AGENCY_ID, "
		 		+ "IFNULL(OP_D.CHILD_MARK,'') AS CHILD_MARK, "
		 		+ "IFNULL(OP_D.SPE_AREA_SVC,'') AS SPE_AREA_SVC, "
		 		+ "IFNULL(OP_D.SUPPORT_AREA,'') AS SUPPORT_AREA, "
		 		+ "IFNULL(OP_D.HOSP_ID,'') AS HOSP_ID, "
		 		+ "IFNULL(OP_D.TRAN_IN_HOSP_ID,'') AS TRAN_IN_HOSP_ID, "
		 		+ "IFNULL(OP_D.ORI_CARD_SEQ_NO,'') AS ORI_CARD_SEQ_NO, "
		 		+ "IFNULL(OP_D.PRSN_NAME,'') AS PRSN_NAME, "
		 		+ "IFNULL(OP_D.PHAR_NAME,'') AS PHAR_NAME "
		 		+ "FROM MR, OP_D, MR_SO WHERE MR.ID = OP_D.MR_ID AND MR.ID = MR_SO.MR_ID "
		 		;
		 if(!applYM.isEmpty()) {
			 sql += " AND MR.APPL_YM = '"+applYM+"' ";
		 }
		 if(!fnSdate.isEmpty()) {
			 sql += " AND OP_D.FUNC_END_DATE BETWEEN '"+fnSdate+"' AND '"+fnEdate+"' ";
		 }
		 if(inhCodes.length > 0) {
			 String inhCodeSql = "";
			 for (String str : inhCodes) {
				 
				 inhCodeList.add(str);
			     inhCodeSql += "'" + str + "',";
			 }
			 
			 inhCodeSql = inhCodeSql.substring(0,inhCodeSql.length() - 1);
			 sql += " AND MR.INH_CLINIC_ID IN ("+inhCodeSql+") ";   
		 }
		 sql += ") t";
	     Query sqlQuery = entityManager.createNativeQuery(sql.toString());
		 sqlQuery.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		 @SuppressWarnings("unchecked")
		 List<Map<String, Object>> result = sqlQuery.getResultList();
		 entityManager.close(); 
		 return Integer.parseInt(result.get(0).get("COUNT").toString());
	}
	/**
	 * 取得opp
	 * @param applYM
	 * @param fnSdate
	 * @param fnEdate
	 * @param inhCodes
	 * @return
	 */
	public int oppCount(String applYM, String fnSdate, String fnEdate, String[] inhCodes){
		
		List<String> inhCodeList = new ArrayList<String>();
		 String sql;
		 sql = "SELECT COUNT(*) AS COUNT FROM ( "
		 		+ "SELECT  "
		 		+ "IFNULL(MR.INH_CLINIC_ID,'') AS INH_NO, "
		 		+ "IFNULL(MR.INH_CODE,'') AS INH_CODE, "
		 		+ "IFNULL(MR.APPL_STATUS,0) AS APPL_STATUS, "
		 		+ "IFNULL(OP_P.PAY_BY ,'') AS PAY_STATUS, "
		 		+ "IFNULL(OP_P.DRUG_DAY,0) AS DRUG_DAY, "
		 		+ "IFNULL(OP_P.MED_TYPE,'') AS MED_TYPE, "
		 		+ "OP_P.ORDER_TYPE, "
		 		+ "OP_P.DRUG_NO , "
		 		+ "IFNULL(OP_P.DRUG_USE,0) AS DRUG_USE, "
		 		+ "IFNULL(OP_P.DRUG_PATH,'') AS DRUG_PATH, "
		 		+ "IFNULL(OP_P.DRUG_FRE,'') AS DRUG_FRE, "
		 		+ "IFNULL(OP_P.PAY_RATE ,'') AS PAY_RATE, "
		 		+ "IFNULL(OP_P.DRUG_PATH ,'') AS CURE_PATH, "
		 		+ "IFNULL(OP_P.TOTAL_Q ,0) AS TW_DRGS_CALCU, "
		 		+ "IFNULL(OP_P.UNIT_P ,0) AS PART_ACCO_DATA, "
		 		+ "IFNULL(OP_P.TOTAL_DOT ,0) AS DONATER, "
		 		+ "OP_P.ORDER_SEQ_NO, "
		 		+ "IFNULL(OP_P.START_TIME,'') AS START_TIME, "
		 		+ "IFNULL(OP_P.END_TIME,'') AS END_TIME, "
		 		+ "IFNULL(OP_P.PRSN_ID,'') AS PRSN_ID, "
		 		+ "IFNULL(OP_P.IMG_SOURCE,'') AS IMG_SOURCE, "
		 		+ "IFNULL(OP_P.PRE_NO,'') AS PRE_NO, "
		 		+ "IFNULL(OP_P.FUNC_TYPE,'') AS FUNC_TYPE, "
		 		+ "IFNULL(OP_P.OWN_EXP_MTR_NO,'') AS OWN_EXP_MTR_NO, "
		 		+ "IFNULL(OP_P.NON_LIST_MARK,'') AS NON_LIST_MARK, "
		 		+ "IFNULL(OP_P.NON_LIST_NAME,'') AS NON_LIST_NAME, "
		 		+ "IFNULL(OP_P.COMM_HOSP_ID,'') AS COMM_HOSP_ID, "
		 		+ "IFNULL(OP_P.DRUG_SERIAL_NO,'') AS DRUG_SERIAL_NO "
		 		;
		 if(!applYM.isEmpty()) {
			 sql += " FROM MR, OP_P WHERE MR.ID = OP_P.MR_ID "
			 		+ " AND MR.APPL_YM = '"+applYM+"'";
		 }
		 if(!fnSdate.isEmpty()) {
			 sql += " FROM MR, OP_P, OP_D WHERE MR.ID = OP_P.MR_ID AND OP_P.OPD_ID = OP_D.ID "
			 		+ "AND OP_D.FUNC_END_DATE BETWEEN '"+fnSdate+"' AND '"+fnEdate+"' ";
		 }
		 if(inhCodes.length > 0) {
			 String inhCodeSql = "";
			 for (String str : inhCodes) {
				 
				 inhCodeList.add(str);
			     inhCodeSql += "'" + str + "',";
			 }
			 
			 inhCodeSql = inhCodeSql.substring(0,inhCodeSql.length() - 1);
			 sql += " FROM MR, OP_P WHERE MR.ID = OP_P.MR_ID "
			 		+ "AND MR.INH_CLINIC_ID IN ("+inhCodeSql+") ";   
		 }
		 sql += ") t";
		 Query sqlQuery = entityManager.createNativeQuery(sql.toString());
		 sqlQuery.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		 @SuppressWarnings("unchecked")
		 List<Map<String, Object>> result = sqlQuery.getResultList();
		 entityManager.close(); 
		 return Integer.parseInt(result.get(0).get("COUNT").toString());
	}
	/**
	 * 取得opso
	 * @param applYM
	 * @param fnSdate
	 * @param fnEdate
	 * @param inhCodes
	 * @return
	 */
	public int opsoCount(String applYM, String fnSdate, String fnEdate, String[] inhCodes){
		
		List<String> inhCodeList = new ArrayList<String>();
		 String sql;
		 sql = "SELECT COUNT(*) AS COUNT FROM ( "
		 		+ "SELECT  "
		 		+ "IFNULL(MR.INH_CLINIC_ID,'') AS INH_NO, "
		 		+ "MR_SO.SUBJECT_TEXT, "
		 		+ "MR_SO.OBJECT_TEXT "
		 		;
		 if(!applYM.isEmpty()) {
			 sql += " FROM MR, MR_SO WHERE MR.ID = MR_SO.MR_ID AND MR.DATA_FORMAT = '10' "
			 		+ " AND MR.APPL_YM = '"+applYM+"'";
		 }
		 if(!fnSdate.isEmpty()) {
			 sql += " FROM MR, MR_SO, OP_D WHERE MR.ID = MR_SO.MR_ID AND MR.ID = OP_D.MR_ID AND MR.DATA_FORMAT = '10'  "
			 		+ "AND OP_D.FUNC_END_DATE BETWEEN '"+fnSdate+"' AND '"+fnEdate+"' ";
		 }
		 if(inhCodes.length > 0) {
			 String inhCodeSql = "";
			 for (String str : inhCodes) {
				 
				 inhCodeList.add(str);
			     inhCodeSql += "'" + str + "',";
			 }
			 
			 inhCodeSql = inhCodeSql.substring(0,inhCodeSql.length() - 1);
			 sql += " FROM MR, MR_SO WHERE MR.ID = MR_SO.MR_ID AND MR.DATA_FORMAT = '10' "
			 		+ "AND MR.INH_CLINIC_ID IN ("+inhCodeSql+") ";   
		 }
		 sql += ") t";
		 Query sqlQuery = entityManager.createNativeQuery(sql.toString());
		 sqlQuery.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		 @SuppressWarnings("unchecked")
		 List<Map<String, Object>> result = sqlQuery.getResultList();
		 entityManager.close(); 
		 return Integer.parseInt(result.get(0).get("COUNT").toString());
	}
	/**
	 * 取得ipd
	 * @param applYM
	 * @param fnSdate
	 * @param fnEdate
	 * @param outSdate
	 * @param outEdate
	 * @param inhCodes
	 * @return
	 */
	public int ipdCount(String applYM, String fnSdate, String fnEdate, String outSdate, String outEdate, String[] inhCodes){
		List<String> inhCodeList = new ArrayList<String>();
		String sql;
		sql = "SELECT COUNT(*) AS COUNT FROM ( "
				+ "SELECT    "
				+ "MR.ID AS INH_MR, "
				+ "IFNULL(MR.INH_CLINIC_ID,'') AS INH_NO, "
				+ "MR_SO.SUBJECT_TEXT  AS SUBJECTIVE, "
				+ "MR_SO.OBJECT_TEXT  AS OBJECTIVE, "
				+ "MR.OWN_EXPENSE AS INH_OWN_EXP, "
				+ "IFNULL(MR.CLINIC,'') AS CLINIC, "
				+ "IP_D.CASE_TYPE, "
				+ "IP_D.SEQ_NO, "
				+ "MR.ROC_ID, "
				+ "IP_D.PART_NO, "
				+ "IP_D.APPL_CAUSE_MARK, "
				+ "IP_D.ID_BIRTH_YMD, "
				+ "IP_D.PAY_TYPE, "
				+ "IP_D.FUNC_TYPE, "
				+ "IP_D.IN_DATE, "
				+ "IFNULL(IP_D.OUT_DATE,'') AS OUT_DATE, "
				+ "IP_D.APPL_START_DATE, "
				+ "IP_D.APPL_END_DATE, "
				+ "IP_D.E_BED_DAY, "
				+ "IP_D.S_BED_DAY, "
				+ "IP_D.PATIENT_SOURCE, "
				+ "IP_D.CARD_SEQ_NO, "
				+ "IFNULL(IP_D.TW_DRG_CODE,'') AS TW_DRG_CODE, "
				+ "IFNULL(IP_D.TW_DRG_PAY_TYPE,'') AS TW_DRG_PAY_TYPE, "
				+ "MR.PRSN_ID, "
				+ "IFNULL(IP_D.CASE_DRG_CODE,'') AS CASE_DRG_CODE, "
				+ "IP_D.TRAN_CODE, "
				+ "IFNULL(IP_D.ICD_CM_1, '') AS ICD_CM_1, "
				+ "IFNULL(IP_D.ICD_CM_2, '') AS ICD_CM_2, "
				+ "IFNULL(IP_D.ICD_CM_3, '') AS ICD_CM_3, "
				+ "IFNULL(IP_D.ICD_CM_4, '') AS ICD_CM_4, "
				+ "IFNULL(IP_D.ICD_CM_5, '') AS ICD_CM_5, "
				+ "IFNULL(IP_D.ICD_CM_6, '') AS ICD_CM_6, "
				+ "IFNULL(IP_D.ICD_CM_7, '') AS ICD_CM_7, "
				+ "IFNULL(IP_D.ICD_CM_8, '') AS ICD_CM_8, "
				+ "IFNULL(IP_D.ICD_CM_9, '') AS ICD_CM_9, "
				+ "IFNULL(IP_D.ICD_CM_10, '') AS ICD_CM_10, "
				+ "IFNULL(IP_D.ICD_CM_11, '') AS ICD_CM_11, "
				+ "IFNULL(IP_D.ICD_CM_12, '') AS ICD_CM_12, "
				+ "IFNULL(IP_D.ICD_CM_13, '') AS ICD_CM_13, "
				+ "IFNULL(IP_D.ICD_CM_14, '') AS ICD_CM_14, "
				+ "IFNULL(IP_D.ICD_CM_15, '') AS ICD_CM_15, "
				+ "IFNULL(IP_D.ICD_CM_16, '') AS ICD_CM_16, "
				+ "IFNULL(IP_D.ICD_CM_17, '') AS ICD_CM_17, "
				+ "IFNULL(IP_D.ICD_CM_18, '') AS ICD_CM_18, "
				+ "IFNULL(IP_D.ICD_CM_19, '') AS ICD_CM_19, "
				+ "IFNULL(IP_D.ICD_CM_20, '') AS ICD_CM_20, "
				+ "IFNULL(IP_D.ICD_OP_CODE1, '') AS ICD_OP_CODE1, "
				+ "IFNULL(IP_D.ICD_OP_CODE2, '') AS ICD_OP_CODE2, "
				+ "IFNULL(IP_D.ICD_OP_CODE3, '') AS ICD_OP_CODE3, "
				+ "IFNULL(IP_D.ICD_OP_CODE4, '') AS ICD_OP_CODE4, "
				+ "IFNULL(IP_D.ICD_OP_CODE5, '') AS ICD_OP_CODE5, "
				+ "IFNULL(IP_D.ICD_OP_CODE6, '') AS ICD_OP_CODE6, "
				+ "IFNULL(IP_D.ICD_OP_CODE7, '') AS ICD_OP_CODE7, "
				+ "IFNULL(IP_D.ICD_OP_CODE8, '') AS ICD_OP_CODE8, "
				+ "IFNULL(IP_D.ICD_OP_CODE9, '') AS ICD_OP_CODE9, "
				+ "IFNULL(IP_D.ICD_OP_CODE10, '') AS ICD_OP_CODE10, "
				+ "IFNULL(IP_D.ICD_OP_CODE11, '') AS ICD_OP_CODE11, "
				+ "IFNULL(IP_D.ICD_OP_CODE12, '') AS ICD_OP_CODE12, "
				+ "IFNULL(IP_D.ICD_OP_CODE13, '') AS ICD_OP_CODE13, "
				+ "IFNULL(IP_D.ICD_OP_CODE14, '') AS ICD_OP_CODE14, "
				+ "IFNULL(IP_D.ICD_OP_CODE15, '') AS ICD_OP_CODE15, "
				+ "IFNULL(IP_D.ICD_OP_CODE16, '') AS ICD_OP_CODE16, "
				+ "IFNULL(IP_D.ICD_OP_CODE17, '') AS ICD_OP_CODE17, "
				+ "IFNULL(IP_D.ICD_OP_CODE18, '') AS ICD_OP_CODE18, "
				+ "IFNULL(IP_D.ICD_OP_CODE19, '') AS ICD_OP_CODE19, "
				+ "IFNULL(IP_D.ICD_OP_CODE20, '') AS ICD_OP_CODE20, "
				+ "IP_D.ORDER_QTY, "
				+ "IP_D.DIAG_DOT, "
				+ "IP_D.ROOM_DOT, "
				+ "IP_D.MEAL_DOT, "
				+ "IP_D.AMIN_DOT, "
				+ "IP_D.RADO_DOT, "
				+ "IP_D.THRP_DOT, "
				+ "IP_D.SGRY_DOT, "
				+ "IP_D.PHSC_DOT, "
				+ "IP_D.BLOD_DOT, "
				+ "IP_D.HD_DOT, "
				+ "IP_D.ANE_DOT, "
				+ "IP_D.METR_DOT, "
				+ "IP_D.DRUG_DOT, "
				+ "IP_D.DSVC_DOT, "
				+ "IP_D.NRTP_DOT, "
				+ "IP_D.INJT_DOT, "
				+ "IP_D.BABY_DOT, "
				+ "IP_D.MED_DOT, "
				+ "IP_D.PART_DOT, "
				+ "IFNULL(IP_D.APPL_DOT,0) AS APPL_DOT, "
				+ "IP_D.EB_APPL30_DOT, "
				+ "IP_D.EB_PART30_DOT, "
				+ "IP_D.EB_APPL60_DOT, "
				+ "IP_D.EB_PART60_DOT, "
				+ "IP_D.EB_APPL61_DOT, "
				+ "IP_D.EB_PART61_DOT, "
				+ "IP_D.SB_APPL30_DOT, "
				+ "IP_D.SB_PART30_DOT, "
				+ "IP_D.SB_APPL90_DOT, "
				+ "IP_D.SB_PART90_DOT, "
				+ "IP_D.SB_APPL180_DOT, "
				+ "IP_D.SB_PART180_DOT, "
				+ "IP_D.SB_APPL181_DOT, "
				+ "IP_D.SB_PART181_DOT, "
				+ "IP_D.NB_BIRTHDAY, "
				+ "IFNULL(IP_D.CHILD_MARK,'') AS CHILD_MARK, "
				+ "IP_D.TW_DRGS_SUIT_MARK, "
				+ "MR.NAME, "
				+ "IFNULL(IP_D.AGENCY_ID,'') AS AGENCY_ID, "
				+ "IFNULL(IP_D.TRAN_IN_HOSP_ID,'') AS TRAN_IN_HOSP_ID, "
				+ "IFNULL(IP_D.TRAN_OUT_HOSP_ID,'') AS TRAN_OUT_HOSP_ID, "
				+ "IFNULL(IP_D.HOSP_ID,'') AS HOSP_ID, "
				+ "IFNULL(IP_D.SVC_PLAN,'') AS SVC_PLAN, "
				+ "IFNULL(IP_D.PILOT_PROJECT,'') AS PILOT_PROJECT, "
				+ "IP_D.NON_APPL_DOT, "
				+ "IFNULL(MR.PRSN_NAME,'') AS PRSN_NAME "
				+ "FROM MR, IP_D, MR_SO WHERE MR.ID = IP_D.MR_ID AND MR.ID = MR_SO.MR_ID "
		 		;
		 if(!applYM.isEmpty()) {
			 sql += " AND MR.APPL_YM = '"+applYM+"'";
		 }
		 if(!fnSdate.isEmpty()) {
			 String fsdate = DateTool.convertChineseToADWithSlash(fnSdate).replaceAll("/", "-");
			 String fedate = DateTool.convertChineseToADWithSlash(fnEdate).replaceAll("/", "-");
			 sql += " AND MR.MR_END_DATE BETWEEN '"+fsdate+"' AND '"+fedate+"' ";
		 }
		 if(!outSdate.isEmpty()) {
			 sql += " AND IP_D.OUT_DATE BETWEEN '"+outSdate+"' AND '"+outEdate+"' ";
		 }
		 if(inhCodes.length > 0) {
			 String inhCodeSql = "";
			 for (String str : inhCodes) {
				 
				 inhCodeList.add(str);
			     inhCodeSql += "'" + str + "',";
			 }
			 
			 inhCodeSql = inhCodeSql.substring(0,inhCodeSql.length() - 1);
			 sql += "AND MR.INH_CLINIC_ID IN ("+inhCodeSql+") ";   
		 }
		 sql += ") t";
		 Query sqlQuery = entityManager.createNativeQuery(sql.toString());
		 sqlQuery.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		 @SuppressWarnings("unchecked")
		 List<Map<String, Object>> result = sqlQuery.getResultList();
		 entityManager.close(); 
		 
		return Integer.parseInt(result.get(0).get("COUNT").toString());
	}
	/**
	 * 取得ipp
	 * @param applYM
	 * @param fnSdate
	 * @param fnEdate
	 * @param outSdate
	 * @param outEdate
	 * @param inhCodes
	 * @return
	 */
	public int ippCount(String applYM, String fnSdate, String fnEdate, String outSdate, String outEdate, String[] inhCodes){
		List<String> inhCodeList = new ArrayList<String>();
		String sql;
		sql = "SELECT COUNT(*) AS COUNT FROM ( "
				+ "SELECT  "
				+ "IFNULL(MR.INH_CLINIC_ID,'') AS INH_NO, "
				+ "IFNULL(MR.INH_CODE,'') AS INH_CODE, "
				+ "IFNULL(MR.APPL_STATUS,0) AS APPL_STATUS, "
				+ "IFNULL(IP_P.PAY_BY ,'') AS PAY_STATUS, "
				+ "IP_P.ORDER_SEQ_NO, "
				+ "IP_P.ORDER_TYPE, "
				+ "IP_P.ORDER_CODE, "
				+ "IP_P.PAY_RATE, "
				+ "IFNULL(IP_P.DRUG_USE,0) AS DRUG_USE, "
				+ "IFNULL(IP_P.DRUG_FRE,'') AS DRUG_FRE, "
				+ "IFNULL(IP_P.DRUG_PATH,'') AS DRUG_PATH, "
				+ "IFNULL(IP_P.CON_FUNC_TYPE,'') AS CON_FUNC_TYPE, "
				+ "IFNULL(IP_P.BED_NO,'') AS BED_NO, "
				+ "IFNULL(IP_P.CURE_PATH,'') AS CURE_PATH, "
				+ "IFNULL(IP_P.TW_DRGS_CALCU,0) AS TW_DRGS_CALCU, "
				+ "IFNULL(IP_P.PART_ACCO_DATA,'') AS PART_ACCO_DATA, "
				+ "IFNULL(IP_P.DONATER,'') AS DONATER, "
				+ "IP_P.START_TIME, "
				+ "IP_P.END_TIME, "
				+ "IP_P.TOTAL_Q, "
				+ "IP_P.UNIT_P, "
				+ "IP_P.TOTAL_DOT, "
				+ "IFNULL(IP_P.PRE_NO,'') AS PRE_NO, "
				+ "IFNULL(IP_P.PRSN_ID,'') AS PRSN_ID, "
				+ "IFNULL(IP_P.IMG_SOURCE,'') AS IMG_SOURCE, "
				+ "IP_P.FUNC_TYPE, "
				+ "IFNULL(IP_P.OWN_EXP_MTR_NO,'') AS OWN_EXP_MTR_NO, "
				+ "IFNULL(IP_P.NON_LIST_MARK,'') AS NON_LIST_MARK, "
				+ "IFNULL(IP_P.NON_LIST_NAME,'') AS NON_LIST_NAME, "
				+ "IFNULL(IP_P.COMM_HOSP_ID,'') AS COMM_HOSP_ID, "
				+ "IFNULL(IP_P.DRUG_SERIAL_NO,'') AS DRUG_SERIAL_NO "
		 		;
		 if(!applYM.isEmpty()) {
			 sql += " FROM MR, IP_P WHERE MR.ID = IP_P.MR_ID "
			 		+ "AND MR.APPL_YM = '"+applYM+"'";
		 }
		 if(!fnSdate.isEmpty()) {
			 String fsdate = DateTool.convertChineseToADWithSlash(fnSdate).replaceAll("/", "-");
			 String fedate = DateTool.convertChineseToADWithSlash(fnEdate).replaceAll("/", "-");
			 sql += " FROM MR, IP_P, IP_D WHERE MR.ID = IP_P.MR_ID AND IP_D.ID = IP_P.IPD_ID "
			 		+ "AND MR.MR_END_DATE BETWEEN '"+fsdate+"' AND '"+fedate+"' ";
		 }
		 if(!outSdate.isEmpty()) {
			 sql += " FROM MR, IP_P, IP_D WHERE MR.ID = IP_P.MR_ID AND IP_D.ID = IP_P.IPD_ID "
			 		+ "AND IP_D.OUT_DATE BETWEEN '"+outSdate+"' AND '"+outEdate+"' ";
		 }
		 if(inhCodes.length > 0) {
			 String inhCodeSql = "";
			 for (String str : inhCodes) {
				 
				 inhCodeList.add(str);
			     inhCodeSql += "'" + str + "',";
			 }
			 
			 inhCodeSql = inhCodeSql.substring(0,inhCodeSql.length() - 1);
			 sql += " "
			 		+ "AND MR.INH_CLINIC_ID IN ("+inhCodeSql+") ";   
		 }
		 sql += ") t";
		 Query sqlQuery = entityManager.createNativeQuery(sql.toString());
		 sqlQuery.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		 @SuppressWarnings("unchecked")
		 List<Map<String, Object>> result = sqlQuery.getResultList();
		 entityManager.close(); 
		 
		 return Integer.parseInt(result.get(0).get("COUNT").toString());
	}
	
	/**
	 * 取得ipso
	 * @param applYM
	 * @param fnSdate
	 * @param fnEdate
	 * @param outSdate
	 * @param outEdate
	 * @param inhCodes
	 * @return
	 */
	public int ipsoCount(String applYM, String fnSdate, String fnEdate, String outSdate, String outEdate, String[] inhCodes){
		
		List<String> inhCodeList = new ArrayList<String>();
		 String sql;
		 sql = "SELECT COUNT(*) AS COUNT FROM ( "
		 		+ "SELECT  "
		 		+ "IFNULL(MR.INH_CLINIC_ID,'') AS INH_NO, "
		 		+ "MR_SO.SUBJECT_TEXT, "
		 		+ "MR_SO.OBJECT_TEXT, "
		 		+ "MR_SO.DISCHARGE_TEXT "
		 		;
		 if(!applYM.isEmpty()) {
			 sql += " FROM MR, MR_SO WHERE MR.ID = MR_SO.MR_ID AND MR.DATA_FORMAT = '20' "
			 		+ " AND MR.APPL_YM = '"+applYM+"'";
		 }
		 if(!fnSdate.isEmpty()) {
			 String fsdate = DateTool.convertChineseToADWithSlash(fnSdate).replaceAll("/", "-");
			 String fedate = DateTool.convertChineseToADWithSlash(fnEdate).replaceAll("/", "-");
			 sql += " FROM MR, MR_SO, IP_D WHERE MR.ID = MR_SO.MR_ID AND MR.ID = IP_D.MR_ID AND MR.DATA_FORMAT = '20'  "
			 		+ "AND MR.MR_END_DATE BETWEEN '"+fsdate+"' AND '"+fedate+"' ";
		 }
		 if(!outSdate.isEmpty()) {
			 sql += " FROM MR, MR_SO, IP_D WHERE MR.ID = MR_SO.MR_ID AND MR.ID = IP_D.MR_ID AND MR.DATA_FORMAT = '20'  "
				 		+ "AND IP_D.OUT_DATE BETWEEN '"+outSdate+"' AND '"+outEdate+"' ";
		 }
		 if(inhCodes.length > 0) {
			 String inhCodeSql = "";
			 for (String str : inhCodes) {
				 
				 inhCodeList.add(str);
			     inhCodeSql += "'" + str + "',";
			 }
			 
			 inhCodeSql = inhCodeSql.substring(0,inhCodeSql.length() - 1);
			 sql += " FROM MR, MR_SO WHERE MR.ID = MR_SO.MR_ID AND MR.DATA_FORMAT = '20' "
			 		+ "AND MR.INH_CLINIC_ID IN ("+inhCodeSql+") ";   
		 }
		 sql += ") t";
		 Query sqlQuery = entityManager.createNativeQuery(sql.toString());
		 sqlQuery.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		 @SuppressWarnings("unchecked")
		 List<Map<String, Object>> result = sqlQuery.getResultList();
		 entityManager.close(); 
		 return Integer.parseInt(result.get(0).get("COUNT").toString());
	}
	
	public int deductedNoteCount(String applYM, String fnSdate, String fnEdate, String outSdate, String outEdate,String[] dataFormats, String[] inhCodes){
		
		List<String> inhCodeList = new ArrayList<String>();
		List<String> dataFormatList = new ArrayList<String>();
		String sql;
		sql = "SELECT COUNT(*) AS COUNT FROM ( "
				+ "SELECT  "
				+ "IFNULL(MR.INH_CLINIC_ID,'') AS INH_NO, "
				+ "DEDUCTED_NOTE.CAT, "
				+ "DEDUCTED_NOTE.ITEM, "
				+ "DEDUCTED_NOTE.CODE, "
				+ "DEDUCTED_NOTE.DEDUCTED_ORDER, "
				+ "DEDUCTED_NOTE.DEDUCTED_QUANTITY, "
				+ "DEDUCTED_NOTE.DEDUCTED_AMOUNT, "
				+ "IFNULL(DEDUCTED_NOTE.REASON,'') AS REASON, "
				+ "IFNULL(DEDUCTED_NOTE.NOTE,'') AS NOTE, "
				+ "IFNULL(DEDUCTED_NOTE.ROLLBACK_M,0) AS ROLLBACK_M, "
				+ "IFNULL(DEDUCTED_NOTE.ROLLBACK_Q,0) AS ROLLBACK_Q, "
				+ "IFNULL(DEDUCTED_NOTE.AFR_QUANTITY,0) AS AFR_QUANTITY, "
				+ "IFNULL(DEDUCTED_NOTE.AFR_AMOUNT,0) AS AFR_AMOUNT, "
				+ "IFNULL(DEDUCTED_NOTE.AFR_PAY_QUANTITY,0) AS AFR_PAY_QUANTITY, "
				+ "IFNULL(DEDUCTED_NOTE.AFR_PAY_AMOUNT,0) AS AFR_PAY_AMOUNT, "
				+ "IFNULL(DEDUCTED_NOTE.AFR_NO_PAY_CODE,'') AS AFR_NO_PAY_CODE, "
				+ "IFNULL(DEDUCTED_NOTE.AFR_NO_PAY_DESC,'') AS AFR_NO_PAY_DESC, "
				+ "IFNULL(DEDUCTED_NOTE.AFR_NOTE,'') AS AFR_NOTE, "
				+ "IFNULL(DEDUCTED_NOTE.DISPUTE_QUANTITY,0) AS DISPUTE_QUANTITY, "
				+ "IFNULL(DEDUCTED_NOTE.DISPUTE_AMOUNT,0) AS DISPUTE_AMOUNT, "
				+ "IFNULL(DEDUCTED_NOTE.DISPUTE_PAY_QUANTITY,0) AS DISPUTE_PAY_QUANTITY, "
				+ "IFNULL(DEDUCTED_NOTE.DISPUTE_PAY_AMOUNT,0) AS DISPUTE_PAY_AMOUNT, "
				+ "IFNULL(DEDUCTED_NOTE.DISPUTE_NO_PAY_CODE,'') AS DISPUTE_NO_PAY_CODE, "
				+ "IFNULL(DEDUCTED_NOTE.DISPUTE_NO_PAY_DESC,'') AS DISPUTE_NO_PAY_DESC, "
				+ "IFNULL(DEDUCTED_NOTE.DISPUTE_NOTE,'') AS DISPUTE_NOTE, "
				+ "IFNULL(DEDUCTED_NOTE.DEDUCTED_DATE,'') AS DEDUCTED_DATE, "
				+ "IFNULL(DEDUCTED_NOTE.ROLLBACK_DATE,'') AS ROLLBACK_DATE, "
				+ "IFNULL(DEDUCTED_NOTE.DISPUTE_DATE,'') AS DISPUTE_DATE "
				;
		
		if(dataFormats.length > 0) {
			///如果門急診/住院
			if(dataFormats.length > 1) {
				if(!applYM.isEmpty()) {
					 sql += " FROM MR, DEDUCTED_NOTE WHERE MR.ID = DEDUCTED_NOTE.MR_ID  "
					 		+ " AND MR.APPL_YM = '"+applYM+"'";
				 }
				 if(!fnSdate.isEmpty()) {
					 String fsdate = DateTool.convertChineseToADWithSlash(fnSdate).replaceAll("/", "-");
					 String fedate = DateTool.convertChineseToADWithSlash(fnEdate).replaceAll("/", "-");
					 sql += " FROM MR, DEDUCTED_NOTE WHERE MR.ID = DEDUCTED_NOTE.MR_ID  "
					 		+ "AND MR.MR_END_DATE BETWEEN '"+fsdate+"' AND '"+fedate+"' ";
				 }
				 if(!outSdate.isEmpty()) {
					 sql += " FROM MR, DEDUCTED_NOTE, IP_D WHERE MR.ID = DEDUCTED_NOTE.MR_ID AND MR.ID = IP_D.MR_ID "
						 		+ "AND IP_D.OUT_DATE BETWEEN '"+outSdate+"' AND '"+outEdate+"' ";
				 }
				 if(inhCodes.length > 0) {
					 String inhCodeSql = "";
					 for (String str : inhCodes) {
						 
						 inhCodeList.add(str);
					     inhCodeSql += "'" + str + "',";
					 }
					 
					 inhCodeSql = inhCodeSql.substring(0,inhCodeSql.length() - 1);
					 sql += " FROM MR, DEDUCTED_NOTE WHERE MR.ID = DEDUCTED_NOTE.MR_ID  "
					 		+ "AND MR.INH_CLINIC_ID IN ("+inhCodeSql+") ";   
				 }
			}
			else {
				String dataformat = dataFormats[0];
				///門急診
				if(dataformat.equals("op")) {
					if(!applYM.isEmpty()) {
						 sql += " FROM MR, DEDUCTED_NOTE WHERE MR.ID = DEDUCTED_NOTE.MR_ID AND MR.DATA_FORMAT = '10'  "
						 		+ " AND MR.APPL_YM = '"+applYM+"'";
					 }
					 if(!fnSdate.isEmpty()) {
						 String fsdate = DateTool.convertChineseToADWithSlash(fnSdate).replaceAll("/", "-");
						 String fedate = DateTool.convertChineseToADWithSlash(fnEdate).replaceAll("/", "-");
						 sql += " FROM MR, DEDUCTED_NOTE WHERE MR.ID = DEDUCTED_NOTE.MR_ID AND MR.DATA_FORMAT = '10' "
						 		+ "AND MR.MR_END_DATE BETWEEN '"+fsdate+"' AND '"+fedate+"' ";
					 }
					 if(inhCodes.length > 0) {
						 String inhCodeSql = "";
						 for (String str : inhCodes) {
							 
							 inhCodeList.add(str);
						     inhCodeSql += "'" + str + "',";
						 }
						 
						 inhCodeSql = inhCodeSql.substring(0,inhCodeSql.length() - 1);
						 sql += " FROM MR, DEDUCTED_NOTE WHERE MR.ID = DEDUCTED_NOTE.MR_ID AND MR.DATA_FORMAT = '10' "
						 		+ "AND MR.INH_CLINIC_ID IN ("+inhCodeSql+") ";   
					 }
				}
				else {///住院
					if(!applYM.isEmpty()) {
						 sql += " FROM MR, DEDUCTED_NOTE WHERE MR.ID = DEDUCTED_NOTE.MR_ID AND MR.DATA_FORMAT = '20' "
						 		+ " AND MR.APPL_YM = '"+applYM+"'";
					 }
					 if(!fnSdate.isEmpty()) {
						 String fsdate = DateTool.convertChineseToADWithSlash(fnSdate).replaceAll("/", "-");
						 String fedate = DateTool.convertChineseToADWithSlash(fnEdate).replaceAll("/", "-");
						 sql += " FROM MR, DEDUCTED_NOTE WHERE MR.ID = DEDUCTED_NOTE.MR_ID AND MR.DATA_FORMAT = '20' "
						 		+ "AND MR.MR_END_DATE BETWEEN '"+fsdate+"' AND '"+fedate+"' ";
					 }
					 if(!outSdate.isEmpty()) {
						 sql += " FROM MR, DEDUCTED_NOTE, IP_D WHERE MR.ID = DEDUCTED_NOTE.MR_ID AND MR.ID = IP_D.MR_ID "
							 		+ "AND IP_D.OUT_DATE BETWEEN '"+outSdate+"' AND '"+outEdate+"' ";
					 }
					 if(inhCodes.length > 0) {
						 String inhCodeSql = "";
						 for (String str : inhCodes) {
							 
							 inhCodeList.add(str);
						     inhCodeSql += "'" + str + "',";
						 }
						 
						 inhCodeSql = inhCodeSql.substring(0,inhCodeSql.length() - 1);
						 sql += " FROM MR, DEDUCTED_NOTE WHERE MR.ID = DEDUCTED_NOTE.MR_ID AND MR.DATA_FORMAT = '20' "
						 		+ "AND MR.INH_CLINIC_ID IN ("+inhCodeSql+") ";   
					 }
				}
			}
			
		}
		 sql += ") t";
		 Query sqlQuery = entityManager.createNativeQuery(sql.toString());
		 sqlQuery.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		 @SuppressWarnings("unchecked")
		 List<Map<String, Object>> result = sqlQuery.getResultList();
		 entityManager.close(); 
		
		
		 return Integer.parseInt(result.get(0).get("COUNT").toString());
	}
	
	public List<LinkedHashMap<String, Object>> opdMapto(List<Map<String, Object>> sqlRes) {
		List<LinkedHashMap<String, Object>> result = new ArrayList<LinkedHashMap<String,Object>>();
		LinkedHashMap<String, Object> m  = new LinkedHashMap<String, Object>();
		if(sqlRes.size() > 0) {
			for(int i=0; i < sqlRes.size(); i++) {
				
				m.put("INH_MR", sqlRes.get(i).get("INH_MR"));          
				m.put("INH_NO", sqlRes.get(i).get("INH_NO"));                            
				m.put("SUBJECTIVE", sqlRes.get(i).get("SUBJECTIVE"));                            
				m.put("OBJECTIVE", sqlRes.get(i).get("OBJECTIVE"));                            
				m.put("INH_OWN_EXP", sqlRes.get(i).get("INH_OWN_EXP"));                            
				m.put("CLINIC", sqlRes.get(i).get("CLINIC"));                            
				m.put("CASE_TYPE", sqlRes.get(i).get("CASE_TYPE"));                            
				m.put("SEQ_NO", sqlRes.get(i).get("SEQ_NO"));                            
				m.put("ROC_ID", sqlRes.get(i).get("ROC_ID"));                            
				m.put("CURE_ITEM_NO1", sqlRes.get(i).get("CURE_ITEM_NO1"));                            
				m.put("CURE_ITEM_NO2", sqlRes.get(i).get("CURE_ITEM_NO2"));                            
				m.put("CURE_ITEM_NO3", sqlRes.get(i).get("CURE_ITEM_NO3"));                            
				m.put("CURE_ITEM_NO4", sqlRes.get(i).get("CURE_ITEM_NO4"));                            
				m.put("FUNC_TYPE", sqlRes.get(i).get("FUNC_TYPE"));                            
				m.put("FUNC_DATE", sqlRes.get(i).get("FUNC_DATE"));                            
				m.put("FUNC_END_DATE", sqlRes.get(i).get("FUNC_END_DATE"));                            
				m.put("ID_BIRTH_YMD", sqlRes.get(i).get("ID_BIRTH_YMD"));                            
				m.put("APPL_CAUSE_MARK", sqlRes.get(i).get("APPL_CAUSE_MARK"));                            
				m.put("CARE_MARK", sqlRes.get(i).get("CARE_MARK"));                            
				m.put("PAY_TYPE", sqlRes.get(i).get("PAY_TYPE"));                            
				m.put("PART_NO", sqlRes.get(i).get("PART_NO"));                            
				m.put("SHARE_MARK", sqlRes.get(i).get("SHARE_MARK"));                            
				m.put("SHARE_HOSP_ID", sqlRes.get(i).get("SHARE_HOSP_ID"));                            
				m.put("PAT_TRAN_OUT", sqlRes.get(i).get("PAT_TRAN_OUT"));                            
				m.put("ICD_CM_1", sqlRes.get(i).get("ICD_CM_1"));                            
				m.put("ICD_CM_2", sqlRes.get(i).get("ICD_CM_2"));                            
				m.put("ICD_CM_3", sqlRes.get(i).get("ICD_CM_3"));                            
				m.put("ICD_CM_4", sqlRes.get(i).get("ICD_CM_4"));                            
				m.put("ICD_CM_5", sqlRes.get(i).get("ICD_CM_5"));                            
				m.put("ICD_OP_CODE1", sqlRes.get(i).get("ICD_OP_CODE1"));                            
				m.put("ICD_OP_CODE2", sqlRes.get(i).get("ICD_OP_CODE2"));                            
				m.put("ICD_OP_CODE3", sqlRes.get(i).get("ICD_OP_CODE3"));                            
				m.put("DRUG_DAY", sqlRes.get(i).get("DRUG_DAY"));                            
				m.put("MED_TYPE", sqlRes.get(i).get("MED_TYPE"));                            
				m.put("CARD_SEQ_NO", sqlRes.get(i).get("CARD_SEQ_NO"));                            
				m.put("PRSN_ID", sqlRes.get(i).get("PRSN_ID"));                            
				m.put("PHAR_ID", sqlRes.get(i).get("PHAR_ID"));                            
				m.put("DRUG_DOT", sqlRes.get(i).get("DRUG_DOT"));                            
				m.put("TREAT_DOT", sqlRes.get(i).get("TREAT_DOT"));                            
				m.put("METR_DOT", sqlRes.get(i).get("METR_DOT"));                            
				m.put("TREAT_CODE", sqlRes.get(i).get("TREAT_CODE"));                            
				m.put("DIAG_DOT", sqlRes.get(i).get("DIAG_DOT"));                            
				m.put("DSVC_NO", sqlRes.get(i).get("DSVC_NO"));                            
				m.put("DSVC_DOT", sqlRes.get(i).get("DSVC_DOT"));                            
				m.put("T_DOT", sqlRes.get(i).get("T_DOT"));                            
				m.put("PART_DOT", sqlRes.get(i).get("PART_DOT"));                            
				m.put("T_APPL_DOT", sqlRes.get(i).get("T_APPL_DOT"));                            
				m.put("CASE_PAY_CODE", sqlRes.get(i).get("CASE_PAY_CODE"));                            
				m.put("ASSIST_PART_DOT", sqlRes.get(i).get("ASSIST_PART_DOT"));                            
				m.put("CHR_DAYS", sqlRes.get(i).get("CHR_DAYS"));                            
				m.put("NB_BIRTHDAY", sqlRes.get(i).get("NB_BIRTHDAY"));                            
				m.put("OUT_SVC_PLAN_CODE", sqlRes.get(i).get("OUT_SVC_PLAN_CODE"));                            
				m.put("NAME", sqlRes.get(i).get("NAME"));                            
				m.put("AGENCY_ID", sqlRes.get(i).get("AGENCY_ID"));                            
				m.put("CHILD_MARK", sqlRes.get(i).get("CHILD_MARK"));                            
				m.put("SPE_AREA_SVC", sqlRes.get(i).get("SPE_AREA_SVC"));                            
				m.put("SUPPORT_AREA", sqlRes.get(i).get("SUPPORT_AREA"));                            
				m.put("HOSP_ID", sqlRes.get(i).get("HOSP_ID"));                            
				m.put("TRAN_IN_HOSP_ID", sqlRes.get(i).get("TRAN_IN_HOSP_ID"));                            
				m.put("ORI_CARD_SEQ_NO", sqlRes.get(i).get("ORI_CARD_SEQ_NO"));                            
				m.put("MR.ID", sqlRes.get(i).get("MRID"));
				
				result.add(m);
				m = new LinkedHashMap<String, Object>();
			}
		}
		else {
			m.put("INH_MR", "");
			m.put("INH_NO", "");
			m.put("SUBJECTIVE","");
			m.put("OBJECTIVE", "");
			m.put("INH_OWN_EXP", "");
			m.put("CLINIC", "");
			m.put("CASE_TYPE", "");
			m.put("SEQ_NO", "");
			m.put("ROC_ID", "");
			m.put("CURE_ITEM_NO1", "");
			m.put("CURE_ITEM_NO2", "");
			m.put("CURE_ITEM_NO3", "");
			m.put("CURE_ITEM_NO4", "");
			m.put("FUNC_TYPE", "");
			m.put("FUNC_DATE", "");
			m.put("FUNC_END_DATE","");
			m.put("ID_BIRTH_YMD", "");
			m.put("APPL_CAUSE_MARK", "");
			m.put("CARE_MARK","");
			m.put("PAY_TYPE", "");
			m.put("PART_NO", "");
			m.put("SHARE_MARK", "");
			m.put("SHARE_HOSP_ID","");
			m.put("PAT_TRAN_OUT", "");
			m.put("ICD_CM_1", "");
			m.put("ICD_CM_2", "");
			m.put("ICD_CM_3", "");
			m.put("ICD_CM_4", "");
			m.put("ICD_CM_5", "");
			m.put("ICD_OP_CODE1", "");
			m.put("ICD_OP_CODE2", "");
			m.put("ICD_OP_CODE3", "");
			m.put("DRUG_DAY", "");
			m.put("MED_TYPE", "");
			m.put("CARD_SEQ_NO", "");
			m.put("PRSN_ID", "");
			m.put("PHAR_ID", "");
			m.put("DRUG_DOT","");
			m.put("TREAT_DOT", "");
			m.put("METR_DOT", "");
			m.put("TREAT_CODE", "");
			m.put("DIAG_DOT", "");
			m.put("DSVC_NO", "");
			m.put("DSVC_DOT","");
			m.put("T_DOT", "");
			m.put("PART_DOT", "");
			m.put("T_APPL_DOT", "");
			m.put("CASE_PAY_CODE", "");
			m.put("ASSIST_PART_DOT", "");
			m.put("CHR_DAYS", "");
			m.put("NB_BIRTHDAY", "");
			m.put("OUT_SVC_PLAN_CODE", "");
			m.put("NAME", "");
			m.put("AGENCY_ID", "");
			m.put("CHILD_MARK", "");
			m.put("SPE_AREA_SVC", "");
			m.put("SUPPORT_AREA", "");
			m.put("HOSP_ID", "");
			m.put("TRAN_IN_HOSP_ID", "");
			m.put("ORI_CARD_SEQ_NO", "");
			m.put("MR.ID", "");
			
			
			result.add(m);
			m = new LinkedHashMap<String, Object>();
		}
		
		return result;
	}
	
	public List<LinkedHashMap<String, Object>> oppMapto(List<Map<String, Object>> sqlRes) {
		List<LinkedHashMap<String, Object>> result = new ArrayList<LinkedHashMap<String,Object>>();
		LinkedHashMap<String, Object> m  = new LinkedHashMap<String, Object>();
		if(sqlRes.size() > 0) {
			for(int i=0; i < sqlRes.size(); i++) {
				m.put("INH_NO", sqlRes.get(i).get("INH_NO"));                            
				m.put("INH_CODE", sqlRes.get(i).get("INH_CODE"));                            
				m.put("APPL_STATUS", sqlRes.get(i).get("APPL_STATUS"));                            
				m.put("PAY_STATUS", sqlRes.get(i).get("PAY_STATUS"));                            
				m.put("DRUG_DAY", sqlRes.get(i).get("DRUG_DAY"));                            
				m.put("MED_TYPE", sqlRes.get(i).get("MED_TYPE"));                            
				m.put("ORDER_TYPE", sqlRes.get(i).get("ORDER_TYPE"));                            
				m.put("DRUG_NO", sqlRes.get(i).get("DRUG_NO"));                            
				m.put("DRUG_USE", sqlRes.get(i).get("DRUG_USE"));                            
				m.put("CURE_PATH", sqlRes.get(i).get("CURE_PATH"));                            
				m.put("DRUG_FRE", sqlRes.get(i).get("DRUG_FRE"));                            
				m.put("PAY_RATE", sqlRes.get(i).get("PAY_RATE"));                            
				m.put("DRUG_PATH", sqlRes.get(i).get("DRUG_PATH"));                            
				m.put("TOTAL_Q", sqlRes.get(i).get("TOTAL_Q"));                            
				m.put("UNIT_P", sqlRes.get(i).get("UNIT_P"));                            
				m.put("TOTAL_DOT", sqlRes.get(i).get("TOTAL_DOT"));                            
				m.put("ORDER_SEQ_NO", sqlRes.get(i).get("ORDER_SEQ_NO"));                            
				m.put("START_TIME", sqlRes.get(i).get("START_TIME"));                            
				m.put("END_TIME", sqlRes.get(i).get("END_TIME"));                            
				m.put("PRSN_ID", sqlRes.get(i).get("PRSN_ID"));                            
				m.put("CHR_MARK", sqlRes.get(i).get("CHR_MARK"));                            
				m.put("IMG_SOURCE", sqlRes.get(i).get("IMG_SOURCE"));                            
				m.put("PRE_NO", sqlRes.get(i).get("PRE_NO"));                            
				m.put("FUNC_TYPE", sqlRes.get(i).get("FUNC_TYPE"));                            
				m.put("OWN_EXP_MTR_NO", sqlRes.get(i).get("OWN_EXP_MTR_NO"));                            
				m.put("NON_LIST_MARK", sqlRes.get(i).get("NON_LIST_MARK"));                            
				m.put("NON_LIST_NAME", sqlRes.get(i).get("NON_LIST_NAME"));                            
				m.put("COMM_HOSP_ID", sqlRes.get(i).get("COMM_HOSP_ID"));                            
				m.put("DRUG_SERIAL_NO", sqlRes.get(i).get("DRUG_SERIAL_NO"));                            
				m.put("MR.ID", sqlRes.get(i).get("MRID"));
				
				
				result.add(m);
				m = new LinkedHashMap<String, Object>();
			}
		}
		else {
			m.put("INH_MR", "");
			m.put("INH_CODE", "");
			m.put("APPL_STATUS", "");
			m.put("PAY_STATUS", "");
			m.put("DRUG_DAY", "");
			m.put("MED_TYPE", "");
			m.put("ORDER_TYPE", "");
			m.put("DRUG_NO", "");
			m.put("DRUG_USE", "");
			m.put("CURE_PATH", "");
			m.put("DRUG_FRE", "");
			m.put("PAY_RATE", "");
			m.put("DRUG_PATH", "");
			m.put("TOTAL_Q", "");
			m.put("UNIT_P", "");
			m.put("TOTAL_DOT", "");
			m.put("ORDER_SEQ_NO", "");
			m.put("START_TIME", "");
			m.put("END_TIME", "");
			m.put("PRSN_ID", "");
			m.put("CHR_MARK", "");
			m.put("IMG_SOURCE", "");
			m.put("PRE_NO", "");
			m.put("FUNC_TYPE", "");
			m.put("OWN_EXP_MTR_NO", "");
			m.put("NON_LIST_MARK", "");
			m.put("NON_LIST_NAME", "");
			m.put("COMM_HOSP_ID", "");
			m.put("DRUG_SERIAL_NO", "");
			m.put("MR.ID", "");
			result.add(m);
			m = new LinkedHashMap<String, Object>();
		}
		
		return result;
	}
	
	public List<LinkedHashMap<String, Object>> opdsoMapto(List<Map<String, Object>> sqlRes) {
		List<LinkedHashMap<String, Object>> result = new ArrayList<LinkedHashMap<String,Object>>();
		LinkedHashMap<String, Object> m  = new LinkedHashMap<String, Object>();
		if(sqlRes.size() > 0) {
			for(int i=0; i < sqlRes.size(); i++) {
				m.put("INH_NO", sqlRes.get(i).get("INH_NO"));                            
				m.put("SUBJECT_TEXT", sqlRes.get(i).get("SUBJECT_TEXT"));                            
				m.put("OBJECT_TEXT", sqlRes.get(i).get("OBJECT_TEXT"));
				m.put("MR.ID", sqlRes.get(i).get("MRID"));
						
				
				result.add(m);
				m = new LinkedHashMap<String, Object>();
			}
		}
		else {
			
			m.put("INH_NO", "");                            
			m.put("SUBJECT_TEXT", "");                            
			m.put("OBJECT_TEXT", "");               
			m.put("MR.ID", "");
			
			result.add(m);
			m = new LinkedHashMap<String, Object>();
		}
		
		return result;
	}
	
	public List<LinkedHashMap<String, Object>> ipdMapto(List<Map<String, Object>> sqlRes) {
		List<LinkedHashMap<String, Object>> result = new ArrayList<LinkedHashMap<String,Object>>();
		LinkedHashMap<String, Object> m  = new LinkedHashMap<String, Object>();
		if(sqlRes.size() > 0) {
			for(int i=0; i < sqlRes.size(); i++) {
				

				m.put("INH_MR", sqlRes.get(i).get("INH_MR"));  
				m.put("INH_NO", sqlRes.get(i).get("INH_NO"));                            
				m.put("SUBJECTIVE", sqlRes.get(i).get("SUBJECTIVE"));                            
				m.put("OBJECTIVE", sqlRes.get(i).get("OBJECTIVE"));                            
				m.put("INH_OWN_EXP", sqlRes.get(i).get("INH_OWN_EXP"));                            
				m.put("CLINIC", sqlRes.get(i).get("CLINIC"));                            
				m.put("CASE_TYPE", sqlRes.get(i).get("CASE_TYPE"));                            
				m.put("SEQ_NO", sqlRes.get(i).get("SEQ_NO"));                            
				m.put("ROC_ID", sqlRes.get(i).get("ROC_ID"));  
				m.put("PART_NO", sqlRes.get(i).get("PART_NO"));  
				m.put("APPL_CAUSE_MARK", sqlRes.get(i).get("APPL_CAUSE_MARK"));   
				m.put("ID_BIRTH_YMD", sqlRes.get(i).get("ID_BIRTH_YMD"));                            
				m.put("PAY_TYPE", sqlRes.get(i).get("PAY_TYPE"));                            
				m.put("FUNC_TYPE", sqlRes.get(i).get("FUNC_TYPE"));                            
				m.put("IN_DATE", sqlRes.get(i).get("IN_DATE"));  
				m.put("OUT_DATE", sqlRes.get(i).get("OUT_DATE"));  
				m.put("APPL_START_DATE", sqlRes.get(i).get("APPL_START_DATE"));  
				m.put("APPL_END_DATE", sqlRes.get(i).get("APPL_END_DATE"));  
				m.put("E_BED_DAY", sqlRes.get(i).get("E_BED_DAY"));  
				m.put("S_BED_DAY", sqlRes.get(i).get("S_BED_DAY"));  
				m.put("PATIENT_SOURCE", sqlRes.get(i).get("PATIENT_SOURCE"));  
				m.put("CARD_SEQ_NO", sqlRes.get(i).get("CARD_SEQ_NO"));  
				m.put("TW_DRG_CODE", sqlRes.get(i).get("TW_DRG_CODE"));  
				m.put("TW_DRG_PAY_TYPE", sqlRes.get(i).get("TW_DRG_PAY_TYPE"));  
				m.put("E_BED_DAY", sqlRes.get(i).get("E_BED_DAY"));  
				m.put("PRSN_ID", sqlRes.get(i).get("PRSN_ID"));   
				m.put("CASE_DRG_CODE", sqlRes.get(i).get("CASE_DRG_CODE"));                            
				m.put("TRAN_CODE", sqlRes.get(i).get("TRAN_CODE"));                            
				m.put("ICD_CM_1", sqlRes.get(i).get("ICD_CM_1"));
				m.put("ICD_CM_2", sqlRes.get(i).get("ICD_CM_2"));
				m.put("ICD_CM_3", sqlRes.get(i).get("ICD_CM_3"));
				m.put("ICD_CM_4", sqlRes.get(i).get("ICD_CM_4"));
				m.put("ICD_CM_5", sqlRes.get(i).get("ICD_CM_5"));
				m.put("ICD_CM_6", sqlRes.get(i).get("ICD_CM_6"));
				m.put("ICD_CM_7", sqlRes.get(i).get("ICD_CM_7"));
				m.put("ICD_CM_8", sqlRes.get(i).get("ICD_CM_8"));
				m.put("ICD_CM_9", sqlRes.get(i).get("ICD_CM_9"));
				m.put("ICD_CM_10", sqlRes.get(i).get("ICD_CM_10"));
				m.put("ICD_CM_11", sqlRes.get(i).get("ICD_CM_11"));
				m.put("ICD_CM_12", sqlRes.get(i).get("ICD_CM_12"));
				m.put("ICD_CM_13", sqlRes.get(i).get("ICD_CM_13"));
				m.put("ICD_CM_14", sqlRes.get(i).get("ICD_CM_14"));
				m.put("ICD_CM_15", sqlRes.get(i).get("ICD_CM_15"));
				m.put("ICD_CM_16", sqlRes.get(i).get("ICD_CM_16"));
				m.put("ICD_CM_17", sqlRes.get(i).get("ICD_CM_17"));
				m.put("ICD_CM_18", sqlRes.get(i).get("ICD_CM_18"));
				m.put("ICD_CM_19", sqlRes.get(i).get("ICD_CM_19"));
				m.put("ICD_CM_20", sqlRes.get(i).get("ICD_CM_20"));
				m.put("ICD_OP_CODE1", sqlRes.get(i).get("ICD_OP_CODE1"));
				m.put("ICD_OP_CODE2", sqlRes.get(i).get("ICD_OP_CODE2"));
				m.put("ICD_OP_CODE3", sqlRes.get(i).get("ICD_OP_CODE3"));
				m.put("ICD_OP_CODE4", sqlRes.get(i).get("ICD_OP_CODE4"));
				m.put("ICD_OP_CODE5", sqlRes.get(i).get("ICD_OP_CODE5"));
				m.put("ICD_OP_CODE6", sqlRes.get(i).get("ICD_OP_CODE6"));
				m.put("ICD_OP_CODE7", sqlRes.get(i).get("ICD_OP_CODE7"));
				m.put("ICD_OP_CODE8", sqlRes.get(i).get("ICD_OP_CODE8"));
				m.put("ICD_OP_CODE9", sqlRes.get(i).get("ICD_OP_CODE9"));
				m.put("ICD_OP_CODE10", sqlRes.get(i).get("ICD_OP_CODE10"));
				m.put("ICD_OP_CODE11", sqlRes.get(i).get("ICD_OP_CODE11"));
				m.put("ICD_OP_CODE12", sqlRes.get(i).get("ICD_OP_CODE12"));
				m.put("ICD_OP_CODE13", sqlRes.get(i).get("ICD_OP_CODE13"));
				m.put("ICD_OP_CODE14", sqlRes.get(i).get("ICD_OP_CODE14"));
				m.put("ICD_OP_CODE15", sqlRes.get(i).get("ICD_OP_CODE15"));
				m.put("ICD_OP_CODE16", sqlRes.get(i).get("ICD_OP_CODE16"));
				m.put("ICD_OP_CODE17", sqlRes.get(i).get("ICD_OP_CODE17"));
				m.put("ICD_OP_CODE18", sqlRes.get(i).get("ICD_OP_CODE18"));
				m.put("ICD_OP_CODE19", sqlRes.get(i).get("ICD_OP_CODE19"));
				m.put("ICD_OP_CODE20", sqlRes.get(i).get("ICD_OP_CODE20"));
				m.put("ORDER_QTY", sqlRes.get(i).get("ORDER_QTY"));
				m.put("DIAG_DOT", sqlRes.get(i).get("DIAG_DOT"));
				m.put("ROOM_DOT", sqlRes.get(i).get("ROOM_DOT"));
				m.put("MEAL_DOT", sqlRes.get(i).get("MEAL_DOT"));
				m.put("AMIN_DOT", sqlRes.get(i).get("AMIN_DOT"));
				m.put("RADO_DOT", sqlRes.get(i).get("RADO_DOT"));
				m.put("THRP_DOT", sqlRes.get(i).get("THRP_DOT"));
				m.put("SGRY_DOT", sqlRes.get(i).get("SGRY_DOT"));
				m.put("PHSC_DOT", sqlRes.get(i).get("PHSC_DOT"));
				m.put("BLOD_DOT", sqlRes.get(i).get("BLOD_DOT"));
				m.put("HD_DOT", sqlRes.get(i).get("HD_DOT"));
				m.put("ANE_DOT", sqlRes.get(i).get("ANE_DOT"));
				m.put("METR_DOT", sqlRes.get(i).get("METR_DOT"));
				m.put("DRUG_DOT", sqlRes.get(i).get("DRUG_DOT"));
				m.put("DSVC_DOT", sqlRes.get(i).get("DSVC_DOT"));
				m.put("NRTP_DOT", sqlRes.get(i).get("NRTP_DOT"));
				m.put("INJT_DOT", sqlRes.get(i).get("INJT_DOT"));
				m.put("BABY_DOT", sqlRes.get(i).get("BABY_DOT"));
				m.put("MED_DOT", sqlRes.get(i).get("MED_DOT"));
				m.put("PART_DOT", sqlRes.get(i).get("PART_DOT"));
				m.put("APPL_DOT", sqlRes.get(i).get("APPL_DOT"));
				m.put("EB_APPL30_DOT", sqlRes.get(i).get("EB_APPL30_DOT"));
				m.put("EB_PART30_DOT", sqlRes.get(i).get("EB_PART30_DOT"));
				m.put("EB_APPL60_DOT", sqlRes.get(i).get("EB_APPL60_DOT"));
				m.put("EB_PART60_DOT", sqlRes.get(i).get("EB_PART60_DOT"));
				m.put("EB_APPL61_DOT", sqlRes.get(i).get("EB_APPL61_DOT"));
				m.put("EB_PART61_DOT", sqlRes.get(i).get("EB_PART61_DOT"));
				m.put("SB_APPL30_DOT", sqlRes.get(i).get("SB_APPL30_DOT"));
				m.put("SB_PART30_DOT", sqlRes.get(i).get("SB_PART30_DOT"));
				m.put("SB_APPL90_DOT", sqlRes.get(i).get("SB_APPL90_DOT"));
				m.put("SB_PART90_DOT", sqlRes.get(i).get("SB_PART90_DOT"));
				m.put("SB_APPL180_DOT", sqlRes.get(i).get("SB_APPL180_DOT"));
				m.put("SB_PART180_DOT", sqlRes.get(i).get("SB_PART180_DOT"));
				m.put("SB_APPL181_DOT", sqlRes.get(i).get("SB_APPL181_DOT"));
				m.put("SB_PART181_DOT", sqlRes.get(i).get("SB_PART181_DOT"));
				m.put("NB_BIRTHDAY", sqlRes.get(i).get("NB_BIRTHDAY"));
				m.put("CHILD_MARK", sqlRes.get(i).get("CHILD_MARK"));
				m.put("TW_DRGS_SUIT_MARK", sqlRes.get(i).get("TW_DRGS_SUIT_MARK"));
				m.put("NAME", sqlRes.get(i).get("NAME"));
				m.put("AGENCY_ID", sqlRes.get(i).get("AGENCY_ID"));
				m.put("TRAN_IN_HOSP_ID", sqlRes.get(i).get("TRAN_IN_HOSP_ID"));
				m.put("TRAN_OUT_HOSP_ID", sqlRes.get(i).get("TRAN_OUT_HOSP_ID"));
				m.put("HOSP_ID", sqlRes.get(i).get("HOSP_ID"));
				m.put("SVC_PLAN", sqlRes.get(i).get("SVC_PLAN"));
				m.put("PILOT_PROJECT", sqlRes.get(i).get("PILOT_PROJECT"));
				m.put("NON_APPL_DOT", sqlRes.get(i).get("NON_APPL_DOT"));
				m.put("MR.ID", sqlRes.get(i).get("MRID"));                          
				
				result.add(m);
				m = new LinkedHashMap<String, Object>();
			}
		}
		else {
			        
			m.put("INH_MR", "");  
			m.put("INH_NO", "");                            
			m.put("SUBJECTIVE", "");                            
			m.put("OBJECTIVE", "");                            
			m.put("INH_OWN_EXP", "");                            
			m.put("CLINIC", "");                            
			m.put("CASE_TYPE", "");                            
			m.put("SEQ_NO", "");                            
			m.put("ROC_ID", "");  
			m.put("PART_NO", "");  
			m.put("APPL_CAUSE_MARK", "");   
			m.put("ID_BIRTH_YMD", "");                            
			m.put("PAY_TYPE", "");                            
			m.put("FUNC_TYPE", "");                            
			m.put("IN_DATE", "");  
			m.put("OUT_DATE", "");  
			m.put("APPL_START_DATE", "");  
			m.put("APPL_END_DATE", "");  
			m.put("E_BED_DAY", "");  
			m.put("S_BED_DAY", "");  
			m.put("PATIENT_SOURCE", "");  
			m.put("CARD_SEQ_NO", "");  
			m.put("TW_DRG_CODE", "");  
			m.put("TW_DRG_PAY_TYPE", "");  
			m.put("E_BED_DAY", "");  
			m.put("PRSN_ID", "");   
			m.put("CASE_DRG_CODE", "");                            
			m.put("TRAN_CODE", "");                            
			m.put("ICD_CM_1", "");
			m.put("ICD_CM_2", "");
			m.put("ICD_CM_3", "");
			m.put("ICD_CM_4", "");
			m.put("ICD_CM_5", "");
			m.put("ICD_CM_6", "");
			m.put("ICD_CM_7", "");
			m.put("ICD_CM_8", "");
			m.put("ICD_CM_9", "");
			m.put("ICD_CM_10", "");
			m.put("ICD_CM_11", "");
			m.put("ICD_CM_12", "");
			m.put("ICD_CM_13", "");
			m.put("ICD_CM_14", "");
			m.put("ICD_CM_15", "");
			m.put("ICD_CM_16", "");
			m.put("ICD_CM_17", "");
			m.put("ICD_CM_18", "");
			m.put("ICD_CM_19", "");
			m.put("ICD_CM_20", "");
			m.put("ICD_OP_CODE1", "");
			m.put("ICD_OP_CODE2", "");
			m.put("ICD_OP_CODE3", "");
			m.put("ICD_OP_CODE4", "");
			m.put("ICD_OP_CODE5", "");
			m.put("ICD_OP_CODE6", "");
			m.put("ICD_OP_CODE7", "");
			m.put("ICD_OP_CODE8", "");
			m.put("ICD_OP_CODE9", "");
			m.put("ICD_OP_CODE10", "");
			m.put("ICD_OP_CODE11", "");
			m.put("ICD_OP_CODE12", "");
			m.put("ICD_OP_CODE13", "");
			m.put("ICD_OP_CODE14", "");
			m.put("ICD_OP_CODE15", "");
			m.put("ICD_OP_CODE16", "");
			m.put("ICD_OP_CODE17", "");
			m.put("ICD_OP_CODE18", "");
			m.put("ICD_OP_CODE19", "");
			m.put("ICD_OP_CODE20", "");
			m.put("ORDER_QTY", "");
			m.put("DIAG_DOT", "");
			m.put("ROOM_DOT", "");
			m.put("MEAL_DOT", "");
			m.put("AMIN_DOT", "");
			m.put("RADO_DOT", "");
			m.put("THRP_DOT", "");
			m.put("SGRY_DOT", "");
			m.put("PHSC_DOT", "");
			m.put("BLOD_DOT", "");
			m.put("HD_DOT", "");
			m.put("ANE_DOT", "");
			m.put("METR_DOT", "");
			m.put("DRUG_DOT", "");
			m.put("DSVC_DOT", "");
			m.put("NRTP_DOT", "");
			m.put("INJT_DOT", "");
			m.put("BABY_DOT", "");
			m.put("MED_DOT", "");
			m.put("PART_DOT", "");
			m.put("APPL_DOT", "");
			m.put("EB_APPL30_DOT", "");
			m.put("EB_PART30_DOT", "");
			m.put("EB_APPL60_DOT", "");
			m.put("EB_PART60_DOT", "");
			m.put("EB_APPL61_DOT", "");
			m.put("EB_PART61_DOT", "");
			m.put("SB_APPL30_DOT", "");
			m.put("SB_PART30_DOT", "");
			m.put("SB_APPL90_DOT", "");
			m.put("SB_PART90_DOT", "");
			m.put("SB_APPL180_DOT", "");
			m.put("SB_PART180_DOT", "");
			m.put("SB_APPL181_DOT", "");
			m.put("SB_PART181_DOT", "");
			m.put("NB_BIRTHDAY", "");
			m.put("CHILD_MARK", "");
			m.put("TW_DRGS_SUIT_MARK", "");
			m.put("NAME", "");
			m.put("AGENCY_ID", "");
			m.put("TRAN_IN_HOSP_ID", "");
			m.put("TRAN_OUT_HOSP_ID", "");
			m.put("HOSP_ID", "");
			m.put("SVC_PLAN", "");
			m.put("PILOT_PROJECT", "");
			m.put("NON_APPL_DOT", "");
			m.put("MR.ID", "");
			
			
			result.add(m);
			m = new LinkedHashMap<String, Object>();
		}
		
		return result;
	}
	
	public List<LinkedHashMap<String, Object>> ippMapto(List<Map<String, Object>> sqlRes) {
		List<LinkedHashMap<String, Object>> result = new ArrayList<LinkedHashMap<String,Object>>();
		LinkedHashMap<String, Object> m  = new LinkedHashMap<String, Object>();
		if(sqlRes.size() > 0) {
			for(int i=0; i < sqlRes.size(); i++) {
				m.put("INH_NO", sqlRes.get(i).get("INH_NO"));                            
				m.put("INH_CODE", sqlRes.get(i).get("INH_CODE"));                            
				m.put("APPL_STATUS", sqlRes.get(i).get("APPL_STATUS"));                            
				m.put("PAY_STATUS", sqlRes.get(i).get("PAY_STATUS"));                            
				m.put("ORDER_SEQ_NO", sqlRes.get(i).get("ORDER_SEQ_NO"));                            
				m.put("ORDER_TYPE", sqlRes.get(i).get("ORDER_TYPE"));                            
				m.put("ORDER_CODE", sqlRes.get(i).get("ORDER_CODE"));                            
				m.put("PAY_RATE", sqlRes.get(i).get("PAY_RATE"));                            
				m.put("DRUG_USE", sqlRes.get(i).get("DRUG_USE"));                            
				m.put("DRUG_FRE", sqlRes.get(i).get("DRUG_FRE"));                            
				m.put("DRUG_PATH", sqlRes.get(i).get("DRUG_PATH"));
				m.put("CON_FUNC_TYPE", sqlRes.get(i).get("CON_FUNC_TYPE"));
				m.put("BED_NO", sqlRes.get(i).get("BED_NO"));
				m.put("CURE_PATH", sqlRes.get(i).get("CURE_PATH"));                            
				m.put("TW_DRGS_CALCU", sqlRes.get(i).get("TW_DRGS_CALCU"));
				m.put("PART_ACCO_DATA", sqlRes.get(i).get("PART_ACCO_DATA"));
				m.put("DONATER", sqlRes.get(i).get("DONATER"));
				m.put("START_TIME", sqlRes.get(i).get("START_TIME"));                            
				m.put("END_TIME", sqlRes.get(i).get("END_TIME"));    
				m.put("TOTAL_Q", sqlRes.get(i).get("TOTAL_Q"));
				m.put("UNIT_P", sqlRes.get(i).get("UNIT_P"));                            
				m.put("TOTAL_DOT", sqlRes.get(i).get("TOTAL_DOT"));                            
				m.put("PRE_NO", sqlRes.get(i).get("PRE_NO"));                            
				m.put("PRSN_ID", sqlRes.get(i).get("PRSN_ID"));                            
				m.put("IMG_SOURCE", sqlRes.get(i).get("IMG_SOURCE"));   
				m.put("FUNC_TYPE", sqlRes.get(i).get("FUNC_TYPE"));                            
				m.put("OWN_EXP_MTR_NO", sqlRes.get(i).get("OWN_EXP_MTR_NO"));                            
				m.put("NON_LIST_MARK", sqlRes.get(i).get("NON_LIST_MARK"));                            
				m.put("NON_LIST_NAME", sqlRes.get(i).get("NON_LIST_NAME"));                            
				m.put("COMM_HOSP_ID", sqlRes.get(i).get("COMM_HOSP_ID"));                            
				m.put("DRUG_SERIAL_NO", sqlRes.get(i).get("DRUG_SERIAL_NO"));
				m.put("MR.ID", sqlRes.get(i).get("MRID"));     
				
				result.add(m);
				m = new LinkedHashMap<String, Object>();
			}
		}
		else {
			m.put("INH_NO", "");                            
			m.put("INH_CODE", "");                            
			m.put("APPL_STATUS", "");                            
			m.put("PAY_STATUS", "");                            
			m.put("ORDER_SEQ_NO", "");                            
			m.put("ORDER_TYPE", "");                            
			m.put("ORDER_CODE", "");                            
			m.put("PAY_RATE", "");                            
			m.put("DRUG_USE", "");                            
			m.put("DRUG_FRE", "");                            
			m.put("DRUG_PATH","");
			m.put("CON_FUNC_TYPE", "");
			m.put("BED_NO", "");
			m.put("CURE_PATH", "");                            
			m.put("TW_DRGS_CALCU", "");
			m.put("PART_ACCO_DATA", "");
			m.put("DONATER", "");
			m.put("START_TIME", "");                            
			m.put("END_TIME", "");    
			m.put("TOTAL_Q", "");
			m.put("UNIT_P", "");                            
			m.put("TOTAL_DOT", "");                            
			m.put("PRE_NO", "");                            
			m.put("PRSN_ID","");                            
			m.put("IMG_SOURCE", "");   
			m.put("FUNC_TYPE", "");                            
			m.put("OWN_EXP_MTR_NO", "");                            
			m.put("NON_LIST_MARK", "");                            
			m.put("NON_LIST_NAME", "");                            
			m.put("COMM_HOSP_ID", "");                            
			m.put("DRUG_SERIAL_NO", "");
			m.put("MR.ID", "");
			
			result.add(m);
			m = new LinkedHashMap<String, Object>();
		}
		
		return result;
	}
	
	public List<LinkedHashMap<String, Object>> ipdsoMapto(List<Map<String, Object>> sqlRes) {
		List<LinkedHashMap<String, Object>> result = new ArrayList<LinkedHashMap<String,Object>>();
		LinkedHashMap<String, Object> m  = new LinkedHashMap<String, Object>();
		if(sqlRes.size() > 0) {
			for(int i=0; i < sqlRes.size(); i++) {
				m.put("INH_NO", sqlRes.get(i).get("INH_NO"));                            
				m.put("SUBJECT_TEXT", sqlRes.get(i).get("SUBJECT_TEXT"));                            
				m.put("OBJECT_TEXT", sqlRes.get(i).get("OBJECT_TEXT"));                            
		        m.put("DISCHARGE_TEXT", sqlRes.get(i).get("DISCHARGE_TEXT"));
		        m.put("MR.ID", sqlRes.get(i).get("MRID"));
		        
				result.add(m);
				m = new LinkedHashMap<String, Object>();
			}
		}
		else {
			m.put("INH_NO", "");                            
			m.put("SUBJECT_TEXT", "");                            
			m.put("OBJECT_TEXT", "");                            
	        m.put("DISCHARGE_TEXT", "");
	        m.put("MR.ID", "");
	        
	        result.add(m);
			m = new LinkedHashMap<String, Object>();
			
		}
		
		return result;
	}
	
	public List<LinkedHashMap<String, Object>> deductedMapto(List<Map<String, Object>> sqlRes) {
		List<LinkedHashMap<String, Object>> result = new ArrayList<LinkedHashMap<String,Object>>();
		LinkedHashMap<String, Object> m  = new LinkedHashMap<String, Object>();
		if(sqlRes.size() > 0) {
			for(int i=0; i < sqlRes.size(); i++) {
				m.put("INH_NO", sqlRes.get(i).get("INH_NO"));   
				m.put("CAT", sqlRes.get(i).get("CAT"));   
				m.put("ITEM", sqlRes.get(i).get("ITEM"));   
				m.put("CODE", sqlRes.get(i).get("CODE"));   
				m.put("DEDUCTED_ORDER", sqlRes.get(i).get("DEDUCTED_ORDER"));   
				m.put("DEDUCTED_QUANTITY", sqlRes.get(i).get("DEDUCTED_QUANTITY"));   
				m.put("DEDUCTED_AMOUNT", sqlRes.get(i).get("DEDUCTED_AMOUNT"));   
				m.put("REASON", sqlRes.get(i).get("REASON"));   
				m.put("NOTE", sqlRes.get(i).get("NOTE"));   
				m.put("ROLLBACK_M", sqlRes.get(i).get("ROLLBACK_M"));   
				m.put("ROLLBACK_Q", sqlRes.get(i).get("ROLLBACK_Q"));   
				m.put("AFR_QUANTITY", sqlRes.get(i).get("AFR_QUANTITY"));   
				m.put("AFR_AMOUNT", sqlRes.get(i).get("AFR_AMOUNT"));   
				m.put("AFR_PAY_QUANTITY", sqlRes.get(i).get("AFR_PAY_QUANTITY"));   
				m.put("AFR_PAY_AMOUNT", sqlRes.get(i).get("AFR_PAY_AMOUNT"));   
				m.put("AFR_NO_PAY_CODE", sqlRes.get(i).get("AFR_NO_PAY_CODE"));   
				m.put("AFR_NO_PAY_DESC", sqlRes.get(i).get("AFR_NO_PAY_DESC"));   
				m.put("AFR_NOTE", sqlRes.get(i).get("AFR_NOTE"));   
				m.put("DISPUTE_QUANTITY", sqlRes.get(i).get("DISPUTE_QUANTITY"));   
				m.put("DISPUTE_AMOUNT", sqlRes.get(i).get("DISPUTE_AMOUNT"));   
				m.put("DISPUTE_PAY_QUANTITY", sqlRes.get(i).get("DISPUTE_PAY_QUANTITY"));   
				m.put("DISPUTE_PAY_AMOUNT", sqlRes.get(i).get("DISPUTE_PAY_AMOUNT"));   
				m.put("DISPUTE_NO_PAY_CODE", sqlRes.get(i).get("DISPUTE_NO_PAY_CODE"));   
				m.put("DISPUTE_NO_PAY_DESC", sqlRes.get(i).get("DISPUTE_NO_PAY_DESC"));   
				m.put("DISPUTE_NOTE", sqlRes.get(i).get("DISPUTE_NOTE"));   
				m.put("MR.ID", sqlRes.get(i).get("MRID"));
				
				result.add(m);
				m = new LinkedHashMap<String, Object>();
			}
		}
		else {
			m.put("INH_NO", "");   
			m.put("CAT", "");   
			m.put("ITEM", "");   
			m.put("CODE", "");   
			m.put("DEDUCTED_ORDER", "");   
			m.put("DEDUCTED_QUANTITY", "");   
			m.put("DEDUCTED_AMOUNT", "");   
			m.put("REASON", "");   
			m.put("NOTE", "");   
			m.put("ROLLBACK_M", "");   
			m.put("ROLLBACK_Q", "");   
			m.put("AFR_QUANTITY", "");   
			m.put("AFR_AMOUNT", "");   
			m.put("AFR_PAY_QUANTITY", "");   
			m.put("AFR_PAY_AMOUNT", "");   
			m.put("AFR_NO_PAY_CODE", "");   
			m.put("AFR_NO_PAY_DESC", "");   
			m.put("AFR_NOTE", "");   
			m.put("DISPUTE_QUANTITY","");   
			m.put("DISPUTE_AMOUNT","");   
			m.put("DISPUTE_PAY_QUANTITY", "");   
			m.put("DISPUTE_PAY_AMOUNT", "");   
			m.put("DISPUTE_NO_PAY_CODE", "");   
			m.put("DISPUTE_NO_PAY_DESC", "");   
			m.put("DISPUTE_NOTE", "");   
			m.put("MR.ID", "");
			
			result.add(m);
			m = new LinkedHashMap<String, Object>();
		}
		
		return result;
	}
	
}
