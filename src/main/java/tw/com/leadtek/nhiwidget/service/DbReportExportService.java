package tw.com.leadtek.nhiwidget.service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.dao.CODE_TABLEDao;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_TABLE;
import tw.com.leadtek.nhiwidget.payload.report.AchievePointQueryCondition;
import tw.com.leadtek.nhiwidget.payload.report.AchievePointQueryConditionDetail;
import tw.com.leadtek.nhiwidget.payload.report.AchievementQuarter;
import tw.com.leadtek.nhiwidget.payload.report.DeductedNoteQueryCondition;
import tw.com.leadtek.nhiwidget.payload.report.DeductedNoteQueryConditionCode;
import tw.com.leadtek.nhiwidget.payload.report.DeductedNoteQueryConditionInfo;
import tw.com.leadtek.nhiwidget.payload.report.DeductedNoteQueryConditionList;
import tw.com.leadtek.nhiwidget.payload.report.DeductedNoteQueryConditionResponse;
import tw.com.leadtek.nhiwidget.payload.report.DrgQueryCoditionResponse;
import tw.com.leadtek.nhiwidget.payload.report.DrgQueryCondition;
import tw.com.leadtek.nhiwidget.payload.report.DrgQueryConditionDetail;
import tw.com.leadtek.nhiwidget.payload.report.OwnExpenseQueryCondition;
import tw.com.leadtek.nhiwidget.payload.report.OwnExpenseQueryConditionDetail;
import tw.com.leadtek.nhiwidget.payload.report.OwnExpenseQueryConditionIhnCodeInfo;
import tw.com.leadtek.nhiwidget.payload.report.OwnExpenseQueryConditionResponse;
import tw.com.leadtek.nhiwidget.payload.report.QuarterData;
import tw.com.leadtek.tools.StringUtility;

/**
 * 資料庫報表匯出專用service
 *
 */
@Service
public class DbReportExportService {

	@Autowired
	private DbReportService dbrService;

	@Autowired
	private CODE_TABLEDao code_TABLEDao;

	private List<CODE_TABLE> codeTableList;

	public final static String FILE_PATH = "download";

	/**
	 * 取得達成率與超額數-匯出
	 * 
	 * @param year
	 * @param quarter
	 * @param isLastM
	 * @param isLastY
	 * @param response
	 * @throws IOException
	 */
	public void getAchievementAndExcessExport(String year, String quarter, boolean isLastM, boolean isLastY,
			HttpServletResponse response) throws IOException {

		AchievementQuarter aqData = dbrService.getAchievementAndExcess(year, quarter, isLastM, isLastY);
		/// 資料size都相同
		List<QuarterData> qdAllList = aqData.getAll();
		List<QuarterData> qdOpList = aqData.getOp();
		List<QuarterData> qdIpList = aqData.getIp();

		List<QuarterData> qdAllListL = new ArrayList<QuarterData>();
		List<QuarterData> qdOpListL = new ArrayList<QuarterData>();
		List<QuarterData> qdIpListL = new ArrayList<QuarterData>();
		List<QuarterData> originListL = new ArrayList<QuarterData>();

		if (isLastM && isLastY) {

			List<Integer> indexs = new ArrayList<Integer>();
			for (int v = 0; v < qdAllList.size(); v++) {
				if (qdAllList.get(v).getDispalyName().equals("上個月同條件相比")) {
					qdAllListL.add(qdAllList.get(v));
					qdOpListL.add(qdOpList.get(v));
					qdIpListL.add(qdIpList.get(v));
					indexs.add(v);
				}
			}
			for (int i : indexs) {
				qdAllList.remove(i);
				qdOpList.remove(i);
				qdIpList.remove(i);
			}
			indexs.clear();
			for (int v = 0; v < qdAllList.size(); v++) {
				if (qdAllList.get(v).getDispalyName().equals("去年同期時段相比")) {
					qdAllListL.add(qdAllList.get(v));
					qdOpListL.add(qdOpList.get(v));
					qdIpListL.add(qdIpList.get(v));
					indexs.add(v);
				}
			}
			for (int i : indexs) {
				qdAllList.remove(i);
				qdOpList.remove(i);
				qdIpList.remove(i);
			}

			/// 用來顯示標題
			originListL.addAll(qdAllList);
			for (int v = 0; v < qdAllListL.size(); v++) {
				qdAllList.add(qdAllListL.get(v));
				qdOpList.add(qdOpListL.get(v));
				qdIpList.add(qdIpListL.get(v));
			}

		} else {
			if (isLastM) {
				List<Integer> indexs = new ArrayList<Integer>();
				for (int v = 0; v < qdAllList.size(); v++) {
					if (qdAllList.get(v).getDispalyName().equals("上個月同條件相比")) {
						qdAllListL.add(qdAllList.get(v));
						qdOpListL.add(qdOpList.get(v));
						qdIpListL.add(qdIpList.get(v));
						indexs.add(v);
					}
				}
				for (int i : indexs) {
					qdAllList.remove(0);
					qdOpList.remove(0);
					qdIpList.remove(0);
				}
				/// 用來顯示標題
				originListL.addAll(qdAllList);
				int z = 1;
				for (int v = 0; v < qdAllListL.size(); v++) {
					qdAllList.add(z, qdAllListL.get(v));
					qdOpList.add(z, qdOpListL.get(v));
					qdIpList.add(z, qdIpListL.get(v));
					z += 2;
				}

			}
			if (isLastY) {
				List<Integer> indexs = new ArrayList<Integer>();
				qdAllListL.clear();
				qdOpListL.clear();
				qdIpListL.clear();
				originListL.clear();
				for (int v = 0; v < qdAllList.size(); v++) {
					if (qdAllList.get(v).getDispalyName().equals("去年同期時段相比")) {
						qdAllListL.add(qdAllList.get(v));
						qdOpListL.add(qdOpList.get(v));
						qdIpListL.add(qdIpList.get(v));
						indexs.add(v);
					}
				}
				for (int i : indexs) {
					qdAllList.remove(0);
					qdOpList.remove(0);
					qdIpList.remove(0);
				}
				/// 用來顯示標題
				originListL.addAll(qdAllList);
				int z = 1;
				for (int v = 0; v < qdAllListL.size(); v++) {
					qdAllList.add(z, qdAllListL.get(v));
					qdOpList.add(z, qdOpListL.get(v));
					qdIpList.add(z, qdIpListL.get(v));
					z += 2;
				}
			}
		}

		// 建立新工作簿
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 樣式
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(HorizontalAlignment.LEFT);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellStyle.setBorderBottom(BorderStyle.MEDIUM);
		cellStyle.setBorderTop(BorderStyle.MEDIUM);
		cellStyle.setBorderLeft(BorderStyle.MEDIUM);
		cellStyle.setBorderRight(BorderStyle.MEDIUM);

		HSSFCellStyle cellTitleStyle = workbook.createCellStyle();
		cellTitleStyle.setAlignment(HorizontalAlignment.LEFT);
		cellTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellTitleStyle.setBorderBottom(BorderStyle.MEDIUM);
		cellTitleStyle.setBorderTop(BorderStyle.MEDIUM);
		cellTitleStyle.setBorderLeft(BorderStyle.MEDIUM);
		cellTitleStyle.setBorderRight(BorderStyle.MEDIUM);
		Font font = workbook.createFont();
		font.setColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
		cellTitleStyle.setFont(font);

		HSSFCellStyle cellFormatStyle = workbook.createCellStyle();
		cellFormatStyle.setAlignment(HorizontalAlignment.LEFT);
		cellFormatStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellFormatStyle.setBorderBottom(BorderStyle.MEDIUM);
		cellFormatStyle.setBorderTop(BorderStyle.MEDIUM);
		cellFormatStyle.setBorderLeft(BorderStyle.MEDIUM);
		cellFormatStyle.setBorderRight(BorderStyle.MEDIUM);
		cellFormatStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));

		int cellIndex = 0;
		int rowIndex = 0;
		/// 如過沒資料就會出空的
		if (qdAllList.size() != 0) {
			/// 新建工作表
			String[] tableRowHeaders = { "統計月份", "門急診/住院總案件數", "分配總點數", "申報總點數", "超額總點數", "達成率(%)" };
			String[] tableRowHeaders2 = { "統計月份", "門急診總案件數", "分配總點數", "申報總點數", "超額總點數", "達成率(%)" };
			String[] tableRowHeaders3 = { "統計月份", "住院總案件數", "分配總點數", "申報總點數", "超額總點數", "達成率(%)" };
			HSSFSheet sheet = workbook.createSheet("達成率與超額數");
			/// 欄位A1
			HSSFRow row = sheet.createRow(0);
			HSSFCell cell = row.createCell(0);
			cell.setCellValue("統計月份");
			cell.setCellStyle(cellStyle);
			/// 欄位B1
			cell = row.createCell(1);
			cellIndex = 2;
			for (int x = 0; x < originListL.size(); x++) {
				cell.setCellValue(originListL.get(x).getName());
				cell.setCellStyle(cellStyle);
				cell = row.createCell(cellIndex + x);
			}

			/// 欄位A2
			row = sheet.createRow(1);
			if (isLastM && isLastY) {
				cell = row.createCell(0);
				cell.setCellValue("上個月同條件相比");
				cell.setCellStyle(cellStyle);
				cell = row.createCell(1);
				cell.setCellValue("去年同期時段相比");
				cell.setCellStyle(cellStyle);
			} else {

				if (isLastM) {
					cell = row.createCell(0);
					cell.setCellValue("上個月同條件相比");
					cell.setCellStyle(cellStyle);
				}
				if (isLastY) {
					cell = row.createCell(0);
					cell.setCellValue("去年同期時段相比");
					cell.setCellStyle(cellStyle);
				}
			}

			/// 欄位A4
			row = sheet.createRow(3);
			rowIndex = 4;
			cellIndex = 0;
			for (int y = 0; y < tableRowHeaders.length; y++) {
				cell = row.createCell(cellIndex);
				cell.setCellValue(tableRowHeaders[y]);
				cell.setCellStyle(cellStyle);
				switch (y) {
				case 0:
					for (int x = 0; x < qdAllList.size(); x++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						String name = "";
						if (qdAllList.get(x).getDispalyName().isEmpty()) {
							name = qdAllList.get(x).getName();
						} else {
							name = qdAllList.get(x).getDispalyName();
						}
						cell.setCellValue(name);
						cell.setCellStyle(cellStyle);
					}
					cellIndex = 0;
					break;
				case 1:
					for (int x = 0; x < qdAllList.size(); x++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(qdAllList.get(x).getCases().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
					break;
				case 2:
					for (int x = 0; x < qdAllList.size(); x++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(qdAllList.get(x).getAssigned().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
					break;
				case 3:
					for (int x = 0; x < qdAllList.size(); x++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(qdAllList.get(x).getActual().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
					break;
				case 4:
					for (int x = 0; x < qdAllList.size(); x++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(qdAllList.get(x).getOver().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
					break;
				case 5:
					for (int x = 0; x < qdAllList.size(); x++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(qdAllList.get(x).getPercent() + "%");
						cell.setCellStyle(cellStyle);
					}
					cellIndex = 0;
					break;
				default:
					break;
				}

				row = sheet.createRow(rowIndex + y);
			}

			/// 欄位A11
			row = sheet.createRow(10);
			rowIndex = 11;
			cellIndex = 0;
			for (int y = 0; y < tableRowHeaders2.length; y++) {
				cell = row.createCell(cellIndex);
				cell.setCellValue(tableRowHeaders2[y]);
				cell.setCellStyle(cellStyle);
				switch (y) {
				case 0:
					for (int x = 0; x < qdOpList.size(); x++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						String name = "";
						if (qdOpList.get(x).getDispalyName().isEmpty()) {
							name = qdOpList.get(x).getName();
						} else {
							name = qdOpList.get(x).getDispalyName();
						}
						cell.setCellValue(name);
						cell.setCellStyle(cellStyle);
					}
					cellIndex = 0;
					break;
				case 1:
					for (int x = 0; x < qdOpList.size(); x++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(qdOpList.get(x).getCases().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
					break;
				case 2:
					for (int x = 0; x < qdOpList.size(); x++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(qdOpList.get(x).getAssigned().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
					break;
				case 3:
					for (int x = 0; x < qdOpList.size(); x++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(qdOpList.get(x).getActual().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
					break;
				case 4:
					for (int x = 0; x < qdOpList.size(); x++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(qdOpList.get(x).getOver().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
					break;
				case 5:
					for (int x = 0; x < qdOpList.size(); x++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(qdOpList.get(x).getPercent() + "%");
						cell.setCellStyle(cellStyle);
					}
					cellIndex = 0;
					break;
				default:
					break;
				}

				row = sheet.createRow(rowIndex + y);
			}

			/// 欄位A18
			row = sheet.createRow(17);
			rowIndex = 18;
			cellIndex = 0;
			for (int y = 0; y < tableRowHeaders3.length; y++) {
				cell = row.createCell(cellIndex);
				cell.setCellValue(tableRowHeaders3[y]);
				cell.setCellStyle(cellStyle);
				switch (y) {
				case 0:
					for (int x = 0; x < qdIpList.size(); x++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						String name = "";
						if (qdIpList.get(x).getDispalyName().isEmpty()) {
							name = qdIpList.get(x).getName();
						} else {
							name = qdIpList.get(x).getDispalyName();
						}
						cell.setCellValue(name);
						cell.setCellStyle(cellStyle);
					}
					cellIndex = 0;
					break;
				case 1:
					for (int x = 0; x < qdIpList.size(); x++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(qdIpList.get(x).getCases().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
					break;
				case 2:
					for (int x = 0; x < qdIpList.size(); x++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(qdIpList.get(x).getAssigned().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
					break;
				case 3:
					for (int x = 0; x < qdIpList.size(); x++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(qdIpList.get(x).getActual().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
					break;
				case 4:
					for (int x = 0; x < qdIpList.size(); x++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(qdIpList.get(x).getOver().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
					break;
				case 5:
					for (int x = 0; x < qdIpList.size(); x++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(qdIpList.get(x).getPercent() + "%");
						cell.setCellStyle(cellStyle);
					}
					cellIndex = 0;
					break;
				default:
					break;
				}

				row = sheet.createRow(rowIndex + y);
			}
			/// 最後設定autosize
			for (int i = 0; i < qdAllList.size() + 2; i++) {
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
			}

		} else {
			HSSFSheet sheet = workbook.createSheet("工作表");
		}

		String fileNameStr = "達成率與超額數";
		String fileName = URLEncoder.encode(fileNameStr, "UTF-8");
		String filepath = (System.getProperty("os.name").toLowerCase().startsWith("windows"))
				? FILE_PATH + "\\" + fileName
				: FILE_PATH + "/" + fileName;
		File file = new File(filepath);
		response.reset();
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "*");
		response.setHeader("Content-Disposition",
				"attachment; filename=" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + ".csv");
		response.setContentType("application/octet-stream;charset=utf8");

		/// 最後由outputstream輸出
		OutputStream out = response.getOutputStream();

		workbook.write(out);
		out.flush();
		out.close();
		workbook.close();

	}

	/**
	 * DRG案件數分佈佔率與定額、實際點數-匯出
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
	 * @param response
	 * @throws ParseException
	 * @throws IOException
	 */
	public void getDrgQueryConditionExport(String dateTypes, String year, String month, String betweenSdate,
			String betweenEdate, String sections, String drgCodes, String dataFormats, String funcTypes,
			String medNames, String icdcms, String medLogCodes, int applMin, int applMax, String icdAll, String payCode,
			String inhCode, boolean isShowDRGList, boolean isLastM, boolean isLastY, HttpServletResponse response)
			throws ParseException, IOException {
		DrgQueryCoditionResponse dataMap = dbrService.getDrgQueryCondition(dateTypes, year, month, betweenSdate,
				betweenEdate, sections, drgCodes, dataFormats, funcTypes, medNames, icdcms, medLogCodes, applMin,
				applMax, icdAll, payCode, inhCode, isShowDRGList, isLastM, isLastY);

		List<DrgQueryCondition> dataList = dataMap.getData();
		List<DrgQueryConditionDetail> total = new ArrayList<DrgQueryConditionDetail>();
		List<DrgQueryConditionDetail> sectionA = new ArrayList<DrgQueryConditionDetail>();
		List<DrgQueryConditionDetail> sectionB1 = new ArrayList<DrgQueryConditionDetail>();
		List<DrgQueryConditionDetail> sectionB2 = new ArrayList<DrgQueryConditionDetail>();
		List<DrgQueryConditionDetail> sectionC = new ArrayList<DrgQueryConditionDetail>();
		// 建立新工作簿
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 樣式
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(HorizontalAlignment.LEFT);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellStyle.setBorderBottom(BorderStyle.MEDIUM);
		cellStyle.setBorderTop(BorderStyle.MEDIUM);
		cellStyle.setBorderLeft(BorderStyle.MEDIUM);
		cellStyle.setBorderRight(BorderStyle.MEDIUM);

		HSSFCellStyle cellTitleStyle = workbook.createCellStyle();
		cellTitleStyle.setAlignment(HorizontalAlignment.CENTER);
		cellTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellTitleStyle.setBorderBottom(BorderStyle.MEDIUM);
		cellTitleStyle.setBorderTop(BorderStyle.MEDIUM);
		cellTitleStyle.setBorderLeft(BorderStyle.MEDIUM);
		cellTitleStyle.setBorderRight(BorderStyle.MEDIUM);
		cellTitleStyle.setWrapText(true);

		HSSFCellStyle cellFormatStyle = workbook.createCellStyle();
		cellFormatStyle.setAlignment(HorizontalAlignment.LEFT);
		cellFormatStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellFormatStyle.setBorderBottom(BorderStyle.MEDIUM);
		cellFormatStyle.setBorderTop(BorderStyle.MEDIUM);
		cellFormatStyle.setBorderLeft(BorderStyle.MEDIUM);
		cellFormatStyle.setBorderRight(BorderStyle.MEDIUM);
		cellFormatStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));

		int cellIndex = 0;
		int rowIndex = 0;
		String[] tableRowHeaders = { "統計月份", "住院DRG案件總數", "DRG案件申報總點數" };
		String[] tableRowHeadersA = { "統計月份", "A區DRG總案件數", "案件佔率", "案件病例總點數(不含自費)", "案件申報總點數", "點數差額" };
		String[] tableRowHeadersB1 = { "統計月份", "B1區DRG總案件數", "案件佔率", "案件病例總點數(不含自費)", "案件申報總點數", "點數差額" };
		String[] tableRowHeadersB2 = { "統計月份", "B2區DRG總案件數", "案件佔率", "案件病例總點數(不含自費)", "案件申報總點數", "點數差額" };
		String[] tableRowHeadersC = { "統計月份", "C區DRG總案件數", "案件佔率", "案件病例總點數(不含自費)", "案件申報總點數", "點數差額" };

		if (dataList.size() > 0) {
			List<DrgQueryCondition> lastData = new ArrayList<DrgQueryCondition>();
			if(dateTypes.equals("0")) {
				if(isLastM || isLastY) {
					
					for(DrgQueryCondition dic : dataList) {
						if(!dic.getDisplayName().isEmpty()) {
							lastData.add(dic);
						}
					}
					for(DrgQueryCondition dic :lastData) {
						dataList.remove(0);
					}
					dataList.addAll(lastData);
				}
				
			}
			HSSFSheet sheet = workbook.createSheet("DRG案件數分佈佔率與定額、實際點數");
			/// 欄位A1
			HSSFRow row = sheet.createRow(0);
			HSSFCell cell = row.createCell(0);
			cell.setCellValue("統計月份");
			cell.setCellStyle(cellStyle);
			/// 欄位B1
			cellIndex = 1;
			cell = row.createCell(cellIndex);
			for (DrgQueryCondition dic : dataList) {
				cell.setCellValue(dic.getDate());
				cell.setCellStyle(cellStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
			}

			cellIndex = 0;
			/// 欄位A2
			rowIndex = 1;
			row = sheet.createRow(rowIndex);
			if (drgCodes != null && (drgCodes.length() > 0 || isShowDRGList)) {
				cell = row.createCell(cellIndex);
				cell.setCellValue("DRG代碼");
				cell.setCellStyle(cellStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				if (drgCodes.length() > 0) {
					cell.setCellValue(drgCodes);
					cell.setCellStyle(cellTitleStyle);
				}
				if (isShowDRGList) {
					cell.setCellValue("DRG項目列出");
					cell.setCellStyle(cellTitleStyle);
				}
				cellIndex++;
				if (dataFormats == null || dataFormats.length() == 0)
					rowIndex++;
			}
			if (dataFormats != null && dataFormats.length() > 0) {
				cell = row.createCell(cellIndex);
				cell.setCellValue("就醫類別");
				cell.setCellStyle(cellStyle);
				cellIndex++;

				cell = row.createCell(cellIndex);
				String[] split = StringUtility.splitBySpace(dataFormats);
				String cellValue = "";
				for (String str : split) {
					switch (str) {
					case "all":
						cellValue += "不分區" + "、";
						break;
					case "totalop":
						cellValue += "門急診" + "、";
						break;
					case "op":
						cellValue += "門診" + "、";
						break;
					case "em":
						cellValue += "急診" + "、";
						break;
					case "ip":
						cellValue += "住院" + "、";
						break;
					}
				}
				cellValue = cellValue.substring(0, cellValue.length() - 1);
				cell.setCellValue(cellValue);
				cell.setCellStyle(cellTitleStyle);
				if ((drgCodes == null || drgCodes.length() == 0) && !isShowDRGList)
					rowIndex++;
			}

			if ((drgCodes != null && drgCodes.length() > 0 && isShowDRGList)
					&& (dataFormats != null && dataFormats.length() > 0))
				rowIndex++;

			cellIndex = 0;
			/// 欄位A
			row = sheet.createRow(rowIndex);
			if (funcTypes != null && funcTypes.length() > 0) {
				cell = row.createCell(cellIndex);
				cell.setCellValue("科別");
				cell.setCellStyle(cellStyle);
				cellIndex++;

				cell = row.createCell(cellIndex);
				cell.setCellValue(funcTypes);
				cell.setCellStyle(cellTitleStyle);
				cellIndex++;
				if (medNames == null || medNames.length() == 0)
					rowIndex++;
			}

			if (medNames != null && medNames.length() > 0) {
				cell = row.createCell(cellIndex);
				cell.setCellValue("醫護");
				cell.setCellStyle(cellStyle);
				cellIndex++;

				cell = row.createCell(cellIndex);
				cell.setCellValue(medNames);
				cell.setCellStyle(cellTitleStyle);
				cellIndex++;
				if (funcTypes == null || funcTypes.length() == 0)
					rowIndex++;
			}
			if ((funcTypes != null && funcTypes.length() > 0) && (medNames != null && medNames.length() > 0))
				rowIndex++;

			cellIndex = 0;
			/// 欄位A
			row = sheet.createRow(rowIndex);
			if (icdcms != null && icdcms.length() > 0) {
				cell = row.createCell(cellIndex);
				cell.setCellValue("病歷編號");
				cell.setCellStyle(cellStyle);
				cellIndex++;

				cell = row.createCell(cellIndex);
				cell.setCellValue(icdcms);
				cell.setCellStyle(cellStyle);
				cellIndex++;
				if (medLogCodes == null || medLogCodes.length() == 0)
					rowIndex++;
			}
			if (medLogCodes != null && medLogCodes.length() > 0) {
				cell = row.createCell(cellIndex);
				cell.setCellValue("就醫記錄");
				cell.setCellStyle(cellStyle);
				cellIndex++;

				cell = row.createCell(cellIndex);
				cell.setCellValue(medLogCodes);
				cell.setCellStyle(cellStyle);
				cellIndex++;
				if (icdcms == null || icdcms.length() == 0)
					rowIndex++;
			}
			if ((icdcms != null && icdcms.length() > 0) && (medLogCodes != null && medLogCodes.length() > 0))
				rowIndex++;

			cellIndex = 0;
			/// 欄位A
			row = sheet.createRow(rowIndex);
			if (applMax > 0 && applMin > 0) {
				cell = row.createCell(cellIndex);
				cell.setCellValue("單筆申報點數");
				cell.setCellStyle(cellStyle);
				cellIndex++;

				cell = row.createCell(cellIndex);
				cell.setCellValue("最小點數:");
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;

				cell = row.createCell(cellIndex);
				cell.setCellValue(applMin);
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;

				cell = row.createCell(cellIndex);
				cell.setCellValue("最大點數:");
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;

				cell = row.createCell(cellIndex);
				cell.setCellValue(applMax);
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				if (icdAll == null || icdAll.length() == 0)
					rowIndex++;
			}
			if (icdAll != null && icdAll.length() > 0) {
				cell = row.createCell(cellIndex);
				cell.setCellValue("不分區ICD碼");
				cell.setCellStyle(cellStyle);
				cellIndex++;

				cell = row.createCell(cellIndex);
				cell.setCellValue(icdAll);
				cell.setCellStyle(cellStyle);
				cellIndex++;
				if (applMax == 0 && applMin == 0)
					rowIndex++;
			}
			if ((icdAll != null && icdAll.length() > 0) && (applMin > 0 && applMax > 0))
				rowIndex++;

			cellIndex = 0;
			/// 欄位A
			row = sheet.createRow(rowIndex);
			if (payCode != null && payCode.length() > 0) {
				cell = row.createCell(cellIndex);
				cell.setCellValue("支付標準代碼");
				cell.setCellStyle(cellStyle);
				cellIndex++;

				cell = row.createCell(cellIndex);
				cell.setCellValue(payCode);
				cell.setCellStyle(cellStyle);
				cellIndex++;
				if (inhCode == null || inhCode.length() == 0)
					rowIndex++;
			}
			if (inhCode != null && inhCode.length() > 0) {
				cell = row.createCell(cellIndex);
				cell.setCellValue("院內碼");
				cell.setCellStyle(cellStyle);
				cellIndex++;

				cell = row.createCell(cellIndex);
				cell.setCellValue(inhCode);
				cell.setCellStyle(cellStyle);
				cellIndex++;
				if (payCode == null || payCode.length() == 0)
					rowIndex++;
			}
			if ((payCode != null && payCode.length() > 0) && (inhCode != null && inhCode.length() > 0))
				rowIndex++;
			
			if(sections != null && sections.length() > 1) {
				cell = row.createCell(cellIndex);
				cell.setCellValue("顯示區間");
				cell.setCellStyle(cellStyle);
				cellIndex++;

				cell = row.createCell(cellIndex);
				String[] split = StringUtility.splitBySpace(sections);
				String cellValue = "";
				for (String str : split) {
					switch (str.replaceAll("區", "")) {
					case "A":
						cellValue += "A區" + "、";
						break;
					case "B1":
						cellValue += "B1區" + "、";
						break;
					case "B2":
						cellValue += "B2區" + "、";
						break;
					case "C":
						cellValue += "C區" + "、";
						break;
					}
				}
				cellValue = cellValue.substring(0, cellValue.length() - 1);
				cell.setCellValue(cellValue);
				cell.setCellStyle(cellStyle);
				rowIndex++;
			}

			cellIndex = 0;
			/// 欄位A
			rowIndex++;
			row = sheet.createRow(rowIndex);
			for (int y = 0; y < tableRowHeaders.length; y++) {
				cell = row.createCell(cellIndex);
				cell.setCellValue(tableRowHeaders[y]);
				cell.setCellStyle(cellStyle);
				switch (y) {
				case 0:
					for (int x = 0; x < dataList.size(); x++) {
						String cellValue = "";
						total = dataList.get(x).getTotal();
						for (int v = 0; v < total.size(); v++) {
							cellIndex++;
							cell = row.createCell(cellIndex);

							String date = total.get(v).getDate();
							String code = total.get(v).getDrgCode();
							String displayName = total.get(v).getDisplayName();
							if (!code.isEmpty()) {
								cellValue = code;
							} else {
								if(displayName != null && displayName.length()>0) {
									cellValue = displayName;
								}
								else {
									cellValue = date;
								}
							}

							cell.setCellValue(cellValue);
							cell.setCellStyle(cellStyle);
						}
					}
					cellIndex = 0;
					break;
				case 1:
					for (int x = 0; x < dataList.size(); x++) {
						total = dataList.get(x).getTotal();
						for (int v = 0; v < total.size(); v++) {
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(total.get(v).getDrgQuantity());
							cell.setCellStyle(cellFormatStyle);

						}
					}
					cellIndex = 0;
					break;
				case 2:
					for (int x = 0; x < dataList.size(); x++) {
						total = dataList.get(x).getTotal();
						for (int v = 0; v < total.size(); v++) {
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(total.get(v).getDrgApplPoint());
							cell.setCellStyle(cellFormatStyle);

						}
					}
					cellIndex = 0;
					break;
				default:
					break;
				}
				rowIndex++;
				row = sheet.createRow(rowIndex);
			}

			cellIndex = 0;
			try {
				/// 欄位A
				rowIndex++;
				row = sheet.createRow(rowIndex);
				for (int y = 0; y < tableRowHeadersA.length; y++) {
					cell = row.createCell(cellIndex);
					cell.setCellValue(tableRowHeadersA[y]);
					cell.setCellStyle(cellStyle);

					switch (y) {
					case 0:
						for (int x = 0; x < dataList.size(); x++) {
							sectionA = dataList.get(x).getSectionA();
							String cellValue = "";

							for (int v = 0; v < sectionA.size(); v++) {
								cellIndex++;
								cell = row.createCell(cellIndex);

								String date = sectionA.get(v).getDate();
								String code = sectionA.get(v).getDrgCode();
								String displayName = sectionA.get(v).getDisplayName();
								if (!code.isEmpty()) {
									cellValue = code;
								} else {
									if(displayName != null && displayName.length()>0) {
										cellValue = displayName;
									}
									else {
										cellValue = date;
									}
								}

								cell.setCellValue(cellValue);
								cell.setCellStyle(cellStyle);
							}
						}
						cellIndex = 0;
						break;
					case 1:
						for (int x = 0; x < dataList.size(); x++) {
							sectionA = dataList.get(x).getSectionA();
							for (int v = 0; v < sectionA.size(); v++) {
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellValue(sectionA.get(v).getDrgQuantity());
								cell.setCellStyle(cellFormatStyle);

							}
						}
						cellIndex = 0;
						break;
					case 2:
						for (int x = 0; x < dataList.size(); x++) {
							sectionA = dataList.get(x).getSectionA();
							for (int v = 0; v < sectionA.size(); v++) {
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellValue(sectionA.get(v).getPercent().doubleValue() + "%");
								cell.setCellStyle(cellFormatStyle);

							}
						}
						cellIndex = 0;
						break;
					case 3:
						for (int x = 0; x < dataList.size(); x++) {
							sectionA = dataList.get(x).getSectionA();
							for (int v = 0; v < sectionA.size(); v++) {
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellValue(sectionA.get(v).getDrgActual());
								cell.setCellStyle(cellFormatStyle);
							}
						}
						cellIndex = 0;
						break;
					case 4:
						for (int x = 0; x < dataList.size(); x++) {
							sectionA = dataList.get(x).getSectionA();
							for (int v = 0; v < sectionA.size(); v++) {
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellValue(sectionA.get(v).getDrgApplPoint());
								cell.setCellStyle(cellFormatStyle);
							}
						}
						cellIndex = 0;
						break;
					case 5:
						for (int x = 0; x < dataList.size(); x++) {
							sectionA = dataList.get(x).getSectionA();
							for (int v = 0; v < sectionA.size(); v++) {
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellValue(sectionA.get(v).getDiff());
								cell.setCellStyle(cellFormatStyle);

							}
						}
						cellIndex = 0;
						break;
					default:
						break;
					}
					rowIndex++;
					row = sheet.createRow(rowIndex);
				}
				
				cellIndex = 0;
				/// 欄位A
				rowIndex++;
				row = sheet.createRow(rowIndex);
				for (int y = 0; y < tableRowHeadersB1.length; y++) {
					cell = row.createCell(cellIndex);
					cell.setCellValue(tableRowHeadersB1[y]);
					cell.setCellStyle(cellStyle);

					switch (y) {
					case 0:
						for (int x = 0; x < dataList.size(); x++) {
							sectionB1 = dataList.get(x).getSectionB1();
							String cellValue = "";

							for (int v = 0; v < sectionB1.size(); v++) {
								cellIndex++;
								cell = row.createCell(cellIndex);

								String date = sectionB1.get(v).getDate();
								String code = sectionB1.get(v).getDrgCode();
								String displayName = sectionB1.get(v).getDisplayName();
								if (!code.isEmpty()) {
									cellValue = code;
								} else {
									if(displayName != null && displayName.length()>0) {
										cellValue = displayName;
									}
									else {
										cellValue = date;
									}
								}

								cell.setCellValue(cellValue);
								cell.setCellStyle(cellStyle);
							}
						}
						cellIndex = 0;
						break;
					case 1:
						for (int x = 0; x < dataList.size(); x++) {
							sectionB1 = dataList.get(x).getSectionB1();
							for (int v = 0; v < sectionB1.size(); v++) {
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellValue(sectionB1.get(v).getDrgQuantity());
								cell.setCellStyle(cellFormatStyle);

							}
						}
						cellIndex = 0;
						break;
					case 2:
						for (int x = 0; x < dataList.size(); x++) {
							sectionB1 = dataList.get(x).getSectionB1();
							for (int v = 0; v < sectionB1.size(); v++) {
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellValue(sectionB1.get(v).getPercent().doubleValue() + "%");
								cell.setCellStyle(cellFormatStyle);

							}
						}
						cellIndex = 0;
						break;
					case 3:
						for (int x = 0; x < dataList.size(); x++) {
							sectionB1 = dataList.get(x).getSectionB1();
							for (int v = 0; v < sectionB1.size(); v++) {
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellValue(sectionB1.get(v).getDrgActual());
								cell.setCellStyle(cellFormatStyle);
							}
						}
						cellIndex = 0;
						break;
					case 4:
						for (int x = 0; x < dataList.size(); x++) {
							sectionB1 = dataList.get(x).getSectionB1();
							for (int v = 0; v < sectionB1.size(); v++) {
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellValue(sectionB1.get(v).getDrgApplPoint());
								cell.setCellStyle(cellFormatStyle);
							}
						}
						cellIndex = 0;
						break;
					case 5:
						for (int x = 0; x < dataList.size(); x++) {
							sectionB1 = dataList.get(x).getSectionB1();
							for (int v = 0; v < sectionB1.size(); v++) {
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellValue(sectionB1.get(v).getDiff());
								cell.setCellStyle(cellFormatStyle);

							}
						}
						cellIndex = 0;
						break;
					default:
						break;
					}
					rowIndex++;
					row = sheet.createRow(rowIndex);
				}
				
				cellIndex = 0;
				/// 欄位A
				rowIndex++;
				row = sheet.createRow(rowIndex);
				for (int y = 0; y < tableRowHeadersB2.length; y++) {
					cell = row.createCell(cellIndex);
					cell.setCellValue(tableRowHeadersB2[y]);
					cell.setCellStyle(cellStyle);

					switch (y) {
					case 0:
						for (int x = 0; x < dataList.size(); x++) {
							sectionB2 = dataList.get(x).getSectionB2();
							String cellValue = "";

							for (int v = 0; v < sectionB2.size(); v++) {
								cellIndex++;
								cell = row.createCell(cellIndex);

								String date = sectionB2.get(v).getDate();
								String code = sectionB2.get(v).getDrgCode();
								String displayName = sectionB2.get(v).getDisplayName();
								if (!code.isEmpty()) {
									cellValue = code;
								} else {
									if(displayName != null && displayName.length()>0) {
										cellValue = displayName;
									}
									else {
										cellValue = date;
									}
								}

								cell.setCellValue(cellValue);
								cell.setCellStyle(cellStyle);
							}
						}
						cellIndex = 0;
						break;
					case 1:
						for (int x = 0; x < dataList.size(); x++) {
							sectionB2 = dataList.get(x).getSectionB2();
							for (int v = 0; v < sectionB2.size(); v++) {
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellValue(sectionB2.get(v).getDrgQuantity());
								cell.setCellStyle(cellFormatStyle);

							}
						}
						cellIndex = 0;
						break;
					case 2:
						for (int x = 0; x < dataList.size(); x++) {
							sectionB2 = dataList.get(x).getSectionB2();
							for (int v = 0; v < sectionB2.size(); v++) {
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellValue(sectionB2.get(v).getPercent().doubleValue() + "%");
								cell.setCellStyle(cellFormatStyle);

							}
						}
						cellIndex = 0;
						break;
					case 3:
						for (int x = 0; x < dataList.size(); x++) {
							sectionB2 = dataList.get(x).getSectionB2();
							for (int v = 0; v < sectionB2.size(); v++) {
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellValue(sectionB2.get(v).getDrgActual());
								cell.setCellStyle(cellFormatStyle);
							}
						}
						cellIndex = 0;
						break;
					case 4:
						for (int x = 0; x < dataList.size(); x++) {
							sectionB2 = dataList.get(x).getSectionB2();
							for (int v = 0; v < sectionB2.size(); v++) {
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellValue(sectionB2.get(v).getDrgApplPoint());
								cell.setCellStyle(cellFormatStyle);
							}
						}
						cellIndex = 0;
						break;
					case 5:
						for (int x = 0; x < dataList.size(); x++) {
							sectionB2 = dataList.get(x).getSectionB2();
							for (int v = 0; v < sectionB2.size(); v++) {
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellValue(sectionB2.get(v).getDiff());
								cell.setCellStyle(cellFormatStyle);

							}
						}
						cellIndex = 0;
						break;
					default:
						break;
					}
					rowIndex++;
					row = sheet.createRow(rowIndex);
				}
				
				cellIndex = 0;
				/// 欄位A
				rowIndex++;
				row = sheet.createRow(rowIndex);
				for (int y = 0; y < tableRowHeadersC.length; y++) {
					cell = row.createCell(cellIndex);
					cell.setCellValue(tableRowHeadersC[y]);
					cell.setCellStyle(cellStyle);

					switch (y) {
					case 0:
						for (int x = 0; x < dataList.size(); x++) {
							sectionC = dataList.get(x).getSectionC();
							String cellValue = "";

							for (int v = 0; v < sectionC.size(); v++) {
								cellIndex++;
								cell = row.createCell(cellIndex);

								String date = sectionC.get(v).getDate();
								String code = sectionC.get(v).getDrgCode();
								String displayName = sectionC.get(v).getDisplayName();
								if (!code.isEmpty()) {
									cellValue = code;
								} else {
									if(displayName != null && displayName.length()>0) {
										cellValue = displayName;
									}
									else {
										cellValue = date;
									}
								}

								cell.setCellValue(cellValue);
								cell.setCellStyle(cellStyle);
							}
						}
						cellIndex = 0;
						break;
					case 1:
						for (int x = 0; x < dataList.size(); x++) {
							sectionC = dataList.get(x).getSectionC();
							for (int v = 0; v < sectionC.size(); v++) {
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellValue(sectionC.get(v).getDrgQuantity());
								cell.setCellStyle(cellFormatStyle);

							}
						}
						cellIndex = 0;
						break;
					case 2:
						for (int x = 0; x < dataList.size(); x++) {
							sectionC = dataList.get(x).getSectionC();
							for (int v = 0; v < sectionC.size(); v++) {
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellValue(sectionC.get(v).getPercent().doubleValue() + "%");
								cell.setCellStyle(cellFormatStyle);

							}
						}
						cellIndex = 0;
						break;
					case 3:
						for (int x = 0; x < dataList.size(); x++) {
							sectionC = dataList.get(x).getSectionC();
							for (int v = 0; v < sectionC.size(); v++) {
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellValue(sectionC.get(v).getDrgActual());
								cell.setCellStyle(cellFormatStyle);
							}
						}
						cellIndex = 0;
						break;
					case 4:
						for (int x = 0; x < dataList.size(); x++) {
							sectionC = dataList.get(x).getSectionC();
							for (int v = 0; v < sectionC.size(); v++) {
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellValue(sectionC.get(v).getDrgApplPoint());
								cell.setCellStyle(cellFormatStyle);
							}
						}
						cellIndex = 0;
						break;
					case 5:
						for (int x = 0; x < dataList.size(); x++) {
							sectionC = dataList.get(x).getSectionC();
							for (int v = 0; v < sectionC.size(); v++) {
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellValue(sectionC.get(v).getDiff());
								cell.setCellStyle(cellFormatStyle);

							}
						}
						cellIndex = 0;
						break;
					default:
						break;
					}
					rowIndex++;
					row = sheet.createRow(rowIndex);
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
			

			/// 最後設定autosize
			for (int i = 0; i < tableRowHeadersA.length + 2; i++) {
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
			}

		} else {
			HSSFSheet sheet = workbook.createSheet("工作表");
		}
		String fileNameStr = "DRG案件數分佈佔率與定額與實際點數";
		String fileName = URLEncoder.encode(fileNameStr, "UTF-8");
		String filepath = (System.getProperty("os.name").toLowerCase().startsWith("windows"))
				? FILE_PATH + "\\" + fileName
				: FILE_PATH + "/" + fileName;
		File file = new File(filepath);
		response.reset();
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "*");
		response.setHeader("Content-Disposition",
				"attachment; filename=" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + ".csv");
		response.setContentType("application/octet-stream;charset=utf8");

		/// 最後由outputstream輸出
		OutputStream out = response.getOutputStream();

		workbook.write(out);
		out.flush();
		out.close();
		workbook.close();

	}

	/**
	 * 取得自費項目清單-匯出
	 * 
	 * @param betweenSDate
	 * @param betweenEDate
	 * @param dataFormats
	 * @param funcTypes
	 * @param medNames
	 * @param icdAll
	 * @param payCode
	 * @param inhCode
	 * @param isLastY
	 * @param isShowOwnExpense
	 * @param response
	 */
	public void getOwnExpenseQueryConditionExport(String betweenSDate, String betweenEDate, String dataFormats,
			String funcTypes, String medNames, String icdAll, String payCode, String inhCode, boolean isLastY,
			boolean isShowOwnExpense, HttpServletResponse response) throws IOException {

		OwnExpenseQueryConditionResponse res = dbrService.getOwnExpenseQueryCondition(betweenSDate, betweenEDate,
				dataFormats, funcTypes, medNames, icdAll, payCode, inhCode, isLastY, isShowOwnExpense);
		List<OwnExpenseQueryCondition> modelList = res.getData();

		// 建立新工作簿
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 樣式
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(HorizontalAlignment.LEFT);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellStyle.setBorderBottom(BorderStyle.MEDIUM);
		cellStyle.setBorderTop(BorderStyle.MEDIUM);
		cellStyle.setBorderLeft(BorderStyle.MEDIUM);
		cellStyle.setBorderRight(BorderStyle.MEDIUM);

		HSSFCellStyle cellTitleStyle = workbook.createCellStyle();
		cellTitleStyle.setAlignment(HorizontalAlignment.CENTER);
		cellTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellTitleStyle.setBorderBottom(BorderStyle.MEDIUM);
		cellTitleStyle.setBorderTop(BorderStyle.MEDIUM);
		cellTitleStyle.setBorderLeft(BorderStyle.MEDIUM);
		cellTitleStyle.setBorderRight(BorderStyle.MEDIUM);
		cellTitleStyle.setWrapText(true);

		HSSFCellStyle cellFormatStyle = workbook.createCellStyle();
		cellFormatStyle.setAlignment(HorizontalAlignment.LEFT);
		cellFormatStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellFormatStyle.setBorderBottom(BorderStyle.MEDIUM);
		cellFormatStyle.setBorderTop(BorderStyle.MEDIUM);
		cellFormatStyle.setBorderLeft(BorderStyle.MEDIUM);
		cellFormatStyle.setBorderRight(BorderStyle.MEDIUM);
		cellFormatStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));

		int cellIndex = 0;
		int rowIndex = 0;
		String[] tableRowHeaders = { "就醫日期-起", "就醫日期-訖" };

		HSSFSheet sheet = workbook.createSheet("自費項目清單");
		/// 欄位A1
		HSSFRow row = sheet.createRow(0);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue("統計月份");
		cell.setCellStyle(cellStyle);
		/// 欄位B1
		cell = row.createCell(1);
		cell.setCellValue(betweenSDate + " " + betweenEDate);
		cell.setCellStyle(cellStyle);
		if (isShowOwnExpense) {
			cell = row.createCell(2);
			cell.setCellValue("自費分項列出");
			cell.setCellStyle(cellStyle);
			cell = row.createCell(3);
			cell.setCellValue("顯示");
			cell.setCellStyle(cellStyle);
		}

		String cellValue = "";
		String dataFormatAppend = "";

		cellIndex = 0;
		/// 欄位A2
		rowIndex = 1;
		row = sheet.createRow(rowIndex);
		if (dataFormats != null && dataFormats.length() > 0) {
			cell = row.createCell(cellIndex);
			cell.setCellValue("就醫類別");
			cell.setCellStyle(cellStyle);
			cellIndex++;

			cell = row.createCell(cellIndex);
			String[] split = StringUtility.splitBySpace(dataFormats);
			cellValue = "";
			for (String str : split) {
				switch (str) {
				case "all":
					cellValue += "不分區" + "、";
					break;
				case "totalop":
					cellValue += "門急診" + "、";
					break;
				case "op":
					cellValue += "門診" + "、";
					break;
				case "em":
					cellValue += "急診" + "、";
					break;
				case "ip":
					cellValue += "住院" + "、";
					break;
				}
			}
			cellValue = cellValue.substring(0, cellValue.length() - 1);
			dataFormatAppend = cellValue;
			cell.setCellValue(cellValue);
			cell.setCellStyle(cellTitleStyle);
			rowIndex++;
		}

		cellIndex = 0;
		/// 欄位A
		row = sheet.createRow(rowIndex);
		if (funcTypes != null && funcTypes.length() > 0) {
			cell = row.createCell(cellIndex);
			cell.setCellValue("科別");
			cell.setCellStyle(cellStyle);
			cellIndex++;

			cell = row.createCell(cellIndex);
			cell.setCellValue(funcTypes);
			cell.setCellStyle(cellTitleStyle);
			cellIndex++;
			if (medNames == null || medNames.length() == 0)
				rowIndex++;
		}

		if (medNames != null && medNames.length() > 0) {
			cell = row.createCell(cellIndex);
			cell.setCellValue("醫護");
			cell.setCellStyle(cellStyle);
			cellIndex++;

			cell = row.createCell(cellIndex);
			cell.setCellValue(medNames);
			cell.setCellStyle(cellTitleStyle);
			cellIndex++;
			if (funcTypes == null || funcTypes.length() == 0)
				rowIndex++;
		}
		if ((funcTypes != null && funcTypes.length() > 0) && (medNames != null && medNames.length() > 0))
			rowIndex++;

		cellIndex = 0;

		/// 欄位A
		row = sheet.createRow(rowIndex);
		if (icdAll != null && icdAll.length() > 0) {
			cell = row.createCell(cellIndex);
			cell.setCellValue("不分區ICD碼");
			cell.setCellStyle(cellStyle);
			cellIndex++;

			cell = row.createCell(cellIndex);
			cell.setCellValue(icdAll);
			cell.setCellStyle(cellStyle);
			cellIndex++;

			rowIndex++;
		}

		cellIndex = 0;
		/// 欄位A
		row = sheet.createRow(rowIndex);
		if (payCode != null && payCode.length() > 0) {
			cell = row.createCell(cellIndex);
			cell.setCellValue("支付標準代碼");
			cell.setCellStyle(cellStyle);
			cellIndex++;

			cell = row.createCell(cellIndex);
			cell.setCellValue(payCode);
			cell.setCellStyle(cellStyle);
			cellIndex++;
			if (inhCode == null || inhCode.length() == 0)
				rowIndex++;
		}
		if (inhCode != null && inhCode.length() > 0) {
			cell = row.createCell(cellIndex);
			cell.setCellValue("院內碼");
			cell.setCellStyle(cellStyle);
			cellIndex++;

			cell = row.createCell(cellIndex);
			cell.setCellValue(inhCode);
			cell.setCellStyle(cellStyle);
			cellIndex++;
			if (payCode == null || payCode.length() == 0)
				rowIndex++;
		}
		if ((payCode != null && payCode.length() > 0) && (inhCode != null && inhCode.length() > 0))
			rowIndex++;

		cellIndex = 0;
		/// 欄位A
		rowIndex++;
		rowIndex++;
		row = sheet.createRow(rowIndex);
		for (int x = 0; x < tableRowHeaders.length; x++) {
			switch (x) {
			case 0:
				cell = row.createCell(x);
				cell.setCellValue(tableRowHeaders[x]);
				cell.setCellStyle(cellStyle);
				break;
			case 1:
				cell = row.createCell(x);
				cell.setCellValue(tableRowHeaders[x]);
				cell.setCellStyle(cellStyle);
				break;
			}
		}

		/// 欄位C
		cellIndex = 2;
		int count = 0;
		for (OwnExpenseQueryCondition model : modelList) {
			if (count == 0) {
				cell = row.createCell(cellIndex);
				if (model.getAllQuantity() != null) {
					cell.setCellValue("不分區總案件數");
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					if (modelList.size() > 1) {
						cell.setCellValue("去年同期時段相比");
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
					}
					cell.setCellValue("不分區自費總金額");
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					if (modelList.size() > 1) {
						cell.setCellValue("去年同期時段相比");
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
					}
				}
				if (model.getOpAllQuantity() != null) {
					cell.setCellValue("門急診總案件數");
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					if (modelList.size() > 1) {
						cell.setCellValue("去年同期時段相比");
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
					}
					cell.setCellValue("門急診自費總金額");
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					if (modelList.size() > 1) {
						cell.setCellValue("去年同期時段相比");
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
					}
				}
				if (model.getOpQuantity() != null) {
					cell.setCellValue("門診總案件數");
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					if (modelList.size() > 1) {
						cell.setCellValue("去年同期時段相比");
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
					}
					cell.setCellValue("門診自費總金額");
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					if (modelList.size() > 1) {
						cell.setCellValue("去年同期時段相比");
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
					}
				}
				if (model.getEmQuantity() != null) {
					cell.setCellValue("急診總案件數");
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					if (modelList.size() > 1) {
						cell.setCellValue("去年同期時段相比");
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
					}
					cell.setCellValue("急診自費總金額");
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					if (modelList.size() > 1) {
						cell.setCellValue("去年同期時段相比");
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
					}
				}
				if (model.getIpQuantity() != null) {
					cell.setCellValue("住院總案件數");
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					if (modelList.size() > 1) {
						cell.setCellValue("去年同期時段相比");
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
					}
					cell.setCellValue("住院自費總金額");
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					if (modelList.size() > 1) {
						cell.setCellValue("去年同期時段相比");
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
					}
				}
			}
			count++;
			break;
		}
		count = 0;
		rowIndex++;
		cellIndex = 0;
		/// 欄位A
		row = sheet.createRow(rowIndex);
		cell = row.createCell(cellIndex);
		cell.setCellValue(betweenSDate);
		cell.setCellStyle(cellStyle);
		cellIndex++;
		cell = row.createCell(cellIndex);
		cell.setCellValue(betweenEDate);
		cell.setCellStyle(cellStyle);

		OwnExpenseQueryCondition model = modelList.get(0);
		OwnExpenseQueryCondition model2 = new OwnExpenseQueryCondition();
		/// 欄位C
		cellIndex++;
		cell = row.createCell(cellIndex);
		if (model.getAllQuantity() != null) {
			cell.setCellValue(model.getAllQuantity().doubleValue());
			cell.setCellStyle(cellFormatStyle);
			cellIndex++;
			cell = row.createCell(cellIndex);
			if (modelList.size() > 1) {
				model2 = modelList.get(1);
				cell.setCellValue(model2.getAllQuantity().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
			}
			cell.setCellValue(model.getAllExpense().doubleValue());
			cell.setCellStyle(cellFormatStyle);
			cellIndex++;
			cell = row.createCell(cellIndex);
			if (modelList.size() > 1) {
				model2 = modelList.get(1);
				cell.setCellValue(model2.getAllExpense().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
			}
		}
		if (model.getOpAllQuantity() != null) {
			cell.setCellValue(model.getOpAllQuantity().doubleValue());
			cell.setCellStyle(cellFormatStyle);
			cellIndex++;
			cell = row.createCell(cellIndex);
			if (modelList.size() > 1) {
				model2 = modelList.get(1);
				cell.setCellValue(model2.getOpAllQuantity().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
			}
			cell.setCellValue(model.getOpAllExpense().doubleValue());
			cell.setCellStyle(cellFormatStyle);
			cellIndex++;
			cell = row.createCell(cellIndex);
			if (modelList.size() > 1) {
				model2 = modelList.get(1);
				cell.setCellValue(model2.getOpAllExpense().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
			}
		}
		if (model.getOpQuantity() != null) {
			cell.setCellValue(model.getOpQuantity().doubleValue());
			cell.setCellStyle(cellFormatStyle);
			cellIndex++;
			cell = row.createCell(cellIndex);
			if (modelList.size() > 1) {
				model2 = modelList.get(1);
				cell.setCellValue(model2.getOpQuantity().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
			}
			cell.setCellValue(model.getOpExpense().doubleValue());
			cell.setCellStyle(cellFormatStyle);
			cellIndex++;
			cell = row.createCell(cellIndex);
			if (modelList.size() > 1) {
				model2 = modelList.get(1);
				cell.setCellValue(model2.getOpExpense().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
			}
		}
		if (model.getEmQuantity() != null) {
			cell.setCellValue(model.getEmQuantity().doubleValue());
			cell.setCellStyle(cellFormatStyle);
			cellIndex++;
			cell = row.createCell(cellIndex);
			if (modelList.size() > 1) {
				model2 = modelList.get(1);
				cell.setCellValue(model2.getEmQuantity().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
			}
			cell.setCellValue(model.getEmQuantity().doubleValue());
			cell.setCellStyle(cellFormatStyle);
			cellIndex++;
			cell = row.createCell(cellIndex);
			if (modelList.size() > 1) {
				model2 = modelList.get(1);
				cell.setCellValue(model2.getEmExpense().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
			}
		}
		if (model.getIpQuantity() != null) {
			cell.setCellValue(model.getIpQuantity().doubleValue());
			cell.setCellStyle(cellFormatStyle);
			cellIndex++;
			cell = row.createCell(cellIndex);
			if (modelList.size() > 1) {
				model2 = modelList.get(1);
				cell.setCellValue(model2.getIpQuantity().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
			}
			cell.setCellValue(model.getIpExpense().doubleValue());
			cell.setCellStyle(cellFormatStyle);
			cellIndex++;
			cell = row.createCell(cellIndex);
			if (modelList.size() > 1) {
				model2 = modelList.get(1);
				cell.setCellValue(model2.getIpExpense().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
			}
		}
		cellIndex = 0;
		rowIndex++;
		rowIndex++;
		/// 欄位A
		row = sheet.createRow(rowIndex);
		cell = row.createCell(0);
		cell.setCellValue("科別");
		cell.setCellStyle(cellTitleStyle);
		cell = row.createCell(1);
		cell.setCellStyle(cellTitleStyle);
		sheet.addMergedRegionUnsafe(new CellRangeAddress(rowIndex, rowIndex, 0, 1));

		/// 欄位C
		cellIndex = 2;
		count = 0;
		for (OwnExpenseQueryCondition models : modelList) {
			if (count == 0) {
				cell = row.createCell(cellIndex);
				if (models.getAllQuantity() != null) {
					cell.setCellValue("不分區總案件數");
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					if (modelList.size() > 1) {
						cell.setCellValue("去年同期時段相比");
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
					}
					cell.setCellValue("不分區自費總金額");
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					if (modelList.size() > 1) {
						cell.setCellValue("去年同期時段相比");
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
					}
				}
				if (models.getOpAllQuantity() != null) {
					cell.setCellValue("門急診總案件數");
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					if (modelList.size() > 1) {
						cell.setCellValue("去年同期時段相比");
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
					}
					cell.setCellValue("門急診自費總金額");
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					if (modelList.size() > 1) {
						cell.setCellValue("去年同期時段相比");
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
					}
				}
				if (models.getOpQuantity() != null) {
					cell.setCellValue("門診總案件數");
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					if (modelList.size() > 1) {
						cell.setCellValue("去年同期時段相比");
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
					}
					cell.setCellValue("門診自費總金額");
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					if (modelList.size() > 1) {
						cell.setCellValue("去年同期時段相比");
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
					}
				}
				if (models.getEmQuantity() != null) {
					cell.setCellValue("急診總案件數");
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					if (modelList.size() > 1) {
						cell.setCellValue("去年同期時段相比");
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
					}
					cell.setCellValue("急診自費總金額");
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					if (modelList.size() > 1) {
						cell.setCellValue("去年同期時段相比");
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
					}
				}
				if (models.getIpQuantity() != null) {
					cell.setCellValue("住院總案件數");
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					if (modelList.size() > 1) {
						cell.setCellValue("去年同期時段相比");
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
					}
					cell.setCellValue("住院自費總金額");
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					if (modelList.size() > 1) {
						cell.setCellValue("去年同期時段相比");
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
					}
				}
			}
			count++;
			break;
		}
		count = 0;
		cellIndex = 0;
		rowIndex++;
		try {
			List<OwnExpenseQueryConditionDetail> allList = model.getAllList();
			List<OwnExpenseQueryConditionDetail> opAllList = model.getOpAllList();
			List<OwnExpenseQueryConditionDetail> opList = model.getOpList();
			List<OwnExpenseQueryConditionDetail> emList = model.getEmList();
			List<OwnExpenseQueryConditionDetail> ipList = model.getIpList();

			List<OwnExpenseQueryConditionDetail> buildList = new ArrayList<OwnExpenseQueryConditionDetail>();
			/// 欄位A
			if (allList.size() > 0
					&& (opAllList.size() == 0 && opList.size() == 0 && emList.size() == 0 && ipList.size() == 0)) {
				buildList.addAll(allList);
			} else if (opAllList.size() > 0
					&& (allList.size() == 0 && opList.size() == 0 && emList.size() == 0 && ipList.size() == 0)) {
				buildList.addAll(opAllList);
			} else if (opList.size() > 0
					&& (opAllList.size() == 0 && allList.size() == 0 && emList.size() == 0 && ipList.size() == 0)) {
				buildList.addAll(opList);
			} else if (emList.size() > 0
					&& (opAllList.size() == 0 && opList.size() == 0 && allList.size() == 0 && ipList.size() == 0)) {
				buildList.addAll(emList);
			} else if (ipList.size() > 0
					&& (opAllList.size() == 0 && opList.size() == 0 && emList.size() == 0 && allList.size() == 0)) {
				buildList.addAll(ipList);
			}

			if (buildList.size() > 0) {
				for (int i = 0; i < buildList.size(); i++) {
					row = sheet.createRow(rowIndex);
					cell = row.createCell(cellIndex);
					if (buildList.get(i).getFuncType().length() < 3) {

						cell.setCellValue(buildList.get(i).getDescChi());
					} else {
						cell.setCellValue(buildList.get(i).getFuncType());
					}
					cell.setCellStyle(cellTitleStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellStyle(cellTitleStyle);
					sheet.addMergedRegionUnsafe(new CellRangeAddress(rowIndex, rowIndex, 0, 1));
					cellIndex++;
					cell = row.createCell(cellIndex);
					if (allList.size() > 0 && dataFormatAppend.contains("不分區")) {
						int v = 0;
						for (OwnExpenseQueryConditionDetail m : allList) {
							if (buildList.get(i).getFuncType().equals(m.getFuncType())) {
								cell.setCellValue(modelList.get(0).getAllList().get(v).getQuantity().doubleValue());
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								if (modelList.size() > 1) {
									cell.setCellValue(modelList.get(1).getAllList().get(v).getQuantity().doubleValue());
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
								}
								cell.setCellValue(modelList.get(0).getAllList().get(v).getExpense().doubleValue());
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								if (modelList.size() > 1) {
									cell.setCellValue(modelList.get(1).getAllList().get(v).getExpense().doubleValue());
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
								}
								break;
							}
							v++;
						}
					} else if (allList.size() == 0 && dataFormatAppend.contains("不分區")) {
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						if (isLastY) {
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
						}

					}
					if (opAllList.size() > 0 && dataFormatAppend.contains("門急診")) {
						int v = 0;
						for (OwnExpenseQueryConditionDetail m : opAllList) {
							if (buildList.get(i).getFuncType().equals(m.getFuncType())) {
								cell.setCellValue(modelList.get(0).getOpAllList().get(v).getQuantity().doubleValue());
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								if (modelList.size() > 1) {
									cell.setCellValue(
											modelList.get(1).getOpAllList().get(v).getQuantity().doubleValue());
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
								}
								cell.setCellValue(modelList.get(0).getOpAllList().get(v).getExpense().doubleValue());
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								if (modelList.size() > 1) {
									cell.setCellValue(
											modelList.get(1).getOpAllList().get(v).getExpense().doubleValue());
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
								}
								break;
							}
							v++;
						}
					} else if (opAllList.size() == 0 && dataFormatAppend.contains("門急診")) {
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						if (isLastY) {
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
						}
					}
					if (opList.size() > 0 && dataFormatAppend.contains("門診")) {
						for (OwnExpenseQueryConditionDetail m : opList) {
							int v = 0;
							if (buildList.get(i).getFuncType().equals(m.getFuncType())) {
								cell.setCellValue(modelList.get(0).getOpList().get(v).getQuantity().doubleValue());
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								if (modelList.size() > 1) {
									cell.setCellValue(modelList.get(1).getOpList().get(v).getQuantity().doubleValue());
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
								}
								cell.setCellValue(modelList.get(0).getOpList().get(v).getExpense().doubleValue());
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								if (modelList.size() > 1) {
									cell.setCellValue(modelList.get(1).getOpList().get(v).getExpense().doubleValue());
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
								}
								break;
							}
							v++;
						}
					} else if (opList.size() == 0 && dataFormatAppend.contains("門診")) {
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						if (isLastY) {
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
						}
					}
					if (emList.size() > 0 && dataFormatAppend.contains("急診")) {
						int v = 0;
						for (OwnExpenseQueryConditionDetail m : emList) {
							if (buildList.get(i).getFuncType().equals(m.getFuncType())) {
								cell.setCellValue(modelList.get(0).getEmList().get(v).getQuantity().doubleValue());
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								if (modelList.size() > 1) {
									cell.setCellValue(modelList.get(1).getEmList().get(v).getQuantity().doubleValue());
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
								}
								cell.setCellValue(modelList.get(0).getEmList().get(v).getExpense().doubleValue());
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								if (modelList.size() > 1) {
									cell.setCellValue(modelList.get(1).getEmList().get(v).getExpense().doubleValue());
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
								}
								break;
							}
							v++;
						}
					} else if (emList.size() == 0 && dataFormatAppend.contains("急診")) {
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						if (isLastY) {
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
						}
					}
					if (ipList.size() > 0 && dataFormatAppend.contains("住院")) {
						int v = 0;
						for (OwnExpenseQueryConditionDetail m : ipList) {
							if (buildList.get(i).getFuncType().equals(m.getFuncType())) {
								cell.setCellValue(modelList.get(0).getIpList().get(v).getQuantity().doubleValue());
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								if (modelList.size() > 1) {
									cell.setCellValue(modelList.get(1).getIpList().get(v).getQuantity().doubleValue());
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
								}
								cell.setCellValue(modelList.get(0).getIpList().get(v).getExpense().doubleValue());
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								if (modelList.size() > 1) {
									cell.setCellValue(modelList.get(1).getIpList().get(v).getExpense().doubleValue());
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
								}
								break;
							}
							v++;
						}
					} else if (ipList.size() == 0 && dataFormatAppend.contains("住院")) {
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						if (isLastY) {
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
						}
					}
					rowIndex++;
					cellIndex = 0;

					List<OwnExpenseQueryConditionIhnCodeInfo> infos = buildList.get(i).getIhnCodeInfo();
					if (infos != null && infos.size() > 0) {
						int v = 0;
						for (OwnExpenseQueryConditionIhnCodeInfo info : infos) {
							row = sheet.createRow(rowIndex);
							cell = row.createCell(cellIndex);
							cell.setCellValue(info.getIhnCode());
							cell.setCellStyle(cellTitleStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellStyle(cellTitleStyle);
							sheet.addMergedRegionUnsafe(new CellRangeAddress(rowIndex, rowIndex, 0, 1));
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(info.getQuantity().doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							if (modelList.size() > 1) {
								cell.setCellValue(modelList.get(1).getAllList().get(i).getIhnCodeInfo().get(v)
										.getQuantity().doubleValue());
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
							}
							cell.setCellValue(info.getExpense().doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							if (modelList.size() > 1) {
								cell.setCellValue(modelList.get(1).getAllList().get(i).getIhnCodeInfo().get(v)
										.getExpense().doubleValue());
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
							}
							if (opAllList.size() > 0 && dataFormatAppend.contains("門急診")) {
								cell.setCellValue(info.getQuantity().doubleValue());
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								if (modelList.size() > 1) {
									cell.setCellValue(modelList.get(1).getAllList().get(i).getIhnCodeInfo().get(v)
											.getQuantity().doubleValue());
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
								}
								cell.setCellValue(info.getExpense().doubleValue());
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								if (modelList.size() > 1) {
									cell.setCellValue(modelList.get(1).getAllList().get(i).getIhnCodeInfo().get(v)
											.getExpense().doubleValue());
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
								}
							} else {
								if (dataFormatAppend.contains("門急診")) {

									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
									if (isLastY) {
										cell.setCellStyle(cellFormatStyle);
										cellIndex++;
										cell = row.createCell(cellIndex);
										cell.setCellStyle(cellFormatStyle);
										cellIndex++;
										cell = row.createCell(cellIndex);
									}
								}
							}

							if (opList.size() > 0 && dataFormatAppend.contains("門診")) {
								cell.setCellValue(info.getQuantity().doubleValue());
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								if (modelList.size() > 1) {
									cell.setCellValue(modelList.get(1).getAllList().get(i).getIhnCodeInfo().get(v)
											.getQuantity().doubleValue());
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
								}
								cell.setCellValue(info.getExpense().doubleValue());
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								if (modelList.size() > 1) {
									cell.setCellValue(modelList.get(1).getAllList().get(i).getIhnCodeInfo().get(v)
											.getExpense().doubleValue());
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
								}
							} else {
								if (dataFormatAppend.contains("門診")) {

									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
									if (isLastY) {
										cell.setCellStyle(cellFormatStyle);
										cellIndex++;
										cell = row.createCell(cellIndex);
										cell.setCellStyle(cellFormatStyle);
										cellIndex++;
										cell = row.createCell(cellIndex);
									}
								}
							}

							if (emList.size() > 0 && dataFormatAppend.contains("急診")) {
								cell.setCellValue(info.getQuantity().doubleValue());
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								if (modelList.size() > 1) {
									cell.setCellValue(modelList.get(1).getAllList().get(i).getIhnCodeInfo().get(v)
											.getQuantity().doubleValue());
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
								}
								cell.setCellValue(info.getExpense().doubleValue());
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								if (modelList.size() > 1) {
									cell.setCellValue(modelList.get(1).getAllList().get(i).getIhnCodeInfo().get(v)
											.getExpense().doubleValue());
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
								}
							} else {
								if (dataFormatAppend.contains("急診")) {

									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
									if (isLastY) {
										cell.setCellStyle(cellFormatStyle);
										cellIndex++;
										cell = row.createCell(cellIndex);
										cell.setCellStyle(cellFormatStyle);
										cellIndex++;
										cell = row.createCell(cellIndex);
									}
								}
							}
							if (ipList.size() > 0 && dataFormatAppend.contains("住院")) {
								cell.setCellValue(info.getQuantity().doubleValue());
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								if (modelList.size() > 1) {
									cell.setCellValue(modelList.get(1).getAllList().get(i).getIhnCodeInfo().get(v)
											.getQuantity().doubleValue());
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
								}
								cell.setCellValue(info.getExpense().doubleValue());
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								if (modelList.size() > 1) {
									cell.setCellValue(modelList.get(1).getAllList().get(i).getIhnCodeInfo().get(v)
											.getExpense().doubleValue());
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
								}
							} else {
								if (dataFormatAppend.contains("住院")) {

									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
									if (isLastY) {
										cell.setCellStyle(cellFormatStyle);
										cellIndex++;
										cell = row.createCell(cellIndex);
										cell.setCellStyle(cellFormatStyle);
										cellIndex++;
										cell = row.createCell(cellIndex);
									}
								}
							}

							rowIndex++;
							cellIndex = 0;
							v++;
						}
					}
				}
			}

			/// 如果醫護人員，資料要獨立顯示
			if ((medNames != null && medNames.length() > 0)) {
				List<OwnExpenseQueryConditionDetail> allPList = model.getAllPrsnList();
				List<OwnExpenseQueryConditionDetail> opAllPList = model.getOpAllPrsnList();
				List<OwnExpenseQueryConditionDetail> opPList = model.getOpPrsnList();
				List<OwnExpenseQueryConditionDetail> emPList = model.getEmPrsnList();
				List<OwnExpenseQueryConditionDetail> ipPList = model.getIpPrsnList();
				buildList = new ArrayList<OwnExpenseQueryConditionDetail>();
				if (allPList.size() > 0 && (opAllPList.size() == 0 && opPList.size() == 0 && emPList.size() == 0
						&& ipPList.size() == 0)) {
					buildList.addAll(allPList);
				} else if (opAllPList.size() > 0 && (allPList.size() == 0 && opPList.size() == 0 && emPList.size() == 0
						&& ipPList.size() == 0)) {
					buildList.addAll(opAllPList);
				} else if (opPList.size() > 0 && (opAllPList.size() == 0 && allPList.size() == 0 && emPList.size() == 0
						&& ipPList.size() == 0)) {
					buildList.addAll(opPList);
				} else if (emPList.size() > 0 && (opAllPList.size() == 0 && opPList.size() == 0 && allPList.size() == 0
						&& ipPList.size() == 0)) {
					buildList.addAll(emPList);
				} else if (ipPList.size() > 0 && (opAllPList.size() == 0 && opPList.size() == 0 && emPList.size() == 0
						&& allPList.size() == 0)) {
					buildList.addAll(ipPList);
				}

				if (buildList.size() > 0) {
					rowIndex++;
					cellIndex = 0;
					for (int i = 0; i < buildList.size(); i++) {
						/// 醫護人員名獨立一row
						row = sheet.createRow(rowIndex);
						cell = row.createCell(cellIndex);
						cell.setCellValue(buildList.get(i).getPrsnId());
						cell.setCellStyle(cellTitleStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellStyle(cellTitleStyle);
						sheet.addMergedRegionUnsafe(new CellRangeAddress(rowIndex, rowIndex, 0, 1));
						rowIndex++;
						cellIndex = 0;
						/// 此醫護人員下面資料
						row = sheet.createRow(rowIndex);
						cell = row.createCell(cellIndex);
						if (buildList.get(i).getFuncType().length() < 3) {

							cell.setCellValue(buildList.get(i).getDescChi());
						} else {
							cell.setCellValue(buildList.get(i).getFuncType());
						}
						cell.setCellStyle(cellTitleStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellStyle(cellTitleStyle);
						sheet.addMergedRegionUnsafe(new CellRangeAddress(rowIndex, rowIndex, 0, 1));
						cellIndex++;
						cell = row.createCell(cellIndex);
						if (allList.size() > 0 && dataFormatAppend.contains("不分區")) {
							int v = 0;
							for (OwnExpenseQueryConditionDetail m : allList) {
								if (buildList.get(i).getFuncType().equals(m.getFuncType())) {
									cell.setCellValue(modelList.get(0).getAllList().get(v).getQuantity().doubleValue());
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
									if (modelList.size() > 1) {
										cell.setCellValue(
												modelList.get(1).getAllList().get(v).getQuantity().doubleValue());
										cell.setCellStyle(cellFormatStyle);
										cellIndex++;
										cell = row.createCell(cellIndex);
									}
									cell.setCellValue(modelList.get(0).getAllList().get(v).getExpense().doubleValue());
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
									if (modelList.size() > 1) {
										cell.setCellValue(
												modelList.get(1).getAllList().get(v).getExpense().doubleValue());
										cell.setCellStyle(cellFormatStyle);
										cellIndex++;
										cell = row.createCell(cellIndex);
									}
									break;
								}
								v++;
							}
						} else if (allList.size() == 0 && dataFormatAppend.contains("不分區")) {
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							if (isLastY) {
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
							}

						}
						if (opAllList.size() > 0 && dataFormatAppend.contains("門急診")) {
							int v = 0;
							for (OwnExpenseQueryConditionDetail m : opAllList) {
								if (buildList.get(i).getFuncType().equals(m.getFuncType())) {
									cell.setCellValue(
											modelList.get(0).getOpAllList().get(v).getQuantity().doubleValue());
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
									if (modelList.size() > 1) {
										cell.setCellValue(
												modelList.get(1).getOpAllList().get(v).getQuantity().doubleValue());
										cell.setCellStyle(cellFormatStyle);
										cellIndex++;
										cell = row.createCell(cellIndex);
									}
									cell.setCellValue(
											modelList.get(0).getOpAllList().get(v).getExpense().doubleValue());
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
									if (modelList.size() > 1) {
										cell.setCellValue(
												modelList.get(1).getOpAllList().get(v).getExpense().doubleValue());
										cell.setCellStyle(cellFormatStyle);
										cellIndex++;
										cell = row.createCell(cellIndex);
									}
									break;
								}
								v++;
							}
						} else if (opAllList.size() == 0 && dataFormatAppend.contains("門急診")) {
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							if (isLastY) {
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
							}
						}
						if (opList.size() > 0 && dataFormatAppend.contains("門診")) {
							for (OwnExpenseQueryConditionDetail m : opList) {
								int v = 0;
								if (buildList.get(i).getFuncType().equals(m.getFuncType())) {
									cell.setCellValue(modelList.get(0).getOpList().get(v).getQuantity().doubleValue());
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
									if (modelList.size() > 1) {
										cell.setCellValue(
												modelList.get(1).getOpList().get(v).getQuantity().doubleValue());
										cell.setCellStyle(cellFormatStyle);
										cellIndex++;
										cell = row.createCell(cellIndex);
									}
									cell.setCellValue(modelList.get(0).getOpList().get(v).getExpense().doubleValue());
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
									if (modelList.size() > 1) {
										cell.setCellValue(
												modelList.get(1).getOpList().get(v).getExpense().doubleValue());
										cell.setCellStyle(cellFormatStyle);
										cellIndex++;
										cell = row.createCell(cellIndex);
									}
									break;
								}
								v++;
							}
						} else if (opList.size() == 0 && dataFormatAppend.contains("門診")) {
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							if (isLastY) {
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
							}
						}
						if (emList.size() > 0 && dataFormatAppend.contains("急診")) {
							int v = 0;
							for (OwnExpenseQueryConditionDetail m : emList) {
								if (buildList.get(i).getFuncType().equals(m.getFuncType())) {
									cell.setCellValue(modelList.get(0).getEmList().get(v).getQuantity().doubleValue());
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
									if (modelList.size() > 1) {
										cell.setCellValue(
												modelList.get(1).getEmList().get(v).getQuantity().doubleValue());
										cell.setCellStyle(cellFormatStyle);
										cellIndex++;
										cell = row.createCell(cellIndex);
									}
									cell.setCellValue(modelList.get(0).getEmList().get(v).getExpense().doubleValue());
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
									if (modelList.size() > 1) {
										cell.setCellValue(
												modelList.get(1).getEmList().get(v).getExpense().doubleValue());
										cell.setCellStyle(cellFormatStyle);
										cellIndex++;
										cell = row.createCell(cellIndex);
									}
									break;
								}
								v++;
							}
						} else if (emList.size() == 0 && dataFormatAppend.contains("急診")) {
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							if (isLastY) {
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
							}
						}
						if (ipList.size() > 0 && dataFormatAppend.contains("住院")) {
							int v = 0;
							for (OwnExpenseQueryConditionDetail m : ipList) {
								if (buildList.get(i).getFuncType().equals(m.getFuncType())) {
									cell.setCellValue(modelList.get(0).getIpList().get(v).getQuantity().doubleValue());
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
									if (modelList.size() > 1) {
										cell.setCellValue(
												modelList.get(1).getIpList().get(v).getQuantity().doubleValue());
										cell.setCellStyle(cellFormatStyle);
										cellIndex++;
										cell = row.createCell(cellIndex);
									}
									cell.setCellValue(modelList.get(0).getIpList().get(v).getExpense().doubleValue());
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
									if (modelList.size() > 1) {
										cell.setCellValue(
												modelList.get(1).getIpList().get(v).getExpense().doubleValue());
										cell.setCellStyle(cellFormatStyle);
										cellIndex++;
										cell = row.createCell(cellIndex);
									}
									break;
								}
								v++;
							}
						} else if (ipList.size() == 0 && dataFormatAppend.contains("住院")) {
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							if (isLastY) {
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
							}
						}
						rowIndex++;
						cellIndex = 0;

						List<OwnExpenseQueryConditionIhnCodeInfo> infos = buildList.get(i).getIhnCodeInfo();
						if (infos != null && infos.size() > 0) {
							int v = 0;
							for (OwnExpenseQueryConditionIhnCodeInfo info : infos) {
								row = sheet.createRow(rowIndex);
								cell = row.createCell(cellIndex);
								cell.setCellValue(info.getIhnCode());
								cell.setCellStyle(cellTitleStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellStyle(cellTitleStyle);
								sheet.addMergedRegionUnsafe(new CellRangeAddress(rowIndex, rowIndex, 0, 1));
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellValue(info.getQuantity().doubleValue());
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								if (modelList.size() > 1) {
									cell.setCellValue(modelList.get(1).getAllList().get(i).getIhnCodeInfo().get(v)
											.getQuantity().doubleValue());
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
								}
								cell.setCellValue(info.getExpense().doubleValue());
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								if (modelList.size() > 1) {
									cell.setCellValue(modelList.get(1).getAllList().get(i).getIhnCodeInfo().get(v)
											.getExpense().doubleValue());
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
								}
								if (opAllList.size() > 0 && dataFormatAppend.contains("門急診")) {
									cell.setCellValue(info.getQuantity().doubleValue());
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
									if (modelList.size() > 1) {
										cell.setCellValue(modelList.get(1).getAllList().get(i).getIhnCodeInfo().get(v)
												.getQuantity().doubleValue());
										cell.setCellStyle(cellFormatStyle);
										cellIndex++;
										cell = row.createCell(cellIndex);
									}
									cell.setCellValue(info.getExpense().doubleValue());
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
									if (modelList.size() > 1) {
										cell.setCellValue(modelList.get(1).getAllList().get(i).getIhnCodeInfo().get(v)
												.getExpense().doubleValue());
										cell.setCellStyle(cellFormatStyle);
										cellIndex++;
										cell = row.createCell(cellIndex);
									}
								} else {
									if (dataFormatAppend.contains("門急診")) {

										cell.setCellStyle(cellFormatStyle);
										cellIndex++;
										cell = row.createCell(cellIndex);
										cell.setCellStyle(cellFormatStyle);
										cellIndex++;
										cell = row.createCell(cellIndex);
										if (isLastY) {
											cell.setCellStyle(cellFormatStyle);
											cellIndex++;
											cell = row.createCell(cellIndex);
											cell.setCellStyle(cellFormatStyle);
											cellIndex++;
											cell = row.createCell(cellIndex);
										}
									}
								}

								if (opList.size() > 0 && dataFormatAppend.contains("門診")) {
									cell.setCellValue(info.getQuantity().doubleValue());
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
									if (modelList.size() > 1) {
										cell.setCellValue(modelList.get(1).getAllList().get(i).getIhnCodeInfo().get(v)
												.getQuantity().doubleValue());
										cell.setCellStyle(cellFormatStyle);
										cellIndex++;
										cell = row.createCell(cellIndex);
									}
									cell.setCellValue(info.getExpense().doubleValue());
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
									if (modelList.size() > 1) {
										cell.setCellValue(modelList.get(1).getAllList().get(i).getIhnCodeInfo().get(v)
												.getExpense().doubleValue());
										cell.setCellStyle(cellFormatStyle);
										cellIndex++;
										cell = row.createCell(cellIndex);
									}
								} else {
									if (dataFormatAppend.contains("門診")) {

										cell.setCellStyle(cellFormatStyle);
										cellIndex++;
										cell = row.createCell(cellIndex);
										cell.setCellStyle(cellFormatStyle);
										cellIndex++;
										cell = row.createCell(cellIndex);
										if (isLastY) {
											cell.setCellStyle(cellFormatStyle);
											cellIndex++;
											cell = row.createCell(cellIndex);
											cell.setCellStyle(cellFormatStyle);
											cellIndex++;
											cell = row.createCell(cellIndex);
										}
									}
								}

								if (emList.size() > 0 && dataFormatAppend.contains("急診")) {
									cell.setCellValue(info.getQuantity().doubleValue());
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
									if (modelList.size() > 1) {
										cell.setCellValue(modelList.get(1).getAllList().get(i).getIhnCodeInfo().get(v)
												.getQuantity().doubleValue());
										cell.setCellStyle(cellFormatStyle);
										cellIndex++;
										cell = row.createCell(cellIndex);
									}
									cell.setCellValue(info.getExpense().doubleValue());
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
									if (modelList.size() > 1) {
										cell.setCellValue(modelList.get(1).getAllList().get(i).getIhnCodeInfo().get(v)
												.getExpense().doubleValue());
										cell.setCellStyle(cellFormatStyle);
										cellIndex++;
										cell = row.createCell(cellIndex);
									}
								} else {
									if (dataFormatAppend.contains("急診")) {

										cell.setCellStyle(cellFormatStyle);
										cellIndex++;
										cell = row.createCell(cellIndex);
										cell.setCellStyle(cellFormatStyle);
										cellIndex++;
										cell = row.createCell(cellIndex);
										if (isLastY) {
											cell.setCellStyle(cellFormatStyle);
											cellIndex++;
											cell = row.createCell(cellIndex);
											cell.setCellStyle(cellFormatStyle);
											cellIndex++;
											cell = row.createCell(cellIndex);
										}
									}
								}
								if (ipList.size() > 0 && dataFormatAppend.contains("住院")) {
									cell.setCellValue(info.getQuantity().doubleValue());
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
									if (modelList.size() > 1) {
										cell.setCellValue(modelList.get(1).getAllList().get(i).getIhnCodeInfo().get(v)
												.getQuantity().doubleValue());
										cell.setCellStyle(cellFormatStyle);
										cellIndex++;
										cell = row.createCell(cellIndex);
									}
									cell.setCellValue(info.getExpense().doubleValue());
									cell.setCellStyle(cellFormatStyle);
									cellIndex++;
									cell = row.createCell(cellIndex);
									if (modelList.size() > 1) {
										cell.setCellValue(modelList.get(1).getAllList().get(i).getIhnCodeInfo().get(v)
												.getExpense().doubleValue());
										cell.setCellStyle(cellFormatStyle);
										cellIndex++;
										cell = row.createCell(cellIndex);
									}
								} else {
									if (dataFormatAppend.contains("住院")) {

										cell.setCellStyle(cellFormatStyle);
										cellIndex++;
										cell = row.createCell(cellIndex);
										cell.setCellStyle(cellFormatStyle);
										cellIndex++;
										cell = row.createCell(cellIndex);
										if (isLastY) {
											cell.setCellStyle(cellFormatStyle);
											cellIndex++;
											cell = row.createCell(cellIndex);
											cell.setCellStyle(cellFormatStyle);
											cellIndex++;
											cell = row.createCell(cellIndex);
										}
									}
								}

								rowIndex++;
								cellIndex = 0;
								v++;
							}
						}
					}
				}
			}
			/// 最後設定autosize
			for (int i = 0; i < allList.size() * 2; i++) {
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		String fileNameStr = "自費項目清單";
		String fileName = URLEncoder.encode(fileNameStr, "UTF-8");
		String filepath = (System.getProperty("os.name").toLowerCase().startsWith("windows"))
				? FILE_PATH + "\\" + fileName
				: FILE_PATH + "/" + fileName;
		File file = new File(filepath);
		response.reset();
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "*");
		response.setHeader("Content-Disposition",
				"attachment; filename=" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + ".csv");
		response.setContentType("application/octet-stream;charset=utf8");

		/// 最後由outputstream輸出
		OutputStream out = response.getOutputStream();

		workbook.write(out);
		out.flush();
		out.close();
		workbook.close();

	}

	/**
	 * 取得申報分配佔率與點數、金額-匯出
	 * 
	 * @param nhiStatus
	 * @param payCodeType
	 * @param year
	 * @param month
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
	 * @param isLastM
	 * @param isLastY
	 * @param response
	 * @throws IOException
	 */
	public void getAchievePointQueryConditionExport(String nhiStatus, String payCodeType, String year, String month,
			String dataFormats, String funcTypes, String medNames, String icdcms, String medLogCodes, Long applMin,
			Long applMax, String icdAll, String payCode, String inhCode, boolean isLastM, boolean isLastY,
			HttpServletResponse response) throws IOException {

		Map<String, Object> dataMap = dbrService.getAchievePointQueryCondition(nhiStatus, payCodeType, year, month,
				dataFormats, funcTypes, medNames, icdcms, medLogCodes, applMin, applMax, icdAll, payCode, inhCode,
				isLastM, isLastY);

		@SuppressWarnings("unchecked")
		List<AchievePointQueryCondition> modelList = (List<AchievePointQueryCondition>) dataMap.get("data");

		// 建立新工作簿
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 樣式
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(HorizontalAlignment.LEFT);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellStyle.setBorderBottom(BorderStyle.MEDIUM);
		cellStyle.setBorderTop(BorderStyle.MEDIUM);
		cellStyle.setBorderLeft(BorderStyle.MEDIUM);
		cellStyle.setBorderRight(BorderStyle.MEDIUM);

		HSSFCellStyle cellTitleStyle = workbook.createCellStyle();
		cellTitleStyle.setAlignment(HorizontalAlignment.CENTER);
		cellTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellTitleStyle.setBorderBottom(BorderStyle.MEDIUM);
		cellTitleStyle.setBorderTop(BorderStyle.MEDIUM);
		cellTitleStyle.setBorderLeft(BorderStyle.MEDIUM);
		cellTitleStyle.setBorderRight(BorderStyle.MEDIUM);
		cellTitleStyle.setWrapText(true);

		HSSFCellStyle cellFormatStyle = workbook.createCellStyle();
		cellFormatStyle.setAlignment(HorizontalAlignment.LEFT);
		cellFormatStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellFormatStyle.setBorderBottom(BorderStyle.MEDIUM);
		cellFormatStyle.setBorderTop(BorderStyle.MEDIUM);
		cellFormatStyle.setBorderLeft(BorderStyle.MEDIUM);
		cellFormatStyle.setBorderRight(BorderStyle.MEDIUM);
		cellFormatStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));

		int cellIndex = 0;
		int rowIndex = 0;
		if (modelList.size() > 0) {
			HSSFSheet sheet = workbook.createSheet("申報分配佔率與點數、金額");/// 欄位A1
			HSSFRow row = sheet.createRow(0);
			HSSFCell cell = row.createCell(0);
			cell.setCellValue("統計月份");
			cell.setCellStyle(cellStyle);
			/// 欄位B1
			cell = row.createCell(1);
			cellIndex = 2;
			String searchDate = "";
			for (int x = 0; x < modelList.size(); x++) {
				if (modelList.get(x).getDisplayName().isEmpty()) {
					searchDate = modelList.get(x).getDate();
					cell.setCellValue(modelList.get(x).getDate());
					cell.setCellStyle(cellStyle);
					cell = row.createCell(cellIndex);
					cellIndex++;
				}
			}

			/// 欄位B3
			cell = row.createCell(cellIndex);
			if (isLastM && isLastY) {
				cell.setCellValue("上個月同條件相比");
				cell.setCellStyle(cellStyle);
				cell = row.createCell(3);
				cell.setCellValue("去年同期時段相比");
				cell.setCellStyle(cellStyle);
			} else {
				if (isLastM) {
					cell.setCellValue("上個月同條件相比");
					cell.setCellStyle(cellStyle);
				}
				if (isLastY) {
					cell.setCellValue("去年同期時段相比");
					cell.setCellStyle(cellStyle);
				}
			}

			/// 欄位A2
			row = sheet.createRow(1);
			cell = row.createCell(0);
			cell.setCellValue("狀態");
			cell.setCellStyle(cellStyle);

			cell = row.createCell(1);
			if (nhiStatus.equals("1")) {
				cell.setCellValue("健保(含勞保)");
			} else {
				cell.setCellValue("健保(不含勞保)");
			}
			cell.setCellStyle(cellStyle);

			cell = row.createCell(2);
			cell.setCellValue("分類");
			cell.setCellStyle(cellStyle);
			cellIndex = 3;
			if (payCodeType != null && payCodeType.length() > 0) {
				String[] split = StringUtility.splitBySpace(payCodeType);
				for (String str : split) {
					cell = row.createCell(cellIndex);
					codeTableList = code_TABLEDao.findByCodeAndCat(str, "PAY_CODE_TYPE");
					String name = codeTableList.get(0).getDescChi();
					cell.setCellValue(name);
					cell.setCellStyle(cellStyle);
					cellIndex++;
				}
			}
			cellIndex = 0;
			rowIndex = 2;
			row = sheet.createRow(rowIndex);
			List<String> dataFormatList = new ArrayList<String>();
			if (dataFormats != null && dataFormats.length() > 0) {
				cell = row.createCell(cellIndex);
				cell.setCellValue("就醫類別");
				cell.setCellStyle(cellStyle);
				cellIndex++;

				cell = row.createCell(cellIndex);
				String[] split = StringUtility.splitBySpace(dataFormats);
				String cellValue = "";
				for (String str : split) {
					switch (str) {
					case "all":
						cellValue += "不分區" + "、";
						break;
					case "totalop":
						cellValue += "門急診" + "、";
						break;
					case "op":
						cellValue += "門診" + "、";
						break;
					case "em":
						cellValue += "急診" + "、";
						break;
					case "ip":
						cellValue += "住院" + "、";
						break;
					}
				}

				cellValue = cellValue.substring(0, cellValue.length() - 1);
				split = cellValue.split("、");
				for (String s : split) {
					dataFormatList.add(s);
				}
				cell.setCellValue(cellValue);
				cell.setCellStyle(cellTitleStyle);

				rowIndex++;
			}
			cellIndex = 0;
			/// 欄位A
			row = sheet.createRow(rowIndex);
			if (funcTypes != null && funcTypes.length() > 0) {
				cell = row.createCell(cellIndex);
				cell.setCellValue("科別");
				cell.setCellStyle(cellStyle);
				cellIndex++;

				cell = row.createCell(cellIndex);
				cell.setCellValue(funcTypes);
				cell.setCellStyle(cellTitleStyle);
				cellIndex++;
				if (medNames == null || medNames.length() == 0)
					rowIndex++;
			}

			if (medNames != null && medNames.length() > 0) {
				cell = row.createCell(cellIndex);
				cell.setCellValue("醫護");
				cell.setCellStyle(cellStyle);
				cellIndex++;

				cell = row.createCell(cellIndex);
				cell.setCellValue(medNames);
				cell.setCellStyle(cellTitleStyle);
				cellIndex++;
				if (funcTypes == null || funcTypes.length() == 0)
					rowIndex++;
			}
			if ((funcTypes != null && funcTypes.length() > 0) && (medNames != null && medNames.length() > 0))
				rowIndex++;

			cellIndex = 0;
			/// 欄位A
			row = sheet.createRow(rowIndex);
			if (icdcms != null && icdcms.length() > 0) {
				cell = row.createCell(cellIndex);
				cell.setCellValue("病歷編號");
				cell.setCellStyle(cellStyle);
				cellIndex++;

				cell = row.createCell(cellIndex);
				cell.setCellValue(icdcms);
				cell.setCellStyle(cellStyle);
				cellIndex++;
				if (medLogCodes == null || medLogCodes.length() == 0)
					rowIndex++;
			}
			if (medLogCodes != null && medLogCodes.length() > 0) {
				cell = row.createCell(cellIndex);
				cell.setCellValue("就醫記錄");
				cell.setCellStyle(cellStyle);
				cellIndex++;

				cell = row.createCell(cellIndex);
				cell.setCellValue(medLogCodes);
				cell.setCellStyle(cellStyle);
				cellIndex++;
				if (icdcms == null || icdcms.length() == 0)
					rowIndex++;
			}
			if ((icdcms != null && icdcms.length() > 0) && (medLogCodes != null && medLogCodes.length() > 0))
				rowIndex++;

			cellIndex = 0;
			/// 欄位A
			row = sheet.createRow(rowIndex);
			if (applMax > 0 && applMin > 0) {
				cell = row.createCell(cellIndex);
				cell.setCellValue("單筆申報點數");
				cell.setCellStyle(cellStyle);
				cellIndex++;

				cell = row.createCell(cellIndex);
				cell.setCellValue("最小點數:");
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;

				cell = row.createCell(cellIndex);
				cell.setCellValue(applMin);
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;

				cell = row.createCell(cellIndex);
				cell.setCellValue("最大點數:");
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;

				cell = row.createCell(cellIndex);
				cell.setCellValue(applMax);
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				if (icdAll == null || icdAll.length() == 0)
					rowIndex++;
			}
			if (icdAll != null && icdAll.length() > 0) {
				cell = row.createCell(cellIndex);
				cell.setCellValue("不分區ICD碼");
				cell.setCellStyle(cellStyle);
				cellIndex++;

				cell = row.createCell(cellIndex);
				cell.setCellValue(icdAll);
				cell.setCellStyle(cellStyle);
				cellIndex++;
				if (applMax == 0 && applMin == 0)
					rowIndex++;
			}
			if ((icdAll != null && icdAll.length() > 0) && (applMin > 0 && applMax > 0))
				rowIndex++;

			cellIndex = 0;
			/// 欄位A
			row = sheet.createRow(rowIndex);
			if (payCode != null && payCode.length() > 0) {
				cell = row.createCell(cellIndex);
				cell.setCellValue("支付標準代碼");
				cell.setCellStyle(cellStyle);
				cellIndex++;

				cell = row.createCell(cellIndex);
				cell.setCellValue(payCode);
				cell.setCellStyle(cellStyle);
				cellIndex++;
				if (inhCode == null || inhCode.length() == 0)
					rowIndex++;
			}
			if (inhCode != null && inhCode.length() > 0) {
				cell = row.createCell(cellIndex);
				cell.setCellValue("院內碼");
				cell.setCellStyle(cellStyle);
				cellIndex++;

				cell = row.createCell(cellIndex);
				cell.setCellValue(inhCode);
				cell.setCellStyle(cellStyle);
				cellIndex++;
				if (payCode == null || payCode.length() == 0)
					rowIndex++;
			}
			if ((payCode != null && payCode.length() > 0) && (inhCode != null && inhCode.length() > 0))
				rowIndex++;

			cellIndex = 0;
			rowIndex++;

			row = sheet.createRow(rowIndex);
			cell = row.createCell(cellIndex);
			cell.setCellValue("統計月份: " + searchDate);
			cell.setCellStyle(cellStyle);
			cellIndex++;
			for (String str : dataFormatList) {
				cell = row.createCell(cellIndex);
				cell.setCellValue(str);
				cell.setCellStyle(cellTitleStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellStyle(cellTitleStyle);
				cellIndex++;
			}
			int x1 = 1;
			int x2 = 2;
			/// merge
			for (String str : dataFormatList) {

				sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, x1, x2));
				x1 += 2;
				x2 += 2;
			}

			/// 欄位A
			cellIndex = 0;
			rowIndex++;

			/// 將資料拆分現在、去年、上月
			List<AchievePointQueryCondition> nowList = new ArrayList<AchievePointQueryCondition>();
			List<AchievePointQueryCondition> lastMList = new ArrayList<AchievePointQueryCondition>();
			List<AchievePointQueryCondition> lastYList = new ArrayList<AchievePointQueryCondition>();
			for (int y = 0; y < modelList.size(); y++) {
				if (modelList.get(y).getDisplayName().isEmpty()) {
					nowList.add(modelList.get(y));
				} else if (modelList.get(y).getDisplayName().contains("上個月")) {
					lastMList.add(modelList.get(y));
				} else if (modelList.get(y).getDisplayName().contains("去年")) {
					lastYList.add(modelList.get(y));
				}
			}
			/// 欄位B
			for (int y = 0; y < nowList.size(); y++) {
				row = sheet.createRow(rowIndex);
				cell = row.createCell(cellIndex);
				/// 所輸入查詢日期
				cell.setCellValue("本月申報總點數");
				cell.setCellStyle(cellStyle);
				cellIndex++;
				rowIndex = loopsheet(sheet, cell, row, cellStyle, cellFormatStyle, nowList, dataFormatList, payCode,
						inhCode, cellIndex, rowIndex, x1, x2, y);
				/// 如果上月資料存在
				if (lastMList.size() > 0) {
					rowIndex += 2;
					cellIndex = 0;
					row = sheet.createRow(rowIndex);
					cell = row.createCell(cellIndex);
					cell.setCellValue("上個月同條件申報總點數");
					cell.setCellStyle(cellStyle);
					cellIndex++;
					rowIndex = loopsheet(sheet, cell, row, cellStyle, cellFormatStyle, lastMList, dataFormatList,
							payCode, inhCode, cellIndex, rowIndex, x1, x2, y);
				}
				/// 如果去年資料存在
				if (lastYList.size() > 0) {
					rowIndex += 2;
					cellIndex = 0;
					row = sheet.createRow(rowIndex);
					cell = row.createCell(cellIndex);
					cell.setCellValue("去年同期時段相比申報總點數");
					cell.setCellStyle(cellStyle);
					cellIndex++;
					rowIndex = loopsheet(sheet, cell, row, cellStyle, cellFormatStyle, lastMList, dataFormatList,
							payCode, inhCode, cellIndex, rowIndex, x1, x2, y);
				}
				rowIndex++;
				cellIndex = 0;
			}

			/// 最後設定autosize
			for (int i = 0; i < dataFormatList.size() * 2; i++) {
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
			}

		} else {
			HSSFSheet sheet = workbook.createSheet("工作表");
		}

		String fileNameStr = "申報分配佔率與點數金額報表";
		String fileName = URLEncoder.encode(fileNameStr, "UTF-8");
		String filepath = (System.getProperty("os.name").toLowerCase().startsWith("windows"))
				? FILE_PATH + "\\" + fileName
				: FILE_PATH + "/" + fileName;
		File file = new File(filepath);
		response.reset();
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "*");
		response.setHeader("Content-Disposition",
				"attachment; filename=" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + ".csv");
		response.setContentType("application/octet-stream;charset=utf8");

		/// 最後由outputstream輸出
		OutputStream out = response.getOutputStream();

		workbook.write(out);
		out.flush();
		out.close();
		workbook.close();
	}

	@SuppressWarnings("unused")
	private int loopsheet(HSSFSheet sheet, HSSFCell cell, HSSFRow row, HSSFCellStyle cellStyle,
			HSSFCellStyle cellFormatStyle, List<AchievePointQueryCondition> modelList, List<String> dataFormatList,
			String payCode, String inhCode, int cellIndex, int rowIndex, int x1, int x2, int y) {

		for (String str : dataFormatList) {
			cell = row.createCell(cellIndex);
			switch (str) {
			case "不分區":
				cell.setCellValue(modelList.get(y).getAllDot().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellStyle(cellFormatStyle);
				break;
			case "門急診":
				cell.setCellValue(modelList.get(y).getOpAllDot().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellStyle(cellFormatStyle);
				break;
			case "門診":
				cell.setCellValue(modelList.get(y).getOpDot().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellStyle(cellFormatStyle);
				break;
			case "急診":
				cell.setCellValue(modelList.get(y).getEmDot().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellStyle(cellFormatStyle);
				break;
			case "住院":
				cell.setCellValue(modelList.get(y).getIpDot().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellStyle(cellFormatStyle);
				break;
			}
			cellIndex++;
		}
		x1 = 1;
		x2 = 2;
		/// merge
		for (String str : dataFormatList) {

			sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, x1, x2));
			x1 += 2;
			x2 += 2;
		}
		rowIndex++;
		cellIndex = 0;

		List<AchievePointQueryConditionDetail> plist = modelList.get(y).getPayCodeTypeList() == null
				? new ArrayList<AchievePointQueryConditionDetail>()
				: modelList.get(y).getPayCodeTypeList();
		if (plist.size() > 0) {
			for (AchievePointQueryConditionDetail detail : plist) {
				row = sheet.createRow(rowIndex);
				cell = row.createCell(cellIndex);
				cell.setCellValue(detail.getName() + "/費用占率");
				cell.setCellStyle(cellStyle);
				cellIndex++;
				for (String str : dataFormatList) {
					cell = row.createCell(cellIndex);
					switch (str) {
					case "不分區":
						cell.setCellValue(detail.getAllPayCodeTypeDot().doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(detail.getAllPayCodeTypeDotPercent() + "%");
						cell.setCellStyle(cellFormatStyle);
						break;
					case "門急診":
						cell.setCellValue(detail.getOpAllPayCodeTypeDot().doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(detail.getOpAllPayCodeTypeDotPercent() + "%");
						cell.setCellStyle(cellFormatStyle);
						break;
					case "門診":
						cell.setCellValue(detail.getOpPayCodeTypeDot().doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(detail.getOpPayCodeTypeDotPercent() + "%");
						cell.setCellStyle(cellFormatStyle);
						break;
					case "急診":
						cell.setCellValue(detail.getEmPayCodeTypeDot().doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(detail.getEmPayCodeTypeDotPercent() + "%");
						cell.setCellStyle(cellFormatStyle);
						break;
					case "住院":
						cell.setCellValue(detail.getIpPayCodeTypeDot().doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(detail.getIpPayCodeTypeDotPercent() + "%");
						cell.setCellStyle(cellFormatStyle);
						break;
					}
					cellIndex++;
				}
				rowIndex++;
				cellIndex = 0;

			}
		}
		cellIndex = 0;
		if (payCode != null && payCode.length() > 0) {
			row = sheet.createRow(rowIndex);
			cell = row.createCell(cellIndex);
			cell.setCellValue("支付準則代碼" + payCode + "/費用占率");
			cell.setCellStyle(cellStyle);
			cellIndex++;
			for (String str : dataFormatList) {
				cell = row.createCell(cellIndex);
				switch (str) {
				case "不分區":
					cell.setCellValue(modelList.get(y).getAllPayCodeDot().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(modelList.get(y).getAllPayCodeDotPercent() + "%");
					cell.setCellStyle(cellFormatStyle);
					break;
				case "門急診":
					cell.setCellValue(modelList.get(y).getOpAllPayCodeDot().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(modelList.get(y).getOpAllPayCodeDotPercent() + "%");
					cell.setCellStyle(cellFormatStyle);
					break;
				case "門診":
					cell.setCellValue(modelList.get(y).getOpPayCodeDot().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(modelList.get(y).getOpPayCodeDotPercent() + "%");
					cell.setCellStyle(cellFormatStyle);
					break;
				case "急診":
					cell.setCellValue(modelList.get(y).getEmPayCodeDot().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(modelList.get(y).getEmPayCodeDotPercent() + "%");
					cell.setCellStyle(cellFormatStyle);
					break;
				case "住院":
					cell.setCellValue(modelList.get(y).getIpPayCodeDot().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(modelList.get(y).getIpPayCodeDotPercent() + "%");
					cell.setCellStyle(cellFormatStyle);
					break;
				}
				cellIndex++;
			}
		}
		cellIndex = 0;
		if (inhCode != null && inhCode.length() > 0) {
			row = sheet.createRow(rowIndex);
			cell = row.createCell(cellIndex);
			cell.setCellValue("院內碼" + payCode + "/費用占率");
			cell.setCellStyle(cellStyle);
			cellIndex++;
			for (String str : dataFormatList) {
				cell = row.createCell(cellIndex);
				switch (str) {
				case "不分區":
					cell.setCellValue(modelList.get(y).getAllInhCodeDot().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(modelList.get(y).getAllInhCodeDotPercent() + "%");
					cell.setCellStyle(cellFormatStyle);
					break;
				case "門急診":
					cell.setCellValue(modelList.get(y).getOpAllInhCodeDot().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(modelList.get(y).getOpAllInhCodeDotPercent() + "%");
					cell.setCellStyle(cellFormatStyle);
					break;
				case "門診":
					cell.setCellValue(modelList.get(y).getOpInhCodeDot().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(modelList.get(y).getOpInhCodeDotPercent() + "%");
					cell.setCellStyle(cellFormatStyle);
					break;
				case "急診":
					cell.setCellValue(modelList.get(y).getEmInhCodeDot().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(modelList.get(y).getEmInhCodeDotPercent() + "%");
					cell.setCellStyle(cellFormatStyle);
					break;
				case "住院":
					cell.setCellValue(modelList.get(y).getIpInhCodeDot().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(modelList.get(y).getIpInhCodeDotPercent() + "%");
					cell.setCellStyle(cellFormatStyle);
					break;
				}
				cellIndex++;
			}
		}
		return row.getRowNum();
	}

	/*
	 * 醫令項目與執行量-匯出
	 * 
	 * @param result 醫令項目與執行量統計結果
	 * 
	 * @param feeApply 費用申報狀態，健保 自費
	 * 
	 * @param dateType 時間區間型態
	 * 
	 * @param year 年 dateType=0，需填入
	 * 
	 * @param month 月 dateType=0，需填入
	 * 
	 * @param betweenSDate 起始時間 dateType=1，需填入
	 * 
	 * @param betweenEDate 結束時間 dateType=1，需填入
	 * 
	 * @param dataFormats 就醫類型
	 * 
	 * @param funcTypes 科別
	 * 
	 * @param medNames 醫護名
	 * 
	 * @param icdAll 不分區icd碼
	 * 
	 * @param payCode 支付標準代碼
	 * 
	 * @param inhCode 院內碼
	 * 
	 * @param isLastM 上個月同條件相比
	 * 
	 * @param isLastY 去年同時期同條件相比
	 */
	@SuppressWarnings({ "unchecked" })
	public void getMedicalOrderExport(Map<String, Object> result, String feeApply, String dateType, String year,
			String month, String betweenSdate, String betweenEdate, String dataFormats, String funcTypes,
			String medNames, String icdAll, String payCode, String inhCode, boolean isLastM, boolean isLastY,
			HttpServletResponse response) {

		try {
			String[] years = StringUtility.splitBySpace(year);
			String[] months = StringUtility.splitBySpace(month);
			List<String> yearMonth = new ArrayList<String>();

			for (int i = 0; i < years.length; i++) {
				StringBuffer str = new StringBuffer();
				str.append(years[i]);
				str.append("-");
				if (Integer.valueOf(months[i]) < 10) {
					str.append("0" + months[i]);
				} else {
					str.append(months[i]);
				}

				yearMonth.add(str.toString());
			}

			List<String> feeApplyList = Arrays.asList(StringUtility.splitBySpace(feeApply));

			// 建立新工作簿
			HSSFWorkbook workbook = new HSSFWorkbook();

			HSSFCellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setAlignment(HorizontalAlignment.CENTER);
			cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setBorderTop(BorderStyle.THIN);

			HSSFCellStyle cellStyle_left = workbook.createCellStyle();
			cellStyle_left.setAlignment(HorizontalAlignment.LEFT);
			cellStyle_left.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle_left.setBorderBottom(BorderStyle.THIN);
			cellStyle_left.setBorderLeft(BorderStyle.THIN);
			cellStyle_left.setBorderRight(BorderStyle.THIN);
			cellStyle_left.setBorderTop(BorderStyle.THIN);

			/*
			 * 新建工作表 醫令項目與執行量
			 */
			HSSFSheet medicalOrderSheet = workbook.createSheet("醫令項目與執行量");

			HSSFRow row0 = medicalOrderSheet.createRow(0);
			HSSFRow row1 = medicalOrderSheet.createRow(1);
			HSSFRow row2 = medicalOrderSheet.createRow(2);
			HSSFRow row3 = medicalOrderSheet.createRow(3);
			HSSFRow row4 = medicalOrderSheet.createRow(4);

			StringBuffer ym = new StringBuffer();

			// 統計月份title
			medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(0, 0, 0, 1));
			medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(0, 0, 2, 9));
			addRowCell(row0, 0, "統計月份", cellStyle_left);

			if (dateType.equals("0")) {

				if (years.length > 1) {
					isLastM = false;
				}

				for (int i = 0; i < years.length; i++) {
					ym.append(years[i]);
					ym.append("-");
					ym.append(months[i]);

					if (i < years.length - 1) {
						ym.append(" ");
					}
				}
				addRowCell(row0, 2, ym.toString(), cellStyle_left);

				for (int i = 3; i < 10; i++) {
					addRowCell(row0, i, "", cellStyle_left);
				}
			} else if (dateType.equals("1")) {

				isLastM = false;

				ym.append(betweenSdate);
				ym.append("~");
				ym.append(betweenEdate);
				addRowCell(row0, 2, ym.toString(), cellStyle_left);

				for (int i = 3; i < 10; i++) {
					addRowCell(row0, i, "", cellStyle_left);
				}
			}

			// 費用申報狀態title
			medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(1, 1, 0, 1));
			medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(1, 1, 2, 4));
			addRowCell(row1, 0, "費用申報狀態", cellStyle_left);
			addRowCell(row1, 1, "", cellStyle_left);
			addRowCell(row1, 2, feeApply, cellStyle_left);
			addRowCell(row1, 3, "", cellStyle_left);
			addRowCell(row1, 4, "", cellStyle_left);

			// 就醫類別title
			String[] dataFormatArr = StringUtility.splitBySpace(dataFormats);
			for (int i = 0; i < dataFormatArr.length; i++) {
				switch (dataFormatArr[i]) {
				case "all":
					dataFormatArr[i] = "不分區";
					break;
				case "totalop":
					dataFormatArr[i] = "門急診";
					break;
				case "op":
					dataFormatArr[i] = "門診";
					break;
				case "em":
					dataFormatArr[i] = "急診";
					break;
				case "ip":
					dataFormatArr[i] = "住院";
					break;
				default:
					break;
				}
			}
			medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(1, 1, 5, 6));
			medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(1, 1, 7, 9));
			addRowCell(row1, 5, "就醫類別", cellStyle_left);
			addRowCell(row1, 6, "", cellStyle_left);
			addRowCell(row1, 7, Arrays.toString(dataFormatArr).replaceAll("\\[", "").replaceAll("\\]", ""),
					cellStyle_left);
			addRowCell(row1, 8, "", cellStyle_left);
			addRowCell(row1, 9, "", cellStyle_left);

			// 科名title
			List<CODE_TABLE> codeTableList = (List<CODE_TABLE>) result.get("codeTableList");
			String[] codes = StringUtility.splitBySpace(funcTypes);
			for (int i = 0; i < codes.length; i++) {
				String code1 = codes[i];

				for (int j = 0; j < codeTableList.size(); j++) {
					String code2 = codeTableList.get(j).getCode();
					String desc_chi = codeTableList.get(j).getDescChi();

					if (code1.equals(code2)) {
						codes[i] = desc_chi;
					}
				}
			}
			medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(2, 2, 0, 1));
			medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(2, 2, 2, 4));
			addRowCell(row2, 0, "科名", cellStyle_left);
			addRowCell(row2, 1, "", cellStyle_left);
			addRowCell(row2, 2, Arrays.toString(codes).replaceAll("\\[", "").replaceAll("\\]", ""), cellStyle_left);
			addRowCell(row2, 3, "", cellStyle_left);
			addRowCell(row2, 4, "", cellStyle_left);

			// 醫護名title
			medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(2, 2, 5, 6));
			medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(2, 2, 7, 9));
			addRowCell(row2, 5, "醫護名", cellStyle_left);
			addRowCell(row2, 6, "", cellStyle_left);
			addRowCell(row2, 7, medNames, cellStyle_left);
			addRowCell(row2, 8, "", cellStyle_left);
			addRowCell(row2, 9, "", cellStyle_left);

			// 不分區ICD碼title
			medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(3, 3, 0, 1));
			medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(3, 3, 2, 4));
			addRowCell(row3, 0, "不分區ICD碼", cellStyle_left);
			addRowCell(row3, 1, "", cellStyle_left);
			addRowCell(row3, 2, icdAll, cellStyle_left);
			addRowCell(row3, 3, "", cellStyle_left);
			addRowCell(row3, 4, "", cellStyle_left);

			// 支付標準代碼title
			medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(4, 4, 0, 1));
			medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(4, 4, 2, 4));
			addRowCell(row4, 0, "支付標準代碼", cellStyle_left);
			addRowCell(row4, 1, "", cellStyle_left);
			addRowCell(row4, 2, payCode, cellStyle_left);
			addRowCell(row4, 3, "", cellStyle_left);
			addRowCell(row4, 4, "", cellStyle_left);

			// 院內碼title
			medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(4, 4, 5, 6));
			medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(4, 4, 7, 9));
			addRowCell(row4, 5, "院內碼", cellStyle_left);
			addRowCell(row4, 6, "", cellStyle_left);
			addRowCell(row4, 7, inhCode, cellStyle_left);
			addRowCell(row4, 8, "", cellStyle_left);
			addRowCell(row4, 9, "", cellStyle_left);

			// 依照起訖時間的案件數、申報點數、自費金額
			List<Map<String, Object>> dataList = (List<Map<String, Object>>) result.get("dataList");
			// 前一年同時段同條件的案件數、申報點數、自費金額
			List<Map<String, Object>> dataList_lastY = new ArrayList<Map<String, Object>>();
			// 前一個月同條件的案件數、申報點數、自費金額
			List<Map<String, Object>> dataList_lastM = new ArrayList<Map<String, Object>>();

			// 依照起訖時間的各科別的案件數、申報點數、自費金額
			List<Map<String, Object>> classDataList = (List<Map<String, Object>>) result.get("classDataList");
			// 前一年同時段同條件的各科別的案件數、申報點數、自費金額
			List<Map<String, Object>> classDataList_lastY = new ArrayList<Map<String, Object>>();
			// 前一個月同條件的各科別的案件數、申報點數、自費金額
			List<Map<String, Object>> classDataList_lastM = new ArrayList<Map<String, Object>>();

			// 依照起訖時間的醫護人員的案件數、申報點數、自費金額
			List<Map<String, Object>> medDataList = (List<Map<String, Object>>) result.get("medDataList");
			// 前一年同時段同條件的醫護人員的案件數、申報點數、自費金額
			List<Map<String, Object>> medDataList_lastY = new ArrayList<Map<String, Object>>();
			// 前一個月同條件的醫護人員的案件數、申報點數、自費金額
			List<Map<String, Object>> medDataList_lastM = new ArrayList<Map<String, Object>>();

			List<Map<String, Object>> lastDate = (List<Map<String, Object>>) result.get("lastDate");

			if (dateType.equals("0")) {

				List<String> lastDateStr = new ArrayList<String>();

				// 找出去年同時段或上個月同條件資料
				for (int i = 0; i < lastDate.size(); i++) {
					StringBuffer YM = new StringBuffer(lastDate.get(i).get("YM").toString());
					StringBuffer value = new StringBuffer(lastDate.get(i).get("Value").toString());

					lastDateStr.add(YM.toString());

					for (int j = 0; j < dataList.size(); j++) {
						StringBuffer date = new StringBuffer(dataList.get(j).get("date").toString());
						StringBuffer dataFormat = new StringBuffer(dataList.get(j).get("dataFormat").toString());
						List<Map<String, Object>> data = (List<Map<String, Object>>) dataList.get(j).get("data");

						if (YM.toString().equals(date.toString().replaceAll("\\-", ""))) {
							Map<String, Object> dataMap = new HashedMap<String, Object>();

							// 去年同時段同條件
							if (value.toString().equals("Y")) {
								dataMap.put("date", date.toString());
								dataMap.put("data", data);
								dataMap.put("dataFormat", dataFormat.toString());
								dataList_lastY.add(dataMap);
							}
							// 上個月同條件
							else {
								dataMap.put("date", date.toString());
								dataMap.put("data", data);
								dataMap.put("dataFormat", dataFormat.toString());
								dataList_lastM.add(dataMap);
							}
						}
					}

					for (int j = 0; j < classDataList.size(); j++) {
						StringBuffer date = new StringBuffer(classDataList.get(j).get("date").toString());
						StringBuffer dataFormat = new StringBuffer(classDataList.get(j).get("dataFormat").toString());
						List<Map<String, Object>> classData = (List<Map<String, Object>>) classDataList.get(j)
								.get("classData");

						if (YM.toString().equals(date.toString().replaceAll("\\-", ""))) {
							Map<String, Object> dataMap = new HashedMap<String, Object>();

							// 去年同時段同條件
							if (value.toString().equals("Y")) {
								dataMap.put("date", date.toString());
								dataMap.put("classData", classData);
								dataMap.put("dataFormat", dataFormat.toString());
								classDataList_lastY.add(dataMap);
							}
							// 上個月同條件
							else {
								dataMap.put("date", date.toString());
								dataMap.put("classData", classData);
								dataMap.put("dataFormat", dataFormat.toString());
								classDataList_lastM.add(dataMap);
							}
						}
					}

					for (int j = 0; j < medDataList.size(); j++) {
						StringBuffer date = new StringBuffer(medDataList.get(j).get("date").toString());
						StringBuffer dataFormat = new StringBuffer(medDataList.get(j).get("dataFormat").toString());
						StringBuffer medName = new StringBuffer(medDataList.get(j).get("medName").toString());
						List<Map<String, Object>> data = (List<Map<String, Object>>) medDataList.get(j).get("data");

						if (YM.toString().equals(date.toString().replaceAll("\\-", ""))) {
							Map<String, Object> dataMap = new HashedMap<String, Object>();

							// 去年同時段同條件
							if (value.toString().equals("Y")) {
								dataMap.put("date", date.toString());
								dataMap.put("data", data);
								dataMap.put("medName", medName.toString());
								dataMap.put("dataFormat", dataFormat.toString());
								medDataList_lastY.add(dataMap);
							}
							// 上個月同條件
							else {
								dataMap.put("date", date.toString());
								dataMap.put("data", data);
								dataMap.put("medName", medName.toString());
								dataMap.put("dataFormat", dataFormat.toString());
								medDataList_lastM.add(dataMap);
							}
						}
					}
				}

				medicalOrderTemplate_dateType0(medicalOrderSheet, dataList, dataList_lastY, dataList_lastM,
						classDataList, classDataList_lastY, classDataList_lastM, medDataList, medDataList_lastY,
						medDataList_lastM, lastDateStr, yearMonth, feeApplyList, Arrays.asList(codes), isLastM, isLastY,
						cellStyle, cellStyle_left);
			} else if (dateType.equals("1")) {

				List<String> lastSDateStr = new ArrayList<String>();
				List<String> lastEDateStr = new ArrayList<String>();

				// 找出去年同時段資料
				if (lastDate.size() > 0) {

					for (int i = 0; i < lastDate.size(); i++) {
						StringBuffer sDate = new StringBuffer(lastDate.get(i).get("sDate").toString());
						StringBuffer eDate = new StringBuffer(lastDate.get(i).get("eDate").toString());

						lastSDateStr.add(sDate.toString());
						lastEDateStr.add(eDate.toString());

						for (int j = 0; j < dataList.size(); j++) {
							StringBuffer sDate2 = new StringBuffer(dataList.get(j).get("startDate").toString());
							StringBuffer eDate2 = new StringBuffer(dataList.get(j).get("endDate").toString());
							StringBuffer dataFormat = new StringBuffer(dataList.get(j).get("dataFormat").toString());
							List<Map<String, Object>> data = (List<Map<String, Object>>) dataList.get(j).get("data");

							if (sDate.toString().equals(sDate2.toString())
									&& eDate.toString().equals(eDate2.toString())) {
								Map<String, Object> dataMap = new HashedMap<String, Object>();

								// 去年同時段同條件
								dataMap.put("startDate", sDate2.toString());
								dataMap.put("endDate", eDate2.toString());
								dataMap.put("data", data);
								dataMap.put("dataFormat", dataFormat.toString());
								dataList_lastY.add(dataMap);
							}
						}

						for (int j = 0; j < classDataList.size(); j++) {
							StringBuffer sDate2 = new StringBuffer(classDataList.get(j).get("startDate").toString());
							StringBuffer eDate2 = new StringBuffer(classDataList.get(j).get("endDate").toString());
							StringBuffer dataFormat = new StringBuffer(
									classDataList.get(j).get("dataFormat").toString());
							List<Map<String, Object>> classData = (List<Map<String, Object>>) classDataList.get(j)
									.get("classData");

							if (sDate.toString().equals(sDate2.toString())
									&& eDate.toString().equals(eDate2.toString())) {
								Map<String, Object> dataMap = new HashedMap<String, Object>();

								// 去年同時段同條件
								dataMap.put("startDate", sDate2.toString());
								dataMap.put("endDate", eDate2.toString());
								dataMap.put("classData", classData);
								dataMap.put("dataFormat", dataFormat.toString());
								classDataList_lastY.add(dataMap);
							}
						}

						for (int j = 0; j < medDataList.size(); j++) {
							StringBuffer sDate2 = new StringBuffer(medDataList.get(j).get("startDate").toString());
							StringBuffer eDate2 = new StringBuffer(medDataList.get(j).get("endDate").toString());
							StringBuffer dataFormat = new StringBuffer(medDataList.get(j).get("dataFormat").toString());
							StringBuffer medName = new StringBuffer(medDataList.get(j).get("medName").toString());
							List<Map<String, Object>> data = (List<Map<String, Object>>) medDataList.get(j).get("data");

							if (sDate.toString().equals(sDate2.toString())
									&& eDate.toString().equals(eDate2.toString())) {
								Map<String, Object> dataMap = new HashedMap<String, Object>();

								// 去年同時段同條件
								dataMap.put("startDate", sDate2.toString());
								dataMap.put("endDate", eDate2.toString());
								dataMap.put("data", data);
								dataMap.put("medName", medName.toString());
								dataMap.put("dataFormat", dataFormat.toString());
								medDataList_lastY.add(dataMap);
							}
						}
					}
				}

				medicalOrderTemplate_dateType1(medicalOrderSheet, dataList, dataList_lastY, classDataList,
						classDataList_lastY, medDataList, medDataList_lastY, lastSDateStr, lastEDateStr, feeApplyList,
						Arrays.asList(codes), isLastY, cellStyle, cellStyle_left);
			}

			// 產生報表
			ym = new StringBuffer("");
			ym.append("醫令項目與執行量_");
			if (dateType.equals("0")) {
				for (int i = 0; i < years.length; i++) {
					ym.append(years[i]);
					ym.append("年");
					ym.append(months[i]);
					ym.append("月");

					if (i < years.length - 1) {
						ym.append("_");
					}
				}
			} else if (dateType.equals("1")) {
				ym.append(betweenSdate);
				ym.append("至");
				ym.append(betweenEdate);
			}
			String fileName = URLEncoder.encode(ym.toString(), "UTF-8");
			String filepath = (System.getProperty("os.name").toLowerCase().startsWith("windows"))
					? FILE_PATH + "\\" + fileName
					: FILE_PATH + "/" + fileName;
			File file = new File(filepath);
			response.reset();
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.setHeader("Access-Control-Allow-Methods", "*");
			response.setHeader("Content-Disposition",
					"attachment; filename=" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + ".csv");
			response.setContentType("application/octet-stream;charset=utf8");

			workbook.write(response.getOutputStream());
			workbook.close();
		} catch (Exception e) {
			// TODO: handle exception
//			e.printStackTrace();
			System.err.println("醫令項目與執行量-匯出error: " + e);
		}
	}

	/**
	 * 核刪資料匯出
	 * 
	 * @param year
	 * @param month
	 * @param dataFormats
	 * @param funcTypes
	 * @param medNames
	 * @param icdAll
	 * @param payCode
	 * @param inhCode
	 * @param isLastM
	 * @param isLastY
	 * @throws IOException
	 */
	public void getDeductedNoteQueryConditionExport(String year, String month, String dataFormats, String funcTypes,
			String medNames, String icdAll, String payCode, String inhCode, boolean isLastM, boolean isLastY,
			HttpServletResponse response) throws IOException {
		DeductedNoteQueryConditionResponse res = dbrService.getDeductedNoteQueryCondition(year, month, dataFormats,
				funcTypes, medNames, icdAll, payCode, inhCode, isLastM, isLastY);
		List<DeductedNoteQueryCondition> modelList = res.getData();

		// 建立新工作簿
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 樣式
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(HorizontalAlignment.LEFT);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellStyle.setBorderBottom(BorderStyle.MEDIUM);
		cellStyle.setBorderTop(BorderStyle.MEDIUM);
		cellStyle.setBorderLeft(BorderStyle.MEDIUM);
		cellStyle.setBorderRight(BorderStyle.MEDIUM);

		HSSFCellStyle cellTitleStyle = workbook.createCellStyle();
		cellTitleStyle.setAlignment(HorizontalAlignment.CENTER);
		cellTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellTitleStyle.setBorderBottom(BorderStyle.MEDIUM);
		cellTitleStyle.setBorderTop(BorderStyle.MEDIUM);
		cellTitleStyle.setBorderLeft(BorderStyle.MEDIUM);
		cellTitleStyle.setBorderRight(BorderStyle.MEDIUM);
		cellTitleStyle.setWrapText(true);

		HSSFCellStyle cellFormatStyle = workbook.createCellStyle();
		cellFormatStyle.setAlignment(HorizontalAlignment.LEFT);
		cellFormatStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellFormatStyle.setBorderBottom(BorderStyle.MEDIUM);
		cellFormatStyle.setBorderTop(BorderStyle.MEDIUM);
		cellFormatStyle.setBorderLeft(BorderStyle.MEDIUM);
		cellFormatStyle.setBorderRight(BorderStyle.MEDIUM);
		cellFormatStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));

		int cellIndex = 0;
		int rowIndex = 0;
		if (modelList.size() > 0) {
			String[] tableRowHeaders = { "統計月份", "抽審件數", "核刪件數總點數", "門急診核刪件數", "門急診核刪總點數", "住院診核刪件數", "住院核刪總點數",
					"放大回推金額(月)" };
			String[] tableRowHeaders2 = { "就醫類別", "科別", "醫護名", "就醫記錄編號", "核刪代碼", "核刪支付代碼", "核刪數量", "核刪點數", "放大回推金額(月)",
					"申復不補附理由代碼", "申復補付數量", "申復補付金額" };
			String[] tableRowHeaders3 = { "門急診", "核刪代碼", "數量", "相關核刪總點數" };
			String[] tableRowHeaders4 = { "住院", "核刪代碼", "數量", "相關核刪總點數" };
			/// 欄位A1
			HSSFSheet sheet = workbook.createSheet("核刪件數資訊");
			HSSFRow row = sheet.createRow(0);
			HSSFCell cell = row.createCell(0);
			cell.setCellValue("統計月份");
			cell.setCellStyle(cellStyle);
			/// 欄位B1
			cell = row.createCell(1);
			cellIndex = 2;
			String searchDate = "";

			for (int x = 0; x < modelList.size(); x++) {
				if (modelList.get(x).getDisplayName().isEmpty()) {
					searchDate = modelList.get(x).getDate();
					cell.setCellValue(modelList.get(x).getDate());
					cell.setCellStyle(cellStyle);
					cell = row.createCell(cellIndex);
					cellIndex++;
				}
			}

			/// 欄位B3
			cellIndex--;
			cell = row.createCell(cellIndex);
			if (isLastM && isLastY) {
				cell.setCellValue("上個月同條件相比");
				cell.setCellStyle(cellStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue("去年同期時段相比");
				cell.setCellStyle(cellStyle);
			} else {
				if (isLastM) {
					cell.setCellValue("上個月同條件相比");
					cell.setCellStyle(cellStyle);
				}
				if (isLastY) {
					cell.setCellValue("去年同期時段相比");
					cell.setCellStyle(cellStyle);
				}
			}
			/// 欄位A2
			rowIndex = 1;
			cellIndex = 0;
			String cellValue = "";
			row = sheet.createRow(rowIndex);
			if (dataFormats != null && dataFormats.length() > 0) {
				cell = row.createCell(cellIndex);
				cell.setCellValue("就醫類別");
				cell.setCellStyle(cellStyle);
				cellIndex++;

				cell = row.createCell(cellIndex);
				String[] split = StringUtility.splitBySpace(dataFormats);
				cellValue = "";
				for (String str : split) {
					switch (str) {
					case "all":
						cellValue += "不分區" + "、";
						break;
					case "totalop":
						cellValue += "門急診" + "、";
						break;
					case "op":
						cellValue += "門診" + "、";
						break;
					case "em":
						cellValue += "急診" + "、";
						break;
					case "ip":
						cellValue += "住院" + "、";
						break;
					}
				}
				cellValue = cellValue.substring(0, cellValue.length() - 1);
				cell.setCellValue(cellValue);
				cell.setCellStyle(cellTitleStyle);
				rowIndex++;
			}
			if (isLastM || isLastY) {
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue("相比條件");
				cell.setCellStyle(cellStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);

				if (isLastM) {
					cell.setCellValue("上個月同條件相比");
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
				}
				if (isLastY) {
					cell.setCellValue("去年同期時段相比");
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
				}
			}

			cellIndex = 0;
			/// 欄位A3
			row = sheet.createRow(rowIndex);
			if (funcTypes != null && funcTypes.length() > 0) {
				cell = row.createCell(cellIndex);
				cell.setCellValue("科別");
				cell.setCellStyle(cellStyle);
				cellIndex++;

				cell = row.createCell(cellIndex);
				cell.setCellValue(funcTypes);
				cell.setCellStyle(cellTitleStyle);
				cellIndex++;
				if (medNames == null || medNames.length() == 0)
					rowIndex++;
			}

			if (medNames != null && medNames.length() > 0) {
				cell = row.createCell(cellIndex);
				cell.setCellValue("醫護");
				cell.setCellStyle(cellStyle);
				cellIndex++;

				cell = row.createCell(cellIndex);
				cell.setCellValue(medNames);
				cell.setCellStyle(cellTitleStyle);
				cellIndex++;
				if (funcTypes == null || funcTypes.length() == 0)
					rowIndex++;
			}
			if ((funcTypes != null && funcTypes.length() > 0) && (medNames != null && medNames.length() > 0))
				rowIndex++;

			cellIndex = 0;

			/// 欄位A4
			row = sheet.createRow(rowIndex);
			if (icdAll != null && icdAll.length() > 0) {
				cell = row.createCell(cellIndex);
				cell.setCellValue("不分區ICD碼");
				cell.setCellStyle(cellStyle);
				cellIndex++;

				cell = row.createCell(cellIndex);
				cell.setCellValue(icdAll);
				cell.setCellStyle(cellStyle);
				cellIndex++;

				rowIndex++;
			}

			cellIndex = 0;
			/// 欄位A5
			row = sheet.createRow(rowIndex);
			if (payCode != null && payCode.length() > 0) {
				cell = row.createCell(cellIndex);
				cell.setCellValue("支付標準代碼");
				cell.setCellStyle(cellStyle);
				cellIndex++;

				cell = row.createCell(cellIndex);
				cell.setCellValue(payCode);
				cell.setCellStyle(cellStyle);
				cellIndex++;
				if (inhCode == null || inhCode.length() == 0)
					rowIndex++;
			}
			if (inhCode != null && inhCode.length() > 0) {
				cell = row.createCell(cellIndex);
				cell.setCellValue("院內碼");
				cell.setCellStyle(cellStyle);
				cellIndex++;

				cell = row.createCell(cellIndex);
				cell.setCellValue(inhCode);
				cell.setCellStyle(cellStyle);
				cellIndex++;
				if (payCode == null || payCode.length() == 0)
					rowIndex++;
			}
			if ((payCode != null && payCode.length() > 0) && (inhCode != null && inhCode.length() > 0))
				rowIndex++;

			cellIndex = 0;
			rowIndex++;

			row = sheet.createRow(rowIndex);
			cell = row.createCell(cellIndex);
			cell.setCellValue("核刪件數資訊");
			cell.setCellStyle(cellStyle);

			rowIndex++;
			row = sheet.createRow(rowIndex);
			cellIndex = 0;
			for (int x = 0; x < tableRowHeaders.length; x++) {
				cell = row.createCell(cellIndex);
				cell.setCellValue(tableRowHeaders[x]);
				cell.setCellStyle(cellStyle);
				cellIndex++;

			}
			rowIndex++;
			cellIndex = 0;

			/// 欄位A，必顯示欄位
			for (int v = 0; v < modelList.size(); v++) {
				row = sheet.createRow(rowIndex);
				cell = row.createCell(cellIndex);
				if (modelList.get(v).getDisplayName().isEmpty()) {
					cell.setCellValue(modelList.get(v).getDate());
					cell.setCellStyle(cellStyle);
					List<DeductedNoteQueryConditionInfo> infos = modelList.get(v).getDeductedNoteInfo();
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(infos.get(0).getExtractCase().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(infos.get(0).getDeductedAmountAll().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(infos.get(0).getDeductedQuantityOp().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(infos.get(0).getDeductedAmountOp().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(infos.get(0).getDeductedQuantityIp().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(infos.get(0).getDeductedAmountIp().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(infos.get(0).getRollbackMAll().doubleValue());
					cell.setCellStyle(cellFormatStyle);

					rowIndex++;
					cellIndex = 0;
				}
			}
			rowIndex++;
			cellIndex = 0;
			/// 欄位A，isLastM or isLastY才顯示
			if (isLastM || isLastY) {
				if (isLastY) {
					row = sheet.createRow(rowIndex);
					cell = row.createCell(cellIndex);
					cell.setCellValue("去年同時段相比");
					cell.setCellStyle(cellStyle);
					rowIndex++;
					row = sheet.createRow(rowIndex);
					for (int x = 0; x < tableRowHeaders.length; x++) {
						cell = row.createCell(cellIndex);
						cell.setCellValue(tableRowHeaders[x]);
						cell.setCellStyle(cellStyle);
						cellIndex++;
					}
					rowIndex++;
					cellIndex = 0;
					for (int v = 0; v < modelList.size(); v++) {
						row = sheet.createRow(rowIndex);
						cell = row.createCell(cellIndex);
						if (modelList.get(v).getDisplayName().contains("去年")) {
							cell.setCellValue(modelList.get(v).getDate());
							cell.setCellStyle(cellStyle);
							List<DeductedNoteQueryConditionInfo> infos = modelList.get(v).getDeductedNoteInfo();
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(infos.get(0).getExtractCase().doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(infos.get(0).getDeductedAmountAll().doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(infos.get(0).getDeductedQuantityOp().doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(infos.get(0).getDeductedAmountOp().doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(infos.get(0).getDeductedQuantityIp().doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(infos.get(0).getDeductedAmountIp().doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(infos.get(0).getRollbackMAll().doubleValue());
							cell.setCellStyle(cellFormatStyle);

							rowIndex++;
							cellIndex = 0;
						}
					}
					cellIndex = 0;
					rowIndex++;
				}
				if (isLastM) {
					row = sheet.createRow(rowIndex);
					cell = row.createCell(cellIndex);
					cell.setCellValue("上個月同條件相比");
					cell.setCellStyle(cellStyle);
					rowIndex++;
					row = sheet.createRow(rowIndex);
					for (int x = 0; x < tableRowHeaders.length; x++) {
						cell = row.createCell(cellIndex);
						cell.setCellValue(tableRowHeaders[x]);
						cell.setCellStyle(cellStyle);
						cellIndex++;
					}
					rowIndex++;
					cellIndex = 0;
					for (int v = 0; v < modelList.size(); v++) {
						row = sheet.createRow(rowIndex);
						cell = row.createCell(cellIndex);
						if (modelList.get(v).getDisplayName().contains("上個月")) {
							cell.setCellValue(modelList.get(v).getDate());
							cell.setCellStyle(cellStyle);
							List<DeductedNoteQueryConditionInfo> infos = modelList.get(v).getDeductedNoteInfo();
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(infos.get(0).getExtractCase().doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(infos.get(0).getDeductedAmountAll().doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(infos.get(0).getDeductedQuantityOp().doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(infos.get(0).getDeductedAmountOp().doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(infos.get(0).getDeductedQuantityIp().doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(infos.get(0).getDeductedAmountIp().doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(infos.get(0).getRollbackMAll().doubleValue());
							cell.setCellStyle(cellFormatStyle);

							rowIndex++;
							cellIndex = 0;
						}
					}
					cellIndex = 0;
					rowIndex++;
				}
			}
			cellIndex = 0;
			rowIndex++;
			/// 欄位A
			row = sheet.createRow(rowIndex);
			cell = row.createCell(0);
			cell.setCellValue("核刪項目清單");
			cell.setCellStyle(cellStyle);
			rowIndex++;
			cellIndex = 0;
			/// 欄位A
			row = sheet.createRow(rowIndex);
			for (int x = 0; x < tableRowHeaders2.length; x++) {
				cell = row.createCell(cellIndex);
				cell.setCellValue(tableRowHeaders2[x]);
				cell.setCellStyle(cellStyle);
				cellIndex++;
			}
			rowIndex++;
			cellIndex = 0;
			/// 資料處理
			List<DeductedNoteQueryConditionList> oplists = new ArrayList<DeductedNoteQueryConditionList>();
			List<DeductedNoteQueryConditionList> iplists = new ArrayList<DeductedNoteQueryConditionList>();
			for (int i = 0; i < modelList.size(); i++) {
				List<DeductedNoteQueryConditionList> lists = modelList.get(i).getDeductedList();
				if (lists != null && lists.size() > 0) {
					for (int v = 0; v < lists.size(); v++) {
						if (lists.get(v).getDataFormat().equals("10")) {
							oplists.add(lists.get(v));
						} else {
							iplists.add(lists.get(v));
						}
					}
				}
			}
			/// 欄位A
			if (oplists.size() > 0) {
				Collections.sort(oplists, mapComparatorDeductedFT);
				for (int i = 0; i < oplists.size(); i++) {
					row = sheet.createRow(rowIndex);
					cell = row.createCell(cellIndex);
					cell.setCellValue("門急診");
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(oplists.get(i).getFuncTypeName());
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(oplists.get(i).getPrsnId());
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(oplists.get(i).getInhClinicId());
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(oplists.get(i).getCode());
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(oplists.get(i).getDeductedOrder());
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(oplists.get(i).getDeductedQuantity().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(oplists.get(i).getDeductedAmount().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(oplists.get(i).getRollbackM().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(oplists.get(i).getAfrNoPayCode());
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(oplists.get(i).getAfrPayQuantity().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(oplists.get(i).getAfrPayAmount().doubleValue());
					cell.setCellStyle(cellFormatStyle);

					cellIndex = 0;
					rowIndex++;
				}
			}
			if (iplists.size() > 0) {
				Collections.sort(iplists, mapComparatorDeductedFT);
				for (int i = 0; i < iplists.size(); i++) {
					row = sheet.createRow(rowIndex);
					cell = row.createCell(cellIndex);
					cell.setCellValue("住院");
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(iplists.get(i).getFuncTypeName());
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(iplists.get(i).getPrsnId());
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(iplists.get(i).getInhClinicId());
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(iplists.get(i).getCode());
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(iplists.get(i).getDeductedOrder());
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(iplists.get(i).getDeductedQuantity().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(iplists.get(i).getDeductedAmount().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(iplists.get(i).getRollbackM().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(iplists.get(i).getAfrNoPayCode());
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(iplists.get(i).getAfrPayQuantity().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(iplists.get(i).getAfrPayAmount().doubleValue());
					cell.setCellStyle(cellFormatStyle);

					cellIndex = 0;
					rowIndex++;
				}
			}

			cellIndex = 0;
			rowIndex++;
			/// 資料統計
			List<DeductedNoteQueryConditionCode> opCodes = new ArrayList<DeductedNoteQueryConditionCode>();
			List<DeductedNoteQueryConditionCode> ipCodes = new ArrayList<DeductedNoteQueryConditionCode>();
			for (int i = 0; i < modelList.size(); i++) {
				List<DeductedNoteQueryConditionCode> codes = modelList.get(i).getDeductedCode();
				if (codes != null && codes.size() > 0) {
					for (int v = 0; v < codes.size(); v++) {
						if (codes.get(v).getDataFormat().equals("10")) {
							opCodes.add(codes.get(v));
						} else {
							ipCodes.add(codes.get(v));
						}
					}
				}
			}
			/// 欄位A
			row = sheet.createRow(rowIndex);
			cell = row.createCell(0);
			cell.setCellValue("核刪代碼數量");
			cell.setCellStyle(cellStyle);
			rowIndex++;
			cellIndex = 0;
			row = sheet.createRow(rowIndex);
			int trh3RowIndex = row.getRowNum();
			for (int x = 0; x < tableRowHeaders3.length; x++) {
				cell = row.createCell(x);
				cell.setCellValue(tableRowHeaders3[x]);
				if (x == 0) {
					cell.setCellStyle(cellTitleStyle);
				} else {
					cell.setCellStyle(cellStyle);
				}
			}
			rowIndex++;
			cellIndex = 0;
			/// 資料整理
			List<DeductedNoteQueryConditionCode> finalIpCodes = new ArrayList<DeductedNoteQueryConditionCode>();
			List<DeductedNoteQueryConditionCode> finalOpCodes = new ArrayList<DeductedNoteQueryConditionCode>();
			if (opCodes.size() > 0) {
				Collections.sort(opCodes, mapComparatorDeductedCD);
				DeductedNoteQueryConditionCode codeModel = new DeductedNoteQueryConditionCode();
				int index = -1;
				String key = "";
				Long quantity = 0L;
				Long amount = 0L;
				for (int i = 0; i < opCodes.size(); i++) {
					String ic = opCodes.get(i).getCode();
					if (ic.equals(key)) {
						break;
					}
					for (int j = 0; j < opCodes.size(); j++) {
						String jc = opCodes.get(j).getCode();
						if (ic.equals(jc)) {
							key = jc;
							amount += opCodes.get(j).getDeductedAmount();
							quantity += opCodes.get(j).getDeductedQuantity();
							index = 1;
							/// 最後一筆直接+
							if (i == opCodes.size() - 1) {
								codeModel.setCode(ic);
								codeModel.setDataFormat("10");
								codeModel.setDeductedAmount(amount);
								codeModel.setDeductedQuantity(quantity);
								finalOpCodes.add(codeModel);
								codeModel = new DeductedNoteQueryConditionCode();
								amount = 0L;
								quantity = 0L;
								index = -1;
							}
						} else {
							if (index > 0) {
								codeModel.setCode(ic);
								codeModel.setDataFormat("10");
								codeModel.setDeductedAmount(amount);
								codeModel.setDeductedQuantity(quantity);
								finalOpCodes.add(codeModel);
								codeModel = new DeductedNoteQueryConditionCode();
								amount = 0L;
								quantity = 0L;
								index = -1;
								break;
							} else {
								index = -1;
								continue;
							}
						}
					}
				}
				if (finalOpCodes.size() > 0) {
					row = sheet.createRow(rowIndex);
					for (int i = 0; i < finalOpCodes.size(); i++) {
						cell = row.createCell(cellIndex);
						cell.setCellValue("");
						cell.setCellStyle(cellTitleStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(finalOpCodes.get(i).getCode());
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(finalOpCodes.get(i).getDeductedQuantity().doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(finalOpCodes.get(i).getDeductedAmount().doubleValue());
						cell.setCellStyle(cellFormatStyle);

						rowIndex++;
						cellIndex = 0;
						row = sheet.createRow(rowIndex);
					}
				}
				sheet.addMergedRegion(new CellRangeAddress(trh3RowIndex, rowIndex - 1, 0, 0));
				rowIndex++;
			}
			row = sheet.createRow(rowIndex);
			trh3RowIndex = row.getRowNum();
			for (int x = 0; x < tableRowHeaders4.length; x++) {
				cell = row.createCell(x);
				cell.setCellValue(tableRowHeaders4[x]);
				if (x == 0) {
					cell.setCellStyle(cellTitleStyle);
				} else {
					cell.setCellStyle(cellStyle);
				}
			}
			rowIndex++;
			cellIndex = 0;
			if (ipCodes.size() > 0) {
				Collections.sort(ipCodes, mapComparatorDeductedCD);
				DeductedNoteQueryConditionCode codeModel = new DeductedNoteQueryConditionCode();
				int index = -1;
				String key = "";
				Long quantity = 0L;
				Long amount = 0L;
				for (int i = 0; i < ipCodes.size(); i++) {
					String ic = ipCodes.get(i).getCode();
					if (ic.equals(key)) {
						break;
					}
					for (int j = 0; j < ipCodes.size(); j++) {
						String jc = ipCodes.get(j).getCode();
						if (ic.equals(jc)) {
							key = jc;
							amount += ipCodes.get(j).getDeductedAmount();
							quantity += ipCodes.get(j).getDeductedQuantity();
							index = 1;
							/// 最後一筆直接+
							if (i == ipCodes.size() - 1) {
								codeModel.setCode(ic);
								codeModel.setDataFormat("20");
								codeModel.setDeductedAmount(amount);
								codeModel.setDeductedQuantity(quantity);
								finalIpCodes.add(codeModel);
								codeModel = new DeductedNoteQueryConditionCode();
								amount = 0L;
								quantity = 0L;
								index = -1;
							}
						} else {
							if (index > 0) {
								codeModel.setCode(ic);
								codeModel.setDataFormat("20");
								codeModel.setDeductedAmount(amount);
								codeModel.setDeductedQuantity(quantity);
								finalIpCodes.add(codeModel);
								codeModel = new DeductedNoteQueryConditionCode();
								amount = 0L;
								quantity = 0L;
								index = -1;
								break;
							} else {
								index = -1;
								continue;
							}
						}
					}
				}
				if (finalIpCodes.size() > 0) {
					row = sheet.createRow(rowIndex);
					for (int i = 0; i < finalIpCodes.size(); i++) {
						cell = row.createCell(cellIndex);
						cell.setCellValue("");
						cell.setCellStyle(cellTitleStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(finalIpCodes.get(i).getCode());
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(finalIpCodes.get(i).getDeductedQuantity().doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(finalIpCodes.get(i).getDeductedAmount().doubleValue());
						cell.setCellStyle(cellFormatStyle);

						rowIndex++;
						cellIndex = 0;
						row = sheet.createRow(rowIndex);
					}
				}
				sheet.addMergedRegion(new CellRangeAddress(trh3RowIndex, rowIndex - 1, 0, 0));
				rowIndex++;
			}

			/// 最後設定autosize
			for (int i = 0; i < tableRowHeaders.length + 2; i++) {
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
			}
		} else {
			HSSFSheet sheet = workbook.createSheet("工作表");
		}
		String fileNameStr = "核刪件數資訊";
		String fileName = URLEncoder.encode(fileNameStr, "UTF-8");
		String filepath = (System.getProperty("os.name").toLowerCase().startsWith("windows"))
				? FILE_PATH + "\\" + fileName
				: FILE_PATH + "/" + fileName;
		File file = new File(filepath);
		response.reset();
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "*");
		response.setHeader("Content-Disposition",
				"attachment; filename=" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + ".csv");
		response.setContentType("application/octet-stream;charset=utf8");

		/// 最後由outputstream輸出
		OutputStream out = response.getOutputStream();

		workbook.write(out);
		out.flush();
		out.close();
		workbook.close();
	}

	public void addRowCell(HSSFRow row, int num, String value, HSSFCellStyle cellStyle) {
		// 建立單元格,row已經確定了行號,列號作為引數傳遞給createCell(),第一列從0開始計算
		HSSFCell cell = row.createCell(num);
		// 設定單元格的值,即A1的值(第一行,第一列)
		cell.setCellValue(value);
		if (cellStyle != null) {
			cell.setCellStyle(cellStyle);
		}
	}

	@SuppressWarnings("unchecked")
	public void medicalOrderTemplate_dateType0(HSSFSheet medicalOrderSheet, List<Map<String, Object>> dataList,
			List<Map<String, Object>> dataList_lastY, List<Map<String, Object>> dataList_lastM,
			List<Map<String, Object>> classDataList, List<Map<String, Object>> classDataList_lastY,
			List<Map<String, Object>> classDataList_lastM, List<Map<String, Object>> medDataList,
			List<Map<String, Object>> medDataList_lastY, List<Map<String, Object>> medDataList_lastM,
			List<String> lastDateStr, List<String> yearMonth, List<String> feeApplyList, List<String> funcTypeList,
			boolean isLastM, boolean isLastY, HSSFCellStyle cellStyle, HSSFCellStyle cellStyle_left) {

		int indexRow = 6;

		List<Map<String, Object>> data_Y = null;
		List<Map<String, Object>> data_M = null;
		List<Map<String, Object>> classData_Y = null;
		List<Map<String, Object>> classData_M = null;

		for (int i = 0; i < dataList.size(); i++) {

			StringBuffer date = new StringBuffer(dataList.get(i).get("date").toString());
			StringBuffer dataFormat = new StringBuffer(dataList.get(i).get("dataFormat").toString());

			List<Map<String, Object>> data = (List<Map<String, Object>>) dataList.get(i).get("data");
			List<Map<String, Object>> classData = (List<Map<String, Object>>) classDataList.get(i).get("classData");

			if (!lastDateStr.contains(date.toString().replaceAll("\\-", "")) || yearMonth.contains(date.toString())) {

				if (dataList_lastY.size() > 0 && isLastY) {
					data_Y = getLastYearData(false, date.toString(), dataFormat.toString(), dataList_lastY);
				}

				if (dataList_lastM.size() > 0 && isLastM) {
					data_M = getLastMonthData(false, date.toString(), dataFormat.toString(), dataList_lastM);
				}

				if (classDataList_lastY.size() > 0 && isLastY) {
					classData_Y = getLastYearData(true, date.toString(), dataFormat.toString(), classDataList_lastY);
				}

				if (classDataList_lastM.size() > 0 && isLastM) {
					classData_M = getLastMonthData(true, date.toString(), dataFormat.toString(), classDataList_lastM);
				}

				switch (dataFormat.toString()) {
				case "不分區":
					indexRow = addRowCellByMedicalOrder_dateType0(medicalOrderSheet, "ALL", indexRow, date.toString(),
							dataFormat.toString(), data, data_Y, data_M, classData, classData_Y, classData_M,
							medDataList, medDataList_lastY, medDataList_lastM, feeApplyList, funcTypeList, lastDateStr,
							yearMonth, isLastM, isLastY, cellStyle, cellStyle_left);
					break;
				case "門急診":
					indexRow = addRowCellByMedicalOrder_dateType0(medicalOrderSheet, "OPALL", indexRow, date.toString(),
							dataFormat.toString(), data, data_Y, data_M, classData, classData_Y, classData_M,
							medDataList, medDataList_lastY, medDataList_lastM, feeApplyList, funcTypeList, lastDateStr,
							yearMonth, isLastM, isLastY, cellStyle, cellStyle_left);
					break;
				case "門診":
					indexRow = addRowCellByMedicalOrder_dateType0(medicalOrderSheet, "OP", indexRow, date.toString(),
							dataFormat.toString(), data, data_Y, data_M, classData, classData_Y, classData_M,
							medDataList, medDataList_lastY, medDataList_lastM, feeApplyList, funcTypeList, lastDateStr,
							yearMonth, isLastM, isLastY, cellStyle, cellStyle_left);
					break;
				case "急診":
					indexRow = addRowCellByMedicalOrder_dateType0(medicalOrderSheet, "EM", indexRow, date.toString(),
							dataFormat.toString(), data, data_Y, data_M, classData, classData_Y, classData_M,
							medDataList, medDataList_lastY, medDataList_lastM, feeApplyList, funcTypeList, lastDateStr,
							yearMonth, isLastM, isLastY, cellStyle, cellStyle_left);
					break;
				case "住院":
					indexRow = addRowCellByMedicalOrder_dateType0(medicalOrderSheet, "IP", indexRow, date.toString(),
							dataFormat.toString(), data, data_Y, data_M, classData, classData_Y, classData_M,
							medDataList, medDataList_lastY, medDataList_lastM, feeApplyList, funcTypeList, lastDateStr,
							yearMonth, isLastM, isLastY, cellStyle, cellStyle_left);
					break;
				default:
					break;
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public int addRowCellByMedicalOrder_dateType0(HSSFSheet medicalOrderSheet, String dataFormatCode, int indexRow,
			String date, String dataFormat, List<Map<String, Object>> data, List<Map<String, Object>> data_Y,
			List<Map<String, Object>> data_M, List<Map<String, Object>> classData,
			List<Map<String, Object>> classData_Y, List<Map<String, Object>> classData_M,
			List<Map<String, Object>> medDataList, List<Map<String, Object>> medDataList_Y,
			List<Map<String, Object>> medDataList_M, List<String> feeApplyList, List<String> funcTypeList,
			List<String> lastDateStr, List<String> yearMonth, boolean isLastM, boolean isLastY, HSSFCellStyle cellStyle,
			HSSFCellStyle cellStyle_left) {

		StringBuffer titleName = new StringBuffer();

		// 起訖日期title
		HSSFRow titleRow = medicalOrderSheet.createRow(indexRow);
		medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, 0, 1));
		medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, 2, 3));
		addRowCell(titleRow, 0, "就醫日期-起", cellStyle_left);
		addRowCell(titleRow, 1, "", cellStyle_left);
		addRowCell(titleRow, 2, "就醫日期-訖", cellStyle_left);
		addRowCell(titleRow, 3, "", cellStyle_left);

		int indexCol = 4;

		// 案件數title
		titleName = new StringBuffer("");
		titleName.append(dataFormat);
		titleName.append("總案件數");
		medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
		addRowCell(titleRow, indexCol, titleName.toString(), cellStyle_left);
		addRowCell(titleRow, indexCol + 1, "", cellStyle_left);
		addRowCell(titleRow, indexCol + 2, "", cellStyle_left);
		indexCol = indexCol + 3;

		// 上個月同條件案件數title
		if (isLastM) {
			titleName = new StringBuffer("");
			titleName.append("上個月同條件");
			medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
			addRowCell(titleRow, indexCol, titleName.toString(), cellStyle_left);
			addRowCell(titleRow, indexCol + 1, "", cellStyle_left);
			addRowCell(titleRow, indexCol + 2, "", cellStyle_left);
			indexCol = indexCol + 3;
		}
		// 去年同期時段案件數title
		if (isLastY) {
			titleName = new StringBuffer("");
			titleName.append("去年同期時段");
			medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
			addRowCell(titleRow, indexCol, titleName.toString(), cellStyle_left);
			addRowCell(titleRow, indexCol + 1, "", cellStyle_left);
			addRowCell(titleRow, indexCol + 2, "", cellStyle_left);
			indexCol = indexCol + 3;
		}

		// 申報點數title
		if (feeApplyList.contains("健保")) {
			titleName = new StringBuffer("");
			titleName.append(dataFormat);
			titleName.append("申報總點數");
			medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
			addRowCell(titleRow, indexCol, titleName.toString(), cellStyle_left);
			addRowCell(titleRow, indexCol + 1, "", cellStyle_left);
			addRowCell(titleRow, indexCol + 2, "", cellStyle_left);
			indexCol = indexCol + 3;

			// 上個月同條件申報點數title
			if (isLastM) {
				titleName = new StringBuffer("");
				titleName.append("上個月同條件");
				medicalOrderSheet
						.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
				addRowCell(titleRow, indexCol, titleName.toString(), cellStyle_left);
				addRowCell(titleRow, indexCol + 1, "", cellStyle_left);
				addRowCell(titleRow, indexCol + 2, "", cellStyle_left);
				indexCol = indexCol + 3;
			}
			// 去年同期時段申報點數title
			if (isLastY) {
				titleName = new StringBuffer("");
				titleName.append("去年同期時段");
				medicalOrderSheet
						.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
				addRowCell(titleRow, indexCol, titleName.toString(), cellStyle_left);
				addRowCell(titleRow, indexCol + 1, "", cellStyle_left);
				addRowCell(titleRow, indexCol + 2, "", cellStyle_left);
				indexCol = indexCol + 3;
			}
		}

		// 自費金額title
		if (feeApplyList.contains("自費")) {
			titleName = new StringBuffer("");
			titleName.append(dataFormat);
			titleName.append("自費總金額");
			medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
			addRowCell(titleRow, indexCol, titleName.toString(), cellStyle_left);
			addRowCell(titleRow, indexCol + 1, "", cellStyle_left);
			addRowCell(titleRow, indexCol + 2, "", cellStyle_left);
			indexCol = indexCol + 3;

			// 上個月同條件自費金額title
			if (isLastM) {
				titleName = new StringBuffer("");
				titleName.append("上個月同條件");
				medicalOrderSheet
						.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
				addRowCell(titleRow, indexCol, titleName.toString(), cellStyle_left);
				addRowCell(titleRow, indexCol + 1, "", cellStyle_left);
				addRowCell(titleRow, indexCol + 2, "", cellStyle_left);
				indexCol = indexCol + 3;
			}
			// 去年同期時段自費金額title
			if (isLastY) {
				titleName = new StringBuffer("");
				titleName.append("去年同期時段");
				medicalOrderSheet
						.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
				addRowCell(titleRow, indexCol, titleName.toString(), cellStyle_left);
				addRowCell(titleRow, indexCol + 1, "", cellStyle_left);
				addRowCell(titleRow, indexCol + 2, "", cellStyle_left);
				indexCol = indexCol + 3;
			}
		}

		indexRow++;

		indexCol = 4;

		// 起訖日期
		HSSFRow dataRow = medicalOrderSheet.createRow(indexRow);
		medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, 0, 1));
		medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, 2, 3));
		addRowCell(dataRow, 0, date, cellStyle_left);
		addRowCell(dataRow, 1, "", cellStyle_left);
		addRowCell(dataRow, 2, date, cellStyle_left);
		addRowCell(dataRow, 3, "", cellStyle_left);

		// 起訖日期之間的案件數
		medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
		if (data != null) {
			addRowCell(dataRow, indexCol,
					addThousandths(Long.parseLong(data.get(0).get(dataFormatCode + "_QUANTITY").toString())),
					cellStyle_left);
		} else {
			addRowCell(dataRow, indexCol, "0", cellStyle_left);
		}
		addRowCell(dataRow, indexCol + 1, "", cellStyle_left);
		addRowCell(dataRow, indexCol + 2, "", cellStyle_left);
		indexCol = indexCol + 3;

		// 上個月同條件的案件數
		if (isLastM) {
			medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
			if (data_M != null) {
				addRowCell(dataRow, indexCol,
						addThousandths(Long.parseLong(data_M.get(0).get(dataFormatCode + "_QUANTITY").toString())),
						cellStyle_left);
			} else {
				addRowCell(dataRow, indexCol, "0", cellStyle_left);
			}
			addRowCell(dataRow, indexCol + 1, "", cellStyle_left);
			addRowCell(dataRow, indexCol + 2, "", cellStyle_left);
			indexCol = indexCol + 3;
		}
		// 去年同期時段案件數
		if (isLastY) {
			medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
			if (data_Y != null) {
				addRowCell(dataRow, indexCol,
						addThousandths(Long.parseLong(data_Y.get(0).get(dataFormatCode + "_QUANTITY").toString())),
						cellStyle_left);
			} else {
				addRowCell(dataRow, indexCol, "0", cellStyle_left);
			}
			addRowCell(dataRow, indexCol + 1, "", cellStyle_left);
			addRowCell(dataRow, indexCol + 2, "", cellStyle_left);
			indexCol = indexCol + 3;
		}

		// 起訖日期之間的申報點數
		if (feeApplyList.contains("健保")) {
			medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
			if (data != null) {
				addRowCell(dataRow, indexCol,
						addThousandths(Long.parseLong(data.get(0).get(dataFormatCode + "_DOT").toString())),
						cellStyle_left);
			} else {
				addRowCell(dataRow, indexCol, "0", cellStyle_left);
			}
			addRowCell(dataRow, indexCol + 1, "", cellStyle_left);
			addRowCell(dataRow, indexCol + 2, "", cellStyle_left);
			indexCol = indexCol + 3;

			// 上個月同條件的申報點數
			if (isLastM) {
				medicalOrderSheet
						.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
				if (data_M != null) {
					addRowCell(dataRow, indexCol,
							addThousandths(Long.parseLong(data_M.get(0).get(dataFormatCode + "_DOT").toString())),
							cellStyle_left);
				} else {
					addRowCell(dataRow, indexCol, "0", cellStyle_left);
				}
				addRowCell(dataRow, indexCol + 1, "", cellStyle_left);
				addRowCell(dataRow, indexCol + 2, "", cellStyle_left);
				indexCol = indexCol + 3;
			}
			// 去年同期時段申報點數
			if (isLastY) {
				medicalOrderSheet
						.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
				if (data_Y != null) {
					addRowCell(dataRow, indexCol,
							addThousandths(Long.parseLong(data_Y.get(0).get(dataFormatCode + "_DOT").toString())),
							cellStyle_left);
				} else {
					addRowCell(dataRow, indexCol, "0", cellStyle_left);
				}
				addRowCell(dataRow, indexCol + 1, "", cellStyle_left);
				addRowCell(dataRow, indexCol + 2, "", cellStyle_left);
				indexCol = indexCol + 3;
			}
		}

		// 起訖日期之間的自費金額
		if (feeApplyList.contains("自費")) {
			medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
			if (data != null) {
				addRowCell(dataRow, indexCol,
						addThousandths(Long.parseLong(data.get(0).get(dataFormatCode + "_EXPENSE").toString())),
						cellStyle_left);
			} else {
				addRowCell(dataRow, indexCol, "0", cellStyle_left);
			}
			addRowCell(dataRow, indexCol + 1, "", cellStyle_left);
			addRowCell(dataRow, indexCol + 2, "", cellStyle_left);
			indexCol = indexCol + 3;

			// 上個月同條件的自費金額
			if (isLastM) {
				medicalOrderSheet
						.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
				if (data_M != null) {
					addRowCell(dataRow, indexCol,
							addThousandths(Long.parseLong(data_M.get(0).get(dataFormatCode + "_EXPENSE").toString())),
							cellStyle_left);
				} else {
					addRowCell(dataRow, indexCol, "0", cellStyle_left);
				}
				addRowCell(dataRow, indexCol + 1, "", cellStyle_left);
				addRowCell(dataRow, indexCol + 2, "", cellStyle_left);
				indexCol = indexCol + 3;
			}
			// 去年同期時段自費金額
			if (isLastY) {
				medicalOrderSheet
						.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
				if (data_Y != null) {
					addRowCell(dataRow, indexCol,
							addThousandths(Long.parseLong(data_Y.get(0).get(dataFormatCode + "_EXPENSE").toString())),
							cellStyle_left);
				} else {
					addRowCell(dataRow, indexCol, "0", cellStyle_left);
				}
				addRowCell(dataRow, indexCol + 1, "", cellStyle_left);
				addRowCell(dataRow, indexCol + 2, "", cellStyle_left);
				indexCol = indexCol + 3;
			}
		}

		indexRow++;

		// 科名title
		HSSFRow titleRow_class = medicalOrderSheet.createRow(indexRow);
		medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, 0, 3));
		addRowCell(titleRow_class, 0, "科名", cellStyle);
		addRowCell(titleRow_class, 1, "", cellStyle_left);
		addRowCell(titleRow_class, 2, "", cellStyle_left);
		addRowCell(titleRow_class, 3, "", cellStyle_left);

		indexCol = 4;

		// 科名案件數title
		titleName = new StringBuffer("");
//		titleName.append(dataFormat);
//		titleName.append("總案件數");
		medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
		addRowCell(titleRow_class, indexCol, titleName.toString(), cellStyle_left);
		addRowCell(titleRow_class, indexCol + 1, "", cellStyle_left);
		addRowCell(titleRow_class, indexCol + 2, "", cellStyle_left);
		indexCol = indexCol + 3;

		// 科名上個月同條件案件數title
		if (isLastM) {
			titleName = new StringBuffer("");
//			titleName.append("上個月同條件");
			medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
			addRowCell(titleRow_class, indexCol, titleName.toString(), cellStyle_left);
			addRowCell(titleRow_class, indexCol + 1, "", cellStyle_left);
			addRowCell(titleRow_class, indexCol + 2, "", cellStyle_left);
			indexCol = indexCol + 3;
		}
		// 科名去年同期時段案件數title
		if (isLastY) {
			titleName = new StringBuffer("");
//			titleName.append("去年同期時段");
			medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
			addRowCell(titleRow_class, indexCol, titleName.toString(), cellStyle_left);
			addRowCell(titleRow_class, indexCol + 1, "", cellStyle_left);
			addRowCell(titleRow_class, indexCol + 2, "", cellStyle_left);
			indexCol = indexCol + 3;
		}

		// 科名申報點數title
		if (feeApplyList.contains("健保")) {
			titleName = new StringBuffer("");
//			titleName.append(dataFormat);
//			titleName.append("申報總點數");
			medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
			addRowCell(titleRow_class, indexCol, titleName.toString(), cellStyle_left);
			addRowCell(titleRow_class, indexCol + 1, "", cellStyle_left);
			addRowCell(titleRow_class, indexCol + 2, "", cellStyle_left);
			indexCol = indexCol + 3;

			// 科名上個月同條件申報點數title
			if (isLastM) {
				titleName = new StringBuffer("");
//				titleName.append("上個月同條件");
				medicalOrderSheet
						.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
				addRowCell(titleRow_class, indexCol, titleName.toString(), cellStyle_left);
				addRowCell(titleRow_class, indexCol + 1, "", cellStyle_left);
				addRowCell(titleRow_class, indexCol + 2, "", cellStyle_left);
				indexCol = indexCol + 3;
			}
			// 科名去年同期時段申報點數title
			if (isLastY) {
				titleName = new StringBuffer("");
//				titleName.append("去年同期時段");
				medicalOrderSheet
						.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
				addRowCell(titleRow_class, indexCol, titleName.toString(), cellStyle_left);
				addRowCell(titleRow_class, indexCol + 1, "", cellStyle_left);
				addRowCell(titleRow_class, indexCol + 2, "", cellStyle_left);
				indexCol = indexCol + 3;
			}
		}

		// 科名自費金額title
		if (feeApplyList.contains("自費")) {
			titleName = new StringBuffer("");
//			titleName.append(dataFormat);
//			titleName.append("自費總金額");
			medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
			addRowCell(titleRow_class, indexCol, titleName.toString(), cellStyle_left);
			addRowCell(titleRow_class, indexCol + 1, "", cellStyle_left);
			addRowCell(titleRow_class, indexCol + 2, "", cellStyle_left);
			indexCol = indexCol + 3;

			// 科名上個月同條件自費金額title
			if (isLastM) {
				titleName = new StringBuffer("");
//				titleName.append("上個月同條件");
				medicalOrderSheet
						.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
				addRowCell(titleRow_class, indexCol, titleName.toString(), cellStyle_left);
				addRowCell(titleRow_class, indexCol + 1, "", cellStyle_left);
				addRowCell(titleRow_class, indexCol + 2, "", cellStyle_left);
				indexCol = indexCol + 3;
			}
			// 科名去年同期時段自費金額title
			if (isLastY) {
				titleName = new StringBuffer("");
//				titleName.append("去年同期時段");
				medicalOrderSheet
						.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
				addRowCell(titleRow_class, indexCol, titleName.toString(), cellStyle_left);
				addRowCell(titleRow_class, indexCol + 1, "", cellStyle_left);
				addRowCell(titleRow_class, indexCol + 2, "", cellStyle_left);
				indexCol = indexCol + 3;
			}
		}

		indexRow++;

		/*
		 * 各科別案件數、申報點數、自費金額 當科別條件為空，顯示全部科別資料，不為空，顯示指定科別資料
		 */
		if (funcTypeList.size() < 1) {

			for (int i = 0; i < classData.size(); i++) {

				StringBuffer desc_chi = new StringBuffer("");
				StringBuffer quantity = new StringBuffer("");
				StringBuffer dot = new StringBuffer("");
				StringBuffer expense = new StringBuffer("");

				StringBuffer desc_chi_Y = new StringBuffer("");
				StringBuffer quantity_Y = new StringBuffer("");
				StringBuffer dot_Y = new StringBuffer("");
				StringBuffer expense_Y = new StringBuffer("");

				StringBuffer desc_chi_M = new StringBuffer("");
				StringBuffer quantity_M = new StringBuffer("");
				StringBuffer dot_M = new StringBuffer("");
				StringBuffer expense_M = new StringBuffer("");

				desc_chi = new StringBuffer(classData.get(i).get("DESC_CHI").toString());
				quantity = new StringBuffer(classData.get(i).get("QUANTITY").toString());
				dot = new StringBuffer(classData.get(i).get("DOT").toString());
				expense = new StringBuffer(classData.get(i).get("EXPENSE").toString());

				if (classData_Y != null) {
					desc_chi_Y = new StringBuffer(classData_Y.get(i).get("DESC_CHI").toString());
					quantity_Y = new StringBuffer(classData_Y.get(i).get("QUANTITY").toString());
					dot_Y = new StringBuffer(classData_Y.get(i).get("DOT").toString());
					expense_Y = new StringBuffer(classData_Y.get(i).get("EXPENSE").toString());
				}

				if (classData_M != null) {
					desc_chi_M = new StringBuffer(classData_M.get(i).get("DESC_CHI").toString());
					quantity_M = new StringBuffer(classData_M.get(i).get("QUANTITY").toString());
					dot_M = new StringBuffer(classData_M.get(i).get("DOT").toString());
					expense_M = new StringBuffer(classData_M.get(i).get("EXPENSE").toString());
				}

				indexCol = 4;

				// 科名
				HSSFRow dataRow_class = medicalOrderSheet.createRow(indexRow);
				medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, 0, 3));
				addRowCell(dataRow_class, 0, desc_chi.toString(), cellStyle_left);
				addRowCell(dataRow_class, 1, "", cellStyle_left);
				addRowCell(dataRow_class, 2, "", cellStyle_left);
				addRowCell(dataRow_class, 3, "", cellStyle_left);

				// 科名的案件數
				medicalOrderSheet
						.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
				if (classData.get(i) != null) {
					addRowCell(dataRow_class, indexCol, addThousandths(Long.parseLong(quantity.toString())),
							cellStyle_left);
				} else {
					addRowCell(dataRow_class, indexCol, "0", cellStyle_left);
				}
				addRowCell(dataRow_class, indexCol + 1, "", cellStyle_left);
				addRowCell(dataRow_class, indexCol + 2, "", cellStyle_left);
				indexCol = indexCol + 3;

				// 科名上個月同條件的案件數
				if (isLastM) {
					medicalOrderSheet
							.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
					if (classData_M != null) {
						addRowCell(dataRow_class, indexCol, addThousandths(Long.parseLong(quantity_M.toString())),
								cellStyle_left);
					} else {
						addRowCell(dataRow_class, indexCol, "0", cellStyle_left);
					}
					addRowCell(dataRow_class, indexCol + 1, "", cellStyle_left);
					addRowCell(dataRow_class, indexCol + 2, "", cellStyle_left);
					indexCol = indexCol + 3;
				}
				// 科名去年同期時段案件數
				if (isLastY) {
					medicalOrderSheet
							.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
					if (classData_Y != null) {
						addRowCell(dataRow_class, indexCol, addThousandths(Long.parseLong(quantity_Y.toString())),
								cellStyle_left);
					} else {
						addRowCell(dataRow_class, indexCol, "0", cellStyle_left);
					}
					addRowCell(dataRow_class, indexCol + 1, "", cellStyle_left);
					addRowCell(dataRow_class, indexCol + 2, "", cellStyle_left);
					indexCol = indexCol + 3;
				}

				// 科名的申報點數
				if (feeApplyList.contains("健保")) {
					medicalOrderSheet
							.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
					if (classData.get(i) != null) {
						addRowCell(dataRow_class, indexCol, addThousandths(Long.parseLong(dot.toString())),
								cellStyle_left);
					} else {
						addRowCell(dataRow_class, indexCol, "0", cellStyle_left);
					}
					addRowCell(dataRow_class, indexCol + 1, "", cellStyle_left);
					addRowCell(dataRow_class, indexCol + 2, "", cellStyle_left);
					indexCol = indexCol + 3;

					// 科名上個月同條件的申報點數
					if (isLastM) {
						medicalOrderSheet.addMergedRegionUnsafe(
								new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
						if (classData_M != null) {
							addRowCell(dataRow_class, indexCol, addThousandths(Long.parseLong(dot_M.toString())),
									cellStyle_left);
						} else {
							addRowCell(dataRow_class, indexCol, "0", cellStyle_left);
						}
						addRowCell(dataRow_class, indexCol + 1, "", cellStyle_left);
						addRowCell(dataRow_class, indexCol + 2, "", cellStyle_left);
						indexCol = indexCol + 3;
					}
					// 科名去年同期時段申報點數
					if (isLastY) {
						medicalOrderSheet.addMergedRegionUnsafe(
								new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
						if (classData_Y != null) {
							addRowCell(dataRow_class, indexCol, addThousandths(Long.parseLong(dot_Y.toString())),
									cellStyle_left);
						} else {
							addRowCell(dataRow_class, indexCol, "0", cellStyle_left);
						}
						addRowCell(dataRow_class, indexCol + 1, "", cellStyle_left);
						addRowCell(dataRow_class, indexCol + 2, "", cellStyle_left);
						indexCol = indexCol + 3;
					}
				}

				// 科名的自費金額
				if (feeApplyList.contains("自費")) {
					medicalOrderSheet
							.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
					if (classData.get(i) != null) {
						addRowCell(dataRow_class, indexCol, addThousandths(Long.parseLong(expense.toString())),
								cellStyle_left);
					} else {
						addRowCell(dataRow_class, indexCol, "0", cellStyle_left);
					}
					addRowCell(dataRow_class, indexCol + 1, "", cellStyle_left);
					addRowCell(dataRow_class, indexCol + 2, "", cellStyle_left);
					indexCol = indexCol + 3;

					// 科名上個月同條件的自費金額
					if (isLastM) {
						medicalOrderSheet.addMergedRegionUnsafe(
								new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
						if (classData_M != null) {
							addRowCell(dataRow_class, indexCol, addThousandths(Long.parseLong(expense_M.toString())),
									cellStyle_left);
						} else {
							addRowCell(dataRow_class, indexCol, "0", cellStyle_left);
						}
						addRowCell(dataRow_class, indexCol + 1, "", cellStyle_left);
						addRowCell(dataRow_class, indexCol + 2, "", cellStyle_left);
						indexCol = indexCol + 3;
					}
					// 科名去年同期時段自費金額
					if (isLastY) {
						medicalOrderSheet.addMergedRegionUnsafe(
								new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
						if (classData_Y != null) {
							addRowCell(dataRow_class, indexCol, addThousandths(Long.parseLong(expense_Y.toString())),
									cellStyle_left);
						} else {
							addRowCell(dataRow_class, indexCol, "0", cellStyle_left);
						}
						addRowCell(dataRow_class, indexCol + 1, "", cellStyle_left);
						addRowCell(dataRow_class, indexCol + 2, "", cellStyle_left);
						indexCol = indexCol + 3;
					}
				}

				indexRow++;

				indexCol = 4;

				// 科別之下醫護人員的案件數、申報點數、自費金額
				if (medDataList.size() > 0) {

					// 醫護名title
					HSSFRow titleRow_med = medicalOrderSheet.createRow(indexRow);
					medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, 0, 3));
					addRowCell(titleRow_med, 0, "醫護名", cellStyle);
					addRowCell(titleRow_med, 1, "", cellStyle_left);
					addRowCell(titleRow_med, 2, "", cellStyle_left);
					addRowCell(titleRow_med, 3, "", cellStyle_left);

					// 醫護案件數title
					titleName = new StringBuffer("");
//					titleName.append(dataFormat);
//					titleName.append("總案件數");
					medicalOrderSheet
							.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
					addRowCell(titleRow_med, indexCol, titleName.toString(), cellStyle_left);
					addRowCell(titleRow_med, indexCol + 1, "", cellStyle_left);
					addRowCell(titleRow_med, indexCol + 2, "", cellStyle_left);
					indexCol = indexCol + 3;

					// 醫護上個月同條件案件數title
					if (isLastM) {
						titleName = new StringBuffer("");
//						titleName.append("上個月同條件");
						medicalOrderSheet.addMergedRegionUnsafe(
								new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
						addRowCell(titleRow_med, indexCol, titleName.toString(), cellStyle_left);
						addRowCell(titleRow_med, indexCol + 1, "", cellStyle_left);
						addRowCell(titleRow_med, indexCol + 2, "", cellStyle_left);
						indexCol = indexCol + 3;
					}
					// 醫護去年同期時段案件數title
					if (isLastY) {
						titleName = new StringBuffer("");
//						titleName.append("去年同期時段");
						medicalOrderSheet.addMergedRegionUnsafe(
								new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
						addRowCell(titleRow_med, indexCol, titleName.toString(), cellStyle_left);
						addRowCell(titleRow_med, indexCol + 1, "", cellStyle_left);
						addRowCell(titleRow_med, indexCol + 2, "", cellStyle_left);
						indexCol = indexCol + 3;
					}

					// 醫護申報點數title
					if (feeApplyList.contains("健保")) {
						titleName = new StringBuffer("");
//						titleName.append(dataFormat);
//						titleName.append("申報總點數");
						medicalOrderSheet.addMergedRegionUnsafe(
								new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
						addRowCell(titleRow_med, indexCol, titleName.toString(), cellStyle_left);
						addRowCell(titleRow_med, indexCol + 1, "", cellStyle_left);
						addRowCell(titleRow_med, indexCol + 2, "", cellStyle_left);
						indexCol = indexCol + 3;

						// 醫護上個月同條件申報點數title
						if (isLastM) {
							titleName = new StringBuffer("");
//							titleName.append("上個月同條件");
							medicalOrderSheet.addMergedRegionUnsafe(
									new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
							addRowCell(titleRow_med, indexCol, titleName.toString(), cellStyle_left);
							addRowCell(titleRow_med, indexCol + 1, "", cellStyle_left);
							addRowCell(titleRow_med, indexCol + 2, "", cellStyle_left);
							indexCol = indexCol + 3;
						}
						// 醫護去年同期時段申報點數title
						if (isLastY) {
							titleName = new StringBuffer("");
//							titleName.append("去年同期時段");
							medicalOrderSheet.addMergedRegionUnsafe(
									new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
							addRowCell(titleRow_med, indexCol, titleName.toString(), cellStyle_left);
							addRowCell(titleRow_med, indexCol + 1, "", cellStyle_left);
							addRowCell(titleRow_med, indexCol + 2, "", cellStyle_left);
							indexCol = indexCol + 3;
						}
					}

					// 醫護自費金額title
					if (feeApplyList.contains("自費")) {
						titleName = new StringBuffer("");
//						titleName.append(dataFormat);
//						titleName.append("自費總金額");
						medicalOrderSheet.addMergedRegionUnsafe(
								new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
						addRowCell(titleRow_med, indexCol, titleName.toString(), cellStyle_left);
						addRowCell(titleRow_med, indexCol + 1, "", cellStyle_left);
						addRowCell(titleRow_med, indexCol + 2, "", cellStyle_left);
						indexCol = indexCol + 3;

						// 醫護上個月同條件自費金額title
						if (isLastM) {
							titleName = new StringBuffer("");
//							titleName.append("上個月同條件");
							medicalOrderSheet.addMergedRegionUnsafe(
									new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
							addRowCell(titleRow_med, indexCol, titleName.toString(), cellStyle_left);
							addRowCell(titleRow_med, indexCol + 1, "", cellStyle_left);
							addRowCell(titleRow_med, indexCol + 2, "", cellStyle_left);
							indexCol = indexCol + 3;
						}
						// 醫護去年同期時段自費金額title
						if (isLastY) {
							titleName = new StringBuffer("");
//							titleName.append("去年同期時段");
							medicalOrderSheet.addMergedRegionUnsafe(
									new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
							addRowCell(titleRow_med, indexCol, titleName.toString(), cellStyle_left);
							addRowCell(titleRow_med, indexCol + 1, "", cellStyle_left);
							addRowCell(titleRow_med, indexCol + 2, "", cellStyle_left);
							indexCol = indexCol + 3;
						}
					}

					indexRow++;

					Map<String, Object> medData_lastY = new HashedMap<String, Object>();
					Map<String, Object> medData_lastM = new HashedMap<String, Object>();
					Map<String, Object> medData = new HashedMap<String, Object>();

					for (int j = 0; j < medDataList.size(); j++) {

						StringBuffer medDate = new StringBuffer(medDataList.get(j).get("date").toString());
						StringBuffer medName = new StringBuffer(medDataList.get(j).get("medName").toString());
						StringBuffer medDataF = new StringBuffer(medDataList.get(j).get("dataFormat").toString());

						if ((!lastDateStr.contains(medDate.toString().replaceAll("\\-", ""))
								|| yearMonth.contains(medDate.toString())) && date.equals(medDate.toString())
								&& dataFormat.equals(medDataF.toString())) {

//							    StringBuffer medDesc_chi=new StringBuffer("0");
							StringBuffer medQuantity = new StringBuffer("0");
							StringBuffer medDot = new StringBuffer("0");
							StringBuffer medExpense = new StringBuffer("0");

							StringBuffer medQuantity_Y = new StringBuffer("0");
							StringBuffer medDot_Y = new StringBuffer("0");
							StringBuffer medExpense_Y = new StringBuffer("0");

							StringBuffer medQuantity_M = new StringBuffer("0");
							StringBuffer medDot_M = new StringBuffer("0");
							StringBuffer medExpense_M = new StringBuffer("0");

							List<Map<String, Object>> md = (List<Map<String, Object>>) medDataList.get(j).get("data");

							for (int k = 0; k < md.size(); k++) {
								if (md.get(k).get("DESC_CHI").equals(desc_chi.toString())) {
									medData = (Map<String, Object>) md.get(k);
									break;
								}
							}

							if (medDataList_Y.size() > 0 && isLastY) {
								medData_lastY = getLastYearMedData(medDate.toString(), medDataF.toString(),
										medName.toString(), desc_chi.toString(), medDataList_Y);
							}

							if (medDataList_M.size() > 0 && isLastM) {
								medData_lastM = getLastMonthMedData(medDate.toString(), medDataF.toString(),
										medName.toString(), desc_chi.toString(), medDataList_M);
							}

							if (medData != null && !medData.isEmpty()) {
								medQuantity = new StringBuffer(medData.get("QUANTITY").toString());
								medDot = new StringBuffer(medData.get("DOT").toString());
								medExpense = new StringBuffer(medData.get("EXPENSE").toString());
							}

							if (medData_lastY != null && !medData_lastY.isEmpty()) {
								medQuantity_Y = new StringBuffer(medData_lastY.get("QUANTITY").toString());
								medDot_Y = new StringBuffer(medData_lastY.get("DOT").toString());
								medExpense_Y = new StringBuffer(medData_lastY.get("EXPENSE").toString());
							}

							if (medData_lastM != null && !medData_lastM.isEmpty()) {
								medQuantity_M = new StringBuffer(medData_lastM.get("QUANTITY").toString());
								medDot_M = new StringBuffer(medData_lastM.get("DOT").toString());
								medExpense_M = new StringBuffer(medData_lastM.get("EXPENSE").toString());
							}

							indexCol = 4;

							// 醫護名
							HSSFRow dataRow_med = medicalOrderSheet.createRow(indexRow);
							medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, 0, 3));
							addRowCell(dataRow_med, 0, medName.toString(), cellStyle_left);
							addRowCell(dataRow_med, 1, "", cellStyle_left);
							addRowCell(dataRow_med, 2, "", cellStyle_left);
							addRowCell(dataRow_med, 3, "", cellStyle_left);

							// 醫護的案件數
							medicalOrderSheet.addMergedRegionUnsafe(
									new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
							if (!medQuantity.toString().equals("")) {
								addRowCell(dataRow_med, indexCol,
										addThousandths(Long.parseLong(medQuantity.toString())), cellStyle_left);
							} else {
								addRowCell(dataRow_med, indexCol, "0", cellStyle_left);
							}
							addRowCell(dataRow_med, indexCol + 1, "", cellStyle_left);
							addRowCell(dataRow_med, indexCol + 2, "", cellStyle_left);
							indexCol = indexCol + 3;

							// 醫護上個月同條件的案件數
							if (isLastM) {
								medicalOrderSheet.addMergedRegionUnsafe(
										new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
								if (!medQuantity_M.toString().equals("")) {
									addRowCell(dataRow_med, indexCol,
											addThousandths(Long.parseLong(medQuantity_M.toString())), cellStyle_left);
								} else {
									addRowCell(dataRow_med, indexCol, "0", cellStyle_left);
								}
								addRowCell(dataRow_med, indexCol + 1, "", cellStyle_left);
								addRowCell(dataRow_med, indexCol + 2, "", cellStyle_left);
								indexCol = indexCol + 3;
							}
							// 醫護去年同期時段案件數
							if (isLastY) {
								medicalOrderSheet.addMergedRegionUnsafe(
										new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
								if (!medQuantity_Y.toString().equals("")) {
									addRowCell(dataRow_med, indexCol,
											addThousandths(Long.parseLong(medQuantity_Y.toString())), cellStyle_left);
								} else {
									addRowCell(dataRow_med, indexCol, "0", cellStyle_left);
								}
								addRowCell(dataRow_med, indexCol + 1, "", cellStyle_left);
								addRowCell(dataRow_med, indexCol + 2, "", cellStyle_left);
								indexCol = indexCol + 3;
							}

							// 醫護的申報點數
							if (feeApplyList.contains("健保")) {
								medicalOrderSheet.addMergedRegionUnsafe(
										new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
								if (!medDot.toString().equals("")) {
									addRowCell(dataRow_med, indexCol, addThousandths(Long.parseLong(medDot.toString())),
											cellStyle_left);
								} else {
									addRowCell(dataRow_med, indexCol, "0", cellStyle_left);
								}
								addRowCell(dataRow_med, indexCol + 1, "", cellStyle_left);
								addRowCell(dataRow_med, indexCol + 2, "", cellStyle_left);
								indexCol = indexCol + 3;

								// 醫護上個月同條件的申報點數
								if (isLastM) {
									medicalOrderSheet.addMergedRegionUnsafe(
											new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
									if (!medDot_M.toString().equals("")) {
										addRowCell(dataRow_med, indexCol,
												addThousandths(Long.parseLong(medDot_M.toString())), cellStyle_left);
									} else {
										addRowCell(dataRow_med, indexCol, "0", cellStyle_left);
									}
									addRowCell(dataRow_med, indexCol + 1, "", cellStyle_left);
									addRowCell(dataRow_med, indexCol + 2, "", cellStyle_left);
									indexCol = indexCol + 3;
								}
								// 醫護去年同期時段申報點數
								if (isLastY) {
									medicalOrderSheet.addMergedRegionUnsafe(
											new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
									if (!medDot_Y.toString().equals("")) {
										addRowCell(dataRow_med, indexCol,
												addThousandths(Long.parseLong(medDot_Y.toString())), cellStyle_left);
									} else {
										addRowCell(dataRow_med, indexCol, "0", cellStyle_left);
									}
									addRowCell(dataRow_med, indexCol + 1, "", cellStyle_left);
									addRowCell(dataRow_med, indexCol + 2, "", cellStyle_left);
									indexCol = indexCol + 3;
								}
							}

							// 醫護的自費金額
							if (feeApplyList.contains("自費")) {
								medicalOrderSheet.addMergedRegionUnsafe(
										new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
								if (!medExpense.toString().equals("")) {
									addRowCell(dataRow_med, indexCol,
											addThousandths(Long.parseLong(medExpense.toString())), cellStyle_left);
								} else {
									addRowCell(dataRow_med, indexCol, "0", cellStyle_left);
								}
								addRowCell(dataRow_med, indexCol + 1, "", cellStyle_left);
								addRowCell(dataRow_med, indexCol + 2, "", cellStyle_left);
								indexCol = indexCol + 3;

								// 醫護上個月同條件的自費金額
								if (isLastM) {
									medicalOrderSheet.addMergedRegionUnsafe(
											new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
									if (!medExpense_M.toString().equals("")) {
										addRowCell(dataRow_med, indexCol,
												addThousandths(Long.parseLong(medExpense_M.toString())),
												cellStyle_left);
									} else {
										addRowCell(dataRow_med, indexCol, "0", cellStyle_left);
									}
									addRowCell(dataRow_med, indexCol + 1, "", cellStyle_left);
									addRowCell(dataRow_med, indexCol + 2, "", cellStyle_left);
									indexCol = indexCol + 3;
								}
								// 醫護去年同期時段自費金額
								if (isLastY) {
									medicalOrderSheet.addMergedRegionUnsafe(
											new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
									if (!medExpense_Y.toString().equals("")) {
										addRowCell(dataRow_med, indexCol,
												addThousandths(Long.parseLong(medExpense_Y.toString())),
												cellStyle_left);
									} else {
										addRowCell(dataRow_med, indexCol, "0", cellStyle_left);
									}
									addRowCell(dataRow_med, indexCol + 1, "", cellStyle_left);
									addRowCell(dataRow_med, indexCol + 2, "", cellStyle_left);
									indexCol = indexCol + 3;
								}
							}

							indexRow++;

							indexCol = 4;
						}
					}
				}
			}
		} else {

			for (int i = 0; i < classData.size(); i++) {

				if (funcTypeList.contains(classData.get(i).get("DESC_CHI").toString())) {

					StringBuffer desc_chi = new StringBuffer("");
					StringBuffer quantity = new StringBuffer("");
					StringBuffer dot = new StringBuffer("");
					StringBuffer expense = new StringBuffer("");

					StringBuffer desc_chi_Y = new StringBuffer("");
					StringBuffer quantity_Y = new StringBuffer("");
					StringBuffer dot_Y = new StringBuffer("");
					StringBuffer expense_Y = new StringBuffer("");

					StringBuffer desc_chi_M = new StringBuffer("");
					StringBuffer quantity_M = new StringBuffer("");
					StringBuffer dot_M = new StringBuffer("");
					StringBuffer expense_M = new StringBuffer("");

					desc_chi = new StringBuffer(classData.get(i).get("DESC_CHI").toString());
					quantity = new StringBuffer(classData.get(i).get("QUANTITY").toString());
					dot = new StringBuffer(classData.get(i).get("DOT").toString());
					expense = new StringBuffer(classData.get(i).get("EXPENSE").toString());

					if (classData_Y != null) {
						desc_chi_Y = new StringBuffer(classData_Y.get(i).get("DESC_CHI").toString());
						quantity_Y = new StringBuffer(classData_Y.get(i).get("QUANTITY").toString());
						dot_Y = new StringBuffer(classData_Y.get(i).get("DOT").toString());
						expense_Y = new StringBuffer(classData_Y.get(i).get("EXPENSE").toString());
					}

					if (classData_M != null) {
						desc_chi_M = new StringBuffer(classData_M.get(i).get("DESC_CHI").toString());
						quantity_M = new StringBuffer(classData_M.get(i).get("QUANTITY").toString());
						dot_M = new StringBuffer(classData_M.get(i).get("DOT").toString());
						expense_M = new StringBuffer(classData_M.get(i).get("EXPENSE").toString());
					}

					indexCol = 4;

					// 科名
					HSSFRow dataRow_class = medicalOrderSheet.createRow(indexRow);
					medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, 0, 3));
					addRowCell(dataRow_class, 0, desc_chi.toString(), cellStyle_left);
					addRowCell(dataRow_class, 1, "", cellStyle_left);
					addRowCell(dataRow_class, 2, "", cellStyle_left);
					addRowCell(dataRow_class, 3, "", cellStyle_left);

					// 科名的案件數
					medicalOrderSheet
							.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
					if (classData.get(i) != null) {
						addRowCell(dataRow_class, indexCol, addThousandths(Long.parseLong(quantity.toString())),
								cellStyle_left);
					} else {
						addRowCell(dataRow_class, indexCol, "0", cellStyle_left);
					}
					addRowCell(dataRow_class, indexCol + 1, "", cellStyle_left);
					addRowCell(dataRow_class, indexCol + 2, "", cellStyle_left);
					indexCol = indexCol + 3;

					// 科名上個月同條件的案件數
					if (isLastM) {
						medicalOrderSheet.addMergedRegionUnsafe(
								new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
						if (classData_M != null) {
							addRowCell(dataRow_class, indexCol, addThousandths(Long.parseLong(quantity_M.toString())),
									cellStyle_left);
						} else {
							addRowCell(dataRow_class, indexCol, "0", cellStyle_left);
						}
						addRowCell(dataRow_class, indexCol + 1, "", cellStyle_left);
						addRowCell(dataRow_class, indexCol + 2, "", cellStyle_left);
						indexCol = indexCol + 3;
					}
					// 科名去年同期時段案件數
					if (isLastY) {
						medicalOrderSheet.addMergedRegionUnsafe(
								new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
						if (classData_Y != null) {
							addRowCell(dataRow_class, indexCol, addThousandths(Long.parseLong(quantity_Y.toString())),
									cellStyle_left);
						} else {
							addRowCell(dataRow_class, indexCol, "0", cellStyle_left);
						}
						addRowCell(dataRow_class, indexCol + 1, "", cellStyle_left);
						addRowCell(dataRow_class, indexCol + 2, "", cellStyle_left);
						indexCol = indexCol + 3;
					}

					// 科名的申報點數
					if (feeApplyList.contains("健保")) {
						medicalOrderSheet.addMergedRegionUnsafe(
								new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
						if (classData.get(i) != null) {
							addRowCell(dataRow_class, indexCol, addThousandths(Long.parseLong(dot.toString())),
									cellStyle_left);
						} else {
							addRowCell(dataRow_class, indexCol, "0", cellStyle_left);
						}
						addRowCell(dataRow_class, indexCol + 1, "", cellStyle_left);
						addRowCell(dataRow_class, indexCol + 2, "", cellStyle_left);
						indexCol = indexCol + 3;

						// 科名上個月同條件的申報點數
						if (isLastM) {
							medicalOrderSheet.addMergedRegionUnsafe(
									new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
							if (classData_M != null) {
								addRowCell(dataRow_class, indexCol, addThousandths(Long.parseLong(dot_M.toString())),
										cellStyle_left);
							} else {
								addRowCell(dataRow_class, indexCol, "0", cellStyle_left);
							}
							addRowCell(dataRow_class, indexCol + 1, "", cellStyle_left);
							addRowCell(dataRow_class, indexCol + 2, "", cellStyle_left);
							indexCol = indexCol + 3;
						}
						// 科名去年同期時段申報點數
						if (isLastY) {
							medicalOrderSheet.addMergedRegionUnsafe(
									new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
							if (classData_Y != null) {
								addRowCell(dataRow_class, indexCol, addThousandths(Long.parseLong(dot_Y.toString())),
										cellStyle_left);
							} else {
								addRowCell(dataRow_class, indexCol, "0", cellStyle_left);
							}
							addRowCell(dataRow_class, indexCol + 1, "", cellStyle_left);
							addRowCell(dataRow_class, indexCol + 2, "", cellStyle_left);
							indexCol = indexCol + 3;
						}
					}

					// 科名的自費金額
					if (feeApplyList.contains("自費")) {
						medicalOrderSheet.addMergedRegionUnsafe(
								new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
						if (classData.get(i) != null) {
							addRowCell(dataRow_class, indexCol, addThousandths(Long.parseLong(expense.toString())),
									cellStyle_left);
						} else {
							addRowCell(dataRow_class, indexCol, "0", cellStyle_left);
						}
						addRowCell(dataRow_class, indexCol + 1, "", cellStyle_left);
						addRowCell(dataRow_class, indexCol + 2, "", cellStyle_left);
						indexCol = indexCol + 3;

						// 科名上個月同條件的自費金額
						if (isLastM) {
							medicalOrderSheet.addMergedRegionUnsafe(
									new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
							if (classData_M != null) {
								addRowCell(dataRow_class, indexCol,
										addThousandths(Long.parseLong(expense_M.toString())), cellStyle_left);
							} else {
								addRowCell(dataRow_class, indexCol, "0", cellStyle_left);
							}
							addRowCell(dataRow_class, indexCol + 1, "", cellStyle_left);
							addRowCell(dataRow_class, indexCol + 2, "", cellStyle_left);
							indexCol = indexCol + 3;
						}
						// 科名去年同期時段自費金額
						if (isLastY) {
							medicalOrderSheet.addMergedRegionUnsafe(
									new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
							if (classData_Y != null) {
								addRowCell(dataRow_class, indexCol,
										addThousandths(Long.parseLong(expense_Y.toString())), cellStyle_left);
							} else {
								addRowCell(dataRow_class, indexCol, "0", cellStyle_left);
							}
							addRowCell(dataRow_class, indexCol + 1, "", cellStyle_left);
							addRowCell(dataRow_class, indexCol + 2, "", cellStyle_left);
							indexCol = indexCol + 3;
						}
					}

					indexRow++;

					indexCol = 4;

					// 科別之下醫護人員的案件數、申報點數、自費金額
					if (medDataList.size() > 0) {

						// 醫護名title
						HSSFRow titleRow_med = medicalOrderSheet.createRow(indexRow);
						medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, 0, 3));
						addRowCell(titleRow_med, 0, "醫護名", cellStyle);
						addRowCell(titleRow_med, 1, "", cellStyle_left);
						addRowCell(titleRow_med, 2, "", cellStyle_left);
						addRowCell(titleRow_med, 3, "", cellStyle_left);

						indexCol = 4;

						// 醫護案件數title
						titleName = new StringBuffer("");
//						titleName.append(dataFormat);
//						titleName.append("總案件數");
						medicalOrderSheet.addMergedRegionUnsafe(
								new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
						addRowCell(titleRow_med, indexCol, titleName.toString(), cellStyle_left);
						addRowCell(titleRow_med, indexCol + 1, "", cellStyle_left);
						addRowCell(titleRow_med, indexCol + 2, "", cellStyle_left);
						indexCol = indexCol + 3;

						// 醫護上個月同條件案件數title
						if (isLastM) {
							titleName = new StringBuffer("");
//							titleName.append("上個月同條件");
							medicalOrderSheet.addMergedRegionUnsafe(
									new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
							addRowCell(titleRow_med, indexCol, titleName.toString(), cellStyle_left);
							addRowCell(titleRow_med, indexCol + 1, "", cellStyle_left);
							addRowCell(titleRow_med, indexCol + 2, "", cellStyle_left);
							indexCol = indexCol + 3;
						}
						// 醫護去年同期時段案件數title
						if (isLastY) {
							titleName = new StringBuffer("");
//							titleName.append("去年同期時段");
							medicalOrderSheet.addMergedRegionUnsafe(
									new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
							addRowCell(titleRow_med, indexCol, titleName.toString(), cellStyle_left);
							addRowCell(titleRow_med, indexCol + 1, "", cellStyle_left);
							addRowCell(titleRow_med, indexCol + 2, "", cellStyle_left);
							indexCol = indexCol + 3;
						}

						// 醫護申報點數title
						if (feeApplyList.contains("健保")) {
							titleName = new StringBuffer("");
//							titleName.append(dataFormat);
//							titleName.append("申報總點數");
							medicalOrderSheet.addMergedRegionUnsafe(
									new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
							addRowCell(titleRow_med, indexCol, titleName.toString(), cellStyle_left);
							addRowCell(titleRow_med, indexCol + 1, "", cellStyle_left);
							addRowCell(titleRow_med, indexCol + 2, "", cellStyle_left);
							indexCol = indexCol + 3;

							// 醫護上個月同條件申報點數title
							if (isLastM) {
								titleName = new StringBuffer("");
//								titleName.append("上個月同條件");
								medicalOrderSheet.addMergedRegionUnsafe(
										new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
								addRowCell(titleRow_med, indexCol, titleName.toString(), cellStyle_left);
								addRowCell(titleRow_med, indexCol + 1, "", cellStyle_left);
								addRowCell(titleRow_med, indexCol + 2, "", cellStyle_left);
								indexCol = indexCol + 3;
							}
							// 醫護去年同期時段申報點數title
							if (isLastY) {
								titleName = new StringBuffer("");
//								titleName.append("去年同期時段");
								medicalOrderSheet.addMergedRegionUnsafe(
										new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
								addRowCell(titleRow_med, indexCol, titleName.toString(), cellStyle_left);
								addRowCell(titleRow_med, indexCol + 1, "", cellStyle_left);
								addRowCell(titleRow_med, indexCol + 2, "", cellStyle_left);
								indexCol = indexCol + 3;
							}
						}

						// 醫護自費金額title
						if (feeApplyList.contains("自費")) {
							titleName = new StringBuffer("");
//							titleName.append(dataFormat);
//							titleName.append("自費總金額");
							medicalOrderSheet.addMergedRegionUnsafe(
									new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
							addRowCell(titleRow_med, indexCol, titleName.toString(), cellStyle_left);
							addRowCell(titleRow_med, indexCol + 1, "", cellStyle_left);
							addRowCell(titleRow_med, indexCol + 2, "", cellStyle_left);
							indexCol = indexCol + 3;

							// 醫護上個月同條件自費金額title
							if (isLastM) {
								titleName = new StringBuffer("");
//								titleName.append("上個月同條件");
								medicalOrderSheet.addMergedRegionUnsafe(
										new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
								addRowCell(titleRow_med, indexCol, titleName.toString(), cellStyle_left);
								addRowCell(titleRow_med, indexCol + 1, "", cellStyle_left);
								addRowCell(titleRow_med, indexCol + 2, "", cellStyle_left);
								indexCol = indexCol + 3;
							}
							// 醫護去年同期時段自費金額title
							if (isLastY) {
								titleName = new StringBuffer("");
//								titleName.append("去年同期時段");
								medicalOrderSheet.addMergedRegionUnsafe(
										new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
								addRowCell(titleRow_med, indexCol, titleName.toString(), cellStyle_left);
								addRowCell(titleRow_med, indexCol + 1, "", cellStyle_left);
								addRowCell(titleRow_med, indexCol + 2, "", cellStyle_left);
								indexCol = indexCol + 3;
							}
						}

						indexRow++;

						Map<String, Object> medData_lastY = new HashedMap<String, Object>();
						Map<String, Object> medData_lastM = new HashedMap<String, Object>();
						Map<String, Object> medData = new HashedMap<String, Object>();

						for (int j = 0; j < medDataList.size(); j++) {

							StringBuffer medDate = new StringBuffer(medDataList.get(j).get("date").toString());
							StringBuffer medName = new StringBuffer(medDataList.get(j).get("medName").toString());
							StringBuffer medDataF = new StringBuffer(medDataList.get(j).get("dataFormat").toString());

							if ((!lastDateStr.contains(medDate.toString().replaceAll("\\-", ""))
									|| yearMonth.contains(medDate.toString())) && date.equals(medDate.toString())
									&& dataFormat.equals(medDataF.toString())) {

//								    StringBuffer medDesc_chi=new StringBuffer("0");
								StringBuffer medQuantity = new StringBuffer("0");
								StringBuffer medDot = new StringBuffer("0");
								StringBuffer medExpense = new StringBuffer("0");

								StringBuffer medQuantity_Y = new StringBuffer("0");
								StringBuffer medDot_Y = new StringBuffer("0");
								StringBuffer medExpense_Y = new StringBuffer("0");

								StringBuffer medQuantity_M = new StringBuffer("0");
								StringBuffer medDot_M = new StringBuffer("0");
								StringBuffer medExpense_M = new StringBuffer("0");

								List<Map<String, Object>> md = (List<Map<String, Object>>) medDataList.get(j)
										.get("data");

								for (int k = 0; k < md.size(); k++) {
									if (md.get(k).get("DESC_CHI").equals(desc_chi.toString())) {
										medData = (Map<String, Object>) md.get(k);
										break;
									}
								}

								if (medDataList_Y.size() > 0 && isLastY) {
									medData_lastY = getLastYearMedData(medDate.toString(), medDataF.toString(),
											medName.toString(), desc_chi.toString(), medDataList_Y);
								}

								if (medDataList_M.size() > 0 && isLastM) {
									medData_lastM = getLastMonthMedData(medDate.toString(), medDataF.toString(),
											medName.toString(), desc_chi.toString(), medDataList_M);
								}

								if (medData != null && !medData.isEmpty()) {
									medQuantity = new StringBuffer(medData.get("QUANTITY").toString());
									medDot = new StringBuffer(medData.get("DOT").toString());
									medExpense = new StringBuffer(medData.get("EXPENSE").toString());
								}

								if (medData_lastY != null && !medData_lastY.isEmpty()) {
									medQuantity_Y = new StringBuffer(medData_lastY.get("QUANTITY").toString());
									medDot_Y = new StringBuffer(medData_lastY.get("DOT").toString());
									medExpense_Y = new StringBuffer(medData_lastY.get("EXPENSE").toString());
								}

								if (medData_lastM != null && !medData_lastM.isEmpty()) {
									medQuantity_M = new StringBuffer(medData_lastM.get("QUANTITY").toString());
									medDot_M = new StringBuffer(medData_lastM.get("DOT").toString());
									medExpense_M = new StringBuffer(medData_lastM.get("EXPENSE").toString());
								}

								indexCol = 4;

								// 醫護名
								HSSFRow dataRow_med = medicalOrderSheet.createRow(indexRow);
								medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, 0, 3));
								addRowCell(dataRow_med, 0, medName.toString(), cellStyle_left);
								addRowCell(dataRow_med, 1, "", cellStyle_left);
								addRowCell(dataRow_med, 2, "", cellStyle_left);
								addRowCell(dataRow_med, 3, "", cellStyle_left);

								// 醫護的案件數
								medicalOrderSheet.addMergedRegionUnsafe(
										new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
								if (!medQuantity.toString().equals("")) {
									addRowCell(dataRow_med, indexCol,
											addThousandths(Long.parseLong(medQuantity.toString())), cellStyle_left);
								} else {
									addRowCell(dataRow_med, indexCol, "0", cellStyle_left);
								}
								addRowCell(dataRow_med, indexCol + 1, "", cellStyle_left);
								addRowCell(dataRow_med, indexCol + 2, "", cellStyle_left);
								indexCol = indexCol + 3;

								// 醫護上個月同條件的案件數
								if (isLastM) {
									medicalOrderSheet.addMergedRegionUnsafe(
											new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
									if (!medQuantity_M.toString().equals("")) {
										addRowCell(dataRow_med, indexCol,
												addThousandths(Long.parseLong(medQuantity_M.toString())),
												cellStyle_left);
									} else {
										addRowCell(dataRow_med, indexCol, "0", cellStyle_left);
									}
									addRowCell(dataRow_med, indexCol + 1, "", cellStyle_left);
									addRowCell(dataRow_med, indexCol + 2, "", cellStyle_left);
									indexCol = indexCol + 3;
								}
								// 醫護去年同期時段案件數
								if (isLastY) {
									medicalOrderSheet.addMergedRegionUnsafe(
											new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
									if (!medQuantity_Y.toString().equals("")) {
										addRowCell(dataRow_med, indexCol,
												addThousandths(Long.parseLong(medQuantity_Y.toString())),
												cellStyle_left);
									} else {
										addRowCell(dataRow_med, indexCol, "0", cellStyle_left);
									}
									addRowCell(dataRow_med, indexCol + 1, "", cellStyle_left);
									addRowCell(dataRow_med, indexCol + 2, "", cellStyle_left);
									indexCol = indexCol + 3;
								}

								// 醫護的申報點數
								if (feeApplyList.contains("健保")) {
									medicalOrderSheet.addMergedRegionUnsafe(
											new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
									if (!medDot.toString().equals("")) {
										addRowCell(dataRow_med, indexCol,
												addThousandths(Long.parseLong(medDot.toString())), cellStyle_left);
									} else {
										addRowCell(dataRow_med, indexCol, "0", cellStyle_left);
									}
									addRowCell(dataRow_med, indexCol + 1, "", cellStyle_left);
									addRowCell(dataRow_med, indexCol + 2, "", cellStyle_left);
									indexCol = indexCol + 3;

									// 醫護上個月同條件的申報點數
									if (isLastM) {
										medicalOrderSheet.addMergedRegionUnsafe(
												new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
										if (!medDot_M.toString().equals("")) {
											addRowCell(dataRow_med, indexCol,
													addThousandths(Long.parseLong(medDot_M.toString())),
													cellStyle_left);
										} else {
											addRowCell(dataRow_med, indexCol, "0", cellStyle_left);
										}
										addRowCell(dataRow_med, indexCol + 1, "", cellStyle_left);
										addRowCell(dataRow_med, indexCol + 2, "", cellStyle_left);
										indexCol = indexCol + 3;
									}
									// 醫護去年同期時段申報點數
									if (isLastY) {
										medicalOrderSheet.addMergedRegionUnsafe(
												new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
										if (!medDot_Y.toString().equals("")) {
											addRowCell(dataRow_med, indexCol,
													addThousandths(Long.parseLong(medDot_Y.toString())),
													cellStyle_left);
										} else {
											addRowCell(dataRow_med, indexCol, "0", cellStyle_left);
										}
										addRowCell(dataRow_med, indexCol + 1, "", cellStyle_left);
										addRowCell(dataRow_med, indexCol + 2, "", cellStyle_left);
										indexCol = indexCol + 3;
									}
								}

								// 醫護的自費金額
								if (feeApplyList.contains("自費")) {
									medicalOrderSheet.addMergedRegionUnsafe(
											new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
									if (!medExpense.toString().equals("")) {
										addRowCell(dataRow_med, indexCol,
												addThousandths(Long.parseLong(medExpense.toString())), cellStyle_left);
									} else {
										addRowCell(dataRow_med, indexCol, "0", cellStyle_left);
									}
									addRowCell(dataRow_med, indexCol + 1, "", cellStyle_left);
									addRowCell(dataRow_med, indexCol + 2, "", cellStyle_left);
									indexCol = indexCol + 3;

									// 醫護上個月同條件的自費金額
									if (isLastM) {
										medicalOrderSheet.addMergedRegionUnsafe(
												new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
										if (!medExpense_M.toString().equals("")) {
											addRowCell(dataRow_med, indexCol,
													addThousandths(Long.parseLong(medExpense_M.toString())),
													cellStyle_left);
										} else {
											addRowCell(dataRow_med, indexCol, "0", cellStyle_left);
										}
										addRowCell(dataRow_med, indexCol + 1, "", cellStyle_left);
										addRowCell(dataRow_med, indexCol + 2, "", cellStyle_left);
										indexCol = indexCol + 3;
									}
									// 醫護去年同期時段自費金額
									if (isLastY) {
										medicalOrderSheet.addMergedRegionUnsafe(
												new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
										if (!medExpense_Y.toString().equals("")) {
											addRowCell(dataRow_med, indexCol,
													addThousandths(Long.parseLong(medExpense_Y.toString())),
													cellStyle_left);
										} else {
											addRowCell(dataRow_med, indexCol, "0", cellStyle_left);
										}
										addRowCell(dataRow_med, indexCol + 1, "", cellStyle_left);
										addRowCell(dataRow_med, indexCol + 2, "", cellStyle_left);
										indexCol = indexCol + 3;
									}
								}

								indexRow++;

								indexCol = 4;
							}
						}
					}
				}
			}
		}

		indexRow++;

		return indexRow;
	}

	@SuppressWarnings("unchecked")
	public void medicalOrderTemplate_dateType1(HSSFSheet medicalOrderSheet, List<Map<String, Object>> dataList,
			List<Map<String, Object>> dataList_lastY, List<Map<String, Object>> classDataList,
			List<Map<String, Object>> classDataList_lastY, List<Map<String, Object>> medDataList,
			List<Map<String, Object>> medDataList_lastY, List<String> lastSDateStr, List<String> lastEDateStr,
			List<String> feeApplyList, List<String> funcTypeList, boolean isLastY, HSSFCellStyle cellStyle,
			HSSFCellStyle cellStyle_left) {

		int indexRow = 6;

		List<Map<String, Object>> data_Y = null;
		List<Map<String, Object>> classData_Y = null;

		for (int i = 0; i < dataList.size(); i++) {

			StringBuffer sDate = new StringBuffer(dataList.get(i).get("startDate").toString());
			StringBuffer eDate = new StringBuffer(dataList.get(i).get("endDate").toString());
			StringBuffer dataFormat = new StringBuffer(dataList.get(i).get("dataFormat").toString());

			List<Map<String, Object>> data = (List<Map<String, Object>>) dataList.get(i).get("data");
			List<Map<String, Object>> classData = (List<Map<String, Object>>) classDataList.get(i).get("classData");

			if (!lastSDateStr.contains(sDate.toString()) && !lastEDateStr.contains(eDate.toString())) {

				if (dataList_lastY.size() > 0 && isLastY) {
					data_Y = getLastYearData(false, sDate.toString(), eDate.toString(), dataFormat.toString(),
							dataList_lastY);
				}

				if (classDataList_lastY.size() > 0 && isLastY) {
					classData_Y = getLastYearData(true, sDate.toString(), eDate.toString(), dataFormat.toString(),
							classDataList_lastY);
				}

				switch (dataFormat.toString()) {
				case "不分區":
					indexRow = addRowCellByMedicalOrder_dateType1(medicalOrderSheet, "ALL", indexRow, sDate.toString(),
							eDate.toString(), dataFormat.toString(), data, data_Y, classData, classData_Y, medDataList,
							medDataList_lastY, lastSDateStr, lastEDateStr, feeApplyList, funcTypeList, isLastY,
							cellStyle, cellStyle_left);
					break;
				case "門急診":
					indexRow = addRowCellByMedicalOrder_dateType1(medicalOrderSheet, "OPALL", indexRow,
							sDate.toString(), eDate.toString(), dataFormat.toString(), data, data_Y, classData,
							classData_Y, medDataList, medDataList_lastY, lastSDateStr, lastEDateStr, feeApplyList,
							funcTypeList, isLastY, cellStyle, cellStyle_left);
					break;
				case "門診":
					indexRow = addRowCellByMedicalOrder_dateType1(medicalOrderSheet, "OP", indexRow, sDate.toString(),
							eDate.toString(), dataFormat.toString(), data, data_Y, classData, classData_Y, medDataList,
							medDataList_lastY, lastSDateStr, lastEDateStr, feeApplyList, funcTypeList, isLastY,
							cellStyle, cellStyle_left);
					break;
				case "急診":
					indexRow = addRowCellByMedicalOrder_dateType1(medicalOrderSheet, "EM", indexRow, sDate.toString(),
							eDate.toString(), dataFormat.toString(), data, data_Y, classData, classData_Y, medDataList,
							medDataList_lastY, lastSDateStr, lastEDateStr, feeApplyList, funcTypeList, isLastY,
							cellStyle, cellStyle_left);
					break;
				case "住院":
					indexRow = addRowCellByMedicalOrder_dateType1(medicalOrderSheet, "IP", indexRow, sDate.toString(),
							eDate.toString(), dataFormat.toString(), data, data_Y, classData, classData_Y, medDataList,
							medDataList_lastY, lastSDateStr, lastEDateStr, feeApplyList, funcTypeList, isLastY,
							cellStyle, cellStyle_left);
					break;
				default:
					break;
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public int addRowCellByMedicalOrder_dateType1(HSSFSheet medicalOrderSheet, String dataFormatCode, int indexRow,
			String sDate, String eDate, String dataFormat, List<Map<String, Object>> data,
			List<Map<String, Object>> data_Y, List<Map<String, Object>> classData,
			List<Map<String, Object>> classData_Y, List<Map<String, Object>> medDataList,
			List<Map<String, Object>> medDataList_Y, List<String> lastSDateStr, List<String> lastEDateStr,
			List<String> feeApplyList, List<String> funcTypeList, boolean isLastY, HSSFCellStyle cellStyle,
			HSSFCellStyle cellStyle_left) {

		StringBuffer titleName = new StringBuffer();

		// 起訖日期title
		HSSFRow titleRow = medicalOrderSheet.createRow(indexRow);
		medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, 0, 1));
		medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, 2, 3));
		addRowCell(titleRow, 0, "就醫日期-起", cellStyle_left);
		addRowCell(titleRow, 1, "", cellStyle_left);
		addRowCell(titleRow, 2, "就醫日期-訖", cellStyle_left);
		addRowCell(titleRow, 3, "", cellStyle_left);

		int indexCol = 4;

		// 案件數title
		titleName = new StringBuffer("");
		titleName.append(dataFormat);
		titleName.append("總案件數");
		medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
		addRowCell(titleRow, indexCol, titleName.toString(), cellStyle_left);
		addRowCell(titleRow, indexCol + 1, "", cellStyle_left);
		addRowCell(titleRow, indexCol + 2, "", cellStyle_left);
		indexCol = indexCol + 3;

		// 去年同期時段案件數title
		if (isLastY) {
			titleName = new StringBuffer("");
			titleName.append("去年同期時段");
			medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
			addRowCell(titleRow, indexCol, titleName.toString(), cellStyle_left);
			addRowCell(titleRow, indexCol + 1, "", cellStyle_left);
			addRowCell(titleRow, indexCol + 2, "", cellStyle_left);
			indexCol = indexCol + 3;
		}

		// 申報點數title
		if (feeApplyList.contains("健保")) {
			titleName = new StringBuffer("");
			titleName.append(dataFormat);
			titleName.append("申報總點數");
			medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
			addRowCell(titleRow, indexCol, titleName.toString(), cellStyle_left);
			addRowCell(titleRow, indexCol + 1, "", cellStyle_left);
			addRowCell(titleRow, indexCol + 2, "", cellStyle_left);
			indexCol = indexCol + 3;

			// 去年同期時段申報點數title
			if (isLastY) {
				titleName = new StringBuffer("");
				titleName.append("去年同期時段");
				medicalOrderSheet
						.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
				addRowCell(titleRow, indexCol, titleName.toString(), cellStyle_left);
				addRowCell(titleRow, indexCol + 1, "", cellStyle_left);
				addRowCell(titleRow, indexCol + 2, "", cellStyle_left);
				indexCol = indexCol + 3;
			}
		}

		// 自費金額title
		if (feeApplyList.contains("自費")) {
			titleName = new StringBuffer("");
			titleName.append(dataFormat);
			titleName.append("自費總金額");
			medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
			addRowCell(titleRow, indexCol, titleName.toString(), cellStyle_left);
			addRowCell(titleRow, indexCol + 1, "", cellStyle_left);
			addRowCell(titleRow, indexCol + 2, "", cellStyle_left);
			indexCol = indexCol + 3;

			// 去年同期時段自費金額title
			if (isLastY) {
				titleName = new StringBuffer("");
				titleName.append("去年同期時段");
				medicalOrderSheet
						.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
				addRowCell(titleRow, indexCol, titleName.toString(), cellStyle_left);
				addRowCell(titleRow, indexCol + 1, "", cellStyle_left);
				addRowCell(titleRow, indexCol + 2, "", cellStyle_left);
				indexCol = indexCol + 3;
			}
		}

		indexRow++;

		indexCol = 4;

		// 起訖日期
		HSSFRow dataRow = medicalOrderSheet.createRow(indexRow);
		medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, 0, 1));
		medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, 2, 3));
		addRowCell(dataRow, 0, sDate, cellStyle_left);
		addRowCell(dataRow, 1, "", cellStyle_left);
		addRowCell(dataRow, 2, eDate, cellStyle_left);
		addRowCell(dataRow, 3, "", cellStyle_left);

		// 起訖日期之間的案件數
		medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
		if (data != null) {
			addRowCell(dataRow, indexCol,
					addThousandths(Long.parseLong(data.get(0).get(dataFormatCode + "_QUANTITY").toString())),
					cellStyle_left);
		} else {
			addRowCell(dataRow, indexCol, "0", cellStyle_left);
		}
		addRowCell(dataRow, indexCol + 1, "", cellStyle_left);
		addRowCell(dataRow, indexCol + 2, "", cellStyle_left);
		indexCol = indexCol + 3;

		// 去年同期時段案件數
		if (isLastY) {
			medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
			if (data_Y != null) {
				addRowCell(dataRow, indexCol,
						addThousandths(Long.parseLong(data_Y.get(0).get(dataFormatCode + "_QUANTITY").toString())),
						cellStyle_left);
			} else {
				addRowCell(dataRow, indexCol, "0", cellStyle_left);
			}
			addRowCell(dataRow, indexCol + 1, "", cellStyle_left);
			addRowCell(dataRow, indexCol + 2, "", cellStyle_left);
			indexCol = indexCol + 3;
		}

		// 起訖日期之間的申報點數
		if (feeApplyList.contains("健保")) {
			medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
			if (data != null) {
				addRowCell(dataRow, indexCol,
						addThousandths(Long.parseLong(data.get(0).get(dataFormatCode + "_DOT").toString())),
						cellStyle_left);
			} else {
				addRowCell(dataRow, indexCol, "0", cellStyle_left);
			}
			addRowCell(dataRow, indexCol + 1, "", cellStyle_left);
			addRowCell(dataRow, indexCol + 2, "", cellStyle_left);
			indexCol = indexCol + 3;

			// 去年同期時段申報點數
			if (isLastY) {
				medicalOrderSheet
						.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
				if (data_Y != null) {
					addRowCell(dataRow, indexCol,
							addThousandths(Long.parseLong(data_Y.get(0).get(dataFormatCode + "_DOT").toString())),
							cellStyle_left);
				} else {
					addRowCell(dataRow, indexCol, "0", cellStyle_left);
				}
				addRowCell(dataRow, indexCol + 1, "", cellStyle_left);
				addRowCell(dataRow, indexCol + 2, "", cellStyle_left);
				indexCol = indexCol + 3;
			}
		}

		// 起訖日期之間的自費金額
		if (feeApplyList.contains("自費")) {
			medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
			if (data != null) {
				addRowCell(dataRow, indexCol,
						addThousandths(Long.parseLong(data.get(0).get(dataFormatCode + "_EXPENSE").toString())),
						cellStyle_left);
			} else {
				addRowCell(dataRow, indexCol, "0", cellStyle_left);
			}
			addRowCell(dataRow, indexCol + 1, "", cellStyle_left);
			addRowCell(dataRow, indexCol + 2, "", cellStyle_left);
			indexCol = indexCol + 3;

			// 去年同期時段自費金額
			if (isLastY) {
				medicalOrderSheet
						.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
				if (data_Y != null) {
					addRowCell(dataRow, indexCol,
							addThousandths(Long.parseLong(data_Y.get(0).get(dataFormatCode + "_EXPENSE").toString())),
							cellStyle_left);
				} else {
					addRowCell(dataRow, indexCol, "0", cellStyle_left);
				}
				addRowCell(dataRow, indexCol + 1, "", cellStyle_left);
				addRowCell(dataRow, indexCol + 2, "", cellStyle_left);
				indexCol = indexCol + 3;
			}
		}

		indexRow++;

		// 科名title
		HSSFRow titleRow_class = medicalOrderSheet.createRow(indexRow);
		medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, 0, 3));
		addRowCell(titleRow_class, 0, "科名", cellStyle);
		addRowCell(titleRow_class, 1, "", cellStyle_left);
		addRowCell(titleRow_class, 2, "", cellStyle_left);
		addRowCell(titleRow_class, 3, "", cellStyle_left);

		indexCol = 4;

		// 科名案件數title
		titleName = new StringBuffer("");
//		titleName.append(dataFormat);
//		titleName.append("總案件數");
		medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
		addRowCell(titleRow_class, indexCol, titleName.toString(), cellStyle_left);
		addRowCell(titleRow_class, indexCol + 1, "", cellStyle_left);
		addRowCell(titleRow_class, indexCol + 2, "", cellStyle_left);
		indexCol = indexCol + 3;

		// 科名去年同期時段案件數title
		if (isLastY) {
			titleName = new StringBuffer("");
//			titleName.append("去年同期時段");
			medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
			addRowCell(titleRow_class, indexCol, titleName.toString(), cellStyle_left);
			addRowCell(titleRow_class, indexCol + 1, "", cellStyle_left);
			addRowCell(titleRow_class, indexCol + 2, "", cellStyle_left);
			indexCol = indexCol + 3;
		}

		// 科名申報點數title
		if (feeApplyList.contains("健保")) {
			titleName = new StringBuffer("");
//			titleName.append(dataFormat);
//			titleName.append("申報總點數");
			medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
			addRowCell(titleRow_class, indexCol, titleName.toString(), cellStyle_left);
			addRowCell(titleRow_class, indexCol + 1, "", cellStyle_left);
			addRowCell(titleRow_class, indexCol + 2, "", cellStyle_left);
			indexCol = indexCol + 3;

			// 科名去年同期時段申報點數title
			if (isLastY) {
				titleName = new StringBuffer("");
//				titleName.append("去年同期時段");
				medicalOrderSheet
						.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
				addRowCell(titleRow_class, indexCol, titleName.toString(), cellStyle_left);
				addRowCell(titleRow_class, indexCol + 1, "", cellStyle_left);
				addRowCell(titleRow_class, indexCol + 2, "", cellStyle_left);
				indexCol = indexCol + 3;
			}
		}

		// 科名自費金額title
		if (feeApplyList.contains("自費")) {
			titleName = new StringBuffer("");
//			titleName.append(dataFormat);
//			titleName.append("自費總金額");
			medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
			addRowCell(titleRow_class, indexCol, titleName.toString(), cellStyle_left);
			addRowCell(titleRow_class, indexCol + 1, "", cellStyle_left);
			addRowCell(titleRow_class, indexCol + 2, "", cellStyle_left);
			indexCol = indexCol + 3;

			// 科名去年同期時段自費金額title
			if (isLastY) {
				titleName = new StringBuffer("");
//				titleName.append("去年同期時段");
				medicalOrderSheet
						.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
				addRowCell(titleRow_class, indexCol, titleName.toString(), cellStyle_left);
				addRowCell(titleRow_class, indexCol + 1, "", cellStyle_left);
				addRowCell(titleRow_class, indexCol + 2, "", cellStyle_left);
				indexCol = indexCol + 3;
			}
		}

		indexRow++;

		/*
		 * 各科別案件數、申報點數、自費金額 當科別條件為空，顯示全部科別資料，不為空，顯示指定科別資料
		 */
		if (funcTypeList.size() < 1) {

			for (int i = 0; i < classData.size(); i++) {

				StringBuffer desc_chi = new StringBuffer("");
				StringBuffer quantity = new StringBuffer("");
				StringBuffer dot = new StringBuffer("");
				StringBuffer expense = new StringBuffer("");

				StringBuffer desc_chi_Y = new StringBuffer("");
				StringBuffer quantity_Y = new StringBuffer("");
				StringBuffer dot_Y = new StringBuffer("");
				StringBuffer expense_Y = new StringBuffer("");

				StringBuffer desc_chi_M = new StringBuffer("");
				StringBuffer quantity_M = new StringBuffer("");
				StringBuffer dot_M = new StringBuffer("");
				StringBuffer expense_M = new StringBuffer("");

				desc_chi = new StringBuffer(classData.get(i).get("DESC_CHI").toString());
				quantity = new StringBuffer(classData.get(i).get("QUANTITY").toString());
				dot = new StringBuffer(classData.get(i).get("DOT").toString());
				expense = new StringBuffer(classData.get(i).get("EXPENSE").toString());

				if (classData_Y != null) {
					desc_chi_Y = new StringBuffer(classData_Y.get(i).get("DESC_CHI").toString());
					quantity_Y = new StringBuffer(classData_Y.get(i).get("QUANTITY").toString());
					dot_Y = new StringBuffer(classData_Y.get(i).get("DOT").toString());
					expense_Y = new StringBuffer(classData_Y.get(i).get("EXPENSE").toString());
				}

				indexCol = 4;

				// 科名
				HSSFRow dataRow_class = medicalOrderSheet.createRow(indexRow);
				medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, 0, 3));
				addRowCell(dataRow_class, 0, desc_chi.toString(), cellStyle_left);
				addRowCell(dataRow_class, 1, "", cellStyle_left);
				addRowCell(dataRow_class, 2, "", cellStyle_left);
				addRowCell(dataRow_class, 3, "", cellStyle_left);

				// 科名的案件數
				medicalOrderSheet
						.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
				if (classData.get(i) != null) {
					addRowCell(dataRow_class, indexCol, addThousandths(Long.parseLong(quantity.toString())),
							cellStyle_left);
				} else {
					addRowCell(dataRow_class, indexCol, "0", cellStyle_left);
				}
				addRowCell(dataRow_class, indexCol + 1, "", cellStyle_left);
				addRowCell(dataRow_class, indexCol + 2, "", cellStyle_left);
				indexCol = indexCol + 3;

				// 科名去年同期時段案件數
				if (isLastY) {
					medicalOrderSheet
							.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
					if (classData_Y != null) {
						addRowCell(dataRow_class, indexCol, addThousandths(Long.parseLong(quantity_Y.toString())),
								cellStyle_left);
					} else {
						addRowCell(dataRow_class, indexCol, "0", cellStyle_left);
					}
					addRowCell(dataRow_class, indexCol + 1, "", cellStyle_left);
					addRowCell(dataRow_class, indexCol + 2, "", cellStyle_left);
					indexCol = indexCol + 3;
				}

				// 科名的申報點數
				if (feeApplyList.contains("健保")) {
					medicalOrderSheet
							.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
					if (classData.get(i) != null) {
						addRowCell(dataRow_class, indexCol, addThousandths(Long.parseLong(dot.toString())),
								cellStyle_left);
					} else {
						addRowCell(dataRow_class, indexCol, "0", cellStyle_left);
					}
					addRowCell(dataRow_class, indexCol + 1, "", cellStyle_left);
					addRowCell(dataRow_class, indexCol + 2, "", cellStyle_left);
					indexCol = indexCol + 3;

					// 科名去年同期時段申報點數
					if (isLastY) {
						medicalOrderSheet.addMergedRegionUnsafe(
								new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
						if (classData_Y != null) {
							addRowCell(dataRow_class, indexCol, addThousandths(Long.parseLong(dot_Y.toString())),
									cellStyle_left);
						} else {
							addRowCell(dataRow_class, indexCol, "0", cellStyle_left);
						}
						addRowCell(dataRow_class, indexCol + 1, "", cellStyle_left);
						addRowCell(dataRow_class, indexCol + 2, "", cellStyle_left);
						indexCol = indexCol + 3;
					}
				}

				// 科名的自費金額
				if (feeApplyList.contains("自費")) {
					medicalOrderSheet
							.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
					if (classData.get(i) != null) {
						addRowCell(dataRow_class, indexCol, addThousandths(Long.parseLong(expense.toString())),
								cellStyle_left);
					} else {
						addRowCell(dataRow_class, indexCol, "0", cellStyle_left);
					}
					addRowCell(dataRow_class, indexCol + 1, "", cellStyle_left);
					addRowCell(dataRow_class, indexCol + 2, "", cellStyle_left);
					indexCol = indexCol + 3;

					// 科名去年同期時段自費金額
					if (isLastY) {
						medicalOrderSheet.addMergedRegionUnsafe(
								new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
						if (classData_Y != null) {
							addRowCell(dataRow_class, indexCol, addThousandths(Long.parseLong(expense_Y.toString())),
									cellStyle_left);
						} else {
							addRowCell(dataRow_class, indexCol, "0", cellStyle_left);
						}
						addRowCell(dataRow_class, indexCol + 1, "", cellStyle_left);
						addRowCell(dataRow_class, indexCol + 2, "", cellStyle_left);
						indexCol = indexCol + 3;
					}
				}

				indexRow++;

				indexCol = 4;

				// 科別之下醫護人員的案件數、申報點數、自費金額
				if (medDataList.size() > 0) {

					// 醫護名title
					HSSFRow titleRow_med = medicalOrderSheet.createRow(indexRow);
					medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, 0, 3));
					addRowCell(titleRow_med, 0, "醫護名", cellStyle);
					addRowCell(titleRow_med, 1, "", cellStyle_left);
					addRowCell(titleRow_med, 2, "", cellStyle_left);
					addRowCell(titleRow_med, 3, "", cellStyle_left);

					indexCol = 4;

					// 醫護案件數title
					titleName = new StringBuffer("");
//					titleName.append(dataFormat);
//					titleName.append("總案件數");
					medicalOrderSheet
							.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
					addRowCell(titleRow_med, indexCol, titleName.toString(), cellStyle_left);
					addRowCell(titleRow_med, indexCol + 1, "", cellStyle_left);
					addRowCell(titleRow_med, indexCol + 2, "", cellStyle_left);
					indexCol = indexCol + 3;

					// 醫護去年同期時段案件數title
					if (isLastY) {
						titleName = new StringBuffer("");
//						titleName.append("去年同期時段");
						medicalOrderSheet.addMergedRegionUnsafe(
								new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
						addRowCell(titleRow_med, indexCol, titleName.toString(), cellStyle_left);
						addRowCell(titleRow_med, indexCol + 1, "", cellStyle_left);
						addRowCell(titleRow_med, indexCol + 2, "", cellStyle_left);
						indexCol = indexCol + 3;
					}

					// 醫護申報點數title
					if (feeApplyList.contains("健保")) {
						titleName = new StringBuffer("");
//						titleName.append(dataFormat);
//						titleName.append("申報總點數");
						medicalOrderSheet.addMergedRegionUnsafe(
								new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
						addRowCell(titleRow_med, indexCol, titleName.toString(), cellStyle_left);
						addRowCell(titleRow_med, indexCol + 1, "", cellStyle_left);
						addRowCell(titleRow_med, indexCol + 2, "", cellStyle_left);
						indexCol = indexCol + 3;

						// 醫護去年同期時段申報點數title
						if (isLastY) {
							titleName = new StringBuffer("");
//							titleName.append("去年同期時段");
							medicalOrderSheet.addMergedRegionUnsafe(
									new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
							addRowCell(titleRow_med, indexCol, titleName.toString(), cellStyle_left);
							addRowCell(titleRow_med, indexCol + 1, "", cellStyle_left);
							addRowCell(titleRow_med, indexCol + 2, "", cellStyle_left);
							indexCol = indexCol + 3;
						}
					}

					// 醫護自費金額title
					if (feeApplyList.contains("自費")) {
						titleName = new StringBuffer("");
//						titleName.append(dataFormat);
//						titleName.append("自費總金額");
						medicalOrderSheet.addMergedRegionUnsafe(
								new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
						addRowCell(titleRow_med, indexCol, titleName.toString(), cellStyle_left);
						addRowCell(titleRow_med, indexCol + 1, "", cellStyle_left);
						addRowCell(titleRow_med, indexCol + 2, "", cellStyle_left);
						indexCol = indexCol + 3;

						// 醫護去年同期時段自費金額title
						if (isLastY) {
							titleName = new StringBuffer("");
//							titleName.append("去年同期時段");
							medicalOrderSheet.addMergedRegionUnsafe(
									new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
							addRowCell(titleRow_med, indexCol, titleName.toString(), cellStyle_left);
							addRowCell(titleRow_med, indexCol + 1, "", cellStyle_left);
							addRowCell(titleRow_med, indexCol + 2, "", cellStyle_left);
							indexCol = indexCol + 3;
						}
					}

					indexRow++;

					Map<String, Object> medData_lastY = new HashedMap<String, Object>();
					Map<String, Object> medData = new HashedMap<String, Object>();

					for (int j = 0; j < medDataList.size(); j++) {

						StringBuffer medSDate = new StringBuffer(medDataList.get(j).get("startDate").toString());
						StringBuffer medEDate = new StringBuffer(medDataList.get(j).get("endDate").toString());
						StringBuffer medName = new StringBuffer(medDataList.get(j).get("medName").toString());
						StringBuffer medDataF = new StringBuffer(medDataList.get(j).get("dataFormat").toString());

						if (!lastSDateStr.contains(sDate.toString()) && !lastEDateStr.contains(eDate.toString())
								&& sDate.equals(medSDate.toString()) && eDate.equals(medEDate.toString())
								&& dataFormat.equals(medDataF.toString())) {

//							    StringBuffer medDesc_chi=new StringBuffer("0");
							StringBuffer medQuantity = new StringBuffer("0");
							StringBuffer medDot = new StringBuffer("0");
							StringBuffer medExpense = new StringBuffer("0");

							StringBuffer medQuantity_Y = new StringBuffer("0");
							StringBuffer medDot_Y = new StringBuffer("0");
							StringBuffer medExpense_Y = new StringBuffer("0");

							List<Map<String, Object>> md = (List<Map<String, Object>>) medDataList.get(j).get("data");

							for (int k = 0; k < md.size(); k++) {
								if (md.get(k).get("DESC_CHI").equals(desc_chi.toString())) {
									medData = (Map<String, Object>) md.get(k);
									break;
								}
							}

							if (medDataList_Y.size() > 0 && isLastY) {
								medData_lastY = getLastYearMedData(medSDate.toString(), medEDate.toString(),
										medDataF.toString(), medName.toString(), desc_chi.toString(), medDataList_Y);
							}

							if (medData != null && !medData.isEmpty()) {
								medQuantity = new StringBuffer(medData.get("QUANTITY").toString());
								medDot = new StringBuffer(medData.get("DOT").toString());
								medExpense = new StringBuffer(medData.get("EXPENSE").toString());
							}

							if (medData_lastY != null && !medData_lastY.isEmpty()) {
								medQuantity_Y = new StringBuffer(medData_lastY.get("QUANTITY").toString());
								medDot_Y = new StringBuffer(medData_lastY.get("DOT").toString());
								medExpense_Y = new StringBuffer(medData_lastY.get("EXPENSE").toString());
							}

							indexCol = 4;

							// 醫護名
							HSSFRow dataRow_med = medicalOrderSheet.createRow(indexRow);
							medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, 0, 3));
							addRowCell(dataRow_med, 0, medName.toString(), cellStyle_left);
							addRowCell(dataRow_med, 1, "", cellStyle_left);
							addRowCell(dataRow_med, 2, "", cellStyle_left);
							addRowCell(dataRow_med, 3, "", cellStyle_left);

							// 醫護的案件數
							medicalOrderSheet.addMergedRegionUnsafe(
									new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
							if (!medQuantity.toString().equals("")) {
								addRowCell(dataRow_med, indexCol,
										addThousandths(Long.parseLong(medQuantity.toString())), cellStyle_left);
							} else {
								addRowCell(dataRow_med, indexCol, "0", cellStyle_left);
							}
							addRowCell(dataRow_med, indexCol + 1, "", cellStyle_left);
							addRowCell(dataRow_med, indexCol + 2, "", cellStyle_left);
							indexCol = indexCol + 3;

							// 醫護去年同期時段案件數
							if (isLastY) {
								medicalOrderSheet.addMergedRegionUnsafe(
										new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
								if (!medQuantity_Y.toString().equals("")) {
									addRowCell(dataRow_med, indexCol,
											addThousandths(Long.parseLong(medQuantity_Y.toString())), cellStyle_left);
								} else {
									addRowCell(dataRow_med, indexCol, "0", cellStyle_left);
								}
								addRowCell(dataRow_med, indexCol + 1, "", cellStyle_left);
								addRowCell(dataRow_med, indexCol + 2, "", cellStyle_left);
								indexCol = indexCol + 3;
							}

							// 醫護的申報點數
							if (feeApplyList.contains("健保")) {
								medicalOrderSheet.addMergedRegionUnsafe(
										new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
								if (!medDot.toString().equals("")) {
									addRowCell(dataRow_med, indexCol, addThousandths(Long.parseLong(medDot.toString())),
											cellStyle_left);
								} else {
									addRowCell(dataRow_med, indexCol, "0", cellStyle_left);
								}
								addRowCell(dataRow_med, indexCol + 1, "", cellStyle_left);
								addRowCell(dataRow_med, indexCol + 2, "", cellStyle_left);
								indexCol = indexCol + 3;

								// 醫護去年同期時段申報點數
								if (isLastY) {
									medicalOrderSheet.addMergedRegionUnsafe(
											new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
									if (!medDot_Y.toString().equals("")) {
										addRowCell(dataRow_med, indexCol,
												addThousandths(Long.parseLong(medDot_Y.toString())), cellStyle_left);
									} else {
										addRowCell(dataRow_med, indexCol, "0", cellStyle_left);
									}
									addRowCell(dataRow_med, indexCol + 1, "", cellStyle_left);
									addRowCell(dataRow_med, indexCol + 2, "", cellStyle_left);
									indexCol = indexCol + 3;
								}
							}

							// 醫護的自費金額
							if (feeApplyList.contains("自費")) {
								medicalOrderSheet.addMergedRegionUnsafe(
										new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
								if (!medExpense.toString().equals("")) {
									addRowCell(dataRow_med, indexCol,
											addThousandths(Long.parseLong(medExpense.toString())), cellStyle_left);
								} else {
									addRowCell(dataRow_med, indexCol, "0", cellStyle_left);
								}
								addRowCell(dataRow_med, indexCol + 1, "", cellStyle_left);
								addRowCell(dataRow_med, indexCol + 2, "", cellStyle_left);
								indexCol = indexCol + 3;

								// 醫護去年同期時段自費金額
								if (isLastY) {
									medicalOrderSheet.addMergedRegionUnsafe(
											new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
									if (!medExpense_Y.toString().equals("")) {
										addRowCell(dataRow_med, indexCol,
												addThousandths(Long.parseLong(medExpense_Y.toString())),
												cellStyle_left);
									} else {
										addRowCell(dataRow_med, indexCol, "0", cellStyle_left);
									}
									addRowCell(dataRow_med, indexCol + 1, "", cellStyle_left);
									addRowCell(dataRow_med, indexCol + 2, "", cellStyle_left);
									indexCol = indexCol + 3;
								}
							}

							indexRow++;

							indexCol = 4;
						}
					}
				}
			}

		} else {

			for (int i = 0; i < classData.size(); i++) {

				if (funcTypeList.contains(classData.get(i).get("DESC_CHI").toString())) {

					StringBuffer desc_chi = new StringBuffer("");
					StringBuffer quantity = new StringBuffer("");
					StringBuffer dot = new StringBuffer("");
					StringBuffer expense = new StringBuffer("");

					StringBuffer desc_chi_Y = new StringBuffer("");
					StringBuffer quantity_Y = new StringBuffer("");
					StringBuffer dot_Y = new StringBuffer("");
					StringBuffer expense_Y = new StringBuffer("");

					StringBuffer desc_chi_M = new StringBuffer("");
					StringBuffer quantity_M = new StringBuffer("");
					StringBuffer dot_M = new StringBuffer("");
					StringBuffer expense_M = new StringBuffer("");

					desc_chi = new StringBuffer(classData.get(i).get("DESC_CHI").toString());
					quantity = new StringBuffer(classData.get(i).get("QUANTITY").toString());
					dot = new StringBuffer(classData.get(i).get("DOT").toString());
					expense = new StringBuffer(classData.get(i).get("EXPENSE").toString());

					if (classData_Y != null) {
						desc_chi_Y = new StringBuffer(classData_Y.get(i).get("DESC_CHI").toString());
						quantity_Y = new StringBuffer(classData_Y.get(i).get("QUANTITY").toString());
						dot_Y = new StringBuffer(classData_Y.get(i).get("DOT").toString());
						expense_Y = new StringBuffer(classData_Y.get(i).get("EXPENSE").toString());
					}

					indexCol = 4;

					// 科名
					HSSFRow dataRow_class = medicalOrderSheet.createRow(indexRow);
					medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, 0, 3));
					addRowCell(dataRow_class, 0, desc_chi.toString(), cellStyle_left);
					addRowCell(dataRow_class, 1, "", cellStyle_left);
					addRowCell(dataRow_class, 2, "", cellStyle_left);
					addRowCell(dataRow_class, 3, "", cellStyle_left);

					// 科名的案件數
					medicalOrderSheet
							.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
					if (classData.get(i) != null) {
						addRowCell(dataRow_class, indexCol, addThousandths(Long.parseLong(quantity.toString())),
								cellStyle_left);
					} else {
						addRowCell(dataRow_class, indexCol, "0", cellStyle_left);
					}
					addRowCell(dataRow_class, indexCol + 1, "", cellStyle_left);
					addRowCell(dataRow_class, indexCol + 2, "", cellStyle_left);
					indexCol = indexCol + 3;

					// 科名去年同期時段案件數
					if (isLastY) {
						medicalOrderSheet.addMergedRegionUnsafe(
								new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
						if (classData_Y != null) {
							addRowCell(dataRow_class, indexCol, addThousandths(Long.parseLong(quantity_Y.toString())),
									cellStyle_left);
						} else {
							addRowCell(dataRow_class, indexCol, "0", cellStyle_left);
						}
						addRowCell(dataRow_class, indexCol + 1, "", cellStyle_left);
						addRowCell(dataRow_class, indexCol + 2, "", cellStyle_left);
						indexCol = indexCol + 3;
					}

					// 科名的申報點數
					if (feeApplyList.contains("健保")) {
						medicalOrderSheet.addMergedRegionUnsafe(
								new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
						if (classData.get(i) != null) {
							addRowCell(dataRow_class, indexCol, addThousandths(Long.parseLong(dot.toString())),
									cellStyle_left);
						} else {
							addRowCell(dataRow_class, indexCol, "0", cellStyle_left);
						}
						addRowCell(dataRow_class, indexCol + 1, "", cellStyle_left);
						addRowCell(dataRow_class, indexCol + 2, "", cellStyle_left);
						indexCol = indexCol + 3;

						// 科名去年同期時段申報點數
						if (isLastY) {
							medicalOrderSheet.addMergedRegionUnsafe(
									new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
							if (classData_Y != null) {
								addRowCell(dataRow_class, indexCol, addThousandths(Long.parseLong(dot_Y.toString())),
										cellStyle_left);
							} else {
								addRowCell(dataRow_class, indexCol, "0", cellStyle_left);
							}
							addRowCell(dataRow_class, indexCol + 1, "", cellStyle_left);
							addRowCell(dataRow_class, indexCol + 2, "", cellStyle_left);
							indexCol = indexCol + 3;
						}
					}

					// 科名的自費金額
					if (feeApplyList.contains("自費")) {
						medicalOrderSheet.addMergedRegionUnsafe(
								new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
						if (classData.get(i) != null) {
							addRowCell(dataRow_class, indexCol, addThousandths(Long.parseLong(expense.toString())),
									cellStyle_left);
						} else {
							addRowCell(dataRow_class, indexCol, "0", cellStyle_left);
						}
						addRowCell(dataRow_class, indexCol + 1, "", cellStyle_left);
						addRowCell(dataRow_class, indexCol + 2, "", cellStyle_left);
						indexCol = indexCol + 3;

						// 科名去年同期時段自費金額
						if (isLastY) {
							medicalOrderSheet.addMergedRegionUnsafe(
									new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
							if (classData_Y != null) {
								addRowCell(dataRow_class, indexCol,
										addThousandths(Long.parseLong(expense_Y.toString())), cellStyle_left);
							} else {
								addRowCell(dataRow_class, indexCol, "0", cellStyle_left);
							}
							addRowCell(dataRow_class, indexCol + 1, "", cellStyle_left);
							addRowCell(dataRow_class, indexCol + 2, "", cellStyle_left);
							indexCol = indexCol + 3;
						}
					}

					indexRow++;

					// 科別之下醫護人員的案件數、申報點數、自費金額
					if (medDataList.size() > 0) {

						// 醫護名title
						HSSFRow titleRow_med = medicalOrderSheet.createRow(indexRow);
						medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, 0, 3));
						addRowCell(titleRow_med, 0, "醫護名", cellStyle);
						addRowCell(titleRow_med, 1, "", cellStyle_left);
						addRowCell(titleRow_med, 2, "", cellStyle_left);
						addRowCell(titleRow_med, 3, "", cellStyle_left);

						indexCol = 4;

						// 醫護案件數title
						titleName = new StringBuffer("");
//						titleName.append(dataFormat);
//						titleName.append("總案件數");
						medicalOrderSheet.addMergedRegionUnsafe(
								new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
						addRowCell(titleRow_med, indexCol, titleName.toString(), cellStyle_left);
						addRowCell(titleRow_med, indexCol + 1, "", cellStyle_left);
						addRowCell(titleRow_med, indexCol + 2, "", cellStyle_left);
						indexCol = indexCol + 3;

						// 醫護去年同期時段案件數title
						if (isLastY) {
							titleName = new StringBuffer("");
//							titleName.append("去年同期時段");
							medicalOrderSheet.addMergedRegionUnsafe(
									new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
							addRowCell(titleRow_med, indexCol, titleName.toString(), cellStyle_left);
							addRowCell(titleRow_med, indexCol + 1, "", cellStyle_left);
							addRowCell(titleRow_med, indexCol + 2, "", cellStyle_left);
							indexCol = indexCol + 3;
						}

						// 醫護申報點數title
						if (feeApplyList.contains("健保")) {
							titleName = new StringBuffer("");
//							titleName.append(dataFormat);
//							titleName.append("申報總點數");
							medicalOrderSheet.addMergedRegionUnsafe(
									new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
							addRowCell(titleRow_med, indexCol, titleName.toString(), cellStyle_left);
							addRowCell(titleRow_med, indexCol + 1, "", cellStyle_left);
							addRowCell(titleRow_med, indexCol + 2, "", cellStyle_left);
							indexCol = indexCol + 3;

							// 醫護去年同期時段申報點數title
							if (isLastY) {
								titleName = new StringBuffer("");
//								titleName.append("去年同期時段");
								medicalOrderSheet.addMergedRegionUnsafe(
										new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
								addRowCell(titleRow_med, indexCol, titleName.toString(), cellStyle_left);
								addRowCell(titleRow_med, indexCol + 1, "", cellStyle_left);
								addRowCell(titleRow_med, indexCol + 2, "", cellStyle_left);
								indexCol = indexCol + 3;
							}
						}

						// 醫護自費金額title
						if (feeApplyList.contains("自費")) {
							titleName = new StringBuffer("");
//							titleName.append(dataFormat);
//							titleName.append("自費總金額");
							medicalOrderSheet.addMergedRegionUnsafe(
									new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
							addRowCell(titleRow_med, indexCol, titleName.toString(), cellStyle_left);
							addRowCell(titleRow_med, indexCol + 1, "", cellStyle_left);
							addRowCell(titleRow_med, indexCol + 2, "", cellStyle_left);
							indexCol = indexCol + 3;

							// 醫護去年同期時段自費金額title
							if (isLastY) {
								titleName = new StringBuffer("");
//								titleName.append("去年同期時段");
								medicalOrderSheet.addMergedRegionUnsafe(
										new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
								addRowCell(titleRow_med, indexCol, titleName.toString(), cellStyle_left);
								addRowCell(titleRow_med, indexCol + 1, "", cellStyle_left);
								addRowCell(titleRow_med, indexCol + 2, "", cellStyle_left);
								indexCol = indexCol + 3;
							}
						}

						indexRow++;

						Map<String, Object> medData_lastY = new HashedMap<String, Object>();
						Map<String, Object> medData = new HashedMap<String, Object>();

						for (int j = 0; j < medDataList.size(); j++) {

							StringBuffer medSDate = new StringBuffer(medDataList.get(j).get("startDate").toString());
							StringBuffer medEDate = new StringBuffer(medDataList.get(j).get("endDate").toString());
							StringBuffer medName = new StringBuffer(medDataList.get(j).get("medName").toString());
							StringBuffer medDataF = new StringBuffer(medDataList.get(j).get("dataFormat").toString());

							if (!lastSDateStr.contains(sDate.toString()) && !lastEDateStr.contains(eDate.toString())
									&& sDate.equals(medSDate.toString()) && eDate.equals(medEDate.toString())
									&& dataFormat.equals(medDataF.toString())) {

//								    StringBuffer medDesc_chi=new StringBuffer("0");
								StringBuffer medQuantity = new StringBuffer("0");
								StringBuffer medDot = new StringBuffer("0");
								StringBuffer medExpense = new StringBuffer("0");

								StringBuffer medQuantity_Y = new StringBuffer("0");
								StringBuffer medDot_Y = new StringBuffer("0");
								StringBuffer medExpense_Y = new StringBuffer("0");

								List<Map<String, Object>> md = (List<Map<String, Object>>) medDataList.get(j)
										.get("data");

								for (int k = 0; k < md.size(); k++) {
									if (md.get(k).get("DESC_CHI").equals(desc_chi.toString())) {
										medData = (Map<String, Object>) md.get(k);
										break;
									}
								}

								if (medDataList_Y.size() > 0 && isLastY) {
									medData_lastY = getLastYearMedData(medSDate.toString(), medEDate.toString(),
											medDataF.toString(), medName.toString(), desc_chi.toString(),
											medDataList_Y);
								}

								if (medData != null && !medData.isEmpty()) {
									medQuantity = new StringBuffer(medData.get("QUANTITY").toString());
									medDot = new StringBuffer(medData.get("DOT").toString());
									medExpense = new StringBuffer(medData.get("EXPENSE").toString());
								}

								if (medData_lastY != null && !medData_lastY.isEmpty()) {
									medQuantity_Y = new StringBuffer(medData_lastY.get("QUANTITY").toString());
									medDot_Y = new StringBuffer(medData_lastY.get("DOT").toString());
									medExpense_Y = new StringBuffer(medData_lastY.get("EXPENSE").toString());
								}

								indexCol = 4;

								// 醫護名
								HSSFRow dataRow_med = medicalOrderSheet.createRow(indexRow);
								medicalOrderSheet.addMergedRegionUnsafe(new CellRangeAddress(indexRow, indexRow, 0, 3));
								addRowCell(dataRow_med, 0, medName.toString(), cellStyle_left);
								addRowCell(dataRow_med, 1, "", cellStyle_left);
								addRowCell(dataRow_med, 2, "", cellStyle_left);
								addRowCell(dataRow_med, 3, "", cellStyle_left);

								// 醫護的案件數
								medicalOrderSheet.addMergedRegionUnsafe(
										new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
								if (!medQuantity.toString().equals("")) {
									addRowCell(dataRow_med, indexCol,
											addThousandths(Long.parseLong(medQuantity.toString())), cellStyle_left);
								} else {
									addRowCell(dataRow_med, indexCol, "0", cellStyle_left);
								}
								addRowCell(dataRow_med, indexCol + 1, "", cellStyle_left);
								addRowCell(dataRow_med, indexCol + 2, "", cellStyle_left);
								indexCol = indexCol + 3;

								// 醫護去年同期時段案件數
								if (isLastY) {
									medicalOrderSheet.addMergedRegionUnsafe(
											new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
									if (!medQuantity_Y.toString().equals("")) {
										addRowCell(dataRow_med, indexCol,
												addThousandths(Long.parseLong(medQuantity_Y.toString())),
												cellStyle_left);
									} else {
										addRowCell(dataRow_med, indexCol, "0", cellStyle_left);
									}
									addRowCell(dataRow_med, indexCol + 1, "", cellStyle_left);
									addRowCell(dataRow_med, indexCol + 2, "", cellStyle_left);
									indexCol = indexCol + 3;
								}

								// 醫護的申報點數
								if (feeApplyList.contains("健保")) {
									medicalOrderSheet.addMergedRegionUnsafe(
											new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
									if (!medDot.toString().equals("")) {
										addRowCell(dataRow_med, indexCol,
												addThousandths(Long.parseLong(medDot.toString())), cellStyle_left);
									} else {
										addRowCell(dataRow_med, indexCol, "0", cellStyle_left);
									}
									addRowCell(dataRow_med, indexCol + 1, "", cellStyle_left);
									addRowCell(dataRow_med, indexCol + 2, "", cellStyle_left);
									indexCol = indexCol + 3;

									// 醫護去年同期時段申報點數
									if (isLastY) {
										medicalOrderSheet.addMergedRegionUnsafe(
												new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
										if (!medDot_Y.toString().equals("")) {
											addRowCell(dataRow_med, indexCol,
													addThousandths(Long.parseLong(medDot_Y.toString())),
													cellStyle_left);
										} else {
											addRowCell(dataRow_med, indexCol, "0", cellStyle_left);
										}
										addRowCell(dataRow_med, indexCol + 1, "", cellStyle_left);
										addRowCell(dataRow_med, indexCol + 2, "", cellStyle_left);
										indexCol = indexCol + 3;
									}
								}

								// 醫護的自費金額
								if (feeApplyList.contains("自費")) {
									medicalOrderSheet.addMergedRegionUnsafe(
											new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
									if (!medExpense.toString().equals("")) {
										addRowCell(dataRow_med, indexCol,
												addThousandths(Long.parseLong(medExpense.toString())), cellStyle_left);
									} else {
										addRowCell(dataRow_med, indexCol, "0", cellStyle_left);
									}
									addRowCell(dataRow_med, indexCol + 1, "", cellStyle_left);
									addRowCell(dataRow_med, indexCol + 2, "", cellStyle_left);
									indexCol = indexCol + 3;

									// 醫護去年同期時段自費金額
									if (isLastY) {
										medicalOrderSheet.addMergedRegionUnsafe(
												new CellRangeAddress(indexRow, indexRow, indexCol, indexCol + 2));
										if (!medExpense_Y.toString().equals("")) {
											addRowCell(dataRow_med, indexCol,
													addThousandths(Long.parseLong(medExpense_Y.toString())),
													cellStyle_left);
										} else {
											addRowCell(dataRow_med, indexCol, "0", cellStyle_left);
										}
										addRowCell(dataRow_med, indexCol + 1, "", cellStyle_left);
										addRowCell(dataRow_med, indexCol + 2, "", cellStyle_left);
										indexCol = indexCol + 3;
									}
								}

								indexRow++;

								indexCol = 4;
							}
						}
					}
				}
			}
		}

		indexRow++;

		return indexRow;
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getLastYearData(boolean isClassData, String date, String format,
			List<Map<String, Object>> dataList) {

		List<Map<String, Object>> lastData = null;

		for (int i = 0; i < dataList.size(); i++) {
			if (findLastYear(date, false).equals(dataList.get(i).get("date").toString())
					&& format.equals(dataList.get(i).get("dataFormat").toString())) {

				if (isClassData) {
					lastData = (List<Map<String, Object>>) dataList.get(i).get("classData");
				} else {
					lastData = (List<Map<String, Object>>) dataList.get(i).get("data");
				}

				break;
			}
		}

		return lastData;
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getLastYearData(boolean isClassData, String sDate, String eDate, String format,
			List<Map<String, Object>> dataList) {

		List<Map<String, Object>> lastData = null;

		for (int i = 0; i < dataList.size(); i++) {
			if (findLastYear(sDate, true).equals(dataList.get(i).get("startDate").toString())
					&& findLastYear(eDate, true).equals(dataList.get(i).get("endDate").toString())
					&& format.equals(dataList.get(i).get("dataFormat").toString())) {

				if (isClassData) {
					lastData = (List<Map<String, Object>>) dataList.get(i).get("classData");
				} else {
					lastData = (List<Map<String, Object>>) dataList.get(i).get("data");
				}

				break;
			}
		}

		return lastData;
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getLastMonthData(boolean isClassData, String date, String format,
			List<Map<String, Object>> dataList) {

		List<Map<String, Object>> lastData = null;

		for (int i = 0; i < dataList.size(); i++) {
			if (findLastMonth(date).equals(dataList.get(i).get("date").toString())
					&& format.equals(dataList.get(i).get("dataFormat").toString())) {

				if (isClassData) {
					lastData = (List<Map<String, Object>>) dataList.get(i).get("classData");
				} else {
					lastData = (List<Map<String, Object>>) dataList.get(i).get("data");
				}

				break;
			}
		}

		return lastData;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getLastYearMedData(String medDate, String medDataF, String medName, String medDesc_chi,
			List<Map<String, Object>> medDataList) {

		Map<String, Object> lastData = null;

		for (int i = 0; i < medDataList.size(); i++) {
			if (findLastYear(medDate, false).equals(medDataList.get(i).get("date").toString())
					&& medDataF.equals(medDataList.get(i).get("dataFormat").toString())
					&& medName.equals(medDataList.get(i).get("medName").toString())) {

				List<Map<String, Object>> data = (List<Map<String, Object>>) medDataList.get(i).get("data");

				for (int j = 0; j < data.size(); j++) {
					if (data.get(j).get("DESC_CHI").equals(medDesc_chi)) {
						lastData = (Map<String, Object>) data.get(j);
						break;
					}
				}
			}
		}

		return lastData;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getLastYearMedData(String sDate, String eDate, String medDataF, String medName,
			String medDesc_chi, List<Map<String, Object>> medDataList) {

		Map<String, Object> lastData = null;

		for (int i = 0; i < medDataList.size(); i++) {
			if (findLastYear(sDate, true).equals(medDataList.get(i).get("startDate").toString())
					&& findLastYear(eDate, true).equals(medDataList.get(i).get("endDate").toString())
					&& medDataF.equals(medDataList.get(i).get("dataFormat").toString())
					&& medName.equals(medDataList.get(i).get("medName").toString())) {

				List<Map<String, Object>> data = (List<Map<String, Object>>) medDataList.get(i).get("data");

				for (int j = 0; j < data.size(); j++) {
					if (data.get(j).get("DESC_CHI").equals(medDesc_chi)) {
						lastData = (Map<String, Object>) data.get(j);
						break;
					}
				}
			}
		}

		return lastData;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getLastMonthMedData(String medDate, String medDataF, String medName, String medDesc_chi,
			List<Map<String, Object>> medDataList) {

		Map<String, Object> lastData = null;

		for (int i = 0; i < medDataList.size(); i++) {
			if (findLastMonth(medDate).equals(medDataList.get(i).get("date").toString())
					&& medDataF.equals(medDataList.get(i).get("dataFormat").toString())
					&& medName.equals(medDataList.get(i).get("medName").toString())) {

				List<Map<String, Object>> data = (List<Map<String, Object>>) medDataList.get(i).get("data");

				for (int j = 0; j < data.size(); j++) {
					if (data.get(j).get("DESC_CHI").equals(medDesc_chi)) {
						lastData = (Map<String, Object>) data.get(j);
						break;
					}
				}
			}
		}

		return lastData;
	}

	public String findLastYear(String str, boolean isHaveDay) {

		StringBuffer dateType = new StringBuffer();
		if (isHaveDay) {
			dateType.append("yyyy-MM-dd");
		} else {
			dateType.append("yyyy-MM");
		}

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(dateType.toString());
		try {
			cal.setTime(sdf.parse(str));
			cal.add(Calendar.YEAR, -1);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String formatted = sdf.format(cal.getTime());

		return formatted;
	}

	public String findLastMonth(String str) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		try {
			cal.setTime(sdf.parse(str));
			cal.add(Calendar.MONTH, -1);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String formatted = sdf.format(cal.getTime());

		return formatted;
	}

	// 千分位
	public String addThousandths(Long num) {
		DecimalFormat df = new DecimalFormat("#,###");
		return df.format(num);
	}

	public Comparator<OwnExpenseQueryConditionDetail> mapComparatorPI = new Comparator<OwnExpenseQueryConditionDetail>() {
		public int compare(OwnExpenseQueryConditionDetail m1, OwnExpenseQueryConditionDetail m2) {
			return m1.getPrsnId().compareTo(m2.getPrsnId());
		}
	};

	public Comparator<DeductedNoteQueryConditionList> mapComparatorDeductedFT = new Comparator<DeductedNoteQueryConditionList>() {
		public int compare(DeductedNoteQueryConditionList m1, DeductedNoteQueryConditionList m2) {
			return m1.getFuncType().compareTo(m2.getFuncType());
		}
	};

	public Comparator<DeductedNoteQueryConditionCode> mapComparatorDeductedCD = new Comparator<DeductedNoteQueryConditionCode>() {
		public int compare(DeductedNoteQueryConditionCode m1, DeductedNoteQueryConditionCode m2) {
			return m1.getCode().compareTo(m2.getCode());
		}
	};

}
