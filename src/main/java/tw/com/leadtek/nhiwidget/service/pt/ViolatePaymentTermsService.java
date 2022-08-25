/**
 * Created on 2022/6/27.
 */
package tw.com.leadtek.nhiwidget.service.pt;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tw.com.leadtek.nhiwidget.constant.INTELLIGENT_REASON;
import tw.com.leadtek.nhiwidget.constant.MR_STATUS;
import tw.com.leadtek.nhiwidget.constant.XMLConstant;
import tw.com.leadtek.nhiwidget.dao.IP_DDao;
import tw.com.leadtek.nhiwidget.dao.IP_PDao;
import tw.com.leadtek.nhiwidget.dao.MRDao;
import tw.com.leadtek.nhiwidget.dao.OP_DDao;
import tw.com.leadtek.nhiwidget.dao.OP_PDao;
import tw.com.leadtek.nhiwidget.dao.PT_PAYMENT_TERMSDao;
import tw.com.leadtek.nhiwidget.dto.PaymentTermsPl;
import tw.com.leadtek.nhiwidget.dto.PtAdjustmentFeePl;
import tw.com.leadtek.nhiwidget.dto.PtAnesthesiaFeePl;
import tw.com.leadtek.nhiwidget.dto.PtBoneMarrowTransFeePl;
import tw.com.leadtek.nhiwidget.dto.PtInjectionFeePl;
import tw.com.leadtek.nhiwidget.dto.PtInpatientFeePl;
import tw.com.leadtek.nhiwidget.dto.PtMedicineFeePl;
import tw.com.leadtek.nhiwidget.dto.PtNutritionalFeePl;
import tw.com.leadtek.nhiwidget.dto.PtOthersFeePl;
import tw.com.leadtek.nhiwidget.dto.PtOutpatientFeePl;
import tw.com.leadtek.nhiwidget.dto.PtPsychiatricFeePl;
import tw.com.leadtek.nhiwidget.dto.PtPsychiatricWardFeePl;
import tw.com.leadtek.nhiwidget.dto.PtQualityServicePl;
import tw.com.leadtek.nhiwidget.dto.PtRadiationFeePl;
import tw.com.leadtek.nhiwidget.dto.PtRehabilitationFeePl;
import tw.com.leadtek.nhiwidget.dto.PtSpecificMedicalFeePl;
import tw.com.leadtek.nhiwidget.dto.PtSurgeryFeePl;
import tw.com.leadtek.nhiwidget.dto.PtTreatmentFeePl;
import tw.com.leadtek.nhiwidget.dto.PtWardFeePl;
import tw.com.leadtek.nhiwidget.dto.ptNhiNoTimes;
import tw.com.leadtek.nhiwidget.model.rdb.INTELLIGENT;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.model.rdb.PARAMETERS;
import tw.com.leadtek.nhiwidget.service.CodeTableService;
import tw.com.leadtek.nhiwidget.service.IntelligentService;
import tw.com.leadtek.nhiwidget.service.ParametersService;
import tw.com.leadtek.tools.DateTool;

/**
 * 計算違反支付準則服務
 * 
 * @author kenlai
 *
 */
@Service
public class ViolatePaymentTermsService {

  /**
   * 急診觀察床–病房費
   */
  private final static String[] EMERGENCY_WARD =
      new String[] {"03073A", "03074B", "03018A", "03019B",};

  @Autowired
  private MRDao mrDao;

  @Autowired
  private OP_DDao opdDao;

  @Autowired
  private OP_PDao oppDao;

  @Autowired
  private IP_DDao ipdDao;

  @Autowired
  private IP_PDao ippDao;

  @Autowired
  private PT_PAYMENT_TERMSDao ptDao;

  @Autowired
  private IntelligentService is;

  @Autowired
  private ParametersService ps;

  @Autowired
  private CodeTableService cts;

  private static HashMap<String, String> wordings;

  /**
   * 牙醫案件分類代碼
   */
  private static List<String> caseTypeDentist;

  /**
   * 中醫案件分類代碼
   */
  private static List<String> caseTypeCM;

  public void updateWordings(boolean forceUpdate) {
    if (wordings == null || forceUpdate) {
      wordings = new HashMap<String, String>();
      List<PARAMETERS> parameters = ps.getByCat("INTELLIGENT");
      for (PARAMETERS p : parameters) {
        if (p.getName().startsWith("VIOLATE")) {
          wordings.put(p.getName(), p.getValue());
        }
      }
      caseTypeDentist = new ArrayList<String>();
      caseTypeDentist.add("11");
      caseTypeDentist.add("12");
      caseTypeDentist.add("13");
      caseTypeDentist.add("14");
      caseTypeDentist.add("16");
      caseTypeDentist.add("17");
      caseTypeDentist.add("19");

      caseTypeCM = new ArrayList<String>();
      caseTypeDentist.add("21");
      caseTypeDentist.add("22");
      caseTypeDentist.add("23");
      caseTypeDentist.add("24");
      caseTypeDentist.add("25");
      caseTypeDentist.add("28");
      caseTypeDentist.add("29");
      caseTypeDentist.add("30");
      caseTypeDentist.add("31");
    }
  }

  /**
   * 取得整個DB有使用該醫令的病歷數
   * 
   * @param code
   * @return
   */
  public int getCountOfUseCodeMR(String code) {
    List<Long> countList = mrDao.getCountByCodeLike("%," + code + ",%");
    if (countList != null && countList.size() > 0) {
      return countList.get(0).intValue();
    }
    return 0;
  }

  /**
   * 檢查支付代碼適用的病歷型態(data_format)
   * 
   * @param pt
   * @param mrList 有使用 pt.NHI_NO 的病歷 list
   * @param batch
   */
  private void checkDataFormat(PaymentTermsPl pt, List<MR> mrList, List<INTELLIGENT> batch) {
    if (pt.getOutpatient_type() == 1 && pt.getHospitalized_type() == 1) {
      // 門急診及住院皆適用
      return;
    }
    if (pt.getOutpatient_type() == 0) {
      String wording = String.format(wordings.get("VIOLATE_DATA_FORMAT"), pt.getNhi_no(), "門診");
      for (MR mr : mrList) {
        if (XMLConstant.DATA_FORMAT_OP.equals(mr.getDataFormat())) {
          if ((pt instanceof PtInpatientFeePl) && mr.getFuncType() == "22") {
            // 急診會用到住院診察費
          } else {
            is.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), pt.getNhi_no(), wording,
                true, batch);
          }
        }
      }
    }
    if (pt.getHospitalized_type() == 0) {
      String wording = String.format(wordings.get("VIOLATE_DATA_FORMAT"), pt.getNhi_no(), "住院");
      for (MR mr : mrList) {
        if (XMLConstant.DATA_FORMAT_IP.equals(mr.getDataFormat())) {
          is.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), pt.getNhi_no(), wording,
              true, batch);
        }
      }
    }
  }

  /**
   * 檢查支付代碼適用的病歷型態(data_format)
   * 
   * @param pt
   * @param mrList 有使用 pt.NHI_NO 的病歷 list
   * @param batch
   */
  private void checkDentistAndCM(PtOutpatientFeePl pt, List<MR> mrList, List<Long> mrIdList,
      List<INTELLIGENT> batch) {
    // 不含牙科
    if (pt.getNo_dentisit() == 1) {
      String wording = String.format(wordings.get("VIOLATE_DENTIST_CM"), pt.getNhi_no(), "牙科");
      List<Long> violateMrId = opdDao.getMrIdByCaseTypeAndByMrId(mrIdList, caseTypeDentist);
      insertIntelligent(mrList, violateMrId, batch, pt.getNhi_no(), wording);
    }
    // 不含中醫
    if (pt.getNo_chi_medicine() == 1) {
      String wording = String.format(wordings.get("VIOLATE_DENTIST_CM"), pt.getNhi_no(), "中醫");
      List<Long> violateMrId = opdDao.getMrIdByCaseTypeAndByMrId(mrIdList, caseTypeCM);
      insertIntelligent(mrList, violateMrId, batch, pt.getNhi_no(), wording);
    }
  }

  /**
   * 檢查該病歷是否有調劑費，只有門診診察費用到
   * 
   * @param pt
   * @param mrList
   * @param mrIdList
   * @param batch
   */
  private void checkDispensing(PtOutpatientFeePl pt, List<MR> mrList, List<Long> mrIdList,
      List<INTELLIGENT> batch) {
    if (pt.getNo_service_charge() != 1) {
      return;
    }
    String wording = String.format(wordings.get("VIOLATE_ORDER_TYPE_9"), pt.getNhi_no());
    // 9 為藥事服務費(調劑費)
    List<Long> violateMrId = oppDao.getMrIdByOrderTypeAndMrId("9", mrIdList);
    insertIntelligent(mrList, violateMrId, batch, pt.getNhi_no(), wording);
  }

  /**
   * 限定山地離島區域申報使用，只有門診診察費用到
   * 
   * @param pt
   * @param mrList
   * @param mrIdList
   * @param batch
   */
  private void checkOutIsland(PtOutpatientFeePl pt, List<MR> mrList, List<Long> mrIdList,
      List<INTELLIGENT> batch) {
    if (pt.getLim_out_islands() != 1) {
      return;
    }
    String wording = String.format(wordings.get("VIOLATE_OUTLAND"), pt.getNhi_no());
    // 007 為離島免除部分負擔
    List<Long> violateMrId = opdDao.getMrIdByPartNoAndByMrId(mrIdList, "007");
    insertIntelligent(mrList, violateMrId, batch, pt.getNhi_no(), wording);
  }

  /**
   * 限定假日加計使用，只有門診診察費用到
   * 
   * @param pt
   * @param mrList
   * @param mrIdList
   * @param batch
   */
  private void checkHoliday(PtOutpatientFeePl pt, List<MR> mrList, List<Long> mrIdList,
      List<INTELLIGENT> batch) {
    if (pt.getLim_holiday() != 1) {
      return;
    }
    String wording = String.format(wordings.get("VIOLATE_HOLIDAY"), pt.getNhi_no());
    List<MR> violateMr = new ArrayList<MR>();
    Calendar cal = Calendar.getInstance();
    for (MR mr : mrList) {
      boolean violate = true;
      // 就診日期或就醫結束日期是假日即可
      cal.setTime(mr.getMrEndDate());
      if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
          || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
        violate = false;
      }
      cal.setTime(mr.getMrDate());
      if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
          || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
        violate = false;
      }
      if (violate) {
        violateMr.add(mr);
      }
    }
    insertIntelligent(violateMr, null, batch, pt.getNhi_no(), wording);
  }

  /**
   * 不可與任一支付標準代碼並存單一就醫紀錄
   * 
   * @param isEnable
   * @param nhiNo
   * @param excludeNhiNoList
   * @param mrList
   * @param mrIdList
   * @param batch
   */
  private void checkExcludeOrderCode(boolean isEnable, String nhiNo, List<String> excludeNhiNoList,
      List<MR> mrList, List<Long> mrIdList, List<INTELLIGENT> batch, boolean isNotify) {
    if (!isEnable) {
      return;
    }
    String excludeOrderCode = stringListToString(excludeNhiNoList);
    // System.out.println("checkExcludeOrderCode isEnable=" + isEnable + ", orderCode=" + nhiNo +
    // " exclude:" + excludeNhiNoList.get(0) + ", total=" + excludeOrderCode);
    String wording = isNotify
        ? String.format(wordings.get("VIOLATE_EXCLUDE_ORDER_CODE_NOTIFY"), nhiNo, excludeOrderCode)
        : String.format(wordings.get("VIOLATE_EXCLUDE_ORDER_CODE"), nhiNo, excludeOrderCode);
    List<Long> violateMrIdOp = new ArrayList<Long>();
    List<Long> violateMrIdIp = new ArrayList<Long>();
    for (MR mr : mrList) {
      for (String excludeNhiNo : excludeNhiNoList) {
        String code = "," + excludeNhiNo + ",";
        if (mr.getCodeAll() == null) {
          continue;
        }
        if (mr.getCodeAll().indexOf(code) > -1) {
          if (mr.getDataFormat().equals(XMLConstant.DATA_FORMAT_OP)) {
            violateMrIdOp.add(mr.getId());
          } else {
            violateMrIdIp.add(mr.getId());
          }
          break;
        }
      }
    }
    List<String> excludeNhiNoListIncludeInhNo = new ArrayList<>(excludeNhiNoList);
    excludeNhiNoListIncludeInhNo.add(nhiNo);
    checkCoExistWithoutOrderType4(true, violateMrIdOp, nhiNo, excludeNhiNoListIncludeInhNo);
    checkCoExistWithoutOrderType4(false, violateMrIdIp, nhiNo, excludeNhiNoListIncludeInhNo);
    List<Long> violateMrId = new ArrayList<Long>();
    violateMrId.addAll(violateMrIdOp);
    violateMrId.addAll(violateMrIdIp);
    insertIntelligent(mrList, violateMrId, batch, nhiNo, wording);
  }

  private void checkCoExistWithoutOrderType4(boolean isOp, List<Long> mrList, String nhiNo, List<String> excludeNhiNoList) {
    List<Object[]> pp =
        isOp ? oppDao.getMrIdAndOrderCodeAndStartTimeByMrIdAndOrderCode(excludeNhiNoList, mrList)
            : ippDao.getMrIdAndOrderCodeAndStartTimeByMrIdAndOrderCode(excludeNhiNoList, mrList);
    // 存放因OrderType為4未找到的 mrId，避免被當做違反支付準則
    List<Long> notFoundMrIdList = new ArrayList<>(mrList);
    long mrId = 0;
    List<String> times = null;
    for (Object[] obj : pp) {
      String startTime = ((String) obj[2] == null) ? "" : ((String) obj[2]).substring(0, 7);
      if (mrId != ((BigInteger) obj[0]).longValue()) {
        if (times != null) {
          if (!checkTimesOverlap(times)) {
            mrList.remove(Long.valueOf(mrId));
          }
        }
        times = new ArrayList<String>();
        mrId = ((BigInteger) obj[0]).longValue();
        notFoundMrIdList.remove(Long.valueOf(mrId));
      }
      if (nhiNo.equals((String) obj[1])) {
        times.add(0, startTime);
      } else {
        times.add(startTime);
      }
    }
    if (!checkTimesOverlap(times)) {
      mrList.remove(Long.valueOf(mrId));
    }
    for (Long notFoundMrId : notFoundMrIdList) {
      mrList.remove(notFoundMrId);
    }
  }
  
  /**
   * 同一天的不可並存醫令才成立違反支付準則
   * @param times
   * @return
   */
  private boolean checkTimesOverlap(List<String> times) {
    if (times == null) {
      return false;
    }
    String firstTime = times.get(0);
    for (int i=1; i<times.size(); i++) {
      if (times.get(i).equals(firstTime)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 需與以下任一支付標準代碼並存，方可進行申報
   * 
   * @param isEnable
   * @param nhiNo
   * @param includeNhiNoList
   * @param mrList
   * @param mrIdList
   * @param batch
   */
  private void checkIncludeOrderCode(boolean isEnable, String nhiNo, List<String> includeNhiNoList,
      List<MR> mrList, List<Long> mrIdList, List<INTELLIGENT> batch) {
    if (!isEnable) {
      return;
    }
    String includeOrderCode = stringListToString(includeNhiNoList);
    // System.out.println("checkIncludeOrderCode " + nhiNo + " check order code=" + includeOrderCode
    // + ", mr size=" + mrList.size());
    String wording =
        String.format(wordings.get("VIOLATE_INCLUDE_ORDER_CODE"), nhiNo, includeOrderCode);
    List<MR> violateMr = new ArrayList<MR>();
    for (MR mr : mrList) {
      int count = 0;
      for (String includeNhiNo : includeNhiNoList) {
        String code = "," + includeNhiNo + ",";
        if (mr.getCodeAll().indexOf(code) > -1) {
          count++;
        }
      }
      if (count == 0) {
        violateMr.add(mr);
      }
    }
    insertIntelligent(violateMr, null, batch, nhiNo, wording);
  }

  /**
   * 限定同患者執行過 ? 支付標準代碼，>= ? 次，方可申報
   * 
   * @param isEnable
   * @param nhiNo
   * @param includeNhiNoList
   * @param mrList
   * @param mrIdList
   * @param batch
   */
  private void checkUserIncludeOrderCodeTimes(boolean isEnable, String nhiNo,
      List<String> includeNhiNoList, int minimum, List<MR> mrList, List<Long> mrIdList,
      List<INTELLIGENT> batch) {
    if (!isEnable) {
      return;
    }
    String includeOrderCode = stringListToString(includeNhiNoList);
    String wording = String.format(wordings.get("VIOLATE_INCLUDE_ORDER_CODE_TIMES"), nhiNo,
        includeOrderCode, String.valueOf(minimum));
    List<String> rocIds = getRocIdList(mrList);

    HashMap<String, Integer> rocIdCountMap = new HashMap<String, Integer>();
    for (String orderCode : includeNhiNoList) {
      List<Object[]> rocIdCount = mrDao.getRocIdByCodeTimes("%," + orderCode + ",%", rocIds);
      for (Object[] obj : rocIdCount) {
        Integer count = rocIdCountMap.get((String) obj[0]);
        if (count == null) {
          rocIdCountMap.put((String) obj[0], ((BigInteger) obj[1]).intValue());
        } else {
          rocIdCountMap.put((String) obj[0], ((BigInteger) obj[1]).intValue() + count.intValue());
        }
      }
    }

    List<MR> violateMr = new ArrayList<MR>();
    for (MR mr : mrList) {
      Integer count = rocIdCountMap.get(mr.getRocId());
      if (count == null) {
        violateMr.add(mr);
      } else if (count < minimum) {
        violateMr.add(mr);
      }
    }
    insertIntelligent(violateMr, null, batch, nhiNo, wording);
  }

  /**
   * 檢查年齡限制
   * 
   * @param isEnable 是否要檢查
   * @param isOP 是否為門診
   * @param nhiNo 支付代碼
   * @param ageType 判斷邏輯
   * @param age 步數
   * @param mrList 病歷清單
   * @param mrIdList 病歷id清單
   * @param batch 批次儲存INTELLIGENT table
   */
  private void checkAge(boolean isEnable, boolean isOP, String nhiNo, int ageType, int age,
      List<MR> mrList, List<Long> mrIdList, List<INTELLIGENT> batch) {
    if (!isEnable) {
      return;
    }
    StringBuffer sb = new StringBuffer();
    if (ageType == 1) {
      sb.append("未滿");
    } else if (ageType == 2) {
      sb.append("大於等於");
    } else if (ageType == 3) {
      sb.append("小於等於");
    }
    sb.append(age);
    String wording = String.format(wordings.get("VIOLATE_AGE"), nhiNo, sb.toString());

    List<Long> violateMrIdList = new ArrayList<Long>();
    List<Object[]> data =
        (isOP) ? opdDao.getMrIdBirthdayByMrId(mrIdList) : ipdDao.getMrIdBirthdayByMrId(mrIdList);
    if (data == null || data.size() == 0) {
      return;
    }

    for (Object[] obj : data) {
      String rocBirth = (String) obj[3];
      /// 如果新生日期不為空
      if (obj[4] != null && ((String) obj[4]).length() > 0) {
        rocBirth = (String) obj[4];
      }
      String funcDate =
          (obj[2] == null || ((String) obj[2]).length() == 0) ? (String) obj[1] : (String) obj[2];
      checkAge(((BigInteger) obj[0]).longValue(), ageType, age, rocBirth, funcDate,
          violateMrIdList);
    }
    insertIntelligent(mrList, violateMrIdList, batch, nhiNo, wording);
  }

  private void checkAge(long mrId, int ageType, int age, String rocBirth, String funcDate,
      List<Long> violateMrIdList) {
    int birthYear = Integer.parseInt(rocBirth.substring(0, 3));
    int birthMonthDate = Integer.parseInt(rocBirth.substring(3, 7));

    int funcEndYear = Integer.parseInt(funcDate.substring(0, 3));
    int funcEndMonthDate = Integer.parseInt(funcDate.substring(3, 7));

    int diffY = funcEndYear - birthYear;
    if (funcEndMonthDate < birthMonthDate) {
      // 未過生日
      diffY--;
    }

    switch (ageType) {
      case 1: // 需未滿 age歲
        if (age <= diffY) {
          violateMrIdList.add(mrId);
        }
        break;
      case 2: // 需>= age歲
        if (age > diffY) {
          violateMrIdList.add(mrId);
        }
        break;
      case 3: // 需<= age歲
        if (age < diffY) {
          violateMrIdList.add(mrId);
        }
        break;
    }
  }

  /**
   * 檢查是否符合限定科別
   * 
   * @param isEnable
   * @param nhiNo
   * @param funcTypeList
   * @param mrList
   * @param mrIdList
   * @param batch
   */
  private void checkFuncType(boolean isEnable, String nhiNo, List<String> funcTypeList,
      List<MR> mrList, List<Long> mrIdList, List<INTELLIGENT> batch) {
    if (!isEnable) {
      return;
    }
    String funcTypeString = appendStringListToString(funcTypeList);
    String[] funcTypeCodeList = new String[funcTypeList.size()];
    for (int i = 0; i < funcTypeList.size(); i++) {
      funcTypeCodeList[i] = cts.getFuncTypeCodeByName(funcTypeList.get(i));
    }
    String wording = String.format(wordings.get("VIOLATE_FUNC_TYPE"), nhiNo, funcTypeString);
    List<MR> violateMr = new ArrayList<MR>();
    for (MR mr : mrList) {
      int count = 0;
      for (String funcType : funcTypeCodeList) {
        if (mr.getFuncType().equals(funcType)) {
          count++;
          break;
        }
      }
      if (count == 0) {
        violateMr.add(mr);
      }
    }
    insertIntelligent(violateMr, null, batch, nhiNo, wording);
  }

  private void checkCountOfUniquePrsn(boolean isEnable, String nhiNo, int max, List<MR> mrList,
      List<Long> mrIdList, List<INTELLIGENT> batch) {
    if (!isEnable) {
      return;
    }
    List<String> prsnIds = new ArrayList<String>();
    List<Object[]> opData = opdDao.getPrsnIdCountByMrId(mrIdList);
    for (Object[] obj : opData) {
      int count =
          (obj[1] instanceof Long) ? ((Long) obj[1]).intValue() : ((BigInteger) obj[1]).intValue();
      if (count > max) {
        prsnIds.add((String) obj[0]);
      }
    }

    for (String prsnId : prsnIds) {
      List<MR> violateMr = new ArrayList<MR>();
      for (MR mr : mrList) {
        if (mr.getPrsnId().equals(prsnId)) {
          violateMr.add(mr);
        }
      }
      String wording =
          String.format(wordings.get("VIOLATE_PRSN"), nhiNo, prsnId, String.valueOf(max));
      insertIntelligent(violateMr, null, batch, nhiNo, wording);
    }
    // @Query(value = "SELECT PRSN_ID, COUNT(PRSN_ID) FROM OP_D WHERE MR_ID IN ?1 GROUP BY PRSN_ID",
    // nativeQuery = true)
    // public List<Object[]> getPrsnIdCountByMrId(List<Long> mrId);
  }

  /**
   * 檢查單一住院就醫紀錄應用數量,限定<=次數
   * 
   * @param isEnable
   * @param nhiNo
   * @param funcTypeList
   * @param mrList
   * @param mrIdList
   * @param batch
   */
  private void checkIPUseTimes(boolean isEnable, String nhiNo, int max, List<MR> mrList,
      List<Long> mrIdList, List<INTELLIGENT> batch) {
    if (!isEnable) {
      return;
    }
    String wording = String.format(wordings.get("VIOLATE_TIMES"), nhiNo, "住院", String.valueOf(max));
    List<Object[]> ids = ippDao.getMrIdByOrderCodeCount(nhiNo, mrIdList, max);
    List<Long> violateMrIdList = new ArrayList<Long>();
    for (Object[] obj : ids) {
      violateMrIdList.add(((BigInteger) obj[0]).longValue());
    }
    insertIntelligent(mrList, violateMrIdList, batch, nhiNo, wording);
  }

  /**
   * 檢查單一急診就醫紀錄應用數量,限定<=次數，目前只出現在 02005B
   */
  private void checkOrderCodeUseTimesWithEM(boolean isEnable, String nhiNo, int max,
      List<MR> mrList, List<Long> mrIdList, List<INTELLIGENT> batch) {
    if (!isEnable) {
      return;
    }
    String wording = String.format(wordings.get("VIOLATE_TIMES"), nhiNo, "急診", String.valueOf(max));

    // 有用到急診觀察病房費的病歷
    // List<Long> useEmWardMrIdList = new ArrayList<Long>();
    // for (MR mr : mrList) {
    // for (String ward : EMERGENCY_WARD) {
    // String code = "," + ward + ",";
    // if (mr.getCodeAll().indexOf(code) > -1) {
    // useEmWardMrIdList.add(mr.getId());
    // break;
    // }
    // }
    // }
    List<Long> useEmWardMrIdList = mrIdList;
    List<Object[]> ids = ippDao.getMrIdByOrderCodeCount(nhiNo, useEmWardMrIdList, max);
    List<Long> violateMrIdList = new ArrayList<Long>();
    for (Object[] obj : ids) {
      violateMrIdList.add(((BigInteger) obj[0]).longValue());
    }

    ids = oppDao.getMrIdByOrderCodeCount(nhiNo, useEmWardMrIdList, max);
    for (Object[] obj : ids) {
      violateMrIdList.add(((BigInteger) obj[0]).longValue());
    }

    insertIntelligent(mrList, violateMrIdList, batch, nhiNo, wording);
  }

  /**
   * 檢查單一就醫紀錄應用數量,限定<=次數
   * 
   * @param isEnable
   * @param nhiNo
   * @param funcTypeList
   * @param mrList
   * @param mrIdList
   * @param batch
   */
  private void checkAllUseTimes(boolean isEnable, String nhiNo, int max, List<MR> mrList,
      List<Long> mrIdList, List<INTELLIGENT> batch) {
    if (!isEnable) {
      return;
    }
    String wording = String.format(wordings.get("VIOLATE_TIMES"), nhiNo, "", String.valueOf(max));
    List<Object[]> ids = ippDao.getMrIdByOrderCodeCount(nhiNo, mrIdList, max);
    List<Long> violateMrIdList = new ArrayList<Long>();
    for (Object[] obj : ids) {
      violateMrIdList.add(((BigInteger) obj[0]).longValue());
    }
    ids = oppDao.getMrIdByOrderCodeCount(nhiNo, mrIdList, max);
    for (Object[] obj : ids) {
      violateMrIdList.add(((BigInteger) obj[0]).longValue());
    }
    insertIntelligent(mrList, violateMrIdList, batch, nhiNo, wording);
  }

  /**
   * 每組病歷號碼，每院限申報次數
   * 
   * @param isEnable
   * @param nhiNo
   * @param max
   * @param mrList
   * @param batch
   */
  private void checkUseTimesInAllMr(boolean isEnable, String nhiNo, int max, List<MR> mrList,
      List<INTELLIGENT> batch) {
    if (!isEnable) {
      return;
    }
    String wording = String.format(wordings.get("VIOLATE_ROCID_TIMES"), nhiNo, String.valueOf(max));

    List<String> rocIdList = mrDao.getRocIdByCodeTimes("%," + nhiNo + ",%", max);
    List<MR> violateMr = new ArrayList<MR>();
    for (MR mr : mrList) {
      for (String rocId : rocIdList) {
        if (mr.getRocId().equals(rocId)) {
          violateMr.add(mr);
          break;
        }
      }
    }
    insertIntelligent(violateMr, null, batch, nhiNo, wording);
  }

  /**
   * 檢查住院診察費病歷內是否有門診診察費
   */
  private void checkIPWithOP(boolean isEnable, String nhiNo, List<MR> mrList, List<Long> mrIdList,
      List<INTELLIGENT> batch) {
    if (!isEnable) {
      return;
    }
    String wording = String.format(wordings.get("VIOLATE_IP_WITH_OP"), nhiNo);

    List<Object[]> ids = ippDao.getMrIdByOrderPayTypeAndOrderCode(mrIdList);
    List<Long> violateMrIdList = new ArrayList<Long>();
    for (Object[] obj : ids) {
      violateMrIdList.add(((BigInteger) obj[0]).longValue());
    }
    insertIntelligent(mrList, violateMrIdList, batch, nhiNo, wording);
  }

  /**
   * 入住時間滿 小時，方可申報此支付標準代碼
   * 
   * @param isEnable
   * @param nhiNo
   * @param limitHour
   * @param mrList
   * @param mrIdList
   * @param batch
   */
  private void checkIpHours(boolean isEnable, String nhiNo, int limitType, int limitHour,
      List<MR> mrList, List<Long> mrIdList, List<INTELLIGENT> batch) {
    if (!isEnable) {
      return;
    }
    String wording = null;
    if (limitType == 2) {
      wording = String.format(wordings.get("VIOLATE_IP_HOURS_GREATERTHAN"), nhiNo,
          String.valueOf(limitHour));
    } else if (limitType == 3) {
      wording = String.format(wordings.get("VIOLATE_IP_HOURS_LESSTHAN"), nhiNo,
          String.valueOf(limitHour));
    }

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
    List<Object[]> ippData =
        ippDao.getMrIdAndStartTimeAndEndTimeByOrderCodeAndMrIdList(nhiNo, mrIdList);
    List<Long> violateMrIdList = new ArrayList<Long>();
    checkStartTimeAndEndTime(limitType, limitHour, violateMrIdList, ippData, sdf);

    List<Object[]> oppData =
        oppDao.getMrIdAndStartTimeAndEndTimeByOrderCodeAndMrIdList(nhiNo, mrIdList);
    checkStartTimeAndEndTime(limitType, limitHour, violateMrIdList, oppData, sdf);
    insertIntelligent(mrList, violateMrIdList, batch, nhiNo, wording);
  }

  private void checkStartTimeAndEndTime(int limitType, int limitHour, List<Long> violateMrIdList,
      List<Object[]> ippData, SimpleDateFormat sdf) {
    for (Object[] obj : ippData) {
      Date sDate = DateTool.convertChineseToYears((String) obj[1], sdf);
      Date eDate = DateTool.convertChineseToYears((String) obj[2], sdf);
      if (sDate == null || eDate == null) {
        continue;
      }
      int diffHour = hoursBetween(sDate, eDate);
      if (limitType == 2) {
        // 未滿不可申報
        if (diffHour < limitHour) {
          violateMrIdList.add(((BigInteger) obj[0]).longValue());
        }
      } else if (limitType == 3) {
        // 超過不可申報
        if (diffHour > limitHour) {
          violateMrIdList.add(((BigInteger) obj[0]).longValue());
        }
      }
    }
  }

  /**
   * 檢查是否包含以下任一ICD診斷碼
   * 
   * @param isEnable
   * @param nhiNo
   * @param funcTypeList
   * @param mrList
   * @param mrIdList
   * @param batch
   */
  private void checkICD(boolean isEnable, String nhiNo, List<String> icdList, List<MR> mrList,
      List<INTELLIGENT> batch) {
    if (!isEnable) {
      return;
    }
    String icdString = appendStringListToString(icdList);
    String wording = String.format(wordings.get("VIOLATE_INCLUDE_ICD"), nhiNo, icdString);
    List<MR> violateMr = new ArrayList<MR>();
    for (MR mr : mrList) {
      int count = 0;
      for (String icd : icdList) {
        if (mr.getIcdAll().indexOf("," + icd + ",") > -1) {
          count++;
          break;
        }
      }
      if (count == 0) {
        violateMr.add(mr);
      }
    }
    insertIntelligent(violateMr, null, batch, nhiNo, wording);
  }

  /**
   * 檢查是否包含任一DRG代碼
   * 
   * @param isEnable
   * @param nhiNo
   * @param funcTypeList
   * @param mrList
   * @param mrIdList
   * @param batch
   */
  private void checkDRG(boolean isEnable, String nhiNo, List<String> drgList, List<MR> mrList,
      List<Long> mrIdList, List<INTELLIGENT> batch) {
    if (!isEnable) {
      return;
    }
    String drgString = appendStringListToString(drgList);
    String wording = String.format(wordings.get("VIOLATE_INCLUDE_DRG"), nhiNo, drgString);
    List<MR> violateMr = new ArrayList<MR>();
    for (MR mr : mrList) {
      int count = 0;
      for (String drg : drgList) {
        if (mr.getDrgCode().equals(drg)) {
          count++;
          break;
        }
      }
      if (count == 0) {
        violateMr.add(mr);
      }
    }
    insertIntelligent(violateMr, null, batch, nhiNo, wording);
  }

  /**
   * 檢查支付準則限定每n日不可超過x次 byUser: true : 同患者限定每<= ? 日，總申報次數<= ? 次
   */
  private void checkUseTimesEveryDay(boolean isEnable, String nhiNo, int days, int max,
      List<MR> mrList, List<Long> mrIdList, List<INTELLIGENT> batch) {
    if (!isEnable) {
      return;
    }

    String wording = null;

    if (days == 1) {
      wording = String.format(wordings.get("VIOLATE_DAILY_MAX"), nhiNo, "日", String.valueOf(max));
    } else {
      wording =
          String.format(wordings.get("VIOLATE_DAILY_MAX"), nhiNo, days + "日內", String.valueOf(max));
    }
    checkUseTimesEveryDayByDataFormat(true, nhiNo, days, max, mrList, mrIdList, batch, wording);
    checkUseTimesEveryDayByDataFormat(false, nhiNo, days, max, mrList, mrIdList, batch, wording);
  }

  private void checkUseTimesEveryDayByDataFormat(boolean isOp, String nhiNo, int days, int max,
      List<MR> mrList, List<Long> mrIdList, List<INTELLIGENT> batch, String wording) {
    // 1. 先檢查總數有沒有超過 max
    List<Object[]> ids = (isOp) ? oppDao.getMrIdByOrderCodeCount(nhiNo, mrIdList, max)
        : ippDao.getMrIdByOrderCodeCount(nhiNo, mrIdList, max);
    if (ids == null || ids.size() == 0) {
      return;
    }
    List<Long> violateMrIdList = new ArrayList<Long>();
    for (Object[] obj : ids) {
      violateMrIdList.add(((BigInteger) obj[0]).longValue());
    }

    // 2. 再檢查天數，將有超過的一個一個調出來看 end_time - start_time , 用量 / 天數，取得每天使用量
    List<Object[]> data =
        (isOp) ? oppDao.getMrIdAndStartTimeAndEndTimeByOrderCodeAndMrIdList(nhiNo, violateMrIdList)
            : ippDao.getMrIdAndStartTimeAndEndTimeByOrderCodeAndMrIdList(nhiNo, violateMrIdList);
    // System.out.println("checkUseTimesEveryDayByDataFormat violateMr size=" +
    // violateMrIdList.size() + ", order count=" + data.size());
    // 存在違反規則的 MR ID
    HashMap<Long, String> mrIdMap = new HashMap<Long, String>();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
    int daysSum = 0;
    int total = 0;
    long lastMrId = 0;
    String firstDay = null;
    for (Object[] obj : data) {
      long mrId = ((BigInteger) obj[0]).longValue();
      if (lastMrId != mrId) {
        daysSum = 0;
        total = 0;
        lastMrId = mrId;
        firstDay = null;
      }
      String startTime = (String) obj[1];
      String endTime = (String) obj[2];
      if (mrIdMap.containsKey(Long.valueOf(mrId))) {
        continue;
      }
      if (days == 1) {
        // 每日不可超過 max 次數
        if (startTime == null || endTime == null
            || startTime.substring(0, 7).equals(endTime.substring(0, 7))) {
          // 同一天
          if (((Double) obj[3]).intValue() > max) {
            mrIdMap.put(mrId, "");
          }
        } else {
          int diffDays = diffDays(startTime, endTime, sdf);
          int totalQ = ((Double) obj[3]).intValue();
          double avg = (double) totalQ / (double) diffDays;
          if (avg > max) {
            mrIdMap.put(mrId, "");
          }
        }
      } else {
        total += ((Double) obj[3]).intValue();
        if (startTime == null || endTime == null) {
          // 同一天
          daysSum++;
        } else if (startTime.substring(0, 7).equals(endTime.substring(0, 7))) {
          // 同一天
          if (firstDay == null) {
            firstDay = startTime;
          } else {
            daysSum += diffDays(firstDay, endTime, sdf);
          }
        } else {
          if (firstDay == null) {
            firstDay = startTime;
          }
          daysSum += diffDays(firstDay, endTime, sdf);
        }
        // 每3日內限定應用不可超過1次 -> days = 3, max = 1
        if (daysSum <= days && total > max) {
          mrIdMap.put(mrId, "");
        } else if (startTime != null && !startTime.equals(firstDay)) {
          firstDay = startTime;
        }
      }
    }
    insertIntelligent(mrIdMap, mrList, batch, nhiNo, wording);
  }

  private void checkUseTimesAfterDay(boolean isEnable, String nhiNo, int days, int max,
      List<MR> mrList, List<Long> mrIdList, List<INTELLIGENT> batch) {
    if (!isEnable) {
      return;
    }
    // 1. 先檢查總數有沒有超過 max
    List<Object[]> ids = ippDao.getMrIdByOrderCodeCount(nhiNo, mrIdList, max);
    if (ids == null || ids.size() == 0) {
      return;
    }
    String wording = String.format(wordings.get("VIOLATE_OVER_DAYS"), nhiNo, String.valueOf(days),
        String.valueOf(max));
    List<Long> violateMrIdList = new ArrayList<Long>();
    for (Object[] obj : ids) {
      violateMrIdList.add(((BigInteger) obj[0]).longValue());
    }

    // 2. 再檢查天數，將有超過的一個一個調出來看 end_time - 第一筆start_time
    List<Object[]> data =
        ippDao.getMrDateAndMrIdAndStartTimeAndEndTimeByOrderCodeAndMrIdList(nhiNo, violateMrIdList);
    // 存在違反規則的 MR ID
    HashMap<Long, String> mrIdMap = new HashMap<Long, String>();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
    int total = 0;
    long lastMrId = 0;
    Date firstDate = null;

    for (Object[] obj : data) {
      long mrId = ((BigInteger) obj[0]).longValue();
      if (mrIdMap.containsKey(Long.valueOf(mrId))) {
        continue;
      }
      if (obj[3] == null) {
        continue;
      }
      if (lastMrId != mrId) {
        total = 0;
        String startTime = (String) obj[2];
        firstDate = DateTool.convertChineseToYears(startTime, sdf);
        lastMrId = mrId;
        continue;
      }
      String startTime = (String) obj[2];
      Date startDate = DateTool.convertChineseToYears(startTime, sdf);
      String endTime = (String) obj[3];
      Date endDate = DateTool.convertChineseToYears(endTime, sdf);

      if ((daysBetween(firstDate, startDate) - 1) > days) {
        total += ((Double) obj[4]).intValue();
      } else if ((daysBetween(firstDate, startDate) - 1) <= days && (daysBetween(firstDate, endDate) - 1) > days) {
        int avgOneDay = ((Double) obj[4]).intValue() / daysBetween(startDate, endDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(Calendar.DAY_OF_YEAR, 1);
        do {
          if ((daysBetween(firstDate, cal.getTime()) - 1) > days) {
            total += avgOneDay;
          }
          cal.add(Calendar.DAY_OF_YEAR, 1);
        } while (cal.getTimeInMillis() <= endDate.getTime());
      }
      if (total > max) {
        mrIdMap.put(mrId, "");
      }
    }

    insertIntelligent(mrIdMap, mrList, batch, nhiNo, wording);
  }

  /**
   * 限定同患者累積申報此支付標準代碼一年度最多一次
   */
  private void checkUserUseTimesEveryYear(boolean isEnable, String nhiNo, List<MR> mrList,
      List<INTELLIGENT> batch) {
    if (!isEnable) {
      return;
    }

    List<String> violateRocId = new ArrayList<String>();
    List<String> rocIds = getRocIdList(mrList);
    Map<String, String> rocIdYear = new HashMap<String, String>();
    long start = System.currentTimeMillis();
    List<Object[]> data = mrDao.getRocIdAndUseCountAndApplYm("%," + nhiNo + ",%", rocIds);
    long usedTime = System.currentTimeMillis() - start;
    // System.out.println("checkUserUseTimesEveryYear rocId size=" + rocIds.size() + " data size=" +
    // data.size() + ", use" + usedTime);
    start = System.currentTimeMillis();
    for (Object[] obj : data) {
      if (violateRocId.contains((String) obj[0])) {
        continue;
      }
      String year = rocIdYear.get((String) obj[0]);
      if (year == null) {
        rocIdYear.put((String) obj[0], ((String) obj[2]).substring(0, 3));
      } else {
        if (year.equals(((String) obj[2]).substring(0, 3))) {
          // 同年
          violateRocId.add((String) obj[0]);
        } else {
          rocIdYear.put((String) obj[0], ((String) obj[2]).substring(0, 3));
        }
      }
    }
    String wording = String.format(wordings.get("VIOLATE_INCLUDE_ORDER_CODE_EVERY_YEAR"), nhiNo);
    // System.out.println(wording + " " + violateRocId.size());
    if (violateRocId.size() == 0) {
      return;
    }
    List<MR> violateMr = new ArrayList<MR>();
    for (MR mr : mrList) {
      for (String rocid : violateRocId) {
        if (mr.getRocId().equals(rocid)) {
          violateMr.add(mr);
        }
      }
    }
    usedTime = System.currentTimeMillis() - start;
    // System.out.println("before insertIntelligent " + usedTime);
    insertIntelligent(mrList, null, batch, nhiNo, wording);
  }

  /**
   * 同患者限定每<= ? 日，總申報次數<= ? 次
   */
  private void checkUserUseTimesInDays(int dataFormat, boolean isEnable, String nhiNo, int days, int max,
      List<MR> mrList, List<INTELLIGENT> batch) {
    if (!isEnable) {
      return;
    }

    List<String> rocIds = getRocIdList(mrList);
    Date minDate = getMinimumDate(mrList);
    List<Long> violateMrIdList = new ArrayList<Long>();
    // 存在違反規則的 ROC ID
    HashMap<String, String> rocIdMap = new HashMap<String, String>();
    if ((dataFormat & 1) > 0) {
      checkUserUseTimesInDays(rocIdMap, true, nhiNo, rocIds, minDate, days, max);
    }
    if ((dataFormat & 2) > 0) {
      checkUserUseTimesInDays(rocIdMap, false, nhiNo, rocIds, minDate, days, max);
    }
    if (rocIdMap.size() == 0) {
      return;
    }
    for (MR mr : mrList) {
      if (rocIdMap.containsKey(mr.getRocId())) {
        violateMrIdList.add(mr.getId());
      }
    }
    String wording = String.format(wordings.get("VIOLATE_USER_N_DAYS_MAX"), nhiNo,
        String.valueOf(days), String.valueOf(max));
    insertIntelligent(mrList, violateMrIdList, batch, nhiNo, wording);
  }
  
  /**
   * 檢查同一病患使用該支付代碼每 days 日內應用是否超過 max 次
   * @param rocIdMap 違反準則的身分證號 map
   * @param isOp true:門診，false:住院
   * @param nhiNo 醫令(支付代碼)
   * @param rocIds 要檢查的病歷中的所有證號
   * @param minDate 最早的病歷日期
   * @param days 限定幾天內
   * @param max 申報次數上限
   */
  private void checkUserUseTimesInDays(HashMap<String, String> rocIdMap, boolean isOp, String nhiNo,
      List<String> rocIds, Date minDate, int days, int max) {
    String rocId = "";
    List<Date> dates = null;
    List<Object[]> data = isOp ?  oppDao.getRocIdAndUseCountAndApplYm(nhiNo, rocIds) :
      ippDao.getRocIdAndUseCountAndApplYm(nhiNo, rocIds);
    for (Object[] obj : data) {
      if (rocIdMap.get((String) obj[0]) != null) {
        continue;
      }
      if (daysBetween((Date) obj[1], minDate) > days) {
        // 小於所撈取病歷最舊-days的病歷，不處理
        continue;
      }
      if (!rocId.equals((String) obj[0])) {
        rocId = (String) obj[0];
        dates = new ArrayList<Date>();
        dates.add((Date) obj[1]);
      } else {
        dates.add((Date) obj[1]);
        if ((daysBetween(dates.get(0), (Date) obj[1]) + 1) <= days) {
          if (dates.size() > max) {
            rocIdMap.put(rocId, "");
            // 2022/7/8 只將最後一筆加入違反支付準則，改為全部加入違反支付準則
            // violateMrIdList.add(((BigInteger) obj[3]).longValue());
            continue;
          }
        } else {
          // 和第一筆比超過max日
          do {
            dates.remove(0);
          } while ((daysBetween(dates.get(0), (Date) obj[1]) + 1) > days);
        }
      }
    }
  }

  /**
   * 限定同患者執行過 ? 支付標準代碼， <= ? 日，申報過此支付代碼
   */
  private void checkUserUseOtherOrderCodeInDays(boolean isEnable, String nhiNo, List<String> nhiNo2,
      int days, List<MR> mrList, List<INTELLIGENT> batch) {
    if (!isEnable) {
      return;
    }

    // 有使用 nhiNo 的身分證號
    HashMap<String, List<MR>> rocIdDate = new HashMap<String, List<MR>>();
    for (MR mr : mrList) {
      List<MR> list = rocIdDate.get(mr.getRocId());
      if (list == null) {
        list = new ArrayList<MR>();
        rocIdDate.put(mr.getRocId(), list);
      }
      list.add(mr);
    }
    List<String> rocIds = new ArrayList<String>(rocIdDate.keySet());
    // 有使用 nhiNo 及曾使用 nhiNo2 的身分證號與時間
    List<Object[]> data = mrDao.getRocIdAndUseCountAndApplYm("%," + nhiNo2.get(0) + ",%", rocIds);
    for (Object[] obj : data) {
      String rocId = (String) obj[0];
      Date nhiNo2Time = (Date) obj[1];
      List<MR> list = rocIdDate.get(rocId);
      if (list == null) {
        continue;
      }
      for (int i = list.size() - 1; i >= 0; i--) {
        if (daysBetween(nhiNo2Time, list.get(i).getMrEndDate()) <= days) {
          // qualify
          list.remove(i);
        }
      }
      if (list.isEmpty()) {
        rocIdDate.remove(rocId);
      }
    }
    if (rocIdDate.size() == 0) {
      return;
    }
    List<MR> violateMr = new ArrayList<MR>();
    for (String rocId : rocIdDate.keySet()) {
      violateMr.addAll(rocIdDate.get(rocId));
    }
    String wording = String.format(wordings.get("VIOLATE_USER_WITH_ORDER_CODE_N_DAYS"), nhiNo,
        nhiNo2.get(0), String.valueOf(days));
    insertIntelligent(violateMr, null, batch, nhiNo, wording);
  }

  /**
   * 檢查給藥天數
   * 
   * @param isOp
   * @param nhiNo
   * @param days
   * @param max
   * @param mrList
   * @param mrIdList
   * @param batch
   * @param wording
   */
  private void checkDrugDay(boolean isEnable, String nhiNo, int days, List<MR> mrList,
      List<Long> mrIdList, List<INTELLIGENT> batch) {
    if (!isEnable) {
      return;
    }
    String wording = String.format(wordings.get("VIOLATE_DRUG_DAYS"), nhiNo, String.valueOf(days));
    checkDrugDayByDataFormat(true, nhiNo, days, mrList, mrIdList, batch, wording);
    checkDrugDayByDataFormat(false, nhiNo, days, mrList, mrIdList, batch, wording);
  }

  private void checkDrugDayByDataFormat(boolean isOp, String nhiNo, int days, List<MR> mrList,
      List<Long> mrIdList, List<INTELLIGENT> batch, String wording) {
    List<Object[]> data =
        (isOp) ? oppDao.getMrIdAndStartTimeAndEndTimeByOrderCodeAndMrIdList(nhiNo, mrIdList)
            : ippDao.getMrIdAndStartTimeAndEndTimeByOrderCodeAndMrIdList(nhiNo, mrIdList);
    //System.out.println("checkUseTimesEveryDayByDataFormat " + ", order count=" + data.size());
    // 存在違反規則的 MR ID
    HashMap<Long, String> mrIdMap = new HashMap<Long, String>();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
    int daysSum = 0;
    long lastMrId = 0;
    for (Object[] obj : data) {
      long mrId = ((BigInteger) obj[0]).longValue();
      if (lastMrId != mrId) {
        daysSum = 0;
        lastMrId = mrId;
      }
      String startTime = (String) obj[1];
      String endTime = (String) obj[2];
      if (mrIdMap.containsKey(Long.valueOf(mrId))) {
        continue;
      }
      if (startTime == null || endTime == null
          || startTime.substring(0, 7).equals(endTime.substring(0, 7))) {
        // 同一天
        daysSum++;
      } else {
        daysSum += diffDays(startTime, endTime, sdf);
      }
      if (daysSum > days) {
        mrIdMap.put(mrId, "");
      }
    }
    insertIntelligent(mrIdMap, mrList, batch, nhiNo, wording);
  }

  /**
   * 每月申報數量，不可超過該科門診就診人次之百分之 ?
   * 
   * @param isEnable
   * @param nhiNo
   * @param funcTypeList
   * @param mrList
   * @param mrIdList
   * @param batch
   */
  private void checkFuncTypePercent(boolean isEnable, String nhiNo, int percentMax, List<MR> mrList,
      List<Long> mrIdList, List<INTELLIGENT> batch) {
    if (!isEnable) {
      return;
    }
    String wording =
        String.format(wordings.get("VIOLATE_FUNC_TYPE_PERCENT"), nhiNo, String.valueOf(percentMax));

    // 違反支付準則的病歷
    List<MR> violateMr = new ArrayList<MR>();
    // 科別
    Set<String> funcTypes = findDistinctFuncType(mrList);
    // 申請年月
    List<String> applYms = findDistinctApplYm(mrList);
    for (String applYm : applYms) {
      // 違反支付準則的科別
      List<String> violateFuncTypes = new ArrayList<String>();
      for (String funcType : funcTypes) {
        List<Object[]> data =
            mrDao.getMrCountByFuncTypeAndOrderCode(applYm, funcType, "%," + nhiNo + ",%");
        Object[] obj = data.get(0);
        double percent =
            (((BigInteger) obj[0]).doubleValue() * 100) / ((BigInteger) obj[1]).doubleValue();
        if (percent > (double) percentMax) {
          violateFuncTypes.add(funcType);
        }
      }
      for (MR mr : mrList) {
        if (!mr.getApplYm().equals(applYm)) {
          continue;
        }
        for (String funcType : violateFuncTypes) {
          if (mr.getFuncType().equals(funcType)) {
            violateMr.add(mr);
            break;
          }
        }
      }
    }

    insertIntelligent(violateMr, null, batch, nhiNo, wording);
  }

  /**
   * 限定同患者前一次應用與當次應用待申報此支付標準代碼，每次申報間隔>= ? 日
   * 
   * @param isEnable
   * @param nhiNo
   * @param days
   * @param mrList
   * @param batch
   */
  private void checkIntervalDays(boolean isEnable, String nhiNo, int days, List<MR> mrList,
      List<INTELLIGENT> batch) {
    if (!isEnable) {
      return;
    }
    String wording =
        String.format(wordings.get("VIOLATE_INTERVAL_DAYS"), nhiNo, String.valueOf(days));
    // 存在違反規則的 rocId
    HashMap<String, String> rocIdMap = new HashMap<String, String>();
    java.sql.Date[] minMax = getMinAndMaxDate(mrList);
    minMax[0] = addDate(minMax[0], -days - 5);
    List<Object[]> data = mrDao.getMrEndDateByOrderCode("%," + nhiNo + ",%", minMax[0], minMax[1]);
    String rocId = "";
    Date lastDate = null;
    for (Object[] obj : data) {
      if (rocIdMap.containsKey((String) obj[1])) {
        continue;
      }
      if (!rocId.equals((String) obj[1])) {
        rocId = (String) obj[1];
      } else {
        if (daysBetween(lastDate, (Date) obj[2]) < days) {
          MR mr = findMrById(mrList, ((BigInteger) obj[0]).longValue());
          if (mr != null) {
            rocIdMap.put(rocId, "");
          }
        }
      }
      lastDate = (Date) obj[2];
    }
    List<MR> violateMR = new ArrayList<MR>();
    for (MR mr : mrList) {
      if (rocIdMap.get(mr.getRocId()) != null) {
        violateMR.add(mr);
      }
    }
    insertIntelligent(violateMR, null, batch, nhiNo, wording);
  }

  private java.sql.Date[] getMinAndMaxDate(List<MR> list) {
    java.sql.Date[] result = new java.sql.Date[2];
    result[0] = new java.sql.Date(list.get(0).getMrEndDate().getTime());
    result[1] = result[0];
    for (MR mr : list) {
      if (result[0].getTime() > mr.getMrEndDate().getTime()) {
        result[0] = new java.sql.Date(mr.getMrEndDate().getTime());
      } else if (result[1].getTime() < mr.getMrEndDate().getTime()) {
        result[1] = new java.sql.Date(mr.getMrEndDate().getTime());
      }
    }
    return result;
  }

  private java.sql.Date addDate(java.sql.Date date, int days) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date(date.getTime()));
    cal.add(Calendar.DAY_OF_YEAR, -days);
    return new java.sql.Date(cal.getTimeInMillis());
  }

  /**
   * 檢查每次使用間隔時間
   * 
   * @param isEnable
   * @param nhiNo
   * @param totalTimes 使用總次數
   * @param firstN 第一次使用後隔多久(分鐘)
   * @param overTimesN 每隔多久(分鐘)可使用一次
   * @param mrList
   * @param mrIdList
   * @param batch
   */
  private void checkDrugNoUseDiffMinutes(boolean isEnable, String nhiNo, int totalTimes, int firstN,
      int overTimesN, List<MR> mrList, List<Long> mrIdList, List<INTELLIGENT> batch) {
    if (!isEnable) {
      return;
    }
    String wording = String.format(wordings.get("VIOLATE_USE_MINUTES"), nhiNo,
        String.valueOf(totalTimes), String.valueOf(firstN), String.valueOf(overTimesN));
    // 存在違反規則的 MR ID
    HashMap<Long, String> mrIdMap = new HashMap<Long, String>();
    List<Object[]> data = oppDao.getOrderCodeTimeDiffByMrid("%," + nhiNo + ",%", mrIdList);
    long lastMrId = 0;
    for (Object[] obj : data) {
      long mrId = ((BigInteger) obj[0]).longValue();
      if (mrIdMap.containsKey(Long.valueOf(mrId))) {
        continue;
      }

    }

    insertIntelligent(mrIdMap, mrList, batch, nhiNo, wording);
  }

  /**
   * 限定同患者前一次應用與當次應用待申報此支付標準代碼，在1年內加總次數不可超過 max.
   * 
   * @param isEnable
   * @param nhiNo
   * @param days
   * @param mrList
   * @param batch
   */
  private void checkTimesIn1Year(boolean isEnable, String nhiNo, int max, List<MR> mrList,
      List<INTELLIGENT> batch) {
    if (!isEnable) {
      return;
    }
    String wording = String.format(wordings.get("VIOLATE_TIMES_YEAR"), nhiNo, String.valueOf(max));
    // 存在違反規則的 MR ID
    HashMap<String, String> rocIdMap = new HashMap<String, String>();
    List<Object[]> data = mrDao.getMrEndDateByOrderCode("%," + nhiNo + ",%");
    String rocId = "";
    List<Date> dates = null;
    for (Object[] obj : data) {
      if (rocIdMap.containsKey((String) obj[1])) {
        continue;
      }
      if (!rocId.equals((String) obj[1])) {
        rocId = (String) obj[1];
        dates = new ArrayList<Date>();
        dates.add((Date) obj[2]);
      } else {
        dates.add((Date) obj[2]);
        if (daysBetween(dates.get(0), (Date) obj[2]) <= 365) {
          if (dates.size() > max) {
            rocIdMap.put(rocId, "");
            continue;
          }
        } else {
          // 和第一筆比超過1年
          do {
            dates.remove(0);
          } while ((daysBetween(dates.get(0), (Date) obj[2]) + 1) > 365);
        }
      }
    }

    List<MR> violateMrList = new ArrayList<MR>();
    for (MR mr : mrList) {
      if (rocIdMap.containsKey(mr.getRocId())) {
        violateMrList.add(mr);
      }
    }
    insertIntelligent(violateMrList, null, batch, nhiNo, wording);
  }

  private void insertIntelligent(HashMap<Long, String> mrIdMap, List<MR> mrList,
      List<INTELLIGENT> batch, String nhiNo, String wording) {
    if (mrIdMap == null || mrIdMap.size() == 0) {
      return;
    }
    ArrayList<Long> violateMrIdList = new ArrayList<Long>();
    for (Long mrId : mrIdMap.keySet()) {
      violateMrIdList.add(mrId);
    }
    insertIntelligent(mrList, violateMrIdList, batch, nhiNo, wording);
  }

  private void insertIntelligent(List<MR> mrList, List<Long> mrIdList, List<INTELLIGENT> batch,
      String nhiNo, String wording) {
    String reason = wording.length() < 100 ? wording : wording.substring(0, 99);
    List<MR> violateMrList = null;
    boolean needAddMrIdList = false;
    if (mrIdList == null) {
      needAddMrIdList = true;
      mrIdList = new ArrayList<Long>();
      violateMrList = mrList;
    } else if (mrIdList != null && mrIdList.size() == 0) {
      return;
    } else {
      violateMrList = getMrByMrId(mrList, mrIdList);
    }

    long start = System.currentTimeMillis();
    for (MR violateMr : violateMrList) {
      is.insertIntelligentNoUpdateMrStatus(violateMr, INTELLIGENT_REASON.VIOLATE.value(), nhiNo,
          reason, true, batch);
      if (needAddMrIdList) {
        mrIdList.add(violateMr.getId());
      }
    }
    mrDao.updateMultiMrStauts(MR_STATUS.WAIT_CONFIRM.value(), mrIdList);
    long usedTime = System.currentTimeMillis() - start;
    // System.out.println("insertIntelligent " + mrList.size() + " use " + usedTime);
  }

  /**
   * 檢查支付準則
   * 
   * @pt 支付準則object
   */
  public void checkFee(PaymentTermsPl pt, List<INTELLIGENT> batch) {
    if (pt instanceof PtOutpatientFeePl) {
      // 需計算到單一醫師執行此醫令單月上限，所以要每月每月跑
      checkFeeByMonth(pt, batch);
    } else {
      int countOfMR = getCountOfUseCodeMR(pt.getNhi_no());
      if (countOfMR < 1000) {
        // 整個DB不到1000筆，一次處理全部
        List<MR> mrList = mrDao.findByCodeAllContaining("," + pt.getNhi_no() + ",");
        checkFee(pt, mrList, batch);
      } else {
        // by 年月處理
        checkFeeByMonth(pt, batch);
      }
    }
  }

  public void checkFeeByMonth(PaymentTermsPl pt, List<INTELLIGENT> batch) {
    Calendar calMin = ps.getMinMaxCalendar(new Date(pt.getStart_date()), true);
    if (calMin.getTimeInMillis() > pt.getStart_date()) {
      Calendar ptStart = Calendar.getInstance();
      ptStart.setTimeInMillis(pt.getStart_date());
      if (ptStart.get(Calendar.YEAR) == calMin.get(Calendar.YEAR)
          && ptStart.get(Calendar.MONTH) == calMin.get(Calendar.MONTH)) {
        // 同年同月，以 pt.start_date 為起始日
        calMin.setTimeInMillis(pt.getStart_date());
      }
    }
    Calendar calMax = ps.getMinMaxCalendar(new Date(pt.getEnd_date()), false);
    // System.out.println("calMin=" + DateTool.getChineseYmDate(calMin) + ", calMax=" +
    // DateTool.getChineseYmDate(calMax));
    Calendar cal = calMin;
    do {
      Date start = cal.getTime();
      cal.add(Calendar.MONTH, 1);
      // 到月底
      cal.add(Calendar.DAY_OF_YEAR, -1);
      if (cal.getTimeInMillis() > calMax.getTimeInMillis()) {
        cal = calMax;
      }
      Date end = cal.getTime();
      long startSQL = System.currentTimeMillis();
      List<MR> mrList = mrDao.getMRByCodeLikeAndMrEndDate("%," + pt.getNhi_no() + ",%", start, end);
      long usedTime = System.currentTimeMillis() - startSQL;
      checkFee(pt, mrList, batch);
      is.saveIntelligentBatch(batch);
      // 回到下個月月初
      cal.add(Calendar.DAY_OF_YEAR, 1);
    } while (cal.getTimeInMillis() < calMax.getTimeInMillis());
  }

  /**
   * 
   * @param pt
   * @param mrList 有使用 pt.nhi_no 醫令的 MR list
   * @param batch
   */
  public void checkFee(PaymentTermsPl pt, List<MR> mrList, List<INTELLIGENT> batch) {
    List<MR> mrListInDates = getMrInEffectedDate(mrList, pt.getStart_date(), pt.getEnd_date());
    if (mrListInDates.size() == 0) {
      return;
    }
    checkDataFormat(pt, mrListInDates, batch);
    if (pt instanceof PtOutpatientFeePl) {
      checkOutpatientFee((PtOutpatientFeePl) pt, mrListInDates, batch);
    } else if (pt instanceof PtInpatientFeePl) {
      checkInpatientFee((PtInpatientFeePl) pt, mrListInDates, batch);
    } else if (pt instanceof PtWardFeePl) {
      checkWardFee((PtWardFeePl) pt, mrListInDates, batch);
    } else if (pt instanceof PtPsychiatricWardFeePl) {
      checkPsychiatricWardFee((PtPsychiatricWardFeePl) pt, mrListInDates, batch);
    } else if (pt instanceof PtSurgeryFeePl) {
      checkSurgeryFee((PtSurgeryFeePl) pt, mrListInDates, batch);
    } else if (pt instanceof PtTreatmentFeePl) {
      checkTreatmentFee((PtTreatmentFeePl) pt, mrListInDates, batch);
    } else if (pt instanceof PtNutritionalFeePl) {
      checkNutritionalFee((PtNutritionalFeePl) pt, mrListInDates, batch);
    } else if (pt instanceof PtAdjustmentFeePl) {
      checkAdjustmentFee((PtAdjustmentFeePl) pt, mrListInDates, batch);
    } else if (pt instanceof PtMedicineFeePl) {
      checkMedicineFee((PtMedicineFeePl) pt, mrListInDates, batch);
    } else if (pt instanceof PtRadiationFeePl) {
      checkRadiationFee((PtRadiationFeePl) pt, mrListInDates, batch);
    } else if (pt instanceof PtInjectionFeePl) {
      checkInjectionFee((PtInjectionFeePl) pt, mrListInDates, batch);
    } else if (pt instanceof PtQualityServicePl) {
      checkQualityServiceFee((PtQualityServicePl) pt, mrListInDates, batch);
    } else if (pt instanceof PtRehabilitationFeePl) {
      checkRehabilitationFee((PtRehabilitationFeePl) pt, mrListInDates, batch);
    } else if (pt instanceof PtPsychiatricFeePl) {
      checkPsychiatricFee((PtPsychiatricFeePl) pt, mrListInDates, batch);
    } else if (pt instanceof PtBoneMarrowTransFeePl) {
      checkBoneMarrowTransFee((PtBoneMarrowTransFeePl) pt, mrListInDates, batch);
    } else if (pt instanceof PtAnesthesiaFeePl) {
      checkAnesthesiaFee((PtAnesthesiaFeePl) pt, mrListInDates, batch);
    } else if (pt instanceof PtSpecificMedicalFeePl) {
      checkSpecificMedicalFee((PtSpecificMedicalFeePl) pt, mrListInDates, batch);
    } else if (pt instanceof PtOthersFeePl) {
      checkOthersFee((PtOthersFeePl) pt, mrListInDates, batch);
    }
  }

  private List<MR> getMrInEffectedDate(List<MR> list, long startDate, long endDate) {
    List<MR> result = new ArrayList<>();
    for (MR mr : list) {
      if (mr.getMrEndDate().getTime() >= startDate && mr.getMrEndDate().getTime() <= endDate) {
        result.add(mr);
      }
    }
    return result;
  }

  public List<Long> getMrId(List<MR> mrList, String nhiNo) {
    List<Long> result = new ArrayList<Long>();
    for (MR mr : mrList) {
      if (nhiNo != null) {
        if (mr.getCodeAll().indexOf("," + nhiNo + ",") < 0) {
          continue;
        }
      }
      result.add(mr.getId());
    }
    return result;
  }

  public List<MR> getMrByMrId(List<MR> mrList, List<Long> mrIdList) {
    List<MR> result = new ArrayList<MR>();
    for (Long mrId : mrIdList) {
      for (MR mr : mrList) {
        if (mr.getId().longValue() == mrId.longValue()) {
          result.add(mr);
          break;
        }
      }
    }
    return result;
  }

  public MR findMrById(List<MR> mrList, long id) {
    for (MR mr : mrList) {
      if (mr.getId().longValue() == id) {
        return mr;
      }
    }
    return null;
  }

  private String stringListToString(List<String> strList) {
    StringBuffer sb = new StringBuffer();
    for (String string : strList) {
      sb.append(string);
      sb.append(",");
    }
    if (sb.length() > 0 && sb.charAt(sb.length() - 1) == ',') {
      sb.deleteCharAt(sb.length() - 1);
    }
    return sb.toString();
  }

  /**
   * 檢查門診診察費
   * 
   * @param pt 門診診察費object
   * @param mrList 有使用該醫令的病歷list
   */
  public void checkOutpatientFee(PtOutpatientFeePl pt, List<MR> mrList, List<INTELLIGENT> batch) {
    List<Long> mrIdList = getMrId(mrList, null);
    checkDentistAndCM(pt, mrList, mrIdList, batch);
    checkDispensing(pt, mrList, mrIdList, batch);
    checkOutIsland(pt, mrList, mrIdList, batch);
    checkHoliday(pt, mrList, mrIdList, batch);
    checkExcludeOrderCode(pt.getExclude_nhi_no_enable() == 1, pt.getNhi_no(), pt.getLst_nhi_no(),
        mrList, mrIdList, batch, false);
    checkAge(pt.getLim_age_enable() == 1, true, pt.getNhi_no(), pt.getLim_age_type(),
        pt.getLim_age(), mrList, mrIdList, batch);
    checkFuncType(pt.getLim_division_enable() == 1, pt.getNhi_no(), pt.getLst_division(), mrList,
        mrIdList, batch);
    checkCountOfUniquePrsn(pt.getLim_max_enable() == 1, pt.getNhi_no(), pt.getLim_max(), mrList,
        mrIdList, batch);
  }

  /**
   * 檢查住院診察費
   * 
   * @param pt 住院診察費object
   * @param mrList 有使用該醫令的病歷list
   */
  public void checkInpatientFee(PtInpatientFeePl pt, List<MR> mrList, List<INTELLIGENT> batch) {
    List<Long> mrIdList = getMrId(mrList, null);
    checkExcludeOrderCode(pt.getExclude_nhi_no_enable() == 1, pt.getNhi_no(), pt.getLst_nhi_no(),
        mrList, mrIdList, batch, false);
    checkIncludeOrderCode(pt.getCoexist_nhi_no_enable() == 1, pt.getNhi_no(), pt.getLst_co_nhi_no(),
        mrList, mrIdList, batch);
    // 單一住院就醫紀錄應用數量,限定小於等於n次
    checkIPUseTimes(pt.getMax_inpatient_enable() == 1, pt.getNhi_no(), pt.getMax_inpatient(),
        mrList, mrIdList, batch);
    // 單一急診就醫紀錄應用數量,限定<=次數
    checkOrderCodeUseTimesWithEM(pt.getMax_emergency_enable() == 1, pt.getNhi_no(),
        pt.getMax_emergency(), mrList, mrIdList, batch);
    // 每組病歷號碼，每院限申報次數
    checkUseTimesInAllMr(pt.getMax_patient_no_enable() == 1, pt.getNhi_no(), pt.getMax_patient_no(),
        mrList, batch);
    // 違反支付準則限定不可與門診診察費並存
    checkIPWithOP(pt.getNo_coexist_enable() == 1, pt.getNhi_no(), mrList, mrIdList, batch);
  }

  /**
   * 檢查病房費
   * 
   * @param pt 病房費object
   * @param mrList 有使用該醫令的病歷list
   */
  public void checkWardFee(PtWardFeePl pt, List<MR> mrList, List<INTELLIGENT> batch) {
    List<Long> mrIdList = getMrId(mrList, null);
    checkExcludeOrderCode(pt.getExclude_nhi_no_enable() == 1, pt.getNhi_no(), pt.getLst_nhi_no(),
        mrList, mrIdList, batch, false);
    checkIpHours(pt.getMin_stay_enable() == 1, pt.getNhi_no(), 2, pt.getMin_stay(), mrList,
        mrIdList, batch);
    checkIpHours(pt.getMax_stay_enable() == 1, pt.getNhi_no(), 3, pt.getMax_stay(), mrList,
        mrIdList, batch);
  }

  /**
   * 檢查精神慢性病房費
   * 
   * @param pt
   * @param mrList
   * @param batch
   */
  public void checkPsychiatricWardFee(PtPsychiatricWardFeePl pt, List<MR> mrList,
      List<INTELLIGENT> batch) {

  }

  /**
   * 檢查手術費
   * 
   * @param pt
   * @param mrList
   * @param batch
   */
  public void checkSurgeryFee(PtSurgeryFeePl pt, List<MR> mrList, List<INTELLIGENT> batch) {
    List<Long> mrIdList = getMrId(mrList, null);
    checkExcludeOrderCode(pt.getExclude_nhi_no_enable() == 1, pt.getNhi_no(), pt.getLst_nhi_no(),
        mrList, mrIdList, batch, false);
    checkAge(pt.getLim_age_enable() == 1, true, pt.getNhi_no(), 3, pt.getLim_age(), mrList,
        mrIdList, batch);
    checkAge(pt.getLim_age_enable() == 1, false, pt.getNhi_no(), 3, pt.getLim_age(), mrList,
        mrIdList, batch);
    checkFuncType(pt.getLim_division_enable() == 1, pt.getNhi_no(), pt.getLst_division(), mrList,
        mrIdList, batch);
  }

  /**
   * 檢查治療處置費
   * 
   * @param pt
   * @param mrList
   * @param batch
   */
  public void checkTreatmentFee(PtTreatmentFeePl pt, List<MR> mrList, List<INTELLIGENT> batch) {
    List<Long> mrIdList = getMrId(mrList, null);
    int dataFormat = 0; 
    // 門診 + 1
    dataFormat += pt.getOutpatient_type() == 1 ? 1 : 0;
    // 住院 + 2
    dataFormat += pt.getHospitalized_type() == 1 ? 2 : 0;
    
    // 不可與此支付標準代碼並存單一就醫紀錄一併申報
    checkExcludeOrderCode(pt.getExclude_nhi_no_enable() == 1, pt.getNhi_no(), pt.getLst_nhi_no(),
        mrList, mrIdList, batch, false);
    // 需與以下任一支付標準代碼並存，方可進行申報
    checkIncludeOrderCode(pt.getCoexist_nhi_no_enable() == 1, pt.getNhi_no(), pt.getLst_co_nhi_no(),
        mrList, mrIdList, batch);
    checkAge(pt.getMax_age_enable() == 1, true, pt.getNhi_no(), 3, pt.getMax_age(), mrList,
        mrIdList, batch);
    checkAge(pt.getMax_age_enable() == 1, false, pt.getNhi_no(), 3, pt.getMax_age(), mrList,
        mrIdList, batch);
    // 限定科別
    checkFuncType(pt.getLim_division_enable() == 1, pt.getNhi_no(), pt.getLst_division(), mrList,
        mrIdList, batch);
    // 檢查單一就醫紀錄應用數量,限定<=次數
    checkAllUseTimes(pt.getMax_inpatient_enable() == 1, pt.getNhi_no(), pt.getMax_inpatient(),
        mrList, mrIdList, batch);
    // 單一就醫紀錄上，每日限定應用<= 次
    checkUseTimesEveryDay(pt.getMax_daily_enable() == 1, pt.getNhi_no(), 1, pt.getMax_daily(),
        mrList, mrIdList, batch);
    // 單一就醫紀錄上，每 ? 日內，限定應用<= ? 次
    checkUseTimesEveryDay(pt.getEvery_nday_enable() == 1, pt.getNhi_no(), pt.getEvery_nday_days(),
        pt.getEvery_nday_times(), mrList, mrIdList, batch);
    // 同患者限定每<= ? 日，總申報次數<= ? 次
    checkUserUseTimesInDays(dataFormat, pt.getPatient_nday_enable() == 1, pt.getNhi_no(),
        pt.getPatient_nday_days(), pt.getPatient_nday_times(), mrList, batch);
    // 每組病歷號碼，每院限申報次數
    checkUseTimesInAllMr(pt.getMax_patient_enable() == 1, pt.getNhi_no(), pt.getMax_patient(),
        mrList, batch);
    // 檢查是否包含以下任一ICD診斷碼
    checkICD(pt.getInclude_icd_no_enable() == 1, pt.getNhi_no(), pt.getLst_icd_no(), mrList, batch);
    // 每月申報數量，不可超過該科門診就診人次之百分之 ?
    checkFuncTypePercent(pt.getMax_month_enable() == 1, pt.getNhi_no(),
        pt.getMax_month_percentage(), mrList, mrIdList, batch);
  }

  /**
   * 檢查管灌飲食費及營養照護費
   * 
   * @param pt
   * @param mrList
   * @param batch
   */
  public void checkNutritionalFee(PtNutritionalFeePl pt, List<MR> mrList, List<INTELLIGENT> batch) {
    List<Long> mrIdList = getMrId(mrList, null);
    // 不可與此支付標準代碼並存單一就醫紀錄一併申報
    checkExcludeOrderCode(pt.getExclude_nhi_no_enable() == 1, pt.getNhi_no(), pt.getLst_nhi_no(),
        mrList, mrIdList, batch, false);
    // 單一住院就醫紀錄應用數量,限定小於等於n次
    checkIPUseTimes(pt.getMax_inpatient_enable() == 1, pt.getNhi_no(), pt.getMax_inpatient(),
        mrList, mrIdList, batch);
    // 單一就醫紀錄上，每日限定應用<= 次
    checkUseTimesEveryDay(pt.getMax_daily_enable() == 1, pt.getNhi_no(), 1, pt.getMax_daily(),
        mrList, mrIdList, batch);
    // 單一就醫紀錄上，每 ? 日內，限定應用<= ? 次
    checkUseTimesEveryDay(pt.getEvery_nday_enable() == 1, pt.getNhi_no(), pt.getEvery_nday_days(),
        pt.getEvery_nday_times(), mrList, mrIdList, batch);
    // 單一就醫紀錄上，超過 ? 日後，超出天數部份，限定應用<= ? 次
    checkUseTimesAfterDay(pt.getOver_nday_enable() == 1, pt.getNhi_no(), pt.getOver_nday_days(),
        pt.getOver_nday_times(), mrList, mrIdList, batch);
  }

  /**
   * 檢查調劑費
   * 
   * @param pt
   * @param mrList
   * @param batch
   */
  public void checkAdjustmentFee(PtAdjustmentFeePl pt, List<MR> mrList, List<INTELLIGENT> batch) {
    List<Long> mrIdList = getMrId(mrList, null);
    // 不可與此支付標準代碼並存單一就醫紀錄一併申報
    checkExcludeOrderCode(pt.getExclude_nhi_no_enable() == 1, pt.getNhi_no(), pt.getLst_nhi_no(),
        mrList, mrIdList, batch, false);
    // 需與以下任一支付標準代碼並存，方可進行申報
    checkIncludeOrderCode(pt.getCoexist_nhi_no_enable() == 1, pt.getNhi_no(), pt.getLst_co_nhi_no(),
        mrList, mrIdList, batch);
  }

  /**
   * 檢查藥費
   * 
   * @param pt
   * @param mrList
   * @param batch
   */
  public void checkMedicineFee(PtMedicineFeePl pt, List<MR> mrList, List<INTELLIGENT> batch) {
    List<Long> mrIdList = getMrId(mrList, null);
    // 每件給藥日數不得超過 ? 日
    checkDrugDay(pt.getMax_nday_enable() == 1, pt.getNhi_no(), pt.getMax_nday(), mrList, mrIdList,
        batch);

  }

  /**
   * 檢查放射線診療費
   * 
   * @param pt
   * @param mrList
   * @param batch
   */
  public void checkRadiationFee(PtRadiationFeePl pt, List<MR> mrList, List<INTELLIGENT> batch) {
    List<Long> mrIdList = getMrId(mrList, null);

    // 不可與此支付標準代碼並存單一就醫紀錄一併申報
    checkExcludeOrderCode(pt.getExclude_nhi_no_enable() == 1, pt.getNhi_no(), pt.getLst_nhi_no(),
        mrList, mrIdList, batch, false);
    // 需與以下任一支付標準代碼並存，方可進行申報
    checkIncludeOrderCode(pt.getCoexist_nhi_no_enable() == 1, pt.getNhi_no(), pt.getLst_co_nhi_no(),
        mrList, mrIdList, batch);
    // 檢查單一就醫紀錄應用數量,限定<=次數
    checkAllUseTimes(pt.getMax_inpatient_enable() == 1, pt.getNhi_no(), pt.getMax_inpatient(),
        mrList, mrIdList, batch);
    // 不可與此支付標準代碼並存單一就醫紀錄一併申報，需提示原由
    checkExcludeOrderCode(pt.getNotify_nhi_no_enable() == 1, pt.getNhi_no(), pt.getLst_ntf_nhi_no(),
        mrList, mrIdList, batch, true);
  }

  /**
   * 檢查注射
   * 
   * @param pt
   * @param mrList
   * @param batch
   */
  public void checkInjectionFee(PtInjectionFeePl pt, List<MR> mrList, List<INTELLIGENT> batch) {
    List<Long> mrIdList = getMrId(mrList, null);
    // 不可與此支付標準代碼並存單一就醫紀錄一併申報
    checkExcludeOrderCode(pt.getExclude_nhi_no_enable() == 1, pt.getNhi_no(), pt.getLst_nhi_no(),
        mrList, mrIdList, batch, false);
    // 檢查單一就醫紀錄應用數量,限定<=次數
    checkAllUseTimes(pt.getMax_inpatient_enable() == 1, pt.getNhi_no(), pt.getMax_inpatient(),
        mrList, mrIdList, batch);
    // 限定同患者前一次應用與當次應用待申報此支付標準代碼，每次申報間隔>= ? 日
    checkIntervalDays(pt.getInterval_nday_enable() == 1, pt.getNhi_no(), pt.getInterval_nday(),
        mrList, batch);
  }

  /**
   * 檢查品質支付服務
   * 
   * @param pt
   * @param mrList
   * @param batch
   */
  public void checkQualityServiceFee(PtQualityServicePl pt, List<MR> mrList,
      List<INTELLIGENT> batch) {
    List<Long> mrIdList = getMrId(mrList, null);
    int dataFormat = 0; 
    // 門診 + 1
    dataFormat += pt.getOutpatient_type() == 1 ? 1 : 0;
    // 住院 + 2
    dataFormat += pt.getHospitalized_type() == 1 ? 2 : 0;
    
    // 限定同患者前一次應用與當次應用待申報此支付標準代碼，每次申報間隔>= ? 日
    checkIntervalDays(pt.getInterval_nday_enable() == 1, pt.getNhi_no(), pt.getInterval_nday(),
        mrList, batch);
    // 限定同患者執行過 ? 支付標準代碼，>= ? 次，方可申報
    if (pt.getCoexist_nhi_no_enable() == 1) {
      List<ptNhiNoTimes> coList = pt.getLst_co_nhi_no();
      int minimum = 0;
      List<String> orderCodes = new ArrayList<String>();
      for (ptNhiNoTimes ptNhiNo : coList) {
        orderCodes.add(ptNhiNo.getNhi_no());
        if (ptNhiNo.getTimes() > minimum) {
          minimum = ptNhiNo.getTimes();
        }
      }
      checkUserIncludeOrderCodeTimes(true, pt.getNhi_no(), orderCodes, minimum, mrList, mrIdList,
          batch);
    }
    // 同患者限定每<= ? 日，總申報次數<= ? 次
    checkUserUseTimesInDays(dataFormat, pt.getEvery_nday_enable() == 1, pt.getNhi_no(), pt.getEvery_nday_days(),
        pt.getEvery_nday_times(), mrList, batch);
  }

  /**
   * 復健治療費
   * 
   * @param pt
   * @param mrList
   * @param batch
   */
  public void checkRehabilitationFee(PtRehabilitationFeePl pt, List<MR> mrList,
      List<INTELLIGENT> batch) {
    List<Long> mrIdList = getMrId(mrList, null);
    int dataFormat = 0; 
    // 門診 + 1
    dataFormat += pt.getOutpatient_type() == 1 ? 1 : 0;
    // 住院 + 2
    dataFormat += pt.getHospitalized_type() == 1 ? 2 : 0;
    
    // 不可與此支付標準代碼並存單一就醫紀錄一併申報
    checkExcludeOrderCode(pt.getExclude_nhi_no_enable() == 1, pt.getNhi_no(), pt.getLst_nhi_no(),
        mrList, mrIdList, batch, false);
    // 限定科別
    checkFuncType(pt.getLim_division_enable() == 1, pt.getNhi_no(), pt.getLst_division(), mrList,
        mrIdList, batch);
    // 同患者限定每<= ? 日，總申報次數<= ? 次
    
    checkUserUseTimesInDays(dataFormat, pt.getPatient_nday_enable() == 1, pt.getNhi_no(),
        pt.getPatient_nday_days(), pt.getPatient_nday_times(), mrList, batch);
    // 檢查是否包含以下任一ICD診斷碼
    checkICD(pt.getInclude_icd_no_enable() == 1, pt.getNhi_no(), pt.getLst_icd_no(), mrList, batch);
    // 限定同患者執行過 ? 支付標準代碼， <= ? 日，申報過此支付代碼
    checkUserUseOtherOrderCodeInDays(pt.getCoexist_nhi_no_enable() == 1, pt.getNhi_no(),
        pt.getLst_co_nhi_no(), pt.getMin_coexist(), mrList, batch);
  }

  /**
   * 檢查精神醫療治療費
   * 
   * @param pt
   * @param mrList
   * @param batch
   */
  public void checkPsychiatricFee(PtPsychiatricFeePl pt, List<MR> mrList, List<INTELLIGENT> batch) {
    List<Long> mrIdList = getMrId(mrList, null);
    int dataFormat = 0; 
    // 門診 + 1
    dataFormat += pt.getOutpatient_type() == 1 ? 1 : 0;
    // 住院 + 2
    dataFormat += pt.getHospitalized_type() == 1 ? 2 : 0;
    
    // 不可與此支付標準代碼並存單一就醫紀錄一併申報
    checkExcludeOrderCode(pt.getExclude_nhi_no_enable() == 1, pt.getNhi_no(), pt.getLst_nhi_no(),
        mrList, mrIdList, batch, false);
    // 限定科別
    checkFuncType(pt.getLim_division_enable() == 1, pt.getNhi_no(), pt.getLst_division(), mrList,
        mrIdList, batch);
    // 檢查單一就醫紀錄應用數量,限定<=次數
    checkAllUseTimes(pt.getMax_inpatient_enable() == 1, pt.getNhi_no(), pt.getMax_inpatient(),
        mrList, mrIdList, batch);
    // 同患者限定每<= ? 日，總申報次數<= ? 次
    checkUserUseTimesInDays(dataFormat, pt.getPatient_nday_enable() == 1, pt.getNhi_no(),
        pt.getPatient_nday_days(), pt.getPatient_nday_times(), mrList, batch);
  }

  /**
   * 輸血及骨髓移植費
   * 
   * @param pt
   * @param mrList
   * @param batch
   */
  public void checkBoneMarrowTransFee(PtBoneMarrowTransFeePl pt, List<MR> mrList,
      List<INTELLIGENT> batch) {
    List<Long> mrIdList = getMrId(mrList, null);

    // 需與以下任一支付標準代碼並存，方可進行申報
    checkIncludeOrderCode(pt.getCoexist_nhi_no_enable() == 1, pt.getNhi_no(), pt.getLst_co_nhi_no(),
        mrList, mrIdList, batch);
    // 限定科別
    checkFuncType(pt.getLim_division_enable() == 1, pt.getNhi_no(), pt.getLst_division(), mrList,
        mrIdList, batch);
  }

  /**
   * 麻醉費
   * 
   * @param pt
   * @param mrList
   * @param batch
   */
  public void checkAnesthesiaFee(PtAnesthesiaFeePl pt, List<MR> mrList, List<INTELLIGENT> batch) {
    List<Long> mrIdList = getMrId(mrList, null);

    // 需與以下任一支付標準代碼並存，方可進行申報
    checkIncludeOrderCode(pt.getCoexist_nhi_no_enable() == 1, pt.getNhi_no(), pt.getLst_co_nhi_no(),
        mrList, mrIdList, batch);
    // 限定科別
    checkFuncType(pt.getLim_division_enable() == 1, pt.getNhi_no(), pt.getLst_division(), mrList,
        mrIdList, batch);
    // 檢查是否包含任一DRG代碼
    checkDRG(pt.getInclude_drg_no_enable() == 1, pt.getNhi_no(), pt.getLst_drg_no(), mrList,
        mrIdList, batch);
    // 檢查使用間隔分鐘
    checkDrugNoUseDiffMinutes(pt.getOver_times_enable() == 1, pt.getNhi_no(), pt.getOver_times_n(),
        pt.getOver_times_first_n(), pt.getOver_times_next_n(), mrList, mrIdList, batch);
  }

  /**
   * 檢查特定診療檢查費
   * 
   * @param pt
   * @param mrList
   * @param batch
   */
  public void checkSpecificMedicalFee(PtSpecificMedicalFeePl pt, List<MR> mrList,
      List<INTELLIGENT> batch) {
    List<Long> mrIdList = getMrId(mrList, null);

    // 不可與此支付標準代碼並存單一就醫紀錄一併申報
    checkExcludeOrderCode(pt.getExclude_nhi_no_enable() == 1, pt.getNhi_no(), pt.getLst_nhi_no(),
        mrList, mrIdList, batch, false);
    // 限定同患者前一次應用與當次應用待申報此支付標準代碼，每次申報間隔>= ? 日
    checkIntervalDays(pt.getInterval_nday_enable() == 1, pt.getNhi_no(), pt.getInterval_nday(),
        mrList, batch);
    // 限定同患者前一次應用與當次應用待申報此支付標準代碼，在1年內加總次數不可超過 max.
    checkTimesIn1Year(pt.getMax_times_enable() == 1, pt.getNhi_no(), pt.getMax_times(), mrList,
        batch);
  }

  /**
   * 檢查不分類
   * 
   * @param pt
   * @param mrList
   * @param batch
   */
  public void checkOthersFee(PtOthersFeePl pt, List<MR> mrList, List<INTELLIGENT> batch) {
    List<Long> mrIdList = getMrId(mrList, null);
    int dataFormat = 0; 
    // 門診 + 1
    dataFormat += pt.getOutpatient_type() == 1 ? 1 : 0;
    // 住院 + 2
    dataFormat += pt.getHospitalized_type() == 1 ? 2 : 0;

    // 不可與此支付標準代碼並存單一就醫紀錄一併申報
    checkExcludeOrderCode(pt.getExclude_nhi_no_enable() == 1, pt.getNhi_no(), pt.getLst_nhi_no(),
        mrList, mrIdList, batch, false);
    // 限定同患者前一次應用與當次應用待申報此支付標準代碼，每次申報間隔>= ? 日
    checkIntervalDays(pt.getInterval_nday_enable() == 1, pt.getNhi_no(), pt.getInterval_nday(),
        mrList, batch);
    // 限定同患者前一次應用與當次應用待申報此支付標準代碼，在1年內加總次數不可超過 max.
    checkTimesIn1Year(pt.getMax_times_enable() == 1, pt.getNhi_no(), pt.getMax_times(), mrList,
        batch);
    // 檢查單一就醫紀錄應用數量,限定<=次數
    checkAllUseTimes(pt.getMax_inpatient_enable() == 1, pt.getNhi_no(), pt.getMax_inpatient(),
        mrList, mrIdList, batch);
    // 同患者限定每<= ? 日，總申報次數<= ? 次
    // System.out.println("checkOthersFee " + pt.getPatient_nday_enable() +","+ pt.getNhi_no() +","+
    // pt.getPatient_nday_days() +","+ pt.getPatient_nday_times());
    checkUserUseTimesInDays(dataFormat, pt.getPatient_nday_enable() == 1, pt.getNhi_no(),
        pt.getPatient_nday_days(), pt.getPatient_nday_times(), mrList, batch);
  }

  public String appendStringListToString(List<String> list) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < list.size(); i++) {
      sb.append(list.get(i));
      sb.append("、");
    }
    if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '、') {
      sb.deleteCharAt(sb.length() - 1);
    }
    return sb.toString();
  }

  public static int hoursBetween(Date start, Date end) {
    long diff = end.getTime() - start.getTime();
    return (int) ((diff / 1000) / 3600);
  }

  /**
   * 使用天數
   * 
   * @param start
   * @param end
   * @return
   */
  public static int daysBetween(Date start, Date end) {
    long diff = end.getTime() - start.getTime();
    return ((int) (((diff / 1000) / 3600) / 24)) + 1;
  }

  /**
   * 醫令使用天數
   * 
   * @param startTime 民國年月日時分 yyyMMddHHmm
   * @param endTime 民國年月日時分 yyyMMddHHmm
   * @param sdf SimpleDateFormat("yyyyMMddHHmm")
   * @return
   */
  public static int diffDays(String startTime, String endTime, SimpleDateFormat sdf) {
    Date sDate = DateTool.convertChineseToYears(startTime, sdf);
    Date eDate = DateTool.convertChineseToYears(endTime, sdf);

    return daysBetween(sDate, eDate);
  }

  private Set<String> findDistinctFuncType(List<MR> mrList) {
    HashMap<String, String> map = new HashMap<String, String>();
    for (MR mr : mrList) {
      if (map.containsKey(mr.getFuncType())) {
        continue;
      }
      map.put(mr.getFuncType(), "");
    }
    return map.keySet();
  }

  private List<String> getRocIdList(List<MR> mrList) {
    HashMap<String, String> rocIdMap = new HashMap<String, String>();
    for (MR mr : mrList) {
      if (rocIdMap.containsKey(mr.getRocId())) {
        continue;
      }
      rocIdMap.put(mr.getRocId(), "");
    }
    return new ArrayList<String>(rocIdMap.keySet());
  }

  private List<String> findDistinctApplYm(List<MR> mrList) {
    HashMap<String, String> map = new HashMap<String, String>();
    for (MR mr : mrList) {
      if (map.containsKey(mr.getApplYm())) {
        continue;
      }
      map.put(mr.getApplYm(), "");
    }
    return new ArrayList<String>(map.keySet());
  }

  private Date getMinimumDate(List<MR> mrList) {
    Date result = null;
    for (MR mr : mrList) {
      if (result == null) {
        result = mr.getMrEndDate();
      } else if (mr.getMrEndDate().before(result)) {
        result = mr.getMrEndDate();
      }
    }
    return result;
  }
}
