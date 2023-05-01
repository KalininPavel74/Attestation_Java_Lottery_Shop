package toys.interfaces;

import toys.util.ExceptionUser;

import java.util.List;
public interface IToys {
    IGenerator getGenerator();
    List<IToy> getListToys();
    void add(IToy toy) throws ExceptionUser;
    IToy search(int id);
    String searchToStr(int id);
    boolean delete(int id);
    String getTopToys(int n);
    String[][] getArrays();
    void setChance(IToy toy, int chance) throws ExceptionUser;
    int sumChance();
    int sumQty();
    int sumQtyBegin();
    boolean isUniqueName(String name);
    String analiticChances();
}
