function changeFollow(member_Id) {

    // 요청 바디에 전송할 데이터 설정
    var data = {
        member_Id: member_Id
    };

    $.ajax({
        url: "/changeFollow",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(data),
        success: function(response) {
        },
	    error : function(request,status,error){
	        alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
		}
    });
}
