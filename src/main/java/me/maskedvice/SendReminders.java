package me.maskedvice;
import java.util.concurrent.TimeUnit;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
public class SendReminders extends  ListenerAdapter {
    private static final Dotenv config = Dotenv.configure().load();
    
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        super.onMessageReceived(event);
        int delay = Integer.parseInt(config.get("DELAY"));
        int total = 20;
        String messageReceived = event.getMessage().getContentRaw();
    
        try {
            if(messageReceived.split(" ")[0].equals("Friendly")){
                while(total>=0)
                {
                    if(total == 0){
                        event.getChannel().sendMessage( "Time's Up!! \n\n").completeAfter(delay, TimeUnit.SECONDS);
                        break;
                    }
                    event.getChannel().sendMessage( total +" Hours Left. ⏱️❕ \n\n").completeAfter(delay, TimeUnit.SECONDS);
                    total-=delay;
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}

