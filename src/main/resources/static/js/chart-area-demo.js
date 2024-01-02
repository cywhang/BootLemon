// Set new default font family and font color to mimic Bootstrap's default styling
Chart.defaults.global.defaultFontFamily = '-apple-system,system-ui,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif';
Chart.defaults.global.defaultFontColor = '#292b2c';
console.log(memberTendency);
// Area Chart Example
var ctx = document.getElementById("myAreaChart");
// 현재 날짜 포함 10일 가져오기
// 1. 현재 날짜
var currentDate = new Date();
// 2. 9일 전 날짜 계산
var startDate = new Date();
startDate.setDate(currentDate.getDate() - 9);

// 3. labels 배열 초기화
var labels = [];

// 4. startDate 부터 currentDate까지의 날짜를 labels 배열에 추가
var currentDateCopy = new Date(startDate);
while (currentDateCopy <= currentDate) {
	labels.push(currentDateCopy.toLocaleDateString('en-US', {month: 'short', day: 'numeric' }));
	currentDateCopy.setDate(currentDateCopy.getDate() + 1);
}

// 5. labels 배열을 사용해 차트 생성
var ctx = document.getElementById("myAreaChart");

var myLineChart = new Chart(ctx, {
  type: 'line',
  data: {
    labels: labels,
    datasets: [{
      label: "Sessions",
      lineTension: 0.3,
      backgroundColor: "rgba(2,117,216,0.2)",
      borderColor: "rgba(2,117,216,1)",
      pointRadius: 5,
      pointBackgroundColor: "rgba(2,117,216,1)",
      pointBorderColor: "rgba(255,255,255,0.8)",
      pointHoverRadius: 5,
      pointHoverBackgroundColor: "rgba(2,117,216,1)",
      pointHitRadius: 2,
      pointBorderWidth: 2,
      data: memberTendency,
    }],
  },
  options: {
    scales: {
      xAxes: [{
        time: {
          unit: 'date'
        },
        gridLines: {
          display: false
        },
        ticks: {
          maxTicksLimit: 7
        }
      }],
      yAxes: [{
        ticks: {
          min: 0,
          max: 30,
        },
        gridLines: {
          color: "rgba(0, 0, 0, .125)",
        }
      }],
    },
    legend: {
      display: false
    }
  }
});
