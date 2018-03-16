<%@ page import="nb.domain.Asset" %>
<%@ page import="nb.domain.Credit" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<% List<Asset> assetList = (List<Asset>) request.getAttribute("assetList"); %>
<% List<Credit> creditList = (List<Credit>) request.getAttribute("creditList"); %>
<% int lotType = (Integer) request.getAttribute("lotType"); %>
<base href="${pageContext.request.contextPath}/"/>
<head>
    <title>Створення лоту</title>

    <script type="text/javascript" src="resources/js/jquery-3.2.1.js"></script>

    <%if (lotType == 0) {%>
    <script src="resources/js/lotCrCreator.js"></script>
    <%}%>
    <%if (lotType == 1) {%>
    <script src="resources/js/lotCreator.js"></script>
    <%}%>

    <link rel="stylesheet" media="screen" type="text/css" href="resources/css/general_style.css"/>
    <style>

    </style>
</head>

<header>
    <div id="div_left_side" class="div_header_additions">
        <div id="div_beck_img" title="головна" onclick="location.href='index'">
            <img src="resources/css/images/back.png">
        </div>
    </div>
    <div id="div_sheet_header">
        <h1>Створення нового лоту</h1>
    </div>
    <div id="div_right_side" class="div_header_additions">
    </div>
</header>

<body>

<div id="mainBlock">

    <div id="searchBlock" class="choice-box">

        <div>
            <table border="1" class="table" style="background-color: lightcyan; width: 100%" id="tblParam">
                <tr>
                    <td colspan="2"><input id="commIn" type="text" style="width: 100%" placeholder="Коментар"></td>
                </tr>
                <tr>
                    <th>Ціна лоту, грн.</th>
                    <th>К-ть об'єктів</th>
                </tr>
                <tr>
                    <td id="priceId" align="center">0</td>
                    <td id="kolId" align="center">0</td>
                </tr>
                <tr>
                    <td style="border: none" colspan="2">
                        <button id="showLCrdts" class="button"
                                style="background-color: cyan; width: 100%"> Показати список лоту
                        </button>
                    </td>
                </tr>
            </table>
        </div>
        <div align="center">
            <button id="createLot" class="button" title="Натисніть для створення лоту з обраних об'єктів">
                СТВОРИТИ ЛОТ
            </button>
        </div>
        <div>
            <table style="width: 100%">
                <tr>
                    <td>
                        <input id="inn" type="text" placeholder="Введіть ІНН для пошуку" style="width: 100%">
                    </td>
                </tr>
                <%if (lotType == 0) {%>
                <tr>
                    <td>
                        <input id="idBars" type="text" placeholder="Введіть ID_BARS"
                               style="background-color: aliceblue; width: 100%">
                    </td>
                </tr>
                <%}%>
                <tr>
                    <td>
                        <button id="findObjBut" class="button" style="width: 100%">Знайти</button>
                    </td>
                </tr>
            </table>
        </div>

    </div>
    <div id="listSearchBlock">
        <%if (lotType == 0) {%>
        <form method="POST" action="" enctype="multipart/form-data" lang="utf8">
            <h3>Обрати файл зі списком ID_Bars:</h3>
            <input align="center" type="file" name="file" title="натисніть для обрання файлу"><br/>
            <input name="idType" value="0" type="number" hidden="hidden">
        </form>
        <%}%>
        <%if (lotType == 1) {%>
        <form method="POST" action="" enctype="multipart/form-data" lang="utf8">
            <h3>Обрати файл з Інвентарними номерами:</h3>
            <input align="center" type="file" name="file" title="натисніть для обрання файлу"><br/>
            <input name="idType" value="1" type="number" hidden="hidden">
        </form>
        <%}%>
        <button id="sendBut">Знайти по списку з файлу</button>
    </div>
    <div style="width: 10%">
        <button id="formDownld" title="Завантажити зразок файлу зі списком ID для пошуку">форма(.xls)</button>
    </div>
</div>
<br/>
<%if (lotType == 0) {%>
<div class="view">
    <table class="findTab" border="2" hidden="hidden">
        <tr align="center" style="background-color: darkblue; color: white">
            <th hidden="hidden">Key_N</th>
            <th>ID_BARS</th>
            <th>ІНН</th>
            <th>Боржник</th>
            <th>Опис кредиту</th>
            <th>Регіон</th>
            <th>Загальний борг</th>
            <th>Оціночна вартість, грн.</th>
            <th>В заставі НБУ</th>
            <th>
                <button class="addAllBut">Додати всі</button>
            </th>
        </tr>
    </table>
    <table class="lotTab" border="2" hidden="hidden">
        <tr align="center" style="background-color: cyan">
            <th hidden="hidden">Key_N</th>
            <th>ID_BARS</th>
            <th>ІНН</th>
            <th>Боржник</th>
            <th>Опис кредиту</th>
            <th>Регіон</th>
            <th>Загальний борг</th>
            <th>Оціночна вартість, грн.</th>
            <th>В заставі НБУ</th>
        </tr>
        <% for (Credit crdt : creditList) {
            if (crdt.getLot() == null /*&& !crdt.getFondDecision().equals("Відправлено до ФГВФО") && !crdt.getFondDecision().equals("")*/) {
        %>
        <tr align="center">
            <td class="idObj" hidden="hidden"><%=crdt.getId()%></td>
            <td class="ndObj"><%=crdt.getNd()%></td>
            <td><%=crdt.getInn()%></td>
            <td><%=crdt.getFio()%></td>
            <td><%=crdt.getProduct()%></td>
            <td><%=crdt.getRegion()%></td>
            <td><%=crdt.getZb()%></td>
            <td><%=crdt.getRv()%></td>
            <td><%=crdt.getNbuPladge()%></td>
        </tr>
        <%
                }
            }
        %>
    </table>
</div>
<%}%>
<%if (lotType == 1) {%>
<div class="view">
    <table class="findTab" border="2" hidden="hidden">
        <tr align="center" style="background-color: #27d927">
            <th>ID</th>
            <th>Інвентарний №</th>
            <th>Назва активу</th>
            <th>Опис об'єкту</th>
            <th>Регіон</th>
            <th>Балансова вартість</th>
            <th>Оціночна вартість, грн.</th>
            <th>В заставі НБУ</th>
            <th>
                <button class="addAllBut">Додати всі</button>
            </th>
        </tr>
    </table>
    <table class="lotTab" border="2" hidden="hidden">
        <tr align="center" style="background-color: cyan">
            <th>ID</th>
            <th>Інвентарний №</th>
            <th>Назва активу</th>
            <th>Опис об'єкту</th>
            <th>Регіон</th>
            <th>Балансова вартість</th>
            <th>Оціночна вартість, грн.</th>
            <th>В заставі НБУ</th>
        </tr>

        <% for (Asset asset : assetList) {
            if (asset.getLot() == null /*&& !asset.getLot().getFondDecision().equals("Відправлено до ФГВФО") && !asset.getLot().getFondDecision().equals("")*/) {
        %>
        <tr align="center">
            <td class="idObj"><%=asset.getId()%></td>
            <td><%=asset.getInn()%></td>
            <td><%=asset.getAsset_name()%>
            </td>
            <td><%=asset.getAsset_descr()%>
            </td>
            <td><%=asset.getRegion()%>
            </td>
            <td><%=asset.getZb()%>
            </td>
            <td><%=asset.getRv()%>
            </td>
            <td><%
                if (asset.isApproveNBU()) out.print("Так");
                else out.print("Ні");
            %></td>
        </tr>
        <%
                }
            }
        %>
    </table>
</div>
<%}%>
</body>
</html>