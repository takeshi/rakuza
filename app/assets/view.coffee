@onmessage = (e)=>
	console.log(e)
	$html = $(e.data)
	# clear cache
	$("#main_page").remove()
	$html.wrap("<div id=main_page></div>") if $html.filter("#main_page").size() == 0
	$("body").append($html)
	$main_page = $("#main_page")
	$main_page.page()
	$.mobile.initializePage() unless @initPage
	@initPage = true
	$.mobile.activePage = null
	$.mobile.changePage($main_page);
	prettyPrint();
	