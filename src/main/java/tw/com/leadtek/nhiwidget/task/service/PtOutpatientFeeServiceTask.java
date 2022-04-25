package tw.com.leadtek.nhiwidget.task.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.constant.INTELLIGENT_REASON;
import tw.com.leadtek.nhiwidget.dao.IP_DDao;
import tw.com.leadtek.nhiwidget.dao.MRDao;
import tw.com.leadtek.nhiwidget.dao.OP_DDao;
import tw.com.leadtek.nhiwidget.dao.PAY_CODEDao;
import tw.com.leadtek.nhiwidget.dto.PtOutpatientFeePl;
import tw.com.leadtek.nhiwidget.model.rdb.IP_D;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.model.rdb.OP_D;
import tw.com.leadtek.nhiwidget.model.rdb.PAY_CODE;
import tw.com.leadtek.nhiwidget.service.IntelligentService;
import tw.com.leadtek.tools.DateTool;

@Service
public class PtOutpatientFeeServiceTask {
	
  private Logger logger = LogManager.getLogger();
  
	@Autowired
	private MRDao mrDao;
	@Autowired
	private IP_DDao ipdDao;
	@Autowired
	private OP_DDao opdDao;
	@Autowired
	private PAY_CODEDao payCodeDao;

	@Autowired
	private IntelligentService intelligentService;

	private String Category = "門診診察費";

	public void vaidOutpatientFee(PtOutpatientFeePl params) throws ParseException {
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
				
				mrIdListStr.add(mr.getId().toString()) ;
			}
		}
		if (params.getHospitalized_type() == 0) {
			for (MR r : mrList2) {
				if (r.getDataFormat() != "20" && r.getCodeAll().contains(params.getNhi_no())) {
					intelligentService.insertIntelligent(r, INTELLIGENT_REASON.VIOLATE.value(), params.getNhi_no(),
							String.format("(醫令代碼)%s不適用(住院)就醫方式", params.getNhi_no()), true);
				}
			}
		} else if (params.getOutpatient_type() == 0) {
			for (MR r : mrList2) {
				if (r.getDataFormat() != "10") {
					intelligentService.insertIntelligent(r, INTELLIGENT_REASON.VIOLATE.value(), params.getNhi_no(),
							String.format("(醫令代碼)%s不適用(門診)就醫方式", params.getNhi_no()), true);
				}
			}
		}

	
		/// 1.
		/// 住院
		List<Map<String, Object>> hospitalListD = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> hospitalListC = new ArrayList<Map<String, Object>>();
		/// 門診
		List<Map<String, Object>> outpatientListD = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> outpatientListC = new ArrayList<Map<String, Object>>();

		if (params.getHospitalized_type() == 1 && params.getOutpatient_type() == 0) {

			if (params.getNo_dentisit() == 0) {
				hospitalListD = ipdDao.getValidByNoDentisit(mrIdListStr);

			}
			if (params.getNo_chi_medicine() == 0) {
				hospitalListC = ipdDao.getValidByNoChiMedicine(mrIdListStr);

			}
			if (hospitalListD.size() > 0) {

				for (Map<String, Object> map : hospitalListD) {
					MR mr = mrDao.getMrByID(map.get("mr_id").toString());
					intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), params.getNhi_no(),
							String.format("(醫令代碼)%s與支付準則條件:不含牙科(條件敘述)疑似有出入", params.getNhi_no()),
							true);

				}

			}
			if (hospitalListC.size() > 0) {

				for (Map<String, Object> map : hospitalListC) {
					MR mr = mrDao.getMrByID(map.get("mr_id").toString());
					intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), params.getNhi_no(),
							String.format("(醫令代碼)%s與支付準則條件:不含中醫(條件敘述)疑似有出入", params.getNhi_no()),
							true);

				}
			}
		} else if (params.getHospitalized_type() == 0 && params.getOutpatient_type() == 1) {

			if (params.getNo_dentisit() == 0) {
				outpatientListD = opdDao.getValidByNoDentisit(mrIdListStr);

			}
			if (params.getNo_chi_medicine() == 0) {
				outpatientListC = opdDao.getValidByNoChiMedicine(mrIdListStr);

			}
			if (outpatientListD.size() > 0) {

				for (Map<String, Object> map : hospitalListD) {
					MR mr = mrDao.getMrByID(map.get("mr_id").toString());
					intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), params.getNhi_no(),
							String.format("(醫令代碼)%s與支付準則條件:門診不含牙科(條件敘述)疑似有出入", params.getNhi_no()),
							true);

				}

			}
			if (outpatientListC.size() > 0) {

				for (Map<String, Object> map : hospitalListC) {
					MR mr = mrDao.getMrByID(map.get("mr_id").toString());
					intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), params.getNhi_no(),
							String.format("(醫令代碼)%s與支付準則條件:門診不含中醫(條件敘述)疑似有出入", params.getNhi_no()),
							true);

				}
			}
		}
		/// 2.
		/// 開立此醫令，處方交付特約藥局調劑或未開處方者，不得申報藥事服務費(調劑費)
		if (params.getNo_service_charge() == 1) {
			for (MR mr : mrList2) {
				if (mr.getCodeAll().contains(params.getNhi_no())) {

					PAY_CODE pc = payCodeDao.findDataByCode(params.getNhi_no());
					if (pc.getCodeType().equals("調劑費")) {
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
								params.getNhi_no(),
								String.format("(醫令代碼)%s與支付準則條件:開立此醫令，處方交付特約藥局調劑或未開處方者，不得申報藥事服務費(調劑費)疑似有出入",
										params.getNhi_no()),
								true);
					}
				}
			}

		}
		/// 3.
		/// 限定山地離島區域申報使用
		if (params.getLim_out_islands() == 1) {
			/// 如果門診
			if (params.getOutpatient_type() == 1) {

				List<Map<String, Object>> opData = opdDao.getPartNoByOutisLand();
				if (opData.size() > 0) {
					for (Map<String, Object> map : opData) {
						MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
								params.getNhi_no(),
								String.format("(醫令代碼)%s與支付準則條件:限定離島區域申報使用:單一就醫紀錄，部分負擔代號(PartNO)代號007，方可使用此醫令，疑似有出入",
										params.getNhi_no()),
								true);
					}
				}
			}
			/// 如果住院
			if (params.getHospitalized_type() == 1) {

				List<Map<String, Object>> ipData = ipdDao.getPartNoByOutisLand();
				if (ipData.size() > 0) {
					for (Map<String, Object> map : ipData) {
						MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
								params.getNhi_no(),
								String.format("(醫令代碼)%s與支付準則條件:限定離島區域申報使用:單一就醫紀錄，部分負擔代號(PartNO)代號007，方可使用此醫令，疑似有出入",
										params.getNhi_no()),
								true);
					}
				}
			}
		}
		/// 4.
		/// 限定假日加計使用
		if (params.getLim_holiday() == 1) {
            for(MR mr: mrList2) {
            	if(mr.getCodeAll().contains(params.getNhi_no())) {
            		
            		Date date = mr.getMrDate();
            		Calendar cal = dateToCalendar(date);
            		if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            			intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
								params.getNhi_no(),
								String.format("(醫令代碼)%s與支付準則條件:限定假日加計使用:六日或國定假日，方可使用此醫令，疑似有出入",
										params.getNhi_no()),
								true);
            		}
            	}
            	
            }
		}

		/// 5.
		/// 不可與___(輸入支付標準代碼)____任一，並存單一就醫紀錄一併申報
		if (params.getExclude_nhi_no_enable() == 1) {
			List<String> nhiNoList = params.getLst_nhi_no();
			int count = 0;
			for (MR mr : mrList2) {
				for (String nhiNo : nhiNoList) {
					if (mr.getCodeAll().contains(nhiNo) && count == 0) {
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
								params.getNhi_no(),
								String.format("(醫令代碼)%s與支付準則條件:不可與%s(輸入支付標準代碼)%s任一，並存單一就醫紀錄一併申報，疑似有出入",
										params.getNhi_no(), nhiNo),
								true);
						count++;
					} else if (mr.getCodeAll().contains(nhiNo) && count > 0) {
						continue;
					}
				}
			}

		}

		/// 6.
		/// 限定 未滿 /大於等於 /小於等於 ______歲病患開立
		if (params.getLim_age_enable() == 1) {
			String ageStr = "";
			if (params.getHospitalized_type() == 1) {
				List<Map<String, Object>> ipData = ipdDao.getBirthByMrId(mrIdListStr);
				if (ipData.size() > 0) {
					for (Map<String, Object> map : ipData) {
						String rocBirth = map.get("ID_BIRTH_YMD").toString();
						Date d = DateTool.convertChineseToYear(rocBirth);
						SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
						Date currentDate = new Date();
						String rocStr = sdf2.format(d);
						String currentStr = sdf2.format(currentDate);
						int diffY = yearsBetween(rocStr, currentStr);

						switch (params.getLim_age_type()) {
						case 1:
							ageStr = "未滿";
							if (params.getLim_age() < diffY) {
								MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
								intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
										params.getNhi_no(),
										String.format("(醫令代碼)%s與支付準則條件:限定" + ageStr + "%d歲病患開立，疑似有出入",
												params.getNhi_no(), params.getLim_age()),
										true);
							}
							break;
						case 2:
							ageStr = "大於等於";
							if (params.getLim_age() >= diffY) {
								MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
								intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
										params.getNhi_no(),
										String.format("(醫令代碼)%s與支付準則條件:限定" + ageStr + "%d歲病患開立，疑似有出入",
												params.getNhi_no(), params.getLim_age()),
										true);
							}
							break;
						case 3:
							ageStr = "小於等於";
							if (params.getLim_age() <= diffY) {
								MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
								intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
										params.getNhi_no(),
										String.format("(醫令代碼)%s與支付準則條件:限定" + ageStr + "%d歲病患開立，疑似有出入",
												params.getNhi_no(), params.getLim_age()),
										true);
							}
							break;
						}
					}
				}
			}
			if (params.getOutpatient_type() == 1) {
				List<Map<String, Object>> opData = opdDao.getBirthByMrId(mrIdListStr);
				if (opData.size() > 0) {
					for (Map<String, Object> map : opData) {
						String rocBirth = map.get("ID_BIRTH_YMD").toString();
						Date d = DateTool.convertChineseToYear(rocBirth);
						SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
						Date currentDate = new Date();
						String rocStr = sdf2.format(d);
						String currentStr = sdf2.format(currentDate);
						int diffY = yearsBetween(rocStr, currentStr);

						switch (params.getLim_age_type()) {
						case 1:
							ageStr = "未滿";
							if (params.getLim_age() < diffY) {
								MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
								intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
										params.getNhi_no(),
										String.format("(醫令代碼)%s與支付準則條件:限定" + ageStr + "%d歲病患開立，疑似有出入",
												params.getNhi_no(), params.getLim_age()),
										true);
							}
							break;
						case 2:
							ageStr = "大於等於";
							if (params.getLim_age() >= diffY) {
								MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
								intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
										params.getNhi_no(),
										String.format("(醫令代碼)%s與支付準則條件:限定" + ageStr + "%d歲病患開立，疑似有出入",
												params.getNhi_no(), params.getLim_age()),
										true);
							}
							break;
						case 3:
							ageStr = "小於等於";
							if (params.getLim_age() <= diffY) {
								MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
								intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
										params.getNhi_no(),
										String.format("(醫令代碼)%s與支付準則條件:限定" + ageStr + "%d歲病患開立，疑似有出入",
												params.getNhi_no(), params.getLim_age()),
										true);
							}
							break;
						}
					}
				}
			}
		}

		/// 7.
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

					intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), params.getNhi_no(),
							String.format("(醫令代碼)%s與支付準則條件:限定特定科%s別應用，疑似有出入", params.getNhi_no(), funcAppend), true);
				}
			}

		}

		/// 8.
		/// 限定單一醫師、護理人員、藥師執行此醫令單月上限
		if (params.getLim_max_enable() == 1) {
			if (params.getOutpatient_type() == 1) {
				List<String> mrStrAppendList = new ArrayList<String>();
				for (MR mr : mrList2) {
					/// 取出門診資料病例id
					if (mr.getCodeAll().contains(params.getNhi_no()) && mr.getDataFormat().equals("10")) {
						mrStrAppendList.add(mr.getId().toString());
					}
				}
				/// 依照條件查詢出門診清單
				List<OP_D> opdList = opdDao.getListByMrId(mrStrAppendList);
				if (opdList.size() > 0) {
					int prsnCount = 0;
					int pharCount = 0;
					for (OP_D opd : opdList) {

						Date funcDate = DateTool.convertChineseToYear(opd.getFuncDate());
						String mStr = funcDate.getMonth() + "m";
						for (OP_D opd2 : opdList) {

							Date funcDate2 = DateTool.convertChineseToYear(opd2.getFuncDate());
							String mStr2 = funcDate2.getMonth() + "m";
							/// 計算同月有出現的醫療人員
							if (mStr.equals(mStr2)) {
								if (opd.getPrsnId().equals(opd2.getPrsnId())) {
									prsnCount++;
								}
								if (opd.getPharId().equals(opd2.getPharId())) {
									pharCount++;
								}
							}

						}
						/// 單月限定超過次數寫入
						if (params.getLim_max() > prsnCount || params.getLim_max() > pharCount) {
							MR mr = mrDao.getMrByID(opd.getMrId().toString());
							intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
									params.getNhi_no(), String.format("(醫令代碼)%s與支付準則條件:限定單一門急診醫師、護理人員、藥師執行此醫令單月上限，"
											+ params.getLim_max() + "次，疑似有出入", params.getNhi_no()),
									true);
						}

						prsnCount = 0;
						pharCount = 0;

					}
				}

			}
			if (params.getHospitalized_type() == 1) {

				List<String> mrStrAppendList = new ArrayList<String>();
				for (MR mr : mrList2) {
					/// 取出住院資料病例id
					if (mr.getCodeAll().contains(params.getNhi_no()) && mr.getDataFormat().equals("20")) {
						mrStrAppendList.add(mr.getId().toString());
					}
				}
				/// 依照條件查詢住院診清單
				List<IP_D> ipdList = ipdDao.getListByMrId(mrStrAppendList);
				if (ipdList.size() > 0) {
					int prsnCount = 0;
					for (IP_D ipd : ipdList) {

						Date funcDate = DateTool.convertChineseToYear(ipd.getInDate());
						String mStr = funcDate.getMonth() + "m";
						for (IP_D ipd2 : ipdList) {

							Date funcDate2 = DateTool.convertChineseToYear(ipd2.getInDate());
							String mStr2 = funcDate2.getMonth() + "m";
							/// 計算同月有出現的醫療人員
							if (mStr.equals(mStr2)) {
								if (ipd.getPrsnId().equals(ipd2.getPrsnId())) {
									prsnCount++;
								}

							}

						}
						/// 單月限定超過次數寫入
						if (params.getLim_max() > prsnCount) {
							MR mr = mrDao.getMrByID(ipd.getMrId().toString());
							intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
									params.getNhi_no(), String.format("(醫令代碼)%s與支付準則條件:限定單一住院醫師、護理人員、藥師執行此醫令單月上限，"
											+ params.getLim_max() + "次，疑似有出入", params.getNhi_no()),
									true);
						}

						prsnCount = 0;

					}
				}
			}
		}
      logger.info("vaidOutpatientFee " + params.getFee_name());
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
	 * @param early
	 * @param late
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
