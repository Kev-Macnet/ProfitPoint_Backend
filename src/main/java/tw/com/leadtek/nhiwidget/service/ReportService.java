/**
 * Created on 2021/11/3.
 */
package tw.com.leadtek.nhiwidget.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.constant.XMLConstant;
import tw.com.leadtek.nhiwidget.dao.ASSIGNED_POINTDao;
import tw.com.leadtek.nhiwidget.dao.DRG_MONTHLYDao;
import tw.com.leadtek.nhiwidget.dao.DRG_WEEKLYDao;
import tw.com.leadtek.nhiwidget.dao.IP_DDao;
import tw.com.leadtek.nhiwidget.dao.IP_PDao;
import tw.com.leadtek.nhiwidget.dao.IP_TDao;
import tw.com.leadtek.nhiwidget.dao.MRDao;
import tw.com.leadtek.nhiwidget.dao.OP_DDao;
import tw.com.leadtek.nhiwidget.dao.OP_PDao;
import tw.com.leadtek.nhiwidget.dao.OP_TDao;
import tw.com.leadtek.nhiwidget.dao.POINT_MONTHLYDao;
import tw.com.leadtek.nhiwidget.dao.POINT_WEEKLYDao;
import tw.com.leadtek.nhiwidget.model.rdb.ASSIGNED_POINT;
import tw.com.leadtek.nhiwidget.model.rdb.DRG_MONTHLY;
import tw.com.leadtek.nhiwidget.model.rdb.DRG_WEEKLY;
import tw.com.leadtek.nhiwidget.model.rdb.IP_D;
import tw.com.leadtek.nhiwidget.model.rdb.IP_T;
import tw.com.leadtek.nhiwidget.model.rdb.OP_T;
import tw.com.leadtek.nhiwidget.model.rdb.POINT_MONTHLY;
import tw.com.leadtek.nhiwidget.model.rdb.POINT_WEEKLY;
import tw.com.leadtek.nhiwidget.payload.report.AchievementQuarter;
import tw.com.leadtek.nhiwidget.payload.report.AchievementWeekly;
import tw.com.leadtek.nhiwidget.payload.report.DRGMonthlyPayload;
import tw.com.leadtek.nhiwidget.payload.report.DRGMonthlySectionPayload;
import tw.com.leadtek.nhiwidget.payload.report.NameCodePoint;
import tw.com.leadtek.nhiwidget.payload.report.NameCodePointQuantity;
import tw.com.leadtek.nhiwidget.payload.report.NameValueList;
import tw.com.leadtek.nhiwidget.payload.report.NameValueList2;
import tw.com.leadtek.nhiwidget.payload.report.NameValueList3;
import tw.com.leadtek.nhiwidget.payload.report.PeriodPointPayload;
import tw.com.leadtek.nhiwidget.payload.report.PeriodPointWeeklyPayload;
import tw.com.leadtek.nhiwidget.payload.report.PointMRPayload;
import tw.com.leadtek.nhiwidget.payload.report.PointPeriod;
import tw.com.leadtek.nhiwidget.payload.report.PointQuantityList;
import tw.com.leadtek.nhiwidget.payload.report.QuarterData;
import tw.com.leadtek.nhiwidget.payload.report.VisitsPeriod;
import tw.com.leadtek.nhiwidget.payload.report.VisitsPeriodDetail;
import tw.com.leadtek.nhiwidget.payload.report.VisitsVarietyPayload;
import tw.com.leadtek.tools.DateTool;
import tw.com.leadtek.tools.StringUtility;

@Service
public class ReportService {

	private Logger logger = LogManager.getLogger();

	/**
	 * 全部科別的科別代碼
	 */
	public static final String FUNC_TYPE_ALL_NAME = "不分科";

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
	private OP_PDao oppDao;

	@Autowired
	private MRDao mrDao;

	@Autowired
	private ASSIGNED_POINTDao assignedPointDao;

	@Autowired
	private POINT_MONTHLYDao pointMonthlyDao;

	@Autowired
	private CodeTableService codeTableService;

	@Autowired
	private POINT_WEEKLYDao pointWeeklyDao;

	@Autowired
	private DRG_MONTHLYDao drgMonthlyDao;

	@Autowired
	private DRG_WEEKLYDao drgWeeklyDao;

	@Autowired
	private ParametersService parametersService;

	public final static String FILE_PATH = "download";

	public PointMRPayload getMonthlyReport(int year, int month) {
		int lastM = year * 100 + month - 1;
		if (month - 1 <= 0) {
			lastM = (year - 1) * 100 + 12;
		}
		PointMRPayload result = new PointMRPayload();
		result.setCurrent(pointMonthlyDao.findByYm(year * 100 + month));
		result.setLastM(pointMonthlyDao.findByYm(lastM));
		result.setLastY(pointMonthlyDao.findByYm((year - 1) * 100 + month));
		if (result.getCurrent() != null) {
			result.calculateDifference();
		}

		return result;
	}

	/**
	 * 取得各年月的 IP_T/OP_T id
	 * 
	 * @param isOP
	 * @return
	 */
	public HashMap<String, Long> getYMTID(boolean isOP) {
		HashMap<String, Long> result = new HashMap<String, Long>();
		if (isOP) {
			List<OP_T> list = optDao.findAll();
			for (OP_T op_T : list) {
				result.put(op_T.getFeeYm(), op_T.getId());
			}
		} else {
			List<IP_T> list = iptDao.findAll();
			for (IP_T ip_T : list) {
				result.put(ip_T.getFeeYm(), ip_T.getId());
			}
		}

		return result;
	}

	/**
	 * 計算指定年月的單月健保點數總表
	 * 
	 * @param ym
	 */
	public void calculatePointMR(String ym) {
		if ("ALL".equals(ym.toUpperCase())) {
			calculatePointMRAll();
			return;
		}
		String chineseYM = ymToROCYM(ym);
		String adYM = ymToADYM(ym);
		POINT_MONTHLY pm = null;
		POINT_MONTHLY old = pointMonthlyDao.findByYm(Integer.parseInt(adYM));
		if (old == null) {
			pm = new POINT_MONTHLY();
		} else {
			pm = old;
		}
		pm.setYm(Integer.parseInt(adYM));

		long optId = 0;
		long iptId = 0;
		List<OP_T> listOPT = optDao.findByFeeYmOrderById(chineseYM);
		if (listOPT != null && listOPT.size() > 0) {
			optId = listOPT.get(0).getId();
		} else {
			return;
		}

		List<IP_T> listIPT = iptDao.findByFeeYmOrderById(chineseYM);
		if (listIPT != null && listIPT.size() > 0) {
			iptId = listIPT.get(0).getId();
		} else {
			return;
		}

		List<Object[]> list = opdDao.findMonthlyPoint(chineseYM);
		if (list != null && list.size() > 0) {
			Object[] obj = list.get(0);
			pm.setPartOp(getLongValue(obj[0]));
			pm.setPartEm(getLongValue(obj[1]));
			pm.setPartOpAll(pm.getPartOp() + pm.getPartEm());
			pm.setPartIp(getLongValue(obj[2]));
			pm.setPartAll(pm.getPartOpAll() + pm.getPartIp());

			pm.setApplOp(getLongValue(obj[3]));
			pm.setApplEm(getLongValue(obj[4]));
			pm.setApplOpAll(pm.getApplOp() + pm.getApplEm());
			pm.setApplIp(getLongValue(obj[5]));
			pm.setApplAll(pm.getApplOpAll() + pm.getApplIp());

			pm.setTotalOp(pm.getPartOp() + pm.getApplOp());
			pm.setTotalEm(pm.getPartEm() + pm.getApplEm());
			pm.setTotalOpAll(pm.getTotalOp() + pm.getTotalEm());
			pm.setTotalIp(pm.getPartIp() + pm.getApplIp());
			pm.setTotalAll(pm.getTotalOpAll() + pm.getTotalIp());

			pm.setPatientOp(((BigInteger) obj[6]).longValue());
			pm.setPatientEm(((BigInteger) obj[7]).longValue());
			pm.setPatientIp(((BigInteger) obj[8]).longValue());
			pm.setChronic(getLongValue(obj[9]));

			pm.setIpQuantity(((BigInteger) obj[10]).longValue());
			pm.setDrgQuantity(((BigInteger) obj[11]).longValue());
			pm.setDrgApplPoint(getLongValue(obj[12]));
			pm.setDrgActualPoint(getLongValue(obj[13]));
			pm.setNoApplIp(getLongValue(obj[14]));
			pm.setNoApplOp(getLongValue(obj[15]));
			pm.setNoApplAll(pm.getNoApplIp() + pm.getNoApplOp());
			pm.setUpdateAt(new Date());

			updateAssignedPoint(pm, adYM, null);
		}
		pointMonthlyDao.save(pm);
	}

	/**
	 * 計算指定年月的單月健保點數總表
	 * 
	 * @param ym
	 */
	public void calculatePointMRAll() {
		List<Map<String, Object>> list = mrDao.getAllApplYm();
		for (Map<String, Object> map : list) {
			String applYm = (String) map.get("APPL_YM");
			calculatePointMR(applYm);
		}
	}

	/**
	 * 取得最舊一筆的 POINT_MONTHLY 資料
	 * 
	 * @return yyyyMM
	 */
	public Integer getMinPointMonthly() {
		return pointMonthlyDao.getMinYm();
	}

	/**
	 * 取得最新一筆的 POINT_MONTHLY 資料
	 * 
	 * @return yyyyMM
	 */
	public Integer getMaxPointMonthly() {
		return pointMonthlyDao.getMaxYm();
	}

	public boolean refreshPointMonthly(String adYM, ASSIGNED_POINT ap) {
		POINT_MONTHLY pm = pointMonthlyDao.findByYm(Integer.parseInt(adYM));
		if (pm == null) {
			return false;
		}
		updateAssignedPoint(pm, adYM, ap);
		return true;
	}

	public void updateAssignedPoint(POINT_MONTHLY pm, String adYM, ASSIGNED_POINT ap) {
		if (ap == null) {
			ap = getAssignedPoint(adYM);
		}
		if (ap == null) {
			pm.setRemaining(0L);
			return;
		}

		pm.setAssignedOpAll(ap.getWmOpPoints());
		pm.setAssignedIp(ap.getWmIpPoints());
		pm.setAssignedAll(ap.getWmp());

		pm.setRateAll(cutPointNumber(((double) pm.getTotalAll() * (double) 100) / (double) pm.getAssignedAll()));
		pm.setRateOpAll(cutPointNumber(((double) pm.getTotalOpAll() * (double) 100) / (double) pm.getAssignedOpAll()));
		pm.setRateIp(cutPointNumber(((double) pm.getTotalIp() * (double) 100) / (double) pm.getAssignedIp()));
		pm.setRemaining(pm.getAssignedAll().longValue() - pm.getApplAll().longValue() - pm.getPartAll());
		pointMonthlyDao.save(pm);
	}

	/**
	 * 將double只取小數點後一位
	 * 
	 * @param d
	 * @return
	 */
	public static Double cutPointNumber(double d) {
		String s = String.valueOf(d);
		int index = s.indexOf('.');
		if (index > 0 && s.length() - index > 2) {
			return Double.parseDouble(s.substring(0, index + 2));
		}
		return d;
	}

	public ASSIGNED_POINT getAssignedPoint(String adYM) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		Date date = null;
		try {
			date = sdf.parse(adYM);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		List<ASSIGNED_POINT> list = assignedPointDao.findAllByStartDateLessThanEqualAndEndDateGreaterThanEqual(date,
				date);
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			list = assignedPointDao.findAllByOrderByEndDateDesc();
			if (list != null && list.size() > 0) {
				return list.get(0);
			}
		}
		return null;
	}

	/**
	 * 西元年月轉民國年月
	 * 
	 * @param ym
	 * @return
	 */
	private String ymToROCYM(String ym) {
		if (ym == null || ym.length() != 6 || !ym.startsWith("20")) {
			return ym;
		}
		return DateTool.convertToChineseYear(ym);
	}

	/**
	 * 民國年月轉西元年月
	 * 
	 * @param ym
	 * @return
	 */
	private String ymToADYM(String ym) {
		if (ym == null || ym.length() != 5 || !ym.startsWith("1")) {
			return ym;
		}
		return DateTool.convertChineseToAD(ym);
	}

	public PeriodPointPayload getPeriodPoint(Date sdate, Date edate) {
		PeriodPointPayload result = new PeriodPointPayload();
		java.sql.Date s = new java.sql.Date(sdate.getTime());
		java.sql.Date e = new java.sql.Date(edate.getTime());
		List<Object[]> list = opdDao.findPeriodPoint(s, e);
		if (list != null && list.size() > 0) {
			Object[] obj = list.get(0);
			// 案件數
			result.setQuantityAll(((BigInteger) obj[0]).longValue());
			result.setQuantityOpAll(((BigInteger) obj[1]).longValue());
			result.setQuantityOp(((BigInteger) obj[2]).longValue());
			result.setQuantityEm(((BigInteger) obj[3]).longValue());
			result.setQuantityIp(((BigInteger) obj[4]).longValue());

			// 申請總點數
			result.setApplNoPartPointOpAll(getLongValue(obj[5]));
			result.setApplNoPartPointOp(getLongValue(obj[6]));
			result.setApplNoPartPointEm(getLongValue(obj[7]));
			result.setApplNoPartPointIp(getLongValue(obj[8]));
			result.setApplNoPartPointAll(result.getApplNoPartPointOpAll() + result.getApplNoPartPointIp());

			// 部分負擔
			result.setPartPointOpAll(getLongValue(obj[9]));
			result.setPartPointOp(getLongValue(obj[10]));
			result.setPartPointEm(getLongValue(obj[11]));
			result.setPartPointIp(getLongValue(obj[12]));
			result.setPartPointAll(result.getPartPointOpAll() + result.getPartPointIp());
			// 自費
			result.setOwnExpOpAll(getLongValue(obj[13]));
			result.setOwnExpOp(getLongValue(obj[14]));
			result.setOwnExpEm(getLongValue(obj[15]));
			result.setOwnExpIp(getLongValue(obj[16]));
			result.setOwnExpAll(result.getOwnExpOpAll() + result.getOwnExpIp());
			// 不申報
			result.setNoApplOpAll(getLongValue(obj[17]));
			result.setNoApplOp(getLongValue(obj[18]));
			result.setNoApplEm(getLongValue(obj[19]));
			result.setNoApplIp(getLongValue(obj[20]));
			result.setNoApplAll(result.getNoApplOpAll() + result.getNoApplIp());

			// 累積申報總點數
			result.setApplPointOp(result.getApplNoPartPointOp() + result.getPartPointOp());
			result.setApplPointEm(result.getApplNoPartPointEm() + result.getPartPointEm());
			result.setApplPointIp(result.getApplNoPartPointIp() + result.getPartPointIp());
			result.setApplPointOpAll(result.getApplPointOp() + result.getApplPointEm());
			result.setApplPointAll(result.getApplPointOpAll() + result.getApplPointIp());

			// 原始總點數
			result.setPointAll(result.getApplPointAll() + result.getOwnExpAll());
			result.setPointEm(result.getApplPointEm() + result.getOwnExpEm());
			// 住院: 醫療費用+不計入醫療費用點數合計+自費
			result.setPointIp(getLongValue(obj[21]) + result.getNoApplIp() + result.getOwnExpIp());
			result.setPointOp(result.getApplPointOp() + result.getOwnExpOp());
			result.setPointOpAll(result.getPointOp() + result.getPointEm());
		}

		result.setApplByFuncType(getApplPointGroupByFuncType(s, e));
		result.setPartByFuncType(getPartPointGroupByFuncType(s, e));
		result.setPayByOrderType(getPointGroupByOrderType(s, e, ""));
		result.setOwnExpByFuncType(getOwnExpenseGroupByFuncType(s, e));
		result.setOwnExpByOrderType(getOwnExpenseGroupByOrderType(s, e, ""));
//		result.setPayByOrderTypeList(getPointGroupByOrderTypeList(s, e));
//		result.setOwnExpByOrderTypeList(getOwnExpenseGroupByOrderTypeList(s, e));
		return result;
	}
	
	public PeriodPointPayload getPeriodPointByFunctype(Date sdate, Date edate, String funcType) {
		PeriodPointPayload result = new PeriodPointPayload();
		java.sql.Date s = new java.sql.Date(sdate.getTime());
		java.sql.Date e = new java.sql.Date(edate.getTime());


		result.setPayByOrderType(getPointGroupByOrderType(s, e, funcType));
		result.setOwnExpByOrderType(getOwnExpenseGroupByOrderType(s, e, funcType));
		return result;
	}

	public PointQuantityList getApplPointGroupByFuncType(java.sql.Date s, java.sql.Date e) {
		PointQuantityList result = new PointQuantityList();
		// 門急診各科申報總數
		List<Object[]> list = opdDao.findApplPointGroupByFuncType(s, e);
		if (list != null && list.size() > 0) {
			for (Object[] objects : list) {
				NameCodePointQuantity npq = new NameCodePointQuantity();
				npq.setName(codeTableService.getDesc("FUNC_TYPE", (String) objects[0]));
				npq.setCode((String) objects[0]);
				if (objects[1] == null) {
					npq.setPoint(0L);
				} else if (objects[1] instanceof BigDecimal) {
					BigDecimal dot = (BigDecimal) objects[1];
					npq.setPoint(dot.longValue());
				} else {
					npq.setPoint(((long) (int) objects[1]));
				}
				npq.setQuantity(((BigInteger) objects[2]).longValue());
				result.addOp(npq);
			}
		}
		list = ipdDao.findApplPointGroupByFuncType(s, e);
		if (list != null && list.size() > 0) {
			for (Object[] objects : list) {
				NameCodePointQuantity npq = new NameCodePointQuantity();
				npq.setName(codeTableService.getDesc("FUNC_TYPE", (String) objects[0]));
				npq.setCode((String) objects[0]);
				if (objects[1] == null) {
					npq.setPoint(0L);
				} else if (objects[1] instanceof BigDecimal) {
					BigDecimal dot = (BigDecimal) objects[1];
					npq.setPoint(dot.longValue());
				} else {
					npq.setPoint(((long) (int) objects[1]));
				}
				npq.setQuantity(((BigInteger) objects[2]).longValue());
				result.addIp(npq);
			}
		}
		return result;
	}

	public PointQuantityList getPartPointGroupByFuncType(java.sql.Date s, java.sql.Date e) {
		PointQuantityList result = new PointQuantityList();
		// 門急診各科部份負擔總數
		List<Object[]> list = opdDao.findPartPointGroupByFuncType(s, e);
		if (list != null && list.size() > 0) {
			for (Object[] objects : list) {
				NameCodePointQuantity npq = new NameCodePointQuantity();
				npq.setName(codeTableService.getDesc("FUNC_TYPE", (String) objects[0]));
				npq.setCode((String) objects[0]);
				if (objects[1] == null) {
					npq.setPoint(0L);
				} else if (objects[1] instanceof BigDecimal) {
					BigDecimal dot = (BigDecimal) objects[1];
					npq.setPoint(dot.longValue());
				} else {
					npq.setPoint(((long) (int) objects[1]));
				}
				npq.setQuantity(((BigInteger) objects[2]).longValue());
				result.addOp(npq);
			}
		}
		list = ipdDao.findPartPointGroupByFuncType(s, e);
		if (list != null && list.size() > 0) {
			for (Object[] objects : list) {
				NameCodePointQuantity npq = new NameCodePointQuantity();
				npq.setName(codeTableService.getDesc("FUNC_TYPE", (String) objects[0]));
				npq.setCode((String) objects[0]);
				if (objects[1] == null) {
					npq.setPoint(0L);
				} else if (objects[1] instanceof BigDecimal) {
					BigDecimal dot = (BigDecimal) objects[1];
					npq.setPoint(dot.longValue());
				} else {
					npq.setPoint(((long) (int) objects[1]));
				}
				npq.setQuantity(((BigInteger) objects[2]).longValue());
				result.addIp(npq);
			}
		}
		return result;
	}

	public PointQuantityList getOwnExpenseGroupByFuncType(java.sql.Date s, java.sql.Date e) {
		PointQuantityList result = new PointQuantityList();
		// 門急診各科自費總數
		List<Object[]> list = opdDao.findOwnExpenseGroupByFuncType(s, e);
		if (list != null && list.size() > 0) {
			for (Object[] objects : list) {
				NameCodePointQuantity npq = new NameCodePointQuantity();
				npq.setName(codeTableService.getDesc("FUNC_TYPE", (String) objects[0]));
				npq.setCode((String) objects[0]);
				if (objects[1] == null) {
					npq.setPoint(0L);
				} else if (objects[1] instanceof BigDecimal) {
					BigDecimal dot = (BigDecimal) objects[1];
					npq.setPoint(dot.longValue());
				} else {
					npq.setPoint(((long) (int) objects[1]));
				}
				npq.setQuantity(((BigInteger) objects[2]).longValue());
				result.addOp(npq);
			}
		}
		list = ipdDao.findOwnExpenseGroupByFuncType(s, e);
		if (list != null && list.size() > 0) {
			for (Object[] objects : list) {
				NameCodePointQuantity npq = new NameCodePointQuantity();
				npq.setName(codeTableService.getDesc("FUNC_TYPE", (String) objects[0]));
				npq.setCode((String) objects[0]);
				if (objects[1] == null) {
					npq.setPoint(0L);
				} else if (objects[1] instanceof BigDecimal) {
					BigDecimal dot = (BigDecimal) objects[1];
					npq.setPoint(dot.longValue());
				} else {
					npq.setPoint(((long) (int) objects[1]));
				}
				npq.setQuantity(((BigInteger) objects[2]).longValue());
				result.addIp(npq);
			}
		}
		return result;
	}

	public PointQuantityList getPointGroupByOrderType(java.sql.Date s, java.sql.Date e, String funcType) {
		PointQuantityList result = new PointQuantityList();
		// 門急診各科申報總數
		List<Object[]> list = new ArrayList<Object[]>();
		if(!funcType.isEmpty() && funcType.equals("00")) {
			funcType = "";
		}
		if(funcType.isEmpty()) {
			list = oppDao.findPointGroupByPayCodeType(s, e);
		}
		else {
			
			list = oppDao.findPointAndFuncTypeGroupByPayCodeType(s, e, funcType);
		}
		if (list != null && list.size() > 0) {
			for (Object[] objects : list) {
				NameCodePointQuantity npq = new NameCodePointQuantity();
				String name = codeTableService.getDesc("PAY_CODE_TYPE", (String) objects[0]);
				if (name == null || name.length() < 1) {
					name = "無";
				}
				npq.setName(name);
				npq.setCode((String) objects[0]);
				if (objects[1] == null) {
					npq.setPoint(0L);
				} else if (objects[1] instanceof BigDecimal) {
					BigDecimal dot = (BigDecimal) objects[1];
					npq.setPoint(dot.longValue());
				} else {
					npq.setPoint(((long) (int) objects[1]));
				}
				npq.setQuantity(((BigInteger) objects[2]).longValue());
				result.addOp(npq);
			}
		}
		if(funcType.isEmpty()) {
			list = ippDao.findPointGroupByPayCodeType(s, e);
		}
		else {
			
			list = ippDao.findPointAndFuncTypeGroupByPayCodeType(s, e, funcType);
		}
		if (list != null && list.size() > 0) {
			for (Object[] objects : list) {
				NameCodePointQuantity npq = new NameCodePointQuantity();
				String name = codeTableService.getDesc("PAY_CODE_TYPE", (String) objects[0]);
				if (name == null || name.length() < 1) {
					name = "無";
				}
				npq.setName(name);
				npq.setCode((String) objects[0]);
				if (objects[1] == null) {
					npq.setPoint(0L);
				} else if (objects[1] instanceof BigDecimal) {
					BigDecimal dot = (BigDecimal) objects[1];
					npq.setPoint(dot.longValue());
				} else {
					npq.setPoint(((long) (int) objects[1]));
				}
				npq.setQuantity(((BigInteger) objects[2]).longValue());
				result.addIp(npq);
			}
		}
		return result;
	}

	public PointQuantityList getOwnExpenseGroupByOrderType(java.sql.Date s, java.sql.Date e, String funcType) {
		PointQuantityList result = new PointQuantityList();
		List<Object[]> list = new ArrayList<Object[]>();
		if(!funcType.isEmpty() && funcType.equals("00")) {
			funcType = "";
		}
		// 門急診各科申報總數
		if(funcType.isEmpty()) {
			list = oppDao.findOwnExpensePointGroupByPayCodeType(s, e);
		}
		else {
			list = oppDao.findOwnExpensePointAndFuncTypeGroupByPayCodeType(s, e, funcType);
		}
		if (list != null && list.size() > 0) {
			for (Object[] objects : list) {
				NameCodePointQuantity npq = new NameCodePointQuantity();
				String name = codeTableService.getDesc("PAY_CODE_TYPE", (String) objects[0]);
				if (name == null || name.length() < 1) {
					name = "無";
				}
				npq.setName(name);
				npq.setCode((String) objects[0]);
				if (objects[1] == null) {
					npq.setPoint(0L);
				} else if (objects[1] instanceof BigDecimal) {
					BigDecimal dot = (BigDecimal) objects[1];
					npq.setPoint(dot.longValue());
				} else {
					npq.setPoint(((long) (int) objects[1]));
				}
				npq.setQuantity(((BigInteger) objects[2]).longValue());
				result.addOp(npq);
			}
		}
		if(funcType.isEmpty()) {
			
			list = ippDao.findOwnExpenseGroupByPayCodeType(s, e);
		}
		else {
			list = ippDao.findOwnExpenseAndFuncTypeGroupByPayCodeType(s, e, funcType);

		}
		if (list != null && list.size() > 0) {
			for (Object[] objects : list) {
				NameCodePointQuantity npq = new NameCodePointQuantity();
				String name = codeTableService.getDesc("PAY_CODE_TYPE", (String) objects[0]);
				if (name == null || name.length() < 1) {
					name = "無";
				}
				npq.setName(name);
				npq.setCode((String) objects[0]);
				if (objects[1] == null) {
					npq.setPoint(0L);
				} else if (objects[1] instanceof BigDecimal) {
					BigDecimal dot = (BigDecimal) objects[1];
					npq.setPoint(dot.longValue());
				} else {
					npq.setPoint(((long) (int) objects[1]));
				}
				npq.setQuantity(((BigInteger) objects[2]).longValue());
				result.addIp(npq);
			}
		}
		return result;
	}
	

	public POINT_WEEKLY calculatePointByWeek(Date sdate, Date edate, List<String> funcTypes) {
		if (!checkWeekday(sdate, Calendar.SUNDAY) || !checkWeekday(edate, Calendar.SATURDAY)) {
			logger.error("calculatePointByWeek failed");
			return null;
		}

		java.sql.Date s = new java.sql.Date(sdate.getTime());
		java.sql.Date e = new java.sql.Date(edate.getTime());
		POINT_WEEKLY pw = calculatePointByWeek(s, e, XMLConstant.FUNC_TYPE_ALL);
		if (pw == null) {
			return null;
		}
		for (String string : funcTypes) {
			calculatePointByWeek(s, e, string);
		}
		return pw;

	}

	private POINT_WEEKLY calculatePointByWeek(Date sdate, Date edate, String funcType) {
		if (!checkWeekday(sdate, Calendar.SUNDAY) || !checkWeekday(edate, Calendar.SATURDAY)) {
			logger.error("calculatePointByWeek failed");
			return null;
		}

		java.sql.Date s = new java.sql.Date(sdate.getTime());
		java.sql.Date e = new java.sql.Date(edate.getTime());
		POINT_WEEKLY pw = pointWeeklyDao.findByStartDateAndEndDateAndFuncType(s, e, funcType);
		if (pw == null) {
			pw = new POINT_WEEKLY();
			pw.setFuncType(funcType);
			pw.setStartDate(sdate);
			pw.setEndDate(edate);
			Calendar cal = Calendar.getInstance();
			cal.setTime(edate);
			pw.setPyear(cal.get(Calendar.YEAR));
			int week = cal.get(Calendar.WEEK_OF_YEAR);
			// if (isFirstDaySunday(pw.getPyear())) {
			// // 若1/1不是週日，則透過 Calendar.WEEK_OF_YEAR抓出來的週數都要減1，因1/1的值被算在上一年的最後一週
			// week--;
			// }
			pw.setPweek(week);
		}
		List<Object[]> list = null;
		if (XMLConstant.FUNC_TYPE_ALL.equals(funcType)) {
			list = opdDao.findAllPoint(s, e);
		} else {
			list = opdDao.findAllPointByFuncType(s, e, funcType);
		}

		if (list != null && list.size() > 0) {
			Object[] object = list.get(0);
			pw.setOp(getLongValue(object[0]));
			pw.setIp(getLongValue(object[1]));
			pw.setEm(getLongValue(object[2]));
			pw.setOwnExpOp(getLongValue(object[3]));
			pw.setOwnExpIp(getLongValue(object[4]));
			pw.setVisitsOp(getLongValue(object[5]));
			pw.setVisitsIp(getLongValue(object[6]));
			pw.setVisitsLeave(getLongValue(object[7]));
		}
		pw.setUpdateAt(new Date());
		return pointWeeklyDao.save(pw);
	}

	public void calculatePointWeekly(Calendar startCal) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, startCal.get(Calendar.YEAR));
		cal.set(Calendar.MONTH, startCal.get(Calendar.MONTH));
		cal.set(Calendar.DAY_OF_YEAR, startCal.get(Calendar.DAY_OF_YEAR));

		if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
			cal.add(Calendar.DAY_OF_YEAR, Calendar.SUNDAY - cal.get(Calendar.DAY_OF_WEEK));
		}
		Calendar calMax = parametersService.getMinMaxCalendar(new Date(), false);
		List<Object[]> list = mrDao.findDRGAllFuncType();
		List<String> funcTypesDRG = new ArrayList<String>();
		for (Object[] obj : list) {
			funcTypesDRG.add((String) obj[0]);
		}
		// funcTypes.add(0, ReportService.FUNC_TYPE_ALL);
		List<String> funcTypes = findAllFuncTypes(false);
		do {
			Date start = cal.getTime();
			cal.add(Calendar.DAY_OF_YEAR, 6);
			Date end = cal.getTime();

			calculatePointByWeek(start, end, funcTypes);
			calculateDRGPointByWeek(start, end, funcTypesDRG);
			cal.add(Calendar.DAY_OF_YEAR, 1);
		} while (cal.before(calMax));
		logger.info("calculatePointWeekly done");
	}

	private List<String> findAllFuncTypes(boolean includeAll) {
		List<String> result = new ArrayList<String>();
		List<Object[]> list = mrDao.findAllFuncType();
		for (Object[] objects : list) {
			result.add((String) objects[0]);
		}
		if (includeAll) {
			result.add(XMLConstant.FUNC_TYPE_ALL);
		}
		return result;
	}

	private List<String> findAllFuncTypesName(boolean includeAll) {
		List<String> result = new ArrayList<String>();
		List<String> funcCodes = findAllFuncTypes(includeAll);
		for (String funcCode : funcCodes) {
			result.add(codeTableService.getDesc("FUNC_TYPE", funcCode));
		}
		return result;
	}

	/**
	 * 取得DB所有科別代碼及科別中文名稱
	 * 
	 * @param includeAll
	 * @return
	 */
	private Map<String, String> findAllFuncTypesMap(boolean includeAll) {
		Map<String, String> result = new HashMap<String, String>();
		List<String> funcCodes = findAllFuncTypes(includeAll);
		for (String funcCode : funcCodes) {
			result.put(funcCode, codeTableService.getDesc("FUNC_TYPE", funcCode));
		}
		return result;
	}

	public void calculateDRGPointByWeek(Date sdate, Date edate, List<String> funcTypes) {
		if (!checkWeekday(sdate, Calendar.SUNDAY) || !checkWeekday(edate, Calendar.SATURDAY)) {
			logger.error("calculatePointByWeek failed");
			return;
		}

		java.sql.Date s = new java.sql.Date(sdate.getTime());
		java.sql.Date e = new java.sql.Date(edate.getTime());

		HashMap<String, String> elapseFuncType = new HashMap<String, String>();
		for (String string : funcTypes) {
			elapseFuncType.put(string, "");
		}

		DRG_WEEKLY drgWeeklyAll = selectOrCreateDrgWeekly(s, e, XMLConstant.FUNC_TYPE_ALL);
		List<Object[]> list = mrDao.countDRGPointByStartDateAndEndDate(s, e, s, e, s, e);
		for (Object[] obj : list) {
			String funcType = (String) obj[0];
			elapseFuncType.remove(funcType);
			DRG_WEEKLY drgWeekly = selectOrCreateDrgWeekly(s, e, funcType);
			drgWeekly.setDrgQuantity(((BigInteger) obj[1]).longValue());
			drgWeekly.setDrgPoint(getLongValue(obj[2]));
			drgWeekly.setNondrgQuantity(((BigInteger) obj[4]).longValue());
			drgWeekly.setNondrgPoint(getLongValue(obj[5]));
			drgWeeklyAll.setDrgQuantity(drgWeeklyAll.getDrgQuantity() + drgWeekly.getDrgQuantity());
			drgWeeklyAll.setDrgPoint(drgWeeklyAll.getDrgPoint() + drgWeekly.getDrgPoint());
			drgWeeklyAll.setNondrgQuantity(drgWeeklyAll.getNondrgQuantity() + drgWeekly.getNondrgQuantity());
			drgWeeklyAll.setNondrgPoint(drgWeeklyAll.getNondrgPoint() + drgWeekly.getNondrgPoint());

			List<Object[]> sectionList = mrDao.countDRGPointByFuncTypeGroupByDRGSection(s, e, funcType);
			for (Object[] obj2 : sectionList) {
				long point = (obj2[2] instanceof Integer) ? ((Integer) obj2[2]).longValue()
						: ((BigInteger) obj2[2]).longValue();
				if ("A".equals((String) obj2[0])) {
					drgWeekly.setSectionA(((BigInteger) obj2[1]).longValue());
					drgWeeklyAll.setSectionA(drgWeeklyAll.getSectionA() + drgWeekly.getSectionA());
					drgWeekly.setPointA(point);
					drgWeeklyAll.setPointA(drgWeeklyAll.getPointA() + drgWeekly.getPointA());
				} else if ("B1".equals((String) obj2[0])) {
					drgWeekly.setSectionB1(((BigInteger) obj2[1]).longValue());
					drgWeeklyAll.setSectionB1(drgWeeklyAll.getSectionB1() + drgWeekly.getSectionB1());
					drgWeekly.setPointB1(point);
					drgWeeklyAll.setPointB1(drgWeeklyAll.getPointB1() + drgWeekly.getPointB1());
				} else if ("B2".equals((String) obj2[0])) {
					drgWeekly.setSectionB2(((BigInteger) obj2[1]).longValue());
					drgWeeklyAll.setSectionB2(drgWeeklyAll.getSectionB2() + drgWeekly.getSectionB2());
					drgWeekly.setPointB2(point);
					drgWeeklyAll.setPointB2(drgWeeklyAll.getPointB2() + drgWeekly.getPointB2());
				} else if ("C".equals((String) obj2[0])) {
					drgWeekly.setSectionC(((BigInteger) obj2[1]).longValue());
					drgWeeklyAll.setSectionC(drgWeeklyAll.getSectionC() + drgWeekly.getSectionC());
					drgWeekly.setPointC(point);
					drgWeeklyAll.setPointC(drgWeeklyAll.getPointC() + drgWeekly.getPointC());
				}
			}
			drgWeeklyDao.save(drgWeekly);
		}
		drgWeeklyDao.save(drgWeeklyAll);
		processElapseFuncTypeWeekly(s, e, elapseFuncType.keySet());
	}

	private void processElapseFuncTypeWeekly(java.sql.Date startDate, java.sql.Date endDate,
			Set<String> elapseFuncTypes) {
		List<Object[]> list = mrDao.countNonDRGPointByStartDateAndEndDate(startDate, endDate);
		for (Object[] obj : list) {
			String funcType = (String) obj[0];
			if (!elapseFuncTypes.contains(funcType)) {
				continue;
			}
			DRG_WEEKLY drgWeekly = selectOrCreateDrgWeekly(startDate, endDate, funcType);
			drgWeekly.setDrgQuantity(0L);
			drgWeekly.setDrgPoint(0L);
			drgWeekly.setNondrgQuantity(((BigInteger) obj[1]).longValue());
			drgWeekly.setNondrgPoint(getLongValue(obj[2]));
			drgWeekly.setSectionA(0L);
			drgWeekly.setSectionB1(0L);
			drgWeekly.setSectionB2(0L);
			drgWeekly.setSectionC(0L);
			drgWeeklyDao.save(drgWeekly);
		}
	}

	private DRG_WEEKLY selectOrCreateDrgWeekly(java.sql.Date startDate, java.sql.Date endDate, String funcType) {
		DRG_WEEKLY result = drgWeeklyDao.findByFuncTypeAndStartDateAndEndDate(funcType, startDate, endDate);
		if (result == null) {
			result = new DRG_WEEKLY();
			result.setFuncType(funcType);
			result.setStartDate(startDate);
			result.setEndDate(endDate);
			Calendar cal = Calendar.getInstance();
			cal.setTime(endDate);
			result.setPyear(cal.get(Calendar.YEAR));
			int week = cal.get(Calendar.WEEK_OF_YEAR);
			result.setPweek(week);
		}
		if (XMLConstant.FUNC_TYPE_ALL.equals(funcType)) {
			result.setDrgQuantity(0L);
			result.setDrgPoint(0L);
			result.setNondrgQuantity(0L);
			result.setNondrgPoint(0L);
			result.setSectionA(0L);
			result.setSectionB1(0L);
			result.setSectionB2(0L);
			result.setSectionC(0L);
			result.setPointA(0L);
			result.setPointB1(0L);
			result.setPointB2(0L);
			result.setPointC(0L);
		}
		return result;
	}

	private boolean checkWeekday(Date date, int weekday) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_WEEK) == weekday;
	}

	public static boolean isFirstDaySunday(int year) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		return cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
	}

	public PeriodPointWeeklyPayload getPeroidPointWeekly(Date edate) {
		PeriodPointWeeklyPayload result = new PeriodPointWeeklyPayload();
		java.sql.Date e = new java.sql.Date(edate.getTime());
		List<POINT_WEEKLY> list = pointWeeklyDao.findByEndDateLessThanEqualAndFuncTypeOrderByEndDateDesc(e,
				XMLConstant.FUNC_TYPE_ALL);
		int count = 0;
		for (POINT_WEEKLY pw : list) {
			String name = pw.getPyear() + " w" + pw.getPweek();
			result.getIp().add(name, pw.getIp());
			result.getOp().add(name, pw.getOp());
			result.getOwnExpIp().add(name, pw.getOwnExpIp());
			result.getOwnExpOp().add(name, pw.getOwnExpOp());
			count++;
			if (count >= 52) {
				break;
			}
		}
		return result;
	}
	
	public PeriodPointWeeklyPayload getPeroidPointWeeklyByFunctype(Date edate, String funcType) {
		PeriodPointWeeklyPayload result = new PeriodPointWeeklyPayload();
		java.sql.Date e = new java.sql.Date(edate.getTime());
		List<POINT_WEEKLY> list = new ArrayList<POINT_WEEKLY>();
		if(funcType != null && funcType.equals("00")) {
			funcType = "";
		}
		if(funcType.isEmpty()) {
			list = pointWeeklyDao.findByEndDateLessThanEqualAndFuncTypeOrderByEndDateDesc(e,
					XMLConstant.FUNC_TYPE_ALL);
		}
		else {
			list = pointWeeklyDao.findByEndDateLessThanEqualAndFuncTypeOrderByEndDateDesc(e,
					funcType);
		}
		int count = 0;
		for (POINT_WEEKLY pw : list) {
			String name = pw.getPyear() + " w" + pw.getPweek();
			result.getIp().add(name, pw.getIp());
			result.getOp().add(name, pw.getOp());
			result.getOwnExpIp().add(name, pw.getOwnExpIp());
			result.getOwnExpOp().add(name, pw.getOwnExpOp());
			count++;
			if (count >= 52) {
				break;
			}
		}
		return result;
	}

	public List<String> getAllDRGFuncTypes(java.sql.Date startDate, java.sql.Date endDate) {
		List<String> result = new ArrayList<String>();
		List<Object[]> list = mrDao.findDRGDistinctFuncTypeByDate(startDate, endDate);
		for (Object[] objects : list) {
			result.add((String) objects[0]);
		}
		return result;
	}

	public List<String> getAllDRGFuncTypes(String ym) {
		List<String> result = new ArrayList<String>();
		List<Object[]> list = mrDao.findDRGDistinctFuncTypeByApplYm(ym);
		for (Object[] objects : list) {
			result.add((String) objects[0]);
		}
		return result;
	}

	public void calculateDRGMonthly(String ym) {
		if ("ALL".equals(ym.toUpperCase())) {
			List<Map<String, Object>> list = mrDao.getAllApplYm();
			for (Map<String, Object> map : list) {
				String applYm = (String) map.get("APPL_YM");
				System.out.println("calculateDRGMonthly " + ym);
				calculateDRGMonthly(applYm);
			}
			return;
		}

		String chineseYM = ymToROCYM(ym);
		String adYM = ymToADYM(ym);

		DRG_MONTHLY drgMonthlyAll = drgMonthlyDao.findByYmAndFuncType(Integer.parseInt(adYM),
				XMLConstant.FUNC_TYPE_ALL);
		if (drgMonthlyAll == null) {
			drgMonthlyAll = new DRG_MONTHLY();
		}
		drgMonthlyAll.setYm(Integer.parseInt(adYM));
		drgMonthlyAll.setFuncType(XMLConstant.FUNC_TYPE_ALL);

		List<String> funcTypes = getAllDRGFuncTypes(chineseYM);
		for (String funcType : funcTypes) {
			DRG_MONTHLY pm = null;
			DRG_MONTHLY old = drgMonthlyDao.findByYmAndFuncType(Integer.parseInt(adYM), funcType);
			if (old == null) {
				pm = new DRG_MONTHLY();
			} else {
				pm = old;
			}
			pm.setYm(Integer.parseInt(adYM));
			pm.setFuncType(funcType);
			List<Object[]> list = mrDao.findDRGCountAndDotByApplYmGroupByDrgSection(chineseYM, funcType);
			if (list != null && list.size() > 0) {
				for (Object[] obj : list) {
					String section = (String) obj[0];
					long appl = getLongValue(obj[2]);
					long actual = getLongValue(obj[3]);
					if ("A".equals(section)) {
						pm.setSectionA(((BigInteger) obj[1]).longValue());
						pm.setSectionAAppl(appl);
						pm.setSectionAActual(actual);
					} else if ("B1".equals(section)) {
						pm.setSectionB1(((BigInteger) obj[1]).longValue());
						pm.setSectionB1Appl(appl);
						pm.setSectionB1Actual(actual);
					} else if ("B2".equals(section)) {
						pm.setSectionB2(((BigInteger) obj[1]).longValue());
						pm.setSectionB2Appl(appl);
						pm.setSectionB2Actual(actual);
					} else if ("C".equals(section)) {
						pm.setSectionC(((BigInteger) obj[1]).longValue());
						pm.setSectionCAppl(appl);
						pm.setSectionCActual(actual);
					}
				}

				drgMonthlyAll.setSectionA(drgMonthlyAll.getSectionA() + pm.getSectionA());
				drgMonthlyAll.setSectionB1(drgMonthlyAll.getSectionB1() + pm.getSectionB1());
				drgMonthlyAll.setSectionB2(drgMonthlyAll.getSectionB2() + pm.getSectionB2());
				drgMonthlyAll.setSectionC(drgMonthlyAll.getSectionC() + pm.getSectionC());

				drgMonthlyAll.setSectionAAppl(drgMonthlyAll.getSectionAAppl() + pm.getSectionAAppl());
				drgMonthlyAll.setSectionB1Appl(drgMonthlyAll.getSectionB1Appl() + pm.getSectionB1Appl());
				drgMonthlyAll.setSectionB2Appl(drgMonthlyAll.getSectionB2Appl() + pm.getSectionB2Appl());
				drgMonthlyAll.setSectionCAppl(drgMonthlyAll.getSectionCAppl() + pm.getSectionCAppl());

				drgMonthlyAll.setSectionAActual(drgMonthlyAll.getSectionAActual() + pm.getSectionAActual());
				drgMonthlyAll.setSectionB1Actual(drgMonthlyAll.getSectionB1Actual() + pm.getSectionB1Actual());
				drgMonthlyAll.setSectionB2Actual(drgMonthlyAll.getSectionB2Actual() + pm.getSectionB2Actual());
				drgMonthlyAll.setSectionCActual(drgMonthlyAll.getSectionCActual() + pm.getSectionCActual());
			}
			pm.setUpdateAt(new Date());
			drgMonthlyDao.save(pm);
		}
		drgMonthlyAll.setUpdateAt(new Date());
		drgMonthlyDao.save(drgMonthlyAll);
	}

	private java.sql.Date getLastDayOfMonth(int year, int month) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(sdf.parse(String.valueOf(year * 100 + month)));
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.add(Calendar.MONTH, 1);
			cal.add(Calendar.DAY_OF_YEAR, -1);
			return new java.sql.Date(cal.getTimeInMillis());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public DRGMonthlyPayload getDrgMonthly(int year, int month) {
		DRGMonthlyPayload result = new DRGMonthlyPayload(pointMonthlyDao.findByYm(year * 100 + month));

		result.getFuncTypes().add(FUNC_TYPE_ALL_NAME);
		java.sql.Date lastDay = getLastDayOfMonth(year, month);
		addQuantityAndPoint(result, XMLConstant.FUNC_TYPE_ALL, FUNC_TYPE_ALL_NAME, lastDay);
		return result;
	}

	public DRGMonthlyPayload getDrgMonthlyAllFuncType(int year, int month) {
		DRGMonthlyPayload result = new DRGMonthlyPayload(pointMonthlyDao.findByYm(year * 100 + month));
		List<String> funcTypes = getAllDRGFuncTypes(String.valueOf((year - 1911) * 100 + month));
		funcTypes.add(0, XMLConstant.FUNC_TYPE_ALL);
		List<String> funcTypeName = codeTableService.convertFuncTypeToName(funcTypes);
		result.setFuncTypes(funcTypeName);
		java.sql.Date lastDay = getLastDayOfMonth(year, month);

		for (int i = 0; i < funcTypes.size(); i++) {
			addQuantityAndPoint(result, funcTypes.get(i), funcTypeName.get(i), lastDay);
		}

		return result;
	}

	private void addQuantityAndPoint(DRGMonthlyPayload payload, String funcType, String funcTypeName,
			java.sql.Date lastDay) {

		List<DRG_WEEKLY> list = drgWeeklyDao.findByFuncTypeAndEndDateLessThanEqualOrderByEndDateDesc(funcType, lastDay);
		if (list != null && list.size() > 0) {
			int count = 0;
			for (DRG_WEEKLY dw : list) {
				String name = dw.getPyear() + " w" + dw.getPweek();
				payload.getQuantityList(funcType, funcTypeName).add(name, dw.getDrgQuantity(), dw.getNondrgQuantity());
				payload.getPointList(funcType, funcTypeName).add(name, dw.getDrgPoint(), dw.getNondrgPoint());
				count++;
				if (count >= 52) {
					break;
				}
			}
		}
	}

	public DRGMonthlySectionPayload getDrgMonthlySection(int year, int month) {
		DRGMonthlySectionPayload result = new DRGMonthlySectionPayload(pointMonthlyDao.findByYm(year * 100 + month));
		List<String> funcTypes = getAllDRGFuncTypes(String.valueOf((year - 1911) * 100 + month));
		funcTypes.add(0, XMLConstant.FUNC_TYPE_ALL);
		List<String> funcTypeNames = codeTableService.convertFuncTypeToName(funcTypes);
		result.setFuncTypes(funcTypeNames);

		java.sql.Date lastDay = getLastDayOfMonth(year, month);

		for (int i = 0; i < funcTypes.size(); i++) {
			String funcTypeName = codeTableService.getDesc("FUNC_TYPE", funcTypes.get(i));
			DRG_MONTHLY dm = drgMonthlyDao.findByYmAndFuncType(year * 100 + month, funcTypes.get(i));
			if (funcTypes.get(i).equals(XMLConstant.FUNC_TYPE_ALL)) {
				result.setActualA(dm.getSectionAActual());
				result.setActualB1(dm.getSectionB1Actual());
				result.setActualB2(dm.getSectionB2Actual());
				result.setActualC(dm.getSectionCActual());

				result.setApplA(dm.getSectionAAppl());
				result.setApplB1(dm.getSectionB1Appl());
				result.setApplB2(dm.getSectionB2Appl());
				result.setApplC(dm.getSectionCAppl());

				result.setDiffA(result.getApplA() - result.getActualA());
				result.setDiffB1(result.getApplB1() - result.getActualB1());
				result.setDiffB2(result.getApplB2() - result.getActualB2());
				result.setDiffC(result.getApplC() - result.getActualC());

				result.setQuantityA(dm.getSectionA());
				result.setQuantityB1(dm.getSectionB1());
				result.setQuantityB2(dm.getSectionB2());
				result.setQuantityC(dm.getSectionC());
			} else {
				NameCodePointQuantity ncpqA = new NameCodePointQuantity();
				ncpqA.setCode(funcTypes.get(i));
				ncpqA.setName(funcTypeName);
				ncpqA.setPoint(dm.getSectionAAppl());
				ncpqA.setQuantity(dm.getSectionA());
				result.getSectionA().add(ncpqA);

				NameCodePointQuantity ncpqB1 = new NameCodePointQuantity();
				ncpqB1.setCode(funcTypes.get(i));
				ncpqB1.setName(funcTypeName);
				ncpqB1.setPoint(dm.getSectionB1Appl());
				ncpqB1.setQuantity(dm.getSectionB1());
				result.getSectionB1().add(ncpqB1);

				NameCodePointQuantity ncpqB2 = new NameCodePointQuantity();
				ncpqB2.setCode(funcTypes.get(i));
				ncpqB2.setName(funcTypeName);
				ncpqB2.setPoint(dm.getSectionB2Appl());
				ncpqB2.setQuantity(dm.getSectionB2());
				result.getSectionB2().add(ncpqB2);

				NameCodePointQuantity ncpqC = new NameCodePointQuantity();
				ncpqC.setCode(funcTypes.get(i));
				ncpqC.setName(funcTypeName);
				ncpqC.setPoint(dm.getSectionCAppl());
				ncpqC.setQuantity(dm.getSectionC());
				result.getSectionC().add(ncpqC);

				NameCodePoint ncpB1 = new NameCodePoint();
				ncpB1.setCode(funcTypes.get(i));
				ncpB1.setName(funcTypeName);
				ncpB1.setPoint(dm.getSectionB1Appl() - dm.getSectionB1Actual());
				result.getDiffB1FuncType().add(ncpB1);

				NameCodePoint ncpB2 = new NameCodePoint();
				ncpB2.setCode(funcTypes.get(i));
				ncpB2.setName(funcTypeName);
				ncpB2.setPoint(dm.getSectionB2Appl() - dm.getSectionB2Actual());
				result.getDiffB2FuncType().add(ncpB2);

				NameCodePoint ncpC = new NameCodePoint();
				ncpC.setCode(funcTypes.get(i));
				ncpC.setName(funcTypeName);
				ncpC.setPoint(dm.getSectionCAppl() - dm.getSectionCActual());
				result.getDiffCFuncType().add(ncpC);
			}
			getDrgSectionWeekly(result, funcTypes.get(i), funcTypeName, lastDay);
		}

		return result;
	}

	private void getDrgSectionWeekly(DRGMonthlySectionPayload payload, String funcType, String funcTypeName,
			java.sql.Date lastDay) {
		List<DRG_WEEKLY> list = drgWeeklyDao.findByFuncTypeAndEndDateLessThanEqualOrderByEndDateDesc(funcType, lastDay);
		if (list != null && list.size() > 0) {
			int count = 0;
			// A 區, 件數, 點數
			NameValueList2 nvlA = new NameValueList2();
			// B1 區, 件數, 點數
			NameValueList2 nvlB1 = new NameValueList2();
			// B2 區, 件數, 點數
			NameValueList2 nvlB2 = new NameValueList2();
			// C 區, 件數, 點數
			NameValueList2 nvlC = new NameValueList2();
			for (DRG_WEEKLY dw : list) {
				String name = dw.getPyear() + " w" + dw.getPweek();
				nvlA.add(name, dw.getSectionA(), dw.getPointA() == null ? 0 : dw.getPointA());
				nvlB1.add(name, dw.getSectionB1(), dw.getPointB1() == null ? 0 : dw.getPointB1());
				nvlB2.add(name, dw.getSectionB2(), dw.getPointB2() == null ? 0 : dw.getPointB2());
				nvlC.add(name, dw.getSectionC(), dw.getPointC() == null ? 0 : dw.getPointC());
				count++;
				if (count >= 52) {
					break;
				}
			}
			payload.getWeeklyAMap().put(funcTypeName, nvlA);
			payload.getWeeklyB1Map().put(funcTypeName, nvlB1);
			payload.getWeeklyB2Map().put(funcTypeName, nvlB2);
			payload.getWeeklyCMap().put(funcTypeName, nvlC);
		}
	}

	public static long getLongValue(Object obj) {
		if (obj == null) {
			return 0L;
		}
		if (obj instanceof BigDecimal) {
			BigDecimal dot = (BigDecimal) obj;
			return dot.longValue();
		} else if (obj instanceof BigInteger) {
			BigInteger dot = (BigInteger) obj;
			return dot.longValue();
		}
		return (long) (int) obj;
	}

	/**
	 * 取得健保申報總額達成趨勢資料
	 * 
	 * @param cal
	 * @return
	 */
	public AchievementWeekly getAchievementWeekly(Calendar cal) {
		AchievementWeekly result = new AchievementWeekly();
		java.sql.Date e = new java.sql.Date(cal.getTimeInMillis());
		List<POINT_WEEKLY> list = pointWeeklyDao.findByEndDateLessThanEqualAndFuncTypeOrderByEndDateDesc(e,
				XMLConstant.FUNC_TYPE_ALL);
		int count = 0;
		for (POINT_WEEKLY pw : list) {
			String name = pw.getPyear() + " w" + pw.getPweek();
			result.getIp().add(name, pw.getIp());
			result.getOp().add(name, pw.getOp() - pw.getEm());
			result.getAll().add(name, pw.getIp() + pw.getOp());
			result.getEm().add(name, pw.getEm());
			result.getOpAll().add(name, pw.getOp());
			;
			count++;
			if (count >= 52) {
				break;
			}
		}
		addMonthlyData(result, cal);
		return result;
	}

	private void addMonthlyData(AchievementWeekly aw, Calendar cal) {
		Calendar lastMonth = Calendar.getInstance();
		lastMonth.setTime(cal.getTime());

		lastMonth.set(Calendar.DAY_OF_WEEK, cal.getActualMaximum(Calendar.DAY_OF_WEEK));
		int endYm = lastMonth.get(Calendar.YEAR) * 100 + lastMonth.get(Calendar.MONTH) + 1;
		lastMonth.add(Calendar.WEEK_OF_YEAR, -52);
		lastMonth.set(Calendar.DAY_OF_WEEK, cal.getActualMinimum(Calendar.DAY_OF_WEEK));
		int startYm = lastMonth.get(Calendar.YEAR) * 100 + lastMonth.get(Calendar.MONTH) + 1;
		// System.out.println("startYm=" + startYm + ", endYm=" + endYm);
		// SimpleDateFormat sdf = new SimpleDateFormat(DateTool.SDF);
		List<POINT_MONTHLY> list = pointMonthlyDao.findByYmBetweenOrderByYm(startYm, endYm);
		Calendar temp = Calendar.getInstance();
		for (int i = 0; i < list.size(); i++) {
			POINT_MONTHLY pm = list.get(i);
			if (i == list.size() - 1) {
				aw.setMonthTotal(pm.getTotalAll());
				aw.setMonthAssigned(pm.getAssignedAll());
				DecimalFormat df = new DecimalFormat("#.##");
				aw.setAchievementRate(
						df.format(((double) aw.getMonthTotal() * (double) 100) / (double) aw.getMonthAssigned()) + "%");
			}

			temp.set(Calendar.YEAR, pm.getYm() / 100);
			temp.set(Calendar.MONTH, (pm.getYm() % 100) - 1);
			// temp.set(Calendar.DAY_OF_MONTH,
			// temp.getActualMaximum(Calendar.DAY_OF_MONTH));
			temp.set(Calendar.DAY_OF_MONTH, 1);

			String name = String.valueOf(temp.get(Calendar.YEAR)) + " w" + temp.get(Calendar.WEEK_OF_YEAR);
			// System.out.println("point monthly " + i + ":" + pm.getYm() + " day=" +
			// sdf.format(temp.getTime()) + " name=" + name);
			aw.getAssignedAll().add(name, pm.getAssignedAll());
			aw.getActualAll().add(name, pm.getTotalAll(), (pm.getTotalAll() * 100 / pm.getAssignedAll()));

			aw.getAssignedOpAll().add(name, pm.getAssignedOpAll());
			aw.getActualOpAll().add(name, pm.getTotalOpAll(), (pm.getTotalOpAll() * 100 / pm.getAssignedOpAll()));

			aw.getAssignedIp().add(name, pm.getAssignedIp());
			aw.getActualIp().add(name, pm.getTotalIp(), (pm.getTotalIp() * 100 / pm.getAssignedIp()));
		}
	}

	/**
	 * 若當週有跨月份，只算到前一月份的日期
	 * 
	 * @param cal
	 * @return
	 */
	@Deprecated
	public Calendar checkWeekInMonth(Calendar cal) {
		Calendar result = Calendar.getInstance();
		result.setTime(cal.getTime());
		result.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		int month = result.get(Calendar.MONTH);
		for (int i = 1; i < 7; i++) {
			result.add(Calendar.DAY_OF_WEEK, 1);
			if (result.get(Calendar.MONTH) != month) {
				result.add(Calendar.DAY_OF_WEEK, -1);
				return result;
			}
		}
		return result;
	}

	public AchievementQuarter getAchievementQuarter(String year, String quarter) {
		AchievementQuarter result = new AchievementQuarter();

		String[] years = StringUtility.splitBySpace(year);
		String[] quarters = StringUtility.splitBySpace(quarter);
		int[] yearMonthBetween = findOldestAndNewestYearMonth(years, quarters);
		List<POINT_MONTHLY> list = pointMonthlyDao.findByYmBetweenOrderByYm(yearMonthBetween[0], yearMonthBetween[1]);
		for (int i = 0; i < years.length; i++) {
			int[] yearMonth = getYearMonthByQuarter(years[i], quarters[i]);
			for (int j = 0; j < yearMonth.length; j++) {
				int index = -1;
				for (int k = 0; k < list.size(); k++) {
					POINT_MONTHLY pm = list.get(k);
					pm.checkNull();
					if (pm.getYm().intValue() == yearMonth[j]) {
						index = k;
						calculateAchievementQuarter(result, pm, years[i] + "/" + quarters[i]);
						break;
					}
				}
				if (index > -1) {
					list.remove(index);
				}
			}
		}

		return result;
	}

	private int[] findOldestAndNewestYearMonth(String[] years, String[] quarters) {
		int min = Integer.MAX_VALUE;
		int max = 0;
		for (int i = 0; i < years.length; i++) {
			int[] yearMonth = getYearMonthByQuarter(years[i], quarters[i]);
			// System.out.println(yearMonth[0] + "," + yearMonth[1] + "," + yearMonth[2]);
			if (max < yearMonth[2]) {
				max = yearMonth[2];
			}
			if (min > yearMonth[0]) {
				min = yearMonth[0];
			}
		}
		return new int[] { min, max };
	}

	/**
	 * 計算健保總額累積達成率
	 * 
	 * @param aq
	 * @param pm
	 * @param name
	 */

	private void calculateAchievementQuarter(AchievementQuarter aq, POINT_MONTHLY pm, String name) {
		DecimalFormat df = new DecimalFormat("#.##");
		QuarterData qdAll = getQuarterDataByName(aq.getAll(), name);
		QuarterData qdIp = getQuarterDataByName(aq.getIp(), name);
		QuarterData qdOp = getQuarterDataByName(aq.getOp(), name);

		qdAll.setActual(qdAll.getActual() + pm.getTotalAll());
		qdAll.setAssigned(qdAll.getAssigned() + pm.getAssignedAll());
		qdAll.setOriginal(qdAll.getOriginal() + pm.getTotalAll() + pm.getNoApplAll());
		qdAll.setOver(qdAll.getActual().longValue() - qdAll.getAssigned().longValue());
		qdAll.setPercent(Float.parseFloat(
				df.format((double) (qdAll.getActual().longValue() * 100) / qdAll.getAssigned().doubleValue())));

		qdIp.setActual(qdIp.getActual() + pm.getTotalIp());
		qdIp.setAssigned(qdIp.getAssigned() + pm.getAssignedIp());
		qdIp.setOriginal(qdIp.getOriginal() + pm.getTotalIp() + pm.getNoApplIp());
		qdIp.setOver(qdIp.getActual().longValue() - qdIp.getAssigned().longValue());
		qdIp.setPercent(Float.parseFloat(
				df.format((double) (qdIp.getActual().longValue() * 100) / qdIp.getAssigned().doubleValue())));

		qdOp.setActual(qdOp.getActual() + pm.getTotalOpAll());
		qdOp.setAssigned(qdOp.getAssigned() + pm.getAssignedOpAll());
		qdOp.setOriginal(qdOp.getOriginal() + pm.getTotalOpAll() + pm.getNoApplOp());
		qdOp.setOver(qdOp.getActual().longValue() - qdOp.getAssigned().longValue());
		qdOp.setPercent(Float.parseFloat(
				df.format((double) (qdOp.getActual().longValue() * 100) / qdOp.getAssigned().doubleValue())));
	}

	private QuarterData getQuarterDataByName(List<QuarterData> list, String name) {
		if (list.size() == 0) {
			QuarterData result = new QuarterData(name);
			list.add(result);
			return result;
		}
		for (QuarterData quarterData : list) {
			if (quarterData.getName().equals(name)) {
				return quarterData;
			}
		}
		QuarterData result = new QuarterData(name);
		list.add(result);
		return result;
	}

	public static int[] getYearMonthByQuarter(String year, String quarter) {
		int[] result = new int[3];
		int yearInt = Integer.parseInt(year) * 100;
		String q = quarter.toUpperCase();
		if (q.startsWith("Q")) {
			q = q.substring(1);
		}
		int quarterInt = Integer.parseInt(q);
		for (int i = 0; i < 3; i++) {
			result[i] = yearInt + (quarterInt - 1) * 3 + i + 1;
		}

		return result;
	}

	/**
	 * 取得圖表門急診/住院/出院人次變化資料
	 * 
	 * @param sdate
	 * @param edate
	 * @param year
	 * @param week
	 * @return
	 */
	public VisitsVarietyPayload getVisitsVariety(java.sql.Date sdate, java.sql.Date edate, String year, String week) {
		VisitsVarietyPayload result = new VisitsVarietyPayload();

		result.setFuncTypes(findAllFuncTypesName(true));
		List<Object[]> list = mrDao.getPointPeriod(sdate, edate, sdate, edate, sdate, edate, sdate, edate, sdate, edate,
				sdate, edate, sdate, edate, sdate, edate);

		if (list != null && list.size() > 0) {
			Object[] obj = list.get(0);

			PointPeriod actual = new PointPeriod();
			actual.setAll(getLongValue(obj[0]));
			actual.setOpem(getLongValue(obj[1]));
			actual.setEm(getLongValue(obj[2]));
			actual.setIp(getLongValue(obj[3]));
			result.setActual(actual);

			PointPeriod appl = new PointPeriod();
			appl.setAll(getLongValue(obj[4]));
			appl.setOpem(getLongValue(obj[5]));
			appl.setEm(getLongValue(obj[6]));
			appl.setIp(getLongValue(obj[7]));
			result.setAppl(appl);
		}

		java.sql.Date lastSdate = null;
		java.sql.Date lastEdate = null;
		int daysDiff = (int) ((edate.getTime() - sdate.getTime()) / 86400000L);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(sdate.getTime());
		if (daysDiff <= 30) {
			// 30天抓上個月同區間
			cal.add(Calendar.MONTH, -1);
			lastSdate = new java.sql.Date(cal.getTimeInMillis());

			cal.setTimeInMillis(edate.getTime());
			cal.add(Calendar.MONTH, -1);
			lastEdate = new java.sql.Date(cal.getTimeInMillis());
		} else {
			cal.add(Calendar.DAY_OF_YEAR, -1);
			lastEdate = new java.sql.Date(cal.getTimeInMillis());

			cal.add(Calendar.DAY_OF_YEAR, -daysDiff);
			lastSdate = new java.sql.Date(cal.getTimeInMillis());
		}
		list = mrDao.getVisitsPeriod(sdate, edate, sdate, edate, sdate, edate, sdate, edate, sdate, edate, sdate, edate,
				sdate, edate, sdate, edate, sdate, edate, lastSdate, lastEdate, lastSdate, lastEdate, lastSdate,
				lastEdate, lastSdate, lastEdate, lastSdate, lastEdate);
		if (list != null && list.size() > 0) {
			Object[] obj = list.get(0);

			VisitsPeriod vp = new VisitsPeriod();
			VisitsPeriodDetail vpd = new VisitsPeriodDetail();
			vpd.setAll(getLongValue(obj[0]));
			vpd.setOpem(getLongValue(obj[1]));
			vpd.setEm(getLongValue(obj[2]));
			vpd.setIp(getLongValue(obj[3]));
			vpd.setLeave(getLongValue(obj[4]));
			vp.setTotal(vpd);

			VisitsPeriodDetail vpdSurgery = new VisitsPeriodDetail();
			vpdSurgery.setOpem(getLongValue(obj[5]));
			vpdSurgery.setEm(getLongValue(obj[6]));
			vpdSurgery.setIp(getLongValue(obj[7]));
			vpdSurgery.setLeave(getLongValue(obj[8]));
			vpdSurgery.setAll(vpdSurgery.getOpem().longValue() + vpdSurgery.getIp());
			vp.setSurgery(vpdSurgery);

			VisitsPeriodDetail vpdDiff = new VisitsPeriodDetail();
			vpdDiff.setAll(vpd.getAll().longValue() - getLongValue(obj[9]));
			vpdDiff.setOpem(vpd.getOpem().longValue() - getLongValue(obj[10]));
			vpdDiff.setEm(vpd.getEm().longValue() - getLongValue(obj[11]));
			vpdDiff.setIp(vpd.getIp().longValue() - getLongValue(obj[12]));
			vpdDiff.setLeave(vpd.getLeave().longValue() - getLongValue(obj[13]));
			vp.setDiff(vpdDiff);

			DecimalFormat df = new DecimalFormat("#.##");
			if (getLongValue(obj[9]) == 0) {
				vp.setPercentAll(100f);
			} else {
				vp.setPercentAll(Float.parseFloat(
						df.format(((double) vpdDiff.getAll() * (double) 100) / (double) getLongValue(obj[9]))));
			}
			if (getLongValue(obj[10]) == 0) {
				vp.setPercentOpem(100f);
			} else {
				vp.setPercentOpem(Float.parseFloat(
						df.format(((double) vpdDiff.getOpem() * (double) 100) / (double) getLongValue(obj[10]))));
			}
			if (getLongValue(obj[11]) == 0) {
				vp.setPercentEm(100f);
			} else {
				vp.setPercentEm(Float.parseFloat(
						df.format(((double) vpdDiff.getEm() * (double) 100) / (double) getLongValue(obj[11]))));
			}

			if (getLongValue(obj[12]) == 0) {
				vp.setPercentIp(100f);
			} else {
				vp.setPercentIp(Float.parseFloat(
						df.format(((double) vpdDiff.getIp() * (double) 100) / (double) getLongValue(obj[12]))));
			}
			if (getLongValue(obj[13]) == 0) {
				vp.setPercentLeave(100f);
			} else {
				vp.setPercentLeave(Float.parseFloat(
						df.format(((double) vpdDiff.getLeave() * (double) 100) / (double) getLongValue(obj[13]))));
			}
			result.setVisitsPeriod(vp);
		}
		getVisitsWeekly(result, year, week);
		return result;
	}

	private void getVisitsWeekly(VisitsVarietyPayload vvp, String year, String week) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, Integer.parseInt(year));
		cal.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(week));
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);

		Map<String, String> funcMap = findAllFuncTypesMap(true);

		Map<String, NameValueList> opemMap = vvp.getOpemMap();
		Map<String, NameValueList> ipMap = vvp.getIpMap();
		Map<String, NameValueList> leaveMap = vvp.getLeaveMap();

		List<POINT_WEEKLY> list = pointWeeklyDao
				.findByEndDateLessThanEqualOrderByEndDateDesc(new java.sql.Date(cal.getTimeInMillis()));
		// 記錄抓了幾週的資料
		Map<String, String> weeks = new HashMap<String, String>();
		for (POINT_WEEKLY pw : list) {
			String name = pw.getPyear() + " w" + pw.getPweek();
			if (weeks.get(name) == null) {
				weeks.put(name, "");
			}
			NameValueList nvlOpem = opemMap.get(funcMap.get(pw.getFuncType()));
			if (nvlOpem == null) {
				nvlOpem = new NameValueList();
				opemMap.put(funcMap.get(pw.getFuncType()), nvlOpem);
			}
			NameValueList nvlIp = ipMap.get(funcMap.get(pw.getFuncType()));
			if (nvlIp == null) {
				nvlIp = new NameValueList();
				ipMap.put(funcMap.get(pw.getFuncType()), nvlIp);
			}
			NameValueList nvlLeave = leaveMap.get(funcMap.get(pw.getFuncType()));
			if (nvlLeave == null) {
				nvlLeave = new NameValueList();
				leaveMap.put(funcMap.get(pw.getFuncType()), nvlLeave);
			}

			nvlOpem.add(name, pw.getVisitsOp());
			nvlIp.add(name, pw.getVisitsIp());
			nvlLeave.add(name, pw.getVisitsLeave());
			if (weeks.keySet().size() >= 52) {
				break;
			}
		}
	}

	private VisitsVarietyPayload getVistAndPointWeekly(VisitsVarietyPayload vvp, String year, String month) {

		String sDate = String.valueOf(Integer.valueOf(year) - 1) + "-" + month + "-01";
		String eDate = year + "-" + month + "-01";
		Date Sdate = new Date();
		Date Edate = new Date();
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendarS = Calendar.getInstance();
		Calendar calendarE = Calendar.getInstance();
		try {
			Sdate = sdf.parse(sDate);
			Edate = sdf.parse(eDate);
			
		}catch(Exception e) {
			
		}

		///抓取帶入年月的月底
		calendarS.setTime(Sdate);
		calendarS.set(Calendar.DAY_OF_MONTH, calendarS.getActualMaximum(Calendar.DAY_OF_MONTH)); 
		///抓取帶入年月前一年的的月底
		calendarE.setTime(Edate);
		calendarE.set(Calendar.DAY_OF_MONTH, calendarE.getActualMaximum(Calendar.DAY_OF_MONTH)); 
		String start = sdf.format(calendarS.getTime()); 
		String last = sdf.format(calendarE.getTime()); 
		
		sDate = start;
		eDate = last;

		Map<String, String> funcMap = findAllFuncTypesMap(true);
		///門急診案件數＆點數
		Map<String, NameValueList3> opemMap3 = vvp.getOpemMap3();
		///住院案件數＆點數
		Map<String, NameValueList3> ipMap3 = vvp.getIpMap3();
		///門急診＆住院案件數＆點數
		Map<String, NameValueList3> allMap3 = vvp.getAllMap3();
		///門急診人次
		Map<String, NameValueList> opemMap = vvp.getOpemMap();
		///住院人次
		Map<String, NameValueList> ipMap = vvp.getIpMap();
		///出院人次
		Map<String, NameValueList> leaveMap = vvp.getLeaveMap();
		///數據會以52週為例
		List<POINT_WEEKLY> list = pointWeeklyDao.getTredAllData(sDate, eDate);
		
		// 記錄抓了幾週的資料
		Map<String, String> weeks = new HashMap<String, String>();

		
		for (POINT_WEEKLY pw : list) {
			String name = pw.getPyear() + " w" + pw.getPweek();
			if (weeks.get(name) == null) {
				weeks.put(name, "");
			}
	
			try {

				NameValueList3 nvlOpem3 = opemMap3.get(funcMap.get(pw.getFuncType()));
				if (nvlOpem3 == null) {
					nvlOpem3 = new NameValueList3();
					opemMap3.put(funcMap.get(pw.getFuncType()), nvlOpem3);
				}
				NameValueList3 nvlIp3 = ipMap3.get(funcMap.get(pw.getFuncType()));
				if (nvlIp3 == null) {
					nvlIp3 = new NameValueList3();
					ipMap3.put(funcMap.get(pw.getFuncType()), nvlIp3);
				}
				NameValueList3 nvlAll3 = allMap3.get(funcMap.get(pw.getFuncType()));
				if (nvlAll3 == null) {
					nvlAll3 = new NameValueList3();
					allMap3.put(funcMap.get(pw.getFuncType()), nvlAll3);
				}
				NameValueList nvlLeave = leaveMap.get(funcMap.get(pw.getFuncType()));
				if (nvlLeave == null) {
					nvlLeave = new NameValueList();
					leaveMap.put(funcMap.get(pw.getFuncType()), nvlLeave);
				}
				NameValueList nvlOpem = opemMap.get(funcMap.get(pw.getFuncType()));
				if (nvlOpem == null) {
					nvlOpem = new NameValueList();
					opemMap.put(funcMap.get(pw.getFuncType()), nvlOpem);
				}
				NameValueList nvlIp = ipMap.get(funcMap.get(pw.getFuncType()));
				if (nvlIp == null) {
					nvlIp = new NameValueList();
					ipMap.put(funcMap.get(pw.getFuncType()), nvlIp);
				}
				nvlOpem3.add(name, pw.getVisitsOp(), pw.getOp());
				nvlIp3.add(name, pw.getVisitsIp(), pw.getIp());
				Long allCase = pw.getVisitsOp() + pw.getVisitsIp();
				Long allPoint = pw.getOp() + pw.getIp();
				nvlAll3.add(name, allCase, allPoint);
				nvlLeave.add(name, pw.getVisitsLeave());
				nvlOpem.add(name, pw.getVisitsOp());
				nvlIp.add(name, pw.getVisitsIp());
				
				
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(e);
			}

			if (weeks.keySet().size() >= 52) {
				break;
			}
		}
		return vvp;
	}

	/**
	 * 單月各科健保申報量與人次
	 * 
	 * @param year
	 * @param month
	 * @param func_type
	 * @return
	 */
	public PointMRPayload getMonthlyReportApplCount(int year, int month) {

		String monthStr = "";
		if (month < 10) {
			monthStr = "0" + month;
		} else {
			monthStr = String.valueOf(month);
		}

		String inputDate = DateTool.convertToChineseYear(year + monthStr + "00");
		/// 轉成民國年月
		String endDate = inputDate.substring(0, inputDate.length() - 2);
		
		POINT_MONTHLY pmModel =	pointMonthlyDao.findByYm(year * 100 + month);
		/// 取得門急診人數
		int patient_op = oppDao.getFuncEndDateCount(endDate);
		patient_op = pmModel.getPatientOp().intValue() + pmModel.getPatientEm().intValue();
		List<IP_D> ipdList = ipdDao.getApplCountByApplYM(endDate);
		/// 住院人數
		int patient_ip = ipdList.size();
		patient_ip = pmModel.getIpQuantity().intValue();

		List<Map<String, Object>> mrList = ipdDao.getMrDataByApplYMNull();
		int out_count = 0;
		int in_count = 0;
		/// 如果病例無申報日
		if (mrList.size() > 0) {
			for (Map<String, Object> map : mrList) {
				/// 先以出院日為主
				if (map.get("OUT_DATE") != null && map.get("OUT_DATE").toString().contains(endDate)) {
					out_count++;
				}
				if (map.get("OUT_DATE") == null) {
					/// 再以住院日為主
					if (map.get("IN_DATE").toString().contains(endDate)) {
						in_count++;
					}
				}

			}
		}
		/// 最終住院人數
		int finalPatient_ip = patient_ip + out_count + in_count;
		finalPatient_ip = patient_ip;
		/// 取得門急診圓餅圖資料 人
		List<Map<String, Object>> opPieCountData = opdDao.getOPPieCountData(endDate);

		/// 取得住院圓餅圖資料 人
		List<Map<String, Object>> ipPieCountData = ipdDao.getIPPieCountData(endDate);
		List<Map<String, Object>> collectionList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> collectionList2 = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> peoplePie = new ArrayList<Map<String, Object>>();
		Map<String, Object> ojectMap = new HashMap<String, Object>();
		/// 將門診和急診list加一起
		collectionList.addAll(opPieCountData);
		collectionList.addAll(ipPieCountData);
		collectionList2.addAll(collectionList);

		if (opPieCountData.size() > 0) {
			/// 將門急診和住院資料+在一起
			int opPieCountTotal = opdDao.getOPPieCountTotal(endDate);
			int ipPieCountTotal = ipdDao.getIPPieCountTotal(endDate);
			for (Map<String, Object> op : opPieCountData) {
				String opFt = op.get("FUNC_TYPE").toString();
				String opDC = op.get("DESC_CHI").toString();
				int opC = Integer.parseInt(op.get("COUNT").toString());
				for (Map<String, Object> ip : ipPieCountData) {
					String ipFt = ip.get("FUNC_TYPE").toString();
					String ipDC = ip.get("DESC_CHI").toString();
					int ipC = Integer.parseInt(ip.get("COUNT").toString());
					if (opFt.equals(ipFt)) {
						ojectMap.put("FUNC_TYPE", ipFt);
						ojectMap.put("DESC_CHI", ipDC);
						ojectMap.put("COUNT", String.valueOf(opC + ipC));
						float fp = Float.valueOf(ojectMap.get("COUNT").toString());
						String tt = String.valueOf(opPieCountTotal + ipPieCountTotal);
						float m = (fp / Float.valueOf(tt)) * 100;
						String str = String.format("%.02f", m);
						ojectMap.put("PERCENT", str);
						peoplePie.add(ojectMap);
						ojectMap = new HashMap<String, Object>();
					}
				}
			}
			int del = 0;
			for (Map<String, Object> p : peoplePie) {
				String pFt = p.get("FUNC_TYPE").toString();
				for (Map<String, Object> col : collectionList) {
					String colFt = col.get("FUNC_TYPE").toString();
					if (colFt.equals(pFt)) {
						if (del == collectionList2.size()) {
							collectionList2.remove(del - 1);
						} else {
							collectionList2.remove(del);
						}
					}
					del++;
				}
				collectionList.clear();
				collectionList.addAll(collectionList2);
				del = 0;
			}
		}
		/// 將最後結果add倒要顯示集合
		peoplePie.addAll(collectionList2);

		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		Calendar cal_s = DateTool.chineseYmToCalendar2(endDate);
		System.out.print(cal_s.getTime());
		cal_s.add(Calendar.MONTH, -1);
		cal_s.set(Calendar.DAY_OF_MONTH, 1);
		System.out.print(cal_s.getTime());
		String firstDate = format.format(cal_s.getTime());

		Calendar cal_e = DateTool.chineseYmToCalendar2(endDate);
		cal_e.add(Calendar.MONTH, -1);
		cal_e.set(Calendar.DAY_OF_MONTH, cal_e.getActualMaximum(Calendar.DAY_OF_MONTH));
		String lastDate = format.format(cal_e.getTime());

		/// 取得出院圓餅圖資料
		List<Map<String, Object>> ipPieOutCountData = ipdDao.getIPPieOutCountData(firstDate, lastDate);

		/// 取得門急診圓餅圖資料 點數
		List<Map<String, Object>> opPieDotData = opdDao.getOPPieDotData(endDate);
		/// 取得住院圓餅圖資料 點數
		List<Map<String, Object>> ipPieDotData = ipdDao.getIPPieDotData(endDate);
		List<Map<String, Object>> dotPie = new ArrayList<Map<String, Object>>();
		collectionList.clear();
		collectionList2.clear();
		collectionList.addAll(opPieDotData);
		collectionList.addAll(ipPieDotData);
		collectionList2.addAll(collectionList);
		if (opPieDotData.size() > 0) {
			/// 將門急診和住院資料+在一起
			int opPieDotTotal = opdDao.getOPPieDotTotal(endDate);
			int ipPieDoTotal = ipdDao.getIPPieDotTotal(endDate);
			for (Map<String, Object> op : opPieDotData) {
				String opFt = op.get("FUNC_TYPE").toString();
				String opDC = op.get("DESC_CHI").toString();
				int opC = Integer.parseInt(op.get("SUM").toString());
				for (Map<String, Object> ip : ipPieDotData) {
					String ipFt = ip.get("FUNC_TYPE").toString();
					String ipDC = ip.get("DESC_CHI").toString();
					int ipC = Integer.parseInt(ip.get("SUM").toString());
					if (opFt.equals(ipFt)) {
						ojectMap.put("FUNC_TYPE", ipFt);
						ojectMap.put("DESC_CHI", ipDC);
						ojectMap.put("SUM", String.valueOf(opC + ipC));
						float fp = Float.valueOf(ojectMap.get("SUM").toString());
						String tt = String.valueOf(opPieDotTotal + ipPieDoTotal);
						float m = (fp / Float.valueOf(tt)) * 100;
						String str = String.format("%.02f", m);
						ojectMap.put("PERCENT", str);
						dotPie.add(ojectMap);
						ojectMap = new HashMap<String, Object>();
					}
				}
			}

			int del = 0;
			for (Map<String, Object> p : dotPie) {
				String pFt = p.get("FUNC_TYPE").toString();
				for (Map<String, Object> col : collectionList) {
					String colFt = col.get("FUNC_TYPE").toString();
					if (colFt.equals(pFt)) {
						if (del == collectionList2.size()) {
							collectionList2.remove(del - 1);
						} else {
							collectionList2.remove(del);
						}
					}
					del++;
				}
				collectionList.clear();
				collectionList.addAll(collectionList2);
				del = 0;
			}
		}
		/// 將最後結果add倒要顯示集合
		dotPie.addAll(collectionList2);

		PointMRPayload result = new PointMRPayload();

		result.setFuncTypes(findAllFuncTypesName(true));
		/// 取得返回當月資料
		result.setCurrent(pointMonthlyDao.findByYm(year * 100 + month));
		/// 返回門急診人數
		result.setPatient_op_count(patient_op);
		/// 返回住院人數
		result.setPatient_ip_count(finalPatient_ip);
		/// 返回門急診/住院人數
		result.setPatient_total_count(patient_op + finalPatient_ip);
		/// 返回門急診人數圓餅
		Collections.sort(opPieCountData, mapComparator);
		result.setOpPieCountData(opPieCountData);
		/// 返回出院人數圓餅
		Collections.sort(ipPieOutCountData, mapComparator);
		result.setIpPieOutCountData(ipPieOutCountData);
		/// 返回住院人數圓餅
		Collections.sort(ipPieCountData, mapComparator);
		result.setIpPieCountData(ipPieCountData);
		/// 返回 門急診＋住院人數園餅
		Collections.sort(peoplePie, mapComparator);
		result.setTotalPieCountData(peoplePie);
		/// 返回門急診點數圓餅
		Collections.sort(opPieDotData, mapComparator);
		result.setOpPieDotData(opPieDotData);
		/// 返回住院點數圓餅
		Collections.sort(ipPieDotData, mapComparator);
		result.setIpPieDotData(ipPieDotData);
		/// 返回 門急診＋住院點數園餅
		Collections.sort(dotPie, mapComparator);
		result.setTotalPieDotData(dotPie);
		/// 返回趨勢圖資料
		VisitsVarietyPayload res = new VisitsVarietyPayload();
		result.setVisitsVarietyPayload(getVistAndPointWeekly(res, String.valueOf(year), monthStr));

		return result;
	}
	
	public Comparator<Map<String, Object>> mapComparator = new Comparator<Map<String, Object>>() {
	    public int compare(Map<String, Object> m1, Map<String, Object> m2) {
	        return m1.get("DESC_CHI").toString().compareTo(m2.get("DESC_CHI").toString());
	    }
	};

}
