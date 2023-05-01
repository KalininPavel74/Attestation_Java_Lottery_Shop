package toys.model;

import toys.interfaces.IGenerator;
import toys.interfaces.IToy;
import toys.interfaces.IToys;
import toys.util.ExceptionProg;
import toys.util.ExceptionUser;

import java.util.*;
import java.util.logging.Logger;

public class Toys implements IToys {

    private static final Logger logger = Logger.getLogger(Toys.class.getName());

    private final ArrayList<IToy> toys;
    private final IGenerator generator;

    public Toys(String[][] ss) throws ExceptionProg {
        this.toys = new ArrayList<IToy>();
        if (ss == null || ss.length == 0) {
            this.generator = new Generator(0);
        } else {
            Set<Integer> setForId = new HashSet<Integer>();
            Set<String> setForName = new HashSet<String>();
            int maxId = 0;
            for (String[] arS : ss) {
                try {
                    int id = Integer.parseInt(arS[0]);
                    if (id > maxId) maxId = id;
                    boolean b = setForId.add(id);
                    if (!b) throw new ExceptionProg("В записях присутствуют дубли по номеру. " + id);
                    b = setForName.add(arS[1]);
                    if (!b) throw new ExceptionProg("В записях присутствуют дубли по наименованию. " + id);
                    this.toys.add(FactoryToy.cteateFromDB(id, arS[1]
                            , Integer.parseInt(arS[2]), Integer.parseInt(arS[3]), Integer.parseInt(arS[4]), Long.parseLong(arS[5])));
                } catch (NumberFormatException e) {
                    throw new ExceptionProg("Ошибка в числе записи." + e.getMessage());
                }
            }
            if (setForId.size() != ss.length)
                throw new ExceptionProg("В записях присутствуют дубли по номеру. " + setForId.size() + " <> " + ss.length);
            if (setForName.size() != ss.length)
                throw new ExceptionProg("В записях присутствуют дубли по наименованию. " + setForName.size() + " <> " + ss.length);
            this.generator = new Generator(maxId);
        }

    }

    @Override
    public IGenerator getGenerator() {
        return generator;
    }

    @Override
    public List<IToy> getListToys() {
        return toys;
    }

    public int sumChance() {
        int sum = 0;
        for (IToy t : toys)
            if (t.getQty() > 0) // если остатк 0, то в розыгрыше не участвует и шанс не учитывать
                sum += t.getChance();
        return sum;
    }

    public int sumQty() {
        int sum = 0;
        for (IToy t : toys)
            sum += t.getQty();
        return sum;
    }

    public int sumQtyBegin() {
        int sum = 0;
        for (IToy t : toys)
            sum += t.getQtyBegin();
        return sum;
    }

    @Override
    synchronized public void add(IToy toy) throws ExceptionUser {
        if (!isUniqueName(toy.getName()))
            throw new ExceptionUser("Ошибка. Игрушка '" + toy.getName() + "' уже присутствует.");
        int sum = sumChance();
        if ((sum + toy.getChance()) > 100)
            throw new ExceptionUser("Ошибка. Игрушка не добавлена. Суммарный шанс > 100%. Текущий = " + sum + " %.");
        toys.add(toy);
    }

    public boolean isUniqueName(String name) {
        for (IToy t : toys)
            if (t.getName().equals(name))
                return false;
        return true;
    }

    public void setChance(IToy toy, int chance) throws ExceptionUser {
        int sum = sumChance();
        if ((sum + chance - toy.getChance()) > 100)
            throw new ExceptionUser("Ошибка. Шанс не изменен. Суммарный шанс > 100%. Текущий = " + sum + " %.");
        ((Toy) toy).setChance(chance);
    }

    @Override
    public IToy search(int id) {
        for (IToy toy : toys)
            if (toy.getId() == id) {
                return toy;
            }
        return null;
    }

    @Override
    public String searchToStr(int id) {
        IToy toy = search(id);
        if (toy != null)
            return toy.toString();
        return "Запись не найдена.";
    }

    @Override
    public boolean delete(int id) {
        IToy toy = search(id);
        if (toy != null) {
            toys.remove(toy);
            return true;
        }
        return false;
    }

    @Override
    public String getTopToys(int n) {
        StringBuilder sb = new StringBuilder();
        int count = n;
        for (IToy toy : toys) {
            sb.append(toy.toString()).append("\n");
            if (--count <= 0) break;
        }
        return sb.toString();
    }

    @Override
    public String[][] getArrays() {
        if (toys.size() == 0) return null;

        String[][] ss = new String[toys.size()][6];
        int i = 0;
        for (IToy toy : toys) {
            ss[i][0] = String.valueOf(toy.getId());
            ss[i][1] = String.valueOf(toy.getName());
            ss[i][2] = String.valueOf(toy.getQtyBegin());
            ss[i][3] = String.valueOf(toy.getQty());
            ss[i][4] = String.valueOf(toy.getChance());
            ss[i][5] = String.valueOf(toy.getCreate());
            i++;
        }
        return ss;
    }

    public String analiticChances() {
        int sumChance = sumChance();
        int sumLottery = sumQtyBegin() - sumQty();
        if (sumChance == 0 || sumLottery == 0) return "";
        StringBuilder sbPlan = new StringBuilder();
        StringBuilder sbFact = new StringBuilder();
        sbPlan.append("Шансы план: ");
        sbFact.append("Шансы факт: ");
        for (IToy t : toys) {
            double plan = 100.0 * t.getChance() / sumChance;
            double fact = (100.0 * (t.getQtyBegin() - t.getQty())) / sumLottery;
            sbPlan.append( (int)plan ).append("% ");
            sbFact.append( (int)fact ).append("% ");
        }
        sbPlan.append("\n");
        return sbPlan.toString() + sbFact;
    }


}
