package org.hcl.IO.myTool;

import java.io.File;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.common.IDfAttr;
import com.documentum.fc.common.IDfValue;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * Take care of excel workbook.
 * 
 * @author Ankur_Tomar
 */
public class Excel {

	/**
	 * Reads the Excel's workbook first sheet
	 * 
	 * @param filename
	 * @return Array of String
	 */
	public static String[] readExcel(String filename) {
		HSSFCell cell;
		short i, j;
		ArrayList al = new ArrayList();
		try {
			HSSFWorkbook wbook = new HSSFWorkbook(new POIFSFileSystem(
					new FileInputStream(filename)));
			HSSFSheet xlssheet = wbook.getSheetAt(0);
			int firstronum = xlssheet.getFirstRowNum();
			int lastrownum = xlssheet.getLastRowNum();
			for (int m = 1; m < lastrownum + 1; m++) {
				HSSFRow row = xlssheet.getRow(m);
				if (row == null)// check rows if they are empty
				{
					continue;
				}
				int lastCell = row.getLastCellNum();
				System.out.println("lastCell " + lastCell);
				i = (short) lastCell;
				for (j = 0; j < i; j++) {

					cell = row.getCell(j);

					if (cell == null) {
						al.add("");
						continue;
					}

					switch (cell.getCellType()) {
					case HSSFCell.CELL_TYPE_NUMERIC:
						al.add(new Double(cell.getNumericCellValue()));
						break;

					case HSSFCell.CELL_TYPE_BLANK:
						al.add("");
						break;

					case HSSFCell.CELL_TYPE_ERROR:
						break;
					case HSSFCell.CELL_TYPE_STRING:
						al.add(cell.getStringCellValue().toString());
						// System.out.println(cell.getStringCellValue().toString());
						break;

					case HSSFCell.CELL_TYPE_BOOLEAN:
						break;

					default:
						al.add("");
						break;
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return (String[]) al.toArray(new String[0]);
	}

	/**
	 * Writes Dctm resultset(i.e. IDfCollection) to Excel workbook.
	 * 
	 * @param col
	 *            IDfCollection
	 * @param name
	 *            Workbook name
	 * @throws Exception
	 */
	public static void writeExcel(IDfCollection col, String name)
			throws Exception {

		int row = 1, column = 0;
		IDfAttr attr;

		String reapAttr;

		WritableWorkbook w = Workbook.createWorkbook(new File(name + ".xls"));
		WritableSheet s = w.createSheet("Results Sheet", 0);

		int attr_count = col.getAttrCount();

		int i = 0;
		while (i < attr_count) {
			attr = col.getAttr(i);
			s.addCell(new Label(i, 0, attr.getName()));
			i++;
		}

		while (col.next()) {

			i = 0;
			while (i < attr_count) {

				attr = col.getAttr(i);
				if (attr.isRepeating()) {
					reapAttr = col
							.getAllRepeatingStrings(attr.getName(), " | ");
				} else {
					IDfValue val = col.getValue(attr.getName());
					reapAttr = val.toString();
				}
				s.addCell(new Label(column, row, reapAttr));
				i++;
				column++;
			}
			row++;
			column = 0;
		}
		col.close();
		w.write();
		w.close();
	}

	public static void writeExcel(String[] data, String filename, int cell)
			throws IOException, WriteException {
		WritableWorkbook w = Workbook
				.createWorkbook(new File(filename + ".xls"));
		WritableSheet s = w.createSheet("Results Sheet", 0);
		int row = 0;
		s.addCell(new Label(0, row++, filename));
		for (int i = 0; i < data.length; i += cell) {
			for (int j = 0; j < cell; j++) {
				s.addCell(new Label(j, row, data[i + j]));
			}
			row++;
		}
		w.write();
		w.close();
	}
}
