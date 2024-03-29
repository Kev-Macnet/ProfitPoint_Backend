package tw.com.leadtek.nhiwidget.controller;

import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import tw.com.leadtek.nhiwidget.annotation.LogDefender;
import tw.com.leadtek.nhiwidget.constant.LogType;
import tw.com.leadtek.nhiwidget.dto.PtAdjustmentFeePl;
import tw.com.leadtek.nhiwidget.dto.PtAnesthesiaFeePl;
import tw.com.leadtek.nhiwidget.dto.PtBoneMarrowTransFeePl;
import tw.com.leadtek.nhiwidget.dto.PtInjectionFeePl;
import tw.com.leadtek.nhiwidget.dto.PtInpatientFeePl;
import tw.com.leadtek.nhiwidget.dto.PtMedicineFeePl;
import tw.com.leadtek.nhiwidget.dto.PtNutritionalFeePl;
import tw.com.leadtek.nhiwidget.dto.PtOthersFeePl;
import tw.com.leadtek.nhiwidget.dto.PtOutpatientFeePl;
import tw.com.leadtek.nhiwidget.dto.PtPsychiatricFeePl;
import tw.com.leadtek.nhiwidget.dto.PtQualityServicePl;
import tw.com.leadtek.nhiwidget.dto.PtRadiationFeePl;
import tw.com.leadtek.nhiwidget.dto.PtRehabilitationFeePl;
import tw.com.leadtek.nhiwidget.dto.PtSpecificMedicalFeePl;
import tw.com.leadtek.nhiwidget.dto.PtSurgeryFeePl;
import tw.com.leadtek.nhiwidget.dto.PtTreatmentFeePl;
import tw.com.leadtek.nhiwidget.dto.PtWardFeePl;
import tw.com.leadtek.nhiwidget.service.AIService;
import tw.com.leadtek.nhiwidget.task.service.PtAdjustmentFeeServiceTask;
import tw.com.leadtek.nhiwidget.task.service.PtAnesthesiaFeeServiceTask;
import tw.com.leadtek.nhiwidget.task.service.PtBoneMarrowTransFeeServiceTask;
import tw.com.leadtek.nhiwidget.task.service.PtInjectionFeeServiceTask;
import tw.com.leadtek.nhiwidget.task.service.PtInpatientFeeServiceTask;
import tw.com.leadtek.nhiwidget.task.service.PtMedicineFeeServiceTask;
import tw.com.leadtek.nhiwidget.task.service.PtOthersFeeServiceTask;
import tw.com.leadtek.nhiwidget.task.service.PtOutpatientFeeServiceTask;
import tw.com.leadtek.nhiwidget.task.service.PtPsychiatricFeeServiceTask;
import tw.com.leadtek.nhiwidget.task.service.PtQualityServiceServiceTask;
import tw.com.leadtek.nhiwidget.task.service.PtRadiationFeeServiceTask;
import tw.com.leadtek.nhiwidget.task.service.PtRehabilitationFeeServiceTask;
import tw.com.leadtek.nhiwidget.task.service.PtSpecificMedicalFeeServiceTask;
import tw.com.leadtek.nhiwidget.task.service.PtSurgeryFeeServiceTask;
import tw.com.leadtek.nhiwidget.task.service.PtTreatmentFeeServiceTask;
import tw.com.leadtek.nhiwidget.task.service.PtTubeFeedingFeeServiceTask;
import tw.com.leadtek.nhiwidget.task.service.PtWardFeeServiceTask;

@RestController
@RequestMapping("/auth")
public class AICotroller {

	@Autowired
	private AIService aiService;
	@Autowired
	private PtOutpatientFeeServiceTask ptOutpatientFeeService;
	@Autowired
	private PtInpatientFeeServiceTask ptInpatientFeeService;
	@Autowired
	private PtWardFeeServiceTask ptWardFeeService;
	@Autowired
	private PtSurgeryFeeServiceTask ptSurgeryFeeService;
	@Autowired
	private PtTreatmentFeeServiceTask ptTreatmentFeeService;
	@Autowired
	private PtTubeFeedingFeeServiceTask ptTubeFeedingFeeService;
	@Autowired
	private PtAdjustmentFeeServiceTask ptAdjustmentFeeServic;
	@Autowired
	private PtMedicineFeeServiceTask ptMedicineFeeService;
	@Autowired
	private PtRadiationFeeServiceTask ptRadiationFeeService;
	@Autowired
	private PtInjectionFeeServiceTask ptInjectionFeeService;
	@Autowired
	private PtQualityServiceServiceTask ptQualityServiceService;
	@Autowired
	private PtRehabilitationFeeServiceTask ptRehabilitationFeeService;
	@Autowired
	private PtPsychiatricFeeServiceTask PtPsychiatricFeeServic;
	@Autowired
	private PtBoneMarrowTransFeeServiceTask ptBoneMarrowTransFeeService;
	@Autowired
	private PtAnesthesiaFeeServiceTask ptAnesthesiaFeeService;
	@Autowired
	private PtSpecificMedicalFeeServiceTask ptSpecificMedicalFeeService;
	@Autowired
	private PtOthersFeeServiceTask ptOthersFeeService;

	/**
	 * 費用差異-門診
	 * 
	 * @param sDate1
	 * @param eDate1
	 * @param sDate2
	 * @param eDate2
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getClinicCostDiffData", method = { RequestMethod.GET, RequestMethod.POST })
	@LogDefender(value = {LogType.SIGNIN})
	public Object getClinicCostDiffData(@RequestParam(required = true) String sDate1, String eDate1, String sDate2,
			String eDate2) {
		return aiService.clinicCostDiff(sDate1, eDate1, sDate2, eDate2);
	}

	/**
	 * 費用差異-住院
	 * 
	 * @param sDate1
	 * @param eDate1
	 * @param sDate2
	 * @param eDate2
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getHospitalCostDiffData", method = { RequestMethod.GET, RequestMethod.POST })
	@LogDefender(value = {LogType.SIGNIN})
	public Object getHospitalCostDiffData(@RequestParam(required = true) String sDate1, String eDate1, String sDate2,
			String eDate2) {
		return aiService.hospitalCostDiff(sDate1, eDate1, sDate2, eDate2);
	}

	/**
	 * 醫療行為差異-門診
	 * 
	 * @param sDate
	 * @param eDate
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getClinicMmedBehDiff", method = { RequestMethod.GET, RequestMethod.POST })
	@LogDefender(value = {LogType.SIGNIN})
	public Object getClinicMmedBehDiff(@RequestParam(required = true) String sDate, String eDate) {
		return aiService.clinicMmedBehDiff(sDate, eDate);
	}

	/**
	 * 醫療行為差異-住院
	 * 
	 * @param sDate
	 * @param eDate
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getHospitalMmedBehDiff", method = { RequestMethod.GET, RequestMethod.POST })
	@LogDefender(value = {LogType.SIGNIN})
	public Object getHospitalMmedBehDiff(@RequestParam(required = true) String sDate, String eDate) {
		return aiService.hospitalMmedBehDiff(sDate, eDate);
	}

	/**
	 * 醫療行為差異-門診手術
	 * 
	 * @param sDate
	 * @param eDate
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getClinicOperation", method = { RequestMethod.GET, RequestMethod.POST })
	@LogDefender(value = {LogType.SIGNIN})
	public Object getClinicOperation(@RequestParam(required = true) String sDate, String eDate) {
		return aiService.clinicOperaiton(sDate, eDate);
	}

	/**
	 * 醫療行為差異-住院手術
	 * 
	 * @param sDate
	 * @param eDate
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getHospitalOperation", method = { RequestMethod.GET, RequestMethod.POST })
	@LogDefender(value = {LogType.SIGNIN})
	public Object getHospitalOperation(@RequestParam(required = true) String sDate, String eDate) {
		return aiService.hospitalOperaiton(sDate, eDate);
	}

	/**
	 * 用藥差異-門診
	 * 
	 * @param sDate
	 * @param eDate
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getClinicMedicine", method = { RequestMethod.GET, RequestMethod.POST })
	@LogDefender(value = {LogType.SIGNIN})
	public Object getClinicMedicine(@RequestParam(required = true) String sDate, String eDate) {
		return aiService.clinicMedicine(sDate, eDate);
	};

	/**
	 * 用藥差異-住院
	 * 
	 * @param sDate
	 * @param eDate
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getHospitalMedicine", method = { RequestMethod.GET, RequestMethod.POST })
	@LogDefender(value = {LogType.SIGNIN})
	public Object getHospitalMedicine(@RequestParam(required = true) String sDate, String eDate) {
		return aiService.hospitalMedicine(sDate, eDate);
	};
    /**
     * 住院天數差異
 	 * @param sDate
	 * @param eDate
     * @return
     */
	@ResponseBody
	@RequestMapping(value = "/getHospitalDays", method = { RequestMethod.GET, RequestMethod.POST })
	@LogDefender(value = {LogType.SIGNIN})
	public Object getHospitalDays(@RequestParam(required = true) String sDate, String eDate) {
		return aiService.hospitalDays(sDate, eDate);
	}
	/**
	 * 支付條件準則-門診診察費
	 * 
	 * @param request
	 * @param params
	 * @throws ParseException
	 */
	@ResponseBody
	@RequestMapping(value = "/vaidOutpatientFee", method = { RequestMethod.GET, RequestMethod.POST })
	@LogDefender(value = {LogType.SIGNIN})
	public void vaidOutpatientFee(HttpServletRequest request, @RequestBody PtOutpatientFeePl params)
			throws ParseException {
		ptOutpatientFeeService.vaidOutpatientFee(params);
	}

	/**
	 * 支付條件準則-住院診察費
	 * @param request
	 * @param params
	 * @throws ParseException
	 */
	@ResponseBody
	@RequestMapping(value = "/vaidInpatientFee", method = { RequestMethod.GET, RequestMethod.POST })
	@LogDefender(value = {LogType.SIGNIN})
	public void vaidInpatientFee(HttpServletRequest request, @RequestBody PtInpatientFeePl params)
			throws ParseException {
		ptInpatientFeeService.validInpatienFee(params);
	}

	/**
	 * 支付條件準則-病房費
	 * 
	 * @param request
	 * @param params
	 * @throws ParseException
	 */
	@ResponseBody
	@RequestMapping(value = "/vaidWardFeee", method = { RequestMethod.GET, RequestMethod.POST })
	@LogDefender(value = {LogType.SIGNIN})
	public void vaidWardFeee(HttpServletRequest request, @RequestBody PtWardFeePl params) throws ParseException {
		ptWardFeeService.validWardFee(params);
	}

	/**
	 * 支付條件準則-手術費
	 * @param request
	 * @param params
	 * @throws ParseException
	 */
	@ResponseBody
	@RequestMapping(value = "/vaidSurgeryFee", method = { RequestMethod.GET, RequestMethod.POST })
	@LogDefender(value = {LogType.SIGNIN})
	public void vaidSurgeryFee(HttpServletRequest request, @RequestBody PtSurgeryFeePl params) throws ParseException {
		ptSurgeryFeeService.validSurgeryFee(params);
	}
	/**
	 * 支付條件準則-治療處置費
	 * @param request
	 * @param params
	 * @throws ParseException
	 */
	@ResponseBody
	@RequestMapping(value = "/vaidTreatmentFee", method = { RequestMethod.GET, RequestMethod.POST })
	@LogDefender(value = {LogType.SIGNIN})
	public void vaidTreatmentFee(HttpServletRequest request, @RequestBody PtTreatmentFeePl params)
			throws ParseException {
		ptTreatmentFeeService.validTreatmentFee(params);
	}
	/**
	 * 支付條件準則-管灌飲食與營養照護費
	 * @param request
	 * @param params
	 * @throws ParseException
	 */
	@ResponseBody
	@RequestMapping(value = "/vaidTubeFeedingFee", method = { RequestMethod.GET, RequestMethod.POST })
	@LogDefender(value = {LogType.SIGNIN})
	public void vaidTubeFeedingFee(HttpServletRequest request, @RequestBody PtNutritionalFeePl params)
			throws ParseException {
		ptTubeFeedingFeeService.validTubeFeedingFee(params);
	}
	/**
	 * 支付條件準則-調劑費
	 * @param request
	 * @param params
	 * @throws ParseException
	 */
	@ResponseBody
	@RequestMapping(value = "/vaidAdjustmentFee", method = { RequestMethod.GET, RequestMethod.POST })
	@LogDefender(value = {LogType.SIGNIN})
	public void vaidAdjustmentFee(HttpServletRequest request, @RequestBody PtAdjustmentFeePl params)
			throws ParseException {
		ptAdjustmentFeeServic.validAdjustmentFee(params);
	}
	/**
	 * 支付條件準則-藥費
	 * @param request
	 * @param params
	 * @throws ParseException
	 */
	@ResponseBody
	@RequestMapping(value = "/validMedicineFee", method = { RequestMethod.GET, RequestMethod.POST })
	@LogDefender(value = {LogType.SIGNIN})
	public void validMedicineFee(HttpServletRequest request, @RequestBody PtMedicineFeePl params)
			throws ParseException {
		ptMedicineFeeService.validMedicineFee(params);
	}
	/**
	 * 支付條件準則-放射線診療費
	 * @param request
	 * @param params
	 * @throws ParseException
	 */
	@ResponseBody
	@RequestMapping(value = "/validRadiationFee", method = { RequestMethod.GET, RequestMethod.POST })
	@LogDefender(value = {LogType.SIGNIN})
	public void validRadiationFee(HttpServletRequest request, @RequestBody PtRadiationFeePl params)
			throws ParseException {
		ptRadiationFeeService.validRadiationFee(params);
	}
	/**
	 * 支付條件準則-注射費
	 * @param request
	 * @param params
	 * @throws ParseException
	 */
	@ResponseBody
	@RequestMapping(value = "/validInjectionFee", method = { RequestMethod.GET, RequestMethod.POST })
	@LogDefender(value = {LogType.SIGNIN})
	public void validInjectionFee(HttpServletRequest request, @RequestBody PtInjectionFeePl params)
			throws ParseException {
		ptInjectionFeeService.validInjectionFee(params);
	}
	
	/**
	 * 支付條件準則-品質支付服務費
	 * @param request
	 * @param params
	 * @throws ParseException
	 */
	@ResponseBody
	@RequestMapping(value = "/validQualityServic", method = { RequestMethod.GET, RequestMethod.POST })
	@LogDefender(value = {LogType.SIGNIN})
	public void validQualityServic(HttpServletRequest request, @RequestBody PtQualityServicePl params)
			throws ParseException {
		ptQualityServiceService.validQualityServic(params);
	}
	/**
	 * 支付條件準則-復健治療費
	 * @param request
	 * @param params
	 * @throws ParseException
	 */
	@ResponseBody
	@RequestMapping(value = "/validRehabilitationFee", method = { RequestMethod.GET, RequestMethod.POST })
	@LogDefender(value = {LogType.SIGNIN})
	public void validRehabilitationFee(HttpServletRequest request, @RequestBody PtRehabilitationFeePl params)
			throws ParseException {
		ptRehabilitationFeeService.validRehabilitationFee(params);
	}
	/**
	 * 支付條件準則-精神醫療治療費
	 * @param request
	 * @param params
	 * @throws ParseException
	 */
	@ResponseBody
	@RequestMapping(value = "/validPsychiatricFee", method = { RequestMethod.GET, RequestMethod.POST })
	@LogDefender(value = {LogType.SIGNIN})
	public void validPsychiatricFee(HttpServletRequest request, @RequestBody PtPsychiatricFeePl params)
			throws ParseException {
		PtPsychiatricFeeServic.validPsychiatricFee(params);
	}
	/**
	 * 支付條件準則-輸血及骨髓移植費
	 * @param request
	 * @param params
	 * @throws ParseException
	 */
	@ResponseBody
	@RequestMapping(value = "/validBoneMarrowTransFee", method = { RequestMethod.GET, RequestMethod.POST })
	@LogDefender(value = {LogType.SIGNIN})
	public void validBoneMarrowTransFee(HttpServletRequest request, @RequestBody PtBoneMarrowTransFeePl params)
			throws ParseException {
		ptBoneMarrowTransFeeService.validBoneMarrowTransFee(params);
	}
	/**
	 * 支付條件準則-麻醉費
	 * @param request
	 * @param params
	 * @throws ParseException
	 */
	@ResponseBody
	@RequestMapping(value = "/validAnesthesiaFee", method = { RequestMethod.GET, RequestMethod.POST })
	@LogDefender(value = {LogType.SIGNIN})
	public void validAnesthesiaFee(HttpServletRequest request, @RequestBody PtAnesthesiaFeePl params)
			throws ParseException {
		ptAnesthesiaFeeService.validAnesthesiaFee(params);
	}
	/**
	 * 支付條件準則-特定診療檢查費
	 * @param request
	 * @param params
	 * @throws ParseException
	 */
	@ResponseBody
	@RequestMapping(value = "/validSpecificMedicalFee", method = { RequestMethod.GET, RequestMethod.POST })
	@LogDefender(value = {LogType.SIGNIN})
	public void validSpecificMedicalFee(HttpServletRequest request, @RequestBody PtSpecificMedicalFeePl params)
			throws ParseException {
		ptSpecificMedicalFeeService.validSpecificMedicalFee(params);
	}
	/**
	 * 支付條件準則-不分類
	 * @param request
	 * @param params
	 * @throws ParseException
	 */
	@ResponseBody
	@RequestMapping(value = "/validOthersFee", method = { RequestMethod.GET, RequestMethod.POST })
	@LogDefender(value = {LogType.SIGNIN})
	public void validOthersFee(HttpServletRequest request, @RequestBody PtOthersFeePl params) throws ParseException {
		ptOthersFeeService.validOthersFee(params);
	}

}
