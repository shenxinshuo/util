import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author: 沈鑫烁
 * @date: 2021/11/27
 * @description:
 */
public class Test {


    @org.junit.Test
    public void test() throws IOException {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue("你得");
        FileOutputStream fileOutputStream = new FileOutputStream("test.xls");
        workbook.write(fileOutputStream);
        fileOutputStream.close();
        workbook.close();
    }

}
