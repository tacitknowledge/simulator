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
                    url: '../'+this.conversationId + '.json',
                    method: 'GET',
                    failure: function(form, action) {
                        Ext.Msg.alert("Load failed", action.result.errorMessage);
                    }
                });
            }
        },
        constructor:function(config) {
             TK.ConversationForm.superclass.constructor.call(this, Ext.apply({
                labelWidth: 120, // label settings here cascade unless overridden
                url:'conversation',
                frame: true,
                title: 'Conversation',
                bodyStyle: 'padding:5px 5px 0;',
                width: "800",

                defaults: {
                    width: "98%"
                },

                defaultType: 'textfield',

                items: [
                    {
                        fieldLabel: 'Name',
                        name: 'name',
                        allowBlank:false,
                        xtype: 'textfield'
                    },
                    {
                        fieldLabel: 'Description',
                        name: 'description',
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
                                hiddenName: 'inbound_transport_type',
                                store: new Ext.data.ArrayStore({
                                    fields: ['inbound_transport_type', 'name'],
                                    data : [['JmsTransport', 'JMS'], ['FileTransport', 'File']]
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
                                hiddenName: 'inbound_format_type',
                                store: new Ext.data.ArrayStore({
                                    fields: ['inbound_format_type', 'name'],
                                    data : [['XmlAdapter', 'XML'], ['CsvAdapter', 'CSV']]
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
                                hiddenName: 'outbound_transport_type',
                                store: new Ext.data.ArrayStore({
                                    fields: ['outbound_transport_type', 'name'],
                                    data : [['JmsTransport', 'JMS'], ['FileTransport', 'File']]
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
                                hiddenName: 'outbound_format_type',
                                store: new Ext.data.ArrayStore({
                                    fields: ['outbound_format_type', 'name'],
                                    data : [['XmlAdapter', 'XML'], ['CsvAdapter', 'CSV']]
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
                    },
                ],
                buttons: [
                    {
                        text: 'Save',
                        handler: function() {
                            if (form.getForm().isValid()) {
                                form.getForm().submit({
                                    url: 'save',
                                    waitMsg: 'Saving....',
                                    success: function(fp, o) {
                                        Ext.MessageBox.alert('Success', o.result.msg)
                                    }
                                });
                            }
                        }
                    },
                    {
                        text: 'Cancel'
                    }
                ]

                }, config))
            }
        }
    );
