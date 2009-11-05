class AddFormatTable < ActiveRecord::Migration
  def self.up
    create_table :formats do |t|
      t.string :type, :null => false
    end

    create_table :format_types do |t|
      t.string :type, :null => false
      t.string :class_name, :null => false
    end
    add_column :conversations, :inboundFormat, :integer
    add_column :conversations, :outboundFormat, :integer
  end

  def self.down
    remove_column :conversations, :inboundFormat
    remove_column :conversations, :outboundFormat
    drop_table :format_types
    drop_table :formats
  end
end
