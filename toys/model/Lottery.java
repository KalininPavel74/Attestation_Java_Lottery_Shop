package toys.model;

import toys.interfaces.ILottery;
import toys.interfaces.IToy;
import toys.interfaces.IToys;
import toys.util.ExceptionProg;

import java.util.*;
import java.util.logging.Logger;

public class Lottery implements ILottery {

    private static final Logger logger = Logger.getLogger(Lottery.class.getName());
    private final List<String> prizes;
    private final IToys toys;

    public Lottery(String[][] ss, IToys toys) throws ExceptionProg {
        this.prizes = new LinkedList<String>();
        this.toys = toys;

        if (ss != null && ss.length > 0) {
            for (String[] arS : ss) {
                this.prizes.add(arS[0]);
            }
        }
    }

    public String getPrizes() {
        return prizes.toString();
    }

    public String giveOutPrize() {
        if(prizes.size()>0)
            return prizes.remove(0);
        return null;
    }

    public int sumPrizes() {
        return prizes.size();
    }

    public String play() {
        List<IToy> list1 = toys.getListToys();
        List<IToy> list = new LinkedList<>();
        for(IToy t: list1)
            if(t.getQty()>0)
                list.add(t);
        int qtyTypeOfToys = list.size();
        if(qtyTypeOfToys==0) // список товаров для розыгрыша пустой
            return null;
        Random random = new Random();
        int i = 0;
        while (i++ < qtyTypeOfToys) { // страховка, если будут параллельные розыгрыши в разных отделах магазина
            int sumChance = toys.sumChance();
            if(sumChance==0) // товар для розыгрыша закончился
                return null;
            int n = random.nextInt(sumChance); // товар может закончится, но соотношение шансов не изменится
            n++; // сдвинуть диапазон [1..
            logger.info("Суммарный шанс = "+sumChance+". Выигрышное число "+n);
            int sum = 0;
            for (IToy toy : list) {
                // если "выпало" число входящее в диапазон шанса текущей игрушки
                if ( n <= (sum + toy.getChance()) ) {
                    // если текущая игрушка еще не разыгали в соседнем отделе
                    if (toy.spendOne()) {
                        prizes.add(toy.getName());
                        return toy.getName();
                    } else {
                        // остаток нулевой - розыгрыш не удался - переиграть
                        logger.info("Во время розыгыша '" + toy.getName() + "' неожиданно закончился.");
                        break;
                    }
                }
                sum += toy.getChance();
            }
        }
        return null;
    }

    @Override
    public String[][] getArrays() {
        if (prizes.size() == 0) return null;

        String[][] ss = new String[prizes.size()][1];
        int i = 0;
        for (String s : prizes) {
            ss[i][0] = s;
            i++;
        }
        return ss;
    }

}
