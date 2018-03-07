package nb.queryDomain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class CreditAccPriceHistory implements Serializable {

    private Date date;
    private BigDecimal acceptedPrice;

    public CreditAccPriceHistory() {
    }
    public CreditAccPriceHistory(Date date, BigDecimal acceptedPrice) {
        this.date = date;
        this.acceptedPrice = acceptedPrice;
    }

    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    public BigDecimal getAcceptedPrice() {
        return acceptedPrice;
    }
    public void setAcceptedPrice(BigDecimal acceptedPrice) {
        this.acceptedPrice = acceptedPrice;
    }

}
