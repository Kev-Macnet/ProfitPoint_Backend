package tw.com.leadtek.nhiwidget.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import tw.com.leadtek.nhiwidget.dto.PtInjectionFeePl;
import tw.com.leadtek.nhiwidget.dto.PtQualityServicePl;
import tw.com.leadtek.nhiwidget.dto.PtRadiationFeePl;
import tw.com.leadtek.nhiwidget.dto.PtTreatmentFeeDto;
import tw.com.leadtek.nhiwidget.dto.PtWardFeeDto;
import tw.com.leadtek.nhiwidget.dto.PtWardFeePl;
import tw.com.leadtek.nhiwidget.service.PaymentTermsService;
import tw.com.leadtek.nhiwidget.service.PtInjectionFeeService;
import tw.com.leadtek.nhiwidget.service.PtQualityServiceService;
import tw.com.leadtek.nhiwidget.service.PtRadiationFeeService;


@Api(value = "健保標準給付額 支付條件設定 API", tags = {"10-3 健保標準給付額 支付條件設定"})
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class E2101cControll {
    
    @Autowired
    private PaymentTermsService paymentTermsService;
    @Autowired
    private PtInjectionFeeService ptInjectionFeeService;
    @Autowired
    private PtRadiationFeeService ptRadiationFeeService;
    @Autowired
    private PtQualityServiceService ptQualityServiceService;
    
    //==== 放射線診療費 Radiation Fee
    @ApiOperation(value="10-3.01 放射線診療費設定(get)", notes="")
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }", response=PtTreatmentFeeDto.class)
    })
    @RequestMapping(value = "/payment/radiationfee/{pt_id}", method = RequestMethod.POST)
    public ResponseEntity<?> getPaymentRadiationfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.Map<String, Object> retMap = ptRadiationFeeService.findRadiationFee(pt_id);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-3.02 放射線診療費設定(add)", notes="category = \"放射線診療費\"")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/radiationfee/add", method = RequestMethod.POST)
    public ResponseEntity<?> addPaymentRadiationfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @RequestBody PtRadiationFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = 0;
            long ptId = ptRadiationFeeService.addRadiationFee(params);
            if (ptId<=0) {
                status = -1;
            }
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status==0) {
                retMap.put("message", "新增成功。/id="+ptId);
            } else {
                retMap.put("message", "新增失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-3.03 放射線診療費設定(update)", notes="<b>category 無法變更</b>")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/radiationfee/{pt_id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePaymentRadiationfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id,
        @RequestBody PtRadiationFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = ptRadiationFeeService.updateRadiationFee(pt_id, params);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
                retMap.put("message", "修改成功。/id="+pt_id);
            } else {
                retMap.put("message", "修改失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-3.04 放射線診療費設定(delete)", notes="")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/radiationfee/{pt_id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePaymentRadiationfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = ptRadiationFeeService.deleteRadiationFee(pt_id);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
                retMap.put("message", "刪除成功。/id="+pt_id);
            } else {
                retMap.put("message", "刪除失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
  //==== 注射 (injection Fee, P75)
    @ApiOperation(value="10-3.05 注射費設定(get)", notes="")
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }", response=PtWardFeeDto.class)
    })
    @RequestMapping(value = "/payment/injectionfee/{pt_id}", method = RequestMethod.POST)
    public ResponseEntity<?> getPaymentInjectionfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.Map<String, Object> retMap = ptInjectionFeeService.findInjectionFee(pt_id);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-3.06 注射費設定(add)", notes="category = \"病房費\"")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/injectionfee/add", method = RequestMethod.POST)
    public ResponseEntity<?> addPaymentInjectionfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @RequestBody PtInjectionFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = 0;
            long ptId = ptInjectionFeeService.addInjectionFee(params);
            if (ptId<=0) {
                status = -1;
            }
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status==0) {
                retMap.put("message", "新增成功。/id="+ptId);
            } else {
                retMap.put("message", "新增失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-3.07 注射費設定(update)", notes="<b>category 無法變更</b>")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/injectionfee/{pt_id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePaymentInjectionfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id,
        @RequestBody PtInjectionFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = ptInjectionFeeService.updateInjectionFee(pt_id, params);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
                retMap.put("message", "修改成功。/id="+pt_id);
            } else {
                retMap.put("message", "修改失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-3.08 注射費設定(delete)", notes="")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/injectionfee/{pt_id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePaymentInjectionfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = ptInjectionFeeService.deleteInjectionFee(pt_id);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
                retMap.put("message", "刪除成功。/id="+pt_id);
            } else {
                retMap.put("message", "刪除失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    //==== 品質支付服務 Quality Service  ptQualityServiceService
    @ApiOperation(value="10-3.09 品質支付服務設定(get)", notes="")
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }", response=PtWardFeeDto.class)
    })
    @RequestMapping(value = "/payment/qualityservice/{pt_id}", method = RequestMethod.POST)
    public ResponseEntity<?> getPaymentQualityService(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.Map<String, Object> retMap = ptQualityServiceService.findQualityService(pt_id);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-3.10 品質支付服務設定(add)", notes="category = \"品質支付服務\"")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/qualityservice/add", method = RequestMethod.POST)
    public ResponseEntity<?> addPaymentQualityService(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @RequestBody PtQualityServicePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = 0;
            long ptId = ptQualityServiceService.addQualityService(params);
            if (ptId<=0) {
                status = -1;
            }
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status==0) {
                retMap.put("message", "新增成功。/id="+ptId);
            } else {
                retMap.put("message", "新增失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-3.11 品質支付服務設定(update)", notes="<b>category 無法變更</b>")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/qualityservice/{pt_id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePaymentQualityService(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id,
        @RequestBody PtQualityServicePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = ptQualityServiceService.updateQualityService(pt_id, params);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
                retMap.put("message", "修改成功。/id="+pt_id);
            } else {
                retMap.put("message", "修改失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-3.12 品質支付服務設定(delete)", notes="")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/qualityservice/{pt_id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePaymentQualityService(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = ptQualityServiceService.deleteQualityService(pt_id);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
                retMap.put("message", "刪除成功。/id="+pt_id);
            } else {
                retMap.put("message", "刪除失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }


}
