

	var memberIdInput = $('#member_Id');
	var idMessage = $('#id_error_message');
	var duplicateResult = $('#duplicate_result');
	var memberPasswordInput = $('#member_Password');
	var passwordMessage = $('#password_message');
	var confirmPasswordInput = $('#repassword');
	var confirmPasswordMessage = $('#confirm_password_message');
	var memberNameInput = $('#member_Name');
	var nameMessage = $('#name_error_message');
	var memberEmailInput = $('#member_Email');
	var emailMessage = $('#email_error_message');
	var emailaddInput = $('#email_add')
	var memberPhoneInput = $('#member_Phone');
	var phoneMessage = $('#phone_error_message');
	var male = document.getElementById("male");
	var female = document.getElementById("female");
	var prevPassword = '';
	
	// 유호성 검사 여부
	var isIdChecked = false;
	var isPassword = false;
	var isConfirm = false;
	var isName = false;
	var isEmail = false;
	var isEmailadd = false;
	var isPhone = false;
	
	//아이디 유호성 함수
	function isIdValid(member_Id) {
		 var idRegex = /^[a-zA-Z0-9]+$/;
		if (!idRegex.test(member_Id)) {
		return false; // 유호성 위반
	}
		return true; // 유호성 통과
	}
	
	//패스워드 유호성 함수
	function isPasswordValid(password) {
		var passwordRegex = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[\W_])\S{8,12}$/;;
	    return passwordRegex.test(password);
	}
	
	//이름 유호성 함수	
	function isKoreanNameValid(name) {
		var koreanRegex = /^[가-힣]+$/;
		if (!koreanRegex.test(name)) {
			return false; // 유호성 위반
	}
			return true; // 유호성 통과
	}
	
	//이메일 아이디 유호성 함수	
	function isEmailIdValid(emailId) {
		var emailIdRegex = /^[a-zA-Z0-9]+$/;
		if (!emailIdRegex.test(emailId)){
			return false; // 유호성 위반
		}
			return true; // 유호성 통과
	}
	
	//이메일 주소 유호성 함수
	function isValidEmailAdd(emailAdd) {
		// 입력된 값이 영어와 마침표로만 구성되어 있는지 확인
		var regex = /^[a-zA-Z]+(\.[a-zA-Z]+)*$/;
		if (!regex.test(emailAdd) || (emailAdd.split('.').length - 1) > 1) {
		return false; // 유호성 위반
	}
		 return true; // 유호성 통과
	}
	
	//전화번호 유호성 함수
	function isPhoneNumberValid(phoneNumber) {
		var phoneNumberRegex = /^[0-9]{11}$/; // 하이픈 없이 숫자만 11자리 입력되어야 함
		return phoneNumberRegex.test(phoneNumber);
	}
	
	//생년월일 오늘 일자 기준 이후 날짜 선택불가
	function handleDateClick() {
	    var birthInput = document.getElementById("member_Birth");
	    var nowUtc = Date.now();
	    var timeOffset = new Date().getTimezoneOffset() * 60000;
	    var today = new Date(nowUtc - timeOffset).toISOString().split("T")[0];
	    birthInput.max = today;
	}


	
	// 중복 체크 버튼 클릭 이벤트
	$('#check_duplicate_button').click(function() {
		
		var memberId = memberIdInput.val();
		
		if (!isIdValid(memberId)) {
			idMessage.text("12자 이하로 공백 없이 영문숫자로만 이루어진 아이디를 입력해주세요.");
			idMessage.css("color", "red");
	        return false;
	    } else {
	        // 중복 체크를 위한 AJAX 요청
	        $.ajax({
	            url: "checkDuplicate",
	            type: "POST",
	            data: { member_Id: memberId },
	            success: function(response) {
	                if (response === "duplicate") {
	                	idMessage.text("사용 중인 아이디입니다.");
	                	idMessage.css("color", "red");
	                    return false;
	                } else if (response === "not-duplicate") {
	                	idMessage.text("사용 가능한 아이디입니다.");
	                	idMessage.css("color", "green");
	                	isIdChecked = true
	                  
	                }
	            },
	            	error: function() {
	                IdMessage.text("중복 체크에 실패했습니다.");
	                IdMessage.css("color", "red");
	                isIdChecked = false;
	            }
	        });	    
	    }
	});
		
	

	// 패스워드 입력칸 포커스 아웃 이벤트
	memberPasswordInput.blur(function() {
		var password = memberPasswordInput.val();
		
		// 패스워드 함수에 적합하지 않는 입력값이면 false를 변수에 저장
		 if (!isPasswordValid(password)) {
			passwordMessage.text("비밀번호는 최소 8자 이상이어야 하며,공백없이 영문 대문자,소문자,숫자,특수문자 를 포함해야 합니다.");
			passwordMessage.css("color", "red");
			isPassword = false;
			
		// 패스워드 함수에 적합한 입력값이면 true를 변수에 저장	
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

	    // 패스워드 확인 입력값이 패스워드와 일치 하지 않으면 false를 변수에 저장
	    if (password !== confirmPassword) {
	        confirmPasswordMessage.text("입력한 비밀번호와 일치하지 않습니다.");
	        confirmPasswordMessage.css("color", "red");
	        isConfirm = false;
	    
	    // 패스워드 일치 여부가 확인되면 true를 변수에 저장
	    } else {
	        confirmPasswordMessage.text("비밀번호가 일치합니다.");
	        confirmPasswordMessage.css("color", "green");
	        isConfirm = true;
	    }
	});
	
	// 이름 입력칸 포커스 아웃 이벤트
	memberNameInput.blur(function() {
		var name = memberNameInput.val();
		
		// 이름이 유호성 함수에 적합하지 않는 입력값이면 false를 변수에 저장
		if (!isKoreanNameValid(name)) {
			nameMessage.text('공백없이 한글만 입력 가능합니다.');
			nameMessage.css("color", "red");
			isName = false;
			
	    // 이름이 유호성 함수에 적합하면 true를 변수에 저장
		} else {
			nameMessage.text('이름이 입력되었습니다.');
			nameMessage.css("color", "green");
			isName = true;
		}
	}); //memberNameInput.blur

	
		
	// 이메일 입력칸 포커스 아웃 이벤트
	memberEmailInput.blur(function() {
		var email = memberEmailInput.val();
		
		// 이메일 아이디가 유호성 함수에 적합하지 않는 아이디면 false를 변수에저장
		if (!isEmailIdValid(email))  {
			emailMessage.text('공백,한글,특수문자는 입력할수 없습니다.');
			emailMessage.css("color", "red");
			isEmail = false;
		
		// 이메일 아이디가 유호성 함수에 적합하면 true를 변수에 저장
		} else {
			emailMessage.text('사용가능한 이메일 아이디 입니다.');
			emailMessage.css("color", "green");
			isEmail = true;
		}
	}); //memberEmailInput.blur
	
		
	// 이메일 주소 입력칸 아웃 포커스
	emailaddInput.blur(function() {
		 var emailadd = emailaddInput.val();
		  
		 // 이메일 주소가 유호성 함수에 적합하지 않는 아이디면 false를 변수에저장	
		 if (!isValidEmailAdd(emailadd)) {
		    emailMessage.text('공백,한글,특수문자 사용불가.정확한 이메일 주소를 입력해주세요.');
		    emailMessage.css( "color", "red");
		    isEmailadd = false;
		
		 // 이메일 주소가 유호성 함수에 적합하면 true를 변수에저장
		 } else {
		    emailMessage.text('정상적으로 입력되었습니다.');
		    emailMessage.css("color", "green");
		    isEmailadd = true;
		}
	});
	
		
	// 전화번호 입력칸 아웃 포커스
	memberPhoneInput.blur(function() {
		var phoneNumber = memberPhoneInput.val();
		
		//전화번호가 유호성 함수에 적합하지 않는 값이면 false를 변수에 저장
		if (!isPhoneNumberValid(phoneNumber)) {
			phoneMessage.text('11자리 숫자만 입력해주세요.');
			phoneMessage.css("color", "red");
			isPhone = false;
			
		// 전화번호가 유호성에 적합한 값이면 true를 변수에 저장
		} else {
			phoneMessage.text('폰번호가 입력되었습니다.');
			phoneMessage.css("color", "green");
			isPhone = true;		
		}
	});
	
	
	

	//회원가입 폼 제출 이벤트
	function go_save(event) { 
		event.preventDefault();
		var input = $('#member_Birth');
		var birth = input.val();
		
		// 아이디 중복체크 여부 확인
	    if (!isIdChecked) {
			alert("중복체크를 확인해주세요");
	        return;
		}
		
	    // 패스워드 유호성 검사여부 확인
		if (!isPassword) {
			alert("패스워드를 확인해주세요");
			return;
		}
		
		// 패스워드 확인 유호성 검사 여부 확인
		if (!isConfirm) {
		    alert("패스워드가 일치하지 않습니다.");
		    return;
		}
		
		// 이름 유호성 검사 여부 확인
	    if (!isName) {
			alert("이름은 확인해 주세요.");  
			return;
	    }
	    
	    // 이메일아이디 유호성 검사 여부 확인
		if (!isEmail)  {
			alert("이메일 아이디를 확인해주세요.");	
			return;
		}
		
		// 이메일주소 유호성 검사 여부 확인
		if (!isEmailadd) {
		    alert("이메일 주소를 확인해 주세요.");
		    return;
		}
		
		// 전화번호 유호성 검사 여부 확인
	    if (!isPhone) {
			alert("폰번호를 확인해 주세요."); 
			return;
	    } 
	    
	    // 생년월일 입력값 여부 확인
	    if (birth === ""){
	    	alert("생년월일을 입력해주세요");
	    	return;
	    }
	    
	    // 성별 선택 여부 확인
	    if (!male.checked && !female.checked) {
	    	alert("성별을 선택해 주세요.");
	    	return;
	    }
	    
	    // 모든 확인 작업을 통과하면 양식을 제출합니다.
	    alert("회원가입이 완료되었습니다.");
	    document.getElementById("createAccount").action ="create_form";
	    document.getElementById("createAccount").submit();
	}


