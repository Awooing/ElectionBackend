package awoo.cult.vote.handlers;

import awoo.cult.vote.Votes;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class VotesHandler implements HttpHandler {

    private Votes votes;

    public VotesHandler(Votes votes) {
        this.votes = votes;
    }

    @Override
    public void handle(HttpExchange http) throws IOException {
        http.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        byte [] response = votes.getVoteJson().getBytes();
        http.sendResponseHeaders(200, response.length);
        OutputStream os = http.getResponseBody();
        os.write(response);
        os.close();
        System.out.println("Request for /votes @ Response:" + votes);
    }
}
