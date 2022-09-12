package tw.com.leadtek.nhiwidget.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
import tw.com.leadtek.nhiwidget.dto.LogActionDto;
import tw.com.leadtek.nhiwidget.dto.LogMrDto;
import tw.com.leadtek.nhiwidget.dto.LogSigninDto;
import tw.com.leadtek.nhiwidget.security.service.UserDetailsImpl;
import tw.com.leadtek.nhiwidget.sql.LogOperateDao;
import tw.com.leadtek.tools.DateTool;
import tw.com.leadtek.tools.StringUtility;

@Service
public class LogOperateService {
	
	private Logger logger = LogManager.getLogger();
	
	@Autowired
	private LogOperateDao logOperateDao;
	
	@Autowired
	private LogDataService logDataService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	protected HttpServletRequest httpServletReq;

	public Map<String, Object> query(String sdate         , String edate         , String showType  , 
			                         String actor         , String pCondition    , String pUserNames, 
			                         String pDisplayNames , String msCondition   , String msDepts   , 
			                         String msDisplayNames, String showLogTypes) {
		
		Map<String, Object> result = new HashMap<>();
		
		for(String logType : StringUtility.splitBySpace(showLogTypes)) {
			
			List<Object> pUserNames_ = Arrays.asList(StringUtility.splitBySpace(pUserNames));
			List<Object> pDisplayNames_ = Arrays.asList(StringUtility.splitBySpace(pDisplayNames));
			List<Object> msDepts_ = Arrays.asList(StringUtility.splitBySpace(msDepts));
			List<Object> msDisplayNames_ = Arrays.asList(StringUtility.splitBySpace(msDisplayNames));
			
			List<?> dtoList = new ArrayList<>();
			
			//登出入時間/系統登入總時數
			if("SG".equalsIgnoreCase(logType)) {

				List<LogSigninDto> list = logOperateDao.querySignin(sdate, edate, showType, actor, pCondition, pUserNames_, pDisplayNames_, msCondition, msDepts_, msDisplayNames_);
				
				calculateElapsedTime(list);
				
				dtoList = list;
			}
			
			//申請密碼清單/累計次數
			if("FG".equalsIgnoreCase(logType)) {
				
				dtoList = logOperateDao.queryForgotPwd(sdate, edate, showType, actor, pCondition, pUserNames_, pDisplayNames_, msCondition, msDepts_, msDisplayNames_);
			}
			
			//比對警示待確認案件數
			if("CW".equalsIgnoreCase(logType)) {
				
				List<LogMrDto> cwList = logOperateDao.queryStatus(sdate, edate, showType, actor, pCondition, pUserNames_, pDisplayNames_, msCondition, msDepts_, msDisplayNames_, -2);
				
				dtoList = extractMrDtoList(cwList, showType);
			}
			
			//疑問標示案件通知數/時間(現有疑問標示總案件數)
			if("DM".equalsIgnoreCase(logType)) {
				
				List<LogMrDto> dmList = logOperateDao.queryStatus(sdate, edate, showType, actor, pCondition, pUserNames_, pDisplayNames_, msCondition, msDepts_, msDisplayNames_, -1);
				
				dtoList = extractMrDtoList(dmList, showType);
			}
			
			//評估不調整案件數(評估不調整總案件數)
			if("EC".equalsIgnoreCase(logType)) {
				
				List<LogMrDto> ecList  = logOperateDao.queryStatus(sdate, edate, showType, actor, pCondition, pUserNames_, pDisplayNames_, msCondition, msDepts_, msDisplayNames_, 3);
				
				dtoList = extractMrDtoList(ecList, showType);
			}
			
			//優化完成案件數(疑問優化完成總案件數)
			if("OF".equalsIgnoreCase(logType)) {
				
				List<LogMrDto> ofList = logOperateDao.queryStatus(sdate, edate, showType, actor, pCondition, pUserNames_, pDisplayNames_, msCondition, msDepts_, msDisplayNames_, 2);
				
				dtoList = extractMrDtoList(ofList, showType);
			}
			
			//未讀取次數紀錄
			if("UR".equalsIgnoreCase(logType)) {
				
				List<LogMrDto> urList = logOperateDao.queryUnread(sdate, edate, showType, actor, pCondition, pUserNames_, pDisplayNames_, msCondition, msDepts_, msDisplayNames_);
				
				dtoList = extractMrDtoList(urList, showType);
			}
			
			//被通知次數紀錄
			if("BN".equalsIgnoreCase(logType)) {
				
				List<LogMrDto> bnList = logOperateDao.queryNotifyed(sdate, edate, showType, actor, pCondition, pUserNames_, pDisplayNames_, msCondition, msDepts_, msDisplayNames_);
				
				dtoList = extractMrDtoList(bnList, showType);
			}
			
			//使用者操作紀錄
			if("AC".equalsIgnoreCase(logType)) {
				
				List<LogActionDto> acList = logOperateDao.queryAction(sdate, edate, showType, actor, pCondition, pUserNames_, pDisplayNames_, msCondition, msDepts_, msDisplayNames_);
				
				dtoList = extractActionDtoList(acList, showType);
			}
			
//			System.out.println("XXXXXXXXXXXXXX");
//			
//			dtoList.forEach(System.out::println);
			
			result.put(logType, dtoList);
		}
		
		return result;
	}
	
	private List<LogMrDto> extractMrDtoList(List<LogMrDto> mrDtoList, String showType) {
		
		List<LogMrDto> result = mrDtoList.stream().
				filter(distinctByKey(dto-> mrDtoUniqueKey(dto, showType)))
				.collect(Collectors.toList());
		
		Map<String, Set<String>> inhClinicIdMap = mrDtoList.stream()
				.filter(f-> Objects.nonNull(f.getInhClinicIds()))
				.collect(Collectors.groupingBy(dto -> mrDtoUniqueKey(dto, showType), 
						Collectors.mapping(LogMrDto::getInhClinicIds, Collectors.toSet())));
		
		//當前CNT會包含NULL資料 
		Map<String, BigInteger> cntMap  = mrDtoList.stream()
				.collect(Collectors.groupingBy(dto -> mrDtoUniqueKey(dto, showType), 
						Collectors.mapping(LogMrDto::getCnt, 
								Collectors.reducing(BigInteger.ZERO, BigInteger::add))));
		
		for(LogMrDto dto : result) {
			
			String uniqueKey = mrDtoUniqueKey(dto, showType);
			
			if(inhClinicIdMap.containsKey(uniqueKey)) {
				//update inhClinicIds
				dto.setInhClinicIds(String.join("、", inhClinicIdMap.get(uniqueKey)));
			}
			
			if(cntMap.containsKey(uniqueKey)) {
				//update cnt
				dto.setCnt(cntMap.get(uniqueKey));
			}
		}
		
		return result;
	}
	
	private List<LogActionDto> extractActionDtoList(List<LogActionDto> actionDtoList, String showType) {
		
		List<LogActionDto> result = actionDtoList.stream().
				filter(distinctByKey(dto-> actionDtoUniqueKey(dto, showType)))
				.collect(Collectors.toList());
		
		Map<String, Set<String>> pkMap = actionDtoList.stream()
				.collect(Collectors.groupingBy(dto -> actionDtoUniqueKey(dto, showType), 
						Collectors.mapping(LogActionDto::getPks, Collectors.toSet())));
		
		for(LogActionDto dto : result) {
			
			String uniqueKey = actionDtoUniqueKey(dto, showType);
			
			if(pkMap.containsKey(uniqueKey)) {
				//update pks
				dto.setPks(String.join("、", pkMap.get(uniqueKey)));
			}
			
		}
		
		return result;
	}
	
	private String mrDtoUniqueKey(LogMrDto dto , String showType) {
		
		String result = dto.getUsername();
		
		if("D".equalsIgnoreCase(showType)) {
			
			result += "#"+ dto.getCreateDate();
		}
		
		return result;
	}
	
	private String actionDtoUniqueKey(LogActionDto dto , String showType) {
		
		String result = dto.getUsername() + "#" + dto.getFunctionName() + "#" + dto.getCrud();
		
		if("D".equalsIgnoreCase(showType)) {
			
			result += "#"+ dto.getCreateDate();
		}
		
		return result;
	}
	
	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
	    Set<Object> seen = ConcurrentHashMap.newKeySet();
	    return t -> seen.add(keyExtractor.apply(t));
	}
	
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
			
			this.hendleImport();
		}
		
		if(logTypes.contains(LogType.EXPORT)) {
			
			this.hendleExport();
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
		
		if(Arrays.asList(new int[]{-1, -2, 2, 3}).contains(status)) {
			
			this.createLogMedicalRecordStatus(inhClinicId , userId, status);
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
		
		this.createLogMedicalRecordUnread(inhClinicId, userId);
	}
	
	@SuppressWarnings("unchecked")
	public void handleAction(LogDefender logDefender) {
		
		List<Object> logTypes = Arrays.asList(logDefender.value());
		String functionName = logDefender.name();
		Long   userId       = takeLoginUserInfo().getId();
		List<Object> pks    = new ArrayList<>();
		
		CRUD crud = null;
		
		if(logTypes.contains(LogType.ACTION_C)) {
			
			crud = CRUD.C;

			pks = (List<Object>) httpServletReq.getAttribute(LogType.ACTION_C.name()+"_PKS");
			
		}else if(logTypes.contains(LogType.ACTION_U)) {
			
			crud = CRUD.U;
			
			pks = (List<Object>) httpServletReq.getAttribute(LogType.ACTION_U.name()+"_PKS");
			
		}else if(logTypes.contains(LogType.ACTION_D)) {
			
			crud = CRUD.D;
			
			pks = (List<Object>) httpServletReq.getAttribute(LogType.ACTION_D.name()+"_PKS");
		}
		
		for(Object pk : pks) {
			
			this.createLogAction(userId, crud.name(), functionName, String.valueOf(pk));
		}
		
	}
	
	private void hendleExport() {
		
		Long   userId = takeLoginUserInfo().getId();
		Integer count = (Integer)httpServletReq.getAttribute(LogType.EXPORT.name()+"_CNT");
		
		this.createLogExport(userId, count);
	}
	
	private void hendleImport() {
		
		Long userId   = takeLoginUserInfo().getId();
		Integer count = (Integer)httpServletReq.getAttribute(LogType.IMPORT.name()+"_CNT");
		
		this.createLogImport(userId, count);
	}
	
	public int createLogMedicalRecordStatus(String inhClinicId, Long userId, Integer status) {
		
		return logOperateDao.addMedicalRecordStatus(inhClinicId, userId, status);
	}
	
	public int createLogForgotPassword(Long userId) {
		
		return logOperateDao.addForgotPassword(userId);
	}
	
	public int createLogMedicalRecordNotifyed(String inhClinicId, Long userId) {
		
		return logOperateDao.addMedicalRecordNotifyed(inhClinicId, userId);
	}
	
	public int createLogMedicalRecordUnread(String inhClinicId, Long userId) {
		
		return logOperateDao.addMedicalRecordUnread(inhClinicId, userId);
	}
	
	public int createLogAction(Long userId, String crud, String functionName, String pk) {
		
		return logOperateDao.addAction(userId, crud, functionName, pk);
	}
	
	private int createLogExport(Long userId, Integer count) {
		
		return logOperateDao.addExport(userId, count);
	}
	
	private int createLogImport(Long userId, Integer count) {
		
		return logOperateDao.addImport(userId, count);
	}
	
	private void calculateElapsedTime(List<LogSigninDto> list) {
		
		list.forEach(obj ->{
			
			obj.setElapsedTime(DateTool.formatElapsedTime(obj.getSecondsBetween().longValue()));
		});
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
