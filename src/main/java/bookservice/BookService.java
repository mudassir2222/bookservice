package bookservice;

import java.util.*;
import org.apache.commons.csv.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class BookService {

    // CSV is READ-ONLY and must be loaded from classpath
    private static final String CSV_FILE = "Book1.csv";

    // Excel must be written to filesystem (WAR is read-only)
    private static final String EXCEL_FILE = "/opt/bookservice/books_output.xlsx";

    // ---------------- GET SINGLE BOOK ----------------
    public Book getBook(int id) throws Exception {

        InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream(CSV_FILE);

        if (is == null) {
            throw new RuntimeException("Book1.csv not found in classpath");
        }

        Reader reader = new BufferedReader(new InputStreamReader(is));
        Iterable<CSVRecord> records =
                CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);

        for (CSVRecord record : records) {
            if (Integer.parseInt(record.get("id")) == id) {
                Book book = new Book();
                book.id = id;
                book.title = record.get("title");
                book.author = record.get("author");
                book.price = Double.parseDouble(record.get("price"));
                return book;
            }
        }

        throw new RuntimeException("Book not found");
    }

    // ---------------- SAVE BOOK TO EXCEL ----------------
    public void saveBook(Book book) throws Exception {

        File excelFile = new File(EXCEL_FILE);

        XSSFWorkbook workbook;
        if (excelFile.exists()) {
            workbook = new XSSFWorkbook(new FileInputStream(excelFile));
        } else {
            workbook = new XSSFWorkbook();
            workbook.createSheet("Books");
            workbook.getSheetAt(0).createRow(0)
                    .createCell(0).setCellValue("id");
            workbook.getSheetAt(0).getRow(0)
                    .createCell(1).setCellValue("title");
            workbook.getSheetAt(0).getRow(0)
                    .createCell(2).setCellValue("author");
            workbook.getSheetAt(0).getRow(0)
                    .createCell(3).setCellValue("price");
        }

        var sheet = workbook.getSheetAt(0);
        var row = sheet.createRow(sheet.getLastRowNum() + 1);

        row.createCell(0).setCellValue(book.id);
        row.createCell(1).setCellValue(book.title);
        row.createCell(2).setCellValue(book.author);
        row.createCell(3).setCellValue(book.price);

        try (FileOutputStream fos = new FileOutputStream(excelFile)) {
            workbook.write(fos);
        }

        workbook.close();
    }

    // ---------------- GET ALL BOOKS ----------------
    public List<Book> getAllBooks() throws Exception {

        List<Book> books = new ArrayList<>();

        InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream(CSV_FILE);

        if (is == null) {
            throw new RuntimeException("Book1.csv not found in classpath");
        }

        Reader reader = new BufferedReader(new InputStreamReader(is));
        Iterable<CSVRecord> records =
                CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);

        for (CSVRecord record : records) {
            Book book = new Book();
            book.id = Integer.parseInt(record.get("id"));
            book.title = record.get("title");
            book.author = record.get("author");
            book.price = Double.parseDouble(record.get("price"));
            books.add(book);
        }

        return books;
    }
}

