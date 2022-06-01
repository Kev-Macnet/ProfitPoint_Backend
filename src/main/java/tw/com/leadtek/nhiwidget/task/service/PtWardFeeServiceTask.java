package tw.com.leadtek.nhiwidget.task.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.constant.INTELLIGENT_REASON;
import tw.com.leadtek.nhiwidget.dao.IP_PDao;
import tw.com.leadtek.nhiwidget.dao.MRDao;
import tw.com.leadtek.nhiwidget.dto.PtWardFeePl;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.service.IntelligentService;
import tw.com.leadtek.tools.DateTool;

@Service
public class PtWardFeeServiceTask extends BasicIntelligentService{

	@Autowired
	private MRDao mrDao;

	@Autowired
	private IP_PDao ippDao;

	@Autowired
	private IntelligentService intelligentService;

	private String Category = "病房費";

	@SuppressWarnings("unchecked")
	public void validWardFee(PtWardFeePl params) throws ParseException {

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
		/// 入住時間滿 小時，方可申報此支付標準代碼
		if (params.getMin_stay_enable() == 1) {
			List<Map<String, Object>> ippData = ippDao.getTimeListByMrid(mrIdListStr);
			String print = mrIdListStr.toString();
			for (Map<String, Object> map : ippData) {
				Date sDate = DateTool.convertChineseToYears(map.get("START_TIME").toString());
				Date eDate = DateTool.convertChineseToYears(map.get("END_TIME").toString());

				long diff = this.hourBetween(sDate, eDate);
				if (params.getMin_stay() > diff) {
					MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
					intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), params.getNhi_no(),
							String.format("(醫令代碼)%s與支付準則條件:入住時間滿%d小時，方可申報此支付標準代碼，疑似有出入", params.getNhi_no(),
									params.getMin_stay()),
							true);
				}
			}
		}

		/// 2.
		/// 入住時間超過 小時，方可申報此支付標準代碼
		if (params.getMax_stay_enable() == 1) {
			List<Map<String, Object>> ippData = ippDao.getTimeListByMrid(mrIdListStr);
			for (Map<String, Object> map : ippData) {
				Date sDate = DateTool.convertChineseToYears(map.get("START_TIME").toString());
				Date eDate = DateTool.convertChineseToYears(map.get("END_TIME").toString());
				long diff = this.hourBetween(sDate, eDate);
				if (params.getMax_stay() < diff) {
					MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
					intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), params.getNhi_no(),
							String.format("(醫令代碼)%s與支付準則條件:入住時間滿%d小時，不方可申報此支付標準代碼，疑似有出入", params.getNhi_no(),
									params.getMax_stay()),
							true);
				}
			}
		}

		/// 3.
		/// 不可與 支付代碼任一，並存單一就醫紀錄一併申報
		if (params.getExclude_nhi_no_enable() == 1) {

			this.Exclude_nhi_no_enable(params.getNhi_no(), params.getLst_nhi_no(), isOutpatien, isHospital, mrList);
		}

	}
}
