(async function () {

    Vue.component('list-item', {
        template: document.getElementById('list-item').innerText,
        props: ['data'],
        methods: {
            click: function () {
                this.data.active = !this.data.active;
                this.$emit('item_click', this.data);
            },
            info_click: function (e) {
                this.$emit('info_click', this.data);
                e.stopPropagation();
            }
        }
    });

    Vue.component('list-panel', {
        template: document.getElementById('list-panel').innerText,
        props: ['title', 'list', 'has_add_button', 'single_choose'],
        methods: {
            item_click: function (item) {
                if (item.active && this.single_choose) {
                    if (this.current_item && this.current_item != item) {
                        this.current_item.active = false;
                    }
                    this.current_item = item;
                }
                this.$emit('item_click', item);
            },
            info_click: function (item) {
                this.$emit('info_click', item);
            },
            add_button_click: function () {
                this.$emit('add_button_click');
            }
        }
    });

    Vue.component('modal', {
        template: document.getElementById('dialog').innerText,
        props: ['title', 'commit_title', 'close_title'],
        methods: {
            commit: function () {
                console.log('commit...');
                this.$emit('commit');
            }
        }
    });

    // -- app ...

    var connections = JSON.parse(window.localStorage.getItem("connections") || "[]");

    var app = new Vue({
        el: "#container",
        data: {
            connections: connections,
            databases: [],
            tables: [],
            templates: [
                {title: "vo", active: false},
                {title: "repository", active: false},
            ],
            connectionData: {
                title: "",
                host: "",
                port: 3306,
                userName: "",
                password: ""
            },
            packageName: window.localStorage.getItem("packageName") || "",
            current: {
                connection: {},
                database: {},
                table: {},
            }
        },
        methods: {
            conn_item_click: async function (item) {
                console.log('conn item click', item);
                var data = await $.post("/getDatabases", item);
                console.log(data);
                if (data.code === 200) {
                    this.databases = data.data.map(d => {
                        return {
                            title: d,
                            active: false
                        }
                    });
                }
                this.current.connection = item;
            },
            conn_info_click: function (item) {
                console.log('conn info click', item);
                this.connectionData = $.extend({}, item);
                delete this.connectionData["active"];
                $('#add_connection').modal();
            },
            conn_add_button_click: function () {
                console.log('conn add click');
                $('#add_connection').modal();
            },
            save_connection: async function () {
                var data = await $.post("/testJDBC", this.connectionData);
                if (data.code === 200) {
                    connections.push($.extend({active: false}, this.connectionData));
                    window.localStorage.setItem("connections", JSON.stringify(connections));
                    $('#add_connection').modal('hide');
                } else {
                    console.error("error: ", data);
                }
            },

            database_item_click: async function (item) {
                this.current.database = item;
                var conn = this.current.connection;
                var postData = {
                    host: conn.host,
                    port: conn.port,
                    userName: conn.userName,
                    password: conn.password,
                    database: item.title,

                };
                let data = await $.post("/getTables", postData);

                this.tables = data.data.map(d => {
                    return {
                        title: d,
                        active: false
                    }
                });
            },
            generator: async function () {
                var connection = this.current.connection;
                var database = this.current.database;
                var tables = this.tables.filter(t => t.active).map(t => t.title);
                var templates = this.templates.filter(t => t.active).map(t => t.title);
                var postData = {
                    host: connection.host,
                    port: connection.port,
                    userName: connection.userName,
                    password: connection.password,
                    database: database.title,
                    packageName: this.packageName,
                    tables: tables,
                    templates: templates
                };
                let data = await $.ajax({
                    url: 'generator',
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify(postData)
                });

                console.log(data.file);

                // var aLink = document.createElement('a');
                // // var blob = new Blob([data.file]);
                // var evt = document.createEvent("HTMLEvents");
                // evt.initEvent("click", false, false);//initEvent 不加后两个参数在FF下会报错, 感谢 Barret Lee 的反馈
                // aLink.download = "code.zip";
                // aLink.href = data.file;//URL.createObjectURL(blob);
                //
                // aLink.dispatchEvent(evt);

                // let a = document.createElement("a");
                // a.href = "download/" + data.file;
                // a.download = "code.zip";
                // document.body.appendChild(a);
                // $(a).click();


                // 此方法chrome失效了
                // var aLink = document.createElement('a');
                // // var blob = new Blob([data.file]);
                // var evt = document.createEvent("HTMLEvents");
                // evt.initEvent("click", false, false);//initEvent 不加后两个参数在FF下会报错, 感谢 Barret Lee 的反馈
                // aLink.download = "code.zip";
                // aLink.href = "download/"+data.file;//URL.createObjectURL(blob);
                //
                // aLink.dispatchEvent(evt);

                // window.open("download/"+data.file);


                console.log(data);
            },
            config_package_name: function () {
                $('#config_package_name').modal();
            },
            save_package_name: function () {
                var packageName = this.packageName || "";
                window.localStorage.setItem("packageName", packageName);
                $('#config_package_name').modal('hide');
            }
        }
    });
})();