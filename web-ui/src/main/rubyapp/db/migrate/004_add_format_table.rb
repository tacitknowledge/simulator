class AddFormatTable < ActiveRecord::Migration
  def self.up
    create_table :formats do |t|
      t.string :format_type, :null => false
    end

    create_table :format_types do |t|
      t.string :format_type, :null => false
      t.string :class_name, :null => false
    end
    add_column :conversations, :inbound_format, :integer
    add_column :conversations, :outbound_format, :integer
  end

  def self.down
    remove_column :conversations, :inbound_format
    remove_column :conversations, :outbound_format
    drop_table :format_types
    drop_table :formats
  end
end
