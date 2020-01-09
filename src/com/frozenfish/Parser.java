package com.frozenfish;
/**
 * @author Sergei Churkin
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Parser {
    String path;
    String directory;
    Map<String, ArrayList<String>> uniqLinksMap;

    Parser(String p, String d) {
        path = p;
        directory = d;
        uniqLinksMap = new HashMap<>();
    }


    public Map<String, ArrayList<String>> readLinksFromFile() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path))) {
            String string;
            while ((string = bufferedReader.readLine()) != null) {
                if (!string.equals("")) {
                    String[] readingStrings = string.split(" ");
                    ArrayList<String> tmp = new ArrayList<>();
                    if (uniqLinksMap.containsKey(readingStrings[0])) {
                        tmp = uniqLinksMap.get(readingStrings[0]);
                    }
                    tmp.add(directory + File.separator + readingStrings[1]);
                    uniqLinksMap.put(readingStrings[0], tmp);
                }
            }
        } catch (IOException ex) {
            System.out.println("Can't read file with links");
            System.exit(-2);
        }
        return uniqLinksMap;
    }

}
