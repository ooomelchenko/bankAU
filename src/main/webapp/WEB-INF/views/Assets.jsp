<%@ page import="nb.domain.Asset" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="nb.domain.Exchange" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<base href="${pageContext.request.contextPath}/"/>
<html>
<head>
    <%
        int assetPortion = (Integer) request.getAttribute("assetPortion");
        List<Asset> assetList = (List<Asset>) request.getAttribute("assetList");
        /*List<String> fondDecisionsList = (List<String>) request.getAttribute("fondDecisionsList");*/
        List<Date> allBidDates = (List<Date>) request.getAttribute("allBidDates");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        SimpleDateFormat yearMonthFormat = new SimpleDateFormat("MM/yyyy", Locale.ENGLISH);
        List<String> bidResultList = (List<String>) request.getAttribute("bidResultList");
        List<String> workStages = (List<String>) request.getAttribute("workStages");
        List<String> exchangeList = (List<String>) request.getAttribute("exchangeList");
        List<String> decisionNumbers = (List<String>) request.getAttribute("decisionNumbers");
        List<Long> allLotId = (List<Long>) request.getAttribute("allLotId");
        List<Exchange> allExchangeList = (List<Exchange>) request.getAttribute("allExchangeList");
    %>
    <title>Список об'єктів</title>

    <script type="text/javascript" src="resources/js/jquery-3.2.1.js"></script>
    <script type="text/javascript" src="resources/js/jquery-ui.js"></script>
    <script type="text/javascript" src="resources/js/Monthpicker.js"></script>

    <link rel="stylesheet" media="screen" type="text/css" href="resources/css/general_style.css"/>
    <link rel="stylesheet" media="screen" type="text/css" href="resources/css/monthpicker.css"/>
    <link rel="stylesheet" media="screen" type="text/css" href="resources/css/jquery-ui.css"/>
    <link rel="stylesheet" media="screen" type="text/css" href="resources/css/jquery-ui.structure.css"/>
    <link rel="stylesheet" media="screen" type="text/css" href="resources/css/jquery-ui.theme.css"/>

    <script>
        $(document).ready(function(){
            var portionNum = $('#portion');
            $('#back').click( function(){
                var nval = parseInt(portionNum.val())-1;
                getPortion(nval);
            });
            $('#forward').click( function(){
                var nval = parseInt(portionNum.val())+1;
                getPortion(nval);
            });
            portionNum.keydown(function(event){
                if(event.keyCode==13);
                getPortion(portionNum);
            });

            function getPortion(portNum){
                location.replace("assets/"+portNum);
            }

            function loadFile(){
                var formData = new FormData($('form')[0]);
                $.ajax({
                    url: "setAccPriceByFile",
                    type: "POST",
                    processData: false,
                    contentType: false,
                    data: formData,
                    success: function (res) {
                        if (res==1){
                            alert("затверджені ціни додано!");
                             location.reload(true);
                        }
                        else if (res==0){
                            alert("затверджені ціни не додано!");
                        }
                    }
                })
            }

            var dp = $('#inputFondDecDate');
            dp.datepicker({dateFormat: "yy-mm-dd", dayNamesMin: ["Нд","Пн","Вт","Ср","Чт","Пт","Сб"], monthNames: ["січень","лютий","березень", "квітень", "травень","червень",
                "липень","серпень","вересень","жовтень","листопад","грудень"] });
            var countOfPortions;
            $('.objId').each(function(){
                var obj = $(this);
                $.ajax({
                    url: "getPaySum_Residual",
                    type: "GET",
                    data: {id: obj.text()},
                    success: function (paySum_Residual) {
                        if(paySum_Residual.length>0){
                            obj.parent().find('.paysSum').text(paySum_Residual[0]);
                            obj.parent().find('.residualToPay').text(paySum_Residual[1]);

                            if(paySum_Residual[1]!=null && paySum_Residual[1]<=0){
                                obj.parent().find('.payStatus').text("100% сплата");
                            }
                            else{
                                if(paySum_Residual[0]==0){
                                    obj.parent().find('.payStatus').text("Очікується оплата");
                                }
                                else obj.parent().find('.payStatus').text("Часткова оплата");
                            }
                        }
                    }
                });
            });
            $('.lotId').dblclick(function(){
                var lotId =$(this).text();
                if(lotId!=""){
                    window.open("lotRedactor/"+lotId)
                }
            });

            $.ajax({
                url: "getTotalCountOfObjects",
                type: "GET",
                success: function (count) {
                    countOfPortions = parseInt(count/5000+1);
                    $('#range').text("1 - "+countOfPortions + "  (по 5000)");
                }
            });

            $('#createLot').click(function(){
                var idList="";
                $(".check-asset:checked").each(function(){
                    idList=idList+','+$(this).parent().parent().find('.objId').text();
                });
                $.ajax({
                    url: "createLotByCheckedAssets",
                    type: "POST",
                    data: {idList: idList.substring(1)},
                    success: function(result){
                        if(result===1) {
                            window.open("lotCreator1");
                        }
                        else alert("Не обрано жодного об'єкта");
                    }
                })
            });
            $('.check-asset').change(function(){
                if($(this).is(':checked')){
                    $(this).parent().parent().css({'color': "#00ffff"});
                }
                else{
                    $(this).parent().parent().css({'color': ""});
                }
            });
            $('.inn').click(function(){
                var checkBox = $(this).parent().find('.check-asset');
                if(checkBox.is(':checked')){
                    checkBox.prop('checked', false);
                    $(this).parent().css({'color': ""});
                }
                else{
                    checkBox.prop('checked', true);
                    $(this).parent().css({'color': "#00ffff"});
                }
            });

            var bidDateSelector = $('.bidDateSelector');
            var nbuZastSelector = $('.nbuZastSelector');
            var payStatus = $('.payStatusSelector');
            var bidResult = $('.bidResSelector');
            var workStage = $('.workStageSelector');
            var exchange = $('.exchangeSelector');
            var fondDecNum = $('.fondDecNumSelector');
            var lotIdSelector = $('.lotIDSelector');
            var selectors = $('.bidDateSelector, .nbuZastSelector, .payStatusSelector, .bidResSelector, .workStageSelector, .exchangeSelector, .fondDecNumSelector, .lotIDSelector');
            selectors.hover(function(){
                $(this).css(
                        {'color': "white"})
            })
                    .mouseout( function(){
                        $(this).css({'color': ""})
            });

            bidDateSelector.click(function () {
                $('.bidDateHide').each(function () {
                    $(this).removeClass();
                    $(this).addClass("bidDate");
                });
                var exSelected = $(this).text();
                if (exSelected != "всі дати") {

                    $('.bidDate').each(function () {
                        if ($(this).text() != exSelected) {
                            $(this).removeClass();
                            $(this).addClass("bidDateHide");
                        }
                    });
                }
                doFilters();
            });
            nbuZastSelector.click(function () {
                $('.nbuZastHide').each(function () {
                    $(this).removeClass();
                    $(this).addClass("nbuZast");
                });
                var nbuSelected = $(this).text();
                if (nbuSelected != "всі") {
                    $('.nbuZast').each(function () {
                        if ($(this).text() != nbuSelected) {
                            $(this).removeClass();
                            $(this).addClass("nbuZastHide");
                        }
                    });
                }
                doFilters();
            });
            payStatus.click(function () {
                $('.payStatusHide').each(function () {
                    $(this).removeClass();
                    $(this).addClass("payStatus");
                });
                var payStatusSelected = $(this).text();
                if (payStatusSelected != "всі") {
                    $('.payStatus').each(function () {
                        if ($(this).text() != payStatusSelected) {
                            $(this).removeClass();
                            $(this).addClass("payStatusHide");
                        }
                    });
                }
                doFilters();
            });
            bidResult.click(function () {
                $('.bidResultHide').each(function () {
                    $(this).removeClass();
                    $(this).addClass("bidResult");
                });
                var bidResultSelected = $(this).text();
                if (bidResultSelected != "всі") {
                    $('.bidResult').each(function () {
                        if ($(this).text() != bidResultSelected) {
                            $(this).removeClass();
                            $(this).addClass("bidResultHide");
                        }
                    });
                }
                doFilters();
            });
            workStage.click(function () {
                $('.workStageHide').each(function () {
                    $(this).removeClass();
                    $(this).addClass("workStage");
                });
                var workStageSelected = $(this).text();
                if (workStageSelected != "всі") {
                    $('.workStage').each(function () {
                        if ($(this).text() != workStageSelected) {
                            $(this).removeClass();
                            $(this).addClass("workStageHide");
                        }
                    });
                }
                doFilters();
            });
            exchange.click(function () {
                $('.exchangeNameHide').each(function () {
                    $(this).removeClass();
                    $(this).addClass("exchangeName");
                });
                var exchangeNameSelected = $(this).text();
                if (exchangeNameSelected != "всі") {
                    $('.exchangeName').each(function () {
                        if ($(this).text() != exchangeNameSelected) {
                            $(this).removeClass();
                            $(this).addClass("exchangeNameHide");
                        }
                    });
                }
                doFilters();
            });
            fondDecNum.click(function(){
                $('.fondDecNumHide').each(function(){
                    $(this).removeClass();
                    $(this).addClass("fondDecNum");
                });
                var fondDecNumSelected = $(this).text();
                if(fondDecNumSelected !="всі"){
                    $('.fondDecNum').each(function(){
                        if ($(this).text() != fondDecNumSelected){
                            $(this).removeClass();
                            $(this).addClass("fondDecNumHide");
                        }
                    })
                }
                doFilters();
            });
            lotIdSelector.click(function(){
                $('.lotIdHide').each(function(){
                    $(this).removeClass();
                    $(this).addClass("lotId");
                });
                var lotIdSelected = $(this).text();
                if(lotIdSelected !="всі лоти"){
                    $('.lotId').each(function(){
                        if ($(this).text() != lotIdSelected){
                            $(this).removeClass();
                            $(this).addClass("lotIdHide");
                        }
                    })
                }
                doFilters();
            });

            $('.acceptPrice').dblclick(function () {
                if ($(this).find("input").is(":hidden")) {
                    $(this).find("input").show();
                    $(this).find("div").hide();
                    $(this).append($("<button class='okButton'>ok</button>").click(function () {
                                var assetTr = $(this).parent().parent();
                                var inAcceptPrice = $(this).parent().find('input').val();
                                $.ajax({
                                    url: "setAcceptedPrice",
                                    type: "POST",
                                    data: {
                                        assetId: $(this).parent().parent().find('.objId').text(),
                                        acceptPrice: inAcceptPrice
                                    },
                                    success: function (result) {
                                        if (result == "1") {
                                            assetTr.find(".acceptDiv").text(inAcceptPrice);
                                            assetTr.find(".inputAcceptPrice").hide();
                                            assetTr.find(".okButton").remove();
                                            assetTr.find(".acceptDiv").show();
                                        }
                                        else alert("данні не внесені!");
                                    }
                                });
                            })
                    );
                }

            });

            $('.planSaleDate').dblclick(function(){
                var planSaleTd = $(this);
                var objId = $(this).parent().find('.objId');
                var data =$(this).text();
                $(this).empty();
                var inp = $('<input type="text"/>');
                inp.val(data);

                $(this).append(inp);
                inp.Monthpicker({
                    format: 'yyyy.mm',
                    minYear: 2016,
                    maxYear: 2030,
                    minValue: null,
                    maxValue: null,
                    monthLabels: ["Січ", "Лют", "Бер", "Кві", "Тра", "Чер", "Лип", "Сер", "Вер", "Жов", "Лис", "Гру"],
                    onSelect: function () {
                        // $(this).text(inp.val());
                        $.ajax({
                            url: "setPlanSaleDate",
                            type: "GET",
                            data: {objID: objId.text(),
                                objType: 1,
                                planDate: inp.val()
                            },
                            success: function(){
                                planSaleTd.text(inp.val());
                                inp.remove();
                            }
                        });
                    }
                });
            });

            function doFilters(){
                $('.assetTr').show();
                var hideElements =$('.bidDateHide, .nbuZastHide, .payStatusHide, .bidResultHide, .workStageHide, .exchangeNameHide, .fondDecNumHide, .lotIdHide');
                hideElements.each(function(){
                   $(this).parent().hide();
                });
            }

            var i=0;
            $('.spoiler_links').click(function(){
                if(i===0){
                    $(this).children('div.spoiler_body').slideDown('fast');
                    i=1;
                }
                else{
                    $(this).children('div.spoiler_body').slideUp('fast');
                    i=0;}
            });
            $('#printObj').click(function(){

                var lotToPrint = "";
                $('.id:visible').each(function(){
                    lotToPrint +=","+($(this).text());
                });
                $.ajax({
                    url: "setLotsToPrint",
                    type: "GET",
                    data: {lotsId: lotToPrint.substring(1)},
                    success: function(res){
                        if(res=='1') {
                            window.open("download");
                        }
                    }
                });
            });

            $('#addPriceByFileBut').click(function(){
                if($(this).val()==0) {
                    $('#div_add_dec_form').show();
                    $(this).val(1);
                    $(this).text("Приховати");
                }
                else if($(this).val()==1) {
                    $('#div_add_dec_form').hide();
                    $(this).val(0);
                    $(this).text("ДОДАТИ ЗАТВЕРДЖЕНУ ФГВФО ЦІНУ");
                }
            });

            $('#button_addPriceByFile').click(function(){
                loadFile();
            });

        })
    </script>
    <style type="text/css">
        table{
            border-collapse: collapse;
            font-size: x-small;
        }
        #div_b0{
            margin-top: -40px;
        }
        #div_obj_table {
            width: 100%;
            margin-top: 1px;
        }
        .assetTr:hover{
            color: ghostwhite;
            background: #37415d;
        }
        .lotId:hover {
            cursor: pointer;
            font-style: italic;
        }
        .spoiler_body {
            display:none;
            cursor:pointer;
            float:left;
            width:auto;
            background: #37415d;
            text-align:center;
            position:absolute;
            z-index:99;}
        .spoiler_body a {padding:0px 10px;}
        .spoiler_links {
            cursor: pointer;
            float:left;
            margin:0px 5px;
            width:auto;
            line-height:1.5;
            text-align:center;}
        .assetTr {
            cursor: default;
        }

    </style>
</head>
<body>

<header>
    <div id="div_left_side" class="div_header_additions">
        <img id="createLot" class="icon_button" title="СТВОРИТИ ЛОТ З ОБРАНИХ ОБ'ЄКТІВ" src="resources/css/images/create_lot.png">
    </div>
    <div id="div_sheet_header">
        <table align="center">
            <tr>
                <td id="range" colspan="3" align="center">
                </td>
            </tr>
            <tr>
                <td>
                    <button id="back">назад</button>
                </td>
                <td><input value="<%out.print(assetPortion);%>" type="number" id="portion"/></td>
                <td>
                    <button id="forward">вперед</button>
                </td>
            </tr>
        </table>
    </div>
    <div id="div_right_side" class="div_header_additions">
        <button id="addPriceByFileBut" value="0">ДОДАТИ ЗАТВЕРДЖЕНУ ФГВФО ЦІНУ</button>
    </div>
</header>

<div id="div_b0" >
        <div id="div_add_dec_form" hidden="hidden">
            <form method="POST" action="" enctype="multipart/form-data" lang="utf8" >
                <h3>Обрати файл зі списком Інвентарних номерів:</h3>
                <input align="center" type="file" name="file" title="натисніть для обрання файлу"><br/>
                <input name="idType" value="1" type="number" hidden="hidden">
            </form>
            <button id="button_addPriceByFile" title="завантажити ціну з файлу">Завантажити</button>
        </div>
</div>

<div id="div_obj_table">
    <table id="table_objects" border="1px">
        <tr style="color: limegreen; border-bottom: 1px solid limegreen">
            <th></th>
            <th hidden="hidden">ID</th>
            <th>
                <div class="spoiler_links" style="width: 100%; height: 100%">^ID лоту^
                    <div class="spoiler_body" >
                        <b class="lotIDSelector">всі лоти</b><br>
                        <% for(Long lotId: allLotId){%>
                        <b class="lotIDSelector"><%out.print(lotId);%></b><br>
                        <%}%>
                    </div>
                </div>
            </th>
            <th>№ Лоту в публікації</th>
            <th>Інвентарний №</th>
            <th>
                <div class="spoiler_links" style="width: 100%; height: 100%">^Дата аукціону^
                    <div class="spoiler_body" >
                        <b class="bidDateSelector" style="width:100%">всі дати</b><br>
                        <% for(Date bidDate: allBidDates){%>
                        <b class="bidDateSelector" style="width:100%"><%out.print(sdf.format(bidDate));%></b><br>
                        <%}%>
                    </div>
                </div>
            </th>
            <th>
                <div class="spoiler_links" style="width: 100%; height: 100%">^Біржа^
                    <div class="spoiler_body" >
                        <b class="exchangeSelector">всі</b><br>
                        <% for(String ex: exchangeList){%>
                        <b class="exchangeSelector"><%out.print(ex);%></b><br>
                        <%}%>
                    </div>
                </div>
            </th>
            <th>Найменування</th>
            <th>
                <div class="spoiler_links" style="width: 100%; height: 100%">^Застава НБУ^
                    <div class="spoiler_body" >
                        <b class="nbuZastSelector">всі</b><br>
                        <b class="nbuZastSelector">Так</b><br>
                        <b class="nbuZastSelector">Ні</b><br>
                    </div>
                </div>
            </th>
            <th>Оціночна вартість, грн.</th>
            <th>Стадія торгів</th>
            <%--<th>Початкова ціна, грн.</th>--%>
            <th>Кількість зареєстрованих учасників</th>
            <th>Зниження початкової ціни(%)</th>
            <th>Ціна реалізації, грн.</th>
            <th>
                <div class="spoiler_links" style="width: 100%; height: 100%">^Статус аукціону^
                    <div class="spoiler_body" >
                        <b class="bidResSelector">всі</b><br>
                        <% for(String bidres: bidResultList){%>
                        <b class="bidResSelector"><%out.print(bidres);%></b><br>
                        <%}%>
                    </div>
                </div>
            </th>
            <th>
                <div class="spoiler_links" style="width: 100%; height: 100%">^Статус оплати^
                    <div class="spoiler_body" >
                        <b class="payStatusSelector">всі</b><br>
                        <b class="payStatusSelector">Часткова оплата</b><br>
                        <b class="payStatusSelector">100% сплата</b><br>
                    </div>
                </div>
            </th>
            <th>Сплачено, грн.</th>
            <th>Залишок оплати, грн.</th>
            <th>Покупець</th>
            <th>
                <div class="spoiler_links" style="width: 100%; height: 100%">^Стадія роботи^
                    <div class="spoiler_body" >
                        <b class="workStageSelector">всі</b><br>
                        <% for(String ws: workStages){%>
                        <b class="workStageSelector"><%out.print(ws);%></b><br>
                        <%}%>
                    </div>
                </div>
            </th>
            <th>Дата прийняття рішення ФГВФО</th>
            <th>Рівень прийняття рішення</th>
            <th>
                <div class="spoiler_links" style="width: 100%; height: 100%">^Номер рішення^
                    <div class="spoiler_body" >
                        <b class="fondDecNumSelector">всі</b><br>
                        <% for(String decNum: decisionNumbers){%>
                        <b class="fondDecNumSelector"><%out.print(decNum);%></b><br>
                        <%}%>
                    </div>
                </div>
            </th>
            <th>Погоджена початкова ціна</th>
            <th>Погоджена біржа</th>
            <th>Необхідно перепогодити</th>
            <th>Заплановано реалізувати</th>
            <th>Акт підписано</th>
        </tr>

        <%for(Asset asset: assetList){%>
        <tr align="center" class="assetTr">
            <td class="checkTd"><input size="120%" type="checkbox" class="check-asset" title="Обрання активів для подальшої обробки"></td>
            <td class="objId" hidden="hidden"><%=asset.getId()%></td>
            <td class="lotId" style="font-weight: bold; color: #00ffff" title="для переходу до ЛОТу подвійний клік"><%if(asset.getLot()!=null)out.print(asset.getLot().getId());%></td>
            <td><%if(asset.getLot()!=null) out.print(asset.getLot().getLotNum());%></td>
            <td class="inn"><%=asset.getInn()%></td>
            <td class="bidDate"><%if(asset.getLot()!=null&&asset.getLot().getBid()!=null) out.print(sdf.format(asset.getLot().getBid().getBidDate()));%></td>
            <td class="exchangeName"><%if(asset.getLot()!=null&&asset.getLot().getBid()!=null) out.print(asset.getLot().getBid().getExchange().getCompanyName());%></td>
            <td><%=asset.getAsset_descr()%></td>
            <td class="nbuZast"><%if(asset.isApproveNBU())out.print("Так");else out.print("Ні");%></td>
            <td style="font-weight: bold"><%=asset.getRv()%></td>
            <td><%if(asset.getLot()!=null) out.print(asset.getLot().getBidStage());%></td>
            <%--<td> <%if(asset.getBidPrice()!=null)out.print(asset.getBidPrice());%></td>--%>
            <td><%if(asset.getLot()!=null) out.print(asset.getLot().getCountOfParticipants());%></td>
            <td class="discountPr"><%
                if (asset.getLot()!=null&&asset.getLot().getStartPrice() != null && asset.getLot().getFirstStartPrice() != null)
                    out.print((new BigDecimal(1).subtract(asset.getLot().getStartPrice().divide(asset.getLot().getFirstStartPrice(), 4, BigDecimal.ROUND_HALF_UP))).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP) + "%");
            %></td>
            <td class="factPrice" style="font-weight: bold"><%if(asset.getFactPrice()!=null)out.print(asset.getFactPrice());%></td>
            <td class="bidResult"><%if(asset.getLot()!=null) out.print(asset.getLot().getStatus());%></td>
            <td class="payStatus"></td>
            <%--<td class="paysSum" style="font-weight: bold"></td>
            <td class="residualToPay" style="font-weight: bold"></td>--%>
            <td class="paysSum" style="font-weight: bold"><%out.print(asset.getPaysBid().add(asset.getPaysCustomer()));%></td>
            <td class="residualToPay" style="font-weight: bold"><%if(asset.getFactPrice()!=null )out.print(asset.getFactPrice().subtract(asset.getPaysBid().add(asset.getPaysCustomer())));%></td>
            <td class="customerName"><%if(asset.getLot()!=null) out.print(asset.getLot().getCustomerName());%></td>
            <td class="workStage"><%if(asset.getLot()!=null) out.print(asset.getLot().getWorkStage());%></td>
            <td><%--<input class="inputFondDecDate" value="<%if(asset.getLot()!=null && asset.getLot().getFondDecisionDate()!=null)out.print(sdf.format(asset.getLot().getFondDecisionDate()));%>" hidden="hidden">--%> <div class="fondDecDat"><%if(asset.getLot()!=null && asset.getLot().getFondDecisionDate()!=null)out.print(sdf.format(asset.getLot().getFondDecisionDate()));%></div></td>
            <td>
                <%--<select class="inputFondDec" name="decisionSelect" style="width: 100%" hidden="hidden">
                    <%
                        if (fondDecisionsList != null) {
                            for (String decision : fondDecisionsList) {
                    %>
                    <option value="<%=decision%>" <%if (asset.getLot()!=null && asset.getLot().getFondDecision() != null && asset.getLot().getFondDecision().equals(decision)) {%>
                            selected="selected"<%}%>>
                        <%out.print(decision);%>
                    </option>
                    <%
                            }
                        }
                    %>
                </select>--%>
                <div class="fondDec"><%if(asset.getLot()!=null && asset.getLot().getFondDecision() != null) out.print(asset.getLot().getFondDecision());%></div>
            </td>
            <td class="fondDecNum"><%if(asset.getLot()!=null && asset.getLot().getDecisionNumber()!=null)out.print(asset.getLot().getDecisionNumber());%></td>
            <td class="acceptPrice"><div class="acceptDiv"><%if(asset.getAcceptPrice()!=null) out.print(asset.getAcceptPrice());%></div><input class="inputAcceptPrice" type="number" step="0.01" hidden="hidden"></td>
            <td class="acceptEx" title="клікніть двічі для зміни погодженої фондом біржі">
                <div class="accExDiv"><%if(asset.getLot()!=null && asset.getLot().getAcceptExchange()!=null) out.print(asset.getLot().getAcceptExchange());%></div>
                <%--<select class="inputAccEx" name="exSelect" style="width: 100%" hidden="hidden">
                    <%
                        if (allExchangeList != null) {
                            for (Exchange ex : allExchangeList) {
                    %>
                    <option value="<%=ex.getId()%>">
                        <%out.print(ex.getCompanyName());%>
                    </option>
                    <%
                            }
                        }
                    %>
                </select>--%>
            </td>
            <td class ="isNeedNewFondDec">
                <%
                if(asset.getLot()!=null && asset.getLot().isNeedNewFondDec())out.print("Так");
                else out.print("Ні");
                %>
            </td>

            <td class="planSaleDate" title="клікніть двічі для зміни планової дати"><% if(asset.getLot()!=null && asset.getLot().getPlanSaleDate()!=null)out.print(yearMonthFormat.format(asset.getLot().getPlanSaleDate()));%> </td>
            <td><% if(asset.getLot()!=null && asset.getLot().getActSignedDate()!=null)out.print(sdf.format(asset.getLot().getActSignedDate()));%> </td>
        </tr>
        <%}%>

    </table>
    </div>


</body>
</html>