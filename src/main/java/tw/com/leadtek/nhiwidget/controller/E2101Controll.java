package tw.com.leadtek.nhiwidget.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import tw.com.leadtek.nhiwidget.dto.PaymentTermsSearchDto;
import tw.com.leadtek.nhiwidget.payload.PaymentTermsSearchPl;
import tw.com.leadtek.nhiwidget.service.PaymentTermsService;
import tw.com.leadtek.tools.Utility;


@Api(value = "健保標準給付額 支付條件設定 API", tags = {"10 健保標準給付額 支付條件設定"})
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class E2101Controll {
    @Autowired
    private PaymentTermsService paymentTermsService;

    @ApiOperation(value="10.1 支付條件設定搜尋", notes="Result: PaymentTermsSearchDto")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="[PaymentTermsSearchDto, ...]", response=PaymentTermsSearchDto.class)
    })
    @RequestMapping(value = "/payment/terms/search", method = RequestMethod.POST)
    public ResponseEntity<?> paymentTermsSearh(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @RequestBody PaymentTermsSearchPl params) throws Exception {
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


    


}
