var data, linkCount, obj;
	var txt = '{"Tasks": [{"name": "MelissaHolland", "startDate": "1971-01-28", "shiftEarlierPrice": 1, "shiftLaterPrice": 0, "duration": 821, "min_duration": 201, "reduceDurationPrice": 1, "parentTask": [1,2], "childrenTasks": [], "connectedTasks": [], "scheduleId": 1, "id": 0}, {"name": "RodneyAlexander", "startDate": "1971-09-13", "shiftEarlierPrice": 375393, "shiftLaterPrice": 0, "duration": 765, "min_duration": 110, "reduceDurationPrice": 0, "parentTask": [0,3], "childrenTasks": [1,2], "connectedTasks": [], "scheduleId": 1, "id": 1}]}'

	txt= txt.split('},')
	txt[0] = txt[0].replace('{"Tasks": [', '')+"}";
	txt[txt.length - 1] = txt[txt.length -1].replace(']}', '');
	
	data = {tasks:[], links:[]};
	linkCount=1;
	obj;
	for(var i = 0; i < txt.length; i++)
	{
		obj = JSON.parse(txt[i]);
		data.tasks.push({
			id:obj.id, 
			text:obj.name, 
			start_date:obj.startDate, 
			ErlierPrice:obj.shiftEarlierPrice, 
			LaterPrice:obj.shiftLaterPrice, 
			duration:obj.duration, 
			MinDuration:obj.min_duration, 
			DurationPrice:obj.reduceDurationPrice, 
			parent:obj.scheduleId});		
		
		if(obj.parentTask != null)
		{
			if(obj.parentTask.length > 1)
			{
				for(var j = 0; j < obj.parentTask.length; j++)
				{
					data.links.push({
						id:linkCount, 
						source:obj.id, 
						target:obj.parentTask[j], 
						type:"0"});
					linkCount++;
				}
			}
		}
		else
		{
			data.links.push({
				id:linkCount, 
				source:obj.id, 
				target:obj.parentTask[0], 
				type:"0"});
		}
	}
	console.log(data)