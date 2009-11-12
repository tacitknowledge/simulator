/*
* Ext JS Library 3.0 RC2
* Copyright(c) 2006-2009, Ext JS, LLC.
* licensing@extjs.com
*
* http://extjs.com/license
*/

// Application instance for showing user-feedback messages.
var App = new Ext.App({});

// Create a standard HttpProxy instance.
var proxy = new Ext.data.HttpProxy({
    url: 'systems'
});

// Typical JsonReader. Notice additional meta-data params for defining the core attributes of your json-response
var reader = new Ext.data.JsonReader({
    totalProperty: 'total',
    successProperty: 'success',
    idProperty: 'id',
    root: 'data'
}, [
    {name: 'id'},
    {name: 'name', allowBlank: false},
    {name: 'description', allowBlank: true},
    {name: 'script_language', allowBlank: false}
]);

// The new DataWriter component.
var writer = new Ext.data.JsonWriter();

// Typical Store collecting the Proxy, Reader and Writer together.
var store = new Ext.data.Store({
    id: 'system',
    restful: true, // <-- This Store is RESTful
    proxy: proxy,
    reader: reader,
    writer: writer, // <-- plug a DataWriter into the store just as you would a Reader
    listeners: {
        write : function(store, action, result, response, rs) {
            App.setAlert(response.success, response.message);
        }
    }
});

// Let's pretend we rendered our grid-columns with meta-data from our ORM framework.
var userColumns = [
    {header: "ID", width: 40, sortable: true, dataIndex: 'id'},
    {header: "Name", width: 100, sortable: true, dataIndex: 'name', editor: new Ext.form.TextField({})},
    {header: "Description", width: 50, sortable: true, dataIndex: 'description', editor: new Ext.form.TextField({})},
    {header: "Script language", width: 50, sortable: true, dataIndex: 'script_language', editor: new Ext.form.TextField({})}
];

// load the store immeditately
store.load();

Ext.onReady(function() {
    Ext.QuickTips.init();

    // We'll use the new RowEditor for this example.
    //var editor = new Ext.ux.grid.RowEditor({
     //   saveText: 'Update'
    //});

    // Create a typical GridPanel with RowEditor plugin
    var systemGrid = new Ext.grid.GridPanel({
        renderTo: 'system-grid',
        iconCls: 'icon-grid',
        frame: true,
        title: 'Systems',
        autoScroll: true,
        height: 300,
        store: store,
     //   plugins: [editor],
        columns : userColumns,
      //  tbar: [{
       //     text: 'Add',
       //     iconCls: 'silk-add',
       //     handler: onAdd
       // }, '-', {
       //     text: 'Delete',
       //     iconCls: 'silk-delete',
        //    handler: onDelete
       // }, '-'],
        viewConfig: {
            forceFit: true
        },
        listeners: {
            click : function(id) {
                var rec = systemGrid.getSelectionModel().getSelected();
                this.hide();
                var win = new TK.SystemForm({
                    systemId: rec.id
                });

                win.render(document.body)

            }
        }
    });

    /**
* onAdd
*/
    function onAdd(btn, ev) {
     //   var u = new systemGrid.store.recordType({
     //       name : '',
     //       description: '',
     //       script_language : ''
     //   });
       // editor.stopEditing();
      //  systemGrid.store.insert(0, u);
      //  editor.startEditing(0);
    }
    /**
* onDelete
*/
    function onDelete() {
        var rec = systemGrid.getSelectionModel().getSelected();
        if (!rec) {
            return false;
        }
        systemGrid.store.remove(rec);
    }

});


