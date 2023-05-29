package ru.yurch;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;
import java.util.StringJoiner;
import java.util.regex.Pattern;

public class HoursMeter {

    private Output out;

    private Scanner sc = new Scanner(System.in);

    private Store store;

    private ReportGenerator report;

    public HoursMeter(Output out, Store store, ReportGenerator report) {
        this.out = out;
        this.store = store;
        this.report = report;
    }

    public Store getStore() {
        return store;
    }

    private void init() {
        boolean run = true;
        int userChoice;
        final String dateRegex = "^\\d{4}[;\\-,]\\d{1,2}[;\\-,]\\d{1,2}$";
        final String timeRegex = "^\\d{1,2}[:\\-]\\d{1,2}$";
        while (run) {
            System.out.println("""
        Меню:
        1 - Добавить временной интервал;
        2 - Вывести все временные интервалы за месяц;
        3 - Выход;
        """);
            userChoice = Integer.parseInt(sc.nextLine());
            if (userChoice == 1) {
                Item item = new Item(
                        dateInput(sc, dateRegex),
                        timeInput(sc, timeRegex, "начальное"),
                        timeInput(sc, timeRegex, "конечное")
                );
                store.add(item);
            } else if (userChoice == 2) {
                StringJoiner sj = new StringJoiner(System.lineSeparator());
                store.findByDate(
                        dateInput(sc, dateRegex),
                        dateInput(sc, dateRegex)
                ).forEach(i -> sj.add(i.toString()));
                out.print(sj);
            } else if (userChoice == 3) {
                run = false;
            }
        }
    }

    private LocalDate dateInput(Scanner sc, String regex) {
        System.out.println("Введите дату:");
        Pattern pattern = Pattern.compile(regex);
        String date = sc.nextLine();
        while (!pattern.matcher(date).find()) {
            System.out.println("Повторите ввод даты!");
            date = sc.nextLine();
        }
        String[] arrDate = date.split(";|-|:|,");
        return LocalDate.of(
                Integer.parseInt(arrDate[0]),
                Integer.parseInt(arrDate[1]),
                Integer.parseInt(arrDate[2])
        );
    }

    private LocalTime timeInput(Scanner sc, String regex, String parameter) {
        System.out.printf("Введите %s время:%s", parameter, System.lineSeparator());
        Pattern pattern = Pattern.compile(regex);
        String time = sc.nextLine();
        while (!pattern.matcher(time).find()) {
            System.out.println("Повторите ввод времени!");
            time = sc.nextLine();
        }
        String[] arrDate = time.split("-|:");
        return LocalTime.of(
                Integer.parseInt(arrDate[0]),
                Integer.parseInt(arrDate[1]),
                0
        );
    }
    public static void main(String[] args) {
        try (SqlStore sqlStore = new SqlStore()) {
            sqlStore.init();
            HoursMeter hm = new HoursMeter(
                    new FileOutput("C:\\projects\\hours_meter\\data\\target.txt"),
                    sqlStore,
                    new SimpleHoursCalculator());

            hm.init();
        } catch (Exception e) {
        e.printStackTrace();
    }
    }
}
