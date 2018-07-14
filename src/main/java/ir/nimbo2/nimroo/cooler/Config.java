package ir.nimbo2.nimroo.cooler;

public class Config {
    private Config(){}

    //Not final because of testing database.
    public static String DATABASE_NAME = "rssfeed";

    public static final String DATABASE_USER = "root";
    public static final String DATABASE_PASSWORD = "boogh";

    public static final String MY_SQL_CONNECTION_ADDRESS = "jdbc:mysql://localhost/"
            + "?useUnicode=yes&characterEncoding=UTF-8";




}
