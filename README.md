# Alfred

This is a Java chatbot project named **Alfred**. Given below are instructions on how to set it up.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. Run the **GUI** from the project root with `./gradlew run` (macOS / Linux) or `gradlew run` (Windows), or run `alfred.Launcher.main()` from the IDE. You should see Alfred's chat window. Type commands in the text field, press Enter or **Send**, and type `bye` to close the app.
1. To use the **console** UI instead, locate `src/main/java/alfred/Alfred.java`, right-click it, and choose `Run Alfred.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see a greeting. Alfred then replies to each command you type, and exits when you type `bye`:
   ```
       ____________________________________________________________
           _    _  __              _
          / \  | |/ _|_ __ ___  __| |
         / _ \ | | |_| '__/ _ \/ _` |
        / ___ \| |  _| | |  __/ (_| |
       /_/   \_\_|_| |_|  \___|\__,_|
        Hello! I'm Alfred.
        What can I do for you?
       ____________________________________________________________
   list
       ____________________________________________________________
        list
       ____________________________________________________________
   blah
       ____________________________________________________________
        blah
       ____________________________________________________________
   bye
       ____________________________________________________________
        Bye. Hope to see you again soon!
       ____________________________________________________________
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Creating an executable JAR

Prerequisites: JDK 25.

Alfred is packaged as a fat JAR with the Gradle Shadow plugin. The output includes the application classes and a `Main-Class` manifest entry so it can be launched with `java -jar`. The JAR starts the GUI (`alfred.Launcher`).

1. Open a terminal in the project root (the folder that contains `build.gradle`).
1. Create the JAR:
   * macOS / Linux: `./gradlew clean shadowJar`
   * Windows: `gradlew clean shadowJar`
1. Locate the file at `build/libs/alfred.jar`.

To run a distributed copy:

1. Copy `alfred.jar` into an empty folder.
1. Open a command window in that folder.
1. Run `java -jar alfred.jar`.

Tasks are saved in `data/alfred.txt` next to the JAR (the folder you ran the command from), not inside the project directory.
