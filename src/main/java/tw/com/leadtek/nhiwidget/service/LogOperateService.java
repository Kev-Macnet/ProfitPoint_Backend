package tw.com.leadtek.nhiwidget.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.annotation.LogDefender;
import tw.com.leadtek.nhiwidget.constant.CRUD;
import tw.com.leadtek.nhiwidget.constant.LogType;
import tw.com.leadtek.nhiwidget.security.service.UserDetailsImpl;
import tw.com.leadtek.nhiwidget.sql.LogOperateDao;

@Service
public class LogOperateService {
	
	private Logger logger = LogManager.getLogger();
	
	@Autowired
	private LogOperateDao logOperate;
	
	@Autowired
	private LogDataService logDataService;
	
	@Autowired
	protected HttpServletRequest httpServletReq;
	
	public void handleLog(LogDefender logDefender) {
		
		List<Object> logTypes = Arrays.asList(logDefender.value());
		
		if(logTypes.contains(LogType.FORGOT_PASSWORD)) {
			
			this.handleForgotPassword();
		}
		
		if(logTypes.contains(LogType.MEDICAL_RECORD_STATUS_CHANGE)) {
			
			this.handleMrStatusCheage();
		}
		
		if(logTypes.contains(LogType.MEDICAL_RECORD_NOTIFYED)) {
			
			this.handleMrNotifyed();
		}
		
		if(logTypes.contains(LogType.ACTION_C) ||
		   logTypes.contains(LogType.ACTION_U) ||
		   logTypes.contains(LogType.ACTION_D)) {
			
			this.handleAction(logDefender);
		}
		
		if(logTypes.contains(LogType.SIGNIN)) {
			
			this.hendleSigin();
		}
		
		if(logTypes.contains(LogType.IMPORT)) {
			
		}
		
		if(logTypes.contains(LogType.EXPORT)) {
			
		}
	}
	
	private void hendleSigin() {
		
		String authorizationJwt = httpServletReq.getHeader("Authorization");
		
		if(StringUtils.isNotBlank(authorizationJwt)) {
			
			String jwt = authorizationJwt.replaceAll("Bearer ", "");
			
			logDataService.setLogout(jwt);
		}
		
	}

	public void handleForgotPassword() {
		
		Long userId = (Long)httpServletReq.getAttribute(LogType.FORGOT_PASSWORD.name()+"_ID");
		
		this.createLogForgotPassword(userId);
	}
	
	public void handleMrStatusCheage() {
		
		String inhClinicId = (String)httpServletReq.getAttribute(LogType.MEDICAL_RECORD_STATUS_CHANGE.name()+"_INH_CLINIC_ID");
		Long userId        = (Long)httpServletReq.getAttribute(LogType.MEDICAL_RECORD_STATUS_CHANGE.name()+"_USER_ID");
		int status         = (Integer)httpServletReq.getAttribute(LogType.MEDICAL_RECORD_STATUS_CHANGE.name()+"_STATUS");
		
		if(StringUtils.isNotBlank(inhClinicId)) {
			
			if(Arrays.asList(new int[]{-1, -2, 2, 3}).contains(status)) {
				
				this.createLogMedicalRecordStatus(inhClinicId , userId, status);
			}
			
		}
	}
	
	@SuppressWarnings("unchecked")
	public void handleMrNotifyed() {
		
		List<String> inhClinicIds = (List<String>)httpServletReq.getAttribute(LogType.MEDICAL_RECORD_NOTIFYED.name()+"_INH_CLINIC_IDS");
		List<Long> doctorIds      = (List<Long>)httpServletReq.getAttribute(LogType.MEDICAL_RECORD_NOTIFYED.name()+"_DOCTOR_IDS");
		
		inhClinicIds.stream().forEach(inhClinicId -> {
			
			doctorIds.stream().forEach(dortorId ->{
				
				this.createLogMedicalRecordNotifyed(inhClinicId , dortorId);
			});
			
		});
	}
	
	public void handleMrUnread(String inhClinicId, Long userId) {
		
		if(StringUtils.isNotBlank(inhClinicId)) {
			
			this.createLogMedicalRecordUnread(inhClinicId, userId);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void handleAction(LogDefender logDefender) {
		
		List<Object> logTypes = Arrays.asList(logDefender.value());
		String functionName = logDefender.name();
		Long   userId       = takeLoginUserInfo().getId();
		List<Object> pks    = new ArrayList<>();
		
		if(logTypes.contains(LogType.ACTION_C)) {
			
			pks = (List<Object>) httpServletReq.getAttribute(LogType.ACTION_C.name()+"_PKS");
		}else if(logTypes.contains(LogType.ACTION_U)) {
			
			pks = (List<Object>) httpServletReq.getAttribute(LogType.ACTION_U.name()+"_PKS");
		}else if(logTypes.contains(LogType.ACTION_D)) {
			
			pks = (List<Object>) httpServletReq.getAttribute(LogType.ACTION_D.name()+"_PKS");
		}
		
		pks.stream().forEach(pk ->{
			
			this.createLogAction(userId, CRUD.U.name(), functionName, String.valueOf(pk));
		});
	}
	
	public int createLogMedicalRecordStatus(String inhClinicId, Long userId, Integer status) {
		
		return logOperate.addMedicalRecordStatus(inhClinicId, userId, status);
	}
	
	public int createLogForgotPassword(Long userId) {
		
		return logOperate.addForgotPassword(userId);
	}
	
	public int createLogMedicalRecordNotifyed(String inhClinicId, Long userId) {
		
		return logOperate.addMedicalRecordNotifyed(inhClinicId, userId);
	}
	
	public int createLogMedicalRecordUnread(String inhClinicId, Long userId) {
		
		return logOperate.addMedicalRecordUnread(inhClinicId, userId);
	}
	
	public int createLogAction(Long userId, String crud, String functionName, String pk) {
		
		return logOperate.addLogAction(userId, crud, functionName, pk);
	}
	
	private UserDetailsImpl takeLoginUserInfo() {
		
		UserDetailsImpl result = null;
		
		try {
			Object obj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if (obj instanceof UserDetailsImpl) {
				
				result = (UserDetailsImpl) obj;
				
			} else {
				return null;
			}
			
		} catch (Exception e) {
			logger.error("LogOperateService can't takeLoginUserInfo", e);
		}
		
		return result;
	}
}
