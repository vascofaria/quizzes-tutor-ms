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
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.9530148125653743, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [1.0, 500, 1500, "Get list of questions"], "isController": false}, {"data": [0.5, 500, 1500, "Write quiz answers"], "isController": false}, {"data": [0.0, 500, 1500, "Populate quiz"], "isController": false}, {"data": [1.0, 500, 1500, "get next quiz question"], "isController": false}, {"data": [1.0, 500, 1500, "Submit answer"], "isController": false}, {"data": [0.07333333333333333, 500, 1500, "Login as student"], "isController": false}, {"data": [1.0, 500, 1500, "Conclude quiz"], "isController": false}, {"data": [0.5, 500, 1500, "Login as teacher"], "isController": false}, {"data": [1.0, 500, 1500, "Start quiz by qrcode"], "isController": false}, {"data": [1.0, 500, 1500, "Create question"], "isController": false}, {"data": [1.0, 500, 1500, "Generate number of times a student answers to a question"], "isController": false}, {"data": [1.0, 500, 1500, "Create quiz"], "isController": false}, {"data": [1.0, 500, 1500, "Calculate Port Number"], "isController": false}]}, function(index, item){
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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 29637, 0, 0.0, 1234.9248237000975, 0, 83069, 18.0, 24.0, 48.0, 222.80945757997216, 102.89639267657785, 132.89068960737512], "isController": false}, "titles": ["Label", "#Samples", "KO", "Error %", "Average", "Min", "Max", "90th pct", "95th pct", "99th pct", "Transactions\/s", "Received", "Sent"], "items": [{"data": ["Get list of questions", 1, 0, 0.0, 33.0, 33, 33, 33.0, 33.0, 33.0, 30.303030303030305, 192.11647727272725, 22.904829545454543], "isController": false}, {"data": ["Write quiz answers", 1, 0, 0.0, 1287.0, 1287, 1287, 1287.0, 1287.0, 1287.0, 0.777000777000777, 0.29744560994561, 0.6002027486402487], "isController": false}, {"data": ["Populate quiz", 1, 0, 0.0, 4792.0, 4792, 4792, 4792.0, 4792.0, 4792.0, 0.20868113522537562, 0.1687382616861436, 0.16180939586811352], "isController": false}, {"data": ["get next quiz question", 7500, 0, 0.0, 1.6228000000000002, 0, 40, 3.0, 4.0, 9.0, 373.02297821545807, 216.0181895329752, 286.68855845270065], "isController": false}, {"data": ["Submit answer", 8626, 0, 0.0, 18.262578251796878, 6, 266, 22.0, 29.649999999999636, 60.0, 429.04750062173594, 87.98825696344193, 417.7347247264362], "isController": false}, {"data": ["Login as student", 1500, 0, 0.0, 24246.97600000002, 594, 83069, 65291.60000000002, 74469.35, 82326.44, 14.285850341431823, 21.63387419165897, 2.0926538586081773], "isController": false}, {"data": ["Conclude quiz", 1500, 0, 0.0, 29.03333333333334, 15, 263, 36.0, 56.0, 101.97000000000003, 75.4565118969767, 32.79115995522913, 108.10029585240706], "isController": false}, {"data": ["Login as teacher", 2, 0, 0.0, 677.0, 654, 700, 700.0, 700.0, 700.0, 0.07484189649365715, 0.10608692652396812, 0.009866851588519254], "isController": false}, {"data": ["Start quiz by qrcode", 1500, 0, 0.0, 2.0653333333333324, 0, 52, 3.0, 5.0, 14.0, 74.63800567248843, 230.47399798477383, 56.4887249962681], "isController": false}, {"data": ["Create question", 5, 0, 0.0, 52.4, 16, 161, 161.0, 161.0, 161.0, 18.796992481203006, 19.329329182330827, 23.184181156015036], "isController": false}, {"data": ["Generate number of times a student answers to a question", 7500, 0, 0.0, 0.5377333333333324, 0, 35, 1.0, 1.0, 2.0, 373.0972042582828, 0.0, 0.0], "isController": false}, {"data": ["Create quiz", 1, 0, 0.0, 40.0, 40, 40, 40.0, 40.0, 40.0, 25.0, 94.091796875, 27.44140625], "isController": false}, {"data": ["Calculate Port Number", 1500, 0, 0.0, 0.5666666666666667, 0, 18, 1.0, 1.0, 3.0, 74.62686567164178, 0.0, 0.0], "isController": false}]}, function(index, item){
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
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 29637, 0, null, null, null, null, null, null, null, null, null, null], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
