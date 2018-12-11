function d3_aggregate(div, pids, psa_lists, date_lists, implant_dates, sel_pid, size) {

	var lines = [];
	var non_null_pids = [];
	
	var parseDate = d3.time.format("%Y-%m-%d").parse;
	var dateOutputFormat = d3.time.format("%Y-%m-%d");
	
	var date_sort_asc = function (d1, d2) {
		if (d1[0] > d2[0]) return 1;
		if (d1[0] < d2[0]) return -1;
		return 0;
	};
	
	for (var i = 0; i < pids.length; i++) {
		if (implant_dates[i] != "-1") {
			
			//console.log("PID: " + pids[i]);
			//console.log("IMP Date: " + implant_dates[i]);
			
			non_null_pids.push(pids[i]);
			
			var psa = psa_lists[i];
			var dates = date_lists[i];
		
			var data = [];
			var days = 0;
		
			for (var j = 0; j < dates.length; j++) {
				if (parseDate(dates[j]) < parseDate(implant_dates[i]))
					days = -d3.time.days(parseDate(dates[j]),parseDate(implant_dates[i])).length/365;
				else
					days = d3.time.days(parseDate(implant_dates[i]),parseDate(dates[j])).length/365;
				data.push([parseDate(dates[j]), parseFloat(psa[j]), days]);
			}
		
			data.sort(date_sort_asc);
		
			if (data.length > 0){
				lines.push(data);
			}
			
			//if (data.length == 0){
			//	console.log(pids[i] + " IS 0!!");
			//}
			
			//for (var j = 0; j < data.length; j++) {
			//	console.log("       date/val: " + dateOutputFormat(data[j][0]) + "  " + data[j][1] + "  " + data[j][2]);
			//}
		}
	}
	
	/*for (var i = 0; i < lines.length; i++) {
		console.log("PID: " + non_null_pids[i]);
		console.log("IMP Date: " + implant_dates[i]);
		
		var data = lines[i];
		
		for (var j = 0; j < data.length; j++) {
			console.log("       date/val: " + dateOutputFormat(data[j][0]) + "  " + data[j][1] + "  " + data[j][2]);
		}
	}*/
	
	var margin = {top: 30, right: 40, bottom: 40, left: 50};
	var width = size[0] - margin.left - margin.right;
    var height = size[1] - margin.top - margin.bottom;
	var yMax = d3.max(lines, function(d) { return d3.max(d, function(d) { return d[1];}); });
    var curr_yMax = yMax;
	
    var x = d3.scale.linear()
	  			    .range([0, width]);
    var y = d3.scale.linear()
	  				.range([height, 0]);
    
    var colorScale = d3.scale.category20();

    var xAxis = d3.svg.axis()
		  			  .scale(x)
		  			  .orient("bottom")
		  			  .ticks(5);
    var yAxis = d3.svg.axis()
		  			  .scale(y)
		  			  .orient("left")
		  			  .ticks(5);
    
    var line = d3.svg.line()
    				 .x(function(d) {return x(d[2]);})
    				 .y(function(d) {return y(d[1]);})
    				 .interpolate("linear");
    
    var svg = d3.select(div)
				.append("svg")
				.attr("width",width+margin.left+margin.right)
				.attr("height",height+margin.top+margin.bottom)
				.attr("class","psaChart")
				.append("g")
				.attr("transform", "translate(" + margin.left + "," + margin.top + ")");
    
    svg.append("clipPath")
       .attr("id","chart-area")
       .append("rect")
       .attr("width",width+margin.right)
       .attr("height",height+margin.top)
       .attr("transform", "translate(0,-" + margin.top + ")");
    
    function gen() {
	    
	    x.domain([d3.min(lines, function(d) { return d3.min(d, function(d) { return d[2];}); }),
	              d3.max(lines, function(d) { return d3.max(d, function(d) { return d[2];}); })]);
		y.domain([0, curr_yMax]);
		
		/*var pidGroup = svg.append("g")
		   .attr("id","lines");
		
		pidGroup.selectAll("g")
		   					.data(lines)
		   					.enter()
		   					.append("text")
		   					.datum(function(d){return d;})
		   					.attr("x", function(d){return x(d[d.length - 1][2])+1;})
		   					.attr("y", function(d){return y(d[d.length - 1][1]);})
		   					.attr("r", 40)
		   					.attr("id", function(d,i){return "pid_text"+non_null_pids[i];})
		   					.style("fill",function(d,i){return colorScale(non_null_pids[i]);})
		   					.text(function(d,i){return non_null_pids[i];});*/
		
		
		var lineGroup = svg.append("g")
						   .attr("id","lines")
						   .attr("clip-path","url(#chart-area)");
		
		lineGroup.selectAll("g")
				 .data(lines)
				 .enter()
				 .append("path")
				 .datum(function(d){return d;})
				 //.style("stroke-width",function(d,i){return ((non_null_pids[i] != sel_pid) ? "1px" : "4px");})
				 .style("stroke-width","1px")
				 .attr("d", line)
				 .attr("r", 40)
				 .attr("id", function(d,i){return "name"+non_null_pids[i];})
				 .attr("fill","none")
				 .style("stroke", function(d,i){return colorScale(non_null_pids[i]);})
				 .style("stroke-linejoin", "round")
				 .on("mouseover",function(d,i){mouseOnLine(this,i);})
				 .on("mouseout",function(d,i){mouseOffLine(this,i);})
				 .on("click", function(d,i){window.constructPatientSummary(non_null_pids[i]);});
		
		lineGroup.selectAll("g")
			.data(lines)
			.enter()
			.append("text")
			.datum(function(d){return d;})
			.attr("x", function(d){return x(d[d.length - 1][2])+1;})
			.attr("y", function(d){return y(d[d.length - 1][1]);})
			.attr("r", 40)
			.attr("id", function(d,i){return "pid_"+non_null_pids[i]+"_text";})
			.style("fill",function(d,i){return colorScale(non_null_pids[i]);})
			.text(function(d,i){return non_null_pids[i];});

		/*var ID_Label = svg.append("text")
		   				  .attr("x", width)
		   				  .attr("y", margin.top)
		   				  .append("tspan")
		   				  .style("fill","black")
		   				  .text("ID: ");
		   				  
		var pid_label = ID_Label.append("tspan")
		   				  .style("fill", colorScale(sel_pid))
		   				  .style("font-weight","bold")
		   				  .text(sel_pid);*/
	
		svg.append("line")
		   .attr("class", "implantOrigin")
		   .attr({x1: x(0),
			   	  y1: y(0),
			   	  x2: x(0),
			   	  y2: y(d3.max(lines, function(d) { return d3.max(d, function(d) { return d[1];}); }))});
		
		svg.append("text")
	 	.attr("transform", "rotate(-90)")
	 	.attr("y", x(0))
	 	.attr("x", y(d3.max(lines, function(d) { return d3.max(d, function(d) { return d[1];}); })))
	 	.attr("dy", ".90em")
	 	.text("Center Date")
	 	.style("text-anchor", "end");
		
		svg.append("g")
	    	.attr("class", "yaxis")
	    	.call(yAxis);
		
		svg.append("text")
	 	.attr("transform", "rotate(-90)")
	 	//.attr("y", x(d3.min(data, function(d) { return d[0]; })))
	 	.attr("y", 0)
	 	.attr("x", -(height/2))
	 	.attr("dy", "-4em")
	 	.text("PSA (ng/mL)")
	 	.style("text-anchor", "middle");
		
		svg.append("text")
	 	.attr("x", width/2)
	 	//.attr("y", y(d3.min(data, function(d) { return d[1]; })))
	 	.attr("y", height)
	 	.attr("dy", "4em")
	 	.text("Years")
	 	.style("text-anchor", "middle");
		
		svg.append("g")
		   .attr("class", "xaxis")
		   .attr("transform","translate(0,"+height+")")
		   .call(xAxis);
		
		//d3.select(div)
		//  .on("wheel",zoom);

	    d3.selection.prototype.moveToFront = function() {  
	        return this.each(function(){
	          this.parentNode.appendChild(this);
	        });
	    };
		
		function zoom(){
			if (d3.event.deltaY > 0)
				curr_yMax = curr_yMax + curr_yMax;
			else
				curr_yMax = curr_yMax - curr_yMax/2;
				
			y.domain([0, curr_yMax])
			
			svg.select(".yaxis")
			   .transition()
			   .duration(500)
			   .ease("sin-in-out")
			   .call(yAxis);
			
			lineGroup.selectAll("path")
			   .transition()
			   .duration(500)
			   .ease("sin-in-out")
			   .attr("d", line);
			
			lineGroup.selectAll("text")
			   .transition()
			   .duration(500)
			   .ease("sin-in-out")
			   .attr("x", function(d){return x(d[d.length - 1][2])+1;})
			   .attr("y", function(d){return y(d[d.length - 1][1]);});
	
		}
		
		function mouseOnLine(line,i){
			//pid_label.style("fill", colorScale(non_null_pids[i]))
			//   		 .style("font-weight","bold")
			//   		 .text(non_null_pids[i]);
			
			d3.select("#pid_"+non_null_pids[i]+"_text").moveToFront()
			  .transition()
			  .duration(100)
			  .style("font-weight","bold")
			  .style("font-size", "15px")
			  .style("fill","Black");
			
			d3.select(line).moveToFront()
			  .transition()
			  .duration(100)
			  .style("stroke-width", "4px")
			  .style("stroke-linejoin", "round");
			//d3.select("#name"+sel_pid)
			//  .transition()
			//  .duration(100)
			//  .style("stroke-width","1px");
		}
		
		function mouseOffLine(line,i) {
			//pid_label.style("fill", colorScale(sel_pid))
			//  		 .style("font-weight","bold")
			//  		 .text(sel_pid);
			
			d3.select("#pid_"+non_null_pids[i]+"_text")
			  .transition()
			  .duration(100)
			  .style("font-weight","normal")
			  .style("font-size", "10px")
			  .style("fill",colorScale(non_null_pids[i]));
			
			
			d3.select(line)
			  .transition()
			  .duration(100)
			  .style("stroke-width","1px");
			//d3.select("#name"+sel_pid)
			//  .transition()
			//  .duration(100)
			//  .style("stroke-width", "4px");
		}
		
    }
    
    return gen;
}

function d3_psa_graph(div, psa_data, date_data, implant, size, tabNumber) {

	var data = [];
	
	var parseDate = d3.time.format("%Y-%m-%d").parse;
	var dateOutputFormat = d3.time.format("%b %d, %Y");
	
	for (var i = 0; i < psa_data.length; i++) {
		var next = [parseDate(date_data[i]), parseFloat(psa_data[i])];
		if(findValue(data,next)==-1)
			data.push([parseDate(date_data[i]), parseFloat(psa_data[i])]);
	}
	
	function findValue(array, object) {
	    for(var i = 0; i<array.length; i++) {
	        if(array[i][0].getTime() == object[0].getTime() & array[i][1] == object[1]) return i;
	    }
	    return -1;
	}
	
	var date_sort_asc = function (d1, d2) {
		if (d1[0] > d2[0]) return 1;
		if (d1[0] < d2[0]) return -1;
		return 0;
	};
	
	data.sort(date_sort_asc);
	  
	var margin = {top: 30, right: 20, bottom: 40, left: 50};
	var width = size[0] - margin.left - margin.right;
    var height = size[1] - margin.top - margin.bottom;
    var yMax = d3.max(data, function(d) { return d[1]; });
    var curr_yMax = yMax;
    var number = tabNumber[0];
    
    var pointA;
    var pointB;
    var doubling;
    var doublingLabel;
    var doublingTimeLabel;
    
	var x = d3.time.scale()
			  .range([0, width]);
	var y = d3.scale.linear()
			  .range([height, 0]);
	
	var xAxis = d3.svg.axis()
				  .scale(x)
				  .orient("bottom")
				  .ticks(5);
	var yAxis = d3.svg.axis()
				  .scale(y)
				  .orient("left")
				  .ticks(5);
	
	var line = d3.svg.line()
		         .x(function(d) {return x(d[0]);})
		         .y(function(d) {return y(d[1]);})
		         .interpolate("linear");

	var svg = d3.select(div)
				.append("svg")
				.attr("id", "PSA_SVG")
				.attr("width",width+margin.left+margin.right)
				.attr("height",height+margin.top+margin.bottom)
				.attr("class","psaChart")
				.append("g")
				.attr("transform", "translate(" + margin.left + "," + margin.top + ")");
	
	svg.append("clipPath")
       .attr("id",number+"chart-area")
       .append("rect")
       .attr("width",width)
       .attr("height",height);
	
	svg.append("clipPath")
    .attr("id",number+"eventlabel-area")
    .append("rect")
    .attr("width",width+10)
    .attr("height",height);

	function gen() {

		x.domain([d3.min(data, function(d) { return d[0]; }), d3.max(data, function(d) { return d[0]; })]);
		y.domain([0, curr_yMax]);

		if(implant[0]!="-1"){
			
			svg.append("line")
				.attr("class", "implant_date")
				.attr("id", "implant_date")
				.attr("id2", "eventLine")
				.attr("date", implant[0])
				.attr({
					x1: x(parseDate(implant[0])),
					y1: y(0),
					x2: x(parseDate(implant[0])),
					y2: y(d3.max(data, function(d) { return d[1]; }))});
			
			 svg.append("text")
			 	.attr("id", "implant_date")
			 	.attr("id2", "eventLineLabel")
			 	.attr("date", implant[0])
			 	.attr("transform", "rotate(-90)")
			 	.attr("y", x(parseDate(implant[0])))
			 	.attr("x", y(d3.max(data, function(d) { return d[1]; })))
			 	.attr("dy", ".75em")
			 	.text("Center Date")
			 	.style("text-anchor", "end");
			
		}
		
		var pathGroup = svg.append("g")
		   .attr("id","lines")
		   .attr("clip-path","url(#"+number+"chart-area)");
		
		pathGroup.append("path")
	       .datum(data)
	       .attr("class", "line")
	       .attr("d", line);
	
		svg.append("g")
	       .attr("class", "yaxis")
	       .call(yAxis);
	
		 svg.append("text")
		 	.attr("transform", "rotate(-90)")
		 	//.attr("y", x(d3.min(data, function(d) { return d[0]; })))
		 	.attr("y", 0)
		 	.attr("x", -(height/2))
		 	.attr("dy", "-4em")
		 	.text("PSA (ng/mL)")
		 	.style("text-anchor", "middle");
		 
		 svg.append("text")
		 	.attr("x", width/2)
		 	//.attr("y", y(d3.min(data, function(d) { return d[1]; })))
		 	.attr("y", height)
		 	.attr("dy", "4em")
		 	.text("Date")
		 	.style("text-anchor", "middle");
		
		svg.append("g")
		   .attr("class", "xaxis")
		   .attr("transform","translate(0,"+height+")")
		   .call(xAxis);
		
		var circleGroup = svg.append("g")
		   .attr("id","lines")
		   .attr("clip-path","url(#"+number+"chart-area)");
		
		circleGroup.selectAll("circle")
		   .data(data)
		   .enter().append("circle")
		   .attr("class","dot")
		   .attr("r",3)
		   .attr("cx",function(d){return x(d[0]);})
		   .attr("cy",function(d){return y(d[1]);})
		   .on("click",function(d,i){doublingTime(this,d,i);})
		   .on("mouseover",function(d){showData(this,d);})
		   .on("mouseout",function(){hideData(this);});
	
		d3.select(div)
		  .on("wheel",zoom);
		
		function dragMove() {
			var cur_xMax = x.domain()[1];
			var cur_xMin = x.domain()[0];
			
			console.log("dragMove");
			console.log(d3.event.dx);
			console.log(x(d3.event.dx));
			console.log(x.invert(d3.event.dx));

			var span = d3.time.days(cur_xMin, x.invert(Math.abs(d3.event.dx))).length;
			console.log("span: " + span);
			
			if (d3.event.dx<0){
				cur_xMax = d3.time.day.offset(cur_xMax, span);
				cur_xMin = d3.time.day.offset(cur_xMin, span);
			}else{
				cur_xMax = d3.time.day.offset(cur_xMax, -span);
				cur_xMin = d3.time.day.offset(cur_xMin, -span);
			}

			gen.rescaleX(cur_xMin, cur_xMax, 0);
			
			/*x.domain([cur_xMin, cur_xMax]);
			
			svg.select(".xaxis")
			.call(xAxis);
			
			svg.selectAll(".line")
			   .attr("d", line);
				
			svg.selectAll(".dot")
			   .attr("cx",function(d){return x(d[0]);})
			   .attr("cy",function(d){return y(d[1]);});
				
			svg.selectAll("[id2=eventLine]")
			   .each(function(d){
					d3.select(this)
					  .attr({
					  	x1: x(parseDate(d3.select(this).attr("date"))),
					  	y1: y(0),
					  	x2: x(parseDate(d3.select(this).attr("date"))),
					 	y2: y(curr_yMax)});
				   	});
	
			svg.selectAll("[id2=eventLineLabel]")
			   .each(function(d){
					d3.select(this)
					  .attr("y", x(parseDate(d3.select(this).attr("date"))))
					  .attr("x", y(curr_yMax));
			   });
	
			if (pointA!=null)
				doublingLabel.style("left", x(pointA[0]) + 50 + "px")
					  		 .style("top", y(pointA[1]) + 320 + "px");*/
		}

		var dragBehavior = d3.behavior.drag()
		    .on("drag", dragMove);

		d3.select(div).call(dragBehavior);
		
		function doublingTime(dot,d,i) {
			
			if(doubling!=null){
				if(dot==pointA[2]){
					d3.select(pointA[2])
					  .transition()
					  .duration(50)
					  .style("fill","steelblue");
					d3.select(div).select("#doublingLine").remove();
					d3.select(div).select("#doublingLabel").remove();
					//doublingLabel.remove();
					//doublingTimeLabel.remove();
					doubling = null;
					doublingLabel = null;
					doublingTimeLabel = null;
					pointA = pointB;
					pointB = null;
					return;
				}
				d3.select(pointB[2])
				  .transition()
				  .duration(50)
				  .style("fill","steelblue");
				d3.select(div).select("#doublingLine").remove();
				d3.select(div).select("#doublingLabel").remove();
				//doublingLabel.remove();
				//doublingTimeLabel.remove();
				doubling = null;
				doublingLabel = null;
				doublingTimeLabel = null;
				if(dot==pointB[2]){
					pointB = null;
					return;
				}
				pointB = null;
			}
			
			if(pointA!=null & pointB==null){
				if (dot==pointA[2]){
					d3.select(pointA[2])
						.transition()
						.duration(50)
						.style("fill","steelblue");
					pointA = null;
					return;
				}
			}
	
			if(pointA==null){
				var prev = dot.previousSibling;
				if(prev!=null){
					pointA = [];
					pointA[0] = d[0];
					pointA[1] = d[1];
					pointA[2] = dot;
					d3.select(dot)
					  .transition()
					  .duration(50)
					  .style("fill","rgba(255, 0, 0, 1)");
				
					pointB = [];
					pointB[0] = data[i-1][0];
					pointB[1] = data[i-1][1];
					pointB[2] = prev;
					d3.select(prev)
					  .transition()
					  .duration(50)
					  .style("fill","rgba(255, 0, 0, 1)");
					
					//console.log("PointA " + pointA[0] + " " + pointA[1]);
					//console.log("PointB " + pointB[0] + " " + pointB[1]);
				}
			} else if (pointB==null & dot!=pointA){
				pointB = [];
				pointB[0] = d[0];
				pointB[1] = d[1];
				pointB[2] = dot;
				d3.select(pointB[2])
				.transition()
				.duration(50)
				.style("fill","rgba(255, 0, 0, 1)");
			}
			
			if(pointA!=null & pointB!=null & doubling==null){
				
				if (pointA[0] < pointB[0]){
					var temp = pointA;
					pointA = pointB;
					pointB = temp;
				}
				
				doubling = [];
				doubling.push([pointA[0], pointA[1]]);
				doubling.push([pointB[0], pointB[1]]);
				pathGroup.append("path")
			       		 .datum(doubling)
			       		 .attr("class", "line")
			       		 .attr("id","doublingLine")
			       		 .attr("d", line)
			       		 .style("stroke","rgba(255, 0, 0, 0.3)");
				
				/*doublingLabel = svg.append("text")
					  			   .attr("x", x(pointA[0])+10)
					  			   .attr("y", y(pointA[1])-10)
					  			   .attr("id","doublingLabel")
					  			   .attr("clip-path","url(#chart-area)")
					  			   .append("tspan")
					  			   .style("fill","black")
					  			   .text("Doubling Time: ");*/
				
				var xPos = x(pointA[0]) + 50;
				var yPos = y(pointA[1]) + 320;
				
				var doublingTime = computeDoublingTime(doubling);
				
				doublingLabel = d3.select(div)
				  				  .append("div")
				  				  .attr("class","dt_tooltip")
				  				  .attr("id", "doublingLabel")
				  				  .attr("clip-path","url(#"+number+"chart-area)")
				  				  .style("left", xPos + "px")
				  				  .style("top", yPos + "px")
				  				  .html("<span style='color:white'><strong>Doubling Time: </strong></span><span style='color:rgb(255, 115, 115)'>" + doublingTime + " months" + "</span><br>");
				
				/*doublingTimeLabel = doublingLabel.append("tspan")
					  			   .style("fill", "rgba(255, 0, 0, 1)")
					  			   .style("font-weight","bold")
					  			   .text(doublingTime + " months");*/
			}
			
			function computeDoublingTime(db){
				var days = d3.time.days(db[1][0],db[0][0]).length;
				
				var y1 = db[1][1];
				var y2 = db[0][1];
				var m = (y2-y1)/days;
				var b = y1;
				
				var dT = (2*y1-b)/m;
				
				//console.log("y1: " + y1);
				//console.log("y2: " + y2);
				//console.log(days);
				//console.log(m);
				//console.log(dT);
				return d3.round(dT/30,2);
			}
	
		}
	  
		function zoom(){
			
			var cur_xMax = x.domain()[1];
			var cur_xMin = x.domain()[0];
			var span = d3.time.days(cur_xMin, cur_xMax).length;
			
			//console.log("xmax: " + cur_xMax);
			//console.log("xmin: " + cur_xMin);
			
			//console.log("range: " + d3.time.days(cur_xMin, cur_xMax).length);
			
			if (d3.event.deltaY > 0)
				cur_xMax = d3.time.day.offset(cur_xMax, 365);
			else{
				if (span > 365)
					cur_xMax = d3.time.day.offset(cur_xMax, -365);
			}
			
			//console.log("new xmax: " + cur_xMax);
			//console.log("new xmin: " + cur_xMin);
				
			gen.rescaleX(cur_xMin, cur_xMax, 500);
			
			/*x.domain([cur_xMin, cur_xMax]);
			
			svg.select(".xaxis")
				.transition()
				.duration(500)
				.ease("sin-in-out")
				.call(xAxis);
				
			svg.selectAll(".line")
			   .transition()
			   .duration(500)
			   .ease("sin-in-out")
			   .attr("d", line);
				
			svg.selectAll(".dot")
			   .transition()
			   .duration(500)
			   .ease("sin-in-out")
			   .attr("cx",function(d){return x(d[0]);})
			   .attr("cy",function(d){return y(d[1]);});
				
			svg.selectAll("[id2=eventLine]")
			   .each(function(d){
					d3.select(this)
					  .transition()
					  .duration(500)
					  .ease("sin-in-out")
					  .attr({
					  	x1: x(parseDate(d3.select(this).attr("date"))),
					  	y1: y(0),
					  	x2: x(parseDate(d3.select(this).attr("date"))),
					 	y2: y(curr_yMax)});
				   	});

			svg.selectAll("[id2=eventLineLabel]")
			   .each(function(d){
					d3.select(this)
					  .transition()
					  .duration(500)
					  .ease("sin-in-out")
					  .attr("y", x(parseDate(d3.select(this).attr("date"))))
					  .attr("x", y(curr_yMax));
			   });

			if (pointA!=null)
				doublingLabel.transition()
							 .duration(500)
							 .ease("sin-in-out")
							 .style("left", x(pointA[0]) + 50 + "px")
					  		 .style("top", y(pointA[1]) + 320 + "px");*/

		}
		
		function showData(dot,d){
			var xPos = x(d[0]);
			var yPos = y(d[1]) + 300;
			
			d3.select(div)
			  .append("div")
			  .attr("class","tooltip")
			  .attr("id", "tooltip")
			  .style("left", xPos + "px")
			  .style("top", yPos + "px")
			  .html("<span style='color:white'><strong>PSA: </strong></span><span style='color:cyan'>" + d[1] + "</span><br>" +
				    "<span style='color:white'><strong>Date: </strong></span><span style='color:cyan'>" + dateOutputFormat(d[0]) + "</span>");
			
			d3.select(dot)
			  .transition()
			  .duration(50)
			  .attr("r",6);
		}
		
		function hideData(dot) {
			  
			d3.select("#tooltip").remove();
	
			d3.select(dot)
			  .transition()
			  .duration(100)
			  .attr("r",3);
		}

	}
	
	gen.drawEvent = function(name, date, lineCode, tabNumber, toggle){
		
		//console.log(tabNumber);
		
		var name_id = ("" + name).replace(/ /g, "_");
		name_id = ("" + name_id).replace("?", "");
		
		var data_max = d3.max(data, function(d) { return d[0]; });
		var data_min = d3.min(data, function(d) { return d[0]; });
		var all_line_dates = [];
		
		svg.selectAll("[id2=eventLine]").each(function(d, i){
			var id = d3.select(this).attr("id");
			var new_id = "d" + tabNumber + "_" + date[0] + name_id;
			if(id!=new_id)
				all_line_dates.push([parseDate(d3.select(this).attr("date"))]);
		});
		all_line_dates.sort(date_sort_asc);

		var line_max = d3.max(all_line_dates, function(d) { return d[0]; });
		var line_min = d3.min(all_line_dates, function(d) { return d[0]; });
		
		var max = d3.max([data_max, line_max]);
		var min = d3.min([data_min, line_min]);
		
		//console.log("max: " + max + ", min: " + min);
		
		if(toggle[0]=="true"){
			
			if(parseDate(date[0])>max){
				this.rescaleX(min, parseDate(date[0]), 500);
			} else if(parseDate(date[0])<min){
				this.rescaleX(parseDate(date[0]), max, 500);
			}
			
			svg.append("line")
			.attr("class", lineCode[0])
			.attr("id", "d" + tabNumber + "_" + date[0] + name_id)
			.attr("id2", "eventLine")
			.attr("clip-path","url(#"+number+"chart-area)")
			.attr("date", date[0])
			.attr({
				x1: x(parseDate(date[0])),
				y1: y(0),
				x2: x(parseDate(date[0])),
				y2: y(curr_yMax)});
	
			svg.append("g").attr("clip-path","url(#"+number+"eventlabel-area)").append("text")
			.attr("id", "d" + tabNumber + "_" + date[0] + name_id)
			.attr("id2", "eventLineLabel")
			.attr("date", date[0])
			.attr("transform", "rotate(-90)")
			.attr("y", x(parseDate(date[0])))
			.attr("x", y(curr_yMax))
			.attr("dy", ".75em")
			.text(name)
			.style("text-anchor", "end");
		
		} else {

			svg.selectAll("#d" + tabNumber + "_" + date[0] + name_id).remove();
			
			if(parseDate(date[0])>max){
				this.rescaleX(min, max, 500);
			} else if(parseDate(date[0])<min){
				this.rescaleX(min, max, 500);
			}

		}
		
		return gen;
	};
		
	gen.rescaleX = function(low, high, dur){

		//if(upper){
		//	x.domain([d3.min(data, function(d) { return d[0]; }), newBound])
		//} else {
		//	x.domain([newBound, d3.max(data, function(d) { return d[0]; })])
		//}
			
		x.domain([low, high]);
		
		svg.select(".xaxis")
			.transition()
			.duration(dur)
			.ease("sin-in-out")
			.call(xAxis);
			
		svg.selectAll(".line")
		   .transition()
		   .duration(dur)
		   .ease("sin-in-out")
		   .attr("d", line);
			
		svg.selectAll(".dot")
		   .transition()
		   .duration(dur)
		   .ease("sin-in-out")
		   .attr("cx",function(d){return x(d[0]);})
		   .attr("cy",function(d){return y(d[1]);});
			
		svg.selectAll("[id2=eventLine]")
		   .each(function(d){
				d3.select(this)
				  .transition()
				  .duration(dur)
				  .ease("sin-in-out")
				  .attr({
				  	x1: x(parseDate(d3.select(this).attr("date"))),
				  	y1: y(0),
				  	x2: x(parseDate(d3.select(this).attr("date"))),
				 	y2: y(curr_yMax)});
			   	});

		svg.selectAll("[id2=eventLineLabel]")
		   .each(function(d){
				d3.select(this)
				  .transition()
				  .duration(dur)
				  .ease("sin-in-out")
				  .attr("y", x(parseDate(d3.select(this).attr("date"))))
				  .attr("x", y(curr_yMax));
		   });

		if (pointA!=null)
			doublingLabel.transition()
						 .duration(dur)
						 .ease("sin-in-out")
						 .style("left", x(pointA[0]) + 50 + "px")
				  		 .style("top", y(pointA[1]) + 320 + "px");

		return gen;
	};
	
	return gen;
}

function d3_kmchart(div, lineX_lists, lineY_lists, censX_lists, censY_lists, atRisk_lists, size, names, n) {

	var data = [];
	var cens = [];
	var atRisk = [];

	for (var i = 0; i < lineX_lists.length; i++) {
		var linex = lineX_lists[i];
		var liney = lineY_lists[i];

		var lines = [];
		
		for (var j = 0; j < linex.length; j++) {
			lines.push([parseFloat(linex[j]), parseFloat(liney[j])]);
		}

		data.push(lines);
	}

	for (var i = 0; i < censX_lists.length; i++) {
		var censx = censX_lists[i];
		var censy = censY_lists[i];
		
		var marks = [];
		
		//console.log("start");
		for (var j = 0; j < censx.length; j++) {
			marks.push([parseFloat(censx[j]), parseFloat(censy[j])]);
			//console.log(marks[j]);
		}

		cens.push(marks);
	}
	
	for (var i = 0; i < atRisk_lists.length; i++) {
		var atRisk_days = atRisk_lists[i];

		var days = [];
		
		//console.log("Start");
		for (var j = 0; j < atRisk_days.length; j++) {
			days.push([parseFloat(atRisk_days[j])]);
			//console.log(atRisk_days[j]);
		}

		atRisk.push(days);
	}
	
	var margin = {top: 30, right: 120, bottom: 240, left: 50};
	var width = size[0] - margin.left - margin.right;
    var height = size[1] - margin.top - margin.bottom;
    var xMax = d3.max(data, function(d) { return d3.max(d, function(d) { return d[0];}); });
    var xMin = d3.min(data, function(d) { return d3.min(d, function(d) { return d[0];}); });
    var yMax = d3.max(data, function(d) { return d3.max(d, function(d) { return d[1];}); });
    var curr_yMax = yMax;
    var curr_xMax = xMax;
    var curr_xMin = xMin;
    var legend;
    var cursor;
    var raxis_buff = 50;

	var x = d3.scale.linear()
			  .range([0, width]);
	var y = d3.scale.linear()
			  .range([height, 0]);
	
	var colorScale = d3.scale.category10();
	
	var xAxis = d3.svg.axis()
				  .scale(x)
				  .orient("bottom")
				  .ticks(20);
	var yAxis = d3.svg.axis()
				  .scale(y)
				  .orient("left")
				  .ticks(10);
	
	var line = d3.svg.line()
		         .x(function(d) {return x(d[0]);})
		         .y(function(d) {return y(d[1]);})
		         .interpolate("linear");
	
	var svg = d3.select(div)
				.append("svg")
				.attr("id", "PSA_SVG")
				.attr("width",width+margin.left+margin.right)
				.attr("height",height+margin.top+margin.bottom)
				.attr("class","psaChart")
				.append("g")
				.attr("transform", "translate(" + margin.left + "," + margin.top + ")");
	
	 svg.append("clipPath")
     	.attr("id","chart-area")
     	.append("rect")
     	.attr("width",width)
     	.attr("height",height);

	 function gen() {
		 
		 	x.domain([0, curr_xMax + 0.05*curr_xMax]);
		 	y.domain([0, curr_yMax]);
		 	
		 	//console.log("curr_xMax: " + curr_xMax);
		 	//console.log("calc dom: " + (curr_xMax + 0.05*curr_xMax));
		 	//console.log("x.domain: " + x.domain()[0] + "," +  x.domain()[1]);
		 	
		 	
		 	var pathGroup = svg.append("g")
		 					   .attr("id","lines")
		 					   .attr("clip-path","url(#chart-area)");
	
		 	pathGroup.selectAll("g")
			 .data(data)
			 .enter()
			 .append("path")
			 .datum(function(d){return d;})
			 .attr("class", "line")
			 .attr("d", line)
			 .attr("id", function(d,i){return "name"+i;})
			 .attr("fill","none")
			 .style("stroke", function(d,i){return colorScale(i);});
		 	
			/*pathGroup.append("path")
		       .datum(data)
		       .attr("class", "line")
		       .attr("d", line);*/
		
			svg.append("g")
		       .attr("class", "yaxis")
		       .call(yAxis);
			
			svg.append("g")
			   .attr("class", "xaxis")
			   .attr("transform","translate(0,"+height+")")
			   .call(xAxis);
		
			 svg.append("text")
			 	.attr("transform", "rotate(-90)")
			 	.attr("y", 0)
			 	.attr("x", -(height/2))
			 	.attr("dy", "-4em")
			 	.text("Percent Survival")
			 	.style("text-anchor", "middle");
			 
			 svg.append("text")
			 	.attr("x", width/2)
			 	.attr("y", height)
			 	.attr("dy", "4em")
			 	.text("Years")
			 	.style("text-anchor", "middle");
			 
			 legend = svg.append("g")
 			.attr("class","legend")
 			.attr("height", 100)
 			.attr("width", 100);

			 cursor = {x: width + 20, y:  9};
			 
			 for (var i = 0; i < data.length; i++) {
				 addRow(names[i],n[i],colorScale(i));
			 }
			 
			 for (var i = 0; i < atRisk.length; i++) {
				 if (i==atRisk.length-1)
					 addRiskAxis(i,colorScale(i),atRisk[i],true);
				 else
					 addRiskAxis(i,colorScale(i),atRisk[i],false);
			 }
			
			/*pathGroup.selectAll("g")
			 .data(data)
			 .enter()
			 .append("path")
			 .datum(function(d){return d;})
			 .attr("class", "line")
			 .attr("d", line)
			 .attr("id", function(d,i){return "name"+i;})
			 .attr("fill","none")
			 .style("stroke", function(d,i){return colorScale(i);});*/
			
			/*for (var i = 0; i < cens.length; i++) {
			
			var marks = cens[i];
		
			censGroup.selectAll("rect")
			   .data(marks)
			   .enter().append("rect")
			   .attr("class","cens")
			   .attr("width",1)
			   .attr("height",5)
			   .attr("x",function(d){return x(d[0]);})
			   .attr("y",function(d){return y(d[1])-2;})
			   .style("stroke", function(d,i){return colorScale(i);});
			}*/
			
			
			/*censGroup.selectAll("rect")
			 .data(cens)
			 .datum(function(d){return d;})
			 .enter()
			 .append("rect")
			 .attr("class","cens")
			 .attr("id", function(d,i){return "name"+i;})
			 .attr("width",1)
			 .attr("height",5)
			 .attr("x",function(d){return x(d[0]);})
			 .attr("y",function(d){return y(d[1])-2;})
			 .style("stroke", function(d,i){return colorScale(i);});*/
			
			 var censGroup = svg.append("g")
			   .attr("id","lines")
			   .attr("clip-path","url(#chart-area)");
			 
			/*censGroup.selectAll("rect")
			   .data(cens)
			   .enter().append("rect")
			   .attr("class","cens")
			   .attr("width",1)
			   .attr("height",5)
			   .attr("x",function(d){return x(d[0]);})
			   .attr("y",function(d){return y(d[1])-2;})
			   .style("stroke", function(d,i){return colorScale(i);});*/
			
			censGroup.selectAll("g")
			 .data(cens)
			 .enter()
			 .append("g")
			 .selectAll("rect")
			 .data(function(d){return d;})
			 .enter()
			 .append("rect")
			 .attr("class","cens")
			 .attr("width",1)
			 .attr("height",5)
			 .attr("x",function(d){return x(d[0]);})
			 .attr("y",function(d){return y(d[1])-2;})
			 .style("stroke", function(d,i,j){return colorScale(j);})
			 .on("click",function(d,i){console.log(d[0] + " " + d[1]);});
			   

			/*pathGroup.selectAll("g")
			 .data(data)
			 .enter()
			 .append("path")
			 .datum(function(d){return d;})
			 .attr("class", "line")
			 .attr("d", line)
			 .attr("id", function(d,i){return "name"+i;})
			 .attr("fill","none")
			 .style("stroke", function(d,i){return colorScale(i);});*/
			   
			//d3.select(div)
			//  .on("wheel",zoom);
		
			function zoom(){
				
				if (d3.event.deltaY > 0)
					curr_xMax = curr_xMax + curr_xMax;
				else
					curr_xMax = curr_xMax - curr_xMax/2;
					
				x.domain([0, curr_xMax + 0.05*curr_xMax]);
				
				svg.select(".xaxis")
				   .transition()
				   .duration(500)
				   .ease("sin-in-out")
				   .call(xAxis);
				
				svg.selectAll(".line")
				   .transition()
				   .duration(500)
				   .ease("sin-in-out")
				   .attr("d", line);
				
				svg.selectAll(".cens")
				   .transition()
				   .duration(500)
				   .ease("sin-in-out")
				   .attr("x",function(d){return x(d[0]);})
				   .attr("y",function(d){return y(d[1])-2;});
				
			}
			
			function addRow(label,n,color){

				var rect = legend.append("rect")
			  	  .attr("x", cursor.x)
			  	  .attr("y", cursor.y)
			      .attr("width", 10)
			      .attr("height", 10)
			      .style("fill", color)
			      .style("cursor", "pointer");
				
				legend.append("text")
		  		  	  .attr("x", cursor.x + 12)
		  		  	  .attr("y", cursor.y + 9)
		  		  	  .text(label + " (" + n + ")");

				cursor.y = cursor.y + 14;
				
				return rect;
				
			}
			
			function addRiskAxis(i,color,atRisk,fin){

				var atRisk_ticks = 14;
				var maxDomain = (curr_xMax + 0.05*curr_xMax)*365;
				
				var tickVals = [];
				var tickPositions = [];
				
				for (var k = atRisk_ticks - 1; k >= 0; k--) {
					if (d3.round((maxDomain/13.0)*k,0) >= atRisk.length) {
						tickVals[k] = "0";
						//console.log("0 at " + k + " rounded index: " + d3.round((maxDomain/13.0)*k,0));
					} else {
						tickVals[k] = "" + 1*(atRisk[d3.round((maxDomain/13.0)*k,0)]);
						//console.log(atRisk[d3.round((maxDomain/13.0)*k,0)] + " at " + k + " rounded index: " + d3.round((maxDomain/13.0)*k,0));
					}
					tickPositions[k] = (width/13.0)*k;
				}
				
				//console.log("tickvals");
				//for (var j = 0; j < tickVals.length; j++) {
				//	console.log(tickVals[j]);
				//}
				//console.log("tickPositions");
				//for (var j = 0; j < tickPositions.length; j++) {
				//	console.log(tickPositions[j]);
				//}

				//console.log("size of " + i + ": " + atRisk.length);
				
				/*var r = d3.scale.linear()
				  		  .range([0, width]);
				 
				r.domain([0, curr_xMax + 0.05*curr_xMax]);*/
				
				// Your custom scale:
				var customScale = d3.scale.linear()
				        			//.domain([2000,0])
				        			//.range([0,width]);
									//.domain([0,150,200,250,300])
									//.range([0,150,200,250,3000]);
									//.domain(tickVals)
									//.range(tickPositions);
									.domain([0,1,2,3,4,5,6,7,8,9,10,11,12,13])
									.range(tickPositions);
			
				// The axis uses the above scale and the same domain:
				//var r = d3.svg.axis()
				//        	 .scale(customScale)
				//        	 .tickValues([125,250,500,1000,2000]);
				
				
				var pos = height + raxis_buff + (i+1)*20;
				
				var rAxis = d3.svg.axis() //Risk axis
	  			  .scale(customScale)
	  			  //.tickValues([0,150,200,250,300])
	  			  //.ticks(14)
	  			  //.tickValues([tickVals])
	  			  .orient("bottom")
	  			  //.ticks(14);
	  			  .ticks(tickVals.length)
	  			  .tickFormat(function(d) {
	  				  return tickVals[d];
	  			  });
				
				svg.append("g")
				   .attr("class", "raxis")
				   .attr("id", "atRisk"+i)
				   .attr("transform","translate(0,"+pos+")")
				   .call(rAxis);

				svg.select("[id=atRisk"+i+"]")
				   .selectAll("text")
				   .attr("fill",color);
				
				svg.select("[id=atRisk"+i+"]")
				   .selectAll("line")
				   .attr("stroke",color);
				
				svg.select("[id=atRisk"+i+"]")
				   .selectAll("path")
				   .attr("stroke",color);
				
				if (fin){
					svg.append("text")
			 	   	   .attr("x", width/2)
			 	   	   .attr("y", pos)
			 	   	   .attr("dy", "4em")
			 	   	   .text("# At Risk")
			 	   	   .style("text-anchor", "middle");
				}
				
			}
			
	 }

	/*function showData(dot,d){
		var xPos = x(d[0]);
		var yPos = y(d[1]) + 300;
		
		d3.select(div)
		  .append("div")
		  .attr("class","tooltip")
		  .attr("id", "tooltip")
		  .style("left", xPos + "px")
		  .style("top", yPos + "px")
		  .html("<span style='color:white'><strong>PSA: </strong></span><span style='color:cyan'>" + d[1] + "</span><br>" +
			    "<span style='color:white'><strong>Date: </strong></span><span style='color:cyan'>" + dateOutputFormat(d[0]) + "</span>");
		
		d3.select(dot)
		  .transition()
		  .duration(50)
		  .attr("r",6);
	}
	
	function hideData(dot) {
		  
		d3.select("#tooltip").remove();

		d3.select(dot)
		  .transition()
		  .duration(100)
		  .attr("r",3);
	}*/
	
	return gen;
}

function d3_barchart(div, y_data, x_data, size, title) {

	var data = [];
	var div_id = "graph_";

	for (var i = 0; i < y_data.length; i++) {
		data.push([x_data[i], y_data[i]]);
	}

	var date_sort_des = function (d1, d2) {
		if (parseFloat(d1[1]) < parseFloat(d2[1])) return 1;
		if (parseFloat(d1[1]) > parseFloat(d2[1])) return -1;
		return 0;
	};
	
	data.sort(date_sort_des);
	
	var margin = {top: 20, right: 20, bottom: 50, left: 50};
	var width = size[0] - margin.left - margin.right;
    var height = size[1] - margin.top - margin.bottom;
    var yMax = d3.max(data, function(d){return parseFloat(d[1]);});

    var x = d3.scale.ordinal()
    		  .rangeRoundBands([0, width], 0.015);
	var y = d3.scale.linear()
			  .range([height, 0]);
	
	var xAxis = d3.svg.axis()
				  .scale(x)
				  .orient("bottom")
				  .ticks(5);
	var yAxis = d3.svg.axis()
				  .scale(y)
				  .orient("left")
				  .ticks(5);
	
	/*var dl_link = d3.select(div.parentNode).append("a")
	  .attr("id","download_"+div_id)
	  .attr("href","#")
	  .on("click",function(){
	      d3.select(this)
	        .attr("href", 'data:application/octet-stream;base64,' + btoa(d3.select("#"+div_id).html()))
	        .attr("download", "viz.svg") 
	    })
	  .text("linklink");*/
	
	/*var dl_link = d3.select(div.parentNode).append("a")
	  .attr("id","download_"+div_id)
	  .attr("href","#")
	  .on("click",function(){
		  
		  var doctype = '<?xml version="1.0" standalone="no"?>'
			  + '<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">';
		  
		  var source = (new XMLSerializer()).serializeToString(d3.select("#download_"+div_id).node());
		  
		  var blob = new Blob([ doctype + source], { type: 'image/svg+xml;charset=utf-8' });
		  
		  var url = window.URL.createObjectURL(blob);
		  
		  var img = d3.select(div.parentNode).append('img')
		  .attr('width', 100)
		  .attr('height', 100)
		  .node();
	       
		  img.onload = function(){
			  // Now that the image has loaded, put the image into a canvas element.
			  var canvas = d3.select(div.parentNode).append('canvas').node();
			  canvas.width = 100;
			  canvas.height = 100;
			  var ctx = canvas.getContext('2d');
			  ctx.drawImage(img, 0, 0);
			  var canvasUrl = canvas.toDataURL("image/png");
			  var img2 = d3.select(div.parentNode).append('img')
			    .attr('width', 100)
			    .attr('height', 100)
			    .node();
			  // this is now the base64 encoded version of our PNG! you could optionally 
			  // redirect the user to download the PNG by sending them to the url with 
			  // `window.location.href= canvasUrl`.
			  img2.src = canvasUrl; 
			  d3.select(this)
		        .attr("href", canvasUrl).attr("download", "viz.png");
			}
			// start loading the image.
			img.src = url;
			
			//d3.select(this)
	        //.attr("href", canvasUrl).attr("download", "viz.png");
		  
	    })
	  .text("linklink");*/
	
	var svg = d3.select(div)
				.attr("id", div_id)
				.append("svg")
				.attr("id", "PSA_SVG")
				.attr("width",width+margin.left+margin.right)
				.attr("height",height+margin.top+margin.bottom)
				.attr("xmlns", "http://www.w3.org/2000/svg")
				.attr("font-family","sans-serif")
				.attr("font-size","10px")
				.attr("shape-rendering","crispEdges")
				.attr("class","psaChart")
				.append("g")
				.attr("transform", "translate(" + margin.left + "," + margin.top + ")");
	
    svg.append("clipPath")
    .attr("id","chart-area")
    .append("rect")
    .attr("width",width-1) // this is so that the stroke on the bars doesn't obscure the axis line
    .attr("height",height+margin.top)
    .attr("transform", "translate(1,-" + margin.top + ")");
    
	function gen() {
		
		x.domain(data.map(function(d){return d[0];}));
		y.domain([0, (yMax)]);

		svg.append("g")
	       .attr("class", "yaxis")
	       .attr("shape-rendering","crispEdges")
	       .call(yAxis)
	       .selectAll("text")
	       .attr("font-family","sans-serif")
		     .attr("font-size","10px");
	
		 svg.append("text")
		 	.attr("transform", "rotate(-90)")
		 	.attr("font-family","sans-serif")
			.attr("font-size","10px")
		 	.attr("y", 0)
		 	.attr("x", -(height/2))
		 	.attr("dy", "-4em")
		 	.text(title)
		 	.style("text-anchor", "middle");
		
		
		svg.append("g")
		   .attr("class", "xaxis")
	       .attr("shape-rendering","crispEdges")
		   .attr("transform","translate(0,"+height+")")
		   .call(xAxis)
		     .selectAll("text")
		     .attr("y", 0)
		     .attr("x", 9)
		     .attr("dy", ".35em")
		     .attr("transform", "rotate(90)")
		     .style("text-anchor", "start")
		     .attr("font-family","sans-serif")
		     .attr("font-size","10px");
		
		svg.selectAll("line")
	     .attr("fill", "none")
	     .attr("stroke","black");
		
		svg.selectAll("path")
	     .attr("fill", "none")
	     .attr("stroke","black");
		
		var barGroup = svg.append("g")
		   .attr("id","bars")
		   .attr("clip-path","url(#chart-area)");

		barGroup.selectAll("g")
			.data(data)
			.enter().append("rect")
			.style("fill", "LightBlue")
			.attr("class", "bar")
			.attr("x", function(d) { return x(d[0]); })
			.attr("y", function(d) { return y(d[1]); })
			.attr("width", x.rangeBand())
			.attr("height", function(d) {return height - y(d[1]);})
			.on("mouseover", function(d) {
			  	d3.select(this).style("fill", d3.hcl(-97, 32, 30))
			})
			.on("mouseout", function(d, i) {
				d3.select(this).style("fill", "LightBlue");
			});

		barGroup.selectAll("g")
			.data(data)
			.enter().append("text")
			.attr("text-anchor", "middle")
			.attr("x", function(d) {return x(d[0]) + x.rangeBand()/2;})
			.attr("y", function(d) {return y(d[1]) - 2; })
			.text(function(d) {return d[1];});
	
	}
	
	gen.export_module = function(){
		//console.log("EXPORT THIS CHART!!!!");
		//console.log(title);
		
		/*var img = d3.select(div.parentNode)
		.append('div')
		.append('img')
		.attr('width',width+margin.left+margin.right)
		.attr('height',height+margin.top+margin.bottom)
		.attr('src',svgDataURL(div.querySelector('svg')));
		
		var sourceImage = new Image;
		sourceImage.width = width+margin.left+margin.right;
		sourceImage.height = height+margin.top+margin.bottom;
		
		var can = d3.select(div.parentNode)
		.append('div')
		.append('canvas')
		.attr('width',width+margin.left+margin.right)
		.attr('height',height+margin.top+margin.bottom);
		
		var ctx = can[0][0].getContext('2d');
		
		sourceImage.onload = function(){
			ctx.drawImage(sourceImage,0,0,width+margin.left+margin.right,height+margin.top+margin.bottom);
			var img2 = d3.select(div.parentNode)
			.append('div')
			.append('img')
			.attr('width',width+margin.left+margin.right)
			.attr('height',height+margin.top+margin.bottom)
			.attr('src',can[0][0].toDataURL());
		};
		sourceImage.src = svgDataURL(div.querySelector('svg'));
		
		function svgDataURL(svg){
			var svgAsXML = (new XMLSerializer).serializeToString(svg);
			return "data:image/svg+xml," + encodeURIComponent(svgAsXML);
		}*/
		
		
		
		
		
		
		
		
		
		/*var html = d3.select(svg)
        	.attr("version", 1.1)
        	.attr("xmlns", "http://www.w3.org/2000/svg")
        	.node().parentNode.innerHTML;

		console.log(html);
		var imgsrc = 'data:image/svg+xml;base64,'+ btoa(html);
		var img = '<img src="'+imgsrc+'">'; 
		d3.select("#svgdataurl").html(img);
		
		var canvas = document.querySelector("canvas");
		var context = canvas.getContext("2d");

		var image = new Image;
		image.src = imgsrc;
		image.onload = function() {
		  context.drawImage(image, 0, 0);

		  var canvasdata = canvas.toDataURL("image/png");

		  var pngimg = '<img src="'+canvasdata+'">'; 
	  	  d3.select("#pngdataurl").html(pngimg);

		  var a = document.createElement("a");
		  a.download = "sample.png";
		  a.href = canvasdata;
		  a.click();
	
	  };*/
	  
		//d3.select(dl_link).d3Click();
		//d3.select("#download_"+div_id).d3Click();
		
		var dl_link = d3.select(div.parentNode).append("a")
	  	.attr("id","download_"+title)
	  	.attr("href", 'data:application/octet-stream;base64,' +  btoa(d3.select(div).html()))
	  	.attr("download", title+".svg");
	  
	  //location.href = 'data:application/octet-stream;base64,' + btoa(svg[0][0]);
	  dl_link[0][0].click();
	  
	  dl_link.remove();
	 
		//console.log(svg.attr("width"));
		//console.log(d3.select(svg).attr("height"));

	};
	
	return gen;
}

function d3_histogram(div, f_data, size, title, dim) {

	var margin = {top: 20, right: 20, bottom: 50, left: 50};
	var width = size[0] - margin.left - margin.right;
    var height = size[1] - margin.top - margin.bottom;

	//var stuff = [-3, -2, -1, 0,1, 2,3, 4, 5,6, 7,8, 9,10];
    var stuff = f_data;
    //console.log(f_data);
    
	//var min = 20;
	//var max = 100;
	//var sub = 2;
	//var tick_interval = 10;
	var min = dim[0];
	var max = dim[1];
	var sub = dim[2];
	var tick_interval = dim[3];
	
	
	//console.log("min: " + min + " max: " + max + " sub: " + sub + " tick: " + tick_interval);
	
	var bin_interval = tick_interval/sub;
	var numTicks = ((max-min)/tick_interval) + 1;
	var numBins = (numTicks-1)*sub;
	var tickvals = [];
	var binvals = [];
	
	for(i=0; i<numTicks; i++){
		tickvals[i] = min + tick_interval*i;
	}
	
	for(i=0; i<=numBins; i++){
		binvals[i] = min + bin_interval*i;
	}
	
	//console.log("sub: " + sub);
	//console.log("tickvals: " + tickvals);
	//console.log("binvals: " + binvals);
	//console.log("numTicks: " + numTicks);
	//console.log("numBins: " + numBins);
	//console.log("tick_interval: " + tick_interval);
	//console.log("bin_interval: " + bin_interval);
	
	var x = d3.scale.linear().domain([min, max]).range([0, width]);
	var data = d3.layout.histogram().bins(binvals)(stuff);
	
	//Axes and scales
	var yhist = d3.scale.linear()
	                .domain([0, d3.max(data, function(d) { return d.y; })])
	                .range([height, 0]);

	var xAxis = d3.svg.axis()
	              .scale(x)
	              .orient('bottom').tickValues(tickvals);

	var yAxis = d3.svg.axis()
	              .scale(yhist)
	              .orient('left');
	
	//Draw svg
	var svg = d3.select(div).append("svg")
			.attr("id", "PSA_SVG")
			.attr("class","psaChart")
            .attr("width", width+ margin.left + margin.right)
            .attr("height", height + margin.top + margin.bottom)
            .attr("xmlns", "http://www.w3.org/2000/svg")
            .attr("font-family","sans-serif")
            .attr("font-size","10px")
            .attr("shape-rendering","crispEdges")
            .append("g")
            .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

	svg.append("clipPath")
			.attr("id","chart-area")
			.append("rect")
			.attr("width",width-1) // this is so that the stroke on the bars doesn't obscure the axis line
			.attr("height",height+margin.top)
			.attr("transform", "translate(1,-" + margin.top + ")");

	function gen() {
	
		//Draw histogram
		var bar = svg.selectAll(".bar")
		              .data(data)
		              .enter().append("g")
		              .attr("class", "bar")
		              .attr("clip-path","url(#chart-area)")
		              .attr("transform", function(d) { return "translate(" + x(d.x) + "," + yhist(d.y) + ")"; });
	
		bar.append("rect")
			.style("fill", "LightBlue")
		    .attr("x", 0)
		    .attr("width", width/numBins)
		    .attr("height", function(d) { return height - yhist(d.y);})
		    .on("mouseover", function(d) {
				  	d3.select(this).style("fill", d3.hcl(-97, 32, 30))
				  	d3.select(this.parentNode).append("text")
					.attr("clip-path","url(#chart-area)")
			    	.attr("dy", ".75em")
			    	.attr("y", -12)
			    	.attr("x", (width/numBins)/2);
			    	//.attr("text-anchor", "middle")
			    	//.text(function(d){return d.y;});
			  })
			.on("mouseout", function(d, i) {
					d3.select(this).style("fill", "LightBlue");
					//d3.select(this.parentNode).select("text").remove();
			});

		var lineGroup = svg.append("g")
		   .attr("id","lines")
		   .attr("clip-path","url(#chart-area)");
		
		lineGroup.selectAll("line")
		   .data(binvals)
		   .enter().append("line")
		   .attr("fill","none")
		   .style("stroke-width","1px")
		   .style("stroke", "white")
		   .attr({
			   x1: function(d){return x(d);},
			   y1: yhist(0),
			   x2: function(d){return x(d);},
			   y2: yhist(d3.max(data, function(d) { return d.y; }))});

		//Draw axes
		svg.append("g")
		    .attr("class", "xaxis")
		    .attr("shape-rendering","crispEdges")
		    .attr("transform", "translate(0," + height + ")")
		    .call(xAxis)
		    .selectAll("text")
		     .attr("font-family","sans-serif")
		     .attr("font-size","10px");
	
		svg.append("g")
		    .attr("class", "yaxis")
		    .attr("shape-rendering","crispEdges")
		    .call(yAxis)
		    .selectAll("text")
	       .attr("font-family","sans-serif")
		     .attr("font-size","10px");
		 
		 svg.append("text")
		 	.attr("transform", "rotate(-90)")
		 	.attr("font-family","sans-serif")
			.attr("font-size","10px")
		 	.attr("y", 0)
		 	.attr("x", -(height/2))
		 	.attr("dy", "-4em")
		 	.text(title)
		 	.style("text-anchor", "middle");
		 
			svg.selectAll("line")
		     .attr("fill", "none")
		     .attr("stroke","black");
			
			svg.selectAll("path")
		     .attr("fill", "none")
		     .attr("stroke","black");
		
		 /*svg.append("text")
		 	.attr("x", width/2)
		 	.attr("y", 3)
		 	.attr("dy", "4em")
		 	.text(title)
		 	.style("text-anchor", "middle");*/
		
		//d3.select(div)
		//  .on("wheel",divide);
		
		function divide() {
			if (d3.event.deltaY > 0)
				if (sub<10)
					sub = sub + 1;
				else
					return;
			else
				if (sub>1)
					sub = sub - 1;
				else
					return;
			
			bin_interval = tick_interval/sub;
			numBins = (numTicks-1)*sub;
			
			binvals = [];
			
			for(i=0; i<=numBins; i++){
				binvals[i] = min + bin_interval*i;
			}
			
			/*console.log("############################");
			console.log("sub: " + sub);
			console.log("tickvals: " + tickvals);
			console.log("binvals: " + binvals);
			console.log("numTicks: " + numTicks);
			console.log("numBins: " + numBins);
			console.log("tick_interval: " + tick_interval);
			console.log("bin_interval: " + bin_interval);*/
			
			data = d3.layout.histogram().bins(binvals)(stuff);
				
			yhist.domain([0, d3.max(data, function(d) { return d.y; })])
			
			svg.selectAll(".bar").remove();
			svg.select("#lines").remove();
			
			bar = svg.selectAll(".bar")
            .data(data)
            .enter().append("g")
            .attr("class", "bar")
            .attr("clip-path","url(#chart-area)")
            .attr("transform", function(d) { return "translate(" + x(d.x) + "," + yhist(d.y) + ")"; });

			bar.append("rect")
			.style("fill", "steelblue")
		    .attr("x", 0)
		    .attr("width", width/numBins)
		    .attr("height", function(d) { return height - yhist(d.y);})
		    .on("mouseover", function(d) {
				  	d3.select(this).style("fill", d3.hcl(-97, 32, 30))
				  	d3.select(this.parentNode).append("text")
					.attr("clip-path","url(#chart-area)")
			    	.attr("dy", ".75em")
			    	.attr("y", -12)
			    	.attr("x", (width/numBins)/2)
			    	.attr("text-anchor", "middle")
			    	.text(function(d){return d.y;});
			  })
			.on("mouseout", function(d, i) {
					d3.select(this).style("fill", "steelblue");
					d3.select(this.parentNode).select("text").remove();
			});

			lineGroup = svg.append("g")
			   .attr("id","lines")
			   .attr("clip-path","url(#chart-area)");
			
			lineGroup.selectAll("line")
			   .data(binvals)
			   .enter().append("line")
			   .attr("fill","none")
			   .style("stroke-width","1px")
			   .style("stroke", "white")
			   .attr({
				   x1: function(d){return x(d);},
				   y1: yhist(0),
				   x2: function(d){return x(d);},
				   y2: yhist(d3.max(data, function(d) { return d.y; }))});
			
			svg.select(".yaxis")
			   .call(yAxis);

		}

	}
	
	gen.export_module = function(){
		
		var dl_link = d3.select(div.parentNode).append("a")
	  	.attr("id","download_"+title)
	  	.attr("href", 'data:application/octet-stream;base64,' +  btoa(d3.select(div).html()))
	  	.attr("download", title+".svg");
	  
	  dl_link[0][0].click();
	  dl_link.remove();

	};
	
	return gen;
	
}

function d3_tabulate(div, rows, columns, cells, size, title) {
	
	console.log("rows: " + rows);
	console.log("columns: " + columns);
	console.log("data: " + cells);
	
	var margin = {top: 50, right: 50, bottom: 50, left: 50};
	var width = size[0] - margin.left - margin.right;
    var height = size[1] - margin.top - margin.bottom;
    //var yMax = d3.max(data, function(d){return parseFloat(d[1]);});
    
	/*var svg = d3.select(div)
		.append("svg")
		.attr("id", "PSA_SVG")
		.attr("width",width+margin.left+margin.right)
		.attr("height",height+margin.top+margin.bottom)
		.attr("class","psaChart")
		.append("g")
		.attr("transform", "translate(" + margin.left + "," + margin.top + ")");*/

	function gen() {
		var table = d3.select(div)
							//.attr("style", "height: " + height + "px; width: " + width + "px; margin-top: " + margin.top + "px; margin-bottom: " + margin.bottom + "px; margin-left: " + margin.left + "px; margin-right: " + margin.right + "px;")
							.append("table")
							.attr("cellpadding",10)
							.attr("id", title);
							//.attr("border",1);
		var thead = table.append("thead");
		var tbody = table.append("tbody");

		var row_size = columns.length;
		console.log("row_size: " + row_size);
		columns.unshift("");
		columns.unshift("");
		
		table.append("col").attr("width", "20")
		
		// append the header row
		thead.append("tr")
			.selectAll("th")
			.data(columns)
			.enter()
			.append("th")
			.attr("style", "font: 12px sans-serif")
			.text(function(column) { return column;});

		tbody.selectAll("tr")
					.data(rows)
					.enter()
					.append("tr")
					.append("th")
					.attr("style", "font: 12px sans-serif")
					//.attr("style", "font-weight: bold")
					.text(function(row){return row;});
		
		tbody.select("tr")
		 .insert("th",":first-child")
		 .attr("rowspan", row_size+1)
		 .attr("scope", "rowgroup")
		 .attr("style","padding: 0px; margin: 0px; white-space: nowrap; font: 10px sans-serif; ")
		 .append("p")
		 .attr("style","padding:0px; margin: 0px; writing-mode: tb-rl; transform: rotate(180deg);")
		 .text(title);

		tbody.selectAll("tr").selectAll("td")
		.data(function(d,i) {
				return cells.slice(i*row_size,i*row_size+row_size); // needs to return an array containing the ith row from cells
			})
		.enter()
		.append("td")
		.attr("align","center")
		.attr("style", "font: 10px sans-serif; border: 1px solid #BBBBBB;")
		.text(function(d) {return d;});
		
	}
	
	return gen;
}