# Gmaeni
Android Gradle Plugin - Obfuscator Strings

this plugin help you to obfuscate string in your android project
due to the task change source for obfuscate string and restore after build success,
you must use SCM (git, svn etc..) to run the plugin.

## How integrate it?

build.gradle (app)
```groovy
apply plugin: 'com.android.application'

// Add Gmaeni Plugin
apply plugin: 'io.github.duccuideptrai.gmaeni'

// Set Gmaeni options:
gmaeni.enabled = true
gmaeni.injectFakeKeys = true
gmaeni.ignoredClasses = ["com.my.packagename.MainActivity.java"]

android {
    buildTypes {
        release {
            // Don't forget to enable ProGuard !
            minifyEnabled true
        }
    }
}
```

## Compile your App process

In the compilation process, gmaeni plugin do:
- Backup all Java files in backup directory **gmaeni-backup**
- Parse your code and encrypt all String values for each Java file
- Inject Gmaeni source code (encryption code)
- Inject fake secrete keys (optional - check **gmaeni.injectFakeKeys** option)
- Compile your App (classic process)
- Restore your original Java files

```sh
$ ./gradlew assembleRelease
```
```sh
> Task :app:backup
💾 Backup: /app/src/main/java/com/app/helloworld/MainActivity.java
💾 Backup: /app/src/main/java/com/app/helloworld/helpers/IResponse.java
💾 Backup: /app/src/main/java/com/app/helloworld/helpers/Utils.java
💾 Backup: /app/src/main/java/com/app/helloworld/helpers/ATest.java
💾 Backup: /app/src/main/java/com/app/helloworld/helpers/TestImpl.java
💾 Backup: /app/src/main/java/com/app/helloworld/Constants.java

> Task :app:encrypt
🔐 MainActivity.java encrypted
🔐 IResponse.java encrypted
🔐 Utils.java encrypted
🔐 ATest.java encrypted
🔐 TestImpl.java encrypted
🔐 Constants.java encrypted

> Task :app:injectCode
✏️ Add Gmaeni code

> Task :app:restore
♻️ Restore: /app/src/main/java/com/proto/helloworld/MainActivity.java
♻️ Restore: /app/src/main/java/com/app/helloworld/helpers/IResponse.java
♻️ Restore: /app/src/main/java/com/app/helloworld/helpers/Utils.java
♻️ Restore: /app/src/main/java/com/app/helloworld/helpers/ATest.java
♻️ Restore: /app/src/main/java/com/app/helloworld/helpers/TestImpl.java
♻️ Restore: /app/src/main/java/com/app/helloworld/Constants.java
🧹 Remove Gmaeni code: ~/HelloWorld/app/src/main/java/com/app

```

## Options

Bellow the options of Gmaeni plugin:

* **gmaeni.enabled** *(true | false)* : Enable or disable the string obfuscate process (default: true)
* **gmaeni.injectFakeKeys** *(true | false)* : if activated, create fake string keys and injected it into your code (default: true)
* **gmaeni.hash** (string) : let you define your own obfuscate key (32 characters recommended)
* **gmaeni.classes** (array of strings) : let you defined the only classes to encrypt
* **gmaeni.ignoredClasses** (array of strings): define the classes to not encrypt
* **gmaeni.srcJava** (string): root path of your JAVA files (default: **/app/src/main/java**)