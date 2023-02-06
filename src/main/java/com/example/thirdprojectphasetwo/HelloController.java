package com.example.thirdprojectphasetwo;

import com.example.thirdprojectphasetwo.model.Answer;
import com.example.thirdprojectphasetwo.model.InnerNode;
import com.example.thirdprojectphasetwo.model.Node;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import lombok.SneakyThrows;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.Scanner;

public class HelloController implements Initializable {
//    public final File CORPUS = new File ("C:\\Users\\Ahmad Khateeb\\Desktop\\TestingTrigram.csv");
    public final File CORPUS = new File ("Ngram.csv");
    public final File BICORPUS = new File ("NgramOrdered.csv");
    String str = "يا سيد #";
    Hashtable<String, Node> trigram = new Hashtable<> ();
    Hashtable<String, Node> bigram = new Hashtable<> ();
    @FXML
    private TextArea textArea;
    @FXML
    private ListView<String> listView;

    @FXML
    protected void buttonPredict() {
        str = textArea.getText ();
        System.out.println (str);
        if (str.equals ("")) {
            new Alert (Alert.AlertType.ERROR, "No Value Entered").show ();
            return;
        }

        Answer[]results = new Answer[0];
        try {
            results = predict ();
        } catch (Exception e) {
            e.printStackTrace ();
        }
        listView.getItems ().clear ();
        for (Answer result : results) {
            listView.getItems ().add (result.toString ());
        }
    }

    @SneakyThrows
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setTriTableHash ();
        setBiTableHash ();
        textArea.setOnKeyPressed (e->{
            if (e.getCode () == KeyCode.ENTER){
                textArea.setText (textArea.getText ().strip ().trim ());
                buttonPredict ();
            }

        });

    }

    @SuppressWarnings("IdempotentLoopBody")
    public Answer[] predict(){
        String[] sentence = str.split (" ");
        String[] words = new String[7];
        words[3] = "#";
        for (int i = 0; i < sentence.length; i++) {
            if (sentence[i].equals ("#")) {
                setValues (sentence, words, i - 3, 0);
                setValues (sentence, words, i - 2, 1);
                setValues (sentence, words, i - 1, 2);
                setValues (sentence, words, i + 1, 4);
                setValues (sentence, words, i + 2, 5);
                setValues (sentence, words, i + 3, 6);
            }
        }
        String[]keys = new String[4];
        keys[0]=words[0] + " " + words[1];
        keys[1]=words[1] + " " + words[2];
        keys[2]=words[4] + " " + words[5];
        keys[3]=words[5] + " " + words[6];

        Node secondTriValue = trigram.get (keys[1]);


        //Bi Gram Test
        Node firstBiValue = bigram.get (words[1]).clone ();
        Node secondBiValue = bigram.get (words[2]).clone ();
        InnerNode firstInner = firstBiValue.getQueue ().peek ();
        if (firstInner != null) {
            while(!(firstInner != null && firstInner.getWord ().equals (secondBiValue.getKey ())))
                firstInner = firstBiValue.getQueue ().peek ();
        }
        InnerNode[]biInnerNodes = new InnerNode[5];
        for (int i = 0; i < 5; i++)
            biInnerNodes[i] = secondBiValue.getQueue ().poll ();

        InnerNode[]triInnerNodes = new InnerNode[5];
        for (int i = 0; i < 5; i++)
            triInnerNodes[i] = secondTriValue.getQueue ().poll ();

        System.out.println ();
        InnerNode [] inners = new InnerNode [5];
        System.arraycopy (biInnerNodes, 0, inners, 0, 5);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if(biInnerNodes[i].equals (triInnerNodes[j])) {
                    inners[i] = triInnerNodes[j];
                }
            }
        }
        Answer[] answers = new Answer[5];
        int i = 0;
        for (InnerNode inner : inners) {
            double prob = (inner.getCount ()*1.0/secondBiValue.getCount ());
            answers[i++] = new Answer (inner.getWord (),prob);
            System.out.println (inner.getWord () +" "+prob);
        }
        return answers;
    }
    public void setValues(String[] sentence, String[] words, int i, int index) {
        try {
            words[index] = sentence[i];
        } catch (ArrayIndexOutOfBoundsException e) {
            words[index] = ("EOA");
        }
    }


    @SneakyThrows
    public void setTriTableHash() {
        Scanner in = new Scanner (CORPUS);
        long startTime = System.currentTimeMillis ();
        while (in.hasNext ()) {
            String[] str = in.nextLine ().split (",");
            String[] words = str[0].split (" ");
            String key = words[0] + " " + words[1];
            if (trigram.containsKey (key)) {
                Node value = trigram.get (key);
                int count = Integer.parseInt (str[1]);
                value.setCount (value.getCount () + count);
                InnerNode innerNode = new InnerNode (words[2], count);
                value.getQueue ().add (innerNode);
            } else {
                Node value = new Node ();
                value.setKey (key);
                int count = Integer.parseInt (str[1]);
                value.setCount (count);
                value.getQueue ().add (new InnerNode (words[2], count));
                trigram.put (key, value);
            }

        }
        long elapsedTime = System.currentTimeMillis () - startTime;
        long elapsedSeconds = elapsedTime / 1000;
        long secondsDisplay = elapsedSeconds % 60;
        long elapsedMinutes = elapsedSeconds / 60;
        Path path = Paths.get (CORPUS.getAbsolutePath ());
        in.close ();
        System.out.println (Files.lines (path).count () + " line in the file");
        System.out.println (elapsedTime);
        System.out.println (secondsDisplay + "s");
        System.out.println (elapsedMinutes + "m");
        System.out.println (trigram.size ());
    }

    @SneakyThrows
    public void setBiTableHash() {
        Scanner in = new Scanner (BICORPUS);
        long startTime = System.currentTimeMillis ();
        while (in.hasNext ()) {
            String[] str = in.nextLine ().split (",");
            String[] words = str[0].split (" ");
            String key = words[0];
            if (bigram.containsKey (key)) {
                Node value = bigram.get (key);
                int count = Integer.parseInt (str[1]);
                value.setCount (value.getCount () + count);
                InnerNode innerNode = new InnerNode (words[1], count);
                value.getQueue ().add (innerNode);
            } else {
                Node value = new Node ();
                value.setKey (key);
                int count = Integer.parseInt (str[1]);
                value.setCount (count);
                value.getQueue ().add (new InnerNode (words[1], count));
                bigram.put (key, value);
            }

        }
        long elapsedTime = System.currentTimeMillis () - startTime;
        long elapsedSeconds = elapsedTime / 1000;
        long secondsDisplay = elapsedSeconds % 60;
        long elapsedMinutes = elapsedSeconds / 60;
        Path path = Paths.get (BICORPUS.getAbsolutePath ());
        System.out.println (Files.lines (path).count () + " line in the file");
        System.out.println (elapsedTime);
        System.out.println (secondsDisplay + "s");
        System.out.println (elapsedMinutes + "m");
        System.out.println (bigram.size ());
    }
}