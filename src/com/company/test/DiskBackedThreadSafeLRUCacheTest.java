package com.company.test;

import com.company.DiskBackedThreadSafeLRUCache;
import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

public class DiskBackedThreadSafeLRUCacheTest {

    @Test
    public void testStandardGetAndSet() {
        DiskBackedThreadSafeLRUCache<Integer, Double> cache = DiskBackedThreadSafeLRUCache.open(10);
        cache.put(1, 12.12);
        cache.put(2, 12.1234);
        cache.put(3, 12.121313);
        cache.put(4, 12.13132);
        cache.put(5, 12.33432525);
        cache.put(6, 1123131.12);
        cache.put(7, 12.2);
        cache.put(8, 1212313.12);
        cache.put(9, 12123747.12);

        Assert.assertEquals(Double.valueOf(12.12), (Double) cache.get(1));

        cache.put(10, 12.12);
        cache.put(11, 12.12);
        //Since key 1 was accessed right above 2 became the least recently used and got deleted when 11 was inserted
        Assert.assertEquals(null, (Double) cache.get(2));

        Assert.assertEquals(Double.valueOf(12.12), (Double) cache.get(1));
        Assert.assertEquals(Double.valueOf(12.121313), (Double) cache.get(3));
        Assert.assertEquals(Double.valueOf(12.13132), (Double) cache.get(4));
    }

    @Test
    public void testingCustomDomainObject() {
        DiskBackedThreadSafeLRUCache<SampleDomainObject, SampleDomainObject> cache = DiskBackedThreadSafeLRUCache.open(10);
        SampleDomainObject s1 = new SampleDomainObject(1, Calendar.getInstance().getTime(), "Deepanker");
        cache.put(s1, s1);

        SampleDomainObject s2 = new SampleDomainObject(1, Calendar.getInstance().getTime(), "JohnDoe");
        cache.put(s2, s2);

        SampleDomainObject s3 = new SampleDomainObject(5, Calendar.getInstance().getTime(), "Another Person");
        cache.put(s3, s3);
        Assert.assertEquals(s2, cache.get(s2));

        SampleDomainObject s4 = new SampleDomainObject(4, Calendar.getInstance().getTime(), "Testing-Testing");
        cache.put(s4, s4);
        Assert.assertEquals(s3, cache.get(s3));

        SampleDomainObject s5 = new SampleDomainObject(0, Calendar.getInstance().getTime(), "So many person");
        cache.put(s5, s5);
        Assert.assertEquals(s4, cache.get(s4));

        SampleDomainObject s6 = new SampleDomainObject(5, Calendar.getInstance().getTime(), "deepanker");
        cache.put(s6, s6);
        Assert.assertEquals(s5, cache.get(s5));

        SampleDomainObject s7 = new SampleDomainObject(112, Calendar.getInstance().getTime(), "");
        cache.put(s7, s7);

        SampleDomainObject s8 = new SampleDomainObject(2, Calendar.getInstance().getTime(), "TestingScenario");
        cache.put(s8, s8);

        SampleDomainObject s9 = new SampleDomainObject(4, Calendar.getInstance().getTime(), "");
        cache.put(s9, s9);

        SampleDomainObject s10 = new SampleDomainObject(16, Calendar.getInstance().getTime(), "Deepanker12");
        cache.put(s10, s10);

        SampleDomainObject s11 = new SampleDomainObject(2234, Calendar.getInstance().getTime(), "Deepanker3456");
        cache.put(s11, s11);

        Assert.assertEquals(null, cache.get(s1));
    }

    @Test
    public void testSettingMultipleValuesForSameKey() {
        DiskBackedThreadSafeLRUCache<Integer, Double> cache = DiskBackedThreadSafeLRUCache.open(10);
        cache.put(1, 12.12);
        cache.put(1, 12.1234);
        cache.put(1, 12.121313);
        cache.put(1, 12.13132);
        cache.put(1, 12.33432525);

        Assert.assertEquals(Double.valueOf(12.33432525), (Double) cache.get(1));
    }

}
