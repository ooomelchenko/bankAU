package nb.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "LOTS_HISTORY")
public class LotHistory implements Serializable {
    @Id
    @SequenceGenerator(name = "sequence", sequenceName = "LOT_HISTORY_SEQ", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence")
    @Column(name = "ID_KEY")
    private Long idKey;
    @Column(name = "CHANGE_DATE")
    private Date changeDate;
    @Column(name = "USER_NAME")
    private String userName;
    @Column(name = "ID")
    private Long id;
    @Column(name = "LotNum")
    private String lotNum;
    @Column(name = "Status")
    private String workStage;
    @Column(name = "Sold")
    private Boolean isItSold;
    @Column(name = "Comments")
    private String comment;
    @Column(name = "Lot_Created")
    private Date lotDate;
    @Column(name = "BID_Stage")
    private String bidStage;
    @Column(name = "COUNT_OF_PARTICIPANTS")
    private int countOfParticipants;
    @Column(name = "Start_PRICE")
    private BigDecimal startPrice;
    @Column(name = "FIRST_Start_PRICE")
    private BigDecimal firstStartPrice;
    @Column(name = "FACT_PRICE")
    private BigDecimal factPrice;
    @Column(name = "CUSTOMER")
    private String customerName;
    @Column(name = "RESULT_Status")
    private String status;
    @Column(name = "ACT_SIGNED_DATE")
    private Date actSignedDate;
    @Column(name="lot_Type")
    private int lotType;
    @Column(name = "USER_ID")
    private Long userId;
    @Column(name = "BID_ID")
    private Long bidId;

    @Column(name = "PLAN_SALE_DATE")
    private Date planSaleDate;
    @Column(name = "ACCEPTED_EXCHANGE")
    private String acceptExchange;
    @Column(name = "NEED_NEW_FD")
    private boolean neadNewFondDec;
    @Column(name = "FOND_DEC_DATE")
    private Date fondDecisionDate;
    @Column(name = "FOND_DECISION")
    private String fondDecision;
    @Column(name = "FOND_DECISION_NUM")
    private String decisionNumber;

    @Column(name = "NBU_DEC_DATE")
    private Date nbuDecisionDate;
    @Column(name = "NBU_DECISION")
    private String nbuDecision;
    @Column(name = "NBU_DECISION_NUM")
    private String nbuDecisionNumber;

    public Long getIdKey() {
        return idKey;
    }
    public void setIdKey(Long idKey) {
        this.idKey = idKey;
    }

    public Date getChangeDate() {
        return changeDate;
    }
    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getLotNum() {
        return lotNum;
    }
    public void setLotNum(String lotNum) {
        this.lotNum = lotNum;
    }

    public String getWorkStage() {
        return workStage;
    }
    public void setWorkStage(String workStage) {
        this.workStage = workStage;
    }

    public Boolean getItSold() {
        return isItSold;
    }
    public void setItSold(Boolean itSold) {
        isItSold = itSold;
    }

    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getLotDate() {
        return lotDate;
    }
    public void setLotDate(Date lotDate) {
        this.lotDate = lotDate;
    }

    public String getBidStage() {
        return bidStage;
    }
    public void setBidStage(String bidStage) {
        this.bidStage = bidStage;
    }

    public int getCountOfParticipants() {
        return countOfParticipants;
    }
    public void setCountOfParticipants(int countOfParticipants) {
        this.countOfParticipants = countOfParticipants;
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

    public BigDecimal getFactPrice() {
        return factPrice;
    }
    public void setFactPrice(BigDecimal factPrice) {
        this.factPrice = factPrice;
    }

    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public Date getActSignedDate() {
        return actSignedDate;
    }
    public void setActSignedDate(Date actSignedDate) {
        this.actSignedDate = actSignedDate;
    }

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getBidId() {
        return bidId;
    }
    public void setBidId(Long bidId) {
        this.bidId = bidId;
    }

    public int getLotType() {
        return lotType;
    }
    public void setLotType(int lotType) {
        this.lotType = lotType;
    }

    public Date getPlanSaleDate() {
        return planSaleDate;
    }
    public void setPlanSaleDate(Date planSaleDate) {
        this.planSaleDate = planSaleDate;
    }

    public String getAcceptExchange() {
        return acceptExchange;
    }
    public void setAcceptExchange(String acceptExchange) {
        this.acceptExchange = acceptExchange;
    }

    public boolean isNeadNewFondDec() {
        return neadNewFondDec;
    }
    public void setNeadNewFondDec(boolean neadNewFondDec) {
        this.neadNewFondDec = neadNewFondDec;
    }

    public Date getFondDecisionDate() {
        return fondDecisionDate;
    }
    public void setFondDecisionDate(Date fondDecisionDate) {
        this.fondDecisionDate = fondDecisionDate;
    }

    public String getFondDecision() {
        return fondDecision;
    }
    public void setFondDecision(String fondDecision) {
        this.fondDecision = fondDecision;
    }

    public String getDecisionNumber() {
        return decisionNumber;
    }
    public void setDecisionNumber(String decisionNumber) {
        this.decisionNumber = decisionNumber;
    }

    public Date getNbuDecisionDate() {
        return nbuDecisionDate;
    }
    public void setNbuDecisionDate(Date nbuDecisionDate) {
        this.nbuDecisionDate = nbuDecisionDate;
    }

    public String getNbuDecision() {
        return nbuDecision;
    }
    public void setNbuDecision(String nbuDecision) {
        this.nbuDecision = nbuDecision;
    }

    public String getNbuDecisionNumber() {
        return nbuDecisionNumber;
    }
    public void setNbuDecisionNumber(String nbuDecisionNumber) {
        this.nbuDecisionNumber = nbuDecisionNumber;
    }

    //Конструктора
    public LotHistory() {
    }

    public LotHistory(String userName, Lot lot) {
        this.changeDate = new Date();
        this.userName = userName;
        this.id = lot.getId();
        this.lotNum = lot.getLotNum();
        this.workStage = lot.getWorkStage();
        this.isItSold = lot.getItSold();
        this.comment = lot.getComment();
        this.lotDate = lot.getLotDate();
        this.bidStage = lot.getBidStage();
        this.countOfParticipants = lot.getCountOfParticipants();
        this.startPrice = lot.getStartPrice();
        this.firstStartPrice = lot.getFirstStartPrice();
        this.factPrice = lot.getFactPrice();
        this.customerName = lot.getCustomerName();
        this.status = lot.getStatus();
        this.actSignedDate = lot.getActSignedDate();
        if(lot.getUser()!=null)
        this.userId = lot.getUser().getId();
        this.lotType = lot.getLotType();
        if(lot.getBid()!=null)
        this.bidId = lot.getBid().getId();
        this.planSaleDate = lot.getPlanSaleDate();
        this.acceptExchange = lot.getAcceptExchange();
        this.neadNewFondDec = lot.isNeedNewFondDec();
        this.fondDecisionDate = lot.getFondDecisionDate();
        this.fondDecision = lot.getFondDecision();
        this.decisionNumber = lot.getDecisionNumber();
        this.nbuDecision = lot.getNbuDecision();
        this.nbuDecisionDate = lot.getNbuDecisionDate();
        this.nbuDecisionNumber = lot.getNbuDecisionNumber();
    }
}