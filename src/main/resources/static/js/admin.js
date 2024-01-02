// 관리자가 회원 삭제
function delMem(member_Id){
	console.log(member_Id);
	if(confirm('삭제 하시겠습니까?')) {
		window.location.href = "/blue/member_Delete_By_Admin?member_Id=" + member_Id;
		console.log(member_Id);
	} else {
		
	}
}