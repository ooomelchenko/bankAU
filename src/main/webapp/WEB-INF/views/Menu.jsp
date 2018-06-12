<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Головне меню</title>
    <script src="resources/js/jquery-3.2.1.js"></script>
    <script>
        $(document).ready(function () {
            $('#button_lots_direction').click(function(){
                if($('#radio_asset_lot_type').prop('checked')){
                    location.replace("lotMenu/assets/notSolded");
                }
                else if($('#radio_credit_lot_type').prop('checked')){
                    location.replace("lotMenu/credits/notSolded");
                }
            });
            $('#button_bids_direction').click(function(){
                if($('#radio_bids_this_year').prop('checked')){
                    location.replace("bidMenu/2018");
                }
                else if($('#radio_bids_all').prop('checked')){
                    location.replace("bidMenu");
                }
            });

            $('#search_img').click(function () {
                window.open("assetsSearch")
            });
            $('#search_cr_img').click(function () {
                window.open("creditsSearch")
            });
            $('#button_assets').click(function () {
                window.open("assets/1")
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
            border: 1px solid cyan;
        }
        #button_bids_direction:hover{
            border: 1px solid sandybrown;
        }
        #button_exchanges_direction:hover{
            border: 1px solid mediumvioletred;
        }
        #button_assets:hover{
            border: 1px solid darkgreen;
        }
        #button_credits:hover{
            border: 1px solid blue;
        }
        #search_img, #search_cr_img{
            width: 75px;
            height: 45px;
            opacity: 0.5;
        }
        #search_img:hover, #search_cr_img:hover{
            cursor: pointer;
            opacity: 1;
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
        #div_direct_menu{
            display: inline-table;
        }
        #div_direct_menu div{
            display: table-cell;
        }
    </style>
</head>

<body>
<header>
    <div id="div_left_side" class="div_header_additions">
        <h3>  ${userId}, вітаємо в програмі!</h3>
    </div>
    <div id="div_sheet_header">
        <h1>ГОЛОВНЕ МЕНЮ</h1>
    </div>
    <div id="div_right_side" class="div_header_additions" >
        <a href="logout">
            <img src="resources/css/images/log_out.png" title="Вийти з програми">
        </a>
    </div>
</header>

<div id="div_mainMenu" align="center" style="width: 100%; position: fixed">
    <div id="div_direct_menu">
        <div>
            <button id="button_lots_direction">Лоти</button>
            <br/>
            активи <input type="radio" name="radio_lot_type" id="radio_asset_lot_type" checked="checked">
                   <input type="radio" name="radio_lot_type" id="radio_credit_lot_type"> кредити
        </div>
        <div>
            <button id="button_bids_direction" onclick="location.href = 'bidMenu'">Аукціони</button>
            <br/>
            з 2018 року <input type="radio" name="radio_bid_period" id="radio_bids_this_year" checked="checked">
                        <input type="radio" name="radio_bid_period" id="radio_bids_all"> всі
        </div>
        <div>
            <button id="button_exchanges_direction" onclick="location.href = 'exMenu'">Біржі</button>
        </div>

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
        <div style="width: 50%">
            <h4 title="поточна версія">Аукціони v2.0</h4>
        </div>
        <div style="display: table-cell; width: 50%; text-align: center">
            <a href="http://172.17.2.245:8083/nb_v1.0/" title="Клікніть для переходу до старої версії програми" style="font-size: large; color: violet">Аукціони v1.0</a>
            <br/>
            <b>посилання на стару версію</b>
        </div>
</footer>
</body>
</html>