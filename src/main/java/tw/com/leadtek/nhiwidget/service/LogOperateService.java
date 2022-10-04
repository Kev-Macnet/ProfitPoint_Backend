package tw.com.leadtek.nhiwidget.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
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
import tw.com.leadtek.nhiwidget.dto.LogExportDto;
import tw.com.leadtek.nhiwidget.dto.LogForgotPwdDto;
import tw.com.leadtek.nhiwidget.dto.LogImportDto;
import tw.com.leadtek.nhiwidget.dto.LogMrDto;
import tw.com.leadtek.nhiwidget.dto.LogSigninDto;
import tw.com.leadtek.nhiwidget.security.service.UserDetailsImpl;
import tw.com.leadtek.nhiwidget.sql.LogOperateDao;
import tw.com.leadtek.tools.DateTool;
import tw.com.leadtek.tools.ExcelUtil;
import tw.com.leadtek.tools.StringUtility;
import tw.com.leadtek.tools.ZipLib;

@Service
public class LogOperateService {
	
	private Logger logger = LogManager.getLogger();
	
	@Autowired
	private LogOperateDao logOperateDao;
	
	@Autowired
	private LogDataService logDataService;
	
	@Autowired
	protected HttpServletRequest httpServletReq;

	public Map<String, Object> query(String sdate         , String edate         , String showType  , 
			                         String actor         , String pCondition    , String pUserNames, 
			                         String pDisplayNames , String msCondition   , String msDepts   , 
			                         String msDisplayNames, String showLogTypes) {
		
		Map<String, Object> result = new HashMap<>();
		
		for(String logType : StringUtility.splitBySpace(showLogTypes)) {
			
			List<Object> pUserNames_     = Arrays.asList(StringUtility.splitBySpace(pUserNames));
			List<Object> pDisplayNames_  = Arrays.asList(StringUtility.splitBySpace(pDisplayNames));
			List<Object> msDepts_        = Arrays.asList(StringUtility.splitBySpace(msDepts));
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
			
			//資料匯入時間
			if("IP".equalsIgnoreCase(logType)) {
				
				dtoList = logOperateDao.queryImport(sdate, edate, showType, actor, pCondition, pUserNames_, pDisplayNames_, msCondition, msDepts_, msDisplayNames_);
			}
			
			//資料匯出筆數/時間
			if("EP".equalsIgnoreCase(logType)) {
				
				dtoList = logOperateDao.queryExport(sdate, edate, showType, actor, pCondition, pUserNames_, pDisplayNames_, msCondition, msDepts_, msDisplayNames_);
			}
			
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
	
	@SuppressWarnings("all")
	public void handleLog(LogDefender logDefender) {
		
		List<Object> logTypes = Arrays.asList(logDefender.value());
		
		if(logTypes.contains(LogType.FORGOT_PASSWORD)) {
			
			this.handleForgotPassword();
		}
		
		if(logTypes.contains(LogType.MEDICAL_RECORD_STATUS_CHANGE)) {
			
			this.handleMrStatusCheage();
		}
		
		if(logTypes.contains(LogType.MEDICAL_RECORD_NOTIFYED)) {
			
			Map<Long, String> mrMap = (Map<Long, String>)httpServletReq.getAttribute(LogType.MEDICAL_RECORD_NOTIFYED.name()+"_MR_MAP");
			List<Long> doctorIds    = (List<Long>)httpServletReq.getAttribute(LogType.MEDICAL_RECORD_NOTIFYED.name()+"_DOCTOR_IDS");
			Collection<String> inhClinicIds = mrMap.values();
			
			this.handleMrNotifyed(inhClinicIds, doctorIds);
			this.handleMrUnread  (mrMap, doctorIds);
		}
		
		if(logTypes.contains(LogType.MEDICAL_RECORD_READ)) {
			
			this.handleMrRead();
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
	
	public void handleMrNotifyed(Collection<String> inhClinicIds, List<Long> doctorIds) {
		
		inhClinicIds.stream().forEach(inhClinicId -> {
			
			doctorIds.stream().forEach(dortorId ->{
				
				this.createLogMedicalRecordNotifyed(inhClinicId, dortorId);
			});
			
		});
			
	}
	
	public void handleMrUnread(Map<Long, String> mrMap, List<Long> doctorIds) {
		
		mrMap.forEach((mrId, inhClinicId) -> {
			
			doctorIds.stream().forEach(dortorId ->{
				
				this.createLogMedicalRecordRead(inhClinicId, dortorId, mrId);
			});
			
		});
	}
	
	private void handleMrRead() {
		
		Long userId = (Long)httpServletReq.getAttribute(LogType.MEDICAL_RECORD_READ.name()+"_USER_ID");
		Long mrId   = (Long)httpServletReq.getAttribute(LogType.MEDICAL_RECORD_READ.name()+"_MR_ID"  );
		
		this.updateMedicalRecordRead(userId, mrId);
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
		
		if(null != count) {
			this.createLogExport(userId, count);
		}
	}
	
	private void hendleImport() {
		
		Long userId   = takeLoginUserInfo().getId();
		Integer count = (Integer)httpServletReq.getAttribute(LogType.IMPORT.name()+"_CNT");
		
		if(null != count) {
			this.createLogImport(userId, count);
		}
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
	
	public int createLogMedicalRecordRead(String inhClinicId, Long userId, Long mrId) {
		
		return logOperateDao.addMedicalRecordRead(inhClinicId, userId, mrId);
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
	
	private int updateMedicalRecordRead(Long userId, Long mrId) {
		
		return logOperateDao.updateMedicalRecordRead(userId, mrId);
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

	@SuppressWarnings("all")
	public ByteArrayInputStream exportCSV(Map<String, Object> map, String fileName, boolean showInhClinicId) throws IOException {
		
		String timeStamp = System.currentTimeMillis()+"";
		
		String tempFolder = com.google.common.io.Files.createTempDir().getAbsolutePath() + File.separator + timeStamp;
		
		try {
			
			final String outputZip = tempFolder + File.separator + "UserReport.zip";
			
			System.out.println("tempFolder " + tempFolder);
			
			Files.createDirectories(Paths.get(tempFolder));
			
			List<String> zipSrcPath = new ArrayList<>();
			
			for(String key : map.keySet()) {
				
				String filePath = null;
				
				List<LinkedHashMap<String, Object>> list = new ArrayList<>();
				
				if("SG".equalsIgnoreCase(key)) {
					
					filePath = tempFolder + File.separator + key + ".csv";
					
					if(CollectionUtils.isNotEmpty((Collection) map.get(key))) {
						
						list = this.toLogSigninMap((List<LogSigninDto>) map.get(key));
					}else {
						
						list = this.getEmptyLogSigninMap();
					}
				}
				
				if("FG".equalsIgnoreCase(key)) {
					
					filePath = tempFolder + File.separator + key + ".csv";
					
					if(CollectionUtils.isNotEmpty((Collection) map.get(key))) {
						
						list = this.toLogForgotPwdMap((List<LogForgotPwdDto>) map.get(key));
					}else {
						
						list = this.getEmptyLogForgotPwdMap();
					}
				}
				
				if("CW".equalsIgnoreCase(key)) {
					
					filePath = tempFolder + File.separator + key + ".csv";
					
					if(CollectionUtils.isNotEmpty((Collection) map.get(key))) {
						
						list = this.toLogMrMap((List<LogMrDto>) map.get(key), showInhClinicId);
					}else {
						
						list = this.getEmptyLogMrMap(showInhClinicId); 
					}
				}
				
				if("DM".equalsIgnoreCase(key)) {
					
					filePath = tempFolder + File.separator + key + ".csv";
					
					if(CollectionUtils.isNotEmpty((Collection) map.get(key))) {
						
						list = this.toLogMrMap((List<LogMrDto>) map.get(key), showInhClinicId);
					}else {
						
						list = this.getEmptyLogMrMap(showInhClinicId); 
					}
				}
				
				if("EC".equalsIgnoreCase(key)) {
					
					filePath = tempFolder + File.separator + key + ".csv";
					
					if(CollectionUtils.isNotEmpty((Collection) map.get(key))) {
						
						list = this.toLogMrMap((List<LogMrDto>) map.get(key), showInhClinicId);
					}else {
						
						list = this.getEmptyLogMrMap(showInhClinicId); 
					}
				}
				
				if("OF".equalsIgnoreCase(key)) {
					
					filePath = tempFolder + File.separator + key + ".csv";
					
					if(CollectionUtils.isNotEmpty((Collection) map.get(key))) {
						
						list = this.toLogMrMap((List<LogMrDto>) map.get(key), showInhClinicId);
					}else {
						
						list = this.getEmptyLogMrMap(showInhClinicId); 
					}
				}
				
				if("UR".equalsIgnoreCase(key)) {
					
					filePath = tempFolder + File.separator + key + ".csv";
					
					if(CollectionUtils.isNotEmpty((Collection) map.get(key))) {
						
						list = this.toLogMrMap((List<LogMrDto>) map.get(key), showInhClinicId);
					}else {
						
						list = this.getEmptyLogMrMap(showInhClinicId); 
					}
				}
				
				if("BN".equalsIgnoreCase(key)) {
					
					filePath = tempFolder + File.separator + key + ".csv";
					
					if(CollectionUtils.isNotEmpty((Collection) map.get(key))) {
						
						list = this.toLogMrMap((List<LogMrDto>) map.get(key), showInhClinicId);
					}else {
						
						list = this.getEmptyLogMrMap(showInhClinicId); 
					}
				}
				
				if("AC".equalsIgnoreCase(key)) {
					
					filePath = tempFolder + File.separator + key + ".csv";
					
					if(CollectionUtils.isNotEmpty((Collection) map.get(key))) {
						
						list = this.toLogActionMap((List<LogActionDto>) map.get(key));
					}else {
						
						list = this.getEmptyLogActionMap(); 
					}
				}
				
				if("IP".equalsIgnoreCase(key)) {
					
					filePath = tempFolder + File.separator + key + ".csv";
					
					if(CollectionUtils.isNotEmpty((Collection) map.get(key))) {
						
						list = this.toLogImportMap((List<LogImportDto>) map.get(key));
					}else {
						
						list = this.getEmptyLogImportMap();
					}
				}
				
				if("EP".equalsIgnoreCase(key)) {
					
					filePath = tempFolder + File.separator + key + ".csv";
					
					if(CollectionUtils.isNotEmpty((Collection) map.get(key))) {
						
						list = this.toLogExportMap((List<LogExportDto>) map.get(key));
					}else {
						
						list = this.getEmptyLogExportMap();
					}
				}
				
				if(null != filePath) {
					
					ExcelUtil.createCSV(list, filePath);
					
					zipSrcPath.add(filePath);
				}
			}
			
			ZipLib.zipFiles(outputZip, zipSrcPath);
			
			return new ByteArrayInputStream(FileUtils.readFileToByteArray(new File(outputZip)));
			
		}finally {
			
			FileUtils.deleteDirectory(new File(tempFolder));
		}
	}

	private List<LinkedHashMap<String, Object>> toLogSigninMap(List<LogSigninDto> list) {
		
		List<LinkedHashMap<String, Object>> result = new ArrayList<>();
		
		list.forEach(dto -> {
			
			LinkedHashMap<String, Object> map = new LinkedHashMap<>();  
			
			map.put("DISPLAY_NAME", dto.getDisplayName());
			map.put("USER_NAME"   , dto.getUsername());
			map.put("CREATE_DATE" , dto.getCreateDate());
			map.put("LOGIN_TIME"  , dto.getLoginTime());
			map.put("LOGOUT_TIME" , dto.getLogoutTime());
			map.put("ELAPSED_TIME", dto.getElapsedTime());
			
			result.add(map);
		});
		
		return result;
	}
	
	private List<LinkedHashMap<String, Object>> getEmptyLogSigninMap() {
		
		List<LinkedHashMap<String, Object>> result = new ArrayList<>();
		
		LinkedHashMap<String, Object> map = new LinkedHashMap<>(); 
		
		map.put("DISPLAY_NAME", "");
		map.put("USER_NAME"   , "");
		map.put("CREATE_DATE" , "");
		map.put("LOGIN_TIME"  , "");
		map.put("LOGOUT_TIME" , "");
		map.put("ELAPSED_TIME", "");
		
		result.add(map);
		
		return result;
	}
	
	private List<LinkedHashMap<String, Object>> toLogForgotPwdMap(List<LogForgotPwdDto> list) {
		
		List<LinkedHashMap<String, Object>> result = new ArrayList<>();
		
		list.forEach(dto -> {
			
			LinkedHashMap<String, Object> map = new LinkedHashMap<>();  
			
			map.put("DISPLAY_NAME"   , dto.getDisplayName());
			map.put("USER_NAME"      , dto.getUsername());
			map.put("CREATE_DATE"    , dto.getCreateDate());
			map.put("CREATE_TIME"    , dto.getCreateTime());
			map.put("CREATE_USER_AT" , dto.getCreateUserAt());
			map.put("CNT"            , dto.getCnt());
			map.put("ROLE"           , dto.getRole());
			map.put("STATUS"         , dto.getStatus());
			
			result.add(map);
		});
		
		return result;
		
	}
	
	private List<LinkedHashMap<String, Object>> getEmptyLogForgotPwdMap() {
		
		List<LinkedHashMap<String, Object>> result = new ArrayList<>();
		
		LinkedHashMap<String, Object> map = new LinkedHashMap<>();  
		
		map.put("DISPLAY_NAME"   , "");
		map.put("USER_NAME"      , "");
		map.put("CREATE_DATE"    , "");
		map.put("CREATE_TIME"    , "");
		map.put("CREATE_USER_AT" , "");
		map.put("CNT"            , "");
		map.put("ROLE"           , "");
		map.put("STATUS"         , "");
		
		result.add(map);
		
		return result;
		
	}
	
	private List<LinkedHashMap<String, Object>> toLogMrMap(List<LogMrDto> list, boolean showInhClinicId) {
		
		List<LinkedHashMap<String, Object>> result = new ArrayList<>();
		
		list.forEach(dto -> {
			
			LinkedHashMap<String, Object> map = new LinkedHashMap<>();  
			
			map.put("DISPLAY_NAME"   , dto.getDisplayName());
			map.put("USER_NAME"      , dto.getUsername());
			map.put("CREATE_DATE"    , dto.getCreateDate());
			
			if(showInhClinicId) {
				map.put("INH_CLINIC_IDS" , dto.getInhClinicIds());
			}
			
			map.put("CNT"            , dto.getCnt());
			
			result.add(map);
		});
		
		return result;
	}
	
	private List<LinkedHashMap<String, Object>> getEmptyLogMrMap(boolean showInhClinicId) {
		
		List<LinkedHashMap<String, Object>> result = new ArrayList<>();
		
		LinkedHashMap<String, Object> map = new LinkedHashMap<>();  
		
		map.put("DISPLAY_NAME"   , "");
		map.put("USER_NAME"      , "");
		map.put("CREATE_DATE"    , "");
		
		if(showInhClinicId) {
			map.put("INH_CLINIC_IDS" , "");
		}
		
		map.put("CNT"            , "");
		
		result.add(map);
		
		return result;
	}
	
	private List<LinkedHashMap<String, Object>> toLogActionMap(List<LogActionDto> list) {
		
		List<LinkedHashMap<String, Object>> result = new ArrayList<>();
		
		list.forEach(dto -> {
			
			LinkedHashMap<String, Object> map = new LinkedHashMap<>();  
			
			map.put("DISPLAY_NAME"   , dto.getDisplayName());
			map.put("USER_NAME"      , dto.getUsername());
			map.put("FUNCTION_NAME"  , dto.getFunctionName());
			map.put("CRUD"           , dto.getCrud());
			map.put("PK"            , dto.getPks());
			map.put("CREATE_DATE"    , dto.getCreateDate());
			
			result.add(map);
		});
		
		return result;
	}
	
	private List<LinkedHashMap<String, Object>> getEmptyLogActionMap() {
		
		List<LinkedHashMap<String, Object>> result = new ArrayList<>();
		
		LinkedHashMap<String, Object> map = new LinkedHashMap<>();  
		
		map.put("DISPLAY_NAME"   , "");
		map.put("USER_NAME"      , "");
		map.put("FUNCTION_NAME"  , "");
		map.put("CRUD"           , "");
		map.put("PK"             , "");
		map.put("CREATE_DATE"    , "");
		
		result.add(map);
		
		return result;
	}
	
	private List<LinkedHashMap<String, Object>> toLogImportMap(List<LogImportDto> list) {
		
		List<LinkedHashMap<String, Object>> result = new ArrayList<>();
		
		list.forEach(dto -> {
			
			LinkedHashMap<String, Object> map = new LinkedHashMap<>();  
			
			map.put("DISPLAY_NAME"   , dto.getDisplayName());
			map.put("USER_NAME"      , dto.getUsername());
			map.put("CREATE_DATE"    , dto.getCreateDate());
			map.put("CREATE_TIME"    , dto.getCreateTime());
			map.put("CNT"            , dto.getCnt());
			
			result.add(map);
		});
		
		return result;
	}
	
	private List<LinkedHashMap<String, Object>> getEmptyLogImportMap() {
		
		List<LinkedHashMap<String, Object>> result = new ArrayList<>();
		
		LinkedHashMap<String, Object> map = new LinkedHashMap<>();  
		
		map.put("DISPLAY_NAME"   , "");
		map.put("USER_NAME"      , "");
		map.put("CREATE_DATE"    , "");
		map.put("CREATE_TIME"    , "");
		map.put("CNT"            , "");
		
		result.add(map);
		
		return result;
	}
	
	private List<LinkedHashMap<String, Object>> toLogExportMap(List<LogExportDto> list) {
		
		List<LinkedHashMap<String, Object>> result = new ArrayList<>();
		
		list.forEach(dto -> {
			
			LinkedHashMap<String, Object> map = new LinkedHashMap<>();  
			
			map.put("DISPLAY_NAME"   , dto.getDisplayName());
			map.put("USER_NAME"      , dto.getUsername());
			map.put("CREATE_DATE"    , dto.getCreateDate());
			map.put("CREATE_TIME"    , dto.getCreateTime());
			map.put("CNT"            , dto.getCnt());
			
			result.add(map);
		});
		
		return result;
	}
	
	private List<LinkedHashMap<String, Object>> getEmptyLogExportMap() {
		
		List<LinkedHashMap<String, Object>> result = new ArrayList<>();
		
		LinkedHashMap<String, Object> map = new LinkedHashMap<>();  
		
		map.put("DISPLAY_NAME"   , "");
		map.put("USER_NAME"      , "");
		map.put("CREATE_DATE"    , "");
		map.put("CREATE_TIME"    , "");
		map.put("CNT"            , "");
		
		result.add(map);
		
		return result;
	}
}
