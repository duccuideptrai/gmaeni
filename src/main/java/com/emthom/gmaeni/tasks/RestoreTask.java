package com.emthom.gmaeni.tasks;

import org.apache.commons.io.FileUtils;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

/**
 * Gradle Task to restore original JAVA files after compilation.
 */
public class RestoreTask extends AbstractTask {

    @Inject
    public RestoreTask() {
        super();
    }

    @TaskAction
    public void restore() throws IOException {
        if (!enabled) return;
        if (!checkSCM()) return;

        if (backupDirExists()) {
            for (File javaFile : this.getAllJavaFiles()) {
                this.restoreFile(javaFile);
            }
        } else {
            System.out.println("⚠️ There is no backup to restore!");
        }
        removeGmaeniCode();
    }

    private void removeGmaeniCode() throws IOException {
        File codePackage = new File(pathSrc + File.separator + InjectCodeTask.PACKAGE_NAME.replace(".", File.separator));
        FileUtils.deleteDirectory(codePackage);
        System.out.println("\uD83E\uDDF9 Remove Gmaeni code: " + codePackage.getAbsolutePath());
    }

    private void restoreFile(File file) throws IOException {

        String srcFile = file.getAbsolutePath().replace(rootProject, "");
        if (isGmaeniFile(file)) return;

        if (isGmaenitized(file)) {
            File backup = new File(backupDir() + srcFile);
            if (backup.exists() && backup.length() > 0) {
                FileUtils.copyFile(backup, file);
                System.out.println("♻️ Restore: " + srcFile);
            } else {
                System.out.println("⚠️ There is no backup for: " + srcFile);
            }
        } else {
            System.out.println("⚠️ Cannot overwrite an unencrypted file: " + srcFile);
        }
    }

}
