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
import tw.com.leadtek.nhiwidget.dto.PtRehabilitationFeePl;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.service.IntelligentService;

@Service
public class PtRehabilitationFeeServiceTask extends BasicIntelligentService{

	@Autowired
	private IntelligentService intelligentService;

	private String Category = "復健治療費";

	@SuppressWarnings("unchecked")
	public void validRehabilitationFee(PtRehabilitationFeePl params) throws ParseException {
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
		

		/// .1
		/// 不可與 任一，並存單一就醫紀錄一併申報
		if (params.getExclude_nhi_no_enable() == 1) {
			
			this.Exclude_nhi_no_enable(params.getNhi_no(), params.getLst_nhi_no(), isOutpatien, isHospital, mrList);
		}

		/// 2.
		/// 同患者限定每小於等於 日，僅能待申報 次(可累績申報)
		if (params.getPatient_nday_enable() == 1) {
			
			this.Patient_nday_enableT(params.getNhi_no(), params.getPatient_nday_days(), params.getPatient_nday_times(), mrIdListStr, isOutpatien, isHospital);
		}

		/// 3.
		/// 單一就醫紀錄上，須包含以下任一ICD診斷碼
		if (params.getInclude_icd_no_enable() == 1) {
			this.Include_icd_no_enable(params.getNhi_no(), params.getLst_icd_no(), mrList, isOutpatien, isHospital);
		}

		/// 4.
		/// 限定同患者執行過 ，並在小於等於，並在小於等於 日內，申報指支付標準代碼
		if (params.getCoexist_nhi_no_enable() == 1) {
			List<String> coList = params.getLst_co_nhi_no();
			List<MR> mrAppendList = new ArrayList<MR>();
			List<MR> mrAppendList2 = new ArrayList<MR>();
			int count = 0;
			for (MR mr : mrList) {
				for (String s : coList) {
					/// 先判斷有相同支付標準
					if (mr.getCodeAll().contains(params.getNhi_no())) {
						/// 再判斷沒有符合
						if (mr.getCodeAll().contains(s)) {

							if (count == 0) {
								mrAppendList.add(mr);
								count++;

							} else if (count == 1) {
								mrAppendList2.add(mr);
							}

						}
					}
					count = 0;
				}
			}
			count = 0;
			if (mrAppendList.size() > 0) {
				for (MR mr : mrAppendList) {
					for (MR mr2 : mrAppendList2) {
						if (mr.getRocId() == mr2.getRocId()) {

							Date d1 = mr.getMrDate();
							Date d2 = mr2.getMrDate();
							/// 當次與上次日期相減
							long diff = this.dayBetween(d1, d2);
							/// 如果日期大於指定天數
							if (params.getMin_coexist() < diff) {
								if (isOutpatien && mr.getDataFormat().equals("10")) {
									intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
											params.getNhi_no(),
											String.format("(醫令代碼)%s與支付準則條件:限定同患者執行過[%s]，並在小於等於%d日內，申報指支付標準代碼，疑似有出入",
													params.getNhi_no(), coList.toString(), params.getMin_coexist()),
											true);
								}
								if (isHospital && mr.getDataFormat().equals("20")) {
									intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
											params.getNhi_no(),
											String.format("(醫令代碼)%s與支付準則條件:限定同患者執行過[%s]，並在小於等於%d日內，申報指支付標準代碼，疑似有出入",
													params.getNhi_no(), coList.toString(), params.getMin_coexist()),
											true);
								}

							}

						}
					}
				}
			}
		}

		/// 5.
		/// 限定特定科別應用
		if (params.getLim_division_enable() == 1) {
			this.Lim_division_enable(params.getNhi_no(), params.getLst_division(), mrIdListStr, isOutpatien, isHospital);
		}
	}
}
