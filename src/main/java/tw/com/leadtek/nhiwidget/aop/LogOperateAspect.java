package tw.com.leadtek.nhiwidget.aop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import tw.com.leadtek.nhiwidget.annotation.LogDefender;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;
import tw.com.leadtek.nhiwidget.service.LogOperateService;

@Aspect
@Component
@Order(10)
public class LogOperateAspect {

	private Logger logger = LogManager.getLogger();
	
	@Pointcut("@annotation(logDefender)")
	private void logDefenderPointcut(LogDefender logDefender) {}
	
	@Autowired
	private LogOperateService logOperateService;
	
	@SuppressWarnings("all")
	@Around("logDefenderPointcut(logDefender)")
	public Object logDefenderProcessor(ProceedingJoinPoint pjp, LogDefender logDefender) throws Throwable {
		
		Object result = null;
		
		String methodName = pjp.getSignature().getName();
		
		logger.debug("LogOperateAspect.methodName：" + methodName);
		
		result = pjp.proceed(pjp.getArgs());
		
		if(null != result && HttpStatus.OK.equals(((ResponseEntity<BaseResponse>)result).getStatusCode())) {
			
			logOperateService.handleLog(logDefender);
		}
		
		return result;
	}

}
