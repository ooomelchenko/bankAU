<%@ page import="nadrabank.domain.Exchange" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Біржі</title>
    <script src="js/jquery-1.11.1.js"></script>
    <script>
        $(document).ready(function () {
            $(".idEx").each(function () {
                var idEx = $(this);
                $.ajax({
                    url: "countSumLotsByExchange",
                    type: "POST",
                    data: {exId: idEx.text()},
                    success: function (count_sum) {
                        idEx.parent().find('.countLots').text(count_sum[0]);
                        idEx.parent().find('.rv').text(count_sum[1]);
                    }
                });
                $.ajax({
                    url: "countBidsByExchange",
                    type: "GET",
                    data: {exId: idEx.text()},
                    success: function (countBids) {
                        idEx.parent().find('.countBids').text(countBids);
                    }
                });

            });

            var butt = $('<button class="buttonRedactor" style="height: 100%; width: 100%">Лоти</button>');
            butt.click(function () {
                var idEx = $(this).parent().find('.idEx');
                $.ajax({
                    url: "setRex",
                    type: "GET",
                    data: {exId: idEx.text()},
                    success: function () {
                        window.open('exLots');
                    }
                });
            });
            $('.exTr').append(butt);
        });
    </script>
    <style type="text/css">
        #b0{
            display: table;
            height: 80px;
            width: 100%;
            color: white;
            background-color: #205081;
        }
        #b0 div{
            width: 33%;
            display: table-cell;
        }
        .beckImg {
            width: 40px;
            height: 40px;
        }
        .beckImg:hover {
            cursor: pointer;
            width: 45px;
            height: 45px;
        }
        body {
            background-image: url(images/exchanges_font.jpg);
            background-size: 100%;
        }

        .exTr {
            cursor: pointer;
        }
        .exTr:hover {
            background-color: white;
            color: darkblue;
            font-weight: bold;
        }
    </style>
</head>
<%
    List<Exchange> exchangeList = (List<Exchange>) request.getAttribute("exchangesList");
%>

<body>
<header id="b0">
    <div>
        <img class="beckImg" onclick="location.href='index'" src="images/back.png" title="назад">
    </div>
    <div>
        <H1 align="center">Біржі</H1>
    </div>
    <div>
    </div>
</header>

<div id="exDiv" class="view">
    <table id="extbl" border="light" style="background-color: lightcyan; width: 90%; font-weight: bold">
        <tr align="center" style="background-color: darkblue; color: ghostwhite">
            <th>Номер</th>
            <th>ЄДРПОУ</th>
            <th>Назва</th>
            <th>Кількість торгів</th>
            <th>Кількість лотів</th>
            <th>Оціночна вартість</th>
        </tr>
        <%for (Exchange ex : exchangeList) {%>
        <tr class="exTr">
            <td class="idEx"><%=ex.getId()%></td>
            <td><%=ex.getInn()%></td>
            <td><%=ex.getCompanyName()%></td>
            <td class="countBids" align="center"></td>
            <td class="countLots" align="center"></td>
            <td class="rv" align="right"></td>
        </tr>
        <%}%>
    </table>
</div>
</body>
</html>