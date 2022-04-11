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

import tw.com.leadtek.nhiwidget.dto.PtAdjustmentFeePl;
import tw.com.leadtek.nhiwidget.dto.PtBoneMarrowTransFeePl;
import tw.com.leadtek.nhiwidget.dto.PtInjectionFeePl;
import tw.com.leadtek.nhiwidget.dto.PtInpatientFeePl;
import tw.com.leadtek.nhiwidget.dto.PtMedicineFeePl;
import tw.com.leadtek.nhiwidget.dto.PtNutritionalFeePl;
import tw.com.leadtek.nhiwidget.dto.PtOutpatientFeePl;
import tw.com.leadtek.nhiwidget.dto.PtPsychiatricFeePl;
import tw.com.leadtek.nhiwidget.dto.PtQualityServicePl;
import tw.com.leadtek.nhiwidget.dto.PtRadiationFeePl;
import tw.com.leadtek.nhiwidget.dto.PtRehabilitationFeePl;
import tw.com.leadtek.nhiwidget.dto.PtSurgeryFeePl;
import tw.com.leadtek.nhiwidget.dto.PtTreatmentFeePl;
import tw.com.leadtek.nhiwidget.dto.PtWardFeePl;
import tw.com.leadtek.nhiwidget.service.AIService;
import tw.com.leadtek.nhiwidget.task.service.PtAdjustmentFeeServiceTask;
import tw.com.leadtek.nhiwidget.task.service.PtBoneMarrowTransFeeServiceTask;
import tw.com.leadtek.nhiwidget.task.service.PtInjectionFeeServiceTask;
import tw.com.leadtek.nhiwidget.task.service.PtInpatientFeeServiceTask;
import tw.com.leadtek.nhiwidget.task.service.PtMedicineFeeServiceTask;
import tw.com.leadtek.nhiwidget.task.service.PtOutpatientFeeServiceTask;
import tw.com.leadtek.nhiwidget.task.service.PtPsychiatricFeeServiceTask;
import tw.com.leadtek.nhiwidget.task.service.PtQualityServiceServiceTask;
import tw.com.leadtek.nhiwidget.task.service.PtRadiationFeeServiceTask;
import tw.com.leadtek.nhiwidget.task.service.PtRehabilitationFeeServiceTask;
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

	@ResponseBody
	@RequestMapping(value = "/getClinicCostDiffData", method = { RequestMethod.GET, RequestMethod.POST })
	public Object getClinicCostDiffData(@RequestParam(required = true) String sDate1, String eDate1, String sDate2,
			String eDate2) {
		return aiService.clinicCostDiff(sDate1, eDate1, sDate2, eDate2);
	}

	@ResponseBody
	@RequestMapping(value = "/getHospitalCostDiffData", method = { RequestMethod.GET, RequestMethod.POST })
	public Object getHospitalCostDiffData(@RequestParam(required = true) String date) {
		return aiService.hospitalCostDiff(date);
	}

	@ResponseBody
	@RequestMapping(value = "/getClinicMmedBehDiff", method = { RequestMethod.GET, RequestMethod.POST })
	public Object getClinicMmedBehDiff(@RequestParam(required = true) String date) {
		return aiService.clinicMmedBehDiff(date);
	}

	@ResponseBody
	@RequestMapping(value = "/getHospitalMmedBehDiff", method = { RequestMethod.GET, RequestMethod.POST })
	public Object getHospitalMmedBehDiff(@RequestParam(required = true) String date) {
		return aiService.hospitalMmedBehDiff(date);
	}

	@ResponseBody
	@RequestMapping(value = "/getClinicOperation", method = { RequestMethod.GET, RequestMethod.POST })
	public Object getClinicOperation(@RequestParam(required = true) String date) {
		return aiService.clinicOperaiton(date);
	}

	@ResponseBody
	@RequestMapping(value = "/getHospitalOperation", method = { RequestMethod.GET, RequestMethod.POST })
	public Object getHospitalOperation(@RequestParam(required = true) String date) {
		return aiService.hospitalOperaiton(date);
	}

	@ResponseBody
	@RequestMapping(value = "/getClinicMedicine", method = { RequestMethod.GET, RequestMethod.POST })
	public Object getClinicMedicine(@RequestParam(required = true) String date) {
		return aiService.clinicMedicine(date);
	};

	@ResponseBody
	@RequestMapping(value = "/getHospitalMedicine", method = { RequestMethod.GET, RequestMethod.POST })
	public Object getHospitalMedicine(@RequestParam(required = true) String date) {
		return aiService.hospitalMedicine(date);
	};

	@ResponseBody
	@RequestMapping(value = "/getHospitalDays", method = { RequestMethod.GET, RequestMethod.POST })
	public Object getHospitalDays(@RequestParam(required = true) String date) {
		return aiService.hospitalDays(date);
	}

	@ResponseBody
	@RequestMapping(value = "/vaidOutpatientFee", method = { RequestMethod.GET, RequestMethod.POST })
	public void vaidOutpatientFee(HttpServletRequest request, @RequestBody PtOutpatientFeePl params)
			throws ParseException {
		ptOutpatientFeeService.vaidOutpatientFee(params);
	}

	@ResponseBody
	@RequestMapping(value = "/vaidInpatientFee", method = { RequestMethod.GET, RequestMethod.POST })
	public void vaidInpatientFee(HttpServletRequest request, @RequestBody PtInpatientFeePl params)
			throws ParseException {
		ptInpatientFeeService.validInpatienFee(params);
	}

	@ResponseBody
	@RequestMapping(value = "/vaidWardFeee", method = { RequestMethod.GET, RequestMethod.POST })
	public void vaidWardFeee(HttpServletRequest request, @RequestBody PtWardFeePl params) throws ParseException {
		ptWardFeeService.validWardFee(params);
	}

	@ResponseBody
	@RequestMapping(value = "/vaidSurgeryFee", method = { RequestMethod.GET, RequestMethod.POST })
	public void vaidSurgeryFee(HttpServletRequest request, @RequestBody PtSurgeryFeePl params) throws ParseException {
		ptSurgeryFeeService.validSurgeryFee(params);
	}

	@ResponseBody
	@RequestMapping(value = "/vaidTreatmentFee", method = { RequestMethod.GET, RequestMethod.POST })
	public void vaidTreatmentFee(HttpServletRequest request, @RequestBody PtTreatmentFeePl params)
			throws ParseException {
		ptTreatmentFeeService.validTreatmentFee(params);
	}

	@ResponseBody
	@RequestMapping(value = "/vaidTubeFeedingFee", method = { RequestMethod.GET, RequestMethod.POST })
	public void vaidTubeFeedingFee(HttpServletRequest request, @RequestBody PtNutritionalFeePl params)
			throws ParseException {
		ptTubeFeedingFeeService.validTubeFeedingFee(params);
	}

	@ResponseBody
	@RequestMapping(value = "/vaidAdjustmentFee", method = { RequestMethod.GET, RequestMethod.POST })
	public void vaidAdjustmentFee(HttpServletRequest request, @RequestBody PtAdjustmentFeePl params)
			throws ParseException {
		ptAdjustmentFeeServic.validAdjustmentFee(params);
	}

	@ResponseBody
	@RequestMapping(value = "/validMedicineFee", method = { RequestMethod.GET, RequestMethod.POST })
	public void validMedicineFee(HttpServletRequest request, @RequestBody PtMedicineFeePl params)
			throws ParseException {
		ptMedicineFeeService.validMedicineFee(params);
	}

	@ResponseBody
	@RequestMapping(value = "/validRadiationFee", method = { RequestMethod.GET, RequestMethod.POST })
	public void validRadiationFee(HttpServletRequest request, @RequestBody PtRadiationFeePl params)
			throws ParseException {
		ptRadiationFeeService.validRadiationFee(params);
	}

	@ResponseBody
	@RequestMapping(value = "/validInjectionFee", method = { RequestMethod.GET, RequestMethod.POST })
	public void validInjectionFee(HttpServletRequest request, @RequestBody PtInjectionFeePl params)
			throws ParseException {
		ptInjectionFeeService.validInjectionFee(params);
	}

	@ResponseBody
	@RequestMapping(value = "/validQualityServic", method = { RequestMethod.GET, RequestMethod.POST })
	public void validQualityServic(HttpServletRequest request, @RequestBody PtQualityServicePl params)
			throws ParseException {
		ptQualityServiceService.validQualityServic(params);
	}

	@ResponseBody
	@RequestMapping(value = "/validRehabilitationFee", method = { RequestMethod.GET, RequestMethod.POST })
	public void validRehabilitationFee(HttpServletRequest request, @RequestBody PtRehabilitationFeePl params)
			throws ParseException {
		ptRehabilitationFeeService.validRehabilitationFee(params);
	}
	
	@ResponseBody
	@RequestMapping(value = "/validPsychiatricFee", method = { RequestMethod.GET, RequestMethod.POST })
	public void validPsychiatricFee(HttpServletRequest request, @RequestBody PtPsychiatricFeePl params)
			throws ParseException {
		PtPsychiatricFeeServic.validPsychiatricFee(params);
	}
	
	@ResponseBody
	@RequestMapping(value = "/validBoneMarrowTransFee", method = { RequestMethod.GET, RequestMethod.POST })
	public void validBoneMarrowTransFee(HttpServletRequest request, @RequestBody PtBoneMarrowTransFeePl params)
			throws ParseException {
		ptBoneMarrowTransFeeService.validBoneMarrowTransFee(params);
	}

}
