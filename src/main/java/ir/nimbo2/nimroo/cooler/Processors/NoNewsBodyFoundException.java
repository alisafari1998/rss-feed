package ir.nimbo2.nimroo.cooler.Processors;

import java.util.IllegalFormatException;

public class NoNewsBodyFoundException extends Exception {
    public NoNewsBodyFoundException(String msg) {
        super("No NewsBody Found:\n"+msg);
    }
}
