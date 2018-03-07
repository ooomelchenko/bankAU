<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Звіти</title>
    <script src="js/jquery-1.11.1.js"></script>
    <link rel="stylesheet" media="screen" type="text/css" href="css/jquery-ui.css"/>
    <link rel="stylesheet" media="screen" type="text/css" href="css/jquery-ui.structure.css"/>
    <link rel="stylesheet" media="screen" type="text/css" href="css/jquery-ui.theme.css"/>
    <script type="text/javascript" src="js/jquery-ui.js"></script>
    <script>
        $(document).ready(function() {
            var dp = $('.datepicker');
            dp.datepicker({dateFormat: "yy-mm-dd", dayNamesMin: ["Нд","Пн","Вт","Ср","Чт","Пт","Сб"], monthNames: ["січень","лютий","березень", "квітень", "травень","червень",
                "липень","серпень","вересень","жовтень","листопад","грудень"] });
            $('.reportName').click(function(){
                if($(this).parent().find(".dateTd").is(":hidden"))
               $(this).parent().find(".dateTd").show();
                else
                    $(this).parent().find(".dateTd").hide();
            });
            $('#doAUreport1').click(function(){
                $.ajax({
                    url: "setReportPath",
                    type: "GET",
                    data: {reportNum: $(this).val(),
                    startDate: $('#startDate1').val(),
                    endDate: $('#endDate1').val() },
                    success: function(res){
                        if(res=='1') {
                            window.open("reportDownload");
                        }
                    }
                });
            });
            $('#downloadCrdts').click(function(){
                $.ajax({
                    url: "setReportPath",
                    type: "GET",
                    data: {reportNum: $(this).val(),
                        startDate: null,
                        endDate: null },
                    success: function(res){
                        if(res=='1') {
                            window.open("reportDownload");
                        }
                    }
                });
            });
            $('#downloadAss').click(function(){
                $.ajax({
                    url: "setReportPath",
                    type: "GET",
                    data: {reportNum: $(this).val(),
                        startDate: null,
                        endDate: null },
                    success: function(res){
                        if(res=='1') {
                            window.open("reportDownload");
                        }
                    }
                });
            });
            $('#downloadPays').click(function(){
                $.ajax({
                    url: "setReportPath",
                    type: "GET",
                    data: {reportNum: $(this).val(),
                        startDate: $('#startPayDate').val(),
                        endDate: $('#endPayDate').val() },
                    success: function(res){
                        if(res=='1') {
                            window.open("reportDownload");
                        }
                    }
                });
            });
            $('#downloadBidReport').click(function(){
                $.ajax({
                    url: "setReportPath",
                    type: "GET",
                    data: {reportNum: $(this).val(),
                        startDate: $('#minBidDate').val(),
                        endDate: $('#maxBidDate').val() },
                    success: function(res){
                        if(res=='1') {
                            window.open("reportDownload");
                        }
                    }
                });
            })
        })
    </script>
    <style type="text/css">
        body {
            background-image: url(images/reports_font.jpg);
            background-size: 100%;
        }
        body img{
            opacity: 0.5;
        }
        .reportTr:hover {
            color: darkblue;
            background-color: lightcyan;
        }
        #reportTable{
            cursor: pointer;
            background-color: whitesmoke;
            border: lawngreen;
            font-size: x-large;
            font-weight: bold;
        }
        button{
            font-weight: bold;
            font-size: large;
            cursor: pointer;
        }
    </style>
</head>
<body>
<button onclick="location.href='index'">Назад до меню</button>

<div align="center">
    <H1>
        ЗВІТИ
    </H1>
    <table id="reportTable">

        <tr class="reportTr">
            <td id="report1" class="reportName">
                Таблиця продажів
            </td>
            <td hidden="hidden" class="dateTd">
                <input id="startDate1" class="datepicker" about="початкова дата аукціонів" title="оберіть початкову дату торгів">
            </td>
            <td hidden="hidden" class="dateTd">
                <input id="endDate1" class="datepicker" about="кінцева дата аукціонів" title="оберіть кінцеву дату торгів">
            </td>
            <td hidden="hidden" class="dateTd">
                <button id="doAUreport1" value="1">завантажити</button>
            </td>
        </tr>
        <%--<tr class="reportTr">
            <td id="report2" class="reportName">
                Додаток 2.14
            </td>
            <td hidden="hidden" class="dateTd">
                <input id="startDate2" class="datepicker" about="початкова дата аукціонів" title="оберіть початкову дату торгів">
            </td>
            <td hidden="hidden" class="dateTd">
                <input id="endDate2" class="datepicker" about="кінцева дата аукціонів" title="оберіть кінцеву дату торгів">
            </td>
            <td hidden="hidden" class="dateTd">
                <button id="doAUreport2" value="2">завантажити</button>
            </td>
        </tr>--%>
        <tr class="reportTr">
            <td id="report3" class="reportName">
                Таблиця кредитів
            </td>
            <td hidden="hidden" class="dateTd">
                <button id="downloadCrdts" value="3">завантажити</button>
            </td>
        </tr>
        <tr class="reportTr">
            <td id="report4" class="reportName">
                Таблиця активів
            </td>
            <td hidden="hidden" class="dateTd">
                <button id="downloadAss" value="4">завантажити</button>
            </td>
        </tr>
        <tr class="reportTr">
            <td id="report5" class="reportName">
                Звіт по платежам
            </td>
            <td hidden="hidden" class="dateTd">
                <input id="startPayDate" class="datepicker" about="початкова дата платежів" title="оберіть початкову дату платежів">
            </td>
            <td hidden="hidden" class="dateTd">
                <input id="endPayDate" class="datepicker" about="кінцева дата платежів" title="оберіть кінцеву дату платежів">
            </td>
            <td hidden="hidden" class="dateTd">
                <button id="downloadPays" value="5">завантажити</button>
            </td>
        </tr>
        <tr class="reportTr">
            <td id="report6" class="reportName">
                Звіт по сумам торгів
            </td>
            <td hidden="hidden" class="dateTd">
                <input id="minBidDate" class="datepicker" about="початкова дата платежів" title="оберіть початкову дату платежів">
            </td>
            <td hidden="hidden" class="dateTd">
                <input id="maxBidDate" class="datepicker" about="кінцева дата платежів" title="оберіть кінцеву дату платежів">
            </td>
            <td hidden="hidden" class="dateTd">
                <button id="downloadBidReport" value="6">завантажити</button>
            </td>
        </tr>
    </table>
</div>
</body>
</html>
