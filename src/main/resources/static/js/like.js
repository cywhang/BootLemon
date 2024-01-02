// 뉴스피드 게시글의 좋아요 스크립트
function toggleLike(post_Seq) {
    console.log("[좋아요 - 1] like.js의 toggleLike로 옴");
    var likeImage = document.querySelectorAll(".likeImage_" + post_Seq); // post_Seq에 따라 동적인 좋아요 이미지
    var isLiked = (likeImage.length > 0 && likeImage[0].dataset.liked === "true"); // 좋아요 버튼을 누르는 시점의 좋아요 상태 (false, ture)
    var likeCount = document.querySelectorAll(".post_Like_Count_" + post_Seq); // 좋아요 카운트 출력
    
    // 이미지 경로에 따라 이미지 변경
    if (isLiked) {
    	for(var i=0; i<likeImage.length; i++){
    		likeImage[i].src = "img/like.png";
    		likeCount[i].innerText = parseInt(likeCount[i].innerText) - 1; // 좋아요 숫자 감소
    	}
    } else {
    	for(var i=0; i<likeImage.length; i++){
    		likeImage[i].src = "img/unlike.png";
    		likeCount[i].innerText = parseInt(likeCount[i].innerText) + 1; // 좋아요 숫자 증가
    	}
    }
    // 동시에 값 설정
    for (var i = 0; i < likeImage.length; i++) {
      likeImage[i].dataset.liked = !isLiked;
    }
    changeLike(post_Seq);
}



function changeLike(post_Seq) {
    console.log("[좋아요 - 2] like.js의 changeLike로 옴");

    // 요청 바디에 전송할 데이터 설정
    var data = {
        post_Seq: post_Seq
    };

    console.log(data);

    $.ajax({
        url: "changeLike",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(data)
    });
}

// 댓글 좋아요
function toggleReplyLike(post_Seq, reply_Seq) {
	console.log("[미리보기 댓글 좋아요 - 1] like.js의 togglReplyLike로 옴");
	console.log("[미리보기 댓글 좋아요 - 1.5] post_Seq : " + post_Seq + " reply_Seq : " + reply_Seq)
    var likeReplyImage = document.querySelectorAll(".likeReplyImage_" + reply_Seq);
    var isReplyLiked = (likeReplyImage.length > 0 && likeReplyImage[0].dataset.liked === "true");  // 좋아요 상태를 나타내는 변수
    var likeReplyCount = document.querySelectorAll(".reply_Like_Count_" + reply_Seq); // 좋아요 카운트 출력
    
    // 이미지 경로에 따라 이미지 변경
    if (isReplyLiked) {
    	for(var i=0; i<likeReplyImage.length; i++){
    		likeReplyImage[i].src = "img/like.png";
    		likeReplyCount[i].innerText = parseInt(likeReplyCount[i].innerText) - 1; // 좋아요 숫자 감소
    	}
    } else {
    	for(var i=0; i<likeReplyImage.length; i++){
    		likeReplyImage[i].src = "img/unlike.png";
    		likeReplyCount[i].innerText = parseInt(likeReplyCount[i].innerText) + 1; // 좋아요 숫자 증가
    	}
    }
    // 동시에 값 설정
    for (var i = 0; i < likeReplyImage.length; i++) {
    	likeReplyImage[i].dataset.liked = !isReplyLiked;
    }
    
    changeReplyLike(post_Seq, reply_Seq);
    
}

function changeReplyLike(post_Seq, reply_Seq) {
	console.log("[미리보기 댓글 좋아요 - 2] like.js의 changeReplyLike로 옴");
    // 요청 바디에 전송할 데이터 설정
    var data = {
    	post_Seq: post_Seq,
        reply_Seq: reply_Seq
    };

    console.log(data);

    $.ajax({
        url: "changeReplyLike",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(data)
    });
}
