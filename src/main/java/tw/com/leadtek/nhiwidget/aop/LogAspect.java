package tw.com.leadtek.nhiwidget.aop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.assertj.core.util.Arrays;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

import tw.com.leadtek.nhiwidget.annotation.LogDefender;
import tw.com.leadtek.nhiwidget.constant.CRUD;
import tw.com.leadtek.nhiwidget.constant.LogType;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;
import tw.com.leadtek.nhiwidget.security.service.UserDetailsImpl;
import tw.com.leadtek.nhiwidget.service.LogDataService;
import tw.com.leadtek.nhiwidget.service.UserService;

@Aspect
@Component
@Order(10)
public class LogAspect {

	private Logger logger = LogManager.getLogger();
	
	@Pointcut("@annotation(logDefender)")
	private void logDefenderPointcut(LogDefender logDefender) {}
	
	@Autowired
	private LogDataService logDataService;
	
	@Autowired
	private UserService userService;
	
	@SuppressWarnings("all")
	@Around("logDefenderPointcut(logDefender)")
	public Object logDefenderProcessor(ProceedingJoinPoint pjp, LogDefender logDefender) throws Throwable {
		
		Object result = null;
		
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		
		String authorizationJwt = request.getHeader("Authorization");
		
		logger.debug("authorizationJwt：" + authorizationJwt);
		
		String methodName = pjp.getSignature().getName();
		
		logger.debug("前端呼叫：" + methodName);
		
		result = pjp.proceed(pjp.getArgs());
		
		List<Object> logTypes = Arrays.asList(logDefender.value());
		
		UserDetailsImpl loginUser = takeLoginUserInfo();
		
		if(HttpStatus.OK.equals(((ResponseEntity<BaseResponse>)result).getStatusCode())) {
			
			if(logTypes.contains(LogType.FORGOT_PASSWORD)) {
				
				Long userId = (Long)request.getAttribute(LogType.FORGOT_PASSWORD.name()+"_ID");
				
				logDataService.createLogForgotPassword(userId);
				
			}
			
			if(logTypes.contains(LogType.MEDICAL_RECORD_STATUS_CHANGE)) {
				
				Long inhClinicId = (Long)request.getAttribute(LogType.MEDICAL_RECORD_STATUS_CHANGE.name()+"_INH_CLINIC_ID");
				Long userId      = (Long)request.getAttribute(LogType.MEDICAL_RECORD_STATUS_CHANGE.name()+"_USER_ID");
				int status       = (Integer)request.getAttribute(LogType.MEDICAL_RECORD_STATUS_CHANGE.name()+"_STATUS");
				
				if(Arrays.asList(new int[]{-1, -2, 2, 3}).contains(status)) {

					logDataService.createLogMedicalRecordStatus(inhClinicId , userId, status);
				}
			}
			
			if(logTypes.contains(LogType.MEDICAL_RECORD_NOTIFYED)) {
				
				List<String> inhClinicIds = (List<String>)request.getAttribute(LogType.MEDICAL_RECORD_NOTIFYED.name()+"_INH_CLINIC_IDS");
				List<String> doctorIds    = (List<String>)request.getAttribute(LogType.MEDICAL_RECORD_NOTIFYED.name()+"_DOCTOR_IDS");
				
				inhClinicIds.stream().forEach(inhClinicId -> {
					
					doctorIds.stream().forEach(dortorId ->{
						
						logDataService.createLogMedicalRecordNotifyed(inhClinicId , dortorId);
					});
					
				});
			}
			
			
			if(logTypes.contains(LogType.ACTION_C) ||
			   logTypes.contains(LogType.ACTION_U) ||
			   logTypes.contains(LogType.ACTION_D)) {
				
				String functionName = logDefender.name();
				Long   userId       = loginUser.getId();
				List<Object> pks    = new ArrayList<>();
				
				if(logTypes.contains(LogType.ACTION_C)) {
					
					pks.addAll(((List<Object>) request.getAttribute(LogType.ACTION_C.name()+"_PKS")));
				}
				
				if(logTypes.contains(LogType.ACTION_U)) {
					
					pks.addAll(((List<Object>) request.getAttribute(LogType.ACTION_U.name()+"_PKS")));
					
				}
				
				if(logTypes.contains(LogType.ACTION_D)) {
					
					pks.addAll(((List<Object>) request.getAttribute(LogType.ACTION_D.name()+"_PKS")));
				}

				pks.stream().forEach(pk ->{
					
					logDataService.createLogAction(userId, CRUD.U.name(), functionName, String.valueOf(pks));
				});
				
			}
			
			if(logTypes.contains(LogType.IMPORT)) {
				
			}
			
			if(logTypes.contains(LogType.EXPORT)) {
				
			}
			
			if(logTypes.contains(LogType.SIGNIN)) {
				
				if(StringUtils.isNotBlank(authorizationJwt)) {
					
					String jwt = authorizationJwt.replaceAll("Bearer ", "");
					
					logDataService.setLogout(jwt);
					
				}
			}
		}
		
		
		return result;
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
			logger.error("LogAspect can't takeLoginUserInfo", e);
		}
		
		return result;
	}
}
