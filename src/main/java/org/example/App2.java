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

        //Пишемо в XML файл за допомогою парсера JAXB.
        writeResultToXmlByJAXB(sortingResultMap(result));

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
        return result.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    //Пишемо в XML файл за допомогою парсера JAXB
    public static void writeResultToXmlByJAXB(Map<String, Double> map) throws JAXBException {
        ViolationsMap violationsMap = new ViolationsMap();
        violationsMap.setViolate(map);
        JAXBContext jaxbContext = JAXBContext.newInstance(ViolationsMap.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(violationsMap, System.out);
        jaxbMarshaller.marshal(violationsMap, new File("parsed_json.xml"));
    }
}
