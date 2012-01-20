/*
 * $Id$
 *
 * Copyright (C) 2011 by Valentin Kolesnikov, javadev75@gmail.com
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PaymentCalculator {
    public static class Payment {
        public java.util.Date date = new java.util.Date();
        public double totalInstalmentPayment;
        public double capitalPayment;
        public double interestPayment;
        public double openingFee;
        public double monthlyFee;
        public double monthlyFeePayment;
        public double interestRate;
        public double insurancePayment;
        public int duration;
        public double debetIn;
    }

    public static class PaymentInput {
        public long duration;
        public double amountOfLoan;
        public double rate;
        public double openingFee;
        public double monthlyFee;
        public double insurancePayment;
        public double monthly_charge;
    }
    private PaymentInput paymentInput;

    public PaymentCalculator(PaymentInput paymentInput) {
        this.paymentInput = paymentInput;
    }
    
    public List<Payment> calc() {
        List<Payment> result = new ArrayList<Payment>();
        java.util.Calendar today = java.util.Calendar.getInstance();
        Payment previous = null;
        {
            Payment payment = new Payment();
            payment.date = today.getTime();
            payment.totalInstalmentPayment = -paymentInput.amountOfLoan;
            payment.openingFee = paymentInput.openingFee / 100.00 * paymentInput.amountOfLoan;
            payment.insurancePayment = paymentInput.insurancePayment;
            payment.debetIn = paymentInput.amountOfLoan;
            result.add(payment);
            today.add(java.util.Calendar.MONTH, 1);
            previous = payment;
        }
        double totalCapitalPayment = 0;
        double totalInterestPayment = 0;
        double totalMonthlyPayment = 0;
        for (int index = 0; index < paymentInput.duration; index += 1, today.add(java.util.Calendar.MONTH, 1)) {
            Payment payment = new Payment();
            payment.date = today.getTime();
            payment.monthlyFeePayment = round(paymentInput.monthlyFee / 100.00 * paymentInput.amountOfLoan, 2);
            totalMonthlyPayment += payment.monthlyFeePayment;
            if (index == paymentInput.duration - 1) {
                payment.interestPayment = round(paymentInput.monthly_charge * paymentInput.duration, 2)
                    - paymentInput.amountOfLoan - totalInterestPayment
                    - totalMonthlyPayment;
                payment.totalInstalmentPayment = paymentInput.amountOfLoan - totalCapitalPayment
                        + payment.interestPayment + payment.monthlyFeePayment;
                if (payment.totalInstalmentPayment == 0) {
                    payment.totalInstalmentPayment = 0;
                }
            } else {
                double rest = previous.debetIn - previous.capitalPayment;
                payment.debetIn = rest;
                long days = getDays(previous.date, payment.date);
                payment.interestPayment = round(calcInterest(rest, paymentInput.rate, days), 2);
                payment.totalInstalmentPayment = paymentInput.monthly_charge;
            }
            payment.capitalPayment = round(payment.totalInstalmentPayment - payment.interestPayment
                - payment.monthlyFeePayment, 2);
            totalCapitalPayment += payment.capitalPayment;
            totalInterestPayment += payment.interestPayment;
            result.add(payment);
            previous = payment;
        }
        {
            Payment payment = new Payment();
            payment.date = today.getTime();
            payment.monthlyFeePayment = totalMonthlyPayment;
            payment.interestPayment = totalInterestPayment;
            payment.totalInstalmentPayment = paymentInput.amountOfLoan + totalMonthlyPayment + totalInterestPayment;
            payment.capitalPayment = totalCapitalPayment;
            result.add(payment);
        }
        return result;
    }

    private double round(double value, int number) {
        java.math.BigDecimal bd = new java.math.BigDecimal(Double.toString(value));
        bd = bd.setScale(number, java.math.BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }

    private long getDays(Date fromDate, Date toDate) {
        return (toDate.getTime() - fromDate.getTime()) / (60 * 60 * 1000 * 24);
    }

    private double calcInterest(double sum, double rate, double days) {
        double result = sum * rate * days / (365 * 100);
        return result;
    }
}
