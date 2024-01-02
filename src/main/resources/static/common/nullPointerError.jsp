<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>잘못된 값 참조 에러 화면</title>
</head>
<body bgcolor="#ffffff" text="#000000">

	<table style="width:100%; border:1">
		<tr>
			<td align="center" bgcolor="orange"><b>잘못된 값 참조 에러 화면입니다.</b>
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
	</table>
</body>
</html>