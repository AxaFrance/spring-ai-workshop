![cover picture](workshop/images/cover.avif)

# Exploring interactions with LLMs : Practical insights with Spring AI

LLM (Large Language Model) concept is widely used to build chatbots, question-answering systems, and other conversational applications.

In this workshop, we will discover how to interact with a LLM as we craft a conversational agent step-by-step.
Throughout this journey, we will understand the capabilities and limitations of LLMs, and explore prompt engineering techniques to elevate their standard behavior.

For the practical part, we will learn about Spring AI's chat completion features to interact with LLMs. Spring AI serves as an abstraction layer able to interact with several LLM providers.
For the workshop's purposes, we will use Ollama, a solution for running LLMs locally.

## :sparkles: Quick start

If you don't want to save your work, you can start the workshop directly in a cloud environment.

[![Open in GitHub Codespaces](https://github.com/codespaces/badge.svg)](https://github.com/codespaces/new?hide_repo_select=true&ref=main&repo=863368883&machine=standardLinux32gb&devcontainer_path=.devcontainer%2Fdevcontainer.json&location=WestEurope)

[![Open in Gitpod](https://gitpod.io/button/open-in-gitpod.svg)](https://gitpod.io?autostart=true&editor=code&workspaceClass=g1-large#https://github.com/AxaFrance/spring-ai-workshop)

## :wrench: Environment setup

- [GitHub Codespaces](workshop/setup/codespaces.md) (5min)
- [GitPod](workshop/setup/gitpod.md) (5min)
- [Local Hosting](workshop/setup/local.md) (15min)

## :crystal_ball: Install phi 4 mini model

Execute this command only once to pull the model.

```shell
docker exec -it ollama sh -c "ollama pull phi4-mini:latest"
```

## :building_construction: Build the project

Execute this command after code update.

```shell
mvn clean install
```

## :running_woman: Run the application

```shell
mvn spring-boot:run
```

## :rocket: Hands-on

1. [Zero-shot prompting](workshop/exercise-1.md) (15min)
2. [Conversational memory](workshop/exercise-2.md) (15min)
3. [Information extraction](workshop/exercise-3.md) (20min)
4. [Retrieval Augmented Generation (RAG)](workshop/exercise-4.md) (30min)
5. [Conclusion](workshop/conclusion.md)

## :gift: Bonus / Best Practices

1. [Conversational memory with Advisor](bonus/bonus-1.md) (10min)

## :star: Contributors

This workshop is proposed by AXA France's Development Guild.

Maintainers:

- Clément GIGUEL [:octocat:](https://github.com/clementgig)
- Lamine DIAKHABY [:octocat:](https://github.com/DIAKHABY20)
- Benoît CACCINOLO [:octocat:](https://github.com/bc-axa)
- Oussama HMANI [:octocat:](https://github.com/OussamaHMANI)
- Samir KERROUMI [:octocat:](https://github.com/samkerr4coding)
