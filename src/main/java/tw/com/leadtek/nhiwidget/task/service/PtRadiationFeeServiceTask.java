package tw.com.leadtek.nhiwidget.task.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.constant.INTELLIGENT_REASON;
import tw.com.leadtek.nhiwidget.dto.PtRadiationFeePl;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.service.IntelligentService;

@Service
public class PtRadiationFeeServiceTask extends BasicIntelligentService {
	
	@Autowired
	private IntelligentService intelligentService;

	private String Category = "放射線診療費";

	@SuppressWarnings("unchecked")
	public void validRadiationFee(PtRadiationFeePl params) throws ParseException {
		boolean isHospital = false;
		boolean isOutpatien = false;
		Map<String, Object> retMap = this.vaidIntelligentTtype(params.getStart_date(), params.getEnd_date(),
				params.getNhi_no(), params.getOutpatient_type(), params.getHospitalized_type());
		isHospital = (boolean) retMap.get("isHospital");
		isOutpatien = (boolean) retMap.get("isOutpatien");
		/// 存放病例
		List<MR> mrList = new ArrayList<MR>();
		mrList.addAll((Collection<? extends MR>) retMap.get("mrList"));
		/// 存放mrID
		List<String> mrIdListStr = new ArrayList<String>();
		mrIdListStr.addAll((Collection<? extends String>) retMap.get("mrIdListStr"));

		/// 1.
		/// 與 任一，並存單一就醫紀錄待申報時，需提示有無特別原由
		if (params.getNotify_nhi_no_enable() == 1) {
			List<String> notifyList = params.getLst_ntf_nhi_no();
			int count = 0;
			for (MR mr : mrList) {
				for (String s : notifyList) {

					/// 再判斷沒有符合
					if (mr.getCodeAll().contains(s)) {
						if (count == 0) {
							if (isHospital) {
								intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
										params.getNhi_no(),
										String.format("(醫令代碼)%s與支付準則條件:與[%s]任一，並存單一就醫紀錄待申報時，需提示有無特別原由，疑似有出入",
												params.getNhi_no(), notifyList.toString()),
										true);
							}
							if (isOutpatien) {
								intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
										params.getNhi_no(),
										String.format("(醫令代碼)%s與支付準則條件:與[%s]任一，並存單一就醫紀錄待申報時，需提示有無特別原由，疑似有出入",
												params.getNhi_no(), notifyList.toString()),
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
		/// 需與任一，並存於單一就醫紀錄，方可申報
		if (params.getCoexist_nhi_no_enable() == 1) {
			
			this.Coexist_nhi_no_enable(params.getNhi_no(), params.getLst_co_nhi_no(), mrList, isOutpatien, isOutpatien);
		}

		/// 3.
		/// 不可與 任一，並存單一就醫紀錄一併申報
		if (params.getExclude_nhi_no_enable() == 1) {

			this.Exclude_nhi_no_enable(params.getNhi_no(), params.getLst_nhi_no(), isOutpatien, isHospital, mrList);
		}
		/// 4.
		/// 單一就醫紀錄應用數量,限定小於等於 次
		if (params.getMax_inpatient_enable() == 1) {

			this.Max_inpatient_enable(params.getNhi_no(), params.getMax_inpatient(),mrIdListStr, isOutpatien, isOutpatien);

		}

	}
}
