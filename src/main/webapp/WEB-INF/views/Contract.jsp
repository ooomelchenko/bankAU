<%@ page import="nb.domain.Lot" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<base href="${pageContext.request.contextPath}/"/>
<html>
<head>
    <title>Договір</title>
    <%Lot lot = (Lot) request.getAttribute("lott");%>

    <script src="resources/js/jquery-3.2.1.js"></script>
    <script type="text/javascript" src="resources/js/jquery-ui.js"></script>
    <script>
        $(document).ready(function() {
            var lotId<%=("="+request.getAttribute("lotId"))%>;



            var dp = $('.datepicker');
            dp.datepicker({dateFormat: "yy-mm-dd", dayNamesMin: ["Нд","Пн","Вт","Ср","Чт","Пт","Сб"],
                monthNames: ["січень","лютий","березень", "квітень", "травень","червень", "липень","серпень","вересень","жовтень","листопад","грудень"] });
        })
    </script>

    <link rel="stylesheet" type="text/css" href="resources/css/general_style.css" >
    <link rel="stylesheet" media="screen" type="text/css" href="resources/css/jquery-ui.css"/>
    <link rel="stylesheet" media="screen" type="text/css" href="resources/css/jquery-ui.structure.css"/>
    <link rel="stylesheet" media="screen" type="text/css" href="resources/css/jquery-ui.theme.css"/>
    <style type="text/css">
        input{
            width: 330px;
            font-size: x-large;
        }

    </style>
</head>
<body>
<header>
    <div id="div_left_side" class="div_header_additions">
    </div>
    <div id="div_sheet_header">
        <h1>Параметри договору</h1>
    </div>
    <div id="div_right_side" class="div_header_additions" >
    </div>
</header>

<div id="div_contract_params">
    <input type="text" placeholder="рік (можна цифрами)">
    <br/>
    <input type="text" placeholder="адреса переможця">
    <br/>
    <input type="text" placeholder="№ протоколу">
    <input type="text" placeholder="Дата протоколу">
    <input type="text" placeholder="Ким складено протокол">
</div>

<footer>
    <div style="width: 100%; text-align: center">
        Завантаження договору <img class="icon_button" id="icon_contract_download" src="resources/css/images/contract_icon.png" title="Завантажити документ" style="width: 40px; height: 40px;">
    </div>
</footer>
</body>
</html>
