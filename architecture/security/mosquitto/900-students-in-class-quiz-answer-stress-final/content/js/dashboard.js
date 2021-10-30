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
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.5973047181441219, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [1.0, 500, 1500, "Get list of questions"], "isController": false}, {"data": [0.5, 500, 1500, "Login as teacher"], "isController": false}, {"data": [0.9144444444444444, 500, 1500, "Start quiz by qrcode"], "isController": false}, {"data": [1.0, 500, 1500, "Create question"], "isController": false}, {"data": [0.0, 500, 1500, "Populate quiz"], "isController": false}, {"data": [1.0, 500, 1500, "Generate number of times a student answers to a question"], "isController": false}, {"data": [0.7501111111111111, 500, 1500, "get next quiz question"], "isController": false}, {"data": [1.0, 500, 1500, "Create quiz"], "isController": false}, {"data": [0.031888485774298266, 500, 1500, "Submit answer"], "isController": false}, {"data": [0.0, 500, 1500, "Login as student"], "isController": false}, {"data": [1.0, 500, 1500, "Calculate Port Number"], "isController": false}, {"data": [0.985, 500, 1500, "Conclude quiz"], "isController": false}]}, function(index, item){
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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 17846, 0, 0.0, 3271.3489297321535, 0, 79815, 7111.0, 8705.149999999976, 57411.86999999991, 121.55185331498862, 55.7953235468403, 72.81353710461251], "isController": false}, "titles": ["Label", "#Samples", "KO", "Error %", "Average", "Min", "Max", "90th pct", "95th pct", "99th pct", "Transactions\/s", "Received", "Sent"], "items": [{"data": ["Get list of questions", 1, 0, 0.0, 30.0, 30, 30, 30.0, 30.0, 30.0, 33.333333333333336, 313.5416666666667, 25.1953125], "isController": false}, {"data": ["Login as teacher", 1, 0, 0.0, 757.0, 757, 757, 757.0, 757.0, 757.0, 1.321003963011889, 1.8731423381770145, 0.17415579590488772], "isController": false}, {"data": ["Start quiz by qrcode", 900, 0, 0.0, 213.98666666666676, 1, 1656, 1051.8999999999996, 1320.6999999999996, 1551.97, 69.08197727970524, 208.6626520762972, 52.35118590727663], "isController": false}, {"data": ["Create question", 5, 0, 0.0, 34.2, 16, 91, 91.0, 91.0, 91.0, 28.735632183908045, 29.66168283045977, 35.4424838362069], "isController": false}, {"data": ["Populate quiz", 1, 0, 0.0, 4606.0, 4606, 4606, 4606.0, 4606.0, 4606.0, 0.21710811984368217, 0.17194793475901, 0.16855562038645247], "isController": false}, {"data": ["Generate number of times a student answers to a question", 4500, 0, 0.0, 0.7920000000000007, 0, 153, 1.0, 1.0, 10.0, 94.3613831281847, 0.0, 0.0], "isController": false}, {"data": ["get next quiz question", 4500, 0, 0.0, 517.9857777777779, 0, 2993, 1401.8000000000002, 1975.749999999999, 2692.0, 94.34753438443475, 55.005349636237845, 72.60337606927206], "isController": false}, {"data": ["Create quiz", 1, 0, 0.0, 22.0, 22, 22, 22.0, 22.0, 22.0, 45.45454545454545, 171.2091619318182, 49.893465909090914], "isController": false}, {"data": ["Submit answer", 5237, 0, 0.0, 4925.867290433458, 389, 11217, 7623.4, 8314.699999999997, 9429.359999999997, 108.93848938073347, 22.34090114253323, 106.27885829233665], "isController": false}, {"data": ["Login as student", 900, 0, 0.0, 33181.59333333331, 1675, 79815, 68309.09999999999, 74302.54999999997, 78311.64, 9.676066786363197, 14.66584518091557, 1.4173925956586713], "isController": false}, {"data": ["Calculate Port Number", 900, 0, 0.0, 0.7411111111111106, 0, 61, 1.0, 1.0, 5.0, 74.77567298105683, 0.0, 0.0], "isController": false}, {"data": ["Conclude quiz", 900, 0, 0.0, 207.72333333333307, 15, 1102, 350.9, 456.94999999999993, 633.0, 21.30026270324001, 9.256461819279105, 30.660729711499776], "isController": false}]}, function(index, item){
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
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 17846, 0, null, null, null, null, null, null, null, null, null, null], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
