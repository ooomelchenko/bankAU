$(document).ready(function() {
    lotsCalculations();
});
function lotsCalculations(){
    var trL = $('.trL');
    trL.dblclick(function(){
        var idL = $(this).children().first().text();
        $.ajax({
            url: "/setRlot",
            type: "GET",
            data: {lotID: idL},
            success: function(){
                window.open("/lotRedactor")
            }
        })
    });

    $('.lotId').each(function(){
        var lotStr = $(this).parent();
        var count = $(this).parent().find('.countOfCrd');
        var sum = $(this).parent().find('.sumOfCrd');
        var paymentsSum = $(this).parent().find('.paymentsSum');
        var factPrice = $(this).parent().find('.factPrice');
        var residualToPay = $(this).parent().find('.residualToPay');
        var payStatus = $(this).parent().find('.payStatus');

        $.ajax({
            url: "/countSumByLot",
            type: "POST",
            data: {lotId: $(this).text()},
            success: function(countSum){
                count.text(countSum[0]);
                sum.text(countSum[1]);
            }
        });
        $.ajax({
            url: "/paymentsSumByLot",
            method: "POST",
            data: {lotId: $(this).text()},
            success: function (paySum) {
                if (paySum>0) {
                    paymentsSum.text(paySum);
                    var residual = (parseFloat(paySum) - parseFloat(factPrice.text())).toFixed(2);
                    residualToPay.text(residual);
                    if(residual>=0){
                        payStatus.text("100% сплата");
                        lotStr.css({'color': "green"})
                    }
                    else {payStatus.text("Часткова сплата")}
                }
                else{
                    residualToPay.text(factPrice.text())
                }
            }
        });
    });
    $('.isSoldId').each(function () {
        if ($(this).text() === "Продано")
            $(this).parent().css({'color': "blue"});
    });
}