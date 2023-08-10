package com.example.accountingassistant.service;

import com.example.accountingassistant.enums.CalculateType;
import com.example.accountingassistant.exception.CalculationException;
import com.example.accountingassistant.model.CalcData;
import com.example.accountingassistant.model.jpa.Calculation;
import lombok.val;
import org.springframework.stereotype.Component;

@Component
public class AccountingCalculationService {

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

    private int gbaCalculate(int b1, int b2, int b3, int c1, int c2, int c3, int c4, int c5, int c6, int c7, int c8, int type) {
        ++b1;
        ++b2;
        ++b3;
        c1 = (c1 + 2) * 5;
        ++c2;

        int total = 0;
        int baseOffset = (b1 - 1) + (b2 - 1) * 6 + type * 2;
        String base = CalcData.data[b3 - 1][baseOffset];
        total = total + Integer.parseInt(base);
        total = total + (int) (c1 <= 10 ? 0 : (Math.ceil((c1 - 10) / 5)) * 5000);
        total = total + Integer.parseInt(CalcData.data[c2 + 28 - 1][baseOffset]);
        total = total + Integer.parseInt(CalcData.data[44 - 1][baseOffset]) * c3;
        total = total + Integer.parseInt(CalcData.data[45 - 1][baseOffset]) * c4;
        total = total + Integer.parseInt(CalcData.data[c5 + 46 - 1][baseOffset]);
        total = total + Integer.parseInt(CalcData.data[c6 + 52 - 1][baseOffset]);
        total = total + Integer.parseInt(CalcData.data[55 - 1][baseOffset]) * c7;

        total = total + getC8val(c8);
        return total;
    }

    private int getC8val(int c8) {
        return switch (c8) {
            case 0 -> 0;
            case 1 -> 3000;
            case 2 -> 5000;
            case 3 -> 10000;
            default -> 0;
        };
    }
}
