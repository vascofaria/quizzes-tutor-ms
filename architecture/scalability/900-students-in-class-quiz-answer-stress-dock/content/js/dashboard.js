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
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.9277939042089985, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [1.0, 500, 1500, "Get list of questions"], "isController": false}, {"data": [0.0, 500, 1500, "Write quiz answers"], "isController": false}, {"data": [1.0, 500, 1500, "Login as teacher"], "isController": false}, {"data": [0.9972222222222222, 500, 1500, "Start quiz by qrcode"], "isController": false}, {"data": [1.0, 500, 1500, "Create question"], "isController": false}, {"data": [0.0, 500, 1500, "Populate quiz"], "isController": false}, {"data": [1.0, 500, 1500, "Generate number of times a student answers to a question"], "isController": false}, {"data": [1.0, 500, 1500, "Create quiz"], "isController": false}, {"data": [0.9996147177807744, 500, 1500, "Submit answer"], "isController": false}, {"data": [0.012777777777777779, 500, 1500, "Login as student"], "isController": false}, {"data": [0.9994444444444445, 500, 1500, "Conclude quiz"], "isController": false}]}, function(index, item){
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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 12402, 0, 0.0, 563.3626028060048, 0, 17866, 225.0, 3435.8499999999985, 13616.799999999974, 208.055830495395, 107.15093620510325, 119.93404414601821], "isController": false}, "titles": ["Label", "#Samples", "KO", "Error %", "Average", "Min", "Max", "90th pct", "95th pct", "99th pct", "Transactions\/s", "Received", "Sent"], "items": [{"data": ["Get list of questions", 1, 0, 0.0, 47.0, 47, 47, 47.0, 47.0, 47.0, 21.27659574468085, 134.80718085106383, 16.14444813829787], "isController": false}, {"data": ["Write quiz answers", 1, 0, 0.0, 8447.0, 8447, 8447, 8447.0, 8447.0, 8447.0, 0.11838522552385462, 0.0453193441458506, 0.09167918343790696], "isController": false}, {"data": ["Login as teacher", 2, 0, 0.0, 271.5, 254, 289, 289.0, 289.0, 289.0, 0.08678295582747549, 0.12352163878764211, 0.01144111234053632], "isController": false}, {"data": ["Start quiz by qrcode", 900, 0, 0.0, 105.24555555555548, 26, 579, 214.79999999999995, 276.0, 428.97, 44.86987735566856, 130.97271817977864, 34.04677217319772], "isController": false}, {"data": ["Create question", 5, 0, 0.0, 57.4, 24, 151, 151.0, 151.0, 151.0, 17.36111111111111, 17.835828993055557, 21.464029947916668], "isController": false}, {"data": ["Populate quiz", 1, 0, 0.0, 1768.0, 1768, 1768, 1768.0, 1768.0, 1768.0, 0.5656108597285069, 0.44574996464932126, 0.4396740667420814], "isController": false}, {"data": ["Generate number of times a student answers to a question", 4500, 0, 0.0, 0.7008888888888899, 0, 75, 1.0, 1.0, 5.0, 223.69140527911716, 0.0, 0.0], "isController": false}, {"data": ["Create quiz", 1, 0, 0.0, 63.0, 63, 63, 63.0, 63.0, 63.0, 15.873015873015872, 58.74875992063492, 17.314608134920636], "isController": false}, {"data": ["Submit answer", 5191, 0, 0.0, 72.59776536312873, 13, 640, 145.0, 204.0, 324.15999999999985, 257.7457795431976, 98.66830623138034, 250.44633852097814], "isController": false}, {"data": ["Login as student", 900, 0, 0.0, 7134.973333333323, 247, 17866, 14649.599999999999, 16496.449999999993, 17454.88, 32.017075773745994, 48.474742084667376, 4.690001334044824], "isController": false}, {"data": ["Conclude quiz", 900, 0, 0.0, 88.29111111111108, 21, 696, 180.79999999999995, 243.94999999999993, 396.8700000000001, 45.65051990869895, 19.83836070251078, 64.95391358102967], "isController": false}]}, function(index, item){
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
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 12402, 0, null, null, null, null, null, null, null, null, null, null], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
