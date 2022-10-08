package me.maskedvice;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import com.opencsv.bean.CsvToBeanBuilder;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
public class SendEasyQuestion extends  ListenerAdapter {
    private static final Dotenv config = Dotenv.configure().load();
    
    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        super.onMessageReceived(event);


        String localDir = System.getProperty("user.dir") + config.get("PATHTOQUESTIONSCSV");


        if(event.getMessage().getContentRaw().equalsIgnoreCase("!ques"))
        {
            try
            {
                //Get all questions sorted by Easy,Med,Hard from CSV
                List<Questions> questions =(new CsvToBeanBuilder(new FileReader(localDir)).withType(Questions.class).build().parse());
                
                //Filter only free questions
                final List<Questions> quesList = questions.stream().filter(q -> q.isPaidOnly.equalsIgnoreCase("false")).collect(Collectors.toList());


                //Scheduler that sends message with delay
                ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
                Runnable task = () -> {

                    //Get easy question from list and set isused as 1
                    Questions easy = quesList.stream().filter(q->!q.isUsed).findFirst().get();
                    easy.isUsed = true;

                    //Get Hard question from leetcode
                    Questions medHard = getQuestionOfTheDay(quesList);
                    
                    // Get message to send
                    String message = getMessageToSend(easy, medHard);

                    //Send Message
                    event.getChannel().sendMessage(message).queue();
                };
                //Executor can be setup with delay and TImeUnit(seconds,minutes,days etc)
                executor.scheduleWithFixedDelay(task, 0, Integer.parseInt(config.get("DELAY")), TimeUnit.MINUTES);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //Formatting message to send
    public String getMessageToSend(@Nonnull Questions easy,Questions hard){
        String date = getCurrentUtcDate();
        String easyQuestion = easy.FrontendQuestionId + ": " + easy.Title + " ❓\n" + config.get("LEETCODEURL") + easy.TitleSlug;
        String hardQuestion = hard.FrontendQuestionId + ": " + hard.Title + " ❓\n" + config.get("LEETCODEURL") + hard.TitleSlug;
        String message = config.get("MESSAGE");

        return java.text.MessageFormat.format(message,date,easyQuestion,hardQuestion,"\n");
    }

    //Getting UTC Date for message
    public static String getCurrentUtcDate() {  
        ZoneId z = ZoneId.of("Etc/UTC");
        LocalDateTime myDateObj = LocalDateTime.now(z);  
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("E, MMM dd yyyy");  
        String formattedDate = myDateObj.format(myFormatObj);  
        return formattedDate;
    }
    
    //Fetch and return Daily Question from Leectcode.com/problems/all
    public static Questions getQuestionOfTheDay(List<Questions> questions){
        Document doc;
        try {
            String d = getCurrentPSTDay();
            doc = Jsoup.connect(config.get("QUESOFTHEDAY")).get();
            Elements link = doc.select("a.group");
            String relHref = link.get(Integer.parseInt(d)-1).attr("href");
            Questions hard = questions.stream().filter(q->q.TitleSlug.equalsIgnoreCase(relHref.split("/")[2])).findFirst().get();
            return hard;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get day of the month to fetch daily Question
    public static String getCurrentPSTDay() {  
        ZoneId z = ZoneId.of("America/Los_Angeles");
        LocalDateTime myDateObj = LocalDateTime.now(z);
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd");  
        String formattedDate = myDateObj.format(myFormatObj);  
        return formattedDate;
    }  
        
}

