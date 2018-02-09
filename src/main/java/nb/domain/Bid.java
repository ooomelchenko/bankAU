package nb.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "BIDS")
public class Bid implements Serializable, Comparable<Bid> {

    @Id
    @SequenceGenerator(name = "sequence", sequenceName = "BIDS_SEQ", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence")
    @Column(name = "ID")
    private Long id;
    @Column(name = "BID_Date")
    private Date bidDate;
    /*@Column(name = "Status")
    private String status;*/
    /*@Column(name = "BID_Stage")
    private String bidStage;*/
    @Column(name = "NEWSPAPER")
    private String newspaper;
    @Column(name = "NEWSPAPER1_DATE")
    private Date news1Date;
    @Column(name = "REGISTRATION_END_Date")
    private Date registrEndDate;
    /*@Column(name = "COUNT_OF_PARTICIPANTS")
    private int countOfParticipants;*/

    @ManyToOne
    private Exchange exchange;//класс

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bid bid = (Bid) o;
        return Objects.equals(bidDate, bid.bidDate) &&
                Objects.equals(exchange, bid.exchange);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bidDate, exchange);
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Date getBidDate() {
        return bidDate;
    }
    public void setBidDate(Date bidDate) {
        this.bidDate = bidDate;
    }

    public String getNewspaper() {
        return newspaper;
    }
    public void setNewspaper(String newspaper) {
        this.newspaper = newspaper;
    }

    public Date getNews1Date() {
        return news1Date;
    }
    public void setNews1Date(Date news1Date) {
        this.news1Date = news1Date;
    }

    public Date getRegistrEndDate() {
        return registrEndDate;
    }
    public void setRegistrEndDate(Date registrEndDate) {
        this.registrEndDate = registrEndDate;
    }

    public Exchange getExchange() {
        return exchange;
    }
    public void setExchange(Exchange exchange) {
        this.exchange = exchange;
    }

    public Bid() {
    }
    public Bid(Date bidDate, String newspaper, Date news1Date, Date registrEndDate, Exchange exchange) {
        this.bidDate = bidDate;
        this.newspaper = newspaper;
        this.news1Date = news1Date;
        this.registrEndDate = registrEndDate;
        this.exchange = exchange;
    }
    public Bid(Date bidDate, Exchange exchange, String newspaper, Date news1Date, Date registrEndDate) {
        this.bidDate = bidDate;
        this.newspaper = newspaper;
        this.news1Date = news1Date;
        this.registrEndDate = registrEndDate;
        this.exchange = exchange;
    }

    @Override
    public int compareTo(Bid bid) {
        return bidDate.compareTo(bid.getBidDate());
    }

    @Override
    public String toString() {
        return "Bid{" +
                "id=" + id +
                ", bidDate=" + bidDate +
                ", newspaper='" + newspaper + '\'' +
                ", news1Date=" + news1Date +
                ", registrEndDate=" + registrEndDate +
                ", exchange=" + exchange +
                '}';
    }

}