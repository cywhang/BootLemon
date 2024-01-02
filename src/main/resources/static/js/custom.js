/*
Template Name: Vogel - Social Network & Community HTML Template 
Author: Askbootstrap
Author URI: https://themeforest.net/user/askbootstrap
Version: 1.2
*/

(function ($) {
	"use strict"; // Start of use strict

	var Popular = {
		init: function () {
			this.Basic.init();
		},

		Basic: {
			init: function () {
				this.ListSlider();
			},

			// list_slider
			ListSlider: function () {
				$('.account-slider').slick({
					dots: false,
					arrows: false,
					infinite: false,
					speed: 300,
					slidesToShow: 4.2,
					slidesToScroll: 4.2,
					responsive: [
					  {
						breakpoint: 1024,
						settings: {
						  slidesToShow: 4.5,
						  slidesToScroll: 4.5,
						}
					  },
					  {
						breakpoint: 680,
						settings: {
						  slidesToShow: 2.5,
						  slidesToScroll: 2.5
						}
					  },
					  {
						breakpoint: 520,
						settings: {
						  slidesToShow: 3.5,
						  slidesToScroll: 3.5
						}
					  },
					  {
						breakpoint: 422,
						settings: {
						  slidesToShow: 2.5,
						  slidesToScroll: 2.5
						}
					  }
					]
				  });
			},
		}
	}
	jQuery(document).ready(function () {
		Popular.init();
	});

	
	
	// Dark Mode
	const toggleSwitch = document.querySelector('.theme-switch input[type="checkbox"]');
	const currentTheme = localStorage.getItem('theme');
	if (currentTheme) {
		document.documentElement.setAttribute('class', currentTheme);

		if (currentTheme === 'dark') {
			toggleSwitch.checked = true;
		}
	}
	function switchTheme(e) {
		if (e.target.checked) {
			document.documentElement.setAttribute('class', 'dark');
			localStorage.setItem('theme', 'dark');
		}
		else {
			document.documentElement.setAttribute('class', 'light');
			localStorage.setItem('theme', 'light');
		}
	}
	toggleSwitch.addEventListener('change', switchTheme, false);

})(jQuery); // End of use strict


// 게시글 삭제
function deletePost(post_Seq){
	
	if(confirm('삭제하시겠습니까?')){
		window.location.href="/blue/postDelete?post_Seq="+ post_Seq;
		console.log(post_Seq);
	}else{
		
	}	
	
}