/**
 * Created on 2022/2/24.
 */
package tw.com.leadtek.nhiwidget.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import tw.com.leadtek.nhiwidget.annotation.LogDefender;
import tw.com.leadtek.nhiwidget.constant.LogType;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;
import tw.com.leadtek.nhiwidget.payload.system.VersionResponse;
import tw.com.leadtek.nhiwidget.service.SystemService;

@Api(tags = "不需認證之相關API", value = "不需認證之相關API")
@CrossOrigin(origins = "*", maxAge = 36000)
@RestController
@RequestMapping(value = "/no", produces = "application/json; charset=utf-8")
public class NoAuthController extends BaseController {
  
  @Autowired
  private SystemService systemService;
  
  @Value("${project.version}")
  private String version;

  @GetMapping("/downloadXML/{id}") 
  @LogDefender(value = {LogType.SIGNIN})
  public ResponseEntity<BaseResponse> downloadSampleFile(@PathVariable String id,
    HttpServletResponse response){
     try {
      systemService.downloadXML(id, response);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  @ApiOperation(value = "取得版本編號", notes = "取得版本編號")
  @GetMapping("/version") 
  public ResponseEntity<VersionResponse> getVersion(){
    return ResponseEntity.ok(new VersionResponse(version, 1));
  }
  
}
