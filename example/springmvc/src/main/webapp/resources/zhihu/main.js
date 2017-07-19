// tab事件
(function() {
	window.onload = function() {
	    var hash = location.hash;
	    if (hash == "#signup") {
	        $("#SignInForm").hide();
	        $("#SignUpForm").show();
	        $(".navs-slider a:eq(0)").addClass("active").siblings().removeClass("active");
	        $(".navs-slider").attr("data-active-index", 0);
	    } else if (hash == "#signin") {
	        $("#SignInForm").show();
	        $("#SignUpForm").hide();
	        $(".navs-slider a:eq(1)").addClass("active").siblings().removeClass("active");
	        $(".navs-slider").attr("data-active-index", 1);
	    }
	};
	$(window).bind("hashchange", window.onload);
})();
