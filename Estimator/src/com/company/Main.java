package com.company;

import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner stdin = new Scanner(System.in);

        Random rand = new Random();
        PercentileEstimate estimator1 = new PercentileEstimate(0.25);
        PercentileEstimate estimator2 = new PercentileEstimate(0.5);
        PercentileEstimate estimator3 = new PercentileEstimate(0.75);

        for (int i = 1; i < 200001; ++i) {
            int l = rand.nextInt(10001);

            estimator1.add(l);
            estimator2.add(l);
            estimator3.add(l);

            if (i % 5000 == 0) {
                System.out.println("m = " + estimator1.getEstimate());
                System.out.println("m = " + estimator2.getEstimate());
                System.out.println("m = " + estimator3.getEstimate());
                System.out.println();
            }
        }
    }
}
