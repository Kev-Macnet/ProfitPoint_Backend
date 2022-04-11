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
import tw.com.leadtek.nhiwidget.dto.PtAnesthesiaFeePl;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.service.IntelligentService;
@Service
public class PtAnesthesiaFeeServiceTask {

	@Autowired
	private MRDao mrDao;

	@Autowired
	private IP_PDao ippDao;
	@Autowired
	private OP_PDao oppDao;

	@Autowired
	private IntelligentService intelligentService;

	public void validAnesthesiaFee(PtAnesthesiaFeePl params) throws ParseException {
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
		/// 單一就醫紀錄上，須包含以下任一DRG代碼
		if (params.getInclude_drg_no_enable() == 1) {
			List<String> icdList = params.getLst_drg_no();
			int count = 0;
			for (MR mr : mrList2) {
				for (String s : icdList) {
					if (mr.getCodeAll().contains(params.getNhi_no()) && !mr.getDrgCode().contains(s)) {

						if (count == 0) {
							intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(),
									params.getNhi_no(), String.format("(醫令代碼)%s與支付準則條件:單一就醫紀錄上，須包含以下任一DRG代碼[%s]，疑似有出入",
											params.getNhi_no(), icdList.toString()),
									true);
						}
						count++;
					}
				}
				count = 0;
			}
		}

		/// 2.
		/// 需與任一，並存單一就醫紀錄一併申報時
		if (params.getCoexist_nhi_no_enable() == 1) {
			List<String> coList = params.getLst_co_nhi_no();
			int count = 0;
			for (MR mr : mrList2) {
				for (String s : coList) {
					/// 先判斷有相同支付標準
					if (mr.getCodeAll().contains(params.getNhi_no())) {
						/// 再判斷沒有符合
						if (!mr.getCodeAll().contains(s)) {
							if (count == 0) {

								intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(),
										params.getNhi_no(),
										String.format("(醫令代碼)%s與支付準則條件:需與[%s]任一，並存單一就醫紀錄一併申報時，疑似有出入",
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
		/// 單一就醫紀錄上，應用 大於等於 次時，首次執行須滿分鐘，方可進行下一次執行。後續每次執行需間隔超過 分鐘
		if (params.getOver_times_enable() == 1) {
			List<String> mridList = new ArrayList<String>();
			for(MR m:mrList2) {
				if(m.getCodeAll().contains(params.getNhi_no())) {
					
					mridList.add(m.getId().toString());
				}
			}
			List<Map<String, Object>> oppData = oppDao.getAllListByMrid(mrIdListStr);
			List<Map<String, Object>> ippData = ippDao.getAllListByMrid(mrIdListStr);

			if (oppData.size() > 0) {
				for (Map<String, Object> opp : oppData) {
					float t = Float.valueOf(opp.get("TOTAL_Q").toString());
					if (params.getOver_times_n() >= t) {

						if (opp.get("DIFF") != null) {

							float time = Float.valueOf(opp.get("DIFF").toString());
							if (params.getOver_times_first_n() <= time) {
								MR mr = mrDao.getMrByID(opp.get("MR_ID").toString());
								intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(),
										params.getNhi_no(),
										String.format(
												"(醫令代碼)%s與支付準則條件:單一就醫紀錄上，應用大於等於%d次時，首次執行須滿%d分鐘，方可進行下一次執行。後續每次執行需間隔超過%d分鐘",
												params.getNhi_no(), params.getOver_times_n(),
												params.getOver_times_first_n(), params.getOver_times_next_n()),
										true);
							}

						}
					}

				}
			}

			if (ippData.size() > 0) {
				for (Map<String, Object> ipp : ippData) {
					int t = Integer.parseInt(ipp.get("TOTAL_Q").toString());
					if (params.getOver_times_n() >= t) {

						if (ipp.get("DIFF") != null) {

							int time = Integer.parseInt(ipp.get("DIFF").toString());
							if (params.getOver_times_first_n() >= time) {
								MR mr = mrDao.getMrByID(ipp.get("MR_ID").toString());
								intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(),
										params.getNhi_no(),
										String.format(
												"(醫令代碼)%s與支付準則條件:單一就醫紀錄上，應用大於等於%d次時，首次執行須滿%d分鐘，方可進行下一次執行。後續每次執行需間隔超過%d分鐘",
												params.getNhi_no(), params.getOver_times_n(),
												params.getOver_times_first_n(), params.getOver_times_next_n()),
										true);
							}

						}
					}

				}
			}
		}

		/// 4.
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
