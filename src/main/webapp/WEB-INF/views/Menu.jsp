<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Головне меню</title>
    <script src="resources/js/jquery-3.2.1.js"></script>
    <script>
        $(document).ready(function () {
            $('#search_img').click(function () {
                window.open("assetsSearch")
            });
            $('#search_cr_img').click(function () {
                window.open("creditsSearch")
            });
            $('#button_assets').click(function () {
                window.open("assets")
            });
            $('#button_credits').click(function () {
                window.open("credits")
            });
        })
    </script>
    <link href="resources/css/general_style.css" rel="stylesheet" type="text/css">
    <style type="text/css">
        #div_mainMenu button{
            width: 200px;
            height: 60px;
            font-size: 35px;
            font-weight: bolder;
        }
        #div_mainMenu div{
            vertical-align: top;
            height: 100px;
            padding: 3%;
        }
        #button_lots_direction:hover{
            border: double cyan;
        }
        #button_bids_direction:hover{
            border: double sandybrown;
        }
        #button_exchanges_direction:hover{
            border: double darkblue;
        }
        #button_assets:hover{
            border: double darkgreen;
        }
        #button_credits:hover{
            border: double blue;
        }
        #search_img, #search_cr_img{
            width: 75px;
            height: 45px;
        }
        #search_img:hover, #search_cr_img:hover{
            cursor: pointer;
            width: 77px;
            height: 47px;
        }
        #img_reports{
            width: 80px;
            height: 60px;
        }
        #img_reports:hover{
            width: 90px ;
            height: 70px;
        }
        button {
            color: #bdc2e7;
            background-color: #37415d;
            width: 20%;
            height: 60px;
            cursor: pointer;
            font-family: "Trebuchet MS", "Lucida Sans";
            padding: 7px 20px;
            margin-bottom: 10px;
            border-radius: 5px;

            box-shadow: 2px -2px 5px 0 rgba(0, 0, 0, .1),
            -2px -2px 5px 0 rgba(0, 0, 0, .1),
            2px 2px 5px 0 rgba(0, 0, 0, .1),
            -2px 2px 5px 0 rgba(0, 0, 0, .1);
            font-size: 25px;
            letter-spacing: 2px;
            transition: 0.2s all linear;
        }
        button:hover {
            background-color: #252F48;
            color: whitesmoke;
        }
    </style>
</head>

<body>
<header>
    <div id="div_left_side" class="div_header_additions">
        <h3 >${userId}, вітаємо в програмі!</h3>
    </div>
    <div id="div_sheet_header">
        <h1>ГОЛОВНЕ МЕНЮ</h1>
    </div>
    <div id="div_right_side" class="div_header_additions" style="text-align: center">
        <a href="logout">
            <img src="resources/css/images/log_aut.png" title="Вийти з програми" width="50px" height="50px">
        </a>
    </div>
</header>

<div id="div_mainMenu" align="center">
    <div id="div_direct_menu">
        <%--<img class="menuImg" src="images/menu/lot.jpg" onclick="location.href = 'lotMenu'" title="ЛОТИ">
        <img class="menuImg" src="images/menu/bid.jpg" onclick="location.href = 'bidMenu'" title="ТОРГИ">
        <img class="menuImg" src="images/menu/ex.jpg" onclick="location.href = 'exMenu'" title="БІРЖІ">--%>
        <button id="button_lots_direction" onclick="location.href = 'notSoldedLotMenu'">Лоти</button>
        <button id="button_bids_direction" onclick="location.href = 'bidMenu'">Аукціони</button>
        <button id="button_exchanges_direction" onclick="location.href = 'exMenu'">Біржі</button>
    </div>
    <div id="div_unit_lists">
        <%--<img id="assButt" class="menuImg" src="images/menu/assets.jpg" title="Відкрити список матеріальних активів">
        <img id="crdButt" class="menuImg" src="images/menu/credits.jpg" title="Відкрити список кредитів">--%>
        <img id="search_img" src="resources/images/search.png" title="пошук об'єктів">
        <button id="button_assets">Об'єкти</button>
        <button id="button_credits">Кредити</button>
        <img id="search_cr_img" src="resources/images/search.png" title="пошук кредитів">
    </div>
    <div id="div_reports">
        <a href="reports"><img id="img_reports" src="resources/images/reports.png"
                               title="Перейти до завантаження звітів щодо проведених аукціонів"/>
        </a>
    </div>
</div>
<footer>
    <%--<h3>
        Останні оновлення
    </h3>
        <ul>
            <li>Додано редагування Затвердженої ФГВФО ціни об'єктів у вкладці РЕДАГУВАННЯ ЛОТУ</li>
            <li>Додано можливість редагування Початкової ціни лоту(від якої розраховується дисконт)</li>
        </ul>--%>
</footer>

</body>
</html>