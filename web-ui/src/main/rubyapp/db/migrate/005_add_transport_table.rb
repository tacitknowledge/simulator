class AddTransportTable < ActiveRecord::Migration
  def self.up
    create_table :transport_types do |t|
      t.string :name, :null => false
      t.string :class_name, :null => false

      t.timestamps
    end

    create_table :transports do |t|
      t.string :transport_type_id, :null => false

      t.timestamps
    end
  end

  def self.down
    drop_table :transports
    drop_table :transport_types
  end
end
