package nb.queryDomain;

import java.io.Serializable;
import java.math.BigDecimal;

public class BidDetails implements Serializable {

    private Long bidId;
    private BigDecimal startPrice;

    public BidDetails() {
    }
    public BidDetails(Long bidId, BigDecimal startPrice) {
        this.bidId = bidId;
        this.startPrice = startPrice;
    }

    public Long getBidId() {
        return bidId;
    }
    public void setBidId(Long bidId) {
        this.bidId = bidId;
    }

    public BigDecimal getStartPrice() {
        return startPrice;
    }
    public void setStartPrice(BigDecimal startPrice) {
        this.startPrice = startPrice;
    }

    @Override
    public String toString() {
        return "BidDetails{" +
                "bidId=" + bidId +
                ", startPrice=" + startPrice +
                '}';
    }
}
