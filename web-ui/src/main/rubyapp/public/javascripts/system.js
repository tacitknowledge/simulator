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

    initComponent: function() {

        TK.SystemForm.superclass.initComponent.apply(this, arguments);

        //if system_id is set then load the data into the form. otherwise we are trying to create a new form
        if (this.systemId != '' && this.systemId != undefined) {
            this.getForm().load({
                url: this.systemId + ".json",
                method: 'GET',
                failure: function(form, action) {
                    Ext.Msg.alert("Load failed", action.result.errorMessage);
                }
            })
        }

    },

    constructor:function(config) {
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
            defaultType: 'textfield',
            items: [
                {
                    fieldLabel: 'Name',
                    name : 'name',
                    allowBlank : false,
                    xtype : 'textfield'
                }
                ,
                {
                    fieldLabel: 'Description',
                    name : 'description',
                    xtype : 'textarea'
                }
                ,
                {
                    fieldLabel: 'Script language',
                    name : 'script_language',
                    xtype : 'combo',
                    store : TK.ScriptLanguageStore,
                    displayField : 'value',
                    mode : 'local'
                },
                {
                    xtype:'button',
                    text: 'Update',
                    scope: this,
                    width: '10%',
                    listeners: {
                        click : function(id) {
                            if (this.getForm().isValid()) {
                                this.getForm().submit({
                                    url: 'systems/' + this.systemId,
                                    waitMsg: 'Saving....',
                                    method: 'POST',

                                    success: function(fp, o) {
                                        Ext.MessageBox.alert('Success', o.result.msg)
                                    },
                                    failure: function(fp, o) {
                                        Ext.MessageBox.alert('Error', o.result.msg)
                                    }
                                });
                            }

                        }
                    }

                },
                {
                    xtype: 'grid',
                    height: 300,
                    width: '100%',
                    store: {
                        url: 'conversation/list?system_id=' + this.systemId,
                        root:'conversations',
                        storeId:'conversationStore',
                        idProperty:'name',
                        fields: ['name', 'description']
                    },
                    columns: [
                        {
                            id:'name',
                            header: 'Name',
                            width: 160,
                            sortable: true,
                            dataIndex: 'company'
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
                            handler:function() {
                                window.open('../conversation/', 'GET')
                            }
                        }
                    ]
                }


            ],
            buttons: [

            ]
        }, config));
    }
})

