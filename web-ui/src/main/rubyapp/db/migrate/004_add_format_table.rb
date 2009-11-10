class AddFormatTable < ActiveRecord::Migration
  def self.up
    create_table :formats do |t|
      t.integer :format_type_id, :null => false
    end

    create_table :format_types do |t|
      t.string :name, :null => false
      t.string :class_name, :null => false
    end
    add_column :conversations, :inbound_format_id, :integer
    add_column :conversations, :outbound_format_id, :integer
  end

  def self.down
    remove_column :conversations, :inbound_format_id
    remove_column :conversations, :outbound_format_id
    drop_table :format_types
    drop_table :formats
  end
end
