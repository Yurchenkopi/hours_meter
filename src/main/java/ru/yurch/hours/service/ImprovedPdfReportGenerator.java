package ru.yurch.hours.service;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.renderer.ParagraphRenderer;
import com.itextpdf.layout.renderer.IRenderer;
import org.apache.commons.io.IOUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Arrays;

public class ImprovedPdfReportGenerator {
    // Константы для настройки форматирования
    private static final float MAIN_FONT_SIZE = 12f;           // Основной размер шрифта
    private static final float HEADER_FONT_SIZE = 16f;         // Размер шрифта заголовков
    private static final float TITLE_FONT_SIZE = 20f;          // Размер шрифта главного заголовка
    private static final float CHARACTER_SPACING = 0.5f;       // Расстояние между символами
    private static final float WORD_SPACING = 2f;              // Расстояние между словами
    private static final float LINE_SPACING = 1.2f;            // Межстрочный интервал
    private static final float PARAGRAPH_SPACING = 10f;        // Отступы между абзацами

    /**
     * Главный метод для создания улучшенного PDF отчета
     * @param filename имя выходного файла
     * @throws FileNotFoundException если файл не может быть создан
     * @throws IOException при ошибках ввода-вывода
     */
    public static void createImprovedPDFReport(String filename) throws FileNotFoundException, IOException {

        // Инициализация PDF документа с улучшенными настройками
        PdfWriter writer = new PdfWriter(filename);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Установка шрифтов для поддержки кириллицы и улучшенной читаемости
        InputStream fontStream = ImprovedPdfReportGenerator.class.getResourceAsStream("/static/fonts/TimesNewRoman/timesnewromanpsmt.ttf");
        PdfFont mainFont = PdfFontFactory.createFont(IOUtils.toByteArray(fontStream), PdfEncodings.IDENTITY_H, true);
        PdfFont boldFont = mainFont;

        // Настройка глобальных отступов документа
        document.setMargins(50, 50, 50, 50);

        // Создание главного заголовка с улучшенным форматированием
        Paragraph title = createStyledParagraph(
                "ОТЧЕТ О ПРОДАЖАХ ЗА КВАРТАЛ",
                boldFont,
                TITLE_FONT_SIZE,
                TextAlignment.CENTER
        );
        title.setMarginBottom(PARAGRAPH_SPACING * 2); // Увеличенный отступ после заголовка
        document.add(title);

        // Создание подзаголовка с улучшенным форматированием
        Paragraph subtitle = createStyledParagraph(
                "Детальный анализ результатов продаж",
                mainFont,
                HEADER_FONT_SIZE,
                TextAlignment.CENTER
        );
        subtitle.setMarginBottom(PARAGRAPH_SPACING);
        document.add(subtitle);

        // Добавление описательного текста с правильными настройками
        Paragraph description = createStyledParagraph(
                "Данный отчет содержит подробную информацию о результатах продаж " +
                        "за текущий квартал. В отчете представлены ключевые показатели " +
                        "эффективности, анализ трендов и рекомендации для улучшения результатов.",
                mainFont,
                MAIN_FONT_SIZE,
                TextAlignment.JUSTIFIED
        );
        description.setMarginBottom(PARAGRAPH_SPACING);
        document.add(description);

        // Создание заголовка для таблицы
        Paragraph tableHeader = createStyledParagraph(
                "ДЕТАЛИЗАЦИЯ ПРОДАЖ ПО ТОВАРАМ",
                boldFont,
                HEADER_FONT_SIZE,
                TextAlignment.LEFT
        );
        tableHeader.setMarginTop(PARAGRAPH_SPACING);
        tableHeader.setMarginBottom(PARAGRAPH_SPACING / 2);
        document.add(tableHeader);

        // Создание улучшенной таблицы с данными
        createImprovedTable(document, mainFont, boldFont);

        // Добавление заключительного раздела
        Paragraph conclusion = createStyledParagraph(
                "ЗАКЛЮЧЕНИЕ",
                boldFont,
                HEADER_FONT_SIZE,
                TextAlignment.LEFT
        );
        conclusion.setMarginTop(PARAGRAPH_SPACING * 2);
        conclusion.setMarginBottom(PARAGRAPH_SPACING / 2);
        document.add(conclusion);

        // Текст заключения с правильным форматированием
        Paragraph conclusionText = createStyledParagraph(
                "По результатам анализа видно устойчивое развитие продаж. " +
                        "Рекомендуется продолжить текущую стратегию с фокусом на " +
                        "наиболее успешные товарные позиции. Особое внимание следует " +
                        "уделить товарам с высоким потенциалом роста.",
                mainFont,
                MAIN_FONT_SIZE,
                TextAlignment.JUSTIFIED
        );
        document.add(conclusionText);

        // Закрытие документа
        document.close();
        System.out.println("PDF отчет успешно создан: " + filename);
    }

    /**
     * Создание параграфа с улучшенными настройками форматирования
     * @param text текст параграфа
     * @param font используемый шрифт
     * @param fontSize размер шрифта
     * @param alignment выравнивание текста
     * @return настроенный параграф
     */
    private static Paragraph createStyledParagraph(String text, PdfFont font,
                                                   float fontSize, TextAlignment alignment) {
        Paragraph paragraph = new Paragraph(text);

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

    /**
     * Создание улучшенной таблицы с правильным форматированием
     * @param document документ для добавления таблицы
     * @param mainFont основной шрифт
     * @param boldFont жирный шрифт для заголовков
     */
    private static void createImprovedTable(Document document, PdfFont mainFont, PdfFont boldFont) {

        // Данные для таблицы - пример товаров и продаж
        String[][] tableData = {
                {"№", "Наименование товара", "Количество", "Цена за единицу", "Общая сумма"},
                {"1", "Ноутбук ASUS VivoBook", "25", "45 000 ₽", "1 125 000 ₽"},
                {"2", "Смартфон Samsung Galaxy", "40", "25 000 ₽", "1 000 000 ₽"},
                {"3", "Планшет iPad Air", "15", "55 000 ₽", "825 000 ₽"},
                {"4", "Наушники Sony WH-1000XM4", "60", "15 000 ₽", "900 000 ₽"},
                {"5", "Клавиатура Logitech MX Keys", "35", "8 000 ₽", "280 000 ₽"}
        };

        // Создание таблицы с настроенными колонками
        float[] columnWidths = {8f, 35f, 15f, 20f, 22f}; // Пропорциональные ширины колонок
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        // Добавление строк в таблицу
        for (int i = 0; i < tableData.length; i++) {
            for (int j = 0; j < tableData[i].length; j++) {
                Cell cell;

                if (i == 0) {
                    // Создание ячейки заголовка с улучшенным стилем
                    cell = createHeaderCell(tableData[i][j], boldFont);
                } else {
                    // Создание обычной ячейки с улучшенным форматированием
                    cell = createDataCell(tableData[i][j], mainFont);
                }

                table.addCell(cell);
            }
        }

        // Добавление итоговой строки с выделением
        addTotalRow(table, boldFont, mainFont);

        // Настройка отступов таблицы
        table.setMarginBottom(PARAGRAPH_SPACING);

        document.add(table);
    }

    /**
     * Создание ячейки заголовка таблицы с улучшенным форматированием
     * @param text текст ячейки
     * @param font шрифт
     * @return настроенная ячейка заголовка
     */
    private static Cell createHeaderCell(String text, PdfFont font) {
        Paragraph cellParagraph = new Paragraph(text);
        cellParagraph.setFont(font);
        cellParagraph.setFontSize(MAIN_FONT_SIZE);
        cellParagraph.setCharacterSpacing(CHARACTER_SPACING);
        cellParagraph.setTextAlignment(TextAlignment.CENTER);
        cellParagraph.setFontColor(ColorConstants.WHITE);

        Cell cell = new Cell();
        cell.add(cellParagraph);
        cell.setBackgroundColor(ColorConstants.DARK_GRAY);
        cell.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
        cell.setPadding(8f); // Увеличенные внутренние отступы

        return cell;
    }

    /**
     * Создание ячейки данных с улучшенным форматированием
     * @param text текст ячейки
     * @param font шрифт
     * @return настроенная ячейка данных
     */
    private static Cell createDataCell(String text, PdfFont font) {
        Paragraph cellParagraph = new Paragraph(text);
        cellParagraph.setFont(font);
        cellParagraph.setFontSize(MAIN_FONT_SIZE);
        cellParagraph.setCharacterSpacing(CHARACTER_SPACING);
        cellParagraph.setWordSpacing(WORD_SPACING);

        // Выравнивание числовых данных по правому краю
        if (text.matches(".*\\d.*₽.*") || text.matches("^\\d+$")) {
            cellParagraph.setTextAlignment(TextAlignment.RIGHT);
        } else {
            cellParagraph.setTextAlignment(TextAlignment.LEFT);
        }

        Cell cell = new Cell();
        cell.add(cellParagraph);
        cell.setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f));
        cell.setPadding(6f); // Комфортные внутренние отступы

        return cell;
    }

    /**
     * Добавление итоговой строки в таблицу
     * @param table таблица
     * @param boldFont жирный шрифт
     * @param mainFont основной шрифт
     */
    private static void addTotalRow(Table table, PdfFont boldFont, PdfFont mainFont) {
        // Пустые ячейки для выравнивания
        for (int i = 0; i < 3; i++) {
            Cell emptyCell = new Cell();
            emptyCell.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
            emptyCell.setPadding(6f);
            table.addCell(emptyCell);
        }

        // Ячейка "ИТОГО"
        Paragraph totalLabel = new Paragraph("ИТОГО:");
        totalLabel.setFont(boldFont);
        totalLabel.setFontSize(MAIN_FONT_SIZE);
        totalLabel.setCharacterSpacing(CHARACTER_SPACING);
        totalLabel.setTextAlignment(TextAlignment.RIGHT);

        Cell totalLabelCell = new Cell();
        totalLabelCell.add(totalLabel);
        totalLabelCell.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
        totalLabelCell.setPadding(6f);
        totalLabelCell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
        table.addCell(totalLabelCell);

        // Ячейка с суммой
        Paragraph totalAmount = new Paragraph("4 130 000 ₽");
        totalAmount.setFont(boldFont);
        totalAmount.setFontSize(MAIN_FONT_SIZE);
        totalAmount.setCharacterSpacing(CHARACTER_SPACING);
        totalAmount.setTextAlignment(TextAlignment.RIGHT);
        totalAmount.setFontColor(ColorConstants.RED);

        Cell totalAmountCell = new Cell();
        totalAmountCell.add(totalAmount);
        totalAmountCell.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
        totalAmountCell.setPadding(6f);
        totalAmountCell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
        table.addCell(totalAmountCell);
    }

    /**
     * Главный метод для тестирования функциональности
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        try {
            createImprovedPDFReport("C:\\projects\\hours_meter\\src\\main\\java\\ru\\yurch\\hours\\aa.pdf");
        } catch (Exception e) {
            System.err.println("Ошибка при создании PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

