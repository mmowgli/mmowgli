function _mmowgliFinishedLoading() {
	// Set css for dbug divs
	var node = document.createElement('style');
	node.innerHTML = '#triggerDbugDiv {position:fixed;bottom:10px;left:10px;width:10px;height:10px;z-index:10000;} \
	 #triggerDbugDiv:hover {cursor:pointer;} \
	 #displayDiv {position:fixed; top:0px; left:0px; font-family:monospace; font-size:10px; background:white; border: 2px solid black; z-index:10000; word-break: break-all;}';
	document.body.appendChild(node);
}

// executed inline
if (window.performance.getEntriesByType && document.addEventListener)
	document.addEventListener('DOMContentLoaded', _mmowgliFinishedLoading);

var displayDiv;

function triggerDbugDivMouseDown() {
	if(!window.performance.getEntriesByType)
		return;
	var trigDiv = document.getElementById("triggerDbugDiv");
	if (trigDiv) {
		if (displayDiv) {
			trigDiv.removeChild(displayDiv);
			displayDiv = null;
		} else {
			displayDiv = document.createElement('div');
			trigDiv.appendChild(displayDiv);

			displayDiv.id = 'displayDiv';
			displayDiv.style.width = '250px';

			var resources = window.performance.getEntriesByType("resource"); 
			var numberOfResources = resources.length;
			var dataString = 'Resources:<br/>';
			dataString += '<table>'
			for(var idx = 0; idx < numberOfResources; idx++) {
				dataString += '<tr>';
				entry = resources[idx];
				num = entry.duration; //entry.responseEnd - entry.startTime;
				dataString += '<td>';
				dataString += num.toFixed(0);				
				dataString += '</td><td title="'+entry.name+'">';
				dataString += (entry.name).substring(0,30);
				dataString += '...</td></tr>';
			}
			dataString+='</table>';
			dataString+='<br/>click to close';
			displayDiv.innerHTML = dataString;				
		}
	}
}