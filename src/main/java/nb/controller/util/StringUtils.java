package nb.controller.util;

import nb.domain.Bid;
import nb.util.CustomDateFormats;

import static org.apache.commons.lang3.StringUtils.EMPTY;

public class StringUtils {

    public static String getExchangeName(Bid bid) {
        return bid != null && bid.getExchange() != null ? bid.getExchange().getCompanyName() : EMPTY;
    }

    public static String getBidDateFormatted(Bid bid) {
        return bid != null ? CustomDateFormats.sdfshort.format(bid.getBidDate()) : EMPTY;
    }
}
