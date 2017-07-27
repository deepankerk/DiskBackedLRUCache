# LRUCache

Disk backed LRU Cache which writes the cache values to files in a directory called Cache. 

To keep things simple, the name of the file (which holds our value) is the hashcode of the key used to cache our object. The key file contains the serialized version of the value.

Space/Time Complexity: O(n)/O(1) since size of the map (and number of files) will be equal to the number of currently cached objects. The time complexity will be amortized O(1) since value (filenames) are stored in a LinkedHashMap which gives us a O(1) access to them.

Is it thread safe? 
The access to LinkedHashMap used to hold our key and filenames is synchronized but reading/writing to the same file via different threads could lead to an undefined output so no right now the cache is not thread safe.

Overiding the removeEldestEntry of the LinkedHashMap to remove the least recently used file. This ensures that our cache behaves in the LRU manner.

Possible Improvements:
1. Making it thread safe.
2. Delete the existing files (if present) in Cache folder when getting a cache instance.
3. Instead of diffent files for every object can we do it in one file and then at the time of get(K key) read from the same file?
