class AddTransportTable < ActiveRecord::Migration
  def self.up
    create_table :transport do |t|
      t.string :type, :null => false
    end
    create_table :transport_type do |t|
      t.string :type, :null => false
      t.string :class_name, :null => false
    end

    add_column :systems, :inboundTransport, :string
    add_column :systems, :outboundTransport, :string
  end

  def self.down
    remove_column :systems, :inboundTransport
    remove_column :systems, :outboundTransport
    drop_table :transport_type
    drop_table :transport
  end
end
