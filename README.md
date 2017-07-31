# DiskBackedLRUCache

Disk backed LRU Cache which writes the cache values to files in a directory called Cache. 

To keep things simple, the name of the file (which holds our value) is the hashcode of the key used to cache our object. The file contains the serialized version of the value.

Space/Time Complexity: O(n)/O(1) since size of the map (and number of files) will be equal to the number of currently cached objects. The time complexity will be amortized O(1) since value (filenames) are stored in a LinkedHashMap which gives us an O(1) access to them.

Is it thread safe?
Different read calls (even from different threads) can read the values from file. They will not be blocked by each other. But put calls will only happen one after the other to avoid data corruption. Assuming high read/write ratio this should work fine.

Overiding the removeEldestEntry of the LinkedHashMap to remove the least recently used file. This ensures that our cache behaves in the LRU manner.

Problems as of now:
1. When reading and writing the file at the same time behavior is undefined. Need to explore thread safe read methods (FileChannel?).
2. No test case to test the behavior in case of multi-threading scenario.

Possible Improvements:
1. Move to an enum based singleton pattern for the cache object. (Best practice effective java!)
2. Delete the existing files (if present) in Cache folder when getting a cache instance.
3. Instead of diffent files for every object can we do it in one file and then at the time of get(K key) read from the same file? There must be a way to seek an existing file. This will save us overhead of creating new files for every new value.
