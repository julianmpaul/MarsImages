var dates = [];
document.getElementById('dateinput').addEventListener('change', readFileSelect, false);

const observer = lozad();
observer.observe();

function httpGetAsync(theUrl, callback)
{
	document.getElementById('btnFetch').disabled = true;
	var statusList = document.getElementById('statusList');

	dates.forEach(function (date) {
		var xmlHttp = new XMLHttpRequest();
	    
	    xmlHttp.onreadystatechange = function() { 
	        if (xmlHttp.readyState == 4 && xmlHttp.status == 200)
	            callback(xmlHttp.response);
	    }
	    xmlHttp.open("GET", theUrl + "?apiKey=" + document.getElementById('apikey').value + "&dateParam=" + date, true); // true for asynchronous
	    //xmlHttp.setRequestHeader( "Content-Type", "application/json" );
	    xmlHttp.send();

	    var node = document.getElementById(date);
	    if(node) {
	      statusList.removeChild(node);
	    }
	});
}

function httpPostAsync(theUrl, date, callback)
{
	var xmlHttp = new XMLHttpRequest();
    
  xmlHttp.onreadystatechange = function() { 
      if (xmlHttp.readyState == 4 && xmlHttp.status == 200)
          callback(xmlHttp.response);
  }
  xmlHttp.open("POST", theUrl + "?dateParam=" + date, true); // true for asynchronous
  //xmlHttp.setRequestHeader( "Content-Type", "application/json" );
  xmlHttp.send();
}

function processCarousel(response) 
{
  var statusList = document.getElementById('statusList');
	
  var imgdata = JSON.parse(response);
  
	if(imgdata && imgdata.error) {
		var statusElem = document.createElement("li");
	  statusElem.innerText = imgdata.date + " error: " + imgdata.error;

	  statusList.appendChild(statusElem);
  	return;
	}

  if(!imgdata || imgdata.images.length == 0){
  	var statusElem = document.createElement("li");
	  statusElem.id = imgdata.date + "noimage";
	  statusElem.innerText = imgdata.date + " no images found.";

	  statusList.appendChild(statusElem);
  	return;
  }

  var body = document.body;

  //create carousel container
  var container = document.createElement("div");
  container.className = "container";
  container.style = "width: 30%;";
  body.appendChild(container);

  //create carousel header
  var hdr = document.createElement("h4");
  hdr.innerText = imgdata.date;
  container.appendChild(hdr);

  //create carousel
  var carousel = document.createElement("div");
  carousel.className = "carousel slide";
  carousel.id = imgdata.date; //TODO CHANGE 
  carousel.setAttribute('data-ride', 'carousel');
  container.appendChild(carousel);

  //create carousel indicators
  // var indicators = document.createElement("ol");
  // indicators.className = "carousel-indicators";
  // carousel.appendChild(indicators);

  //create inner carousel, image wrapper
  var inner = document.createElement("div");
  inner.className = "carousel-inner";
  carousel.appendChild(inner);
  
  //carousel widgets, left
  var prev = document.createElement("a");
  prev.className = "left carousel-control";
  prev.href = "#"+imgdata.date; //TODO change
  prev.setAttribute('data-slide', 'prev');
  carousel.appendChild(prev);

  var left = document.createElement("span");
  left.className = "glyphicon glyphicon-chevron-left";
  left.setAttribute('aria-hidden', 'true');
  prev.appendChild(left);

  //carousel widgets, right
  var next = document.createElement("a");
  next.className = "right carousel-control";
  next.href = "#"+imgdata.date; //TODO change
  next.setAttribute('data-slide', 'next');
  carousel.appendChild(next);

  var right = document.createElement("span");
  right.className = "glyphicon glyphicon-chevron-right";
  right.setAttribute('aria-hidden', 'true');
  next.appendChild(right);

  for (var i = 0; i < imgdata.images.length; i++) {   
    //create indicator element
    // var li = document.createElement("li");
    // li.setAttribute('data-target', '#'+imgdata.date); //TODO change
    // li.setAttribute('data-slide-to', i);
    // indicators.appendChild(li);

    //create inner carousel image element
    var imageElem = document.createElement("div");
    inner.appendChild(imageElem);

    if(i == 0) { //set first image in carousel active
      // li.className = "active";
      imageElem.className = "item active";
    } else {
      imageElem.className = "item";
    }

    //create img
    var img = document.createElement("img");
    img.className = "lozad"; //lazy loading
    img.src = imgdata.images[i];
    img.style = "width:100%;";
    
    imageElem.appendChild(img);    

    //image caption
    var caption = document.createElement("div");
    caption.className = "carousel-caption";
    imageElem.appendChild(caption); 

    var capTxt = document.createElement("h5");
    capTxt.innerText = (i+1) + "/" + imgdata.images.length;
    caption.appendChild(capTxt);
  }

  var statusElem = document.createElement("li");
  statusElem.id = imgdata.date + "status";
  statusElem.innerText = imgdata.date + " downloading " + imgdata.images.length + " images...";

  statusList.appendChild(statusElem);

  httpPostAsync('/dwnld', imgdata.date, downloadComplete);
}

function downloadComplete(response) 
{
	if(!response){
		console.log("download server error");
		return;
	}

	var statusList = document.getElementById('statusList');
	
  var imgdata = JSON.parse(response);
  var node = document.getElementById(imgdata.date + "status");

  if(imgdata.complete) {
  	node.innerText = imgdata.date + " image download complete.";
  } else {
  	node.innerText = imgdata.date + " image download failed.";
  }
}

function readFileSelect(evt) 
{
	var f = evt.target.files[0]; 

	var statusList = document.getElementById('statusList');

  while (statusList.firstChild) {
      statusList.removeChild(statusList.firstChild);
  }
  dates = [];

  if (f) {
  	document.getElementById('labelFile').value = f.name;

    var r = new FileReader();
    r.onload = function(e) {
      lines = e.target.result.split(/\r?\n/);

        lines.forEach(function (line) {
          if(line.trim()) {
          	var separators = ['\-', ', ', ',', '/', ' '];
          	tokens = line.split(new RegExp(separators.join('|'), 'g'));
          	date = new Date(line);
          	if(!isNaN(date) && tokens.length > 2) {
          		tokens.splice( tokens.indexOf(date.getFullYear().toString()), 1 );

          		if(tokens.indexOf(date.getDate().toString()) > -1) {
          			var datestr = date.toDateString();
                dates.push(datestr);

                var statusElem = document.createElement("li");
                statusElem.id = datestr;
                statusElem.innerText = datestr + " valid earth date.";

                statusList.appendChild(statusElem);
                document.getElementById('btnFetch').disabled = false;
          		} else {
                var statusElem = document.createElement("li");
                statusElem.id = line;
                statusElem.innerText = line + " invalid date/date format";

                statusList.appendChild(statusElem);
              }
          	} else {
          		var statusElem = document.createElement("li");
              statusElem.id = line;
              statusElem.innerText = line + " invalid date/date format";

              statusList.appendChild(statusElem);
          	}
          }
        });
        console.log(dates);
    }
    r.readAsText(f);
  } else { 
    alert("Failed to load file");
  }
}