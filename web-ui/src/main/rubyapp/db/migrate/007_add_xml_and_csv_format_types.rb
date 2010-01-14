class AddXmlAndCsvFormatTypes < ActiveRecord::Migration
  def self.up
    FormatType.create(:name => 'XML',  :class_name => 'com.tacitknowledge.simulator.formats.XmlAdapter')
    FormatType.create(:name => 'CSV',  :class_name => 'com.tacitknowledge.simulator.formats.CsvAdapter')
  end

  def self.down
    FormatType.delete_all("name = 'XML' OR name = 'CSV'")
  end
end
