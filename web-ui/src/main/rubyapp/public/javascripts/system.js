TK.ScriptLanguageStore = new Ext.data.ArrayStore({
    fields: ['id', 'value'],
    data : [
        ['JavaScript','Java Script'],
        ['somethingelse',"something else"]
    ]
});

TK.SystemForm = Ext.extend(Ext.FormPanel, {

    /**
     * current systems id.
     * null means new systems
     */
    systemId:'',

    onSaveHandler:function(id) {
        var form = Ext.getCmp('system-form').getForm();
        if (form.isValid()) {
            var doRedirect = false;
            if (this.systemId != '' && this.systemId != undefined) {
                //update existing form
                url = '../' + this.systemId
                submitMethod = 'PUT'
            } else {
                doRedirect = true
                url = '../'
                submitMethod = 'POST'
            }
            form.submit({
                url: url,
                waitMsg: 'Saving....',
                method: submitMethod,
                success: function(fp, o) {
                    Ext.MessageBox.alert('Success', o.result.message)
                    if (doRedirect) {
                        window.location = '../' + o.result.data.id + '/'
                    }
                },
                failure: function(fp, o) {
                    Ext.MessageBox.alert('Error', o.result.message)
                }
            });

        }
    },
    initComponent: function() {

        TK.SystemForm.superclass.initComponent.apply(this, arguments);

        //if system_id is set then load the data into the form. otherwise we are trying to create a new form
        if (this.systemId != '' && this.systemId != undefined) {
            this.getForm().load({
                url: '../' + this.systemId + ".json",
                method: 'GET',
                failure: function(form, action) {
                    Ext.Msg.alert("Load failed", action.result.errorMessage);
                }
            });
            var grid = Ext.getCmp('conversations_grid');
            grid.show();
        }
    },

    constructor:function(config) {
        var store = new Ext.data.JsonStore({
            autoDestroy: true,
            url: 'conversations.json',
            root:'data',
            storeId:'conversationStore',
            idProperty:'id',
            fields: ['enabled', 'id', 'name', 'description']
        });
        store.load();
        var checkColumn = new Ext.grid.CheckColumn({
            header: 'Enabled?',
            dataIndex: 'enabled',
            sortable: true,
            width: 55

        })
        checkColumn.onChange = function(record) {
            TK.enableEntity('conversations', record.data.id)
        }

        TK.SystemForm.superclass.constructor.call(this, Ext.apply({
            labelWidth: 120,
            // label settings here cascade unless overridden
            url: 'systems/' + this.systemId,
            frame:true,
            title: 'System',
            bodyStyle:'padding:5px 5px 0',
            width: "100%",
            defaults: {
                width: "99%"
            },
            id:'system-form',
            defaultType: 'textfield',
            items: [
                {
                    fieldLabel: 'Name',
                    name : 'name',
                    allowBlank : false,
                    xtype : 'textfield',
                    id: 'name'
                }
                ,
                {
                    fieldLabel: 'Description',
                    name : 'description',
                    xtype : 'textarea',
                    id: 'description'
                }
                ,
                {
                    fieldLabel: 'Script language',
                    name : 'script_language',
                    xtype : 'combo',
                    store : TK.ScriptLanguageStore,
                    displayField : 'value',
                    mode : 'local',
                    id: 'script_language'
                },
                {
                    xtype:'button',
                    text: 'Save',
                    id: 'system_save',
                    scope: this,
                    width: '10%',
                    handler :this.onSaveHandler
                },
                {
                    id:'conversations_grid',
                    hidden:true,
                    xtype: 'editorgrid',
                    selModel: new Ext.grid.RowSelectionModel(),
                    height: 300,
                    width: '100%',
                    store: store,
                    columns: [
                        checkColumn,
                        {
                            id:'name',
                            header: 'Name',
                            width: 160,
                            sortable: true,
                            dataIndex: 'name'
                        },
                        {
                            header: 'Description',
                            width: 75,
                            dataIndex: 'description'
                        }
                    ],
                    title: 'Conversations',
                    stateful: true,
                    stateId: 'grid',
                    plugins: [checkColumn],
                    buttons:[
                        {
                            text:'Add',
                            id: 'conversation_add',
                            handler:function() {
                                window.location = 'conversations/new/'
                            }
                        },
                        {
                            text:'Edit',
                            id: 'conversation_edit',
                            handler:function() {
                                TK.editEntity('conversations')
                            }
                        },
                        {
                            text:'Delete',
                            id: 'conversation_delete',
                            handler:function() {
                                TK.deleteEntity('conversations');
                            }
                        }

                    ]
                }
            ]
        }, config));
    }
})


