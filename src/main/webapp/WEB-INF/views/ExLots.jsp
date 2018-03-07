<%@ page import="nadrabank.domain.Bid" %>
<%@ page import="nadrabank.domain.Exchange" %>
<%@ page import="nadrabank.domain.Lot" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Formatter" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <script src="js/jquery-1.11.1.js"></script>
    <script src="js/lotsMenu.js"></script>
    <title>Торги на біржі</title>
</head>
<body style="background-color: mintcream" id="bd">
<%
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    Exchange exchange = (Exchange) request.getAttribute("exchange");
    List <Lot> lotList = (List <Lot>) request.getAttribute("lotList");
%>
<H2 align="center">
    <%out.print(exchange.getCompanyName());%>
</H2>
<div>
    <table id="ltbl" border="light" class="table" style="background-color: lightcyan">
        <tr bgcolor="#00ff7f">
            <th>ID</th>
            <th title="Натисніть для відображення фільтру">
                Дата торгів
            </th>
            <th title="Натисніть для відображення фільтру">
                Назва біржі
            </th>
            <th>Оціночна вартість, грн.</th>
            <th>Торги</th>
            <th>Статус аукціону</th>
            <th>№ Лоту в публікації</th>
            <th>Початкова ціна лоту, грн.</th>
            <th>Ціна реалізації, грн.</th>
            <th>Статус оплати</th>
            <th>Сума сплати</th>
            <th>Стадія роботи</th>
            <th>Покупець</th>
            <th>Акт підписано</th>
            <th>Коментар</th>
        </tr>
        <%for(Lot lot: lotList){Bid bid = lot.getBid();%>
        <tr class="trL">
            <td align="center" class="lotId"><%=lot.getId()%></td>
            <td align="center" class="bidDate"><%if(bid!=null&&bid.getBidDate()!=null){out.print(sdf.format(bid.getBidDate()));}%></td>
            <td align="center" class="company"><%if(bid!=null&&bid.getExchange()!=null)out.print(bid.getExchange().getCompanyName());%></td>
            <td align="center" class="sumOfCrd"></td>
            <td align="center" class="bidStage"><%out.print(lot.getBidStage());%></td>
            <td align="center" class="bidStatus"><%if(lot.getStatus()!=null)out.print(lot.getStatus());%></td>
            <td align="center" class="lotNum"><%if(lot.getLotNum()!=null)out.print(lot.getLotNum());%></td>
            <td align="center" class="startPrice"><%if(lot.getStartPrice()!=null)out.print(lot.getStartPrice());%></td>
            <td align="center" class="factPrice"><%if(lot.getFactPrice()!=null)out.print(lot.getFactPrice());%></td>
            <td align="center" class="payStatus"></td>
            <td align="center" class="paymentsSum"></td>
            <%--<td align="center" class="residualToPay"></td>--%>
            <td align="center" class="workstage"><%=lot.getWorkStage()%></td>
            <td align="center" class="customer"><%if(lot.getCustomerName()!=null)out.print(lot.getCustomerName());%></td>
            <td align="center" class="aktDate"><%if(lot.getActSignedDate()!=null)out.print(sdf.format(lot.getActSignedDate()));%></td>
            <%--<td align="center" class="user"><%=lot.getUser().getLogin()%></td>--%>
            <td align="center" class="comment"><%if(lot.getComment()!=null)out.print(lot.getComment());%></td>
        </tr>
        <%}%>
    </table>
</div>
</body>
</html>