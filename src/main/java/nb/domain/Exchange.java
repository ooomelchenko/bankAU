package nb.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name="Exchanges")
public class Exchange implements Serializable, Comparable {
    @Id
    @SequenceGenerator(name = "sequence", sequenceName = "Exch_SEQ", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence")
    @Column(name = "ID")
    private Long id;
    @Column(name = "EDRPO")
    private String inn;
    @Column(name = "Exchange_NAME")
    private String companyName;
    @Column(name = "Address_UR")
    private String address;
    @Column(name = "Post_Address")
    private String postAddress;
    @Column(name = "Contact_FIO")
    private String contactFIO;
    @Column(name = "Requizit")
    private String req;
    @Column(name = "EMail")
    private String email;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Exchange exchange = (Exchange) o;
        return Objects.equals(companyName, exchange.companyName);
    }
    @Override
    public int hashCode() {
        return Objects.hash(companyName);
    }

    @Override
    public int compareTo(Object o) {
        Exchange entryEx = (Exchange) o;
        return companyName.compareTo(entryEx.getCompanyName());
    }

    //Getters&Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getInn() {
        return inn;
    }
    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getCompanyName() {
        return companyName;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostAddress() {
        return postAddress;
    }
    public void setPostAddress(String postAddress) {
        this.postAddress = postAddress;
    }

    public String getContactFIO() {
        return contactFIO;
    }
    public void setContactFIO(String contactFIO) {
        this.contactFIO = contactFIO;
    }

    public String getReq() {
        return req;
    }
    public void setReq(String req) {
        this.req = req;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    //Конструктора
    public Exchange() {
    }
    public Exchange(String inn, String companyName, String address, String postAddress, String contactFIO, String req, String email) {
        this.inn = inn;
        this.companyName = companyName;
        this.address = address;
        this.postAddress = postAddress;
        this.contactFIO = contactFIO;
        this.req = req;
        this.email = email;
    }

    @Override
    public String toString() {
        return "Exchange{" +
                "id=" + id +
                ", inn='" + inn + '\'' +
                ", companyName='" + companyName + '\'' +
                ", address='" + address + '\'' +
                ", postIndex='" + postAddress + '\'' +
                ", contactFIO='" + contactFIO + '\'' +
                ", req='" + req + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
