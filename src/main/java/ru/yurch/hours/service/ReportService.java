package ru.yurch.hours.service;

import com.itextpdf.io.font.PdfEncodings;
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
                                ReportSetting reportSetting,
                                LocalDate startDate,
                                LocalDate endDate) throws IOException {
        PdfWriter writer = new PdfWriter(os);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        document.setMargins(25, 50, 50, 25);
        InputStream fontStream = getClass().getResourceAsStream("/static/fonts/TimesNewRoman/timesnewromanpsmt.ttf");
        PdfFont font = PdfFontFactory.createFont(IOUtils.toByteArray(fontStream), PdfEncodings.IDENTITY_H, true);
        document.setFont(font);

        float[] userInfoTableColumnWidth = {35f, 65f};
        Table userInfoTable = new Table(UnitValue.createPercentArray(userInfoTableColumnWidth));
        userInfoTable.setWidth(UnitValue.createPercentValue(70));
        Cell fioCell = createUserInfoCell(font, "ФИО");
        Cell fioDataCell = createUserInfoCell(
                font,
                String.format("   %s %s %s   ", user.getSurname(), user.getName(), user.getPatronymic()));
        Cell dateCell = createUserInfoCell(font, "Дата создания");
        Cell dateDataCell = createUserInfoCell(
                font,
                String.format("   %s   ", java.time.LocalDate.now()));
        Cell timeCell = createUserInfoCell(font, "Период");
        Cell timeDataCell = createUserInfoCell(
                font,
                String.format("c %s по %s", startDate, endDate));
        userInfoTable
                .addCell(fioCell)
                .addCell(fioDataCell)
                .addCell(dateCell)
                .addCell(dateDataCell)
                .addCell(timeCell)
                .addCell(timeDataCell);
        document.add(userInfoTable);
        document.add(new Paragraph())
                .add(new Paragraph())
                .add(new Paragraph());

        if (getReportSize(reportSetting).length != 0) {
            Table dataTable = new Table(UnitValue.createPercentArray(getReportSize(reportSetting)))
                    .useAllAvailableWidth();
            var userReportSettings = getReportSettingMap(reportSetting);
            for (String header : userReportSettings.keySet()) {
                if (userReportSettings.get(header)) {
                    dataTable.addHeaderCell(createTableHeaderCell(font, header));
                }
            }
            DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");
            for (List<ItemDto> items : report.getContent().values()) {
                for (ItemDto item : items) {
                    if (reportSetting.isDateColumn()) {
                        dataTable.addCell(createTableMainCell(font, item.getDate().format(df)));
                    }
                    if (reportSetting.isStartTimeColumn()) {
                        dataTable.addCell(createTableMainCell(font, item.getStartTime().format(tf)));
                    }
                    if (reportSetting.isEndTimeColumn()) {
                        dataTable.addCell(createTableMainCell(font, item.getEndTime().format(tf)));
                    }
                    if (reportSetting.isLunchBreakColumn()) {
                        dataTable.addCell(createTableMainCell(font, item.isLunchBreak() ? "Да" : "Нет"));
                    }
                    if (reportSetting.isExtraHoursOnlyColumn()) {
                        dataTable.addCell(createTableMainCell(font, item.isExtraHoursOnly() ? "Да" : "Нет"));
                    }
                    if (reportSetting.isRemarkColumn()) {
                        dataTable.addCell(createTableMainCell(font, item.getRemark()));
                    }
                    if (reportSetting.isHoursColumn()) {
                        dataTable.addCell(createTableMainCell(font, String.format("%.2f",
                                (float) Math.round(item.getMinutes() * 100 / (60 * 8)) / 100)));
                    }

                }
            }

            float[] hoursTableColumnWidths = {65f, 35f};
            Table hoursTable = new Table(UnitValue.createPercentArray(hoursTableColumnWidths));
            hoursTable.setWidth(UnitValue.createPercentValue(50));
            Cell hoursCell = createTableTotalCell(font, "Общее время, дней");
            Cell hoursDataCell = createTableTotalCell(font, String.valueOf(
                    (float) Math.round(report.getTimeInMinutes() * 100 / (60 * 8)) / 100));
            hoursTable
                    .addCell(hoursCell)
                    .addCell(hoursDataCell)
                    .setHorizontalAlignment(HorizontalAlignment.LEFT);

            document.add(dataTable);
            document.add(new Paragraph())
                    .add(new Paragraph());
            document.add(hoursTable);
        } else {
            document.add(createStyledParagraph(font, MAIN_FONT_SIZE, TextAlignment.LEFT, (DeviceRgb) ColorConstants.RED, 0.1f)
                    .add(String.format("Не выбраны данные для формирования отчёта!%s", System.lineSeparator()))
                    .add("Проверьте пользовательские настройки отчёта.")
            );
        }
        document.close();
    }

    private Cell createStyledTableCell(
            Paragraph paragraph,
            String text,
            float textPadding,
            TextAlignment textAlignment,
            VerticalAlignment verticalAlignment,
            DeviceRgb borderColor,
            float borderWidth) {
        return new Cell().add(paragraph.add(text))
                .setBorder(new SolidBorder(borderColor, borderWidth))
                .setPaddingLeft(textPadding)
                .setTextAlignment(textAlignment)
                .setVerticalAlignment(verticalAlignment);
    }

    private static Paragraph createStyledParagraph(PdfFont font,
                                                   float fontSize,
                                                   TextAlignment alignment,
                                                   DeviceRgb textColor,
                                                   float characterSpacing) {
        Paragraph paragraph = new Paragraph();
        paragraph.setFont(font);
        paragraph.setFontSize(fontSize);
        paragraph.setTextAlignment(alignment);
        paragraph.setCharacterSpacing(characterSpacing);
        paragraph.setWordSpacing(WORD_SPACING);
        paragraph.setMultipliedLeading(LINE_SPACING);
        paragraph.setFontColor(textColor);
        if (alignment == TextAlignment.JUSTIFIED) {
            paragraph.setTextAlignment(TextAlignment.JUSTIFIED);
            paragraph.setSpacingRatio(1f);
        }
        return paragraph;
    }

    private Paragraph createUserInfoParagraph(PdfFont font) {
        return createStyledParagraph(font, MAIN_FONT_SIZE, TextAlignment.LEFT, (DeviceRgb) ColorConstants.BLACK, 1.5f);
    }

     private Paragraph createTableHeaderParagraph(PdfFont font) {
        return createStyledParagraph(font, TABLE_FONT_SIZE, TextAlignment.LEFT, (DeviceRgb) ColorConstants.BLACK, 0.1f);
    }

    private Paragraph createTableMainParagraph(PdfFont font) {
        return createStyledParagraph(font, TABLE_FONT_SIZE, TextAlignment.LEFT, (DeviceRgb) ColorConstants.BLACK, 0.1f);
    }

    private Paragraph createTableTotalParagraph(PdfFont font) {
        return createStyledParagraph(font, TABLE_FONT_SIZE, TextAlignment.LEFT, (DeviceRgb) ColorConstants.BLACK, 1.5f);
    }

    private Cell createUserInfoCell(PdfFont font, String text) {
        return createStyledTableCell(createUserInfoParagraph(font), text, 5f, TextAlignment.LEFT, VerticalAlignment.MIDDLE, (DeviceRgb) ColorConstants.WHITE, 1f)
                .setBackgroundColor(DEFAULT_HEADER_COLOR);
    }

    private Cell createTableHeaderCell(PdfFont font, String text) {
        return createStyledTableCell(createTableHeaderParagraph(font), text, 5f, TextAlignment.CENTER, VerticalAlignment.MIDDLE, (DeviceRgb) ColorConstants.BLACK, 0.7f)
                .setBackgroundColor(DEFAULT_HEADER_COLOR)
                .setBold();
    }

    private Cell createTableMainCell(PdfFont font, String text) {
        return createStyledTableCell(createTableMainParagraph(font), text, 3f, TextAlignment.LEFT, VerticalAlignment.MIDDLE, (DeviceRgb) ColorConstants.BLACK, 0.7f);
    }

    private Cell createTableTotalCell(PdfFont font, String text) {
        return createStyledTableCell(createTableTotalParagraph(font), text, 5f, TextAlignment.LEFT, VerticalAlignment.MIDDLE, (DeviceRgb) ColorConstants.BLACK, 0.7f)
                .setBackgroundColor(DEFAULT_HEADER_COLOR);
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
