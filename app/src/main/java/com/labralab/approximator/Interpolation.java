package com.labralab.approximator;

/**
 * Created by pc on 09.05.2018.
 */

public class Interpolation {

    //Входной методкласса
    public double[][] getLine(double[][] inputData) {

        double[][] result;

        double a = 0; //левое крайнее значение
        double b = 1400; //правое крайнее значение
        int fragments = 1400; //количество точек для постоения

        result = new double[fragments][2];

        double[] xv;
        double[] yv;

        xv = new double[inputData.length];
        yv = new double[inputData.length];
        for (int i = 0; i < inputData.length; i++) {
            xv[i] = inputData[i][0];
            yv[i] = inputData[i][1];
        }

        //формируем результирующий список
        for (int i = 0; i < fragments; i++) {
            double x = a + (b - a) / (fragments - 1) * i;

            result[i][0] = x;
            result[i][1] = lagrangePolynomial(x, xv, yv);
        }

        return result;
    }

    //Расчет полинома
    private double lagrangePolynomial(double x, double[] xv, double[] yv) {

        int size = xv.length;
        double sum = 0;
        for (int i = 0; i < size; i++) {
            double mul = 1;
            for (int j = 0; j < size; j++) {
                if (i != j) {
                    mul *= (x - xv[j]) / (xv[i] - xv[j]);
                }
            }
            sum += yv[i] * mul;
        }
        return sum;
    }
}

