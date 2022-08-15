package tw.com.leadtek.nhiwidget.aop;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.assertj.core.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import tw.com.leadtek.nhiwidget.annotation.LogDefender;
import tw.com.leadtek.nhiwidget.constant.LogType;
import tw.com.leadtek.nhiwidget.service.LogDataService;

@Aspect
@Component
@Order(10)
public class LogAspect {

	private Logger logger = LogManager.getLogger();
	
	@Pointcut("@annotation(logDefender)")
	private void logDefenderPointcut(LogDefender logDefender) {}
	
	@Autowired
	private LogDataService logDataService;
	
	@Around("logDefenderPointcut(logDefender)")
	public Object logDefenderProcessor(ProceedingJoinPoint pjp, LogDefender logDefender) throws Throwable {
		
		Object result = null;
		
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		
		String authorizationJwt = request.getHeader("Authorization");
		
		logger.debug("authorizationJwt：" + authorizationJwt);
		
		String methodName = pjp.getSignature().getName();
		
		logger.debug("前端呼叫：" + methodName);
		
		if(Arrays.asList(logDefender.value()).contains(LogType.ACTION)) {
			
		}
		
		if(Arrays.asList(logDefender.value()).contains(LogType.FORGOT_PASSWORD)) {
			//用 Mail去關聯 找出USER_ID
		}
		
		result = pjp.proceed(pjp.getArgs());
		
		if(Arrays.asList(logDefender.value()).contains(LogType.IMPORT)) {
			
		}
		
		if(Arrays.asList(logDefender.value()).contains(LogType.EXPORT)) {
			
		}
		
		if(Arrays.asList(logDefender.value()).contains(LogType.SIGNIN)) {
			
			if(StringUtils.isNotBlank(authorizationJwt)) {
				
				String jwt = authorizationJwt.replaceAll("Bearer ", "");
				
				logDataService.setLogout(jwt);
				
			}
		}
		
		return result;
	}
	
}
