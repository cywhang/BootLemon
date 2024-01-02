	
	//패스워드 유호성 함수
	function isPasswordValid(password) {
		var passwordRegex = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[\W_])\S{8,12}$/;;
	    return passwordRegex.test(password);
	}

	//이메일 아이디 유호성 함수	
	function isEmailIdValid(email) {
		var emailIdRegex = /^[a-zA-Z0-9]+$/;
		if (!emailIdRegex.test(email)){
			return false; // 유호성 위반
		}
			return true; // 유호성 통과
	}

	//이메일 주소 유호성 함수
	function isValidEmailAdd(emailAdd) {
		// 입력된 값이 영어와 마침표로만 구성되어 있는지 확인
		var regex = /^[a-zA-Z.]+$/;
		 if(!regex.test(emailAdd)){
		return false;
	}
		 return true; // 유호성 통과
	}
	
	//전화번호 유호성 함수
	function isPhoneNumberValid(phoneNumber) {
		var phoneNumberRegex = /^[0-9]{11}$/; // 하이픈 없이 숫자만 11자리 입력되어야 함
		return phoneNumberRegex.test(phoneNumber);
			}
	

	var memberPasswordInput = $('#member_Password');
	var passwordMessage = $('#password_message');
	var confirmPasswordInput = $('#repassword');
	var confirmPasswordMessage = $('#confirm_password_message');
	var checkPasswordInput = $('#check_Password');
	var passwordErrorMessage = $('#password_error_message');
	var memberEmailInput = $('#member_Email');
	var emailMessage = $('#email_error_message');
	var emailaddInput = $('#email_add')
	var memberPhoneInput = $('#member_Phone');
	var phoneMessage = $('#phone_error_message');
	var isIdChecked = false;
	var prevPassword = '';


	
	$(document).ready(function() {

	// 패스워드 입력칸 포커스 아웃 이벤트
	memberPasswordInput.blur(function() {
		var password = memberPasswordInput.val();
		
		
		 if (!isPasswordValid(password)) {
			passwordMessage.text("비밀번호는 최소 8자 이상이어야 하며,공백없이 영문 대문자,소문자,숫자,특수문자 를 포함해야 합니다.");
			passwordMessage.css("color", "red");
			isPassword = false;
		} else {
			passwordMessage.text("사용 가능한 패스워드입니다.");
			passwordMessage.css("color", "green");
			isPassword = true;
			prevPassword = password;
		}
	});






	// 패스워드 확인 입력칸 포커스 아웃 이벤트
	confirmPasswordInput.blur(function() {
	    var password = memberPasswordInput.val();
	    var confirmPassword = confirmPasswordInput.val();

	    if (password !== confirmPassword) {
	        confirmPasswordMessage.text("입력한 비밀번호와 일치하지 않습니다.");
	        confirmPasswordMessage.css("color", "red");
	        isConfirm = false;
	    } else {
	        confirmPasswordMessage.text("비밀번호가 일치합니다.");
	        confirmPasswordMessage.css("color", "green");
	        isConfirm = true;
	    }
	});
	
		
	// 이메일 입력칸 포커스 아웃 이벤트
	memberEmailInput.blur(function() {
		var email = memberEmailInput.val();
		
		 if (!isEmailIdValid(email))  {
			emailMessage.text('공백,한글,특수문자는 입력할수 없습니다.');
			emailMessage.css("color", "red");
			isEmail = false;
		} else {
			emailMessage.text('사용가능한 이메일 아이디 입니다.');
			emailMessage.css("color", "green");
			isEmail = true;
		}
	}); //memberEmailInput.blur
		
		
		
		
	// 이메일 주소 입력칸 아웃 포커스
	emailaddInput.blur(function() {
		var emailadd = emailaddInput.val();
		  
		
		 if (!isValidEmailAdd(emailadd)) {
		    emailMessage.text('공백,한글,특수문자 사용불가.정확한 이메일 주소를 입력해주세요.');
		    emailMessage.css( "color", "red");
		    isEmailadd = false;
		} else {
		    emailMessage.text('이메일 주소가 입력되었습니다.');
		    emailMessage.css("color", "green");
		    isEmailadd = true;
		}
	});
	
	
		
	// 전화번호 입력칸 아웃 포커스
	memberPhoneInput.blur(function() {
		var phoneNumber = memberPhoneInput.val();
		
		if (!isPhoneNumberValid(phoneNumber)) {
			phoneMessage.text('11자리 숫자만 입력해주세요.');
			phoneMessage.css("color", "red");
			isPhone = false;
		} else {
			
			phoneMessage.text('폰번호가 입력되었습니다.');
			phoneMessage.css("color", "green");
			isPhone = true;		
		}
	});
	
	});		
	

	// 회원 정보 수정;
	function go_update(event) {
		event.preventDefault();
		
		var memberPasswordInput2 = $('#member_Password');
		var confirmPasswordInput2 = $('#repassword');
		var memberEmailInput2 = $('#member_Email');
		var emailaddInput2 = $('#email_add');
		var memberPhoneInput2 = $('#member_Phone');
		
	   
		  var password = memberPasswordInput2.val();
		  var confirmPassword = confirmPasswordInput2.val();
		  var email = memberEmailInput2.val();
		  var emailadd = emailaddInput2.val();
		  var phoneNumber = memberPhoneInput2.val();
		
		  
		 if (!isPasswordValid(password)) {
			alert("패스워드를 확인해주세요");
			return;
		}
		

		 if (password !== confirmPassword) {
			alert("패스워드가 일치하지 않습니다.");
		    return;
		}
		
	    
		 if (!isEmailIdValid(email))  {
			alert("이메일 아이디를 확인해주세요.");	
			return;
		}
		
		 if (!isValidEmailAdd(emailadd)) {
		    alert("이메일 주소를 확인해 주세요.");
		    return;
		}
		
		 if (!isPhoneNumberValid(phoneNumber)) {
			alert("폰번호를 확인해 주세요."); 
			return;
	    } 
   
	    
	    
	 // 모든 필수 항목이 입력되었으므로 회원수정을 진행합니다.
		alert("수정이 완료되었습니다.");
		document.getElementById("edit_profile").action = "update_form"; // 회원수정
		document.getElementById("edit_profile").submit();
	}

