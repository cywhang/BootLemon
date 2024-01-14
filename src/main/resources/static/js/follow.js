function changeFollow(member_Id) {
    console.log("[팔로우, 언팔로우 - 2]follow.js로 옴");

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
        	console.log("ajax요청 성공");
        },
	    error : function(request,status,error){
	        alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
		}
    });
}
