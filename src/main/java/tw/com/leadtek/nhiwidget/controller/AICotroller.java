package tw.com.leadtek.nhiwidget.controller;

import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import tw.com.leadtek.nhiwidget.dto.PtOutpatientFeePl;
import tw.com.leadtek.nhiwidget.service.AIService;
import tw.com.leadtek.nhiwidget.task.service.PtOutpatientFeeService;

@RestController
@RequestMapping("/auth")
public class AICotroller {
	
	@Autowired
	private AIService aiService;
	@Autowired
	private PtOutpatientFeeService ptOutpatientFeeService;

	@ResponseBody
	@RequestMapping(value = "/getClinicCostDiffData", method = { RequestMethod.GET, RequestMethod.POST })
	public Object getClinicCostDiffData(@RequestParam(required = true) String date) {
		return aiService.clinicCostDiff(date);
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
}
