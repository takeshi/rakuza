@initWebSocket = (id)=>
	@websocket = new WebSocket("ws:" +document.location.host+"/websocket/"+id)
	@websocket.onclose = ()=>
 		console.log("onClose")
 		setTimeout ()=>
 		 	initWebSocket(id) 
 		,1000
 	@websocket.onmessage = onmessage
