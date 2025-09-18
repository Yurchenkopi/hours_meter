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
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
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
    private static final float MAIN_FONT_SIZE = 14f;           // Основной размер шрифта
    private static final float HEADER_FONT_SIZE = 16f;         // Размер шрифта заголовков
    private static final float TABLE_FONT_SIZE = 13f;          // Размер шрифта главного заголовка
    private static final float CHARACTER_SPACING = 1.5f;       // Расстояние между символами
    private static final float WORD_SPACING = 2f;              // Расстояние между словами
    private static final float LINE_SPACING = 1.0f;            // Межстрочный интервал
    private static final float PARAGRAPH_SPACING = 10f;        // Отступы между абзацами
    private static final DeviceRgb DEFAULT_HEADER_COLOR = new DeviceRgb(173, 181, 189);
    private static final DeviceRgb BLUE_HEADER_COLOR = new DeviceRgb(13, 110, 253);

    public void createPDFReport(OutputStream os,
                                Report report,
                                User user,
                                LocalDate startDate,
                                LocalDate endDate) throws IOException {
        PdfWriter writer = new PdfWriter(os);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        document.setMargins(25, 50, 50, 25);
        InputStream fontStream = getClass().getResourceAsStream("/static/fonts/TimesNewRoman/timesnewromanpsmt.ttf");
        PdfFont font = PdfFontFactory.createFont(IOUtils.toByteArray(fontStream), PdfEncodings.IDENTITY_H, true);
        document.setFont(font);
        Paragraph userDataParagraph = createStyledParagraph(
                font,
                HEADER_FONT_SIZE,
                TextAlignment.LEFT
        );
        float[] headerTableColumnWidths = {35f, 65f}; // Пропорциональные ширины колонок
        Table headerTable = new Table(UnitValue.createPercentArray(headerTableColumnWidths));
        headerTable.setWidth(UnitValue.createPercentValue(70));
        Cell fioCell = new Cell();
        Cell fioDataCell = new Cell();
        Cell dateCell = new Cell();
        Cell dateDataCell = new Cell();
        Cell timeCell = new Cell();
        Cell timeDataCell = new Cell();

        fioCell.add(createStyledParagraph(font, MAIN_FONT_SIZE, TextAlignment.LEFT)
                        .add("ФИО"))
                .setBorder(new SolidBorder(ColorConstants.WHITE, 1))
                .setPaddingLeft(5f)
                .setBackgroundColor(DEFAULT_HEADER_COLOR);
        fioDataCell.add(createStyledParagraph(font, MAIN_FONT_SIZE, TextAlignment.LEFT)
                        .add(String.format("   %s %s %s   ", user.getSurname(), user.getName(), user.getPatronymic()))
                        .setFontColor(ColorConstants.BLACK))
                .setBorder(new SolidBorder(ColorConstants.WHITE, 1))
                .setPaddingLeft(5f)
                .setBackgroundColor(DEFAULT_HEADER_COLOR);
        dateCell.add(createStyledParagraph(font, MAIN_FONT_SIZE, TextAlignment.LEFT)
                        .add("Дата создания"))
                .setBorder(new SolidBorder(ColorConstants.WHITE, 1))
                .setPaddingLeft(5f)
                .setBackgroundColor(DEFAULT_HEADER_COLOR);
        dateDataCell.add(createStyledParagraph(font, MAIN_FONT_SIZE, TextAlignment.LEFT)
                        .add(String.format("   %s   ", java.time.LocalDate.now()))
                        .setFontColor(ColorConstants.BLACK))
                .setBorder(new SolidBorder(ColorConstants.WHITE, 1))
                .setPaddingLeft(5f)
                .setBackgroundColor(DEFAULT_HEADER_COLOR);
        timeCell.add(createStyledParagraph(font, MAIN_FONT_SIZE, TextAlignment.LEFT)
                        .add("Период"))
                .setBorder(new SolidBorder(ColorConstants.WHITE, 1))
                .setPaddingLeft(5f)
                .setBackgroundColor(DEFAULT_HEADER_COLOR);
        timeDataCell.add(createStyledParagraph(font, MAIN_FONT_SIZE, TextAlignment.LEFT)
                        .add(String.format("c %s по %s", startDate, endDate))
                        .setFontColor(ColorConstants.BLACK))
                .setBorder(new SolidBorder(ColorConstants.WHITE, 1))
                .setPaddingLeft(5f)
                .setBackgroundColor(DEFAULT_HEADER_COLOR);

        headerTable.addCell(fioCell)
                .addCell(fioDataCell)
                .addCell(dateCell)
                .addCell(dateDataCell)
                .addCell(timeCell)
                .addCell(timeDataCell);
        document.add(headerTable);
        document.add(new Paragraph())
                .add(new Paragraph())
                .add(new Paragraph());

        Table table = new Table(UnitValue.createPercentArray(getReportSize(user.getReportSetting())))
                .useAllAvailableWidth();
        var userReportSettings = getReportSettingMap(user.getReportSetting());
        for (String header : userReportSettings.keySet()) {
            if (userReportSettings.get(header)) {
                table.addHeaderCell(
                        new Cell()
                                .add(createStyledParagraph(font, TABLE_FONT_SIZE, TextAlignment.LEFT)
                                        .add(header))
                                .setBorder(new SolidBorder(ColorConstants.BLACK, 1))
                                .setPaddingLeft(5f)
                                .setBackgroundColor(DEFAULT_HEADER_COLOR)
                                .setTextAlignment(TextAlignment.CENTER)
                                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                );
            }
        }
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");
        for (List<ItemDto> items : report.getContent().values()) {
            for (ItemDto item : items) {
                if (user.getReportSetting().isDateColumn()) {
                    table.addCell(new Cell()
                            .add(createStyledParagraph(font, TABLE_FONT_SIZE, TextAlignment.LEFT)
                                    .add(new Paragraph(item.getDate().format(df))))
                            .setBorder(new SolidBorder(ColorConstants.BLACK, 1))
                            .setPaddingLeft(5f)
                            .setTextAlignment(TextAlignment.LEFT)
                    );
                }
                if (user.getReportSetting().isStartTimeColumn()) {
                    table.addCell(new Cell()
                            .add(createStyledParagraph(font, TABLE_FONT_SIZE, TextAlignment.LEFT)
                                    .add(new Paragraph(item.getStartTime().format(tf))))
                            .setBorder(new SolidBorder(ColorConstants.BLACK, 1))
                            .setPaddingLeft(3f)
                            .setTextAlignment(TextAlignment.LEFT)
                    );
                }
                if (user.getReportSetting().isEndTimeColumn()) {
                    table.addCell(new Cell()
                            .add(createStyledParagraph(font, TABLE_FONT_SIZE, TextAlignment.LEFT)
                                    .add(new Paragraph(item.getEndTime().format(tf))))
                            .setBorder(new SolidBorder(ColorConstants.BLACK, 1))
                            .setPaddingLeft(3f)
                            .setTextAlignment(TextAlignment.LEFT)
                    );
                }
                if (user.getReportSetting().isLunchBreakColumn()) {
                    table.addCell(new Cell()
                            .add(createStyledParagraph(font, TABLE_FONT_SIZE, TextAlignment.LEFT)
                                    .add(new Paragraph(item.isLunchBreak() ? "Да" : "Нет")))
                            .setBorder(new SolidBorder(ColorConstants.BLACK, 1))
                            .setPaddingLeft(3f)
                            .setTextAlignment(TextAlignment.LEFT)
                    );
                }
                if (user.getReportSetting().isExtraHoursOnlyColumn()) {
                    table.addCell(new Cell()
                            .add(createStyledParagraph(font, TABLE_FONT_SIZE, TextAlignment.LEFT)
                                    .add(new Paragraph(item.isExtraHoursOnly() ? "Да" : "Нет")))
                            .setBorder(new SolidBorder(ColorConstants.BLACK, 1))
                            .setPaddingLeft(3f)
                            .setTextAlignment(TextAlignment.LEFT)
                    );
                }
                if (user.getReportSetting().isRemarkColumn()) {
                    table.addCell(new Cell()
                            .add(createStyledParagraph(font, TABLE_FONT_SIZE, TextAlignment.LEFT)
                                    .add(new Paragraph(item.getRemark())))
                            .setBorder(new SolidBorder(ColorConstants.BLACK, 1))
                            .setPaddingLeft(3f)
                            .setTextAlignment(TextAlignment.LEFT)
                    );
                }
                if (user.getReportSetting().isHoursColumn()) {
                    table.addCell(new Cell()
                            .add(createStyledParagraph(font, TABLE_FONT_SIZE, TextAlignment.LEFT)
                                    .add(new Paragraph(String.format("%.2f",
                            (float) Math.round(item.getMinutes() * 100 / (60 * 8)) / 100))))
                            .setBorder(new SolidBorder(ColorConstants.BLACK, 1))
                            .setPaddingLeft(3f)
                            .setTextAlignment(TextAlignment.LEFT)
                    );
                }

            }
        }
        document.add(table);
        document.add(new Paragraph())
                .add(new Paragraph());
        float[] hoursTableColumnWidths = {65f, 35f};
        Table hoursTable = new Table(UnitValue.createPercentArray(hoursTableColumnWidths));
        hoursTable.setWidth(UnitValue.createPercentValue(50));
        Cell hoursCell = new Cell();
        Cell hoursDataCell = new Cell();

        hoursCell.add(createStyledParagraph(font, MAIN_FONT_SIZE, TextAlignment.LEFT)
                        .add("Общее время, дней"))
                .setBorder(new SolidBorder(ColorConstants.BLACK, 1))
                .setPaddingLeft(5f)
                .setBackgroundColor(DEFAULT_HEADER_COLOR);
        hoursDataCell.add(createStyledParagraph(font, MAIN_FONT_SIZE, TextAlignment.LEFT)
                        .add(String.valueOf(
                                (float) Math.round(report.getTimeInMinutes() * 100 / (60 * 8)) / 100))
                        .setFontColor(ColorConstants.BLACK))
                .setBorder(new SolidBorder(ColorConstants.BLACK, 1))
                .setPaddingLeft(5f)
                .setBackgroundColor(DEFAULT_HEADER_COLOR);
        document.add(hoursTable
                        .addCell(hoursCell)
                        .addCell(hoursDataCell)
                .setHorizontalAlignment(HorizontalAlignment.LEFT));
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
