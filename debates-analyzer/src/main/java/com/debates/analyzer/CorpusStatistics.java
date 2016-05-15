package com.debates.analyzer;

import com.debates.analyzer.tofactor.Tag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by lukasz on 15.05.16.
 */
public class CorpusStatistics {

    private final Map<String, Integer> precedingTokensOfTag_PROPOSITION_START;

    private final Map<String, Integer> precedingPosesOfTag_PROPOSITION_START;

    private final Map<String, Integer> precedingTokensOfTag_REASON_START;

    private final Map<String, Integer> precedingPosesOfTag_REASON_START;

    private final Map<String, Integer> tokens;

    private final Map<String, Integer> poses;

    public CorpusStatistics() {
        precedingTokensOfTag_PROPOSITION_START = new HashMap<>();
        precedingPosesOfTag_PROPOSITION_START = new HashMap<>();
        precedingTokensOfTag_REASON_START = new HashMap<>();
        precedingPosesOfTag_REASON_START = new HashMap<>();
        tokens = new HashMap<>();
        poses = new HashMap<>();
    }

    public void addPrecedingToken(String tag, String precedingToken) {

        Map<String, Integer> mapToUpdate;

        if(Tag.PROPOSITION_START.name().equals(tag)) {
            mapToUpdate = precedingTokensOfTag_PROPOSITION_START;
        } else if(Tag.REASON_START.name().equals(tag)) {
            mapToUpdate = precedingTokensOfTag_REASON_START;
        } else {
            throw new IllegalArgumentException();
        }

        mapToUpdate.put(precedingToken, mapToUpdate.getOrDefault(precedingToken, 0) + 1);

    }

    public void addPrecedingPos(String tag, String precedingPos) {

        Map<String, Integer> mapToUpdate;

        if(Tag.PROPOSITION_START.name().equals(tag)) {
            mapToUpdate = precedingPosesOfTag_PROPOSITION_START;
        } else if(Tag.REASON_START.name().equals(tag)) {
            mapToUpdate = precedingPosesOfTag_REASON_START;
        } else {
            throw new IllegalArgumentException();
        }

        mapToUpdate.put(precedingPos, mapToUpdate.getOrDefault(precedingPos, 0) + 1);
    }

    public void addToken(String token) {
        tokens.put(token, tokens.getOrDefault(token, 0) + 1);
    }

    public void addPos(String pos) {
        poses.put(pos, poses.getOrDefault(pos, 0) + 1);
    }

    @Override
    public String toString() {

        // sort data    TODO compute-intensive operation in toString isnt good idea

        List<Map.Entry<String, Integer>> precedingTokensOfTag_PROPOSITION_START_List =
                precedingTokensOfTag_PROPOSITION_START.entrySet().stream()
                        .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                        .collect(Collectors.toList());

        List<Map.Entry<String, Integer>> precedingPosesOfTag_PROPOSITION_START_List =
                precedingPosesOfTag_PROPOSITION_START.entrySet().stream()
                        .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                        .collect(Collectors.toList());

        List<Map.Entry<String, Integer>> precedingTokensOfTag_REASON_START_List =
                precedingTokensOfTag_REASON_START.entrySet().stream()
                        .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                        .collect(Collectors.toList());

        List<Map.Entry<String, Integer>> precedingPosesOfTag_REASON_START_List =
                precedingPosesOfTag_REASON_START.entrySet().stream()
                        .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                        .collect(Collectors.toList());

        List<Map.Entry<String, Integer>> tokenList =
                tokens.entrySet().stream()
                        .filter(e -> e.getValue() > 50)
                        .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                        .collect(Collectors.toList());

        List<Map.Entry<String, Integer>> posList =
                poses.entrySet().stream()
                        .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                        .collect(Collectors.toList());


        StringBuilder sb = new StringBuilder();

        sb.append(Tag.PROPOSITION_START.name());
        sb.append(" ");
        sb.append("preceding tokens");
        sb.append(System.lineSeparator());
        soak(sb, precedingTokensOfTag_PROPOSITION_START_List);
        sb.append(System.lineSeparator());

        sb.append(Tag.PROPOSITION_START.name());
        sb.append(" ");
        sb.append("preceding poses");
        sb.append(System.lineSeparator());
        soak(sb, precedingPosesOfTag_PROPOSITION_START_List);
        sb.append(System.lineSeparator());

        sb.append(Tag.REASON_START.name());
        sb.append(" ");
        sb.append("preceding tokens");
        sb.append(System.lineSeparator());
        soak(sb, precedingTokensOfTag_REASON_START_List);
        sb.append(System.lineSeparator());

        sb.append(Tag.REASON_START.name());
        sb.append(" ");
        sb.append("preceding poses");
        sb.append(System.lineSeparator());
        soak(sb, precedingPosesOfTag_REASON_START_List);
        sb.append(System.lineSeparator());

        sb.append("Tokens (filtered to only those ones which appeared 50 and more times)");
        sb.append(System.lineSeparator());
        soak(sb, tokenList);
        sb.append(System.lineSeparator());

        sb.append("POSes");
        sb.append(System.lineSeparator());
        soak(sb, posList);
        sb.append(System.lineSeparator());

        return sb.toString();
    }

    private void soak(StringBuilder sb, List<Map.Entry<String, Integer>> list) {

        int sum = 0;

        for(Map.Entry<String, Integer> entry : list) {
            sb.append("\t");
            Integer value = entry.getValue();
            sb.append(value);
            sb.append("\t");
            sb.append(entry.getKey());
            sb.append(System.lineSeparator());

            sum += value;
        }

        sb.append(System.lineSeparator());
        sb.append("sum=");
        sb.append(sum);
        sb.append(System.lineSeparator());
    }
}
