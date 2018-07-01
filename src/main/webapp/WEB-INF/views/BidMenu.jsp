<%@ page import="nb.domain.Bid" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="nb.domain.Exchange" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<base href="${pageContext.request.contextPath}/"/>
<html>
<head>
    <script type="text/javascript" src="resources/js/jquery-3.2.1.js"></script>
    <script type="text/javascript" src="resources/js/Monthpicker.js"></script>
    <script type="text/javascript" src="resources/js/lotsMenu.js"></script>
    <script type="text/javascript" src="resources/js/docUploads.js"></script>
    <script type="text/javascript" src="resources/js/jquery-ui.js"></script>
    <link rel="stylesheet" media="screen" type="text/css" href="resources/css/jquery-ui.css"/>
    <link rel="stylesheet" media="screen" type="text/css" href="resources/css/jquery-ui.structure.css"/>
    <link rel="stylesheet" media="screen" type="text/css" href="resources/css/jquery-ui.theme.css"/>
    <link rel="stylesheet" media="screen" type="text/css" href="resources/css/general_style.css"/>
    <script>
        $(document).ready(function () {
            var objType = "bid";
            var bidN;
            var i = 0;
            var i_bid_new=0;

            function addDatePicker(dp) {
                dp.datepicker({
                    dateFormat: "yy-mm-dd",
                    dayNamesMin: ["Нд", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб"],
                    monthNames: ["січень", "лютий", "березень", "квітень", "травень", "червень",
                        "липень", "серпень", "вересень", "жовтень", "листопад", "грудень"]
                });
            }

            addDatePicker($('.datepicker'));
            addDatePicker($('[datatype = date]'));

            var addBidTr = $('#addBidTr');
            var bidTr = $('.bidTr');
            $('#icon_new_bid').click(function () {
                if (i_bid_new === 0) {
                    addBidTr.show();
                    i_bid_new++;
                }
                else {
                    addBidTr.hide();
                    i_bid_new--;
                }
            });

            bidTr.each(function() {
                var currentTr = $(this);
                var bidId = currentTr.find('.idBid').text();
                $.ajax({
                    url: "comentsByLotsFromBid",
                    type: "GET",
                    data: {bidId: bidId},
                    success: function (comments) {
                        currentTr.find('.lotData').attr("title", comments[0]);
                        currentTr.find('.lotData').text(comments[0].substring(0,45));
                    }
                })
            });

            bidTr.dblclick(function () {
                var bid = $(this);
                var bidId = $(this).find('.idBid').text();
                if (bid.next().is('.appendedTr')) {
                    bid.next().remove();
                }
                else {
                    $.ajax({
                        url: "lotsByBid",
                        type: "POST",
                        data: {bidId: bidId},
                        success: function (lots) {
                            if (lots.length > 0) {
                                var tr = $('<tr class="appendedTr"></tr>');
                                var td = $('<td colspan="12"></td>');
                                var table_lots = $('<table id="table_lots" class="table_lots" border="solid cyan"></table>');

                                var trhL = $('<tr id="trh" class="trh" style="color: #00ffff; background-color: #37415d">' +
                                    '<th>ID</th>' +
                                    '<th>№ Лоту</th>' +
                                    /*'<th>Оціночна вартість, грн.</th>' +*/
                                    '<th>Статус торгів</th>' +
                                    '<th>Початкова ціна, грн.</th>' +
                                    '<th>К-ть учасників</th>' +
                                    '<th>Ціна реалізації, грн.</th>' +
                                    '<th>Покупець</th>' +
                                    '<th>Статус оплати</th>' +
                                    '<th>Сплачено, грн.</th>' +
                                    '<th>Залишок оплати, грн.</th>' +
                                    '<th>Стадія</th>' +
                                    '<th>Коментар</th>' + '</tr>');

                                table_lots.append(trhL);

                                for (var i = 0; i < lots.length; i++) {
                                    var appendedTr = $('<tr style="color: lightcyan; background-color: #37415d; cursor: pointer" class="trL">' +
                                        '<td class="lotId">' + lots[i].id + '</td>' +
                                        '<td align="center">' + lots[i].lotNum + '</td>' +
                                        /*'<td align="center" class="sumOfCrd">' + '</td>' +*/
                                        '<td align="center">' + lots[i].bidStage + '</td>' +
                                        '<td align="center">' + lots[i].startPrice + '</td>' +
                                        '<td align="center">' + lots[i].countOfParticipants + '</td>' +
                                        '<td align="center" class="factPrice">' + lots[i].factPrice + '</td>' +
                                        '<td align="center" class="customer">' + lots[i].customerName + '</td>' +
                                        '<td align="center" class="payStatus">' + '</td>' +
                                        '<td align="center" class="paymentsSum">' + '</td>' +
                                        '<td align="center" class="residualToPay">' + '</td>' +
                                        '<td align="right">' + lots[i].workStage + '</td>' +
                                        '<td>' + lots[i].comment + '</td>' +
                                        '</tr>');
                                    table_lots.append(appendedTr);
                                }
                                td.append(table_lots);
                                tr.append(td);
                                bid.after(tr);
                            }
                            $('.trL').dblclick(function(){
                                var idL = $(this).find('.lotId').text();
                                window.open("lotRedactor/"+idL);
                            });
                            lotsCalculations();
                        }
                    })
                }
            });

            $('.button_accept_bid_changes').click(function(){

                    $.ajax({
                        url: "changeBidParams",
                        method: "POST",
                        data: {
                            bidId: $(this).parent().parent().find('.idBid').text(),
                            bidDate: $(this).parent().parent().find('.newBD').val(),
                            exId: $(this).parent().parent().find('.ex').val(),
                            newNP: $(this).parent().parent().find('.newNP').val(),
                            newND1: $(this).parent().parent().find('.newND1').val(),
                            //  newND2: $(this).parent().parent().find('.newND2').val(),
                            newRED: $(this).parent().parent().find('.newRED').val()
                        },
                        success: function (req) {
                            if (req == "1") {
                                alert("Аукціон змінено!");
                                location.href = 'bidMenu';
                            }
                        }
                    });

            });
            $('.changeButton').click(function () {
                $(this).parent().find('button').show();
                    var bd = $(this).parent().parent().find('.bidData');
                    bd.children().show();
                    bd.find('b').hide();
                    $(this).hide();
            });
            $('#buttAddBid').click(function () {
                $.ajax({
                    url: "createBid",
                    type: "GET",
                    data: {
                        exId: $('#exId').val(),
                        bidDate: $('#newBidDate').val(),
                        newspaper: $('#newspaper').val(),
                        newsDate1: $('#newsDate1').val(),
                        registrEnd: $('#registrEnd').val()
                    },
                    success: function (res) {
                        if (res === "1") {
                            alert("Аукціон додано!");
                            location.href = 'bidMenu';
                        }
                        else
                            alert("Аукціон НЕ додано!")
                    }
                })
            });
            /*$('.delBidButton').click(function () {
             $.ajax({
             url: "deleteBid",
             type: "POST",
             data: {idBid: $(this).parent().parent().find('.idBid').text()},
             success: function (res) {
             if (res === "1") {
             alert("Аукціон видалено!");
             location.href = 'bidMenu';
             }
             else
             alert("Аукціон НЕ видалено!")
             }
             })
             });*/
            $('.showDocs').click(function () {
                $('.div_documents_scan').show();
                $(".fileTr").remove();
                bidN = $(this).parent().parent().find('.idBid').text();
                $('#bidN').text(bidN);
                getFileNames(bidN, objType);
            });

            $('#icon_show_addDoc_form').click(function () {
                var div_addDoc_form = $('#div_addDoc_form');

                if (i === 0) {
                    div_addDoc_form.show();
                    i++;
                }
                else {
                    div_addDoc_form.hide();
                    i--;
                }
            });

            $('.getOgolosh').click(function () {
                window.open("downloadOgolosh/"+bidN);
                });

            $('.getObjTab').click(function () {
                window.open("downloadT/"+bidN);
            });
        })
    </script>
    <style type="text/css">

        input{
            width: 100%;
        }
        button{
            font-weight: bold;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            font-size: small;
        }

        .table_lots{
            width: 100%;
            border: 1px solid cyan;
            font-size: x-small;
        }
        .bidTr:hover {
            color: orangered;
            border-bottom: 1px solid orangered;
            cursor: pointer;
        }

        #dodatok2:hover, #objTab:hover {
            color: white;
            cursor: pointer;
            font-style: inherit;
        }
        #div_body{
            margin-top: -40px;
            display: table;
            width: 100%;
        }
        #div_bids{
            display: table-cell;
            width: 85%;
        }
        #div_docs{
            width: 15%;
            height: 100%;
            background: #37415d;
            position: fixed;
            display: table-cell;
            border: 1px solid ghostwhite;
            font-size: x-small;
        }
        #table_documents_scan, #table_documents_scan td {
            border: 1px solid ghostwhite;
        }
        .showDocs{
            width: 25px;
            height: 25px;
        }

        .lotData{
            width: 150px;
            height: 30px;
            text-align: left;
            overflow: hidden;
            text-overflow: ellipsis;
        }

    </style>
    <title>Торги</title>
    <%
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        List<Bid> bidList = (List<Bid>) request.getAttribute("bidList");
        List<Exchange> exchangeList = (List<Exchange>) request.getAttribute("exchangeList");
    %>
</head>
<body>
<header>
    <div id="div_left_side" class="div_header_additions">
        <div id="div_beck_img" title="назад" onclick="location.href='index'">
            <img src="resources/css/images/back.png">
        </div>
    </div>
    <div id="div_sheet_header">
        <h1>ТОРГИ</h1>
    </div>
    <div id="div_right_side" class="div_header_additions">
    </div>
</header>

<div id="div_body">

    <div id="div_bids">
        <table id="table_bids" border="1px">
            <tr id="addBidTr" hidden="hidden">
                <td></td>
                <td><input id="newBidDate" class="datepicker" title="оберіть дату аукціону" placeholder="дата торгів"></td>
                <td class="ex">
                    <select id="exId">
                        <%for (Exchange ex : exchangeList) {%>
                        <option value="<%out.print(ex.getId());%>">
                            <%
                                out.print(ex.getCompanyName());
                            %>
                        </option>
                        <%
                            }
                        %>
                    </select>
                </td>
                <td><input type="text" id="newspaper" title="заповніть назву газети"></td>
                <td><input id="newsDate1" class="datepicker" title="оберіть дату публікації 1"></td>
                <td><input id="registrEnd" class="datepicker" title="введіть дату кінця реєстрації"></td>
                <td>
                    <button id="buttAddBid">Підтвердити</button>
                </td>
            </tr>
            <tr id="addTr" style="border-bottom: 1px solid orangered; color: orangered; background-color: #37415d" >
                <th>ID</th>
                <th>Дата торгів</th>
                <th>Біржа</th>
                <th>N рішення</th>
                <th>Дата публікації</th>
                <th>Кінець реєстрації</th>
                <%--<th>Коментар</th>--%>
                <th>Коментарі лотів</th>
                <th colspan="2"> <img id="icon_new_bid" class="icon_button" style="width: 30px; height: 30px" src="resources/css/images/bid_new.png" title="Додати торги"></th>
            </tr>

            <%for (Bid bid : bidList) {%>
            <tr class="bidTr" align="center">
                <td class="idBid"><%=bid.getId()%></td>
                <td class="bidData"><input class="newBD" datatype="date" hidden="hidden"
                                           value="<%if(bid.getBidDate()!=null) out.print(sdf.format(bid.getBidDate()));%>">
                    <b><%if (bid.getBidDate() != null) out.print(sdf.format(bid.getBidDate()));%></b></td>
                <td class="bidData">
                    <select class="ex" hidden="hidden">
                        <% for (Exchange ex : exchangeList) {%>
                        <option value="<%=ex.getId()%>" <%
                            if (ex.getCompanyName().equals(bid.getExchange().getCompanyName()))
                                out.print("selected=selected");
                        %>>
                            <%out.print(ex.getCompanyName());%>
                        </option>
                        <%}%>
                    </select>
                    <b><%=bid.getExchange().getCompanyName()%>
                    </b>
                </td>
                <td class="bidData"><input class="newNP" type="text" hidden="hidden"
                                           value="<%if(bid.getNewspaper()!=null)out.print(bid.getNewspaper());%>">
                    <b><%if(bid.getNewspaper()!=null)out.print(bid.getNewspaper());%>
                </b>
                </td>
                <td class="bidData"><input class="newND1" datatype="date" hidden="hidden"
                                           value="<%if(bid.getNews1Date()!=null)out.print(sdf.format(bid.getNews1Date()));%>">
                    <b><%if (bid.getNews1Date() != null) out.print(sdf.format(bid.getNews1Date()));%></b></td>

                <td class="bidData"><input class="newRED" datatype="date" hidden="hidden"
                                           value="<%if(bid.getRegistrEndDate()!=null)out.print(sdf.format(bid.getRegistrEndDate()));%>">
                    <b><%if (bid.getRegistrEndDate() != null) out.print(sdf.format(bid.getRegistrEndDate()));%>
                    </b>
                </td>
                <%--<td class="bidData"><input class="newND2" hidden="hidden"
                                           value="<%if(bid.getComent()!=null)out.print(bid.getComent());%>">
                    <b><%if(bid.getComent()!=null)out.print(bid.getComent());%></b>
                </td>--%>
                <td class="lotData">

                </td>
                <td>
                    <img class="icon_button changeButton" style="width: 25px; height: 25px" title="Редагувати" src="resources/css/images/redactor_icon.png">
                    <button class="button_accept_bid_changes" hidden="hidden" style="width: 100%; height: 100%;">ок</button>
                </td>
                <%--<td ><button class="delBidButton">Видалити торги</button></td>--%>
                <td>
                    <img class="showDocs icon_button" src="resources/css/images/docs.png" title="Клікніть для відображення документів по аукціону">
                </td>
            </tr>
            <%}%>
        </table>
    </div>

    <div id="div_docs" >
        <div class="div_documents_scan" hidden="hidden">
            <table>
                <tr>
                    <td id="dodatok2" title="Клікніть двічі для завантаженн Додатку 2">
                        <a class="getOgolosh">Оголошення (.xls)</a>
                    </td>
                    <td id="objTab" title="Клікніть двічі для завантаження таблиці об'єктів">
                        <a class="getObjTab">Таблиця об'єктів (.xls)</a>
                    </td>
                </tr>
            </table>
        </div>
        <div class="div_documents_scan" hidden="hidden">
            <table id="table_documents_scan">
                <tr>
                    <th>
                        <img class="icon_button" id="icon_show_addDoc_form" src="resources/css/images/add_document.png" width="20px" height="20px" title="натисніть для завантаження документів">
                        Документи Аукціон N <b id="bidN" style="color: sandybrown"></b>
                    </th>

                </tr>
            </table>
        </div>
        <div id="div_addDoc_form" hidden="hidden" style="background-color: ghostwhite; color: black">
            <form method="POST" action="uploadFile" enctype="multipart/form-data"
                  lang="utf8">
                <%--<input type="text" id="objType" name="objType" value="bid" hidden="hidden">
                <input type="text" id="objId" name="objId" value="" hidden="hidden">--%>
                Обрати документ для збереження:
                <br/>
                <input align="center" type="file" name="file" title="натисніть для обрання файлу"><br/>
                <input align="center" type="submit" value="Зберегти" title="натисніть щоб зберегти файл">
            </form>
        </div>
    </div>

</div>

</body>
</html>