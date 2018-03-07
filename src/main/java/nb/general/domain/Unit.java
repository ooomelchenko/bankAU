package nb.general.domain;

import java.math.BigDecimal;
import java.util.Date;

public class Unit {

    private Long id;
    private String assetTypeCode;
    private String assetGroupCode;
    private String inn;
    private BigDecimal zb;
    private BigDecimal rv;
    private String region;
    private BigDecimal factPrice;
    private boolean isSold;
    private BigDecimal acceptPrice;
    private BigDecimal paysBid;
    private BigDecimal paysCustomer;
    private Date bidPayDate;
    private Date customerPayDate;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getAssetTypeCode() {
        return assetTypeCode;
    }
    public void setAssetTypeCode(String assetTypeCode) {
        this.assetTypeCode = assetTypeCode;
    }

    public String getAssetGroupCode() {
        return assetGroupCode;
    }
    public void setAssetGroupCode(String assetGroupCode) {
        this.assetGroupCode = assetGroupCode;
    }

    public String getInn() {
        return inn;
    }
    public void setInn(String inn) {
        this.inn = inn;
    }

    public BigDecimal getZb() {
        return zb;
    }
    public void setZb(BigDecimal zb) {
        this.zb = zb;
    }

    public BigDecimal getRv() {
        return rv;
    }
    public void setRv(BigDecimal rv) {
        this.rv = rv;
    }

    public String getRegion() {
        return region;
    }
    public void setRegion(String region) {
        this.region = region;
    }

    public BigDecimal getFactPrice() {
        return factPrice;
    }
    public void setFactPrice(BigDecimal factPrice) {
        this.factPrice = factPrice;
    }

    public boolean isSold() {
        return isSold;
    }
    public void setSold(boolean sold) {
        isSold = sold;
    }

    public BigDecimal getAcceptPrice() {
        return acceptPrice;
    }
    public void setAcceptPrice(BigDecimal acceptPrice) {
        this.acceptPrice = acceptPrice;
    }

    public BigDecimal getPaysBid() {
        return paysBid;
    }
    public void setPaysBid(BigDecimal paysBid) {
        this.paysBid = paysBid;
    }

    public BigDecimal getPaysCustomer() {
        return paysCustomer;
    }
    public void setPaysCustomer(BigDecimal paysCustomer) {
        this.paysCustomer = paysCustomer;
    }

    public Date getBidPayDate() {
        return bidPayDate;
    }
    public void setBidPayDate(Date bidPayDate) {
        this.bidPayDate = bidPayDate;
    }

    public Date getCustomerPayDate() {
        return customerPayDate;
    }
    public void setCustomerPayDate(Date customerPayDate) {
        this.customerPayDate = customerPayDate;
    }

}
