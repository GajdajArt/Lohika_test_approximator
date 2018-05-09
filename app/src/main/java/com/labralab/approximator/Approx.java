package com.labralab.approximator;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.QRDecomposition;
import cern.colt.matrix.linalg.SingularValueDecomposition;
import cern.colt.matrix.linalg.Algebra;

/**
 * Код позаимствован из библиотеки JSS
 */

public class Approx {


    public static double[] calcCoefficients (double[][] input, int deg) {

        double[] X = new double[input.length];
        double[] Y = new double[input.length];

        for(int i = 0; i < input.length; i++){

            X[i] = input[i][0];
            Y[i] = input[i][1];
        }


        final int n = X.length;
        if (n != Y.length)
            throw new IllegalArgumentException ("Lengths of X and Y are not equal");
        if (n < deg + 1)
            throw new IllegalArgumentException ("Not enough points");

        final double[] xSums = new double[2 * deg + 1];
        final double[] xySums = new double[deg + 1];
        xSums[0] = n;
        for (int i = 0; i < n; i++) {
            double xv = X[i];
            xySums[0] += Y[i];
            for (int j = 1; j <= 2 * deg; j++) {
                xSums[j] += xv;
                if (j <= deg)
                    xySums[j] += xv * Y[i];
                xv *= X[i];
            }
        }
        final DoubleMatrix2D A = new DenseDoubleMatrix2D (deg + 1, deg + 1);
        final DoubleMatrix2D B = new DenseDoubleMatrix2D (deg + 1, 1);
        for (int i = 0; i <= deg; i++) {
            for (int j = 0; j <= deg; j++) {
                final int d = i + j;
                A.setQuick (i, j, xSums[d]);
            }
            B.setQuick (i, 0, xySums[i]);
        }

        return solution(A, B, deg + 1);
    }

    private static double[] solution (DoubleMatrix2D X, DoubleMatrix2D Y, int k) {
        // Solve X * Beta = Y for Beta
        // Only the first column of Y is used
        // k is number of beta coefficients

        QRDecomposition qr = new QRDecomposition(X);

        if (qr.hasFullRank()) {
            DoubleMatrix2D B = qr.solve(Y);
            return B.viewColumn(0).toArray();

        } else {
            DoubleMatrix1D Y0 = Y.viewColumn(0);   // first column of Y
            SingularValueDecomposition svd = new SingularValueDecomposition(X);
            DoubleMatrix2D S = svd.getS();
            DoubleMatrix2D V = svd.getV();
            DoubleMatrix2D U = svd.getU();
            Algebra alg = new Algebra();
            DoubleMatrix2D Ut = alg.transpose(U);
            DoubleMatrix1D g = alg.mult(Ut, Y0);    // Ut*Y0

            for (int j = 0; j < k; j++) {
                // solve S*p = g for p;  S is a diagonal matrix
                double x = S.getQuick(j, j);
                if (x > 0.) {
                    x = g.getQuick(j) / x;   // p[j] = g[j]/S[j]
                    g.setQuick(j, x);        // overwrite g by p
                } else
                    g.setQuick(j, 0.);
            }
            DoubleMatrix1D beta = alg.mult(V, g);   // V*p
            return beta.toArray();
        }
    }

}
