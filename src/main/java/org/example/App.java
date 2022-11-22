package org.example;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader("in.xml"))) {
            String strCurrentLine;
            Pattern patternForName = Pattern.compile("\\b(name)\\s*=\\s*\"([^\"]*)\"");
            Pattern patternForSurname = Pattern.compile("\\b(surname)\\s*=\\s*\"([^\"]*)\"");
            Pattern patternForXMLTags = Pattern.compile(".*(<[^(><)]+>)\\n");
            Matcher matcher;
            StringBuilder checkTag = new StringBuilder();
            StringBuilder saveName = new StringBuilder();
            StringBuilder saveSurname = new StringBuilder();
            StringBuilder surnameFull = new StringBuilder();
            while ((strCurrentLine = br.readLine()) != null) {
                //Якщо в прочитаній стрічці є атрибут name, то зберігаємо його.
                matcher = patternForName.matcher(strCurrentLine);
                if (matcher.find()) saveName.append(matcher.group(2));
                //Якщо в прочитаній стрічці є атрибут surname, то зберігаємо його.
                matcher = patternForSurname.matcher(strCurrentLine);
                if (matcher.find()) {
                    saveSurname.append(matcher.group(2));
                    surnameFull.append(matcher.group(0));
                }
                //Ця змінна буде конкатенувати прочитані стрічки враховуючи перенос стрічки
                // доки в ній не опиниться повний тег.
                checkTag.append(strCurrentLine).append("\n");
                //Якщо в змінній checkTag є повний тег, тобто і відкриваюча і закриваюча дужка,
                // тоді робимо зміни і пишемо в файл.
                matcher = patternForXMLTags.matcher(checkTag);
                if (matcher.matches()) {
                    if (!String.valueOf(saveName).equals("") || !String.valueOf(saveSurname).equals("")) {
                        int start = checkTag.indexOf(String.valueOf(saveName));
                        int end = start + saveName.length();
                        checkTag.replace(start, end, saveName + " " + saveSurname);
                        start = checkTag.indexOf(String.valueOf(surnameFull));
                        end = start + surnameFull.length();
                        checkTag.replace(start, end, "");
                    }
                    try (BufferedWriter bw = new BufferedWriter(new FileWriter("out.xml", true))) {
                        bw.write(String.valueOf(checkTag));
                    }
                    checkTag.setLength(0);
                    saveName.setLength(0);
                    saveSurname.setLength(0);
                    surnameFull.setLength(0);
                }
            }
        }
    }
}
