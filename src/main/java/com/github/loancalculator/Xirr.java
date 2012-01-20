/*
 * $Id$
 *
 * Copyright 2012 Valentyn Kolesnikov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.loancalculator;

import java.util.List;

/**
 *
 * @author valik
 */
public class Xirr {
    private static final double ACCURACY_1E_100 = 1e+100;
    private static final double DAYS_IN_YEAR = 365.0;
    private static final double TOL = 0.001;

    /** Fxirr. */
    public class Fxirr {
        private double x;

        /** Constructor.
         * @param x the value
         */
        public Fxirr(double x) {
            this.x = x;
        }

        double exec(double p, double dt, double dt0) {
            return p * Math.pow(1.0 + x, (dt0 - dt) / DAYS_IN_YEAR);
        }
    }

    /** Dfxirr. */
    public class Dfxirr {
        private double x;

        /** Constructor.
         * @param x the value
         */
        public Dfxirr(double x) {
            this.x = x;
        }

        double exec(double p, double dt, double dt0) {
            return (1.0 / DAYS_IN_YEAR) * (dt0 - dt) * p * Math.pow(x + 1.0, ((dt0 - dt) / DAYS_IN_YEAR) - 1.0);
        }
    }

    /** TotalFxirr. */
    public class TotalFxirr {
        private double[] payments;
        private double[] days;

        /** Constructor.
         * @param payments the payments
         * @param days the days
         */
        public TotalFxirr(double[] payments, double[] days) {
            this.payments = payments;
            this.days = days;
        }

        double exec(double x) {
            double resf = 0.0;
            Fxirr fxirr = new Fxirr(x);
            for (int i = 0; i < payments.length; i++) {
                resf += fxirr.exec(payments[i], days[i], days[0]);
            }
            return resf;
        }
    }

    /** TotalDfxirr. */
    public class TotalDfxirr {
        private double[] payments;
        private double[] days;

        /** Constructor.
         * @param payments the payments
         * @param days the days
         */
        public TotalDfxirr(double[] payments, double[] days) {
            this.payments = payments;
            this.days = days;
        }

        double exec(double x) {
            double resf = 0.0;
            Dfxirr dfxirr = new Dfxirr(x);
            for (int i = 0; i < payments.length; i++) {
                resf += dfxirr.exec(payments[i], days[i], days[0]);
            }
            return resf;
        }
    }

    private double newtonsMethod(double guess, TotalFxirr f, TotalDfxirr df) {
        double x0 = guess;
        double x1 = 0.0;
        double err = ACCURACY_1E_100;
        while (err > TOL) {
            x1 = x0 - f.exec(x0) / df.exec(x0);
            err = Math.abs(x1 - x0);
            x0 = x1;
        }
        return x0;
    }

    /**
     * Calculates xirr for array paymants and days.
     * @param guess the guess
     * @param payments the payments
     * @param days the days
     * @return the IRR
     */
    public double calc(double guess, List<Double> payments, List<Double> days) {
        return newtonsMethod(guess, new TotalFxirr(listToArray(payments), listToArray(days)),
            new TotalDfxirr(listToArray(payments), listToArray(days)));
    }
    
    private double[] listToArray(List<Double> data) {
        double[] result = new double[data.size()];
        int index = 0;
        for(Double d : data) {
            result[index] = d;
            index += 1;
        }
        return result;
    }
}
