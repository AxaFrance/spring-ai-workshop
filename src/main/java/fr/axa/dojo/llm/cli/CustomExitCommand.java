package fr.axa.dojo.llm.cli;

import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.standard.commands.Quit;

@Command
public class CustomExitCommand implements Quit.Command {

    @Command(command = "exit", interactionMode = InteractionMode.INTERACTIVE, description = "Exit the shell")
    public void exit() {
        System.out.println("Goodbye!");
        System.exit(0);
    }
}
