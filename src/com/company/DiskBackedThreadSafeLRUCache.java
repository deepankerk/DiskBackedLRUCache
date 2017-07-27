package com.company;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

public class DiskBackedThreadSafeLRUCache<K, V> {

    private final Map<K,String> cache;
    private final String directory;

    public static DiskBackedThreadSafeLRUCache open(final int maxEntries) {
        File cacheDir = new File("Cache");
        cacheDir.mkdir(); //Doesn't create if already exists.
        DiskBackedThreadSafeLRUCache cache =  new DiskBackedThreadSafeLRUCache(Paths.get("Cache").toAbsolutePath().toString(), maxEntries);
        return cache;
    }

    private DiskBackedThreadSafeLRUCache(final String directory, final int maxEntries) {
        this.cache = new LinkedHashMap<K, String>(maxEntries, 0.75F, true) {
            private static final long serialVersionUID = -1236481390177598762L;
            @Override
            protected boolean removeEldestEntry(Map.Entry<K,String> eldest){
                if(size() > maxEntries) {
                    try {
                        return Files.deleteIfExists(FileSystems.getDefault().getPath(directory, String.valueOf(eldest.getKey().hashCode())));
                    }
                    catch(IOException e) {
                        // Exception tells us that there was a problem deleting the file. But we still delete the entry from map so that user can't access the stale values.
                        return true;
                    }
                }
                return false;
            }
        };
        this.directory = directory;
    }

    public void put(K key, V value) {
        String fileName = directory + "/" + key.hashCode();
        try {
            FileOutputStream fileOut = new FileOutputStream(fileName);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);

            out.writeObject(value);
            // If the object has been successfully serialized then only put it in cache.
            cache.put(key, fileName);

            out.close();
            fileOut.close();
        } catch (IOException e) {

        }
    }

    public synchronized V get(K key) {
        if(!cache.containsKey(key))
            return null;
        try {
            FileInputStream inputFileStream = new FileInputStream(cache.get(key));
            ObjectInputStream objectInputStream = new ObjectInputStream(inputFileStream);

            V deserializedObj = (V) objectInputStream.readObject();

            objectInputStream.close();
            inputFileStream.close();

            return deserializedObj;
        }
        catch(IOException | ClassNotFoundException e) {
            // Appropriate exception handling like logging etc.
        }
        return null;
    }
}
