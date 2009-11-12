class AddJMSAndFileTransportTypes < ActiveRecord::Migration
  def self.up
    jms_transport_type = TransportType.new(:name => 'JMS',  :class_name => 'JmsTransport')
    jms_transport_type.save
    file_transport_type = TransportType.new(:name => 'File',  :class_name => 'FileTransport')
    file_transport_type.save
  end

  def self.down
    file_transport_type = TransportType.find_by_name 'File'
    file_transport_type.delete
    jms_transport_type = TransportType.find_by_name 'JMS'
    jms_transport_type.delete
  end
end
