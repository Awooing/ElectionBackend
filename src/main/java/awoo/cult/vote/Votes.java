package awoo.cult.vote;

import awoo.cult.vote.storage.FileConfig;
import awoo.cult.vote.storage.MySQL;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class Votes {

    private JDA jda;
    private FileConfig config;
    private HashMap<String, String> applicants = new HashMap<>();

    private String votes;

    public Votes(FileConfig config) throws Exception {
        this.jda = Awoo.jda;
        this.config = config;

        // Applicants
        applicants.put("toki", config.getProperty("vote.message.toki"));
        applicants.put("rin", config.getProperty("vote.message.rin"));
        applicants.put("vock", config.getProperty("vote.message.vock"));
        applicants.put("spaghett", config.getProperty("vote.message.spaghett"));
        applicants.put("muffin", config.getProperty("vote.message.muffin"));
        applicants.put("vottus", config.getProperty("vote.message.vottus"));

        try {
            votes = prepareVoteJson();
        } catch (IOException e) {
            System.out.println("Couldn't create Vote JSON.");
            System.out.println("Error: " + e.getMessage());
        }

        prepareDb();
    }

    public void update() throws Exception {
        long startTime = System.currentTimeMillis();
        for (String applicant : applicants.keySet()) {
            int votes = getVoteCount(applicants.get(applicant));
            insertApplicant(applicant, votes, Awoo.sql.getConnection());
        }
        votes = prepareVoteJson();
        System.out.println("Updated applicants in " + (System.currentTimeMillis() - startTime) + "ms.");
    }

    private void prepareDb() throws Exception {
        Connection conn = Awoo.sql.getConnection();
        if (!conn.isClosed()) {
            PreparedStatement statement = conn.prepareStatement("create table if not exists `awoo_votes`(`id` int(11) not null primary key auto_increment, `applicant` text not null,`votes` int(11) not null) engine=InnoDB;");
            statement.executeUpdate();

            for (String applicant : applicants.keySet()) {
                insertApplicant(applicant, getVoteCount(applicants.get(applicant)), conn);
            }
        }
    }

    private void insertApplicant(String name, int votes, Connection conn) throws SQLException {
        PreparedStatement statement = conn.prepareStatement("select id from `awoo_votes` where `applicant` = ?");
        statement.setString(1, name);
        ResultSet set = statement.executeQuery();
        if (set.next()) {
            int id = set.getInt("id");
            PreparedStatement update = conn.prepareStatement("update `awoo_votes` set `votes` = ? where `id` = ?");
            update.setInt(1, votes);
            update.setInt(2, id);
            update.executeUpdate();
        } else {
            PreparedStatement insert = conn.prepareStatement("insert into `awoo_votes`(`applicant`, `votes`) values (?, ?)");
            insert.setString(1, name);
            insert.setInt(2, votes);
            insert.executeUpdate();
        }
    }

    public String getVoteJson() {
        if (votes == null) {
            try {
                votes = prepareVoteJson();
            } catch (IOException e) {
                System.out.println("Couldn't create Vote JSON.");
                System.out.println("Error: " + e.getMessage());
            }
        }
        return votes;
    }

    private String prepareVoteJson() throws IOException {
        JsonFactory factory = new JsonFactory();
        StringWriter writer = new StringWriter();
        JsonGenerator generator = factory.createGenerator(writer);
        generator.writeStartObject();
        for (String applicant : applicants.keySet()) {
            generator.writeFieldName(applicant + "Votes");
            generator.writeNumber(getVoteCount(applicants.get(applicant)));
        }
        generator.close();
        return writer.toString();
    }

    public int getVoteCount(String messageId) {
        int voteCount = 0;
        try {
            Message message = jda.getGuildById(config.getProperty("guild.id"))
                    .getTextChannelById(config.getProperty("guild.voteChannelId"))
                    .retrieveMessageById(messageId)
                    .complete();
            for (MessageReaction reaction : message.getReactions()) {
                voteCount = voteCount + reaction.getCount();
            }
        } catch (NullPointerException e) {
            voteCount = 0;
        }
        return voteCount;
    }


}
