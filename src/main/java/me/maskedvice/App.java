package me.maskedvice;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import com.opencsv.bean.CsvToBeanBuilder;

/**
 * Hello world!
 */
public final class App {
    public static JDABuilder jdaBuilder;
    public static List<Questions> qList;
    private static final Dotenv config = Dotenv.configure().load();
    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        
        String token = config.get("TOKEN");
        qList = getQuestionsListFromCSV();
        jdaBuilder = JDABuilder.createDefault(token);
        jdaBuilder.enableIntents(GatewayIntent.MESSAGE_CONTENT,GatewayIntent.GUILD_MESSAGES).addEventListeners(new SendQuestion(),new SendSpecificQuestion()).build();
    }
        //Gets Questions from CSV File 
    public static List<Questions> getQuestionsListFromCSV() throws IllegalStateException, FileNotFoundException {
        try {
            String localDir = System.getProperty("user.dir") + config.get("PATHTOQUESTIONSCSV");
            List<Questions> qList =  new CsvToBeanBuilder(new FileReader(localDir)).withType(Questions.class).build().parse();
            return qList;
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null; 
    }
}