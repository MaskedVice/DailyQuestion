package me.maskedvice;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

/**
 * Hello world!
 */
public final class App {
    public static JDABuilder jdaBuilder;
    private static final Dotenv config = Dotenv.configure().load();
    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        
        String token = config.get("TOKEN");
        jdaBuilder = JDABuilder.createDefault(token);
        jdaBuilder.enableIntents(GatewayIntent.MESSAGE_CONTENT,GatewayIntent.GUILD_MESSAGES).addEventListeners(new SendQuestion()).build();
    }
}