package nb.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "Credits_History")
public class CreditHistory implements Serializable {
    @Id
    @Column(name = "KEY_N")
    private Long id;
    @Column(name = "ND_NLS")
    private Long nd;
    @Column(name = "F_IDCODE")
    private String inn;
    @Column(name = "Start_PRICE")
    private BigDecimal startPrice;
    @Column(name = "FIRST_Start_PRICE")
    private BigDecimal firstStartPrice;

    @Column(name = "ACCEPTED_PRICE")
    private BigDecimal acceptPrice;

    @Column(name = "LOT_ID")
    private Long lotId;
    @Column(name = "user_name")
    private String user;
    @Column(name = "CHANGE_DATE")
    private Date changeDate;

    public CreditHistory() {
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getNd() {
        return nd;
    }
    public void setNd(Long nd) {
        this.nd = nd;
    }

    public String getInn() {
        return inn;
    }
    public void setInn(String inn) {
        this.inn = inn;
    }

    public BigDecimal getStartPrice() {
        return startPrice;
    }
    public void setStartPrice(BigDecimal startPrice) {
        this.startPrice = startPrice;
    }

    public BigDecimal getFirstStartPrice() {
        return firstStartPrice;
    }
    public void setFirstStartPrice(BigDecimal firstStartPrice) {
        this.firstStartPrice = firstStartPrice;
    }

    public BigDecimal getAcceptPrice() {
        return acceptPrice;
    }
    public void setAcceptPrice(BigDecimal acceptPrice) {
        this.acceptPrice = acceptPrice;
    }

    public Long getLotId() {
        return lotId;
    }
    public void setLotId(Long lotId) {
        this.lotId = lotId;
    }

    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }

    public Date getChangeDate() {
        return changeDate;
    }
    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public CreditHistory(String user, Credit credit) {
        id=credit.getId();
        nd=credit.getNd();
        inn=credit.getInn();
        startPrice=credit.getStartPrice();
        firstStartPrice=credit.getFirstStartPrice();
        acceptPrice=credit.getAcceptPrice();
        lotId=credit.getLot();
        this.user=user;
        changeDate=new Date();
    }
    @Override
    public String toString() {
        return "Credit{" +
                "id=" + id +
                ", nd=" + nd +
                ", inn='" + inn + '\'' +
                ", startPrice=" + startPrice +
                ", firstStartPrice=" + firstStartPrice +
                ", acceptPrice=" + acceptPrice +
                ", lot=" + lotId +
                '}';
    }

}