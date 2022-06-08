package tw.com.leadtek.nhiwidget.payload.report;


import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
 
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;


public class WorkbookUtil {
    public List<HSSFSheet> sheets = new ArrayList<>();
    public HSSFWorkbook hssfWorkbook;
    public volatile int sheetIndex = 0;
    public String fileName = "Excel預設名稱.csv";
 
 
    public WorkbookUtil(String fileName) {
        this.fileName = fileName + ".csv";
    }
 
    public WorkbookUtil() {
    }
 
    public String getFileName() {
        return fileName;
    }
 
    public WorkbookUtil setFileName(String fileName) {
        if (Objects.nonNull(fileName)){
            this.fileName = fileName + ".csv";
        }
        return this;
    }
 
 
    public static void main(String[] args) {
 
        // 使用方法
        new WorkbookUtil()
                // 檔名稱
                .setFileName(null)
                // TODO 初始化一個excel
                .initWorkbook()
                // TODO 初始化一個sheet頁,可自定義邏輯，(HSSFWorkbook,String) ->{ HSSFSheet}
                .initSheet(null,null)
                // TODO 初始化sheet頁表頭 ，可自定義邏輯，(HSSFSheet,HeadDetails) ->{ HSSFSheet} 下同
                .initSheetHead(null,null,null,null)
                // 填充sheet頁資料
                .setSheetData(null,null,null)
                // sheet頁資料指定列合併行
                .setDataRowMerged(null,null,null)
                // sheet頁資料指定相鄰列合併,可以傳入多值，(單行合併，值相同情況)
                .setDataCellMerged(null,null,null)
                // 當前sheet當前行插入資料  data ：Map<Integer, String> (列索引,資料)
                .setSheetRow(null,null)
                // 當前sheet當前行強制合併    (firstCol,lastCol) 合併索引[必填]
                .setRowMerged(null,null)
                // 當前sheet指定行強制合併
                .setCellRangeAddress(null,null)
                // 初始化表尾
                .initSheetFoot(null,null)
                // TODO 第二個sheet頁..
                .initSheet(null,null)
                .initSheetHead(null,null,null,null)
                .setSheetData(null,null,null)
                .initSheetFoot(null,null)
                .setDataRowAndCellMerged(null,null,null)
                //....
                // .builderByte()  輸出位元組陣列
                // 直接寫入報文
                .builderResponseEntity();
 
    }
 
 
 
    /**
     * <per>
     * <p>行列合併，一般根據特定需求編碼，這裡沒有寫預設的邏輯</p>
     * <per/>
     * @param merged 傳遞的引數，Map<合併行的列索引,合併列的列索引>
     * @param list
     * @param sheetBiFunction
     * @return com.hhwy.pwps.util.excel.WorkbookUtil
     * @throws
     * @Description : TODO
     **/
    public WorkbookUtil setDataRowAndCellMerged(Map<int[], int[]> merged,List<?> list, BiFunction<HSSFSheet, Map<int[], int[]>, HSSFSheet> sheetBiFunction){
 
        HSSFSheet sheet = this.sheets.get(sheetIndex);
        if (Objects.nonNull(merged)  && Objects.nonNull(list) && Objects.isNull(sheetBiFunction)) {
               // 預設行列同時合併邏輯
        }
        sheets.add(sheetIndex, Objects.isNull(sheetBiFunction) ? sheet : sheetBiFunction.apply(sheet, merged));
        return this;
    }
 
 
    /**
     * <per>
     * <p>資料指定列索引合併</p>
     * <per/>
     *
     * @param merged
     * @param list
     * @param sheetBiFunction
     * @return com.hhwy.pwps.managepointins.service.impl.WorkbookUtil
     * @throws
     * @Description : TODO
     **/
    public WorkbookUtil setDataCellMerged(Map<Integer, Integer> merged, List<?> list, BiFunction<HSSFSheet, Map<Integer, Integer>, HSSFSheet> sheetBiFunction) {
        HSSFSheet sheet = this.sheets.get(sheetIndex);
        List<CellRangeAddress> cellRangeAddresses = new ArrayList<>();
        if (Objects.nonNull(merged) && Objects.isNull(sheetBiFunction)) {
            int numberOfRows = sheet.getPhysicalNumberOfRows() - list.size();
            List<Integer> cellIndex = merged.keySet().stream().collect(Collectors.toList());
            for (int j = 0; j < cellIndex.size(); j++) {
                int cellIndex_ = cellIndex.get(j);
                for (int i = numberOfRows; i < sheet.getPhysicalNumberOfRows(); i++) {
                    HSSFRow sheetRow = sheet.getRow(i);
                    String cell1 = sheetRow.getCell(cellIndex_).toString();
                    String cell2 = sheetRow.getCell(merged.get(cellIndex_)).toString();
                    if (cell1.equals(cell2)) {
                        sheet.addMergedRegionUnsafe(new CellRangeAddress(i, i, cellIndex_, merged.get(cellIndex_)));
                        // cellRangeAddresses.add(new CellRangeAddress(i, i, cellIndex_, merged.get(cellIndex_)));
                    }
                }
            }
        }
        sheets.add(sheetIndex, Objects.isNull(sheetBiFunction) ? sheet : sheetBiFunction.apply(sheet, merged));
        return this;
    }
 
 
    /**
     * <per>
     * <p>指定行索引強制合併指定的列資料:CellRangeAddress原始方法呼叫</p>
     * <per/>
     * @param merged
     * @param sheetBiFunction
     * @return com.hhwy.pwps.managepointins.service.impl.WorkbookUtil
     * @throws
     * @Description : TODO
     **/
    public WorkbookUtil setCellRangeAddress(Map<int[], int[]> merged, BiFunction<HSSFSheet, Map<int[], int[]>, HSSFSheet> sheetBiFunction) {
        HSSFSheet sheet = this.sheets.get(sheetIndex);
        if (Objects.nonNull(merged) && Objects.isNull(sheetBiFunction)) {
            merged.forEach((o1, o2) -> {
                sheet.addMergedRegionUnsafe(new CellRangeAddress(o1[0], o1[1], o2[0], o2[1]));
            });
        }
        sheets.add(sheetIndex, Objects.isNull(sheetBiFunction) ? sheet : sheetBiFunction.apply(sheet, merged));
        return this;
    }
 
    /**
     * <per>
     * <p>資料列合併對應行資料</p>
     * <per/>
     *
     * @param list            sheet資料[必填]
     * @param merged          列索引[必填]
     * @param sheetBiFunction
     * @return com.hhwy.pwps.managepointins.service.impl.WorkbookUtil
     * @throws
     * @Description : TODO
     **/
    public WorkbookUtil setDataRowMerged(List<Integer> merged, List<?> list, BiFunction<HSSFSheet, List<Integer>, HSSFSheet> sheetBiFunction) {
        HSSFSheet sheet = this.sheets.get(sheetIndex);
        List<CellRangeAddress> cellRangeAddresses = new ArrayList<>();
        if (Objects.nonNull(merged) && Objects.isNull(sheetBiFunction)) {
            //資料起始行
            int numberOfRows = sheet.getPhysicalNumberOfRows();
            int first_ = numberOfRows - list.size();
            merged.stream().forEach(index -> {
                String old = null;
                int first = first_;
                for (int i = first; i < numberOfRows; i++) {
                    HSSFRow sheetRow = sheet.getRow(i);
                    String cell = sheetRow.getCell(index).toString();
                    //第一行跳過
                    if (i == first_) {
                        old = cell;
                        continue;
                    }
                    //合併邏輯
                    if (!old.equals(cell)) {
                        if (first != i - 1) {
                            sheet.addMergedRegionUnsafe(new CellRangeAddress(first, i - 1, index, index));
                            //  cellRangeAddresses.add(new CellRangeAddress(first, i - 1, index, index));
                        }
                        first = i;
                        old = cell;
                        // 最後一行判斷
                    } else if (i == numberOfRows - 1) {
                        if (first != i) {
                            sheet.addMergedRegionUnsafe(new CellRangeAddress(first, i, index, index));
                            //    cellRangeAddresses.add(new CellRangeAddress(first, i - 1, index, index));
                        }
                    }
                }
            });
        }
        sheets.add(sheetIndex, Objects.isNull(sheetBiFunction) ? sheet : sheetBiFunction.apply(sheet, merged));
        return this;
    }
 
    /**
     * <per>
     * <p>當前行實現單元格強制合併</p>
     * <per/>
     *
     * @param merged          合併索引[必填] (firstCol,lastCol)
     * @param sheetBiFunction
     * @return com.hhwy.pwps.managepointins.service.impl.WorkbookUtil
     * @throws
     * @Description : TODO
     **/
    public WorkbookUtil setRowMerged(Map<Integer, Integer> merged, BiFunction<HSSFSheet, Map<Integer, Integer>, HSSFSheet> sheetBiFunction) {
        HSSFSheet sheet = this.sheets.get(sheetIndex);
        if (Objects.nonNull(merged) && Objects.isNull(sheetBiFunction)) {
            int numberOfRows = sheet.getPhysicalNumberOfRows();
            List<Integer> cellIndex = merged.keySet().stream().collect(Collectors.toList());
            for (int i = 0; i < cellIndex.size(); i++) {
                int index = cellIndex.get(i);
                sheet.addMergedRegionUnsafe(new CellRangeAddress(numberOfRows - 1, numberOfRows - 1, index, merged.get(index)));
            }
        }
        sheets.add(sheetIndex, Objects.isNull(sheetBiFunction) ? sheet : sheetBiFunction.apply(sheet, merged));
        return this;
    }
 
    /**
     * <per>
     * <p>sheet頁指定列索引插入單行資料</p>
     * <per/>
     *
     * @param data            單行資料[必填]  Map<Integer, String> (列索引,資料)
     * @param sheetBiFunction
     * @return com.hhwy.pwps.managepointins.service.impl.WorkbookUtil
     * @throws
     * @Description : TODO Specify column index inserts single line data
     **/
    public WorkbookUtil setSheetRow(Map<Integer, String> data, BiFunction<HSSFSheet, Map<Integer, String>, HSSFSheet> sheetBiFunction) {
        HSSFSheet sheet = this.sheets.get(sheetIndex);
        if (Objects.nonNull(data) && Objects.isNull(sheetBiFunction)) {
            int numberOfRows = sheet.getPhysicalNumberOfRows();
            HSSFRow sheetRow = sheet.createRow(numberOfRows);
            HSSFCellStyle cellStyle = hssfWorkbook.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER_SELECTION);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            List<Integer> cellIndex = data.keySet().stream().collect(Collectors.toList());
            for (int i = 0; i < cellIndex.size(); i++) {
                int index = cellIndex.get(i);
                HSSFCell cell = sheetRow.createCell(index);
                cell.setCellValue(data.get(index));
                cell.setCellStyle(cellStyle);
            }
        }
        sheets.add(sheetIndex, Objects.isNull(sheetBiFunction) ? sheet : sheetBiFunction.apply(sheet, data));
        return this;
    }
 
 
    /**
     * <per>
     * <p>sheet頁資料填充</p>
     * <per/>
     *
     * @param headDetails     表頭[必填]
     * @param list            資料[必填]
     * @param sheetBiFunction
     * @return com.hhwy.pwps.managepointins.service.impl.WorkbookUtil
     * @throws
     * @Description : TODO
     **/
    public WorkbookUtil setSheetData(HeadDetails headDetails, List<?> list, BiFunction<HSSFSheet, List<?>, HSSFSheet> sheetBiFunction) {
        HSSFSheet sheet = this.sheets.get(sheetIndex);
        List<List<String>> sheetData = new ArrayList<List<String>>();
        if (Objects.nonNull(list) && Objects.isNull(sheetBiFunction)) {
            //準備資料
            list.stream().forEach(o -> {
            	
                String jsonString = o.toString();
                List<String> collect = headDetails.builder().stream().map(headDetail -> {
                	// 將單行資料轉化為JSON串。
                	JSONObject json = null;
					try {
						json = new JSONObject(jsonString);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    String key = headDetail.getKey();
                    Object obj = null;
					try {
						obj = json.get(key);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    if (obj instanceof String) {
                        return obj.toString();
                    } else if (obj instanceof BigDecimal) {
                        return ((BigDecimal) obj).stripTrailingZeros().toPlainString();
                    } else {
                        if (obj != null) {
                            return obj.toString();
                        } else {
                            return " ";
                        }
                    }
                }).collect(Collectors.toList());
                sheetData.add(collect);
            });
            int numberOfRows = sheet.getPhysicalNumberOfRows();
            HSSFCellStyle cellStyle = hssfWorkbook.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER_SELECTION);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            //資料解析
            for (int rowIndex = 0; rowIndex < sheetData.size(); rowIndex++) {
                HSSFRow sheetRow = sheet.createRow(numberOfRows + rowIndex);
                List<String> cells = sheetData.get(rowIndex);
                for (int cellIndex = 0; cellIndex < cells.size(); cellIndex++) {
                    HSSFCell cell = sheetRow.createCell(cellIndex);
                    cell.setCellValue(cells.get(cellIndex));
                    if (headDetails.builder().get(cellIndex).isCenter()) {
                        cell.setCellStyle(cellStyle);
                    }
                }
            }
        }
        sheets.add(sheetIndex, Objects.isNull(sheetBiFunction) ? sheet : sheetBiFunction.apply(sheet, list));
        return this;
    }
 
    /**
     * <per>
     * <p>shell頁表尾初始化</p>
     * <per/>
     *
     * @param sheetFoot       表尾[可選]
     * @param sheetBiFunction
     * @return com.hhwy.pwps.managepointins.service.impl.WorkbookUtil
     * @throws
     * @Description : TODO
     **/
    public WorkbookUtil initSheetFoot(String sheetFoot, BiFunction<HSSFSheet, String, HSSFSheet> sheetBiFunction) {
        HSSFSheet sheet = this.sheets.get(sheetIndex);
        if (Objects.nonNull(sheetFoot) && Objects.isNull(sheetBiFunction)) {
            HSSFCellStyle cellStyle = hssfWorkbook.createCellStyle();
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            HSSFFont font = hssfWorkbook.createFont();
            int numberOfRows = sheet.getPhysicalNumberOfRows();
            int numberOfCells = sheet.getRow(numberOfRows - 1).getPhysicalNumberOfCells();
            HSSFRow sheetRow = sheet.createRow(numberOfRows);
            sheetRow.setHeight((short) 800);
            font.setFontHeightInPoints((short) 8);
            font.setFontName("宋體");
            cellStyle.setFont(font);
            HSSFCell cell = sheetRow.createCell(0);
            cell.setCellValue(sheetFoot);
            cell.setCellStyle(cellStyle);
            sheet.addMergedRegionUnsafe(new CellRangeAddress(numberOfRows, numberOfRows, 0, numberOfCells - 1));
        }
        sheets.add(sheetIndex, Objects.isNull(sheetBiFunction) ? sheet : sheetBiFunction.apply(sheet, sheetFoot));
        return this;
    }
 
 
    /**
     * <per>
     * <p>shell頁表頭的初始化,可以傳入BiFunction自定義初始化邏輯,使用預設值，傳入null</p>
     * <per/>
     *
     * @param headDetails     表頭[必填]
     * @param sheetTitle      標題[可選]
     * @param projectName     工程名稱[可選]
     * @param sheetBiFunction 初始化邏輯
     * @return org.apache.poi.hssf.usermodel.HSSFSheet
     * @throws
     * @Description : TODO
     **/
    public WorkbookUtil initSheetHead(HeadDetails headDetails, String sheetTitle, String projectName, BiFunction<HSSFSheet, HeadDetails, HSSFSheet> sheetBiFunction) {
        //行索引
        int rowIndex = 0;
        HSSFCellStyle cellStyle = hssfWorkbook.createCellStyle();
        HSSFRow sheetRow = null;
        HSSFSheet sheet = this.sheets.get(sheetIndex);
        // 有標題行時
        if (Objects.nonNull(sheetTitle)) {
            HSSFCellStyle cellStyleTitle = hssfWorkbook.createCellStyle();
            HSSFFont fontTitle = hssfWorkbook.createFont();
            //對齊方式
            cellStyleTitle.setAlignment(HorizontalAlignment.CENTER_SELECTION);
            cellStyleTitle.setVerticalAlignment(VerticalAlignment.CENTER);
            fontTitle.setFontName("宋體");
            //大小
            fontTitle.setFontHeightInPoints((short) 21);
            cellStyleTitle.setFont(fontTitle);
            sheetRow = sheet.createRow(rowIndex++);
            sheetRow.setHeight((short) 800);
            HSSFCell cell = sheetRow.createCell(0);
            cell.setCellValue(sheetTitle);
            cell.setCellStyle(cellStyleTitle);
            // 合併單元格
            sheet.addMergedRegionUnsafe(new CellRangeAddress(rowIndex - 1, rowIndex - 1, 0, headDetails.headSize() - 1));
        }
        // 有專案工程名的時
        if (Objects.nonNull(projectName)) {
            HSSFCellStyle cellStyleName = hssfWorkbook.createCellStyle();
            HSSFFont fontName = hssfWorkbook.createFont();
            sheetRow = sheet.createRow(rowIndex++);
            HSSFCell cell = sheetRow.createCell(0);
            fontName.setBold(true);
            fontName.setFontHeightInPoints((short) 10);
            fontName.setFontName("微軟雅黑");
            cellStyleName.setAlignment(HorizontalAlignment.LEFT);
            cellStyleName.setFont(fontName);
            cell.setCellStyle(cellStyleName);
            cell.setCellValue(projectName);
            // 合併單元格
            sheet.addMergedRegionUnsafe(new CellRangeAddress(rowIndex - 1, rowIndex - 1, 0, headDetails.headSize() - 1));
        }
 
        // 填充表頭。預設合併同名表頭
        sheetRow = sheet.createRow(rowIndex++);
        List<HeadDetails.HeadDetail> builder = headDetails.builder();
        String oldName = builder.get(0).getTitle();
        HSSFFont fontHead = hssfWorkbook.createFont();
        fontHead.setBold(true);
        cellStyle.setAlignment(HorizontalAlignment.CENTER_SELECTION);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        fontHead.setFontHeightInPoints((short) 10);
        fontHead.setFontName("微軟雅黑");
        cellStyle.setFont(fontHead);
        for (int i = 0; i < headDetails.headSize(); i++) {
            HSSFCell cell = sheetRow.createCell(i);
            //填充單元格資料
            HeadDetails.HeadDetail headDetail = builder.get(i);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(headDetail.getTitle());
            sheet.setColumnWidth(i, headDetail.getWidth() * 200);
        }
        sheets.add(sheetIndex, Objects.isNull(sheetBiFunction) ? sheet : sheetBiFunction.apply(sheet, headDetails));
        return this;
    }
 
 
    /**
     * <per>
     * <p>sheet頁初始化，設定預設值，可以傳入BiFunction自定義初始化邏輯,使用預設值，傳入null</p>
     * <per/>
     *
     * @param sheetName       sheet頁名稱[可選]
     * @param sheetBiFunction 自定義sheet頁規則，使用預設傳入 null
     * @return org.apache.poi.hssf.usermodel.HSSFSheet
     * @throws
     * @Description : TODO
     **/
    public WorkbookUtil initSheet(String sheetName, BiFunction<HSSFWorkbook, String, HSSFSheet> sheetBiFunction) {
        HSSFSheet sheet = this.hssfWorkbook.createSheet(Optional.ofNullable(sheetName).orElse("sheet頁XX"));
        //建立預設樣式
        sheet.setDefaultColumnWidth(15);
        sheet.setDefaultRowHeight((short) 300);
 
        sheets.add(sheetIndex == 0 ? sheetIndex : ++sheetIndex, Objects.isNull(sheetBiFunction) ? sheet : sheetBiFunction.apply(this.hssfWorkbook, sheetName));
        return this;
    }
 
 
    /**
     * <per>
     * <p>Workbook 初始化</p>
     * <per/>
     *
     * @param setCategory 文件類別[可選]
     * @param setManager  文件管理員[可選]
     * @param setCompany  設定公司資訊[可選]
     * @param setTitle    文件標題[可選]
     * @param setAuthor   文件作者[可選]
     * @param setComments 文件備註[可選]
     * @return com.hhwy.pwps.managepointins.service.impl.ManagePointIn.WorkbookUtil
     * @throws
     * @Description : TODO
     **/
    public WorkbookUtil initWorkbook(String setCategory, String setManager, String setCompany, String setTitle, String setAuthor, String setComments) {
        //1. 建立一個 Excel 文件
        HSSFWorkbook workbook = new HSSFWorkbook();
        //2. 建立文件摘要
        workbook.createInformationProperties();
        //3. 獲取並配置文件資訊
        DocumentSummaryInformation docInfo = workbook.getDocumentSummaryInformation();
        //文件類別
        docInfo.setCategory(Optional.ofNullable(setCategory).orElse("配網輸出報表"));
        //文件管理員
        docInfo.setManager(Optional.ofNullable(setManager).orElse("配網工程評審平臺"));
        //設定公司資訊
        docInfo.setCompany(Optional.ofNullable(setCompany).orElse("XXXXXX"));
        docInfo.setDocumentVersion("1.0");
        //4. 獲取文件摘要資訊
        SummaryInformation summInfo = workbook.getSummaryInformation();
        //文件標題
        summInfo.setTitle(Optional.ofNullable(setTitle).orElse("配網輸出報表"));
        //文件作者
        summInfo.setAuthor(Optional.ofNullable(setAuthor).orElse("配網工程評審平臺"));
        // 建立時間
        summInfo.setCreateDateTime(new Date());
        // 文件備註
        summInfo.setComments(Optional.ofNullable(setComments).orElse(LocalDateTime.now().toString() + " 配網工程評審平臺匯出"));
        this.hssfWorkbook = workbook;
        return this;
    }
 
 
    public WorkbookUtil initWorkbook() {
        //1. 建立一個 Excel 文件
        HSSFWorkbook workbook = new HSSFWorkbook();
        this.hssfWorkbook = workbook;
        return this;
    }
 
 
    /**
     * <per>
     * <p>Excel以位元組陣列輸出</p>
     * <per/>
     *
     * @param
     * @return byte[]
     * @throws
     * @Description : TODO Output byte array
     **/
    public byte[] builderByte() {
        return hssfWorkbook.getBytes();
    }
 
 
    /**
     * <per>
     * <p>Excel以application/octet-stream形式輸出，返回二進位制的報文實體</p>
     * <per/>
     *
     * @param
     * @return org.springframework.http.ResponseEntity<byte       [       ]>
     * @throws
     * @Description : TODO
     **/
    public ResponseEntity<byte[]> builderResponseEntity() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        HttpHeaders headers = new HttpHeaders();
        try {
        	System.out.print(new String(fileName.getBytes("UTF-8"), "ISO-8859-1"));
            headers.setContentDispositionFormData("attachment", new String(fileName.getBytes("UTF-8"), "ISO-8859-1"));
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            hssfWorkbook.write(baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<byte[]>(baos.toByteArray(), headers, HttpStatus.CREATED);
    }
 
 
 
}
