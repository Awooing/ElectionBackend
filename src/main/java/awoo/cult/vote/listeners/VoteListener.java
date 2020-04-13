package awoo.cult.vote.listeners;

import awoo.cult.vote.Awoo;
import awoo.cult.vote.Votes;
import awoo.cult.vote.storage.FileConfig;
import awoo.cult.vote.storage.MySQL;
import net.dv8tion.jda.api.events.message.react.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class VoteListener extends ListenerAdapter {

    private FileConfig config;
    private Votes votes;
    private MySQL sql;

    public VoteListener() {
        this.config = Awoo.config;
        this.votes = Awoo.votes;
        this.sql = Awoo.sql;
    }

    @Override
    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
        super.onMessageReactionAdd(event);
        if (event.getGuild().getId().equals(config.getProperty("guild.id"))) {
            if (event.getChannel().getId().equals(config.getProperty("guild.voteChannelId"))) {
                Awoo.update();
            }
        }
    }

    @Override
    public void onMessageReactionRemove(@Nonnull MessageReactionRemoveEvent event) {
        super.onMessageReactionRemove(event);
        if (event.getGuild().getId().equals(config.getProperty("guild.id"))) {
            if (event.getChannel().getId().equals(config.getProperty("guild.voteChannelId"))) {
                Awoo.update();
            }
        }
    }

}
