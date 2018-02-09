package nb.domain;

import nb.general.domain.Unit;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "ASSETS")
public class Asset extends Unit implements Serializable  {

    @Id
    @SequenceGenerator(name = "sequence", sequenceName = "Assets_SEQ", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence")
    @Column(name = "MY_ID")
    private Long id;
    @Column(name = "TYPE_CODE")
    private String assetTypeCode;
    @Column(name = "GROUP_CODE")
    private String assetGroupCode;
    @Column(name = "INVENT")
    private String inn;
    @Column(name = "ASSET_NAME")
    private String asset_name;
    @Column(name = "ASSET_DESCRIPTION")
    private String asset_descr;
    @Column(name = "VIDDIL")
    private String viddil;
    @Column(name = "BALANCE_ACCOUNT")
    private String balanceAccount;
    @Column(name = "EKSPL_VVedeno_DATE")
    private Date eksplDate;
    @Column(name = "PERVISNA_VARTIST_UAH")
    private BigDecimal originalPrice;
    @Column(name = "BALANCE_COST_UAH")
    private BigDecimal zb;
    @Column(name = "RV_BEZ_PDV_UAH")
    private BigDecimal rvNoPdv;
    @Column(name = "RV_UAH")
    private BigDecimal rv;
    @Column(name = "STAN_OCINKI")
    private String evaluationStatus;
    @Column(name = "REGION")
    private String region;
    @Column(name = "FACT_SALE_PRICE_UAH")
    private BigDecimal factPrice;
    @Column(name = "IS_IT_SOLD")
    private boolean isSold;
    @Column(name = "NBU_APPROVE")
    private boolean approveNBU;
    @Column(name = "ACCEPTED_PRICE")
    private BigDecimal acceptPrice;
    @Column(name = "PROPOSITION")
    private String proposition;
    @Column(name = "PAYMENTS_BID")
    private BigDecimal paysBid;
    @Column(name = "PAYMENTS_CUSTOMER")
    private BigDecimal paysCustomer;
    @Column(name = "LAST_BID_PAY_DATE")
    private Date bidPayDate;
    @Column(name = "LAST_CUSTOMER_PAY_DATE")
    private Date customerPayDate;

    @ManyToOne
    private Lot lot;//класс

    public String getAsset_name() {
        return asset_name;
    }
    public void setAsset_name(String asset_name) {
        this.asset_name = asset_name;
    }

    public String getAsset_descr() {
        return asset_descr;
    }
    public void setAsset_descr(String asset_descr) {
        this.asset_descr = asset_descr;
    }

    public String getViddil() {
        return viddil;
    }
    public void setViddil(String viddil) {
        this.viddil = viddil;
    }

    public String getBalanceAccount() {
        return balanceAccount;
    }
    public void setBalanceAccount(String balanceAccount) {
        this.balanceAccount = balanceAccount;
    }

    public Date getEksplDate() {
        return eksplDate;
    }
    public void setEksplDate(Date eksplDate) {
        this.eksplDate = eksplDate;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }
    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public BigDecimal getRvNoPdv() {
        return rvNoPdv;
    }
    public void setRvNoPdv(BigDecimal rvNoPdv) {
        this.rvNoPdv = rvNoPdv;
    }

    public String getEvaluationStatus() {
        return evaluationStatus;
    }
    public void setEvaluationStatus(String evaluationStatus) {
        this.evaluationStatus = evaluationStatus;
    }

    public Lot getLot() {
        return lot;
    }
    public void setLot(Lot lot) {
        this.lot = lot;
    }

    public boolean isApproveNBU() {
        return approveNBU;
    }
    public void setApproveNBU(boolean approveNBU) {
        this.approveNBU = approveNBU;
    }

    public String getProposition() {
        return proposition;
    }
    public void setProposition(String proposition) {
        this.proposition = proposition;
    }

    public Asset() {
    }

    @Override
    public String toString() {
        return "Asset{" +
                "id=" + id +
                ", assetTypeCode='" + assetTypeCode + '\'' +
                ", assetGroupCode='" + assetGroupCode + '\'' +
                ", inn='" + inn + '\'' +
                ", asset_name='" + asset_name + '\'' +
                ", asset_descr='" + asset_descr + '\'' +
                ", viddil='" + viddil + '\'' +
                ", balanceAccount='" + balanceAccount + '\'' +
                ", eksplDate=" + eksplDate +
                ", originalPrice=" + originalPrice +
                ", zb=" + zb +
                ", rvNoPdv=" + rvNoPdv +
                ", rv=" + rv +
                ", evaluationStatus='" + evaluationStatus + '\'' +
                ", region='" + region + '\'' +
                ", factPrice=" + factPrice +
                ", isSold=" + isSold +
                ", approveNBU=" + approveNBU +
                ", acceptPrice=" + acceptPrice +
                ", proposition='" + proposition + '\'' +
                ", paysBid=" + paysBid +
                ", paysCustomer=" + paysCustomer +
                ", bidPayDate=" + bidPayDate +
                ", customerPayDate=" + customerPayDate +
                ", lot=" + lot +
                '}';
    }
}
