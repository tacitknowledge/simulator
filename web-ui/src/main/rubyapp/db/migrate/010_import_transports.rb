class ImportTransports < ActiveRecord::Migration
  def self.up
    file_transport_type = TransportType.find_by_name 'File'
    file_transport_type.delete
    jms_transport_type = TransportType.find_by_name 'JMS'
    jms_transport_type.delete

    jms_transport_type = TransportType.new(:name => 'JMS',  :class_name => 'com.tacitknowledge.simulator.transports.JmsTransport')
    jms_transport_type.save
    file_transport_type = TransportType.new(:name => 'File',  :class_name => 'com.tacitknowledge.simulator.transports.FileTransport')
    file_transport_type.save
    ftp_transport_type = TransportType.new(:name => 'FTP',  :class_name => 'com.tacitknowledge.simulator.transports.FtpTransport')
    ftp_transport_type.save

  end


  def self.down

    file_transport_type = TransportType.find_by_name 'File'
    file_transport_type.delete
    jms_transport_type = TransportType.find_by_name 'JMS'
    jms_transport_type.delete
    ftp_transport_type = TransportType.find_by_name 'FTP'
    ftp_transport_type.delete

    jms_transport_type = TransportType.new(:name => 'JMS',  :class_name => 'JmsTransport')
    jms_transport_type.save
    file_transport_type = TransportType.new(:name => 'File',  :class_name => 'FileTransport')
    file_transport_type.save
  end
end
