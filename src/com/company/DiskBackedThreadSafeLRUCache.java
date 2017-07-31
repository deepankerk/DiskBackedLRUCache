package com.company;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

public class DiskBackedThreadSafeLRUCache<K, V> {

    private static DiskBackedThreadSafeLRUCache cacheObj;
    private final Map<K,String> cache;
    private final String directory;

    public static DiskBackedThreadSafeLRUCache open(final int maxEntries) {
        // Cache must be singleton. If the instance exist return that other create one;
        if(cacheObj != null) {
            return cacheObj;
        }

        File cacheDir = new File("Cache");
        cacheDir.mkdir(); //Doesn't create if already exists.
        cacheObj =  new DiskBackedThreadSafeLRUCache(Paths.get("Cache").toAbsolutePath().toString(), maxEntries);
        return cacheObj;
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

    public synchronized void put(K key, V value) {
        String fileName = directory + "/" + key.hashCode();
        try {
            FileOutputStream fileOut = new FileOutputStream(fileName);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);

            out.writeObject(value);
            // If the object has been successfully serialized to file then only put it in cache.
            cache.put(key, fileName);

            out.close();
            fileOut.close();
        } catch (IOException e) {

        }
    }

    public V get(K key) {
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
