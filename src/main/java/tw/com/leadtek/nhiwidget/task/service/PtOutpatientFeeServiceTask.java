package tw.com.leadtek.nhiwidget.task.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.constant.INTELLIGENT_REASON;
import tw.com.leadtek.nhiwidget.dao.IP_DDao;
import tw.com.leadtek.nhiwidget.dao.MRDao;
import tw.com.leadtek.nhiwidget.dao.OP_DDao;
import tw.com.leadtek.nhiwidget.dto.PtOutpatientFeePl;
import tw.com.leadtek.nhiwidget.model.rdb.IP_D;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.model.rdb.OP_D;
import tw.com.leadtek.nhiwidget.service.IntelligentService;
import tw.com.leadtek.tools.DateTool;

@Service
public class PtOutpatientFeeServiceTask extends BasicIntelligentService {

	@Autowired
	private MRDao mrDao;
	@Autowired
	private IP_DDao ipdDao;
	@Autowired
	private OP_DDao opdDao;
	@Autowired
	private IntelligentService intelligentService;

	private String Category = "門診診察費";

	@SuppressWarnings("unchecked")
	public void vaidOutpatientFee(PtOutpatientFeePl params) throws ParseException {
		
		boolean isHospital = false;
		boolean isOutpatien = false;
		Map<String,Object> retMap = this.vaidIntelligentTtype(params.getStart_date(), params.getEnd_date(), params.getNhi_no(), params.getOutpatient_type(), params.getHospitalized_type());
		isHospital = (boolean) retMap.get("isHospital");
		isOutpatien = (boolean) retMap.get("isOutpatien");
		/// 存放病例
		List<MR> mrList = new ArrayList<MR>();
		mrList.addAll((Collection<? extends MR>) retMap.get("mrList"));
		/// 存放mrID
		List<String> mrIdListStr = new ArrayList<String>();
		mrIdListStr.addAll((Collection<? extends String>) retMap.get("mrIdListStr"));
		
		

		/// 1.
		/// 住院
		List<Map<String, Object>> hospitalListD = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> hospitalListC = new ArrayList<Map<String, Object>>();
		/// 門診
		List<Map<String, Object>> outpatientListD = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> outpatientListC = new ArrayList<Map<String, Object>>();
		/// 如果住院
		if (isHospital) {
			/// 不含牙科
			if (params.getNo_dentisit() == 1) {
				hospitalListD = ipdDao.getValidByNoDentisit(mrIdListStr);

			}
			/// 不含中醫
			if (params.getNo_chi_medicine() == 1) {
				hospitalListC = ipdDao.getValidByNoChiMedicine(mrIdListStr);

			}
			if (hospitalListD.size() > 0) {

				for (Map<String, Object> map : hospitalListD) {
					MR mr = mrDao.getMrByID(map.get("mr_id").toString());
					intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), params.getNhi_no(),
							String.format("(醫令代碼)%s與支付準則條件:不含牙科(條件敘述)疑似有出入", params.getNhi_no()), true);

				}

			}
			if (hospitalListC.size() > 0) {

				for (Map<String, Object> map : hospitalListC) {
					MR mr = mrDao.getMrByID(map.get("mr_id").toString());
					intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), params.getNhi_no(),
							String.format("(醫令代碼)%s與支付準則條件:不含中醫(條件敘述)疑似有出入", params.getNhi_no()), true);

				}
			}
		}
		/// 如果門診
		if (isOutpatien) {
			/// 不含牙科
			if (params.getNo_dentisit() == 1) {
				outpatientListD = opdDao.getValidByNoDentisit(mrIdListStr);

			}
			/// 不含中醫
			if (params.getNo_chi_medicine() == 1) {
				outpatientListC = opdDao.getValidByNoChiMedicine(mrIdListStr);

			}
			if (outpatientListD.size() > 0) {

				for (Map<String, Object> map : outpatientListD) {
					MR mr = mrDao.getMrByID(map.get("mr_id").toString());
					intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), params.getNhi_no(),
							String.format("(醫令代碼)%s與支付準則條件:門診不含牙科(條件敘述)疑似有出入", params.getNhi_no()), true);

				}

			}
			if (outpatientListC.size() > 0) {

				for (Map<String, Object> map : outpatientListC) {
					MR mr = mrDao.getMrByID(map.get("mr_id").toString());
					intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), params.getNhi_no(),
							String.format("(醫令代碼)%s與支付準則條件:門診不含中醫(條件敘述)疑似有出入", params.getNhi_no()), true);

				}
			}
		}
		/// 2.
		/// 開立此醫令，處方交付特約藥局調劑或未開處方者，不得申報藥事服務費(調劑費)
		if (params.getNo_service_charge() == 1) {
			/// 如果門診
			if (isOutpatien) {
				List<Map<String, Object>> opData = mrDao.getIdByOPandPaycode(mrIdListStr, "調劑費");
				for (Map<String, Object> map : opData) {
					MR mr = mrDao.getMrByID(map.get("ID").toString());
					intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), params.getNhi_no(),
							String.format("(醫令代碼)%s與支付準則條件:開立此醫令，處方交付特約藥局調劑或未開處方者，不得申報藥事服務費(調劑費)疑似有出入",
									params.getNhi_no()),
							true);
				}
			}
			/// 如果住院
			if (isHospital) {
				List<Map<String, Object>> ipData = mrDao.getIdByIPandPaycode(mrIdListStr, "調劑費");
				for (Map<String, Object> map : ipData) {
					MR mr = mrDao.getMrByID(map.get("ID").toString());
					intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), params.getNhi_no(),
							String.format("(醫令代碼)%s與支付準則條件:開立此醫令，處方交付特約藥局調劑或未開處方者，不得申報藥事服務費(調劑費)疑似有出入",
									params.getNhi_no()),
							true);
				}
			}

		}
		/// 3.
		/// 限定山地離島區域申報使用，目前測試將離島排除，但正式要等於離島
		if (params.getLim_out_islands() == 1) {
			/// 如果門診
			if (isOutpatien) {

				List<Map<String, Object>> opData = opdDao.getPartNoByOutisLand(params.getNhi_no(), mrIdListStr);
				if (opData.size() > 0) {
					for (Map<String, Object> map : opData) {
						MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), params.getNhi_no(),
								String.format("(醫令代碼)%s與支付準則條件:限定離島區域申報使用:單一就醫紀錄，部分負擔代號(PartNO)代號007，方可使用此醫令，疑似有出入",
										params.getNhi_no()),
								true);
					}
				}
			}
			/// 如果住院
			if (isHospital) {

				List<Map<String, Object>> ipData = ipdDao.getPartNoByOutisLand(params.getNhi_no(), mrIdListStr);
				if (ipData.size() > 0) {
					for (Map<String, Object> map : ipData) {
						MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), params.getNhi_no(),
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
			for (MR mr : mrList) {
				Date date = mr.getMrDate();
				Calendar cal = this.dateToCalendar(date);
				/// 如果住院
				if (isHospital) {
					if (mr.getDataFormat().equals("20")) {
						if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
								|| cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
							intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
									params.getNhi_no(),
									String.format("(醫令代碼)%s與支付準則條件:限定假日加計使用:六日或國定假日，方可使用此醫令，疑似有出入", params.getNhi_no()),
									true);
						}
					}
				}
				/// 如果門診
				if (isOutpatien) {
					if (mr.getDataFormat().equals("10")) {
						if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
								|| cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
							intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
									params.getNhi_no(),
									String.format("(醫令代碼)%s與支付準則條件:限定假日加計使用:六日或國定假日，方可使用此醫令，疑似有出入", params.getNhi_no()),
									true);
						}
					}
				}

			}
		}

		/// 5.
		/// 不可與___(輸入支付標準代碼)____任一，並存單一就醫紀錄一併申報
		if (params.getExclude_nhi_no_enable() == 1) {

			this.Exclude_nhi_no_enable(params.getNhi_no(), params.getLst_nhi_no(), isOutpatien, isHospital, mrList);
		}

		/// 6.
		/// 限定 未滿 /大於等於 /小於等於 ______歲病患開立
		if (params.getLim_age_enable() == 1) {
			String ageStr = "";
			/// 如果住院
			if (isHospital) {

				List<IP_D> ipData = ipdDao.getDataListByMrId(mrIdListStr);
				if (ipData.size() > 0) {
					for (IP_D model : ipData) {
						String rocBirth = "";
						/// 如果新生日期不為空
						if (model.getNbBirthday() != null && !model.getNbBirthday().isEmpty()) {
							rocBirth = model.getNbBirthday();
						} else {
							rocBirth = model.getIdBirthYmd();
						}
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
								MR mr = mrDao.getMrByID(model.getMrId().toString());
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
								MR mr = mrDao.getMrByID(model.getMrId().toString());
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
								MR mr = mrDao.getMrByID(model.getMrId().toString());
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
			if (isOutpatien) {
				List<OP_D> opData = opdDao.getDataListByMrId(mrIdListStr);
				if (opData.size() > 0) {
					for (OP_D model : opData) {
						String rocBirth = "";
						/// 如果新生日期不為空
						if (model.getNbBirthday() != null && !model.getNbBirthday().isEmpty()) {
							rocBirth = model.getNbBirthday();
						} else {
							rocBirth = model.getIdBirthYmd();
						}
						Date d = DateTool.convertChineseToYear(rocBirth);
						SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
						Date currentDate = new Date();
						String rocStr = sdf2.format(d);
						String currentStr = sdf2.format(currentDate);
						int diffY = this.yearsBetween(rocStr, currentStr);

						switch (params.getLim_age_type()) {
						case 1:
							ageStr = "未滿";
							if (params.getLim_age() < diffY) {
								MR mr = mrDao.getMrByID(model.getMrId().toString());
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
								MR mr = mrDao.getMrByID(model.getMrId().toString());
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
								MR mr = mrDao.getMrByID(model.getMrId().toString());
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
			
			this.Lim_division_enable(params.getNhi_no(), params.getLst_division(), mrIdListStr, isOutpatien, isHospital);

		}

		/// 8.
		/// 限定單一醫師、護理人員、藥師執行此醫令單月上限
		if (params.getLim_max_enable() == 1) {
			/// 如果門診
			if (isOutpatien) {
				List<String> mrStrAppendList = new ArrayList<String>();
				for (MR mr : mrList) {
					/// 取出門診資料病例id
					if (mr.getDataFormat().equals("10")) {
						mrStrAppendList.add(mr.getId().toString());
					}
				}
				/// 依照條件查詢出門診清單
				List<OP_D> opdList = opdDao.getListByMrId(mrStrAppendList);
				/// 取得計算出每月醫療人員該支付準則次數
				List<Map<String, Object>> opdData = opdDao.getPerMonthPrmanCount(mrStrAppendList, params.getLim_max());

				if (opdList.size() > 0) {

					int prsnCount = 0;
					int pharCount = 0;
					for (OP_D model : opdList) {
						for (Map<String, Object> map : opdData) {
							String fDate1 = map.get("FUNC_DATE").toString();
							String fDate2 = model.getFuncDate().substring(0, 5);
							String prman1 = map.get("PRSN_ID") == null ? "" : map.get("PRSN_ID").toString();
							String phman1 = map.get("PHAR_ID") == null ? "" : map.get("PHAR_ID").toString();
							String prman2 = model.getPrsnId() == null ? "" : model.getPrsnId();
							String phman2 = model.getPharId() == null ? "" : model.getPharId();
							if (fDate1.equals(fDate2)) {
								if (!prman1.isEmpty() && !prman2.isEmpty()) {
								    if(prman1.equals(prman2))
									prsnCount++;
								}
								if (!phman1.isEmpty() && !phman2.isEmpty()) {
									if(phman1.equals(phman2))
									pharCount++;
								}
							}
						}
						/// 單月限定超過次數寫入
						if (prsnCount > 0 || pharCount > 0) {
							MR mr = mrDao.getMrByID(model.getMrId().toString());
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
			/// 如果住院
			if (isHospital) {

				List<String> mrStrAppendList = new ArrayList<String>();
				for (MR mr : mrList) {
					/// 取出住院資料病例id
					if (mr.getDataFormat().equals("20")) {
						mrStrAppendList.add(mr.getId().toString());
					}
				}
				/// 依照條件查詢住院診清單
				List<IP_D> ipdList = ipdDao.getListByMrId(mrStrAppendList);
				/// 取得計算出每月醫療人員該支付準則次數
				List<Map<String, Object>> ipdData = ipdDao.getPerMonthPrmanCount(mrIdListStr, params.getLim_max());
				if (ipdList.size() > 0) {
					int ipdprsnCount = 0;
					int ippprsnCount = 0;
					for (IP_D model : ipdList) {
						for (Map<String, Object> map : ipdData) {

							String fDate1 = map.get("IN_DATE").toString();
							String fDate2 = model.getInDate().substring(0, 5);
							String prman1 = map.get("IPDPRID") == null ? "" : map.get("IPDPRID").toString();
							String phman1 = map.get("IPPPRID") == null ? "" : map.get("IPPPRID").toString();
							String prman2 = model.getPrsnId() == null ? "" : model.getPrsnId();
							if (fDate1.equals(fDate2)) {
								if (!prman1.isEmpty() && !prman2.isEmpty()) {
									if(prman1.equals(prman2))
									ipdprsnCount++;
								}
								if (!phman1.isEmpty()) {
									ippprsnCount++;
								}
							}
						}
						/// 單月限定超過次數寫入
						if (ipdprsnCount > 0 || ippprsnCount > 0) {
							MR mr = mrDao.getMrByID(model.getMrId().toString());
							intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
									params.getNhi_no(), String.format("(醫令代碼)%s與支付準則條件:限定單一住院醫師、護理人員、藥師執行此醫令單月上限，"
											+ params.getLim_max() + "次，疑似有出入", params.getNhi_no()),
									true);
						}

					}
				}
			}
		}

	}
}
