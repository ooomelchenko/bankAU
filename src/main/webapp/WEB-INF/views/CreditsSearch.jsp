<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Пошук кредитів</title>

    <link rel="stylesheet" media="screen" type="text/css" href="resources/css/general_style.css"/>

    <script type="text/javascript" src="resources/js/jquery-3.2.1.js"></script>
    <script>
        $(document).ready(function () {
            var ftab = $('.findTab');
            var history_table = $('.history_table');
            var history_accPrice_table = $('.history_accPrice_table');

            function addToFindTab(obj) {
                $('.findTab').find('.ftr').remove();
                ftab.show();

                for (var i = 0; i < obj.length; i++) {

                    var tr = $('<tr class="ftr" align="center">' +
                        '<td class="idObj" hidden="hidden">' + obj[i].id + '</td>' +
                        '<td class="ndObj">' + obj[i].nd + '</td>' +
                        '<td>' + obj[i].inn + '</td>' +
                        '<td class="lotId">' + obj[i].lot + '</td>' +
                        '<td>' + obj[i].fio + '</td>' +
                        '<td>' + obj[i].product + '</td>' +
                        '<td>' + obj[i].region + '</td>' +
                        '<td>' + obj[i].zb + '</td>' +
                        '<td>' + obj[i].rv + '</td>' +
                        '<td>' + obj[i].nbuPladge + '</td>' +
                        '</tr>');

                    ftab.append(tr);
                }
                $('.lotId').dblclick(function(){
                    var idL =$(this).text();
                    window.open("lotRedactor/"+idL)
                });
            }

            $('#findObjBut').click( function () {
                $('.findTab').find('.ftr').remove();
                ftab.show();

                $.ajax({
                    url: "allCreditsByClient",
                    method: "POST",
                    data: {
                        inn: $('#inn').val(),
                        idBars: $('#idBars').val()
                    },
                    success(obj){
                        addToFindTab(obj);
                    }
                });
            });

            $('#objHistoryBut').click( function() {
                if($(this).val()==0){
                    $(this).val(1);
                    $('#div_asset_history').show();
                }
                else{
                    $(this).val(0);
                    $('#div_asset_history').hide();
                }
                $('.asset_history_tr').remove();
                $('.price_history_tr').remove();

                $.ajax({
                    url: "getCreditsHistory",
                    method: "POST",
                    data: {
                        inn: $('#inn').val(),
                        idBars: $('#idBars').val()
                    },
                    success(objList){
                        for (var i = 0; i < objList.length; i++) {
                            var obj = objList[i].split("||");

                            var trR = $('<tr align="center" class="asset_history_tr">' +
                                '<td>' + obj[0] +'</td>' +
                                '<td>' + obj[1] + '</td>' +
                                '<td>' + obj[2] + '</td>' +
                                '<td>' + obj[3] + '</td>' +
                                '<td>' + obj[4] + '</td>' +
                                '</tr>');
                            history_table.append(trR);
                        }
                    }
                });
                $.ajax({
                    url: "getCrPriceHistory",
                    method: "POST",
                    data: {inn: $('#inn').val(),
                        idBars: $('#idBars').val()},
                    success(objList){
                        for (var i = 0; i < objList.length; i++) {
                            var d = new Date(objList[i].date);
                            var trPh = $('<tr align="center" class="price_history_tr">' +
                                '<td>' + d.toISOString().substring(0, 10) + '</td>' +
                                '<td>' + objList[i].acceptedPrice + '</td>' +
                                '</tr>');
                            history_accPrice_table.append(trPh);
                        }
                    }
                });
            });

            $('#formDownld').click(function(){
                window.open("downLotIdListForm");
            });

            $('#sendBut').click(function(){
                sendFile();
            });

            $('#button_addPriceByFile').click(function(){
                loadFile();
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

            function loadFile(){
                var formData = new FormData($('#form_prices')[0]);
                $.ajax({
                    type: "POST",
                    processData: false,
                    contentType: false,
                    url: "setAccPriceByFile",
                    data:  formData,
                    success: function (res) {
                        if ((res)==="1"){
                            alert("затверджені ціни додано!");
                            location.reload(true);
                        }
                        else if ((res)==="0"){
                            alert("затверджені ціни не додано!");
                        }
                    }
                })
            }

            function sendFile(){
                var formData = new FormData($('form')[0]);
                $.ajax({
                    type: "POST",
                    processData: false,
                    contentType: false,
                    url: "uploadIdFile",
                    data:  formData,
                    success: function (obj) {
                        addToFindTab(obj);
                    }
                })
            }

        });
    </script>

    <style>
        table{
            border-collapse: collapse;
            font-size: x-small;
        }
        table th{
            background-color: #37415d;
        }
        #div_search {
            margin-top: -40px;
            margin-bottom: 20px;
            width: 100%;
            display: inline-table;
        }

        .div_search_block {
            border: 1px solid;
            text-align: center;
            vertical-align: center;
            display: table-cell;
        }
        #div_search_file div{
            display: table-cell;
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
        <h1>Пошук кредитів</h1>
    </div>
    <div id="div_right_side" class="div_header_additions">
        <button id="addPriceByFileBut" value="0">ДОДАТИ ЗАТВЕРДЖЕНУ ФГВФО ЦІНУ</button>
    </div>
</header>

<div id="div_search">

    <div id="div_search_inn" class="div_search_block" style="width: 50%">
        <input id="inn" type="text" placeholder="Введіть ІНН для пошуку" style="width: 50%; font-size: large">

        <input id="idBars" type="text" placeholder="Введіть ID_BARS"
               style=" width: 50%">
        <br/>
        <img id="findObjBut" class="icon_button" src="resources/css/images/search.png" title="Знайти" width="40px"
             height="40px">
        <br/>
        <button id="objHistoryBut">Показати історію</button>

    </div>
    <div class="div_search_block" style="width: 30%">

        <form method="POST" action="" enctype="multipart/form-data" lang="utf8">
            <h4>Обрати файл зі списком ID_Bars:</h4>
            <input align="center" type="file" name="file" title="натисніть для обрання файлу"><br/>
            <input name="idType" value="0" type="number" hidden="hidden">
        </form>
        <img id="sendBut" class="icon_button" src="resources/css/images/search.png" title="Знайти по списку з файлу" width="40px" height="40px">

    </div>
    <div id="div_add_dec_form" class="div_search_block" style="width: 20%; display: none; background-color: white">
        <form id="form_prices" method="POST" action="" enctype="multipart/form-data" lang="utf8" >
            <h4>Обрати файл з цінами:</h4>
            <input align="center" type="file" name="file" title="натисніть для обрання файлу"><br/>
            <input name="idType" value="0" type="number" hidden="hidden">
        </form>
        <button id="button_addPriceByFile" title="завантажити ціну з файлу">Завантажити</button>
    </div>
</div>

<div class="div_obj_table">
    <table class="findTab" border="2" hidden="hidden" width="100%">
        <tr align="center" style="color: lightskyblue;">
            <th hidden="hidden">Key_N</th>
            <th>ID_BARS</th>
            <th>ІНН</th>
            <th>Лот</th>
            <th>Боржник</th>
            <th>Опис кредиту</th>
            <th>Регіон</th>
            <th>Загальний борг</th>
            <th>Оціночна вартість, грн.</th>
            <th>В заставі НБУ</th>
        </tr>
    </table>
</div>

<div id="div_asset_history" hidden="hidden">
    <div>
        <h2>Історія торгів</h2>
        <table class="history_table" border="1">
            <tr align="center" style="color: #edff9a">
                <th>Інвентарний №</th>
                <th>Лот</th>
                <th>Біржа</th>
                <th>Дата аукціону</th>
                <th>Затверджена ціна, грн.</th>
            </tr>
        </table>
    </div>
    <div>
        <h2>Історія зміни ціни</h2>
        <table class="history_accPrice_table" border="1">
            <tr align="center" style="color: #fffe9f">
                <th>Дата запису</th>
                <th>Затверджена ціна, грн.</th>
            </tr>
        </table>
    </div>
</div>

<footer >
    <div style="width: 100%; text-align: center">
        Форма для завантаження <img class="icon_button" id="formDownld" style="width: 40px; height: 40px" src="resources/css/images/excel.jpg" title="Завантажити зразок файлу зі списком ID для пошуку (.xls)" >
    </div>
</footer>
</body>
</html>