package toys.view;

import toys.interfaces.IView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class ViewConsole implements IView {
    private static final Logger logger = Logger.getLogger(ViewConsole.class.getName());
    private char[] exitLetters;
    private BufferedReader reader = null;
    private InputStreamReader inputStreamReader = null;

    public ViewConsole(String title, String charset, char[] exitLetters) {
        this.exitLetters = exitLetters;
        try {
            inputStreamReader = new InputStreamReader(System.in, charset);
            reader = new BufferedReader(inputStreamReader);
            logger.info("Открыт слушатель консоли System.in");
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
        logger.warning(title);
        StringBuilder sb = new StringBuilder();
        for (char c : exitLetters)
            sb.append(c).append(" ");
        logger.warning("(Для выхода из программы вместо ответа нажмите кнопку: " + sb.toString() + ")\n");
    }

    public void close() {
        logger.info("Перед закрытием консоли System.in");
        try {
            reader.close();
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
        try {
            inputStreamReader.close();
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
    }

    @Override
    public String request(String description) throws ExceptionExit {
        String strInputData = description + " Введите данные >>>";
        //logger.warning(description);
        while (true) {
            logger.warning(strInputData);
            try {
                String s = reader.readLine();
                logger.info("Пользователь ввел: " + s);
                if (s == null || s.length() <= 0 || s.isBlank()) {
                    logger.warning("!!! Ошибка. Получена пустая строка.");
                    continue;
                } else {
                    s = s.trim();
                    if (isExit(s)) {
                        String m = "Получен символ для выхода из программы.";
                        logger.info(m);
                        throw new ExceptionExit(m);
                    }
                    return s;
                }
            } catch (IOException e) {
                logger.info(e.toString());
                logger.warning("Что-то пошло не так. Нужно повторить.");
            }
        }
    }

    @Override
    public void clearScreen() {
        System.out.print("\033[H\033[J");
        logger.info("Очистка экрана.");
    }

    @Override
    public String requestMenu(String description, String[] symbols) throws ExceptionExit {
        logger.warning(description);
        while (true) {
            logger.warning(" Введите данные >>>");
            try {
                String s = reader.readLine();
                logger.info("Пользователь ввел: " + s);
                if (s == null || s.length() <= 0 || s.isBlank()) {
                    logger.warning("!!! Ошибка. Получена пустая строка.");
                    continue;
                } else {
                    s = s.trim();
                    if (isExit(s)) {
                        String m = "Получен символ для выхода из программы.";
                        logger.info(m);
                        throw new ExceptionExit(m);
                    }
                    if (symbols != null) {
                        for (String symbol : symbols)
                            if (s.equals(symbol))
                                return s;
                        logger.warning("!!! Ошибка. Требуется ввести один из пунктов меню.");
                        continue;
                    }
                    return s;
                }
            } catch (IOException e) {
                logger.info(e.toString());
                logger.warning("Что-то пошло не так. Нужно повторить.");
            }
        }

    }

    @Override
    public void viewText(String text) {
        logger.warning(text);
    }

    private boolean isExit(String str) {
        if (str != null && str.length() == 1) {
            char inputChar = str.charAt(0);
            for (char c : exitLetters)
                if (inputChar == c)
                    return true;
        }
        return false;
    }
}
