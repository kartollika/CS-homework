package com.company;

import java.io.*;
import java.util.Scanner;

import static java.lang.Math.*;

public class Main {

    public static void main(String[] args) {

        Scanner stdin = new Scanner(System.in);
        long n;
        double prob;

        Scanner labels = null;
        labels = getScanner(labels);

        System.out.print("Count of viewed lines: ");
        n = stdin.nextLong();

        System.out.print("Probability of 'false' alert: ");
        prob = stdin.nextDouble();

        BloomFilter bloomFilter = new BloomFilter(n, prob);
        String s;
        for (int i = 0; i < n; ++i) {
            s = labels.nextLine();
            bloomFilter.add(s);
        }
        labels.close();

        labels = getScanner(labels);

        int fn = 0;
        for (int i = 0; i < n; ++i) {
            s = labels.nextLine();
            fn += bloomFilter.checkFalseNegative(s);
        }
        if (fn == 0) {
            System.out.println("This works fine");
        } else {
            System.out.println("This doesn't work");
        }

        int fp = 0;
        long countOfLines = 0;
        while (labels.hasNext()) {
            s = labels.nextLine();
            ++countOfLines;
            fp += bloomFilter.checkFalsePositive(s);
        }

        System.out.println("New p = " + (double) fp / countOfLines);
    }

    private static Scanner getScanner(Scanner labels) {
        try {
            labels = new Scanner(new File("labels_en.ttl"));
        } catch (FileNotFoundException e) {
            System.out.println("File with lines wasn't found");
            System.exit(1);
        }
        labels.useDelimiter("\n");
        return labels;
    }
}

class BloomFilter {

    private BitArray bitArray;
    private String[] salt;
    private int k;
    private int m;

    BloomFilter(long n, double p) {
        this.m = calcSizeOfBArray(n, p);
        this.k = calcCountOfHashes(n, m);
        bitArray = new BitArray(m);
        salt = inputSalt();
    }

    private String[] inputSalt() {
        Scanner extens = null;
        try {
            extens = new Scanner(new File("extens.txt"));
        } catch (FileNotFoundException e) {
            System.out.println("File wasn't found");
        }

        salt = new String[30];
        for (int i = 0; i < 30; ++i) {
            assert extens != null;
            salt[i] = extens.nextLine();
        }
        extens.close();
        return salt;
    }

    private int calcSizeOfBArray(long n, double p) {
        return (int) abs(ceil(n * log(p) / pow(log(2), 2)));
    }

    private int calcCountOfHashes(long n, int m) {
        int k1 = (int) ceil(m / n * log(2));
        int k2 = (int) floor(m / n * log(2));

        double pAtK1 = pow((1 - pow(E, -k1 * n / m)), k1);
        double pAtK2 = pow((1 - pow(E, -k2 * n / m)), k2);

        if (pAtK1 < pAtK2)
            return k1;
        else
            return k2;
    }

    void add(String s) {
        for (int i = 0; i < k; ++i) {
            int hCode = calcHash(s, salt[i], m);
            int bit = hCode / 32;
            int mask = (1 << hCode % 32);
            bitArray.add(bit, mask);
        }
    }

    /*int mayContain(String s) {
        int bit;
        int checked = 0;
        for(int i = 0; i < k; ++i) {
            bit = calcHash(s, salt[i], m);
            if (bitArray.isBitContain(bit)) {
                ++checked;
            }
        }
        return (checked == k) ? 1 : 0;
    }*/

    int checkFalseNegative(String s) {
        int bit;
        int fn = 0;
        for (int i = 0; i < k; ++i) {
            bit = calcHash(s, salt[i], m);
            if (!bitArray.isBitContain(bit)) {
                fn++;
            }
        }
        return (fn == 0) ? 0 : 1;
    }

    double checkFalsePositive(String s) {
        long fp = 0;
        int bit;

        for (int i = 0; i < k; ++i) {
            bit = calcHash(s, salt[i], m);
            if (bitArray.isBitContain(bit)) {
                fp++;
            }
        }
        return (fp == k) ? 1 : 0;
    }

    private int calcHash(String s, String ext, long m) {
        return (int) abs((s + ext).hashCode() % m);
    }
}

class BitArray {
    private int[] bits;

    BitArray(int m) {
        bits = new int[m / 32 + 1];
    }

    void add(int bit, int mask) {
        bits[bit] |= mask;
    }

    boolean isBitContain(int bit) {
        return (bits[bit / 32] & (1 << bit % 32)) != 0;
    }
}