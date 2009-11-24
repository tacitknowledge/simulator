class AddJmsAndFileTransportTypes < ActiveRecord::Migration
  def self.up
    jms_transport_type = TransportType.create(:name => 'JMS',  :class_name => 'JmsTransport')
    file_transport_type = TransportType.create(:name => 'File',  :class_name => 'FileTransport')
  end

  def self.down
    file_transport_type = TransportType.find_by_name 'File'
    file_transport_type.delete
    jms_transport_type = TransportType.find_by_name 'JMS'
    jms_transport_type.delete
  end
end
