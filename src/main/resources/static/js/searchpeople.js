function searchMembers(results) {
	
  // 검색어를 사용하여 멤버 아이디를 검색하고 결과를 반환
	var searchKeyword = document.getElementById("keyword").value;
	var values = [];  // ArrayList 값을 받을 변수

  $.ajax({
    url: 'search_people', // 검색 요청을 보낼 서버의 URL
    method: 'GET',
    dataType: 'json',
    data: { keyword: searchKeyword }, // 검색 키워드를 서버에 전달
    success: function(searchResults) {
    	$.each(searchResults, function(index, item){
    		console.log("item=", item.member_Id);
        });
    	
        displayResults(searchResults); // 검색 결과를 출력하는 함수 호출
    },
    error: function() {
      console.log('Error occurred during search.');
    }
  });
}

function displayResults(results) {
  var searchResultsElement = document.getElementById('searchResults');
  searchResultsElement.innerHTML = '';
  console.log("displayResults()...");
  console.log("results=", results);
  if (results != null) {
    var ul = document.createElement('ul');
    
    $.each(results, function(index, item){
        var li = document.createElement('li');
        var link = document.createElement('a');
        link.href = "profile?member_Id=" + item.member_Id; // 링크의 목적지 URL을 지정해야 합니다.
        link.textContent = item.member_Id;
        li.appendChild(link);
        ul.appendChild(li);
      });
  
    searchResultsElement.appendChild(ul);
    searchResultsElement.style.display = 'block'; // 결과를 표시하기 위해 display 속성을 block으로 설정
  } else {
    searchResultsElement.style.display = 'none'; // 결과가 없을 경우 결과 영역을 숨기기 위해 display 속성을 none으로 설정
  }
}
function handleKeyDown(event) {
	  if (event.keyCode === 13) {
	    event.preventDefault();
	    searchMembers();
	  }
	}
	
