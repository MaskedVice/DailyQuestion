package me.maskedvice;
import java.io.FileNotFoundException;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import com.opencsv.bean.CsvToBeanBuilder;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
public class SendQuestion extends  ListenerAdapter {
    private static final Dotenv config = Dotenv.configure().load();
    
    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        super.onMessageReceived(event);
        try
        {
            //Get all questions sorted by Easy,Med,Hard from CSV
            List<Questions> questions = getQuestionsListFromCSV();

            if(event.getMessage().getContentRaw().equalsIgnoreCase("!ques") && event.getChannel().getIdLong() == Long.parseLong(config.get("CHANNELIDRISTRICTEDFORMESSAGE")))
            {
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
                    executor.scheduleWithFixedDelay(task, 0, Integer.parseInt(config.get("DELAY")), TimeUnit.DAYS);
                
            }
            else{
                if (event.getAuthor().equals(event.getJDA().getSelfUser())) return;
                event.getChannel().sendMessage("Command not Allowed in this Channel").queue();  
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //Gets Questions from CSV File 
    private List<Questions> getQuestionsListFromCSV() throws IllegalStateException, FileNotFoundException {
        String localDir = System.getProperty("user.dir") + config.get("PATHTOQUESTIONSCSV");
        List<Questions> qList =  new CsvToBeanBuilder(new FileReader(localDir)).withType(Questions.class).build().parse();
        return qList;
    }

    //Formatting message to send
    public String getMessageToSend(@Nonnull Questions easy,Questions hard){
        String date = getCurrentUtcDate();
        String easyQuestion = easy.FrontendQuestionId + ": " + easy.Title + " " + config.get("LEETCODEURL") + easy.TitleSlug;
        String hardQuestion = hard.FrontendQuestionId + ": " + hard.Title + " " + config.get("LEETCODEURL") + hard.TitleSlug;
        String message = config.get("MESSAGE");

        return java.text.MessageFormat.format(message,date,easyQuestion,hardQuestion,"\n","<t:" + getTimeInTimestamp() + ":F>");
    }

    //Getting UTC Date for message
    public static String getCurrentUtcDate() {  
        ZoneId z = ZoneId.of("Etc/UTC");
        LocalDateTime myDateObj = LocalDateTime.now(z);  
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("E, MMM dd yyyy");  
        String formattedDate = myDateObj.format(myFormatObj);  
        return formattedDate;
    }
    
    //Fetch and return Daily Question from Leetcode.com/problems/all
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

    //Get TimeStamp for personalized end date
    public static long getTimeInTimestamp() {  
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(Calendar.DAY_OF_MONTH,Integer.parseInt(getCurrentPSTDay())+1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date endDate = cal.getTime();
        long secs = endDate.getTime()/1000;
        return secs;
    } 

}

