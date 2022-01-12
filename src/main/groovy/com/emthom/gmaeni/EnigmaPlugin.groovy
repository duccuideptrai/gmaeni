package com.emthom.gmaeni


import com.emthom.gmaeni.tasks.CleanBackupTask
import com.emthom.gmaeni.tasks.EnigmaTask
import com.emthom.gmaeni.tasks.RestoreTask
import com.emthom.gmaeni.utils.TextUtils
import com.emthom.gmaeni.utils.Utils
import com.emthom.gmaeni.tasks.BackupTask
import com.emthom.gmaeni.tasks.InjectCodeTask
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Gmaeni Gradle Plugin
 */
class EnigmaPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        // https://docs.gradle.org/current/userguide/custom_plugins.html
        def extension = project.extensions.create('gmaeni', EnigmaPluginExtension)

        // Generate random hash key, if not defined
        if (TextUtils.isEmpty(extension.hash))
            extension.hash = Utils.randomHashKey()

        project.afterEvaluate {

            // Search custom Encryption task:
            def customEncryptTask = null
            if (extension.encryptionTaskName != null) {
                customEncryptTask = project.tasks.getByName(extension.encryptionTaskName)
                if (customEncryptTask != null) {
                    println("⚙️ C️ustom Encryption Task found: ${extension.encryptionTaskName}")
                }
            }

            project.task('cleanBackup', type: CleanBackupTask) {
                enabled = extension.enabled
                rootProject = project.rootDir.absolutePath
                pathSrc = project.rootDir.absolutePath + extension.srcJava
                debug = extension.debug
            }

            project.task('backup', type: BackupTask) {
                enabled = extension.enabled
                rootProject = project.rootDir.absolutePath
                pathSrc = project.rootDir.absolutePath + extension.srcJava
                debug = extension.debug
            }

            project.task('injectCode', type: InjectCodeTask) {
                enabled = extension.enabled
                rootProject = project.rootDir.absolutePath
                pathSrc = project.rootDir.absolutePath + extension.srcJava
                hash = extension.hash
                customFunction = extension.customFunction
                debug = extension.debug
            }

            project.task('encrypt', type: EnigmaTask) {
                enabled = extension.enabled
                rootProject = project.rootDir.absolutePath
                pathSrc = project.rootDir.absolutePath + extension.srcJava
                hash = extension.hash
                ignoredClasses = extension.ignoredClasses
                classes = extension.classes
                customFunction = extension.customFunction
                customEncryptionTask = customEncryptTask
                injectFakeKeys = extension.injectFakeKeys
                debug = extension.debug
            }

            project.task('restore', type: RestoreTask) {
                enabled = extension.enabled
                rootProject = project.rootDir.absolutePath
                pathSrc = project.rootDir.absolutePath + extension.srcJava
                debug = extension.debug
            }

            project.tasks.getByName('clean').dependsOn('cleanBackup')

            project.tasks.getByName('preBuild').dependsOn('backup')
            project.tasks.getByName('preBuild').dependsOn('injectCode')
            project.tasks.getByName('preBuild').dependsOn('encrypt')

            for (task in project.tasks) {
                if ((task.name.startsWith('assemble') || task.name.startsWith('bundle'))
                        && (task.name.endsWith('Release') || task.name.endsWith('Debug')
                                || task.name.endsWith('Stg') || task.name.endsWith('Security')))
                    task.finalizedBy('restore')
                if (task.name.startsWith('generate') && task.name.endsWith('Sources'))
                    task.finalizedBy('restore')
            }
        }
    }
}
