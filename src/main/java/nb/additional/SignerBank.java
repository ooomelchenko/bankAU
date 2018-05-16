package nb.additional;

public enum SignerBank {
    Glushchenko("Глущенко С.В.", "Глущенка Сергія Вікторовича, місце проживання якого зареєстровано за адресою: м. Київ, Р. Окіпної, буд. 10, кв. 45, який діє на підставі довіреності, посвідченої 26 вересня 2017 року Бочкарьовою Наталією Михайлівною, приватним нотаріусом Київського міського нотаріального округу, за реєстровим № 515"),
    Kulibaba("Кулібаба І.В.", "Кулібаби Ірини Володимирівни, яка діє на підставі довіреності, посвідченої 26 вересня 2017 року Бочкарьовою Наталією Михайлівною, приватним нотаріусом Київського міського нотаріального округу, за реєстровим № 515"),
    Strukova("Стрюкова І.О.", "Уповноваженої особи Фонду гарантування вкладів фізичних осіб на ліквідацію Стрюкової Ірини Олександрівни, яка діє на підставі Рішень виконавчої дирекції Фонду гарантування вкладів фізичних осіб № 113 від 05.06.2015 р. «Про початок процедури ліквідації ПАТ «КБ «НАДРА» та призначення уповноваженої особи Фонду на ліквідацію банку»,  від 28.04.2016 року № 616 «Про продовження строків здійснення процедури ліквідації ПАТ «КБ «НАДРА» та делегування ліквідатора», від 03.11.2016 року № 2342 «Про продовження строків здійснення процедури ліквідації ПАТ «КБ «НАДРА» та делегування повноважень ліквідатора», Закону України «Про систему гарантування вкладів фізичних осіб»");

    private String text;
    private String fio;

    private SignerBank(String fio, String text) {
        this.fio = fio;
        this.text = text;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public String getFio() {
        return fio;
    }
    public void setFio(String fio) {
        this.fio = fio;
    }
}
