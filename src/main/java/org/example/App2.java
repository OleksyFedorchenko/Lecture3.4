package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class App2 {
    public static void main(String[] args) throws IOException, JAXBException {
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        List<Violation> violations = new ArrayList<>();
        List<Violation> temp;
        Map<String, Double> result = new HashMap<>();
        List<String> jsonFileNames = getFileNames();


        //Читаємо усі файли і десеріалізуємо їх в коллекцію об'єктів
        for (String file : jsonFileNames
        ) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                temp = objectMapper.readValue(br, new TypeReference<List<Violation>>() {
                });
                violations.addAll(temp);
            }
        }

        //Робимо мапу з типами без дублікатів
        for (Violation v : violations) {
            result.put(v.getType(), 0.0);
        }

        //Підраховуємо суму для кожного типу порушень,
        // як результат отримуємо мапу з результатами завдання (Тип порушення: Сума штрафу) не сортовану.
        for (String s : result.keySet()) {
            double sum = 0.0;
            for (Violation v : violations) {
                if (s.equals(v.type)) {
                    sum += v.fineAmount;
                }
                result.put(s, sum);
            }
        }

        //Сортуємо результати в порядку спадання суми штрафів.
        Map<String, Double> sortedResultByAmountReverseOrder = sortingResultMap(result);

        //Пишемо в XML файл за допомогою парсера JAXB. Варіант 1.
        writeResultToXmlByJAXB(sortedResultByAmountReverseOrder);

        //Пишемо в XML файл построково. Варіант 2.
        writeResultToXmlByWriteStringLine(sortedResultByAmountReverseOrder);

    }

    //Шукаємо всі json файли в поточному каталозі
    public static List<String> getFileNames() throws IOException {
        try (Stream<Path> walk = Files.walk(Paths.get("."), 1)) {
            return walk
                    .filter(p -> !Files.isDirectory(p))
                    .map(Path::getFileName)
                    .map(p -> p.toString().toLowerCase())
                    .filter(f -> f.endsWith("json"))
                    .collect(Collectors.toList());
        }
    }

    //Метод сортування результатів в порядку спадання суми штрафів.
    public static LinkedHashMap<String, Double> sortingResultMap(Map<String, Double> result) {
        LinkedHashMap<String, Double> sortedResultByAmountReverseOrder = new LinkedHashMap<>();
        ArrayList<Double> list = new ArrayList<>();
        for (Map.Entry<String, Double> entry : result.entrySet()) {
            list.add(entry.getValue());
        }
        list.sort(Collections.reverseOrder());
        for (Double dbl : list) {
            for (Map.Entry<String, Double> entry : result.entrySet()) {
                if (entry.getValue().equals(dbl)) {
                    sortedResultByAmountReverseOrder.put(entry.getKey(), dbl);
                }
            }
        }
        return sortedResultByAmountReverseOrder;
    }

    //Варіант 1. Пишемо в XML файл за допомогою парсера JAXB
    public static void writeResultToXmlByJAXB(Map<String, Double> map) throws JAXBException {
        ViolationsMap violationsMap = new ViolationsMap();
        violationsMap.setViolate(map);
        JAXBContext jaxbContext = JAXBContext.newInstance(ViolationsMap.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(violationsMap, System.out);
        jaxbMarshaller.marshal(violationsMap, new File("parsed_json.xml"));
    }

    //Варіант 2. Пишемо в XML файл построково. Варіант 2.
    public static void writeResultToXmlByWriteStringLine(Map<String, Double> map) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("parsed_json2.xml", true))) {
//            bw.write(String.valueOf(checkTag));
            bw.write("<violates>\n");
            for (Map.Entry<String, Double> mapElement : map.entrySet()) {
                bw.write("\t<violate>\n");
                bw.write("\t\t<name>" + mapElement.getKey() + "</name>\n\t\t<value>" + mapElement.getValue() + "</value\n>");
                bw.write("\t</violate>\n");
            }
            bw.write("</violates>");
        }
    }
}
