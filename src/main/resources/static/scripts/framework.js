setInterval(function(){   
        var date = new Date();   
        var year = date.getFullYear();    //获取当前年份   
        var mon = date.getMonth()+1;      //获取当前月份   
        var da = date.getDate();          //获取当前日   
        var day = date.getDay();          //获取当前星期几   
        var h = date.getHours();          //获取小时   
        var m = date.getMinutes();        //获取分钟   
        var s = date.getSeconds();        //获取秒
	var dayC;
        switch(day){
		case 1:dayC='一';break;
		case 2:dayC='二';break;
		case 3:dayC='三';break;
		case 4:dayC='四';break;
		case 5:dayC='五';break;
		case 6:dayC='六';break;
		case 0:dayC='天';break;
	}
          document.getElementById('date').innerHTML = '当前时间:'+year+'年'+mon+'月'+da+'日'+'星期'+dayC+' '+h+':'+(m-10<0?'0':'')+m+':'+(s-10<0?'0':'')+s;  
        },1000)
//将网页加载到主框架中
function loadWebURL(url){
    $.get(url+sessionString,function(e){
        $(".right .content").html(e);
    });
}