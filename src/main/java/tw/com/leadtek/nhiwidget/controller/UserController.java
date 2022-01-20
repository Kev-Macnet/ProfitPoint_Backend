/**
 * Created on 2021/5/3.
 */
package tw.com.leadtek.nhiwidget.controller;

import java.security.Principal;
import java.text.DecimalFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import tw.com.leadtek.nhiwidget.model.rdb.DEDUCTED_NOTE;
import tw.com.leadtek.nhiwidget.model.rdb.DEPARTMENT;
import tw.com.leadtek.nhiwidget.model.rdb.USER;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;
import tw.com.leadtek.nhiwidget.payload.MemoryStatus;
import tw.com.leadtek.nhiwidget.payload.UserRequest;
import tw.com.leadtek.nhiwidget.security.jwt.ChangePassword;
import tw.com.leadtek.nhiwidget.security.jwt.JwtResponse;
import tw.com.leadtek.nhiwidget.security.jwt.JwtUtils;
import tw.com.leadtek.nhiwidget.security.jwt.LoginRequest;
import tw.com.leadtek.nhiwidget.security.service.UserDetailsImpl;
import tw.com.leadtek.nhiwidget.service.LogDataService;
import tw.com.leadtek.nhiwidget.service.UserService;

@Api(tags = "帳號、權限相關API", value = "帳號、權限相關API")
@CrossOrigin(origins = "*", maxAge = 36000)
@RestController
// @CrossOrigin(value = "http://localhost:8080")
@RequestMapping(produces = "application/json; charset=utf-8") // reponse 有中文才不會亂碼
public class UserController extends BaseController {

  @Autowired
  private UserService userService;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private JwtUtils jwtUtils;
  
  @Autowired
  private LogDataService logService;

  @ApiOperation(value = "新增一組帳號", notes = "新增一組帳號")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "新增成功"),
      @ApiResponse(responseCode = "400", description = "已有相同的名稱")})
  @PostMapping("/auth/user")
  public ResponseEntity<BaseResponse> newUser(
      @ApiParam(value = "帳號內容") @RequestBody(required = true) UserRequest request) {
    USER result = userService.newUser(request);
    if (result != null) {
      return returnIDResult(result.getId().toString());
    } else {
      return returnAPIResult("已有相同的名稱");
    }
  }
  
  @ApiOperation(value = "忘記密碼", notes = "更換密碼")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "舊密碼有誤")})
  @PutMapping("/auth/forgetPassword")
  public ResponseEntity<BaseResponse> forgetPassword(
      @ApiParam(name = "username", value = "登入帳號",
          example = "test") @RequestParam(required = true) String username,
      @ApiParam(name = "email", value = "email",
          example = "test@test.com") @RequestParam(required = true) String email) {
    String result = userService.forgetPassword(username, email);
    if (result == null) {
      return returnAPIResult(null);
    } else
      return returnAPIResult(result);
  }

  @ApiOperation(value = "刪除帳號", notes = "刪除帳號")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @DeleteMapping("/user/{id}")
  public ResponseEntity<?> deleteUser(
      @ApiParam(name = "id", value = "帳號id", example = "1") @PathVariable String id) {
    logger.info("/user/{id}: delete:" + id);
    String deleteId = HtmlUtils.htmlEscape(id, "UTF-8");
    try {
      return returnAPIResult(userService.deleteUser(Long.parseLong(deleteId)));
    } catch (NumberFormatException e) {
      return returnAPIResult("id 有誤");
    }
  }

  @ApiOperation(value = "更新帳號", notes = "更新帳號")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "未找到帳號")})
  @PutMapping("/user/{id}")
  public ResponseEntity<BaseResponse> updateUser(
      @ApiParam(name = "id", value = "帳號id", example = "1") @PathVariable String id,
      @RequestBody UserRequest request) {
    String updateId = HtmlUtils.htmlEscape(id, "UTF-8");
    try {
      Long lid = Long.parseLong(updateId);
      if (lid.longValue() != request.getId().longValue()) {
        return returnAPIResult("id 不符合");
      }
      return returnAPIResult(userService.updateUser(request));
    } catch (NumberFormatException e) {
      return returnAPIResult("id 有誤");
    }
  }

  @ApiOperation(value = "取得指定id帳號資訊", notes = "取得指定id帳號資訊")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @GetMapping("/user/{id}")
  public ResponseEntity<UserRequest> getUserById(
      @ApiParam(name = "id", value = "user id", example = "1") @PathVariable String id) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Object obj = authentication.getPrincipal();
    if (obj instanceof String) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new UserRequest("未登入，無法取得資訊"));
    }
    UserDetailsImpl userDetail = (UserDetailsImpl) obj;
    Long idL = 0L;
    idL = Long.parseLong(id);
    if (userDetail.getId().longValue() != idL.longValue()) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new UserRequest("未登入，無法取得資訊"));
    }
    return ResponseEntity.ok(userService.getUserById(idL));
  }
  
  @ApiOperation(value = "取得所有帳號", notes = "取得所有帳號")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @GetMapping("/user")
  public ResponseEntity<List<UserRequest>> getAllUser(@ApiParam(name = "funcType", value = "科別代碼",
      example = "00") @RequestParam(required = false) String funcType,
      @ApiParam(name = "funcTypeC", value = "科別中文名，如不分科、家醫科、內科...",
      example = "不分科") @RequestParam(required = false) String funcTypeC,
      @ApiParam(name = "funcTypec", value = "科別中文名，如不分科、家醫科、內科...", example = "家醫科") 
      @RequestParam(required = false) String funcTypec,
      @ApiParam(name = "rocId", value = "醫護代碼rocId或inhId都可") @RequestParam(required = false) String rocId,
      @ApiParam(name = "inhId", value = "醫護代碼rocId或inhId都可") @RequestParam(required = false) String inhId,
      @ApiParam(name = "name", value = "醫護名稱",
        example = "王小明") @RequestParam(required = false) String name,
      @ApiParam(value = "帳號權限，E:醫護人員，D:申報人員，U:使用者清單",
        example = "E") @RequestParam(required = false, defaultValue = "E") String role,
      @ApiParam(value = "關鍵字查詢",
        example = "test") @RequestParam(required = false) String keyword) {
    // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    // System.out.println("currentPrincipalName:" + authentication.getName());
    // UserDetailsImpl userDetail = (UserDetailsImpl) authentication.getPrincipal();
    // System.out.println(userDetail.getEmail());
      String id = (rocId == null) ? inhId : rocId;
      String funcTypeChinese = (funcTypec != null) ? funcTypec : funcTypeC;
    return ResponseEntity.ok(userService.getAllUser(funcType, funcTypeChinese, id, name, role, keyword));
  }

  @ApiOperation(value = "更換密碼", notes = "更換密碼")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "舊密碼有誤")})
  @PutMapping("/user/changePassword")
  public ResponseEntity<BaseResponse> changePassword(@Valid @RequestBody ChangePassword cp) {
    if (cp.getOldPassword() == null || cp.getNewPassword() == null) {
      return returnAPIResult("新舊密碼不可為空值");
    }
    if (cp.getOldPassword().equals(cp.getNewPassword())) {
      return returnAPIResult("新舊密碼不可重複");
    }
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String loginResult = userService.login(authentication.getName(), cp.getOldPassword());
    if (loginResult != null) {
      return returnAPIResult(loginResult);
    }
    if (userService.changePassword(authentication.getName(), cp.getNewPassword())) {
      return returnAPIResult(null);
    } else {
      return returnAPIResult("更換密碼出錯");
    }
  }

  @ApiOperation(value = "登出", notes = "登出，將 session 清空")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "登出成功")})
  @ApiImplicitParams({
      @ApiImplicitParam(name="Authorization", dataType="string", paramType="header", required=true)
  })
  @PostMapping("/user/logout")
  public ResponseEntity<BaseResponse> logout(HttpServletRequest request, @RequestHeader("Authorization") String jwt) {
    request.getSession().invalidate();
    UserDetailsImpl user = getUserDetails();
    if (user == null || (jwt == null || jwt.indexOf(' ') < 0 || jwt.split(" ").length != 2)) {
      return returnAPIResult(null);
    }
    if (jwt !=null && jwt.length()>20) {
        int status = logService.setLogout(jwt.split(" ")[1]);
    }
    userService.logoutLog(user.getUsername(), jwt.split(" ")[1]);
    return returnAPIResult(null);
  }
  
  @ApiOperation(value = "登入", notes = "輸入帳號密碼，取得 JWT")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "登入成功"),
      @ApiResponse(responseCode = "401", description = "Unauthorized 帳密有誤")})
  @PostMapping("/auth/login")
  public ResponseEntity<JwtResponse> authenticateUser(
      @Valid @RequestBody LoginRequest loginRequest) {
    Authentication authentication =
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
            loginRequest.getUsername(), loginRequest.getPassword()));

    USER user = userService.findUser(loginRequest.getUsername());
    if (user != null && user.getStatus().intValue() == 0) {
      JwtResponse jwt = new JwtResponse();
      return new ResponseEntity<JwtResponse>(jwt, HttpStatus.FORBIDDEN); 
    }
    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);
    logService.setLogin(jwt);
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    userService.loginLog(loginRequest.getUsername(), jwt);
    // List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
    // .collect(Collectors.toList());

    return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getRole(), userDetails.getUsername(),
        userDetails.getDisplayName(), userDetails.getId(), userService.needChangePassword(userDetails.getId())));
  }

  /**
   * 取得同義詞/代碼表資料
   * 
   * @param name
   * @param model
   * @return
   */
  @ApiOperation(value = "取得所有部門", notes = "取得所有部門")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @GetMapping("/department")
  public ResponseEntity<List<DEPARTMENT>> getAllDepartments(
      @ApiParam(name = "funcType", value = "科別代碼", example = "00") 
      @RequestParam(required = false) String funcType,
      @ApiParam(name = "funcTypec", value = "科別中文名，如不分科、家醫科、內科...", example = "家醫科") 
      @RequestParam(required = false) String funcTypec,
      @ApiParam(name = "funcTypeC", value = "科別中文名，如不分科、家醫科、內科...", example = "家醫科") 
      @RequestParam(required = false) String funcTypeC) {
	String funcTypeChinese = (funcTypec != null) ? funcTypec : funcTypeC;
    return ResponseEntity.ok(userService.getAllDepartment(funcType, funcTypeChinese));
  }

  @ApiOperation(value = "新增部門", notes = "新增部門")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "新增成功"),
      @ApiResponse(responseCode = "400", description = "已有相同的名稱")})
  @PostMapping("/department")
  public ResponseEntity<BaseResponse> newDepartment(@RequestBody DEPARTMENT request) {
    // public boolean addSynonym(Synonym synonym, List<SynonymField> details) {
    logger.info("/newDepartment:" + request.getName() + "," + request);
    DEPARTMENT result = userService.newDepartment(request);
    if (result != null) {
      return returnIDResult(result.getId().toString());
    } else {
      return returnAPIResult("已有相同的名稱");
    }
  }

  @ApiOperation(value = "刪除部門", notes = "刪除部門")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "刪除成功"),
      @ApiResponse(responseCode = "400", description = "找不到該id部門")})
  @DeleteMapping("/department/{id}")
  public ResponseEntity<BaseResponse> deleteDepartment(
      @ApiParam(name = "id", value = "部門id", example = "1") @PathVariable String id) {
    String deleteId = HtmlUtils.htmlEscape(id, "UTF-8");
    try {
      return returnAPIResult(userService.deleteDepartment(Long.parseLong(deleteId)));
    } catch (NumberFormatException e) {
      return returnAPIResult("id 有誤");
    }
  }

  @ApiOperation(value = "更新部門", notes = "更新部門")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "未找到部門id")})
  @PutMapping("/department/{id}")
  public ResponseEntity<BaseResponse> updateDepartment(
      @ApiParam(name = "id", value = "部門id", example = "1") @PathVariable String id,
      @RequestBody DEPARTMENT request) {
    String updateId = HtmlUtils.htmlEscape(id, "UTF-8");
    try {
      Long lid = Long.parseLong(updateId);
      if (lid.longValue() != request.getId().longValue()) {
        return returnAPIResult("id 不符合");
      }
      return returnAPIResult(userService.updateDepartment(request));
    } catch (NumberFormatException e) {
      return returnAPIResult("id 有誤");
    }
  }
  
  @GetMapping("ms")
  public MemoryStatus getMemoryStatistics() {
    MemoryStatus stats = new MemoryStatus();
    DecimalFormat df = new DecimalFormat(",###.###");
    stats.setHeapSize(
        df.format((double) Runtime.getRuntime().totalMemory() / ((double) 1024 * (double) 1024))
            + "MB");
    stats.setHeapMaxSize(
        df.format((double) Runtime.getRuntime().maxMemory() / ((double) 1024 * (double) 1024))
            + "MB");
    stats.setHeapFreeSize(
        df.format((double) Runtime.getRuntime().freeMemory() / ((double) 1024 * (double) 1024))
            + "MB");
    return stats;
  }
}
