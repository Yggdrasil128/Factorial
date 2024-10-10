package de.yggdrasil128.factorial.devtools;

import de.yggdrasil128.factorial.FactorialApplication;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class DevToolsService {

    public void wipeDatabaseAndRestart() {
        File databaseFile = new File("data/db.mv.db");
        if (!databaseFile.exists()) {
            System.err.println("Couldn't wipe the database because the file is missing");
            return;
        }

        // noinspection ResultOfMethodCallIgnored
        FactorialApplication.restart(databaseFile::delete);
    }
}
