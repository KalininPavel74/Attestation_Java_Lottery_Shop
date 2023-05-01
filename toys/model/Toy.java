package toys.model;

import toys.interfaces.IToy;
import toys.util.ExceptionProg;
import toys.util.ExceptionUser;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Toy implements IToy {
    static final String DATE_FORMAT = "dd.MM.yyyy HH:mm:ss";
    private int id;
    private String name;
    private int qty;
    private int chance;
    private long create;
    private int qtyBegin;

    public Toy(int id, String name, int qtyBegin, int qty, int chance) throws ExceptionProg {
        this(id, name, qtyBegin, qty, chance, new Date().getTime());
    }

    protected Toy(int id, String name, int qtyBegin, int qty, int chance, long create) throws ExceptionProg {
        this.id = id;
        this.name = name;
        this.qtyBegin = qtyBegin;
        this.qty = qty;
        this.chance = chance;
        this.create = create;
        if(id<=0 || name == null || name.isBlank() || qtyBegin<=0 || qty<=0 || chance <= 0 || create <=0)
            throw new ExceptionProg("Неправильные данные для игрушки "+this.toString());
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getChance() {
        return chance;
    }

    protected void setChance(int chance) {
        this.chance = chance;
    }

    @Override
    public long getCreate() {
        return create;
    }

    @Override
    public int getQty() {
        return qty;
    }

    @Override
    synchronized public boolean spendOne() {
        if (qty > 0) {
            qty--;
            return true;
        }
        return false;
    }

    @Override
    public int getQtyBegin() {
        return qtyBegin;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("№ ").append(id).append("; \t");
        sb.append("шанс ").append(chance).append(" %; \t");
        sb.append("кол-во ").append(qty).append("/").append(qtyBegin).append(" шт.; \t");
        sb.append(name).append("; \t");
        sb.append((new SimpleDateFormat(DATE_FORMAT)).format(create));
        return sb.toString();
    }
}
