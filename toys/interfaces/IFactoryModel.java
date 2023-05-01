package toys.interfaces;

import toys.util.ExceptionProg;

import java.util.logging.Logger;

public interface IFactoryModel {
    IToys createToys(String[][] ss) throws ExceptionProg;

    ILottery createLottery(String[][] ss, IToys toys) throws ExceptionProg;

}
