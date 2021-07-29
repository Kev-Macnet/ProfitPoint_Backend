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
import tw.com.leadtek.nhiwidget.dto.PtSurgeryFeeDto;
import tw.com.leadtek.nhiwidget.dto.PtSurgeryFeePl;
import tw.com.leadtek.nhiwidget.service.PaymentTermsService;
import tw.com.leadtek.nhiwidget.service.PtInpatientFeeService;
import tw.com.leadtek.nhiwidget.service.PtOutpatientFeeService;
import tw.com.leadtek.nhiwidget.service.PtSurgeryFeeService;
import tw.com.leadtek.tools.Utility;


@Api(value = "健保標準給付額 支付條件設定 API", tags = {"10-4 健保標準給付額 支付條件設定"})
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class E2101dControll {
    
    @Autowired
    private PaymentTermsService paymentTermsService;
    @Autowired
    private PtSurgeryFeeService ptSurgeryFeeService;
    
    //==== 手術費設定 surgery fee
    @ApiOperation(value="10d.09 手術費設定(get)", notes="")
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }", response=PtSurgeryFeeDto.class)
    })
    @RequestMapping(value = "/payment/surgeryfee/{pt_id}", method = RequestMethod.POST)
    public ResponseEntity<?> getPaymentSurgeryfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.Map<String, Object> retMap = ptSurgeryFeeService.findSurgeryFee(pt_id);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10d.10 手術費設定(add)", notes="category = \"手術費\"")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/surgeryfee/add", method = RequestMethod.POST)
    public ResponseEntity<?> addPaymentSurgeryfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @RequestBody PtSurgeryFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = 0;
            long ptId = ptSurgeryFeeService.addSurgeryFee(params);
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
    
    @ApiOperation(value="10d.11 手術費設定(update)", notes="<b>category 無法變更</b>")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/surgeryfee/{pt_id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePaymentSurgeryfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id,
        @RequestBody PtSurgeryFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = ptSurgeryFeeService.updateSurgeryFee(pt_id, params);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
                retMap.put("message", "修改成功。/id="+pt_id);
                retMap.put("notify", "修改時 category 無法變更");
            } else {
                retMap.put("message", "修改失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10d.12 手術費設定(delete)", notes="")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/surgeryfee/{pt_id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePaymentSurgeryfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = ptSurgeryFeeService.deleteSurgeryFee(pt_id);
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


}
