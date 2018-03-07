<%@ page import="nadrabank.domain.Credit" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Створення лоту</title>
    <script src="js/jquery-1.11.1.js"></script>
    <script>
        $(document).ready(function () {
            var ftab = $('#findTab');
            var ltab = $('#lotTab');

            calculate();

            $('#findObjBut').on('click', function () {
                ltab.hide();
                $('#findTab').find('.ftr').remove();
                ftab.show();
                $.ajax({
                    url: "creditsByClient",
                    method: "POST",
                    data: {
                        inn: $('#inn').val(),
                        idBars: $('#idBars').val()
                    },
                    success(obj){

                        for (var i = 0; i < obj.length; i++) {
                            var approveNBU = obj[i].nbuPladge ? "Так" : "Ні";
                            var tr = $('<tr class="ftr" align="center">' +
                                '<td class="idCredit">' + obj[i].id + '</td>' +
                                '<td>' + obj[i].inn + '</td>' +
                                '<td>' + obj[i].fio + '</td>' +
                                '<td>' + obj[i].product + '</td>' +
                                '<td>' + obj[i].region + '</td>' +
                                '<td>' + obj[i].zb + '</td>' +
                                '<td>' + obj[i].rv + '</td>' +
                                '<td>' + approveNBU + '</td>' +
                                '</tr>');

                            var addButton = $('<button id="addButton">Додати в лот</button>');
                            addButton.on('click', function () {
                                var thisTr = $(this).parent();
                                ltab.append(thisTr);
                                $(this).remove();

                                var delButton = $('<button id="delButton">Видалити</button>');
                                thisTr.append(delButton);
                                delButton.on('click', function () {
                                    $(this).parent().remove();
                                    calculate();
                                });
                                calculate();
                            });
                            var messageTd = $('<td bgcolor="#00ffff">Вже додано до лоту</td>');

                            ftab.append(tr);
                            tr.append(addButton);

                            var idF = tr.children().first().text();
                            var lids = ltab.find('.idCredit');
                            for (var j = 0; j < lids.length; j++) {
                                if (lids[j].text() == idF) {
                                    addButton.remove();
                                    tr.append(messageTd);
                                }
                            }
                        }
                    }
                });
            });
            $('#showLCrdts').on('click', function () {
                ftab.hide();
                ltab.show();
            });
            function calculate() {
                var tdId = ltab.find('.idCredit');

                var idl = "";
                for (var i = 0; i < tdId.length; i++) {
                    idl = idl + ',' + tdId[i].innerHTML;
                }
                $.ajax({
                    url: "sumByIDBars",
                    method: "POST",
                    data: {idMass: idl},
                    success(summ){
                        if (summ)
                            $('#priceId').text(summ);
                        $('#kolId').text(tdId.length);
                    }
                });
            }

            $('#createLot').on('click', function createLot() {
                var tdId = ltab.find('.idCredit');
                var idl = "";
                for (var i = 0; i < tdId.length; i++) {
                    idl = idl + ',' + tdId[i].innerHTML;
                }
                $.ajax({
                    url: "createCreditLot",
                    method: "POST",
                    data: {
                        idMass: idl,
                        comment: $('#commId').val()
                    },
                    success(ok){
                        if (ok == '1') {
                            alert("Лот створено!");
                            location.href = "lotRedactor";
                        }
                        else
                            alert("Лот не створено!");
                    }
                });
            })
        })
    </script>
</head>

<body style="background-color: mintcream">
<button onclick="location.href='lotMenu'">Назад</button>
<div id="credSort" class="choice-box">
    <H1 align="center">Створення нового лоту</H1>
    <table width="100%">
        <tr>
            <td>
                <table>
                    <tr>
                        <td colspan="2">ІНН</td>
                        <td colspan="3">
                            <input id="inn" type="text" style="background-color: aliceblue; width: 100%">
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2">ID_BARS</td>
                        <td colspan="3">
                            <input id="idBars" type="text" style="background-color: aliceblue; width: 100%">
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2"></td>
                        <td colspan="3">
                            <button id="findObjBut" class="button" style="width: 100%">Знайти</button>
                        </td>
                    </tr>
                </table>
            </td>

            <td align="top">
                <table>
                    <tr>
                        <td colspan="1">Коментар</td>
                        <td colspan="2"><input id="commId" type="text" style="width: 100%"></td>
                    </tr>
                </table>
                <table>
                    <tr>
                        <td>
                            <table border="1" class="table" style="background-color: lightcyan" id="tblParam">
                                <tr>
                                    <th>Ціна лоту, грн.</th>
                                    <th>К-ть об'єктів</th>
                                </tr>
                                <tr>
                                    <td id="priceId" align="center">0</td>
                                    <td id="kolId" align="center">0</td>
                                </tr>
                                <tr>
                                    <td style="border: none" colspan="2">
                                        <button id="showLCrdts" class="button"
                                                style="background-color: cyan; width: 100%"> Показати список лоту
                                        </button>
                                    </td>
                                </tr>
                            </table>
                        </td>
                        <td>
                            <button style="font: inherit; color: darkblue; width: 120px; height: 60px"
                                    id="createLot" class="button">СТВОРИТИ ЛОТ
                            </button>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>

    </table>
</div>

<div id="assetList" class="view" style="width: 100%">
    <table id="findTab" class="table" border="2" hidden="hidden" bgcolor="#0000cd">
        <tr align="center" bgcolor="#ffefd5">
            <th>ID_BARS</th>
            <th>Інвентарний №</th>
            <th>Боржник</th>
            <th>Опис кредиту</th>
            <th>Регіон</th>
            <th>Загальний борг</th>
            <th>Оціночна вартість, грн.</th>
            <th>В заставі НБУ</th>
        </tr>
    </table>
    <table id="lotTab" class="table" border="3" hidden="hidden" bgcolor="lightcyan">
        <tr align="center" bgcolor="#00ffff">
            <th>ID_BARS</th>
            <th>Інвентарний №</th>
            <th>Боржник</th>
            <th>Опис кредиту</th>
            <th>Регіон</th>
            <th>Загальний борг</th>
            <th>Оціночна вартість, грн.</th>
            <th>В заставі НБУ</th>
        </tr>
        <%
            List<Credit> creditList = (List<Credit>) request.getAttribute("creditList");
            for (Credit crdt : creditList) {
                // if(crdt.getLot()==null && !crdt.getFondDecision().equals("Відправлено до ФГВФО") && !crdt.getFondDecision().equals("") ){
        %>
        <tr align="center">
            <td class="idCredit"><%=crdt.getId()%></td>
            <td><%=crdt.getInn()%></td>
            <td><%=crdt.getFio()%></td>
            <td><%=crdt.getProduct()%></td>
            <td><%=crdt.getRegion()%></td>
            <td><%=crdt.getZb()%></td>
            <td><%=crdt.getRv()%></td>
            <td><%=crdt.getNbuPladge()%></td>
        </tr>
        <%
                //    }
            }
        %>
    </table>
</div>

</body>
</html>