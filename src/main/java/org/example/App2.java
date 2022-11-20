package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class App2 {
    public static void main(String[] args) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        List<Violation> violations = new ArrayList<>();
        List<Violation> temp;
        Map<String,Double> result=new HashMap<>();
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

        System.out.println(violations.size());

        //Робимо мапу з типами без дублікатів
        for(Violation v:violations){
            result.put(v.getType(),0.0);
        }
        System.out.println(result);

        //Підраховуємо суму для кожного типу порушень,
        // як результат отримуємо мапу з результатами завдання (Тип порушення: Сума штрафу).
        for(String s:result.keySet()){
            double sum=0.0;
            for(Violation v:violations){
                if(s.equals(v.type)){
                    sum+=v.fineAmount;
                }
                result.put(s,sum);
            }
        }
        System.out.println(result);
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
}
