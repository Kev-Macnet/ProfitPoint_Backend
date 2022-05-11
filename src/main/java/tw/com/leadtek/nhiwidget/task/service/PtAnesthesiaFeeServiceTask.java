package tw.com.leadtek.nhiwidget.task.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
public class PtAnesthesiaFeeServiceTask extends BasicIntelligentService{

	@Autowired
	private MRDao mrDao;

	@Autowired
	private IP_PDao ippDao;
	@Autowired
	private OP_PDao oppDao;

	@Autowired
	private IntelligentService intelligentService;
	
	private String Category = "麻醉費";

	@SuppressWarnings("unchecked")
	public void validAnesthesiaFee(PtAnesthesiaFeePl params) throws ParseException {
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
		/// 單一就醫紀錄上，須包含以下任一DRG代碼
		if (params.getInclude_drg_no_enable() == 1) {
			List<String> icdList = params.getLst_drg_no();
			int count = 0;
			for (MR mr : mrList) {
				for (String s : icdList) {
					if (mr.getDrgCode() != null && !mr.getDrgCode().contains(s)) {

						if (count == 0) {
							if(isOutpatien && mr.getDataFormat().equals("10")) {
								intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
										params.getNhi_no(), String.format("(醫令代碼)%s與支付準則條件:單一就醫紀錄上，須包含以下任一DRG代碼[%s]，疑似有出入",
												params.getNhi_no(), icdList.toString()),
										true);
							}
							if(isHospital && mr.getDataFormat().equals("20")) {
								intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
										params.getNhi_no(), String.format("(醫令代碼)%s與支付準則條件:單一就醫紀錄上，須包含以下任一DRG代碼[%s]，疑似有出入",
												params.getNhi_no(), icdList.toString()),
										true);
							}
							
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
			
			this.Coexist_nhi_no_enable(params.getNhi_no(), params.getLst_co_nhi_no(), mrList, isOutpatien, isOutpatien);
			
		}

		/// 3.
		/// 單一就醫紀錄上，應用 大於等於 次時，首次執行須滿分鐘，方可進行下一次執行。後續每次執行需間隔超過 分鐘
		if (params.getOver_times_enable() == 1) {
		
			
			if(isOutpatien) 
			{
				List<Map<String, Object>> oppData = oppDao.getAllListByMrid(mrIdListStr);
				
				if (oppData.size() > 0) {
					for (Map<String, Object> opp : oppData) {
						float t = Float.valueOf(opp.get("TOTAL_Q").toString());
						if (params.getOver_times_n() >= t) {
							
							if (opp.get("DIFF") != null) {
								
								float time = Float.valueOf(opp.get("DIFF").toString());
								if (params.getOver_times_first_n() <= time) {
									MR mr = mrDao.getMrByID(opp.get("MR_ID").toString());
									intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
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

			if(isHospital) {
				List<Map<String, Object>> ippData = ippDao.getAllListByMrid(mrIdListStr);
				if (ippData.size() > 0) {
					for (Map<String, Object> ipp : ippData) {
						float t = Float.valueOf(ipp.get("TOTAL_Q").toString());
						if (params.getOver_times_n() >= Math.round(t)) {
							
							if (ipp.get("DIFF") != null) {
								
								float time = Float.valueOf(ipp.get("DIFF").toString());
								if (params.getOver_times_first_n() >= time) {
									MR mr = mrDao.getMrByID(ipp.get("MR_ID").toString());
									intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
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
		}

		/// 4.
		/// 限定特定科別應用
		if (params.getLim_division_enable() == 1) {
			
			this.Lim_division_enable(params.getNhi_no(), params.getLst_division(), mrIdListStr, isOutpatien, isHospital);
		}

	}
}
