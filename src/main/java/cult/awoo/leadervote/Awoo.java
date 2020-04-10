package cult.awoo.leadervote;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.sun.net.httpserver.HttpServer;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.emote.EmoteAddedEvent;
import net.dv8tion.jda.api.events.emote.EmoteRemovedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;


public class Awoo extends ListenerAdapter {

    /** @Awoo Settings **/
    private static String guildId = "206530953391243275";

    /** @Awoo Vote Messages **/
    private static String voteChannelId = "431457496104960000";

    private static String tokiVotes = "697152027200978954";
    private static String rinVotes = "697152076765069442";
    private static String vockVotes = "697152124139601950";
    private static String spaghettVotes = "697152161318043668";
    private static String muffinVotes = "697152207400730714";
    private static String vottusVotes = "697152257271267408";

    /** @Awoo Instance **/
    private static JDA jda;

    /** @Awoo CacheVote **/
    private static String votes;

    public static void main(String[] args) throws RateLimitedException, InterruptedException, LoginException, IOException {
        jda = new JDABuilder(AccountType.BOT)
                .setToken("<owo what's this a token?>")
                .setAutoReconnect(true)
                .addEventListeners(new Awoo()).build();
        launchServer();

        // Wait so JDA doesn't load after I sent the vote fetch (that'll result in NullPointerException).
        Thread.sleep(4000);
        votes = getVotes();


    }

    private static void launchServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 7777), 0);
        server.createContext("/votes", http -> {
            http.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            if (votes == null) {
                votes = getVotes();
            }
            byte [] response = votes.getBytes();
            http.sendResponseHeaders(200, response.length);
            OutputStream os = http.getResponseBody();
            os.write(response);
            os.close();
            System.out.println("Request for /votes @ Response:" + votes);
        });
        server.start();
        System.out.println("OwO api started at 7777");
    }

    private static String getVotes() throws IOException {
        JsonFactory factory = new JsonFactory();
        StringWriter writer = new StringWriter();
        JsonGenerator generator = factory.createGenerator(writer);
        generator.writeStartObject();
        generator.writeFieldName("tokiVotes");
        generator.writeNumber(getVoteCount(tokiVotes));
        generator.writeFieldName("rinVotes");
        generator.writeNumber(getVoteCount(rinVotes));
        generator.writeFieldName("vockVotes");
        generator.writeNumber(getVoteCount(vockVotes));
        generator.writeFieldName("spaghettVotes");
        generator.writeNumber(getVoteCount(spaghettVotes));
        generator.writeFieldName("muffinVotes");
        generator.writeNumber(getVoteCount(muffinVotes));
        generator.writeFieldName("vottusVotes");
        generator.writeNumber(getVoteCount(vottusVotes));
        generator.close();
        return writer.toString();
    }

    @Override
    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
        super.onMessageReactionAdd(event);
        if (event.getGuild().getId().equals(guildId)) {
            if (event.getChannel().getId().equals(voteChannelId)) {
                try {
                    votes = getVotes();
                    System.out.println("Reaction Add" +
                            " Event - Updated Votes");
                    System.out.println("New Values @ " + votes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onMessageReactionRemove(@Nonnull MessageReactionRemoveEvent event) {
        super.onMessageReactionRemove(event);
        if (event.getGuild().getId().equals(guildId)) {
            if (event.getChannel().getId().equals(voteChannelId)) {
                try {
                    votes = getVotes();
                    System.out.println("Reaction Remove Event - Updated Votes");
                    System.out.println("New Values @ " + votes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static int getVoteCount(String messageId) {
        Message message = jda.getGuildById(guildId).getTextChannelById(voteChannelId).retrieveMessageById(messageId).complete();
        int voteCount = 0;
        for (MessageReaction reaction : message.getReactions()) {
            voteCount = voteCount + reaction.getCount();
        }
        return voteCount;
    }


}
