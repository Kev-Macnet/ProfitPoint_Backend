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
import tw.com.leadtek.nhiwidget.dao.IP_PDao;
import tw.com.leadtek.nhiwidget.dao.MRDao;
import tw.com.leadtek.nhiwidget.dao.OP_PDao;
import tw.com.leadtek.nhiwidget.dto.PtNutritionalFeePl;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.service.IntelligentService;

@Service
public class PtTubeFeedingFeeServiceTask {
	@Autowired
	private MRDao mrDao;

	@Autowired
	private IP_PDao ippDao;
	@Autowired
	private OP_PDao oppDao;

	@Autowired
	private IntelligentService intelligentService;
	
	public void validTubeFeedingFee(PtNutritionalFeePl params) throws ParseException {
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
		/// 單一住院就醫紀錄應用數量,限定小於等於 
		if(params.getMax_inpatient_enable() == 1) {
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
		
		/// 2.
		///單一就醫紀錄上，每日限定應用 小於等於
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
		
		/// 3.
		///單一就醫紀錄上，每 日內，限定應用小於等於 次
		if(params.getEvery_nday_enable() == 1) {
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
		
		/// 4.
		///單一就醫紀錄上，超過 日後，超出天數部份，限定應用 小於等於 次
		if(params.getOver_nday_enable() == 1) {
			List<Map<String, Object>> ippData = ippDao.getListOverByOrderCodeAndMridAndDays(params.getOver_nday_days(),params.getNhi_no(), mrIdListStr);
			if(ippData.size() > 0) {
				for (Map<String, Object> map : ippData) {
					float t = Float.parseFloat(map.get("TOTAL").toString());
					/// 如果資料大於限定值
					if (params.getOver_nday_times() < t) {
						MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(), params.getNhi_no(),
								String.format("(醫令代碼)%s與支付準則條件:單一就醫紀錄上，超過%d日後，超出天數部份，限定應用小於等於%d次，疑似有出入", params.getNhi_no(),
										params.getOver_nday_days(),params.getOver_nday_times()),
								true);
					}
				}
			}
		}
		
		/// 5.
		///不可與  任一，並存單一就醫紀錄一併申報
		if(params.getExclude_nhi_no_enable() ==1) {
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
