/**
 * Created on 2021/8/26.
 */
package tw.com.leadtek.nhiwidget.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tw.com.leadtek.nhiwidget.dao.DRG_CODEDao;
import tw.com.leadtek.nhiwidget.dao.IP_DDao;
import tw.com.leadtek.nhiwidget.dao.IP_PDao;
import tw.com.leadtek.nhiwidget.model.DrgCalculate;
import tw.com.leadtek.nhiwidget.model.rdb.DRG_CODE;
import tw.com.leadtek.tools.DateTool;

/**
 * 計算DRG定額並提供存取DRG_CAL table的service.
 * 
 * @author kenlai
 */
@Service
public class DrgCalService {

  public final static int ADD_CHILD_NONE = 0;

  public final static int ADD_CHILD_6M = 1;

  public final static int ADD_CHILD_2Y = 2;

  public final static int ADD_CHILD_6Y = 3;

  /**
   * 不得加計各項加成或其他另行加計之醫療點數之DRG Code
   */
  public final static List<String> DRG_NO_ADD = new ArrayList<String>() {
    {
      add("513");
    }
  };

  private Logger logger = LogManager.getLogger();

  @Autowired
  private ParametersService parameters;

  @Autowired
  private DRG_CODEDao drgDao;

  @Autowired
  private IP_PDao ippDao;

  @Autowired
  private IP_DDao ipdDao;

  /**
   * 取得醫院加成率
   * 
   * @return
   */
  private float getHospAdd(Date date, boolean isM, boolean isMDC15, int childAdd) {
    float result = 1;
    // 山地離島加成率
    if (parameters.getParameter("HOSP_OUT") != null
        && "Y".equals(parameters.getParameter("HOSP_OUT"))) {
      result += Float.parseFloat(parameters.getParameter("OL"));
    }
    // 基本診療加成率
    String hospLevel = parameters.getParameter("HOSP_LEVEL");
    result += (Float) parameters.getParameterValueBetween("ADD_" + hospLevel, date);

    // CMI 加成率
    float cmi = (Float) parameters.getParameterValueBetween("CMI", date);
    if (cmi >= 0.011f && cmi <= 0.012f) {
      cmi = (Float) parameters.getParameterValueBetween("CMI2", date);
    } else if (cmi >= 0.012f && cmi <= 0.013f) {
      cmi = (Float) parameters.getParameterValueBetween("CMI3", date);
    } else if (cmi > 0.013f) {
      cmi = (Float) parameters.getParameterValueBetween("CMI4", date);
    }
    result += cmi;
    if (childAdd == ADD_CHILD_NONE) {
      return result;
    }
    // 兒童加成率
    if (childAdd == ADD_CHILD_6M) {
      if (isMDC15) {
        return result + (Float) parameters.getParameterValueBetween("ADD_CHILD_15_6M", date);
      }
      if (isM) {
        return result + (Float) parameters.getParameterValueBetween("ADD_CHILD_N15M_6M", date);
      }
      return result + (Float) parameters.getParameterValueBetween("ADD_CHILD_N15P_6M", date);
    } else if (childAdd == ADD_CHILD_2Y) {
      if (isMDC15) {
        return result + (Float) parameters.getParameterValueBetween("ADD_CHILD_15_2Y", date);
      }
      if (isM) {
        return result + (Float) parameters.getParameterValueBetween("ADD_CHILD_N15M_2Y", date);
      }
      return result + (Float) parameters.getParameterValueBetween("ADD_CHILD_N15P_2Y", date);
    } else if (childAdd == ADD_CHILD_6Y) {
      if (isMDC15) {
        return result + (Float) parameters.getParameterValueBetween("ADD_CHILD_15_6Y", date);
      }
      if (isM) {
        return result + (Float) parameters.getParameterValueBetween("ADD_CHILD_N15M_6Y", date);
      }
      return result + (Float) parameters.getParameterValueBetween("ADD_CHILD_N15P_6Y", date);
    }
    return result;
  }

  public double getFixedWithoutRW(Date date, boolean isM, boolean isMDC15, int addChild) {
    System.out.println("SPR=" + (Integer) parameters.getParameterValueBetween("SPR", date) + "," + getHospAdd(date, isM, isMDC15, addChild));
    return ((double)((Integer) parameters.getParameterValueBetween("SPR", date))
        * (double) getHospAdd(date, isM, isMDC15, addChild));
  }

  /**
   * 取得DRG code在當時的定額給付金額
   * 
   * @param drg
   * @return
   */
  public int getDRGFixed(String drg, String applYM, int addChild) {
    Date date = DateTool.getDateByApplYM(applYM);
    List<DRG_CODE> drgList =
        drgDao.findByCodeAndStartDayLessThanEqualAndEndDayGreaterThanEqual(drg, date, date);
    if (drgList == null || drgList.size() == 0) {
      return -1;
    }
    DRG_CODE drgCode = drgList.get(0);
    double value = getFixedWithoutRW(date, "M".equals(drgCode.getDep()),
        "15".equals(drgCode.getMdc()), addChild);
    return (int) Math.round((double)drgCode.getRw() * value);
  }

  /**
   * 取得DRG code資料，並將定額給付金額放在 serial欄位，DRG section放在 dep 欄位
   * 
   * @param drg
   * @return
   */
  public DrgCalculate getDRGSection(String drg, String applYM, int medDot, int childAdd) {
    Date date = DateTool.getDateByApplYM(applYM);
    List<DRG_CODE> drgList =
        drgDao.findByCodeAndStartDayLessThanEqualAndEndDayGreaterThanEqual(drg, date, date);
    if (drgList == null || drgList.size() == 0) {
      return null;
    }
    DRG_CODE drgCode = drgList.get(0);
    DrgCalculate result = new DrgCalculate(drgCode);
    double value = 0;
    for (String string : DRG_NO_ADD) {
      if (string.equals(drg)) {
        result.setDrgNoAdd(true);
        break;
      }
    }
    if (result.isDrgNoAdd()) {
      // 不得加計各項加成或其他另行加計之醫療點數，直接帶標準給付額
      value = (Integer) parameters.getParameterValueBetween("SPR", date);
    } else {
      value = getFixedWithoutRW(date, "M".equals(drgCode.getDep()), "15".equals(drgCode.getMdc()),
          childAdd);
    }

    result.setFixed((int)(Math.round(drgCode.getRw() * value)));
    if (drgCode.getStarted().intValue() == 1) {
      // 有導入實施的 DRG
      if (result.isDrgNoAdd()) {
        if (medDot > result.getFixed()) {
          result.setSection("B2");
        } else {
          result.setSection("B1");
        }
      } else if (medDot < drgCode.getLlimit().intValue()) {
        result.setSection("A");
      } else if (medDot < drgCode.getSerial().intValue()) {
        result.setSection("B1");
      } else if (medDot < drgCode.getUlimit().intValue()) {
        result.setSection("B2");
      } else {
        result.setSection("C");
      }
    } else {
      result.setSection("");
    }
    return result;
  }

  /**
   * 算出該DRG的申請費用點數
   * 
   * @param drg
   * @param medDot
   * @param partDot
   * @param nonApplDot
   * @param mrId
   * @return
   */
  public int getApplDot(DrgCalculate drg, int medDot, int partDot, int nonApplDot, long mrId,
      int bedDay, String tranCode, boolean isCase20) {
    if (drg.isDrgNoAdd()) {
      return drg.getFixed() - partDot + nonApplDot;
    }
    List<Object[]> values = ippDao.findVirtualCodeByMrId(mrId);
    int h = 0;
    int j = 0;
    int g = 0;
    int x = 0;
    if (values != null && values.size() > 0) {
      for (Object[] obj : values) {
        String orderCode = (String) obj[0];
        if (obj[1] == null && obj[2] != null) {
          int value = (Integer) obj[2];
          x += value;
        } else {
          int value = (int) Math.round((double) obj[1]);
          if (orderCode.startsWith("J")) {
            j += value;
          } else if (orderCode.startsWith("G")) {
            g += value;
          } else if (orderCode.startsWith("H")) {
            h += value;
          }
        }
      }
    }
    int realMedDot = medDot - x;
    // 計算超出上限之邊際成本
    int margin = 0;
    if (realMedDot > drg.getUlimit() && drg.getUlimit() > 0) {
      margin = (int) Math.round((realMedDot - drg.getUlimit()) * 0.8);
    }
    // 論日支付
    boolean payByDay = false;
    if (("5".equals(tranCode) || "6".equals(tranCode)) && bedDay < drg.getAvgInDay()) {
      payByDay = true;
    }

    if (realMedDot < drg.getLlimit()) {
      System.out.println("drg:" + drg.getCode() + ",realMedDot　＜=" + realMedDot + ",margin="
          + margin + ",x=" + x + ",j=" + j + ",g=" + g + ",h=" + h + ",partDot=" + partDot
          + ",nonApplDot=" + nonApplDot);
      return realMedDot + x + h - g - partDot + nonApplDot;
    } else {
      System.out.println("drg:" + drg.getCode() + ",realMedDot　>=" + realMedDot + ",margin="
          + margin + ",x=" + x + ",j=" + j + ",g=" + g + ",h=" + h + ",partDot=" + partDot
          + ",nonApplDot=" + nonApplDot);
      if (payByDay) {
        return (int) Math.round(((double) drg.getFixed() / (double) drg.getAvgInDay()) * bedDay) - g
            - partDot + nonApplDot;
      } else if (drg.getRw() == 0 || isCase20) {
        return realMedDot + x + h - partDot + nonApplDot;
      } else {
        return drg.getFixed() + margin + x + h - j - g - partDot + nonApplDot;
      }
    }
  }

  /**
   * 取得是否有兒童加成
   * 
   * @param nbBirthday 新生兒生日
   * @param birthday 病患生日
   * @param inDay 住院日
   * @return
   */
  public int getAddChild(String nbBirthday, String birthday, String inDay) {
    if (nbBirthday != null && nbBirthday.length() > 0) {
      return ADD_CHILD_6M;
    }
    Date birth = DateTool.convertChineseToYear(birthday);
    Date in = DateTool.convertChineseToYear(inDay);
    long time = in.getTime() - birth.getTime();
    long day = time / (24 * 60 * 60 * 1000);
    // System.out.println("birth=" + birth + ",in=" + in + " day=" + day);
    if (day < 158) {
      // 因有案件 159 天仍算為 2歲內，所以由 6個月 180 天改為 158 天
      return ADD_CHILD_6M;
    }
    if (day < 730) {
      return ADD_CHILD_2Y;
    }
    if (day < 2190) {
      return ADD_CHILD_6Y;
    }
    return ADD_CHILD_NONE;
  }

  /**
   * 計算當月DRG個案數不超過20件的總數
   * 
   * @param applYM
   * @return
   */
  public HashMap<String, List<Long>> countCase20(String applYM) {
    HashMap<String, List<Long>> result = new HashMap<String,  List<Long>>();
    Date date = DateTool.getDateByApplYM(applYM);
    java.sql.Date sqlDate = new java.sql.Date(date.getTime());
    List<Object[]> list = ipdDao.getDRGCase20Id(sqlDate, sqlDate, applYM);
    if (list != null && list.size() > 0) {
      for (Object[] obj : list) {
        String drg = (String) obj[0];
        List<Long> ids = result.get(drg);
        if (ids == null) {
          ids = new ArrayList<Long>();
          result.put(drg, ids);
        }
        ids.add(((BigInteger) obj[1]).longValue());
      }
    }
    return result;
  }
}
