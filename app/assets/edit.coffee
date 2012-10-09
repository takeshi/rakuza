$("#input_data").keyup (e)=>
#	console.log(e)
	text = $("#input_data").attr("value")
	clearTimeout(@timer) if(@timer)
	@timer = setTimeout ()=>
		console.log("send message")
		@websocket.send(text)
		@timer = null
	,1000
	
$("#update_btn").click ()=>
	text = $("#input_data").attr("value")
	console.log(document.location.href)
	$.ajax
		type:"post"
		url:document.location.href
		data:text
		success:()->
			console.log "update success"
			
		fail:(e)->
			console.log e