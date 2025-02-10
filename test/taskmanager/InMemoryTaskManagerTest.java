package taskmanager;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends AbstractTaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void initManager() {
        this.manager = new InMemoryTaskManager();
        createTasks();
    }

    @Override
    protected InMemoryTaskManager getTaskManagerForChecks() {
        return this.manager;
    }
}