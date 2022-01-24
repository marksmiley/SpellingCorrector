package spell;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class SpellCorrector implements ISpellCorrector {
    private Trie dictionary = new Trie();
    private final char[] ALPHABET = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    private ArrayList<String> wordListdist1 = new ArrayList<String>();
    private ArrayList<String> wordListdist2 = new ArrayList<String>();
    private final int EDITDISTANCE1 = 1;
    private final int EDITDISTANCE2 = 2;

    @Override
    public void useDictionary(String dictionaryFileName) throws IOException {
        Scanner scanner = new Scanner(new File(dictionaryFileName));
        do {
            String word = scanner.next().toLowerCase(Locale.ROOT);
            dictionary.add(word);
        } while (scanner.hasNext());
    }

    @Override
    public String suggestSimilarWord(String inputWord) {
        inputWord = inputWord.toLowerCase(Locale.ROOT);
        //check to see if word already exists in dictionary
        if (inputWord.equals("")) {
            return null;
        }
        if (dictionary.find(inputWord) != null) {
            return inputWord;
        }

        // distance 1
        DeletionDistance(inputWord, EDITDISTANCE1);
        TranspositionDistance(inputWord, EDITDISTANCE1);
        AlterationDistance(inputWord, EDITDISTANCE1);
        InsertionDistance(inputWord, EDITDISTANCE1);

        // to check the edit distance 1 instances
        String returnString = ListChecker(wordListdist1);
        if (returnString != null) {
            wordListdist1.clear();
            return returnString;
        } else {

            //checks edit distance 2 instances
            DeletionDistance(inputWord, EDITDISTANCE2);
            TranspositionDistance(inputWord, EDITDISTANCE2);
            AlterationDistance(inputWord, EDITDISTANCE2);
            InsertionDistance(inputWord, EDITDISTANCE2);

            returnString = ListChecker(wordListdist2);
            wordListdist1.clear();
            wordListdist2.clear();
            return returnString;
        }
    }

    public String ListChecker(ArrayList<String> wordList) {
        int highestFreq = 0;
        ArrayList<String> correctList = new ArrayList<String>();
        String highestFreqWord = null;
        for (int i = 0; i < wordList.size(); i++) {
            if (wordList.get(i).isEmpty()) {
                continue;
            }

            if (dictionary.find(wordList.get(i)) != null) {

                correctList.add(wordList.get(i));

                for (int j = 0; j < correctList.size(); j++) {

                    if (dictionary.find(correctList.get(j)).getValue() >= highestFreq) {
                        if (highestFreqWord == null) { // accounts for the first word found
                            highestFreq = dictionary.find(correctList.get(j)).getValue();
                            highestFreqWord = correctList.get(j);
                        } else if (dictionary.find(correctList.get(j)).getValue() > highestFreq) {
                            //accounts for words of higher frequency
                            highestFreq = dictionary.find(correctList.get(j)).getValue();
                            highestFreqWord = correctList.get(j);
                        } else if (dictionary.find(correctList.get(j)).getValue() == highestFreq) {
                            //accounts for words of same frequency, checks alphabetical order
                            if (correctList.get(j).compareTo(highestFreqWord) < 0) {
                                highestFreq = dictionary.find(correctList.get(j)).getValue();
                                highestFreqWord = correctList.get(j);
                            }
                        }
                    }
                }
            }
        }
        return highestFreqWord;
    }

    public void InsertionDistance(String inputWord, int editDistance) {
        if (editDistance == EDITDISTANCE1) {
            for (int i = 0; i < inputWord.length(); i++) {
                StringBuilder insertion = new StringBuilder(inputWord);
                wordListdist1.add(insertion.deleteCharAt(i).toString());
            }
        } else {
            for (int j = 0; j < wordListdist1.size(); j++) {
                for (int i = 0; i < wordListdist1.get(j).length(); i++) {
                    StringBuilder insertion = new StringBuilder(wordListdist1.get(j));
                    wordListdist2.add(insertion.deleteCharAt(i).toString());
                }
            }
        }
    }

    public void DeletionDistance(String inputWord, int editDistance) {
        if (editDistance == EDITDISTANCE1) {
            for (int i = 0; i <= inputWord.length(); ++i) {
                for (int j = 0; j < ALPHABET.length; ++j) {
                    StringBuilder insertion = new StringBuilder(inputWord);
                    wordListdist1.add(insertion.insert(i, ALPHABET[j]).toString());
                }
            }
        } else {
            for (int i = 0; i < wordListdist1.size(); i++) {
                for (int j = 0; j <= wordListdist1.get(i).length(); ++j) {
                    for (int k = 0; k < ALPHABET.length; ++k) {
                        StringBuilder insertion = new StringBuilder(wordListdist1.get(i));
                        wordListdist2.add(insertion.insert(j, ALPHABET[k]).toString());
                    }
                }
            }
        }
    }

    public void TranspositionDistance(String inputWord, int editDistance) {
        if (editDistance == EDITDISTANCE1) {
            for (int i = 0; i < inputWord.length() - 1; ++i) {
                StringBuilder transpositionDist1 = new StringBuilder(inputWord);
                char iTemp = transpositionDist1.charAt(i);
                char jTemp = transpositionDist1.charAt(i + 1);
                transpositionDist1.delete(i, i + 2);
                transpositionDist1.insert(i, jTemp);
                transpositionDist1.insert(i + 1, iTemp);
                wordListdist1.add(transpositionDist1.toString());
            }
        } else {
            for (int i = 0; i < wordListdist1.size(); ++i) {
                for (int j = 0; j < wordListdist1.get(i).length() - 1; j++) {
                    StringBuilder transpositionDist2 = new StringBuilder(wordListdist1.get(i));
                    char iTemp2 = transpositionDist2.charAt(j);
                    char jTemp2 = transpositionDist2.charAt(j + 1);
                    transpositionDist2.delete(j, j + 2);
                    transpositionDist2.insert(j, jTemp2);
                    transpositionDist2.insert(j + 1, iTemp2);
                    wordListdist2.add(transpositionDist2.toString());
                }
            }
        }
    }

    public void AlterationDistance(String inputWord, int editDistance) {
        if (editDistance == EDITDISTANCE1) {
            for (int i = 0; i < inputWord.length(); i++) {
                for (int l = 0; l < ALPHABET.length; l++) {

                    StringBuilder altDist1 = new StringBuilder(inputWord);
                    altDist1.replace(i, i + 1, String.valueOf(ALPHABET[l]));
                    wordListdist1.add(altDist1.toString());
                }
            }
        } else {
            for (int i = 0; i < wordListdist1.size(); ++i) {
                for (int p = 0; p < wordListdist1.get(i).length(); ++p) {
                    for (int a = 0; a < ALPHABET.length; ++a) {
                        StringBuilder altDist2 = new StringBuilder(wordListdist1.get(i));
                        altDist2.replace(p, p + 1, String.valueOf(ALPHABET[a]));
                        wordListdist2.add(altDist2.toString());
                    }
                }
            }
        }
    }
}


