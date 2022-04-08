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
import tw.com.leadtek.nhiwidget.dto.PtWardFeePl;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.service.IntelligentService;
import tw.com.leadtek.tools.DateTool;

@Service
public class PtWardFeeServiceTask {

	@Autowired
	private MRDao mrDao;

	@Autowired
	private IP_PDao ippDao;

	@Autowired
	private IntelligentService intelligentService;

	public void validInpatienFee(PtWardFeePl params) throws ParseException {

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
		/// 入住時間滿 小時，方可申報此支付標準代碼
		if (params.getMin_stay_enable() == 1) {
			List<Map<String, Object>> ippData = ippDao.getTimeListByMrid(mrIdListStr);
			for (Map<String, Object> map : ippData) {
				Date sDate = DateTool.convertChineseToYear(map.get("START_TIME").toString().substring(0,map.get("START_TIME").toString().lastIndexOf(".")));
				Date eDate = DateTool.convertChineseToYear(map.get("END_TIME").toString().substring(0,map.get("END_TIME").toString().lastIndexOf(".")));
				long diff = hourBetween(sDate, eDate);
				if (params.getMin_stay() > diff) {
					MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
					intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(), params.getNhi_no(),
							String.format("(醫令代碼)%s與支付準則條件:入住時間滿%d小時，方可申報此支付標準代碼，疑似有出入", params.getNhi_no(),
									params.getMin_stay()),
							true);
				}
			}
		}
		
		/// 2.
	    /// 入住時間超過 小時，方可申報此支付標準代碼
		if(params.getMax_stay_enable() == 1) {
			List<Map<String, Object>> ippData = ippDao.getTimeListByMrid(mrIdListStr);
			for (Map<String, Object> map : ippData) {
				Date sDate = DateTool.convertChineseToYear(map.get("START_TIME").toString().substring(0,map.get("START_TIME").toString().lastIndexOf(".")));
				Date eDate = DateTool.convertChineseToYear(map.get("END_TIME").toString().substring(0,map.get("END_TIME").toString().lastIndexOf(".")));
				long diff = hourBetween(sDate, eDate);
				if (params.getMax_stay() < diff) {
					MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
					intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(), params.getNhi_no(),
							String.format("(醫令代碼)%s與支付準則條件:入住時間滿%d小時，不方可申報此支付標準代碼，疑似有出入", params.getNhi_no(),
									params.getMax_stay()),
							true);
				}
			}
		}
		
		///3.
		/// 不可與 支付代碼任一，並存單一就醫紀錄一併申報
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
