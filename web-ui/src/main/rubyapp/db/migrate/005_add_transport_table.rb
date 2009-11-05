class AddTransportTable < ActiveRecord::Migration
  def self.up
    create_table :transports do |t|
      t.string :type, :null => false
    end
    create_table :transport_types do |t|
      t.string :type, :null => false
      t.string :class_name, :null => false
    end

    add_column :systems, :inboundTransport, :string
    add_column :systems, :outboundTransport, :string
  end

  def self.down
    remove_column :systems, :inboundTransport
    remove_column :systems, :outboundTransport
    drop_table :transport_types
    drop_table :transports
  end
end
