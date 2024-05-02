package ru.yurch;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.regex.Pattern;

public class HoursMeter {

    final private Output out;

    final private Scanner sc = new Scanner(System.in);

    final private Store store;

    final private ReportGenerator report;

    public HoursMeter(Output out, Store store, ReportGenerator report) {
        this.out = out;
        this.store = store;
        this.report = report;
    }

    private void init() {

        boolean run = true;

        int userChoice;

        String dateRegex = "^\\d{4}[;\\-,]\\d{1,2}[;\\-,]\\d{1,2}$";

        String timeRegex = "^\\d{1,2}[:\\-]\\d{1,2}$";

        while (run) {

            System.out.println("""
        Меню:
        1 - Добавить временной интервал;
        2 - Вывести все временные интервалы за период;
        3 - Вывести отчет о дополнительно отработанных днях за период;
        4 - Удалить;
        5 - Выход;
        """);

            userChoice = Integer.parseInt(sc.nextLine());
            if (userChoice == 1) {
                Item item = new Item(
                        dateInput(sc, dateRegex, ""),
                        timeInput(sc, timeRegex, "начальное "),
                        timeInput(sc, timeRegex, "конечное "),
                        lunchBreakInput(sc)
                );
                store.add(item);
            } else if (userChoice == 2) {
                Collection<List<Item>> temp =
                        store.findByDate(
                        dateInput(sc, dateRegex, "начальную "),
                        dateInput(sc, dateRegex, "конечную ")
                ).values();
                temp.stream()
                        .flatMap(Collection::stream)
                        .forEach(System.out::println);
            } else if (userChoice == 3) {
                System.out.println(
        """
        -----------------------------------------
        1          -    Сформировать отчёт за текущий месяц;
        anyKey     -    Сформировать отчёт за произвольный период;
        -----------------------------------------
        """
                );
                String choice = sc.nextLine();
                if (choice.length() == 1 && choice.charAt(0) == '1') {
                    var currentYear = LocalDate.now().getYear();
                    var currentMonth = LocalDate.now().getMonth();
                    var currentLastDayOfMonth = LocalDate.now().lengthOfMonth();
                    out.print(report.save(store.findByDate(
                            LocalDate.of(currentYear, currentMonth, 1),
                            LocalDate.of(currentYear, currentMonth, currentLastDayOfMonth))));
                } else {
                    out.print(report.save(
                            store.findByDate(
                                    dateInput(sc, dateRegex, "начальную "),
                                    dateInput(sc, dateRegex, "конечную ")
                            )));
                }
            } else if (userChoice == 4) {
                store.deleteByDate(
                        dateInput(sc, dateRegex, "")
                );
            } else if (userChoice == 5) {
                run = false;
            }
        }
    }

    private LocalDate dateInput(Scanner sc, String regex, String parameter) {
        String menu = """
        -----------------------------------------
        1          -    Ввести текущую дату;
        YYYY-MM-dd -    Ввести произвольную дату;
        -----------------------------------------
        """;
        System.out.printf(
                "Введите %sдату:%s%s%s",
                parameter,
                System.lineSeparator(),
                menu,
                System.lineSeparator()
        );
        String choice = sc.nextLine();
        if (choice.length() == 1 && choice.charAt(0) == '1') {
            return LocalDate.now();
        } else {
            Pattern pattern = Pattern.compile(regex);
            while (!pattern.matcher(choice).find()) {
                System.out.println("Повторите ввод даты!");
                choice = sc.nextLine();
            }
            String[] arrDate = choice.split("[;\\-:,]");
            return LocalDate.of(
                    Integer.parseInt(arrDate[0]),
                    Integer.parseInt(arrDate[1]),
                    Integer.parseInt(arrDate[2])
            );
        }
    }

    private LocalTime timeInput(Scanner sc, String regex, String parameter) {
        System.out.printf("Введите %sвремя:%s", parameter, System.lineSeparator());
        Pattern pattern = Pattern.compile(regex);
        String time = sc.nextLine();
        while (!pattern.matcher(time).find()) {
            System.out.println("Повторите ввод времени!");
            time = sc.nextLine();
        }
        String[] arrDate = time.split("[-:]");
        return LocalTime.of(
                Integer.parseInt(arrDate[0]),
                Integer.parseInt(arrDate[1]),
                0
        );
    }

    private boolean lunchBreakInput(Scanner sc) {
        System.out.println("Работа с обеденным перерывом?");
        System.out.println("1 - да, 2 - нет");
        return Integer.parseInt(sc.nextLine()) == 1;
    }

    public static void main(String[] args) {
        try (SqlStore sqlStore = new SqlStore()) {
            sqlStore.init();
            HoursMeter hm = new HoursMeter(
                    new FileOutput("C:\\projects\\hours_meter\\data\\target.csv"),
//                    new ConsoleOutput(),
                    sqlStore,
                    new CsvReportGenerator());

            hm.init();
        } catch (Exception e) {
        e.printStackTrace();
        }
    }
}
