package tw.com.leadtek.nhiwidget.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.dao.POINT_MONTHLYDao;
import tw.com.leadtek.nhiwidget.model.rdb.POINT_MONTHLY;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;
import tw.com.leadtek.nhiwidget.payload.report.AchievementQuarter;
import tw.com.leadtek.nhiwidget.payload.report.QuarterData;
import tw.com.leadtek.tools.StringUtility;

@Service
public class DbReportService {

	private Logger logger = LogManager.getLogger();

	@Autowired
	private POINT_MONTHLYDao pointMonthlyDao;

	public BaseResponse test() {
		BaseResponse res = new BaseResponse();
		res.setMessage("isdone");
		res.setResult("ok");
		return res;
	}

	public AchievementQuarter getAchievementAndExcess(String year, String quarter, boolean isLastM, boolean isLastY) {
		AchievementQuarter result = new AchievementQuarter();

		String[] years = StringUtility.splitBySpace(year);
		String[] quarters = StringUtility.splitBySpace(quarter);
		List<Object> yList = Arrays.asList(years);
		List<Object> mList = Arrays.asList(quarters);
		Map<String, Object> map = new HashMap<String,Object>();
		List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
		///如果年月為多個，則不能用上個月同條件相比
		if(years.length > 1) {
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
					String append = String.valueOf((y+1) * 100 + m);
					append = append.substring(0,append.length() - 2) + "/" + append.substring(append.length() - 2, append.length());
					map.put("displayName", "上個月同條件相比");
				} else {
					yList.add(years[0]);
					mList.add(m - 1);
					map.put("YM", String.valueOf((y * 100 ) + (m - 1)));
					map.put("Value", "M");
					String append = String.valueOf((y) * 100 + m);
					append = append.substring(0,append.length() - 2) + "/" + append.substring(append.length() - 2, append.length());
					map.put("displayName","上個月同條件相比");
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
				map.put("YM", String.valueOf((y * 100 ) + m));
				map.put("Value", "Y");
				String append = String.valueOf((y + 1) * 100 + m);
				append = append.substring(0,append.length() - 2) + "/" + append.substring(append.length() - 2, append.length());
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

		List<Integer> yearMonthBetween = findYearMonth(yList, mList);
		/// 這裡做排序，name才會對應正確值
		Collections.sort(yearMonthBetween);
//	
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
				///如果有條件帶入才進來
				if(mapList.size() > 0) {
					for(Map<String,Object> mm : mapList) {
						String ym =  mm.get("YM").toString();
						if(name.equals(ym)) {
							displayName = mm.get("displayName").toString();
						}
					}
				}
				
				calculateAchievementQuarter(result, pm, show,displayName);
			}

		}

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

	private void calculateAchievementQuarter(AchievementQuarter aq, POINT_MONTHLY pm, String name , String displayName) {
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

}
