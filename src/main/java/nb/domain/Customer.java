package nb.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name="Customers")
public class Customer implements Serializable {

    @Id
    @SequenceGenerator(name = "sequence", sequenceName = "Customer_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence")
    @Column(name = "ID")
    private Long id;

    @Column(name = "CUSTOMER_INN")
    private Long customerInn;

    @Column(name = "CUSTOMER_NAME")
    private String customerName;

    @Column(name = "MIDDLE_NAME")
    private String middleName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name="MERRIED")
    private boolean isMerried;

    @Enumerated(EnumType.STRING)
    @Column(name="SUBSCRIBER_TYPE")
    private SubscriberType type = SubscriberType.CUSTOMER;

    @Enumerated(EnumType.STRING)
    @Column(name="LEGAL_TYPE")
    private LegalType legalType = LegalType.INDIVIDUAL;

    public enum SubscriberType {

        CUSTOMER("Покупець"),
        CONFIDANT("Довірена особа");

        private String ukrType;

        public String getUkrType() {
            return ukrType;
        }

        SubscriberType(String ukrType) {
            this.ukrType=ukrType;
        }
    }

    public enum LegalType {

        INDIVIDUAL("Фізична особа"),
        ENTERPRICE("Юридична особа");

        private String ukrType;

        public String getUkrType() {
            return ukrType;
        }

        LegalType(String ukrType) {
            this.ukrType = ukrType;
        }
    }

    public Customer() {
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getMiddleName() {
        return middleName;
    }
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getCustomerInn() {
        return customerInn;
    }
    public void setCustomerInn(Long customerInn) {
        this.customerInn = customerInn;
    }

    public boolean isMerried() {
        return isMerried;
    }
    public void setMerried(boolean merried) {
        isMerried = merried;
    }

    public SubscriberType getType() {
        return type;
    }
    public void setType(SubscriberType type) {
        this.type = type;
    }

    public LegalType getLegalType() {
        return legalType;
    }
    public void setLegalType(LegalType legalType) {
        this.legalType = legalType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id) &&
                Objects.equals(customerInn, customer.customerInn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customerInn);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", customerInn=" + customerInn +
                ", customerName='" + customerName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", isMerried=" + isMerried +
                ", type=" + type +
                ", legalType=" + legalType +
                '}';
    }

    public String shortDescription(){
        return (lastName == null ? "" : lastName+" ") +
                (customerName == null ? "" : customerName+" ") +
                (middleName == null ? "" : middleName);
    }
}
