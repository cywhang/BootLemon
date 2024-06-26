// S3 기본 이미지 경로
var S3Path = "https://bluelemonbucket.s3.ap-northeast-2.amazonaws.com/";

// PEOPLE 탭의 사람들 목록을 가져와서 동적으로 표시하는 함수
function trending_List() {

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
            var trending_feed = document.getElementById("trending_feed");
            $("#trending_feed").empty();

            // 받아온 데이터를 활용해 동적으로 카드 추가
            if (trending_postList.length > 0) {

                var html = "";
                
                for (var i = 0; i < 10; i++) {

                    if (i == (trending_postList.length) && trending_postList.length <= 10) {

                        var loadingStop = document.getElementById("trendingFeedStop");

                        // 컨테이너 초기화
                        loadingStop.innerHTML = "";

                        html += '<div id="trendFeedEnd"><br>';
                        html += '	<h5 align="center">No Post To Show</h5>';
                        html += '	<br>';
                        html += '</div>';

                        loadingStop.innerHTML += html;

                        document.getElementById("trendFeedLoading").remove();

                    } else {

                        var PostVO = trending_postList[i];

                        html += '<div class="bg-white p-3 feed-item rounded-4 mb-3 shadow-sm">';
                        html += '   <div class="d-flex">';
                        html += '      <img src="'+ S3Path + 'profile/' + trending_profileMap[PostVO.member_Id] + '"  class="img-fluid rounded-circle user-img" alt="profile-img">';
                        html += '      <div class="d-flex ms-3 align-items-start w-100">';
                        html += '         <div class="w-100">';
                        html += '            <div class="d-flex align-items-center justify-content-between">';
                        html += '               <a href="profile?member_Id=' + PostVO.member_Id + '" class="text-decoration-none">';
                        html += '                  <h6 class="fw-bold mb-0 text-body" style="font-size: 26px;">' + PostVO.member_Id + '</h6>';
                        html += '               </a>';
                        html += '               <div class="d-flex align-items-center small">';
                        html += '                  <p class="text-muted mb-0">' + PostVO.post_Date + '</p>';

                        if (session_Id == PostVO.member_Id) {
                            html += '                <div class="dropdown">';
                            html += '                  <a href="#" class="text-muted text-decoration-none material-icons ms-2 md-20 rounded-circle bg-light p-1" id="dropdownMenuButton1" data-bs-toggle="dropdown" aria-expanded="false">more_vert</a>';
                            html += '                  <ul class="dropdown-menu fs-13 dropdown-menu-end" aria-labelledby="dropdownMenuButton1">';
                            html += '                    <li><a class="dropdown-item text-muted editbutton" data-bs-toggle="modal" data-bs-target="#postModal2" onclick="postEditView(' + PostVO.post_Seq + ')"><span class="material-icons md-13 me-1">edit</span>Edit</a></li>';
                            html += '                    <li><a class="dropdown-item text-muted deletebutton" onclick="deletePost(' + PostVO.post_Seq + ')"><span class="material-icons md-13 me-1">delete</span>Delete</a></li>';
                            html += '                  </ul>';
                            html += '                </div>';
                        } else {

                        }
                        ;

                        html += '               </div>';
                        html += '            </div>';
                        html += '            <div class="my-2">';


                        html += '               <a id="openModalBtn" class="text-decoration-none" data-bs-toggle="modal" data-bs-target="#commentModal" onclick="modalseq(' + PostVO.post_Seq + ')">';


                        if (PostVO.post_Image_Count == 0) {
                            html += '                        <br>';
                        } else {
                            html += '                        <img src="'+ S3Path + 'post/' + PostVO.post_Seq + '-1" class="img-fluid rounded mb-3" alt="post-img">';
                        }


                        html += '               </a>';
                        html += '               <br>';
                        html += '               <p class="text-dark">' + PostVO.post_Content.replace('\n', '<br>') + '</p>';
                        var hash = hashMap[PostVO.post_Seq];

                        if (hash == null) {

                        } else {

                            for (var j = 0; j < hash.length; j++) {
                                var Tag = hash[j]
                                html += '               <a href="search_HashTag?tag_Content=' + Tag.tag_Content + '" class="mb-3 text-primary">#' + Tag.tag_Content + '</a>';
                            }
                        }

                        html += ' <hr>';

                        html += '               <div class="d-flex align-items-center justify-content-between mb-2">';
                        html += '                  <div class="like-group" role="group">';

                        if (PostVO.post_LikeYN == "Y") {
                            html += '                           <button type="button" style = "border : none; background-color : white;" onclick="toggleLike(' + PostVO.post_Seq + ')">';
                            html += '                              <img class="likeImage_' + PostVO.post_Seq + '" src="img/unlike.png" width="20px" height="20px" data-liked = "true">';
                            html += '                           </button>';
                            html += '                              <p class ="post_Like_Count_' + PostVO.post_Seq + '" style="display: inline; margin-left: 3px; font-size : 13px;">' + PostVO.post_Like_Count + '</p>';

                        } else {
                            html += '                           <button type="button" style = "border : none; background-color : white;" onclick="toggleLike(' + PostVO.post_Seq + ')">';
                            html += '                              <img class="likeImage_' + PostVO.post_Seq + '" src="img/like.png" width="20px" height="20px" data-liked = "false">';
                            html += '                           </button>';
                            html += '                              <p class ="post_Like_Count_' + PostVO.post_Seq + '" style="display: inline; margin-left: 3px; font-size : 13px;">' + PostVO.post_Like_Count + '</p>';
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

                        for (j = 0; j <= 2; j++) {
                            var replyVO = reply[j];


                            if (replyVO == null) {

                            } else {


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

                                if (replyVO.reply_LikeYN == 'N') {
                                    html += '														<button type="button" style="border: none; background-color: white;" onclick="toggleReplyLike(\'' + replyVO.post_Seq + '\', \'' + replyVO.reply_Seq + '\');">';
                                    html += '														<img class="likeReplyImage_' + replyVO.reply_Seq + '" src="img/like.png" data-liked="false">';
                                    html += '														</button>';
                                    html += '														<p class="reply_Like_Count_' + replyVO.reply_Seq + '" style="display: inline; margin-left: 1px; font-size: 10px;">' + replyVO.reply_Like_Count + '</p>';
                                } else {
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

                        trending_feed.innerHTML += html;
                        html = "";
                    }
                }
            } else {
                var trending_feed = document.getElementById("trending_feed");

                // 컨테이너 초기화
                trending_feed.innerHTML = "";

                var card = document.createElement("div");
                card.innerHTML = `
                    <div>
                        <br>
                        <h5 align="center">No Post To Show</h5>
                        <br>
                    </div>
                `;
                cardsContainer.appendChild(card);
            }

            
        },
        error: function(xhr, status, error) {
            console.error(error);
        }
    });
}
