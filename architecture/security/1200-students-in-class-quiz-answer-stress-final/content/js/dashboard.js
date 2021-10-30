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
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.9542447861807457, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [1.0, 500, 1500, "Get list of questions"], "isController": false}, {"data": [0.5, 500, 1500, "Write quiz answers"], "isController": false}, {"data": [0.0, 500, 1500, "Populate quiz"], "isController": false}, {"data": [1.0, 500, 1500, "get next quiz question"], "isController": false}, {"data": [1.0, 500, 1500, "Submit answer"], "isController": false}, {"data": [0.09708333333333333, 500, 1500, "Login as student"], "isController": false}, {"data": [1.0, 500, 1500, "Conclude quiz"], "isController": false}, {"data": [0.5, 500, 1500, "Login as teacher"], "isController": false}, {"data": [1.0, 500, 1500, "Start quiz by qrcode"], "isController": false}, {"data": [1.0, 500, 1500, "Create question"], "isController": false}, {"data": [1.0, 500, 1500, "Generate number of times a student answers to a question"], "isController": false}, {"data": [1.0, 500, 1500, "Create quiz"], "isController": false}, {"data": [1.0, 500, 1500, "Calculate Port Number"], "isController": false}]}, function(index, item){
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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 23735, 0, 0.0, 802.5606066989685, 0, 54863, 20.0, 24.0, 49.0, 258.5765488991296, 119.37779975365233, 154.32579174428867], "isController": false}, "titles": ["Label", "#Samples", "KO", "Error %", "Average", "Min", "Max", "90th pct", "95th pct", "99th pct", "Transactions\/s", "Received", "Sent"], "items": [{"data": ["Get list of questions", 1, 0, 0.0, 31.0, 31, 31, 31.0, 31.0, 31.0, 32.25806451612903, 204.5110887096774, 24.382560483870968], "isController": false}, {"data": ["Write quiz answers", 1, 0, 0.0, 1375.0, 1375, 1375, 1375.0, 1375.0, 1375.0, 0.7272727272727272, 0.2784090909090909, 0.5617897727272727], "isController": false}, {"data": ["Populate quiz", 1, 0, 0.0, 3894.0, 3894, 3894, 3894.0, 3894.0, 3894.0, 0.2568053415511043, 0.2076511941448382, 0.1991244542886492], "isController": false}, {"data": ["get next quiz question", 6000, 0, 0.0, 1.7365000000000008, 0, 87, 3.0, 4.0, 10.0, 349.87462825820745, 202.6129439034346, 268.8977855851653], "isController": false}, {"data": ["Submit answer", 6924, 0, 0.0, 20.462305025996642, 8, 298, 24.0, 35.75, 121.5, 403.23801758779337, 82.6952965756217, 392.6057651709277], "isController": false}, {"data": ["Login as student", 1200, 0, 0.0, 15707.124166666677, 247, 54863, 40783.000000000015, 46231.35, 50465.89, 17.690243830527464, 26.786164686035026, 2.5913443111124215], "isController": false}, {"data": ["Conclude quiz", 1200, 0, 0.0, 29.176666666666684, 15, 291, 40.0, 50.950000000000045, 87.96000000000004, 70.90522335145354, 30.81330506972347, 101.58004165681872], "isController": false}, {"data": ["Login as teacher", 2, 0, 0.0, 549.5, 542, 557, 557.0, 557.0, 557.0, 0.08858965272856131, 0.12557410247608078, 0.011679299920269313], "isController": false}, {"data": ["Start quiz by qrcode", 1200, 0, 0.0, 2.407500000000001, 0, 94, 4.0, 6.0, 20.99000000000001, 71.23775601068566, 219.97439893143368, 53.91529385574355], "isController": false}, {"data": ["Create question", 5, 0, 0.0, 42.0, 17, 105, 105.0, 105.0, 105.0, 23.809523809523807, 24.483816964285715, 29.366629464285715], "isController": false}, {"data": ["Generate number of times a student answers to a question", 6000, 0, 0.0, 0.47899999999999693, 0, 35, 1.0, 1.0, 3.0, 349.95625546806644, 0.0, 0.0], "isController": false}, {"data": ["Create quiz", 1, 0, 0.0, 44.0, 44, 44, 44.0, 44.0, 44.0, 22.727272727272727, 85.53799715909092, 24.946732954545457], "isController": false}, {"data": ["Calculate Port Number", 1200, 0, 0.0, 0.5825000000000008, 0, 35, 1.0, 1.0, 6.0, 71.22929898498249, 0.0, 0.0], "isController": false}]}, function(index, item){
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
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 23735, 0, null, null, null, null, null, null, null, null, null, null], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
