class ImportFormats < ActiveRecord::Migration
  def self.up
# clear all fake formats
    FormatType.create(:name => 'JSON',  :class_name => 'com.tacitknowledge.simulator.formats.JsonAdapter')
    FormatType.create(:name => 'Properties',  :class_name => 'com.tacitknowledge.simulator.formats.PropertiesAdapter')
    FormatType.create(:name => 'YAML',  :class_name => 'com.tacitknowledge.simulator.formats.YamlAdapter')

  end

  def self.down
# we don't need to restore old fake formats.. just remove new

    FormatType.delete_all("name = 'JSON' OR name = 'Properties' OR name = 'YAML'")
  end
end
