package toys.util;

import toys.interfaces.ICSV;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Logger;

public class CSV implements ICSV {

    static public final String SEPARATOR = ";";
    static public final String REPLACEMENT = "REPLACEMENT_OF_SEPARATOR";
    private static final Logger logger = Logger.getLogger(CSV.class.getName());

    @Override
    public String[][] readFileCSV(String fileName, String charset) throws ExceptionProg {
        if (fileName == null || fileName.isBlank())
            throw new ExceptionProg("Ошибка. Не указан CSV файл.");
        File file = new File(fileName);
        if (!file.exists()) return null;
        ArrayList<String> arrayList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(fileName), charset))) {
            String str;
            while ((str = br.readLine()) != null) {
                if (!str.isBlank())
                    arrayList.add(str);
            }
        } catch (Exception e) {
            throw new ExceptionProg("Ошибка. Проблема с данными из файла " + fileName + " (" + e.getMessage() + ") ");
        }
        if (arrayList.size() > 0) {
            int qtyLines = arrayList.size();
            int qtyColumns = arrayList.get(0).split(";").length;
            String[][] ss = new String[qtyLines][qtyColumns];
            for (int i = 0; i < qtyLines; i++) {
                String[] arS = arrayList.get(i).split(";");
                if (arS.length == qtyColumns) {
                    for (int j = 0; j < arS.length; j++)
                        arS[j] = arS[j].replace(REPLACEMENT, SEPARATOR);
                    ss[i] = arS;
                } else {
                    for (String s : arS)
                        logger.warning(s);
                    throw new ExceptionProg("Ошибка. Проблема с данными из файла " + fileName
                            + " (разное кол-во колонок " + arS.length + " <> " + qtyColumns + " ) ");
                }
            }
            return ss;
        }
        return null;
    }

    @Override
    public void writeFileCSV(String[][] ss, String fileName, String charset) throws ExceptionProg {
        if (fileName == null || fileName.isBlank())
            throw new ExceptionProg("Ошибка. Не указан CSV файл.");
        try (BufferedWriter br = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(fileName), charset))) {
            if (ss == null || ss.length == 0) {
                br.write("");
            } else {
                for (String[] arS : ss) {
                    StringBuilder sb = new StringBuilder();
                    for (String s : arS) {
                        sb.append(s.replace(SEPARATOR, REPLACEMENT)).append(SEPARATOR);    // и возврат каретки отработать
                    }
                    sb.delete(sb.length() - SEPARATOR.length(), sb.length());
                    br.write(sb.toString());
                    br.newLine();
                }
            }
            br.flush();
        } catch (Exception e) {
            throw new ExceptionProg("Ошибка. Сохранение данных в файла " + fileName + " (" + e.getMessage() + ") ");
        }
    }

}
