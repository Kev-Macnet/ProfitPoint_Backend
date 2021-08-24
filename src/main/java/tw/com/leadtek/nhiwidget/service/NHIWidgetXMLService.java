/**
 * Created on 2021/1/25.
 */
package tw.com.leadtek.nhiwidget.service;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import tw.com.leadtek.nhiwidget.constant.MR_STATUS;
import tw.com.leadtek.nhiwidget.constant.XMLConstant;
import tw.com.leadtek.nhiwidget.dao.CODE_TABLEDao;
import tw.com.leadtek.nhiwidget.dao.IP_DDao;
import tw.com.leadtek.nhiwidget.dao.IP_PDao;
import tw.com.leadtek.nhiwidget.dao.IP_TDao;
import tw.com.leadtek.nhiwidget.dao.MRDao;
import tw.com.leadtek.nhiwidget.dao.MR_CHECKEDDao;
import tw.com.leadtek.nhiwidget.dao.OP_DDao;
import tw.com.leadtek.nhiwidget.dao.OP_PDao;
import tw.com.leadtek.nhiwidget.dao.OP_TDao;
import tw.com.leadtek.nhiwidget.model.CodeBase;
import tw.com.leadtek.nhiwidget.model.JsonSuggestion;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_TABLE;
import tw.com.leadtek.nhiwidget.model.rdb.DHead;
import tw.com.leadtek.nhiwidget.model.rdb.IP;
import tw.com.leadtek.nhiwidget.model.rdb.IP_D;
import tw.com.leadtek.nhiwidget.model.rdb.IP_DData;
import tw.com.leadtek.nhiwidget.model.rdb.IP_P;
import tw.com.leadtek.nhiwidget.model.rdb.IP_T;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.model.rdb.OP;
import tw.com.leadtek.nhiwidget.model.rdb.OP_D;
import tw.com.leadtek.nhiwidget.model.rdb.OP_DData;
import tw.com.leadtek.nhiwidget.model.rdb.OP_P;
import tw.com.leadtek.nhiwidget.model.rdb.OP_T;
import tw.com.leadtek.nhiwidget.payload.MO;
import tw.com.leadtek.nhiwidget.payload.MRCount;
import tw.com.leadtek.nhiwidget.payload.MRCountResponse;
import tw.com.leadtek.nhiwidget.payload.MRDetail;
import tw.com.leadtek.nhiwidget.payload.MRResponse;
import tw.com.leadtek.nhiwidget.payload.MRStatusCount;
import tw.com.leadtek.nhiwidget.payload.QuickSearchResponse;
import tw.com.leadtek.nhiwidget.payload.SearchReq;
import tw.com.leadtek.tools.DateTool;
import tw.com.leadtek.tools.StringUtility;
import tw.com.leadtek.tools.Utility;

@Service
public class NHIWidgetXMLService {

  private Logger logger = LogManager.getLogger();
  
  private final String DOCTYPE = "<?xml version=\"1.0\" encoding=\"Big5\"?>\n";
  
  private final int IGNORE_STATUS = 100;
  
  private final int BATCH = 1000;
  
  /**
   * 針對姓名、證號是否隱碼
   */
  private final static boolean ISMASK = true;

  @Autowired
  private OP_PDao oppDao;

  @Autowired
  private OP_DDao opdDao;

  @Autowired
  private OP_TDao optDao;

  @Autowired
  private IP_TDao iptDao;

  @Autowired
  private IP_DDao ipdDao;

  @Autowired
  private IP_PDao ippDao;

  @Autowired
  private CODE_TABLEDao ctDao;

  @Autowired
  private MRDao mrDao;

  @Autowired
  private RedisService redis;

  @Autowired
  private CodeTableService cts;

  @Autowired
  private MR_CHECKEDDao mrCheckedDao;

  @Autowired
  private ParametersService parameters;

  @Autowired
  private LogDataService logService;

  public void saveOP(OP op) {
    OP_T opt = saveOPT(op.getTdata());
//    Map<String, Object> condition1 =
//        logService.makeCondition(new String[][] {{"ID", Long.toString(opt.getId())}});
//    Map<String, Object> row1 = logService.findOne("OP_T", condition1);
//    logService.updateModification("system", "OP_T", condition1, new HashMap<String, Object>(), row1);
    List<HashMap<String, Object>> opdList = getOPDByOPTID(opt.getId());
    List<HashMap<String, Object>> oppList = getOPPByOPTID(opt.getId());
    List<OP_DData> dDataList = op.getDdata();
    List<OP_P> oppBatch = new ArrayList<OP_P>();
    long timeAll = System.currentTimeMillis();
    long timeAllMR = 0;
    int count = 0;
    for (OP_DData op_dData : dDataList) {
      count ++;
      if (count > 1000) {
        break;
      }
      OP_D opd = op_dData.getDbody();
      maskOPD(opd);
      List<OP_P> oppListXML = opd.getPdataList();
      opd.setCaseType(op_dData.getDhead().getCASE_TYPE());
      opd.setSeqNo(op_dData.getDhead().getSEQ_NO());
      opd.setOptId(opt.getId());
      
      updateOPDID(opdList, opd);

      long timeMR = System.currentTimeMillis();
      MR mr = new MR(opd);
      if (opd.getMrId() != null) {
        mr.setId(opd.getMrId());
      }
      mr.setStatus(MR_STATUS.NO_CHANGE.value());
      mr = mrDao.save(mr);
      timeMR = System.currentTimeMillis() - timeMR;
      timeAllMR += timeMR;
      //System.out.println("timeMR=" + timeMR + " ms");
      long timeOpd = System.currentTimeMillis();
      CODE_TABLE ct = cts.getCodeTable("INFECTIOUS", opd.getIcdCm1());
      mr.setInfectious((ct == null) ? 0 : 1);
      opd.setMrId(mr.getId());
      opd = opdDao.save(opd);
     // timeOpd = System.currentTimeMillis() - timeOpd;
     // System.out.println("timeOpd=" + timeOpd + " ms");
      mr.setdId(opd.getId());
      mrDao.updateDid(opd.getId(), mr.getId());
//      Map<String, Object> condition2 =
//          logService.makeCondition(new String[][] {{"ID", Long.toString(opd.getId())}});
//      Map<String, Object> row2 = logService.findOne("OP_D", condition2);
//      logService.updateModification("system", "OP_D", condition2, new HashMap<String, Object>(),
//          row2);l
      for (OP_P opp : oppListXML) {
        opp.setOpdId(opd.getId());
        updateOPPID(oppList, opp);
        opp.setMrId(mr.getId());
        //oppBatch.add(opp);
        if (oppBatch.size() > BATCH) {
         // long timeOpp = System.currentTimeMillis();
         // oppDao.saveAll(oppBatch);
         // timeOpp = System.currentTimeMillis() - timeOpp;
         // System.out.println("save opp:" + timeOpp + " ms, " + oppBatch.size());
          oppBatch.clear();
        }
        //oppDao.save(opp);
//        Map<String, Object> condition3 =
//            logService.makeCondition(new String[][] {{"ID", Long.toString(opp.getId())}});
//        Map<String, Object> row3 = logService.findOne("OP_P", condition3);
//        logService.updateModification("system", "OP_P", condition3, new HashMap<String, Object>(),
//            row3);
      }
    }
    if (oppBatch.size() > 0) {
      //oppDao.saveAll(oppBatch);
    }
    timeAll = System.currentTimeMillis() - timeAll;
    count--;
    double avg = (double) timeAllMR / (double) count;
    System.out.println("timeAll:" + count + "," + timeAll + " ms" + ", mr:" + timeAllMR + ", avg:" + avg);
  }
  
  public void saveOPBatch(OP op) {
   int testMax = 10000000; 
    OP_T opt = saveOPT(op.getTdata());
    // 避免重複insert
    List<HashMap<String, Object>> opdList = getOPDByOPTID(opt.getId());
    List<HashMap<String, Object>> oppList = getOPPByOPTID(opt.getId());
    List<OP_DData> dDataList = op.getDdata();
    List<OP_P> oppBatch = new ArrayList<OP_P>();
    long timeAll = System.currentTimeMillis();
    int count = 0;
    if (dDataList == null) {
        System.err.println("dataList is null");
        return;
    }
    for (OP_DData op_dData : dDataList) {
      count ++;
      if (count > testMax) {
        break;
      }
      OP_D opd = op_dData.getDbody();
      maskOPD(opd);
      List<OP_P> oppListXML = opd.getPdataList();
      opd.setCaseType(op_dData.getDhead().getCASE_TYPE());
      opd.setSeqNo(op_dData.getDhead().getSEQ_NO());
      opd.setOptId(opt.getId());
      
      updateOPDID(opdList, opd);
      MR mr = new MR(opd);
      if (opd.getMrId() != null) {
        mr.setId(opd.getMrId());
      }
      mr.setStatus(MR_STATUS.NO_CHANGE.value());
      mr.setApplYm(opt.getFeeYm());
      //beforeSaveMRAndOPD(opt.getId(), mr, opd);
      CODE_TABLE ct = cts.getCodeTable("INFECTIOUS", opd.getIcdCm1());
      mr.setInfectious((ct == null) ? 0 : 1);
      mr = mrDao.save(mr);
      opd.setMrId(mr.getId());
      opd = opdDao.save(opd);
      mr.setdId(opd.getId());
      mrDao.updateDid(opd.getId(), mr.getId());
      for (OP_P opp : oppListXML) {
        opp.setOpdId(opd.getId());
        updateOPPID(oppList, opp);
        opp.setMrId(mr.getId());
        maskOPP(opp, opd.getCaseType());
        oppBatch.add(opp);
        if (oppBatch.size() > BATCH) {
          oppDao.saveAll(oppBatch);
          oppBatch.clear();
        }
      }
    }
    if (oppBatch.size() > 0) {
      oppDao.saveAll(oppBatch);
    }
    timeAll = System.currentTimeMillis() - timeAll;
    count--;
    double avg = (double) timeAll / (double) count;
    System.out.println("timeAll:" + count + "," + timeAll + " ms, avg:" + avg);
  }

  public void saveIP(IP ip) {
    IP_T ipt = saveIPT(ip.getTdata());
//    Map<String, Object> condition1 =
//        logService.makeCondition(new String[][] {{"ID", Long.toString(ipt.getId())}});
//    Map<String, Object> row1 = logService.findOne("IP_T", condition1);
//    logService.updateModification("system", "IP_T", condition1, new HashMap<String, Object>(),
//        row1);
    List<HashMap<String, Object>> ipdList = getIPDByIPTID(ipt.getId());
    List<HashMap<String, Object>> ippList = getIPPByIPTID(ipt.getId());
    
    if (ip.getDdata() == null) {
        System.err.println("dataList is null");
        return;
    }
    int count =0;
    long start =System.currentTimeMillis();
    //long saveIPD = 0;
    List<IP_P> ippBatch = new ArrayList<IP_P>();
    for (IP_DData ip_dData : ip.getDdata()) {
      count++;
      if (count >50) {
        break;
      }
      IP_D ipd = ip_dData.getDbody();
      maskIPD(ipd);
      List<IP_P> ippListXML = ipd.getPdataList();
      ipd.setCaseType(ip_dData.getDhead().getCASE_TYPE());
      ipd.setSeqNo(ip_dData.getDhead().getSEQ_NO());
      ipd.setIptId(ipt.getId());
      
      updateIPDID(ipdList, ipd);
      
      //long startMR =System.currentTimeMillis();
      MR mr = new MR(ipd);
      if (ipd.getMrId() != null) {
        mr.setId(ipd.getMrId());
      }
      mr.setStatus(MR_STATUS.NO_CHANGE.value());
      mr.setApplYm(ipt.getFeeYm());
      
      mr = mrDao.save(mr);
      //startMR = System.currentTimeMillis() - startMR;
     // System.out.println("save MR:" + startMR + " ms");
      CODE_TABLE ct = cts.getCodeTable("INFECTIOUS", ipd.getIcdCm1());
      mr.setInfectious((ct == null) ? 0 : 1);
      ipd.setMrId(mr.getId());
      
      //long startIPD = System.currentTimeMillis();
      ipd = ipdDao.save(ipd);
      //saveIPD += System.currentTimeMillis() - startIPD;
      //System.out.println("save IPD:" + startIPD + " ms");
//      Map<String, Object> condition2 =
//          logService.makeCondition(new String[][] {{"ID", Long.toString(ipd.getId())}});
//      Map<String, Object> row2 = logService.findOne("IP_D", condition2);
//      logService.updateModification("system", "IP_D", condition2, new HashMap<String, Object>(),
//          row2);
      mr.setdId(ipd.getId());
      mrDao.updateDid(ipd.getId(), mr.getId());
      
      //long startIPP = System.currentTimeMillis();
      for (IP_P ipp : ippListXML) {
        ipp.setIpdId(ipd.getId());
        updateIPPID(ippList, ipp);
        maskIPP(ipp, ipd.getCaseType());
        ipp.setMrId(mr.getId());
        ippBatch.add(ipp);
        if (ippBatch.size() > BATCH) {
          ippDao.saveAll(ippBatch);
          ippBatch.clear();
        }
//        Map<String, Object> condition3 =
//            logService.makeCondition(new String[][] {{"ID", Long.toString(ipp.getId())}});
//        Map<String, Object> row3 = logService.findOne("IP_P", condition3);
//        logService.updateModification("system", "IP_P", condition3, new HashMap<String, Object>(),
//            row3);
      }
    }
    if (ippBatch.size() > 0) {
      ippDao.saveAll(ippBatch);
    }
    count--;
    start = System.currentTimeMillis() - start;
    double avg = (double) start / (double) count;
     System.out.println("save " + count + " MR:" + start + "ms, avg=" + avg + "ms");
  }

  private OP_T saveOPT(OP_T opt) {
    opt.setUpdateAt(new java.util.Date());
    List<OP_T> list = optDao.findByFeeYmAndHospIdOrderById(opt.getFeeYm(), opt.getHospId());
    if (list == null || list.size() == 0) {
      return optDao.save(opt);
    } else {
      OP_T optDB = list.get(0);
      opt.setId(optDB.getId());
      return optDao.save(opt);
    }
  }
  
  private IP_T saveIPT(IP_T ipt) {
    ipt.setUpdateAt(new java.util.Date());
    List<IP_T> list = iptDao.findByFeeYmAndHospIdOrderById(ipt.getFeeYm(), ipt.getHospId());
    if (list == null || list.size() == 0) {
      return iptDao.save(ipt);
    } else {
      IP_T iptDB = list.get(0);
      ipt.setId(iptDB.getId());
      return iptDao.save(ipt);
    }
  }
  
  private void maskOPD(OP_D opd) {
    if (ISMASK) {
      opd.setRocId(StringUtility.maskString(opd.getRocId(), StringUtility.MASK_MOBILE));
      opd.setName(StringUtility.maskString(opd.getName(), StringUtility.MASK_NAME));
      opd.setPharId(StringUtility.maskString(opd.getPharId(), StringUtility.MASK_MOBILE));
      opd.setPrsnId(StringUtility.maskString(opd.getPrsnId(), StringUtility.MASK_MOBILE));
    }
    if (opd.getCareMark() != null) {
      String careMark = opd.getCareMark().trim();
      if (careMark.length() == 0) {
        opd.setCareMark(null);
      }
    }
    if (opd.getCasePayCode() != null) {
      String casePayCode = opd.getCasePayCode().trim();
      if (casePayCode.length() == 0) {
        opd.setCasePayCode(null);
      }
    }
    if (opd.getShareMark() != null) {
      String shareMark = opd.getShareMark().trim();
      if (shareMark.length() == 0) {
        opd.setShareMark(null);
      }
    }
    if (" ".equals(opd.getCureItemNo1())) {
      opd.setCureItemNo1(null);
    }
    if (" ".equals(opd.getCureItemNo2())) {
      opd.setCureItemNo2(null);
    }
    if (" ".equals(opd.getCureItemNo3())) {
      opd.setCureItemNo3(null);
    }
    if (" ".equals(opd.getCureItemNo4())) {
      opd.setCureItemNo4(null);
    }
    opd.setIcdCm1(StringUtility.formatICDtoUpperCase(opd.getIcdCm1()));
    opd.setIcdCm2(StringUtility.formatICDtoUpperCase(opd.getIcdCm2()));
    opd.setIcdCm3(StringUtility.formatICDtoUpperCase(opd.getIcdCm3()));
    opd.setIcdCm4(StringUtility.formatICDtoUpperCase(opd.getIcdCm4()));
    opd.setIcdCm5(StringUtility.formatICDtoUpperCase(opd.getIcdCm5()));
  }
  
  private void maskOPP(OP_P opp, String caseType) {
    if (ISMASK) {
      opp.setPrsnId(StringUtility.maskString(opp.getPrsnId(), StringUtility.MASK_MOBILE));
    }
    if ("B6".equals(caseType)) {
      opp.setPayBy(3);
    } else {
      opp.setPayBy(1);
    }
    opp.setApplStatus(1);
  }
  
  private void maskIPP(IP_P ipp, String caseType) {
    if (ISMASK) {
      ipp.setPrsnId(StringUtility.maskString(ipp.getPrsnId(), StringUtility.MASK_MOBILE));
    }
    if (caseType != null && caseType.startsWith("A")) {
      ipp.setPayBy(3);
    } else {
      ipp.setPayBy(1);
    }
    ipp.setApplStatus(1);
  }
  
  private void maskIPD(IP_D ipd) {
    if (ISMASK) {
      ipd.setRocId(StringUtility.maskString(ipd.getRocId(), StringUtility.MASK_MOBILE));
      ipd.setName(StringUtility.maskString(ipd.getName(), StringUtility.MASK_NAME));
      ipd.setPrsnId(StringUtility.maskString(ipd.getPrsnId(), StringUtility.MASK_MOBILE));
    }
    ipd.setIcdCm1(StringUtility.formatICDtoUpperCase(ipd.getIcdCm1()));
    ipd.setIcdCm2(StringUtility.formatICDtoUpperCase(ipd.getIcdCm2()));
    ipd.setIcdCm3(StringUtility.formatICDtoUpperCase(ipd.getIcdCm3()));
    ipd.setIcdCm4(StringUtility.formatICDtoUpperCase(ipd.getIcdCm4()));
    ipd.setIcdCm5(StringUtility.formatICDtoUpperCase(ipd.getIcdCm5()));
    ipd.setIcdCm6(StringUtility.formatICDtoUpperCase(ipd.getIcdCm6()));
    ipd.setIcdCm7(StringUtility.formatICDtoUpperCase(ipd.getIcdCm7()));
    ipd.setIcdCm8(StringUtility.formatICDtoUpperCase(ipd.getIcdCm8()));
    ipd.setIcdCm9(StringUtility.formatICDtoUpperCase(ipd.getIcdCm9()));
    ipd.setIcdCm10(StringUtility.formatICDtoUpperCase(ipd.getIcdCm10()));
    ipd.setIcdCm11(StringUtility.formatICDtoUpperCase(ipd.getIcdCm11()));
    ipd.setIcdCm12(StringUtility.formatICDtoUpperCase(ipd.getIcdCm12()));
    ipd.setIcdCm13(StringUtility.formatICDtoUpperCase(ipd.getIcdCm13()));
    ipd.setIcdCm14(StringUtility.formatICDtoUpperCase(ipd.getIcdCm14()));
    ipd.setIcdCm15(StringUtility.formatICDtoUpperCase(ipd.getIcdCm15()));
    ipd.setIcdCm16(StringUtility.formatICDtoUpperCase(ipd.getIcdCm16()));
    ipd.setIcdCm17(StringUtility.formatICDtoUpperCase(ipd.getIcdCm17()));
    ipd.setIcdCm18(StringUtility.formatICDtoUpperCase(ipd.getIcdCm18()));
    ipd.setIcdCm19(StringUtility.formatICDtoUpperCase(ipd.getIcdCm19()));
    ipd.setIcdCm20(StringUtility.formatICDtoUpperCase(ipd.getIcdCm20()));
  }
  
  /**
   * 取得DB中相同OPT_ID的 OP_D，減少存取DB次數.
   * @param optId
   * @return
   */
  private List<HashMap<String, Object>> getOPDByOPTID(long optId) {
    long start = System.currentTimeMillis();
    List<Object[]> list = opdDao.findByOptIdSimple(optId);
    List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
    for (Object[] obj : list) {
      HashMap<String, Object> map = new HashMap<String, Object>();
      map.put("seqNo", (Integer) obj[0]);
      map.put("id", (BigInteger) obj[1]);
      map.put("rocId", (String) obj[2]);
      map.put("funcDate", (String) obj[3]);
      map.put("mrId", (BigInteger) obj[4]);
      result.add(map);
    }
    start = System.currentTimeMillis() - start;
    logger.info("getOPDByOPTID used:" + start + "ms");
    return result;
  }
  
  private List<HashMap<String, Object>> getOPPByOPTID(long optId) {
    long start = System.currentTimeMillis();
    List<Object[]> list = oppDao.findByOptId(optId);
    List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
    for (Object[] obj : list) {
      HashMap<String, Object> map = new HashMap<String, Object>();
      map.put("id", (BigInteger) obj[0]);
      map.put("opdId", (BigInteger) obj[1]);
      map.put("orderSeqNo", (Integer) obj[2]);
      result.add(map);
    }
    start = System.currentTimeMillis() - start;
    logger.info("getOPPByOPTID used:" + start + "ms");
    return result;
  }
  
  /**
   * 取得DB中相同IPT_ID的 IP_D，減少存取DB次數.
   * @param optId
   * @return
   */
  private List<HashMap<String, Object>> getIPDByIPTID(long iptId) {
    long start = System.currentTimeMillis();
    List<Object[]> list = ipdDao.findByIptIdSimple(iptId);
    List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
    for (Object[] obj : list) {
      HashMap<String, Object> map = new HashMap<String, Object>();
      map.put("seqNo", (Integer) obj[0]);
      map.put("id", (BigInteger) obj[1]);
      map.put("rocId", (String) obj[2]);
      map.put("inDate", (String) obj[3]);
      map.put("mrId", (BigInteger) obj[4]);
      result.add(map);
    }
    start = System.currentTimeMillis() - start;
    logger.info("getIPDByIPTID used:" + start + "ms");
    return result;
  }
  
  private List<HashMap<String, Object>> getIPPByIPTID(long iptId) {
    long start = System.currentTimeMillis();
    List<Object[]> list = ippDao.findByIptId(iptId);
    List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
    for (Object[] obj : list) {
      HashMap<String, Object> map = new HashMap<String, Object>();
      map.put("id", (BigInteger) obj[0]);
      map.put("ipdId", (BigInteger) obj[1]);
      map.put("orderSeqNo", (Integer) obj[2]);
      result.add(map);
    }
    start = System.currentTimeMillis() - start;
    logger.info("getIPPByIPTID used:" + start + "ms");
    return result;
  }
  
  private void updateOPDID(List<HashMap<String, Object>> list, OP_D opd) {
    opd.setUpdateAt(new java.util.Date());
    int index = -1;
    for (int i=list.size() -1; i>=0; i--) {
      HashMap<String, Object> map = list.get(i);
      if (((Integer) map.get("seqNo")).intValue() == opd.getSeqNo().intValue() 
          && ((String) map.get("rocId")).equals(opd.getRocId()) 
          && ((String) map.get("funcDate")).equals(opd.getFuncDate())) {
        index = i;
        opd.setId(((BigInteger) map.get("id")).longValue());
        opd.setMrId(((BigInteger) map.get("mrId")).longValue());
        break;
      }
    }
    if (index > -1) {
      list.remove(index);
    }
  }
  
  private void updateOPPID(List<HashMap<String, Object>> list, OP_P opp) {
    opp.setUpdateAt(new java.util.Date());
    int index = -1;
    for (int i=list.size() -1; i>=0; i--) {
      HashMap<String, Object> map = list.get(i);
      if (((BigInteger) map.get("opdId")).longValue() == opp.getOpdId().longValue()
          && ((Integer) map.get("orderSeqNo")).intValue() == opp.getOrderSeqNo().intValue()) {
        index = i;
        opp.setId(((BigInteger) map.get("id")).longValue());
        break;
      }
    }
    if (index > -1) {
      list.remove(index);
    }
  }

  private void updateIPDID(List<HashMap<String, Object>> list, IP_D ipd) {
    ipd.setUpdateAt(new java.util.Date());
    int index = -1;
    for (int i=list.size() -1; i>=0; i--) {
      HashMap<String, Object> map = list.get(i);
      if (((Integer) map.get("seqNo")).intValue() == ipd.getSeqNo().intValue() 
          && ((String) map.get("rocId")).equals(ipd.getRocId()) 
          && ((String) map.get("inDate")).equals(ipd.getInDate())) {
        index = i;
        ipd.setId(((BigInteger) map.get("id")).longValue());
        ipd.setMrId(((BigInteger) map.get("mrId")).longValue());
      }
    }
    if (index > -1) {
      list.remove(index);
    }
  }
  
  private void updateIPPID(List<HashMap<String, Object>> list, IP_P ipp) {
    ipp.setUpdateAt(new java.util.Date());
    int index = -1;
    for (int i=list.size() -1; i>=0; i--) {
      HashMap<String, Object> map = list.get(i);
      if (((BigInteger) map.get("ipdId")).longValue() == ipp.getIpdId().longValue()
          && ((Integer) map.get("orderSeqNo")).intValue() == ipp.getOrderSeqNo().intValue()) {
        index = i;
        ipp.setId(((BigInteger) map.get("id")).longValue());
      }
    }
    if (index > -1) {
      list.remove(index);
    }
  }

  /**
   * 下載表單資料為 Excel 檔案.
   * 
   * @param name
   * @param outputStream
   * @return
   * @throws IOException
   */
  public int getNHIXMLFile(String ym, String dataFormat, OutputStream outputStream)
      throws IOException {

    XmlMapper xmlMapper = new XmlMapper();
    // 若值為 null 不會輸出到 String
    xmlMapper.setSerializationInclusion(Include.NON_NULL);
    Object data = null;
    if ("IP".equals(dataFormat)) {
      data = getIP(ym);
    } else if ("OP".equals(dataFormat)) {
      data = getOP(ym);
    }
    // xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
    String xml = xmlMapper.writeValueAsString(data);

    try {
      outputStream.write(DOCTYPE.getBytes());
      outputStream.write(xml.getBytes(Charset.forName("BIG5")));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return -1;

  }

  /**
   * 下載表單資料為 Excel 檔案.
   * 
   * @param name
   * @param outputStream
   * @return
   * @throws IOException
   */
  public int getNHIXMLFileBetween(String sdate, String edate, String dataFormat,
      OutputStream outputStream) throws IOException {

    XmlMapper xmlMapper = new XmlMapper();
    // 若值為 null 不會輸出到 String
    xmlMapper.setSerializationInclusion(Include.NON_NULL);
    xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
    Object data = null;
    int result = 0;
    if ("20".equals(dataFormat)) {
      data = getIP(sdate, edate);
      if (data != null) {
        result = ((IP) data).getDdata().size();
      }
    } else if ("10".equals(dataFormat)) {
      data = getOP(sdate, edate);
      if (data != null) {
        result = ((OP) data).getDdata().size();
      }
    } // xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
    String xml = xmlMapper.writeValueAsString(data);
    // System.out.println("xml=" + xml);

    try {
      outputStream.write(DOCTYPE.getBytes());
      outputStream.write(xml.getBytes(Charset.forName("BIG5")));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return result;
  }

  /**
   * 取得住院申報資料
   * 
   * @param ym
   * @return
   */
  private IP getIP(String ym) {
    IP result = new IP();
    List<IP_T> iptList = iptDao.findByFeeYmOrderById(ym);
    if (iptList != null && iptList.size() > 0) {
      IP_T ipt = iptList.get(0);
      result.setTdata(ipt);
      List<IP_DData> ip_DDataList = new ArrayList<IP_DData>();
      result.setDdata(ip_DDataList);
      List<IP_D> ipdList = ipdDao.findByIptId(ipt.getId());
      for (IP_D ip_D : ipdList) {
        IP_DData ip_Ddata = new IP_DData();
        DHead dHead = new DHead();
        dHead.setCASE_TYPE(ip_D.getCaseType());
        dHead.setSEQ_NO(ip_D.getSeqNo());
        ip_Ddata.setDhead(dHead);
        ip_Ddata.setDbody(ip_D);
        ip_DDataList.add(ip_Ddata);

        ip_D.setPdataList(ippDao.findByIpdId(ip_D.getId()));
      }
    }
    return result;
  }

  /**
   * 取得門診申報資料
   * 
   * @param ym
   * @return
   */
  private OP getOP(String ym) {
    OP result = new OP();
    List<OP_T> optList = optDao.findByFeeYmOrderById(ym);
    if (optList != null && optList.size() > 0) {
      OP_T opt = optList.get(0);
      result.setTdata(opt);
      List<OP_DData> op_DDataList = new ArrayList<OP_DData>();
      result.setDdata(op_DDataList);
      List<OP_D> opdList = opdDao.findByOptId(opt.getId());
      for (OP_D op_D : opdList) {
        OP_DData op_Ddata = new OP_DData();
        DHead dHead = new DHead();
        dHead.setCASE_TYPE(op_D.getCaseType());
        dHead.setSEQ_NO(op_D.getSeqNo());
        op_Ddata.setDhead(dHead);
        op_Ddata.setDbody(op_D);
        op_DDataList.add(op_Ddata);

        op_D.setPdataList(oppDao.findByOpdIdOrderByOrderSeqNo(op_D.getId()));
      }
    }
    return result;
  }

  /**
   * 取得住院申報資料
   * 
   * @param ym
   * @return
   */
  private IP getIP(String sdate, String edate) {
    IP result = new IP();
    IP_T ipt = new IP_T();
    result.setTdata(ipt);
    List<IP_DData> ip_DDataList = new ArrayList<IP_DData>();
    result.setDdata(ip_DDataList);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    try {
      Date sDate = (sdate == null) ? null : new Date(sdf.parse(sdate).getTime());
      Date eDate = (edate == null) ? null : new Date(sdf.parse(edate).getTime());

      List<IP_D> ipdList = ipdDao.findByIDFromMR("10", sDate, eDate);
      if (ipdList != null && ipdList.size() > 0) {
        List<IP_P> ippList = ippDao.findByIpdIDFromMR("10", sDate, eDate);

        for (IP_D ip_D : ipdList) {
          IP_DData ip_Ddata = new IP_DData();
          DHead dHead = new DHead();
          dHead.setCASE_TYPE(ip_D.getCaseType());
          dHead.setSEQ_NO(ip_D.getSeqNo());
          ip_Ddata.setDhead(dHead);
          ip_Ddata.setDbody(ip_D);
          ip_DDataList.add(ip_Ddata);
          ip_D.setPdataList(searchIppByIpdId(ippList, ip_D.getId()));
          ip_DDataList.add(ip_Ddata);
        }
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return result;
  }

  /**
   * 取得門診申報資料
   * 
   * @param ym
   * @return
   */
  private OP getOP(String sdate, String edate) {
    OP result = new OP();
    OP_T opt = new OP_T();
    result.setTdata(opt);
    List<OP_DData> op_DDataList = new ArrayList<OP_DData>();
    result.setDdata(op_DDataList);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    try {
      Date sDate = (sdate == null) ? null : new Date(sdf.parse(sdate).getTime());
      Date eDate = (edate == null) ? null : new Date(sdf.parse(edate).getTime());

      List<OP_D> opdList = opdDao.findByIDFromMR("10", sDate, eDate);
      if (opdList != null && opdList.size() > 0) {
        List<OP_P> oppList = oppDao.findByOpdIDFromMR("10", sDate, eDate);

        for (OP_D op_D : opdList) {
          OP_DData op_Ddata = new OP_DData();
          DHead dHead = new DHead();
          dHead.setCASE_TYPE(op_D.getCaseType());
          dHead.setSEQ_NO(op_D.getSeqNo());
          op_Ddata.setDhead(dHead);
          op_Ddata.setDbody(op_D);
          op_D.setPdataList(searchOppByOpdId(oppList, op_D.getId()));
          op_DDataList.add(op_Ddata);
        }
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return result;
  }

  private List<OP_P> searchOppByOpdId(List<OP_P> oppList, Long opdId) {
    List<OP_P> result = new ArrayList<OP_P>();
    for (int i = oppList.size() - 1; i >= 0; i--) {
      OP_P opP = oppList.get(i);
      if (opP.getOpdId().longValue() == opdId.longValue()) {
        result.add(oppList.remove(i));
      }
    }
    return result;
  }

  private List<IP_P> searchIppByIpdId(List<IP_P> ippList, Long ipdId) {
    List<IP_P> result = new ArrayList<IP_P>();
    for (int i = ippList.size() - 1; i >= 0; i--) {
      IP_P ipP = ippList.get(i);
      if (ipP.getIpdId().longValue() == ipdId.longValue()) {
        result.add(ippList.remove(i));
      }
    }
    return result;
  }

  public List<CODE_TABLE> getCodeTable(String cat) {
    List<CODE_TABLE> list = "ALL".equals(cat) ? ctDao.findAll() : ctDao.findByCat(cat);
    if (list == null || list.size() == 0) {
      return new ArrayList<CODE_TABLE>();
    }
    filterCat(list, cat);
    return list;
  }

  private void filterCat(List<CODE_TABLE> list, String cat) {
    for (CODE_TABLE code_TABLE : list) {
      if (code_TABLE.getRemark() != null && code_TABLE.getRemark().length() == 0) {
        code_TABLE.setRemark(null);
      }
    }
    if ("ALL".equals(cat)) {
      return;
    }
    for (CODE_TABLE code_TABLE : list) {
      code_TABLE.setCat(null);
    }
  }

  public String search(SearchReq req) {
    List<IP_D> ls = null;
    if (req.getAll() != null) {
      ls = fullSearchIP_D(null, PageRequest.of(0, 10), req.getAll());
    } else {
      IP_D ipd = new IP_D();
      if (req.getCaseType() != null) {
        ipd.setCaseType(req.getCaseType());
      }
      if (req.getApplyPoints() != null) {
        ipd.setMedDot(req.getApplyPoints().intValue());
      }
      if (req.getDrugFee() != null) {
        ipd.setDrugDot(req.getDrugFee().intValue());
      }
      if (req.getFuncDate() != null) {
        ipd.setApplStartDate(req.getFuncDate());
      }
      if (req.getFuncType() != null) {
        ipd.setFuncType(req.getFuncType());
      }
      if (req.getIcdCM() != null) {
        ipd.setIcdCm1(req.getIcdCM());
      }
      if (req.getIcdOP() != null) {
        ipd.setIcdOpCode1(req.getIcdOP());
      }
      if (req.getName() != null) {
        ipd.setName(req.getName());
      }
      if (req.getPrsnID() != null) {
        ipd.setPrsnId(req.getPrsnID());
      }
      if (req.getRocID() != null) {
        ipd.setRocId(req.getRocID());
      }
      if (req.getYm() != null) {
        ipd.setApplStartDate(req.getYm());
      }
      // in
      // List<String> strList=new ArrayList<>();
      // strList.add("20");
      // strList.add("24");
      // predicatesList.add(exp.in(strList));

      ExampleMatcher matcher = ExampleMatcher.matching() // 構建物件
          .withStringMatcher(StringMatcher.CONTAINING) // 改變預設字串匹配方式：模糊查詢
          .withIgnoreCase(true) // 改變預設大小寫忽略方式：忽略大小寫
          .withMatcher("APPL_START_DATE", GenericPropertyMatchers.startsWith());
      // .withIgnorePaths("focus"); // 忽略屬性：是否關注。因為是基本型別，需要忽略掉

      Example<IP_D> ex = Example.of(ipd, matcher);

      // 查詢
      ls = ipdDao.findAll(ex);
    }

    ObjectMapper objectMapper = new ObjectMapper();
    // 若值為 null 不會輸出到 String
    objectMapper.setSerializationInclusion(Include.NON_NULL);
    try {
      String json = objectMapper.writeValueAsString(ls);
      return json;
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    if (req.getOrder() != null) {
      // 醫令代碼
    }
    return "";
  }

  private List<IP_D> fullSearchIP_D(IP_D ipd, PageRequest page, String q) {
    // PageRequest pageRequest = new PageRequest(0,10);
    Page<IP_D> pages = ipdDao.findAll(new Specification<IP_D>() {

      public Predicate toPredicate(Root<IP_D> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicate = new ArrayList<Predicate>();

        // 1.混合條件查詢
        Path<String> pathPART_CODE = root.get("partNo");
        Path<String> pathFUNC_TYPE = root.get("funcType");
        Path<String> pathPRSN_ID = root.get("prsnId");
        Path<String> pathTW_DRG_CODE = root.get("twDrgCode");
        predicate.add(cb.or(cb.like(root.get("rocId"), q + "%"), cb.like(pathPART_CODE, q + "%"),
            cb.like(pathFUNC_TYPE, q + "%"), cb.like(pathPRSN_ID, q + "%"),
            cb.like(pathTW_DRG_CODE, q + "%")));

        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));
        return query.getRestriction();
      }

    }, page);

    List<IP_D> result = new ArrayList<IP_D>();

    if (pages != null && pages.getSize() > 0) {
      for (IP_D ip_D : pages) {
        result.add(ip_D);
      }
    }
    return result;
  }

  private Map<String, Object> fullSearchMR(int page, int perPage, String all, Date sDate,
      Date eDate, int status) {
    // PageRequest pageRequest = new PageRequest(0,10);
    String[] ss = all.split(" ");
    Specification<MR> spec = new Specification<MR>() {

      private static final long serialVersionUID = 1L;

      public Predicate toPredicate(Root<MR> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicate = new ArrayList<Predicate>();

        // 1.混合條件查詢
        Path<String> pathInhMrId = root.get("inhMrId");
        Path<String> pathFuncType = root.get("funcType");
        Path<String> pathRocId = root.get("rocId");
        Path<String> pathPrsnId = root.get("prsnId");
        Path<String> pathApplId = root.get("applId");
        Path<String> pathRemark = root.get("remark");
        Path<String> pathInhClinicId = root.get("inhClinicId");
        Path<String> pathName = root.get("name");
        predicate.add(cb.between(root.get("mrDate"), sDate, eDate));
        for (int i = 0; i < ss.length; i++) {
          predicate.add(cb.or(cb.like(pathRocId, ss[i] + "%"), cb.like(pathInhMrId, ss[i] + "%"),
              cb.like(pathFuncType, ss[i] + "%"), cb.like(pathPrsnId, ss[i] + "%"),
              cb.like(pathApplId, ss[i] + "%"), cb.like(pathRemark, ss[i] + "%"),
              cb.like(pathInhClinicId, ss[i] + "%"), cb.like(pathName, ss[i] + "%")));
        }
        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));
        return query.getRestriction();
      }
    };
    int count = 0;
    int min = perPage * page + 1;
    int max = perPage * (page + 1);
    List<MR> pages = mrDao.findAll(spec);

    List<MRResponse> mrList = new ArrayList<MRResponse>();
    MRStatusCount mc = new MRStatusCount();
    if (pages != null && pages.size() > 0) {
      for (MR mrDb : pages) {
        if (mrDb.getStatus() == null) {
          mrDb.setStatus(MR_STATUS.NO_CHANGE.value());
        }
        if (status < IGNORE_STATUS && mrDb.getStatus().intValue() != status) {
          continue;
        }
        count++;
        if (count >= min && count <= max) {
          mrList.add(new MRResponse(mrDb, cts));
        }
        updateMRStatusCount(mrDb, mc);
      }
    }
    Map<String, Object> result = new HashMap<String, Object>();
    result.put("count", count);
    result.put("totalPage", Utility.getTotalPage(count, perPage));
    result.put("mr", mrList);
    result.put("mrStatus", mc);
    return result;
  }
  
  private void updateMRStatusCount(MR mr, MRStatusCount mc) {
    if (mr.getStatus() == null) {
      mr.setStatus(MR_STATUS.NO_CHANGE.value());
    }
    if (mr.getStatus() == MR_STATUS.CLASSIFIED.value()) {
      mc.setClassified(mc.getClassified() + 1);
    } else if (mr.getStatus() == MR_STATUS.DONT_CHANGE.value()) {
      mc.setDontChange(mc.getDontChange() + 1);
    } else if (mr.getStatus() == MR_STATUS.NO_CHANGE.value()) {
      mc.setNoChange(mc.getNoChange() + 1);
    } else if (mr.getStatus() == MR_STATUS.OPTIMIZED.value()) {
      mc.setOptimized(mc.getOptimized() + 1);
    } else if (mr.getStatus() == MR_STATUS.QUESTION_MARK.value()) {
      mc.setQuestionMark(mc.getQuestionMark() + 1);
    } else if (mr.getStatus() == MR_STATUS.WAIT_CONFIRM.value()) {
      mc.setWaitConfirm(mc.getWaitConfirm() + 1);
    } else if (mr.getStatus() == MR_STATUS.WAIT_PROCESS.value()) {
      mc.setWaitProcess(mc.getWaitProcess() + 1);
    }
  }

  public Map<String, Object> getMR(String allMatch, String startDate, String endDate, String applYM,
      Integer minPoints, Integer maxPoints, String dataFormat, String funcType, String prsnId,
      String prsnName, String applId, String applName, String inhMrId, String inhClinicId,
      String drg, String drgSection, String orderCode, String inhCode, String drugUse,
      String inhCodeDrugUse, String icdAll, String icdCMMajor, String icdCMSecondary, String icdPCS,
      String qrObject, String qrSdate, String qrEdate, String status, String deductedCode,
      String deductedOrder, String all, int perPage, int page) {

    Map<String, Object> result = new HashMap<String, Object>();

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    int mrStatus = status == null ? IGNORE_STATUS : Integer.parseInt(status);
    try {
      Date sDate = (startDate == null || startDate.length() == 0) ? null
          : new Date(sdf.parse(startDate).getTime());
      Date eDate = (endDate == null || endDate.length() == 0) ? null
          : new Date(sdf.parse(endDate).getTime());

      if (all != null) {
        result = fullSearchMR(page, perPage, all, sDate, eDate, mrStatus);
      } else {
        boolean isAnd = false;
        if (allMatch != null && allMatch.toUpperCase().equals("Y")) {
          isAnd = true;
        }
        // TODO 缺drg, drgSection, orderCode, drugUse, icdAll, icdCMMajor, icdCMSecondary, icdPCS
        // qrObject, qrSdate, qrEdate, deductedCode, deductedOrder, inhCode, inhCodeDrugUse
        result = getMR(page, perPage, isAnd, sDate, eDate, applYM, minPoints, maxPoints, dataFormat,
            funcType, prsnId, prsnName, applId, applName, inhMrId, inhClinicId, drg, drgSection,
            status, deductedCode);
      }
      return result;
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return new HashMap<String, Object>();
  }

  // List<MR>
  private Map<String, Object> getMR(int page, int perPage, boolean isAnd, Date sDate, Date eDate,
      String applYM, Integer minPoints, Integer maxPoints, String dataFormat, String funcType, String prsnId,
      String prsnName, String applId, String applName, String inhMrId, String inhClinicId,
      String drg, String drgSection, String status, String deductedCode) {

    Specification<MR> spec = new Specification<MR>() {

      private static final long serialVersionUID = 1L;

      public Predicate toPredicate(Root<MR> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicate = new ArrayList<Predicate>();
        predicate.add(cb.between(root.get("mrDate"), sDate, eDate));
        if (minPoints != null && maxPoints != null) {
          predicate.add(cb.between(root.get("totalDot"), minPoints, maxPoints));
        }
        addPredicate(root, predicate, cb, "dataFormat", dataFormat, false, false);
        if (!"00".equals(funcType)) {
          addPredicate(root, predicate, cb, "funcType", funcType, false, false);
        }
        addPredicate(root, predicate, cb, "prsnId", prsnId, false, false);
        addPredicate(root, predicate, cb, "prsnName", prsnId, false, false);
        addPredicate(root, predicate, cb, "applId", applId, false, false);
        addPredicate(root, predicate, cb, "applName", applId, false, false);
        addPredicate(root, predicate, cb, "inhMrId", inhMrId, false, false);
        addPredicate(root, predicate, cb, "inhClinicId", inhClinicId, false, false);
        addPredicate(root, predicate, cb, "drgCode", drg, false, false);
        addPredicate(root, predicate, cb, "drgSection", drgSection, false, false);
        addPredicate(root, predicate, cb, "status", status, false, true);
        addPredicate(root, predicate, cb, "applYm", applYM, true, false);
        if (deductedCode != null && deductedCode.length() > 0) {
          predicate.add(cb.greaterThan(root.get("deductedDot"), 0));
        }

        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));
        return query.getRestriction();
      }
    };
    long total = mrDao.count(spec);
    int iPerPage = perPage < 1 ? 50 : perPage;
    Page<MR> pages = mrDao.findAll(spec, PageRequest.of(page, iPerPage));

    ObjectMapper objectMapper = new ObjectMapper();
    // 若值為 null 不會輸出到 String
    // objectMapper.setSerializationInclusion(Include.NON_NULL);


    List<MRResponse> mrList = new ArrayList<MRResponse>();
    MRCount mc = new MRCount();
    if (pages != null && pages.getSize() > 0) {
      for (MR mrDb : pages) {
        updateMRStatusCount(mrDb, mc);
        MRResponse mrr = new MRResponse(mrDb, cts);
        mrList.add(mrr);
        try {
          String json = objectMapper.writeValueAsString(mrr);
        } catch (JsonProcessingException e) {
          e.printStackTrace();
        }
      }
    }
    Map<String, Object> result = new HashMap<String, Object>();
    result.put("count", (int) total);
    result.put("totalPage", Utility.getTotalPage((int) total, iPerPage));
    result.put("mr", mrList);
    result.put("mrStatus", mc);
    return result;
  }

  public Map<String, Object> dwSearch(String sdate, String edate, String dataFormat,
      String funcType, String prsnId, String prsnName, String applId, String applName,
      String status, Integer perPage, Integer page) {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    try {
      Date sDate = (sdate == null) ? null : new Date(sdf.parse(sdate).getTime());
      Date eDate = (edate == null) ? null : new Date(sdf.parse(edate).getTime());

      // TODO 缺drg, drgSection, orderCode, drugUse, icdAll, icdCMMajor, icdCMSecondary, icdPCS
      // qrObject, qrSdate, qrEdate, deductedCode, deductedOrder, inhCode, inhCodeDrugUse
      return getMR(page, perPage, false, sDate, eDate, null, null, null, dataFormat, funcType, prsnId,
          prsnName, applId, applName, null, null, null, null, null, null);

    } catch (ParseException e) {
      e.printStackTrace();
    }

    return new HashMap<String, Object>();
  }

  public Map<String, Object> quickSearch(int perPage, int page, String startDate, String endDate,
      Integer minPoints, Integer maxPoints, String dataFormat, String funcType, String orderCode,
      String inhCode, String icdCMMajor) {

    List<QuickSearchResponse> qsList = new ArrayList<QuickSearchResponse>();
    Map<String, Object> result = new HashMap<String, Object>();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    try {
      Date sDate = (startDate == null || startDate.length() == 0) ? null
          : new Date(sdf.parse(startDate).getTime());
      Date eDate = (endDate == null || endDate.length() == 0) ? null
          : new Date(sdf.parse(endDate).getTime());


      Map<String, Object> mrMap = getMR(page, perPage, false, sDate, eDate, null, minPoints, maxPoints,
          dataFormat, funcType, null, null, null, null, null, null, null, null, null, null);

      if (mrMap == null || mrMap.get("mr") == null) {
        return new HashMap<String, Object>();
      }
      List<Long> ids = new ArrayList<Long>();
      for (MR mr : (List<MR>) mrMap.get("mr")) {
        qsList.add(new QuickSearchResponse(mr, cts.getDesc("FUNC_TYPE", mr.getFuncType())));
        ids.add(mr.getId());
      }
      List<Map<String, Object>> reasons = mrCheckedDao.queryMRChecked(ids);
      for (Map<String, Object> map : reasons) {
        Long id = (Long) map.get("MRID");
        String reason = (String) map.get("REASON");
        String detail = (String) map.get("DETAIL");
        for (QuickSearchResponse qsr : qsList) {
          if (qsr.getId().longValue() == id.longValue()) {
            qsr.setDetail(detail);
            qsr.setReason(reason);
            break;
          }
        }
      }
      result.put("totalPage", mrMap.get("totalPage"));
      result.put("qs", qsList);
      return result;
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return result;
  }

  private void addPredicate(Root<MR> root, List<Predicate> predicate, CriteriaBuilder cb,
      String paramName, String params, boolean isAnd, boolean isInteger) {
    if (params == null || params.length() == 0) {
      return;
    }
    if (params.indexOf(',') < 0) {
      if (isInteger) {
        predicate.add(cb.equal(root.get(paramName), params));
      } else {
        predicate.add(cb.like(root.get(paramName), params + "%"));
      }
    } else {
      String[] ss = params.split(",");
      List<Predicate> predicates = new ArrayList<Predicate>();
      for (int i = 0; i < ss.length; i++) {
        if (isInteger) {
          predicates.add(cb.equal(root.get(paramName), ss[i]));
        } else {
          predicates.add(cb.like(root.get(paramName), ss[i] + "%"));
        }
      }
      Predicate[] pre = new Predicate[predicates.size()];
      if (isAnd) {
        predicate.add(cb.and(predicates.toArray(pre)));
      } else {
        predicate.add(cb.or(predicates.toArray(pre)));
      }
    }
  }

  public List<MR> getMRByMrDateBetween(String startDate, String endDate, Integer status) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    try {
      Date sDate = new Date(sdf.parse(startDate).getTime());
      Date eDate = new Date(sdf.parse(endDate).getTime());

      List<MR> list = null;
      if (status != null) {
        list = mrDao.findByStatusAndMrDateBetween(status, sDate, eDate);
      } else {
        list = mrDao.findByMrDateBetween(sDate, eDate);
      }
      return list;
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return new ArrayList<MR>();
  }

  public MRCountResponse getMRCount(String startDate, String endDate, String dataFormat,
      String funcType, String prsnId, String applId) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    MRCount ipMR = new MRCount();
    MRCount opMR = new MRCount();
    MRCount allMR = new MRCount();
    try {
      Date sDate = new Date(sdf.parse(startDate).getTime());
      Date eDate = new Date(sdf.parse(endDate).getTime());
      ipMR.setDayCount((int) ((eDate.getTime() - sDate.getTime()) / 86400000L) + 1);
      opMR.setDayCount(ipMR.getDayCount());
      allMR.setDayCount(ipMR.getDayCount());

      List<String> dataFormatList = new ArrayList<String>();
      if ("10".equals(dataFormat)) {
        // 門急診
        dataFormatList.add("10");
      } else if ("20".equals(dataFormat)) {
        // 住院
        dataFormatList.add("20");
      } else {
        dataFormatList.add("10");
        dataFormatList.add("20");
      }
      // 取得指定日期區間的病歷數, 申請病歷數及申請總點數.
      List<Map<String, Object>> list = null;
      // 取得指定日期區間的各個病歷確認狀態總數
      List<Map<String, Object>> statusCount = null;
      if (prsnId == null && applId == null) {
        if (funcType == null || "00".equals(funcType)) {
          processMRCount(mrDao.queryMRRecordCount(sDate, eDate, sDate, eDate), ipMR, opMR, allMR);
          if ("10".equals(dataFormat)) {
            // 門急診
            opMR.updateValues(list.get(0));
            statusCount = mrDao.queryMRStatusCount(dataFormat, sDate, eDate);
            opMR.updateMrStatusCount(statusCount);
          } else if ("20".equals(dataFormat)) {
            // 住院
            statusCount = mrDao.queryMRStatusCount(dataFormat, sDate, eDate);
            ipMR.updateMrStatusCount(statusCount);
          } else {
            // 門急診
            statusCount = mrDao.queryMRStatusCount(dataFormat, sDate, eDate);
            opMR.updateMrStatusCount(statusCount);
            // 住院
            statusCount = mrDao.queryMRStatusCount(dataFormat, sDate, eDate);
            ipMR.updateMrStatusCount(statusCount);
            allMR.setClassified(ipMR.getClassified().intValue() + opMR.getClassified().intValue());
            allMR.setWaitConfirm(
                ipMR.getWaitConfirm().intValue() + opMR.getWaitConfirm().intValue());
            allMR.setQuestionMark(
                ipMR.getQuestionMark().intValue() + opMR.getQuestionMark().intValue());
            allMR.setWaitProcess(
                ipMR.getWaitProcess().intValue() + opMR.getWaitProcess().intValue());
            allMR.setNoChange(ipMR.getNoChange().intValue() + opMR.getNoChange().intValue());
            allMR.setOptimized(ipMR.getOptimized().intValue() + opMR.getOptimized().intValue());
            allMR.setDontChange(ipMR.getDontChange().intValue() + opMR.getDontChange().intValue());
          }
        } else {
          processMRCount(
              mrDao.queryMRRecordCountByFuncType(sDate, eDate, funcType, sDate, eDate, funcType),
              ipMR, opMR, allMR);
          if ("10".equals(dataFormat)) {
            // 門急診
            statusCount = mrDao.queryMRStatusCount(dataFormat, sDate, eDate, funcType);
            opMR.updateMrStatusCount(statusCount);
          } else if ("20".equals(dataFormat)) {
            // 住院
            statusCount = mrDao.queryMRStatusCount(dataFormat, sDate, eDate, funcType);
            ipMR.updateMrStatusCount(statusCount);
          } else {
            // 門急診
            statusCount = mrDao.queryMRStatusCount("10", sDate, eDate, funcType);
            opMR.updateMrStatusCount(statusCount);
            // 住院
            statusCount = mrDao.queryMRStatusCount("20", sDate, eDate, funcType);
            ipMR.updateMrStatusCount(statusCount);
            allMR.setClassified(ipMR.getClassified().intValue() + opMR.getClassified().intValue());
            allMR.setWaitConfirm(
                ipMR.getWaitConfirm().intValue() + opMR.getWaitConfirm().intValue());
            allMR.setQuestionMark(
                ipMR.getQuestionMark().intValue() + opMR.getQuestionMark().intValue());
            allMR.setWaitProcess(
                ipMR.getWaitProcess().intValue() + opMR.getWaitProcess().intValue());
            allMR.setNoChange(ipMR.getNoChange().intValue() + opMR.getNoChange().intValue());
            allMR.setOptimized(ipMR.getOptimized().intValue() + opMR.getOptimized().intValue());
            allMR.setDontChange(ipMR.getDontChange().intValue() + opMR.getDontChange().intValue());
          }
        }
      } else {
        Map<String, Object> map = getMR(0, 1000000, false, sDate, eDate, null, null, null, dataFormat,
            funcType, prsnId, null, applId, null, null, null, null, null, null, null);
        if (map.get("mr") != null) {
          List<MR> mrList = (List<MR>) map.get("mr");
          for (MR mr : mrList) {
            MRCount mrCount = mr.getDataFormat().equals("10") ? opMR : ipMR;
            mrCount.setTotalMr(mrCount.getTotalMr().intValue() + 1);
            updateMRStatusCount(mr, mrCount);

            if (mr.getApplDot() != null && mr.getApplDot().intValue() > 0) {
              mrCount.setApplSum(mrCount.getApplSum() + 1);
              mrCount.setApplDot(mrCount.getApplDot().intValue() + mr.getApplDot().intValue());
            }
            if (mr.getDrgCode() != null && mr.getDrgCode().length() > 0) {
              mrCount.setDrg(mrCount.getDrg().intValue() + 1);
            }
          }
          allMR.setApplDot(ipMR.getApplDot().intValue() + opMR.getApplDot().intValue());
          allMR.setTotalMr(ipMR.getTotalMr().intValue() + opMR.getTotalMr().intValue());
          allMR.setClassified(ipMR.getClassified().intValue() + opMR.getClassified().intValue());
          allMR.setWaitConfirm(ipMR.getWaitConfirm().intValue() + opMR.getWaitConfirm().intValue());
          allMR.setQuestionMark(
              ipMR.getQuestionMark().intValue() + opMR.getQuestionMark().intValue());
          allMR.setWaitProcess(ipMR.getWaitProcess().intValue() + opMR.getWaitProcess().intValue());
          allMR.setNoChange(ipMR.getNoChange().intValue() + opMR.getNoChange().intValue());
          allMR.setOptimized(ipMR.getOptimized().intValue() + opMR.getOptimized().intValue());
          allMR.setDontChange(ipMR.getDontChange().intValue() + opMR.getDontChange().intValue());
        }
      }

    } catch (ParseException e) {
      e.printStackTrace();
    }
    MRCountResponse result = new MRCountResponse();
    result.setAll(allMR);
    result.setIp(ipMR);
    result.setOp(opMR);
    return result;
  }

  private void processMRCount(List<Map<String, Object>> list, MRCount ip, MRCount op, MRCount all) {
    // OP_MR, IP_MR, OP_DOT, IP_DOT
    if (list == null || list.size() < 1) {
      return;
    }
    Map<String, Object> values = list.get(0);
    int totalMR = 0;
    int totalDot = 0;
    if (values.get("OP_MR") != null) {
      op.setTotalMr(((BigInteger) values.get("OP_MR")).intValue());
      totalMR += op.getTotalMr().intValue();
    }
    if (values.get("IP_MR") != null) {
      ip.setTotalMr(((BigInteger) values.get("IP_MR")).intValue());
      totalMR += ip.getTotalMr().intValue();
    }
    if (values.get("OP_DOT") != null) {
      op.setApplDot(((Integer) values.get("OP_DOT")).intValue());
      totalDot += op.getApplDot().intValue();
    }
    if (values.get("IP_DOT") != null) {
      ip.setApplDot(((Integer) values.get("IP_DOT")).intValue());
      totalDot += ip.getApplDot().intValue();
    }
    all.setApplDot(totalDot);
    all.setTotalMr(totalMR);
  }

  @Deprecated
  public MRCount getMRCountByApplId(String startDate, String endDate, String applId) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    MRCount result = new MRCount();
    try {
      Date sDate = new Date(sdf.parse(startDate).getTime());
      Date eDate = new Date(sdf.parse(endDate).getTime());

      // 取得指定日期區間的各個病歷確認狀態總數
      List<Map<String, Object>> statusCount = null;
      if (applId == null) {
        statusCount = mrDao.queryMRStatusCount("10", sDate, eDate, null);
      } else {
        statusCount = mrDao.queryMRStatusCountByApplId(sDate, eDate, splitStringToList(applId));
      }
      result.updateMrStatusCount(statusCount);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return result;
  }

  public List<String> splitStringToList(String s) {
    List<String> result = new ArrayList<String>();
    if (s.indexOf(',') < 0) {
      result.add(s.trim());
      return result;
    }
    String[] ss = s.split(",");
    for (String string : ss) {
      result.add(string.trim());
    }
    return result;
  }

  public MRDetail getMRDetail(String id) {
    MRDetail result = null;
    MR mr = mrDao.findById(Long.parseLong(id)).orElse(null);
    if (mr != null) {
      result = new MRDetail(mr);
      if (XMLConstant.DATA_FORMAT_OP.equals(result.getDataFormat())) {
        OP_D opD = opdDao.getOne(result.getdId());
        result.setOPDData(opD, cts);

        List<OP_P> oppList = oppDao.findByOpdIdOrderByOrderSeqNo(opD.getId());
        List<MO> moList = new ArrayList<MO>();
        for (OP_P op_P : oppList) {
          MO mo = new MO();
          mo.setOPPData(op_P, cts);
          moList.add(mo);
        }
        result.setMos(moList);
      } else if (XMLConstant.DATA_FORMAT_IP.equals(result.getDataFormat())) {
        IP_D ipD = ipdDao.getOne(result.getdId());
        result.setIPDData(ipD, cts);
        
        List<IP_P> ippList = ippDao.findByIpdIdOrderByOrderSeqNo(ipD.getId());
        List<MO> moList = new ArrayList<MO>();
        for (IP_P ip_P : ippList) {
          MO mo = new MO();
          mo.setIPPData(ip_P, cts);
          moList.add(mo);
        }
        result.setMos(moList);
      }
    }
    return result;
  }

  public MRDetail updateMRDetail(MRDetail mrDetail) {
    MRDetail result = null;
    MR mr = mrDao.getOne(mrDetail.getId());
    if (mr == null) {
      return null;
    }
    mr.updateMR(mrDetail);
    if (XMLConstant.DATA_FORMAT_OP.equals(mrDetail.getDataFormat())) {
      OP_D opD = opdDao.getOne(mr.getdId());
      Map<String, Object> row1;
      if (opD.getId() == null) {
        row1 = new HashMap<String, Object>();
      } else {
        Map<String, Object> condition1 =
            logService.makeCondition(new String[][] {{"ID", Long.toString(opD.getId())}});
        row1 = logService.findOne("OP_D", condition1);
      }
      opD.setFuncType(mr.getFuncType());
      updateOPDByMrDetail(opD, mrDetail);
      opdDao.save(opD);
      Map<String, Object> condition2 =
          logService.makeCondition(new String[][] {{"ID", Long.toString(opD.getId())}});
      Map<String, Object> row2 = logService.findOne("OP_D", condition2);
      logService.updateModification("system", "OP_D", condition2, row1, row2);
      // List<OP_P> oppList = oppDao.findByOpdId(opD.getId());
      // List<MO> moList = new ArrayList<MO>();
      // for (OP_P op_P : oppList) {
      // MO mo = new MO();
      // mo.setOPPData(op_P, cts);
      // moList.add(mo);
      // }
      // result.setMos(moList);
    } else if (XMLConstant.DATA_FORMAT_IP.equals(mrDetail.getDataFormat())) {
      IP_D ipD = ipdDao.getOne(mr.getdId());
      Map<String, Object> row1;
      if (ipD.getId() == null) {
        row1 = new HashMap<String, Object>();
      } else {
        Map<String, Object> condition1 =
            logService.makeCondition(new String[][] {{"ID", Long.toString(ipD.getId())}});
        row1 = logService.findOne("IP_D", condition1);
      }
      ipD.setFuncType(mr.getFuncType());
      ipD.setRocId(mrDetail.getRocId());
      ipD.setName(mrDetail.getName());
      ipD.setPrsnId(mrDetail.getPrsnId());
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
      ipD.setInDate(DateTool.convertToChineseYear(sdf.format(mrDetail.getMrDate())));
      ipD.setApplDot(mrDetail.getApplDot());
      ipD.setNonApplDot(mrDetail.getOwnExpense());
      ipdDao.save(ipD);
      Map<String, Object> condition2 =
          logService.makeCondition(new String[][] {{"ID", Long.toString(ipD.getId())}});
      Map<String, Object> row2 = logService.findOne("IP_D", condition2);
      logService.updateModification("system", "IP_D", condition2, row1, row2);
    }
    System.out.println("new FuncType:" + mr.getFuncType());
    mr = mrDao.save(mr);
    result = new MRDetail(mr);
    System.out.println("after save new FuncType:" + result.getFuncType());
    return result;
  }

  private void updateOPDByMrDetail(OP_D opD, MRDetail mrDetail) {
    opD.setRocId(mrDetail.getRocId());
    opD.setName(mrDetail.getName());
    opD.setPrsnId(mrDetail.getPrsnId());
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    opD.setFuncDate(DateTool.convertToChineseYear(sdf.format(mrDetail.getMrDate())));
    opD.setTotalDot(mrDetail.getTotalDot());
    opD.setTotalApplDot(mrDetail.getApplDot());
    opD.setIdBirthYmd(mrDetail.getBirthday());
    opD.setPayType(removeDash(mrDetail.getPayType()));
    if (mrDetail.getIcdCM() != null) {
      if (mrDetail.getIcdCM().size() >= 1) {
        opD.setIcdCm1(mrDetail.getIcdCM().get(0).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 2) {
        opD.setIcdCm2(mrDetail.getIcdCM().get(1).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 3) {
        opD.setIcdCm3(mrDetail.getIcdCM().get(2).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 4) {
        opD.setIcdCm4(mrDetail.getIcdCM().get(3).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 5) {
        opD.setIcdCm5(mrDetail.getIcdCM().get(4).getCode());
      }
    }
    if (mrDetail.getCureItems() != null) {
      if (mrDetail.getCureItems().size() >= 1) {
        opD.setCureItemNo1(mrDetail.getCureItems().get(0).getCode());
      }
      if (mrDetail.getCureItems().size() >= 2) {
        opD.setCureItemNo1(mrDetail.getCureItems().get(1).getCode());
      }
      if (mrDetail.getCureItems().size() >= 3) {
        opD.setCureItemNo1(mrDetail.getCureItems().get(2).getCode());
      }
      if (mrDetail.getCureItems().size() >= 4) {
        opD.setCureItemNo1(mrDetail.getCureItems().get(3).getCode());
      }
    }
    if (mrDetail.getIcdOP() != null) {
      if (mrDetail.getIcdOP().size() >= 1) {
        opD.setIcdOpCode1(mrDetail.getIcdOP().get(0).getCode());
      }
      if (mrDetail.getCureItems().size() >= 2) {
        opD.setIcdOpCode2(mrDetail.getIcdOP().get(1).getCode());
      }
      if (mrDetail.getCureItems().size() >= 3) {
        opD.setIcdOpCode3(mrDetail.getIcdOP().get(2).getCode());
      }
    }
    opD.setApplCauseMark(mrDetail.getApplCauseMark());
  }

  public void initMRDetailValueByOpd(MRDetail detail, OP_D opD) {
    detail.setBirthday(opD.getIdBirthYmd());
    detail.setPayType(opD.getPayType());
    List<CodeBase> cureItems = new ArrayList<CodeBase>();
    addCodeBaseByCode(cureItems, "ICD10-PCS", opD.getCureItemNo1());
    addCodeBaseByCode(cureItems, "ICD10-PCS", opD.getCureItemNo2());
    addCodeBaseByCode(cureItems, "ICD10-PCS", opD.getCureItemNo3());
    addCodeBaseByCode(cureItems, "ICD10-PCS", opD.getCureItemNo4());
    if (cureItems.size() > 0) {
      detail.setCureItems(cureItems);
    }

    List<CodeBase> icdCM = new ArrayList<CodeBase>();
    addCodeBaseByCode(icdCM, "ICD10-CM", opD.getIcdCm1());
    addCodeBaseByCode(icdCM, "ICD10-CM", opD.getIcdCm2());
    addCodeBaseByCode(icdCM, "ICD10-CM", opD.getIcdCm3());
    addCodeBaseByCode(icdCM, "ICD10-CM", opD.getIcdCm4());
    addCodeBaseByCode(icdCM, "ICD10-CM", opD.getIcdCm5());
    if (icdCM.size() > 0) {
      detail.setIcdCM(icdCM);
    }

    List<CodeBase> icdOp = new ArrayList<CodeBase>();
    addCodeBaseByCode(icdCM, "ICD10-PCS", opD.getIcdOpCode1());
    addCodeBaseByCode(icdCM, "ICD10-PCS", opD.getIcdOpCode2());
    addCodeBaseByCode(icdCM, "ICD10-PCS", opD.getIcdOpCode3());
    if (icdOp.size() > 0) {
      detail.setIcdOP(icdOp);
    }

    detail.setApplCauseMark(opD.getApplCauseMark());
  }

  private void addCodeBaseByCode(List<CodeBase> list, String category, String code) {
    if (code == null || code.length() == 0) {
      return;
    }
    String codes = "ICD10-CM".equals(category) ?  StringUtility.formatICD(code) : code;
    System.out.println("addCodeBaseByCode:" + codes);
    List<JsonSuggestion> query = redis.query(category, codes);
    for (JsonSuggestion jsonSuggestion : query) {
      String[] ss = jsonSuggestion.getId().split(":");
      if (ss.length > 1 && ss[1].equals(code)) {
        CodeBase cb = new CodeBase();
        cb.setCode(code);
        cb.setDesc(jsonSuggestion.getValue());
        list.add(cb);
        break;
      }
    }
  }

  public String removeDash(String s) {
    if (s != null && s.indexOf('-') <= 2) {
      return s.substring(0, s.indexOf('-'));
    }
    return s;
  }
  
  private void beforeSaveMRAndOPD(long tid, MR mr, OP_D opd) {
    StringBuffer sb = new StringBuffer();
    sb.append(tid);
    sb.append(",");
    sb.append(opd.getCaseType());
    sb.append(",");
    sb.append(opd.getSeqNo());
    mr.setObjective(sb.toString());
  }
}
