package tw.com.leadtek.nhiwidget.task.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.constant.INTELLIGENT_REASON;
import tw.com.leadtek.nhiwidget.dao.IP_PDao;
import tw.com.leadtek.nhiwidget.dao.MRDao;
import tw.com.leadtek.nhiwidget.dao.OP_PDao;
import tw.com.leadtek.nhiwidget.dto.PtQualityServicePl;
import tw.com.leadtek.nhiwidget.dto.ptNhiNoTimes;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.service.IntelligentService;

@Service
public class PtQualityServiceServiceTask extends BasicIntelligentService{
	@Autowired
	private MRDao mrDao;

	@Autowired
	private IP_PDao ippDao;
	@Autowired
	private OP_PDao oppDao;

	@Autowired
	private IntelligentService intelligentService;
	
	private String Category = "品質支付服務費";

	@SuppressWarnings("unchecked")
	public void validQualityServic(PtQualityServicePl params) throws ParseException {
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
		/// 限定同患者每次申報此支付標準代碼，與前一次申報日間隔大於等於 日，方可申報
		if (params.getInterval_nday_enable() == 1) {
			
			this.Interval_nday_enable(params.getNhi_no(), params.getInterval_nday(), mrIdListStr, isOutpatien, isHospital);
		}

		/// 2.
		/// 限定同患者執行過 任一，大於等於 次，方可申報
		if (params.getCoexist_nhi_no_enable() == 1) {
			List<ptNhiNoTimes> coList = params.getLst_co_nhi_no();
			int count = 0;
			if(isHospital) {
				
				/// 住院
				for (ptNhiNoTimes model : coList) {
					List<Map<String, Object>> ippData = ippDao.getRocidTotalByDrugNoandMrid(model.getNhi_no(), mrIdListStr);
					if (count == 0) {
						if (ippData.size() > 0) {
							for (Map<String, Object> map : ippData) {
								int total = Integer.parseInt(map.get("TOTAL").toString());
								
								if (model.getTimes() < total) {
									MR mr = mrDao.getMrByRocIdAndCode(map.get("ROC_ID").toString(), model.getNhi_no(), mrIdListStr);
									intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
											params.getNhi_no(),
											String.format("(醫令代碼)%s與支付準則條件:限定同患者執行過%s任一，大於等於%d次，方可申報，疑似有出入",
													params.getNhi_no(), model.getNhi_no(), model.getTimes()),
											true);
								}
							}
							count++;
						}
					}
				}
			}
			if(isOutpatien) {
				
				/// 門診
				for (ptNhiNoTimes model : coList) {
					List<Map<String, Object>> oppData = oppDao.getRocidTotalByDrugNoandMrid(model.getNhi_no(), mrIdListStr);
					/// 進入條件為or，
					if (count == 0) {
						
						if (oppData.size() > 0) {
							for (Map<String, Object> map : oppData) {
								int total = Integer.parseInt(map.get("TOTAL").toString());
								
								if (model.getTimes() < total) {
									MR mr = mrDao.getMrByRocIdAndCode(map.get("ROC_ID").toString(), model.getNhi_no(), mrIdListStr);
									intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
											params.getNhi_no(),
											String.format("(醫令代碼)%s與支付準則條件:限定同患者執行過%s任一，大於等於%d次，方可申報，疑似有出入",
													params.getNhi_no(), model.getNhi_no(), model.getTimes()),
											true);
								}
							}
							count++;
						}
						
					}
					
				}
			}

		}

		/// 3.
		/// 限定同患者累積申報此支付標準代碼， 天內小於等於 次，方可申報
		if (params.getEvery_nday_enable() == 1) {
			Map<String, Object> m1 = new HashMap<String, Object>();
			if(isOutpatien) {
				List<Map<String, Object>> oppData = oppDao.getRocIdCount(params.getNhi_no(), mrIdListStr);
				List<Map<String, Object>> oppList = new ArrayList<Map<String, Object>>();
				List<Map<String, Object>> oppList2 = new ArrayList<Map<String, Object>>();
				
				/// 門診
				if (oppData.size() > 0) {
					int count = 0;
					/// 先理出最後2筆資料
					for (Map<String, Object> map : oppData) {
						
						String rocid = map.get("ROC_ID").toString();
						String sDate = map.get("START_TIME").toString();
						String eDate = map.get("END_TIME").toString();
						String mrid = map.get("MR_ID").toString();
						String total = map.get("TOTAL_Q").toString();
						
						if (count == 0) {
							m1.put("ROC_ID", rocid);
							m1.put("START_TIME", sDate);
							m1.put("END_TIME", eDate);
							m1.put("MR_ID", mrid);
							m1.put("TOTAL_Q", total);
							oppList.add(m1);
							m1 = new HashMap<String, Object>();
							count++;
						} else if (count == 1) {
							m1.put("ROC_ID", rocid);
							m1.put("START_TIME", sDate);
							m1.put("END_TIME", eDate);
							m1.put("TOTAL_Q", total);
							oppList2.add(m1);
							count++;
						} else {
							if (rocid.equals(m1.get("ROC_ID"))) {
								continue;
							} else {
								m1 = new HashMap<String, Object>();
								m1.put("ROC_ID", rocid);
								m1.put("START_TIME", sDate);
								m1.put("END_TIME", eDate);
								m1.put("MR_ID", mrid);
								m1.put("TOTAL_Q", total);
								oppList.add(m1);
								m1 = new HashMap<String, Object>();
								count = 1;
							}
						}
					}
					/// 相同rocid做日期比對
					for (Map<String, Object> map : oppList) {
						String rocid = map.get("ROC_ID").toString();
						String eDate = map.get("END_TIME").toString();
						String mrid = map.get("MR_ID").toString();
						String total = map.get("TOTAL_Q").toString();
						for (Map<String, Object> map2 : oppList2) {
							String rocid2 = map2.get("ROC_ID").toString();
							String eDate2 = map2.get("END_TIME").toString();
							String total2 = map2.get("TOTAL_Q").toString();
							if (rocid.equals(rocid2)) {
								float f = Float.valueOf(eDate);
								float f2 = Float.valueOf(eDate2);
								float diff = (f - f2) / 10000;
								///先判斷為日期內
								if (params.getEvery_nday_days() <= diff) {
									float tCount = Float.valueOf(total) + Float.valueOf(total2);
									///再判斷加總是否有超過條件
									if(params.getEvery_nday_times() < tCount) {
										
										MR mr = mrDao.getMrByID(mrid);
										intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
												params.getNhi_no(),
												String.format("(醫令代碼)%s與支付準則條件:限定同患者累積申報此支付標準代碼%d天內小於等於%d次，方可申報，疑似有出入",
														params.getNhi_no(), params.getEvery_nday_days(), params.getEvery_nday_times()),
												true);
									}
									
								}
								
							}
						}
					}
				}
			}
			if(isHospital) {
				
				List<Map<String, Object>> ippData = ippDao.getRocIdCount(params.getNhi_no(), mrIdListStr);
				List<Map<String, Object>> ippList = new ArrayList<Map<String, Object>>();
				List<Map<String, Object>> ippList2 = new ArrayList<Map<String, Object>>();
				m1 = new HashMap<String, Object>();
				/// 住院
				if (ippData.size() > 0) {
					int count = 0;
					/// 先理出最後2筆資料
					for (Map<String, Object> map : ippData) {
						
						String rocid = map.get("ROC_ID").toString();
						String sDate = map.get("START_TIME").toString();
						String eDate = map.get("END_TIME").toString();
						String mrid = map.get("MR_ID").toString();
						String total = map.get("TOTAL_Q").toString();
						if (count == 0) {
							m1.put("ROC_ID", rocid);
							m1.put("START_TIME", sDate);
							m1.put("END_TIME", eDate);
							m1.put("MR_ID", mrid);
							m1.put("TOTAL_Q", total);
							ippList.add(m1);
							m1 = new HashMap<String, Object>();
							count++;
						} else if (count == 1) {
							m1.put("ROC_ID", rocid);
							m1.put("START_TIME", sDate);
							m1.put("END_TIME", eDate);
							m1.put("MR_ID", mrid);
							m1.put("TOTAL_Q", total);
							ippList2.add(m1);
							count++;
						} else {
							if (rocid.equals(m1.get("ROC_ID"))) {
								continue;
							} else {
								m1 = new HashMap<String, Object>();
								m1.put("ROC_ID", rocid);
								m1.put("START_TIME", sDate);
								m1.put("END_TIME", eDate);
								m1.put("MR_ID", mrid);
								m1.put("TOTAL_Q", total);
								ippList.add(m1);
								m1 = new HashMap<String, Object>();
								count = 1;
							}
						}
					}
					/// 相同rocid做日期比對
					for (Map<String, Object> map : ippList) {
						String rocid = map.get("ROC_ID").toString();
						String eDate = map.get("END_TIME").toString();
						String mrid = map.get("MR_ID").toString();
						String total = map.get("TOTAL_Q").toString();
						for (Map<String, Object> map2 : ippList2) {
							String rocid2 = map2.get("ROC_ID").toString();
							String eDate2 = map2.get("END_TIME").toString();
							String total2 = map2.get("TOTAL_Q").toString();
							if (rocid.equals(rocid2)) {
								float f = Float.valueOf(eDate);
								float f2 = Float.valueOf(eDate2);
								float diff = (f - f2) / 10000;
								///先判斷為日期內
								if (params.getEvery_nday_days() <= diff) {
									float tCount = Float.valueOf(total) + Float.valueOf(total2);
									///再判斷加總是否有超過條件
									if(params.getEvery_nday_times() < tCount) {
										
										MR mr = mrDao.getMrByID(mrid);
										intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
												params.getNhi_no(),
												String.format("(醫令代碼)%s與支付準則條件:限定同患者累積申報此支付標準代碼%d天內小於等於%d次，方可申報，疑似有出入",
														params.getNhi_no(), params.getEvery_nday_days(), params.getEvery_nday_times()),
												true);
									}
								}
								
							}
						}
					}
				}
			}
            
		}
	}
}
