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
import tw.com.leadtek.nhiwidget.dto.PaymentTermsSearchDto;
import tw.com.leadtek.nhiwidget.dto.PaymentTermsSearchPl;
import tw.com.leadtek.nhiwidget.dto.PtOutpatientFeeDto;
import tw.com.leadtek.nhiwidget.dto.PtOutpatientFeePl;
import tw.com.leadtek.nhiwidget.service.PaymentTermsService;
import tw.com.leadtek.nhiwidget.service.PtOutpatientFeeService;
import tw.com.leadtek.tools.Utility;


@Api(value = "健保標準給付額 支付條件設定 API", tags = {"10 健保標準給付額 支付條件設定"})
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class E2101Controll {
    @Value("${springfox.documentation.swagger.use-model-v3:true}")
    boolean useModelV3;
    
    @Autowired
    private PaymentTermsService paymentTermsService;
    @Autowired
    private PtOutpatientFeeService ptOutpatientFeeService;

    @ApiOperation(value="10.1 支付條件設定搜尋", notes="Result: PaymentTermsSearchDto")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="[PaymentTermsSearchDto, ...]", response=PaymentTermsSearchDto.class)
    })
    @RequestMapping(value = "/payment/terms/search", method = RequestMethod.POST)
    public ResponseEntity<?> paymentTermsSearh(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @RequestBody PaymentTermsSearchPl params) throws Exception {
        System.out.println("useModelV3="+useModelV3);
        java.util.Map<String, Object> jwtValidation = Utility.jwtValidate(jwt);
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
    
    @ApiOperation(value="10.2 get 門診診察費設定", notes="Result: PtOutpatientFeeDto")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ PtOutpatientFeeDto }", response=PtOutpatientFeeDto.class)
//        @ApiResponse(responseCode = "200", description = "登入成功"),
    })
    @RequestMapping(value = "/payment/outpatientfee/{pt_id}", method = RequestMethod.POST)
    public ResponseEntity<?> getPaymentOutpatientfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = Utility.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.Map<String, Object> retMap = ptOutpatientFeeService.findOutpatientFee(pt_id);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }

    
    @ApiOperation(value="10.3 add 門診診察費設定", notes="")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
//        @ApiResponse(responseCode = "200", description = "登入成功"),
    })
    @RequestMapping(value = "/payment/outpatientfee/add", method = RequestMethod.POST)
    public ResponseEntity<?> addPaymentOutpatientfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @RequestBody PtOutpatientFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = Utility.jwtValidate(jwt);
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


}
