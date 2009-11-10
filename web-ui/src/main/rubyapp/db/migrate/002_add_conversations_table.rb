class AddConversationsTable < ActiveRecord::Migration
  def self.up
    create_table :conversations do |t|
      t.integer :system_id, :null => false
      t.string :name, :null => false
      t.text :description
      t.boolean :is_active, :default => false
    end
  end

  def self.down
  end
end
