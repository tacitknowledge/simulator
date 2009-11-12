TK.ScriptLanguageStore = new Ext.data.ArrayStore({
    fields: ['id', 'value'],
    data : [
        ['JavaScript','Java Script'],
        ['somethingelse',"something else"]
    ]
});

TK.SystemForm = Ext.extend(Ext.FormPanel, {
    /**
     * current system id.
     * null means new system
     */
    systemId:'',

    initComponent: function() {

        TK.SystemForm.superclass.initComponent.apply(this, arguments);
        //if system_id is set then load the data into the form. otherwise we are trying to create a new form
        if (this.systemId != '' && this.systemId != undefined) {
            this.getForm().load({
                url: 'load',
                params: {
                    system_id:this.systemId
                },
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
            url:'save',
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
                    name: 'system_id',
                    xtype: 'hidden'
                },
                {
                    fieldLabel: 'Project Name',
                    name : 'project_name',
                    allowBlank : false,
                    xtype : 'textfield'
                }
                ,
                {
                    fieldLabel: 'Description',
                    name : 'project_description',
                    xtype : 'textarea'
                }
                ,
                {
                    fieldLabel: 'Script language',
                    name : 'project_script_language',
                    xtype : 'combo',
                    store : TK.ScriptLanguageStore,
                    displayField : 'value',
                    mode : 'local'
                }
            ],
            buttons: [
                {
                    text: 'Update',
                    handler: function() {
                        if (this.getForm().isValid()) {
                            this.getForm().submit({
                                url: 'system/save',
                                waitMsg: 'Saving....',
                                method: 'PUT',

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
            ]
        }, config));
    }
})


