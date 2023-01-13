package com.company;

import org.apache.tika.Tika;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Pdf2Csv {

  public static void main(String[] arg) {

    StringBuilder sbAll = new StringBuilder("Buchung;Valuta;Auftraggeber/Empfänger;Buchungstext;Verwendungszweck;Saldo;Währung;Betrag;Währung\n");

    File start = new File(arg[0]);
    processPDFsRecursively(start, sbAll);

    writeToFile(sbAll.toString(), start.getAbsolutePath() + "\\aggregated.csv");
  }

  private static void writeToFile(String content, String path) {
    FileWriter fw;
    try {
      fw = new FileWriter(path);
      fw.write(content);
      fw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void processPDFsRecursively(File start, StringBuilder sbAll) {

    File[] files = start.listFiles();
    if (files == null) {
      return;
    }

    for (File file : files) {
      if (file.isDirectory()) {
        processPDFsRecursively(file, sbAll);
        continue;
      }

      if (file.getName().toLowerCase().endsWith(".pdf")) {
        String result = processPDF(file);

        sbAll.append(result);

        result = "Buchung;Valuta;Auftraggeber/Empfänger;Buchungstext;Verwendungszweck;Saldo;Währung;Betrag;Währung\n" + result;
        writeToFile(result, file.getAbsolutePath().replace("pdf", "csv"));
      }
    }
  }

  private static String processPDF(File file) {
    System.out.println("Processing " + file.getName());

    String content = "";
    try {
      content = new Tika().parseToString(file);
    } catch (Exception e) {
      e.printStackTrace();
    }

    boolean reachedEnd = false;
    StringBuilder sb = new StringBuilder();

    // filter empty lines from input
    String[] splitContent = Arrays.stream(content.split("\n")).filter(e -> !e.equals("")).toArray(String[]::new);
    String currency = "";

    Pattern firstLinePattern = Pattern.compile("^(\\d\\d\\.\\d\\d\\.\\d\\d\\d\\d) +(.*) +(.*,\\d\\d)");
    Pattern secondLinePattern = Pattern.compile("^(\\d\\d\\.\\d\\d\\.\\d\\d\\d\\d) +(.*)");
    Pattern currencyPattern = Pattern.compile("^.*Betrag \\((\\w\\w\\w)\\)$");

    for (int i = 0; i < splitContent.length; i++) {

      if (reachedEnd) {
        break;
      }

      String line = splitContent[i];

      if (currency.equals("")) {
        Matcher currencyMatcher = currencyPattern.matcher(line);

        if (currencyMatcher.find()) {
          currency = currencyMatcher.group(1);
          continue;
        }
      }

      Matcher firstline = firstLinePattern.matcher(line);

      if (firstline.find()) {
        String date = firstline.group(1);
        String type = firstline.group(2).trim();
        String amount = firstline.group(3);
        String otherAccount = "";
        String valuta = "";
        StringBuilder description = new StringBuilder();

        // most transactions have an opposing account after the first space
        if (type.contains(" ")) {
          otherAccount = type.substring(type.indexOf(" ") + 1);
          type = type.substring(0, type.indexOf(" "));
        }

        // get next line which contains further information about the transaction
        String nextLine = splitContent[i + 1];
        Matcher secondline = secondLinePattern.matcher(nextLine);

        if (secondline.find()) {
          valuta = secondline.group(1);
          description = new StringBuilder(secondline.group(2));

          // descriptions can span several lines, so check further
          i++;

          boolean reachedNextTransaction = firstLinePattern.matcher(nextLine).find();
          while (!reachedNextTransaction) {

            description.append(nextLine);
            i++;

            nextLine = splitContent[i + 1];

            if (i + 1 == splitContent.length                          // check if there is a next line
                || nextLine.length() == 1) {                          // skip vertical text on the side of the page
              break;
            }

            if (nextLine.startsWith("Neuer Saldo")) {                 // check if we reached the end
              reachedEnd = true;
              break;
            }

            reachedNextTransaction = firstLinePattern.matcher(nextLine).find();  // check if nextLine is new transaction
          }
        }

        String csvline = MessageFormat.format("{1}{0}{2}{0}{3}{0}{4}{0}{5}{0}{6}{0}{7}",
            ";", date, valuta, otherAccount, type, description, amount, currency);

        sb.append(csvline).append("\n");
      }
    }

    return sb.toString();
  }
}
