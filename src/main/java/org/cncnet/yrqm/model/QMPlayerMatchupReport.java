package org.cncnet.yrqm.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class QMPlayerMatchupReport implements Comparable<QMPlayerMatchupReport> {
    private final String ladderGame;
    private final String playerName;
    private final List<QMPlayerMatchup> qmPlayerMatchupList;

    public QMPlayerMatchupReport(String ladderGame, String playerName) {
        this.ladderGame = ladderGame;
        this.playerName = playerName;
        qmPlayerMatchupList = new ArrayList<>();
    }

    public void addMatchup(QMPlayerMatchup qmPlayerMatchup) {
        qmPlayerMatchupList.add(qmPlayerMatchup);
    }

    @Override
    public int compareTo(QMPlayerMatchupReport o) {
        return playerName.compareTo(o.playerName);
    }

    @Getter
    public static class QMPlayerMatchup implements Comparable<QMPlayerMatchup> {
        private final String opponent;
        private int myWins;
        private int opponentWins;

        public QMPlayerMatchup(String opponent) {
            this.opponent = opponent;
        }

        public void incrementMyWins() {
            myWins++;
        }

        public void incrementOpponentWins() {
            opponentWins++;
        }

        @Override
        public int compareTo(QMPlayerMatchup o) {
            return opponent.compareTo(o.opponent);
        }
    }

    public QMPlayerMatchup getQmPlayerMatchup(String opponent) {
        return qmPlayerMatchupList.stream()
                .filter(x -> x.getOpponent().equals(opponent))
                .findFirst().orElse(null);
    }
}