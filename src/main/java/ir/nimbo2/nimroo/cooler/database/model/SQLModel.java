package ir.nimbo2.nimroo.cooler.database.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class SQLModel extends Model {

    protected PreparedStatement createTablePS;

    protected SQLModel(String tableName) {
        super(tableName);
    }

    public abstract boolean init(Connection connection);

    public boolean createTable() {

        try {
            createTablePS.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Problem with creating table: " + modelName);
            return false;
        }

        return true;
    };

}
