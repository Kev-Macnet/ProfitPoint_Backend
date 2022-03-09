/**
 * Created on 2021/2/22.
 */
package tw.com.leadtek.nhiwidget.importdata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import tw.com.leadtek.nhiwidget.NHIWidget;
import tw.com.leadtek.nhiwidget.dao.IP_DDao;
import tw.com.leadtek.nhiwidget.dao.IP_PDao;
import tw.com.leadtek.nhiwidget.dao.IP_TDao;
import tw.com.leadtek.nhiwidget.dao.OP_DDao;
import tw.com.leadtek.nhiwidget.dao.OP_PDao;
import tw.com.leadtek.nhiwidget.dao.OP_TDao;
import tw.com.leadtek.nhiwidget.model.rdb.IP_D;
import tw.com.leadtek.nhiwidget.model.rdb.IP_T;
import tw.com.leadtek.nhiwidget.model.rdb.OP_D;
import tw.com.leadtek.nhiwidget.model.rdb.OP_P;
import tw.com.leadtek.nhiwidget.model.rdb.OP_T;
import tw.com.leadtek.tools.DateTool;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = NHIWidget.class)
@WebAppConfiguration
public class ImportSASData {

  private final static int OPDTE_YM = 1;
  private final static int OPDTE_CASE_TYPE = 4;
  private final static int OPDTE_SEQ_NO = 5;
  private final static int OPDTE_CURE_ITEM_NO1 = 6;
  private final static int OPDTE_CURE_ITEM_NO2 = 7;
  private final static int OPDTE_CURE_ITEM_NO3 = 8;
  private final static int OPDTE_CURE_ITEM_NO4 = 9;
  private final static int OPDTE_FUNC_TYPE = 10;
  private final static int OPDTE_FUNC_DATE = 11;
  private final static int OPDTE_TREAT_END_DATE = 12;
  private final static int OPDTE_BIRTH_YM = 13;
  private final static int OPDTE_CARD_SEQ_NO = 14;
  private final static int OPDTE_PAY_TYPE = 15;
  private final static int OPDTE_PART_NO = 16;
  private final static int OPDTE_ICD_CM_1 = 17;
  private final static int OPDTE_ICD_CM_2 = 18;
  private final static int OPDTE_ICD_CM_3 = 19;
  private final static int OPDTE_ICD_OP_CODE1 = 20;
  private final static int OPDTE_DRUG_DAY = 21;
  private final static int OPDTE_MED_TYPE = 22;
  private final static int OPDTE_DRUG_DOT = 23;
  private final static int OPDTE_TREAT_DOT = 24;
  private final static int OPDTE_TREAT_CODE = 25;
  private final static int OPDTE_DIAG_DOT = 26;
  private final static int OPDTE_DSVC_NO = 27;
  private final static int OPDTE_DSVC_DOT = 28;
  // private final static int OPDTE_ = 29;
  private final static int OPDTE_T_DOT = 30;
  private final static int OPDTE_PART_DOT = 31;
  private final static int OPDTE_T_APPL_DOT = 32;
  private final static int OPDTE_ROC_ID = 33;
  // private final static int OPDTE_ = 34;
  // private final static int OPDTE_ = 35;
  private final static int OPDTE_PRSN_ID = 36;
  // private final static int OPDTE_ = 37;
  // private final static int OPDTE_ = 38;
  // private final static int OPDTE_ = 39;
  // private final static int OPDTE_ = 40;
  // private final static int OPDTE_ = 41;
  private final static int OPDTE_HOSP_ID = 42;

  private final static int OPP_YM = 1;
  private final static int OPP_SEQ_NO = 5;
  private final static int OPP_ORDER_TYPE = 6;
  private final static int OPP_DRUG_NO = 7;
  private final static int OPP_DRUG_USE = 8;
  private final static int OPP_DRUG_FRE = 9;
  private final static int OPP_UNIT_P = 10;
  private final static int OPP_TOTAL_DOT = 12;
  private final static int OPP_COMM_HOSP_ID = 13;

  private final static int IPDTE_ID_BIRTH_YMD = 6;
  private final static int IPDTE_PAY_TYPE = 7;
  private final static int IPDTE_FUNC_TYPE = 10;
  private final static int IPDTE_IN_DATE = 11;
  private final static int IPDTE_OUT_DATE = 12;
  private final static int IPDTE_APPL_START_DATE = 13;
  private final static int IPDTE_APPL_END_DATE = 14;
  private final static int IPDTE_E_BED_DAY = 15;
  private final static int IPDTE_S_BED_DAY = 16;
  private final static int IPDTE_CASE_DRG_CODE = 17;
  private final static int IPDTE_TRAN_CODE = 20;
  private final static int IPDTE_ICD_CM_1 = 21;
  private final static int IPDTE_ICD_CM_2 = 22;
  private final static int IPDTE_ICD_CM_3 = 23;
  private final static int IPDTE_ICD_CM_4 = 24;
  private final static int IPDTE_ICD_CM_5 = 25;
  private final static int IPDTE_ICD_OP_CODE1 = 26;
  private final static int IPDTE_ICD_OP_CODE2 = 27;
  private final static int IPDTE_ICD_OP_CODE3 = 28;
  private final static int IPDTE_ICD_OP_CODE4 = 29;
  private final static int IPDTE_ICD_OP_CODE5 = 30;
  private final static int IPDTE_DIAG_DOT = 31;
  private final static int IPDTE_ROOM_DOT = 32;
  private final static int IPDTE_MEAL_DOT = 33;
  private final static int IPDTE_AMIN_DOT = 34;
  private final static int IPDTE_RADO_DOT = 35 ;
  private final static int IPDTE_THRP_DOT = 36;
  private final static int IPDTE_SGRY_DOT = 37;
  private final static int IPDTE_PHSC_DOT = 38;
  private final static int IPDTE_BLOD_DOT = 39;
  private final static int IPDTE_HD_DOT = 40;
  private final static int IPDTE_ANE_DOT = 41;
  private final static int IPDTE_METR_DOT = 42;
  private final static int IPDTE_DRUG_DOT = 43;
  private final static int IPDTE_DSVC_DOT = 44;
  private final static int IPDTE_NRTP_DOT = 45;
  private final static int IPDTE_INJT_DOT = 46;
  private final static int IPDTE_BABY_DOT = 47;
  private final static int IPDTE_MED_DOT = 49;
  private final static int IPDTE_PART_DOT = 50;
  private final static int IPDTE_APPL_DOT = 51;
  private final static int IPDTE_EB_APPL30_DOT = 52;
  private final static int IPDTE_EB_PART30_DOT = 53;
  private final static int IPDTE_EB_APPL60_DOT = 54;
  private final static int IPDTE_EB_PART60_DOT = 55;
  private final static int IPDTE_EB_APPL61_DOT = 56;
  private final static int IPDTE_EB_PART61_DOT = 57;
  private final static int IPDTE_SB_APPL30_DOT = 58;
  private final static int IPDTE_SB_PART30_DOT = 59;
  private final static int IPDTE_SB_APPL90_DOT = 60;
  private final static int IPDTE_SB_PART90_DOT = 61;
  private final static int IPDTE_SB_APPL180_DOT = 62;
  private final static int IPDTE_SB_PART180_DOT = 63;
  private final static int IPDTE_SB_APPL181_DOT = 64;
  private final static int IPDTE_SB_PART181_DOT = 65;
  private final static int IPDTE_PART_CODE = 66;
  private final static int IPDTE_ROC_ID = 67;
  private final static int IPDTE_PRSN_ID = 70;
  private final static int IPDTE_HOSP_ID = 73;

  /**
   * 存放已存在總表的年月，避免重複 query DB.
   */
  private HashMap<String, Long> existT = null;

  private final static String YM = "200901";

  @Autowired
  private IP_TDao ipTDao;

  @Autowired
  private OP_TDao opTDao;

  @Autowired
  private OP_DDao opDDao;

  @Autowired
  private IP_DDao ipDDao;

  @Autowired
  private OP_PDao opPDao;

  @Autowired
  private IP_PDao ipPDao;

  private int MIN_SEQ_NO = 37000;

  private List<OP_D> opdSet = new ArrayList<OP_D>();

  private List<OP_P> oppList = new ArrayList<OP_P>();
  
  private List<IP_D> ipdList = new ArrayList<IP_D>();

  private HashMap<Integer, Long> OP_DKeyID = null;

  /**
   * SAS txt file.
   */
  // @Ignore
  @Test
  public void importSASFile() {
    System.out.println("importSASFile");
     importFile("D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\資料匯入用\\test\\data\\h_nhi_opdte98.csv",
     false, false);
    //importFile("D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\資料匯入用\\test\\data\\h_nhi_opdto98.csv",
    //    false, true);
    
    existT = null;
  }

  public void importFile(String filename, boolean isIP, boolean isP) {
    System.out.println("import file:" + filename);
    existT = new HashMap<String, Long>();

    try {
      BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
      String s = null;
      while ((s = br.readLine()) != null) {
        if (s.indexOf("appl_date") > -1) {
          // header
          continue;
        }
        if (isIP) {

        } else {
          if (!isP) {
            processOPD(s);
          } else {
            processOPP(s);
          }
        }
      }
      if (opdSet.size() > 0) {
        opDDao.saveAll(opdSet);
        opdSet.clear();
      }
      if (oppList.size() > 0) {
        opPDao.saveAll(oppList);
        oppList.clear();
      }
      br.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void processOPD(String s) {
    String[] ss = s.split(",");
    if (YM != null && !YM.equals(ss[OPDTE_YM])) {
      return;
    }
    Long opT_id = getTID(ss[OPDTE_YM], false);
    OP_D opD = new OP_D();
    opD.setOptId(opT_id);
    opD.setCaseType(ss[OPDTE_CASE_TYPE]);
    opD.setSeqNo((int) Float.parseFloat(ss[OPDTE_SEQ_NO]));
    if (opD.getSeqNo().intValue() <= MIN_SEQ_NO) {
      System.out.println("return " + opD.getSeqNo().intValue());
      return;
    }
    opD.setCureItemNo1(ss[OPDTE_CURE_ITEM_NO1]);
    opD.setCureItemNo2(ss[OPDTE_CURE_ITEM_NO2]);
    opD.setCureItemNo3(ss[OPDTE_CURE_ITEM_NO3]);
    opD.setCureItemNo4(ss[OPDTE_CURE_ITEM_NO4]);
    opD.setFuncType(ss[OPDTE_FUNC_TYPE]);
    opD.setFuncDate(DateTool.convertToChineseYear(ss[OPDTE_FUNC_DATE]));
    if (ss[OPDTE_TREAT_END_DATE] != null && ss[OPDTE_TREAT_END_DATE].length() > 0) {
      opD.setFuncEndDate(DateTool.convertToChineseYear(ss[OPDTE_TREAT_END_DATE]));
    }
    if (ss[OPDTE_BIRTH_YM] != null && ss[OPDTE_BIRTH_YM].length() > 0) {
      opD.setIdBirthYmd(DateTool.convertToChineseYear(ss[OPDTE_BIRTH_YM]));
    }
    opD.setCardSeqNo(ss[OPDTE_CARD_SEQ_NO]);
    opD.setPayType(ss[OPDTE_PAY_TYPE]);
    opD.setPartNo(ss[OPDTE_PART_NO]);
    opD.setIcdCm1(ss[OPDTE_ICD_CM_1]);
    opD.setIcdCm2(ss[OPDTE_ICD_CM_2]);
    opD.setIcdCm3(ss[OPDTE_ICD_CM_3]);
    opD.setIcdOpCode1(ss[OPDTE_ICD_OP_CODE1]);
    if (ss[OPDTE_DRUG_DAY] != null) {
      opD.setDrugDay((int) Float.parseFloat(ss[OPDTE_DRUG_DAY]));
    }
    opD.setMedType(ss[OPDTE_MED_TYPE]);
    if (ss[OPDTE_DRUG_DOT] != null) {
      opD.setDrugDot((int) Float.parseFloat(ss[OPDTE_DRUG_DOT]));
    }
    if (ss[OPDTE_TREAT_DOT] != null) {
      opD.setTreatDot((int) Float.parseFloat(ss[OPDTE_TREAT_DOT]));
    }
    opD.setTreatCode(ss[OPDTE_TREAT_CODE]);
    if (ss[OPDTE_DIAG_DOT] != null) {
      opD.setDiagDot((int) Float.parseFloat(ss[OPDTE_DIAG_DOT]));
    }
    opD.setDsvcNo(ss[OPDTE_DSVC_NO]);
    if (ss[OPDTE_DSVC_DOT] != null) {
      opD.setDiagDot((int) Float.parseFloat(ss[OPDTE_DSVC_DOT]));
    }
    if (ss[OPDTE_T_DOT] != null) {
      opD.setTotalDot((int) Float.parseFloat(ss[OPDTE_T_DOT]));
    }
    if (ss[OPDTE_PART_DOT] != null) {
      opD.setPartDot((int) Float.parseFloat(ss[OPDTE_PART_DOT]));
    }
    if (ss[OPDTE_T_APPL_DOT] != null) {
      opD.setTotalApplDot((int) Float.parseFloat(ss[OPDTE_T_APPL_DOT]));
    }
    opD.setRocId(ss[OPDTE_ROC_ID]);
    opD.setPrsnId(ss[OPDTE_PRSN_ID]);
    opD.setHospId(ss[OPDTE_HOSP_ID]);
    opD.setUpdateAt(new Date());
    opdSet.add(opD);
    if (opdSet.size() > 500) {
      long start = System.currentTimeMillis();
      opDDao.saveAll(opdSet);
      long usedTime = System.currentTimeMillis() - start;
      System.out.println("save 500:" + usedTime);
      opdSet.clear();
    }
  }

  public void processOPP(String s) {
    // retrieveAllOP_DID(ym);
    String[] ss = s.split(",");
    if (YM != null && !YM.equals(ss[OPP_YM])) {
      return;
    }
    if (OP_DKeyID == null) {
      retrieveAllOP_DID(ss[OPP_YM]);
    }

    OP_P opP = new OP_P();

    // opD.setOptId(opT_id);
    int seqNo = (int) Float.parseFloat(ss[OPP_SEQ_NO]);
    Long opDID = OP_DKeyID.get(new Integer(seqNo));
    if (opDID.longValue() <= 12000) {
      return;
    }
    if (opDID != null) {
      opP.setOpdId(opDID);
    }
    if (ss[OPP_ORDER_TYPE] != null && ss[OPP_ORDER_TYPE].endsWith(".0")) {
      ss[OPP_ORDER_TYPE] = ss[OPP_ORDER_TYPE].substring(0, ss[OPP_ORDER_TYPE].length() - 2);
    }
    opP.setOrderType(ss[OPP_ORDER_TYPE]);
    opP.setDrugNo(ss[OPP_DRUG_NO]);
    if (ss[OPP_DRUG_USE] != null) {
      opP.setDrugUse(Double.parseDouble(ss[OPP_DRUG_USE]));
    }
    opP.setDrugFre(ss[OPP_DRUG_FRE]);
    if (ss[OPP_UNIT_P] != null && ss[OPP_UNIT_P].length() > 0) {
      try {
        opP.setUnitP(Float.parseFloat(ss[OPP_UNIT_P]));
      } catch (NumberFormatException e) {
        e.printStackTrace();
      }
    }
    if (ss[OPP_TOTAL_DOT] != null && ss[OPP_TOTAL_DOT].length() > 0) {
      opP.setTotalDot((int) (Float.parseFloat(ss[OPP_TOTAL_DOT])));
    }
    opP.setCommHospId(ss[OPP_COMM_HOSP_ID]);
    opP.setUpdateAt(new Date());
    oppList.add(opP);
    if (oppList.size() > 500) {
      long start = System.currentTimeMillis();
      opPDao.saveAll(oppList);
      long usedTime = System.currentTimeMillis() - start;
      System.out.println("save 500:" + usedTime);
      oppList.clear();
    }
  }


  public void processIPD(String s) {
    String[] ss = s.split(",");
    if (YM != null && !YM.equals(ss[OPDTE_YM])) {
      return;
    }
    Long ipT_id = getTID(ss[OPDTE_YM], true);
    IP_D ipD = new IP_D();
    ipD.setIptId(ipT_id);
    ipD.setCaseType(ss[OPDTE_CASE_TYPE]);
    ipD.setSeqNo((int) Float.parseFloat(ss[OPDTE_SEQ_NO]));
    if (ipD.getSeqNo().intValue() <= MIN_SEQ_NO) {
      System.out.println("return " + ipD.getSeqNo().intValue());
      return;
    }
    
    ipD.setIdBirthYmd(ss[IPDTE_ID_BIRTH_YMD]);
    ipD.setPayType(ss[IPDTE_PAY_TYPE]);
    ipD.setFuncType(ss[IPDTE_FUNC_TYPE]);
    ipD.setInDate(ss[IPDTE_IN_DATE]);
    ipD.setOutDate(ss[IPDTE_OUT_DATE]);
    ipD.setApplStartDate(ss[IPDTE_APPL_START_DATE]);
    ipD.setApplEndDate(ss[IPDTE_APPL_END_DATE]);
    ipD.setEbedDay(Integer.parseInt(ss[IPDTE_E_BED_DAY]));
    ipD.setSbedDay(Integer.parseInt(ss[IPDTE_S_BED_DAY]));
    ipD.setCaseDrgCode(ss[IPDTE_CASE_DRG_CODE]);
    ipD.setTranCode(ss[IPDTE_TRAN_CODE]);
    ipD.setIcdCm1(ss[IPDTE_ICD_CM_1]);
    ipD.setIcdCm2(ss[IPDTE_ICD_CM_2]);
    ipD.setIcdCm3(ss[IPDTE_ICD_CM_3]);
    ipD.setIcdCm4(ss[IPDTE_ICD_CM_4]);
    ipD.setIcdCm5(ss[IPDTE_ICD_CM_5]);
    ipD.setIcdOpCode1(ss[IPDTE_ICD_OP_CODE1]);
    ipD.setIcdOpCode2(ss[IPDTE_ICD_OP_CODE2]);
    ipD.setIcdOpCode3(ss[IPDTE_ICD_OP_CODE3]);
    ipD.setIcdOpCode4(ss[IPDTE_ICD_OP_CODE4]);
    ipD.setIcdOpCode5(ss[IPDTE_ICD_OP_CODE5]);
    if (ss[IPDTE_DIAG_DOT] != null && ss[IPDTE_DIAG_DOT].length() > 0) {
      ipD.setDiagDot(Integer.parseInt(ss[IPDTE_DIAG_DOT]));
    }
    if (ss[IPDTE_ROOM_DOT] != null && ss[IPDTE_ROOM_DOT].length() > 0) {
      ipD.setRoomDot(Integer.parseInt(ss[IPDTE_ROOM_DOT]));
    }
    if (ss[IPDTE_MEAL_DOT] != null && ss[IPDTE_MEAL_DOT].length() > 0) {
      ipD.setMealDot(Integer.parseInt(ss[IPDTE_MEAL_DOT]));
    }
    if (ss[IPDTE_AMIN_DOT] != null && ss[IPDTE_AMIN_DOT].length() > 0) {
      ipD.setAminDot(Integer.parseInt(ss[IPDTE_AMIN_DOT]));
    }
    if (ss[IPDTE_RADO_DOT] != null && ss[IPDTE_RADO_DOT].length() > 0) {
      ipD.setRadoDot(Integer.parseInt(ss[IPDTE_RADO_DOT]));
    }
    if (ss[IPDTE_THRP_DOT] != null && ss[IPDTE_THRP_DOT].length() > 0) {
      ipD.setThrpDot(Integer.parseInt(ss[IPDTE_THRP_DOT]));
    }
    if (ss[IPDTE_SGRY_DOT] != null && ss[IPDTE_SGRY_DOT].length() > 0) {
      ipD.setSgryDot(Integer.parseInt(ss[IPDTE_SGRY_DOT]));
    }
    if (ss[IPDTE_PHSC_DOT] != null && ss[IPDTE_PHSC_DOT].length() > 0) {
      ipD.setPhscDot(Integer.parseInt(ss[IPDTE_PHSC_DOT]));
    }
    if (ss[IPDTE_BLOD_DOT] != null && ss[IPDTE_BLOD_DOT].length() > 0) {
      ipD.setBlodDot(Integer.parseInt(ss[IPDTE_BLOD_DOT]));
    }
    if (ss[IPDTE_HD_DOT] != null && ss[IPDTE_HD_DOT].length() > 0) {
      ipD.setHdDot(Integer.parseInt(ss[IPDTE_HD_DOT]));
    }
    if (ss[IPDTE_ANE_DOT] != null && ss[IPDTE_ANE_DOT].length() > 0) {
      ipD.setAneDot(Integer.parseInt(ss[IPDTE_ANE_DOT]));
    }
    if (ss[IPDTE_METR_DOT] != null && ss[IPDTE_METR_DOT].length() > 0) {
      ipD.setMetrDot(Integer.parseInt(ss[IPDTE_METR_DOT]));
    }
    if (ss[IPDTE_DRUG_DOT] != null && ss[IPDTE_DRUG_DOT].length() > 0) {
      ipD.setDrugDot(Integer.parseInt(ss[IPDTE_DRUG_DOT]));
    }
    if (ss[IPDTE_DSVC_DOT] != null && ss[IPDTE_DSVC_DOT].length() > 0) {
      ipD.setDsvcDot(Integer.parseInt(ss[IPDTE_DSVC_DOT]));
    }
    if (ss[IPDTE_NRTP_DOT] != null && ss[IPDTE_NRTP_DOT].length() > 0) {
      ipD.setNrtpDot(Integer.parseInt(ss[IPDTE_NRTP_DOT]));
    }
    if (ss[IPDTE_INJT_DOT] != null && ss[IPDTE_INJT_DOT].length() > 0) {
      ipD.setInjtDot(Integer.parseInt(ss[IPDTE_INJT_DOT]));
    }
    if (ss[IPDTE_BABY_DOT] != null && ss[IPDTE_BABY_DOT].length() > 0) {
      ipD.setBabyDot(Integer.parseInt(ss[IPDTE_BABY_DOT]));
    }
    if (ss[IPDTE_MED_DOT] != null && ss[IPDTE_MED_DOT].length() > 0) {
      ipD.setMedDot(Integer.parseInt(ss[IPDTE_MED_DOT]));
    }
    if (ss[IPDTE_PART_DOT] != null && ss[IPDTE_PART_DOT].length() > 0) {
      ipD.setPartDot(Integer.parseInt(ss[IPDTE_PART_DOT]));
    }
    if (ss[IPDTE_APPL_DOT] != null && ss[IPDTE_APPL_DOT].length() > 0) {
      ipD.setApplDot(Integer.parseInt(ss[IPDTE_APPL_DOT]));
    }
    if (ss[IPDTE_EB_APPL30_DOT] != null && ss[IPDTE_EB_APPL30_DOT].length() > 0) {
      ipD.setEbAppl30Dot(Integer.parseInt(ss[IPDTE_EB_APPL30_DOT]));
    }
    if (ss[IPDTE_EB_PART30_DOT] != null && ss[IPDTE_EB_PART30_DOT].length() > 0) {
      ipD.setEbPart30Dot(Integer.parseInt(ss[IPDTE_EB_PART30_DOT]));
    }
    if (ss[IPDTE_EB_APPL60_DOT] != null && ss[IPDTE_EB_APPL60_DOT].length() > 0) {
      ipD.setEbAppl60Dot(Integer.parseInt(ss[IPDTE_EB_APPL60_DOT]));
    }
    if (ss[IPDTE_EB_PART60_DOT] != null && ss[IPDTE_EB_PART60_DOT].length() > 0) {
      ipD.setEbPart60Dot(Integer.parseInt(ss[IPDTE_EB_PART60_DOT]));
    }
    if (ss[IPDTE_EB_APPL61_DOT] != null && ss[IPDTE_EB_APPL61_DOT].length() > 0) {
      ipD.setEbAppl61Dot(Integer.parseInt(ss[IPDTE_EB_APPL61_DOT]));
    }
    if (ss[IPDTE_EB_PART61_DOT] != null && ss[IPDTE_EB_PART61_DOT].length() > 0) {
      ipD.setEbPart61Dot(Integer.parseInt(ss[IPDTE_EB_PART61_DOT]));
    }
    if (ss[IPDTE_SB_APPL30_DOT] != null && ss[IPDTE_SB_APPL30_DOT].length() > 0) {
      ipD.setSbAppl30Dot(Integer.parseInt(ss[IPDTE_SB_APPL30_DOT]));
    }
    if (ss[IPDTE_SB_PART30_DOT] != null && ss[IPDTE_SB_PART30_DOT].length() > 0) {
      ipD.setSbPart30Dot(Integer.parseInt(ss[IPDTE_SB_PART30_DOT]));
    }
    if (ss[IPDTE_SB_APPL90_DOT] != null && ss[IPDTE_SB_APPL90_DOT].length() > 0) {
      ipD.setSbAppl90Dot(Integer.parseInt(ss[IPDTE_SB_APPL90_DOT]));
    }
    if (ss[IPDTE_SB_PART90_DOT] != null && ss[IPDTE_SB_PART90_DOT].length() > 0) {
      ipD.setSbPart90Dot(Integer.parseInt(ss[IPDTE_SB_PART90_DOT]));
    }
    if (ss[IPDTE_SB_APPL180_DOT] != null && ss[IPDTE_SB_APPL180_DOT].length() > 0) {
      ipD.setSbAppl180Dot(Integer.parseInt(ss[IPDTE_SB_APPL180_DOT]));
    }
    if (ss[IPDTE_SB_PART180_DOT] != null && ss[IPDTE_SB_PART180_DOT].length() > 0) {
      ipD.setSbPart180Dot(Integer.parseInt(ss[IPDTE_SB_PART180_DOT]));
    }
    if (ss[IPDTE_SB_APPL181_DOT] != null && ss[IPDTE_SB_APPL181_DOT].length() > 0) {
      ipD.setSbAppl181Dot(Integer.parseInt(ss[IPDTE_SB_APPL181_DOT]));
    }
    if (ss[IPDTE_SB_PART181_DOT] != null && ss[IPDTE_SB_PART181_DOT].length() > 0) {
      ipD.setSbPart181Dot(Integer.parseInt(ss[IPDTE_SB_PART181_DOT]));
    }
    ipD.setPartNo(ss[IPDTE_PART_CODE]);
    ipD.setRocId(ss[IPDTE_ROC_ID]);
    ipD.setPrsnId(ss[IPDTE_PRSN_ID]);
    ipD.setHospId(ss[IPDTE_HOSP_ID]); 
    ipD.setUpdateAt(new Date());
    
    ipdList.add(ipD);
    if (ipdList.size() > 500) {
      long start = System.currentTimeMillis();
      ipDDao.saveAll(ipdList);
      long usedTime = System.currentTimeMillis() - start;
      System.out.println("save 500:" + usedTime);
      ipdList.clear();
    }
  }

  public void processIPP(String s) {

  }

  /**
   * 取得總表ID
   * 
   * @param ym
   * @param isIP
   * @return
   */
  public Long getTID(String ym, boolean isIP) {
    if (existT.containsKey(ym)) {
      return existT.get(ym);
    }
    String feeYM = ym;
    if (ym.startsWith("2")) {
      feeYM = DateTool.convertToChineseYear(feeYM);
    }
    if (isIP) {
      List<IP_T> list = ipTDao.findByFeeYmOrderById(feeYM);
      if (list == null || list.size() == 0) {
        IP_T ipt = new IP_T();
        ipt.setFeeYm(feeYM);
        ipt.setUpdateAt(new Date());
        ipt = ipTDao.save(ipt);
        existT.put(ym, ipt.getId());
        return ipt.getId();
      } else {
        existT.put(ym, list.get(0).getId());
        return list.get(0).getId();
      }
    } else {
      List<OP_T> list = opTDao.findByFeeYmOrderById(feeYM);
      if (list == null || list.size() == 0) {
        OP_T opt = new OP_T();
        opt.setFeeYm(feeYM);
        opt.setUpdateAt(new Date());
        opt = opTDao.save(opt);
        existT.put(ym, opt.getId());
        return opt.getId();
      } else {
        existT.put(ym, list.get(0).getId());
        return list.get(0).getId();
      }
    }
  }

  /**
   * 取得指定年月的 OP_D 的 ID，key 為 SEQ_NO，value為 ID
   * 
   * @param ym
   */
  private void retrieveAllOP_DID(String ym) {
    long start = System.currentTimeMillis();
    OP_DKeyID = new HashMap<Integer, Long>();
    Long opTID = getTID(ym, false);
    List<Object[]> list = opDDao.findByOptIdSimple(opTID);
    if (list != null) {
      for (Object[] objects : list) {
        OP_DKeyID.put((Integer) objects[0], ((BigInteger) objects[1]).longValue());
      }
    }
    long usedTime = System.currentTimeMillis() - start;
    System.out.println("retrieveAllOP_DID: " + usedTime + "ms");
  }
}
