/**
 * Created on 2021/9/11.
 */
package tw.com.leadtek.nhiwidget.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import tw.com.leadtek.nhiwidget.controller.BaseController;
import tw.com.leadtek.nhiwidget.dao.ATCDao;
import tw.com.leadtek.nhiwidget.dao.CODE_TABLEDao;
import tw.com.leadtek.nhiwidget.dao.DEDUCTEDDao;
import tw.com.leadtek.nhiwidget.dao.ICD10Dao;
import tw.com.leadtek.nhiwidget.dao.PARAMETERSDao;
import tw.com.leadtek.nhiwidget.dao.PAY_CODEDao;
import tw.com.leadtek.nhiwidget.model.rdb.ATC;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_TABLE;
import tw.com.leadtek.nhiwidget.model.rdb.DEDUCTED;
import tw.com.leadtek.nhiwidget.model.rdb.ICD10;
import tw.com.leadtek.nhiwidget.model.rdb.PARAMETERS;
import tw.com.leadtek.nhiwidget.model.rdb.PAY_CODE;
import tw.com.leadtek.nhiwidget.model.redis.CodeBaseLongId;
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
import tw.com.leadtek.tools.Utility;

@Service
public class SystemService {

  private Logger logger = LogManager.getLogger();

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

  public ATCListResponse getATC(String code, String note, int perPage, int page) {
    List<ATC> codes = new ArrayList<ATC>();
    Specification<ATC> spec = new Specification<ATC>() {

      private static final long serialVersionUID = 1L;

      public Predicate toPredicate(Root<ATC> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicate = new ArrayList<Predicate>();
        if (code != null) {
          predicate.add(cb.like(root.get("code"), code.toUpperCase() + "%"));
        }
        if (note != null && note.length() > 0) {
          predicate.add(cb.like(cb.upper(root.get("note")),  "%" + note.toUpperCase() + "%"));
        }
        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));
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
          e.printStackTrace();
        }

        List<Predicate> predicate = new ArrayList<Predicate>();
        if (sd != null) {
          predicate.add(cb.lessThanOrEqualTo(root.get("startDay"), sd));
        }
        if (ed != null) {
          predicate.add(cb.greaterThan(root.get("endDay"), ed));
        }
        if (atc != null) {
          predicate.add(cb.equal(root.get("atc"), atc));
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

  public void savePayCode(PAY_CODE pc, boolean isCreate) {
    CodeBaseLongId cb = new CodeBaseLongId(0, pc.getCode(), null, null);
    cb.setCategory("ORDER");
    if (pc.getInhName() == null) {
      cb.setDesc(pc.getName());
    } else {
      cb.setDesc(pc.getInhName());
    }

    if (isCreate) {
      int id = redisService.getMaxId() + 1;
      cb.setId((long) id);
      pc.setRedisId(id);
      redisService.addIndexToRedisIndex(RedisService.INDEX_KEY, String.valueOf(cb.getId()),
          cb.getCode().toLowerCase());
    } else {
      cb.setId((long) pc.getRedisId());
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
    payCodeDao.save(pc);
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
          predicate.add(cb.equal(root.get("code"), code));
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
          Integer cat = null;
          if ("第一類".equals(infCat)) {
            cat = new Integer(1);
          } else if ("第二類".equals(infCat)) {
            cat = new Integer(2);
          } else if ("第三類".equals(infCat)) {
            cat = new Integer(3);
          } else if ("第四類".equals(infCat)) {
            cat = new Integer(4);
          } else if ("第五類".equals(infCat)) {
            cat = new Integer(5);
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
      cb.setId((long) id);
      icd10.setRedisId((long)id);
      redisService.addIndexToRedisIndex(RedisService.INDEX_KEY, String.valueOf(cb.getId()),
          cb.getCode().toLowerCase());
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
    }
    return null;
  }
  
  public CompareWarningPayload getCompareWarningPayload() {
    return new CompareWarningPayload(parametersDao.findByCatOrderByName("COMPARE_WARNING"));
  }
  
  public String updateCompareWarningPayload(CompareWarningPayload payload) {
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
    }
    return null;
  }
  
  public QuestionMarkPayload getQuestionMarkPayload() {
    return new QuestionMarkPayload(parametersDao.findByCatOrderByName("QUESTION_MARK"));
  }
  
  public String updateQuestionMarkPayload(QuestionMarkPayload payload) {
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
    }
    return null;
  }
  
  public IntelligentConfig getIntelligentConfig() {
    return new IntelligentConfig(parametersDao.findByCatOrderByName("INTELLIGENT_CONFIG"));
  }
  
  public String updateIntelligentConfig(IntelligentConfig payload) {
    List<PARAMETERS> list = parametersDao.findByCatOrderByName("INTELLIGENT_CONFIG");
    for (PARAMETERS p : list) {
      if (p.getName().equals("VIOLATE")) {
        if (payload.getViolate() != null) {
          p.setValue(payload.getViolate().booleanValue() ? "1" : "0");
        }
      } else if (p.getName().equals("RARE_ICD")) {
        if (payload.getRareIcd() != null) {
          p.setValue(payload.getRareIcd().booleanValue() ? "1" : "0");
        }
      } else if (p.getName().equals("HIGH_RATIO")) {
        if (payload.getHighRatio() != null) {
          p.setValue(payload.getHighRatio().booleanValue() ? "1" : "0");
        }
      } else if (p.getName().equals("OVER_AMOUNT")) {
        if (payload.getOverAmount() != null) {
          p.setValue(payload.getOverAmount().booleanValue() ? "1" : "0");
        }
      }  else if (p.getName().equals("INH_OWN_EXIST")) {
        if (payload.getInhOwnExist() != null) {
          p.setValue(payload.getInhOwnExist().booleanValue() ? "1" : "0");
        }
      } else if (p.getName().equals("INFECTIOUS")) {
        if (payload.getInfectious() != null) {
          p.setValue(payload.getInfectious().booleanValue() ? "1" : "0");
        }
      } else if (p.getName().equals("SAME_ATC")) {
        if (payload.getSameAtc() != null) {
          p.setValue(payload.getSameAtc().booleanValue() ? "1" : "0");
        }
      } else if (p.getName().equals("SAME_ATC_LENGTH")) {
        if (payload.getSameAtcLen5() != null) {
          p.setValue(payload.getSameAtcLen5().booleanValue() ? "5" : "7");
        }
      } else if (p.getName().equals("PILOT_PROJECT")) {
        if (payload.getPilotProject() != null) {
          p.setValue(payload.getOverAmount().booleanValue() ? "1" : "0");
        }
      } else if (p.getName().equals("HIGH_RISK")) {
        if (payload.getHighRisk() != null) {
          p.setValue(payload.getHighRisk().booleanValue() ? "1" : "0");
        }
      }
      
      p.setUpdateAt(new Date());
      parametersDao.save(p);
    }
    return null;
  }
  
  public DbManagement getDbManagement() {
    return new DbManagement(parametersDao.findByCatOrderByName("DB_MANAGEMENT"));
  }
  
  public String updateDbManagement(DbManagement payload) {
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
    }
    return null;
  }
}
