// 체크 버튼에 따라 아이디/비밀번호 기능 변경
  function search_check(num) {
    if (num == '1') {
      document.getElementById("searchP").style.display = "none";
      document.getElementById("searchI").style.display = "";
    } else {
      document.getElementById("searchI").style.display = "none";
      document.getElementById("searchP").style.display = "";
    }
  }// 체크 버튼에 따라 아이디/비밀번호 기능 변경



//아이디 찾기 확인 버튼 클릭 이벤트
  $('#searchBtn').click(function() {
    var inputName = document.getElementById("inputName_1").value;
    var inputPhone = document.getElementById("inputPhone_1").value;

    // 결과 출력
    if (inputName === "" || inputPhone === "") {
      alert("이름과 휴대폰번호를 입력해주세요!");
    } else {
      $.ajax({
        type: "POST",
        url: "memberSearch",
        dataType: "text",
        contentType: "application/json",
        data: JSON.stringify({
          inputName_1: inputName,
          inputPhone_1: inputPhone
        }),
        success: function(result) {
        	console.log(result);
        if (result === "") {
            alert("등록된 회원이 아닙니다."); // 입력한 정보가 DB에 저장된 회원이 아닌 경우
          } else {
            alert("해당 아이디: " + result); // DB에 저장된 회원인 경우 아이디 출력
          }
        },
        error: function() {
          alert("서버 요청에 실패했습니다.");
        }
      });
    }
  });//아이디 찾기 확인 버튼 클릭 이벤트


  // 패스워드 찾기 
  function submit1() {
	    var inputId = document.getElementById("inputId").value;
	    var inputEmail = document.getElementById("inputEmail_2").value;

	    if (inputId === "" || inputEmail === "") {
	        alert("아이디와 이메일을 입력해주세요!");
	    } else {
	        var theform = document.getElementById("pwdauth");
	        theform.submit();
	    }
	}
	

  // 패스워드 찾기
  function go_password() {
	  var inputId = document.getElementById("inputId").value;
	  var inputEmail = document.getElementById("inputEmail_2").value;

	  // 결과 출력
	    if (inputId === "" || inputEmail === "") {
	      alert("아이디와  이메일을 입력해주세요!");
	    } else { 
		$.ajax({
		    type: "post",
		    url: "pwdauth",
		    dataType: "json",
		    contentType: "application/json",
		    data: JSON.stringify({
		          inputId: inputId,
		          inputEmail_2: inputEmail
		        }),
	    
	    success: function(dataMap) {
	      if (dataMap.message === 1) {
	        alert('인증번호 이메일이 발송되었습니다.');
	        // 필요한 로직 수행
	        window.location.href = "changePassword";
	      } else if (dataMap.message === -1) {
	        alert('등록된 회원이 아닙니다.');
	        // 필요한 로직 수행
	      } else {
	        alert('1오류가 발생했습니다.'+ dataMap.message);
	        // 필요한 로직 수행
	      }
	    },
     error: function() {
	      alert('2오류가 발생했습니다.');
      // 필요한 로직 수행
	    }
	  });
	 }
  }
  
  
//인증번호 확인버튼 
  function verifyAuthCode() {
      var enteredCode = $("#inputAuthCode").val();
      var generatedCode = $("#num").val();

      console.log("유저가 입력한 인증코드: " + enteredCode);
      console.log("서버가 생성한  인증코드: " + generatedCode);
      if (enteredCode === generatedCode) {
          // 인증번호 일치하는 경우
          // 예: 비밀번호 변경 폼 표시
          $("#passwordForm").show();
          $("#searchI").hide();
      } else {
          // 인증번호 불일치하는 경우
          $("#error_message").text("인증번호가 일치하지 않습니다.");
      }
  }


  $(document).ready(function() {
	  var password = $('#newPassword');
	  var passwordMessage = $('#password_message');
	  var confirmPassword = $('#checkPassword');
	  var confirmPasswordMessage = $('#confirm_password_message');

	  // password 입력칸 포커스 아웃 이벤트
	  password.blur(function() {
	    var passwordValue = password.val();

	    if (passwordValue === "") {
	      passwordMessage.text('비밀번호를 입력해 주세요.');
	      passwordMessage.css("color", "red");
	    } else if (!isPasswordValid(passwordValue)) {
	      passwordMessage.text("8 ~ 12자의 영문,숫자,특수문자만 가능합니다.");
	      passwordMessage.css("color", "red");
	    } else {
	      passwordMessage.text("사용 가능한 패스워드입니다.");
	      passwordMessage.css("color", "green");
	    }
	  });

	  // confirmPassword 입력칸 포커스 아웃 이벤트
	  confirmPassword.blur(function() {
	    var passwordValue = password.val();
	    var confirmPasswordValue = confirmPassword.val();

	    if (passwordValue === "") {
	      confirmPasswordMessage.text("먼저 비밀번호를 입력해주세요.");
	      confirmPasswordMessage.css("color", "red");
	      password.focus();
	    } else if (passwordValue !== confirmPasswordValue) {
	      confirmPasswordMessage.text("입력한 비밀번호와 일치하지 않습니다.");
	      confirmPasswordMessage.css("color", "red");
	    } else {
	      confirmPasswordMessage.text("입력한 비밀번호와 일치합니다.");
	      confirmPasswordMessage.css("color", "green");
	    }
	  });

	  // 패스워드 변경 확인 버튼 클릭 이벤트
	  $('#changePasswordBtn').click(function() {
	    var passwordValue = password.val();
	    var confirmPasswordValue = confirmPassword.val();

	    if (passwordValue === "") {
	      alert("패스워드를 입력해주세요.");
	      return;
	    } else if (!isPasswordValid(passwordValue)) {
	      alert("8 ~ 12자의 영문 대소문자와 숫자만 가능합니다.");
	      return;
	    } else if (passwordValue !== confirmPasswordValue) {
	      alert("입력한 비밀번호와 일치하지 않습니다.");
	      return;
	    }
		    
	    // 패스워드 변경이 확인되면 메세지를 띄우고 폼을 제출
	    alert("패스워드가 변경되었습니다.")
	    
        var theform = document.getElementById("updatePassword");
        theform.submit();
    
	  });
	//패스워드 유호성 함수
		function isPasswordValid(password) {
			var passwordRegex = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[\W_])\S{8,12}$/;;
		    return passwordRegex.test(password);
		}

  });	  


	  function displayErrorMessage(message) {
	    var errorContainer = document.getElementById("errorContainer");
	    errorContainer.textContent = message;
	  }

  
  
