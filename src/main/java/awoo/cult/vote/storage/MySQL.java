package awoo.cult.vote.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;

public class MySQL {

    private HikariConfig config;
    private HikariDataSource source;
    private Connection con;

    public MySQL(String host, String port, String user, String password, String database) {
        config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        config.setUsername(user);
        config.setPassword(password);
        config.addDataSourceProperty("useSSL", "false");

        try {
            getConnection();
            if (con != null && !con.isClosed()) {
                System.out.println("Connected to MySQL successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PreparedStatement prepareStatement(String paramString) {
        try {
            return this.con.prepareStatement(paramString);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Connection getConnection() throws Exception {
        if (this.source == null) {
            source = new HikariDataSource(config);
        }
        if (this.con == null || this.con.isClosed()) {
            con = source.getConnection();
        }
        return this.con;
    }

    public boolean close() {
        try {
            if (this.con != null) {
                this.con.close();
                return true;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public void update(final String qry) {
        try {
            final Statement st = this.con.createStatement();
            st.executeUpdate(qry);
            st.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet query(final String qry) {
        try {
            final PreparedStatement ps = this.con.prepareStatement(qry);
            return ps.executeQuery();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isConnected() {
        return this.con != null;
    }
}