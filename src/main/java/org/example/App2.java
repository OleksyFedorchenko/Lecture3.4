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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class App2 {
    public static void main(String[] args) throws IOException {
        List<Violation> violations = new ArrayList<>();
        List<Violation> violations1;
        List<String> jsonFileNames;
        //Шукаємо всі json файли в поточному каталозі
        try (Stream<Path> stream = Files.walk(Paths.get("."), 1)) {
            jsonFileNames = stream
                    .filter(p -> !Files.isDirectory(p))
                    .map(Path::getFileName)
                    .map(p -> p.toString().toLowerCase())
                    .filter(f -> f.endsWith("json"))
                    .collect(Collectors.toList());
        }
        System.out.println(jsonFileNames);
        //Читаємо усі файли і десеріалізуємо їх в коллекцію об'єктів
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        for (String file : jsonFileNames
        ) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                violations1 = objectMapper.readValue(br, new TypeReference<List<Violation>>() {
                });
                violations.addAll(violations1);
            }

        }
        System.out.println(violations);
        System.out.println(violations.size());
    }
}
