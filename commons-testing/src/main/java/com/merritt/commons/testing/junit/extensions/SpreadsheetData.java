package com.merritt.commons.testing.junit.extensions;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class SpreadsheetData
{
    
    private transient List<Map<String, String>> data = null;
    
    public SpreadsheetData( final InputStream excelInputStream, final String sheetName ) throws IOException
    {
        this.data = loadFromSpreadsheet( excelInputStream, sheetName );
    }
    
    public List<Map<String, String>> getData()
    {
        return data;
    }
    
    private List<Map<String, String>> loadFromSpreadsheet( final InputStream excelFile, final String sheetName ) throws IOException
    {
        
        HSSFWorkbook workbook = new HSSFWorkbook( excelFile );
        
        Sheet sheet;
        if( "".equals( sheetName ) )
        {
            sheet = workbook.getSheet( sheetName );
        }
        else
        {
            sheet = workbook.getSheetAt( 0 );
        }
        
        int numberOfColumns = countNonEmptyColumns( sheet );
        List<Map<String, String>> rows = new ArrayList<Map<String, String>>();
        Map<String, String> columnData = new HashMap<String, String>();
        String[] columnNames = new String[numberOfColumns];
        boolean emptyRow = false;
        
        for( Row row : sheet )
        {
            if( row.getRowNum() == 0 ) {
                for( int column = 0; column < numberOfColumns; column++ )
                {
                    Cell cell = row.getCell( column, Row.CREATE_NULL_AS_BLANK );
                    cell.setCellType( Cell.CELL_TYPE_STRING );
                    columnNames[column] = cell.getStringCellValue();
                    System.out.println(columnNames[column]);
                }
            }
                
            else
            {
                emptyRow = true;
                columnData = new HashMap<String, String>();
                for( int column = 0; column < numberOfColumns; column++ )
                {
                    
                    Cell cell = row.getCell( column, Row.CREATE_NULL_AS_BLANK );
                    cell.setCellType( Cell.CELL_TYPE_STRING );
                    String temp = cell.getStringCellValue();
                    if(temp != null && !temp.isEmpty()) {
                        emptyRow = false;
                    }
                    columnData.put( columnNames[column], temp );
                }
                if(emptyRow) {
                    break;
                }
                rows.add( columnData );
            }
        }
        return rows;
    }
    
    /**
     * Count the number of columns, using the number of non-empty cells in the
     * first row.
     */
    private int countNonEmptyColumns( final Sheet sheet )
    {
        Row firstRow = sheet.getRow( 0 );
        return firstEmptyCellPosition( firstRow );
    }
    
    private int firstEmptyCellPosition( final Row cells )
    {
        int columnCount = 0;
        for( Cell cell : cells )
        {
            if( cell.getCellType() == Cell.CELL_TYPE_BLANK )
            {
                break;
            }
            columnCount++;
        }
        return columnCount;
    }
   
}
