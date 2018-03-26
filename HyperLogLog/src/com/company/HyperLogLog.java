package com.company;

import com.google.common.hash.Hashing;

import static java.lang.Math.*;

class HyperLogLog {
    private int m;
    private int[] streams;
    private double alpha;

    HyperLogLog(int p) {
        m = (int) pow(2, p);
        streams = new int[m];
        alpha = getAlpha(m);
    }

    void add(String s) {
        int bitmask = m - 1;
        long hash = getHash(s);
        int stream = (int) (hash & bitmask);
        hash |= bitmask;
        streams[stream] = Integer.max(Long.numberOfLeadingZeros(hash) + 1, streams[stream]);
    }

    private long getHash(String s) {
        return Hashing.murmur3_128().hashString(s).asLong();
    }

    private double getAlpha(int m) {
        switch (m) {
            case 16:
                return 0.673;
            case 32:
                return 0.697;
            default:
                return 0.7213 / (1 + 1.079 / m);
        }
    }

    int getResult() {
        double sum = 0;
        int countOfZeros = 0;

        for (int elem : streams) {
            sum += pow(2, -1 * elem);
            if (elem == 0)
                countOfZeros++;
        }

        double E = floor((alpha * m * m) / sum);

        if (E <= 5 * m) {
            if (countOfZeros > 0) {
                E = m * log((double) m / countOfZeros);
            }
        }
        return (int) E;
    }

}
