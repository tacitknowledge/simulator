class AddConversationsTable < ActiveRecord::Migration
  def self.up
    create_table :conversations do |t|
      t.integer :systemId, :null => false
      t.string :name, :null => false
      t.text :description, :null => false
      t.boolean :isActive, :null => false
    end
  end

  def self.down
  end
end
