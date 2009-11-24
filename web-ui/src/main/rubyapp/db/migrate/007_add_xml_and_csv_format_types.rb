class AddXmlAndCsvFormatTypes < ActiveRecord::Migration
  def self.up
    xml_format_type = FormatType.create(:name => 'XML',  :class_name => 'XmlAdapter')
    csv_format_type = FormatType.create(:name => 'CSV',  :class_name => 'CsvAdapter')
  end

  def self.down
    xml_format_type = FormatType.find_by_name 'XML'
    xml_format_type.delete
    csv_format_type = FormatType.find_by_name 'CSV'
    csv_format_type.delete
  end
end
