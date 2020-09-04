package org.cncnet.yrqm.model;

import org.cncnet.yrqm.model.enums.YRFactionEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class YRFaction {
    private YRFactionEnum name;
    private int won;

    public YRFaction(YRFactionEnum name, int won) {
        this.name = name;
        this.won = won;
    }
}
