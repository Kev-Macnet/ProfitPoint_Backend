package tw.com.leadtek.nhiwidget.service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.payload.report.AchievementQuarter;
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

	public void getDrgQueryConditionExport(String dateTypes, String year, String month, String betweenSdate,
			String betweenEdate, String sections, String drgCodes, String dataFormats, String funcTypes,
			String medNames, String icdcms, String medLogCodes, int applMin, int applMax, String icdAll, String payCode,
			String inhCode, boolean isShowDRGList, boolean isLastM, boolean isLastY, HttpServletResponse response)
			throws ParseException, IOException {
		Map<String, Object> dataMap = dbrService.getDrgQueryCondition(dateTypes, year, month, betweenSdate,
				betweenEdate, sections, drgCodes, dataFormats, funcTypes, medNames, icdcms, medLogCodes, applMin,
				applMax, icdAll, payCode, inhCode, isShowDRGList, isLastM, isLastY);

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> drgDataList = (List<Map<String, Object>>) dataMap.get("drgData");
		List<Map<String, Object>> appendList = new ArrayList<Map<String, Object>>();

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

		if (drgDataList.size() > 0) {
			HSSFSheet sheet = workbook.createSheet("DRG案件數分佈佔率與定額、實際點數");
			/// 欄位A1
			HSSFRow row = sheet.createRow(0);
			HSSFCell cell = row.createCell(0);
			cell.setCellValue("統計月份");
			cell.setCellStyle(cellStyle);
			/// 欄位B1
			cell = row.createCell(1);
			String cellValue = "";
			String cellValue2 = "";
			List<Integer> il = new ArrayList<Integer>();
			int count = 0;
			for (Map<String, Object> map : drgDataList) {
				String date = map.get("DATE").toString();
				String dc = map.get("DRG_CODE").toString();
				Object dn = map.get("disPlayName");
				if (dc.isEmpty() && (dn == null || dn.toString().length() == 0)) {
					cellValue += date + "、";
				} else if (dc.isEmpty() && !dn.toString().isEmpty()) {
					cellValue2 += dn + "、";
					il.add(count);
					appendList.add(map);
				}
				count++;
			}
			if(il.size() > 0) {
				for(int i : il) {
					drgDataList.remove(0);
				}
				drgDataList.addAll(appendList);
			}
			cellValue = cellValue.substring(0, cellValue.length() - 1);
			if (cellValue2.length() > 0) {
				cellValue2 = cellValue2.substring(0, cellValue2.length() - 1);
				cellValue += "、" + cellValue2;
			}
			String[] split = cellValue.split("、");
			cellIndex = 2;
			for (String str : split) {
				cell.setCellValue(str);
				cell.setCellStyle(cellStyle);
				cell = row.createCell(cellIndex);
				cellIndex++;
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
				split = StringUtility.splitBySpace(dataFormats);
				cellValue = "";
				for (String str : split) {
					switch (str) {
					case "all":
						cellValue += "不分科" + "、";
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
					for (int x = 0; x < drgDataList.size(); x++) {

						cellIndex++;
						cell = row.createCell(cellIndex);
						String date = drgDataList.get(x).get("DATE").toString();
						String dc = drgDataList.get(x).get("DRG_CODE").toString();
						Object dn = drgDataList.get(x).get("disPlayName");
						cellValue = date;
						if (dc.isEmpty() && dn == null) {
							cellValue = date;
						} else if (dc.isEmpty() && dn.toString().isEmpty()) {
							cellValue = date;
						} else if (dc.isEmpty() && dn.toString().length() > 0) {
							cellValue = dn.toString();
						} else if (dc.length() > 0) {
							cellValue = dc;
						}
						cell.setCellValue(cellValue);
						cell.setCellStyle(cellStyle);
					}
					cellIndex = 0;
					break;
				case 1:
					for (int x = 0; x < drgDataList.size(); x++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						Long dq = Long.parseLong(drgDataList.get(x).get("DRG_QUANTITY").toString());
						cell.setCellValue(dq.doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
					break;
				case 2:
					for (int x = 0; x < drgDataList.size(); x++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						if (drgDataList.get(x).get("DRG_APPL_POINT") == null) {
							cell.setCellValue(0);
						} else {

							Long da = Long.parseLong(drgDataList.get(x).get("DRG_APPL_POINT").toString());
							cell.setCellValue(da.doubleValue());
						}
						cell.setCellStyle(cellFormatStyle);
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
			if (sections != null && sections.length() > 0) {
				Long sectionA = 0L;
				Long sectionB1 = 0L;
				Long sectionB2 = 0L;
				Long sectionC = 0L;
				///計算分區各案件數量
				for(int v=0; v < drgDataList.size(); v++) {
					Object a = drgDataList.get(v).get("SECTION_A");
					Object b1 = drgDataList.get(v).get("SECTION_B1");
					Object b2 = drgDataList.get(v).get("SECTION_B2");
					Object c = drgDataList.get(v).get("SECTION_C");
					if((drgCodes != null && drgCodes.length() > 0) || isShowDRGList) {
						
						if(drgDataList.get(v).get("DRG_CODE") != null && drgDataList.get(v).get("DRG_CODE").toString().length() == 0) {
							continue;
						} 
					}
					if(a != null) sectionA += Long.parseLong(a.toString());
					if(b1 != null) sectionB1 += Long.parseLong(b1.toString());
					if(b2 != null) sectionB2 += Long.parseLong(b2.toString());
					if(c != null) sectionC += Long.parseLong(c.toString());
				}
				split = StringUtility.splitBySpace(sections);
				/// 欄位A
				rowIndex++;
				row = sheet.createRow(rowIndex);
				for (String str : split) {
					switch (str) {
					case "A":
						cellIndex = 0;
						for (int y = 0; y < tableRowHeadersA.length; y++) {
							cell = row.createCell(cellIndex);
							cell.setCellValue(tableRowHeadersA[y]);
							cell.setCellStyle(cellStyle);
							switch (y) {
							case 0:
								for (int x = 0; x < drgDataList.size(); x++) {

									cellIndex++;
									cell = row.createCell(cellIndex);
									String date = drgDataList.get(x).get("DATE").toString();
									String dc = drgDataList.get(x).get("DRG_CODE").toString();
									Object dn = drgDataList.get(x).get("disPlayName");
									cellValue = date;
									if (dc.isEmpty() && dn == null) {
										cellValue = date;
									} else if (dc.isEmpty() && dn.toString().isEmpty()) {
										cellValue = date;
									} else if (dc.isEmpty() && dn.toString().length() > 0) {
										cellValue = dn.toString();
									} else if (dc.length() > 0) {
										cellValue = dc;
									}
									cell.setCellValue(cellValue);
									cell.setCellStyle(cellStyle);
								}
								cellIndex = 0;
								break;
							case 1:
								for (int x = 0; x < drgDataList.size(); x++) {
									cellIndex++;
									cell = row.createCell(cellIndex);
									if(drgDataList.get(x).get("SECTION_A") != null) {
										
										Long dq = Long.parseLong(drgDataList.get(x).get("SECTION_A").toString());
										cell.setCellValue(dq.doubleValue());
										cell.setCellStyle(cellFormatStyle);
									}
									else {
										cell.setCellValue(0);
										cell.setCellStyle(cellFormatStyle);
									}
									
								}
								cellIndex = 0;
								break;
							case 2:
								for (int x = 0; x < drgDataList.size(); x++) {
									cellIndex++;
									cell = row.createCell(cellIndex);
									if(drgDataList.get(x).get("SECTION_A") != null) {
										
										Long dq = Long.parseLong(drgDataList.get(x).get("SECTION_A").toString());
										double d = Math.round(dq.doubleValue() / sectionA.doubleValue() * 100.0 * 100.0)
												/ 100.0;
										cell.setCellValue(d + "%");
										cell.setCellStyle(cellFormatStyle);
									}
									else {
										cell.setCellValue(0);
										cell.setCellStyle(cellFormatStyle);
									}
									
								}
								cellIndex = 0;
								break;
							case 3:
								for (int x = 0; x < drgDataList.size(); x++) {
									cellIndex++;
									cell = row.createCell(cellIndex);
									if(drgDataList.get(x).get("SECTION_A_ACTUAL") != null) {
										
										Long dq = Long.parseLong(drgDataList.get(x).get("SECTION_A_ACTUAL").toString());
										cell.setCellValue(dq.doubleValue());
										cell.setCellStyle(cellFormatStyle);
									}
									else {
										cell.setCellValue(0);
										cell.setCellStyle(cellFormatStyle);
									}
									
								}
								cellIndex = 0;
								break;
							case 4:
								for (int x = 0; x < drgDataList.size(); x++) {
									cellIndex++;
									cell = row.createCell(cellIndex);
									if(drgDataList.get(x).get("SECTION_A_APPL") != null) {
										
										Long dq = Long.parseLong(drgDataList.get(x).get("SECTION_A_APPL").toString());
										cell.setCellValue(dq.doubleValue());
										cell.setCellStyle(cellFormatStyle);
									}
									else {
										cell.setCellValue(0);
										cell.setCellStyle(cellFormatStyle);
									}
									
								}
								cellIndex = 0;
								break;
							case 5:
								for (int x = 0; x < drgDataList.size(); x++) {
									cellIndex++;
									cell = row.createCell(cellIndex);
									if(drgDataList.get(x).get("DIFFA") != null) {
										
										Long dq = Long.parseLong(drgDataList.get(x).get("DIFFA").toString());
										cell.setCellValue(dq.doubleValue());
										cell.setCellStyle(cellFormatStyle);
									}
									else {
										cell.setCellValue(0);
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
						rowIndex++;
						row = sheet.createRow(rowIndex);
						break;
					case "B1":
						cellIndex = 0;
						for (int y = 0; y < tableRowHeadersB1.length; y++) {
							cell = row.createCell(cellIndex);
							cell.setCellValue(tableRowHeadersB1[y]);
							cell.setCellStyle(cellStyle);
							switch (y) {
							case 0:
								for (int x = 0; x < drgDataList.size(); x++) {

									cellIndex++;
									cell = row.createCell(cellIndex);
									String date = drgDataList.get(x).get("DATE").toString();
									String dc = drgDataList.get(x).get("DRG_CODE").toString();
									Object dn = drgDataList.get(x).get("disPlayName");
									cellValue = date;
									if (dc.isEmpty() && dn == null) {
										cellValue = date;
									} else if (dc.isEmpty() && dn.toString().isEmpty()) {
										cellValue = date;
									} else if (dc.isEmpty() && dn.toString().length() > 0) {
										cellValue = dn.toString();
									} else if (dc.length() > 0) {
										cellValue = dc;
									}
									cell.setCellValue(cellValue);
									cell.setCellStyle(cellStyle);
								}
								cellIndex = 0;
								break;
							case 1:
								for (int x = 0; x < drgDataList.size(); x++) {
									cellIndex++;
									cell = row.createCell(cellIndex);
									if(drgDataList.get(x).get("SECTION_B1") != null) {
										
										Long dq = Long.parseLong(drgDataList.get(x).get("SECTION_B1").toString());
										cell.setCellValue(dq.doubleValue());
										cell.setCellStyle(cellFormatStyle);
									}
									else {
										cell.setCellValue(0);
										cell.setCellStyle(cellFormatStyle);
									}
									
								}
								cellIndex = 0;
								break;
							case 2:
								for (int x = 0; x < drgDataList.size(); x++) {
									cellIndex++;
									cell = row.createCell(cellIndex);
									if(drgDataList.get(x).get("SECTION_B1") != null) {
										
										Long dq = Long.parseLong(drgDataList.get(x).get("SECTION_B1").toString());
										double d = Math.round(dq.doubleValue() / sectionB1.doubleValue() * 100.0 * 100.0)
												/ 100.0;
										cell.setCellValue(d + "%");
										cell.setCellStyle(cellFormatStyle);
									}
									else {
										cell.setCellValue(0);
										cell.setCellStyle(cellFormatStyle);
									}
									
								}
								cellIndex = 0;
								break;
							case 3:
								for (int x = 0; x < drgDataList.size(); x++) {
									cellIndex++;
									cell = row.createCell(cellIndex);
									if(drgDataList.get(x).get("SECTION_B1_ACTUAL") != null) {
										
										Long dq = Long.parseLong(drgDataList.get(x).get("SECTION_B1_ACTUAL").toString());
										cell.setCellValue(dq.doubleValue());
										cell.setCellStyle(cellFormatStyle);
									}
									else {
										cell.setCellValue(0);
										cell.setCellStyle(cellFormatStyle);
									}
									
								}
								cellIndex = 0;
								break;
							case 4:
								for (int x = 0; x < drgDataList.size(); x++) {
									cellIndex++;
									cell = row.createCell(cellIndex);
									if(drgDataList.get(x).get("SECTION_B1_APPL") != null) {
										
										Long dq = Long.parseLong(drgDataList.get(x).get("SECTION_B1_APPL").toString());
										cell.setCellValue(dq.doubleValue());
										cell.setCellStyle(cellFormatStyle);
									}
									else {
										cell.setCellValue(0);
										cell.setCellStyle(cellFormatStyle);
									}
									
								}
								cellIndex = 0;
								break;
							case 5:
								for (int x = 0; x < drgDataList.size(); x++) {
									cellIndex++;
									cell = row.createCell(cellIndex);
									if(drgDataList.get(x).get("DIFFB1") != null) {
										
										Long dq = Long.parseLong(drgDataList.get(x).get("DIFFB1").toString());
										cell.setCellValue(dq.doubleValue());
										cell.setCellStyle(cellFormatStyle);
									}
									else {
										cell.setCellValue(0);
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
						rowIndex++;
						row = sheet.createRow(rowIndex);
						break;
					case "B2":
						cellIndex = 0;
						for (int y = 0; y < tableRowHeadersB2.length; y++) {
							cell = row.createCell(cellIndex);
							cell.setCellValue(tableRowHeadersB2[y]);
							cell.setCellStyle(cellStyle);
							switch (y) {
							case 0:
								for (int x = 0; x < drgDataList.size(); x++) {

									cellIndex++;
									cell = row.createCell(cellIndex);
									String date = drgDataList.get(x).get("DATE").toString();
									String dc = drgDataList.get(x).get("DRG_CODE").toString();
									Object dn = drgDataList.get(x).get("disPlayName");
									cellValue = date;
									if (dc.isEmpty() && dn == null) {
										cellValue = date;
									} else if (dc.isEmpty() && dn.toString().isEmpty()) {
										cellValue = date;
									} else if (dc.isEmpty() && dn.toString().length() > 0) {
										cellValue = dn.toString();
									} else if (dc.length() > 0) {
										cellValue = dc;
									}
									cell.setCellValue(cellValue);
									cell.setCellStyle(cellStyle);
								}
								cellIndex = 0;
								break;
							case 1:
								for (int x = 0; x < drgDataList.size(); x++) {
									cellIndex++;
									cell = row.createCell(cellIndex);
									if(drgDataList.get(x).get("SECTION_B2") != null) {
										
										Long dq = Long.parseLong(drgDataList.get(x).get("SECTION_B2").toString());
										cell.setCellValue(dq.doubleValue());
										cell.setCellStyle(cellFormatStyle);
									}
									else {
										cell.setCellValue(0);
										cell.setCellStyle(cellFormatStyle);
									}
									
								}
								cellIndex = 0;
								break;
							case 2:
								for (int x = 0; x < drgDataList.size(); x++) {
									cellIndex++;
									cell = row.createCell(cellIndex);
									if(drgDataList.get(x).get("SECTION_B2") != null) {
										
										Long dq = Long.parseLong(drgDataList.get(x).get("SECTION_B2").toString());
										double d = Math.round(dq.doubleValue() / sectionB2.doubleValue() * 100.0 * 100.0)
												/ 100.0;
										cell.setCellValue(d + "%");
										cell.setCellStyle(cellFormatStyle);
									}
									else {
										cell.setCellValue(0);
										cell.setCellStyle(cellFormatStyle);
									}
									
								}
								cellIndex = 0;
								break;
							case 3:
								for (int x = 0; x < drgDataList.size(); x++) {
									cellIndex++;
									cell = row.createCell(cellIndex);
									if(drgDataList.get(x).get("SECTION_B2_ACTUAL") != null) {
										
										Long dq = Long.parseLong(drgDataList.get(x).get("SECTION_B2_ACTUAL").toString());
										cell.setCellValue(dq.doubleValue());
										cell.setCellStyle(cellFormatStyle);
									}
									else {
										cell.setCellValue(0);
										cell.setCellStyle(cellFormatStyle);
									}
									
								}
								cellIndex = 0;
								break;
							case 4:
								for (int x = 0; x < drgDataList.size(); x++) {
									cellIndex++;
									cell = row.createCell(cellIndex);
									if(drgDataList.get(x).get("SECTION_B2_APPL") != null) {
										
										Long dq = Long.parseLong(drgDataList.get(x).get("SECTION_B2_APPL").toString());
										cell.setCellValue(dq.doubleValue());
										cell.setCellStyle(cellFormatStyle);
									}
									else {
										cell.setCellValue(0);
										cell.setCellStyle(cellFormatStyle);
									}
									
								}
								cellIndex = 0;
								break;
							case 5:
								for (int x = 0; x < drgDataList.size(); x++) {
									cellIndex++;
									cell = row.createCell(cellIndex);
									if(drgDataList.get(x).get("DIFFB2") != null) {
										
										Long dq = Long.parseLong(drgDataList.get(x).get("DIFFB2").toString());
										cell.setCellValue(dq.doubleValue());
										cell.setCellStyle(cellFormatStyle);
									}
									else {
										cell.setCellValue(0);
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
						rowIndex++;
						row = sheet.createRow(rowIndex);
						break;
					case "C":
						cellIndex = 0;
						for (int y = 0; y < tableRowHeadersC.length; y++) {
							cell = row.createCell(cellIndex);
							cell.setCellValue(tableRowHeadersC[y]);
							cell.setCellStyle(cellStyle);
							switch (y) {
							case 0:
								for (int x = 0; x < drgDataList.size(); x++) {

									cellIndex++;
									cell = row.createCell(cellIndex);
									String date = drgDataList.get(x).get("DATE").toString();
									String dc = drgDataList.get(x).get("DRG_CODE").toString();
									Object dn = drgDataList.get(x).get("disPlayName");
									cellValue = date;
									if (dc.isEmpty() && dn == null) {
										cellValue = date;
									} else if (dc.isEmpty() && dn.toString().isEmpty()) {
										cellValue = date;
									} else if (dc.isEmpty() && dn.toString().length() > 0) {
										cellValue = dn.toString();
									} else if (dc.length() > 0) {
										cellValue = dc;
									}
									cell.setCellValue(cellValue);
									cell.setCellStyle(cellStyle);
								}
								cellIndex = 0;
								break;
							case 1:
								for (int x = 0; x < drgDataList.size(); x++) {
									cellIndex++;
									cell = row.createCell(cellIndex);
									if(drgDataList.get(x).get("SECTION_C") != null) {
										
										Long dq = Long.parseLong(drgDataList.get(x).get("SECTION_C").toString());
										cell.setCellValue(dq.doubleValue());
										cell.setCellStyle(cellFormatStyle);
									}
									else {
										cell.setCellValue(0);
										cell.setCellStyle(cellFormatStyle);
									}
									
								}
								cellIndex = 0;
								break;
							case 2:
								for (int x = 0; x < drgDataList.size(); x++) {
									cellIndex++;
									cell = row.createCell(cellIndex);
									if(drgDataList.get(x).get("SECTION_C") != null) {
										
										Long dq = Long.parseLong(drgDataList.get(x).get("SECTION_C").toString());
										double d = Math.round(dq.doubleValue() / sectionC.doubleValue() * 100.0 * 100.0)
												/ 100.0;
										cell.setCellValue(d + "%");
										cell.setCellStyle(cellFormatStyle);
									}
									else {
										cell.setCellValue(0);
										cell.setCellStyle(cellFormatStyle);
									}
									
								}
								cellIndex = 0;
								break;
							case 3:
								for (int x = 0; x < drgDataList.size(); x++) {
									cellIndex++;
									cell = row.createCell(cellIndex);
									if(drgDataList.get(x).get("SECTION_C_ACTUAL") != null) {
										
										Long dq = Long.parseLong(drgDataList.get(x).get("SECTION_C_ACTUAL").toString());
										cell.setCellValue(dq.doubleValue());
										cell.setCellStyle(cellFormatStyle);
									}
									else {
										cell.setCellValue(0);
										cell.setCellStyle(cellFormatStyle);
									}
									
								}
								cellIndex = 0;
								break;
							case 4:
								for (int x = 0; x < drgDataList.size(); x++) {
									cellIndex++;
									cell = row.createCell(cellIndex);
									if(drgDataList.get(x).get("SECTION_C_APPL") != null) {
										
										Long dq = Long.parseLong(drgDataList.get(x).get("SECTION_C_APPL").toString());
										cell.setCellValue(dq.doubleValue());
										cell.setCellStyle(cellFormatStyle);
									}
									else {
										cell.setCellValue(0);
										cell.setCellStyle(cellFormatStyle);
									}
									
								}
								cellIndex = 0;
								break;
							case 5:
								for (int x = 0; x < drgDataList.size(); x++) {
									cellIndex++;
									cell = row.createCell(cellIndex);
									if(drgDataList.get(x).get("DIFFC") != null) {
										
										Long dq = Long.parseLong(drgDataList.get(x).get("DIFFC").toString());
										cell.setCellValue(dq.doubleValue());
										cell.setCellStyle(cellFormatStyle);
									}
									else {
										cell.setCellValue(0);
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
						break;
					default:
						break;
					}
				
				}

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
}
