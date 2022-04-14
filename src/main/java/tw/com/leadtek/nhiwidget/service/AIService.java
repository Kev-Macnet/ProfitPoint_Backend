package tw.com.leadtek.nhiwidget.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.constant.INTELLIGENT_REASON;
import tw.com.leadtek.nhiwidget.dao.ICDCM_DRUGDao;
import tw.com.leadtek.nhiwidget.dao.ICDCM_ICDOPDao;
import tw.com.leadtek.nhiwidget.dao.ICDCM_ORDERDao;
import tw.com.leadtek.nhiwidget.dao.IP_DDao;
import tw.com.leadtek.nhiwidget.dao.IP_PDao;
import tw.com.leadtek.nhiwidget.dao.MRDao;
import tw.com.leadtek.nhiwidget.dao.OP_DDao;
import tw.com.leadtek.nhiwidget.dao.OP_PDao;
import tw.com.leadtek.nhiwidget.dao.PAY_CODEDao;
import tw.com.leadtek.nhiwidget.model.rdb.ICDCM_DRUG;
import tw.com.leadtek.nhiwidget.model.rdb.ICDCM_DRUG_KEYS;
import tw.com.leadtek.nhiwidget.model.rdb.ICDCM_ICDOP;
import tw.com.leadtek.nhiwidget.model.rdb.ICDCM_ICDOP_KEYS;
import tw.com.leadtek.nhiwidget.model.rdb.ICDCM_ORDER;
import tw.com.leadtek.nhiwidget.model.rdb.ICDCM_ORDER_KEYS;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.model.rdb.PAY_CODE;

@Service
public class AIService {

	@Autowired
	private MRDao mrDao;
	@Autowired
	private ParametersService parametersService;
	@Autowired
	private IntelligentService intelligentService;
	@Autowired
	private OP_PDao oppDao;
	@Autowired
	private IP_PDao ippDao;
	@Autowired
	private OP_DDao opdDao;
	@Autowired
	private IP_DDao ipdDao;
	@Autowired
	private PAY_CODEDao pcDao;
	@Autowired
	private ICDCM_ORDERDao icdcmOrderDao;
	@Autowired
	private ICDCM_DRUGDao icdcmDrugDao;
	@Autowired
	private ICDCM_ICDOPDao icdcmIcdopDao;

	/**
	 * 計算門診費用差異
	 * 
	 * @param InhMrId
	 * @return
	 */
	public Map<String, Object> clinicCostDiff(String sDate1, String eDate1, String sDate2, String eDate2) {
		Map<String, Object> data = new HashMap<String, Object>();
		try {

			String msg = "";
			/// 取得門診上限6%下限6%的病例點數資料
			List<Map<String, Object>> listClinicMap = mrDao.clinic(sDate1, eDate2, sDate2, eDate2);
			// 設定檔上限
			int costDiffUl = Integer
					.parseInt(parametersService.getOneValueByName("INTELLIGENT_CONFIG", "COST_DIFF_UL"));
			// 設定檔下限
			int costDiffll = Integer
					.parseInt(parametersService.getOneValueByName("INTELLIGENT_CONFIG", "COST_DIFF_LL"));
			for (Map<String, Object> clinicMap : listClinicMap) {
				MR mr = mrDao.getMrByID(clinicMap.get("ID").toString());
				// 門診上下限
				float clinicUp = (Float.parseFloat(mr.getTotalDot().toString())
						/ Float.parseFloat(clinicMap.get("UP").toString())) * 100;
				float clinicDown = (Float.parseFloat(mr.getTotalDot().toString())
						/ Float.parseFloat(clinicMap.get("DOWN").toString())) * 100;
				double dUp = (double) clinicMap.get("UP");
				double dDown = (double) clinicMap.get("DOWN");
				int iUp = (int) dUp;
				int iDown = (int) dDown;
				if (clinicUp > costDiffUl) {
					msg = retMsg(COSTTYPE.UP.name(), mr.getInhMrId(), mr.getIcdcm1(), iUp, clinicUp);
					intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(),
							mr.getIcdcm1().toString(), msg, true);
				} else if (clinicDown > costDiffll) {
					msg = retMsg(COSTTYPE.DOWN.name(), mr.getInhMrId(), mr.getIcdcm1(), iDown, clinicUp);
					intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(),
							mr.getIcdcm1().toString(), msg, true);
				}
			}

//			data.put("mr", mr);
//			data.put("clinicMap", clinicMap);
//			data.put("costDiffUl", costDiffUl);
			data.put("msg", msg);
		} catch (Exception e) {
			e.printStackTrace();
			data.put("msg", "該病歷查無門診資料");
		}
		return data;
	}

	/**
	 * 計算住院費用差異
	 * 
	 * @param InhMrId
	 * @return
	 */
	public Map<String, Object> hospitalCostDiff(String date) {
		Map<String, Object> data = new HashMap<String, Object>();

		try {
			String dateStr = minusYear(date);
			String msg = "";
//			Map<String, Object> mr = mrDao.queryByInhMrID(InhMrId);
			List<Map<String, Object>> listHospitalizedMap = mrDao.hospitalized(dateStr);
			// 設定檔上限
			int costDiffUl = Integer
					.parseInt(parametersService.getOneValueByName("INTELLIGENT_CONFIG", "COST_DIFF_UL"));
			// 設定檔下限
			int costDiffll = Integer
					.parseInt(parametersService.getOneValueByName("INTELLIGENT_CONFIG", "COST_DIFF_LL"));
			for (Map<String, Object> hospitalizedMap : listHospitalizedMap) {

				MR mm = mrDao.getMrByID(hospitalizedMap.get("ID").toString());
				// 住院上下限
				float hospitalizedUp = (Float.parseFloat(mm.getTotalDot().toString())
						/ Float.parseFloat(hospitalizedMap.get("up").toString())) * 100;
				float hospitalizedDown = (Float.parseFloat(mm.getTotalDot().toString())
						/ Float.parseFloat(hospitalizedMap.get("down").toString())) * 100;
				double dUp = (double) hospitalizedMap.get("up");
				double dDown = (double) hospitalizedMap.get("down");
				int iUp = (int) dUp;
				int iDown = (int) dDown;
				if (hospitalizedUp > costDiffUl) {
					msg = retMsg(COSTTYPE.UP.name(), mm.getInhMrId(), mm.getIcdcm1().toString(), iUp, hospitalizedUp);

					intelligentService.insertIntelligent(mm, INTELLIGENT_REASON.COST_DIFF.value(),
							mm.getIcdcm1().toString(), msg, true);
				} else if (hospitalizedDown > costDiffll) {
					msg = retMsg(COSTTYPE.DOWN.name(), mm.getInhMrId(), mm.getIcdcm1().toString(), iDown,
							hospitalizedDown);
					intelligentService.insertIntelligent(mm, INTELLIGENT_REASON.COST_DIFF.value(),
							mm.getIcdcm1().toString(), msg, true);
				}
			}

//			data.put("mr", mm);
//			data.put("hospitalMap", hospitalizedMap);
//			data.put("costDiffUl", costDiffUl);
			data.put("msg", msg);
		} catch (Exception e) {
			e.printStackTrace();
			data.put("msg", "該病歷無近一年住院資料");
		}
		return data;
	}

	/**
	 * 醫療行為差異,門診
	 */
	public Map<String, Object> clinicMmedBehDiff(String date) {
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			String dateStr = minusYear(date);
			String msg = "";
			String paraResult = "";
			List<ICDCM_ORDER> icdList = icdcmOrderDao.queryByDataFormat("10");
			if (icdList.size() == 0) {
				List<Map<String, Object>> calculateIcdList = new ArrayList<Map<String, Object>>();
				/// 主診斷搭配醫令使用次數
				calculateIcdList = oppDao.calculate();
				List<ICDCM_ORDER> icdcmList = new ArrayList<ICDCM_ORDER>();
				for (Map<String, Object> m : calculateIcdList) {
					ICDCM_ORDER io = new ICDCM_ORDER();
					io.setIcdcmorderPK(
							new ICDCM_ORDER_KEYS(m.get("icdcm1").toString(), m.get("drug_no").toString(), "10"));
					io.setAverage(Float.parseFloat(m.get("AVG").toString()));
					io.setUlimit(Float.parseFloat(m.get("UP").toString()));
					io.setLlimit(Float.parseFloat(m.get("DOWN").toString()));
					io.setUpdateAT(new Date());

					icdcmList.add(io);
				}
				/// 寫入診斷碼搭配醫令的平均數及上下限
				icdcmOrderDao.saveAll(icdcmList);
			}
			List<Map<String, Object>> clinicList = mrDao.clinicMedBeh(dateStr);

			// 設定檔上限
			int orderDiffUl = Integer
					.parseInt(parametersService.getOneValueByName("INTELLIGENT_CONFIG", "ORDER_DIFF_UL"));
			// 設定檔下限
			int orderDiffll = Integer
					.parseInt(parametersService.getOneValueByName("INTELLIGENT_CONFIG", "ORDER_DIFF_LL"));

			for (Map<String, Object> map : clinicList) {
				MR mm = mrDao.getMrByID(map.get("ID").toString());
				int count = Integer.parseInt(map.get("count").toString());
				if (count > orderDiffUl) {
					float clinicUp = ((float) (count / orderDiffUl) * 100);
					paraResult = parametersService.getOneValueByName("INTELLIGENT", "ORDER_DIFF_UL_WORDING");
					msg = String.format(paraResult + "%", map.get("ID").toString(), map.get("ICDCM1").toString(),
							map.get("ORDER_CODE"), orderDiffUl, clinicUp);
					intelligentService.insertIntelligent(mm, INTELLIGENT_REASON.ORDER_DIFF.value(),
							map.get("ORDER_CODE").toString(), msg, true);
				} else if (count < orderDiffll) {
					float clinicDown = ((float) (count / orderDiffll));
					paraResult = parametersService.getOneValueByName("INTELLIGENT", "ORDER_DIFF_LL_WORDING");
					msg = String.format(paraResult + "%", map.get("MR_ID"), map.get("ICDCM1").toString(),
							map.get("ORDER_CODE"), orderDiffUl, clinicDown);
					intelligentService.insertIntelligent(mm, INTELLIGENT_REASON.ORDER_DIFF.value(),
							map.get("ORDER_CODE").toString(), msg, true);
				}

			}
			data.put("msg", msg);

		} catch (Exception e) {
			e.printStackTrace();
			data.put("msg", "該病歷無近一年門診資料");
		}
		return data;
	}

	/**
	 * 醫療行為差異,住院
	 */
	public Map<String, Object> hospitalMmedBehDiff(String date) {
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			String dateStr = minusYear(date);
			String msg = "";
			String paraResult = "";
			List<ICDCM_ORDER> icdList = icdcmOrderDao.queryByDataFormat("20");
			if (icdList.size() == 0) {
				List<Map<String, Object>> calculateIcdList = new ArrayList<Map<String, Object>>();
				calculateIcdList = ippDao.calculate(dateStr);
				List<ICDCM_ORDER> icdcmList = new ArrayList<ICDCM_ORDER>();
				for (Map<String, Object> m : calculateIcdList) {
					ICDCM_ORDER io = new ICDCM_ORDER();
					io.setIcdcmorderPK(new ICDCM_ORDER_KEYS(m.get("ICDCM").toString(), m.get("ORDER_CODE").toString(),
							m.get("DATA_FORMAT").toString()));
					io.setAverage(Float.parseFloat(m.get("AVERAGE").toString()));
					io.setUlimit(Float.parseFloat(m.get("ULIMIT").toString()));
					io.setLlimit(Float.parseFloat(m.get("LLIMIT").toString()));
					io.setUpdateAT(new Date());

					icdcmList.add(io);
				}
				icdcmOrderDao.saveAll(icdcmList);
			}
			List<Map<String, Object>> clinicList = mrDao.clinicMedBeh(dateStr);

			// 設定檔上限
			int orderDiffUl = Integer
					.parseInt(parametersService.getOneValueByName("INTELLIGENT_CONFIG", "ORDER_DIFF_UL"));
			// 設定檔下限
			int orderDiffll = Integer
					.parseInt(parametersService.getOneValueByName("INTELLIGENT_CONFIG", "ORDER_DIFF_LL"));

			for (Map<String, Object> map : clinicList) {
				MR mm = mrDao.getMrByID(map.get("ID").toString());
				int count = Integer.parseInt(map.get("count").toString());
				if (count > orderDiffUl) {
					float clinicUp = ((float) (count / orderDiffUl) * 100);
					paraResult = parametersService.getOneValueByName("INTELLIGENT", "ORDER_DIFF_UL_WORDING");
					msg = String.format(paraResult + "%", map.get("ID").toString(), map.get("ICDCM1").toString(),
							map.get("ORDER_CODE"), orderDiffUl, clinicUp);
					intelligentService.insertIntelligent(mm, INTELLIGENT_REASON.ORDER_DIFF.value(),
							map.get("ORDER_CODE").toString(), msg, true);
				} else if (count < orderDiffll) {
					float clinicDown = ((float) (count / orderDiffll));
					paraResult = parametersService.getOneValueByName("INTELLIGENT", "ORDER_DIFF_LL_WORDING");
					msg = String.format(paraResult + "%", map.get("MR_ID"), map.get("ICDCM1").toString(),
							map.get("ORDER_CODE"), orderDiffUl, clinicDown);
					intelligentService.insertIntelligent(mm, INTELLIGENT_REASON.ORDER_DIFF.value(),
							map.get("ORDER_CODE").toString(), msg, true);
				}

			}
			data.put("msg", msg);

		} catch (Exception e) {
			e.printStackTrace();
			data.put("msg", "該病歷無近一年住院資料");
		}
		return data;
	}

	/**
	 * 主診斷使用的手術碼,門診
	 * 
	 * @param date
	 * @return
	 */
	public Map<String, Object> clinicOperaiton(String date) {
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			String dateStr = minusYear(date);
			String msg = "";
			String paraResult = "";
			List<ICDCM_ICDOP> icdList = icdcmIcdopDao.queryByDataFormat("10");
			if (icdList.size() == 0) {
				List<Map<String, Object>> calculateIcdList = opdDao.getClinicOperation(dateStr);

				List<ICDCM_ICDOP> icdcmList = new ArrayList<ICDCM_ICDOP>();
				int maxNum = 0;
				int maxNum2 = 0;
				for (Map<String, Object> m : calculateIcdList) {
					maxNum = Integer.parseInt(m.get("IOC1COUNT").toString());

				}
				for (Map<String, Object> m : calculateIcdList) {
					int i = Integer.parseInt(m.get("IOC2COUNT").toString());
					for (Map<String, Object> m2 : calculateIcdList) {
						int i2 = Integer.parseInt(m2.get("IOC2COUNT").toString());
						if (i2 > 0) {
							maxNum2 = i2;
						}
					}
					if (i > maxNum2) {
						maxNum2 = i;
					}

				}
				for (Map<String, Object> m : calculateIcdList) {
					ICDCM_ICDOP ii = new ICDCM_ICDOP();
					/// 主診斷碼之手術碼1
					int t = Integer.parseInt(m.get("IOC1COUNT").toString());
					int math = Math.round((t * 100 / maxNum));
					ii.setTotal(t);
					ii.setUpdateAT(new Date());
					ii.setIcdcmicdopPK(
							new ICDCM_ICDOP_KEYS(m.get("ICDCM1").toString(), m.get("ICD_OP_CODE1").toString(), "10"));
					ii.setPercent(math);

					icdcmList.add(ii);
					/// 主診斷碼之手術碼2
					int t2 = Integer.parseInt(m.get("IOC2COUNT").toString());
					if (t2 > 0) {
						ii = new ICDCM_ICDOP();
						int math2 = Math.round((t2 * 100 / maxNum2));
						ii.setTotal(t2);
						ii.setUpdateAT(new Date());
						ii.setIcdcmicdopPK(new ICDCM_ICDOP_KEYS(m.get("ICDCM1").toString(),
								m.get("ICD_OP_CODE2").toString(), "10"));
						ii.setPercent(math2);

						icdcmList.add(ii);
					}
				}
				icdcmIcdopDao.saveAll(icdcmList);
			}
			List<Map<String, Object>> clinicList = mrDao.clinicOpepration(dateStr);
			for (Map<String, Object> map : clinicList) {
				String str = "";
				String str2 = "";
				MR mm = mrDao.getMrByID(map.get("ID").toString());
				float percent = Float.parseFloat(map.get("PERCENT").toString());
				if (percent < 5) {
					List<ICDCM_ICDOP> iList = icdcmIcdopDao.queryClinicOperation(map.get("ICD_CM_1").toString());
					/// 取得第一常用手術碼
					for (ICDCM_ICDOP ii : iList) {
						str = ii.getIcdcmicdopPK().getIcdop();
					}
					iList.remove(iList.size() - 1);
					/// 取得第二常用手術碼
					for (ICDCM_ICDOP ii : iList) {
						str2 = ii.getIcdcmicdopPK().getIcdop();
					}
					paraResult = parametersService.getOneValueByName("INTELLIGENT", "ORDER_DIFF_OP_WORDING");
					msg = String.format(paraResult, mm.getInhMrId(), map.get("ICD_CM_1").toString(),
							map.get("ICDOP").toString(), str, str2);
					intelligentService.insertIntelligent(mm, INTELLIGENT_REASON.ORDER_DIFF.value(),
							map.get("ICDOP").toString(), msg, true);
				}

			}
			data.put("msg", msg);

		} catch (Exception e) {
			e.printStackTrace();
			data.put("msg", "該病歷無近一年住院資料");
		}
		return data;
	}

	/**
	 * 主診斷使用的手術碼,醫院
	 * 
	 * @param date
	 * @return
	 */
	public Map<String, Object> hospitalOperaiton(String date) {
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			String dateStr = minusYear(date);
			String msg = "";
			String paraResult = "";
			List<ICDCM_ICDOP> icdList = icdcmIcdopDao.queryByDataFormat("20");
			if (icdList.size() == 0) {
				List<Map<String, Object>> calculateIcdList = ipdDao.getHospitalOperation(dateStr);

				List<ICDCM_ICDOP> icdcmList = new ArrayList<ICDCM_ICDOP>();
				int maxNum = 0;
				int maxNum2 = 0;
				for (Map<String, Object> m : calculateIcdList) {
					maxNum = Integer.parseInt(m.get("IOC1COUNT").toString());

				}
				for (Map<String, Object> m : calculateIcdList) {
					int i = Integer.parseInt(m.get("IOC2COUNT").toString());
					for (Map<String, Object> m2 : calculateIcdList) {
						int i2 = Integer.parseInt(m2.get("IOC2COUNT").toString());
						if (i2 > 0) {
							maxNum2 = i2;
						}
					}
					if (i > maxNum2) {
						maxNum2 = i;
					}

				}
				for (Map<String, Object> m : calculateIcdList) {
					ICDCM_ICDOP ii = new ICDCM_ICDOP();
					/// 主診斷碼之手術碼1
					int t = Integer.parseInt(m.get("IOC1COUNT").toString());
					int math = Math.round((t * 100 / maxNum));
					ii.setTotal(t);
					ii.setUpdateAT(new Date());
					ii.setIcdcmicdopPK(
							new ICDCM_ICDOP_KEYS(m.get("ICDCM1").toString(), m.get("ICD_OP_CODE1").toString(), "20"));
					ii.setPercent(math);

					icdcmList.add(ii);
					/// 主診斷碼之手術碼2
					int t2 = Integer.parseInt(m.get("IOC2COUNT").toString());
					if (t2 > 0) {
						ii = new ICDCM_ICDOP();
						int math2 = Math.round((t2 * 100 / maxNum2));
						ii.setTotal(t2);
						ii.setUpdateAT(new Date());
						ii.setIcdcmicdopPK(new ICDCM_ICDOP_KEYS(m.get("ICDCM1").toString(),
								m.get("ICD_OP_CODE2").toString(), "20"));
						ii.setPercent(math2);

						icdcmList.add(ii);
					}
				}
				icdcmIcdopDao.saveAll(icdcmList);
			}
			List<Map<String, Object>> clinicList = mrDao.hospitalOpepration(dateStr);
			for (Map<String, Object> map : clinicList) {
				String str = "";
				String str2 = "";
				MR mm = mrDao.getMrByID(map.get("ID").toString());
				float percent = Float.parseFloat(map.get("PERCENT").toString());
				if (percent < 5) {
					List<ICDCM_ICDOP> iList = icdcmIcdopDao.queryHospitalOperation(map.get("ICD_CM_1").toString());
					/// 取得第一常用手術碼
					for (ICDCM_ICDOP ii : iList) {
						str = ii.getIcdcmicdopPK().getIcdop();
					}
					if (iList.size() > 2) {

						iList.remove(iList.size() - 1);
					}
					/// 取得第二常用手術碼
					for (ICDCM_ICDOP ii : iList) {
						str2 = ii.getIcdcmicdopPK().getIcdop();
					}
					paraResult = parametersService.getOneValueByName("INTELLIGENT", "ORDER_DIFF_OP_WORDING");
					msg = String.format(paraResult, mm.getInhMrId(), map.get("ICD_CM_1").toString(),
							map.get("ICDOP").toString(), str, str2);
					intelligentService.insertIntelligent(mm, INTELLIGENT_REASON.ORDER_DIFF.value(),
							map.get("ICDOP").toString(), msg, true);
				}

			}
			data.put("msg", msg);

		} catch (Exception e) {
			e.printStackTrace();
			data.put("msg", "該病歷無近一年住院資料");
		}
		return data;
	}

	/**
	 * 用藥差異-門診
	 * 
	 * @param date
	 * @return
	 */
	public Map<String, Object> clinicMedicine(String date) {
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			String dateStr = minusYear(date);
			String msg = "";
			String paraResult = "";
			List<ICDCM_DRUG> icdList = icdcmDrugDao.queryByDataFormat("10");
			/// 取得主診斷count
			List<Map<String, Object>> icdmcList = mrDao.getIcdcmCount(dateStr, "10");
			/// 取得藥用count
			List<Map<String, Object>> drugnoList = mrDao.getDrugNoCount(dateStr,"10");
			List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
			Map<String, Object> model = new HashMap<String, Object>();
			List<ICDCM_DRUG> icdcmList = new ArrayList<ICDCM_DRUG>();
		

			
			if (icdList.size() == 0) {
				/// 將資料append再一起
				for (Map<String, Object> map1 : drugnoList) {
					for(Map<String, Object> map2 : icdmcList) {
						if(map1.get("ICDCM1").toString().equals(map2.get("ICDCM1"))) {
							int isDrug = 1;
							PAY_CODE pcModel = pcDao.findByCodeOne(map1.get("DRUG_NO").toString());
							
							if (pcModel != null) {
								if (pcModel.getCodeType() == "西藥藥品") {
									isDrug = 1;
								} else {
									isDrug = 2;
								}
							}
							model.put("DRUGNO", map1.get("DRUG_NO"));
							model.put("DRUGNO_COUNT", map1.get("COUNT"));
							model.put("ICDCM", map1.get("ICDCM1"));
							model.put("IS_DRUG", isDrug);
							model.put("ICDCM_COUNT", map2.get("COUNT"));
							float percent = (Float.parseFloat(map1.get("COUNT").toString())
									/ Float.parseFloat(map2.get("COUNT").toString()));
							model.put("PERCENT", percent);
							dataList.add(model);
							model = new HashMap<String, Object>();
						}
						
					}
				}
				/// 寫資料庫
				for (Map<String, Object> m : dataList) {
					ICDCM_DRUG idModel = new ICDCM_DRUG();
					int t = Integer.parseInt(m.get("ICDCM_COUNT").toString());
					int math = Math.round((float) m.get("PERCENT"));
					idModel.setTotal(t);
					idModel.setUpdateAT(new Date());
					idModel.setIcdcmdrugPK(
							new ICDCM_DRUG_KEYS(m.get("ICDCM").toString(), m.get("DRUGNO").toString(), "10"));
					idModel.setPercent(math);
					idModel.setIsdurug(Integer.parseInt(m.get("IS_DRUG").toString()));

					icdcmList.add(idModel);
				}

				icdcmDrugDao.saveAll(icdcmList);

			}
			if (icdList.size() == 0) {
				icdList = icdcmDrugDao.queryByDataFormat("10");
			}
			String drug11 = "";
			String drug12 = "";
			String drug21 = "";
			String drug22 = "";
			int count1 = 0;
			int count2 = 0;
			for (ICDCM_DRUG idModel : icdList) {
				if (idModel.getIsdurug() == 1) {
					if (count1 == 0) {
						drug11 = idModel.getIcdcmdrugPK().getDrug();
					} else if (count1 == 1) {
						drug12 = idModel.getIcdcmdrugPK().getDrug();
					} else {
						break;
					}

					count1++;
				} else {
					if (count2 == 0) {
						drug21 = idModel.getIcdcmdrugPK().getDrug();
					} else if (count2 == 1) {
						drug22 = idModel.getIcdcmdrugPK().getDrug();
					} else {
						break;
					}

					count2++;
				}
			}
			List<Map<String, Object>> idList = mrDao.getIdByDrugNoCount(dateStr);
			for (ICDCM_DRUG idModel : icdList) {
				for (Map<String, Object> mapModel : dataList) {
					for(Map<String, Object> map : idList) {
						
						MR mr = mrDao.getMrByID(map.get("ID").toString());
						if (idModel.getIsdurug() == 1) {
							if (idModel.getIcdcmdrugPK().getDrug().equals(mapModel.get("DRUGNO").toString())) {
								int math = Math.round((float) mapModel.get("PERCENT"));
								int iModelPer = idModel.getPercent();
								float a = (float) math;
								float b = (float) iModelPer;
								float fPer = a / b;
								int round = Math.round(fPer);
								if (round < 5) {
									
									paraResult = parametersService.getOneValueByName("INTELLIGENT", "DRUG_DIFF_WORDING");
									msg = String.format(paraResult, mr.getInhMrId(), mapModel.get("ICDCM").toString(),
											mr.getName(), mapModel.get("DRUGNO").toString(), drug11, drug12);
									intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.DRUG_DIFF.value(),
											mapModel.get("ICDCM").toString(), msg, true);
								}
							}
						} else {
							
							if (idModel.getIcdcmdrugPK().getDrug().equals(mapModel.get("DRUGNO").toString())) {
								int math = Math.round((float) mapModel.get("PERCENT"));
								int iModelPer = idModel.getPercent();
								float a = (float) math;
								float b = (float) iModelPer;
								float fPer = a / b;
								int round = Math.round(fPer);
								if (round < 5) {
									
									paraResult = parametersService.getOneValueByName("INTELLIGENT",
											"MATERIAL_DIFF_WORDING");
									msg = String.format(paraResult, mr.getInhMrId(), mapModel.get("ICDCM").toString(),
											mr.getName(), mapModel.get("DRUGNO").toString(), drug21, drug22);
									intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.DRUG_DIFF.value(),
											mapModel.get("ICDCM").toString(), msg, true);
								}
							}
							
						}
					}
				}

			}

			data.put("msg", msg);

		} catch (Exception e) {
			e.printStackTrace();
			data.put("msg", "該病歷無近一年門診資料");
		}
		return data;
	}

	/**
	 * 用藥差異-住院
	 * 
	 * @param date
	 * @return
	 */
	public Map<String, Object> hospitalMedicine(String date) {
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			String dateStr = minusYear(date);
			String msg = "";
			String paraResult = "";
			List<ICDCM_DRUG> icdList = icdcmDrugDao.queryByDataFormat("20");

			/// 取得主診斷count
			List<Map<String, Object>> icdmcList = mrDao.getIcdcmCount(dateStr, "20");
			/// 取得藥用count
			List<Map<String, Object>> drugnoList = mrDao.getDrugNoCount(dateStr,"20");
			List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
			Map<String, Object> model = new HashMap<String, Object>();
			List<ICDCM_DRUG> icdcmList = new ArrayList<ICDCM_DRUG>();
			
			if (icdList.size() == 0) {
				/// 將資料append再一起
				for (Map<String, Object> map1 : drugnoList) {
					for(Map<String, Object> map2 : icdmcList) {
						if(map1.get("ICDCM1").toString().equals(map2.get("ICDCM1"))) {
							int isDrug = 1;
							PAY_CODE pcModel = pcDao.findByCodeOne(map1.get("DRUG_NO").toString());
							
							if (pcModel != null) {
								if (pcModel.getCodeType() == "西藥藥品") {
									isDrug = 1;
								} else {
									isDrug = 2;
								}
							}
							model.put("DRUGNO", map1.get("DRUG_NO"));
							model.put("DRUGNO_COUNT", map1.get("COUNT"));
							model.put("ICDCM", map1.get("ICDCM1"));
							model.put("IS_DRUG", isDrug);
							model.put("ICDCM_COUNT", map2.get("COUNT"));
							float percent = (Float.parseFloat(map1.get("COUNT").toString())
									/ Float.parseFloat(map2.get("COUNT").toString()));
							model.put("PERCENT", percent);
							dataList.add(model);
							model = new HashMap<String, Object>();
						}
						
					}
				}
				/// 寫資料庫
				for (Map<String, Object> m : dataList) {
					ICDCM_DRUG idModel = new ICDCM_DRUG();
					int t = Integer.parseInt(m.get("ICDCM_COUNT").toString());
					int math = Math.round((float) m.get("PERCENT"));
					idModel.setTotal(t);
					idModel.setUpdateAT(new Date());
					idModel.setIcdcmdrugPK(
							new ICDCM_DRUG_KEYS(m.get("ICDCM").toString(), m.get("DRUGNO").toString(), "10"));
					idModel.setPercent(math);
					idModel.setIsdurug(Integer.parseInt(m.get("IS_DRUG").toString()));

					icdcmList.add(idModel);
				}

				icdcmDrugDao.saveAll(icdcmList);

			}
			if (icdList.size() == 0) {
				icdList = icdcmDrugDao.queryByDataFormat("10");
			}
			String drug11 = "";
			String drug12 = "";
			String drug21 = "";
			String drug22 = "";
			int count1 = 0;
			int count2 = 0;
			for (ICDCM_DRUG idModel : icdList) {
				if (idModel.getIsdurug() == 1) {
					if (count1 == 0) {
						drug11 = idModel.getIcdcmdrugPK().getDrug();
					} else if (count1 == 1) {
						drug12 = idModel.getIcdcmdrugPK().getDrug();
					} else {
						break;
					}

					count1++;
				} else {
					if (count2 == 0) {
						drug21 = idModel.getIcdcmdrugPK().getDrug();
					} else if (count2 == 1) {
						drug22 = idModel.getIcdcmdrugPK().getDrug();
					} else {
						break;
					}

					count2++;
				}
			}
			List<Map<String, Object>> idList = mrDao.getIdByDrugNoCount(dateStr);
			for (ICDCM_DRUG idModel : icdList) {
				for (Map<String, Object> mapModel : dataList) {
					for(Map<String, Object> map : idList) {
						
						MR mr = mrDao.getMrByID(map.get("ID").toString());
						if (idModel.getIsdurug() == 1) {
							if (idModel.getIcdcmdrugPK().getDrug().equals(mapModel.get("DRUGNO").toString())) {
								int math = Math.round((float) mapModel.get("PERCENT"));
								int iModelPer = idModel.getPercent();
								float a = (float) math;
								float b = (float) iModelPer;
								float fPer = a / b;
								int round = Math.round(fPer);
								if (round < 5) {
									
									paraResult = parametersService.getOneValueByName("INTELLIGENT", "DRUG_DIFF_WORDING");
									msg = String.format(paraResult, mr.getInhMrId(), mapModel.get("ICDCM").toString(),
											mr.getName(), mapModel.get("DRUGNO").toString(), drug11, drug12);
									intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.DRUG_DIFF.value(),
											mapModel.get("ICDCM").toString(), msg, true);
								}
							}
						} else {
							
							if (idModel.getIcdcmdrugPK().getDrug().equals(mapModel.get("DRUGNO").toString())) {
								int math = Math.round((float) mapModel.get("PERCENT"));
								int iModelPer = idModel.getPercent();
								float a = (float) math;
								float b = (float) iModelPer;
								float fPer = a / b;
								int round = Math.round(fPer);
								if (round < 5) {
									
									paraResult = parametersService.getOneValueByName("INTELLIGENT",
											"MATERIAL_DIFF_WORDING");
									msg = String.format(paraResult, mr.getInhMrId(), mapModel.get("ICDCM").toString(),
											mr.getName(), mapModel.get("DRUGNO").toString(), drug21, drug22);
									intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.DRUG_DIFF.value(),
											mapModel.get("ICDCM").toString(), msg, true);
								}
							}
							
						}
					}
				}

			}

			data.put("msg", msg);

		} catch (Exception e) {
			e.printStackTrace();
			data.put("msg", "該病歷無近一年門診資料");
		}
		return data;
	}

	/**
	 * 住院天數差異
	 * 
	 * @param date
	 * @return
	 */
	public Map<String, Object> hospitalDays(String date) {
		Map<String, Object> data = new HashMap<String, Object>();

		try {
			String dateStr = minusYear2(date);
			String msg = "";
			List<Map<String, Object>> listHospitalizedMap = mrDao.hospitalDays(dateStr);
			// 設定檔上限
			int ipDays = Integer.parseInt(parametersService.getOneValueByName("INTELLIGENT_CONFIG", "IP_DAYS"));
			for (Map<String, Object> map : listHospitalizedMap) {

				MR mm = mrDao.getMrByID(map.get("ID").toString());
				// 住院上限
				float hospitalizedUp = Float.parseFloat(map.get("up").toString());
				int up = Math.round(hospitalizedUp);

				if (up > ipDays) {

					String paraResult = parametersService.getOneValueByName("INTELLIGENT", "IP_DAYS_WORDING");
					msg = String.format(paraResult, mm.getInhMrId(), map.get("ICD_CM_1").toString(), mm.getName(),
							ipDays, (up - ipDays));
					intelligentService.insertIntelligent(mm, INTELLIGENT_REASON.ORDER_DIFF.value(), "", msg, true);
				}
			}

			data.put("msg", msg);
		} catch (Exception e) {
			e.printStackTrace();
			data.put("msg", "該病歷無近一年住院資料");
		}
		return data;
	}

	/**
	 * 
	 * @param ctype,   enum帶入要顯示上限還是下限
	 * @param InhMrId, 病歷單號
	 * @param ICDM1,   主診斷號
	 * @param Val1,    病歷點數常數
	 * @param Val2,    對比%
	 * @return
	 */
	public String retMsg(String ctype, String InhMrId, String ICDM1, int Val1, float Val2) {
		String result = "";
		String paraResult = "";
		switch (ctype) {
		case "UP":
			/// 取得設定檔的上限word
			paraResult = parametersService.getOneValueByName("INTELLIGENT", "COST_DIFF_UL_WORDING");
			result = String.format(paraResult + "%", InhMrId, ICDM1, Val1, Val2);
			break;
		case "DOWN":
			/// 取得設定檔的下限word
			paraResult = parametersService.getOneValueByName("INTELLIGENT", "COST_DIFF_LL_WORDING");
			result = String.format(paraResult + "%", InhMrId, ICDM1, Val1, Val2);
			break;
		default:
			result = "";
		}

		return result;
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
	 * 帶入日期並減二年
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	private String minusYear2(String date) throws ParseException {
		String result = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date d = sdf.parse(date);
		Calendar currentDate = Calendar.getInstance();
		currentDate.setTime(d);
		currentDate.add(Calendar.YEAR, -2);
		Date d2 = currentDate.getTime();
		result = sdf.format(d2);

		return result;
	}

	public enum COSTTYPE {
		UP, DOWN
	}
}
