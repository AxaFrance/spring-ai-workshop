package fr.axa.dojo.llm;

import fr.axa.dojo.llm.cli.CommandsGroup;
import fr.axa.dojo.llm.cli.CustomExitCommand;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.shell.command.annotation.EnableCommand;

@SpringBootApplication
@EnableCommand({ CommandsGroup.class, CustomExitCommand.class})
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

}