package ru.yurch;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;
import java.util.StringJoiner;
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
        3 - Вывести отчет о дополнительно отработанных днях;
        4 - Выход;
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
                StringJoiner sj = new StringJoiner(System.lineSeparator());
                List<Item> temp =
                        store.findByDate(
                        dateInput(sc, dateRegex, "начальную "),
                        dateInput(sc, dateRegex, "конечную ")
                );
                temp.forEach(i -> sj.add(i.toString()));
                out.print(sj.add(""));
            } else if (userChoice == 3) {
                out.print(report.save(
                        store.findByDate(
                        dateInput(sc, dateRegex, "начальную "),
                        dateInput(sc, dateRegex, "конечную ")
                )));
            } else if (userChoice == 4) {
                run = false;
            }
        }
    }

    private LocalDate dateInput(Scanner sc, String regex, String parameter) {
        System.out.printf("Введите %sдату:%s", parameter, System.lineSeparator());
        Pattern pattern = Pattern.compile(regex);
        String date = sc.nextLine();
        while (!pattern.matcher(date).find()) {
            System.out.println("Повторите ввод даты!");
            date = sc.nextLine();
        }
        String[] arrDate = date.split("[;\\-:,]");
        return LocalDate.of(
                Integer.parseInt(arrDate[0]),
                Integer.parseInt(arrDate[1]),
                Integer.parseInt(arrDate[2])
        );
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
 //                   new ConsoleOutput(),
                    sqlStore,
                    new CsvReportGenerator());

            hm.init();
        } catch (Exception e) {
        e.printStackTrace();
        }
    }
}
