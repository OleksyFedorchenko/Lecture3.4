package org.example;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader("in.xml"))) {
            String strCurrentLine;
            Pattern patternForName = Pattern.compile("\\b(name)\\s*=\\s*\"([^\"]*)\"");
            Pattern patternForSurname = Pattern.compile("\\b(surname)\\s*=\\s*\"([^\"]*)\"");
            Pattern patternForXMLTags = Pattern.compile(".*(<[^(><)]+>)\\n");
            Matcher matcher;
            String checkTag = "";
            String saveName = "";
            String saveSurname = "";
            String surnameFull = "";
            while ((strCurrentLine = br.readLine()) != null) {
                //Якщо в прочитаній стрічці є атрибут name, то зберігаємо його.
                matcher = patternForName.matcher(strCurrentLine);
                if (matcher.find()) {
                    saveName = matcher.group(2);
                }
                //Якщо в прочитаній стрічці є атрибут surname, то зберігаємо його.
                matcher = patternForSurname.matcher(strCurrentLine);
                if (matcher.find()) {
                    saveSurname = matcher.group(2);
                    surnameFull = matcher.group(0);
                }
                //Ця змінна буде конкатенувати прочитані стрічки враховуючи перенос стрічки
                // доки в ній не опиниться повний тег.
                checkTag += strCurrentLine + "\n";
                //Якщо в змінній checkTag є повний тег, тобто і відкриваюча і закриваюча дужка,
                // тоді робимо зміни і пишемо в файл.
                matcher = patternForXMLTags.matcher(checkTag);
                if (matcher.matches()) {
                    String result;
                    if (!saveName.equals("") || !saveSurname.equals("")) {
                        result = checkTag.replace(saveName, saveName + " " + saveSurname);
                        result = result.replace(surnameFull, "");
                    } else result = checkTag;
                    try (BufferedWriter bw = new BufferedWriter(new FileWriter("out.xml", true))) {
                        bw.write(result);
                    }
                    checkTag="";
                }
            }
        }
    }
}
