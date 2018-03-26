package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static java.lang.Math.abs;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        Scanner labels;
        Scanner stdin;

        double hyperLogLogResult;
        int p;

        stdin = new Scanner(System.in);
        labels = new Scanner(new File("labels_en.ttl"));

        System.out.print("Enter the p : ");
        p = stdin.nextInt();

        HyperLogLog hyperLogLog = new HyperLogLog(p);

        int k = 0;
        while (labels.hasNext()) {
            String s = labels.nextLine();
            hyperLogLog.add(s);
            if (++k % 200000 == 0) {

                System.out.println(k + " " + hyperLogLog.getResult() + " " +
                        100 * abs(k - hyperLogLog.getResult()) / (double) k + " %");
            }
        }

        hyperLogLogResult = hyperLogLog.getResult();
        System.out.println(k + " " + hyperLogLogResult + " " +
                100 * abs(k - hyperLogLog.getResult()) / (double) k + " %");
    }
}
