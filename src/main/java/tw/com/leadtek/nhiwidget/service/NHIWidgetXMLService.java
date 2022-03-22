/**
 * Created on 2021/1/25.
 */
package tw.com.leadtek.nhiwidget.service;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.jsonwebtoken.Claims;
import tw.com.leadtek.nhiwidget.constant.ACTION_TYPE;
import tw.com.leadtek.nhiwidget.constant.MR_STATUS;
import tw.com.leadtek.nhiwidget.constant.ORDER_TYPE;
import tw.com.leadtek.nhiwidget.constant.ROLE_TYPE;
import tw.com.leadtek.nhiwidget.constant.XMLConstant;
import tw.com.leadtek.nhiwidget.dao.CODE_TABLEDao;
import tw.com.leadtek.nhiwidget.dao.DEDUCTED_NOTEDao;
import tw.com.leadtek.nhiwidget.dao.DRG_CALDao;
import tw.com.leadtek.nhiwidget.dao.FILE_DIFFDao;
import tw.com.leadtek.nhiwidget.dao.IP_DDao;
import tw.com.leadtek.nhiwidget.dao.IP_PDao;
import tw.com.leadtek.nhiwidget.dao.IP_TDao;
import tw.com.leadtek.nhiwidget.dao.MODao;
import tw.com.leadtek.nhiwidget.dao.MRDao;
import tw.com.leadtek.nhiwidget.dao.MR_NOTEDao;
import tw.com.leadtek.nhiwidget.dao.MR_NOTICEDao;
import tw.com.leadtek.nhiwidget.dao.MY_MRDao;
import tw.com.leadtek.nhiwidget.dao.OP_DDao;
import tw.com.leadtek.nhiwidget.dao.OP_PDao;
import tw.com.leadtek.nhiwidget.dao.OP_TDao;
import tw.com.leadtek.nhiwidget.model.CodeBase;
import tw.com.leadtek.nhiwidget.model.JsonSuggestion;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_TABLE;
import tw.com.leadtek.nhiwidget.model.rdb.DEDUCTED_NOTE;
import tw.com.leadtek.nhiwidget.model.rdb.DHead;
import tw.com.leadtek.nhiwidget.model.rdb.DRG_CAL;
import tw.com.leadtek.nhiwidget.model.rdb.FILE_DIFF;
import tw.com.leadtek.nhiwidget.model.rdb.IP;
import tw.com.leadtek.nhiwidget.model.rdb.IP_D;
import tw.com.leadtek.nhiwidget.model.rdb.IP_DData;
import tw.com.leadtek.nhiwidget.model.rdb.IP_P;
import tw.com.leadtek.nhiwidget.model.rdb.IP_T;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.model.rdb.MR_NOTE;
import tw.com.leadtek.nhiwidget.model.rdb.MR_NOTICE;
import tw.com.leadtek.nhiwidget.model.rdb.MY_MR;
import tw.com.leadtek.nhiwidget.model.rdb.OP;
import tw.com.leadtek.nhiwidget.model.rdb.OP_D;
import tw.com.leadtek.nhiwidget.model.rdb.OP_DData;
import tw.com.leadtek.nhiwidget.model.rdb.OP_P;
import tw.com.leadtek.nhiwidget.model.rdb.OP_T;
import tw.com.leadtek.nhiwidget.model.rdb.USER;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;
import tw.com.leadtek.nhiwidget.payload.DeductedNoteListResponse;
import tw.com.leadtek.nhiwidget.payload.MO;
import tw.com.leadtek.nhiwidget.payload.MRCount;
import tw.com.leadtek.nhiwidget.payload.MRCountResponse;
import tw.com.leadtek.nhiwidget.payload.MRDetail;
import tw.com.leadtek.nhiwidget.payload.MRResponse;
import tw.com.leadtek.nhiwidget.payload.MRStatusCount;
import tw.com.leadtek.nhiwidget.payload.MrNoteListResponse;
import tw.com.leadtek.nhiwidget.payload.MrNotePayload;
import tw.com.leadtek.nhiwidget.payload.SearchReq;
import tw.com.leadtek.nhiwidget.payload.mr.DrgCalPayload;
import tw.com.leadtek.nhiwidget.payload.mr.DrgListPayload;
import tw.com.leadtek.nhiwidget.payload.mr.EditMRPayload;
import tw.com.leadtek.nhiwidget.payload.mr.HomepageParameters;
import tw.com.leadtek.nhiwidget.payload.mr.SearchMRParameters;
import tw.com.leadtek.nhiwidget.payload.my.DoctorList;
import tw.com.leadtek.nhiwidget.payload.my.DoctorListResponse;
import tw.com.leadtek.nhiwidget.payload.my.MyTodoList;
import tw.com.leadtek.nhiwidget.payload.my.MyTodoListResponse;
import tw.com.leadtek.nhiwidget.payload.my.NoticeRecord;
import tw.com.leadtek.nhiwidget.payload.my.NoticeRecordResponse;
import tw.com.leadtek.nhiwidget.payload.my.QuestionMark;
import tw.com.leadtek.nhiwidget.payload.my.QuestionMarkResponse;
import tw.com.leadtek.nhiwidget.payload.my.WarningOrder;
import tw.com.leadtek.nhiwidget.payload.my.WarningOrderResponse;
import tw.com.leadtek.nhiwidget.security.jwt.JwtUtils;
import tw.com.leadtek.nhiwidget.security.service.UserDetailsImpl;
import tw.com.leadtek.tools.DateTool;
import tw.com.leadtek.tools.SendHTTP;
import tw.com.leadtek.tools.StringUtility;
import tw.com.leadtek.tools.Utility;

@Service
public class NHIWidgetXMLService {

  private Logger logger = LogManager.getLogger();

  public final static String DOCTYPE = "<?xml version=\"1.0\" encoding=\"Big5\"?>\n";

  private final int IGNORE_STATUS = 100;

  private final int BATCH = 1000;

  /**
   * 針對姓名、證號是否隱碼
   */
  @Value("${project.isMask}")
  private boolean ISMASK;

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
  private CodeTableService codeTableService;

  @Autowired
  private MY_MRDao myMrDao;

  @Autowired
  private MR_NOTICEDao mrNoticeDao;

  @Autowired
  private ParametersService parameters;

  @Autowired
  private LogDataService logService;

  @Autowired
  private MR_NOTEDao mrNoteDao;

  @Autowired
  private DEDUCTED_NOTEDao deductedNoteDao;

  @Autowired
  private RedisService redisService;

  @Autowired
  private JwtUtils jwtUtils;

  @Autowired
  private UserService userService;

  @Autowired
  private IntelligentService is;

  @Autowired
  private EntityManager em;

  @Autowired
  private DRG_CALDao drgCalDao;

  @Autowired
  private EMailService emailService;

  @Autowired
  private MODao moDao;

  @Autowired
  private FILE_DIFFDao diffDao;

  @Value("${project.serverUrl}")
  private String serverUrl;

  @Value("${project.hospId}")
  private String hospId;

  public void saveOP(OP op) {
    OP_T opt = saveOPT(op.getTdata());
    // Map<String, Object> condition1 =
    // logService.makeCondition(new String[][] {{"ID", Long.toString(opt.getId())}});
    // Map<String, Object> row1 = logService.findOne("OP_T", condition1);
    // logService.updateModification("system", "OP_T", condition1, new HashMap<String, Object>(),
    // row1);
    List<HashMap<String, Object>> opdList = getOPDByOPTID(opt.getId());
    List<HashMap<String, Object>> oppList = getOPPByOPTID(opt.getId());

    List<OP_DData> dDataList = op.getDdata();
    List<OP_P> oppBatch = new ArrayList<OP_P>();
    long timeAll = System.currentTimeMillis();
    long timeAllMR = 0;
    int count = 0;
    for (OP_DData op_dData : dDataList) {
      count++;
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
      // System.out.println("timeMR=" + timeMR + " ms");
      long timeOpd = System.currentTimeMillis();
      CODE_TABLE ct = codeTableService.getCodeTable("INFECTIOUS", opd.getIcdCm1());
      mr.setInfectious((ct == null) ? 0 : 1);
      opd.setMrId(mr.getId());
      opd = opdDao.save(opd);

      // timeOpd = System.currentTimeMillis() - timeOpd;
      // System.out.println("timeOpd=" + timeOpd + " ms");
      mr.setdId(opd.getId());
      mrDao.updateDid(opd.getId(), mr.getId());
      // Map<String, Object> condition2 =
      // logService.makeCondition(new String[][] {{"ID", Long.toString(opd.getId())}});
      // Map<String, Object> row2 = logService.findOne("OP_D", condition2);
      // logService.updateModification("system", "OP_D", condition2, new HashMap<String, Object>(),
      // row2);l
      for (OP_P opp : oppListXML) {
        opp.setOpdId(opd.getId());
        updateOPPID(oppList, opp);
        opp.setMrId(mr.getId());
        // oppBatch.add(opp);
        if (oppBatch.size() > BATCH) {
          // long timeOpp = System.currentTimeMillis();
          // oppDao.saveAll(oppBatch);
          // timeOpp = System.currentTimeMillis() - timeOpp;
          // System.out.println("save opp:" + timeOpp + " ms, " + oppBatch.size());
          oppBatch.clear();
        }
        // oppDao.save(opp);
        // Map<String, Object> condition3 =
        // logService.makeCondition(new String[][] {{"ID", Long.toString(opp.getId())}});
        // Map<String, Object> row3 = logService.findOne("OP_P", condition3);
        // logService.updateModification("system", "OP_P", condition3, new HashMap<String,
        // Object>(),
        // row3);
      }
    }
    if (oppBatch.size() > 0) {
      // oppDao.saveAll(oppBatch);
    }
    timeAll = System.currentTimeMillis() - timeAll;
    count--;
    double avg = (double) timeAllMR / (double) count;
    System.out
        .println("timeAll:" + count + "," + timeAll + " ms" + ", mr:" + timeAllMR + ", avg:" + avg);
  }

  public void saveOPBatch(OP op) {
    OP_T opt = saveOPT(op.getTdata());
    // 避免重複insert
    List<HashMap<String, Object>> opdList = getOPDByOPTID(opt.getId());
    List<HashMap<String, Object>> oppList = getOPPByOPTID(opt.getId());
    List<OP_DData> dDataList = op.getDdata();
    List<OP_P> oppBatch = new ArrayList<OP_P>();
    if (dDataList == null) {
      System.err.println("dataList is null");
      return;
    }
    for (OP_DData op_dData : dDataList) {
      OP_D opd = op_dData.getDbody();
      maskOPD(opd);
      List<OP_P> oppListXML = opd.getPdataList();
      if (op_dData.getDhead() != null) {
        opd.setCaseType(op_dData.getDhead().getCASE_TYPE());
        opd.setSeqNo(op_dData.getDhead().getSEQ_NO());
      }
      opd.setOptId(opt.getId());

      updateOPDID(opdList, opd);
      MR mr = null;
      List<FILE_DIFF> diffList = null;
      if (opd.getMrId() != null) {
        Optional<MR> optional = mrDao.findById(opd.getMrId());
        if (optional.isPresent()) {
          mr = optional.get();
          if (mr.getStatus().intValue() != MR_STATUS.NO_CHANGE.value()) {
            diffList = new ArrayList<FILE_DIFF>();
            clearFileDiff(mr.getId());
          }
          mr.updateMR(opd, diffList, codeTableService);
          if (diffList != null && diffList.size() > 0) {
            mr.setChangeOther(1);
          }
        } else {
          mr = new MR(opd);
          mr.setId(opd.getMrId());
          mr.setStatus(MR_STATUS.NO_CHANGE.value());
        }
      } else {
        mr = new MR(opd);
        mr.setStatus(MR_STATUS.NO_CHANGE.value());
      }
      mr.setApplYm(opt.getFeeYm());
      // beforeSaveMRAndOPD(opt.getId(), mr, opd);
      
      if (diffList == null) {
        CODE_TABLE ct = codeTableService.getCodeTable("INFECTIOUS", opd.getIcdCm1());
        mr.setInfectious((ct == null) ? 0 : 1);
        mr.setIcdcm1(opd.getIcdCm1());
        MRDetail.updateIcdcmOtherOP(mr, opd);
        MRDetail.updateIcdpcsOP(mr, opd);
        MRDetail.updateIcdAll(mr);
        mr = mrDao.save(mr);
        opd.setMrId(mr.getId());
        opd = opdDao.save(opd);
      } else {
        if (! mr.getIcdcm1().equals(opd.getIcdCm1())) {
          FILE_DIFF fd = new FILE_DIFF(mr.getId(), "icdCM", opd.getIcdCm1());
          fd.setArrayIndex(0);
          diffList.add(fd);
          mr.setChangeICD(1);
        }

        String oldIcdcmOthers = mr.getIcdcmOthers();
        MRDetail.updateIcdcmOtherOP(mr, opd);
        if (compareDotStrings(mr.getId(), oldIcdcmOthers, mr.getIcdcmOthers(), "icdCM", diffList,
            0)) {
          mr.setChangeICD(1);
          mr.setIcdcmOthers(oldIcdcmOthers);
        }

        String oldIcdpcs = mr.getIcdpcs();
        MRDetail.updateIcdpcsOP(mr, opd);
        if (compareDotStrings(mr.getId(), oldIcdpcs, mr.getIcdpcs(), "icdOP", diffList, -1)) {
          mr.setChangeOther(1);
          mr.setIcdpcs(oldIcdpcs);
        }
      }

      mr.setdId(opd.getId());
      mrDao.updateDid(opd.getId(), mr.getId());
      StringBuffer sb = new StringBuffer(",");
      if (diffList == null) {
        for (OP_P opp : oppListXML) {
          if (opp.getDrugNo() != null) {
            sb.append(opp.getDrugNo());
            sb.append(",");
          }
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
      } else {
        // 需比對有無差異
        List<OP_P> opps = oppDao.findByOpdIdOrderByOrderSeqNo(opd.getId());
        List<MO> moList = new ArrayList<MO>();
        for (int i = 0; i < opps.size(); i++) {
          OP_P oppOld = opps.get(i);
          boolean isFound = false;
          for (int j = 0; i < oppListXML.size(); j++) {
            OP_P oppNew = oppListXML.get(j);
            if (compareOPP(mr.getId(), oppOld, oppNew, diffList, moList)) {
              isFound = true;
              break;
            }
          }
          if (!isFound) {
            addDiff(mr.getId(), null, oppOld.getOrderSeqNo().intValue(), (OP_P) null, diffList,
                moList);
          }
        }
        if (oppListXML.size() > opps.size()) {
          for(int i=opps.size(); i<oppListXML.size(); i++) {
            OP_P opp = oppListXML.get(i);
            addDiff(mr.getId(), opp.getDrugNo(), opp.getOrderSeqNo().intValue(), opp, diffList,
                moList);
          }
        }
        if (moList.size() > 0) {
          mr.setChangeOrder(1);
          for (MO mo : moList) {
            moDao.save(mo);
          }
        }
      }
      if (sb.length() > 1) {
        if (!sb.toString().equals(mr.getCodeAll())) {
          mr.setCodeAll(sb.toString());
        }
      } else {
        if (mr.getCodeAll() != null) {
          mr.setCodeAll(null);
        }
      }
      if (diffList != null) {
        for (FILE_DIFF diff : diffList) {
          diffDao.save(diff);
        }
        USER user = null;
        long userId = -1; 
        if (mr.getApplId() != null) {
          try {
            userId = Long.parseLong(mr.getApplId());
          } catch (NumberFormatException e) {
            user = userService.findUser(mr.getApplId());
            if (user != null) {
              userId = user.getId();
            }
          }
          if (userId > 0 && user == null) {
            user = userService.findUserById(userId);
          }
        }
        if (user != null) {
          updateMyMrStatus(mr.getId().longValue(), MR_STATUS.WAIT_CONFIRM.value(), mr, userId, user.getUsername(), user.getDisplayName());
        } else {
          updateMyMrStatus(mr.getId().longValue(), MR_STATUS.WAIT_CONFIRM.value(), mr, userId, null, mr.getApplName());  
        }
        mr.setStatus(MR_STATUS.WAIT_CONFIRM.value());
      }
      mrDao.save(mr);
    }
    if (oppBatch.size() > 0) {
      oppDao.saveAll(oppBatch);
    }
    // timeAll = System.currentTimeMillis() - timeAll;
    // count--;
    // double avg = (double) timeAll / (double) count;
  }

  public void saveIP(IP ip) {
    IP_T ipt = saveIPT(ip.getTdata());
    // Map<String, Object> condition1 =
    // logService.makeCondition(new String[][] {{"ID", Long.toString(ipt.getId())}});
    // Map<String, Object> row1 = logService.findOne("IP_T", condition1);
    // logService.updateModification("system", "IP_T", condition1, new HashMap<String, Object>(),
    // row1);
    List<HashMap<String, Object>> ipdList = getIPDByIPTID(ipt.getId());
    List<HashMap<String, Object>> ippList = getIPPByIPTID(ipt.getId());

    if (ip.getDdata() == null) {
      System.err.println("dataList is null");
      return;
    }
    // long saveIPD = 0;
    List<IP_P> ippBatch = new ArrayList<IP_P>();
    for (IP_DData ip_dData : ip.getDdata()) {
      // if (count > 50) {
      // break;
      // }

      IP_D ipd = ip_dData.getDbody();
      maskIPD(ipd);
      List<IP_P> ippListXML = ipd.getPdataList();
      ipd.setCaseType(ip_dData.getDhead().getCASE_TYPE());
      ipd.setSeqNo(ip_dData.getDhead().getSEQ_NO());
      ipd.setIptId(ipt.getId());
      ipd.setLeaveDate(DateTool.convertChineseToYear(ipd.getOutDate()));
      
      updateIPDID(ipdList, ipd);

      // long startMR =System.currentTimeMillis();
      MR mr = null;
      List<FILE_DIFF> diffList = null;
      if (ipd.getMrId() != null) {
        // 已存在該病歷
        Optional<MR> optional = mrDao.findById(ipd.getMrId());
        if (optional.isPresent()) {
          mr = optional.get();
          if (mr.getStatus().intValue() != MR_STATUS.NO_CHANGE.value()) {
            diffList = new ArrayList<FILE_DIFF>();
            clearFileDiff(mr.getId());
          }
          mr.updateMR(ipd, diffList, codeTableService);
          if (diffList != null && diffList.size() > 0) {
            mr.setChangeOther(1);
          }
        } else {
          mr = new MR(ipd);
          mr.setStatus(MR_STATUS.NO_CHANGE.value());
        }
      } else {
        mr = new MR(ipd);
        mr.setStatus(MR_STATUS.NO_CHANGE.value());
      }
      mr.setApplYm(ipt.getFeeYm());
      if (diffList == null) {
        mr.setIcdcm1(ipd.getIcdCm1());
        MRDetail.updateIcdcmOtherIP(mr, ipd);
        MRDetail.updateIcdpcsIP(mr, ipd);
        MRDetail.updateIcdAll(mr);
        CODE_TABLE ct = codeTableService.getCodeTable("INFECTIOUS", ipd.getIcdCm1());
        mr.setInfectious((ct == null) ? 0 : 1);
        mr = mrDao.save(mr);
        ipd.setMrId(mr.getId());
        ipd = ipdDao.save(ipd);
      } else {
        if (!mr.getIcdcm1().equals(ipd.getIcdCm1())) {
          FILE_DIFF fd = new FILE_DIFF(mr.getId(), "icdCM", ipd.getIcdCm1());
          fd.setArrayIndex(0);
          diffList.add(fd);
          mr.setChangeICD(1);
        }

        String oldIcdcmOthers = mr.getIcdcmOthers();
        MRDetail.updateIcdcmOtherIP(mr, ipd);
        if (compareDotStrings(mr.getId(), oldIcdcmOthers, mr.getIcdcmOthers(), "icdCM", diffList,
            0)) {
          mr.setChangeICD(1);
          mr.setIcdcmOthers(oldIcdcmOthers);
        }

        String oldIcdpcs = mr.getIcdpcs();
        MRDetail.updateIcdpcsIP(mr, ipd);
        if (compareDotStrings(mr.getId(), oldIcdpcs, mr.getIcdpcs(), "icdOP", diffList, -1)) {
          mr.setChangeOther(1);
          mr.setIcdpcs(oldIcdpcs);
        }
      }

      mr.setdId(ipd.getId());
      mrDao.updateDid(ipd.getId(), mr.getId());
      if(mr.getId() != null && mr.getId() == 4395) {
        System.out.println("changeICD=" + mr.getChangeICD());
      }
      StringBuffer sb = new StringBuffer(",");
      // 自費金額
      int ownExpense = 0;
      if (diffList == null) {
        for (IP_P ipp : ippListXML) {
          if (ipp.getOrderCode() != null) {
            sb.append(ipp.getOrderCode());
            sb.append(",");
          }
          ipp.setIpdId(ipd.getId());
          updateIPPID(ippList, ipp);
          maskIPP(ipp);
          ipp.setMrId(mr.getId());
          // E:自費特材項目-未支付
          if ("E".equals(ipp.getOrderType())){
            ownExpense += ipp.getTotalDot();
          }
          ippBatch.add(ipp);
          if (ippBatch.size() > BATCH) {
            ippDao.saveAll(ippBatch);
            ippBatch.clear();
          }
          // Map<String, Object> condition3 =
          // logService.makeCondition(new String[][] {{"ID", Long.toString(ipp.getId())}});
          // Map<String, Object> row3 = logService.findOne("IP_P", condition3);
          // logService.updateModification("system", "IP_P", condition3, new HashMap<String,
          // Object>(),
          // row3);
        }
      } else {
        // 需比對有無差異
        List<IP_P> ipps = ippDao.findByIpdIdOrderByOrderSeqNo(ipd.getId());
        List<MO> moList = new ArrayList<MO>();
        for (int i = 0; i < ipps.size(); i++) {
          IP_P ippOld = ipps.get(i);
          boolean isFound = false;
          for (int j = 0; i < ippListXML.size(); j++) {
            IP_P ippNew = ippListXML.get(j);
            if (compareIPP(mr.getId(), ippOld, ippNew, diffList, moList)) {
              isFound = true;
              // E:自費特材項目-未支付
              if ("E".equals(ippNew.getOrderType())){
                ownExpense += ippNew.getTotalDot();
              }
              break;
            }
          }
          if (!isFound) {
            addDiff(mr.getId(), null, ippOld.getOrderSeqNo().intValue(), (IP_P) null, diffList,
                moList);
          }
        }
        if (ippListXML.size() > ipps.size()) {
          for(int i=ipps.size(); i<ippListXML.size(); i++) {
            IP_P ipp = ippListXML.get(i);
            addDiff(mr.getId(), ipp.getOrderCode(), ipp.getOrderSeqNo().intValue(), ipp, diffList,
                moList);
            // E:自費特材項目-未支付
            if ("E".equals(ipp.getOrderType())){
              ownExpense += ipp.getTotalDot();
            }
          }
        }
        if (moList.size() > 0) {
          mr.setChangeOrder(1);
          for (MO mo : moList) {
            moDao.save(mo);
          }
        }
      }
      if (sb.length() > 1) {
        if (sb.toString().length() > 4000) {
          mr.setCodeAll(sb.toString().substring(0, 3999));          
        } else {
          mr.setCodeAll(sb.toString());
        }
      } else {
        if (mr.getCodeAll() != null) {
          mr.setCodeAll(null);
        }
      }
      if (diffList != null) {
        for (FILE_DIFF diff : diffList) {
          diffDao.save(diff);
        }
        long userId = -1; 
        USER user = null;
        if (mr.getApplId() != null) {
          try {
            userId = Long.parseLong(mr.getApplId());
          } catch (NumberFormatException e) {
            user = userService.findUser(mr.getApplId());
            if (user != null) {
              userId = user.getId();
            }
          }
          if (userId > 0 && user == null) {
            user = userService.findUserById(userId);
          }
        }
        mr.setStatus(MR_STATUS.WAIT_CONFIRM.value());
        if (user != null) {
          updateMyMrStatus(mr.getId().longValue(), MR_STATUS.WAIT_CONFIRM.value(), mr, userId, user.getUsername(), user.getDisplayName());
        } else {
          updateMyMrStatus(mr.getId().longValue(), MR_STATUS.WAIT_CONFIRM.value(), mr, userId, null, mr.getApplName());  
        }
      }
      mr.setOwnExpense(ownExpense);
      mrDao.save(mr);
    }
    if (ippBatch.size() > 0) {
      ippDao.saveAll(ippBatch);
    }
  }

  private boolean compareDotStrings(Long mrId, String oldS, String newS, String columnName,
      List<FILE_DIFF> diffList, int startArrayIndex) {
    boolean changeICD = false;
    if (oldS == null && newS == null) {
      return false;
    }
    if (oldS == null && newS != null) {
      changeICD = true;
      String[] newIcdcm = newS.split(",");
      for (int i = 1; i < newIcdcm.length; i++) {
        FILE_DIFF fd = new FILE_DIFF(mrId, columnName, newIcdcm[i]);
        fd.setArrayIndex(i + startArrayIndex);
        diffList.add(fd);
      }
    } else if (oldS != null && newS == null) {
      changeICD = true;
      String[] oldIcdcm = oldS.split(",");
      for (int i = 1; i < oldIcdcm.length; i++) {
        FILE_DIFF fd = new FILE_DIFF(mrId, columnName, null);
        fd.setArrayIndex(i + startArrayIndex);
        diffList.add(fd);
      }
    } else if (!oldS.equals(newS)) {
      String[] oldIcdcm = oldS.split(",");
      String[] newIcdcm = newS.split(",");
      for (int i = 1; i < oldIcdcm.length; i++) {
        if (i < newIcdcm.length) {
          if (!oldIcdcm[i].equals(newIcdcm[i])) {
            FILE_DIFF fd = new FILE_DIFF(mrId, columnName, newIcdcm[i]);
            fd.setArrayIndex(i + startArrayIndex);
            diffList.add(fd);
            changeICD = true;
          }
        } else {
          FILE_DIFF fd = new FILE_DIFF(mrId, columnName, null);
          fd.setArrayIndex(i + startArrayIndex);
          diffList.add(fd);
          changeICD = true;
        }
      }
      if (newIcdcm.length > oldIcdcm.length) {
        for (int i = oldIcdcm.length; i < newIcdcm.length; i++) {
          FILE_DIFF fd = new FILE_DIFF(mrId, columnName, newIcdcm[i]);
          fd.setArrayIndex(i + startArrayIndex);
          diffList.add(fd);
          changeICD = true;
        }
      }
    }
    return changeICD;
  }

  private boolean compareIPP(Long mrId, IP_P ippOld, IP_P ippNew, List<FILE_DIFF> diffList,
      List<MO> moList) {
    boolean result = false;
    if (ippOld.getOrderSeqNo().intValue() == ippNew.getOrderSeqNo().intValue()) {
      result = true;
      if (ippOld.getOrderCode() != null && !ippOld.getOrderCode().equals(ippNew.getOrderCode())
          || (ippNew.getOrderCode() != null
              && !ippNew.getOrderCode().equals(ippOld.getOrderCode()))) {
        addDiff(mrId, "orderCode", ippOld.getOrderSeqNo().intValue(), ippNew, diffList, moList);
      } else if ((ippOld.getBedNo() != null && !ippOld.getBedNo().equals(ippNew.getBedNo()))
          || (ippNew.getBedNo() != null && !ippNew.getBedNo().equals(ippOld.getBedNo()))) {
        addDiff(mrId, "bedNo", ippOld.getOrderSeqNo().intValue(), ippNew, diffList, moList);
      } else if (ippOld.getCommHospId() != null
          && !ippOld.getCommHospId().equals(ippNew.getCommHospId())
          || (ippNew.getCommHospId() != null
              && !ippNew.getCommHospId().equals(ippOld.getCommHospId()))) {
        addDiff(mrId, "commHospId", ippOld.getOrderSeqNo().intValue(), ippNew, diffList, moList);
      } else if (ippOld.getConFuncType() != null
          && !ippOld.getConFuncType().equals(ippNew.getConFuncType())
          || (ippNew.getConFuncType() != null
              && !ippNew.getConFuncType().equals(ippOld.getConFuncType()))) {
        addDiff(mrId, "conFuncType", ippOld.getOrderSeqNo().intValue(), ippNew, diffList, moList);
      } else if (ippOld.getCurePath() != null && !ippOld.getCurePath().equals(ippNew.getCurePath())
          || (ippNew.getCurePath() != null && !ippNew.getCurePath().equals(ippOld.getCurePath()))) {
        addDiff(mrId, "curePath", ippOld.getOrderSeqNo().intValue(), ippNew, diffList, moList);
      } else if (ippOld.getDonater() != null && !ippOld.getDonater().equals(ippNew.getDonater())
          || (ippNew.getDonater() != null && !ippNew.getDonater().equals(ippOld.getDonater()))) {
        addDiff(mrId, "donater", ippOld.getOrderSeqNo().intValue(), ippNew, diffList, moList);
      } else if (ippOld.getDrugFre() != null && !ippOld.getDrugFre().equals(ippNew.getDrugFre())
          || (ippNew.getDrugFre() != null && !ippNew.getDrugFre().equals(ippOld.getDrugFre()))) {
        addDiff(mrId, "drugFre", ippOld.getOrderSeqNo().intValue(), ippNew, diffList, moList);
      } else if (ippOld.getDrugPath() != null && !ippOld.getDrugPath().equals(ippNew.getDrugPath())
          || (ippNew.getDrugPath() != null && !ippNew.getDrugPath().equals(ippOld.getDrugPath()))) {
        addDiff(mrId, "drugPath", ippOld.getOrderSeqNo().intValue(), ippNew, diffList, moList);
      } else if (ippOld.getDrugSerialNo() != null
          && !ippOld.getDrugSerialNo().equals(ippNew.getDrugSerialNo())
          || (ippNew.getDrugSerialNo() != null
              && !ippNew.getDrugSerialNo().equals(ippOld.getDrugSerialNo()))) {
        addDiff(mrId, "drugSerialNo", ippOld.getOrderSeqNo().intValue(), ippNew, diffList, moList);
      } else if (ippOld.getDrugUse() != null && !ippOld.getDrugUse().equals(ippNew.getDrugUse())
          || (ippNew.getDrugUse() != null && !ippNew.getDrugUse().equals(ippOld.getDrugUse()))) {
        addDiff(mrId, "drugUse", ippOld.getOrderSeqNo().intValue(), ippNew, diffList, moList);
      } else if (ippOld.getEndTime() != null && !ippOld.getEndTime().equals(ippNew.getEndTime())
          || (ippNew.getEndTime() != null && !ippNew.getEndTime().equals(ippOld.getEndTime()))) {
        addDiff(mrId, "endTime", ippOld.getOrderSeqNo().intValue(), ippNew, diffList, moList);
      } else if (ippOld.getFuncType() != null && !ippOld.getFuncType().equals(ippNew.getFuncType())
          || (ippNew.getFuncType() != null && !ippNew.getFuncType().equals(ippOld.getFuncType()))) {
        addDiff(mrId, "funcType", ippOld.getOrderSeqNo().intValue(), ippNew, diffList, moList);
      } else if (ippOld.getImgSource() != null
          && !ippOld.getImgSource().equals(ippNew.getImgSource())
          || (ippNew.getImgSource() != null
              && !ippNew.getImgSource().equals(ippOld.getImgSource()))) {
        addDiff(mrId, "imgSource", ippOld.getOrderSeqNo().intValue(), ippNew, diffList, moList);
      } else if (ippOld.getInhCode() != null && !ippOld.getInhCode().equals(ippNew.getInhCode())
          || (ippNew.getInhCode() != null && !ippNew.getInhCode().equals(ippOld.getInhCode()))) {
        addDiff(mrId, "inhCode", ippOld.getOrderSeqNo().intValue(), ippNew, diffList, moList);
      } else if (ippOld.getNonListMark() != null
          && !ippOld.getNonListMark().equals(ippNew.getNonListMark())
          || (ippNew.getNonListMark() != null
              && !ippNew.getNonListMark().equals(ippOld.getNonListMark()))) {
        addDiff(mrId, "nonListMark", ippOld.getOrderSeqNo().intValue(), ippNew, diffList, moList);
      } else if (ippOld.getNonListName() != null
          && !ippOld.getNonListName().equals(ippNew.getNonListName())
          || (ippNew.getNonListName() != null
              && !ippNew.getNonListName().equals(ippOld.getNonListName()))) {
        addDiff(mrId, "nonListName", ippOld.getOrderSeqNo().intValue(), ippNew, diffList, moList);
      } else if (ippOld.getOrderType() != null
          && !ippOld.getOrderType().equals(ippNew.getOrderType())
          || (ippNew.getOrderType() != null
              && !ippNew.getOrderType().equals(ippOld.getOrderType()))) {
        addDiff(mrId, "orderType", ippOld.getOrderSeqNo().intValue(), ippNew, diffList, moList);
      } else if (ippOld.getOwnExpMtrNo() != null
          && !ippOld.getOwnExpMtrNo().equals(ippNew.getOwnExpMtrNo())
          || (ippNew.getOwnExpMtrNo() != null
              && !ippNew.getOwnExpMtrNo().equals(ippOld.getOwnExpMtrNo()))) {
        addDiff(mrId, "ownExpMtrNo", ippOld.getOrderSeqNo().intValue(), ippNew, diffList, moList);
      } else if (ippOld.getPartAccoData() != null
          && !ippOld.getPartAccoData().equals(ippNew.getPartAccoData())
          || (ippNew.getPartAccoData() != null
              && !ippNew.getPartAccoData().equals(ippOld.getPartAccoData()))) {
        addDiff(mrId, "partAccoData", ippOld.getOrderSeqNo().intValue(), ippNew, diffList, moList);
      } 
//      else if (ippOld.getPayBy() != null && !ippOld.getPayBy().equals(ippNew.getPayBy())
//          || (ippNew.getPayBy() != null && !ippNew.getPayBy().equals(ippOld.getPayBy()))) {
//        addDiff(mrId, "payBy", ippOld.getOrderSeqNo().intValue(), ippNew, diffList, moList);
//      }
      else if (ippOld.getPayCodeType() != null
          && !ippOld.getPayCodeType().equals(ippNew.getPayCodeType())
          || (ippNew.getPayCodeType() != null
              && !ippNew.getPayCodeType().equals(ippOld.getPayCodeType()))) {
        addDiff(mrId, "payCodeType", ippOld.getOrderSeqNo().intValue(), ippNew, diffList, moList);
      } else if (ippOld.getPayRate() != null && !ippOld.getPayRate().equals(ippNew.getPayRate())
          || (ippNew.getPayRate() != null && !ippNew.getPayRate().equals(ippOld.getPayRate()))) {
        addDiff(mrId, "payRate", ippOld.getOrderSeqNo().intValue(), ippNew, diffList, moList);
      } else if (ippOld.getPreNo() != null && !ippOld.getPreNo().equals(ippNew.getPreNo())
          || (ippNew.getPreNo() != null && !ippNew.getPreNo().equals(ippOld.getPreNo()))) {
        addDiff(mrId, "preNo", ippOld.getOrderSeqNo().intValue(), ippNew, diffList, moList);
      } else if (ippOld.getPrsnId() != null && !ippOld.getPrsnId().equals(ippNew.getPrsnId())
          || (ippNew.getPrsnId() != null && !ippNew.getPrsnId().equals(ippOld.getPrsnId()))) {
        addDiff(mrId, "prsnId", ippOld.getOrderSeqNo().intValue(), ippNew, diffList, moList);
      } else if (ippOld.getStartTime() != null
          && !ippOld.getStartTime().equals(ippNew.getStartTime())
          || (ippNew.getStartTime() != null
              && !ippNew.getStartTime().equals(ippOld.getStartTime()))) {
        addDiff(mrId, "startTime", ippOld.getOrderSeqNo().intValue(), ippNew, diffList, moList);
      } else if ((ippOld.getTotalQ() != null && ippNew.getTotalQ() != null)
          && (ippOld.getTotalQ().doubleValue() != ippNew.getTotalQ().doubleValue())) {
        addDiff(mrId, "totalQ", ippOld.getOrderSeqNo().intValue(), ippNew, diffList, moList);
      } else if ((ippOld.getTotalDot() != null && ippNew.getTotalDot() != null)
          && (ippOld.getTotalDot().intValue() != ippNew.getTotalDot().intValue())) {
        addDiff(mrId, "totalDot", ippOld.getOrderSeqNo().intValue(), ippNew, diffList, moList);
      } else if ((ippOld.getTwDrgsCalcu() != null && ippNew.getTwDrgsCalcu() != null)
          && (ippOld.getTwDrgsCalcu().doubleValue() != ippNew.getTwDrgsCalcu().doubleValue())) {
        addDiff(mrId, "twDrgsCalcu", ippOld.getOrderSeqNo().intValue(), ippNew, diffList, moList);
      } else if ((ippOld.getUnitP() != null && ippNew.getUnitP() != null)
          && (ippOld.getUnitP().floatValue() != ippNew.getUnitP().floatValue())) {
        addDiff(mrId, "unitP", ippOld.getOrderSeqNo().intValue(), ippNew, diffList, moList);
      }
    }
    return result;
  }

  /**
   * IP_P 比對有差異，加到 FILE_DIFF 及 MO_DIFF 2 張 table
   * @param mrId
   * @param columnName
   * @param seqNo
   * @param ippNew
   * @param diffList
   * @param moList
   */
  private void addDiff(Long mrId, String columnName, int seqNo, IP_P ippNew,
      List<FILE_DIFF> diffList, List<MO> moList) {
    FILE_DIFF fd = new FILE_DIFF(mrId, "mos", columnName);
    fd.setArrayIndex(seqNo);
    diffList.add(fd);
    if (ippNew != null) {
      MO mo = new MO();
      mo.setMrId(mrId);
      mo.setIPPData(ippNew, codeTableService);
      moList.add(mo);
    }
  }
  
  /**
   * IP_P 比對有差異，加到 FILE_DIFF 及 MO_DIFF 2 張 table
   * @param mrId
   * @param columnName
   * @param seqNo
   * @param ippNew
   * @param diffList
   * @param moList
   */
  private void addDiff(Long mrId, String columnName, int seqNo, OP_P oppNew,
      List<FILE_DIFF> diffList, List<MO> moList) {
    FILE_DIFF fd = new FILE_DIFF(mrId, "mos", columnName);
    fd.setArrayIndex(seqNo);
    diffList.add(fd);
    if (oppNew != null) {
      MO mo = new MO();
      mo.setMrId(mrId);
      mo.setOPPData(oppNew, codeTableService);
      moList.add(mo);
    }
  }
  
  
  private boolean compareOPP(Long mrId, OP_P oppOld, OP_P oppNew, List<FILE_DIFF> diffList,
      List<MO> moList) {
    boolean result = false;
    if (oppOld.getOrderSeqNo().intValue() == oppNew.getOrderSeqNo().intValue()) {
      result = true;
      if ((oppOld.getDrugNo() != null && !oppOld.getDrugNo().equals(oppNew.getDrugNo()))
          || (oppNew.getDrugNo() != null && !oppNew.getDrugNo().equals(oppOld.getDrugNo()))) {
        addDiff(mrId, "drugNo", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      } else if ((oppOld.getChrMark() != null && !oppOld.getChrMark().equals(oppNew.getChrMark()))
          || (oppNew.getChrMark() != null && !oppNew.getChrMark().equals(oppOld.getChrMark()))) {
        addDiff(mrId, "chrMark", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      } else if ((oppOld.getCommHospId() != null && !oppOld.getCommHospId().equals(oppNew.getCommHospId()))
          || (oppNew.getCommHospId() != null && !oppNew.getCommHospId().equals(oppOld.getCommHospId()))) {
        addDiff(mrId, "commHospId", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      } else if ((oppOld.getCurePath() != null && !oppOld.getCurePath().equals(oppNew.getCurePath()))
          || (oppNew.getCurePath() != null && !oppNew.getCurePath().equals(oppOld.getCurePath()))) {
        addDiff(mrId, "curePath", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      } else if ((oppOld.getDrugDay() != null && oppNew.getDrugDay() != null)
          && (oppOld.getDrugDay().intValue() != oppNew.getDrugDay().intValue())) {
        addDiff(mrId, "drugDay", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      } else if ((oppOld.getDrugFre() != null && !oppOld.getDrugFre().equals(oppNew.getDrugFre()))
          || (oppNew.getDrugFre() != null && !oppNew.getDrugFre().equals(oppOld.getDrugFre()))) {
        addDiff(mrId, "drugFre", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      } else if ((oppOld.getDrugNo() != null && !oppOld.getDrugNo().equals(oppNew.getDrugNo()))
          || (oppNew.getDrugNo() != null && !oppNew.getDrugNo().equals(oppOld.getDrugNo()))) {
        addDiff(mrId, "drugNo", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      }else if ((oppOld.getDrugPath() != null && !oppOld.getDrugPath().equals(oppNew.getDrugPath()))
          || (oppNew.getDrugPath() != null && !oppNew.getDrugPath().equals(oppOld.getDrugPath()))) {
        addDiff(mrId, "drugPath", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      } else if ((oppOld.getDrugSerialNo() != null && !oppOld.getDrugSerialNo().equals(oppNew.getDrugSerialNo()))
          || (oppNew.getDrugSerialNo() != null && !oppNew.getDrugSerialNo().equals(oppOld.getDrugSerialNo()))) {
        addDiff(mrId, "drugSerialNo", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      } else if ((oppOld.getDrugUse() != null && oppNew.getDrugUse() != null)
          && (oppOld.getDrugUse().doubleValue() != oppNew.getDrugUse().doubleValue())) {
        addDiff(mrId, "drugUse", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      } else if ((oppOld.getEndTime() != null && !oppOld.getEndTime().equals(oppNew.getEndTime()))
          || (oppNew.getEndTime() != null && !oppNew.getEndTime().equals(oppOld.getEndTime()))) {
        addDiff(mrId, "endTime", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      } else if ((oppOld.getFuncType() != null && !oppOld.getFuncType().equals(oppNew.getFuncType()))
          || (oppNew.getFuncType() != null && !oppNew.getFuncType().equals(oppOld.getFuncType()))) {
        addDiff(mrId, "funcType", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      } else if ((oppOld.getImgSource() != null && !oppOld.getImgSource().equals(oppNew.getImgSource()))
          || (oppNew.getImgSource() != null && !oppNew.getImgSource().equals(oppOld.getImgSource()))) {
        addDiff(mrId, "imgSource", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      } else if ((oppOld.getInhCode() != null && !oppOld.getInhCode().equals(oppNew.getInhCode()))
          || (oppNew.getInhCode() != null && !oppNew.getInhCode().equals(oppOld.getInhCode()))) {
        addDiff(mrId, "", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      } else if ((oppOld.getMedType() != null && !oppOld.getMedType().equals(oppNew.getMedType()))
          || (oppNew.getMedType() != null && !oppNew.getMedType().equals(oppOld.getMedType()))) {
        addDiff(mrId, "medType", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      } else if ((oppOld.getNonListMark() != null && !oppOld.getNonListMark().equals(oppNew.getNonListMark()))
          || (oppNew.getNonListMark() != null && !oppNew.getNonListMark().equals(oppOld.getNonListMark()))) {
        addDiff(mrId, "nonListMark", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      } else if ((oppOld.getNonListName() != null && !oppOld.getNonListName().equals(oppNew.getNonListName()))
          || (oppNew.getNonListName() != null && !oppNew.getNonListName().equals(oppOld.getNonListName()))) {
        addDiff(mrId, "nonListName", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      } else if ((oppOld.getOrderType() != null && !oppOld.getOrderType().equals(oppNew.getOrderType()))
          || (oppNew.getOrderType() != null && !oppNew.getOrderType().equals(oppOld.getOrderType()))) {
        addDiff(mrId, "orderType", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      } else if ((oppOld.getOwnExpMtrNo() != null && !oppOld.getOwnExpMtrNo().equals(oppNew.getOwnExpMtrNo()))
          || (oppNew.getOwnExpMtrNo() != null && !oppNew.getOwnExpMtrNo().equals(oppOld.getOwnExpMtrNo()))) {
        addDiff(mrId, "ownExpMtrNo", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      } else if ((oppOld.getPayCodeType() != null && !oppOld.getPayCodeType().equals(oppNew.getPayCodeType()))
          || (oppNew.getPayCodeType() != null && !oppNew.getPayCodeType().equals(oppOld.getPayCodeType()))) {
        addDiff(mrId, "payCodeType", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      } else if ((oppOld.getPayRate() != null && !oppOld.getPayRate().equals(oppNew.getPayRate()))
          || (oppNew.getPayRate() != null && !oppNew.getPayRate().equals(oppOld.getPayRate()))) {
        addDiff(mrId, "payRate", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      }  else if ((oppOld.getPreNo() != null && !oppOld.getPreNo().equals(oppNew.getPreNo()))
          || (oppNew.getPreNo() != null && !oppNew.getPreNo().equals(oppOld.getPreNo()))) {
        addDiff(mrId, "preNo", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      }  else if ((oppOld.getPrsnId() != null && !oppOld.getPrsnId().equals(oppNew.getPrsnId()))
          || (oppNew.getPrsnId() != null && !oppNew.getPrsnId().equals(oppOld.getPrsnId()))) {
        addDiff(mrId, "prsnId", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      }  else if ((oppOld.getUnitP() != null && oppNew.getUnitP() != null)
          && (oppNew.getUnitP().floatValue() != oppOld.getUnitP().floatValue())) {
        addDiff(mrId, "unitP", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      }  else if ((oppOld.getStartTime() != null && !oppOld.getStartTime().equals(oppNew.getStartTime()))
          || (oppNew.getStartTime() != null && !oppNew.getStartTime().equals(oppOld.getStartTime()))) {
        addDiff(mrId, "startTime", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      }  else if ((oppOld.getTotalDot() != null && oppNew.getTotalDot() != null)
          && (oppNew.getTotalDot().intValue() != oppOld.getTotalDot().intValue())) {
        addDiff(mrId, "totalDot", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      }  else if ((oppOld.getTotalQ() != null && oppNew.getTotalQ() != null)
          && (oppNew.getTotalQ().doubleValue() != oppOld.getTotalQ().doubleValue())) {
        addDiff(mrId, "totalQ", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      } 
    }
    return result;
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
    opp.setPayBy("N");
    opp.setApplStatus(1);
  }

  private void maskIPP(IP_P ipp) {
    if (ISMASK) {
      ipp.setPrsnId(StringUtility.maskString(ipp.getPrsnId(), StringUtility.MASK_MOBILE));
    }
    ipp.setPayBy("N");
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
   * 
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
      map.put("birth", (String) obj[5]);
      result.add(map);
    }
    start = System.currentTimeMillis() - start;
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
    return result;
  }

  /**
   * 取得DB中相同IPT_ID的 IP_D，減少存取DB次數.
   * 
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
      map.put("birth", (String) obj[5]);
      result.add(map);
    }
    start = System.currentTimeMillis() - start;
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
    return result;
  }

  private void updateOPDID(List<HashMap<String, Object>> list, OP_D opd) {
    opd.setUpdateAt(new java.util.Date());
    int index = -1;
    for (int i = list.size() - 1; i >= 0; i--) {
      HashMap<String, Object> map = list.get(i);
      if (((String) map.get("birth")).equals(opd.getIdBirthYmd())
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
    for (int i = list.size() - 1; i >= 0; i--) {
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
    for (int i = list.size() - 1; i >= 0; i--) {
      HashMap<String, Object> map = list.get(i);
      if (((String) map.get("birth")).equals(ipd.getIdBirthYmd())
          && ((String) map.get("rocId")).equals(ipd.getRocId())
          && ((String) map.get("inDate")).equals(ipd.getInDate())) {
        // System.out
        // .println("find same:" + ipd.getSeqNo() + "," + ipd.getRocId() + "," + ipd.getInDate());
        index = i;
        ipd.setId(((BigInteger) map.get("id")).longValue());
        ipd.setMrId(((BigInteger) map.get("mrId")).longValue());
        ipd.setUpdateAt(new java.util.Date());
        break;
      }
    }
    if (index > -1) {
      list.remove(index);
    }
  }

  /**
   * 比對IP_P是否已存在DB，若是則指定在IP_P中的id
   * 
   * @param list
   * @param ipp
   */
  private void updateIPPID(List<HashMap<String, Object>> list, IP_P ipp) {
    ipp.setUpdateAt(new java.util.Date());
    int index = -1;
    for (int i = list.size() - 1; i >= 0; i--) {
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

      List<IP_D> ipdList = ipdDao.findByIDFromMR("20", sDate, eDate);
      if (ipdList != null && ipdList.size() > 0) {
        List<IP_P> ippList = ippDao.findByIpdIDFromMR("20", sDate, eDate);

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
    List<CODE_TABLE> list = "ALL".equals(cat) ? ctDao.findAll() : ctDao.findByCatOrderByCode(cat);
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

  /**
   * 全域搜尋
   * 
   * @param page
   * @param perPage
   * @param all
   * @param sDate
   * @param eDate
   * @param status
   * @return
   */
  private Map<String, Object> fullSearchMR(int page, int perPage, String all, Date sDate,
      Date eDate, String status) {
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
        Path<String> icdName = root.get("icdAll");
        Path<String> codeName = root.get("codeAll");
        Path<String> subjective = root.get("subjective");
        Path<String> objective = root.get("objective");
        Path<String> drg = root.get("drgCode");
        if (sDate != null && eDate != null) {
          predicate.add(cb.between(root.get("mrDate"), sDate, eDate));
        }
        for (int i = 0; i < ss.length; i++) {
          predicate.add(cb.or(cb.like(pathRocId, ss[i] + "%"), cb.like(pathInhMrId, ss[i] + "%"),
              cb.like(pathFuncType, ss[i] + "%"), cb.like(pathPrsnId, ss[i] + "%"),
              cb.like(pathApplId, ss[i] + "%"), cb.like(pathRemark, ss[i] + "%"),
              cb.like(pathInhClinicId, ss[i] + "%"), cb.like(pathName, ss[i] + "%"),
              cb.like(icdName, "%," + ss[i] + "%"), cb.like(codeName, "%," + ss[i] + "%"),
              cb.like(subjective, "%," + ss[i] + "%"), cb.like(objective, "%," + ss[i] + "%"),
              cb.like(drg, "%," + ss[i] + "%")));
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
        // if (status < IGNORE_STATUS && mrDb.getStatus().intValue() != status) {
        // continue;
        // }
        count++;
        if (count >= min && count <= max) {
          mrList.add(new MRResponse(mrDb, codeTableService));
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
      String deductedOrder, String all, String patientName, String patientId, String pharId,
      int perPage, int page) {

    Map<String, Object> result = new HashMap<String, Object>();

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    // int mrStatus = status == null ? IGNORE_STATUS : Integer.parseInt(status);
    try {
      Date sDate = (startDate == null || startDate.length() == 0) ? null
          : new Date(sdf.parse(startDate).getTime());
      Date eDate = (endDate == null || endDate.length() == 0) ? null
          : new Date(sdf.parse(endDate).getTime());

      if (all != null) {
        result = fullSearchMR(page, perPage, all, sDate, eDate, status);
      } else {
        boolean isAnd = false;
        if (allMatch != null && allMatch.toUpperCase().equals("Y")) {
          isAnd = true;
        }
        // TODO 缺orderCode, drugUse, icdAll, icdCMMajor, icdCMSecondary, icdPCS
        // qrObject, qrSdate, qrEdate, deductedCode, deductedOrder, inhCode, inhCodeDrugUse
        result = getMR(page, perPage, isAnd, sDate, eDate, applYM, minPoints, maxPoints, dataFormat,
            funcType, prsnId, prsnName, applId, applName, inhMrId, inhClinicId, drg, drgSection,
            status, deductedCode, patientName, patientId, pharId);
      }
      return result;
    } catch (ParseException e) {
      result.put("result", "error");
      result.put("message", "sdate或edate格式錯誤");
      return result;
    }
  }

  // List<MR>
  private Map<String, Object> getMR(int page, int perPage, boolean isAnd, Date sDate, Date eDate,
      String applYM, Integer minPoints, Integer maxPoints, String dataFormat, String funcType,
      String prsnId, String prsnName, String applId, String applName, String inhMrId,
      String inhClinicId, String drg, String drgSection, String status, String deductedCode,
      String patientName, String patientId, String pharId) {

    Specification<MR> spec = new Specification<MR>() {

      private static final long serialVersionUID = 2L;

      public Predicate toPredicate(Root<MR> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicate = searchMRPredicate(root, cb, sDate, eDate, applYM, minPoints,
            maxPoints, dataFormat, funcType, prsnId, prsnName, applId, applName, inhMrId,
            inhClinicId, drg, drgSection, status, deductedCode, patientName, patientId, pharId);
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
        MRResponse mrr = new MRResponse(mrDb, codeTableService);
        mrList.add(mrr);
        try {
          String json = objectMapper.writeValueAsString(mrr);
        } catch (JsonProcessingException e) {
          e.printStackTrace();
        }
      }
    }
    mc.setTotalMr((int) total);
    updateMRStatusCountAll(mc, sDate, eDate, applYM, minPoints, maxPoints, dataFormat, funcType,
        prsnId, prsnName, applId, applName, inhMrId, inhClinicId, drg, drgSection, status,
        deductedCode, patientName, patientId, pharId);
    Map<String, Object> result = new HashMap<String, Object>();
    result.put("count", (int) total);
    result.put("totalPage", Utility.getTotalPage((int) total, iPerPage));
    result.put("mr", mrList);
    result.put("mrStatus", mc);
    return result;
  }

  private List<Predicate> searchMRPredicate(Root<MR> root, CriteriaBuilder cb, Date sDate,
      Date eDate, String applYM, Integer minPoints, Integer maxPoints, String dataFormat,
      String funcType, String prsnId, String prsnName, String applId, String applName,
      String inhMrId, String inhClinicId, String drg, String drgSection, String status,
      String deductedCode, String patientName, String patientId, String pharId) {
    List<Predicate> predicate = new ArrayList<Predicate>();
    predicate.add(cb.between(root.get("mrDate"), sDate, eDate));
    if (minPoints != null && maxPoints != null) {
      predicate.add(cb.between(root.get("totalDot"), minPoints, maxPoints));
    }
    if (!"00".equals(dataFormat)) {
      addPredicate(root, predicate, cb, "dataFormat", dataFormat, false, false, false);
    }
    if (!"00".equals(funcType) && !"0".equals(funcType)) {
      addPredicate(root, predicate, cb, "funcType", funcType, false, false, false);
    }
    addPredicate(root, predicate, cb, "prsnId", prsnId, false, false, false);
    addPredicate(root, predicate, cb, "prsnName", prsnId, false, false, false);
    addPredicate(root, predicate, cb, "applId", applId, false, false, false);
    addPredicate(root, predicate, cb, "applName", applId, false, false, false);
    addPredicate(root, predicate, cb, "inhMrId", inhMrId, false, false, false);
    addPredicate(root, predicate, cb, "inhClinicId", inhClinicId, false, false, false);
    addPredicate(root, predicate, cb, "drgCode", drg, false, false, false);
    addPredicate(root, predicate, cb, "drgSection", drgSection, false, false, false);
    addPredicate(root, predicate, cb, "status", status, false, true, false);
    addPredicate(root, predicate, cb, "applYm", applYM, true, false, false);
    if (patientName != null && patientName.length() > 0) {
      addPredicate(root, predicate, cb, "name", "%" + patientName, true, false, false);
    }
    if (patientId != null && patientId.length() > 0) {
      addPredicate(root, predicate, cb, "rocId", "%" + patientId, true, false, false);
    }
    if (deductedCode != null && deductedCode.length() > 0) {
      predicate.add(cb.greaterThan(root.get("deductedDot"), 0));
    }
    return predicate;
  }

  public Map<String, Object> getMR(SearchMRParameters smrp) {
    if (smrp.getAll() != null) {
      return fullSearchMR(smrp.getPage(), smrp.getPerPage(), smrp.getAll(), null, null, null);
    }
    Specification<MR> spec = new Specification<MR>() {

      private static final long serialVersionUID = 4L;

      public Predicate toPredicate(Root<MR> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicate = searchMRPredicate(root, query, cb, smrp);

        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));

        List<Order> orderList = new ArrayList<Order>();
        if (smrp.getOrderBy() != null && smrp.getAsc() != null) {
          if (smrp.getAsc().booleanValue()) {
            orderList.add(cb.asc(root.get(smrp.getOrderBy())));
          } else {
            orderList.add(cb.desc(root.get(smrp.getOrderBy())));
          }
        } else {
          orderList.add(cb.desc(root.get("mrDate")));
        }
        query.orderBy(orderList);
        return query.getRestriction();
      }
    };
    long total = mrDao.count(spec);

    Page<MR> pages = mrDao.findAll(spec,
        PageRequest.of(smrp.getPage().intValue(), smrp.getPerPage().intValue()));

    ObjectMapper objectMapper = new ObjectMapper();
    // 若值為 null 不會輸出到 String
    // objectMapper.setSerializationInclusion(Include.NON_NULL);
    List<MRResponse> mrList = new ArrayList<MRResponse>();
    MRCount mc = new MRCount();
    if (pages != null && pages.getSize() > 0) {
      for (MR mrDb : pages) {
        updateMRStatusCount(mrDb, mc);
        MRResponse mrr = new MRResponse(mrDb, codeTableService);
        mrList.add(mrr);
        try {
          String json = objectMapper.writeValueAsString(mrr);
        } catch (JsonProcessingException e) {
          e.printStackTrace();
        }
      }
    }
    mc.setTotalMr((int) total);
    updateMRStatusCountAll(mc, smrp);
    Map<String, Object> result = new HashMap<String, Object>();
    result.put("count", (int) total);
    result.put("totalPage", Utility.getTotalPage((int) total, smrp.getPerPage().intValue()));
    result.put("mr", mrList);
    result.put("mrStatus", mc);
    return result;
  }

  private List<Predicate> searchMRPredicate(Root<MR> root, CriteriaQuery<?> query,
      CriteriaBuilder cb, SearchMRParameters smrp) {
    List<Predicate> predicate = new ArrayList<Predicate>();
    if (smrp.getsDate() != null && smrp.geteDate() != null) {
      predicate.add(cb.between(root.get("mrDate"), smrp.getsDate(), smrp.geteDate()));
    }
    boolean notOthers = smrp.getNotOthers() == null ? false : smrp.getNotOthers().booleanValue();

    if (smrp.getMinPoints() != null && smrp.getMaxPoints() != null) {
      if (notOthers) {
        predicate.add(cb.lessThan(root.get("totalDot"), smrp.getMinPoints()));
        predicate.add(cb.greaterThan(root.get("totalDot"), smrp.getMaxPoints()));
      } else {
        predicate.add(cb.between(root.get("totalDot"), smrp.getMinPoints(), smrp.getMaxPoints()));
      }
    }
    if (smrp.getDataFormat() != null) {
      addPredicate(root, predicate, cb, "dataFormat", smrp.getDataFormat(), false, false, false);
    }
    addPredicate(root, predicate, cb, "funcType", smrp.getFuncType(), false, false, notOthers);
    addPredicate(root, predicate, cb, "prsnId", smrp.getPrsnId(), false, false, notOthers);
    addPredicate(root, predicate, cb, "prsnName", smrp.getPrsnName(), false, false, notOthers);
    addPredicate(root, predicate, cb, "applId", smrp.getApplId(), false, false, false);
    addPredicate(root, predicate, cb, "applName", smrp.getApplName(), false, false, false);
    addPredicate(root, predicate, cb, "inhMrId", smrp.getInhMrId(), false, false, false);
    addPredicate(root, predicate, cb, "inhClinicId", smrp.getInhClinicId(), false, false, false);
    if (smrp.getNotStatus() != null && smrp.getNotStatus().booleanValue()) {
      addPredicate(root, predicate, cb, "status", smrp.getStatus(), true, true, true);
    } else {
      addPredicate(root, predicate, cb, "status", smrp.getStatus(), false, true, false);
    }
    addPredicate(root, predicate, cb, "applYm", smrp.getApplYM(), true, false, false);
    addPredicate(root, predicate, cb, "name", smrp.getPatientName(), true, false, notOthers);
    addPredicate(root, predicate, cb, "rocId", smrp.getPatientId(), true, false, notOthers);
    if (smrp.getNotDRG() != null && smrp.getNotDRG().booleanValue()) {
      addPredicate(root, predicate, cb, "drgCode", smrp.getDrg(), false, false, true);
      addPredicate(root, predicate, cb, "drgSection", smrp.getDrgSection(), true, false, true);
    } else {
      addPredicate(root, predicate, cb, "drgCode", smrp.getDrg(), false, false, false);
      addPredicate(root, predicate, cb, "drgSection", smrp.getDrgSection(), false, false, false);
    }
    if (smrp.getOnlyDRG() != null && smrp.getOnlyDRG().booleanValue()) {
      predicate.add(cb.isNotNull(root.get("drgSection")));
    }
    boolean notOrderCode =
        smrp.getNotOrderCode() == null ? false : smrp.getNotOrderCode().booleanValue();
    addPredicate(root, predicate, cb, "codeAll", smrp.getOrderCode(), true, false, notOrderCode);
    if (smrp.getDrugUse() != null && smrp.getDrugUse().length() > 0
        && smrp.getOrderCode() != null) {
      String orderCode = smrp.getOrderCode().startsWith("%,") ? smrp.getOrderCode().substring(2)
          : smrp.getOrderCode();
      // select * from MR where id in (select distinct(mrId) from OP_P where drugNo=? and totalQ =
      // ?)
      Subquery<OP_P> oppSubquery = query.subquery(OP_P.class);
      Root<OP_P> oppRoot = oppSubquery.from(OP_P.class);

      oppSubquery.select(oppRoot.get("mrId")).distinct(true);
      List<Predicate> oppPredicate = new ArrayList<Predicate>();
      addPredicate(oppRoot, oppPredicate, cb, "drugNo", orderCode, true, false, notOrderCode);
      addPredicate(oppRoot, oppPredicate, cb, "totalQ", smrp.getDrugUse(), true, true,
          notOrderCode);

      Predicate[] pre = new Predicate[oppPredicate.size()];
      oppSubquery.where(oppPredicate.toArray(pre));

      Subquery<IP_P> ippSubquery = query.subquery(IP_P.class);
      Root<IP_P> ippRoot = ippSubquery.from(IP_P.class);

      ippSubquery.select(ippRoot.get("mrId")).distinct(true);
      List<Predicate> ippPredicate = new ArrayList<Predicate>();
      addPredicate(ippRoot, ippPredicate, cb, "orderCode", orderCode, true, false, notOrderCode);
      addPredicate(ippRoot, ippPredicate, cb, "totalQ", smrp.getDrugUse(), true, true,
          notOrderCode);

      Predicate[] ippPre = new Predicate[ippPredicate.size()];
      ippSubquery.where(ippPredicate.toArray(ippPre));

      predicate.add(cb.or(cb.in(root.get("id")).value(oppSubquery),
          cb.in(root.get("id")).value(ippSubquery)));
    }

    boolean notIcd = smrp.getNotICD() == null ? false : smrp.getNotICD().booleanValue();
    // System.out.println("icdAll:" + smrp.getIcdAll() + ", icdCMMajor=" + smrp.getIcdCMMajor() + ",
    // icdCMSec=" +
    // smrp.getIcdCMSec() + ",pcs=" + smrp.getIcdPCS());
    addPredicate(root, predicate, cb, "icdAll", smrp.getIcdAll(), true, false, notIcd);
    addPredicate(root, predicate, cb, "icdcm1", smrp.getIcdCMMajor(), false, false, notIcd);
    addPredicate(root, predicate, cb, "icdcmOthers", smrp.getIcdCMSec(), true, false, notIcd);
    addPredicate(root, predicate, cb, "icdpcs", smrp.getIcdPCS(), true, false, notIcd);

    if (smrp.getDeductedCode() != null || smrp.getDeductedOrder() != null) {
      Subquery<DEDUCTED_NOTE> subquery = query.subquery(DEDUCTED_NOTE.class);
      Root<DEDUCTED_NOTE> deductedNoteRoot = subquery.from(DEDUCTED_NOTE.class);

      subquery.select(deductedNoteRoot.get("mrId")).distinct(true);
      List<Predicate> predicateDeductedNote = new ArrayList<Predicate>();
      if (smrp.getDeductedCode() != null) {
        addPredicate(deductedNoteRoot, predicateDeductedNote, cb, "code", smrp.getDeductedCode(),
            false, false, false);
      }
      if (smrp.getDeductedOrder() != null) {
        addPredicate(deductedNoteRoot, predicateDeductedNote, cb, "deductedOrder",
            smrp.getDeductedOrder(), false, false, false);
      }
      Predicate[] pre = new Predicate[predicateDeductedNote.size()];
      subquery.where(predicateDeductedNote.toArray(pre));
      predicate.add(cb.in(root.get("id")).value(subquery));
    }
    return predicate;
  }

  private void updateMRStatusCountAll(MRCount result, Date sDate, Date eDate, String applYM,
      Integer minPoints, Integer maxPoints, String dataFormat, String funcType, String prsnId,
      String prsnName, String applId, String applName, String inhMrId, String inhClinicId,
      String drg, String drgSection, String status, String deductedCode, String patientName,
      String patientId, String pharId) {

    int[] statusInt = stringToIntArray(status, " ");
    if (isIntegerInArrayOrIgnore(statusInt, MR_STATUS.CLASSIFIED.value())) {
      result.setClassified(getMRStatusCount(sDate, eDate, applYM, minPoints, maxPoints, dataFormat,
          funcType, prsnId, prsnName, applId, applName, inhMrId, inhClinicId, drg, drgSection,
          String.valueOf(MR_STATUS.CLASSIFIED.value()), deductedCode, patientName, patientId,
          pharId));
    }

    if (isIntegerInArrayOrIgnore(statusInt, MR_STATUS.DONT_CHANGE.value())) {
      result.setDontChange(getMRStatusCount(sDate, eDate, applYM, minPoints, maxPoints, dataFormat,
          funcType, prsnId, prsnName, applId, applName, inhMrId, inhClinicId, drg, drgSection,
          String.valueOf(MR_STATUS.DONT_CHANGE.value()), deductedCode, patientName, patientId,
          pharId));
    }

    if (isIntegerInArrayOrIgnore(statusInt, MR_STATUS.NO_CHANGE.value())) {
      result.setNoChange(getMRStatusCount(sDate, eDate, applYM, minPoints, maxPoints, dataFormat,
          funcType, prsnId, prsnName, applId, applName, inhMrId, inhClinicId, drg, drgSection,
          String.valueOf(MR_STATUS.NO_CHANGE.value()), deductedCode, patientName, patientId,
          pharId));
    }
    if (isIntegerInArrayOrIgnore(statusInt, MR_STATUS.OPTIMIZED.value())) {
      result.setOptimized(getMRStatusCount(sDate, eDate, applYM, minPoints, maxPoints, dataFormat,
          funcType, prsnId, prsnName, applId, applName, inhMrId, inhClinicId, drg, drgSection,
          String.valueOf(MR_STATUS.OPTIMIZED.value()), deductedCode, patientName, patientId,
          pharId));
    }
    if (isIntegerInArrayOrIgnore(statusInt, MR_STATUS.QUESTION_MARK.value())) {
      result.setQuestionMark(getMRStatusCount(sDate, eDate, applYM, minPoints, maxPoints,
          dataFormat, funcType, prsnId, prsnName, applId, applName, inhMrId, inhClinicId, drg,
          drgSection, String.valueOf(MR_STATUS.QUESTION_MARK.value()), deductedCode, patientName,
          patientId, pharId));
    }
    if (isIntegerInArrayOrIgnore(statusInt, MR_STATUS.WAIT_CONFIRM.value())) {
      result.setWaitConfirm(getMRStatusCount(sDate, eDate, applYM, minPoints, maxPoints, dataFormat,
          funcType, prsnId, prsnName, applId, applName, inhMrId, inhClinicId, drg, drgSection,
          String.valueOf(MR_STATUS.WAIT_CONFIRM.value()), deductedCode, patientName, patientId,
          pharId));
    }
    if (isIntegerInArrayOrIgnore(statusInt, MR_STATUS.WAIT_PROCESS.value())) {
      result.setWaitProcess(getMRStatusCount(sDate, eDate, applYM, minPoints, maxPoints, dataFormat,
          funcType, prsnId, prsnName, applId, applName, inhMrId, inhClinicId, drg, drgSection,
          String.valueOf(MR_STATUS.WAIT_PROCESS.value()), deductedCode, patientName, patientId,
          pharId));
    }
    result.setDrg(getMRDRGCount(sDate, eDate, applYM, minPoints, maxPoints, dataFormat, funcType,
        prsnId, prsnName, applId, applName, inhMrId, inhClinicId, drg, drgSection, status,
        deductedCode, patientName, patientId, pharId));
  }

  private int getMRStatusCount(Date sDate, Date eDate, String applYM, Integer minPoints,
      Integer maxPoints, String dataFormat, String funcType, String prsnId, String prsnName,
      String applId, String applName, String inhMrId, String inhClinicId, String drg,
      String drgSection, String status, String deductedCode, String patientName, String patientId,
      String pharId) {
    Specification<MR> spec = new Specification<MR>() {

      private static final long serialVersionUID = 5L;

      public Predicate toPredicate(Root<MR> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicate = searchMRPredicate(root, cb, sDate, eDate, applYM, minPoints,
            maxPoints, dataFormat, funcType, prsnId, prsnName, applId, applName, inhMrId,
            inhClinicId, drg, drgSection, status, deductedCode, patientName, patientId, pharId);
        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));
        return query.getRestriction();
      }
    };
    return (int) mrDao.count(spec);
  }

  private int getMRDRGCount(Date sDate, Date eDate, String applYM, Integer minPoints,
      Integer maxPoints, String dataFormat, String funcType, String prsnId, String prsnName,
      String applId, String applName, String inhMrId, String inhClinicId, String drg,
      String drgSection, String status, String deductedCode, String patientName, String patientId,
      String pharId) {
    Specification<MR> spec = new Specification<MR>() {

      private static final long serialVersionUID = 6L;

      public Predicate toPredicate(Root<MR> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicate = searchMRPredicate(root, cb, sDate, eDate, applYM, minPoints,
            maxPoints, dataFormat, funcType, prsnId, prsnName, applId, applName, inhMrId,
            inhClinicId, drg, drgSection, status, deductedCode, patientName, patientId, pharId);
        predicate.add(cb.isNotNull(root.get("drgSection")));
        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));
        return query.getRestriction();
      }
    };
    return (int) mrDao.count(spec);
  }

  private void updateMRStatusCountAll(MRCount result, SearchMRParameters smrp) {
    String originalStatus = smrp.getStatus();
    int[] statusInt = stringToIntArray(smrp.getStatus(), " ");
    if (isIntegerInArrayOrIgnore(statusInt, MR_STATUS.CLASSIFIED.value())) {
      smrp.setStatus(String.valueOf(MR_STATUS.CLASSIFIED.value()));
      result.setClassified(getMRStatusCount(smrp));
    }
    if (isIntegerInArrayOrIgnore(statusInt, MR_STATUS.DONT_CHANGE.value())) {
      smrp.setStatus(String.valueOf(MR_STATUS.DONT_CHANGE.value()));
      result.setDontChange(getMRStatusCount(smrp));
    }
    if (isIntegerInArrayOrIgnore(statusInt, MR_STATUS.NO_CHANGE.value())) {
      smrp.setStatus(String.valueOf(MR_STATUS.NO_CHANGE.value()));
      result.setNoChange(getMRStatusCount(smrp));
    }
    if (isIntegerInArrayOrIgnore(statusInt, MR_STATUS.OPTIMIZED.value())) {
      smrp.setStatus(String.valueOf(MR_STATUS.OPTIMIZED.value()));
      result.setOptimized(getMRStatusCount(smrp));
    }
    if (isIntegerInArrayOrIgnore(statusInt, MR_STATUS.QUESTION_MARK.value())) {
      smrp.setStatus(String.valueOf(MR_STATUS.QUESTION_MARK.value()));
      result.setQuestionMark(getMRStatusCount(smrp));
    }
    if (isIntegerInArrayOrIgnore(statusInt, MR_STATUS.WAIT_CONFIRM.value())) {
      smrp.setStatus(String.valueOf(MR_STATUS.WAIT_CONFIRM.value()));
      result.setWaitConfirm(getMRStatusCount(smrp));
    }
    if (isIntegerInArrayOrIgnore(statusInt, MR_STATUS.WAIT_PROCESS.value())) {
      smrp.setStatus(String.valueOf(MR_STATUS.WAIT_PROCESS.value()));
      result.setWaitProcess(getMRStatusCount(smrp));
    }
    smrp.setStatus(originalStatus);
    result.setDrg(getMRDRGCount(smrp));
  }

  private int getMRStatusCount(SearchMRParameters smrp) {
    Specification<MR> spec = new Specification<MR>() {

      private static final long serialVersionUID = 7L;

      public Predicate toPredicate(Root<MR> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicate = searchMRPredicate(root, query, cb, smrp);
        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));
        return query.getRestriction();
      }
    };

    return (int) mrDao.count(spec);
  }

  private int getMRDRGCount(SearchMRParameters smrp) {
    Specification<MR> spec = new Specification<MR>() {

      private static final long serialVersionUID = 8L;

      public Predicate toPredicate(Root<MR> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicate = searchMRPredicate(root, query, cb, smrp);
        if (smrp.getOnlyDRG() == null || !smrp.getOnlyDRG().booleanValue()) {
          predicate.add(cb.isNotNull(root.get("drgSection")));
        }
        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));
        return query.getRestriction();
      }
    };
    return (int) mrDao.count(spec);
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
      return getMR(page, perPage, false, sDate, eDate, null, null, null, dataFormat, funcType,
          prsnId, prsnName, applId, applName, null, null, null, null, null, null, null, null, null);

    } catch (ParseException e) {
      e.printStackTrace();
    }

    return new HashMap<String, Object>();
  }

  private void addPredicate(Root<?> root, List<Predicate> predicate, CriteriaBuilder cb,
      String paramName, String params, boolean isAnd, boolean isInteger, boolean isNot) {
    if (params == null || params.length() == 0) {
      return;
    }
    if (params.indexOf(' ') < 0) {
      if (isInteger) {
        if (isNot) {
          predicate.add(cb.notEqual(root.get(paramName), params));
        } else {
          predicate.add(cb.equal(root.get(paramName), params));
        }
      } else {
        if (isNot) {
          // System.out.println("not like " + params + "%");
          predicate.add(
              cb.or(cb.notLike(root.get(paramName), params + "%"), cb.isNull(root.get(paramName))));
        } else {
          predicate.add(cb.like(root.get(paramName), params + "%"));
        }
      }
    } else {
      String[] ss = params.split(" ");
      List<Predicate> predicates = new ArrayList<Predicate>();
      for (int i = 0; i < ss.length; i++) {
        if (isInteger) {
          if (isNot) {
            predicates.add(cb.notEqual(root.get(paramName), ss[i]));
          } else {
            predicates.add(cb.equal(root.get(paramName), ss[i]));
          }
        } else {
          if (i > 0 && (paramName.equals("icdAll") || paramName.equals("icdcmOthers")
              || paramName.equals("icdpcs") || paramName.equals("codeAll"))) {
            if (isNot) {
              predicates.add(cb.notLike(root.get(paramName), "%," + ss[i] + "%"));
            } else {
              predicates.add(cb.like(root.get(paramName), "%," + ss[i] + "%"));
            }
          } else {
            if (isNot) {
              predicates.add(cb.notLike(root.get(paramName), ss[i] + "%"));
            } else {
              predicates.add(cb.like(root.get(paramName), ss[i] + "%"));
            }
          }
        }
      }
      Predicate[] pre = new Predicate[predicates.size()];
      if (isNot) {
        predicate.add(cb.or(cb.and(predicates.toArray(pre)), cb.isNull(root.get(paramName))));
      } else {
        if (isAnd) {
          predicate.add(cb.and(predicates.toArray(pre)));
        } else {
          predicate.add(cb.or(predicates.toArray(pre)));
        }
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

  public MRCountResponse getMRCount(String applYM, String startDate, String endDate,
      String dataFormat, String funcType, String prsnId) {
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
      if (prsnId == null) {
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
        Map<String, Object> map =
            getMR(0, 1000000, false, sDate, eDate, null, null, null, dataFormat, funcType, prsnId,
                null, null, null, null, null, null, null, null, null, null, null, null);
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
      if (values.get("OP_DOT") instanceof BigDecimal) {
        BigDecimal dot = (BigDecimal) values.get("OP_DOT");
        op.setApplDot(dot.intValue());
      } else {
        op.setApplDot(((Integer) values.get("OP_DOT")).intValue());
      }
      totalDot += op.getApplDot().intValue();
    }
    if (values.get("IP_DOT") != null) {
      if (values.get("IP_DOT") instanceof BigDecimal) {
        BigDecimal dot = (BigDecimal) values.get("IP_DOT");
        ip.setApplDot(dot.intValue());
      } else {
        ip.setApplDot(((Integer) values.get("IP_DOT")).intValue());
      }
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

  public MRDetail getMRDetail(long id, UserDetailsImpl user, boolean isRaw) {
    MRDetail result = null;
    MR mr = mrDao.findById(id).orElse(null);
    if (mr != null) {
      result = new MRDetail(mr);
      if (mr.getUpdateUserId() != null
          && user.getId().longValue() != mr.getUpdateUserId().longValue()) {
        result.setIsLastEditor(false);
      } else {
        result.setIsLastEditor(true);
      }
      if (XMLConstant.DATA_FORMAT_OP.equals(result.getDataFormat())) {
        OP_D opD = opdDao.getOne(result.getdId());
        result.setOPDData(opD, codeTableService);

        List<OP_P> oppList = oppDao.findByOpdIdOrderByOrderSeqNo(opD.getId());
        List<MO> moList = new ArrayList<MO>();
        for (OP_P op_P : oppList) {
          MO mo = new MO();
          mo.setOPPData(op_P, codeTableService);
          moList.add(mo);
        }
        result.setMos(moList);
      } else if (XMLConstant.DATA_FORMAT_IP.equals(result.getDataFormat())) {
        IP_D ipD = ipdDao.getOne(result.getdId());
        result.setIPDData(ipD, codeTableService);

        List<IP_P> ippList = ippDao.findByIpdIdOrderByOrderSeqNo(ipD.getId());
        List<MO> moList = new ArrayList<MO>();
        for (IP_P ip_P : ippList) {
          MO mo = new MO();
          mo.setIPPData(ip_P, codeTableService);
          moList.add(mo);
        }
        result.setMos(moList);
      }
      getMRNote(result);
      result.setHint(is.getMRHint(id));
      result.setDeducted(getDeductedNote(result.getId(), true));
      updateMRReaded(id, user);
      checkDiff(result, isRaw);
      result.convertToADYear();
    }

    return result;
  }
  
  /**
   * 檢查是否有HIS異動記錄，若有則更新MRDetail
   * @param result
   * @param isRaw true:回傳最新的HIS記錄，false:回傳目前資料
   * @return true:有最新資料，false：無最新資料
   */
  private boolean checkDiff(MRDetail mrDetail, boolean isRaw) {
    SimpleDateFormat sdf = new SimpleDateFormat(DateTool.SDF);
    List<FILE_DIFF> diffList = diffDao.findByMrId(mrDetail.getId());
    List<MO> moList = moDao.findByMrId(mrDetail.getId());
    if (diffList == null || diffList.size() == 0) {
      return false;
    }
    for (FILE_DIFF fd : diffList) {
      if ("icdCM".equals(fd.getName())) {
        List<Integer> diffIcdCMList = mrDetail.getDiffIcdCM();
        if (diffIcdCMList == null) {
          diffIcdCMList = new ArrayList<Integer>();
        }
        diffIcdCMList.add(fd.getArrayIndex());
        mrDetail.setDiffIcdCM(diffIcdCMList);
        if (isRaw && fd.getNewValue() != null) {
          mrDetail.getIcdCM().set(fd.getArrayIndex().intValue(),
              CodeTableService.getCodeBase(codeTableService, "ICD10-CM", fd.getNewValue()));
        }
      } else if ("icdOP".equals(fd.getName())) {
        List<Integer> diffIcdOPList = mrDetail.getDiffIcdOP();
        if (diffIcdOPList == null) {
          diffIcdOPList = new ArrayList<Integer>();
        }
        diffIcdOPList.add(fd.getArrayIndex());
        mrDetail.setDiffIcdOP(diffIcdOPList);
        if (isRaw && fd.getNewValue() != null) {
          mrDetail.getIcdOP().set(fd.getArrayIndex().intValue(),
              CodeTableService.getCodeBase(codeTableService, "ICD10-PCS", fd.getNewValue()));
        }
      } else if ("mos".equals(fd.getName())) {
        List<Integer> diffMosList = mrDetail.getDiffMos();
        if (diffMosList == null) {
          diffMosList = new ArrayList<Integer>();
        }
        diffMosList.add(fd.getArrayIndex());
        mrDetail.setDiffMos(diffMosList);
        if (isRaw && fd.getNewValue() != null) {
          MO newMO = null;
          for (MO mo : moList) {
            if (mo.getOrderSeqNo().intValue() == fd.getArrayIndex().intValue()) {
              newMO = mo;
              if (XMLConstant.DATA_FORMAT_IP.equals(mrDetail.getDataFormat())) {
                IP_P ipp = newMO.toIpp(codeTableService);
                newMO.setIPPData(ipp, codeTableService);
              } else {

              }
              break;
            }
          }
          if (newMO != null) {
            if (mrDetail.getMos() == null) {
              mrDetail.setMos(new ArrayList<MO>());
            }
            if (mrDetail.getMos().size() < (fd.getArrayIndex())) {
              mrDetail.getMos().add(newMO);
            } else {
              mrDetail.getMos().set(fd.getArrayIndex().intValue() - 1, newMO);
            }
          }
        }
      } else if ("cureItems".equals(fd.getName())) {
        List<Integer> diffCureItemsList = mrDetail.getDiffCureItems();
        if (diffCureItemsList == null) {
          diffCureItemsList = new ArrayList<Integer>();
        }
        diffCureItemsList.add(fd.getArrayIndex());
        mrDetail.setDiffCureItems(diffCureItemsList);
        if (isRaw && fd.getNewValue() != null) {
          mrDetail.getCureItems().set(fd.getArrayIndex().intValue(),
              CodeTableService.getCodeBase(codeTableService, "OP_CURE_ITEM", fd.getNewValue()));
        }
      } else {
        List<String> diffFields =
            mrDetail.getDiffFields() == null ? new ArrayList<String>() : mrDetail.getDiffFields();
        diffFields.add(fd.getName());
        mrDetail.setDiffFields(diffFields);
        if (isRaw) {
          try {
            updateNewFieldValue(fd, mrDetail, sdf);
          } catch (ParseException e) {
            e.printStackTrace();
          }
        }
      }
    }
    return true;
  }
  
  private void updateNewFieldValue(FILE_DIFF fd, MRDetail result, SimpleDateFormat sdf) throws ParseException {
    if ("funcType".equals(fd.getName())) {
      result.setFuncType(CodeTableService.getDesc(codeTableService, "FUNC_TYPE", fd.getNewValue()));
    } else if ("rocId".equals(fd.getName())) {
      result.setRocId(fd.getNewValue());
    } else if ("name".equals(fd.getName())) {
      result.setName(fd.getNewValue());
    }else if ("inDate".equals(fd.getName())) {
      result.setMrDate(sdf.parse(fd.getNewValue()));
    }else if ("outDate".equals(fd.getName())) {
      result.setMrEndDate(sdf.parse(fd.getNewValue()));
    }else if ("applDot".equals(fd.getName())) {
      result.setApplDot(Integer.parseInt(fd.getNewValue()));
    }else if ("drgCode".equals(fd.getName())) {
      result.setTwDrgCode(fd.getNewValue());
      result.setDrgCode(fd.getNewValue());
    }else if ("ownExpense".equals(fd.getName())) {
      result.setNonApplDot(Integer.parseInt(fd.getNewValue()));
    }
  }
  
  private boolean updateDiff(MRDetail mrDetail, int oldStatus) {
    if (mrDetail.getStatus().intValue() != MR_STATUS.NO_CHANGE.value()
        || oldStatus == MR_STATUS.NO_CHANGE.value()) {
      return false;
    }
    // 由其他狀態改為為無需變更，需檢查是否有HIS異動記錄，若有則以HIS資料為準
    return checkDiff(mrDetail, true);
  }

  /**
   * 開始/取消編輯病歷資料
   * 
   * @param id
   * @param jwt
   * @param isEdit
   * @return
   */
  public EditMRPayload editMRDetail(long id, String jwt, boolean isEdit, Integer actionId) {
    EditMRPayload result = new EditMRPayload();
    result.setResult(BaseResponse.SUCCESS);

    MR mr = mrDao.findById(id).orElse(null);
    if (mr == null) {
      result.setMessage("病歷id:" + id + "不存在");
      result.setResult(BaseResponse.ERROR);;
      return result;
    }
    String key = UserService.MREDIT + id + "*";
    String currentUser = jwtUtils.getUsernameFromToken(jwt);
    String userKey = UserService.USER_PREFIX + currentUser;
    // System.out.println("key=" + key);
    Set<Object> sets = redisService.hkeys(key);
    if (sets != null && sets.size() > 0) {
      if (isEdit) {
        for (Object object : sets) {
          // System.out.println("not null:" + object);
          String user = jwtUtils.getUsernameFromToken((String) object);
          if (user == null) {
            redisService.deleteHash(key, (String) object);
          } else if (user.equals(currentUser)) {
            // 同一人
            String values = (String) redisService.hget(key, (String) object);
            // System.out.println("same user:" + values);
            String[] ss = values.split(",");
            result.setActionId(ss.length);
            values = values + "," + ss.length;
            // System.out.println("save " + key + ":" + jwt + "," + values);
            redisService.putHash(key, jwt, values);
            return result;
          } else {
            result.setMessage("目前:" + user + "正在進行編輯作業，請稍後再使用，如果當下無人員進行編輯，可能前次作業儲存流程不完整，導致無法編輯，請聯繫"
                + user + "重新登入系統解除鎖定");
            result.setResult(BaseResponse.ERROR);;
            return result;
          }
        }
      } else {
        // 取消編輯
        result.setActionId(actionId);
        for (Object object : sets) {
          String user = jwtUtils.getUsernameFromToken((String) object);
          if (user == null) {
            redisService.deleteHash(key, (String) object);
          } else if (user.equals(currentUser)) {
            // 同一人
            String values = (String) redisService.hget(key, (String) object);
            String[] ss = values.split(",");
            if (ss.length == 2) {
              // 只有一筆編輯
              redisService.deleteHash(key, jwt);
              redisService.deleteHash(userKey, UserService.EDITING);
              return result;
            } else {
              StringBuffer sb = new StringBuffer(ss[0]);
              for (int i = 1; i < ss.length; i++) {
                // System.out.println("ss[" + i + "]=" + ss[i]);
                if (!ss[i].equals(actionId.toString())) {
                  sb.append(",");
                  sb.append(ss[i]);
                }
              }
              redisService.putHash(key, jwt, sb.toString());
              return result;
            }
          }
        }
      }
    } else if (!isEdit) {
      // 取消編輯但無相關編輯資料
      result.setResult(BaseResponse.ERROR);
      result.setMessage("無相關編輯資料");
      return result;
    }
    redisService.putHash(key, jwt, String.valueOf(System.currentTimeMillis() + ",1"));
    redisService.putHash(userKey, UserService.EDITING, String.valueOf(id));
    result.setActionId(1);
    return result;
  }

  /**
   * 取得該病歷的所有資訊備註
   * 
   * @param id
   * @param user
   * @param isNote
   * @return
   */
  public MrNoteListResponse getMRNote(long id, UserDetailsImpl user, boolean isNote) {
    MrNoteListResponse result = new MrNoteListResponse();
    MR mr = mrDao.findById(id).orElse(null);
    if (mr == null) {
      result.setMessage("病歷id" + id + "不存在");
      result.setResult(BaseResponse.ERROR);
    }

    List<MR_NOTE> list = mrNoteDao.findByMrIdOrderById(id);
    List<MrNotePayload> data = new ArrayList<MrNotePayload>();
    for (MR_NOTE note : list) {
      if (isNote && note.getNoteType() == 1) {
        data.add(new MrNotePayload(note));
      } else if (!isNote && note.getNoteType() == 2) {
        data.add(new MrNotePayload(note));
      }
    }
    result.setData(data);
    return result;
  }

  public String updateMRStatus(long id, UserDetailsImpl user, int status) {
    MR mr = mrDao.findById(id).orElse(null);
    if (mr == null) {
      return "病歷id:" + id + "不存在";
    }
    updateMyMrStatus(id, status, mr, user.getId(), user.getUsername(), user.getDisplayName());
    MRDetail mrDetail = getMRDetail(id, user, true);
    mrDetail.setStatus(status);
    if (updateDiff(mrDetail, mr.getStatus().intValue())) {
      diffDao.deleteByMrId(mrDetail.getId()); 
      moDao.deleteByMrId(mrDetail.getId());
      updateMRDetail(mrDetail, mr, String.valueOf(user.getId()), user.getUsername(), user.getDisplayName());
    }
    mr.setStatus(status);
    mr.setApplName(user.getDisplayName());
    mr.setApplId(userService.findUserById(user.getId()).getInhId());
    mrDao.save(mr);
    return null;
  }

  public void updateMyMrStatus(long mrId, int status, MR mr, long userId, String username, String displayName) {
    MY_MR myMr = myMrDao.findByMrId(mrId);
    if (myMr == null) {
      if (status == MR_STATUS.WAIT_PROCESS.value() || status == MR_STATUS.QUESTION_MARK.value() || 
          status == MR_STATUS.WAIT_CONFIRM.value()) {
        myMr = new MY_MR(mr);
        myMr.setStatus(status);
        myMr.setApplId(username);
        myMr.setApplUserId(userId);
        myMr.setApplName(displayName);
        myMr.setPrsnUserId(userService.getUserIdByName(mr.getPrsnName()));
        myMr.setFuncTypec(codeTableService.getDesc("FUNC_TYPE", myMr.getFuncType()));
        myMrDao.save(myMr);
      }
    } else {
      myMr.setStatus(status);
      myMrDao.save(myMr);
    }
    List<MR_NOTICE> notices = mrNoticeDao.findByMrId(mrId);
    if (notices != null && notices.size() > 0) {
      for (MR_NOTICE notice : notices) {
        notice.setStatus(status);
      }
    }
    if (status == MR_STATUS.NO_CHANGE.value()
        && mr.getStatus().intValue() == MR_STATUS.WAIT_CONFIRM.value()) {
      // 由待確認改為無需變更，檢查智能提示是否有該病歷，若有則修改狀態
      is.removeIntelligentWaitConfirm(mrId);
    }
  }

  public void getMRNote(MRDetail mrDetail) {
    List<MR_NOTE> list = mrNoteDao.findByMrIdOrderById(mrDetail.getId());
    List<MrNotePayload> notes = new ArrayList<MrNotePayload>();
    for (MR_NOTE note : list) {
      if (note.getStatus().intValue() == 0) {
        continue;
      }
      if (note.getNoteType() == 1) {
        notes.add(new MrNotePayload(note));
      }
    }
    mrDetail.setNotes(notes);
  }

  public long getTId(java.util.Date date, boolean isIP) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
    String ymAD = sdf.format(date);
    String chineseYm = String.valueOf(Integer.parseInt(ymAD) - 191100);
    if (isIP) {
      List<IP_T> list = iptDao.findByFeeYmOrderById(chineseYm);
      if (list != null && list.size() > 0) {
        return list.get(0).getId();
      } else {
        IP_T ipt = createIPT(chineseYm);
        return ipt.getId();
      }
    } else {
      List<OP_T> list = optDao.findByFeeYmOrderById(chineseYm);
      if (list != null && list.size() > 0) {
        return list.get(0).getId();
      } else {
        OP_T opt = createOPT(chineseYm);
        return opt.getId();
      }
    }
  }

  public IP_T createIPT(String chineseYm) {
    IP_T ipt = new IP_T();
    ipt.setDataFormat("20");
    ipt.setHospId(hospId);
    ipt.setFeeYm(chineseYm);
    ipt.setApplMode("2");
    ipt.setApplType("1");
    ipt.setApplDate(chineseYm + "01");
    ipt.setUpdateAt(new java.util.Date());
    return iptDao.save(ipt);
  }

  public OP_T createOPT(String chineseYm) {
    OP_T opt = new OP_T();
    opt.setDataFormat("10");
    opt.setHospId(hospId);
    opt.setFeeYm(chineseYm);
    opt.setApplMode("2");
    opt.setApplType("2");
    opt.setApplDate(chineseYm + "01");
    opt.setUpdateAt(new java.util.Date());
    return optDao.save(opt);
  }

  public MRDetail updateMRDetail(MRDetail mrDetail, String jwt, Integer actionId) {
    MRDetail result = null;
    MR mr = mrDao.findById(mrDetail.getId()).orElse(null);
    if (mr == null) {
      result = new MRDetail();
      result.setError("id:" + mrDetail.getId() + " 不存在");
      return result;
    }
    String userId = jwtUtils.getUserID(jwt);
    Claims claims = jwtUtils.getClaimsFromToken(jwt);
    String currentUser = jwtUtils.getUsernameFromClaims(claims);
    String displayName = jwtUtils.getDisplaynameFromClaims(claims);
    if (userId == null || currentUser == null) {
      result = new MRDetail();
      result.setError("登入狀態有誤，請重新登入");
      return result;
    }

    String userKey = UserService.USER_PREFIX + jwtUtils.getUsernameFromToken(jwt);
    // 不加 * hkeys 有時會找不到
    String key = UserService.MREDIT + mrDetail.getId().toString() + "*";
    Set<Object> sets = redisService.hkeys(key);
    if (sets != null && sets.size() > 0) {
      for (Object object : sets) {
        String user = jwtUtils.getUsernameFromToken((String) object);
        if (user == null) {
          redisService.deleteHash(key, (String) object);
        } else if (user.equals(currentUser)) {
          // 同一人
          String values = (String) redisService.hget(key, (String) object);
          String[] ss = values.split(",");
          if (ss.length == 2) {
            // 只有一筆編輯
            redisService.deleteHash(key, jwt);
            redisService.deleteHash(userKey, UserService.EDITING);
            break;
          } else {
            StringBuffer sb = new StringBuffer(ss[0]);
            for (int i = 1; i < ss.length; i++) {
              if (!ss[i].equals(actionId.toString())) {
                sb.append(",");
                sb.append(ss[i]);
              }
            }
            redisService.putHash(key, jwt, sb.toString());
            break;
          }
        }
      }
    } else {
      result = new MRDetail();
      result.setError("未開始編輯無法儲存");
      return result;
    }
    if (updateDiff(mrDetail, mr.getStatus().intValue())) {
      diffDao.deleteByMrId(mrDetail.getId());
      moDao.deleteByMrId(mrDetail.getId());
    }
    return updateMRDetail(mrDetail, mr, userId, currentUser, displayName);
  }
  
  private MRDetail updateMRDetail (MRDetail mrDetail, MR mr, String userId, 
      String username, String displayName) {
    MRDetail result = null;
    if (mr.getStatus().intValue() != mrDetail.getStatus().intValue()) {
      updateMyMrStatus(mrDetail.getId(), mrDetail.getStatus().intValue(), mr,
          Long.parseLong(userId), username, displayName);
    }
    mr.updateMR(mrDetail);
    mr.setUpdateUserId(Long.parseLong(userId));
    boolean needCalculate = false;
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
      needCalculate = updateOPPByMrDetail(mr, opD, mrDetail, true);
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
      // mrDetail.setInDate(ipD.getInDate());
      // System.out.println("ipD.indate=" + ipD.getInDate());
      ipD.setApplDot(mrDetail.getApplDot());
      ipD.setNonApplDot(mrDetail.getOwnExpense());
      ipD.setTwDrgsSuitMark(mrDetail.getTwDrgsSuitMark());
      if(updateIPPByMrDetail(mr, ipD, mrDetail, true)) {
        // 若有新增醫令，再重算一次點數
        updateIPPByMrDetail(mr, ipD, mrDetail, true);
      }
      updateIPDByMrDetail(ipD, mrDetail, mr);
      ipdDao.save(ipD);
      Map<String, Object> condition2 =
          logService.makeCondition(new String[][] {{"ID", Long.toString(ipD.getId())}});
      Map<String, Object> row2 = logService.findOne("IP_D", condition2);
      logService.updateModification("system", "IP_D", condition2, row1, row2);
    }
    mr = mrDao.save(mr);
    result = new MRDetail(mr);
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
    opD.setIdBirthYmd(DateTool.removeSlashForChineseYear(mrDetail.getBirthday()));
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
        opD.setCureItemNo2(mrDetail.getCureItems().get(1).getCode());
      }
      if (mrDetail.getCureItems().size() >= 3) {
        opD.setCureItemNo3(mrDetail.getCureItems().get(2).getCode());
      }
      if (mrDetail.getCureItems().size() >= 4) {
        opD.setCureItemNo4(mrDetail.getCureItems().get(3).getCode());
      }
    }
    if (mrDetail.getIcdOP() != null) {
      if (mrDetail.getIcdOP().size() >= 1) {
        opD.setIcdOpCode1(mrDetail.getIcdOP().get(0).getCode());
      }
      if (mrDetail.getIcdOP().size() >= 2) {
        opD.setIcdOpCode2(mrDetail.getIcdOP().get(1).getCode());
      }
      if (mrDetail.getIcdOP().size() >= 3) {
        opD.setIcdOpCode3(mrDetail.getIcdOP().get(2).getCode());
      }
    }
    opD.setApplCauseMark(mrDetail.getApplCauseMark());
    if (mrDetail.getFuncType() != null && mrDetail.getFuncType().length() > 0) {
      if (mrDetail.getFuncType().indexOf('-') > -1) {
        opD.setFuncType(mrDetail.getFuncType().substring(0, mrDetail.getFuncType().indexOf('-')));
      } else {
        opD.setFuncType(mrDetail.getFuncType());
      }
    }
    opD.setUpdateAt(new java.util.Date());
  }

  /**
   * 
   * @param opdId
   * @param mrDetail
   * @param saveOPP OP_P有異動，是否儲存。只有在測試驗證時才會設為 false
   * @return true:需重算點數，false:不需重算點數
   */
  public boolean updateOPPByMrDetail(MR mr, OP_D opd, MRDetail mrDetail, boolean saveOPP) {
    boolean result = false;
    List<OP_P> oppList = oppDao.findByOpdIdOrderByOrderSeqNo(opd.getId());
    List<MO> moList = mrDetail.getMos();
    initialOP_DDot(opd);
    int drugDot = opd.getDrugDot().intValue();
    int treatDot = opd.getTreatDot().intValue();
    int metrDot = opd.getMetrDot().intValue();
    int diagDot = opd.getDiagDot().intValue();
    int dsvcDot = opd.getDsvcDot().intValue();

    for (OP_P opp : oppList) {
      int index = -1;
      for (int i = 0; i < moList.size(); i++) {
        MO mo = moList.get(i);
        if (opp.getDrugNo().equals(mo.getDrugNo())) {
          int applStatus = mo.getApplStatus();
          boolean dirty = false;
          if (opp.getTotalQ() != null && mo.getTotalQ() != null
              && opp.getTotalQ().doubleValue() == mo.getTotalQ().doubleValue()) {
            index = i;
            dirty = updateOpp(opp, mo);
          } else if (opp.getTotalQ() == null && opp.getTotalQ() == null) {
            index = i;
            dirty = updateOpp(opp, mo);
          }
          if (dirty) {
            if (saveOPP) {
              oppDao.save(opp);
            }
            if (opp.getApplStatus().intValue() != applStatus
                && (opp.getApplStatus().intValue() == 1 || applStatus == 1)) {
              // 申報狀態有變，且有申報改為不申報或不申報改為有申報
              result = true;
            }
          }
        }
        if (index > -1) {
          moList.remove(index);
          if (opp.getApplStatus() == 1) {
            if (opd.getCasePayCode() != null) {
              // 二、論病例計酬、呼吸照護之居家照護、乳癌案件、肺結核案件、南投縣信義鄉暨仁愛鄉精神疾病醫療給付效益提昇計畫：加總醫令類別 4 且醫令代碼長度不為 10 碼及12
              // 碼之點數。
              if (ORDER_TYPE.NO_PAY.value().equals(opp.getOrderType())
                  && opp.getDrugNo().length() != 12 && opp.getDrugNo().length() != 10) {
                treatDot = treatDot - opp.getTotalDot();
              }
            }
            if (ORDER_TYPE.DRUG.value().equals(opp.getOrderType())) {
              drugDot = drugDot - opp.getTotalDot();
            } else if (ORDER_TYPE.TREAT.value().equals(opp.getOrderType())
                && opd.getCasePayCode() == null) {
              treatDot = treatDot - opp.getTotalDot();
            } else if (ORDER_TYPE.METERIAL.value().equals(opp.getOrderType())) {
              metrDot = metrDot - opp.getTotalDot();
            } else if (ORDER_TYPE.DIAGNOSIS.value().equals(opp.getOrderType())) {
              diagDot = diagDot - opp.getTotalDot();
            } else if (ORDER_TYPE.DSVC.value().equals(opp.getOrderType())) {
              dsvcDot = dsvcDot - opp.getTotalDot();
            } else if ("7".equals(opp.getPayCodeType())) {
              // 7 為藥費
              drugDot = drugDot - opp.getTotalDot();
            } else if ("20".equals(opp.getPayCodeType()) && opp.getDrugNo().length() == 12) {
              // 不分類且支付代碼為12碼列為特殊材料費
              metrDot = metrDot - opp.getTotalDot();
            }
          }
          break;
        }
      }
    }
    if (!saveOPP) {
      result = true;
    }
    if (result) {
      if (drugDot != 0) {
        opd.setDrugDot(opd.getDrugDot().intValue() - drugDot);
      }
      if (treatDot != 0) {
        opd.setTreatDot(opd.getTreatDot().intValue() - treatDot);
      }
      if (metrDot != 0) {
        opd.setMetrDot(opd.getMetrDot().intValue() - metrDot);
      }
      if (diagDot != 0) {
        opd.setDiagDot(opd.getDiagDot().intValue() - diagDot);
      }
      if (dsvcDot != 0) {
        opd.setDsvcDot(opd.getDsvcDot().intValue() - dsvcDot);
      }
      opd.calculateTotalDot();
      opd.calculateTotalApplDot();
      mr.setApplDot(opd.getTotalApplDot());
      mr.setTotalDot(opd.getTotalDot());
    }
    return result;
  }

  /**
   * 更新 opp
   * 
   * @param opp
   * @param mo
   * @return true:資料有異動，false:資料無異動
   */
  private boolean updateOpp(OP_P opp, MO mo) {
    boolean result = false;
    if (opp.getOrderSeqNo().intValue() != mo.getOrderSeqNo().intValue()) {
      result = true;
      opp.setOrderSeqNo(mo.getOrderSeqNo());
    }
    if ((opp.getPayBy() != null && !opp.getPayBy().equals(mo.getPayBy()))
        || (mo.getPayBy() != null && !mo.getPayBy().equals(opp.getPayBy()))) {
      result = true;
      opp.setPayBy(mo.getPayBy());
    }
    if (opp.getApplStatus().intValue() != mo.getApplStatus().intValue()) {
      result = true;
      opp.setApplStatus(mo.getApplStatus());
    }
    return result;
  }

  private void updateIPDByMrDetail(IP_D ipD, MRDetail mrDetail, MR mr) {
    ipD.setTwDrgCode(mrDetail.getDrgCode());
    ipD.setRocId(mrDetail.getRocId());
    ipD.setName(mrDetail.getName());
    ipD.setPrsnId(mrDetail.getPrsnId());
    ipD.setInDate(DateTool.removeSlashForChineseYear(mrDetail.getInDate()));
    ipD.setOutDate(DateTool.removeSlashForChineseYear(mrDetail.getOutDate()));
    ipD.setLeaveDate(DateTool.convertChineseToYear(ipD.getOutDate()));

    ipD.setIdBirthYmd(DateTool.removeSlashForChineseYear(mrDetail.getBirthday()));
    ipD.setPayType(removeDash(mrDetail.getPayType()));
    if (mrDetail.getIcdCM() != null) {
      if (mrDetail.getIcdCM().size() >= 1) {
        if (!ipD.getIcdCm1().equals(mrDetail.getIcdCM().get(0).getCode())
            && (ipD.getTwDrgCode() != null || mrDetail.getTwDrgCode() != null)
            && "0".equals(ipD.getTwDrgsSuitMark())) {
          // 主診斷碼有異動且為 DRG 案件
          updateDrgList(mrDetail.getIcdCM().get(0).getCode(), ipD, mr);
        }
        ipD.setIcdCm1(mrDetail.getIcdCM().get(0).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 2) {
        ipD.setIcdCm2(mrDetail.getIcdCM().get(1).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 3) {
        ipD.setIcdCm3(mrDetail.getIcdCM().get(2).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 4) {
        ipD.setIcdCm4(mrDetail.getIcdCM().get(3).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 5) {
        ipD.setIcdCm5(mrDetail.getIcdCM().get(4).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 6) {
        ipD.setIcdCm6(mrDetail.getIcdCM().get(5).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 7) {
        ipD.setIcdCm7(mrDetail.getIcdCM().get(6).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 8) {
        ipD.setIcdCm8(mrDetail.getIcdCM().get(7).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 9) {
        ipD.setIcdCm9(mrDetail.getIcdCM().get(8).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 10) {
        ipD.setIcdCm10(mrDetail.getIcdCM().get(9).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 11) {
        ipD.setIcdCm11(mrDetail.getIcdCM().get(10).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 12) {
        ipD.setIcdCm12(mrDetail.getIcdCM().get(11).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 13) {
        ipD.setIcdCm13(mrDetail.getIcdCM().get(12).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 14) {
        ipD.setIcdCm14(mrDetail.getIcdCM().get(13).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 15) {
        ipD.setIcdCm15(mrDetail.getIcdCM().get(14).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 16) {
        ipD.setIcdCm16(mrDetail.getIcdCM().get(15).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 17) {
        ipD.setIcdCm17(mrDetail.getIcdCM().get(16).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 18) {
        ipD.setIcdCm18(mrDetail.getIcdCM().get(17).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 19) {
        ipD.setIcdCm19(mrDetail.getIcdCM().get(18).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 20) {
        ipD.setIcdCm20(mrDetail.getIcdCM().get(19).getCode());
      }
    }
    if (mrDetail.getIcdOP() != null) {
      if (mrDetail.getIcdOP().size() >= 1) {
        ipD.setIcdOpCode1(mrDetail.getIcdOP().get(0).getCode());
      }
      if (mrDetail.getIcdOP().size() >= 2) {
        ipD.setIcdOpCode2(mrDetail.getIcdOP().get(1).getCode());
      }
      if (mrDetail.getIcdOP().size() >= 3) {
        ipD.setIcdOpCode3(mrDetail.getIcdOP().get(2).getCode());
      }
      if (mrDetail.getIcdOP().size() >= 4) {
        ipD.setIcdOpCode4(mrDetail.getIcdOP().get(3).getCode());
      }
      if (mrDetail.getIcdOP().size() >= 5) {
        ipD.setIcdOpCode5(mrDetail.getIcdOP().get(4).getCode());
      }
      if (mrDetail.getIcdOP().size() >= 6) {
        ipD.setIcdOpCode6(mrDetail.getIcdOP().get(5).getCode());
      }
      if (mrDetail.getIcdOP().size() >= 7) {
        ipD.setIcdOpCode7(mrDetail.getIcdOP().get(6).getCode());
      }
      if (mrDetail.getIcdOP().size() >= 8) {
        ipD.setIcdOpCode8(mrDetail.getIcdOP().get(7).getCode());
      }
      if (mrDetail.getIcdOP().size() >= 9) {
        ipD.setIcdOpCode9(mrDetail.getIcdOP().get(8).getCode());
      }
      if (mrDetail.getIcdOP().size() >= 10) {
        ipD.setIcdOpCode10(mrDetail.getIcdOP().get(9).getCode());
      }
      if (mrDetail.getIcdOP().size() >= 11) {
        ipD.setIcdOpCode11(mrDetail.getIcdOP().get(10).getCode());
      }
      if (mrDetail.getIcdOP().size() >= 12) {
        ipD.setIcdOpCode12(mrDetail.getIcdOP().get(11).getCode());
      }
      if (mrDetail.getIcdOP().size() >= 13) {
        ipD.setIcdOpCode13(mrDetail.getIcdOP().get(12).getCode());
      }
      if (mrDetail.getIcdOP().size() >= 14) {
        ipD.setIcdOpCode14(mrDetail.getIcdOP().get(13).getCode());
      }
      if (mrDetail.getIcdOP().size() >= 15) {
        ipD.setIcdOpCode15(mrDetail.getIcdOP().get(14).getCode());
      }
      if (mrDetail.getIcdOP().size() >= 16) {
        ipD.setIcdOpCode16(mrDetail.getIcdOP().get(15).getCode());
      }
      if (mrDetail.getIcdOP().size() >= 17) {
        ipD.setIcdOpCode17(mrDetail.getIcdOP().get(16).getCode());
      }
      if (mrDetail.getIcdOP().size() >= 18) {
        ipD.setIcdOpCode18(mrDetail.getIcdOP().get(17).getCode());
      }
      if (mrDetail.getIcdOP().size() >= 19) {
        ipD.setIcdOpCode19(mrDetail.getIcdOP().get(18).getCode());
      }
      if (mrDetail.getIcdOP().size() >= 20) {
        ipD.setIcdOpCode20(mrDetail.getIcdOP().get(19).getCode());
      }
    }
    ipD.setApplCauseMark(mrDetail.getApplCauseMark());
    if (mrDetail.getFuncType() != null && mrDetail.getFuncType().length() > 0) {
      if (mrDetail.getFuncType().indexOf('-') > -1) {
        ipD.setFuncType(mrDetail.getFuncType().substring(0, mrDetail.getFuncType().indexOf('-')));
      } else {
        ipD.setFuncType(mrDetail.getFuncType());
      }
    }
    ipD.setUpdateAt(new java.util.Date());
    MRDetail.updateIcdpcsIP(mr, ipD);
    MRDetail.updateIcdcmOtherIP(mr, ipD);
  }

  /**
   * 
   * @param ipdId
   * @param mrDetail
   * @param saveIPP 是否儲存IPP。false:測試驗証用
   * @return true:需重算點數，false:不需重算點數
   */
  public boolean updateIPPByMrDetail(MR mr, IP_D ipd, MRDetail mrDetail, boolean saveIPP) {
    boolean result = false;
    List<IP_P> ippList = ippDao.findByIpdIdOrderByOrderSeqNo(ipd.getId());
    List<MO> moList = mrDetail.getMos();

    // 醫令總數
    int orderQty = 0;
    // 診察費點數
    int diagDot = ipd.getDiagDot().intValue();
    // System.out.println("initial diagDot=" + diagDot);
    // 病房費點數
    int roomDot = ipd.getRoomDot().intValue();
    // 管灌膳食費點數
    int mealDot = ipd.getMealDot().intValue();
    // 檢查費點數
    int aminDot = ipd.getAminDot().intValue();
    // 放射線診療費點數
    int radoDot = ipd.getRadoDot().intValue();
    // 治療處置費點數
    int thrpDot = ipd.getThrpDot().intValue();
    // 手術費點數
    int sgryDot = ipd.getSgryDot().intValue();
    // 復健治療費點數
    int phscDot = ipd.getPhscDot().intValue();
    // 血液血漿費點數
    int blodDot = ipd.getBlodDot().intValue();
    // 血液透析費點數
    int hdDot = ipd.getHdDot().intValue();
    // 麻醉費點數
    int aneDot = ipd.getAneDot().intValue();
    // 特殊材料費點數
    int metrDot = ipd.getMetrDot().intValue();
    // 藥費點數
    int drugDot = ipd.getDrugDot().intValue();
    // 藥事服務費點數
    int dsvcDot = ipd.getDsvcDot().intValue();
    // 精神科治療費點數
    int nrtpDot = ipd.getNrtpDot().intValue();
    // 注射技術費點數
    int injtDot = ipd.getInjtDot().intValue();
    // 嬰兒費點數
    int babyDot = ipd.getBabyDot().intValue();
    int applDot = 0;

    List<IP_P> notFoundList = new ArrayList<IP_P>();
    for (IP_P ipp : ippList) {
      int index = -1;
      for (int i = 0; i < moList.size(); i++) {
        MO mo = moList.get(i);
        if (mo.getOrderSeqNo().intValue() == ipp.getOrderSeqNo().intValue()) {
          int applStatus = mo.getApplStatus();
          boolean dirty = false;
          // 確認是同一筆醫令
          index = i;
          dirty = updateIpp(ipp, mo);
          if (dirty) {
            if (saveIPP) {
              ippDao.save(ipp);
            }
            if (ipp.getApplStatus().intValue() != applStatus
                && (ipp.getApplStatus().intValue() == 1 || applStatus == 1)) {
              // 申報狀態有變，且有申報改為不申報或不申報改為有申報
              result = true;
            }
          }
        }
        if (index < 0) {
          continue;
        }
        // if ("2".equals(ipp.getPayCodeType())) {
        // System.out.println("index=" + index + "," + ipp.getApplStatus() + "," +
        // ipp.getOrderCode() + "," + ipp.getTotalDot());
        // }

        if (ipp.getApplStatus().intValue() == 1) {
          orderQty++;
          // System.out.println(ipp.getOrderCode() + "-" + orderQty +"," + mo.getOrderSeqNo());
          if ("4".equals(ipd.getCaseType())) {
            // 試辦計畫
            if ("9".equals(ipd.getPayType())) {
              // 呼吸照護
              // if (ORDER_TYPE.TREAT.value().equals(ipp.getOrderType())) {
              if (!ORDER_TYPE.NO_PAY.value().equals(ipp.getOrderType())) {
                applDot += ipp.getTotalDot().intValue();
              }
            } else {
              if (ipp.getOrderCode().startsWith("P")) {
                thrpDot -= ipp.getTotalDot().intValue();
                break;
              }
            }
          } else if ("9".equals(ipd.getPayType())) {
            // 呼吸照護
            if (!ORDER_TYPE.NO_PAY.value().equals(ipp.getOrderType())) {
              applDot += ipp.getTotalDot().intValue();
            }
          }
          if ("2".equals(ipp.getPayCodeType())) {
            // && ORDER_TYPE.NO_PAY.value().equals(ipp.getOrderType())) {
            // 2 為住院診察費
            diagDot -= ipp.getTotalDot().intValue();
            // System.out.println("diagDot = " + diagDot + ","+ ipp.getOrderCode() + "," +
            // ipp.getTotalDot() );
          } else if (ORDER_TYPE.NO_MED.value().equals(ipp.getOrderType())) {
            // 不計入醫療費用點數合計欄位項目

          } else if ("3".equals(ipp.getPayCodeType())) {
            // 3 為病房費
            roomDot -= ipp.getTotalDot().intValue();
          } else if ("5".equals(ipp.getPayCodeType())) {
            // 5 為管灌飲食費及營養照護費
            mealDot -= ipp.getTotalDot().intValue();
          } else if ("8".equals(ipp.getPayCodeType())) {
            // 8 為放射線診療費
            radoDot -= ipp.getTotalDot().intValue();
          } else if ("12".equals(ipp.getPayCodeType())) {
            // 12 為復健治療費
            phscDot -= ipp.getTotalDot().intValue();
          } else if ("14".equals(ipp.getPayCodeType())) {
            // 12 為輸血及骨髓移植費
            blodDot -= ipp.getTotalDot().intValue();
          } else if ("16".equals(ipp.getPayCodeType())) {
            // 16 為麻醉費
            aneDot -= ipp.getTotalDot().intValue();
          } else if ("17".equals(ipp.getPayCodeType())) {
            // 17 為手術費
            sgryDot -= ipp.getTotalDot().intValue();
          } else if ("18".equals(ipp.getPayCodeType()) || "15".equals(ipp.getPayCodeType())) {
            // 18 為治療處置費, 15為石膏繃帶費
            if (ipp.getOrderCode().startsWith("58")) {
              // 58 開頭醫令為血液透析費
              hdDot -= ipp.getTotalDot().intValue();
            } else {
              thrpDot -= ipp.getTotalDot().intValue();
            }
          } else if ("19".equals(ipp.getPayCodeType())) {
            // 19 為特定診療檢查費
            aminDot -= ipp.getTotalDot().intValue();
          } else if (ORDER_TYPE.METERIAL.value().equals(ipp.getOrderType())) {
            // 特殊材料費點數
            metrDot -= ipp.getTotalDot().intValue();
          } else if (ORDER_TYPE.DRUG.value().equals(ipp.getOrderType())) {
            // 藥費點數
            drugDot -= ipp.getTotalDot().intValue();
          } else if (ipp.getOrderCode().length() == 10) {
            // 支付代碼為10碼列為藥費
            drugDot -= ipp.getTotalDot();
          } else if ("6".equals(ipp.getPayCodeType())) {
            // 6 為調劑費(藥事服務費)
            dsvcDot -= ipp.getTotalDot().intValue();
          } else if ("9".equals(ipp.getPayCodeType())) {
            // 9 為注射
            injtDot -= ipp.getTotalDot().intValue();
          } else if ("20".equals(ipp.getPayCodeType()) && ipp.getOrderCode().length() == 12
              && !ORDER_TYPE.METERIAL_OWN.value().equals(ipp.getOrderType())
              && !ORDER_TYPE.METERIAL_OWN2.value().equals(ipp.getOrderType())) {
            // 不分類且支付代碼為12碼列為特殊材料費 且非自費
            metrDot = metrDot - ipp.getTotalDot();
          } else if ("A".equals(ipd.getTwDrgsSuitMark()) && "G00001".equals(ipp.getOrderCode())) {
            applDot += ipp.getTwDrgsCalcu().intValue();
          }
        }
        break;
      }
      if (index > -1) {
        moList.remove(index);
        // System.out.println("remove " + index + ", size=" + moList.size());
      } else {
        // 有醫令被刪除
        notFoundList.add(ipp);
      }
    }
    if (ipd.getMrId() == 4290) {
      System.out.println("orderQty=" + orderQty + ", notFoundList size=" + notFoundList.size() + ", moList.size=" + moList.size());
    }
    if (notFoundList.size() > 0) {
      for (IP_P ip_P : notFoundList) {
        ippDao.deleteById(ip_P.getId());
      }
    }
    if(moList.size() > 0) {
      // 有新增的醫令
      for (MO mo : moList) {
        IP_P newIPP = mo.toIpp(codeTableService);
        if (saveIPP) {
          ippDao.save(newIPP);
          orderQty++;
        }
      }
      result = true;
    }

    if (!saveIPP) {
      result = true;
    }
    if (result) {
      if (diagDot != 0) {
        ipd.setDiagDot(ipd.getDiagDot().intValue() - diagDot);
      }
      if (roomDot != 0) {
        ipd.setRoomDot(ipd.getRoomDot().intValue() - roomDot);
      }
      if (mealDot != 0) {
        ipd.setMealDot(ipd.getMealDot().intValue() - mealDot);
      }
      if (aminDot != 0) {
        ipd.setAminDot(ipd.getAminDot().intValue() - aminDot);
      }
      if (radoDot != 0) {
        ipd.setRadoDot(ipd.getRadoDot().intValue() - radoDot);
      }
      if (thrpDot != 0) {
        ipd.setThrpDot(ipd.getThrpDot().intValue() - thrpDot);
      }
      if (sgryDot != 0) {
        ipd.setSgryDot(ipd.getSgryDot().intValue() - sgryDot);
      }
      if (phscDot != 0) {
        ipd.setPhscDot(ipd.getPhscDot().intValue() - phscDot);
      }
      if (blodDot != 0) {
        ipd.setBlodDot(ipd.getBlodDot().intValue() - blodDot);
      }
      if (hdDot != 0) {
        ipd.setHdDot(ipd.getHdDot().intValue() - hdDot);
      }
      if (aneDot != 0) {
        ipd.setAneDot(ipd.getAneDot().intValue() - aneDot);
      }
      if (metrDot != 0) {
        ipd.setMetrDot(ipd.getMetrDot().intValue() - metrDot);
      }
      if (drugDot != 0) {
        ipd.setDrugDot(ipd.getDrugDot().intValue() - drugDot);
      }
      if (dsvcDot != 0) {
        ipd.setDsvcDot(ipd.getDsvcDot().intValue() - dsvcDot);
      }
      if (nrtpDot != 0) {
        ipd.setNrtpDot(ipd.getNrtpDot().intValue() - nrtpDot);
      }
      if (injtDot != 0) {
        ipd.setInjtDot(ipd.getInjtDot().intValue() - injtDot);
      }
      if (babyDot != 0) {
        ipd.setBabyDot(ipd.getBabyDot().intValue() - babyDot);
      }
      if ("4".equals(ipd.getCaseType()) && !"9".equals(ipd.getPayType())) {
        // 試辦但非呼吸計畫
        ipd.setDiagDot(0);
        ipd.setRoomDot(0);
        ipd.setPhscDot(0);
        ipd.setAminDot(0);
        ipd.setMetrDot(0);
        ipd.setInjtDot(0);
      }
      ipd.setOrderQty(orderQty);
      ipd.calculateTotalDot();
      // System.out.println("g00001 = " + applDot + "," + ipd.getTwDrgsSuitMark());
      ipd.calculateTotalApplDot(applDot);
      if ("9".equals(ipd.getTwDrgsSuitMark())) {
        // 當欄位IDd102(不適用Tw-DRGs案件特殊註記代碼)為「9」時，本欄費用應為「0」
        ipd.setApplDot(0);
      }
      mr.setApplDot(ipd.getApplDot());
      mr.setTotalDot(ipd.getMedDot());
    }
    return result;
  }

  /**
   * 更新住院醫令IP_P
   * 
   * @param ipp
   * @param mo
   * @return true:資料有異動，false:資料無異動
   */
  private boolean updateIpp(IP_P ipp, MO mo) {
    boolean result = false;
    if (ipp.getOrderSeqNo().intValue() != mo.getOrderSeqNo().intValue()) {
      result = true;
      ipp.setOrderSeqNo(mo.getOrderSeqNo());
    }
    if ((ipp.getPayBy() != null && !ipp.getPayBy().equals(mo.getPayBy()))
        || (mo.getPayBy() != null && !mo.getPayBy().equals(ipp.getPayBy()))) {
      result = true;
      ipp.setPayBy(mo.getPayBy());
    }
    if (ipp.getApplStatus().intValue() != mo.getApplStatus().intValue()) {
      result = true;
      ipp.setApplStatus(mo.getApplStatus());
    }
    return result;
  }

  private void updateIPDByMrDetail(IP_D ipD, MRDetail mrDetail) {
    ipD.setCaseType(mrDetail.getCaseType());
    ipD.setAgencyId(mrDetail.getAgencyId());
    ipD.setAminDot(mrDetail.getAminDot());
    ipD.setAneDot(mrDetail.getAneDot());
    ipD.setApplCauseMark(mrDetail.getApplCauseMark());
    ipD.setApplDot(mrDetail.getApplDot());
    ipD.setApplStartDate(DateTool.removeSlashForChineseYear(mrDetail.getApplSDate()));
    ipD.setApplEndDate(DateTool.removeSlashForChineseYear(mrDetail.getApplEDate()));
    ipD.setBabyDot(mrDetail.getBabyDot());
    ipD.setBlodDot(mrDetail.getBlodDot());
    ipD.setCardSeqNo(mrDetail.getCardSeqNo());
    ipD.setCaseDrgCode(mrDetail.getCaseDrgCode());
    if (mrDetail.getCaseType() != null && mrDetail.getCaseType().length() > 0) {
      if (mrDetail.getCaseType().indexOf('-') > -1) {
        ipD.setCaseType(mrDetail.getCaseType().substring(0, mrDetail.getCaseType().indexOf('-')));
      } else {
        ipD.setCaseType(mrDetail.getCaseType());
      }
    }
    ipD.setChildMark(mrDetail.getChildMark());
    ipD.setDiagDot(mrDetail.getDiagDot());
    ipD.setDrugDot(mrDetail.getDrugDot());
    ipD.setDsvcDot(mrDetail.getDsvcDot());
    ipD.setEbAppl30Dot(mrDetail.getEbAppl30Dot());
    ipD.setEbAppl60Dot(mrDetail.getEbAppl60Dot());
    ipD.setEbAppl61Dot(mrDetail.getEbAppl61Dot());
    ipD.setEbedDay(mrDetail.geteBedDay());
    ipD.setEbPart30Dot(mrDetail.getEbPart30Dot());
    ipD.setEbPart60Dot(mrDetail.getEbPart60Dot());
    ipD.setEbPart61Dot(mrDetail.getEbPart61Dot());
    if (mrDetail.getFuncType() != null && mrDetail.getFuncType().length() > 0) {
      if (mrDetail.getFuncType().indexOf('-') > -1) {
        ipD.setFuncType(mrDetail.getFuncType().substring(0, mrDetail.getFuncType().indexOf('-')));
      } else {
        ipD.setFuncType(mrDetail.getFuncType());
      }
    }
    ipD.setHdDot(mrDetail.getHdDot());
    ipD.setHospId(mrDetail.getHospId());

    if (mrDetail.getIcdCM() != null) {
      if (mrDetail.getIcdCM().size() >= 1) {
        ipD.setIcdCm1(mrDetail.getIcdCM().get(0).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 2) {
        ipD.setIcdCm2(mrDetail.getIcdCM().get(1).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 3) {
        ipD.setIcdCm3(mrDetail.getIcdCM().get(2).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 4) {
        ipD.setIcdCm4(mrDetail.getIcdCM().get(3).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 5) {
        ipD.setIcdCm5(mrDetail.getIcdCM().get(4).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 6) {
        ipD.setIcdCm6(mrDetail.getIcdCM().get(5).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 7) {
        ipD.setIcdCm7(mrDetail.getIcdCM().get(6).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 8) {
        ipD.setIcdCm8(mrDetail.getIcdCM().get(7).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 9) {
        ipD.setIcdCm9(mrDetail.getIcdCM().get(8).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 10) {
        ipD.setIcdCm10(mrDetail.getIcdCM().get(9).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 11) {
        ipD.setIcdCm11(mrDetail.getIcdCM().get(10).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 12) {
        ipD.setIcdCm12(mrDetail.getIcdCM().get(11).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 13) {
        ipD.setIcdCm13(mrDetail.getIcdCM().get(12).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 14) {
        ipD.setIcdCm14(mrDetail.getIcdCM().get(13).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 15) {
        ipD.setIcdCm15(mrDetail.getIcdCM().get(14).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 16) {
        ipD.setIcdCm16(mrDetail.getIcdCM().get(15).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 17) {
        ipD.setIcdCm17(mrDetail.getIcdCM().get(16).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 18) {
        ipD.setIcdCm18(mrDetail.getIcdCM().get(17).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 19) {
        ipD.setIcdCm19(mrDetail.getIcdCM().get(18).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 20) {
        ipD.setIcdCm20(mrDetail.getIcdCM().get(19).getCode());
      }
    }

    if (mrDetail.getIcdOP() != null) {
      if (mrDetail.getIcdOP().size() >= 1) {
        ipD.setIcdOpCode1(mrDetail.getIcdOP().get(0).getCode());
      }
      if (mrDetail.getIcdOP().size() >= 2) {
        ipD.setIcdOpCode2(mrDetail.getIcdOP().get(1).getCode());
      }
      if (mrDetail.getIcdOP().size() >= 3) {
        ipD.setIcdOpCode3(mrDetail.getIcdOP().get(2).getCode());
      }
      if (mrDetail.getIcdOP().size() >= 4) {
        ipD.setIcdOpCode4(mrDetail.getIcdOP().get(3).getCode());
      }
      if (mrDetail.getIcdOP().size() >= 5) {
        ipD.setIcdOpCode5(mrDetail.getIcdOP().get(4).getCode());
      }
      if (mrDetail.getIcdOP().size() >= 6) {
        ipD.setIcdOpCode6(mrDetail.getIcdOP().get(5).getCode());
      }
      if (mrDetail.getIcdOP().size() >= 7) {
        ipD.setIcdOpCode7(mrDetail.getIcdOP().get(6).getCode());
      }
      if (mrDetail.getIcdOP().size() >= 8) {
        ipD.setIcdOpCode8(mrDetail.getIcdOP().get(7).getCode());
      }
      if (mrDetail.getIcdOP().size() >= 9) {
        ipD.setIcdOpCode9(mrDetail.getIcdOP().get(8).getCode());
      }
      if (mrDetail.getIcdOP().size() >= 10) {
        ipD.setIcdOpCode10(mrDetail.getIcdOP().get(9).getCode());
      }
      if (mrDetail.getIcdOP().size() >= 11) {
        ipD.setIcdOpCode11(mrDetail.getIcdOP().get(10).getCode());
      }
      if (mrDetail.getIcdOP().size() >= 12) {
        ipD.setIcdOpCode12(mrDetail.getIcdOP().get(11).getCode());
      }
      if (mrDetail.getIcdOP().size() >= 13) {
        ipD.setIcdOpCode13(mrDetail.getIcdOP().get(12).getCode());
      }
      if (mrDetail.getIcdOP().size() >= 14) {
        ipD.setIcdOpCode14(mrDetail.getIcdOP().get(13).getCode());
      }
      if (mrDetail.getIcdOP().size() >= 15) {
        ipD.setIcdOpCode15(mrDetail.getIcdOP().get(14).getCode());
      }
      if (mrDetail.getIcdOP().size() >= 16) {
        ipD.setIcdOpCode16(mrDetail.getIcdOP().get(15).getCode());
      }
      if (mrDetail.getIcdOP().size() >= 17) {
        ipD.setIcdOpCode17(mrDetail.getIcdOP().get(16).getCode());
      }
      if (mrDetail.getIcdOP().size() >= 18) {
        ipD.setIcdOpCode18(mrDetail.getIcdOP().get(17).getCode());
      }
      if (mrDetail.getIcdOP().size() >= 19) {
        ipD.setIcdOpCode19(mrDetail.getIcdOP().get(18).getCode());
      }
      if (mrDetail.getIcdOP().size() >= 20) {
        ipD.setIcdOpCode20(mrDetail.getIcdOP().get(19).getCode());
      }
    }

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    ipD.setIdBirthYmd(DateTool.removeSlashForChineseYear(mrDetail.getBirthday()));
    ipD.setInDate(DateTool.convertToChineseYear(sdf.format(mrDetail.getMrDate())));
    ipD.setInjtDot(mrDetail.getInjtDot());
    ipD.setIptId(getTId(mrDetail.getMrDate(), true));
    ipD.setName(mrDetail.getName());
    ipD.setPrsnId(mrDetail.getPrsnId());
    ipD.setMealDot(mrDetail.getMealDot());
    ipD.setMedDot(mrDetail.getMedDot());
    ipD.setMrId(mrDetail.getId());
    ipD.setMetrDot(mrDetail.getMetrDot());
    ipD.setNbBirthday(DateTool.removeSlashForChineseYear(mrDetail.getNbBirthday()));
    ipD.setNonApplDot(mrDetail.getNonApplDot());
    ipD.setNrtpDot(mrDetail.getNrtpDot());
    ipD.setOrderQty(mrDetail.getMos().size());
    if (mrDetail.getMrEndDate() != null) {
      ipD.setApplEndDate(DateTool.convertToChineseYear(sdf.format(mrDetail.getMrEndDate())));
    }
    ipD.setPartDot(mrDetail.getPartDot());
    if (mrDetail.getPartNo() != null && mrDetail.getPartNo().length() > 0) {
      if (mrDetail.getPartNo().indexOf('-') > -1) {
        ipD.setPartNo(mrDetail.getPartNo().substring(0, mrDetail.getPartNo().indexOf('-')));
      } else {
        ipD.setPartNo(mrDetail.getPartNo());
      }
    }
    ipD.setPayType(removeDash(mrDetail.getPayType()));
    ipD.setPhscDot(mrDetail.getPhscDot());
    ipD.setPilotProject(mrDetail.getPilotProject());
    ipD.setRadoDot(mrDetail.getRadoDot());
    ipD.setRocId(mrDetail.getRocId());
    ipD.setRoomDot(mrDetail.getRoomDot());
    ipD.setSbAppl180Dot(mrDetail.getSbAppl180Dot());
    ipD.setSbAppl181Dot(mrDetail.getSbAppl181Dot());
    ipD.setSbAppl30Dot(mrDetail.getSbAppl30Dot());
    ipD.setSbAppl90Dot(mrDetail.getSbAppl90Dot());
    ipD.setSbedDay(mrDetail.getsBedDay());
    ipD.setSbPart180Dot(mrDetail.getSbPart180Dot());
    ipD.setSbPart181Dot(mrDetail.getSbPart181Dot());
    ipD.setSbPart30Dot(mrDetail.getSbPart30Dot());
    ipD.setSbPart90Dot(mrDetail.getSbPart90Dot());
    if (mrDetail.getSeqNo() != null && mrDetail.getSeqNo().length() > 0) {
      ipD.setSeqNo(Integer.parseInt(mrDetail.getSeqNo()));
    }
    ipD.setSgryDot(mrDetail.getSgryDot());
    ipD.setSvcPlan(mrDetail.getSvcPlan());
    ipD.setThrpDot(mrDetail.getThrpDot());
    ipD.setTranInHospId(mrDetail.getTranInHospId());
    ipD.setTranOutHospId(mrDetail.getTranOutHospId());

    if (mrDetail.getTranCode() != null && mrDetail.getTranCode().length() > 0) {
      if (mrDetail.getTranCode().indexOf('-') > -1) {
        ipD.setTranCode(mrDetail.getTranCode().substring(0, mrDetail.getTranCode().indexOf('-')));
      } else {
        ipD.setTranCode(mrDetail.getTranCode());
      }
    }
    ipD.setTwDrgCode(mrDetail.getTwDrgCode());
    if (mrDetail.getTwDrgPayType() != null && mrDetail.getTwDrgPayType().length() > 0) {
      if (mrDetail.getTwDrgPayType().indexOf('-') > -1) {
        ipD.setTwDrgPayType(
            mrDetail.getTwDrgPayType().substring(0, mrDetail.getTwDrgPayType().indexOf('-')));
      } else {
        ipD.setTwDrgPayType(mrDetail.getTwDrgPayType());
      }
    }
    ipD.setTwDrgsSuitMark(mrDetail.getTwDrgsSuitMark());
    ipD.setUpdateAt(new java.util.Date());
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
    String codes = "ICD10-CM".equals(category) ? StringUtility.formatICD(code) : code;
    List<JsonSuggestion> query = redis.query(category, codes, false);
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

  public String newMrNote(MrNotePayload note, String mrId, boolean isDelete) {
    MR_NOTE mn = note.toDB(Long.parseLong(mrId));
    if (isDelete) {
      Optional<MR_NOTE> optional = mrNoteDao.findById(note.getId());
      if (!optional.isPresent()) {
        return "找不到對應的備註id";
      }
      MR_NOTE oldNote = optional.get();
      oldNote.setStatus(0);
      oldNote.setUpdateAt(new java.util.Date());
      mn.setNote(oldNote.getNote());
      mn.setId(null);
      mn.setStatus(0);
    }
    mrNoteDao.save(mn);
    return null;
  }

  public MyTodoListResponse getMyTodoList(UserDetailsImpl user, java.util.Date sdate,
      java.util.Date edate, String dataFormat, String funcType, String funcTypec, String prsnId,
      String prsnName, String applId, String applName, Integer status, String orderBy, Boolean asc,
      int perPage, int page) {
    MyTodoListResponse result = new MyTodoListResponse();

    Specification<MY_MR> spec = new Specification<MY_MR>() {

      private static final long serialVersionUID = 1001L;

      public Predicate toPredicate(Root<MY_MR> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicate = new ArrayList<Predicate>();
        if (ROLE_TYPE.APPL.getRole().equals(user.getRole())) {
          // 是申報人員，只撈自己的，若不是，則是看所有人的
          predicate.add(cb.equal(root.get("applUserId"), user.getId()));
        } else if (applId != null) {
          predicate.add(cb.equal(root.get("applId"), applId));
        } else if (applName != null) {
          predicate.add(cb.equal(root.get("applName"), applName));
        }
        if (sdate != null && edate != null) {
          predicate.add(cb.and(cb.lessThanOrEqualTo(root.get("endDate"), edate),
              cb.greaterThanOrEqualTo(root.get("startDate"), sdate)));
        }
        if (dataFormat != null) {
          predicate.add(cb.equal(root.get("dataFormat"), dataFormat));
        }
        if (funcType != null) {
          predicate.add(cb.equal(root.get("funcType"), funcType));
        }
        if (prsnId != null) {
          predicate.add(cb.equal(root.get("prsnId"), prsnId));
        }
        if (prsnName != null) {
          predicate.add(cb.equal(root.get("prsnName"), prsnName));
        }
        if (funcTypec != null) {
          predicate.add(cb.equal(root.get("funcTypec"), funcTypec));
        }

        if (status != null) {
          predicate.add(cb.equal(root.get("status"), status.intValue()));
        } else {
          predicate.add(cb.or(cb.equal(root.get("status"), MR_STATUS.WAIT_PROCESS.value()),
              cb.equal(root.get("status"), MR_STATUS.QUESTION_MARK.value())));
        }
        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));

        List<Order> orderList = new ArrayList<Order>();
        if (orderBy != null && asc != null) {
          if (asc.booleanValue()) {
            orderList.add(cb.asc(root.get(orderBy)));
          } else {
            orderList.add(cb.desc(root.get(orderBy)));
          }
        } else {
          orderList.add(cb.desc(root.get("startDate")));
        }
        query.orderBy(orderList);
        return query.getRestriction();
      }
    };
    result.setCount((int) myMrDao.count(spec));
    Page<MY_MR> pages = myMrDao.findAll(spec, PageRequest.of(page, perPage));
    List<MyTodoList> list = new ArrayList<MyTodoList>();
    if (pages != null && pages.getSize() > 0) {
      for (MY_MR p : pages) {
        list.add(new MyTodoList(p));
      }
    }
    result.setTotalPage(Utility.getTotalPage(result.getCount(), perPage));
    result.setData(list);
    result.setQuestionMark(getCountOfQuestionMark(user, false));
    result.setWaitProcess(getCountOfWaitProcess(user));
    return result;
  }

  public NoticeRecordResponse getNoticeRecord(UserDetailsImpl user, String applYm,
      java.util.Date sdate, java.util.Date edate, String dataFormat, String funcType,
      String funcTypec, String prsnId, String prsnName, String applId, String applName,
      String block, String orderBy, Boolean asc, int perPage, int page) {

    NoticeRecordResponse result = new NoticeRecordResponse();
    boolean isAppl = ROLE_TYPE.APPL.getRole().equals(user.getRole());
    Specification<MR_NOTICE> spec = new Specification<MR_NOTICE>() {

      private static final long serialVersionUID = 1008L;

      public Predicate toPredicate(Root<MR_NOTICE> root, CriteriaQuery<?> query,
          CriteriaBuilder cb) {

        List<Predicate> predicate = new ArrayList<Predicate>();
        if (isAppl) {
          // 是申報人員，只撈自己的，若不是，則是看所有人的
          predicate.add(cb.equal(root.get("applUserId"), user.getId()));
        }
        if (applId != null) {
          predicate.add(cb.equal(root.get("applId"), applId));
        } else if (applName != null) {
          predicate.add(cb.equal(root.get("applName"), applName));
        }
        if (applYm != null) {
          predicate.add(cb.equal(root.get("ym"), Integer.parseInt(applYm)));
        }
        if (sdate != null && edate != null) {
          predicate.add(cb.and(cb.lessThanOrEqualTo(root.get("endDate"), edate),
              cb.greaterThanOrEqualTo(root.get("startDate"), sdate)));
        }
        if (dataFormat != null) {
          predicate.add(cb.equal(root.get("dataFormat"), dataFormat));
        }
        if (funcType != null) {
          predicate.add(cb.equal(root.get("funcType"), funcType));
        }
        if (prsnId != null) {
          predicate.add(cb.equal(root.get("prsnId"), prsnId));
        }
        if (prsnName != null) {
          predicate.add(cb.equal(root.get("prsnName"), prsnName));
        }
        if (funcTypec != null) {
          predicate.add(cb.equal(root.get("funcTypec"), funcTypec));
        }
        if ("notify".equals(block)) {
          predicate.add(cb.greaterThan(root.get("noticePpl"), 0));
        } else if ("read".equals(block)) {
          predicate.add(cb.greaterThan(root.get("readedPpl"), 0));
        } else if ("unread".equals(block)) {
          predicate.add(cb.equal(root.get("readedPpl"), 0));
        }
        predicate.add(cb.equal(root.get("status"), MR_STATUS.QUESTION_MARK.value()));
        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));

        List<Order> orderList = new ArrayList<Order>();
        if (orderBy != null && asc != null) {
          if (asc.booleanValue()) {
            orderList.add(cb.asc(root.get(orderBy)));
          } else {
            orderList.add(cb.desc(root.get(orderBy)));
          }
        } else {
          orderList.add(cb.desc(root.get("startDate")));
          orderList.add(cb.desc(root.get("seq")));
        }
        query.orderBy(orderList);
        return query.getRestriction();
      }
    };
    result.setCount((int) mrNoticeDao.count(spec));
    Page<MR_NOTICE> pages = mrNoticeDao.findAll(spec, PageRequest.of(page, perPage));
    List<NoticeRecord> list = new ArrayList<NoticeRecord>();
    if (pages != null && pages.getSize() > 0) {
      for (MR_NOTICE p : pages) {
        NoticeRecord dl = new NoticeRecord(p);
        list.add(dl);
      }
    }
    result.setTotalPage(Utility.getTotalPage(result.getCount(), perPage));
    result.setData(list);

//    if (isAppl) {
//      result.setQuestionMark(myMrDao
//          .getCountByStatusAndApplUserId(MR_STATUS.QUESTION_MARK.value(), user.getId()).intValue());
//    } else {
//      result.setQuestionMark(myMrDao.getCountByStatus(MR_STATUS.QUESTION_MARK.value()).intValue());
//    }
    Specification<MY_MR> myMrSpec = getWarningSpec(user, sdate, edate, dataFormat, funcType, funcTypec,
        prsnId, prsnName, applId, applName, null, orderBy, asc, perPage, page, isAppl, 
        MR_STATUS.QUESTION_MARK.value(), false, false);

    // 疑問標示總病歷數
    result.setQuestionMark((int) myMrDao.count(myMrSpec));
    
    myMrSpec = getWarningSpec(user, sdate, edate, dataFormat, funcType, funcTypec,
        prsnId, prsnName, applId, applName, null, orderBy, asc, perPage, page, isAppl, 
        MR_STATUS.QUESTION_MARK.value(), true, false);

    result.setNoticeTimes((int) myMrDao.count(myMrSpec));
    
    myMrSpec = getWarningSpec(user, sdate, edate, dataFormat, funcType, funcTypec,
        prsnId, prsnName, applId, applName, null, orderBy, asc, perPage, page, isAppl, 
        MR_STATUS.QUESTION_MARK.value(), false, true);
    result.setReaded((int) myMrDao.count(myMrSpec));
    result.setUnread(result.getQuestionMark().intValue() - result.getReaded().intValue());
    return result;
  }

  public DoctorListResponse getDoctorList(UserDetailsImpl user, String block, String orderBy,
      Boolean asc, int perPage, int page) {
    boolean isDoctor = ROLE_TYPE.DOCTOR.getRole().equals(user.getRole())
        || ROLE_TYPE.AM_ADM.getRole().equals(user.getRole());
    DoctorListResponse result = new DoctorListResponse();

    Specification<MY_MR> spec = new Specification<MY_MR>() {

      private static final long serialVersionUID = 1002L;

      public Predicate toPredicate(Root<MY_MR> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicate = new ArrayList<Predicate>();
        if (isDoctor) {
          predicate.add(cb.like(root.get("noticeName"), "%" + user.getUsername() + "%"));
        }
        if ("read".equals(block)) {
          predicate.add(cb.like(root.get("readedName"), "%" + user.getUsername() + "%"));
        } else if ("unread".equals(block)) {
          predicate.add(cb.or(cb.notLike(root.get("readedName"), "%" + user.getUsername() + "%"),
              cb.isNull(root.get("readedName"))));
        }
        predicate.add(cb.equal(root.get("status"), MR_STATUS.QUESTION_MARK.value()));
        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));

        List<Order> orderList = new ArrayList<Order>();
        if (orderBy != null && asc != null) {
          if (asc.booleanValue()) {
            orderList.add(cb.asc(root.get(orderBy)));
          } else {
            orderList.add(cb.desc(root.get(orderBy)));
          }
        } else {
          orderList.add(cb.desc(root.get("startDate")));
        }
        query.orderBy(orderList);
        return query.getRestriction();
      }
    };
    result.setCount((int) myMrDao.count(spec));
    // result.setCount(getCountOfQuestionMark(user, isDoctor));
    Page<MY_MR> pages = myMrDao.findAll(spec, PageRequest.of(page, perPage));
    List<DoctorList> list = new ArrayList<DoctorList>();
    if (pages != null && pages.getSize() > 0) {
      for (MY_MR p : pages) {
        DoctorList dl = new DoctorList(p);
        if (p.getNoticeName() != null) {
          if (p.getReadedName() != null && ROLE_TYPE.DOCTOR.getRole().equals(user.getRole())) {
            if (p.getReadedName().indexOf(user.getUsername()) > -1) {
              dl.setReadedStatus("已讀取");
            } else {
              dl.setReadedStatus("未讀取");
            }
          }
        }
        list.add(dl);
      }
    }
    result.setTotalPage(Utility.getTotalPage(result.getCount(), perPage));
    result.setData(list);

    result.setQuestionMark(getCountOfQuestionMark(user, isDoctor));
    result.setReaded(getCountOfReaded(user, isDoctor));
    result.setUnread(result.getQuestionMark().intValue() - result.getReaded().intValue());
    return result;
  }

  /**
   * 取得待處理病歷數
   * 
   * @param user
   * @return
   */
  private int getCountOfWaitProcess(UserDetailsImpl user) {

    Specification<MY_MR> spec = new Specification<MY_MR>() {

      private static final long serialVersionUID = 1003L;

      public Predicate toPredicate(Root<MY_MR> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicate = new ArrayList<Predicate>();
        if (ROLE_TYPE.APPL.getRole().equals(user.getRole())) {
          predicate.add(cb.equal(root.get("applUserId"), user.getId()));
        }
        predicate.add(cb.equal(root.get("status"), MR_STATUS.WAIT_PROCESS.value()));
        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));
        return query.getRestriction();
      }
    };
    return (int) myMrDao.count(spec);
  }

  /**
   * 取得疑問標示病歷數
   * 
   * @param user
   * @return
   */
  private int getCountOfQuestionMark(UserDetailsImpl user, boolean isDoctor) {

    Specification<MY_MR> spec = new Specification<MY_MR>() {

      private static final long serialVersionUID = 1004L;

      public Predicate toPredicate(Root<MY_MR> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicate = new ArrayList<Predicate>();
        if (ROLE_TYPE.APPL.getRole().equals(user.getRole()) && !isDoctor) {
          predicate.add(cb.equal(root.get("applUserId"), user.getId()));
        } else if (isDoctor) {
          predicate.add(cb.like(root.get("noticeName"), "%" + user.getUsername() + "%"));
        }
        predicate.add(cb.equal(root.get("status"), MR_STATUS.QUESTION_MARK.value()));
        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));
        return query.getRestriction();
      }
    };
    return (int) myMrDao.count(spec);
  }

  /**
   * 取得疑問標示病歷數
   * 
   * @param user
   * @param readed true:已讀取，false:不分(全部)
   * @return
   */
  private int getCountOfReaded(UserDetailsImpl user, boolean readed) {

    Specification<MY_MR> spec = new Specification<MY_MR>() {

      private static final long serialVersionUID = 1005L;

      public Predicate toPredicate(Root<MY_MR> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicate = new ArrayList<Predicate>();
        if (ROLE_TYPE.DOCTOR.getRole().equals(user.getRole())
            || ROLE_TYPE.AM_ADM.getRole().equals(user.getRole())) {
          // predicate.add(cb.equal(root.get("prsnUserId"), user.getId()));
          predicate.add(cb.like(root.get("noticeName"), "%" + user.getUsername() + "%"));
          if (readed) {
            predicate.add(cb.like(root.get("readedName"), "%" + user.getUsername() + "%"));
          }
        } else {
          if (readed) {
            predicate.add(cb.isNotNull(root.get("readedName")));
          }
        }
        predicate.add(cb.equal(root.get("status"), MR_STATUS.QUESTION_MARK.value()));
        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));
        return query.getRestriction();
      }
    };
    return (int) myMrDao.count(spec);
  }

  // public String sendNotice(long mrId, UserDetailsImpl user, String doctorId) {
  // MR mr = mrDao.findById(mrId).orElse(null);
  // if (mr == null) {
  // return "病歷id:" + mrId + "不存在";
  // }
  // String[] ids = splitBySpace(doctorId);
  // if (ids == null || ids.length == 0) {
  // return "接收人員id有誤";
  // }
  //
  // String receiver = getAllUserName(ids);
  //
  // MY_MR myMr = myMrDao.findByMrId(mrId);
  // if (myMr == null) {
  // myMr = new MY_MR(mr);
  // myMr.setPrsnUserId(userService.getUserIdByName(mr.getPrsnName()));
  // myMr.setFuncTypec(codeTableService.getDesc("FUNC_TYPE", myMr.getFuncType()));
  // }
  // myMr.setApplUserId(user.getId());
  // myMr.setNoticeName(receiver);
  // myMr.setNoticePpl(ids.length);
  // myMr.setNoticeSeq(myMr.getNoticeSeq().intValue() + 1);
  // myMr.setNoticeTimes(myMr.getNoticeTimes().intValue() + 1);
  // myMr.setNoticeDate(new java.util.Date());
  // myMr.setReadedPpl(0);
  // myMr.setReadedName(null);
  // myMr.setStatus(MR_STATUS.QUESTION_MARK.value());
  // myMrDao.save(myMr);
  //
  // List<MR> mrList = new ArrayList<MR>();
  // mrList.add(mr);
  //
  // String[] receiverName = receiver.split(",");
  // List<String> receiverList = getAllUserEmail(ids);
  // String sender = user.getDisplayName() == null ? user.getUsername() : user.getDisplayName();
  // for(int i=0;i<receiverList.size();i++) {
  // sendNotic("病歷狀態確認通知", receiverList.get(i) , generateNoticeEmailContent(mrList,
  // receiverName[i+1], sender, myMr.getNoticeTimes().intValue()));
  // }
  //
  // MR_NOTICE notice = new MR_NOTICE(myMr);
  // StringBuffer sb = new StringBuffer(",");
  // for (String string : ids) {
  // sb.append(string);
  // sb.append(",");
  // }
  // notice.setReceiveUserId(sb.toString());
  // mrNoticeDao.save(notice);
  // return null;
  // }

  public String sendNotice(String mrIds, UserDetailsImpl user, String doctorId) {
    String[] mrids = StringUtility.splitBySpace(mrIds);
    if (mrids == null || mrids.length == 0) {
      return "病歷id有誤";
    }

    String[] ids = StringUtility.splitBySpace(doctorId);
    if (ids == null || ids.length == 0) {
      return "接收人員id有誤";
    }
    List<MR> mrList = new ArrayList<MR>();
    // <MR_ID, noticeTime>
    HashMap<Long, Integer> noticeTimes = new HashMap<Long, Integer>();
    String receiver = getAllUserName(ids);
    for (String s : mrids) {
      Long mrId = Long.parseLong(s);
      MR mr = mrDao.findById(mrId).orElse(null);
      if (mr == null) {
        return "病歷id:" + s + "不存在";
      }
      mrList.add(mr);
      MY_MR myMr = myMrDao.findByMrId(mrId);
      if (myMr == null) {
        myMr = new MY_MR(mr);
        myMr.setPrsnUserId(userService.getUserIdByName(mr.getPrsnName()));
        myMr.setFuncTypec(codeTableService.getDesc("FUNC_TYPE", myMr.getFuncType()));
      }
      myMr.setApplUserId(user.getId());
      myMr.setNoticeName(receiver);
      myMr.setNoticePpl(ids.length);
      myMr.setNoticeSeq(myMr.getNoticeSeq().intValue() + 1);
      myMr.setNoticeTimes(myMr.getNoticeTimes().intValue() + 1);
      noticeTimes.put(mr.getId(), myMr.getNoticeTimes());
      myMr.setNoticeDate(new java.util.Date());
      myMr.setReadedPpl(0);
      myMr.setReadedName(null);
      myMr.setStatus(MR_STATUS.QUESTION_MARK.value());
      myMrDao.save(myMr);

      MR_NOTICE notice = new MR_NOTICE(myMr);
      StringBuffer sb = new StringBuffer(",");
      for (String string : ids) {
        sb.append(string);
        sb.append(",");
      }
      notice.setReceiveUserId(sb.toString());
      mrNoticeDao.save(notice);
    }

    String[] receiverName = receiver.split(",");
    List<String> receiverList = getAllUserEmail(ids);
    String sender = user.getDisplayName() == null ? user.getUsername() : user.getDisplayName();
    for (int i = 0; i < receiverList.size(); i++) {
      sendNotic("病歷狀態確認通知", receiverList.get(i),
          generateNoticeEmailContent(mrList, receiverName[i + 1], sender, noticeTimes));
    }
    return null;
  }

  private String getAllUserName(String[] ids) {
    if (ids == null || ids.length == 0) {
      return null;
    }
    StringBuffer sb = new StringBuffer(",");
    for (String id : ids) {
      if (id == null || id.length() < 1) {
        continue;
      }
      USER user = userService.findUserById(Long.parseLong(id));
      if (user != null) {
        sb.append(user.getUsername());
        sb.append(",");
      }
    }
    if (sb.length() == 1) {
      return null;
    }
    return sb.toString();
  }

  private List<String> getAllUserEmail(String[] ids) {
    if (ids == null || ids.length == 0) {
      return null;
    }
    List<String> result = new ArrayList<String>();
    int i = 0;
    for (String id : ids) {
      USER user = userService.findUserById(Long.parseLong(id));
      if (user != null) {
        result.add(user.getEmail());
      }
    }
    return result;
  }


  private void updateMRReaded(long mrId, UserDetailsImpl user) {
    List<MR_NOTICE> list =
        mrNoticeDao.findByMrIdAndStatusAndReceiveUserIdContainingOrderByNoticeDateDesc(mrId, 0,
            "," + String.valueOf(user.getId()) + ",");
    for (int i = 0; i < list.size(); i++) {
      MR_NOTICE mn = list.get(i);
      if (mn.getReadedName() != null && mn.getReadedName().indexOf(user.getUsername()) > -1) {
        // 之前已讀取過
        continue;
      }
      if (mn.getReadedName() == null) {
        mn.setReadedName("," + user.getUsername() + ",");
      } else {
        mn.setReadedName(mn.getReadedName() + user.getUsername() + ",");
      }
      mn.setReadedPpl(mn.getReadedPpl().intValue() + 1);
//      if (mn.getNoticePpl().intValue() == mn.getReadedPpl().intValue()) {
//        mn.setStatus(1);
//      }
      mrNoticeDao.save(mn);

      if (i == 0) {
        // 最新一則
        MY_MR myMr = myMrDao.findByMrId(mrId);
        if (myMr == null) {
          System.err.println("MY_MR id:" + mrId + " not found!");
          logger.error("MY_MR id:" + mrId + " not found!");
        } else {
          myMr.setReadedName(mn.getReadedName());
          myMr.setReadedPpl(mn.getReadedPpl());
          myMrDao.save(myMr);
        }
      }
    }
  }

  public WarningOrderResponse getWarningOrderList(UserDetailsImpl user, java.util.Date sdate,
      java.util.Date edate, String dataFormat, String funcType, String funcTypec, String prsnId,
      String prsnName, String applId, String applName, String block, String orderBy, Boolean asc,
      int perPage, int page) {
    WarningOrderResponse result = new WarningOrderResponse();

    // 是否為申報人員，若是則只撈自己的，若不是，則是看所有人的
    boolean isAppl = ROLE_TYPE.APPL.getRole().equals(user.getRole());
    Specification<MY_MR> spec = getWarningSpec(user, sdate, edate, dataFormat, funcType, funcTypec,
        prsnId, prsnName, applId, applName, null, orderBy, asc, perPage, page, isAppl, 
        MR_STATUS.WAIT_CONFIRM.value(), false, false);

    // 警示總病歷數
    result.setWarning((int) myMrDao.count(spec));
    spec = getWarningSpec(user, sdate, edate, dataFormat, funcType, funcTypec, prsnId, prsnName,
        applId, applName, block, orderBy, asc, perPage, page, isAppl, MR_STATUS.WAIT_CONFIRM.value(), false, false);
    // 加上block參數後的病歷數
    result.setCount((int) myMrDao.count(spec));
    Page<MY_MR> pages = myMrDao.findAll(spec, PageRequest.of(page, perPage));
    List<WarningOrder> list = new ArrayList<WarningOrder>();
    if (pages != null && pages.getSize() > 0) {
      for (MY_MR p : pages) {
        list.add(new WarningOrder(p));
      }
    }

    if (result.getWarning() > 0) {
      List<Object[]> times = (isAppl)
          ? myMrDao.getNoticeAndReadedTimes(MR_STATUS.WAIT_CONFIRM.value(), user.getId(),
              MR_STATUS.QUESTION_MARK.value(), user.getId())
          : myMrDao.getNoticeAndReadedTimes(MR_STATUS.WAIT_CONFIRM.value(),
              MR_STATUS.QUESTION_MARK.value());

      if (times != null && times.size() > 0) {
        Object[] obj = times.get(0);
        result.setNoticeTimes(((BigInteger) obj[0]).intValue());
        result.setNonNoticeTimes(result.getWarning() - result.getNoticeTimes());
      }
    }

    result.setTotalPage(Utility.getTotalPage(result.getCount(), perPage));
    result.setData(list);

    if (result.getWarning() > 0) {
      List<Object[]> countList =
          (isAppl) ? myMrDao.getWarningOrderCount(user.getId()) : myMrDao.getWarningOrderCount();
      if (countList != null && countList.size() > 0) {
        Object[] obj = countList.get(0);
        if (obj[0] instanceof BigDecimal) {
          // Maria DB
          result.setChangeIcd(((BigDecimal) obj[0]).intValue());
          result.setChangeInh(((BigDecimal) obj[1]).intValue());
          result.setChangeOrder(((BigDecimal) obj[2]).intValue());
          result.setChangeOther(((BigDecimal) obj[3]).intValue());
          result.setChangeSo(((BigDecimal) obj[4]).intValue());
        } else {
          result.setChangeIcd((int) obj[0]);
          result.setChangeInh((int) obj[1]);
          result.setChangeOrder((int) obj[2]);
          result.setChangeOther((int) obj[3]);
          result.setChangeSo((int) obj[4]);
        }
      }
    }
    return result;
  }

  public QuestionMarkResponse getQuestionMark(UserDetailsImpl user, String applYm,
      java.util.Date sdate, java.util.Date edate, String dataFormat, String funcType,
      String funcTypec, String prsnId, String prsnName, String applId, String applName,
      String block, String orderBy, Boolean asc, int perPage, int page) {

    QuestionMarkResponse result = new QuestionMarkResponse();
    // 是否為申報人員，若是則只撈自己的，若不是，則是看所有人的
    boolean isAppl = ROLE_TYPE.APPL.getRole().equals(user.getRole());

    Specification<MY_MR> spec =
        getQuestionMarkSpec(isAppl, user, applYm, sdate, edate, dataFormat, funcType, funcTypec,
            prsnId, prsnName, applId, applName, null, orderBy, asc, perPage, page, null);
    result.setQuestionMark((int) myMrDao.count(spec));

    if (block != null) {
      spec = getQuestionMarkSpec(isAppl, user, applYm, sdate, edate, dataFormat, funcType,
          funcTypec, prsnId, prsnName, applId, applName, block, orderBy, asc, perPage, page, null);
    }
    result.setCount((int) myMrDao.count(spec));
    Page<MY_MR> pages = myMrDao.findAll(spec, PageRequest.of(page, perPage));
    List<QuestionMark> list = new ArrayList<QuestionMark>();
    if (pages != null && pages.getSize() > 0) {
      for (MY_MR p : pages) {
        list.add(new QuestionMark(p));
      }
    }

    spec = getQuestionMarkSpec(isAppl, user, applYm, sdate, edate, dataFormat, funcType, funcTypec,
        prsnId, prsnName, applId, applName, "notify", orderBy, asc, perPage, page, null);
    result.setNoticeTimes((int) myMrDao.count(spec));
    result.setNonNoticeTimes(result.getQuestionMark() - result.getNoticeTimes());

    spec = getQuestionMarkSpec(isAppl, user, applYm, sdate, edate, dataFormat, funcType, funcTypec,
        prsnId, prsnName, applId, applName, "read", orderBy, asc, perPage, page, null);
    result.setReaded((int) myMrDao.count(spec));
    result.setUnread(result.getNoticeTimes().intValue() - result.getReaded().intValue());

    result.setTotalPage(Utility.getTotalPage(result.getCount(), perPage));
    result.setData(list);
    return result;
  }

  public Specification<MY_MR> getWarningSpec(UserDetailsImpl user, java.util.Date sdate,
      java.util.Date edate, String dataFormat, String funcType, String funcTypec, String prsnId,
      String prsnName, String applId, String applName, String block, String orderBy, Boolean asc,
      int perPage, int page, boolean isAppl, int status, boolean isNoticeDateNotNull, boolean isReadedPplNotZero) {
    return new Specification<MY_MR>() {

      private static final long serialVersionUID = 1006L;

      public Predicate toPredicate(Root<MY_MR> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicate = new ArrayList<Predicate>();
        if (isAppl) {
          // 是申報人員，只撈自己的，若不是，則是看所有人的
          predicate.add(cb.equal(root.get("applUserId"), user.getId()));
        }
        if (applId != null) {
          predicate.add(cb.equal(root.get("applId"), applId));
        } else if (applName != null) {
          predicate.add(cb.equal(root.get("applName"), applName));
        }
        if (sdate != null && edate != null) {
          predicate.add(cb.and(cb.lessThanOrEqualTo(root.get("endDate"), edate),
              cb.greaterThanOrEqualTo(root.get("startDate"), sdate)));
        }
        if (dataFormat != null) {
          predicate.add(cb.equal(root.get("dataFormat"), dataFormat));
        }
        if (funcType != null) {
          predicate.add(cb.equal(root.get("funcType"), funcType));
        }
        if (prsnId != null) {
          predicate.add(cb.equal(root.get("prsnId"), prsnId));
        }
        if (prsnName != null) {
          predicate.add(cb.equal(root.get("prsnName"), prsnName));
        }
        if (funcTypec != null) {
          predicate.add(cb.equal(root.get("funcTypec"), funcTypec));
        }
        if ("icd".equals(block)) {
          predicate.add(cb.greaterThan(root.get("changeIcd"), 0));
        } else if ("order".equals(block)) {
          predicate.add(cb.greaterThan(root.get("changeOrder"), 0));
        } else if ("inh".equals(block)) {
          predicate.add(cb.greaterThan(root.get("changeInh"), 0));
        } else if ("so".equals(block)) {
          predicate.add(cb.greaterThan(root.get("changeSo"), 0));
        } else if ("other".equals(block)) {
          predicate.add(cb.greaterThan(root.get("changeOther"), 0));
        } else if ("notify".equals(block)) {
          predicate.add(cb.greaterThan(root.get("noticeTimes"), 0));
        } else if ("nonnotify".equals(block)) {
          predicate.add(cb.equal(root.get("noticeTimes"), 0));
        }
        
        if (isNoticeDateNotNull) {
          predicate.add(cb.isNotNull(root.get("noticeDate")));
        }
        if (isReadedPplNotZero) {
          predicate.add(cb.greaterThan(root.get("readedPpl"), 0));
        }
        predicate.add(cb.equal(root.get("status"), status));
        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));

        List<Order> orderList = new ArrayList<Order>();
        if (orderBy != null && asc != null) {
          if (asc.booleanValue()) {
            orderList.add(cb.asc(root.get(orderBy)));
          } else {
            orderList.add(cb.desc(root.get(orderBy)));
          }
        } else {
          orderList.add(cb.desc(root.get("startDate")));
        }
        query.orderBy(orderList);
        return query.getRestriction();
      }
    };
  }

  public Specification<MY_MR> getQuestionMarkSpec(boolean isAppl, UserDetailsImpl user,
      String applYm, java.util.Date sdate, java.util.Date edate, String dataFormat, String funcType,
      String funcTypec, String prsnId, String prsnName, String applId, String applName,
      String block, String orderBy, Boolean asc, int perPage, int page, Boolean isReaded) {
    return new Specification<MY_MR>() {

      private static final long serialVersionUID = 1006L;

      public Predicate toPredicate(Root<MY_MR> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicate = new ArrayList<Predicate>();
        if (isAppl) {
          // 是申報人員，只撈自己的，若不是，則是看所有人的
          predicate.add(cb.equal(root.get("applUserId"), user.getId()));
        }
        if (applId != null) {
          predicate.add(cb.equal(root.get("applId"), applId));
        } else if (applName != null) {
          predicate.add(cb.equal(root.get("applName"), applName));
        }
        if (applYm != null) {
          predicate.add(cb.equal(root.get("ym"), Integer.parseInt(applYm)));
        }
        if (sdate != null && edate != null) {
          predicate.add(cb.and(cb.lessThanOrEqualTo(root.get("endDate"), edate),
              cb.greaterThanOrEqualTo(root.get("startDate"), sdate)));
        }
        if (dataFormat != null) {
          predicate.add(cb.equal(root.get("dataFormat"), dataFormat));
        }
        if (funcType != null) {
          predicate.add(cb.equal(root.get("funcType"), funcType));
        }
        if (prsnId != null) {
          predicate.add(cb.equal(root.get("prsnId"), prsnId));
        }
        if (prsnName != null) {
          predicate.add(cb.equal(root.get("prsnName"), prsnName));
        }
        if (funcTypec != null) {
          predicate.add(cb.equal(root.get("funcTypec"), funcTypec));
        }
        if ("notify".equals(block)) {
          predicate.add(cb.greaterThan(root.get("noticeTimes"), 0));
          if (isReaded != null)
            if (isReaded.booleanValue()) {
              predicate.add(cb.greaterThan(root.get("readedPpl"), 0));
            } else {
              predicate.add(cb.equal(root.get("readedPpl"), 0));
            }
        } else if ("notnotify".equals(block)) {
          predicate.add(cb.equal(root.get("noticeTimes"), 0));
        } else if ("read".equals(block)) {
          predicate.add(cb.greaterThan(root.get("readedPpl"), 0));
        } else if ("unread".equals(block)) {
          predicate.add(cb.greaterThan(root.get("noticeTimes"), 0));
          predicate.add(cb.equal(root.get("readedPpl"), 0));
        }
        predicate.add(cb.equal(root.get("status"), MR_STATUS.QUESTION_MARK.value()));
        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));

        List<Order> orderList = new ArrayList<Order>();
        if (orderBy != null && asc != null) {
          if (asc.booleanValue()) {
            orderList.add(cb.asc(root.get(orderBy)));
          } else {
            orderList.add(cb.desc(root.get(orderBy)));
          }
        } else {
          orderList.add(cb.desc(root.get("startDate")));
        }
        query.orderBy(orderList);
        return query.getRestriction();
      }
    };
  }

  public MRDetail addMRDetail(MRDetail mrDetail) {
    MRDetail result = null;

    // Optional<MR> optional = mrDao.findById(mrDetail.getId());
    // if (optional.isPresent()) {
    // result = new MRDetail(optional.get());
    // result.setError("id:" + mrDetail.getId() + " 已存在");
    // return result;
    // }
    MRDetail old = getExistMRFromOtherServer(mrDetail.getId().toString());
    mrDetail.setFuncType(codeTableService.getCodeByDesc("FUNC_TYPE", mrDetail.getFuncType()));
    MR mr = new MR();
    result = updateMrDetail(old, mrDetail);

    if ("住院".equals(mrDetail.getDataFormat())) {
      mrDetail.setDataFormat(XMLConstant.DATA_FORMAT_IP);
    } else if ("門診".equals(mrDetail.getDataFormat())) {
      mrDetail.setDataFormat(XMLConstant.DATA_FORMAT_OP);
    }
    mr.updateMR(result);
    if (result.getMrDate() != null) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
      String ym = sdf.format(result.getMrDate());
      mr.setApplYm(DateTool.convertToChineseYear(ym));
    }

    mr = mrDao.save(mr);
    if (mrDetail.getMos() == null || mrDetail.getMos().size() == 0) {
      mrDetail.setMos(old.getMos());
    }

    StringBuffer sb = new StringBuffer(",");
    if (XMLConstant.DATA_FORMAT_OP.equals(mrDetail.getDataFormat())) {
      // OP_D opD = opdDao.getOne(mr.getdId());
      OP_D opD = new OP_D();
      opD.setMrId(mr.getId());
      opD.setOptId(getTId(mrDetail.getMrDate(), false));
      updateOPDByMrDetail(opD, result);
      opD = opdDao.save(opD);
      mr.setdId(opD.getId());
      mr.setDataFormat(XMLConstant.DATA_FORMAT_OP);
      mr.setIcdcm1(opD.getIcdCm1());
      MRDetail.updateIcdcmOtherOP(mr, opD);
      MRDetail.updateIcdpcsOP(mr, opD);
      MRDetail.updateIcdAll(mr);

      if (mrDetail.getMos() != null) {
        for (int i = 0; i < mrDetail.getMos().size(); i++) {
          OP_P opp = mrDetail.getMos().get(i).toOpp(codeTableService);
          opp.setMrId(mr.getId());
          opp.setOpdId(opD.getId());
          oppDao.save(opp);

          if (opp.getDrugNo() != null) {
            sb.append(opp.getDrugNo());
            sb.append(",");
          }
        }
        if (sb.length() > 1) {
          mr.setCodeAll(sb.toString());
        }
      }
    } else if (XMLConstant.DATA_FORMAT_IP.equals(mrDetail.getDataFormat())) {
      IP_D ipD = new IP_D();
      updateIPDByMrDetail(ipD, result);
      ipD.setMrId(mr.getId());
      ipD = ipdDao.save(ipD);
      mr.setDataFormat(XMLConstant.DATA_FORMAT_IP);
      mr.setdId(ipD.getId());
      if (mrDetail.getMos() != null) {
        for (int i = 0; i < mrDetail.getMos().size(); i++) {
          IP_P ipp = mrDetail.getMos().get(i).toIpp(codeTableService);
          ipp.setMrId(mr.getId());
          ipp.setIpdId(ipD.getId());
          ippDao.save(ipp);

          if (ipp.getOrderCode() != null) {
            sb.append(ipp.getOrderCode());
            sb.append(",");
          }
        }
        if (sb.length() > 1) {
          mr.setCodeAll(sb.toString());
        }
      }
    }
    mr = mrDao.save(mr);
    result = new MRDetail(mr);
    return result;
  }

  public OP_P convertMoToOpp(MO mo) {
    OP_P result = new OP_P();

    return result;
  }

  public MRDetail getExistMRFromOtherServer(String mrId) {
    SendHTTP send = new SendHTTP();
    send.setServerIP("10.10.5.30");
    send.setPort("8081");
    String token = send.loginUser("applc", "leadtek");
    String response = send.getAPI(token, "/nhixml/mr/" + mrId);
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      return objectMapper.readValue(response, MRDetail.class);
    } catch (JsonMappingException e) {
      e.printStackTrace();
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return null;
  }

  private MRDetail updateMrDetail(MRDetail old, MRDetail newMR) {
    MRDetail result = old;
    result.setRocId(newMR.getRocId());
    result.setName(newMR.getName());
    result.setPrsnId(newMR.getPrsnId());
    if (result.getPrsnId() != null) {
      USER user = userService.findUser(result.getPrsnId());
      if (user != null) {
        result.setPrsnName(user.getDisplayName());
      }
    }
    result.setMrDate(newMR.getMrDate());
    result.setMrEndDate(newMR.getMrEndDate());
    result.setStatus(newMR.getStatus());
    result.setTotalDot(newMR.getTotalDot());
    result.setApplDot(newMR.getApplDot());
    result.setOwnExpense(newMR.getOwnExpense());
    result.setDrgCode(newMR.getDrgCode());
    result.setDrgFixed(newMR.getDrgFixed());
    result.setDrgSection(newMR.getDrgSection());
    if (newMR.getIcdCM() != null) {
      result.setIcdCM(newMR.getIcdCM());
    }
    return result;
  }

  public List<DEDUCTED_NOTE> getDeductedNote(long mrId, boolean ignoreDeleted) {
    List<DEDUCTED_NOTE> notes = new ArrayList<DEDUCTED_NOTE>();
    List<DEDUCTED_NOTE> list = deductedNoteDao.findByMrIdOrderById(mrId);
    for (DEDUCTED_NOTE note : list) {
      if (ignoreDeleted && note.getStatus().intValue() == 0) {
        continue;
      } else {
        String record = null;
        if (note.getStatus().intValue() == 1) {
          record = "新增核刪代碼" + note.getCode();
          note.setModifyStatus("新增");
        } else if (note.getStatus().intValue() == 2) {
          record = "修改核刪代碼" + note.getCode();
          note.setModifyStatus("修改");
        } else if (note.getStatus().intValue() == 3) {
          record = "刪除核刪代碼" + note.getCode();
          note.setModifyStatus("刪除");
        }
        note.setRecord(record);
      }
      notes.add(note);
    }
    return notes;
  }

  public DeductedNoteListResponse getDeductedNoteResponse(long mrId, boolean ignoreDeleted) {
    DeductedNoteListResponse result = new DeductedNoteListResponse();
    result.setData(getDeductedNote(mrId, ignoreDeleted));
    return result;
  }

  public String updateDeductedNote(DEDUCTED_NOTE note) {
    if (note.getId() == null || note.getId() < 1) {
      return "核刪註記id" + note.getId() + "不存在";
    }
    DEDUCTED_NOTE db = deductedNoteDao.findById(note.getId()).orElse(null);
    if (db == null) {
      return "核刪註記id" + note.getId() + "不存在";
    }
    note.setMrId(db.getMrId());
    note.setUpdateAt(new java.util.Date());
    note.setActionType(ACTION_TYPE.MODIFIED.value());
    note.setStatus(1);
    deductedNoteDao.save(note);
    return null;
  }

  public String deleteDeductedNote(String username, long noteId) {
    DEDUCTED_NOTE db = deductedNoteDao.findById(noteId).orElse(null);
    if (db == null) {
      return "核刪註記id:" + noteId + "不存在";
    }
    db.setEditor(username);
    db.setStatus(0);
    db.setActionType(ACTION_TYPE.DELETED.value());
    db.setUpdateAt(new java.util.Date());
    deductedNoteDao.save(db);
    parameters.deleteCodeConflictForHighRisk(db);
    return null;
  }

  public String newDeductedNote(String mrId, DEDUCTED_NOTE note) {
    MR mr = null;
    try {
      mr = mrDao.findById(Long.parseLong(mrId)).orElse(null);
      if (mr == null) {
        return "病歷id" + mrId + "不存在";
      }
    } catch (NumberFormatException e) {
      return "病歷id" + mrId + "有誤";
    }
    note.setMrId(Long.parseLong(mrId));
    note.setStatus(1);
    note.setUpdateAt(new java.util.Date());
    deductedNoteDao.save(note);
    parameters.upsertCodeConflictForHighRisk(mr.getIcdcm1(), note.getDeductedOrder(),
        mr.getDataFormat());
    return null;
  }

  public static void initialOP_DDot(OP_D opd) {
    if (opd.getDrugDot() == null) {
      opd.setDrugDot(0);
    }
    if (opd.getTreatDot() == null) {
      opd.setTreatDot(0);
    }
    if (opd.getMetrDot() == null) {
      opd.setMetrDot(0);
    }
    if (opd.getDiagDot() == null) {
      opd.setDiagDot(0);
    }
    if (opd.getDsvcDot() == null) {
      opd.setDsvcDot(0);
    }
    if (opd.getTotalDot() == null) {
      opd.setTotalDot(0);
    }
    if (opd.getTotalApplDot() == null) {
      opd.setTotalApplDot(0);
    }
  }

  public String removeSlash(String s) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < s.length(); i++) {
      if (s.charAt(i) == '/') {
        continue;
      }
      if (s.charAt(i) == ' ') {
        continue;
      }
      if (s.charAt(i) == ':') {
        continue;
      }
      sb.append(s.charAt(i));
    }
    return sb.toString();
  }

  private int[] stringToIntArray(String s, String splitter) {
    if (s == null || s.length() < 1) {
      return new int[0];
    }
    if (s.indexOf(splitter) < 0) {
      int[] result = new int[1];
      result[0] = Integer.parseInt(s);
      return result;
    }
    String[] ss = s.split(splitter);
    int[] result = new int[ss.length];
    for (int i = 0; i < ss.length; i++) {
      result[i] = Integer.parseInt(ss[i]);
    }
    return result;
  }

  private boolean isIntegerInArrayOrIgnore(int[] a, int value) {
    if (a == null || a.length == 0) {
      return true;
    }
    for (int i = 0; i < a.length; i++) {
      if (a[i] == value) {
        return true;
      }
    }
    return false;
  }

  public MRCountResponse getHomePageMRCount(HomepageParameters hp) {
    MRCountResponse result = new MRCountResponse();

    String originalDataFormat = hp.getDataFormat();
    if (originalDataFormat == null || XMLConstant.DATA_FORMAT_OP.equals(hp.getDataFormat())) {
      hp.setDataFormat(XMLConstant.DATA_FORMAT_OP);
      updateHomePageMRCountByDataFormat(result.getOp(), hp);
      updateMRStatusCount(result.getOp(), hp);
    }
    if (originalDataFormat == null || XMLConstant.DATA_FORMAT_IP.equals(hp.getDataFormat())) {
      hp.setDataFormat(XMLConstant.DATA_FORMAT_IP);
      updateHomePageMRCountByDataFormat(result.getIp(), hp);
      updateMRStatusCount(result.getIp(), hp);
    }

    result.refreshAll();
    return result;
  }

  private void updateHomePageMRCountByDataFormat(MRCount mc, HomepageParameters hp) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Tuple> query = cb.createTupleQuery();
    Root<MR> root = query.from(MR.class);

    List<Predicate> predicates = getHomePageCountPredicate(cb, query, root, hp);

    mc.setDayCount(getCountingDays(cb, query, root, predicates, hp));
    mc.setTotalMr(getMRCount(cb, query, root, predicates, hp));

    // predicates.add(cb.greaterThan(root.get("applDot"), 0));
    retrieveMRApplyCount(cb, query, root, predicates, hp, mc);
  }

  private void updateMRStatusCount(MRCount mc, HomepageParameters hp) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Tuple> query = cb.createTupleQuery();
    Root<MR> root = query.from(MR.class);

    List<Tuple> tuples = groupByStatus(hp);
    for (Tuple tuple : tuples) {
      int value = (tuple.get(1) instanceof Long) ? ((Long) tuple.get(1)).intValue()
          : ((Integer) tuple.get(1)).intValue();
      if (((Integer) tuple.get(0)).intValue() == MR_STATUS.CLASSIFIED.value()) {
        mc.setClassified(value);
      } else if (((Integer) tuple.get(0)).intValue() == MR_STATUS.DONT_CHANGE.value()) {
        mc.setDontChange(value);
      } else if (((Integer) tuple.get(0)).intValue() == MR_STATUS.NO_CHANGE.value()) {
        mc.setNoChange(value);
      } else if (((Integer) tuple.get(0)).intValue() == MR_STATUS.OPTIMIZED.value()) {
        mc.setOptimized(value);
      } else if (((Integer) tuple.get(0)).intValue() == MR_STATUS.QUESTION_MARK.value()) {
        mc.setQuestionMark(value);
      } else if (((Integer) tuple.get(0)).intValue() == MR_STATUS.WAIT_CONFIRM.value()) {
        mc.setWaitConfirm(value);
      } else if (((Integer) tuple.get(0)).intValue() == MR_STATUS.WAIT_PROCESS.value()) {
        mc.setWaitProcess(value);
      }
    }
  }

  private List<Predicate> getHomePageCountPredicate(CriteriaBuilder cb, CriteriaQuery<Tuple> query,
      Root<MR> root, HomepageParameters hp) {
    List<Predicate> predicate = new ArrayList<Predicate>();
    if (hp.getApplYM() != null) {
      addPredicate(root, predicate, cb, "applYm", hp.getApplYM(), true, false, false);
    } else {
      predicate.add(cb.between(root.get("mrDate"), hp.getsDate(), hp.geteDate()));
    }
    addPredicate(root, predicate, cb, "dataFormat", hp.getDataFormat(), false, false, false);
    if (!"00".equals(hp.getFuncType())) {
      addPredicate(root, predicate, cb, "funcType", hp.getFuncType(), false, false, false);
    }
    addPredicate(root, predicate, cb, "prsnName", hp.getPrsnName(), false, false, false);
    return predicate;
  }

  public List<Tuple> groupByStatus(HomepageParameters hp) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Tuple> query = cb.createTupleQuery();
    Root<MR> root = query.from(MR.class);

    query.select(cb.tuple(root.get("status"), cb.count(root)));
    List<Predicate> predicate = getHomePageCountPredicate(cb, query, root, hp);
    Predicate[] pre = new Predicate[predicate.size()];
    query.where(predicate.toArray(pre)).groupBy(root.get("status"));
    TypedQuery<Tuple> typedQuery = em.createQuery(query);
    return typedQuery.getResultList();
  }

  /**
   * 取得申請日數
   * 
   * @param cb
   * @param query
   * @param predicate
   * @param hp
   * @return
   */
  private int getCountingDays(CriteriaBuilder cb, CriteriaQuery<Tuple> query, Root<MR> root,
      List<Predicate> predicate, HomepageParameters hp) {

    // query.select(cb.tuple(cb.max(root.get("mrDate")), cb.min(root.get("mrDate"))));
    // Predicate[] pre = new Predicate[predicate.size()];
    // query.where(predicate.toArray(pre));
    // TypedQuery<Tuple> typedQuery = em.createQuery(query);
    // List<Tuple> list = typedQuery.getResultList();
    // Tuple values = list.get(0);
    // if (values.get(0) == null) {
    // 該時段無資料
    if (hp.getApplYM() != null) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
      try {
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(String.valueOf(Integer.parseInt(hp.getApplYM()) + 191100)));
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        java.util.Date firstDayOfMonth = cal.getTime();
        cal.add(Calendar.MONTH, 1);
        java.util.Date lastDayOfMonth = cal.getTime();
        return (int) ((lastDayOfMonth.getTime() - firstDayOfMonth.getTime()) / 86400000L);
      } catch (ParseException e) {
        e.printStackTrace();
      }
    } else if (hp.geteDate() != null && hp.getsDate() != null) {
      return (int) ((hp.geteDate().getTime() - hp.getsDate().getTime()) / 86400000L) + 1;
    }
    // ipMR.setDayCount((int) ((eDate.getTime() - sDate.getTime()) / 86400000L) + 1);
    return 0;
    // }
    // java.util.Date maxDate = (java.util.Date) values.get(0);
    // java.util.Date minDate = (java.util.Date) values.get(1);
    // long diff = (maxDate.getTime() - minDate.getTime()) / 86400000;
    // return (int) diff + 1;
  }

  /**
   * 取得病例數
   * 
   * @param root
   * @param cb
   * @param query
   * @param predicate
   * @param hp
   * @return
   */
  private int getMRCount(CriteriaBuilder cb, CriteriaQuery<Tuple> query, Root<MR> root,
      List<Predicate> predicate, HomepageParameters hp) {
    query.select(cb.tuple(cb.count(root.get("id"))));
    Predicate[] pre = new Predicate[predicate.size()];
    query.where(predicate.toArray(pre));

    TypedQuery<Tuple> typedQuery = em.createQuery(query);
    List<Tuple> list = typedQuery.getResultList();
    Tuple values = list.get(0);
    if (values.get(0) == null) {
      return 0;
    }
    if (values.get(0) instanceof Long) {
      return ((Long) values.get(0)).intValue();
    } else {
      return ((Integer) values.get(0)).intValue();
    }
  }

  /**
   * 取得申請件數及申請點數
   * 
   * @param root
   * @param cb
   * @param query
   * @param predicate
   * @param hp
   * @return
   */
  private void retrieveMRApplyCount(CriteriaBuilder cb, CriteriaQuery<Tuple> query, Root<MR> root,
      List<Predicate> predicate, HomepageParameters hp, MRCount mrCount) {
    // SELECT COUNT(ID), SUM(APPL_DOT) FROM MR WHERE ...
    query.select(cb.tuple(cb.count(root.get("id")), cb.sumAsLong(root.get("applDot"))));
    Predicate[] pre = new Predicate[predicate.size()];
    query.where(predicate.toArray(pre));

    TypedQuery<Tuple> typedQuery = em.createQuery(query);
    List<Tuple> list = typedQuery.getResultList();
    Tuple values = list.get(0);
    if (values.get(0) == null) {
      mrCount.setApplSum(0);
      mrCount.setApplDot(0);
      return;
    }
    if (values.get(0) instanceof Long) {
      mrCount.setApplSum(((Long) values.get(0)).intValue());
    } else {
      mrCount.setApplSum(((Integer) values.get(0)).intValue());
    }
    if (values.get(1) == null) {
      mrCount.setApplDot(0);
    } else {
      mrCount.setApplDot(((Long) values.get(1)).intValue());
    }
  }

  public DrgListPayload getDrgList(long idL) {
    DrgListPayload result = new DrgListPayload();

    List<IP_D> ipd = ipdDao.findByMrId(idL);
    if (ipd == null || ipd.size() == 0) {
      result.setMessage("病歷 id:" + idL + " 不存在");
      result.setResult(BaseResponse.ERROR);
    }
    List<DRG_CAL> list = drgCalDao.findByMrIdAndErrorIsNull(idL);
    if (list == null || list.size() == 0) {
      result.setMessage("無drg記錄");
      return result;
    }
    List<DrgCalPayload> data = new ArrayList<DrgCalPayload>();
    for (DRG_CAL drgCal : list) {
      DrgCalPayload drg = new DrgCalPayload(drgCal);

      if (drg.getIcdCM1().equals(ipd.get(0).getIcdCm1())) {
        drg.setSelected(true);
      } else {
        drg.setSelected(false);
      }
      data.add(drg);
    }
    result.setData(data);
    return result;
  }

  public String updateDrgList(long idL, String icd) {
    List<IP_D> ipdList = ipdDao.findByMrId(idL);
    if (ipdList == null || ipdList.size() == 0) {
      return "病歷 id:" + idL + " 不存在";
    }
    Optional<MR> optional = mrDao.findById(idL);
    if (!optional.isPresent()) {
      return "病歷 id:" + idL + " 不存在";
    }
    MR mr = optional.get();
    IP_D ipd = ipdList.get(0);
    if (ipd.getIcdCm1().equals(icd)) {
      // 主診斷未變，不處理
      return null;
    }
    List<DRG_CAL> list = drgCalDao.findByMrIdAndErrorIsNull(idL);
    if (list == null || list.size() == 0) {
      return "無drg記錄";
    }
    List<DrgCalPayload> data = new ArrayList<DrgCalPayload>();
    for (DRG_CAL drgCal : list) {
      if (drgCal.getIcdCM1().equals(icd)) {
        String tmp = ipd.getIcdCm1();
        ipd.setIcdCm1(icd);
        if (ipd.getIcdCm2().equals(icd)) {
          ipd.setIcdCm2(tmp);
        } else if (ipd.getIcdCm3().equals(icd)) {
          ipd.setIcdCm3(tmp);
        } else if (ipd.getIcdCm4().equals(icd)) {
          ipd.setIcdCm4(tmp);
        } else if (ipd.getIcdCm5().equals(icd)) {
          ipd.setIcdCm5(tmp);
        } else if (ipd.getIcdCm6().equals(icd)) {
          ipd.setIcdCm6(tmp);
        } else if (ipd.getIcdCm7().equals(icd)) {
          ipd.setIcdCm7(tmp);
        } else if (ipd.getIcdCm8().equals(icd)) {
          ipd.setIcdCm8(tmp);
        } else if (ipd.getIcdCm9().equals(icd)) {
          ipd.setIcdCm9(tmp);
        } else if (ipd.getIcdCm10().equals(icd)) {
          ipd.setIcdCm10(tmp);
        }
        ipd.setTwDrgCode(drgCal.getDrg());
        ipd.setApplDot(drgCal.getDrgDot());
        ipd.setUpdateAt(new java.util.Date());
        mr.setDrgCode(drgCal.getDrg());
        mr.setDrgFixed(drgCal.getDrgFix());
        mr.setDrgSection(drgCal.getDrgSection());
        mr.setApplDot(ipd.getApplDot());
        mr.setUpdateAt(new java.util.Date());
        ipdDao.save(ipd);
        mrDao.save(mr);
        break;
      }
    }
    return null;
  }

  public void updateDrgList(String icd, IP_D ipd, MR mr) {
    List<DRG_CAL> list = drgCalDao.findByMrIdAndErrorIsNull(mr.getId());
    if (list == null || list.size() == 0) {
      return;
    }
    for (DRG_CAL drgCal : list) {
      if (drgCal.getIcdCM1().equals(icd)) {
        ipd.setIcdCm1(icd);
        ipd.setTwDrgCode(drgCal.getDrg());
        ipd.setApplDot(drgCal.getDrgDot());
        ipd.setUpdateAt(new java.util.Date());
        mr.setDrgCode(drgCal.getDrg());
        mr.setDrgFixed(drgCal.getDrgFix());
        mr.setDrgSection(drgCal.getDrgSection());
        mr.setApplDot(ipd.getApplDot());
        mr.setUpdateAt(new java.util.Date());
        return;
      }
    }
  }

  public String generateNoticeEmailContent(List<MR> list, String username, String senderName,
      HashMap<Long, Integer> noticeTimes) {
    SimpleDateFormat sdf = new SimpleDateFormat(DateTool.SDF);

    StringBuffer sb = new StringBuffer(username);
    sb.append("您好：<p>");
    sb.append(senderName).append("通知您以下就醫紀錄有待確認項目，請再登入系統查看註記欄位中詳細資訊。<p>");
    sb.append("共有").append(list.size()).append("筆資料：<br>");
    sb.append(
        "<table class=\"MsoTableGrid\" border=1 cellspacing=0 cellpadding=5 style='border-collapse:collapse;border:none;mso-border-alt:solid'><tr style='border:solid'>");
    sb.append(
        "<th>就醫日期-起</th><th>就醫日期-訖</th><th>就醫記錄編號</th><th>科別代碼</th><th>科別</th><th>醫護代碼</th><th>醫護名</th><th>負責人員</th><th>通知次數</th></tr>");
    for (MR mr : list) {
      sb.append("<tr style='border:solid'><td>");
      sb.append(sdf.format(mr.getMrDate()));
      sb.append("</td><td>");
      if (mr.getMrEndDate() != null) {
        sb.append(sdf.format(mr.getMrEndDate()));
      } else {
        sb.append(sdf.format(mr.getMrDate()));
      }
      sb.append("</td><td><a href=\"");
      sb.append(serverUrl);
      if (XMLConstant.DATA_FORMAT_OP.equals(mr.getDataFormat())) {
        sb.append("outpatient-order/");
      } else {
        sb.append("inpatient-order/");
      }
      sb.append(mr.getId());
      sb.append("\">");
      if (mr.getInhClinicId() != null && mr.getInhClinicId().length() > 0) {
        sb.append(mr.getInhClinicId());
      } else {
        sb.append(mr.getId());
      }
      sb.append("</a></td><td>");
      sb.append(mr.getFuncType());
      sb.append("</td><td>");
      sb.append(codeTableService.getDesc("FUNC_TYPE", mr.getFuncType()));
      sb.append("</td><td>");
      sb.append(mr.getPrsnId());
      sb.append("</td><td>");
      sb.append(mr.getPrsnName());
      sb.append("</td><td>");
      sb.append(mr.getApplName());
      sb.append("</td><td>");
      sb.append(noticeTimes.get(mr.getId()));
      sb.append("</td></tr>");
    }
    sb.append("</table>");
    return sb.toString();
  }

  public void sendNotic(String subject, String receiverMail, String content) {
    Thread thread = new Thread(new Runnable() {
      public void run() {
        emailService.sendMail(subject, receiverMail, content);
      }
    });
    thread.start();
  }

  private void clearFileDiff(Long mrId) {
    diffDao.deleteByMrId(mrId);
    moDao.deleteByMrId(mrId);
  }
}
