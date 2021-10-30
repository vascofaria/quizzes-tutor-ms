/*
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
var showControllersOnly = false;
var seriesFilter = "";
var filtersOnlySampleSeries = true;

/*
 * Add header in statistics table to group metrics by category
 * format
 *
 */
function summaryTableHeader(header) {
    var newRow = header.insertRow(-1);
    newRow.className = "tablesorter-no-sort";
    var cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Requests";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 3;
    cell.innerHTML = "Executions";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 6;
    cell.innerHTML = "Response Times (ms)";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Throughput";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 2;
    cell.innerHTML = "Network (KB/sec)";
    newRow.appendChild(cell);
}

/*
 * Populates the table identified by id parameter with the specified data and
 * format
 *
 */
function createTable(table, info, formatter, defaultSorts, seriesIndex, headerCreator) {
    var tableRef = table[0];

    // Create header and populate it with data.titles array
    var header = tableRef.createTHead();

    // Call callback is available
    if(headerCreator) {
        headerCreator(header);
    }

    var newRow = header.insertRow(-1);
    for (var index = 0; index < info.titles.length; index++) {
        var cell = document.createElement('th');
        cell.innerHTML = info.titles[index];
        newRow.appendChild(cell);
    }

    var tBody;

    // Create overall body if defined
    if(info.overall){
        tBody = document.createElement('tbody');
        tBody.className = "tablesorter-no-sort";
        tableRef.appendChild(tBody);
        var newRow = tBody.insertRow(-1);
        var data = info.overall.data;
        for(var index=0;index < data.length; index++){
            var cell = newRow.insertCell(-1);
            cell.innerHTML = formatter ? formatter(index, data[index]): data[index];
        }
    }

    // Create regular body
    tBody = document.createElement('tbody');
    tableRef.appendChild(tBody);

    var regexp;
    if(seriesFilter) {
        regexp = new RegExp(seriesFilter, 'i');
    }
    // Populate body with data.items array
    for(var index=0; index < info.items.length; index++){
        var item = info.items[index];
        if((!regexp || filtersOnlySampleSeries && !info.supportsControllersDiscrimination || regexp.test(item.data[seriesIndex]))
                &&
                (!showControllersOnly || !info.supportsControllersDiscrimination || item.isController)){
            if(item.data.length > 0) {
                var newRow = tBody.insertRow(-1);
                for(var col=0; col < item.data.length; col++){
                    var cell = newRow.insertCell(-1);
                    cell.innerHTML = formatter ? formatter(col, item.data[col]) : item.data[col];
                }
            }
        }
    }

    // Add support of columns sort
    table.tablesorter({sortList : defaultSorts});
}

$(document).ready(function() {

    // Customize table sorter default options
    $.extend( $.tablesorter.defaults, {
        theme: 'blue',
        cssInfoBlock: "tablesorter-no-sort",
        widthFixed: true,
        widgets: ['zebra']
    });

    var data = {"OkPercent": 100.0, "KoPercent": 0.0};
    var dataset = [
        {
            "label" : "KO",
            "data" : data.KoPercent,
            "color" : "#FF6347"
        },
        {
            "label" : "OK",
            "data" : data.OkPercent,
            "color" : "#9ACD32"
        }];
    $.plot($("#flot-requests-summary"), dataset, {
        series : {
            pie : {
                show : true,
                radius : 1,
                label : {
                    show : true,
                    radius : 3 / 4,
                    formatter : function(label, series) {
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'
                            + label
                            + '<br/>'
                            + Math.round10(series.percent, -2)
                            + '%</div>';
                    },
                    background : {
                        opacity : 0.5,
                        color : '#000'
                    }
                }
            }
        },
        legend : {
            show : true
        }
    });

    // Creates APDEX table
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.9610619469026549, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [1.0, 500, 1500, "Get list of questions"], "isController": false}, {"data": [0.5, 500, 1500, "Write quiz answers"], "isController": false}, {"data": [0.0, 500, 1500, "Populate quiz"], "isController": false}, {"data": [1.0, 500, 1500, "get next quiz question"], "isController": false}, {"data": [1.0, 500, 1500, "Submit answer"], "isController": false}, {"data": [0.2325, 500, 1500, "Login as student"], "isController": false}, {"data": [1.0, 500, 1500, "Conclude quiz"], "isController": false}, {"data": [1.0, 500, 1500, "Login as teacher"], "isController": false}, {"data": [1.0, 500, 1500, "Start quiz by qrcode"], "isController": false}, {"data": [1.0, 500, 1500, "Create question"], "isController": false}, {"data": [1.0, 500, 1500, "Generate number of times a student answers to a question"], "isController": false}, {"data": [1.0, 500, 1500, "Create quiz"], "isController": false}, {"data": [1.0, 500, 1500, "Calculate Port Number"], "isController": false}]}, function(index, item){
        switch(index){
            case 0:
                item = item.toFixed(3);
                break;
            case 1:
            case 2:
                item = formatDuration(item);
                break;
        }
        return item;
    }, [[0, 0]], 3);

    // Create statistics table
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 11865, 0, 0.0, 136.30257058575668, 0, 7563, 31.0, 575.9999999999891, 4352.34, 373.0193661971831, 171.05787188954037, 222.26845570493273], "isController": false}, "titles": ["Label", "#Samples", "KO", "Error %", "Average", "Min", "Max", "90th pct", "95th pct", "99th pct", "Transactions\/s", "Received", "Sent"], "items": [{"data": ["Get list of questions", 1, 0, 0.0, 37.0, 37, 37, 37.0, 37.0, 37.0, 27.027027027027028, 171.3471283783784, 20.428631756756758], "isController": false}, {"data": ["Write quiz answers", 1, 0, 0.0, 1417.0, 1417, 1417, 1417.0, 1417.0, 1417.0, 0.7057163020465773, 0.27015702187720536, 0.5451382762879322], "isController": false}, {"data": ["Populate quiz", 1, 0, 0.0, 2075.0, 2075, 2075, 2075.0, 2075.0, 2075.0, 0.4819277108433735, 0.3892131024096385, 0.3736822289156626], "isController": false}, {"data": ["get next quiz question", 3000, 0, 0.0, 2.3650000000000024, 0, 51, 4.0, 6.0, 15.0, 276.8549280177187, 158.21826550387595, 212.77815268549278], "isController": false}, {"data": ["Submit answer", 3454, 0, 0.0, 21.937174290677483, 8, 283, 29.0, 40.0, 121.44999999999982, 318.54652771373236, 65.32692462879277, 309.6517302349442], "isController": false}, {"data": ["Login as student", 600, 0, 0.0, 2507.755000000002, 272, 7563, 5546.8, 6366.999999999998, 6702.84, 36.094567767550984, 54.645142930728504, 5.2872902003248505], "isController": false}, {"data": ["Conclude quiz", 600, 0, 0.0, 35.49333333333335, 15, 274, 54.0, 75.94999999999993, 150.87000000000012, 56.53443889569396, 24.568188777913882, 80.55053354376709], "isController": false}, {"data": ["Login as teacher", 2, 0, 0.0, 217.5, 207, 228, 228.0, 228.0, 228.0, 0.14534883720930233, 0.2058871956758721, 0.019162200218023256], "isController": false}, {"data": ["Start quiz by qrcode", 600, 0, 0.0, 3.3916666666666684, 1, 42, 6.0, 11.0, 26.99000000000001, 55.57613931085587, 169.49637018340127, 42.06201949796221], "isController": false}, {"data": ["Create question", 5, 0, 0.0, 39.0, 16, 91, 91.0, 91.0, 91.0, 25.510204081632654, 26.232661033163264, 31.464245854591834], "isController": false}, {"data": ["Generate number of times a student answers to a question", 3000, 0, 0.0, 0.5943333333333336, 0, 19, 1.0, 1.0, 4.0, 276.9827347428677, 0.0, 0.0], "isController": false}, {"data": ["Create quiz", 1, 0, 0.0, 40.0, 40, 40, 40.0, 40.0, 40.0, 25.0, 92.9931640625, 27.197265625], "isController": false}, {"data": ["Calculate Port Number", 600, 0, 0.0, 0.663333333333333, 0, 9, 1.0, 1.0, 5.0, 55.56584552694944, 0.0, 0.0], "isController": false}]}, function(index, item){
        switch(index){
            // Errors pct
            case 3:
                item = item.toFixed(2) + '%';
                break;
            // Mean
            case 4:
            // Mean
            case 7:
            // Percentile 1
            case 8:
            // Percentile 2
            case 9:
            // Percentile 3
            case 10:
            // Throughput
            case 11:
            // Kbytes/s
            case 12:
            // Sent Kbytes/s
                item = item.toFixed(2);
                break;
        }
        return item;
    }, [[0, 0]], 0, summaryTableHeader);

    // Create error table
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": []}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 11865, 0, null, null, null, null, null, null, null, null, null, null], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
