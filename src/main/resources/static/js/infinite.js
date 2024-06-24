/**
 *  인피니트 스크롤을 구현하기 위한 js
 */

// S3 기본 이미지 경로
var S3Path = "https://bluelemonbucket.s3.ap-northeast-2.amazonaws.com/";

var trendpageNum = 0;

function followingload(followingTotalPageNum, followingPageNum, member_Id){
	   
	   var followingTotalPageNum = followingTotalPageNum
	   
	   // 요청 바디에 전송할 데이터 설정
	    var data = {
	       followingTotalPageNum : parseInt(followingTotalPageNum),
	       followingPageNum : parseInt(followingPageNum),
	       member_Id : member_Id
	    };

	   $.ajax({
	       url: "/moreLoadFollowing",
	       type: "POST",
	       dataType: "json",
	       contentType: "application/json",
	       data: JSON.stringify(data),

	       success: function (response) {
	         
	    	 // 데이터 맵에서 값 가져오기
	         var following_info = response.following_info;
	         var following_size = response.following_size;
	         var followingPageNum = response.followingPageNum;
	         var followingTotalPageNum = response.followingTotalPageNum;

	         // HTML 생성을 위한 변수
	         var html = '';
	         
	         // following_info를 순회하며 HTML 생성
	         for (var i = 0; i < following_size; i++) {
	           var memberVO = following_info[i];
	           html += '<a href="profile?member_Id='+ memberVO.member_Id +'" class="p-3 d-flex text-dark text-decoration-none account-item pf-item">';
	           html += '<img src="'+ S3Path + 'profile/' + memberVO.member_Profile_Image + '" class="img-fluid rounded-circle me-3" alt="profile-img">';           html += '<div>';
	           html += '<p class="fw-bold mb-0 pe-3 d-flex align-items-center">' + memberVO.member_Id + '</p>'
	           html += '<div>';
	           html += '<p class="text-muted fw-light mb-1 small">' + memberVO.member_Name + '</p>';
	           html += '<span class="d-flex align-items-center small">' + '' + '/' + memberVO.member_Gender + '/' + memberVO.member_Email + '</span>';
	           html += '</div>';
	           html += '</div>';
	           html += '<div class="ms-auto">';
	           html += '<div class="btn-group" role="group" aria-label="Basic checkbox toggle button group">';
	           html += '<input type="checkbox" class="btn-check" name="btncheckbox" id="btncheck' + memberVO.member_Id + '2" checked>';
	           html += '<label class="btn btn-outline-primary btn-sm px-3 rounded-pill" for="btncheck' + memberVO.member_Id + '2" onclick="changeFollowing(\'' + memberVO.member_Id + '\')">';
	           html += '<span class="following d-none">Following</span><span class="follow">+ Follow</span></label>';
	           html += '</div>';
	           html += '</div>';
	           html += '</a>';
	         }

	         
	         // HTML 요소에 추가
	         var container = document.getElementById("followingContainer");
	         container.innerHTML += html;

	         // "더보기" 버튼 추가 여부 결정
	         var loadButton = document.getElementById("followingloadButton");
	         var loadButtonHTML = '';
	         
	         if (followingPageNum >= followingTotalPageNum) {
	           loadButtonHTML = '';
	         } else {
	           loadButtonHTML = '<div class="ms-auto" align="center">';
	           loadButtonHTML += '<span class="btn btn-outline-primary btn-sm px-3 rounded-pill" id="load" onclick="followingload(\'' + followingTotalPageNum + '\', \'' + followingPageNum + '\')">+ 더보기</span>';
	           loadButtonHTML += '</div>';
	         }
	         loadButton.innerHTML = loadButtonHTML;
	       },
	       error : function(request,status,error){
	           alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
	        }
	     });
	   }

function followerload(followerTotalPageNum, followerPageNum, member_Id) {
     var data = {
       followerTotalPageNum: parseInt(followerTotalPageNum),
       followerPageNum: parseInt(followerPageNum),
       member_Id: member_Id
     };

     $.ajax({
       url: "/moreLoadFollower",
       type: "POST",
       dataType: "json",
       contentType: "application/json",
       data: JSON.stringify(data),

       success: function (response) {
         
        // 데이터 맵에서 값 가져오기
         var follower_info = response.follower_info;
         var follower_size = response.follower_size;
         var followerPageNum = response.followerPageNum;
         var followerTotalPageNum = response.followerTotalPageNum;
         var following_Id = response.following_Id;
         var following_size = response.following_size;
         
         // HTML 생성을 위한 변수
         var html = '';
         
         // follower_info를 순회하며 HTML 생성
         for (var i = 0; i < follower_size; i++) {
           var memberVO = follower_info[i];

           html += '<a href="profile?member_Id='+ memberVO.member_Id +'"  class="p-3 d-flex text-dark text-decoration-none account-item pf-item">';
           html += '<img src="'+ S3Path + 'profile/' + memberVO.member_Profile_Image + '" class="img-fluid rounded-circle me-3" alt="profile-img">';           html += '<div>';
           html += '<p class="fw-bold mb-0 pe-3 d-flex align-items-center">' + memberVO.member_Id + '</p>';
           html += '<div>';
           html += '<p class="text-muted fw-light mb-1 small">' + memberVO.member_Name + '</p>';
           html += '<span class="d-flex align-items-center small">' + '' + '/' + memberVO.member_Gender + '/' + memberVO.member_Email + '</span>';
           html += '</div>';
           html += '</div>';
           html += '<div class="ms-auto">';
           html += '<div class="btn-group" role="group" aria-label="Basic checkbox toggle button group">';
           var exist = 0;
           
           for(var j = 0; j < following_size; j++){
                 
              var followingId = following_Id[j]
              
              if(memberVO.member_Id === followingId.following){
                    exist = 1;
                 }
              }
           if(exist === 0){
              html += '<input type="checkbox" class="btn-check" name="btncheckbox" id="btncheck' + memberVO.member_Id + '2">';
           }else if(exist === 1){
              html += '<input type="checkbox" class="btn-check" name="btncheckbox" id="btncheck' + memberVO.member_Id + '2" checked>';
           }
           html += '<label class="btn btn-outline-primary btn-sm px-3 rounded-pill" for="btncheck' + memberVO.member_Id + '2" onclick="changeFollow(\'' + memberVO.member_Id + '\')">';
           html += '<span class="following d-none">Following</span><span class="follow">+ Follow</span></label>';
           html += '</div>';
           html += '</div>';
           html += '</a>';
         }

         // HTML 요소에 추가
         var container = document.getElementById("followerContainer");
         container.innerHTML += html;
         
         // "더보기" 버튼 추가 여부 결정
         var loadButton = document.getElementById("followerloadButton");
         var loadButtonHTML = '';
         
         if (followerPageNum >= followerTotalPageNum) {
           loadButtonHTML = '';
         } else {
           loadButtonHTML = '<div class="ms-auto" align="center">';
           loadButtonHTML += '<span class="btn btn-outline-primary btn-sm px-3 rounded-pill" id="load" onclick="follwerload(\'' + followerTotalPageNum + '\', \'' + followerPageNum + '\')">+ 더보기</span>';
           loadButtonHTML += '</div>';
         }
         loadButton.innerHTML = loadButtonHTML;
       },
       error : function(request,status,error){
           alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
        }
     });
   }


//Intersection Observer 생성
const options = {
    root: null, // 뷰포트를 root로 사용
    rootMargin: '0px', // 뷰포트의 경계에서 시작
    threshold: 1.0 // 타겟 엘리먼트가 100% 뷰포트 내에 들어왔을 때 콜백 실행
};

const observer = new IntersectionObserver((entries, observer) => {
    entries.forEach(entry => {
         if (entry.isIntersecting && entry.target.id === 'feedInfinity') {
                // 뷰포트 내에 들어온 div id "feedInfinity"인 경우 콜백 함수 실행
                handleFeedInfinity();

         }else if(entry.isIntersecting && entry.target.id === 'feedEnd'){
                // 관찰 중지
                observer.unobserve(entry.target);
         }
    });}, options);

//관찰할 대상 설정
const target = document.getElementById('feedInfinity');
observer.observe(target);
var pageNum = 0;

//뷰포트 내에 들어올 경우 실행할 함수
function handleFeedInfinity() {

pageNum = pageNum+1;

$.ajax({
   url: "feedInfinite",
   type: "GET",
   dataType: "json", // 데이터 형식을 JSON으로 지정
   success: function(response) {

       console.log("infinite scroll");
       var trending_profileMap = response.profileMap;
       var trending_postList = response.postList;
       var trending_replyMap = response.replyMap;
       var session_Id = response.session_Id;
       var hashMap = response.hashMap;
       
       var firstRow = pageNum * 10;
       var lastRow = pageNum * 10 + 9;

       if(trending_postList.length < lastRow){
          lastRow = trending_postList.length
       }

       // 받아온 데이터를 활용해 동적으로 카드 추가
       if (trending_postList.length > 0) {
           var feed = document.getElementById("main_feed");

           var html = "";

           for (var i = firstRow; i <= lastRow; i++) {

              if(i == trending_postList.length){

                  var loadingStop = document.getElementById("loadingStop");

                  // 컨테이너 초기화
                  loadingStop.innerHTML = "";

                   html += '<div id="feedEnd"><br>';
                   html += '   <h5 align="center">No Post To Show</h5>';
                   html += '   <br>';
                   html += '</div>';

                   loadingStop.innerHTML += html;

                   document.getElementById("feedInfinity").remove();
               }

              var PostVO = trending_postList[i];

               html += '<div class="bg-white p-3 feed-item rounded-4 mb-3 shadow-sm">';
               html += '   <div class="d-flex">';
               html += '      <img src="'+ S3Path + 'profile/'+ trending_profileMap[PostVO.member_Id] + '"  class="img-fluid rounded-circle user-img" alt="profile-img">';
               html += '      <div class="d-flex ms-3 align-items-start w-100">';
               html += '         <div class="w-100">';
               html += '            <div class="d-flex align-items-center justify-content-between">';
               html += '               <a href="profile?member_Id=' + PostVO.member_Id + '" class="text-decoration-none">';
               html += '                  <h6 class="fw-bold mb-0 text-body" style="font-size: 26px;">' + PostVO.member_Id + '</h6>';
               html += '               </a>';
               html += '               <div class="d-flex align-items-center small">';
               html += '                  <p class="text-muted mb-0">' + PostVO.post_Date + '</p>';

               if(session_Id == PostVO.member_Id){
                  html += '                <div class="dropdown">';
                       html += '                  <a href="#" class="text-muted text-decoration-none material-icons ms-2 md-20 rounded-circle bg-light p-1" id="dropdownMenuButton1" data-bs-toggle="dropdown" aria-expanded="false">more_vert</a>';
                       html += '                  <ul class="dropdown-menu fs-13 dropdown-menu-end" aria-labelledby="dropdownMenuButton1">';
                       html += '                    <li><a class="dropdown-item text-muted editbutton" data-bs-toggle="modal" data-bs-target="#postModal2" onclick="postEditView(' + PostVO.post_Seq + ')"><span class="material-icons md-13 me-1">edit</span>Edit</a></li>';
                       html += '                    <li><a class="dropdown-item text-muted deletebutton" onclick="deletePost(' + PostVO.post_Seq + ')"><span class="material-icons md-13 me-1">delete</span>Delete</a></li>';
                       html += '                  </ul>';
                       html += '                </div>';
               }else{

               };

               html += '               </div>';
               html += '            </div>';
               html += '            <div class="my-2">';
               html += '               <a id="openModalBtn" class="text-decoration-none" data-bs-toggle="modal" data-bs-target="#commentModal" onclick="modalseq(' + PostVO.post_Seq + ')">';
               

               if(PostVO.post_Image_Count == 0){
                  html += '                        <br>';
               }else{
                  html += '                        <img src="'+ S3Path + 'post/' + PostVO.post_Seq + '-1.png" class="img-fluid rounded mb-3" alt="post-img">';
               }
               html += '               </a>';
               html += '               <br>';
               html += '               <p class="text-dark" id="postContent'+PostVO.post_Seq+'">' + PostVO.post_Content + '</p>';
               html += '               <br>';
               
               var hash = hashMap[PostVO.post_Seq];

                if(hash == null){

                }else{

                  for(var j=0; j<hash.length; j++){
                     var Tag = hash[j]
                      html += '               <a href="search_HashTag?tag_Content=' + Tag.tag_Content + '" class="mb-3 text-primary">#' + Tag.tag_Content + '</a>';
                  }
                }
               html += '               <hr>';
               html += '               <div class="d-flex align-items-center justify-content-between mb-2">';
               html += '                  <div class="like-group" role="group">';

               if(PostVO.post_LikeYN == "Y"){
                   html += '                           <button type="button" style = "border : none; background-color : white;" onclick="toggleLike(' + PostVO.post_Seq + ')">';
                   html += '                              <img class="likeImage_' + PostVO.post_Seq + '" src="img/unlike.png" width="20px" height="20px" data-liked = "true">';
                   html += '                           </button>';
                   html += '                              <p class ="post_Like_Count_' + PostVO.post_Seq + '" style="display: inline; margin-left: 3px; font-size : 13px;">' + PostVO.post_Like_Count + '</p>';

               }else{
                   html += '                           <button type="button" style = "border : none; background-color : white;" onclick="toggleLike(' + PostVO.post_Seq + ')">';
                   html += '                              <img class="likeImage_' + PostVO.post_Seq + '" src="img/like.png" width="20px" height="20px" data-liked = "false">';
                   html += '                           </button>';
                   html += '                              <p class ="post_Like_Count_' + PostVO.post_Seq + '" style="display: inline; margin-left: 3px; font-size : 13px;">' + PostVO.post_Like_Count + '</p>';
               }

               html += '                           </div>';
               html += '                                <div>';
               html += '                                 <div class="text-muted text-decoration-none d-flex align-items-start fw-light"><span class="material-icons md-20 me-2">chat_bubble_outline</span><span>' + PostVO.post_Reply_Count + '</span></div>';      
               html += '                        </div>';
               html += '                     </div>';
               html += '                     <div class="d-flex align-items-center mb-3" data-bs-toggle="modal" data-bs-target="#commentModal2" onclick="replyModalseq(' + PostVO.post_Seq + ')">';
               html += '                        <span class="material-icons bg-white border-0 text-primary pe-2 md-36">account_circle</span>';
               html += '                        <input type="text" class="form-control form-control-sm rounded-3 fw-light" placeholder="Write Your comment">';
               html += '                     </div>';

               html += '                     <div class="comments">';


               //html += '                        <c:set var="key" value="' + i + '"/>';
               //html += '                        <c:set var="value" value="' + trending_replyMap[i] + '"/>';
               //html += '                        <c:forEach var="reply" items="' + value + '" begin="0" end="2">';

               var reply = trending_replyMap[i];

               for(j=0; j<=2; j++){
                  var replyVO = reply[j];


               if(replyVO == null){

               }else{



               html += '                           <div class="d-flex mb-2">';
               html += '                              <a href="#" class="text-dark text-decoration-none" data-bs-toggle="modal" data-bs-target="#commentModal2" onclick="replyModalseq(' + replyVO.post_Seq + ')">';
               html += '                              <img src="'+ S3Path + 'profile/' + trending_profileMap[replyVO.member_Id] + '" class="img-fluid rounded-circle profile" alt="commenters-img">';
               html += '                              </a>';
               html += '                              <div class="ms-2 small">';
               html += '                                 <a href="#" class="text-dark text-decoration-none" data-bs-toggle="modal" data-bs-target="#commentModal2" onclick="replyModalseq(' + replyVO.post_Seq + ');">';
               html += '                                 <div class="bg-light px-3 py-2 rounded-4 mb-1 chat-text" style="display: inline-block;">';
               html += '                                    <p class="fw-500 mb-0">' + replyVO.member_Id + '</p>';
               html += '                                    <span class="text-muted">' + replyVO.reply_Content + '</span>';
               html += '                                 </div>';
               html += '                                 </a>';
               html += '                                 <div class="reply-like-group" role="group" style="display: inline-block;">';

               //html += '                                    <c:set var="replySeq" value="' + reply.reply_Seq + '"/>';

               if(replyVO.reply_LikeYN == 'N'){
                  html += '                                          <button type="button" style="border: none; background-color: white;" onclick="toggleReplyLike(\'' + replyVO.post_Seq + '\', \'' + replyVO.reply_Seq + '\');">';
                   html += '                                          <img class="likeReplyImage_' + replyVO.reply_Seq + '" src="img/like.png" data-liked="false">';
                   html += '                                          </button>';
                   html += '                                          <p class="reply_Like_Count_' + replyVO.reply_Seq + '" style="display: inline; margin-left: 1px; font-size: 10px;">' + replyVO.reply_Like_Count + '</p>';
               }else{
                   html += '                                          <button type="button" style="border: none; background-color: white;" onclick="toggleReplyLike(\'' + trending_replyMap[i].post_Seq + '\', \'' + replyVO.reply_Seq + '\');">';
                   html += '                                          <img class="likeReplyImage_' + replyVO.reply_Seq + '" src="img/unlike.png" data-liked="true">';
                   html += '                                          </button>';
                   html += '                                          <p class="reply_Like_Count_' + replyVO.reply_Seq + '" style="display: inline; margin-left: 1px; font-size: 10px;">' + replyVO.reply_Like_Count + '</p>';
               }

               html += '                                 </div>';
               html += '                                 <div style="display: inline-block;" width="5px"></div>';
               html += '                                    <span class="small text-muted">' + replyVO.reply_WhenDid + '</span>';
               html += '                                 </a>';
               html += '                                    <div class="d-flex align-items-center justify-content-between mb-2">';
               html += '                                    </div>';
               html += '                                 </div>';
               html += '                              </div>';

                 }   

               }

               html += '                           </div>';
               html += '                        </div>';
               html += '                     </div>';
               html += '                  </div>';
               html += '               </div>';
               html += '            </div>';
               html += '         </div>';



               feed.innerHTML += html;
               autolink('postContent'+PostVO.post_Seq);
               
               html = "";

               


           }
           

       } else {
           var feed_loading = document.getElementById("feedInfinity");
           // 컨테이너 초기화
           feed_loading.innerHTML = "";
           var card = document.createElement("div");
           card.innerHTML = `
               <div id="feedEnd">
                   <br>
                   <h5 align="center">No Post To Show</h5>
                   <br>
               </div>
           `;
           cardsContainer.appendChild(card);
       }

   },error: function(xhr, status, error) {
       console.error(error);
   }
});
}





//뷰포트 내에 들어올 경우 실행할 함수
function handleTrendInfinity() {

trendpageNum = trendpageNum+1;


$.ajax({
    url: "trending_List",
    type: "GET",
    dataType: "json", // 데이터 형식을 JSON으로 지정
    success: function(response) {
  	  
  	  var trending_profileMap = response.trending_profileMap;
        var trending_postList = response.trending_postList;
        var trending_replyMap = response.trending_replyMap;
        var session_Id = response.session_Id;
        var hashMap = response.hashMap;
        
        var firstRow = trendpageNum * 10;
        var lastRow = trendpageNum * 10 + 9;
        
        if(trending_postList.length <= lastRow){
      	  lastRow = trending_postList.length
        }
        
        // 받아온 데이터를 활용해 동적으로 카드 추가
        if (trending_postList.length > 0) {
            var feed = document.getElementById("trendingFeedInfinite");

            var html = "";
            
            for (var i = firstRow; i <= lastRow; i++) {
          	  
          	  if(i == trending_postList.length){
              	  
              	  var loadingStop = document.getElementById("trendingFeedStop");
              	  
              	  // 컨테이너 초기화
              	  loadingStop.innerHTML = "";
                    
                    html += '<div id="trendFeedEnd"><br>';
                    html += '	<h5 align="center">No Post To Show</h5>';
                    html += '	<br>';
                    html += '</div>';
                            
                    loadingStop.innerHTML += html;
                    
                    document.getElementById("trendFeedLoading").remove();
                } 
          	  
          	  var PostVO = trending_postList[i];
          	  
                html += '<div class="bg-white p-3 feed-item rounded-4 mb-3 shadow-sm">';
                html += '   <div class="d-flex">';
                html += '      <img src="'+ S3Path + 'profile/'+ trending_profileMap[PostVO.member_Id] + '"  class="img-fluid rounded-circle user-img" alt="profile-img">';
                html += '      <div class="d-flex ms-3 align-items-start w-100">';
                html += '         <div class="w-100">';
                html += '            <div class="d-flex align-items-center justify-content-between">';
                html += '               <a href="profile?member_Id=' + PostVO.member_Id + '" class="text-decoration-none">';
                html += '                  <h6 class="fw-bold mb-0 text-body" style="font-size: 26px;">' + PostVO.member_Id + '</h6>';
                html += '               </a>';
                html += '               <div class="d-flex align-items-center small">';
                html += '                  <p class="text-muted mb-0">' + PostVO.post_Date + '</p>';

                if(session_Id == PostVO.member_Id){
						html += '                <div class="dropdown">';
	                    html += '                  <a href="#" class="text-muted text-decoration-none material-icons ms-2 md-20 rounded-circle bg-light p-1" id="dropdownMenuButton1" data-bs-toggle="dropdown" aria-expanded="false">more_vert</a>';
	                    html += '                  <ul class="dropdown-menu fs-13 dropdown-menu-end" aria-labelledby="dropdownMenuButton1">';
	                    html += '                    <li><a class="dropdown-item text-muted editbutton" data-bs-toggle="modal" data-bs-target="#postModal2" onclick="postEditView(' + PostVO.post_Seq + ')"><span class="material-icons md-13 me-1">edit</span>Edit</a></li>';
	                    html += '                    <li><a class="dropdown-item text-muted deletebutton" onclick="deletePost(' + PostVO.post_Seq + ')"><span class="material-icons md-13 me-1">delete</span>Delete</a></li>';
	                    html += '                  </ul>';
	                    html += '                </div>';
					}else{
						
					};
                
                html += '               </div>';
                html += '            </div>';
                
                html += '               <a id="openModalBtn" class="text-decoration-none" data-bs-toggle="modal" data-bs-target="#commentModal" onclick="modalseq(' + PostVO.post_Seq + ')">';
                if(PostVO.post_Image_Count == 0){
                	html += '                        <br>';
                }else{
                	html += '                        <img src="'+ S3Path + 'post/' + PostVO.post_Seq + '-1.png" class="img-fluid rounded mb-3" alt="post-img">';
                }
                html += '               </a>';
                
                html += '               <br>';
                html += '               <p class="text-dark">' + PostVO.post_Content + '</p>';
                html += '               <br>';

                var hash = hashMap[PostVO.post_Seq];

	             if(hash == null){

	             }else{

	          	  for(var j=0; j<hash.length; j++){
	          		  var Tag = hash[j]
	             		html += '               <a href="search_HashTag?hashTag=' + Tag.tag_Content + '" class="mb-3 text-primary">#' + Tag.tag_Content + '</a>';
	          	  }
	             }
	             
	            html += ' <hr>'; 
                html += '               <div class="d-flex align-items-center justify-content-between mb-2">';
                html += '                  <div class="like-group" role="group">';
                
                if(PostVO.post_LikeYN == "Y"){
                    html += '                           <button type="button" style = "border : none; background-color : white;" onclick="toggleLike(' + PostVO.post_Seq + ')">';
                    html += '                              <img class="likeImage_' + PostVO.post_Seq + '1" src="img/unlike.png" width="20px" height="20px" data-liked = "true">';
                    html += '                           </button>';
                    html += '                              <p class ="post_Like_Count_' + PostVO.post_Seq + '1" style="display: inline; margin-left: 3px; font-size : 13px;">' + PostVO.post_Like_Count + '</p>';

                }else{
                    html += '                           <button type="button" style = "border : none; background-color : white;" onclick="toggleLike(' + PostVO.post_Seq + ')">';
                    html += '                              <img class="likeImage_' + PostVO.post_Seq + '1" src="img/unlike.png" width="20px" height="20px" data-liked = "false">';
                    html += '                           </button>';
                    html += '                              <p class ="post_Like_Count_' + PostVO.post_Seq + '1" style="display: inline; margin-left: 3px; font-size : 13px;">' + PostVO.post_Like_Count + '</p>';
                }
                
                html += '                           </div>';
                html += '                          		<div>';
                html += '                           		<div class="text-muted text-decoration-none d-flex align-items-start fw-light"><span class="material-icons md-20 me-2">chat_bubble_outline</span><span>' + PostVO.post_Reply_Count + '</span></div>';		
                html += '								</div>';
                html += '							</div>';
                html += '							<div class="d-flex align-items-center mb-3" data-bs-toggle="modal" data-bs-target="#commentModal2" onclick="replyModalseq(' + PostVO.post_Seq + ')">';
                html += '								<span class="material-icons bg-white border-0 text-primary pe-2 md-36">account_circle</span>';
                html += '								<input type="text" class="form-control form-control-sm rounded-3 fw-light" placeholder="Write Your comment">';
                html += '							</div>';

                html += '							<div class="comments">';
                
                
                var reply = trending_replyMap[i];
                
                for(j=0; j<=2; j++){
						var replyVO = reply[j];
					
                
                if(replyVO == null){
                	
                }else{
                
                
					
                html += '									<div class="d-flex mb-2">';
                html += '										<a href="#" class="text-dark text-decoration-none" data-bs-toggle="modal" data-bs-target="#commentModal2" onclick="replyModalseq(' + replyVO.post_Seq + ')">';
                html += '										<img src="'+ S3Path + 'profile/' + trending_profileMap[replyVO.member_Id] + '" class="img-fluid rounded-circle profile" alt="commenters-img">';
                html += '										</a>';
                html += '										<div class="ms-2 small">';
                html += '											<a href="#" class="text-dark text-decoration-none" data-bs-toggle="modal" data-bs-target="#commentModal2" onclick="replyModalseq(' + replyVO.post_Seq + ');">';
                html += '											<div class="bg-light px-3 py-2 rounded-4 mb-1 chat-text" style="display: inline-block;">';
                html += '												<p class="fw-500 mb-0">' + replyVO.member_Id + '</p>';
                html += '												<span class="text-muted">' + replyVO.reply_Content + '</span>';
                html += '											</div>';
                html += '											</a>';
                html += '											<div class="reply-like-group" role="group" style="display: inline-block;">';
                
                //html += '												<c:set var="replySeq" value="' + reply.reply_Seq + '"/>';
                
                if(replyVO.reply_LikeYN == 'N'){
                	html += '														<button type="button" style="border: none; background-color: white;" onclick="toggleReplyLike(\'' + replyVO.post_Seq + '\', \'' + replyVO.reply_Seq + '\');">';
                    html += '														<img class="likeReplyImage_' + replyVO.reply_Seq + '" src="img/like.png" data-liked="false">';
                    html += '														</button>';
                    html += '														<p class="reply_Like_Count_' + replyVO.reply_Seq + '" style="display: inline; margin-left: 1px; font-size: 10px;">' + replyVO.reply_Like_Count + '</p>';
                }else{
                    html += '														<button type="button" style="border: none; background-color: white;" onclick="toggleReplyLike(\'' + trending_replyMap[i].post_Seq + '\', \'' + replyVO.reply_Seq + '\');">';
                    html += '														<img class="likeReplyImage_' + replyVO.reply_Seq + '" src="img/unlike.png" data-liked="true">';
                    html += '														</button>';
                    html += '														<p class="reply_Like_Count_' + replyVO.reply_Seq + '" style="display: inline; margin-left: 1px; font-size: 10px;">' + replyVO.reply_Like_Count + '</p>';
                }
                
                html += '											</div>';
                html += '											<div style="display: inline-block;" width="5px"></div>';
                html += '												<span class="small text-muted">' + replyVO.reply_WhenDid + '</span>';
                html += '											</a>';
                html += '												<div class="d-flex align-items-center justify-content-between mb-2">';
                html += '												</div>';
                html += '											</div>';
                html += '										</div>';
                
            		}	
					
                }
                												
                html += '									</div>';
                html += '								</div>';
                html += '							</div>';
                html += '						</div>';
                html += '					</div>';
                html += '				</div>';
                html += '			</div>';
                
                

                feed.innerHTML += html;
                autolink('postContent'+PostVO.post_Seq);
                html = "";
                
                
                
                
                
            }
            
        } else {
            var feed_loading = document.getElementById("trendingFeedStop");
            // 컨테이너 초기화
            feed_loading.innerHTML = "";
            var card = document.createElement("div");
            card.innerHTML = `
                <div id="trendFeedEnd">
                    <br>
                    <h5 align="center">No Post To Show</h5>
                    <br>
                </div>
            `;
            cardsContainer.appendChild(card);
        }
  	  
  	  
  	  
  	  
  	  
  	  
  	  
  	  
    },error: function(xhr, status, error) {
        console.error(error);
    }
});
}