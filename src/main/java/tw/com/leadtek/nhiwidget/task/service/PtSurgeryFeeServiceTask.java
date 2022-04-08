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
import tw.com.leadtek.nhiwidget.dao.MRDao;
import tw.com.leadtek.nhiwidget.dao.OP_DDao;
import tw.com.leadtek.nhiwidget.dto.PtSurgeryFeePl;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.service.IntelligentService;
import tw.com.leadtek.tools.DateTool;

@Service
public class PtSurgeryFeeServiceTask {

	@Autowired
	private MRDao mrDao;

	@Autowired
	private IP_DDao ipdDao;
	@Autowired
	private OP_DDao opdDao;

	@Autowired
	private IntelligentService intelligentService;

	public void validSurgeryFee(PtSurgeryFeePl params) throws ParseException {
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
		
		/// 2.
		///患者限定年紀小於等於 歲，方可進行申報
		if(params.getLim_age_enable() == 1) {
			List<Map<String, Object>> ipData = ipdDao.getBirthByMrId(mrIdListStr);
			List<Map<String, Object>> opData = opdDao.getBirthByMrId(mrIdListStr);
			if (ipData.size() > 0) {
				for (Map<String, Object> map : ipData) {
					String rocBirth = map.get("ID_BIRTH_YMD").toString();
					Date d = DateTool.convertChineseToYear(rocBirth);
					SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
					Date currentDate = new Date();
					String rocStr = sdf2.format(d);
					String currentStr = sdf2.format(currentDate);
					int diffY = yearsBetween(rocStr, currentStr);
					if(params.getLim_age() > diffY) {
						MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(),
								params.getNhi_no(),
								String.format("(醫令代碼)%s與支付準則條件:患者限定年紀小於等於%d歲，方可進行申報，疑似有出入",
										params.getNhi_no(), params.getLim_age()),
								true);
					}
				}
			}
			if(opData.size() > 0) {
				for (Map<String, Object> map : opData) {
					String rocBirth = map.get("ID_BIRTH_YMD").toString();
					Date d = DateTool.convertChineseToYear(rocBirth);
					SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
					Date currentDate = new Date();
					String rocStr = sdf2.format(d);
					String currentStr = sdf2.format(currentDate);
					int diffY = yearsBetween(rocStr, currentStr);
					if(params.getLim_age() > diffY) {
						MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(),
								params.getNhi_no(),
								String.format("(醫令代碼)%s與支付準則條件:患者限定年紀小於等於%d歲，方可進行申報，疑似有出入",
										params.getNhi_no(), params.getLim_age()),
								true);
					}
				}
			} 
		}
		
		/// 3.
		/// 限定特定科別應用
		if (params.getLim_division_enable() == 1) {
			List<String> funList = params.getLst_division();
			List<String> funcAppend = new ArrayList<String>();

			for (String func : funList) {
				funcAppend.add(func);
			}


			List<MR> mrDataList = mrDao.getIntelligentMrByFuncName(sDateStr, eDateStr, funcAppend);
			/// 如果有非指定funcName資料
			if (mrDataList.size() > 0) {
				for (MR mr : mrDataList) {

					intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(), params.getNhi_no(),
							String.format("(醫令代碼)%s與支付準則條件:限定特定科%s別應用，疑似有出入", params.getNhi_no(), funcAppend), true);
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
