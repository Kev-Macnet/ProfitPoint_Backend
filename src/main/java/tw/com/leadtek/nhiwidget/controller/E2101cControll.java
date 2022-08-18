package tw.com.leadtek.nhiwidget.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.assertj.core.util.Arrays;
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
import tw.com.leadtek.nhiwidget.annotation.LogDefender;
import tw.com.leadtek.nhiwidget.constant.LogType;
import tw.com.leadtek.nhiwidget.dto.PtInjectionFeeDto;
import tw.com.leadtek.nhiwidget.dto.PtInjectionFeePl;
import tw.com.leadtek.nhiwidget.dto.PtInpatientCareDto;
import tw.com.leadtek.nhiwidget.dto.PtInpatientCarePl;
import tw.com.leadtek.nhiwidget.dto.PtQualityServiceDto;
import tw.com.leadtek.nhiwidget.dto.PtQualityServicePl;
import tw.com.leadtek.nhiwidget.dto.PtRadiationFeeDto;
import tw.com.leadtek.nhiwidget.dto.PtRadiationFeePl;
import tw.com.leadtek.nhiwidget.dto.PtRehabilitationFeeDto;
import tw.com.leadtek.nhiwidget.dto.PtRehabilitationFeePl;
import tw.com.leadtek.nhiwidget.service.pt.PaymentTermsService;
import tw.com.leadtek.nhiwidget.service.pt.PtInjectionFeeService;
import tw.com.leadtek.nhiwidget.service.pt.PtInpatientCareService;
import tw.com.leadtek.nhiwidget.service.pt.PtQualityServiceService;
import tw.com.leadtek.nhiwidget.service.pt.PtRadiationFeeService;
import tw.com.leadtek.nhiwidget.service.pt.PtRehabilitationFeeService;


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
    @Autowired
    private PtInpatientCareService ptInpatientCareService;
    @Autowired
    private PtRehabilitationFeeService ptRehabilitationFeeService;
    
    @Autowired
    private HttpServletRequest httpServletReq;
    
    //==== 放射線診療費 Radiation Fee
    @ApiOperation(value="10-3.01 放射線診療費設定(get)", notes="")
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }", response=PtRadiationFeeDto.class)
    })
    @RequestMapping(value = "/payment/radiationfee/{pt_id}", method = RequestMethod.POST)
    @LogDefender(value = {LogType.SIGNIN})
    public ResponseEntity<?> getPaymentRadiationfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
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
    @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_C}, name = "新增放射線診療費設定")
    public ResponseEntity<?> addPaymentRadiationfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @Valid @RequestBody PtRadiationFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = 0;
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            long ptId = ptRadiationFeeService.addRadiationFee(params);
            if (ptId<=0) {
                status = -1;
            }
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status==0) {
//                paymentTermsService.correctEndDate(params.getCategory());
                paymentTermsService.correctEndDateByNhiNo(params.getNhi_no(), "");
                paymentTermsService.updateActiveByThread(ptId, PtRadiationFeeService.Category, params.getActive(), true);
                retMap.put("message", "新增成功。id="+ptId);
                
                httpServletReq.setAttribute(LogType.ACTION_C.name()+"_PKS", Arrays.asList(new Long[]{ptId}));
                
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
    @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_U}, name = "修改放射線診療費設定")
    public ResponseEntity<?> updatePaymentRadiationfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id,
        @Valid @RequestBody PtRadiationFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            int status = ptRadiationFeeService.updateRadiationFee(pt_id, params);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
//                paymentTermsService.correctEndDate(params.getCategory());
                paymentTermsService.correctEndDateByNhiNo(params.getNhi_no(), "");
                paymentTermsService.updateActiveByThread(pt_id, PtRadiationFeeService.Category, params.getActive(), true);
                retMap.put("message", "修改成功。/id="+pt_id);
                
                httpServletReq.setAttribute(LogType.ACTION_U.name()+"_PKS", Arrays.asList(new Long[]{pt_id}));
                
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
    @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_D}, name = "刪除放射線診療費設定")
    public ResponseEntity<?> deletePaymentRadiationfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            paymentTermsService.updateActiveByThread(pt_id, PtRadiationFeeService.Category, 0, false);
            int status = ptRadiationFeeService.deleteRadiationFee(pt_id);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
                retMap.put("message", "刪除成功。/id="+pt_id);
                
                httpServletReq.setAttribute(LogType.ACTION_D.name()+"_PKS", Arrays.asList(new Long[]{pt_id}));
                
            } else {
                retMap.put("message", "刪除失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    //==== 注射 (injection Fee, P51)
    @ApiOperation(value="10-3.05 注射費設定(get)", notes="")
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }", response=PtInjectionFeeDto.class)
    })
    @RequestMapping(value = "/payment/injectionfee/{pt_id}", method = RequestMethod.POST)
    @LogDefender(value = {LogType.SIGNIN})
    public ResponseEntity<?> getPaymentInjectionfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.Map<String, Object> retMap = ptInjectionFeeService.findInjectionFee(pt_id);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-3.06 注射費設定(add)", notes="category = \"注射費\"")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/injectionfee/add", method = RequestMethod.POST)
    @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_C}, name = "新增注射費設定")
    public ResponseEntity<?> addPaymentInjectionfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @Valid @RequestBody PtInjectionFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = 0;
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            long ptId = ptInjectionFeeService.addInjectionFee(params);
            if (ptId<=0) {
                status = -1;
            }
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status==0) {
//                paymentTermsService.correctEndDate(params.getCategory());
                paymentTermsService.correctEndDateByNhiNo(params.getNhi_no(), "");
                paymentTermsService.updateActiveByThread(ptId, PtInjectionFeeService.Category, params.getActive(), true);
                retMap.put("message", "新增成功。/id="+ptId);
                
                httpServletReq.setAttribute(LogType.ACTION_C.name()+"_PKS", Arrays.asList(new Long[]{ptId}));
                
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
    @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_U}, name = "修改注射費設定")
    public ResponseEntity<?> updatePaymentInjectionfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id,
        @Valid @RequestBody PtInjectionFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            int status = ptInjectionFeeService.updateInjectionFee(pt_id, params);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
//                paymentTermsService.correctEndDate(params.getCategory());
                paymentTermsService.correctEndDateByNhiNo(params.getNhi_no(), "");
                paymentTermsService.updateActiveByThread(pt_id, PtInjectionFeeService.Category, params.getActive(), true);
                retMap.put("message", "修改成功。/id="+pt_id);
                
                httpServletReq.setAttribute(LogType.ACTION_U.name()+"_PKS", Arrays.asList(new Long[]{pt_id}));
                
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
    @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_D}, name = "刪除注射費設定")
    public ResponseEntity<?> deletePaymentInjectionfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            paymentTermsService.updateActiveByThread(pt_id, PtInjectionFeeService.Category, 0, false);
            int status = ptInjectionFeeService.deleteInjectionFee(pt_id);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
                retMap.put("message", "刪除成功。/id="+pt_id);
                
                httpServletReq.setAttribute(LogType.ACTION_D.name()+"_PKS", Arrays.asList(new Long[]{pt_id}));
                
            } else {
                retMap.put("message", "刪除失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    //==== 品質支付服務 Quality Service  ptQualityServiceService
    @ApiOperation(value="10-3.09 品質支付服務設定(get)", notes="")
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }", response=PtQualityServiceDto.class)
    })
    @RequestMapping(value = "/payment/qualityservice/{pt_id}", method = RequestMethod.POST)
    @LogDefender(value = {LogType.SIGNIN})
    public ResponseEntity<?> getPaymentQualityService(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
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
    @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_C}, name = "新增品質支付服務設定")
    public ResponseEntity<?> addPaymentQualityService(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @Valid @RequestBody PtQualityServicePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = 0;
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            long ptId = ptQualityServiceService.addQualityService(params);
            if (ptId<=0) {
                status = -1;
            }
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status==0) {
//                paymentTermsService.correctEndDate(params.getCategory());
                paymentTermsService.correctEndDateByNhiNo(params.getNhi_no(), "");
                paymentTermsService.updateActiveByThread(ptId, PtQualityServiceService.Category, params.getActive(), true);
                retMap.put("message", "新增成功。/id="+ptId);
                
                httpServletReq.setAttribute(LogType.ACTION_C.name()+"_PKS", Arrays.asList(new Long[]{ptId}));
                
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
    @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_U}, name = "修改品質支付服務設定")
    public ResponseEntity<?> updatePaymentQualityService(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id,
        @Valid @RequestBody PtQualityServicePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            int status = ptQualityServiceService.updateQualityService(pt_id, params);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
//                paymentTermsService.correctEndDate(params.getCategory());
                paymentTermsService.correctEndDateByNhiNo(params.getNhi_no(), "");
                paymentTermsService.updateActiveByThread(pt_id, PtQualityServiceService.Category, params.getActive(), true);
                retMap.put("message", "修改成功。/id="+pt_id);
                
                httpServletReq.setAttribute(LogType.ACTION_U.name()+"_PKS", Arrays.asList(new Long[]{pt_id}));
                
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
    @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_D}, name = "刪除品質支付服務設定")
    public ResponseEntity<?> deletePaymentQualityService(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            paymentTermsService.updateActiveByThread(pt_id, PtQualityServiceService.Category, 0, false);
            int status = ptQualityServiceService.deleteQualityService(pt_id);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
                retMap.put("message", "刪除成功。/id="+pt_id);
                
                httpServletReq.setAttribute(LogType.ACTION_D.name()+"_PKS", Arrays.asList(new Long[]{pt_id}));
                
            } else {
                retMap.put("message", "刪除失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    //==== 住院安寧療護 (Inpatient Care, P77)
    @ApiOperation(value="10-3.13 住院安寧療護(get)", notes="")
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }", response=PtInpatientCareDto.class)
    })
    @RequestMapping(value = "/payment/inpatientcare/{pt_id}", method = RequestMethod.POST)
    @LogDefender(value = {LogType.SIGNIN})
    public ResponseEntity<?> getPaymentInpatientCare(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.Map<String, Object> retMap = ptInpatientCareService.findInpatientCare(pt_id);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-3.14 住院安寧療護(add)", notes="category = \"住院安寧療護\"")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/inpatientcare/add", method = RequestMethod.POST)
    @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_C}, name = "新增住院安寧療護")
    public ResponseEntity<?> addPaymentInpatientCare(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @Valid @RequestBody PtInpatientCarePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = 0;
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            long ptId = ptInpatientCareService.addInpatientCare(params);
            if (ptId<=0) {
                status = -1;
            }
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status==0) {
//                paymentTermsService.correctEndDate(params.getCategory());
                paymentTermsService.correctEndDateByNhiNo(params.getNhi_no(), "");
                paymentTermsService.updateActiveByThread(ptId, PtInpatientCareService.Category, params.getActive(), true);
                retMap.put("message", "新增成功。/id="+ptId);
                
                httpServletReq.setAttribute(LogType.ACTION_C.name()+"_PKS", Arrays.asList(new Long[]{ptId}));
                
            } else {
                retMap.put("message", "新增失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-3.15 住院安寧療護(update)", notes="<b>category 無法變更</b>")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/inpatientcare/{pt_id}", method = RequestMethod.PUT)
    @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_U}, name = "修改住院安寧療護")
    public ResponseEntity<?> updatePaymentInpatientCare(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id,
        @Valid @RequestBody PtInpatientCarePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            int status = ptInpatientCareService.updateInpatientCare(pt_id, params);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
//                paymentTermsService.correctEndDate(params.getCategory());
                paymentTermsService.correctEndDateByNhiNo(params.getNhi_no(), "");
                paymentTermsService.updateActiveByThread(pt_id, PtInpatientCareService.Category, params.getActive(), true);
                retMap.put("message", "修改成功。/id="+pt_id);
                
                httpServletReq.setAttribute(LogType.ACTION_U.name()+"_PKS", Arrays.asList(new Long[]{pt_id}));
                
            } else {
                retMap.put("message", "修改失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-3.16 住院安寧療護(delete)", notes="")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/inpatientcare/{pt_id}", method = RequestMethod.DELETE)
    @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_D}, name = "刪除住院安寧療護")
    public ResponseEntity<?> deletePaymentInpatientCare(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            paymentTermsService.updateActiveByThread(pt_id, PtInpatientCareService.Category, 0, false);
            int status = ptInpatientCareService.deleteInpatientCare(pt_id);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
                retMap.put("message", "刪除成功。/id="+pt_id);
                
                httpServletReq.setAttribute(LogType.ACTION_D.name()+"_PKS", Arrays.asList(new Long[]{pt_id}));
                
            } else {
                retMap.put("message", "刪除失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }

    //==== 復健治療費 (Rehabilitation fee)
    @ApiOperation(value="10-3.17 復健治療費設定(get)", notes="")
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }", response=PtRehabilitationFeeDto.class)
    })
    @RequestMapping(value = "/payment/rehabilitationfee/{pt_id}", method = RequestMethod.POST)
    @LogDefender(value = {LogType.SIGNIN})
    public ResponseEntity<?> getPaymentRehabilitationfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.Map<String, Object> retMap = ptRehabilitationFeeService.findRehabilitationFee(pt_id);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-3.18 復健治療費設定(add)", notes="category = \"復健治療費\"")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/rehabilitationfee/add", method = RequestMethod.POST)
    @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_C}, name = "新增復健治療費設定")
    public ResponseEntity<?> addPaymentRehabilitationfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @Valid @RequestBody PtRehabilitationFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = 0;
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            long ptId = ptRehabilitationFeeService.addRehabilitationFee(params);
            if (ptId<=0) {
                status = -1;
            }
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status==0) {
//                paymentTermsService.correctEndDate(params.getCategory());
                paymentTermsService.correctEndDateByNhiNo(params.getNhi_no(), "");
                paymentTermsService.updateActiveByThread(ptId, PtRehabilitationFeeService.Category, params.getActive(), true);
                retMap.put("message", "新增成功。id="+ptId);
                
                httpServletReq.setAttribute(LogType.ACTION_C.name()+"_PKS", Arrays.asList(new Long[]{ptId}));
                
            } else {
                retMap.put("message", "新增失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-3.19 復健治療費設定(update)", notes="<b>category 無法變更</b>")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/rehabilitationfee/{pt_id}", method = RequestMethod.PUT)
    @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_U}, name = "修改復健治療費設定")
    public ResponseEntity<?> updatePaymentRehabilitationfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id,
        @Valid @RequestBody PtRehabilitationFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            int status = ptRehabilitationFeeService.updateRehabilitationFee(pt_id, params);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
//                paymentTermsService.correctEndDate(params.getCategory());
                paymentTermsService.correctEndDateByNhiNo(params.getNhi_no(), "");
                paymentTermsService.updateActiveByThread(pt_id, PtRehabilitationFeeService.Category, params.getActive(), true);
                retMap.put("message", "修改成功。/id="+pt_id);
                
                httpServletReq.setAttribute(LogType.ACTION_U.name()+"_PKS", Arrays.asList(new Long[]{pt_id}));
                
            } else {
                retMap.put("message", "修改失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-3.20 復健治療費設定(delete)", notes="")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/rehabilitationfee/{pt_id}", method = RequestMethod.DELETE)
    @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_D}, name = "刪除復健治療費設定")
    public ResponseEntity<?> deletePaymentRehabilitationfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            paymentTermsService.updateActiveByThread(pt_id, PtRehabilitationFeeService.Category, 0, false);
            int status = ptRehabilitationFeeService.deleteRehabilitationFee(pt_id);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
                retMap.put("message", "刪除成功。/id="+pt_id);
                
                httpServletReq.setAttribute(LogType.ACTION_D.name()+"_PKS", Arrays.asList(new Long[]{pt_id}));
                
            } else {
                retMap.put("message", "刪除失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }

}
