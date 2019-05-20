package nb.util;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class CustomDateFormats {

    public static final SimpleDateFormat sdfshort = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    public static final SimpleDateFormat sdfpoints = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
    public static final SimpleDateFormat yearMonthFormat = new SimpleDateFormat("MM/yyyy", Locale.ENGLISH);

}
