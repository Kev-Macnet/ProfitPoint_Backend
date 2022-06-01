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
import tw.com.leadtek.nhiwidget.dto.PtInjectionFeePl;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.service.IntelligentService;

@Service
public class PtInjectionFeeServiceTask extends BasicIntelligentService{
	@Autowired
	private MRDao mrDao;

	@Autowired
	private IP_PDao ippDao;
	@Autowired
	private OP_PDao oppDao;

	@Autowired
	private IntelligentService intelligentService;

	private String Category = "注射費";

	@SuppressWarnings("unchecked")
	public void validInjectionFee(PtInjectionFeePl params) throws ParseException {
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
		/// 限定同患者前一次應用與當次應用待申報此支付標準代碼，每次申報間隔大於等於 日
		if (params.getInterval_nday_enable() == 1) {
			
			this.Interval_nday_enable(params.getNhi_no(), params.getInterval_nday(), mrIdListStr, isOutpatien, isHospital);
		}

		/// 2.
		/// 不可與 任一，並存單一就醫紀錄一併申報
		if (params.getExclude_nhi_no_enable() == 1) {
			
			this.Exclude_nhi_no_enable(params.getNhi_no(), params.getLst_nhi_no(), isOutpatien, isHospital, mrList);
		}

		/// 3.
		/// 單一就醫紀錄應用數量,限定小於等於 次
		if (params.getMax_inpatient_enable() == 1) {
			
			this.Max_inpatient_enable(params.getNhi_no(), params.getMax_inpatient(),mrIdListStr, isOutpatien, isOutpatien);
			
		}

	}
}
