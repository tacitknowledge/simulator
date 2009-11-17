TK.ConversationForm = Ext.extend(Ext.FormPanel, {
    /**
     * current conversation id.
     * null means new conversation
     */
    conversationId:'',



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
        }
    },
    constructor:function(config) {
        TK.ConversationForm.superclass.constructor.call(this, Ext.apply({

            id:'conversation-form',
            labelWidth: 120, // label settings here cascade unless overridden
            url:'conversations',
            //            frame: true,
            title: 'Conversation',
            bodyStyle: 'padding:5px 5px 0;',
            width: "100%",

            defaults: {
                width: "98%"
            },

            defaultType: 'textfield',

            items: [
            {
                fieldLabel: 'Name',
                name: 'name',
                id: 'name',
                allowBlank:false,
                xtype: 'textfield'
            },
            {
                fieldLabel: 'Description',
                name: 'description',
                id: 'description',
                xtype: 'textarea'
            },
            {
                xtype: 'fieldset',
                title: 'Inbound',
                collapsible: false,
                autoHeight: true,
                width: "750",

                items :[
                new Ext.form.ComboBox({
                    fieldLabel: 'Transport',
                    id: "inbound_transport",
                    hiddenName: 'inbound_transport_type',
                    store: new Ext.data.ArrayStore({
                        fields: ['inbound_transport_type', 'name'],
                        data : [
                        ['JmsTransport', 'JMS'],
                        ['FileTransport', 'File']
                        ]
                    }),
                    valueField: 'inbound_transport_type',
                    displayField:'name',
                    typeAhead: true,
                    mode: 'local',
                    triggerAction: 'all',
                    emptyText:'Select a transport...',
                    selectOnFocus: true,
                    width:190,
                    editable: false
                }),
                new Ext.form.ComboBox({
                    fieldLabel: 'Format',
                    id: "inbound_format",
                    hiddenName: 'inbound_format_type',
                    store: new Ext.data.ArrayStore({
                        fields: ['inbound_format_type', 'name'],
                        data : [
                        ['XmlAdapter', 'XML'],
                        ['CsvAdapter', 'CSV']
                        ]
                    }),
                    valueField: 'inbound_format_type',
                    displayField:'name',
                    typeAhead: true,
                    mode: 'local',
                    triggerAction: 'all',
                    emptyText: 'Select a format...',
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
                width: "750",

                items :[
                new Ext.form.ComboBox({
                    fieldLabel: 'Transport',
                    id: "outbound_transport",
                    hiddenName: 'outbound_transport_type',
                    store: new Ext.data.ArrayStore({
                        fields: ['outbound_transport_type', 'name'],
                        data : [
                        ['JmsTransport', 'JMS'],
                        ['FileTransport', 'File']
                        ]
                    }),
                    valueField: 'outbound_transport_type',
                    displayField:'name',
                    typeAhead: true,
                    mode: 'local',
                    triggerAction: 'all',
                    emptyText:'Select a transport...',
                    selectOnFocus: true,
                    width:190
                }),
                new Ext.form.ComboBox({
                    fieldLabel: 'Format',
                    id: "outbound_format",
                    hiddenName: 'outbound_format_type',
                    store: new Ext.data.ArrayStore({
                        fields: ['outbound_format_type', 'name'],
                        data : [
                        ['XmlAdapter', 'XML'],
                        ['CsvAdapter', 'CSV']
                        ]
                    }),
                    valueField: 'outbound_format_type',
                    displayField:'name',
                    typeAhead: true,
                    mode: 'local',
                    triggerAction: 'all',
                    emptyText:'Select a format...',
                    selectOnFocus: true,
                    width:190
                }),
                new Ext.form.Hidden({
                    hiddenName: 'system_id',
                    name: 'system_id',
                    value:"1"
                })
                ]
            }
            ],
            buttons: [
            {
                text: 'Save',
                id: 'conversation_save',
                handler:
                function(id) {
                    var doRedirect = false;
                    //todo remove duplicates from here and the system form
                    if (this.conversationId != '' && this.conversationId != undefined) {
                        url = this.conversationId
                        submitMethod = 'PUT'
                    } else {
                        doRedirect = true
                        url = '../conversations'
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
                                    window.location = '../conversations/' + o.result.data.id + '/'
                                }
                            },
                            failure: function(fp, o) {
                                Ext.MessageBox.alert('Error', o.result.message)
                            }
                        });
                    }
                }


            }
            ]

        },
        config
        ))
    }
}
)
;

