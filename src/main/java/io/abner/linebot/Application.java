package io.abner.linebot;

import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootApplication
@LineMessageHandler
@PropertySource(ignoreResourceNotFound = true, value = "classpath:app.properties")
public class Application {

    public static Path downloadedContentDir;

    public static void main(String args[]) throws IOException {
        downloadedContentDir = Files.createTempDirectory("line-bot");
        SpringApplication.run(Application.class);
    }

}
