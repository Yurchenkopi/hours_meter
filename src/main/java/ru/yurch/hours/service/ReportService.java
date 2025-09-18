package ru.yurch.hours.service;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import ru.yurch.hours.dto.ItemDto;
import ru.yurch.hours.model.Report;
import ru.yurch.hours.model.ReportSetting;
import ru.yurch.hours.model.User;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Service
public class ReportService {
    private static final float MAIN_FONT_SIZE = 12f;           // Основной размер шрифта
    private static final float HEADER_FONT_SIZE = 16f;         // Размер шрифта заголовков
    private static final float TITLE_FONT_SIZE = 20f;          // Размер шрифта главного заголовка
    private static final float CHARACTER_SPACING = 1.5f;       // Расстояние между символами
    private static final float WORD_SPACING = 2f;              // Расстояние между словами
    private static final float LINE_SPACING = 1.2f;            // Межстрочный интервал
    private static final float PARAGRAPH_SPACING = 10f;        // Отступы между абзацами
    private static final DeviceRgb DEFAULT_HEADER_COLOR = new DeviceRgb(173, 181, 189);

    public void createPDFReport(OutputStream os,
                                Report report,
                                User user,
                                LocalDate startDate,
                                LocalDate endDate) throws IOException {
        PdfWriter writer = new PdfWriter(os);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        document.setMargins(50, 50, 50, 50);
        InputStream fontStream = getClass().getResourceAsStream("/static/fonts/TimesNewRoman/timesnewromanpsmt.ttf");
        PdfFont font = PdfFontFactory.createFont(IOUtils.toByteArray(fontStream), PdfEncodings.IDENTITY_H, true);
        document.setFont(font);
        Paragraph userDataParagraph = createStyledParagraph(
                font,
                HEADER_FONT_SIZE,
                TextAlignment.LEFT
        );
        document.add(userDataParagraph
                .add("ФИО:")
                .add("    ")
                .add(new Text(String.format("%s %s %s", user.getSurname(), user.getName(), user.getPatronymic()))
                        .setBackgroundColor(DEFAULT_HEADER_COLOR))
                .add(System.lineSeparator())
                .add(new Text("Дата создания:"))
                .add(new Text("    "))
                .add(new Text(java.time.LocalDate.now().toString())
                        .setBackgroundColor(DEFAULT_HEADER_COLOR))
                .add(System.lineSeparator())
                .add(new Text("Период с"))
                .add(new Text("    "))
                .add(new Text(startDate.toString())
                        .setBackgroundColor(DEFAULT_HEADER_COLOR))
                .add(new Text("    "))
                .add(new Text("по"))
                .add(new Text("    "))
                .add(new Text(endDate.toString())
                        .setBackgroundColor(DEFAULT_HEADER_COLOR))
        );
        Table table = new Table(UnitValue.createPercentArray(getReportSize(user.getReportSetting())))
                .useAllAvailableWidth();
        var userReportSettings = getReportSettingMap(user.getReportSetting());
        for (String header : userReportSettings.keySet()) {
            if (userReportSettings.get(header)) {
                table.addHeaderCell(
                        new Cell()
                                .add(new Paragraph(header).setBold())
                                .setTextAlignment(TextAlignment.CENTER)
                );
            }
        }
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");
        for (List<ItemDto> items : report.getContent().values()) {
            for (ItemDto item : items) {
                if (user.getReportSetting().isDateColumn()) {
                    table.addCell(new Cell().add(new Paragraph(item.getDate().format(df))));
                }
                if (user.getReportSetting().isStartTimeColumn()) {
                    table.addCell(new Cell().add(new Paragraph(item.getStartTime().format(tf))));
                }
                if (user.getReportSetting().isEndTimeColumn()) {
                    table.addCell(new Cell().add(new Paragraph(item.getEndTime().format(tf))));
                }
                if (user.getReportSetting().isLunchBreakColumn()) {
                    table.addCell(new Cell().add(new Paragraph(item.isLunchBreak() ? "Да" : "Нет")));
                }
                if (user.getReportSetting().isExtraHoursOnlyColumn()) {
                    table.addCell(new Cell().add(new Paragraph(item.isExtraHoursOnly() ? "Да" : "Нет")));
                }
                if (user.getReportSetting().isRemarkColumn()) {
                    table.addCell(new Cell().add(new Paragraph(item.getRemark())));
                }
                if (user.getReportSetting().isHoursColumn()) {
                    table.addCell(new Cell().add(new Paragraph(String.format("%.2f",
                            (float) Math.round(item.getMinutes() * 100 / (60 * 8)) / 100))));
                }

            }
        }
        document.add(table);
        document.add(new Paragraph()
                .add(new Text("Общее время в днях:"))
                        .setFontSize(20)
                .add(new Text("    "))
                .add(new Text(String.valueOf(
                        (float) Math.round(report.getTimeInMinutes() * 100 / (60 * 8)) / 100))
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                        .setFontSize(20)));
        document.close();
    }

    private static Paragraph createStyledParagraph(PdfFont font,
                                                   float fontSize, TextAlignment alignment) {
        Paragraph paragraph = new Paragraph();

        // Установка основных параметров шрифта
        paragraph.setFont(font);
        paragraph.setFontSize(fontSize);
        paragraph.setTextAlignment(alignment);

        // Улучшенные настройки для читаемости
        paragraph.setCharacterSpacing(CHARACTER_SPACING);  // Расстояние между символами
        paragraph.setWordSpacing(WORD_SPACING);            // Расстояние между словами
        paragraph.setMultipliedLeading(LINE_SPACING);      // Межстрочный интервал

        // Настройка отступов для лучшего визуального восприятия
        if (alignment == TextAlignment.JUSTIFIED) {
            paragraph.setTextAlignment(TextAlignment.JUSTIFIED);
            paragraph.setSpacingRatio(1f); // Равномерное распределение пробелов
        }

        return paragraph;
    }

    private float[] getReportSize(ReportSetting reportSetting) {
        int size = 0;
        var temp = new ArrayList<Float>();
        if (reportSetting.isDateColumn()) {
            size++;
            temp.add(40f);
        }
        if (reportSetting.isStartTimeColumn()) {
            size++;
            temp.add(30f);
        }
        if (reportSetting.isEndTimeColumn()) {
            size++;
            temp.add(30f);
        }
        if (reportSetting.isLunchBreakColumn()) {
            size++;
            temp.add(20f);
        }
        if (reportSetting.isExtraHoursOnlyColumn()) {
            size++;
            temp.add(20f);
        }
        if (reportSetting.isRemarkColumn()) {
            size++;
            temp.add(40f);
        }
        if (reportSetting.isHoursColumn()) {
            size++;
            temp.add(30f);
        }
        float[] array = new float[size];
        for (int i = 0; i < size; i++) {
            array[i] = temp.get(i);
        }
        return array;
    }

    private Map<String, Boolean> getReportSettingMap(ReportSetting reportSetting) {
        Map<String, Boolean> map = new LinkedHashMap<>();
        map.put("Дата", reportSetting.isDateColumn());
        map.put("Время начала", reportSetting.isStartTimeColumn());
        map.put("Время окончания", reportSetting.isEndTimeColumn());
        map.put("Обеденный перерыв", reportSetting.isLunchBreakColumn());
        map.put("Без учета основного времени", reportSetting.isExtraHoursOnlyColumn());
        map.put("Примечание", reportSetting.isRemarkColumn());
        map.put("Доп.время, дней", reportSetting.isHoursColumn());
        return map;
    }
}
