class AddHttpTransport < ActiveRecord::Migration
  def self.up
    TransportType.create(:name => 'HTTP',  :class_name => 'com.tacitknowledge.simulator.transports.HttpTransport')
  end


  def self.down
    TransportType.delete_all("name = 'HTTP'")
  end
end
