import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    // Один Scanner используется во всех методах этого класса.
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        TaskCalendar calendar = new TaskCalendar();
        boolean running = true;

        // Меню повторяется, пока пользователь не выберет пункт 0.
        while (running) {
            printMenu();

            System.out.print("Выберите действие: ");
            int choice = readInteger();

            if (choice == 0) {
                System.out.println("Работа календаря завершена.");
                running = false;
            } else if (choice == 1) {
                printTasks(calendar.getTasks());

            } else if (choice == 2) {
                System.out.print("Время начала (чч:мм): ");
                int startTime = readTime();

                System.out.print("Продолжительность в минутах: ");
                int duration = readInteger();

                System.out.print("Название задачи: ");
                String title = scanner.nextLine();

                if (startTime == -1 || !calendar.addTask(startTime, duration, title)) {
                    System.out.println("Задачу добавить не удалось. Проверьте время, длительность и пересечения.");
                } else {
                    System.out.println("Задача добавлена.");
                }

            } else if (choice == 3) {
                Task task = calendar.getLongestTask();

                if (task == null) {
                    System.out.println("Календарь пуст.");
                } else {
                    System.out.println("Самая длинная задача: " + task);
                }

            } else if (choice == 4) {
                int[] period = readPeriod();

                if (period == null) {
                    System.out.println("Некорректный временной промежуток.");
                } else {
                    printTasks(calendar.getTasksInPeriod(period[0], period[1]));
                }

            } else if (choice == 5) {
                System.out.print("Укажите время (чч:мм): ");
                int time = readTime();

                if (time == -1) {
                    System.out.println("Некорректное время.");
                } else {
                    printTasks(calendar.getCompletedBefore(time));
                }

            } else if (choice == 6) {
                System.out.print("Номер задачи: ");
                int number = readInteger();

                if (calendar.removeTask(number)) {
                    System.out.println("Задача удалена.");
                } else {
                    System.out.println("Задачи с таким номером нет.");
                }

            } else if (choice == 7) {
                int[] period = readPeriod();

                if (period == null) {
                    System.out.println("Некорректный временной промежуток.");
                } else {
                    int removed = calendar.removeStartingInPeriod(period[0], period[1]);
                    System.out.println("Удалено задач: " + removed);
                }

            } else if (choice == 8) {
                int[] period = readPeriod();

                if (period == null) {
                    System.out.println("Некорректный временной промежуток.");
                } else {
                    int affected = calendar.removeIntersectingPeriod(period[0], period[1]);
                    System.out.println("Изменено или удалено задач: " + affected);
                }

            } else if (choice == 9) {
                int removed = calendar.removeShortestTasks();
                System.out.println("Удалено самых коротких задач: " + removed);

            } else if (choice == 10) {
                System.out.print("Продолжительность в минутах: ");
                int duration = readInteger();

                System.out.print("Название задачи: ");
                String title = scanner.nextLine();

                if (calendar.addInFirstFreeSlot(duration, title)) {
                    System.out.println("Задача добавлена в первый свободный промежуток.");
                } else {
                    System.out.println("Подходящего промежутка нет или введены некорректные данные.");
                }

            } else {
                System.out.println("Такого пункта меню нет.");
            }

            System.out.println();
        }

        scanner.close();
    }

    // Выводит все команды календаря.
    private static void printMenu() {
        System.out.println("КАЛЕНДАРЬ ЗАДАЧ (рабочее время 09:00–21:00)");
        System.out.println("1. Вывести список задач");
        System.out.println("2. Добавить задачу");
        System.out.println("3. Найти самую длинную задачу");
        System.out.println("4. Найти задачи на промежутке времени");
        System.out.println("5. Найти задачи, завершенные к указанному времени");
        System.out.println("6. Удалить задачу по номеру");
        System.out.println("7. Удалить задачи, начинающиеся на промежутке");
        System.out.println("8. Удалить задачи, пересекающие промежуток (с обрезкой)");
        System.out.println("9. Удалить все самые короткие задачи");
        System.out.println("10. Добавить задачу в первый свободный промежуток");
        System.out.println("0. Выход");
    }

    // Выводит список задач с номерами, начиная с 1.
    private static void printTasks(ArrayList<Task> tasks) {
        if (tasks.isEmpty()) {
            System.out.println("Подходящих задач нет.");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println((i + 1) + " - " + tasks.get(i));
            }
        }
    }

    // Считывает время в формате чч:мм и переводит его в минуты.
    // Возвращает -1, если время введено неправильно или не входит в рабочий день.
    private static int readTime() {
        String value = scanner.nextLine().trim();

        // Для формата чч:мм должно быть ровно 5 символов.
        if (value.length() != 5 || value.charAt(2) != ':') {
            return -1;
        }

        // Проверяем, что все символы, кроме двоеточия, являются цифрами.
        if (value.charAt(0) < '0' || value.charAt(0) > '9'
                || value.charAt(1) < '0' || value.charAt(1) > '9'
                || value.charAt(3) < '0' || value.charAt(3) > '9'
                || value.charAt(4) < '0' || value.charAt(4) > '9') {
            return -1;
        }

        // Получаем часы и минуты из отдельных цифр.
        int hours = (value.charAt(0) - '0') * 10 + (value.charAt(1) - '0');
        int minutes = (value.charAt(3) - '0') * 10 + (value.charAt(4) - '0');

        if (hours > 23 || minutes > 59) {
            return -1;
        }

        // Переводим время в количество минут от начала суток.
        int time = hours * 60 + minutes;

        if (time < 9 * 60 || time > 21 * 60) {
            return -1;
        }

        return time;
    }

    // Считывает целое положительное число без использования parseInt.
    // Возвращает -1, если введены не цифры.
    private static int readInteger() {
        String value = scanner.nextLine().trim();

        if (value.length() == 0) {
            return -1;
        }

        int number = 0;

        for (int i = 0; i < value.length(); i++) {
            char symbol = value.charAt(i);

            if (symbol < '0' || symbol > '9') {
                return -1;
            }

            number = number * 10 + (symbol - '0');
        }

        return number;
    }

    // Считывает начало и конец временного промежутка.
    private static int[] readPeriod() {
        System.out.print("Начало промежутка (чч:мм): ");
        int start = readTime();

        System.out.print("Конец промежутка (чч:мм): ");
        int end = readTime();

        if (start == -1 || end == -1 || start >= end) {
            return null;
        }

        return new int[] { start, end };
    }
}
