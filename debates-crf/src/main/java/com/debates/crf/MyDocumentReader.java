package com.debates.crf;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by lukasz on 14.04.16.
 */
public class MyDocumentReader {

    private final List<String> stoplist;
    private final String splitPattern;

    public MyDocumentReader(final List<String> stoplist, final String splitPattern) {
        this.stoplist = stoplist;
        this.splitPattern = splitPattern;
    }

    public MyDocumentReader(final List<String> stoplist) {
        this(stoplist, "[^a-zA-Z0-9\\-\'zżźćńółęąśŻŹĆĄŚĘŁÓŃ]+");
    }

    /**
     * sequence == single line tokens with stopwords removed
     * @param file
     * @return
     * @throws IOException
     */
    public List<List<String>> readSequences(File file) throws IOException {
        LinkedList<List<String>> result = new LinkedList<>();
//        ArrayList<String> result = new ArrayList<>();

//        try(FileReader fileReader = new FileReader(fileDir)) {
            Scanner sc = new Scanner(file);
            while (sc.hasNext()) {
                String line = sc.nextLine();

                // split line into words
                ArrayList<String> sequence = new ArrayList<>(Arrays.asList(line.split(splitPattern)));

                sequence.removeIf(s -> s.isEmpty() || s.matches(splitPattern));

                // to lower case
                sequence = sequence.stream()
                        .map(String::toLowerCase)
                        .collect(Collectors.toCollection(ArrayList::new));

                // remove stop words
//                sequence.removeIf(s -> stoplist.contains(s));

                result.add(sequence);
            }
//        }

        return new ArrayList<>(result);
    }



}
