package me.maskedvice;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import com.opencsv.bean.CsvToBeanBuilder;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SendEasyQuestion extends ListenerAdapter {
    private static final Dotenv config = Dotenv.configure().load();
    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event){
        super.onSlashCommandInteraction(event);
        Random rand = new Random();
        String localDir = System.getProperty("user.dir");
        localDir +=config.get("PATHTOQUESTIONSCSV");
        try
        {
            
            List<Questions> beans = new CsvToBeanBuilder(new FileReader(localDir)).withType(Questions.class).build().parse();

            List<Questions> filteredEasyQuestions = beans
                .stream()
                .filter(q ->
                (
                    q.isPaidOnly.equalsIgnoreCase("false") &&
                    q.Difficulty.equalsIgnoreCase("easy")
                )).collect(Collectors.toList());

            List<Questions> filteredMedHardQuestions = beans
                .stream().
                filter(q ->
                (
                    q.isPaidOnly.equalsIgnoreCase("false") &&
                    (
                        q.Difficulty.equalsIgnoreCase("medium") || q.Difficulty.equalsIgnoreCase("hard")
                    )
                )).collect(Collectors.toList());

            
            Questions easy = filteredEasyQuestions.get(rand.nextInt(filteredEasyQuestions.size()));
            Questions medHard = filteredMedHardQuestions.get(rand.nextInt(filteredMedHardQuestions.size()));
            
            String message = getMessageToSend(easy, medHard);
            event.reply(message).queue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getMessageToSend(@Nonnull Questions easy,Questions hard){
        String date = getCurrentUtcTime();
        String easyQuestion = easy.FrontendQuestionId + ": " + easy.Title + " ❓\n" + config.get("LEETCODEURL") + easy.TitleSlug;
        String hardQuestion = hard.FrontendQuestionId + ": " + hard.Title + " ❓\n" + config.get("LEETCODEURL") + hard.TitleSlug;
        String message = config.get("MESSAGE");

        return java.text.MessageFormat.format(message,date,easyQuestion,hardQuestion,"\n");
    }


    public static String getCurrentUtcTime() {  
        ZoneId z = ZoneId.of("Etc/UTC");
        LocalDateTime myDateObj = LocalDateTime.now(z);  
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("E, MMM dd yyyy");  
        String formattedDate = myDateObj.format(myFormatObj);  
        return formattedDate;
    }  
}
