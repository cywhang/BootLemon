
// 게시글 상세보기 모달창 1
function modalseq(post_Seq) {
	// 요청 바디에 전송할 데이터 설정
	var data = { 
		post_Seq : post_Seq
	};
	$.ajax({
		url : "modal",
		type : "GET",
		dataType: "json",
		data : data, // post_Seq를 보냄
		
		success : function(response) {
			console.log("ajax응답 성공");
			console.log(response);    // ajax요청으로 응답받은 값
			// response로 받은 dataMap을 사용할수있도록 vo, list 타입으로 꺼내어 준다.
			var post = response.post; // 게시글 정보
		    var replies = response.replies; // 댓글 리스트
		    var hashtag = response.hashtag; // 게시글 해시태그
		    var profileMap = response.profile; // 전체 회원의 프로필 이미지
		    var member_Id = response.member_Id; // 세션아이디
			
		    // 1. 게시글 상세정보를 그려주는 컨테이너들
		    // 1-1. 프로필 이미지를 그려주는 컨테이너
		    var profileImgContainer = $('#profileImgContainer');
		    profileImgContainer.empty();
		    var image = $('<img>').attr('src', 'img/uploads/profile/' + profileMap[post.member_Id]).addClass('img-fluid rounded-circle user-img').attr('alt', 'profile-img');
		    var imgLink = $('<a>').attr('href', 'profile?member_Id=' + post.member_Id).css('text-decoration', 'none').append(image);
		    $('#profileImgContainer').append(imgLink);
		    
		    
		    // 1-2. 작성자 아이디를 그려주는 컨테이너
		    var writerContainer = $('#writerContainer');
		    writerContainer.empty();
		    var h6Element = $('<h6>').addClass('fw-bold text-body mb-0').text(post.member_Id);
		    var writerLink = $('<a>').attr('href', 'profile?member_Id=' + post.member_Id).css('text-decoration', 'none').append(h6Element);
		    $('#writerContainer').append(writerLink);
		    
		    
		    // 1-3. 작성자 아이디(small)를 그려주는 컨테이너
		    var smallWriterContainer = $('#smallWriterContainer');
		    smallWriterContainer.empty();
		    var pElement = $('<p>').addClass('text-muted mb-0 small').text(post.member_Id);
		    var smallWriterLink = $('<a>').attr('href', 'profile?member_Id=' + post.member_Id).css('text-decoration', 'none').append(pElement);
		    $('#smallWriterContainer').append(smallWriterLink);
		    
		    
		    // 1-4. 댓글수를 그려주는 컨테이너
		    var replyContainer = $('#replyContainer');
		    replyContainer.empty();
		    var replycount = $('<div>').text(post.post_Reply_Count);
		    $('#replyContainer').append(replycount);
		    
		    
		    // 1-5. 좋아요 버튼과 좋아요 카운트를 그려주는 컨테이너
		    var likeGroupDiv = $('#likeImage');
		    likeGroupDiv.empty();
		    
		    var likebutton = $('<button>').attr('type', 'button').css({ border: 'none', 'background-color': 'white' })
		    .on('click', function() {
		    	toggleLike(post.post_Seq);
		    });
		    
		    // 좋아요 여부 'n'
		    if(post.post_LikeYN === 'N'){
			    var likeimg = $('<img>')
			    .attr({
			      class: 'likeImage_' + post.post_Seq,
			      src: 'img/like.png',
			      width: '20px',
			      height: '20px',
			      'data-liked': 'false'
			    });
			// 좋아요 여부 'y'
		    } else{ 
		    	var likeimg = $('<img>')
			    .attr({
			      class: 'likeImage_' + post.post_Seq,
			      src: 'img/unlike.png',
			      width: '20px',
			      height: '20px',
			      'data-liked': 'true'
			    });
		    }
		    
		    var likeCount = $('<p>')
		    .attr('class', 'post_Like_Count_' + post.post_Seq)
		    .css({ display: 'inline', 'margin-left': '3px', 'font-size': '13px' })
		    .text(post.post_Like_Count);
		    
		    
		    likebutton.append(likeimg);
		    $('#likeImage').append(likebutton, likeCount);
		    
		    
		    
		    
		    // 1-6. 이미지 슬라이드를 적용할 컨테이너 생성
		    // 이미지를 지정하는 컨테이너
		    var sliderContainer = $('.image-slider .carousel-inner');
		    sliderContainer.empty(); // 기존의 이미지 슬라이더 내용을 비웁니다.
		    // 슬라이드의 인덱스 버튼을 지정하는 컨테이너
		    var indexButton = $('.image-slider .carousel-indicators');
		    indexButton.empty(); 
		    // 슬라이드의 좌, 우 이동버튼을 지정하는 컨테이너
		    var arrowButton = $('.image-slider .arrow-button');
		    arrowButton.empty();
		    
		    
		    // 이미지 슬라이더 아이템 생성 및 추가 
		    // 하단의 인덱스버튼, 좌우 이동버튼 유무 고려
		    // 이미지가 0개 일때는 댓글 리스트만 출력 (모달창2 출력)
		    
		    // 이미지가 1개일때
		    if (post.post_Image_Count === 1){
		    	 var image = $('<img>').attr('src', 'img/uploads/post/' + post.post_Seq + '-1.png').addClass('d-block w-100').attr('alt', '...');
			      sliderContainer.append(image);
		    // 이미지가 2 ~ 4개일때
		    } else {
		    	// 1. sliderContainer
			    for (var i = 1; i <= post.post_Image_Count; i++) {
			      var imageItem = $('<div>').addClass('carousel-item');
			      if (i === 1) {
			        imageItem.addClass('active');
			      }
			      var image = $('<img>').attr('src', 'img/uploads/post/' + post.post_Seq + '-' + i + '.png').addClass('d-block w-100').attr('alt', '...');
			      imageItem.append(image);
			      sliderContainer.append(imageItem);
			    }
	
			    // 이미지 슬라이더 초기화
			    var carousel = new bootstrap.Carousel($('#carouselExampleIndicators')[0]);
			    
			    // 2. indexButton
			    for (var i = 0; i < post.post_Image_Count; i++) {
		    	  var button = $('<button>')
		    	    .attr('type', 'button')
		    	    .attr('data-bs-target', '#carouselExampleIndicators')
		    	    .attr('data-bs-slide-to', i)
		    	    .attr('aria-label', 'Slide ' + (i + 1))
		    	    .css('background-color', '#0a58ca'); // 스타일 변경 부분

		    	  if (i === 0) {
		    	    button.addClass('active').attr('aria-current', 'true');
		    	  }

		    	  indexButton.append(button);
		    	}
			    
			    // 3. arrowButton
			    var arrowButtonContainer = $('.arrow-button');
			    arrowButtonContainer.empty(); // 기존의 버튼 내용을 비웁니다.

			    var prevButton = $('<button>')
			      .addClass('carousel-control-prev')
			      .attr('type', 'button')
			      .attr('data-bs-target', '#carouselExampleIndicators')
			      .attr('data-bs-slide', 'prev');

			    var prevIcon = $('<span>').addClass('carousel-control-prev-icon').attr('aria-hidden', 'true');
			    var prevText = $('<span>').addClass('visually-hidden').text('Previous');

			    prevButton.append(prevIcon, prevText);
			    arrowButtonContainer.append(prevButton);

			    var nextButton = $('<button>')
			      .addClass('carousel-control-next')
			      .attr('type', 'button')
			      .attr('data-bs-target', '#carouselExampleIndicators')
			      .attr('data-bs-slide', 'next');

			    var nextIcon = $('<span>').addClass('carousel-control-next-icon').attr('aria-hidden', 'true');
			    var nextText = $('<span>').addClass('visually-hidden').text('Next');

			    nextButton.append(nextIcon, nextText);
			    arrowButtonContainer.append(nextButton);
			    
		    }
		    
		    
		    // 1-7. 게시글 내용 그려주는 컨테이너 
		    var modalContent = $("#imgModalContent");
		    modalContent.empty();
		    
		    var Content = $('<h5>').html(post.post_Content.replace(/\n/g, "<br>"));
		    	
		    modalContent.append(Content);   
		    
		    
		    // 1-8. 게시글 해시태그 그려주는 컨테이너
		    var modalHashtag = $("#imgModalHashtag");
		    modalHashtag.empty();

		    for (var i = 0; i < hashtag.length; i++) {
		      var tagContent = '#' + hashtag[i].tag_Content; // '#' 문자와 tag_Content를 합친 문자열 생성

		      var hashtagElement = $('<span>').text(tagContent).css('color', 'blue'); // span 태그로 감싸고 파란색으로 스타일링

		      modalHashtag.append(hashtagElement);
		      
		      // 띄어쓰기
		      var nbsp = '&nbsp;';
		      modalHashtag.append(nbsp);

		    }
		    
		    // 2. 댓글 리스트를 그려주는 컨테이너 생성
			var replyListContainer = $('#replyListContainer');
			replyListContainer.empty(); // 기존에 그렸던 댓글 리스트들을 비워내주는 작업
			for (var i = 0; i < replies.length; i++) {
			  var replyItem = $('<div>').addClass('d-flex mb-2');
			  
			  var profileImg = $('<img>').attr('src', 'img/uploads/profile/' + profileMap[replies[i].member_Id]).addClass('img-fluid rounded-circle').attr('alt', 'profile-img');
			  var imgLink = $('<a>').attr('href', 'profile?member_Id=' + replies[i].member_Id).css('text-decoration', 'none').append(profileImg);
			  replyItem.append(imgLink);                         
			  
			  var replyContentWrapper = $('<div>').addClass('ms-2 small');
			  
			  var chatText = $('<div>').addClass('bg-light px-3 py-2 rounded-4 mb-1 chat-text');
			  
			  var memberName = $('<p>').addClass('fw-500 mb-0').text(replies[i].member_Id);
			  var replyContent = $('<span>').addClass('text-muted').text(replies[i].reply_Content);
			  
			  chatText.append(memberName);
			  chatText.append(replyContent);
			  
			  replyContentWrapper.append(chatText);
			  
			  // 댓글 좋아요 버튼, 좋아요 카운트, 댓글 작성일 
			  let post_Seq = replies[i].post_Seq;
			  let reply_Seq = replies[i].reply_Seq;
			  
			  let likeLink = $('<button>').attr('type', 'button').css({ border: 'none', 'background-color': 'white'})
			  	.addClass('thumbs')
			    .on('click', function() {
			    	toggleReplyLike(post_Seq, reply_Seq);
			    });
			  
			  if(replies[i].reply_LikeYN === 'N'){
				  var replyLikeImage = $('<img>').attr({
					  class: 'likeReplyImage_' + replies[i].reply_Seq,
					  src: 'img/like.png',
					  'data-liked': 'false'
					});
			  } else {
				  var replyLikeImage = $('<img>').attr({
					  class: 'likeReplyImage_' + replies[i].reply_Seq,
					  src: 'img/unlike.png',
					  'data-liked': 'true'
					});
			  }
			  likeLink.append(replyLikeImage);
			  replyContentWrapper.append(likeLink);
			  
			  // 띄어쓰기
			  var nbsp = '&nbsp;';
			  replyContentWrapper.append(nbsp);
			  
			  
			  // 좋아요 카운트
			  var p = $('<p>')
				.attr('class', 'reply_Like_Count_' + replies[i].reply_Seq)
				.css({ display: 'inline', 'margin-left': '1px', 'font-size': '10px' })
				.text(replies[i].reply_Like_Count);
			  replyContentWrapper.append(p);
			  
			  // 띄어쓰기
			  replyContentWrapper.append(nbsp);	
			  replyContentWrapper.append(nbsp);	
			  replyContentWrapper.append(nbsp);	
			  
			  // 댓글 작성일
			  var timestamp = $('<span>').addClass('small text-muted').text(replies[i].reply_WhenDid);
			  
			  replyContentWrapper.append(timestamp);
			  
			  replyContentWrapper.append(nbsp);
			  
			  if(replies[i].member_Id === member_Id){
			  		// 댓글 삭제버튼
				  var deleteButton = $('<img>').addClass('replyDelete').attr('src', 'img/delete.png')
				    .css('cursor', 'pointer')
				    .on('click', function() {
				    	var result = confirm("해당 댓글을 삭제하시겠습니까?");
				        if (!result) {
				          return false;
				        } else {
				          replyDelete(post_Seq, reply_Seq);
				        }
				    });
				  
				  // 띄어쓰기
				  var nbsp = '&nbsp;';
				  replyContentWrapper.append(nbsp);
				  
				  // 댓글 수정 버튼
				  var updateButton = $('<img>').addClass('replyUpdate').attr('src', 'img/update.png')
				    .css('cursor', 'pointer')
				    .on('click', function() {
				    	var result = confirm("해당 댓글을 수정하시겠습니까?");
				        if (!result) {
				          return false;
				        } else {
							replyeditView(post_Seq, reply_Seq);
				        }
				    });
				  
				  replyContentWrapper.append(updateButton);
				  replyContentWrapper.append(deleteButton);
			  }
			  
			  replyItem.append(replyContentWrapper);
			  replyListContainer.append(replyItem);
			}
			// 3. 댓글 작성 완료 버튼 컨테이너
			var postButton = $('#postButton');
			postButton.empty();
			// 버튼 생성
			var timestamp = $('<button>').addClass('bg-white border-0 text-primary ps-2 text-decoration-none').text('Post').attr('onclick', 'insertReply(' + post.post_Seq + ')');
			postButton.append(timestamp);
		},
		error : function(request,status,error){
	        alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
		}
	});
}


// 게시글 상세보기 모달창 2 (이미지 없는 모달창)
function replyModalseq(post_Seq) {
	// 요청 바디에 전송할 데이터 설정
	var data = {
		post_Seq : post_Seq
	};
	$.ajax({
		url : "replyModal",
		type : "GET",
		dataType: "json",
		data : data, // post_Seq를 보냄
		
		success : function(response) {
			console.log("ajax응답 성공");
			console.log(response);    // ajax요청으로 응답받은 값
			
			// response로 받은 dataMap을 사용할수있도록 vo, list 타입으로 꺼내어 준다.
			var post = response.post; // 게시글 정보
		    var replies = response.replies; // 댓글 리스트
		    var hashtag = response.hashtag; // 해시태그 리스트
		    var profileMap = response.profile; // 전체 회원의 프로필 이미지
		    var member_Id = response.member_Id; // 세션아이디
		   
		    // 1. 게시글 상세정보를 그려주는 컨테이너들
		    // 1-1. 프로필 이미지를 그려주는 컨테이너
		    var profileImgContainer = $('#profileImgContainer2');
		    profileImgContainer.empty();
		    var image = $('<img>').attr('src', 'img/uploads/profile/' + profileMap[post.member_Id]).addClass('img-fluid rounded-circle user-img').attr('alt', 'profile-img');
		    var imgLink = $('<a>').attr('href', 'profile?member_Id=' + post.member_Id).css('text-decoration', 'none').append(image);
		    $('#profileImgContainer2').append(imgLink);
		    
		    
		    // 1-2. 작성자 아이디를 그려주는 컨테이너
		    var writerContainer = $('#writerContainer2');
		    writerContainer.empty();
		    var h6Element = $('<h6>').addClass('fw-bold text-body mb-0').text(post.member_Id);
		    var writerLink = $('<a>').attr('href', 'profile?member_Id=' + post.member_Id).css('text-decoration', 'none').append(h6Element);
		    $('#writerContainer2').append(writerLink);
		    
		    
		    // 1-3. 작성자 아이디(small)를 그려주는 컨테이너
		    var smallWriterContainer = $('#smallWriterContainer2');
		    smallWriterContainer.empty();
		    var pElement = $('<p>').addClass('text-muted mb-0 small').text(post.member_Id);
		    var smallWriterLink = $('<a>').attr('href', 'profile?member_Id=' + post.member_Id).css('text-decoration', 'none').append(pElement);
		    $('#smallWriterContainer2').append(smallWriterLink);
		    
		    
		    // 1-4. 게시글의 댓글수를 그려주는 컨테이너
		    var replyContainer = $('#replyContainer2');
		    replyContainer.empty();
		    var replycount = $('<div>').text(post.post_Reply_Count);
		    $('#replyContainer2').append(replycount);
		    
		    
		    // 1-5. 좋아요 버튼과 좋아요 카운트를 그려주는 컨테이너
		    var likeGroupDiv = $('#likeImage2');
		    likeGroupDiv.empty();
		    
		    var likebutton = $('<button>').attr('type', 'button').css({ border: 'none', 'background-color': 'white' })
		    .addClass('thumbs')
		    .on('click', function() {
		    	toggleLike(post.post_Seq);
		    });
		    
		    console.log("LikeYN: " + post.post_LikeYN);
		    // 좋아요 여부 'n'
			if(post.post_LikeYN === 'N'){
			    var likeimg = $('<img>')
			    .attr({
			      class: 'likeImage_' + post.post_Seq,
			      src: 'img/like.png',
			      width: '20px',
			      height: '20px',
			      'data-liked': 'false'
			    });
			// 좋아요 여부 'y'
			} else{ 
				var likeimg = $('<img>')
			    .attr({
			      class: 'likeImage_' + post.post_Seq,
			      src: 'img/unlike.png',
			      width: '20px',
			      height: '20px',
			      'data-liked': 'true'
			    });
			}
		    
		    var likeCount = $('<p>')
		    .attr('class', 'post_Like_Count_' + post.post_Seq)
		    .css({ display: 'inline', 'margin-left': '3px', 'font-size': '13px' })
		    .text(post.post_Like_Count);
		    
		    
		    likebutton.append(likeimg);
		    $('#likeImage2').append(likebutton, likeCount);
		    
		    // 1-6. 게시글 내용 그려주는 컨테이너 
		    var modalContent = $("#modalContent");
		    modalContent.empty();
		    
		    var Content = $('<h4>').html(post.post_Content.replace(/\n/g, "<br>"));
		    	
		    modalContent.append(Content);   
		    
		    
		    // 1-7. 게시글 해시태그 그려주는 컨테이너
		    var modalHashtag = $("#modalHashtag");
		    modalHashtag.empty();

		    for (var i = 0; i < hashtag.length; i++) {
		      var tagContent = '#' + hashtag[i].tag_Content; // '#' 문자와 tag_Content를 합친 문자열 생성

		      var hashtagElement = $('<span>').text(tagContent).css('color', 'blue'); // span 태그로 감싸고 파란색으로 스타일링

		      modalHashtag.append(hashtagElement);
		      // 띄어쓰기
		      var nbsp = '&nbsp;';
		      modalHashtag.append(nbsp);

		    }
		    
		    // 2. 댓글 리스트를 그려주는 컨테이너 생성
			var replyListContainer = $('#replyListContainer2');
			replyListContainer.empty(); // 기존에 그렸던 댓글 리스트들을 비워내주는 작업
			for (var i = 0; i < replies.length; i++) {
			  var replyItem = $('<div>').addClass('d-flex mb-2');
			  
			  var profileImg = $('<img>').attr('src', 'img/uploads/profile/' + profileMap[replies[i].member_Id]).addClass('img-fluid rounded-circle').attr('alt', 'profile-img');
			  var imgLink = $('<a>').attr('href', 'profile?member_Id=' + replies[i].member_Id).css('text-decoration', 'none').append(profileImg);
			  replyItem.append(imgLink);                         
			  
			  var replyContentWrapper = $('<div>').addClass('ms-2 small');
			  
			  var chatText = $('<div>').addClass('bg-light px-3 py-2 rounded-4 mb-1 chat-text');
			  
			  var memberName = $('<p>').addClass('fw-500 mb-0').text(replies[i].member_Id);
			  var replyContent = $('<span>').addClass('text-muted').text(replies[i].reply_Content);
			  
			  chatText.append(memberName);
			  chatText.append(replyContent);
			  
			  replyContentWrapper.append(chatText);
			  
			  // 댓글 좋아요 버튼, 좋아요 카운트, 댓글 작성일 
			  let post_Seq = replies[i].post_Seq;
			  let reply_Seq = replies[i].reply_Seq;
			  
			  let likeLink = $('<button>').attr('type', 'button').css({ border: 'none', 'background-color': 'white'})
			  	.addClass('thumbs')
			    .on('click', function() {
			    	toggleReplyLike(post_Seq, reply_Seq);
			    });
			  
			  if(replies[i].reply_LikeYN === 'N'){
				  var replyLikeImage = $('<img>').attr({
					  class: 'likeReplyImage_' + replies[i].reply_Seq,
					  src: 'img/like.png',
					  'data-liked': 'false'
					});
			  } else {
				  var replyLikeImage = $('<img>').attr({
					  class: 'likeReplyImage_' + replies[i].reply_Seq,
					  src: 'img/unlike.png',
					  'data-liked': 'true'
					});
			  }
			  likeLink.append(replyLikeImage);
			  replyContentWrapper.append(likeLink);
			  
			  // 띄어쓰기
			  var nbsp = '&nbsp;';
			  replyContentWrapper.append(nbsp);

			  
			  
			  // 좋아요 카운트
			  var p = $('<p>')
				.attr('class', 'reply_Like_Count_' + replies[i].reply_Seq)
				.css({ display: 'inline', 'margin-left': '1px', 'font-size': '10px' })
				.text(replies[i].reply_Like_Count);
			  replyContentWrapper.append(p);
			  
			  // 띄어쓰기
			  replyContentWrapper.append(nbsp);
			  replyContentWrapper.append(nbsp);
			  replyContentWrapper.append(nbsp);
			  
			  // 댓글 작성일
			  var timestamp = $('<span>').addClass('small text-muted').text(replies[i].reply_WhenDid);
			  replyContentWrapper.append(timestamp);
			  replyContentWrapper.append(nbsp);
			  
			  // 댓글 삭제 & 수정 버튼
			  if(replies[i].member_Id === member_Id){
				  // 댓글 삭제 버튼
				  var deleteButton = $('<img>').addClass('replyDelete').attr('src', 'img/delete.png')
				    .css('cursor', 'pointer')
				    .on('click', function() {
				    	var result = confirm("해당 댓글을 삭제하시겠습니까?");
				        if (!result) {
				          return false;
				        } else {
				          replyDelete2(post_Seq, reply_Seq);
				        }
				    });
				  
				  // 띄어쓰기
				  var nbsp = '&nbsp;';
				  replyContentWrapper.append(nbsp);
				  
				  // 댓글 수정 버튼
				  var updateButton = $('<img>').addClass('replyUpdate').attr('src', 'img/update.png')
				    .css('cursor', 'pointer')
				    .on('click', function() {
				    	var result = confirm("해당 댓글을 수정하시겠습니까?");
				        if (!result) {
				          return false;
				        } else {
				          replyUpdate2(post_Seq, reply_Seq);
				        }
				    });
				  
				  replyContentWrapper.append(updateButton);
				  replyContentWrapper.append(deleteButton);
			  }
			  
			  replyItem.append(replyContentWrapper);
			  replyListContainer.append(replyItem);
			}

			// 3. 댓글 작성 완료 버튼 컨테이너
			var postButton = $('#postButton2');
			postButton.empty();
			
			// 버튼 생성
			var timestamp = $('<button>').addClass('bg-white border-0 text-primary ps-2 text-decoration-none').text('Post').attr('onclick', 'insertReply2(' + post.post_Seq + ')');
			postButton.append(timestamp);
		},
		error : function(request,status,error){
	        alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
		}
	});
}

// 게시글 상세보기 모달창 1 인서트 처리
function insertReply(post_Seq){
	
	// 모달창의 댓글 입력창에 담긴 값(value)을 가져온다.
	var input = document.getElementById("inputContent");
	var reply_Content = input.value;
	
	// 입력한 댓글 내용이 비어 있는 경우 처리
	if (reply_Content.trim() === "") {
		alert("내용을 입력해주세요."); 
		return; 
	}
	// 해당 모달창의 post_Seq와 입력한 댓글 내용을 data배열에 담는 과정
	var data = {
			post_Seq : post_Seq,
			reply_Content : reply_Content
	};
	
	// 값 저장 처리
	$.ajax({
		url : "insertReply",
		type : "GET",
		dataType: "json",
		data : data, // post_Seq를 보냄
		
		/* 댓글 작성 완료 후 모달창의 댓글 리스트 다시 그려주기  */
		success : function(response) {
			console.log("인서트 ajax응답 성공");
			// 1. 컨트롤러에서 넘겨받은 댓글 리스트(replylist) 꺼내기
			var post = response.postInfo;
			var replies = response.replies;
			var profileMap = response.profile;
			var member_Id = response.member_Id; // 세션아이디
			
			// 2. 댓글 리스트를 그려주는 컨테이너 생성 
			var replyListContainer = $('#replyListContainer');
			replyListContainer.empty();  // 기존에 그렸던 댓글 리스트들을 비워내주는 작업
			for (var i = 0; i < replies.length; i++) {
			  var replyItem = $('<div>').addClass('d-flex mb-2');
			  
			  var profileImg = $('<img>').attr('src', 'img/uploads/profile/' + profileMap[replies[i].member_Id]).addClass('img-fluid rounded-circle').attr('alt', 'profile-img');
			  replyItem.append(profileImg);                         
			  
			  var replyContentWrapper = $('<div>').addClass('ms-2 small');
			  
			  var chatText = $('<div>').addClass('bg-light px-3 py-2 rounded-4 mb-1 chat-text');
			  
			  var memberName = $('<p>').addClass('fw-500 mb-0').text(replies[i].member_Id);
			  var replyContent = $('<span>').addClass('text-muted').text(replies[i].reply_Content);
			  
			  chatText.append(memberName);
			  chatText.append(replyContent);
			  
			  replyContentWrapper.append(chatText);
			  
			  // 댓글 좋아요 버튼, 좋아요 카운트, 댓글 작성일 
			  let post_Seq = replies[i].post_Seq;
			  let reply_Seq = replies[i].reply_Seq;
			  
			  let likeLink = $('<button>').attr('type', 'button').css({ border: 'none', 'background-color': 'white'})
			  	.addClass('thumbs')
			    .on('click', function() {
			    	toggleReplyLike(post_Seq, reply_Seq);
			    });
			  
			  if(replies[i].reply_LikeYN === 'N'){
				  var replyLikeImage = $('<img>').attr({
					  class: 'likeReplyImage_' + replies[i].reply_Seq,
					  src: 'img/like.png',
					  'data-liked': 'false'
					});
			  } else {
				  var replyLikeImage = $('<img>').attr({
					  class: 'likeReplyImage_' + replies[i].reply_Seq,
					  src: 'img/unlike.png',
					  'data-liked': 'true'
					});
			  }
			  likeLink.append(replyLikeImage);
			  replyContentWrapper.append(likeLink);
			  
			  // 띄어쓰기
			  var nbsp = '&nbsp;';
			  replyContentWrapper.append(nbsp);
			  
			  
			  // 좋아요 카운트
			  var p = $('<p>')
				.attr('class', 'reply_Like_Count_' + replies[i].reply_Seq)
				.css({ display: 'inline', 'margin-left': '1px', 'font-size': '10px' })
				.text(replies[i].reply_Like_Count);
			  replyContentWrapper.append(p);
			  
			  // 띄어쓰기
			  replyContentWrapper.append(nbsp);
			  replyContentWrapper.append(nbsp);
			  replyContentWrapper.append(nbsp);
			  
			  // 댓글 작성일
			  var timestamp = $('<span>').addClass('small text-muted').text(replies[i].reply_WhenDid);
			  replyContentWrapper.append(timestamp);
			  replyContentWrapper.append(nbsp);
			  
			  // 댓글 삭제 & 수정 버튼
			  if(replies[i].member_Id === member_Id){
				  // 댓글 삭제버튼
				  var deleteButton = $('<img>').addClass('replyDelete').attr('src', 'img/delete.png')
				    .css('cursor', 'pointer')
				    .on('click', function() {
				    	var result = confirm("해당 댓글을 삭제하시겠습니까?");
				        if (!result) {
				          return false;
				        } else {
				          replyDelete(post_Seq, reply_Seq);
				        }
				    });
				  
				  // 띄어쓰기
				  var nbsp = '&nbsp;';
				  replyContentWrapper.append(nbsp);
				  
				  // 댓글 수정 버튼
				  var updateButton = $('<img>').addClass('replyUpdate').attr('src', 'img/update.png')
				    .css('cursor', 'pointer')
				    .on('click', function() {
				    	var result = confirm("해당 댓글을 수정하시겠습니까?");
				        if (!result) {
				          return false;
				        } else {
				          replyUpdate(post_Seq, reply_Seq);
				        }
				    });
				  
				  replyContentWrapper.append(updateButton);
				  replyContentWrapper.append(deleteButton);
			  }
			  
			  replyItem.append(replyContentWrapper);
			  replyListContainer.append(replyItem);
			}
			
			// 3. 댓글 작성 완료 후 댓글 카운트 추가
			var replyCountContainer = $('#replyContainer');
			replyCountContainer.empty();
			var replycount = $('<div>').text(post.post_Reply_Count);
			$('#replyContainer').append(replycount);
			
			// 4. 댓글 작성 완료 후 입력 창 비우기
			var input = document.getElementById("inputContent");
			input.value = "";
			
		},error : function(request,status,error){
	        alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
		}
	});
}


//게시글 상세보기 모달창 2 인서트 처리
function insertReply2(post_Seq){
	
	// 모달창의 댓글 입력창에 담긴 값(value)을 가져온다.
	var input2 = document.getElementById("inputContent2");
	var reply_Content = input2.value;
	
	// 입력한 댓글 내용이 비어 있는 경우 처리
	if (reply_Content.trim() === "") {
		alert("내용을 입력해주세요."); 
		return; 
	}
	// 해당 모달창의 post_Seq와 입력한 댓글 내용을 data배열에 담는 과정
	var data = {
			post_Seq : post_Seq,
			reply_Content : reply_Content
	};
	// 값 저장 처리
	$.ajax({
		url : "insertReply",
		type : "GET",
		dataType: "json",
		data : data, // post_Seq를 보냄
		
		/* 댓글 작성 완료 후 모달창의 댓글 리스트 다시 그려주기  */
		success : function(response) {
			console.log("인서트2 ajax응답 성공");
			// 1. 컨트롤러에서 넘겨받은 댓글 리스트(replylist) 꺼내기
			var post = response.postInfo;
			var replies = response.replies;
			var profileMap = response.profile;
			var member_Id = response.member_Id; // 세션아이디
			
			// 2. 댓글 리스트를 그려주는 컨테이너 생성
			var replyListContainer2 = $('#replyListContainer2');
			replyListContainer2.empty();

			for (var i = 0; i < replies.length; i++) {
			  var replyItem = $('<div>').addClass('d-flex mb-2');

			  var profileImg = $('<img>').attr('src', 'img/uploads/profile/' + profileMap[replies[i].member_Id]).addClass('img-fluid rounded-circle').attr('alt', 'profile-img');
			  replyItem.append(profileImg);                         
			  
			  var replyContentWrapper = $('<div>').addClass('ms-2 small');
			  
			  var chatText = $('<div>').addClass('bg-light px-3 py-2 rounded-4 mb-1 chat-text');
			  
			  var memberName = $('<p>').addClass('fw-500 mb-0').text(replies[i].member_Id);
			  var replyContent = $('<span>').addClass('text-muted').text(replies[i].reply_Content);
			  
			  chatText.append(memberName);
			  chatText.append(replyContent);
			  
			  replyContentWrapper.append(chatText);
			  
			  // 댓글 좋아요 버튼, 좋아요 카운트, 댓글 작성일 
			  let post_Seq = replies[i].post_Seq;
			  let reply_Seq = replies[i].reply_Seq;
			  
			  let likeLink = $('<button>').attr('type', 'button').css({ border: 'none', 'background-color': 'white'})
			    .addClass('thumbs')
			    .on('click', function() {
			    	toggleReplyLike(post_Seq, reply_Seq);
			    });

			  if(replies[i].reply_LikeYN === 'N'){
				  var replyLikeImage = $('<img>').attr({
					  class: 'likeReplyImage_' + replies[i].reply_Seq,
					  src: 'img/like.png',
					  'data-liked': 'false'
					});
			  } else {
				  var replyLikeImage = $('<img>').attr({
					  class: 'likeReplyImage_' + replies[i].reply_Seq,
					  src: 'img/unlike.png',
					  'data-liked': 'true'
					});
			  }
			  likeLink.append(replyLikeImage);
			  replyContentWrapper.append(likeLink);
			  
			  // 띄어쓰기
			  var nbsp = '&nbsp;';
			  replyContentWrapper.append(nbsp);
			  
			  // 좋아요 카운트
			  var p = $('<p>')
				.attr('class', 'reply_Like_Count_' + replies[i].reply_Seq)
				.css({ display: 'inline', 'margin-left': '1px', 'font-size': '10px' })
				.text(replies[i].reply_Like_Count);
			  replyContentWrapper.append(p);

			  // 띄어쓰기
			  replyContentWrapper.append(nbsp);
			  replyContentWrapper.append(nbsp);
			  replyContentWrapper.append(nbsp);
			  
			  // 댓글 작성일
			  var timestamp = $('<span>').addClass('small text-muted').text(replies[i].reply_WhenDid);
			  replyContentWrapper.append(timestamp);
			  replyContentWrapper.append(nbsp);
			  
			  // 댓글 삭제버튼
			  if(replies[i].member_Id === member_Id){
				  var deleteButton = $('<img>').addClass('replyDelete').attr('src', 'img/delete.png')
				    .css('cursor', 'pointer')
				    .on('click', function() {
				    	var result = confirm("해당 댓글을 삭제하시겠습니까?");
				        if (!result) {
				          return false;
				        } else {
				          replyDelete2(post_Seq, reply_Seq);
				        }
				    });
				  
				  // 띄어쓰기
				  var nbsp = '&nbsp;';
				  replyContentWrapper.append(nbsp);
				  
				  // 댓글 수정 버튼
				  var updateButton = $('<img>').addClass('replyUpdate').attr('src', 'img/update.png')
				    .css('cursor', 'pointer')
				    .on('click', function() {
				    	var result = confirm("해당 댓글을 수정하시겠습니까?");
				        if (!result) {
				          return false;
				        } else {
				          replyUpdate2(post_Seq, reply_Seq);
				        }
				    });
				  
				  replyContentWrapper.append(updateButton);
				  replyContentWrapper.append(deleteButton);
			  }
			  
			  replyItem.append(replyContentWrapper);
			  replyListContainer2.append(replyItem);

			}
			
			// 3. 댓글 작성 완료 후 댓글 카운트 추가
			var replyCountContainer = $('#replyContainer2');
			replyCountContainer.empty();
			var replycount = $('<div>').text(post.post_Reply_Count);
			$('#replyContainer2').append(replycount);
			
			// 4. 댓글 작성 완료 후 입력 창 비우기
			var input2 = document.getElementById("inputContent2");
			input2.value = "";

		},error : function(request,status,error){
	        alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
		}
	});
}

$('#closeModal').on('hidden.bs.modal', function () {
	  var hashtagContainer = document.getElementById('hashtagContainer');
	  if (hashtagContainer) {
	    var existingInput = hashtagContainer.querySelector('input');
	    if (existingInput) {
	      existingInput.tagify.destroy();
	      hashtagContainer.removeChild(existingInput);
	    }
	  }
	});

// edit view
function postEditView(post_Seq){
	
	var data = {
		post_Seq : post_Seq
	};
	console.log("postEditView의 post_Seq : ",data);
	$.ajax({
		url : "postEditView",
		type : "GET",
		dataType: "json",
		data : data, // post_Seq를 보냄
		
		success : function(response) {
			console.log("ajax응답 성공");
			console.log(response);    // ajax요청으로 응답받은 값
			// response로 받은 dataMap을 사용할수있도록 vo, list 타입으로 꺼내어 준다.
			var post = response.post; // 게시글 정보
			var folderPath = response.folderPath;
			
			
		    var hashList = response.hashList; 
		    var hashtags = hashList.map(function(tag) {
	    	  return tag.tag_Content;
	    	});
		    
		    // 1. 게시글 공개여부 
		    var checkboxContainer = $('#postPublicContainer');
	
			var checkbox = $('<input>').attr({
			  type: 'checkbox',
			  name: 'post_Public',
			  value: 'y'
			}).prop('checked', post.post_Public === 'y');
	
			checkboxContainer.empty().append(checkbox);
		    
			
		    // 2. 게시글 내용
		    var textareaContainer = $('#postContentContainer');
			
			var textarea = $('<textarea>').addClass('form-control rounded-5 border-0 shadow-sm')
			   .attr({
			     name: 'post_Content',
			     placeholder: 'Leave a comment here',
			     id: 'floatingTextarea2',
			     style: 'height: 200px',
			     maxlength: '300',
			     onkeypress : 'characterCheck(this)'
			}).text(post.post_Content);
			
			var label = $('<label>').attr('for', 'floatingTextarea2')
			  .addClass('h6 text-muted mb-0')
			  .text('게시글 내용');
			
			textareaContainer.empty().append(textarea, label);
		    
			
		    // 3. 게시글 해시태그
			var tagifyScript = document.createElement('script');
			tagifyScript.src = 'https://unpkg.com/@yaireo/tagify';
			tagifyScript.onload = function() {
			  var hashtagContainer = document.getElementById('hashtagContainer');
 
			  if (hashtagContainer) {  
			    // 기존에 생성된 입력 필드 및 플러그인 인스턴스 제거
			    var existingInput = hashtagContainer.querySelector('input');
			    if (existingInput) {
			      hashtagContainer.removeChild(existingInput);
			    } 

			    // 새로운 입력 필드 생성 및 초기화
			    var input = document.createElement('input');
			    input.setAttribute('name', 'post_Hashtag');
			    input.setAttribute('class', 'form-control rounded-5 border-0 shadow-sm');
			    input.setAttribute('placeholder', '해시태그: #없이 입력');
			    input.setAttribute('id', 'floatingTextarea2');
			    input.setAttribute('style', 'height: 50px');
			    input.setAttribute('maxlength', '20');
			    input.setAttribute('onkeypress', 'characterCheck(this)');
			    input.value = hashtags.join(',');

			    hashtagContainer.appendChild(input);

			    new Tagify(input);
			  }
			};

			document.head.appendChild(tagifyScript);

			// 4. 이미지 컨테이너
			$(document).ready(function() {
			  // 서버에서 가져온 게시글 이미지들의 URL 배열
			  var editFileArr = []; // 기존 업로드된 이미지를 담을 배열
			  var editFileNo = 0;   // 숫자 값으로 사용하기 위한 초기화
			  
			  var ImageCount = post.post_Image_Count;
			  console.log("해당 게시글의 이미지 카운트: ",ImageCount);
			  
			  if(ImageCount > 0){ // 해당 게시글에 이미지가 업로드 되어있을 경우
				  var imageUrls = [];
				  for(var i=1; i < ImageCount+1; i++){
					  var image = folderPath + post.post_Seq + "-" + i + ".png"
					  imageUrls.push(image);
				  }
	
				  // 이미지 입력창 및 미리보기 컨테이너를 동적으로 생성하여 부모 요소에 추가
				  var imageContainer = $("#editPreview");
				  imageContainer.empty();
				  
				  // 이미지 미리보기 컨테이너 생성
//				  var previewContainer = $('<ul id="editPreview" class="sortable"></ul>');
//				  imageContainer.empty().append(previewContainer);
	
				  
				  // 업로드한 이미지가 이미 있으면 미리보기 컨테이너에 이미지들 생성해주는 부분
				  // 서버에서 가져온 이미지들을 미리보기 컨테이너에 추가
				  for (var i = 0; i < ImageCount; i++) {
				    var imageUrl = imageUrls[i];
	
				    // 이미지 미리보기 요소 생성
				    var imagePreview = $('<li id="file' + i + '" class="editfilebox ui-state-default">' +
				      '<img src="' + imageUrl + '" width="80" height="80">' +
				      '<a class="delete" onclick="deleteAlreadyFile(' + i + ');">' +
				      '<span class="delBtn">x</span>' +
				      '</a>' +
				      '</li>');
				    
				    imageContainer.append(imagePreview);
				    
				    editFileArr.push(imageUrl);
				    // 기존 이미지로 인한 다음 이미지 추가시 부여할 순번 
				    alreadyFileNo = i + 1;
				  }
				  
				  // modalAction.js페이지의 전역변수에 값을 설정하는 부분
				  alreadyVariable(editFileArr, alreadyFileNo);
				  
			  } else{ // 해당 게시글에 이미지가 업로드 되어있지 않은경우
				  var imageContainer = $("#editPreview");
				  imageContainer.empty();
			  }
			  
			  // edit폼 제출 버튼 생성
			  var button = $('<button></button>').attr({
				    'id': 'submitButton',
				    'class': 'btn btn-primary rounded-5 fw-bold px-3 py-2 fs-6 mb-0 d-flex align-items-center update-button',
				    'data-bs-dismiss': 'modal'
				  }).click(function() {
				     postEditAction(post_Seq);
				  });
				  
				  var span = $('<span></span>').addClass('material-icons me-2 md-16').text('create');
				  button.append(span).append('Update');
				  
				  // 버튼을 원하는 위치에 추가
				  $('#editButtonContainer').empty().append(button);
			});
		    
		},error : function(request,status,error){
	        alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
		}
	});
}

function replyeditView(post_Seq, reply_Seq){

	var data = {
		post_Seq : post_Seq,
		reply_Seq : reply_Seq
	};

	console.log("replyEditView의 post_Seq : ",data.post_Seq);
	console.log("replyEditView의 reply_Seq : ",data.reply_Seq);
	$.ajax({
		url : "replyEditView",
		type : "GET",
		dataType: "json",
		data : data, // post_Seq를 보냄

		success : function(response) {
			console.log("ajax응답 성공");
			console.log(response);    // ajax요청으로 응답받은 값

			var post_Seq = response.post_seq;
			var reply_Seq = response.reply_seq;
			var replyContent = response.replycontent;

			// 모달창의 댓글 컨테이너 내용을 비우고 수정창 만을 출력하기 위한 컨테이너 지정
			var replyListContainer = $('#replyListContainer');
			replyListContainer.empty();

			// '초기화', '수정하기' 버튼을 좌측 우측으로 배치 하기위한 부모 컨테이너
			var buttonContainer = $('<div>').css({
				display: 'flex',
				justifyContent: 'space-between', // 좌우 간격을 최대한으로 벌리기
				width: '100%' // 부모 컨테이너의 폭을 100%로 설정
			});

			// 댓글 수정 입력창
			var textarea = $('<textarea>').addClass('bg-light px-3 py-2 rounded-4 mb-1 chat-text');
			textarea.val(replyContent);
			textarea.prop('rows', 5);
			textarea.css('width', '100%');

			// 입력창 내용 리셋 버튼
			var resetButton = $('<button>').text('초기화').addClass('btn btn-secondary rounded-5 fw-bold px-3 py-2 fs-6 mb-0 d-flex align-items-center')
				.click(function() {
				textarea.val(''); // Textarea 내용을 초기화
			});

			// 제출하기 버튼
			var submitButton = $('<button>').text('수정하기').addClass('btn btn-primary rounded-5 fw-bold px-3 py-2 fs-6 mb-0 d-flex align-items-center update-button')
			.on('click', function() {
				replyUpdate(post_Seq, reply_Seq, textarea.val());
			});

			// 부모 컨테이너에 버튼 추가
			buttonContainer.append(resetButton, submitButton);

			// replyListContainer에 부모 컨테이너 추가
			replyListContainer.append(textarea, buttonContainer);

		},error : function(request,status,error){
			alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
		}
	});
}
