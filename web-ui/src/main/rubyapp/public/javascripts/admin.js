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
            title: 'Admin',
            method:"POST",
            frame: true,
            fileUpload: true,
            autoHeight: true,
            items: [
                {
                    xtype: 'fileuploadfield',
                    id: 'form-file',
                    emptyText: 'Select a file',
                    fieldLabel: 'File',
                    name: 'file'
                },
                {
                    id:"import_button",
                    name:"import_submit",
                    text:"Import",
                    xtype:"button",
                    handler: function()
                    {
                        var form = Ext.getCmp("admin-form").getForm();
                        if (form.isValid())
                        {
                            form.submit({
                                url: "configuration/import",
                                waitMsg: 'Uploading',
                                success: function(fp, o){
                                    alert('Successfully imported');
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
