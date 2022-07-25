package tw.com.leadtek.nhiwidget.service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.dao.CODE_TABLEDao;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_TABLE;
import tw.com.leadtek.nhiwidget.model.rdb.POINT_MONTHLY;
import tw.com.leadtek.nhiwidget.payload.report.AchievementQuarter;
import tw.com.leadtek.nhiwidget.payload.report.AchievementWeekly;
import tw.com.leadtek.nhiwidget.payload.report.DRGMonthlyPayload;
import tw.com.leadtek.nhiwidget.payload.report.DRGMonthlySectionPayload;
import tw.com.leadtek.nhiwidget.payload.report.DeductedPayload;
import tw.com.leadtek.nhiwidget.payload.report.NameCodePoint;
import tw.com.leadtek.nhiwidget.payload.report.NameCodePointQuantity;
import tw.com.leadtek.nhiwidget.payload.report.NameValueList;
import tw.com.leadtek.nhiwidget.payload.report.NameValueList2;
import tw.com.leadtek.nhiwidget.payload.report.NameValueList3;
import tw.com.leadtek.nhiwidget.payload.report.PeriodPointPayload;
import tw.com.leadtek.nhiwidget.payload.report.PeriodPointWeeklyPayload;
import tw.com.leadtek.nhiwidget.payload.report.PointMRPayload;
import tw.com.leadtek.nhiwidget.payload.report.PointPeriod;
import tw.com.leadtek.nhiwidget.payload.report.QuarterData;
import tw.com.leadtek.nhiwidget.payload.report.VisitsPeriod;
import tw.com.leadtek.nhiwidget.payload.report.VisitsPeriodDetail;
import tw.com.leadtek.nhiwidget.payload.report.VisitsVarietyPayload;
import tw.com.leadtek.tools.DateTool;

/**
 * 報表匯出專用service
 *
 */
@Service
public class ReportExportService {

	@Autowired
	private ReportService reportService;
	@Autowired
	private CODE_TABLEDao codeTableDao;

	public final static String FILE_PATH = "download";

	public void addRowCell(HSSFRow row, int num, String value, HSSFCellStyle cellStyle) {
		// 建立單元格,row已經確定了行號,列號作為引數傳遞給createCell(),第一列從0開始計算
		HSSFCell cell = row.createCell(num);
		// 設定單元格的值,即A1的值(第一行,第一列)
		cell.setCellValue(value);
		if (cellStyle != null) {
			cell.setCellStyle(cellStyle);
		}
	}

	/**
	 * 取得單月各科健保申報量與人次報表-匯出
	 * 
	 * @param year
	 * @param month
	 * @param response
	 * @throws IOException
	 */
	public void getMonthlyReportApplCountExport(int year, int month, HttpServletResponse response) throws IOException {
		String monthStr = "";
		if (month < 10) {
			monthStr = "0" + month;
		} else {
			monthStr = String.valueOf(month);
		}

		String inputDate = DateTool.convertToChineseYear(year + monthStr + "00");
		/// 轉成民國年月
		String endDate = inputDate.substring(0, inputDate.length() - 2);
		/// 呼叫上面api
		PointMRPayload pointData = new PointMRPayload();
		POINT_MONTHLY model = new POINT_MONTHLY();
		try {

			pointData = reportService.getMonthlyReportApplCount(year, month);
			model = pointData.getCurrent();
		} catch (Exception e) {
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("工作表");
			String fileNameStr = "單月各科健保申報量與人次報表" + "_" + endDate;
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
//			response.setContentType("application/vnd.ms-excel;charset=utf8");

			/// 最後由outputstream輸出
			OutputStream out = response.getOutputStream();

			workbook.write(out);
			out.flush();
			out.close();
			workbook.close();
			return;

		}

		String[] tableHeaderNum = { "門急診/住院", "門急診", "門診", "門診(早)", "門診(中)", "門診(晚)", "急診", "住院", "出院" };
		String[] tableCellHeader = { "單月各科人次比\n門急診/住院(含手術)", "人次", "比例", "", "單月各科人次比\n門急診(含手術)", "人次", "比例", "" };

		String sheetName = "單月各科健保申報量與人次報表" + "_" + endDate;

		// 建立新工作簿 sheet1
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 新建工作表
		HSSFSheet sheet = workbook.createSheet("單月各科健保申報量與人次報表");
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

		HSSFCellStyle cellFormatStyle = workbook.createCellStyle();
		cellFormatStyle.setAlignment(HorizontalAlignment.LEFT);
		cellFormatStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellFormatStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));

		Font font = workbook.createFont();
		// 建立行,行號作為引數傳遞給createRow()方法,第一行從0開始計算
		HSSFRow row = sheet.createRow(0);
		// 建立單元格,row已經確定了行號,列號作為引數傳遞給createCell(),第一列從0開始計算
		HSSFCell cell = row.createCell(0);
		// 設定單元格的值,即A1的值(第一行,第一列)
		cell.setCellValue("統計月份");

		cell = row.createCell(1);
		cell.setCellValue(year + "/" + monthStr);
		try {
			HSSFRow row2 = sheet.createRow(2);
			for (int i = 0; i < tableHeaderNum.length; i++) {
				HSSFCell cell2 = row2.createCell(1 + i);
				cell2.setCellValue(tableHeaderNum[i]);
				cell2.setCellStyle(cellStyle);
			}

			HSSFRow row3 = sheet.createRow(3);
			HSSFCell cell3 = row3.createCell(0);
			cell3.setCellValue("申報總點數");
			for (int i = 0; i < 9; i++) {
				HSSFCell cell3_2 = row3.createCell(1 + i);
				cell3_2.setCellStyle(cellFormatStyle);
				switch (i) {
				case 0:
					cell3_2.setCellValue(model.getTotalAll().doubleValue());
					break;
				case 1:
					cell3_2.setCellValue(model.getTotalOpAll().doubleValue());
					break;
				case 2:
					cell3_2.setCellValue(model.getTotalOp().doubleValue());
					break;
				case 3:
					break;
				case 4:
					break;
				case 5:
					break;
				case 6:
					cell3_2.setCellValue(model.getTotalEm().doubleValue());
					break;
				case 7:
					cell3_2.setCellValue(model.getTotalIp().doubleValue());
					break;
				case 8:
					cell3_2.setCellValue("-");
					break;
				default:
					break;

				}

			}

			HSSFRow row4 = sheet.createRow(4);
			HSSFCell cell4 = row4.createCell(0);
			cell4.setCellValue("總人次(含手術)");
			// todo
			for (int i = 0; i < 9; i++) {
				HSSFCell cell4_2 = row4.createCell(1 + i);
				cell4_2.setCellStyle(cellFormatStyle);
				switch (i) {
				case 0:
					cell4_2.setCellValue(Double.valueOf(pointData.getPatient_total_count()));
					break;
				case 1:
					cell4_2.setCellValue(Double.valueOf(pointData.getPatient_op_count()));
					break;
				case 2:
					cell4_2.setCellValue(model.getPatientOp().doubleValue());
					break;
				case 3:
					break;
				case 4:
					break;
				case 5:
					break;
				case 6:
					cell4_2.setCellValue(model.getPatientEm().doubleValue());
					break;
				case 7:
					cell4_2.setCellValue(Double.valueOf(pointData.getPatient_ip_count()));
					break;
				case 8:
					cell4_2.setCellValue(model.getPatientIp().doubleValue());
					break;
				default:
					break;

				}

			}

			HSSFRow row6 = sheet.createRow(6);
			HSSFCell cell6 = row6.createCell(0);
			cell6.setCellValue("單月各科人次比\n門急診/住院(含手術)");
			HSSFCellStyle cellStyle6 = workbook.createCellStyle();
			cellStyle6.setWrapText(true);
			cell6.setCellStyle(cellStyle6);
			row6.setHeightInPoints(2 * sheet.getDefaultRowHeightInPoints());
			for (int i = 0; i < pointData.getTotalPieCountData().size(); i++) {
				HSSFCell cell6_2 = row6.createCell(1 + i);
				Map<String, Object> map = pointData.getTotalPieCountData().get(i);
				cell6_2.setCellValue(map.get("DESC_CHI").toString());
				HSSFCellStyle cellStyle6_2 = workbook.createCellStyle();
				cellStyle6_2.setFont(font);
				cell6_2.setCellStyle(cellStyle6_2);
			}

			HSSFRow row7 = sheet.createRow(7);
			HSSFCell cell7 = row7.createCell(0);
			cell7.setCellValue("人次");
			for (int i = 0; i < pointData.getTotalPieCountData().size(); i++) {
				HSSFCell cell7_2 = row7.createCell(1 + i);
				Map<String, Object> map = pointData.getTotalPieCountData().get(i);
				cell7_2.setCellValue(Double.valueOf(map.get("COUNT").toString()));
				cell7_2.setCellStyle(cellFormatStyle);
			}

			HSSFRow row8 = sheet.createRow(8);
			HSSFCell cell8 = row8.createCell(0);
			cell8.setCellValue("比例");
			for (int i = 0; i < pointData.getTotalPieCountData().size(); i++) {
				HSSFCell cell8_2 = row8.createCell(1 + i);
				Map<String, Object> map = pointData.getTotalPieCountData().get(i);
				float f = Float.valueOf(map.get("PERCENT").toString());
				String str = String.format("%.02f", f);
				cell8_2.setCellValue(str + "%");
			}

			HSSFRow row10 = sheet.createRow(10);
			HSSFCell cell10 = row10.createCell(0);
			cell10.setCellValue("單月各科人次比\n門急診(含手術)");
			HSSFCellStyle cellStyle10 = workbook.createCellStyle();
			cellStyle10.setWrapText(true);
			cell10.setCellStyle(cellStyle6);
			row10.setHeightInPoints(2 * sheet.getDefaultRowHeightInPoints());
			for (int i = 0; i < pointData.getOpPieCountData().size(); i++) {
				HSSFCell cell10_2 = row10.createCell(1 + i);
				Map<String, Object> map = pointData.getOpPieCountData().get(i);
				cell10_2.setCellValue(map.get("DESC_CHI").toString());
				HSSFCellStyle cellStyle10_2 = workbook.createCellStyle();
				cellStyle10_2.setFont(font);
				cell10_2.setCellStyle(cellStyle10_2);
			}

			HSSFRow row11 = sheet.createRow(11);
			HSSFCell cell11 = row11.createCell(0);
			cell11.setCellValue("人次");
			for (int i = 0; i < pointData.getOpPieCountData().size(); i++) {
				HSSFCell cell11_2 = row11.createCell(1 + i);
				Map<String, Object> map = pointData.getOpPieCountData().get(i);
				cell11_2.setCellValue(Double.valueOf(map.get("COUNT").toString()));
				cell11_2.setCellStyle(cellFormatStyle);
			}
			HSSFRow row12 = sheet.createRow(12);
			HSSFCell cell12 = row12.createCell(0);
			cell12.setCellValue("比例");
			for (int i = 0; i < pointData.getOpPieCountData().size(); i++) {
				HSSFCell cell12_2 = row12.createCell(1 + i);
				Map<String, Object> map = pointData.getOpPieCountData().get(i);
				float f = Float.valueOf(map.get("PERCENT").toString());
				String str = String.format("%.02f", f);
				cell12_2.setCellValue(str + "%");
			}
			///
			HSSFRow row14 = sheet.createRow(14);
			HSSFCell cell14 = row14.createCell(0);
			cell14.setCellValue("單月各科人次比\n住院(含手術)");
			HSSFCellStyle cellStyle14 = workbook.createCellStyle();
			cellStyle14.setWrapText(true);
			cell14.setCellStyle(cellStyle14);
			row14.setHeightInPoints(2 * sheet.getDefaultRowHeightInPoints());
			for (int i = 0; i < pointData.getIpPieCountData().size(); i++) {
				HSSFCell cell14_2 = row14.createCell(1 + i);
				Map<String, Object> map = pointData.getIpPieCountData().get(i);
				cell14_2.setCellValue(map.get("DESC_CHI").toString());
				HSSFCellStyle cellStyle14_2 = workbook.createCellStyle();
				cellStyle14_2.setFont(font);
				cell14_2.setCellStyle(cellStyle14_2);
			}

			HSSFRow row15 = sheet.createRow(15);
			HSSFCell cell15 = row15.createCell(0);
			cell15.setCellValue("人次");
			for (int i = 0; i < pointData.getIpPieCountData().size(); i++) {
				HSSFCell cell15_2 = row15.createCell(1 + i);
				Map<String, Object> map = pointData.getIpPieCountData().get(i);
				cell15_2.setCellValue(Double.valueOf(map.get("COUNT").toString()));
				cell15_2.setCellStyle(cellFormatStyle);
			}
			HSSFRow row16 = sheet.createRow(16);
			HSSFCell cell16 = row16.createCell(0);
			cell16.setCellValue("比例");
			for (int i = 0; i < pointData.getIpPieCountData().size(); i++) {
				HSSFCell cell16_2 = row16.createCell(1 + i);
				Map<String, Object> map = pointData.getIpPieCountData().get(i);
				float f = Float.valueOf(map.get("PERCENT").toString());
				String str = String.format("%.02f", f);
				cell16_2.setCellValue(str + "%");
			}
			///
			HSSFRow row18 = sheet.createRow(18);
			HSSFCell cell18 = row18.createCell(0);
			cell18.setCellValue("單月各科人次比\n出院(含手術)");
			HSSFCellStyle cellStyle18 = workbook.createCellStyle();
			cellStyle18.setWrapText(true);
			cell18.setCellStyle(cellStyle18);
			row18.setHeightInPoints(2 * sheet.getDefaultRowHeightInPoints());
			for (int i = 0; i < pointData.getIpPieOutCountData().size(); i++) {
				HSSFCell cell18_2 = row18.createCell(1 + i);
				Map<String, Object> map = pointData.getIpPieOutCountData().get(i);
				cell18_2.setCellValue(map.get("DESC_CHI").toString());
				HSSFCellStyle cellStyle18_2 = workbook.createCellStyle();
				cellStyle18_2.setFont(font);
				cell18_2.setCellStyle(cellStyle18_2);
			}

			HSSFRow row19 = sheet.createRow(19);
			HSSFCell cell19 = row19.createCell(0);
			cell19.setCellValue("人次");
			for (int i = 0; i < pointData.getIpPieOutCountData().size(); i++) {
				HSSFCell cell19_2 = row19.createCell(1 + i);
				Map<String, Object> map = pointData.getIpPieOutCountData().get(i);
				cell19_2.setCellValue(Double.valueOf(map.get("COUNT").toString()));
				cell19_2.setCellStyle(cellFormatStyle);
			}
			HSSFRow row20 = sheet.createRow(20);
			HSSFCell cell20 = row20.createCell(0);
			cell20.setCellValue("比例");
			for (int i = 0; i < pointData.getIpPieOutCountData().size(); i++) {
				HSSFCell cell20_2 = row20.createCell(1 + i);
				Map<String, Object> map = pointData.getIpPieOutCountData().get(i);
				float f = Float.valueOf(map.get("PERCENT").toString());
				String str = String.format("%.02f", f);
				cell20_2.setCellValue(str + "%");
			}

			///
			HSSFRow row22 = sheet.createRow(22);
			HSSFCell cell22 = row22.createCell(0);
			cell22.setCellValue("單月各科申報點數比\n門急診/出院(含手術)");
			HSSFCellStyle cellStyle22 = workbook.createCellStyle();
			cellStyle22.setWrapText(true);
			cell22.setCellStyle(cellStyle22);
			row22.setHeightInPoints(2 * sheet.getDefaultRowHeightInPoints());
			for (int i = 0; i < pointData.getTotalPieDotData().size(); i++) {
				HSSFCell cell22_2 = row22.createCell(1 + i);
				Map<String, Object> map = pointData.getTotalPieDotData().get(i);
				cell22_2.setCellValue(map.get("DESC_CHI").toString());
				HSSFCellStyle cellStyle22_2 = workbook.createCellStyle();
				cellStyle22_2.setFont(font);
				cell22_2.setCellStyle(cellStyle22_2);
			}

			HSSFRow row23 = sheet.createRow(23);
			HSSFCell cell23 = row23.createCell(0);
			cell23.setCellValue("點數");
			for (int i = 0; i < pointData.getTotalPieDotData().size(); i++) {
				HSSFCell cell23_2 = row23.createCell(1 + i);
				Map<String, Object> map = pointData.getTotalPieDotData().get(i);
				cell23_2.setCellValue(Double.valueOf(map.get("SUM").toString()));
				cell23_2.setCellStyle(cellFormatStyle);
			}
			HSSFRow row24 = sheet.createRow(24);
			HSSFCell cell24 = row24.createCell(0);
			cell24.setCellValue("比例");
			for (int i = 0; i < pointData.getTotalPieDotData().size(); i++) {
				HSSFCell cell24_2 = row24.createCell(1 + i);
				Map<String, Object> map = pointData.getTotalPieDotData().get(i);
				float f = Float.valueOf(map.get("PERCENT").toString());
				String str = String.format("%.02f", f);
				cell24_2.setCellValue(str + "%");
			}

			///
			HSSFRow row26 = sheet.createRow(26);
			HSSFCell cell26 = row26.createCell(0);
			cell26.setCellValue("單月各科申報點數比\n門急診(含手術)");
			HSSFCellStyle cellStyle26 = workbook.createCellStyle();
			cellStyle26.setWrapText(true);
			cell26.setCellStyle(cellStyle26);
			row26.setHeightInPoints(2 * sheet.getDefaultRowHeightInPoints());
			for (int i = 0; i < pointData.getOpPieDotData().size(); i++) {
				HSSFCell cell26_2 = row26.createCell(1 + i);
				Map<String, Object> map = pointData.getOpPieDotData().get(i);
				cell26_2.setCellValue(map.get("DESC_CHI").toString());
				HSSFCellStyle cellStyle26_2 = workbook.createCellStyle();
				cellStyle26_2.setFont(font);
				cell26_2.setCellStyle(cellStyle26_2);
			}

			HSSFRow row27 = sheet.createRow(27);
			HSSFCell cell27 = row27.createCell(0);
			cell27.setCellValue("點數");
			for (int i = 0; i < pointData.getOpPieDotData().size(); i++) {
				HSSFCell cell27_2 = row27.createCell(1 + i);
				Map<String, Object> map = pointData.getOpPieDotData().get(i);
				cell27_2.setCellValue(Double.valueOf(map.get("SUM").toString()));
				cell27_2.setCellStyle(cellFormatStyle);
			}
			HSSFRow row28 = sheet.createRow(28);
			HSSFCell cell28 = row28.createCell(0);
			cell28.setCellValue("比例");
			for (int i = 0; i < pointData.getOpPieDotData().size(); i++) {
				HSSFCell cell28_2 = row28.createCell(1 + i);
				Map<String, Object> map = pointData.getOpPieDotData().get(i);
				float f = Float.valueOf(map.get("PERCENT").toString());
				String str = String.format("%.02f", f);
				cell28_2.setCellValue(str + "%");
			}

			///
			HSSFRow row30 = sheet.createRow(30);
			HSSFCell cell30 = row30.createCell(0);
			cell30.setCellValue("單月各科申報點數比\n住院(含手術)");
			HSSFCellStyle cellStyle30 = workbook.createCellStyle();
			cellStyle30.setWrapText(true);
			cell30.setCellStyle(cellStyle30);
			row30.setHeightInPoints(2 * sheet.getDefaultRowHeightInPoints());
			for (int i = 0; i < pointData.getIpPieDotData().size(); i++) {
				HSSFCell cell30_2 = row30.createCell(1 + i);
				Map<String, Object> map = pointData.getIpPieDotData().get(i);
				cell30_2.setCellValue(map.get("DESC_CHI").toString());
				HSSFCellStyle cellStyle30_2 = workbook.createCellStyle();
				cellStyle30_2.setFont(font);
				cell30_2.setCellStyle(cellStyle30_2);
			}

			HSSFRow row31 = sheet.createRow(31);
			HSSFCell cell31 = row31.createCell(0);
			cell31.setCellValue("點數");
			for (int i = 0; i < pointData.getIpPieDotData().size(); i++) {
				HSSFCell cell31_2 = row31.createCell(1 + i);
				Map<String, Object> map = pointData.getIpPieDotData().get(i);
				cell31_2.setCellValue(Double.valueOf(map.get("SUM").toString()));
				cell31_2.setCellStyle(cellFormatStyle);
			}
			HSSFRow row32 = sheet.createRow(32);
			HSSFCell cell32 = row32.createCell(0);
			cell32.setCellValue("比例");
			for (int i = 0; i < pointData.getIpPieDotData().size(); i++) {
				HSSFCell cell32_2 = row32.createCell(1 + i);
				Map<String, Object> map = pointData.getIpPieDotData().get(i);
				float f = Float.valueOf(map.get("PERCENT").toString());
				String str = String.format("%.02f", f);
				cell32_2.setCellValue(str + "%");
			}

			/// 最後設定autosize
			for (int i = 0; i < pointData.getTotalPieDotData().size(); i++) {
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
			}
			/// 新建工作表
			String[] tableHeader = { "門急診/住院申報總點數趨勢圖", "", "", "門急診申報總點數趨勢圖", "", "", "住院申報總點數趨勢圖", "", "", "門急診人數趨勢圖",
					"", "住院人數趨勢圖", "", "出院人數趨勢圖", "" };
			String[] tableHeader2 = { "週數", "點數", "案件數", "週數", "點數", "案件數", "週數", "點數", "案件數", "週數", "人次", "週數", "人次",
					"週數", "人次" };
			VisitsVarietyPayload model2 = pointData.getVisitsVarietyPayload();
			List<String> functypes = pointData.getFuncTypes();
			int funCount = 0;
			for (String str : functypes) {
				if (str.equals("不分科")) {
					if (funCount == 0) {
						int cellIndex = 0;
						/// 第二頁籤 sheet2
						sheet = workbook.createSheet("申報點數趨勢圖(全院)");
						// 建立行,行號作為引數傳遞給createRow()方法,第一行從0開始計算
						row = sheet.createRow(0);
						// 建立單元格,row已經確定了行號,列號作為引數傳遞給createCell(),第一列從0開始計算
						cell = row.createCell(0);
						// 設定單元格的值,即A1的值(第一行,第一列)
						cell.setCellValue("全院");
						HSSFRow row1 = sheet.createRow(1);
						CellStyle style1 = workbook.createCellStyle();
						style1.setAlignment(HorizontalAlignment.CENTER);// 水平置中
						style1.setVerticalAlignment(VerticalAlignment.CENTER);

						for (int i = 0; i < tableHeader.length; i++) {
							HSSFCell cell1 = row1.createCell(i);

							switch (i) {
							case 0:
								cell1 = row1.createCell(i);
								cell1.setCellValue(tableHeader[i]);
								cell1.setCellStyle(style1);
								sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 2));
								break;
							case 3:
								cell1 = row1.createCell(i);
								cell1.setCellValue(tableHeader[i]);
								cell1.setCellStyle(style1);
								sheet.addMergedRegion(new CellRangeAddress(1, 1, 3, 5));
								break;
							case 6:
								cell1 = row1.createCell(i);
								cell1.setCellValue(tableHeader[i]);
								cell1.setCellStyle(style1);
								sheet.addMergedRegion(new CellRangeAddress(1, 1, 6, 8));
								break;
							case 9:
								cell1 = row1.createCell(i);
								cell1.setCellValue(tableHeader[i]);
								cell1.setCellStyle(style1);
								sheet.addMergedRegion(new CellRangeAddress(1, 1, 9, 10));
								break;
							case 11:
								cell1 = row1.createCell(i);
								cell1.setCellValue(tableHeader[i]);
								cell1.setCellStyle(style1);
								sheet.addMergedRegion(new CellRangeAddress(1, 1, 11, 12));
								break;
							case 13:
								cell1 = row1.createCell(i);
								cell1.setCellValue(tableHeader[i]);
								cell1.setCellStyle(style1);
								sheet.addMergedRegion(new CellRangeAddress(1, 1, 13, 14));
								break;
							default:
								break;
							}
						}
						row2 = sheet.createRow(2);
						for (int i = 0; i < tableHeader2.length; i++) {
							HSSFCell cell2 = row2.createCell(i);
							cell2.setCellValue(tableHeader2[i]);
							cell2.setCellStyle(style1);
						}
						cellIndex = 0;
						NameValueList3 nvlAll3 = model2.getAllMap3().get(str);
						NameValueList3 nvlOp3 = model2.getOpemMap3().get(str);
						NameValueList3 nvlip3 = model2.getIpMap3().get(str);
						NameValueList nvlLeave = model2.getLeaveMap().get(str);
						NameValueList nvlOp = model2.getOpemMap().get(str);
						NameValueList nvlIp = model2.getIpMap().get(str);

						HSSFRow rows = sheet.createRow(3);
						/// 不知為何，poi如果直向寫入會發生值消失問題，這邊用一般橫向資料增長
						for (int i = 0; i < nvlAll3.getNames().size(); i++) {
							HSSFCell cells = rows.createCell(cellIndex + i);
							cells.setCellValue(nvlAll3.getNames().get(i));
							cellIndex++;
							cells = rows.createCell(cellIndex + i);
							cells.setCellValue(nvlAll3.getValues2().get(i).doubleValue());
							cells.setCellStyle(cellFormatStyle);
							cellIndex++;
							cells = rows.createCell(cellIndex + i);
							cells.setCellValue(nvlAll3.getValues().get(i).doubleValue());
							cells.setCellStyle(cellFormatStyle);
							cellIndex++;
							cells = rows.createCell(cellIndex + i);
							cells.setCellValue(nvlOp3.getNames().get(i));
							cellIndex++;
							cells = rows.createCell(cellIndex + i);
							cells.setCellValue(nvlOp3.getValues2().get(i).doubleValue());
							cells.setCellStyle(cellFormatStyle);
							cellIndex++;
							cells = rows.createCell(cellIndex + i);
							cells.setCellValue(nvlOp3.getValues().get(i).doubleValue());
							cells.setCellStyle(cellFormatStyle);
							cellIndex++;
							cells = rows.createCell(cellIndex + i);
							cells.setCellValue(nvlip3.getNames().get(i));
							cellIndex++;
							cells = rows.createCell(cellIndex + i);
							cells.setCellValue(nvlip3.getValues2().get(i).doubleValue());
							cells.setCellStyle(cellFormatStyle);
							cellIndex++;
							cells = rows.createCell(cellIndex + i);
							cells.setCellValue(nvlip3.getValues().get(i).doubleValue());
							cells.setCellStyle(cellFormatStyle);
							cellIndex++;
							cells = rows.createCell(cellIndex + i);
							cells.setCellValue(nvlOp.getNames().get(i));
							cellIndex++;
							cells = rows.createCell(cellIndex + i);
							cells.setCellValue(nvlOp.getValues().get(i).doubleValue());
							cells.setCellStyle(cellFormatStyle);
							cellIndex++;
							cells = rows.createCell(cellIndex + i);
							cells.setCellValue(nvlIp.getNames().get(i));
							cellIndex++;
							cells = rows.createCell(cellIndex + i);
							cells.setCellValue(nvlIp.getValues().get(i).doubleValue());
							cells.setCellStyle(cellFormatStyle);
							cellIndex++;
							cells = rows.createCell(cellIndex + i);
							cells.setCellValue(nvlLeave.getNames().get(i));
							cellIndex++;
							cells = rows.createCell(cellIndex + i);
							cells.setCellValue(nvlLeave.getValues().get(i).doubleValue());
							cells.setCellStyle(cellFormatStyle);
							rows = sheet.createRow(4 + i);
							cellIndex = 0;
							cellIndex--;
							if (i >= 1) {
								cellIndex -= i;
							}
							cells.setCellStyle(cellFormatStyle);
						}
						/// auto size
						for (int i = 0; i < tableHeader.length; i++) {
							sheet.autoSizeColumn(i);
							sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
						}
						funCount++;
					}

				}
			}

			for (String str : functypes) {
				if (!str.equals("不分科")) {
					/// 略過沒有資料的科別
					if (model2.getAllMap3().get(str) == null) {
						continue;
					}
					int cellIndex = 0;
					/// sheet3.....n
					sheet = workbook.createSheet("申報點數趨勢圖(" + str + ")");
					// 建立行,行號作為引數傳遞給createRow()方法,第一行從0開始計算
					row = sheet.createRow(0);
					// 建立單元格,row已經確定了行號,列號作為引數傳遞給createCell(),第一列從0開始計算
					cell = row.createCell(0);
					// 設定單元格的值,即A1的值(第一行,第一列)
					cell.setCellValue(str);
					HSSFRow row1 = sheet.createRow(1);
					CellStyle style1 = workbook.createCellStyle();
					style1.setAlignment(HorizontalAlignment.CENTER);// 水平置中
					style1.setVerticalAlignment(VerticalAlignment.CENTER);

					for (int i = 0; i < tableHeader.length; i++) {
						HSSFCell cell1 = row1.createCell(i);

						switch (i) {
						case 0:
							cell1 = row1.createCell(i);
							cell1.setCellValue(tableHeader[i]);
							cell1.setCellStyle(style1);
							sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 2));
							break;
						case 3:
							cell1 = row1.createCell(i);
							cell1.setCellValue(tableHeader[i]);
							cell1.setCellStyle(style1);
							sheet.addMergedRegion(new CellRangeAddress(1, 1, 3, 5));
							break;
						case 6:
							cell1 = row1.createCell(i);
							cell1.setCellValue(tableHeader[i]);
							cell1.setCellStyle(style1);
							sheet.addMergedRegion(new CellRangeAddress(1, 1, 6, 8));
							break;
						case 9:
							cell1 = row1.createCell(i);
							cell1.setCellValue(tableHeader[i]);
							cell1.setCellStyle(style1);
							sheet.addMergedRegion(new CellRangeAddress(1, 1, 9, 10));
							break;
						case 11:
							cell1 = row1.createCell(i);
							cell1.setCellValue(tableHeader[i]);
							cell1.setCellStyle(style1);
							sheet.addMergedRegion(new CellRangeAddress(1, 1, 11, 12));
							break;
						case 13:
							cell1 = row1.createCell(i);
							cell1.setCellValue(tableHeader[i]);
							cell1.setCellStyle(style1);
							sheet.addMergedRegion(new CellRangeAddress(1, 1, 13, 14));
							break;
						default:
							break;
						}
					}
					row2 = sheet.createRow(2);
					for (int i = 0; i < tableHeader2.length; i++) {
						HSSFCell cell2 = row2.createCell(i);
						cell2.setCellValue(tableHeader2[i]);
						cell2.setCellStyle(style1);
					}
					cellIndex = 0;
					NameValueList3 nvlAll3 = model2.getAllMap3().get(str);
					NameValueList3 nvlOp3 = model2.getOpemMap3().get(str);
					NameValueList3 nvlip3 = model2.getIpMap3().get(str);
					NameValueList nvlLeave = model2.getLeaveMap().get(str);
					NameValueList nvlOp = model2.getOpemMap().get(str);
					NameValueList nvlIp = model2.getIpMap().get(str);
					HSSFRow rows = sheet.createRow(3);
					/// 不知為何，poi如果直向寫入會發生值消失問題，這邊用一般橫向資料增長
					for (int i = 0; i < nvlAll3.getNames().size(); i++) {
						HSSFCell cells = rows.createCell(cellIndex + i);
						cells.setCellValue(nvlAll3.getNames().get(i));
						cellIndex++;
						cells = rows.createCell(cellIndex + i);
						cells.setCellValue(nvlAll3.getValues2().get(i).doubleValue());
						cells.setCellStyle(cellFormatStyle);
						cellIndex++;
						cells = rows.createCell(cellIndex + i);
						cells.setCellValue(nvlAll3.getValues().get(i).doubleValue());
						cells.setCellStyle(cellFormatStyle);
						cellIndex++;
						cells = rows.createCell(cellIndex + i);
						cells.setCellValue(nvlOp3.getNames().get(i));
						cellIndex++;
						cells = rows.createCell(cellIndex + i);
						cells.setCellValue(nvlOp3.getValues2().get(i).doubleValue());
						cells.setCellStyle(cellFormatStyle);
						cellIndex++;
						cells = rows.createCell(cellIndex + i);
						cells.setCellValue(nvlOp3.getValues().get(i).doubleValue());
						cells.setCellStyle(cellFormatStyle);
						cellIndex++;
						cells = rows.createCell(cellIndex + i);
						cells.setCellValue(nvlip3.getNames().get(i));
						cellIndex++;
						cells = rows.createCell(cellIndex + i);
						cells.setCellValue(nvlip3.getValues2().get(i).doubleValue());
						cells.setCellStyle(cellFormatStyle);
						cellIndex++;
						cells = rows.createCell(cellIndex + i);
						cells.setCellValue(nvlip3.getValues().get(i).doubleValue());
						cells.setCellStyle(cellFormatStyle);
						cellIndex++;
						cells = rows.createCell(cellIndex + i);
						cells.setCellValue(nvlOp.getNames().get(i));
						cellIndex++;
						cells = rows.createCell(cellIndex + i);
						cells.setCellValue(nvlOp.getValues().get(i).doubleValue());
						cells.setCellStyle(cellFormatStyle);
						cellIndex++;
						cells = rows.createCell(cellIndex + i);
						cells.setCellValue(nvlIp.getNames().get(i));
						cellIndex++;
						cells = rows.createCell(cellIndex + i);
						cells.setCellValue(nvlIp.getValues().get(i).doubleValue());
						cells.setCellStyle(cellFormatStyle);
						cellIndex++;
						cells = rows.createCell(cellIndex + i);
						cells.setCellValue(nvlLeave.getNames().get(i));
						cellIndex++;
						cells = rows.createCell(cellIndex + i);
						cells.setCellValue(nvlLeave.getValues().get(i).doubleValue());
						cells.setCellStyle(cellFormatStyle);
						rows = sheet.createRow(4 + i);
						cellIndex = 0;
						cellIndex--;
						if (i >= 1) {
							cellIndex -= i;
						}
						cells.setCellStyle(cellFormatStyle);

					}
					/// auto size
					for (int i = 0; i < tableHeader.length; i++) {
						sheet.autoSizeColumn(i);
						sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
					}

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		String fileNameStr = "單月各科健保申報量與人次報表" + "_" + endDate;
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
//		response.setContentType("application/vnd.ms-excel;charset=utf8");

		/// 最後由outputstream輸出
		OutputStream out = response.getOutputStream();

		workbook.write(out);
		out.flush();
		out.close();
		workbook.close();

	}

	/**
	 * 取得健保點數月報表-匯出
	 * 
	 * @param year
	 * @param month
	 * @param response
	 * @throws IOException
	 */
	public void getMonthlyReportExport(int year, int month, String type, HttpServletResponse response)
			throws IOException {

		String monthStr = "";
		if (month < 10) {
			monthStr = "0" + month;
		} else {
			monthStr = String.valueOf(month);
		}

		String inputDate = DateTool.convertToChineseYear(year + monthStr + "00");
		/// 轉成民國年月
		String endDate = inputDate.substring(0, inputDate.length() - 2);
		PointMRPayload pointData = new PointMRPayload();
		try {
			pointData = reportService.getMonthlyReport(year, month);
		} catch (Exception e) {

			// 建立新工作簿
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("工作表");
			String fileNameStr = "單月健保點數總表" + "_" + endDate;
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
//			response.setContentType("application/vnd.ms-excel;charset=utf8");

			/// 最後由outputstream輸出
			OutputStream out = response.getOutputStream();

			workbook.write(out);
			out.flush();
			out.close();
			workbook.close();
		}

		POINT_MONTHLY cModel = pointData.getCurrent();
		POINT_MONTHLY yModel = pointData.getLastY();
		POINT_MONTHLY mModel = pointData.getLastM();

		String dateStr = year + "/" + monthStr;

		String[] tableCellHeaders1 = { "門急診/住院", "門急診", "門診", "急診", "住院" };
		String[] tableRowHeaders1 = { "統計月份", "慢籤額度條件", "分配額度", "申請點數(含慢籤預留)", "部分負擔點數", "慢籤預留總額度", "剩餘額度", "", "",
				"累計部分負擔", "累計申請點數", "當月申報點數", "分配額度點數", "總額達成率" };

		// 建立新工作簿
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 新建工作表
		HSSFSheet sheet = workbook.createSheet("單月健保點數總表");
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setBorderBottom(BorderStyle.MEDIUM);
		cellStyle.setBorderTop(BorderStyle.MEDIUM);
		cellStyle.setBorderLeft(BorderStyle.MEDIUM);
		cellStyle.setBorderRight(BorderStyle.MEDIUM);

		HSSFCellStyle cellHeadStyle = workbook.createCellStyle();
		cellHeadStyle.setAlignment(HorizontalAlignment.CENTER);
		cellHeadStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellHeadStyle.setBorderBottom(BorderStyle.MEDIUM);
		cellHeadStyle.setBorderTop(BorderStyle.MEDIUM);
		cellHeadStyle.setBorderLeft(BorderStyle.MEDIUM);
		cellHeadStyle.setBorderRight(BorderStyle.MEDIUM);

		HSSFCellStyle cellHeadDataStyle = workbook.createCellStyle();
		cellHeadDataStyle.setAlignment(HorizontalAlignment.LEFT);
		cellHeadDataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellHeadDataStyle.setBorderBottom(BorderStyle.MEDIUM);
		cellHeadDataStyle.setBorderTop(BorderStyle.MEDIUM);
		cellHeadDataStyle.setBorderLeft(BorderStyle.MEDIUM);
		cellHeadDataStyle.setBorderRight(BorderStyle.MEDIUM);

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
		try {
			for (int y = 0; y < tableRowHeaders1.length; y++) {
				HSSFRow rows = sheet.createRow(y);
				HSSFCell cells = rows.createCell(cellIndex);
				cells.setCellValue(tableRowHeaders1[y]);
				cells.setCellStyle(cellStyle);

				cellIndex++;
				cells = rows.createCell(cellIndex);
				if (cModel != null) {

					switch (y) {
					case 0:
						cells.setCellValue(dateStr);
						cells.setCellStyle(cellHeadDataStyle);
						break;
					case 1:
						cells.setCellValue(type);
						cells.setCellStyle(cellHeadDataStyle);
						break;
					case 2:
						cells.setCellValue(cModel.getAssignedAll().doubleValue());
						cells.setCellStyle(cellFormatStyle);
						break;
					case 3:
						cells.setCellValue(cModel.getApplAll().doubleValue());
						cells.setCellStyle(cellFormatStyle);
						break;
					case 4:
						cells.setCellValue(cModel.getPartAll().doubleValue());
						cells.setCellStyle(cellFormatStyle);
						break;
					case 5:
						cells.setCellValue(cModel.getChronic().doubleValue());
						cells.setCellStyle(cellFormatStyle);
						break;
					case 6:
						cells.setCellValue(cModel.getRemaining().doubleValue());
						cells.setCellStyle(cellFormatStyle);
						break;

					case 8:
						for (int x = 0; x < tableCellHeaders1.length; x++) {
							cells = rows.createCell(cellIndex + x);
							cells.setCellValue(tableCellHeaders1[x]);
							cells.setCellStyle(cellHeadStyle);
						}
						break;
					case 9:
						for (int x = 0; x < tableCellHeaders1.length; x++) {
							cells = rows.createCell(cellIndex + x);
							switch (x) {
							case 0:
								cells.setCellValue(cModel.getPartAll().doubleValue());
								break;
							case 1:
								cells.setCellValue(cModel.getPartOpAll().doubleValue());
								break;
							case 2:
								cells.setCellValue(cModel.getPartOp().doubleValue());
								break;
							case 3:
								cells.setCellValue(cModel.getPartEm().doubleValue());
								break;
							case 4:
								cells.setCellValue(cModel.getPartIp().doubleValue());
								break;
							default:
								break;
							}
							cells.setCellStyle(cellFormatStyle);
						}
						break;
					case 10:
						for (int x = 0; x < tableCellHeaders1.length; x++) {
							cells = rows.createCell(cellIndex + x);
							switch (x) {
							case 0:
								cells.setCellValue(cModel.getApplAll().doubleValue());
								break;
							case 1:
								cells.setCellValue(cModel.getApplOpAll().doubleValue());
								break;
							case 2:
								cells.setCellValue(cModel.getApplOp().doubleValue());
								break;
							case 3:
								cells.setCellValue(cModel.getApplEm().doubleValue());
								break;
							case 4:
								cells.setCellValue(cModel.getApplIp().doubleValue());
								break;
							default:
								break;
							}
							cells.setCellStyle(cellFormatStyle);
						}
						break;
					case 11:
						for (int x = 0; x < tableCellHeaders1.length; x++) {
							cells = rows.createCell(cellIndex + x);
							switch (x) {
							case 0:
								cells.setCellValue(cModel.getTotalAll().doubleValue());
								break;
							case 1:
								cells.setCellValue(cModel.getTotalOpAll().doubleValue());
								break;
							case 2:
								cells.setCellValue(cModel.getTotalOp().doubleValue());
								break;
							case 3:
								cells.setCellValue(cModel.getTotalEm().doubleValue());
								break;
							case 4:
								cells.setCellValue(cModel.getTotalIp().doubleValue());
								break;
							default:
								break;
							}
							cells.setCellStyle(cellFormatStyle);
						}
						break;
					case 12:
						for (int x = 0; x < tableCellHeaders1.length; x++) {
							cells = rows.createCell(cellIndex + x);
							switch (x) {
							case 0:
								cells.setCellValue(cModel.getAssignedAll().doubleValue());
								break;
							case 1:
								cells.setCellValue(cModel.getAssignedOpAll().doubleValue());
								break;
							case 2:
								cells.setCellValue("-");
								break;
							case 3:
								cells.setCellValue("-");
								break;
							case 4:
								cells.setCellValue(cModel.getAssignedIp().doubleValue());
								break;
							default:
								break;
							}
							cells.setCellStyle(cellFormatStyle);
						}
						break;
					case 13:
						for (int x = 0; x < tableCellHeaders1.length; x++) {
							cells = rows.createCell(cellIndex + x);
							switch (x) {
							case 0:
								cells.setCellValue(String.valueOf(cModel.getRateAll()) + "%");
								break;
							case 1:
								cells.setCellValue(String.valueOf(cModel.getRateOpAll()) + "%");
								break;
							case 2:
								cells.setCellValue("-");
								break;
							case 3:
								cells.setCellValue("-");
								break;
							case 4:
								cells.setCellValue(String.valueOf(cModel.getRateIp()) + "%");
								break;
							default:
								break;
							}
							cells.setCellStyle(cellHeadDataStyle);
						}
						break;
					default:
						break;
					}
				}

				cellIndex = 0;
			}
			/// 最後設定autosize
			for (int i = 0; i < tableRowHeaders1.length; i++) {
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
			}
			cellIndex = 0;
			rowIndex = 0;
			String[] tableCellHeders2 = { "單月", "上月", "去年同期" };
			String[] tableRowHeders2 = { "門急診／住院", "申報點數", "分配點數", "上月差額", "去年同期差額", "門急診", "申報點數", "分配點數", "上月差額",
					"去年同期差額", "門診", "申報點數", "分配點數", "上月差額", "去年同期差額", "急診", "申報點數", "分配點數", "上月差額", "去年同期差額", "住院",
					"申報點數", "分配點數", "上月差額", "去年同期差額" };
			// 新建工作表 sheet2
			sheet = workbook.createSheet("門急診和住院");
			Font font = workbook.createFont();
//		font.setColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
			font.setBold(true);

			HSSFCellStyle cellStyle2 = workbook.createCellStyle();
			cellStyle2.setBorderBottom(BorderStyle.MEDIUM);
			cellStyle2.setBorderTop(BorderStyle.MEDIUM);
			cellStyle2.setBorderLeft(BorderStyle.MEDIUM);
			cellStyle2.setBorderRight(BorderStyle.MEDIUM);
			cellStyle2.setAlignment(HorizontalAlignment.LEFT);
			cellStyle2.setVerticalAlignment(VerticalAlignment.CENTER);

			HSSFCellStyle cellTitleStyle2 = workbook.createCellStyle();
			cellTitleStyle2.setBorderBottom(BorderStyle.MEDIUM);
			cellTitleStyle2.setBorderTop(BorderStyle.MEDIUM);
			cellTitleStyle2.setBorderLeft(BorderStyle.MEDIUM);
			cellTitleStyle2.setBorderRight(BorderStyle.MEDIUM);
			cellTitleStyle2.setAlignment(HorizontalAlignment.LEFT);
			cellTitleStyle2.setVerticalAlignment(VerticalAlignment.CENTER);
			cellTitleStyle2.setFont(font);

			for (int y = 0; y < tableRowHeders2.length; y++) {
				HSSFRow rows = sheet.createRow(y);
				HSSFCell cells = rows.createCell(cellIndex);
				cells.setCellValue(tableRowHeders2[y]);
				cells.setCellStyle(cellStyle);

				cellIndex++;
				cells = rows.createCell(cellIndex);
				if (cModel != null && mModel != null) {

					switch (y) {
					case 0:
						for (int x = 0; x < tableCellHeders2.length; x++) {
							HSSFCell cellss = rows.createCell(cellIndex + x);
							cellss.setCellValue(tableCellHeders2[x]);
							cellss.setCellStyle(cellStyle2);
						}
						cells.setCellStyle(cellTitleStyle2);
						break;
					case 1:
						for (int x = 0; x < tableCellHeders2.length; x++) {
							HSSFCell cellss = rows.createCell(cellIndex + x);
							switch (x) {
							case 0:
								cellss.setCellValue(cModel.getTotalAll().doubleValue());
								break;
							case 1:
								cellss.setCellValue(mModel.getTotalAll().doubleValue());
								break;
							case 2:
								cellss.setCellValue(yModel.getTotalAll().doubleValue());
								break;
							default:
								break;
							}
							cellss.setCellStyle(cellFormatStyle);
						}
						break;
					case 2:
						for (int x = 0; x < tableCellHeders2.length; x++) {
							HSSFCell cellss = rows.createCell(cellIndex + x);
							switch (x) {
							case 0:
								cellss.setCellValue(cModel.getAssignedAll().doubleValue());
								break;
							case 1:
								cellss.setCellValue(mModel.getAssignedAll().doubleValue());
								break;
							case 2:
								cellss.setCellValue(yModel.getAssignedAll().doubleValue());
								break;
							default:
								break;
							}
							cellss.setCellStyle(cellFormatStyle);
						}
						break;
					case 3:
						for (int x = 0; x < tableCellHeders2.length; x++) {
							HSSFCell cellss = rows.createCell(cellIndex + x);
							switch (x) {
							case 0:
								cellss.setCellValue(pointData.getDiffAllLastM().doubleValue());
								break;
							case 1:
								cellss.setCellValue("-");
								break;
							case 2:
								cellss.setCellValue("-");
								break;
							default:
								break;
							}
							cellss.setCellStyle(cellFormatStyle);
						}
						break;
					case 4:
						for (int x = 0; x < tableCellHeders2.length; x++) {
							HSSFCell cellss = rows.createCell(cellIndex + x);
							switch (x) {
							case 0:
								cellss.setCellValue(pointData.getDiffAllLastY().doubleValue());
								break;
							case 1:
								cellss.setCellValue("-");
								break;
							case 2:
								cellss.setCellValue("-");
								break;
							default:
								break;
							}
							cellss.setCellStyle(cellFormatStyle);
						}
						break;
					case 5:
						for (int x = 0; x < tableCellHeders2.length; x++) {
							HSSFCell cellss = rows.createCell(cellIndex + x);
							cellss.setCellValue(tableCellHeders2[x]);
							cellss.setCellStyle(cellStyle2);
						}
						cells.setCellStyle(cellTitleStyle2);
						break;
					case 6:
						for (int x = 0; x < tableCellHeders2.length; x++) {
							HSSFCell cellss = rows.createCell(cellIndex + x);
							switch (x) {
							case 0:
								cellss.setCellValue(cModel.getTotalOpAll().doubleValue());
								break;
							case 1:
								cellss.setCellValue(mModel.getTotalOpAll().doubleValue());
								break;
							case 2:
								cellss.setCellValue(yModel.getTotalOpAll().doubleValue());
								break;
							default:
								break;
							}
							cellss.setCellStyle(cellFormatStyle);
						}
						break;
					case 7:
						for (int x = 0; x < tableCellHeders2.length; x++) {
							HSSFCell cellss = rows.createCell(cellIndex + x);
							switch (x) {
							case 0:
								cellss.setCellValue(cModel.getAssignedOpAll().doubleValue());
								break;
							case 1:
								cellss.setCellValue(mModel.getAssignedOpAll().doubleValue());
								break;
							case 2:
								cellss.setCellValue(yModel.getAssignedOpAll().doubleValue());
								break;
							default:
								break;
							}
							cellss.setCellStyle(cellFormatStyle);
						}
						break;
					case 8:
						for (int x = 0; x < tableCellHeders2.length; x++) {
							HSSFCell cellss = rows.createCell(cellIndex + x);
							switch (x) {
							case 0:
								cellss.setCellValue(pointData.getDiffOpAllLastM().doubleValue());
								break;
							case 1:
								cellss.setCellValue("-");
								break;
							case 2:
								cellss.setCellValue("-");
								break;
							default:
								break;
							}
							cellss.setCellStyle(cellFormatStyle);
						}
						break;
					case 9:
						for (int x = 0; x < tableCellHeders2.length; x++) {
							HSSFCell cellss = rows.createCell(cellIndex + x);
							switch (x) {
							case 0:
								cellss.setCellValue(pointData.getDiffOpAllLastY().doubleValue());
								break;
							case 1:
								cellss.setCellValue("-");
								break;
							case 2:
								cellss.setCellValue("-");
								break;
							default:
								break;
							}
							cellss.setCellStyle(cellFormatStyle);
						}
						break;
					case 10:
						for (int x = 0; x < tableCellHeders2.length; x++) {
							HSSFCell cellss = rows.createCell(cellIndex + x);
							cellss.setCellValue(tableCellHeders2[x]);
							cellss.setCellStyle(cellStyle2);
						}
						cells.setCellStyle(cellTitleStyle2);
						break;
					case 11:
						for (int x = 0; x < tableCellHeders2.length; x++) {
							HSSFCell cellss = rows.createCell(cellIndex + x);
							switch (x) {
							case 0:
								cellss.setCellValue(cModel.getTotalOp().doubleValue());
								break;
							case 1:
								cellss.setCellValue(mModel.getTotalOp().doubleValue());
								break;
							case 2:
								cellss.setCellValue(yModel.getTotalOp().doubleValue());
								break;
							default:
								break;
							}
							cellss.setCellStyle(cellFormatStyle);
						}
						break;
					case 12:
						for (int x = 0; x < tableCellHeders2.length; x++) {
							HSSFCell cellss = rows.createCell(cellIndex + x);
							switch (x) {
							case 0:
								cellss.setCellValue("-");
								break;
							case 1:
								cellss.setCellValue("-");
								break;
							case 2:
								cellss.setCellValue("-");
								break;
							default:
								break;
							}
							cellss.setCellStyle(cellStyle2);
						}
						break;
					case 13:
						for (int x = 0; x < tableCellHeders2.length; x++) {
							HSSFCell cellss = rows.createCell(cellIndex + x);
							switch (x) {
							case 0:
								cellss.setCellValue(pointData.getDiffOpLastM().doubleValue());
								break;
							case 1:
								cellss.setCellValue("-");
								break;
							case 2:
								cellss.setCellValue("-");
								break;
							default:
								break;
							}
							cellss.setCellStyle(cellFormatStyle);
						}
						break;
					case 14:
						for (int x = 0; x < tableCellHeders2.length; x++) {
							HSSFCell cellss = rows.createCell(cellIndex + x);
							switch (x) {
							case 0:
								cellss.setCellValue(pointData.getDiffOpLastY().doubleValue());
								break;
							case 1:
								cellss.setCellValue("-");
								break;
							case 2:
								cellss.setCellValue("-");
								break;
							default:
								break;
							}
							cellss.setCellStyle(cellFormatStyle);
						}
						break;
					case 15:
						for (int x = 0; x < tableCellHeders2.length; x++) {
							HSSFCell cellss = rows.createCell(cellIndex + x);
							cellss.setCellValue(tableCellHeders2[x]);
							cellss.setCellStyle(cellStyle2);
						}
						cells.setCellStyle(cellTitleStyle2);
						break;
					case 16:
						for (int x = 0; x < tableCellHeders2.length; x++) {
							HSSFCell cellss = rows.createCell(cellIndex + x);
							switch (x) {
							case 0:
								cellss.setCellValue(cModel.getTotalEm().doubleValue());
								break;
							case 1:
								cellss.setCellValue(mModel.getTotalEm().doubleValue());
								break;
							case 2:
								cellss.setCellValue(yModel.getTotalEm().doubleValue());
								break;
							default:
								break;
							}
							cellss.setCellStyle(cellFormatStyle);
						}
						break;
					case 17:
						for (int x = 0; x < tableCellHeders2.length; x++) {
							HSSFCell cellss = rows.createCell(cellIndex + x);
							switch (x) {
							case 0:
								cellss.setCellValue("-");
								break;
							case 1:
								cellss.setCellValue("-");
								break;
							case 2:
								cellss.setCellValue("-");
								break;
							default:
								break;
							}
							cellss.setCellStyle(cellStyle2);
						}
						break;
					case 18:
						for (int x = 0; x < tableCellHeders2.length; x++) {
							HSSFCell cellss = rows.createCell(cellIndex + x);
							switch (x) {
							case 0:
								cellss.setCellValue(pointData.getDiffEmLastM());
								break;
							case 1:
								cellss.setCellValue("-");
								break;
							case 2:
								cellss.setCellValue("-");
								break;
							default:
								break;
							}
							cellss.setCellStyle(cellStyle2);
						}
						break;
					case 19:
						for (int x = 0; x < tableCellHeders2.length; x++) {
							HSSFCell cellss = rows.createCell(cellIndex + x);
							switch (x) {
							case 0:
								cellss.setCellValue(pointData.getDiffEmLastY().doubleValue());
								break;
							case 1:
								cellss.setCellValue("-");
								break;
							case 2:
								cellss.setCellValue("-");
								break;
							default:
								break;
							}
							cellss.setCellStyle(cellFormatStyle);
						}
						break;
					case 20:
						for (int x = 0; x < tableCellHeders2.length; x++) {
							HSSFCell cellss = rows.createCell(cellIndex + x);
							cellss.setCellValue(tableCellHeders2[x]);
							cellss.setCellStyle(cellStyle2);
						}
						cells.setCellStyle(cellTitleStyle2);
						break;
					case 21:
						for (int x = 0; x < tableCellHeders2.length; x++) {
							HSSFCell cellss = rows.createCell(cellIndex + x);
							switch (x) {
							case 0:
								cellss.setCellValue(cModel.getTotalIp().doubleValue());
								break;
							case 1:
								cellss.setCellValue(mModel.getTotalIp().doubleValue());
								break;
							case 2:
								cellss.setCellValue(yModel.getTotalIp().doubleValue());
								break;
							default:
								break;
							}
							cellss.setCellStyle(cellFormatStyle);
						}
						break;
					case 22:
						for (int x = 0; x < tableCellHeders2.length; x++) {
							HSSFCell cellss = rows.createCell(cellIndex + x);
							switch (x) {
							case 0:
								cellss.setCellValue(cModel.getAssignedIp().doubleValue());
								break;
							case 1:
								cellss.setCellValue(mModel.getAssignedIp().doubleValue());
								break;
							case 2:
								cellss.setCellValue(yModel.getAssignedIp().doubleValue());
								break;
							default:
								break;
							}
							cellss.setCellStyle(cellFormatStyle);
						}
						break;
					case 23:
						for (int x = 0; x < tableCellHeders2.length; x++) {
							HSSFCell cellss = rows.createCell(cellIndex + x);
							switch (x) {
							case 0:
								cellss.setCellValue(pointData.getDiffIpLastM().doubleValue());
								break;
							case 1:
								cellss.setCellValue("-");
								break;
							case 2:
								cellss.setCellValue("-");
								break;
							default:
								break;
							}
							cellss.setCellStyle(cellFormatStyle);
						}
						break;
					case 24:
						for (int x = 0; x < tableCellHeders2.length; x++) {
							HSSFCell cellss = rows.createCell(cellIndex + x);
							switch (x) {
							case 0:
								cellss.setCellValue(pointData.getDiffIpLastY().doubleValue());
								break;
							case 1:
								cellss.setCellValue("-");
								break;
							case 2:
								cellss.setCellValue("-");
								break;
							default:
								break;
							}
							cellss.setCellStyle(cellFormatStyle);
						}
						break;
					default:
						break;
					}
				}
				cellIndex = 0;
			}

			/// 最後設定autosize
			for (int i = 0; i < tableRowHeders2.length; i++) {
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
			}

			/// sheet3
			String[] tableRowHeaders3 = { "", "單月", "上月差額", "去年同期差額" };
			String[] tableCellHeaders3 = { "門診", "急診", "出院" };
			cellIndex = 0;
			rowIndex = 0;
			// 新建工作表 sheet3
			sheet = workbook.createSheet("人次");

			HSSFCellStyle cellStyle3 = workbook.createCellStyle();
			cellStyle3.setBorderBottom(BorderStyle.MEDIUM);
			cellStyle3.setBorderTop(BorderStyle.MEDIUM);
			cellStyle3.setBorderLeft(BorderStyle.MEDIUM);
			cellStyle3.setBorderRight(BorderStyle.MEDIUM);
			cellStyle3.setAlignment(HorizontalAlignment.LEFT);
			cellStyle3.setVerticalAlignment(VerticalAlignment.CENTER);

			for (int y = 0; y < tableRowHeaders3.length; y++) {
				HSSFRow rows = sheet.createRow(y);
				HSSFCell cells = rows.createCell(cellIndex);
				cells.setCellValue(tableRowHeaders3[y]);
				cells.setCellStyle(cellStyle);

				cellIndex++;
				cells = rows.createCell(cellIndex);
				if (cModel != null) {

					switch (y) {
					case 0:
						for (int x = 0; x < tableCellHeaders3.length; x++) {
							cells = rows.createCell(cellIndex + x);
							cells.setCellValue(tableCellHeaders3[x]);
							cells.setCellStyle(cellStyle3);
						}
						break;
					case 1:
						for (int x = 0; x < tableCellHeaders3.length; x++) {
							cells = rows.createCell(cellIndex + x);
							switch (x) {
							case 0:
								cells.setCellValue(cModel.getPatientOp().doubleValue());
								break;
							case 1:
								cells.setCellValue(cModel.getPatientEm().doubleValue());
								break;
							case 2:
								cells.setCellValue(cModel.getPatientIp().doubleValue());
								break;
							default:
								break;
							}
							cells.setCellStyle(cellFormatStyle);
						}
						break;
					case 2:
						for (int x = 0; x < tableCellHeaders3.length; x++) {
							cells = rows.createCell(cellIndex + x);
							switch (x) {
							case 0:
								cells.setCellValue(
										cModel.getPatientOp().doubleValue() - mModel.getPatientOp().doubleValue());
								break;
							case 1:
								cells.setCellValue(
										cModel.getPatientEm().doubleValue() - mModel.getPatientEm().doubleValue());
								break;
							case 2:
								cells.setCellValue(
										cModel.getPatientIp().doubleValue() - mModel.getPatientIp().doubleValue());
								break;
							default:
								break;
							}
							cells.setCellStyle(cellFormatStyle);
						}
						break;
					case 3:
						for (int x = 0; x < tableCellHeaders3.length; x++) {
							cells = rows.createCell(cellIndex + x);
							switch (x) {
							case 0:
								cells.setCellValue(
										cModel.getPatientOp().doubleValue() - yModel.getPatientOp().doubleValue());
								break;
							case 1:
								cells.setCellValue(
										cModel.getPatientEm().doubleValue() - yModel.getPatientEm().doubleValue());
								break;
							case 2:
								cells.setCellValue(
										cModel.getPatientIp().doubleValue() - yModel.getPatientIp().doubleValue());
								break;
							default:
								break;
							}
							cells.setCellStyle(cellFormatStyle);
						}
						break;
					default:
						break;
					}
				}
				cellIndex = 0;
			}

			/// 最後設定autosize
			for (int i = 0; i < tableRowHeaders3.length; i++) {
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		String fileNameStr = "單月健保點數總表" + "_" + endDate;
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
//		response.setContentType("application/vnd.ms-excel;charset=utf8");

		/// 最後由outputstream輸出
		OutputStream out = response.getOutputStream();

		workbook.write(out);
		out.flush();
		out.close();
		workbook.close();

	}

	/**
	 * 取得健保總額累積達成率-匯出
	 * 
	 * @param year
	 * @param week
	 * @param response
	 * @throws IOException
	 */
	public void getAchievementRateExport(String year, String week, String type, HttpServletResponse response)
			throws IOException {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, Integer.parseInt(year));
		cal.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(week));
		// 指定週的最後一天
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);

		String dateStr = year + week + "w";

		AchievementWeekly awData = reportService.getAchievementWeekly(cal);

		if (awData.getMonthTotal() == null) {
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("工作表");
			String fileNameStr = "健保申報總額達成趨勢圖" + "_" + dateStr;
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
//			response.setContentType("application/vnd.ms-excel;charset=utf8");

			/// 最後由outputstream輸出
			OutputStream out = response.getOutputStream();

			workbook.write(out);
			out.flush();
			out.close();
			workbook.close();
			return;
		}

		// 建立新工作簿
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 新建工作表
		HSSFSheet sheet = workbook.createSheet("基本數值");
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

		String[] tableRowHeaders = { "趨勢圖截止時間", "慢籤額度條件", "本月申報點數", "本月分配額度", "總額達成率" };

		for (int y = 0; y < tableRowHeaders.length; y++) {
			HSSFRow rows = sheet.createRow(y);
			HSSFCell cells = rows.createCell(cellIndex);
			cells.setCellValue(tableRowHeaders[y]);
			cells.setCellStyle(cellStyle);

			cellIndex++;
			HSSFCell cellss = rows.createCell(cellIndex);
			switch (y) {
			case 0:
				cellss = rows.createCell(cellIndex);
				cellss.setCellValue(dateStr);
				cellss.setCellStyle(cellStyle);
				break;
			case 1:
				cellss = rows.createCell(cellIndex);
				cellss.setCellValue(type);
				cellss.setCellStyle(cellStyle);
				break;
			case 2:
				cellss = rows.createCell(cellIndex);
				cellss.setCellValue(awData.getMonthTotal().doubleValue());
				cellss.setCellStyle(cellFormatStyle);
				break;
			case 3:
				cellss = rows.createCell(cellIndex);
				cellss.setCellValue(awData.getMonthAssigned().doubleValue());
				cellss.setCellStyle(cellFormatStyle);
				break;
			case 4:
				cellss = rows.createCell(cellIndex);
				cellss.setCellValue(awData.getAchievementRate());
				cellss.setCellStyle(cellStyle);
				break;
			default:
				break;
			}
			cellIndex = 0;
		}

		/// 最後設定autosize
		for (int i = 0; i < tableRowHeaders.length; i++) {
			sheet.autoSizeColumn(i);
			sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
		}
		/// sheet2
		String[] sheetHeaders = { "門急診住院總額總點數趨勢圖", "門急診總額總點數趨勢圖", "住院總額總點數趨勢圖", "門診急診總額總點數" };
		String[] tableCellHeaders = { "週數", "總額點數", "週數", "分配點數", "實際申報點數", "達成率" };
		String[] tableCellHeaders5_1 = { "門診申報點數趨勢圖", "", "急診申報點數趨勢圖", "" };
		String[] tableCellHeaders5_2 = { "週數", "總額點數", "週數", "總額點數" };

		NameValueList nvlAll = null;
		NameValueList nvlAssingAll = null;
		NameValueList2 nvlActualAll = null;
		/// 重點： nvlAssingAll物件長度與nvlActualAll相同

		cellIndex = 0;
		rowIndex = 1;
		boolean isContinue = false;
		try {
			for (String sheetName : sheetHeaders) {
				switch (sheetName) {
				case "門急診住院總額總點數趨勢圖":
					nvlAll = awData.getAll();
					nvlAssingAll = awData.getAssignedAll();
					nvlActualAll = awData.getActualAll();
					break;
				case "門急診總額總點數趨勢圖":
					nvlAll = awData.getOpAll();
					nvlAssingAll = awData.getAssignedOpAll();
					nvlActualAll = awData.getActualOpAll();
					break;
				case "住院總額總點數趨勢圖":
					nvlAll = awData.getIp();
					nvlAssingAll = awData.getAssignedIp();
					nvlActualAll = awData.getActualIp();
					break;
				case "門診急診總額總點數":
					nvlAll = awData.getOp();
					nvlAssingAll = awData.getEm();
					break;
				default:
					break;
				}
				if (!sheetHeaders[3].equals(sheetName)) {
					/// 不是 門診急診總額總點數走這
					sheet = workbook.createSheet(sheetName);
					HSSFRow rows = sheet.createRow(0);
					HSSFCell cells = rows.createCell(cellIndex);
					for (int i = 0; i <= 5; i++) {
						cells = rows.createCell(i);
						cells.setCellStyle(cellTitleStyle);
						if (i == 0) {

							cells.setCellValue(sheetName);
						}
					}

					sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

					/// headers
					HSSFRow rowss = sheet.createRow(1);
					for (int x = 0; x < tableCellHeaders.length; x++) {
						HSSFCell cellss = rowss.createCell(x + cellIndex);
						cellss.setCellValue(tableCellHeaders[x]);
						cellss.setCellStyle(cellStyle);
					}
					cellIndex = 0;
					rowIndex = 3;
					rowss = sheet.createRow(2);
					HSSFCell cellss = rowss.createCell(cellIndex);
					/// values
					for (int v1 = 0; v1 < nvlAll.getNames().size(); v1++) {
						String v1Name = nvlAll.getNames().get(v1);
						Long v1Val = nvlAll.getValues().get(v1);

						cellss = rowss.createCell(cellIndex);
						cellss.setCellValue(v1Name);
						cellss.setCellStyle(cellStyle);
						cellIndex++;
						cellss = rowss.createCell(cellIndex);
						cellss.setCellValue(v1Val.doubleValue());
						cellss.setCellStyle(cellFormatStyle);
						cellIndex = 2;
						for (int v2 = 0; v2 < nvlAssingAll.getNames().size(); v2++) {
							String v2Name = nvlAssingAll.getNames().get(v2);
							Long v2Val = nvlAssingAll.getValues().get(v2);
							if (v1Name.equals(v2Name)) {
								cellss = rowss.createCell(cellIndex);
								cellss.setCellValue(v2Name);
								cellss.setCellStyle(cellStyle);
								cellIndex++;
								cellss = rowss.createCell(cellIndex);
								cellss.setCellValue(v2Val.doubleValue());
								cellss.setCellStyle(cellFormatStyle);
								cellIndex++;
								cellss = rowss.createCell(cellIndex);
								Long v3Val = nvlActualAll.getValues().get(v2);
								Long v3Val2 = nvlActualAll.getValues2().get(v2);
								cellss.setCellValue(v3Val.doubleValue());
								cellss.setCellStyle(cellFormatStyle);
								cellIndex++;
								cellss = rowss.createCell(cellIndex);
								cellss.setCellValue(String.valueOf(v3Val2) + "%");
								cellss.setCellStyle(cellStyle);
								isContinue = true;
							} else {
								if (!isContinue) {
									cellss = rowss.createCell(cellIndex);
									cellss.setCellStyle(cellStyle);
									cellIndex++;
									cellss = rowss.createCell(cellIndex);
									cellss.setCellStyle(cellStyle);
									cellIndex++;
									cellss = rowss.createCell(cellIndex);
									cellss.setCellStyle(cellStyle);
									cellIndex++;
									cellss = rowss.createCell(cellIndex);
									cellss.setCellStyle(cellStyle);
									isContinue = true;
								} else {
									continue;
								}
							}
							cellss.setCellStyle(cellStyle);
							cellIndex = 2;
						}
						isContinue = false;
						rowss = sheet.createRow(rowIndex + v1);
						cellIndex = 0;

					}
					cellIndex = 0;
					rowIndex = 0;
				} else {
					/// 門診急診總額總點數走這
					sheet = workbook.createSheet(sheetName);
					HSSFRow rows = sheet.createRow(0);
					HSSFCell cells = rows.createCell(cellIndex);
					for (int i = 0; i < tableCellHeaders5_1.length; i++) {
						cells = rows.createCell(i);
						cells.setCellStyle(cellTitleStyle);
						if (i == 0) {
							cells.setCellValue(tableCellHeaders5_1[i]);
						} else if (i == 2) {
							cells.setCellValue(tableCellHeaders5_1[i]);
						}
					}

					sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
					sheet.addMergedRegion(new CellRangeAddress(0, 0, 2, 3));

					HSSFRow rowss = sheet.createRow(1);
					for (int x = 0; x < tableCellHeaders5_2.length; x++) {
						HSSFCell cellss = rowss.createCell(x + cellIndex);
						cellss.setCellValue(tableCellHeaders5_2[x]);
						cellss.setCellStyle(cellStyle);
					}
					rowss = sheet.createRow(2);
					rowIndex = 3;
					for (int v = 0; v < nvlAll.getNames().size(); v++) {
						String v1Name = nvlAll.getNames().get(v);
						Long v1Val = nvlAll.getValues().get(v);
						String v2Name = nvlAssingAll.getNames().get(v);
						Long v2Val = nvlAssingAll.getValues().get(v);

						HSSFCell cellss = rowss.createCell(cellIndex);
						cellss.setCellValue(v1Name);
						cellss.setCellStyle(cellStyle);
						cellIndex++;
						cellss = rowss.createCell(cellIndex);
						cellss.setCellValue(v1Val.doubleValue());
						cellss.setCellStyle(cellFormatStyle);
						cellIndex++;
						cellss = rowss.createCell(cellIndex);
						cellss.setCellValue(v2Name);
						cellss.setCellStyle(cellStyle);
						cellIndex++;
						cellss = rowss.createCell(cellIndex);
						cellss.setCellValue(v2Val.doubleValue());
						cellss.setCellStyle(cellFormatStyle);
						rowss = sheet.createRow(rowIndex + v);
						cellIndex = 0;
					}
				}
				/// 最後設定autosize
				for (int i = 0; i < tableCellHeaders.length; i++) {
					sheet.autoSizeColumn(i);
					sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		String fileNameStr = "健保申報總額達成趨勢圖" + "_" + dateStr;
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
//		response.setContentType("application/vnd.ms-excel;charset=utf8");

		/// 最後由outputstream輸出
		OutputStream out = response.getOutputStream();

		workbook.write(out);
		out.flush();
		out.close();
		workbook.close();

	}

	/**
	 * 取得健保總額累積達成率-匯出
	 * 
	 * @param year
	 * @param quarter
	 * @param response
	 * @throws IOException
	 */
	public void getAchievementQuarterExport(String year, String quarter, HttpServletResponse response)
			throws IOException {

		AchievementQuarter aqData = reportService.getAchievementQuarter(year, quarter);
		List<QuarterData> qdAllList = aqData.getAll();
		List<QuarterData> qdOpList = aqData.getOp();
		List<QuarterData> qdIpList = aqData.getIp();

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

			if (qdAllList.size() < 2) {
				/// 單季度資料匯出
				String[] tableCellHeaders = { "申報與分配點數與達成率", "病歷總點數(含自費)", "申報總點數", "分配額度總點數", "超額總點數", "總額達成率" };
				String[] tableRowHeaders = { "門急診/住院", "門急診", "住院" };
				String[] tableCellHeaders2 = { "", "分配點數", "申報總額點數" };
				HSSFSheet sheet = workbook.createSheet("單季度");
				/// 欄位A1
				HSSFRow row = sheet.createRow(0);
				HSSFCell cell = row.createCell(0);
				cell.setCellValue("申報季度");
				cell.setCellStyle(cellStyle);

				/// 欄位B1
				cell = row.createCell(1);
				cell.setCellValue(qdAllList.get(0).getName());
				cell.setCellStyle(cellStyle);

				/// 欄位A3
				row = sheet.createRow(2);
				for (int x = 0; x < tableCellHeaders.length; x++) {
					cell = row.createCell(x);
					cell.setCellValue(tableCellHeaders[x]);
					cell.setCellStyle(cellStyle);

				}
				/// 欄位A4
				row = sheet.createRow(3);
				rowIndex = 4;
				for (int y = 0; y < tableRowHeaders.length; y++) {
					cell = row.createCell(cellIndex);
					cell.setCellValue(tableRowHeaders[y]);
					cell.setCellStyle(cellStyle);
					cellIndex = 1;
					HSSFCell cells = row.createCell(cellIndex);
					switch (y) {
					case 0:
						for (int x = 0; x < tableCellHeaders.length - 1; x++) {
							cells = row.createCell(cellIndex + x);
							switch (x) {
							case 0:
								cells.setCellValue(qdAllList.get(0).getOriginal().doubleValue());
								break;
							case 1:
								cells.setCellValue(qdAllList.get(0).getActual().doubleValue());
								break;
							case 2:
								cells.setCellValue(qdAllList.get(0).getAssigned().doubleValue());
								break;
							case 3:
								cells.setCellValue(qdAllList.get(0).getOver().doubleValue());
								break;
							case 4:
								cells.setCellValue(qdAllList.get(0).getPercent() + "%");
								break;
							default:
								break;
							}
							cells.setCellStyle(cellFormatStyle);
						}
						break;
					case 1:
						for (int x = 0; x < tableCellHeaders.length - 1; x++) {
							cells = row.createCell(cellIndex + x);
							switch (x) {
							case 0:
								cells.setCellValue(qdOpList.get(0).getOriginal().doubleValue());
								break;
							case 1:
								cells.setCellValue(qdOpList.get(0).getActual().doubleValue());
								break;
							case 2:
								cells.setCellValue(qdOpList.get(0).getAssigned().doubleValue());
								break;
							case 3:
								cells.setCellValue(qdOpList.get(0).getOver().doubleValue());
								break;
							case 4:
								cells.setCellValue(qdOpList.get(0).getPercent() + "%");
								break;
							default:
								break;
							}
							cells.setCellStyle(cellFormatStyle);
						}
						break;
					case 2:
						for (int x = 0; x < tableCellHeaders.length - 1; x++) {
							cells = row.createCell(cellIndex + x);
							switch (x) {
							case 0:
								cells.setCellValue(qdIpList.get(0).getOriginal().doubleValue());
								break;
							case 1:
								cells.setCellValue(qdIpList.get(0).getActual().doubleValue());
								break;
							case 2:
								cells.setCellValue(qdIpList.get(0).getAssigned().doubleValue());
								break;
							case 3:
								cells.setCellValue(qdIpList.get(0).getOver().doubleValue());
								break;
							case 4:
								cells.setCellValue(qdIpList.get(0).getPercent() + "%");
								break;
							default:
								break;
							}
							cells.setCellStyle(cellFormatStyle);
						}
						break;
					default:
						break;

					}
					row = sheet.createRow(rowIndex + y);
					cellIndex = 0;
				}
				/// 欄位A8
				cellIndex = 0;
				row = sheet.createRow(7);
				for (int x = 0; x < tableCellHeaders2.length; x++) {
					cell = row.createCell(x);
					cell.setCellValue(tableCellHeaders2[x]);
					cell.setCellStyle(cellStyle);
				}
				/// 欄位A9
				row = sheet.createRow(8);
				rowIndex = 9;
				for (int y = 0; y < tableRowHeaders.length; y++) {
					cell = row.createCell(cellIndex);
					cell.setCellValue(tableRowHeaders[y]);
					cell.setCellStyle(cellStyle);
					cellIndex = 1;
					HSSFCell cells = row.createCell(cellIndex);
					switch (y) {
					case 0:
						for (int x = 0; x < tableCellHeaders2.length - 1; x++) {
							cells = row.createCell(cellIndex + x);
							switch (x) {
							case 0:
								cells.setCellValue(qdAllList.get(0).getAssigned().doubleValue());
								break;
							case 1:
								cells.setCellValue(qdAllList.get(0).getActual().doubleValue());
								break;
							default:
								break;
							}
							cells.setCellStyle(cellFormatStyle);
						}
						break;
					case 1:
						for (int x = 0; x < tableCellHeaders2.length - 1; x++) {
							cells = row.createCell(cellIndex + x);
							switch (x) {
							case 0:
								cells.setCellValue(qdOpList.get(0).getAssigned().doubleValue());
								break;
							case 1:
								cells.setCellValue(qdOpList.get(0).getActual().doubleValue());
								break;
							default:
								break;
							}
							cells.setCellStyle(cellFormatStyle);
						}
						break;
					case 2:
						for (int x = 0; x < tableCellHeaders2.length - 1; x++) {
							cells = row.createCell(cellIndex + x);
							switch (x) {
							case 0:
								cells.setCellValue(qdIpList.get(0).getAssigned().doubleValue());
								break;
							case 1:
								cells.setCellValue(qdIpList.get(0).getActual().doubleValue());
								break;
							default:
								break;
							}
							cells.setCellStyle(cellFormatStyle);
						}
						break;
					default:
						break;

					}
					row = sheet.createRow(rowIndex + y);
					cellIndex = 0;
				}
				/// 最後設定autosize
				for (int i = 0; i < tableCellHeaders.length; i++) {
					sheet.autoSizeColumn(i);
					sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
				}

			} else {
				/// 多季度資料匯出
				String[] tableCellHeaders = { "申報與分配點數與達成率", "病歷總點數(含自費)", "申報總點數", "分配額度總點數", "超額總點數", "總額達成率" };
				HSSFSheet sheet = workbook.createSheet("多季度");
				cellIndex = 0;
				rowIndex = 0;
				/// 欄位A1
				HSSFRow row = sheet.createRow(0);
				HSSFCell cell = row.createCell(0);
				cell.setCellValue("申報季度");
				cell.setCellStyle(cellStyle);
				cellIndex = 1;
				for (int v = 0; v < qdAllList.size(); v++) {
					cell = row.createCell(cellIndex + v);
					cell.setCellValue(qdAllList.get(v).getName());
					cell.setCellStyle(cellStyle);
				}

				/// 欄位A3
				row = sheet.createRow(2);
				cell = row.createCell(0);
				cell.setCellValue("門急診/住院");
				cell.setCellStyle(cellStyle);

				/// 欄位A4
				row = sheet.createRow(3);
				for (int x = 0; x < tableCellHeaders.length; x++) {
					cell = row.createCell(x);
					cell.setCellValue(tableCellHeaders[x]);
					cell.setCellStyle(cellStyle);
				}

				/// 欄位A5
				row = sheet.createRow(4);
				cellIndex = 0;
				rowIndex = 5;
				for (int v = 0; v < qdAllList.size(); v++) {
					cell = row.createCell(cellIndex);
					cell.setCellValue(qdAllList.get(v).getName());
					cell.setCellStyle(cellTitleStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(qdAllList.get(v).getOriginal().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(qdAllList.get(v).getActual().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(qdAllList.get(v).getAssigned().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(qdAllList.get(v).getOver().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(qdAllList.get(v).getPercent() + "%");
					cell.setCellStyle(cellStyle);

					rowIndex += v;
					row = sheet.createRow(rowIndex);
					cellIndex = 0;
				}

				/// 欄位 rowIndex
				row = sheet.createRow(rowIndex);
				cell = row.createCell(0);
				cell.setCellValue("門急診");
				cell.setCellStyle(cellStyle);
				rowIndex++;

				/// 欄位 rowIndex
				row = sheet.createRow(rowIndex);
				for (int x = 0; x < tableCellHeaders.length; x++) {
					cell = row.createCell(x);
					cell.setCellValue(tableCellHeaders[x]);
					cell.setCellStyle(cellStyle);
				}
				rowIndex++;

				/// 欄位 rowIndex
				row = sheet.createRow(rowIndex);
				cellIndex = 0;
				rowIndex++;
				for (int v = 0; v < qdOpList.size(); v++) {
					cell = row.createCell(cellIndex);
					cell.setCellValue(qdOpList.get(v).getName());
					cell.setCellStyle(cellTitleStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(qdOpList.get(v).getOriginal().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(qdOpList.get(v).getActual().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(qdOpList.get(v).getAssigned().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(qdOpList.get(v).getOver().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(qdOpList.get(v).getPercent() + "%");
					cell.setCellStyle(cellStyle);

					rowIndex += v;
					row = sheet.createRow(rowIndex);
					cellIndex = 0;
				}

				/// 欄位 rowIndex
				row = sheet.createRow(rowIndex);
				cell = row.createCell(0);
				cell.setCellValue("住院");
				cell.setCellStyle(cellStyle);
				rowIndex++;

				/// 欄位 rowIndex
				row = sheet.createRow(rowIndex);
				for (int x = 0; x < tableCellHeaders.length; x++) {
					cell = row.createCell(x);
					cell.setCellValue(tableCellHeaders[x]);
					cell.setCellStyle(cellStyle);
				}
				rowIndex++;

				/// 欄位 rowIndex
				row = sheet.createRow(rowIndex);
				cellIndex = 0;
				rowIndex++;
				for (int v = 0; v < qdIpList.size(); v++) {
					cell = row.createCell(cellIndex);
					cell.setCellValue(qdIpList.get(v).getName());
					cell.setCellStyle(cellTitleStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(qdIpList.get(v).getOriginal().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(qdIpList.get(v).getActual().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(qdIpList.get(v).getAssigned().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(qdIpList.get(v).getOver().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(qdIpList.get(v).getPercent() + "%");
					cell.setCellStyle(cellStyle);

					rowIndex += v;
					row = sheet.createRow(rowIndex);
					cellIndex = 0;
				}
				/// 最後設定autosize
				for (int i = 0; i < tableCellHeaders.length; i++) {
					sheet.autoSizeColumn(i);
					sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
				}
			}
		} else {
			HSSFSheet sheet = workbook.createSheet("工作表");
		}

		String fileNameStr = "健保總額累積達成率";
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
//		response.setContentType("application/vnd.ms-excel;charset=utf8");

		/// 最後由outputstream輸出
		OutputStream out = response.getOutputStream();

		workbook.write(out);
		out.flush();
		out.close();
		workbook.close();
	}

	/**
	 * 取得DRG分配比例月報表-匯出
	 * 
	 * @param year
	 * @param month
	 * @param response
	 * @throws IOException
	 */
	public void getDrgMonthlyExport(int year, int month, HttpServletResponse response) throws IOException {

		String monthStr = "";
		if (month < 10) {
			monthStr = "0" + month;
		} else {
			monthStr = String.valueOf(month);
		}

		String inputDate = DateTool.convertToChineseYear(year + monthStr + "00");
		/// 轉成民國年月
		String endDate = inputDate.substring(0, inputDate.length() - 2);

		String dateStr = year + "/" + month;
		DRGMonthlyPayload drgData = new DRGMonthlyPayload();
		try {

			drgData = reportService.getDrgMonthly(year, month);

		} catch (Exception e) {
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("工作表");
			String fileNameStr = "DRG分配比例月報表_" + endDate;
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
//			response.setContentType("application/vnd.ms-excel;charset=utf8");

			/// 最後由outputstream輸出
			OutputStream out = response.getOutputStream();

			workbook.write(out);
			out.flush();
			out.close();
			workbook.close();
			return;
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
		cellTitleStyle.setAlignment(HorizontalAlignment.CENTER);
		cellTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellTitleStyle.setBorderBottom(BorderStyle.MEDIUM);
		cellTitleStyle.setBorderTop(BorderStyle.MEDIUM);
		cellTitleStyle.setBorderLeft(BorderStyle.MEDIUM);
		cellTitleStyle.setBorderRight(BorderStyle.MEDIUM);

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

		if (drgData != null) {
			/// 新建工作表
			HSSFSheet sheet = workbook.createSheet("DRG分配比例");
			String[] tableCellHeaders = { "總住院案件數(含)手術", "住院案件申報總點數 ", "住院案件病歷總點數(不含自費)" };
			String[] tableCellHeaders2 = { "DRG總案件數(含)手術", "DRG案件申報總點數", "DRG案件病歷總點數(不含自費)" };
			String[] tableCellHeaders3 = { "DRG件數佔率", "DRG費用佔率", "DRG案件支付差額點數" };

			/// 欄位A1
			HSSFRow row = sheet.createRow(0);
			HSSFCell cell = row.createCell(0);
			cell.setCellValue("統計月份");
			cell.setCellStyle(cellStyle);

			/// 欄位B1
			cell = row.createCell(1);
			cell.setCellValue(dateStr);
			cell.setCellStyle(cellStyle);

			/// 欄位A3
			row = sheet.createRow(2);
			for (int x = 0; x < tableCellHeaders.length; x++) {
				cell = row.createCell(x);
				cell.setCellValue(tableCellHeaders[x]);
				cell.setCellStyle(cellStyle);
			}

			/// 欄位A4
			row = sheet.createRow(3);
			for (int x = 0; x < tableCellHeaders.length; x++) {
				cell = row.createCell(x);
				switch (x) {
				case 0:
					cell.setCellValue(drgData.getQuantityIp().doubleValue());
					break;
				case 1:
					cell.setCellValue(drgData.getApplPointIp().doubleValue());
					break;
				case 2:
					cell.setCellValue(drgData.getPointIp().doubleValue());
					break;
				default:
					break;
				}
				cell.setCellStyle(cellFormatStyle);
			}

			/// 欄位A5
			row = sheet.createRow(4);
			for (int x = 0; x < tableCellHeaders2.length; x++) {
				cell = row.createCell(x);
				cell.setCellValue(tableCellHeaders[x]);
				cell.setCellStyle(cellStyle);
			}

			/// 欄位A6
			row = sheet.createRow(5);
			for (int x = 0; x < tableCellHeaders2.length; x++) {
				cell = row.createCell(x);
				switch (x) {
				case 0:
					cell.setCellValue(drgData.getQuantityDrg().doubleValue());
					break;
				case 1:
					cell.setCellValue(drgData.getApplPointDrg().doubleValue());
					break;
				case 2:
					cell.setCellValue(drgData.getPointDrg().doubleValue());
					break;
				default:
					break;
				}
				cell.setCellStyle(cellFormatStyle);
			}

			/// 欄位A7
			row = sheet.createRow(6);
			for (int x = 0; x < tableCellHeaders3.length; x++) {
				cell = row.createCell(x);
				cell.setCellValue(tableCellHeaders[x]);
				cell.setCellStyle(cellStyle);
			}

			/// 欄位A8
			row = sheet.createRow(7);
			for (int x = 0; x < tableCellHeaders3.length; x++) {
				cell = row.createCell(x);
				switch (x) {
				case 0:
					cell.setCellValue(drgData.getRateDrg() + "%");
					break;
				case 1:
					cell.setCellValue(drgData.getRatePointDrg() + "%");
					break;
				case 2:
					cell.setCellValue(drgData.getDiffDrg().doubleValue());
					break;
				default:
					break;
				}
				cell.setCellStyle(cellFormatStyle);
			}

			/// 最後設定autosize
			for (int i = 0; i < tableCellHeaders.length; i++) {
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
			}

			/// 新建工作表
			tableCellHeaders = new String[] { "", "DRG案件數", "非DRG案件數" };
			String[] tableRowHeaders = { "件數", "比例" };
			sheet = workbook.createSheet("DRG比例");

			/// 欄位A1
			row = sheet.createRow(0);
			cell = row.createCell(0);
			cell.setCellValue("DRG件數佔率");
			cell.setCellStyle(cellStyle);

			/// 欄位A2
			row = sheet.createRow(1);
			for (int x = 0; x < tableCellHeaders.length; x++) {
				cell = row.createCell(x);
				cell.setCellValue(tableCellHeaders[x]);
				cell.setCellStyle(cellStyle);
			}

			/// 欄位A3
			row = sheet.createRow(2);
			rowIndex = 3;
			for (int y = 0; y < tableRowHeaders.length; y++) {
				cell = row.createCell(0);
				cell.setCellValue(tableRowHeaders[y]);
				cell.setCellStyle(cellStyle);

				cellIndex = 1;
				switch (y) {
				case 0:
					for (int x = 0; x < tableCellHeaders.length - 1; x++) {
						cell = row.createCell(cellIndex + x);
						switch (x) {
						case 0:
							cell.setCellValue(drgData.getQuantityDrg().doubleValue());
							break;
						case 1:
							cell.setCellValue(
									drgData.getQuantityIp().doubleValue() - drgData.getQuantityDrg().doubleValue());
							break;
						default:
							break;
						}
						cell.setCellStyle(cellFormatStyle);
					}
					break;
				case 1:
					for (int x = 0; x < tableCellHeaders.length - 1; x++) {
						cell = row.createCell(cellIndex + x);
						switch (x) {
						case 0:
							cell.setCellValue(drgData.getRateDrg() + "%");
							break;
						case 1:
							cell.setCellValue(100 - drgData.getRateDrg() + "%");
							break;
						default:
							break;
						}
						cell.setCellStyle(cellStyle);
					}
					break;
				default:
					break;

				}

				row = sheet.createRow(rowIndex + y);
			}

			/// 欄位A6
			row = sheet.createRow(5);
			cell = row.createCell(0);
			cell.setCellValue("DRG費用佔率");
			cell.setCellStyle(cellStyle);

			/// 欄位A7
			row = sheet.createRow(6);
			for (int x = 0; x < tableCellHeaders.length; x++) {
				cell = row.createCell(x);
				cell.setCellValue(tableCellHeaders[x]);
				cell.setCellStyle(cellStyle);
			}

			/// 欄位A8
			cellIndex = 1;
			row = sheet.createRow(7);
			rowIndex = 8;
			tableRowHeaders = new String[] { "申報點數", "比例" };
			for (int y = 0; y < tableRowHeaders.length; y++) {
				cell = row.createCell(0);
				cell.setCellValue(tableRowHeaders[y]);
				cell.setCellStyle(cellStyle);

				cellIndex = 1;
				switch (y) {
				case 0:
					for (int x = 0; x < tableCellHeaders.length - 1; x++) {
						cell = row.createCell(cellIndex + x);
						switch (x) {
						case 0:
							cell.setCellValue(drgData.getApplPointDrg().doubleValue());
							break;
						case 1:
							cell.setCellValue(
									drgData.getApplPointIp().doubleValue() - drgData.getApplPointDrg().doubleValue());
							break;
						default:
							break;
						}
						cell.setCellStyle(cellFormatStyle);
					}
					break;
				case 1:
					for (int x = 0; x < tableCellHeaders.length - 1; x++) {
						cell = row.createCell(cellIndex + x);
						switch (x) {
						case 0:
							cell.setCellValue(drgData.getRatePointDrg() + "%");
							break;
						case 1:
							cell.setCellValue(100 - drgData.getRatePointDrg() + "%");
							break;
						default:
							break;
						}
						cell.setCellStyle(cellStyle);
					}
					break;
				default:
					break;

				}

				row = sheet.createRow(rowIndex + y);
			}

			/// 最後設定autosize
			for (int i = 0; i < tableCellHeaders.length; i++) {
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
			}

			/// 新建工作表
			tableCellHeaders = new String[] { "DRG案件數比例趨勢圖", "", "", "DRG點數比例趨勢圖", "", "" };
			tableCellHeaders2 = new String[] { "週數", "DRG案件數", "非DRG案件數", "週數", "DRG案件點數", "非DRG案件點數" };
			sheet = workbook.createSheet("趨勢圖(全院)");
			cellIndex = 0;
			rowIndex = 0;

			/// 欄位A1
			row = sheet.createRow(0);
			for (int i = 0; i < tableCellHeaders.length; i++) {
				cell = row.createCell(i);
				cell.setCellStyle(cellTitleStyle);
				if (i == 0) {
					cell.setCellValue(tableCellHeaders[i]);
				} else if (i == 3) {
					cell.setCellValue(tableCellHeaders[i]);
				}
			}
			/// 合併欄位
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 3, 5));

			/// 欄位A2
			row = sheet.createRow(1);
			for (int x = 0; x < tableCellHeaders2.length; x++) {
				cell = row.createCell(x);
				cell.setCellValue(tableCellHeaders2[x]);
				cell.setCellStyle(cellStyle);
			}

			/// 欄位A3
			row = sheet.createRow(2);
			rowIndex = 3;
			cellIndex = 0;
			NameValueList2 nvlQ = drgData.getQuantityMap().get("00");
			NameValueList2 nvlD = drgData.getPointMap().get("00");
			/// values
			for (int v1 = 0; v1 < nvlQ.getNames().size(); v1++) {
				String v1Name = nvlQ.getNames().get(v1);
				Long v1Val = nvlQ.getValues().get(v1);
				Long v1Val2 = nvlQ.getValues2().get(v1);
				String v2Name = nvlD.getNames().get(v1);
				Long v2Val = nvlD.getValues().get(v1);
				Long v2Val2 = nvlD.getValues2().get(v1);
				cell = row.createCell(cellIndex);
				cell.setCellValue(v1Name);
				cell.setCellStyle(cellStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(v1Val.doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(v1Val2.doubleValue());
				cell.setCellStyle(cellFormatStyle);

				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(v2Name);
				cell.setCellStyle(cellStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(v2Val.doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(v2Val2.doubleValue());
				cell.setCellStyle(cellFormatStyle);

				row = sheet.createRow(rowIndex + v1);
				cellIndex = 0;
			}
			/// 最後設定autosize
			for (int i = 0; i < tableCellHeaders.length; i++) {
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
			}

		} else {
			HSSFSheet sheet = workbook.createSheet("工作表");
		}

		String fileNameStr = "DRG分配比例月報表_" + endDate;
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
//		response.setContentType("application/vnd.ms-excel;charset=utf8");

		/// 最後由outputstream輸出
		OutputStream out = response.getOutputStream();

		workbook.write(out);
		out.flush();
		out.close();
		workbook.close();

	}

	/**
	 * 取得DRG各科分配比例月報表-匯出
	 * 
	 * @param year
	 * @param month
	 * @param response
	 * @throws IOException
	 */
	public void getDrgMonthlyAllFuncTypeExport(int year, int month, HttpServletResponse response) throws IOException {
		String monthStr = "";
		if (month < 10) {
			monthStr = "0" + month;
		} else {
			monthStr = String.valueOf(month);
		}

		String inputDate = DateTool.convertToChineseYear(year + monthStr + "00");
		/// 轉成民國年月
		String endDate = inputDate.substring(0, inputDate.length() - 2);

		String dateStr = year + "/" + month;
		DRGMonthlyPayload drgData = new DRGMonthlyPayload();
		try {

			drgData = reportService.getDrgMonthlyAllFuncType(year, month);
		} catch (Exception e) {
			// 建立新工作簿
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("工作表");
			String fileNameStr = "DRG各科分配比例月報表_" + endDate;
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
//			response.setContentType("application/vnd.ms-excel;charset=utf8");

			/// 最後由outputstream輸出
			OutputStream out = response.getOutputStream();

			workbook.write(out);
			out.flush();
			out.close();
			workbook.close();
			return;
		}

		List<CODE_TABLE> ctModelList = codeTableDao.findByCat("FUNC_TYPE");

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

		if (drgData != null) {
			/// 新建工作表
			HSSFSheet sheet = workbook.createSheet("DRG各科分配(全院)");
			String[] tableCellHeaders = { "總住院案件數(含)手術", "住院案件申報總點數 ", "住院案件病歷總點數(不含自費)" };
			String[] tableCellHeaders2 = { "DRG總案件數(含)手術", "DRG案件申報總點數", "DRG案件病歷總點數(不含自費)" };
			String[] tableCellHeaders3 = { "DRG件數佔率", "DRG費用佔率", "DRG案件支付差額點數" };

			/// 欄位A1
			HSSFRow row = sheet.createRow(0);
			HSSFCell cell = row.createCell(0);
			cell.setCellValue("統計月份");
			cell.setCellStyle(cellStyle);

			/// 欄位B1
			cell = row.createCell(1);
			cell.setCellValue(dateStr);
			cell.setCellStyle(cellStyle);

			/// 欄位A3
			row = sheet.createRow(2);
			for (int x = 0; x < tableCellHeaders.length; x++) {
				cell = row.createCell(x);
				cell.setCellValue(tableCellHeaders[x]);
				cell.setCellStyle(cellStyle);
			}

			/// 欄位A4
			row = sheet.createRow(3);
			for (int x = 0; x < tableCellHeaders.length; x++) {
				cell = row.createCell(x);
				switch (x) {
				case 0:
					cell.setCellValue(drgData.getQuantityIp().doubleValue());
					break;
				case 1:
					cell.setCellValue(drgData.getApplPointIp().doubleValue());
					break;
				case 2:
					cell.setCellValue(drgData.getPointIp().doubleValue());
					break;
				default:
					break;
				}
				cell.setCellStyle(cellFormatStyle);
			}

			/// 欄位A5
			row = sheet.createRow(4);
			for (int x = 0; x < tableCellHeaders2.length; x++) {
				cell = row.createCell(x);
				cell.setCellValue(tableCellHeaders[x]);
				cell.setCellStyle(cellStyle);
			}

			/// 欄位A6
			row = sheet.createRow(5);
			for (int x = 0; x < tableCellHeaders2.length; x++) {
				cell = row.createCell(x);
				switch (x) {
				case 0:
					cell.setCellValue(drgData.getQuantityDrg().doubleValue());
					break;
				case 1:
					cell.setCellValue(drgData.getApplPointDrg().doubleValue());
					break;
				case 2:
					cell.setCellValue(drgData.getPointDrg().doubleValue());
					break;
				default:
					break;
				}
				cell.setCellStyle(cellFormatStyle);
			}

			/// 欄位A7
			row = sheet.createRow(6);
			for (int x = 0; x < tableCellHeaders3.length; x++) {
				cell = row.createCell(x);
				cell.setCellValue(tableCellHeaders[x]);
				cell.setCellStyle(cellStyle);
			}

			/// 欄位A8
			row = sheet.createRow(7);
			for (int x = 0; x < tableCellHeaders3.length; x++) {
				cell = row.createCell(x);
				switch (x) {
				case 0:
					cell.setCellValue(drgData.getRateDrg() + "%");
					break;
				case 1:
					cell.setCellValue(drgData.getRatePointDrg() + "%");
					break;
				case 2:
					cell.setCellValue(drgData.getDiffDrg().doubleValue());
					break;
				default:
					break;
				}
				cell.setCellStyle(cellFormatStyle);
			}

			/// 最後設定autosize
			for (int i = 0; i < tableCellHeaders.length; i++) {
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
			}

			/// 新建工作表
			tableCellHeaders = new String[] { "", "DRG案件數", "非DRG案件數" };
			String[] tableRowHeaders = { "件數", "比例" };
			sheet = workbook.createSheet("DRG比例(全院)");

			/// 欄位A1
			row = sheet.createRow(0);
			cell = row.createCell(0);
			cell.setCellValue("DRG件數佔率");
			cell.setCellStyle(cellStyle);

			/// 欄位A2
			row = sheet.createRow(1);
			for (int x = 0; x < tableCellHeaders.length; x++) {
				cell = row.createCell(x);
				cell.setCellValue(tableCellHeaders[x]);
				cell.setCellStyle(cellStyle);
			}

			/// 欄位A3
			row = sheet.createRow(2);
			rowIndex = 3;
			for (int y = 0; y < tableRowHeaders.length; y++) {
				cell = row.createCell(0);
				cell.setCellValue(tableRowHeaders[y]);
				cell.setCellStyle(cellStyle);

				cellIndex = 1;
				switch (y) {
				case 0:
					for (int x = 0; x < tableCellHeaders.length - 1; x++) {
						cell = row.createCell(cellIndex + x);
						switch (x) {
						case 0:
							cell.setCellValue(drgData.getQuantityDrg().doubleValue());
							break;
						case 1:
							cell.setCellValue(
									drgData.getQuantityIp().doubleValue() - drgData.getQuantityDrg().doubleValue());
							break;
						default:
							break;
						}
						cell.setCellStyle(cellFormatStyle);
					}
					break;
				case 1:
					for (int x = 0; x < tableCellHeaders.length - 1; x++) {
						cell = row.createCell(cellIndex + x);
						switch (x) {
						case 0:
							cell.setCellValue(drgData.getRateDrg() + "%");
							break;
						case 1:
							cell.setCellValue(100 - drgData.getRateDrg() + "%");
							break;
						default:
							break;
						}
						cell.setCellStyle(cellStyle);
					}
					break;
				default:
					break;

				}

				row = sheet.createRow(rowIndex + y);
			}

			/// 欄位A6
			row = sheet.createRow(5);
			cell = row.createCell(0);
			cell.setCellValue("DRG費用佔率");
			cell.setCellStyle(cellStyle);

			/// 欄位A7
			row = sheet.createRow(6);
			for (int x = 0; x < tableCellHeaders.length; x++) {
				cell = row.createCell(x);
				cell.setCellValue(tableCellHeaders[x]);
				cell.setCellStyle(cellStyle);
			}

			/// 欄位A8
			cellIndex = 1;
			row = sheet.createRow(7);
			rowIndex = 8;
			tableRowHeaders = new String[] { "申報點數", "比例" };
			for (int y = 0; y < tableRowHeaders.length; y++) {
				cell = row.createCell(0);
				cell.setCellValue(tableRowHeaders[y]);
				cell.setCellStyle(cellStyle);

				cellIndex = 1;
				switch (y) {
				case 0:
					for (int x = 0; x < tableCellHeaders.length - 1; x++) {
						cell = row.createCell(cellIndex + x);
						switch (x) {
						case 0:
							cell.setCellValue(drgData.getApplPointDrg().doubleValue());
							break;
						case 1:
							cell.setCellValue(
									drgData.getApplPointIp().doubleValue() - drgData.getApplPointDrg().doubleValue());
							break;
						default:
							break;
						}
						cell.setCellStyle(cellFormatStyle);
					}
					break;
				case 1:
					for (int x = 0; x < tableCellHeaders.length - 1; x++) {
						cell = row.createCell(cellIndex + x);
						switch (x) {
						case 0:
							cell.setCellValue(drgData.getRatePointDrg() + "%");
							break;
						case 1:
							cell.setCellValue(100 - drgData.getRatePointDrg() + "%");
							break;
						default:
							break;
						}
						cell.setCellStyle(cellStyle);
					}
					break;
				default:
					break;

				}

				row = sheet.createRow(rowIndex + y);
			}

			/// 最後設定autosize
			for (int i = 0; i < tableCellHeaders.length; i++) {
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
			}

			/// 新建工作表
			tableCellHeaders = new String[] { "DRG案件數比例趨勢圖", "", "", "DRG點數比例趨勢圖", "", "" };
			tableCellHeaders2 = new String[] { "週數", "DRG案件數", "非DRG案件數", "週數", "DRG案件點數", "非DRG案件點數" };
			cellIndex = 0;
			rowIndex = 0;

			List<String> funcTypes = drgData.getFuncTypes();

			for (String funcName : funcTypes) {
				if (funcName.equals("不分科")) {
					sheet = workbook.createSheet("趨勢圖(全院)");
					/// 欄位A1
					row = sheet.createRow(0);
					for (int i = 0; i < tableCellHeaders.length; i++) {
						cell = row.createCell(i);
						cell.setCellStyle(cellTitleStyle);
						if (i == 0) {
							cell.setCellValue(tableCellHeaders[i]);
						} else if (i == 3) {
							cell.setCellValue(tableCellHeaders[i]);
						}
					}
					/// 合併欄位
					sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));
					sheet.addMergedRegion(new CellRangeAddress(0, 0, 3, 5));

					/// 欄位A2
					row = sheet.createRow(1);
					for (int x = 0; x < tableCellHeaders2.length; x++) {
						cell = row.createCell(x);
						cell.setCellValue(tableCellHeaders2[x]);
						cell.setCellStyle(cellStyle);
					}

					/// 欄位A3
					row = sheet.createRow(2);
					rowIndex = 3;
					cellIndex = 0;
					NameValueList2 nvlQ = drgData.getQuantityMap().get("00");
					NameValueList2 nvlD = drgData.getPointMap().get("00");
					/// values
					for (int v1 = 0; v1 < nvlQ.getNames().size(); v1++) {
						String v1Name = nvlQ.getNames().get(v1);
						Long v1Val = nvlQ.getValues().get(v1);
						Long v1Val2 = nvlQ.getValues2().get(v1);
						String v2Name = nvlD.getNames().get(v1);
						Long v2Val = nvlD.getValues().get(v1);
						Long v2Val2 = nvlD.getValues2().get(v1);
						cell = row.createCell(cellIndex);
						cell.setCellValue(v1Name);
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(v1Val.doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(v1Val2.doubleValue());
						cell.setCellStyle(cellFormatStyle);

						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(v2Name);
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(v2Val.doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(v2Val2.doubleValue());
						cell.setCellStyle(cellFormatStyle);

						row = sheet.createRow(rowIndex + v1);
						cellIndex = 0;
					}
					cellIndex = 0;
					rowIndex = 0;
					/// 最後設定autosize
					for (int i = 0; i < tableCellHeaders.length; i++) {
						sheet.autoSizeColumn(i);
						sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
					}
				} else {
					String funcCode = "";
					/// 先用funcType中文取得對應的code
					for (CODE_TABLE model : ctModelList) {
						if (model.getDescChi().equals(funcName)) {
							funcCode = model.getCode();
						}
					}
					NameValueList2 nvlQ = drgData.getQuantityMap().get(funcCode);
					NameValueList2 nvlD = drgData.getPointMap().get(funcCode);
					if (nvlQ == null) {
						continue;
					}
					sheet = workbook.createSheet("趨勢圖(" + funcName + ")");

					/// 欄位A1
					row = sheet.createRow(0);
					cell = row.createCell(0);
					cell.setCellValue(funcName);

					/// 欄位A2
					row = sheet.createRow(1);
					for (int i = 0; i < tableCellHeaders.length; i++) {
						cell = row.createCell(i);
						cell.setCellStyle(cellTitleStyle);
						if (i == 0) {
							cell.setCellValue(tableCellHeaders[i]);
						} else if (i == 3) {
							cell.setCellValue(tableCellHeaders[i]);
						}
					}
					/// 合併欄位
					sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 2));
					sheet.addMergedRegion(new CellRangeAddress(1, 1, 3, 5));

					/// 欄位A3
					row = sheet.createRow(2);
					for (int x = 0; x < tableCellHeaders2.length; x++) {
						cell = row.createCell(x);
						cell.setCellValue(tableCellHeaders2[x]);
						cell.setCellStyle(cellStyle);
					}

					/// 欄位A4
					row = sheet.createRow(3);
					rowIndex = 4;
					cellIndex = 0;

					/// values
					for (int v1 = 0; v1 < nvlQ.getNames().size(); v1++) {
						String v1Name = nvlQ.getNames().get(v1);
						Long v1Val = nvlQ.getValues().get(v1);
						Long v1Val2 = nvlQ.getValues2().get(v1);
						String v2Name = nvlD.getNames().get(v1);
						Long v2Val = nvlD.getValues().get(v1);
						Long v2Val2 = nvlD.getValues2().get(v1);
						cell = row.createCell(cellIndex);
						cell.setCellValue(v1Name);
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(v1Val.doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(v1Val2.doubleValue());
						cell.setCellStyle(cellFormatStyle);

						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(v2Name);
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(v2Val.doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(v2Val2.doubleValue());
						cell.setCellStyle(cellFormatStyle);

						row = sheet.createRow(rowIndex + v1);
						cellIndex = 0;
					}
					cellIndex = 0;
					rowIndex = 0;
					/// 最後設定autosize
					for (int i = 0; i < tableCellHeaders.length; i++) {
						sheet.autoSizeColumn(i);
						sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
					}
				}
			}

		} else {
			HSSFSheet sheet = workbook.createSheet("工作表");
		}

		String fileNameStr = "DRG各科分配比例月報表_" + endDate;
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
//		response.setContentType("application/vnd.ms-excel;charset=utf8");

		/// 最後由outputstream輸出
		OutputStream out = response.getOutputStream();

		workbook.write(out);
		out.flush();
		out.close();
		workbook.close();
	}

	/**
	 * 取得DRG各區分配比例月報表-匯出
	 * 
	 * @param year
	 * @param month
	 * @param response
	 * @throws IOException
	 */
	public void getDrgMonthlySectionExport(int year, int month, HttpServletResponse response) throws IOException {
		String monthStr = "";
		if (month < 10) {
			monthStr = "0" + month;
		} else {
			monthStr = String.valueOf(month);
		}

		String inputDate = DateTool.convertToChineseYear(year + monthStr + "00");
		/// 轉成民國年月
		String endDate = inputDate.substring(0, inputDate.length() - 2);

		String dateStr = year + "/" + month;
		DRGMonthlySectionPayload drgData = new DRGMonthlySectionPayload();
		try {
			drgData = reportService.getDrgMonthlySection(year, month);
		} catch (Exception e) {
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("工作表");
			String fileNameStr = "DRG各科別各區分配資訊報表_" + endDate;
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
//			response.setContentType("application/vnd.ms-excel;charset=utf8");

			/// 最後由outputstream輸出
			OutputStream out = response.getOutputStream();

			workbook.write(out);
			out.flush();
			out.close();
			workbook.close();
			return;
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
		cellTitleStyle.setAlignment(HorizontalAlignment.CENTER);
		cellTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellTitleStyle.setBorderBottom(BorderStyle.MEDIUM);
		cellTitleStyle.setBorderTop(BorderStyle.MEDIUM);
		cellTitleStyle.setBorderLeft(BorderStyle.MEDIUM);
		cellTitleStyle.setBorderRight(BorderStyle.MEDIUM);

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

		if (drgData != null) {
			/// 新建工作表
			HSSFSheet sheet = workbook.createSheet("DRG各科分配(全院)");
			String[] tableCellHeaders = { "總住院案件數(含)手術", "住院案件申報總點數 ", "住院案件病歷總點數(不含自費)" };
			String[] tableCellHeaders2 = { "DRG總案件數(含)手術", "DRG案件申報總點數", "DRG案件病歷總點數(不含自費)" };
			String[] tableCellHeaders3 = { "DRG件數佔率", "DRG費用佔率", "DRG案件支付差額點數" };

			/// 欄位A1
			HSSFRow row = sheet.createRow(0);
			HSSFCell cell = row.createCell(0);
			cell.setCellValue("統計月份");
			cell.setCellStyle(cellStyle);

			/// 欄位B1
			cell = row.createCell(1);
			cell.setCellValue(dateStr);
			cell.setCellStyle(cellStyle);

			/// 欄位A3
			row = sheet.createRow(2);
			for (int x = 0; x < tableCellHeaders.length; x++) {
				cell = row.createCell(x);
				cell.setCellValue(tableCellHeaders[x]);
				cell.setCellStyle(cellStyle);
			}

			/// 欄位A4
			row = sheet.createRow(3);
			for (int x = 0; x < tableCellHeaders.length; x++) {
				cell = row.createCell(x);
				switch (x) {
				case 0:
					cell.setCellValue(drgData.getQuantityIp().doubleValue());
					break;
				case 1:
					cell.setCellValue(drgData.getApplPointIp().doubleValue());
					break;
				case 2:
					cell.setCellValue(drgData.getPointIp().doubleValue());
					break;
				default:
					break;
				}
				cell.setCellStyle(cellFormatStyle);
			}

			/// 欄位A5
			row = sheet.createRow(4);
			for (int x = 0; x < tableCellHeaders2.length; x++) {
				cell = row.createCell(x);
				cell.setCellValue(tableCellHeaders2[x]);
				cell.setCellStyle(cellStyle);
			}

			/// 欄位A6
			row = sheet.createRow(5);
			for (int x = 0; x < tableCellHeaders2.length; x++) {
				cell = row.createCell(x);
				switch (x) {
				case 0:
					cell.setCellValue(drgData.getQuantityDrg().doubleValue());
					break;
				case 1:
					cell.setCellValue(drgData.getApplPointDrg().doubleValue());
					break;
				case 2:
					cell.setCellValue(drgData.getPointDrg().doubleValue());
					break;
				default:
					break;
				}
				cell.setCellStyle(cellFormatStyle);
			}

			/// 欄位A7
			row = sheet.createRow(6);
			for (int x = 0; x < tableCellHeaders3.length; x++) {
				cell = row.createCell(x);
				cell.setCellValue(tableCellHeaders3[x]);
				cell.setCellStyle(cellStyle);
			}

			/// 欄位A8
			row = sheet.createRow(7);
			for (int x = 0; x < tableCellHeaders3.length; x++) {
				cell = row.createCell(x);
				switch (x) {
				case 0:
					cell.setCellValue(drgData.getRateDrg() + "%");
					break;
				case 1:
					cell.setCellValue(drgData.getRatePointDrg() + "%");
					break;
				case 2:
					cell.setCellValue(drgData.getDiffDrg().doubleValue());
					break;
				default:
					break;
				}
				cell.setCellStyle(cellFormatStyle);
			}

			/// 最後設定autosize
			for (int i = 0; i < tableCellHeaders.length; i++) {
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
			}

			/// 新建工作表
			tableCellHeaders = new String[] { "", "DRG案件數", "非DRG案件數" };
			String[] tableRowHeaders = { "件數", "比例" };
			sheet = workbook.createSheet("DRG比例");

			/// 欄位A1
			row = sheet.createRow(0);
			cell = row.createCell(0);
			cell.setCellValue("DRG件數佔率");
			cell.setCellStyle(cellStyle);

			/// 欄位A2
			row = sheet.createRow(1);
			for (int x = 0; x < tableCellHeaders.length; x++) {
				cell = row.createCell(x);
				cell.setCellValue(tableCellHeaders[x]);
				cell.setCellStyle(cellStyle);
			}

			/// 欄位A3
			row = sheet.createRow(2);
			rowIndex = 3;
			for (int y = 0; y < tableRowHeaders.length; y++) {
				cell = row.createCell(0);
				cell.setCellValue(tableRowHeaders[y]);
				cell.setCellStyle(cellStyle);

				cellIndex = 1;
				switch (y) {
				case 0:
					for (int x = 0; x < tableCellHeaders.length - 1; x++) {
						cell = row.createCell(cellIndex + x);
						switch (x) {
						case 0:
							cell.setCellValue(drgData.getQuantityDrg().doubleValue());
							break;
						case 1:
							cell.setCellValue(
									drgData.getQuantityIp().doubleValue() - drgData.getQuantityDrg().doubleValue());
							break;
						default:
							break;
						}
						cell.setCellStyle(cellFormatStyle);
					}
					break;
				case 1:
					for (int x = 0; x < tableCellHeaders.length - 1; x++) {
						cell = row.createCell(cellIndex + x);
						switch (x) {
						case 0:
							cell.setCellValue(drgData.getRateDrg() + "%");
							break;
						case 1:
							cell.setCellValue(100 - drgData.getRateDrg() + "%");
							break;
						default:
							break;
						}
						cell.setCellStyle(cellStyle);
					}
					break;
				default:
					break;

				}

				row = sheet.createRow(rowIndex + y);
			}

			/// 欄位A6
			row = sheet.createRow(5);
			cell = row.createCell(0);
			cell.setCellValue("DRG費用佔率");
			cell.setCellStyle(cellStyle);

			/// 欄位A7
			row = sheet.createRow(6);
			for (int x = 0; x < tableCellHeaders.length; x++) {
				cell = row.createCell(x);
				cell.setCellValue(tableCellHeaders[x]);
				cell.setCellStyle(cellStyle);
			}

			/// 欄位A8
			cellIndex = 1;
			row = sheet.createRow(7);
			rowIndex = 8;
			tableRowHeaders = new String[] { "申報點數", "比例" };
			for (int y = 0; y < tableRowHeaders.length; y++) {
				cell = row.createCell(0);
				cell.setCellValue(tableRowHeaders[y]);
				cell.setCellStyle(cellStyle);

				cellIndex = 1;
				switch (y) {
				case 0:
					for (int x = 0; x < tableCellHeaders.length - 1; x++) {
						cell = row.createCell(cellIndex + x);
						switch (x) {
						case 0:
							cell.setCellValue(drgData.getApplPointDrg().doubleValue());
							break;
						case 1:
							cell.setCellValue(
									drgData.getApplPointIp().doubleValue() - drgData.getApplPointDrg().doubleValue());
							break;
						default:
							break;
						}
						cell.setCellStyle(cellFormatStyle);
					}
					break;
				case 1:
					for (int x = 0; x < tableCellHeaders.length - 1; x++) {
						cell = row.createCell(cellIndex + x);
						switch (x) {
						case 0:
							cell.setCellValue(drgData.getRatePointDrg() + "%");
							break;
						case 1:
							cell.setCellValue(100 - drgData.getRatePointDrg() + "%");
							break;
						default:
							break;
						}
						cell.setCellStyle(cellStyle);
					}
					break;
				default:
					break;

				}

				row = sheet.createRow(rowIndex + y);
			}

			/// 最後設定autosize
			for (int i = 0; i < tableCellHeaders.length; i++) {
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
			}

			/// 新建工作表
			tableCellHeaders = new String[] { "", "A 區", "B1 區", "B2 區", "C 區" };
			tableRowHeaders = new String[] { "DRG案件數", "比例", "DRG案件病歷總點數(不含自費)", "DRG案件申報總點數", "DRG案件支付差額點數",
					"DRG案件佔率" };
			cellIndex = 0;
			rowIndex = 0;
			sheet = workbook.createSheet("DRG各區差額(全院)");

			/// 欄位A1
			row = sheet.createRow(0);
			cell = row.createCell(0);
			for (int x = 0; x < tableCellHeaders.length; x++) {
				cell = row.createCell(x);
				cell.setCellValue(tableCellHeaders[x]);
				cell.setCellStyle(cellTitleStyle);
			}

			/// 欄位A2
			row = sheet.createRow(1);
			rowIndex = 2;
			cellIndex = 0;
			for (int y = 0; y < tableRowHeaders.length; y++) {
				cell = row.createCell(0);
				cell.setCellValue(tableRowHeaders[y]);
				cell.setCellStyle(cellStyle);
				cellIndex = 1;
				switch (y) {
				case 0:
					for (int x = 0; x < tableCellHeaders.length - 1; x++) {
						cell = row.createCell(cellIndex + x);
						switch (x) {
						case 0:
							cell.setCellValue(drgData.getQuantityA().doubleValue());
							break;
						case 1:
							cell.setCellValue(drgData.getQuantityB1().doubleValue());
							break;
						case 2:
							cell.setCellValue(drgData.getQuantityB2().doubleValue());
							break;
						case 3:
							cell.setCellValue(drgData.getQuantityC().doubleValue());
							break;
						default:
							break;
						}
						cell.setCellStyle(cellFormatStyle);
					}
					break;
				case 1:
					Long sum = drgData.getQuantityA() + drgData.getQuantityB1() + drgData.getQuantityB2()
							+ drgData.getQuantityC();
					for (int x = 0; x < tableCellHeaders.length - 1; x++) {
						cell = row.createCell(cellIndex + x);
						/// 四捨五入double型態取小數2位
						double d = Math.round(drgData.getQuantityA().doubleValue() / sum.doubleValue() * 100.0 * 100.0)
								/ 100.0;
						switch (x) {
						case 0:
							cell.setCellValue(d + "%");
							break;
						case 1:
							/// 四捨五入double型態取小數2位
							d = Math.round(drgData.getQuantityB1().doubleValue() / sum.doubleValue() * 100.0 * 100.0)
									/ 100.0;
							cell.setCellValue(d + "%");
							break;
						case 2:
							/// 四捨五入double型態取小數2位
							d = Math.round(drgData.getQuantityB2().doubleValue() / sum.doubleValue() * 100.0 * 100.0)
									/ 100.0;
							cell.setCellValue(d + "%");
							break;
						case 3:
							/// 四捨五入double型態取小數2位
							d = Math.round(drgData.getQuantityC().doubleValue() / sum.doubleValue() * 100.0 * 100.0)
									/ 100.0;
							cell.setCellValue(d + "%");
							break;
						default:
							break;
						}
						cell.setCellStyle(cellStyle);
					}
					break;
				case 2:
					for (int x = 0; x < tableCellHeaders.length - 1; x++) {
						cell = row.createCell(cellIndex + x);
						switch (x) {
						case 0:
							cell.setCellValue(drgData.getActualA().doubleValue());
							break;
						case 1:
							cell.setCellValue(drgData.getActualB1().doubleValue());
							break;
						case 2:
							cell.setCellValue(drgData.getActualB2().doubleValue());
							break;
						case 3:
							cell.setCellValue(drgData.getActualC().doubleValue());
							break;
						default:
							break;
						}
						cell.setCellStyle(cellFormatStyle);
					}
					break;
				case 3:
					for (int x = 0; x < tableCellHeaders.length - 1; x++) {
						cell = row.createCell(cellIndex + x);
						switch (x) {
						case 0:
							cell.setCellValue(drgData.getApplA().doubleValue());
							break;
						case 1:
							cell.setCellValue(drgData.getApplB1().doubleValue());
							break;
						case 2:
							cell.setCellValue(drgData.getApplB2().doubleValue());
							break;
						case 3:
							cell.setCellValue(drgData.getApplC().doubleValue());
							break;
						default:
							break;
						}
						cell.setCellStyle(cellFormatStyle);
					}
					break;
				case 4:
					for (int x = 0; x < tableCellHeaders.length - 1; x++) {
						cell = row.createCell(cellIndex + x);
						switch (x) {
						case 0:
							cell.setCellValue(drgData.getDiffA().doubleValue());
							break;
						case 1:
							cell.setCellValue(drgData.getDiffB1().doubleValue());
							break;
						case 2:
							cell.setCellValue(drgData.getDiffB2().doubleValue());
							break;
						case 3:
							cell.setCellValue(drgData.getDiffC().doubleValue());
							break;
						default:
							break;
						}
						cell.setCellStyle(cellFormatStyle);
					}
					break;
				case 5:
					sum = drgData.getApplA() + drgData.getApplB1() + drgData.getApplB2() + drgData.getApplC();
					for (int x = 0; x < tableCellHeaders.length - 1; x++) {
						cell = row.createCell(cellIndex + x);
						/// 四捨五入double型態取小數2位
						double d = Math.round(drgData.getApplA().doubleValue() / sum.doubleValue() * 100.0 * 100.0)
								/ 100.0;
						switch (x) {
						case 0:
							cell.setCellValue(d + "%");
							break;
						case 1:
							/// 四捨五入double型態取小數2位
							d = Math.round(drgData.getApplB1().doubleValue() / sum.doubleValue() * 100.0 * 100.0)
									/ 100.0;
							cell.setCellValue(d + "%");
							break;
						case 2:
							/// 四捨五入double型態取小數2位
							d = Math.round(drgData.getApplB2().doubleValue() / sum.doubleValue() * 100.0 * 100.0)
									/ 100.0;
							cell.setCellValue(d + "%");
							break;
						case 3:
							/// 四捨五入double型態取小數2位
							d = Math.round(drgData.getApplC().doubleValue() / sum.doubleValue() * 100.0 * 100.0)
									/ 100.0;
							cell.setCellValue(d + "%");
							break;
						default:
							break;
						}
						cell.setCellStyle(cellStyle);
					}
					break;
				default:
					break;
				}

				row = sheet.createRow(rowIndex + y);
				cellIndex = 1;
			}

			/// 最後設定autosize
			for (int i = 0; i < tableCellHeaders.length; i++) {
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
			}

			/// 新建工作表
			List<String> tableCellHeadersList = new ArrayList<String>();
			List<String> tableCellHeadersList2 = new ArrayList<String>();
			tableCellHeaders = new String[] { "A區", "B1區", "B2區", "C區", };
			tableRowHeaders = new String[] { "DRG案件數", "DRG案件申報總點數", "DRG案件支付差額點數", "DRG案件佔率" };
			cellIndex = 0;
			rowIndex = 0;
			int y1, y2, x1, x2;
			sheet = workbook.createSheet("DRG各科別各區差額資訊(各科)");
			/// 以下資料筆數皆為相同
			List<NameCodePointQuantity> sectonA = drgData.getSectionA();
			List<NameCodePointQuantity> sectonB1 = drgData.getSectionB1();
			List<NameCodePointQuantity> sectonB2 = drgData.getSectionB2();
			List<NameCodePointQuantity> sectonC = drgData.getSectionC();
			List<NameCodePoint> diffB1 = drgData.getDiffB1FuncType();
			List<NameCodePoint> diffB2 = drgData.getDiffB2FuncType();
			List<NameCodePoint> diffC = drgData.getDiffCFuncType();
			/// 將表頭先拿出
			for (NameCodePointQuantity ncpq : sectonA) {
				tableCellHeadersList.add(ncpq.getName());
				/// 有幾個funcType就塞入幾次
				Collections.addAll(tableCellHeadersList2, tableCellHeaders);
			}
			y1 = 0;
			y2 = 0;
			x1 = 1;
			x2 = 4;
			row = sheet.createRow(0);
			cellIndex = 1;
			/// 欄位B1
			for (String name : tableCellHeadersList) {
				for (int i = 0; i < 4; i++) {
					cell = row.createCell(cellIndex + i);
					cell.setCellStyle(cellTitleStyle);
					if (i == 0) {

						cell.setCellValue(name);
					}
				}
				/// merge欄位
				sheet.addMergedRegion(new CellRangeAddress(y1, y2, x1, x2));
				x1 += 4;
				x2 += 4;
				cellIndex += 4;
			}

			/// 欄位A2
			cellIndex = 1;
			rowIndex = 0;
			row = sheet.createRow(1);
			for (String name : tableCellHeadersList2) {
				cell = row.createCell(cellIndex);
				cell.setCellValue(name);
				cell.setCellStyle(cellTitleStyle);
				cellIndex++;
			}

			/// 欄位A3
			row = sheet.createRow(2);
			rowIndex = 3;
			cellIndex = 1;
			for (int y = 0; y < tableRowHeaders.length; y++) {
				cell = row.createCell(0);
				cell.setCellValue(tableRowHeaders[y]);
				cell.setCellStyle(cellStyle);
				switch (y) {
				case 0:/// DRG案件數
					for (int x = 0; x < tableCellHeadersList.size(); x++) {
						if (tableCellHeadersList.get(x).equals(sectonA.get(x).getName())) {
							cell = row.createCell(cellIndex);
							cell.setCellValue(sectonA.get(x).getQuantity().doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(sectonB1.get(x).getQuantity().doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(sectonB2.get(x).getQuantity().doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(sectonC.get(x).getQuantity().doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
						}
					}
					cellIndex = 1;
					break;
				case 1:/// DRG案件申報總點數
					for (int x = 0; x < tableCellHeadersList.size(); x++) {
						if (tableCellHeadersList.get(x).equals(sectonA.get(x).getName())) {
							cell = row.createCell(cellIndex);
							cell.setCellValue(sectonA.get(x).getPoint().doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(sectonB1.get(x).getPoint().doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(sectonB2.get(x).getPoint().doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(sectonC.get(x).getPoint().doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
						}
					}
					cellIndex = 1;
					break;
				case 2:/// DRG案件支付差額點數
					for (int x = 0; x < tableCellHeadersList.size(); x++) {
						if (tableCellHeadersList.get(x).equals(sectonA.get(x).getName())) {
							cell = row.createCell(cellIndex);
							cell.setCellValue("");
							cell.setCellStyle(cellStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(diffB1.get(x).getPoint().doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(diffB2.get(x).getPoint().doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(diffC.get(x).getPoint().doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
						}
					}
					cellIndex = 1;
					break;
				case 3:/// DRG案件佔率
					Long tQuantityA = 0L;
					Long tQuantityB1 = 0L;
					Long tQuantityB2 = 0L;
					Long tQuantityC = 0L;
					/// 總計各區總點數
					for (int v = 0; v < sectonA.size(); v++) {

						tQuantityA += sectonA.get(v).getQuantity();
						tQuantityB1 += sectonB1.get(v).getQuantity();
						tQuantityB2 += sectonB2.get(v).getQuantity();
						tQuantityC += sectonC.get(v).getQuantity();
					}

					for (int x = 0; x < tableCellHeadersList.size(); x++) {
						if (tableCellHeadersList.get(x).equals(sectonA.get(x).getName())) {
							cell = row.createCell(cellIndex);
							double d = Math.round(sectonA.get(x).getQuantity().doubleValue() / tQuantityA.doubleValue()
									* 100.0 * 100.0) / 100.0;
							cell.setCellValue(d + "%");
							cell.setCellStyle(cellStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							d = Math.round(sectonB1.get(x).getQuantity().doubleValue() / tQuantityB1.doubleValue()
									* 100.0 * 100.0) / 100.0;
							cell.setCellValue(d + "%");
							cell.setCellStyle(cellStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							d = Math.round(sectonB2.get(x).getQuantity().doubleValue() / tQuantityB2.doubleValue()
									* 100.0 * 100.0) / 100.0;
							cell.setCellValue(d + "%");
							cell.setCellStyle(cellStyle);
							cellIndex++;
							d = Math.round(sectonC.get(x).getQuantity().doubleValue() / tQuantityC.doubleValue() * 100.0
									* 100.0) / 100.0;
							cell = row.createCell(cellIndex);
							cell.setCellValue(d + "%");
							cell.setCellStyle(cellStyle);
							cellIndex++;
						}
					}
					cellIndex = 1;
					break;
				default:
					break;
				}
				row = sheet.createRow(rowIndex + y);
			}
			/// 最後設定autosize
			for (int i = 0; i < tableCellHeaders.length; i++) {
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
			}

			/// 新建工作表
			tableCellHeaders = new String[] { "A區", "B1區", "B2區", "C區" };
			tableCellHeaders2 = new String[] { "週數", "申報點數", "案件數", "週數", "申報點數", "案件數", "週數", "申報點數", "案件數", "週數",
					"申報點數", "案件數", };
			cellIndex = 0;
			rowIndex = 0;
			List<String> funcTypes = drgData.getFuncTypes();
			for (String funcName : funcTypes) {
				if (funcName.equals("不分科")) {
					sheet = workbook.createSheet("案件趨勢圖(全院)");

					/// 欄位A2
					row = sheet.createRow(1);
					cell = row.createCell(0);
					cell.setCellValue("全院");
					cell.setCellStyle(cellStyle);

					/// 欄位A3
					row = sheet.createRow(2);
					y1 = 0;
					y2 = 0;
					x1 = 0;
					x2 = 2;
					for (String name : tableCellHeaders) {
						for (int i = 0; i < 3; i++) {
							cell = row.createCell(cellIndex + i);
							cell.setCellStyle(cellTitleStyle);
							if (i == 0) {

								cell.setCellValue(name);
							}
						}
						/// merge欄位
						sheet.addMergedRegion(new CellRangeAddress(y1, y2, x1, x2));
						x1 += 3;
						x2 += 3;
						cellIndex += 3;
					}

					/// 欄位A4
					row = sheet.createRow(3);
					cell = row.createCell(0);
					cellIndex = 1;
					for (int i = 0; i < tableCellHeaders2.length; i++) {
						cell.setCellValue(tableCellHeaders2[i]);
						cell.setCellStyle(cellStyle);
						cell = row.createCell(cellIndex + i);
					}

					/// 欄位A5
					NameValueList2 nvlA = drgData.getWeeklyAMap().get("不分科");
					NameValueList2 nvlB1 = drgData.getWeeklyB1Map().get("不分科");
					NameValueList2 nvlB2 = drgData.getWeeklyB2Map().get("不分科");
					NameValueList2 nvlC = drgData.getWeeklyCMap().get("不分科");
					row = sheet.createRow(4);
					cellIndex = 0;
					rowIndex = 5;
					for (int v1 = 0; v1 < nvlA.getNames().size(); v1++) {
						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlA.getNames().get(v1));
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlA.getValues2().get(v1).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlA.getValues().get(v1).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;

						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlB1.getNames().get(v1));
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlB1.getValues2().get(v1).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlB1.getValues().get(v1).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;

						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlB2.getNames().get(v1));
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlB2.getValues2().get(v1).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlB2.getValues().get(v1).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;

						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlC.getNames().get(v1));
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlC.getValues2().get(v1).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlC.getValues().get(v1).doubleValue());
						cell.setCellStyle(cellFormatStyle);

						row = sheet.createRow(rowIndex + v1);
						cellIndex = 0;
					}
					for (int i = 0; i < tableCellHeaders2.length; i++) {
						sheet.autoSizeColumn(i);
						sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
					}
					cellIndex = 0;
					rowIndex = 0;

				} else {
					if (drgData.getWeeklyAMap().get(funcName) == null) {
						continue;
					}
					sheet = workbook.createSheet("案件趨勢圖(" + funcName + ")");

					/// 欄位A2
					row = sheet.createRow(1);
					cell = row.createCell(0);
					cell.setCellValue(funcName);
					cell.setCellStyle(cellStyle);

					/// 欄位A3
					row = sheet.createRow(2);
					y1 = 0;
					y2 = 0;
					x1 = 0;
					x2 = 2;
					for (String name : tableCellHeaders) {
						for (int i = 0; i < 3; i++) {
							cell = row.createCell(cellIndex + i);
							cell.setCellStyle(cellTitleStyle);
							if (i == 0) {

								cell.setCellValue(name);
							}
						}
						/// merge欄位
						sheet.addMergedRegion(new CellRangeAddress(y1, y2, x1, x2));
						x1 += 3;
						x2 += 3;
						cellIndex += 3;
					}

					/// 欄位A4
					row = sheet.createRow(3);
					cell = row.createCell(0);
					cellIndex = 1;
					for (int i = 0; i < tableCellHeaders2.length; i++) {
						cell.setCellValue(tableCellHeaders2[i]);
						cell.setCellStyle(cellStyle);
						cell = row.createCell(cellIndex + i);
					}

					/// 欄位A5
					NameValueList2 nvlA = drgData.getWeeklyAMap().get(funcName);
					NameValueList2 nvlB1 = drgData.getWeeklyB1Map().get(funcName);
					NameValueList2 nvlB2 = drgData.getWeeklyB2Map().get(funcName);
					NameValueList2 nvlC = drgData.getWeeklyCMap().get(funcName);
					row = sheet.createRow(4);
					cellIndex = 0;
					rowIndex = 5;
					for (int v1 = 0; v1 < nvlA.getNames().size(); v1++) {
						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlA.getNames().get(v1));
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlA.getValues2().get(v1).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlA.getValues().get(v1).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;

						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlB1.getNames().get(v1));
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlB1.getValues2().get(v1).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlB1.getValues().get(v1).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;

						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlB2.getNames().get(v1));
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlB2.getValues2().get(v1).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlB2.getValues().get(v1).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;

						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlC.getNames().get(v1));
						cell.setCellStyle(cellStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlC.getValues2().get(v1).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(nvlC.getValues().get(v1).doubleValue());
						cell.setCellStyle(cellFormatStyle);

						row = sheet.createRow(rowIndex + v1);
						cellIndex = 0;
					}
					for (int i = 0; i < tableCellHeaders2.length; i++) {
						sheet.autoSizeColumn(i);
						sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
					}
					cellIndex = 0;
					rowIndex = 0;

				}
			}

		} else {
			HSSFSheet sheet = workbook.createSheet("工作表");
		}

		String fileNameStr = "DRG各科別各區分配資訊報表_" + endDate;
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
//		response.setContentType("application/vnd.ms-excel;charset=utf8");

		/// 最後由outputstream輸出
		OutputStream out = response.getOutputStream();

		workbook.write(out);
		out.flush();
		out.close();
		workbook.close();
	}

	/**
	 * 取得門急診/住院/出院人次變化-匯出
	 * 
	 * @param year
	 * @param quarter
	 * @param response
	 * @throws IOException
	 */
	public void getVisitsVarietyExport(VisitsVarietyPayload visitsVarietyPayload, HttpServletResponse response,
			String year, String week, String sdate, String edate) {

		StringBuilder yearWeek = new StringBuilder();
		yearWeek.append(year + "/" + week + "週");

		StringBuilder day = new StringBuilder();
		day.append(sdate + "~" + edate);

		// 實際總點數
		PointPeriod actual = visitsVarietyPayload.getActual();
		// 門急診-住院
		Long all = actual.getAll();
		// 門急診
		Long opem = actual.getOpem();
		// 門診 早
		Long opMorning = actual.getOpMorning();
		// 門診 中
		Long opAfternoon = actual.getOpAfternoon();
		// 門診 晚
		Long opNight = actual.getOpNight();
		// 急診
		Long em = actual.getEm();
		// 住院
		Long ip = actual.getIp();

		// 申報總點數
		PointPeriod appl = visitsVarietyPayload.getAppl();
		// 門急診-住院
		Long all_appl = appl.getAll();
		// 門急診
		Long opem_appl = appl.getOpem();
		// 門診 早
		Long opMorning_appl = appl.getOpMorning();
		// 門診 中
		Long opAfternoon_appl = appl.getOpAfternoon();
		// 門診 晚
		Long opNight_appl = appl.getOpNight();
		// 急診
		Long em_appl = appl.getEm();
		// 住院
		Long ip_appl = appl.getIp();

		// 統計截止人次
		VisitsPeriod visitsPeriod = visitsVarietyPayload.getVisitsPeriod();
		// 總人次
		VisitsPeriodDetail total = visitsPeriod.getTotal();
		Long total_all = total.getAll();
		Long total_opem = total.getOpem();
		Long total_opMorning = total.getOpMorning();
		Long total_opAfternoon = total.getOpAfternoon();
		Long total_opNight = total.getOpNight();
		Long total_em = total.getEm();
		Long total_ip = total.getIp();
		Long total_leave = total.getLeave();

		// 手術人次
		VisitsPeriodDetail surgery = visitsPeriod.getSurgery();
		Long surgery_all = surgery.getAll();
		Long surgery_opem = surgery.getOpem();
		Long surgery_opMorning = surgery.getOpMorning();
		Long surgery_opAfternoon = surgery.getOpAfternoon();
		Long surgery_opNight = surgery.getOpNight();
		Long surgery_em = surgery.getEm();
		Long surgery_ip = surgery.getIp();
		Long surgery_leave = surgery.getLeave();

		// 上月同區間總人次相比差額
		VisitsPeriodDetail diff = visitsPeriod.getDiff();
		Long diff_all = diff.getAll();
		Long diff_opem = diff.getOpem();
		Long diff_opMorning = diff.getOpMorning();
		Long diff_opAfternoon = diff.getOpAfternoon();
		Long diff_opNight = diff.getOpNight();
		Long diff_em = diff.getEm();
		Long diff_ip = diff.getIp();
		Long diff_leave = diff.getLeave();

		// 上月同區間總差額率-門急診和住院
		Float percentAll = visitsPeriod.getPercentAll();
		// 上月同區間總差額率-門急診
		Float percentOpem = visitsPeriod.getPercentOpem();
		// 上月同區間總差額率-門診(早)
		Float percentOpMorning = visitsPeriod.getPercentOpMorning();
		// 上月同區間總差額率-門診(中)
		Float percentOpAfternoon = visitsPeriod.getPercentOpAfternoon();
		// 上月同區間總差額率-門診(晚)
		Float percentOpNight = visitsPeriod.getPercentOpNight();
		// 上月同區間總差額率-急診
		Float percentEm = visitsPeriod.getPercentEm();
		// 上月同區間總差額率-住院
		Float percentIp = visitsPeriod.getPercentIp();
		// 上月同區間總差額率-出院
		Float percentLeave = visitsPeriod.getPercentLeave();

		// 科名
		List<String> funcTypes = visitsVarietyPayload.getFuncTypes();
		// 各科門急診人次
		Map<String, NameValueList> opemMap = visitsVarietyPayload.getOpemMap();
		// 各科住院人次
		Map<String, NameValueList> ipMap = visitsVarietyPayload.getIpMap();
		// 各科出院人次
		Map<String, NameValueList> leaveMap = visitsVarietyPayload.getLeaveMap();

		try {
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

			HSSFCellStyle cellStyle_noBorder = workbook.createCellStyle();
			cellStyle_noBorder.setAlignment(HorizontalAlignment.CENTER);
			cellStyle_noBorder.setVerticalAlignment(VerticalAlignment.CENTER);

			Font font = workbook.createFont();
			font.setColor(HSSFColor.HSSFColorPredefined.RED.getIndex());

			/* 新建工作表 門急診/住院/出院人次變化 */
			HSSFSheet visitsVarietySheet = workbook.createSheet("門急診-住院-出院人次變化");

			for (int i = 0; i < 14; i++) {
				if (i == 0 || i == 1) {
					visitsVarietySheet.addMergedRegion(new CellRangeAddress(i, i, 0, 2));
					visitsVarietySheet.addMergedRegion(new CellRangeAddress(i, i, 3, 4));
				} else if (i == 2) {
					visitsVarietySheet.addMergedRegion(new CellRangeAddress(i, i, 0, 2));
				} else if (i > 3 && i < 8) {
					visitsVarietySheet.addMergedRegion(new CellRangeAddress(i, i, 0, 2));
				} else if (i > 8 && i < 14) {
					visitsVarietySheet.addMergedRegion(new CellRangeAddress(i, i, 0, 2));
				} else if (i == 3) {
					visitsVarietySheet.addMergedRegion(new CellRangeAddress(i, i, 0, 9));
				} else if (i == 8) {
					visitsVarietySheet.addMergedRegion(new CellRangeAddress(i, i, 0, 10));
				}
			}

			HSSFRow rowA0 = visitsVarietySheet.createRow(0);
			HSSFRow rowA1 = visitsVarietySheet.createRow(1);
			HSSFRow rowA2 = visitsVarietySheet.createRow(2);
			HSSFRow rowA3 = visitsVarietySheet.createRow(3);
			HSSFRow rowA4 = visitsVarietySheet.createRow(4);
			HSSFRow rowA5 = visitsVarietySheet.createRow(5);
			HSSFRow rowA6 = visitsVarietySheet.createRow(6);
			HSSFRow rowA7 = visitsVarietySheet.createRow(7);
			HSSFRow rowA8 = visitsVarietySheet.createRow(8);
			HSSFRow rowA9 = visitsVarietySheet.createRow(9);
			HSSFRow rowA10 = visitsVarietySheet.createRow(10);
			HSSFRow rowA11 = visitsVarietySheet.createRow(11);
			HSSFRow rowA12 = visitsVarietySheet.createRow(12);
			HSSFRow rowA13 = visitsVarietySheet.createRow(13);
			HSSFRow rowA14 = visitsVarietySheet.createRow(14);

			String[] titleHeader_point = { "門急診/住院", "門急診", "門診(早)", "門診(中)", "門診(晚)", "急診", "住院" };
			String[] titleHeader_people = { "門急診/住院", "門急診", "門診(早)", "門診(中)", "門診(晚)", "急診", "住院", "出院" };

			addRowCell(rowA0, 0, "就醫日期區間", cellStyle);
			for (int i = 1; i < 3; i++) {
				addRowCell(rowA0, i, "", cellStyle);
			}
			addRowCell(rowA0, 3, day.toString(), cellStyle);
			addRowCell(rowA0, 4, "", cellStyle);

			addRowCell(rowA1, 0, "人次趨勢統計截止時間", cellStyle);
			for (int i = 1; i < 3; i++) {
				addRowCell(rowA1, i, "", cellStyle);
			}
			addRowCell(rowA1, 3, yearWeek.toString(), cellStyle);
			addRowCell(rowA1, 4, "", cellStyle);

			addRowCell(rowA3, 0, "就醫日期區間總點數", cellStyle);
			for (int i = 1; i < 10; i++) {
				addRowCell(rowA3, i, "", cellStyle);
			}

			for (int i = 0; i < 3; i++) {
				addRowCell(rowA4, i, "", cellStyle);
			}
			for (int i = 0; i < titleHeader_point.length; i++) {
				addRowCell(rowA4, i + 3, titleHeader_point[i], cellStyle);
			}

			addRowCell(rowA5, 0, "病歷總點數(含自費)", cellStyle);
			for (int i = 1; i < 3; i++) {
				addRowCell(rowA5, i, "", cellStyle);
			}
			addRowCell(rowA5, 3, addThousandths(all), cellStyle);
			addRowCell(rowA5, 4, addThousandths(opem), cellStyle);
			addRowCell(rowA5, 5, addThousandths(opMorning), cellStyle);
			addRowCell(rowA5, 6, addThousandths(opAfternoon), cellStyle);
			addRowCell(rowA5, 7, addThousandths(opNight), cellStyle);
			addRowCell(rowA5, 8, addThousandths(em), cellStyle);
			addRowCell(rowA5, 9, addThousandths(ip), cellStyle);

			addRowCell(rowA6, 0, "申報總點數", cellStyle);
			for (int i = 1; i < 3; i++) {
				addRowCell(rowA6, i, "", cellStyle);
			}
			addRowCell(rowA6, 3, addThousandths(all_appl), cellStyle);
			addRowCell(rowA6, 4, addThousandths(opem_appl), cellStyle);
			addRowCell(rowA6, 5, addThousandths(opMorning_appl), cellStyle);
			addRowCell(rowA6, 6, addThousandths(opAfternoon_appl), cellStyle);
			addRowCell(rowA6, 7, addThousandths(opNight_appl), cellStyle);
			addRowCell(rowA6, 8, addThousandths(em_appl), cellStyle);
			addRowCell(rowA6, 9, addThousandths(ip_appl), cellStyle);

			addRowCell(rowA8, 0, "趨勢統計截止人次", cellStyle);
			for (int i = 1; i < 11; i++) {
				addRowCell(rowA8, i, "", cellStyle);
			}

			for (int i = 1; i < 3; i++) {
				addRowCell(rowA9, i, "", cellStyle);
			}
			for (int i = 0; i < titleHeader_people.length; i++) {
				addRowCell(rowA9, i + 3, titleHeader_people[i], cellStyle);
			}

			addRowCell(rowA10, 0, "總人次", cellStyle);
			for (int i = 1; i < 3; i++) {
				addRowCell(rowA10, i, "", cellStyle);
			}
			addRowCell(rowA10, 3, addThousandths(total_all), cellStyle);
			addRowCell(rowA10, 4, addThousandths(total_opem), cellStyle);
			addRowCell(rowA10, 5, addThousandths(total_opMorning), cellStyle);
			addRowCell(rowA10, 6, addThousandths(total_opAfternoon), cellStyle);
			addRowCell(rowA10, 7, addThousandths(total_opNight), cellStyle);
			addRowCell(rowA10, 8, addThousandths(total_em), cellStyle);
			addRowCell(rowA10, 9, addThousandths(total_ip), cellStyle);
			addRowCell(rowA10, 10,addThousandths(total_leave), cellStyle);

			addRowCell(rowA11, 0, "手術人次", cellStyle);
			for (int i = 1; i < 3; i++) {
				addRowCell(rowA11, i, "", cellStyle);
			}
			addRowCell(rowA11, 3, addThousandths(surgery_all), cellStyle);
			addRowCell(rowA11, 4, addThousandths(surgery_opem), cellStyle);
			addRowCell(rowA11, 5, addThousandths(surgery_opMorning), cellStyle);
			addRowCell(rowA11, 6, addThousandths(surgery_opAfternoon), cellStyle);
			addRowCell(rowA11, 7, addThousandths(surgery_opNight), cellStyle);
			addRowCell(rowA11, 8, addThousandths(surgery_em), cellStyle);
			addRowCell(rowA11, 9, addThousandths(surgery_ip), cellStyle);
			addRowCell(rowA11, 10,addThousandths(surgery_leave), cellStyle);

			addRowCell(rowA12, 0, "上月同區間總人次相比差額", cellStyle);
			for (int i = 1; i < 3; i++) {
				addRowCell(rowA12, i, "", cellStyle);
			}
			addRowCell(rowA12, 3, addThousandths(diff_all), cellStyle);
			addRowCell(rowA12, 4, addThousandths(diff_opem), cellStyle);
			addRowCell(rowA12, 5, addThousandths(diff_opMorning), cellStyle);
			addRowCell(rowA12, 6, addThousandths(diff_opAfternoon), cellStyle);
			addRowCell(rowA12, 7, addThousandths(diff_opNight), cellStyle);
			addRowCell(rowA12, 8, addThousandths(diff_em), cellStyle);
			addRowCell(rowA12, 9, addThousandths(diff_ip), cellStyle);
			addRowCell(rowA12, 10,addThousandths(diff_leave), cellStyle);

			addRowCell(rowA13, 0, "上月同區間總差額率(%)", cellStyle);
			for (int i = 1; i < 3; i++) {
				addRowCell(rowA13, i, "", cellStyle);
			}
			addRowCell(rowA13, 3, convertToInteger(percentAll), cellStyle);
			addRowCell(rowA13, 4, convertToInteger(percentOpem), cellStyle);
			addRowCell(rowA13, 5, convertToInteger(percentOpMorning), cellStyle);
			addRowCell(rowA13, 6, convertToInteger(percentOpAfternoon), cellStyle);
			addRowCell(rowA13, 7, convertToInteger(percentOpNight), cellStyle);
			addRowCell(rowA13, 8, convertToInteger(percentEm), cellStyle);
			addRowCell(rowA13, 9, convertToInteger(percentIp), cellStyle);
			addRowCell(rowA13, 10, convertToInteger(percentLeave), cellStyle);

			/* 新建工作表 人次趨勢圖(全院) */
			HSSFSheet allClassSheet = workbook.createSheet("人次趨勢圖(全院)");

			// 各科門急診人數趨勢
			for (Entry<String, NameValueList> entry : opemMap.entrySet()) {
				if (entry.getKey().equals("不分科")) {
					allClassSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
					allClassSheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 1));
					allClassSheet.addMergedRegion(new CellRangeAddress(1, 1, 2, 3));
					allClassSheet.addMergedRegion(new CellRangeAddress(1, 1, 4, 5));

					HSSFRow row = allClassSheet.createRow(0);
					addRowCell(row, 0, entry.getKey(), cellStyle_noBorder);

					HSSFRow row_head = allClassSheet.createRow(1);
					addRowCell(row_head, 0, "門急診人數趨勢圖", cellStyle);
					addRowCell(row_head, 1, "", cellStyle);
					addRowCell(row_head, 2, "住院人數趨勢圖", cellStyle);
					addRowCell(row_head, 3, "", cellStyle);
					addRowCell(row_head, 4, "出院人數趨勢圖", cellStyle);
					addRowCell(row_head, 5, "", cellStyle);

					HSSFRow row_head2 = allClassSheet.createRow(2);
					addRowCell(row_head2, 0, "週數", cellStyle_left);
					addRowCell(row_head2, 1, "人次", cellStyle_left);
					addRowCell(row_head2, 2, "週數", cellStyle_left);
					addRowCell(row_head2, 3, "人次", cellStyle_left);
					addRowCell(row_head2, 4, "週數", cellStyle_left);
					addRowCell(row_head2, 5, "人次", cellStyle_left);

					List<String> names = entry.getValue().getNames();
					List<Long> values = entry.getValue().getValues();

					for (int i = 0; i < names.size(); i++) {
						HSSFRow row_head3 = allClassSheet.createRow(i + 3);
						addRowCell(row_head3, 0, names.get(i), cellStyle_left);
						addRowCell(row_head3, 1, addThousandths(values.get(i)), cellStyle_left);
					}
				}

			}

			// 各科住院人數趨勢
			for (Entry<String, NameValueList> entry : ipMap.entrySet()) {
				if (entry.getKey().equals("不分科")) {
					List<String> names = entry.getValue().getNames();
					List<Long> values = entry.getValue().getValues();
					for (int i = 0; i < names.size(); i++) {
						HSSFRow row_head3 = allClassSheet.getRow(i + 3);
						addRowCell(row_head3, 2, names.get(i), cellStyle_left);
						addRowCell(row_head3, 3, addThousandths(values.get(i)), cellStyle_left);
					}
				}
			}

			// 各科出院人數趨勢
			for (Entry<String, NameValueList> entry : leaveMap.entrySet()) {
				if (entry.getKey().equals("不分科")) {
					List<String> names = entry.getValue().getNames();
					List<Long> values = entry.getValue().getValues();
					for (int i = 0; i < names.size(); i++) {
						HSSFRow row_head3 = allClassSheet.getRow(i + 3);
						addRowCell(row_head3, 4, names.get(i), cellStyle_left);
						addRowCell(row_head3, 5, addThousandths(values.get(i)), cellStyle_left);
					}
				}
			}

			/* 新建工作表 人次趨勢圖(單一科別名) */
			HSSFSheet singleClassSheet = workbook.createSheet("人次趨勢圖(單一科別名)");

			int index = 0;
			int title = 0;
			// 各科門急診人數趨勢
			for (Entry<String, NameValueList> entry : opemMap.entrySet()) {
				if (index != 0) {
					title = title + entry.getValue().getValues().size() + 4;
				}
				singleClassSheet.addMergedRegion(new CellRangeAddress(title, title, 0, 1));
				singleClassSheet.addMergedRegion(new CellRangeAddress(title + 1, title + 1, 0, 1));
				singleClassSheet.addMergedRegion(new CellRangeAddress(title + 1, title + 1, 2, 3));
				singleClassSheet.addMergedRegion(new CellRangeAddress(title + 1, title + 1, 4, 5));

				HSSFRow row = singleClassSheet.createRow(title);
				addRowCell(row, 0, entry.getKey(), cellStyle_noBorder);

				HSSFRow row_head = singleClassSheet.createRow(title + 1);
				addRowCell(row_head, 0, "門急診人數趨勢圖", cellStyle);
				addRowCell(row_head, 1, "", cellStyle);
				addRowCell(row_head, 2, "住院人數趨勢圖", cellStyle);
				addRowCell(row_head, 3, "", cellStyle);
				addRowCell(row_head, 4, "出院人數趨勢圖", cellStyle);
				addRowCell(row_head, 5, "", cellStyle);

				HSSFRow row_head2 = singleClassSheet.createRow(title + 2);
				addRowCell(row_head2, 0, "週數", cellStyle_left);
				addRowCell(row_head2, 1, "人次", cellStyle_left);
				addRowCell(row_head2, 2, "週數", cellStyle_left);
				addRowCell(row_head2, 3, "人次", cellStyle_left);
				addRowCell(row_head2, 4, "週數", cellStyle_left);
				addRowCell(row_head2, 5, "人次", cellStyle_left);

				List<String> names = entry.getValue().getNames();
				List<Long> values = entry.getValue().getValues();

				for (int i = 0; i < names.size(); i++) {
					HSSFRow row_head3 = singleClassSheet.createRow(title + i + 3);
					addRowCell(row_head3, 0, names.get(i), cellStyle_left);
					addRowCell(row_head3, 1, addThousandths(values.get(i)), cellStyle_left);
				}

				index++;
			}

			index = 0;
			title = 0;
			// 各科住院人數趨勢
			for (Entry<String, NameValueList> entry : ipMap.entrySet()) {
				List<String> names = entry.getValue().getNames();
				List<Long> values = entry.getValue().getValues();
				if (index != 0) {
					title = title + entry.getValue().getValues().size() + 4;
				}
				for (int i = 0; i < names.size(); i++) {
					HSSFRow row_head3 = singleClassSheet.getRow(title + i + 3);
					addRowCell(row_head3, 2, names.get(i), cellStyle_left);
					addRowCell(row_head3, 3, addThousandths(values.get(i)), cellStyle_left);
				}

				index++;
			}

			index = 0;
			title = 0;
			// 各科出院人數趨勢
			for (Entry<String, NameValueList> entry : leaveMap.entrySet()) {
				List<String> names = entry.getValue().getNames();
				List<Long> values = entry.getValue().getValues();
				if (index != 0) {
					title = title + entry.getValue().getValues().size() + 4;
				}
				for (int i = 0; i < names.size(); i++) {
					HSSFRow row_head3 = singleClassSheet.getRow(title + i + 3);
					addRowCell(row_head3, 4, names.get(i), cellStyle_left);
					addRowCell(row_head3, 5, addThousandths(values.get(i)), cellStyle_left);
				}

				index++;
			}
			
			StringBuffer dateBetween=new StringBuffer();
			dateBetween.append(sdate.replaceAll("\\/", "-"));
			dateBetween.append("至");
			dateBetween.append(edate.replaceAll("\\/", "-"));

			// 產生報表
			String fileNameStr = "門急診_住院_出院人次變化" + "_" + dateBetween.toString();
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

			workbook.write(response.getOutputStream());
			workbook.close();

		} catch (Exception e) {
			// TODO: handle exception
//			e.printStackTrace();
		}
	}

	/**
	 * 費用業務-匯出
	 * 
	 * @param sDate
	 * @param eDate
	 * @param funcType
	 * @param response
	 * @throws ParseException
	 * @throws IOException
	 */
	public void getPeriodPointExport(String sDate, String eDate, String funcType, HttpServletResponse response)
			throws ParseException, IOException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		Date startDate = null;
		Date endDate = null;
		startDate = sdf.parse(sDate);
		endDate = sdf.parse(eDate);
		PeriodPointPayload ppModel = reportService.getPeriodPoint(startDate, endDate);
		PeriodPointPayload ppftModel = reportService.getPeriodPointByFunctype(startDate, endDate, funcType);
		PeriodPointWeeklyPayload ppwModel = reportService.getPeroidPointWeekly(endDate);
		PeriodPointWeeklyPayload ppwftModel = reportService.getPeroidPointWeeklyByFunctype(endDate, funcType);

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

		/// 新建工作表
		HSSFSheet sheet = workbook.createSheet("費用業務");
		String[] tableCellHeaders = { "門急診/住院", "門急診", "門診(早)", "門診(中)", "門診(晚)", "急診", "住院" };
		String[] tableRowHeaders = { "總案件數(含)手術", "病例點數(含自費)", "申請總點數", "部分負擔點數", "申報總點數", "自費總金額", "不申報點數" };

		/// 欄位A1
		HSSFRow row = sheet.createRow(0);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue("費用日期區間");
		cell.setCellStyle(cellStyle);

		/// 欄位B1
		cell = row.createCell(1);
		cell.setCellValue(sDate + "~" + eDate);
		cell.setCellStyle(cellStyle);

		/// 欄位A3
		row = sheet.createRow(2);
		cell = row.createCell(1);
		cellIndex = 1;
		for (String str : tableCellHeaders) {
			cell.setCellValue(str);
			cell.setCellStyle(cellStyle);
			cellIndex++;
			cell = row.createCell(cellIndex);
		}

		/// 欄位A4
		row = sheet.createRow(3);
		cellIndex = 0;
		rowIndex = 4;
		for (int y = 0; y < tableRowHeaders.length; y++) {
			cell = row.createCell(cellIndex);
			cell.setCellValue(tableRowHeaders[y]);
			cell.setCellStyle(cellStyle);

			switch (y) {
			case 0:
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getQuantityAll() == null ? 0 : ppModel.getQuantityAll().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getQuantityOpAll() == null ? 0 : ppModel.getQuantityOpAll().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getQuantityOpM() == null ? 0 : ppModel.getQuantityOpM().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getQuantityOpN() == null ? 0 : ppModel.getQuantityOpN().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getQuantityOpE() == null ? 0 : ppModel.getQuantityOpE().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getQuantityEm() == null ? 0 : ppModel.getQuantityEm().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getQuantityIp() == null ? 0 : ppModel.getQuantityIp().doubleValue());
				cell.setCellStyle(cellFormatStyle);

				cellIndex = 0;
				break;
			case 1:
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getPointAll() == null ? 0 : ppModel.getPointAll().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getPointOpAll() == null ? 0 : ppModel.getPointOpAll().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getPointOpM() == null ? 0 : ppModel.getPointOpM().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getPointOpN() == null ? 0 : ppModel.getPointOpN().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getPointOpE() == null ? 0 : ppModel.getPointOpE().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getPointEm() == null ? 0 : ppModel.getPointEm().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getPointIp() == null ? 0 : ppModel.getPointIp().doubleValue());
				cell.setCellStyle(cellFormatStyle);

				cellIndex = 0;
				break;
			case 2:
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(
						ppModel.getApplNoPartPointAll() == null ? 0 : ppModel.getApplNoPartPointAll().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getApplNoPartPointOpAll() == null ? 0
						: ppModel.getApplNoPartPointOpAll().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(
						ppModel.getApplNoPartPointOpM() == null ? 0 : ppModel.getApplNoPartPointOpM().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(
						ppModel.getApplNoPartPointOpN() == null ? 0 : ppModel.getApplNoPartPointOpN().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(
						ppModel.getApplNoPartPointOpE() == null ? 0 : ppModel.getApplNoPartPointOpE().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(
						ppModel.getApplNoPartPointEm() == null ? 0 : ppModel.getApplNoPartPointEm().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(
						ppModel.getApplNoPartPointIp() == null ? 0 : ppModel.getApplNoPartPointIp().doubleValue());
				cell.setCellStyle(cellFormatStyle);

				cellIndex = 0;
				break;
			case 3:
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getPartPointAll() == null ? 0 : ppModel.getPartPointAll().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getPartPointOpAll() == null ? 0 : ppModel.getPartPointOpAll().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getPartPointOpM() == null ? 0 : ppModel.getPartPointOpM().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getPartPointOpN() == null ? 0 : ppModel.getPartPointOpN().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getPartPointOpE() == null ? 0 : ppModel.getPartPointOpE().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getPartPointEm() == null ? 0 : ppModel.getPartPointEm().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getPartPointIp() == null ? 0 : ppModel.getPartPointIp().doubleValue());
				cell.setCellStyle(cellFormatStyle);

				cellIndex = 0;
				break;
			case 4:
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getApplPointAll() == null ? 0 : ppModel.getApplPointAll().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getApplPointOpAll() == null ? 0 : ppModel.getApplPointOpAll().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getApplPointOpM() == null ? 0 : ppModel.getApplPointOpM().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getApplPointOpN() == null ? 0 : ppModel.getApplPointOpN().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getApplPointOpE() == null ? 0 : ppModel.getApplPointOpE().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getApplPointEm() == null ? 0 : ppModel.getApplPointEm().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getApplPointIp() == null ? 0 : ppModel.getApplPointIp().doubleValue());
				cell.setCellStyle(cellFormatStyle);

				cellIndex = 0;
				break;
			case 5:
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getOwnExpAll() == null ? 0 : ppModel.getOwnExpAll().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getOwnExpOpAll() == null ? 0 : ppModel.getOwnExpOpAll().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getOwnExpOpM() == null ? 0 : ppModel.getOwnExpOpM().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getOwnExpOpN() == null ? 0 : ppModel.getOwnExpOpN().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getOwnExpOpE() == null ? 0 : ppModel.getOwnExpOpE().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getOwnExpEm() == null ? 0 : ppModel.getOwnExpEm().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getOwnExpIp() == null ? 0 : ppModel.getOwnExpIp().doubleValue());
				cell.setCellStyle(cellFormatStyle);

				cellIndex = 0;
				break;
			case 6:
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getNoApplAll() == null ? 0 : ppModel.getNoApplAll().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getNoApplOpAll() == null ? 0 : ppModel.getNoApplOpAll().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getNoApplOpM() == null ? 0 : ppModel.getNoApplOpM().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getNoApplOpN() == null ? 0 : ppModel.getNoApplOpN().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getNoApplOpE() == null ? 0 : ppModel.getNoApplOpE().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getNoApplEm() == null ? 0 : ppModel.getNoApplEm().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(ppModel.getNoApplIp() == null ? 0 : ppModel.getNoApplIp().doubleValue());
				cell.setCellStyle(cellFormatStyle);

				cellIndex = 0;
				break;
			}
			cellIndex = 0;
			row = sheet.createRow(rowIndex + y);

		}
		/// 最後設定autosize
		for (int i = 0; i < tableCellHeaders.length + 2; i++) {
			sheet.autoSizeColumn(i);
			sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
		}

		/// 新建工作表
		cellIndex = 0;
		rowIndex = 0;
		sheet = workbook.createSheet("各科申報總點數");
		tableCellHeaders = new String[] { "各科申報總點數\n門急診/住院(含手術)", "申報點數", "案件數", "比例" };
		String[] tableCellHeaders2 = { "各科申報總點數\n住院(含手術)", "申報點數", "案件數", "比例" };
		String[] tableCellHeaders3 = { "各科申報總點數\n門急診(含手術)", "申報點數", "案件數", "比例" };
		List<NameCodePointQuantity> npqAllList = ppModel.getApplByFuncType().getAll();
		List<NameCodePointQuantity> npqIpList = ppModel.getApplByFuncType().getIp();
		List<NameCodePointQuantity> npqOpList = ppModel.getApplByFuncType().getOp();
		rowIndex = 1;
		/// 欄位A2
		for (int y = 0; y < tableCellHeaders.length; y++) {
			row = sheet.createRow(rowIndex + y);
			cell = row.createCell(cellIndex);
			cell.setCellValue(tableCellHeaders[y]);
			cell.setCellStyle(cellStyle);
			switch (y) {
			case 0:
				if (npqAllList.size() > 0) {
					for (int v = 0; v < npqAllList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(npqAllList.get(v).getName());
						cell.setCellStyle(cellStyle);
					}
					cellIndex = 0;
				}
				break;
			case 1:
				if (npqAllList.size() > 0) {
					for (int v = 0; v < npqAllList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(
								npqAllList.get(v).getPoint() == null ? 0 : npqAllList.get(v).getPoint().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				break;
			case 2:
				if (npqAllList.size() > 0) {
					for (int v = 0; v < npqAllList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(npqAllList.get(v).getQuantity() == null ? 0
								: npqAllList.get(v).getQuantity().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				break;
			case 3:
				if (npqAllList.size() > 0) {
					for (int v = 0; v < npqAllList.size(); v++) {
						Long point = npqAllList.get(v).getPoint() == null ? 0 : npqAllList.get(v).getPoint();
						double d = Math.round(
								point.doubleValue() / ppModel.getApplNoPartPointAll().doubleValue() * 100.0 * 100.0) / 100.0;
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(d + "%");
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				break;
			}
		}
		rowIndex = row.getRowNum() + 2;
		cellIndex = 0;

		/// 欄位A7
		for (int y = 0; y < tableCellHeaders2.length; y++) {
			row = sheet.createRow(rowIndex + y);
			cell = row.createCell(cellIndex);
			cell.setCellValue(tableCellHeaders2[y]);
			cell.setCellStyle(cellStyle);
			switch (y) {
			case 0:
				if (npqIpList.size() > 0) {
					for (int v = 0; v < npqIpList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(npqIpList.get(v).getName());
						cell.setCellStyle(cellStyle);
					}
					cellIndex = 0;
				}
				break;
			case 1:
				if (npqIpList.size() > 0) {
					for (int v = 0; v < npqIpList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(
								npqIpList.get(v).getPoint() == null ? 0 : npqIpList.get(v).getPoint().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				break;
			case 2:
				if (npqIpList.size() > 0) {
					for (int v = 0; v < npqIpList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(npqIpList.get(v).getQuantity() == null ? 0
								: npqIpList.get(v).getQuantity().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				break;
			case 3:
				if (npqIpList.size() > 0) {
					for (int v = 0; v < npqIpList.size(); v++) {
						Long point = npqIpList.get(v).getPoint() == null ? 0 : npqIpList.get(v).getPoint();
						double d = Math.round(
								point.doubleValue() / ppModel.getApplNoPartPointIp().doubleValue() * 100.0 * 100.0) / 100.0;
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(d + "%");
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				break;
			}
		}
		rowIndex = row.getRowNum() + 2;
		cellIndex = 0;

		/// 欄位A12
		for (int y = 0; y < tableCellHeaders3.length; y++) {
			row = sheet.createRow(rowIndex + y);
			cell = row.createCell(cellIndex);
			cell.setCellValue(tableCellHeaders3[y]);
			cell.setCellStyle(cellStyle);
			switch (y) {
			case 0:
				if (npqOpList.size() > 0) {
					for (int v = 0; v < npqOpList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(npqOpList.get(v).getName());
						cell.setCellStyle(cellStyle);
					}
					cellIndex = 0;
				}
				break;
			case 1:
				if (npqOpList.size() > 0) {
					for (int v = 0; v < npqOpList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(
								npqOpList.get(v).getPoint() == null ? 0 : npqOpList.get(v).getPoint().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				break;
			case 2:
				if (npqOpList.size() > 0) {
					for (int v = 0; v < npqOpList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(npqOpList.get(v).getQuantity() == null ? 0
								: npqOpList.get(v).getQuantity().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				break;
			case 3:
				if (npqOpList.size() > 0) {
					for (int v = 0; v < npqOpList.size(); v++) {
						Long point = npqOpList.get(v).getPoint() == null ? 0 : npqOpList.get(v).getPoint();
						double d = Math.round(
								point.doubleValue() / ppModel.getApplNoPartPointOpAll().doubleValue() * 100.0 * 100.0) / 100.0;
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(d + "%");
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				break;
			}
		}

		/// 最後設定autosize
		if (npqAllList.size() > 0) {

			for (int i = 0; i < npqAllList.size(); i++) {
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
			}
		} else {
			for (int i = 0; i < tableCellHeaders.length + 2; i++) {
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
			}
		}

		/// 新建工作表
		cellIndex = 0;
		rowIndex = 0;
		sheet = workbook.createSheet("各科部分負擔點數");
		tableCellHeaders = new String[] { "各科部分負擔點數\n門急診/住院(含手術)", "部分負擔點數", "案件數", "比例" };
		tableCellHeaders2 = new String[] { "各科部分負擔點數\n住院(含手術)", "部分負擔點數", "案件數", "比例" };
		tableCellHeaders3 = new String[] { "各科部分負擔點數\n門急診(含手術)", "部分負擔點數", "案件數", "比例" };
		npqAllList = ppModel.getPartByFuncType().getAll();
		npqIpList = ppModel.getPartByFuncType().getIp();
		npqOpList = ppModel.getPartByFuncType().getOp();

		rowIndex = 1;
		/// 欄位A2
		for (int y = 0; y < tableCellHeaders.length; y++) {
			row = sheet.createRow(rowIndex + y);
			cell = row.createCell(cellIndex);
			cell.setCellValue(tableCellHeaders[y]);
			cell.setCellStyle(cellStyle);
			switch (y) {
			case 0:
				if (npqAllList.size() > 0) {
					for (int v = 0; v < npqAllList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(npqAllList.get(v).getName());
						cell.setCellStyle(cellStyle);
					}
					cellIndex = 0;
				}
				break;
			case 1:
				if (npqAllList.size() > 0) {
					for (int v = 0; v < npqAllList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(
								npqAllList.get(v).getPoint() == null ? 0 : npqAllList.get(v).getPoint().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				break;
			case 2:
				if (npqAllList.size() > 0) {
					for (int v = 0; v < npqAllList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(npqAllList.get(v).getQuantity() == null ? 0
								: npqAllList.get(v).getQuantity().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				break;
			case 3:
				if (npqAllList.size() > 0) {
					for (int v = 0; v < npqAllList.size(); v++) {
						Long point = npqAllList.get(v).getPoint() == null ? 0 : npqAllList.get(v).getPoint();
						double d = Math.round(
								point.doubleValue() / ppModel.getPartPointAll().doubleValue() * 100.0 * 100.0) / 100.0;
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(d + "%");
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				break;
			}
		}
		rowIndex = row.getRowNum() + 2;
		cellIndex = 0;

		/// 欄位A7
		for (int y = 0; y < tableCellHeaders2.length; y++) {
			row = sheet.createRow(rowIndex + y);
			cell = row.createCell(cellIndex);
			cell.setCellValue(tableCellHeaders2[y]);
			cell.setCellStyle(cellStyle);
			switch (y) {
			case 0:
				if (npqIpList.size() > 0) {
					for (int v = 0; v < npqIpList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(npqIpList.get(v).getName());
						cell.setCellStyle(cellStyle);
					}
					cellIndex = 0;
				}
				break;
			case 1:
				if (npqIpList.size() > 0) {
					for (int v = 0; v < npqIpList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(
								npqIpList.get(v).getPoint() == null ? 0 : npqIpList.get(v).getPoint().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				break;
			case 2:
				if (npqIpList.size() > 0) {
					for (int v = 0; v < npqIpList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(npqIpList.get(v).getQuantity() == null ? 0
								: npqIpList.get(v).getQuantity().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				break;
			case 3:
				if (npqIpList.size() > 0) {
					for (int v = 0; v < npqIpList.size(); v++) {
						Long point = npqIpList.get(v).getPoint() == null ? 0 : npqIpList.get(v).getPoint();
						double d = Math.round(
								point.doubleValue() / ppModel.getPartPointIp().doubleValue() * 100.0 * 100.0) / 100.0;
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(d + "%");
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				break;
			}
		}
		rowIndex = row.getRowNum() + 2;
		cellIndex = 0;

		/// 欄位A12
		for (int y = 0; y < tableCellHeaders3.length; y++) {
			row = sheet.createRow(rowIndex + y);
			cell = row.createCell(cellIndex);
			cell.setCellValue(tableCellHeaders3[y]);
			cell.setCellStyle(cellStyle);
			switch (y) {
			case 0:
				if (npqOpList.size() > 0) {
					for (int v = 0; v < npqOpList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(npqOpList.get(v).getName());
						cell.setCellStyle(cellStyle);
					}
					cellIndex = 0;
				}
				break;
			case 1:
				if (npqOpList.size() > 0) {
					for (int v = 0; v < npqOpList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(
								npqOpList.get(v).getPoint() == null ? 0 : npqOpList.get(v).getPoint().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				break;
			case 2:
				if (npqOpList.size() > 0) {
					for (int v = 0; v < npqOpList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(npqOpList.get(v).getQuantity() == null ? 0
								: npqOpList.get(v).getQuantity().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				break;
			case 3:
				if (npqOpList.size() > 0) {
					for (int v = 0; v < npqOpList.size(); v++) {
						Long point = npqOpList.get(v).getPoint() == null ? 0 : npqOpList.get(v).getPoint();
						double d = Math.round(
								point.doubleValue() / ppModel.getPartPointOpAll().doubleValue() * 100.0 * 100.0) / 100.0;
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(d + "%");
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				break;
			}
		}

		/// 最後設定autosize
		if (npqAllList.size() > 0) {

			for (int i = 0; i < npqAllList.size(); i++) {
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
			}
		} else {
			for (int i = 0; i < tableCellHeaders.length + 2; i++) {
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
			}
		}

		/// 新建工作表
		cellIndex = 0;
		rowIndex = 0;
		sheet = workbook.createSheet("各科自費總金額");
		tableCellHeaders = new String[] { "各科自費總金額\n門急診/住院(含手術)", "自費點數", "案件數", "比例" };
		tableCellHeaders2 = new String[] { "各科自費總金額數\n住院(含手術)", "自費點數", "案件數", "比例" };
		tableCellHeaders3 = new String[] { "各科自費總金額\n門急診(含手術)", "自費點數", "案件數", "比例" };
		npqAllList = ppModel.getOwnExpByFuncType().getAll();
		npqIpList = ppModel.getOwnExpByFuncType().getIp();
		npqOpList = ppModel.getOwnExpByFuncType().getOp();

		rowIndex = 1;
		/// 欄位A2
		for (int y = 0; y < tableCellHeaders.length; y++) {
			row = sheet.createRow(rowIndex + y);
			cell = row.createCell(cellIndex);
			cell.setCellValue(tableCellHeaders[y]);
			cell.setCellStyle(cellStyle);
			switch (y) {
			case 0:
				if (npqAllList.size() > 0) {
					for (int v = 0; v < npqAllList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(npqAllList.get(v).getName());
						cell.setCellStyle(cellStyle);
					}
					cellIndex = 0;
				}
				break;
			case 1:
				if (npqAllList.size() > 0) {
					for (int v = 0; v < npqAllList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(
								npqAllList.get(v).getPoint() == null ? 0 : npqAllList.get(v).getPoint().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				break;
			case 2:
				if (npqAllList.size() > 0) {
					for (int v = 0; v < npqAllList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(npqAllList.get(v).getQuantity() == null ? 0
								: npqAllList.get(v).getQuantity().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				break;
			case 3:
				if (npqAllList.size() > 0) {
					for (int v = 0; v < npqAllList.size(); v++) {
						Long point = npqAllList.get(v).getPoint() == null ? 0 : npqAllList.get(v).getPoint();
						double d = Math.round(
								point.doubleValue() / ppModel.getOwnExpAll().doubleValue() * 100.0 * 100.0) / 100.0;
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(d + "%");
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				break;
			}
		}
		rowIndex = row.getRowNum() + 2;
		cellIndex = 0;

		/// 欄位A7
		for (int y = 0; y < tableCellHeaders2.length; y++) {
			row = sheet.createRow(rowIndex + y);
			cell = row.createCell(cellIndex);
			cell.setCellValue(tableCellHeaders2[y]);
			cell.setCellStyle(cellStyle);
			switch (y) {
			case 0:
				if (npqIpList.size() > 0) {
					for (int v = 0; v < npqIpList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(npqIpList.get(v).getName());
						cell.setCellStyle(cellStyle);
					}
					cellIndex = 0;
				}
				break;
			case 1:
				if (npqIpList.size() > 0) {
					for (int v = 0; v < npqIpList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(
								npqIpList.get(v).getPoint() == null ? 0 : npqIpList.get(v).getPoint().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				break;
			case 2:
				if (npqIpList.size() > 0) {
					for (int v = 0; v < npqIpList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(npqIpList.get(v).getQuantity() == null ? 0
								: npqIpList.get(v).getQuantity().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				break;
			case 3:
				if (npqIpList.size() > 0) {
					for (int v = 0; v < npqIpList.size(); v++) {
						Long point = npqIpList.get(v).getPoint() == null ? 0 : npqIpList.get(v).getPoint();
						double d = Math.round(
								point.doubleValue() / ppModel.getOwnExpIp().doubleValue() * 100.0 * 100.0) / 100.0;
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(d + "%");
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				break;
			}
		}
		rowIndex = row.getRowNum() + 2;
		cellIndex = 0;

		/// 欄位A12
		for (int y = 0; y < tableCellHeaders3.length; y++) {
			row = sheet.createRow(rowIndex + y);
			cell = row.createCell(cellIndex);
			cell.setCellValue(tableCellHeaders3[y]);
			cell.setCellStyle(cellStyle);
			switch (y) {
			case 0:
				if (npqOpList.size() > 0) {
					for (int v = 0; v < npqOpList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(npqOpList.get(v).getName());
						cell.setCellStyle(cellStyle);
					}
					cellIndex = 0;
				}
				else if(npqAllList.size() > 0) {
					for (int v = 0; v < npqAllList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(npqAllList.get(v).getName());
						cell.setCellStyle(cellStyle);
					}
					cellIndex = 0;
				}
				break;
			case 1:
				if (npqOpList.size() > 0) {
					for (int v = 0; v < npqOpList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(
								npqOpList.get(v).getPoint() == null ? 0 : npqOpList.get(v).getPoint().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				else if(npqAllList.size() > 0) {
					for (int v = 0; v < npqAllList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(0);
						cell.setCellStyle(cellStyle);
					}
					cellIndex = 0;
				}
				break;
			case 2:
				if (npqOpList.size() > 0) {
					for (int v = 0; v < npqOpList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(npqOpList.get(v).getQuantity() == null ? 0
								: npqOpList.get(v).getQuantity().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				else if(npqAllList.size() > 0) {
					for (int v = 0; v < npqAllList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(0);
						cell.setCellStyle(cellStyle);
					}
					cellIndex = 0;
				}
				break;
			case 3:
				if (npqOpList.size() > 0) {
					for (int v = 0; v < npqOpList.size(); v++) {
						Long point = npqOpList.get(v).getPoint() == null ? 0 : npqOpList.get(v).getPoint();
						double d = Math.round(
								point.doubleValue() / ppModel.getOwnExpOpAll().doubleValue() * 100.0 * 100.0) / 100.0;
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(d + "%");
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				else if(npqAllList.size() > 0) {
					for (int v = 0; v < npqAllList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(0);
						cell.setCellStyle(cellStyle);
					}
					cellIndex = 0;
				}
				break;
			}
		}

		/// 最後設定autosize
		if (npqAllList.size() > 0) {

			for (int i = 0; i < npqAllList.size(); i++) {
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
			}
		} else {
			for (int i = 0; i < tableCellHeaders.length + 2; i++) {
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
			}
		}

		/// 新建工作表
		cellIndex = 0;
		rowIndex = 0;
		sheet = workbook.createSheet("(全院)");
		tableCellHeaders = new String[] { "門急診/住院(含手術)", "申報點數", "醫令數量", "比例" };
		tableCellHeaders2 = new String[] { "住院(含手術)", "申報點數", "醫令數量", "比例" };
		tableCellHeaders3 = new String[] { "門急診(含手術)", "申報點數", "醫令數量", "比例" };
		String[] tableCellHeaders4 = { "門急診/住院(含手術)", "自費點數", "醫令數量", "比例" };
		String[] tableCellHeaders5 = { "住院(含手術)", "自費點數", "醫令數量", "比例" };
		String[] tableCellHeaders6 = { "門急診(含手術)", "自費點數", "醫令數量", "比例" };
		npqAllList = ppModel.getPayByOrderType().getAll();
		npqIpList = ppModel.getPayByOrderType().getIp();
		npqOpList = ppModel.getPayByOrderType().getOp();
		if (npqAllList.size() > 0) {
			Long point = 0L;
			Long quantity = 0L;
			NameCodePointQuantity n = new NameCodePointQuantity();
			for (NameCodePointQuantity npq : npqAllList) {
				point += npq.getPoint();
				quantity += npq.getQuantity();
			}
			n.setName("全品項");
			n.setCode("00");
			n.setPoint(point);
			n.setQuantity(quantity);
			npqAllList.add(0, n);
		}
		if (npqIpList.size() > 0) {
			Long point = 0L;
			Long quantity = 0L;
			NameCodePointQuantity n = new NameCodePointQuantity();
			for (NameCodePointQuantity npq : npqIpList) {
				point += npq.getPoint();
				quantity += npq.getQuantity();
			}
			n.setName("全品項");
			n.setCode("00");
			n.setPoint(point);
			n.setQuantity(quantity);
			npqIpList.add(0, n);
		}
		if (npqOpList.size() > 0) {
			Long point = 0L;
			Long quantity = 0L;
			NameCodePointQuantity n = new NameCodePointQuantity();
			for (NameCodePointQuantity npq : npqOpList) {
				point += npq.getPoint();
				quantity += npq.getQuantity();
			}
			n.setName("全品項");
			n.setCode("00");
			n.setPoint(point);
			n.setQuantity(quantity);
			npqOpList.add(0, n);
		}

		/// 欄位A2
		row = sheet.createRow(1);
		cell = row.createCell(0);
		cell.setCellValue("醫療費用比例與申報點數");
		cell.setCellStyle(cellStyle);

		rowIndex = 2;
		/// 欄位A3
		for (int y = 0; y < tableCellHeaders.length; y++) {
			row = sheet.createRow(rowIndex + y);
			cell = row.createCell(cellIndex);
			cell.setCellValue(tableCellHeaders[y]);
			cell.setCellStyle(cellStyle);
			switch (y) {
			case 0:
				if (npqAllList.size() > 0) {
					for (int v = 0; v < npqAllList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(npqAllList.get(v).getName());
						cell.setCellStyle(cellStyle);
					}
					cellIndex = 0;
				}
				break;
			case 1:
				if (npqAllList.size() > 0) {
					for (int v = 0; v < npqAllList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(
								npqAllList.get(v).getPoint() == null ? 0 : npqAllList.get(v).getPoint().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				break;
			case 2:
				if (npqAllList.size() > 0) {
					for (int v = 0; v < npqAllList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(npqAllList.get(v).getQuantity() == null ? 0
								: npqAllList.get(v).getQuantity().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				break;
			case 3:
				if (npqAllList.size() > 0) {
					Long allPoint = npqAllList.get(0).getPoint();
					for (int v = 0; v < npqAllList.size(); v++) {
						Long point = npqAllList.get(v).getPoint() == null ? 0 : npqAllList.get(v).getPoint();
						double d = Math.round(point.doubleValue() / allPoint.doubleValue() * 100.0 * 100.0) / 100.0;
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(d + "%");
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				break;
			}
		}
		rowIndex = row.getRowNum() + 2;
		cellIndex = 0;

		/// 欄位A7
		for (int y = 0; y < tableCellHeaders2.length; y++) {
			row = sheet.createRow(rowIndex + y);
			cell = row.createCell(cellIndex);
			cell.setCellValue(tableCellHeaders2[y]);
			cell.setCellStyle(cellStyle);
			switch (y) {
			case 0:
				if (npqIpList.size() > 0) {
					for (int v = 0; v < npqIpList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(npqIpList.get(v).getName());
						cell.setCellStyle(cellStyle);
					}
					cellIndex = 0;
				}
				break;
			case 1:
				if (npqIpList.size() > 0) {
					for (int v = 0; v < npqIpList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(
								npqIpList.get(v).getPoint() == null ? 0 : npqIpList.get(v).getPoint().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				break;
			case 2:
				if (npqIpList.size() > 0) {
					for (int v = 0; v < npqIpList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(npqIpList.get(v).getQuantity() == null ? 0
								: npqIpList.get(v).getQuantity().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				break;
			case 3:
				if (npqIpList.size() > 0) {
					Long allPoint = npqIpList.get(0).getPoint();
					for (int v = 0; v < npqIpList.size(); v++) {
						Long point = npqIpList.get(v).getPoint() == null ? 0 : npqIpList.get(v).getPoint();
						double d = Math.round(point.doubleValue() / allPoint.doubleValue() * 100.0 * 100.0) / 100.0;
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(d + "%");
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				break;
			}
		}
		rowIndex = row.getRowNum() + 2;
		cellIndex = 0;

		/// 欄位A12
		for (int y = 0; y < tableCellHeaders3.length; y++) {
			row = sheet.createRow(rowIndex + y);
			cell = row.createCell(cellIndex);
			cell.setCellValue(tableCellHeaders3[y]);
			cell.setCellStyle(cellStyle);
			switch (y) {
			case 0:
				if (npqOpList.size() > 0) {
					for (int v = 0; v < npqOpList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(npqOpList.get(v).getName());
						cell.setCellStyle(cellStyle);
					}
					cellIndex = 0;
				}
				break;
			case 1:
				if (npqOpList.size() > 0) {
					for (int v = 0; v < npqOpList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(
								npqOpList.get(v).getPoint() == null ? 0 : npqOpList.get(v).getPoint().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				break;
			case 2:
				if (npqOpList.size() > 0) {
					for (int v = 0; v < npqOpList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(npqOpList.get(v).getQuantity() == null ? 0
								: npqOpList.get(v).getQuantity().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				break;
			case 3:
				if (npqOpList.size() > 0) {
					Long allPoint = npqOpList.get(0).getPoint();
					for (int v = 0; v < npqOpList.size(); v++) {
						Long point = npqOpList.get(v).getPoint() == null ? 0 : npqOpList.get(v).getPoint();
						double d = Math.round(point.doubleValue() / allPoint.doubleValue() * 100.0 * 100.0) / 100.0;
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(d + "%");
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				break;
			}
		}

		rowIndex = row.getRowNum() + 2;
		cellIndex = 0;
		/// 欄位A18
		row = sheet.createRow(rowIndex);
		cell = row.createCell(0);
		cell.setCellValue("醫療費用自費比例與申報點數");
		cell.setCellStyle(cellStyle);

		npqAllList = ppModel.getOwnExpByOrderType().getAll();
		npqIpList = ppModel.getOwnExpByOrderType().getIp();
		npqOpList = ppModel.getOwnExpByOrderType().getOp();
		if (npqAllList.size() > 0) {
			Long point = 0L;
			Long quantity = 0L;
			NameCodePointQuantity n = new NameCodePointQuantity();
			for (NameCodePointQuantity npq : npqAllList) {
				point += npq.getPoint();
				quantity += npq.getQuantity();
			}
			n.setName("全品項");
			n.setCode("00");
			n.setPoint(point);
			n.setQuantity(quantity);
			npqAllList.add(0, n);
		}
		if (npqIpList.size() > 0) {
			Long point = 0L;
			Long quantity = 0L;
			NameCodePointQuantity n = new NameCodePointQuantity();
			for (NameCodePointQuantity npq : npqIpList) {
				point += npq.getPoint();
				quantity += npq.getQuantity();
			}
			n.setName("全品項");
			n.setCode("00");
			n.setPoint(point);
			n.setQuantity(quantity);
			npqIpList.add(0, n);
		}
		if (npqOpList.size() > 0) {
			Long point = 0L;
			Long quantity = 0L;
			NameCodePointQuantity n = new NameCodePointQuantity();
			for (NameCodePointQuantity npq : npqOpList) {
				point += npq.getPoint();
				quantity += npq.getQuantity();
			}
			n.setName("全品項");
			n.setCode("00");
			n.setPoint(point);
			n.setQuantity(quantity);
			npqOpList.add(0, n);
		}
		rowIndex++;
		/// 欄位A19
		for (int y = 0; y < tableCellHeaders4.length; y++) {
			row = sheet.createRow(rowIndex + y);
			cell = row.createCell(cellIndex);
			cell.setCellValue(tableCellHeaders4[y]);
			cell.setCellStyle(cellStyle);
			switch (y) {
			case 0:
				if (npqAllList.size() > 0) {
					for (int v = 0; v < npqAllList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(npqAllList.get(v).getName());
						cell.setCellStyle(cellStyle);
					}
					cellIndex = 0;
				}
				break;
			case 1:
				if (npqAllList.size() > 0) {
					for (int v = 0; v < npqAllList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(
								npqAllList.get(v).getPoint() == null ? 0 : npqAllList.get(v).getPoint().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				break;
			case 2:
				if (npqAllList.size() > 0) {
					for (int v = 0; v < npqAllList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(npqAllList.get(v).getQuantity() == null ? 0
								: npqAllList.get(v).getQuantity().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				break;
			case 3:
				if (npqAllList.size() > 0) {
					Long allPoint = npqAllList.get(0).getPoint();
					for (int v = 0; v < npqAllList.size(); v++) {
						Long point = npqAllList.get(v).getPoint() == null ? 0 : npqAllList.get(v).getPoint();
						double d = Math.round(point.doubleValue() / allPoint.doubleValue() * 100.0 * 100.0) / 100.0;
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(d + "%");
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				break;
			}
		}
		rowIndex = row.getRowNum() + 2;
		cellIndex = 0;

		/// 欄位A7
		for (int y = 0; y < tableCellHeaders5.length; y++) {
			row = sheet.createRow(rowIndex + y);
			cell = row.createCell(cellIndex);
			cell.setCellValue(tableCellHeaders5[y]);
			cell.setCellStyle(cellStyle);
			switch (y) {
			case 0:
				if (npqIpList.size() > 0) {
					for (int v = 0; v < npqIpList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(npqIpList.get(v).getName());
						cell.setCellStyle(cellStyle);
					}
					cellIndex = 0;
				}
				break;
			case 1:
				if (npqIpList.size() > 0) {
					for (int v = 0; v < npqIpList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(
								npqIpList.get(v).getPoint() == null ? 0 : npqIpList.get(v).getPoint().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				break;
			case 2:
				if (npqIpList.size() > 0) {
					for (int v = 0; v < npqIpList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(npqIpList.get(v).getQuantity() == null ? 0
								: npqIpList.get(v).getQuantity().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				break;
			case 3:
				if (npqIpList.size() > 0) {
					Long allPoint = npqIpList.get(0).getPoint();
					for (int v = 0; v < npqIpList.size(); v++) {
						Long point = npqIpList.get(v).getPoint() == null ? 0 : npqIpList.get(v).getPoint();
						double d = Math.round(point.doubleValue() / allPoint.doubleValue() * 100.0 * 100.0) / 100.0;
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(d + "%");
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				break;
			}
		}
		rowIndex = row.getRowNum() + 2;
		cellIndex = 0;

		/// 欄位A12
		for (int y = 0; y < tableCellHeaders6.length; y++) {
			row = sheet.createRow(rowIndex + y);
			cell = row.createCell(cellIndex);
			cell.setCellValue(tableCellHeaders6[y]);
			cell.setCellStyle(cellStyle);
			switch (y) {
			case 0:
				if (npqOpList.size() > 0) {
					for (int v = 0; v < npqOpList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(npqOpList.get(v).getName());
						cell.setCellStyle(cellStyle);
					}
					cellIndex = 0;
				}
				else if (npqAllList.size()>0) {
					for (int v = 0; v < npqAllList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(npqAllList.get(v).getName());
						cell.setCellStyle(cellStyle);
					}
					cellIndex = 0;
				}
				break;
			case 1:
				if (npqOpList.size() > 0) {
					for (int v = 0; v < npqOpList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(
								npqOpList.get(v).getPoint() == null ? 0 : npqOpList.get(v).getPoint().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				else if (npqAllList.size()>0) {
					for (int v = 0; v < npqAllList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(0);
						cell.setCellStyle(cellStyle);
					}
					cellIndex = 0;
				}
				break;
			case 2:
				if (npqOpList.size() > 0) {
					for (int v = 0; v < npqOpList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(npqOpList.get(v).getQuantity() == null ? 0
								: npqOpList.get(v).getQuantity().doubleValue());
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				else if (npqAllList.size()>0) {
					for (int v = 0; v < npqAllList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(0);
						cell.setCellStyle(cellStyle);
					}
					cellIndex = 0;
				}
				break;
			case 3:
				if (npqOpList.size() > 0) {
					Long allPoint = npqOpList.get(0).getPoint();
					for (int v = 0; v < npqOpList.size(); v++) {
						Long point = npqOpList.get(v).getPoint() == null ? 0 : npqOpList.get(v).getPoint();
						double d = Math.round(point.doubleValue() / allPoint.doubleValue() * 100.0 * 100.0) / 100.0;
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(d + "%");
						cell.setCellStyle(cellFormatStyle);
					}
					cellIndex = 0;
				}
				else if (npqAllList.size()>0) {
					for (int v = 0; v < npqAllList.size(); v++) {
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(0);
						cell.setCellStyle(cellStyle);
					}
					cellIndex = 0;
				}
				break;
			}
		}

		/// 最後設定autosize
		if (npqAllList.size() > 0) {

			for (int i = 0; i < npqAllList.size(); i++) {
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
			}
		} else {
			for (int i = 0; i < tableCellHeaders.length + 2; i++) {
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
			}
		}

		if (funcType != null && !funcType.equals("00")) {
			/// 新建工作表
			cellIndex = 0;
			rowIndex = 0;
			List<CODE_TABLE> ctList = codeTableDao.findByCodeAndCat(funcType, "FUNC_TYPE");
			sheet = workbook.createSheet("(" + ctList.get(0).getDescChi() + ")");
			tableCellHeaders = new String[] { "門急診/住院(含手術)", "申報點數", "醫令數量", "比例" };
			tableCellHeaders2 = new String[] { "住院(含手術)", "申報點數", "醫令數量", "比例" };
			tableCellHeaders3 = new String[] { "門急診(含手術)", "申報點數", "醫令數量", "比例" };
			tableCellHeaders4 = new String[] { "門急診/住院(含手術)", "自費點數", "醫令數量", "比例" };
			tableCellHeaders5 = new String[] { "住院(含手術)", "自費點數", "醫令數量", "比例" };
			tableCellHeaders6 = new String[] { "門急診(含手術)", "自費點數", "醫令數量", "比例" };
			npqAllList = ppftModel.getPayByOrderType().getAll();
			npqIpList = ppftModel.getPayByOrderType().getIp();
			npqOpList = ppftModel.getPayByOrderType().getOp();
			if (npqAllList.size() > 0) {
				Long point = 0L;
				Long quantity = 0L;
				NameCodePointQuantity n = new NameCodePointQuantity();
				for (NameCodePointQuantity npq : npqAllList) {
					point += npq.getPoint();
					quantity += npq.getQuantity();
				}
				n.setName("全品項");
				n.setCode("00");
				n.setPoint(point);
				n.setQuantity(quantity);
				npqAllList.add(0, n);
			}
			if (npqIpList.size() > 0) {
				Long point = 0L;
				Long quantity = 0L;
				NameCodePointQuantity n = new NameCodePointQuantity();
				for (NameCodePointQuantity npq : npqIpList) {
					point += npq.getPoint();
					quantity += npq.getQuantity();
				}
				n.setName("全品項");
				n.setCode("00");
				n.setPoint(point);
				n.setQuantity(quantity);
				npqIpList.add(0, n);
			}
			if (npqOpList.size() > 0) {
				Long point = 0L;
				Long quantity = 0L;
				NameCodePointQuantity n = new NameCodePointQuantity();
				for (NameCodePointQuantity npq : npqOpList) {
					point += npq.getPoint();
					quantity += npq.getQuantity();
				}
				n.setName("全品項");
				n.setCode("00");
				n.setPoint(point);
				n.setQuantity(quantity);
				npqOpList.add(0, n);
			}
			/// 欄位A1
			row = sheet.createRow(0);
			cell = row.createCell(0);
			cell.setCellValue("科別名：");
			cell.setCellStyle(cellStyle);
			/// 欄位B1
			cell = row.createCell(1);
			cell.setCellValue(ctList.get(0).getDescChi());
			cell.setCellStyle(cellStyle);

			/// 欄位A2
			row = sheet.createRow(2);
			cell = row.createCell(0);
			cell.setCellValue("醫療費用比例與申報點數");
			cell.setCellStyle(cellStyle);

			rowIndex = 3;
			/// 欄位A3
			for (int y = 0; y < tableCellHeaders.length; y++) {
				row = sheet.createRow(rowIndex + y);
				cell = row.createCell(cellIndex);
				cell.setCellValue(tableCellHeaders[y]);
				cell.setCellStyle(cellStyle);
				switch (y) {
				case 0:
					if (npqAllList.size() > 0) {
						for (int v = 0; v < npqAllList.size(); v++) {
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(npqAllList.get(v).getName());
							cell.setCellStyle(cellStyle);
						}
						cellIndex = 0;
					}
					break;
				case 1:
					if (npqAllList.size() > 0) {
						for (int v = 0; v < npqAllList.size(); v++) {
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(npqAllList.get(v).getPoint() == null ? 0
									: npqAllList.get(v).getPoint().doubleValue());
							cell.setCellStyle(cellFormatStyle);
						}
						cellIndex = 0;
					}
					break;
				case 2:
					if (npqAllList.size() > 0) {
						for (int v = 0; v < npqAllList.size(); v++) {
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(npqAllList.get(v).getQuantity() == null ? 0
									: npqAllList.get(v).getQuantity().doubleValue());
							cell.setCellStyle(cellFormatStyle);
						}
						cellIndex = 0;
					}
					break;
				case 3:
					if (npqAllList.size() > 0) {
						Long allPoint = npqAllList.get(0).getPoint();
						for (int v = 0; v < npqAllList.size(); v++) {
							Long point = npqAllList.get(v).getPoint() == null ? 0 : npqAllList.get(v).getPoint();
							double d = Math.round(point.doubleValue() / allPoint.doubleValue() * 100.0 * 100.0) / 100.0;
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(d + "%");
							cell.setCellStyle(cellFormatStyle);
						}
						cellIndex = 0;
					}
					break;
				}
			}
			rowIndex = row.getRowNum() + 2;
			cellIndex = 0;

			/// 欄位A7
			for (int y = 0; y < tableCellHeaders2.length; y++) {
				row = sheet.createRow(rowIndex + y);
				cell = row.createCell(cellIndex);
				cell.setCellValue(tableCellHeaders2[y]);
				cell.setCellStyle(cellStyle);
				switch (y) {
				case 0:
					if (npqIpList.size() > 0) {
						for (int v = 0; v < npqIpList.size(); v++) {
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(npqIpList.get(v).getName());
							cell.setCellStyle(cellStyle);
						}
						cellIndex = 0;
					}
					break;
				case 1:
					if (npqIpList.size() > 0) {
						for (int v = 0; v < npqIpList.size(); v++) {
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(npqIpList.get(v).getPoint() == null ? 0
									: npqIpList.get(v).getPoint().doubleValue());
							cell.setCellStyle(cellFormatStyle);
						}
						cellIndex = 0;
					}
					break;
				case 2:
					if (npqIpList.size() > 0) {
						for (int v = 0; v < npqIpList.size(); v++) {
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(npqIpList.get(v).getQuantity() == null ? 0
									: npqIpList.get(v).getQuantity().doubleValue());
							cell.setCellStyle(cellFormatStyle);
						}
						cellIndex = 0;
					}
					break;
				case 3:
					if (npqIpList.size() > 0) {
						Long allPoint = npqIpList.get(0).getPoint();
						for (int v = 0; v < npqIpList.size(); v++) {
							Long point = npqIpList.get(v).getPoint() == null ? 0 : npqIpList.get(v).getPoint();
							double d = Math.round(point.doubleValue() / allPoint.doubleValue() * 100.0 * 100.0) / 100.0;
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(d + "%");
							cell.setCellStyle(cellFormatStyle);
						}
						cellIndex = 0;
					}
					break;
				}
			}
			rowIndex = row.getRowNum() + 2;
			cellIndex = 0;

			/// 欄位A12
			for (int y = 0; y < tableCellHeaders3.length; y++) {
				row = sheet.createRow(rowIndex + y);
				cell = row.createCell(cellIndex);
				cell.setCellValue(tableCellHeaders3[y]);
				cell.setCellStyle(cellStyle);
				switch (y) {
				case 0:
					if (npqOpList.size() > 0) {
						for (int v = 0; v < npqOpList.size(); v++) {
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(npqOpList.get(v).getName());
							cell.setCellStyle(cellStyle);
						}
						cellIndex = 0;
					}
					break;
				case 1:
					if (npqOpList.size() > 0) {
						for (int v = 0; v < npqOpList.size(); v++) {
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(npqOpList.get(v).getPoint() == null ? 0
									: npqOpList.get(v).getPoint().doubleValue());
							cell.setCellStyle(cellFormatStyle);
						}
						cellIndex = 0;
					}
					break;
				case 2:
					if (npqOpList.size() > 0) {
						for (int v = 0; v < npqOpList.size(); v++) {
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(npqOpList.get(v).getQuantity() == null ? 0
									: npqOpList.get(v).getQuantity().doubleValue());
							cell.setCellStyle(cellFormatStyle);
						}
						cellIndex = 0;
					}
					break;
				case 3:
					if (npqOpList.size() > 0) {
						Long allPoint = npqOpList.get(0).getPoint();
						for (int v = 0; v < npqOpList.size(); v++) {
							Long point = npqOpList.get(v).getPoint() == null ? 0 : npqOpList.get(v).getPoint();
							double d = Math.round(point.doubleValue() / allPoint.doubleValue() * 100.0 * 100.0) / 100.0;
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(d + "%");
							cell.setCellStyle(cellFormatStyle);
						}
						cellIndex = 0;
					}
					break;
				}
			}

			rowIndex = row.getRowNum() + 2;
			cellIndex = 0;
			/// 欄位A18
			row = sheet.createRow(rowIndex);
			cell = row.createCell(0);
			cell.setCellValue("醫療費用自費比例與申報點數");
			cell.setCellStyle(cellStyle);

			npqAllList = ppftModel.getOwnExpByOrderType().getAll();
			npqIpList = ppftModel.getOwnExpByOrderType().getIp();
			npqOpList = ppftModel.getOwnExpByOrderType().getOp();
			if (npqAllList.size() > 0) {
				Long point = 0L;
				Long quantity = 0L;
				NameCodePointQuantity n = new NameCodePointQuantity();
				for (NameCodePointQuantity npq : npqAllList) {
					point += npq.getPoint();
					quantity += npq.getQuantity();
				}
				n.setName("全品項");
				n.setCode("00");
				n.setPoint(point);
				n.setQuantity(quantity);
				npqAllList.add(0, n);
			}
			if (npqIpList.size() > 0) {
				Long point = 0L;
				Long quantity = 0L;
				NameCodePointQuantity n = new NameCodePointQuantity();
				for (NameCodePointQuantity npq : npqIpList) {
					point += npq.getPoint();
					quantity += npq.getQuantity();
				}
				n.setName("全品項");
				n.setCode("00");
				n.setPoint(point);
				n.setQuantity(quantity);
				npqIpList.add(0, n);
			}
			if (npqOpList.size() > 0) {
				Long point = 0L;
				Long quantity = 0L;
				NameCodePointQuantity n = new NameCodePointQuantity();
				for (NameCodePointQuantity npq : npqOpList) {
					point += npq.getPoint();
					quantity += npq.getQuantity();
				}
				n.setName("全品項");
				n.setCode("00");
				n.setPoint(point);
				n.setQuantity(quantity);
				npqOpList.add(0, n);
			}
			rowIndex++;
			/// 欄位A19
			for (int y = 0; y < tableCellHeaders4.length; y++) {
				row = sheet.createRow(rowIndex + y);
				cell = row.createCell(cellIndex);
				cell.setCellValue(tableCellHeaders4[y]);
				cell.setCellStyle(cellStyle);
				switch (y) {
				case 0:
					if (npqAllList.size() > 0) {
						for (int v = 0; v < npqAllList.size(); v++) {
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(npqAllList.get(v).getName());
							cell.setCellStyle(cellStyle);
						}
						cellIndex = 0;
					}
					break;
				case 1:
					if (npqAllList.size() > 0) {
						for (int v = 0; v < npqAllList.size(); v++) {
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(npqAllList.get(v).getPoint() == null ? 0
									: npqAllList.get(v).getPoint().doubleValue());
							cell.setCellStyle(cellFormatStyle);
						}
						cellIndex = 0;
					}
					break;
				case 2:
					if (npqAllList.size() > 0) {
						for (int v = 0; v < npqAllList.size(); v++) {
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(npqAllList.get(v).getQuantity() == null ? 0
									: npqAllList.get(v).getQuantity().doubleValue());
							cell.setCellStyle(cellFormatStyle);
						}
						cellIndex = 0;
					}
					break;
				case 3:
					if (npqAllList.size() > 0) {
						Long allPoint = npqAllList.get(0).getPoint();
						for (int v = 0; v < npqAllList.size(); v++) {
							Long point = npqAllList.get(v).getPoint() == null ? 0 : npqAllList.get(v).getPoint();
							double d = Math.round(point.doubleValue() / allPoint.doubleValue() * 100.0 * 100.0) / 100.0;
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(d + "%");
							cell.setCellStyle(cellFormatStyle);
						}
						cellIndex = 0;
					}
					break;
				}
			}
			rowIndex = row.getRowNum() + 2;
			cellIndex = 0;

			/// 欄位A7
			for (int y = 0; y < tableCellHeaders5.length; y++) {
				row = sheet.createRow(rowIndex + y);
				cell = row.createCell(cellIndex);
				cell.setCellValue(tableCellHeaders5[y]);
				cell.setCellStyle(cellStyle);
				switch (y) {
				case 0:
					if (npqIpList.size() > 0) {
						for (int v = 0; v < npqIpList.size(); v++) {
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(npqIpList.get(v).getName());
							cell.setCellStyle(cellStyle);
						}
						cellIndex = 0;
					}
					break;
				case 1:
					if (npqIpList.size() > 0) {
						for (int v = 0; v < npqIpList.size(); v++) {
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(npqIpList.get(v).getPoint() == null ? 0
									: npqIpList.get(v).getPoint().doubleValue());
							cell.setCellStyle(cellFormatStyle);
						}
						cellIndex = 0;
					}
					break;
				case 2:
					if (npqIpList.size() > 0) {
						for (int v = 0; v < npqIpList.size(); v++) {
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(npqIpList.get(v).getQuantity() == null ? 0
									: npqIpList.get(v).getQuantity().doubleValue());
							cell.setCellStyle(cellFormatStyle);
						}
						cellIndex = 0;
					}
					break;
				case 3:
					if (npqIpList.size() > 0) {
						Long allPoint = npqIpList.get(0).getPoint();
						for (int v = 0; v < npqIpList.size(); v++) {
							Long point = npqIpList.get(v).getPoint() == null ? 0 : npqIpList.get(v).getPoint();
							double d = Math.round(point.doubleValue() / allPoint.doubleValue() * 100.0 * 100.0) / 100.0;
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(d + "%");
							cell.setCellStyle(cellFormatStyle);
						}
						cellIndex = 0;
					}
					break;
				}
			}
			rowIndex = row.getRowNum() + 2;
			cellIndex = 0;

			/// 欄位A12
			for (int y = 0; y < tableCellHeaders6.length; y++) {
				row = sheet.createRow(rowIndex + y);
				cell = row.createCell(cellIndex);
				cell.setCellValue(tableCellHeaders6[y]);
				cell.setCellStyle(cellStyle);
				switch (y) {
				case 0:
					if (npqOpList.size() > 0) {
						for (int v = 0; v < npqOpList.size(); v++) {
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(npqOpList.get(v).getName());
							cell.setCellStyle(cellStyle);
						}
						cellIndex = 0;
					}
					else if (npqAllList.size()>0) {
						for (int v = 0; v < npqAllList.size(); v++) {
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(npqAllList.get(v).getName());
							cell.setCellStyle(cellStyle);
						}
						cellIndex = 0;
					}
					break;
				case 1:
					if (npqOpList.size() > 0) {
						for (int v = 0; v < npqOpList.size(); v++) {
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(npqOpList.get(v).getPoint() == null ? 0
									: npqOpList.get(v).getPoint().doubleValue());
							cell.setCellStyle(cellFormatStyle);
						}
						cellIndex = 0;
					}
					else if (npqAllList.size()>0) {
						for (int v = 0; v < npqAllList.size(); v++) {
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(0);
							cell.setCellStyle(cellStyle);
						}
						cellIndex = 0;
					}
					break;
				case 2:
					if (npqOpList.size() > 0) {
						for (int v = 0; v < npqOpList.size(); v++) {
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(npqOpList.get(v).getQuantity() == null ? 0
									: npqOpList.get(v).getQuantity().doubleValue());
							cell.setCellStyle(cellFormatStyle);
						}
						cellIndex = 0;
					}
					else if (npqAllList.size()>0) {
						for (int v = 0; v < npqAllList.size(); v++) {
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(0);
							cell.setCellStyle(cellStyle);
						}
						cellIndex = 0;
					}
					break;
				case 3:
					if (npqOpList.size() > 0) {
						Long allPoint = npqOpList.get(0).getPoint();
						for (int v = 0; v < npqOpList.size(); v++) {
							Long point = npqOpList.get(v).getPoint() == null ? 0 : npqOpList.get(v).getPoint();
							double d = Math.round(point.doubleValue() / allPoint.doubleValue() * 100.0 * 100.0) / 100.0;
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(d + "%");
							cell.setCellStyle(cellFormatStyle);
						}
						cellIndex = 0;
					}
					else if (npqAllList.size()>0) {
						for (int v = 0; v < npqAllList.size(); v++) {
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(0);
							cell.setCellStyle(cellStyle);
						}
						cellIndex = 0;
					}
					break;
				}
			}

			/// 最後設定autosize
			if (npqAllList.size() > 0) {

				for (int i = 0; i < npqAllList.size(); i++) {
					sheet.autoSizeColumn(i);
					sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
				}
			} else {
				for (int i = 0; i < tableCellHeaders.length + 2; i++) {
					sheet.autoSizeColumn(i);
					sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
				}
			}
		}

		/// 新建工作表
		cellIndex = 0;
		rowIndex = 0;
		sheet = workbook.createSheet("(全院)趨勢圖");
		tableCellHeaders = new String[] { "門急診申報點數趨勢圖", "門急診自費趨勢圖", "住院申報點數趨勢圖", "住院自費趨勢圖" };
		tableCellHeaders2 = new String[] { "週數", "點數", "週數", "點數", "週數", "點數", "週數", "點數" };
		NameValueList nvOp = ppwModel.getOp();
		NameValueList nvIp = ppwModel.getIp();
		NameValueList nveOp = ppwModel.getOwnExpOp();
		NameValueList nveIp = ppwModel.getOwnExpIp();
		/// 欄位A2
		row = sheet.createRow(1);
		cell = row.createCell(0);
		cell.setCellValue("全院");
		cell.setCellStyle(cellStyle);

		/// 欄位A3
		row = sheet.createRow(2);
		int y1 = 2;
		int y2 = 2;
		int x1 = 0;
		int x2 = 1;
		for (String name : tableCellHeaders) {
			for (int i = 0; i < 2; i++) {
				cell = row.createCell(cellIndex + i);
				cell.setCellStyle(cellTitleStyle);
				if (i == 0) {

					cell.setCellValue(name);
				}
			}
			/// merge欄位
			sheet.addMergedRegion(new CellRangeAddress(y1, y2, x1, x2));
			x1 += 2;
			x2 += 2;
			cellIndex += 2;
		}
		cellIndex = 0;
		rowIndex = 3;
		row = sheet.createRow(rowIndex);
		for (String name : tableCellHeaders2) {
			cell = row.createCell(cellIndex);
			cell.setCellValue(name);
			cell.setCellStyle(cellStyle);
			cellIndex++;
		}
		cellIndex = 0;
		rowIndex = 4;
		if (nvOp.getNames() != null && nvOp.getNames().size() > 0) {
			for (int v = 0; v < nvOp.getNames().size(); v++) {
				/// 門急診申報點數
				row = sheet.createRow(rowIndex + v);
				cell = row.createCell(cellIndex);
				cell.setCellValue(nvOp.getNames().get(v));
				cell.setCellStyle(cellStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(nvOp.getValues().get(v).doubleValue());
				cell.setCellStyle(cellFormatStyle);
				/// 門急診自費
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(nveOp.getNames().get(v));
				cell.setCellStyle(cellStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(nveOp.getValues().get(v).doubleValue());
				cell.setCellStyle(cellFormatStyle);
				/// 住院申報點數
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(nvIp.getNames().get(v));
				cell.setCellStyle(cellStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(nvIp.getValues().get(v).doubleValue());
				cell.setCellStyle(cellFormatStyle);
				/// 住院自費
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(nveIp.getNames().get(v));
				cell.setCellStyle(cellStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(nveIp.getValues().get(v).doubleValue());
				cell.setCellStyle(cellFormatStyle);

				cellIndex = 0;
			}
		}

		/// 最後autosize
		for (int i = 0; i < tableCellHeaders2.length; i++) {
			sheet.autoSizeColumn(i);
			sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
		}

		/// 如果有單選趨勢圖
		if (funcType != null && !funcType.equals("00")) {/// 新建工作表
			/// 新建工作表
			cellIndex = 0;
			rowIndex = 0;
			List<CODE_TABLE> ctList = codeTableDao.findByCodeAndCat(funcType, "FUNC_TYPE");
			String sheetName = "("+ctList.get(0).getDescChi()+")趨勢圖";
			System.out.println(sheetName);
			sheet = workbook.createSheet(sheetName);
			tableCellHeaders = new String[] { "門急診申報點數趨勢圖", "門急診自費趨勢圖", "住院申報點數趨勢圖", "住院自費趨勢圖" };
			tableCellHeaders2 = new String[] { "週數", "點數", "週數", "點數", "週數", "點數", "週數", "點數" };
			nvOp = ppwftModel.getOp();
			nvIp = ppwftModel.getIp();
			nveOp = ppwftModel.getOwnExpOp();
			nveIp = ppwftModel.getOwnExpIp();
			/// 欄位A2
			row = sheet.createRow(1);
			cell = row.createCell(0);
			cell.setCellValue("全院");
			cell.setCellStyle(cellStyle);

			/// 欄位A3
			row = sheet.createRow(2);
			y1 = 2;
			y2 = 2;
			x1 = 0;
			x2 = 1;
			for (String name : tableCellHeaders) {
				for (int i = 0; i < 2; i++) {
					cell = row.createCell(cellIndex + i);
					cell.setCellStyle(cellTitleStyle);
					if (i == 0) {

						cell.setCellValue(name);
					}
				}
				/// merge欄位
				sheet.addMergedRegion(new CellRangeAddress(y1, y2, x1, x2));
				x1 += 2;
				x2 += 2;
				cellIndex += 2;
			}
			cellIndex = 0;
			rowIndex = 3;
			row = sheet.createRow(rowIndex);
			for (String name : tableCellHeaders2) {
				cell = row.createCell(cellIndex);
				cell.setCellValue(name);
				cell.setCellStyle(cellStyle);
				cellIndex++;
			}
			cellIndex = 0;
			rowIndex = 4;

			if (nvOp.getNames() != null && nvOp.getNames().size() > 0) {
				for (int v = 0; v < nvOp.getNames().size(); v++) {
					/// 門急診申報點數
					row = sheet.createRow(rowIndex + v);
					cell = row.createCell(cellIndex);
					cell.setCellValue(nvOp.getNames().get(v));
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(nvOp.getValues().get(v).doubleValue());
					cell.setCellStyle(cellFormatStyle);
					/// 門急診自費
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(nveOp.getNames().get(v));
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(nveOp.getValues().get(v).doubleValue());
					cell.setCellStyle(cellFormatStyle);
					/// 住院申報點數
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(nvIp.getNames().get(v));
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(nvIp.getValues().get(v).doubleValue());
					cell.setCellStyle(cellFormatStyle);
					/// 住院自費
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(nveIp.getNames().get(v));
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(nveIp.getValues().get(v).doubleValue());
					cell.setCellStyle(cellFormatStyle);

					cellIndex = 0;
				}
			}

			/// 最後autosize
			for (int i = 0; i < tableCellHeaders2.length; i++) {
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
			}

		}

		String fileNameStr = "費用業務報表";
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
//		response.setContentType("application/vnd.ms-excel;charset=utf8");

		/// 最後由outputstream輸出
		OutputStream out = response.getOutputStream();

		workbook.write(out);
		out.flush();
		out.close();
		workbook.close();

	}
	

	/**
	 * 核刪資訊-匯出
	 * @param year
	 * @param quarter
	 * @param response
	 */
	public void getDeductedNoteExport(String year , String quarter, HttpServletResponse response)throws IOException {
		
		List<DeductedPayload> modelList = reportService.getDeductedNote(year, quarter);
		
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
		
		if(modelList != null && modelList.size() > 0) {
			///單季度
			if(modelList.size() == 1) {
				/// 新建工作表
				HSSFSheet sheet = workbook.createSheet("單季度核減");
				String[] tableRowHeaders = { "","非專案(隨機)","專案(隨機)","藥費(隨機)" };
				String[] tableRowHeaders2 = { "季度","非專案核減點數","專案核減點數","藥費核減點數","總抽件數","總和扣件數" };

				/// 欄位A1
				HSSFRow row = sheet.createRow(0);
				HSSFCell cell = row.createCell(0);
				cell.setCellValue("核刪季度");
				cell.setCellStyle(cellStyle);

				/// 欄位B1
				cell = row.createCell(1);
				cell.setCellValue(modelList.get(0).getDisplayName());
				cell.setCellStyle(cellStyle);
				
				/// 欄位A3
				row = sheet.createRow(2);
				cell = row.createCell(0);
				cell.setCellValue("門急診/住院總核刪點數");
				cell.setCellStyle(cellStyle);
				
				/// 欄位A4
				row = sheet.createRow(3);
				for(int x=0; x<tableRowHeaders.length; x++) {
					cell = row.createCell(x);
					cell.setCellValue(tableRowHeaders[x]);
					cell.setCellStyle(cellStyle);
				}
				
				/// 欄位A5
				row = sheet.createRow(4);
				cell = row.createCell(0);
				cell.setCellValue("門急診");
				cell.setCellStyle(cellStyle);
				cell = row.createCell(1);
				cell.setCellValue(modelList.get(0).getNoprojectAmountOp().doubleValue());
				cell.setCellStyle(cellFormatStyle);	
				cell = row.createCell(2);
				cell.setCellValue(modelList.get(0).getProjectAmountOp().doubleValue());
				cell.setCellStyle(cellFormatStyle);	
				cell = row.createCell(3);
				cell.setCellValue(modelList.get(0).getMedAmountOp().doubleValue());
				cell.setCellStyle(cellFormatStyle);	
				
				/// 欄位A5
				row = sheet.createRow(5);
				cell = row.createCell(0);
				cell.setCellValue("住院");
				cell.setCellStyle(cellStyle);
				cell = row.createCell(1);
				cell.setCellValue(modelList.get(0).getNoprojectAmountIp().doubleValue());
				cell.setCellStyle(cellFormatStyle);	
				cell = row.createCell(2);
				cell.setCellValue(modelList.get(0).getProjectAmountIp().doubleValue());
				cell.setCellStyle(cellFormatStyle);	
				cell = row.createCell(3);
				cell.setCellValue(modelList.get(0).getMedAmountIp().doubleValue());
				cell.setCellStyle(cellFormatStyle);	
				
				/// 欄位A8
				row = sheet.createRow(7);
				cell = row.createCell(0);
				cell.setCellValue("季度差異計算");
				cell.setCellStyle(cellStyle);
				/// 欄位A9
				row = sheet.createRow(8);
				for(int x=0; x<tableRowHeaders2.length; x++) {
					cell = row.createCell(x);
					cell.setCellValue(tableRowHeaders2[x]);
					cell.setCellStyle(cellStyle);
				}
				/// 欄位A10
				row = sheet.createRow(9);
				cell = row.createCell(0);
				cell.setCellValue(modelList.get(0).getDisplayName());
				cell.setCellStyle(cellStyle);
				cell = row.createCell(1);
				cell.setCellValue(modelList.get(0).getNoprojectAmountAll());
				cell.setCellStyle(cellFormatStyle);
				cell = row.createCell(2);
				cell.setCellValue(modelList.get(0).getProjectAmountAll());
				cell.setCellStyle(cellFormatStyle);
				cell = row.createCell(3);
				cell.setCellValue(modelList.get(0).getMedAmountAll());
				cell.setCellStyle(cellFormatStyle);
				cell = row.createCell(4);
				cell.setCellValue(modelList.get(0).getExtractCase());
				cell.setCellStyle(cellFormatStyle);
				cell = row.createCell(5);
				cell.setCellValue(modelList.get(0).getQuatity());
				cell.setCellStyle(cellFormatStyle);
				
				/// 最後設定autosize
				for (int i = 0; i < tableRowHeaders2.length; i++) {
					sheet.autoSizeColumn(i);
					sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
				}
				
				/// 新建工作表
				tableRowHeaders = new String[] {"健保門急診核減抽審總件數","健保門急診核減總核扣點數","健保住院核減抽審總件數","健保住院核減總核扣點數"};
				tableRowHeaders2 = new String[] {"類別","核扣件數","核扣點數","核刪醫令","核扣點數","理由","類別","核扣件數","核扣點數","核刪醫令","核扣點數","理由"};
				sheet = workbook.createSheet("季度核減資訊");
				cellIndex = 0;
				rowIndex = 0;
				/// 欄位A1
				row = sheet.createRow(0);
				cell = row.createCell(0);
				cell.setCellValue("季度核減資訊");
				cell.setCellStyle(cellTitleStyle);
				/// 欄位A2
				row = sheet.createRow(1);
				for(int x=0; x < tableRowHeaders2.length; x++) {
					cell = row.createCell(x);
					switch(x) {
					case 0:
						cell.setCellValue(tableRowHeaders[0]);
						break;
					case 3:
						cell.setCellValue(tableRowHeaders[1]);
						break;
					case 6:
						cell.setCellValue(tableRowHeaders[2]);
						break;
					case 9:
						cell.setCellValue(tableRowHeaders[3]);
						break;
					default:
						break;
					}
					cell.setCellStyle(cellTitleStyle);
				}
				/// 欄位merge
				int x1 = 0;
				int x2 = 2;
				for(int x=0; x < tableRowHeaders.length; x++) {
					sheet.addMergedRegion(new CellRangeAddress(1, 1, x1, x2));
					x1 += 3;
					x2 += 3;
				}
				/// 欄位A3
				row = sheet.createRow(2);
				for(int x=0; x < tableRowHeaders2.length; x++) {
					cell = row.createCell(x);
					cell.setCellValue(tableRowHeaders2[x]);
					cell.setCellStyle(cellStyle);
				}
				cellIndex = 0;
				/// 欄位A4 健保門急診核減抽審總件數
				row = sheet.createRow(3);
				cell = row.createCell(cellIndex);
				cell.setCellValue("非專案(隨機)");
				cell.setCellStyle(cellStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(modelList.get(0).getNoprojectQuantityOp().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(modelList.get(0).getNoprojectAmountOp().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				
				List<Map<String,Object>> deductedNoteInfos = modelList.get(0).getDeductedList();
				///門急診資料
				List<Map<String,Object>> opInfos = new ArrayList<Map<String,Object>>();
				///住院資料
				List<Map<String,Object>> ipInfos = new ArrayList<Map<String,Object>>();
				
				if(deductedNoteInfos.size() > 0) {
					for(Map<String, Object> map : deductedNoteInfos) {
						String dateformat = map.get("dataFormat").toString();
						if(dateformat.equals("10")) {
							opInfos.add(map);
						}
						else {
							ipInfos.add(map);
						}
					}
				}
				///健保門急診核減總核扣點數
				if(opInfos.size()>0) {
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(opInfos.get(0).get("name").toString());
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(Long.valueOf(opInfos.get(0).get("amount").toString()).doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(opInfos.get(0).get("reason").toString());
					cell.setCellStyle(cellFormatStyle);
					///remove已顯示資料列
					opInfos.remove(0);
					cellIndex++;
				}
				else {
					cellIndex +=3;
				}
				///健保住院核減抽審總件數
				cell = row.createCell(cellIndex);
				cell.setCellValue("非專案(隨機)");
				cell.setCellStyle(cellStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(modelList.get(0).getNoprojectQuantityIp().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(modelList.get(0).getNoprojectAmountIp().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				///健保住院核減總核扣點數
				if(ipInfos.size() > 0) {
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(ipInfos.get(0).get("name").toString());
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(Long.valueOf(ipInfos.get(0).get("amount").toString()).doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(ipInfos.get(0).get("reason").toString());
					cell.setCellStyle(cellFormatStyle);
					///remove已顯示資料列
					ipInfos.remove(0);
				}
				cellIndex = 0;
				/// 欄位A5 健保門急診核減抽審總件數
				row = sheet.createRow(4);
				cell = row.createCell(cellIndex);
				cell.setCellValue("專案(隨機)");
				cell.setCellStyle(cellStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(modelList.get(0).getProjectQuantityOp().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(modelList.get(0).getProjectAmountOp().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				///健保門急診核減總核扣點數
				if(opInfos.size() > 0) {
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(opInfos.get(0).get("name").toString());
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(Long.valueOf(opInfos.get(0).get("amount").toString()).doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(opInfos.get(0).get("reason").toString());
					cell.setCellStyle(cellFormatStyle);
					///remove已顯示資料列
					opInfos.remove(0);
					cellIndex++;
				}
				else {
					cellIndex +=3;
				}
				///健保住院核減抽審總件數
				cell = row.createCell(cellIndex);
				cell.setCellValue("專案(隨機)");
				cell.setCellStyle(cellStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(modelList.get(0).getProjectQuantityIp().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(modelList.get(0).getProjectAmountIp().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				///健保住院核減總核扣點數
				if(ipInfos.size() > 0) {
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(ipInfos.get(0).get("name").toString());
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(Long.valueOf(ipInfos.get(0).get("amount").toString()).doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(ipInfos.get(0).get("reason").toString());
					cell.setCellStyle(cellFormatStyle);
					///remove已顯示資料列
					ipInfos.remove(0);
				}
				cellIndex = 0;
				/// 欄位A6 健保門急診核減抽審總件數
				row = sheet.createRow(5);
				cell = row.createCell(cellIndex);
				cell.setCellValue("藥費(隨機)");
				cell.setCellStyle(cellStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(modelList.get(0).getMedQuantityOp().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(modelList.get(0).getMedAmountOp().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				///健保門急診核減總核扣點數
				if(opInfos.size() > 0) {
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(opInfos.get(0).get("name").toString());
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(Long.valueOf(opInfos.get(0).get("amount").toString()).doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(opInfos.get(0).get("reason").toString());
					cell.setCellStyle(cellFormatStyle);
					///remove已顯示資料列
					opInfos.remove(0);
					cellIndex++;
				}
				else {
					cellIndex +=3;
				}
				///健保住院核減抽審總件數
				cell = row.createCell(cellIndex);
				cell.setCellValue("藥費(隨機)");
				cell.setCellStyle(cellStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(modelList.get(0).getMedQuantityIp().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				cellIndex++;
				cell = row.createCell(cellIndex);
				cell.setCellValue(modelList.get(0).getMedAmountIp().doubleValue());
				cell.setCellStyle(cellFormatStyle);
				///健保住院核減總核扣點數
				if(ipInfos.size() > 0) {
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(ipInfos.get(0).get("name").toString());
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(Long.valueOf(ipInfos.get(0).get("amount").toString()).doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(ipInfos.get(0).get("reason").toString());
					cell.setCellStyle(cellFormatStyle);
					///remove已顯示資料列
					ipInfos.remove(0);
				}
				rowIndex = 5;
				///最後判斷資料是否存在，有存在就用迴圈顯示
				if(opInfos.size() > 0 || ipInfos.size() > 0) {
					int loopCount = opInfos.size()+ipInfos.size();
					for(int i=0; i<loopCount;i++) {
						rowIndex++;
						row = sheet.createRow(rowIndex);
						if(opInfos.size() > 0) {
							cellIndex = 3;
							cell = row.createCell(cellIndex);
							cell.setCellValue(opInfos.get(0).get("name").toString());
							cell.setCellStyle(cellStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(Long.valueOf(opInfos.get(0).get("amount").toString()).doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(opInfos.get(0).get("reason").toString());
							cell.setCellStyle(cellFormatStyle);
							///remove已顯示資料列
							opInfos.remove(0);
						}
						if( ipInfos.size() > 0 ) {
							cellIndex = 9;
							cell = row.createCell(cellIndex);
							cell.setCellValue(ipInfos.get(0).get("name").toString());
							cell.setCellStyle(cellStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(Long.valueOf(ipInfos.get(0).get("amount").toString()).doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(ipInfos.get(0).get("reason").toString());
							cell.setCellStyle(cellFormatStyle);
							///remove已顯示資料列
							ipInfos.remove(0);
						}
					}
				}
				/// 最後設定autosize
				for (int i = 0; i < tableRowHeaders2.length; i++) {
					sheet.autoSizeColumn(i);
					sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
				}
				
				/// 新建工作表
				tableRowHeaders = new String[] {"核刪醫令","放大回推金額","申復數量","申復金額","申復補付數量","申復補復金額"};
				sheet = workbook.createSheet("放大回推核減資訊(門急)");
				cellIndex = 0;
				rowIndex = 0;
				List<Map<String,Object>> rollbacks = modelList.get(0).getRollbackList();
				///門急診資料
				List<Map<String,Object>> opRollbacks = new ArrayList<Map<String,Object>>();
				///住院資料
				List<Map<String,Object>> ipRollbacks = new ArrayList<Map<String,Object>>();
				if(rollbacks != null && rollbacks.size() > 0) {
					for(int x=0; x<rollbacks.size();x++) {
						String dataformat = rollbacks.get(x).get("dataFormat").toString();
						if(dataformat.equals("10")) {
							opRollbacks.add(rollbacks.get(x));
						}
						else {
							ipRollbacks.add(rollbacks.get(x));
						}
						
					}
				}
				/// 欄位A1
				row = sheet.createRow(0);
				for(int x=0; x<tableRowHeaders.length;x++) {
					cell = row.createCell(x);
					if(x==0) {
						cell.setCellValue("健保門急診放大回推");
					}
					cell.setCellStyle(cellTitleStyle);
				}
				/// merge欄位
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, tableRowHeaders.length - 1));
				
				/// 欄位A2
				row = sheet.createRow(1);
				for(int x=0; x<tableRowHeaders.length;x++) {
					cell = row.createCell(x);
					cell.setCellValue(tableRowHeaders[x]);
					cell.setCellStyle(cellStyle);
				}
				
				/// 欄位A3
				row = sheet.createRow(2);
				rowIndex = 3;
				cellIndex = 0;
				if(opRollbacks.size() > 0) {
					for(int x=0; x<opRollbacks.size();x++) {
					    cell = row.createCell(cellIndex);
					    cell.setCellValue(opRollbacks.get(x).get("name").toString());
					    cell.setCellStyle(cellStyle);
					    cellIndex++;
					    cell = row.createCell(cellIndex);
					    cell.setCellValue(Long.valueOf(opRollbacks.get(x).get("amount").toString()).doubleValue());
					    cell.setCellStyle(cellFormatStyle);
					    cellIndex++;
					    cell = row.createCell(cellIndex);
					    cell.setCellValue(Long.valueOf(opRollbacks.get(x).get("afrQuantity").toString()).doubleValue());
					    cell.setCellStyle(cellFormatStyle);
					    cellIndex++;
					    cell = row.createCell(cellIndex);
					    cell.setCellValue(Long.valueOf(opRollbacks.get(x).get("afrAmount").toString()).doubleValue());
					    cell.setCellStyle(cellFormatStyle);
					    cellIndex++;
					    cell = row.createCell(cellIndex);
					    cell.setCellValue(Long.valueOf(opRollbacks.get(x).get("afrPayQuantity").toString()).doubleValue());
					    cell.setCellStyle(cellFormatStyle);
					    cellIndex++;
					    cell = row.createCell(cellIndex);
					    cell.setCellValue(Long.valueOf(opRollbacks.get(x).get("afrPayAmount").toString()).doubleValue());
					    cell.setCellStyle(cellFormatStyle);
					  
					    cellIndex = 0;
					    row = sheet.createRow(rowIndex+x);
					}
				}
				else {
					cell = row.createCell(cellIndex);
				    cell.setCellValue("無");
				    cell.setCellStyle(cellStyle);
				    cellIndex++;
				    cell = row.createCell(cellIndex);
				    cell.setCellValue(0);
				    cell.setCellStyle(cellFormatStyle);
				    cellIndex++;
				    cell = row.createCell(cellIndex);
				    cell.setCellValue(0);
				    cell.setCellStyle(cellFormatStyle);
				    cellIndex++;
				    cell = row.createCell(cellIndex);
				    cell.setCellValue(0);
				    cell.setCellStyle(cellFormatStyle);
				    cellIndex++;
				    cell = row.createCell(cellIndex);
				    cell.setCellValue(0);
				    cell.setCellStyle(cellFormatStyle);
				    cellIndex++;
				    cell = row.createCell(cellIndex);
				    cell.setCellValue(0);
				    cell.setCellStyle(cellFormatStyle);
				}
				/// 最後設定autosize
				for (int i = 0; i < tableRowHeaders.length; i++) {
					sheet.autoSizeColumn(i);
					sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
				}
				
				/// 新建工作表
				tableRowHeaders = new String[] {"核刪醫令","放大回推金額","申復數量","申復金額","申復補付數量","申復補復金額"};
				sheet = workbook.createSheet("放大回推核減資訊(住院)");
				cellIndex = 0;
				rowIndex = 0;
				/// 欄位A1
				row = sheet.createRow(0);
				for(int x=0; x<tableRowHeaders.length;x++) {
					cell = row.createCell(x);
					if(x==0) {
						cell.setCellValue("健保住院放大回推");
					}
					cell.setCellStyle(cellTitleStyle);
				}
				/// merge欄位
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, tableRowHeaders.length - 1));
				
				/// 欄位A2
				row = sheet.createRow(1);
				for(int x=0; x<tableRowHeaders.length;x++) {
					cell = row.createCell(x);
					cell.setCellValue(tableRowHeaders[x]);
					cell.setCellStyle(cellStyle);
				}
				
				/// 欄位A3
				row = sheet.createRow(2);
				rowIndex = 3;
				cellIndex = 0;
				if(ipRollbacks.size() > 0) {
					for(int x=0; x<ipRollbacks.size();x++) {
					    cell = row.createCell(cellIndex);
					    cell.setCellValue(ipRollbacks.get(x).get("name").toString());
					    cell.setCellStyle(cellStyle);
					    cellIndex++;
					    cell = row.createCell(cellIndex);
					    cell.setCellValue(Long.valueOf(ipRollbacks.get(x).get("amount").toString()).doubleValue());
					    cell.setCellStyle(cellFormatStyle);
					    cellIndex++;
					    cell = row.createCell(cellIndex);
					    cell.setCellValue(Long.valueOf(ipRollbacks.get(x).get("afrQuantity").toString()).doubleValue());
					    cell.setCellStyle(cellFormatStyle);
					    cellIndex++;
					    cell = row.createCell(cellIndex);
					    cell.setCellValue(Long.valueOf(ipRollbacks.get(x).get("afrAmount").toString()).doubleValue());
					    cell.setCellStyle(cellFormatStyle);
					    cellIndex++;
					    cell = row.createCell(cellIndex);
					    cell.setCellValue(Long.valueOf(ipRollbacks.get(x).get("afrPayQuantity").toString()).doubleValue());
					    cell.setCellStyle(cellFormatStyle);
					    cellIndex++;
					    cell = row.createCell(cellIndex);
					    cell.setCellValue(Long.valueOf(ipRollbacks.get(x).get("afrPayAmount").toString()).doubleValue());
					    cell.setCellStyle(cellFormatStyle);
					  
					    cellIndex = 0;
					    row = sheet.createRow(rowIndex+x);
					}
				}
				else {
					cell = row.createCell(cellIndex);
				    cell.setCellValue("無");
				    cell.setCellStyle(cellStyle);
				    cellIndex++;
				    cell = row.createCell(cellIndex);
				    cell.setCellValue(0);
				    cell.setCellStyle(cellFormatStyle);
				    cellIndex++;
				    cell = row.createCell(cellIndex);
				    cell.setCellValue(0);
				    cell.setCellStyle(cellFormatStyle);
				    cellIndex++;
				    cell = row.createCell(cellIndex);
				    cell.setCellValue(0);
				    cell.setCellStyle(cellFormatStyle);
				    cellIndex++;
				    cell = row.createCell(cellIndex);
				    cell.setCellValue(0);
				    cell.setCellStyle(cellFormatStyle);
				    cellIndex++;
				    cell = row.createCell(cellIndex);
				    cell.setCellValue(0);
				    cell.setCellStyle(cellFormatStyle);
				}
				/// 最後設定autosize
				for (int i = 0; i < tableRowHeaders.length; i++) {
					sheet.autoSizeColumn(i);
					sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
				}
				
				/// 新建工作表
				tableRowHeaders = new String[] {"核刪醫令","爭議數量","爭議金額","爭議不補付代碼"};
				sheet = workbook.createSheet("爭議核減資訊(門急)");
				cellIndex = 0;
				rowIndex = 0;
				List<Map<String,Object>> disputes = modelList.get(0).getDisputeList();
				///門急診資料
				List<Map<String,Object>> opDisputes = new ArrayList<Map<String,Object>>();
				///住院資料
				List<Map<String,Object>> ipDisputes = new ArrayList<Map<String,Object>>();
				if(disputes != null && disputes.size() > 0) {
					for(int x=0; x<disputes.size();x++) {
						String dataformat = disputes.get(x).get("dataFormat").toString();
						if(dataformat.equals("10")) {
							opDisputes.add(disputes.get(x));
						}
						else {
							ipDisputes.add(disputes.get(x));
						}
					}
				}
				/// 欄位A1
				row = sheet.createRow(0);
				for(int x=0; x<tableRowHeaders.length;x++) {
					cell = row.createCell(x);
					if(x==0) {
						cell.setCellValue("健保門急診爭議");
					}
					cell.setCellStyle(cellTitleStyle);
				}
				/// merge欄位
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, tableRowHeaders.length - 1));
				
				/// 欄位A2
				row = sheet.createRow(1);
				for(int x=0; x<tableRowHeaders.length;x++) {
					cell = row.createCell(x);
					cell.setCellValue(tableRowHeaders[x]);
					cell.setCellStyle(cellStyle);
				}
				
				/// 欄位A3
				row = sheet.createRow(2);
				rowIndex = 3;
				cellIndex = 0;
				if(opDisputes.size() > 0) {
					for(int x=0; x<opDisputes.size();x++) {
					    cell = row.createCell(cellIndex);
					    cell.setCellValue(opDisputes.get(x).get("name").toString());
					    cell.setCellStyle(cellStyle);
					    cellIndex++;
					    cell = row.createCell(cellIndex);
					    cell.setCellValue(Long.valueOf(opDisputes.get(x).get("disputeQuantity").toString()).doubleValue());
					    cell.setCellStyle(cellFormatStyle);
					    cellIndex++;
					    cell = row.createCell(cellIndex);
					    cell.setCellValue(Long.valueOf(opDisputes.get(x).get("disputeAmount").toString()).doubleValue());
					    cell.setCellStyle(cellFormatStyle);
					    cellIndex++;
					    cell = row.createCell(cellIndex);
					    cell.setCellValue(opDisputes.get(x).get("disputeNoPayCode").toString());
					    cell.setCellStyle(cellFormatStyle);
					   
					    cellIndex = 0;
					    row = sheet.createRow(rowIndex+x);
					}
				}
				else {
					cell = row.createCell(cellIndex);
				    cell.setCellValue("無");
				    cell.setCellStyle(cellStyle);
				    cellIndex++;
				    cell = row.createCell(cellIndex);
				    cell.setCellValue(0);
				    cell.setCellStyle(cellFormatStyle);
				    cellIndex++;
				    cell = row.createCell(cellIndex);
				    cell.setCellValue(0);
				    cell.setCellStyle(cellFormatStyle);
				    cellIndex++;
				    cell = row.createCell(cellIndex);
				    cell.setCellValue(0);
				    cell.setCellStyle(cellFormatStyle);
				   
				}
				/// 最後設定autosize
				for (int i = 0; i < tableRowHeaders.length; i++) {
					sheet.autoSizeColumn(i);
					sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
				}
				
				/// 新建工作表
				tableRowHeaders = new String[] {"核刪醫令","爭議數量","爭議金額","爭議不補付代碼"};
				sheet = workbook.createSheet("爭議核減資訊(住院)");
				cellIndex = 0;
				rowIndex = 0;
	
				/// 欄位A1
				row = sheet.createRow(0);
				for(int x=0; x<tableRowHeaders.length;x++) {
					cell = row.createCell(x);
					if(x==0) {
						cell.setCellValue("健保住院爭議");
					}
					cell.setCellStyle(cellTitleStyle);
				}
				/// merge欄位
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, tableRowHeaders.length - 1));
				
				/// 欄位A2
				row = sheet.createRow(1);
				for(int x=0; x<tableRowHeaders.length;x++) {
					cell = row.createCell(x);
					cell.setCellValue(tableRowHeaders[x]);
					cell.setCellStyle(cellStyle);
				}
				
				/// 欄位A3
				row = sheet.createRow(2);
				rowIndex = 3;
				cellIndex = 0;
				if(ipDisputes.size() > 0) {
					for(int x=0; x<ipDisputes.size();x++) {
					    cell = row.createCell(cellIndex);
					    cell.setCellValue(ipDisputes.get(x).get("name").toString());
					    cell.setCellStyle(cellStyle);
					    cellIndex++;
					    cell = row.createCell(cellIndex);
					    cell.setCellValue(Long.valueOf(ipDisputes.get(x).get("disputeQuantity").toString()).doubleValue());
					    cell.setCellStyle(cellFormatStyle);
					    cellIndex++;
					    cell = row.createCell(cellIndex);
					    cell.setCellValue(Long.valueOf(ipDisputes.get(x).get("disputeAmount").toString()).doubleValue());
					    cell.setCellStyle(cellFormatStyle);
					    cellIndex++;
					    cell = row.createCell(cellIndex);
					    cell.setCellValue(ipDisputes.get(x).get("disputeNoPayCode").toString());
					    cell.setCellStyle(cellFormatStyle);
					   
					    cellIndex = 0;
					    row = sheet.createRow(rowIndex+x);
					}
				}
				else {
					cell = row.createCell(cellIndex);
				    cell.setCellValue("無");
				    cell.setCellStyle(cellStyle);
				    cellIndex++;
				    cell = row.createCell(cellIndex);
				    cell.setCellValue(0);
				    cell.setCellStyle(cellFormatStyle);
				    cellIndex++;
				    cell = row.createCell(cellIndex);
				    cell.setCellValue(0);
				    cell.setCellStyle(cellFormatStyle);
				    cellIndex++;
				    cell = row.createCell(cellIndex);
				    cell.setCellValue(0);
				    cell.setCellStyle(cellFormatStyle);
				   
				}
				/// 最後設定autosize
				for (int i = 0; i < tableRowHeaders.length; i++) {
					sheet.autoSizeColumn(i);
					sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
				}
				
			}
			///多季度
			else {
				/// 新建工作表
				HSSFSheet sheet = workbook.createSheet("多季度核減");
				String[] tableRowHeaders = { "季度","非專案核減點數","專案核減點數","藥費核減點數","總抽件數","總和扣件數" };
				String[] tableRowHeaders2 = {"門急診/住院總核刪點數（非專案-隨機)","門急診/住院總核刪點數（專案-隨機)","門急診/住院總核刪點數（藥費總額)"};
				String[] tableRowHeaders3 = {"季度","門急診","住院","季度","門急診","住院","季度","門急診","住院"};
				cellIndex = 0;
				rowIndex = 0;
				/// 欄位A1
				HSSFRow row = sheet.createRow(0);
				HSSFCell cell = row.createCell(0);
				cell.setCellValue("核刪季度");
				cell.setCellStyle(cellStyle);

				/// 欄位B1
				for(int x=0; x < modelList.size(); x++) {
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(modelList.get(x).getDisplayName());
					cell.setCellStyle(cellStyle);
				}
				
				/// 欄位A3
				cellIndex = 0;
				row = sheet.createRow(2);
				cell = row.createCell(0);
				cell.setCellValue("季度差異計算");
				cell.setCellStyle(cellStyle);
				
				/// 欄位A4
				row = sheet.createRow(3);
				for(int x=0; x < tableRowHeaders.length; x++) {
					cell = row.createCell(x);
					cell.setCellValue(tableRowHeaders[x]);
					cell.setCellStyle(cellStyle);
				}
				/// 欄位A5
				rowIndex = 5;
				cellIndex = 0;
				row = sheet.createRow(4);
				for(int x=0; x < modelList.size(); x++) {
					cell = row.createCell(cellIndex);
					cell.setCellValue(modelList.get(x).getDisplayName());	
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(Long.valueOf(modelList.get(x).getNoprojectAmountAll().toString()).doubleValue());	
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(Long.valueOf(modelList.get(x).getProjectAmountAll().toString()).doubleValue());	
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(Long.valueOf(modelList.get(x).getMedAmountAll().toString()).doubleValue());	
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(Long.valueOf(modelList.get(x).getExtractCase().toString()).doubleValue());	
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(Long.valueOf(modelList.get(x).getQuatity().toString()).doubleValue());	
					cell.setCellStyle(cellFormatStyle);
					
					cellIndex = 0;
					row = sheet.createRow(rowIndex + x);
				}
				
				/// 欄位A
				rowIndex = row.getRowNum() + 1;
				cellIndex = 0;
				row = sheet.createRow(rowIndex);
				for(int x=0; x < tableRowHeaders3.length; x++) {
					cell = row.createCell(x);
					switch(x) {
					case 0:
						cell.setCellValue(tableRowHeaders2[0]);
						break;
					case 3:
						cell.setCellValue(tableRowHeaders2[1]);
						break;
					case 6:
						cell.setCellValue(tableRowHeaders2[2]);
						break;
					default:
						break;
					}
					cell.setCellStyle(cellTitleStyle);
				}
				/// merge欄位
				int x1 = 0;
				int x2 = 2;
				for(int x=0; x < tableRowHeaders2.length; x++) {
					
					sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, x1, x2));
					x1 += 3;
					x2 += 3;
				}
				rowIndex = row.getRowNum() + 1;
				
				/// 欄位A
				row = sheet.createRow(rowIndex);
				for(int x=0; x < tableRowHeaders3.length; x++) {
					cell = row.createCell(x);
					cell.setCellValue(tableRowHeaders3[x]);
					cell.setCellStyle(cellStyle);
				}
				
				rowIndex++;
				/// 欄位A
				row = sheet.createRow(rowIndex);
				cellIndex = 0;
				for(int x=0; x < modelList.size(); x++) {
					cell = row.createCell(cellIndex);
					cell.setCellValue(modelList.get(x).getDisplayName());
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(Long.valueOf(modelList.get(x).getNoprojectAmountOp().toString()).doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(Long.valueOf(modelList.get(x).getNoprojectAmountIp().toString()).doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(modelList.get(x).getDisplayName());
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(Long.valueOf(modelList.get(x).getProjectAmountOp().toString()).doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(Long.valueOf(modelList.get(x).getProjectAmountIp().toString()).doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(modelList.get(x).getDisplayName());
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(Long.valueOf(modelList.get(x).getMedAmountOp().toString()).doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(Long.valueOf(modelList.get(x).getMedAmountIp().toString()).doubleValue());
					cell.setCellStyle(cellFormatStyle);
					
					cellIndex = 0;
					row = sheet.createRow(rowIndex + x); 
				}
				/// 最後設定autosize
				for (int i = 0; i < tableRowHeaders3.length + 2; i++) {
					sheet.autoSizeColumn(i);
					sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
				}
				
				/// 新建工作表
				sheet = workbook.createSheet("多季度核減資訊(門急)");
				tableRowHeaders = new String[] {"健保門急診核減抽審總件數", "健保門急診核減總核扣點數"};
				tableRowHeaders2 = new String[] {"類別","核扣件數","核扣點數","核刪醫令","核扣點數","理由"};
				cellIndex = 0;
				rowIndex = 0;
				///資料統計
				List<List<Map<String,Object>>> infosList = new ArrayList<List<Map<String,Object>>>();
				List<Map<String,Object>> deductedNoteInfosQ1 = new ArrayList<Map<String,Object>>();
				List<Map<String,Object>> deductedNoteInfosQ2 = new ArrayList<Map<String,Object>>();
				List<Map<String,Object>> deductedNoteInfosQ3 = new ArrayList<Map<String,Object>>();
				List<Map<String,Object>> deductedNoteInfosQ4 = new ArrayList<Map<String,Object>>();
				///門急診資料
				List<Map<String,Object>> opInfosQ1 = new ArrayList<Map<String,Object>>();
				List<Map<String,Object>> opInfosQ2 = new ArrayList<Map<String,Object>>();
				List<Map<String,Object>> opInfosQ3 = new ArrayList<Map<String,Object>>();
				List<Map<String,Object>> opInfosQ4 = new ArrayList<Map<String,Object>>();
				///住院資料
				List<Map<String,Object>> ipInfosQ1 = new ArrayList<Map<String,Object>>();
				List<Map<String,Object>> ipInfosQ2 = new ArrayList<Map<String,Object>>();
				List<Map<String,Object>> ipInfosQ3 = new ArrayList<Map<String,Object>>();
				List<Map<String,Object>> ipInfosQ4 = new ArrayList<Map<String,Object>>();
				
				///將所有季度資料裝載array
				for(int i=0; i<modelList.size(); i++) {
					infosList.add(modelList.get(i).getDeductedList());
				}
				for(int i=0; i<infosList.size(); i++) {
					switch(i) {
					case 0:
						if(infosList.get(0) != null)
						deductedNoteInfosQ1.addAll(infosList.get(0));
						break;
					case 1:
						if(infosList.get(1) != null)
						deductedNoteInfosQ2.addAll(infosList.get(1));
						break;
					case 2:
						if(infosList.get(2) != null)
						deductedNoteInfosQ3.addAll(infosList.get(2));
						break;
					case 3:
						if(infosList.get(3) != null)
						deductedNoteInfosQ4.addAll(infosList.get(3));
						break;
					}
				}
				
				for(int i=0; i<deductedNoteInfosQ1.size(); i++) {
					String dateformat = deductedNoteInfosQ1.get(i).get("dataFormat").toString();
					if(dateformat.equals("10")) {
						opInfosQ1.add(deductedNoteInfosQ1.get(i));
					}
					else {
						ipInfosQ1.add(deductedNoteInfosQ1.get(i));
					}
				}
				for(int i=0; i<deductedNoteInfosQ2.size(); i++) {
					String dateformat = deductedNoteInfosQ2.get(i).get("dataFormat").toString();
					if(dateformat.equals("10")) {
						opInfosQ2.add(deductedNoteInfosQ2.get(i));
					}
					else {
						ipInfosQ2.add(deductedNoteInfosQ2.get(i));
					}
				}
				for(int i=0; i<deductedNoteInfosQ3.size(); i++) {
					String dateformat = deductedNoteInfosQ3.get(i).get("dataFormat").toString();
					if(dateformat.equals("10")) {
						opInfosQ3.add(deductedNoteInfosQ3.get(i));
					}
					else {
						ipInfosQ3.add(deductedNoteInfosQ3.get(i));
					}
				}
				for(int i=0; i<deductedNoteInfosQ4.size(); i++) {
					String dateformat = deductedNoteInfosQ4.get(i).get("dataFormat").toString();
					if(dateformat.equals("10")) {
						opInfosQ2.add(deductedNoteInfosQ4.get(i));
					}
					else {
						ipInfosQ2.add(deductedNoteInfosQ4.get(i));
					}
				}
				
				/// 欄位A1
				row = sheet.createRow(0);
				cell = row.createCell(0);
				cell.setCellValue("多季度核減資訊");
				cell.setCellStyle(cellStyle);
				
				/// 欄位A2
				row = sheet.createRow(1);
				cell = row.createCell(0);
				cell.setCellStyle(cellStyle);
				cellIndex = 1;
				for(int x=0; x<tableRowHeaders2.length; x++) {
					cell = row.createCell(cellIndex + x);
					if(x==0) {
						cell.setCellValue(tableRowHeaders[0]);
					}
					else if(x==3) {
						cell.setCellValue(tableRowHeaders[1]);
					}
					cell.setCellStyle(cellTitleStyle);
				}
				/// merge欄位
				x1 = 1;
				x2 = 3;
				for(int x=0; x<tableRowHeaders.length; x++) {
					sheet.addMergedRegion(new CellRangeAddress(1, 1, x1, x2));
					x1 += 3;
					x2 += 3;
				}
				
				/// 欄位A3
				row = sheet.createRow(2);
				cell = row.createCell(0);	
				cell.setCellValue("季度");
				cell.setCellStyle(cellStyle);
				cellIndex = 1;
				for(int x=0; x<tableRowHeaders2.length; x++) {
					cell = row.createCell(cellIndex + x);
					cell.setCellValue(tableRowHeaders2[x]);
					cell.setCellStyle(cellStyle);
				}
				
				/// 欄位A4
				cellIndex = 0;
				rowIndex = 3;
				for(int x=0; x<modelList.size(); x++) {
					row = sheet.createRow(rowIndex);
					cell = row.createCell(cellIndex);
					cell.setCellValue(modelList.get(x).getDisplayName());
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue("非專案(隨機)");
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(modelList.get(x).getNoprojectQuantityOp().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(modelList.get(x).getNoprojectAmountOp().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					switch(x) {
					case 0:
						if(opInfosQ1.size() > 0) {
							cell = row.createCell(cellIndex);
							cell.setCellValue(opInfosQ1.get(0).get("name").toString());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(Long.valueOf(opInfosQ1.get(0).get("amount").toString()).doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(opInfosQ1.get(0).get("reason").toString());
							cell.setCellStyle(cellFormatStyle);
							
							opInfosQ1.remove(0);
						}
						else {
							for(int i=0; i<3; i++) {
								cell = row.createCell(cellIndex+i);
								cell.setCellValue("");
								cell.setCellStyle(cellStyle);
							}
						}
						break;
					case 1:
						if(opInfosQ2.size() > 0) {
							cell = row.createCell(cellIndex);
							cell.setCellValue(opInfosQ2.get(0).get("name").toString());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(Long.valueOf(opInfosQ2.get(0).get("amount").toString()).doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(opInfosQ2.get(0).get("reason").toString());
							cell.setCellStyle(cellFormatStyle);
							
							opInfosQ2.remove(0);
						}
						else {
							for(int i=0; i<3; i++) {
								cell = row.createCell(cellIndex+i);
								cell.setCellValue("");
								cell.setCellStyle(cellStyle);
							}
						}
						break;
					case 2:
						if(opInfosQ3.size() > 0) {
							cell = row.createCell(cellIndex);
							cell.setCellValue(opInfosQ3.get(0).get("name").toString());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(Long.valueOf(opInfosQ3.get(0).get("amount").toString()).doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(opInfosQ3.get(0).get("reason").toString());
							cell.setCellStyle(cellFormatStyle);
							
							opInfosQ3.remove(0);
						}
						else {
							for(int i=0; i<3; i++) {
								cell = row.createCell(cellIndex+i);
								cell.setCellValue("");
								cell.setCellStyle(cellStyle);
							}
						}
						break;
					case 3:
						if(opInfosQ4.size() > 0) {
							cell = row.createCell(cellIndex);
							cell.setCellValue(opInfosQ4.get(0).get("name").toString());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(Long.valueOf(opInfosQ4.get(0).get("amount").toString()).doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(opInfosQ4.get(0).get("reason").toString());
							cell.setCellStyle(cellFormatStyle);
							
							opInfosQ4.remove(0);
						}
						else {
							for(int i=0; i<3; i++) {
								cell = row.createCell(cellIndex+i);
								cell.setCellValue("");
								cell.setCellStyle(cellStyle);
							}
						}
						break;
					}
					
					rowIndex++;
					cellIndex = 0;
					row = sheet.createRow(rowIndex);
					cell = row.createCell(cellIndex);
					cell.setCellValue(modelList.get(x).getDisplayName());
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue("專案(隨機)");
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(modelList.get(x).getProjectQuantityOp().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(modelList.get(x).getProjectAmountOp().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					switch(x) {
					case 0:
						if(opInfosQ1.size() > 0) {
							cell = row.createCell(cellIndex);
							cell.setCellValue(opInfosQ1.get(0).get("name").toString());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(Long.valueOf(opInfosQ1.get(0).get("amount").toString()).doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(opInfosQ1.get(0).get("reason").toString());
							cell.setCellStyle(cellFormatStyle);
							
							opInfosQ1.remove(0);
						}
						else {
							for(int i=0; i<3; i++) {
								cell = row.createCell(cellIndex+i);
								cell.setCellValue("");
								cell.setCellStyle(cellStyle);
							}
						}
						break;
					case 1:
						if(opInfosQ2.size() > 0) {
							cell = row.createCell(cellIndex);
							cell.setCellValue(opInfosQ2.get(0).get("name").toString());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(Long.valueOf(opInfosQ2.get(0).get("amount").toString()).doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(opInfosQ2.get(0).get("reason").toString());
							cell.setCellStyle(cellFormatStyle);
							
							opInfosQ2.remove(0);
						}
						else {
							for(int i=0; i<3; i++) {
								cell = row.createCell(cellIndex+i);
								cell.setCellValue("");
								cell.setCellStyle(cellStyle);
							}
						}
						break;
					case 2:
						if(opInfosQ3.size() > 0) {
							cell = row.createCell(cellIndex);
							cell.setCellValue(opInfosQ3.get(0).get("name").toString());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(Long.valueOf(opInfosQ3.get(0).get("amount").toString()).doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(opInfosQ3.get(0).get("reason").toString());
							cell.setCellStyle(cellFormatStyle);
							
							opInfosQ3.remove(0);
						}
						else {
							for(int i=0; i<3; i++) {
								cell = row.createCell(cellIndex+i);
								cell.setCellValue("");
								cell.setCellStyle(cellStyle);
							}
						}
						break;
					case 3:
						if(opInfosQ4.size() > 0) {
							cell = row.createCell(cellIndex);
							cell.setCellValue(opInfosQ4.get(0).get("name").toString());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(Long.valueOf(opInfosQ4.get(0).get("amount").toString()).doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(opInfosQ4.get(0).get("reason").toString());
							cell.setCellStyle(cellFormatStyle);
							
							opInfosQ4.remove(0);
						}
						else {
							for(int i=0; i<3; i++) {
								cell = row.createCell(cellIndex+i);
								cell.setCellValue("");
								cell.setCellStyle(cellStyle);
							}
						}
						break;
					}
					rowIndex++;
					cellIndex = 0;
					row = sheet.createRow(rowIndex);
					cell = row.createCell(cellIndex);
					cell.setCellValue(modelList.get(x).getDisplayName());
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue("藥費(隨機)");
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(modelList.get(x).getMedQuantityOp().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(modelList.get(x).getMedAmountOp().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					switch(x) {
					case 0:
						if(opInfosQ1.size() > 0) {
							cell = row.createCell(cellIndex);
							cell.setCellValue(opInfosQ1.get(0).get("name").toString());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(Long.valueOf(opInfosQ1.get(0).get("amount").toString()).doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(opInfosQ1.get(0).get("reason").toString());
							cell.setCellStyle(cellFormatStyle);
							
							opInfosQ1.remove(0);
						}
						else {
							for(int i=0; i<3; i++) {
								cell = row.createCell(cellIndex+i);
								cell.setCellValue("");
								cell.setCellStyle(cellStyle);
							}
						}
						break;
					case 1:
						if(opInfosQ2.size() > 0) {
							cell = row.createCell(cellIndex);
							cell.setCellValue(opInfosQ2.get(0).get("name").toString());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(Long.valueOf(opInfosQ2.get(0).get("amount").toString()).doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(opInfosQ2.get(0).get("reason").toString());
							cell.setCellStyle(cellFormatStyle);
							
							opInfosQ2.remove(0);
						}
						else {
							for(int i=0; i<3; i++) {
								cell = row.createCell(cellIndex+i);
								cell.setCellValue("");
								cell.setCellStyle(cellStyle);
							}
						}
						break;
					case 2:
						if(opInfosQ3.size() > 0) {
							cell = row.createCell(cellIndex);
							cell.setCellValue(opInfosQ3.get(0).get("name").toString());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(Long.valueOf(opInfosQ3.get(0).get("amount").toString()).doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(opInfosQ3.get(0).get("reason").toString());
							cell.setCellStyle(cellFormatStyle);
							
							opInfosQ3.remove(0);
						}
						else {
							for(int i=0; i<3; i++) {
								cell = row.createCell(cellIndex+i);
								cell.setCellValue("");
								cell.setCellStyle(cellStyle);
							}
						}
						break;
					case 3:
						if(opInfosQ4.size() > 0) {
							cell = row.createCell(cellIndex);
							cell.setCellValue(opInfosQ4.get(0).get("name").toString());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(Long.valueOf(opInfosQ4.get(0).get("amount").toString()).doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(opInfosQ4.get(0).get("reason").toString());
							cell.setCellStyle(cellFormatStyle);
							
							opInfosQ4.remove(0);
						}
						else {
							for(int i=0; i<3; i++) {
								cell = row.createCell(cellIndex+i);
								cell.setCellValue("");
								cell.setCellStyle(cellStyle);
							}
						}
						break;
					}
					
					switch(x) {
					case 0:
						if(opInfosQ1.size() > 0) {
							for(int i=0; i<opInfosQ1.size(); i++) {
								rowIndex++;
								cellIndex = 0;
								row = sheet.createRow(rowIndex);
								cell = row.createCell(cellIndex);
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellStyle(cellFormatStyle);
								cellIndex = 4;
								cell = row.createCell(cellIndex);
								cell.setCellValue(opInfosQ1.get(i).get("name").toString());
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellValue(Long.valueOf(opInfosQ1.get(i).get("amount").toString()).doubleValue());
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellValue(opInfosQ1.get(i).get("reason").toString());
								cell.setCellStyle(cellFormatStyle);
							}
						}
						break;
					case 1:
						if(opInfosQ2.size() > 0) {
							for(int i=0; i<opInfosQ2.size(); i++) {
								rowIndex++;
								cellIndex = 0;
								row = sheet.createRow(rowIndex);
								cell = row.createCell(cellIndex);
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellStyle(cellFormatStyle);
								cellIndex = 4;
								cell = row.createCell(cellIndex);
								cell.setCellValue(opInfosQ2.get(i).get("name").toString());
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellValue(Long.valueOf(opInfosQ2.get(i).get("amount").toString()).doubleValue());
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellValue(opInfosQ2.get(i).get("reason").toString());
								cell.setCellStyle(cellFormatStyle);
							}
						}
						break;
					case 2:
						if(opInfosQ3.size() > 0) {
							for(int i=0; i<opInfosQ3.size(); i++) {
								rowIndex++;
								cellIndex = 0;
								row = sheet.createRow(rowIndex);
								cell = row.createCell(cellIndex);
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellStyle(cellFormatStyle);
								cellIndex = 4;
								cell = row.createCell(cellIndex);
								cell.setCellValue(opInfosQ3.get(i).get("name").toString());
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellValue(Long.valueOf(opInfosQ3.get(i).get("amount").toString()).doubleValue());
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellValue(opInfosQ3.get(i).get("reason").toString());
								cell.setCellStyle(cellFormatStyle);
							}
						}
						
						break;
					case 3:
						if(opInfosQ4.size() > 0) {
							for(int i=0; i<opInfosQ4.size(); i++) {
								rowIndex++;
								cellIndex = 0;
								row = sheet.createRow(rowIndex);
								cell = row.createCell(cellIndex);
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellStyle(cellFormatStyle);
								cellIndex = 4;
								cell = row.createCell(cellIndex);
								cell.setCellValue(opInfosQ4.get(i).get("name").toString());
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellValue(Long.valueOf(opInfosQ4.get(i).get("amount").toString()).doubleValue());
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellValue(opInfosQ4.get(i).get("reason").toString());
								cell.setCellStyle(cellFormatStyle);
							}
						}
						break;
					}
					
					rowIndex += 2;
					cellIndex = 0;
					
				}
				
				/// 最後設定autosize
				for (int i = 0; i < tableRowHeaders2.length + 2; i++) {
					sheet.autoSizeColumn(i);
					sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
				}
				
				/// 新建工作表
				sheet = workbook.createSheet("多季度核減資訊(住院)");
				tableRowHeaders = new String[] {"健保住院核減抽審總件數", "健保住院核減總核扣點數"};
				tableRowHeaders2 = new String[] {"類別","核扣件數","核扣點數","核刪醫令","核扣點數","理由"};
				cellIndex = 0;
				rowIndex = 0;
				
				/// 欄位A1
				row = sheet.createRow(0);
				cell = row.createCell(0);
				cell.setCellValue("多季度核減資訊");
				cell.setCellStyle(cellStyle);
				
				/// 欄位A2
				row = sheet.createRow(1);
				cell = row.createCell(0);
				cell.setCellStyle(cellStyle);
				cellIndex = 1;
				for(int x=0; x<tableRowHeaders2.length; x++) {
					cell = row.createCell(cellIndex + x);
					if(x==0) {
						cell.setCellValue(tableRowHeaders[0]);
					}
					else if(x==3) {
						cell.setCellValue(tableRowHeaders[1]);
					}
					cell.setCellStyle(cellTitleStyle);
				}
				/// merge欄位
				x1 = 1;
				x2 = 3;
				for(int x=0; x<tableRowHeaders.length; x++) {
					sheet.addMergedRegion(new CellRangeAddress(1, 1, x1, x2));
					x1 += 3;
					x2 += 3;
				}
				
				/// 欄位A3
				row = sheet.createRow(2);
				cell = row.createCell(0);	
				cell.setCellValue("季度");
				cell.setCellStyle(cellStyle);
				cellIndex = 1;
				for(int x=0; x<tableRowHeaders2.length; x++) {
					cell = row.createCell(cellIndex + x);
					cell.setCellValue(tableRowHeaders2[x]);
					cell.setCellStyle(cellStyle);
				}
				
				/// 欄位A4
				cellIndex = 0;
				rowIndex = 3;
				for(int x=0; x<modelList.size(); x++) {
					row = sheet.createRow(rowIndex);
					cell = row.createCell(cellIndex);
					cell.setCellValue(modelList.get(x).getDisplayName());
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue("非專案(隨機)");
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(modelList.get(x).getNoprojectQuantityIp().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(modelList.get(x).getNoprojectAmountIp().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					switch(x) {
					case 0:
						if(ipInfosQ1.size() > 0) {
							cell = row.createCell(cellIndex);
							cell.setCellValue(ipInfosQ1.get(0).get("name").toString());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(Long.valueOf(ipInfosQ1.get(0).get("amount").toString()).doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(ipInfosQ1.get(0).get("reason").toString());
							cell.setCellStyle(cellFormatStyle);
							
							ipInfosQ1.remove(0);
						}
						else {
							for(int i=0; i<3; i++) {
								cell = row.createCell(cellIndex+i);
								cell.setCellValue("");
								cell.setCellStyle(cellStyle);
							}
						}
						break;
					case 1:
						if(ipInfosQ2.size() > 0) {
							cell = row.createCell(cellIndex);
							cell.setCellValue(ipInfosQ2.get(0).get("name").toString());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(Long.valueOf(ipInfosQ2.get(0).get("amount").toString()).doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(ipInfosQ2.get(0).get("reason").toString());
							cell.setCellStyle(cellFormatStyle);
							
							ipInfosQ2.remove(0);
						}
						else {
							for(int i=0; i<3; i++) {
								cell = row.createCell(cellIndex+i);
								cell.setCellValue("");
								cell.setCellStyle(cellStyle);
							}
						}
						break;
					case 2:
						if(ipInfosQ3.size() > 0) {
							cell = row.createCell(cellIndex);
							cell.setCellValue(ipInfosQ3.get(0).get("name").toString());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(Long.valueOf(ipInfosQ3.get(0).get("amount").toString()).doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(ipInfosQ3.get(0).get("reason").toString());
							cell.setCellStyle(cellFormatStyle);
							
							ipInfosQ3.remove(0);
						}
						else {
							for(int i=0; i<3; i++) {
								cell = row.createCell(cellIndex+i);
								cell.setCellValue("");
								cell.setCellStyle(cellStyle);
							}
						}
						break;
					case 3:
						if(ipInfosQ4.size() > 0) {
							cell = row.createCell(cellIndex);
							cell.setCellValue(ipInfosQ4.get(0).get("name").toString());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(Long.valueOf(ipInfosQ4.get(0).get("amount").toString()).doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(ipInfosQ4.get(0).get("reason").toString());
							cell.setCellStyle(cellFormatStyle);
							
							ipInfosQ4.remove(0);
						}
						else {
							for(int i=0; i<3; i++) {
								cell = row.createCell(cellIndex+i);
								cell.setCellValue("");
								cell.setCellStyle(cellStyle);
							}
						}
						break;
					}
					
					rowIndex++;
					cellIndex = 0;
					row = sheet.createRow(rowIndex);
					cell = row.createCell(cellIndex);
					cell.setCellValue(modelList.get(x).getDisplayName());
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue("專案(隨機)");
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(modelList.get(x).getProjectQuantityIp().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(modelList.get(x).getProjectAmountIp().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					switch(x) {
					case 0:
						if(ipInfosQ1.size() > 0) {
							cell = row.createCell(cellIndex);
							cell.setCellValue(ipInfosQ1.get(0).get("name").toString());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(Long.valueOf(ipInfosQ1.get(0).get("amount").toString()).doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(ipInfosQ1.get(0).get("reason").toString());
							cell.setCellStyle(cellFormatStyle);
							
							ipInfosQ1.remove(0);
						}
						else {
							for(int i=0; i<3; i++) {
								cell = row.createCell(cellIndex+i);
								cell.setCellValue("");
								cell.setCellStyle(cellStyle);
							}
						}
						break;
					case 1:
						if(ipInfosQ2.size() > 0) {
							cell = row.createCell(cellIndex);
							cell.setCellValue(ipInfosQ2.get(0).get("name").toString());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(Long.valueOf(ipInfosQ2.get(0).get("amount").toString()).doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(ipInfosQ2.get(0).get("reason").toString());
							cell.setCellStyle(cellFormatStyle);
							
							ipInfosQ2.remove(0);
						}
						else {
							for(int i=0; i<3; i++) {
								cell = row.createCell(cellIndex+i);
								cell.setCellValue("");
								cell.setCellStyle(cellStyle);
							}
						}
						break;
					case 2:
						if(ipInfosQ3.size() > 0) {
							cell = row.createCell(cellIndex);
							cell.setCellValue(ipInfosQ3.get(0).get("name").toString());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(Long.valueOf(ipInfosQ3.get(0).get("amount").toString()).doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(ipInfosQ3.get(0).get("reason").toString());
							cell.setCellStyle(cellFormatStyle);
							
							ipInfosQ3.remove(0);
						}
						else {
							for(int i=0; i<3; i++) {
								cell = row.createCell(cellIndex+i);
								cell.setCellValue("");
								cell.setCellStyle(cellStyle);
							}
						}
						break;
					case 3:
						if(ipInfosQ4.size() > 0) {
							cell = row.createCell(cellIndex);
							cell.setCellValue(ipInfosQ4.get(0).get("name").toString());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(Long.valueOf(ipInfosQ4.get(0).get("amount").toString()).doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(ipInfosQ4.get(0).get("reason").toString());
							cell.setCellStyle(cellFormatStyle);
							
							ipInfosQ4.remove(0);
						}
						else {
							for(int i=0; i<3; i++) {
								cell = row.createCell(cellIndex+i);
								cell.setCellValue("");
								cell.setCellStyle(cellStyle);
							}
						}
						break;
					}
					rowIndex++;
					cellIndex = 0;
					row = sheet.createRow(rowIndex);
					cell = row.createCell(cellIndex);
					cell.setCellValue(modelList.get(x).getDisplayName());
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue("藥費(隨機)");
					cell.setCellStyle(cellStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(modelList.get(x).getMedQuantityIp().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					cell = row.createCell(cellIndex);
					cell.setCellValue(modelList.get(x).getMedAmountIp().doubleValue());
					cell.setCellStyle(cellFormatStyle);
					cellIndex++;
					switch(x) {
					case 0:
						if(ipInfosQ1.size() > 0) {
							cell = row.createCell(cellIndex);
							cell.setCellValue(ipInfosQ1.get(0).get("name").toString());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(Long.valueOf(ipInfosQ1.get(0).get("amount").toString()).doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(ipInfosQ1.get(0).get("reason").toString());
							cell.setCellStyle(cellFormatStyle);
							
							ipInfosQ1.remove(0);
						}
						else {
							for(int i=0; i<3; i++) {
								cell = row.createCell(cellIndex+i);
								cell.setCellValue("");
								cell.setCellStyle(cellStyle);
							}
						}
						break;
					case 1:
						if(ipInfosQ2.size() > 0) {
							cell = row.createCell(cellIndex);
							cell.setCellValue(ipInfosQ2.get(0).get("name").toString());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(Long.valueOf(ipInfosQ2.get(0).get("amount").toString()).doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(ipInfosQ2.get(0).get("reason").toString());
							cell.setCellStyle(cellFormatStyle);
							
							ipInfosQ2.remove(0);
						}
						else {
							for(int i=0; i<3; i++) {
								cell = row.createCell(cellIndex+i);
								cell.setCellValue("");
								cell.setCellStyle(cellStyle);
							}
						}
						break;
					case 2:
						if(ipInfosQ3.size() > 0) {
							cell = row.createCell(cellIndex);
							cell.setCellValue(ipInfosQ3.get(0).get("name").toString());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(Long.valueOf(ipInfosQ3.get(0).get("amount").toString()).doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(ipInfosQ3.get(0).get("reason").toString());
							cell.setCellStyle(cellFormatStyle);
							
							ipInfosQ3.remove(0);
						}
						else {
							for(int i=0; i<3; i++) {
								cell = row.createCell(cellIndex+i);
								cell.setCellValue("");
								cell.setCellStyle(cellStyle);
							}
						}
						break;
					case 3:
						if(ipInfosQ4.size() > 0) {
							cell = row.createCell(cellIndex);
							cell.setCellValue(ipInfosQ4.get(0).get("name").toString());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(Long.valueOf(ipInfosQ4.get(0).get("amount").toString()).doubleValue());
							cell.setCellStyle(cellFormatStyle);
							cellIndex++;
							cell = row.createCell(cellIndex);
							cell.setCellValue(ipInfosQ4.get(0).get("reason").toString());
							cell.setCellStyle(cellFormatStyle);
							
							ipInfosQ4.remove(0);
						}
						else {
							for(int i=0; i<3; i++) {
								cell = row.createCell(cellIndex+i);
								cell.setCellValue("");
								cell.setCellStyle(cellStyle);
							}
						}
						break;
					}
					
					switch(x) {
					case 0:
						if(ipInfosQ1.size() > 0) {
							for(int i=0; i<ipInfosQ1.size(); i++) {
								rowIndex++;
								cellIndex = 0;
								row = sheet.createRow(rowIndex);
								cell = row.createCell(cellIndex);
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellStyle(cellFormatStyle);
								cellIndex = 4;
								cell = row.createCell(cellIndex);
								cell.setCellValue(ipInfosQ1.get(i).get("name").toString());
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellValue(Long.valueOf(ipInfosQ1.get(i).get("amount").toString()).doubleValue());
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellValue(ipInfosQ1.get(i).get("reason").toString());
								cell.setCellStyle(cellFormatStyle);
							}
						}
						break;
					case 1:
						if(ipInfosQ2.size() > 0) {
							for(int i=0; i<ipInfosQ2.size(); i++) {
								rowIndex++;
								cellIndex = 0;
								row = sheet.createRow(rowIndex);
								cell = row.createCell(cellIndex);
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellStyle(cellFormatStyle);
								cellIndex = 4;
								cell = row.createCell(cellIndex);
								cell.setCellValue(ipInfosQ2.get(i).get("name").toString());
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellValue(Long.valueOf(ipInfosQ2.get(i).get("amount").toString()).doubleValue());
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellValue(ipInfosQ2.get(i).get("reason").toString());
								cell.setCellStyle(cellFormatStyle);
							}
						}
						break;
					case 2:
						if(ipInfosQ3.size() > 0) {
							for(int i=0; i<ipInfosQ3.size(); i++) {
								rowIndex++;
								cellIndex = 0;
								row = sheet.createRow(rowIndex);
								cell = row.createCell(cellIndex);
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellStyle(cellFormatStyle);
								cellIndex = 4;
								cell = row.createCell(cellIndex);
								cell.setCellValue(ipInfosQ3.get(i).get("name").toString());
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellValue(Long.valueOf(ipInfosQ3.get(i).get("amount").toString()).doubleValue());
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellValue(ipInfosQ3.get(i).get("reason").toString());
								cell.setCellStyle(cellFormatStyle);
							}
						}
						
						break;
					case 3:
						if(ipInfosQ4.size() > 0) {
							for(int i=0; i<ipInfosQ4.size(); i++) {
								rowIndex++;
								cellIndex = 0;
								row = sheet.createRow(rowIndex);
								cell = row.createCell(cellIndex);
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellStyle(cellFormatStyle);
								cellIndex = 4;
								cell = row.createCell(cellIndex);
								cell.setCellValue(ipInfosQ4.get(i).get("name").toString());
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellValue(Long.valueOf(ipInfosQ4.get(i).get("amount").toString()).doubleValue());
								cell.setCellStyle(cellFormatStyle);
								cellIndex++;
								cell = row.createCell(cellIndex);
								cell.setCellValue(ipInfosQ4.get(i).get("reason").toString());
								cell.setCellStyle(cellFormatStyle);
							}
						}
						break;
					}
					
					rowIndex += 2;
					cellIndex = 0;
					
				}
				
				/// 最後設定autosize
				for (int i = 0; i < tableRowHeaders2.length + 2; i++) {
					sheet.autoSizeColumn(i);
					sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
				}
				
				/// 新建工作表
				sheet = workbook.createSheet("多季度放大回推核減資訊(門急)");
				tableRowHeaders = new String[] {"健保門急診放大回推"};
				tableRowHeaders2 = new String[] {"季度","核刪醫令","放大回推金額","申復數量","申復金額","申復補付數量","申復補復金額"};
				cellIndex = 0;
				rowIndex = 0;
				///資料統計
				List<List<Map<String,Object>>> rollbackList = new ArrayList<List<Map<String,Object>>>();
				List<Map<String,Object>> rollbacksQ1 = new ArrayList<Map<String,Object>>();
				List<Map<String,Object>> rollbacksQ2 = new ArrayList<Map<String,Object>>();
				List<Map<String,Object>> rollbacksQ3 = new ArrayList<Map<String,Object>>();
				List<Map<String,Object>> rollbacksQ4 = new ArrayList<Map<String,Object>>();
				///門急診資料
				List<Map<String,Object>> opRollbackQ1 = new ArrayList<Map<String,Object>>();
				List<Map<String,Object>> opRollbackQ2 = new ArrayList<Map<String,Object>>();
				List<Map<String,Object>> opRollbackQ3 = new ArrayList<Map<String,Object>>();
				List<Map<String,Object>> opRollbackQ4 = new ArrayList<Map<String,Object>>();
				///住院資料
				List<Map<String,Object>> ipRollbackQ1 = new ArrayList<Map<String,Object>>();
				List<Map<String,Object>> ipRollbackQ2 = new ArrayList<Map<String,Object>>();
				List<Map<String,Object>> ipRollbackQ3 = new ArrayList<Map<String,Object>>();
				List<Map<String,Object>> ipRollbackQ4 = new ArrayList<Map<String,Object>>();
				
				///將所有季度資料裝載array
				for(int i=0; i<modelList.size(); i++) {
					rollbackList.add(modelList.get(i).getRollbackList());
				}
				for(int i=0; i<rollbackList.size(); i++) {
					switch(i) {
					case 0:
						if(rollbackList.get(0) != null)
							rollbacksQ1.addAll(rollbackList.get(0));
						break;
					case 1:
						if(rollbackList.get(1) != null)
							rollbacksQ2.addAll(rollbackList.get(1));
						break;
					case 2:
						if(rollbackList.get(2) != null)
							rollbacksQ3.addAll(rollbackList.get(2));
						break;
					case 3:
						if(rollbackList.get(3) != null)
							rollbacksQ4.addAll(rollbackList.get(3));
						break;
					}
				}
				
				for(int i=0; i<rollbacksQ1.size(); i++) {
					String dateformat = rollbacksQ1.get(i).get("dataFormat").toString();
					if(dateformat.equals("10")) {
						opRollbackQ1.add(rollbacksQ1.get(i));
					}
					else {
						ipRollbackQ1.add(rollbacksQ1.get(i));
					}
				}
				for(int i=0; i<rollbacksQ2.size(); i++) {
					String dateformat = rollbacksQ2.get(i).get("dataFormat").toString();
					if(dateformat.equals("10")) {
						opRollbackQ1.add(rollbacksQ2.get(i));
					}
					else {
						ipRollbackQ2.add(rollbacksQ2.get(i));
					}
				}
				for(int i=0; i<rollbacksQ3.size(); i++) {
					String dateformat = rollbacksQ3.get(i).get("dataFormat").toString();
					if(dateformat.equals("10")) {
						opRollbackQ3.add(rollbacksQ3.get(i));
					}
					else {
						ipRollbackQ3.add(rollbacksQ3.get(i));
					}
				}
				for(int i=0; i<rollbacksQ4.size(); i++) {
					String dateformat = rollbacksQ4.get(i).get("dataFormat").toString();
					if(dateformat.equals("10")) {
						opRollbackQ4.add(rollbacksQ4.get(i));
					}
					else {
						ipRollbackQ4.add(rollbacksQ4.get(i));
					}
				}
				
				/// 欄位A1
				row = sheet.createRow(0);
				for(int i=0; i<tableRowHeaders2.length; i++) {
					cell = row.createCell(i);
					if(i==0) {
						cell.setCellValue("健保門急診放大回推");
					}
					cell.setCellStyle(cellTitleStyle);
				}
				/// merge
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
				
				/// 欄位A2
				row = sheet.createRow(1);
				for(int i=0; i<tableRowHeaders2.length; i++) {
					cell = row.createCell(i);
					cell.setCellValue(tableRowHeaders2[i]);
					cell.setCellStyle(cellTitleStyle);
				}
				
				/// 欄位A3
				rowIndex = 2;
				cellIndex = 0;
				if(opRollbackQ1.size() > 0) {
					for(int i=0; i<opRollbackQ1.size(); i++) {
						row = sheet.createRow(rowIndex);
						cell = row.createCell(cellIndex);
						cell.setCellValue(modelList.get(0).getDisplayName());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(opRollbackQ1.get(i).get("name").toString());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(opRollbackQ1.get(i).get("amount").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(opRollbackQ1.get(i).get("afrQuantity").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(opRollbackQ1.get(i).get("afrAmount").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(opRollbackQ1.get(i).get("afrPayQuantity").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(opRollbackQ1.get(i).get("afrPayAmount").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						
						cellIndex = 0;
						rowIndex++;
					}
				}
				if(opRollbackQ2.size() > 0) {
					for(int i=0; i<opRollbackQ2.size(); i++) {
						row = sheet.createRow(rowIndex);
						cell = row.createCell(cellIndex);
						cell.setCellValue(modelList.get(0).getDisplayName());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(opRollbackQ2.get(i).get("name").toString());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(opRollbackQ2.get(i).get("amount").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(opRollbackQ2.get(i).get("afrQuantity").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(opRollbackQ2.get(i).get("afrAmount").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(opRollbackQ2.get(i).get("afrPayQuantity").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(opRollbackQ2.get(i).get("afrPayAmount").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						
						cellIndex = 0;
						rowIndex++;
					}
				}
				if(opRollbackQ3.size() > 0) {
					for(int i=0; i<opRollbackQ3.size(); i++) {
						row = sheet.createRow(rowIndex);
						cell = row.createCell(cellIndex);
						cell.setCellValue(modelList.get(0).getDisplayName());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(opRollbackQ3.get(i).get("name").toString());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(opRollbackQ3.get(i).get("amount").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(opRollbackQ3.get(i).get("afrQuantity").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(opRollbackQ3.get(i).get("afrAmount").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(opRollbackQ3.get(i).get("afrPayQuantity").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(opRollbackQ3.get(i).get("afrPayAmount").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						
						cellIndex = 0;
						rowIndex++;
					}
				}
				if(opRollbackQ4.size() > 0) {
					for(int i=0; i<opRollbackQ4.size(); i++) {
						row = sheet.createRow(rowIndex);
						cell = row.createCell(cellIndex);
						cell.setCellValue(modelList.get(0).getDisplayName());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(opRollbackQ4.get(i).get("name").toString());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(opRollbackQ4.get(i).get("amount").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(opRollbackQ4.get(i).get("afrQuantity").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(opRollbackQ4.get(i).get("afrAmount").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(opRollbackQ4.get(i).get("afrPayQuantity").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(opRollbackQ4.get(i).get("afrPayAmount").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						
						cellIndex = 0;
						rowIndex++;
					}
				}
				
				/// 最後設定autosize
				for (int i = 0; i < tableRowHeaders2.length + 2; i++) {
					sheet.autoSizeColumn(i);
					sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
				}
				
				/// 新建工作表
				sheet = workbook.createSheet("多季度放大回推核減資訊(住院)");
				tableRowHeaders = new String[] {"健保住院放大回推"};
				tableRowHeaders2 = new String[] {"季度","核刪醫令","放大回推金額","申復數量","申復金額","申復補付數量","申復補復金額"};
				cellIndex = 0;
				rowIndex = 0;
				
				/// 欄位A1
				row = sheet.createRow(0);
				for(int i=0; i<tableRowHeaders2.length; i++) {
					cell = row.createCell(i);
					if(i==0) {
						cell.setCellValue("健保住院放大回推");
					}
					cell.setCellStyle(cellTitleStyle);
				}
				/// merge
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
				
				/// 欄位A2
				row = sheet.createRow(1);
				for(int i=0; i<tableRowHeaders2.length; i++) {
					cell = row.createCell(i);
					cell.setCellValue(tableRowHeaders2[i]);
					cell.setCellStyle(cellTitleStyle);
				}
				
				/// 欄位A3
				rowIndex = 2;
				cellIndex = 0;
				if(ipRollbackQ1.size() > 0) {
					for(int i=0; i<ipRollbackQ1.size(); i++) {
						row = sheet.createRow(rowIndex);
						cell = row.createCell(cellIndex);
						cell.setCellValue(modelList.get(0).getDisplayName());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(ipRollbackQ1.get(i).get("name").toString());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(ipRollbackQ1.get(i).get("amount").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(ipRollbackQ1.get(i).get("afrQuantity").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(ipRollbackQ1.get(i).get("afrAmount").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(ipRollbackQ1.get(i).get("afrPayQuantity").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(ipRollbackQ1.get(i).get("afrPayAmount").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						
						cellIndex = 0;
						rowIndex++;
					}
				}
				if(ipRollbackQ2.size() > 0) {
					for(int i=0; i<ipRollbackQ2.size(); i++) {
						row = sheet.createRow(rowIndex);
						cell = row.createCell(cellIndex);
						cell.setCellValue(modelList.get(0).getDisplayName());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(ipRollbackQ2.get(i).get("name").toString());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(ipRollbackQ2.get(i).get("amount").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(ipRollbackQ2.get(i).get("afrQuantity").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(ipRollbackQ2.get(i).get("afrAmount").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(ipRollbackQ2.get(i).get("afrPayQuantity").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(ipRollbackQ2.get(i).get("afrPayAmount").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						
						cellIndex = 0;
						rowIndex++;
					}
				}
				if(ipRollbackQ3.size() > 0) {
					for(int i=0; i<ipRollbackQ3.size(); i++) {
						row = sheet.createRow(rowIndex);
						cell = row.createCell(cellIndex);
						cell.setCellValue(modelList.get(0).getDisplayName());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(ipRollbackQ3.get(i).get("name").toString());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(ipRollbackQ3.get(i).get("amount").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(ipRollbackQ3.get(i).get("afrQuantity").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(ipRollbackQ3.get(i).get("afrAmount").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(ipRollbackQ3.get(i).get("afrPayQuantity").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(ipRollbackQ3.get(i).get("afrPayAmount").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						
						cellIndex = 0;
						rowIndex++;
					}
				}
				if(ipRollbackQ4.size() > 0) {
					for(int i=0; i<ipRollbackQ4.size(); i++) {
						row = sheet.createRow(rowIndex);
						cell = row.createCell(cellIndex);
						cell.setCellValue(modelList.get(0).getDisplayName());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(ipRollbackQ4.get(i).get("name").toString());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(ipRollbackQ4.get(i).get("amount").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(ipRollbackQ4.get(i).get("afrQuantity").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(ipRollbackQ4.get(i).get("afrAmount").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(ipRollbackQ4.get(i).get("afrPayQuantity").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(ipRollbackQ4.get(i).get("afrPayAmount").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						
						cellIndex = 0;
						rowIndex++;
					}
				}
				
				/// 最後設定autosize
				for (int i = 0; i < tableRowHeaders2.length + 2; i++) {
					sheet.autoSizeColumn(i);
					sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
				}
				
				/// 新建工作表
				sheet = workbook.createSheet("多季度爭議核減資訊(門急)");
				tableRowHeaders = new String[] {"健保門急診爭議"};
				tableRowHeaders2 = new String[] {"季度","核刪醫令","爭議數量","爭議金額","爭議不補付代碼"};
				cellIndex = 0;
				rowIndex = 0;
				///資料統計
				List<List<Map<String,Object>>> disputeList = new ArrayList<List<Map<String,Object>>>();
				List<Map<String,Object>> disputesQ1 = new ArrayList<Map<String,Object>>();
				List<Map<String,Object>> disputesQ2 = new ArrayList<Map<String,Object>>();
				List<Map<String,Object>> disputesQ3 = new ArrayList<Map<String,Object>>();
				List<Map<String,Object>> disputesQ4 = new ArrayList<Map<String,Object>>();
				///門急診資料
				List<Map<String,Object>> opdisputeQ1 = new ArrayList<Map<String,Object>>();
				List<Map<String,Object>> opdisputeQ2 = new ArrayList<Map<String,Object>>();
				List<Map<String,Object>> opdisputeQ3 = new ArrayList<Map<String,Object>>();
				List<Map<String,Object>> opdisputeQ4 = new ArrayList<Map<String,Object>>();
				///住院資料
				List<Map<String,Object>> ipdisputeQ1 = new ArrayList<Map<String,Object>>();
				List<Map<String,Object>> ipdisputeQ2 = new ArrayList<Map<String,Object>>();
				List<Map<String,Object>> ipdisputeQ3 = new ArrayList<Map<String,Object>>();
				List<Map<String,Object>> ipdisputeQ4 = new ArrayList<Map<String,Object>>();
				
				///將所有季度資料裝載array
				for(int i=0; i<modelList.size(); i++) {
					disputeList.add(modelList.get(i).getDisputeList());
				}
				for(int i=0; i<disputeList.size(); i++) {
					switch(i) {
					case 0:
						if(disputeList.get(0) != null)
							disputesQ1.addAll(disputeList.get(0));
						break;
					case 1:
						if(disputeList.get(1) != null)
							disputesQ2.addAll(disputeList.get(1));
						break;
					case 2:
						if(disputeList.get(2) != null)
							disputesQ3.addAll(disputeList.get(2));
						break;
					case 3:
						if(disputeList.get(3) != null)
							disputesQ4.addAll(disputeList.get(3));
						break;
					}
				}
				
				for(int i=0; i<disputesQ1.size(); i++) {
					String dateformat = disputesQ1.get(i).get("dataFormat").toString();
					if(dateformat.equals("10")) {
						opdisputeQ1.add(disputesQ1.get(i));
					}
					else {
						ipdisputeQ1.add(disputesQ1.get(i));
					}
				}
				for(int i=0; i<disputesQ2.size(); i++) {
					String dateformat = disputesQ2.get(i).get("dataFormat").toString();
					if(dateformat.equals("10")) {
						opdisputeQ1.add(disputesQ2.get(i));
					}
					else {
						ipdisputeQ2.add(disputesQ2.get(i));
					}
				}
				for(int i=0; i<disputesQ3.size(); i++) {
					String dateformat = disputesQ3.get(i).get("dataFormat").toString();
					if(dateformat.equals("10")) {
						opdisputeQ3.add(disputesQ3.get(i));
					}
					else {
						ipdisputeQ3.add(disputesQ3.get(i));
					}
				}
				for(int i=0; i<disputesQ4.size(); i++) {
					String dateformat = disputesQ4.get(i).get("dataFormat").toString();
					if(dateformat.equals("10")) {
						opdisputeQ4.add(disputesQ4.get(i));
					}
					else {
						ipdisputeQ4.add(disputesQ4.get(i));
					}
				}
				
				/// 欄位A1
				row = sheet.createRow(0);
				for(int i=0; i<tableRowHeaders2.length; i++) {
					cell = row.createCell(i);
					if(i==0) {
						cell.setCellValue("健保門急診爭議");
					}
					cell.setCellStyle(cellTitleStyle);
				}
				/// merge
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, tableRowHeaders2.length - 1));
				
				/// 欄位A2
				row = sheet.createRow(1);
				for(int i=0; i<tableRowHeaders2.length; i++) {
					cell = row.createCell(i);
					cell.setCellValue(tableRowHeaders2[i]);
					cell.setCellStyle(cellTitleStyle);
				}
				
				/// 欄位A3
				rowIndex = 2;
				cellIndex = 0;
				if(opdisputeQ1.size() > 0) {
					for(int i=0; i<opdisputeQ1.size(); i++) {
						row = sheet.createRow(rowIndex);
						cell = row.createCell(cellIndex);
						cell.setCellValue(modelList.get(0).getDisplayName());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(opdisputeQ1.get(i).get("name").toString());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(opdisputeQ1.get(i).get("disputeQuantity").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(opdisputeQ1.get(i).get("disputeAmount").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(opdisputeQ1.get(i).get("disputeNoPayCode").toString());
						cell.setCellStyle(cellFormatStyle);
						
						cellIndex = 0;
						rowIndex++;
					}
				}
				if(opdisputeQ2.size() > 0) {
					for(int i=0; i<opdisputeQ2.size(); i++) {
						row = sheet.createRow(rowIndex);
						cell = row.createCell(cellIndex);
						cell.setCellValue(modelList.get(0).getDisplayName());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(opdisputeQ2.get(i).get("name").toString());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(opdisputeQ2.get(i).get("amount").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(opdisputeQ2.get(i).get("disputeQuantity").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(opdisputeQ2.get(i).get("disputeAmount").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(opdisputeQ2.get(i).get("disputeNoPayCode").toString());
						cell.setCellStyle(cellFormatStyle);
						
						cellIndex = 0;
						rowIndex++;
					}
				}
				if(opdisputeQ3.size() > 0) {
					for(int i=0; i<opdisputeQ3.size(); i++) {
						row = sheet.createRow(rowIndex);
						cell = row.createCell(cellIndex);
						cell.setCellValue(modelList.get(0).getDisplayName());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(opdisputeQ3.get(i).get("name").toString());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(opdisputeQ3.get(i).get("amount").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(opdisputeQ3.get(i).get("disputeQuantity").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(opdisputeQ3.get(i).get("disputeAmount").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(opdisputeQ3.get(i).get("disputeNoPayCode").toString());
						cell.setCellStyle(cellFormatStyle);
						
						cellIndex = 0;
						rowIndex++;
					}
				}
				if(opdisputeQ4.size() > 0) {
					for(int i=0; i<opdisputeQ4.size(); i++) {
						row = sheet.createRow(rowIndex);
						cell = row.createCell(cellIndex);
						cell.setCellValue(modelList.get(0).getDisplayName());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(opdisputeQ4.get(i).get("name").toString());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(opdisputeQ4.get(i).get("amount").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(opdisputeQ4.get(i).get("disputeQuantity").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(opdisputeQ4.get(i).get("disputeAmount").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(opdisputeQ4.get(i).get("disputeNoPayCode").toString());
						cell.setCellStyle(cellFormatStyle);
						
						cellIndex = 0;
						rowIndex++;
					}
				}
				
				/// 最後設定autosize
				for (int i = 0; i < tableRowHeaders2.length + 2; i++) {
					sheet.autoSizeColumn(i);
					sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
				}
				
				/// 新建工作表
				sheet = workbook.createSheet("多季度爭議核減資訊(住院)");
				tableRowHeaders = new String[] {"健保住院爭議"};
				tableRowHeaders2 = new String[] {"季度","核刪醫令","爭議數量","爭議金額","爭議不補付代碼"};
				cellIndex = 0;
				rowIndex = 0;
				
				/// 欄位A1
				row = sheet.createRow(0);
				for(int i=0; i<tableRowHeaders2.length; i++) {
					cell = row.createCell(i);
					if(i==0) {
						cell.setCellValue("健保住院爭議");
					}
					cell.setCellStyle(cellTitleStyle);
				}
				/// merge
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, tableRowHeaders2.length - 1));
				
				/// 欄位A2
				row = sheet.createRow(1);
				for(int i=0; i<tableRowHeaders2.length; i++) {
					cell = row.createCell(i);
					cell.setCellValue(tableRowHeaders2[i]);
					cell.setCellStyle(cellTitleStyle);
				}
				
				/// 欄位A3
				rowIndex = 2;
				cellIndex = 0;
				if(ipdisputeQ1.size() > 0) {
					for(int i=0; i<ipdisputeQ1.size(); i++) {
						row = sheet.createRow(rowIndex);
						cell = row.createCell(cellIndex);
						cell.setCellValue(modelList.get(0).getDisplayName());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(ipdisputeQ1.get(i).get("name").toString());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(ipdisputeQ1.get(i).get("disputeQuantity").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(ipdisputeQ1.get(i).get("disputeAmount").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(ipdisputeQ1.get(i).get("disputeNoPayCode").toString());
						cell.setCellStyle(cellFormatStyle);
						
						cellIndex = 0;
						rowIndex++;
					}
				}
				if(ipdisputeQ2.size() > 0) {
					for(int i=0; i<ipdisputeQ2.size(); i++) {
						row = sheet.createRow(rowIndex);
						cell = row.createCell(cellIndex);
						cell.setCellValue(modelList.get(0).getDisplayName());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(ipdisputeQ2.get(i).get("name").toString());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(ipdisputeQ2.get(i).get("amount").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(ipdisputeQ2.get(i).get("disputeQuantity").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(ipdisputeQ2.get(i).get("disputeAmount").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(ipdisputeQ2.get(i).get("disputeNoPayCode").toString());
						cell.setCellStyle(cellFormatStyle);
						
						cellIndex = 0;
						rowIndex++;
					}
				}
				if(ipdisputeQ3.size() > 0) {
					for(int i=0; i<ipdisputeQ3.size(); i++) {
						row = sheet.createRow(rowIndex);
						cell = row.createCell(cellIndex);
						cell.setCellValue(modelList.get(0).getDisplayName());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(ipdisputeQ3.get(i).get("name").toString());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(ipdisputeQ3.get(i).get("amount").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(ipdisputeQ3.get(i).get("disputeQuantity").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(ipdisputeQ3.get(i).get("disputeAmount").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(ipdisputeQ3.get(i).get("disputeNoPayCode").toString());
						cell.setCellStyle(cellFormatStyle);
						
						cellIndex = 0;
						rowIndex++;
					}
				}
				if(ipdisputeQ4.size() > 0) {
					for(int i=0; i<ipdisputeQ4.size(); i++) {
						row = sheet.createRow(rowIndex);
						cell = row.createCell(cellIndex);
						cell.setCellValue(modelList.get(0).getDisplayName());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(ipdisputeQ4.get(i).get("name").toString());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(ipdisputeQ4.get(i).get("amount").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(ipdisputeQ4.get(i).get("disputeQuantity").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(Long.valueOf(ipdisputeQ4.get(i).get("disputeAmount").toString()).doubleValue());
						cell.setCellStyle(cellFormatStyle);
						cellIndex++;
						cell = row.createCell(cellIndex);
						cell.setCellValue(ipdisputeQ4.get(i).get("disputeNoPayCode").toString());
						cell.setCellStyle(cellFormatStyle);
						
						cellIndex = 0;
						rowIndex++;
					}
				}
				
				/// 最後設定autosize
				for (int i = 0; i < tableRowHeaders2.length + 2; i++) {
					sheet.autoSizeColumn(i);
					sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
				}
				
			}
		}
		else {
			HSSFSheet sheet = workbook.createSheet("工作表");
		}
		
		String fileNameStr = "核刪資訊";
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
//		response.setContentType("application/vnd.ms-excel;charset=utf8");

		/// 最後由outputstream輸出
		OutputStream out = response.getOutputStream();

		workbook.write(out);
		out.flush();
		out.close();
		workbook.close();
	}
	

	//四捨五入取到整數
	public String convertToInteger(Float num) {
		int scale = 0;//設定位數
		//int roundingMode = 4;//表示四捨五入,可以選擇其他舍值方式,例如去尾,等等.
		BigDecimal bd = new BigDecimal((double)num);
		bd = bd.setScale(scale,BigDecimal.ROUND_HALF_UP);
		num = bd.floatValue();
		
		return num.toString().replaceAll("\\.0", "");
	}
	
	//千分位
	public String addThousandths(Long num) {
		DecimalFormat df=new DecimalFormat("#,###");
		return df.format(num);
	}
}
