package toys.control;

import toys.interfaces.*;
import toys.util.ExceptionProg;
import toys.util.ExceptionUser;
import toys.view.ExceptionExit;
import toys.interfaces.IView;

import java.io.*;
import java.nio.charset.Charset;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class ControlMenu implements IControl {
    private static final Logger logger = Logger.getLogger(ControlMenu.class.getName());
    static private final String messageStart = "\nПрограмма \"Лотерея игрушек\".";
    static private final String messageFinal = "Работа программы завершена.";
    static final String DATE_FORMAT = "dd.MM.yyyy HH:mm";
    static private final String CREATE = "1"; // TODO надо будет переделать в enum и меню в нем же реализовать
    static private final String EDIT = "2";
    static private final String VIEW = "3";
    static private final String DELETE = "4";
    static private final String SHOW_TOP = "5";
    static private final String LOTTERY = "6";
    static private final String LOTTERY_ALL = "7";
    static private final String PRIZE = "8";
    static private final String STATISTIC = "9";
    static private final String EXIT = "0";

    static private final String[] MENU_SYMBOLS =
            {CREATE, EDIT, VIEW, DELETE, SHOW_TOP, LOTTERY_ALL, LOTTERY, PRIZE, STATISTIC, EXIT};
    static private final String MENU = """
            Меню:
            1 - создать
            2 - редактировать
            3 - отобразить
            4 - удалить
            5 - отобразить список (TOP)
            6 - розыгрыш
            7 - розыгрыш всех игрушек в трех отделах магазина одновременно 
            8 - получить приз
            9 - статистика, баланс
            0 - выход из программы
            """;

/*
    static private final String[] SUBMENU_SYMBOLS = {EXIT};
    static private final String SUBMENU = """
            Дополнительное меню:
            0 - вернуться в главное меню
            """;
*/

    private final IView view; // визуализатор (MCV)
    private final IToys toys; // модель игрушек из лотерейного фонда (MCV)
    private final ILottery lottery; // модель лотереи (и призов) (MCV)
    private final IFactoryToy fToy; // фабрика для создания игрушки
    private final ICSV csv; // для работы с файлами csv
    private final String dbFileToys; // имя файла с базой данных игрушек для фонда лотереи
    private final String dbFilePrizes; // имя файла с базой данных игрушек выигранных, но не отданных призов
    private final String fileGiveOutPrizes; // имя файла со списком отданных призов
    private final String charset; // кодировка файла с базой данных

    public ControlMenu(IView view, IToys toys, ILottery lottery, IFactoryToy fToy, ICSV csv
            , String dbFileToys, String dbFilePrizes, String fileGiveOutPrizes, String charset) {
        this.view = view;
        this.toys = toys;
        this.lottery = lottery;
        this.fToy = fToy;
        this.csv = csv;
        this.dbFileToys = dbFileToys;
        this.dbFilePrizes = dbFilePrizes;
        this.fileGiveOutPrizes = fileGiveOutPrizes;
        this.charset = charset;
    }

    @Override
    public void run() throws ExceptionProg {
        String result = "";
        try {
            while (true) {
                // отобразить меню, потом раздел с ответной информаацией
                view.clearScreen();
                view.viewText(messageStart);
                String s = MENU + "------------------------------\n" + result + "\n------------------------------";
                String symbol = view.requestMenu(s, MENU_SYMBOLS);
                result = "";
                switch (symbol) {
                    case CREATE -> { // добавить игрушку в лотерейный фонд
                        String name = view.request("Наименование игрушки.");
                        if (!toys.isUniqueName(name)) {
                            result = "Ошибка. Игрушка '" + name + "' уже присутствует.";
                            continue;
                        }
                        String qtyS = view.request("Количество экземпляров.");
                        int qty;
                        try {
                            qty = Integer.parseInt(qtyS);
                        } catch (Exception e) {
                            result = "Ошибка. Требуется ввести число.";
                            continue;
                        }
                        if (qty <= 0) {
                            result = "Ошибка. Требуется ввести натуральное число > 0.";
                            continue;
                        }

                        String chanceS = view.request("Шанс выигрыша.");
                        int chance;
                        try {
                            chance = Integer.parseInt(chanceS);
                        } catch (Exception e) {
                            result = "Ошибка. Требуется ввести натуральное число [1..100].";
                            continue;
                        }
                        if (chance <= 0 || chance > 100) {
                            result = "Ошибка. Требуется ввести натуральное число [1..100].";
                            continue;
                        }

                        IToy toy = fToy.create(name, qty, qty, chance);
                        try {
                            toys.add(toy);
                        } catch (ExceptionUser e) {
                            result = e.getMessage();
                            continue;
                        }
                        result = "Добавлена запись: " + toy + "\nСуммарный шанс = " + toys.sumChance() + " %";
                        csv.writeFileCSV(toys.getArrays(), dbFileToys, charset);
                        result = result + "\n\nДанные сохранены в файл " + dbFileToys;
                    }
                    case EDIT -> {  // редактировать шанс
                        String idS = view.request("Идентификатор записи.");
                        int id;
                        try {
                            id = Integer.parseInt(idS);
                        } catch (Exception e) {
                            result = "Ошибка. Требуется ввести число.";
                            continue;
                        }
                        IToy toy = toys.search(id);
                        if (toy == null) {
                            result = "Ошибка. Запись не найдена.";
                            continue;
                        }
                        String chanceS = view.request(
                                "Шанс выигрыша данного вида игрушек в % [1..100].");
                        int chance;
                        try {
                            chance = Integer.parseInt(chanceS);
                        } catch (Exception e) {
                            result = "Ошибка. Требуется ввести натуральное число [1..100].";
                            continue;
                        }
                        if (chance <= 0 || chance > 100) {
                            result = "Ошибка. Требуется ввести натуральное число [1..100].";
                            continue;
                        }
                        try {
                            toys.setChance(toy, chance);
                        } catch (ExceptionUser e) {
                            result = e.getMessage();
                            continue;
                        }
                        result = "Запись отредактирована: " + toy + "\nСуммарный шанс = " + toys.sumChance() + " %";
                        csv.writeFileCSV(toys.getArrays(), dbFileToys, charset);
                        result = result + "\n\nДанные сохранены в файл " + dbFileToys;
                    }
                    case VIEW -> { // вывести полные данные по одной записи об игрушке из лотерейного фонда,
                        // которую нужно указать по номеру
                        String idS = view.request("Идентификатор записи.");
                        int id;
                        try {
                            id = Integer.parseInt(idS);
                        } catch (Exception e) {
                            result = "Ошибка. Требуется ввести число.";
                            continue;
                        }
                        IToy toy = toys.search(id);
                        if (toy == null) {
                            result = "Ошибка. Запись не найдена.";
                            continue;
                        }
                        result = toy.toString();
                    }
                    case DELETE -> { // удалить игрушку из лотерейного фонда
                        String idS = view.request("Идентификатор записи.");
                        int id;
                        try {
                            id = Integer.parseInt(idS);
                        } catch (Exception e) {
                            result = "Ошибка. Требуется ввести число.";
                            continue;
                        }
                        IToy toy = toys.search(id);
                        if (toy == null) {
                            result = "Ошибка. Запись не найдена.";
                            continue;
                        }
                        toys.delete(id);
                        result = "Запись удалена. " + toy + "\nСуммарный шанс = " + toys.sumChance() + " %";
                        csv.writeFileCSV(toys.getArrays(), dbFileToys, charset);
                        result = result + "\n\nДанные сохранены в файл " + dbFileToys;
                    }
                    case SHOW_TOP -> {  // вывести информацию по указанному кол-ву игрушек из лотерейного фонда
                        String qtyS = view.request("Количество записей для отображения.");
                        int qty;
                        try {
                            qty = Integer.parseInt(qtyS);
                        } catch (Exception e) {
                            result = "Ошибка. Требуется ввести число > 0.";
                            continue;
                        }
                        if (qty <= 0) {
                            result = "Ошибка. Требуется ввести число > 0.";
                            continue;
                        }
                        String topList = toys.getTopToys(qty);
                        if (topList == null || topList.isBlank())
                            result = "Нет данных для отображения.";
                        else result = "Суммарный шанс = " + toys.sumChance() + " %\n" + topList;
                    }
                    case LOTTERY -> {  // розыгрыш лотереи
                        String prizeName = lottery.play();
                        if (prizeName == null) {
                            result = "Товары для розыгрыша отсутствуют.";
                            continue;
                        }
                        String emoji = new String(Character.toChars(0x1f3c6));
                        result = emoji + " Приз '" + prizeName + "'.";

                        csv.writeFileCSV(toys.getArrays(), dbFileToys, charset);
                        result = result + "\n\nДанные сохранены в файл " + dbFileToys;
                        csv.writeFileCSV(lottery.getArrays(), dbFilePrizes, charset);
                        result = result + "\nДанные сохранены в файл " + dbFilePrizes;
                    }
                    case LOTTERY_ALL -> {  // розыгрыш лотереи - все игрушек
/*
                        // можно посмотреть реальные шансы
                        int n = toys.sumQty() / 5;
                        while(n-- > 0) {
                            lottery.play();
                        }
*/


                        MyThread myThread1 = new MyThread("t1");
                        MyThread myThread2 = new MyThread("t2");
                        MyThread myThread3 = new MyThread("t3");
                        myThread1.start();
                        myThread2.start();
                        myThread3.start();
                        try {
                            myThread1.join();
                            myThread2.join();
                            myThread3.join();
                        } catch (InterruptedException e) {
                            logger.warning(e.getMessage());
                        }

                        // на всякий случай
                        try { Thread.sleep(100); } catch (InterruptedException e) { throw new RuntimeException(e); }

                        result = "Все игрушки розыграны в лотерею.";
                        csv.writeFileCSV(toys.getArrays(), dbFileToys, charset);
                        result = result + "\n\nДанные сохранены в файл " + dbFileToys;
                        csv.writeFileCSV(lottery.getArrays(), dbFilePrizes, charset);
                        result = result + "\nДанные сохранены в файл " + dbFilePrizes;
                    }
                    case PRIZE -> {  // забрать приз
                        String name_prize = lottery.giveOutPrize();
                        if (name_prize == null || name_prize.isBlank()) {
                            result = "Нет не выданных призов.";
                            continue;
                        } else
                            result = "Выдан приз '" + name_prize + "'.";
                        // FileWriter writer = new FileWriter(fileGiveOutPrizes, true);
                        try (BufferedWriter br = new BufferedWriter(
//                              new OutputStreamWriter(new FileOutputStream(fileGiveOutPrizes), charset))) {
                                new FileWriter(fileGiveOutPrizes, Charset.forName(charset), true))) {
                            br.write(name_prize);
                            br.newLine();
                            br.flush();
                        } catch (Exception e) {
                            throw new ExceptionProg("Ошибка. Сохранение данных в файла " + fileGiveOutPrizes + " (" + e.getMessage() + ") ");
                        }
                        result = result + "\n\nДанные сохранены в файл " + fileGiveOutPrizes;
                        csv.writeFileCSV(lottery.getArrays(), dbFilePrizes, charset);
                        result = result + "\nДанные сохранены в файл " + dbFilePrizes;
                    }
                    case STATISTIC -> {  // статистика, подвести баланс

                        long qtyGiveOut = 0;
                        File file = new File(fileGiveOutPrizes);
                        if (file.exists())
                            try (BufferedReader br = new BufferedReader(
                                    new InputStreamReader(new FileInputStream(fileGiveOutPrizes), charset))) {
                                String str;
                                Stream<String> ss = br.lines();
                                qtyGiveOut = ss.count();
                            } catch (Exception e) {
                                throw new ExceptionProg("Ошибка. Проблема с данными из файла "
                                        + fileGiveOutPrizes + " (" + e.getMessage() + ") ");
                            }

                        int sumChance = toys.sumChance();

                        result = sumChance + " % - Суммарный шанс\n"
                                + toys.sumQtyBegin() + " - Начальное кол-во экземпляров игрушек в фонде\n"
                                + toys.sumQty() + " - Текущее кол-во экземпляров игрушек в фонде\n"
                                + lottery.sumPrizes() + " - Кол-во выигранных призов\n"
                                + qtyGiveOut + " - Кол-во полученных призов";
                        if ((toys.sumQtyBegin() - toys.sumQty() - lottery.sumPrizes() - qtyGiveOut) == 0)
                            result += "\nБаланс сходится.";
                        else
                            result += "\nОшибка!!!!!!!! Баланс НЕ сходится.";

                        result += "\n\n" + toys.analiticChances();
                    }
                    case EXIT -> {  // выход из программы
                        view.viewText(messageFinal);
                        return;
                    }
//                    case SEARCH -> { // отдельное меню для поисковых функций
//                        runSubMenu();
//                    }
                    default -> {
                        logger.warning("Не обработанный пункт меню " + symbol);
                    }
                }
            }
        } catch (ExceptionExit e) {
            view.viewText(messageFinal);
        }
    }


/*
    // подменю, устроено по аналогии с основным
    private void runSubMenu() throws ExceptionProg, ExceptionExit {
        String result = "";
        try {
            while (true) {
                view.clearScreen();
                view.viewText(messageStart);
                String s = SUBMENU + "------------------------------\n" + result + "\n------------------------------";
                String symbol = view.requestMenu(s, SUBMENU_SYMBOLS);
                result = "";
                switch (symbol) {
                    case EXIT -> { // выход из подменю в основное меню
                        return;
                    }
                    default -> {
                        logger.warning("Не обработанный пункт меню " + symbol);
                    }
                }
            }
        } catch (ExceptionExit e) {
            throw new ExceptionExit(e.getMessage());
        }
    }
*/


    class MyThread extends Thread  {
        MyThread(String name) { super(name); }

        public void run() {
            // появляются коллизии когда товар одного типа заканчивается; одна розыгрыш в холостую на каждый вид игрушки
            //StringBuilder sb = new StringBuilder();
            int n = toys.sumQty() / 3 + toys.getListToys().size();
            while(n-- > 0) {
                  lottery.play();
//                для красоты, но задержка больше и коллизий нет
//                StringBuilder sb = new StringBuilder();
//                sb.append(n).append(")").append(getName()).append(" ").append(lottery.play()).append("\n");
//                logger.info(sb.toString());
            }
            //logger.info(sb.toString());
        }
    }


}
