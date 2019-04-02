<%@ page contentType="text/html;charset=UTF-8" %>
<base href="${pageContext.request.contextPath}/"/>
<html>
<head>
    <title>Звіти</title>

    <script src="resources/js/jquery-3.2.1.js"></script>
    <script type="text/javascript" src="resources/js/jquery-ui.js"></script>
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
                var startDate = $('#startDate1').val();
                var endDate = $('#endDate1').val();
                window.open("getReport/1/"+startDate+"/"+endDate);
            });
            $('#downloadCrdts').click(function(){
                var startDate = $('#startDate3').val();
                var endDate = $('#endDate3').val();
                window.open("getReport/3/"+startDate+"/"+endDate);
            });
            $('#downloadAss').click(function(){
                window.open("getReport/4/"+null+"/"+null);
            });
            $('#downloadPays').click(function(){
                var startDate = $('#startPayDate').val();
                var endDate = $('#endPayDate').val();
                window.open("getReport/5/"+startDate+"/"+endDate);
            });
            $('#downloadBidReport').click(function(){
                var startDate = $('#minBidDate').val();
                var endDate = $('#maxBidDate').val();
                window.open("getReport/6/"+startDate+"/"+endDate);
            });
            $('#downloadAudit').click(function(){
                var startDate = $('#minAuditDate').val();
                var endDate = $('#maxAuditDate').val();
                window.open("getReport/7/"+startDate+"/"+endDate);
            });
            $('#downloadReestr').click(function(){
                var startDate = $('#startDate8').val();
                var endDate = $('#endDate8').val();
                window.open("getReport/8/"+startDate+"/"+endDate);
            });
        })
    </script>

    <link rel="stylesheet" type="text/css" href="resources/css/general_style.css" >
    <link rel="stylesheet" media="screen" type="text/css" href="resources/css/jquery-ui.css"/>
    <link rel="stylesheet" media="screen" type="text/css" href="resources/css/jquery-ui.structure.css"/>
    <link rel="stylesheet" media="screen" type="text/css" href="resources/css/jquery-ui.theme.css"/>
    <style type="text/css">
        body{
            background-color: #37415d;
            color: #bbbbbb;
        }
        .reportTr:hover {
            color: whitesmoke;
            border-bottom: 1px solid lightcyan;
        }
        #table_reports{
            position: fixed;
            border: 1px solid yellow;
            cursor: pointer;
            font-size: x-large;
        }
       .icon_button{
           width: 28px;
           height: 28px;
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
        <h1>ЗВІТИ</h1>
    </div>
    <div id="div_right_side" class="div_header_additions" >
    </div>
</header>

<div>
    <table id="table_reports">
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
                <img id="doAUreport1" class="icon_button" src="resources/css/images/excel.jpg" title="завантажити">
            </td>
        </tr>

        <tr class="reportTr">
            <td id="report3" class="reportName">
                Таблиця проданих кредитів
            </td>
            <td hidden="hidden" class="dateTd">
                <input id="startDate3" class="datepicker" about="початкова дата" title="оберіть початкову дату торгів">
            </td>
            <td hidden="hidden" class="dateTd">
                <input id="endDate3" class="datepicker" about="кінцева дата" title="оберіть кінцеву дату торгів">
            </td>
            <td hidden="hidden" class="dateTd">
                <img id="downloadCrdts" class="icon_button" src="resources/css/images/excel.jpg" title="завантажити">
            </td>
        </tr>

        <tr class="reportTr">
            <td id="report4" class="reportName">
                Таблиця активів
            </td>
            <td hidden="hidden" class="dateTd">
                <img id="downloadAss" class="icon_button" src="resources/css/images/excel.jpg" title="завантажити">
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
                <img id="downloadPays" class="icon_button" src="resources/css/images/excel.jpg" title="завантажити">
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
                <img id="downloadBidReport" class="icon_button" src="resources/css/images/excel.jpg" title="завантажити">
            </td>
        </tr>

        <tr class="reportTr">
            <td id="report7" class="reportName">
                Реєстр кредитів
            </td>
            <td hidden="hidden" class="dateTd">
                <input id="startDate8" class="datepicker" about="початкова дата" title="оберіть початкову дату торгів">
            </td>
            <td hidden="hidden" class="dateTd">
                <input id="endDate8" class="datepicker" about="кінцева дата" title="оберіть кінцеву дату торгів">
            </td>
            <td hidden="hidden" class="dateTd">
                <img id="downloadReestr" class="icon_button" src="resources/css/images/excel.jpg" title="завантажити">
            </td>
        </tr>

        <%--<tr class="reportTr">
            <td id="report7" class="reportName">
                Звірка данних
            </td>
            <td hidden="hidden" class="dateTd">
                <input id="minAuditDate" class="datepicker" about="початкова дата звірки" title="оберіть початкову дату звірки">
            </td>
            <td hidden="hidden" class="dateTd">
                <input id="maxAuditDate" class="datepicker" about="кінцева дата звірки" title="оберіть кінцеву дату звірки">
            </td>
            <td hidden="hidden" class="dateTd">
                <img id="downloadAudit" class="icon_button" src="resources/css/images/excel.jpg" title="завантажити">
            </td>
        </tr>--%>

    </table>
</div>

<footer>
</footer>
</body>
</html>
