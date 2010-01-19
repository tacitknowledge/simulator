class AddSoapFormat < ActiveRecord::Migration
    def self.up
      FormatType.create(:name => 'SOAP',  :class_name => 'com.tacitknowledge.simulator.formats.SoapAdapter')
    end

    def self.down
      FormatType.delete_all("name = 'SOAP'")
    end
end
