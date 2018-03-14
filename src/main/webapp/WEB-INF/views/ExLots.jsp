<%@ page import="nb.domain.Bid" %>
<%@ page import="nb.domain.Exchange" %>
<%@ page import="nb.domain.Lot" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page contentType="text/html;charset=UTF-8"  %>
<base href="${pageContext.request.contextPath}/"/>
<html>
<head>
    <script src="resources/js/jquery-3.2.1.js"></script>
    <script src="resources/js/lotsMenu.js"></script>

    <link href="resources/css/general_style.css" rel="stylesheet" type="text/css">
    <style>
        table{
            width: 100%;
            font-size: small;
            border-collapse: collapse;
        }
        table th{
            color: mediumvioletred;
            border-bottom: 1px solid mediumvioletred;
        }
        .trL{
            color: cyan;
        }
        .trL:hover{
            cursor: pointer;
            color: ghostwhite;
            border-bottom: 1px solid ghostwhite;
        }
    </style>

    <title>Торги на біржі</title>
</head>
<%
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    Exchange exchange = (Exchange) request.getAttribute("exchange");
    List <Lot> lotList = (List <Lot>) request.getAttribute("lotList");
%>
<body>
<header>
    <div id="div_sheet_header">
        <h1><%out.print(exchange.getCompanyName());%></h1>
    </div>
</header>

<div style="width: 100%">
    <table id="ltbl" border="light">
        <tr>
            <th>ID</th>
            <th>Дата торгів</th>
            <th>Назва біржі</th>
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