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
import tw.com.leadtek.nhiwidget.dto.PtAdjustmentFeeDto;
import tw.com.leadtek.nhiwidget.dto.PtAdjustmentFeePl;
import tw.com.leadtek.nhiwidget.dto.PtMedicineFeeDto;
import tw.com.leadtek.nhiwidget.dto.PtMedicineFeePl;
import tw.com.leadtek.nhiwidget.dto.PtNutritionalFeeDto;
import tw.com.leadtek.nhiwidget.dto.PtNutritionalFeePl;
import tw.com.leadtek.nhiwidget.dto.PtTreatmentFeeDto;
import tw.com.leadtek.nhiwidget.dto.PtTreatmentFeePl;
import tw.com.leadtek.nhiwidget.dto.PtTubeFeedingFeeDto;
import tw.com.leadtek.nhiwidget.dto.PtTubeFeedingFeePl;
import tw.com.leadtek.nhiwidget.service.pt.PaymentTermsService;
import tw.com.leadtek.nhiwidget.service.pt.PtAdjustmentFeeService;
import tw.com.leadtek.nhiwidget.service.pt.PtMedicineFeeService;
import tw.com.leadtek.nhiwidget.service.pt.PtNutritionalFeeService;
import tw.com.leadtek.nhiwidget.service.pt.PtTreatmentFeeService;
import tw.com.leadtek.nhiwidget.service.pt.PtTubeFeedingFeeService;


@Api(value = "健保標準給付額 支付條件設定 API", tags = {"10-2 健保標準給付額 支付條件設定"})
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class E2101bControll {
    
    @Autowired
    private PaymentTermsService paymentTermsService;
    @Autowired
    private PtTreatmentFeeService ptTreatmentFeeService;
    @Autowired
    private PtTubeFeedingFeeService ptTubeFeedingFeeService;
    @Autowired
    private PtNutritionalFeeService ptNutritionalFeeService;
    @Autowired
    private PtAdjustmentFeeService ptAdjustmentFeeService;
    @Autowired
    private PtMedicineFeeService ptMedicineFeeService;

    @Autowired
    private HttpServletRequest httpServletReq;
    
    //==== 治療處置費設定 Treatment Fee
    @ApiOperation(value="10-2.01 治療處置費設定(get)", notes="")
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }", response=PtTreatmentFeeDto.class)
    })
    @RequestMapping(value = "/payment/treatmentfee/{pt_id}", method = RequestMethod.POST)
    @LogDefender(value = {LogType.SIGNIN})
    public ResponseEntity<?> getPaymentTreatmentfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.Map<String, Object> retMap = ptTreatmentFeeService.findTreatmentFee(pt_id);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-2.02 治療處置費設定(add)", notes="category = \"治療處置費\"")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/treatmentfee/add", method = RequestMethod.POST)
    @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_C}, name = "新增治療處置費設定")
    public ResponseEntity<?> addPaymentTreatmentfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @Valid @RequestBody PtTreatmentFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = 0;
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            long ptId = ptTreatmentFeeService.addTreatmentFee(params);
            if (ptId<=0) {
                status = -1;
            }
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status==0) {
//                paymentTermsService.correctEndDate(params.getCategory());
                paymentTermsService.correctEndDateByNhiNo(params.getNhi_no(), "");
                paymentTermsService.updateActiveByThread(ptId, PtTreatmentFeeService.Category, params.getActive(), true);
                retMap.put("message", "新增成功。/id="+ptId);
                
                httpServletReq.setAttribute(LogType.ACTION_C.name()+"_PKS", Arrays.asList(new Long[]{ptId}));
                
            } else {
                retMap.put("message", "新增失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-2.03 治療處置費設定(update)", notes="<b>category 無法變更</b>")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/treatmentfee/{pt_id}", method = RequestMethod.PUT)
    @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_U}, name = "修改治療處置費設定")
    public ResponseEntity<?> updatePaymentTreatmentfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id,
        @Valid @RequestBody PtTreatmentFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            int status = ptTreatmentFeeService.updateTreatmentFee(pt_id, params);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
//                paymentTermsService.correctEndDate(params.getCategory());
                paymentTermsService.correctEndDateByNhiNo(params.getNhi_no(), "");
                paymentTermsService.updateActiveByThread(pt_id, PtTreatmentFeeService.Category, params.getActive(), true);
                retMap.put("message", "修改成功。/id="+pt_id);
                
                httpServletReq.setAttribute(LogType.ACTION_U.name()+"_PKS", Arrays.asList(new Long[]{pt_id}));
                
            } else {
                retMap.put("message", "修改失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-2.04 治療處置費設定(delete)", notes="")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/treatmentfee/{pt_id}", method = RequestMethod.DELETE)
    @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_D}, name = "刪除治療處置費設定")
    public ResponseEntity<?> deletePaymentTreatmentfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            paymentTermsService.updateActiveByThread(pt_id, PtTreatmentFeeService.Category, 0, false);
            int status = ptTreatmentFeeService.deleteTreatmentFee(pt_id);
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

    //==== 管灌飲食費設定 Tube Feeding Fee
    @ApiOperation(value="10-2.05 管灌飲食費設定(get, 棄用!)", notes="")
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }", response=PtTubeFeedingFeeDto.class)
    })
    @RequestMapping(value = "/payment/tubefeedingfee/{pt_id}", method = RequestMethod.POST)
    @LogDefender(value = {LogType.SIGNIN})
    public ResponseEntity<?> getPaymentTubeFeedingfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.Map<String, Object> retMap = ptTubeFeedingFeeService.findTubeFeedingFee(pt_id);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-2.06 管灌飲食費設定(add, 棄用!)", notes="category = \"管灌飲食費\"")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/tubefeedingfee/add", method = RequestMethod.POST)
    @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_C}, name = "新增管灌飲食費設定")
    public ResponseEntity<?> addPaymentTubeFeedingfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @Valid @RequestBody PtTubeFeedingFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = 0;
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            long ptId = ptTubeFeedingFeeService.addTubeFeedingFee(params);
            if (ptId<=0) {
                status = -1;
            }
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status==0) {
                retMap.put("message", "新增成功。/id="+ptId);
                
                httpServletReq.setAttribute(LogType.ACTION_C.name()+"_PKS", Arrays.asList(new Long[]{ptId}));
                
            } else {
                retMap.put("message", "新增失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    
    @ApiOperation(value="10-2.07 管灌飲食費設定(update, 棄用!)", notes="<b>category 無法變更</b>")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/tubefeedingfee/{pt_id}", method = RequestMethod.PUT)
    @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_U}, name = "修改管灌飲食費設定")
    public ResponseEntity<?> updatePaymentTubeFeedingfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id,
        @Valid @RequestBody PtTubeFeedingFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            int status = ptTubeFeedingFeeService.updateTubeFeedingFee(pt_id, params);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
                retMap.put("message", "修改成功。/id="+pt_id);
                
                httpServletReq.setAttribute(LogType.ACTION_U.name()+"_PKS", Arrays.asList(new Long[]{pt_id}));
                
            } else {
                retMap.put("message", "修改失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-2.08 管灌飲食費設定(delete, 棄用!)", notes="")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/tubefeedingfee/{pt_id}", method = RequestMethod.DELETE)
    @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_D}, name = "刪除管灌飲食費設定")
    public ResponseEntity<?> deletePaymentTubeFeedingfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = ptTubeFeedingFeeService.deleteTubeFeedingFee(pt_id);
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
    
    //=== 營養照護費 Nutritional Fee, P47
    @ApiOperation(value="10-2.09 管灌飲食費及營養照護費設定(get)", notes="") 
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }", response=PtNutritionalFeeDto.class)
    })
    @RequestMapping(value = "/payment/nutritionalfee/{pt_id}", method = RequestMethod.POST)
    @LogDefender(value = {LogType.SIGNIN})
    public ResponseEntity<?> getPaymentNutritionalfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.Map<String, Object> retMap = ptNutritionalFeeService.findNutritionalFee(pt_id);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-2.10 管灌飲食費及營養照護費設定(add)", notes="category = \"管灌飲食費及營養照護費\"")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/nutritionalfee/add", method = RequestMethod.POST)
    @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_C}, name = "新增管灌飲食費及營養照護費設定")
    public ResponseEntity<?> addPaymentNutritionalfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @Valid @RequestBody PtNutritionalFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = 0;
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            long ptId = ptNutritionalFeeService.addNutritionalFee(params);
            if (ptId<=0) {
                status = -1;
            }
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status==0) {
//                paymentTermsService.correctEndDate(params.getCategory());
                paymentTermsService.correctEndDateByNhiNo(params.getNhi_no(), "");
                paymentTermsService.updateActiveByThread(ptId, PtNutritionalFeeService.Category, params.getActive(), true);
                retMap.put("message", "新增成功。/id="+ptId);
                
                httpServletReq.setAttribute(LogType.ACTION_C.name()+"_PKS", Arrays.asList(new Long[]{ptId}));
                
            } else {
                retMap.put("message", "新增失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-2.11 管灌飲食費及營養照護費設定(update)", notes="<b>category 無法變更</b>")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/nutritionalfee/{pt_id}", method = RequestMethod.PUT)
    @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_U}, name = "修改管灌飲食費及營養照護費設定")
    public ResponseEntity<?> updatePaymentNutritionalfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id,
        @Valid @RequestBody PtNutritionalFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            int status = ptNutritionalFeeService.updateNutritionalFee(pt_id, params);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
//                paymentTermsService.correctEndDate(params.getCategory());
                paymentTermsService.correctEndDateByNhiNo(params.getNhi_no(), "");
                paymentTermsService.updateActiveByThread(pt_id, PtNutritionalFeeService.Category, params.getActive(), true);
                retMap.put("message", "修改成功。/id="+pt_id);
                
                httpServletReq.setAttribute(LogType.ACTION_U.name()+"_PKS", Arrays.asList(new Long[]{pt_id}));
                
            } else {
                retMap.put("message", "修改失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-2.12 管灌飲食費及營養照護費設定(delete)", notes="")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/nutritionalfee/{pt_id}", method = RequestMethod.DELETE)
    @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_D}, name = "刪除管灌飲食費及營養照護費設定")
    public ResponseEntity<?> deletePaymentNutritionalfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            paymentTermsService.updateActiveByThread(pt_id, PtNutritionalFeeService.Category, 0, false);
            int status = ptNutritionalFeeService.deleteNutritionalFee(pt_id);
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
    
  //==== 調劑費 (Adjustment fee)
    @ApiOperation(value="10-2.13 調劑費設定(get)", notes="")
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }", response=PtAdjustmentFeeDto.class)
    })
    @RequestMapping(value = "/payment/adjustmentfee/{pt_id}", method = RequestMethod.POST)
    @LogDefender(value = {LogType.SIGNIN})
    public ResponseEntity<?> getPaymentAdjustmentfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.Map<String, Object> retMap = ptAdjustmentFeeService.findAdjustmentFee(pt_id);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-2.14 調劑費設定(add)", notes="category = \"調劑費\"")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/adjustmentfee/add", method = RequestMethod.POST)
    @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_C}, name = "新增調劑費設定")
    public ResponseEntity<?> addPaymentAdjustmentfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @Valid @RequestBody PtAdjustmentFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = 0;
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            long ptId = ptAdjustmentFeeService.addAdjustmentFee(params);
            if (ptId<=0) {
                status = -1;
            }
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status==0) {
//                paymentTermsService.correctEndDate(params.getCategory());
                paymentTermsService.correctEndDateByNhiNo(params.getNhi_no(), "");
                paymentTermsService.updateActiveByThread(ptId, PtAdjustmentFeeService.Category, params.getActive(), true);
                retMap.put("message", "新增成功。/id="+ptId);
                
                httpServletReq.setAttribute(LogType.ACTION_C.name()+"_PKS", Arrays.asList(new Long[]{ptId}));
                
            } else {
                retMap.put("message", "新增失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-2.15 調劑費設定(update)", notes="<b>category 無法變更</b>")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/adjustmentfee/{pt_id}", method = RequestMethod.PUT)
    @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_U}, name = "修改調劑費設定")
    public ResponseEntity<?> updatePaymentAdjustmentfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id,
        @Valid @RequestBody PtAdjustmentFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            int status = ptAdjustmentFeeService.updateAdjustmentFee(pt_id, params);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
//                paymentTermsService.correctEndDate(params.getCategory());
                paymentTermsService.correctEndDateByNhiNo(params.getNhi_no(), "");
                paymentTermsService.updateActiveByThread(pt_id, PtAdjustmentFeeService.Category, params.getActive(), true);
                retMap.put("message", "修改成功。/id="+pt_id);
                
                httpServletReq.setAttribute(LogType.ACTION_U.name()+"_PKS", Arrays.asList(new Long[]{pt_id}));
                
            } else {
                retMap.put("message", "修改失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-2.16 調劑費設定(delete)", notes="")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/adjustmentfee/{pt_id}", method = RequestMethod.DELETE)
    @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_D}, name = "刪除調劑費設定")
    public ResponseEntity<?> deletePaymentAdjustmentfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            paymentTermsService.updateActiveByThread(pt_id, PtAdjustmentFeeService.Category, 0, false);
            int status = ptAdjustmentFeeService.deleteAdjustmentFee(pt_id);
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
    
  //==== 藥費設定 Medicine fee
    @ApiOperation(value="10-2.17 藥費設定(get)", notes="")
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }", response=PtMedicineFeeDto.class)
    })
    @RequestMapping(value = "/payment/medicinefee/{pt_id}", method = RequestMethod.POST)
    @LogDefender(value = {LogType.SIGNIN})
    public ResponseEntity<?> getPaymentMedicinefee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.Map<String, Object> retMap = ptMedicineFeeService.findMedicineFee(pt_id);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-2.18 藥費設定(add)", notes="category = \"藥費\"")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/medicinefee/add", method = RequestMethod.POST)
    @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_C}, name = "新增藥費設定")
    public ResponseEntity<?> addPaymentMedicinefee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @Valid @RequestBody PtMedicineFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = 0;
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            long ptId = ptMedicineFeeService.addMedicineFee(params);
            if (ptId<=0) {
                status = -1;
            }
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status==0) {
//                paymentTermsService.correctEndDate(params.getCategory());
                paymentTermsService.correctEndDateByNhiNo(params.getNhi_no(), "");
                paymentTermsService.updateActiveByThread(ptId, PtMedicineFeeService.Category, params.getActive(), true);
                retMap.put("message", "新增成功。/id="+ptId);
                
                httpServletReq.setAttribute(LogType.ACTION_C.name()+"_PKS", Arrays.asList(new Long[]{ptId}));
                
            } else {
                retMap.put("message", "新增失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-2.19 藥費設定(update)", notes="<b>category 無法變更</b>")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/medicinefee/{pt_id}", method = RequestMethod.PUT)
    @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_U}, name = "修改藥費設定")
    public ResponseEntity<?> updatePaymentMedicinefee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id,
        @Valid @RequestBody PtMedicineFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            int status = ptMedicineFeeService.updateMedicineFee(pt_id, params);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
//                paymentTermsService.correctEndDate(params.getCategory());
                paymentTermsService.correctEndDateByNhiNo(params.getNhi_no(), "");
                paymentTermsService.updateActiveByThread(pt_id, PtMedicineFeeService.Category, params.getActive(), true);
                retMap.put("message", "修改成功。/id="+pt_id);
                
                httpServletReq.setAttribute(LogType.ACTION_U.name()+"_PKS", Arrays.asList(new Long[]{pt_id}));
                
            } else {
                retMap.put("message", "修改失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-2.20 藥費設定(delete)", notes="")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/medicinefee/{pt_id}", method = RequestMethod.DELETE)
    @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_D}, name = "刪除藥費設定")
    public ResponseEntity<?> deletePaymentMedicinefee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            paymentTermsService.updateActiveByThread(pt_id, PtMedicineFeeService.Category, 0, false);
            int status = ptMedicineFeeService.deleteMedicineFee(pt_id);
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
