import java.util.ArrayList;
import java.util.Scanner;
import java.io.PrintStream;

public class Main {
    // in нужен для чтения данных с клавиатуры.
    // out нужен для вывода текста на экран.
    // Эти переменные доступны во всех методах класса Main.
    private static Scanner in = new Scanner(System.in);
    private static PrintStream out = System.out;

    public static void main(String[] args) {
        TaskCalendar calendar = new TaskCalendar();
        boolean running = true;

        // Меню повторяется, пока running равно true.
        while (running) {
            printMenu();

            out.print("Выберите действие: ");
            int choice = readInteger();

            if (choice == 0) {
                out.println("Работа календаря завершена.");
                running = false;

            } else if (choice == 1) {
                printTasks(calendar.getTasks());

            } else if (choice == 2) {
                out.print("Время начала (чч:мм): ");
                int startTime = readTime();

                out.print("Продолжительность в минутах: ");
                int duration = readInteger();

                out.print("Название задачи: ");
                String title = in.nextLine();

                if (startTime == -1 || !calendar.addTask(startTime, duration, title)) {
                    out.println("Задачу добавить не удалось. Проверьте время, длительность и пересечения.");
                } else {
                    out.println("Задача добавлена.");
                }

            } else if (choice == 3) {
                Task task = calendar.getLongestTask();

                if (task == null) {
                    out.println("Календарь пуст.");
                } else {
                    out.println("Самая длинная задача: " + task);
                }

            } else if (choice == 4) {
                int[] period = readPeriod();

                if (period == null) {
                    out.println("Некорректный временной промежуток.");
                } else {
                    printTasks(calendar.getTasksInPeriod(period[0], period[1]));
                }

            } else if (choice == 5) {
                out.print("Укажите время (чч:мм): ");
                int time = readTime();

                if (time == -1) {
                    out.println("Некорректное время.");
                } else {
                    printTasks(calendar.getCompletedBefore(time));
                }

            } else if (choice == 6) {
                out.print("Номер задачи: ");
                int number = readInteger();

                if (calendar.removeTask(number)) {
                    out.println("Задача удалена.");
                } else {
                    out.println("Задачи с таким номером нет.");
                }

            } else if (choice == 7) {
                int[] period = readPeriod();

                if (period == null) {
                    out.println("Некорректный временной промежуток.");
                } else {
                    int removed = calendar.removeStartingInPeriod(period[0], period[1]);
                    out.println("Удалено задач: " + removed);
                }

            } else if (choice == 8) {
                int[] period = readPeriod();

                if (period == null) {
                    out.println("Некорректный временной промежуток.");
                } else {
                    int affected = calendar.removeIntersectingPeriod(period[0], period[1]);
                    out.println("Изменено или удалено задач: " + affected);
                }

            } else if (choice == 9) {
                int removed = calendar.removeShortestTasks();
                out.println("Удалено самых коротких задач: " + removed);

            } else if (choice == 10) {
                out.print("Продолжительность в минутах: ");
                int duration = readInteger();

                out.print("Название задачи: ");
                String title = in.nextLine();

                if (calendar.addInFirstFreeSlot(duration, title)) {
                    out.println("Задача добавлена в первый свободный промежуток.");
                } else {
                    out.println("Подходящего промежутка нет или введены некорректные данные.");
                }

            } else {
                out.println("Такого пункта меню нет.");
            }

            out.println();
        }

        in.close();
    }

    // Показывает список действий.
    private static void printMenu() {
        out.println("КАЛЕНДАРЬ ЗАДАЧ (рабочее время 09:00–21:00)");
        out.println("1. Вывести список задач");
        out.println("2. Добавить задачу");
        out.println("3. Найти самую длинную задачу");
        out.println("4. Найти задачи на промежутке времени");
        out.println("5. Найти задачи, завершенные к указанному времени");
        out.println("6. Удалить задачу по номеру");
        out.println("7. Удалить задачи, начинающиеся на промежутке");
        out.println("8. Удалить задачи, пересекающие промежуток (с обрезкой)");
        out.println("9. Удалить все самые короткие задачи");
        out.println("10. Добавить задачу в первый свободный промежуток");
        out.println("0. Выход");
    }

    // Получает список задач и выводит его на экран.
    private static void printTasks(ArrayList<Task> tasks) {
        if (tasks.isEmpty()) {
            out.println("Подходящих задач нет.");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                out.println((i + 1) + " - " + tasks.get(i));
            }
        }
    }

    // Читает время в формате чч:мм.
    // Возвращает количество минут или -1, если время неправильное.
    private static int readTime() {
        String value = in.nextLine();

        // В записи времени должно быть 5 символов, например 09:30.
        if (value.length() != 5 || value.charAt(2) != ':') {
            return -1;
        }

        // Проверяем, что на местах часов и минут стоят цифры.
        if (value.charAt(0) < '0' || value.charAt(0) > '9'
                || value.charAt(1) < '0' || value.charAt(1) > '9'
                || value.charAt(3) < '0' || value.charAt(3) > '9'
                || value.charAt(4) < '0' || value.charAt(4) > '9') {
            return -1;
        }

        // Собираем число часов из двух цифр.
        int hours = (value.charAt(0) - '0') * 10 + (value.charAt(1) - '0');

        // Собираем число минут из двух цифр.
        int minutes = (value.charAt(3) - '0') * 10 + (value.charAt(4) - '0');

        if (hours > 23 || minutes > 59) {
            return -1;
        }

        // Переводим часы и минуты в общее количество минут.
        int time = hours * 60 + minutes;

        // Проверяем рабочее время календаря: с 09:00 до 21:00.
        if (time < 540 || time > 1260) {
            return -1;
        }

        return time;
    }

    // Читает целое число. Каждую цифру добавляем к предыдущему результату.
    // Например, для строки "125": сначала получаем 1, потом 12, потом 125.
    private static int readInteger() {
        String value = in.nextLine();

        if (value.length() == 0) {
            return -1;
        }

        int number = 0;

        for (int i = 0; i < value.length(); i++) {
            char symbol = value.charAt(i);

            // Если встретился не символ от 0 до 9, ввод неправильный.
            if (symbol < '0' || symbol > '9') {
                return -1;
            }

            int digit = symbol - '0';
            number = number * 10 + digit;
        }

        return number;
    }

    // Читает начало и конец промежутка и возвращает их в одном массиве.
    private static int[] readPeriod() {
        out.print("Начало промежутка (чч:мм): ");
        int start = readTime();

        out.print("Конец промежутка (чч:мм): ");
        int end = readTime();

        // Начало должно быть раньше конца, а оба значения должны быть корректными.
        if (start == -1 || end == -1 || start >= end) {
            return null;
        }

        // В нулевой ячейке начало, в первой ячейке конец.
        return new int[] { start, end };
    }
}
