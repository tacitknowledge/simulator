TK.deleteEntity = function(entityName, relativeUrl) {
    relativeUrl = relativeUrl == undefined ? entityName : relativeUrl
    var grid = Ext.getCmp(entityName + '_grid');

    var rec = grid.getSelectionModel().getSelected();
    if (rec != undefined) {
        Ext.Ajax.request({
            url: relativeUrl + '/' + rec.data.id ,
            method: 'DELETE',
            success: function(response) {
                grid.store.reload()

                // --- Show the flash message
                var obj = Ext.decode(response.responseText);
                TK.showFlashMessage(obj.message);
            },
            failure: function(response) {
                var jsonResponse = Ext.util.JSON.decode(response.responseText)
                Ext.MessageBox.alert('Error',jsonResponse.message)
            }
        })
    }
}
TK.editEntity = function(entityName, relativeUrl) {
    relativeUrl = relativeUrl == undefined ? entityName : relativeUrl
    var rec = Ext.getCmp(entityName + '_grid').getSelectionModel().getSelected();
    if (rec != undefined) {
        window.location = relativeUrl + '/' + rec.data.id + '/'
    }
}
/**
 *
 * toggles enabled/disabled stat
 * @param entity  entity name.. like scenarios or conversations
 * @param entityId entityId
 *
 */
TK.enableEntity = function(entity, record, dataIndex) {

    Ext.Ajax.request({
        record : record,
        dataIndex : dataIndex,
        url: entity + '/' + record.data.id + '/enable'  ,
        method: 'GET',
        success: function(fp, o) {
            record.set(this.dataIndex, !record.data.enabled)
        },
        failure: function(response) {
                var jsonResponse = Ext.util.JSON.decode(response.responseText)
                Ext.MessageBox.alert('Error',jsonResponse.message)
                o.record.set(o.dataIndex, o.record.modified.enabled)
        }
    })

}

/**
 *
 * toggles activated/diactivated status
 * @param entity  entity name.. like scenarios or conversations
 * @param entityId entityId
 *
 */
TK.activateEntity = function(entity, record, dataIndex) {
    Ext.Ajax.request({
        record : record,
        dataIndex : dataIndex,
        url: entity + '/' + record.data.id + '/activate'  ,
        method: 'GET',
        success: function(fp, o) {
            //do nothing
        },
        failure: function(response, o) {
                var jsonResponse = Ext.util.JSON.decode(response.responseText)
                Ext.MessageBox.alert('Error',jsonResponse.message)
                o.record.set(o.dataIndex, o.record.modified.active)
        }
    })

}

TK.cloneScenario = function (scenarioId) {
    Ext.Ajax.request({
        url: 'scenarios/' + scenarioId + '/clone'  ,
        method: 'GET',
        success: function(result, request) {
            var jsonResponse = Ext.util.JSON.decode(result.responseText)
            window.location = "scenarios/" + jsonResponse.data.id + "/"
        },
        failure: function(response) {
                var jsonResponse = Ext.util.JSON.decode(response.responseText)
                Ext.MessageBox.alert('Error',jsonResponse.message)
        }
    })
},

TK.export = function () {
    window.location = 'configuration/export.xml'
};
