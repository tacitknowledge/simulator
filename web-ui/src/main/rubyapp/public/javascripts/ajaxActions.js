TK.deleteEntity = function(entityName, relativeUrl) {
    relativeUrl=relativeUrl==undefined?entityName:relativeUrl
    var grid = Ext.getCmp(entityName + '_grid');

    var rec = grid.getSelectionModel().getSelected();
    if (rec != undefined) {
        Ext.Ajax.request({
            url: relativeUrl+'/'+rec.data.id ,
            method: 'DELETE',
            success: function() {
                grid.store.reload()
            },
            failure: function() {
            }
        })
    }
}
TK.editEntity = function(entityName, relativeUrl) {
    relativeUrl=relativeUrl==undefined?entityName:relativeUrl
    var rec = Ext.getCmp(entityName + '_grid').getSelectionModel().getSelected();
    if (rec != undefined) {
        window.location = relativeUrl+'/'+rec.data.id + '/'
    }
}