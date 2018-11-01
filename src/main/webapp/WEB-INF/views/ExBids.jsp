<%@ page import="nb.domain.Bid" %>
<%@ page import="nb.domain.Exchange" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <script src="js/jquery-3.2.1.js"></script>
    <script>
        $(document).ready(function() {
            $('.exchangeTr').each( function(){
               var bidId=$(this).children().first().text();
                var count = $(this).find('.countLots');
                var sum = $(this).find('.sumLots');
                var lots = $(this).find('.lots');
                var tab = $('#ltbl');
                $.ajax({
                    url: "countSumLotsByBid",
                    type: "POST",
                    data: {bidId: bidId},
                    success: function (countSum) {
                        count.append(countSum[0]);
                        sum.append(countSum[1]);
                    }
                });
                $.ajax({
                    url: "lotsByBid",
                    type: "POST",
                    data: {bidId: bidId},
                    success: function (lotsByBid) {
                        var listOfLotsId="";
                        for(var i=0; i<lotsByBid.length; i++){
                           listOfLotsId += lotsByBid[i].id +',';
                        }
                        lots.append(listOfLotsId);
                    }
                });
                $(this).dblclick(function(){
                    $.ajax({
                        url: "strLotsByBid",
                        type: "POST",
                        data: {bidId: bidId},
                        success: function (bidLots) {
                            tab.empty();
                            for (var i = 0; i < bidLots.length; i++) {
                                var stroka = bidLots[i].split('|');
                                if (i === 0) {
                                    var tr = $('<tr class="trh"></tr>');
                                    for (var j = 0; j < stroka.length; j++) {
                                        tr.append($('<th align="center" style="background-color: #00ffff">' + stroka[j] + '</th>'))
                                    }
                                }
                                else{
                                    var tr = $('<tr class="tr"></tr>');
                                    for (var k = 0; k < stroka.length; k++) {
                                        tr.append($('<td align="center">' + stroka[k] + '</td>'));
                                    }
                                }
                                tab.append(tr);
                            }
                        }
                    });
                });
                $(this).mouseover( function(){
                        $(this).css({'background-color': "#00ffff"})
                });
                $(this).mouseout( function(){
                    $(this).css({'background-color': "mintcream"})
                });
            })
        })
    </script>
    <title>Торги на біржі</title>
</head>
<body style="background-color: mintcream" id="bd">
<%
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    Exchange exchange = (Exchange) request.getAttribute("exchange");
    List<Bid> bidList = (List<Bid>) request.getAttribute("bidList");
%>
<H2 align="center">
    <%out.print(exchange.getCompanyName());%>
</H2>
<div id="bidDiv">
    <table>
        <tr>
            <td>
                <table id="bidTab" border="3">
                    <tr bgcolor="#bdb76b">
                        <th>ID</th>
                        <th>Дата торгів</th>
                        <th>Статус</th>
                        <th>К-сть лотів</th>
                        <th>Ціна лотів</th>
                        <th>Лоти</th>
                    </tr>
                    <%for(Bid bid: bidList){
                    %>
                    <tr class="exchangeTr">
                        <td><%=bid.getId()%></td>
                        <td><%=sdf.format(bid.getBidDate())%></td>
                        <td><%=bid.getStatus()%></td>
                        <td class="countLots"></td>
                        <td class="sumLots"></td>
                        <td class="lots"></td>
                    </tr>
                    <%}%>
                </table>
            </td>
            <td>
                <table id="ltbl" border="light" class="table" style="background-color: lightcyan"></table>
            </td>
    </tr>
    </table>

</div>
</body>
</html>
