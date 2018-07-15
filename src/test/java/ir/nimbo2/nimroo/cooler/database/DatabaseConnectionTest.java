package ir.nimbo2.nimroo.cooler.database;

import ir.nimbo2.nimroo.cooler.Config;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class DatabaseConnectionTest {

    @Test
    public void dbAccessTest() throws SQLException {
        DatabaseConnection db = new DatabaseConnection();
        try {
          db.getConnection().createStatement();
        } catch (SQLException e) {
          e.printStackTrace();
          assert false;
        } catch (Exception e) {
          e.printStackTrace();
          assert false;
        }
        finally{
            db.getConnection().close();
        }

        assert true;
    }

    @Test
    public void createDatabaseTest() throws SQLException {
        DatabaseConnection db = new DatabaseConnection();
        try {
            db.init();

            Statement st =  new DatabaseConnection().getConnection().createStatement();
            ResultSet res = st.executeQuery("SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '"+ Config.DATABASE_NAME +"'");
            res.next();
            assertEquals(res.getString("SCHEMA_NAME"), Config.DATABASE_NAME);

        } catch (Exception e) {
          e.printStackTrace();
          assertFalse(true);
        }
        finally{
            db.getConnection().close();
        }
    }

}
