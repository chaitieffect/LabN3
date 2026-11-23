import java.util.ArrayList;

public class TaskCalendar {
    private static final int DAY_START = 9 * 60;
    private static final int DAY_END = 21 * 60;

    private ArrayList<Task> tasks = new ArrayList<>();

    // Добавляет задачу, если она помещается в рабочий день и не пересекается с другими.
    public boolean addTask(int startTime, int duration, String title) {
        if (startTime < DAY_START || startTime >= DAY_END) {
            return false;
        }
        if (duration <= 0 || duration > DAY_END - startTime) {
            return false;
        }
        if (title == null || title.trim().isEmpty()) {
            return false;
        }

        int endTime = startTime + duration;

        for (Task task : tasks) {
            if (startTime < task.getEndTime() && endTime > task.getStartTime()) {
                return false;
            }
        }

        tasks.add(new Task(startTime, endTime, title.trim()));
        sortTasks();
        return true;
    }

    // Возвращает копию списка, чтобы внешний код не мог изменить календарь напрямую.
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks);
    }

    // Находит самую длинную задачу. При равной длительности остается первая по времени.
    public Task getLongestTask() {
        if (tasks.isEmpty()) {
            return null;
        }

        Task longest = tasks.get(0);
        for (int i = 1; i < tasks.size(); i++) {
            if (tasks.get(i).getDuration() > longest.getDuration()) {
                longest = tasks.get(i);
            }
        }
        return longest;
    }

    // Возвращает задачи, пересекающиеся с заданным промежутком [periodStart, periodEnd).
    public ArrayList<Task> getTasksInPeriod(int periodStart, int periodEnd) {
        ArrayList<Task> result = new ArrayList<>();
        if (!isValidPeriod(periodStart, periodEnd)) {
            return result;
        }

        for (Task task : tasks) {
            if (task.getStartTime() < periodEnd && task.getEndTime() > periodStart) {
                result.add(task);
            }
        }
        return result;
    }

    // Возвращает задачи, которые закончились не позже указанного времени.
    public ArrayList<Task> getCompletedBefore(int time) {
        ArrayList<Task> result = new ArrayList<>();
        if (time < DAY_START || time > DAY_END) {
            return result;
        }

        for (Task task : tasks) {
            if (task.getEndTime() <= time) {
                result.add(task);
            }
        }
        return result;
    }

    // Номер задачи соответствует ее позиции в текущем отсортированном списке (нумерация с 1).
    public boolean removeTask(int number) {
        if (number < 1 || number > tasks.size()) {
            return false;
        }
        tasks.remove(number - 1);
        return true;
    }

    // Удаляет задачи, время начала которых входит в заданный промежуток, включая его границы.
    public int removeStartingInPeriod(int periodStart, int periodEnd) {
        if (!isValidPeriod(periodStart, periodEnd)) {
            return 0;
        }

        int removed = 0;
        for (int i = tasks.size() - 1; i >= 0; i--) {
            int start = tasks.get(i).getStartTime();
            if (start >= periodStart && start <= periodEnd) {
                tasks.remove(i);
                removed++;
            }
        }
        return removed;
    }

    // Вырезает заданный промежуток из каждой пересекающейся задачи.
    // Если промежуток находится внутри задачи, вместо нее создаются две оставшиеся части.
    public int removeIntersectingPeriod(int periodStart, int periodEnd) {
        if (!isValidPeriod(periodStart, periodEnd)) {
            return 0;
        }

        ArrayList<Task> updatedTasks = new ArrayList<>();
        int affected = 0;

        for (Task task : tasks) {
            int start = task.getStartTime();
            int end = task.getEndTime();

            if (start < periodEnd && end > periodStart) {
                affected++;

                if (start < periodStart) {
                    updatedTasks.add(new Task(start, periodStart, task.getTitle()));
                }
                if (end > periodEnd) {
                    updatedTasks.add(new Task(periodEnd, end, task.getTitle()));
                }
            } else {
                updatedTasks.add(task);
            }
        }

        tasks = updatedTasks;
        sortTasks();
        return affected;
    }

    // Удаляет все задачи с минимальной продолжительностью.
    public int removeShortestTasks() {
        if (tasks.isEmpty()) {
            return 0;
        }

        int minDuration = tasks.get(0).getDuration();
        for (Task task : tasks) {
            if (task.getDuration() < minDuration) {
                minDuration = task.getDuration();
            }
        }

        int removed = 0;
        for (int i = tasks.size() - 1; i >= 0; i--) {
            if (tasks.get(i).getDuration() == minDuration) {
                tasks.remove(i);
                removed++;
            }
        }
        return removed;
    }

    // Размещает задачу в первом свободном промежутке рабочего дня.
    public boolean addInFirstFreeSlot(int duration, String title) {
        if (duration <= 0 || duration > DAY_END - DAY_START) {
            return false;
        }
        if (title == null || title.trim().isEmpty()) {
            return false;
        }

        int freeStart = DAY_START;

        for (Task task : tasks) {
            if (task.getStartTime() - freeStart >= duration) {
                return addTask(freeStart, duration, title);
            }
            if (task.getEndTime() > freeStart) {
                freeStart = task.getEndTime();
            }
        }

        if (DAY_END - freeStart >= duration) {
            return addTask(freeStart, duration, title);
        }
        return false;
    }

    private boolean isValidPeriod(int periodStart, int periodEnd) {
        return periodStart >= DAY_START && periodEnd <= DAY_END && periodStart < periodEnd;
    }

    // Поддерживает хронологический порядок задач после добавления или обрезки.
    private void sortTasks() {
        for (int i = 1; i < tasks.size(); i++) {
            Task current = tasks.get(i);
            int j = i - 1;

            while (j >= 0 && tasks.get(j).getStartTime() > current.getStartTime()) {
                tasks.set(j + 1, tasks.get(j));
                j--;
            }
            tasks.set(j + 1, current);
        }
    }
}
