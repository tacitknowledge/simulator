class ImportTransports < ActiveRecord::Migration
  def self.up
  
    TransportType.create(:name => 'FTP',  :class_name => 'com.tacitknowledge.simulator.transports.FtpTransport')

  end


  def self.down
    TransportType.delete_all("name = 'File' OR name = 'JMS' OR name = 'FTP'")
  end
end
