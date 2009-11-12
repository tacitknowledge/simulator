class AddXMLAndCSVFormatTypes < ActiveRecord::Migration
  def self.up
    xml_format_type = FormatType.new(:name => 'XML',  :class_name => 'XmlAdapter')
    xml_format_type.save
    csv_format_type = FormatType.new(:name => 'CSV',  :class_name => 'CsvAdapter')
    csv_format_type.save
  end

  def self.down
    xml_format_type = FormatType.find_by_name 'XML'
    xml_format_type.delete
    csv_format_type = FormatType.find_by_name 'CSV'
    csv_format_type.delete
  end
end
