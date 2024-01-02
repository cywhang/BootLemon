<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>기본 에러 화면</title>
</head>
<body bgcolor="#ffffff" text="#000000">

	<table style="width:100%; border:1">
		<tr>
			<td align="center" bgcolor="orange"><b>기본 에러 화면입니다.</b>
			</td>
		</tr>
	</table>
	<!-- 에러 메시지 -->
	<table style="width:100%; borde:1;">
		<tr>
			<td align="center">
			<br><br><br><br><br>
			Message: ${exception.message}
			<br><br><br><br><br>
			</td>
		</tr>
		<tr>
			<td align = "center">
				<!-- login.do 하면 아이디 비번 저장된 로그인 창, login.jsp는 기본 로그인 창 -->
				<a href ="login.do">로그인 화면으로 돌아가기</a>
			</td>
		</tr>
	</table>
	<a href = "index">메인 화면으로 이동</a>
</body>
</html>