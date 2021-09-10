function showpwdchangebox(){
    myConfirm("修改密码",
        "<div id='origpass'>原密码：<input/></div><div id='newpass'>新密码：<input/></div>",
        function(i){
        if(i){
            $.ajaxSettings.async = false;
            //拼接请求
            var data = "userno=";
            data += $("#userno").html();
            data += "&origpass=";
            data += origpass;
            data += "&newpass=";
            data += newpass;
            console.log(data);
            var resp;
            resp=($.post("/updatepassword", data).responseText);
            if(resp=="OK")
                alert("修改成功！");
            else
                //由于ID不可能出错，因此一定是原密码错误。
                alert("修改失败，原密码错误！");
            $.ajaxSettings.async = true;
        }
    });
}