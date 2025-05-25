package mvcstockesystem.c;

public class DBConfig {
    public String username;
    public String password;
    public String databaseName;

    public DBConfig(String username, String password, String databaseName) {
        this.username = username;
        this.password = password;
        this.databaseName = databaseName;
    }

    public String getJdbcUrl() {
        return "jdbc:mysql://localhost:3306/" + databaseName;
    }
}
