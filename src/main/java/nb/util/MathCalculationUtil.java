package nb.util;

import java.math.BigDecimal;

public class MathCalculationUtil {

    public MathCalculationUtil() {
    }

    public static BigDecimal getCoefficient(BigDecimal divident, BigDecimal divisor) {
        try {
            return divident.divide(divisor, 10, BigDecimal.ROUND_HALF_UP);
        } catch (NullPointerException e) {
            return BigDecimal.valueOf(0);
        }
    }
}
