function _mmowgliFinishedLoading() {
	console.log("_mmowgliFinishedLoading() called");
	// Set css for dbug divs
	var node = document.createElement('style');
	node.innerHTML = '#triggerDbugDiv {position:fixed;bottom:10px;left:10px;width:10px;height:10px;z-index:10000;} \
	 #triggerDbugDiv:hover {cursor:pointer;} \
	 #displayDiv {position:fixed; top:0px; left:0px; font-family:monospace; font-size:10px; background:white; border: 2px solid black; z-index:10000; word-break: break-all;}';
	document.body.appendChild(node);
	
	shipStats();
}

var userInfoData = null;
UserInfo.getInfo(
	function(data) {
	    userInfoData = data;
    },
    function(err){
        console.log("UserInfo error: "+err);
    }
);


// executed inline
//if (window.performance.getEntriesByType && document.addEventListener)
if(!(typeof(window.performance) == "undefinded") &&
   !(typeof(window.performance.getEntriesByType) == "undefined") &&
   !(typeof(document.addEventListener) == 'undefined')) 
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

// Javascript functions called from Java
var doShipStats = true;  // starts off true

function shipStatsOn() {
	doShipStats = true;
}
function shipStatsOff() {
	doShipStats = false;
}

function shipStats() {
	if(!doShipStats)
		return;
	
	if(typeof(edu_nps_moves_mmowgli_utility_browserperformancelogger_loadstatscallback) == "undefined") {
		setTimeout("shipStats()",250);
	}
	else {
		var stats = {};
		if(userInfoData) {
			stats.clientip = userInfoData.ip_address;
			stats.location = userInfoData.city.name+","+userInfoData.continent.name+","+userInfoData.country.name;
		}
		else {
			stats.clientip = "unknown";
			stats.location = "unknown";
		}
		stats.browser = navigator.userAgent;
		stats.postdatetime = myformat(new Date());

		var arr = [];
		stats.resources = arr;
		
		var perfs = window.performance.getEntriesByType("resource"); 
		var numberOfResources = perfs.length;
		for(var idx = 0; idx < numberOfResources; idx++) {
			entry = perfs[idx];
			var line = {
				"url":entry.name,
				"duration":entry.duration
				}
            stats.resources.push(line);
		}
		
		edu_nps_moves_mmowgli_utility_browserperformancelogger_loadstatscallback(stats);
		
		if(window.performance.clearResourceTimings)
			window.performance.clearResourceTimings();
	}
}

function myformat(date) {
	return date.getMonth()+"/"+date.getDate()+   "/"+date.getFullYear()+" "+
	       date.getHours()+":"+date.getMinutes()+":"+date.getSeconds()+ "."+date.getMilliseconds();
}
