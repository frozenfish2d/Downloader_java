package com.frozenfish;
/**
 * @author Sergei Churkin
 */

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class Main {

    public static void main(String[] args) {
        long time = System.currentTimeMillis();
        Path directory = Paths.get(args[1]).toAbsolutePath();
        Map<String, ArrayList<String>> uniqMap;

        if (Files.notExists(directory)) {
            try {
                Files.createDirectory(directory);
            } catch (IOException e) {
                System.out.println("Can't create directory " + directory);
                System.exit(-2);
            }
        }
        Queue<Loader> queue = new LinkedList<>();
        Parser parser = new Parser(args[2], directory.toString());
        uniqMap = parser.readLinksFromFile();
        Queue<DataStrings> dataStringsQueue = new LinkedList<>();

        for (Map.Entry<String, ArrayList<String>> entry : uniqMap.entrySet()) {
            dataStringsQueue.add(new DataStrings(entry.getKey(), entry.getValue()));
            queue.add(new Loader(dataStringsQueue, time));
        }

        if (!queue.isEmpty()) {
            ExecutorService exec = Executors.newFixedThreadPool(Integer.parseInt(args[0]));
            for (int i = 0; i <= Integer.parseInt(args[0]); i++) {
                exec.execute(Objects.requireNonNull(queue.remove()));
            }
            exec.shutdown();

            if (exec.isShutdown()) {
                System.out.println("Общее время выполнения: " + TimeUnit.MILLISECONDS.toSeconds((System.currentTimeMillis() - time)));
            }
        }


    }
}
