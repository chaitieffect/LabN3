import java.util.ArrayList;

public class CalendarTests {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        testAddingAndSorting();
        testLongestTask();
        testTasksInPeriod();
        testCompletedTasks();
        testRemoveByNumber();
        testRemoveStartingInPeriod();
        testRemoveIntersectingPeriod();
        testRemoveShortestTasks();
        testFirstFreeSlot();

        System.out.println();
        System.out.println("Успешно: " + passed);
        System.out.println("Ошибок: " + failed);
        if (failed > 0) {
            throw new RuntimeException("Не все тесты пройдены.");
        }
    }

    private static void testAddingAndSorting() {
        TaskCalendar calendar = new TaskCalendar();
        boolean first = calendar.addTask(12 * 60, 30, "Встреча");
        boolean second = calendar.addTask(9 * 60, 60, "Учеба");
        boolean overlap = calendar.addTask(9 * 60 + 30, 30, "Пересечение");
        boolean outside = calendar.addTask(20 * 60 + 30, 60, "За пределами дня");

        ArrayList<Task> tasks = calendar.getTasks();
        check("Добавление и сортировка задач", first && second && tasks.size() == 2
                && tasks.get(0).getStartTime() == 9 * 60);
        check("Запрет пересечений и выхода за рабочее время", !overlap && !outside);
    }

    private static void testLongestTask() {
        TaskCalendar calendar = new TaskCalendar();
        calendar.addTask(9 * 60, 30, "Короткая");
        calendar.addTask(10 * 60, 90, "Первая длинная");
        calendar.addTask(12 * 60, 90, "Вторая длинная");

        Task longest = calendar.getLongestTask();
        check("Поиск первой самой длинной задачи", longest != null
                && longest.getTitle().equals("Первая длинная"));
    }

    private static void testTasksInPeriod() {
        TaskCalendar calendar = new TaskCalendar();
        calendar.addTask(9 * 60, 60, "Заканчивается внутри промежутка");
        calendar.addTask(11 * 60, 60, "Начинается внутри промежутка");
        calendar.addTask(13 * 60, 60, "Не пересекается");

        ArrayList<Task> found = calendar.getTasksInPeriod(9 * 60 + 30, 11 * 60 + 30);
        check("Поиск пересекающихся задач", found.size() == 2);

        TaskCalendar containingCalendar = new TaskCalendar();
        containingCalendar.addTask(10 * 60, 120, "Полностью покрывает промежуток");
        check("Поиск задачи, которая покрывает весь промежуток",
                containingCalendar.getTasksInPeriod(10 * 60 + 30, 11 * 60).size() == 1);
    }

    private static void testCompletedTasks() {
        TaskCalendar calendar = new TaskCalendar();
        calendar.addTask(9 * 60, 60, "Первая");
        calendar.addTask(10 * 60, 60, "Вторая");
        calendar.addTask(11 * 60, 60, "Третья");

        ArrayList<Task> found = calendar.getCompletedBefore(11 * 60);
        check("Поиск завершенных к указанному времени задач", found.size() == 2);
    }

    private static void testRemoveByNumber() {
        TaskCalendar calendar = new TaskCalendar();
        calendar.addTask(9 * 60, 60, "Первая");
        calendar.addTask(11 * 60, 60, "Вторая");

        boolean removed = calendar.removeTask(2);
        check("Удаление задачи по номеру", removed && calendar.getTasks().size() == 1
                && calendar.getTasks().get(0).getTitle().equals("Первая"));
        check("Проверка неверного номера", !calendar.removeTask(5));
    }

    private static void testRemoveStartingInPeriod() {
        TaskCalendar calendar = new TaskCalendar();
        calendar.addTask(9 * 60, 30, "До промежутка");
        calendar.addTask(10 * 60, 30, "На начале промежутка");
        calendar.addTask(12 * 60, 30, "На конце промежутка");

        int removed = calendar.removeStartingInPeriod(10 * 60, 12 * 60);
        check("Удаление задач по времени начала", removed == 2 && calendar.getTasks().size() == 1);
    }

    private static void testRemoveIntersectingPeriod() {
        TaskCalendar calendar = new TaskCalendar();
        calendar.addTask(9 * 60, 360, "Длинная задача");

        int affected = calendar.removeIntersectingPeriod(11 * 60, 13 * 60);
        ArrayList<Task> tasks = calendar.getTasks();
        check("Обрезка задачи с образованием двух частей", affected == 1 && tasks.size() == 2
                && tasks.get(0).getStartTime() == 9 * 60
                && tasks.get(0).getEndTime() == 11 * 60
                && tasks.get(1).getStartTime() == 13 * 60
                && tasks.get(1).getEndTime() == 15 * 60);
    }

    private static void testRemoveShortestTasks() {
        TaskCalendar calendar = new TaskCalendar();
        calendar.addTask(9 * 60, 30, "Короткая 1");
        calendar.addTask(10 * 60, 60, "Длинная");
        calendar.addTask(12 * 60, 30, "Короткая 2");

        int removed = calendar.removeShortestTasks();
        check("Удаление всех задач минимальной длительности", removed == 2
                && calendar.getTasks().size() == 1
                && calendar.getTasks().get(0).getTitle().equals("Длинная"));
    }

    private static void testFirstFreeSlot() {
        TaskCalendar calendar = new TaskCalendar();
        calendar.addTask(9 * 60, 60, "Первая");
        calendar.addTask(10 * 60 + 30, 90, "Вторая");

        boolean added = calendar.addInFirstFreeSlot(20, "Новая задача");
        ArrayList<Task> tasks = calendar.getTasks();
        check("Добавление в первый подходящий свободный промежуток", added && tasks.size() == 3
                && tasks.get(1).getStartTime() == 10 * 60
                && tasks.get(1).getEndTime() == 10 * 60 + 20);

        TaskCalendar fullCalendar = new TaskCalendar();
        fullCalendar.addTask(9 * 60, 12 * 60, "Весь рабочий день");
        check("Отказ при отсутствии свободного промежутка",
                !fullCalendar.addInFirstFreeSlot(1, "Невозможная задача"));
    }

    private static void check(String name, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("[OK] " + name);
        } else {
            failed++;
            System.out.println("[ОШИБКА] " + name);
        }
    }
}
