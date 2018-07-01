<%@ page import="nb.domain.Exchange" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<%
    List<Exchange> exchangeList = (List<Exchange>) request.getAttribute("exchangesList");
%>
<head>
    <script src="resources/js/jquery-3.2.1.js"></script>
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

            $('.button_open_lots').click(function () {
                var idEx = $(this).parent().parent().find('.idEx').text();
                window.open('exLots/'+idEx);
            });

        });
    </script>

    <link href="resources/css/general_style.css" rel="stylesheet" type="text/css">
    <style type="text/css">
        table{
            width: 100%;
            border-collapse: collapse;
        }
        th{
            color: mediumvioletred;
            background-color: #37415d;
            border-bottom: 1px solid mediumvioletred;
        }
        tr:hover {
            cursor: pointer;
            color: mediumvioletred;
            border-bottom: 1px solid mediumvioletred;
        }
        #div_exchanges{
            margin-top: -40px;
        }
        .button_open_lots{
            background-color: #37415d;
            color: cyan;
        }
    </style>

    <title>Біржі</title>
</head>


<body>

<header>
    <div id="div_left_side" class="div_header_additions">
        <div id="div_beck_img" title="назад" onclick="location.href='index'">
            <img src="resources/css/images/back.png">
        </div>
    </div>
    <div id="div_sheet_header">
        <h1>Біржі</h1>
    </div>
    <div id="div_right_side" class="div_header_additions">

    </div>
</header>

<div id="div_exchanges">
    <table border="1px">
        <tr align="center">
            <th>№</th>
            <th>ЄДРПОУ</th>
            <th>Назва</th>
            <th>Кількість торгів</th>
            <th>Кількість лотів</th>
            <th>Оціночна вартість</th>
        </tr>
        <%for (Exchange ex : exchangeList) {%>
        <tr class="tr_exchange">
            <td class="idEx"><%=ex.getId()%></td>
            <td><%=ex.getInn()%></td>
            <td><%=ex.getCompanyName()%></td>
            <td class="countBids" align="center"></td>
            <td class="countLots" align="center"></td>
            <td class="rv" align="right"></td>
            <td><button class="button_open_lots" style="height: 100%; width: 100%">Лоти</button></td>
        </tr>
        <%}%>
    </table>
</div>
</body>
</html>