package tw.com.leadtek.nhiwidget.sql;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import tw.com.leadtek.nhiwidget.dao.DRG_CALDao;

@Repository
public class LogOperateDao extends BaseSqlDao{

  private Logger logger = LogManager.getLogger();

  @Autowired
  protected JdbcTemplate jdbcTemplate;
  
  @Autowired
  protected DRG_CALDao drgCalDao;


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
  
  public int addMedicalRecordStatus(String inhClinicId, Long userId, Integer status) {
	  String sql;
	  
	  if(null == inhClinicId ) {
		  
		  sql = "Insert into \r\n" + "LOG_MEDICAL_RECORD_STATUS(USER_ID, STATUS)\r\n" + "Values (%d, %s)";
		  sql = String.format(sql, userId, status);
	  }else {
		  
		  sql = "Insert into \r\n" + "LOG_MEDICAL_RECORD_STATUS(INH_CLINIC_ID, USER_ID, STATUS)\r\n" + "Values ('%s', %d, %s)";
		  sql = String.format(sql, inhClinicId, userId, status);
	  }
	  try {
		  int ret = jdbcTemplate.update(sql);
		  return ret;
	  } catch (DataAccessException ex) {
		  ex.printStackTrace();
		  return 0;
	  }
  }
  
  public int addMedicalRecordNotifyed(String inhClinicId, Long userId) {
	  String sql;
	  
	  if(null == inhClinicId ) {
		  
		  sql = "Insert into \r\n" + "LOG_MEDICAL_RECORD_NOTIFYED(USER_ID)\r\n" + "Values (%d)";
		  sql = String.format(sql, userId);
	  }else {
		  
		  sql = "Insert into \r\n" + "LOG_MEDICAL_RECORD_NOTIFYED(INH_CLINIC_ID, USER_ID)\r\n" + "Values ('%s', %d)";
		  sql = String.format(sql, inhClinicId, userId);
	  }
	  try {
		  int ret = jdbcTemplate.update(sql);
		  return ret;
	  } catch (DataAccessException ex) {
		  ex.printStackTrace();
		  return 0;
	  }
  }
  
  public int addMedicalRecordUnread(String inhClinicId, Long userId) {
	  String sql;
	  
	  if(null == inhClinicId ) {
		  
		  sql = "Insert into \r\n" + "LOG_MEDICAL_RECORD_UNREAD(USER_ID)\r\n" + "Values (%d)";
		  sql = String.format(sql, userId);
	  }else {
		  
		  sql = "Insert into \r\n" + "LOG_MEDICAL_RECORD_UNREAD(INH_CLINIC_ID, USER_ID)\r\n" + "Values ('%s', %d)";
		  sql = String.format(sql, inhClinicId, userId);
	  }
	  try {
		  int ret = jdbcTemplate.update(sql);
		  return ret;
	  } catch (DataAccessException ex) {
		  ex.printStackTrace();
		  return 0;
	  }
  }
  
  public int addLogAction(Long userId, String crud, String functionName, String pk) {
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

}
