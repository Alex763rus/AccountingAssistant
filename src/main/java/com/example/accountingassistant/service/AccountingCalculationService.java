package com.example.accountingassistant.service;

import com.example.accountingassistant.enums.CalculateType;
import com.example.accountingassistant.exception.CalculationException;
import com.example.accountingassistant.model.CalcData;
import com.example.accountingassistant.model.jpa.Calculation;
import lombok.val;
import org.springframework.stereotype.Component;

import static com.example.accountingassistant.constant.Constant.SPACE;
import static com.example.accountingassistant.enums.CalculateType.EXPERT;
import static com.example.accountingassistant.enums.CalculateType.STANDART;

@Component
public class AccountingCalculationService {

    public boolean checkValidateCalculation(Calculation calculation) {
        try {
            calculate(calculation, STANDART);
            calculate(calculation, EXPERT);
        } catch (CalculationException e) {
            return false;
        }
        return true;
    }

    public int calculate(Calculation calculation, CalculateType calculateType) throws CalculationException {
        try {
            val calculatedValue =
                    gbaCalculate(
                            calculation.getForm().ordinal()
                            , calculation.getMode().ordinal()
                            , calculation.getEmployee().ordinal()
                            , calculation.getMoneyTurnover().ordinal()
                            , calculation.getOperation().ordinal()
                            , calculation.getNdsAgent()
                            , calculation.getNdflAgent()
                            , calculation.getAgencyContract().ordinal()
                            , calculation.getVed().ordinal()
                            , calculation.getDetached()
                            , calculation.getCashBox().ordinal()
                            , calculateType.ordinal()
                    );
            return (int) (calculatedValue + calculatedValue * 10.0 / 100);
        } catch (Exception ex) {
            throw new CalculationException(ex.getMessage());
        }
    }


    private void test() {
        testGbaCalculate(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8990, 9990, 14490);
        testGbaCalculate(0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9990, 11490, 15490);
        testGbaCalculate(0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 11990, 13990, 19990);
        testGbaCalculate(0, 4, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 19539, 22989, 33339);
        testGbaCalculate(0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 18990, 19990, 24490);
        testGbaCalculate(0, 0, 0, 8, 0, 0, 0, 0, 0, 0, 0, 0, 48990, 49990, 54490);
        testGbaCalculate(0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 77777, 21000, 25500);
        testGbaCalculate(0, 0, 0, 0, 0, 500, 77, 0, 0, 0, 0, 0, 77777, 548490, 14490);
        testGbaCalculate(0, 0, 0, 0, 0, 500, 77, 3, 0, 0, 0, 0, 77777, 551490, 17490);
        testGbaCalculate(0, 0, 0, 0, 0, 500, 77, 3, 1, 0, 0, 0, 77777, 553990, 17490);
        testGbaCalculate(0, 0, 0, 0, 0, 500, 77, 3, 1, 45, 0, 0, 77777, 666490, 129990);
        testGbaCalculate(0, 0, 0, 0, 0, 500, 77, 3, 1, 45, 5, 0, 77777, 681490, 144990);
        int i = 0;
    }

    private String testGbaCalculate(int b1, int b2, int b3, int c1, int c2, int c3, int c4, int c5, int c6, int c7, int c8, int type, int expectedResult0, int expectedResult1, int expectedResult2) {
        val textResult = new StringBuilder();
        try {
//            val result0 = gbaCalculate(b1, b2, b3, c1, c2, c3, c4, c5, c6, c7, c8, 0);
            val result1 = gbaCalculate(b1, b2, b3, c1, c2, c3, c4, c5, c6, c7, c8, 1);
            val result2 = gbaCalculate(b1, b2, b3, c1, c2, c3, c4, c5, c6, c7, c8, 2);
            textResult//.append("RESULT:").append(result0).append(SPACE).append(result0 == expectedResult0).append(", ")
                    .append(SPACE).append(result1).append(SPACE).append(result1 == expectedResult1).append(", ")
                    .append(SPACE).append(result2).append(SPACE).append(result2 == expectedResult2);

        } catch (Exception ex) {
            textResult.append("ОШИБКА" + ex.getMessage());
        }
        System.out.println(textResult);
        return textResult.toString();
    }

    private int gbaCalculate(int b1, int b2, int b3, int c1, int c2, int c3, int c4, int c5, int c6, int c7, int c8, int type) {
        ++b1;
        ++b2;
        ++b3;
        c1 = (c1 + 2) * 5;
        ++c2;

        int total = 0;
        int base_offset = (b1 - 1) + (b2 - 1) * 6 + type * 2;
        String base = CalcData.data[b3 - 1][base_offset];
        total = total + Integer.parseInt(base);
        double c1_val;
        if (c1 <= 10) {
            c1_val = 0;
        } else {
            c1_val = (Math.ceil((c1 - 10) / 5)) * 5000;
        }
        ;
        total = total + (int) c1_val;
        String c2_val = CalcData.data[c2 + 28 - 1][base_offset];
        total = total + Integer.parseInt(c2_val);
        int c3_val = Integer.parseInt(CalcData.data[44 - 1][base_offset]) * c3;
        total = total + c3_val;
        int c4_val = Integer.parseInt(CalcData.data[45 - 1][base_offset]) * c4;
        total = total + c4_val;
        String c5_val = CalcData.data[c5 + 46 - 1][base_offset];
        total = total + Integer.parseInt(c5_val);
        String c6_val = CalcData.data[c6 + 52 - 1][base_offset];
        total = total + Integer.parseInt(c6_val);
        int c7_val = Integer.parseInt(CalcData.data[55 - 1][base_offset]) * c7;
        total = total + c7_val;

        total = total + getC8val(c8);
        return total;
    }

    private int getC8val(int c8) {
        switch (c8) {
            case 0:
                return 0;
            case 1:
                return 3000;
            case 2:
                return 5000;
            case 3:
                return 10000;
            default:
                return 0;
        }
    }
}
