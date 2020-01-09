package com.frozenfish;
/**
 * @author Sergei Churkin
 */
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class Loader implements Runnable {

    private InputStream inputStream;
    private double fileSize;
    final Queue<DataStrings> loadingQueue;
    long timer;


    Loader(Queue<DataStrings> queue, long t) {
        loadingQueue = queue;
        timer = t;
    }


    private DataStrings getDataFromQueue() {
        DataStrings dataStrings = null;
        synchronized (loadingQueue) {
            Iterator<DataStrings> iterator = loadingQueue.iterator();
            if (iterator.hasNext()) {
                dataStrings = iterator.next();
                iterator.remove();
            }
        }
        return dataStrings;
    }

    @Override
    public void run() {
        DataStrings data;
        while ((data = getDataFromQueue()) != null) {
            try {
                URL url = new URL(data.urlString);
                URLConnection connection = url.openConnection();
                fileSize = connection.getContentLength();
                inputStream = connection.getInputStream();
            } catch (IOException e) {
                System.out.println("Error while downloading: " + data.urlString);
            }

            try {

                    Path pathToSave = Path.of(data.fileNames.get(0));
                    if (fileSize != Files.copy(inputStream, pathToSave, StandardCopyOption.REPLACE_EXISTING)) {
                        throw new UnsupportedOperationException("Error while saving: " + data.fileNames.get(0));
                    }
                double fs = fileSize/(1024*1024);
                System.out.printf(data.fileNames.get(0) + " (%.2fMb) скачан за " + TimeUnit.MILLISECONDS.toSeconds((System.currentTimeMillis() - timer)) + " сек.\n", fs);

                if(data.fileNames.size()>1) {
                    Iterator<String> it = data.fileNames.iterator();
                    it.next();
                    while (it.hasNext()){
                        Path pathToSaveOther = Path.of(it.next());
                        Files.copy(pathToSave, pathToSaveOther, StandardCopyOption.REPLACE_EXISTING);
                        System.out.printf(pathToSaveOther + " (%.2fMb) скопирован \n",fs);
                    }

                }
            } catch (UnsupportedOperationException e) {
                System.out.println("Unsupported exception: " + data.fileNames.toString());

            } catch (IOException e) {
                System.out.println("Error while saving");
            }


        }
    }
}




