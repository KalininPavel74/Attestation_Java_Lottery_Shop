package toys.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class MyLog {

    private static final Logger logger = Logger.getLogger(MyLog.class.getName());
    static public final String LOG_FILE = "log.txt";
    static public final String CHARSET_FILE = "UTF-8";
    static public String CHARSET_CONSOLE = "UTF-8";//"CP866";


    // Инициализация логера
    public static void loggerInit(String aFileName) {
        FileHandler fh = null;
        try {
            fh = new FileHandler(aFileName, true);
        } catch (Exception e) {
            System.out.println("Проблемы с файлом "+aFileName+" "+e.getMessage());
        }
        if(fh == null) System.exit(0);
        try {
            fh.setEncoding(CHARSET_FILE);
        } catch (Exception e) {
            System.out.println("Проблемы с кодировкой FileHandler "+e.getMessage());
        }
        fh.setLevel(Level.INFO); // все что ниже INFO не работает, зараза.
        //fh.setLevel(Level.FINE);
        //fh.setLevel(Level.WARNING);

        Logger lg = LogManager.getLogManager().getLogger("");
        lg.addHandler(fh);

//        SimpleFormatter sFormat = new SimpleFormatter();
//        fh.setFormatter(sFormat);
        fh.setFormatter(withoutRipplesInTheEyesFormatter);

        // Изменение консольного логера, которые уже создан по умолчанию
        for (Handler h : LogManager.getLogManager().getLogger("").getHandlers()) {
            if (h instanceof ConsoleHandler) {
                if (h.getFormatter() == null || !(h.getFormatter() instanceof EmptyFormatter)) {
                    h.setFormatter(emptyFormatter);
                    try {
                        h.setEncoding(CHARSET_CONSOLE);
                        h.setLevel(Level.WARNING); // все что ниже INFO не работает, зараза.
                        //h.setLevel(Level.INFO);
                    } catch (Exception e) {
                        System.out.println("Проблемы с кодировкой ConsoleHandler "+e.getMessage());
                    }
                    //break;
                }
            }
        }
/*      // он там по умолчанию создан
        ConsoleHandler ch = new ConsoleHandler();
        ch.setFormatter(sFormat);
        try {
            ch.setEncoding(CHARSET_CONSOLE);
        } catch (Exception e) {
           System.out.println("Проблемы с кодировкой ConsoleHandler "+e.getMessage());
        }
        logger.addHandler(ch);
*/
        logger.info("\t\t\n\n------------------------------------------------------------\n");
        logger.info("\t\tЛогирование инициализировано");
    }

    // Создание пустого формата для консоли
    static class EmptyFormatter extends Formatter {
        String lineSeparator = System.getProperty("line.separator");
        @Override public synchronized String format(LogRecord record) {
            return formatMessage(record)+lineSeparator;
        }
    }
    static EmptyFormatter emptyFormatter = new EmptyFormatter();

    static final Date dat = new Date();

    // Создание формата для файла лога, чтобы не рябило в глазах.
    static class WithoutRipplesInTheEyesFormatter extends Formatter {
        String lineSeparator = System.getProperty("line.separator");
        String yyyy_MM_dd_HH_mm_ss = "yyyy.MM.dd HH:mm:ss";
        @Override public synchronized String format(LogRecord record) {
            dat.setTime(record.getMillis());
            String dateStr = (new SimpleDateFormat( yyyy_MM_dd_HH_mm_ss )).format(dat);
            String message = formatMessage(record);
            String throwable = "";
            if (record.getThrown() != null) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                pw.print("");
                record.getThrown().printStackTrace(pw);
                pw.close();
                throwable = sw.toString();
            }
            StringBuilder sb = new StringBuilder();
            sb.append(dateStr)
                    .append(" ")
                    .append(((record.getSourceClassName() != null) ? record.getSourceClassName() : record.getLoggerName()))
                    .append(" ")
                    .append(((record.getSourceMethodName() != null) ? record.getSourceMethodName() : ""))
                    .append("\t")
                    .append(record.getLevel().getName())
                    .append("  ")
                    .append(message)
                    .append(throwable)
                    .append(lineSeparator);
            return sb.toString();
        }
    }
    static WithoutRipplesInTheEyesFormatter withoutRipplesInTheEyesFormatter
            = new WithoutRipplesInTheEyesFormatter();

}
