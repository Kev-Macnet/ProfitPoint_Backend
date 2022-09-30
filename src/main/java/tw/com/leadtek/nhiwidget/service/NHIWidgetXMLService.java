/**
 * Created on 2021/1/25.
 */
package tw.com.leadtek.nhiwidget.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.assertj.core.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.jsonwebtoken.Claims;
import tw.com.leadtek.nhiwidget.constant.ACTION_TYPE;
import tw.com.leadtek.nhiwidget.constant.INTELLIGENT_REASON;
import tw.com.leadtek.nhiwidget.constant.LogType;
import tw.com.leadtek.nhiwidget.constant.MR_STATUS;
import tw.com.leadtek.nhiwidget.constant.ORDER_TYPE;
import tw.com.leadtek.nhiwidget.constant.ROLE_TYPE;
import tw.com.leadtek.nhiwidget.constant.XMLConstant;
import tw.com.leadtek.nhiwidget.dao.CODE_TABLEDao;
import tw.com.leadtek.nhiwidget.dao.DEDUCTED_NOTEDao;
import tw.com.leadtek.nhiwidget.dao.DRG_CALDao;
import tw.com.leadtek.nhiwidget.dao.FILE_DIFFDao;
import tw.com.leadtek.nhiwidget.dao.FILE_DOWNLOADDao;
import tw.com.leadtek.nhiwidget.dao.IP_DDao;
import tw.com.leadtek.nhiwidget.dao.IP_PDao;
import tw.com.leadtek.nhiwidget.dao.IP_TDao;
import tw.com.leadtek.nhiwidget.dao.MODao;
import tw.com.leadtek.nhiwidget.dao.MRDao;
import tw.com.leadtek.nhiwidget.dao.MR_NOTEDao;
import tw.com.leadtek.nhiwidget.dao.MR_NOTICEDao;
import tw.com.leadtek.nhiwidget.dao.MR_SODao;
import tw.com.leadtek.nhiwidget.dao.MY_MRDao;
import tw.com.leadtek.nhiwidget.dao.OP_DDao;
import tw.com.leadtek.nhiwidget.dao.OP_PDao;
import tw.com.leadtek.nhiwidget.dao.OP_TDao;
import tw.com.leadtek.nhiwidget.dao.PAY_CODEDao;
import tw.com.leadtek.nhiwidget.model.CodeBase;
import tw.com.leadtek.nhiwidget.model.CompareWarning;
import tw.com.leadtek.nhiwidget.model.JsonSuggestion;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_TABLE;
import tw.com.leadtek.nhiwidget.model.rdb.DEDUCTED_NOTE;
import tw.com.leadtek.nhiwidget.model.rdb.DHead;
import tw.com.leadtek.nhiwidget.model.rdb.DRG_CAL;
import tw.com.leadtek.nhiwidget.model.rdb.FILE_DIFF;
import tw.com.leadtek.nhiwidget.model.rdb.FILE_DOWNLOAD;
import tw.com.leadtek.nhiwidget.model.rdb.IP;
import tw.com.leadtek.nhiwidget.model.rdb.IP_D;
import tw.com.leadtek.nhiwidget.model.rdb.IP_DData;
import tw.com.leadtek.nhiwidget.model.rdb.IP_P;
import tw.com.leadtek.nhiwidget.model.rdb.IP_T;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.model.rdb.MR_NOTE;
import tw.com.leadtek.nhiwidget.model.rdb.MR_NOTICE;
import tw.com.leadtek.nhiwidget.model.rdb.MR_SO;
import tw.com.leadtek.nhiwidget.model.rdb.MY_MR;
import tw.com.leadtek.nhiwidget.model.rdb.OP;
import tw.com.leadtek.nhiwidget.model.rdb.OP_D;
import tw.com.leadtek.nhiwidget.model.rdb.OP_DData;
import tw.com.leadtek.nhiwidget.model.rdb.OP_P;
import tw.com.leadtek.nhiwidget.model.rdb.OP_T;
import tw.com.leadtek.nhiwidget.model.rdb.PAY_CODE;
import tw.com.leadtek.nhiwidget.model.rdb.USER;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;
import tw.com.leadtek.nhiwidget.payload.DeductedNoteListResponse;
import tw.com.leadtek.nhiwidget.payload.MO;
import tw.com.leadtek.nhiwidget.payload.MRCount;
import tw.com.leadtek.nhiwidget.payload.MRCountResponse;
import tw.com.leadtek.nhiwidget.payload.MRDetail;
import tw.com.leadtek.nhiwidget.payload.MRResponse;
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
import tw.com.leadtek.nhiwidget.payload.my.SecondFilterParameter;
import tw.com.leadtek.nhiwidget.payload.my.WarningOrder;
import tw.com.leadtek.nhiwidget.payload.my.WarningOrderResponse;
import tw.com.leadtek.nhiwidget.security.jwt.JwtUtils;
import tw.com.leadtek.nhiwidget.security.service.UserDetailsImpl;
import tw.com.leadtek.nhiwidget.service.pt.ViolatePaymentTermsService;
import tw.com.leadtek.nhiwidget.sql.ExportCSVDao;
import tw.com.leadtek.tools.DateTool;
import tw.com.leadtek.tools.ExcelUtil;
import tw.com.leadtek.tools.StringUtility;
import tw.com.leadtek.tools.Utility;

@Service
public class NHIWidgetXMLService {

  private Logger logger = LogManager.getLogger();

  public final static String DOCTYPE = "<?xml version=\"1.0\" encoding=\"Big5\"?>\n";
  
  /**
   * 申報狀態為自費
   */
  public final static int APPL_STATUS_OE = 5;
  
  /**
   * 申報狀態為本月申報
   */
  public final static int APPL_STATUS_THIS_MONTH = 1;
  
  public final static String THESE_OPP = "門急診醫令清單";
  
  public final static String THESE_OPD = "註記";
  
  public final static String THESE_IPP = "住院醫令清單";
  
  public final static String THESE_IPD = "病 床 號 碼";
  
  /**
   * 健保不計價申報 (費用轉為部份負擔)
   */
  public final static String PAY_BY_H = "H";
  
  /**
   * 健保病人額外自費，如用較好的藥或衛材
   */
  public final static String PAY_BY_Y = "Y";
  
  /**
   * 自費病人自費,不申報
   */
  public final static String PAY_BY_Z = "Z";
  
  /**
   * 自費院內碼列為部分負擔，而不算自費
   */
  public final static String[] OWN_EXPENSE_TO_PART = new String[] {"REG1", "REGEMG"};
  
  // 有新申報檔匯入後最快 3 分鐘開始跑檢查條件及報表 
  private final int CHECK_ALL_AFTER_UPLOAD_WAIT_SECOND = 180;

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
  private CodeTableService cts;

  @Autowired
  private MY_MRDao myMrDao;

  @Autowired
  private MR_NOTICEDao mrNoticeDao;

  @Autowired
  private ParametersService parameters;

  @Autowired
  private LogDataService logService;

  @Autowired
  private LogOperateService logOperateService;
  
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

  @Autowired
  private PAY_CODEDao payCodeDao;
  
  @Autowired
  private ViolatePaymentTermsService vpts;
  
  @Autowired
  private ExportCSVDao exportCSVDao;
  
  @Autowired
  private MR_SODao mrSoDao;
  
  @Autowired
  private ReportService reportService;
  
  @Value("${project.serverUrl}")
  private String serverUrl;

  @Value("${project.hospId}")
  private String hospId;
  
  public final static String FILE_PATH = "download";

  @Autowired
  private HttpServletRequest httpServletReq;
  
  @Autowired
  private FILE_DOWNLOADDao fdDao;
  
  /**
   * 儲存xml申報檔-門診
   * @param op
   * @param fd
   * @return 匯入筆數
   */
  public int saveOP(OP op, FILE_DOWNLOAD fd) {
    OP_T opt = saveOPT(op.getTdata());
    // 避免重複insert
    List<HashMap<String, Object>> opdList = getOPDByOPTID(opt.getId());
    List<HashMap<String, Object>> oppList = getOPPByOPTID(opt.getId());
    List<OP_DData> dDataList = op.getDdata();
    List<OP_P> oppBatch = new ArrayList<OP_P>();

    if (dDataList == null) {
      return 0;
    }
    int result = dDataList.size();
    fd.setRecord(result);
    HashMap<String, String> payCodeType = getPayCodeType();
    CompareWarning cw = new CompareWarning(parameters.getByCat("COMPARE_WARNING"), cts);

    int count = 0;
    for (OP_DData op_dData : dDataList) {
      count++;
      if (((count * 100) / result) % 5 == 0) {
        fd.setProgress((count * 100) / result);
        fd.setUpdateAt(new java.util.Date());
        fdDao.save(fd);
      }
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
          if ((mr.getStatus().intValue() != MR_STATUS.NO_CHANGE.value() 
              && mr.getStatus().intValue() != MR_STATUS.WAIT_CONFIRM.value())
              && shouldCompareWarning(mr, cw, opd.getFuncType())) {
            diffList = new ArrayList<FILE_DIFF>();
            clearFileDiff(mr.getId());
            checkDiffOpdCureItem(diffList, opd);
          }
          mr.updateMR(opd, diffList, cts);
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
      findDiffOpd(diffList, mr, opd);
      mr.setdId(opd.getId());
      mrDao.updateDid(opd.getId(), mr.getId());
      StringBuffer sb = new StringBuffer(",");
      if (oppListXML != null) {
        if (diffList == null) {
          for (OP_P opp : oppListXML) {
            maskOPP(opp, opd.getCaseType());
            if (opp.getDrugNo() != null) {
              sb.append(opp.getDrugNo());
              sb.append(",");
              opp.setPayCodeType(payCodeType.get(opp.getDrugNo()));
            }
            opp.setOpdId(opd.getId());
            updateOPPID(oppList, opp);
            opp.setMrId(mr.getId());
            oppBatch.add(opp);
            if (oppBatch.size() > XMLConstant.BATCH) {
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
            for (int j = 0; j < oppListXML.size(); j++) {
              OP_P oppNew = oppListXML.get(j);
              maskOPP(oppNew, opd.getCaseType());
              if (compareOPP(mr.getId(), oppOld, oppNew, diffList, moList)) {
                isFound = true;
                break;
              }
            }
            if (!isFound) {
              addDiff(
                  mr.getId(),
                  null,
                  oppOld.getOrderSeqNo().intValue(),
                  (OP_P) null,
                  diffList,
                  moList);
            }
          }
          if (oppListXML.size() > opps.size()) {
            for (int i = opps.size(); i < oppListXML.size(); i++) {
              OP_P opp = oppListXML.get(i);
              addDiff(
                  mr.getId(),
                  opp.getDrugNo(),
                  opp.getOrderSeqNo().intValue(),
                  opp,
                  diffList,
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
      }
      saveDiffList(diffList, mr);
      mrDao.save(mr);
    }
    if (oppBatch.size() > 0) {
      oppDao.saveAll(oppBatch);
    }
    return result;
  }

  /**
   * 儲存xml申報檔-住院
   * @param ip
   * @param fd
   * @return 匯入筆數
   */
  public int saveIP(IP ip, FILE_DOWNLOAD fd) {
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
      return 0;
    }
    int result = ip.getDdata().size();
    fd.setRecord(result);
    CompareWarning cw = new CompareWarning(parameters.getByCat("COMPARE_WARNING"), cts);

    HashMap<String, String> payCodeType = getPayCodeType();
    List<IP_P> ippBatch = new ArrayList<IP_P>();
    
    int count = 0;
      
    for (IP_DData ip_dData : ip.getDdata()) {
      count++;
      if (((count * 100) / result) % 5 == 0) {
        fd.setProgress((count * 100) / result);
        fd.setUpdateAt(new java.util.Date());
        fdDao.save(fd);
      }

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
          if ((mr.getStatus().intValue() != MR_STATUS.NO_CHANGE.value() 
              && mr.getStatus().intValue() != MR_STATUS.WAIT_CONFIRM.value())
              && shouldCompareWarning(mr, cw, ipd.getFuncType())) {
            diffList = new ArrayList<FILE_DIFF>();
            clearFileDiff(mr.getId());
            checkDiffIpdItem(diffList, ipd);
          }
          mr.updateMR(ipd, diffList, cts);
          if (diffList != null && diffList.size() > 0) {
            mr.setChangeOther(1);
          }
        } else {
          mr = new MR(ipd);
          mr.setStatus(MR_STATUS.NO_CHANGE.value());
        }
      } else {
        ipd.setOwnExpense(0);
        mr = new MR(ipd);
        mr.setStatus(MR_STATUS.NO_CHANGE.value());
      }
      mr.setApplYm(ipt.getFeeYm());
      findDiffIpd(diffList, mr, ipd);

      StringBuffer sb = new StringBuffer(",");
      // 自費金額
      int ownExpense = 0;
      if (diffList == null) {
        for (IP_P ipp : ippListXML) {
          maskIPP(ipp);
          if (ipp.getOrderCode() != null) {
            sb.append(ipp.getOrderCode());
            sb.append(",");
            ipp.setPayCodeType(payCodeType.get(ipp.getOrderCode()));
          }
          ipp.setIpdId(ipd.getId());
          updateIPPID(ippList, ipp);
          ipp.setMrId(mr.getId());
          // E:自費特材項目-未支付
          if ("E".equals(ipp.getOrderType())) {
            ownExpense += ipp.getTotalDot();
            // Y:自費計價
            ipp.setPayBy("Y");
          } else {
            // N:健保計價申報 
            ipp.setPayBy("N");
          }
          ippBatch.add(ipp);
          if (ippBatch.size() > XMLConstant.BATCH) {
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
              if ("E".equals(ippNew.getOrderType())) {
                ownExpense += ippNew.getTotalDot();
              }
              break;
            }
          }
          if (!isFound) {
            addDiff(mr.getId(), null, ippOld.getOrderSeqNo().intValue(), (IP_P) null, diffList,
                moList);
            mr.setChangeOrder(1);
          }
        }
        if (ippListXML.size() > ipps.size()) {
          for (int i = ipps.size(); i < ippListXML.size(); i++) {
            IP_P ipp = ippListXML.get(i);
            addDiff(mr.getId(), ipp.getOrderCode(), ipp.getOrderSeqNo().intValue(), ipp, diffList,
                moList);
            mr.setChangeOrder(1);
            // E:自費特材項目-未支付
            if ("E".equals(ipp.getOrderType())) {
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
      saveDiffList(diffList, mr);
      if (ownExpense > 0) {
        int totalDot = ownExpense;
        if (ipd.getMedDot() != null) {
          totalDot += ipd.getMedDot().intValue();
        }
        if (ipd.getNonApplDot() != null) {
          totalDot += ipd.getNonApplDot().intValue();
        }
        mr.setTotalDot(totalDot); 
      }
      mr.setOwnExpense(ownExpense);
      ipd.setOwnExpense(ownExpense);
      mrDao.save(mr);
    }
    if (ippBatch.size() > 0) {
      ippDao.saveAll(ippBatch);
    }
    return result;
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

  private boolean compareIPP(
      Long mrId, IP_P ippOld, IP_P ippNew, List<FILE_DIFF> diffList, List<MO> moList) {
    if (ippOld.getOrderSeqNo().intValue() != ippNew.getOrderSeqNo().intValue()) {
      return false;
    }
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
    } else if (ippOld.getImgSource() != null && !ippOld.getImgSource().equals(ippNew.getImgSource())
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
    } else if (ippOld.getOrderType() != null && !ippOld.getOrderType().equals(ippNew.getOrderType())
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
      // }
      // else if (ippOld.getPayBy() != null && !ippOld.getPayBy().equals(ippNew.getPayBy())
      // || (ippNew.getPayBy() != null && !ippNew.getPayBy().equals(ippOld.getPayBy()))) {
      // addDiff(mrId, "payBy", ippOld.getOrderSeqNo().intValue(), ippNew, diffList, moList);
      // }
      // else if (ippOld.getPayCodeType() != null
      // && !ippOld.getPayCodeType().equals(ippNew.getPayCodeType())
      // || (ippNew.getPayCodeType() != null
      // && !ippNew.getPayCodeType().equals(ippOld.getPayCodeType()))) {
      // addDiff(mrId, "payCodeType", ippOld.getOrderSeqNo().intValue(), ippNew, diffList,
      // moList);
    } else if (ippOld.getPayRate() != null && !ippOld.getPayRate().equals(ippNew.getPayRate())
        || (ippNew.getPayRate() != null && !ippNew.getPayRate().equals(ippOld.getPayRate()))) {
      addDiff(mrId, "payRate", ippOld.getOrderSeqNo().intValue(), ippNew, diffList, moList);
    } else if (ippOld.getPreNo() != null && !ippOld.getPreNo().equals(ippNew.getPreNo())
        || (ippNew.getPreNo() != null && !ippNew.getPreNo().equals(ippOld.getPreNo()))) {
      addDiff(mrId, "preNo", ippOld.getOrderSeqNo().intValue(), ippNew, diffList, moList);
    } else if (ippOld.getPrsnId() != null && !ippOld.getPrsnId().equals(ippNew.getPrsnId())
        || (ippNew.getPrsnId() != null && !ippNew.getPrsnId().equals(ippOld.getPrsnId()))) {
      addDiff(mrId, "prsnId", ippOld.getOrderSeqNo().intValue(), ippNew, diffList, moList);
    } else if (ippOld.getStartTime() != null && !ippOld.getStartTime().equals(ippNew.getStartTime())
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
    return true;
  }

  /**
   * IP_P 比對有差異，加到 FILE_DIFF 及 MO_DIFF 2 張 table
   * 
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
    // 因 seqNo 由 1 開始，所以要 - 1
    fd.setArrayIndex(seqNo - 1);
    diffList.add(fd);
    if (ippNew != null) {
      MO mo = new MO();
      mo.setMrId(mrId);
      mo.setIPPData(ippNew, cts);
      moList.add(mo);
    }
  }

  /**
   * OP_P 比對有差異，加到 FILE_DIFF 及 MO_DIFF 2 張 table
   * 
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
    // 因 seqNo 由 1 開始，所以要 - 1
    fd.setArrayIndex(seqNo - 1);
    diffList.add(fd);
    if (oppNew != null) {
      MO mo = new MO();
      mo.setMrId(mrId);
      mo.setOPPData(oppNew, cts);
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
      } else if ((oppOld.getCommHospId() != null
          && !oppOld.getCommHospId().equals(oppNew.getCommHospId()))
          || (oppNew.getCommHospId() != null
              && !oppNew.getCommHospId().equals(oppOld.getCommHospId()))) {
        addDiff(mrId, "commHospId", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      } else if ((oppOld.getCurePath() != null
          && !oppOld.getCurePath().equals(oppNew.getCurePath()))
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
      } else if ((oppOld.getDrugPath() != null
          && !oppOld.getDrugPath().equals(oppNew.getDrugPath()))
          || (oppNew.getDrugPath() != null && !oppNew.getDrugPath().equals(oppOld.getDrugPath()))) {
        addDiff(mrId, "drugPath", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      } else if ((oppOld.getDrugSerialNo() != null
          && !oppOld.getDrugSerialNo().equals(oppNew.getDrugSerialNo()))
          || (oppNew.getDrugSerialNo() != null
              && !oppNew.getDrugSerialNo().equals(oppOld.getDrugSerialNo()))) {
        addDiff(mrId, "drugSerialNo", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      } else if ((oppOld.getDrugUse() != null && oppNew.getDrugUse() != null)
          && (oppOld.getDrugUse().doubleValue() != oppNew.getDrugUse().doubleValue())) {
        addDiff(mrId, "drugUse", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      } else if ((oppOld.getEndTime() != null && !oppOld.getEndTime().equals(oppNew.getEndTime()))
          || (oppNew.getEndTime() != null && !oppNew.getEndTime().equals(oppOld.getEndTime()))) {
        addDiff(mrId, "endTime", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      } else if ((oppOld.getFuncType() != null
          && !oppOld.getFuncType().equals(oppNew.getFuncType()))
          || (oppNew.getFuncType() != null && !oppNew.getFuncType().equals(oppOld.getFuncType()))) {
        addDiff(mrId, "funcType", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      } else if ((oppOld.getImgSource() != null
          && !oppOld.getImgSource().equals(oppNew.getImgSource()))
          || (oppNew.getImgSource() != null
              && !oppNew.getImgSource().equals(oppOld.getImgSource()))) {
        addDiff(mrId, "imgSource", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      } else if ((oppOld.getInhCode() != null && !oppOld.getInhCode().equals(oppNew.getInhCode()))
          || (oppNew.getInhCode() != null && !oppNew.getInhCode().equals(oppOld.getInhCode()))) {
        addDiff(mrId, "", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      } else if ((oppOld.getMedType() != null && !oppOld.getMedType().equals(oppNew.getMedType()))
          || (oppNew.getMedType() != null && !oppNew.getMedType().equals(oppOld.getMedType()))) {
        addDiff(mrId, "medType", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      } else if ((oppOld.getNonListMark() != null
          && !oppOld.getNonListMark().equals(oppNew.getNonListMark()))
          || (oppNew.getNonListMark() != null
              && !oppNew.getNonListMark().equals(oppOld.getNonListMark()))) {
        addDiff(mrId, "nonListMark", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      } else if ((oppOld.getNonListName() != null
          && !oppOld.getNonListName().equals(oppNew.getNonListName()))
          || (oppNew.getNonListName() != null
              && !oppNew.getNonListName().equals(oppOld.getNonListName()))) {
        addDiff(mrId, "nonListName", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      } else if ((oppOld.getOrderType() != null
          && !oppOld.getOrderType().equals(oppNew.getOrderType()))
          || (oppNew.getOrderType() != null
              && !oppNew.getOrderType().equals(oppOld.getOrderType()))) {
        addDiff(mrId, "orderType", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      } else if ((oppOld.getOwnExpMtrNo() != null
          && !oppOld.getOwnExpMtrNo().equals(oppNew.getOwnExpMtrNo()))
          || (oppNew.getOwnExpMtrNo() != null
              && !oppNew.getOwnExpMtrNo().equals(oppOld.getOwnExpMtrNo()))) {
        addDiff(mrId, "ownExpMtrNo", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      } else if ((oppOld.getPayRate() != null && !oppOld.getPayRate().equals(oppNew.getPayRate()))
          || (oppNew.getPayRate() != null && !oppNew.getPayRate().equals(oppOld.getPayRate()))) {
        addDiff(mrId, "payRate", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      } else if ((oppOld.getPreNo() != null && !oppOld.getPreNo().equals(oppNew.getPreNo()))
          || (oppNew.getPreNo() != null && !oppNew.getPreNo().equals(oppOld.getPreNo()))) {
        addDiff(mrId, "preNo", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      } else if ((oppOld.getPrsnId() != null && !oppOld.getPrsnId().equals(oppNew.getPrsnId()))
          || (oppNew.getPrsnId() != null && !oppNew.getPrsnId().equals(oppOld.getPrsnId()))) {
        addDiff(mrId, "prsnId", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      } else if ((oppOld.getUnitP() != null && oppNew.getUnitP() != null)
          && (oppNew.getUnitP().floatValue() != oppOld.getUnitP().floatValue())) {
        addDiff(mrId, "unitP", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      } else if ((oppOld.getStartTime() != null
          && !oppOld.getStartTime().equals(oppNew.getStartTime()))
          || (oppNew.getStartTime() != null
              && !oppNew.getStartTime().equals(oppOld.getStartTime()))) {
        addDiff(mrId, "startTime", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      } else if ((oppOld.getTotalDot() != null && oppNew.getTotalDot() != null)
          && (oppNew.getTotalDot().intValue() != oppOld.getTotalDot().intValue())) {
        addDiff(mrId, "totalDot", oppOld.getOrderSeqNo().intValue(), oppNew, diffList, moList);
      } else if ((oppOld.getTotalQ() != null && oppNew.getTotalQ() != null)
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

  /**
   * 將病歷資料隱碼並處理空字串.
   * @param opd
   */
  private void maskOPD(OP_D opd) {
    opd.setRocId(trimString(opd.getRocId()));
    opd.setName(trimString(opd.getName()));
    opd.setPharId(trimString(opd.getPharId()));
    opd.setPrsnId(trimString(opd.getPrsnId()));
    if (ISMASK) {
      opd.setRocId(StringUtility.maskString(opd.getRocId(), StringUtility.MASK_MOBILE));
      opd.setName(StringUtility.maskString(opd.getName(), StringUtility.MASK_NAME));
      opd.setPharId(StringUtility.maskString(opd.getPharId(), StringUtility.MASK_MOBILE));
      opd.setPrsnId(StringUtility.maskString(opd.getPrsnId(), StringUtility.MASK_MOBILE));
    }
    opd.setApplCauseMark(trimString(opd.getApplCauseMark()));
    opd.setCareMark(trimString(opd.getCareMark()));
    opd.setCasePayCode(trimString(opd.getCasePayCode()));
    opd.setShareMark(trimString(opd.getShareMark()));
    opd.setFuncDate(trimString(opd.getFuncDate()));
    opd.setFuncEndDate(trimString(opd.getFuncEndDate()));
    opd.setIdBirthYmd(trimString(opd.getIdBirthYmd()));
    opd.setNbBirthday(trimString(opd.getNbBirthday()));
    opd.setCureItemNo1(trimString(opd.getCureItemNo1()));
    opd.setCureItemNo2(trimString(opd.getCureItemNo2()));
    opd.setCureItemNo3(trimString(opd.getCureItemNo2()));
    opd.setCureItemNo4(trimString(opd.getCureItemNo4()));
    opd.setIcdOpCode1(trimString(opd.getIcdOpCode1()));
    opd.setIcdOpCode2(trimString(opd.getIcdOpCode2()));
    opd.setIcdOpCode3(trimString(opd.getIcdOpCode3()));
    opd.setIcdCm1(StringUtility.formatICDtoUpperCase(opd.getIcdCm1()));
    opd.setIcdCm2(StringUtility.formatICDtoUpperCase(opd.getIcdCm2()));
    opd.setIcdCm3(StringUtility.formatICDtoUpperCase(opd.getIcdCm3()));
    opd.setIcdCm4(StringUtility.formatICDtoUpperCase(opd.getIcdCm4()));
    opd.setIcdCm5(StringUtility.formatICDtoUpperCase(opd.getIcdCm5()));
    opd.setShareHospId(trimString(opd.getShareHospId()));
    opd.setTreatCode(trimString(opd.getTreatCode()));
    opd.setDsvcNo(trimString(opd.getDsvcNo()));
    opd.setOutSvcPlanCode(trimString(opd.getOutSvcPlanCode()));
    opd.setAgencyId(trimString(opd.getAgencyId()));
    opd.setChildMark(trimString(opd.getChildMark()));
    opd.setSpeAreaSvc(trimString(opd.getSpeAreaSvc()));
    opd.setSupportArea(trimString(opd.getSupportArea()));
    opd.setHospId(trimString(opd.getHospId()));
    opd.setTranInHospId(trimString(opd.getTranInHospId()));
    opd.setOriCardSeqNo(trimString(opd.getOriCardSeqNo()));
    
    if (opd.getOwnExpense() == null) {
      opd.setOwnExpense(0);
    }
  }
  
  private void maskOPP(OP_P opp, String caseType) {
    if (ISMASK) {
      opp.setPrsnId(StringUtility.maskString(opp.getPrsnId(), StringUtility.MASK_MOBILE));
    }
    if (opp.getApplStatus() == null) {
      opp.setPayBy("N");
      opp.setApplStatus(1);
    }
    opp.setDrugNo(trimString(opp.getDrugNo()));
    opp.setCurePath(trimString(opp.getCurePath()));
    opp.setChrMark(trimString(opp.getChrMark()));
    opp.setCommHospId(trimString(opp.getCommHospId()));
    opp.setDrugFre(trimString(opp.getDrugFre()));
    opp.setDrugPath(trimString(opp.getDrugPath()));
    opp.setDrugSerialNo(trimString(opp.getDrugSerialNo()));
    opp.setEndTime(trimString(opp.getEndTime()));
    opp.setImgSource(trimString(opp.getImgSource()));
    opp.setMedType(trimString(opp.getMedType()));
    opp.setNonListMark(trimString(opp.getNonListMark()));
    opp.setNonListName(trimString(opp.getNonListName()));
    opp.setOrderType(trimString(opp.getOrderType()));
    opp.setOwnExpMtrNo(trimString(opp.getOwnExpMtrNo()));
    opp.setPayBy(trimString(opp.getPayBy()));
    opp.setPayCodeType(trimString(opp.getPayCodeType()));
    opp.setPayRate(trimString(opp.getPayRate()));
    opp.setPreNo(trimString(opp.getPreNo()));
    opp.setReceiveNo(trimString(opp.getReceiveNo()));
    opp.setStartTime(trimString(opp.getStartTime()));
  }

  private void maskIPP(IP_P ipp) {
    if (ISMASK) {
      ipp.setPrsnId(StringUtility.maskString(ipp.getPrsnId(), StringUtility.MASK_MOBILE));
    }
    if (ipp.getPayBy() == null) {
      ipp.setPayBy("N");
    }
    if (ipp.getApplStatus() == null) {
      ipp.setApplStatus(1);  
    }
    if (ipp.getOrderCode() != null) {
      ipp.setOrderCode(ipp.getOrderCode().trim());
    }
  }

  /**
   * 隱碼及欄位檢查
   * @param ipd
   */
  private void maskIPD(IP_D ipd) {
    ipd.setRocId(trimString(ipd.getRocId()));
    ipd.setName(trimString(ipd.getName()));
    ipd.setPrsnId(trimString(ipd.getPrsnId()));
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
    
    ipd.setIcdOpCode1(trimString(ipd.getIcdOpCode1()));
    ipd.setIcdOpCode2(trimString(ipd.getIcdOpCode2()));
    ipd.setIcdOpCode3(trimString(ipd.getIcdOpCode3()));
    ipd.setIcdOpCode4(trimString(ipd.getIcdOpCode4()));
    ipd.setIcdOpCode5(trimString(ipd.getIcdOpCode5()));
    ipd.setIcdOpCode6(trimString(ipd.getIcdOpCode6()));
    ipd.setIcdOpCode7(trimString(ipd.getIcdOpCode7()));
    ipd.setIcdOpCode8(trimString(ipd.getIcdOpCode8()));
    ipd.setIcdOpCode9(trimString(ipd.getIcdOpCode9()));
    ipd.setIcdOpCode10(trimString(ipd.getIcdOpCode10()));
    ipd.setIcdOpCode11(trimString(ipd.getIcdOpCode11()));
    ipd.setIcdOpCode12(trimString(ipd.getIcdOpCode12()));
    ipd.setIcdOpCode13(trimString(ipd.getIcdOpCode13()));
    ipd.setIcdOpCode14(trimString(ipd.getIcdOpCode14()));
    ipd.setIcdOpCode15(trimString(ipd.getIcdOpCode15()));
    ipd.setIcdOpCode16(trimString(ipd.getIcdOpCode16()));
    ipd.setIcdOpCode17(trimString(ipd.getIcdOpCode17()));
    ipd.setIcdOpCode18(trimString(ipd.getIcdOpCode18()));
    ipd.setIcdOpCode19(trimString(ipd.getIcdOpCode19()));
    ipd.setIcdOpCode20(trimString(ipd.getIcdOpCode20()));
    if (ipd.getOwnExpense() == null) {
      ipd.setOwnExpense(0);
    }
    ipd.setOutDate(trimString(ipd.getOutDate()));
    ipd.setInDate(trimString(ipd.getInDate()));
    ipd.setApplCauseMark(trimString(ipd.getApplCauseMark()));
    ipd.setTwDrgPayType(trimString(ipd.getTwDrgPayType()));
    ipd.setTwDrgCode(trimString(ipd.getTwDrgCode()));
    ipd.setTwDrgsSuitMark(trimString(ipd.getTwDrgsSuitMark()));
    ipd.setCaseDrgCode(trimString(ipd.getCaseDrgCode()));
    ipd.setChildMark(trimString(ipd.getChildMark()));
    ipd.setNbBirthday(trimString(ipd.getNbBirthday()));
    ipd.setAgencyId(trimString(ipd.getAgencyId()));
    ipd.setTranCode(trimString(ipd.getTranCode()));
    ipd.setTranInHospId(trimString(ipd.getTranInHospId()));
    ipd.setTranOutHospId(trimString(ipd.getTranOutHospId()));
    ipd.setHospId(trimString(ipd.getHospId()));
    ipd.setSvcPlan(trimString(ipd.getSvcPlan()));
    ipd.setPilotProject(trimString(ipd.getPilotProject()));
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
      map.put("cardSeqNo", (String) obj[6]);
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
  
  private HashMap<Long, List<OP_P>> getOPPByMrIdList(List<Long> mrIdList) {
    HashMap<Long, List<OP_P>> result = new HashMap<Long, List<OP_P>>();
    List<OP_P> list = oppDao.getOppListByMrIdList(mrIdList);
    for (OP_P op_P : list) {
      List<OP_P> oppList = result.get(op_P.getMrId());
      if (oppList == null) {
        oppList = new ArrayList<OP_P>();
        result.put(op_P.getMrId(), oppList);
      }
      oppList.add(op_P);
    }
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
      // 比對生日、證號、就醫日期及就醫序號
      if (((String) map.get("birth")).equals(opd.getIdBirthYmd())
          && ((String) map.get("rocId")).equals(opd.getRocId())
          && ((String) map.get("funcDate")).equals(opd.getFuncDate())) {
        if (map.get("cardSeqNo") != null) {
          if (((String) map.get("cardSeqNo")).equals(opd.getCardSeqNo())) {
            index = i;
            opd.setId(((BigInteger) map.get("id")).longValue());
            opd.setMrId(((BigInteger) map.get("mrId")).longValue());
            if (opd.getOwnExpense() == null) {
              opd.setOwnExpense(0);
            }
            break;
          }
        } else if (opd.getCardSeqNo() == null){
          index = i;
          opd.setId(((BigInteger) map.get("id")).longValue());
          opd.setMrId(((BigInteger) map.get("mrId")).longValue());
          if (opd.getOwnExpense() == null) {
            opd.setOwnExpense(0);
          }
          break;
        }
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

      List<OP_D> opdList = opdDao.findByIDFromMR(sDate, eDate);
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
    return filterCat(list, cat);
  }

  private List<CODE_TABLE> filterCat(List<CODE_TABLE> list, String cat) {
    List<CODE_TABLE> result = new ArrayList<CODE_TABLE>();
    for (CODE_TABLE code_TABLE : list) {
      CODE_TABLE ct = new CODE_TABLE();
      ct.clone(code_TABLE);
      result.add(ct);
    }
    if ("ALL".equals(cat)) {
      return result;
    }
    for (CODE_TABLE code_TABLE : result) {
      code_TABLE.setCat(null);
    }
    return result;
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
   * @param smrp
   * @return
   */
  private Map<String, Object> fullSearchMR(SearchMRParameters smrp) {
    // PageRequest pageRequest = new PageRequest(0,10);
    boolean drg = smrp.getOnlyDRG() == null ? false : smrp.getOnlyDRG().booleanValue();
    MRCount mc = new MRCount();
    mc.setTotalMr((int) mrDao.count(getFullSearchSpec(smrp.getAll(), null, false, smrp.getOrderBy(), smrp.getAsc())));
    int iPerPage = smrp.getPerPage().intValue() < 1 ? 50 : smrp.getPerPage().intValue();
    int count = (int) mrDao.count(getFullSearchSpec(smrp.getAll(), smrp.getStatus(), drg, smrp.getOrderBy(), smrp.getAsc()));
    Page<MR> pages = mrDao.findAll(getFullSearchSpec(smrp.getAll(), smrp.getStatus(), drg, smrp.getOrderBy(), smrp.getAsc()),
        PageRequest.of(smrp.getPage().intValue(), iPerPage));

    List<MRResponse> mrList = new ArrayList<MRResponse>();
    if (pages != null && pages.getSize() > 0) {
      for (MR mrDb : pages) {
        if (mrDb.getStatus() == null) {
          mrDb.setStatus(MR_STATUS.NO_CHANGE.value());
        }
        mrList.add(new MRResponse(mrDb, cts));
      }
    }
    updateMRStatusCountAll(mc, smrp.getAll());
    Map<String, Object> result = new HashMap<String, Object>();
    result.put("count", count);
    result.put("totalPage", Utility.getTotalPage(count, iPerPage));
    result.put("mr", mrList);
    result.put("mrStatus", mc);
    return result;
  }

  private Specification<MR> getFullSearchSpec(String all, String status, boolean isDRG, String orderBy,
      Boolean asc) {
    return new Specification<MR>() {

      private static final long serialVersionUID = 1L;

      public Predicate toPredicate(Root<MR> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicate = getFullSearchPredicate(root, query, cb, all);
        if (status != null) {
          predicate.add(cb.equal(root.get("status"), status));
        }
        if (isDRG) {
          predicate.add(cb.isNotNull(root.get("drgSection")));
        }
        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));
        
        List<Order> orderList = new ArrayList<Order>();
        if (orderBy != null && orderBy.length() > 0 && asc != null) {
          if (asc.booleanValue()) {
            orderList.add(cb.asc(root.get(orderBy)));
          } else {
            orderList.add(cb.desc(root.get(orderBy)));
          }
        } else {
          orderList.add(cb.desc(root.get("mrEndDate")));
        }
        query.orderBy(orderList);
        return query.getRestriction();
      }
    };
  }

  private List<Predicate> getFullSearchPredicate(Root<MR> root, CriteriaQuery<?> query,
      CriteriaBuilder cb, String all) {
    String[] ss = all.split(" ");

    List<Predicate> result = new ArrayList<Predicate>();
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
    Path<String> inhCode = root.get("inhCode");
    Path<String> subjective = root.get("subjective");
    Path<String> objective = root.get("objective");
    Path<String> drg = root.get("drgCode");
    for (int i = 0; i < ss.length; i++) {
      result.add(cb.or(cb.like(pathRocId, ss[i] + "%"), cb.like(pathInhMrId, ss[i] + "%"),
          cb.like(pathFuncType, ss[i] + "%"), cb.like(pathPrsnId, ss[i] + "%"),
          cb.like(pathApplId, ss[i] + "%"), cb.like(pathRemark, ss[i] + "%"),
          cb.like(pathInhClinicId, ss[i] + "%"), cb.like(pathName, ss[i] + "%"),
          cb.like(icdName, "%," + ss[i] + "%"), cb.like(codeName, "%," + ss[i] + "%"),
          cb.like(subjective, "%," + ss[i] + "%"), cb.like(objective, "%," + ss[i] + "%"),
          cb.like(drg, "%," + ss[i] + "%"), cb.like(inhCode, "%," + ss[i] + "%")));
    }
    return result;
  }

  private void updateMRStatusCount(MR mr, MRCount mc) {
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
    if (mr.getDrgSection() != null) {
      mc.setDrg(mc.getDrg() + 1);
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
        MRResponse mrr = new MRResponse(mrDb, cts);
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
      return fullSearchMR(smrp);
    }
    Map<String, Object> result = new HashMap<String, Object>();

    String originalQueryStatus = smrp.getStatus();
    smrp.setStatus(null);

    Specification<MR> spec = getMRSpecification(smrp);
    // 總病例數
    MRCount mc = new MRCount();
    mc.setTotalMr((int) mrDao.count(spec));
    // 加上點選的病歷狀態
    smrp.setStatus(originalQueryStatus);
    spec = getMRSpecification(smrp);
    int count = (int) mrDao.count(spec);
    result.put("count", count);
    Page<MR> pages = mrDao.findAll(spec,
        PageRequest.of(smrp.getPage().intValue(), smrp.getPerPage().intValue()));

    List<MRResponse> mrList = new ArrayList<MRResponse>();
    if (pages != null && pages.getSize() > 0) {
      for (MR mrDb : pages) {
        updateMRStatusCount(mrDb, mc);
        MRResponse mrr = new MRResponse(mrDb, cts);
        mrList.add(mrr);
      }
    }
    updateMRStatusCountAll(mc, smrp);

    result.put("totalPage", Utility.getTotalPage(count, smrp.getPerPage().intValue()));
    result.put("mr", mrList);
    result.put("mrStatus", mc);
    return result;
  }

  private Specification<MR> getMRSpecification(SearchMRParameters smrp) {
    return new Specification<MR>() {
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
          orderList.add(cb.desc(root.get("mrEndDate")));
        }
        query.orderBy(orderList);
        return query.getRestriction();
      }
    };
  }

  private List<Predicate> searchMRPredicate(Root<MR> root, CriteriaQuery<?> query,
      CriteriaBuilder cb, SearchMRParameters smrp) {
    List<Predicate> predicate = new ArrayList<Predicate>();
    if (smrp.getsDate() != null && smrp.geteDate() != null) {
      addSearchMrDateParameter(predicate, cb, root,  smrp.getsDate(), smrp.geteDate());
    }
    boolean notOthers = smrp.getNotOthers() == null ? false : smrp.getNotOthers().booleanValue();
    if (smrp.getPharId() != null && smrp.getPharId().length() > 0) {
      predicate.add(addSearchPharId(root, query, cb, smrp.getPharId(), notOthers));
    }
    if (smrp.getMinPoints() != null && smrp.getMaxPoints() != null) {
      if (notOthers) {
        predicate.add(cb.or(cb.lessThan(root.get("totalDot"), smrp.getMinPoints()),
            cb.greaterThan(root.get("totalDot"), smrp.getMaxPoints())));
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
      predicate.add(cb.equal(root.get("dataFormat"), "20"));
    }
    boolean notOrderCode =
        smrp.getNotOrderCode() == null ? false : smrp.getNotOrderCode().booleanValue();
    addPredicate(root, predicate, cb, "codeAll", smrp.getOrderCode(), true, false, notOrderCode);
    addPredicate(root, predicate, cb, "inhCode", smrp.getInhCode(), true, false, notOrderCode);
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
    addPredicate(root, predicate, cb, "icdAll", smrp.getIcdAll(), true, false, notIcd);
    addPredicate(root, predicate, cb, "icdcm1", smrp.getIcdCMMajor(), false, false, notIcd);
    addPredicate(root, predicate, cb, "icdcmOthers", smrp.getIcdCMSec(), true, false, notIcd);
    addPredicate(root, predicate, cb, "icdpcs", smrp.getIcdPCS(), true, false, notIcd);

    if (smrp.getDeductedCode() != null || smrp.getDeductedOrder() != null) {
      Subquery<DEDUCTED_NOTE> subquery = query.subquery(DEDUCTED_NOTE.class);
      Root<DEDUCTED_NOTE> deductedNoteRoot = subquery.from(DEDUCTED_NOTE.class);

      subquery.select(deductedNoteRoot.get("mrId")).distinct(true);
      boolean isNotDeducted =
          (smrp.getNotDeducted() != null && smrp.getNotDeducted().booleanValue());
      List<Predicate> predicateDeductedNote = new ArrayList<Predicate>();
      if (smrp.getDeductedCode() != null) {
        addPredicate(deductedNoteRoot, predicateDeductedNote, cb, "code", smrp.getDeductedCode(),
            false, false, isNotDeducted);
      }
      if (smrp.getDeductedOrder() != null) {
        addPredicate(deductedNoteRoot, predicateDeductedNote, cb, "deductedOrder",
            smrp.getDeductedOrder(), false, false, isNotDeducted);
      }
      Predicate[] pre = new Predicate[predicateDeductedNote.size()];
      subquery.where(predicateDeductedNote.toArray(pre));
      predicate.add(cb.in(root.get("id")).value(subquery));
    }
    return predicate;
  }
  
  private Predicate addSearchPharId(Root<MR> root, CriteriaQuery<?> query, CriteriaBuilder cb,
      String pharId, boolean notOthers) {
    // select * from MR where id in (select distinct(mrId) from OP_D where pharId=? )
    Subquery<OP_D> oppSubquery = query.subquery(OP_D.class);
    Root<OP_D> oppRoot = oppSubquery.from(OP_D.class);

    oppSubquery.select(oppRoot.get("mrId")).distinct(true);
    List<Predicate> oppPredicate = new ArrayList<Predicate>();
    addPredicate(oppRoot, oppPredicate, cb, "pharId", pharId, true, false, notOthers);

    Predicate[] pre = new Predicate[oppPredicate.size()];
    oppSubquery.where(oppPredicate.toArray(pre));

    return cb.in(root.get("id")).value(oppSubquery);
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
    smrp.setStatus(String.valueOf(MR_STATUS.CLASSIFIED.value()));
    result.setClassified(getMRStatusCount(smrp));
    smrp.setStatus(String.valueOf(MR_STATUS.DONT_CHANGE.value()));
    result.setDontChange(getMRStatusCount(smrp));
    smrp.setStatus(String.valueOf(MR_STATUS.NO_CHANGE.value()));
    result.setNoChange(getMRStatusCount(smrp));
    smrp.setStatus(String.valueOf(MR_STATUS.OPTIMIZED.value()));
    result.setOptimized(getMRStatusCount(smrp));
    smrp.setStatus(String.valueOf(MR_STATUS.QUESTION_MARK.value()));
    result.setQuestionMark(getMRStatusCount(smrp));
    smrp.setStatus(String.valueOf(MR_STATUS.WAIT_CONFIRM.value()));
    result.setWaitConfirm(getMRStatusCount(smrp));
    smrp.setStatus(String.valueOf(MR_STATUS.WAIT_PROCESS.value()));
    result.setWaitProcess(getMRStatusCount(smrp));
    smrp.setStatus(null);
    result.setDrg(getMRDRGCount(smrp));
    smrp.setStatus(originalStatus);
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

  private void updateMRStatusCountAll(MRCount result, String all) {
    result.setClassified(
        getFullSearchMRStatusCount(all, String.valueOf(MR_STATUS.CLASSIFIED.value()), false));
    result.setDontChange(
        getFullSearchMRStatusCount(all, String.valueOf(MR_STATUS.DONT_CHANGE.value()), false));
    result.setNoChange(
        getFullSearchMRStatusCount(all, String.valueOf(MR_STATUS.NO_CHANGE.value()), false));
    result.setOptimized(
        getFullSearchMRStatusCount(all, String.valueOf(MR_STATUS.OPTIMIZED.value()), false));
    result.setQuestionMark(
        getFullSearchMRStatusCount(all, String.valueOf(MR_STATUS.QUESTION_MARK.value()), false));
    result.setWaitConfirm(
        getFullSearchMRStatusCount(all, String.valueOf(MR_STATUS.WAIT_CONFIRM.value()), false));
    result.setWaitProcess(
        getFullSearchMRStatusCount(all, String.valueOf(MR_STATUS.WAIT_PROCESS.value()), false));
    result.setDrg(getFullSearchMRStatusCount(all, null, true));
  }

  private int getFullSearchMRStatusCount(String all, String status, boolean isDRG) {
    Specification<MR> spec = getFullSearchSpec(all, status, isDRG, null, null);
    return (int) mrDao.count(spec);
  }

  private int getMRDRGCount(SearchMRParameters smrp) {
    Specification<MR> spec = new Specification<MR>() {

      private static final long serialVersionUID = 8L;

      public Predicate toPredicate(Root<MR> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicate = searchMRPredicate(root, query, cb, smrp);
        // if (smrp.getOnlyDRG() == null || !smrp.getOnlyDRG().booleanValue()) {
        predicate.add(cb.isNotNull(root.get("drgSection")));
        predicate.add(cb.equal(root.get("dataFormat"), "20"));
        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));
        return query.getRestriction();
      }
    };
    int result = (int) mrDao.count(spec);
    return result;
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
        addPredicateStringLike(root, predicate, cb, paramName, params, isNot);
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
          addPredicateStringLike(root, predicates, cb, paramName, ss[i], isNot);
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
  
  private void addPredicateStringLike(Root<?> root, List<Predicate> predicate, CriteriaBuilder cb,
      String paramName, String params, boolean isNot) {
    if ((paramName.equals("icdAll") || paramName.equals("icdcmOthers")
        || paramName.equals("icdpcs") || paramName.equals("codeAll") || paramName.equals("inhCode"))) {
      if (isNot) {
        predicate.add(cb.notLike(root.get(paramName), "%," + params + "%"));
      } else {
        predicate.add(cb.like(root.get(paramName), "%," + params + "%"));
      }
    } else {
      if (isNot) {
        predicate.add(cb.notLike(root.get(paramName), params + "%"));
      } else {
        predicate.add(cb.like(root.get(paramName), params + "%"));
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
        result.setOPDData(opD, cts);

        List<OP_P> oppList = oppDao.findByOpdIdOrderByOrderSeqNo(opD.getId());
        List<MO> moList = new ArrayList<MO>();
        for (int i = 0; i < oppList.size(); i++) {
          OP_P opp = oppList.get(i);
          if (opp.getOrderSeqNo() == null) {
            opp.setOrderSeqNo(i+1);
          }
          MO mo = new MO();
          mo.setOPPData(opp, cts);
          moList.add(mo);
        }
        result.setMos(moList);
      } else if (XMLConstant.DATA_FORMAT_IP.equals(result.getDataFormat())) {
        IP_D ipD = ipdDao.getOne(result.getdId());
        result.setIPDData(ipD, cts);

        List<IP_P> ippList = ippDao.findByIpdIdOrderByOrderSeqNo(ipD.getId());
        List<MO> moList = new ArrayList<MO>();
        for (int i = 0; i < ippList.size(); i++) {
          IP_P ipp = ippList.get(i);
          if (ipp.getOrderSeqNo() == null) {
            ipp.setOrderSeqNo(i+1);
          }
          MO mo = new MO();
          mo.setIPPData(ipp, cts);
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
   * 
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
    mrDetail.setDiffIcdCM(new ArrayList<Integer>());
    mrDetail.setDiffIcdOP(new ArrayList<Integer>());
    mrDetail.setDiffMos(new ArrayList<Integer>());
    ArrayList<Integer> deleteIcdCM = new ArrayList<>();
    ArrayList<Integer> deleteIcdOP = new ArrayList<>();
    for (FILE_DIFF fd : diffList) {
      if ("icdCM".equals(fd.getName())) {
        checkDiffIcdCM(mrDetail, fd, isRaw, deleteIcdCM);
      } else if ("icdOP".equals(fd.getName())) {
        checkDiffIcdOP(mrDetail, fd, isRaw, deleteIcdOP);
      } else if ("mos".equals(fd.getName())) {
        mrDetail.getDiffMos().add(fd.getArrayIndex());
        if (isRaw) {
          if (fd.getNewValue() == null) {
            if (fd.getArrayIndex().intValue() < mrDetail.getMos().size()) {
              mrDetail.getMos().remove(fd.getArrayIndex().intValue());
            }
            continue;
          }
          MO newMO = null;
          for (MO mo : moList) {
            if (mo.getOrderSeqNo().intValue() == (fd.getArrayIndex().intValue() + 1)) {
              newMO = mo;
              if (XMLConstant.DATA_FORMAT_IP.equals(mrDetail.getDataFormat())) {
                newMO.convertToIPP(cts);
              } else {
                newMO.convertToOPP(cts);
              }
              break;
            }
          }
          if (newMO != null) {
            if (mrDetail.getMos() == null) {
              mrDetail.setMos(new ArrayList<MO>());
            }
            if (mrDetail.getMos().size() < (fd.getArrayIndex() + 1)) {
              mrDetail.getMos().add(newMO);
            } else {
              mrDetail.getMos().set(fd.getArrayIndex().intValue(), newMO);
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
          if (mrDetail.getCureItems().size() <= fd.getArrayIndex().intValue()) {
            mrDetail
                .getCureItems()
                .add(CodeTableService.getCodeBase(cts, "OP_CURE_ITEM", fd.getNewValue()));
          } else {
            mrDetail
                .getCureItems()
                .set(
                    fd.getArrayIndex().intValue(),
                    CodeTableService.getCodeBase(cts, "OP_CURE_ITEM", fd.getNewValue()));
          }
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
    
    if (deleteIcdCM.size() > 0) {
      // 從後面往前刪除
      Collections.sort(deleteIcdCM, Collections.reverseOrder());
      for (Integer index : deleteIcdCM) {
        if (mrDetail.getIcdCM().size() > index.intValue()) {
          mrDetail.getIcdCM().remove(index.intValue());
        }
      }
    }
    if (deleteIcdOP.size() > 0) {
      Collections.sort(deleteIcdOP, Collections.reverseOrder());
      for (Integer index : deleteIcdOP) {
        mrDetail.getIcdOP().remove(index.intValue());
      }
    }
    return true;
  }

  private void checkDiffIcdCM(
      MRDetail mrDetail, FILE_DIFF fd, boolean isRaw, ArrayList<Integer> deleteArray) {
    mrDetail.getDiffIcdCM().add(fd.getArrayIndex());
    if (isRaw) {
      if (fd.getNewValue() == null) {
        // icdCM被刪除
        deleteArray.add(fd.getArrayIndex());
      } else {
        if (mrDetail.getIcdCM().size() <= fd.getArrayIndex().intValue()) {
          mrDetail.getIcdCM().add(CodeTableService.getCodeBase(cts, "ICD10-CM", fd.getNewValue()));
        } else {
          mrDetail
              .getIcdCM()
              .set(
                  fd.getArrayIndex().intValue(),
                  CodeTableService.getCodeBase(cts, "ICD10-CM", fd.getNewValue()));
        }
      }
    }
  }
  
  private void checkDiffIcdOP( MRDetail mrDetail, FILE_DIFF fd, boolean isRaw, ArrayList<Integer> deleteArray) {
    mrDetail.getDiffIcdOP().add(fd.getArrayIndex());
    if (isRaw) {
      if (fd.getNewValue() == null) {
        // icdOP被刪除
        deleteArray.add(fd.getArrayIndex());
      } else {
        if (mrDetail.getIcdOP() == null) {
          mrDetail.setIcdOP(new ArrayList<CodeBase>());
        }
        if (mrDetail.getIcdOP().size() <= fd.getArrayIndex().intValue()) {
          mrDetail
              .getIcdOP()
              .add(CodeTableService.getCodeBase(cts, "ICD10-PCS", fd.getNewValue()));
        } else {
          mrDetail
              .getIcdOP()
              .set(
                  fd.getArrayIndex().intValue(),
                  CodeTableService.getCodeBase(cts, "ICD10-PCS", fd.getNewValue()));
        }
      }
    }
  }

  private void updateNewFieldValue(FILE_DIFF fd, MRDetail result, SimpleDateFormat sdf)
      throws ParseException {
    if ("funcType".equals(fd.getName())) {
      result.setFuncType(CodeTableService.getDesc(cts, "FUNC_TYPE", fd.getNewValue()));
    } else if ("rocId".equals(fd.getName())) {
      result.setRocId(fd.getNewValue());
    } else if ("name".equals(fd.getName())) {
      result.setName(fd.getNewValue());
    } else if ("inDate".equals(fd.getName())) {
      result.setMrDate(sdf.parse(fd.getNewValue()));
    } else if ("outDate".equals(fd.getName())) {
      result.setMrEndDate(sdf.parse(fd.getNewValue()));
    } else if ("applDot".equals(fd.getName())) {
      result.setApplDot(Integer.parseInt(fd.getNewValue()));
    } else if ("drgCode".equals(fd.getName())) {
      result.setTwDrgCode(fd.getNewValue());
      result.setDrgCode(fd.getNewValue());
    } else if ("partNo".equals(fd.getName())) {
      result.setPartNo(CodeTableService.getDesc(cts, "PART_NO", fd.getNewValue()));
    } else if ("payType".equals(fd.getName())) {
      result.setPayType(CodeTableService.getDesc(cts, "PAY_TYPE", fd.getNewValue()));
    } else if ("partDot".equals(fd.getName())) {
      result.setPartDot(Integer.parseInt(fd.getNewValue()));
    } else if ("totalDot".equals(fd.getName())) {
      result.setTotalDot(Integer.parseInt(fd.getNewValue()));
    } else if ("tDot".equals(fd.getName())) {
      result.settDot(Integer.parseInt(fd.getNewValue()));
    } 
  }

  private boolean updateDiff(MRDetail mrDetail, int oldStatus) {
    if (mrDetail.getStatus() == null) {
      return false;
    }
    if (mrDetail.getStatus().intValue() == MR_STATUS.WAIT_CONFIRM.value()
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
                if (actionId != null && !ss[i].equals(actionId.toString())) {
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
    updateMyMrStatus(id, status, mr, user.getId(), user.getUsername(), user.getDisplayName(),
        user.isApplRole());
    MRDetail mrDetail = getMRDetail(id, user, true);
    mrDetail.setStatus(status);
    if (updateDiff(mrDetail, mr.getStatus().intValue())) {
      diffDao.deleteByMrId(mrDetail.getId());
      moDao.deleteByMrId(mrDetail.getId());
      updateMRDetail(mrDetail, mr, String.valueOf(user.getId()), user.getUsername(),
          user.getDisplayName(), user.isApplRole());
    }
    mr.setStatus(status);
    mr.setApplName(user.getDisplayName());
    mr.setApplId(userService.findUserById(user.getId()).getInhId());
    mrDao.save(mr);

    httpServletReq.setAttribute(LogType.MEDICAL_RECORD_STATUS_CHANGE.name()+"_INH_CLINIC_ID", mr.getInhClinicId());
    httpServletReq.setAttribute(LogType.MEDICAL_RECORD_STATUS_CHANGE.name()+"_USER_ID"      , userService.getUserIdByName(mr.getPrsnId()));
    httpServletReq.setAttribute(LogType.MEDICAL_RECORD_STATUS_CHANGE.name()+"_STATUS"       , status);
    
    return null;
  }

  public void updateMyMrStatus(long mrId, int status, MR mr, long userId, String username,
      String displayName, boolean isAppl) {
    MY_MR myMr = myMrDao.findByMrId(mrId);
    if (myMr == null) {
      if (status == MR_STATUS.WAIT_PROCESS.value() || status == MR_STATUS.QUESTION_MARK.value()
          || status == MR_STATUS.WAIT_CONFIRM.value()) {
    	  
      	long prsnUserId = userService.getUserIdByName(mr.getPrsnName());
        myMr = new MY_MR(mr);
        myMr.setStatus(status);
        myMr.setApplId(username);
        myMr.setApplUserId(userId);
        myMr.setApplName(displayName);
        myMr.setPrsnUserId(prsnUserId);
        myMr.setFuncTypec(cts.getDesc("FUNC_TYPE", myMr.getFuncType()));
        myMrDao.save(myMr);
      }
    } else {
      if (isAppl) {
        myMr.setApplId(username);
        myMr.setApplUserId(userId);
        myMr.setApplName(displayName);
      }
      myMr.setChangeIcd(mr.getChangeICD());
      myMr.setChangeInh(mr.getChangeInh());
      myMr.setChangeOrder(mr.getChangeOrder());
      myMr.setChangeOther(mr.getChangeOther());
      myMr.setChangeSo(mr.getChangeSo());
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
    
    if (mrDetail.getInhClinicId() != null) {
      MR_SO so = mrSoDao.findByInhNo(mrDetail.getInhClinicId());
      if (so != null) {
        mrDetail.setSubjective(so.getSubjectText());
        mrDetail.setObjective(so.getObjectText());
        mrDetail.setDischarge(so.getDischargeText());
      }
    }
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
    boolean isAppl = ROLE_TYPE.APPL.getRole().equals(jwtUtils.getRoleFromClaims(claims));

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
    return updateMRDetail(mrDetail, mr, userId, currentUser, displayName, isAppl);
  }

  /**
   * 更新病歷資料
   * 
   * @param mrDetail 要更新的內容
   * @param mr MR table 中的 record
   * @param userId 更新人員的 id
   * @param username 更新人員的 username
   * @param displayName 更新人員的顯示名稱
   * @param isAppl 更新人員是否為申報人員
   * @return
   */
  private MRDetail updateMRDetail(MRDetail mrDetail, MR mr, String userId, String username,
      String displayName, boolean isAppl) {
    MRDetail result = null;
    if (mr.getStatus().intValue() != mrDetail.getStatus().intValue()) {
      updateMyMrStatus(mrDetail.getId(), mrDetail.getStatus().intValue(), mr,
          Long.parseLong(userId), username, displayName, isAppl);
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
      // ipD.setNonApplDot(mrDetail.getOwnExpense());
      if (ipD.getTwDrgsSuitMark() != null
          && !ipD.getTwDrgsSuitMark().equals(mrDetail.getTwDrgsSuitMark())) {
        if ("0".equals(ipD.getTwDrgsSuitMark()) && !"0".equals(mrDetail.getTwDrgsSuitMark())) {
          updateNonDrg(ipD, mr, mrDetail.getTwDrgsSuitMark()); 
          mr.setDrgSection(null);
        } else if (!"0".equals(ipD.getTwDrgsSuitMark()) && "0".equals(mrDetail.getTwDrgsSuitMark())) {
          ipD.setTwDrgsSuitMark(mrDetail.getTwDrgsSuitMark());
          updateDrgList(mrDetail.getIcdCM().get(0).getCode(), ipD, mr);
        }
      }
      ipD.setTwDrgsSuitMark(mrDetail.getTwDrgsSuitMark());
      ipD.setTwDrgCode(mrDetail.getDrgCode());
      if (updateIPPByMrDetail(mr, ipD, mrDetail, true)) {
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
    opD.setIcdCm1(null);
    opD.setIcdCm2(null);
    opD.setIcdCm3(null);
    opD.setIcdCm4(null);
    opD.setIcdCm5(null);
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
    opD.setCureItemNo1(null);
    opD.setCureItemNo2(null);
    opD.setCureItemNo3(null);
    opD.setCureItemNo4(null);
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

    opD.setIcdOpCode1(null);
    opD.setIcdOpCode2(null);
    opD.setIcdOpCode3(null);
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
        if ((opp.getDrugNo() == null && opp.getInhCode() != null && opp.getInhCode().equals(opp.getInhCode())) 
          || opp.getDrugNo().equals(mo.getDrugNo())) {
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
    if (opp.getOrderSeqNo() != null && opp.getOrderSeqNo().intValue() != mo.getOrderSeqNo().intValue()) {
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
    ipD.setIcdCm1(null);
    ipD.setIcdCm2(null);
    ipD.setIcdCm3(null);
    ipD.setIcdCm4(null);
    ipD.setIcdCm5(null);
    ipD.setIcdCm6(null);
    ipD.setIcdCm7(null);
    ipD.setIcdCm8(null);
    ipD.setIcdCm9(null);
    ipD.setIcdCm10(null);
    ipD.setIcdCm11(null);
    ipD.setIcdCm12(null);
    ipD.setIcdCm13(null);
    ipD.setIcdCm14(null);
    ipD.setIcdCm15(null);
    ipD.setIcdCm16(null);
    ipD.setIcdCm17(null);
    ipD.setIcdCm18(null);
    ipD.setIcdCm19(null);
    ipD.setIcdCm20(null);
    if (mrDetail.getIcdCM() != null) {
      if (mrDetail.getIcdCM().size() >= 1) {
        if (!mrDetail.getIcdCM().get(0).getCode().equals(ipD.getIcdCm1())
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
    ipD.setIcdOpCode1(null);
    ipD.setIcdOpCode2(null);
    ipD.setIcdOpCode3(null);
    ipD.setIcdOpCode4(null);
    ipD.setIcdOpCode5(null);
    ipD.setIcdOpCode6(null);
    ipD.setIcdOpCode7(null);
    ipD.setIcdOpCode8(null);
    ipD.setIcdOpCode9(null);
    ipD.setIcdOpCode10(null);
    ipD.setIcdOpCode11(null);
    ipD.setIcdOpCode12(null);
    ipD.setIcdOpCode13(null);
    ipD.setIcdOpCode14(null);
    ipD.setIcdOpCode15(null);
    ipD.setIcdOpCode16(null);
    ipD.setIcdOpCode17(null);
    ipD.setIcdOpCode18(null);
    ipD.setIcdOpCode19(null);
    ipD.setIcdOpCode20(null);
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
    if (mrDetail.getApplCauseMark() != null && mrDetail.getApplCauseMark().length() == 1) {
      ipD.setApplCauseMark(mrDetail.getApplCauseMark());
    }
   
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
    int diagDot = (ipd.getDiagDot() == null) ? 0 : ipd.getDiagDot().intValue();
    // System.out.println("initial diagDot=" + diagDot);
    // 病房費點數
    int roomDot = (ipd.getRoomDot() == null) ? 0 : ipd.getRoomDot().intValue();
    // 管灌膳食費點數
    int mealDot = (ipd.getMealDot() == null) ? 0 : ipd.getMealDot().intValue();
    // 檢查費點數
    int aminDot = (ipd.getAminDot() == null) ? 0 : ipd.getAminDot().intValue();
    // 放射線診療費點數
    int radoDot = (ipd.getRadoDot() == null) ? 0 : ipd.getRadoDot().intValue();
    // 治療處置費點數
    int thrpDot = (ipd.getThrpDot() == null) ? 0 : ipd.getThrpDot().intValue();
    // 手術費點數
    int sgryDot = (ipd.getSgryDot() == null) ? 0 : ipd.getSgryDot().intValue();
    // 復健治療費點數
    int phscDot = (ipd.getPhscDot() == null) ? 0 : ipd.getPhscDot().intValue();
    // 血液血漿費點數
    int blodDot = (ipd.getBlodDot() == null) ? 0 : ipd.getBlodDot().intValue();
    // 血液透析費點數
    int hdDot = (ipd.getHdDot() == null) ? 0 : ipd.getHdDot().intValue();
    // 麻醉費點數
    int aneDot = (ipd.getAneDot() == null) ? 0 : ipd.getAneDot().intValue();
    // 特殊材料費點數
    int metrDot = (ipd.getMetrDot() == null) ? 0 : ipd.getMetrDot().intValue();
    // 藥費點數
    int drugDot = (ipd.getDrugDot() == null) ? 0 : ipd.getDrugDot().intValue();
    // 藥事服務費點數
    int dsvcDot = (ipd.getDsvcDot() == null) ? 0 : ipd.getDsvcDot().intValue();
    // 精神科治療費點數
    int nrtpDot = (ipd.getNrtpDot() == null) ? 0 : ipd.getNrtpDot().intValue();
    // 注射技術費點數
    int injtDot = (ipd.getInjtDot() == null) ? 0 : ipd.getInjtDot().intValue();
    // 嬰兒費點數
    int babyDot = (ipd.getBabyDot() == null) ? 0 : ipd.getBabyDot().intValue();
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
            // 支付代碼為10碼列為西藥藥品
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
    if (notFoundList.size() > 0) {
      for (IP_P ip_P : notFoundList) {
        ippDao.deleteById(ip_P.getId());
      }
    }
    if (moList.size() > 0) {
      // 有新增的醫令
      for (MO mo : moList) {
        IP_P newIPP = mo.toIpp(cts);
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
      if (ipd.getMedDot() != null) {
        mr.setTotalDot(ipd.getMedDot());
      } else {
        mr.setTotalDot(0);
      }
      if (ipd.getNonApplDot() != null) {
        mr.setTotalDot(mr.getTotalDot() + ipd.getNonApplDot());
      }
      if (ipd.getOwnExpense() != null) {
        mr.setTotalDot(mr.getTotalDot() + ipd.getOwnExpense());
      }
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
    if (mrDetail.getApplCauseMark() != null && mrDetail.getApplCauseMark().length() == 1) {
      ipD.setApplCauseMark(mrDetail.getApplCauseMark());
    }
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
    if (s != null && s.indexOf('-') <= 2 && s.indexOf('-') > 0) {
      return s.substring(0, s.indexOf('-'));
    }
    return s;
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
    
    httpServletReq.setAttribute(LogType.ACTION_C.name()+"_PKS", Arrays.asList(new Long[]{mn.getId()}));
    
    return null;
  }

  private List<Predicate> getTodoPredicate(UserDetailsImpl user, java.util.Date sdate,
      java.util.Date edate, String dataFormat, String funcType, String funcTypec, String prsnId,
      String prsnName, String applId, String applName, Integer status, Root<MY_MR> root,
      CriteriaQuery<?> query, CriteriaBuilder cb) {
    List<Predicate> result = new ArrayList<Predicate>();
    if (ROLE_TYPE.APPL.getRole().equals(user.getRole())) {
      // 是申報人員，只撈自己的，若不是，則是看所有人的
      result.add(cb.equal(root.get("applUserId"), user.getId()));
    } else if (applId != null) {
      addPredicate(root, result, cb, "applId", applId, true, false, false);
    } else if (applName != null) {
      addPredicate(root, result, cb, "applName", applName, true, false, false);
    }
    if (sdate != null && edate != null) {
//      result.add(cb.or(
//          cb.and(cb.greaterThanOrEqualTo(root.get("startDate"), sdate),
//              cb.lessThanOrEqualTo(root.get("startDate"), edate)),
//          cb.and(cb.greaterThanOrEqualTo(root.get("endDate"), sdate),
//              cb.lessThanOrEqualTo(root.get("endDate"), edate))));
      result.add(
      cb.or(
          cb.and(cb.equal(root.get("dataFormat"), XMLConstant.DATA_FORMAT_IP), 
              cb.or(
          cb.and(cb.greaterThanOrEqualTo(root.get("startDate"), sdate),
              cb.lessThanOrEqualTo(root.get("startDate"), edate)),
          cb.and(cb.greaterThanOrEqualTo(root.get("endDate"), sdate),
              cb.lessThanOrEqualTo(root.get("endDate"), edate)))), 
          cb.and(cb.equal(root.get("dataFormat"), XMLConstant.DATA_FORMAT_OP), 
              cb.greaterThanOrEqualTo(root.get("startDate"), sdate),
              cb.lessThanOrEqualTo(root.get("endDate"), edate))));
    }
    if (dataFormat != null) {
      result.add(cb.equal(root.get("dataFormat"), dataFormat));
    }
    addPredicate(root, result, cb, "funcType", funcType, false, false, false);
    addPredicate(root, result, cb, "prsnId", prsnId, false, false, false);
    addPredicate(root, result, cb, "prsnName", prsnName, false, false, false);
    addPredicate(root, result, cb, "funcTypec", funcTypec, false, false, false);

    if (status != null) {
      result.add(cb.equal(root.get("status"), status.intValue()));
    } else {
      result.add(cb.or(cb.equal(root.get("status"), MR_STATUS.WAIT_PROCESS.value()),
          cb.equal(root.get("status"), MR_STATUS.QUESTION_MARK.value())));
    }
    return result;
  }

  public MyTodoListResponse getMyTodoList(UserDetailsImpl user, java.util.Date sdate,
      java.util.Date edate, String dataFormat, String funcType, String funcTypec, String prsnId,
      String prsnName, String applId, String applName, Integer status, String orderBy, Boolean asc,
      int perPage, int page) {
    MyTodoListResponse result = new MyTodoListResponse();

    Specification<MY_MR> spec = new Specification<MY_MR>() {

      private static final long serialVersionUID = 1001L;

      public Predicate toPredicate(Root<MY_MR> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicate = getTodoPredicate(user, sdate, edate, dataFormat, funcType,
            funcTypec, prsnId, prsnName, applId, applName, status, root, query, cb);
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
    result.setQuestionMark(getCountOfTodoByStatus(user, sdate, edate, dataFormat, funcType,
        funcTypec, prsnId, prsnName, applId, applName, MR_STATUS.QUESTION_MARK.value()));
    result.setWaitProcess(getCountOfTodoByStatus(user, sdate, edate, dataFormat, funcType,
        funcTypec, prsnId, prsnName, applId, applName, MR_STATUS.WAIT_PROCESS.value()));
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

    Specification<MY_MR> myMrSpec = getWarningSpec(user, sdate, edate, dataFormat, funcType,
        funcTypec, prsnId, prsnName, applId, applName, null, orderBy, asc, perPage, page, isAppl,
        MR_STATUS.QUESTION_MARK.value(), false, false);

    // 疑問標示總病歷數
    result.setQuestionMark((int) myMrDao.count(myMrSpec));

    myMrSpec = getWarningSpec(user, sdate, edate, dataFormat, funcType, funcTypec, prsnId, prsnName,
        applId, applName, null, orderBy, asc, perPage, page, isAppl,
        MR_STATUS.QUESTION_MARK.value(), true, false);

    result.setNoticeTimes((int) myMrDao.count(myMrSpec));

    myMrSpec = getWarningSpec(user, sdate, edate, dataFormat, funcType, funcTypec, prsnId, prsnName,
        applId, applName, null, orderBy, asc, perPage, page, isAppl,
        MR_STATUS.QUESTION_MARK.value(), false, true);
    result.setReaded((int) myMrDao.count(myMrSpec));
    result.setUnread(result.getNoticeTimes().intValue() - result.getReaded().intValue());
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
   * @return
   */
  private int getCountOfTodoByStatus(UserDetailsImpl user, java.util.Date sdate,
      java.util.Date edate, String dataFormat, String funcType, String funcTypec, String prsnId,
      String prsnName, String applId, String applName, int status) {

    Specification<MY_MR> spec = new Specification<MY_MR>() {

      private static final long serialVersionUID = 1004L;

      public Predicate toPredicate(Root<MY_MR> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicate = getTodoPredicate(user, sdate, edate, dataFormat, funcType,
            funcTypec, prsnId, prsnName, applId, applName, status, root, query, cb);
        if (ROLE_TYPE.APPL.getRole().equals(user.getRole())) {
          predicate.add(cb.equal(root.get("applUserId"), user.getId()));
        }
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
    	  
    	long prsnUserId = userService.getUserIdByName(mr.getPrsnName());
        myMr = new MY_MR(mr);
        myMr.setPrsnUserId(prsnUserId);
        myMr.setFuncTypec(cts.getDesc("FUNC_TYPE", myMr.getFuncType()));
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
      if (receiverList.get(i) == null || receiverList.get(i).indexOf("@") < 0) {
        continue;
      }
      sendNotic("病歷狀態確認通知", receiverList.get(i),
          generateNoticeEmailContent(mrList, receiverName[i + 1], sender, noticeTimes));
    }
    
    Map<Long, String> mrMap = mrList.stream().collect(HashMap::new, (map, item) -> map.put(item.getId(), item.getInhClinicId()), HashMap::putAll);
    
    List<Long> doctorIds = Arrays.asList(ids).stream().map(m-> Long.parseLong(m+"")).collect(Collectors.toList());
    
    httpServletReq.setAttribute(LogType.MEDICAL_RECORD_NOTIFYED.name()+"_MR_MAP"    , mrMap);
    httpServletReq.setAttribute(LogType.MEDICAL_RECORD_NOTIFYED.name()+"_DOCTOR_IDS", doctorIds);
    
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
        mrNoticeDao.findByMrIdAndReceiveUserIdContainingOrderByNoticeDateDesc(mrId, "," + String.valueOf(user.getId()) + ",");
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
      // if (mn.getNoticePpl().intValue() == mn.getReadedPpl().intValue()) {
      // mn.setStatus(1);
      // }
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
          
          httpServletReq.setAttribute(LogType.MEDICAL_RECORD_READ.name()+"_USER_ID" , user.getId());
          httpServletReq.setAttribute(LogType.MEDICAL_RECORD_READ.name()+"_MR_ID"   , mrId);
        }
      }
    }
  }

  public WarningOrderResponse getWarningOrderList(SecondFilterParameter sfp) {
    WarningOrderResponse result = new WarningOrderResponse();

    // 是否為申報人員，若是則只撈自己的，若不是，則是看所有人的
    boolean isAppl = ROLE_TYPE.APPL.getRole().equals(sfp.getUser().getRole());

    Specification<MY_MR> spec =
        getWarningSpec(sfp, isAppl, MR_STATUS.WAIT_CONFIRM.value(), false, false);
    result.setCount((int) myMrDao.count(spec));
    Page<MY_MR> pages = myMrDao.findAll(spec, PageRequest.of(sfp.getPage(), sfp.getPerPage()));
    List<WarningOrder> list = new ArrayList<WarningOrder>();
    if (pages != null && pages.getSize() > 0) {
      for (MY_MR p : pages) {
        list.add(new WarningOrder(p));
      }
    }
    result.setTotalPage(Utility.getTotalPage(result.getCount(), sfp.getPerPage()));
    result.setData(list);

    sfp.setBlock(null);
    spec = getWarningSpec(sfp, isAppl, MR_STATUS.WAIT_CONFIRM.value(), false, false);
    // 警示總病歷數
    result.setWarning((int) myMrDao.count(spec));

    if (result.getWarning() > 0) {
      // 已通知次數
      spec = getWarningSpec(sfp, isAppl, MR_STATUS.WAIT_CONFIRM.value(), true, false);
      result.setNoticeTimes((int) myMrDao.count(spec));
      result.setNonNoticeTimes(result.getWarning() - result.getNoticeTimes());

      CriteriaBuilder cb = em.getCriteriaBuilder();
      CriteriaQuery<Tuple> query = cb.createTupleQuery();
      Root<MY_MR> root = query.from(MY_MR.class);

      List<Predicate> predicateList = getMyMrPredicate(cb, query, root, sfp, isAppl,
          MR_STATUS.WAIT_CONFIRM.value(), false, false);
      updateWarningOrderChanges(cb, query, root, predicateList, result);
    }

    return result;
  }

  private void updateWarningOrderChanges(CriteriaBuilder cb, CriteriaQuery<Tuple> query,
      Root<MY_MR> root, List<Predicate> predicate, WarningOrderResponse response) {
    // SELECT COUNT(ID), SUM(APPL_DOT) FROM MR WHERE ...
    query.select(cb.tuple(cb.sumAsLong(root.get("changeIcd")), cb.sumAsLong(root.get("changeInh")),
        cb.sumAsLong(root.get("changeOrder")), cb.sumAsLong(root.get("changeOther")),
        cb.sumAsLong(root.get("changeSo"))));
    Predicate[] pre = new Predicate[predicate.size()];
    query.where(predicate.toArray(pre));

    TypedQuery<Tuple> typedQuery = em.createQuery(query);
    List<Tuple> list = typedQuery.getResultList();
    Tuple values = list.get(0);
    if (values.get(0) == null) {
      response.setChangeIcd(0);
      response.setChangeInh(0);
      response.setChangeOrder(0);
      response.setChangeOther(0);
      response.setChangeSo(0);
    } else {
      response.setChangeIcd(((Long) values.get(0)).intValue());
      response.setChangeInh(((Long) values.get(1)).intValue());
      response.setChangeOrder(((Long) values.get(2)).intValue());
      response.setChangeOther(((Long) values.get(3)).intValue());
      response.setChangeSo(((Long) values.get(4)).intValue());
    }
  }

  public QuestionMarkResponse getQuestionMark(UserDetailsImpl user, String applYm,
      java.sql.Date sdate, java.sql.Date edate, String dataFormat, String funcType,
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
      int perPage, int page, boolean isAppl, int status, boolean isNoticeDateNotNull,
      boolean isReadedPplNotZero) {
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
//          predicate.add(cb.or(
//              cb.and(cb.greaterThanOrEqualTo(root.get("startDate"), sdate),
//                  cb.lessThanOrEqualTo(root.get("startDate"), edate)),
//              cb.and(cb.greaterThanOrEqualTo(root.get("endDate"), sdate),
//                  cb.lessThanOrEqualTo(root.get("endDate"), edate))));
          predicate.add(
              cb.or(
                  cb.and(cb.equal(root.get("dataFormat"), XMLConstant.DATA_FORMAT_IP), 
                      cb.or(
                  cb.and(cb.greaterThanOrEqualTo(root.get("startDate"), sdate),
                      cb.lessThanOrEqualTo(root.get("startDate"), edate)),
                  cb.and(cb.greaterThanOrEqualTo(root.get("endDate"), sdate),
                      cb.lessThanOrEqualTo(root.get("endDate"), edate)))), 
                  cb.and(cb.equal(root.get("dataFormat"), XMLConstant.DATA_FORMAT_OP), 
                      cb.greaterThanOrEqualTo(root.get("endDate"), sdate),
                      cb.lessThanOrEqualTo(root.get("endDate"), edate))));
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

  public Specification<MY_MR> getWarningSpec(SecondFilterParameter sfp, boolean isAppl, int status,
      boolean isNoticeDateNotNull, boolean isReadedPplNotZero) {

    return new Specification<MY_MR>() {

      private static final long serialVersionUID = 1006L;

      public Predicate toPredicate(Root<MY_MR> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicate = getMyMrPredicate(cb, query, root, sfp, isAppl, status,
            isNoticeDateNotNull, isReadedPplNotZero);

        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));

        List<Order> orderList = new ArrayList<Order>();
        if (sfp.getOrderBy() != null && sfp.getAsc() != null) {
          if (sfp.getAsc().booleanValue()) {
            orderList.add(cb.asc(root.get(sfp.getOrderBy())));
          } else {
            orderList.add(cb.desc(root.get(sfp.getOrderBy())));
          }
        } else {
          orderList.add(cb.desc(root.get("startDate")));
        }
        query.orderBy(orderList);
        return query.getRestriction();
      }
    };
  }

  private List<Predicate> getMyMrPredicate(CriteriaBuilder cb, CriteriaQuery<?> query,
      Root<MY_MR> root, SecondFilterParameter sfp, boolean isAppl, int status,
      boolean isNoticeDateNotNull, boolean isReadedPplNotZero) {
    List<Predicate> predicate = new ArrayList<Predicate>();

    if (isAppl) {
      // 是申報人員，只撈自己的，若不是，則是看所有人的
      predicate.add(cb.equal(root.get("applUserId"), sfp.getUser().getId()));
    }
    if (sfp.getApplId() != null) {
      predicate.add(cb.equal(root.get("applId"), sfp.getApplId()));
    } else if (sfp.getApplName() != null) {
      predicate.add(cb.equal(root.get("applName"), sfp.getApplName()));
    }
    if (sfp.getsDate() != null && sfp.geteDate() != null) {
      addSearchDateParameter(predicate, cb, root, sfp.getsDate(), sfp.geteDate());
    }
    if (sfp.getDataFormat() != null) {
      predicate.add(cb.equal(root.get("dataFormat"), sfp.getDataFormat()));
    }
    if (sfp.getFuncType() != null) {
      predicate.add(cb.equal(root.get("funcType"), sfp.getFuncType()));
    }
    if (sfp.getPrsnId() != null) {
      predicate.add(cb.equal(root.get("prsnId"), sfp.getPrsnId()));
    }
    if (sfp.getPrsnName() != null) {
      predicate.add(cb.equal(root.get("prsnName"), sfp.getPrsnName()));
    }
    if (sfp.getFuncTypec() != null) {
      predicate.add(cb.equal(root.get("funcTypec"), sfp.getFuncTypec()));
    }
    if ("icd".equals(sfp.getBlock())) {
      predicate.add(cb.greaterThan(root.get("changeIcd"), 0));
    } else if ("order".equals(sfp.getBlock())) {
      predicate.add(cb.greaterThan(root.get("changeOrder"), 0));
    } else if ("inh".equals(sfp.getBlock())) {
      predicate.add(cb.greaterThan(root.get("changeInh"), 0));
    } else if ("so".equals(sfp.getBlock())) {
      predicate.add(cb.greaterThan(root.get("changeSo"), 0));
    } else if ("other".equals(sfp.getBlock())) {
      predicate.add(cb.greaterThan(root.get("changeOther"), 0));
    } else if ("notify".equals(sfp.getBlock())) {
      predicate.add(cb.greaterThan(root.get("noticeTimes"), 0));
      addCompareWarningPredicate(predicate, cb, root);
    } else if ("nonnotify".equals(sfp.getBlock())) {
      predicate.add(cb.equal(root.get("noticeTimes"), 0));
      addCompareWarningPredicate(predicate, cb, root);
    } else {
      addCompareWarningPredicate(predicate, cb, root);
    }

    if (isNoticeDateNotNull) {
      predicate.add(cb.isNotNull(root.get("noticeDate")));
    }
    if (isReadedPplNotZero) {
      predicate.add(cb.greaterThan(root.get("readedPpl"), 0));
    }
    predicate.add(cb.equal(root.get("status"), status));
    return predicate;
  }
  
  private void addCompareWarningPredicate(List<Predicate> predicate, CriteriaBuilder cb, Root<MY_MR> root) {
    predicate.add(cb.or(
        cb.greaterThan(root.get("changeIcd"), 0), 
        cb.greaterThan(root.get("changeIcd"), 0), 
        cb.greaterThan(root.get("changeOrder"), 0),
        cb.greaterThan(root.get("changeInh"), 0),
        cb.greaterThan(root.get("changeSo"), 0),
        cb.greaterThan(root.get("changeOther"), 0)
        ));
  }

  public Specification<MY_MR> getQuestionMarkSpec(boolean isAppl, UserDetailsImpl user,
      String applYm, java.sql.Date sdate, java.sql.Date edate, String dataFormat, String funcType,
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
          addSearchDateParameter(predicate, cb, root, sdate, edate);
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

//  public MRDetail addMRDetail(MRDetail mrDetail) {
//    MRDetail result = null;
//
//    // Optional<MR> optional = mrDao.findById(mrDetail.getId());
//    // if (optional.isPresent()) {
//    // result = new MRDetail(optional.get());
//    // result.setError("id:" + mrDetail.getId() + " 已存在");
//    // return result;
//    // }
//    MRDetail old = getExistMRFromOtherServer(mrDetail.getId().toString());
//    mrDetail.setFuncType(codeTableService.getCodeByDesc("FUNC_TYPE", mrDetail.getFuncType()));
//    MR mr = new MR();
//    result = updateMrDetail(old, mrDetail);
//
//    if ("住院".equals(mrDetail.getDataFormat())) {
//      mrDetail.setDataFormat(XMLConstant.DATA_FORMAT_IP);
//    } else if ("門診".equals(mrDetail.getDataFormat())) {
//      mrDetail.setDataFormat(XMLConstant.DATA_FORMAT_OP);
//    }
//    mr.updateMR(result);
//    if (result.getMrEndDate() != null) {
//      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
//      String ym = sdf.format(result.getMrEndDate());
//      mr.setApplYm(DateTool.convertToChineseYear(ym));
//    }
//
//    mr = mrDao.save(mr);
//    if (mrDetail.getMos() == null || mrDetail.getMos().size() == 0) {
//      mrDetail.setMos(old.getMos());
//    }
//
//    StringBuffer sb = new StringBuffer(",");
//    if (XMLConstant.DATA_FORMAT_OP.equals(mrDetail.getDataFormat())) {
//      // OP_D opD = opdDao.getOne(mr.getdId());
//      OP_D opD = new OP_D();
//      opD.setMrId(mr.getId());
//      opD.setOptId(getTId(mrDetail.getMrDate(), false));
//      updateOPDByMrDetail(opD, result);
//      opD = opdDao.save(opD);
//      mr.setdId(opD.getId());
//      mr.setDataFormat(XMLConstant.DATA_FORMAT_OP);
//      mr.setIcdcm1(opD.getIcdCm1());
//      MRDetail.updateIcdcmOtherOP(mr, opD);
//      MRDetail.updateIcdpcsOP(mr, opD);
//      MRDetail.updateIcdAllByAlphabet(mr);
//
//      if (mrDetail.getMos() != null) {
//        for (int i = 0; i < mrDetail.getMos().size(); i++) {
//          OP_P opp = mrDetail.getMos().get(i).toOpp(codeTableService);
//          opp.setMrId(mr.getId());
//          opp.setOpdId(opD.getId());
//          oppDao.save(opp);
//
//          if (opp.getDrugNo() != null) {
//            sb.append(opp.getDrugNo());
//            sb.append(",");
//          }
//        }
//        if (sb.length() > 1) {
//          mr.setCodeAll(sb.toString());
//        }
//      }
//    } else if (XMLConstant.DATA_FORMAT_IP.equals(mrDetail.getDataFormat())) {
//      IP_D ipD = new IP_D();
//      updateIPDByMrDetail(ipD, result);
//      ipD.setMrId(mr.getId());
//      ipD = ipdDao.save(ipD);
//      mr.setDataFormat(XMLConstant.DATA_FORMAT_IP);
//      mr.setdId(ipD.getId());
//      if (mrDetail.getMos() != null) {
//        for (int i = 0; i < mrDetail.getMos().size(); i++) {
//          IP_P ipp = mrDetail.getMos().get(i).toIpp(codeTableService);
//          ipp.setMrId(mr.getId());
//          ipp.setIpdId(ipD.getId());
//          ippDao.save(ipp);
//
//          if (ipp.getOrderCode() != null) {
//            sb.append(ipp.getOrderCode());
//            sb.append(",");
//          }
//        }
//        if (sb.length() > 1) {
//          mr.setCodeAll(sb.toString());
//        }
//      }
//    }
//    mr = mrDao.save(mr);
//    result = new MRDetail(mr);
//    return result;
//  }

  public OP_P convertMoToOpp(MO mo) {
    OP_P result = new OP_P();

    return result;
  }

//  public MRDetail getExistMRFromOtherServer(String mrId) {
//    SendHTTP send = new SendHTTP();
//    send.setServerIP("10.10.5.30");
//    send.setPort("8081");
//    String token = send.loginUser("applc", "leadtek");
//    String response = send.getAPI(token, "/nhixml/mr/" + mrId);
//    ObjectMapper objectMapper = new ObjectMapper();
//    try {
//      return objectMapper.readValue(response, MRDetail.class);
//    } catch (JsonMappingException e) {
//      e.printStackTrace();
//    } catch (JsonProcessingException e) {
//      e.printStackTrace();
//    }
//    return null;
//  }

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
        if (note.getActionType().intValue() == 1) {
          record = "新增核刪代碼" + note.getCode();
          note.setModifyStatus("新增");
        } else if (note.getActionType().intValue() == 2) {
          record = "修改核刪代碼" + note.getCode();
          note.setModifyStatus("修改");
        } else if (note.getActionType().intValue() == 3) {
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

  public String updateDeductedNote(DEDUCTED_NOTE note, String username) {
    changeEmptyToNull(note);
    if (note.getId() == null || note.getId() < 1) {
      return "核刪註記id" + note.getId() + "不存在";
    }
    DEDUCTED_NOTE db = deductedNoteDao.findById(note.getId()).orElse(null);
    if (db == null) {
      return "核刪註記id" + note.getId() + "不存在";
    }
    
    // 儲存核刪修改記錄
    DEDUCTED_NOTE update = checkIfDirty(db, note);
    if (update != null) {
      update.setEditor(username);
      deductedNoteDao.save(update);
      db.setStatus(0);
      deductedNoteDao.save(db);
    }
    return null;
  }
  
  private DEDUCTED_NOTE checkIfDirty(DEDUCTED_NOTE old, DEDUCTED_NOTE newNote) {
    boolean isDirty = false;
    DEDUCTED_NOTE update = new DEDUCTED_NOTE();
    //update.setId(old.getId());
    if (!checkIfEquals(old.getAfrAmount(), newNote.getAfrAmount())) {
      isDirty = true; 
    }
    update.setAfrAmount(newNote.getAfrAmount());
    
    if (!checkIfEquals(old.getAfrNoPayCode(), newNote.getAfrNoPayCode())) {
      isDirty = true;
    }
    update.setAfrNoPayCode(newNote.getAfrNoPayCode());
    
    if (!checkIfEquals(old.getAfrNoPayDesc(), newNote.getAfrNoPayDesc())) {
      isDirty = true;
    }
    update.setAfrNoPayDesc(newNote.getAfrNoPayDesc());
    
    if (!checkIfEquals(old.getAfrNote(), newNote.getAfrNote())) {
      isDirty = true;
    }
    update.setAfrNote(newNote.getAfrNote());
    
    if (!checkIfEquals(old.getAfrPayAmount(), newNote.getAfrPayAmount())) {
      isDirty = true;
    }
    update.setAfrPayAmount(newNote.getAfrPayAmount());
    
    if (!checkIfEquals(old.getAfrPayQuantity(), newNote.getAfrPayQuantity())) {
      isDirty = true;
    }
    update.setAfrPayQuantity(newNote.getAfrPayQuantity());
   
    if (!checkIfEquals(old.getAfrQuantity(), newNote.getAfrQuantity())) {
      isDirty = true; 
    }
    update.setAfrQuantity(newNote.getAfrQuantity());
    if (!checkIfEquals(old.getCat(), newNote.getCat())) {
      isDirty = true; 
    }
    update.setCat(newNote.getCat());
    if (!checkIfEquals(old.getCode(), newNote.getCode())) {
      isDirty = true;
    }
    update.setCode(newNote.getCode());
    if (!checkIfEquals(old.getDeductedAmount(), newNote.getDeductedAmount())) {
      isDirty = true;
    }
    update.setDeductedAmount(newNote.getDeductedAmount());
    if (!checkIfEquals(old.getDeductedOrder(), newNote.getDeductedOrder())) {
      isDirty = true;
    }
    update.setDeductedOrder(newNote.getDeductedOrder());
    if (!checkIfEquals(old.getDeductedQuantity(), newNote.getDeductedQuantity())) {
      isDirty = true;
    }
    update.setDeductedQuantity(newNote.getDeductedQuantity());
    if (!checkIfEquals(old.getDisputeAmount(), newNote.getDisputeAmount())) {
      isDirty = true;
    }
    update.setDisputeAmount(newNote.getDisputeAmount());
    if (!checkIfEquals(old.getDisputeNoPayCode(), newNote.getDisputeNoPayCode())) {
      isDirty = true;
    }
    update.setDisputeNoPayCode(newNote.getDisputeNoPayCode());
    if (!checkIfEquals(old.getDisputeNoPayDesc(), newNote.getDisputeNoPayDesc())) {
      isDirty = true;
    }
    update.setDisputeNoPayDesc(newNote.getDisputeNoPayDesc());
    if (!checkIfEquals(old.getDisputeNote(), newNote.getDisputeNote())) {
      isDirty = true;
    }
    update.setDisputeNote(newNote.getDisputeNote());
    if (!checkIfEquals(old.getDisputePayAmount(), newNote.getDisputePayAmount())) {
      isDirty = true;
    }
    update.setDisputePayAmount(newNote.getDisputePayAmount());
    if (!checkIfEquals(old.getDisputePayQuantity(), newNote.getDisputePayQuantity())) {
      isDirty = true;
    }
    update.setDisputePayQuantity(newNote.getDisputePayQuantity());
    if (!checkIfEquals(old.getDisputeQuantity(), newNote.getDisputeQuantity())) {
      isDirty = true;
    }
    update.setDisputeQuantity(newNote.getDisputeQuantity());
    if (!checkIfEquals(old.getItem(), newNote.getItem())) {
      isDirty = true;
    }
    update.setItem(newNote.getItem());
    if (!checkIfEquals(old.getL1(), newNote.getL1())) {
      isDirty = true;
    }
    update.setL1(newNote.getL1());
    if (!checkIfEquals(old.getL2(), newNote.getL2())) {
      isDirty = true;
    }
    update.setL2(newNote.getL2());
    if (!checkIfEquals(old.getL3(), newNote.getL3())) {
      isDirty = true;
    }
    update.setL3(newNote.getL3());
    update.setModifyStatus(newNote.getModifyStatus());
    update.setMrId(old.getMrId());
    if (!checkIfEquals(old.getNote(), newNote.getNote())) {
      isDirty = true;
    }
    update.setNote(newNote.getNote());
    if (!checkIfEquals(old.getReason(), newNote.getReason())) {
      isDirty = true;
    }
    update.setReason(newNote.getReason());
    if (!checkIfEquals(old.getRollbackM(), newNote.getRollbackM())) {
      isDirty = true;
    }
    update.setRollbackM(newNote.getRollbackM());
    if (!checkIfEquals(old.getRollbackQ(), newNote.getRollbackQ())) {
      isDirty = true;
    }
    update.setRollbackQ(newNote.getRollbackQ());
    update.setStatus(1);
    update.setActionType(ACTION_TYPE.MODIFIED.value());
    if (!checkIfEquals(old.getSubCat(), newNote.getSubCat())) {
      isDirty = true;
    }
    update.setSubCat(newNote.getSubCat());
    if (!checkIfEquals(old.getDeductedDate(), newNote.getDeductedDate())) {
      isDirty = true;
    }
    update.setDeductedDate(newNote.getDeductedDate());
    if (!checkIfEquals(old.getRollbackDate(), newNote.getRollbackDate())) {
      isDirty = true;
    }
    update.setRollbackDate(newNote.getRollbackDate());
    if (!checkIfEquals(old.getDisputeDate(), newNote.getDisputeDate())) {
      isDirty = true;
    }
    update.setDisputeDate(newNote.getDisputeDate());
    update.setUpdateAt(new java.util.Date());
    if (isDirty) {
      return update;
    }
    return null;
  }
  
  /**
   * 
   * @param old
   * @param newInt
   * @return
   */
  private boolean checkIfEquals(Object old, Object newObj) {
    Object a = old;
    Object b = newObj;
    if ("".equals(old)) {
      a = null;
    }
    if ("".equals(newObj)) {
      b = null;
    }
    if (a == null && b == null) {
      return true;
    }
    if (a != null && b == null) {
      return false;
    }
    if (a == null && b != null) {
      return false;
    }
    if (a instanceof Integer) {
      return ((Integer) a).intValue() == ((Integer) b).intValue();
    } else if (a instanceof String) {
      return a.equals(b);
    } else if (a instanceof Date) {
      return ((Date) a).getTime() == ((Date) b).getTime();
    }
    return true;
  }
  
  public String deleteDeductedNote(String username, long noteId) {
    DEDUCTED_NOTE db = deductedNoteDao.findById(noteId).orElse(null);
    if (db == null) {
      return "核刪註記id:" + noteId + "不存在";
    }
    db.setStatus(0);
    deductedNoteDao.save(db);
    
    // 儲存刪除核刪記錄
    DEDUCTED_NOTE deleted = new DEDUCTED_NOTE();
    deleted.setActionType(ACTION_TYPE.DELETED.value());
    deleted.setCode(db.getCode());
    deleted.setStatus(0);
    deleted.setMrId(db.getMrId());
    deleted.setEditor(username);
    deleted.setUpdateAt(new java.util.Date());
    deductedNoteDao.save(deleted);
    parameters.deleteCodeConflictForHighRisk(db);
    return null;
  }

  public String newDeductedNote(String mrId, DEDUCTED_NOTE note) {
    changeEmptyToNull(note);
    MR mr = null;
    try {
      mr = mrDao.findById(Long.parseLong(mrId)).orElse(null);
      if (mr == null) {
        return "病歷id" + mrId + "不存在";
      }
    } catch (NumberFormatException e) {
      return "病歷id" + mrId + "有誤";
    }
    note.setId(null);
    note.setMrId(Long.parseLong(mrId));
    note.setStatus(1);
    note.setUpdateAt(new java.util.Date());
    deductedNoteDao.save(note);
    if (note.getDeductedOrder() != null) {
      parameters.upsertCodeConflictForHighRisk(
          mr.getIcdcm1(), note.getDeductedOrder(), mr.getDataFormat());
    }
    final String requestKey = LogType.ACTION_C.name()+"_PKS";
    
    List<Object> requestPKs = (List<Object>) httpServletReq.getAttribute(requestKey);
    
    if( null == requestPKs) {
    	
    	httpServletReq.setAttribute(requestKey, Arrays.asList(new Long[]{note.getId()}));
    	
    }else {
    	
    	requestPKs.add(note.getId());
    }
    
    
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
    if (s == null || s.length() == 0) {
      return null;
    }
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

    // 5 為自費案件
    predicates.add(cb.or(cb.notEqual(root.get("applStatus"), 5), cb.isNull(root.get("applStatus"))));
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
  
  private void addSearchMrDateParameter(List<Predicate> predicate, CriteriaBuilder cb, Root<MR> root, 
      Date sdate, Date edate) {
    predicate.add(cb.or(
        cb.and(cb.equal(root.get("dataFormat"), XMLConstant.DATA_FORMAT_IP), 
            cb.or(
        cb.and(cb.greaterThanOrEqualTo(root.get("mrDate"), sdate),
            cb.lessThanOrEqualTo(root.get("mrDate"), edate)),
        cb.and(cb.greaterThanOrEqualTo(root.get("mrEndDate"), sdate),
            cb.lessThanOrEqualTo(root.get("mrEndDate"), edate)))), 
        cb.and(cb.equal(root.get("dataFormat"), XMLConstant.DATA_FORMAT_OP), 
            cb.greaterThanOrEqualTo(root.get("mrEndDate"), sdate),
            cb.lessThanOrEqualTo(root.get("mrEndDate"), edate))));
  }
  
  private void addSearchDateParameter(List<Predicate> predicate, CriteriaBuilder cb, Root<MY_MR> root, 
      Date sdate, Date edate) {
    predicate.add(cb.or(
        cb.and(cb.equal(root.get("dataFormat"), XMLConstant.DATA_FORMAT_IP), 
            cb.or(
        cb.and(cb.greaterThanOrEqualTo(root.get("startDate"), sdate),
            cb.lessThanOrEqualTo(root.get("startDate"), edate)),
        cb.and(cb.greaterThanOrEqualTo(root.get("endDate"), sdate),
            cb.lessThanOrEqualTo(root.get("endDate"), edate)))), 
        cb.and(cb.equal(root.get("dataFormat"), XMLConstant.DATA_FORMAT_OP), 
            cb.greaterThanOrEqualTo(root.get("endDate"), sdate),
            cb.lessThanOrEqualTo(root.get("endDate"), edate))));
  }

  private List<Predicate> getHomePageCountPredicate(CriteriaBuilder cb, CriteriaQuery<Tuple> query,
      Root<MR> root, HomepageParameters hp) {
    List<Predicate> predicate = new ArrayList<Predicate>();
    if (hp.getApplYM() != null) {
      addPredicate(root, predicate, cb, "applYm", hp.getApplYM(), true, false, false);
    } else {
      addSearchMrDateParameter(predicate, cb, root, hp.getsDate(), hp.geteDate());
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
    query.select(cb.tuple(cb.count(root.get("id")), cb.sumAsLong(root.get("reportDot"))));
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

    List<IP_D> ipdList = ipdDao.findByMrId(idL);
    if (ipdList == null || ipdList.size() == 0) {
      result.setMessage("病歷 id:" + idL + " 不存在");
      result.setResult(BaseResponse.ERROR);
      return result;
    }
    IP_D ipd = ipdList.get(0);
    List<DRG_CAL> list = drgCalDao.findByMrId(idL);
    if (list == null || list.size() == 0) {
      result.setMessage("無drg記錄");
      return result;
    }
    List<DrgCalPayload> data = new ArrayList<DrgCalPayload>();
    for (DRG_CAL drgCal : list) {
      if (drgCal.getError() != null && !"C".equals(drgCal.getError())) {
        // "C" : DRG代碼尚未導入
        continue;
      }
      DrgCalPayload drg = new DrgCalPayload(drgCal);

      if (drg.getIcdCM1().equals(ipd.getIcdCm1())) {
        drg.setSelected(true);
      } else {
        drg.setSelected(false);
      }
      // 設定病歷點數
      if (ipd.getOwnExpense() == null) {
        ipd.setOwnExpense(0);
      }
      if (ipd.getNonApplDot() == null) {
        ipd.setNonApplDot(0);
      }
      drg.setMedDot(ipd.getMedDot().intValue() + ipd.getNonApplDot() + ipd.getOwnExpense().intValue());
      drg.setMedDotNoOwnExp(ipd.getMedDot().intValue() + ipd.getNonApplDot());
      // 將申請點數改為申報點數
      if (drg.getDrgDot() != null && ipd.getPartDot() != null) {
        drg.setDrgDot(drg.getDrgDot().intValue() + ipd.getPartDot().intValue());
      }
      // 將DrgDot高的放在最前(上)面
      if (drg.getDrgDot() == null || data.size() == 0) {
        data.add(drg);
      } else {
        int index = Integer.MAX_VALUE;
        for (int i=0; i<data.size(); i++) {
          DrgCalPayload drgCalPayload = data.get(i);
          if (drgCalPayload.getDrgDot() == null || drgCalPayload.getDrgDot().intValue() < drg.getDrgDot().intValue()) {
            index = i;
            break;
          }
          if (drgCalPayload.getDrgDot() != null && drgCalPayload.getDrgDot().intValue() == drg.getDrgDot().intValue()) {
            if (drg.getSelected().booleanValue()) {
              // 將目前使用的DRG優先放在前面
              index = i;
              break;
            }
          }
        }
        if (index == Integer.MAX_VALUE) {
          data.add(drg);
        } else {
          data.add(index, drg);
        }
      }
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
    List<DRG_CAL> list = drgCalDao.findByMrId(idL);
    if (list == null || list.size() == 0) {
      return "無drg記錄";
    }
    for (DRG_CAL drgCal : list) {
      if (drgCal.getIcdCM1().equals(icd)) {
        String tmp = ipd.getIcdCm1();
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
        updateDrg(drgCal, ipd, mr);
        break;
      }
    }
    return null;
  }

  public void updateDrgList(String icd, IP_D ipd, MR mr) {
    List<DRG_CAL> list = drgCalDao.findByMrId(mr.getId());
    if (list == null || list.size() == 0) {
      return;
    }
    for (DRG_CAL drgCal : list) {
      if (drgCal.getIcdCM1().equals(icd)) {
        updateDrg(drgCal, ipd, mr);
        return;
      }
    }
  }
  
  public void updateDrg(DRG_CAL drgCal, IP_D ipd, MR mr) {
    ipd.setIcdCm1(drgCal.getIcdCM1());
    ipd.setTwDrgCode(drgCal.getDrg());
    ipd.setApplDot(drgCal.getDrgDot());
    ipd.setUpdateAt(new java.util.Date());
    mr.setDrgCode(drgCal.getDrg());
    mr.setDrgFixed(drgCal.getDrgFix());
    mr.setDrgSection(drgCal.getDrgSection());
    mr.setApplDot(ipd.getApplDot());
    mr.setUpdateAt(new java.util.Date());
    
    if ("C".equals(drgCal.getError())) {
      ipd.setTwDrgsSuitMark("C");
      // 一般案件
      ipd.setCaseType("1");
      mr.setDrgSection(null);
    } else if (drgCal.getError() == null) {
      ipd.setTwDrgsSuitMark("0");
      // DRG 案件
      ipd.setCaseType("5");
    }
    ipdDao.save(ipd);
    mrDao.save(mr);
    reportService.calculatePointMR(ipd.getApplEndDate().substring(0, 5));
    reportService.calculateDRGMonthly(ipd.getApplEndDate().substring(0, 5));
    reportService.calculateDRGWeekly(mr.getMrEndDate());
  }
  
  public void updateNonDrg(IP_D ipd, MR mr, String twDrgsSuitMark) {
    ipd.setUpdateAt(new java.util.Date());
    if (!"0".equals(twDrgsSuitMark)) {
      ipd.setTwDrgsSuitMark(twDrgsSuitMark);
      // 一般案件
      ipd.setCaseType("1");
    }
    ipdDao.save(ipd);
    reportService.calculatePointMR(ipd.getApplEndDate().substring(0, 5));
    reportService.calculateDRGMonthly(ipd.getApplEndDate().substring(0, 5));
    reportService.calculateDRGWeekly(mr.getMrEndDate());
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
      sb.append(cts.getDesc("FUNC_TYPE", mr.getFuncType()));
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

  private boolean shouldCompareWarning(MR mr, CompareWarning cw, String newFuncType) {
    if (cw.getCompareBy() == 0) {
      // 關閉
      return false;
    }
    if (cw.getCompareBy() == 1) {
      // 只比對限定時間內的病歷
      if (cw.getRollbackHour() == 0) {
        return true;
      }
      return (mr.getMrEndDate().getTime() + ((long) cw.getRollbackHour() * 60L * 60000L)) > System
          .currentTimeMillis();
    }
    if (cw.getCompareBy() == 2) {
      if (cw.getFuncType() == null || cw.getFuncType().length == 0) {
        return false;
      }
      for (String func : cw.getFuncType()) {
        if (newFuncType.equals(func)) {
          if (cw.getDoctors() == null || cw.getDoctors().length == 0) {
            return true;
          }
          for (String doc : cw.getDoctors()) {
            if (((mr.getPrsnId() != null) && mr.getPrsnId().equals(doc))
                || (mr.getPrsnName() != null && mr.getPrsnName().equals(doc))) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  /**
   * 取得所有醫令代碼(支付標準代碼)及費用分類代碼
   * 
   * @return
   */
  public HashMap<String, String> getPayCodeType() {
    List<CODE_TABLE> codeTable = getCodeTable("PAY_CODE_TYPE");
    // 費用分類名稱, 費用分類代碼
    HashMap<String, String> payCodeType = new HashMap<String, String>();
    for (CODE_TABLE ct : codeTable) {
      payCodeType.put(ct.getDescChi(), ct.getCode());
    }
    // 醫令代碼(支付標準代碼), 費用分類代碼
    HashMap<String, String> result = new HashMap<String, String>();
    List<PAY_CODE> payCodeList = payCodeDao.findAll();
    for (PAY_CODE pc : payCodeList) {
      if (result.get(pc.getCode()) != null && !"20".equals(result.get(pc.getCode()))) {
        // 已抓過相同醫令類別
        continue;
      }
      // 費用分類代碼
      String payCodeTypeCode = payCodeType.get(pc.getCodeType());
      if (payCodeTypeCode == null) {
        // 不分類
        payCodeTypeCode = "20";
      }
      result.put(pc.getCode(), payCodeTypeCode);
    }
    return result;
  }

  public void readOpdSheet(HSSFSheet sheet) {
    long start = System.currentTimeMillis();
    int titleRowIndex = 0;
    // 取得各欄位名稱的位置
    HashMap<Integer, String> columnMap = ExcelUtil.readTitleRow(sheet.getRow(titleRowIndex));
    // 第一筆資料
    HashMap<String, String> values = ExcelUtil.readCellValue(columnMap, sheet.getRow(titleRowIndex + 1));
    // testColumnMap(columnMap);
    String[] orderDateString = getOrderDateFromSheet(sheet, titleRowIndex + 1, getColumnIndex(columnMap, "FUNC_END_DATE"));
    java.util.Date[] orderDate = new java.util.Date[2];
    orderDate[0] = DateTool.convertChineseToYear(orderDateString[0]);
    orderDate[1] = DateTool.convertChineseToYear(orderDateString[1]);
    String applYm = orderDateString[0].substring(0, 5);
    OP_T opt = getOpt(applYm);
    
    // 避免重複insert
    List<MR> mrList = mrDao.findByMrEndDateAndDataFormatOrderById(XMLConstant.DATA_FORMAT_OP, orderDate[0], orderDate[1]);
    //System.out.println("mrList:" + mrList.size() + "," +  orderDate[0] + " , " +  orderDate[1]);
    List<OP_D> opdList = opdDao.findByIDFromMR(orderDate[0], orderDate[1]);
    //System.out.println("opdList:" + opdList.size());
    CompareWarning cw = new CompareWarning(parameters.getByCat("COMPARE_WARNING"), cts);
   
    for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
      HSSFRow row = sheet.getRow(i);
      if (row == null || row.getCell(0) == null) {
        // System.out.println("sheet:" + i + ", row=" + j + " is null");
        continue;
      }
      values = ExcelUtil.readCellValue(columnMap, row);
      // 存放有差異的欄位
      List<FILE_DIFF> diffList = null;
      OP_D opd = getOpd(values);
      opd.setOptId(opt.getId());
      MR mr = findMR(mrList, values.get("INH_NO"));
      if (mr == null) {
        mr = new MR(opd);
        mr.setStatus(MR_STATUS.NO_CHANGE.value());
      } else {
        OP_D opdDB = findOpdById(opdList, mr.getdId(), true);
        if (opdDB != null) {
          opd.setId(opdDB.getId());
          opd.setMrId(mr.getId());
        } else {
          // 博愛醫院給的OP_D INH_NO有重複
          logger.info("op_d not found:" + mr.getId() + "," +mr.getdId() + ", inhClinicId=" + values.get("INH_NO"));
          continue;
        }
        if (mr.getStatus().intValue() != MR_STATUS.NO_CHANGE.value()
            && shouldCompareWarning(mr, cw, opd.getFuncType())) {
          diffList = new ArrayList<FILE_DIFF>();
          clearFileDiff(mr.getId());
          checkDiffOpdCureItem(diffList, opd);
        }
        mr.updateMR(opd, diffList, cts);
        if (diffList != null && diffList.size() > 0) {
          mr.setChangeOther(1);
        }
      }
      updateMRByExcel(values, mr);
      mr.setApplYm(applYm);
      findDiffOpd(diffList, mr, opd);
      saveDiffList(diffList, mr);
      mr = mrDao.save(mr);
    }
    long usedTime = System.currentTimeMillis() - start;
    logger.info("readOpdSheet used " + usedTime + "ms.");
    System.out.println("readOpdSheet used " + usedTime + "ms.");
  }

  private MR findMR(List<MR> mrList, String inhNo) {
    for (MR mr : mrList) {
      if (mr.getInhClinicId() != null && mr.getInhClinicId().equals(inhNo)) {
        return mr;
      }
    }
    return null;
  }
  
  private MR findMR(List<MR> mrList, String rocId, String funcEndDate, String funcType) {
    SimpleDateFormat sdf = new SimpleDateFormat(DateTool.SDF);
    for (MR mr : mrList) {
      if (mr.getRocId().equals(rocId) && mr.getFuncType().equals(funcType)) {
        int adYear = Integer.parseInt(funcEndDate.substring(0, 3)) + 1911;
        System.out.println("findMR " + rocId + "," + funcType + ", adYear=" + adYear + "," 
        + sdf.format(mr.getMrEndDate()) + "," + String.valueOf(adYear) + funcEndDate.substring(3));
        if (sdf.format(mr.getMrEndDate())
            .equals(String.valueOf(adYear) + funcEndDate.substring(3))) {
          return mr;
        }
      }
    }
    return null;
  }

  /**
   * 匯入 excel 檔，取出 OP_T
   * 
   * @param values
   * @return
   */
  private OP_T getOpt(String feeYM) {
    List<OP_T> list = optDao.findByFeeYmOrderById(feeYM);
    if (list == null || list.size() == 0) {
      OP_T result = new OP_T();
      result.setFeeYm(feeYM);
      result.setUpdateAt(new java.util.Date());
      return optDao.save(result);
    } else {
      return list.get(0);
    }
  }
  
  /**
   * 匯入 excel 檔，取出 OP_T
   * 
   * @param values
   * @return
   */
  private IP_T getIpt(String feeYM) {
    List<IP_T> list = iptDao.findByFeeYmOrderById(feeYM);
    if (list == null || list.size() == 0) {
      IP_T result = new IP_T();
      result.setFeeYm(feeYM);
      result.setUpdateAt(new java.util.Date());
      return iptDao.save(result);
    } else {
      return list.get(0);
    }
  }

  /**
   * 匯入 excel 檔，取出 IP_T
   * 
   * @param values
   * @return
   */
  private IP_T getIpt(HashMap<String, String> values, String feeYM) {
    List<IP_T> list = iptDao.findByFeeYmOrderById(feeYM);
    if (list == null || list.size() == 0) {
      IP_T result = new IP_T();
      result.setFeeYm(feeYM);
      result.setUpdateAt(new java.util.Date());
      return iptDao.save(result);
    } else {
      return list.get(0);
    }
  }

  /**
   * 由 OP_D Excel 檔的就醫日期取得申報年月
   * 
   * @param values
   * @return
   */
  private String getApplYm(HashMap<String, String> values) {
    String applDate = null;
    if (values.get("APPL_YM") != null) {
      return values.get("APPL_YM");
    }
    if (values.get("APPL_END_DATE") != null) {
      applDate = values.get("APPL_END_DATE");
    } else {
      applDate = (values.get("FUNC_EDN_DATE") == null) ? values.get("FUNC_DATE")
        : values.get("FUNC_EDN_DATE");
    }
    return applDate.substring(0, 5);
  }

  /**
   * 由 Excel 檔的就醫記錄編號INH_NO查出對應的 MR.INH_CLINIC_ID，以查到申報年月
   * 
   * @param values
   * @return
   */
  private String getApplYmByInhNo(HashMap<String, String> values) {
    String inhNo = values.get("INH_NO");
    if (inhNo == null || inhNo.length() == 0) {
      return null;
    }
    List<MR> list = mrDao.findByInhClinicId(inhNo);
    if (list == null || list.size() == 0) {
      System.out.println("mrList is null");
      return null;
    }
    return list.get(0).getApplYm();
  }

  private void updateMRByExcel(HashMap<String, String> values, MR mr) {
    if (values.get("INH_MR") != null && values.get("INH_MR").length() > 0) {
      mr.setInhMrId(values.get("INH_MR"));
    }
    if (values.get("INH_NO") != null && values.get("INH_NO").length() > 0) {
      mr.setInhClinicId(values.get("INH_NO"));
    }
    if (values.get("INH_OWN_EXP") != null && values.get("INH_OWN_EXP").length() > 0) {
      mr.setOwnExpense(Integer.parseInt(values.get("INH_OWN_EXP")));
    }
    mr.setClinic(values.get("CLINIC"));
    mr.setPrsnName(values.get("PRSN_NAME"));
  }

  /**
   * 由 OP_D Excel 檔的一列資料，轉成 OP_D object
   * 
   * @param values
   * @return
   */
  private OP_D getOpd(HashMap<String, String> values) {
    OP_D result = new OP_D();
    updateOpd(result, values);
    maskOPD(result);
    return result;
  }
  
  /**
   * 由 OP_D Excel 檔的一列資料，轉成 OP_D object
   * 
   * @param values
   * @return
   */
  private void updateOpd(OP_D result, HashMap<String, String> values) {
    if (values.get("CADE_TYPE") != null) {
      result.setCaseType(values.get("CADE_TYPE"));
    } else {
      result.setCaseType(values.get("CASE_TYPE"));
      if (result.getCaseType() != null && result.getCaseType().length() == 1) {
        result.setCaseType("0" + result.getCaseType());
      }
    }
    if (values.get("SEQ_NO") != null) {
      result.setSeqNo(Integer.parseInt(values.get("SEQ_NO")));
    }
    result.setRocId(values.get("ROC_ID"));
    if (values.get("CURE_ITEM_NO1") != null && values.get("CURE_ITEM_NO1").length() == 1) {
      result.setCureItemNo1("0" + values.get("CURE_ITEM_NO1"));
    } else {
      result.setCureItemNo1(values.get("CURE_ITEM_NO1"));
    }
    if (values.get("CURE_ITEM_NO2") != null && values.get("CURE_ITEM_NO2").length() == 1) {
      result.setCureItemNo2("0" + values.get("CURE_ITEM_NO2"));
    } else {
      result.setCureItemNo2(values.get("CURE_ITEM_NO2"));
    }
    if (values.get("CURE_ITEM_NO3") != null && values.get("CURE_ITEM_NO3").length() == 1) {
      result.setCureItemNo1("0" + values.get("CURE_ITEM_NO3"));
    } else {
      result.setCureItemNo3(values.get("CURE_ITEM_NO3"));
    }
    if (values.get("CURE_ITEM_NO4") != null && values.get("CURE_ITEM_NO4").length() == 1) {
      result.setCureItemNo1("0" + values.get("CURE_ITEM_NO4"));
    } else {
      result.setCureItemNo4(values.get("CURE_ITEM_NO4"));
    }
    String funcType = values.get("FUNC_TYPE");
    if (funcType == null) {
      System.out.println("funcType == null," + values.get("ROC_ID") + "," + values.get("FUNC_DATE"));
    }
    if (funcType.indexOf(' ') > -1) {
      funcType = funcType.substring(0, 2);
    }
    result.setFuncType(funcType);
    result.setFuncDate(DateTool.removeSlashForChineseYear(values.get("FUNC_DATE")));
    if (values.get("FUNC_END_DATE") != null && values.get("FUNC_END_DATE").length() > 0) {
      result.setFuncEndDate(DateTool.removeSlashForChineseYear(values.get("FUNC_END_DATE")));
    }
    result.setIdBirthYmd(values.get("ID_BIRTH_YMD"));
    result.setApplCauseMark(values.get("APPL_CAUSE_MARK"));
    result.setCareMark(values.get("CARE_MARK"));
    result.setPayType(values.get("PAY_TYPE"));
    result.setPartNo(values.get("PART_NO"));
    result.setShareMark(values.get("SHARE_MARK"));
    result.setShareHospId(values.get("SHARE_HOSP_ID"));
    result.setPatTranOut(values.get("PAT_TRAN_OUT"));
    result.setIcdCm1(addICDCMDot(values.get("ICD_CM_1")));
    result.setIcdCm2(addICDCMDot(values.get("ICD_CM_2")));
    result.setIcdCm3(addICDCMDot(values.get("ICD_CM_3")));
    result.setIcdCm4(addICDCMDot(values.get("ICD_CM_4")));
    result.setIcdCm5(addICDCMDot(values.get("ICD_CM_5")));
    result.setIcdOpCode1(values.get("ICD_OP_CODE1"));
    result.setIcdOpCode2(values.get("ICD_OP_CODE2"));
    result.setIcdOpCode3(values.get("ICD_OP_CODE3"));
    if (values.get("DRUG_DAY") != null && values.get("DRUG_DAY").length() > 0) {
      result.setDrugDay(Integer.parseInt(values.get("DRUG_DAY")));
    }
    result.setMedType(values.get("MED_TYPE"));
    result.setCardSeqNo(values.get("CARD_SEQ_NO"));
    result.setPrsnId(values.get("PRSN_ID"));
    result.setPharId(values.get("PHAR_ID"));
    if (values.get("DRUG_DOT") != null && values.get("DRUG_DOT").length() > 0) {
      result.setDrugDot(Integer.parseInt(values.get("DRUG_DOT")));
    }
    if (values.get("TREAT_DOT") != null && values.get("TREAT_DOT").length() > 0) {
      result.setTreatDot(Integer.parseInt(values.get("TREAT_DOT")));
    }
    if (values.get("METR_DOT") != null && values.get("METR_DOT").length() > 0) {
      result.setMetrDot(Integer.parseInt(values.get("METR_DOT")));
    }
    result.setTreatCode(values.get("TREAT_CODE"));
    if (values.get("DIAG_DOT") != null && values.get("DIAG_DOT").length() > 0) {
      result.setDiagDot(Integer.parseInt(values.get("DIAG_DOT")));
    }
    result.setDsvcNo(values.get("DSVC_NO"));
    if (values.get("DSVC_DOT") != null && values.get("DSVC_DOT").length() > 0) {
      result.setDsvcDot(Integer.parseInt(values.get("DSVC_DOT")));
    }
    if (values.get("T_DOT") != null && values.get("T_DOT").length() > 0) {
      result.setTotalDot(Integer.parseInt(values.get("T_DOT")));
    }
    if (values.get("PART_DOT") != null && values.get("PART_DOT").length() > 0) {
      result.setPartDot(Integer.parseInt(values.get("PART_DOT")));
    } else {
      result.setPartDot(0);
    }
    if (values.get("DSVC_PART_DOT") != null && values.get("DSVC_PART_DOT").length() > 0) {
      result.setPartDot(result.getPartDot().intValue() + Integer.parseInt(values.get("DSVC_PART_DOT")));
    }
    if (values.get("T_APPL_DOT") != null && values.get("T_APPL_DOT").length() > 0) {
      result.setTotalApplDot(Integer.parseInt(values.get("T_APPL_DOT")));
    }
    result.setCasePayCode(values.get("CASE_PAY_CODE"));
    if (values.get("ASSIST_PART_DOT") != null && values.get("ASSIST_PART_DOT").length() > 0) {
      result.setAssistPartDot(Integer.parseInt(values.get("ASSIST_PART_DOT")));
    }
    if (values.get("CHR_DAYS") != null && values.get("CHR_DAYS").length() > 0) {
      result.setChrDays(Integer.parseInt(values.get("CHR_DAYS")));
    }
    if (values.get("NB_BIRTHDAY") != null && values.get("NB_BIRTHDAY").length() > 0) {
      if (values.get("NB_BIRTHDAY").length() == 7) {
        result.setNbBirthday(values.get("NB_BIRTHDAY"));  
      } else if (values.get("NB_BIRTHDAY").indexOf("/") > 0) {
        result.setNbBirthday(DateTool.convertExcelDateTimeToChinese(values.get("NB_BIRTHDAY"), false));
      }
    }
    result.setOutSvcPlanCode(values.get("OUT_SVC_PLAN_CODE"));
    result.setName(values.get("NAME"));
    result.setAgencyId(values.get("AGENCY_ID"));
    result.setChildMark(values.get("CHILD_MARK"));
    result.setSpeAreaSvc(values.get("SPE_AREA_SVC"));
    result.setSupportArea(values.get("SUPPORT_AREA"));
    result.setHospId(values.get("HOSP_ID"));
    result.setTranInHospId(values.get("TRAN_IN_HOSP_ID"));
    result.setOriCardSeqNo(values.get("ORI_CARD_SEQ_NO"));
    result.setReceiveNo(values.get("RECEIVE_NO"));
    result.setPrsnName(values.get("PRSN_NAME"));
    result.setPharName(values.get("PHAR_NAME"));
    result.setCardNo(values.get("CARD_NO"));
    result.setUpdateAt(new java.util.Date());
  }

  /**
   * 由 OP_P Excel 檔的一列資料，轉成 OP_P object
   * 
   * @param values
   * @return
   */
  private OP_P getOpp(HashMap<String, String> values, String format) {
    OP_P result = new OP_P();
    result.setInhCode(values.get("INH_CODE"));
    if (values.get("PAY_STATUS") != null && values.get("PAY_STATUS").length() > 0) {
      result.setPayBy(values.get("PAY_STATUS"));      
    }
    if ("THESE".equals(format)) {
      // 杏翔
      if (values.get("APPL_STATUS") != null && values.get("APPL_STATUS").length() > 0) {
        if (values.get("APPL_STATUS").indexOf('.') > 0) {
          updateOppOwnExpense(result, values.get("APPL_STATUS"), values.get("CARD_NO"));
          if (values.get("TOTAL_OW") != null && Double.parseDouble(values.get("TOTAL_OW")) == 0) {
            // 減免
            result.setUnitP(0f);
          } else {
            result.setUnitP(Float.parseFloat(values.get("UNIT_P_OW")));
          }
        } else {
          int applStatus = Integer.parseInt(values.get("APPL_STATUS"));
          if (applStatus > 0) {
            updateOppOwnExpense(result, values.get("APPL_STATUS"), values.get("CARD_NO"));
            if (values.get("TOTAL_OW") != null && Double.parseDouble(values.get("TOTAL_OW")) == 0) {
              // 減免
              result.setUnitP(0f);
            } else {
              result.setUnitP(Float.parseFloat(values.get("UNIT_P_OW")));
            }
          } else {
            // = 0 走健保
            result.setApplStatus(APPL_STATUS_THIS_MONTH);
            result.setPayBy("N");
          }
        }
      }
    } else  if ("LEADTEK".equals(format)) {
      // 麗臺格式
      result.setApplStatus("1".equals((String)values.get("APPL_STATUS")) ? APPL_STATUS_THIS_MONTH : 5);
      result.setPayBy((String)values.get("PAY_STATUS"));
    }
  
    if (values.get("DRUG_DAY") != null && values.get("DRUG_DAY").length() > 0) {
      result.setDrugDay(Integer.parseInt(values.get("DRUG_DAY")));
    }
    result.setMedType(values.get("MED_TYPE"));
    result.setOrderType(values.get("ORDER_TYPE"));
    result.setDrugNo(values.get("DRUG_NO"));
    if (values.get("DRUG_USE") != null && values.get("DRUG_USE").length() > 0) {
      result.setDrugUse(Double.parseDouble(values.get("DRUG_USE")));
    }
    result.setCurePath(values.get("CURE_PATH"));
    result.setDrugFre(values.get("DRUG_FRE"));
    result.setPayRate(values.get("PAY_RATE"));
    result.setDrugPath(values.get("DRUG_PATH"));
    if (result.getTotalQ() == null && values.get("TOTAL_Q") != null && values.get("TOTAL_Q").length() > 0) {
      result.setTotalQ(Double.parseDouble(values.get("TOTAL_Q")));
    }
    if (result.getUnitP() == null && values.get("UNIT_P") != null && values.get("UNIT_P").length() > 0) {
      result.setUnitP(Float.parseFloat(values.get("UNIT_P")));
    }
    if (values.get("TOTAL_DOT") != null && values.get("TOTAL_DOT").length() > 0) {
      result.setTotalDot(Integer.parseInt(values.get("TOTAL_DOT")));
    } else {
      if (result.getTotalQ() != null && result.getUnitP() != null) {
        result.setTotalDot((int) Math.round(result.getTotalQ() * result.getUnitP()));
      } else {
        logger.info("drugno no totalQ=" + result.getDrugNo());
      }
    }
    if (values.get("ORDER_SEQ_NO") != null && values.get("ORDER_SEQ_NO").length() > 0) {
      result.setOrderSeqNo(Integer.parseInt(values.get("ORDER_SEQ_NO")));
    }
    if (values.get("START_TIME") != null && values.get("START_TIME").length() > 0) {
      result.setStartTime(DateTool.convertExcelDateTimeToChinese(values.get("START_TIME"), true));
    }
    if (values.get("END_TIME") != null && values.get("END_TIME").length() > 0) {
      result.setEndTime(DateTool.convertExcelDateTimeToChinese(values.get("END_TIME"), true));
    }
    if (result.getStartTime() == null && result.getInhCode() != null && values.get("FUNC_DATE") != null) {
      String date = removeSlash(values.get("FUNC_DATE"));
      result.setStartTime(date + "0000");
      result.setEndTime(result.getStartTime());
    }
    result.setPrsnId(values.get("PRSN_ID"));
    result.setChrMark(values.get("CHR_MARK"));
    result.setImgSource(values.get("IMG_SOURCE"));
    result.setPreNo(values.get("PRE_NO"));
    result.setFuncType(values.get("FUNC_TYPE"));
    result.setOwnExpMtrNo(values.get("OWN_EXP_MTR_NO"));
    result.setNonListMark(values.get("NON_LIST_MARK"));
    result.setNonListName(values.get("NON_LIST_NAME"));
    result.setCommHospId(values.get("COMM_HOSP_ID"));
    result.setDrugSerialNo(values.get("DRUG_SERIAL_NO"));
    result.setReceiveNo(values.get("RECEIVE_NO"));
    result.setRocId(values.get("ROC_ID"));
    result.setDirty(true);
    return result;
  }

  private void updateIpd(IP_D result, HashMap<String, String> values) {
    if (values.get("CASE_TYPE") != null) {
      if (values.get("CASE_TYPE").length() > 2) {
        result.setCaseType(cts.getCodeByDesc("IP_CASE_TYPE", values.get("CASE_TYPE")));
      }
    } else {
      result.setCaseType(values.get("CADE_TYPE"));
    }
    if (values.get("SEQ_NO") != null) {
      result.setSeqNo(Integer.parseInt(values.get("SEQ_NO")));
    }
    result.setRocId(values.get("ROC_ID"));
    result.setPartNo(values.get("PART_NO"));
    if (values.get("APPL_CAUSE_MARK") != null && values.get("APPL_CAUSE_MARK").length() == 1) {
      result.setApplCauseMark(values.get("APPL_CAUSE_MARK"));
    }
    result.setIdBirthYmd(removeSlash(values.get("ID_BIRTH_YMD")));
    result.setPayType(values.get("PAY_TYPE"));
    if (values.get("FUNC_TYPE") != null) {
      if (values.get("FUNC_TYPE").indexOf("科") > -1 || values.get("FUNC_TYPE").indexOf("學") > -1) {
        result.setFuncType(cts.getCodeByDesc("FUNC_TYPE", values.get("FUNC_TYPE")));
      } else {
        result.setFuncType(values.get("FUNC_TYPE"));
      }
    }
    result.setInDate(values.get("IN_DATE"));
    if (values.get("OUT_DATE") != null && values.get("OUT_DATE").length() > 6) {
      result.setOutDate(values.get("OUT_DATE"));
      result.setLeaveDate(DateTool.convertChineseToYear(result.getOutDate()));      
    }
    result.setApplStartDate(values.get("APPL_START_DATE"));
    result.setApplEndDate(values.get("APPL_END_DATE"));
    if (values.get("E_BED_DAY") != null && values.get("E_BED_DAY").length() > 0) {
      result.setEbedDay(Integer.parseInt(values.get("E_BED_DAY")));
    }
    if (values.get("S_BED_DAY") != null && values.get("S_BED_DAY").length() > 0) {
      result.setSbedDay(Integer.parseInt(values.get("S_BED_DAY")));
    }
    result.setPatientSource(values.get("PATIENT_SOURCE"));
    result.setCardSeqNo(values.get("CARD_SEQ_NO"));
    result.setTwDrgCode(values.get("TW_DRG_CODE"));
    result.setTwDrgPayType(values.get("TW_DRG_PAY_TYPE"));
    result.setPrsnId(values.get("PRSN_ID"));
    result.setCaseDrgCode(values.get("CASE_DRG_CODE"));
    if (values.get("TRAN_CODE") != null && values.get("TRAN_CODE").indexOf('.') > 0) {
      result.setTranCode(values.get("TRAN_CODE").substring(0,  values.get("TRAN_CODE").indexOf('.')));
    } else {
      result.setTranCode(values.get("TRAN_CODE"));
    }
    result.setIcdCm1(addICDCMDot(values.get("ICD_CM_1")));
    result.setIcdCm2(addICDCMDot(values.get("ICD_CM_2")));
    result.setIcdCm3(addICDCMDot(values.get("ICD_CM_3")));
    result.setIcdCm4(addICDCMDot(values.get("ICD_CM_4")));
    result.setIcdCm5(addICDCMDot(values.get("ICD_CM_5")));
    result.setIcdCm6(addICDCMDot(values.get("ICD_CM_6")));
    result.setIcdCm7(addICDCMDot(values.get("ICD_CM_7")));
    result.setIcdCm8(addICDCMDot(values.get("ICD_CM_8")));
    result.setIcdCm9(addICDCMDot(values.get("ICD_CM_9")));
    result.setIcdCm10(addICDCMDot(values.get("ICD_CM_10")));
    result.setIcdCm11(addICDCMDot(values.get("ICD_CM_11")));
    result.setIcdCm12(addICDCMDot(values.get("ICD_CM_12")));
    result.setIcdCm13(addICDCMDot(values.get("ICD_CM_13")));
    result.setIcdCm14(addICDCMDot(values.get("ICD_CM_14")));
    result.setIcdCm15(addICDCMDot(values.get("ICD_CM_15")));
    result.setIcdCm16(addICDCMDot(values.get("ICD_CM_16")));
    result.setIcdCm17(addICDCMDot(values.get("ICD_CM_17")));
    result.setIcdCm18(addICDCMDot(values.get("ICD_CM_18")));
    result.setIcdCm19(addICDCMDot(values.get("ICD_CM_19")));
    result.setIcdCm20(addICDCMDot(values.get("ICD_CM_20")));

    result.setIcdOpCode1(values.get("ICD_OP_CODE1"));
    result.setIcdOpCode2(values.get("ICD_OP_CODE2"));
    result.setIcdOpCode3(values.get("ICD_OP_CODE3"));
    result.setIcdOpCode4(values.get("ICD_OP_CODE4"));
    result.setIcdOpCode5(values.get("ICD_OP_CODE5"));
    result.setIcdOpCode6(values.get("ICD_OP_CODE6"));
    result.setIcdOpCode7(values.get("ICD_OP_CODE7"));
    result.setIcdOpCode8(values.get("ICD_OP_CODE8"));
    result.setIcdOpCode9(values.get("ICD_OP_CODE9"));
    result.setIcdOpCode10(values.get("ICD_OP_CODE10"));
    result.setIcdOpCode11(values.get("ICD_OP_CODE11"));
    result.setIcdOpCode12(values.get("ICD_OP_CODE12"));
    result.setIcdOpCode13(values.get("ICD_OP_CODE13"));
    result.setIcdOpCode14(values.get("ICD_OP_CODE14"));
    result.setIcdOpCode15(values.get("ICD_OP_CODE15"));
    result.setIcdOpCode16(values.get("ICD_OP_CODE16"));
    result.setIcdOpCode17(values.get("ICD_OP_CODE17"));
    result.setIcdOpCode18(values.get("ICD_OP_CODE18"));
    result.setIcdOpCode19(values.get("ICD_OP_CODE19"));
    result.setIcdOpCode20(values.get("ICD_OP_CODE20"));
    result.setOrderQty(getIntegerDefault0(values.get("ORDER_QTY")));
    result.setDiagDot(getIntegerDefault0(values.get("DIAG_DOT")));
    result.setRoomDot(getIntegerDefault0(values.get("ROOM_DOT")));
    result.setMealDot(getIntegerDefault0(values.get("MEAL_DOT")));
    result.setAminDot(getIntegerDefault0(values.get("AMIN_DOT")));
    result.setRadoDot(getIntegerDefault0(values.get("RADO_DOT")));
    result.setThrpDot(getIntegerDefault0(values.get("THRP_DOT")));
    result.setSgryDot(getIntegerDefault0(values.get("SGRY_DOT")));
    result.setPhscDot(getIntegerDefault0(values.get("PHSC_DOT")));
    result.setBlodDot(getIntegerDefault0(values.get("BLOD_DOT")));
    result.setHdDot(getIntegerDefault0(values.get("HD_DOT")));
    result.setAneDot(getIntegerDefault0(values.get("ANE_DOT")));
    result.setMetrDot(getIntegerDefault0(values.get("METR_DOT")));
    result.setDrugDot(getIntegerDefault0(values.get("DRUG_DOT")));
    result.setDsvcDot(getIntegerDefault0(values.get("DSVC_DOT")));
    result.setNrtpDot(getIntegerDefault0(values.get("NRTP_DOT")));
    result.setInjtDot(getIntegerDefault0(values.get("INJT_DOT")));
    result.setBabyDot(getIntegerDefault0(values.get("BABY_DOT")));
    result.setMedDot(getIntegerDefault0(values.get("MED_DOT")));
    result.setPartDot(getIntegerDefault0(values.get("PART_DOT")));
    result.setApplDot(getIntegerDefault0(values.get("APPL_DOT")));
    result.setEbAppl30Dot(getIntegerDefault0(values.get("EB_APPL30_DOT")));
    result.setEbPart30Dot(getIntegerDefault0(values.get("EB_PART30_DOT")));
    result.setEbAppl60Dot(getIntegerDefault0(values.get("EB_APPL60_DOT")));
    result.setEbPart60Dot(getIntegerDefault0(values.get("EB_PART60_DOT")));
    result.setEbAppl61Dot(getIntegerDefault0(values.get("EB_APPL61_DOT")));
    result.setEbPart61Dot(getIntegerDefault0(values.get("EB_PART61_DOT")));
    result.setSbAppl30Dot(getIntegerDefault0(values.get("SB_APPL30_DOT")));
    result.setSbPart30Dot(getIntegerDefault0(values.get("SB_PART30_DOT")));
    result.setSbAppl90Dot(getIntegerDefault0(values.get("SB_APPL90_DOT")));
    result.setSbPart90Dot(getIntegerDefault0(values.get("SB_PART90_DOT")));
    result.setSbAppl180Dot(getIntegerDefault0(values.get("SB_APPL180_DOT")));
    result.setSbPart180Dot(getIntegerDefault0(values.get("SB_PART180_DOT")));
    result.setSbAppl181Dot(getIntegerDefault0(values.get("SB_APPL181_DOT")));
    result.setSbPart181Dot(getIntegerDefault0(values.get("SB_PART181_DOT")));
    if (values.get("NB_BIRTHDAY") != null && values.get("NB_BIRTHDAY").length() > 0) {
      if (values.get("NB_BIRTHDAY").length() == 7) {
        result.setNbBirthday(values.get("NB_BIRTHDAY"));
      } else if (values.get("NB_BIRTHDAY").indexOf("/") > 0) {
        result.setNbBirthday(
            DateTool.convertExcelDateTimeToChinese(values.get("NB_BIRTHDAY"), false));
      }
    }
    result.setChildMark(values.get("CHILD_MARK"));
    result.setTwDrgsSuitMark(values.get("TW_DRGS_SUIT_MARK"));
    result.setName(values.get("NAME"));
    result.setAgencyId(values.get("AGENCY_ID"));
    result.setTranInHospId(values.get("TRAN_IN_HOSP_ID"));
    result.setTranOutHospId(values.get("TRAN_OUT_HOSP_ID"));
    result.setHospId(values.get("HOSP_ID"));
    result.setSvcPlan(values.get("SVC_PLAN"));
    result.setPilotProject(values.get("PILOT_PROJECT"));
    result.setNonApplDot(getIntegerDefault0(values.get("NON_APPL_DOT")));
    if (values.get("PRSN_NAME") != null) {
      result.setPrsnName(values.get("PRSN_NAME"));
    }
    result.setBedNo(values.get("BED_NO"));
    result.setUpdateAt(new java.util.Date());
    if (values.get("APPL_DOT_DRG") != null && values.get("APPL_DOT_DRG").length() > 0) {
      int drgDot = Integer.parseInt(values.get("APPL_DOT_DRG"));
      if (drgDot > 0) {
        result.setApplDot(drgDot);
      }
    }
    result.setOwnExpense(0);
  }
  
  private Integer getIntegerDefault0(String s) {
    if (s != null && s.length() > 0) {
      return Integer.parseInt(s);
    } else {
      return 0;
    }
  }
  
  /**
   * 由 IP_D Excel 檔的一列資料，轉成 IP_D object
   * 
   * @param values
   * @return
   */
  private IP_D getIpd(HashMap<String, String> values) {
    IP_D result = new IP_D();
    updateIpd(result, values);
    maskIPD(result);
    return result;
  }
  
  /**
   * 由 OP_P Excel 檔的一列資料，轉成 OP_P object
   * 
   * @param values
   * @return
   */
  private IP_P getIpp(HashMap<String, String> values, String format) {
    IP_P result = new IP_P();
    result.setInhCode(values.get("INH_CODE"));
    if (values.get("PAY_STATUS") != null && values.get("PAY_STATUS").length() > 0) {
      result.setPayBy(values.get("PAY_STATUS"));      
    }
    if ("THESE".equals(format)) {
      // 杏翔
      if (values.get("APPL_STATUS") != null && values.get("APPL_STATUS").length() > 0) {
        if (values.get("APPL_STATUS").indexOf('.') > 0) {
          // 自費
          result.setApplStatus(APPL_STATUS_OE);
          result.setPayBy("Y");
          result.setTotalQ(Double.parseDouble(values.get("APPL_STATUS")));
        } else {
          int applStatus = Integer.parseInt(values.get("APPL_STATUS"));
          if (applStatus > 0) {
            // 自費
            result.setApplStatus(APPL_STATUS_OE);
            result.setPayBy("Y");
            result.setTotalQ(Double.parseDouble(values.get("APPL_STATUS")));
          } else {
            // = 0 走健保
            result.setApplStatus(APPL_STATUS_THIS_MONTH);
            result.setPayBy("N");
          }
        }
      } else {
        result.setApplStatus(APPL_STATUS_THIS_MONTH);
        result.setPayBy("N");
      }
    } else  if ("LEADTEK".equals(format)) {
      // 麗臺格式
      result.setApplStatus("1".equals((String)values.get("APPL_STATUS")) ? APPL_STATUS_THIS_MONTH : 5);
      result.setPayBy((String)values.get("PAY_STATUS"));
      result.setInhClinicId(values.get("INH_NO"));
    }
    if (values.get("ORDER_SEQ_NO") != null && values.get("ORDER_SEQ_NO").length() > 0) {
      result.setOrderSeqNo(Integer.parseInt(values.get("ORDER_SEQ_NO")));
    }
    result.setOrderType(values.get("ORDER_TYPE"));
    result.setOrderCode(values.get("ORDER_CODE"));
    result.setPayRate(values.get("PAY_RATE"));
    if (values.get("DRUG_USE") != null && values.get("DRUG_USE").length() > 0) {
      result.setDrugUse(Double.parseDouble(values.get("DRUG_USE")));
    }
    result.setDrugFre(values.get("DRUG_FRE"));
    if (values.get("DRUG_PATH") != null && values.get("DRUG_PATH").length() < 5) {
      result.setDrugPath(values.get("DRUG_PATH"));
    }
    result.setConFuncType(values.get("CON_FUNC_TYPE"));
    result.setBedNo(values.get("BED_NO"));
    result.setCurePath(values.get("CURE_PATH"));
    if (values.get("TW_DRGS_CALCU") != null && values.get("TW_DRGS_CALCU").length() > 0) {
      result.setTwDrgsCalcu(Double.parseDouble(values.get("TW_DRGS_CALCU")));
    }
    result.setPartAccoData(values.get("PART_ACCO_DATA"));
    result.setDonater(values.get("DONATER"));
    if (values.get("START_TIME") != null && values.get("START_TIME").length() > 0) {
      result.setStartTime(DateTool.convertExcelDateTimeToChinese(values.get("START_TIME"), true));
    }
    if (values.get("END_TIME") != null && values.get("END_TIME").length() > 0) {
      result.setEndTime(DateTool.convertExcelDateTimeToChinese(values.get("END_TIME"), true));
    }
    if ("THESE".equals(format) && result.getApplStatus() == APPL_STATUS_OE) {
      // 杏翔格式，自費
      result.setTotalQ(Double.parseDouble(values.get("APPL_STATUS")));
      result.setUnitP(Float.parseFloat(values.get("UNIT_P_OW")));
      result.setTotalDot((int) Math.round(result.getTotalQ() * result.getUnitP()));
    } else {
      if (values.get("TOTAL_Q") != null && values.get("TOTAL_Q").length() > 0) {
        result.setTotalQ(Double.parseDouble(values.get("TOTAL_Q")));
      }
      if (values.get("UNIT_P") != null && values.get("UNIT_P").length() > 0) {
        result.setUnitP(Float.parseFloat(values.get("UNIT_P")));
      }
      if (values.get("TOTAL_DOT") != null && values.get("TOTAL_DOT").length() > 0) {
        if (values.get("TOTAL_DOT").indexOf('.') > 0) {
          result.setTotalDot((int) Math.round(Double.parseDouble(values.get("TOTAL_DOT"))));
        } else {
          result.setTotalDot(Integer.parseInt(values.get("TOTAL_DOT")));
        }
      }
    }
    result.setPreNo(values.get("PRE_NO"));
    result.setPrsnId(values.get("PRSN_ID"));
    result.setImgSource(values.get("IMG_SOURCE"));
    result.setFuncType(values.get("FUNC_TYPE"));
    result.setOwnExpMtrNo(values.get("OWN_EXP_MTR_NO"));
    if (values.get("NON_LIST_MARK") != null &&  values.get("NON_LIST_MARK").length() > 0) {
      if (values.get("NON_LIST_MARK").length() == 1) {
        result.setNonListMark(values.get("NON_LIST_MARK"));
      } else {
        result.setNonListMark("1");
      }
    }
    result.setNonListName(values.get("NON_LIST_NAME"));
    result.setCommHospId(values.get("COMM_HOSP_ID"));
    result.setDrugSerialNo(values.get("DRUG_SERIAL_NO"));
    result.setRocid(values.get("ROC_ID"));
    result.setPrsnName(values.get("PRSN_NAME"));
    result.setIcdCm1(values.get("ICD_CM_1"));
    result.setName(values.get("NAME"));
    result.setInhMr(values.get("INH_MR"));
    result.setBirthday(values.get("ID_BIRTH_YMD"));
    return result;
  }

  private void findDiffOpd(List<FILE_DIFF> diffList, MR mr, OP_D opd) {
    if (diffList == null) {
      CODE_TABLE ct = cts.getCodeTable("INFECTIOUS", opd.getIcdCm1());
      mr.setInfectious((ct == null) ? 0 : 1);
      mr.setIcdcm1(opd.getIcdCm1());
      MRDetail.updateIcdcmOtherOP(mr, opd);
      MRDetail.updateIcdpcsOP(mr, opd);
      MRDetail.updateIcdAllByAlphabet(mr);
      mr = mrDao.save(mr);
      opd.setMrId(mr.getId());
      if (opd.getOwnExpense() == null) {
        opd.setOwnExpense(0);
      }
      opd = opdDao.save(opd);
      //mr.setdId(opd.getId());
    } else {
      if (!mr.getIcdcm1().equals(opd.getIcdCm1())) {
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
  }
  
  private void findDiffIpd(List<FILE_DIFF> diffList, MR mr, IP_D ipd) {
    if (diffList == null) {
      CODE_TABLE ct = cts.getCodeTable("INFECTIOUS", ipd.getIcdCm1());
      mr.setInfectious((ct == null) ? 0 : 1);
      mr.setIcdcm1(ipd.getIcdCm1());
      MRDetail.updateIcdcmOtherIP(mr, ipd);
      MRDetail.updateIcdpcsIP(mr, ipd);
      MRDetail.updateIcdAllByAlphabet(mr);
      mr = mrDao.save(mr);
      ipd.setMrId(mr.getId());
      if (ipd.getOwnExpense() == null) {
        ipd.setOwnExpense(0);
      }
      ipd = ipdDao.save(ipd);
    } else {
      if (!mr.getIcdcm1().equals(ipd.getIcdCm1())) {
        FILE_DIFF fileDiff = new FILE_DIFF(mr.getId(), "icdCM", ipd.getIcdCm1());
        fileDiff.setArrayIndex(0);
        diffList.add(fileDiff);
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
  }

  private void saveDiffList(List<FILE_DIFF> diffList, MR mr) {
    if (diffList == null || diffList.size() == 0) {
      return;
    }
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
      updateMyMrStatus(mr.getId().longValue(), MR_STATUS.WAIT_CONFIRM.value(), mr, userId,
          user.getUsername(), user.getDisplayName(), false);
    } else {
      updateMyMrStatus(mr.getId().longValue(), MR_STATUS.WAIT_CONFIRM.value(), mr, userId, null,
          mr.getApplName(), false);
    }
    mr.setStatus(MR_STATUS.WAIT_CONFIRM.value());
  }

  public String checkOppHSSFSheet(File file) {
    HSSFWorkbook workbook = null;
    if (!file.getName().endsWith(".xls")) {
      return "檔案格式有誤";
    }
    try {
      if (file.getName().toUpperCase().indexOf("OPP") > -1
          || file.getName().toUpperCase().indexOf("IPP") > -1) {
        workbook = new HSSFWorkbook(new FileInputStream(file));
        if (workbook.getSheetAt(0).getRow(0).getPhysicalNumberOfCells() > 10) {
          if (getOppApplYm(workbook.getSheetAt(0)) == null) {
            if (file.getName().toUpperCase().indexOf("OPP") > -1) {
              return "未找到對應的 OP_D 資料";
            } else {
              return "未找到對應的 IP_D 資料";
            }
          }
        } else {
          return "檔案格式有誤";
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (workbook != null) {
        try {
          workbook.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return null;
  }

  public String getOppApplYm(HSSFSheet sheet) {
    // 取得各欄位名稱的位置
    HashMap<Integer, String> columnMap = ExcelUtil.readTitleRow(sheet.getRow(0));
    // 第一筆資料
    HashMap<String, String> values = ExcelUtil.readCellValue(columnMap, sheet.getRow(1));
    return getApplYmByInhNo(values);
  }

  private MR findMRByInhClinicId(List<MR> list, String inhClinicId) {
    for (MR mr : list) {
      if (inhClinicId.equals(mr.getInhClinicId())) {
        return mr;
      }
    }
    return null;
  }
  
  private MR findMRByMrId(List<MR> list, Long mrId) {
    for (MR mr : list) {
      if (mr.getId().longValue() == mrId.longValue()) {
        return mr;
      }
    }
    return null;
  }
  
  private MR findMRByInhMrId(List<MR> list, String inhMrId) {
    for (MR mr : list) {
      if (mr.getInhMrId() != null && mr.getInhMrId().equals(inhMrId)) {
        return mr;
      }
    }
    return null;
  }

  private OP_D findOpdById(List<OP_D> opdList, long id, boolean remove) {
    int index = -1;
    for (int i=opdList.size() - 1; i >=0 ; i--) {
      OP_D op_D = opdList.get(i);
      if (op_D.getId().longValue() == id) {
        index = i;
        break;
      }
    }
    if (index > -1) {
      if (remove) {
        return opdList.remove(index);
      }
      return opdList.get(index);
    }
    return null;
  }
  
  private IP_D findIpdById(List<IP_D> ipdList, long id) {
    for (IP_D ip_D : ipdList) {
      if (ip_D.getId().longValue() == id) {
        return ip_D;
      }
    }
    return null;
  }

  public boolean readOppHSSFSheet(HSSFSheet sheet) {
    // 由標題列取得各欄位名稱的位置
    HashMap<Integer, String> columnMap = ExcelUtil.readTitleRow(sheet.getRow(0));
    long start = System.currentTimeMillis();
    HashMap<String, List<OP_P>> opps = aggregateLeadtekOPP(sheet, 1, columnMap);
    //OP_T opt = getOpt(applYm);

    // 避免重複insert
    List<MR> mrList = mrDao.getMrByInhClinicId(new ArrayList<String>(opps.keySet()));
    List<Long> mrIdList = vpts.getMrId(mrList, null);
    List<OP_D> opdList = opdDao.getOpdListByMrId(mrIdList);
    HashMap<Long, List<OP_P>> oppListDB = getOPPByMrIdList(mrIdList);
    // 要存到 DB 的 batch
    List<OP_P> oppBatch = new ArrayList<OP_P>();

    HashMap<String, String> payCodeType = getPayCodeType();
    CompareWarning cw = new CompareWarning(parameters.getByCat("COMPARE_WARNING"), cts);
    for (String inhNo : opps.keySet()) {
      List<OP_P> oppList = opps.get(inhNo);
      int index = -1;
      for (int i = mrList.size() - 1; i >= 0; i--) {
        MR mr = mrList.get(i);
        if (mr.getInhClinicId() != null && mr.getInhClinicId().equals(inhNo)) {
          index = i;
          break;
        }
      }
      if (index == -1) {
        System.out.println("mr not found, inhNo=" + inhNo);
        continue;
      }
      MR mr = mrList.remove(index);
      OP_D opd = findOpdById(opdList, mr.getdId(), true);
      if (opd == null) {
        System.out.println("opd is null, did=" + mr.getdId());
        continue;
      }

      // 存放有差異的欄位
      List<FILE_DIFF> diffList = null;
      if (mr.getStatus().intValue() != MR_STATUS.NO_CHANGE.value()
          && shouldCompareWarning(mr,cw, opd.getFuncType())) {
        diffList = new ArrayList<FILE_DIFF>();
        moDao.deleteByMrId(mr.getId());
      }

      saveMrAndOpd(mr, opd, oppList, payCodeType, oppBatch);
    
      // } else {
      // // 需比對有無差異
      // List<OP_P> opps = oppDao.findByOpdIdOrderByOrderSeqNo(opd.getId());
      // List<MO> moList = new ArrayList<MO>();
      // for (int i = 0; i < opps.size(); i++) {
      // OP_P oppOld = opps.get(i);
      // boolean isFound = false;
      // for (int j = 0; i < oppListXML.size(); j++) {
      // OP_P oppNew = oppListXML.get(j);
      // if (compareOPP(mr.getId(), oppOld, oppNew, diffList, moList)) {
      // isFound = true;
      // break;
      // }
      // }
      // if (!isFound) {
      // addDiff(mr.getId(), null, oppOld.getOrderSeqNo().intValue(), (OP_P) null, diffList,
      // moList);
      // }
      // }
      // if (oppListXML.size() > opps.size()) {
      // for(int i=opps.size(); i<oppListXML.size(); i++) {
      // OP_P opp = oppListXML.get(i);
      // addDiff(mr.getId(), opp.getDrugNo(), opp.getOrderSeqNo().intValue(), opp, diffList,
      // moList);
      // }
      // }
      // if (moList.size() > 0) {
      // mr.setChangeOrder(1);
      // for (MO mo : moList) {
      // moDao.save(mo);
      // }
      // }
      // }
    }
    if (oppBatch.size() > 0) {
      oppDao.saveAll(oppBatch);
    }
    long usedTime = System.currentTimeMillis() - start;
    logger.info("readOppHSSFSheet used " + usedTime + " ms.");
    System.out.println("readOppHSSFSheet used " + usedTime + " ms.");
    return true;
  }
  
  public void readIpdSheet(HSSFSheet sheet) {
    int titleRowIndex = 0;
    // 由標題列取得各欄位名稱的位置
    HashMap<Integer, String> columnMap = ExcelUtil.readTitleRow(sheet.getRow(0));

    // 第一筆資料
    HashMap<String, String> values = null;
    
    String[] orderDateString = getOrderDateFromSheet(sheet, titleRowIndex + 1, getColumnIndex(columnMap, "APPL_END_DATE"));
    java.util.Date[] orderDate = new java.util.Date[2];
    orderDate[0] = DateTool.convertChineseToYear(orderDateString[0]);
    orderDate[1] = DateTool.convertChineseToYear(orderDateString[1]);
    
    String applYm = orderDateString[0].substring(0, 5);
    IP_T ipt = getIpt(applYm);
    
    java.util.Date[] orderDateMore = add1Month(orderDate);
    // 避免重複insert
    List<MR> mrList = mrDao.findByMrEndDateAndDataFormatOrderById(XMLConstant.DATA_FORMAT_IP, orderDateMore[0], orderDateMore[1]);
 // 避免重複insert
    List<IP_D> ipdList = ipdDao.findByIDFromMR(orderDateMore[0], orderDateMore[1]);
    System.out.println("db mr count=" + mrList.size() + ",ip_D count=" + ipdList.size());
    CompareWarning cw = new CompareWarning(parameters.getByCat("COMPARE_WARNING"), cts);
    for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
      HSSFRow row = sheet.getRow(i);
      if (row == null) {
        // System.out.println("sheet:" + i + ", row=" + j + " is null");
        continue;
      }
      values = ExcelUtil.readCellValue(columnMap, row);
      if (values.get("ICD_CM_1") == null || values.get("ICD_CM_1").length() == 0) {
        continue;
      }
      // 存放有差異的欄位
      List<FILE_DIFF> diffList = null;
      IP_D ipd = getIpd(values);
      ipd.setIptId(ipt.getId());
      maskIPD(ipd);
      MR mr = findMR(mrList, values.get("INH_NO"));
      if (mr == null) {
        mr = new MR(ipd);
        mr.setStatus(MR_STATUS.NO_CHANGE.value());
      } else {
        if (mr.getStatus().intValue() != MR_STATUS.NO_CHANGE.value() && 
            shouldCompareWarning(mr, cw, ipd.getFuncType())) {
          diffList = new ArrayList<FILE_DIFF>();
          clearFileDiff(mr.getId());
        }
        IP_D ipdDB = findIpdById(ipdList, mr.getdId());
        if (ipdDB != null) {
          ipd.setId(ipdDB.getId());
        }
        mr.updateMR(ipd, diffList, cts);
        if (diffList != null && diffList.size() > 0) {
          mr.setChangeOther(1);
        }
      }
      updateMRByExcel(values, mr);
      mr.setApplYm(applYm);

      if (diffList == null) {
        mr.setIcdcm1(ipd.getIcdCm1());
        MRDetail.updateIcdcmOtherIP(mr, ipd);
        MRDetail.updateIcdpcsIP(mr, ipd);
        MRDetail.updateIcdAllByAlphabet(mr);
        CODE_TABLE ct = cts.getCodeTable("INFECTIOUS", ipd.getIcdCm1());
        mr.setInfectious((ct == null) ? 0 : 1);
        mr = mrDao.save(mr);
        ipd.setMrId(mr.getId());
        ipd = ipdDao.save(ipd);
        mr.setdId(ipd.getId());
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
      
      saveDiffList(diffList, mr);
      mr = mrDao.save(mr);
    }
  }
  
  public void readIppHSSFSheet(HSSFSheet sheet) {
    // 由標題列取得各欄位名稱的位置
    int titleRowIndex = 0;
    HashMap<Integer, String> columnMap = ExcelUtil.readTitleRowHSSFRow(sheet.getRow(titleRowIndex), parameters.getByCat("IP_P"));
    // 第一筆資料
    HashMap<String, String> values = ExcelUtil.readCellValue(columnMap, sheet.getRow(1));

    String applYm = getApplYmByInhNo(values);
    if (applYm == null) {
      return;
    }
    logger.info("readIppHSSFSheet applYm=" + applYm);
    IP_T ipt = getIpt(values, applYm);

    // 避免重複insert
    List<MR> mrList = mrDao.findByApplYmAndDataFormatOrderById(applYm, XMLConstant.DATA_FORMAT_IP);
    List<IP_D> ipdList = ipdDao.findByApplYM(applYm);
    HashMap<String, List<IP_P>> ippMap = aggregateLeadtekIPP(sheet, titleRowIndex + 1, columnMap);
    List<HashMap<String, Object>> ippList = getIPPByIPTID(ipt.getId());
    // 要存到 DB 的 batch
    List<IP_P> ippBatch = new ArrayList<IP_P>();
    // 有異動或新增的IP_D list
    List<IP_D> dirtyIPD = new ArrayList<IP_D>(); 
    // 有異動或新增的病歷
    List<MR> dirtyMR = new ArrayList<MR>();
    // 有異動或新增的IPP
    List<IP_P> ippListDB = new ArrayList<>();

    HashMap<String, String> payCodeType = getPayCodeType();
    CompareWarning cw =
        new CompareWarning(parameters.getByCat("COMPARE_WARNING"), cts);
    
    for (String key : ippMap.keySet()) {
      List<IP_P> newIppList = ippMap.get(key);
      MR mr = findMRByInhClinicId(mrList, newIppList.get(0).getInhClinicId());
      if (mr == null) {
        logger.info("mr not found, inhNo=" + newIppList.get(0).getInhClinicId());
        continue;
      }
      IP_D ipd = findIpdById(ipdList, mr.getdId());
      if (ipd == null) {
        continue;
      }

      List<FILE_DIFF> diffList = null;
      if (mr.getStatus().intValue() != MR_STATUS.NO_CHANGE.value()
          && shouldCompareWarning(mr, cw, ipd.getFuncType())) {
        diffList = new ArrayList<FILE_DIFF>();
        moDao.deleteByMrId(mr.getId());
      }

      boolean isIpdDirty = false;
//      StringBuffer sb = new StringBuffer((mr.getCodeAll() == null) ? "," : mr.getCodeAll());
//      StringBuffer sbInhCode = new StringBuffer((mr.getInhCode() == null) ? "," : mr.getInhCode());
      for (int i = 0; i < newIppList.size(); i++) {
        IP_P ipp = newIppList.get(i);
        ipp.setMrId(mr.getId());
        ipp.setIpdId(ipd.getId());
        maskIPP(ipp);
        
     // 檢查該筆醫令是否已存在DB
        boolean isFound = false;
        for (int j = 0; j < ippList.size(); j++) {
          HashMap<String, Object> ippDB = ippList.get(j);

          if (((BigInteger) ippDB.get("ipdId")).longValue() != ipd.getId().longValue()) {
            continue;
          }
          if (((Integer) ippDB.get("orderSeqNo")).intValue() != ipp.getOrderSeqNo().intValue()) {
            continue;
          }
          isFound = true;
          if (diffList == null) {
            ipp.setId(((BigInteger) ippDB.get("id")).longValue());
            if (ipp.getOrderCode() != null) {
              ipp.setPayCodeType(payCodeType.get(ipp.getOrderCode()));
            }
            ipp.setMrId(mr.getId());
            ippBatch.add(ipp);
            ippListDB.add(ipp);
            if (ippBatch.size() > XMLConstant.BATCH) {
              ippDao.saveAll(ippBatch);
              ippBatch.clear();
            }
          } else {
            // 需比對有無差異
            List<IP_P> ipps = ippDao.findByIpdIdAndOrderSeqNo(ipd.getId(), ipp.getOrderSeqNo());
            List<MO> moList = new ArrayList<MO>();
            for (int k = 0; k < ipps.size(); j++) {
              IP_P ippOld = ipps.get(k);
              if (compareIPP(mr.getId(), ippOld, ipp, diffList, moList)) {
                break;
              }
            }
          }
          break;
        }
        if (!isFound)  {
          ippListDB.add(ipp);
          if (ipp.getOrderCode() != null) {
            if (mr.getCodeAll() == null) {
              mr.setCodeAll("," + ipp.getOrderCode() + ",");
              isIpdDirty = true;
            } else if (mr.getCodeAll().indexOf("," + ipp.getOrderCode() + ",") < 0){
              mr.setCodeAll(mr.getCodeAll() + ipp.getOrderCode() + ",");
              isIpdDirty = true;
            }
          }
          if (ipp.getInhCode() != null && ipp.getInhCode().length() > 0) {
            if (mr.getInhCode() == null) {
              mr.setInhCode("," + ipp.getInhCode() + ",");
              isIpdDirty = true;
            } else if (mr.getInhCode().indexOf("," + ipp.getInhCode() + ",") < 0){
              mr.setInhCode(mr.getInhCode() + ipp.getInhCode() + ",");
              isIpdDirty = true;
            }
          }
        }

        ippBatch.add(ipp);
        if (ippBatch.size() > XMLConstant.BATCH) {
          ippDao.saveAll(ippBatch);
          ippBatch.clear();
        }
      }
      if (isIpdDirty) {
        mr.setIcdcm1(null);
        dirtyIPD.add(ipd);
        dirtyMR.add(mr);
      }
    }
    if (ippBatch.size() > 0) {
      ippDao.saveAll(ippBatch);
      ippBatch.clear();
    }
    saveMrAndIpd(dirtyMR, dirtyIPD, ippListDB); 
  }
  
  public void readSOPSheet(HSSFSheet sheet) {
    // 由標題列取得各欄位名稱的位置
    HashMap<Integer, String> columnMap = ExcelUtil.readTitleRow(sheet.getRow(0), parameters.getByCat("SOP"));
    // 第一筆資料
    HashMap<String, String> values = ExcelUtil.readCellValue(columnMap, sheet.getRow(1));

    String applYm = getApplYmByInhNo(values);
    if (applYm == null) {
      return;
    }
  
    for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
      HSSFRow row = sheet.getRow(i);
      if (row == null) {
        // System.out.println("sheet:" + i + ", row=" + j + " is null");
        continue;
      }
      values = ExcelUtil.readCellValue(columnMap, row);
      List<Long> mrIdList = mrDao.getIdByInhClinicId((String) values.get("INH_NO"));
      if (mrIdList == null || mrIdList.size() == 0) {
        continue;
      }
      MR_SO mrSo = mrSoDao.findByInhNo((String) values.get("INH_NO"));
      if (mrSo == null) {
        mrSo = new MR_SO();
        mrSo.setInhNo((String) values.get("INH_NO"));
      }
      mrSo.setMrId(mrIdList.get(0));
      mrSo.setObjectText(values.get("OBJECT_TEXT"));
      mrSo.setSubjectText(values.get("SUBJECT_TEXT"));
      mrSo.setDischargeText(values.get("DISCHARGE_TEXT"));
      mrSoDao.save(mrSo);
    }
  }
  
  /**
   * 檢查特定治療項目是否有異動
   * @param list
   * @param newOpd
   */
  public void checkDiffOpdCureItem(List<FILE_DIFF> list, OP_D newOpd) {
    Optional<OP_D> optional = opdDao.findById(newOpd.getId());
    if (!optional.isPresent()) {
      return;
    }
    OP_D old = optional.get();
    checkDiffItem(old.getPayType(), newOpd.getPayType(), list, newOpd.getMrId(), "payType", 0);
    checkDiffItem(old.getPartNo(), newOpd.getPartNo(), list, newOpd.getMrId(), "partNo", 0);
    checkDiffInteger(old.getPartDot(), newOpd.getPartDot(), list, newOpd.getMrId(), "partDot", 0);
    
    if (checkDiffItem(
        old.getCureItemNo1(), newOpd.getCureItemNo1(), list, newOpd.getMrId(), "cureItems", 0)) {
      return;
    }
    if (checkDiffItem(
        old.getCureItemNo2(), newOpd.getCureItemNo2(), list, newOpd.getMrId(), "cureItems", 1)) {
      return;
    }
    if (checkDiffItem(
        old.getCureItemNo3(), newOpd.getCureItemNo3(), list, newOpd.getMrId(), "cureItems", 2)) {
      return;
    }
    if (checkDiffItem(
        old.getCureItemNo4(), newOpd.getCureItemNo4(), list, newOpd.getMrId(), "cureItems", 3)) {
      return;
    }
  }
  
  /**
   * 比對新舊申報檔的欄位是否有異
   * @param oldItem
   * @param newItem
   * @param list
   * @param mrId
   * @param diffFieldName
   * @param index
   * @return true:該欄位有資料，false:該欄位無資料
   */
  private boolean checkDiffItem(String oldItem, String newItem, List<FILE_DIFF> list,
     Long mrId, String diffFieldName, int index) {
    if (oldItem == null && newItem == null) {
      return true;
    }
    if (oldItem == null && newItem != null) {
      list.add(new FILE_DIFF(mrId, diffFieldName, index, newItem));
    } else if (oldItem != null && !oldItem.equals(newItem)) {
      list.add(new FILE_DIFF(mrId, diffFieldName, index, newItem));
    }
    return false;
  }
  
  private boolean checkDiffInteger(Integer oldInt, Integer newInt, List<FILE_DIFF> list,
      Long mrId, String diffFieldName, int index) {
     if (oldInt == null && newInt == null) {
       return true;
     }
     if (oldInt == null && newInt != null) {
       list.add(new FILE_DIFF(mrId, diffFieldName, index, newInt.toString()));
     } else if (oldInt != null && oldInt.intValue() != newInt.intValue()) {
       list.add(new FILE_DIFF(mrId, diffFieldName, index, newInt.toString()));
     }
     return false;
   }
  
  /**
   * 檢查特定治療項目是否有異動
   * @param list
   * @param newOpd
   */
  public void checkDiffIpdItem(List<FILE_DIFF> list, IP_D newIpd) {
    Optional<IP_D> optional = ipdDao.findById(newIpd.getId());
    if (!optional.isPresent()) {
      return;
    }
    IP_D old = optional.get();
    checkDiffItem(old.getCaseType(), newIpd.getCaseType(), list, newIpd.getMrId(), "caseType", 0);
    checkDiffItem(old.getPayType(), newIpd.getPayType(), list, newIpd.getMrId(), "payType", 0);
    checkDiffItem(old.getPartNo(), newIpd.getPartNo(), list, newIpd.getMrId(), "partNo", 0);
    checkDiffItem(old.getPatientSource(), newIpd.getPatientSource(), list, newIpd.getMrId(), "patientSource", 0);
    checkDiffItem(old.getTwDrgsSuitMark() , newIpd.getTwDrgsSuitMark(), list, newIpd.getMrId(), "twDrgsSuitMark", 0);
    checkDiffInteger(old.getAminDot(), newIpd.getAminDot(), list, newIpd.getMrId(), "aminDot", 0);
    checkDiffInteger(old.getAneDot(), newIpd.getAneDot(), list, newIpd.getMrId(), "aneDot", 0);
    checkDiffInteger(old.getDiagDot(), newIpd.getDiagDot(), list, newIpd.getMrId(), "diagDot", 0);
    checkDiffInteger(old.getDrugDot(), newIpd.getDrugDot(), list, newIpd.getMrId(), "drugDot", 0);
    checkDiffInteger(old.getDsvcDot(), newIpd.getDsvcDot(), list, newIpd.getMrId(), "dsvcDot", 0);
    checkDiffInteger(old.getBabyDot(), newIpd.getBabyDot(), list, newIpd.getMrId(), "babyDot", 0);
    checkDiffInteger(old.getBlodDot(), newIpd.getBlodDot(), list, newIpd.getMrId(), "blodDot", 0);
    checkDiffInteger(old.getHdDot(), newIpd.getHdDot(), list, newIpd.getMrId(), "hdDot", 0);
    checkDiffInteger(old.getInjtDot(), newIpd.getInjtDot(), list, newIpd.getMrId(), "injtDot", 0);
    checkDiffInteger(old.getNrtpDot(), newIpd.getNrtpDot(), list, newIpd.getMrId(), "nrtpDot", 0);
    checkDiffInteger(old.getNonApplDot(), newIpd.getNonApplDot(), list, newIpd.getMrId(), "nonApplDot", 0);
    checkDiffInteger(old.getMealDot(), newIpd.getMealDot(), list, newIpd.getMrId(), "mealDot", 0);
    checkDiffInteger(old.getMedDot(), newIpd.getMedDot(), list, newIpd.getMrId(), "medDot", 0);
    checkDiffInteger(old.getMetrDot(), newIpd.getMetrDot(), list, newIpd.getMrId(), "metrDot", 0);
    checkDiffInteger(old.getPartDot(), newIpd.getPartDot(), list, newIpd.getMrId(), "partDot", 0);
    checkDiffInteger(old.getPhscDot() , newIpd.getPhscDot(), list, newIpd.getMrId(), "phscDot", 0);
  }
  
  private void changeEmptyToNull(DEDUCTED_NOTE note) {
    if ("".equals(note.getCat())) {
      note.setCat(null);
    }
    if ("".equals(note.getItem())) {
      note.setItem(null);
    }
    if ("".equals(note.getReason())) {
      note.setReason(null);
    }
    if ("".equals(note.getAfrNoPayCode())) {
      note.setAfrNoPayCode(null);
    }
    if ("".equals(note.getAfrNoPayDesc())) {
      note.setAfrNoPayDesc(null);
    }
    if ("".equals(note.getAfrNote())) {
      note.setAfrNote(null);
    }
    if ("".equals(note.getDisputeNoPayDesc())) {
      note.setDisputeNoPayDesc(null);
    }
    if ("".equals(note.getDisputeNote())) {
      note.setDisputeNote(null);
    }
    if ("".equals(note.getNote())) {
      note.setNote(null);
    }
    if ("".equals(note.getSubCat())) {
      note.setSubCat(null);
    }
    if ("".equals(note.getDisputeNoPayCode())) {
      note.setDisputeNoPayCode(null);
    }
    if ("".equals(note.getL3())) {
      note.setL3(null);
    }
    if ("".equals(note.getL2())) {
      note.setL2(null);
    }
    if ("".equals(note.getL1())) {
      note.setL1(null);
    }
  }
  
  /**
   * 
   * @param importUsedTime 匯入使用時間
   * @param isImporting true:仍在匯入病歷，false:已匯入完成
   */
  public void checkAll(long importUsedTime, boolean isImporting) {
    if (isImporting) {
      is.setIntelligentRunningTime(INTELLIGENT_REASON.XML.value(), Long.MAX_VALUE);
      return;
    }
    boolean waitCheckAllFinished = false;
    long extendTime = CHECK_ALL_AFTER_UPLOAD_WAIT_SECOND * 1000;
    long startRunningTime = is.getIntelligentRunningTime(INTELLIGENT_REASON.XML.value());
    if (startRunningTime > 0) {
      if (startRunningTime == Long.MAX_VALUE) {
        is.setIntelligentRunningTime(INTELLIGENT_REASON.XML.value(),
            System.currentTimeMillis() + extendTime);
      } else {
        // 已經在跑所有條件檢查，就不處理
        if (startRunningTime > System.currentTimeMillis()) {
          // 等待執行
          is.extendIntelligentRunning(INTELLIGENT_REASON.XML.value(), extendTime);
          return;
        }
        if (startRunningTime <= System.currentTimeMillis()) {
          // 正在執行
          waitCheckAllFinished = true;
        }
      }
    } else {
      // server 空閒，等待 extendTime後再跑，避免user又再上傳其他申報檔案
      is.setIntelligentRunningTime(INTELLIGENT_REASON.XML.value(), System.currentTimeMillis() + extendTime);
    }
    
    final boolean waitUntilFinished = waitCheckAllFinished;
    Thread thread = new Thread(new Runnable() {

      @Override
      public void run() {
        long startRunningTime = 0;
        boolean needWaitUntilFinished = waitUntilFinished;
        do {
          try {
            Thread.sleep(5000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          startRunningTime = is.getIntelligentRunningTime(INTELLIGENT_REASON.XML.value());
          if (startRunningTime == Long.MAX_VALUE) {
            // 又有新的申報檔進來
            logger.info("new file coming, stop checkAll");
            break;
          }
          if (needWaitUntilFinished) {
            if (startRunningTime < 0) {
              // 已完成
              is.extendIntelligentRunning(INTELLIGENT_REASON.XML.value(), extendTime);
              needWaitUntilFinished = false;
            } else if (startRunningTime < System.currentTimeMillis()) {
                // 等待時間已過，開始執行
                break;
            }
          } else {
            if (startRunningTime < System.currentTimeMillis()) {
              // 等待時間已過，開始執行
              break;
            }
          }
          logger.info("wait to run checkAll:" + (startRunningTime - System.currentTimeMillis()));
        } while(true);
        if (startRunningTime < Long.MAX_VALUE) {
          // 又有新的申報檔進來
          is.checkAllIntelligentCondition();
        }
      }
    });
    thread.start();
  }
  
  /**
   * 讀取杏翔HIS產出的醫療記錄檔
   * @param sheet
   */
  public void readTheseSheet(XSSFSheet sheet) {
    long startImport = System.currentTimeMillis();
    checkAll(0, true);
    if (THESE_OPP.equals(sheet.getRow(0).getCell(0).getStringCellValue())) {
      readTheseOPP(sheet);
    } else if (THESE_OPD.equals(sheet.getRow(0).getCell(0).getStringCellValue())) {
      readTheseOPD(sheet);
    } else if (THESE_IPD.equals(sheet.getRow(0).getCell(0).getStringCellValue())) {
      readTheseIPD(sheet);
    } else if (THESE_IPP.equals(sheet.getRow(0).getCell(0).getStringCellValue())) {
      readTheseIPP(sheet);
    }
    long usedTime = System.currentTimeMillis() - startImport;
    checkAll(usedTime, false);
  }
  
  /**
   * 讀取杏翔門診病歷檔
   * @param sheet
   * @return 病歷民國年月
   */
  private String readTheseOPD(XSSFSheet sheet) {
    //System.out.println("readTheseOPD, isMASK=" + ISMASK);
    
    int titleRowIndex = 0;
    // 取得各欄位名稱的位置
    HashMap<Integer, String> columnMap = ExcelUtil.readTitleRow(sheet.getRow(titleRowIndex), parameters.getByCat("OP_D"));
    // 第一筆資料
    HashMap<String, String> values = ExcelUtil.readCellValue(columnMap, sheet.getRow(titleRowIndex + 1));
    // testColumnMap(columnMap);
    String[] orderDateString = getOrderDateFromSheet(sheet, titleRowIndex + 1, getColumnIndex(columnMap, "FUNC_END_DATE"));
    java.util.Date[] orderDate = new java.util.Date[2];
    orderDate[0] = DateTool.convertChineseToYear(orderDateString[0]);
    orderDate[1] = DateTool.convertChineseToYear(orderDateString[1]);
    String result = orderDateString[0].substring(0, 5);
    OP_T opt = getOpt(result);
    
    // 避免重複insert
    List<MR> mrList = mrDao.findByMrEndDateAndDataFormatOrderById(XMLConstant.DATA_FORMAT_OP, orderDate[0], orderDate[1]);
    //System.out.println("mrList:" + mrList.size() + "," +  orderDate[0] + " , " +  orderDate[1]);
    List<OP_D> opdList = opdDao.findByIDFromMR(orderDate[0], orderDate[1]);
    //System.out.println("opdList:" + opdList.size());
    CompareWarning cw = new CompareWarning(parameters.getByCat("COMPARE_WARNING"), cts);
    
    for (int i = titleRowIndex + 1; i < sheet.getPhysicalNumberOfRows(); i++) {
      XSSFRow row = sheet.getRow(i);
      if (row == null || row.getCell(0) == null) {
        // System.out.println("sheet:" + i + ", row=" + j + " is null");
        continue;
      }
      values = formatOPDValues(ExcelUtil.readCellValue(columnMap, row));
      // 存放有差異的欄位
      List<FILE_DIFF> diffList = null;
      
      //System.out.println(values.get("ROC_ID") + "," + values.get("CARD_NO"));
      OP_D opdDB = null; 
      OP_D opd = findOpdByOpdValues(opdList, values);
      if (opd.getId() != null) {
        opdDB = opd;
        opd = getOpd(values);
        opd.setId(opdDB.getId());
        opd.setMrId(opdDB.getMrId());
      }
      if (opd.getCaseType() == null) {
        // 此 opd 為依據醫令清單建立，因此不需跑檢查更新記錄
        String funcType = opd.getFuncType();
        // updateOpd(opd, values);
        if (funcType != null) {
          // 病歷的科別是錯誤的，改讀醫令的科別
          opd.setFuncType(funcType);
        }
      }
      opd.setOptId(opt.getId());
      MR mr = null;
      if (opd.getMrId() != null) {
        mr = findMRByMrId(mrList, opd.getMrId());
        //mr.setFuncType(opd.getFuncType());
      }
      if (mr == null) {
        mr = new MR(opd);
        mr.setStatus(MR_STATUS.NO_CHANGE.value());
      } else {
        if (mr.getStatus().intValue() != MR_STATUS.NO_CHANGE.value()
            && shouldCompareWarning(mr, cw, opd.getFuncType())) {
          diffList = new ArrayList<FILE_DIFF>();
          clearFileDiff(mr.getId());
          checkDiffOpdCureItem(diffList, opd);
        }
        mr.updateMR(opd, diffList, cts);
        if (diffList != null && diffList.size() > 0) {
          mr.setChangeOther(1);
        }
      }
      updateMRByExcel(values, mr);
      mr.setApplYm(result);
      findDiffOpd(diffList, mr, opd);
      saveDiffList(diffList, mr);
      mr = mrDao.save(mr);
    }
    return result;
  }
  
  /**
   * 讀取杏翔門診醫令檔
   * @param sheet
   */
  private void readTheseOPP(XSSFSheet sheet) {
    //System.out.println("readTheseOPP, isMASK=" + ISMASK);
    // 取得各欄位名稱的位置
    int titleRowIndex = 2;
    HashMap<Integer, String> columnMap = ExcelUtil.readTitleRow(sheet.getRow(titleRowIndex), parameters.getByCat("OP_P"));
    HashMap<String, List<OP_P>> oppsNew = aggregateTheseOPP(sheet, titleRowIndex + 1, columnMap);
    List<OP_P> minMax = oppsNew.get("MINMAX");
    java.util.Date[] orderDate = new java.util.Date[2];
    orderDate[0] = DateTool.convertChineseToYear(minMax.get(0).getStartTime().substring(0, 7));
    orderDate[1] = DateTool.convertChineseToYear(minMax.get(1).getEndTime().substring(0, 7));

    Date[] sqlDate = new Date[2];
    sqlDate[0] = new Date(orderDate[0].getTime());
    sqlDate[1] = new Date(orderDate[1].getTime());

    String applYm = minMax.get(1).getEndTime().substring(0, 5);
    //System.out.println("start:" + orderDate[0] + ", end:" + orderDate[1] + ", applYm=" + applYm);
    OP_T opt = getOpt(applYm);
    // 避免重複insert，現存在DB中的 MR
    List<MR> mrList = mrDao.getByDataFormatAndMrDateBetween(XMLConstant.DATA_FORMAT_OP,
        orderDate[0], orderDate[1]);
    // 避免重複insert，現存在DB中的 OP_D
    List<OP_D> opdList = opdDao.findByIDFromMR(sqlDate[0], sqlDate[1]);
    HashMap<String, Integer> rocIdDateCount = rocIdDateCount(opdList);
    // 避免重複insert，現存在DB中的 OP_P
    List<OP_P> oppList = oppDao.getByMrIdFromMR(orderDate[0], orderDate[1]);
    // 要存到 DB 的 batch
    List<OP_P> oppBatch = new ArrayList<OP_P>();
    CompareWarning cw =
        new CompareWarning(parameters.getByCat("COMPARE_WARNING"), cts);
    HashMap<String, String> values = null;
    
    for (int i = titleRowIndex + 1; i < sheet.getPhysicalNumberOfRows(); i++) {
      XSSFRow row = sheet.getRow(i);
      if (row == null || row.getCell(0) == null) {
        continue;
      }
      values = formatOPDValues(ExcelUtil.readCellValue(columnMap, row));
      OP_P oppNew = findOppByOppValues(oppsNew, values);
//      if ("M221233913".equals(values.get("ROC_ID"))) {
//        if (oppNew == null) {
//          System.out.println("oppNew is null, cardNo=" + values.get("CARD_NO") + ", icd=" + values.get("ICD_CM_1"));
//        } else {
//          System.out.println("oppNew " + oppNew.getInhCode() + ", cardNo=" + values.get("CARD_NO") + ", icd=" + values.get("ICD_CM_1"));
//        }
//      }
      if (oppNew == null) {
        // 相同病歷相同醫令已處理過
        continue;
      }
      MR mr = null;
      OP_D opd = findOpdByTheseOpp(mrList, opdList, rocIdDateCount, values);
      if (opd == null) {
        mr = initialMrAndOpdByOpp(values);
        mr.setApplYm(applYm);
        opd = newOpdByOpp(values);
        opd.setOptId(opt.getId());
        opd.setMrId(mr.getId());
        opd = opdDao.save(opd);
        opdList.add(opd);
        mr.setdId(opd.getId());
        mrList.add(mr);
        Integer rocIdDateCountValue = rocIdDateCount.get(values.get("ROC_ID") + opd.getFuncEndDate());
        if (rocIdDateCountValue == null) {
          rocIdDateCount.put(values.get("ROC_ID") + opd.getFuncEndDate(), Integer.valueOf(1));
        } else {
          rocIdDateCount.put(values.get("ROC_ID") + opd.getFuncEndDate(), Integer.valueOf(rocIdDateCountValue.intValue() + 1));
        }
        //System.out.println("new opd=" + opd.getId() + " opd.MrId=" + opd.getMrId() + ",mr=" + mr.getId() + " did=" + mr.getdId());
      } else {
        // 科別以醫令為主
        mr = findMRByMrId(mrList, opd.getMrId());
        if (opd.getFuncType() == null || !opd.getFuncType().equals(values.get("FUNC_TYPE"))) {
          // 用主診斷碼是否為 null 當做mr是否要更新的依據
          opd.setFuncType(values.get("FUNC_TYPE"));
          mr.setFuncType(values.get("FUNC_TYPE"));
          mr.setIcdcm1(null);  
        }
      }
      // 調整OPD及MR的起訖時間
      if (!oppNew.getStartTime().equals(oppNew.getEndTime())) {
        if (!oppNew.getStartTime().substring(0, 7).equals(opd.getFuncDate()) ||
            !oppNew.getEndTime().substring(0, 7).equals(opd.getFuncEndDate())){
          opd.setFuncDate(oppNew.getStartTime().substring(0, 7));
          opd.setFuncEndDate(oppNew.getEndTime().substring(0, 7));
          opdDao.updateFuncTypeById(oppNew.getStartTime().substring(0, 7), oppNew.getEndTime().substring(0, 7), opd.getId());
          
          mr.setMrEndDate(DateTool.convertChineseToYear(opd.getFuncEndDate()));
          mr.setMrDate(DateTool.convertChineseToYear(opd.getFuncDate()));
        }
      }
      // 存放有差異的欄位
      List<FILE_DIFF> diffList = null;
      if (mr.getStatus().intValue() != MR_STATUS.NO_CHANGE.value()
          && shouldCompareWarning(mr, cw, opd.getFuncType())) {
        diffList = new ArrayList<FILE_DIFF>();
        moDao.deleteByMrId(mr.getId());
      }

      OP_P opp = findOppByOppValues(oppList, oppNew, (mr == null) ? null : mr.getId());
      if (diffList == null) {
        if (opp == null) {
          // DB 尚未有該筆醫令
          opp = oppNew;
        } else {
          boolean isDiff = updateOpp(opp, oppNew);
          if (!isDiff) {
            // 資料未異動
            // System.out.println("資料未異動 " + opp.getId() + "," + opp.getDrugNo() + "," + opp.getInhCode() + "," +  oppNew.getRocId());
            opp.setDirty(false);
            continue;
          } else {
            opp.setDirty(true);
         // 資料有異動
            // System.out.println("資料有異動 " + opp.getId() + "," + opp.getDrugNo() + "," + opp.getInhCode() + "," +  oppNew.getRocId());
          }
        }
        maskOPP(opp, opd.getCaseType());
        if (opp.getDrugNo() != null) {
          if (mr.getCodeAll() == null) {
            mr.setCodeAll(",");
          }
          if (mr.getIcdcm1() == null) {
            mr.setCodeAll(mr.getCodeAll() + opp.getDrugNo() + ",");
          } else if (mr.getCodeAll().indexOf("," + opp.getDrugNo() + ",") < 0){
            mr.setCodeAll(mr.getCodeAll() + opp.getDrugNo() + ",");
            // 用主診斷碼是否為 null 當做mr是否要更新的依據
            mr.setIcdcm1(null);
          }
        }
        if (opp.getInhCode() != null && opp.getInhCode().length() > 0) {
          if (mr.getInhCode() == null) {
            mr.setInhCode("," + opp.getInhCode() + ",");
          } else if (mr.getInhCode().indexOf("," + opp.getInhCode() + ",") < 0){
            mr.setInhCode(mr.getInhCode() + opp.getInhCode() + ",");
            // 用主診斷碼是否為 null 當做mr是否要更新的依據
            mr.setIcdcm1(null);
          }
        }
        opp.setOpdId(opd.getId());
        opp.setMrId(mr.getId());
        if (opp.isDirty()) {
          opp.setUpdateAt(new java.util.Date());
        }
        if (opp.getId() == null) {
          // 還未存到DB
          oppList.add(opp);
        }
        if (!opp.isDirty()) {
//          System.out.println("add oppBatch id=" + opp.getId() + "," + opp.getInhCode() + ","
//              + opp.getRocId() + ",updateAt=" + opp.getUpdateAt());
        } else {
          oppBatch.add(opp);
          if (oppBatch.size() > XMLConstant.BATCH) {
            oppDao.saveAll(oppBatch);
            oppBatch.clear();
          }
        }
      }
      // } else {
      // // 需比對有無差異
      // List<OP_P> opps = oppDao.findByOpdIdOrderByOrderSeqNo(opd.getId());
      // List<MO> moList = new ArrayList<MO>();
      // for (int i = 0; i < opps.size(); i++) {
      // OP_P oppOld = opps.get(i);
      // boolean isFound = false;
      // for (int j = 0; i < oppListXML.size(); j++) {
      // OP_P oppNew = oppListXML.get(j);
      // if (compareOPP(mr.getId(), oppOld, oppNew, diffList, moList)) {
      // isFound = true;
      // break;
      // }
      // }
      // if (!isFound) {
      // addDiff(mr.getId(), null, oppOld.getOrderSeqNo().intValue(), (OP_P) null, diffList,
      // moList);
      // }
      // }
      // if (oppListXML.size() > opps.size()) {
      // for(int i=opps.size(); i<oppListXML.size(); i++) {
      // OP_P opp = oppListXML.get(i);
      // addDiff(mr.getId(), opp.getDrugNo(), opp.getOrderSeqNo().intValue(), opp, diffList,
      // moList);
      // }
      // }
      // if (moList.size() > 0) {
      // mr.setChangeOrder(1);
      // for (MO mo : moList) {
      // moDao.save(mo);
      // }
      // }
      // }
    }
   
    if (oppBatch.size() > 0) {
      oppDao.saveAll(oppBatch);
    }
    saveMrAndOpd(mrList, opdList, oppList);
  }
  
  public static String addICDCMDot(String s) {
    if (s == null || s.indexOf('.') > 0 || s.length() == 0) {
      return s;
    }
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < s.length(); i++) {
      sb.append(s.charAt(i));
       if (i == 2) {
         sb.append('.');
       }
    }
    if (sb.charAt(sb.length() - 1) == '.') {
      sb.deleteCharAt(sb.length() - 1);
    }
    return sb.toString();
  }
  
  private int getColumnIndex(HashMap<Integer, String> columnMap, String columnName) {
    for (Integer key : columnMap.keySet()) {
      if (columnName.equals(columnMap.get(key))) {
        return key.intValue();
      }
    }
    return -1;
  }
  
  /**
   * 取出醫令清單中的最小、最大日期
   * @param sheet
   * @param columnIndex
   * @return 民國年月日
   */
  private String[] getOrderDateFromSheet(XSSFSheet sheet, int startRowIndex, int columnIndex) {
    String[] result = new String[2];
    int min = Integer.MAX_VALUE;
    int max = Integer.MIN_VALUE; 
    for (int i = startRowIndex; i < sheet.getPhysicalNumberOfRows(); i++) {
      XSSFRow row = sheet.getRow(i);
      if (row == null) {
        // System.out.println("sheet:" + i + ", row=" + j + " is null");
        continue;
      }
      if (row.getCell(columnIndex) == null) {
        continue;
      }
      
      int dateInt = 0;
      String date = null;
      if (row.getCell(columnIndex).getCellType() == CellType.NUMERIC) {
        date = String.valueOf(row.getCell(columnIndex).getNumericCellValue());
        if (date.endsWith(".0")) {
          date = date.substring(0, date.length() - 2);
        }
        dateInt = Integer.parseInt(date);
      } else {
        String cellValue = row.getCell(columnIndex).getStringCellValue().trim();
        if (cellValue.length() == 0) {
          continue;
        }
        date = removeSlash(cellValue);
        dateInt = Integer.parseInt(date);
      }
      if (dateInt < min) {
        min = dateInt;
        result[0] = date;
      }
      if (dateInt > max) {
        max = dateInt;
        result[1] = date;
      }
    }
    return result;
  }
  
  /**
   * 取出醫令清單中的最小、最大日期
   * @param sheet
   * @param columnIndex
   * @return 民國年月日
   */
  private String[] getOrderDateFromSheet(HSSFSheet sheet, int startRowIndex, int columnIndex) {
    String[] result = new String[2];
    int min = Integer.MAX_VALUE;
    int max = Integer.MIN_VALUE; 
    for (int i = startRowIndex; i < sheet.getPhysicalNumberOfRows(); i++) {
      HSSFRow row = sheet.getRow(i);
      if (row == null) {
        // System.out.println("sheet:" + i + ", row=" + j + " is null");
        continue;
      }
      if (row.getCell(columnIndex) == null) {
        continue;
      }
      
      int dateInt = 0;
      String date = null;
      if (row.getCell(columnIndex).getCellType() == CellType.NUMERIC) {
        date = String.valueOf(row.getCell(columnIndex).getNumericCellValue());
        if (date.endsWith(".0")) {
          date = date.substring(0, date.length() - 2);
        }
        dateInt = Integer.parseInt(date);
      } else {
        String cellValue = row.getCell(columnIndex).getStringCellValue().trim();
        if (cellValue.length() == 0) {
          continue;
        }
        date = removeSlash(cellValue);
        dateInt = Integer.parseInt(date);
      }
      if (dateInt < min) {
        min = dateInt;
        result[0] = date;
      }
      if (dateInt > max) {
        max = dateInt;
        result[1] = date;
      }
    }
    return result;
  }
  
  /**
   * 杏翔格式由醫令取得醫療記錄
   * @param opdList
   * @param id
   * @return
   */
  private OP_D findOpdByTheseOpp(List<MR> mrList, List<OP_D> opdList, HashMap<String, Integer> countMap, 
      HashMap<String, String> values) {
    for (OP_D op_D : opdList) {
      if (op_D.getRocId() == null) {
        MR mr = findMRByInhMrId(mrList, values.get("INH_MR"));
        if (mr == null) {
          return null;
        }
        return findOpdById(opdList, mr.getdId(), false);
      } else if (op_D.getRocId().equals(values.get("ROC_ID"))) {
       // 先比對卡號，不一致再比其他欄位
        if (op_D.getCardNo() != null) {
          if (op_D.getCardNo().equals(values.get("CARD_NO"))) {
            return op_D;
          }
          if (values.get("CARD_NO") == null) {
            // 自費病歷
            continue;
          }
        }
        if (op_D.getFuncEndDate().equals(values.get("FUNC_DATE"))) {
          // 先比對領藥號，比不到若當天只有一筆病歷，就回傳該筆病歷
          if (op_D.getReceiveNo() != null) {
            if (op_D.getReceiveNo().equals(values.get("RECEIVE_NO"))) {
              return op_D;
            }
            if (op_D.getReceiveNo() != null && values.get("RECEIVE_NO") != null
                && values.get("RECEIVE_NO").indexOf(op_D.getReceiveNo()) > 0) {
              return op_D;
            }
          } else if (op_D.getReceiveNo() == null && values.get("RECEIVE_NO") == null) {
            return op_D;
          }
        } else if (op_D.getFuncDate() != null) {
          // 因醫令時間可能位於病歷的看診日期和治療日期
          int startDate = Integer.parseInt(op_D.getFuncDate());
          int endDate = Integer.parseInt(op_D.getFuncEndDate());
          int funcDate = Integer.parseInt(values.get("FUNC_DATE"));
          if (funcDate < endDate && funcDate >= startDate) {
            if (isOpdMatchOpp(op_D, countMap, values)) {
              return op_D;
            }  
          }
        }
      }
    }
    return null;
  }
  
  private boolean isOpdMatchOpp(OP_D op_D, HashMap<String, Integer> countMap,  HashMap<String, String> values) {
    // 因醫令領藥號和病歷的領藥號對不起來，所以若同一病患當天只有一筆病歷，就抓該筆病歷
    Integer count = countMap.get(values.get("ROC_ID") + op_D.getFuncEndDate());
    if (count == null) {
      return false;
    }
    if (count.intValue() == 1) {
      return true;
    }
    if (op_D.getReceiveNo() != null) {
      if (op_D.getReceiveNo().equals(values.get("RECEIVE_NO"))) {
        return true;
      }
      if (op_D.getReceiveNo() != null && values.get("RECEIVE_NO") != null
          && values.get("RECEIVE_NO").indexOf(op_D.getReceiveNo()) > 0) {
        return true;
      }
    } else if (op_D.getReceiveNo() == null && values.get("RECEIVE_NO") == null) {
      return true;
    }
    if (op_D.getPrsnName().equals(values.get("PRSN_NAME"))) {
      return true;
    }
    return false;
  }
  
  private MR initialMrAndOpdByOpp(HashMap<String, String> values) {
    MR result = new MR();
    result.setDataFormat(XMLConstant.DATA_FORMAT_OP);
    result.setCodeAll(",");
    result.setInhCode(",");
    result.setStatus(MR_STATUS.NO_CHANGE.value());
    result.setFuncType(values.get("FUNC_TYPE"));
    result.setInhMrId(values.get("INH_MR"));
    result.setName(values.get("NAME"));
    result.setRocId(values.get("ROC_ID"));
    result.setPrsnName(values.get("PRSN_NAME"));
    result.setMrEndDate(DateTool.convertChineseToYear(values.get("FUNC_DATE")));
    result.setMrDate(DateTool.convertChineseToYear(values.get("FUNC_DATE")));
    result.setUpdateAt(new java.util.Date());
    result.setOwnExpense(0);
    result.setReportDot(0L);
    result.setApplDot(0);
    result.setTotalDot(0);
    result.setDeductedDot(0);
    result = mrDao.save(result);
    return result;
  }
  
  private void saveMrAndOpd(List<MR> mrList, List<OP_D> opdList, List<OP_P> oppList) {
    for (MR mr : mrList) {
      // 自費金額
      int ownExpense = 0;
      // 病歷點數
      int total = 0;
      // 用藥明細點數小計, drug_no length=10
      int drugDot = 0;
      // 診療明細點數小計, order type = 2
      int treatDot = 0;
      // 診察費點數, order type = 0
      int diagDot = 0;
      // 藥事服務費點數(調劑費), order type = 9
      int dsvcDot = 0;
      // 特殊材料明細點數小計, order type = 3
      int metrDot = 0;
      // 是否為自費案件
      boolean isZ = true;
      // 該筆病歷的所有醫令是否有新增或異動過
      boolean isDirty = false;
      for(int i = oppList.size() - 1; i >=0 ; i--) {
        OP_P opp = oppList.get(i);
        if (opp.getMrId() != mr.getId()) {
          continue;
        }
        if (opp.isDirty()) {
          isDirty = true;
        }
        total += opp.getTotalDot();
        if (!PAY_BY_Z.equals(opp.getPayBy())) {
          isZ = false;
        }
        if (PAY_BY_Y.equals(opp.getPayBy()) || PAY_BY_Z.equals(opp.getPayBy()) ) {
          ownExpense += opp.getTotalDot();
        } else if (opp.getDrugNo() != null && opp.getDrugNo().length() == 10) {
          drugDot += opp.getTotalDot();
        } else if (opp.getDrugNo() != null && opp.getDrugNo().length() == 12) {
          metrDot += opp.getTotalDot();
        } else if ("2".equals(opp.getOrderType())) {
          treatDot += opp.getTotalDot();
        } else if ("0".equals(opp.getOrderType())) {
          diagDot += opp.getTotalDot();
        } else if ("9".equals(opp.getOrderType())) {
          dsvcDot += opp.getTotalDot();
        }
        oppList.remove(i);
      }

      if (!isDirty) {
        continue;
      }
      if (mr.getOwnExpense().intValue() != ownExpense) {
        mr.setIcdcm1(null);
        mr.setOwnExpense(ownExpense);
      }
      int opdIndex = -1;
      for (int i = 0; i < opdList.size(); i++) {
        OP_D opd = opdList.get(i);
        if (opd.getMrId().longValue() == mr.getId().longValue()) {
          opdIndex = i;
          break;
        }
      }
      OP_D opd = opdList.remove(opdIndex);
      if (opd.getDrugDot() == null) {
        opd.setDrugDot(drugDot);
      } 
      if (opd.getTreatDot() == null) {
        opd.setTreatDot(treatDot);
      }
      if (opd.getMetrDot() == null) {
        opd.setMetrDot(metrDot);
      }
      if (opd.getDiagDot() == null) {
        opd.setDiagDot(diagDot);
      }
      if (opd.getDsvcDot() == null) {
        opd.setDsvcDot(dsvcDot);
      }
      if (opd.getTotalDot() != null) {
        mr.setTotalDot(opd.getTotalDot() + mr.getOwnExpense());
      } else {
        mr.setTotalDot(total);
      }
      
      //if (mr.getClinic() != null) {
        // 該筆OP_D有讀過病歷檔，所以要更新科別，改以醫令科別為主
//        if (opd.getRocId().equals("S101840488")) {
//          System.out.println("new func type=" + opd.getFuncType() + ", id=" + opd.getId());
//        }
      opdDao.updateFuncTypeById(opd.getFuncType(), opd.getDrugDot(), opd.getTreatDot(), opd.getMetrDot(),
        opd.getDiagDot(), opd.getDsvcDot(), opd.getId());
      //}
      mr.setApplStatus(isZ ? 5 : 1);
      mr.setIcdcm1(opd.getIcdCm1());
      mr.setUpdateAt(new java.util.Date());
      mr.setdId(opd.getId());
      mrDao.save(mr);
    }
  }
  
  private void saveMrAndOpd(MR mr, OP_D opd, List<OP_P> oppList, HashMap<String, String> payCodeType, List<OP_P> oppBatch) {
    StringBuffer sb = new StringBuffer((mr.getCodeAll() == null) ? "," : mr.getCodeAll());
    StringBuffer sbInhCode = new StringBuffer((mr.getInhCode() == null) ? "," : mr.getInhCode());
    // 自費金額
    int ownExpense = 0;
    // 病歷點數
    int total = 0;
    // 用藥明細點數小計, drug_no length=10
    int drugDot = 0;
    // 診療明細點數小計, order type = 2
    int treatDot = 0;
    // 診察費點數, order type = 0
    int diagDot = 0;
    // 藥事服務費點數(調劑費), order type = 9
    int dsvcDot = 0;
    // 特殊材料明細點數小計, order type = 3
    int metrDot = 0;
    // 是否為自費案件
    boolean isZ = true;
    // 該筆病歷的所有醫令是否有新增或異動過
    boolean isDirty = false;
    for (int i = oppList.size() - 1; i >= 0; i--) {
      OP_P opp = oppList.get(i);
      opp.setMrId(mr.getId());
      total += opp.getTotalDot();
      if (!PAY_BY_Z.equals(opp.getPayBy())) {
        isZ = false;
      }
      if (PAY_BY_Y.equals(opp.getPayBy()) || PAY_BY_Z.equals(opp.getPayBy())) {
        ownExpense += opp.getTotalDot();
      } else if (opp.getDrugNo() != null && opp.getDrugNo().length() == 10) {
        drugDot += opp.getTotalDot();
      } else if (opp.getDrugNo() != null && opp.getDrugNo().length() == 12) {
        metrDot += opp.getTotalDot();
      } else if ("2".equals(opp.getOrderType())) {
        treatDot += opp.getTotalDot();
      } else if ("0".equals(opp.getOrderType())) {
        diagDot += opp.getTotalDot();
      } else if ("9".equals(opp.getOrderType())) {
        dsvcDot += opp.getTotalDot();
      }
      if (opp.getDrugNo() != null) {
        sb.append(opp.getDrugNo());
        sb.append(",");
        opp.setPayCodeType(payCodeType.get(opp.getDrugNo()));
      }
      if (opp.getInhCode() != null && opp.getInhCode().length() > 0) {
        sbInhCode.append(opp.getInhCode());
        sbInhCode.append(",");
      }
      opp.setOpdId(opd.getId());
      // updateOPPID(oppList, opp);
      opp.setMrId(mr.getId());
      maskOPP(opp, opd.getCaseType());
      opp.setUpdateAt(new java.util.Date());
      oppBatch.add(opp);
      if (oppBatch.size() > XMLConstant.BATCH) {
        oppDao.saveAll(oppBatch);
        oppBatch.clear();
      }
      oppList.remove(i);
    }

    mr.setCodeAll(sb.toString());
    mr.setInhCode(sbInhCode.toString());
    if (mr.getOwnExpense().intValue() != ownExpense) {
      mr.setOwnExpense(ownExpense);
    }
    if (opd.getDrugDot() == null) {
      opd.setDrugDot(drugDot);
    }
    if (opd.getTreatDot() == null) {
      opd.setTreatDot(treatDot);
    }
    if (opd.getMetrDot() == null) {
      opd.setMetrDot(metrDot);
    }
    if (opd.getDiagDot() == null) {
      opd.setDiagDot(diagDot);
    }
    if (opd.getDsvcDot() == null) {
      opd.setDsvcDot(dsvcDot);
    }
    if (opd.getTotalDot() != null) {
      mr.setTotalDot(opd.getTotalDot() + mr.getOwnExpense());
    } else {
      mr.setTotalDot(total);
    }

    // if (mr.getClinic() != null) {
    // 該筆OP_D有讀過病歷檔，所以要更新科別，改以醫令科別為主
    // if (opd.getRocId().equals("S101840488")) {
    // System.out.println("new func type=" + opd.getFuncType() + ", id=" + opd.getId());
    // }
    opdDao.updateFuncTypeById(opd.getFuncType(), opd.getDrugDot(), opd.getTreatDot(),
        opd.getMetrDot(), opd.getDiagDot(), opd.getDsvcDot(), opd.getId());
    // }
    mr.setApplStatus(isZ ? 5 : 1);
    mr.setUpdateAt(new java.util.Date());
    mrDao.save(mr);
  }
  
  private void saveMrAndIpd(List<MR> mrList, List<IP_D> ipdList, List<IP_P> ippList) {
    for (MR mr : mrList) {
      if (mr.getIcdcm1() != null) {
        continue;
      }
      int ipdIndex = -1;
      for(int i=0; i<ipdList.size();i++) {
        IP_D ipd = ipdList.get(i);
        if (ipd.getMrId().longValue() == mr.getId().longValue()) {
          ipdIndex = i;
          break;
        }
      }
      IP_D ipd = ipdList.remove(ipdIndex);
      // 自費金額
      int ownExpense = 0;
      // 病歷點數
      int total = 0;
      // 檢查費
      int aminDot = 0;
      // 藥費
      int drugDot = 0;
      // 病房費
      int roomDot = 0;
      // 診察費點數
      int diagDot = 0;
      // 藥事服務費點數(調劑費)
      int dsvcDot = 0;
      // 注射
      int injtDot = 0;
      // 麻醉費
      int aneDot = 0;
      // 治療處置費
      int thrpDot = 0;
      for (int i=ippList.size() -1; i>=0; i--) {
        IP_P ipp = ippList.get(i);
        if (ipp.getMrId().longValue() == mr.getId().longValue()) {
          total += ipp.getTotalDot();
          if ("Y".equals(ipp.getPayBy()) || "Z".equals(ipp.getPayBy())) {
            ownExpense += ipp.getTotalDot();
          } else if ("2".equals(ipp.getPayCodeType())){
            diagDot += ipp.getTotalDot();
          } else if ("7".equals(ipp.getPayCodeType())){
            drugDot += ipp.getTotalDot();
          } else if ("6".equals(ipp.getPayCodeType())){
            dsvcDot += ipp.getTotalDot();
          } else if ("9".equals(ipp.getPayCodeType())){
            injtDot += ipp.getTotalDot();
          } else if ("3".equals(ipp.getPayCodeType())){
            roomDot += ipp.getTotalDot();
          } else if ("16".equals(ipp.getPayCodeType())){
            aneDot += ipp.getTotalDot();
          } else if ("18".equals(ipp.getPayCodeType())){
            thrpDot += ipp.getTotalDot();
          } else if ("19".equals(ipp.getPayCodeType())){
            aminDot += ipp.getTotalDot();
          }
          ippList.remove(i);
        }
      }
      if (ipd.getDiagDot() == null || ipd.getDiagDot().intValue() != diagDot) {
        ipd.setDiagDot(diagDot);
        mr.setIcdcm1(null);
      }
      if (ipd.getDrugDot() == null || ipd.getDrugDot().intValue() != drugDot) {
        ipd.setDrugDot(drugDot);
        mr.setIcdcm1(null);
      }
      if (ipd.getDsvcDot() == null || ipd.getDsvcDot().intValue() != dsvcDot) {
        ipd.setDsvcDot(dsvcDot);
        mr.setIcdcm1(null);
      }
      if (ipd.getInjtDot() == null || ipd.getInjtDot().intValue() != injtDot) {
        ipd.setInjtDot(injtDot);
        mr.setIcdcm1(null);
      }
      if (ipd.getRoomDot() == null || ipd.getRoomDot().intValue() != roomDot) {
        ipd.setRoomDot(roomDot);
        mr.setIcdcm1(null);
      }
      if (ipd.getAneDot() == null || ipd.getAneDot().intValue() != aneDot) {
        ipd.setAneDot(aneDot);
        mr.setIcdcm1(null);
      }
      if (ipd.getThrpDot() == null || ipd.getThrpDot().intValue() != thrpDot) {
        ipd.setThrpDot(thrpDot);
        mr.setIcdcm1(null);
      }
      if (ipd.getAminDot() == null || ipd.getAminDot().intValue() != aminDot) {
        ipd.setAminDot(thrpDot);
        mr.setIcdcm1(null);
      }
      if (mr.getOwnExpense() == null || mr.getOwnExpense().intValue() != ownExpense) {
        mr.setOwnExpense(ownExpense);
        ipd.setOwnExpense(ownExpense);
        mr.setIcdcm1(null);
      }
      
      if (mr.getIcdcm1() == null) {
        // 該筆IP_D有讀過病歷檔，所以要更新科別，改以醫令科別為主
        // System.out.println("updateFuncTypeById:" + ipd.getId() + ",roomDot=" + roomDot + ",
        // diagDot=" + diagDot);
        ipdDao.updateFuncTypeById(ipd.getFuncType(), ipd.getApplStartDate(), 
            ipd.getApplEndDate(), aneDot, dsvcDot, diagDot, drugDot, injtDot,
            roomDot, thrpDot, aminDot, ownExpense, ipd.getId());
        mr.setTotalDot(total);
        mr.setIcdcm1(ipd.getIcdCm1());
        mr.setUpdateAt(new java.util.Date());
        mr.setdId(ipd.getId());
        mr.setApplStatus(1);
        mrDao.save(mr);
      }
    }
  }
  
  private OP_D findOpdByOpdValues(List<OP_D> list, HashMap<String, String> values) {
    for (OP_D opd : list) {
      if (opd.getFuncEndDate().equals(values.get("FUNC_END_DATE")) && opd.getRocId().equals(values.get("ROC_ID"))) {
          //&& opd.getPrsnName().equals(values.get("PRSN_NAME"))) {
        if (opd.getCardNo() != null && opd.getCardNo().equals(values.get("CARD_NO"))) {
          return opd;
        }
        if (opd.getReceiveNo() != null) {
          if (opd.getReceiveNo().equals(values.get("RECEIVE_NO"))) {
            return opd;
          }
          if (opd.getReceiveNo() != null && values.get("RECEIVE_NO") != null &&
              opd.getReceiveNo().indexOf(values.get("RECEIVE_NO")) > 0) {
            return opd;
          }
        } else if (opd.getReceiveNo() == null && values.get("RECEIVE_NO") == null) {
          return opd;
        }
      }
    }
    return getOpd(values);
  }
  
  private OP_P findOppByOppValues(List<OP_P> list, OP_P oppNew, Long mrId) {
    for (OP_P opp : list) {
      if (mrId != null && opp.getMrId().longValue() == mrId.longValue()) {
        if (opp.getInhCode() != null && opp.getInhCode().equals(oppNew.getInhCode())) {
          return opp;
        }
      }
    }
    return null;
  }
  
  private IP_D findIpdByIpdValues(List<IP_D> list, HashMap<String, String> values) {
    for (IP_D ipd : list) {
      if (ipd.getPrsnName() == null) {
        continue;
      }
      if ((ipd.getApplEndDate().equals(values.get("APPL_END_DATE")) || 
          ipd.getApplStartDate().equals(values.get("APPL_START_DATE")))
          && ipd.getRocId().equals(values.get("ROC_ID"))
          && ipd.getPrsnName().equals(values.get("PRSN_NAME"))) {
        return ipd;
      }
    }
    return getIpd(values);
  }

   /**
   * 讀取杏翔格式的excel檔
   * @param sheet
   * @return 病歷檔的申報年月
   */
  private String readTheseIPD(XSSFSheet sheet) {
    //System.out.println("readTheseIPD");
    int titleRowIndex = 0;
    // 取得各欄位名稱的位置
    HashMap<Integer, String> columnMap = ExcelUtil.readTitleRow(sheet.getRow(0), parameters.getByCat("IP_D"));
//    for (Integer key : columnMap.keySet()) {
//      System.out.println(key + ":" + columnMap.get(key));
//    }
    // 第一筆資料
    HashMap<String, String> values = null;
    
    String[] orderDateString = getOrderDateFromSheet(sheet, titleRowIndex + 1, getColumnIndex(columnMap, "APPL_END_DATE"));
    java.util.Date[] orderDate = new java.util.Date[2];
    orderDate[0] = DateTool.convertChineseToYear(orderDateString[0]);
    orderDate[1] = DateTool.convertChineseToYear(orderDateString[1]);
    
    String result = orderDateString[0].substring(0, 5);
    IP_T ipt = getIpt(result);
    
    java.util.Date[] orderDateMore = add1Month(orderDate);
    // 避免重複insert
    List<MR> mrList = mrDao.findByMrEndDateAndDataFormatOrderById(XMLConstant.DATA_FORMAT_IP, orderDateMore[0], orderDateMore[1]);
    
    List<IP_D> ipdList = ipdDao.findByIDFromMR(orderDateMore[0], orderDateMore[1]);
    CompareWarning cw = new CompareWarning(parameters.getByCat("COMPARE_WARNING"), cts);
    
    //System.out.println("readTheseIPD select from " + orderDateMore[0] + " to " + orderDateMore[1]+", ipdList=" + ipdList.size() + ", mrList=" + mrList.size());
    for (int i = titleRowIndex + 1; i < sheet.getPhysicalNumberOfRows(); i++) {
      XSSFRow row = sheet.getRow(i);
      if (row == null || row.getCell(0) == null) {
        // System.out.println("sheet:" + i + ", row=" + j + " is null");
        continue;
      }
      values = formatOPDValues(ExcelUtil.readCellValue(columnMap, row));
      // 存放有差異的欄位
      List<FILE_DIFF> diffList = null;
      IP_D ipd = findIpdByIpdValues(ipdList, values); 
      ipd.setIptId(ipt.getId());
      MR mr = null;
      if (ipd.getMrId() != null) {
        mr = findMRByMrId(mrList, ipd.getMrId());
      }
      if (ipd.getInDate() == null) {
        // 表示此病歷只讀過醫令檔，還未讀過病歷檔，因此需更新
        updateIpd(ipd, values);
      }
      if (mr == null) {
        mr = new MR(ipd);
        mr.setStatus(MR_STATUS.NO_CHANGE.value());
      } else {
        if (mr.getStatus().intValue() != MR_STATUS.NO_CHANGE.value()
            && shouldCompareWarning(mr, cw, ipd.getFuncType())) {
          diffList = new ArrayList<FILE_DIFF>();
          clearFileDiff(mr.getId());
        }
        mr.updateMR(ipd, diffList, cts);
        if (diffList != null && diffList.size() > 0) {
          mr.setChangeOther(1);
        }
      }
      updateMRByExcel(values, mr);
//      if (mr.getName().equals("張金鋼")) {
//        System.out.println("inhid=" + mr.getInhMrId() + ", functype=" + mr.getFuncType() +", ipd.funcType=" + ipd.getFuncType() );
//      }
      mr.setApplYm(result);
      
      if (diffList == null) {
        mr.setIcdcm1(ipd.getIcdCm1());
        MRDetail.updateIcdcmOtherIP(mr, ipd);
        MRDetail.updateIcdpcsIP(mr, ipd);
        //System.out.println(ipd.getRocId() + " icdcm1=" + ipd.getIcdCm1() + "," + mr.getIcdcmOthers() + "," + mr.getIcdpcs());
        MRDetail.updateIcdAllByAlphabet(mr);
        CODE_TABLE ct = cts.getCodeTable("INFECTIOUS", ipd.getIcdCm1());
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
      saveDiffList(diffList, mr);
    }
    return result;
  }
  
  /**
   * 杏翔格式由醫令取得醫療記錄
   * @param opdList
   * @param id
   * @return
   */
  private IP_D findIpdByTheseIpp(List<IP_D> ipdList, IP_P ipp) {
    for (IP_D ipd : ipdList) {
      if (ipd.getRocId().equals(ipp.getRocid()) && ipd.getIcdCm1().equals(ipp.getIcdCm1())) {
        //同一病患因換床，導致床號不同
        //if (ipp.getBedNo().equals(ipd.getBedNo())) {
          if (ipd.getPrsnName() == null) {
            ipd.setPrsnName(ipp.getPrsnName());
          }
          return ipd;
        //}
      }
    }
    return null;
  }
  
  /**
   * 讀取杏翔門診醫令檔
   * @param sheet
   */
  private void readTheseIPP(XSSFSheet sheet) {
    //System.out.println("readTheseIPP");
    // 取得各欄位名稱的位置
    int titleRowIndex = 2;
    HashMap<Integer, String> columnMap = ExcelUtil.readTitleRow(sheet.getRow(titleRowIndex), parameters.getByCat("IP_P"));
    //testColumnMap(columnMap);
    HashMap<String, List<IP_P>> ippMap = aggregateTheseIPP(sheet, titleRowIndex + 1, columnMap);
    List<IP_P> minMax = ippMap.get("MINMAX");
    java.util.Date[] orderDate = new java.util.Date[2];
    orderDate[0] = DateTool.convertChineseToYear(minMax.get(0).getStartTime().substring(0, 7));
    orderDate[1] = DateTool.convertChineseToYear(minMax.get(1).getEndTime().substring(0, 7));
    
    String applYm = minMax.get(1).getEndTime().substring(0, 5);
    IP_T ipt = getIpt(applYm);
    // 避免重複insert
    java.util.Date[] orderDateMore = add1Month(orderDate);
    // 現存的病歷
    List<MR> mrList = mrDao.getByDataFormatAndMrDateBetween(XMLConstant.DATA_FORMAT_IP, orderDateMore[0], orderDateMore[1]);
    // 有異動或新增的病歷
    List<MR> dirtyMR = new ArrayList<MR>();
    // 現存的 IP_D list
    List<IP_D> ipdList = ipdDao.findByIDFromMR(orderDateMore[0],  orderDateMore[1]);
    // 有異動或新增的IP_D list
    List<IP_D> dirtyIPD = new ArrayList<IP_D>(); 
    List<IP_P> ippListDB = ippDao.getByMrIdFromMR(orderDate[0],  orderDate[1]);
    // 要存到 DB 的 batch
    List<IP_P> ippBatch = new ArrayList<IP_P>();
    
    CompareWarning cw = new CompareWarning(parameters.getByCat("COMPARE_WARNING"), cts);
   
    // System.out.println("select from " + orderDateMore[0] + " to " + orderDateMore[1]+", ipdList=" + ipdList.size() + ", mrList=" + mrList.size());
    for (String key : ippMap.keySet()) {
      if ("MINMAX".equals(key)) {
        continue;
      }
      List<IP_P> newIppList = ippMap.get(key);
      MR mr = null;
      IP_D ipd = findIpdByTheseIpp(ipdList, newIppList.get(0));
      if (ipd == null) {
        IP_P orderHasStartEndTime = null;
        long maxEndTime = 0;
        for (int i = 0; i < newIppList.size(); i++) {
          IP_P ipp = newIppList.get(i);
          if (ipp.getStartTime() != null && ipp.getEndTime() != null) {
            long endTime = Long.parseLong(ipp.getEndTime());
            if (endTime > maxEndTime) {
              maxEndTime = endTime;
              orderHasStartEndTime = ipp;
            }
          }
        }
        mr = newMrByIpp(orderHasStartEndTime);
        mr.setIcdcm1(null);
        ipd = newIpdByIpp(orderHasStartEndTime);
        ipd.setIptId(ipt.getId());
        ipd.setMrId(mr.getId());
        ipd = ipdDao.save(ipd);
        mr.setdId(ipd.getId());
        ipdList.add(ipd);
        mrList.add(mr);
        dirtyIPD.add(ipd);
        dirtyMR.add(mr);
      } else {
        mr = findMRByMrId(mrList, ipd.getMrId());
        IP_P ipp = newIppList.get(0);
        if (ipd.getFuncType() == null || !ipd.getFuncType().equals(ipp.getFuncType())) {
          // 用主診斷碼是否為 null 當做mr是否要更新的依據
          ipd.setFuncType(ipp.getFuncType());
          mr.setFuncType(ipp.getFuncType());
          mr.setIcdcm1(null);
        }
      }

      boolean isIpdDirty = false;
      for (int i = 0; i < newIppList.size(); i++) {
        IP_P ipp = newIppList.get(i);
        ipp.setMrId(mr.getId());
        ipp.setIpdId(ipd.getId());

        // 檢查該筆醫令是否已存在DB
        boolean isFound = false;
        for (int j = 0; j < ippListDB.size(); j++) {
          IP_P ippDB = ippListDB.get(j);

          if (ippDB.getMrId().longValue() != mr.getId().longValue()) {
            continue;
          }
          if (ippDB.getInhCode() != null && !ippDB.getInhCode().equals(ipp.getInhCode())) {
            continue;
          }
          isFound = true;
          ipp.setId(ippDB.getId());
          // 整合起始、結束日及數量
          if (ipp.getStartTime() != null && ipp.getEndTime() != null) {
            long start = Long.parseLong(ippDB.getStartTime().substring(0, 7));
            long end = Long.parseLong(ippDB.getEndTime().substring(0, 7));
            long newIppStart = Long.parseLong(ipp.getStartTime().substring(0, 7));
            long newIppEnd = Long.parseLong(ipp.getEndTime().substring(0, 7));

            if (newIppStart < start || newIppEnd > end) {
              ipp.setTotalQ(ippDB.getTotalQ().doubleValue() + ipp.getTotalQ().doubleValue());
              ipp.setTotalDot((int) Math.round(ipp.getTotalQ() * ipp.getUnitP()));
              if (Long.parseLong(ipd.getApplStartDate()) > newIppStart) {
                ipd.setApplStartDate(ipp.getStartTime().substring(0, 7));
                mr.setMrDate(DateTool.convertChineseToYear(ipp.getStartTime().substring(0, 7)));
                isIpdDirty = true;
              }
              if (Long.parseLong(ipd.getApplEndDate()) < newIppEnd) {
                ipd.setApplEndDate(ipp.getEndTime().substring(0, 7));
                mr.setMrEndDate(DateTool.convertChineseToYear(ipp.getEndTime().substring(0, 7)));
                isIpdDirty = true;
              }
            }
            break;
          }
        }
        if (!isFound) {
          ippListDB.add(ipp);
          if (ipp.getOrderCode() != null) {
            if (mr.getCodeAll() == null) {
              mr.setCodeAll("," + ipp.getOrderCode() + ",");
              isIpdDirty = true;
            } else if (mr.getCodeAll().indexOf("," + ipp.getOrderCode() + ",") < 0){
              mr.setCodeAll(mr.getCodeAll() + ipp.getOrderCode() + ",");
              isIpdDirty = true;
            }
          }
          if (ipp.getInhCode() != null && ipp.getInhCode().length() > 0) {
            if (mr.getInhCode() == null) {
              mr.setInhCode("," + ipp.getInhCode() + ",");
              isIpdDirty = true;
            } else if (mr.getInhCode().indexOf("," + ipp.getInhCode() + ",") < 0){
              mr.setInhCode(mr.getInhCode() + ipp.getInhCode() + ",");
              isIpdDirty = true;
            }
          }
        }

        ippBatch.add(ipp);
        if (ippBatch.size() > XMLConstant.BATCH) {
          ippDao.saveAll(ippBatch);
          ippBatch.clear();
        }
      }
      if (isIpdDirty) {
        mr.setIcdcm1(null);
        dirtyIPD.add(ipd);
        dirtyMR.add(mr);
      }
    }
    
    if (ippBatch.size() > 0) {
      ippDao.saveAll(ippBatch);
      ippBatch.clear();
    }
    saveMrAndIpd(dirtyMR, dirtyIPD, ippListDB);    
  }
  
  /**
   * 將杏翔住院醫令清單中同一病患同科別同醫令的放在一起，如 一般病床住院診察費(天) 有6筆， 放成一筆，寫上START_TIME, END_TIME
   * @param sheet
   * @param startRow
   * @param columnMap
   * @return
   */
  private HashMap<String, List<IP_P>> aggregateTheseIPP(XSSFSheet sheet, int startRow, HashMap<Integer, String> columnMap){
    HashMap<String, List<IP_P>> result = new HashMap<String, List<IP_P>>();
    HashMap<String, String> payCodeTypes = getPayCodeType();
    long min = Long.MAX_VALUE;
    long max = Long.MIN_VALUE;
    IP_P ippMax = null;
    IP_P ippMin = null;
    for (int i = startRow; i < sheet.getPhysicalNumberOfRows(); i++) {
      XSSFRow row = sheet.getRow(i);
      if (row == null || row.getCell(0) == null) {
        continue;
      }
      HashMap<String, String> values = formatOPDValues(ExcelUtil.readCellValue(columnMap, row));
      if (values.get("ROC_ID") == null && values.get("ICD_CM_1") == null) {
        continue;
      }
      if (values.get("FUNC_DATE") != null) {
        values.put("FUNC_DATE", values.get("FUNC_DATE") + "0000"); 
      }
      // 因病患可能換床，所以不抓床號
      String key = values.get("ROC_ID") + values.get("ICD_CM_1"); //  + values.get("BED_NO");
      List<IP_P> ippList = result.get(key);
      if (ippList == null) {
        ippList = new ArrayList<IP_P>();
        result.put(key, ippList);
      }
      boolean isFound = false;
      for (IP_P ipp : ippList) {
        if (ipp.getInhCode().equals(values.get("INH_CODE"))) {
          // 院內碼
          isFound = true;
          long start = Long.parseLong(ipp.getStartTime());
          long end =  Long.parseLong(ipp.getEndTime());
          long funcDate =  Long.parseLong(values.get("FUNC_DATE"));
          
          if (ipp.getApplStatus().intValue() == APPL_STATUS_OE) {
            ipp.setTotalQ(ipp.getTotalQ().doubleValue() + Double.parseDouble(values.get("APPL_STATUS")));  
          } else {
            ipp.setTotalQ(ipp.getTotalQ().doubleValue() + Double.parseDouble(values.get("TOTAL_Q")));  
          }
          ipp.setTotalDot((int) Math.round(ipp.getTotalQ() * ipp.getUnitP()));
          
          if (funcDate < start) {
            ipp.setStartTime(values.get("FUNC_DATE"));
            if (funcDate < min) {
              min = funcDate;
              ippMin = ipp;
            }
          } else if (funcDate > end) {
            ipp.setEndTime(values.get("FUNC_DATE"));
            if (funcDate > max) {
              max = funcDate;
              ippMax = ipp;
            }
          }
          break;
        }
      }
      if (!isFound) {
        IP_P ipp = getIpp(values, "THESE");
        ipp.setStartTime(values.get("FUNC_DATE"));
        ipp.setEndTime(values.get("FUNC_DATE"));
        long dateInt = Long.parseLong(values.get("FUNC_DATE"));
        if (dateInt < min) {
          min = dateInt;
          ippMin = ipp;
        }
        if (dateInt > max) {
          max = dateInt;
          ippMax = ipp;
        }
        String payCodeType = payCodeTypes.get(ipp.getOrderCode());
        if ("20".equals(payCodeType) || payCodeType == null) {
          if (ipp.getOrderCode() != null) {
            if (ipp.getOrderCode().length() == 10) {
              // 西藥藥品
              payCodeType = "22";
            } else if (ipp.getOrderCode().length() == 10) {
              // 衛材品項費
              payCodeType = "23";
            }
          }
        }
        ipp.setPayCodeType(payCodeType);
        ipp.setUpdateAt(new java.util.Date());
        ippList.add(ipp);
      }
    }
    ArrayList<IP_P> minMax = new ArrayList<IP_P>();
    minMax.add(ippMin);
    minMax.add(ippMax);
    result.put("MINMAX", minMax);
    return result;
  }
  
  /**
   * 將杏翔門診醫令清單中同一病患同科別同醫令的放在一起，數量相加
   * @param sheet
   * @param startRow
   * @param columnMap
   * @return
   */
  private HashMap<String, List<OP_P>> aggregateTheseOPP(XSSFSheet sheet, int startRow, HashMap<Integer, String> columnMap){
    HashMap<String, List<OP_P>> result = new HashMap<String, List<OP_P>>();
    HashMap<String, String> payCodeTypes = getPayCodeType();
    long min = Long.MAX_VALUE;
    long max = Long.MIN_VALUE;
    OP_P oppMax = null;
    OP_P oppMin = null;
    for (int i = startRow; i < sheet.getPhysicalNumberOfRows(); i++) {
      XSSFRow row = sheet.getRow(i);
      if (row == null || row.getCell(0) == null) {
        continue;
      }
      HashMap<String, String> values = formatOPDValues(ExcelUtil.readCellValue(columnMap, row));
      String key = values.get("ROC_ID") + values.get("CARD_NO");
      List<OP_P> oppList = result.get(key);
      if (oppList == null) {
        oppList = new ArrayList<OP_P>();
        result.put(key, oppList);
      }
      boolean isFound = false;
      for (OP_P opp : oppList) {
        if (opp.getInhCode().equals(values.get("INH_CODE"))) {
          // 院內碼
          isFound = true;
          if (opp.getApplStatus().intValue() == APPL_STATUS_OE) {
            opp.setTotalQ(
                opp.getTotalQ().doubleValue() + Double.parseDouble(values.get("APPL_STATUS")));
          } else {
            opp.setTotalQ(
                opp.getTotalQ().doubleValue() + Double.parseDouble(values.get("TOTAL_Q")));
          }
          opp.setTotalDot((int) Math.round(opp.getTotalQ() * opp.getUnitP()));
          if (values.get("FUNC_DATE") != null) {
            long startTime = Long.valueOf(opp.getStartTime());
            long endTime = Long.valueOf(opp.getEndTime());
            long newTime = Long.valueOf(values.get("FUNC_DATE") + "0000");
            if (newTime < startTime) {
              opp.setStartTime(String.valueOf(newTime));
              if (newTime < min) {
                min = newTime;
                oppMin = opp;
              }
            } else if (newTime > endTime) {
              opp.setEndTime(String.valueOf(newTime));
              if (newTime > max) {
                max = newTime;
                oppMax = opp;
              }
            }
          }
          break;
        }
      }
      if (!isFound) {
        OP_P opp = getOpp(values, "THESE");
        long newTime = Long.parseLong(opp.getStartTime());
        if (newTime < min) {
          min = newTime;
          oppMin = opp;
        }
        newTime = Long.parseLong(opp.getEndTime());
        if (newTime > max) {
          max = newTime;
          oppMax = opp;
        }
        if (opp.getDrugNo() != null) {
          if (opp.getDrugNo().length() == 10) {
            // 藥費
            opp.setPayCodeType("22");
            // 醫令類別：用藥明細
            opp.setOrderType("1");
          } else if (opp.getDrugNo().length() == 12) {
            // 衛材品項費
            opp.setPayCodeType("23");
            // 醫令類別：特殊材料
            opp.setOrderType("3");
          } else {
            opp.setPayCodeType(payCodeTypes.get(opp.getDrugNo()));
            if ("6".equals(opp.getPayCodeType())) {
              // 6:調劑費 <-> order type 9:藥事服務費
              opp.setOrderType("9");
            }
          }
        }
        opp.setUpdateAt(new java.util.Date());
        oppList.add(opp);
      }
    }
    ArrayList<OP_P> minMax = new ArrayList<OP_P>();
    minMax.add(oppMin);
    minMax.add(oppMax);
    result.put("MINMAX", minMax);
    return result;
  }
  
  /**
   * 將杏翔住院醫令清單中同一病患同科別同醫令的放在一起，如 一般病床住院診察費(天) 有6筆， 放成一筆，寫上START_TIME, END_TIME
   * @param sheet
   * @param startRow
   * @param columnMap
   * @return
   */
  private HashMap<String, List<IP_P>> aggregateLeadtekIPP(HSSFSheet sheet, int startRow, HashMap<Integer, String> columnMap){
    HashMap<String, List<IP_P>> result = new HashMap<String, List<IP_P>>();
    HashMap<String, String> payCodeTypes = getPayCodeType();
    long min = Long.MAX_VALUE;
    long max = Long.MIN_VALUE;
    IP_P ippMax = null;
    IP_P ippMin = null;
    for (int i = startRow; i < sheet.getPhysicalNumberOfRows(); i++) {
      HSSFRow row = sheet.getRow(i);
      if (row == null || row.getCell(0) == null) {
        continue;
      }
      HashMap<String, String> values = formatOPDValues(ExcelUtil.readCellValue(columnMap, row));
      if (values.get("INH_NO") == null) {
        continue;
      }
      List<IP_P> ippList = result.get(values.get("INH_NO"));
      if (ippList == null) {
        ippList = new ArrayList<IP_P>();
        result.put(values.get("INH_NO"), ippList);
      }
      boolean isFound = false;
      for (IP_P ipp : ippList) {
        if (ipp.getInhCode() == null) {
          continue;
        }
        if (ipp.getInhCode().equals(values.get("INH_CODE"))) {
          // 院內碼
          isFound = true;
          long start = Long.parseLong(ipp.getStartTime());
          long end =  Long.parseLong(ipp.getEndTime());
          long funcDate =  Long.parseLong(values.get("FUNC_DATE"));
          ipp.setTotalQ(ipp.getTotalQ().doubleValue() + Double.parseDouble(values.get("TOTAL_Q")));  
          ipp.setTotalDot(ipp.getTotalDot().intValue() + (int) Double.parseDouble(values.get("TOTAL_DOT")));

          if (ipp.getStartTime() != null && ipp.getStartTime().length() == 11) {
            if (funcDate < start) {
              ipp.setStartTime(values.get("FUNC_DATE"));
              if (funcDate < min) {
                min = funcDate;
                ippMin = ipp;
              }
            } else if (funcDate > end) {
              ipp.setEndTime(values.get("FUNC_DATE"));
              if (funcDate > max) {
                max = funcDate;
                ippMax = ipp;
              }
            }
          }
          break;
        }
      }
      if (!isFound) {
        IP_P ipp = getIpp(values, "LEADTEK");
        String payCodeType = payCodeTypes.get(ipp.getOrderCode());
        if ("20".equals(payCodeType) || payCodeType == null) {
          if (ipp.getOrderCode() != null) {
            if (ipp.getOrderCode().length() == 10) {
              // 西藥藥品
              payCodeType = "22";
            } else if (ipp.getOrderCode().length() == 10) {
              // 衛材品項費
              payCodeType = "23";
            }
          }
        }
        ipp.setPayCodeType(payCodeType);
        ipp.setUpdateAt(new java.util.Date());
        ippList.add(ipp);
      }
    }
    return result;
  }
  
  private IP_D newIpdByIpp(IP_P ipp) {
    IP_D result = new IP_D();
    result.setFuncType(ipp.getFuncType());
    result.setRocId(ipp.getRocid());
    result.setName(ipp.getName());
    result.setPrsnName(ipp.getPrsnName());
    result.setIcdCm1(ipp.getIcdCm1());
    if (ipp.getStartTime() != null) {
      result.setApplStartDate(ipp.getStartTime().substring(0, 7));
    }
    if (ipp.getEndTime() != null) {
      result.setApplEndDate(ipp.getEndTime().substring(0, 7));
    }
    result.setUpdateAt(new java.util.Date());
    result.setBedNo(ipp.getBedNo());
    result.setIdBirthYmd(ipp.getBirthday());
    result.setAneDot(0);
    result.setDsvcDot(0);
    result.setRoomDot(0);
    result.setInjtDot(0);
    result.setDrugDot(0);
    result.setDiagDot(0);
    result.setThrpDot(0);
    result.setAminDot(0);
    result.setBabyDot(0);
    result.setBlodDot(0);
    result.setHdDot(0);
    result.setMealDot(0);
    result.setMetrDot(0);
    maskIPD(result);
    return result;
  }
  
  private MR newMrByIpp(IP_P ipp) {
    MR result = new MR();
    result.setStatus(MR_STATUS.NO_CHANGE.value());
    result.setDataFormat(XMLConstant.DATA_FORMAT_IP);
    result.setFuncType(ipp.getFuncType());
    result.setRocId(ipp.getRocid());
    result.setName(ipp.getName());
    result.setPrsnName(ipp.getPrsnName());
    result.setIcdcm1(ipp.getIcdCm1());
    result.setMrDate(DateTool.convertChineseToYear(ipp.getStartTime().substring(0, 7)));
    result.setMrEndDate(DateTool.convertChineseToYear(ipp.getEndTime().substring(0, 7)));
    result.setApplYm(ipp.getEndTime().substring(0, 5));
    result.setInhMrId(ipp.getInhMr());
    result.setTotalDot(0);
    result.setOwnExpense(0);
    result.setReportDot(0L);
    result.setApplDot(0);
    result.setDeductedDot(0);
    result.setUpdateAt(new java.util.Date());
    if (ISMASK) {
      result.setRocId(StringUtility.maskString(result.getRocId(), StringUtility.MASK_MOBILE));
      result.setName(StringUtility.maskString(result.getName(), StringUtility.MASK_NAME));
    }
    return mrDao.save(result);
  }
    
  /**
   * 更新 opp
   * 
   * @param opp
   * @param mo
   * @return true:資料有異動，false:資料無異動
   */
  private boolean updateOpp(OP_P opp, OP_P newOPP) {
    boolean result = false;
    if (opp.getApplStatus().intValue() != newOPP.getApplStatus().intValue()) {
      result = true;
      opp.setApplStatus(newOPP.getApplStatus());
      opp.setPayBy(newOPP.getPayBy());
    }
    
    if (opp.getDrugUse() != null && newOPP.getDrugUse() != null && opp.getDrugUse().doubleValue() != newOPP.getDrugUse().doubleValue()) {
      result = true;
      opp.setDrugUse(newOPP.getDrugUse());
    }
    if (opp.getDrugFre() != null) {
      if (!opp.getDrugFre().equals(newOPP.getDrugFre())) {
        result = true;
        opp.setDrugFre(newOPP.getDrugFre());
      }
    } else if (newOPP.getDrugFre() != null) {
      result = true;
      opp.setDrugFre(newOPP.getDrugFre());
    }
    if (opp.getTotalQ().doubleValue() != newOPP.getTotalQ().doubleValue()) {
      result = true;
      opp.setTotalQ(newOPP.getTotalQ());
    }
    return result;
  }
  
  private OP_D newOpdByOpp(HashMap<String, String> values) {
    OP_D result = new OP_D();
    result.setFuncType(values.get("FUNC_TYPE"));
    result.setName(values.get("NAME"));
    result.setRocId(values.get("ROC_ID"));
    result.setIdBirthYmd(values.get("ID_BIRTH_YMD"));
    result.setPrsnName(values.get("PRSN_NAME"));
    result.setFuncEndDate(values.get("FUNC_DATE"));
    result.setFuncDate(values.get("FUNC_DATE"));
    result.setIcdCm1(addICDCMDot(values.get("ICD_CM_1")));
    result.setReceiveNo(values.get("RECEIVE_NO"));
    if (values.get("CARD_NO") != null && values.get("CARD_NO").length() > 0) {
      result.setCardNo(values.get("CARD_NO"));
    }
    maskOPD(result);
    result.setUpdateAt(new java.util.Date());
    return result;
  }
  
  private HashMap<String, Integer> rocIdDateCount(List<OP_D> opdList) {
    HashMap<String, Integer> result = new HashMap<String, Integer>();
    for (OP_D op_D : opdList) {
      Integer count = result.get(op_D.getRocId() + op_D.getFuncEndDate());
      if (count == null) {
        result.put(op_D.getRocId() + op_D.getFuncEndDate(), Integer.valueOf(1));
      } else {
        result.put(op_D.getRocId() + op_D.getFuncEndDate(), Integer.valueOf(count.intValue() + 1));
      }
    }
    
    return result;
  }
  
  public void testColumnMap(HashMap<Integer, String> columnMap) {
    for (Integer integer : columnMap.keySet()) {
      System.out.println("columnMap " + integer + ":" + columnMap.get(integer));
    }
  }
  
  /**
   * 自費醫令，設定申報狀態及費用狀態
   * @param opp
   * @param applStatus
   * @param cardNo
   */
  private void updateOppOwnExpense(OP_P opp, String applStatus, String cardNo) {
    opp.setApplStatus(APPL_STATUS_OE);
    if (cardNo == null || cardNo.length() == 0) {
   // 自費病人自費,健保病人不申報不計價
      opp.setPayBy("Z");
    } else if (isH(opp.getInhCode())) {
      opp.setPayBy("H");
    } else {
      opp.setPayBy("Y");
    }
    opp.setTotalQ(Double.parseDouble(applStatus));
  }
  
  /**
   * 是否為自費但列為部分負擔費用
   * @param orderCode
   * @return
   */
  public boolean isH(String orderCode) {
    for (String string : OWN_EXPENSE_TO_PART) {
      if(orderCode.equals(string)) {
        return true;
      }
    }
    return false;
  }
  
  private HashMap<String, String> formatOPDValues(HashMap<String, String> values) {
    HashMap<String, String> result = values;
    result.put("FUNC_DATE", removeSlash(values.get("FUNC_DATE")));
    result.put("ICD_CM_1", addICDCMDot(values.get("ICD_CM_1")));
    if (values.get("FUNC_END_DATE") != null && values.get("FUNC_END_DATE").length() > 0) {
      result.put("FUNC_END_DATE", DateTool.removeSlashForChineseYear(values.get("FUNC_END_DATE")));
    }
    if (values.get("CARD_NO") != null && values.get("CARD_NO").length() > 0) {
      String cardNo = values.get("CARD_NO");
      int index = cardNo.indexOf('-');
      if (index > -1) {
//        if (index < 3) {
//          values.put("CARD_NO", cardNo.substring(index + 1));
//        } else if (index > 3) {
//          values.put("CARD_NO", cardNo.substring(0, index));
//        }
        result.put("CARD_NO", cardNo.substring(0, index));
      }
    }
    if (ISMASK) {
      result.put("ROC_ID", StringUtility.maskString(values.get("ROC_ID"), StringUtility.MASK_MOBILE));
      result.put("NAME", StringUtility.maskString(values.get("NAME"), StringUtility.MASK_NAME));
      result.put("PRSN_NAME", StringUtility.maskString(values.get("PRSN_NAME"), StringUtility.MASK_NAME));
      result.put("PHAR_ID", StringUtility.maskString(values.get("PHAR_ID"), StringUtility.MASK_MOBILE));
      result.put("PRSN_ID", StringUtility.maskString(values.get("PRSN_ID"), StringUtility.MASK_MOBILE));
    }
    return result;
  }
  
  private OP_P findOppByOppValues(HashMap<String, List<OP_P>> map, HashMap<String, String> values) {
    for (String key : map.keySet()) {
      String keyNew = values.get("ROC_ID") + values.get("CARD_NO");
      if (!key.equals(keyNew)) {
        continue;
      }
      List<OP_P> oppList = map.get(key);
      if (oppList == null || oppList.size() == 0) {
        return null;
      }
      int index = -1;
      for (int i=0; i<oppList.size(); i++) {
        OP_P opp = oppList.get(i);
        if (opp.getInhCode().equals(values.get("INH_CODE"))) {
          index = i;
          break;
        }
      }
      if (index > -1) {
        return oppList.remove(index);
      }
    }
    return null;
  }
  
  /**
   * 住院病歷多抓前、後各1個月，避免抓不到跨月病歷
   * @param old
   * @return
   */
  private java.util.Date[] add1Month(java.util.Date[] old) {
    java.util.Date[] result = new java.util.Date[2];
    Calendar calStart = Calendar.getInstance();
    calStart.setTime(old[0]);
    Calendar calEnd = Calendar.getInstance();
    calEnd.setTime(old[1]);
    
    calStart.add(Calendar.MONTH, -1);
    calEnd.add(Calendar.MONTH, 1);
    result[0] = calStart.getTime();
    result[1] = calEnd.getTime();

    return result;
  }

  /// 匯入核刪excel檔案
  public List<DEDUCTED_NOTE> readDeductedNoteHSSFSheet(Sheet sheet, String mrid, String editorName)
      throws ParseException {

    // 取得最後一筆row位置作為資料size()
    int lastRowNum = sheet.getLastRowNum();
    // 由標題列取得各欄位名稱的位置
    HashMap<Integer, String> columnMap = new HashMap<Integer, String>();
    HashMap<String, String> values = new HashMap<String, String>();
    List<HashMap<String, String>> valueList = new ArrayList<HashMap<String, String>>();
    DEDUCTED_NOTE note = new DEDUCTED_NOTE();
    List<DEDUCTED_NOTE> listNote = new ArrayList<DEDUCTED_NOTE>();
    /// 先取欄位名稱
    for (int v = 0; v < lastRowNum + 1; v++) {
      try {
        columnMap = ExcelUtil.readTitleRow(sheet.getRow(v));
        if (columnMap.get(16).equals("備註"))
          columnMap.replace(16, "核刪備註");
        if (columnMap.get(26).equals("備註"))
          columnMap.replace(26, "申復備註");
        if (columnMap.get(34).equals("備註"))
          columnMap.replace(34, "爭議備註");
        System.out.println("columnMap -> " + columnMap.toString());
        break;
      } catch (Exception e) {
        continue;
      }

    }
    /// 再取值
    for (int v = 0; v < lastRowNum + 1; v++) {
      try {
        values = ExcelUtil.readCellValue(columnMap, sheet.getRow(v));
        System.out.println("values -> " + values.toString());
        System.out.println("lastRowNum -> " + lastRowNum);
        /// 如果是表頭就略過
        if (values.get("病歷號").equals("病歷號")) {
          values = new HashMap<String, String>();
          continue;
        }
        valueList.add(values);
        values = new HashMap<String, String>();
      } catch (Exception e) {
        continue;
      }

    }
    System.out.println("valueList -> " + valueList.toString());

    if (valueList.size() > 0) {
      for (HashMap<String, String> map : valueList) {
        note.setMrId(Long.valueOf(map.get("病歷號")));
        note.setActionType(1);
        note.setItem(map.get("項目"));
        note.setCat(map.get("類別"));
        note.setL1(map.get("大分類"));
        note.setL2(map.get("中分類"));
        note.setL3(map.get("小分類"));
        note.setSubCat(map.get("次分類"));
        note.setCode(map.get("核刪代碼"));
        note.setDeductedOrder(map.get("核刪醫令"));
        note.setDeductedQuantity(map.get("核刪數量") == null ? null : Integer.valueOf(map.get("核刪數量")));
        note.setDeductedAmount(map.get("核刪總點數") == null ? null : Integer.valueOf(map.get("核刪總點數")));
        note.setReason(map.get("核減理由"));
        note.setNote(map.get("核刪備註"));
        note.setRollbackM(
            map.get("放大回推金額(月)") == null ? null : Integer.valueOf(map.get("放大回推金額(月)")));
        note.setRollbackQ(
            map.get("放大回推金額(季)") == null ? null : Integer.valueOf(map.get("放大回推金額(季)")));
        note.setAfrQuantity(map.get("申復數量") == null ? null : Integer.valueOf(map.get("申復數量")));
        note.setAfrAmount(map.get("申復金額") == null ? null : Integer.valueOf(map.get("申復金額")));
        note.setAfrPayQuantity(
            map.get("申復補付數量") == null ? null : Integer.valueOf(map.get("申復補付數量")));
        note.setAfrAmount(map.get("申復補付金額") == null ? null : Integer.valueOf(map.get("申復補付金額")));
        note.setAfrNoPayCode(map.get("申復不補付理由代碼"));
        note.setAfrNoPayDesc(map.get("申復不補付理由說明"));
        note.setAfrNote(map.get("申復備註"));
        note.setDisputeQuantity(
            map.get("核刪醫令爭議數量") == null ? null : Integer.valueOf(map.get("核刪醫令爭議數量")));
        note.setDisputeAmount(map.get("爭議金額") == null ? null : Integer.valueOf(map.get("爭議金額")));
        note.setDisputePayQuantity(
            map.get("爭議補付數量") == null ? null : Integer.valueOf(map.get("爭議補付數量")));
        note.setDisputePayAmount(
            map.get("爭議補付金額") == null ? null : Integer.valueOf(map.get("爭議補付金額")));
        note.setDisputeNoPayCode(map.get("爭議不補付理由代碼"));
        note.setDisputeNoPayDesc(map.get("爭議不補付理由說明"));
        note.setDisputeNote(map.get("爭議備註"));
        note.setEditor(editorName);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        if (map.get("核刪日期") != null && !map.get("核刪日期").isEmpty()) {

          java.util.Date d = DateUtil.getJavaDate(Double.valueOf(map.get("核刪日期")));
          String outputDate = sdf.format(d);
          note.setDeductedDate(sdf.parse(outputDate));

        }
        if (map.get("放大回推日期") != null && !map.get("放大回推日期").isEmpty()) {
          java.util.Date d = DateUtil.getJavaDate(Double.valueOf(map.get("放大回推日期")));
          String outputDate = sdf.format(d);
          note.setRollbackDate(sdf.parse(outputDate));
        }
        if (map.get("爭議日期") != null && !map.get("爭議日期").isEmpty()) {
          java.util.Date d = DateUtil.getJavaDate(Double.valueOf(map.get("爭議日期")));
          String outputDate = sdf.format(d);
          note.setDisputeDate(sdf.parse(outputDate));
        }

        if (mrid.equals(map.get("病歷號"))) {
          listNote.add(note);
        }
        /// 寫入資料庫
        newDeductedNote(map.get("病歷號"), note);
        note = new DEDUCTED_NOTE();
      }
      return listNote;
    } else {
      return listNote;
    }
  }

  /**
   * 將Leadtek門診醫令清單中同一INH_NO的醫令放在一起
   * @param sheet
   * @param startRow
   * @param columnMap
   * @return
   */
  private HashMap<String, List<OP_P>> aggregateLeadtekOPP(HSSFSheet sheet, int startRow,
      HashMap<Integer, String> columnMap) {
    HashMap<String, List<OP_P>> result = new HashMap<String, List<OP_P>>();
    HashMap<String, String> payCodeTypes = getPayCodeType();
    for (int i = startRow; i < sheet.getPhysicalNumberOfRows(); i++) {
      HSSFRow row = sheet.getRow(i);
      if (row == null || row.getCell(0) == null) {
        continue;
      }
      HashMap<String, String> values = formatOPDValues(ExcelUtil.readCellValue(columnMap, row));
      OP_P opp = getOpp(values, "LEADTEK");
      List<OP_P> oppList = result.get(values.get("INH_NO"));
      if (oppList == null) {
        oppList = new ArrayList<OP_P>();
        result.put(values.get("INH_NO"), oppList);
      }

      if (opp.getDrugNo() != null) {
        if (opp.getDrugNo().length() == 10) {
          // 藥費
          opp.setPayCodeType("22");
          // 醫令類別：用藥明細
          opp.setOrderType("1");
        } else if (opp.getDrugNo().length() == 12) {
          // 衛材品項費
          opp.setPayCodeType("23");
          // 醫令類別：特殊材料
          opp.setOrderType("3");
        } else {
          opp.setPayCodeType(payCodeTypes.get(opp.getDrugNo()));
          if ("6".equals(opp.getPayCodeType())) {
            // 6:調劑費 <-> order type 9:藥事服務費
            opp.setOrderType("9");
          }
        }
      }
      opp.setUpdateAt(new java.util.Date());
      oppList.add(opp);
    }
    return result;
  }
  
  public void exportCSV(String exportType,String dataFormat,String dateType,String year,String month,String fnSdate,String fnEdate, String outSdate,String outEdate,String inhCode,HttpServletResponse response) throws IOException {
	  ///申報日
	  String applYM = "";
	  ///治療結束日起
	  String fsdate = "";
	  ///治療結束日迄
	  String fedate = "";
	  ///出院日起
	  String osdate = "";
	  ///出院日迄
	  String oedate = "";
	  ///就醫類型
	  String[] dateFormats = {};
	  ///就醫紀錄編號
	  String[] inhCodes = {};
	  if(exportType.equals("fileExportTypeOne")) {
		  dateFormats = StringUtility.splitBySpace(dataFormat);
		  switch(dateType) {
			  case "applyDate":
				  if(year != null && !year.isEmpty()) {
					  int yy = Integer.parseInt(year);
					  int mm = Integer.parseInt(month);
					  int ym = yy * 100 + mm;
					  String d = DateTool.convertToChineseYear(String.valueOf(ym*100));
					  applYM = d.substring(0,d.length()-2);
				   }
			  break;
			  case "cureFinishRange":
				  if(fnSdate != null && !fnSdate.isEmpty()) {
					  String rs = fnSdate.replaceAll("-", "");
					  String re = fnEdate.replaceAll("-", "");
					  fsdate = DateTool.convertToChineseYear(rs);
					  fedate = DateTool.convertToChineseYear(re);
				  }
			  break;
			  case "lpRange":
				  if(outSdate != null && !outSdate.isEmpty()) {
					  String rs = outSdate.replaceAll("-", "");
					  String re = outEdate.replaceAll("-", "");
					  osdate = DateTool.convertToChineseYear(rs);
					  oedate = DateTool.convertToChineseYear(re);
				  }
			  break;
		  }
	  }
	  else {
		  inhCodes = StringUtility.splitBySpace(inhCode);
	  }
	  
	  
	  List<LinkedHashMap<String, Object>> ipdList = new ArrayList<LinkedHashMap<String,Object>>();
	  List<LinkedHashMap<String, Object>> ippList = new ArrayList<LinkedHashMap<String,Object>>();
	  List<LinkedHashMap<String, Object>> ipsoList = new ArrayList<LinkedHashMap<String,Object>>();
	  List<LinkedHashMap<String, Object>> opdList = new ArrayList<LinkedHashMap<String,Object>>();
	  List<LinkedHashMap<String, Object>> oppList = new ArrayList<LinkedHashMap<String,Object>>();
	  List<LinkedHashMap<String, Object>> opsoList = new ArrayList<LinkedHashMap<String,Object>>();
	  List<LinkedHashMap<String, Object>> deductedNoteList = new ArrayList<LinkedHashMap<String,Object>>();
	  int ipdCount = 0;
	  int ippCount = 0;
	  int ipsoCount = 0;
	  int opdCount = 0;
	  int oppCount = 0;
	  int opsoCount = 0;
	  int deductedCount = 0;
	  
	  if(exportType.equals("fileExportTypeOne")) {
		  if(dateFormats.length > 1) {
			  
			  ipdCount = exportCSVDao.ipdCount(applYM, fsdate, fedate, osdate, oedate, inhCodes);
			  ippCount = exportCSVDao.ippCount(applYM, fsdate, fedate, osdate, oedate, inhCodes);
			  ipsoCount = exportCSVDao.ipsoCount(applYM, fsdate, fedate, osdate, oedate, inhCodes);
			  opdCount = exportCSVDao.opdCount(applYM, fsdate, fedate, inhCodes);
			  oppCount = exportCSVDao.oppCount(applYM, fsdate, fedate, inhCodes);
			  opsoCount = exportCSVDao.opsoCount(applYM, fsdate, fedate, inhCodes);
			  deductedCount = exportCSVDao.deductedNoteCount(applYM, fsdate, fedate, osdate, oedate, dateFormats, inhCodes);
			  
			  if(ipdCount > 2000) { 
				  List<LinkedHashMap<String, Object>> dataList = new ArrayList<LinkedHashMap<String,Object>>();
				  double d = ipdCount;
				  Long l =  Math.round(d / 1000.0);
				  int c = 1000;
				  for(int i=0; i<l; i++) {
					  dataList.addAll(exportCSVDao.ipdData(applYM, fsdate, fedate, osdate, oedate, inhCodes, "1000", String.valueOf(c)));
					  c += 1000;
				  }
				  ipdList = dataList;
			  }
			  else {
				  ipdList = exportCSVDao.ipdData(applYM, fsdate, fedate, osdate, oedate, inhCodes, null, null);
			  }
			  if(ippCount > 2000) {
				  List<LinkedHashMap<String, Object>> dataList = new ArrayList<LinkedHashMap<String,Object>>();
				  double d = ippCount;
				  Long l =  Math.round(d / 1000.0);
				  int c = 1000;
				  for(int i=0; i<l; i++) {
					  dataList.addAll(exportCSVDao.ippData(applYM, fsdate, fedate, osdate, oedate, inhCodes, "1000", String.valueOf(c)));
					  c += 1000;
				  }
				  ippList = dataList;
			  }
			  else {
				  ippList = exportCSVDao.ippData(applYM, fsdate, fedate, osdate, oedate, inhCodes,null,null);
			  }
			  if(ipsoCount > 2000) {
				  List<LinkedHashMap<String, Object>> dataList = new ArrayList<LinkedHashMap<String,Object>>();
				  double d = ipsoCount;
				  Long l =  Math.round(d / 1000.0);
				  int c = 1000;
				  for(int i=0; i<l; i++) {
					  dataList.addAll(exportCSVDao.ipsoData(applYM, fsdate, fedate, osdate, oedate, inhCodes, "1000", String.valueOf(c)));
					  c += 1000;
				  }
				  ipsoList = dataList;
			  }
			  else {
				  ipsoList = exportCSVDao.ipsoData(applYM, fsdate, fedate, osdate, oedate, inhCodes,null,null);
			  }
			  if(opdCount > 2000) {
				  List<LinkedHashMap<String, Object>> dataList = new ArrayList<LinkedHashMap<String,Object>>();
				  double d = opdCount;
				  Long l =  Math.round(d / 1000.0);
				  int c = 1000;
				  for(int i=0; i<l; i++) {
					  dataList.addAll(exportCSVDao.opdData(applYM, fsdate, fedate, inhCodes, "1000", String.valueOf(c)));
					  c += 1000;
				  }
				  opdList = dataList;
			  }
			  else {
				  opdList = exportCSVDao.opdData(applYM, fsdate, fedate, inhCodes,null,null);
			  }
			  if(oppCount > 2000) {
				  List<LinkedHashMap<String, Object>> dataList = new ArrayList<LinkedHashMap<String,Object>>();
				  double d = oppCount;
				  Long l =  Math.round(d / 1000.0);
				  int c = 1000;
				  for(int i=0; i<l; i++) {
					  dataList.addAll(exportCSVDao.oppData(applYM, fsdate, fedate, inhCodes, "1000", String.valueOf(c)));
					  c += 1000;
				  }
				  oppList = dataList;
			  }
			  else {
				  oppList = exportCSVDao.oppData(applYM, fsdate, fedate, inhCodes,null,null);
			  }
			  if(opsoCount > 2000) {
				  List<LinkedHashMap<String, Object>> dataList = new ArrayList<LinkedHashMap<String,Object>>();
				  double d = opsoCount;
				  Long l =  Math.round(d / 1000.0);
				  int c = 1000;
				  for(int i=0; i<l; i++) {
					  dataList.addAll(exportCSVDao.opsoData(applYM, fsdate, fedate, inhCodes, "1000", String.valueOf(c)));
					  c += 1000;
				  }
				  opsoList = dataList;
			  }
			  else {
				  opsoList = exportCSVDao.opsoData(applYM, fsdate, fedate, inhCodes,null,null);
			  }
			  if(deductedCount > 2000) {
				  List<LinkedHashMap<String, Object>> dataList = new ArrayList<LinkedHashMap<String,Object>>();
				  double d = deductedCount;
				  Long l =  Math.round(d / 1000.0);
				  int c = 1000;
				  for(int i=0; i<l; i++) {
					  dataList.addAll(exportCSVDao.deductedNoteData(applYM, fsdate, fedate, osdate, oedate, dateFormats, inhCodes, "1000", String.valueOf(c)));
					  c += 1000;
				  }
				  deductedNoteList = dataList;
			  }
			  else {
				  deductedNoteList = exportCSVDao.deductedNoteData(applYM, fsdate, fedate, osdate, oedate, dateFormats, inhCodes,null,null);
			  }
		  }
		  else {
			 
			  String dataformat = dateFormats[0];
			  if(dataformat.equals("op")) {
				  opdCount = exportCSVDao.opdCount(applYM, fsdate, fedate, inhCodes);
				  oppCount = exportCSVDao.oppCount(applYM, fsdate, fedate, inhCodes);
				  opsoCount = exportCSVDao.opsoCount(applYM, fsdate, fedate, inhCodes);
				  deductedCount = exportCSVDao.deductedNoteCount(applYM, fsdate, fedate, osdate, oedate, dateFormats, inhCodes);
				  
				  if(opdCount > 2000) {
					  List<LinkedHashMap<String, Object>> dataList = new ArrayList<LinkedHashMap<String,Object>>();
					  double d = opdCount;
					  Long l =  Math.round(d / 1000.0);
					  int c = 1000;
					  for(int i=0; i<l; i++) {
						  dataList.addAll(exportCSVDao.opdData(applYM, fsdate, fedate, inhCodes, "1000", String.valueOf(c)));
						  c += 1000;
					  }
					  opdList = dataList;
				  }
				  else {
					  opdList = exportCSVDao.opdData(applYM, fsdate, fedate, inhCodes,null,null);
				  }
				  if(oppCount > 2000) {
					  List<LinkedHashMap<String, Object>> dataList = new ArrayList<LinkedHashMap<String,Object>>();
					  double d = oppCount;
					  Long l =  Math.round(d / 1000.0);
					  int c = 1000;
					  for(int i=0; i<l; i++) {
						  dataList.addAll(exportCSVDao.oppData(applYM, fsdate, fedate, inhCodes, "1000", String.valueOf(c)));
						  c += 1000;
					  }
					  oppList = dataList;
				  }
				  else {
					  oppList = exportCSVDao.oppData(applYM, fsdate, fedate, inhCodes,null,null);
				  }
				  if(opsoCount > 2000) {
					  List<LinkedHashMap<String, Object>> dataList = new ArrayList<LinkedHashMap<String,Object>>();
					  double d = opsoCount;
					  Long l =  Math.round(d / 1000.0);
					  int c = 1000;
					  for(int i=0; i<l; i++) {
						  dataList.addAll(exportCSVDao.opsoData(applYM, fsdate, fedate, inhCodes, "1000", String.valueOf(c)));
						  c += 1000;
					  }
					  opsoList = dataList;
				  }
				  else {
					  opsoList = exportCSVDao.opsoData(applYM, fsdate, fedate, inhCodes,null,null);
				  }
				  if(deductedCount > 2000) {
					  List<LinkedHashMap<String, Object>> dataList = new ArrayList<LinkedHashMap<String,Object>>();
					  double d = deductedCount;
					  Long l =  Math.round(d / 1000.0);
					  int c = 1000;
					  for(int i=0; i<l; i++) {
						  dataList.addAll(exportCSVDao.deductedNoteData(applYM, fsdate, fedate, osdate, oedate, dateFormats, inhCodes, "1000", String.valueOf(c)));
						  c += 1000;
					  }
					  deductedNoteList = dataList;
				  }
				  else {
					  deductedNoteList = exportCSVDao.deductedNoteData(applYM, fsdate, fedate, osdate, oedate, dateFormats, inhCodes,null,null);
				  }
			  }
			  else {
				  ipdCount = exportCSVDao.ipdCount(applYM, fsdate, fedate, osdate, oedate, inhCodes);
				  ippCount = exportCSVDao.ippCount(applYM, fsdate, fedate, osdate, oedate, inhCodes);
				  ipsoCount = exportCSVDao.ipsoCount(applYM, fsdate, fedate, osdate, oedate, inhCodes);
				  deductedCount = exportCSVDao.deductedNoteCount(applYM, fsdate, fedate, osdate, oedate, dateFormats, inhCodes);
				  
				  if(ipdCount > 2000) { 
					  List<LinkedHashMap<String, Object>> dataList = new ArrayList<LinkedHashMap<String,Object>>();
					  double d = ipdCount;
					  Long l =  Math.round(d / 1000.0);
					  int c = 1000;
					  for(int i=0; i<l; i++) {
						  dataList.addAll(exportCSVDao.ipdData(applYM, fsdate, fedate, osdate, oedate, inhCodes, "1000", String.valueOf(c)));
						  c += 1000;
					  }
					  ipdList = dataList;
				  }
				  else {
					  ipdList = exportCSVDao.ipdData(applYM, fsdate, fedate, osdate, oedate, inhCodes, null, null);
				  }
				  if(ippCount > 2000) {
					  List<LinkedHashMap<String, Object>> dataList = new ArrayList<LinkedHashMap<String,Object>>();
					  double d = ippCount;
					  Long l =  Math.round(d / 1000.0);
					  int c = 1000;
					  for(int i=0; i<l; i++) {
						  dataList.addAll(exportCSVDao.ippData(applYM, fsdate, fedate, osdate, oedate, inhCodes, "1000", String.valueOf(c)));
						  c += 1000;
					  }
					  ippList = dataList;
				  }
				  else {
					  ippList = exportCSVDao.ippData(applYM, fsdate, fedate, osdate, oedate, inhCodes,null,null);
				  }
				  if(ipsoCount > 2000) {
					  List<LinkedHashMap<String, Object>> dataList = new ArrayList<LinkedHashMap<String,Object>>();
					  double d = ipsoCount;
					  Long l =  Math.round(d / 1000.0);
					  int c = 1000;
					  for(int i=0; i<l; i++) {
						  dataList.addAll(exportCSVDao.ipsoData(applYM, fsdate, fedate, osdate, oedate, inhCodes, "1000", String.valueOf(c)));
						  c += 1000;
					  }
					  ipsoList = dataList;
				  }
				  else {
					  ipsoList = exportCSVDao.ipsoData(applYM, fsdate, fedate, osdate, oedate, inhCodes,null,null);
				  }
				  
				  if(deductedCount > 2000) {
					  List<LinkedHashMap<String, Object>> dataList = new ArrayList<LinkedHashMap<String,Object>>();
					  double d = deductedCount;
					  Long l =  Math.round(d / 1000.0);
					  int c = 1000;
					  for(int i=0; i<l; i++) {
						  dataList.addAll(exportCSVDao.deductedNoteData(applYM, fsdate, fedate, osdate, oedate, dateFormats, inhCodes, "1000", String.valueOf(c)));
						  c += 1000;
					  }
					  deductedNoteList = dataList;
				  }
				  else {
					  deductedNoteList = exportCSVDao.deductedNoteData(applYM, fsdate, fedate, osdate, oedate, dateFormats, inhCodes,null,null);
				  }
			  }
		  }
	  }
	  else {
		  ipdCount = exportCSVDao.ipdCount(applYM, fsdate, fedate, osdate, oedate, inhCodes);
		  ippCount = exportCSVDao.ippCount(applYM, fsdate, fedate, osdate, oedate, inhCodes);
		  ipsoCount = exportCSVDao.ipsoCount(applYM, fsdate, fedate, osdate, oedate, inhCodes);
		  opdCount = exportCSVDao.opdCount(applYM, fsdate, fedate, inhCodes);
		  oppCount = exportCSVDao.oppCount(applYM, fsdate, fedate, inhCodes);
		  opsoCount = exportCSVDao.opsoCount(applYM, fsdate, fedate, inhCodes);
		  deductedCount = exportCSVDao.deductedNoteCount(applYM, fsdate, fedate, osdate, oedate, dateFormats, inhCodes);
		  
		  if(ipdCount > 2000) { 
			  List<LinkedHashMap<String, Object>> dataList = new ArrayList<LinkedHashMap<String,Object>>();
			  double d = ipdCount;
			  Long l =  Math.round(d / 1000.0);
			  int c = 1000;
			  for(int i=0; i<l; i++) {
				  dataList.addAll(exportCSVDao.ipdData(applYM, fsdate, fedate, osdate, oedate, inhCodes, "1000", String.valueOf(c)));
				  c += 1000;
			  }
			  ipdList = dataList;
		  }
		  else {
			  ipdList = exportCSVDao.ipdData(applYM, fsdate, fedate, osdate, oedate, inhCodes, null, null);
		  }
		  if(ippCount > 2000) {
			  List<LinkedHashMap<String, Object>> dataList = new ArrayList<LinkedHashMap<String,Object>>();
			  double d = ippCount;
			  Long l =  Math.round(d / 1000.0);
			  int c = 1000;
			  for(int i=0; i<l; i++) {
				  dataList.addAll(exportCSVDao.ippData(applYM, fsdate, fedate, osdate, oedate, inhCodes, "1000", String.valueOf(c)));
				  c += 1000;
			  }
			  ippList = dataList;
		  }
		  else {
			  ippList = exportCSVDao.ippData(applYM, fsdate, fedate, osdate, oedate, inhCodes,null,null);
		  }
		  if(ipsoCount > 2000) {
			  List<LinkedHashMap<String, Object>> dataList = new ArrayList<LinkedHashMap<String,Object>>();
			  double d = ipsoCount;
			  Long l =  Math.round(d / 1000.0);
			  int c = 1000;
			  for(int i=0; i<l; i++) {
				  dataList.addAll(exportCSVDao.ipsoData(applYM, fsdate, fedate, osdate, oedate, inhCodes, "1000", String.valueOf(c)));
				  c += 1000;
			  }
			  ipsoList = dataList;
		  }
		  else {
			  ipsoList = exportCSVDao.ipsoData(applYM, fsdate, fedate, osdate, oedate, inhCodes,null,null);
		  }
		  if(opdCount > 2000) {
			  List<LinkedHashMap<String, Object>> dataList = new ArrayList<LinkedHashMap<String,Object>>();
			  double d = opdCount;
			  Long l =  Math.round(d / 1000.0);
			  int c = 1000;
			  for(int i=0; i<l; i++) {
				  dataList.addAll(exportCSVDao.opdData(applYM, fsdate, fedate, inhCodes, "1000", String.valueOf(c)));
				  c += 1000;
			  }
			  opdList = dataList;
		  }
		  else {
			  opdList = exportCSVDao.opdData(applYM, fsdate, fedate, inhCodes,null,null);
		  }
		  if(oppCount > 2000) {
			  List<LinkedHashMap<String, Object>> dataList = new ArrayList<LinkedHashMap<String,Object>>();
			  double d = oppCount;
			  Long l =  Math.round(d / 1000.0);
			  int c = 1000;
			  for(int i=0; i<l; i++) {
				  dataList.addAll(exportCSVDao.oppData(applYM, fsdate, fedate, inhCodes, "1000", String.valueOf(c)));
				  c += 1000;
			  }
			  oppList = dataList;
		  }
		  else {
			  oppList = exportCSVDao.oppData(applYM, fsdate, fedate, inhCodes,null,null);
		  }
		  if(opsoCount > 2000) {
			  List<LinkedHashMap<String, Object>> dataList = new ArrayList<LinkedHashMap<String,Object>>();
			  double d = opsoCount;
			  Long l =  Math.round(d / 1000.0);
			  int c = 1000;
			  for(int i=0; i<l; i++) {
				  dataList.addAll(exportCSVDao.opsoData(applYM, fsdate, fedate, inhCodes, "1000", String.valueOf(c)));
				  c += 1000;
			  }
			  opsoList = dataList;
		  }
		  else {
			  opsoList = exportCSVDao.opsoData(applYM, fsdate, fedate, inhCodes,null,null);
		  }
		  if(deductedCount > 2000) {
			  List<LinkedHashMap<String, Object>> dataList = new ArrayList<LinkedHashMap<String,Object>>();
			  double d = deductedCount;
			  Long l =  Math.round(d / 1000.0);
			  int c = 1000;
			  for(int i=0; i<l; i++) {
				  dataList.addAll(exportCSVDao.deductedNoteData(applYM, fsdate, fedate, osdate, oedate, dateFormats, inhCodes, "1000", String.valueOf(c)));
				  c += 1000;
			  }
			  deductedNoteList = dataList;
		  }
		  else {
			  deductedNoteList = exportCSVDao.deductedNoteData(applYM, fsdate, fedate, osdate, oedate, dateFormats, inhCodes,null,null);
		  }
	  }
	  String timeStamp = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
	  String tempPath = getTemporaryPath();
	  java.io.File fwork = new java.io.File(tempPath);
      if (!fwork.exists()) { 
          fwork.mkdirs();
      }
	  List<String> csvFilesPath = new ArrayList<String>();
	  if(ipdList.size() > 0) {
		  String filePath = tempPath + "/ipd_"+timeStamp+".csv";
		  csvFilesPath.add(ExcelUtil.createCSV(ipdList, filePath));
	  }
	  
	  if(ippList.size() > 0) {
		  String filePath = tempPath + "/ipp_"+timeStamp+".csv";
		  csvFilesPath.add(ExcelUtil.createCSV(ippList, filePath));
	  }
	  if(ipsoList.size() > 0) {
		  String filePath = tempPath + "/ipd_sop_"+timeStamp+".csv";
		  csvFilesPath.add(ExcelUtil.createCSV(ipsoList, filePath));
	  }
	  if(opdList.size() > 0) {
		  String filePath = tempPath + "/opd_"+timeStamp+".csv";
		  csvFilesPath.add(ExcelUtil.createCSV(opdList, filePath));
	  }
	  if(oppList.size() > 0) {
		  String filePath = tempPath + "/opp_"+timeStamp+".csv";
		  csvFilesPath.add(ExcelUtil.createCSV(oppList, filePath));
	  }
	  if(opsoList.size() > 0) {
		  String filePath = tempPath + "/opd_sop_"+timeStamp+".csv";
		  csvFilesPath.add(ExcelUtil.createCSV(opsoList, filePath));
	  }
	  if(deductedNoteList.size() > 0) {
		  String filePath = tempPath + "/deducted_"+timeStamp+".csv";
		  csvFilesPath.add(ExcelUtil.createCSV(deductedNoteList, filePath));
	  }
	  
	    String fileNameStr = "病例資料";
		String fileName = URLEncoder.encode(fileNameStr, "UTF-8");
		String filepath = (System.getProperty("os.name").toLowerCase().startsWith("windows"))
				? FILE_PATH + "\\" + fileName
				: FILE_PATH + "/" + fileName;
		File file = new File(filepath);
		response.reset();
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "*");
		response.setHeader("Content-Disposition",
				"attachment; filename=" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + ".zip");
		response.setContentType("application/zip");
		try(ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream())) {
            for(String fileNamePath : csvFilesPath) {
                FileSystemResource fileSystemResource = new FileSystemResource(fileNamePath);
                ZipEntry zipEntry = new ZipEntry(fileSystemResource.getFilename());
                zipEntry.setSize(fileSystemResource.contentLength());
                zipEntry.setTime(System.currentTimeMillis());

                zipOutputStream.putNextEntry(zipEntry);

                StreamUtils.copy(fileSystemResource.getInputStream(), zipOutputStream);
                zipOutputStream.closeEntry();
            }
            zipOutputStream.finish();
            
            httpServletReq.setAttribute(LogType.EXPORT.name()+"_CNT", ipdList.size() + 
            		                                                  ippList.size() + 
            		                                                  ipsoList.size()+ 
            		                                                  opdList.size() + 
            		                                                  oppList.size() + 
            		                                                  opsoList.size()+ 
            		                                                  deductedNoteList.size());
            
            for (String fname : csvFilesPath) {
                Utility.deleteFile(fname);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
		
  }
  
  public String getTemporaryPath() {
      java.io.File fcurrent = new java.io.File("");
      String separator = (System.getProperty("os.name").toLowerCase().startsWith("windows")) ? "\\" : "/" ;
      String currentPath = fcurrent.getAbsolutePath()+separator;
      String backupPath = currentPath+"tempcsv";
      return (backupPath);
  }

  private String trimString(String s) {
    String result = s;
    if (result != null) {
      result = result.strip();
      if (result.length() == 0) {
        return null;
      }
    }
    return result;
  }
  
}
