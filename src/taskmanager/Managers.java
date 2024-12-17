package taskmanager;

public class Managers {

    public static TaskManager getDefault() {
        HistoryManager defaultHistory = getDefaultHistory();
        return new InMemoryTaskManager(defaultHistory);
    }
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
