package toys.model;

import toys.interfaces.IGenerator;

public class Generator implements IGenerator {
    private Integer maxId;
    public Generator(int begin) {
        this.maxId = begin;
    }
    synchronized public int getNewId() {
        return ++maxId;
    }

}
