package ir.nimbo2.nimroo.cooler.database;

import java.rmi.UnexpectedException;

public class UnexpectedSQLBehaviorException extends UnexpectedException {
    public UnexpectedSQLBehaviorException(String s) {
        super(s);
    }
}
