package de.yggdrasil128.factorial.backup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.yggdrasil128.factorial.model.External;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.game.*;
import de.yggdrasil128.factorial.model.save.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class BackupService {

    private static final Logger LOG = LoggerFactory.getLogger(BackupService.class);

    private static final String GAME_SUBFOLDER_NAME = "games";
    private static final String SAVE_SUBFOLDER_NAME = "saves";

    private final GameService gameService;
    private final Set<Integer> dirtyGameIds = new HashSet<>();
    private final SaveService saveService;
    private final Set<Integer> dirtySaveIds = new HashSet<>();

    /** The main autowired executor. */
    private final ExecutorService taskExecutor;
    /** Our local executor, used solely for scheduling tasks to the main executor. */
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    // TODO make configurable
    private final Path backupRootFolder = Paths.get("backups");
    private final Duration backupInterval = Duration.ofMinutes(10);
    private final int backupCount = 5;

    private final ObjectMapper writer = new ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, true);

    @Autowired
    public BackupService(GameService gameService, SaveService saveService, ExecutorService taskExecutor) {
        this.gameService = gameService;
        this.saveService = saveService;
        this.taskExecutor = taskExecutor;
    }

    @PostConstruct
    public void start() {
        long delay = backupInterval.toSeconds();
        scheduler.scheduleWithFixedDelay(() -> taskExecutor.submit(this::processAll), delay, delay, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void stop() {
        scheduler.shutdown();
        try {
            if (scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                // FIXME this currently does not work, because the hibernate session is closed before we get to do this
                // do one last backup if necessary
                // taskExecutor.submit(this::processAll);
            }
            /*
             * Else we were waiting for more than five seconds, hence a backup was currently taking place and it did not
             * work, so there's no point in trying it again here.
             */
        } catch (InterruptedException exc) {
            LOG.warn("Interrupted while joining backup scheduler");
        }
    }

    private void processAll() {
        LOG.debug("Checking for queued backups");
        for (int gameId : dirtyGameIds) {
            LOG.info("Backing up game #{}", gameId);
            try {
                store(gameService.getSummary(gameId, External.SAVE_FILE),
                        getBackupSubFolder(GAME_SUBFOLDER_NAME, gameId).resolve("game.json"));
            } catch (Exception exc) {
                LOG.error("Could not back up game #{}", gameId, exc);
            }
        }
        dirtyGameIds.clear();
        for (int saveId : dirtySaveIds) {
            LOG.info("Backing up save #{}", saveId);
            try {
                store(saveService.getSummary(saveId, External.SAVE_FILE),
                        getBackupSubFolder(SAVE_SUBFOLDER_NAME, saveId).resolve("save.json"));
            } catch (Exception exc) {
                LOG.error("Could not back up save #{}", saveId, exc);
            }
        }
        dirtySaveIds.clear();
    }

    private void store(Object data, Path file) throws IOException {
        LOG.debug("Writing to {}", file);
        try (Writer out = Files.newBufferedWriter(file)) {
            writer.writeValue(out, data);
        }
    }

    private Path getBackupSubFolder(String type, int id) throws IOException {
        Path backupSubFolder = backupRootFolder.resolve(type).resolve(Integer.toString(id));
        if (Files.isDirectory(backupSubFolder)) {
            clearBackupSlot(backupSubFolder);
        }
        Path result = getBackupFolder(backupSubFolder, 0);
        Files.createDirectories(result);
        return result;
    }

    private void clearBackupSlot(Path backupSubFolder) throws IOException {
        Path oldestBackupFolder = getBackupFolder(backupSubFolder, backupCount - 1);
        if (Files.isDirectory(oldestBackupFolder)) {
            FileSystemUtils.deleteRecursively(oldestBackupFolder);
        }
        for (int i = backupCount - 2; i >= 0; i--) {
            Path backupFolderFrom = getBackupFolder(backupSubFolder, i);
            Path backupFolderTo = getBackupFolder(backupSubFolder, i + 1);
            if (Files.isDirectory(backupFolderFrom)) {
                Files.move(backupFolderFrom, backupFolderTo);
            }
        }
    }

    private static Path getBackupFolder(Path backupSubFolder, int index) {
        return backupSubFolder.resolve("backup-" + index);
    }

    @EventListener
    public void on(GameRelatedEvent event) {
        if (handle(gameService, event.getGameId(), dirtyGameIds)) {
            LOG.debug("Queuing game #{} for backup", event.getGameId());
        }
    }

    @EventListener
    public void on(GamesReorderedEvent event) {
        for (Game game : event.getGames()) {
            if (handle(gameService, game.getId(), dirtyGameIds)) {
                LOG.debug("Queuing game #{} for backup", game.getId());
            }
        }
    }

    @EventListener
    public void on(SaveRelatedEvent event) {
        if (handle(saveService, event.getSaveId(), dirtySaveIds)) {
            LOG.debug("Queuing save #{} for backup", event.getSaveId());
        }
    }

    @EventListener
    public void on(SavesReorderedEvent event) {
        for (Save save : event.getSaves()) {
            if (handle(saveService, save.getId(), dirtySaveIds)) {
                LOG.debug("Queuing save #{} for backup", save.getId());
            }
        }
    }

    private static boolean handle(ModelService<?, ?, ?> service, int id, Set<Integer> sink) {
        if (service.has(id)) {
            return sink.add(id);
        }
        sink.remove(id);
        return false;
    }

}
