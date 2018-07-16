(function () {

    Vue.component('list-item', {
        template: document.getElementById('list-item').innerText,
        props: ['data'],
        methods: {
            click: function() {
                this.data.active = !this.data.active;
                this.$emit('item_click', this.data);
            },
            info_click: function(e) {
                this.$emit('info_click', this.data);
                e.stopPropagation();
            }
        }
    });

    Vue.component('list-panel', {
        template: document.getElementById('list-panel').innerText,
        props: ['title', 'list', 'has_add_button', 'single_choose'],
        methods: {
            item_click: function(item) {
                if (item.active && this.single_choose) {
                    if(this.current_item && this.current_item != item) {
                        this.current_item.active = false;
                    }
                    this.current_item = item;
                }
                this.$emit('item_click', item);
            },
            info_click: function(item) {
                this.$emit('info_click', item);
            },
            add_button_click: function() {
                this.$emit('add_button_click');
            }
        }
    });

    // -- app ...

    var database = [];

    for (var i = 0; i < 10; i++) {
        database.push({
            name: i,
            active: false,
            has_info: true,
        });
    }

    var vue = new Vue({
        el: "#container",
        data: {
            connections: database,
            databases: database,
            tables: database,
            templates: database
        },
        methods: {
            conn_item_click: function(item) {
                console.log('conn item click', item);
            },
            conn_info_click: function(item) {
                console.log('conn info click', item);
            },
            conn_add_button_click: function() {
                console.log('conn add click');
            }
        }
    });
})();