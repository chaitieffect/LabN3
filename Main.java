import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TaskCalendar calendar = new TaskCalendar();

        while (true) {
            printMenu();
            System.out.print("Выберите действие: ");
            String choice = scanner.nextLine().trim();

            if (choice.equals("0")) {
                System.out.println("Работа календаря завершена.");
                break;
            } else if (choice.equals("1")) {
                printTasks(calendar.getTasks());
            } else if (choice.equals("2")) {
                int startTime = readTime(scanner, "Время начала (чч:мм): ");
                int duration = readInteger(scanner, "Продолжительность в минутах: ");
                System.out.print("Название задачи: ");
                String title = scanner.nextLine();

                if (startTime == -1 || !calendar.addTask(startTime, duration, title)) {
                    System.out.println("Задачу добавить не удалось. Проверьте время, длительность и пересечения.");
                } else {
                    System.out.println("Задача добавлена.");
                }
            } else if (choice.equals("3")) {
                Task task = calendar.getLongestTask();
                if (task == null) {
                    System.out.println("Календарь пуст.");
                } else {
                    System.out.println("Самая длинная задача: " + task);
                }
            } else if (choice.equals("4")) {
                int[] period = readPeriod(scanner);
                if (period == null) {
                    System.out.println("Некорректный временной промежуток.");
                } else {
                    printTasks(calendar.getTasksInPeriod(period[0], period[1]));
                }
            } else if (choice.equals("5")) {
                int time = readTime(scanner, "Укажите время (чч:мм): ");
                if (time == -1) {
                    System.out.println("Некорректное время.");
                } else {
                    printTasks(calendar.getCompletedBefore(time));
                }
            } else if (choice.equals("6")) {
                int number = readInteger(scanner, "Номер задачи: ");
                if (calendar.removeTask(number)) {
                    System.out.println("Задача удалена.");
                } else {
                    System.out.println("Задачи с таким номером нет.");
                }
            } else if (choice.equals("7")) {
                int[] period = readPeriod(scanner);
                if (period == null) {
                    System.out.println("Некорректный временной промежуток.");
                } else {
                    int removed = calendar.removeStartingInPeriod(period[0], period[1]);
                    System.out.println("Удалено задач: " + removed);
                }
            } else if (choice.equals("8")) {
                int[] period = readPeriod(scanner);
                if (period == null) {
                    System.out.println("Некорректный временной промежуток.");
                } else {
                    int affected = calendar.removeIntersectingPeriod(period[0], period[1]);
                    System.out.println("Изменено или удалено задач: " + affected);
                }
            } else if (choice.equals("9")) {
                int removed = calendar.removeShortestTasks();
                System.out.println("Удалено самых коротких задач: " + removed);
            } else if (choice.equals("10")) {
                int duration = readInteger(scanner, "Продолжительность в минутах: ");
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

    private static void printTasks(ArrayList<Task> tasks) {
        if (tasks.isEmpty()) {
            System.out.println("Подходящих задач нет.");
            return;
        }

        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + " - " + tasks.get(i));
        }
    }

    private static int readTime(Scanner scanner, String message) {
        System.out.print(message);
        String value = scanner.nextLine().trim();
        String[] parts = value.split(":");

        if (parts.length != 2) {
            return -1;
        }

        try {
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);

            if (hours < 0 || hours > 23 || minutes < 0 || minutes > 59) {
                return -1;
            }

            int time = hours * 60 + minutes;
            if (time < 9 * 60 || time > 21 * 60) {
                return -1;
            }
            return time;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static int readInteger(Scanner scanner, String message) {
        System.out.print(message);
        String value = scanner.nextLine().trim();
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static int[] readPeriod(Scanner scanner) {
        int start = readTime(scanner, "Начало промежутка (чч:мм): ");
        int end = readTime(scanner, "Конец промежутка (чч:мм): ");

        if (start == -1 || end == -1 || start >= end) {
            return null;
        }
        return new int[] { start, end };
    }
}
