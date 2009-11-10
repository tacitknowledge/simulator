class AddConfigurationTable < ActiveRecord::Migration
  def self.up
    create_table :configurations do |t|
      t.integer :transport_id
      t.integer :format_id
      t.string :attribute_name, :null => false
      t.string :attribute_value, :null => false, :limit => 1000

      t.timestamps
    end
  end

  def self.down
    drop_table :configurations
  end
end
