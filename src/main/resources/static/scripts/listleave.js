function loadDetail(i){
    //alert($(i).attr("uuid"));
    $.post("http://127.0.0.1:8080/showleave","leaveuuid="+$(i).attr("uuid"),function(j){
        $(".detail").html(j)})
}