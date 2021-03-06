<%@ page import="nb.domain.Bid" %>
<%@ page import="nb.domain.Customer" %>
<%@ page import="nb.domain.Exchange" %>
<%@ page import="nb.domain.Lot" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.TreeSet" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<base href="${pageContext.request.contextPath}/"/>

<%request.setCharacterEncoding("UTF-8");%>
<%response.setCharacterEncoding("UTF-8");%>

<%
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    Lot lot = (Lot) request.getAttribute("lott");
    List<String> bidStatusList = (List<String>) request.getAttribute("bidStatusList");
    List<String> statusList = (List<String>) request.getAttribute("statusList");
    List<String> bidResultList = (List<String>) request.getAttribute("bidResultList");
    List<Bid> allBidsList = (List<Bid>) request.getAttribute("allBidsList");
    Set<Bid> bidsHistoryList = (TreeSet<Bid>) request.getAttribute("bidsHistoryList");
    String userName = (String) request.getAttribute("user");
    List<String> fondDecisionsList = (List<String>) request.getAttribute("fondDecisionsList");
    List<Exchange> allExchangeList = (List<Exchange>) request.getAttribute("allExchangeList");
%>
<html>
<head>
    <title>Редагування лоту співробітником <%out.print(userName);%></title>

    <script type="text/javascript" src="resources/js/jquery-3.2.1.js"></script>
    <script type="text/javascript" src="resources/js/jquery-ui.js"></script>

    <script type="text/javascript" src="resources/bootstrap/js/bootstrap.bundle.min.js"></script>

    <link rel="stylesheet" media="screen" type="text/css" href="resources/bootstrap/css/bootstrap.css"/>

    <script type="text/javascript" src="resources/js/docUploads.js"></script>
    <script>
        $(document).ready(function () {
            var table_finance =$('#table_finance');
            var payTab = $('#table_payments');
            var table_addPay = $('#table_addPay');
            var dp = $('.datepicker');
            var lotID = $('#lotId').text();

            var button_addPay = $('#button_addPay');
            dp.datepicker({
                dateFormat: "yy-mm-dd", dayNamesMin: ["Нд", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб"],
                monthNames: ["січень", "лютий", "березень", "квітень", "травень", "червень", "липень", "серпень", "вересень", "жовтень", "листопад", "грудень"]
            });
            $('#inputFondDecDate, #inputNBUDecDate, #input_Date_signDeadline, #input_Date_prozoro').datepicker({
                dateFormat: "yy-mm-dd", dayNamesMin: ["Нд", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб"],
                monthNames: ["січень", "лютий", "березень", "квітень", "травень", "червень", "липень", "серпень", "вересень", "жовтень", "листопад", "грудень"]
            });
            function getCountSum() {
                $('.payLine').remove();
                $.ajax({
                    url: "countSumByLot",
                    method: "POST",
                    data: {lotId: lotID},
                    success: function (countSum) {
                        $('#count').text(countSum[0]);
                        $('#sum').text(countSum[1]);
                    }
                });
                $.ajax({
                    url: "paymentsSumByLot",
                    method: "POST",
                    data: {lotId: lotID},
                    success: function (paymentsSum) {
                        $('#paymentsSum').text(paymentsSum);

                        var residualToPay = parseFloat($('#factPrice').val()) - parseFloat(paymentsSum);

                        if (isNaN(Number(residualToPay))) {
                            $('#residualToPay').text($('#factPrice').val());
                        }
                        else {
                            $('#residualToPay').text(Number(residualToPay).toFixed(2));
                        }
                    }
                });
                $.ajax({
                    url: "paymentsByLot",
                    method: "POST",
                    data: {lotId: lotID},
                    success: function (payments) {

                        for (var i = 0; i < payments.length; i++) {
                            var d = new Date(payments[i].date);
                            d.setDate(d.getDate() + 1);
                            payTab.append($('<tr class="payLine" style="border: 1px solid">' +
                                '<td class="payId" style="display:none;">' + payments[i].id + '</td>' +
                                '<td>' + d.toISOString().substring(0, 10) + '</td>' +
                                '<td>' + payments[i].paySum + '</td>' +
                                '<td>' + payments[i].paySource + '</td>' +
                                '<td><img class="icon_del_pay icon_button" src="resources/css/images/delete.png" style="width: 20px; height: 20px" title="натисніть для видалення платежу"/></td>' +
                                '</tr>'));
                        }
                        $('.icon_del_pay').click(function () {
                            $.ajax({
                                url: "delPay/"+lotID+"/"+$(this).parent().parent().find('.payId').text(),
                                method: "POST",
                                success: function (res) {
                                    if (res == "1") {
                                        alert("Платіж видалено!");
                                        getCountSum();
                                    }
                                    else
                                        alert("При видаленні проплати виникла проблема!!! Зверніться до адміністратора!");
                                }
                            });
                        })
                    }
                });
            }

            getFileNames(lotID, $("#objType").val());

            getCountSum();

            function reBid(reType) {
                $.ajax({
                    url: "reBidByLot",
                    type: "GET",
                    data: {
                        lotId: lotID,
                        reqType: reType
                    },
                    success: function (res) {
                        if (res == 1) {
                        location.reload(true);
                    }
                        else {
                            alert("Якась халепа!");
                        }
                    }
                })
            }

            $('#button_reBid_type1').click(function(){
                reBid(1)
            });
            $('#button_reBid_type2').click(function(){
                reBid(2)
            });

            $('#button_del_lot').click(function () {
                if ($(this).val() == '1') {
                    $(this).val('0');
                    table_finance.css('border', "none");
                    table_finance.find('th').css('color', "#d2d7ff");
                }
                else {
                    $('#button_setSold_lot').val('0');
                    $(this).val('1');
                    //  $('#lotTh').css('backgroundColor', "#dc143c");
                    table_finance.css('border', "1px solid red");
                    table_finance.find('th').css('color', "red");
                    alert('Увага! Лот буде розформовано після підтвердження!')
                }
            });
            $('#button_setSold_lot').click(function () {

                if ($(this).val() == '1') {
                    $(this).val('0');
                    table_finance.css('border', "none");
                    table_finance.find('th').css('color', "#d2d7ff");

                }
                else {
                    if ($('#ws').val() != "Угода укладена") {
                        alert("Змініть стадію на 'Угода укладена!'");
                    }
                    else if ($('#factPrice').val() == '') {
                        alert('Введіть ціну фактичного продажу!');
                    }
                    else {

                        $('#button_del_lot').val('0');
                        $(this).val('1');
                        table_finance.css('border', "1px solid lawngreen");
                        table_finance.find('th').css('color', "lawngreen");
                        alert('Статус буде збереження після підтвердження!')
                    }
                }
            });

            $('#showCredits').click(function showCredits() {
                var tab;
                <%if (lot.getLotType()==1){%>
                tab = $('#table_assets_list');
                <% }
                if (lot.getLotType()==0){
                %>
                tab = $('#table_credits_list');
                <% } %>
                if (tab.is(':visible')) {
                    tab.hide();
                }
                else {
                    $('.tR').remove();
                    tab.show();
                    <%if (lot.getLotType()==1){%>
                    $.ajax({
                        url: "selectAssetsbyLot",
                        type: "POST",
                        data: {
                            lotId: $('#lotId').text()
                        },
                        success: function (objList) {
                            for (var i = 0; i < objList.length; i++) {
                                var approveNBU = objList[i].approveNBU ? "Так" : "Ні";
                                var neadNewFondDec = objList[i].neadNewFondDec ? "Так" : "Ні";
                                var trR = $('<tr align="center" class="tR">' +
                                    '<td class="idLot" style="display:none;">' + objList[i].id + '</td>' +
                                    '<td>' + objList[i].inn + '</td>' +
                                    '<td>' + objList[i].asset_name + '</td>' +
                                    '<td>' + objList[i].asset_descr + '</td>' +
                                    '<td>' + objList[i].region + '</td>' +
                                    '<td>' + objList[i].zb + '</td>' +
                                    '<td>' + objList[i].rv + '</td>' +
                                    '<td><div class="accPrice">' + objList[i].acceptPrice + '</div></td>' +
                                    '<td>' + objList[i].factPrice + '</td>' +
                                    '<td>' + neadNewFondDec + '</td>' +
                                    '<td>' + approveNBU + '</td>' +
                                    '<td>' + objList[i].srvId + '</td>' +
                                    '</tr>');
                                //var factPriceTd = trR.find('.factPriceTd');

                                if (objList[i].sold) {
                                    trR.css('color', "lightgreen");
                                }
                                else {
                                    var delCrButt = $('<button class="delCrdButt icon_button" value="0" title="Видалити обєкт" style="width: 30px; height: 100%">' +
                                        '<img height="15px" width="15px" src="resources/css/images/delete.png">' +
                                        '</button>').click(function () {
                                        if ($(this).val() == '0') {
                                            $(this).val('1');
                                            $(this).parent().css('color', "red");
                                            $(this).parent().attr('title', "Об'єкт буде видалено зі списку!")
                                        }
                                        else {
                                            $(this).val('0');
                                            $(this).parent().css('color', '#bdc2e7');
                                            $(this).parent().attr('title', "")
                                        }
                                    });
                                    trR.append(delCrButt);
                                }
                                tab.append(trR);
                            }
                            $('.accPrice').dblclick(function(){
                                var accPrice =$(this).text();
                                var inAccPrice = $('<input class="inAccPrice" type="number" step="0.01">');
                                inAccPrice.val(accPrice);
                                $(this).hide();
                                $(this).parent().append(inAccPrice);
                            });
                        }
                    });
                    <% } %>
                    <%if (lot.getLotType()==0){%>
                    $.ajax({
                        url: "selectCreditsLot",
                        type: "POST",
                        data: {
                            lotId: $('#lotId').text()
                        },
                        success: function (objList) {
                            for (var i = 0; i < objList.length; i++) {
                                var neadNewFondDec = objList[i].neadNewFondDec ? "Так" : "Ні";
                                var trR = $('<tr align="center" class="tR">' +
                                    '<td class="idLot" style="display:none;">' + objList[i].id + '</td>' +
                                    '<td class="nd">' + objList[i].nd + '</td>' +
                                    '<td>' + objList[i].inn + '</td>' +
                                    '<td>' + objList[i].contractNum + '</td>' +
                                    '<td>' + objList[i].fio + '</td>' +
                                    '<td>' + objList[i].product + '</td>' +
                                    '<td>' + objList[i].region + '</td>' +
                                    '<td>' + objList[i].zb + '</td>' +
                                    '<td>' + objList[i].rv + '</td>' +
                                    '<td ><div class="accPrice">' + objList[i].acceptPrice + '</div></td>' +
                                    '<td>' + objList[i].factPrice + '</td>' +
                                    '<td>' + neadNewFondDec + '</td>' +
                                    '<td>' + objList[i].nbuPladge + '</td>' +
                                    '</tr>');
                                //var factPriceTd = trR.find('.factPriceTd');

                                if (objList[i].isSold) {
                                    trR.css('background-color', "lightgreen");
                                }
                                else {
                                    var delCrButt = $('<button class="delCrdButt icon_button" value="0" title="Видалити обєкт" style="width: 30px; height: 100%">' +
                                        '<img height="15px" width="15px" src="resources/css/images/delete.png">' +
                                        '</button>').click(function () {
                                        if ($(this).val() == '0') {
                                            $(this).val('1');
                                            $(this).parent().css('color', "red");
                                            $(this).parent().attr('title', "Об'єкт буде видалено зі списку!")
                                        }
                                        else {
                                            $(this).val('0');
                                            $(this).parent().css('color', '#bdc2e7');
                                            $(this).parent().attr('title', "")
                                        }
                                    });
                                    trR.append(delCrButt);
                                }
                                tab.append(trR);
                            }
                            $('.accPrice').dblclick(function(){
                                var accPrice =$(this).text();
                                var inAccPrice = $('<input class="inAccPrice" type="number" step="0.01">');
                                inAccPrice.val(accPrice);
                                $(this).hide();
                                $(this).parent().append(inAccPrice);
                            });
                        }
                    });
                    <% } %>
                    $('#div_obj_list').append(tab);
                }

            });

            $('#button_Accept').click(function () {
                if ($('#button_del_lot').val() == '0') {

                    $('.delCrdButt').each(function () {
                        if ($(this).val() == '1') {
                            var idL = $(this).parent().children().first().text();
                            $.ajax({
                                url: "delObjectFromLot",
                                type: 'POST',
                                data: {
                                    objId: idL,
                                    lotId: $('#lotId').text()
                                },
                                success(res){
                                    if (res == "0")
                                        alert("Об'єкт не видалено!");
                                }
                            })
                        }
                    });
                    $('.inAccPrice').each(function(){
                        $.ajax({
                            url: "changeObjAccPrice",
                            type: 'POST',
                            data:{
                                objId: $(this).parent().parent().children().first().text(),
                                objAccPrice: $(this).val(),
                                lotId: $('#lotId').text()
                            },
                            success: function(res){
                                if (res == "0")
                                    alert("Прийняту ФГВФО ціну не змінено!!");
                            }
                        })
                    });

                    var bidScenario=0 ;
                    if ($('#input_bid_scenario').is(":checked"))
                        bidScenario=1;
                    $.ajax({
                        url: "changeLotParams",
                        method: "POST",
                        data: {
                            lotId: $('#lotId').text(),
                            workStage: $('#ws').val(),
                            comment: $('#input_comment').val(),
                            bidStage: $('#bidst').val(),
                            lotNum: $('#lotNum').val(),
                            resultStatus: $('#bidResultSt').val(),
                            customer: $('#customerName').val(),
                            customerInn: $('#customerInn').val(),
                            firstPrice: $('#firstPrice').val(),
                            startPrice: $('#startPrice').val(),
                            factPrice: $('#factPrice').val(),
                            isSold: $('#button_setSold_lot').val(),
                            selectedBidId: $('#bidSelector').val(),
                            countOfParticipants: $('#countOfPart').val(),
                            bidScenario: bidScenario
                        },
                        success: function (rez) {
                            if (rez == "1") {
                                alert("Лот змінено!");
                                location.reload(true);
                            }
                            else alert("Лот не змінено!")
                        }
                    });
                }
                else {
                    $.ajax({
                        url: "lotDel",
                        method: "POST",
                        data: {lotId: lotID},
                        success: function(rez){
                            if (rez == 1) {
                                alert("Лот видалено!");
                                window.close();
                            }
                            else alert("Лот не видалено!")
                        }
                    });
                }
            });

            $('#icon_add_document').click(function () {
                var addDocForm = $("#addDocForm");
                if (addDocForm.is(':visible')) {
                    addDocForm.hide();
                }
                else {
                    addDocForm.show();
                }
            });
            $('#icon_contract').click(function(){
                window.open("contract/"+lotID);
            });

            $('#icon_excel_download').click(function () {
                window.open("unitsByLot/"+lotID);
            });

            $('#icon_add_pay').click(function(){

             if($('#factPrice').val()==""){
                    alert("Спочатку введіть будь-ласка ціну продажу!")
                }
                else if(button_addPay.val()=='0'){
                    table_addPay.show();
                    button_addPay.val('1');
                }
                else{
                    table_addPay.hide();
                    button_addPay.val('0');
                }
            });

            button_addPay.click(function adder() {
                var payTd = $('.payTd');
                var payDate = $('#payDate');
                var pay = $('#pay');
                var paySource = $('#paySource');
                    if (isNaN(parseFloat(pay.val()))) {
                        alert("Введіть будь-ласка суму платежу у коректному форматі!")
                    }
                    else {
                        $.ajax({
                            url: "addPayToLot/"+lotID,
                            method: "POST",
                            data: {
                                payDate: payDate.val(),
                                pay: pay.val(),
                                paySource: paySource.val()
                            },
                            success: function(rez){
                                if (rez == "1") {
                                    alert("Платіж додано!");
                                    getCountSum();
                                    payDate.val(null);
                                    pay.val(null);
                                    table_addPay.hide();
                                }
                                else alert("Платіж НЕ ДОДАНО!")
                            }
                        });
                    }
            });

            $("#bidDate").click(function () {
                var bidHistoryTr = $(".bidHistoryTr");
                if (bidHistoryTr.is(":hidden"))
                    bidHistoryTr.show();
                else
                    bidHistoryTr.hide();
            });

            var redactButton = $('#redactButton');
            redactButton.click(function () {
                if (redactButton.val() == "0") {
                    redactButton.val(1);
                    redactButton.text("Прийняти");
                    $('#fdChoose').show();
                    $('#fdCurrent').hide();
                }
                else {
                    redactButton.val(0);
                    redactButton.text("Додати рішення");
                    $.ajax({
                        url: "changeFondDec",
                        type: "POST",
                        data: {
                            lotId: <%out.print(lot.getId());%>,
                            fondDecDate: $('#inputFondDecDate').val(),
                            fondDec: $('#inputFondDec').val(),
                            decNum: $('#inputFondDecNum').val()
                        },
                        success: function (res) {
                            if (res == 1) {
                                alert("Зміни прийнято!");
                                location.reload(true);
                            }
                        }
                    });
                }
            });

            var redactNBUButton = $('#redactNBUButton');
            redactNBUButton.click(function () {
                if (redactNBUButton.val() == "0") {
                    redactNBUButton.val(1);
                    redactNBUButton.text("Прийняти");
                    $('#nbuChoose').show();
                    $('#nbuCurrent').hide();
                }
                else {
                    redactNBUButton.val(0);
                    redactNBUButton.text("Додати рішення");
                    $.ajax({
                        url: "changeNBUDec",
                        type: "POST",
                        data: {
                            lotId: <%out.print(lot.getId());%>,
                            NBUDecDate: $('#inputNBUDecDate').val(),
                            NBUDec: $('#inputNBUDec').val(),
                            decNum: $('#inputNBUDecNum').val()
                        },
                        success: function (res) {
                            if (res == 1) {
                                alert("Зміни прийнято!");
                                location.reload(true);
                            }
                        }
                    });
                }
            });

            $("#okButton").click(function () {
                $.ajax({
                    url: "setAcceptEx",
                    type: "POST",
                    data: {
                        lotId: $('#lotId').text(),
                        acceptEx: $('#inputAccEx').val()
                    },
                    success: function (result) {
                        if (result == "1") {
                            alert("затверджена біржа змінена!");
                            location.reload(true);
                        }
                        else alert("данні не внесені!");
                    }
                });
            });
            $('.acceptEx').dblclick(function () {
                var accExChoose = $("#accExChoose");
                var accExCurrent = $('#accExCurrent');
                var okButton = $("#okButton");
                if (accExChoose.is(":hidden")) {
                    accExChoose.show();
                    accExCurrent.hide();
                }
                else {
                    accExChoose.hide();
                    accExCurrent.show();
                }
            });
            $('#firstPriceTd').dblclick(function(){
                $(this).find('div').remove();
                $(this).find('input').show();
            });

            $('#customerInn').dblclick(function () {
                window.open("customer/"+$('#customerInn').val());
            });

            div_customer = $('#div_customer');

            $('#td_customer').dblclick(function () {

                if (div_customer.is(":hidden")) {
                    div_customer.show();
                }
                else {
                    div_customer.hide();
                }
            });
            $('#button_customer_update').click(function () {
                $.ajax({
                    url: lotID+"/customer/update",
                    type: "POST",
                    data: {
                        inn: $('#input_customer_inn').val(),
                        name: $('#input_customer_name').val(),
                        middleName: $('#input_customer_middleName').val(),
                        lastName: $('#input_customer_lastName').val(),
                        isMerried: $('#checkbox_customer_isMerried').is(':checked'),
                        type: $('#select_customer_type').val(),
                        legalType: $('#select_legal_type').val()
                    },
                    success: function (customer) {
                        $('#td_customer').text(customer.customerName+" "+customer.middleName+" "+customer.lastName+" "+customer.customerInn);
                        div_customer.hide();
                    }
                });
            });
            $('#button_customer_remove').click(function () {
                $.ajax({
                    url: lotID+"/customer/delete",
                    type: "POST",
                    success: function (result) {
                        alert(result);
                        $('#td_customer').empty();
                        $('.customer_field_input').each(function () {
                            $(this).empty();
                        });
                        div_customer.hide();
                    }
                });
            });

            $('#button_set_dates').click(function () {
                $.ajax({
                    url: lotID+"/setDates",
                    type: "POST",
                    data: {
                        dateProzoro: $('#input_Date_prozoro').val(),
                        dateDeadline: $('#input_Date_signDeadline').val()
                    },
                    success: function () {
                        alert("Успішно додано!");
                    }
                });
            });

            $('#input_customer_inn').dblclick(function () {
                $.ajax({
                    url: "customer/"+$(this).val(),
                    type: "POST",
                    success: function (customer) {
                        if(customer==""){
                            alert("Клієнта не знайдено");
                        }
                            else{

                            $('#input_customer_lastName').val(customer.lastName);
                            $('#input_customer_middleName').val(customer.middleName);
                            $('#input_customer_name').val(customer.customerName);
                            if(customer.merried){
                                $('#checkbox_customer_isMerried').prop( "checked", true );
                            }
                            else
                                $('#checkbox_customer_isMerried').prop( "checked", false );
                            
                            $('#select_legal_type').find('option').each(function () {
                                $(this).prop( "selected", false );
                                if( $(this).val()===customer.legalType ){
                                    $(this).prop( "selected", true )
                                }
                            });
                            $('#select_customer_type').find('option').each(function () {
                                $(this).prop( "selected", false );
                                if( $(this).val()===customer.type ){
                                    $(this).prop( "selected", true )
                                }
                            })

                        }

                    }
                });
            });


            <%if (lot.getItSold()){%>
            $('#div_mainButtons').hide();
            $('#factPrice').hide();
            $('#factPriceTd').append(<%out.print(lot.getFactPrice());%>);
            <%}%>
        })
    </script>

    <link rel="stylesheet" media="screen" type="text/css" href="resources/css/general_style.css"/>
    <link rel="stylesheet" media="screen" type="text/css" href="resources/css/jquery-ui.css"/>
    <link rel="stylesheet" media="screen" type="text/css" href="resources/css/jquery-ui.structure.css"/>
    <link rel="stylesheet" media="screen" type="text/css" href="resources/css/jquery-ui.theme.css"/>
    <style>
        input{
            width: 100%;
        }
        table{
            border-collapse: collapse;
        }
        .tR:hover{
            cursor: pointer;
            background-color: #252F48;
            color: ghostwhite;
        }
        #div_b1{
            margin-top: -40px;
            position: static;
            padding: 0;
        }
        .div_menu_block_header{
            vertical-align: center;
            height: 25px;
            font-size: larger;
            font-weight: bold;
            color: ghostwhite;
            text-align: center;
            background-color: #37415d;
        }

        #div_documents table, #div_payments table, #div_bid table{
            font-size: small;
            align-content: center;
            width: 100%;
        }
        #div_payments tr:hover{
            cursor: pointer;
            background-color: #37415d;
            color: ghostwhite;
        }

        #div_bid table tr {
            color: orangered;
        }
        #finInfoTr{
            font-weight: bold
        }

        #firstPriceTd, #bidDate, .bidHistoryTr, #bidTd {
            cursor:pointer;
        }
        #bidDate:hover{
            background-color: white;
            font-weight: bold
        }
        .bidHistoryTr{
            background-color: white;
        }
        .bidHistoryTr:hover{
            background-color: white;
        }

        #button_del_lot{
            width: 100px;
            height: 50px;
        }

        #button_setSold_lot{
            width: 100px;
            height: 50px;
        }


        .table_decision{
            cursor: pointer;
            font-weight: bold;
            text-align: center;
            width: 100%;
        }
        .table_decision td{
            width: 33%;
        }
        .table_decision td:hover{
            background-color: #37415d;
            color: ghostwhite;
        }
        .table_decision th{
            border-top: 1px solid ghostwhite;
            border-bottom: 1px solid ghostwhite;
            color: ghostwhite;
        }
        button{
            cursor:pointer;
        }
        select, option{
            width: 100%;
            font-weight: bold;
        }

        #div_comments {
            width: 450px;
        }
        #div_comments input{
            font-size: 14px;
            height: 40px;
        }

        #showCredits{
            cursor:pointer;
            height: 30px;
            font-size: 16px;
            font-weight: bold;
        }

        #div_b2{
            background-color: white;
        }
        #table_finance {
            font-size: small;
        }
        #table_finance td{
            border: solid 1px #00ffff;
        }
        #table_finance th{
            background-color: #37415d;
            color: #00ffff;
            border: solid 1px;
        }
        .table_obj_list{
            width: 100%;
            font-size: small;

        }
        #table_assets_list{
            border: 1px solid lightgreen;
         }
        #table_credits_list{
            border: 1px solid deepskyblue;
        }
        .row{
            padding: 0 !important;
            margin: 0 !important;
        }

    </style>
</head>
<body>

<header>
    <div id="div_left_side" class="div_header_additions">
        <div id="div_crud_buttons">
            <div>
                <button id="button_del_lot" class="btn-danger" value="0">Видалити лот</button>
            </div>
            <div>
                <button id="button_setSold_lot" class="btn-light" value="0">Акт підписано</button>
            </div>
            <div id="div_mainButtons" class="col-lg-1">
                <button class="btn btn-success" id="button_Accept" title="натисніть для збереження змін">
                    ПІДТВЕРДИТИ
                </button>
            </div>
        </div>
    </div>
    <div id="div_sheet_header">
        <h1>ЛОТ №<b id="lotId"><%out.print(lot.getId());%></b></h1>
    </div>
    <div id="div_right_side" class="div_header_additions">
        <div id="div_comments">
            <input id="input_comment" value="<%if(lot.getComment()!=null)out.print(lot.getComment());%>" placeholder="Коментар"
                   title="Введіть коментарі стосовно особливостей лоту">
        </div>
    </div>
</header>

<div id="div_b1" class="jumbotron-fluid">
    <div class="row border-bottom" >

        <div id="div_bid" class="col-md-4 border-right" >
            <table id="table_bid">
                <tr>
                    <th>Біржа</th>
                    <td id="bidTd" title="клікніть двічі для зміни торгів">
                        <select id="bidSelector" >
                            <option value="0">
                            </option>
                            <% for (Bid bid : allBidsList) { %>
                            <option value="<%out.print(bid.getId());%>"<%if (lot.getBid() != null && lot.getBid().getId().equals(bid.getId())) {%>
                                    selected="selected"<%
                                    ;
                                }
                            %>>
                                <%
                                    if(bid.getBidDate()!=null&&bid.getExchange().getCompanyName()!=null) {
                                        out.print(sdf.format(bid.getBidDate()) + " - " + bid.getExchange().getCompanyName()+" ("+bid.getNewspaper()+")");
                                    }
                                %>
                            </option>
                            <%
                                }
                            %>
                        </select>
                    </td>
                </tr>
                <tr>
                    <th>Стадія</th>
                    <td>
                        <select id="ws">
                            <% for (String ws : statusList) {%>
                            <option value="<%out.print(ws);%>" <%if (ws.equals(lot.getWorkStage())) {%>
                                    selected="selected" <%
                                    ;
                                }
                            %>>
                                <%
                                    out.print(ws);
                                %>
                            </option>
                            <%
                                }
                            %>
                        </select>
                    </td>
                </tr>
                <tr>
                    <th>Торги</th>
                    <td>
                        <select id="bidst">
                            <% for (String bidst : bidStatusList) {
                            %>
                            <option value="<%out.print(bidst);%>" <%if (bidst.equals(lot.getBidStage())) {%>
                                    selected="selected" <%
                                    ;
                                }
                            %>>
                                <%
                                    out.print(bidst);
                                %>
                            </option>
                            <%
                                }
                            %>
                        </select>
                    </td>
                </tr>
                <tr>
                    <th>Статус аукціону</th>
                    <td>
                        <select id="bidResultSt">
                            <% for (String resultStatus : bidResultList) {
                            %>
                            <option value="<%out.print(resultStatus);%>" <%if (resultStatus.equals(lot.getStatus())) {%>
                                    selected="selected" <%
                                    ;
                                }
                            %>>
                                <%
                                    out.print(resultStatus);
                                %>
                            </option>
                            <%
                                }
                            %>
                        </select>
                    </td>
                </tr>
                <tr>
                    <th>Кінець реєстрації</th>
                    <td>
                        <%
                            if (lot.getBid() != null && lot.getBid().getRegistrEndDate() != null)
                                out.print(sdf.format(lot.getBid().getRegistrEndDate()));
                        %>
                    </td>
                </tr>
                <tr>
                    <th>Дата торгів</th>
                    <td id="bidDate" title="натисніть для перегляду історії">
                        <%
                            if (lot.getBid() != null && lot.getBid().getBidDate() != null)
                                out.print(sdf.format(lot.getBid().getBidDate()));
                        %>
                    </td>
                </tr>

                <%for (Bid bid : bidsHistoryList) {%>
                <tr class="bidHistoryTr" style="display:none;" title="Історія попередніх торгів">
                    <td align="left">
                        <%out.print(bid.getExchange().getCompanyName());%>
                    </td>
                    <td>
                        <%if (bid.getBidDate() != null) out.print(sdf.format(bid.getBidDate()));%>
                    </td>
                </tr>
                <%}%>
            </table>
            <table>
                <tr>
                    <td width="30%">
                        <label>Голландський тип</label>
                    </td>
                    <td width="10%" >
                        <%if(lot.getBidScenario()==1){%>
                        <input type="checkbox" id="input_bid_scenario" checked="checked" title="оберіть голландський сценарій торгів">
                        <%}
                        else{%>
                        <input type="checkbox" id="input_bid_scenario" title="оберіть голландський сценарій торгів">
                        <%}%>
                    </td>
                    <td width="30%"></td>
                    <td width="30%" >
                        <button id="button_reBid_type1" class="button_reBid btn-sm btn-light " value="1" title="Повторні торги на біржі">Повторні торги</button>
                    </td>
                </tr>
            </table>
        </div>

        <div id="div_decisions" class="col-md-4 border-right">
            <div>
                <div class="div_menu_block_header">
                    Погодження продажу ФГВФО
                </div>
                <div>
                    <table id="table_fond_decision" class="table_decision">
                        <tr id="accExCurrent" title="клікніть двічі для зміни погодженої фондом біржі">
                            <td colspan="3" class="acceptEx">
                                <%out.print(lot.getAcceptExchange());%>
                            </td>
                        </tr>
                        <tr id="accExChoose" style="display:none;">
                            <td colspan="2">
                                <select id="inputAccEx" name="exSelect">
                                    <option value="0">

                                    </option>
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
                                </select>
                            </td>
                            <td>
                                <button id='okButton' style="width: 100%">ok</button>
                            </td>
                        </tr>
                        <tr id="fdCurrent">
                            <td><%if (lot.getFondDecisionDate() != null) out.print(sdf.format(lot.getFondDecisionDate()));%></td>
                            <td><%if (lot.getFondDecision() != null) out.print(lot.getFondDecision());%></td>
                            <td><%if (lot.getDecisionNumber() != null) out.print(lot.getDecisionNumber());%></td>
                        </tr>
                        <tr id="fdChoose" style="display: none">
                            <td>
                                <input id="inputFondDecDate" title="Дата прийняття рішення" placeholder="дата рішення">
                            </td>
                            <td>
                                <select id="inputFondDec" name="decisionSelect" title="Рівень прийняття рішення">
                                    <%
                                        if (fondDecisionsList != null) {
                                            for (String decision : fondDecisionsList) {
                                    %>
                                    <option value="<%=decision%>">
                                        <%out.print(decision);%>
                                    </option>
                                    <%
                                            }
                                        }
                                    %>
                                </select>
                            </td>
                            <td>
                                <input id="inputFondDecNum" type="text" title="Номер рішення" placeholder="номер рішення">
                            </td>
                        </tr>
                        <tr>
                            <td title="Необхідне перепогодження ФГВФО" width="45%">
                                <button id="button_reBid_type2" class="button_reBid btn-light text-danger" value="2" style="width: 100%" title="Необхідне перепогодження ФГВФО">
                                    Необхідне перепогодження
                                </button>
                            </td>
                            <td style="background-color: transparent" width="10%"></td>
                            <td width="45%">
                                <button id="redactButton" class="btn-light" value="0" style="width: 100%" title="Додати актуальне рішення ФГВФО">
                                    Додати рішення
                                </button>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
            <%if(lot.getLotType()==0){%>
            <div>
                <div class="div_menu_block_header">
                    Погодження НБУ
                </div>
                <div>
                    <table id="table_nbu_decision" class="table_decision">
                        <tr id="nbuCurrent">
                            <td><%if (lot.getNbuDecisionDate() != null) out.print(sdf.format(lot.getNbuDecisionDate()));%></td>
                            <td><%if (lot.getNbuDecision() != null) out.print(lot.getNbuDecision());%></td>
                            <td><%if (lot.getNbuDecisionNumber() != null) out.print(lot.getNbuDecisionNumber());%></td>
                        </tr>
                        <tr id="nbuChoose" style="display:none;">
                            <td>
                                <input id="inputNBUDecDate" title="Дата прийняття рішення" placeholder="дата рішення">
                            </td>
                            <td>
                                <select id="inputNBUDec" name="decisionSelect" title="Рівень прийняття рішення">
                                    <option value="погоджено">
                                        на погодженні
                                    </option>
                                    <option value="погоджено">
                                        не погоджено
                                    </option>
                                    <option value="погоджено">
                                        погоджено
                                    </option>
                                </select>
                            </td>
                            <td>
                                <input id="inputNBUDecNum" type="text" title="Номер рішення" placeholder="номер рішення">
                            </td>
                        </tr>
                        <tr>
                            <td style="background-color: transparent"></td>
                            <td style="background-color: transparent"></td>
                            <td>
                                <button id="redactNBUButton" value="0" style="width: 100%; color: darkblue" title="Додати актуальне рішення НБУ">
                                    Додати рішення
                                </button>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
            <%}%>
            <hr class="my-4">

            <div class="row text-danger" style="position: absolute; bottom: 0;">

                <div class="col-4"><b>Дата прозоро</b></div>
                <div class="col-5">
                    <input id="input_Date_prozoro" title="Дата прозоро" value="<%if (lot.getProzoroDate() != null) out.print(sdf.format(lot.getProzoroDate()));%>">
                </div>

                <div class="col-4"><b>Кінцева дата</b></div>
                <div class="col-5">
                    <input id="input_Date_signDeadline" title="Крайня дата підписання" value="<%if (lot.getDeadlineDate() != null) out.print(sdf.format(lot.getDeadlineDate()));%>">
                </div>

                <div class="col-3">
                    <button id="button_set_dates" class="btn-success col-12">зберегти</button>
                </div>

            </div>
        </div>

        <div id="div_payments" class="col-md-2 border-right">
            <div class="div_menu_block_header">
                Платежі по лоту <img id="icon_add_pay" class="icon_button" src="resources/css/images/add_payment.png" title="Додати платіж" style="width: 20px; height: 20px">
            </div>
            <div>
                <table id="table_payments" ><%--заповнюється скриптом--%>
                </table>
                <table width="100%" id="table_addPay" style="display:none;">
                    <tr>
                        <th class="payTd" >
                            <input id="payDate" class="datepicker" placeholder="дата платежу" title="введіть дату платежу">
                        </th>
                        <th class="payTd" >
                            <input id="pay" type="number" step="0.01" placeholder="сума, грн" title="введіть суму платежу">
                        </th>
                        <th class="payTd" >
                            <select id="paySource" style="width: 100px" title="оберіть джерело надходження коштів">
                                <option value="Біржа">
                                    Біржа
                                </option>
                                <option value="Покупець">
                                    Покупець
                                </option>
                            </select>
                        </th>
                    </tr>
                    <tr>
                        <td colspan="3" >
                            <button id="button_addPay" style="width: 100%;" title="Додати платіж" value="0">ок</button>
                        </td>
                    </tr>
                </table>
            </div>
        </div>

        <div id="div_documents" class="col-md-2 border-right">
            <div class="div_menu_block_header">
                <img id="icon_contract" class="icon_button" src="resources/css/images/contract_icon.png" style="width: 20px; height: 20px" title="завантажити договір">
                Документи
                <img id="icon_add_document" class="icon_button" src="resources/css/images/add_document.png" style="width: 20px; height: 20px" title="додати документ">
            </div>
            <div>
                <table id="table_documents_scan" border="1">

                </table>
            </div>
            <div id="addDocForm" style="display:none;">
                <form method="POST" action="uploadFile" enctype="multipart/form-data" lang="utf8">
                    <input type="text" id="objType" name="objType" value="lot" style="display:none;">
                    <input type="text" name="objId" value="<%out.print(lot.getId());%>" style="display:none;">
                    Обрати документ для збереження:
                    <br/>
                    <input align="center" type="file" name="file" title="натисніть для обрання файлу"><br/>
                    <input align="center" type="submit" value="Зберегти" title="натисніть щоб зберегти файл">
                </form>
            </div>
        </div>

    </div>
</div>

<div id="div_b2" class="jumbotron-fluid">

    <div id="div_finance" class="col-lg-12 row">

            <table id="table_finance" class="col-md-12" style="margin: 5px">
                <tr>
                    <th>К-ть об'єктів</th>
                    <td id="count" align="center"></td>
                    <th>№ лоту в публікації</th>
                    <th>К-ть учасників</th>
                    <th>Початкова ціна</th>
                    <th>Дисконт</th>
                    <th>Стартова ціна аукціону</th>
                    <th>Ціна продажу, грн.</th>
                    <th>Залишок до сплати, грн.</th>
                    <th>Фактично сплачено, грн.</th>
                    <th>Покупець</th> <%--<img id="img_new_customer" class="icon_button" src="resources/images/plus_green.png"
                                             style="width: 15px; height: 15px" title="додати нового покупця">--%>
                </tr>
                <tr id="finInfoTr">
                    <th>Оціночна вартість, грн</th>
                    <td id="sum" align="center"></td>
                    <td><input id="lotNum" type="text" value="<%if(lot.getLotNum()!=null)out.print(lot.getLotNum());%>">
                    </td>
                    <td><input id="countOfPart" type="number" value="<%out.print(lot.getCountOfParticipants());%>"></td>
                    <td id="firstPriceTd" align="center" title="Клікніть двічі для редагування">
                        <div><%=lot.getFirstStartPrice()%>
                        </div>
                        <input id="firstPrice" type="number" style="display:none;" step="0.01"
                               title="Початкова ціна лоту без дисконту" value="<%out.print(lot.getFirstStartPrice());%>">
                    </td>
                    <td id="discount" align="center" title="Дисконт відносно початкової ціни на перших торгах">
                        <%
                            if (lot.getStartPrice() != null && lot.getFirstStartPrice() != null)
                                out.print((new BigDecimal(1).subtract(lot.getStartPrice().divide(lot.getFirstStartPrice(), 4, BigDecimal.ROUND_HALF_UP))).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP) + "%");
                        %>
                    </td>
                    <td id="startPriceTd" align="center">
                        <input id="startPrice" type="number" step="0.01" title="Ціна лоту на актуальному аукціоні"
                               value="<%out.print(lot.getStartPrice());%>">
                    </td>
                    <td id="factPriceTd" align="center">
                        <input id="factPrice" type="number" step="0.01" title="Ціна за яку фактично продано лот"
                               value=<%out.print(lot.getFactPrice());%>>
                    </td>
                    <td id="residualToPay" align="center" title="Залишок до сплати (ціна продажу-сплчено)">
                    </td>
                    <td id="paymentsSum" datatype="number" align="center">
                    </td>
                     <td id="td_customer" title="Клікніть двічі для редагування">

                         <c:out value="${requestScope.lott.getCustomer().shortDescription()}"/>
                         <%--<input id="customerName" type="text" placeholder="ФІО" title="ФІО покупця" value='<%if(lot.getCustomerName()!=null)out.print(lot.getCustomerName());%>'>
                         <input id="customerInn" type="number" placeholder="ІНН" title="ІНН покупця" value='<%if(lot.getCustomerInn()!=0)out.print(lot.getCustomerInn());%>'>--%>
                     </td>
                </tr>
            </table>

    </div>

    <div id="div_customer" class="col-md-12" style="display:none;">

        <table class="table table-hover ">
            <tr>
                <th>Форма власності</th>
                <th>ІНН покупця</th>
                <th>Прізвище</th>
                <th>Ім'я</th>
                <th>По-батькові</th>
                <th>Одружений(на)</th>
                <th>Підписант</th>
                <th>
                    <button id="button_customer_remove" type="button" class="btn-sm btn-danger">Видалити</button>
                </th>
            </tr>
            <tr>
                <td>
                    <select id="select_legal_type">

                        <% for(Customer.LegalType type : Customer.LegalType.values()){
                            out.print(type.getUkrType());
                        %>

                        <option value="<%out.print(type.name());%>" <%if (lot.getCustomer()!=null&&lot.getCustomer().getLegalType().name().equals(type.name())) out.print("selected=\"selected\"");%> >
                            <%out.print(type.getUkrType());%>
                        </option>
                        <%}%>
                    </select>
                </td>
                <td title="клікніть двічі для пошуку клієнта в базі">
                    <input class="customer_field_input" id="input_customer_inn" type="number" min="0" value="<c:out value="${requestScope.lott.getCustomer().getCustomerInn()}"/>">
                </td>
                <td>
                    <input class="customer_field_input" id="input_customer_lastName" type="text" value="<c:out value="${requestScope.lott.getCustomer().lastName}"/>">
                </td>
                <td>
                    <input class="customer_field_input" id="input_customer_name" type="text" value="<c:out value="${requestScope.lott.getCustomer().customerName}"/>">
                </td>
                <td>
                    <input class="customer_field_input" id="input_customer_middleName" type="text" value="<c:out value="${requestScope.lott.getCustomer().middleName}"/>">
                </td>
                <td>
                    <input id="checkbox_customer_isMerried" type="checkbox"
                           <c:if test="${requestScope.lott.getCustomer().isMerried()==true}">checked="checked"</c:if>>
                </td>
                <td>
                    <select id="select_customer_type">

                        <% for(Customer.SubscriberType type : Customer.SubscriberType.values()){
                            out.print(type.getUkrType());
                        %>

                        <option value="<%out.print(type.name());%>" <%if (lot.getCustomer()!=null&&lot.getCustomer().getType().name().equals(type.name())) out.print("selected=\"selected\"");%> >
                            <%out.print(type.getUkrType());%>
                        </option>
                        <%}%>
                    </select>
                </td>
                <td>
                    <button id="button_customer_update" type="button" class="btn-sm btn-success">OK</button>
                </td>
            </tr>

        </table>

    </div>

</div>

<hr class="my-4">

<div class="view jumbotron-fluid" id="div_obj_list" align="center">
    <table align="center">
        <tr>
            <td align="center">
                <button id="showCredits" class="btn-info" title="Показати/Приховати список об'єктів в лоті">Список об'єктів</button>
            </td>
            <td align="right" title="натисніть для збереження в форматі .xls">
                <img id="icon_excel_download" class="icon_button" src="resources/css/images/excel.jpg" width="30px" height="30px"/>
            </td>
        </tr>
    </table>
    <table id="table_assets_list" class="table_obj_list" style="display:none;" border="green" style="border: 1px solid lawngreen;">
        <tr style="color: lawngreen; background-color: #37415d">
            <th style="display:none;">ID</th>
            <th>Інвентарний №</th>
            <th>Назва активу</th>
            <th>Опис обєкту</th>
            <th>Регіон</th>
            <th>Балансова вартість</th>
            <th>Оціночна вартість, грн.</th>
            <th>Затверджена ФГВФО ціна, грн.</th>
            <th>Ціна продажу, грн.</th>
            <th>Необхідно перепогодити</th>
            <th>В заставі НБУ</th>
            <th>SRV_ID</th>
        </tr>
    </table>
    <table id="table_credits_list" class="table_obj_list" style="display:none;" border="blue">
        <tr style="color: deepskyblue; background-color: #37415d">
            <th style="display:none;">ID</th>
            <th>ID_Bars</th>
            <th>ІНН</th>
            <th>N договору</th>
            <th>Назва</th>
            <th>Опис обєкту</th>
            <th>Регіон</th>
            <th>Балансова вартість</th>
            <th>Оціночна вартість, грн.</th>
            <th>Затверджена ФГВФО ціна, грн.</th>
            <th>Ціна продажу, грн.</th>
            <th>Необхідно перепогодити</th>
            <th>В заставі НБУ</th>
        </tr>
    </table>
</div>

</body>
</html>