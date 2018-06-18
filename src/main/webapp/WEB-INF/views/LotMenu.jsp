<%@ page import="nb.domain.Bid" %>
<%@ page import="nb.domain.Lot" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<base href="${pageContext.request.contextPath}/"/>
<html>
<head>
    <%
        String saleStatus = (String) request.getAttribute("saleStatus");
        String lotsType = (String) request.getAttribute("lotsType");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        List<Lot> lotList = (List<Lot>) request.getAttribute("lotList");

        TreeSet<Date> dateSet = new TreeSet<>();
        for (Lot lot : lotList) {
            if (lot.getBid() != null && lot.getBid().getBidDate() != null)
                dateSet.add(lot.getBid().getBidDate());
        }
        TreeSet<String> exchangeSet = new TreeSet<>();
        for (Lot lot : lotList) {
            if (lot.getBid() != null && lot.getBid().getExchange() != null)
                exchangeSet.add(lot.getBid().getExchange().getCompanyName());
        }
    %>

    <title>Меню лотів</title>
    <script type="text/javascript" src="resources/js/jquery-3.2.1.js"></script>
    <script type="text/javascript" src="resources/js/lotsMenu.js"></script>
    <script type="text/javascript">
        $(document).ready(function () {
            lotsCalculations();

            function checkBids() {
                $('.bidStatus').each(function () {
                    if ($(this).text() === "Торги не відбулись")
                        $(this).parent().css('color', 'red');
                });
                $('.neadNewFondDec').each(function () {
                    if ($(this).text() === "Так")
                        $(this).css('color', 'red');
                });
            }

            checkBids();
            var dateSelector = $('.dateSelector');
            var exSelector = $('.exSelector');
            var bidStatusSelector = $('.bidStatusSelector');
            var div_create_lot = $('#div_create_lot');

            div_create_lot.click(function () {
                if ($('#radio_asset_lot_type').prop('checked')) {
                    window.open("lotCreator")
                }
                else if ($('#radio_credit_lot_type').prop('checked')) {
                    window.open("lotCreditsCreator")
                }
                else alert("Оберіть тип нового лоту!")
            });

            bidStatusSelector.click(function () {
                $('.bidStatusHide').each(function () {
                    $(this).removeClass();
                    $(this).addClass("bidStatus");
                });

                if ($(this).text() != "всі") {
                    var selectedStatus = $(this).text();
                    $('.bidStatus').each(function () {
                        if ($(this).text() != selectedStatus) {
                            $(this).removeClass();
                            $(this).addClass("bidStatusHide");
                        }
                    });
                }
                filterLots();
            });

            dateSelector.click(function () {
                $('.bidDateHide').each(function () {
                    $(this).removeClass();
                    $(this).addClass("bidDate");
                });

                if ($(this).text() != "всі дати") {
                    var selectedDate = $(this).text();
                    $('.bidDate').each(function () {
                        if ($(this).text() != selectedDate) {
                            $(this).removeClass();
                            $(this).addClass("bidDateHide");
                        }
                    });
                }
                filterLots();
            });

            exSelector.click(function () {
                $('.companyHide').each(function () {
                    $(this).removeClass();
                    $(this).addClass("company");
                });

                if ($(this).text() != "всі біржі") {
                    var exSelected = $(this).text();
                    $('.company').each(function () {
                        if ($(this).text() != exSelected) {
                            $(this).removeClass();
                            $(this).addClass("companyHide");
                        }
                    });
                }
                filterLots()
            });

            function filterLots() {
                $('.trL').show();
                $('.bidDateHide').each(function () {
                    $(this).parent().hide();
                });
                $('.companyHide').each(function () {
                    $(this).parent().hide();
                });
                $('.bidStatusHide').each(function () {
                    $(this).parent().hide();
                });
            }

            var i = 0;
            $('.spoiler_links').click(function () {
                if (i === 0) {
                    $(this).children('div.spoiler_body').slideDown('fast');
                    i = 1;
                }
                else {
                    $(this).children('div.spoiler_body').slideUp('fast');
                    i = 0;
                }
            });
        });
    </script>

    <link href="resources/css/general_style.css" rel="stylesheet" type="text/css">
    <style>

        #div_lots_filter {
            margin-top: -30px;
            margin-bottom: 5px;
        }

        #table_lots {
            width: 100%;
            font-size: 12px;
            border-collapse: collapse;
            border: solid 1px #00ffff;
        }

        #table_lots tr:nth-child(odd) {
            background-color: #37415d; /* Цвет фона */
        }

        #table_lots .trL:hover {
            color: ghostwhite;
            background-color: #141429; /* Цвет фона */
        }

        #table_lots td, #table_lots th {
            border: solid 1px #00ffff;
        }

        .lotId, .bidDate, .bidStatus, .bidStage, .workstage {
            font-weight: bold;
        }

        .spoiler_body b:hover {
            color: #00ffff ;
            border: 1px solid #00ffff;
        }

        .spoiler_body {
            display: none;
            cursor: pointer;
            float: left;
            width: auto;
            background-color: #37415d;
            color: white;
            text-align: center;
            position: absolute;
            z-index: 99;
        }

        .spoiler_links {
            height: 100%;
            width: 100%;
            cursor: pointer;
            float: left;
            margin: 0px 5px;
            line-height: 1.5;
            text-align: center;
        }

        .trL {
            cursor: pointer;
        }

        #filterMenu_table button {
            width: 160px;
            height: 30px;
            font-size: 100%;
            text-align: left;
        }

        #createMenu_table button {
            width: 120%;
            height: 30px;
            font-size: 100%;
            text-align: left;
        }

        #createMenu_table img {
            width: 25px;
            height: 25px;
        }
    </style>
</head>
<body>

<header>
    <div id="div_left_side" class="div_header_additions">
        <div id="div_beck_img" title="назад" onclick="location.href='index'">
            <img src="resources/css/images/back.png">
        </div>
    </div>
    <div id="div_sheet_header">
        <h1>МЕНЮ ЛОТІВ</h1>
    </div>
    <div id="div_right_side" class="div_header_additions">
        <div id="div_create_lot" title="створити новий лот">
            <img src="resources/css/images/create_lot.png">
        </div>
        <div style="text-align: left">
            <input type="radio" name="radio_new_lot_type" id="radio_credit_lot_type"> кредити
            <br/>
            <input type="radio" name="radio_new_lot_type" id="radio_asset_lot_type"> активи
        </div>
    </div>
</header>

<div id="div_lots_filter">
    Всі лоти <input type="radio" name="lot_type"
                    onclick="location.href ='lotMenu/<%out.print(lotsType+"/");%>all'" <%if (saleStatus.equals("all")) out.print("checked=\"checked\"");%> >
    Непродані лоти <input type="radio" name="lot_type"
                          onclick="location.href ='lotMenu/<%out.print(lotsType+"/");%>notSolded'" <%if (saleStatus.equals("notSolded")) out.print("checked=\"checked\"");%> >
    Продані лоти <input type="radio" name="lot_type"
                        onclick="location.href ='lotMenu/<%out.print(lotsType+"/");%>solded'" <%if (saleStatus.equals("solded")) out.print("checked=\"checked\"");%> >
</div>

<div id="div_lots" class="view">
    <table id="table_lots" class="table">
        <tr class="trh" style="color: #00ffff">
            <th>ID</th>
            <th title="Натисніть для відображення фільтру">
                <div class="spoiler_links">Дата торгів^
                    <div class="spoiler_body">
                        <b class="dateSelector">всі дати</b>
                        <br>
                        <% for (Date date : dateSet) {%>
                        <b class="dateSelector"><%out.print(sdf.format(date));%></b>
                        <br>
                        <%}%>
                    </div>
                </div>
            </th>
            <th title="Натисніть для відображення фільтру">
                <div class="spoiler_links" style="width: 100%; height: 100%">Біржа^
                    <div class="spoiler_body">
                        <b class="exSelector">всі біржі</b>
                        <br>
                        <% for (String exchangeName : exchangeSet) {%>
                        <b class="exSelector"><%out.print(exchangeName);%></b>
                        <br>
                        <%}%>
                    </div>
                </div>
            </th>
            <%--<th>Оціночна вартість, грн.</th>--%>
            <th>Торги</th>
            <th title="Натисніть для відображення фільтру">
                <div class="spoiler_links" style="width: 100%; height: 100%">Статус аукціону^
                    <div class="spoiler_body">
                        <b class="bidStatusSelector">всі</b>
                        <br>
                        <b class="bidStatusSelector">Торги не відбулись</b>
                        <br>
                        <b class="bidStatusSelector">Торги відбулись</b>
                    </div>
                </div>
            </th>
            <th>№ Лоту в публікації</th>
            <th>Початкова ціна лоту, грн.</th>
            <%--<th>Кількість зареєстрованих учасиків</th>--%>
            <th>Ціна реалізації, грн.</th>
            <th>Статус оплати</th>
            <th>Сума сплати</th>
            <%--<th>Залишок оплати, грн.</th>--%>
            <th>Стадія роботи</th>
            <th>Покупець</th>
            <th>Акт підписано</th>
            <%--<th>Співробітник</th>--%>
            <th>Коментар</th>
            <th>Потребує перепогодження ФГВФО</th>
        </tr>
        <%
            for (Lot lot : lotList) {
                Bid bid = lot.getBid();
        %>
        <tr class="trL" align="center">
            <td class="lotId" <%if (lot.getLotType() == 0) out.print("style=\"color: #00a5ff\"");%> ><%=lot.getId()%></td>
            <td class="bidDate"><%
                if (bid != null && bid.getBidDate() != null) {
                    out.print(sdf.format(bid.getBidDate()));
                }
            %></td>
            <td class="company"><%
                if (bid != null && bid.getExchange() != null) out.print(bid.getExchange().getCompanyName());%></td>
            <%--<td class="sumOfCrd"></td>--%>
            <td class="bidStage"><%out.print(lot.getBidStage());%></td>
            <td class="bidStatus"><%if (lot.getStatus() != null) out.print(lot.getStatus());%></td>
            <td class="lotNum"><%if (lot.getLotNum() != null) out.print(lot.getLotNum());%></td>
            <td class="startPrice"><%if (lot.getStartPrice() != null) out.print(lot.getStartPrice());%></td>
            <td class="factPrice"><%if (lot.getFactPrice() != null) out.print(lot.getFactPrice());%></td>
            <td class="payStatus"></td>
            <td class="paymentsSum"></td>
            <%--<td align="center" class="residualToPay"></td>--%>
            <td class="workstage"><%=lot.getWorkStage()%>
            </td>
            <td class="customer"><%if (lot.getCustomerName() != null) out.print(lot.getCustomerName());%></td>
            <td class="aktDate"><%
                if (lot.getActSignedDate() != null) out.print(sdf.format(lot.getActSignedDate()));%></td>
            <%--<td align="center" class="user"><%=lot.getUser().getLogin()%></td>--%>
            <td class="comment"><%if (lot.getComment() != null) out.print(lot.getComment());%></td>
            <td class="neadNewFondDec"><%
                if (lot.isNeedNewFondDec()) {
                    out.print("Так");
                } else out.print("Ні");
            %></td>
        </tr>
        <%}%>
    </table>
</div>

</body>
</html>