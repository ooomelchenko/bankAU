package nb.queryDomain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class AcceptPriceHistory implements Serializable {

    private Date date;
    private BigDecimal acceptedPrice;

    public AcceptPriceHistory() {
    }
    public AcceptPriceHistory(Date date, BigDecimal acceptedPrice) {
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

    @Override
    public String toString() {
        return "AcceptPriceHistory{" +
                "date=" + date +
                ", acceptedPrice=" + acceptedPrice +
                '}';
    }

}
