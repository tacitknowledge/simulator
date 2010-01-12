class AddRestFormatType < ActiveRecord::Migration
  def self.up
    FormatType.create(:name => 'REST',  :class_name => 'com.tacitknowledge.simulator.formats.RestAdapter')
  end

  def self.down
    FormatType.delete_all("name = 'REST'")
  end
end
