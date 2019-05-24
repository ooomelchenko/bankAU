package nb.util;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;

import java.io.*;
import java.util.List;

public class Word implements Serializable {

    public Word() {
    }

    public void testDocument() throws IOException {

        InputStream fs = new FileInputStream("C:\\\\projectFiles\\\\Dodatok 2.docx");

        XWPFDocument docx = new XWPFDocument(fs);

        List<XWPFTable> tableList = docx.getTables();
        XWPFTable tab1= tableList.get(0);
        tab1.getRow(1).getCell(0).setText("hello");
        tab1.addRow(tab1.getRow(1));

        String fileName = "C:\\projectFiles\\Dodatok 2222.docx";
        OutputStream fileOut = new FileOutputStream(fileName);

        docx.write(fileOut);
        fileOut.close();

    }

    public static void main(String[] args) throws IOException {
        Word word = new Word();
        word.testDocument();
    }

}
