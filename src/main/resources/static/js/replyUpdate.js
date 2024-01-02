
// 
function replyUpdate(post_Seq, reply_Seq){
	
	var data = {
			post_Seq : post_Seq,
			reply_Seq : reply_Seq
	};
	
	$.ajax({
		url : "deleteReply",
		type : "POST",
		dataType: "json",
		data : data, // post_Seq, reply_Seq 를 보냄
		
		
		/* 댓글 작성 완료 후 모달창의 댓글 리스트 다시 그려주기  */
		success : function(response) {
			console.log("딜리트 ajax응답 성공");
	  
}



























































