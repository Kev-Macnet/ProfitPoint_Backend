package tw.com.leadtek.nhiwidget.sql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import tw.com.leadtek.nhiwidget.dto.LogActionDto;
import tw.com.leadtek.nhiwidget.dto.LogExportDto;
import tw.com.leadtek.nhiwidget.dto.LogForgotPwdDto;
import tw.com.leadtek.nhiwidget.dto.LogImportDto;
import tw.com.leadtek.nhiwidget.dto.LogMrDto;
import tw.com.leadtek.nhiwidget.dto.LogSigninDto;

@Repository
public class LogOperateDao extends BaseSqlDao{

  private Logger logger = LogManager.getLogger();

  public List<LogSigninDto> querySignin(String sdate         , String edate      , String showType   , 
		                            String actor         , String pCondition , List<?> pUserNames,
		                            List<?> pDisplayNames, String msCondition, List<?> msDepts   , 
		                            List<?> msDisplayNames) {
	  
	  Map<String, Object> queryParaMap = new HashMap<>();
	  
	  String where = whereBy(sdate      , edate     , actor         , 
			                 pCondition , pUserNames, pDisplayNames , 
			                 msCondition, msDepts   , msDisplayNames, 
			                 queryParaMap);
	  String secondsBtw = "SECONDS_BETWEEN(TO_SECONDDATE(LOG.LOGIN_TM) , TO_SECONDDATE(LOG.LOGOUT_TM)) ";
	  String sql;
	 
	  sql = "SELECT DISTINCT                                              "
		  + "  U.DISPLAY_NAME                           AS \"displayName\""
		  + ", U.USERNAME                               AS \"username\"   ";
		  
	  if("R".equalsIgnoreCase(showType)) {
		  sql+= String.format(", SUM(%s)", secondsBtw);
	  }else {
		  sql+= ", TO_CHAR(LOG.CREATE_AT , 'YYYY-MM-DD') AS \"createDate\"   "
			  + ", TO_CHAR(TO_TIME(LOG.LOGIN_TM))        AS \"loginTime\"    "
			  + ", TO_CHAR(TO_TIME(LOG.LOGOUT_TM))       AS \"logoutTime\"   "
		      + ", "+secondsBtw;
	  }
	  
	  sql+= " AS \"secondsBetween\"                                       ";
	  
	  sql+= "FROM LOG_SIGNIN LOG                                          "
		  + joinUserDepartmentOnUserName()
		  + where
	      + "AND LOG.LOGOUT_TM IS NOT NULL                                ";
	  
	  if("R".equalsIgnoreCase(showType)) {
		  sql+= "GROUP BY U.DISPLAY_NAME, U.USERNAME";
	  }
	  return super.getNativeQueryResult(sql, LogSigninDto.class, queryParaMap);
  }
  
  public List<LogForgotPwdDto> queryForgotPwd(String sdate         , String edate      , String showType   , 
		                                      String actor         , String pCondition , List<?> pUserNames,
		                                      List<?> pDisplayNames, String msCondition, List<?> msDepts   , 
		                                      List<?> msDisplayNames) {
	  
	  Map<String, Object> queryParaMap = new HashMap<>();
	  
	  String where = whereBy(sdate      , edate     , actor         , 
			                 pCondition , pUserNames, pDisplayNames , 
			                 msCondition, msDepts   , msDisplayNames, 
			                 queryParaMap);
	  
	  String sql;
	  
	  sql = "SELECT DISTINCT                                              "
		  + "  U.DISPLAY_NAME                       AS \"displayName\"    "
		  + ", U.USERNAME                           AS \"username\"       ";
	  
	  if("D".equalsIgnoreCase(showType)) {
		  sql +=", TO_CHAR(LOG.CREATE_AT,'YYYY-MM-DD' ) AS \"createDate\"";
		  sql +=", TO_CHAR(TO_TIME(LOG.CREATE_AT))      AS \"createTime\"";
	  }
		    
	  sql +=", TO_CHAR(to_Date(U.CREATE_AT))     AS \"createUserAt\"   "
		  + ", COUNT(*)                             AS \"cnt\"            "
		  + ", CASE U.ROLE                                                "
		  + "     WHEN 'A' THEN 'MIS主管'                                  "
		  + "     WHEN 'B' THEN '行政主管'                                  "
		  + "     WHEN 'C' THEN '申報主管'                                  "
		  + "     WHEN 'D' THEN '申報人員'                                  "
		  + "     WHEN 'E' THEN '醫護人員'                                  "
		  + "     WHEN 'Z' THEN '原廠開發者'                                 "
		  + "   END                                 AS \"role\"          "
		  + ", CASE U.STATUS                                             "
		  + "     WHEN -1 THEN '忘記密碼'                                   "
		  + "     WHEN 1 THEN '有效'                                      "
		  + "     WHEN 0 THEN '無效'                                      "
		  + "   END                                 AS \"status\"        "
		  + "FROM LOG_FORGOT_PASSWORD LOG                                "
		  + joinUserDepartmentOnUserId()
		  + where
	      + "GROUP BY U.DISPLAY_NAME, U.USERNAME,  U.STATUS, U.ROLE,U.CREATE_AT";
	  
	  if("D".equalsIgnoreCase(showType)) {
		  sql +=", LOG.CREATE_AT";
	  }
	  
	  return super.getNativeQueryResult(sql, LogForgotPwdDto.class, queryParaMap);
  }
  
  
  public List<LogMrDto> queryStatus(String sdate          , String edate      , String showType   , 
		                            String actor          , String pCondition , List<?> pUserNames,
		                            List<?> pDisplayNames , String msCondition, List<?> msDepts   , 
		                            List<?> msDisplayNames, int status) {
	  
	  Map<String, Object> queryParaMap = new HashMap<>();
	  
	  String where = whereBy(sdate      , edate     , actor         , 
			                 pCondition , pUserNames, pDisplayNames , 
			                 msCondition, msDepts   , msDisplayNames, 
			                 queryParaMap);
	  
	  queryParaMap.put("status", status);
	  
	  String sql;
	  
	  sql = "SELECT                                                                              "
		  + "  U.DISPLAY_NAME                           AS \"displayName\"                       "
		  + ", U.USERNAME                               AS \"username\"                          "
		  + ", COUNT(*)                                 AS \"cnt\"                               "
		  + ", LOG.INH_CLINIC_ID                        AS \"inhClinicIds\"                      ";
	  
	  if("D".equalsIgnoreCase(showType)) {
		  sql +=", TO_CHAR(TO_DATE(LOG.CREATE_AT)) AS \"createDate\"                          ";
	  }
	  sql+= "FROM LOG_MEDICAL_RECORD_STATUS LOG                                                  "
			  + joinUserDepartmentOnUserIdOrCreateUserId()
			  + where
			  +" AND LOG.STATUS = :status                                                        "
			  + "GROUP BY U.DISPLAY_NAME, U.USERNAME, LOG.INH_CLINIC_ID                          ";
	  
	  if("D".equalsIgnoreCase(showType)) {
		  sql +=", TO_DATE(LOG.CREATE_AT) ";
	  }
	  
	  return super.getNativeQueryResult(sql, LogMrDto.class, queryParaMap);
  }
  
  public List<LogMrDto> queryDoubt(String sdate          , String edate      , String showType   , 
		                           String actor          , String pCondition , List<?> pUserNames,
		                           List<?> pDisplayNames , String msCondition, List<?> msDepts   , 
		                           List<?> msDisplayNames) {
	  
	  Map<String, Object> queryParaMap = new HashMap<>();
	  
	  String where = whereBy(null       , null      , actor         , 
			                 pCondition , pUserNames, pDisplayNames , 
			                 msCondition, msDepts   , msDisplayNames, 
			                 queryParaMap);
	  
	  String sql;
	  
	  sql = "SELECT                                                                              "
			  + "  U.DISPLAY_NAME                           AS \"displayName\"                   "
			  + ", U.USERNAME                               AS \"username\"                      "
			  + ", COUNT(*)                                 AS \"cnt\"                           "
			  + ", LOG.INH_CLINIC_ID                        AS \"inhClinicIds\"                  ";
	  
	  if("D".equalsIgnoreCase(showType)) {
		  sql +=", TO_CHAR(TO_DATE(LOG.CREATE_AT)) AS \"createDate\"                          ";
	  }
	  sql+= "FROM ( " + this.selectDistinctMrNotifyedSql(showType, sdate, edate, queryParaMap) +" ) LOG                                                                      "
			  + joinUserDepartmentOnCreateUserId()
			  + where
			  + "GROUP BY U.DISPLAY_NAME, U.USERNAME, LOG.INH_CLINIC_ID                          ";
	  
	  if("D".equalsIgnoreCase(showType)) {
		  sql +=", TO_DATE(LOG.CREATE_AT) ";
	  }
	  
	  return super.getNativeQueryResult(sql, LogMrDto.class, queryParaMap);
  }
  
  public List<LogMrDto> queryUnread(String sdate          , String edate      , String showType   , 
		                            String actor          , String pCondition , List<?> pUserNames,
		                            List<?> pDisplayNames , String msCondition, List<?> msDepts   , 
		                            List<?> msDisplayNames) {
	  
	  Map<String, Object> queryParaMap = new HashMap<>();
	  
	  String where = whereBy(sdate      , edate     , actor         , 
			                 pCondition , pUserNames, pDisplayNames , 
			                 msCondition, msDepts   , msDisplayNames, 
			                 queryParaMap);
	  
	  String sql;
	  
	  sql = "SELECT                                                                              "
			  + "  U.DISPLAY_NAME                           AS \"displayName\"                   "
			  + ", U.USERNAME                               AS \"username\"                      "
			  + ", COUNT(*)                                 AS \"cnt\"                           "
			  + ", LOG.INH_CLINIC_ID                        AS \"inhClinicIds\"                  ";
	  
	  if("D".equalsIgnoreCase(showType)) {
		  sql +=", TO_CHAR(TO_DATE(LOG.CREATE_AT)) AS \"createDate\"                          ";
	  }
	  sql+= "FROM LOG_MEDICAL_RECORD_READ LOG                                                    "
			  + joinUserDepartmentOnUserId()
			  + where
			  +" AND LOG.READ_NY = 'N'                                                           "
			  + "GROUP BY U.DISPLAY_NAME, U.USERNAME, LOG.INH_CLINIC_ID                          ";
	  
	  if("D".equalsIgnoreCase(showType)) {
		  sql +=", TO_DATE(LOG.CREATE_AT) ";
	  }
	  
	  return super.getNativeQueryResult(sql, LogMrDto.class, queryParaMap);
  }
  
  public List<LogMrDto> queryNotifyed(String sdate          , String edate      , String showType   , 
		                              String actor          , String pCondition , List<?> pUserNames,
		                              List<?> pDisplayNames , String msCondition, List<?> msDepts   , 
		                              List<?> msDisplayNames) {
	  
	  Map<String, Object> queryParaMap = new HashMap<>();
	  
	  String where = whereBy(sdate      , edate     , actor         , 
			                 pCondition , pUserNames, pDisplayNames , 
			                 msCondition, msDepts   , msDisplayNames, 
			                 queryParaMap);
	  
	  String sql;
	  
	  sql = "SELECT                                                                              "
			  + "  U.DISPLAY_NAME                           AS \"displayName\"                   "
			  + ", U.USERNAME                               AS \"username\"                      "
			  + ", COUNT(*)                                 AS \"cnt\"                           "
			  + ", LOG.INH_CLINIC_ID                        AS \"inhClinicIds\"                  ";
	  
	  if("D".equalsIgnoreCase(showType)) {
		  sql +=", TO_CHAR(TO_DATE(LOG.CREATE_AT)) AS \"createDate\"                          ";
	  }
	  sql+= "FROM LOG_MEDICAL_RECORD_NOTIFYED LOG                                                "
			  + joinUserDepartmentOnUserId()
			  + where
			  + "GROUP BY U.DISPLAY_NAME, U.USERNAME, LOG.INH_CLINIC_ID                          ";
	  
	  if("D".equalsIgnoreCase(showType)) {
		  sql +=", TO_DATE(LOG.CREATE_AT) ";
	  }
	  
	  return super.getNativeQueryResult(sql, LogMrDto.class, queryParaMap);
  }
  
  public List<LogActionDto> queryAction(String sdate          , String edate      , String showType   , 
		                                String actor          , String pCondition , List<?> pUserNames,
		                                List<?> pDisplayNames , String msCondition, List<?> msDepts   , 
		                                List<?> msDisplayNames) {
	  
	  Map<String, Object> queryParaMap = new HashMap<>();
	  
	  String where = whereBy(sdate      , edate     , actor         , 
			                 pCondition , pUserNames, pDisplayNames , 
			                 msCondition, msDepts   , msDisplayNames, 
			                 queryParaMap);
	  
	  String sql;
	  
	  sql = "SELECT                                                                              "
			  + "  U.DISPLAY_NAME                           AS \"displayName\"                   "
			  + ", U.USERNAME                               AS \"username\"                      "
			  + ", LOG.FUNCTION_NAME                        AS \"functionName\"                  "
			  + ", LOG.CRUD                                 AS \"crud\"                          "
			  + ", LOG.PK                                   AS \"pks\"                           ";
	  
	  if("D".equalsIgnoreCase(showType)) {
		  sql +=", TO_CHAR(LOG.CREATE_AT,'YYYY-MM-DD' ) AS \"createDate\"                     ";
	  }
	  
	  sql+= "FROM LOG_ACTION LOG                                                                 "
			  + joinUserDepartmentOnUserId()
			  + where;
	  
	  
	  return super.getNativeQueryResult(sql, LogActionDto.class, queryParaMap);
  }
  
  public List<LogImportDto> queryImport(String sdate          , String edate      , String showType   , 
		                                String actor          , String pCondition , List<?> pUserNames, 
		                                List<?> pDisplayNames , String msCondition, List<?> msDepts   ,
		                                List<?> msDisplayNames) {
	  
	  Map<String, Object> queryParaMap = new HashMap<>();
	  
	  String where = whereBy(sdate      , edate     , actor         , 
			                 pCondition , pUserNames, pDisplayNames , 
			                 msCondition, msDepts   , msDisplayNames, 
			                 queryParaMap);
	  
	  String sql;
	  
	  sql = "SELECT                                                                              "
			  + "  U.DISPLAY_NAME                           AS \"displayName\"                   "
			  + ", U.USERNAME                               AS \"username\"                      "
			  + ", COUNT(*)                                 AS \"cnt\"                           ";
	  
	  if("D".equalsIgnoreCase(showType)) {
		  sql +=", TO_CHAR(TO_DATE(LOG.CREATE_AT)) AS \"createDate\"                          ";
		  sql +=", TO_CHAR(TO_TIME(LOG.CREATE_AT)) AS \"createTime\"                          ";
	  }
	  sql+= "FROM LOG_IMPORT LOG                                                                 "
			  + joinUserDepartmentOnUserId()
			  + where
			  + "GROUP BY U.DISPLAY_NAME, U.USERNAME                                             ";
	  
	  if("D".equalsIgnoreCase(showType)) {
		  sql +=", LOG.CREATE_AT";
	  }
	  
	  return super.getNativeQueryResult(sql, LogImportDto.class, queryParaMap);
  }
  
  public List<LogExportDto> queryExport(String sdate              , String edate      , String showType        , 
		                                String actor              , String pCondition , List<Object> pUserNames, 
		                                List<Object> pDisplayNames, String msCondition, List<Object> msDepts   ,
			                            List<Object> msDisplayNames) {
	  
	  Map<String, Object> queryParaMap = new HashMap<>();
	  
	  String where = whereBy(sdate      , edate     , actor         , 
			                 pCondition , pUserNames, pDisplayNames , 
			                 msCondition, msDepts   , msDisplayNames, 
			                 queryParaMap);
	  
	  String sql;
	  
	  sql = "SELECT                                                                              "
			  + "  U.DISPLAY_NAME                           AS \"displayName\"                   "
			  + ", U.USERNAME                               AS \"username\"                      "
			  + ", COUNT(*)                                 AS \"cnt\"                           ";
	  
	  if("D".equalsIgnoreCase(showType)) {
		  sql +=", TO_CHAR(TO_DATE(LOG.CREATE_AT)) AS \"createDate\"                          ";
		  sql +=", TO_CHAR(TO_TIME(LOG.CREATE_AT)) AS \"createTime\"                          ";
	  }
	  sql+= "FROM LOG_EXPORT LOG                                                                 "
			  + joinUserDepartmentOnUserId()
			  + where
			  + "GROUP BY U.DISPLAY_NAME, U.USERNAME                                             ";
	  
	  if("D".equalsIgnoreCase(showType)) {
		  sql +=", LOG.CREATE_AT";
	  }
	  
	  return super.getNativeQueryResult(sql, LogExportDto.class, queryParaMap);
	}
  
  private String whereBy(String sdate          , String edate      ,  
                         String actor          , String pCondition , List<?> pUserNames,
                         List<?> pDisplayNames , String msCondition, List<?> msDepts   , 
                         List<?> msDisplayNames, Map<String, Object> queryParaMap) {
	  
	  StringBuilder result = new StringBuilder("WHERE 1=1 ");
	  
	  if(StringUtils.isNotBlank(sdate)) {
		  result.append("AND TO_DATE(LOG.CREATE_AT) >= :sdate ");
		  queryParaMap.put("sdate", sdate);
	  }
	  
	  if(StringUtils.isNotBlank(edate)) {
		  result.append("AND TO_DATE(LOG.CREATE_AT) <= :edate ");
		  queryParaMap.put("edate", edate);
	  }
	  
	  if("P".equalsIgnoreCase(actor)) {
		  //負責人
		  result.append("AND ROLE IN ('A', 'C', 'D') ");

		  if("UN".equalsIgnoreCase(pCondition)) {
			  result.append("AND U.USERNAME IN (:pUserNames) ");
			  queryParaMap.put("pUserNames", pUserNames);
		  }
		  
		  if("DN".equalsIgnoreCase(pCondition)) {
			  result.append("AND U.DISPLAY_NAME IN (:pDisplayNames) ");
			  queryParaMap.put("pDisplayNames", pDisplayNames);
		  }
		  
	  }else if("D".equalsIgnoreCase(actor)){
		  //醫護人員
		  result.append("AND ROLE IN ('B', 'E') ");
		  
		  if("D".equalsIgnoreCase(msCondition)) {
			  result.append("AND D.ID IN (:msDepts) ");
			  queryParaMap.put("msDepts", msDepts);
		  }
		  
		  if("DN".equalsIgnoreCase(msCondition)) {
			  result.append("AND U.DISPLAY_NAME IN (:msDisplayNames) ");
			  queryParaMap.put("msDisplayNames", msDisplayNames);
		  }
	  }
	  
	  return result.toString();
  }
  
  private String selectDistinctMrNotifyedSql(String showType, String sdate, String edate, Map<String, Object> queryParaMap) {
	  
	  StringBuilder result = new StringBuilder();
	  
	  result
	  .append(" SELECT DISTINCT CREATE_USER_ID, INH_CLINIC_ID ");
	  
	  if("D".equalsIgnoreCase(showType)) {
		  result.append(" , TO_DATE(CREATE_AT) AS CREATE_AT ");
	  }
	  
	  result.append(" FROM LOG_MEDICAL_RECORD_NOTIFYED ");
	  result.append(" WHERE 1=1  ");
		  
	  if(StringUtils.isNotBlank(sdate)) {
		  result.append("AND TO_DATE(CREATE_AT) >= :sdate ");
		  queryParaMap.put("sdate", sdate);
	  }

	  if(StringUtils.isNotBlank(edate)) {
		  result.append("AND TO_DATE(CREATE_AT) <= :edate ");
		  queryParaMap.put("edate", edate);
	  } 
	  
	  return result.toString();
  }
  
  private String joinUserDepartmentOnUserName() {
	  
	  StringBuilder result = new StringBuilder();
	  result
	  .append("INNER JOIN USER U ON LOG.USERNAME = U.USERNAME ")
	  .append("INNER JOIN USER_DEPARTMENT UD ON U.ID  = UD.USER_ID ")
	  .append("INNER JOIN DEPARTMENT D  ON UD.DEPARTMENT_ID  = D.ID ");
	  
	  return result.toString();
  }
  
  private String joinUserDepartmentOnUserId() {
	  
	  StringBuilder result = new StringBuilder();
	  result
	  .append("INNER JOIN USER U ON LOG.USER_ID = U.ID ")
	  .append("INNER JOIN USER_DEPARTMENT UD ON U.ID  = UD.USER_ID ")
	  .append("INNER JOIN DEPARTMENT D  ON UD.DEPARTMENT_ID  = D.ID ");
	  
	  return result.toString();
  }
  
  private String joinUserDepartmentOnCreateUserId() {
	  
	  StringBuilder result = new StringBuilder();
	  result
	  .append("INNER JOIN USER U ON LOG.CREATE_USER_ID = U.ID  ")
	  .append("INNER JOIN USER_DEPARTMENT UD ON U.ID  = UD.USER_ID ")
	  .append("INNER JOIN DEPARTMENT D  ON UD.DEPARTMENT_ID  = D.ID ");
	  
	  return result.toString();
  }
  
  private String joinUserDepartmentOnUserIdOrCreateUserId() {
	  
	  StringBuilder result = new StringBuilder();
	  result
	  .append("INNER JOIN USER U ON LOG.USER_ID = U.ID OR LOG.CREATE_USER_ID = U.ID ")
	  .append("INNER JOIN USER_DEPARTMENT UD ON U.ID  = UD.USER_ID ")
	  .append("INNER JOIN DEPARTMENT D  ON UD.DEPARTMENT_ID  = D.ID ");
	  
	  return result.toString();
  }
  
  public int addForgotPassword(Long userId) {
	  String sql;
	  sql = "Insert into \r\n" + "LOG_FORGOT_PASSWORD(USER_ID)\r\n" + "Values (%d)";
	  sql = String.format(sql, userId);
	  try {
		  int ret = jdbcTemplate.update(sql);
		  return ret;
	  } catch (DataAccessException ex) {
		  ex.printStackTrace();
		  return 0;
	  }
  }
  
  public int addMedicalRecordStatus(String inhClinicId, Long userId, Long loginUserId,Integer status) {
	  String sql;
	  
	  if(null == inhClinicId ) {
		  
		  sql = "Insert into \r\n" + "LOG_MEDICAL_RECORD_STATUS(USER_ID, CREATE_USER_ID, STATUS)\r\n" + "Values (%d, %d, '%s')";
		  sql = String.format(sql, userId, loginUserId, status);
	  }else {
		  
		  sql = "Insert into \r\n" + "LOG_MEDICAL_RECORD_STATUS(INH_CLINIC_ID, USER_ID, CREATE_USER_ID, STATUS)\r\n" + "Values ('%s', %d, %d, '%s')";
		  sql = String.format(sql, inhClinicId, userId, loginUserId, status);
	  }
	  try {
		  int ret = jdbcTemplate.update(sql);
		  return ret;
	  } catch (DataAccessException ex) {
		  ex.printStackTrace();
		  return 0;
	  }
  }
  
  public int addMedicalRecordNotifyed(String inhClinicId, Long userId, Long loginUserId) {
	  String sql;
	  
	  if(null == inhClinicId ) {
		  
		  sql = "Insert into \r\n" + "LOG_MEDICAL_RECORD_NOTIFYED(USER_ID, CREATE_USER_ID)\r\n" + "Values (%d, %d)";
		  sql = String.format(sql, userId, loginUserId);
	  }else {
		  
		  sql = "Insert into \r\n" + "LOG_MEDICAL_RECORD_NOTIFYED(INH_CLINIC_ID, USER_ID, CREATE_USER_ID)\r\n" + "Values ('%s', %d, %d)";
		  sql = String.format(sql, inhClinicId, userId, loginUserId);
	  }
	  try {
		  int ret = jdbcTemplate.update(sql);
		  return ret;
	  } catch (DataAccessException ex) {
		  ex.printStackTrace();
		  return 0;
	  }
  }
  
  public int addMedicalRecordRead(String inhClinicId, Long userId, Long mrId) {
	  String sql;
	  
	  if(null == inhClinicId ) {
		  
		  sql = "Insert into \r\n" + "LOG_MEDICAL_RECORD_READ(USER_ID, MR_ID)\r\n" + "Values (%d, %d)";
		  sql = String.format(sql, userId, mrId);
	  }else {
		  
		  sql = "Insert into \r\n" + "LOG_MEDICAL_RECORD_READ(INH_CLINIC_ID, USER_ID, MR_ID)\r\n" + "Values ('%s', %d, %d)";
		  sql = String.format(sql, inhClinicId, userId, mrId);
	  }
	  try {
		  int ret = jdbcTemplate.update(sql);
		  return ret;
	  } catch (DataAccessException ex) {
		  ex.printStackTrace();
		  return 0;
	  }
  }
  
  public int updateMedicalRecordRead(Long userId, Long mrId) {
	  String sql;
	  
      sql = "Update LOG_MEDICAL_RECORD_READ SET READ_NY='Y' WHERE USER_ID=%d AND MR_ID=%d";
      sql = String.format(sql, userId, mrId);
      
	  try {
		  int ret = jdbcTemplate.update(sql);
		  return ret;
	  } catch (DataAccessException ex) {
		  ex.printStackTrace();
		  return 0;
	  }
  }
  
  public int addAction(Long userId, String crud, String functionName, String pk) {
	  String sql;
	  sql = "Insert into \r\n" + "LOG_ACTION(USER_ID, CRUD, FUNCTION_NAME, PK)\r\n" + "Values (%d, '%s', '%s', '%s')";
	  sql = String.format(sql, userId, noInjection(crud), noInjection(functionName),noInjection(pk));
	  try {
		  int ret = jdbcTemplate.update(sql);
		  return ret;
	  } catch (DataAccessException ex) {
		  ex.printStackTrace();
		  return 0;
	  }
  }
  
  public int addImport(Long userId, Integer count) {
	  String sql;
	  
	  sql = "Insert into \r\n" + "LOG_IMPORT(USER_ID, CNT)\r\n" + "Values (%d, %d)";
	  sql = String.format(sql, userId, count);
	  
	  try {
		  int ret = jdbcTemplate.update(sql);
		  return ret;
	  } catch (DataAccessException ex) {
		  ex.printStackTrace();
		  return 0;
	  }
  }
  
  public int addExport(Long userId, Integer count) {
	  String sql;
	  
	  sql = "Insert into \r\n" + "LOG_EXPORT(USER_ID, CNT)\r\n" + "Values (%d, %d)";
	  sql = String.format(sql, userId, count);
	  
	  try {
		  int ret = jdbcTemplate.update(sql);
		  return ret;
	  } catch (DataAccessException ex) {
		  ex.printStackTrace();
		  return 0;
	  }
  }

}
