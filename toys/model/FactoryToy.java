package toys.model;

import toys.interfaces.IFactoryToy;
import toys.interfaces.IGenerator;
import toys.interfaces.IToy;
import toys.util.ExceptionProg;

public class FactoryToy implements IFactoryToy {
    private final IGenerator generator;

    public FactoryToy(IGenerator generator) {
        this.generator = generator;
    }
    @Override
    public IToy create(String name, int qtyBegin, int qty, int chance) throws ExceptionProg {
        int id = generator.getNewId();
        return new Toy(id, name, qtyBegin, qty, chance);
    }

    static protected IToy cteateFromDB(int id, String name, int qtyBegin, int qty, int chance, long create) throws ExceptionProg {
        return new Toy(id, name, qtyBegin, qty, chance, create);
    }

}
