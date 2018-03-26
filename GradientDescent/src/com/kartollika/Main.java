package com.kartollika;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        ArrayList<Double[]> _x = new ArrayList<>();
        ArrayList<Double> _y = new ArrayList<>();

        Scanner input = new Scanner(System.in);
        Scanner scannerFile = null;
        try {
            scannerFile = new Scanner(new File("Features_Variant_1.csv"));
        } catch (FileNotFoundException e) {
            System.out.println("File hasn't found");
            System.exit(-1);
        }

        System.out.print("Learning rate: ");
        double learn = input.nextDouble();
        System.out.print("Norm error: ");
        double error = input.nextDouble();
        System.out.print("Max number of gradient iterations: ");
        int maxIterations = input.nextInt();

        while (scannerFile.hasNext()) {
            String s = scannerFile.nextLine();
            _x = parseLine(s, _x);
            _y = parseY(s, _y);
        }

        testData(_x, _y, learn, error, maxIterations);

        input.close();
    }

    private static void testData(ArrayList<Double[]> _x, ArrayList<Double> _y, double learn, double error, int maxIterations) {
        ArrayList<Double[]> trainPartX = new ArrayList<>();
        ArrayList<Double> trainPartY = new ArrayList<>();
        ArrayList<Double[]> testPartX = new ArrayList<>();
        ArrayList<Double> testPartY = new ArrayList<>();
        ArrayList<Double> baseline = new ArrayList<>();

        int d = _x.get(0).length;
        double[][] table = new double[7][d + 3];
        double[] res;

        _y = normY(_y, _x);
        _x = normX(_x);

        for (Double[] a_x : _x) {
            baseline.add(a_x[32] / 24);
        }

        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < _x.size(); ++j) {
                if (j % 5 == i) {
                    testPartY.add(_y.get(j));
                    testPartX.add(copy(_x.get(j)));
                } else {
                    trainPartY.add(_y.get(j));
                    trainPartX.add(copy(_x.get(j)));
                }
            }

            GradientDescent gradientDescent = new GradientDescent(trainPartX, trainPartY, learn, error, maxIterations);
            gradientDescent.gradientDescent();
            res = gradientDescent.getResult();

            table[i][0] = gradientDescent.getRMSE(testPartX, testPartY);
            table[i][1] = gradientDescent.getRMSE(trainPartX, trainPartY);
            table[i][2] = normBaseline(testPartY, baseline);

            for (int k = 0; k < d; ++k) {
                table[i][k + 3] = res[k];
            }
            /*System.out.println("\n");
            System.out.println("Train part error: " + trainRMSE);
            System.out.println("Test part error: " + testRMSE);
            System.out.println("Baseline error: " + baselineRMSE);*/
        }

        table[5][0] = table[5][1] = table[5][2] = 0.;
        table[6][0] = table[6][1] = table[6][2] = 0.;
        for (int i = 0; i < d; ++i) {
            table[5][i + 3] = average(table, i);
        }

        for (int i = 0; i < d; ++i) {
            table[6][i + 3] = dispersion(table, i);
        }

        printTable(table);
    }

    private static double dispersion(double[][] table, int row) {
        double dispersion = 0;

        for (int i = 0; i < table.length; ++i) {
            dispersion += Math.pow(table[i][row + 3], 2);
        }

        return Math.sqrt(dispersion / 5);
    }

    private static double average(double[][] table, int row) {
        double average = 0;
        for (int i = 0; i < table.length; ++i) {
            average += table[i][row + 3];
        }
        return average / 5;
    }

    private static void printTable(double[][] table) {
        System.out.println();
        for (int i = 0; i < table[0].length; ++i) {
            for (int j = 0; j < table.length; ++j) {
                if (j == 0 && i > 2) {
                    System.out.printf("%2d\t", i - 2);
                } else if (i < 3 && j == 0) {
                    System.out.print(" -\t");
                }
                if (j > 4 && i < 3) {
                    continue;
                }
                System.out.printf("%15.7f\t ", table[j][i]);
            }
            System.out.println();
        }
    }

    private static Double[] copy(Double[] src) {
        Double[] dest = new Double[src.length];
        System.arraycopy(src, 0, dest, 0, src.length);
        return dest;
    }

    private static ArrayList<Double> normY(ArrayList<Double> _y, ArrayList<Double[]> _x) {
        for (int i = 0; i < _y.size(); ++i) {
            _y.set(i, _y.get(i) / _x.get(i)[38]);
        }

        /*double average = 0;
        for (int i = 0; i < _y.size(); ++i) {
            average += _y.get(i);
        }
        average /= _y.size();

        double dispersion = 0;
        for (int i = 0; i < _y.size(); ++i) {
            dispersion += Math.pow(_y.get(i) - average, 2);
        }
        dispersion = Math.sqrt(dispersion / _y.size());

        for (int i = 0; i < _y.size(); ++i) {
            _y.set(i, _y.get(i) / dispersion);
        }*/

        return _y;
    }

    private static ArrayList<Double[]> normX(ArrayList<Double[]> _x) {
        int d = 54;
        int n = _x.size();

        for (int j = 0; j < d; ++j) {
            double average = 0;
            for (int i = 0; i < n; ++i) {
                average += _x.get(i)[j];
            }
            average /= n;

            double dispersion = 0;
            for (int i = 0; i < n; ++i) {
                dispersion += Math.pow(_x.get(i)[j] - average, 2);
            }
            dispersion = Math.sqrt(dispersion / n);

            for (int i = 0; i < n; ++i) {
                if (dispersion != 0) {
                    _x.get(i)[j] = (_x.get(i)[j] - average) / dispersion;
                }
            }
        }
        return _x;
    }

    private static Double normBaseline(ArrayList<Double> _y, ArrayList<Double> baseline) {
        double norm = 0;
        for (int i = 0; i < _y.size(); ++i) {
            norm += Math.pow(baseline.get(i) - _y.get(i), 2);
        }
        return Math.sqrt(norm / (2 * _y.size()));
    }

    private static ArrayList<Double> parseY(String s, ArrayList<Double> _y) {
        String[] splitted = s.split(",");
        _y.add(Double.valueOf(splitted[53]));
        return _y;
    }

    private static ArrayList<Double[]> parseLine(String s, ArrayList<Double[]> _x) {
        String[] splitted = s.split(",");
        Double[] params = new Double[54];
        for (int i = 0; i < 54; ++i) {
            if (i == 53) {
                params[i] = 1.;
                break;
            }
            params[i] = Double.valueOf(splitted[i]);
        }
        _x.add(params);
        return _x;
    }
}
