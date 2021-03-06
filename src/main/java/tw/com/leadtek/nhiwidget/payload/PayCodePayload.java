/**
 * Created on 2021/9/11.
 */
package tw.com.leadtek.nhiwidget.payload;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.model.rdb.PAY_CODE;
import tw.com.leadtek.tools.DateTool;

@ApiModel("代碼品項資料")
public class PayCodePayload extends PAY_CODE implements Serializable {

  private static final long serialVersionUID = 3669628497114655207L;

  public final static HashMap<String, String> HOSP_LEVEL = new HashMap<String, String>() {
    private static final long serialVersionUID = 2957738062337123409L;

    {
      put("0", "基層院所");
      put("1", "醫學中心");
      put("2", "區域醫院");
      put("3", "地區醫院");
    }
  };

  @ApiModelProperty(value = "是否為二類特材", example = "false", required = false)
  protected Boolean second;

  @ApiModelProperty(value = "代碼適用醫院層級", example =" [\"基層院所\",\"地區醫院\"] ", required = false)
  protected List<String> level;

  @ApiModelProperty(value = "生效日", example = "2021/01/01", required = false)
  protected String sday;

  @ApiModelProperty(value = "終止日", example = "2021/06/30", required = false)
  protected String eday;

  public PayCodePayload() {

  }

  public PayCodePayload(PAY_CODE pc) {
    id = pc.getId();
    code = pc.getCode();
    name = pc.getName();
    point = pc.getPoint();
    inhCode = pc.getInhCode();
    inhName = pc.getInhName();
    ownExpense = pc.getOwnExpense();
    codeType = pc.getCodeType();
    atc = pc.getAtc();
    
    SimpleDateFormat sdf = new SimpleDateFormat(DateTool.SDF);
    if (pc.getStartDate() != null) {
      sday = sdf.format(pc.getStartDate());
    }
    if (pc.getEndDate() == null) {
      try {
        pc.setEndDate(sdf.parse(DateTool.MAX_DATE));
      } catch (ParseException e) {
      }
    }
    eday = sdf.format(pc.getEndDate());

    level = new ArrayList<String>();
    if (pc.getHospLevel() != null) {
      String[] ss = pc.getHospLevel().split(",");
      for (String string : ss) {
        level.add(HOSP_LEVEL.get(string));
      }
    }
    second = false;
    if (pc.getSecSm() != null && pc.getSecSm().intValue() == 1) {
      second = true;
    }
  }

  public PAY_CODE toDB() {
    PAY_CODE result = new PAY_CODE();
    result.setId(id);
    result.setCode(code);
    result.setName(name);
    result.setPoint(point);
    result.setInhCode(inhCode);
    result.setInhName(inhName);
    result.setOwnExpense(ownExpense);
    result.setCodeType(codeType);
    result.setAtc(atc);
    result.setRedisId(redisId);
    result.setUpdateAt(new Date());

    result.setStartDate(DateTool.stringToDate(sday));
    if (eday == null || eday.length() == 0) {
      eday = DateTool.MAX_DATE;
    }
    result.setEndDate(DateTool.stringToDate(eday));
    StringBuffer sb = new StringBuffer();
    if (level != null && level.size() > 0) {
      for (String string : level) {
        for (String key : HOSP_LEVEL.keySet()) {
          if (HOSP_LEVEL.get(key).equals(string)) {
            sb.append(key);
            sb.append(",");
            break;
          }
        }
      }
      if (sb.length() > 0 && sb.charAt(sb.length() - 1) == ',') {
        sb.deleteCharAt(sb.length() - 1);
      }
      result.setHospLevel(sb.toString());
    }
    result.setSecSm((second != null && second.booleanValue()) ? 1 : 0);
    return result;
  }

  public Boolean getSecond() {
    return second;
  }

  public void setSecond(Boolean second) {
    this.second = second;
  }

  public List<String> getLevel() {
    return level;
  }

  public void setLevel(List<String> level) {
    this.level = level;
  }

  public String getSday() {
    return sday;
  }

  public void setSday(String sday) {
    this.sday = sday;
  }

  public String getEday() {
    return eday;
  }

  public void setEday(String eday) {
    this.eday = eday;
  }

}
