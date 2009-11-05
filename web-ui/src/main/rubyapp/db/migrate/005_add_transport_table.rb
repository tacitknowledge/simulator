class AddTransportTable < ActiveRecord::Migration
  def self.up
    create_table :transports do |t|
      t.string :type, :null => false
    end
    create_table :transport_types do |t|
      t.string :type, :null => false
      t.string :class_name, :null => false
    end

    add_column :systems, :inbound_transport, :string
    add_column :systems, :outbound_transport, :string
  end

  def self.down
    remove_column :systems, :inbound_transport
    remove_column :systems, :outbound_transport
    drop_table :transport_types
    drop_table :transports
  end
end
