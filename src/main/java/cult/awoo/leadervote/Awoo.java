package cult.awoo.leadervote;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.sun.net.httpserver.HttpServer;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.InetSocketAddress;


public class Awoo extends ListenerAdapter {

    /** @Awoo Settings **/
    private static String guildId = "694136860640804904";
    private static String welcomeChannelId = "697550586698006622";

    /** @Awoo Vote Messages **/
    private static String voteChannelId = "697550586698006622";

    private static String tokiVotes = "697551873678377090";
    private static String rinVotes = "697551873678377090";
    private static String vockVotes = "697551873678377090";
    private static String spaghettVotes = "697551873678377090";
    private static String muffinVotes = "697551873678377090";
    private static String vottusVotes = "697551873678377090";

    /** @Awoo Instance **/
    private static JDA jda;

    public static void main(String[] args) throws RateLimitedException, InterruptedException, LoginException, IOException {
        jda = new JDABuilder(AccountType.BOT)
                .setToken("Njk3NTQ5NjQ1ODQ4NzcyNjU4.Xo46AQ.iE-YXyZRMAOx2jzNE4yAswNzqfc")
                .setAutoReconnect(true)
                .addEventListeners(new Awoo()).build();
        launchServer();

    }

    private static void launchServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8001), 0);
        server.createContext("/api/votes", http -> {
            http.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            byte [] response = getVotes().getBytes();
            http.sendResponseHeaders(200, response.length);
            OutputStream os = http.getResponseBody();
            os.write(response);
            os.close();
        });
        server.start();
        System.out.println("OwO api started at 8001");
    }

    private static String getVotes() throws IOException {
        JsonFactory factory = new JsonFactory();
        StringWriter writer = new StringWriter();
        JsonGenerator generator = factory.createGenerator(writer);
        generator.writeStartObject();
        generator.writeFieldName("tokiVotes");
        generator.writeNumber(getVoteCount(tokiVotes));
        //generator.writeNumber(10);
        generator.writeFieldName("rinVotes");
        generator.writeNumber(getVoteCount(rinVotes));
        //generator.writeNumber(8);
        generator.writeFieldName("vockVotes");
        generator.writeNumber(getVoteCount(vockVotes));
        //generator.writeNumber(8);
        generator.writeFieldName("spaghettVotes");
        generator.writeNumber(getVoteCount(spaghettVotes));
        //generator.writeNumber(12);
        generator.writeFieldName("muffinVotes");
        generator.writeNumber(getVoteCount(muffinVotes));
        //generator.writeNumber(3);
        generator.writeFieldName("vottusVotes");
        generator.writeNumber(getVoteCount(vottusVotes));
        //generator.writeNumber(9);
        generator.close();
        return writer.toString();
    }

    private static int getVoteCount(String messageId) {
        return jda.getGuildById(guildId).getTextChannelById(voteChannelId).retrieveMessageById(messageId).complete().getReactions().size();
    }
}
