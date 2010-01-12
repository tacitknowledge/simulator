class AddXmlAndCsvFormatTypes < ActiveRecord::Migration
  def self.up
    FormatType.create(:name => 'XML',  :class_name => 'XmlAdapter')
    FormatType.create(:name => 'CSV',  :class_name => 'CsvAdapter')
  end

  def self.down
    FormatType.delete_all("name = 'XML' OR name = 'CSV'")
  end
end
