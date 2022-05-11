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
import tw.com.leadtek.nhiwidget.dto.PtNutritionalFeePl;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.service.IntelligentService;

@Service
public class PtTubeFeedingFeeServiceTask extends BasicIntelligentService{
	@Autowired
	private MRDao mrDao;

	@Autowired
	private IP_PDao ippDao;

	@Autowired
	private IntelligentService intelligentService;
	
	private String Category = "管灌飲食與營養照護費";
	
	@SuppressWarnings("unchecked")
	public void validTubeFeedingFee(PtNutritionalFeePl params) throws ParseException {
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
		/// 單一住院就醫紀錄應用數量,限定小於等於 
		if(params.getMax_inpatient_enable() == 1) {
			
			this.Max_inpatient_enable(params.getNhi_no(), params.getMax_inpatient(),mrIdListStr, isOutpatien, isOutpatien);
		}
		
		/// 2.
		///單一就醫紀錄上，每日限定應用 小於等於
		if (params.getMax_daily_enable() == 1) {
			this.Max_daily_enable(params.getNhi_no(), params.getMax_daily(), mrIdListStr, isOutpatien, isHospital);
		}
		
		/// 3.
		///單一就醫紀錄上，每 日內，限定應用小於等於 次
		if (params.getEvery_nday_enable() == 1) {

			this.Every_nday_enable(params.getNhi_no(), params.getEvery_nday_days(), params.getEvery_nday_times(), mrIdListStr, isOutpatien, isHospital);
		}
		
		/// 4.
		///單一就醫紀錄上，超過 日後，超出天數部份，限定應用 小於等於 次
		if(params.getOver_nday_enable() == 1) {
			List<Map<String, Object>> ippData = ippDao.getListOverByOrderCodeAndMridAndDays(params.getOver_nday_days(),params.getNhi_no(), mrIdListStr);
			if(ippData.size() > 0) {
				for (Map<String, Object> map : ippData) {
					float t = Float.parseFloat(map.get("TOTAL").toString());
					/// 如果資料大於限定值
					if (params.getOver_nday_times() < t) {
						MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), params.getNhi_no(),
								String.format("(醫令代碼)%s與支付準則條件:單一就醫紀錄上，超過%d日後，超出天數部份，限定應用小於等於%d次，疑似有出入", params.getNhi_no(),
										params.getOver_nday_days(),params.getOver_nday_times()),
								true);
					}
				}
			}
		}
		
		/// 5.
		///不可與  任一，並存單一就醫紀錄一併申報
		if(params.getExclude_nhi_no_enable() ==1) {
			
			this.Exclude_nhi_no_enable(params.getNhi_no(), params.getLst_nhi_no(), isOutpatien, isHospital, mrList);
		}
	}
}
