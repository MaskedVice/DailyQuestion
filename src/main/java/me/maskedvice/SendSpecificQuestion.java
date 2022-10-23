package me.maskedvice;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * SendSpecificQuestion
 */
public class SendSpecificQuestion extends ListenerAdapter {
    private static final Dotenv config = Dotenv.configure().load();

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event)
    {
        try {
            if(event.getName().equals("get-ques"))
            {
                String topic =  event.getOption("topic") == null ? "" : event.getOption("topic").getAsString();
                String difficulty = event.getOption("difficulty") == null ? "" : event.getOption("difficulty").getAsString();

                Questions toSend = getQuestionToSend(topic,difficulty);

                String message = getMessagetoSend(toSend,event);

                toSend.isUsed = true;

                event.reply(message).queue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getMessagetoSend(Questions toSend,SlashCommandInteractionEvent event) {
        String message = config.get("SPECIFICQUESTION");
        String ques = toSend.FrontendQuestionId + ": " + toSend.Title + "\n" + config.get("LEETCODEURL") + toSend.TitleSlug;
        return java.text.MessageFormat.format(message,event.getUser().getAsMention(),"\n" ,ques);
    }

    private Questions getQuestionToSend(String topic, String difficulty) {
        List<Questions> res = null;
        try {
            List<Questions> questionPool = App.qList;
            res = questionPool
                .stream()
                .filter(q-> 
                    q.TopicTags.stream().anyMatch(i -> i.equalsIgnoreCase(topic)) &&
                    q.isUsed == false &&
                    q.isPaidOnly.equalsIgnoreCase("False") &&
                    (difficulty == ""? true : q.Difficulty.equalsIgnoreCase(difficulty))                   
                ).toList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res != null ? res.get(new Random().nextInt(res.size())) : null;
    }

    @Override
    public void onGuildReady(@Nonnull GuildReadyEvent event) {
        super.onGuildReady(event);
        List<CommandData> cd = new ArrayList<>();

        OptionData od = new OptionData(OptionType.STRING, "topic", "Enter topic name for question related to that topic",true);
        OptionData od2 = new OptionData(OptionType.STRING, "difficulty", "Enter difficulty of Question required.");

        cd.add(Commands.slash("get-ques", "Get Specific Question").addOptions(od,od2));
        event.getGuild().updateCommands().addCommands(cd).queue();
    }

    
}