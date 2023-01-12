package com.company;

import org.apache.tika.Tika;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Pdf2Csv {

  final static String divider = ";";
  final static String currency = "EUR";
  final static StringBuilder sbAll = new StringBuilder();

  public static void main(String[] arg) {

    File start = new File(arg[0]);

    sbAll.append("Buchung;Valuta;Auftraggeber/Empfänger;Buchungstext;Verwendungszweck;Saldo;Währung;Betrag;Währung\n");
    processPDFsRecursively(start);

    FileWriter fw;
    File all = new File(start.getAbsolutePath() + "\\aggregated.csv");
    try {
      fw = new FileWriter(all);
      fw.write(sbAll.toString());
      fw.close();
    } catch (
        IOException e) {
      e.printStackTrace();
    }
  }

  private static void processPDFsRecursively(File start) {

    File[] files = start.listFiles();
    if (files == null) {
      return;
    }

    for (File file : files) {
      if (file.isDirectory()) {
        processPDFsRecursively(file);
        continue;
      }

      if (file.getName().toLowerCase().endsWith(".pdf")) {
        processPDF(file);
      }
    }
  }

  private static void processPDF(File file) {
    System.out.println("Processing " + file.getName());

    String content = "";
    try {
      content = new Tika().parseToString(file);
    } catch (Exception e) {
      e.printStackTrace();
    }

    boolean reachedEnd = false;

    StringBuilder sb = new StringBuilder();

    sb.append("Buchung;Valuta;Auftraggeber/Empfänger;Buchungstext;Verwendungszweck;Saldo;Währung;Betrag;Währung\n");

    // create array from lines and filtering empty lines
    String[] splitContent = Arrays.stream(content.split("\n")).filter(e -> !e.equals("")).toArray(String[]::new);

    for (int i = 0; i < splitContent.length; i++) {

      if (reachedEnd) {
        break;
      }

      String line = splitContent[i];
      String type;
      String otherAccount;
      String valuta = "";
      StringBuilder description = new StringBuilder();
      Pattern firstLinePattern = Pattern.compile("^(\\d\\d\\.\\d\\d\\.\\d\\d\\d\\d) +(.*) +(.*,\\d\\d)");
      Matcher firstline = firstLinePattern.matcher(line);

      if (firstline.find()) {
        String date = firstline.group(1);

        // some transactions dont have an opposing account
        String group2 = firstline.group(2).trim();
        if (group2.contains(" ")) {
          type = group2.substring(0, group2.indexOf(" "));
          otherAccount = group2.substring(group2.indexOf(" ") + 1);
        } else {
          type = group2;
          otherAccount = "";
        }

        String amount = firstline.group(3);

        // get next line which contains further information about the transaction
        String nextLine = splitContent[i + 1];

        Pattern secondLinePattern = Pattern.compile("^(\\d\\d\\.\\d\\d\\.\\d\\d\\d\\d) +(.*)");
        Matcher secondline = secondLinePattern.matcher(nextLine);

        if (secondline.find()) {
          valuta = secondline.group(1);
          description = new StringBuilder(secondline.group(2));

          // descriptions can span several lines, so check further lines
          i++;

          boolean reachedNextTransaction = firstLinePattern.matcher(splitContent[i + 1]).find();
          while (!reachedNextTransaction) {

            description.append(splitContent[i + 1]);
            i++;

            // check if there is a next line
            if (i + 1 == splitContent.length) {
              break;
            }

            // vertical text on the side of the page
            if (splitContent[i + 1].length() == 1) {
              break;
            }

            if (splitContent[i + 1].startsWith("Neuer Saldo")) {
              reachedEnd = true;
              break;
            }

            reachedNextTransaction = firstLinePattern.matcher(splitContent[i + 1]).find();
          }
        }

        String csvline = date + divider + valuta + divider + otherAccount + divider + type + divider + description +
            divider + "0,00" + divider + currency + divider + amount + divider + currency;

        sb.append(csvline).append("\n");
        sbAll.append(csvline).append("\n");
      }

    }

    System.out.println(sb.toString());

    FileWriter fw;
    try {
      fw = new FileWriter(file.getAbsolutePath().replace("pdf", "csv"));
      fw.write(sb.toString());
      fw.close();
    } catch (
        IOException e) {
      e.printStackTrace();
    }
  }
}
