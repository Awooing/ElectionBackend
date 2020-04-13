package awoo.cult.vote;


import awoo.cult.vote.handlers.*;
import awoo.cult.vote.listeners.VoteListener;
import awoo.cult.vote.storage.*;
import net.dv8tion.jda.api.*;
import java.io.*;
import com.sun.net.httpserver.HttpServer;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import javax.security.auth.login.LoginException;
import java.net.InetSocketAddress;

public class Awoo extends ListenerAdapter {

    /** -->Awoo Instances **/
    public static JDA jda;
    public static HttpServer server;

    /** -->Awoo Storage **/
    public static FileConfig config;
    public static MySQL sql;

    /** -->Awoo Voting **/
    public static Votes votes;

    public static void main(String[] args) throws Exception {
        // Config Init
        config = new FileConfig("app.properties");

        // SQL Init
        sql = new MySQL(config.getProperty("database.address"),
                config.getProperty("database.port"),
                config.getProperty("database.user"),
                config.getProperty("database.pass"),
                config.getProperty("database.dbname"));

        // JDA Init
        jda = new JDABuilder(AccountType.BOT)
                .setToken(config.getProperty("bot.token"))
                .addEventListeners(new VoteListener())
                .setAutoReconnect(true).build();
        Thread.sleep(4000); // timeout so jda doesn't throw a NullPointerException

        // Votes Init
        votes = new Votes(config);

        // WebServer Startup
        server = HttpServer.create(new InetSocketAddress(config.getProperty("server.address"), Integer.parseInt(config.getProperty("server.port"))), 0);
        server.createContext("/votes", new VotesHandler(votes));
        server.start();
        System.out.println("OwO WebServer started at " + server.getAddress());
    }

    public static void update() {
        try {
            votes.update();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
