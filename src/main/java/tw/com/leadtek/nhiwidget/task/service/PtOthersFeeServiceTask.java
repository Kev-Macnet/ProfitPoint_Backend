package tw.com.leadtek.nhiwidget.task.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.constant.INTELLIGENT_REASON;
import tw.com.leadtek.nhiwidget.dao.IP_PDao;
import tw.com.leadtek.nhiwidget.dao.MRDao;
import tw.com.leadtek.nhiwidget.dao.OP_PDao;
import tw.com.leadtek.nhiwidget.dto.PtOthersFeePl;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.service.IntelligentService;

@Service
public class PtOthersFeeServiceTask {
	@Autowired
	private MRDao mrDao;

	@Autowired
	private IP_PDao ippDao;
	@Autowired
	private OP_PDao oppDao;

	@Autowired
	private IntelligentService intelligentService;

	public void validOthersFee(PtOthersFeePl params) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		Date dateObj = calendar.getTime();
		String eDateStr = sdf.format(dateObj);
		String sDateStr = minusYear(eDateStr);

		/// 違反案件數
		List<MR> mrList = mrDao.getIntelligentMR(sDateStr, eDateStr);
		List<MR> mrList2 = new ArrayList<MR>();
		List<String> mrIdListStr = new ArrayList<String>();
		/// 判斷支付條件準則日期，如果病歷小於該日，則不顯示
		for (MR mr : mrList) {
			/// 起日
			Date sd = sdf.parse(sDateStr);
			Date mrSd = mr.getMrDate();
			/// 訖日
			Date ed = sdf.parse(eDateStr);
			Date mrEd = mr.getMrEndDate();

			if (sd.before(mrSd) || ed.equals(mrEd)) {
				mrList2.add(mr);
				mrIdListStr.add(mr.getId().toString());
			}
		}

		if (params.getHospitalized_type() == 0) {
			for (MR r : mrList2) {
				if (r.getDataFormat() != "20" && r.getCodeAll().contains(params.getNhi_no())) {
					intelligentService.insertIntelligent(r, INTELLIGENT_REASON.COST_DIFF.value(), params.getNhi_no(),
							String.format("(醫令代碼)%s不適用(住院)就醫方式", params.getNhi_no()), true);
				}
			}
		} else if (params.getOutpatient_type() == 0) {
			for (MR r : mrList2) {
				if (r.getDataFormat() != "10") {
					intelligentService.insertIntelligent(r, INTELLIGENT_REASON.COST_DIFF.value(), params.getNhi_no(),
							String.format("(醫令代碼)%s不適用(門診)就醫方式", params.getNhi_no()), true);
				}
			}
		}
		
		/// 1.
		/// 不可與  任一，並存單一就醫紀錄一併申報
		if(params.getExclude_nhi_no_enable() == 1) {
			List<String> nhiNoList = params.getLst_nhi_no();
			int count = 0;
			for (MR mr : mrList2) {
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
		///單一就醫紀錄應用數量,限定小於等於 次
		if(params.getMax_inpatient_enable() == 1) {
			List<Map<String, Object>> ippData = ippDao.getListByOrderCodeAndMrid(params.getNhi_no(), mrIdListStr);
			List<Map<String, Object>> oppData = oppDao.getListByDrugNoAndMrid2(params.getNhi_no(), mrIdListStr);
			if (ippData.size() > 0) {

				for (Map<String, Object> map : ippData) {
					float t = Float.parseFloat(map.get("TOTAL").toString());
					/// 如果資料大於限定值
					if (params.getMax_inpatient() < t) {
						MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(),
								params.getNhi_no(), String.format("(醫令代碼)%s與支付準則條件:單一就醫紀錄應用數量,限定小於等於%d次，疑似有出入",
										params.getNhi_no(), params.getMax_inpatient()),
								true);
					}
				}
			}
			if (oppData.size() > 0) {
				for (Map<String, Object> map : oppData) {
					float t = Float.parseFloat(map.get("TOTAL").toString());
					/// 如果資料大於限定值
					if (params.getMax_inpatient() < t) {
						MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(),
								params.getNhi_no(), String.format("(醫令代碼)%s與支付準則條件:單一就醫紀錄應用數量,限定小於等於%d次，疑似有出入",
										params.getNhi_no(), params.getMax_inpatient()),
								true);
					}
				}
			}
		}
		
		/// 3.
		///每組病歷號碼，每院限一年內，限定申報 次，如有需求另外提出
		if(params.getMax_inpatient_enable() == 1) {
			///取得最新病例
			List<Map<String, Object>> mrData = mrDao.getRocLastDayListByIdAndCode(params.getNhi_no());
			if (mrData.size() > 0) {
				for (Map<String, Object> map : mrData) {
					String endDate = map.get("MR_DATE").toString();
					String startDate = minusYear(endDate);
					Map<String, Object> mrData2 = mrDao.getRocCountListByCodeAndDate(params.getNhi_no(), map.get("ROC_ID").toString(),
							startDate, endDate);
					
					if(mrData2.size() >0) {
						float count = Float.valueOf(mrData2.get("COUNT").toString());
			          			
					    if(params.getMax_times() < count) {
					    	
					    	List<MR> mrModelList = mrDao.getAllByCodeAndRocid(params.getNhi_no(), map.get("ROC_ID").toString());
					    	MR mr = mrModelList.get(0);
					    	intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(),
									params.getNhi_no(),
									String.format("(醫令代碼)%s與支付準則條件:每組病歷號碼，每院限一年內，限定申報%d次，如有需求另外提出，疑似有出入",
											params.getNhi_no(), params.getMax_times()),
									true);
					    }
						
						
					}

				}
			}
		}
		
		/// 4.
		///限定同患者每次申報此支付標準代碼，與前一次申報日間隔大於等於 日，方可申報
		if(params.getInterval_nday_enable() == 1) {
			List<Map<String, Object>> oppData = oppDao.getRocIdCount(params.getNhi_no(), mrIdListStr);
			List<Map<String, Object>> oppList = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> oppList2 = new ArrayList<Map<String, Object>>();
			Map<String, Object> m1 = new HashMap<String, Object>();
			/// 門診
			if (oppData.size() > 0) {
				int count = 0;
				/// 先理出最後2筆資料
				for (Map<String, Object> map : oppData) {

					String rocid = map.get("ROC_ID").toString();
					String sDate = map.get("START_TIME").toString();
					String eDate = map.get("END_TIME").toString();
					String mrid = map.get("MR_ID").toString();

					if (count == 0) {
						m1.put("ROC_ID", rocid);
						m1.put("START_TIME", sDate);
						m1.put("END_TIME", eDate);
						m1.put("MR_ID", mrid);
						oppList.add(m1);
						m1 = new HashMap<String, Object>();
						count++;
					} else if (count == 1) {
						m1.put("ROC_ID", rocid);
						m1.put("START_TIME", sDate);
						m1.put("END_TIME", eDate);
						m1.put("MR_ID", mrid);
						oppList2.add(m1);
						count++;
					} else {
						if (rocid.equals(m1.get("ROC_ID"))) {
							continue;
						} else {
							m1 = new HashMap<String, Object>();
							count = 0;
						}
					}
				}
				/// 相同rocid做日期比對
				for (Map<String, Object> map : oppList) {
					String rocid = map.get("ROC_ID").toString();
					String eDate = map.get("END_TIME").toString();
					String mrid = map.get("MR_ID").toString();
					for (Map<String, Object> map2 : oppList2) {
						String rocid2 = map2.get("ROC_ID").toString();
						String eDate2 = map2.get("END_TIME").toString();
						if (rocid.equals(rocid2)) {
							float f = Float.valueOf(eDate);
							float f2 = Float.valueOf(eDate2);
							float diff = (f - f2) / 10000;
							if (params.getInterval_nday() < diff) {
								MR mr = mrDao.getMrByID(mrid);
								intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(),
										params.getNhi_no(),
										String.format("(醫令代碼)%s與支付準則條件:限定同患者前一次應用與當次應用待申報此支付標準代碼，每次申報間隔大於等於%d日，疑似有出入",
												params.getNhi_no(), params.getInterval_nday()),
										true);
							}

						}
					}
				}
			}

			List<Map<String, Object>> ippData = ippDao.getRocIdCount(params.getNhi_no(), mrIdListStr);
			List<Map<String, Object>> ippList = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> ippList2 = new ArrayList<Map<String, Object>>();
			m1 = new HashMap<String, Object>();
			/// 住院
			if (ippData.size() > 0) {
				int count = 0;
				/// 先理出最後2筆資料
				for (Map<String, Object> map : oppData) {

					String rocid = map.get("ROC_ID").toString();
					String sDate = map.get("START_TIME").toString();
					String eDate = map.get("END_TIME").toString();
					String mrid = map.get("MR_ID").toString();

					if (count == 0) {
						m1.put("ROC_ID", rocid);
						m1.put("START_TIME", sDate);
						m1.put("END_TIME", eDate);
						m1.put("MR_ID", mrid);
						ippList.add(m1);
						m1 = new HashMap<String, Object>();
						count++;
					} else if (count == 1) {
						m1.put("ROC_ID", rocid);
						m1.put("START_TIME", sDate);
						m1.put("END_TIME", eDate);
						m1.put("MR_ID", mrid);
						ippList2.add(m1);
						count++;
					} else {
						if (rocid.equals(m1.get("ROC_ID"))) {
							continue;
						} else {
							m1 = new HashMap<String, Object>();
							count = 0;
						}
					}
				}
				/// 相同rocid做日期比對
				for (Map<String, Object> map : ippList) {
					String rocid = map.get("ROC_ID").toString();
					String eDate = map.get("END_TIME").toString();
					String mrid = map.get("MR_ID").toString();
					for (Map<String, Object> map2 : ippList2) {
						String rocid2 = map2.get("ROC_ID").toString();
						String eDate2 = map2.get("END_TIME").toString();
						if (rocid.equals(rocid2)) {
							float f = Float.valueOf(eDate);
							float f2 = Float.valueOf(eDate2);
							float diff = (f - f2) / 10000;
							if (params.getInterval_nday() < diff) {
								MR mr = mrDao.getMrByID(mrid);
								intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(),
										params.getNhi_no(),
										String.format("(醫令代碼)%s與支付準則條件:限定同患者前一次應用與當次應用待申報此支付標準代碼，每次申報間隔大於等於%d日，疑似有出入",
												params.getNhi_no(), params.getInterval_nday()),
										true);
							}

						}
					}
				}
			}
		}
		
		/// 5.
		///限定同患者每次申報此支付標準代碼， 日內，限定申報小於等於 次
		if(params.getPatient_nday_enable() ==1) {
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
	 * 計算兩個時間相差多少分
	 * 
	 * @param start
	 * @param end
	 * @return
	 * @throws ParseException
	 */
	private long minBetween(Date start, Date end) throws ParseException {
		long diff = end.getTime() - start.getTime();

		TimeUnit time = TimeUnit.DAYS;
		long diffrence = time.convert(diff, TimeUnit.MILLISECONDS);
		long hour = diffrence * 24;
		long min = hour * 60;
		return min;
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
