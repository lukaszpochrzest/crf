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
    
    private final Map<String, Integer> posesOfTag_REASON;
    private final Map<String, Integer> posesOfTag_REASON_START;
    private final Map<String, Integer> posesOfTag_REASON_END;
    private final Map<String, Integer> posesOfTag_PROPOSITION;
    private final Map<String, Integer> posesOfTag_PROPOSITION_START;
    private final Map<String, Integer> posesOfTag_PROPOSITION_END;
    private final Map<String, Integer> posesOfTag_OTHER;
    
    private final Map<String, Integer> tokensOfTag_REASON;
    private final Map<String, Integer> tokensOfTag_REASON_START;
    private final Map<String, Integer> tokensOfTag_REASON_END;
    private final Map<String, Integer> tokensOfTag_PROPOSITION;
    private final Map<String, Integer> tokensOfTag_PROPOSITION_START;
    private final Map<String, Integer> tokensOfTag_PROPOSITION_END;
    private final Map<String, Integer> tokensOfTag_OTHER;

    private final Map<String, Integer> tokens;

    private final Map<String, Integer> poses;

    private final Map<String, Integer> tags;

    public CorpusStatistics() {
        precedingTokensOfTag_PROPOSITION_START = new HashMap<>();
        precedingPosesOfTag_PROPOSITION_START = new HashMap<>();
        precedingTokensOfTag_REASON_START = new HashMap<>();
        precedingPosesOfTag_REASON_START = new HashMap<>();
        tokensOfTag_REASON = new HashMap<>();
        tokensOfTag_REASON_START = new HashMap<>();
        tokensOfTag_REASON_END = new HashMap<>();
        tokensOfTag_PROPOSITION = new HashMap<>();
        tokensOfTag_PROPOSITION_END = new HashMap<>();
        tokensOfTag_PROPOSITION_START = new HashMap<>();
        tokensOfTag_OTHER = new HashMap<>();
        posesOfTag_REASON = new HashMap<>();
        posesOfTag_REASON_START = new HashMap<>();
        posesOfTag_REASON_END = new HashMap<>();
        posesOfTag_PROPOSITION = new HashMap<>();
        posesOfTag_PROPOSITION_END = new HashMap<>();
        posesOfTag_PROPOSITION_START = new HashMap<>();
        posesOfTag_OTHER = new HashMap<>();
        tokens = new HashMap<>();
        poses = new HashMap<>();
        tags = new HashMap<>();
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

    public void addPos(String tag, String pos) {

        Map<String, Integer> mapToUpdate;

        if(Tag.PROPOSITION.name().equals(tag)) {
            mapToUpdate = posesOfTag_PROPOSITION;
        } else if(Tag.PROPOSITION_START.name().equals(tag)) {
            mapToUpdate = posesOfTag_PROPOSITION_START;
        } else if(Tag.PROPOSITION_END.name().equals(tag)) {
            mapToUpdate = posesOfTag_PROPOSITION_END;
        } else if(Tag.REASON.name().equals(tag)) {
            mapToUpdate = posesOfTag_REASON;
        } else if(Tag.REASON_START.name().equals(tag)) {
            mapToUpdate = posesOfTag_REASON_START;
        } else if(Tag.REASON_END.name().equals(tag)) {
            mapToUpdate = posesOfTag_REASON_END;
        } else if(Tag.OTHER.name().equals(tag)) {
            mapToUpdate = posesOfTag_OTHER;
        } else {
            throw new IllegalArgumentException();
        }

        mapToUpdate.put(pos, mapToUpdate.getOrDefault(pos, 0) + 1);
    }

    public void addToken(String tag, String token) {

        Map<String, Integer> mapToUpdate;

        if(Tag.PROPOSITION.name().equals(tag)) {
            mapToUpdate = tokensOfTag_PROPOSITION;
        } else if(Tag.PROPOSITION_START.name().equals(tag)) {
            mapToUpdate = tokensOfTag_PROPOSITION_START;
        } else if(Tag.PROPOSITION_END.name().equals(tag)) {
            mapToUpdate = tokensOfTag_PROPOSITION_END;
        } else if(Tag.REASON.name().equals(tag)) {
            mapToUpdate = tokensOfTag_REASON;
        } else if(Tag.REASON_START.name().equals(tag)) {
            mapToUpdate = tokensOfTag_REASON_START;
        } else if(Tag.REASON_END.name().equals(tag)) {
            mapToUpdate = tokensOfTag_REASON_END;
        } else if(Tag.OTHER.name().equals(tag)) {
            mapToUpdate = tokensOfTag_OTHER;
        } else {
            throw new IllegalArgumentException();
        }

        mapToUpdate.put(token, mapToUpdate.getOrDefault(token, 0) + 1);
    }
    
    public void addToken(String token) {
        tokens.put(token, tokens.getOrDefault(token, 0) + 1);
    }

    public void addPos(String pos) {
        poses.put(pos, poses.getOrDefault(pos, 0) + 1);
    }

    public void addTag(String tag) {
        tags.put(tag, tags.getOrDefault(tag, 0) + 1);
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

        /**tokens of tag*/

        List<Map.Entry<String, Integer>> tokensOfTag_REASON_List =
                tokensOfTag_REASON.entrySet().stream()
                        .filter(e -> e.getValue() > 29)
                        .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                        .collect(Collectors.toList());

        List<Map.Entry<String, Integer>> tokensOfTag_REASON_START_List =
                tokensOfTag_REASON_START.entrySet().stream()
                        .filter(e -> e.getValue() > 7)
                        .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                        .collect(Collectors.toList());

        List<Map.Entry<String, Integer>> tokensOfTag_REASON_END_List =
                tokensOfTag_REASON_END.entrySet().stream()
                        .filter(e -> e.getValue() > 3)
                        .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                        .collect(Collectors.toList());

        List<Map.Entry<String, Integer>> tokensOfTag_PROPOSTION_List =
                tokensOfTag_PROPOSITION.entrySet().stream()
                        .filter(e -> e.getValue() > 30)
                        .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                        .collect(Collectors.toList());

        List<Map.Entry<String, Integer>> tokensOfTag_PROPOSTION_START_List =
                tokensOfTag_PROPOSITION_START.entrySet().stream()
                        .filter(e -> e.getValue() > 4)
                        .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                        .collect(Collectors.toList());

        List<Map.Entry<String, Integer>> tokensOfTag_PROPOSTION_END_List =
                tokensOfTag_PROPOSITION_END.entrySet().stream()
                        .filter(e -> e.getValue() > 3)
                        .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                        .collect(Collectors.toList());

        List<Map.Entry<String, Integer>> tokensOfTag_OTHER_List =
                tokensOfTag_OTHER.entrySet().stream()
                        .filter(e -> e.getValue() > 100)
                        .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                        .collect(Collectors.toList());

        /**poses of tag*/

        List<Map.Entry<String, Integer>> posesOfTag_REASON_List =
                posesOfTag_REASON.entrySet().stream()
                        .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                        .collect(Collectors.toList());

        List<Map.Entry<String, Integer>> posesOfTag_REASON_START_List =
                posesOfTag_REASON_START.entrySet().stream()
                        .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                        .collect(Collectors.toList());

        List<Map.Entry<String, Integer>> posesOfTag_REASON_END_List =
                posesOfTag_REASON_END.entrySet().stream()
                        .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                        .collect(Collectors.toList());

        List<Map.Entry<String, Integer>> posesOfTag_PROPOSTION_List =
                posesOfTag_PROPOSITION.entrySet().stream()
                        .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                        .collect(Collectors.toList());

        List<Map.Entry<String, Integer>> posesOfTag_PROPOSTION_START_List =
                posesOfTag_PROPOSITION_START.entrySet().stream()
                        .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                        .collect(Collectors.toList());

        List<Map.Entry<String, Integer>> posesOfTag_PROPOSTION_END_List =
                posesOfTag_PROPOSITION_END.entrySet().stream()
                        .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                        .collect(Collectors.toList());

        List<Map.Entry<String, Integer>> posesOfTag_OTHER_List =
                posesOfTag_OTHER.entrySet().stream()
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

        List<Map.Entry<String, Integer>> tagList =
                tags.entrySet().stream()
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

        sb.append(Tag.REASON.name());
        sb.append(" ");
        sb.append("poses");
        sb.append(System.lineSeparator());
        soak(sb, posesOfTag_REASON_List);
        sb.append(System.lineSeparator());

        sb.append(Tag.REASON_START.name());
        sb.append(" ");
        sb.append("poses");
        sb.append(System.lineSeparator());
        soak(sb, posesOfTag_REASON_START_List);
        sb.append(System.lineSeparator());

        sb.append(Tag.REASON_END.name());
        sb.append(" ");
        sb.append("poses");
        sb.append(System.lineSeparator());
        soak(sb, posesOfTag_REASON_END_List);
        sb.append(System.lineSeparator());

        sb.append(Tag.PROPOSITION.name());
        sb.append(" ");
        sb.append("poses");
        sb.append(System.lineSeparator());
        soak(sb, posesOfTag_PROPOSTION_List);
        sb.append(System.lineSeparator());

        sb.append(Tag.PROPOSITION_START.name());
        sb.append(" ");
        sb.append("poses");
        sb.append(System.lineSeparator());
        soak(sb, posesOfTag_PROPOSTION_START_List);
        sb.append(System.lineSeparator());

        sb.append(Tag.PROPOSITION_END.name());
        sb.append(" ");
        sb.append("poses");
        sb.append(System.lineSeparator());
        soak(sb, posesOfTag_PROPOSTION_END_List);
        sb.append(System.lineSeparator());

        sb.append(Tag.OTHER.name());
        sb.append(" ");
        sb.append("poses");
        sb.append(System.lineSeparator());
        soak(sb, posesOfTag_OTHER_List);
        sb.append(System.lineSeparator());




        sb.append(Tag.REASON.name());
        sb.append(" ");
        sb.append("tokens");
        sb.append(System.lineSeparator());
        soak(sb, tokensOfTag_REASON_List);
        sb.append(System.lineSeparator());

        sb.append(Tag.REASON_START.name());
        sb.append(" ");
        sb.append("tokens");
        sb.append(System.lineSeparator());
        soak(sb, tokensOfTag_REASON_START_List);
        sb.append(System.lineSeparator());

        sb.append(Tag.REASON_END.name());
        sb.append(" ");
        sb.append("tokens");
        sb.append(System.lineSeparator());
        soak(sb, tokensOfTag_REASON_END_List);
        sb.append(System.lineSeparator());

        sb.append(Tag.PROPOSITION.name());
        sb.append(" ");
        sb.append("tokens");
        sb.append(System.lineSeparator());
        soak(sb, tokensOfTag_PROPOSTION_List);
        sb.append(System.lineSeparator());

        sb.append(Tag.PROPOSITION_START.name());
        sb.append(" ");
        sb.append("tokens");
        sb.append(System.lineSeparator());
        soak(sb, tokensOfTag_PROPOSTION_START_List);
        sb.append(System.lineSeparator());

        sb.append(Tag.PROPOSITION_END.name());
        sb.append(" ");
        sb.append("tokens");
        sb.append(System.lineSeparator());
        soak(sb, tokensOfTag_PROPOSTION_END_List);
        sb.append(System.lineSeparator());

        sb.append(Tag.OTHER.name());
        sb.append(" ");
        sb.append("tokens");
        sb.append(System.lineSeparator());
        soak(sb, tokensOfTag_OTHER_List);
        sb.append(System.lineSeparator());
        
        
        
        
        sb.append("Tokens (filtered to only those ones which appeared 50 and more times)");
        sb.append(System.lineSeparator());
        soak(sb, tokenList);
        sb.append(System.lineSeparator());

        sb.append("POSes");
        sb.append(System.lineSeparator());
        soak(sb, posList);
        sb.append(System.lineSeparator());

        sb.append("Tags");
        sb.append(System.lineSeparator());
        soak(sb, tagList);
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
