TK.ConversationForm = Ext.extend(Ext.FormPanel, {
    /**
     * current conversation id.
     * null means new conversation
     */
    conversationId:'',




    onSaveHandler:function(id) {
        var doRedirect = false;
        //todo remove duplicates from here and the system form
        if (this.conversationId != '' && this.conversationId != undefined) {
            url = '../' + this.conversationId
            submitMethod = 'PUT'
        } else {
            doRedirect = true
            url = '../../conversations'
            submitMethod = 'POST'
        }
        if (Ext.getCmp('conversation-form').getForm().isValid()) {
            Ext.getCmp('conversation-form').getForm().submit({
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
        TK.ConversationForm.superclass.initComponent.apply(this, arguments);

        //if system_id is set then load the data into the form. otherwise we are trying to create a new form
        if (this.conversationId != '' && this.conversationId != undefined) {
            this.getForm().load({
                url: '../' + this.conversationId + '.json',
                method: 'GET',
                failure: function(form, action) {
                    Ext.Msg.alert("Load failed", action.result.errorMessage);
                }
            });
            var scenariosGrid = Ext.getCmp('scenarios_grid');
            scenariosGrid.show();
            scenariosGrid.getStore().load();
        }
    },
    constructor:function(config) {
        var scenariosStore = new Ext.data.JsonStore({
            root:'data',
            storeId:'scenarios_store',
            idProperty:'id',
            fields: ['enabled','id', 'name', 'criteria_script', 'execution_script'],
            url : 'scenarios.json'
        });
        var formatsStore = new Ext.data.JsonStore({
            root: 'data',
            idProperty: 'id',
            url: '../../../../conversations/format_types',
            fields: ['id', 'name']
        });

        formatsStore.load()

        var transportsStore = new Ext.data.JsonStore({
            root: 'data',
            idProperty: 'id',
            url: '../../../../conversations/transport_types',
            fields: ['id', 'name']
        });
        transportsStore.load()
        var enabledColumn = new Ext.grid.CheckColumn({
                            header: 'Enabled?',
                            dataIndex: 'enabled',
                            sortable: true,
                            width: 55

                        })

        enabledColumn.onChange= function(record) {
            TK.enableEntity('scenarios',record.data.id)
        }

        var initialConfig = {

            id:'conversation-form',
            labelWidth: 120, // label settings here cascade unless overridden
            //            frame: true,
            title: 'Conversation',
            bodyStyle: 'padding:5px 5px 0;',
            defaults: {
                width: "98%"
            },
            defaultType: 'textfield',

            items: [
                {
                    id:'name',
                    fieldLabel: 'Name',
                    name: 'name',
                    allowBlank:false,
                    xtype: 'textfield'
                },
                {
                    id:'description',
                    fieldLabel: 'Description',
                    name: 'description',
                    xtype: 'textarea'
                },
                {
                    xtype: 'fieldset',
                    title: 'Inbound',
                    collapsible: false,
                    autoHeight: true,

                    items :[

                        new Ext.form.ComboBox({
                            id:'inbound_transport',
                            fieldLabel: 'Transport',
                            store: transportsStore,
                            autoDestroy: true,
                            hiddenName: 'inbound_transport_type_id',
                            valueField: 'id',
                            displayField:'name',
                            typeAhead: true,
                            triggerAction: 'all',
                            emptyText:'Select an inbound transport...',
                            selectOnFocus: true,
                            editable: false
                        }),
                        new Ext.form.ComboBox({
                            id:'inbound_format',
                            fieldLabel: 'Format',
                            hiddenName: 'inbound_format_type_id',
                            store: formatsStore,
                            valueField: 'id',
                            displayField:'name',
                            typeAhead: true,
                            triggerAction: 'all',
                            emptyText: 'Select an inbound format...',
                            selectOnFocus: true,
                            width: 190,
                            editable: false
                        })
                    ]
                },
                {
                    xtype: 'fieldset',
                    title: 'Outbound',
                    collapsible: false,
                    autoHeight: true,

                    items :[
                        new Ext.form.ComboBox({
                            id:'outbound_transport',
                            fieldLabel: 'Transport',
                            hiddenName: 'outbound_transport_type_id',
                            store: transportsStore,
                            valueField: 'id',
                            displayField:'name',
                            typeAhead: true,
                            triggerAction: 'all',
                            emptyText:'Select an outbound transport...',
                            selectOnFocus: true,
                            width:190
                        }),
                        new Ext.form.ComboBox({
                            id:'outbound_format',
                            fieldLabel: 'Format',
                            hiddenName: 'outbound_format_type_id',
                            store:formatsStore,
                            valueField : 'id',
                            displayField : 'name',
                            typeAhead : true,
                            triggerAction : 'all',
                            emptyText : 'Select an outbound format...',
                            selectOnFocus : true,
                            width : 190
                        }),
                        new Ext.form.Hidden({
                            hiddenName: 'system_id',
                            name: 'system_id',
                            value: this.systemId
                        })
                    ]
                },
                {
                    store: scenariosStore,
                    id: 'scenarios_grid',
                    xtype:'editorgrid',
                    selModel: new Ext.grid.RowSelectionModel(),
                    columns: [
                        enabledColumn
                        ,
                        {
                            header: 'Title',
                            width: 150,
                            sortable: true,
                            dataIndex: 'name'
                        },
                        {
                            header: 'Criteria Script',
                            width: 150,
                            sortable: true,
                            dataIndex : 'criteria_script'
                        },
                        {
                            header: 'Execution Script',
                            width: 150,
                            sortable: true,
                            dataIndex : 'execution_script'
                        }
                    ],
                    stripeRows: true,
                    height: 250,
                    title: 'Scenarios',
                    // config options for stateful behavior
                    plugins: [enabledColumn],
                    stateful: true,
                    stateId: 'grid',
                    hidden:true,

                    buttons:[
                        {
                            text:'Add',
                            id: 'scenario_add',
                            handler:function() {
                                window.location = 'scenarios/new/'
                            }
                        },
                        {
                            text:'Edit',
                            id: 'scenarion_edit',
                            handler:function() {
                                TK.editEntity('scenarios')
                            }
                        },
                        {
                            text:'Delete',
                            id: 'scenario_delete',
                            handler:function() {
                                TK.deleteEntity('scenarios')
                            }
                        }

                    ]
                }
            ],
            buttons: [
                {
                    id:'conversation_save',
                    scope:this,
                    text: 'Save',
                    handler: this.onSaveHandler
                }
            ]

        };
        TK.ConversationForm.superclass.constructor.call(this, Ext.apply(initialConfig, config));
    }
}
        );

