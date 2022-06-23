package tw.com.leadtek.nhiwidget.service;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.util.Arrays;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.dao.MRDao;
import tw.com.leadtek.nhiwidget.dao.POINT_MONTHLYDao;
import tw.com.leadtek.nhiwidget.model.rdb.POINT_MONTHLY;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;
import tw.com.leadtek.nhiwidget.payload.report.AchievementQuarter;
import tw.com.leadtek.nhiwidget.payload.report.OwnExpenseQueryCondition;
import tw.com.leadtek.nhiwidget.payload.report.OwnExpenseQueryConditionDetail;
//import tw.com.leadtek.nhiwidget.payload.report.DrgQueryConditionPayload;
import tw.com.leadtek.nhiwidget.payload.report.QuarterData;
import tw.com.leadtek.tools.StringUtility;

@Service
public class DbReportService {

	private Logger logger = LogManager.getLogger();

	@Autowired
	private POINT_MONTHLYDao pointMonthlyDao;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private MRDao mrDao;

	public BaseResponse test() {
		BaseResponse res = new BaseResponse();
		res.setMessage("isdone");
		res.setResult("ok");
		return res;
	}

	/**
	 * 取得達成率與超額數
	 * 
	 * @param year
	 * @param quarter
	 * @param isLastM
	 * @param isLastY
	 * @return
	 */
	public AchievementQuarter getAchievementAndExcess(String year, String quarter, boolean isLastM, boolean isLastY) {
		AchievementQuarter result = new AchievementQuarter();

		String[] years = StringUtility.splitBySpace(year);
		String[] quarters = StringUtility.splitBySpace(quarter);
		List<Object> yList = Arrays.asList(years);
		List<Object> mList = Arrays.asList(quarters);
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
		/// 如果年月為多個，則不能用上個月同條件相比
		if (years.length > 1) {
			isLastM = false;
		}
		if (isLastM) {
			for (String str : quarters) {
				int m = Integer.valueOf(str.replace("0", ""));
				int y = Integer.valueOf(years[0]);
				/// 如果是一月的話
				if (m == 1) {
					y -= 1;
					/// 跨年份
					yList.add(y);
					mList.add(12);
					map.put("YM", String.valueOf(y * 100 + 12));
					map.put("Value", "M");
					String append = String.valueOf((y + 1) * 100 + m);
					append = append.substring(0, append.length() - 2) + "/"
							+ append.substring(append.length() - 2, append.length());
					map.put("displayName", "上個月同條件相比");
				} else {
					yList.add(years[0]);
					mList.add(m - 1);
					map.put("YM", String.valueOf((y * 100) + (m - 1)));
					map.put("Value", "M");
					String append = String.valueOf((y) * 100 + m);
					append = append.substring(0, append.length() - 2) + "/"
							+ append.substring(append.length() - 2, append.length());
					map.put("displayName", "上個月同條件相比");
				}
			}
			mapList.add(map);
			map = new HashMap<String, Object>();
		}

		if (isLastY) {
			int i = 0;
			for (String str : years) {
				int y = Integer.valueOf(str);
				int m = Integer.valueOf(quarters[i].replace("0", ""));
				y -= 1;
				yList.add(y);
				mList.add(m);
				map.put("YM", String.valueOf((y * 100) + m));
				map.put("Value", "Y");
				String append = String.valueOf((y + 1) * 100 + m);
				append = append.substring(0, append.length() - 2) + "/"
						+ append.substring(append.length() - 2, append.length());
				map.put("displayName", "去年同期時段相比");
				mapList.add(map);
				map = new HashMap<String, Object>();
				i++;

			}
		}
		List<String> sList = new ArrayList<String>();
		for (int i = 0; i < yList.size(); i++) {
			sList.add(yList.get(i).toString() + mList.get(i).toString());
		}

		List<Integer> yearMonthBetween = findYearMonth(yList, mList);
		/// 這裡做排序，name才會對應正確值
		Collections.sort(yearMonthBetween);

		List<POINT_MONTHLY> list = pointMonthlyDao.getByYmInOrderByYm(yearMonthBetween);
		for (int i = 0; i < yList.size(); i++) {
			String displayName = "";
			POINT_MONTHLY pm = list.get(i);
			pm.checkNull();
			if (pm.getYm().intValue() == yearMonthBetween.get(i)) {
				String name = yearMonthBetween.get(i).toString();
				String s1 = name.substring(0, name.length() - 2);
				String s2 = name.substring(name.length() - 2, name.length());
				String show = s1 + "/" + s2;
				/// 如果有條件帶入才進來
				if (mapList.size() > 0) {
					for (Map<String, Object> mm : mapList) {
						String ym = mm.get("YM").toString();
						if (name.equals(ym)) {
							displayName = mm.get("displayName").toString();
						}
					}
				}

				calculateAchievementQuarter(result, pm, show, displayName);
			}

		}

		return result;
	}

	/**
	 * 取得DRG案件數分佈佔率與定額、實際點數
	 * 
	 * @param dateTypes
	 * @param year
	 * @param month
	 * @param betweenSdate
	 * @param betweenEdate
	 * @param sections
	 * @param drgCodes
	 * @param dataFormats
	 * @param funcTypes
	 * @param medNames
	 * @param icdcms
	 * @param medLogCodes
	 * @param applMin
	 * @param applMax
	 * @param icdAll
	 * @param payCode
	 * @param inhCode
	 * @param isShowDRGList
	 * @param isLastM
	 * @param isLastY
	 * @return
	 * @throws ParseException
	 */
	@SuppressWarnings("deprecation")
	public Map<String, Object> getDrgQueryCondition(String dateTypes, String year, String month, String betweenSdate,
			String betweenEdate, String sections, String drgCodes, String dataFormats, String funcTypes,
			String medNames, String icdcms, String medLogCodes, int applMin, int applMax, String icdAll, String payCode,
			String inhCode, boolean isShowDRGList, boolean isLastM, boolean isLastY) throws ParseException {

//		DrgQueryConditionPayload result = new DrgQueryConditionPayload();
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> sqlMapList = new ArrayList<Map<String, Object>>();
		/// 顯示區間
		List<String> sectionList = new ArrayList<String>();
		String sectionSql = "";
		/// 指定DRG代碼
		List<String> drgCodeList = new ArrayList<String>();
		String drgCodeSql = "";
		/// 就醫類別
		List<String> dataformatList = new ArrayList<String>();
		String dateformatSql = "";
		/// 科別
		List<String> funcTypeList = new ArrayList<String>();
		String funcTypeSql = "";
		/// 醫護姓名
		List<String> medNameList = new ArrayList<String>();
		String medNameSql = "";
		/// 病歷編號
		List<String> icdcmList = new ArrayList<String>();
		String icdcmSql = "";
		/// 就醫紀錄編號
		List<String> medLogCodeList = new ArrayList<String>();
		String medLogCodeSql = "";
		/// 不分區ICD碼
		List<String> icdAllList = new ArrayList<String>();
		String icdAllSql = "";
		/// 如果section有值
		if (sections.length() > 0) {
			String[] sectionArr = StringUtility.splitBySpace(sections);
			for (String str : sectionArr) {
				sectionList.add(str);
				sectionSql += "'" + str + "',";
			}
			sectionSql = sectionSql.substring(0, sectionSql.length() - 1);
		}
		/// 如果drgCodes有值
		if (drgCodes != null && drgCodes.length() > 0) {
			String[] drgCodeArr = StringUtility.splitBySpace(drgCodes);
			for (String str : drgCodeArr) {
				drgCodeList.add(str);
				drgCodeSql += "'" + str + "',";
			}
			drgCodeSql = drgCodeSql.substring(0, drgCodeSql.length() - 1);
		}
		/// 如果dataformat有值
		if (dataFormats != null && dataFormats.length() > 0) {
			String[] dataformatArr = StringUtility.splitBySpace(dataFormats);
			for (String str : dataformatArr) {
				dataformatList.add(str);
			}
		}
		/// 如果functype有值
		if (funcTypes != null && funcTypes.length() > 0) {
			String[] funcTypeArr = StringUtility.splitBySpace(funcTypes);
			for (String str : funcTypeArr) {
				funcTypeList.add(str);
				funcTypeSql += "'" + str + "',";
			}
			funcTypeSql = funcTypeSql.substring(0, funcTypeSql.length() - 1);
		}
		/// 如果medNames有值
		if (medNames != null && medNames.length() > 0) {
			String[] medNameArr = StringUtility.splitBySpace(medNames);
			for (String str : medNameArr) {
				medNameList.add(str);
				medNameSql += "'" + str + "',";
			}
			medNameSql = medNameSql.substring(0, medNameSql.length() - 1);
		}
		/// 如果icdcm有值
		if (icdcms != null && icdcms.length() > 0) {
			String[] icdcmArr = StringUtility.splitBySpace(icdcms);
			for (String str : icdcmArr) {
				icdcmList.add(str);
				icdcmSql += "'" + str + "',";
			}
			icdcmSql = icdcmSql.substring(0, icdcmSql.length() - 1);
		}
		/// 如果medLogCodes有值
		if (medLogCodes != null && medLogCodes.length() > 0) {
			String[] medLogCodesArr = StringUtility.splitBySpace(medLogCodes);
			for (String str : medLogCodesArr) {
				medLogCodeList.add(str);
				medLogCodeSql += "'" + str + "',";
			}
			medLogCodeSql = medLogCodeSql.substring(0, medLogCodeSql.length() - 1);
		}
		/// 如果icdAll有值
		if (icdAll != null && icdAll.length() > 0) {
			String[] icdAllArr = StringUtility.splitBySpace(icdAll);
			for (String str : icdAllArr) {
				icdAllList.add(str);
			}
		}
		/// 日期格式: 0=年月帶入，1=日期區間
		if (dateTypes.equals("0")) {
			String[] years = StringUtility.splitBySpace(year);
			String[] months = StringUtility.splitBySpace(month);

			List<Object> yList = Arrays.asList(years);
			List<Object> mList = Arrays.asList(months);
			Map<String, Object> map = new HashMap<String, Object>();
			List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
			/// 如果年月為多個，則不能用上個月同條件相比
			if (years.length > 1) {
				isLastM = false;
			}
			if (isLastM) {
				for (String str : months) {
					int m = Integer.valueOf(str.replace("0", ""));
					int y = Integer.valueOf(years[0]);
					/// 如果是一月的話
					if (m == 1) {
						y -= 1;
						/// 跨年份
						yList.add(y);
						mList.add(12);
						map.put("YM", String.valueOf(y * 100 + 12));
						map.put("Value", "M");
						String append = String.valueOf((y + 1) * 100 + m);
						append = append.substring(0, append.length() - 2) + "/"
								+ append.substring(append.length() - 2, append.length());
						map.put("displayName", "上個月同條件相比");
					} else {
						yList.add(years[0]);
						mList.add(m - 1);
						map.put("YM", String.valueOf((y * 100) + (m - 1)));
						map.put("Value", "M");
						String append = String.valueOf((y) * 100 + m);
						append = append.substring(0, append.length() - 2) + "/"
								+ append.substring(append.length() - 2, append.length());
						map.put("displayName", "上個月同條件相比");
					}
				}
				mapList.add(map);
				map = new HashMap<String, Object>();
			}

			if (isLastY) {
				int i = 0;
				for (String str : years) {
					int y = Integer.valueOf(str);
					int m = Integer.valueOf(months[i].replace("0", ""));
					y -= 1;
					yList.add(y);
					mList.add(m);
					map.put("YM", String.valueOf((y * 100) + m));
					map.put("Value", "Y");
					String append = String.valueOf((y + 1) * 100 + m);
					append = append.substring(0, append.length() - 2) + "/"
							+ append.substring(append.length() - 2, append.length());
					map.put("displayName", "去年同期時段相比");
					mapList.add(map);
					map = new HashMap<String, Object>();
					i++;

				}
			}
			System.out.println(mapList.toString());
			List<String> sList = new ArrayList<String>();
			for (int i = 0; i < yList.size(); i++) {
				sList.add(yList.get(i).toString() + mList.get(i).toString());
			}

			List<Integer> yearMonthBetweenInt = findYearMonth(yList, mList);
			List<String> yearMonthBetweenStr = findYearMonthStr(yList, mList);
			/// 這裡做排序，name才會對應正確值
			Collections.sort(yearMonthBetweenInt);
			Collections.sort(yearMonthBetweenStr);

			/// 查詢欄位
			StringBuffer selectColumn = new StringBuffer("");
			/// 條件
			StringBuffer where = new StringBuffer("");
			/// groupBy
			StringBuffer groupBy = new StringBuffer("");
			/// orderBy
			StringBuffer orderBy = new StringBuffer("");

			for (int i = 0; i < yearMonthBetweenStr.size(); i++) {
				selectColumn.append(" SELECT  '" + yearMonthBetweenStr.get(i)
						+ "' AS DATE, '' AS DRG_CODE, DRG_QUANTITY, DRG_APPL_POINT,  DRG_ACTUAL_POINT FROM ");
				selectColumn.append(" (SELECT SUM(T) AS DRG_QUANTITY FROM ");
				selectColumn.append(
						" (SELECT COUNT(1) AS T  FROM MR m  WHERE Data_format = '20' AND drg_section IS NOT NULL  and MR_END_DATE LIKE CONCAT('"
								+ yearMonthBetweenStr.get(i) + "','%') ");
				if (drgCodeList.size() > 0)
					where.append(" AND DRG_CODE IN (" + drgCodeSql + ") ");
				if (funcTypeList.size() > 0)
					where.append(" AND FUNC_TYPE IN (" + funcTypeSql + ") ");
				if (medNameList.size() > 0)
					where.append(" AND PRSN_ID IN (" + medNameSql + ") ");
				if (icdcmList.size() > 0)
					where.append(" AND ICDCM1 IN (" + icdcmSql + ") ");
				if (medLogCodeList.size() > 0)
					where.append(" AND INH_CLINIC_ID IN (" + medLogCodeSql + ") ");
				if (icdAllList.size() > 0) {
					for (String str : icdAllList) {
						where.append(" AND ICD_ALL LIKE CONCAT(CONCAT('%','" + str + "'),'%') ");
					}
				}
				if (payCode != null && payCode.length() > 0)
					where.append(" AND CODE_ALL LIKE CONCAT(CONCAT('%','" + payCode + "'),'%') ");
				if (inhCode != null && inhCode.length() > 0)
					where.append(" AND INH_CODE LIKE CONCAT(CONCAT('%','" + inhCode + "'),'%') ");

				selectColumn.append(where);
				where = new StringBuffer("");
				selectColumn.append(" ))a, ");
				selectColumn.append(
						" (SELECT SUM(IP_D.APPL_DOT + IP_D.PART_DOT) AS DRG_APPL_POINT FROM MR, IP_D WHERE IP_D.MR_ID = MR.ID AND  MR.DATA_FORMAT  = '20' AND MR.DRG_SECTION IS NOT NULL  and MR.MR_END_DATE LIKE CONCAT('"
								+ yearMonthBetweenStr.get(i) + "','%') ");
				if (drgCodeList.size() > 0)
					where.append(" AND MR.DRG_CODE IN (" + drgCodeSql + ") ");
				if (funcTypeList.size() > 0)
					where.append(" AND MR.FUNC_TYPE IN (" + funcTypeSql + ") ");
				if (medNameList.size() > 0)
					where.append(" AND MR.PRSN_ID IN (" + medNameSql + ") ");
				if (icdcmList.size() > 0)
					where.append(" AND MR.ICDCM1 IN (" + icdcmSql + ") ");
				if (medLogCodeList.size() > 0)
					where.append(" AND MR.INH_CLINIC_ID IN (" + medLogCodeSql + ") ");
				if (icdAllList.size() > 0) {
					for (String str : icdAllList) {
						where.append(" AND MR.ICD_ALL LIKE CONCAT(CONCAT('%','" + str + "'),'%') ");
					}
				}
				if (payCode != null && payCode.length() > 0)
					where.append(" AND MR.CODE_ALL LIKE CONCAT(CONCAT('%','" + payCode + "'),'%') ");
				if (inhCode != null && inhCode.length() > 0)
					where.append(" AND MR.INH_CODE LIKE CONCAT(CONCAT('%','" + inhCode + "'),'%') ");

				selectColumn.append(where);
				where = new StringBuffer("");
				selectColumn.append(" )b, ");
				selectColumn.append(
						" (SELECT SUM(IP_D.MED_DOT + IP_D.NON_APPL_DOT) AS DRG_ACTUAL_POINT FROM MR, IP_D WHERE IP_D.MR_ID = MR.ID AND  MR.DATA_FORMAT  = '20' AND MR.DRG_SECTION IS NOT NULL  and MR.MR_END_DATE LIKE CONCAT('"
								+ yearMonthBetweenStr.get(i) + "','%') ");
				if (drgCodeList.size() > 0)
					where.append(" AND MR.DRG_CODE IN (" + drgCodeSql + ") ");
				if (funcTypeList.size() > 0)
					where.append(" AND MR.FUNC_TYPE IN (" + funcTypeSql + ") ");
				if (medNameList.size() > 0)
					where.append(" AND MR.PRSN_ID IN (" + medNameSql + ") ");
				if (icdcmList.size() > 0)
					where.append(" AND MR.ICDCM1 IN (" + icdcmSql + ") ");
				if (medLogCodeList.size() > 0)
					where.append(" AND MR.INH_CLINIC_ID IN (" + medLogCodeSql + ") ");
				if (icdAllList.size() > 0) {
					for (String str : icdAllList) {
						where.append(" AND MR.ICD_ALL LIKE CONCAT(CONCAT('%','" + str + "'),'%') ");
					}
				}
				if (payCode != null && payCode.length() > 0)
					where.append(" AND MR.CODE_ALL LIKE CONCAT(CONCAT('%','" + payCode + "'),'%') ");
				if (inhCode != null && inhCode.length() > 0)
					where.append(" AND MR.INH_CODE LIKE CONCAT(CONCAT('%','" + inhCode + "'),'%') ");

				selectColumn.append(where);
				where = new StringBuffer("");
				selectColumn.append(" )c ");
				if (!isShowDRGList && drgCodeList.size() == 0) {
					if (applMin > 0)
						where.append(" WHERE b.DRG_APPL_POINT >= " + applMin + " ");
					if (applMax > 0)
						where.append(" AND b.DRG_APPL_POINT <= " + applMax + " ");

					selectColumn.append(where);
					where = new StringBuffer("");
				}
				if (isShowDRGList || drgCodeList.size() > 0) {
					selectColumn.append(" UNION ALL ");
					selectColumn.append(" SELECT '" + yearMonthBetweenStr.get(i)
							+ "' AS DATE, DRG_CODE,DRG_QUANTITY,DRG_APPL_POINT, DRG_ACTUAL_POINT FROM ");
					selectColumn.append(
							" (SELECT a.DRG_CODE AS DRG_CODE, a.DRG_QUANTITY AS DRG_QUANTITY, b.DRG_APPL_POINT AS DRG_APPL_POINT, c.DRG_ACTUAL_POINT  AS DRG_ACTUAL_POINT FROM ");
					selectColumn.append(
							" (SELECT  COUNT(1) AS DRG_QUANTITY, DRG_CODE  FROM MR m  WHERE Data_format = '20' AND drg_section IS NOT NULL  and MR_END_DATE LIKE CONCAT('"
									+ yearMonthBetweenStr.get(i) + "','%') ");
					if (drgCodeList.size() > 0)
						where.append(" AND DRG_CODE IN (" + drgCodeSql + ") ");
					if (funcTypeList.size() > 0)
						where.append(" AND FUNC_TYPE IN (" + funcTypeSql + ") ");
					if (medNameList.size() > 0)
						where.append(" AND PRSN_ID IN (" + medNameSql + ") ");
					if (icdcmList.size() > 0)
						where.append(" AND ICDCM1 IN (" + icdcmSql + ") ");
					if (medLogCodeList.size() > 0)
						where.append(" AND INH_CLINIC_ID IN (" + medLogCodeSql + ") ");
					if (icdAllList.size() > 0) {
						for (String str : icdAllList) {
							where.append(" AND ICD_ALL LIKE CONCAT(CONCAT('%','" + str + "'),'%') ");
						}
					}
					if (payCode != null && payCode.length() > 0)
						where.append(" AND CODE_ALL LIKE CONCAT(CONCAT('%','" + payCode + "'),'%') ");
					if (inhCode != null && inhCode.length() > 0)
						where.append(" AND INH_CODE LIKE CONCAT(CONCAT('%','" + inhCode + "'),'%') ");
					selectColumn.append(where);
					groupBy.append(" GROUP BY DRG_CODE)a, ");
					selectColumn.append(groupBy);
					where = new StringBuffer("");
					groupBy = new StringBuffer("");

					selectColumn.append(
							" (SELECT MR.DRG_CODE, SUM(IP_D.APPL_DOT + IP_D.PART_DOT) AS DRG_APPL_POINT  FROM MR, IP_D WHERE IP_D.MR_ID = MR.ID AND  MR.DATA_FORMAT  = '20' AND MR.DRG_SECTION IS NOT NULL  and MR.MR_END_DATE LIKE CONCAT('"
									+ yearMonthBetweenStr.get(i) + "','%') ");
					if (drgCodeList.size() > 0)
						where.append(" AND MR.DRG_CODE IN (" + drgCodeSql + ") ");
					if (funcTypeList.size() > 0)
						where.append(" AND MR.FUNC_TYPE IN (" + funcTypeSql + ") ");
					if (medNameList.size() > 0)
						where.append(" AND MR.PRSN_ID IN (" + medNameSql + ") ");
					if (icdcmList.size() > 0)
						where.append(" AND MR.ICDCM1 IN (" + icdcmSql + ") ");
					if (medLogCodeList.size() > 0)
						where.append(" AND MR.INH_CLINIC_ID IN (" + medLogCodeSql + ") ");
					if (icdAllList.size() > 0) {
						for (String str : icdAllList) {
							where.append(" AND MR.ICD_ALL LIKE CONCAT(CONCAT('%','" + str + "'),'%') ");
						}
					}
					if (payCode != null && payCode.length() > 0)
						where.append(" AND MR.CODE_ALL LIKE CONCAT(CONCAT('%','" + payCode + "'),'%') ");
					if (inhCode != null && inhCode.length() > 0)
						where.append(" AND MR.INH_CODE LIKE CONCAT(CONCAT('%','" + inhCode + "'),'%') ");
					selectColumn.append(where);
					groupBy.append(" GROUP BY DRG_CODE)b, ");
					selectColumn.append(groupBy);
					where = new StringBuffer("");
					groupBy = new StringBuffer("");

					selectColumn.append(
							" (SELECT MR.DRG_CODE, SUM(IP_D.MED_DOT + IP_D.NON_APPL_DOT) AS DRG_ACTUAL_POINT  FROM MR, IP_D WHERE IP_D.MR_ID = MR.ID AND  MR.DATA_FORMAT  = '20' AND MR.DRG_SECTION IS NOT NULL  and MR.MR_END_DATE LIKE CONCAT('"
									+ yearMonthBetweenStr.get(i) + "','%') ");
					if (drgCodeList.size() > 0)
						where.append(" AND MR.DRG_CODE IN (" + drgCodeSql + ") ");
					if (funcTypeList.size() > 0)
						where.append(" AND MR.FUNC_TYPE IN (" + funcTypeSql + ") ");
					if (medNameList.size() > 0)
						where.append(" AND MR.PRSN_ID IN (" + medNameSql + ") ");
					if (icdcmList.size() > 0)
						where.append(" AND MR.ICDCM1 IN (" + icdcmSql + ") ");
					if (medLogCodeList.size() > 0)
						where.append(" AND MR.INH_CLINIC_ID IN (" + medLogCodeSql + ") ");
					if (icdAllList.size() > 0) {
						for (String str : icdAllList) {
							where.append(" AND MR.ICD_ALL LIKE CONCAT(CONCAT('%','" + str + "'),'%') ");
						}
					}
					if (payCode != null && payCode.length() > 0)
						where.append(" AND MR.CODE_ALL LIKE CONCAT(CONCAT('%','" + payCode + "'),'%') ");
					if (inhCode != null && inhCode.length() > 0)
						where.append(" AND MR.INH_CODE LIKE CONCAT(CONCAT('%','" + inhCode + "'),'%') ");
					selectColumn.append(where);
					groupBy.append(" GROUP BY DRG_CODE)c ");
					selectColumn.append(groupBy);
					where = new StringBuffer("");
					groupBy = new StringBuffer("");

					selectColumn.append(" WHERE  a.DRG_CODE = b.DRG_CODE AND  b.DRG_CODE = c.DRG_CODE )t ");
					if (applMin > 0)
						where.append(" WHERE DRG_APPL_POINT >= " + applMin + " ");
					if (applMax > 0)
						where.append(" AND DRG_APPL_POINT <= " + applMax + " ");
					selectColumn.append(where);
					orderBy.append(" ORDER BY DRG_CODE ");
					selectColumn.append(orderBy);
					where = new StringBuffer("");
					orderBy = new StringBuffer("");
				}
				/// 傳統sql語法組成資料
				Query sqlQuery = entityManager.createNativeQuery(selectColumn.toString());
				sqlQuery.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> dataMap = sqlQuery.getResultList();
				sqlMapList.addAll(dataMap);
				selectColumn = new StringBuffer("");
				entityManager.close();
			}
			if (mapList.size() > 0) {
				for (int j = 0; j < mapList.size(); j++) {
					String ym = mapList.get(j).get("YM").toString();
					String displayName = mapList.get(j).get("displayName").toString();
					for (int i = 0; i < sqlMapList.size(); i++) {
						String date = sqlMapList.get(i).get("DATE").toString().replace("-", "");
						String drgcode = sqlMapList.get(i).get("DRG_CODE").toString();
						if (ym.equals(date)) {
							if (drgcode.length() == 0) {
								sqlMapList.get(i).put("disPlayName", displayName);
							} else {
								sqlMapList.get(i).put("disPlayName", "");
							}
							continue;
						}
						if (sqlMapList.get(i).get("disPlayName") == null) {
							sqlMapList.get(i).put("disPlayName", "");
						}
					}
				}
			}
			/// 跑新sql先初始化
			selectColumn = new StringBuffer("");
			where = new StringBuffer("");
			groupBy = new StringBuffer("");
			orderBy = new StringBuffer("");
			for (int i = 0; i < yearMonthBetweenStr.size(); i++) {
				selectColumn.append(" SELECT '" + yearMonthBetweenStr.get(i)
						+ "' AS DATE, '' AS DRG_CODE,DRG_SECTION,DRG_QUANTITY,DRG_APPL_POINT,DRG_ACTUAL_POINT FROM  ");
				selectColumn.append(
						" (SELECT MR.DRG_SECTION  AS DRG_SECTION, COUNT(1) AS DRG_QUANTITY, SUM(IP_D.APPL_DOT + IP_D.PART_DOT) AS DRG_APPL_POINT, SUM(IP_D.MED_DOT + IP_D.NON_APPL_DOT) "
								+ "AS DRG_ACTUAL_POINT FROM MR, IP_D WHERE DRG_SECTION IS NOT  NULL AND DATA_FORMAT = '20' AND MR_END_DATE LIKE CONCAT('"
								+ yearMonthBetweenStr.get(i) + "','%') " + "AND IP_D.MR_ID = MR.ID ");
				if (drgCodeList.size() > 0)
					where.append(" AND MR.DRG_CODE IN (" + drgCodeSql + ") ");
				if (funcTypeList.size() > 0)
					where.append(" AND MR.FUNC_TYPE IN (" + funcTypeSql + ") ");
				if (medNameList.size() > 0)
					where.append(" AND MR.PRSN_ID IN (" + medNameSql + ") ");
				if (icdcmList.size() > 0)
					where.append(" AND MR.ICDCM1 IN (" + icdcmSql + ") ");
				if (medLogCodeList.size() > 0)
					where.append(" AND MR.INH_CLINIC_ID IN (" + medLogCodeSql + ") ");
				if (icdAllList.size() > 0) {
					for (String str : icdAllList) {
						where.append(" AND MR.ICD_ALL LIKE CONCAT(CONCAT('%','" + str + "'),'%') ");
					}
				}
				if (payCode != null && payCode.length() > 0)
					where.append(" AND MR.CODE_ALL LIKE CONCAT(CONCAT('%','" + payCode + "'),'%') ");
				if (inhCode != null && inhCode.length() > 0)
					where.append(" AND MR.INH_CODE LIKE CONCAT(CONCAT('%','" + inhCode + "'),'%') ");
				if (sectionList.size() > 0) {
					where.append(" AND MR.DRG_SECTION IN (" + sectionSql + ") ");
				}

				selectColumn.append(where);
				groupBy.append(" GROUP BY MR.DRG_SECTION ");
				selectColumn.append(groupBy);
				orderBy.append(" ORDER BY MR.DRG_SECTION)n ");
				selectColumn.append(orderBy);

				where = new StringBuffer("");
				groupBy = new StringBuffer("");
				orderBy = new StringBuffer("");
				if (!isShowDRGList && drgCodeList.size() == 0) {
					if (applMin > 0)
						where.append(" WHERE DRG_APPL_POINT >= " + applMin + " ");
					if (applMax > 0)
						where.append(" AND DRG_APPL_POINT <= " + applMax + " ");

					selectColumn.append(where);
					where = new StringBuffer("");
				}
				if (isShowDRGList || drgCodeList.size() > 0) {
					selectColumn.append(" UNION ALL ");
					selectColumn.append(" SELECT '" + yearMonthBetweenStr.get(i)
							+ "' AS DATE, DRG_CODE,DRG_SECTION,DRG_QUANTITY,DRG_APPL_POINT,DRG_ACTUAL_POINT FROM ");
					selectColumn.append(
							" (SELECT MR.DRG_CODE, MR.DRG_SECTION  AS DRG_SECTION, COUNT(1) AS DRG_QUANTITY, SUM(IP_D.APPL_DOT + IP_D.PART_DOT) AS DRG_APPL_POINT, SUM(IP_D.MED_DOT + IP_D.NON_APPL_DOT) AS DRG_ACTUAL_POINT "
									+ "FROM MR, IP_D WHERE DRG_SECTION IS NOT  NULL AND DATA_FORMAT = '20' AND MR_END_DATE LIKE CONCAT('2020-06','%') "
									+ "AND IP_D.MR_ID = MR.ID ");
					if (drgCodeList.size() > 0)
						where.append(" AND MR.DRG_CODE IN (" + drgCodeSql + ") ");
					if (funcTypeList.size() > 0)
						where.append(" AND MR.FUNC_TYPE IN (" + funcTypeSql + ") ");
					if (medNameList.size() > 0)
						where.append(" AND MR.PRSN_ID IN (" + medNameSql + ") ");
					if (icdcmList.size() > 0)
						where.append(" AND MR.ICDCM1 IN (" + icdcmSql + ") ");
					if (medLogCodeList.size() > 0)
						where.append(" AND MR.INH_CLINIC_ID IN (" + medLogCodeSql + ") ");
					if (icdAllList.size() > 0) {
						for (String str : icdAllList) {
							where.append(" AND MR.ICD_ALL LIKE CONCAT(CONCAT('%','" + str + "'),'%') ");
						}
					}
					if (payCode != null && payCode.length() > 0)
						where.append(" AND MR.CODE_ALL LIKE CONCAT(CONCAT('%','" + payCode + "'),'%') ");
					if (inhCode != null && inhCode.length() > 0)
						where.append(" AND MR.INH_CODE LIKE CONCAT(CONCAT('%','" + inhCode + "'),'%') ");
					if (sectionList.size() > 0) {
						where.append(" AND MR.DRG_SECTION IN (" + sectionSql + ") ");
					}

					selectColumn.append(where);
					groupBy.append(" GROUP BY MR.DRG_SECTION,MR.DRG_CODE ");
					selectColumn.append(groupBy);
					orderBy.append(" ORDER BY MR.DRG_CODE) n ");
					selectColumn.append(orderBy);
					where = new StringBuffer("");
					groupBy = new StringBuffer("");
					orderBy = new StringBuffer("");

					if (applMin > 0)
						where.append(" WHERE DRG_APPL_POINT >= " + applMin + " ");
					if (applMax > 0)
						where.append(" AND DRG_APPL_POINT <= " + applMax + " ");
					selectColumn.append(where);
					orderBy.append(" ORDER BY DRG_CODE ");
					selectColumn.append(orderBy);
					where = new StringBuffer("");
					orderBy = new StringBuffer("");
				}
				/// 傳統sql語法組成資料
				Query sqlQuery = entityManager.createNativeQuery(selectColumn.toString());
				sqlQuery.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> dataMap = sqlQuery.getResultList();
				for (int y = 0; y < dataMap.size(); y++) {
					String yd = dataMap.get(y).get("DATE").toString();
					String ydc = dataMap.get(y).get("DRG_CODE").toString();
					for (int z = 0; z < sqlMapList.size(); z++) {
						String zd = sqlMapList.get(z).get("DATE").toString();
						String zdc = sqlMapList.get(z).get("DRG_CODE").toString();
						if (yd.equals(zd) && ydc.equals(zdc)) {

							if (dataMap.get(y).get("DRG_SECTION") != null) {
								String area = dataMap.get(y).get("DRG_SECTION").toString();
								switch (area) {
								case "A":
									sqlMapList.get(z).put("SECTION_A",
											Long.parseLong(dataMap.get(y).get("DRG_QUANTITY").toString()));
									sqlMapList.get(z).put("SECTION_A_APPL",
											Long.parseLong(dataMap.get(y).get("DRG_APPL_POINT").toString()));
									sqlMapList.get(z).put("SECTION_A_ACTUAL",
											Long.parseLong(dataMap.get(y).get("DRG_ACTUAL_POINT").toString()));
									sqlMapList.get(z).put("DIFFA",
											Long.parseLong(dataMap.get(y).get("DRG_APPL_POINT").toString()) - Long
													.parseLong(dataMap.get(y).get("DRG_ACTUAL_POINT").toString()));
									break;
								case "B1":
									sqlMapList.get(z).put("SECTION_B1",
											Long.parseLong(dataMap.get(y).get("DRG_QUANTITY").toString()));
									sqlMapList.get(z).put("SECTION_B1_APPL",
											Long.parseLong(dataMap.get(y).get("DRG_APPL_POINT").toString()));
									sqlMapList.get(z).put("SECTION_B1_ACTUAL",
											Long.parseLong(dataMap.get(y).get("DRG_ACTUAL_POINT").toString()));
									sqlMapList.get(z).put("DIFFB1",
											Long.parseLong(dataMap.get(y).get("DRG_APPL_POINT").toString()) - Long
													.parseLong(dataMap.get(y).get("DRG_ACTUAL_POINT").toString()));
									break;
								case "B2":
									sqlMapList.get(z).put("SECTION_B2",
											Long.parseLong(dataMap.get(y).get("DRG_QUANTITY").toString()));
									sqlMapList.get(z).put("SECTION_B2_APPL",
											Long.parseLong(dataMap.get(y).get("DRG_APPL_POINT").toString()));
									sqlMapList.get(z).put("SECTION_B2_ACTUAL",
											Long.parseLong(dataMap.get(y).get("DRG_ACTUAL_POINT").toString()));
									sqlMapList.get(z).put("DIFFB2",
											Long.parseLong(dataMap.get(y).get("DRG_APPL_POINT").toString()) - Long
													.parseLong(dataMap.get(y).get("DRG_ACTUAL_POINT").toString()));
									break;
								case "C":
									sqlMapList.get(z).put("SECTION_C",
											Long.parseLong(dataMap.get(y).get("DRG_QUANTITY").toString()));
									sqlMapList.get(z).put("SECTION_C_APPL",
											Long.parseLong(dataMap.get(y).get("DRG_APPL_POINT").toString()));
									sqlMapList.get(z).put("SECTION_C_ACTUAL",
											Long.parseLong(dataMap.get(y).get("DRG_ACTUAL_POINT").toString()));
									sqlMapList.get(z).put("DIFFC",
											Long.parseLong(dataMap.get(y).get("DRG_APPL_POINT").toString()) - Long
													.parseLong(dataMap.get(y).get("DRG_ACTUAL_POINT").toString()));
									break;
								}
							}
						}
					}
				}
				selectColumn = new StringBuffer("");
				entityManager.close();
			}
			if (mapList.size() > 0) {
				for (int j = 0; j < mapList.size(); j++) {
					String ym = mapList.get(j).get("YM").toString();
					String displayName = mapList.get(j).get("displayName").toString();
					for (int i = 0; i < sqlMapList.size(); i++) {
						String date = sqlMapList.get(i).get("DATE").toString().replace("-", "");
						String drgcode = sqlMapList.get(i).get("DRG_CODE").toString();
						if (ym.equals(date)) {
							if (drgcode.length() == 0) {
								sqlMapList.get(i).put("disPlayName", displayName);
							} else {
								sqlMapList.get(i).put("disPlayName", "");
							}
							continue;
						}
						if (sqlMapList.get(i).get("disPlayName") == null) {
							sqlMapList.get(i).put("disPlayName", "");
						}
					}
				}
			}

		} else {
			/// 查詢日期為指定區間
			Map<String, Object> map = new HashMap<String, Object>();
			List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();

			map.put("sDate", betweenSdate);
			map.put("eDate", betweenEdate);
			map.put("displayName", "");
			mapList.add(map);
			map = new HashMap<String, Object>();
			if (isLastY) {
				String sdate = betweenSdate;
				String edate = betweenEdate;
				int sy = Integer.parseInt(sdate.substring(0, 4));
				int ey = Integer.parseInt(edate.substring(0, 4));
				sdate = String.valueOf(sy - 1) + betweenSdate.substring(4, betweenSdate.length());
				edate = String.valueOf(ey - 1) + betweenEdate.substring(4, betweenEdate.length());
				map.put("sDate", sdate);
				map.put("eDate", edate);
				map.put("displayName", "去年同期時段相比");
				mapList.add(map);
				map = new HashMap<String, Object>();
			}

			/// 查詢欄位
			StringBuffer selectColumn = new StringBuffer("");
			/// 條件
			StringBuffer where = new StringBuffer("");
			/// groupBy
			StringBuffer groupBy = new StringBuffer("");
			/// orderBy
			StringBuffer orderBy = new StringBuffer("");
			for (int i = 0; i < mapList.size(); i++) {
				selectColumn.append(" SELECT  '" + mapList.get(i).get("sDate") + " " + mapList.get(i).get("eDate")
						+ "' AS DATE, '' AS DRG_CODE, DRG_QUANTITY, DRG_APPL_POINT,  DRG_ACTUAL_POINT FROM ");
				selectColumn.append(" (SELECT SUM(T) AS DRG_QUANTITY FROM ");
				selectColumn.append(
						" (SELECT COUNT(1) AS T  FROM MR m  WHERE Data_format = '20' AND drg_section IS NOT NULL  and MR_END_DATE BETWEEN '"
								+ mapList.get(i).get("sDate") + "' AND '" + mapList.get(i).get("eDate") + "'  ");
				if (drgCodeList.size() > 0)
					where.append(" AND DRG_CODE IN (" + drgCodeSql + ") ");
				if (funcTypeList.size() > 0)
					where.append(" AND FUNC_TYPE IN (" + funcTypeSql + ") ");
				if (medNameList.size() > 0)
					where.append(" AND PRSN_ID IN (" + medNameSql + ") ");
				if (icdcmList.size() > 0)
					where.append(" AND ICDCM1 IN (" + icdcmSql + ") ");
				if (medLogCodeList.size() > 0)
					where.append(" AND INH_CLINIC_ID IN (" + medLogCodeSql + ") ");
				if (icdAllList.size() > 0) {
					for (String str : icdAllList) {
						where.append(" AND ICD_ALL LIKE CONCAT(CONCAT('%','" + str + "'),'%') ");
					}
				}
				if (payCode != null && payCode.length() > 0)
					where.append(" AND CODE_ALL LIKE CONCAT(CONCAT('%','" + payCode + "'),'%') ");
				if (inhCode != null && inhCode.length() > 0)
					where.append(" AND INH_CODE LIKE CONCAT(CONCAT('%','" + inhCode + "'),'%') ");

				selectColumn.append(where);
				where = new StringBuffer("");
				selectColumn.append(" ))a, ");
				selectColumn.append(
						" (SELECT SUM(IP_D.APPL_DOT + IP_D.PART_DOT) AS DRG_APPL_POINT FROM MR, IP_D WHERE IP_D.MR_ID = MR.ID AND  MR.DATA_FORMAT  = '20' AND MR.DRG_SECTION IS NOT NULL  and MR.MR_END_DATE BETWEEN '"
								+ mapList.get(i).get("sDate") + "' AND '" + mapList.get(i).get("eDate") + "' ");
				if (drgCodeList.size() > 0)
					where.append(" AND MR.DRG_CODE IN (" + drgCodeSql + ") ");
				if (funcTypeList.size() > 0)
					where.append(" AND MR.FUNC_TYPE IN (" + funcTypeSql + ") ");
				if (medNameList.size() > 0)
					where.append(" AND MR.PRSN_ID IN (" + medNameSql + ") ");
				if (icdcmList.size() > 0)
					where.append(" AND MR.ICDCM1 IN (" + icdcmSql + ") ");
				if (medLogCodeList.size() > 0)
					where.append(" AND MR.INH_CLINIC_ID IN (" + medLogCodeSql + ") ");
				if (icdAllList.size() > 0) {
					for (String str : icdAllList) {
						where.append(" AND MR.ICD_ALL LIKE CONCAT(CONCAT('%','" + str + "'),'%') ");
					}
				}
				if (payCode != null && payCode.length() > 0)
					where.append(" AND MR.CODE_ALL LIKE CONCAT(CONCAT('%','" + payCode + "'),'%') ");
				if (inhCode != null && inhCode.length() > 0)
					where.append(" AND MR.INH_CODE LIKE CONCAT(CONCAT('%','" + inhCode + "'),'%') ");

				selectColumn.append(where);
				where = new StringBuffer("");
				selectColumn.append(" )b, ");
				selectColumn.append(
						" (SELECT SUM(IP_D.MED_DOT + IP_D.NON_APPL_DOT) AS DRG_ACTUAL_POINT FROM MR, IP_D WHERE IP_D.MR_ID = MR.ID AND  MR.DATA_FORMAT  = '20' AND MR.DRG_SECTION IS NOT NULL  and MR.MR_END_DATE BETWEEN '"
								+ mapList.get(i).get("sDate") + "' AND '" + mapList.get(i).get("eDate") + "' ");
				if (drgCodeList.size() > 0)
					where.append(" AND MR.DRG_CODE IN (" + drgCodeSql + ") ");
				if (funcTypeList.size() > 0)
					where.append(" AND MR.FUNC_TYPE IN (" + funcTypeSql + ") ");
				if (medNameList.size() > 0)
					where.append(" AND MR.PRSN_ID IN (" + medNameSql + ") ");
				if (icdcmList.size() > 0)
					where.append(" AND MR.ICDCM1 IN (" + icdcmSql + ") ");
				if (medLogCodeList.size() > 0)
					where.append(" AND MR.INH_CLINIC_ID IN (" + medLogCodeSql + ") ");
				if (icdAllList.size() > 0) {
					for (String str : icdAllList) {
						where.append(" AND MR.ICD_ALL LIKE CONCAT(CONCAT('%','" + str + "'),'%') ");
					}
				}
				if (payCode != null && payCode.length() > 0)
					where.append(" AND MR.CODE_ALL LIKE CONCAT(CONCAT('%','" + payCode + "'),'%') ");
				if (inhCode != null && inhCode.length() > 0)
					where.append(" AND MR.INH_CODE LIKE CONCAT(CONCAT('%','" + inhCode + "'),'%') ");

				selectColumn.append(where);
				where = new StringBuffer("");
				selectColumn.append(" )c ");
				if (!isShowDRGList && drgCodeList.size() == 0) {
					if (applMin > 0)
						where.append(" WHERE b.DRG_APPL_POINT >= " + applMin + " ");
					if (applMax > 0)
						where.append(" AND b.DRG_APPL_POINT <= " + applMax + " ");

					selectColumn.append(where);
					where = new StringBuffer("");
				}
				if (isShowDRGList || drgCodeList.size() > 0) {
					selectColumn.append(" UNION ALL ");
					selectColumn.append(" SELECT '" + mapList.get(i).get("sDate") + " " + mapList.get(i).get("eDate")
							+ "' AS DATE, DRG_CODE,DRG_QUANTITY,DRG_APPL_POINT, DRG_ACTUAL_POINT FROM ");
					selectColumn.append(
							" (SELECT a.DRG_CODE AS DRG_CODE, a.DRG_QUANTITY AS DRG_QUANTITY, b.DRG_APPL_POINT AS DRG_APPL_POINT, c.DRG_ACTUAL_POINT  AS DRG_ACTUAL_POINT FROM ");
					selectColumn.append(
							" (SELECT  COUNT(1) AS DRG_QUANTITY, DRG_CODE  FROM MR m  WHERE Data_format = '20' AND drg_section IS NOT NULL  and MR_END_DATE BETWEEN '"
									+ mapList.get(i).get("sDate") + "' AND '" + mapList.get(i).get("eDate") + "' ");
					if (drgCodeList.size() > 0)
						where.append(" AND DRG_CODE IN (" + drgCodeSql + ") ");
					if (funcTypeList.size() > 0)
						where.append(" AND FUNC_TYPE IN (" + funcTypeSql + ") ");
					if (medNameList.size() > 0)
						where.append(" AND PRSN_ID IN (" + medNameSql + ") ");
					if (icdcmList.size() > 0)
						where.append(" AND ICDCM1 IN (" + icdcmSql + ") ");
					if (medLogCodeList.size() > 0)
						where.append(" AND INH_CLINIC_ID IN (" + medLogCodeSql + ") ");
					if (icdAllList.size() > 0) {
						for (String str : icdAllList) {
							where.append(" AND ICD_ALL LIKE CONCAT(CONCAT('%','" + str + "'),'%') ");
						}
					}
					if (payCode != null && payCode.length() > 0)
						where.append(" AND CODE_ALL LIKE CONCAT(CONCAT('%','" + payCode + "'),'%') ");
					if (inhCode != null && inhCode.length() > 0)
						where.append(" AND INH_CODE LIKE CONCAT(CONCAT('%','" + inhCode + "'),'%') ");
					selectColumn.append(where);
					groupBy.append(" GROUP BY DRG_CODE)a, ");
					selectColumn.append(groupBy);
					where = new StringBuffer("");
					groupBy = new StringBuffer("");

					selectColumn.append(
							" (SELECT MR.DRG_CODE, SUM(IP_D.APPL_DOT + IP_D.PART_DOT) AS DRG_APPL_POINT  FROM MR, IP_D WHERE IP_D.MR_ID = MR.ID AND  MR.DATA_FORMAT  = '20' AND MR.DRG_SECTION IS NOT NULL  and MR.MR_END_DATE BETWEEN '"
									+ mapList.get(i).get("sDate") + "' AND '" + mapList.get(i).get("eDate") + "' ");
					if (drgCodeList.size() > 0)
						where.append(" AND MR.DRG_CODE IN (" + drgCodeSql + ") ");
					if (funcTypeList.size() > 0)
						where.append(" AND MR.FUNC_TYPE IN (" + funcTypeSql + ") ");
					if (medNameList.size() > 0)
						where.append(" AND MR.PRSN_ID IN (" + medNameSql + ") ");
					if (icdcmList.size() > 0)
						where.append(" AND MR.ICDCM1 IN (" + icdcmSql + ") ");
					if (medLogCodeList.size() > 0)
						where.append(" AND MR.INH_CLINIC_ID IN (" + medLogCodeSql + ") ");
					if (icdAllList.size() > 0) {
						for (String str : icdAllList) {
							where.append(" AND MR.ICD_ALL LIKE CONCAT(CONCAT('%','" + str + "'),'%') ");
						}
					}
					if (payCode != null && payCode.length() > 0)
						where.append(" AND MR.CODE_ALL LIKE CONCAT(CONCAT('%','" + payCode + "'),'%') ");
					if (inhCode != null && inhCode.length() > 0)
						where.append(" AND MR.INH_CODE LIKE CONCAT(CONCAT('%','" + inhCode + "'),'%') ");
					selectColumn.append(where);
					groupBy.append(" GROUP BY DRG_CODE)b, ");
					selectColumn.append(groupBy);
					where = new StringBuffer("");
					groupBy = new StringBuffer("");

					selectColumn.append(
							" (SELECT MR.DRG_CODE, SUM(IP_D.MED_DOT + IP_D.NON_APPL_DOT) AS DRG_ACTUAL_POINT  FROM MR, IP_D WHERE IP_D.MR_ID = MR.ID AND  MR.DATA_FORMAT  = '20' AND MR.DRG_SECTION IS NOT NULL  and MR.MR_END_DATE BETWEEN '"
									+ mapList.get(i).get("sDate") + "' AND '" + mapList.get(i).get("eDate") + "' ");
					if (drgCodeList.size() > 0)
						where.append(" AND MR.DRG_CODE IN (" + drgCodeSql + ") ");
					if (funcTypeList.size() > 0)
						where.append(" AND MR.FUNC_TYPE IN (" + funcTypeSql + ") ");
					if (medNameList.size() > 0)
						where.append(" AND MR.PRSN_ID IN (" + medNameSql + ") ");
					if (icdcmList.size() > 0)
						where.append(" AND MR.ICDCM1 IN (" + icdcmSql + ") ");
					if (medLogCodeList.size() > 0)
						where.append(" AND MR.INH_CLINIC_ID IN (" + medLogCodeSql + ") ");
					if (icdAllList.size() > 0) {
						for (String str : icdAllList) {
							where.append(" AND MR.ICD_ALL LIKE CONCAT(CONCAT('%','" + str + "'),'%') ");
						}
					}
					if (payCode != null && payCode.length() > 0)
						where.append(" AND MR.CODE_ALL LIKE CONCAT(CONCAT('%','" + payCode + "'),'%') ");
					if (inhCode != null && inhCode.length() > 0)
						where.append(" AND MR.INH_CODE LIKE CONCAT(CONCAT('%','" + inhCode + "'),'%') ");
					selectColumn.append(where);
					groupBy.append(" GROUP BY DRG_CODE)c ");
					selectColumn.append(groupBy);
					where = new StringBuffer("");
					groupBy = new StringBuffer("");

					selectColumn.append(" WHERE  a.DRG_CODE = b.DRG_CODE AND  b.DRG_CODE = c.DRG_CODE )t ");
					if (applMin > 0)
						where.append(" WHERE DRG_APPL_POINT >= " + applMin + " ");
					if (applMax > 0)
						where.append(" AND DRG_APPL_POINT <= " + applMax + " ");
					selectColumn.append(where);
					orderBy.append(" ORDER BY DRG_CODE ");
					selectColumn.append(orderBy);
					where = new StringBuffer("");
					orderBy = new StringBuffer("");
				}
				/// 傳統sql語法組成資料
				Query sqlQuery = entityManager.createNativeQuery(selectColumn.toString());
				sqlQuery.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> dataMap = sqlQuery.getResultList();
				sqlMapList.addAll(dataMap);
				selectColumn = new StringBuffer("");
				entityManager.close();
			}
			if (mapList.size() > 0) {
				for (int j = 0; j < mapList.size(); j++) {
					String ym = mapList.get(j).get("sDate").toString();
					String displayName = mapList.get(j).get("displayName").toString();
					for (int i = 0; i < sqlMapList.size(); i++) {
						String date = sqlMapList.get(i).get("DATE").toString();
						String drgcode = sqlMapList.get(i).get("DRG_CODE").toString();
						if (date.contains(ym)) {
							if (drgcode.length() == 0) {
								sqlMapList.get(i).put("disPlayName", displayName);
							} else {
								sqlMapList.get(i).put("disPlayName", "");
							}
							continue;
						}
						if (sqlMapList.get(i).get("disPlayName") == null) {
							sqlMapList.get(i).put("disPlayName", "");
						}
					}
				}
			}
			/// 跑新sql先初始化
			selectColumn = new StringBuffer("");
			where = new StringBuffer("");
			groupBy = new StringBuffer("");
			orderBy = new StringBuffer("");
			for (int i = 0; i < mapList.size(); i++) {
				selectColumn.append(" SELECT '" + mapList.get(i).get("sDate") + " " + mapList.get(i).get("eDate")
						+ "' AS DATE, '' AS DRG_CODE,DRG_SECTION,DRG_QUANTITY,DRG_APPL_POINT,DRG_ACTUAL_POINT FROM  ");
				selectColumn.append(
						" (SELECT MR.DRG_SECTION  AS DRG_SECTION, COUNT(1) AS DRG_QUANTITY, SUM(IP_D.APPL_DOT + IP_D.PART_DOT) AS DRG_APPL_POINT, SUM(IP_D.MED_DOT + IP_D.NON_APPL_DOT) "
								+ "AS DRG_ACTUAL_POINT FROM MR, IP_D WHERE DRG_SECTION IS NOT  NULL AND DATA_FORMAT = '20' AND MR_END_DATE BETWEEN '"
								+ mapList.get(i).get("sDate") + "' AND '" + mapList.get(i).get("eDate") + "' "
								+ "AND IP_D.MR_ID = MR.ID ");
				if (drgCodeList.size() > 0)
					where.append(" AND MR.DRG_CODE IN (" + drgCodeSql + ") ");
				if (funcTypeList.size() > 0)
					where.append(" AND MR.FUNC_TYPE IN (" + funcTypeSql + ") ");
				if (medNameList.size() > 0)
					where.append(" AND MR.PRSN_ID IN (" + medNameSql + ") ");
				if (icdcmList.size() > 0)
					where.append(" AND MR.ICDCM1 IN (" + icdcmSql + ") ");
				if (medLogCodeList.size() > 0)
					where.append(" AND MR.INH_CLINIC_ID IN (" + medLogCodeSql + ") ");
				if (icdAllList.size() > 0) {
					for (String str : icdAllList) {
						where.append(" AND MR.ICD_ALL LIKE CONCAT(CONCAT('%','" + str + "'),'%') ");
					}
				}
				if (payCode != null && payCode.length() > 0)
					where.append(" AND MR.CODE_ALL LIKE CONCAT(CONCAT('%','" + payCode + "'),'%') ");
				if (inhCode != null && inhCode.length() > 0)
					where.append(" AND MR.INH_CODE LIKE CONCAT(CONCAT('%','" + inhCode + "'),'%') ");
				if (sectionList.size() > 0) {
					where.append(" AND MR.DRG_SECTION IN (" + sectionSql + ") ");
				}

				selectColumn.append(where);
				groupBy.append(" GROUP BY MR.DRG_SECTION ");
				selectColumn.append(groupBy);
				orderBy.append(" ORDER BY MR.DRG_SECTION)n ");
				selectColumn.append(orderBy);

				where = new StringBuffer("");
				groupBy = new StringBuffer("");
				orderBy = new StringBuffer("");
				if (!isShowDRGList && drgCodeList.size() == 0) {
					if (applMin > 0)
						where.append(" WHERE DRG_APPL_POINT >= " + applMin + " ");
					if (applMax > 0)
						where.append(" AND DRG_APPL_POINT <= " + applMax + " ");

					selectColumn.append(where);
					where = new StringBuffer("");
				}
				if (isShowDRGList || drgCodeList.size() > 0) {
					selectColumn.append(" UNION ALL ");
					selectColumn.append(" SELECT '" + mapList.get(i).get("sDate") + " " + mapList.get(i).get("eDate")
							+ "' AS DATE, DRG_CODE,DRG_SECTION,DRG_QUANTITY,DRG_APPL_POINT,DRG_ACTUAL_POINT FROM ");
					selectColumn.append(
							" (SELECT MR.DRG_CODE, MR.DRG_SECTION  AS DRG_SECTION, COUNT(1) AS DRG_QUANTITY, SUM(IP_D.APPL_DOT + IP_D.PART_DOT) AS DRG_APPL_POINT, SUM(IP_D.MED_DOT + IP_D.NON_APPL_DOT) AS DRG_ACTUAL_POINT "
									+ "FROM MR, IP_D WHERE DRG_SECTION IS NOT  NULL AND DATA_FORMAT = '20' AND MR_END_DATE LIKE CONCAT('2020-06','%') "
									+ "AND IP_D.MR_ID = MR.ID ");
					if (drgCodeList.size() > 0)
						where.append(" AND MR.DRG_CODE IN (" + drgCodeSql + ") ");
					if (funcTypeList.size() > 0)
						where.append(" AND MR.FUNC_TYPE IN (" + funcTypeSql + ") ");
					if (medNameList.size() > 0)
						where.append(" AND MR.PRSN_ID IN (" + medNameSql + ") ");
					if (icdcmList.size() > 0)
						where.append(" AND MR.ICDCM1 IN (" + icdcmSql + ") ");
					if (medLogCodeList.size() > 0)
						where.append(" AND MR.INH_CLINIC_ID IN (" + medLogCodeSql + ") ");
					if (icdAllList.size() > 0) {
						for (String str : icdAllList) {
							where.append(" AND MR.ICD_ALL LIKE CONCAT(CONCAT('%','" + str + "'),'%') ");
						}
					}
					if (payCode != null && payCode.length() > 0)
						where.append(" AND MR.CODE_ALL LIKE CONCAT(CONCAT('%','" + payCode + "'),'%') ");
					if (inhCode != null && inhCode.length() > 0)
						where.append(" AND MR.INH_CODE LIKE CONCAT(CONCAT('%','" + inhCode + "'),'%') ");
					if (sectionList.size() > 0) {
						where.append(" AND MR.DRG_SECTION IN (" + sectionSql + ") ");
					}

					selectColumn.append(where);
					groupBy.append(" GROUP BY MR.DRG_SECTION,MR.DRG_CODE ");
					selectColumn.append(groupBy);
					orderBy.append(" ORDER BY MR.DRG_CODE) n ");
					selectColumn.append(orderBy);
					where = new StringBuffer("");
					groupBy = new StringBuffer("");
					orderBy = new StringBuffer("");

					if (applMin > 0)
						where.append(" WHERE DRG_APPL_POINT >= " + applMin + " ");
					if (applMax > 0)
						where.append(" AND DRG_APPL_POINT <= " + applMax + " ");
					selectColumn.append(where);
					orderBy.append(" ORDER BY DRG_CODE ");
					selectColumn.append(orderBy);
					where = new StringBuffer("");
					orderBy = new StringBuffer("");
				}
				/// 傳統sql語法組成資料
				Query sqlQuery = entityManager.createNativeQuery(selectColumn.toString());
				sqlQuery.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> dataMap = sqlQuery.getResultList();
				for (int y = 0; y < dataMap.size(); y++) {
					String yd = dataMap.get(y).get("DATE").toString();
					String ydc = dataMap.get(y).get("DRG_CODE").toString();
					for (int z = 0; z < sqlMapList.size(); z++) {
						String zd = sqlMapList.get(z).get("DATE").toString();
						String zdc = sqlMapList.get(z).get("DRG_CODE").toString();
						if (yd.equals(zd) && ydc.equals(zdc)) {

							if (dataMap.get(y).get("DRG_SECTION") != null) {
								String area = dataMap.get(y).get("DRG_SECTION").toString();
								switch (area) {
								case "A":
									sqlMapList.get(z).put("SECTION_A",
											Long.parseLong(dataMap.get(y).get("DRG_QUANTITY").toString()));
									sqlMapList.get(z).put("SECTION_A_APPL",
											Long.parseLong(dataMap.get(y).get("DRG_APPL_POINT").toString()));
									sqlMapList.get(z).put("SECTION_A_ACTUAL",
											Long.parseLong(dataMap.get(y).get("DRG_ACTUAL_POINT").toString()));
									sqlMapList.get(z).put("DIFFA",
											Long.parseLong(dataMap.get(y).get("DRG_APPL_POINT").toString()) - Long
													.parseLong(dataMap.get(y).get("DRG_ACTUAL_POINT").toString()));
									break;
								case "B1":
									sqlMapList.get(z).put("SECTION_B1",
											Long.parseLong(dataMap.get(y).get("DRG_QUANTITY").toString()));
									sqlMapList.get(z).put("SECTION_B1_APPL",
											Long.parseLong(dataMap.get(y).get("DRG_APPL_POINT").toString()));
									sqlMapList.get(z).put("SECTION_B1_ACTUAL",
											Long.parseLong(dataMap.get(y).get("DRG_ACTUAL_POINT").toString()));
									sqlMapList.get(z).put("DIFFB1",
											Long.parseLong(dataMap.get(y).get("DRG_APPL_POINT").toString()) - Long
													.parseLong(dataMap.get(y).get("DRG_ACTUAL_POINT").toString()));
									break;
								case "B2":
									sqlMapList.get(z).put("SECTION_B2",
											Long.parseLong(dataMap.get(y).get("DRG_QUANTITY").toString()));
									sqlMapList.get(z).put("SECTION_B2_APPL",
											Long.parseLong(dataMap.get(y).get("DRG_APPL_POINT").toString()));
									sqlMapList.get(z).put("SECTION_B2_ACTUAL",
											Long.parseLong(dataMap.get(y).get("DRG_ACTUAL_POINT").toString()));
									sqlMapList.get(z).put("DIFFB2",
											Long.parseLong(dataMap.get(y).get("DRG_APPL_POINT").toString()) - Long
													.parseLong(dataMap.get(y).get("DRG_ACTUAL_POINT").toString()));
									break;
								case "C":
									sqlMapList.get(z).put("SECTION_C",
											Long.parseLong(dataMap.get(y).get("DRG_QUANTITY").toString()));
									sqlMapList.get(z).put("SECTION_C_APPL",
											Long.parseLong(dataMap.get(y).get("DRG_APPL_POINT").toString()));
									sqlMapList.get(z).put("SECTION_C_ACTUAL",
											Long.parseLong(dataMap.get(y).get("DRG_ACTUAL_POINT").toString()));
									sqlMapList.get(z).put("DIFFC",
											Long.parseLong(dataMap.get(y).get("DRG_APPL_POINT").toString()) - Long
													.parseLong(dataMap.get(y).get("DRG_ACTUAL_POINT").toString()));
									break;
								}
							}
						}
					}
				}
				selectColumn = new StringBuffer("");
				entityManager.close();
			}
			if (mapList.size() > 0) {
				for (int j = 0; j < mapList.size(); j++) {
					String ym = mapList.get(j).get("sDate").toString();
					String displayName = mapList.get(j).get("displayName").toString();
					for (int i = 0; i < sqlMapList.size(); i++) {
						String date = sqlMapList.get(i).get("DATE").toString();
						String drgcode = sqlMapList.get(i).get("DRG_CODE").toString();
						if (date.contains(ym)) {
							if (drgcode.length() == 0) {
								sqlMapList.get(i).put("disPlayName", displayName);
							} else {
								sqlMapList.get(i).put("disPlayName", "");
							}
							continue;
						}
						if (sqlMapList.get(i).get("disPlayName") == null) {
							sqlMapList.get(i).put("disPlayName", "");
						}
					}
				}
			}

		}
		result.put("result", "success");
		result.put("message", null);
		result.put("drgData", sqlMapList);

		return result;
	}

	/**
	 * 自費項目清單
	 * 
	 * @param sDate[起始日]
	 * @param eDate[迄日]
	 * @param dataFormats[就醫類別]
	 * @param funcTypes[科別]
	 * @param medNames[醫護姓名]
	 * @param idcAll[不分區ICD碼]
	 * @param payCode[支付標準代碼]
	 * @param inhCode[院內碼]
	 * @param isLastY[去年同期時段相比]
	 * @param isShowOwnExpense[自費分項列出]
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getOwnExpenseQueryCondition(String betweenSDate, String betweenEDate, String dataFormats,
			String funcTypes, String medNames, String icdAll, String payCode, String inhCode, boolean isLastY,
			boolean isShowOwnExpense) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> sqlMapList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> sqlMapList2 = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> sqlMapList2_2 = new ArrayList<Map<String, Object>>();
		/// 就醫類別
		List<String> dataformatList = new ArrayList<String>();
		String dateformatSql = "";
		/// 科別
		List<String> funcTypeList = new ArrayList<String>();
		String funcTypeSql = "";
		/// 醫護姓名
		List<String> medNameList = new ArrayList<String>();
		String medNameSql = "";
		/// 不分區ICD碼
		List<String> icdAllList = new ArrayList<String>();

		/// 如果dataformat有值
		if (dataFormats != null && dataFormats.length() > 0) {
			String[] dataformatArr = StringUtility.splitBySpace(dataFormats);
			for (String str : dataformatArr) {
				dataformatList.add(str);
			}
		}
		/// 如果functype有值
		if (funcTypes != null && funcTypes.length() > 0) {
			String[] funcTypeArr = StringUtility.splitBySpace(funcTypes);
			for (String str : funcTypeArr) {
				funcTypeList.add(str);
				funcTypeSql += "'" + str + "',";
			}
			funcTypeSql = funcTypeSql.substring(0, funcTypeSql.length() - 1);
		}
		/// 如果medNames有值
		if (medNames != null && medNames.length() > 0) {
			String[] medNameArr = StringUtility.splitBySpace(medNames);
			for (String str : medNameArr) {
				medNameList.add(str);
				medNameSql += "'" + str + "',";
			}
			medNameSql = medNameSql.substring(0, medNameSql.length() - 1);
		}

		/// 如果icdAll有值
		if (icdAll != null && icdAll.length() > 0) {
			String[] icdAllArr = StringUtility.splitBySpace(icdAll);
			for (String str : icdAllArr) {
				icdAllList.add(str);
			}
		}

		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();

		map.put("sDate", betweenSDate);
		map.put("eDate", betweenEDate);
		map.put("displayName", "");
		mapList.add(map);
		map = new HashMap<String, Object>();
		if (isLastY) {
			String sdate = betweenSDate;
			String edate = betweenEDate;
			int sy = Integer.parseInt(sdate.substring(0, 4));
			int ey = Integer.parseInt(edate.substring(0, 4));
			sdate = String.valueOf(sy - 1) + betweenSDate.substring(4, betweenSDate.length());
			edate = String.valueOf(ey - 1) + betweenEDate.substring(4, betweenEDate.length());
			map.put("sDate", sdate);
			map.put("eDate", edate);
			map.put("displayName", "去年同期時段相比");
			mapList.add(map);
			map = new HashMap<String, Object>();
		}

		/// 查詢欄位
		StringBuffer selectColumn = new StringBuffer("");
		/// 條件
		StringBuffer where = new StringBuffer("");
		/// groupBy
		StringBuffer groupBy = new StringBuffer("");
		/// orderBy
		StringBuffer orderBy = new StringBuffer("");

		for (int i = 0; i < mapList.size(); i++) {
			String sd = mapList.get(i).get("sDate").toString();
			String ed = mapList.get(i).get("eDate").toString();
			
			/// 先計算出期間內的總筆數
			selectColumn.append(" SELECT * FROM  ");
			for (String str : dataformatList) {
				switch (str) {
				case "all":
					selectColumn.append(" (SELECT COUNT(ID) AS ALL_QUANTITY  FROM MR WHERE OWN_EXPENSE > 0 ");
					where.append(" AND MR_END_DATE BETWEEN '" + sd + "' AND '" + ed + "' ");
					if (funcTypeList.size() > 0)
						where.append(" AND FUNC_TYPE IN (" + funcTypeSql + ") ");

					if (medNameList.size() > 0)
						where.append(" AND PRSN_ID IN (" + medNameSql + ") ");

					if (icdAllList.size() > 0)
						for (String s : icdAllList) {
							where.append(" AND ICD_ALL LIKE CONCAT(CONCAT('%','" + s + "'),'%') ");
						}

					if (payCode != null && payCode.length() > 0)
						where.append(" AND CODE_ALL LIKE CONCAT(CONCAT('%','" + payCode + "'),'%') ");
					
					if (inhCode != null && inhCode.length() > 0)
						where.append(" AND INH_CODE LIKE CONCAT(CONCAT('%','" + inhCode + "'),'%') ");
					///條件最後添加別名
					where.append(" ) a, ");
					selectColumn.append(where);
					selectColumn.append(" (SELECT IFNULL(SUM(OWN_EXPENSE),0) ALL_EXPENSE  FROM MR WHERE OWN_EXPENSE > 0  ");
					where = new StringBuffer("");
					where.append(" AND MR_END_DATE BETWEEN '" + sd + "' AND '" + ed + "' ");
					if (funcTypeList.size() > 0)
						where.append(" AND FUNC_TYPE IN (" + funcTypeSql + ") ");

					if (medNameList.size() > 0)
						where.append(" AND PRSN_ID IN (" + medNameSql + ") ");

					if (icdAllList.size() > 0)
						for (String s : icdAllList) {
							where.append(" AND ICD_ALL LIKE CONCAT(CONCAT('%','" + s + "'),'%') ");
						}

					if (payCode != null && payCode.length() > 0)
						where.append(" AND CODE_ALL LIKE CONCAT(CONCAT('%','" + payCode + "'),'%') ");
					
					if (inhCode != null && inhCode.length() > 0)
						where.append(" AND INH_CODE LIKE CONCAT(CONCAT('%','" + inhCode + "'),'%') ");
					///條件最後添加別名
					where.append(" ) b, ");
					selectColumn.append(where);
					where = new StringBuffer("");
					break;
				case "totalop":
					selectColumn.append(
							" (SELECT COUNT(ID) AS OPALL_QUANTITY  FROM MR WHERE OWN_EXPENSE > 0 AND DATA_FORMAT = '10' ");
					where.append(" AND MR_END_DATE BETWEEN '" + sd + "' AND '" + ed + "' ");
					if (funcTypeList.size() > 0)
						where.append(" AND FUNC_TYPE IN (" + funcTypeSql + ") ");

					if (medNameList.size() > 0)
						where.append(" AND PRSN_ID IN (" + medNameSql + ") ");

					if (icdAllList.size() > 0)
						for (String s : icdAllList) {
							where.append(" AND ICD_ALL LIKE CONCAT(CONCAT('%','" + s + "'),'%') ");
						}

					if (payCode != null && payCode.length() > 0)
						where.append(" AND CODE_ALL LIKE CONCAT(CONCAT('%','" + payCode + "'),'%') ");
					
					if (inhCode != null && inhCode.length() > 0)
						where.append(" AND INH_CODE LIKE CONCAT(CONCAT('%','" + inhCode + "'),'%') ");
					///條件最後添加別名
					where.append(" ) c, ");
					selectColumn.append(where);
					selectColumn.append(
							" (SELECT IFNULL(SUM(OWN_EXPENSE),0) OPALL_EXPENSE  FROM MR WHERE OWN_EXPENSE > 0 AND DATA_FORMAT = '10' ");
					where = new StringBuffer("");
					where.append(" AND MR_END_DATE BETWEEN '" + sd + "' AND '" + ed + "' ");
					if (funcTypeList.size() > 0)
						where.append(" AND FUNC_TYPE IN (" + funcTypeSql + ") ");

					if (medNameList.size() > 0)
						where.append(" AND PRSN_ID IN (" + medNameSql + ") ");

					if (icdAllList.size() > 0)
						for (String s : icdAllList) {
							where.append(" AND ICD_ALL LIKE CONCAT(CONCAT('%','" + s + "'),'%') ");
						}

					if (payCode != null && payCode.length() > 0)
						where.append(" AND CODE_ALL LIKE CONCAT(CONCAT('%','" + payCode + "'),'%') ");
					
					if (inhCode != null && inhCode.length() > 0)
						where.append(" AND INH_CODE LIKE CONCAT(CONCAT('%','" + inhCode + "'),'%') ");
					///條件最後添加別名
					where.append(" ) d, ");
					selectColumn.append(where);
					where = new StringBuffer("");
					break;
				case "op":
					selectColumn.append(
							" (SELECT COUNT(ID) AS OP_QUANTITY  FROM MR WHERE OWN_EXPENSE > 0 AND DATA_FORMAT = '10' AND FUNC_TYPE <> '22' ");
					where.append(" AND MR_END_DATE BETWEEN '" + sd + "' AND '" + ed + "' ");
					if (funcTypeList.size() > 0)
						where.append(" AND FUNC_TYPE IN (" + funcTypeSql + ") ");

					if (medNameList.size() > 0)
						where.append(" AND PRSN_ID IN (" + medNameSql + ") ");

					if (icdAllList.size() > 0)
						for (String s : icdAllList) {
							where.append(" AND ICD_ALL LIKE CONCAT(CONCAT('%','" + s + "'),'%') ");
						}

					if (payCode != null && payCode.length() > 0)
						where.append(" AND CODE_ALL LIKE CONCAT(CONCAT('%','" + payCode + "'),'%') ");
					
					if (inhCode != null && inhCode.length() > 0)
						where.append(" AND INH_CODE LIKE CONCAT(CONCAT('%','" + inhCode + "'),'%') ");
					///條件最後添加別名
					where.append(" ) e, ");
					selectColumn.append(where);
					selectColumn.append(
							" (SELECT IFNULL(SUM(OWN_EXPENSE), 0) OP_EXPENSE  FROM MR WHERE OWN_EXPENSE > 0 AND DATA_FORMAT = '10' AND FUNC_TYPE <> '22' ");
					where = new StringBuffer("");
					where.append(" AND MR_END_DATE BETWEEN '" + sd + "' AND '" + ed + "' ");
					if (funcTypeList.size() > 0)
						where.append(" AND FUNC_TYPE IN (" + funcTypeSql + ") ");

					if (medNameList.size() > 0)
						where.append(" AND PRSN_ID IN (" + medNameSql + ") ");

					if (icdAllList.size() > 0)
						for (String s : icdAllList) {
							where.append(" AND ICD_ALL LIKE CONCAT(CONCAT('%','" + s + "'),'%') ");
						}

					if (payCode != null && payCode.length() > 0)
						where.append(" AND CODE_ALL LIKE CONCAT(CONCAT('%','" + payCode + "'),'%') ");
					
					if (inhCode != null && inhCode.length() > 0)
						where.append(" AND INH_CODE LIKE CONCAT(CONCAT('%','" + inhCode + "'),'%') ");
					///條件最後添加別名
					where.append(" ) f, ");
					selectColumn.append(where);
					where = new StringBuffer("");
					break;
				case "em":
					selectColumn.append(
							" (SELECT COUNT(ID) AS EM_QUANTITY  FROM MR WHERE OWN_EXPENSE > 0 AND DATA_FORMAT = '10' AND FUNC_TYPE = '22' ");
					where.append(" AND MR_END_DATE BETWEEN '" + sd + "' AND '" + ed + "' ");
					if (funcTypeList.size() > 0)
						where.append(" AND FUNC_TYPE IN (" + funcTypeSql + ") ");

					if (medNameList.size() > 0)
						where.append(" AND PRSN_ID IN (" + medNameSql + ") ");

					if (icdAllList.size() > 0)
						for (String s : icdAllList) {
							where.append(" AND ICD_ALL LIKE CONCAT(CONCAT('%','" + s + "'),'%') ");
						}

					if (payCode != null && payCode.length() > 0)
						where.append(" AND CODE_ALL LIKE CONCAT(CONCAT('%','" + payCode + "'),'%') ");
					
					if (inhCode != null && inhCode.length() > 0)
						where.append(" AND INH_CODE LIKE CONCAT(CONCAT('%','" + inhCode + "'),'%') ");
					///條件最後添加別名
					where.append(" ) g, ");
					selectColumn.append(where);
					selectColumn.append(
							" (SELECT IFNULL(SUM(OWN_EXPENSE), 0) EM_EXPENSE  FROM MR WHERE OWN_EXPENSE > 0 AND DATA_FORMAT = '10' AND FUNC_TYPE = '22' ");
					where = new StringBuffer("");
					where.append(" AND MR_END_DATE BETWEEN '" + sd + "' AND '" + ed + "' ");
					if (funcTypeList.size() > 0)
						where.append(" AND FUNC_TYPE IN (" + funcTypeSql + ") ");

					if (medNameList.size() > 0)
						where.append(" AND PRSN_ID IN (" + medNameSql + ") ");

					if (icdAllList.size() > 0)
						for (String s : icdAllList) {
							where.append(" AND ICD_ALL LIKE CONCAT(CONCAT('%','" + s + "'),'%') ");
						}

					if (payCode != null && payCode.length() > 0)
						where.append(" AND CODE_ALL LIKE CONCAT(CONCAT('%','" + payCode + "'),'%') ");
					
					if (inhCode != null && inhCode.length() > 0)
						where.append(" AND INH_CODE LIKE CONCAT(CONCAT('%','" + inhCode + "'),'%') ");
					///條件最後添加別名
					where.append(" ) h, ");
					selectColumn.append(where);
					where = new StringBuffer("");
					break;
				case "ip":
					selectColumn.append(
							" (SELECT COUNT(ID) AS IP_QUANTITY  FROM MR WHERE OWN_EXPENSE > 0 AND DATA_FORMAT = '20' ");
					where.append(" AND MR_END_DATE BETWEEN '" + sd + "' AND '" + ed + "' ");
					if (funcTypeList.size() > 0)
						where.append(" AND FUNC_TYPE IN (" + funcTypeSql + ") ");

					if (medNameList.size() > 0)
						where.append(" AND PRSN_ID IN (" + medNameSql + ") ");

					if (icdAllList.size() > 0)
						for (String s : icdAllList) {
							where.append(" AND ICD_ALL LIKE CONCAT(CONCAT('%','" + s + "'),'%') ");
						}

					if (payCode != null && payCode.length() > 0)
						where.append(" AND CODE_ALL LIKE CONCAT(CONCAT('%','" + payCode + "'),'%') ");
					
					if (inhCode != null && inhCode.length() > 0)
						where.append(" AND INH_CODE LIKE CONCAT(CONCAT('%','" + inhCode + "'),'%') ");
					///條件最後添加別名
					where.append(" ) i, ");
					selectColumn.append(where);
					selectColumn.append(
							" (SELECT IFNULL(SUM(OWN_EXPENSE),0) IP_EXPENSE  FROM MR WHERE OWN_EXPENSE > 0 AND DATA_FORMAT = '20' ");
					where = new StringBuffer("");
					where.append(" AND MR_END_DATE BETWEEN '" + sd + "' AND '" + ed + "' ");
					if (funcTypeList.size() > 0)
						where.append(" AND FUNC_TYPE IN (" + funcTypeSql + ") ");

					if (medNameList.size() > 0)
						where.append(" AND PRSN_ID IN (" + medNameSql + ") ");

					if (icdAllList.size() > 0)
						for (String s : icdAllList) {
							where.append(" AND ICD_ALL LIKE CONCAT(CONCAT('%','" + s + "'),'%') ");
						}

					if (payCode != null && payCode.length() > 0)
						where.append(" AND CODE_ALL LIKE CONCAT(CONCAT('%','" + payCode + "'),'%') ");
					
					if (inhCode != null && inhCode.length() > 0)
						where.append(" AND INH_CODE LIKE CONCAT(CONCAT('%','" + inhCode + "'),'%') ");
					///條件最後添加別名
					where.append(" ) j, ");
					selectColumn.append(where);
					where = new StringBuffer("");
					break;
				default:
					break;
				}
			}
			//// 最後添加一個無意義與句避免 “,”使sql錯誤
			selectColumn.append(" (SELECT DISTINCT 1 FROM MR) x ");
			/// 傳統sql語法組成資料
			Query sqlQuery = entityManager.createNativeQuery(selectColumn.toString());
			sqlQuery.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> dataMap = sqlQuery.getResultList();
			sqlMapList.addAll(dataMap);
			selectColumn = new StringBuffer("");
			entityManager.close();
			
			selectColumn = new StringBuffer("");
			where = new StringBuffer("");
			
			///第二段sql
			for (String str : dataformatList) {
				switch (str) {
				case "all":
					if(selectColumn.length() > 0) {selectColumn.append(" UNION ALL ");}
					selectColumn.append(" SELECT '不分區' AS DATA_FORMAT, FUNC_TYPE ,DESC_CHI, QUANTITY, EXPENSE FROM  ");
					selectColumn.append(" (SELECT MR.FUNC_TYPE, CODE_TABLE.DESC_CHI,  COUNT(1) AS QUANTITY, SUM(OWN_EXPENSE) AS EXPENSE  FROM MR, CODE_TABLE WHERE MR.FUNC_TYPE = CODE_TABLE.CODE AND CODE_TABLE.CAT ='FUNC_TYPE' AND  OWN_EXPENSE > 0 ");
					where.append(" AND MR.MR_END_DATE BETWEEN '" + sd + "' AND '" + ed + "' ");
					if (funcTypeList.size() > 0)
						where.append(" AND MR.FUNC_TYPE IN (" + funcTypeSql + ") ");

					if (medNameList.size() > 0)
						where.append(" AND MR.PRSN_ID IN (" + medNameSql + ") ");

					if (icdAllList.size() > 0)
						for (String s : icdAllList) {
							where.append(" AND MR.ICD_ALL LIKE CONCAT(CONCAT('%','" + s + "'),'%') ");
						}

					if (payCode != null && payCode.length() > 0)
						where.append(" AND MR.CODE_ALL LIKE CONCAT(CONCAT('%','" + payCode + "'),'%') ");
					
					if (inhCode != null && inhCode.length() > 0)
						where.append(" AND MR.INH_CODE LIKE CONCAT(CONCAT('%','" + inhCode + "'),'%') ");
					groupBy.append(" GROUP BY MR.FUNC_TYPE, CODE_TABLE.DESC_CHI ) ");
					selectColumn.append(where);
					selectColumn.append(groupBy);
					
					where = new StringBuffer("");
					groupBy = new StringBuffer("");
					///
					
					if(isShowOwnExpense) {
						selectColumn.append(" UNION ALL ");
						selectColumn.append(" SELECT '不分區' AS DATA_FORMAT, FUNC_TYPE, DESC_CHI, QUANTITY, EXPENSE FROM ");
						if(funcTypeList.size() > 0 && medNameList.size() > 0) {
							selectColumn.append(" (SELECT  IP_P.ORDER_CODE AS FUNC_TYPE  , CODE_TABLE.DESC_CHI AS DESC_CHI,  COUNT(1) AS QUANTITY, SUM(MR.OWN_EXPENSE) AS EXPENSE FROM MR, IP_P, CODE_TABLE WHERE  MR.ID = IP_P.MR_ID AND MR.FUNC_TYPE = CODE_TABLE.CODE  AND CODE_TABLE.CAT = 'FUNC_TYPE' AND IP_P.PAY_BY IN ('Y','Z') AND  MR.OWN_EXPENSE > 0 ");
						}
						else {
							selectColumn.append(" (SELECT  IP_P.ORDER_CODE AS FUNC_TYPE  , NULL AS DESC_CHI,  COUNT(1) AS QUANTITY, SUM(MR.OWN_EXPENSE) AS EXPENSE FROM MR, IP_P WHERE  MR.ID = IP_P.MR_ID AND IP_P.PAY_BY IN ('Y','Z') AND  MR.OWN_EXPENSE > 0 ");
						}
						where.append(" AND MR.MR_END_DATE BETWEEN '" + sd + "' AND '" + ed + "' ");
						if (funcTypeList.size() > 0)
							where.append(" AND MR.FUNC_TYPE IN (" + funcTypeSql + ") ");

						if (medNameList.size() > 0)
							where.append(" AND MR.PRSN_ID IN (" + medNameSql + ") ");

						if (icdAllList.size() > 0)
							for (String s : icdAllList) {
								where.append(" AND MR.ICD_ALL LIKE CONCAT(CONCAT('%','" + s + "'),'%') ");
							}

						if (payCode != null && payCode.length() > 0)
							where.append(" AND MR.CODE_ALL LIKE CONCAT(CONCAT('%','" + payCode + "'),'%') ");
						
						if (inhCode != null && inhCode.length() > 0)
							where.append(" AND MR.INH_CODE LIKE CONCAT(CONCAT('%','" + inhCode + "'),'%') ");
						if(funcTypeList.size() > 0 && medNameList.size() > 0) {
							groupBy.append(" GROUP BY IP_P.ORDER_CODE , CODE_TABLE.DESC_CHI ) ");
						}
						else {
							groupBy.append(" GROUP BY IP_P.ORDER_CODE) ");
						}
						
						selectColumn.append(where);
						selectColumn.append(groupBy);
						
						where = new StringBuffer("");
						groupBy = new StringBuffer("");
						selectColumn.append(" UNION ALL ");
						selectColumn.append(" SELECT '不分區' AS DATA_FORMAT, FUNC_TYPE, DESC_CHI, QUANTITY, EXPENSE FROM ");
						if(funcTypeList.size() > 0 && medNameList.size() > 0) {
							selectColumn.append(" (SELECT OP_P.DRUG_NO  AS FUNC_TYPE, CODE_TABLE.DESC_CHI AS DESC_CHI, COUNT(1) AS QUANTITY, SUM(MR.OWN_EXPENSE) AS EXPENSE FROM MR, OP_P, CODE_TABLE WHERE MR.ID = OP_P.MR_ID AND MR.FUNC_TYPE = CODE_TABLE.CODE  AND CODE_TABLE.CAT = 'FUNC_TYPE' AND OP_P.PAY_BY  IN ('Y','Z') AND  MR.OWN_EXPENSE > 0 ");
						}
						else {
							selectColumn.append(" (SELECT OP_P.DRUG_NO  AS FUNC_TYPE, NULL  AS DESC_CHI, COUNT(1) AS QUANTITY, SUM(MR.OWN_EXPENSE) AS EXPENSE FROM MR, OP_P WHERE MR.ID = OP_P.MR_ID  AND OP_P.PAY_BY  IN ('Y','Z') AND  MR.OWN_EXPENSE > 0 ");
						}
						
						where.append(" AND MR.MR_END_DATE BETWEEN '" + sd + "' AND '" + ed + "' ");
						if (funcTypeList.size() > 0)
							where.append(" AND MR.FUNC_TYPE IN (" + funcTypeSql + ") ");

						if (medNameList.size() > 0)
							where.append(" AND MR.PRSN_ID IN (" + medNameSql + ") ");

						if (icdAllList.size() > 0)
							for (String s : icdAllList) {
								where.append(" AND MR.ICD_ALL LIKE CONCAT(CONCAT('%','" + s + "'),'%') ");
							}

						if (payCode != null && payCode.length() > 0)
							where.append(" AND MR.CODE_ALL LIKE CONCAT(CONCAT('%','" + payCode + "'),'%') ");
						
						if (inhCode != null && inhCode.length() > 0)
							where.append(" AND MR.INH_CODE LIKE CONCAT(CONCAT('%','" + inhCode + "'),'%') ");
						if(funcTypeList.size() > 0 && medNameList.size() > 0) {
							groupBy.append(" GROUP BY OP_P.DRUG_NO, CODE_TABLE.DESC_CHI ) ");
						}
						else {
							groupBy.append(" GROUP BY OP_P.DRUG_NO) ");
						}
						
						selectColumn.append(where);
						selectColumn.append(groupBy);
						selectColumn.append(orderBy);
						where = new StringBuffer("");
						groupBy = new StringBuffer("");
						orderBy = new StringBuffer("");
						
					}
					break;
				case "totalop":
					if(selectColumn.length() > 0) {selectColumn.append(" UNION ALL ");}
					selectColumn.append(" SELECT '門急診' AS DATA_FORMAT, FUNC_TYPE ,DESC_CHI, QUANTITY, EXPENSE FROM  ");
					selectColumn.append(
							" (SELECT MR.FUNC_TYPE, CODE_TABLE.DESC_CHI,  COUNT(1) AS QUANTITY, SUM(OWN_EXPENSE) AS EXPENSE  FROM MR, CODE_TABLE WHERE MR.FUNC_TYPE = CODE_TABLE.CODE AND CODE_TABLE.CAT ='FUNC_TYPE' AND  OWN_EXPENSE > 0 AND DATA_FORMAT = '10' ");
					where.append(" AND MR.MR_END_DATE BETWEEN '" + sd + "' AND '" + ed + "' ");
					if (funcTypeList.size() > 0)
						where.append(" AND MR.FUNC_TYPE IN (" + funcTypeSql + ") ");

					if (medNameList.size() > 0)
						where.append(" AND MR.PRSN_ID IN (" + medNameSql + ") ");

					if (icdAllList.size() > 0)
						for (String s : icdAllList) {
							where.append(" AND MR.ICD_ALL LIKE CONCAT(CONCAT('%','" + s + "'),'%') ");
						}

					if (payCode != null && payCode.length() > 0)
						where.append(" AND MR.CODE_ALL LIKE CONCAT(CONCAT('%','" + payCode + "'),'%') ");
					
					if (inhCode != null && inhCode.length() > 0)
						where.append(" AND MR.INH_CODE LIKE CONCAT(CONCAT('%','" + inhCode + "'),'%') ");
					groupBy.append(" GROUP BY MR.FUNC_TYPE, CODE_TABLE.DESC_CHI ) ");
					selectColumn.append(where);
					selectColumn.append(groupBy);

					where = new StringBuffer("");
					groupBy = new StringBuffer("");
					///
					if(isShowOwnExpense) {
						selectColumn.append(" UNION ALL ");
						selectColumn.append(" SELECT '門急診' AS DATA_FORMAT, FUNC_TYPE ,DESC_CHI, QUANTITY, EXPENSE FROM  ");
						if(funcTypeList.size() > 0 && medNameList.size() > 0) {
							selectColumn.append(" (SELECT OP_P.DRUG_NO  AS FUNC_TYPE, CODE_TABLE.DESC_CHI AS DESC_CHI, COUNT(1) AS QUANTITY, SUM(MR.OWN_EXPENSE) AS EXPENSE FROM MR, OP_P, CODE_TABLE WHERE MR.ID = OP_P.MR_ID AND MR.FUNC_TYPE = CODE_TABLE.CODE  AND CODE_TABLE.CAT = 'FUNC_TYPE' AND OP_P.PAY_BY  IN ('Y','Z') AND  MR.OWN_EXPENSE > 0 ");
						}
						else {
							selectColumn.append(" (SELECT OP_P.DRUG_NO  AS FUNC_TYPE, NULL  AS DESC_CHI, COUNT(1) AS QUANTITY, SUM(MR.OWN_EXPENSE) AS EXPENSE FROM MR, OP_P WHERE MR.ID = OP_P.MR_ID  AND OP_P.PAY_BY  IN ('Y','Z') AND  MR.OWN_EXPENSE > 0  ");
						}
						
						where.append(" AND MR.MR_END_DATE BETWEEN '" + sd + "' AND '" + ed + "' ");
						if (funcTypeList.size() > 0)
							where.append(" AND MR.FUNC_TYPE IN (" + funcTypeSql + ") ");

						if (medNameList.size() > 0)
							where.append(" AND MR.PRSN_ID IN (" + medNameSql + ") ");

						if (icdAllList.size() > 0)
							for (String s : icdAllList) {
								where.append(" AND MR.ICD_ALL LIKE CONCAT(CONCAT('%','" + s + "'),'%') ");
							}

						if (payCode != null && payCode.length() > 0)
							where.append(" AND MR.CODE_ALL LIKE CONCAT(CONCAT('%','" + payCode + "'),'%') ");
						
						if (inhCode != null && inhCode.length() > 0)
							where.append(" AND MR.INH_CODE LIKE CONCAT(CONCAT('%','" + inhCode + "'),'%') ");
						if(funcTypeList.size() > 0 && medNameList.size() > 0) {
							groupBy.append(" GROUP BY OP_P.DRUG_NO, CODE_TABLE.DESC_CHI ) ");
						}
						else {
							groupBy.append(" GROUP BY OP_P.DRUG_NO) ");
						}
						
						selectColumn.append(where);
						selectColumn.append(groupBy);
						selectColumn.append(orderBy);
						where = new StringBuffer("");
						groupBy = new StringBuffer("");
						orderBy = new StringBuffer("");
					}
					break;
				case "op":
					if(selectColumn.length() > 0) {selectColumn.append(" UNION ALL ");}
					selectColumn.append(" SELECT '門診' AS DATA_FORMAT, FUNC_TYPE ,DESC_CHI, QUANTITY, EXPENSE FROM  ");
					selectColumn.append(
							" (SELECT FUNC_TYPE, CODE_TABLE.DESC_CHI,  COUNT(1) AS QUANTITY, SUM(OWN_EXPENSE) AS EXPENSE  FROM MR, CODE_TABLE WHERE MR.FUNC_TYPE = CODE_TABLE.CODE AND CODE_TABLE.CAT ='FUNC_TYPE' AND  OWN_EXPENSE > 0 AND DATA_FORMAT = '10' AND FUNC_TYPE <> '22'  ");
					where.append(" AND MR.MR_END_DATE BETWEEN '" + sd + "' AND '" + ed + "' ");
					if (funcTypeList.size() > 0)
						where.append(" AND MR.FUNC_TYPE IN (" + funcTypeSql + ") ");

					if (medNameList.size() > 0)
						where.append(" AND MR.PRSN_ID IN (" + medNameSql + ") ");

					if (icdAllList.size() > 0)
						for (String s : icdAllList) {
							where.append(" AND MR.ICD_ALL LIKE CONCAT(CONCAT('%','" + s + "'),'%') ");
						}

					if (payCode != null && payCode.length() > 0)
						where.append(" AND MR.CODE_ALL LIKE CONCAT(CONCAT('%','" + payCode + "'),'%') ");
					
					if (inhCode != null && inhCode.length() > 0)
						where.append(" AND MR.INH_CODE LIKE CONCAT(CONCAT('%','" + inhCode + "'),'%') ");
					groupBy.append(" GROUP BY MR.FUNC_TYPE, CODE_TABLE.DESC_CHI ) ");
					selectColumn.append(where);
					selectColumn.append(groupBy);

					where = new StringBuffer("");
					groupBy = new StringBuffer("");
					///
					if(isShowOwnExpense) {
						selectColumn.append(" UNION ALL ");
						selectColumn.append(" SELECT '門診' AS DATA_FORMAT, FUNC_TYPE ,DESC_CHI, QUANTITY, EXPENSE FROM  ");
						if(funcTypeList.size() > 0 && medNameList.size() > 0) {
							selectColumn.append(" (SELECT OP_P.DRUG_NO  AS FUNC_TYPE, CODE_TABLE.DESC_CHI AS DESC_CHI, COUNT(1) AS QUANTITY, SUM(MR.OWN_EXPENSE) AS EXPENSE FROM MR, OP_P, CODE_TABLE WHERE MR.ID = OP_P.MR_ID AND MR.FUNC_TYPE = CODE_TABLE.CODE  AND CODE_TABLE.CAT = 'FUNC_TYPE' AND OP_P.PAY_BY  IN ('Y','Z') AND  MR.OWN_EXPENSE > 0  AND MR.FUNC_TYPE <> '22' ");
						}
						else {
							selectColumn.append(" (SELECT OP_P.DRUG_NO  AS FUNC_TYPE, NULL  AS DESC_CHI, COUNT(1) AS QUANTITY, SUM(MR.OWN_EXPENSE) AS EXPENSE FROM MR, OP_P WHERE MR.ID = OP_P.MR_ID  AND OP_P.PAY_BY  IN ('Y','Z') AND  MR.OWN_EXPENSE > 0 AND MR.FUNC_TYPE <> '22' ");
						}
						
						where.append(" AND MR.MR_END_DATE BETWEEN '" + sd + "' AND '" + ed + "' ");
						if (funcTypeList.size() > 0)
							where.append(" AND MR.FUNC_TYPE IN (" + funcTypeSql + ") ");

						if (medNameList.size() > 0)
							where.append(" AND MR.PRSN_ID IN (" + medNameSql + ") ");

						if (icdAllList.size() > 0)
							for (String s : icdAllList) {
								where.append(" AND MR.ICD_ALL LIKE CONCAT(CONCAT('%','" + s + "'),'%') ");
							}

						if (payCode != null && payCode.length() > 0)
							where.append(" AND MR.CODE_ALL LIKE CONCAT(CONCAT('%','" + payCode + "'),'%') ");
						
						if (inhCode != null && inhCode.length() > 0)
							where.append(" AND MR.INH_CODE LIKE CONCAT(CONCAT('%','" + inhCode + "'),'%') ");
						if(funcTypeList.size() > 0 && medNameList.size() > 0) {
							groupBy.append(" GROUP BY OP_P.DRUG_NO, CODE_TABLE.DESC_CHI ) ");
						}
						else {
							groupBy.append(" GROUP BY OP_P.DRUG_NO) ");
						}
						
						selectColumn.append(where);
						selectColumn.append(groupBy);
						selectColumn.append(orderBy);
						where = new StringBuffer("");
						groupBy = new StringBuffer("");
						orderBy = new StringBuffer("");
					}
					break;
				case "em":
					if(selectColumn.length() > 0) {selectColumn.append(" UNION ALL ");}
					selectColumn.append(" SELECT '急診' AS DATA_FORMAT, FUNC_TYPE ,DESC_CHI, QUANTITY, EXPENSE FROM  ");
					selectColumn.append(
							" (SELECT FUNC_TYPE, CODE_TABLE.DESC_CHI,  COUNT(1) AS QUANTITY, SUM(OWN_EXPENSE) AS EXPENSE  FROM MR, CODE_TABLE WHERE MR.FUNC_TYPE = CODE_TABLE.CODE AND CODE_TABLE.CAT ='FUNC_TYPE' AND  OWN_EXPENSE > 0 AND DATA_FORMAT = '10' AND FUNC_TYPE = '22'  ");
					where.append(" AND MR.MR_END_DATE BETWEEN '" + sd + "' AND '" + ed + "' ");
					if (funcTypeList.size() > 0)
						where.append(" AND MR.FUNC_TYPE IN (" + funcTypeSql + ") ");

					if (medNameList.size() > 0)
						where.append(" AND MR.PRSN_ID IN (" + medNameSql + ") ");

					if (icdAllList.size() > 0)
						for (String s : icdAllList) {
							where.append(" AND MR.ICD_ALL LIKE CONCAT(CONCAT('%','" + s + "'),'%') ");
						}

					if (payCode != null && payCode.length() > 0)
						where.append(" AND MR.CODE_ALL LIKE CONCAT(CONCAT('%','" + payCode + "'),'%') ");
					
					if (inhCode != null && inhCode.length() > 0)
						where.append(" AND MR.INH_CODE LIKE CONCAT(CONCAT('%','" + inhCode + "'),'%') ");
					groupBy.append(" GROUP BY MR.FUNC_TYPE, CODE_TABLE.DESC_CHI ) ");
					selectColumn.append(where);
					selectColumn.append(groupBy);

					where = new StringBuffer("");
					groupBy = new StringBuffer("");
					///
					if(isShowOwnExpense) {
						selectColumn.append(" UNION ALL ");
						selectColumn.append(" SELECT '急診' AS DATA_FORMAT, FUNC_TYPE ,DESC_CHI, QUANTITY, EXPENSE FROM  ");
						if(funcTypeList.size() > 0 && medNameList.size() > 0) {
							selectColumn.append(" (SELECT OP_P.DRUG_NO  AS FUNC_TYPE, CODE_TABLE.DESC_CHI AS DESC_CHI, COUNT(1) AS QUANTITY, SUM(MR.OWN_EXPENSE) AS EXPENSE FROM MR, OP_P, CODE_TABLE WHERE MR.ID = OP_P.MR_ID AND MR.FUNC_TYPE = CODE_TABLE.CODE  AND CODE_TABLE.CAT = 'FUNC_TYPE' AND OP_P.PAY_BY  IN ('Y','Z') AND  MR.OWN_EXPENSE > 0  AND MR.FUNC_TYPE = '22' ");
						}
						else {
							
							selectColumn.append(" (SELECT OP_P.DRUG_NO  AS FUNC_TYPE, NULL  AS DESC_CHI, COUNT(1) AS QUANTITY, SUM(MR.OWN_EXPENSE) AS EXPENSE FROM MR, OP_P WHERE MR.ID = OP_P.MR_ID  AND OP_P.PAY_BY  IN ('Y','Z') AND  MR.OWN_EXPENSE > 0 AND MR.FUNC_TYPE = '22' ");
						}
						where.append(" AND MR.MR_END_DATE BETWEEN '" + sd + "' AND '" + ed + "' ");
						if (funcTypeList.size() > 0)
							where.append(" AND MR.FUNC_TYPE IN (" + funcTypeSql + ") ");

						if (medNameList.size() > 0)
							where.append(" AND MR.PRSN_ID IN (" + medNameSql + ") ");

						if (icdAllList.size() > 0)
							for (String s : icdAllList) {
								where.append(" AND MR.ICD_ALL LIKE CONCAT(CONCAT('%','" + s + "'),'%') ");
							}

						if (payCode != null && payCode.length() > 0)
							where.append(" AND MR.CODE_ALL LIKE CONCAT(CONCAT('%','" + payCode + "'),'%') ");
						
						if (inhCode != null && inhCode.length() > 0)
							where.append(" AND MR.INH_CODE LIKE CONCAT(CONCAT('%','" + inhCode + "'),'%') ");
						if(funcTypeList.size() > 0 && medNameList.size() > 0) {
							groupBy.append(" GROUP BY OP_P.DRUG_NO, CODE_TABLE.DESC_CHI ) ");
						}
						else {
							groupBy.append(" GROUP BY OP_P.DRUG_NO) ");
						}
						selectColumn.append(where);
						selectColumn.append(groupBy);
						selectColumn.append(orderBy);
						where = new StringBuffer("");
						groupBy = new StringBuffer("");
						orderBy = new StringBuffer("");
					}
					break;
				case "ip":
					if(selectColumn.length() > 0) {selectColumn.append(" UNION ALL ");}
					selectColumn.append(" SELECT '住院' AS DATA_FORMAT, FUNC_TYPE ,DESC_CHI, QUANTITY, EXPENSE FROM  ");
					selectColumn.append(
							" (SELECT FUNC_TYPE, CODE_TABLE.DESC_CHI,  COUNT(1) AS QUANTITY, SUM(OWN_EXPENSE) AS EXPENSE  FROM MR , CODE_TABLE WHERE MR.FUNC_TYPE = CODE_TABLE.CODE AND CODE_TABLE.CAT ='FUNC_TYPE' AND  OWN_EXPENSE > 0 AND DATA_FORMAT = '20' ");
					where.append(" AND MR.MR_END_DATE BETWEEN '" + sd + "' AND '" + ed + "' ");
					if (funcTypeList.size() > 0)
						where.append(" AND MR.FUNC_TYPE IN (" + funcTypeSql + ") ");

					if (medNameList.size() > 0)
						where.append(" AND MR.PRSN_ID IN (" + medNameSql + ") ");

					if (icdAllList.size() > 0)
						for (String s : icdAllList) {
							where.append(" AND MR.ICD_ALL LIKE CONCAT(CONCAT('%','" + s + "'),'%') ");
						}

					if (payCode != null && payCode.length() > 0)
						where.append(" AND MR.CODE_ALL LIKE CONCAT(CONCAT('%','" + payCode + "'),'%') ");
					
					if (inhCode != null && inhCode.length() > 0)
						where.append(" AND MR.INH_CODE LIKE CONCAT(CONCAT('%','" + inhCode + "'),'%') ");
					groupBy.append(" GROUP BY MR.FUNC_TYPE, CODE_TABLE.DESC_CHI ) ");
					selectColumn.append(where);
					selectColumn.append(groupBy);

					where = new StringBuffer("");
					groupBy = new StringBuffer("");
					///
					if(isShowOwnExpense) {
						selectColumn.append(" UNION ALL ");
						selectColumn.append(" SELECT '住院' AS DATA_FORMAT, FUNC_TYPE ,DESC_CHI, QUANTITY, EXPENSE FROM  ");
						if(funcTypeList.size() > 0 && medNameList.size() > 0) {
							selectColumn.append(" (SELECT  IP_P.ORDER_CODE AS FUNC_TYPE  , CODE_TABLE.DESC_CHI AS DESC_CHI,  COUNT(1) AS QUANTITY, SUM(MR.OWN_EXPENSE) AS EXPENSE FROM MR, IP_P, CODE_TABLE WHERE  MR.ID = IP_P.MR_ID AND MR.FUNC_TYPE = CODE_TABLE.CODE  AND CODE_TABLE.CAT = 'FUNC_TYPE' AND IP_P.PAY_BY IN ('Y','Z') AND  MR.OWN_EXPENSE > 0 ");
						}
						else {
							
							selectColumn.append(" (SELECT  IP_P.ORDER_CODE AS FUNC_TYPE  , NULL AS DESC_CHI,  COUNT(1) AS QUANTITY, SUM(MR.OWN_EXPENSE) AS EXPENSE FROM MR, IP_P WHERE  MR.ID = IP_P.MR_ID AND IP_P.PAY_BY IN ('Y','Z') AND  MR.OWN_EXPENSE > 0 ");
						}
						where.append(" AND MR.MR_END_DATE BETWEEN '" + sd + "' AND '" + ed + "' ");
						if (funcTypeList.size() > 0)
							where.append(" AND MR.FUNC_TYPE IN (" + funcTypeSql + ") ");

						if (medNameList.size() > 0)
							where.append(" AND MR.PRSN_ID IN (" + medNameSql + ") ");

						if (icdAllList.size() > 0)
							for (String s : icdAllList) {
								where.append(" AND MR.ICD_ALL LIKE CONCAT(CONCAT('%','" + s + "'),'%') ");
							}

						if (payCode != null && payCode.length() > 0)
							where.append(" AND MR.CODE_ALL LIKE CONCAT(CONCAT('%','" + payCode + "'),'%') ");
						
						if (inhCode != null && inhCode.length() > 0)
							where.append(" AND MR.INH_CODE LIKE CONCAT(CONCAT('%','" + inhCode + "'),'%') ");
						if(funcTypeList.size() > 0 && medNameList.size() > 0) {
							groupBy.append(" GROUP BY IP_P.ORDER_CODE , CODE_TABLE.DESC_CHI ) ");
						}
						else {
							groupBy.append(" GROUP BY IP_P.ORDER_CODE) ");
						}
						selectColumn.append(where);
						selectColumn.append(groupBy);
						selectColumn.append(orderBy);
						where = new StringBuffer("");
						groupBy = new StringBuffer("");
						orderBy = new StringBuffer("");
					}
					break;
				default:
					break;
				}
			}
			/// 傳統sql語法組成資料
			sqlQuery = entityManager.createNativeQuery(selectColumn.toString());
			sqlQuery.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			dataMap.clear();
			dataMap = sqlQuery.getResultList();
			if(i > 0) {
				///存放去年資料
				sqlMapList2_2.addAll(dataMap);
			}
			else {
				///存放指定期間資料
				sqlMapList2.addAll(dataMap);
			}
			selectColumn = new StringBuffer("");
			entityManager.close();

		    
		}
		if (sqlMapList.size() > 1) {
			sqlMapList.get(0).put("displayName", "");
			sqlMapList.get(1).put("displayName", "去年同期時段相比");
		}
		System.out.println("sqlMapList2.size -> " + sqlMapList2.size());
		
		List<OwnExpenseQueryCondition> modelList = mapToObj(sqlMapList, sqlMapList2,sqlMapList2_2);
	
		result.put("result", "success");
		result.put("message", null);
		result.put("dataList", modelList);
		

		return result;
	}

	private List<Integer> findYearMonth(List<Object> years, List<Object> quarters) {

		List<Integer> resList = new ArrayList<Integer>();
		for (int i = 0; i < years.size(); i++) {

			int yearInt = Integer.parseInt(years.get(i).toString()) * 100;
			int monthInt = Integer.parseInt(quarters.get(i).toString().replace("0", ""));

			resList.add(yearInt + monthInt);
		}

		return resList;
	}

	private List<String> findYearMonthStr(List<Object> years, List<Object> month) {

		List<String> resList = new ArrayList<String>();
		for (int i = 0; i < years.size(); i++) {

			String yearStr = years.get(i).toString();
			String monthStr = month.get(i).toString();
			if (monthStr.length() == 1)
				monthStr = "0" + month.get(i).toString();

			resList.add(yearStr + "-" + monthStr);
		}

		return resList;
	}

	private void calculateAchievementQuarter(AchievementQuarter aq, POINT_MONTHLY pm, String name, String displayName) {
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
		qdAll.setCases(pm.getPatientOp() + pm.getPatientEm() + pm.getIpQuantity());
		qdAll.setDispalyName(displayName);

		qdIp.setActual(qdIp.getActual() + pm.getTotalIp());
		qdIp.setAssigned(qdIp.getAssigned() + pm.getAssignedIp());
		qdIp.setOriginal(qdIp.getOriginal() + pm.getTotalIp() + pm.getNoApplIp());
		qdIp.setOver(qdIp.getActual().longValue() - qdIp.getAssigned().longValue());
		qdIp.setPercent(Float.parseFloat(
				df.format((double) (qdIp.getActual().longValue() * 100) / qdIp.getAssigned().doubleValue())));
		qdIp.setCases(pm.getIpQuantity());
		qdIp.setDispalyName(displayName);

		qdOp.setActual(qdOp.getActual() + pm.getTotalOpAll());
		qdOp.setAssigned(qdOp.getAssigned() + pm.getAssignedOpAll());
		qdOp.setOriginal(qdOp.getOriginal() + pm.getTotalOpAll() + pm.getNoApplOp());
		qdOp.setOver(qdOp.getActual().longValue() - qdOp.getAssigned().longValue());
		qdOp.setPercent(Float.parseFloat(
				df.format((double) (qdOp.getActual().longValue() * 100) / qdOp.getAssigned().doubleValue())));
		qdOp.setCases(pm.getPatientOp() + pm.getPatientEm());
		qdOp.setDispalyName(displayName);

	}

	private QuarterData getQuarterDataByName(List<QuarterData> list, String name) {

		QuarterData result = new QuarterData(name);
		list.add(result);
		return result;

	}

	///自費sqlMap轉物件
	private List<OwnExpenseQueryCondition> mapToObj(List<Map<String,Object>> sqlList,List<Map<String,Object>> sqlList2,List<Map<String,Object>> sqlList2_2) {
		OwnExpenseQueryCondition model = new OwnExpenseQueryCondition();
		OwnExpenseQueryConditionDetail detail = new OwnExpenseQueryConditionDetail();
		List<OwnExpenseQueryCondition> modelList = new ArrayList<OwnExpenseQueryCondition>();
		List<OwnExpenseQueryConditionDetail> allList = new ArrayList<OwnExpenseQueryConditionDetail>();
		List<OwnExpenseQueryConditionDetail> opAllList = new ArrayList<OwnExpenseQueryConditionDetail>();
		List<OwnExpenseQueryConditionDetail> opList = new ArrayList<OwnExpenseQueryConditionDetail>();
		List<OwnExpenseQueryConditionDetail> emList = new ArrayList<OwnExpenseQueryConditionDetail>();
		List<OwnExpenseQueryConditionDetail> ipList = new ArrayList<OwnExpenseQueryConditionDetail>();
		
		for(Map<String,Object> map : sqlList) {
			Object aq =  map.get("ALL_QUANTITY");
			Object ae =  map.get("ALL_EXPENSE");
			Object opaq =  map.get("OPALL_QUANTITY");
			Object opae =  map.get("OPALL_EXPENSE");
			Object oq =  map.get("OP_QUANTITY");
			Object oe =  map.get("OP_EXPENSE");
			Object eq =  map.get("EM_QUANTITY");
			Object ee =  map.get("EM_EXPENSE");
			Object iq =  map.get("IP_QUANTITY");
			Object ie =  map.get("IP_EXPENSE");
			Object disName = map.get("displayName");
			
			if(aq != null) {
				model.setAllQuantity(Long.parseLong(aq.toString()));
				model.setAllExpense(Long.parseLong(ae.toString()));
			}
			if(opaq != null) {
				model.setOpAllQuantity(Long.parseLong(opaq.toString()));
				model.setOpAllExpense(Long.parseLong(opae.toString()));
			}
			if(oq != null) {
				model.setOpQuantity(Long.parseLong(oq.toString()));
				model.setOpExpense(Long.parseLong(oe.toString()));
			}
			if(eq != null) {
				model.setEmQuantity(Long.parseLong(eq.toString()));
				model.setEmExpense(Long.parseLong(ee.toString()));
			}
			if(iq != null) {
				model.setIpQuantity(Long.parseLong(iq.toString()));
				model.setIpExpense(Long.parseLong(ie.toString()));
			}
			if(disName != null) {
				
				model.setDisplayName(disName.toString());
				
				if(disName.toString().length() == 0) {
					
					for(Map<String,Object> m2 : sqlList2) {
						Object df = m2.get("DATA_FORMAT");
						Object ft = m2.get("FUNC_TYPE");
						Object dc = m2.get("DESC_CHI");
						Object q = m2.get("QUANTITY");
						Object e = m2.get("EXPENSE");
						switch(df.toString()) {
						case "不分區":
							detail.setDataFormat(df.toString());
							if(dc != null) {
								detail.setDescChi(dc.toString());
							}
							if(ft != null) {
								detail.setFuncType(ft.toString());
							}
							if(q != null) {
								detail.setQuantity(Long.parseLong(q.toString()));
							}
							if(e != null) {
								detail.setExpense(Long.parseLong(e.toString()));
							}
							allList.add(detail);
							detail = new OwnExpenseQueryConditionDetail();
							break;
						case "門急診":
							detail.setDataFormat(df.toString());
							if(dc != null) {
								detail.setDescChi(dc.toString());
							}
							if(ft != null) {
								detail.setFuncType(ft.toString());
							}
							if(q != null) {
								detail.setQuantity(Long.parseLong(q.toString()));
							}
							if(e != null) {
								detail.setExpense(Long.parseLong(e.toString()));
							}
							opAllList.add(detail);
							detail = new OwnExpenseQueryConditionDetail();
							break;
						case "門診":
							detail.setDataFormat(df.toString());
							if(dc != null) {
								detail.setDescChi(dc.toString());
							}
							if(ft != null) {
								detail.setFuncType(ft.toString());
							}
							if(q != null) {
								detail.setQuantity(Long.parseLong(q.toString()));
							}
							if(e != null) {
								detail.setExpense(Long.parseLong(e.toString()));
							}
							opList.add(detail);
							detail = new OwnExpenseQueryConditionDetail();
							break;
						case "急診":
							detail.setDataFormat(df.toString());
							if(dc != null) {
								detail.setDescChi(dc.toString());
							}
							if(ft != null) {
								detail.setFuncType(ft.toString());
							}
							if(q != null) {
								detail.setQuantity(Long.parseLong(q.toString()));
							}
							if(e != null) {
								detail.setExpense(Long.parseLong(e.toString()));
							}
							emList.add(detail);
							detail = new OwnExpenseQueryConditionDetail();
							break;
						case "住院":
							detail.setDataFormat(df.toString());
							if(dc != null) {
								detail.setDescChi(dc.toString());
							}
							if(ft != null) {
								detail.setFuncType(ft.toString());
							}
							if(q != null) {
								detail.setQuantity(Long.parseLong(q.toString()));
							}
							if(e != null) {
								detail.setExpense(Long.parseLong(e.toString()));
							}
							ipList.add(detail);
							detail = new OwnExpenseQueryConditionDetail();
							break;
						}
					}
				}
				else {
					for(Map<String,Object> m2 : sqlList2_2) {
						Object df = m2.get("DATA_FORMAT");
						Object ft = m2.get("FUNC_TYPE");
						Object dc = m2.get("DESC_CHI");
						Object q = m2.get("QUANTITY");
						Object e = m2.get("EXPENSE");
						switch(df.toString()) {
						case "不分區":
							detail.setDataFormat(df.toString());
							if(dc != null) {
								detail.setDescChi(dc.toString());
							}
							if(ft != null) {
								detail.setFuncType(ft.toString());
							}
							if(q != null) {
								detail.setQuantity(Long.parseLong(q.toString()));
							}
							if(e != null) {
								detail.setExpense(Long.parseLong(e.toString()));
							}
							allList.add(detail);
							detail = new OwnExpenseQueryConditionDetail();
							break;
						case "門急診":
							detail.setDataFormat(df.toString());
							if(dc != null) {
								detail.setDescChi(dc.toString());
							}
							if(ft != null) {
								detail.setFuncType(ft.toString());
							}
							if(q != null) {
								detail.setQuantity(Long.parseLong(q.toString()));
							}
							if(e != null) {
								detail.setExpense(Long.parseLong(e.toString()));
							}
							opAllList.add(detail);
							detail = new OwnExpenseQueryConditionDetail();
							break;
						case "門診":
							detail.setDataFormat(df.toString());
							if(dc != null) {
								detail.setDescChi(dc.toString());
							}
							if(ft != null) {
								detail.setFuncType(ft.toString());
							}
							if(q != null) {
								detail.setQuantity(Long.parseLong(q.toString()));
							}
							if(e != null) {
								detail.setExpense(Long.parseLong(e.toString()));
							}
							opList.add(detail);
							detail = new OwnExpenseQueryConditionDetail();
							break;
						case "急診":
							detail.setDataFormat(df.toString());
							if(dc != null) {
								detail.setDescChi(dc.toString());
							}
							if(ft != null) {
								detail.setFuncType(ft.toString());
							}
							if(q != null) {
								detail.setQuantity(Long.parseLong(q.toString()));
							}
							if(e != null) {
								detail.setExpense(Long.parseLong(e.toString()));
							}
							emList.add(detail);
							detail = new OwnExpenseQueryConditionDetail();
							break;
						case "住院":
							detail.setDataFormat(df.toString());
							if(dc != null) {
								detail.setDescChi(dc.toString());
							}
							if(ft != null) {
								detail.setFuncType(ft.toString());
							}
							if(q != null) {
								detail.setQuantity(Long.parseLong(q.toString()));
							}
							if(e != null) {
								detail.setExpense(Long.parseLong(e.toString()));
							}
							ipList.add(detail);
							detail = new OwnExpenseQueryConditionDetail();
							break;
						}
					}

				}
			}
			else {
				for(Map<String,Object> m2 : sqlList2) {
					Object df = m2.get("DATA_FORMAT");
					Object ft = m2.get("FUNC_TYPE");
					Object dc = m2.get("DESC_CHI");
					Object q = m2.get("QUANTITY");
					Object e = m2.get("EXPENSE");
					switch(df.toString()) {
					case "不分區":
						detail.setDataFormat(df.toString());
						if(dc != null) {
							detail.setDescChi(dc.toString());
						}
						if(ft != null) {
							detail.setFuncType(ft.toString());
						}
						if(q != null) {
							detail.setQuantity(Long.parseLong(q.toString()));
						}
						if(e != null) {
							detail.setExpense(Long.parseLong(e.toString()));
						}
						allList.add(detail);
						detail = new OwnExpenseQueryConditionDetail();
						break;
					case "門急診":
						detail.setDataFormat(df.toString());
						if(dc != null) {
							detail.setDescChi(dc.toString());
						}
						if(ft != null) {
							detail.setFuncType(ft.toString());
						}
						if(q != null) {
							detail.setQuantity(Long.parseLong(q.toString()));
						}
						if(e != null) {
							detail.setExpense(Long.parseLong(e.toString()));
						}
						opAllList.add(detail);
						detail = new OwnExpenseQueryConditionDetail();
						break;
					case "門診":
						detail.setDataFormat(df.toString());
						if(dc != null) {
							detail.setDescChi(dc.toString());
						}
						if(ft != null) {
							detail.setFuncType(ft.toString());
						}
						if(q != null) {
							detail.setQuantity(Long.parseLong(q.toString()));
						}
						if(e != null) {
							detail.setExpense(Long.parseLong(e.toString()));
						}
						opList.add(detail);
						detail = new OwnExpenseQueryConditionDetail();
						break;
					case "急診":
						detail.setDataFormat(df.toString());
						if(dc != null) {
							detail.setDescChi(dc.toString());
						}
						if(ft != null) {
							detail.setFuncType(ft.toString());
						}
						if(q != null) {
							detail.setQuantity(Long.parseLong(q.toString()));
						}
						if(e != null) {
							detail.setExpense(Long.parseLong(e.toString()));
						}
						emList.add(detail);
						detail = new OwnExpenseQueryConditionDetail();
						break;
					case "住院":
						detail.setDataFormat(df.toString());
						if(dc != null) {
							detail.setDescChi(dc.toString());
						}
						if(ft != null) {
							detail.setFuncType(ft.toString());
						}
						if(q != null) {
							detail.setQuantity(Long.parseLong(q.toString()));
						}
						if(e != null) {
							detail.setExpense(Long.parseLong(e.toString()));
						}
						ipList.add(detail);
						detail = new OwnExpenseQueryConditionDetail();
						break;
					}
				}
			}
			
			model.setAllList(allList);
			model.setOpAllList(opAllList);		
			model.setOpList(opList);
			model.setEmList(emList);
			model.setIpList(ipList);
			modelList.add(model);
		    model = new OwnExpenseQueryCondition(); 
		    allList = new ArrayList<OwnExpenseQueryConditionDetail>();
		    opAllList = new ArrayList<OwnExpenseQueryConditionDetail>();
		    opList = new ArrayList<OwnExpenseQueryConditionDetail>();
		    emList = new ArrayList<OwnExpenseQueryConditionDetail>();
		    ipList = new ArrayList<OwnExpenseQueryConditionDetail>();
		    
		
		}
		
		return modelList;
	}

}
