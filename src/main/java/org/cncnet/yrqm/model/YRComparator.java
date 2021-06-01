package org.cncnet.yrqm.model;

import java.util.Comparator;

/**
 * Primary Sort: Standard maps first
 * Secondary Sort: alphabetical
 */
public class YRComparator implements Comparator<String> {

    /**
     * Compare two map names
     * @param map1 map1
     * @param map2 map2
     * @return sort order
     */
    @Override
    public int compare(String map1, String map2) {
        if (map1.contains("Standard") && map2.contains("Standard")) {
            return map1.compareTo(map2);
        } else if (map1.contains("Standard")) {
            return -1;
        } else if (map2.contains("Standard")) {
            return 1;
        } else {
            return map1.compareTo(map2);
        }
    }
}