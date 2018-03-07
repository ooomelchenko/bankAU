<%@ page import="nadrabank.domain.Credit" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Пошук кредитів</title>
    <link rel="stylesheet" media="screen" type="text/css" href="css/lotCreatorStyle.css"/>
    <script src="js/jquery-1.11.1.js"></script>
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
                    if(idL!=""){
                        $.ajax({
                            url: "setRlot",
                            type: "GET",
                            data: {lotID: idL},
                            success: function(){
                                window.open("lotRedactor")
                            }
                        })
                    }
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
                    $('#asset_history_block').show();
                }
                else{
                    $(this).val(0);
                    $('#asset_history_block').hide();
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
</head>

<body style="background-color: mintcream">
<div id="b0">
    <button onclick="location.href='lotMenu'">Назад</button>
    <H1 align="center">Пошук кредитів</H1>
</div>

<div id="mainBlock">

    <div id="searchBlock" class="choice-box">

        <div>
            <table style="width: 100%">
                <tr>
                    <td>
                        <input id="inn" type="text" placeholder="Введіть ІНН для пошуку" style="width: 100%">
                    </td>
                </tr>
                <tr>
                    <td>
                        <input id="idBars" type="text" placeholder="Введіть ID_BARS"
                               style="background-color: aliceblue; width: 100%">
                    </td>
                </tr>
                <tr>
                    <td>
                        <button id="findObjBut" class="button" style="width: 100%">Знайти</button>
                        <button id="objHistoryBut" class="button" style="width: 20%">Історія</button>
                    </td>
                </tr>
            </table>
        </div>

    </div>
    <div id="listSearchBlock">

        <form method="POST" action="" enctype="multipart/form-data" lang="utf8">
            <h3>Обрати файл зі списком ID_Bars:</h3>
            <input align="center" type="file" name="file" title="натисніть для обрання файлу"><br/>
            <input name="idType" value="0" type="number" hidden="hidden">
        </form>

        <button id="sendBut">Знайти по списку з файлу</button>
    </div>
    <div style="width: 10%">
        <button id="formDownld" title="Завантажити зразок файлу зі списком ID для пошуку">форма(.xls)</button>
    </div>
</div>
<br/>

<div class="view">
    <table class="findTab" border="2" hidden="hidden">
        <tr align="center" style="background-color: darkblue; color: white">
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

<div id="asset_history_block" hidden="hidden">
    <div>
        <h2>Історія торгів</h2>
        <table class="history_table" border="1">
            <tr align="center" style="background-color: #edff9a">
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
            <tr align="center" style="background-color: #fffe9f">
                <th>Дата запису</th>
                <th>Затверджена ціна, грн.</th>
            </tr>
        </table>
    </div>
</div>
</body>
</html>