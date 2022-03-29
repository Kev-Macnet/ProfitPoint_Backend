/**
 * Created on 2022/3/28.
 */
package tw.com.leadtek.nhiwidget.payload.my;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.http.ResponseEntity;
import tw.com.leadtek.nhiwidget.payload.mr.HomepageParameters;
import tw.com.leadtek.nhiwidget.security.service.UserDetailsImpl;
import tw.com.leadtek.tools.DateTool;

public class SecondFilterParameter extends HomepageParameters {

  /**
   * 用戶登入資訊
   */
  protected UserDetailsImpl user;
  
  /**
   * 科別中文名稱，如:不分科、:家醫科、內科、外科...
   */
  protected String funcTypec;
  
  /**
   * 醫護代碼
   */
  protected String prsnId;
  
  /**
   * 負責人員代碼
   */
  protected String applId;
  
  /**
   * 負責人員姓名
   */
  protected String applName;

  /**
   * 點擊區塊名稱
   */
  protected String block;
  
  /**
   * 需排序的欄位名稱
   */
  protected String orderBy;
  
  /**
   * true:由小至大，false:由大至小
   */
  protected Boolean asc;
  
  /**
   * 每頁顯示筆數
   */
  protected int perPage; 
  
  /**
   * 第幾頁
   */
  protected int page;
  
  /**
   * 錯誤訊息
   */
  protected String message;
  
  public SecondFilterParameter() {
    
  }
  
  public SecondFilterParameter(UserDetailsImpl user, Boolean isOp, Boolean isIp, 
      String sdate, String edate, String block, int page) {
    this.user = user;
    this.page = page;
    if (isOp != null && isOp.booleanValue()) {
      if (isIp != null && isIp) {
        // null
      } else {
        dataFormat = "10";
      }
    } else if (isIp != null && isIp.booleanValue()) {
      dataFormat = "20";
    }
   
    if (sdate != null && edate != null) {
      SimpleDateFormat sdf = new SimpleDateFormat(DateTool.SDF);
      try {
        sDate = new java.sql.Date(sdf.parse(sdate).getTime());
        eDate = new java.sql.Date(sdf.parse(edate).getTime());
        if (sDate.getTime() > eDate.getTime()) {
          message = "啟始日不可大於結束日";
        }
      } catch (ParseException e) {
        message = "日期格式有誤";
      }
    }
    this.block = block;
  }

  public UserDetailsImpl getUser() {
    return user;
  }

  public void setUser(UserDetailsImpl user) {
    this.user = user;
  }

  public String getFuncTypec() {
    return funcTypec;
  }

  public void setFuncTypec(String funcTypec) {
    this.funcTypec = funcTypec;
  }

  public String getPrsnId() {
    return prsnId;
  }

  public void setPrsnId(String prsnId) {
    this.prsnId = prsnId;
  }

  public String getApplId() {
    return applId;
  }

  public void setApplId(String applId) {
    this.applId = applId;
  }

  public String getApplName() {
    return applName;
  }

  public void setApplName(String applName) {
    this.applName = applName;
  }

  public String getBlock() {
    return block;
  }

  public void setBlock(String block) {
    this.block = block;
  }

  public String getOrderBy() {
    return orderBy;
  }

  public void setOrderBy(String orderBy) {
    this.orderBy = orderBy;
  }

  public Boolean getAsc() {
    return asc;
  }

  public void setAsc(Boolean asc) {
    this.asc = asc;
  }

  public int getPerPage() {
    return perPage;
  }

  public void setPerPage(int perPage) {
    this.perPage = perPage;
  }

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
  
}
