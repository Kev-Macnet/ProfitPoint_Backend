package tw.com.leadtek.nhiwidget.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.media.Content;

import tw.com.leadtek.nhiwidget.dto.PaymentTermsSearchDto;
import tw.com.leadtek.nhiwidget.dto.PaymentTermsSearchPl;
import tw.com.leadtek.nhiwidget.dto.PtInpatientFeeDto;
import tw.com.leadtek.nhiwidget.dto.PtInpatientFeePl;
import tw.com.leadtek.nhiwidget.dto.PtOutpatientFeeDto;
import tw.com.leadtek.nhiwidget.dto.PtOutpatientFeePl;
import tw.com.leadtek.nhiwidget.dto.PtPsychiatricWardFeeDto;
import tw.com.leadtek.nhiwidget.dto.PtPsychiatricWardFeePl;
import tw.com.leadtek.nhiwidget.dto.PtWardFeeDto;
import tw.com.leadtek.nhiwidget.dto.PtWardFeePl;
import tw.com.leadtek.nhiwidget.service.PaymentTermsService;
import tw.com.leadtek.nhiwidget.service.PtInpatientFeeService;
import tw.com.leadtek.nhiwidget.service.PtOutpatientFeeService;
import tw.com.leadtek.nhiwidget.service.PtPsychiatricWardFeeService;
import tw.com.leadtek.nhiwidget.service.PtWardFeeService;
import tw.com.leadtek.tools.Utility;


@Api(value = "健保標準給付額 支付條件設定 API", tags = {"10-1 健保標準給付額 支付條件設定"})
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class E2101Controll {
    @Value("${springfox.documentation.swagger.use-model-v3:true}")
    boolean useModelV3;
    
    @Autowired
    private PaymentTermsService paymentTermsService;
    @Autowired
    private PtOutpatientFeeService ptOutpatientFeeService;
    @Autowired
    private PtWardFeeService ptWardFeeService;
    @Autowired
    private PtInpatientFeeService ptInpatientFeeService;
    @Autowired
    private PtPsychiatricWardFeeService ptPsychiatricWardFeeService;
    
    @ApiOperation(value="10.01 支付條件設定搜尋", notes="Result: PaymentTermsSearchDto")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="[{...}, {...}, ...]", response=PaymentTermsSearchDto.class)
    })
    @RequestMapping(value = "/payment/terms/search", method = RequestMethod.POST)
    public ResponseEntity<?> paymentTermsSearh(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @RequestBody PaymentTermsSearchPl params) throws Exception {
        System.out.println("useModelV3="+useModelV3);
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.Date da1 = Utility.detectDate(params.getStart_date());
            java.util.Date da2 = Utility.detectDate(params.getEnd_date());
            String feeNo = (params.getFeeNo()==null) ? "" : params.getFeeNo();
            String nhiNo = (params.getNhiNo()==null) ? "" : params.getNhiNo();
            String category = (params.getCategory()==null) ? "" : params.getCategory();
            List<Map<String, Object>> retList = paymentTermsService.searchPaymentTerms(feeNo, nhiNo, category, da1, da2);
            return new ResponseEntity<>(retList, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10.02 門診診察費設定(get)", notes="")
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }", response=PtOutpatientFeeDto.class)
//        @ApiResponse(responseCode = "200", description="{ PtOutpatientFeeDto }",
//                     content = @Content(schema = @Schema(implementation = PtOutpatientFeeDto.class)))
    })
    @RequestMapping(value = "/payment/outpatientfee/{pt_id}", method = RequestMethod.POST)
    public ResponseEntity<?> getPaymentOutpatientfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.Map<String, Object> retMap = ptOutpatientFeeService.findOutpatientFee(pt_id);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }

    
    @ApiOperation(value="10.03 門診診察費設定(add)", notes="ategory = \"門診診察費\"")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/outpatientfee/add", method = RequestMethod.POST)
    public ResponseEntity<?> addPaymentOutpatientfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @RequestBody PtOutpatientFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = 0;
            long ptId = ptOutpatientFeeService.addOutpatientFee(params);
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
    
    @ApiOperation(value="10.04 門診診察費設定(update)", notes="<b>category 無法變更</b>")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/outpatientfee/{pt_id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePaymentOutpatientfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id,
        @RequestBody PtOutpatientFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = ptOutpatientFeeService.updateOutpatientFee(pt_id, params);
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
    
    @ApiOperation(value="10.05 門診診察費設定(delete)", notes="")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/outpatientfee/{pt_id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePaymentOutpatientfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = ptOutpatientFeeService.deleteOutpatientFee(pt_id);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>=0) {
                retMap.put("message", "刪除成功。/id="+pt_id);
            } else {
                retMap.put("message", "刪除失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    //==== 住院診察費 inpatient fee
    @ApiOperation(value="10.06 住院診察費設定(get)", notes="")
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }", response=PtInpatientFeeDto.class)
    })
    @RequestMapping(value = "/payment/inpatientfee/{pt_id}", method = RequestMethod.POST)
    public ResponseEntity<?> getPaymentInpatientfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.Map<String, Object> retMap = ptInpatientFeeService.findInpatientFee(pt_id);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10.07 住院診察費設定(add)", notes="category = \"手術費\"")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/inpatientfee/add", method = RequestMethod.POST)
    public ResponseEntity<?> addPaymentInpatientfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @RequestBody PtInpatientFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = 0;
            long ptId = ptInpatientFeeService.addInpatientFee(params);
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
    
    
    @ApiOperation(value="10.08 住院診察費設定(update)", notes="<b>category 無法變更</b>")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/inpatientfee/{pt_id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePaymentInpatientfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id,
        @RequestBody PtInpatientFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = ptInpatientFeeService.updateInpatientFee(pt_id, params);
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
    
    
    @ApiOperation(value="10.09 住院診察費設定(delete)", notes="")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/inpatientfee/{pt_id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePaymentInpatientfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = ptInpatientFeeService.deleteInpatientFee(pt_id);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>=0) {
                retMap.put("message", "刪除成功。/id="+pt_id);
            } else {
                retMap.put("message", "刪除失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    //==== 病房費設定 Ward fee
    @ApiOperation(value="10.10 病房費設定(get)", notes="")
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }", response=PtWardFeeDto.class)
    })
    @RequestMapping(value = "/payment/wardfee/{pt_id}", method = RequestMethod.POST)
    public ResponseEntity<?> getPaymentWardfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.Map<String, Object> retMap = ptWardFeeService.findWardFee(pt_id);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10.11 病房費設定(add)", notes="category = \"病房費\"")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/wardfee/add", method = RequestMethod.POST)
    public ResponseEntity<?> addPaymentWardfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @RequestBody PtWardFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = 0;
            long ptId = ptWardFeeService.addWardFee(params);
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
    
    @ApiOperation(value="10.12 病房費設定(update)", notes="<b>category 無法變更</b>")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/wardfee/{pt_id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePaymentWardfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id,
        @RequestBody PtWardFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = ptWardFeeService.updateWardFee(pt_id, params);
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
    
    @ApiOperation(value="10.13 病房費設定(delete)", notes="")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/wardfee/{pt_id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePaymentWardfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = ptWardFeeService.deleteWardFee(pt_id);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>=0) {
                retMap.put("message", "刪除成功。/id="+pt_id);
            } else {
                retMap.put("message", "刪除失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    //==== 精神慢性病房費 Psychiatric Ward Fee
    @ApiOperation(value="10.14 精神慢性病房費(get)", notes="")
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }", response=PtPsychiatricWardFeeDto.class)
    })
    @RequestMapping(value = "/payment/psychiatricwardfee/{pt_id}", method = RequestMethod.POST)
    public ResponseEntity<?> getPaymentPsychiatricWardfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.Map<String, Object> retMap = ptPsychiatricWardFeeService.findPsychiatricWardFee(pt_id);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    
    @ApiOperation(value="10.15 精神慢性病房費(add)", notes="category = \"精神慢性病房費\"")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/psychiatricwardfee/add", method = RequestMethod.POST)
    public ResponseEntity<?> addPaymentPsychiatricWardfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @RequestBody PtPsychiatricWardFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = 0;
            long ptId = ptPsychiatricWardFeeService.addPsychiatricWardFee(params);
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
    
    @ApiOperation(value="10.16 精神慢性病房費(update)", notes="<b>category 無法變更</b>")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/psychiatricwardfee/{pt_id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePaymentPsychiatricWardfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id,
        @RequestBody PtPsychiatricWardFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = ptPsychiatricWardFeeService.updatePsychiatricWardFee(pt_id, params);
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
    
    
    @ApiOperation(value="10.17 精神慢性病房費(delete)", notes="")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/psychiatricwardfee/{pt_id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePaymentPsychiatricWardfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = ptPsychiatricWardFeeService.deletePsychiatricWardFee(pt_id);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>=0) {
                retMap.put("message", "刪除成功。/id="+pt_id);
            } else {
                retMap.put("message", "刪除失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    //==== 手術費設定 surgery fee

}
