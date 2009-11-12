class AddFormatTable < ActiveRecord::Migration
  def self.up
    create_table :format_types do |t|
      t.string :name, :null => false
      t.string :class_name, :null => false

      t.timestamps
    end

    create_table :formats do |t|
      t.integer :format_type_id, :null => false

      t.timestamps
    end
  end

  def self.down
    drop_table :formats
    drop_table :format_types
  end
end
