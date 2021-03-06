/**
 * Created on 2021/5/3.
 */
package tw.com.leadtek.nhiwidget.controller;

import java.text.DecimalFormat;
import java.util.Calendar;
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

@Api(tags = "?????????????????????API", value = "?????????????????????API")
@CrossOrigin(origins = "*", maxAge = 36000)
@RestController
// @CrossOrigin(value = "http://localhost:8080")
@RequestMapping(produces = "application/json; charset=utf-8") // reponse ????????????????????????
public class UserController extends BaseController {

  @Autowired
  private UserService userService;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private JwtUtils jwtUtils;

  @Autowired
  private LogDataService logService;

  @ApiOperation(value = "??????????????????", notes = "??????????????????")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "????????????"),
      @ApiResponse(responseCode = "400", description = "?????????????????????")})
  @PostMapping("/auth/user")
  public ResponseEntity<BaseResponse> newUser(
      @ApiParam(value = "????????????") @RequestBody(required = true) UserRequest request) {

    if (userService.getUserCount() > 0) {
      // ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
      UserDetailsImpl loginUser = getUserDetails();
      if (loginUser == null) {
        BaseResponse result = new BaseResponse();
        result.setResult("error");
        result.setMessage("????????????????????????");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
      }
    }
    USER result = userService.newUser(request);
    if (result != null) {
      return returnIDResult(result.getId().toString());
    } else {
      return returnAPIResult("?????????????????????");
    }
  }

  @ApiOperation(value = "????????????", notes = "????????????")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "????????????"),
      @ApiResponse(responseCode = "400", description = "???????????????")})
  @PutMapping("/auth/forgetPassword")
  public ResponseEntity<BaseResponse> forgetPassword(@ApiParam(name = "username", value = "????????????",
      example = "test") @RequestParam(required = true) String username) {
    String result = userService.forgetPassword(username, null);
    if (result == null) {
      return returnAPIResult(null);
    } else
      return returnAPIResult(result);
  }

  @ApiOperation(value = "????????????", notes = "????????????")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "??????")})
  @DeleteMapping("/user/{id}")
  public ResponseEntity<?> deleteUser(
      @ApiParam(name = "id", value = "??????id", example = "1") @PathVariable String id) {
    logger.info("/user/{id}: delete:" + id);
    String deleteId = HtmlUtils.htmlEscape(id, "UTF-8");
    try {
      return returnAPIResult(userService.deleteUser(Long.parseLong(deleteId)));
    } catch (NumberFormatException e) {
      return returnAPIResult("id ??????");
    }
  }

  @ApiOperation(value = "????????????", notes = "????????????")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "????????????"),
      @ApiResponse(responseCode = "400", description = "???????????????")})
  @PutMapping("/user/{id}")
  public ResponseEntity<BaseResponse> updateUser(
      @ApiParam(name = "id", value = "??????id", example = "1") @PathVariable String id,
      @RequestBody UserRequest request) {
    String updateId = HtmlUtils.htmlEscape(id, "UTF-8");
    try {
      Long lid = Long.parseLong(updateId);
      if (lid.longValue() != request.getId().longValue()) {
        return returnAPIResult("id ?????????");
      }
      return returnAPIResult(userService.updateUser(request));
    } catch (NumberFormatException e) {
      return returnAPIResult("id ??????");
    }
  }

  @ApiOperation(value = "????????????id????????????", notes = "????????????id????????????")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "??????")})
  @GetMapping("/user/{id}")
  public ResponseEntity<UserRequest> getUserById(
      @ApiParam(name = "id", value = "user id", example = "1") @PathVariable String id) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Object obj = authentication.getPrincipal();
    if (obj instanceof String) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new UserRequest("??????????????????????????????"));
    }
    UserDetailsImpl userDetail = (UserDetailsImpl) obj;
    Long idL = 0L;
    idL = Long.parseLong(id);
    if (userDetail.getId().longValue() != idL.longValue()) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new UserRequest("??????????????????????????????"));
    }
    return ResponseEntity.ok(userService.getUserById(idL));
  }

  @ApiOperation(value = "??????????????????", notes = "??????????????????")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "??????")})
  @GetMapping("/user")
  public ResponseEntity<List<UserRequest>> getAllUser(
      @ApiParam(name = "funcType", value = "????????????",
          example = "00") @RequestParam(required = false) String funcType,
      @ApiParam(name = "funcTypeC", value = "???????????????????????????????????????????????????...",
          example = "?????????") @RequestParam(required = false) String funcTypeC,
      @ApiParam(name = "funcTypec", value = "???????????????????????????????????????????????????...",
          example = "?????????") @RequestParam(required = false) String funcTypec,
      @ApiParam(name = "rocId",
          value = "????????????rocId???inhId??????") @RequestParam(required = false) String rocId,
      @ApiParam(name = "inhId",
          value = "????????????rocId???inhId??????") @RequestParam(required = false) String inhId,
      @ApiParam(name = "name", value = "????????????",
          example = "?????????") @RequestParam(required = false) String name,
      @ApiParam(value = "???????????????E:???????????????D:???????????????U:???????????????", example = "E") @RequestParam(required = false,
          defaultValue = "E") String role,
      @ApiParam(value = "???????????????", example = "test") @RequestParam(required = false) String keyword) {
    // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    // System.out.println("currentPrincipalName:" + authentication.getName());
    // UserDetailsImpl userDetail = (UserDetailsImpl) authentication.getPrincipal();
    // System.out.println(userDetail.getEmail());
    String id = (rocId == null) ? inhId : rocId;
    String funcTypeChinese = (funcTypec != null) ? funcTypec : funcTypeC;
    return ResponseEntity
        .ok(userService.getAllUser(funcType, funcTypeChinese, id, name, role, keyword));
  }

  @ApiOperation(value = "????????????", notes = "????????????")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "????????????"),
      @ApiResponse(responseCode = "400", description = "???????????????")})
  @PutMapping("/user/changePassword")
  public ResponseEntity<BaseResponse> changePassword(@Valid @RequestBody ChangePassword cp) {
    if (cp.getOldPassword() == null || cp.getNewPassword() == null) {
      return returnAPIResult("???????????????????????????");
    }
    if (cp.getNewPassword().length() < 6 || cp.getNewPassword().length() > 10) {
      return returnAPIResult("?????????????????????6-10???");
    }
    if (cp.getOldPassword().equals(cp.getNewPassword())) {
      return returnAPIResult("????????????????????????");
    }
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String loginResult = userService.login(authentication.getName(), cp.getOldPassword());
    if (loginResult != null) {
      return returnAPIResult(loginResult);
    }
    if (userService.changePassword(authentication.getName(), cp.getNewPassword())) {
      return returnAPIResult(null);
    } else {
      return returnAPIResult("??????????????????");
    }
  }

  @ApiOperation(value = "??????", notes = "???????????? session ??????")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "????????????")})
  @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", dataType = "string",
      paramType = "header", required = true)})
  @PostMapping("/user/logout")
  public ResponseEntity<BaseResponse> logout(HttpServletRequest request,
      @RequestHeader("Authorization") String jwt) {
    UserDetailsImpl user = getUserDetails();
    logger.info("logout user=" + user);
    if (user == null || (jwt == null || jwt.indexOf(' ') < 0 || jwt.split(" ").length != 2)) {
      return returnAPIResult(null);
    }
    if (jwt != null && jwt.length() > 20) {
      int status = logService.setLogout(jwt.split(" ")[1]);
    }
    userService.logoutLog(user.getUsername(), jwt.split(" ")[1]);
    request.getSession().invalidate();
    return returnAPIResult(null);
  }

  @ApiOperation(value = "??????", notes = "??????????????????????????? JWT")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "????????????"),
      @ApiResponse(responseCode = "401", description = "Unauthorized ????????????")})
  @PostMapping("/auth/login")
  public ResponseEntity<JwtResponse> authenticateUser(
      @Valid @RequestBody LoginRequest loginRequest) {
    Authentication authentication =
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
            loginRequest.getUsername(), loginRequest.getPassword()));

    USER user = userService.findUser(loginRequest.getUsername());
    if (user == null) {
      JwtResponse jwt = new JwtResponse();
      jwt.setResult("error");
      jwt.setMessage("???????????????");
      return new ResponseEntity<JwtResponse>(jwt, HttpStatus.FORBIDDEN);
    }
    if (user.getStatus() != null && user.getStatus().intValue() == 0) {
      JwtResponse jwt = new JwtResponse();
      jwt.setResult("error");
      jwt.setMessage("???????????????");
      return new ResponseEntity<JwtResponse>(jwt, HttpStatus.FORBIDDEN);
    }
    if (parametersService.getParameter("HOSP_EXPIRE") != null) {
      String expireDate = userService.checkExpire(parametersService.getParameter("HOSP_EXPIRE"));
      if (expireDate != null && expireDate.length() > 0) {
        JwtResponse jwt = new JwtResponse();
        jwt.setResult("error");
        if (expireDate.length() > 0) {
          jwt.setMessage("PrpfitPoint????????????(" + expireDate + ")????????????????????????????????????????????????????????????????????????????????????");
        } else {
          jwt.setMessage("PrpfitPoint????????????????????????????????????????????????????????????????????????????????????????????????");
        }
        return new ResponseEntity<JwtResponse>(jwt, HttpStatus.FORBIDDEN);
      }
    }
    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);
    logService.setLogin(loginRequest.getUsername(), jwt);
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    userService.loginLog(loginRequest.getUsername(), jwt);
    // List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
    // .collect(Collectors.toList());

    return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getRole(), userDetails.getUsername(),
        userDetails.getDisplayName(), userDetails.getId(),
        userService.needChangePassword(userDetails.getId())));
  }

  /**
   * ???????????????/???????????????
   * 
   * @param name
   * @param model
   * @return
   */
  @ApiOperation(value = "??????????????????", notes = "??????????????????")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "??????")})
  @GetMapping("/department")
  public ResponseEntity<List<DEPARTMENT>> getAllDepartments(
      @ApiParam(name = "funcType", value = "????????????",
          example = "00") @RequestParam(required = false) String funcType,
      @ApiParam(name = "funcTypec", value = "???????????????????????????????????????????????????...",
          example = "?????????") @RequestParam(required = false) String funcTypec,
      @ApiParam(name = "funcTypeC", value = "???????????????????????????????????????????????????...",
          example = "?????????") @RequestParam(required = false) String funcTypeC) {
    String funcTypeChinese = (funcTypec != null) ? funcTypec : funcTypeC;
    return ResponseEntity.ok(userService.getAllDepartment(funcType, funcTypeChinese));
  }

  @ApiOperation(value = "????????????", notes = "????????????")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "????????????"),
      @ApiResponse(responseCode = "400", description = "?????????????????????")})
  @PostMapping("/department")
  public ResponseEntity<BaseResponse> newDepartment(@RequestBody DEPARTMENT request) {
    // public boolean addSynonym(Synonym synonym, List<SynonymField> details) {
    logger.info("/newDepartment:" + request.getName() + "," + request);
    DEPARTMENT result = userService.newDepartment(request);
    if (result != null) {
      return returnIDResult(result.getId().toString());
    } else {
      return returnAPIResult("?????????????????????");
    }
  }

  @ApiOperation(value = "????????????", notes = "????????????")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "????????????"),
      @ApiResponse(responseCode = "400", description = "????????????id??????")})
  @DeleteMapping("/department/{id}")
  public ResponseEntity<BaseResponse> deleteDepartment(
      @ApiParam(name = "id", value = "??????id", example = "1") @PathVariable String id) {
    String deleteId = HtmlUtils.htmlEscape(id, "UTF-8");
    try {
      return returnAPIResult(userService.deleteDepartment(Long.parseLong(deleteId)));
    } catch (NumberFormatException e) {
      return returnAPIResult("id ??????");
    }
  }

  @ApiOperation(value = "????????????", notes = "????????????")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "????????????"),
      @ApiResponse(responseCode = "400", description = "???????????????id")})
  @PutMapping("/department/{id}")
  public ResponseEntity<BaseResponse> updateDepartment(
      @ApiParam(name = "id", value = "??????id", example = "1") @PathVariable String id,
      @RequestBody DEPARTMENT request) {
    String updateId = HtmlUtils.htmlEscape(id, "UTF-8");
    try {
      Long lid = Long.parseLong(updateId);
      if (lid.longValue() != request.getId().longValue()) {
        return returnAPIResult("id ?????????");
      }
      return returnAPIResult(userService.updateDepartment(request));
    } catch (NumberFormatException e) {
      return returnAPIResult("id ??????");
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
