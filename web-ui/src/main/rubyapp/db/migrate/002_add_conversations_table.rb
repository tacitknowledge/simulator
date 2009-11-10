class AddConversationsTable < ActiveRecord::Migration
  def self.up
    create_table :conversations do |t|
      t.integer :system_id, :null => false
      t.string :name, :null => false
      t.text :description
      t.boolean :is_active, :default => false

      # mapping to belonging transports and formats
      t.integer :inbound_transport
      t.integer :outound_transport
      t.integer :inbound_format
      t.integer :outbound_format

      t.timestamps
    end
  end

  def self.down
    drop_table :conversations
  end
end