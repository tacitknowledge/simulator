class AddNewConfigurationsTables < ActiveRecord::Migration
  def self.up
    create_table :format_configurations do |t|
      t.integer :format_id
      t.string :attribute_name, :null => false
      t.string :attribute_value, :null => false, :limit => 1000

      t.timestamps
    end

    create_table :transport_configurations do |t|
      t.integer :transport_id
      t.string :attribute_name, :null => false
      t.string :attribute_value, :null => false, :limit => 1000

      t.timestamps
    end
  end

  def self.down
    drop_table :format_configurations
    drop_table :transport_configurations
  end
end
