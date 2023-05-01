package toys.model;

import toys.interfaces.IFactoryModel;
import toys.interfaces.ILottery;
import toys.interfaces.IToys;
import toys.util.ExceptionProg;

public class FactoryModel implements IFactoryModel {
    @Override
    public IToys createToys(String[][] ss) throws ExceptionProg {
        return new Toys(ss);
    }

    @Override
    public ILottery createLottery(String[][] ss, IToys toys) throws ExceptionProg {
        return new Lottery(ss, toys);
    }
}
