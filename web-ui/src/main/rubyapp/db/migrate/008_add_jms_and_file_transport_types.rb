class AddJmsAndFileTransportTypes < ActiveRecord::Migration
  def self.up
    TransportType.create(:name => 'JMS',  :class_name => 'com.tacitknowledge.simulator.transports.JmsTransport')
    TransportType.create(:name => 'File',  :class_name => 'com.tacitknowledge.simulator.transports.FileTransport')
  end

  def self.down
    TransportType.delete_all("name = 'JMS' OR name = 'File'")
  end
end
