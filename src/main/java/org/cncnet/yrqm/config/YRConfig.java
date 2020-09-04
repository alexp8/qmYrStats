package org.cncnet.yrqm.config;

import org.cncnet.yrqm.model.enums.YRCountryEnum;
import org.cncnet.yrqm.model.enums.YRFactionEnum;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

@Getter
public class YRConfig {

    private static final String yrPropertiesFileName = "/yr.properties";

    private final HashMap<Integer, YRCountryEnum> countries;
    private final HashMap<Integer, YRFactionEnum> factions;

    public YRConfig() {
        countries = new HashMap<>();
        factions = new HashMap<>();
        readProperties();
    }

    private void readProperties() {
        Properties properties = new Properties();

        try (InputStream is = YRConfig.class.getResourceAsStream(yrPropertiesFileName)) {
            properties.load(is);

            String value = properties.getProperty("yr.country.0");

            countries.put(0, YRCountryEnum.valueOf(value));
            countries.put(1, YRCountryEnum.valueOf(properties.getProperty("yr.country.1")));
            countries.put(2, YRCountryEnum.valueOf(properties.getProperty("yr.country.2")));
            countries.put(3, YRCountryEnum.valueOf(properties.getProperty("yr.country.3")));
            countries.put(4, YRCountryEnum.valueOf(properties.getProperty("yr.country.4")));
            countries.put(5, YRCountryEnum.valueOf(properties.getProperty("yr.country.5")));
            countries.put(6, YRCountryEnum.valueOf(properties.getProperty("yr.country.6")));
            countries.put(7, YRCountryEnum.valueOf(properties.getProperty("yr.country.7")));
            countries.put(8, YRCountryEnum.valueOf(properties.getProperty("yr.country.8")));
            countries.put(9, YRCountryEnum.valueOf(properties.getProperty("yr.country.9")));

            factions.put(0, YRFactionEnum.valueOf(properties.getProperty("yr.faction.0")));
            factions.put(1, YRFactionEnum.valueOf(properties.getProperty("yr.faction.1")));
            factions.put(2, YRFactionEnum.valueOf(properties.getProperty("yr.faction.2")));
            factions.put(3, YRFactionEnum.valueOf(properties.getProperty("yr.faction.3")));
            factions.put(4, YRFactionEnum.valueOf(properties.getProperty("yr.faction.4")));
            factions.put(5, YRFactionEnum.valueOf(properties.getProperty("yr.faction.5")));
            factions.put(6, YRFactionEnum.valueOf(properties.getProperty("yr.faction.6")));
            factions.put(7, YRFactionEnum.valueOf(properties.getProperty("yr.faction.7")));
            factions.put(8, YRFactionEnum.valueOf(properties.getProperty("yr.faction.8")));
            factions.put(9, YRFactionEnum.valueOf(properties.getProperty("yr.faction.9")));


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}