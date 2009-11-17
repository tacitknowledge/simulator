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
        if (Ext.getCmp('system-form').getForm().isValid()) {
            var doRedirect = false;
            if (this.systemId != '' && this.systemId != undefined) {
                //update existing form
                url = this.systemId
                submitMethod = 'PUT'
            } else {
                doRedirect = true
                url = '../systems'
                submitMethod = 'POST'
            }
            Ext.getCmp('system-form').getForm().submit({
                url: url,
                waitMsg: 'Saving....',
                method: submitMethod,
                success: function(fp, o) {
                    Ext.MessageBox.alert('Success', o.result.message)
                    if (doRedirect) {
                        window.location = '../systems/' + o.result.data.id + '/'
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
            var grid = Ext.getCmp('conversations-grid');
            grid.hidden = false;
        }
    },

    constructor:function(config) {
        var store = new Ext.data.JsonStore({
            autoDestroy: true,
            url: 'conversations.json',
            root:'data',
            storeId:'conversationStore',
            idProperty:'name',
            fields: ['id', 'name', 'description']
        });
        store.load();
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
                    id:'conversations-grid',
                    hidden:true,
                    xtype: 'grid',
                    height: 300,
                    width: '100%',
                    store: store,
                    columns: [
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
                                var rec = Ext.getCmp('conversations-grid').getSelectionModel().getSelected();
                                if (rec != undefined) {
                                    window.location = 'conversations/' + rec.data.id + '/'
                                }
                            }
                        },
                        {
                            text:'Delete',
                            id: 'conversation_delete',
                            handler:function() {
//                                window.open('conversations/new/', 'GET')
                            }
                        }

                    ]
                }
            ]
        }, config));
    }
})


