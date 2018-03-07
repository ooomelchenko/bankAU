<%@ taglib prefix="margin" uri="http://www.springframework.org/tags/form" %>
<%@ page import="java.util.List" %>
<%@ page import="nadrabank.domain.Exchange" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Створення лоту</title>
    <script src="js/jquery-1.11.1.js"></script>
    <script src="js/crdSelectors.js"></script>
    <script src="js/lotCreator.js"></script>
</head>
<body style="background-color: mintcream">
<button onclick="location.href='lotMenu'">Назад</button>
<div id="credSort" class="choice-box">
    <H1 align="center">Створення нового лоту</H1>
    <table width="100%">
        <tr>
            <td>
                <table>
                    <tr>
                        <td colspan="2">Коментар</td>
                        <td colspan="3"><input id="commId" type="text" style="width: 100%"></td>
                    </tr>
                    <tr>
                        <td colspan="2">Біржа</td>
                        <td colspan="3" id="exchangeId">
                            <select id="exId" name="exSelector" style="width: 100%">
                                <%
                                    List<Exchange> exchangesList = (List<Exchange>) request.getAttribute("exList");
                                    if (exchangesList != null) {
                                        for (Exchange ex : exchangesList) {
                                %>
                                <option value="<%=ex.getId()%>">
                                    <%out.print(ex.getCompanyName() + " ЄДРПОУ: " + ex.getInn());%>
                                </option>
                                <%
                                        }
                                    }
                                %>
                            </select>
                        </td>
                    <tr style="border: solid">
                        <td>dpd</td>
                        <td>від</td>
                        <td><input id="dpdmin" type="number" name="dpdmin" value="0"></td>
                        <td>до</td>
                        <td><input id="dpdmax" type="number" name="dpdmax" value="1000"></td>
                    </tr>
                    <tr style="border: solid">
                        <td>Ціна, грн</td>
                        <td>від</td>
                        <td><input id="zbmin" type="number" name="zbmin" value="0"></td>
                        <td>до</td>
                        <td><input id="zbmax" type="number" name="zbmax" value="1000000"></td>
                    </tr>
                    <tr>
                        <td colspan="3" id="regSel">
                            <button id="regView" class="button" style="width: 100%">Всі регіони / Відібрати регіони
                            </button>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3" id="tpSel">
                            <button id="tpView" class="button" style="width: 100%">Тип кредиту / Приховати</button>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3" id="curSel">
                            <button id="curView" class="button" style="width: 100%">Валюта кредиту / Приховати</button>
                        </td>
                    </tr>

                    <tr>
                        <td colspan="2"></td>
                        <td colspan="3">
                            <button id="showCred" class="button" style="width: 100%">Показати список кредитів /
                                Приховати
                            </button>
                        </td>
                    </tr>

                </table>
            </td>

            <td align="top">
                <table>
                    <tr>
                        <td>
                            <table border="1" class="table" style="background-color: lightcyan" id="tblParam">
                                <tr>
                                    <th>Ціна лоту, грн.</th>
                                    <th>К-ть кредитів</th>
                                </tr>
                                <tr>
                                    <td id="zbId" align="center">0</td>
                                    <td id="kolId" align="center">0</td>
                                </tr>
                                <tr>
                                    <td style="border: none" colspan="2">
                                        <button id="showParam" class="button"
                                                style="background-color: cyan; width: 100%"> Розрахувати
                                        </button>
                                    </td>
                                </tr>
                            </table>
                        </td>
                        <td>

                            <button style="font: inherit; color: darkblue; width: 120px; height: 60px"
                                    id="createLot" class="button">СТВОРИТИ ЛОТ
                            </button>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>

    </table>
</div>

<div id="crdList" class="view" style="width: 100%"></div>

</body>
</html>