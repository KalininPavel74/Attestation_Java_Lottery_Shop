package toys;

import toys.control.FactoryControlMenu;
import toys.interfaces.*;
import toys.model.FactoryModel;
import toys.model.FactoryToy;
import toys.util.ExceptionProg;
import toys.util.FactoryUtil;
import toys.util.MyLog;
import toys.view.FactoryView;

import java.util.logging.Logger;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private static final String DB_FILE_TOYS = "toys.csv"; // Файл с базой данных игрушек из лотерейного фонда.
    private static final String DB_FILE_PRIZES = "prizes.csv"; // Файл с базой данных призов.
    private static final String FILE_GIVE_OUT_PRIZES = "give_out.txt"; // Файл со списком выданных призов.
    private static final String CHARSET = "UTF-8"; // Кодировка файла с базой данных.

    public static void main(String[] args) throws ExceptionProg {
        MyLog.loggerInit(MyLog.LOG_FILE); // логирование

        try {
            // Фабрика для объектов утилит. Для доступа к классу для работы с файлами csv
            IFactoryUtil fUtil = new FactoryUtil();
            ICSV csv = fUtil.create();
            String[][] ssToys = csv.readFileCSV(DB_FILE_TOYS, CHARSET);
            String[][] ssPrizes = csv.readFileCSV(DB_FILE_PRIZES, CHARSET);

            // Фабрика для получения объекта Игрушка и Лотерея - модель (MCV)
            IFactoryModel fModel = new FactoryModel();
            IToys toys = fModel.createToys(ssToys);
            ILottery lottery = fModel.createLottery(ssPrizes, toys);
            // Фабрика для получения объекта Игрушка
            // отделена, для того чтобы передать ее в контроллер (MCV)
            // Контроллер будет создават игрушки через фабрику.
            IFactoryToy fToy = new FactoryToy(toys.getGenerator());

            // Фабрика для получения объекта визуализации (MCV)
            IFactoryView fv = new FactoryView();
            // Выход из программы по кнопке q или из меню контроллера.
            IView view = fv.create("", MyLog.CHARSET_CONSOLE, new char[]{'Q', 'q'});

            // Фабрика для получения объекта контроллер (MCV)
            IFactoryControl fc = new FactoryControlMenu();
            IControl control = fc.create(view, toys, lottery, fToy, csv
                    , DB_FILE_TOYS, DB_FILE_PRIZES, FILE_GIVE_OUT_PRIZES, CHARSET);

            control.run(); // запуск цикла работы

        } catch (ExceptionProg e) {
            logger.warning(e.getMessage());
            for (StackTraceElement ob : e.getStackTrace())
                logger.info(ob.toString());
            throw e;
        }
    }

}