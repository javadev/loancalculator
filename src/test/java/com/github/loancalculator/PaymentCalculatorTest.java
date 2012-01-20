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

import com.github.loancalculator.PaymentCalculator.Payment;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author valik
 */
public class PaymentCalculatorTest {
    private PaymentCalculator paymentCalculator;
    
    @Test
    public void calc01() {
        PaymentCalculator.PaymentInput paymentInput = new PaymentCalculator.PaymentInput();
        double[][] params = {
            {1000.0, 12.0, 0.1, 0},
            {3000.0, 13.0, 0.01, 0},
            {1000.0, 12.0, 0.1, 1.2},
            {3000.0, 13.0, 0.01, 3}
        };
        double[][][] expects = {
        {
            {-1000.0, 0.0, 0.0, 0.0},
            {83.38, 83.3, 0.08, 0.0},
            {83.38, 83.3, 0.08, 0.0},
            {83.38, 83.31, 0.07, 0.0},
            {83.38, 83.32, 0.06, 0.0},
            {83.38, 83.32, 0.06, 0.0},
            {83.38, 83.33, 0.05, 0.0},
            {83.38, 83.34, 0.04, 0.0},
            {83.38, 83.35, 0.03, 0.0},
            {83.38, 83.35, 0.03, 0.0},
            {83.38, 83.36, 0.02, 0.0},
            {83.38, 83.37, 0.01, 0.0},
            {83.36, 83.35, 0.01, 0.0},
            {1000.54, 1000.0, 0.54, 0.0}
        },
        {
            {-3000.0, 0.0, 0.0, 0.0},
            {230.78, 230.75, 0.03, 0.0},
            {230.78, 230.76, 0.02, 0.0},
            {230.78, 230.76, 0.02, 0.0},
            {230.78, 230.76, 0.02, 0.0},
            {230.78, 230.76, 0.02, 0.0},
            {230.78, 230.76, 0.02, 0.0},
            {230.78, 230.77, 0.01, 0.0},
            {230.78, 230.77, 0.01, 0.0},
            {230.78, 230.77, 0.01, 0.0},
            {230.78, 230.77, 0.01, 0.0},
            {230.78, 230.77, 0.01, 0.0},
            {230.78, 230.78, 0.00, 0.0},
            {230.82, 230.82, 0.00, 0.0},
            {3000.18, 3000.0, 0.18, 0.0}
        },
        {
            {-1000.0, 0.0, 0.0, 0.0},
            {95.38, 83.3, 0.08, 12.0},
            {95.38, 83.3, 0.08, 12.0},
            {95.38, 83.31, 0.07, 12.0},
            {95.38, 83.32, 0.06, 12.0},
            {95.38, 83.32, 0.06, 12.0},
            {95.38, 83.33, 0.05, 12.0},
            {95.38, 83.34, 0.04, 12.0},
            {95.38, 83.35, 0.03, 12.0},
            {95.38, 83.35, 0.03, 12.0},
            {95.38, 83.36, 0.02, 12.0},
            {95.38, 83.37, 0.01, 12.0},
            {95.36, 83.35, 0.01, 12.0},
            {1144.54, 1000.0, 0.54, 144.0}
        },
        {
            {-3000.0, 0.0, 0.0, 0.0},
            {320.78, 230.75, 0.03, 90.0},
            {320.78, 230.76, 0.02, 90.0},
            {320.78, 230.76, 0.02, 90.0},
            {320.78, 230.76, 0.02, 90.0},
            {320.78, 230.76, 0.02, 90.0},
            {320.78, 230.76, 0.02, 90.0},
            {320.78, 230.77, 0.01, 90.0},
            {320.78, 230.77, 0.01, 90.0},
            {320.78, 230.77, 0.01, 90.0},
            {320.78, 230.77, 0.01, 90.0},
            {320.78, 230.77, 0.01, 90.0},
            {320.78, 230.78, 0.00, 90.0},
            {320.82, 230.82, 0.00, 90.0},
            {4170.18, 3000.0, 0.18, 1170.0}
        }
        };
        int param = 0;
        for (double[][] expected : expects) {
            paymentInput.amountOfLoan = params[param][0];
            paymentInput.duration = (long) params[param][1];
            paymentInput.rate = params[param][2];
            paymentInput.monthlyFee = params[param][3];
            paymentInput.monthly_charge = calc_monthly_charge(paymentInput.amountOfLoan,
                paymentInput.rate/100/12, paymentInput.duration);
            paymentInput.monthly_charge += paymentInput.monthlyFee / 100 * paymentInput.amountOfLoan;
            paymentInput.openingFee = 0;
            paymentCalculator = new PaymentCalculator(paymentInput);
            List<Payment> payments = paymentCalculator.calc();
            int index = 0;
            for (Payment payment : payments) {
                assertEquals("Unexpected totalInstalmentPayment " + index, expected[index][0], payment.totalInstalmentPayment, 0.99);
                assertEquals("Unexpected capitalPayment " + index, expected[index][1], payment.capitalPayment, 0.99);
                assertEquals("Unexpected interestPayment " + index, expected[index][2], payment.interestPayment, 0.99);
                assertEquals("Unexpected monthlyFeePayment " + index, expected[index][3], payment.monthlyFeePayment, 0.99);
                index += 1;
            }
            param += 1;
        }
    }
    
    private double calc_monthly_charge(double credit, double rate, long term) {
        return credit * rate * Math.pow((1 + rate), term) / (Math.pow((1 + rate), term) - 1);
    }
}
