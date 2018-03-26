package com.kartollika;

import java.util.ArrayList;

class GradientDescent {

    private ArrayList<Double[]> _x;
    private ArrayList<Double> _y;
    private double learnRate;
    private double normError;
    private int itersMax;
    private double[] res;

    GradientDescent(ArrayList<Double[]> _x, ArrayList<Double> _y, double learnRate, double normError, int itersMax) {
        this._x = _x;
        this._y = _y;
        this.learnRate = learnRate;
        this.normError = normError;
        this.itersMax = itersMax;
        res = new double[_x.size()];
    }

    void gradientDescent() {
        int d = _x.get(0).length;
        double[] oldParam = new double[d];
        double[] param = new double[d];
        int m = 1;

        do {
            double[] err = getErr(param);
            System.arraycopy(param, 0, oldParam, 0, d);

            for (int i = 0; i < param.length; ++i) {
                param[i] -= learnRate / Math.sqrt(m) * err[i];
            }

            ++m;

            if (m % 100 == 0) System.out.println(m);
        } while (norm(param, oldParam) >= normError && m < itersMax);

        System.arraycopy(param, 0, res, 0, param.length);
    }

    private double norm(double[] _x, double[] _y) {
        double norm = 0;
        double normingCoef = 0;

        for (int i = 0; i < _x.length; ++i) {
            double f = Math.pow(_x[i] - _y[i], 2);
            norm += f;
            normingCoef += Math.pow(_y[i], 2);
        }
        return Math.sqrt(norm / normingCoef);
    }


    double[] getResult() {
        return res;
    }

    private double[] getErr(double[] param) {
        double[] err = new double[_x.get(0).length];
        double t;

        for (int i = 0; i < _x.size(); ++i) {
            t = scalarMult(_x.get(i), param) - _y.get(i);

            for (int j = 0; j < _x.get(0).length; ++j) {
                err[j] += t * _x.get(i)[j];
            }
        }
        for (int i = 0; i < param.length; ++i) {
            err[i] /= _x.size();
        }
        return err;
    }

    private double scalarMult(Double[] _x, double[] param) {
        double result = 0;
        for (int i = 0; i < _x.length; ++i) {
            result += _x[i] * param[i];
        }
        return result;
    }


    double getRMSE(ArrayList<Double[]> otherX, ArrayList<Double> otherY) {
        double RMSE = 0;
        for (int i = 0; i < otherY.size(); ++i) {
            double q = 0;
            for (int j = 0; j < (otherX.get(0)).length; ++j) {
                q += otherX.get(i)[j] * res[j];
            }
            RMSE += Math.pow(otherY.get(i) - q, 2) / (2 * otherY.size());
        }
        return Math.sqrt(RMSE);
    }
}
