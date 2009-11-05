class AddConfigurationTable < ActiveRecord::Migration
  def self.up
    create_table :configurations do |t|
      t.string :type, :null => false
      t.string :atribute_name, :null => false
      t.string :atribute_value, :null => false, :limit => 10000
    end
  end

  def self.down
    drop_table :configurations
  end
end
