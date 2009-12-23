TK.AdminForm = Ext.extend(Ext.FormPanel, {
    /**
     * current conversation id.
     * null means new conversation
     */
    conversationId:'',

    initComponent: function()
    {
        TK.AdminForm.superclass.initComponent.apply(this, arguments);
    },

    constructor:function(config)
    {
        var initialConfig = {
            id:'admin-form',
            //            labelWidth: 120, // label settings here cascade unless overridden
            title: 'Admin',
            method:"POST",
            fileUpload: true,

            //            bodyStyle: 'padding:5px 5px 0;',
            //            defaults: {
            //                width: "98%"
            //            },
            //            frame: true,
            //            defaultType: 'textfield',
            items: [
                {
                    xtype: 'fileuploadfield',
                    id: 'form-file',
                    emptyText: 'Select a file',
                    fieldLabel: 'File',
                    name: 'filefield'
                },
                {
                    id:"import_button",
                    name:"import_submit",
                    xtype:"button",
                    handler: function()
                    {
                        var form = Ext.getCmp("admin-form").getForm();
                        if (form.isValid())
                        {
                            form.submit({
                                url: "configuration/import",
                                waitMsg: 'Uploading',
                                success: function(fp, o)
                                {
                                    alert('success');
                                },
                                failure: function(fp, o)
                                {
                                    alert('failure');
                                }
                            });
                        }
                    }
                },
                {
                    id: 'export_button',
                    text: 'Export',
                    name: 'export_button',
                    xtype: 'button',
                    handler:function()
                    {
                        TK.export();
                    }
                }
            ]
        };
        TK.AdminForm.superclass.constructor.call(this, Ext.apply(initialConfig, config));
    }
});
