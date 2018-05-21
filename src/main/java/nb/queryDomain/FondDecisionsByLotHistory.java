package nb.queryDomain;

import java.io.Serializable;
import java.util.Date;

public class FondDecisionsByLotHistory implements Serializable {

    private Date fondDecisionDate;
    private String fondDecision;
    private String decisionNumber;

    public FondDecisionsByLotHistory() {
    }
    public FondDecisionsByLotHistory(Date fondDecisionDate, String fondDecision, String decisionNumber) {
        this.fondDecisionDate = fondDecisionDate;
        this.fondDecision = fondDecision;
        this.decisionNumber = decisionNumber;
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

    @Override
    public String toString() {
        return "FondDecisionsByLotHistory{" +
                "fondDecisionDate=" + fondDecisionDate +
                ", fondDecision='" + fondDecision + '\'' +
                ", decisionNumber='" + decisionNumber + '\'' +
                '}';
    }
}
