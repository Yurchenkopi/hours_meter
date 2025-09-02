package ru.yurch.hours.service;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;

@Service
public class ReportService {
    public void createPDFReport(OutputStream os,
                                LocalDate startDate,
                                LocalDate endDate) throws IOException {
        PdfWriter writer = new PdfWriter(os);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        InputStream fontStream = getClass().getResourceAsStream("/static/fonts/Arial/arialmt.ttf");
        PdfFont font = PdfFontFactory.createFont(IOUtils.toByteArray(fontStream), PdfEncodings.IDENTITY_H, true);
        document.setFont(font);
        document.add(new Paragraph("Это ваш отчет!"));
        document.add(new Paragraph("Дата создания: " + java.time.LocalDate.now()));
        document.add(new Paragraph("Начало периода: " + startDate.toString()));
        document.add(new Paragraph("Конец периода: " + endDate.toString()));
        document.close();
    }
}
