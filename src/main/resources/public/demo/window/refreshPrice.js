/**
 * Created by liang on 2017/6/6.
 */
function refreshPrice(index ,url){

    var cprice = $("#priceInput"+index).val();
    var tempCookie = $("#cookie"+index).val();
    var yesterdayPrice = $("#yesterdayPrice").val();
    var param = {cookie:tempCookie,currentPrice:cprice};

    if(tempCookie==""||tempCookie==undefined){
        stopRefreshIndex(index);
        return;
    }
        $.ajax({
            url: url,
            data: param,
            dataType: 'json',
            success: function(data){
                $("#w"+index).prepend("<span>"+data.msg+"</span></br>");
                if($("#maxPrice").val()==undefined || $("#maxPrice").val()=='' || $("#maxPrice").val()=='null'){
                    $("#maxPrice").val(data.data.price);
                }else if(data.data.price !='null' && data.data.price != $("#maxPrice").val()){
                    $("#maxPrice").val(data.data.price);
                    if($("#maxPrice").val()!='null'){
                        commitAllOrder(12,'/admin/leo/commit');
                        $('#commitStatus').text('已提交');
                    }
                }
                var tempFlag = $("#refreshFlag"+index).val();
                if(tempFlag == "start"){
                    console.log('refreshPrice...');
                    // refreshPrice(index ,url);
                }
            },
            error: function(data){
                $("#w"+index).prepend("<span>失败："+data.msg+"</span></br>");
                if($("#maxPrice").val()==undefined || $("#maxPrice").val()=='' || $("#maxPrice").val()=='null'){
                    $("#maxPrice").val(data.data.price);
                }else if(data.data.price !='null' && data.data.price != $("#maxPrice").val()){
                    $("#maxPrice").val(data.data.price);
                    if($("#maxPrice").val()!='null'){
                        commitAllOrder(12,'/admin/leo/commit');
                    }
                }
                var tempFlag = $("#refreshFlag"+index).val();
                if(tempFlag == "start"){
                    console.log('refreshPrice...');
                    // refreshPrice(index ,url);
                }
            }
        });


}

function submitForm(){
    $('#ff').form('submit');
}
function clearForm(index){
    $("#w"+index).html("");
}

function stopRefresh(index){


        $('#refreshFlag'+index).val('stop');
        $('#status'+index).text('已暂停');

}

function startRefresh(index) {

    $('#refreshFlag'+index).val('start');
    $('#status'+index).text('已启动');
    refreshPrice(index,'/admin/leo/price');

}

function stopRefreshAll(index){
    for(var m=0;m<index;m++){
        $('#refreshFlag'+m).val('stop');
        $('#status'+m).text('已暂停');
    }
}

function stopRefreshIndex(index){
        $('#refreshFlag'+index).val('stop');
        $('#status'+index).text('已暂停');
}
