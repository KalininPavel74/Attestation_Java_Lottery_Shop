package toys.interfaces;

public interface IToy {
    int getId();

    String getName();

    int getQty(); // текущее кол-во игрушек одного вида, которое уменьшается после розыгрыша

    boolean spendOne();

    // изначальное кол-во игрушек одного вида, чтобы можно было баланс проверить
    // для одного вида игрушек кол-во изменить нельзя, нужно завести новый вид с новым названием (..2)
    // иначе это превратится в 1С
    int getQtyBegin();

    int getChance();

    long getCreate();
}
