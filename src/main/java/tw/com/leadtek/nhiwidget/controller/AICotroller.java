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
import tw.com.leadtek.nhiwidget.dto.PtInpatientFeePl;
import tw.com.leadtek.nhiwidget.dto.PtNutritionalFeePl;
import tw.com.leadtek.nhiwidget.dto.PtOutpatientFeePl;
import tw.com.leadtek.nhiwidget.dto.PtSurgeryFeePl;
import tw.com.leadtek.nhiwidget.dto.PtTreatmentFeePl;
import tw.com.leadtek.nhiwidget.dto.PtWardFeePl;
import tw.com.leadtek.nhiwidget.service.AIService;
import tw.com.leadtek.nhiwidget.task.service.PtAdjustmentFeeServiceTask;
import tw.com.leadtek.nhiwidget.task.service.PtInpatientFeeServiceTask;
import tw.com.leadtek.nhiwidget.task.service.PtOutpatientFeeServiceTask;
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

	@ResponseBody
	@RequestMapping(value = "/getClinicCostDiffData", method = { RequestMethod.GET, RequestMethod.POST })
	public Object getClinicCostDiffData(@RequestParam(required = true) String sDate1,String eDate1,String sDate2,String eDate2) {
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
	public void vaidOutpatientFee(HttpServletRequest request,
	        @RequestBody PtOutpatientFeePl params) throws ParseException {
		 ptOutpatientFeeService.vaidOutpatientFee(params);
	}
	
	@ResponseBody
	@RequestMapping(value = "/vaidInpatientFee", method = { RequestMethod.GET, RequestMethod.POST })
	public void vaidInpatientFee(HttpServletRequest request,
	        @RequestBody PtInpatientFeePl params) throws ParseException {
		ptInpatientFeeService.validInpatienFee(params);
	}
	
	@ResponseBody
	@RequestMapping(value = "/vaidWardFeee", method = { RequestMethod.GET, RequestMethod.POST })
	public void vaidWardFeee(HttpServletRequest request,
	        @RequestBody PtWardFeePl params) throws ParseException {
		ptWardFeeService.validWardFee(params);
	}
	
	@ResponseBody
	@RequestMapping(value = "/vaidSurgeryFee", method = { RequestMethod.GET, RequestMethod.POST })
	public void vaidSurgeryFee(HttpServletRequest request,
	        @RequestBody PtSurgeryFeePl params) throws ParseException {
		ptSurgeryFeeService.validSurgeryFee(params);
	}
	
	@ResponseBody
	@RequestMapping(value = "/vaidTreatmentFee", method = { RequestMethod.GET, RequestMethod.POST })
	public void vaidTreatmentFee(HttpServletRequest request,
	        @RequestBody PtTreatmentFeePl params) throws ParseException {
		ptTreatmentFeeService.validTreatmentFee(params);
	}
	
	@ResponseBody
	@RequestMapping(value = "/vaidTubeFeedingFee", method = { RequestMethod.GET, RequestMethod.POST })
	public void vaidTubeFeedingFee(HttpServletRequest request,
	        @RequestBody PtNutritionalFeePl params) throws ParseException {
		ptTubeFeedingFeeService.validTubeFeedingFee(params);
	}
	
	@ResponseBody
	@RequestMapping(value = "/vaidAdjustmentFee", method = { RequestMethod.GET, RequestMethod.POST })
	public void vaidAdjustmentFee(HttpServletRequest request,
	        @RequestBody PtAdjustmentFeePl params) throws ParseException {
		ptAdjustmentFeeServic.validAdjustmentFee(params);
	}
}
