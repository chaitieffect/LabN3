public class Task {
    private final int startTime;
    private final int endTime;
    private final String title;

    public Task(int startTime, int endTime, String title) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.title = title;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public String getTitle() {
        return title;
    }

    public int getDuration() {
        return endTime - startTime;
    }

    public static String formatTime(int time) {
        int hours = time / 60;
        int minutes = time % 60;
        return String.format("%02d:%02d", hours, minutes);
    }

    @Override
    public String toString() {
        return formatTime(startTime) + " - " + formatTime(endTime) + " - " + title;
    }
}
