package fr.axa.dojo.llm.cli;

import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.context.InteractionMode;

import fr.axa.dojo.llm.services.LLMService;
import fr.axa.dojo.llm.services.RAGDataService;
import fr.axa.dojo.llm.services.RAGService;
import lombok.RequiredArgsConstructor;

@Command
@RequiredArgsConstructor
public class CommandsGroup {

    private final LLMService llmService;
    private final RAGService ragService;
    private final RAGDataService ragDataService;

    @Command(command = "llm", interactionMode = InteractionMode.INTERACTIVE, description = "Interaction with LLM in stream mode")
    public void llm(@Option(arity = CommandRegistration.OptionArity.ZERO_OR_MORE) final String args) {

        final String question = argsToPrompt(args);

        System.out.println("LLM is processing an answer...");
        final Stream<String> generatedStream = llmService.askQuestion(question);
        generatedStream.forEach(System.out::print);
        System.out.println();

    }

    @Command(command = "llmctx", interactionMode = InteractionMode.INTERACTIVE, description = "Interaction with LLM about context in stream mode")
    public void llmctx(@Option(arity = CommandRegistration.OptionArity.ZERO_OR_MORE) final String args) {

        final String question = argsToPrompt(args);

        System.out.println("LLM is processing a context-based answer...");
        final Stream<String> generatedStream = llmService.askQuestionAboutContext(question);
        generatedStream.forEach(System.out::print);
        System.out.println();

    }

    @Command(command = "rag", interactionMode = InteractionMode.INTERACTIVE, description = "Using RAG pattern with LLM in stream mode")
    public void rag(@Option(arity = CommandRegistration.OptionArity.ZERO_OR_MORE) final String args) {

        final String question = argsToPrompt(args);

        System.out.println("LLM is processing an answer using RAG approach...");
        final Stream<String> generatedStream = ragService.getResponse(question);
        generatedStream.forEach(System.out::print);
        System.out.println();

    }

    @Command(command = "etl", interactionMode = InteractionMode.INTERACTIVE, description = "Execute ETL process")
    public void etl() {
        ragDataService.etl();
    }

    /**
     * Replace commas with spaces (for hands-on fluency).
     *
     * @param argsCommaSeparated args separated with commas
     * @return prompt
     */
    private String argsToPrompt(final String argsCommaSeparated) {
        return Optional.ofNullable(argsCommaSeparated)
                .map(args -> args.replace(',', ' '))
                .orElse("");
    }

}
