package com.debates.crf.gui;

import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by lukasz on 14.04.16.
 */
public class GUI {

    public enum Panel {
        PROPOSITION,
        REASON,
        SENTIMENT
    }

    private double neutralThreshold = 1.0;

    private JTextArea propositionDisplay;

    private JTextArea reasonDisplay;

    private JTextArea sentimentDisplay;

    private JLabel stats;

    private Map<MarkClasses, Color> markClassesColorMapping;

    private Map<String, Color> sentimentColorMapping;

    public GUI() {
        markClassesColorMapping = new HashMap<>();
        sentimentColorMapping = new HashMap<>();

        markClassesColorMapping.put(MarkClasses.BEGIN, Color.GREEN);
        markClassesColorMapping.put(MarkClasses.MIDDLE, Color.YELLOW);
        markClassesColorMapping.put(MarkClasses.END, Color.RED);

        sentimentColorMapping.put("GOOD", new Color(0, 199, 140));
        sentimentColorMapping.put("NEUTRAL", new Color(125, 158, 192));
        sentimentColorMapping.put("BAD", new Color(255, 99, 71));
    }

    public void create() {
        JPanel propositionPanel = new JPanel ();
        propositionPanel.setBorder ( new TitledBorder( new EtchedBorder(), "Propositions" ) );

        // create the middle panel components

        propositionDisplay = new JTextArea ( 60, 50 );
        propositionDisplay.setEditable ( false ); // set textArea non-editable
        propositionDisplay.setLineWrap(true);
        propositionDisplay.setWrapStyleWord(true);

        propositionPanel.add(propositionDisplay);

        JPanel reasonPanel = new JPanel ();
        reasonPanel.setBorder ( new TitledBorder( new EtchedBorder(), "Reasons" ) );

        // create the middle panel components

        reasonDisplay = new JTextArea ( 60, 50 );
        reasonDisplay.setEditable ( false ); // set textArea non-editable
        reasonDisplay.setLineWrap(true);
        reasonDisplay.setWrapStyleWord(true);

        reasonPanel.add(reasonDisplay);

        JPanel sentimentPanel = new JPanel ();
        sentimentPanel.setBorder ( new TitledBorder( new EtchedBorder(), "Sentiments" ) );

        // create the middle panel components

        sentimentDisplay = new JTextArea ( 60, 50 );
        sentimentDisplay.setEditable ( false ); // set textArea non-editable
        sentimentDisplay.setLineWrap(true);
        sentimentDisplay.setWrapStyleWord(true);

        sentimentPanel.add(sentimentDisplay);

        JPanel bothPanels = new JPanel();
        bothPanels.setLayout(new GridLayout(1, 2));
        bothPanels.add(propositionPanel, BorderLayout.WEST);
        bothPanels.add(reasonPanel, BorderLayout.CENTER);
        bothPanels.add(sentimentPanel, BorderLayout.EAST);
        bothPanels.setBorder(new TitledBorder( new EtchedBorder(), "Extractor" ));

        JScrollPane scroll = new JScrollPane (bothPanels);
        scroll.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED );

        JTextArea legend = new JTextArea(markClassesColorMapping.entrySet().stream().map(entry -> "     - " + entry.getKey().name()).collect(Collectors.joining(" ")) + " "
                + sentimentColorMapping.entrySet().stream().map(entry -> "     - " + entry.getKey()).collect(Collectors.joining(" ")));
        legend.setEditable(false);

        JPanel legendPanel = new JPanel();
        legendPanel.add(legend, BorderLayout.CENTER);

        markClassesColorMapping.entrySet().forEach(entry -> {
            String legendText = legend.getText();
            int index = legendText.indexOf(entry.getKey().name());
            highlight(legend, (long) (index - 7), (long) (index - 3), MarkClasses.valueOf(entry.getKey().name()));
        });

        sentimentColorMapping.entrySet().forEach(entry -> {
            String legendText = legend.getText();
            int index = legendText.indexOf(entry.getKey());
            highlight(legend, (long) (index - 7), (long) (index - 3), sentimentColorMapping.get(entry.getKey()));
        });

        stats = new JLabel();

        JPanel statsPanel = new JPanel();
        statsPanel.add(stats);

        // My code
        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.add(scroll,BorderLayout.CENTER);
        frame.add(legendPanel,BorderLayout.NORTH);
        frame.add(statsPanel, BorderLayout.SOUTH);
        frame.pack();
        frame.setLocationRelativeTo ( null );
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible ( true );
    }

    public void setText(String text, Panel panel) {
        switch (panel) {
            case PROPOSITION:
                propositionDisplay.setText(text);
                break;
            case REASON:
                reasonDisplay.setText(text);
                break;
            case SENTIMENT:
                sentimentDisplay.setText(text);
        }
    }

    public void highlightText(File textFile, ExtractionResult extractionResult) {
        String text = null;
        try {
            text = FileUtils.readFileToString(textFile, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }

        propositionDisplay.setText(text);
        reasonDisplay.setText(text);
        sentimentDisplay.setText(text);

        for (ClassifiedStatement classifiedStatement : extractionResult.getClassifiedStatements()) {
            highlightStatement(classifiedStatement);
        }

        for (SentimentStatement sentimentStatement : extractionResult.getSentimentStatements()) {
            highlightSentiment(sentimentStatement);
        }
    }

    private void highlightStatement(ClassifiedStatement classifiedStatement) {
        MarkClasses propositionClass = classifiedStatement.getPropositionClass();
        MarkClasses reasonClass = classifiedStatement.getReasonClass();

        if (propositionClass != MarkClasses.NOT_MARK) {
            highlight(propositionDisplay, classifiedStatement.getStartIndex(), classifiedStatement.getEndIndex(), propositionClass);
        }

        if (reasonClass != MarkClasses.NOT_MARK) {
            highlight(reasonDisplay, classifiedStatement.getStartIndex(), classifiedStatement.getEndIndex(), reasonClass);
        }
    }

    private void highlightSentiment(SentimentStatement sentimentStatement) {
        String sent;

        if (Math.abs(sentimentStatement.getSentiment()) < neutralThreshold) {
            sent = "NEUTRAL";
        } else if (sentimentStatement.getSentiment() < 0) {
            sent = "BAD";
        } else {
            sent = "GOOD";
        }

        highlight(sentimentDisplay, sentimentStatement.getStartIndex(), sentimentStatement.getEndIndex(), sentimentColorMapping.get(sent));
    }

    private void highlight(JTextArea display, Long startIndex, Long endIndex, Color color) {
        Highlighter highlighter = display.getHighlighter();
        Highlighter.HighlightPainter painter =
                new DefaultHighlighter.DefaultHighlightPainter(color);
        try {
            highlighter.addHighlight(startIndex.intValue(), endIndex.intValue(), painter);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void highlight(JTextArea display, Long startIndex, Long endIndex, MarkClasses markClass) {
        highlight(display, startIndex,endIndex, markClassesColorMapping.get(markClass));
    }


//    public void showStats(ExtractionResult extractionResult) {
//        Map<String, String> statsMap = new HashMap<>();
//
//        statsMap.put("Proposition begin", String.valueOf(extractionResult.getClassifiedStatements().stream().filter(s -> s.getPropositionClass() == MarkClasses.BEGIN).count()));
//        statsMap.put("Proposition middle", String.valueOf(extractionResult.getClassifiedStatements().stream().filter(s -> s.getPropositionClass() == MarkClasses.MIDDLE).count()));
//        statsMap.put("Proposition end", String.valueOf(extractionResult.getClassifiedStatements().stream().filter(s -> s.getPropositionClass() == MarkClasses.END).count()));
//
//        statsMap.put("Reason begin", String.valueOf(extractionResult.getClassifiedStatements().stream().filter(s -> s.getReasonClass() == MarkClasses.BEGIN).count()));
//        statsMap.put("Reason middle", String.valueOf(extractionResult.getClassifiedStatements().stream().filter(s -> s.getReasonClass() == MarkClasses.MIDDLE).count()));
//        statsMap.put("Reason end", String.valueOf(extractionResult.getClassifiedStatements().stream().filter(s -> s.getReasonClass() == MarkClasses.END).count()));
//
//        stats.setText(statsMap.entrySet().stream().sorted((o1, o2) -> o1.getKey().compareTo(o2.getKey())).map(entry -> entry.getKey() + ": " + entry.getValue()).collect(Collectors.joining(" ")));
//    }
}
