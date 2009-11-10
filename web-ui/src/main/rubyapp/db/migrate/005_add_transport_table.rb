class AddTransportTable < ActiveRecord::Migration
  def self.up
    create_table :transports do |t|
      t.string :transport_type_id, :null => false
    end
    create_table :transport_types do |t|
      t.string :name, :null => false
      t.string :class_name, :null => false
    end

    add_column :conversations, :inbound_transport_id, :integer
    add_column :conversations, :outbound_transport_id, :integer
  end

  def self.down
    remove_column :conversations, :inbound_transport_id
    remove_column :conversations, :outbound_transport_id
    drop_table :transport_types
    drop_table :transports
  end
end
