package toys.interfaces;

import toys.util.ExceptionProg;

public interface IFactoryToy {
    IToy create(String head, int qtyBegin, int qty, int chance) throws ExceptionProg;
}
