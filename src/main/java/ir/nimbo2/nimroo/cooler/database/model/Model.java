package ir.nimbo2.nimroo.cooler.database.model;

public abstract class Model {
    protected final String modelName;

    protected Model(String modelName) {
        this.modelName = modelName;
    }

    public abstract boolean insert();
    public abstract boolean update();
    public abstract boolean load();

}
