package tw.com.leadtek.nhiwidget.task.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.constant.INTELLIGENT_REASON;
import tw.com.leadtek.nhiwidget.dao.IP_DDao;
import tw.com.leadtek.nhiwidget.dao.IP_PDao;
import tw.com.leadtek.nhiwidget.dao.MRDao;
import tw.com.leadtek.nhiwidget.dao.OP_DDao;
import tw.com.leadtek.nhiwidget.dao.OP_PDao;
import tw.com.leadtek.nhiwidget.dto.PtTreatmentFeePl;
import tw.com.leadtek.nhiwidget.model.rdb.IP_D;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.model.rdb.OP_D;
import tw.com.leadtek.nhiwidget.service.IntelligentService;
import tw.com.leadtek.tools.DateTool;

@Service
public class PtTreatmentFeeServiceTask {
	@Autowired
	private MRDao mrDao;

	@Autowired
	private IP_DDao ipdDao;
	@Autowired
	private OP_DDao opdDao;
	@Autowired
	private IP_PDao ippDao;
	@Autowired
	private OP_PDao oppDao;

	@Autowired
	private IntelligentService intelligentService;
	
	public void validTreatmentFee(PtTreatmentFeePl params) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		/// 將timestamp轉成date
		java.sql.Timestamp tSdate = new java.sql.Timestamp(params.getStart_date());
		java.sql.Timestamp tEdate = new java.sql.Timestamp(params.getEnd_date());
		Date tsd = new Date(tSdate.getTime());
		Date ted = new Date(tEdate.getTime());
		String sDateStr = sdf.format(tsd);
		String eDateStr = sdf.format(ted);

		/// 該支付準則區間病歷表
		List<MR> mrList = mrDao.getIntelligentMR(sDateStr, eDateStr, params.getNhi_no());
		/// 存放mrID
		List<String> mrIdListStr = new ArrayList<String>();
		/// 提取將該診斷碼之ID
		for (MR mr : mrList) {

			mrIdListStr.add(mr.getId().toString());
		}
		if (params.getHospitalized_type() == 0) {
			for (MR r : mrList) {
				if (r.getDataFormat() == "20") {
					intelligentService.insertIntelligent(r, INTELLIGENT_REASON.COST_DIFF.value(), params.getNhi_no(),
							String.format("(醫令代碼)%s不適用住院就醫方式", params.getNhi_no()), true);
				}
			}
		} else if (params.getOutpatient_type() == 0) {
			for (MR r : mrList) {
				if (r.getDataFormat() == "10") {
					intelligentService.insertIntelligent(r, INTELLIGENT_REASON.COST_DIFF.value(), params.getNhi_no(),
							String.format("(醫令代碼)%s不適用門診就醫方式", params.getNhi_no()), true);
				}
			}
		}
		
		/// 1.
		/// 不可與 任一，並存單一就醫紀錄一併申報
		if(params.getExclude_nhi_no_enable() == 1) {
			List<String> nhiNoList = params.getLst_nhi_no();
			int count = 0;
			for (MR mr : mrList) {
				for (String nhiNo : nhiNoList) {
					if (mr.getCodeAll().contains(nhiNo) && count == 0) {
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(),
								params.getNhi_no(),
								String.format("(醫令代碼)%s與支付準則條件:不可與%s(輸入支付標準代碼)%s任一，並存單一就醫紀錄一併申報，疑似有出入",
										params.getNhi_no(), nhiNoList.toString()),
								true);
						count++;
					} else if (mr.getCodeAll().contains(nhiNo) && count > 0) {
						continue;
					}
				}
			}
		}
		/// 2.
		/// 需與以下支付標準代碼任一並存，方可進行申報
		if (params.getCoexist_nhi_no_enable() == 1) {
			List<String> coList = params.getLst_co_nhi_no();
			int count = 0;
			for (MR mr : mrList) {
				for (String s : coList) {
					/// 先判斷有相同支付標準
					if (mr.getCodeAll().contains(params.getNhi_no())) {
						/// 再判斷沒有符合
						if (!mr.getCodeAll().contains(s)) {
							if (count == 0) {

								intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(),
										params.getNhi_no(), String.format("(醫令代碼)%s與支付準則條件:需與[%s]任一，並存單一就醫紀錄一併申報，疑似有出入",
												params.getNhi_no(), coList.toString()),
										true);
							}
							count++;
						}
					}
					count = 0;
				}
			}
		}
		
		/// 3.
		/// 單一就醫紀錄應用總數量,限定小於等於
		if (params.getMax_inpatient_enable() == 1) {
			List<Map<String, Object>> ippData = ippDao.getListByOrderCodeAndMrid(params.getNhi_no(), mrIdListStr);
			List<Map<String, Object>> oppData = oppDao.getListByDrugNoAndMrid2(params.getNhi_no(), mrIdListStr);
			if(ippData.size() > 0) {
				
				for (Map<String, Object> map : ippData) {
					float t = Float.parseFloat(map.get("TOTAL").toString());
					/// 如果資料大於限定值
					if (params.getMax_inpatient() < t) {
						MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(), params.getNhi_no(),
								String.format("(醫令代碼)%s與支付準則條件:單一就醫紀錄應用總數量,限定小於等於%d次，疑似有出入", params.getNhi_no(),
										params.getMax_inpatient()),
								true);
					}
				}
			}
			if(oppData.size() > 0) {
				for (Map<String, Object> map : oppData) {
					float t = Float.parseFloat(map.get("TOTAL").toString());
					/// 如果資料大於限定值
					if (params.getMax_inpatient() < t) {
						MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(), params.getNhi_no(),
								String.format("(醫令代碼)%s與支付準則條件:單一就醫紀錄應用總數量,限定小於等於%d次，疑似有出入", params.getNhi_no(),
										params.getMax_inpatient()),
								true);
					}
				}
			}
		}
		
		/// 4. 
		/// 單一就醫紀錄上，每日限定應用小於等於
		if(params.getMax_daily_enable() == 1) {
			List<Map<String, Object>> ippData = ippDao.getListOneDayByOrderCodeAndMrid(params.getNhi_no(), mrIdListStr);
			List<Map<String, Object>> oppData = oppDao.getListOneDayByDrugNoAndMrid(params.getNhi_no(), mrIdListStr);
			if(ippData.size() > 0) {
				for (Map<String, Object> map : ippData) {
					float t = Float.parseFloat(map.get("TOTAL").toString());
					/// 如果資料大於限定值
					if (params.getMax_daily() < t) {
						MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(), params.getNhi_no(),
								String.format("(醫令代碼)%s與支付準則條件:單一住院就醫紀錄應用數量,限定小於等於%d次，疑似有出入", params.getNhi_no(),
										params.getMax_daily()),
								true);
					}
				}
			}
			if(oppData.size() > 0) {
				for (Map<String, Object> map : oppData) {
					float t = Float.parseFloat(map.get("TOTAL").toString());
					/// 如果資料大於限定值
					if (params.getMax_daily() < t) {
						MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(), params.getNhi_no(),
								String.format("(醫令代碼)%s與支付準則條件:單一住院就醫紀錄應用數量,限定小於等於%d次，疑似有出入", params.getNhi_no(),
										params.getMax_daily()),
								true);
					}
				}
			}
		}
		
		/// 5.
		/// 單一就醫紀錄上，每 日內，限定應用小於等於 次
        if(params.getEvery_nday_enable() > 0) {
        	List<Map<String, Object>> ippData = ippDao.getListByOrderCodeAndMridAndDays(params.getEvery_nday_days(),params.getNhi_no(), mrIdListStr);
			List<Map<String, Object>> oppData = oppDao.getListByDrugNoAndMridAndDays(params.getEvery_nday_days(),params.getNhi_no(), mrIdListStr);
			if(ippData.size() > 0) {
				for (Map<String, Object> map : ippData) {
					float t = Float.parseFloat(map.get("TOTAL").toString());
					/// 如果資料大於限定值
					if (params.getEvery_nday_times() < t) {
						MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(), params.getNhi_no(),
								String.format("(醫令代碼)%s與支付準則條件:單一就醫紀錄上，每%d日內，限定應用小於等於%d次，疑似有出入", params.getNhi_no(),
										params.getEvery_nday_days(),params.getEvery_nday_times()),
								true);
					}
				}
			}
			if(oppData.size() > 0) {
				for (Map<String, Object> map : oppData) {
					float t = Float.parseFloat(map.get("TOTAL").toString());
					/// 如果資料大於限定值
					if (params.getEvery_nday_times() < t) {
						MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(), params.getNhi_no(),
								String.format("(醫令代碼)%s與支付準則條件:單一就醫紀錄上，每%d日內，限定應用小於等於%d次，疑似有出入", params.getNhi_no(),
										params.getEvery_nday_days(),params.getEvery_nday_times()),
								true);
					}
				}
			}
        }
        
        /// 6.
        /// 同患者限定每小於等於 日，總申報次數小於等於 次為原則
        if(params.getPatient_nday_enable() == 1) {
        	List<Map<String, Object>> ippData = ippDao.getListRocIdByOrderCodeAndMridAndDays(params.getPatient_nday_days(),params.getNhi_no(), mrIdListStr);
			List<Map<String, Object>> oppData = oppDao.getListRocIdByDrugNoAndMridAndDays(params.getPatient_nday_days(),params.getNhi_no(), mrIdListStr);
			if(ippData.size() > 0) {
				for (Map<String, Object> map : ippData) {
					float t = Float.parseFloat(map.get("TOTAL").toString());
					/// 如果資料大於限定值
					if (params.getPatient_nday_times() < t) {
						MR mr = mrDao.getMrByRocIdAndCode(map.get("ROC_ID").toString(), params.getNhi_no());
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(), params.getNhi_no(),
								String.format("(醫令代碼)%s與支付準則條件:同患者限定每小於等於%d日，總申報次數小於等於%d次為原則，疑似有出入", params.getNhi_no(),
										params.getPatient_nday_days(),params.getPatient_nday_times()),
								true);
					}
				}
			}
			if(oppData.size() > 0) {
				for (Map<String, Object> map : oppData) {
					float t = Float.parseFloat(map.get("TOTAL").toString());
					/// 如果資料大於限定值
					if (params.getPatient_nday_times() < t) {
						MR mr = mrDao.getMrByRocIdAndCode(map.get("ROC_ID").toString(), params.getNhi_no());
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(), params.getNhi_no(),
								String.format("(醫令代碼)%s與支付準則條件:同患者限定每小於等於%d日，總申報次數小於等於%d次為原則，疑似有出入", params.getNhi_no(),
										params.getPatient_nday_days(),params.getPatient_nday_times()),
								true);
					}
				}
			}
        }
        
        /// 7.
        /// 每組病歷號碼，每院限申報 次
        if(params.getMax_patient_enable() == 1) {
        	List<Map<String,Object>> mrDataList = mrDao.getRocListByIdAndCode(mrIdListStr, params.getNhi_no());
        	if(mrDataList.size() > 0) {
        		for(Map<String,Object> map : mrDataList) {
        			int c = Integer.parseInt(map.get("COUNT").toString());
        			
        			if(params.getMax_patient()<c) {
        				MR mr = mrDao.getMrByRocIdAndCode(map.get("ROC_ID").toString(), params.getNhi_no());
        				intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(), params.getNhi_no(),
								String.format("(醫令代碼)%s與支付準則條件:每組病歷號碼，每院限申報%d次，疑似有出入", params.getNhi_no(),
										params.getMax_patient()),
								true);
        			}
        		}
        	}
        }
        
        /// 8.
        /// 單一就醫紀錄上，須包含以下任一ICD診斷碼
        if(params.getInclude_icd_no_enable() == 1) {
        	List<String> icdList = params.getLst_icd_no();
        	int count = 0;
        	for(MR mr : mrList) {
        		for(String s : icdList) {
        			if (mr.getCodeAll().contains(params.getNhi_no()) && !mr.getIcdAll().contains(s)) {
        				
        				if(count == 0) {
    						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(), params.getNhi_no(),
    								String.format("(醫令代碼)%s與支付準則條件:單一就醫紀錄上，須包含以下任一ICD診斷碼[%s]，疑似有出入", params.getNhi_no(),
    										icdList.toString()),
    								true);
        				}
        				count++;
        			}
        		}
        		count = 0;
        	}
        }
        
        /// 9.
        /// 每月申報數量，不可超過門診就診人次之百分之
        if(params.getMax_month_enable() == 1) {
        	List<String> mrIdList = new ArrayList<String>();
        	for(MR mr : mrList) {
        		if(mr.getCodeAll().contains(params.getNhi_no())) {
        			mrIdList.add(mr.getId().toString());
        		}
         	}
        	///取該門診總數
        	Map<String,Object> oppData = oppDao.getTotalDrugByNo(params.getNhi_no());
        	if(oppData.get("TOTAL") != null) {
        		
        		int total = Integer.parseInt(oppData.get("TOTAL").toString());
        		///取每個月百分比
        		List<Map<String,Object>> oppList = oppDao.getPerMonthByDrugNoAndTotal(total, params.getNhi_no(), mrIdList);
        		if(oppList.size() > 0) {
        			for(Map<String,Object> map: oppList) {
        				float percent = Float.valueOf(map.get("PERCENT").toString());
        				if(params.getMax_month_percentage() < percent) {
        					
        					String msg = String.format("(醫令代碼)%s與支付準則條件:每月申報數量，不可超過門診就診人次之百分之%d，疑似有出入", params.getNhi_no(),
        							params.getMax_month_percentage());
        					System.out.println(msg);
//        				intelligentService.insertIntelligent(null, INTELLIGENT_REASON.COST_DIFF.value(), params.getNhi_no(),
//								String.format("(醫令代碼)%s與支付準則條件:每月申報數量，不可超過門診就診人次之百分之%d，疑似有出入", params.getNhi_no(),
//										params.getMax_month_percentage()),
//								true);
        				}
        			}
        		}
        	}
        }
        
        /// 10.
        /// 患者限定年紀小於等於 歲，方可進行申報
        if(params.getMax_age_enable() == 1) {
        	if(params.getOutpatient_type() == 1) {
        		List<OP_D> opData = opdDao.getDataListByMrId(mrIdListStr);
        		if(opData.size() > 0) {
    				for (OP_D model : opData) {
    					String rocBirth = "";
    					if(model.getNbBirthday() != null && !model.getNbBirthday().isEmpty()) {
    						rocBirth = model.getNbBirthday();
    					}
    					else {
    						rocBirth = model.getIdBirthYmd();
    					}
    					Date d = DateTool.convertChineseToYear(rocBirth);
    					SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
    					Date currentDate = new Date();
    					String rocStr = sdf2.format(d);
    					String currentStr = sdf2.format(currentDate);
    					int diffY = yearsBetween(rocStr, currentStr);
    					if(params.getMax_age() > diffY) {
    						MR mr = mrDao.getMrByID(model.getId().toString());
    						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(),
    								params.getNhi_no(),
    								String.format("(醫令代碼)%s與支付準則條件:患者限定年紀小於等於%d歲，方可進行申報，疑似有出入",
    										params.getNhi_no(), params.getMax_age()),
    								true);
    					}
    				}
    			} 
        	}
        	if(params.getHospitalized_type() == 1) {
        		List<IP_D> ipData = ipdDao.getDataListByMrId(mrIdListStr);
        		
        		if (ipData.size() > 0) {
        			for (IP_D model : ipData) {
        				String rocBirth = "";
        				if(model.getNbBirthday() != null && !model.getNbBirthday().isEmpty()) {
        					rocBirth = model.getNbBirthday();
        				}
        				else {
        					rocBirth = model.getIdBirthYmd();
        				}
        				Date d = DateTool.convertChineseToYear(rocBirth);
        				SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
        				Date currentDate = new Date();
        				String rocStr = sdf2.format(d);
        				String currentStr = sdf2.format(currentDate);
        				int diffY = yearsBetween(rocStr, currentStr);
        				if(params.getMax_age() > diffY) {
        					MR mr = mrDao.getMrByID(model.getId().toString());
        					intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(),
        							params.getNhi_no(),
        							String.format("(醫令代碼)%s與支付準則條件:患者限定年紀小於等於%d歲，方可進行申報，疑似有出入",
        									params.getNhi_no(), params.getMax_age()),
        							true);
        				}
        			}
        		}
        	}
			
        }
        
        /// 11.
        /// 限定特定科別應用
        if(params.getLim_division_enable() == 1) {
        	List<String> funList = params.getLst_division();
			List<String> funcAppend = new ArrayList<String>();

			for (String func : funList) {
				funcAppend.add(func);
			}

			List<MR> mrDataList = mrDao.getIntelligentMrByFuncName(mrIdListStr, funcAppend);
			/// 如果有非指定funcName資料
			if (mrDataList.size() > 0) {
				for (MR mr : mrDataList) {
					///如果門診
					if(params.getOutpatient_type() == 1 && mr.getDataFormat().equals("10")) {
						
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(), params.getNhi_no(),
								String.format("(醫令代碼)%s與支付準則條件:限定特定科%s別應用，疑似有出入", params.getNhi_no(), funcAppend), true);
					}
					///如果住院
					if(params.getHospitalized_type() == 1 && mr.getDataFormat().equals("20")) {
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(), params.getNhi_no(),
								String.format("(醫令代碼)%s與支付準則條件:限定特定科%s別應用，疑似有出入", params.getNhi_no(), funcAppend), true);

					}

				}
			}
        }
	}
	
	/**
	 * 帶入日期並減一年
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	private String minusYear(String date) throws ParseException {
		String result = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date d = sdf.parse(date);
		Calendar currentDate = Calendar.getInstance();
		currentDate.setTime(d);
		currentDate.add(Calendar.YEAR, -1);
		Date d2 = currentDate.getTime();
		result = sdf.format(d2);

		return result;
	}
	
	/**
	 * 計算兩個時間相差多少個年
	 * 
	 * @param start
	 * @param end
	 * @return
	 * @throws ParseException
	 */
	private int yearsBetween(String start, String end) throws ParseException {
		Calendar startDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		startDate.setTime(sdf.parse(start));
		endDate.setTime(sdf.parse(end));
		return (endDate.get(Calendar.YEAR) - startDate.get(Calendar.YEAR));
	}

	/**
	 * 計算兩個時間相差多少小時
	 * 
	 * @param start
	 * @param end
	 * @return
	 * @throws ParseException
	 */
	private long hourBetween(Date start, Date end) throws ParseException {
		long diff = end.getTime() - start.getTime();

        TimeUnit time = TimeUnit.DAYS; 
        long diffrence = time.convert(diff, TimeUnit.MILLISECONDS);
        long hour = diffrence * 24;
		return hour;
	}

	// Convert Date to Calendar
	private Calendar dateToCalendar(Date date) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;

	}

	// Convert Calendar to Date
	private Date calendarToDate(Calendar calendar) {
		return calendar.getTime();
	}
}
