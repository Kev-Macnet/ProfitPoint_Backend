/**
 * Created on 2021/9/11.
 */
package tw.com.leadtek.nhiwidget.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.assertj.core.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.io.Files;
import tw.com.leadtek.nhiwidget.constant.INTELLIGENT_REASON;
import tw.com.leadtek.nhiwidget.constant.LogType;
import tw.com.leadtek.nhiwidget.constant.XMLConstant;
import tw.com.leadtek.nhiwidget.controller.BaseController;
import tw.com.leadtek.nhiwidget.dao.ATCDao;
import tw.com.leadtek.nhiwidget.dao.CODE_TABLEDao;
import tw.com.leadtek.nhiwidget.dao.DEDUCTEDDao;
import tw.com.leadtek.nhiwidget.dao.FILE_DOWNLOADDao;
import tw.com.leadtek.nhiwidget.dao.ICD10Dao;
import tw.com.leadtek.nhiwidget.dao.IP_DDao;
import tw.com.leadtek.nhiwidget.dao.IP_PDao;
import tw.com.leadtek.nhiwidget.dao.IP_TDao;
import tw.com.leadtek.nhiwidget.dao.MRDao;
import tw.com.leadtek.nhiwidget.dao.OP_DDao;
import tw.com.leadtek.nhiwidget.dao.OP_PDao;
import tw.com.leadtek.nhiwidget.dao.OP_TDao;
import tw.com.leadtek.nhiwidget.dao.PARAMETERSDao;
import tw.com.leadtek.nhiwidget.dao.PAY_CODEDao;
import tw.com.leadtek.nhiwidget.model.rdb.ATC;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_TABLE;
import tw.com.leadtek.nhiwidget.model.rdb.DEDUCTED;
import tw.com.leadtek.nhiwidget.model.rdb.DHead;
import tw.com.leadtek.nhiwidget.model.rdb.FILE_DOWNLOAD;
import tw.com.leadtek.nhiwidget.model.rdb.ICD10;
import tw.com.leadtek.nhiwidget.model.rdb.IP;
import tw.com.leadtek.nhiwidget.model.rdb.IP_D;
import tw.com.leadtek.nhiwidget.model.rdb.IP_P;
import tw.com.leadtek.nhiwidget.model.rdb.IP_T;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.model.rdb.OP;
import tw.com.leadtek.nhiwidget.model.rdb.OP_D;
import tw.com.leadtek.nhiwidget.model.rdb.OP_P;
import tw.com.leadtek.nhiwidget.model.rdb.OP_T;
import tw.com.leadtek.nhiwidget.model.rdb.PARAMETERS;
import tw.com.leadtek.nhiwidget.model.rdb.PAY_CODE;
import tw.com.leadtek.nhiwidget.model.redis.CodeBaseLongId;
import tw.com.leadtek.nhiwidget.model.xml.InPatient;
import tw.com.leadtek.nhiwidget.model.xml.InPatientDData;
import tw.com.leadtek.nhiwidget.model.xml.OutPatient;
import tw.com.leadtek.nhiwidget.model.xml.OutPatientDData;
import tw.com.leadtek.nhiwidget.payload.ATCListResponse;
import tw.com.leadtek.nhiwidget.payload.PayCodeListResponse;
import tw.com.leadtek.nhiwidget.payload.PayCodePayload;
import tw.com.leadtek.nhiwidget.payload.system.CompareWarningPayload;
import tw.com.leadtek.nhiwidget.payload.system.DbManagement;
import tw.com.leadtek.nhiwidget.payload.system.DeductedListResponse;
import tw.com.leadtek.nhiwidget.payload.system.FileManagementPayload;
import tw.com.leadtek.nhiwidget.payload.system.ICD10ListResponse;
import tw.com.leadtek.nhiwidget.payload.system.IntelligentConfig;
import tw.com.leadtek.nhiwidget.payload.system.QuestionMarkPayload;
import tw.com.leadtek.tools.DateTool;
import tw.com.leadtek.tools.Utility;

@Service
public class SystemService {

  private Logger logger = LogManager.getLogger();

  public final static String INIT_FILE_PARAMETERS = "PARAMETER";

  public final static String INIT_FILE_PAY_CODE = "醫療服務給付項目";

  /**
   * 高雄霖園醫院 藥品衛材xxxxxxx.xlsx
   */
  public final static String INIT_FILE_PAY_CODE_LINYUAN = "代碼品項_高雄霖園";

  /**
   * 羅東博愛醫院 基本檔_藥品衛材.xlsx
   */
  public final static String INIT_FILE_PAY_CODE_POHAI = "代碼品項_羅東博愛";

  public final static String INIT_FILE_PAY_CODE_NAVY = "代碼品項_高雄海總";

  public final static String INIT_FILE_PAY_CODE_MS = "代碼品項_桃園敏盛";

  private final static String INIT_FILE_CODE_TABLE = "CODE_TABLE";

  private final static String INIT_FILE_ICDCM = "ICD-10-CM";

  private final static String INIT_FILE_ICDPCS = "ICD-10-PCS";

  private final static String INIT_FILE_ATC = "ATC";

  private final static String INIT_FILE_INFECTIOUS = "法定傳染病";

  private final static String INIT_FILE_USER = "USER_";

  private final static String INIT_FILE_DEPARTMENT = "DEPARTMENT_";

  private final static String INIT_FILE_USER_DEPARTMENT = "UD_";

  private final static String INIT_FILE_DEDUCTED_COMPUTER = "(行政審查)全民健康保險檔案分析審查異常不予支付";

  private final static String INIT_FILE_DEDUCTED_ARTIFICIAL = "(專業審查)不予支付理由";

  public final static String FILE_PATH = "download";

  /**
   * 違反支付準則
   */
  public final static String MENU_VIOLATE = "/violate";

  public final static String MENU_PILOT_PROJECT = "/pilotProject";

  /**
   * 臨床路徑差異 – AI提示
   */
  public final static String MENU_CLINCAL = "/intelligent?menu=clincal";

  /**
   * 疑似職傷與異常就診紀錄 – AI提示
   */
  public final static String MENU_SUSPECTED = "/intelligent?menu=suspected";

  /**
   * DRG申報建議 – AI提示
   */
  public final static String MENU_DRG_SUGGESTION = "/intelligent?menu=drgSuggestion";

  public final static String MENU_FILE_MANAGEMENT = "/fileManagement";

  public final static String MENU_DB_MANAGEMENT = "/dbManagement";

  @Autowired
  private ATCDao atcDao;

  @Autowired
  private RedisService redisService;

  @Autowired
  private PAY_CODEDao payCodeDao;

  @Autowired
  private CODE_TABLEDao codeTableDao;

  @Autowired
  private DEDUCTEDDao deductedDao;

  @Autowired
  private ICD10Dao icd10Dao;

  @Autowired
  private PARAMETERSDao parametersDao;

  @Autowired
  private OP_TDao optDao;

  @Autowired
  private OP_DDao opdDao;

  @Autowired
  private OP_PDao oppDao;

  @Autowired
  private IP_TDao iptDao;

  @Autowired
  private IP_DDao ipdDao;

  @Autowired
  private IP_PDao ippDao;
  
  @Autowired
  private MRDao mrDao;

  @Autowired
  private NHIWidgetXMLService xmlService;

  @Autowired
  private FILE_DOWNLOADDao downloadDao;

  @Value("${project.apiUrl}")
  private String apiUrl;

  @Autowired
  private ParametersService parametersService;

  @Autowired
  private IntelligentService is;

  @Autowired
  private InitialDataService initial;

  @Autowired
  private HttpServletRequest httpServletReq;
  
  public ATCListResponse getATC(String code, String note, String orderBy, Boolean asc, int perPage,
      int page) {
    List<ATC> codes = new ArrayList<ATC>();
    Specification<ATC> spec = new Specification<ATC>() {

      private static final long serialVersionUID = 1L;

      public Predicate toPredicate(Root<ATC> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicate = new ArrayList<Predicate>();
        if (code != null) {
          predicate.add(cb.like(root.get("code"), code.toUpperCase() + "%"));
        }
        if (note != null && note.length() > 0) {
          predicate.add(cb.like(cb.upper(root.get("note")), "%" + note.toUpperCase() + "%"));
        }

        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));
        if (orderBy != null && asc != null) {
          List<Order> orderList = new ArrayList<Order>();
          if (asc.booleanValue()) {
            orderList.add(cb.asc(root.get(orderBy)));
          } else {
            orderList.add(cb.desc(root.get(orderBy)));
          }
          query.orderBy(orderList);
        }
        return query.getRestriction();
      }
    };

    ATCListResponse result = new ATCListResponse();
    result.setCount((int) atcDao.count(spec));
    result.setTotalPage(BaseController.countTotalPage(result.getCount(), perPage));
    Page<ATC> pages = atcDao.findAll(spec, PageRequest.of(page, perPage));
    if (pages != null && pages.getSize() > 0) {
      for (ATC atc : pages) {
        codes.add(atc);
      }
    }
    result.setData(codes);
    return result;
  }

  public ATC getATC(String code) {
    if (code == null) {
      return null;
    }
    List<ATC> list = atcDao.findByCode(code);
    if (list == null || list.size() == 0) {
      return null;
    }
    return list.get(0);
  }

  public void saveATC(ATC atc, boolean isCreate) {
    CodeBaseLongId cb = new CodeBaseLongId(0, atc.getCode(), null, null);
    cb.setCategory("ATC");
    if (atc.getNote().indexOf("（") > 0) {
      // 齲齒預防藥（Caries prophylactic agents）
      cb.setDesc(atc.getNote().substring(0, atc.getNote().indexOf("（")));
      cb.setDescEn(
          atc.getNote().substring(atc.getNote().indexOf("（") + 1, atc.getNote().length() - 1));
    } else {
      cb.setDesc(atc.getNote());
    }

    if (isCreate) {
      int id = redisService.getMaxId() + 1;
      cb.setId((long) id);
      atc.setRedisId(id);
      redisService.addIndexToRedisIndex(RedisService.INDEX_KEY, String.valueOf(cb.getId()),
          cb.getCode().toLowerCase());
    } else {
      cb.setId((long) atc.getRedisId());
    }
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.setSerializationInclusion(Include.NON_NULL);
      String json = objectMapper.writeValueAsString(cb);
      // 1. save to data
      redisService.putHash("ICD10-data", String.valueOf(cb.getId()), json);
      // 2. save code to index for search
    } catch (JsonProcessingException e) {
      logger.error("saveATC", e);
    }
    atcDao.save(atc);
  }

  public void deleteATC(ATC atc) {
    atcDao.delete(atc);
    redisService.deleteHash("ICD10-data", atc.getRedisId().toString());
    redisService.removeIndexToRedisIndex("ICD10-index", atc.getCode(), atc.getRedisId());
  }

  public PayCodeListResponse getPayCode(String startDay, String endDay, String atc, String codeType,
      String code, String inhCode, String name, String inhName, String orderBy, Boolean asc,
      int perPage, int page) {

    Specification<PAY_CODE> spec = new Specification<PAY_CODE>() {

      private static final long serialVersionUID = 1L;

      public Predicate toPredicate(Root<PAY_CODE> root, CriteriaQuery<?> query,
          CriteriaBuilder cb) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date sd = null;
        Date ed = null;
        try {
          if (startDay != null && startDay.length() > 0) {
            sd = sdf.parse(startDay);
          }
          if (endDay != null && endDay.length() > 0) {
            ed = sdf.parse(endDay);
          }
        } catch (ParseException e) {
          logger.error("getPayCode", e);
        }

        List<Predicate> predicate = new ArrayList<Predicate>();
        if (atc != null) {
          predicate.add(cb.like(root.get("atc"), atc.toUpperCase() + "%"));
        }
        if (codeType != null && codeType.length() > 0) {
          predicate.add(cb.equal(root.get("codeType"), codeType));
        }
        if (code != null && code.length() > 1) {
          predicate.add(cb.like(root.get("code"), code + "%"));
        }
        if (inhCode != null && inhCode.length() > 1) {
          predicate.add(cb.like(root.get("inhCode"), inhCode + "%"));
        }
        if (name != null && name.length() > 1) {
          predicate.add(cb.like(root.get("name"), name + "%"));
        }
        if (inhName != null && inhName.length() > 1) {
          predicate.add(cb.like(root.get("inhName"), inhName + "%"));
        }
        if (sd != null && ed != null) {
          predicate.add(cb.or(
              cb.and(cb.greaterThanOrEqualTo(root.get("startDate"), sd),
                  cb.lessThanOrEqualTo(root.get("startDate"), ed)),
              cb.and(cb.greaterThanOrEqualTo(root.get("endDate"), sd),
                  cb.lessThanOrEqualTo(root.get("endDate"), ed))));
        } else {
          if (sd != null) {
            predicate.add(cb.greaterThanOrEqualTo(root.get("startDate"), sd));
          } else if (ed != null) {
            predicate.add(cb.lessThanOrEqualTo(root.get("endDate"), ed));
          }
        }
        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));

        if (orderBy != null && asc != null) {
          List<Order> orderList = new ArrayList<Order>();
          if (asc.booleanValue()) {
            orderList.add(cb.asc(root.get(orderBy)));
          } else {
            orderList.add(cb.desc(root.get(orderBy)));
          }
          query.orderBy(orderList);
        }
        return query.getRestriction();
      }
    };

    PayCodeListResponse result = new PayCodeListResponse();
    result.setCount((int) payCodeDao.count(spec));
    result.setTotalPage(BaseController.countTotalPage(result.getCount(), perPage));

    List<PayCodePayload> codes = new ArrayList<PayCodePayload>();
    Page<PAY_CODE> pages = payCodeDao.findAll(spec, PageRequest.of(page, perPage));
    if (pages != null && pages.getSize() > 0) {
      for (PAY_CODE pc : pages) {
        codes.add(new PayCodePayload(pc));
      }
    }
    result.setData(codes);
    return result;
  }

  public PAY_CODE getPayCode(PAY_CODE pc) {
    if (pc == null) {
      return null;
    }
    if (pc.getId() != null && pc.getId() > 0) {
      Optional<PAY_CODE> optional = payCodeDao.findById(pc.getId());
      if (optional.isPresent()) {
        return optional.get();
      }
      return null;
    }
    List<PAY_CODE> list = null;
    if (pc.getCode() != null) {
      list = payCodeDao.findByCode(pc.getCode());
    } else if (pc.getInhCode() != null) {
      list = payCodeDao.findByInhCode(pc.getInhCode());
    }
    if (list == null || list.size() == 0) {
      return null;
    }
    return checkPayCodeStartAndEndDay(list, pc);
  }

  private PAY_CODE checkPayCodeStartAndEndDay(List<PAY_CODE> list, PAY_CODE pc) {
    for (PAY_CODE payCode : list) {
      if (payCode.getStartDate().getTime() == pc.getStartDate().getTime()
          && payCode.getEndDate().getTime() == pc.getEndDate().getTime()) {
        return payCode;
      }
    }
    return null;
  }

  private List<PAY_CODE> getPayCodeWithSameCode(PAY_CODE pc) {
    List<PAY_CODE> list = null;
    if (pc.getInhCode() != null) {
      pc.setInhCode(pc.getInhCode().toUpperCase());
    }
    if (pc.getCode() != null) {
      pc.setCode(pc.getCode().toUpperCase());
    }
    if (pc.getCode() == null) {
      list = payCodeDao.findByInhCodeOrderByStartDateDesc(pc.getInhCode().toUpperCase());
    } else {
      list = payCodeDao.findByCodeOrderByStartDateDesc(pc.getCode().toUpperCase());
    }
    return list;
  }

  public boolean isTimeOverlapPayCode(PAY_CODE pc) {
    List<PAY_CODE> list = getPayCodeWithSameCode(pc);
    for (PAY_CODE payCode : list) {
      if (payCode.getId().longValue() == pc.getId().longValue()) {
        continue;
      }
      if (payCode.getStartDate().getTime() <= pc.getStartDate().getTime()
          && payCode.getEndDate().getTime() >= pc.getStartDate().getTime()) {
        return true;
      } else if (payCode.getStartDate().getTime() >= pc.getStartDate().getTime()
          && payCode.getEndDate().getTime() <= pc.getEndDate().getTime()) {
        return true;
      } else if (payCode.getStartDate().getTime() <= pc.getEndDate().getTime()
          && payCode.getEndDate().getTime() >= pc.getEndDate().getTime()) {
        return true;
      }
    }
    return false;
  }

  public void savePayCode(PAY_CODE pc, boolean isCreate) {
    List<PAY_CODE> list = getPayCodeWithSameCode(pc);
    if (isCreate) {
      moveEndDateInAdvancePayCode(list, pc);
    }

    String code = pc.getCode() == null ? pc.getInhCode() : pc.getCode();
    CodeBaseLongId cb = new CodeBaseLongId(0, pc.getCode(), null, null);
    cb.setCategory("ORDER");
    if (pc.getInhName() == null) {
      cb.setDesc(pc.getName());
    } else {
      cb.setDesc(pc.getInhName());
    }
    // redis 中 ICD10-data 的 id
    int id = redisService.getMaxId() + 1;
    if (isCreate) {
      cb.setId(new Long(id));
      pc.setRedisId(id);
      redisService.addIndexToRedisIndex(RedisService.INDEX_KEY, String.valueOf(cb.getId()),
          code.toLowerCase());
    } else {
      cb.setId((long) pc.getRedisId());
    }
    pc = payCodeDao.save(pc);

    try {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.setSerializationInclusion(Include.NON_NULL);
      String json = objectMapper.writeValueAsString(cb);
      // 1. save to data
      redisService.putHash("ICD10-data", String.valueOf(cb.getId()), json);
      // 2. save code to index for search
    } catch (JsonProcessingException e) {
      logger.error("saveATC", e);
    }
  }

  public void deletePayCode(PAY_CODE pc) {
    payCodeDao.delete(pc);
    redisService.deleteHash("ICD10-data", pc.getRedisId().toString());
    redisService.removeIndexToRedisIndex("ICD10-index", pc.getCode(), pc.getRedisId());
  }

  public List<String> getDeductedCat() {
    List<String> result = new ArrayList<String>();
    List<CODE_TABLE> list = codeTableDao.findByCatOrderByCode("DEDUCTED_L1");
    if (list == null || list.size() == 0) {
      return null;
    }
    for (CODE_TABLE ct : list) {
      result.add(ct.getDescChi());
    }
    return result;
  }

  public List<String> getDeductedCat(String l1) {
    List<String> result = new ArrayList<String>();
    List<CODE_TABLE> list = codeTableDao.findByCatOrderByCode("DEDUCTED_L2");
    if (list == null || list.size() == 0) {
      return null;
    }
    String codePrefix = null;
    if (l1.equals("專業審查不予支付代碼")) {
      codePrefix = "1";
    } else if (l1.equals("程序審查核減代碼")) {
      codePrefix = "2";
    } else if (l1.equals("進階人工核減代碼")) {
      codePrefix = "3";
    } else {
      return null;
    }
    for (CODE_TABLE ct : list) {
      if (ct.getCode().startsWith(codePrefix)) {
        result.add(ct.getDescChi());
      }
    }
    return result;
  }

  public List<String> getDeductedCat(String l1, String l2) {
    List<String> result = new ArrayList<String>();
    List<CODE_TABLE> list = codeTableDao.findByCatOrderByCode("DEDUCTED_L3");
    if (list == null || list.size() == 0) {
      return null;
    }
    String codePrefix = null;
    if (l1.equals("專業審查不予支付代碼")) {
      codePrefix = "1";
    } else if (l1.equals("程序審查核減代碼") || l1.equals("進階人工核減代碼")) {
      result.add("-");
      return result;
    }
    if (l2.equals("西醫")) {
      codePrefix = codePrefix + "1";
    } else if (l1.equals("中醫")) {
      codePrefix = codePrefix + "2";
    } else if (l1.equals("牙醫")) {
      codePrefix = codePrefix + "3";
    } else {
      return null;
    }
    for (CODE_TABLE ct : list) {
      if (ct.getCode().startsWith(codePrefix)) {
        result.add(ct.getDescChi());
      }
    }
    return result;
  }

  public DeductedListResponse getDuductedList(String l1, String l2, String l3, String code,
      String name, String orderBy, Boolean asc, int perPage, int page) {
    DeductedListResponse result = new DeductedListResponse();

    Specification<DEDUCTED> spec = new Specification<DEDUCTED>() {

      private static final long serialVersionUID = 1017L;

      public Predicate toPredicate(Root<DEDUCTED> root, CriteriaQuery<?> query,
          CriteriaBuilder cb) {

        List<Predicate> predicate = new ArrayList<Predicate>();
        if (l1 != null && l1.length() > 0) {
          predicate.add(cb.equal(root.get("l1"), l1));
        }
        if (l2 != null && l2.length() > 0) {
          predicate.add(cb.equal(root.get("l2"), l2));
        }
        if (l3 != null && l3.length() > 0) {
          predicate.add(cb.equal(root.get("l3"), l3));
        }
        if (code != null && code.length() > 0) {
          predicate.add(cb.like(root.get("code"), code + "%"));
        }
        if (name != null && name.length() > 0) {
          predicate.add(cb.equal(root.get("name"), name));
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
          orderList.add(cb.asc(root.get("code")));
        }
        query.orderBy(orderList);
        return query.getRestriction();
      }
    };
    long total = deductedDao.count(spec);
    Page<DEDUCTED> pages = deductedDao.findAll(spec, PageRequest.of(page, perPage));
    List<DEDUCTED> list = new ArrayList<DEDUCTED>();
    if (pages != null && pages.getSize() > 0) {
      for (DEDUCTED p : pages) {
        list.add(p);
      }
    }
    result.setCount((int) total);
    result.setTotalPage(Utility.getTotalPage((int) total, perPage));
    result.setData(list);
    return result;
  }

  public DEDUCTED getDeducted(long id) {
    Optional<DEDUCTED> optional = deductedDao.findById(id);
    if (optional.isPresent()) {
      return optional.get();
    }
    return null;
  }

  public String newDeducted(DEDUCTED request) {
    List<DEDUCTED> list = deductedDao.findByCode(request.getCode());
    if (list != null && list.size() > 0) {
      return "核減代碼" + request.getCode() + "已存在";
    }
    String checkResult = checkDeductedCat(request.getL1(), request.getL2());
    if (checkResult != null) {
      return checkResult;
    }
    request.setUpdateAt(new Date());
    request.setStatus(1);
    deductedDao.save(request);
    return null;
  }

  public String checkDeductedCat(String l1, String l2) {
    if (l1 == null) {
      return "核減大分類值不可為空";
    }
    if (l2 == null) {
      return "核減中分類值不可為空";
    }
    List<String> l1List = getDeductedCat();
    if (!l1List.contains(l1)) {
      return "核減大分類:" + l1 + "不存在！";
    }
    List<String> l2List = getDeductedCat(l1);
    if (!l2List.contains(l2)) {
      return "核減中分類:" + l2 + "不存在！";
    }
    return null;
  }

  public String updateDeducted(DEDUCTED request) {
    List<DEDUCTED> list = deductedDao.findByCode(request.getCode());
    if (list == null || list.size() == 0) {
      return "核減代碼" + request.getCode() + "不存在";
    }
    DEDUCTED old = list.get(0);
    String checkResult = checkDeductedCat(request.getL1(), request.getL2());
    if (checkResult != null) {
      return checkResult;
    }
    request.setId(old.getId());
    request.setUpdateAt(new Date());
    request.setStatus(1);
    deductedDao.save(request);
    return null;
  }

  public String deleteDeducted(long id) {
    Optional<DEDUCTED> optional = deductedDao.findById(id);
    if (optional.isPresent()) {
      deductedDao.deleteById(id);
    } else {
      return "核減代碼id:" + id + "不存在";
    }
    return null;
  }

  public ICD10ListResponse getIcd10(String code, String descChi, Boolean isInfectious,
      String infCat, int perPage, int page, String orderBy, Boolean asc) {
    List<ICD10> codes = new ArrayList<ICD10>();
    Specification<ICD10> spec = new Specification<ICD10>() {

      private static final long serialVersionUID = 1L;

      public Predicate toPredicate(Root<ICD10> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicate = new ArrayList<Predicate>();
        if (code != null) {
          // cb.upper 全轉為大寫以免因大小寫不同而搜尋不到
          predicate.add(cb.like(cb.upper(root.get("code")), code.toUpperCase() + "%"));
        }
        if (descChi != null) {
          predicate.add(cb.or(cb.like(root.get("descChi"), "%" + descChi + "%"),
              cb.like(cb.upper(root.get("descEn")), "%" + descChi.toUpperCase() + "%")));
        }
        if (isInfectious != null) {
          predicate.add(cb.equal(root.get("infectious"), isInfectious ? 1 : 0));
        }
        if (infCat != null) {
          String cat = null;
          if ("第一類".equals(infCat)) {
            cat = "1";
          } else if ("第二類".equals(infCat)) {
            cat = "2";
          } else if ("第三類".equals(infCat)) {
            cat = "3";
          } else if ("第四類".equals(infCat)) {
            cat = "4";
          } else if ("第五類".equals(infCat)) {
            cat = "5";
          }
          predicate.add(cb.equal(root.get("infCat"), cat));
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
          query.orderBy(orderList);
        } else {
          orderList.add(cb.asc(root.get("code")));
          query.orderBy(orderList);
        }
        return query.getRestriction();
      }
    };

    ICD10ListResponse result = new ICD10ListResponse();
    result.setCount((int) icd10Dao.count(spec));
    result.setTotalPage(BaseController.countTotalPage(result.getCount(), perPage));
    Page<ICD10> pages = icd10Dao.findAll(spec, PageRequest.of(page, perPage));
    if (pages != null && pages.getSize() > 0) {
      for (ICD10 icd10 : pages) {
        codes.add(icd10);
      }
    }
    result.setData(codes);
    return result;
  }

  public ICD10 getIcd10(long id) {
    return icd10Dao.findById(id).orElse(null);
  }

  public String saveIcd10(ICD10 icd10, boolean isCreate) {
    CodeBaseLongId cb = new CodeBaseLongId(0, icd10.getCode(), null, null);
    cb.setCategory("ICD10-CM");
    cb.setDesc(icd10.getDescChi());
    cb.setDescEn(icd10.getDescEn());

    if (isCreate) {
      int id = redisService.getMaxId() + 1;
      icd10.setRedisId((long) id);
      redisService.addIndexToRedisIndex(RedisService.INDEX_KEY, String.valueOf(cb.getId()),
          cb.getCode().toLowerCase());
      icd10.setId(null);
    } else {
      ICD10 old = icd10Dao.findById(icd10.getId()).orElse(null);
      if (old == null) {
        return "ICD10 不存在";
      }
      if (old.getRedisId() != null) {
        cb.setId((long) old.getRedisId());
      }
    }
    if (cb.getId() != null) {
      try {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        String json = objectMapper.writeValueAsString(cb);
        // 1. save to data
        redisService.putHash("ICD10-data", String.valueOf(cb.getId()), json);
        // 2. save code to index for search
      } catch (JsonProcessingException e) {
        logger.error("saveIcd10", e);
      }
    }
    icd10Dao.save(icd10);
    return null;
  }

  public String deleteIcd10(long id) {
    ICD10 db = getIcd10(id);
    if (db == null) {
      return "ICD10 id:" + id + "不存在";
    }

    icd10Dao.deleteById(id);
    redisService.deleteHash("ICD10-data", db.getRedisId().toString());
    redisService.removeIndexToRedisIndex("ICD10-index", db.getCode(), db.getRedisId().intValue());
    return null;
  }

  public FileManagementPayload getFileManagementPaylod() {
    return new FileManagementPayload(parametersDao.findByCatOrderByName("FILE_MANAGEMENT"));
  }

  public String updateFileManagementPaylod(FileManagementPayload payload) {
    if ("0".equals(parametersService.getParameter(MENU_FILE_MANAGEMENT))) {
      return null;
    }
    
    List<Long> pks = new ArrayList<>();
    
    List<PARAMETERS> list = parametersDao.findByCatOrderByName("FILE_MANAGEMENT");
    for (PARAMETERS p : list) {
      if (p.getName().equals("IS_DAILY_INPUT")) {
        if (payload.getDailyInput() != null) {
          p.setValue(payload.getDailyInput().booleanValue() ? "1" : "0");
        }
      } else if (p.getName().equals("INPUT_TIME")) {
        if (payload.getInputTime() != null) {
          p.setValue(payload.getInputTime());
        }
      } else if (p.getName().equals("INPUT_BY_FILE")) {
        if (payload.getInputByFile() != null) {
          p.setValue(payload.getInputByFile().booleanValue() ? "1" : "0");
        }
      } else if (p.getName().equals("INPUT_BY_BUTTON")) {
        if (payload.getInputByButton() != null) {
          p.setValue(payload.getInputByButton().booleanValue() ? "1" : "0");
        }
      } else if (p.getName().equals("IS_DAILY_OUTPUT")) {
        if (payload.getDailyOutput() != null) {
          p.setValue(payload.getDailyOutput().booleanValue() ? "1" : "0");
        }
      } else if (p.getName().equals("OUTPUT_TIME")) {
        if (payload.getOutputTime() != null) {
          p.setValue(payload.getOutputTime());
        }
      } else if (p.getName().equals("OUTPUT_BY_FILE")) {
        if (payload.getOutputByFile() != null) {
          p.setValue(payload.getOutputByFile().booleanValue() ? "1" : "0");
        }
      } else if (p.getName().equals("OUTPUT_BY_BUTTON")) {
        if (payload.getOutputByButton() != null) {
          p.setValue(payload.getOutputByButton().booleanValue() ? "1" : "0");
        }
      }
      p.setUpdateAt(new Date());
      parametersDao.save(p);
      
      pks.add(p.getId());
    }
    
    httpServletReq.setAttribute(LogType.ACTION_U.name()+"_PKS", pks);
    
    return null;
  }

  public CompareWarningPayload getCompareWarningPayload() {
    return new CompareWarningPayload(parametersDao.findByCatOrderByName("COMPARE_WARNING"));
  }

  public String updateCompareWarningPayload(CompareWarningPayload payload) {
   
	List<Long> pks = new ArrayList<>();
	  
	List<PARAMETERS> list = parametersDao.findByCatOrderByName("COMPARE_WARNING");
    for (PARAMETERS p : list) {
      if (p.getName().equals("COMPARE_BY")) {
        if (payload.getCompareBy() != null) {
          p.setValue(payload.getCompareBy().toString());
        }
      } else if (p.getName().equals("COMPARE_FUNC_TYPE")) {
        if (payload.getFuncType() != null) {
          p.setValue(payload.getFuncType());
        }
      } else if (p.getName().equals("COMPARE_DOCTOR")) {
        if (payload.getDoctor() != null) {
          p.setValue(payload.getDoctor());
        }
      } else if (p.getName().equals("ROLLBACK_HOUR")) {
        if (payload.getRollbackHour() != null) {
          p.setValue(payload.getRollbackHour().toString());
        }
      }
      p.setUpdateAt(new Date());
      parametersDao.save(p);
      
      pks.add(p.getId());
    }
    
    httpServletReq.setAttribute(LogType.ACTION_U.name()+"_PKS", pks);
    
    return null;
  }

  public QuestionMarkPayload getQuestionMarkPayload() {
    return new QuestionMarkPayload(parametersDao.findByCatOrderByName("QUESTION_MARK"));
  }

  public String updateQuestionMarkPayload(QuestionMarkPayload payload) {
    
	List<Long> pks = new ArrayList<>();
	
	List<PARAMETERS> list = parametersDao.findByCatOrderByName("QUESTION_MARK");
    for (PARAMETERS p : list) {
      if (p.getName().equals("MARK_BY")) {
        if (payload.getMarkBy() != null) {
          p.setValue(payload.getMarkBy().toString());
        }
      } else if (p.getName().equals("MARK_FUNC_TYPE")) {
        if (payload.getFuncType() != null) {
          p.setValue(payload.getFuncType());
        }
      } else if (p.getName().equals("MARK_DOCTOR")) {
        if (payload.getDoctor() != null) {
          p.setValue(payload.getDoctor());
        }
      }
      p.setUpdateAt(new Date());
      parametersDao.save(p);
      
      pks.add(p.getId());
    }
    
    httpServletReq.setAttribute(LogType.ACTION_U.name()+"_PKS", pks);
    
    return null;
  }

  public IntelligentConfig getIntelligentConfig() {
    IntelligentConfig result =
        new IntelligentConfig(parametersDao.findByCatOrderByName("INTELLIGENT_CONFIG"));
    if ("0".equals(parametersService.getParameter(MENU_PILOT_PROJECT))) {
      result.setPilotProject(false);
    }
    if ("0".equals(parametersService.getParameter(MENU_VIOLATE))) {
      result.setViolate(false);
    }
    if ("0".equals(parametersService.getParameter(MENU_CLINCAL))) {
      result.setClinicalDiff(false);
    }
    if ("0".equals(parametersService.getParameter(MENU_SUSPECTED))) {
      result.setSuspected(false);
    }
    if ("0".equals(parametersService.getParameter(MENU_DRG_SUGGESTION))) {
      result.setDrgSuggestion(false);
    }
    return result;
  }

  public String updateIntelligentConfig(IntelligentConfig payload) {
    
	List<Long> pks = new ArrayList<>();
	
	List<PARAMETERS> list = parametersDao.findByCatOrderByName("INTELLIGENT_CONFIG");
    HashMap<Integer, Boolean> needProcess = new HashMap<Integer, Boolean>();
    for (PARAMETERS p : list) {
      if (p.getName().equals("VIOLATE")) {
        if (payload.getViolate() != null
            && !"0".equals(parametersService.getParameter(MENU_VIOLATE))) {
          if ((payload.getViolate().booleanValue() && "1".equals(p.getValue()))
              || (!payload.getViolate().booleanValue() && "0".equals(p.getValue()))) {
            continue;
          }
          p.setValue(payload.getViolate().booleanValue() ? "1" : "0");
          needProcess.put(INTELLIGENT_REASON.VIOLATE.value(), payload.getViolate());
        }
      } else if (p.getName().equals("RARE_ICD")) {
        if (payload.getRareIcd() != null) {
          if ((payload.getRareIcd().booleanValue() && "1".equals(p.getValue()))
              || (!payload.getRareIcd().booleanValue() && "0".equals(p.getValue()))) {
            continue;
          }
          p.setValue(payload.getRareIcd().booleanValue() ? "1" : "0");
          needProcess.put(INTELLIGENT_REASON.RARE_ICD.value(), payload.getRareIcd());
        }
      } else if (p.getName().equals("HIGH_RATIO")) {
        if (payload.getHighRatio() != null) {
          if ((payload.getHighRatio().booleanValue() && "1".equals(p.getValue()))
              || (!payload.getHighRatio().booleanValue() && "0".equals(p.getValue()))) {
            continue;
          }
          p.setValue(payload.getHighRatio().booleanValue() ? "1" : "0");
          needProcess.put(INTELLIGENT_REASON.HIGH_RATIO.value(), payload.getHighRatio());
        }
      } else if (p.getName().equals("OVER_AMOUNT")) {
        if (payload.getOverAmount() != null) {
          if ((payload.getOverAmount().booleanValue() && "1".equals(p.getValue()))
              || (!payload.getOverAmount().booleanValue() && "0".equals(p.getValue()))) {
            continue;
          }
          p.setValue(payload.getOverAmount().booleanValue() ? "1" : "0");
          needProcess.put(INTELLIGENT_REASON.OVER_AMOUNT.value(), payload.getOverAmount());
        }
      } else if (p.getName().equals("INH_OWN_EXIST")) {
        if (payload.getInhOwnExist() != null) {
          if ((payload.getInhOwnExist().booleanValue() && "1".equals(p.getValue()))
              || (!payload.getInhOwnExist().booleanValue() && "0".equals(p.getValue()))) {
            continue;
          }
          p.setValue(payload.getInhOwnExist().booleanValue() ? "1" : "0");
          needProcess.put(INTELLIGENT_REASON.INH_OWN_EXIST.value(), payload.getInhOwnExist());
        }
      } else if (p.getName().equals("INFECTIOUS")) {
        if (payload.getInfectious() != null) {
          if ((payload.getInfectious().booleanValue() && "1".equals(p.getValue()))
              || (!payload.getInfectious().booleanValue() && "0".equals(p.getValue()))) {
            continue;
          }
          p.setValue(payload.getInfectious().booleanValue() ? "1" : "0");
          needProcess.put(INTELLIGENT_REASON.INFECTIOUS.value(), payload.getInfectious());
        }
      } else if (p.getName().equals("SAME_ATC")) {
        if (payload.getSameAtc() != null) {
          if ((payload.getSameAtc().booleanValue() && "1".equals(p.getValue()))
              || (!payload.getSameAtc().booleanValue() && "0".equals(p.getValue()))) {
            continue;
          }
          p.setValue(payload.getSameAtc().booleanValue() ? "1" : "0");
          needProcess.put(INTELLIGENT_REASON.SAME_ATC.value(), payload.getSameAtc());
        }
      } else if (p.getName().equals("SAME_ATC_LENGTH")) {
        if (payload.getSameAtcLen5() != null) {
          if ((payload.getSameAtcLen5().booleanValue() && "5".equals(p.getValue()))
              || (!payload.getSameAtcLen5().booleanValue() && "7".equals(p.getValue()))) {
            continue;
          }
          p.setValue(payload.getSameAtcLen5().booleanValue() ? "5" : "7");
          if ("1".equals(parametersService.getOneValueByName("INTELLIGENT_CONFIG", "SAME_ATC"))
              && needProcess.get(INTELLIGENT_REASON.SAME_ATC.value()) == null) {
            needProcess.put(INTELLIGENT_REASON.SAME_ATC.value(), true);
          }
        }
      } else if (p.getName().equals("PILOT_PROJECT")) {
        if (payload.getPilotProject() != null
            && !"0".equals(parametersService.getParameter(MENU_PILOT_PROJECT))) {
          if ((payload.getPilotProject().booleanValue() && "1".equals(p.getValue()))
              || (!payload.getPilotProject().booleanValue() && "0".equals(p.getValue()))) {
            continue;
          }
          p.setValue(payload.getPilotProject().booleanValue() ? "1" : "0");
          needProcess.put(INTELLIGENT_REASON.PILOT_PROJECT.value(), payload.getPilotProject());
        }
      } else if (p.getName().equals("HIGH_RISK")) {
        if (payload.getHighRisk() != null) {
          if ((payload.getHighRisk().booleanValue() && "1".equals(p.getValue()))
              || (!payload.getHighRisk().booleanValue() && "0".equals(p.getValue()))) {
            continue;

          }
          p.setValue(payload.getHighRisk().booleanValue() ? "1" : "0");
          needProcess.put(INTELLIGENT_REASON.HIGH_RISK.value(), payload.getHighRisk());
        }
      } else if (p.getName().equals("CLINICAL_DIFF")
          && !"0".equals(parametersService.getParameter(MENU_CLINCAL))) {
        if (payload.getClinicalDiff() != null) {
          if ((payload.getClinicalDiff().booleanValue() && "1".equals(p.getValue()))
              || (!payload.getClinicalDiff().booleanValue() && "0".equals(p.getValue()))) {
            continue;
          }
          p.setValue(payload.getClinicalDiff().booleanValue() ? "1" : "0");
          needProcess.put(INTELLIGENT_REASON.COST_DIFF.value(), payload.getClinicalDiff());
          needProcess.put(INTELLIGENT_REASON.ORDER_DIFF.value(), payload.getClinicalDiff());
          needProcess.put(INTELLIGENT_REASON.ORDER_DRUG.value(), payload.getClinicalDiff());
          needProcess.put(INTELLIGENT_REASON.IP_DAYS.value(), payload.getClinicalDiff());
        }
      } else if (p.getName().equals("COST_DIFF_UL")
          && !"0".equals(parametersService.getParameter(MENU_CLINCAL))) {
        if (payload.getCostDiffUL() != null) {
          if (payload.getCostDiffUL().equals(p.getValue())) {
            continue;
          }
          p.setValue(payload.getCostDiffUL());
          needProcess.put(INTELLIGENT_REASON.COST_DIFF.value(), true);
        }
      } else if (p.getName().equals("COST_DIFF_LL")
          && !"0".equals(parametersService.getParameter(MENU_CLINCAL))) {
        if (payload.getCostDiffLL() != null) {
          if (payload.getCostDiffLL().equals(p.getValue())) {
            continue;
          }
          p.setValue(payload.getCostDiffLL());
          needProcess.put(INTELLIGENT_REASON.COST_DIFF.value(), true);
        }
      } else if (p.getName().equals("ORDER_DIFF_UL")
          && !"0".equals(parametersService.getParameter(MENU_CLINCAL))) {
        if (payload.getOrderUL() != null) {
          if (payload.getOrderUL().equals(p.getValue())) {
            continue;
          }
          p.setValue(payload.getOrderUL());
          needProcess.put(INTELLIGENT_REASON.ORDER_DIFF.value(), true);
        }
      } else if (p.getName().equals("ORDER_DIFF_LL")
          && !"0".equals(parametersService.getParameter(MENU_CLINCAL))) {
        if (payload.getOrderLL() != null) {
          if (payload.getOrderLL().equals(p.getValue())) {
            continue;
          }
          p.setValue(payload.getOrderLL());
          needProcess.put(INTELLIGENT_REASON.ORDER_DIFF.value(), true);
        }
      } else if (p.getName().equals("IP_DAYS")
          && !"0".equals(parametersService.getParameter(MENU_CLINCAL))) {
        if (payload.getIpDays() != null) {
          if (payload.getIpDays().intValue() == Integer.parseInt(p.getValue())) {
            continue;
          }
          logger.info("IP_DAYS config change");
          p.setValue(payload.getIpDays().toString());
          needProcess.put(INTELLIGENT_REASON.IP_DAYS.value(), true);
        }
      } else if (p.getName().equals("SUSPECTED")
          && !"0".equals(parametersService.getParameter(MENU_SUSPECTED))) {
        if (payload.getSuspected() != null) {
          if ((payload.getSuspected().booleanValue() && "1".equals(p.getValue()))
              || (!payload.getSuspected().booleanValue() && "0".equals(p.getValue()))) {
            continue;
          }
          p.setValue(payload.getSuspected().booleanValue() ? "1" : "0");
          needProcess.put(INTELLIGENT_REASON.SUSPECTED.value(), payload.getSuspected());
        }
      } else if (p.getName().equals("DRG_SUGGESTION")
          && !"0".equals(parametersService.getParameter(MENU_DRG_SUGGESTION))) {
        if (payload.getDrgSuggestion() != null) {
          if ((payload.getDrgSuggestion().booleanValue() && "1".equals(p.getValue()))
              || (!payload.getDrgSuggestion().booleanValue() && "0".equals(p.getValue()))) {
            continue;
          }
          p.setValue(payload.getDrgSuggestion().booleanValue() ? "1" : "0");
          needProcess.put(INTELLIGENT_REASON.DRG_SUGGESTION.value(), payload.getSuspected());
        }
      }

      p.setUpdateAt(new Date());
      parametersDao.save(p);
      
      pks.add(p.getId());
    }
    recalculateAll(needProcess);
    
    httpServletReq.setAttribute(LogType.ACTION_U.name()+"_PKS", pks);
    
    return null;
  }

  public DbManagement getDbManagement() {
    return new DbManagement(parametersDao.findByCatOrderByName("DB_MANAGEMENT"));
  }

  public String updateDbManagement(DbManagement payload) {
	  
	List<Long> pks = new ArrayList<>();
	
    if ("0".equals(parametersService.getParameter(MENU_DB_MANAGEMENT))) {
      return null;
    }
    List<PARAMETERS> list = parametersDao.findByCatOrderByName("DB_MANAGEMENT");
    for (PARAMETERS p : list) {
      if (p.getName().equals("ADD_USER_BY")) {
        if (payload.getAddUserBy() != null) {
          p.setValue(payload.getAddUserBy().toString());
        }
      } else if (p.getName().equals("ADD_FUNC_CODE_BY")) {
        if (payload.getAddFuncCodeBy() != null) {
          p.setValue(payload.getAddFuncCodeBy().toString());
        }
      } else if (p.getName().equals("ADD_PAY_CODE_BY")) {
        if (payload.getAddPayCodeBy() != null) {
          p.setValue(payload.getAddPayCodeBy().toString());
        }
      }
      p.setUpdateAt(new Date());
      parametersDao.save(p);
      
      pks.add(p.getId());
    }
    
    httpServletReq.setAttribute(LogType.ACTION_U.name()+"_PKS", pks);
    
    return null;
  }

  /**
   * 將參數的結束日往前移一天
   * 
   * @param list
   * @param cal
   */
  private void moveEndDateInAdvancePayCode(List<PAY_CODE> list, PAY_CODE pc) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(pc.getStartDate());
    cal.add(Calendar.DAY_OF_YEAR, -1);
    for (PAY_CODE payCode : list) {
      if (pc != null && pc.getId().longValue() == payCode.getId().longValue()) {
        continue;
      }
      if (payCode.getStartDate().getTime() < pc.getEndDate().getTime()
          && payCode.getEndDate().getTime() > pc.getStartDate().getTime()) {
        payCode.setEndDate(cal.getTime());
        payCodeDao.save(payCode);
      }
    }
  }

  public String getDownloadFiles(long userId) {
    List<FILE_DOWNLOAD> list = downloadDao.findAllByOrderByUpdateAtDesc();
    if (list == null || list.size() == 0) {
      return "";
    }
    StringBuffer sb = new StringBuffer();
    for (FILE_DOWNLOAD file : list) {
      if (file.getUserId().longValue() != userId) {
        continue;
      }
      if ((System.currentTimeMillis() - file.getUpdateAt().getTime()) > 3600000) {
        // 超過一小時
        continue;
      }
      if (file.getFileType().equals(XMLConstant.DATA_FORMAT_OP)
          || file.getFileType().equals(XMLConstant.DATA_FORMAT_IP)) {
        String chineseYM = file.getFilename().substring(0, 5);
        String ym = DateTool.convertChineseToAD(chineseYM);
        String year = ym.substring(0, 4);
        String month = String.valueOf(Integer.parseInt(ym.substring(4)));
        if (file.getProgress().intValue() < 100) {
          sb.append("正在處理").append(year).append("年").append(month).append("月的");
          if (file.getFileType().equals(XMLConstant.DATA_FORMAT_OP)) {
            sb.append("門急診");
          } else {
            sb.append("住院");
          }
          sb.append("申報檔，已完成");
          sb.append(file.getProgress());
          sb.append("%");
          return sb.toString();
        } else {
          sb.append(year).append("年").append(month).append("月的");
          if (file.getFileType().equals(XMLConstant.DATA_FORMAT_OP)) {
            sb.append("門急診");
          } else {
            sb.append("住院");
          }
          sb.append("申報檔已完成，請點此<a href=\"");
          sb.append(apiUrl);
          sb.append("no/downloadXML/");
          sb.append(file.getId());
          sb.append("\">下載</a>");
          return sb.toString();
        }
      }
    }
    return sb.toString();
  }

  public String checkDownloadDir(String filePath) {
    File path = new File(filePath);
    if (!path.exists() || !path.isDirectory()) {
      path.mkdir();
    }
    return path.getAbsolutePath();
  }

  public String getDownloadXMLFilename(String dataFormat, String applY, String applM) {
    String applYM = String.valueOf(Integer.parseInt(applY) * 100 + Integer.parseInt(applM));
    return applYM + "-" + ("10".equals(dataFormat) ? "0" : "1") + ".xml";
  }

  public void downloadXML(String id, HttpServletResponse response) throws IOException {
    Optional<FILE_DOWNLOAD> optional = downloadDao.findById(Long.parseLong(id));
    if (!optional.isPresent()) {
      return;
    }
    FILE_DOWNLOAD download = optional.get();
    if (download.getProgress().intValue() < 100) {
      return;
    }
    String filepath = (System.getProperty("os.name").toLowerCase().startsWith("windows"))
        ? FILE_PATH + "\\" + download.getFilename()
        : FILE_PATH + "/" + download.getFilename();
    File file = new File(filepath);

    response.reset();
    response.setContentType("application/octet-stream; charset=BIG5");
    response.addHeader("Content-Disposition",
        "attachment;filename=" + URLEncoder.encode(download.getFilename(), "BIG5"));
    response.addHeader("Content-Length", String.valueOf(file.length()));
    Files.copy(file, response.getOutputStream());
  }

  public int downloadXML(String dataFormat, String applY, String applM, String applDate,
      String applCategory, String applMethod, String applMedic, long userId, String[] inhClinicIds,
      HttpServletResponse response) throws IOException {
    checkDownloadDir(FILE_PATH);
    String applYM = null;
    if (applY != null) {
      applYM = String.valueOf(Integer.parseInt(applY) * 100 + Integer.parseInt(applM));
    }

    String filename = (inhClinicIds != null && inhClinicIds.length > 0) ? inhClinicIds[0] + ".xml"
        : getDownloadXMLFilename(dataFormat, applY, applM);
    List<FILE_DOWNLOAD> list = downloadDao.findByFilenameAndFileType(filename, dataFormat);
    FILE_DOWNLOAD download = null;
    if (list == null || list.size() == 0) {
      download = new FILE_DOWNLOAD();
    } else {
      download = list.get(0);
    }
    download.setFilename(filename);
    download.setFileType(dataFormat);
    download.setProgress(0);
    download.setUpdateAt(new Date());
    download.setUserId(userId);
    download = downloadDao.save(download);
    logger.info("downloadXML generating:" + filename);
    outputXML(dataFormat, applYM, inhClinicIds, download);
    downloadDao.deleteById(download.getId());

    String filepath = (System.getProperty("os.name").toLowerCase().startsWith("windows"))
        ? FILE_PATH + "\\" + download.getFilename()
        : FILE_PATH + "/" + download.getFilename();
    File file = new File(filepath);

    logger.info("downloadXML path:" + filepath + "," + file.getAbsolutePath());
    // response.reset();
    response.setContentType("application/octet-stream; charset=BIG5");
    response.addHeader("Content-Disposition",
        "attachment;filename=" + URLEncoder.encode(filename, "BIG5"));
    response.addHeader("Content-Length", String.valueOf(file.length()));
    Files.copy(file, response.getOutputStream());
    return 100;

  }

  public void outputXML(String dataFormat, String applYm, String[] inhClinicIds,
      FILE_DOWNLOAD download) {
    if (XMLConstant.DATA_FORMAT_OP.equals(dataFormat)) {
      outputOPXML(applYm, inhClinicIds, download);
    } else if (XMLConstant.DATA_FORMAT_IP.equals(dataFormat)) {
      outputIPXML(applYm, inhClinicIds, download);
    }
    logger.info("outputXML " + download.getFilename() + " done");
  }
  
  public void outputXMLWithThread(String dataFormat, String applYm, String[] inhClinicIds, FILE_DOWNLOAD download) {
    Thread thread = new Thread(new Runnable() {

      @Override
      public void run() {
        if (XMLConstant.DATA_FORMAT_OP.equals(dataFormat)) {
          outputOPXML(applYm, inhClinicIds, download);
        } else if (XMLConstant.DATA_FORMAT_IP.equals(dataFormat)) {
          outputIPXML(applYm, inhClinicIds, download);
        }
        logger.info("outputXML " + download.getFilename() + " done");
      }
    });
    thread.start();
  }

  public void outputOPXML(String applYm, String[] inhClinicIds, FILE_DOWNLOAD download) {
    OutPatient op = new OutPatient();
    List<MR> mrList = null;
    List<Long> mrIdList = null;
    if (inhClinicIds != null && inhClinicIds.length > 0) {
      List<String> inhClinicIdList = new ArrayList<>();
      for (String string : inhClinicIds) {
        inhClinicIdList.add(string);
      }
      mrList = mrDao.findByInhClinicId((inhClinicIdList));
      if (mrList == null || mrList.size() == 0) {
        outputFileJAXB(op, download.getFilename());
        download.setProgress(100);
        download.setUpdateAt(new Date());
        downloadDao.save(download);
        return;
      }
      applYm = mrList.get(0).getApplYm();
      mrIdList = new ArrayList<Long>();
      for (MR mr : mrList) {
        mrIdList.add(mr.getId());
      }
    }
    List<OP_T> optList = optDao.findByFeeYmOrderById(applYm);
    OP_T opt = null;
    if (optList == null || optList.size() == 0) {
      opt = xmlService.createOPT(applYm);
    } else {
      opt = optList.get(0);
    }
    List<OP_D> opdList = (mrList == null) ? opdDao.findByApplYM(applYm) : opdDao.getOpdListByMrId(mrIdList);
    List<OP_P> oppList = (mrList == null) ? oppDao.findByApplYM(applYm) : oppDao.getOppListByMrIdList(mrIdList);
    op.setTdata(opt);

    List<OutPatientDData> ddata = new ArrayList<OutPatientDData>();
    double next = 0.1;
    for (int i = 0; i < opdList.size(); i++) {
      OP_D opd = opdList.get(i);
      OutPatientDData opData = new OutPatientDData();
      DHead dhead = new DHead();
      dhead.setCASE_TYPE(opd.getCaseType());
      dhead.setSEQ_NO(opd.getSeqNo());
      opData.setDhead(dhead);

      List<OP_P> pdata = new ArrayList<OP_P>();
      for (int j = oppList.size() - 1; j >= 0; j--) {
        OP_P opp = oppList.get(j);
        if (opp.getOpdId().longValue() == opd.getId().longValue()) {
          pdata.add(opp);
          oppList.remove(j);
        }
      }
      opd.setPdataList(pdata);
      opData.setDbody(opd);
      ddata.add(opData);
      if (((double) i / (double) opdList.size()) > next) {
        download.setProgress((int) (next * 100));
        download.setUpdateAt(new Date());
        downloadDao.save(download);
        next += 0.1;
      }
    }

    op.setDdata(ddata);
    outputFileJAXB(op, download.getFilename());
    
    httpServletReq.setAttribute(LogType.EXPORT.name()+"_CNT", ddata.size());
    
    if (opdList.size() > 0 && oppList.size() > 0) {
      optDao.save(opt);
    }
    download.setProgress(100);
    download.setUpdateAt(new Date());
    downloadDao.save(download);
  }

  public void outputIPXML(String applYm, String[] inhClinicIds, FILE_DOWNLOAD download) {
    InPatient ip = new InPatient();
    List<MR> mrList = null;
    List<Long> mrIdList = null;
    if (inhClinicIds != null && inhClinicIds.length > 0) {
      List<String> inhClinicIdList = new ArrayList<>();
      for (String string : inhClinicIds) {
        inhClinicIdList.add(string);
      }
      mrList = mrDao.findByInhClinicId((inhClinicIdList));
      if (mrList == null || mrList.size() == 0) {
        outputFileJAXB(ip, download.getFilename());
        download.setProgress(100);
        download.setUpdateAt(new Date());
        downloadDao.save(download);
        return;
      }
      applYm = mrList.get(0).getApplYm();
      mrIdList = new ArrayList<Long>();
      for (MR mr : mrList) {
        mrIdList.add(mr.getId());
      }
    }
    List<IP_T> iptList = iptDao.findByFeeYmOrderById(applYm);
    IP_T ipt = null;
    if (iptList == null || iptList.size() == 0) {
      ipt = xmlService.createIPT(applYm);
    } else {
      ipt = iptList.get(0);
    }
    List<IP_D> ipdList = (mrList == null) ? ipdDao.findByApplYM(applYm) : ipdDao.getIpdListByMrId(mrIdList);
    List<IP_P> ippList = (mrList == null) ? ippDao.findByApplYM(applYm) : ippDao.getIppListByMrIdList(mrIdList);
    
    ip.setTdata(ipt);

    double next = 0.1;
    List<InPatientDData> ddata = new ArrayList<InPatientDData>();
    for (int i = 0; i < ipdList.size(); i++) {
      IP_D ipd = ipdList.get(i);
      InPatientDData ipData = new InPatientDData();
      DHead dhead = new DHead();
      dhead.setCASE_TYPE(ipd.getCaseType());
      dhead.setSEQ_NO(ipd.getSeqNo());
      ipData.setDhead(dhead);

      List<IP_P> pdata = new ArrayList<IP_P>();
      for (int j = ippList.size() - 1; j >= 0; j--) {
        IP_P ipp = ippList.get(j);
        if (ipp.getIpdId().longValue() == ipd.getId().longValue()) {
          pdata.add(ipp);
          ippList.remove(j);
        }
      }
      ipd.setPdataList(pdata);
      ipData.setDbody(ipd);
      ddata.add(ipData);

//      if (((double) i / (double) ipdList.size()) > next) {
//        download.setProgress((int) (next * 100));
//        download.setUpdateAt(new Date());
//        downloadDao.save(download);
//        next += 0.1;
//      }
    }
    ip.setDdata(ddata);
    
    httpServletReq.setAttribute(LogType.EXPORT.name()+"_CNT", ddata.size());
    
    outputFileJAXB(ip, download.getFilename());
    if (ipdList.size() > 0 && ippList.size() > 0) {
      iptDao.save(ipt);
    }
    download.setProgress(100);
    download.setUpdateAt(new Date());
    downloadDao.save(download);
  }

  private void outputFileJAXB(Object obj, String filename) {
    try {
      JacksonXmlModule xmlModule = new JacksonXmlModule();
      xmlModule.setDefaultUseWrapper(false);
      ObjectMapper objectMapper = new XmlMapper(xmlModule);
      objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
      objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
      String xml = objectMapper.writeValueAsString(obj);

      String filepath = (System.getProperty("os.name").toLowerCase().startsWith("windows"))
          ? FILE_PATH + "\\" + filename
          : FILE_PATH + "/" + filename;
      File file = new File(filepath);
      BufferedWriter bw =
          new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "BIG5"));
      bw.write("<?xml version=\"1.0\" encoding=\"Big5\"?>");
      bw.newLine();
      bw.write(xml);
      bw.close();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void importFileThread(File file) {
    Thread thread = new Thread(new Runnable() {

      @Override
      public void run() {
        xmlService.checkAll(0, true);
        long startImport = System.currentTimeMillis();
        importXMLFile(file);
        long usedTime = System.currentTimeMillis() - startImport;
        logger
            .info("importFileThread " + file.getAbsolutePath() + " done, used " + usedTime + "ms.");
        xmlService.checkAll((long) (usedTime), false);
      }
    });
    thread.start();
  }

  public void importXMLFile(File file) {
    ObjectMapper xmlMapper = new XmlMapper();
    try {
      if (readFile(file)) {
        IP ip =
            xmlMapper.readValue(new InputStreamReader(new FileInputStream(file), "BIG5"), IP.class);
        if (ip != null) {
          xmlService.saveIP(ip);
        }
      } else {
        OP op =
            xmlMapper.readValue(new InputStreamReader(new FileInputStream(file), "BIG5"), OP.class);
        if (op != null) { 
          xmlService.saveOPBatch(op);
        }
      }
    } catch (JsonMappingException e) {
      e.printStackTrace();
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private boolean readFile(File file) {
    boolean result = false;
    try {
      BufferedReader br =
          new BufferedReader(new InputStreamReader(new FileInputStream(file), "BIG5"));
      String line = null;
      while ((line = br.readLine()) != null) {
        if (line.indexOf("inpatient") > 0) {
          result = true;
          break;
        }
      }
      br.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }

  private void recalculateAll(HashMap<Integer, Boolean> needProcess) {
    logger.info("needProcess size=" + needProcess.size());
    Thread thread = new Thread(new Runnable() {

      @Override
      public void run() {
        for (Integer intelligentType : needProcess.keySet()) {
          if (intelligentType.intValue() == INTELLIGENT_REASON.VIOLATE.value()) {
            parametersService.switchViolate(needProcess.get(intelligentType));
          } else if (intelligentType.intValue() == INTELLIGENT_REASON.RARE_ICD.value()) {
            parametersService.switchRareICD(needProcess.get(intelligentType));
          } else if (intelligentType.intValue() == INTELLIGENT_REASON.HIGH_RATIO.value()) {
            parametersService.switchHighRatio(needProcess.get(intelligentType));
          } else if (intelligentType.intValue() == INTELLIGENT_REASON.HIGH_RISK.value()) {
            parametersService.switchHighRisk(needProcess.get(intelligentType));
          } else if (intelligentType.intValue() == INTELLIGENT_REASON.INFECTIOUS.value()) {
            parametersService.switchInfections(needProcess.get(intelligentType));
          } else if (intelligentType.intValue() == INTELLIGENT_REASON.INH_OWN_EXIST.value()) {
            parametersService.switchInhOwnExist(needProcess.get(intelligentType));
          } else if (intelligentType.intValue() == INTELLIGENT_REASON.OVER_AMOUNT.value()) {
            parametersService.switchOverAmount(needProcess.get(intelligentType));
          } else if (intelligentType.intValue() == INTELLIGENT_REASON.PILOT_PROJECT.value()) {
            parametersService.switchPilotProject(needProcess.get(intelligentType));
          } else if (intelligentType.intValue() == INTELLIGENT_REASON.SAME_ATC.value()) {
            parametersService.switchSameATC(needProcess.get(intelligentType));
          } else if (intelligentType.intValue() == INTELLIGENT_REASON.COST_DIFF.value()) {
            parametersService.switchCostDiff(needProcess.get(intelligentType));
          } else if (intelligentType.intValue() == INTELLIGENT_REASON.ORDER_DRUG.value()) {
            parametersService.switchOrderDrug(needProcess.get(intelligentType));
          } else if (intelligentType.intValue() == INTELLIGENT_REASON.IP_DAYS.value()) {
            parametersService.switchIpDays(needProcess.get(intelligentType));
          }
        }
        logger.info("recalculateAll done");
      }
    });
    thread.start();
  }

  public void refreshMRFromFolder(File[] files) {
    List<FILE_DOWNLOAD> oldFiles = downloadDao.findAllByUserIdOrderByUpdateAtDesc(0L);
    List<FILE_DOWNLOAD> newFiles = new ArrayList<>();
    ArrayList<File> needProcessFile = new ArrayList<>();
    // System.out.println("oldFiles count:" + oldFiles.size());
    for (File file : files) {
      if (file.getName().indexOf('~') > -1 || !(file.getName().endsWith(".xlsx")
          || file.getName().endsWith(".xml") || file.getName().endsWith(".xls"))) {
        continue;
      }
      boolean isOldFile = false;
      for (FILE_DOWNLOAD oldFile : oldFiles) {
        // System.out.println("folder:" + file.getName() + ", old:" + oldFile.getFilename());
        if (file.getName().equals(oldFile.getFilename())) {
          isOldFile = true;
          break;
        }
      }
      if (!isOldFile) {
        needProcessFile.add(file);
        FILE_DOWNLOAD fd = new FILE_DOWNLOAD();
        fd.setFilename(file.getName());
        fd.setProgress(0);
        fd.setUpdateAt(new Date());
        fd.setUserId(0L);
        fd = downloadDao.save(fd);
        newFiles.add(fd);
      }
    }
    if (newFiles.size() == 0) {
      return;
    }

    ArrayList<File> opdList = new ArrayList<File>();
    ArrayList<File> ipdList = new ArrayList<File>();
    ArrayList<File> oppList = new ArrayList<File>();
    ArrayList<File> ippList = new ArrayList<File>();

    for (File file : needProcessFile) {
      if (processSettingFile(file, newFiles)) {
        continue;
      }
      if (file.getName().endsWith(".xls")) {
        if (file.getName().toUpperCase().startsWith("SOP")) {
          oppList.add(file);
        } else if (file.getName().startsWith("OPD")) {
          opdList.add(file);
        } else if (file.getName().startsWith("OPP")) {
          oppList.add(file);
        } else if (file.getName().startsWith("IPD")) {
          ipdList.add(file);
        } else if (file.getName().startsWith("IPP")) {
          ippList.add(file);
        }
      } else {
        opdList.add(file);
      }
    }

    if ((opdList.size() + oppList.size() + ippList.size() + ipdList.size()) > 0) {
      long startImport = System.currentTimeMillis();
      xmlService.checkAll(0, true);
      // 病歷相關檔案
      int mrFile = 0;
      mrFile += importMRFile(opdList, newFiles);
      mrFile += importMRFile(oppList, newFiles);
      mrFile += importMRFile(ipdList, newFiles);
      mrFile += importMRFile(ippList, newFiles);
      long usedTime = System.currentTimeMillis() - startImport;
      logger.info("import " + mrFile + " files used " + usedTime + " ms.");
      xmlService.checkAll(usedTime, false);
    }
  }

  private int importMRFile(List<File> files, List<FILE_DOWNLOAD> newFiles) {
    int result = 0;
    for (File file : files) {
      if (file.getName().indexOf('~') > -1 || !(file.getName().endsWith(".xlsx")
          || file.getName().endsWith(".xml") || file.getName().endsWith(".xls"))) {
        continue;
      }

      int count = 0;
      long filesize1 = 0;
      long filesize2 = 0;
      do {
        count++;
        filesize1 = file.length();
        try {
          Thread.sleep(5000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        filesize2 = file.length();
      } while ((filesize1 == 0 || filesize1 != filesize2) && count < 1800);

      logger.info("process " + file.getName());
     
      if (file.getName().endsWith(".xlsx")) {
        XSSFWorkbook workbook = null;
        try {
          workbook = new XSSFWorkbook(new FileInputStream(file));
          xmlService.readTheseSheet(workbook.getSheetAt(0));
          result++;
        } catch (FileNotFoundException e) {
          logger.error("importMRFile:" + file.getAbsolutePath(), e);
        } catch (IOException e) {
          logger.error("importMRFile:" + file.getAbsolutePath(), e);
        } catch (Exception e) {
          logger.error("importMRFile:" + file.getAbsolutePath(), e);
        } finally {
          if (workbook != null) {
            try {
              workbook.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
      } else if (file.getName().endsWith(".xml")) {
        importXMLFile(file);
        result++;
      } else if (file.getName().endsWith(".xls")) {
        processLeadtekXLS(file);
        result++;
      }
      updateFileDownloadFinished(file, newFiles);
    }
    return result;
  }

  private void updateFileDownloadFinished(File file, List<FILE_DOWNLOAD> newFiles) {
    for (FILE_DOWNLOAD fd : newFiles) {
      if (file.getName().equals(fd.getFilename())) {
        fd.setProgress(100);
        fd.setUpdateAt(new Date());
        downloadDao.save(fd);
        break;
      }
    }
  }

  public boolean processSettingFile(File file, List<FILE_DOWNLOAD> newFiles) {
    long start = System.currentTimeMillis();
    if (file.getName().indexOf(INIT_FILE_PARAMETERS) == 0) {
      initial.importParametersFromExcel(file, "參數設定", 1);
    } else if (file.getName().indexOf(INIT_FILE_PAY_CODE) > -1) {
      initial.importPayCode(file, INIT_FILE_PAY_CODE, 0);
    } else if (file.getName().indexOf(INIT_FILE_PAY_CODE_LINYUAN) == 0) {
      initial.importPayCode(file, INIT_FILE_PAY_CODE_LINYUAN, 0);
    } else if (file.getName().indexOf(INIT_FILE_PAY_CODE_POHAI) == 0) {
      initial.importPayCode(file, INIT_FILE_PAY_CODE_POHAI, 0);
    } else if (file.getName().startsWith(INIT_FILE_PAY_CODE_NAVY)) {
      initial.importPayCode(file, INIT_FILE_PAY_CODE_NAVY, 0);
    } else if (file.getName().startsWith(INIT_FILE_PAY_CODE_MS)) {
      initial.importPayCode(file, INIT_FILE_PAY_CODE_MS, 0);
    } else if (file.getName().indexOf(INIT_FILE_CODE_TABLE) > -1) {
      initial.importCODE_TABLEToRDB(file, "CODE_TABLE");
    } else if (file.getName().indexOf(INIT_FILE_ICDCM) > 0) {
      initial.importICD10ToRedis(file, "ICD10-CM");
    } else if (file.getName().indexOf(INIT_FILE_ICDPCS) > 0) {
      initial.importICD10ToRedis(file, "ICD10-PCS");
    } else if (file.getName().indexOf(INIT_FILE_ATC) > -1) {
      initial.importATC(file);
    } else if (file.getName().indexOf(INIT_FILE_INFECTIOUS) > -1) {
      initial.importInfectious(file);
    } else if (file.getName().startsWith(INIT_FILE_USER)) {
      initial.importUserFile(file, 0);
    } else if (file.getName().startsWith(INIT_FILE_DEPARTMENT)) {
      initial.importDepartmentFile(file, 0);
    } else if (file.getName().startsWith(INIT_FILE_USER_DEPARTMENT)) {
      initial.importUserDepartmentFile(file, 0);
    } else if (file.getName().startsWith(INIT_FILE_DEDUCTED_COMPUTER)) {
      initial.importDeductedFile(file, false);
    } else if (file.getName().startsWith(INIT_FILE_DEDUCTED_ARTIFICIAL)) {
      initial.importDeductedFile(file, true);
    } else {
      return false;
    }
    updateFileDownloadFinished(file, newFiles);
    long usedTime = System.currentTimeMillis() - start;
    logger.info("import " + file.getName() + " used " + usedTime + " ms.");
    return true;
  }

  public void processLeadtekXLS(File file) {
    System.out.println("processLeadtekXLS " + file.getAbsolutePath());
    xmlService.checkAll(0, true);
    long startImport = System.currentTimeMillis();
    HSSFWorkbook workbook = null;
    try {
      if (file.getName().toUpperCase().indexOf("SOP") == 0) {
        System.out.println("process SOP");
        workbook = new HSSFWorkbook(new FileInputStream(file));
        xmlService.readSOPSheet(workbook.getSheetAt(0));
      } else if (file.getName().toUpperCase().indexOf("OPD") > -1) {
        workbook = new HSSFWorkbook(new FileInputStream(file));
        if (workbook.getSheetAt(0).getRow(0).getPhysicalNumberOfCells() > 10) {
          xmlService.readOpdSheet(workbook.getSheetAt(0));
        }
      } else if (file.getName().toUpperCase().indexOf("IPD") > -1) {
        workbook = new HSSFWorkbook(new FileInputStream(file));
        if (workbook.getSheetAt(0).getRow(0).getPhysicalNumberOfCells() > 10) {
          xmlService.readIpdSheet(workbook.getSheetAt(0));
        }
      } else if (file.getName().toUpperCase().indexOf("OPP") > -1) {
        workbook = new HSSFWorkbook(new FileInputStream(file));
        if (workbook.getSheetAt(0).getRow(0).getPhysicalNumberOfCells() > 10) {
          xmlService.readOppHSSFSheet(workbook.getSheetAt(0));
        }
      } else if (file.getName().toUpperCase().indexOf("IPP") > -1) {
        workbook = new HSSFWorkbook(new FileInputStream(file));
        if (workbook.getSheetAt(0).getRow(0).getPhysicalNumberOfCells() > 10) {
          xmlService.readIppHSSFSheet(workbook.getSheetAt(0));
        }
      }
    } catch (FileNotFoundException e) {
      logger.error("processLeadtekXLS", e);
    } catch (IOException e) {
      logger.error("processLeadtekXLS", e);
    } catch (Exception e) {
      logger.error("processLeadtekXLS", e);
    } finally {
      if (workbook != null) {
        try {
          workbook.close();
        } catch (IOException e) {
          logger.error("processLeadtekXLS", e);
        }
      }
    }
    long usedTime = System.currentTimeMillis() - startImport;
    logger.info("import xls:" + file.getName() + " used " + usedTime + " ms.");
  }
  
  public void deleteInFileDownload(String filename) {
    List<FILE_DOWNLOAD> list = downloadDao.findByFilename(filename);
    if (list == null || list.size() == 0) {
      return;
    }
    for (FILE_DOWNLOAD fd : list) {
      downloadDao.deleteById(fd.getId());
    }    
  }
}
