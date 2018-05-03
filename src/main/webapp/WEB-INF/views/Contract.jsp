<%@ page import="nb.domain.Lot" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<base href="${pageContext.request.contextPath}/"/>
<html>
<head>
    <title>Договір</title>
    <%Lot lot = (Lot) request.getAttribute("lot");%>

    <script src="resources/js/jquery-3.2.1.js"></script>
    <script type="text/javascript" src="resources/js/jquery-ui.js"></script>
    <script>
        $(document).ready(function() {
            var lotId<%=("="+request.getAttribute("lotId"))%>;
            var input_contract_year = $('#input_contract_year');
            var input_contract_address = $('#input_contract_address');
            var input_contract_protokol_num = $('#input_contract_protokol_num');
            var input_contract_protokol_date = $('#input_contract_protokol_date');
            var input_protocol_made_by = $('#input_protocol_made_by');
            var input_subscriber = $('#input_subscriber');

            var input_pass_seria = $('#input_pass_seria');
            var input_pass_num = $('#input_pass_num');
            var input_pass_vidano = $('#input_pass_vidano');
            var input_pass_vidano_date = $('#input_pass_vidano_date');
            var input_operates_basis = $('#input_operates_basis');
            var input_account_num = $('#input_account_num');
            var input_account_bank = $('#input_account_bank');
            var input_sign_deadline = $('#input_sign_deadline');

            function checkFields(){
                if(input_subscriber.val() === "")
                    input_subscriber.val("null");
            }

            $('#icon_contract_download').click(function(){
                checkFields();
                <%if(lot.getLotType()==0){%>
                window.open("downloadCreditContract/"+lotId+"/"+input_contract_year.val()+"/"+input_contract_address.val()+"/"+input_contract_protokol_num.val()+"/"+input_contract_protokol_date.val()+"/"+input_protocol_made_by.val()+"/"+input_subscriber.val());
                <%}%>
                <%if(lot.getLotType()==1){%>
                window.open("downloadAssetContract/"+lotId+"/"+input_contract_year.val()+"/"+input_contract_address.val()
                    +"/"+input_contract_protokol_num.val()+"/"+input_contract_protokol_date.val()+"/"+input_protocol_made_by.val()
                    +"/"+input_subscriber.val()+"/"+input_pass_seria.val()+"/"+input_pass_num.val()+"/"+input_pass_vidano.val()+"/"+input_pass_vidano_date.val()
                    +"/" +input_operates_basis.val()+"/" +input_account_num.val()+"/" +input_account_bank.val()+"/" +input_sign_deadline.val());
                <%}%>

            });
            $('#icon_contract_akt_download').click(function(){
                checkFields();
                <%if(lot.getLotType()==0){%>
                window.open("downloadContract_Akt/"+lotId+"/"+input_contract_year.val()+"/"+input_contract_address.val()+"/"+input_contract_protokol_num.val()+"/"+input_contract_protokol_date.val()+"/"+input_protocol_made_by.val()+"/"+input_subscriber.val());
                <%}%>
                <%if(lot.getLotType()==1){%>
                window.open("downloadAssetContract_Akt/"+lotId+"/"+input_contract_year.val()+"/"+input_contract_address.val()
                    +"/"+input_contract_protokol_num.val()+"/"+input_contract_protokol_date.val()+"/"+input_protocol_made_by.val()
                    +"/"+input_subscriber.val()+"/"+input_pass_seria.val()+"/"+input_pass_num.val()+"/"+input_pass_vidano.val()+"/"+input_pass_vidano_date.val()
                    +"/" +input_operates_basis.val()+"/" +input_account_num.val()+"/" +input_account_bank.val()+"/" +input_sign_deadline.val());
                <%}%>
            });
            $('#icon_contract_d1').click(function(){
                checkFields();
                window.open("downloadContract_Dodatok/1/"+lotId+"/"+input_contract_year.val()+"/"+input_contract_address.val()+"/"+input_contract_protokol_num.val()+"/"+input_contract_protokol_date.val()+"/"+input_protocol_made_by.val()+"/"+input_subscriber.val());

            });
            $('#icon_contract_d2').click(function(){
                checkFields();
                window.open("downloadContract_Dodatok/2/"+lotId+"/"+input_contract_year.val()+"/"+input_contract_address.val()+"/"+input_contract_protokol_num.val()+"/"+input_contract_protokol_date.val()+"/"+input_protocol_made_by.val()+"/"+input_subscriber.val());

            })
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
        #div_download_menu{
            width: 100%;
            display: inline-table;
        }
        #div_download_menu div{
            display: table-cell;
            width: 25%;
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
    <input id="input_contract_year" type="text" placeholder="рік (можна цифрами)">
    <br>
    <input id="input_contract_address" type="text" placeholder="адреса переможця">
    <br>
    <input id="input_contract_protokol_num" type="text" placeholder="№ протоколу">
    <br>
    <input id="input_contract_protokol_date" type="text" placeholder="Дата протоколу">
    <br>
    <input id="input_protocol_made_by" type="text" placeholder="Ким складено протокол">
    <br>
    <input id="input_subscriber" type="text" placeholder="ФІО підписанта">
    <%if(lot.getLotType() == 1){%>
    <br>
    <input id="input_pass_seria" type="text" placeholder="Серія паспорту">
    <br>
    <input id="input_pass_num" type="text" placeholder="Номер паспорту">
    <br>
    <input id="input_pass_vidano" type="text" placeholder="Ким видано паспорт">
    <br>
    <input id="input_pass_vidano_date" type="text" placeholder="Дата видачі паспорту">
    <br>
    <input id="input_operates_basis" type="text" placeholder="Який діє на підставі">
    <br>
    <input id="input_account_num" type="number" placeholder="Номер рахунку клієнта">
    <br>
    <input id="input_account_bank" type="text" placeholder="Банк кієнта">
    <br>
    <input id="input_sign_deadline" type="text" placeholder="Кінцевий термін підписання">
    <%}%>
</div>

<footer>
    <div id="div_download_menu">
        <div style="text-align: center">
            Завантаження договору <img class="icon_button" id="icon_contract_download" src="resources/css/images/contract_icon.png" title="Завантажити документ" style="width: 40px; height: 40px;">
        </div>
        <div style="text-align: center">
            Завантаження акту <img class="icon_button" id="icon_contract_akt_download" src="resources/css/images/contract_icon.png" title="Завантажити Акт по договору" style="width: 40px; height: 40px;">
        </div>
        <%if(lot.getLotType() == 0){%>
        <div style="text-align: center">
            Завантаження Додаток 1 <img class="icon_button" id="icon_contract_d1" src="resources/css/images/contract_icon.png" title="Завантажити Додаток 1 по договору" style="width: 40px; height: 40px;">
        </div>
        <div style="text-align: center">
            Завантаження Додаток 2 <img class="icon_button" id="icon_contract_d2" src="resources/css/images/contract_icon.png" title="Завантажити Додаток 2 по договору" style="width: 40px; height: 40px;">
        </div>
        <%}%>
    </div>

</footer>
</body>
</html>
