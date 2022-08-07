package org.cncnet.yrqm.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

@Getter
public class PlayerLookup {
    private String[] nicknames;

    /**
     * @param content json content of player lookup mappings
     * @return a map containing the player's real name and their nicknames
     */
    public static Map<String, List<String>> getPlayerLookups(String content) {
        final Type mapType = new TypeToken<Map<String, List<String>>>() {
        }.getType();
        return new Gson().fromJson(content, mapType);
    }
}