class AddRestAndSoapTransports < ActiveRecord::Migration
  def self.up
    TransportType.delete_all("name = 'HTTP'")
    TransportType.create(:name => 'REST',  :class_name => 'com.tacitknowledge.simulator.transports.RestTransport')
    TransportType.create(:name => 'SOAP',  :class_name => 'com.tacitknowledge.simulator.transports.SoapTransport')
  end


  def self.down
    TransportType.create(:name => 'HTTP',  :class_name => 'com.tacitknowledge.simulator.transports.HttpTransport')
    TransportType.delete_all("name = 'REST'")
    TransportType.delete_all("name = 'SOAP'")
  end
end
