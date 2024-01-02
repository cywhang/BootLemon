
// 해시태그
const input = document.querySelector('textarea[name=post_Hashtag]');
let tagify = new Tagify(input); // initialize Tagify

// 태그가 추가되면 이벤트 발생
tagify.on('add', function() {
  console.log(tagify.value); // 입력된 태그 정보 객체
})





// 이미지 업로드
$(function() {
  // 드래그 앤 드롭
  $(".sortable").sortable();	
  
  //이미지 삭제  
  $(document).on('click', '.delBtn', function() {
    $(this).closest('li').remove();
    console.log("li태그 지움");
  });
  
  // 뒤로가기 버튼시 내용 초기화
  $('#closeModal').on('click', function() {
	    // form 태그 초기화
	    $('#postInsert')[0].reset();
	    // 이미지 컨테이너 초기화
	    $('#Preview').empty();
	});
  
  // 초기화 버튼 클릭시 내용 초기화
  $('#resetB').on('click', function() {
	    // 이미지 컨테이너 초기화
	    $('#Preview').empty();
	});
  
  //특수문자 입력 방지            %%%%%% 아직 안됨 %%%%%%%%%ㄴ
  function characterCheck(obj){
  	var regExp = /[ \{\}\[\]\/?.,;:|\)*~`!^\-_+┼<>@\#$%&\'\"\\\(\=]/gi; 
  	// 허용할 특수문자는 여기서 삭제하면 됨
  	// 지금은 띄어쓰기도 특수문자 처리됨 참고하셈
  	if( regExp.test(obj.value) ){
  		alert("#는  입력하실수 없습니다.");
  		obj.value = obj.value.substring( 0 , obj.value.length - 1 ); // 입력한 특수문자 한자리 지움
  		}
  }
  
  
});


  var fileNo = 0;
  var filesArr = new Array();
  
  
  /* 첨부파일 추가 */
  function addFile(obj){
      var maxFileCnt = 4;   // 첨부파일 최대 개수
      console.log("첨부파일 최대 개수 : ", maxFileCnt);
      var attFileCnt = document.querySelectorAll('.filebox').length;    // 기존 추가된 첨부파일 개수
      console.log("기존 추가된 첨부파일 개수 : ", attFileCnt);
      var remainFileCnt = maxFileCnt - attFileCnt;    // 추가로 첨부가능한 개수
      console.log("추가로 첨부가능한 개수 : ", remainFileCnt);
      var curFileCnt = obj.files.length;  // 현재 선택된 첨부파일 개수
      console.log("현재 선택된 첨부파일 개수 : ", curFileCnt);
      // 첨부파일 개수 확인
      if (curFileCnt > remainFileCnt) {
          alert("첨부파일은 최대 " + maxFileCnt + "개 까지 첨부 가능합니다.");
      } else {
          for (const file of obj.files) {
              // 첨부파일 검증
              if (validation(file)) {
                  // 파일 배열에 담기
                  var reader = new FileReader();
                  reader.onload = function (e) {
                	  var imageData = e.target.result; // 파일 데이터
                      filesArr.push(file);
                      console.log("filesArr : ", filesArr);
                      
                      let htmlData = '';
                      htmlData += '<li id="file' + fileNo + '" class="filebox ui-state-default">';
                      htmlData += '   <img src="' + imageData  + '" width="80" height="80">';
                      htmlData += '   <a class="delete" onclick="deleteFile(' + fileNo + ');"><span class="delBtn">x</span></a>';
                      htmlData += '</li>';
                      
                      $('#Preview').append(htmlData);
                      fileNo++;
                      
                  };
                  reader.readAsDataURL(file);
                  // 목록 추가
                  
              } else {
                  continue;
              }
          }
      }
      // 초기화
      document.querySelector("input[type=file]").value = "";
  }
  
  /* 첨부파일 검증 */
  function validation(obj){
      const fileTypes = ['application/pdf', 'image/gif', 'image/jpeg', 'image/png', 'image/bmp', 'image/tif', 'application/haansofthwp', 'application/x-hwp'];
      if (obj.name.length > 100) {
          alert("파일명이 100자 이상인 파일은 제외되었습니다.");
          return false;
      } else if (obj.size > (100 * 1024 * 1024)) {
          alert("최대 파일 용량인 100MB를 초과한 파일은 제외되었습니다.");
          return false;
      } else if (obj.name.lastIndexOf('.') == -1) {
          alert("확장자가 없는 파일은 제외되었습니다.");
          return false;
      } else if (!fileTypes.includes(obj.type)) {
          alert("첨부가 불가능한 파일은 제외되었습니다.");
          return false;
      } else {
          return true;
      }
  }
  
  /* 첨부파일 삭제 */
  function deleteFile(num) {
      document.querySelector("#file" + num).remove();
      filesArr[num].is_delete = true;
      console.log(num, " 파일 삭제");
  }
  
  
  // preview컨테이너에 변경된 순서 정보를 담는 작업
  var fileList = [];
	$('#Preview').on('sortupdate', function(event, ui) {
		newFileList = [];
		$('#Preview li').each(function() {
			var fileId = $(this).attr('id'); // 파일의 고유 ID
			newFileList.push(fileId);
		});
		// 이미지 순서 변경이 있을 때만 fileList에 할당
		fileList = newFileList.length > 0 ? newFileList : null;
		// fileList 배열에 변경된 파일 순서 정보가 포함됩니다.
		console.log(fileList);
	});

		
  
  /* 폼 전송 */
  function submitForm() {
      // 폼데이터 담기
      var form = document.querySelector("form");
      var formData = new FormData(form);
      // 삭제되지 않은 파일만 폼데이터에 담기
      for (var i = 0; i < filesArr.length; i++) {
          if (!filesArr[i].is_delete) {
              formData.append("attach_file", filesArr[i]);
          }
      }
      // 최종적으로 순서가 변경된 정보를 formData에 담음
      for (var i = 0; i < fileList.length; i++) {
          formData.append("fileList[]", fileList[i]);
      }
      console.log(filesArr);
      
      $.ajax({
          method: 'POST',
          url: 'insertPost',
          //dataType: 'json',
          enctype: "multipart/form-data", //form data 설정
          data: formData,
          contentType: false,
          processData: false,
          async: true,
          timeout: 30000,
          cache: false,
          headers: {'cache-control': 'no-cache', 'pragma': 'no-cache'},
          success: function (response) {
        	  window.location.href = '/blue/index'
          },
          
      })
  }

