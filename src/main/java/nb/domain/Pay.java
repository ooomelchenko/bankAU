package nb.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "PAYMENTS")
public class Pay implements Serializable {
    @Id
    @SequenceGenerator(name = "sequence", sequenceName = "PAYMENTS_SEQ", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence")
    @Column(name = "ID")
    private Long id;
    @Column(name="PAYMENT_SOURCE")
    private String paySource;
    @Column(name = "Pay_Date")
    private Date date;
    @Column(name = "Pay_Sum")
    private BigDecimal paySum;
    @Column(name = "History_Lot_ID")
    private Long historyLotId;
    @Column(name = "Lot_ID")
    private Long lotId;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    public BigDecimal getPaySum() {
        return paySum;
    }
    public void setPaySum(BigDecimal paySum) {
        this.paySum = paySum;
    }

    public Long getLotId() {
        return lotId;
    }
    public void setLotId(Long lotId) {
        this.lotId = lotId;
    }

    public Long getHistoryLotId() {
        return historyLotId;
    }
    public void setHistoryLotId(Long historyLotId) {
        this.historyLotId = historyLotId;
    }

    public String getPaySource() {
        return paySource;
    }
    public void setPaySource(String paySource) {
        this.paySource = paySource;
    }

    public Pay() {
    }

    public Pay(Lot lot, Date date, BigDecimal paySum) {
        lotId = lot.getId();
        this.date = date;
        this.paySum = paySum;
        this.historyLotId = null;
    }

    public Pay(Lot lot, Date date, BigDecimal paySum, String paySource) {
        this.lotId = lot.getId();
        this.date = date;
        this.paySum = paySum;
        this.paySource = paySource;
        this.historyLotId = null;
    }

    public Pay(Lot lot, Date date, BigDecimal paySum, Long historyLotId) {
        this.lotId = lot.getId();
        this.date = date;
        this.paySum = paySum;
        this.historyLotId = historyLotId;
    }

    public Pay(String paySource, Date date, BigDecimal paySum, Long historyLotId, Lot lot) {
        this.paySource = paySource;
        this.date = date;
        this.paySum = paySum;
        this.historyLotId = historyLotId;
        this.lotId = lot.getId();
    }
}
