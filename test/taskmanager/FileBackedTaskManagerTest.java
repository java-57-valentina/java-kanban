package taskmanager;

import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class FileBackedTaskManagerTest extends AbstractTaskManagerTest<FileBackedTaskManager> {

    private Path tmpPath = null;

    @BeforeEach
    void initManager() {
        try {
            tmpPath = Files.createTempFile("tmp", ".csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        manager = FileBackedTaskManager.loadFromFile(tmpPath);
        createTasks();
    }

    @Override
    protected FileBackedTaskManager getTaskManagerForChecks() {
        return FileBackedTaskManager.loadFromFile(tmpPath);
    }
}