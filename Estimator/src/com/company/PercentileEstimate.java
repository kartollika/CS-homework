package com.company;

import java.util.Random;

public class PercentileEstimate {

    private int m = 0;
    private int step = 1;
    private double p;
    private int dir = 0;
    private final Random rand;

    public PercentileEstimate(double p) {
        rand = new Random();
        this.p = 1 - p;
    }

    public void add(int s) {

        double r = rand.nextDouble();
        if (s > m && r > p) {
            step = (dir > 0) ? incStep(step) : 1;
            m += step;
            dir = 1;
            if (m > s) {
                step -= m-s;
                m = s;
            }
        } else if (s < m && r > 1-p) {
            step = (dir == 0) ? incStep(step) : 1;
            m -= step;
            dir = 0;
            if (m < s) {
                step -= s-m;
                m = s;
            }
        }
    }

    private int incStep(int step) {
        return (int) (step + Math.max(1, step * 0.4));
    }

    public int getEstimate() {
        return m;
    }
}
