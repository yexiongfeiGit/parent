(function () {
    var database = [];

    for (var i = 0; i < 10; i++) {
        database.push({
            name: i
        });
    }
    console.log(database);
    var vue = new Vue({
        el: "#container",
        data: {
            connections: database,
            databases: database,
            tables: database,
            templates: database,
            name: "123"
        }
    });
})();