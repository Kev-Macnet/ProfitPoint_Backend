package tw.com.leadtek.nhiwidget.service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

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

}
