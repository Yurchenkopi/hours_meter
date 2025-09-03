package ru.yurch.hours.service;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import ru.yurch.hours.dto.ItemDto;
import ru.yurch.hours.model.Report;
import ru.yurch.hours.model.User;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
public class ReportService {
    public void createPDFReport(OutputStream os,
                                Report report,
                                User user,
                                LocalDate startDate,
                                LocalDate endDate) throws IOException {
        PdfWriter writer = new PdfWriter(os);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        InputStream fontStream = getClass().getResourceAsStream("/static/fonts/Arial/arialmt.ttf");
        PdfFont font = PdfFontFactory.createFont(IOUtils.toByteArray(fontStream), PdfEncodings.IDENTITY_H, true);
        document.setFont(font);
        document.add(new Paragraph(String.format("ФИО: %s %s %s", user.getSurname(), user.getName(), user.getPatronymic())));
        document.add(new Paragraph("Дата создания: " + java.time.LocalDate.now()));
        document.add(new Paragraph(String.format("Период с %s по %s", startDate, endDate)));
        float[] columnWidths = {40, 30, 30, 40};
        Table table = new Table(UnitValue.createPercentArray(columnWidths))
                .useAllAvailableWidth();
        String[] headers = {
                "Дата", "Время начала", "Время окончания", "Доп.время, дней"
        };
        for (String h : headers) {
            table.addHeaderCell(
                    new Cell()
                            .add(new Paragraph(h).setBold())
                            .setTextAlignment(TextAlignment.CENTER)
            );
        }
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");
        for (List<ItemDto> items : report.getContent().values()) {
            for (ItemDto item : items) {
                table.addCell(new Cell().add(new Paragraph(item.getDate().format(df))));
                table.addCell(new Cell().add(new Paragraph(item.getStartTime().format(tf))));
                table.addCell(new Cell().add(new Paragraph(item.getEndTime().format(tf))));
                table.addCell(new Cell().add(new Paragraph(String.format("%.2f",
                        (float) Math.round(item.getMinutes() * 100 / (60 * 8)) / 100))));
            }
        }
        document.add(table);
        document.add(new Paragraph("Общее время в днях: " +
                (float) Math.round(report.getTimeInMinutes() * 100 / (60 * 8)) / 100).setFontSize(20));
        document.close();
    }
}
