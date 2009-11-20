class ImportFormats < ActiveRecord::Migration
  def self.up
# clear all fake formats
    FormatType.delete_all
    format = FormatType.new(:name => 'CSV',  :class_name => 'com.tacitknowledge.simulator.formats.CsvAdapter')
    format.save
    format = FormatType.new(:name => 'JSON',  :class_name => 'com.tacitknowledge.simulator.formats.JsonAdapter')
    format.save
    format = FormatType.new(:name => 'Properties',  :class_name => 'com.tacitknowledge.simulator.formats.PropertiesAdapter')
    format.save
    format = FormatType.new(:name => 'XML',  :class_name => 'com.tacitknowledge.simulator.formats.XmlAdapter')
    format.save
    format = FormatType.new(:name => 'YAML',  :class_name => 'com.tacitknowledge.simulator.formats.YamlAdapter')
    format.save
    
  end

  def self.down
# we don't need to restore old fake formats.. just remove new

    FormatType.delete_all
  end
end
