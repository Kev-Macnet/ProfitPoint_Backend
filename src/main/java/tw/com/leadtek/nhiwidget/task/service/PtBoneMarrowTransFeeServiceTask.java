package tw.com.leadtek.nhiwidget.task.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.constant.INTELLIGENT_REASON;
import tw.com.leadtek.nhiwidget.dao.MRDao;
import tw.com.leadtek.nhiwidget.dto.PtBoneMarrowTransFeePl;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.service.IntelligentService;

@Service
public class PtBoneMarrowTransFeeServiceTask {
	@Autowired
	private MRDao mrDao;

	@Autowired
	private IntelligentService intelligentService;

	public void validBoneMarrowTransFee(PtBoneMarrowTransFeePl params) throws ParseException {
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
					intelligentService.insertIntelligent(r, INTELLIGENT_REASON.VIOLATE.value(), params.getNhi_no(),
							String.format("(醫令代碼)%s不適用住院就醫方式", params.getNhi_no()), true);
				}
			}
		} else if (params.getOutpatient_type() == 0) {
			for (MR r : mrList) {
				if (r.getDataFormat() == "10") {
					intelligentService.insertIntelligent(r, INTELLIGENT_REASON.VIOLATE.value(), params.getNhi_no(),
							String.format("(醫令代碼)%s不適用門診就醫方式", params.getNhi_no()), true);
				}
			}
		}
		
		/// 1.
		///需與 任一，並存單一就醫紀錄一併申報時 
        if(params.getCoexist_nhi_no_enable() == 1) {
        	List<String> coList = params.getLst_co_nhi_no();
			int count = 0;
			for (MR mr : mrList) {
				for (String s : coList) {
					/// 先判斷有相同支付標準
					if (mr.getCodeAll().contains(params.getNhi_no())) {
						/// 再判斷沒有符合
						if (!mr.getCodeAll().contains(s)) {
							if (count == 0) {

								intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
										params.getNhi_no(), String.format("(醫令代碼)%s與支付準則條件:需與[%s]任一，並存單一就醫紀錄一併申報時 ，疑似有出入",
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
        
        ///2.
        ///參與計畫之病患，不得申報
        if(params.getNot_allow_plan_enable() == 1) {
        	List<String> planList = params.getLst_allow_plan();
			for (MR mr : mrList) {
				if (mr.getCodeAll().contains(params.getNhi_no())) {
					intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), params.getNhi_no(),
							String.format("(醫令代碼)%s與支付準則條件:參與[%s]計畫之病患，不得申報，疑似有出入", params.getNhi_no(),
									planList.toString()),
							true);
				}
			}
        }
        
        ///3.
        ///限定特定科別應用
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
						
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), params.getNhi_no(),
								String.format("(醫令代碼)%s與支付準則條件:限定特定科%s別應用，疑似有出入", params.getNhi_no(), funcAppend), true);
					}
					///如果住院
					if(params.getHospitalized_type() == 1 && mr.getDataFormat().equals("20")) {
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), params.getNhi_no(),
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
	 * 計算兩個時間相差多少天
	 * 
	 * @param start
	 * @param end
	 * @return
	 * @throws ParseException
	 */
	private long dayBetween(Date start, Date end) throws ParseException {
		long diff = end.getTime() - start.getTime();

		TimeUnit time = TimeUnit.DAYS;
		long diffrence = time.convert(diff, TimeUnit.MILLISECONDS);

		return diffrence;
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
